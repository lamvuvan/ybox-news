package io.github.lamvv.yboxnews.repository.network;

/**
 * Created by lamvu on 10/8/2016.
 */

public interface OnGetDetailArticleListener<T> {

    void showProgress();

    void hideProgress();

    void onGetDetailTaskComplete(T result);
}
