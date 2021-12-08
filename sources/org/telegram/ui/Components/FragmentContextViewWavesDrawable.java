package org.telegram.ui.Components;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
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
    Path path;
    WeavingState pausedState;
    WeavingState previousState;
    float progressToState = 1.0f;
    WeavingState[] states = new WeavingState[4];

    public FragmentContextViewWavesDrawable() {
        new RectF();
        this.path = new Path();
        new Paint(1);
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00d8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(float r21, float r22, float r23, float r24, android.graphics.Canvas r25, org.telegram.ui.Components.FragmentContextView r26, float r27) {
        /*
            r20 = this;
            r0 = r20
            r1 = r26
            r20.checkColors()
            r2 = 0
            r10 = 1
            if (r1 != 0) goto L_0x000d
        L_0x000b:
            r3 = 0
            goto L_0x001e
        L_0x000d:
            java.util.ArrayList<android.view.View> r3 = r0.parents
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x000b
            java.util.ArrayList<android.view.View> r3 = r0.parents
            java.lang.Object r3 = r3.get(r2)
            if (r1 != r3) goto L_0x000b
            r3 = 1
        L_0x001e:
            int r4 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1))
            if (r4 <= 0) goto L_0x0023
            return
        L_0x0023:
            r4 = 0
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r6 = r0.currentState
            if (r6 == 0) goto L_0x004d
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r7 = r0.previousState
            if (r7 == 0) goto L_0x004d
            int r6 = r6.currentState
            if (r6 != r10) goto L_0x003b
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r6 = r0.previousState
            int r6 = r6.currentState
            if (r6 == 0) goto L_0x004b
        L_0x003b:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r6 = r0.previousState
            int r6 = r6.currentState
            if (r6 != r10) goto L_0x004d
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r6 = r0.currentState
            int r6 = r6.currentState
            if (r6 != 0) goto L_0x004d
        L_0x004b:
            r11 = 1
            goto L_0x004e
        L_0x004d:
            r11 = 0
        L_0x004e:
            if (r3 == 0) goto L_0x006d
            long r4 = android.os.SystemClock.elapsedRealtime()
            long r6 = r0.lastUpdateTime
            long r6 = r4 - r6
            r0.lastUpdateTime = r4
            r4 = 20
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x0063
            r4 = 17
            goto L_0x0064
        L_0x0063:
            r4 = r6
        L_0x0064:
            r6 = 3
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x006d
            r13 = r4
            r12 = 0
            goto L_0x006f
        L_0x006d:
            r12 = r3
            r13 = r4
        L_0x006f:
            r15 = 1065353216(0x3var_, float:1.0)
            r9 = 0
            if (r12 == 0) goto L_0x00d5
            float r3 = r0.animateToAmplitude
            float r4 = r0.amplitude
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x0098
            float r5 = r0.animateAmplitudeDiff
            float r6 = (float) r13
            float r6 = r6 * r5
            float r4 = r4 + r6
            r0.amplitude = r4
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 <= 0) goto L_0x008f
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0095
            r0.amplitude = r3
            goto L_0x0095
        L_0x008f:
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0095
            r0.amplitude = r3
        L_0x0095:
            r26.invalidate()
        L_0x0098:
            float r3 = r0.animateToAmplitude
            float r4 = r0.amplitude2
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x00bc
            float r5 = r0.animateAmplitudeDiff2
            float r6 = (float) r13
            float r6 = r6 * r5
            float r4 = r4 + r6
            r0.amplitude2 = r4
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 <= 0) goto L_0x00b3
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x00b9
            r0.amplitude2 = r3
            goto L_0x00b9
        L_0x00b3:
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00b9
            r0.amplitude2 = r3
        L_0x00b9:
            r26.invalidate()
        L_0x00bc:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.previousState
            if (r3 == 0) goto L_0x00d5
            float r3 = r0.progressToState
            float r4 = (float) r13
            r5 = 1132068864(0x437a0000, float:250.0)
            float r4 = r4 / r5
            float r3 = r3 + r4
            r0.progressToState = r3
            int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r3 <= 0) goto L_0x00d2
            r0.progressToState = r15
            r3 = 0
            r0.previousState = r3
        L_0x00d2:
            r26.invalidate()
        L_0x00d5:
            r1 = 2
            if (r2 >= r1) goto L_0x024c
            if (r2 != 0) goto L_0x00e3
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r1 = r0.previousState
            if (r1 != 0) goto L_0x00e3
            r15 = r2
            r19 = 0
            goto L_0x0245
        L_0x00e3:
            if (r2 != 0) goto L_0x00f3
            float r1 = r0.progressToState
            float r1 = r15 - r1
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.previousState
            android.graphics.Paint r4 = r0.paint
            r3.setToPaint(r4)
        L_0x00f0:
            r16 = r1
            goto L_0x0117
        L_0x00f3:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.currentState
            if (r3 != 0) goto L_0x00f8
            return
        L_0x00f8:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r1 = r0.previousState
            if (r1 == 0) goto L_0x00ff
            float r1 = r0.progressToState
            goto L_0x0101
        L_0x00ff:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0101:
            if (r12 == 0) goto L_0x010f
            float r4 = r24 - r22
            int r4 = (int) r4
            float r5 = r23 - r21
            int r5 = (int) r5
            float r8 = r0.amplitude
            r6 = r13
            r3.update(r4, r5, r6, r8)
        L_0x010f:
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r3 = r0.currentState
            android.graphics.Paint r4 = r0.paint
            r3.setToPaint(r4)
            goto L_0x00f0
        L_0x0117:
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            r1.minRadius = r9
            r17 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r5 = r0.amplitude
            float r4 = r4 * r5
            float r3 = r3 + r4
            r1.maxRadius = r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            r1.minRadius = r3
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            r3 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r5 = 1091567616(0x41100000, float:9.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r7 = r0.amplitude
            float r6 = r6 * r7
            float r4 = r4 + r6
            r1.maxRadius = r4
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            r1.minRadius = r4
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            float r5 = r0.amplitude
            float r4 = r4 * r5
            float r3 = r3 + r4
            r1.maxRadius = r3
            if (r2 != r10) goto L_0x0187
            if (r12 == 0) goto L_0x0187
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            r3 = 1050253722(0x3e99999a, float:0.3)
            r1.update(r5, r3)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            float r3 = r0.amplitude
            r4 = 1060320051(0x3var_, float:0.7)
            r1.update(r3, r4)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            float r3 = r0.amplitude
            r1.update(r3, r4)
        L_0x0187:
            android.graphics.Paint r1 = r0.paint
            r3 = 1117257728(0x42980000, float:76.0)
            float r3 = r3 * r16
            int r3 = (int) r3
            r1.setAlpha(r3)
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r3 = (float) r3
            float r4 = r0.amplitude2
            float r3 = r3 * r4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r4 = r0.amplitude2
            float r18 = r1 * r4
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable1
            float r3 = r22 - r3
            android.graphics.Paint r7 = r0.paint
            r8 = r2
            r2 = r21
            r4 = r23
            r5 = r24
            r6 = r25
            r15 = r8
            r8 = r22
            r19 = 0
            r9 = r27
            r1.draw(r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable2
            float r3 = r22 - r18
            android.graphics.Paint r7 = r0.paint
            r1.draw(r2, r3, r4, r5, r6, r7, r8, r9)
            r1 = 255(0xff, float:3.57E-43)
            if (r15 != r10) goto L_0x01d3
            if (r11 == 0) goto L_0x01d3
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r1)
            goto L_0x01e5
        L_0x01d3:
            if (r15 != r10) goto L_0x01e0
            android.graphics.Paint r1 = r0.paint
            r2 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r16
            int r2 = (int) r2
            r1.setAlpha(r2)
            goto L_0x01e5
        L_0x01e0:
            android.graphics.Paint r2 = r0.paint
            r2.setAlpha(r1)
        L_0x01e5:
            if (r15 != r10) goto L_0x0230
            if (r11 == 0) goto L_0x0230
            android.graphics.Path r1 = r0.path
            r1.reset()
            r1 = 1099956224(0x41900000, float:18.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r23 - r1
            float r2 = r24 - r22
            float r2 = r2 / r17
            float r2 = r22 + r2
            float r3 = r23 - r21
            r4 = 1066192077(0x3f8ccccd, float:1.1)
            float r3 = r3 * r4
            float r3 = r3 * r16
            android.graphics.Path r4 = r0.path
            android.graphics.Path$Direction r5 = android.graphics.Path.Direction.CW
            r4.addCircle(r1, r2, r3, r5)
            r25.save()
            android.graphics.Path r1 = r0.path
            r9 = r25
            r9.clipPath(r1)
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            android.graphics.Paint r7 = r0.paint
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r8 = r22
            r9 = r27
            r1.draw(r2, r3, r4, r5, r6, r7, r8, r9)
            r25.restore()
            goto L_0x0245
        L_0x0230:
            org.telegram.ui.Components.LineBlobDrawable r1 = r0.lineBlobDrawable
            android.graphics.Paint r7 = r0.paint
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r8 = r22
            r9 = r27
            r1.draw(r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x0245:
            int r2 = r15 + 1
            r9 = 0
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x00d5
        L_0x024c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextViewWavesDrawable.draw(float, float, float, float, android.graphics.Canvas, org.telegram.ui.Components.FragmentContextView, float):void");
    }

    private void checkColors() {
        int i = 0;
        while (true) {
            WeavingState[] weavingStateArr = this.states;
            if (i < weavingStateArr.length) {
                weavingStateArr[i].checkColor();
                i++;
            } else {
                return;
            }
        }
    }

    private void setState(int i, boolean z) {
        WeavingState weavingState = this.currentState;
        if (weavingState != null && weavingState.currentState == i) {
            return;
        }
        if (VoIPService.getSharedInstance() == null && this.currentState == null) {
            this.currentState = this.pausedState;
            return;
        }
        WeavingState weavingState2 = z ? this.currentState : null;
        this.previousState = weavingState2;
        this.currentState = this.states[i];
        if (weavingState2 != null) {
            this.progressToState = 0.0f;
        } else {
            this.progressToState = 1.0f;
        }
    }

    public void setAmplitude(float f) {
        this.animateToAmplitude = f;
        float f2 = this.amplitude;
        this.animateAmplitudeDiff = (f - f2) / 250.0f;
        this.animateAmplitudeDiff2 = (f - f2) / 120.0f;
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

    public void updateState(boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            int callState = sharedInstance.getCallState();
            if (sharedInstance.isSwitchingStream() || !(callState == 1 || callState == 2 || callState == 6 || callState == 5)) {
                ChatObject.Call call = sharedInstance.groupCall;
                if (call != null) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(sharedInstance.getSelfId());
                    if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(sharedInstance.getChat())) {
                        setState(sharedInstance.isMicMute() ? 1 : 0, z);
                        return;
                    }
                    sharedInstance.setMicMute(true, false, false);
                    setState(3, z);
                    return;
                }
                setState(sharedInstance.isMicMute() ? 1 : 0, z);
                return;
            }
            setState(2, z);
        }
    }

    public int getState() {
        WeavingState weavingState = this.currentState;
        if (weavingState != null) {
            return weavingState.currentState;
        }
        return 0;
    }

    public static class WeavingState {
        String blueKey1 = "voipgroup_topPanelBlue1";
        String blueKey2 = "voipgroup_topPanelBlue2";
        int color1;
        int color2;
        /* access modifiers changed from: private */
        public final int currentState;
        private float duration;
        String greenKey1 = "voipgroup_topPanelGreen1";
        String greenKey2 = "voipgroup_topPanelGreen2";
        private final Matrix matrix = new Matrix();
        String mutedByAdmin = "voipgroup_mutedByAdminGradient";
        String mutedByAdmin2 = "voipgroup_mutedByAdminGradient2";
        String mutedByAdmin3 = "voipgroup_mutedByAdminGradient3";
        public Shader shader;
        private float startX;
        private float startY;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private float time;

        public WeavingState(int i) {
            this.currentState = i;
            createGradients();
        }

        private void createGradients() {
            int i = this.currentState;
            if (i == 0) {
                int color = Theme.getColor(this.greenKey1);
                this.color1 = color;
                int color3 = Theme.getColor(this.greenKey2);
                this.color2 = color3;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color3}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 1) {
                int color4 = Theme.getColor(this.blueKey1);
                this.color1 = color4;
                int color5 = Theme.getColor(this.blueKey2);
                this.color2 = color5;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color4, color5}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 3) {
                int color6 = Theme.getColor(this.mutedByAdmin);
                this.color1 = color6;
                int color7 = Theme.getColor(this.mutedByAdmin2);
                this.color2 = color7;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color6, Theme.getColor(this.mutedByAdmin3), color7}, new float[]{0.0f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            }
        }

        public void update(int i, int i2, long j, float f) {
            if (this.currentState != 2) {
                float f2 = this.duration;
                if (f2 == 0.0f || this.time >= f2) {
                    this.duration = (float) (Utilities.random.nextInt(700) + 500);
                    this.time = 0.0f;
                    if (this.targetX == -1.0f) {
                        int i3 = this.currentState;
                        if (i3 == 3) {
                            this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.05f) / 100.0f) - 14.4f;
                            this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.05f) / 100.0f) + 0.7f;
                        } else if (i3 == 0) {
                            this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) - 14.4f;
                            this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                        } else {
                            this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 1.1f;
                            this.targetY = (((float) Utilities.random.nextInt(100)) * 4.0f) / 100.0f;
                        }
                    }
                    this.startX = this.targetX;
                    this.startY = this.targetY;
                    int i4 = this.currentState;
                    if (i4 == 3) {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.05f) / 100.0f) - 14.4f;
                        this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.05f) / 100.0f) + 0.7f;
                    } else if (i4 == 0) {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) - 14.4f;
                        this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                    } else {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 1.1f;
                        this.targetY = (((float) Utilities.random.nextInt(100)) * 4.0f) / 100.0f;
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
                float var_ = f6 / 400.0f;
                int i5 = this.currentState;
                float var_ = var_ * ((i5 == 0 || i5 == 3) ? 3.0f : 1.5f);
                this.matrix.reset();
                this.matrix.postTranslate(f8, var_);
                this.matrix.postScale(var_, var_, f8 + 200.0f, var_ + 200.0f);
                this.shader.setLocalMatrix(this.matrix);
            }
        }

        public void checkColor() {
            int i = this.currentState;
            if (i == 0) {
                if (this.color1 != Theme.getColor(this.greenKey1) || this.color2 != Theme.getColor(this.greenKey2)) {
                    createGradients();
                }
            } else if (i == 1) {
                if (this.color1 != Theme.getColor(this.blueKey1) || this.color2 != Theme.getColor(this.blueKey2)) {
                    createGradients();
                }
            } else if (i != 3) {
            } else {
                if (this.color1 != Theme.getColor(this.mutedByAdmin) || this.color2 != Theme.getColor(this.mutedByAdmin2)) {
                    createGradients();
                }
            }
        }

        public void setToPaint(Paint paint) {
            int i = this.currentState;
            if (i == 0 || i == 1 || i == 3) {
                paint.setShader(this.shader);
                return;
            }
            paint.setShader((Shader) null);
            paint.setColor(Theme.getColor("voipgroup_topPanelGray"));
        }
    }
}
