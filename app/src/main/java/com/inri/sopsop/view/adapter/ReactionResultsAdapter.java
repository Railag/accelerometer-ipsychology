package com.inri.sopsop.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Railag on 04.05.2017.
 */

public class ReactionResultsAdapter extends RecyclerView.Adapter<ReactionResultsAdapter.ViewHolder> {

    private List<StatisticsResult.ReactionResults> allResults = new ArrayList<>();

    public void setAllResults(List<StatisticsResult.ReactionResults> allResults) {
        this.allResults = allResults;
        notifyDataSetChanged();
    }

    @Override
    public ReactionResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_reaction_result, parent, false);
        return new ReactionResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReactionResultsAdapter.ViewHolder holder, int position) {
        StatisticsResult.ReactionResults results = allResults.get(position);

        List<Entry> lineEntries = new ArrayList<>();

        for (int i = 0; i < results.times.size(); i++) {
            lineEntries.add(new Entry(i, results.times.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(lineEntries, "Время");
        dataSet.setColor(R.color.purple);

        LineData lineData = new LineData(dataSet);
        holder.chart1.setData(lineData);
        holder.chart1.invalidate();

        holder.chart1.animateX(2000);

        Description description = holder.chart1.getDescription();
        description.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return allResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chart1)
        LineChart chart1;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
