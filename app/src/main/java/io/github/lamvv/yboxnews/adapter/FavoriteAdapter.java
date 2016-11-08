package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.SharedPreference;
import io.github.lamvv.yboxnews.view.activity.ArticleActivity;

/**
 * Created by lamvu on 11/2/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ARTICLE = 1;

    private Context mContext;
    private List<Article> favoriteArticles;
    private SharedPreference sharedPreference;
    private LinearLayout rootLayout;

    public FavoriteAdapter(LinearLayout rootLayout, Context context, List<Article> favoriteArticles){
        this.rootLayout = rootLayout;
        this.mContext = context;
        this.favoriteArticles = favoriteArticles;
        sharedPreference = new SharedPreference();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_ARTICLE) {
            ((ArticleViewHolder)holder).bindData(favoriteArticles.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ARTICLE;
    }

    @Override
    public int getItemCount() {
        return favoriteArticles.size();
    }

    private class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImage;
        TextView tvTitle;
        TextView tvContent;
        TextView tvUpdatedAt;
        ImageButton ibFavorite;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView)itemView.findViewById(R.id.image);
            tvTitle = (TextView)itemView.findViewById(R.id.title);
            tvContent = (TextView)itemView.findViewById(R.id.content);
            tvUpdatedAt = (TextView)itemView.findViewById(R.id.updatedAt);
            ibFavorite = (ImageButton)itemView.findViewById(R.id.favorite);
        }

        void bindData(final Article article) {
            Picasso.with(mContext)
                    .load(article.getImage())
                    .placeholder(R.drawable.default_thumbnail)
                    .error(R.drawable.default_thumbnail)
                    .into(ivImage);

            tvTitle.setText(article.getTitle().toString());

            //Use Html.fromHtml(String) on API Level 23 and older devices, and Html.fromHtml(String, int) on API Level 24+ devices
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw().toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw().toString()));
            }
            tvUpdatedAt.setText(article.getTimestamps().getUpdatedAt().toString());

            ivImage.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
            tvContent.setOnClickListener(this);

            ibFavorite.setImageResource(R.drawable.ic_fav_selected);

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    favoriteArticles.remove(position);
                    sharedPreference.removeFavorite(mContext, position);
                    notifyItemRemoved(position);
                    ibFavorite.setTag("deactive");
                    ibFavorite.setImageResource(R.drawable.ic_fav_normal);
                    Snackbar.make(rootLayout, mContext.getResources().getString(R.string.remove_favorite_message),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Article article = favoriteArticles.get(position);
            Intent intent = new Intent(mContext, ArticleActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        }
    }

}
