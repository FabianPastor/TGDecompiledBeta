package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
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
    WeavingState pausedState;
    WeavingState previousState;
    float progressToState = 1.0f;
    WeavingState[] states = new WeavingState[4];

    public FragmentContextViewWavesDrawable() {
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    public void draw(float f, float f2, float f3, float f4, Canvas canvas, View view, float f5) {
        boolean z;
        int i;
        float f6;
        View view2 = view;
        checkColors();
        if (view2 == null) {
            z = false;
        } else {
            z = this.parents.size() > 0 && view2 == this.parents.get(0);
        }
        if (f2 <= f4) {
            long j = 0;
            if (z) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j2 = elapsedRealtime - this.lastUpdateTime;
                this.lastUpdateTime = elapsedRealtime;
                j = j2 > 20 ? 17 : j2;
                float f7 = this.animateToAmplitude;
                float f8 = this.amplitude;
                if (f7 != f8) {
                    float f9 = this.animateAmplitudeDiff;
                    float var_ = f8 + (((float) j) * f9);
                    this.amplitude = var_;
                    if (f9 > 0.0f) {
                        if (var_ > f7) {
                            this.amplitude = f7;
                        }
                    } else if (var_ < f7) {
                        this.amplitude = f7;
                    }
                    view.invalidate();
                }
                float var_ = this.animateToAmplitude;
                float var_ = this.amplitude2;
                if (var_ != var_) {
                    float var_ = this.animateAmplitudeDiff2;
                    float var_ = var_ + (((float) j) * var_);
                    this.amplitude2 = var_;
                    if (var_ > 0.0f) {
                        if (var_ > var_) {
                            this.amplitude2 = var_;
                        }
                    } else if (var_ < var_) {
                        this.amplitude2 = var_;
                    }
                    view.invalidate();
                }
                if (this.previousState != null) {
                    float var_ = this.progressToState + (((float) j) / 250.0f);
                    this.progressToState = var_;
                    if (var_ > 1.0f) {
                        this.progressToState = 1.0f;
                        this.previousState = null;
                    }
                    view.invalidate();
                }
            }
            long j3 = j;
            int i2 = 0;
            while (i2 < 2) {
                if (i2 == 0 && this.previousState == null) {
                    i = i2;
                } else {
                    if (i2 == 0) {
                        f6 = 1.0f - this.progressToState;
                        if (z) {
                            this.previousState.update((int) (f4 - f2), (int) (f3 - f), j3, this.amplitude);
                        }
                        this.previousState.setToPaint(this.paint);
                    } else {
                        WeavingState weavingState = this.currentState;
                        if (weavingState != null) {
                            f6 = this.previousState != null ? this.progressToState : 1.0f;
                            if (z) {
                                weavingState.update((int) (f4 - f2), (int) (f3 - f), j3, this.amplitude);
                            }
                            this.currentState.setToPaint(this.paint);
                        } else {
                            return;
                        }
                    }
                    float var_ = f6;
                    LineBlobDrawable lineBlobDrawable3 = this.lineBlobDrawable;
                    lineBlobDrawable3.minRadius = 0.0f;
                    lineBlobDrawable3.maxRadius = ((float) AndroidUtilities.dp(2.0f)) + (((float) AndroidUtilities.dp(2.0f)) * this.amplitude);
                    this.lineBlobDrawable1.minRadius = (float) AndroidUtilities.dp(0.0f);
                    this.lineBlobDrawable1.maxRadius = ((float) AndroidUtilities.dp(3.0f)) + (((float) AndroidUtilities.dp(9.0f)) * this.amplitude);
                    this.lineBlobDrawable2.minRadius = (float) AndroidUtilities.dp(0.0f);
                    LineBlobDrawable lineBlobDrawable4 = this.lineBlobDrawable2;
                    float var_ = this.amplitude;
                    lineBlobDrawable4.maxRadius = ((float) AndroidUtilities.dp(3.0f)) + (((float) AndroidUtilities.dp(9.0f)) * var_);
                    this.lineBlobDrawable.update(var_, 0.3f);
                    this.lineBlobDrawable1.update(this.amplitude, 0.7f);
                    this.lineBlobDrawable2.update(this.amplitude, 0.7f);
                    if (i2 == 1) {
                        this.paint.setAlpha((int) (255.0f * var_));
                    } else {
                        this.paint.setAlpha(255);
                    }
                    float var_ = f3;
                    float var_ = f4;
                    Canvas canvas2 = canvas;
                    float var_ = f2;
                    i = i2;
                    float var_ = f5;
                    this.lineBlobDrawable.draw(f, f2, var_, var_, canvas2, this.paint, var_, var_);
                    this.paint.setAlpha((int) (var_ * 76.0f));
                    float dp = ((float) AndroidUtilities.dp(6.0f)) * this.amplitude2;
                    float dp2 = ((float) AndroidUtilities.dp(6.0f)) * this.amplitude2;
                    float var_ = f;
                    this.lineBlobDrawable1.draw(var_, f2 - dp, var_, var_, canvas2, this.paint, var_, var_);
                    this.lineBlobDrawable2.draw(var_, f2 - dp2, var_, var_, canvas2, this.paint, var_, var_);
                }
                i2 = i + 1;
            }
        }
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

    private void setState(int i) {
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

    public void updateState() {
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
            VoIPService.getSharedInstance().setMicMute(true, false, false);
            setState(3);
        }
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
