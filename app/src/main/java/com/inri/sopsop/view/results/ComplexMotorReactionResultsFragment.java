package com.inri.sopsop.view.results;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.inri.sopsop.R;
import com.inri.sopsop.Utils;
import com.inri.sopsop.view.base.SimpleFragment;

import butterknife.BindView;

/**
 * Created by Railag on 19.04.2017.
 */

public class ComplexMotorReactionResultsFragment extends SimpleFragment implements ResultScreen {

    public final static String WINS = "wins";
    public final static String FAILS = "fails";

    public static ComplexMotorReactionResultsFragment newInstance(Bundle args) {

        ComplexMotorReactionResultsFragment fragment = new ComplexMotorReactionResultsFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.winsNumber)
    TextView winsNumber;

    @BindView(R.id.failsNumber)
    TextView failsNumber;

    @Override
    protected String getTitle() {
        return getString(R.string.resultsTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.results, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
            default:
                Utils.savePdf(getActivity(), getView(), "complex_motor_reaction_results", Utils.PdfFormat.A4Landscape);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getViewId() {
        return R.layout.results_complex_motor_reaction_layout;
    }

    @Override
    protected void initView(View v) {
        Bundle args = getArguments();

        Utils.verifyStoragePermissions(getActivity());

        if (args != null) {
            if (args.containsKey(WINS)) {
                int wins = args.getInt(WINS);

                winsNumber.setText(String.valueOf(wins));
            }

            if (args.containsKey(FAILS)) {
                int fails = args.getInt(FAILS);

                failsNumber.setText(String.valueOf(fails));
            }

        }
    }
}
