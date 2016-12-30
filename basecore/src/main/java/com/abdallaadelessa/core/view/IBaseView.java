package com.abdallaadelessa.core.view;

/**
 * Created by Abdalla on 16/10/2016.
 */

public interface IBaseView {
    void showProgress(boolean show);

    void handleError(Throwable throwable);

    void handleNoData();
}
