package com.inri.sopsop.view;

import android.os.Bundle;
import android.view.View;

import com.inri.sopsop.R;
import com.inri.sopsop.view.base.SimpleFragment;

import butterknife.OnClick;

/**
 * Created by Railag on 21.03.2017.
 */

public class LandingFragment extends SimpleFragment {

    public static LandingFragment newInstance() {

        Bundle args = new Bundle();

        LandingFragment fragment = new LandingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.landingScreenTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_landing;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().hideToolbar();
    }

    @OnClick(R.id.infoButton)
    public void goToInfo() {
        getMainActivity().toInfo();
    }

    @OnClick(R.id.statisticsButton)
    public void toStatistics() {
        getMainActivity().toStatistics();
    }

    @OnClick(R.id.settingsButton)
    public void toSettings() {
        getMainActivity().toSettings();
    }

    @OnClick(R.id.infoButton2)
    public void info() {
        goToInfo();
    }

    @OnClick(R.id.statisticsButton2)
    public void stat() {
        toStatistics();
    }

    @OnClick(R.id.settingsButton2)
    public void settings() {
        toSettings();
    }

    @OnClick(R.id.distribution)
    public void toDistribution() {
        getMainActivity().toInstructionFragment(InstructionFragment.Test.ATTENTION_DISTRIBUTION);
    }

    @OnClick(R.id.complexMotorReaction)
    public void toComplexMotorReaction() {
        getMainActivity().toInstructionFragment(InstructionFragment.Test.COMPLEX_MOTOR_REACTION);
    }

    @OnClick(R.id.focusing)
    public void toFocusing() {
        getMainActivity().toInstructionFragment(InstructionFragment.Test.FOCUSING);
    }

    @OnClick(R.id.reaction)
    public void toReaction() {
        getMainActivity().toInstructionFragment(InstructionFragment.Test.REACTION);
    }
}
