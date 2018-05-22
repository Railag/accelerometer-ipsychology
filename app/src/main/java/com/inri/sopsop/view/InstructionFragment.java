package com.inri.sopsop.view;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.inri.sopsop.R;
import com.inri.sopsop.view.base.SimpleFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Railag on 20.03.2017.
 */

public class InstructionFragment extends SimpleFragment {

    private final static String TYPE = "type";

    public enum Test {
        FOCUSING,
        ATTENTION_DISTRIBUTION,
        ATTENTION_STABILITY
    }

    public static InstructionFragment newInstance(Test test) {

        Bundle args = new Bundle();
        args.putSerializable(TYPE, test);

        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.instructionText)
    TextView instructionText;

    private Test test = Test.FOCUSING;

    @Override
    protected String getTitle() {
        return getString(R.string.instruction);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_instruction;
    }

    @Override
    protected void initView(View v) {
        getMainActivity().showToolbar();
        getMainActivity().toggleArrow(true);

        Bundle args = getArguments();
        if (args != null && args.containsKey(TYPE)) {
            test = (Test) args.getSerializable(TYPE);

            String instruction = "";

            switch (test) {
                case FOCUSING:
                    instruction = getString(R.string.instruction_reaction);
                    break;
                case ATTENTION_DISTRIBUTION:
                    instruction = getString(R.string.instruction_attention_volume);
                    break;
                case ATTENTION_STABILITY:
                    instruction = getString(R.string.instruction_complex_motor_reaction);
                    break;
            }

            instructionText.setText(Html.fromHtml(instruction));
        }
    }

    @OnClick(R.id.start)
    public void start() {
        switch (test) {
            case FOCUSING:
                getMainActivity().toFocusingTest();
                break;
            case ATTENTION_DISTRIBUTION:
                getMainActivity().toAttentionVolumeTest();
                break;
            case ATTENTION_STABILITY:
                getMainActivity().toAttentionStabilityTest();
                break;
        }
    }
}
