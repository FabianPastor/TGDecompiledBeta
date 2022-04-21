package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class AudioVisualizerDrawable {
    public int ALPHA = 61;
    public float ANIMATION_DURATION = 120.0f;
    public float IDLE_RADIUS = (((float) AndroidUtilities.dp(6.0f)) * 0.33f);
    final int MAX_SAMPLE_SUM = 6;
    public float WAVE_RADIUS = (((float) AndroidUtilities.dp(12.0f)) * 0.36f);
    private final float[] animateTo = new float[8];
    private final float[] current = new float[8];
    private final CircleBezierDrawable[] drawables = new CircleBezierDrawable[2];
    private final float[] dt = new float[8];
    private float idleScale;
    private boolean idleScaleInc;
    private float[] lastAmplitude = new float[6];
    private int lastAmplitudeCount;
    private int lastAmplitudePointer;
    private final Paint p1;
    private View parentView;
    private final Random random = new Random();
    float rotation;
    private final int[] tmpWaveform = new int[3];

    public AudioVisualizerDrawable() {
        for (int i = 0; i < 2; i++) {
            CircleBezierDrawable[] circleBezierDrawableArr = this.drawables;
            CircleBezierDrawable circleBezierDrawable = new CircleBezierDrawable(6);
            circleBezierDrawableArr[i] = circleBezierDrawable;
            CircleBezierDrawable drawable = circleBezierDrawable;
            drawable.idleStateDiff = 0.0f;
            drawable.radius = (float) AndroidUtilities.dp(24.0f);
            drawable.radiusDiff = 0.0f;
            drawable.randomK = 1.0f;
        }
        this.p1 = new Paint(1);
    }

    public void setWaveform(boolean playing, boolean animate, float[] waveform) {
        float f = 0.0f;
        if (playing || animate) {
            boolean idleState = waveform != null && waveform[6] == 0.0f;
            float amplitude = waveform == null ? 0.0f : waveform[6];
            if (waveform == null || ((double) amplitude) <= 0.4d) {
                this.lastAmplitudeCount = 0;
            } else {
                float[] fArr = this.lastAmplitude;
                int i = this.lastAmplitudePointer;
                fArr[i] = amplitude;
                int i2 = i + 1;
                this.lastAmplitudePointer = i2;
                if (i2 > 5) {
                    this.lastAmplitudePointer = 0;
                }
                this.lastAmplitudeCount++;
            }
            if (idleState) {
                for (int i3 = 0; i3 < 6; i3++) {
                    waveform[i3] = ((float) (this.random.nextInt() % 500)) / 1000.0f;
                }
            }
            float duration = this.ANIMATION_DURATION;
            if (idleState) {
                duration *= 2.0f;
            }
            if (this.lastAmplitudeCount > 6) {
                float a = 0.0f;
                for (int i4 = 0; i4 < 6; i4++) {
                    a += this.lastAmplitude[i4];
                }
                float a2 = a / 6.0f;
                if (a2 > 0.52f) {
                    duration -= this.ANIMATION_DURATION * (a2 - 0.4f);
                }
            }
            for (int i5 = 0; i5 < 7; i5++) {
                if (waveform == null) {
                    this.animateTo[i5] = 0.0f;
                } else {
                    this.animateTo[i5] = waveform[i5];
                }
                if (this.parentView == null) {
                    this.current[i5] = this.animateTo[i5];
                } else if (i5 == 6) {
                    this.dt[i5] = (this.animateTo[i5] - this.current[i5]) / (this.ANIMATION_DURATION + 80.0f);
                } else {
                    this.dt[i5] = (this.animateTo[i5] - this.current[i5]) / duration;
                }
            }
            float[] fArr2 = this.animateTo;
            if (playing) {
                f = 1.0f;
            }
            fArr2[7] = f;
            this.dt[7] = (fArr2[7] - this.current[7]) / 120.0f;
            return;
        }
        for (int i6 = 0; i6 < 8; i6++) {
            float[] fArr3 = this.animateTo;
            this.current[i6] = 0.0f;
            fArr3[i6] = 0.0f;
        }
    }

    public void draw(Canvas canvas, float cx, float cy, boolean outOwner, Theme.ResourcesProvider resourcesProvider) {
        if (outOwner) {
            this.p1.setColor(Theme.getColor("chat_outLoader", resourcesProvider));
            this.p1.setAlpha(this.ALPHA);
        } else {
            this.p1.setColor(Theme.getColor("chat_inLoader", resourcesProvider));
            this.p1.setAlpha(this.ALPHA);
        }
        draw(canvas, cx, cy);
    }

    public void draw(Canvas canvas, float cx, float cy) {
        for (int i = 0; i < 8; i++) {
            float[] fArr = this.animateTo;
            float f = fArr[i];
            float[] fArr2 = this.current;
            if (f != fArr2[i]) {
                float f2 = fArr2[i];
                float[] fArr3 = this.dt;
                fArr2[i] = f2 + (fArr3[i] * 16.0f);
                if ((fArr3[i] > 0.0f && fArr2[i] > fArr[i]) || (fArr3[i] < 0.0f && fArr2[i] < fArr[i])) {
                    fArr2[i] = fArr[i];
                }
                this.parentView.invalidate();
            }
        }
        if (this.idleScaleInc != 0) {
            float f3 = this.idleScale + 0.02f;
            this.idleScale = f3;
            if (f3 > 1.0f) {
                this.idleScaleInc = false;
                this.idleScale = 1.0f;
            }
        } else {
            float f4 = this.idleScale - 0.02f;
            this.idleScale = f4;
            if (f4 < 0.0f) {
                this.idleScaleInc = true;
                this.idleScale = 0.0f;
            }
        }
        float[] fArr4 = this.current;
        float enterProgress = fArr4[7];
        float radiusProgress = fArr4[6] * fArr4[0];
        if (enterProgress != 0.0f || radiusProgress != 0.0f) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.tmpWaveform[i2] = (int) (this.current[i2] * this.WAVE_RADIUS);
            }
            this.drawables[0].setAdditionals(this.tmpWaveform);
            for (int i3 = 0; i3 < 3; i3++) {
                this.tmpWaveform[i3] = (int) (this.current[i3 + 3] * this.WAVE_RADIUS);
            }
            this.drawables[1].setAdditionals(this.tmpWaveform);
            float radius = ((float) AndroidUtilities.dp(22.0f)) + (((float) AndroidUtilities.dp(4.0f)) * radiusProgress) + (this.IDLE_RADIUS * enterProgress);
            if (radius > ((float) AndroidUtilities.dp(26.0f))) {
                radius = (float) AndroidUtilities.dp(26.0f);
            }
            CircleBezierDrawable[] circleBezierDrawableArr = this.drawables;
            CircleBezierDrawable circleBezierDrawable = circleBezierDrawableArr[0];
            circleBezierDrawableArr[1].radius = radius;
            circleBezierDrawable.radius = radius;
            canvas.save();
            double d = (double) this.rotation;
            Double.isNaN(d);
            float f5 = (float) (d + 0.6d);
            this.rotation = f5;
            canvas.rotate(f5, cx, cy);
            canvas.save();
            float s = (this.idleScale * 0.04f) + 1.0f;
            canvas.scale(s, s, cx, cy);
            this.drawables[0].draw(cx, cy, canvas, this.p1);
            canvas.restore();
            canvas.rotate(60.0f, cx, cy);
            float s2 = ((1.0f - this.idleScale) * 0.04f) + 1.0f;
            canvas.scale(s2, s2, cx, cy);
            this.drawables[1].draw(cx, cy, canvas, this.p1);
            canvas.restore();
        }
    }

    public void setParentView(View parentView2) {
        this.parentView = parentView2;
    }

    public View getParentView() {
        return this.parentView;
    }

    public void setColor(int color) {
        this.p1.setColor(color);
    }
}
