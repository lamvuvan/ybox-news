package io.github.lamvv.yboxnews.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.repository.db.SharedPreference;
import io.github.lamvv.yboxnews.util.DeviceUtils;
import io.github.lamvv.yboxnews.util.ImageCache;
import io.github.lamvv.yboxnews.view.activity.DetailActivity;

/**
 * Created by lamvu on 10/9/2016.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ArticleAdapter";

    private static final int TYPE_LOAD = 0;

    private static final int TYPE_ARTICLE = 1;
    private List<Object> list;

    private Context context;
    public OnLoadMoreListener loadMoreListener;

    boolean isLoading = false, isMoreDataAvailable = true;
    private SharedPreference sharedPreference;

    private RelativeLayout rootLayout;

    private int lastPosition = RecyclerView.NO_POSITION;

    public ArticleAdapter(RelativeLayout rootLayout, Context context, List<Object> list){
        this.rootLayout = rootLayout;
        this.context = context;
        this.list = list;
        sharedPreference = new SharedPreference();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(!DeviceUtils.isTablet(context)) {
            switch (viewType){
                case TYPE_ARTICLE:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
                case TYPE_LOAD:
                    return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
                default:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
            }
        } else {
            switch (viewType) {
                case TYPE_ARTICLE:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article_tablet, parent, false));
                case TYPE_LOAD:
                    return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
                default:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article_tablet, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener != null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        switch (getItemViewType(position)){
            case TYPE_ARTICLE:
                ((ArticleViewHolder)holder).bindData((Article) list.get(position));
                break;
            default:

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getItemCount() -1) {
            return TYPE_LOAD;
        } else {
            return TYPE_ARTICLE;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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

        void bindData(Article article){
            int position = getAdapterPosition();
            setAnimation(ivImage, position);
            Bitmap bitmap = ImageCache.getBitmapFromCache(article.getImage());
            if(bitmap == null) {
                Picasso.with(context)
                        .load(article.getImage())
                        .placeholder(R.drawable.default_thumbnail)
                        .error(R.drawable.default_thumbnail)
                        .into(ivImage);
            }else{
                ivImage.setImageBitmap(bitmap);
            }

            tvTitle.setText(article.getTitle());
            tvCreatedAt.setText(article.getTimestamps().getCreatedAt());

            //Use Html.fromHtml(String) on API Level 23 and older devices, and Html.fromHtml(String, int) on API Level 24+ devices
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw()));
            }

            if (checkFavoriteItem(article)) {
                ibFavorite.setImageResource(R.drawable.ic_fav_selected);
                ibFavorite.setTag("active");
            } else {
                ibFavorite.setImageResource(R.drawable.ic_fav_normal);
                ibFavorite.setTag("deactive");
            }

            ivImage.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
            tvContent.setOnClickListener(this);
            tvCreatedAt.setOnClickListener(this);

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String tag = ibFavorite.getTag().toString();
                    if (tag.equalsIgnoreCase("deactive")) {
                        sharedPreference.addFavorite(context, (Article) list.get(position));
                        ibFavorite.setTag("active");
                        ibFavorite.setImageResource(R.drawable.ic_fav_selected);
                        Snackbar.make(rootLayout, context.getResources().getString(R.string.add_favorite_message),
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        sharedPreference.removeFavorite(context, (Article) list.get(position));
                        ibFavorite.setTag("deactive");
                        ibFavorite.setImageResource(R.drawable.ic_fav_normal);
                        Snackbar.make(rootLayout, context.getResources().getString(R.string.remove_favorite_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Article article = (Article) list.get(position);
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("article", article);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private class LoadViewHolder extends RecyclerView.ViewHolder{

        public LoadViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /**
     * notifyDataSetChanged is final method so we can't override it call adapter.notifyDataChanged(); after update the list
    */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener){
        this.loadMoreListener = loadMoreListener;
    }

    private boolean checkFavoriteItem(Article checkArticle) {
        boolean check = false;
        List<Article> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (Article article : favorites) {
                if (article.equals(checkArticle)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    private void setAnimation(ImageView viewToAnimate, int position){
        if (position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void setAnimation(View view, int position){
        if(position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

}
