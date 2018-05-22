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

public class AttentionVolumeTestPresenter extends BasePresenter<AttentionVolumeTestFragment> {

    @State
    long userId;

    @State
    Double time;

    @State
    long wins;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_RESULTS_ATTENTION_VOLUME,
                () -> service.sendAttentionVolumeResults(userId, time, wins)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                AttentionVolumeTestFragment::onSuccess,
                AttentionVolumeTestFragment::onError);
    }

    public void save(Double time, long wins) {
        this.userId = User.get(App.getMainActivity()).getId();
        this.time = time;
        this.wins = wins;

        start(REQUEST_RESULTS_ATTENTION_VOLUME);
    }
}

