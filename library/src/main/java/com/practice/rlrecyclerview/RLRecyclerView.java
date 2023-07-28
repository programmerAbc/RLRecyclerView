package com.practice.rlrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


public class RLRecyclerView extends FrameLayout {
    public static final int HEADER_STYLE_MATERIAL = 0;
    public static final int HEADER_STYLE_CUSTOM = 1;
    public static final int HEADER_STYLE_IOS = 2;
    Handler mainHandler;
    LinearLayoutManager linearLayoutManager;
    View emptyView = null;
    View errorLayout;
    View loadingLayout;
    RLRecyclerViewState rlrvState;
    SmartRefreshLayout refreshLayout;
    RecyclerView rv;


    public RLRecyclerView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public RLRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RLRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rlrv_rlrecyclerview, this, true);
        refreshLayout = findViewById(R.id.rlrv_refreshLayout);
        rv = findViewById(R.id.rlrv_recyclerView);
        mainHandler = new Handler(Looper.getMainLooper());
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableOverScrollDrag(false);
        refreshLayout.setEnableOverScrollBounce(false);
    }


    public void backToTop(boolean animated) {
        if (animated) {
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition <= 5) {
                rv.smoothScrollToPosition(0);
            } else {
                rv.scrollToPosition(5);
                rv.smoothScrollToPosition(0);
            }
        } else {
            rv.scrollToPosition(0);
        }
    }

    public RecyclerView getRecyclerView() {
        return rv;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public View getErrorLayout() {
        return errorLayout;
    }

    public View getLoadingLayout() {
        return loadingLayout;
    }

    private void bind(AppCompatActivity activity, Fragment fragment, RLRecyclerViewState state) {
        this.rlrvState = state;
        refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
        refreshLayout.setBackgroundResource(rlrvState.getBackgroundResId());
        switch (rlrvState.getHeaderStyle()) {
            case HEADER_STYLE_IOS:
                refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
                refreshLayout.setEnableHeaderTranslationContent(true);
                break;
            case HEADER_STYLE_CUSTOM:
                refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
                refreshLayout.setEnableHeaderTranslationContent(false);
                break;
            default:
                refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
                refreshLayout.setEnableHeaderTranslationContent(false);
                break;
        }

        errorLayout = LayoutInflater.from(getContext()).inflate(rlrvState.getErrorLayoutResId(), this, false);
        errorLayout.setVisibility(INVISIBLE);
        addView(errorLayout);
        errorLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rlrvState.firstRefresh();
            }
        });
        loadingLayout = LayoutInflater.from(getContext()).inflate(rlrvState.getLoadingLayoutResId(), this, false);
        loadingLayout.setVisibility(VISIBLE);
        addView(loadingLayout);
        emptyView = LayoutInflater.from(getContext()).inflate(rlrvState.getEmptyLayoutResId(), rv, false);
        state.adapter.setEmptyView(emptyView);

        rv.setAdapter(state.adapter);
        refreshLayout.setOnRefreshListener(
                new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshLayout) {
                        state.pullRefresh();
                    }
                }
        );

        state.adapter.getLoadMoreModule().setOnLoadMoreListener(
                new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        state.loadMore();
                    }
                }
        );

        if (activity != null) {
            state.state.observe(activity, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (integer != null) {
                        handleStateChanged(integer);
                    }
                }
            });
        } else {
            state.state.observe(fragment, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (integer != null) {
                        handleStateChanged(integer);
                    }
                }
            });
        }
    }

    public void bind(AppCompatActivity activity, RLRecyclerViewState state) {
        bind(activity, null, state);
    }

    public void bind(Fragment fragment, RLRecyclerViewState state) {
        bind(null, fragment, state);
    }


    public void unbind() {
        rv.setAdapter(null);
    }

    private void handleStateChanged(int value) {
        switch (value) {
            case RLRecyclerViewState.STATE_FIRST_SHOW:
            case RLRecyclerViewState.STATE_FIRST_REFRESHING:
                showLoadingLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            case RLRecyclerViewState.STATE_REFRESH_ERROR:
                showErrorLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            case RLRecyclerViewState.STATE_PULL_REFRESHING:
                hideCoverLayout();
                if (!refreshLayout.isRefreshing()) {
                    refreshLayout.autoRefreshAnimationOnly();
                }
                break;
            case RLRecyclerViewState.STATE_LOADING_MORE:
                hideCoverLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(false);
                break;
            case RLRecyclerViewState.STATE_REFRESH_SUCCESS:
                hideCoverLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            case RLRecyclerViewState.STATE_NO_MORE_DATA:
                hideCoverLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            case RLRecyclerViewState.STATE_LOAD_MORE_COMPLETE:
                hideCoverLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            case RLRecyclerViewState.STATE_LOAD_MORE_FAIL:
                hideCoverLayout();
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(!rlrvState.isDisableManualRefresh());
                break;
            default:
                break;
        }

    }


    public void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
    }

    public void showLoadingLayout() {
        errorLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    public void hideCoverLayout() {
        errorLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
    }
}
