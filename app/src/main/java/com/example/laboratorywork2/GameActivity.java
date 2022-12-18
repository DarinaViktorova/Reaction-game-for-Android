package com.example.laboratorywork2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.laboratorywork2.observer.Observer;
import com.example.laboratorywork2.services.WriteAndReadService;


import java.io.Serializable;

public class GameActivity extends AppCompatActivity implements Observer, Serializable {

    private static final String TAG = GameActivity.class.getSimpleName();

    public static final String ARG_PARAM1 = "grid";
    public static final String ARG_PARAM2 = "timer";
    public static final String ARG_PARAM3 = "range";

    transient private TextView gameTimerText;
    transient private TextView gamePointsText;
    transient private LinearLayout tableLayout;

    private String gridSize;
    private String timer;
    private String frameRates;

    private int mGridSize;
    private int mTimer;
    private double mRangeBegin;
    private double mRangeEnd;

    private boolean isFinished = false;
    private int mPoints = 0;
    boolean mAlreadyClickedCorrect = false;

    transient CountDownTimer mTimerRound;

    public GameActivity() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        gridSize = getIntent().getStringExtra(ARG_PARAM1);
        timer = getIntent().getStringExtra(ARG_PARAM2);
        frameRates = getIntent().getStringExtra(ARG_PARAM3);


        mGridSize = Options.gridSizes.get(gridSize);
        mTimer = Options.timers.get(timer);
        mRangeBegin = Options.frameRates.get(frameRates).range_begin;
        mRangeEnd = Options.frameRates.get(frameRates).range_end;

        startDialog();
        gameTimerText = findViewById(R.id.game_timer);
        gamePointsText = findViewById(R.id.game_points);
        tableLayout = findViewById(R.id.table_linear_layout);

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
                    launchService(WriteAndReadService.ACTION_WRITE_TO_FILE);
                }
            }.start();
            createTable();
            startRound();
        }, 3500);

        findViewById(R.id.go_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
        recalculatePoints(clickedButton.isChosen());
        gamePointsText.setText(String.valueOf(mPoints));

        return clickedButton.isChosen();
    }

    private void startRound()
    {
        resetTimer();
        int randomizedIndex = (int)(Math.random() * mGridSize * mGridSize);

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
                        mAlreadyClickedCorrect = checkResults((MyButton) view);
                        startRound();
                    }
                });
                button.setLayoutParams(param);
                tableRow.addView(button);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void startDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_dialog, findViewById(R.id.custom_dialog));
        TextView text = layout.findViewById(R.id.textView);
        text.setText("Game starts in:");
        TextView timer = layout.findViewById(R.id.startTimer);
        Toast toast = new Toast(this);
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
        new AlertDialog.Builder(this)
                .setTitle("GAME OVER")
                .setMessage("Total points: " + mPoints)
                .setPositiveButton(android.R.string.yes, (dialog, which) ->
                        startActivity(new Intent(GameActivity.this, MainActivity.class)))
                .show();
    }

    private void launchService(String action) {
        Bundle bundle = new Bundle();
        bundle.putString(WriteAndReadService.GRIDSIZES, gridSize);
        bundle.putString(WriteAndReadService.TIMER, timer);
        bundle.putString(WriteAndReadService.FRAMERATES, frameRates);
        bundle.putString(WriteAndReadService.POINTS, String.valueOf(mPoints));

        Intent intent = new Intent(this, WriteAndReadService.class);
        intent.setAction(action);
        intent.putExtra("observer", this);
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    public void update(String message) {
        Log.println(Log.INFO,TAG,message);
    }
}