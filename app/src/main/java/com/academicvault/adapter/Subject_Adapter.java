package com.academicvault.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.R;
import com.academicvault.model.Subject;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Subject_Adapter extends RecyclerView.Adapter<Subject_Adapter.ViewHolder> {
    private ArrayList<Subject> subjects;
    private int totalSubjects;
    private Context context;

    public Subject_Adapter(Context context, Cursor cursor) {
        this.context = context;
        subjects = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int docCount = cursor.getInt(2);
            subjects.add(new Subject(id, name, docCount));
        }
        cursor.close();
        this.totalSubjects = subjects.size();
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.subjectName.setText(subject.getName());
        holder.docCount.setText(String.valueOf(subject.getDocCount()));

//----------------------MAKING THE CARD CLCIKABLE--------------------------------------
        try {
                holder.subjectcard.setOnClickListener(v -> {
                Toast.makeText(context, // THIS IS TEST
                        "CARD CLICKED", Toast.LENGTH_LONG).show();
                android.content.Intent intent = new android.content.Intent(context, com.academicvault.view_subject.class);
                //           PASSING THE SUBJECT DETAIL TO THE NEXT ACTIVITY
                intent.putExtra("subject_id", subject.getId());
                intent.putExtra("subject_name", subject.getName());
                intent.putExtra("doc_Count", subject.getDocCount());
                //            STARTS THE NEXT ACTIVITY
                context.startActivity(intent);
            });
        } catch (Exception e) {
            Log.e("CARD EVENTCLICKING", "Error occurred: ", e);
            Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public int getItemCount() {
        return totalSubjects;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView subjectcard;
        TextView subjectName;
        TextView docCount;
        BackgroundColorSpan BgColor; // need to be set to get different color in the book icon in background

        public ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectname);
            docCount = itemView.findViewById(R.id.documentcountofsubject);
            subjectcard=itemView.findViewById(R.id.subjectcard);
        }
    }
}
