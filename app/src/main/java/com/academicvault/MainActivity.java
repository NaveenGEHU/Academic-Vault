package com.academicvault;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.adapter.Subject_Adapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
//---MEMEBER VARIABLES---
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private Subject_Adapter subjectAdapter;
    private TextView totalSubCount;
// REFRESING THE NEWLY ADDED SUBJECT SO THAT IT DISPLAY ON SCREEN
    public void load(){
        dbHelper = new DatabaseHelper(this);
        try {
            //       Subject Adapter contaning all the data of cards
            subjectAdapter = new Subject_Adapter(this, dbHelper.getAllSubjects());

            //       Getting Recycler view Element in the UI
            recyclerView = findViewById(R.id.subject_recycleview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(subjectAdapter);

            //        TOTAL SUBJECT COUNT IN DASHBOARD
            totalSubCount = findViewById(R.id.totalsubjects);
            totalSubCount.setText(String.valueOf(subjectAdapter.getItemCount()));
        }
        catch (Exception e) {
            Log.e("DATABASE_ERROR", "Error occurred: ", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


//-----------------------------Insert the subject in the database----------------------------------------
    private boolean insert(String subName){
        if(subName.isEmpty()){
            Toast.makeText(this,"Subject Name cannot be empty",Toast.LENGTH_SHORT).show();
        }
        else {
            long id = dbHelper.insertSubject(subName);
            if (id != 0) {
                Toast.makeText(this, "Subject Added", Toast.LENGTH_SHORT).show();
                load();
                return true;
            } else {
                Toast.makeText(this, "Subject Not Added", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

//-----------------------LOADING THE CARDS FROM DATABASE USING ADAPTER-----------------------------
        load();

// -----------------ADD NEW SUBJECT EVENT---------------------------
        try{
            CardView addSubjectCard=findViewById(R.id.addsubjectbtn);
            addSubjectCard.setOnClickListener(v->{
                    View dialogBox= getLayoutInflater().inflate(R.layout.add_subject_dialog_box,null);
                    AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogBox).create();
                    dialog.show();
                    Button addBtn=dialogBox.findViewById(R.id.add_btn);
                    addBtn.setOnClickListener(y-> {
                        EditText subName=dialogBox.findViewById(R.id.subject_name_input);
                        boolean success=insert(subName.getText().toString().trim());
                        if(success){
                            dialog.dismiss();
                        }
                    });
                    TextView cancelBtn=dialogBox.findViewById(R.id.cancel_btn);
                    cancelBtn.setOnClickListener(x->{
                        dialog.dismiss();
                    });
            });
        }
        catch (Exception e) {
            Log.e("DATABASE_ERROR", "Error occurred: ", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


        try{}
        catch (Exception e) {
            Log.e("ERRROR", "Error occurred: ", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }



}

