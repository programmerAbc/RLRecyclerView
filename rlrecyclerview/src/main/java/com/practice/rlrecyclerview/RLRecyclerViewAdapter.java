package com.practice.rlrecyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

public abstract class RLRecyclerViewAdapter<T, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> implements LoadMoreModule {
    public RLRecyclerViewAdapter(int layoutResId) {
        super(layoutResId);
    }
    public RLRecyclerViewAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    @Override
    public BaseLoadMoreModule addLoadMoreModule(BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
