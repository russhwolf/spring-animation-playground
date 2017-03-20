package com.brucelet.playground.dynamicanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends Activity {

    private SeekBar damping, stiffness;
    private SpringAnimation animX, animY;
    private View root;
    private View box;

    private float getOffsetX() {
        return (root.getWidth()) / 2;
    }

    private float getOffsetY() {
        return (root.getHeight() + damping.getBottom()) / 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stiffness = (SeekBar) findViewById(R.id.stiffness);
        damping = (SeekBar) findViewById(R.id.damping);
        box = findViewById(R.id.box);
        root = findViewById(R.id.root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (animX == null || !animX.isRunning()) {
                            animX = new SpringAnimation(box, SpringAnimation.TRANSLATION_X,
                                                        event.getX() - getOffsetX());
                            animX.getSpring().setStiffness(getStiffness());
                            animX.getSpring().setDampingRatio(getDamping());
                            animX.setStartVelocity(0);
                            animX.start();
                        } else {
                            animX.getSpring().setFinalPosition(event.getX() - getOffsetX());
                        }
                        if (animY == null || !animY.isRunning()) {
                            animY = new SpringAnimation(box, SpringAnimation.TRANSLATION_Y,
                                                        event.getY() - getOffsetY());
                            animY.getSpring().setStiffness(getStiffness());
                            animY.getSpring().setDampingRatio(getDamping());
                            animY.setStartVelocity(0);
                            animY.start();
                        } else {
                            animY.getSpring().setFinalPosition(event.getY() - getOffsetY());
                        }
                        return true;
                }
                return false;
            }
        });

        stiffness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (animX != null) {
                    animX.getSpring().setStiffness(getStiffness());
                }
                if (animY != null) {
                    animY.getSpring().setStiffness(getStiffness());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        damping.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (animX != null) {
                    animX.getSpring().setDampingRatio(getDamping());
                }
                if (animY != null) {
                    animY.getSpring().setDampingRatio(getDamping());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private float getStiffness() {
        return Math.max(stiffness.getProgress(), 1f);
    }

    private float getDamping() {
        return damping.getProgress() / 100f;
    }
}