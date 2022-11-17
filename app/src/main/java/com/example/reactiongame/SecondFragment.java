package com.example.reactiongame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.reactiongame.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private TextView gameTimerText;
    private TextView gamePointsText;
    private TableLayout tableLayout;

    private static final String ARG_PARAM1 = "grid";
    private static final String ARG_PARAM2 = "timer";
    private static final String ARG_PARAM3 = "range";

    private int mGridSize;
    private int mTimer;
    private double mRangeBegin;
    private double mRangeEnd;

    private boolean isFinished = false;
    private int mPoints = 0;
    boolean mAlreadyClickedCorrect = false;

    CountDownTimer mTimerRound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGridSize = Options.gridSizes.get(getArguments().getString(ARG_PARAM1));
            mTimer = Options.timers.get(getArguments().getString(ARG_PARAM2));
            mRangeBegin = Options.frameRates.get(getArguments().getString(ARG_PARAM3)).range_begin;
            mRangeEnd = Options.frameRates.get(getArguments().getString(ARG_PARAM3)).range_end;
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startDialog(view);
        gameTimerText = view.findViewById(R.id.game_timer);
        gamePointsText = view.findViewById(R.id.game_points);
        tableLayout = view.findViewById(R.id.table_layout);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            new CountDownTimer(mTimer*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    gameTimerText.setText(String.valueOf(millisUntilFinished / 1000));
                }
                public void onFinish() {
                    isFinished = true;
                    gameTimerText.setText("Finish!");
                    resetTimer();
                    pointsDialog();
                }
            }.start();
            createTable();
            startRound();
        }, 3500);
    }

    public double randomInRange(double begin, double end)
    {
        double diff = end - begin;
        double range = Math.random() * diff;
        return begin + range;
    }

    private void resetTimer()
    {
        if (isFinished) {
            mTimerRound.cancel();
            return;
        }
        if (!mAlreadyClickedCorrect && mTimerRound != null) mTimerRound.cancel();

        double dTimeout = randomInRange(mRangeBegin, mRangeEnd);
        long lTimeoutSeconds = (long) (dTimeout * 1000);

        mTimerRound = new CountDownTimer(lTimeoutSeconds, lTimeoutSeconds) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                if (!mAlreadyClickedCorrect) {
                    recalculatePoints(false);
                    gamePointsText.setText(String.valueOf(mPoints));
                    startRound();
                }
                mAlreadyClickedCorrect = false;
            }
        };
        mTimerRound.start();
    }

    private void recalculatePoints(boolean win)
    {
        if (win) mPoints += 2;
        else mPoints = (mPoints > 0) ? (mPoints - 1) : 0;
    }

    private boolean checkResults(MyButton clickedButton)
    {
        if (clickedButton.isChosen()) recalculatePoints(true);
        else recalculatePoints(false);
        gamePointsText.setText(String.valueOf(mPoints));

        return clickedButton.isChosen();
    }

    private void startRound()
    {
        resetTimer();
        Integer randomizedIndex = (int)(Math.random() * mGridSize * mGridSize);

        for (int y = 0; y < mGridSize; ++y)
        {
            TableRow row = (TableRow) tableLayout.getChildAt(y);
            for (int x = 0; x < mGridSize; ++x)
            {
                MyButton button = (MyButton) row.getChildAt(x);
                int index = y * mGridSize + x;

                if (index != randomizedIndex) {
                    button.setChosen(false);
                    button.setBackgroundResource(R.drawable.original_button_shape);
                }
                else {
                    button.setChosen(true);
                    button.setBackgroundResource(R.drawable.curve_shape);
                }
            }
        }
    }

    private void createTable () {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f
        );
        for (int i = 0; i < mGridSize; ++i){
            TableRow tableRow = new TableRow(tableLayout.getContext());
            tableRow.setMinimumHeight(tableLayout.getWidth() / mGridSize);

            for (int j = 0; j < mGridSize; ++j){
                MyButton button = new MyButton(tableLayout.getContext(), false);
                button.setOnClickListener((View view) -> {
                    if (!isFinished) {
                        if (checkResults((MyButton) view))
                            mAlreadyClickedCorrect = true;
                        else
                            mAlreadyClickedCorrect = false;
                        startRound();
                    }
                });
                button.setLayoutParams(param);
                tableRow.addView(button);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void startDialog(@NonNull View view){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_dialog, view.findViewById(R.id.custom_dialog));
        TextView text = layout.findViewById(R.id.textView);
        text.setText("Game starts in:");
        TextView timer = layout.findViewById(R.id.startTimer);
        Toast toast = new Toast(view.getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        new CountDownTimer(3500, 1000) {
            public void onTick(long millisUntilFinished) {
                long value = millisUntilFinished / 1000;
                if (value != 0) timer.setText(String.valueOf(value));
                else timer.setText("Let's go!");
            }
            public void onFinish() {
                timer.setText("Let's go!");
            }
        }.start();
    }

    private void pointsDialog () {
        new AlertDialog.Builder(getContext())
                .setTitle("GAME OVER")
                .setMessage("Total points: " + mPoints)
                .setPositiveButton(android.R.string.yes, (dialog, which) ->
                        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_from_SecondFragment_to_FirstFragment))
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}