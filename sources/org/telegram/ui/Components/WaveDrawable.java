package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class WaveDrawable {
    private static final float ANIMATION_SPEED_CIRCLE = 0.45f;
    private static final float ANIMATION_SPEED_WAVE_HUGE = 0.65f;
    private static final float ANIMATION_SPEED_WAVE_SMALL = 0.45f;
    public static final float CIRCLE_ALPHA_1 = 0.3f;
    public static final float CIRCLE_ALPHA_2 = 0.15f;
    public static final float FLING_DISTANCE = 0.5f;
    private static final float IDLE_RADIUS = 0.56f;
    private static final float IDLE_ROTATE_DIF = 0.020000001f;
    private static final float IDLE_ROTATION_SPEED = 0.2f;
    private static final float IDLE_SCALE_SPEED = 0.3f;
    private static final float IDLE_WAVE_ANGLE = 0.5f;
    public static final float MAX_AMPLITUDE = 1800.0f;
    private static final float RANDOM_RADIUS_SIZE = 0.3f;
    private static final float ROTATION_SPEED = 0.036000002f;
    public static final float SINE_WAVE_SPEED = 0.81f;
    public static final float SMALL_WAVE_RADIUS = 0.55f;
    public static final float SMALL_WAVE_SCALE = 0.4f;
    public static final float SMALL_WAVE_SCALE_SPEED = 0.6f;
    private static final float WAVE_ANGLE = 0.03f;
    private static final float animationSpeed = 0.35000002f;
    public static final float animationSpeedCircle = 0.55f;
    private static final float animationSpeedTiny = 0.55f;
    private float amplitude;
    public float amplitudeRadius;
    public float amplitudeWaveDif;
    private float animateAmplitudeDiff;
    private float animateAmplitudeSlowDiff;
    private float animateToAmplitude;
    private ValueAnimator animator;
    private final CircleBezierDrawable circleBezierDrawable;
    private float circleRadius;
    private boolean expandIdleRadius;
    private boolean expandScale;
    public float fling;
    private Animator flingAnimator;
    private float flingRadius;
    private final ValueAnimator.AnimatorUpdateListener flingUpdateListener = new WaveDrawable$$ExternalSyntheticLambda0(this);
    private float idleGlobalRadius = (((float) AndroidUtilities.dp(10.0f)) * 0.56f);
    private float idleRadius = 0.0f;
    private float idleRadiusK = 0.075f;
    float idleRotation;
    private boolean incRandomAdditionals;
    private boolean isBig;
    private boolean isIdle = true;
    float lastRadius;
    private long lastUpdateTime;
    private Interpolator linearInterpolator = new LinearInterpolator();
    public float maxScale;
    private Paint paintRecordWaveBig = new Paint();
    private Paint paintRecordWaveTin = new Paint();
    private View parentView;
    float radiusDiff;
    float randomAdditions = (((float) AndroidUtilities.dp(8.0f)) * 0.3f);
    public float rotation;
    private float scaleDif;
    private float scaleIdleDif;
    public float scaleSpeed = 8.0E-5f;
    public float scaleSpeedIdle = 6.0000002E-5f;
    private float sineAngleMax;
    private float slowAmplitude;
    private WaveDrawable tinyWaveDrawable;
    boolean wasFling;
    double waveAngle;
    float waveDif;

    /* renamed from: lambda$new$0$org-telegram-ui-Components-WaveDrawable  reason: not valid java name */
    public /* synthetic */ void m2725lambda$new$0$orgtelegramuiComponentsWaveDrawable(ValueAnimator animation) {
        this.flingRadius = ((Float) animation.getAnimatedValue()).floatValue();
    }

    public WaveDrawable(View parent, int n, float rotateDif, float radius, WaveDrawable tinyDrawable) {
        this.parentView = parent;
        this.circleBezierDrawable = new CircleBezierDrawable(n);
        this.amplitudeRadius = radius;
        boolean z = tinyDrawable != null;
        this.isBig = z;
        this.tinyWaveDrawable = tinyDrawable;
        this.expandIdleRadius = z;
        this.radiusDiff = ((float) AndroidUtilities.dp(34.0f)) * 0.0012f;
        if (Build.VERSION.SDK_INT >= 26) {
            this.paintRecordWaveBig.setAntiAlias(true);
            this.paintRecordWaveTin.setAntiAlias(true);
        }
    }

    public void setValue(float value) {
        ValueAnimator valueAnimator;
        float f = value;
        this.animateToAmplitude = f;
        boolean z = this.isBig;
        if (z) {
            float f2 = this.amplitude;
            if (f > f2) {
                this.animateAmplitudeDiff = (f - f2) / 205.0f;
            } else {
                this.animateAmplitudeDiff = (f - f2) / 275.0f;
            }
            this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / 275.0f;
        } else {
            float f3 = this.amplitude;
            if (f > f3) {
                this.animateAmplitudeDiff = (f - f3) / 320.0f;
            } else {
                this.animateAmplitudeDiff = (f - f3) / 375.0f;
            }
            this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / 375.0f;
        }
        boolean idle = f < 0.1f;
        if (this.isIdle != idle && idle && z) {
            float bRotation = this.rotation;
            float animateToBRotation = (float) ((Math.round(this.rotation / ((float) 60)) * 60) + (60 / 2));
            float tRotation = this.tinyWaveDrawable.rotation;
            float animateToTRotation = (float) (Math.round(tRotation / ((float) 60)) * 60);
            float bWaveDif = this.waveDif;
            float tWaveDif = this.tinyWaveDrawable.waveDif;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.animator = ofFloat;
            WaveDrawable$$ExternalSyntheticLambda1 waveDrawable$$ExternalSyntheticLambda1 = r0;
            float f4 = bRotation;
            float f5 = bWaveDif;
            WaveDrawable$$ExternalSyntheticLambda1 waveDrawable$$ExternalSyntheticLambda12 = new WaveDrawable$$ExternalSyntheticLambda1(this, animateToBRotation, bRotation, animateToTRotation, tRotation, bWaveDif, tWaveDif);
            ofFloat.addUpdateListener(waveDrawable$$ExternalSyntheticLambda1);
            this.animator.setDuration(1200);
            this.animator.start();
        }
        this.isIdle = idle;
        if (!idle && (valueAnimator = this.animator) != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
    }

    /* renamed from: lambda$setValue$1$org-telegram-ui-Components-WaveDrawable  reason: not valid java name */
    public /* synthetic */ void m2726lambda$setValue$1$orgtelegramuiComponentsWaveDrawable(float animateToBRotation, float bRotation, float animateToTRotation, float tRotation, float bWaveDif, float tWaveDif, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.rotation = ((bRotation - animateToBRotation) * v) + animateToBRotation;
        WaveDrawable waveDrawable = this.tinyWaveDrawable;
        waveDrawable.rotation = ((tRotation - animateToTRotation) * v) + animateToTRotation;
        this.waveDif = ((bWaveDif - 1.0f) * v) + 1.0f;
        waveDrawable.waveDif = ((tWaveDif - 1.0f) * v) + 1.0f;
        this.waveAngle = (double) ((float) Math.acos((double) this.waveDif));
        WaveDrawable waveDrawable2 = this.tinyWaveDrawable;
        waveDrawable2.waveAngle = (double) ((float) Math.acos((double) (-waveDrawable2.waveDif)));
    }

    private void startFling(float delta) {
        Animator animator2 = this.flingAnimator;
        if (animator2 != null) {
            animator2.cancel();
        }
        float fling2 = this.fling * 2.0f;
        float flingDistance = this.amplitudeRadius * delta * ((float) (this.isBig ? 8 : 20)) * 16.0f * fling2;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{this.flingRadius, flingDistance});
        valueAnimator.addUpdateListener(this.flingUpdateListener);
        valueAnimator.setDuration((long) (((float) (this.isBig ? 200 : 350)) * fling2));
        valueAnimator.setInterpolator(this.linearInterpolator);
        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(new float[]{flingDistance, 0.0f});
        valueAnimator1.addUpdateListener(this.flingUpdateListener);
        valueAnimator1.setInterpolator(this.linearInterpolator);
        valueAnimator1.setDuration((long) (((float) (this.isBig ? 220 : 380)) * fling2));
        AnimatorSet animatorSet = new AnimatorSet();
        this.flingAnimator = animatorSet;
        animatorSet.playSequentially(new Animator[]{valueAnimator, valueAnimator1});
        animatorSet.start();
    }

    public void tick(float circleRadius2) {
        float f = circleRadius2;
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 20) {
            dt = 17;
        }
        float f2 = this.animateToAmplitude;
        float f3 = this.amplitude;
        if (f2 != f3) {
            float f4 = this.animateAmplitudeDiff;
            float f5 = f3 + (((float) dt) * f4);
            this.amplitude = f5;
            if (f4 > 0.0f) {
                if (f5 > f2) {
                    this.amplitude = f2;
                }
            } else if (f5 < f2) {
                this.amplitude = f2;
            }
            if (Math.abs(this.amplitude - f2) * this.amplitudeRadius >= ((float) AndroidUtilities.dp(4.0f))) {
                this.wasFling = false;
            } else if (!this.wasFling) {
                startFling(this.animateAmplitudeDiff);
                this.wasFling = true;
            }
        }
        float f6 = this.animateToAmplitude;
        float f7 = this.slowAmplitude;
        if (f6 != f7) {
            float f8 = f7 + (this.animateAmplitudeSlowDiff * ((float) dt));
            this.slowAmplitude = f8;
            float abs = Math.abs(f8 - this.amplitude);
            float f9 = 0.2f;
            if (abs > 0.2f) {
                float var_ = this.amplitude;
                if (this.slowAmplitude <= var_) {
                    f9 = -0.2f;
                }
                this.slowAmplitude = var_ + f9;
            }
            if (this.animateAmplitudeSlowDiff > 0.0f) {
                float var_ = this.slowAmplitude;
                float var_ = this.animateToAmplitude;
                if (var_ > var_) {
                    this.slowAmplitude = var_;
                }
            } else {
                float var_ = this.slowAmplitude;
                float var_ = this.animateToAmplitude;
                if (var_ < var_) {
                    this.slowAmplitude = var_;
                }
            }
        }
        this.idleRadius = this.idleRadiusK * f;
        if (this.expandIdleRadius) {
            float var_ = this.scaleIdleDif + (this.scaleSpeedIdle * ((float) dt));
            this.scaleIdleDif = var_;
            if (var_ >= 0.05f) {
                this.scaleIdleDif = 0.05f;
                this.expandIdleRadius = false;
            }
        } else {
            float var_ = this.scaleIdleDif - (this.scaleSpeedIdle * ((float) dt));
            this.scaleIdleDif = var_;
            if (var_ < 0.0f) {
                this.scaleIdleDif = 0.0f;
                this.expandIdleRadius = true;
            }
        }
        float var_ = this.maxScale;
        if (var_ > 0.0f) {
            if (this.expandScale) {
                float var_ = this.scaleDif + (this.scaleSpeed * ((float) dt));
                this.scaleDif = var_;
                if (var_ >= var_) {
                    this.scaleDif = var_;
                    this.expandScale = false;
                }
            } else {
                float var_ = this.scaleDif - (this.scaleSpeed * ((float) dt));
                this.scaleDif = var_;
                if (var_ < 0.0f) {
                    this.scaleDif = 0.0f;
                    this.expandScale = true;
                }
            }
        }
        float var_ = this.sineAngleMax;
        float var_ = this.animateToAmplitude;
        if (var_ > var_) {
            float var_ = var_ - 0.25f;
            this.sineAngleMax = var_;
            if (var_ < var_) {
                this.sineAngleMax = var_;
            }
        } else if (var_ < var_) {
            float var_ = var_ + 0.25f;
            this.sineAngleMax = var_;
            if (var_ > var_) {
                this.sineAngleMax = var_;
            }
        }
        boolean z = this.isIdle;
        if (!z) {
            float var_ = this.rotation;
            float var_ = this.amplitude;
            float var_ = var_ + ((((var_ > 0.5f ? 1.0f : var_ / 0.5f) * 0.14400001f) + 0.018000001f) * ((float) dt));
            this.rotation = var_;
            if (var_ > 360.0f) {
                this.rotation = var_ % 360.0f;
            }
        } else {
            float var_ = this.idleRotation + (((float) dt) * 0.020000001f);
            this.idleRotation = var_;
            if (var_ > 360.0f) {
                this.idleRotation = var_ % 360.0f;
            }
        }
        float var_ = this.lastRadius;
        if (var_ < f) {
            this.lastRadius = f;
        } else {
            float var_ = var_ - (this.radiusDiff * ((float) dt));
            this.lastRadius = var_;
            if (var_ < f) {
                this.lastRadius = f;
            }
        }
        this.lastRadius = f;
        if (!z) {
            double d = this.waveAngle;
            double d2 = (double) (this.amplitudeWaveDif * this.sineAngleMax * ((float) dt));
            Double.isNaN(d2);
            double d3 = d + d2;
            this.waveAngle = d3;
            if (this.isBig) {
                this.waveDif = (float) Math.cos(d3);
            } else {
                this.waveDif = -((float) Math.cos(d3));
            }
            float var_ = this.waveDif;
            if (var_ > 0.0f && this.incRandomAdditionals) {
                this.circleBezierDrawable.calculateRandomAdditionals();
                this.incRandomAdditionals = false;
            } else if (var_ < 0.0f && !this.incRandomAdditionals) {
                this.circleBezierDrawable.calculateRandomAdditionals();
                this.incRandomAdditionals = true;
            }
        }
        this.parentView.invalidate();
    }

    public void draw(float cx, float cy, float scale, Canvas canvas) {
        float f = this.amplitude;
        float waveAmplitude = f < 0.3f ? f / 0.3f : 1.0f;
        float radiusDiff2 = ((float) AndroidUtilities.dp(10.0f)) + (((float) AndroidUtilities.dp(50.0f)) * 0.03f * this.animateToAmplitude);
        this.circleBezierDrawable.idleStateDiff = this.idleRadius * (1.0f - waveAmplitude);
        float kDiff = 0.35f * waveAmplitude * this.waveDif;
        this.circleBezierDrawable.radiusDiff = radiusDiff2 * kDiff;
        this.circleBezierDrawable.cubicBezierK = (Math.abs(kDiff) * waveAmplitude) + 1.0f + ((1.0f - waveAmplitude) * this.idleRadiusK);
        this.circleBezierDrawable.radius = this.lastRadius + (this.amplitudeRadius * this.amplitude) + this.idleGlobalRadius + (this.flingRadius * waveAmplitude);
        float f2 = this.circleBezierDrawable.radius + this.circleBezierDrawable.radiusDiff;
        float f3 = this.circleRadius;
        if (f2 < f3) {
            CircleBezierDrawable circleBezierDrawable2 = this.circleBezierDrawable;
            circleBezierDrawable2.radiusDiff = f3 - circleBezierDrawable2.radius;
        }
        if (this.isBig) {
            this.circleBezierDrawable.globalRotate = this.rotation + this.idleRotation;
        } else {
            this.circleBezierDrawable.globalRotate = (-this.rotation) + this.idleRotation;
        }
        canvas.save();
        float s = (this.scaleIdleDif * (1.0f - waveAmplitude)) + scale + (this.scaleDif * waveAmplitude);
        canvas.scale(s, s, cx, cy);
        this.circleBezierDrawable.setRandomAdditions(this.waveDif * waveAmplitude * this.randomAdditions);
        this.circleBezierDrawable.draw(cx, cy, canvas, this.isBig ? this.paintRecordWaveBig : this.paintRecordWaveTin);
        canvas.restore();
    }

    public void setCircleRadius(float radius) {
        this.circleRadius = radius;
    }

    public void setColor(int color, int alpha) {
        this.paintRecordWaveBig.setColor(color);
        this.paintRecordWaveTin.setColor(color);
        this.paintRecordWaveBig.setAlpha(alpha);
        this.paintRecordWaveTin.setAlpha(alpha);
    }
}
