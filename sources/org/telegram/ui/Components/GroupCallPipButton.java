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
    boolean prepareToRemove;
    private final LinearGradient prepareToRemoveShader;
    float pressedProgress;
    boolean pressedState;
    WeavingState previousState;
    float progressToPrepareRemove;
    float progressToState = 1.0f;
    Random random = new Random();
    float removeAngle;
    WeavingState[] states = new WeavingState[3];
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
        for (int i2 = 0; i2 < 3; i2++) {
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
            int i = this.currentState;
            if (i == 0) {
                if (!(this.color1 == Theme.getColor("voipgroup_overlayGreen1") && this.color2 == Theme.getColor("voipgroup_overlayGreen2"))) {
                    int color = Theme.getColor("voipgroup_overlayGreen1");
                    this.color1 = color;
                    int color3 = Theme.getColor("voipgroup_overlayGreen2");
                    this.color2 = color3;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color3}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (i != 1) {
                return;
            } else {
                if (!(this.color1 == Theme.getColor("voipgroup_overlayBlue1") && this.color2 == Theme.getColor("voipgroup_overlayBlue2"))) {
                    int color4 = Theme.getColor("voipgroup_overlayBlue1");
                    this.color1 = color4;
                    int color5 = Theme.getColor("voipgroup_overlayBlue2");
                    this.color2 = color5;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color4, color5}, (float[]) null, Shader.TileMode.CLAMP);
                }
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
    /* JADX WARNING: Removed duplicated region for block: B:15:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0118  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            super.onDraw(r18)
            int r2 = r17.getMeasuredWidth()
            r3 = 1
            int r2 = r2 >> r3
            float r2 = (float) r2
            int r4 = r17.getMeasuredHeight()
            int r4 = r4 >> r3
            float r4 = (float) r4
            boolean r5 = r0.pressedState
            r6 = 1037726734(0x3dda740e, float:0.10666667)
            r7 = 0
            r8 = 1065353216(0x3var_, float:1.0)
            if (r5 == 0) goto L_0x002e
            float r9 = r0.pressedProgress
            int r10 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x002e
            float r9 = r9 + r6
            r0.pressedProgress = r9
            int r5 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x003f
            r0.pressedProgress = r8
            goto L_0x003f
        L_0x002e:
            if (r5 != 0) goto L_0x003f
            float r5 = r0.pressedProgress
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x003f
            float r5 = r5 - r6
            r0.pressedProgress = r5
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x003f
            r0.pressedProgress = r7
        L_0x003f:
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r6 = r0.pressedProgress
            float r5 = r5.getInterpolation(r6)
            org.telegram.ui.Components.RLottieImageView r6 = r0.muteButton
            r9 = 1036831949(0x3dcccccd, float:0.1)
            float r5 = r5 * r9
            float r10 = r5 + r8
            r6.setScaleY(r10)
            org.telegram.ui.Components.RLottieImageView r6 = r0.muteButton
            r6.setScaleX(r10)
            boolean r6 = r0.stub
            if (r6 == 0) goto L_0x0091
            long r11 = java.lang.System.currentTimeMillis()
            long r13 = r0.lastStubUpdateAmplitude
            long r13 = r11 - r13
            r15 = 1000(0x3e8, double:4.94E-321)
            int r6 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r6 <= 0) goto L_0x0091
            r0.lastStubUpdateAmplitude = r11
            java.util.Random r6 = r0.random
            int r6 = r6.nextInt()
            int r6 = r6 % 100
            int r6 = java.lang.Math.abs(r6)
            float r6 = (float) r6
            r11 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 * r11
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            float r6 = r6 / r12
            float r6 = r6 + r11
            r0.animateToAmplitude = r6
            float r11 = r0.amplitude
            float r6 = r6 - r11
            r11 = 1153138688(0x44bb8000, float:1500.0)
            float r13 = org.telegram.ui.Components.BlobDrawable.AMPLITUDE_SPEED
            float r13 = r13 * r11
            float r13 = r13 + r12
            float r6 = r6 / r13
            r0.animateAmplitudeDiff = r6
        L_0x0091:
            float r6 = r0.animateToAmplitude
            float r11 = r0.amplitude
            int r12 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r12 == 0) goto L_0x00b3
            float r12 = r0.animateAmplitudeDiff
            r13 = 1098907648(0x41800000, float:16.0)
            float r13 = r13 * r12
            float r11 = r11 + r13
            r0.amplitude = r11
            int r12 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r12 <= 0) goto L_0x00ad
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 <= 0) goto L_0x00b3
            r0.amplitude = r6
            goto L_0x00b3
        L_0x00ad:
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 >= 0) goto L_0x00b3
            r0.amplitude = r6
        L_0x00b3:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r6 = r0.previousState
            if (r6 == 0) goto L_0x00c8
            float r6 = r0.progressToState
            r11 = 1032000111(0x3d83126f, float:0.064)
            float r6 = r6 + r11
            r0.progressToState = r6
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x00c8
            r0.progressToState = r8
            r6 = 0
            r0.previousState = r6
        L_0x00c8:
            boolean r6 = r0.prepareToRemove
            r11 = 1027292903(0x3d3b3ee7, float:0.NUM)
            if (r6 == 0) goto L_0x00df
            float r12 = r0.progressToPrepareRemove
            int r13 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x00df
            float r12 = r12 + r11
            r0.progressToPrepareRemove = r12
            int r6 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x00f0
            r0.progressToPrepareRemove = r8
            goto L_0x00f0
        L_0x00df:
            if (r6 != 0) goto L_0x00f0
            float r6 = r0.progressToPrepareRemove
            int r12 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r12 == 0) goto L_0x00f0
            float r6 = r6 - r11
            r0.progressToPrepareRemove = r6
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 >= 0) goto L_0x00f0
            r0.progressToPrepareRemove = r7
        L_0x00f0:
            org.telegram.ui.Components.BlobDrawable r6 = r0.blobDrawable
            float r11 = r0.amplitude
            boolean r12 = r0.stub
            r13 = 1061997773(0x3f4ccccd, float:0.8)
            if (r12 == 0) goto L_0x00ff
            r12 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x0102
        L_0x00ff:
            r12 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x0102:
            r6.update(r11, r12)
            org.telegram.ui.Components.BlobDrawable r6 = r0.blobDrawable2
            float r11 = r0.amplitude
            boolean r12 = r0.stub
            if (r12 == 0) goto L_0x010e
            goto L_0x0111
        L_0x010e:
            r9 = 1061997773(0x3f4ccccd, float:0.8)
        L_0x0111:
            r6.update(r11, r9)
            r6 = 0
        L_0x0115:
            r9 = 3
            if (r6 >= r9) goto L_0x0264
            if (r6 != 0) goto L_0x0120
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.previousState
            if (r9 != 0) goto L_0x0120
            goto L_0x0260
        L_0x0120:
            r11 = 16
            if (r6 != 0) goto L_0x013f
            float r9 = r0.progressToPrepareRemove
            int r9 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r9 != 0) goto L_0x012c
            goto L_0x0260
        L_0x012c:
            float r9 = r0.progressToState
            float r9 = r8 - r9
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r13 = r0.previousState
            float r14 = r0.amplitude
            r13.update(r11, r14)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r11 = r0.previousState
            android.graphics.Paint r12 = r0.paint
            r11.setToPaint(r12)
            goto L_0x01a3
        L_0x013f:
            if (r6 != r3) goto L_0x0165
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            if (r9 != 0) goto L_0x0146
            return
        L_0x0146:
            float r13 = r0.progressToPrepareRemove
            int r13 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r13 != 0) goto L_0x014e
            goto L_0x0260
        L_0x014e:
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r13 = r0.previousState
            if (r13 == 0) goto L_0x0155
            float r13 = r0.progressToState
            goto L_0x0157
        L_0x0155:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0157:
            float r14 = r0.amplitude
            r9.update(r11, r14)
            org.telegram.ui.Components.GroupCallPipButton$WeavingState r9 = r0.currentState
            android.graphics.Paint r11 = r0.paint
            r9.setToPaint(r11)
            r9 = r13
            goto L_0x01a3
        L_0x0165:
            float r9 = r0.progressToPrepareRemove
            int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x016d
            goto L_0x0260
        L_0x016d:
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
            r9.postTranslate(r11, r7)
            android.graphics.Matrix r9 = r0.matrix
            float r11 = r0.removeAngle
            r9.postRotate(r11, r2, r4)
            android.graphics.LinearGradient r9 = r0.prepareToRemoveShader
            android.graphics.Matrix r11 = r0.matrix
            r9.setLocalMatrix(r11)
            android.graphics.Paint r9 = r0.paint
            android.graphics.LinearGradient r11 = r0.prepareToRemoveShader
            r9.setShader(r11)
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01a3:
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
            if (r6 == r13) goto L_0x01e3
            android.graphics.Paint r14 = r0.paint
            float r11 = r11 * r9
            float r15 = r0.progressToPrepareRemove
            float r15 = r8 - r15
            float r11 = r11 * r15
            int r11 = (int) r11
            r14.setAlpha(r11)
            goto L_0x01ef
        L_0x01e3:
            android.graphics.Paint r14 = r0.paint
            float r11 = r11 * r9
            float r15 = r0.progressToPrepareRemove
            float r11 = r11 * r15
            int r11 = (int) r11
            r14.setAlpha(r11)
        L_0x01ef:
            r11 = 1050253722(0x3e99999a, float:0.3)
            float r14 = r0.amplitude
            float r14 = r14 * r11
            float r14 = r14 + r8
            float r14 = r14 + r5
            r11 = 1067869798(0x3fa66666, float:1.3)
            float r14 = java.lang.Math.min(r14, r11)
            r18.save()
            r1.scale(r14, r14, r2, r4)
            org.telegram.ui.Components.BlobDrawable r14 = r0.blobDrawable
            android.graphics.Paint r15 = r0.paint
            r14.draw(r2, r4, r1, r15)
            r18.restore()
            r14 = 1048911544(0x3e851eb8, float:0.26)
            float r15 = r0.amplitude
            float r15 = r15 * r14
            float r15 = r15 + r8
            float r15 = r15 + r5
            float r11 = java.lang.Math.min(r15, r11)
            r18.save()
            r1.scale(r11, r11, r2, r4)
            org.telegram.ui.Components.BlobDrawable r11 = r0.blobDrawable2
            android.graphics.Paint r14 = r0.paint
            r11.draw(r2, r4, r1, r14)
            r18.restore()
            r11 = 1132396544(0x437var_, float:255.0)
            if (r6 != r13) goto L_0x023b
            android.graphics.Paint r9 = r0.paint
            float r13 = r0.progressToPrepareRemove
            float r13 = r13 * r11
            int r11 = (int) r13
            r9.setAlpha(r11)
            goto L_0x024d
        L_0x023b:
            if (r6 != r3) goto L_0x0246
            android.graphics.Paint r13 = r0.paint
            float r9 = r9 * r11
            int r9 = (int) r9
            r13.setAlpha(r9)
            goto L_0x024d
        L_0x0246:
            android.graphics.Paint r9 = r0.paint
            r11 = 255(0xff, float:3.57E-43)
            r9.setAlpha(r11)
        L_0x024d:
            r18.save()
            r1.scale(r10, r10, r2, r4)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            android.graphics.Paint r11 = r0.paint
            r1.drawCircle(r2, r4, r9, r11)
            r18.restore()
        L_0x0260:
            int r6 = r6 + 1
            goto L_0x0115
        L_0x0264:
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
        if (VoIPService.getSharedInstance() != null) {
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
            setState(2);
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
