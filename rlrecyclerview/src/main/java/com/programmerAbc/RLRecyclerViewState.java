package com.programmerAbc;

import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;


import com.programmerAbc.rlrv.R;

import java.util.ArrayList;
import java.util.List;


public class RLRecyclerViewState<T> implements LoadDataResult<T> {
    public static final int STATE_FIRST_SHOW = 0;
    public static final int STATE_FIRST_REFRESHING = 1;
    public static final int STATE_PULL_REFRESHING = 2;
    public static final int STATE_REFRESH_ERROR = 3;
    public static final int STATE_REFRESH_SUCCESS = 4;
    public static final int STATE_LOADING_MORE = 5;
    public static final int STATE_NO_MORE_DATA = 6;
    public static final int STATE_LOAD_MORE_COMPLETE = 7;
    public static final int STATE_LOAD_MORE_FAIL = 8;

    MutableLiveData<Integer> state;
    List<T> allData = new ArrayList<>();
    List<T> respData = new ArrayList<>();
    int page = 0;
    Callback<T> callback;
    boolean autoHideFooter;
    boolean disableManualRefresh;
    int errorLayoutResId;
    int loadingLayoutResId;
    int emptyLayoutResId;
    int backgroundResId;
    int headerStyle;
    Parcelable recyclerViewState;

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }

    public void setRecyclerViewState(Parcelable recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    public List<T> getAllData() {
        return allData;
    }

    public List<T> getRespData() {
        return respData;
    }

    public int getPage() {
        return page;
    }

    public boolean isDisableManualRefresh() {
        return disableManualRefresh;
    }

    public void setDisableManualRefresh(boolean disableManualRefresh) {
        this.disableManualRefresh = disableManualRefresh;
    }

    public int getErrorLayoutResId() {
        return errorLayoutResId;
    }

    public void setErrorLayoutResId(int errorLayoutResId) {
        this.errorLayoutResId = errorLayoutResId;
    }

    public int getLoadingLayoutResId() {
        return loadingLayoutResId;
    }

    public void setLoadingLayoutResId(int loadingLayoutResId) {
        this.loadingLayoutResId = loadingLayoutResId;
    }

    public int getEmptyLayoutResId() {
        return emptyLayoutResId;
    }

    public void setEmptyLayoutResId(int emptyLayoutResId) {
        this.emptyLayoutResId = emptyLayoutResId;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    public int getHeaderStyle() {
        return headerStyle;
    }

    public void setHeaderStyle(int headerStyle) {
        this.headerStyle = headerStyle;
    }

    public RLRecyclerViewState(Callback<T> callback) {
        errorLayoutResId = R.layout.rlrv_error_layout;
        loadingLayoutResId = R.layout.rlrv_loading_layout;
        emptyLayoutResId = R.layout.rlrv_empyt_layout;
        backgroundResId = R.drawable.rlrv_background;
        headerStyle = RLRecyclerView.HEADER_STYLE_MATERIAL;
        autoHideFooter = true;
        disableManualRefresh = false;
        this.callback = callback;
        state = new MutableLiveData<>();
        state.setValue(STATE_FIRST_SHOW);
    }

    public boolean isAutoHideFooter() {
        return autoHideFooter;
    }

    public void setAutoHideFooter(boolean autoHideFooter) {
        this.autoHideFooter = autoHideFooter;
    }


    private void loadData(int nextState) {
        switch (getCurrentState()) {
            case STATE_FIRST_SHOW:
            case STATE_LOAD_MORE_COMPLETE:
            case STATE_LOAD_MORE_FAIL:
            case STATE_REFRESH_SUCCESS:
            case STATE_NO_MORE_DATA:
            case STATE_REFRESH_ERROR: {
                switch (nextState) {
                    case STATE_FIRST_REFRESHING:
                    case STATE_PULL_REFRESHING:
                        page = 0;
                    default:
                        page++;
                        break;
                }
                state.setValue(nextState);
                callback.loadData(this, page, this);
                break;
            }
            default:
                break;
        }
    }

    public void pullRefresh() {
        loadData(STATE_PULL_REFRESHING);
    }

    public void firstRefresh() {
        loadData(STATE_FIRST_REFRESHING);
    }

    public void loadMore() {
        loadData(STATE_LOADING_MORE);
    }


    public int getCurrentState() {
        Integer value = state.getValue();
        return value == null ? STATE_FIRST_SHOW : value;
    }

    public MutableLiveData<Integer> getState() {
        return state;
    }

    @Override
    public void result(boolean result, String message, boolean isLast, List<T> data) {
        if (result) {
            switch (getCurrentState()) {
                case STATE_FIRST_REFRESHING:
                case STATE_PULL_REFRESHING:
                    allData.clear();
                    if (data != null) {
                        allData.addAll(data);
                        respData = data;
                    } else {
                        respData.clear();
                    }
                    if (isLast) {
                        state.setValue(STATE_NO_MORE_DATA);
                    } else {
                        state.setValue(STATE_REFRESH_SUCCESS);
                    }
                    break;
                default:
                    if (data != null) {
                        allData.addAll(data);
                        respData = data;
                    } else {
                        respData.clear();
                    }
                    if (isLast) {
                        state.setValue(STATE_NO_MORE_DATA);
                    } else {
                        state.setValue(STATE_LOAD_MORE_COMPLETE);
                    }
                    break;
            }
        } else {
            switch (getCurrentState()) {
                case STATE_FIRST_REFRESHING:
                case STATE_PULL_REFRESHING:
                    state.setValue(STATE_REFRESH_ERROR);
                    break;
                case STATE_LOADING_MORE:
                    state.setValue(STATE_LOAD_MORE_FAIL);
                    break;
            }

        }
    }


    public interface Callback<T> {
        void loadData(RLRecyclerViewState<T> instance, int page, LoadDataResult<T> loadDataResult);
    }

}
