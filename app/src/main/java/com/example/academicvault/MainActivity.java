package com.example.academicvault;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btnAddSubject = findViewById(R.id.btnAddSubject);
        btnAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSubjectActivity.class);
            startActivity(intent);
        });
        ListView listSubjects = findViewById(R.id.listSubjects);
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add("Mathematics");
        subjects.add("Physics");
        subjects.add("Computer Science");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);

        listSubjects.setAdapter(adapter);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}