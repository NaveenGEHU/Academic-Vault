package com.academicvault.adapter;

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
import com.academicvault.view_subject;

public class Link_Adapter extends RecyclerView.Adapter<Link_Adapter.LinkViewHolder> {
    private Context context;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private Runnable refreshCallback;

    public Link_Adapter(Context context, Cursor cursor, Runnable refreshCallback) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_card, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        try {
            if (!cursor.moveToPosition(position)) return;

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            final String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            final String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow("dateAdded"));
            final String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));

            holder.titleText.setText(title);
            holder.dateText.setText(dateAdded);
            holder.sizeText.setText(url);
            holder.iconView.setImageResource(android.R.drawable.ic_menu_share); 

            holder.itemView.setOnClickListener(v -> {
                if (context instanceof view_subject) {
                    ((view_subject) context).openLink(url);
                }
            });

            holder.deleteBtn.setOnClickListener(v -> {
                try {
                    dbHelper.deleteLink(id);
                    Toast.makeText(context, "Link Deleted", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                } catch (Exception e) {
                    Log.e("Link_Adapter", "Error deleting link", e);
                }
            });
        } catch (Exception e) {
            Log.e("Link_Adapter", "Error binding view holder", e);
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText, sizeText;
        ImageView deleteBtn, iconView;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.doc_title);
            dateText = itemView.findViewById(R.id.doc_dateofupload);
            sizeText = itemView.findViewById(R.id.doc_sizeofdoc);
            deleteBtn = itemView.findViewById(R.id.deleteDocBtn);
            iconView = itemView.findViewById(R.id.imageView6);
        }
    }
}
