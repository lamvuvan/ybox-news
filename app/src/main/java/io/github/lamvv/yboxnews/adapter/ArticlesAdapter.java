package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
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
    private static final int AD_FORM = 2;

    private List<Object> mList;
    private Context mContext;
    private NativeAdsManager mAds;
    private NativeAd mAd = null;

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

    public ArticlesAdapter(Context context, List<Object> list, NativeAdsManager ads){
        this.mContext = context;
        this.mList = list;
        this.mAds = ads;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(!CheckConfig.isTablet(mContext)) {
            if(viewType == TYPE_ARTICLE){
                return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
            }else{
                return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
            }
        } else {
            if(viewType == TYPE_ARTICLE){
                return new ArticleViewHolder(inflater.inflate(R.layout.item_article_tablet, parent, false));
            }else{
                return new LoadViewHolder(inflater.inflate(R.layout.item_load, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener != null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        if(getItemViewType(position) == TYPE_ARTICLE){
            ((ArticleViewHolder)holder).bindData((Article) mList.get(position));
        }
//        if (holder.getItemViewType() == TYPE_AD) {
//            if (mAd != null) {
//                ((AdHolder)holder).bindView(mAd);
//            } else if (mAds != null && mAds.isLoaded()) {
//                mAd = mAds.nextNativeAd();
//                ((AdHolder)holder).bindView(mAd);
//            } else {
//                ((AdHolder)holder).bindView(null);
//            }
//        } else {
//            int index = position;
//            if (index != 0) {
//                index--;
//            }
//            Article article = (Article) mList.get(index);
//            ((ArticleViewHolder)holder).bindData(article);
//        }
    }

    @Override
    public int getItemViewType(int position) {
//        String type = ((Article)mList.get(position)).getType();
//        if(type.equals("fil")) {
//            return TYPE_ARTICLE;
//        } else {
//            return TYPE_LOAD;
//        }
        if(position >= getItemCount() - 1){
            return TYPE_LOAD;
        } else {
            return TYPE_ARTICLE;
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

    private class AdHolder extends RecyclerView.ViewHolder {
        private MediaView mAdMedia;
        private ImageView mAdIcon;
        private TextView mAdTitle;
        private TextView mAdBody;
        private TextView mAdSocialContext;
        private Button mAdCallToAction;

        public AdHolder(View view) {
            super(view);

            if (AD_FORM == 2) {
                mAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
                mAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
                mAdCallToAction = (Button)view.findViewById(R.id.native_ad_call_to_action);
            }
            else {
                mAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
                mAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
                mAdBody = (TextView) view.findViewById(R.id.native_ad_body);
                mAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
                mAdCallToAction = (Button)view.findViewById(R.id.native_ad_call_to_action);
                mAdIcon = (ImageView)view.findViewById(R.id.native_ad_icon);
            }
        }

        public void bindView(NativeAd ad) {
            if (ad == null) {
                if (AD_FORM == 2) {
                    mAdSocialContext.setText("No Ad");
                } else {
                    mAdTitle.setText("No Ad");
                    mAdBody.setText("Ad is not loaded.");
                }
            } else {
                if (AD_FORM == 2) {
                    mAdSocialContext.setText(ad.getAdSocialContext());
                    mAdCallToAction.setText(ad.getAdCallToAction());
                    mAdMedia.setNativeAd(ad);
                } else {
                    mAdTitle.setText(ad.getAdTitle());
                    mAdBody.setText(ad.getAdBody());
                    mAdSocialContext.setText(ad.getAdSocialContext());
                    mAdCallToAction.setText(ad.getAdCallToAction());
                    mAdMedia.setNativeAd(ad);
                    NativeAd.Image adIcon = ad.getAdIcon();
                    NativeAd.downloadAndDisplayImage(adIcon, mAdIcon);
                }
            }
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
