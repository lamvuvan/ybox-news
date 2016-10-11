package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 10/9/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ARTICLE = 0;
    public static final int TYPE_LOAD = 1;

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
        if(viewType == TYPE_ARTICLE){
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
        if(getItemViewType(position) == TYPE_ARTICLE){
            ((ArticleHolder)holder).bindData(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).getType().equals("fil")){
            return TYPE_ARTICLE;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ArticleHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvTitle;
        TextView tvContent;
        TextView tvUpdatedt;
        ShareButton btnShareFacebook;

        public ArticleHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView)itemView.findViewById(R.id.image);
            tvTitle = (TextView)itemView.findViewById(R.id.title);
            tvContent = (TextView)itemView.findViewById(R.id.content);
            tvUpdatedt = (TextView)itemView.findViewById(R.id.updatedAt);
            btnShareFacebook = (ShareButton)itemView.findViewById(R.id.btnShareFacebook);
        }

        void bindData(Article article){
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage(article.getImage(), ivImage);

            Picasso.with(itemView.getContext()).load(article.getImage()).into(ivImage);
            tvTitle.setText(article.getTitle().toString());
            /*
            Use Html.fromHtml(String) on API Level 23 and older devices, and Html.fromHtml(String, int) on API Level 24+ devices
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw().toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvContent.setText(Html.fromHtml(article.getContent().getRaw().toString()));
            }
            tvUpdatedt.setText(article.getTimestamps().getUpdatedAt().toString());

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

    /** notifyDataSetChanged is final method so we can't override it
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
