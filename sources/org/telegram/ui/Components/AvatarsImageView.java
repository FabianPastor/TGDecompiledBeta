package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import java.util.Random;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;

public class AvatarsImageView extends FrameLayout {
    DrawingState[] animatingStates = new DrawingState[3];
    boolean centered;
    protected int count;
    DrawingState[] currentStates = new DrawingState[3];
    int currentStyle;
    private boolean isInCall;
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
        ofFloat.addUpdateListener(new AvatarsImageView$$ExternalSyntheticLambda0(this));
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
    public /* synthetic */ void lambda$commitTransition$0(ValueAnimator valueAnimator) {
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
        public long id;
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

    public AvatarsImageView(Context context, boolean z) {
        super(context);
        for (int i = 0; i < 3; i++) {
            this.currentStates[i] = new DrawingState();
            ImageReceiver unused = this.currentStates[i].imageReceiver = new ImageReceiver(this);
            this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[i].avatarDrawable = new AvatarDrawable();
            this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            this.animatingStates[i] = new DrawingState();
            ImageReceiver unused3 = this.animatingStates[i].imageReceiver = new ImageReceiver(this);
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[i].avatarDrawable = new AvatarDrawable();
            this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        }
        this.isInCall = z;
        setWillNotDraw(false);
        this.xRefP.setColor(0);
        this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setObject(int i, int i2, TLObject tLObject) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$Chat tLRPC$Chat2;
        long unused = this.animatingStates[i].id = 0;
        DrawingState[] drawingStateArr = this.animatingStates;
        TLRPC$User tLRPC$User = null;
        drawingStateArr[i].participant = null;
        if (tLObject == null) {
            drawingStateArr[i].imageReceiver.setImageBitmap((Drawable) null);
            invalidate();
            return;
        }
        long unused2 = drawingStateArr[i].lastSpeakTime = -1;
        if (tLObject instanceof TLRPC$TL_groupCallParticipant) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) tLObject;
            this.animatingStates[i].participant = tLRPC$TL_groupCallParticipant;
            long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
            if (DialogObject.isUserDialog(peerId)) {
                TLRPC$User user = MessagesController.getInstance(i2).getUser(Long.valueOf(peerId));
                this.animatingStates[i].avatarDrawable.setInfo(user);
                tLRPC$User = user;
                tLRPC$Chat2 = null;
            } else {
                tLRPC$Chat2 = MessagesController.getInstance(i2).getChat(Long.valueOf(-peerId));
                this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat2);
            }
            if (this.currentStyle != 4) {
                long unused3 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
            } else if (peerId == AccountInstance.getInstance(i2).getUserConfig().getClientUserId()) {
                long unused4 = this.animatingStates[i].lastSpeakTime = 0;
            } else if (this.isInCall) {
                long unused5 = this.animatingStates[i].lastSpeakTime = tLRPC$TL_groupCallParticipant.lastActiveDate;
            } else {
                long unused6 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
            }
            long unused7 = this.animatingStates[i].id = peerId;
            tLRPC$Chat = tLRPC$Chat2;
        } else if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User2 = (TLRPC$User) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$User2);
            long unused8 = this.animatingStates[i].id = tLRPC$User2.id;
            tLRPC$User = tLRPC$User2;
            tLRPC$Chat = null;
        } else {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat);
            long unused9 = this.animatingStates[i].id = -tLRPC$Chat.id;
        }
        if (tLRPC$User != null) {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$User, this.animatingStates[i].avatarDrawable);
        } else {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$Chat, this.animatingStates[i].avatarDrawable);
        }
        int i3 = this.currentStyle;
        boolean z = i3 == 4 || i3 == 10;
        this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(z ? 16.0f : 12.0f));
        float dp = (float) AndroidUtilities.dp(z ? 32.0f : 24.0f);
        this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, dp, dp);
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x04f5  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x04fa  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x04fd A[SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r34) {
        /*
            r33 = this;
            r0 = r33
            r8 = r34
            r9 = 1
            r0.wasDraw = r9
            int r1 = r0.currentStyle
            r10 = 4
            r11 = 10
            if (r1 == r10) goto L_0x0013
            if (r1 != r11) goto L_0x0011
            goto L_0x0013
        L_0x0011:
            r13 = 0
            goto L_0x0014
        L_0x0013:
            r13 = 1
        L_0x0014:
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r13 == 0) goto L_0x001b
            r2 = 1107296256(0x42000000, float:32.0)
            goto L_0x001d
        L_0x001b:
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x001d:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0.currentStyle
            r15 = 11
            if (r2 != r15) goto L_0x0030
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
        L_0x002d:
            r16 = r1
            goto L_0x003a
        L_0x0030:
            if (r13 == 0) goto L_0x0033
            goto L_0x0035
        L_0x0033:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x0035:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x002d
        L_0x003a:
            r1 = 0
            r2 = 0
        L_0x003c:
            r3 = 0
            r7 = 3
            if (r1 >= r7) goto L_0x0052
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r5 = r0.currentStates
            r5 = r5[r1]
            long r5 = r5.id
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x004f
            int r2 = r2 + 1
        L_0x004f:
            int r1 = r1 + 1
            goto L_0x003c
        L_0x0052:
            int r1 = r0.currentStyle
            if (r1 == 0) goto L_0x0064
            if (r1 == r11) goto L_0x0064
            if (r1 != r15) goto L_0x005b
            goto L_0x0064
        L_0x005b:
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r17 = r1
            goto L_0x0066
        L_0x0064:
            r17 = 0
        L_0x0066:
            boolean r1 = r0.centered
            r18 = 1082130432(0x40800000, float:4.0)
            r19 = 1090519040(0x41000000, float:8.0)
            r6 = 2
            if (r1 == 0) goto L_0x0086
            int r1 = r33.getMeasuredWidth()
            int r2 = r2 * r16
            int r1 = r1 - r2
            if (r13 == 0) goto L_0x007b
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x007d
        L_0x007b:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x007d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r6
            r20 = r1
            goto L_0x0088
        L_0x0086:
            r20 = r17
        L_0x0088:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x009a
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x009a
            r1 = 1
            goto L_0x009b
        L_0x009a:
            r1 = 0
        L_0x009b:
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x00ab
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x00bd
        L_0x00ab:
            if (r2 == r7) goto L_0x00bd
            android.graphics.Paint r2 = r0.paint
            if (r1 == 0) goto L_0x00b4
            java.lang.String r1 = "returnToCallMutedBackground"
            goto L_0x00b6
        L_0x00b4:
            java.lang.String r1 = "returnToCallBackground"
        L_0x00b6:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setColor(r1)
        L_0x00bd:
            r1 = 0
            r21 = 0
        L_0x00c0:
            if (r1 >= r7) goto L_0x00d3
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r1]
            long r22 = r2.id
            int r2 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x00d0
            int r21 = r21 + 1
        L_0x00d0:
            int r1 = r1 + 1
            goto L_0x00c0
        L_0x00d3:
            int r1 = r0.currentStyle
            r5 = 5
            if (r1 == 0) goto L_0x00e8
            if (r1 == r9) goto L_0x00e8
            if (r1 == r7) goto L_0x00e8
            if (r1 == r10) goto L_0x00e8
            if (r1 == r5) goto L_0x00e8
            if (r1 == r11) goto L_0x00e8
            if (r1 != r15) goto L_0x00e5
            goto L_0x00e8
        L_0x00e5:
            r22 = 0
            goto L_0x00ea
        L_0x00e8:
            r22 = 1
        L_0x00ea:
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 0
            if (r22 == 0) goto L_0x011b
            if (r1 != r11) goto L_0x00f8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            goto L_0x00f9
        L_0x00f8:
            r1 = 0
        L_0x00f9:
            float r3 = -r1
            int r2 = r33.getMeasuredWidth()
            float r2 = (float) r2
            float r4 = r2 + r1
            int r2 = r33.getMeasuredHeight()
            float r2 = (float) r2
            float r25 = r2 + r1
            r26 = 255(0xff, float:3.57E-43)
            r27 = 31
            r1 = r34
            r2 = r3
            r12 = 5
            r5 = r25
            r12 = 2
            r6 = r26
            r7 = r27
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x011c
        L_0x011b:
            r12 = 2
        L_0x011c:
            r6 = 2
        L_0x011d:
            if (r6 < 0) goto L_0x0514
            r1 = 0
        L_0x0120:
            if (r1 >= r12) goto L_0x0508
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x012f
            float r3 = r0.transitionProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 != 0) goto L_0x012f
        L_0x012c:
            r9 = 5
            goto L_0x04fd
        L_0x012f:
            if (r1 != 0) goto L_0x0134
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.animatingStates
            goto L_0x0136
        L_0x0134:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
        L_0x0136:
            if (r1 != r9) goto L_0x0147
            float r4 = r0.transitionProgress
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0147
            r4 = r3[r6]
            int r4 = r4.animationType
            if (r4 == r9) goto L_0x0147
            goto L_0x012c
        L_0x0147:
            r4 = r3[r6]
            org.telegram.messenger.ImageReceiver r4 = r4.imageReceiver
            boolean r5 = r4.hasImageSet()
            if (r5 != 0) goto L_0x0154
            goto L_0x012c
        L_0x0154:
            if (r1 != 0) goto L_0x0178
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x016f
            int r5 = r33.getMeasuredWidth()
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0166
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0168
        L_0x0166:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0168:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x0171
        L_0x016f:
            r5 = r17
        L_0x0171:
            int r7 = r16 * r6
            int r5 = r5 + r7
            r4.setImageX(r5)
            goto L_0x017f
        L_0x0178:
            int r5 = r16 * r6
            int r5 = r20 + r5
            r4.setImageX(r5)
        L_0x017f:
            int r5 = r0.currentStyle
            if (r5 == 0) goto L_0x0198
            if (r5 == r11) goto L_0x0198
            if (r5 != r15) goto L_0x0188
            goto L_0x0198
        L_0x0188:
            if (r5 != r10) goto L_0x018d
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x018f
        L_0x018d:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x018f:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setImageY(r5)
            goto L_0x01a4
        L_0x0198:
            int r5 = r33.getMeasuredHeight()
            int r5 = r5 - r14
            float r5 = (float) r5
            r7 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r7
            r4.setImageY(r5)
        L_0x01a4:
            float r5 = r0.transitionProgress
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x025d
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r9) goto L_0x01cd
            r34.save()
            float r5 = r0.transitionProgress
            float r7 = r2 - r5
            float r5 = r2 - r5
            float r15 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r8.scale(r7, r5, r15, r11)
            float r5 = r0.transitionProgress
            float r5 = r2 - r5
        L_0x01ca:
            r7 = 1
            goto L_0x0260
        L_0x01cd:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != 0) goto L_0x01e8
            r34.save()
            float r5 = r0.transitionProgress
            float r7 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r8.scale(r5, r5, r7, r11)
            float r5 = r0.transitionProgress
            goto L_0x01ca
        L_0x01e8:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r12) goto L_0x0228
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0209
            int r5 = r33.getMeasuredWidth()
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0200
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0202
        L_0x0200:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0202:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x020b
        L_0x0209:
            r5 = r17
        L_0x020b:
            int r7 = r16 * r6
            int r5 = r5 + r7
            r7 = r3[r6]
            int r7 = r7.moveFromIndex
            int r7 = r7 * r16
            int r7 = r20 + r7
            float r5 = (float) r5
            float r11 = r0.transitionProgress
            float r5 = r5 * r11
            float r7 = (float) r7
            float r11 = r2 - r11
            float r7 = r7 * r11
            float r5 = r5 + r7
            int r5 = (int) r5
            r4.setImageX(r5)
            goto L_0x025d
        L_0x0228:
            r5 = r3[r6]
            int r5 = r5.animationType
            r7 = -1
            if (r5 != r7) goto L_0x025d
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x025d
            int r5 = r33.getMeasuredWidth()
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0241
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0243
        L_0x0241:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0243:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            int r7 = r16 * r6
            int r5 = r5 + r7
            int r7 = r20 + r7
            float r5 = (float) r5
            float r11 = r0.transitionProgress
            float r5 = r5 * r11
            float r7 = (float) r7
            float r11 = r2 - r11
            float r7 = r7 * r11
            float r5 = r5 + r7
            int r5 = (int) r5
            r4.setImageX(r5)
        L_0x025d:
            r5 = 1065353216(0x3var_, float:1.0)
            r7 = 0
        L_0x0260:
            int r11 = r3.length
            int r11 = r11 - r9
            if (r6 == r11) goto L_0x04d4
            int r11 = r0.currentStyle
            r15 = 1097859072(0x41700000, float:15.0)
            r28 = 1101529088(0x41a80000, float:21.0)
            java.lang.String r29 = "voipgroup_speakingText"
            r30 = 1117323264(0x42990000, float:76.5)
            r31 = 1095761920(0x41500000, float:13.0)
            r32 = 1099431936(0x41880000, float:17.0)
            if (r11 == r9) goto L_0x03f4
            r12 = 3
            if (r11 == r12) goto L_0x03f4
            r12 = 5
            if (r11 != r12) goto L_0x027c
            goto L_0x03f4
        L_0x027c:
            if (r11 == r10) goto L_0x02d1
            r12 = 10
            if (r11 != r12) goto L_0x0283
            goto L_0x02d1
        L_0x0283:
            if (r22 == 0) goto L_0x029d
            float r3 = r4.getCenterX()
            float r11 = r4.getCenterY()
            if (r13 == 0) goto L_0x0291
            r31 = 1099431936(0x41880000, float:17.0)
        L_0x0291:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r31)
            float r12 = (float) r12
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r3, r11, r12, r15)
            goto L_0x04d4
        L_0x029d:
            android.graphics.Paint r3 = r0.paint
            int r3 = r3.getAlpha()
            int r11 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r11 == 0) goto L_0x02b0
            android.graphics.Paint r11 = r0.paint
            float r12 = (float) r3
            float r12 = r12 * r5
            int r12 = (int) r12
            r11.setAlpha(r12)
        L_0x02b0:
            float r11 = r4.getCenterX()
            float r12 = r4.getCenterY()
            if (r13 == 0) goto L_0x02bc
            r31 = 1099431936(0x41880000, float:17.0)
        L_0x02bc:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r31)
            float r15 = (float) r15
            android.graphics.Paint r10 = r0.paint
            r8.drawCircle(r11, r12, r15, r10)
            int r10 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x04d4
            android.graphics.Paint r10 = r0.paint
            r10.setAlpha(r3)
            goto L_0x04d4
        L_0x02d1:
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r12 = (float) r12
            android.graphics.Paint r2 = r0.xRefP
            r8.drawCircle(r10, r11, r12, r2)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x02fd
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r10
        L_0x02fd:
            int r2 = r0.currentStyle
            r10 = 10
            if (r2 != r10) goto L_0x0318
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r11 = r5 * r30
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r2.setColor(r10)
            goto L_0x032e
        L_0x0318:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.lang.String r10 = "voipgroup_listeningText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r11 = r5 * r30
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r2.setColor(r10)
        L_0x032e:
            long r10 = java.lang.System.currentTimeMillis()
            r2 = r3[r6]
            long r28 = r2.lastUpdateTime
            long r28 = r10 - r28
            r30 = 100
            int r2 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1))
            if (r2 <= 0) goto L_0x03cd
            r2 = r3[r6]
            long unused = r2.lastUpdateTime = r10
            int r2 = r0.currentStyle
            r10 = 10
            if (r2 != r10) goto L_0x0382
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x0377
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x0377
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.setShowWaves(r9, r0)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r15
            r11 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r11.wavesDrawable
            double r9 = (double) r2
            r11.setAmplitude(r9)
            goto L_0x03cd
        L_0x0377:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 0
            r2.setShowWaves(r9, r0)
            goto L_0x03cd
        L_0x0382:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            long r9 = (long) r2
            r2 = r3[r6]
            long r28 = r2.lastSpeakTime
            long r9 = r9 - r28
            r28 = 5
            int r2 = (r9 > r28 ? 1 : (r9 == r28 ? 0 : -1))
            if (r2 > 0) goto L_0x03b8
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 1
            r2.setShowWaves(r9, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.util.Random r9 = r0.random
            int r9 = r9.nextInt()
            int r9 = r9 % 100
            double r9 = (double) r9
            r2.setAmplitude(r9)
            goto L_0x03cd
        L_0x03b8:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 0
            r2.setShowWaves(r9, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 0
            r2.setAmplitude(r9)
        L_0x03cd:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r9 = r4.getCenterX()
            float r10 = r4.getCenterY()
            r2.draw(r8, r9, r10, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            r9 = 5
            goto L_0x04d7
        L_0x03f4:
            float r2 = r4.getCenterX()
            float r9 = r4.getCenterY()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r31)
            float r10 = (float) r10
            android.graphics.Paint r11 = r0.xRefP
            r8.drawCircle(r2, r9, r10, r11)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x043a
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x0428
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
            goto L_0x043a
        L_0x0428:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
        L_0x043a:
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x0453
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r10 = r5 * r30
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r2.setColor(r9)
        L_0x0453:
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x0481
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x0481
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 1
            r2.setShowWaves(r9, r0)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r15
            r10 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            double r11 = (double) r2
            r10.setAmplitude(r11)
            r10 = 0
            goto L_0x048c
        L_0x0481:
            r9 = 1
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 0
            r2.setShowWaves(r10, r0)
        L_0x048c:
            int r2 = r0.currentStyle
            r11 = 5
            if (r2 != r11) goto L_0x04a7
            long r11 = android.os.SystemClock.uptimeMillis()
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            long r9 = r2.lastSpeakTime
            long r11 = r11 - r9
            r9 = 500(0x1f4, double:2.47E-321)
            int r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x04a7
            java.lang.Runnable r2 = r0.updateDelegate
            r2.run()
        L_0x04a7:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x04c9
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r2.draw(r8, r10, r11, r0)
            r33.invalidate()
        L_0x04c9:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            goto L_0x04d7
        L_0x04d4:
            r9 = 5
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x04d7:
            r4.setAlpha(r5)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x04f5
            r34.save()
            float r3 = r4.getCenterX()
            float r5 = r4.getCenterY()
            r8.scale(r2, r2, r3, r5)
            r4.draw(r8)
            r34.restore()
            goto L_0x04f8
        L_0x04f5:
            r4.draw(r8)
        L_0x04f8:
            if (r7 == 0) goto L_0x04fd
            r34.restore()
        L_0x04fd:
            int r1 = r1 + 1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            r15 = 11
            goto L_0x0120
        L_0x0508:
            r9 = 5
            int r6 = r6 + -1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            r15 = 11
            goto L_0x011d
        L_0x0514:
            if (r22 == 0) goto L_0x0519
            r34.restore()
        L_0x0519:
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

    public void setCount(int i) {
        this.count = i;
        requestLayout();
    }

    public void reset() {
        for (int i = 0; i < this.animatingStates.length; i++) {
            setObject(0, 0, (TLObject) null);
        }
    }
}
