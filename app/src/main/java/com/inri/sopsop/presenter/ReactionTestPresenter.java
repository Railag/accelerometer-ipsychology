package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.ReactionTestFragment;

import java.util.ArrayList;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_REACTION;

/**
 * Created by Railag on 07.11.2016.
 */

public class ReactionTestPresenter extends BasePresenter<ReactionTestFragment> {

    @State
    long userId;

    @State
    ArrayList<Double> times;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_RESULTS_REACTION,
                () -> service.sendReactionResults(userId, times)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                ReactionTestFragment::onSuccess,
                ReactionTestFragment::onError);
    }

    public void save(ArrayList<Double> times) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.times = times;

        start(REQUEST_RESULTS_REACTION);
    }
}
