package com.inri.sopsop.view.tests;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.inri.sopsop.App;
import com.inri.sopsop.BluetoothEventListener;
import com.inri.sopsop.R;
import com.inri.sopsop.model.Difficulty;
import com.inri.sopsop.model.Figure;
import com.inri.sopsop.model.Result;
import com.inri.sopsop.presenter.AttentionDistributionTestPresenter;
import com.inri.sopsop.view.base.BaseFragment;
import com.inri.sopsop.view.results.AttentionDistributionResultsFragment;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Created by Railag on 17.03.2017.
 */

@RequiresPresenter(AttentionDistributionTestPresenter.class)
public class AttentionDistributionTestFragment extends BaseFragment<AttentionDistributionTestPresenter> implements BluetoothEventListener {

    private final static int MAX_FIGURES = 100;

    private final static int BASE_TIME = 2100;

    private Handler handler;

    private int wins;
    private int fails;
    private int misses;

    @BindView(R.id.firstImage)
    ImageView firstImage;

    @BindView(R.id.secondImage)
    ImageView secondImage;

    Figure figure1;
    Figure figure2;

    private boolean active;

    private int currentFigure = 0;

    private Difficulty currentDiff;

    public static AttentionDistributionTestFragment newInstance() {

        Bundle args = new Bundle();

        AttentionDistributionTestFragment fragment = new AttentionDistributionTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.attentionDistributionTestTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_test_figures;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View v) {
        figure1 = Figure.random();
        figure2 = Figure.random();
        currentDiff = App.diff(getActivity());
        next();
    }

    private void next() {
        handler.postDelayed(() -> {
            if (getActivity() == null) {
                return;
            }

            if (active && figure1.equals(figure2)) {
                misses++;
            }

            currentFigure++;
            if (currentFigure >= MAX_FIGURES) {
                Toast.makeText(getActivity(), "Wins = " + wins + ", Fails = " + fails, Toast.LENGTH_SHORT).show();
                toNextTest();
                return;
            }

            Figure temp1 = null, temp2 = null;

            while (true) {
                temp1 = Figure.random();
                temp2 = Figure.random();

                if (!figure1.equals(temp1) && !figure2.equals(temp2)) {
                    break;
                }
            }

            figure1 = temp1;
            figure2 = temp2;

            Resources res = getResources();

            firstImage.setImageDrawable(res.getDrawable(figure1.getDrawableId()));
            secondImage.setImageDrawable(res.getDrawable(figure2.getDrawableId()));
            next();
            active = true;
            Log.i("DEBUG", currentFigure + " w:" + wins + " f:" + fails);
        }, BASE_TIME / currentDiff.getLevel());
    }

    @OnClick(R.id.button)
    public void click() {

        if (!active) {
            return;
        }

        if (currentFigure > MAX_FIGURES) {
            return;
        }

        if (figure1.equals(figure2)) {
            wins++;
        } else {
            fails++;
        }

        active = false;
    }

    private void toNextTest() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        startLoading();
        getPresenter().save(wins, fails, misses);
    }

    public void onSuccess(Result result) {
        stopLoading();

        if (result == null) {
            onError(new IllegalArgumentException());
            return;
        }
        if (result.invalid()) {
            toast(result.error);
            return;
        }


        Bundle args = new Bundle();
        args.putInt(AttentionDistributionResultsFragment.RESULTS, wins);
        getMainActivity().toAttentionDistributionResults(args);
    }

    public void onError(Throwable throwable) {
        stopLoading();
        throwable.printStackTrace();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getMainActivity() != null) {
            getMainActivity().registerBluetoothListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getMainActivity() != null) {
            getMainActivity().unregisterBluetoothListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onLeft() {

    }

    @Override
    public void onRight() {

    }

    @Override
    public void onTop() {
        click();
    }

    @Override
    public void onBottom() {

    }

    @Override
    public void onTopLeft() {

    }

    @Override
    public void onTopRight() {

    }

    @Override
    public void onBottomLeft() {

    }

    @Override
    public void onBottomRight() {

    }

    @Override
    public void onCenter() {

    }
}