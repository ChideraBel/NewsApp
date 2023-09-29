package com.example.newsapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.databinding.ArticleBinding;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    ArticleBinding binding;
    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);

        binding = ArticleBinding.bind(itemView);
    }
}
