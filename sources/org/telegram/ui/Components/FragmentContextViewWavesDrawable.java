package org.telegram.ui.Components;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;

public class FragmentContextViewWavesDrawable {
    private float amplitude;
    private float amplitude2;
    private float animateAmplitudeDiff;
    private float animateAmplitudeDiff2;
    private float animateToAmplitude;
    WeavingState currentState;
    private long lastUpdateTime;
    LineBlobDrawable lineBlobDrawable = new LineBlobDrawable(5);
    LineBlobDrawable lineBlobDrawable1 = new LineBlobDrawable(7);
    LineBlobDrawable lineBlobDrawable2 = new LineBlobDrawable(8);
    Paint paint = new Paint(1);
    ArrayList<View> parents = new ArrayList<>();
    WeavingState pausedState;
    WeavingState previousState;
    float progressToState = 1.0f;
    WeavingState[] states = new WeavingState[2];

    public FragmentContextViewWavesDrawable() {
        for (int i = 0; i < 2; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0022  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(float r21, float r22, float r23, float r24, android.graphics.Canvas r25, android.view.View r26) {
        /*
            r20 = this;
            r0 = r20
            java.util.ArrayList<android.view.View> r1 = r0.parents
            int r1 = r1.size()
            r8 = 1
            r2 = 0
            if (r1 <= 0) goto L_0x0018
            java.util.ArrayList<android.view.View> r1 = r0.parents
            java.lang.Object r1 = r1.get(r2)
            r3 = r26
            if (r3 != r1) goto L_0x001a
            r9 = 1
            goto L_0x001b
        L_0x0018:
            r3 = r26
        L_0x001a:
            r9 = 0
        L_0x001b:
            r4 = 0
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            if (r9 == 0) goto L_0x0097
            long r4 = android.os.SystemClock.elapsedRealtime()
            long r6 = r0.lastUpdateTime
            long r6 = r4 - r6
            r0.lastUpdateTime = r4
            r4 = 20
            int r1 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x0035
            r4 = 17
            goto L_0x0036
        L_0x0035:
            r4 = r6
        L_0x0036:
            float r1 = r0.animateToAmplitude
            float r6 = r0.amplitude
            int r7 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r7 == 0) goto L_0x005a
            float r7 = r0.animateAmplitudeDiff
            float r12 = (float) r4
            float r12 = r12 * r7
            float r6 = r6 + r12
            r0.amplitude = r6
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x0051
            int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x0057
            r0.amplitude = r1
            goto L_0x0057
        L_0x0051:
            int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x0057
            r0.amplitude = r1
        L_0x0057:
            r26.invalidate()
        L_0x005a:
            float r1 = r0.animateToAmplitude
            float r6 = r0.amplitude2
            int r7 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r7 == 0) goto L_0x007e
            float r7 = r0.animateAmplitudeDiff2
            float r12 = (float) r4
            float r12 = r12 * r7
            float r6 = r6 + r12
            r0.amplitude2 = r6
            int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x0075
            int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x007b
            r0.amplitude2 = r1
            goto L_0x007b
        L_0x0075:
            int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x007b
            r0.amplitude2 = r1
        L_0x007b:
            r26.invalidate()
        L_0x007e:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r1 = r0.previousState
            if (r1 == 0) goto L_0x0097
            float r1 = r0.progressToState
            float r6 = (float) r4
            r7 = 1132068864(0x437a0000, float:250.0)
            float r6 = r6 / r7
            float r1 = r1 + r6
            r0.progressToState = r1
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 <= 0) goto L_0x0094
            r0.progressToState = r10
            r1 = 0
            r0.previousState = r1
        L_0x0094:
            r26.invalidate()
        L_0x0097:
            r18 = r4
            r7 = 0
        L_0x009a:
            r1 = 2
            if (r7 >= r1) goto L_0x01bc
            if (r7 != 0) goto L_0x00a6
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r1 = r0.previousState
            if (r1 != 0) goto L_0x00a6
            r14 = r7
            goto L_0x01b8
        L_0x00a6:
            if (r7 != 0) goto L_0x00ca
            float r1 = r0.progressToState
            float r1 = r10 - r1
            if (r9 == 0) goto L_0x00bf
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r12 = r0.previousState
            float r2 = r24 - r22
            int r13 = (int) r2
            float r2 = r23 - r21
            int r14 = (int) r2
            float r2 = r0.amplitude
            r15 = r18
            r17 = r2
            r12.update(r13, r14, r15, r17)
        L_0x00bf:
            android.graphics.Paint r2 = r0.paint
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.previousState
            android.graphics.Shader r3 = r3.shader
            r2.setShader(r3)
        L_0x00c8:
            r12 = r1
            goto L_0x00f3
        L_0x00ca:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r12 = r0.currentState
            if (r12 != 0) goto L_0x00cf
            return
        L_0x00cf:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r1 = r0.previousState
            if (r1 == 0) goto L_0x00d6
            float r1 = r0.progressToState
            goto L_0x00d8
        L_0x00d6:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x00d8:
            if (r9 == 0) goto L_0x00e9
            float r2 = r24 - r22
            int r13 = (int) r2
            float r2 = r23 - r21
            int r14 = (int) r2
            float r2 = r0.amplitude
            r15 = r18
            r17 = r2
            r12.update(r13, r14, r15, r17)
        L_0x00e9:
            android.graphics.Paint r2 = r0.paint
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.currentState
            android.graphics.Shader r3 = r3.shader
            r2.setShader(r3)
            goto L_0x00c8
        L_0x00f3:
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            r1.minRadius = r11
            r2 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r4 = r0.amplitude
            float r2 = r2 * r4
            float r3 = r3 + r2
            r1.maxRadius = r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            r1.minRadius = r2
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            r2 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            r4 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            float r6 = r0.amplitude
            float r5 = r5 * r6
            float r3 = r3 + r5
            r1.maxRadius = r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r3 = (float) r3
            r1.minRadius = r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            float r4 = r0.amplitude
            float r3 = r3 * r4
            float r2 = r2 + r3
            r1.maxRadius = r2
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            r2 = 1050253722(0x3e99999a, float:0.3)
            r1.update(r4, r2)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            float r2 = r0.amplitude
            r3 = 1060320051(0x3var_, float:0.7)
            r1.update(r2, r3)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            float r2 = r0.amplitude
            r1.update(r2, r3)
            if (r7 != r8) goto L_0x016c
            android.graphics.Paint r1 = r0.paint
            r2 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r12
            int r2 = (int) r2
            r1.setAlpha(r2)
            goto L_0x0173
        L_0x016c:
            android.graphics.Paint r1 = r0.paint
            r2 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r2)
        L_0x0173:
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            android.graphics.Paint r13 = r0.paint
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r14 = r7
            r7 = r13
            r1.draw(r2, r3, r4, r5, r6, r7)
            android.graphics.Paint r1 = r0.paint
            r2 = 1117257728(0x42980000, float:76.0)
            float r12 = r12 * r2
            int r2 = (int) r12
            r1.setAlpha(r2)
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            float r3 = r0.amplitude2
            float r2 = r2 * r3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r0.amplitude2
            float r12 = r1 * r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            float r3 = r22 - r2
            android.graphics.Paint r7 = r0.paint
            r2 = r21
            r1.draw(r2, r3, r4, r5, r6, r7)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            float r3 = r22 - r12
            android.graphics.Paint r7 = r0.paint
            r1.draw(r2, r3, r4, r5, r6, r7)
        L_0x01b8:
            int r7 = r14 + 1
            goto L_0x009a
        L_0x01bc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextViewWavesDrawable.draw(float, float, float, float, android.graphics.Canvas, android.view.View):void");
    }

    public void setState(int i) {
        WeavingState weavingState = this.currentState;
        if (weavingState != null && weavingState.currentState == i) {
            return;
        }
        if (VoIPService.getSharedInstance() == null && this.currentState == null) {
            this.currentState = this.pausedState;
            return;
        }
        WeavingState weavingState2 = this.currentState;
        this.previousState = weavingState2;
        this.currentState = this.states[i];
        if (weavingState2 != null) {
            this.progressToState = 0.0f;
        } else {
            this.progressToState = 1.0f;
        }
    }

    public void setAmplitude(double d) {
        float min = (float) (Math.min(8500.0d, d) / 8500.0d);
        this.animateToAmplitude = min;
        float f = this.amplitude;
        this.animateAmplitudeDiff = (min - f) / 250.0f;
        this.animateAmplitudeDiff2 = (min - f) / 120.0f;
    }

    public void addParent(View view) {
        if (!this.parents.contains(view)) {
            this.parents.add(view);
        }
    }

    public void removeParent(View view) {
        this.parents.remove(view);
        if (this.parents.isEmpty()) {
            this.pausedState = this.currentState;
            this.currentState = null;
            this.previousState = null;
        }
    }

    public static class WeavingState {
        /* access modifiers changed from: private */
        public final int currentState;
        private float duration;
        private final Matrix matrix = new Matrix();
        public final Shader shader;
        private float startX;
        private float startY;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private float time;

        public WeavingState(int i) {
            int i2 = i;
            this.currentState = i2;
            if (i2 == 0) {
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
                return;
            }
            this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
        }

        public void update(int i, int i2, long j, float f) {
            float f2 = this.duration;
            if (f2 == 0.0f || this.time >= f2) {
                this.duration = (float) (Utilities.random.nextInt(700) + 500);
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    if (this.currentState == 0) {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) - 14.4f;
                        this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                    } else {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 1.1f;
                        this.targetY = (((float) Utilities.random.nextInt(100)) * 4.0f) / 100.0f;
                    }
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                if (this.currentState == 0) {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) - 14.4f;
                    this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                } else {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 1.1f;
                    this.targetY = (((float) Utilities.random.nextInt(100)) * 2.0f) / 100.0f;
                }
            }
            float f3 = (float) j;
            float f4 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f3) + (f3 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * f);
            this.time = f4;
            float f5 = this.duration;
            if (f4 > f5) {
                this.time = f5;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f5);
            float f6 = (float) i2;
            float f7 = this.startX;
            float f8 = ((f7 + ((this.targetX - f7) * interpolation)) * f6) - 200.0f;
            float f9 = this.startY;
            float var_ = (((float) i) * (f9 + ((this.targetY - f9) * interpolation))) - 200.0f;
            float var_ = (f6 / 400.0f) * (this.currentState == 0 ? 3.0f : 1.5f);
            this.matrix.reset();
            this.matrix.postTranslate(f8, var_);
            this.matrix.postScale(var_, var_, f8 + 200.0f, var_ + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }
    }
}
