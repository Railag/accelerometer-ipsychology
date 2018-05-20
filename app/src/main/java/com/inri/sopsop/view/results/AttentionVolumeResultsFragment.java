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
 * Created by Railag on 20.03.2017.
 */

public class AttentionVolumeResultsFragment extends SimpleFragment implements ResultScreen {

    public final static String RESULTS = "results";

    public static AttentionVolumeResultsFragment newInstance(Bundle args) {

        AttentionVolumeResultsFragment fragment = new AttentionVolumeResultsFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.winsNumber)
    TextView winsNumber;

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
                Utils.savePdf(getActivity(), getView(), "attention_volume_results", Utils.PdfFormat.A4Landscape);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getViewId() {
        return R.layout.results_attention_volume_layout;
    }

    @Override
    protected void initView(View v) {
        Bundle args = getArguments();

        Utils.verifyStoragePermissions(getActivity());

        if (args != null) {
            if (args.containsKey(RESULTS)) {
                int wins = args.getInt(RESULTS);

                winsNumber.setText(String.valueOf(wins));
            }

        }
    }
}
