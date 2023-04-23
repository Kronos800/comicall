package com.example.comicall;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> mComments;
    private Context mContext;

    private boolean mIsLoading = true;

    public CommentsAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Comment comment = mComments.get(position);

        // Setea el texto del comentario en el TextView correspondiente
        holder.commentText.setText(comment.getComment());

        // Setea el nombre del usuario que hizo el comentario en el TextView correspondiente
        holder.userName.setText(comment.getUsername());

        // Setea la fecha en la que se hizo el comentario en el TextView correspondiente

        Long createdAt = comment.getTimestamp() != null ? comment.getTimestamp().getTime() / 1000L : null;
        String timeAgo = null;
        if (createdAt != null) {
            int createdAtInt = createdAt.intValue();
            timeAgo = comment.getTimeAgo(createdAtInt);
        }
        holder.timeAgo.setText(timeAgo);

        /*// Carga la imagen del usuario que hizo el comentario en el ImageView correspondiente
        Glide.with(mContext)
                .load(comment.getUserImageURL())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.userImage);*/

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void cleanList() { this.mComments.clear(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView commentText;
        public TextView userName;

        public TextView timeAgo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentText = itemView.findViewById(R.id.comment_text);
            userName = itemView.findViewById(R.id.comment_username);
            timeAgo = itemView.findViewById(R.id.comment_time_ago);
        }
    }
}