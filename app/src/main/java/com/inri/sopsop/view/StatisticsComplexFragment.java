package com.inri.sopsop.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.view.adapter.ComplexResultsAdapter;
import com.inri.sopsop.view.base.SimpleFragment;

import butterknife.BindView;

/**
 * Created by Railag on 16.05.2017.
 */

public class StatisticsComplexFragment extends SimpleFragment {

    private final static String KEY_RESULTS = "results";

    @BindView(R.id.complexResultsList)
    RecyclerView complexResultsList;

    @Override
    protected String getTitle() {
        return getString(R.string.complexMotorReactionTestTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_statistics_complex;
    }

    public static StatisticsComplexFragment newInstance(StatisticsResult results) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_RESULTS, results);

        StatisticsComplexFragment fragment = new StatisticsComplexFragment();
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

            LinearLayoutManager complexManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            complexResultsList.setLayoutManager(complexManager);

            ComplexResultsAdapter complexAdapter = new ComplexResultsAdapter();
            complexAdapter.setAllResults(results.complexResults);
            complexResultsList.setAdapter(complexAdapter);
        }
    }
}
