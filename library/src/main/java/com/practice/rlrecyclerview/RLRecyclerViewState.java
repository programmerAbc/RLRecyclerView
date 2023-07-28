package com.practice.rlrecyclerview;

import android.telecom.Call;

import androidx.lifecycle.MutableLiveData;


public class RLRecyclerViewState implements LoadDataResult {
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
    RLRecyclerViewAdapter adapter;
    int page = 0;
    Callback callback;
    boolean autoHideFooter = false;
    boolean disableManualRefresh = false;
    int errorLayoutResId;
    int loadingLayoutResId;
    int emptyLayoutResId;
    int backgroundResId;
    int headerStyle;

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

    public RLRecyclerViewState(RLRecyclerViewAdapter adapter, Callback callback) {
        errorLayoutResId = R.layout.rlrv_error_layout;
        loadingLayoutResId = R.layout.rlrv_loading_layout;
        emptyLayoutResId = R.layout.rlrv_empyt_layout;
        backgroundResId = R.drawable.rlrv_background;
        headerStyle = RLRecyclerView.HEADER_STYLE_MATERIAL;
        this.adapter = adapter;
        this.callback = callback;
        state = new MutableLiveData<>();
        state.setValue(STATE_FIRST_SHOW);
        updateAdapter(STATE_FIRST_SHOW);
    }

    public boolean isAutoHideFooter() {
        return autoHideFooter;
    }

    public void setAutoHideFooter(boolean autoHideFooter) {
        this.autoHideFooter = autoHideFooter;
    }

    private void updateAdapter(int state) {
        switch (state) {
            case RLRecyclerViewState.STATE_FIRST_SHOW:
            case RLRecyclerViewState.STATE_FIRST_REFRESHING:
                adapter.getLoadMoreModule().setEnableLoadMore(false);
                break;
            case RLRecyclerViewState.STATE_REFRESH_ERROR:
                adapter.getLoadMoreModule().setEnableLoadMore(false);
                break;
            case RLRecyclerViewState.STATE_PULL_REFRESHING:
                adapter.getLoadMoreModule().setEnableLoadMore(false);
                break;
            case RLRecyclerViewState.STATE_LOADING_MORE:

                break;
            case RLRecyclerViewState.STATE_REFRESH_SUCCESS:
                adapter.getLoadMoreModule().setEnableLoadMore(true);
                break;
            case RLRecyclerViewState.STATE_NO_MORE_DATA:
                adapter.getLoadMoreModule().loadMoreEnd(page == 0 && autoHideFooter);
                break;
            case RLRecyclerViewState.STATE_LOAD_MORE_COMPLETE:
                adapter.getLoadMoreModule().loadMoreComplete();
                break;
            case RLRecyclerViewState.STATE_LOAD_MORE_FAIL:
                adapter.getLoadMoreModule().loadMoreFail();
                break;
        }
    }


    public void loadData(int nextState) {
        switch (getCurrentState()) {
            case STATE_FIRST_SHOW:
            case STATE_LOAD_MORE_COMPLETE:
            case STATE_LOAD_MORE_FAIL:
            case STATE_REFRESH_SUCCESS:
            case STATE_REFRESH_ERROR: {
                switch (nextState) {
                    case STATE_FIRST_REFRESHING:
                    case STATE_PULL_REFRESHING:
                        page = 0;
                    default:
                        page++;
                        break;
                }
                updateAdapter(nextState);
                state.postValue(nextState);
                callback.loadData(page, this);
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


    @Override
    public void result(boolean result, String message, boolean isLast, int page) {
        if (result) {
            switch (getCurrentState()) {
                case STATE_FIRST_REFRESHING:
                case STATE_PULL_REFRESHING:
                    if (isLast) {
                        updateAdapter(STATE_NO_MORE_DATA);
                        state.postValue(STATE_NO_MORE_DATA);
                    } else {
                        updateAdapter(STATE_REFRESH_SUCCESS);
                        state.postValue(STATE_REFRESH_SUCCESS);
                    }
                    break;
                default:
                    if (isLast) {
                        updateAdapter(STATE_NO_MORE_DATA);
                        state.postValue(STATE_NO_MORE_DATA);
                    } else {
                        updateAdapter(STATE_LOAD_MORE_COMPLETE);
                        state.postValue(STATE_LOAD_MORE_COMPLETE);
                    }
                    break;
            }
        } else {
            switch (getCurrentState()) {
                case STATE_FIRST_REFRESHING:
                case STATE_PULL_REFRESHING:
                    updateAdapter(STATE_REFRESH_ERROR);
                    state.postValue(STATE_REFRESH_ERROR);
                    break;
                case STATE_LOADING_MORE:
                    updateAdapter(STATE_LOAD_MORE_FAIL);
                    state.postValue(STATE_LOAD_MORE_FAIL);
                    break;
            }

        }
    }


    public interface Callback {
        void loadData(int page, LoadDataResult loadDataResult);
    }

}
