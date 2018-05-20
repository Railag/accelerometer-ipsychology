package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.ComplexMotorReactionTestFragment;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_COMPLEX_MOTOR_REACTION;

/**
 * Created by Railag on 19.04.2017.
 */

public class ComplexMotorReactionTestPresenter extends BasePresenter<ComplexMotorReactionTestFragment> {

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

        restartableLatestCache(REQUEST_RESULTS_COMPLEX_MOTOR_REACTION,
                () -> service.sendComplexMotorReactionResults(userId, wins, fails, misses)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                ComplexMotorReactionTestFragment::onSuccess,
                ComplexMotorReactionTestFragment::onError);
    }

    public void save(long wins, long fails, long misses) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.wins = wins;
        this.fails = fails;
        this.misses = misses;

        start(REQUEST_RESULTS_COMPLEX_MOTOR_REACTION);
    }
}
