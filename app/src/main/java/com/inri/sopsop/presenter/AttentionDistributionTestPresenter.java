package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.AttentionDistributionTestFragment;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_ATTENTION_DISTRIBUTION;


public class AttentionDistributionTestPresenter extends BasePresenter<AttentionDistributionTestFragment> {


    @State
    long userId;

    @State
    long wins;

    @State
    long fails;

    @State
    long misses;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_RESULTS_ATTENTION_DISTRIBUTION,
                () -> service.sendAttentionDistributionResults(userId, wins, fails, misses)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                AttentionDistributionTestFragment::onSuccess,
                AttentionDistributionTestFragment::onError);
    }

    public void save(long wins, long fails, long misses) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.wins = wins;
        this.fails = fails;
        this.misses = misses;

        start(REQUEST_RESULTS_ATTENTION_DISTRIBUTION);
    }
}