package com.inri.sopsop.view.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.inri.sopsop.App;
import com.inri.sopsop.BluetoothEventListener;
import com.inri.sopsop.R;
import com.inri.sopsop.Utils;
import com.inri.sopsop.model.Answer;
import com.inri.sopsop.model.Circle;
import com.inri.sopsop.model.Difficulty;
import com.inri.sopsop.model.Result;
import com.inri.sopsop.presenter.FocusingTestPresenter;
import com.inri.sopsop.view.adapter.CirclesAdapter;
import com.inri.sopsop.view.base.BaseFragment;
import com.inri.sopsop.view.results.FocusingResultsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;


@RequiresPresenter(FocusingTestPresenter.class)
public class FocusingTestFragment extends BaseFragment<FocusingTestPresenter> implements BluetoothEventListener {

    private Circle baseCircle;

    private final static int LINES_VISIBLE = 11;

    private static int LINES_COUNT = 20;
    private final static int CIRCLES_PER_LINE = 15;

    private final static int BUTTONS_COUNT = 10;
    private final static int BUTTONS_PER_LINE = 5;

    private Handler handler;

    private int wins;
    private int fails;

    private boolean locked = false;

    @BindView(R.id.circlesGrid)
    RecyclerView circlesGrid;

    @BindView(R.id.baseCircle)
    ImageView baseCircleView;

    @BindViews({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button10})
    Button[] buttonViews;

    CirclesAdapter adapter;

    private int currentLine = 0;

    private int previousSelection;
    private int currentButtonSelection;

    private long time;

    private ArrayList<Circle> circles = new ArrayList<>();

    private ArrayList<Answer> answers = new ArrayList<>();

    public static FocusingTestFragment newInstance() {

        Bundle args = new Bundle();

        FocusingTestFragment fragment = new FocusingTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.focusingTestTitle);
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_test_circles;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View v) {
        Difficulty diff = App.diff(getActivity());
        LINES_COUNT *= diff.getLevel();

        baseCircle = Circle.random();
        baseCircleView.setRotation(Circle.rotation(baseCircle));

        for (int i = 0; i < LINES_VISIBLE; i++) {
            ArrayList<Circle> line = new ArrayList<>();
            for (int j = 0; j < CIRCLES_PER_LINE; j++) {
                line.add(Circle.random());
            }

            int result = Circle.answer(line, baseCircle);
            if (result > 10 || result < 1) {
                i--;
            } else {
                circles.addAll(line);
            }
        }

        adapter = new CirclesAdapter();
        adapter.setCircles(circles);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), CIRCLES_PER_LINE);
        circlesGrid.setLayoutManager(manager);

        circlesGrid.setAdapter(adapter);

        time = System.nanoTime();
    }

    private void replaceCircleLine() {
        ArrayList<Circle> newCircles = new ArrayList<>(circles.subList(CIRCLES_PER_LINE, circles.size()));

        if (LINES_COUNT - currentLine > LINES_VISIBLE) {
            for (int i = 0; i < CIRCLES_PER_LINE; i++) {
                newCircles.add(Circle.random());
            }
        }

        circles = newCircles;
        adapter.setCircles(circles);

        baseCircle = Circle.random();
        baseCircleView.setRotation(Circle.rotation(baseCircle));
    }

    @OnClick(R.id.button1)
    public void click1() {
        click(1);
    }

    @OnClick(R.id.button2)
    public void click2() {
        click(2);
    }

    @OnClick(R.id.button3)
    public void click3() {
        click(3);
    }

    @OnClick(R.id.button4)
    public void click4() {
        click(4);
    }

    @OnClick(R.id.button5)
    public void click5() {
        click(5);
    }

    @OnClick(R.id.button6)
    public void click6() {
        click(6);
    }

    @OnClick(R.id.button7)
    public void click7() {
        click(7);
    }

    @OnClick(R.id.button8)
    public void click8() {
        click(8);
    }

    @OnClick(R.id.button9)
    public void click9() {
        click(9);
    }

    @OnClick(R.id.button10)
    public void click10() {
        click(10);
    }

    public void click(int count) {

        if (locked) {
            return;
        }

        if (currentLine == LINES_COUNT) {
            toNextTest();
            return;
        }

        if (currentLine > LINES_COUNT) {
            return;
        }

        List<Circle> lineCircles = circles.subList(0, CIRCLES_PER_LINE);
        int answer = Circle.answer(lineCircles, baseCircle);

        Answer ans = new Answer();
        ans.setNumber(currentLine);

        if (answer == count) {
            wins++;
        } else {
            fails++;
        }

        ans.setErrorValue(Math.abs(answer - count));
        ans.setTime(Utils.calcTime(time));
        answers.add(ans);

        //    Toast.makeText(getActivity(), "Wins = " + wins + ", Fails = " + fails, Toast.LENGTH_SHORT).show();

        replaceCircleLine();
        time = System.nanoTime();

        currentLine++;
        if (currentLine == LINES_COUNT) {
            toNextTest();
        }
    }

    private void toNextTest() {
        locked = true;
        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Long> errors = new ArrayList<>();

        for (Answer a : answers) {
            times.add(a.getTime());

            errors.add((long) a.getErrorValue());
        }

        startLoading();
        getPresenter().save(times, errors);
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
        args.putParcelableArrayList(FocusingResultsFragment.RESULTS, answers);
        args.putInt(FocusingResultsFragment.LINES, LINES_COUNT);
        args.putInt(FocusingResultsFragment.ERRORS, fails);
        getMainActivity().toFocusingResults(args);
    }

    public void onError(Throwable throwable) {
        stopLoading();
        throwable.printStackTrace();

        locked = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getMainActivity() != null) {
            getMainActivity().registerBluetoothListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getMainActivity() != null) {
            getMainActivity().unregisterBluetoothListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onLeft() {
        if (currentButtonSelection > 0) {
            currentButtonSelection--;
            refreshSelection();
        }
    }

    private void refreshSelection() {
        buttonViews[previousSelection].setBackgroundResource(android.R.color.darker_gray);

        previousSelection = currentButtonSelection;

        buttonViews[currentButtonSelection].setBackgroundResource(R.drawable.outline);
    }

    @Override
    public void onRight() {
        if (currentButtonSelection < BUTTONS_COUNT - 1) {
            currentButtonSelection++;
            refreshSelection();
        }
    }

    @Override
    public void onTop() {
        if (currentButtonSelection + BUTTONS_PER_LINE < BUTTONS_COUNT) {
            currentButtonSelection += BUTTONS_PER_LINE;
            refreshSelection();
        }
    }

    @Override
    public void onBottom() {
        if (currentButtonSelection >= BUTTONS_PER_LINE) {
            currentButtonSelection -= BUTTONS_PER_LINE;
            refreshSelection();
        }
    }


    @Override
    public void onTopLeft() {
    }

    @Override
    public void onTopRight() {
    }

    @Override
    public void onBottomLeft() {
    }

    @Override
    public void onBottomRight() {
        click(currentButtonSelection + 1); // 0-9 -> 1-10
    }

    @Override
    public void onCenter() {
    }
}