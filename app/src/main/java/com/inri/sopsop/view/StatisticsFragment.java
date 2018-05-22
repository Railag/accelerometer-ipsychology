package com.inri.sopsop.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.model.User;
import com.inri.sopsop.presenter.StatisticsPresenter;
import com.inri.sopsop.view.base.BaseFragment;

import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Created by Railag on 03.05.2017.
 */
@RequiresPresenter(StatisticsPresenter.class)
public class StatisticsFragment extends BaseFragment<StatisticsPresenter> {

    @BindView(R.id.focusing)
    Button reaction;
    @BindView(R.id.distribution)
    Button distribution;
    @BindView(R.id.stability)
    Button complex;

    private StatisticsResult results;

    public static StatisticsFragment newInstance() {

        Bundle args = new Bundle();

        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.statistics);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_statistics;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        startLoading();

        fetchData();
    }

    private void fetchData() {
        User user = User.get(getActivity());
        getPresenter().fetch(user.getId());
    }

    private StatisticsResult sortResults(StatisticsResult result) {
        Collections.sort(result.volumeResults);
        Collections.sort(result.complexResults);
        Collections.sort(result.reactionResults);
        return result;
    }

    public void onSuccess(StatisticsResult result) {
        stopLoading();

        if (result == null) {
            onError(new IllegalArgumentException());
            return;
        }
        if (result.invalid()) {
            toast(result.error);
            return;
        }

        results = sortResults(result);

        complex.setEnabled(true);
        reaction.setEnabled(true);
        distribution.setEnabled(true);
    }

    public void onError(Throwable throwable) {
        stopLoading();
        throwable.printStackTrace();
    }

    @OnClick({R.id.focusing, R.id.distribution, R.id.stability})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.focusing:
                getMainActivity().toStatisticsReaction(results);
                break;
            case R.id.distribution:
                getMainActivity().toStatisticsDistribution(results);
                break;
            case R.id.stability:
                getMainActivity().toStatisticsComplex(results);
                break;
        }
    }
}