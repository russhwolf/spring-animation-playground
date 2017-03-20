package com.brucelet.playground.dynamicanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends Activity {

    private float downX, downY;
    private SeekBar damping, stiffness;
    private SpringAnimation animX, animY;
    private VelocityTracker velocityTracker;
    private boolean isDragging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stiffness = (SeekBar) findViewById(R.id.stiffness);
        damping = (SeekBar) findViewById(R.id.damping);
        velocityTracker = VelocityTracker.obtain();
        final View box = findViewById(R.id.box);
        final View root = findViewById(R.id.root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isDragging) {
                            if (animX != null) {
                                animX.cancel();
                            }
                            if (animY != null) {
                                animY.cancel();
                            }
                            float x = event.getX() - box.getTranslationX();
                            float y = event.getY() - box.getTranslationY();
                            if (box.getLeft() < x && box.getRight() > x && box.getTop() < y && box.getBottom() > y) {
                                isDragging = true;
                                downX = x;
                                downY = y;
                                velocityTracker.addMovement(event);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            box.setTranslationX(event.getX() - downX);
                            box.setTranslationY(event.getY() - downY);
                            velocityTracker.addMovement(event);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isDragging) {
                            velocityTracker.computeCurrentVelocity(1000);

                            if (animX != null) {
                                animX.cancel();
                            }
                            float translationX = box.getTranslationX();
                            float targetX;
                            if (translationX < -root.getWidth() / 6) {
                                targetX = root.getLeft() - box.getLeft();
                            } else if (translationX > root.getWidth() / 6) {
                                targetX = root.getRight() - box.getRight();
                            } else {
                                targetX = 0;
                            }
                            animX = new SpringAnimation(box, SpringAnimation.TRANSLATION_X, targetX);
                            animX.getSpring().setStiffness(getStiffness());
                            animX.getSpring().setDampingRatio(getDamping());
                            animX.setStartVelocity(velocityTracker.getXVelocity());
                            animX.start();

                            if (animY != null) {
                                animY.cancel();
                            }
                            float translationY = box.getTranslationY();
                            float targetY;
                            if (translationY < -root.getHeight() / 6) {
                                targetY = root.getTop() - box.getTop() + damping.getBottom();
                            } else if (translationY > root.getHeight() / 6) {
                                targetY = root.getBottom() - box.getBottom();
                            } else {
                                targetY = 0;
                            }
                            animY = new SpringAnimation(box, SpringAnimation.TRANSLATION_Y, targetY);
                            animY.getSpring().setStiffness(getStiffness());
                            animY.getSpring().setDampingRatio(getDamping());
                            animY.setStartVelocity(velocityTracker.getYVelocity());
                            animY.start();

                            velocityTracker.clear();
                            isDragging = false;
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