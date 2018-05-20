package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.tests.AttentionVolumeTestFragment;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_RESULTS_ATTENTION_VOLUME;

/**
 * Created by Railag on 17.03.2017.
 */

public class AttentionVolumeTestPresenter extends BasePresenter<AttentionVolumeTestFragment> {


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

        restartableLatestCache(REQUEST_RESULTS_ATTENTION_VOLUME,
                () -> service.sendAttentionVolumeResults(userId, wins, fails, misses)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                AttentionVolumeTestFragment::onSuccess,
                AttentionVolumeTestFragment::onError);
    }

    public void save(long wins, long fails, long misses) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.wins = wins;
        this.fails = fails;
        this.misses = misses;

        start(REQUEST_RESULTS_ATTENTION_VOLUME);
    }
}