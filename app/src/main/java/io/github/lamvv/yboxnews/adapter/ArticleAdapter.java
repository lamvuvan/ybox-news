package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 10/6/2016.
 */

public class ArticleAdapter extends BaseAdapter {

    private List<Article> mList;
    private Context mContext;

    private RelativeLayout.LayoutParams layoutParams;

    public ArticleAdapter(Context context, List<Article> list){
        this.mContext = context;
        this.mList = list;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        layoutParams = new RelativeLayout.LayoutParams(width, height/2);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        view.setLayoutParams(layoutParams);
        Article item = mList.get(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.getImage(), holder.ivImage);
        return view;
    }

    class ViewHolder{

        private ImageView ivImage;

        public ViewHolder(View view){
            ivImage = (ImageView)view.findViewById(R.id.ivImage);
        }
    }
}
