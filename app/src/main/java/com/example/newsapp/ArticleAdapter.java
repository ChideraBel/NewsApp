package com.example.newsapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ArticleAdapter extends
        RecyclerView.Adapter<ArticleViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Article> articleList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat print = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    public ArticleAdapter(MainActivity mainActivity, ArrayList<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        int brokenIcon = mainActivity.getResources().getIdentifier("brokenimage", "drawable", mainActivity.getPackageName());
        int loadingIcon = mainActivity.getResources().getIdentifier("loading", "drawable", mainActivity.getPackageName());
        int noImageIcon = mainActivity.getResources().getIdentifier("noimage", "drawable", mainActivity.getPackageName());
        int pageNumber = position + 1;
        Article article = articleList.get(position);
        Date publishedDate;

       checkNullValues(article, holder);

        holder.binding.articleAuthor.setText((article.getAuthor().equals("null") ? " ": article.getAuthor()));
        holder.binding.pageCounter.setText(pageNumber + " of "+getItemCount());

        if(!article.getUrlToImage().equals("") && !article.getUrlToImage().equals("null")) {
            Picasso.get().load(article.getUrlToImage())
                    .placeholder(loadingIcon)
                    .into(holder.binding.articleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("OFFICIAL ADAPTER", "onSuccess: ");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("OFFICIAL ADAPTER", "onError: " + e);
                            holder.binding.articleImageView.setImageResource(brokenIcon);
                        }
                    });
        }else{
            holder.binding.articleImageView.setImageResource(noImageIcon);
        }

        try {
            publishedDate = dateFormat.parse(article.getPublishedAt());
            holder.binding.dateReleased.setText(print.format(publishedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkNullValues(Article article, ArticleViewHolder holder){
        if(article.getDescription().equals("") || article.getDescription().equals("null")){
            holder.binding.articleDescription.setVisibility(View.GONE);
        }else{
            holder.binding.articleDescription.setText(article.getDescription());
            holder.binding.articleDescription.setVisibility(View.VISIBLE);
        }

        if(article.getTitle().equals("") || article.getTitle().equals("null")){
            holder.binding.articleTitle.setVisibility(View.GONE);
        }else{
            holder.binding.articleTitle.setVisibility(View.VISIBLE);
            holder.binding.articleTitle.setText(article.getTitle());
        }
    }
    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
