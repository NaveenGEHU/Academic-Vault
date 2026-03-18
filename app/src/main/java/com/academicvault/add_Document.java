package com.academicvault;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.academicvault.database.DatabaseHelper;
import com.academicvault.model.Document;
import com.academicvault.model.MetaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class add_Document extends AppCompatActivity {
    // LAUNCHER TO OPEN THE FILE EXPLORER
    Document document;
    DatabaseHelper dbHelper;

    private  ActivityResultLauncher<String> filePickerLauncher= registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if(uri!=null){
                    initialiseDocument(uri);
                    TextView docTitle =findViewById(R.id.docTitle);
                    docTitle.setText(document.getTitle());
                    Button finalUploadbtn = findViewById(R.id.finalUploadBtn);
                    finalUploadbtn.setOnClickListener(v->{
                        document.setTitle(docTitle.getText().toString());
                        document.setId(dbHelper.insertDocument(document));
                        finish();
//                       if(saveDocument(uri)){
//                           finish();
//                       }
                    });
                }
            }
    );
//  ---------------------SAVE THE DOCUMENT TO THE INTERNAL STORAGE-----------------------------------
//    private boolean saveDocument(Uri uri){
//        try {
//            File directory = new File(this.getFilesDir(),document.getSubjectId()+"");
//            File file = new File(directory,document.getDocId()+"");
//            document.setFilePath(file.getAbsolutePath());
//            if(!directory.exists()){
//                directory.mkdir();
//            }
//            InputStream inputStream = this.getContentResolver().openInputStream(uri);
//            OutputStream outputStream = new FileOutputStream(document.getFilePath());
//            if(inputStream == null)
//                return false;
//            byte[] buffer = new byte[1024];
//            int length;
//            while( (length = inputStream.read(buffer)) > 0){
//                outputStream.write(buffer,0,length);
//            }
//            outputStream.flush();
//            outputStream.close();
//            inputStream.close();
//            return true;
//        } catch (Exception e) {
//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
//            return false;
//        }
//    }

//----------------INITIALISE THE DOCUMENT META DATA------------------------
    private  void initialiseDocument(Uri uri){
        try{
            String title = MetaData.getFileName(this,uri);
            String fileSize = MetaData.getFileSize(this,uri);
            String fileType = findViewById(R.id.docTypeSpinner).toString();
            String dateAdded=MetaData.getDate();
            long subjectId=getIntent().getIntExtra("subject_id",0);
            String filePath = null;
            Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
            document = new Document(subjectId,title,filePath,fileType,fileSize,dateAdded);
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_document);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);

//--------------------------------EVENT HANDLING---------------------------------------------
        CardView uploadBtn = findViewById(R.id.uploadDocBtn);
//----------------UPLOAD Btn OPEN THE FILE EXPLORER-------------------------------------------
        try {
            uploadBtn.setOnClickListener(v -> {
            // INTIALIZING THE FILE PICKER
                filePickerLauncher.launch("*/*");
            });
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
}