package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.ActionBar.Theme;

public class GroupCallPipButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
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
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    Paint paint = new Paint(1);
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
    public boolean removed;
    WeavingState[] states = new WeavingState[4];
    private boolean stub;
    float wavesEnter = 0.0f;

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
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
        this.bigMicDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(22.0f), AndroidUtilities.dp(30.0f), true, (int[]) null);
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
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02e1  */
    /* JADX WARNING: Removed duplicated region for block: B:129:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x016c  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0174 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            super.onDraw(r19)
            float r2 = r18.getAlpha()
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x0011
            return
        L_0x0011:
            int r2 = r18.getMeasuredWidth()
            r4 = 1
            int r2 = r2 >> r4
            float r2 = (float) r2
            int r5 = r18.getMeasuredHeight()
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
            if (r7 == 0) goto L_0x00ef
            float r12 = r0.progressToPrepareRemove
            int r13 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x00ef
            float r12 = r12 + r11
            r0.progressToPrepareRemove = r12
            int r7 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x00e7
            r0.progressToPrepareRemove = r8
        L_0x00e7:
            boolean r7 = r0.removed
            if (r7 == 0) goto L_0x0100
            r18.invalidate()
            goto L_0x0100
        L_0x00ef:
            if (r7 != 0) goto L_0x0100
            float r7 = r0.progressToPrepareRemove
            int r12 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r12 == 0) goto L_0x0100
            float r7 = r7 - r11
            r0.progressToPrepareRemove = r7
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x0100
            r0.progressToPrepareRemove = r3
        L_0x0100:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.currentState
            int r7 = r7.currentState
            r12 = 0
            r13 = 3
            r14 = 2
            if (r7 == r13) goto L_0x0116
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.currentState
            int r7 = r7.currentState
            if (r7 != r14) goto L_0x0114
            goto L_0x0116
        L_0x0114:
            r7 = 1
            goto L_0x0117
        L_0x0116:
            r7 = 0
        L_0x0117:
            if (r7 == 0) goto L_0x0129
            float r15 = r0.wavesEnter
            int r16 = (r15 > r8 ? 1 : (r15 == r8 ? 0 : -1))
            if (r16 == 0) goto L_0x0129
            float r15 = r15 + r11
            r0.wavesEnter = r15
            int r7 = (r15 > r8 ? 1 : (r15 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x013a
            r0.wavesEnter = r8
            goto L_0x013a
        L_0x0129:
            if (r7 != 0) goto L_0x013a
            float r7 = r0.wavesEnter
            int r15 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r15 == 0) goto L_0x013a
            float r7 = r7 - r11
            r0.wavesEnter = r7
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x013a
            r0.wavesEnter = r3
        L_0x013a:
            r7 = 1059481190(0x3var_, float:0.65)
            r11 = 1051931443(0x3eb33333, float:0.35)
            android.view.animation.OvershootInterpolator r15 = r0.overshootInterpolator
            float r9 = r0.wavesEnter
            float r9 = r15.getInterpolation(r9)
            float r9 = r9 * r11
            float r9 = r9 + r7
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable
            float r11 = r0.amplitude
            boolean r15 = r0.stub
            r17 = 1061997773(0x3f4ccccd, float:0.8)
            if (r15 == 0) goto L_0x015a
            r15 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x015d
        L_0x015a:
            r15 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x015d:
            r7.update(r11, r15)
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable2
            float r11 = r0.amplitude
            boolean r15 = r0.stub
            if (r15 == 0) goto L_0x016c
            r15 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x016f
        L_0x016c:
            r15 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x016f:
            r7.update(r11, r15)
        L_0x0172:
            if (r12 >= r13) goto L_0x02d7
            if (r12 != 0) goto L_0x017d
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.previousState
            if (r7 != 0) goto L_0x017d
        L_0x017a:
            r11 = 2
            goto L_0x02d1
        L_0x017d:
            r13 = 16
            if (r12 != 0) goto L_0x019b
            float r15 = r0.progressToPrepareRemove
            int r15 = (r15 > r8 ? 1 : (r15 == r8 ? 0 : -1))
            if (r15 != 0) goto L_0x0188
            goto L_0x017a
        L_0x0188:
            float r15 = r0.progressToState
            float r15 = r8 - r15
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.previousState
            float r11 = r0.amplitude
            r7.update(r13, r11)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.previousState
            android.graphics.Paint r11 = r0.paint
            r7.setToPaint(r11)
            goto L_0x01fd
        L_0x019b:
            if (r12 != r4) goto L_0x01c0
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.currentState
            if (r7 != 0) goto L_0x01a2
            return
        L_0x01a2:
            float r11 = r0.progressToPrepareRemove
            int r11 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r11 != 0) goto L_0x01a9
            goto L_0x017a
        L_0x01a9:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.previousState
            if (r11 == 0) goto L_0x01b1
            float r11 = r0.progressToState
            r15 = r11
            goto L_0x01b3
        L_0x01b1:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x01b3:
            float r11 = r0.amplitude
            r7.update(r13, r11)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.currentState
            android.graphics.Paint r11 = r0.paint
            r7.setToPaint(r11)
            goto L_0x01fd
        L_0x01c0:
            float r7 = r0.progressToPrepareRemove
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x01c7
            goto L_0x017a
        L_0x01c7:
            android.graphics.Paint r7 = r0.paint
            r11 = -65536(0xfffffffffffvar_, float:NaN)
            r7.setColor(r11)
            android.graphics.Matrix r7 = r0.matrix
            r7.reset()
            android.graphics.Matrix r7 = r0.matrix
            r11 = 1132068864(0x437a0000, float:250.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r11 = (float) r11
            float r13 = r0.progressToPrepareRemove
            float r13 = r8 - r13
            float r11 = r11 * r13
            r7.postTranslate(r11, r3)
            android.graphics.Matrix r7 = r0.matrix
            float r11 = r0.removeAngle
            r7.postRotate(r11, r2, r5)
            android.graphics.LinearGradient r7 = r0.prepareToRemoveShader
            android.graphics.Matrix r11 = r0.matrix
            r7.setLocalMatrix(r11)
            android.graphics.Paint r7 = r0.paint
            android.graphics.LinearGradient r11 = r0.prepareToRemoveShader
            r7.setShader(r11)
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x01fd:
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable
            r11 = 1109393408(0x42200000, float:40.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r7.maxRadius = r11
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable
            r13 = 1107296256(0x42000000, float:32.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r11 = (float) r11
            r7.minRadius = r11
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable2
            r11 = 1108869120(0x42180000, float:38.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r7.maxRadius = r11
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable2
            r11 = 1107558400(0x42040000, float:33.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r7.minRadius = r11
            r7 = 1117257728(0x42980000, float:76.0)
            r11 = 2
            if (r12 == r11) goto L_0x023d
            android.graphics.Paint r14 = r0.paint
            float r7 = r7 * r15
            float r11 = r0.progressToPrepareRemove
            float r11 = r8 - r11
            float r7 = r7 * r11
            int r7 = (int) r7
            r14.setAlpha(r7)
            goto L_0x0249
        L_0x023d:
            android.graphics.Paint r11 = r0.paint
            float r7 = r7 * r15
            float r14 = r0.progressToPrepareRemove
            float r7 = r7 * r14
            int r7 = (int) r7
            r11.setAlpha(r7)
        L_0x0249:
            float r7 = r0.wavesEnter
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x029c
            r7 = 1050253722(0x3e99999a, float:0.3)
            float r11 = r0.amplitude
            float r11 = r11 * r7
            float r11 = r11 + r8
            float r11 = r11 + r6
            float r7 = r0.pinnedProgress
            float r7 = r8 - r7
            float r11 = r11 * r7
            r7 = 1067869798(0x3fa66666, float:1.3)
            float r11 = java.lang.Math.min(r11, r7)
            float r11 = r11 * r9
            r19.save()
            r1.scale(r11, r11, r2, r5)
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable
            android.graphics.Paint r14 = r0.paint
            r11.draw(r2, r5, r1, r14)
            r19.restore()
            r11 = 1048911544(0x3e851eb8, float:0.26)
            float r14 = r0.amplitude
            float r14 = r14 * r11
            float r14 = r14 + r8
            float r14 = r14 + r6
            float r11 = r0.pinnedProgress
            float r11 = r8 - r11
            float r14 = r14 * r11
            float r7 = java.lang.Math.min(r14, r7)
            float r7 = r7 * r9
            r19.save()
            r1.scale(r7, r7, r2, r5)
            org.telegram.ui.Components.BlobDrawable r7 = r0.blobDrawable2
            android.graphics.Paint r11 = r0.paint
            r7.draw(r2, r5, r1, r11)
            r19.restore()
        L_0x029c:
            r7 = 1132396544(0x437var_, float:255.0)
            r11 = 2
            if (r12 != r11) goto L_0x02ac
            android.graphics.Paint r14 = r0.paint
            float r15 = r0.progressToPrepareRemove
            float r15 = r15 * r7
            int r7 = (int) r15
            r14.setAlpha(r7)
            goto L_0x02be
        L_0x02ac:
            if (r12 != r4) goto L_0x02b7
            android.graphics.Paint r14 = r0.paint
            float r15 = r15 * r7
            int r7 = (int) r15
            r14.setAlpha(r7)
            goto L_0x02be
        L_0x02b7:
            android.graphics.Paint r7 = r0.paint
            r14 = 255(0xff, float:3.57E-43)
            r7.setAlpha(r14)
        L_0x02be:
            r19.save()
            r1.scale(r10, r10, r2, r5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r7 = (float) r7
            android.graphics.Paint r13 = r0.paint
            r1.drawCircle(r2, r5, r7, r13)
            r19.restore()
        L_0x02d1:
            int r12 = r12 + 1
            r13 = 3
            r14 = 2
            goto L_0x0172
        L_0x02d7:
            boolean r1 = r0.removed
            if (r1 != 0) goto L_0x02e4
            float r1 = r0.wavesEnter
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x02e4
            r18.invalidate()
        L_0x02e4:
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
        String str;
        WeavingState weavingState = this.currentState;
        if (weavingState == null || weavingState.currentState != i) {
            WeavingState weavingState2 = this.currentState;
            this.previousState = weavingState2;
            WeavingState weavingState3 = this.states[i];
            this.currentState = weavingState3;
            float f = 0.0f;
            if (weavingState2 != null) {
                this.progressToState = 0.0f;
            } else {
                this.progressToState = 1.0f;
                boolean z = true;
                if (weavingState3.currentState == 3 || this.currentState.currentState == 2) {
                    z = false;
                }
                if (z) {
                    f = 1.0f;
                }
                this.wavesEnter = f;
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance == null || !ChatObject.isChannelOrGiga(sharedInstance.getChat())) {
                str = LocaleController.getString("VoipGroupVoiceChat", NUM);
            } else {
                str = LocaleController.getString("VoipChannelVoiceChat", NUM);
            }
            if (i == 0) {
                str = str + ", " + LocaleController.getString("VoipTapToMute", NUM);
            } else if (i == 2) {
                str = str + ", " + LocaleController.getString("Connecting", NUM);
            } else if (i == 3) {
                str = str + ", " + LocaleController.getString("VoipMutedByAdmin", NUM);
            }
            setContentDescription(str);
            invalidate();
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (Build.VERSION.SDK_INT >= 21 && GroupCallPip.getInstance() != null) {
            if (GroupCallPip.getInstance().showAlert) {
                i = NUM;
                str = "AccDescrCloseMenu";
            } else {
                i = NUM;
                str = "AccDescrOpenMenu2";
            }
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
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
            this.bigMicDrawable.setCustomEndFrame(z ? 13 : 24);
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            updateButtonState();
        }
    }

    private void updateButtonState() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.groupCall != null) {
            int callState = sharedInstance.getCallState();
            if (callState == 1 || callState == 2 || callState == 6 || callState == 5) {
                setState(2);
                return;
            }
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = sharedInstance.groupCall.participants.get(sharedInstance.getSelfId());
            if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(sharedInstance.getChat())) {
                setState(sharedInstance.isMicMute() ? 1 : 0);
                return;
            }
            if (!sharedInstance.isMicMute()) {
                sharedInstance.setMicMute(true, false, false);
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
        boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.bigMicDrawable.setCustomEndFrame(z ? 13 : 24)) {
            if (z) {
                this.bigMicDrawable.setCurrentFrame(0);
            } else {
                this.bigMicDrawable.setCurrentFrame(12);
            }
        }
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
