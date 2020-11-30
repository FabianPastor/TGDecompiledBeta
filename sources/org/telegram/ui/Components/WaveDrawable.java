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
    private final ValueAnimator.AnimatorUpdateListener flingUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            WaveDrawable.this.lambda$new$0$WaveDrawable(valueAnimator);
        }
    };
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$WaveDrawable(ValueAnimator valueAnimator) {
        this.flingRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public WaveDrawable(View view, int i, float f, float f2, WaveDrawable waveDrawable) {
        this.parentView = view;
        this.circleBezierDrawable = new CircleBezierDrawable(i);
        this.amplitudeRadius = f2;
        boolean z = waveDrawable != null;
        this.isBig = z;
        this.tinyWaveDrawable = waveDrawable;
        this.expandIdleRadius = z;
        this.radiusDiff = ((float) AndroidUtilities.dp(34.0f)) * 0.0012f;
        if (Build.VERSION.SDK_INT >= 26) {
            this.paintRecordWaveBig.setAntiAlias(true);
            this.paintRecordWaveTin.setAntiAlias(true);
        }
    }

    public void setValue(float f) {
        ValueAnimator valueAnimator;
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
        boolean z2 = f < 0.1f;
        if (this.isIdle != z2 && z2 && z) {
            float f4 = this.rotation;
            float f5 = (float) 60;
            float round = (float) ((Math.round(f4 / f5) * 60) + 30);
            float f6 = this.tinyWaveDrawable.rotation;
            float f7 = this.waveDif;
            float f8 = this.tinyWaveDrawable.waveDif;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(round, f4, (float) (Math.round(f6 / f5) * 60), f6, f7, f8) {
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;
                public final /* synthetic */ float f$5;
                public final /* synthetic */ float f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    WaveDrawable.this.lambda$setValue$1$WaveDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
                }
            });
            this.animator.setDuration(1200);
            this.animator.start();
        }
        this.isIdle = z2;
        if (!z2 && (valueAnimator = this.animator) != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setValue$1 */
    public /* synthetic */ void lambda$setValue$1$WaveDrawable(float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.rotation = f + ((f2 - f) * floatValue);
        WaveDrawable waveDrawable = this.tinyWaveDrawable;
        waveDrawable.rotation = f3 + ((f4 - f3) * floatValue);
        this.waveDif = ((f5 - 1.0f) * floatValue) + 1.0f;
        waveDrawable.waveDif = ((f6 - 1.0f) * floatValue) + 1.0f;
        this.waveAngle = (double) ((float) Math.acos((double) this.waveDif));
        WaveDrawable waveDrawable2 = this.tinyWaveDrawable;
        waveDrawable2.waveAngle = (double) ((float) Math.acos((double) (-waveDrawable2.waveDif)));
    }

    private void startFling(float f) {
        Animator animator2 = this.flingAnimator;
        if (animator2 != null) {
            animator2.cancel();
        }
        float f2 = this.fling * 2.0f;
        float f3 = f * this.amplitudeRadius * ((float) (this.isBig ? 8 : 20)) * 16.0f * f2;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.flingRadius, f3});
        ofFloat.addUpdateListener(this.flingUpdateListener);
        ofFloat.setDuration((long) (((float) (this.isBig ? 200 : 350)) * f2));
        ofFloat.setInterpolator(this.linearInterpolator);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{f3, 0.0f});
        ofFloat2.addUpdateListener(this.flingUpdateListener);
        ofFloat2.setInterpolator(this.linearInterpolator);
        ofFloat2.setDuration((long) (((float) (this.isBig ? 220 : 380)) * f2));
        AnimatorSet animatorSet = new AnimatorSet();
        this.flingAnimator = animatorSet;
        animatorSet.playSequentially(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
    }

    public void tick(float f) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.lastUpdateTime;
        this.lastUpdateTime = elapsedRealtime;
        if (j > 20) {
            j = 17;
        }
        float f2 = this.animateToAmplitude;
        float f3 = this.amplitude;
        if (f2 != f3) {
            float f4 = this.animateAmplitudeDiff;
            float f5 = f3 + (((float) j) * f4);
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
            float f8 = f7 + (this.animateAmplitudeSlowDiff * ((float) j));
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
            float var_ = this.scaleIdleDif + (this.scaleSpeedIdle * ((float) j));
            this.scaleIdleDif = var_;
            if (var_ >= 0.05f) {
                this.scaleIdleDif = 0.05f;
                this.expandIdleRadius = false;
            }
        } else {
            float var_ = this.scaleIdleDif - (this.scaleSpeedIdle * ((float) j));
            this.scaleIdleDif = var_;
            if (var_ < 0.0f) {
                this.scaleIdleDif = 0.0f;
                this.expandIdleRadius = true;
            }
        }
        float var_ = this.maxScale;
        if (var_ > 0.0f) {
            if (this.expandScale) {
                float var_ = this.scaleDif + (this.scaleSpeed * ((float) j));
                this.scaleDif = var_;
                if (var_ >= var_) {
                    this.scaleDif = var_;
                    this.expandScale = false;
                }
            } else {
                float var_ = this.scaleDif - (this.scaleSpeed * ((float) j));
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
            float var_ = var_ + ((((var_ > 0.5f ? 1.0f : var_ / 0.5f) * 0.14400001f) + 0.018000001f) * ((float) j));
            this.rotation = var_;
            if (var_ > 360.0f) {
                this.rotation = var_ % 360.0f;
            }
        } else {
            float var_ = this.idleRotation + (((float) j) * 0.020000001f);
            this.idleRotation = var_;
            if (var_ > 360.0f) {
                this.idleRotation = var_ % 360.0f;
            }
        }
        float var_ = this.lastRadius;
        if (var_ < f) {
            this.lastRadius = f;
        } else {
            float var_ = var_ - (this.radiusDiff * ((float) j));
            this.lastRadius = var_;
            if (var_ < f) {
                this.lastRadius = f;
            }
        }
        this.lastRadius = f;
        if (!z) {
            double d = this.waveAngle;
            double d2 = (double) (this.amplitudeWaveDif * this.sineAngleMax * ((float) j));
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

    public void draw(float f, float f2, float f3, Canvas canvas) {
        float f4 = this.amplitude;
        float f5 = f4 < 0.3f ? f4 / 0.3f : 1.0f;
        float dp = ((float) AndroidUtilities.dp(10.0f)) + (((float) AndroidUtilities.dp(50.0f)) * 0.03f * this.animateToAmplitude);
        CircleBezierDrawable circleBezierDrawable2 = this.circleBezierDrawable;
        float f6 = 1.0f - f5;
        circleBezierDrawable2.idleStateDiff = this.idleRadius * f6;
        float f7 = 0.35f * f5 * this.waveDif;
        circleBezierDrawable2.radiusDiff = dp * f7;
        circleBezierDrawable2.cubicBezierK = (Math.abs(f7) * f5) + 1.0f + (this.idleRadiusK * f6);
        CircleBezierDrawable circleBezierDrawable3 = this.circleBezierDrawable;
        float f8 = this.lastRadius + (this.amplitudeRadius * this.amplitude) + this.idleGlobalRadius + (this.flingRadius * f5);
        circleBezierDrawable3.radius = f8;
        float f9 = this.circleRadius;
        if (circleBezierDrawable3.radiusDiff + f8 < f9) {
            circleBezierDrawable3.radiusDiff = f9 - f8;
        }
        if (this.isBig) {
            circleBezierDrawable3.globalRotate = this.rotation + this.idleRotation;
        } else {
            circleBezierDrawable3.globalRotate = (-this.rotation) + this.idleRotation;
        }
        canvas.save();
        float var_ = f3 + (this.scaleIdleDif * f6) + (this.scaleDif * f5);
        canvas.scale(var_, var_, f, f2);
        this.circleBezierDrawable.setRandomAdditions(f5 * this.waveDif * this.randomAdditions);
        this.circleBezierDrawable.draw(f, f2, canvas, this.isBig ? this.paintRecordWaveBig : this.paintRecordWaveTin);
        canvas.restore();
    }

    public void setCircleRadius(float f) {
        this.circleRadius = f;
    }

    public void setColor(int i, int i2) {
        this.paintRecordWaveBig.setColor(i);
        this.paintRecordWaveTin.setColor(i);
        this.paintRecordWaveBig.setAlpha(i2);
        this.paintRecordWaveTin.setAlpha(i2);
    }
}
