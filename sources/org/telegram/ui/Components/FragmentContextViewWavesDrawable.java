package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
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
    Path path;
    WeavingState pausedState;
    WeavingState previousState;
    float progressToState = 1.0f;
    WeavingState[] states = new WeavingState[4];
    private final Paint xRefP;

    public FragmentContextViewWavesDrawable() {
        new RectF();
        this.path = new Path();
        Paint paint2 = new Paint(1);
        this.xRefP = paint2;
        new Paint(1);
        paint2.setColor(0);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    public void draw(float f, float f2, float f3, float f4, Canvas canvas, FragmentContextView fragmentContextView, float f5) {
        boolean z;
        int i;
        float f6;
        FragmentContextView fragmentContextView2 = fragmentContextView;
        checkColors();
        if (fragmentContextView2 == null) {
            z = false;
        } else {
            z = this.parents.size() > 0 && fragmentContextView2 == this.parents.get(0);
        }
        if (f2 <= f4) {
            long j = 0;
            WeavingState weavingState = this.currentState;
            boolean z2 = (weavingState == null || this.previousState == null || ((weavingState.currentState != 1 || this.previousState.currentState != 0) && (this.previousState.currentState != 1 || this.currentState.currentState != 0))) ? false : true;
            float f7 = 1.0f;
            if (z) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j2 = elapsedRealtime - this.lastUpdateTime;
                this.lastUpdateTime = elapsedRealtime;
                j = j2 > 20 ? 17 : j2;
                float f8 = this.animateToAmplitude;
                float f9 = this.amplitude;
                if (f8 != f9) {
                    float var_ = this.animateAmplitudeDiff;
                    float var_ = f9 + (((float) j) * var_);
                    this.amplitude = var_;
                    if (var_ > 0.0f) {
                        if (var_ > f8) {
                            this.amplitude = f8;
                        }
                    } else if (var_ < f8) {
                        this.amplitude = f8;
                    }
                    fragmentContextView.invalidate();
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
                    fragmentContextView.invalidate();
                }
                if (this.previousState != null) {
                    float var_ = this.progressToState + (((float) j) / 250.0f);
                    this.progressToState = var_;
                    if (var_ > 1.0f) {
                        this.progressToState = 1.0f;
                        this.previousState = null;
                    }
                    fragmentContextView.invalidate();
                }
            }
            long j3 = j;
            int i2 = 0;
            while (i2 < 2) {
                if (i2 == 0 && this.previousState == null) {
                    Canvas canvas2 = canvas;
                    i = i2;
                } else {
                    if (i2 == 0) {
                        f6 = f7 - this.progressToState;
                        this.previousState.setToPaint(this.paint);
                    } else {
                        WeavingState weavingState2 = this.currentState;
                        if (weavingState2 != null) {
                            f6 = this.previousState != null ? this.progressToState : 1.0f;
                            if (z) {
                                weavingState2.update((int) (f4 - f2), (int) (f3 - f), j3, this.amplitude);
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
                    this.paint.setAlpha((int) (76.0f * var_));
                    float dp = ((float) AndroidUtilities.dp(6.0f)) * this.amplitude2;
                    float dp2 = ((float) AndroidUtilities.dp(6.0f)) * this.amplitude2;
                    float var_ = f;
                    float var_ = f3;
                    float var_ = f4;
                    Canvas canvas3 = canvas;
                    float var_ = f2;
                    i = i2;
                    float var_ = f5;
                    this.lineBlobDrawable1.draw(var_, f2 - dp, var_, var_, canvas3, this.paint, var_, var_);
                    this.lineBlobDrawable2.draw(var_, f2 - dp2, var_, var_, canvas3, this.paint, var_, var_);
                    if (i == 1 && z2) {
                        this.paint.setAlpha(255);
                    } else if (i == 1) {
                        this.paint.setAlpha((int) (255.0f * var_));
                    } else {
                        this.paint.setAlpha(255);
                    }
                    if (i != 1 || !z2) {
                        Canvas canvas4 = canvas;
                        this.lineBlobDrawable.draw(f, f2, f3, f4, canvas, this.paint, f2, f5);
                    } else {
                        this.path.reset();
                        this.path.addCircle(f3 - ((float) AndroidUtilities.dp(18.0f)), f2 + ((f4 - f2) / 2.0f), (f3 - f) * 1.1f * var_, Path.Direction.CW);
                        canvas.save();
                        canvas.clipPath(this.path);
                        this.lineBlobDrawable.draw(f, f2, f3, f4, canvas, this.paint, f2, f5);
                        canvas.restore();
                    }
                }
                i2 = i + 1;
                f7 = 1.0f;
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
        if (VoIPService.getSharedInstance() != null) {
            int callState = VoIPService.getSharedInstance().getCallState();
            if (callState == 1 || callState == 2 || callState == 6 || callState == 5) {
                setState(2, z);
                return;
            }
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = VoIPService.getSharedInstance().groupCall.participants.get(AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()).getUserConfig().getClientUserId());
            if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(VoIPService.getSharedInstance().getChat())) {
                setState(VoIPService.getSharedInstance().isMicMute() ? 1 : 0, z);
                return;
            }
            VoIPService.getSharedInstance().setMicMute(true, false, false);
            setState(3, z);
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
