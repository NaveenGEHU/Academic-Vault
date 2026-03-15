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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.R;
import com.academicvault.database.DatabaseHelper;
import com.academicvault.model.Document;
import com.academicvault.view_subject;

public class Document_Adapter extends RecyclerView.Adapter<Document_Adapter.DocumentViewHolder> {
    private static final String TAG = "Document_Adapter";
    private Context context;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private Runnable refreshCallback;

    public Document_Adapter(Context context, Cursor cursor, Runnable refreshCallback) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_card, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        try {
            if (cursor == null || !cursor.moveToPosition(position)) {
                return;
            }

            int idIndex = cursor.getColumnIndex("id");
            int titleIndex = cursor.getColumnIndex("title");
            int pathIndex = cursor.getColumnIndex("filePath");
            int typeIndex = cursor.getColumnIndex("fileType");
            int sizeIndex = cursor.getColumnIndex("fileSize");
            int dateIndex = cursor.getColumnIndex("dateAdded");
            int subIdIndex = cursor.getColumnIndex("subject_id");
            int mimeIndex = cursor.getColumnIndex("mimeType");

            final int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
            final String title = titleIndex != -1 ? cursor.getString(titleIndex) : "Unknown";
            final String path = pathIndex != -1 ? cursor.getString(pathIndex) : "";
            final String type = typeIndex != -1 ? cursor.getString(typeIndex) : "";
            final String size = sizeIndex != -1 ? cursor.getString(sizeIndex) : "";
            final String date = dateIndex != -1 ? cursor.getString(dateIndex) : "";
            final int subjectId = subIdIndex != -1 ? cursor.getInt(subIdIndex) : -1;
            final String mimeType = mimeIndex != -1 ? cursor.getString(mimeIndex) : null;

            holder.titleText.setText(title);
            holder.dateText.setText(date);
            holder.sizeText.setText(size);

            // Set icons based on file type or extension
            String lowerPath = path.toLowerCase();
            if (lowerPath.endsWith(".pdf") || (mimeType != null && mimeType.contains("pdf"))) {
                holder.iconView.setImageResource(R.drawable.book);
            } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".png") || (mimeType != null && mimeType.contains("image"))) {
                holder.iconView.setImageResource(R.drawable.logo); 
            } else {
                holder.iconView.setImageResource(R.drawable.icon_notes2);
            }

            holder.itemView.setOnClickListener(v -> {
                if (context instanceof view_subject && !path.isEmpty()) {
                    ((view_subject) context).openDocument(path, mimeType);
                }
            });

            holder.deleteBtn.setOnClickListener(v -> {
                try {
                    Document doc = new Document(id, subjectId, title, path, type, size, date, mimeType);
                    dbHelper.deleteDocument(doc);
                    Toast.makeText(context, "Document Deleted", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting doc", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null && !cursor.isClosed()) ? cursor.getCount() : 0;
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText, sizeText;
        ImageButton deleteBtn;
        ImageView iconView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.doc_title);
            dateText = itemView.findViewById(R.id.doc_dateofupload);
            sizeText = itemView.findViewById(R.id.doc_sizeofdoc);
            deleteBtn = itemView.findViewById(R.id.deleteDocBtn);
            iconView = itemView.findViewById(R.id.imageView6);
        }
    }
}
