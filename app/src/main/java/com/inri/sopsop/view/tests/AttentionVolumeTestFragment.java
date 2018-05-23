package com.inri.sopsop.view.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.inri.sopsop.BluetoothEventListener;
import com.inri.sopsop.R;
import com.inri.sopsop.Utils;
import com.inri.sopsop.model.Answer;
import com.inri.sopsop.model.Result;
import com.inri.sopsop.model.Sign;
import com.inri.sopsop.presenter.AttentionVolumeTestPresenter;
import com.inri.sopsop.view.adapter.SignsAdapter;
import com.inri.sopsop.view.base.BaseFragment;
import com.inri.sopsop.view.results.AttentionVolumeResultsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.BindViews;
import nucleus.factory.RequiresPresenter;


@RequiresPresenter(AttentionVolumeTestPresenter.class)
public class AttentionVolumeTestFragment extends BaseFragment<AttentionVolumeTestPresenter> implements BluetoothEventListener {

    private final static int[] backgroundIds = {R.drawable.background, R.drawable.background, R.drawable.background, R.drawable.background, R.drawable.background,
            R.drawable.background, R.drawable.background, R.drawable.background, R.drawable.background, R.drawable.background};

    private final static int MAX_BACKGROUNDS = 1;

    private final static int SIGNS_TYPES = 12;

    private final static int SIGNS_PER_LINE = 5;

    @BindViews({R.id.sign1, R.id.sign2, R.id.sign3, R.id.sign4, R.id.sign5, R.id.sign6, R.id.sign7, R.id.sign8, R.id.sign9, R.id.sign10, R.id.sign11, R.id.sign12})
    ImageView[] signImages;

    @BindView(R.id.attentionBackground)
    RelativeLayout attentionBackground;

    @BindView(R.id.signsGrid)
    RecyclerView signsGrid;

    List<Sign> signsCounter;

    int currentBackground = 0;

    private int previousSelection;
    private int currentSignSelection;

    Random random = new Random();

    private Handler handler;

    private long time;
    private ArrayList<Answer> answers = new ArrayList<>();

    private SignsAdapter signsAdapter;

    private double resultTime;
    private long winsCount;
    private SignsAdapter.OnSignClickListener listener;

    public static AttentionVolumeTestFragment newInstance() {

        Bundle args = new Bundle();

        AttentionVolumeTestFragment fragment = new AttentionVolumeTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.attentionVolumeTestTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_test_attention_volume;
    }

    @Override
    protected void initView(View v) {
        handler = new Handler();

        getMainActivity().registerBluetoothListener(this);

        signsCounter = Arrays.asList(Sign.values());

        next();
    }

    private void next() {
        int startTime = 10000; // 10 seconds

        resetSigns();

        setupBackground();
        setupSigns();

        handler.postDelayed(() -> {
            double result = Utils.calcTime(time);
            Answer answer = new Answer();
            answer.setTime(result);

            answer.setErrorValue(1); // error

            answer.setNumber(answers.size());

            answers.add(answer);

            time = System.nanoTime();

            nextBackground();

        }, startTime);
    }

    private void resetSigns() {
        if (signImages != null && signImages.length > 0) {
            for (ImageView image : signImages) {
                image.setImageResource(0);
            }
        }
    }

    private void setupSigns() {
        List<Sign> signs = Sign.randomSigns(SIGNS_TYPES);

        for (Sign sign : signs) {
            while (true) {
                int position = random.nextInt(SIGNS_TYPES);
                if (signImages[position].getDrawable() == null) {
                    signImages[position].setImageResource(sign.getDrawableId());
                    break;
                }
            }

            signsCounter.get(signsCounter.indexOf(sign)).setShown(true);
        }
    }

    private void setupBackground() {
        int backgroundNumber = random.nextInt(backgroundIds.length);
        attentionBackground.setBackgroundResource(backgroundIds[backgroundNumber]);
    }

    private void nextBackground() {
        currentBackground++;

        if (currentBackground >= MAX_BACKGROUNDS) {
            toFinalSelection();
        } else {
            next();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if (getMainActivity() != null) {
            getMainActivity().unregisterBluetoothListener(this);
        }
    }

    private void toFinalSelection() {
        for (ImageView sign : signImages) {
            sign.setVisibility(View.GONE);
        }

        time = System.nanoTime();

        signsCounter.get(0).setSelected(true);

        listener = sign -> {
            sign.setChosen(!sign.isChosen());

            int chosenCounter = 0;
            for (Sign s : signsCounter) {
                if (s.isChosen()) {
                    chosenCounter++;
                }
            }

            if (chosenCounter >= SIGNS_TYPES) {
                toResults();
            } else {
                signsAdapter.notifyDataSetChanged();
            }
        };

        signsAdapter = new SignsAdapter();
        signsAdapter.setSigns(signsCounter, listener);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), SIGNS_PER_LINE);
        signsGrid.setLayoutManager(manager);

        signsGrid.setAdapter(signsAdapter);
    }

    private void toResults() {
        List<Sign> chosenSigns = new ArrayList<>();
        for (Sign s : signsCounter) {
            if (s.isChosen()) {
                chosenSigns.add(s);
            }
        }

        winsCount = 0;

        for (Sign s : chosenSigns) {
            if (s.isChosen() && s.wasShown()) {
                winsCount++;
                s.setChosen(false);
                s.setShown(false);
            }
        }

        resultTime = Utils.calcTime(time);

        getPresenter().save(resultTime, winsCount);
    }

    public void onSuccess(Result result) {
        stopLoading();

        if (result == null) {
            onError(new IllegalArgumentException());
            return;
        }
        if (result.invalid()) {
            toast(result.error);
            return;
        }

        Bundle args = new Bundle();
        args.putDouble(AttentionVolumeResultsFragment.TIME, resultTime);
        args.putLong(AttentionVolumeResultsFragment.WINS, winsCount);
        getMainActivity().toAttentionVolumeResults(args);
    }

    public void onError(Throwable throwable) {
        stopLoading();
        throwable.printStackTrace();
    }

    @Override
    public void onLeft() {
        if (currentSignSelection > 0) {
            currentSignSelection--;
            refreshSelection();
        }
    }

    private void refreshSelection() {
        signsCounter.get(previousSelection).setSelected(false);
        signsCounter.get(currentSignSelection).setSelected(true);

        previousSelection = currentSignSelection;

        signsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRight() {
        if (currentSignSelection < signsCounter.size()) {
            currentSignSelection++;
            refreshSelection();
        }
    }

    @Override
    public void onTop() {
        if (currentSignSelection > SIGNS_PER_LINE) {
            currentSignSelection -= SIGNS_PER_LINE;
            refreshSelection();
        }
    }

    @Override
    public void onBottom() {
        if (currentSignSelection + SIGNS_PER_LINE < signsCounter.size()) {
            currentSignSelection += SIGNS_PER_LINE;
            refreshSelection();
        }
    }

    @Override
    public void onTopLeft() {
    }

    @Override
    public void onTopRight() {
        if (listener != null) {
            listener.onSignSelected(signsCounter.get(currentSignSelection));
        }
    }

    @Override
    public void onBottomLeft() {
    }

    @Override
    public void onBottomRight() {
    }

    @Override
    public void onCenter() {
    }
}