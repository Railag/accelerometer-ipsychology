package com.inri.sopsop.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.inri.sopsop.App;
import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.presenter.MainPresenter;
import com.inri.sopsop.view.results.AttentionVolumeResultsFragment;
import com.inri.sopsop.view.results.ComplexMotorReactionResultsFragment;
import com.inri.sopsop.view.results.ReactionResultsFragment;
import com.inri.sopsop.view.results.ResultScreen;
import com.inri.sopsop.view.tests.AttentionVolumeTestFragment;
import com.inri.sopsop.view.tests.ComplexMotorReactionTestFragment;
import com.inri.sopsop.view.tests.ReactionTestFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> {

    private static final String TAG_MAIN = "mainTag";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.loading)
    AVLoadingIndicatorView loading;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    private FirebaseAnalytics analytics;

    private Fragment currentFragment;

    @Override
    public void setTitle(CharSequence title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    public void setStatusBarColor(int color) {
        Window window = getWindow();

        window.setStatusBarColor(getResources().getColor(color));
    }

    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void toggleArrow(boolean visible) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(visible);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        App.setMainActivity(this);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        analytics = FirebaseAnalytics.getInstance(this);

        toSplash();

        hideToolbar();

        checkForUpdates();
    }


    @Override
    public void onBackPressed() {

        if (currentFragment instanceof ResultScreen) {
            toLanding();
            return;
        }

        if (currentFragment instanceof LandingFragment) {
            finish();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        loading.setVisibility(View.GONE);
    }

    private <T extends Fragment> void setFragment(final T fragment) {
        runOnUiThread(() -> {
            stopLoading();

            currentFragment = fragment;

            final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            // TODO custom transaction animations
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            fragmentTransaction.replace(R.id.mainFragment, fragment, TAG_MAIN);
            fragmentTransaction.commitAllowingStateLoss();

        });
    }

    public void toSplash() {
        setFragment(SplashFragment.newInstance());
    }

    public void toLogin() {
        setFragment(LoginFragment.newInstance());
    }

    public void toNameScreen() {
        setFragment(NameFragment.newInstance());
    }

    public void toAgeScreen() {
        setFragment(AgeFragment.newInstance());
    }

    public void toTimeScreen() {
        setFragment(TimeFragment.newInstance());
    }

    public void toLanding() {
        setFragment(LandingFragment.newInstance());
    }

    public void toInfo() {
        setFragment(InfoFragment.newInstance());
    }

    public void toStatistics() {
        setFragment(StatisticsFragment.newInstance());
    }


    public void toSettings() {
        setFragment(SettingsFragment.newInstance());
    }

    public void toInstructionFragment(InstructionFragment.Test test) {
        setFragment(InstructionFragment.newInstance(test));
    }

    public void toAttentionVolumeTest() {
        setFragment(AttentionVolumeTestFragment.newInstance());
    }

    public void toReactionTest() {
        setFragment(ReactionTestFragment.newInstance());
    }

    public void toStart() {
        setFragment(StartFragment.newInstance());
    }

    public void toComplexMotorReactionTest() {
        setFragment(ComplexMotorReactionTestFragment.newInstance());
    }

    public void toReactionResults(Bundle args) {
        setFragment(ReactionResultsFragment.newInstance(args));
    }

    public void toAttentionVolumeResults(Bundle args) {
        setFragment(AttentionVolumeResultsFragment.newInstance(args));
    }

    public void toComplexMotorReactionResults(Bundle args) {
        setFragment(ComplexMotorReactionResultsFragment.newInstance(args));
    }

    public void toStatisticsReaction(StatisticsResult results) {
        setFragment(StatisticsReactionFragment.newInstance(results));
    }

    public void toStatisticsComplex(StatisticsResult results) {
        setFragment(StatisticsComplexFragment.newInstance(results));
    }

    public void toStatisticsDistribution(StatisticsResult results) {
        setFragment(StatisticsDistributionFragment.newInstance(results));
    }
}