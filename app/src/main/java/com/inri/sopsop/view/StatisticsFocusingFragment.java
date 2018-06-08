package com.inri.sopsop.view;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.view.adapter.ComplexResultsAdapter;
import com.inri.sopsop.view.adapter.FocusingResultsAdapter;
import com.inri.sopsop.view.base.SimpleFragment;

import butterknife.BindView;

public class StatisticsFocusingFragment extends SimpleFragment {

    private final static String KEY_RESULTS = "results";

    @BindView(R.id.focusingResultsList)
    RecyclerView focusingResultsList;

    @Override
    protected String getTitle() {
        return getString(R.string.focusingTestTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_statistics_focusing;
    }

    public static StatisticsFocusingFragment newInstance(StatisticsResult results) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_RESULTS, results);

        StatisticsFocusingFragment fragment = new StatisticsFocusingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        Bundle args = getArguments();
        if (args != null) {
            StatisticsResult results = (StatisticsResult) args.getSerializable(KEY_RESULTS);

            LinearLayoutManager focusingManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            focusingResultsList.setLayoutManager(focusingManager);

            FocusingResultsAdapter focusingAdapter = new FocusingResultsAdapter();
            focusingAdapter.setAllResults(results.focusingResults);
            focusingResultsList.setAdapter(focusingAdapter);
        }
    }
}