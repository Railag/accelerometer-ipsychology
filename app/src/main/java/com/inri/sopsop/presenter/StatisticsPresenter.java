package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.view.StatisticsFragment;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.inri.sopsop.Requests.REQUEST_STATISTICS;

/**
 * Created by Railag on 03.05.2017.
 */

public class StatisticsPresenter extends BasePresenter<StatisticsFragment> {

    @State
    long userId;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        restartableLatestCache(REQUEST_STATISTICS,
                () -> service.fetchStatistics(userId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                StatisticsFragment::onSuccess,
                StatisticsFragment::onError);
    }

    public void fetch(long userId) {
        this.userId = userId;

        start(REQUEST_STATISTICS);
    }
}
