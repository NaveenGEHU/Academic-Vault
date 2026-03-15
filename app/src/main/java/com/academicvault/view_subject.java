package com.academicvault;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.adapter.Document_Adapter;
import com.academicvault.adapter.Link_Adapter;
import com.academicvault.adapter.Note_Adapter;
import com.academicvault.database.DatabaseHelper;
import com.academicvault.model.Link;
import com.academicvault.model.Note;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity to display the contents of a specific subject, organized by categories.
 */
public class view_subject extends AppCompatActivity {

    private static final String TAG = "view_subject";
    private DatabaseHelper dbHelper;
    private int subjectId;
    private String subjectName;
    
    private RecyclerView notesRecycler, notesDocsRecycler, assignmentRecycler, booksRecycler, linksRecycler, linksDocsRecycler, pyqsRecycler;
    private TextView notesCount, assignmentCount, booksCount, importantCount, pyqCount;

    // Launcher for handling the result of adding a new document
    private final ActivityResultLauncher<Intent> addDocLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadData();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_subject);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        try {
            dbHelper = new DatabaseHelper(this);
            subjectId = getIntent().getIntExtra("subject_id", -1);
            subjectName = getIntent().getStringExtra("subject_name");
            
            if (subjectId == -1) {
                Toast.makeText(this, "Error: Subject ID not received", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
            
            TextView toolbarTitle = findViewById(R.id.toolbarTitle);
            if (toolbarTitle != null) toolbarTitle.setText(subjectName);

            TextView nameTv = findViewById(R.id.subjectname);
            if (nameTv != null) nameTv.setText(subjectName);

            setupUI();
            loadData();

            findViewById(R.id.uploadButton).setOnClickListener(v -> {
                Intent intent = new Intent(this, add_Document.class);
                intent.putExtra("subject_id", subjectId);
                intent.putExtra("subject_name", subjectName);
                addDocLauncher.launch(intent);
            });

            findViewById(R.id.deleteSubjectBtn).setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Subject")
                        .setMessage("Delete this subject and all its documents?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deleteSubject(subjectId);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    /**
     * Initializes UI components, including RecyclerViews and count TextViews.
     */
    private void setupUI() {
        notesRecycler = findViewById(R.id.recycleview_notes);
        notesDocsRecycler = findViewById(R.id.recycleview_notes_docs);
        assignmentRecycler = findViewById(R.id.recycleview_assignment);
        booksRecycler = findViewById(R.id.recycleview_books);
        linksRecycler = findViewById(R.id.recycleview_important);
        linksDocsRecycler = findViewById(R.id.recycleview_important_docs);
        pyqsRecycler = findViewById(R.id.recycleview_pyqs);

        notesCount = findViewById(R.id.notes_count_text);
        assignmentCount = findViewById(R.id.assignment_count_text);
        booksCount = findViewById(R.id.books_count_text);
        importantCount = findViewById(R.id.important_count_text);
        pyqCount = findViewById(R.id.pyq_count_text);

        RecyclerView[] recyclers = {notesRecycler, notesDocsRecycler, assignmentRecycler, booksRecycler, linksRecycler, linksDocsRecycler, pyqsRecycler};
        for (RecyclerView r : recyclers) {
            if (r != null) {
                r.setLayoutManager(new LinearLayoutManager(this));
                r.setNestedScrollingEnabled(false);
            }
        }
    }

    /**
     * Loads and refreshes data for all categories from the database.
     */
    public void loadData() {
        Runnable refresh = this::loadData;
        
        if (notesRecycler != null) notesRecycler.setAdapter(new Note_Adapter(this, dbHelper.getAllNotes(subjectId), refresh));
        if (notesDocsRecycler != null) notesDocsRecycler.setAdapter(new Document_Adapter(this, getFilteredCursor("Notes"), refresh));
        
        if (linksRecycler != null) linksRecycler.setAdapter(new Link_Adapter(this, dbHelper.getAllLinks(subjectId), refresh));
        if (linksDocsRecycler != null) linksDocsRecycler.setAdapter(new Document_Adapter(this, getFilteredCursor("Important"), refresh));
        
        if (assignmentRecycler != null) assignmentRecycler.setAdapter(new Document_Adapter(this, getFilteredCursor("Assignment"), refresh));
        if (booksRecycler != null) booksRecycler.setAdapter(new Document_Adapter(this, getFilteredCursor("Books"), refresh));
        if (pyqsRecycler != null) pyqsRecycler.setAdapter(new Document_Adapter(this, getFilteredCursor("PYQs"), refresh));
        
        updateAllCounts();
    }
    
    /**
     * Helper to get a cursor filtered by document type.
     */
    private Cursor getFilteredCursor(String type) {
        return dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM documents WHERE subject_id=? AND fileType=?",
                new String[]{String.valueOf(subjectId), type});
    }

    /**
     * Updates all document and item count labels in the UI.
     */
    private void updateAllCounts() {
        Cursor totalCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM documents WHERE subject_id=?", 
                new String[]{String.valueOf(subjectId)});
        if (totalCursor.moveToFirst()) {
            ((TextView) findViewById(R.id.documentcountofsubject)).setText(String.valueOf(totalCursor.getInt(0)));
        }
        totalCursor.close();

        if (notesCount != null) notesCount.setText(String.valueOf(getCount("notes") + getDocCount("Notes")));
        if (assignmentCount != null) assignmentCount.setText(String.valueOf(getDocCount("Assignment")));
        if (booksCount != null) booksCount.setText(String.valueOf(getDocCount("Books")));
        if (importantCount != null) importantCount.setText(String.valueOf(getCount("links") + getDocCount("Important")));
        if (pyqCount != null) pyqCount.setText(String.valueOf(getDocCount("PYQs")));
    }

    /**
     * Returns the count of documents of a specific type for the current subject.
     */
    private int getDocCount(String type) {
        int count = 0;
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM documents WHERE subject_id=? AND fileType=?",
                new String[]{String.valueOf(subjectId), type});
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    /**
     * Returns the total row count of a specific table for the current subject.
     */
    private int getCount(String table) {
        int count = 0;
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + table + " WHERE subject_id=?",
                new String[]{String.valueOf(subjectId)});
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    /**
     * Opens a document using an intent with appropriate MIME type.
     */
    public void openDocument(String uriString, String mimeType) {
        try {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType != null ? mimeType : "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens a URL in a web browser.
     */
    public void openLink(String url) {
        try {
            if (!url.startsWith("http")) url = "https://" + url;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a dialog to add a quick text note.
     */
    public void showAddNoteDialog(View v) {
        showDialog("Add Note", "Title", (title) -> {
            String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
            dbHelper.insertNote(new Note(0, subjectId, title, "", date));
            loadData();
        });
    }

    /**
     * Shows a dialog to add a web link.
     */
    public void showAddLinkDialog(View v) {
        showDialog("Add Link", "URL", (url) -> {
            String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
            dbHelper.insertLink(new Link(0, subjectId, "Link", url, date));
            loadData();
        });
    }

    /**
     * Base method to show a simple input dialog.
     */
    private void showDialog(String title, String hint, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_subject_dialog_box, null);
        builder.setView(view);
        
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        EditText input = view.findViewById(R.id.subject_name_input);
        input.setHint(hint);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view.findViewById(R.id.add_btn).setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                callback.onResult(text);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Interface for dialog result handling
    interface DialogCallback {
        void onResult(String text);
    }
}
