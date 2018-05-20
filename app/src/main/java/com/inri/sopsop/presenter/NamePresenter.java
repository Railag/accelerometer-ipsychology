package com.inri.sopsop.presenter;

import com.inri.sopsop.App;
import com.inri.sopsop.model.User;
import com.inri.sopsop.view.NameFragment;

import icepick.State;

/**
 * Created by Railag on 07.11.2016.
 */

public class NamePresenter extends BasePresenter<NameFragment> {

    @State
    String name;

    @State
    String email;

    @State
    String password;

    public void save(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        User.get(App.getMainActivity()).setLogin(name);
        User.get(App.getMainActivity()).setEmail(email);
        User.get(App.getMainActivity()).setPassword(password);
    }
}