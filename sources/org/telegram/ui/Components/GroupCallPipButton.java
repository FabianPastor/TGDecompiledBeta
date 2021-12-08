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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class GroupCallPipButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final float MAX_AMPLITUDE = 8500.0f;
    public static final int MUTE_BUTTON_STATE_MUTE = 1;
    public static final int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 3;
    public static final int MUTE_BUTTON_STATE_RECONNECT = 2;
    public static final int MUTE_BUTTON_STATE_UNMUTE = 0;
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

    public GroupCallPipButton(Context context, int currentAccount2, boolean stub2) {
        super(context);
        this.stub = stub2;
        this.currentAccount = currentAccount2;
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
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
        if (stub2) {
            setState(0);
        }
    }

    public void setPressedState(boolean pressedState2) {
        this.pressedState = pressedState2;
    }

    public void setPinnedProgress(float pinnedProgress2) {
        this.pinnedProgress = pinnedProgress2;
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

        public WeavingState(int state) {
            this.currentState = state;
        }

        public void update(long dt, float amplitude) {
            float s;
            long j = dt;
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
            int width = AndroidUtilities.dp(130.0f);
            int height = width;
            float f = this.duration;
            if (f == 0.0f || this.time >= f) {
                this.duration = (float) (Utilities.random.nextInt(700) + 500);
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    updateTargets();
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                updateTargets();
            }
            float f2 = this.time + (((float) j) * (BlobDrawable.GRADIENT_SPEED_MIN + 0.5f)) + (((float) j) * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * amplitude);
            this.time = f2;
            float f3 = this.duration;
            if (f2 > f3) {
                this.time = f3;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / this.duration);
            float f4 = this.startX;
            float x = (((float) width) * (f4 + ((this.targetX - f4) * interpolation))) - 200.0f;
            float f5 = this.startY;
            float y = (((float) height) * (f5 + ((this.targetY - f5) * interpolation))) - 200.0f;
            int i2 = this.currentState;
            if (i2 == 3) {
                s = 2.0f;
            } else if (i2 == 0) {
                s = 1.5f;
            } else {
                s = 1.5f;
            }
            float scale = (((float) width) / 400.0f) * s;
            this.matrix.reset();
            this.matrix.postTranslate(x, y);
            this.matrix.postScale(scale, scale, x + 200.0f, 200.0f + y);
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
    /* JADX WARNING: Removed duplicated region for block: B:124:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:134:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0174  */
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
            float r10 = r6 * r9
            float r10 = r10 + r8
            r7.setScaleY(r10)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            float r10 = r6 * r9
            float r10 = r10 + r8
            r7.setScaleX(r10)
            boolean r7 = r0.stub
            if (r7 == 0) goto L_0x009c
            long r10 = java.lang.System.currentTimeMillis()
            long r12 = r0.lastStubUpdateAmplitude
            long r12 = r10 - r12
            r14 = 1000(0x3e8, double:4.94E-321)
            int r7 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r7 <= 0) goto L_0x009c
            r0.lastStubUpdateAmplitude = r10
            java.util.Random r7 = r0.random
            int r7 = r7.nextInt()
            int r7 = r7 % 100
            int r7 = java.lang.Math.abs(r7)
            float r7 = (float) r7
            r12 = 1056964608(0x3var_, float:0.5)
            float r7 = r7 * r12
            r13 = 1120403456(0x42CLASSNAME, float:100.0)
            float r7 = r7 / r13
            float r7 = r7 + r12
            r0.animateToAmplitude = r7
            float r12 = r0.amplitude
            float r7 = r7 - r12
            r12 = 1153138688(0x44bb8000, float:1500.0)
            float r14 = org.telegram.ui.Components.BlobDrawable.AMPLITUDE_SPEED
            float r14 = r14 * r12
            float r14 = r14 + r13
            float r7 = r7 / r14
            r0.animateAmplitudeDiff = r7
        L_0x009c:
            float r7 = r0.animateToAmplitude
            float r10 = r0.amplitude
            int r11 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r11 == 0) goto L_0x00be
            float r11 = r0.animateAmplitudeDiff
            r12 = 1098907648(0x41800000, float:16.0)
            float r12 = r12 * r11
            float r10 = r10 + r12
            r0.amplitude = r10
            int r11 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r11 <= 0) goto L_0x00b8
            int r10 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r10 <= 0) goto L_0x00be
            r0.amplitude = r7
            goto L_0x00be
        L_0x00b8:
            int r10 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r10 >= 0) goto L_0x00be
            r0.amplitude = r7
        L_0x00be:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r7 = r0.previousState
            if (r7 == 0) goto L_0x00d3
            float r7 = r0.progressToState
            r10 = 1032000111(0x3d83126f, float:0.064)
            float r7 = r7 + r10
            r0.progressToState = r7
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x00d3
            r0.progressToState = r8
            r7 = 0
            r0.previousState = r7
        L_0x00d3:
            boolean r7 = r0.prepareToRemove
            r10 = 1027292903(0x3d3b3ee7, float:0.NUM)
            if (r7 == 0) goto L_0x00f1
            float r11 = r0.progressToPrepareRemove
            int r12 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r12 == 0) goto L_0x00f1
            float r11 = r11 + r10
            r0.progressToPrepareRemove = r11
            int r7 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x00e9
            r0.progressToPrepareRemove = r8
        L_0x00e9:
            boolean r7 = r0.removed
            if (r7 == 0) goto L_0x0102
            r18.invalidate()
            goto L_0x0102
        L_0x00f1:
            if (r7 != 0) goto L_0x0102
            float r7 = r0.progressToPrepareRemove
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x0102
            float r7 = r7 - r10
            r0.progressToPrepareRemove = r7
            int r7 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x0102
            r0.progressToPrepareRemove = r3
        L_0x0102:
            r7 = 1
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.currentState
            int r11 = r11.currentState
            r12 = 3
            r13 = 2
            if (r11 == r12) goto L_0x0115
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.currentState
            int r11 = r11.currentState
            if (r11 != r13) goto L_0x0116
        L_0x0115:
            r7 = 0
        L_0x0116:
            if (r7 == 0) goto L_0x0128
            float r11 = r0.wavesEnter
            int r14 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x0128
            float r11 = r11 + r10
            r0.wavesEnter = r11
            int r10 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r10 <= 0) goto L_0x0139
            r0.wavesEnter = r8
            goto L_0x0139
        L_0x0128:
            if (r7 != 0) goto L_0x0139
            float r11 = r0.wavesEnter
            int r14 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r14 == 0) goto L_0x0139
            float r11 = r11 - r10
            r0.wavesEnter = r11
            int r10 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r10 >= 0) goto L_0x0139
            r0.wavesEnter = r3
        L_0x0139:
            r10 = 1059481190(0x3var_, float:0.65)
            r11 = 1051931443(0x3eb33333, float:0.35)
            android.view.animation.OvershootInterpolator r14 = r0.overshootInterpolator
            float r15 = r0.wavesEnter
            float r14 = r14.getInterpolation(r15)
            float r14 = r14 * r11
            float r14 = r14 + r10
            org.telegram.ui.Components.BlobDrawable r10 = r0.blobDrawable
            float r11 = r0.amplitude
            boolean r15 = r0.stub
            r16 = 1061997773(0x3f4ccccd, float:0.8)
            if (r15 == 0) goto L_0x0159
            r15 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x015c
        L_0x0159:
            r15 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x015c:
            r10.update(r11, r15)
            org.telegram.ui.Components.BlobDrawable r10 = r0.blobDrawable2
            float r11 = r0.amplitude
            boolean r15 = r0.stub
            if (r15 == 0) goto L_0x016b
            r15 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x016e
        L_0x016b:
            r15 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x016e:
            r10.update(r11, r15)
            r10 = 0
        L_0x0172:
            if (r10 >= r12) goto L_0x0300
            if (r10 != 0) goto L_0x0180
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.previousState
            if (r11 != 0) goto L_0x0180
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r9 = 2
            goto L_0x02f6
        L_0x0180:
            r12 = 16
            if (r10 != 0) goto L_0x01a3
            float r11 = r0.progressToPrepareRemove
            int r11 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r11 != 0) goto L_0x0190
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r9 = 2
            goto L_0x02f6
        L_0x0190:
            float r11 = r0.progressToState
            float r11 = r8 - r11
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r15 = r0.previousState
            float r9 = r0.amplitude
            r15.update(r12, r9)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.previousState
            android.graphics.Paint r12 = r0.paint
            r9.setToPaint(r12)
            goto L_0x020e
        L_0x01a3:
            if (r10 != r4) goto L_0x01cc
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            if (r9 != 0) goto L_0x01aa
            return
        L_0x01aa:
            float r11 = r0.progressToPrepareRemove
            int r11 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r11 != 0) goto L_0x01b6
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r9 = 2
            goto L_0x02f6
        L_0x01b6:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.previousState
            if (r11 == 0) goto L_0x01bd
            float r11 = r0.progressToState
            goto L_0x01bf
        L_0x01bd:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x01bf:
            float r15 = r0.amplitude
            r9.update(r12, r15)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            android.graphics.Paint r12 = r0.paint
            r9.setToPaint(r12)
            goto L_0x020e
        L_0x01cc:
            float r9 = r0.progressToPrepareRemove
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 != 0) goto L_0x01d8
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r9 = 2
            goto L_0x02f6
        L_0x01d8:
            r11 = 1065353216(0x3var_, float:1.0)
            android.graphics.Paint r9 = r0.paint
            r12 = -65536(0xfffffffffffvar_, float:NaN)
            r9.setColor(r12)
            android.graphics.Matrix r9 = r0.matrix
            r9.reset()
            android.graphics.Matrix r9 = r0.matrix
            r12 = 1132068864(0x437a0000, float:250.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = -r12
            float r12 = (float) r12
            float r13 = r0.progressToPrepareRemove
            float r13 = r8 - r13
            float r12 = r12 * r13
            r9.postTranslate(r12, r3)
            android.graphics.Matrix r9 = r0.matrix
            float r12 = r0.removeAngle
            r9.postRotate(r12, r2, r5)
            android.graphics.LinearGradient r9 = r0.prepareToRemoveShader
            android.graphics.Matrix r12 = r0.matrix
            r9.setLocalMatrix(r12)
            android.graphics.Paint r9 = r0.paint
            android.graphics.LinearGradient r12 = r0.prepareToRemoveShader
            r9.setShader(r12)
        L_0x020e:
            org.telegram.ui.Components.BlobDrawable r9 = r0.blobDrawable
            r12 = 1109393408(0x42200000, float:40.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r9.maxRadius = r12
            org.telegram.ui.Components.BlobDrawable r9 = r0.blobDrawable
            r12 = 1107296256(0x42000000, float:32.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            r9.minRadius = r13
            org.telegram.ui.Components.BlobDrawable r9 = r0.blobDrawable2
            r13 = 1108869120(0x42180000, float:38.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r9.maxRadius = r13
            org.telegram.ui.Components.BlobDrawable r9 = r0.blobDrawable2
            r13 = 1107558400(0x42040000, float:33.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r9.minRadius = r13
            r9 = 1117257728(0x42980000, float:76.0)
            r13 = 2
            if (r10 == r13) goto L_0x024e
            android.graphics.Paint r13 = r0.paint
            float r9 = r9 * r11
            float r15 = r0.progressToPrepareRemove
            float r15 = r8 - r15
            float r9 = r9 * r15
            int r9 = (int) r9
            r13.setAlpha(r9)
            goto L_0x025a
        L_0x024e:
            android.graphics.Paint r13 = r0.paint
            float r9 = r9 * r11
            float r15 = r0.progressToPrepareRemove
            float r9 = r9 * r15
            int r9 = (int) r9
            r13.setAlpha(r9)
        L_0x025a:
            float r9 = r0.wavesEnter
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 == 0) goto L_0x02b8
            r9 = 1050253722(0x3e99999a, float:0.3)
            float r13 = r0.amplitude
            float r13 = r13 * r9
            float r13 = r13 + r8
            r9 = 1036831949(0x3dcccccd, float:0.1)
            float r15 = r6 * r9
            float r13 = r13 + r15
            float r9 = r0.pinnedProgress
            float r9 = r8 - r9
            float r13 = r13 * r9
            r9 = 1067869798(0x3fa66666, float:1.3)
            float r15 = java.lang.Math.min(r13, r9)
            float r15 = r15 * r14
            r19.save()
            r1.scale(r15, r15, r2, r5)
            org.telegram.ui.Components.BlobDrawable r13 = r0.blobDrawable
            android.graphics.Paint r3 = r0.paint
            r13.draw(r2, r5, r1, r3)
            r19.restore()
            r3 = 1048911544(0x3e851eb8, float:0.26)
            float r13 = r0.amplitude
            float r13 = r13 * r3
            float r13 = r13 + r8
            r3 = 1036831949(0x3dcccccd, float:0.1)
            float r17 = r6 * r3
            float r13 = r13 + r17
            float r3 = r0.pinnedProgress
            float r3 = r8 - r3
            float r13 = r13 * r3
            float r3 = java.lang.Math.min(r13, r9)
            float r3 = r3 * r14
            r19.save()
            r1.scale(r3, r3, r2, r5)
            org.telegram.ui.Components.BlobDrawable r9 = r0.blobDrawable2
            android.graphics.Paint r13 = r0.paint
            r9.draw(r2, r5, r1, r13)
            r19.restore()
        L_0x02b8:
            r3 = 1132396544(0x437var_, float:255.0)
            r9 = 2
            if (r10 != r9) goto L_0x02c8
            android.graphics.Paint r13 = r0.paint
            float r15 = r0.progressToPrepareRemove
            float r15 = r15 * r3
            int r3 = (int) r15
            r13.setAlpha(r3)
            goto L_0x02da
        L_0x02c8:
            if (r10 != r4) goto L_0x02d3
            android.graphics.Paint r13 = r0.paint
            float r3 = r3 * r11
            int r3 = (int) r3
            r13.setAlpha(r3)
            goto L_0x02da
        L_0x02d3:
            android.graphics.Paint r3 = r0.paint
            r13 = 255(0xff, float:3.57E-43)
            r3.setAlpha(r13)
        L_0x02da:
            r19.save()
            r3 = 1036831949(0x3dcccccd, float:0.1)
            float r13 = r6 * r3
            float r13 = r13 + r8
            float r15 = r6 * r3
            float r15 = r15 + r8
            r1.scale(r13, r15, r2, r5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            android.graphics.Paint r13 = r0.paint
            r1.drawCircle(r2, r5, r12, r13)
            r19.restore()
        L_0x02f6:
            int r10 = r10 + 1
            r3 = 0
            r9 = 1036831949(0x3dcccccd, float:0.1)
            r12 = 3
            r13 = 2
            goto L_0x0172
        L_0x0300:
            boolean r3 = r0.removed
            if (r3 != 0) goto L_0x030e
            float r3 = r0.wavesEnter
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x030e
            r18.invalidate()
        L_0x030e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipButton.onDraw(android.graphics.Canvas):void");
    }

    private void setAmplitude(double value) {
        float min = (float) (Math.min(8500.0d, value) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void setState(int state) {
        String contentDescription;
        WeavingState weavingState = this.currentState;
        if (weavingState == null || weavingState.currentState != state) {
            WeavingState weavingState2 = this.currentState;
            this.previousState = weavingState2;
            WeavingState weavingState3 = this.states[state];
            this.currentState = weavingState3;
            float f = 0.0f;
            if (weavingState2 != null) {
                this.progressToState = 0.0f;
            } else {
                this.progressToState = 1.0f;
                boolean showWaves = true;
                if (weavingState3.currentState == 3 || this.currentState.currentState == 2) {
                    showWaves = false;
                }
                if (showWaves) {
                    f = 1.0f;
                }
                this.wavesEnter = f;
            }
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService == null || !ChatObject.isChannelOrGiga(voIPService.getChat())) {
                contentDescription = LocaleController.getString("VoipGroupVoiceChat", NUM);
            } else {
                contentDescription = LocaleController.getString("VoipChannelVoiceChat", NUM);
            }
            if (state == 0) {
                contentDescription = contentDescription + ", " + LocaleController.getString("VoipTapToMute", NUM);
            } else if (state == 2) {
                contentDescription = contentDescription + ", " + LocaleController.getString("Connecting", NUM);
            } else if (state == 3) {
                contentDescription = contentDescription + ", " + LocaleController.getString("VoipMutedByAdmin", NUM);
            }
            setContentDescription(contentDescription);
            invalidate();
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= 21 && GroupCallPip.getInstance() != null) {
            if (GroupCallPip.getInstance().showAlert) {
                i = NUM;
                str = "AccDescrCloseMenu";
            } else {
                i = NUM;
                str = "AccDescrOpenMenu2";
            }
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.stub) {
            setAmplitude(0.0d);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
            boolean isMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            this.bigMicDrawable.setCustomEndFrame(isMuted ? 13 : 24);
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            updateButtonState();
        }
    }

    private void updateButtonState() {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null && voIPService.groupCall != null) {
            int currentCallState = voIPService.getCallState();
            if (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5) {
                setState(2);
                return;
            }
            TLRPC.TL_groupCallParticipant participant = voIPService.groupCall.participants.get(voIPService.getSelfId());
            if (participant == null || participant.can_self_unmute || !participant.muted || ChatObject.canManageCalls(voIPService.getChat())) {
                setState(voIPService.isMicMute());
                return;
            }
            if (!voIPService.isMicMute()) {
                voIPService.setMicMute(true, false, false);
            }
            setState(3);
            long now = SystemClock.uptimeMillis();
            MotionEvent e = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
            if (getParent() != null) {
                ((View) getParent()).dispatchTouchEvent(e);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webRtcMicAmplitudeEvent) {
            setAmplitude((double) (4000.0f * args[0].floatValue()));
        } else if (id == NotificationCenter.groupCallUpdated) {
            updateButtonState();
        }
    }

    public void onAudioSettingsChanged() {
        boolean isMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.bigMicDrawable.setCustomEndFrame(isMuted ? 13 : 24)) {
            if (isMuted) {
                this.bigMicDrawable.setCurrentFrame(0);
            } else {
                this.bigMicDrawable.setCurrentFrame(12);
            }
        }
        this.muteButton.playAnimation();
        updateButtonState();
    }

    public void onStateChanged(int state) {
        updateButtonState();
    }

    public void setRemoveAngle(double angle) {
        this.removeAngle = (float) angle;
    }

    public void prepareToRemove(boolean prepare) {
        if (this.prepareToRemove != prepare) {
            invalidate();
        }
        this.prepareToRemove = prepare;
    }
}
