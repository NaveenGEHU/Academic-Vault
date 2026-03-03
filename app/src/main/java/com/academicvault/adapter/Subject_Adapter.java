package com.academicvault.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    }

    public int getItemCount() {
        return totalSubjects;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        TextView docCount;
        BackgroundColorSpan BgColor; // need to be set to get different color in the book icon in background

        public ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectname);
            docCount = itemView.findViewById(R.id.documentcountofsubject);
        }
    }
}
