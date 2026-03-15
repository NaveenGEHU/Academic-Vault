package com.academicvault;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.academicvault.database.DatabaseHelper;
import com.academicvault.model.Document;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for selecting and uploading a document to a specific subject.
 */
public class add_Document extends AppCompatActivity {

    private static final String TAG = "add_Document";
    private DatabaseHelper dbHelper;
    private int subjectId;
    private String subjectName;
    
    private TextInputEditText docTitleInput;
    private AutoCompleteTextView docTypeSpinner;
    private TextView uploadInstruction;
    private Uri selectedFileUri;
    private String selectedFileName;
    private String selectedMimeType;
    private long selectedFileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_document);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_document_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            dbHelper = new DatabaseHelper(this);
            subjectId = getIntent().getIntExtra("subject_id", -1);
            subjectName = getIntent().getStringExtra("subject_name");

            if (subjectId == -1) {
                Toast.makeText(this, "Error: Subject not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());

            docTitleInput = findViewById(R.id.docTitle);
            docTypeSpinner = findViewById(R.id.docTypeSpinner);
            uploadInstruction = findViewById(R.id.uploadInstruction);
            TextView descriptionText = findViewById(R.id.descriptionText);
            descriptionText.setText("Adding to: " + subjectName);

            findViewById(R.id.uploadDocBtn).setOnClickListener(v -> selectFile());
            findViewById(R.id.finalUploadBtn).setOnClickListener(v -> uploadDocument());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    /**
     * Opens the system file picker to select a document.
     */
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "image/*", "text/plain", "video/*", "audio/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(intent);
    }

    // Callback for the file picker result
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    try {
                        // Request persistable permission to access the file later
                        getContentResolver().takePersistableUriPermission(selectedFileUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        
                        getFileDetails(selectedFileUri);
                        uploadInstruction.setText(selectedFileName);
                        if (docTitleInput.getText().toString().trim().isEmpty()) {
                            docTitleInput.setText(selectedFileName);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing file", e);
                        Toast.makeText(this, "Error selecting file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /**
     * Queries the content resolver for file metadata like name, size, and MIME type.
     * @param uri URI of the selected file.
     */
    private void getFileDetails(Uri uri) {
        selectedMimeType = getContentResolver().getType(uri);
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                selectedFileName = cursor.getString(nameIndex);
                selectedFileSize = cursor.getLong(sizeIndex);
                
                // Fallback for MIME type if resolver returns null
                if (selectedMimeType == null) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                    if (extension != null) {
                        selectedMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying file details", e);
        }
    }

    /**
     * Validates inputs and inserts the document metadata into the database.
     */
    private void uploadDocument() {
        try {
            String title = docTitleInput.getText().toString().trim();
            String type = docTypeSpinner.getText().toString();

            if (selectedFileUri == null) {
                Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
                return;
            }
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validation for the dropdown/spinner selection
            if (type.isEmpty() || type.equals("Category") || type.equals("Document Type*")) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
            String sizeStr = formatFileSize(selectedFileSize);

            // Create and insert document model
            Document doc = new Document(0, subjectId, title, selectedFileUri.toString(), type, sizeStr, date, selectedMimeType);
            long id = dbHelper.insertDocument(doc);

            if (id != -1) {
                Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to upload. Try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in uploadDocument", e);
            Toast.makeText(this, "Critical Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Formats raw bytes into a human-readable string (e.g., "5.6 MB").
     * @param size Size in bytes.
     * @return Formatted size string.
     */
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
