package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
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
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00ed  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setObject(int r12, int r13, org.telegram.tgnet.TLObject r14) {
        /*
            r11 = this;
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r0 = r11.animatingStates
            r0 = r0[r12]
            r1 = 0
            int unused = r0.id = r1
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r0 = r11.animatingStates
            r2 = r0[r12]
            r3 = 0
            r2.participant = r3
            if (r14 != 0) goto L_0x001e
            r12 = r0[r12]
            org.telegram.messenger.ImageReceiver r12 = r12.imageReceiver
            r12.setImageBitmap((android.graphics.drawable.Drawable) r3)
            r11.invalidate()
            return
        L_0x001e:
            r0 = r0[r12]
            r4 = -1
            long unused = r0.lastSpeakTime = r4
            boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC$TL_groupCallParticipant
            if (r0 == 0) goto L_0x0060
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r14 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r14
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r0 = r11.animatingStates
            r0 = r0[r12]
            r0.participant = r14
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            int r0 = r14.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r13 = r13.getUser(r0)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r0 = r11.animatingStates
            r0 = r0[r12]
            org.telegram.ui.Components.AvatarDrawable r0 = r0.avatarDrawable
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r13)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r0 = r11.animatingStates
            r0 = r0[r12]
            int r14 = r14.active_date
            long r4 = (long) r14
            long unused = r0.lastSpeakTime = r4
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r14 = r11.animatingStates
            r14 = r14[r12]
            int r0 = r13.id
            int unused = r14.id = r0
            r9 = r13
        L_0x005e:
            r7 = r3
            goto L_0x0094
        L_0x0060:
            boolean r13 = r14 instanceof org.telegram.tgnet.TLRPC$User
            if (r13 == 0) goto L_0x007c
            org.telegram.tgnet.TLRPC$User r14 = (org.telegram.tgnet.TLRPC$User) r14
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.ui.Components.AvatarDrawable r13 = r13.avatarDrawable
            r13.setInfo((org.telegram.tgnet.TLRPC$User) r14)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            int r0 = r14.id
            int unused = r13.id = r0
            r9 = r14
            goto L_0x005e
        L_0x007c:
            org.telegram.tgnet.TLRPC$Chat r14 = (org.telegram.tgnet.TLRPC$Chat) r14
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.ui.Components.AvatarDrawable r13 = r13.avatarDrawable
            r13.setInfo((org.telegram.tgnet.TLRPC$Chat) r14)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            int r0 = r14.id
            int unused = r13.id = r0
            r7 = r14
            r9 = r3
        L_0x0094:
            if (r9 == 0) goto L_0x00b2
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.messenger.ImageReceiver r4 = r13.imageReceiver
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUser(r9, r1)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.ui.Components.AvatarDrawable r7 = r13.avatarDrawable
            r8 = 0
            r10 = 0
            java.lang.String r6 = "50_50"
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x00cd
        L_0x00b2:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.messenger.ImageReceiver r2 = r13.imageReceiver
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForChat(r7, r1)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.ui.Components.AvatarDrawable r5 = r13.avatarDrawable
            r6 = 0
            r8 = 0
            java.lang.String r4 = "50_50"
            r2.setImage(r3, r4, r5, r6, r7, r8)
        L_0x00cd:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r13 = r11.animatingStates
            r13 = r13[r12]
            org.telegram.messenger.ImageReceiver r13 = r13.imageReceiver
            int r14 = r11.currentStyle
            r0 = 4
            if (r14 != r0) goto L_0x00dd
            r14 = 1098907648(0x41800000, float:16.0)
            goto L_0x00df
        L_0x00dd:
            r14 = 1094713344(0x41400000, float:12.0)
        L_0x00df:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13.setRoundRadius((int) r14)
            int r13 = r11.currentStyle
            if (r13 != r0) goto L_0x00ed
            r13 = 1107296256(0x42000000, float:32.0)
            goto L_0x00ef
        L_0x00ed:
            r13 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x00ef:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r14 = r11.animatingStates
            r12 = r14[r12]
            org.telegram.messenger.ImageReceiver r12 = r12.imageReceiver
            float r13 = (float) r13
            r14 = 0
            r12.setImageCoords(r14, r14, r13, r13)
            r11.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsImageView.setObject(int, int, org.telegram.tgnet.TLObject):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0435  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0460 A[SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r25) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = 1
            r0.wasDraw = r2
            int r3 = r0.currentStyle
            r4 = 4
            if (r3 != r4) goto L_0x000f
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            goto L_0x0011
        L_0x000f:
            r3 = 1101004800(0x41a00000, float:20.0)
        L_0x0011:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6 = 0
            r7 = 0
        L_0x0017:
            r8 = 3
            if (r6 >= r8) goto L_0x0029
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r8 = r0.currentStates
            r8 = r8[r6]
            int r8 = r8.id
            if (r8 == 0) goto L_0x0026
            int r7 = r7 + 1
        L_0x0026:
            int r6 = r6 + 1
            goto L_0x0017
        L_0x0029:
            boolean r6 = r0.centered
            r9 = 1092616192(0x41200000, float:10.0)
            r12 = 2
            if (r6 == 0) goto L_0x0047
            int r6 = r24.getMeasuredWidth()
            int r7 = r7 * r3
            int r6 = r6 - r7
            int r7 = r0.currentStyle
            if (r7 != r4) goto L_0x003e
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0040
        L_0x003e:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0040:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 / r12
            goto L_0x004b
        L_0x0047:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x004b:
            org.telegram.messenger.voip.VoIPService r7 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r7 == 0) goto L_0x005d
            org.telegram.messenger.voip.VoIPService r7 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r7 = r7.isMicMute()
            if (r7 == 0) goto L_0x005d
            r7 = 1
            goto L_0x005e
        L_0x005d:
            r7 = 0
        L_0x005e:
            int r13 = r0.currentStyle
            if (r13 != r4) goto L_0x006e
            android.graphics.Paint r7 = r0.paint
            java.lang.String r13 = "inappPlayerBackground"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r7.setColor(r13)
            goto L_0x0080
        L_0x006e:
            if (r13 == r8) goto L_0x0080
            android.graphics.Paint r13 = r0.paint
            if (r7 == 0) goto L_0x0077
            java.lang.String r7 = "returnToCallMutedBackground"
            goto L_0x0079
        L_0x0077:
            java.lang.String r7 = "returnToCallBackground"
        L_0x0079:
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r13.setColor(r7)
        L_0x0080:
            r7 = 0
            r13 = 0
        L_0x0082:
            if (r7 >= r8) goto L_0x0093
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r14 = r0.animatingStates
            r14 = r14[r7]
            int r14 = r14.id
            if (r14 == 0) goto L_0x0090
            int r13 = r13 + 1
        L_0x0090:
            int r7 = r7 + 1
            goto L_0x0082
        L_0x0093:
            r7 = 2
        L_0x0094:
            if (r7 < 0) goto L_0x047a
            r14 = 0
        L_0x0097:
            if (r14 >= r12) goto L_0x046d
            r15 = 1065353216(0x3var_, float:1.0)
            if (r14 != 0) goto L_0x00a8
            float r10 = r0.transitionProgress
            int r10 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1))
            if (r10 != 0) goto L_0x00a8
        L_0x00a3:
            r12 = r3
            r23 = r6
            goto L_0x0460
        L_0x00a8:
            if (r14 != 0) goto L_0x00ad
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r10 = r0.animatingStates
            goto L_0x00af
        L_0x00ad:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r10 = r0.currentStates
        L_0x00af:
            if (r14 != r2) goto L_0x00c0
            float r11 = r0.transitionProgress
            int r11 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
            if (r11 == 0) goto L_0x00c0
            r11 = r10[r7]
            int r11 = r11.animationType
            if (r11 == r2) goto L_0x00c0
            goto L_0x00a3
        L_0x00c0:
            r11 = r10[r7]
            org.telegram.messenger.ImageReceiver r11 = r11.imageReceiver
            boolean r16 = r11.hasImageSet()
            if (r16 != 0) goto L_0x00cd
            goto L_0x00a3
        L_0x00cd:
            if (r14 != 0) goto L_0x00f6
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x00eb
            int r5 = r24.getMeasuredWidth()
            int r17 = r13 * r3
            int r5 = r5 - r17
            int r8 = r0.currentStyle
            if (r8 != r4) goto L_0x00e2
            r8 = 1090519040(0x41000000, float:8.0)
            goto L_0x00e4
        L_0x00e2:
            r8 = 1082130432(0x40800000, float:4.0)
        L_0x00e4:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            int r5 = r5 / r12
            goto L_0x00ef
        L_0x00eb:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x00ef:
            int r8 = r3 * r7
            int r5 = r5 + r8
            r11.setImageX(r5)
            goto L_0x00fc
        L_0x00f6:
            int r5 = r3 * r7
            int r5 = r5 + r6
            r11.setImageX(r5)
        L_0x00fc:
            int r5 = r0.currentStyle
            if (r5 != r4) goto L_0x0103
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x0105
        L_0x0103:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0105:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r11.setImageY(r5)
            float r5 = r0.transitionProgress
            int r5 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r5 == 0) goto L_0x01d5
            r5 = r10[r7]
            int r5 = r5.animationType
            if (r5 != r2) goto L_0x0139
            r25.save()
            float r5 = r0.transitionProgress
            float r8 = r15 - r5
            float r5 = r15 - r5
            float r2 = r11.getCenterX()
            float r9 = r11.getCenterY()
            r1.scale(r8, r5, r2, r9)
            float r2 = r0.transitionProgress
            float r2 = r15 - r2
        L_0x0133:
            r5 = r2
            r2 = 1092616192(0x41200000, float:10.0)
            r8 = 1
            goto L_0x01da
        L_0x0139:
            r2 = r10[r7]
            int r2 = r2.animationType
            if (r2 != 0) goto L_0x0154
            r25.save()
            float r2 = r0.transitionProgress
            float r5 = r11.getCenterX()
            float r8 = r11.getCenterY()
            r1.scale(r2, r2, r5, r8)
            float r2 = r0.transitionProgress
            goto L_0x0133
        L_0x0154:
            r2 = r10[r7]
            int r2 = r2.animationType
            if (r2 != r12) goto L_0x019c
            boolean r2 = r0.centered
            if (r2 == 0) goto L_0x017a
            int r2 = r24.getMeasuredWidth()
            int r5 = r13 * r3
            int r2 = r2 - r5
            int r5 = r0.currentStyle
            if (r5 != r4) goto L_0x016e
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x0170
        L_0x016e:
            r5 = 1082130432(0x40800000, float:4.0)
        L_0x0170:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r5
            int r2 = r2 / r12
            r5 = r2
            r2 = 1092616192(0x41200000, float:10.0)
            goto L_0x0180
        L_0x017a:
            r2 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L_0x0180:
            int r8 = r3 * r7
            int r5 = r5 + r8
            r8 = r10[r7]
            int r8 = r8.moveFromIndex
            int r8 = r8 * r3
            int r8 = r8 + r6
            float r5 = (float) r5
            float r9 = r0.transitionProgress
            float r5 = r5 * r9
            float r8 = (float) r8
            float r9 = r15 - r9
            float r8 = r8 * r9
            float r5 = r5 + r8
            int r5 = (int) r5
            r11.setImageX(r5)
            goto L_0x01d7
        L_0x019c:
            r2 = 1092616192(0x41200000, float:10.0)
            r5 = r10[r7]
            int r5 = r5.animationType
            r8 = -1
            if (r5 != r8) goto L_0x01d7
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x01d7
            int r5 = r24.getMeasuredWidth()
            int r8 = r13 * r3
            int r5 = r5 - r8
            int r8 = r0.currentStyle
            if (r8 != r4) goto L_0x01b9
            r8 = 1090519040(0x41000000, float:8.0)
            goto L_0x01bb
        L_0x01b9:
            r8 = 1082130432(0x40800000, float:4.0)
        L_0x01bb:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            int r5 = r5 / r12
            int r8 = r3 * r7
            int r5 = r5 + r8
            int r8 = r8 + r6
            float r5 = (float) r5
            float r9 = r0.transitionProgress
            float r5 = r5 * r9
            float r8 = (float) r8
            float r9 = r15 - r9
            float r8 = r8 * r9
            float r5 = r5 + r8
            int r5 = (int) r5
            r11.setImageX(r5)
            goto L_0x01d7
        L_0x01d5:
            r2 = 1092616192(0x41200000, float:10.0)
        L_0x01d7:
            r5 = 1065353216(0x3var_, float:1.0)
            r8 = 0
        L_0x01da:
            int r9 = r10.length
            r18 = 1
            int r9 = r9 + -1
            if (r7 == r9) goto L_0x0435
            int r9 = r0.currentStyle
            r19 = 1095761920(0x41500000, float:13.0)
            r20 = 1101529088(0x41a80000, float:21.0)
            r21 = 1117323264(0x42990000, float:76.5)
            r22 = 1099431936(0x41880000, float:17.0)
            r2 = 5
            r12 = 3
            if (r9 == r12) goto L_0x02fe
            if (r9 != r2) goto L_0x01f3
            goto L_0x02fe
        L_0x01f3:
            if (r9 != r4) goto L_0x02c6
            float r2 = r11.getCenterX()
            float r9 = r11.getCenterY()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r12 = (float) r12
            android.graphics.Paint r4 = r0.paint
            r1.drawCircle(r2, r9, r12, r4)
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x0221
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r4 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r4.<init>(r9, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r4
        L_0x0221:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.lang.String r4 = "voipgroup_listeningText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            float r9 = r5 * r21
            int r9 = (int) r9
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r4, r9)
            r2.setColor(r4)
            r4 = r3
            long r2 = java.lang.System.currentTimeMillis()
            r9 = r10[r7]
            long r19 = r9.lastUpdateTime
            long r19 = r2 - r19
            r21 = 100
            int r9 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r9 <= 0) goto L_0x029d
            r9 = r10[r7]
            long unused = r9.lastUpdateTime = r2
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            long r2 = (long) r2
            r9 = r10[r7]
            long r19 = r9.lastSpeakTime
            long r2 = r2 - r19
            r19 = 5
            int r9 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
            if (r9 > 0) goto L_0x0286
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r3 = 1
            r2.setShowWaves(r3, r0)
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.util.Random r3 = r0.random
            int r3 = r3.nextInt()
            int r3 = r3 % 100
            r12 = r4
            double r3 = (double) r3
            r2.setAmplitude(r3)
            goto L_0x029e
        L_0x0286:
            r12 = r4
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r3 = 0
            r2.setShowWaves(r3, r0)
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r3 = 0
            r2.setAmplitude(r3)
            goto L_0x029e
        L_0x029d:
            r12 = r4
        L_0x029e:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r3 = r11.getCenterX()
            float r4 = r11.getCenterY()
            r2.draw(r1, r3, r4, r0)
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            r23 = r6
            goto L_0x043a
        L_0x02c6:
            r12 = r3
            android.graphics.Paint r2 = r0.paint
            int r2 = r2.getAlpha()
            int r3 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r3 == 0) goto L_0x02da
            android.graphics.Paint r3 = r0.paint
            float r4 = (float) r2
            float r4 = r4 * r5
            int r4 = (int) r4
            r3.setAlpha(r4)
        L_0x02da:
            float r3 = r11.getCenterX()
            float r4 = r11.getCenterY()
            int r9 = r0.currentStyle
            r10 = 4
            if (r9 != r10) goto L_0x02e9
            r19 = 1099431936(0x41880000, float:17.0)
        L_0x02e9:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r9 = (float) r9
            android.graphics.Paint r10 = r0.paint
            r1.drawCircle(r3, r4, r9, r10)
            int r3 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r3 == 0) goto L_0x0436
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r2)
            goto L_0x0436
        L_0x02fe:
            r12 = r3
            r3 = 4
            r4 = 3
            if (r9 != r4) goto L_0x0364
            org.telegram.ui.Components.FragmentContextViewWavesDrawable r9 = org.telegram.ui.ActionBar.Theme.getFragmentContextViewWavesDrawable()
            r3 = 0
            r4 = 2
        L_0x0309:
            if (r3 >= r4) goto L_0x0364
            if (r3 != 0) goto L_0x0314
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r4 = r9.previousState
            if (r4 != 0) goto L_0x0314
        L_0x0311:
            r23 = r6
            goto L_0x035b
        L_0x0314:
            r4 = 1
            if (r3 != r4) goto L_0x031c
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r4 = r9.currentState
            if (r4 != 0) goto L_0x031c
            goto L_0x0311
        L_0x031c:
            if (r3 != 0) goto L_0x032f
            android.graphics.Paint r4 = r9.paint
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r15 = r9.previousState
            android.graphics.Shader r15 = r15.shader
            r4.setShader(r15)
            android.graphics.Paint r4 = r9.paint
            r15 = 255(0xff, float:3.57E-43)
            r4.setAlpha(r15)
            goto L_0x0344
        L_0x032f:
            android.graphics.Paint r4 = r9.paint
            org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r15 = r9.currentState
            android.graphics.Shader r15 = r15.shader
            r4.setShader(r15)
            android.graphics.Paint r4 = r9.paint
            r15 = 1132396544(0x437var_, float:255.0)
            float r2 = r9.progressToState
            float r2 = r2 * r15
            int r2 = (int) r2
            r4.setAlpha(r2)
        L_0x0344:
            float r2 = r11.getCenterX()
            float r4 = r11.getCenterY()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r15 = (float) r15
            r23 = r6
            android.graphics.Paint r6 = r9.paint
            r1.drawCircle(r2, r4, r15, r6)
            r24.invalidate()
        L_0x035b:
            int r3 = r3 + 1
            r6 = r23
            r2 = 5
            r4 = 2
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0309
        L_0x0364:
            r23 = r6
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x039c
            int r2 = r0.currentStyle
            r3 = 5
            if (r2 != r3) goto L_0x038a
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.<init>(r4, r6)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r3
            goto L_0x039c
        L_0x038a:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r3.<init>(r4, r6)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r3
        L_0x039c:
            int r2 = r0.currentStyle
            r3 = 5
            if (r2 != r3) goto L_0x03b7
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.lang.String r3 = "voipgroup_speakingText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            float r4 = r5 * r21
            int r4 = (int) r4
            int r3 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r4)
            r2.setColor(r3)
        L_0x03b7:
            r2 = r10[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x03e2
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r3 = 1
            r2.setShowWaves(r3, r0)
            r2 = r10[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            r4 = 1097859072(0x41700000, float:15.0)
            float r2 = r2 * r4
            r4 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r4 = r4.wavesDrawable
            double r2 = (double) r2
            r4.setAmplitude(r2)
            r3 = 0
            goto L_0x03ec
        L_0x03e2:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r3 = 0
            r2.setShowWaves(r3, r0)
        L_0x03ec:
            int r2 = r0.currentStyle
            r4 = 5
            if (r2 != r4) goto L_0x0408
            long r19 = android.os.SystemClock.uptimeMillis()
            r2 = r10[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            long r3 = r2.lastSpeakTime
            long r19 = r19 - r3
            r2 = 500(0x1f4, double:2.47E-321)
            int r4 = (r19 > r2 ? 1 : (r19 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0408
            java.lang.Runnable r2 = r0.updateDelegate
            r2.run()
        L_0x0408:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            int r2 = r0.currentStyle
            r3 = 5
            if (r2 != r3) goto L_0x042a
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r3 = r11.getCenterX()
            float r4 = r11.getCenterY()
            r2.draw(r1, r3, r4, r0)
            r24.invalidate()
        L_0x042a:
            r2 = r10[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            goto L_0x043a
        L_0x0435:
            r12 = r3
        L_0x0436:
            r23 = r6
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x043a:
            r11.setAlpha(r5)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0458
            r25.save()
            float r3 = r11.getCenterX()
            float r4 = r11.getCenterY()
            r1.scale(r2, r2, r3, r4)
            r11.draw(r1)
            r25.restore()
            goto L_0x045b
        L_0x0458:
            r11.draw(r1)
        L_0x045b:
            if (r8 == 0) goto L_0x0460
            r25.restore()
        L_0x0460:
            int r14 = r14 + 1
            r3 = r12
            r6 = r23
            r2 = 1
            r4 = 4
            r8 = 3
            r9 = 1092616192(0x41200000, float:10.0)
            r12 = 2
            goto L_0x0097
        L_0x046d:
            r12 = r3
            r23 = r6
            int r7 = r7 + -1
            r2 = 1
            r4 = 4
            r8 = 3
            r9 = 1092616192(0x41200000, float:10.0)
            r12 = 2
            goto L_0x0094
        L_0x047a:
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
