package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.FocusingTestFragment;

import java.util.ArrayList;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_FOCUSING;


public class FocusingTestPresenter extends BasePresenter<FocusingTestFragment> {


    @State
    long userId;

    @State
    ArrayList<Double> times;

    @State
    ArrayList<Long> errors;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_RESULTS_FOCUSING,
                () -> service.sendFocusingResults(userId, times, errors)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                FocusingTestFragment::onSuccess,
                FocusingTestFragment::onError);
    }

    public void save(ArrayList<Double> times, ArrayList<Long> errors) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.times = times;
        this.errors = errors;

        start(REQUEST_RESULTS_FOCUSING);
    }
}
