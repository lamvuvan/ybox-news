package io.github.lamvv.yboxnews.interfaces;

/**
 * Created by lamvu on 10/8/2016.
 */

public interface GetArticleDetailTaskCompleteListener<T> {

    void showProgress();

    void hideProgress();

    void onGetDetailTaskComplete(T result);
}
