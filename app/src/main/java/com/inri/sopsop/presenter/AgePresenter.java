package com.inri.sopsop.presenter;

import android.os.Bundle;

import com.inri.sopsop.App;
import com.inri.sopsop.RConnectorService;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.AgeFragment;

import icepick.State;

/**
 * Created by Railag on 07.11.2016.
 */

public class AgePresenter extends BasePresenter<AgeFragment> {

    @State
    int age;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        RConnectorService service = App.restService();

        /*restartableLatestCache(REQUEST_LOGIN,
                () -> service.login(login, password)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()),
                LoginFragment::onSuccess,
                LoginFragment::onError);*/
    }

    public void save(String age) {
        try {
            int ageNumber = Integer.valueOf(age);
            this.age = ageNumber;
            User.get(App.getMainActivity()).setAge(ageNumber);
            App.getMainActivity().toTimeScreen();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //    start(REQUEST_LOGIN);
    }
}

