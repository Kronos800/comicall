package com.example.comicall.comment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicall.R;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> mComments;
    private Context mContext;

    private boolean mIsLoading = true;

    private boolean justAdded;

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

        // Setea el texto del comentario
        holder.commentText.setText(comment.getComment());

        // Setea el nombre del usuario que hizo el comentario
        holder.userName.setText(comment.getUsername());

        // Setea la fecha en la que se hizo el comentario
        if(!comment.getJustAdded()) {
            Long createdAt = comment.getTimestamp() != null ? comment.getTimestamp().getTime() / 1000L : null;
            String timeAgo = null;
            if (createdAt != null) {
                int createdAtInt = createdAt.intValue();
                timeAgo = comment.getTimeAgo(createdAtInt);
            }
            holder.timeAgo.setText(timeAgo);
        } else {
            comment.setJustAdded(false);
            holder.timeAgo.setText("Just now");
        }

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