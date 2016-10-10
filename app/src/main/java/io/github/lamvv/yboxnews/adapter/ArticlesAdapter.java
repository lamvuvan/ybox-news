package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.constant.Constant;
import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 10/9/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> mList;

    public OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    public ArticlesAdapter(Context context, List<Article> list){
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == Constant.TYPE_ARTICLE){
            return new ArticleHolder(inflater.inflate(R.layout.item_article, parent, false));
        }else{
            return new LoadHolder(inflater.inflate(R.layout.item_load, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener != null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        if(getItemViewType(position) == Constant.TYPE_ARTICLE){
            ((ArticleHolder)holder).bindData(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).getType().equals("fil")){
            return Constant.TYPE_ARTICLE;
        }else{
            return Constant.TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ArticleHolder extends RecyclerView.ViewHolder{

        ImageView ivImage;
        ShareButton btnShareFacebook;

        public ArticleHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView)itemView.findViewById(R.id.ivImage);
            btnShareFacebook = (ShareButton)itemView.findViewById(R.id.btnShareFacebook);
        }

        void bindData(Article article){
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage(article.getImage(), ivImage);
            Picasso.with(itemView.getContext()).load(article.getImage()).into(ivImage);
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(article.getTitle())
                    .setImageUrl(Uri.parse(article.getImage()))
                    .setContentUrl(Uri.parse(article.getLinks().getDetail()))
                    .build();
            btnShareFacebook.setShareContent(shareLinkContent);
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{

        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
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
}
