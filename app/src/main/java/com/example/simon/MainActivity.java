package com.example.simon;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

    TextView gameInfo, scoreInfo;
    Random rand;
    ArrayList<Animator> animList;
    Button[] btnArray;
    Button clickedBtn;
    Button showScores;
    private boolean isGameStarted;
    private int random;
    private int round;
    private int counter;
    private int actScore;
    private int highScore;

    TextView tvx, tvy, tvz, tvSteps, tvStepsRight, tvStepsDown, tvStepsLeft;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final double NORTH_MOVE_FORWARD = 4.5;     // upper mag limit
    private final double NORTH_MOVE_BACKWARD = 2.0;      // lower mag limit
    private final double SOUTH_MOVE_FORWARD = 5.0;     // upper mag limit
    private final double SOUTH_MOVE_BACKWARD = 7.5;
    private final double LEFT_MOVE_FORWARD = -1.9;
    private final double LEFT_MOVE_BACKWARD = 0.4;
    private final double RIGHT_MOVE_FORWARD = 3.0;
    private final double RIGHT_MOVE_BACKWARD = 0.8;


    boolean highLimit = false;      // detect high limit
    boolean bottomLimit = false;
    boolean leftLimit = false;
    boolean rightLimit = false;


        public MainActivity() {

        this.setIsGameStarted(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gameInfo = findViewById(R.id.gameInfo);
        scoreInfo = findViewById(R.id.scoreInfo);
        showScores = findViewById(R.id.showScores);
        showScores.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openActivity2();
            }
        });

        rand = new Random();
        animList = new ArrayList<>();

        fillBtnArray();
        btnSetOnClickListener();
        btnSetOnTouchListener();
        startGame();



        DatabaseHandler db = new DatabaseHandler(this);

        List<HighscoreClass> highscore = db.getAllHighscore();


        //db.emptyContacts();     // empty table if required
        db.emptyHighscore();
        // Inserting Contacts
        Log.i("Insert: ", "Inserting ..");
        db.addHighscore(new HighscoreClass("Joe", 1));
        db.addHighscore(new HighscoreClass("John", 2));
        db.addHighscore(new HighscoreClass("Jack", 3));
        db.addHighscore(new HighscoreClass("James", 10));
        db.addHighscore(new HighscoreClass("Johnrald", 15));
        db.addHighscore(new HighscoreClass("Johnathainsly", 20));

        // Reading all contacts
        Log.i("Reading: ", "Reading all contacts..");

        for (HighscoreClass cn : highscore) {
            String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Highscore: " +
                    cn.getHighscore();
            Log.i("Name: ", log);


        }


    }

    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // called byt the system every x ms
     /*   float x, y, z;

        x = event.values[0];    // get x value from sensor
        y = event.values[1];
        z = event.values[2];

        tvx.setText(String.valueOf(x));
        tvy.setText(String.valueOf(y));
        tvz.setText(String.valueOf(z));

        x = Math.abs(event.values[0]); // get x value
        y = Math.abs(event.values[1]);
        z = Math.abs(event.values[2]);


        if ((x > NORTH_MOVE_FORWARD) && (highLimit == false)) {
            highLimit = true;
        }
        if ((x < NORTH_MOVE_BACKWARD) && (highLimit == true)) {
            // we have a tilt to the north
            counter++;
            tvSteps.setText(String.valueOf(counter));
            highLimit = false;

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);


        }


        if ((x < SOUTH_MOVE_FORWARD) && (bottomLimit == false)) {
            bottomLimit = true;
        }
        if ((x > SOUTH_MOVE_BACKWARD) && (bottomLimit == true)) {

            bottomLimit = false;

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }


        if ((y > LEFT_MOVE_FORWARD) && (leftLimit == false)) {
            leftLimit = true;

        }
        if ((y < LEFT_MOVE_BACKWARD) && (leftLimit == true)) {

            leftLimit = false;

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);

        }

        if ((y > RIGHT_MOVE_FORWARD) && (rightLimit == false)) {
            rightLimit = true;

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }
        if ((y < RIGHT_MOVE_BACKWARD) && (rightLimit == true)) {


            rightLimit = false;



        } */
    }



    @Override
    public void onClick(View view) {
        clickedBtn = (Button)view;

        if(this.getIsGameStarted()) {
            Animator animator = animList.get(this.getCounter());
            ObjectAnimator checkAnimator = (ObjectAnimator)animator;
            Button correctBtn = (Button)checkAnimator.getTarget();

            if(clickedBtn == correctBtn) {
                gameInfo.setText(R.string.match);
                this.setActScore(this.getActScore() + 1);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
                scoreInfo.setText(String.valueOf(this.getActScore()));

                if(this.getCounter() + 1 == this.getRound()) {
                    animateBtn();
                    this.setRound(this.getRound() + 1);
                    this.setCounter(0); // reset counter
                }
                else {
                    this.setCounter(this.getCounter() + 1);
                }
            }
            else {
                newHighscore();
                printHighscore();

                resetGame();
            }
        }
        else {
            showToast();
        }
    }

    public void fillBtnArray() {
        btnArray = new Button[4];
        btnArray[0] = findViewById(R.id.redBtn);
        btnArray[1] = findViewById(R.id.blueBtn);
        btnArray[2] = findViewById(R.id.greenBtn);
        btnArray[3] = findViewById(R.id.yellowBtn);
    }


    public void btnSetOnClickListener() {
        for(int i = 0; i < btnArray.length; i++) {
            btnArray[i].setOnClickListener(this);
        }
    }

    // each button gets an OnTouchListener
    @SuppressLint("ClickableViewAccessibility")
    public void btnSetOnTouchListener() {
        for(int i = 0; i < btnArray.length; i++) {
            btnArray[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            view.getBackground().setAlpha(128);
                            break;
                        case MotionEvent.ACTION_UP:
                            view.getBackground().setAlpha(255);
                            view.performClick();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public void configureAnimation() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(btnArray[this.getRandom()], "alpha", 0.3f);
        animation.setRepeatCount(1);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.setDuration(300);
        animList.add(animation);
    }

    public  void showToast() {
        Toast toast = Toast.makeText(this, R.string.pushStart, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        toast.show();
    }


    //public void GameOver(View view) {

      //  Toast toast = Toast.makeText(this, R.string.GAMEOVER, Toast.LENGTH_SHORT);
      //  toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
      //  toast.show();
   // }

    public void startGame() {
        Button start = findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
                animateBtn();
                round = round + 1;
                isGameStarted = true;
            }
        });
    }

    public void animateBtn() {
        this.setRandom(rand.nextInt(4));
        configureAnimation();

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);

        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                for(int i = 0; i < btnArray.length; i++) {
                    btnArray[i].setEnabled(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                for(int i = 0; i < btnArray.length; i++) {
                    btnArray[i].setEnabled(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSet.start();
    }

    public void newHighscore() {
        if(this.getActScore() >= this.getHighScore()) {
            Toast toast = Toast.makeText(this, R.string.newHighscore, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 50);
            toast.show();
            this.setHighScore(this.getActScore());
        }
    }

    public void printHighscore() {
        gameInfo.setText(String.format("%s %s", getString(R.string.showHighscore), String.valueOf(this.getHighScore())));
    }

    public void resetGame() {
        this.setCounter(0);
        this.setRound(0);
        this.setActScore(0);
        this.setIsGameStarted(false);
        animList.clear(); // there is no animation in animList anymore
    }

    public int getRandom() {
        return random;
    }

    public void setIsGameStarted(boolean isGameStarted) {
        this.isGameStarted = isGameStarted;
    }
    public boolean getIsGameStarted() {
        return isGameStarted;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setActScore(int actScore) {
        this.actScore = actScore;
    }

    public int getActScore() {
        return actScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void openActivity2() {

     Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
    startActivity(intent);
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}