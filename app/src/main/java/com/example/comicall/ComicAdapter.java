package com.example.comicall;
import android.content.Context;
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

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    private List<Comic> mComics;
    private Context mContext;

    private boolean mIsLoading = true;

    public ComicAdapter(Context context, List<Comic> comics) {
        mContext = context;
        mComics = comics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comic comic = mComics.get(position);

        URL url = null;
        String urlImage = comic.getThumbnailUrl().replace("http", "https");
        if (urlImage.contains("image_not_available")) {
            holder.mImageView.setImageResource(R.drawable.image_not_found);
        }else {
            try {
                url = new URL(urlImage);
                Glide.with(mContext)
                        .load(url)
                        .centerCrop()
                        .into(holder.mImageView);
            } catch (MalformedURLException e) {
                Log.d("Error", e.toString());
            }
        }
        holder.mTextView.setText(comic.getTitle());
    }

    @Override
    public int getItemCount() {
        return mComics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;
        private CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.comic_image);
            mTextView = itemView.findViewById(R.id.comic_title);
            mCardView = itemView.findViewById(R.id.comic_card);

            // Calcular el ancho del CardView para que se muestren tres por línea
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int cardWidth = width / 3;
            ViewGroup.LayoutParams params = mCardView.getLayoutParams();
            params.width = cardWidth;
            mCardView.setLayoutParams(params);
        }
    }
}

