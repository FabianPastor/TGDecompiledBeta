package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;

public class GroupCallPipButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener {
    float amplitude;
    float animateAmplitudeDiff;
    float animateToAmplitude;
    private RLottieDrawable bigMicDrawable;
    BlobDrawable blobDrawable = new BlobDrawable(8);
    BlobDrawable blobDrawable2 = new BlobDrawable(9);
    WeavingState currentState;
    long lastStubUpdateAmplitude;
    Matrix matrix = new Matrix();
    private RLottieImageView muteButton;
    Paint paint = new Paint(1);
    WeavingState pausedState;
    boolean prepareToRemove;
    private final LinearGradient prepareToRemoveShader;
    float pressedProgress;
    boolean pressedState;
    WeavingState previousState;
    float progressToPrepareRemove;
    float progressToState = 1.0f;
    Random random = new Random();
    float removeAngle;
    WeavingState[] states = new WeavingState[2];
    private boolean stub;

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPBaseService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onStateChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onStateChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public GroupCallPipButton(Context context, int i, boolean z) {
        super(context);
        this.stub = z;
        for (int i2 = 0; i2 < 2; i2++) {
            this.states[i2] = new WeavingState(i2);
        }
        this.blobDrawable.maxRadius = (float) AndroidUtilities.dp(45.0f);
        this.blobDrawable.minRadius = (float) AndroidUtilities.dp(40.0f);
        this.blobDrawable2.maxRadius = (float) AndroidUtilities.dp(45.0f);
        this.blobDrawable2.minRadius = (float) AndroidUtilities.dp(40.0f);
        this.blobDrawable.generateBlob();
        this.blobDrawable2.generateBlob();
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(22.0f), AndroidUtilities.dp(30.0f), true, (int[]) null);
        this.bigMicDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        setWillNotDraw(false);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.muteButton = rLottieImageView;
        rLottieImageView.setAnimation(this.bigMicDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.muteButton);
        this.prepareToRemoveShader = new LinearGradient(0.0f, 0.0f, (float) AndroidUtilities.dp(350.0f), 0.0f, new int[]{-2801343, -561538, 0}, new float[]{0.0f, 0.4f, 1.0f}, Shader.TileMode.CLAMP);
        if (z) {
            setState(0);
        }
    }

    public void setPressedState(boolean z) {
        this.pressedState = z;
    }

    public static class WeavingState {
        int color1;
        int color2;
        /* access modifiers changed from: private */
        public final int currentState;
        private float duration;
        private final Matrix matrix = new Matrix();
        public Shader shader;
        private float startX;
        private float startY;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private float time;

        public WeavingState(int i) {
            this.currentState = i;
        }

        public void update(long j, float f) {
            if (this.currentState == 0) {
                if (!(this.color1 == Theme.getColor("voipgroup_overlayGreen1") && this.color2 == Theme.getColor("voipgroup_overlayGreen2"))) {
                    int color = Theme.getColor("voipgroup_overlayGreen1");
                    this.color1 = color;
                    int color3 = Theme.getColor("voipgroup_overlayGreen2");
                    this.color2 = color3;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color3}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (!(this.color1 == Theme.getColor("voipgroup_overlayBlue1") && this.color2 == Theme.getColor("voipgroup_overlayBlue2"))) {
                int color4 = Theme.getColor("voipgroup_overlayBlue1");
                this.color1 = color4;
                int color5 = Theme.getColor("voipgroup_overlayBlue2");
                this.color2 = color5;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color4, color5}, (float[]) null, Shader.TileMode.CLAMP);
            }
            int dp = AndroidUtilities.dp(130.0f);
            float f2 = this.duration;
            if (f2 == 0.0f || this.time >= f2) {
                this.duration = (float) (Utilities.random.nextInt(700) + 500);
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    if (this.currentState == 0) {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.2f;
                        this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                    } else {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                        this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
                    }
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                if (this.currentState == 0) {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) + 0.0f;
                    this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.9f;
                } else {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                    this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
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
            float f6 = (float) dp;
            float f7 = this.startX;
            float f8 = ((f7 + ((this.targetX - f7) * interpolation)) * f6) - 200.0f;
            float f9 = this.startY;
            float var_ = ((f9 + ((this.targetY - f9) * interpolation)) * f6) - 200.0f;
            float var_ = (f6 / 400.0f) * (this.currentState == 0 ? 3.0f : 1.5f);
            this.matrix.reset();
            this.matrix.postTranslate(f8, var_);
            this.matrix.postScale(var_, var_, f8 + 200.0f, var_ + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x012e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r14) {
        /*
            r13 = this;
            super.onDraw(r14)
            int r0 = r13.getMeasuredWidth()
            r1 = 1
            int r0 = r0 >> r1
            float r0 = (float) r0
            int r2 = r13.getMeasuredHeight()
            int r2 = r2 >> r1
            float r2 = (float) r2
            boolean r3 = r13.pressedState
            r4 = 1037726734(0x3dda740e, float:0.10666667)
            r5 = 0
            r6 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x002a
            float r7 = r13.pressedProgress
            int r8 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x002a
            float r7 = r7 + r4
            r13.pressedProgress = r7
            int r3 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x003b
            r13.pressedProgress = r6
            goto L_0x003b
        L_0x002a:
            if (r3 != 0) goto L_0x003b
            float r3 = r13.pressedProgress
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x003b
            float r3 = r3 - r4
            r13.pressedProgress = r3
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x003b
            r13.pressedProgress = r5
        L_0x003b:
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r4 = r13.pressedProgress
            float r3 = r3.getInterpolation(r4)
            org.telegram.ui.Components.RLottieImageView r4 = r13.muteButton
            r7 = 1036831949(0x3dcccccd, float:0.1)
            float r3 = r3 * r7
            float r8 = r3 + r6
            r4.setScaleY(r8)
            org.telegram.ui.Components.RLottieImageView r4 = r13.muteButton
            r4.setScaleX(r8)
            android.graphics.Paint r4 = r13.paint
            java.lang.String r9 = "voipgroup_muteButton"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setColor(r9)
            boolean r4 = r13.stub
            if (r4 == 0) goto L_0x009b
            long r9 = java.lang.System.currentTimeMillis()
            long r11 = r13.lastStubUpdateAmplitude
            long r9 = r9 - r11
            r11 = 1000(0x3e8, double:4.94E-321)
            int r4 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r4 <= 0) goto L_0x009b
            long r9 = java.lang.System.currentTimeMillis()
            r13.lastStubUpdateAmplitude = r9
            java.util.Random r4 = r13.random
            int r4 = r4.nextInt()
            int r4 = r4 % 100
            int r4 = java.lang.Math.abs(r4)
            float r4 = (float) r4
            r9 = 1056964608(0x3var_, float:0.5)
            float r4 = r4 * r9
            r10 = 1120403456(0x42CLASSNAME, float:100.0)
            float r4 = r4 / r10
            float r4 = r4 + r9
            r13.animateToAmplitude = r4
            float r9 = r13.amplitude
            float r4 = r4 - r9
            r9 = 1153138688(0x44bb8000, float:1500.0)
            float r11 = org.telegram.ui.Components.BlobDrawable.AMPLITUDE_SPEED
            float r11 = r11 * r9
            float r11 = r11 + r10
            float r4 = r4 / r11
            r13.animateAmplitudeDiff = r4
        L_0x009b:
            float r4 = r13.animateToAmplitude
            float r9 = r13.amplitude
            int r10 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r10 == 0) goto L_0x00c0
            float r10 = r13.animateAmplitudeDiff
            r11 = 1098907648(0x41800000, float:16.0)
            float r11 = r11 * r10
            float r9 = r9 + r11
            r13.amplitude = r9
            int r10 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r10 <= 0) goto L_0x00b7
            int r9 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x00bd
            r13.amplitude = r4
            goto L_0x00bd
        L_0x00b7:
            int r9 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r9 >= 0) goto L_0x00bd
            r13.amplitude = r4
        L_0x00bd:
            r13.invalidate()
        L_0x00c0:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r4 = r13.previousState
            if (r4 == 0) goto L_0x00d8
            float r4 = r13.progressToState
            r9 = 1032000111(0x3d83126f, float:0.064)
            float r4 = r4 + r9
            r13.progressToState = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x00d5
            r13.progressToState = r6
            r4 = 0
            r13.previousState = r4
        L_0x00d5:
            r13.invalidate()
        L_0x00d8:
            boolean r4 = r13.prepareToRemove
            r9 = 1027292903(0x3d3b3ee7, float:0.NUM)
            if (r4 == 0) goto L_0x00f2
            float r10 = r13.progressToPrepareRemove
            int r11 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x00f2
            float r10 = r10 + r9
            r13.progressToPrepareRemove = r10
            int r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x00ee
            r13.progressToPrepareRemove = r6
        L_0x00ee:
            r13.invalidate()
            goto L_0x0106
        L_0x00f2:
            if (r4 != 0) goto L_0x0106
            float r4 = r13.progressToPrepareRemove
            int r10 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x0106
            float r4 = r4 - r9
            r13.progressToPrepareRemove = r4
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0103
            r13.progressToPrepareRemove = r5
        L_0x0103:
            r13.invalidate()
        L_0x0106:
            org.telegram.ui.Components.BlobDrawable r4 = r13.blobDrawable
            float r9 = r13.amplitude
            boolean r10 = r13.stub
            r11 = 1061997773(0x3f4ccccd, float:0.8)
            if (r10 == 0) goto L_0x0115
            r10 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x0118
        L_0x0115:
            r10 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x0118:
            r4.update(r9, r10)
            org.telegram.ui.Components.BlobDrawable r4 = r13.blobDrawable2
            float r9 = r13.amplitude
            boolean r10 = r13.stub
            if (r10 == 0) goto L_0x0124
            goto L_0x0127
        L_0x0124:
            r7 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x0127:
            r4.update(r9, r7)
            r4 = 0
        L_0x012b:
            r7 = 3
            if (r4 >= r7) goto L_0x027f
            if (r4 != 0) goto L_0x0136
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r13.previousState
            if (r7 != 0) goto L_0x0136
            goto L_0x027b
        L_0x0136:
            r9 = 16
            if (r4 != 0) goto L_0x0157
            float r7 = r13.progressToPrepareRemove
            int r7 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r7 != 0) goto L_0x0142
            goto L_0x027b
        L_0x0142:
            float r7 = r13.progressToState
            float r7 = r6 - r7
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r13.previousState
            float r12 = r13.amplitude
            r11.update(r9, r12)
            android.graphics.Paint r9 = r13.paint
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r10 = r13.previousState
            android.graphics.Shader r10 = r10.shader
            r9.setShader(r10)
            goto L_0x01bd
        L_0x0157:
            if (r4 != r1) goto L_0x017f
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r13.currentState
            if (r7 != 0) goto L_0x015e
            return
        L_0x015e:
            float r11 = r13.progressToPrepareRemove
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 != 0) goto L_0x0166
            goto L_0x027b
        L_0x0166:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r13.previousState
            if (r11 == 0) goto L_0x016d
            float r11 = r13.progressToState
            goto L_0x016f
        L_0x016d:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x016f:
            float r12 = r13.amplitude
            r7.update(r9, r12)
            android.graphics.Paint r7 = r13.paint
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r13.currentState
            android.graphics.Shader r9 = r9.shader
            r7.setShader(r9)
            r7 = r11
            goto L_0x01bd
        L_0x017f:
            float r7 = r13.progressToPrepareRemove
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x0187
            goto L_0x027b
        L_0x0187:
            android.graphics.Paint r7 = r13.paint
            r9 = -65536(0xfffffffffffvar_, float:NaN)
            r7.setColor(r9)
            android.graphics.Matrix r7 = r13.matrix
            r7.reset()
            android.graphics.Matrix r7 = r13.matrix
            r9 = 1132068864(0x437a0000, float:250.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = -r9
            float r9 = (float) r9
            float r10 = r13.progressToPrepareRemove
            float r10 = r6 - r10
            float r9 = r9 * r10
            r7.postTranslate(r9, r5)
            android.graphics.Matrix r7 = r13.matrix
            float r9 = r13.removeAngle
            r7.postRotate(r9, r0, r2)
            android.graphics.LinearGradient r7 = r13.prepareToRemoveShader
            android.graphics.Matrix r9 = r13.matrix
            r7.setLocalMatrix(r9)
            android.graphics.Paint r7 = r13.paint
            android.graphics.LinearGradient r9 = r13.prepareToRemoveShader
            r7.setShader(r9)
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x01bd:
            org.telegram.ui.Components.BlobDrawable r9 = r13.blobDrawable
            r10 = 1110704128(0x42340000, float:45.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r11 = (float) r11
            r9.maxRadius = r11
            org.telegram.ui.Components.BlobDrawable r9 = r13.blobDrawable
            r11 = 1108869120(0x42180000, float:38.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r12 = (float) r12
            r9.minRadius = r12
            org.telegram.ui.Components.BlobDrawable r9 = r13.blobDrawable2
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9.maxRadius = r10
            org.telegram.ui.Components.BlobDrawable r9 = r13.blobDrawable2
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r10 = (float) r10
            r9.minRadius = r10
            r9 = 1117257728(0x42980000, float:76.0)
            r10 = 2
            if (r4 == r10) goto L_0x01f9
            android.graphics.Paint r11 = r13.paint
            float r9 = r9 * r7
            float r12 = r13.progressToPrepareRemove
            float r12 = r6 - r12
            float r9 = r9 * r12
            int r9 = (int) r9
            r11.setAlpha(r9)
            goto L_0x0205
        L_0x01f9:
            android.graphics.Paint r11 = r13.paint
            float r9 = r9 * r7
            float r12 = r13.progressToPrepareRemove
            float r9 = r9 * r12
            int r9 = (int) r9
            r11.setAlpha(r9)
        L_0x0205:
            r9 = 1050253722(0x3e99999a, float:0.3)
            float r11 = r13.amplitude
            float r11 = r11 * r9
            float r11 = r11 + r6
            float r11 = r11 + r3
            r9 = 1067869798(0x3fa66666, float:1.3)
            float r11 = java.lang.Math.min(r11, r9)
            r14.save()
            r14.scale(r11, r11, r0, r2)
            org.telegram.ui.Components.BlobDrawable r11 = r13.blobDrawable
            android.graphics.Paint r12 = r13.paint
            r11.draw(r0, r2, r14, r12)
            r14.restore()
            r11 = 1048911544(0x3e851eb8, float:0.26)
            float r12 = r13.amplitude
            float r12 = r12 * r11
            float r12 = r12 + r6
            float r12 = r12 + r3
            float r9 = java.lang.Math.min(r12, r9)
            r14.save()
            r14.scale(r9, r9, r0, r2)
            org.telegram.ui.Components.BlobDrawable r9 = r13.blobDrawable2
            android.graphics.Paint r11 = r13.paint
            r9.draw(r0, r2, r14, r11)
            r14.restore()
            r9 = 1132396544(0x437var_, float:255.0)
            if (r4 != r10) goto L_0x0251
            android.graphics.Paint r7 = r13.paint
            float r10 = r13.progressToPrepareRemove
            float r10 = r10 * r9
            int r9 = (int) r10
            r7.setAlpha(r9)
            goto L_0x0263
        L_0x0251:
            if (r4 != r1) goto L_0x025c
            android.graphics.Paint r10 = r13.paint
            float r7 = r7 * r9
            int r7 = (int) r7
            r10.setAlpha(r7)
            goto L_0x0263
        L_0x025c:
            android.graphics.Paint r7 = r13.paint
            r9 = 255(0xff, float:3.57E-43)
            r7.setAlpha(r9)
        L_0x0263:
            r14.save()
            r14.scale(r8, r8, r0, r2)
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            android.graphics.Paint r9 = r13.paint
            r14.drawCircle(r0, r2, r7, r9)
            r14.restore()
            r13.invalidate()
        L_0x027b:
            int r4 = r4 + 1
            goto L_0x012b
        L_0x027f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipButton.onDraw(android.graphics.Canvas):void");
    }

    private void setAmplitude(double d) {
        float min = (float) (Math.min(8500.0d, d) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.stub) {
            setAmplitude(0.0d);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            int i = (VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().isMicMute()) ? 0 : 1;
            setState(i);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            this.bigMicDrawable.setCustomEndFrame(i != 0 ? 12 : 0);
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.stub) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().unregisterStateListener(this);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            setAmplitude((double) (objArr[0].floatValue() * 4000.0f));
        }
    }

    public void onAudioSettingsChanged() {
        int i = 0;
        int i2 = (VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().isMicMute()) ? 0 : 1;
        setState(i2);
        RLottieDrawable rLottieDrawable = this.bigMicDrawable;
        if (i2 != 0) {
            i = 12;
        }
        rLottieDrawable.setCustomEndFrame(i);
        this.muteButton.playAnimation();
    }

    public void setRemoveAngle(double d) {
        this.removeAngle = (float) d;
    }

    public void prepareToRemove(boolean z) {
        this.prepareToRemove = z;
        invalidate();
    }
}
