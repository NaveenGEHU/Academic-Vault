package com.academicvault.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.R;
import com.academicvault.database.DatabaseHelper;

public class Note_Adapter extends RecyclerView.Adapter<Note_Adapter.NoteViewHolder> {
    private Context context;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private Runnable refreshCallback;

    public Note_Adapter(Context context, Cursor cursor, Runnable refreshCallback) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        try {
            if (!cursor.moveToPosition(position)) return;

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            final String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            final String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            final String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow("dateAdded"));

            holder.titleText.setText(title);
            holder.dateText.setText(dateAdded);
            holder.sizeText.setVisibility(View.GONE);
            holder.iconView.setImageResource(R.drawable.icon_notes);

            holder.itemView.setOnClickListener(v -> {
                showNotePreview(title, content);
            });

            holder.deleteBtn.setOnClickListener(v -> {
                try {
                    dbHelper.deleteNote(id);
                    Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                } catch (Exception e) {
                    Log.e("Note_Adapter", "Error deleting note", e);
                }
            });
        } catch (Exception e) {
            Log.e("Note_Adapter", "Error binding note", e);
        }
    }

    private void showNotePreview(String title, String content) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(content.isEmpty() ? "No content" : content);
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
            builder.show();
        } catch (Exception e) {
            Log.e("Note_Adapter", "Error showing note preview", e);
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText, sizeText;
        ImageView deleteBtn, iconView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.doc_title);
            dateText = itemView.findViewById(R.id.doc_dateofupload);
            sizeText = itemView.findViewById(R.id.doc_sizeofdoc);
            deleteBtn = itemView.findViewById(R.id.deleteDocBtn);
            iconView = itemView.findViewById(R.id.imageView6);
        }
    }
}
