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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.SharedPreferenceUtils;
import io.github.lamvv.yboxnews.view.activity.DetailArticleActivity;

/**
 * Created by lamvu on 11/2/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ARTICLE = 1;

    private Context context;
    private List<Article> favoriteArticles;
    private SharedPreferenceUtils sharedPreference;
    private LinearLayout rootLayout;

    public FavoriteAdapter(LinearLayout rootLayout, Context context, List<Article> favoriteArticles){
        this.rootLayout = rootLayout;
        this.context = context;
        this.favoriteArticles = favoriteArticles;
        sharedPreference = new SharedPreferenceUtils();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
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

    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image)
        ImageView ivImage;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.content)
        TextView tvContent;
        @BindView(R.id.createdAt)
        TextView tvCreatedAt;
        @BindView(R.id.favorite)
        ImageButton ibFavorite;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(final Article article) {
            Picasso.with(context)
                    .load(article.getImage())
                    .placeholder(R.drawable.default_thumbnail)
                    .error(R.drawable.default_thumbnail)
                    .into(ivImage);

            tvTitle.setText(article.getTitle());
            tvCreatedAt.setText(article.getTimestamps().getCreatedAt());

            //Use Html.fromHtml(String) on API Level 23 and older devices, and Html.fromHtml(String, int) on API Level 24+ devices
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw()));
            }

            ivImage.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
            tvContent.setOnClickListener(this);
            tvCreatedAt.setOnClickListener(this);

            ibFavorite.setImageResource(R.drawable.ic_fav_selected);

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    favoriteArticles.remove(position);
                    sharedPreference.removeFavorite(context, favoriteArticles.get(position));
                    notifyItemRemoved(position);
                    ibFavorite.setTag("deactive");
                    ibFavorite.setImageResource(R.drawable.ic_fav_normal);
                    Snackbar.make(rootLayout, context.getResources().getString(R.string.remove_favorite_message),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Article article = favoriteArticles.get(position);
            Intent intent = new Intent(context, DetailArticleActivity.class);
            intent.putExtra("article", article);
            context.startActivity(intent);
        }
    }

}
