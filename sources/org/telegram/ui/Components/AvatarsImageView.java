package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.FrameLayout;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;

public class AvatarsImageView extends FrameLayout {
    DrawingState[] animatingStates = new DrawingState[3];
    boolean centered;
    DrawingState[] currentStates = new DrawingState[3];
    int currentStyle;
    private Paint paint = new Paint(1);
    Random random = new Random();
    float transitionProgress = 1.0f;
    ValueAnimator transitionProgressAnimator;
    boolean updateAfterTransition;
    Runnable updateDelegate;
    boolean wasDraw;
    private Paint xRefP = new Paint(1);

    public void commitTransition(boolean z) {
        boolean z2;
        if (!this.wasDraw || !z) {
            this.transitionProgress = 1.0f;
            swapStates();
            return;
        }
        DrawingState[] drawingStateArr = new DrawingState[3];
        boolean z3 = false;
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr2 = this.currentStates;
            drawingStateArr[i] = drawingStateArr2[i];
            if (drawingStateArr2[i].id != this.animatingStates[i].id) {
                z3 = true;
            } else {
                long unused = this.currentStates[i].lastSpeakTime = this.animatingStates[i].lastSpeakTime;
            }
        }
        if (!z3) {
            this.transitionProgress = 1.0f;
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= 3) {
                    z2 = false;
                    break;
                } else if (this.currentStates[i3].id == this.animatingStates[i2].id) {
                    drawingStateArr[i3] = null;
                    if (i2 == i3) {
                        int unused2 = this.animatingStates[i2].animationType = -1;
                        GroupCallUserCell.AvatarWavesDrawable access$300 = this.animatingStates[i2].wavesDrawable;
                        GroupCallUserCell.AvatarWavesDrawable unused3 = this.animatingStates[i2].wavesDrawable = this.currentStates[i2].wavesDrawable;
                        GroupCallUserCell.AvatarWavesDrawable unused4 = this.currentStates[i2].wavesDrawable = access$300;
                    } else {
                        int unused5 = this.animatingStates[i2].animationType = 2;
                        int unused6 = this.animatingStates[i2].moveFromIndex = i3;
                    }
                    z2 = true;
                } else {
                    i3++;
                }
            }
            if (!z2) {
                int unused7 = this.animatingStates[i2].animationType = 0;
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            if (drawingStateArr[i4] != null) {
                int unused8 = drawingStateArr[i4].animationType = 1;
            }
        }
        ValueAnimator valueAnimator = this.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.transitionProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.transitionProgressAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarsImageView.this.lambda$commitTransition$0$AvatarsImageView(valueAnimator);
            }
        });
        this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AvatarsImageView avatarsImageView = AvatarsImageView.this;
                if (avatarsImageView.transitionProgressAnimator != null) {
                    avatarsImageView.transitionProgress = 1.0f;
                    avatarsImageView.swapStates();
                    AvatarsImageView avatarsImageView2 = AvatarsImageView.this;
                    if (avatarsImageView2.updateAfterTransition) {
                        avatarsImageView2.updateAfterTransition = false;
                        Runnable runnable = avatarsImageView2.updateDelegate;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                    AvatarsImageView.this.invalidate();
                }
                AvatarsImageView.this.transitionProgressAnimator = null;
            }
        });
        this.transitionProgressAnimator.setDuration(220);
        this.transitionProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.transitionProgressAnimator.start();
        invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$commitTransition$0 */
    public /* synthetic */ void lambda$commitTransition$0$AvatarsImageView(ValueAnimator valueAnimator) {
        this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: private */
    public void swapStates() {
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr = this.currentStates;
            DrawingState drawingState = drawingStateArr[i];
            DrawingState[] drawingStateArr2 = this.animatingStates;
            drawingStateArr[i] = drawingStateArr2[i];
            drawingStateArr2[i] = drawingState;
        }
    }

    public void updateAfterTransitionEnd() {
        this.updateAfterTransition = true;
    }

    public void setDelegate(Runnable runnable) {
        this.updateDelegate = runnable;
    }

    public void setStyle(int i) {
        this.currentStyle = i;
        invalidate();
    }

    private static class DrawingState {
        /* access modifiers changed from: private */
        public int animationType;
        /* access modifiers changed from: private */
        public AvatarDrawable avatarDrawable;
        /* access modifiers changed from: private */
        public int id;
        /* access modifiers changed from: private */
        public ImageReceiver imageReceiver;
        /* access modifiers changed from: private */
        public long lastSpeakTime;
        /* access modifiers changed from: private */
        public long lastUpdateTime;
        /* access modifiers changed from: private */
        public int moveFromIndex;
        TLRPC$TL_groupCallParticipant participant;
        /* access modifiers changed from: private */
        public GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

        private DrawingState() {
        }
    }

    public AvatarsImageView(Context context) {
        super(context);
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr = this.currentStates;
            drawingStateArr[i] = new DrawingState();
            ImageReceiver unused = drawingStateArr[i].imageReceiver = new ImageReceiver(this);
            this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[i].avatarDrawable = new AvatarDrawable();
            this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(9.0f));
            DrawingState[] drawingStateArr2 = this.animatingStates;
            drawingStateArr2[i] = new DrawingState();
            ImageReceiver unused3 = drawingStateArr2[i].imageReceiver = new ImageReceiver(this);
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[i].avatarDrawable = new AvatarDrawable();
            this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(9.0f));
        }
        setWillNotDraw(false);
        this.xRefP.setColor(0);
        this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0119  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setObject(int r15, int r16, org.telegram.tgnet.TLObject r17) {
        /*
            r14 = this;
            r0 = r14
            r1 = r17
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            r3 = 0
            int unused = r2.id = r3
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r4 = r2[r15]
            r5 = 0
            r4.participant = r5
            if (r1 != 0) goto L_0x0021
            r1 = r2[r15]
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.setImageBitmap((android.graphics.drawable.Drawable) r5)
            r14.invalidate()
            return
        L_0x0021:
            r2 = r2[r15]
            r6 = -1
            long unused = r2.lastSpeakTime = r6
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_groupCallParticipant
            r4 = 4
            if (r2 == 0) goto L_0x008c
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r1
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            r2.participant = r1
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r16)
            int r6 = r1.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r6 = r0.animatingStates
            r6 = r6[r15]
            org.telegram.ui.Components.AvatarDrawable r6 = r6.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            int r6 = r0.currentStyle
            if (r6 != r4) goto L_0x0077
            int r6 = r1.user_id
            org.telegram.messenger.AccountInstance r7 = org.telegram.messenger.AccountInstance.getInstance(r16)
            org.telegram.messenger.UserConfig r7 = r7.getUserConfig()
            int r7 = r7.getClientUserId()
            if (r6 != r7) goto L_0x006c
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r6 = r0.animatingStates
            r6 = r6[r15]
            r7 = 0
            long unused = r6.lastSpeakTime = r7
            goto L_0x0081
        L_0x006c:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r6 = r0.animatingStates
            r6 = r6[r15]
            int r7 = r1.active_date
            long r7 = (long) r7
            long unused = r6.lastSpeakTime = r7
            goto L_0x0081
        L_0x0077:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r6 = r0.animatingStates
            r6 = r6[r15]
            int r7 = r1.active_date
            long r7 = (long) r7
            long unused = r6.lastSpeakTime = r7
        L_0x0081:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r6 = r0.animatingStates
            r6 = r6[r15]
            int r1 = r1.user_id
            int unused = r6.id = r1
            r12 = r2
            goto L_0x00a7
        L_0x008c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$User
            if (r2 == 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            org.telegram.ui.Components.AvatarDrawable r2 = r2.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            int r6 = r1.id
            int unused = r2.id = r6
            r12 = r1
        L_0x00a7:
            r10 = r5
            goto L_0x00c1
        L_0x00a9:
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            org.telegram.ui.Components.AvatarDrawable r2 = r2.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            int r6 = r1.id
            int unused = r2.id = r6
            r10 = r1
            r12 = r5
        L_0x00c1:
            if (r12 == 0) goto L_0x00df
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r15]
            org.telegram.messenger.ImageReceiver r7 = r1.imageReceiver
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUser(r12, r3)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r15]
            org.telegram.ui.Components.AvatarDrawable r10 = r1.avatarDrawable
            r11 = 0
            r13 = 0
            java.lang.String r9 = "50_50"
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x00fa
        L_0x00df:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r15]
            org.telegram.messenger.ImageReceiver r5 = r1.imageReceiver
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForChat(r10, r3)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r15]
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r9 = 0
            r11 = 0
            java.lang.String r7 = "50_50"
            r5.setImage(r6, r7, r8, r9, r10, r11)
        L_0x00fa:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r15]
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            int r2 = r0.currentStyle
            if (r2 != r4) goto L_0x0109
            r2 = 1098907648(0x41800000, float:16.0)
            goto L_0x010b
        L_0x0109:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x010b:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius((int) r2)
            int r1 = r0.currentStyle
            if (r1 != r4) goto L_0x0119
            r1 = 1107296256(0x42000000, float:32.0)
            goto L_0x011b
        L_0x0119:
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x011b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r15]
            org.telegram.messenger.ImageReceiver r2 = r2.imageReceiver
            float r1 = (float) r1
            r3 = 0
            r2.setImageCoords(r3, r3, r1, r1)
            r14.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsImageView.setObject(int, int, org.telegram.tgnet.TLObject):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x041b  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0423 A[SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r0 = r25
            r8 = r26
            r9 = 1
            r0.wasDraw = r9
            int r1 = r0.currentStyle
            r10 = 4
            if (r1 != r10) goto L_0x000f
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            goto L_0x0011
        L_0x000f:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x0011:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r1 = 0
            r2 = 0
        L_0x0017:
            r13 = 3
            if (r1 >= r13) goto L_0x0029
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
            r3 = r3[r1]
            int r3 = r3.id
            if (r3 == 0) goto L_0x0026
            int r2 = r2 + 1
        L_0x0026:
            int r1 = r1 + 1
            goto L_0x0017
        L_0x0029:
            boolean r1 = r0.centered
            r14 = 1092616192(0x41200000, float:10.0)
            r16 = 1090519040(0x41000000, float:8.0)
            r7 = 2
            if (r1 == 0) goto L_0x0049
            int r1 = r25.getMeasuredWidth()
            int r2 = r2 * r11
            int r1 = r1 - r2
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x0040
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x0042
        L_0x0040:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x0042:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r7
            goto L_0x004d
        L_0x0049:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
        L_0x004d:
            r17 = r1
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0061
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x0061
            r1 = 1
            goto L_0x0062
        L_0x0061:
            r1 = 0
        L_0x0062:
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x0072
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x0084
        L_0x0072:
            if (r2 == r13) goto L_0x0084
            android.graphics.Paint r2 = r0.paint
            if (r1 == 0) goto L_0x007b
            java.lang.String r1 = "returnToCallMutedBackground"
            goto L_0x007d
        L_0x007b:
            java.lang.String r1 = "returnToCallBackground"
        L_0x007d:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setColor(r1)
        L_0x0084:
            r1 = 0
            r18 = 0
        L_0x0087:
            if (r1 >= r13) goto L_0x0098
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r1]
            int r2 = r2.id
            if (r2 == 0) goto L_0x0095
            int r18 = r18 + 1
        L_0x0095:
            int r1 = r1 + 1
            goto L_0x0087
        L_0x0098:
            int r1 = r0.currentStyle
            r6 = 5
            if (r1 == r13) goto L_0x00a5
            if (r1 == r10) goto L_0x00a5
            if (r1 != r6) goto L_0x00a2
            goto L_0x00a5
        L_0x00a2:
            r19 = 0
            goto L_0x00a7
        L_0x00a5:
            r19 = 1
        L_0x00a7:
            if (r19 == 0) goto L_0x00c5
            r2 = 0
            r3 = 0
            int r1 = r25.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r25.getMeasuredHeight()
            float r5 = (float) r1
            r20 = 255(0xff, float:3.57E-43)
            r21 = 31
            r1 = r26
            r15 = 5
            r6 = r20
            r12 = 2
            r7 = r21
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x00c7
        L_0x00c5:
            r12 = 2
            r15 = 5
        L_0x00c7:
            r7 = 2
        L_0x00c8:
            if (r7 < 0) goto L_0x043b
            r1 = 0
        L_0x00cb:
            if (r1 >= r12) goto L_0x042e
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x00db
            float r3 = r0.transitionProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 != 0) goto L_0x00db
        L_0x00d7:
            r10 = 5
            r12 = 0
            goto L_0x0423
        L_0x00db:
            if (r1 != 0) goto L_0x00e0
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.animatingStates
            goto L_0x00e2
        L_0x00e0:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
        L_0x00e2:
            if (r1 != r9) goto L_0x00f3
            float r4 = r0.transitionProgress
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x00f3
            r4 = r3[r7]
            int r4 = r4.animationType
            if (r4 == r9) goto L_0x00f3
            goto L_0x00d7
        L_0x00f3:
            r4 = r3[r7]
            org.telegram.messenger.ImageReceiver r4 = r4.imageReceiver
            boolean r5 = r4.hasImageSet()
            if (r5 != 0) goto L_0x0100
            goto L_0x00d7
        L_0x0100:
            if (r1 != 0) goto L_0x0128
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x011d
            int r5 = r25.getMeasuredWidth()
            int r6 = r18 * r11
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x0114
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x0116
        L_0x0114:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x0116:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r12
            goto L_0x0121
        L_0x011d:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
        L_0x0121:
            int r6 = r11 * r7
            int r5 = r5 + r6
            r4.setImageX(r5)
            goto L_0x012f
        L_0x0128:
            int r5 = r11 * r7
            int r5 = r17 + r5
            r4.setImageX(r5)
        L_0x012f:
            int r5 = r0.currentStyle
            if (r5 != r10) goto L_0x0136
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x0138
        L_0x0136:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0138:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setImageY(r5)
            float r5 = r0.transitionProgress
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x01ff
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != r9) goto L_0x0169
            r26.save()
            float r5 = r0.transitionProgress
            float r6 = r2 - r5
            float r5 = r2 - r5
            float r15 = r4.getCenterX()
            float r13 = r4.getCenterY()
            r8.scale(r6, r5, r15, r13)
            float r5 = r0.transitionProgress
            float r5 = r2 - r5
        L_0x0166:
            r6 = 1
            goto L_0x0202
        L_0x0169:
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != 0) goto L_0x0184
            r26.save()
            float r5 = r0.transitionProgress
            float r6 = r4.getCenterX()
            float r13 = r4.getCenterY()
            r8.scale(r5, r5, r6, r13)
            float r5 = r0.transitionProgress
            goto L_0x0166
        L_0x0184:
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != r12) goto L_0x01c8
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x01a7
            int r5 = r25.getMeasuredWidth()
            int r6 = r18 * r11
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x019e
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x01a0
        L_0x019e:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x01a0:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r12
            goto L_0x01ab
        L_0x01a7:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
        L_0x01ab:
            int r6 = r11 * r7
            int r5 = r5 + r6
            r6 = r3[r7]
            int r6 = r6.moveFromIndex
            int r6 = r6 * r11
            int r6 = r17 + r6
            float r5 = (float) r5
            float r13 = r0.transitionProgress
            float r5 = r5 * r13
            float r6 = (float) r6
            float r13 = r2 - r13
            float r6 = r6 * r13
            float r5 = r5 + r6
            int r5 = (int) r5
            r4.setImageX(r5)
            goto L_0x01ff
        L_0x01c8:
            r5 = r3[r7]
            int r5 = r5.animationType
            r6 = -1
            if (r5 != r6) goto L_0x01ff
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x01ff
            int r5 = r25.getMeasuredWidth()
            int r6 = r18 * r11
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x01e3
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x01e5
        L_0x01e3:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x01e5:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r12
            int r6 = r11 * r7
            int r5 = r5 + r6
            int r6 = r17 + r6
            float r5 = (float) r5
            float r13 = r0.transitionProgress
            float r5 = r5 * r13
            float r6 = (float) r6
            float r13 = r2 - r13
            float r6 = r6 * r13
            float r5 = r5 + r6
            int r5 = (int) r5
            r4.setImageX(r5)
        L_0x01ff:
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
        L_0x0202:
            int r13 = r3.length
            int r13 = r13 - r9
            if (r7 == r13) goto L_0x03fb
            int r13 = r0.currentStyle
            r15 = 1101529088(0x41a80000, float:21.0)
            r22 = 1117323264(0x42990000, float:76.5)
            r23 = 1095761920(0x41500000, float:13.0)
            r24 = 1099431936(0x41880000, float:17.0)
            r12 = 3
            if (r13 == r12) goto L_0x031b
            r12 = 5
            if (r13 != r12) goto L_0x0218
            goto L_0x031b
        L_0x0218:
            if (r13 != r10) goto L_0x02e4
            float r12 = r4.getCenterX()
            float r13 = r4.getCenterY()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r14 = (float) r14
            android.graphics.Paint r10 = r0.xRefP
            r8.drawCircle(r12, r13, r14, r10)
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            if (r10 != 0) goto L_0x0246
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r12 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r12.<init>(r13, r14)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r10.wavesDrawable = r12
        L_0x0246:
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            java.lang.String r12 = "voipgroup_listeningText"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            float r13 = r5 * r22
            int r13 = (int) r13
            int r12 = androidx.core.graphics.ColorUtils.setAlphaComponent(r12, r13)
            r10.setColor(r12)
            long r12 = java.lang.System.currentTimeMillis()
            r10 = r3[r7]
            long r14 = r10.lastUpdateTime
            long r14 = r12 - r14
            r22 = 100
            int r10 = (r14 > r22 ? 1 : (r14 == r22 ? 0 : -1))
            if (r10 <= 0) goto L_0x02bc
            r10 = r3[r7]
            long unused = r10.lastUpdateTime = r12
            int r10 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10)
            int r10 = r10.getCurrentTime()
            long r12 = (long) r10
            r10 = r3[r7]
            long r14 = r10.lastSpeakTime
            long r12 = r12 - r14
            r14 = 5
            int r10 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r10 > 0) goto L_0x02a7
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r10.setShowWaves(r9, r0)
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            java.util.Random r12 = r0.random
            int r12 = r12.nextInt()
            int r12 = r12 % 100
            double r12 = (double) r12
            r10.setAmplitude(r12)
            goto L_0x02bc
        L_0x02a7:
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r12 = 0
            r10.setShowWaves(r12, r0)
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r12 = 0
            r10.setAmplitude(r12)
        L_0x02bc:
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r10.update()
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            float r12 = r4.getCenterX()
            float r13 = r4.getCenterY()
            r10.draw(r8, r12, r13, r0)
            r3 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r3 = r3.getAvatarScale()
            r10 = 5
            r12 = 0
            goto L_0x03ff
        L_0x02e4:
            android.graphics.Paint r3 = r0.paint
            int r3 = r3.getAlpha()
            int r10 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x02f7
            android.graphics.Paint r10 = r0.paint
            float r12 = (float) r3
            float r12 = r12 * r5
            int r12 = (int) r12
            r10.setAlpha(r12)
        L_0x02f7:
            float r10 = r4.getCenterX()
            float r12 = r4.getCenterY()
            int r13 = r0.currentStyle
            r14 = 4
            if (r13 != r14) goto L_0x0306
            r23 = 1099431936(0x41880000, float:17.0)
        L_0x0306:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r13 = (float) r13
            android.graphics.Paint r15 = r0.paint
            r8.drawCircle(r10, r12, r13, r15)
            int r10 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x03fb
            android.graphics.Paint r10 = r0.paint
            r10.setAlpha(r3)
            goto L_0x03fb
        L_0x031b:
            r14 = 4
            float r10 = r4.getCenterX()
            float r12 = r4.getCenterY()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r13 = (float) r13
            android.graphics.Paint r14 = r0.xRefP
            r8.drawCircle(r10, r12, r13, r14)
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            if (r10 != 0) goto L_0x0364
            int r10 = r0.currentStyle
            r12 = 5
            if (r10 != r12) goto L_0x0352
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r12 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r14 = 1098907648(0x41800000, float:16.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r12.<init>(r13, r14)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r10.wavesDrawable = r12
            goto L_0x0364
        L_0x0352:
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r12 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r12.<init>(r13, r14)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r10.wavesDrawable = r12
        L_0x0364:
            int r10 = r0.currentStyle
            r12 = 5
            if (r10 != r12) goto L_0x037f
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            java.lang.String r12 = "voipgroup_speakingText"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            float r13 = r5 * r22
            int r13 = (int) r13
            int r12 = androidx.core.graphics.ColorUtils.setAlphaComponent(r12, r13)
            r10.setColor(r12)
        L_0x037f:
            r10 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r10.participant
            float r10 = r10.amplitude
            r12 = 0
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 <= 0) goto L_0x03a9
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r10.setShowWaves(r9, r0)
            r10 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r10.participant
            float r10 = r10.amplitude
            r12 = 1097859072(0x41700000, float:15.0)
            float r10 = r10 * r12
            r12 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r12 = r12.wavesDrawable
            double r13 = (double) r10
            r12.setAmplitude(r13)
            r12 = 0
            goto L_0x03b3
        L_0x03a9:
            r10 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            r12 = 0
            r10.setShowWaves(r12, r0)
        L_0x03b3:
            int r10 = r0.currentStyle
            r13 = 5
            if (r10 != r13) goto L_0x03ce
            long r13 = android.os.SystemClock.uptimeMillis()
            r10 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r10.participant
            long r9 = r10.lastSpeakTime
            long r13 = r13 - r9
            r9 = 500(0x1f4, double:2.47E-321)
            int r20 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r20 <= 0) goto L_0x03ce
            java.lang.Runnable r9 = r0.updateDelegate
            r9.run()
        L_0x03ce:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r9.update()
            int r9 = r0.currentStyle
            r10 = 5
            if (r9 != r10) goto L_0x03f0
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            float r13 = r4.getCenterX()
            float r14 = r4.getCenterY()
            r9.draw(r8, r13, r14, r0)
            r25.invalidate()
        L_0x03f0:
            r3 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r3 = r3.getAvatarScale()
            goto L_0x03ff
        L_0x03fb:
            r10 = 5
            r12 = 0
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x03ff:
            r4.setAlpha(r5)
            int r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x041b
            r26.save()
            float r2 = r4.getCenterX()
            float r5 = r4.getCenterY()
            r8.scale(r3, r3, r2, r5)
            r4.draw(r8)
            r26.restore()
            goto L_0x041e
        L_0x041b:
            r4.draw(r8)
        L_0x041e:
            if (r6 == 0) goto L_0x0423
            r26.restore()
        L_0x0423:
            int r1 = r1 + 1
            r9 = 1
            r10 = 4
            r12 = 2
            r13 = 3
            r14 = 1092616192(0x41200000, float:10.0)
            r15 = 5
            goto L_0x00cb
        L_0x042e:
            r10 = 5
            r12 = 0
            int r7 = r7 + -1
            r9 = 1
            r10 = 4
            r12 = 2
            r13 = 3
            r14 = 1092616192(0x41200000, float:10.0)
            r15 = 5
            goto L_0x00c8
        L_0x043b:
            if (r19 == 0) goto L_0x0440
            r26.restore()
        L_0x0440:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsImageView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasDraw = false;
        for (int i = 0; i < 3; i++) {
            this.currentStates[i].imageReceiver.onDetachedFromWindow();
            this.animatingStates[i].imageReceiver.onDetachedFromWindow();
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < 3; i++) {
            this.currentStates[i].imageReceiver.onAttachedToWindow();
            this.animatingStates[i].imageReceiver.onAttachedToWindow();
        }
    }

    public void setCentered(boolean z) {
        this.centered = z;
    }
}
