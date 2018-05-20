package com.inri.sopsop.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Railag on 04.05.2017.
 */

public class StatisticsResult extends Result implements Serializable {

    @SerializedName("reaction_results")
    public List<ReactionResults> reactionResults;
    @SerializedName("complex_results")
    public List<ComplexResults> complexResults;
    @SerializedName("volume_results")
    public List<VolumeResults> volumeResults;
    @SerializedName("focusing_results")
    public List<FocusingResults> focusingResults;
    @SerializedName("stability_results")
    public List<StabilityResults> stabilityResults;
    @SerializedName("stress_results")
    public List<StressResults> stressResults;

    public static class ReactionResults implements Comparable<ReactionResults>, Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("times")
        public List<Double> times;

        @Override
        public int compareTo(@NonNull ReactionResults reactionResults) {
            return Integer.valueOf(id).compareTo(reactionResults.id);
        }
    }

    public static class ComplexResults implements Comparable<ComplexResults>, Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("wins")
        public int wins;
        @SerializedName("fails")
        public int fails;
        @SerializedName("misses")
        public int misses;

        @Override
        public int compareTo(@NonNull ComplexResults complexResults) {
            return Integer.valueOf(id).compareTo(complexResults.id);
        }
    }

    public static class VolumeResults implements Comparable<VolumeResults>, Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("wins")
        public int wins;
        @SerializedName("fails")
        public int fails;
        @SerializedName("misses")
        public int misses;

        @Override
        public int compareTo(@NonNull VolumeResults volumeResults) {
            return Integer.valueOf(id).compareTo(volumeResults.id);
        }
    }

    public static class FocusingResults implements Comparable<FocusingResults> {
        @SerializedName("id")
        public int id;
        @SerializedName("times")
        public List<Double> times;
        @SerializedName("error_values")
        public List<Integer> errorValues;

        @Override
        public int compareTo(@NonNull FocusingResults focusingResults) {
            return Integer.valueOf(id).compareTo(focusingResults.id);
        }
    }

    public static class StabilityResults implements Comparable<StabilityResults> {
        @SerializedName("id")
        public int id;
        @SerializedName("times")
        public List<Double> times;
        @SerializedName("errors_value")
        public int errorsValue;
        @SerializedName("misses")
        public int misses;

        @Override
        public int compareTo(@NonNull StabilityResults stabilityResults) {
            return Integer.valueOf(id).compareTo(stabilityResults.id);
        }
    }

    public static class StressResults implements Comparable<StressResults> {
        @SerializedName("id")
        public int id;
        @SerializedName("misses")
        public int misses;
        @SerializedName("times")
        public List<Double> times;

        @Override
        public int compareTo(@NonNull StressResults stressResults) {
            return Integer.valueOf(id).compareTo(stressResults.id);
        }
    }
}
