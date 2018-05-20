package com.inri.sopsop.view;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.inri.sopsop.R;
import com.inri.sopsop.presenter.NamePresenter;
import com.inri.sopsop.view.base.BaseFragment;

import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Created by Railag on 07.11.2016.
 */
@RequiresPresenter(NamePresenter.class)
public class NameFragment extends BaseFragment<NamePresenter> {

    @BindView(R.id.emailField)
    EditText nameField;

    @BindView(R.id.passwordField)
    EditText passwordField;

    public static NameFragment newInstance() {

        Bundle args = new Bundle();

        NameFragment fragment = new NameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.nameTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_name;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        passwordField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                getPresenter().save(nameField.getText().toString(), nameField.getText().toString(), passwordField.getText().toString());
                getMainActivity().toAgeScreen();

                return true;
            } else {
                return false;
            }
        });
    }
}
