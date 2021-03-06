package com.inri.sopsop.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inri.sopsop.R;
import com.inri.sopsop.model.StatisticsResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Railag on 04.05.2017.
 */

public class ComplexResultsAdapter extends RecyclerView.Adapter<ComplexResultsAdapter.ViewHolder> {

    private List<StatisticsResult.ComplexResults> allResults = new ArrayList<>();

    public void setAllResults(List<StatisticsResult.ComplexResults> allResults) {
        this.allResults = allResults;
        notifyDataSetChanged();
    }

    @Override
    public ComplexResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_complex_result, parent, false);
        return new ComplexResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ComplexResultsAdapter.ViewHolder holder, int position) {
        StatisticsResult.ComplexResults results = allResults.get(position);

        holder.winsCount.setText(String.valueOf(results.wins));
        holder.errorsCount.setText(String.valueOf(results.fails));
        holder.missesCount.setText(String.valueOf(results.misses));
    }

    @Override
    public int getItemCount() {
        return allResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.winsCount)
        TextView winsCount;

        @BindView(R.id.errorsCount)
        TextView errorsCount;

        @BindView(R.id.missesCount)
        TextView missesCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

