package com.academicvault.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.R;
import com.academicvault.database.DatabaseHelper;
import com.academicvault.model.Subject;
import com.academicvault.view_subject;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class Subject_Adapter extends RecyclerView.Adapter<Subject_Adapter.ViewHolder> {
    private ArrayList<Subject> subjects;
    private Context context;
    private Runnable refreshCallback;
    private DatabaseHelper dbHelper;

    public Subject_Adapter(Context context, Cursor cursor, Runnable refreshCallback) {
        this.context = context;
        this.refreshCallback = refreshCallback;
        this.dbHelper = new DatabaseHelper(context);
        subjects = new ArrayList<>();
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"));
                    int docCount = cursor.getInt(cursor.getColumnIndexOrThrow("doc_count"));
                    subjects.add(new Subject(id, name, docCount));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("Subject_Adapter", "Error reading cursor", e);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Subject subject = subjects.get(position);
            holder.subjectName.setText(subject.getName());
            holder.docCount.setText(String.valueOf(subject.getDocCount()));

            holder.subjectcard.setOnClickListener(v -> {
                try {
                    android.content.Intent intent = new android.content.Intent(context, view_subject.class);
                    intent.putExtra("subject_id", subject.getId());
                    intent.putExtra("subject_name", subject.getName());
                    intent.putExtra("doc_Count", subject.getDocCount());
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e("CARD_CLICK", "Error opening subject", e);
                    Toast.makeText(context, "Error opening subject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.deleteBtn.setOnClickListener(v -> {
                try {
                    dbHelper.deleteSubject(subject.getId());
                    Toast.makeText(context, "Subject Deleted", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                } catch (Exception e) {
                    Log.e("Subject_Adapter", "Error deleting subject", e);
                }
            });
        } catch (Exception e) {
            Log.e("Subject_Adapter", "Error binding view holder", e);
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView subjectcard;
        TextView subjectName;
        TextView docCount;
        ImageButton deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectname);
            docCount = itemView.findViewById(R.id.documentcountofsubject);
            subjectcard = itemView.findViewById(R.id.subjectcard);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
