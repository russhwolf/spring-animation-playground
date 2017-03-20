package com.brucelet.playground.dynamicanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends Activity {

    private float downX, downY;
    private SeekBar damping, stiffness;
    private SpringAnimation animY;
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
                        float x = event.getX() - box.getTranslationX();
                        float y = event.getY() - box.getTranslationY();
                        if (box.getLeft() < x && box.getRight() > x && box.getTop() < y && box.getBottom() > y) {
                            isDragging = true;
                            downX = x;
                            downY = y;
                            velocityTracker.addMovement(event);
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
                            if (box.getTranslationY() <= 0) {
                                if (animY != null) {
                                    animY.cancel();
                                }
                                animY = new SpringAnimation(box, SpringAnimation.TRANSLATION_Y, 0);
                                animY.getSpring().setStiffness(getStiffness());
                                animY.getSpring().setDampingRatio(getDamping());
                                animY.setMaxValue(0);

                                final float upX = box.getTranslationX();
                                final float velocityX = velocityTracker.getXVelocity();
                                final long startTime = System.currentTimeMillis();
                                animY.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(
                                            DynamicAnimation animation,
                                            float value,
                                            float velocity) {
                                        float translationX =
                                                upX + (System.currentTimeMillis() - startTime) * velocityX / 1000f;
                                        translationX = Math.max(-(root.getWidth() - box.getWidth()) / 2, translationX);
                                        translationX = Math.min(+(root.getWidth() - box.getWidth()) / 2, translationX);
                                        box.setTranslationX(translationX);
                                    }
                                });
                                animY.setStartVelocity(velocityTracker.getYVelocity());
                                animY.start();
                            }
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