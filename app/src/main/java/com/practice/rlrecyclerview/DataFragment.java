package com.practice.rlrecyclerview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.practice.rlrecyclerview.databinding.DataFragmentBinding;
import com.programmerAbc.LoadDataResult;
import com.programmerAbc.RLRecyclerView;
import com.programmerAbc.RLRecyclerViewState;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {
    Handler mainHandler;
    DataFragmentBinding bd;
    RLRecyclerViewState<String> rlrvstate;
    String data = "";
    DataAdapter adapter;

    public DataFragment() {
        // Required empty public constructor
        mainHandler = new Handler(Looper.getMainLooper());
        rlrvstate = new RLRecyclerViewState<>(new RLRecyclerViewState.Callback<String>() {
            @Override
            public void loadData(RLRecyclerViewState<String> instance, int page, LoadDataResult<String> loadDataResult) {
                new Thread() {
                    List<String> resp = new ArrayList<>();

                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 10; ++i) {
                                resp.add(data + (i + page * 10));
                            }
                            Thread.sleep(100);
                        } catch (Exception e) {

                        }
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadDataResult.result(true, "", page > 5, resp);
                            }
                        });
                    }
                }.start();


            }
        });
        rlrvstate.setHeaderStyle(RLRecyclerView.HEADER_STYLE_MATERIAL);
        rlrvstate.setAutoHideFooter(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        data = DataFragmentArgs.fromBundle(getArguments()).getData();
        bd = DataFragmentBinding.inflate(inflater, container, false);
        return bd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new DataAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                NavDirections directions = NavGraphDirections.actionGlobalDataFragment().setData(adapter.getItem(position));
                Navigation.findNavController(DataFragment.this.getView()).navigate(directions);
            }
        });
        bd.rlrv.bind(getViewLifecycleOwner(), adapter, new LinearLayoutManager(getContext()), rlrvstate);
        rlrvstate.firstRefreshWhenFirstShow();
    }

    @Override
    public void onDestroyView() {
        bd.rlrv.unbind();
        super.onDestroyView();
        adapter = null;
        bd = null;
    }
}