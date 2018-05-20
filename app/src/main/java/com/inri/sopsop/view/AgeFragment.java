package com.inri.sopsop.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.inri.sopsop.R;
import com.inri.sopsop.presenter.AgePresenter;
import com.inri.sopsop.view.base.BaseFragment;

import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Created by Railag on 07.11.2016.
 */
@RequiresPresenter(AgePresenter.class)
public class AgeFragment extends BaseFragment<AgePresenter> {

    @BindView(R.id.ageField)
    EditText ageField;

    public static AgeFragment newInstance() {

        Bundle args = new Bundle();

        AgeFragment fragment = new AgeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.ageTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_age;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        ageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    getPresenter().save(ageField.getText().toString());
                    getMainActivity().toTimeScreen();

                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
