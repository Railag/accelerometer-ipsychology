package com.inri.sopsop.view.results;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.inri.sopsop.R;
import com.inri.sopsop.Utils;
import com.inri.sopsop.view.base.SimpleFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class ReactionResultsFragment extends SimpleFragment implements ResultScreen {

    public final static String RESULTS = "results";

    public static ReactionResultsFragment newInstance(Bundle args) {

        ReactionResultsFragment fragment = new ReactionResultsFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.averageReact)
    TextView averageReact;

    @BindView(R.id.minReact)
    TextView minReact;

    @BindView(R.id.maxReact)
    TextView maxReact;

    @BindView(R.id.chart)
    LineChart chart;

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
                Utils.savePdf(getActivity(), getView(), "reaction_results", Utils.PdfFormat.A2Portrait);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getViewId() {
        return R.layout.results_reaction_layout;
    }

    @Override
    protected void initView(View v) {
        Bundle args = getArguments();

        Utils.verifyStoragePermissions(getActivity());

        if (args != null) {
            if (args.containsKey(RESULTS)) {
                ArrayList<Double> results = (ArrayList<Double>) args.getSerializable(RESULTS);


                double average = 0;
                for (int i = 0; i < results.size(); i++) {
                    average += results.get(i);
                }

                average /= results.size();

                averageReact.setText(String.valueOf(average));

                double min = Collections.min(results);
                double max = Collections.max(results);

                minReact.setText(String.valueOf(min));
                maxReact.setText(String.valueOf(max));

                List<Entry> lineEntries = new ArrayList<>();

                for (int i = 0; i < results.size(); i++) {
                    lineEntries.add(new Entry(i, results.get(i).floatValue()));
                }

                LineDataSet dataSet = new LineDataSet(lineEntries, "Время");
                dataSet.setColor(R.color.purple);

                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

                chart.animateX(2000);

                Description description = chart.getDescription();
                description.setEnabled(false);

            }

        }
    }
}
