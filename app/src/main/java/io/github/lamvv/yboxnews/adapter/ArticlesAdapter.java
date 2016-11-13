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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.CheckConfig;
import io.github.lamvv.yboxnews.util.SharedPreference;
import io.github.lamvv.yboxnews.view.activity.ArticleActivity;

/**
 * Created by lamvu on 10/9/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LOAD = 0;
    private static final int TYPE_ARTICLE = 1;
    private static final int TYPE_AD = 2;

    private List<Object> mList;
    private Context mContext;

    public OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    private SharedPreference sharedPreference;
    private LinearLayout rootLayout;

    public ArticlesAdapter(LinearLayout rootLayout, Context context, List<Object> list){
        this.rootLayout = rootLayout;
        this.mContext = context;
        this.mList = list;
        sharedPreference = new SharedPreference();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(!CheckConfig.isTablet(mContext)) {
            switch (viewType){
                case TYPE_ARTICLE:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
                case TYPE_LOAD:
                    return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
                case TYPE_AD:
                    return new NativeExpressAdViewHolder(inflater.inflate(R.layout.native_express_ad_container, parent, false));
                default:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
            }
        } else {
            switch (viewType) {
                case TYPE_ARTICLE:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article_tablet, parent, false));
                case TYPE_LOAD:
                    return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
                case TYPE_AD:
                    return new NativeExpressAdViewHolder(inflater.inflate(R.layout.native_express_ad_container, parent, false));
                default:
                    return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener != null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        switch (getItemViewType(position)){
            case TYPE_ARTICLE:
                ((ArticleViewHolder)holder).bindData((Article) mList.get(position));
                break;
            case TYPE_AD:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 1)
            return TYPE_AD;
        else {
            if (position >= getItemCount() - 1) {
                return TYPE_LOAD;
            } else {
                return TYPE_ARTICLE;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
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

        void bindData(Article article){
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

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String tag = ibFavorite.getTag().toString();
                    if (tag.equalsIgnoreCase("deactive")) {
                        sharedPreference.addFavorite(mContext, (Article) mList.get(position));
                        ibFavorite.setTag("active");
                        ibFavorite.setImageResource(R.drawable.ic_fav_selected);
                        Snackbar.make(rootLayout, mContext.getResources().getString(R.string.add_favorite_message),
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        sharedPreference.removeFavorite(mContext, position);
                        ibFavorite.setTag("deactive");
                        ibFavorite.setImageResource(R.drawable.ic_fav_normal);
                        Snackbar.make(rootLayout, mContext.getResources().getString(R.string.remove_favorite_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Article article = (Article) mList.get(position);
            Intent intent = new Intent(mContext, ArticleActivity.class);
            intent.putExtra("article", article);
            mContext.startActivity(intent);
        }
    }

    private class LoadViewHolder extends RecyclerView.ViewHolder{

        public LoadViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdView nativeAdView;

        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
            nativeAdView = (NativeExpressAdView)itemView.findViewById(R.id.nativeAdView);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                    .build();
            nativeAdView.loadAd(request);
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
        List<Article> favorites = sharedPreference.getFavorites(mContext);
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

}
