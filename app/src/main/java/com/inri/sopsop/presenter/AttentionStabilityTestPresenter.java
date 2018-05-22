package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.AttentionStabilityTestFragment;

import java.util.ArrayList;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_ATTENTION_STABILITY;


public class AttentionStabilityTestPresenter extends BasePresenter<AttentionStabilityTestFragment> {


    @State
    long userId;

    @State
    ArrayList<Double> times;

    @State
    long errors;

    @State
    long misses;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_RESULTS_ATTENTION_STABILITY,
                () -> service.sendAttentionStabilityResults(userId, times, misses, errors)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                AttentionStabilityTestFragment::onSuccess,
                AttentionStabilityTestFragment::onError);
    }

    public void save(ArrayList<Double> times, long errors, long misses) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.times = times;
        this.errors = errors;
        this.misses = misses;

        start(REQUEST_RESULTS_ATTENTION_STABILITY);
    }
}
