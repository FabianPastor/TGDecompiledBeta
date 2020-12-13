package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Random;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.ActionBar.Theme;

public class GroupCallPipButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener {
    float amplitude;
    float animateAmplitudeDiff;
    float animateToAmplitude;
    private RLottieDrawable bigMicDrawable;
    BlobDrawable blobDrawable = new BlobDrawable(8);
    BlobDrawable blobDrawable2 = new BlobDrawable(9);
    private final int currentAccount;
    WeavingState currentState;
    long lastStubUpdateAmplitude;
    Matrix matrix = new Matrix();
    private RLottieImageView muteButton;
    Paint paint = new Paint(1);
    WeavingState pausedState;
    float pinnedProgress;
    boolean prepareToRemove;
    private final LinearGradient prepareToRemoveShader;
    float pressedProgress;
    boolean pressedState;
    WeavingState previousState;
    float progressToPrepareRemove;
    float progressToState = 1.0f;
    Random random = new Random();
    float removeAngle;
    WeavingState[] states = new WeavingState[4];
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

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public GroupCallPipButton(Context context, int i, boolean z) {
        super(context);
        this.stub = z;
        this.currentAccount = i;
        for (int i2 = 0; i2 < 4; i2++) {
            this.states[i2] = new WeavingState(i2);
        }
        this.blobDrawable.maxRadius = (float) AndroidUtilities.dp(37.0f);
        this.blobDrawable.minRadius = (float) AndroidUtilities.dp(32.0f);
        this.blobDrawable2.maxRadius = (float) AndroidUtilities.dp(37.0f);
        this.blobDrawable2.minRadius = (float) AndroidUtilities.dp(32.0f);
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

    public void setPinnedProgress(float f) {
        this.pinnedProgress = f;
    }

    public static class WeavingState {
        int color1;
        int color2;
        int color3;
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
            int i = this.currentState;
            if (i == 0) {
                if (!(this.color1 == Theme.getColor("voipgroup_overlayGreen1") && this.color2 == Theme.getColor("voipgroup_overlayGreen2"))) {
                    int color = Theme.getColor("voipgroup_overlayGreen1");
                    this.color1 = color;
                    int color4 = Theme.getColor("voipgroup_overlayGreen2");
                    this.color2 = color4;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color4}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (i == 1) {
                if (!(this.color1 == Theme.getColor("voipgroup_overlayBlue1") && this.color2 == Theme.getColor("voipgroup_overlayBlue2"))) {
                    int color5 = Theme.getColor("voipgroup_overlayBlue1");
                    this.color1 = color5;
                    int color6 = Theme.getColor("voipgroup_overlayBlue2");
                    this.color2 = color6;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color5, color6}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (i != 3) {
                return;
            } else {
                if (!(this.color1 == Theme.getColor("voipgroup_mutedByAdminGradient") && this.color2 == Theme.getColor("voipgroup_mutedByAdminGradient2") && this.color3 == Theme.getColor("voipgroup_mutedByAdminGradient3"))) {
                    int color7 = Theme.getColor("voipgroup_mutedByAdminGradient2");
                    this.color2 = color7;
                    int color8 = Theme.getColor("voipgroup_mutedByAdminGradient3");
                    this.color3 = color8;
                    int color9 = Theme.getColor("voipgroup_mutedByAdminGradient");
                    this.color1 = color9;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color7, color8, color9}, (float[]) null, Shader.TileMode.CLAMP);
                }
            }
            int dp = AndroidUtilities.dp(130.0f);
            float f2 = this.duration;
            if (f2 == 0.0f || this.time >= f2) {
                this.duration = (float) (Utilities.random.nextInt(700) + 500);
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    updateTargets();
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                updateTargets();
            }
            float f3 = (float) j;
            float f4 = 2.0f;
            float f5 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f3) + (f3 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * f);
            this.time = f5;
            float f6 = this.duration;
            if (f5 > f6) {
                this.time = f6;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f6);
            float f7 = (float) dp;
            float f8 = this.startX;
            float f9 = ((f8 + ((this.targetX - f8) * interpolation)) * f7) - 200.0f;
            float var_ = this.startY;
            float var_ = ((var_ + ((this.targetY - var_) * interpolation)) * f7) - 200.0f;
            if (this.currentState != 3) {
                f4 = 1.5f;
            }
            float var_ = (f7 / 400.0f) * f4;
            this.matrix.reset();
            this.matrix.postTranslate(f9, var_);
            this.matrix.postScale(var_, var_, f9 + 200.0f, var_ + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }

        private void updateTargets() {
            int i = this.currentState;
            if (i == 0) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.1f) / 100.0f) + 0.2f;
                this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.1f) / 100.0f) + 0.7f;
            } else if (i == 3) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.1f) / 100.0f) + 0.6f;
                this.targetY = (((float) Utilities.random.nextInt(100)) * 0.1f) / 100.0f;
            } else {
                this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
            }
        }

        public void setToPaint(Paint paint) {
            if (this.currentState == 2) {
                paint.setShader((Shader) null);
                paint.setColor(Theme.getColor("voipgroup_topPanelGray"));
                return;
            }
            paint.setShader(this.shader);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0121  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            super.onDraw(r18)
            float r2 = r17.getAlpha()
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x0011
            return
        L_0x0011:
            int r2 = r17.getMeasuredWidth()
            r4 = 1
            int r2 = r2 >> r4
            float r2 = (float) r2
            int r5 = r17.getMeasuredHeight()
            int r5 = r5 >> r4
            float r5 = (float) r5
            boolean r6 = r0.pressedState
            r7 = 1037726734(0x3dda740e, float:0.10666667)
            r8 = 1065353216(0x3var_, float:1.0)
            if (r6 == 0) goto L_0x0037
            float r9 = r0.pressedProgress
            int r10 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x0037
            float r9 = r9 + r7
            r0.pressedProgress = r9
            int r6 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x0048
            r0.pressedProgress = r8
            goto L_0x0048
        L_0x0037:
            if (r6 != 0) goto L_0x0048
            float r6 = r0.pressedProgress
            int r9 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r9 == 0) goto L_0x0048
            float r6 = r6 - r7
            r0.pressedProgress = r6
            int r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x0048
            r0.pressedProgress = r3
        L_0x0048:
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r7 = r0.pressedProgress
            float r6 = r6.getInterpolation(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            r9 = 1036831949(0x3dcccccd, float:0.1)
            float r6 = r6 * r9
            float r10 = r6 + r8
            r7.setScaleY(r10)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            r7.setScaleX(r10)
            boolean r7 = r0.stub
            if (r7 == 0) goto L_0x009a
            long r11 = java.lang.System.currentTimeMillis()
            long r13 = r0.lastStubUpdateAmplitude
            long r13 = r11 - r13
            r15 = 1000(0x3e8, double:4.94E-321)
            int r7 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r7 <= 0) goto L_0x009a
            r0.lastStubUpdateAmplitude = r11
            java.util.Random r7 = r0.random
            int r7 = r7.nextInt()
            int r7 = r7 % 100
            int r7 = java.lang.Math.abs(r7)
            float r7 = (float) r7
            r11 = 1056964608(0x3var_, float:0.5)
            float r7 = r7 * r11
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r7 = r7 / r12
            float r7 = r7 + r11
            r0.animateToAmplitude = r7
            float r11 = r0.amplitude
            float r7 = r7 - r11
            r11 = 1153138688(0x44bb8000, float:1500.0)
            float r13 = org.telegram.ui.Components.BlobDrawable.AMPLITUDE_SPEED
            float r13 = r13 * r11
            float r13 = r13 + r12
            float r7 = r7 / r13
            r0.animateAmplitudeDiff = r7
        L_0x009a:
            float r7 = r0.animateToAmplitude
            float r11 = r0.amplitude
            int r12 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x00bc
            float r12 = r0.animateAmplitudeDiff
            r13 = 1098907648(0x41800000, float:16.0)
            float r13 = r13 * r12
            float r11 = r11 + r13
            r0.amplitude = r11
            int r12 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r12 <= 0) goto L_0x00b6
            int r11 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r11 <= 0) goto L_0x00bc
            r0.amplitude = r7
            goto L_0x00bc
        L_0x00b6:
            int r11 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r11 >= 0) goto L_0x00bc
            r0.amplitude = r7
        L_0x00bc:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.previousState
            if (r7 == 0) goto L_0x00d1
            float r7 = r0.progressToState
            r11 = 1032000111(0x3d83126f, float:0.064)
            float r7 = r7 + r11
            r0.progressToState = r7
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x00d1
            r0.progressToState = r8
            r7 = 0
            r0.previousState = r7
        L_0x00d1:
            boolean r7 = r0.prepareToRemove
            r11 = 1027292903(0x3d3b3ee7, float:0.NUM)
            if (r7 == 0) goto L_0x00e8
            float r12 = r0.progressToPrepareRemove
            int r13 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x00e8
            float r12 = r12 + r11
            r0.progressToPrepareRemove = r12
            int r7 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x00f9
            r0.progressToPrepareRemove = r8
            goto L_0x00f9
        L_0x00e8:
            if (r7 != 0) goto L_0x00f9
            float r7 = r0.progressToPrepareRemove
            int r12 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r12 == 0) goto L_0x00f9
            float r7 = r7 - r11
            r0.progressToPrepareRemove = r7
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x00f9
            r0.progressToPrepareRemove = r3
        L_0x00f9:
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable
            float r11 = r0.amplitude
            boolean r12 = r0.stub
            r13 = 1061997773(0x3f4ccccd, float:0.8)
            if (r12 == 0) goto L_0x0108
            r12 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x010b
        L_0x0108:
            r12 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x010b:
            r7.update(r11, r12)
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable2
            float r11 = r0.amplitude
            boolean r12 = r0.stub
            if (r12 == 0) goto L_0x0117
            goto L_0x011a
        L_0x0117:
            r9 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x011a:
            r7.update(r11, r9)
            r7 = 0
        L_0x011e:
            r9 = 3
            if (r7 >= r9) goto L_0x0279
            if (r7 != 0) goto L_0x0129
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.previousState
            if (r9 != 0) goto L_0x0129
            goto L_0x0275
        L_0x0129:
            r11 = 16
            if (r7 != 0) goto L_0x0148
            float r9 = r0.progressToPrepareRemove
            int r9 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r9 != 0) goto L_0x0135
            goto L_0x0275
        L_0x0135:
            float r9 = r0.progressToState
            float r9 = r8 - r9
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r13 = r0.previousState
            float r14 = r0.amplitude
            r13.update(r11, r14)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.previousState
            android.graphics.Paint r12 = r0.paint
            r11.setToPaint(r12)
            goto L_0x01ac
        L_0x0148:
            if (r7 != r4) goto L_0x016e
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            if (r9 != 0) goto L_0x014f
            return
        L_0x014f:
            float r13 = r0.progressToPrepareRemove
            int r13 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r13 != 0) goto L_0x0157
            goto L_0x0275
        L_0x0157:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r13 = r0.previousState
            if (r13 == 0) goto L_0x015e
            float r13 = r0.progressToState
            goto L_0x0160
        L_0x015e:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0160:
            float r14 = r0.amplitude
            r9.update(r11, r14)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            android.graphics.Paint r11 = r0.paint
            r9.setToPaint(r11)
            r9 = r13
            goto L_0x01ac
        L_0x016e:
            float r9 = r0.progressToPrepareRemove
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 != 0) goto L_0x0176
            goto L_0x0275
        L_0x0176:
            android.graphics.Paint r9 = r0.paint
            r11 = -65536(0xfffffffffffvar_, float:NaN)
            r9.setColor(r11)
            android.graphics.Matrix r9 = r0.matrix
            r9.reset()
            android.graphics.Matrix r9 = r0.matrix
            r11 = 1132068864(0x437a0000, float:250.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r11 = (float) r11
            float r12 = r0.progressToPrepareRemove
            float r12 = r8 - r12
            float r11 = r11 * r12
            r9.postTranslate(r11, r3)
            android.graphics.Matrix r9 = r0.matrix
            float r11 = r0.removeAngle
            r9.postRotate(r11, r2, r5)
            android.graphics.LinearGradient r9 = r0.prepareToRemoveShader
            android.graphics.Matrix r11 = r0.matrix
            r9.setLocalMatrix(r11)
            android.graphics.Paint r9 = r0.paint
            android.graphics.LinearGradient r11 = r0.prepareToRemoveShader
            r9.setShader(r11)
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01ac:
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable
            r12 = 1109393408(0x42200000, float:40.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r11.maxRadius = r12
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable
            r12 = 1107296256(0x42000000, float:32.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            r11.minRadius = r13
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable2
            r13 = 1108869120(0x42180000, float:38.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r11.maxRadius = r13
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable2
            r13 = 1107558400(0x42040000, float:33.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r11.minRadius = r13
            r11 = 1117257728(0x42980000, float:76.0)
            r13 = 2
            if (r7 == r13) goto L_0x01ec
            android.graphics.Paint r14 = r0.paint
            float r11 = r11 * r9
            float r15 = r0.progressToPrepareRemove
            float r15 = r8 - r15
            float r11 = r11 * r15
            int r11 = (int) r11
            r14.setAlpha(r11)
            goto L_0x01f8
        L_0x01ec:
            android.graphics.Paint r14 = r0.paint
            float r11 = r11 * r9
            float r15 = r0.progressToPrepareRemove
            float r11 = r11 * r15
            int r11 = (int) r11
            r14.setAlpha(r11)
        L_0x01f8:
            r11 = 1050253722(0x3e99999a, float:0.3)
            float r14 = r0.amplitude
            float r14 = r14 * r11
            float r14 = r14 + r8
            float r14 = r14 + r6
            float r11 = r0.pinnedProgress
            float r11 = r8 - r11
            float r14 = r14 * r11
            r11 = 1067869798(0x3fa66666, float:1.3)
            float r14 = java.lang.Math.min(r14, r11)
            r18.save()
            r1.scale(r14, r14, r2, r5)
            org.telegram.ui.Components.BlobDrawable r14 = r0.blobDrawable
            android.graphics.Paint r15 = r0.paint
            r14.draw(r2, r5, r1, r15)
            r18.restore()
            r14 = 1048911544(0x3e851eb8, float:0.26)
            float r15 = r0.amplitude
            float r15 = r15 * r14
            float r15 = r15 + r8
            float r15 = r15 + r6
            float r14 = r0.pinnedProgress
            float r14 = r8 - r14
            float r15 = r15 * r14
            float r11 = java.lang.Math.min(r15, r11)
            r18.save()
            r1.scale(r11, r11, r2, r5)
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable2
            android.graphics.Paint r14 = r0.paint
            r11.draw(r2, r5, r1, r14)
            r18.restore()
            r11 = 1132396544(0x437var_, float:255.0)
            if (r7 != r13) goto L_0x0250
            android.graphics.Paint r9 = r0.paint
            float r13 = r0.progressToPrepareRemove
            float r13 = r13 * r11
            int r11 = (int) r13
            r9.setAlpha(r11)
            goto L_0x0262
        L_0x0250:
            if (r7 != r4) goto L_0x025b
            android.graphics.Paint r13 = r0.paint
            float r9 = r9 * r11
            int r9 = (int) r9
            r13.setAlpha(r9)
            goto L_0x0262
        L_0x025b:
            android.graphics.Paint r9 = r0.paint
            r11 = 255(0xff, float:3.57E-43)
            r9.setAlpha(r11)
        L_0x0262:
            r18.save()
            r1.scale(r10, r10, r2, r5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            android.graphics.Paint r11 = r0.paint
            r1.drawCircle(r2, r5, r9, r11)
            r18.restore()
        L_0x0275:
            int r7 = r7 + 1
            goto L_0x011e
        L_0x0279:
            r17.invalidate()
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            this.bigMicDrawable.setCustomEndFrame(z ? 12 : 0);
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
            updateButtonState();
        }
    }

    private void updateButtonState() {
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null) {
            int callState = VoIPService.getSharedInstance().getCallState();
            if (callState == 1 || callState == 2 || callState == 6 || callState == 5) {
                setState(2);
                return;
            }
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = VoIPService.getSharedInstance().groupCall.participants.get(AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()).getUserConfig().getClientUserId());
            if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(VoIPService.getSharedInstance().getChat())) {
                setState(VoIPService.getSharedInstance().isMicMute() ? 1 : 0);
                return;
            }
            if (!VoIPService.getSharedInstance().isMicMute()) {
                VoIPService.getSharedInstance().setMicMute(true, false, false);
            }
            setState(3);
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            if (getParent() != null) {
                ((View) getParent()).dispatchTouchEvent(obtain);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.stub) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().unregisterStateListener(this);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            setAmplitude((double) (objArr[0].floatValue() * 4000.0f));
        } else if (i == NotificationCenter.groupCallUpdated) {
            updateButtonState();
        }
    }

    public void onAudioSettingsChanged() {
        int i = 0;
        boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        RLottieDrawable rLottieDrawable = this.bigMicDrawable;
        if (z) {
            i = 12;
        }
        rLottieDrawable.setCustomEndFrame(i);
        this.muteButton.playAnimation();
        updateButtonState();
    }

    public void onStateChanged(int i) {
        updateButtonState();
    }

    public void setRemoveAngle(double d) {
        this.removeAngle = (float) d;
    }

    public void prepareToRemove(boolean z) {
        if (this.prepareToRemove != z) {
            invalidate();
        }
        this.prepareToRemove = z;
    }
}
