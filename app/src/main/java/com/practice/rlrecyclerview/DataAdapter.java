package com.practice.rlrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.practice.rlrecyclerview.databinding.DataItemBinding;
import com.programmerAbc.RLRecyclerViewAdapter;

import java.util.List;

public class DataAdapter extends RLRecyclerViewAdapter<String, BaseViewHolder> {
    public DataAdapter(List<String> data) {
        super(0, data);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
        DataItemBinding bd = DataItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(bd);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        ((VH) baseViewHolder).bindData(s);
    }


    public class VH extends BaseViewHolder {
        DataItemBinding bd;

        public VH(@NonNull DataItemBinding bd) {
            super(bd.getRoot());
            this.bd = bd;
        }

        public void bindData(String data) {
            bd.titleTv.setText(data);
        }
    }
}
