package com.inri.sopsop.view;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.inri.sopsop.R;
import com.inri.sopsop.model.User;
import com.inri.sopsop.model.UserResult;
import com.inri.sopsop.presenter.TimePresenter;
import com.inri.sopsop.view.base.BaseFragment;

import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Created by Railag on 07.11.2016.
 */
@RequiresPresenter(TimePresenter.class)
public class TimeFragment extends BaseFragment<TimePresenter> {

    @BindView(R.id.timeField)
    EditText timeField;

    public static TimeFragment newInstance() {

        Bundle args = new Bundle();

        TimeFragment fragment = new TimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.timeTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_time;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        timeField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                getPresenter().save(timeField.getText().toString());

                startLoading();
                User user = User.get(getActivity());
                getPresenter().request(user.getLogin(), user.getPassword(), user.getEmail(), user.getAge(), user.getTime());

                return true;
            } else {
                return false;
            }
        });
    }

    public void onSuccess(UserResult result) {
        stopLoading();
        if (result == null) {
            onError(new IllegalArgumentException());
            return;
        }
        if (result.invalid()) {
            toast(result.error);
            return;
        }
        User.save(result, getActivity());
        getMainActivity().toBluetoothFragment();
    }

    public void onError(Throwable error) {
        error.printStackTrace();
        stopLoading();
        toast(error.getMessage());
    }
}
