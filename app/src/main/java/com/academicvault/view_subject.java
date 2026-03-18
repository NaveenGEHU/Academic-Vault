package com.academicvault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.academicvault.model.Subject;

public class view_subject extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_subject);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//-----------------------GETTING DATA FROM MAIN ACTIVITY -------------------------------------
        Subject subject = new Subject(getIntent().getIntExtra("subject_id",0),
                getIntent().getStringExtra("subject_name"), getIntent().getIntExtra(    "doc_Count",0));
//--------------------LAYOUT COMPONENTS-----------------------------------
        TextView subjectName= findViewById(R.id.subjectname);
        subjectName.setText(subject.getName());
        TextView doucumentCount= findViewById(R.id.documentcountofsubject);
        doucumentCount.setText(String.valueOf(subject.getDocCount()));
        CardView uploadDocBtn= findViewById(R.id.uploadButton);
//---------------ON CLCIK ON UPLOAD BTN OPEN THE ACTIVITY add_document-------------------------------
        uploadDocBtn.setOnClickListener(v->{
            Intent intent=new Intent(this,add_Document.class);
            intent.putExtra("subject_id",subject.getId());
            intent.putExtra("doc_id",subject.getDocCount());
            startActivity(intent);
        });
    }
}