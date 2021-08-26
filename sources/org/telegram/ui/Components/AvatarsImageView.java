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

    public AvatarsImageView(Context context, boolean z) {
        super(context);
        for (int i = 0; i < 3; i++) {
            this.currentStates[i] = new DrawingState();
            ImageReceiver unused = this.currentStates[i].imageReceiver = new ImageReceiver(this);
            this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[i].avatarDrawable = new AvatarDrawable();
            this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(9.0f));
            this.animatingStates[i] = new DrawingState();
            ImageReceiver unused3 = this.animatingStates[i].imageReceiver = new ImageReceiver(this);
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[i].avatarDrawable = new AvatarDrawable();
            this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(9.0f));
        }
        this.isInCall = z;
        setWillNotDraw(false);
        this.xRefP.setColor(0);
        this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setObject(int i, int i2, TLObject tLObject) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$Chat tLRPC$Chat2;
        boolean z = false;
        int unused = this.animatingStates[i].id = 0;
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
            int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
            if (peerId > 0) {
                TLRPC$User user = MessagesController.getInstance(i2).getUser(Integer.valueOf(peerId));
                this.animatingStates[i].avatarDrawable.setInfo(user);
                TLRPC$User tLRPC$User2 = user;
                tLRPC$Chat2 = null;
                tLRPC$User = tLRPC$User2;
            } else {
                tLRPC$Chat2 = MessagesController.getInstance(i2).getChat(Integer.valueOf(-peerId));
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
            int unused7 = this.animatingStates[i].id = peerId;
            tLRPC$Chat = tLRPC$Chat2;
        } else if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User3 = (TLRPC$User) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$User3);
            int unused8 = this.animatingStates[i].id = tLRPC$User3.id;
            tLRPC$User = tLRPC$User3;
            tLRPC$Chat = null;
        } else {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat);
            int unused9 = this.animatingStates[i].id = -tLRPC$Chat.id;
        }
        if (tLRPC$User != null) {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$User, this.animatingStates[i].avatarDrawable);
        } else {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$Chat, this.animatingStates[i].avatarDrawable);
        }
        int i3 = this.currentStyle;
        if (i3 == 4 || i3 == 10) {
            z = true;
        }
        this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(z ? 16.0f : 12.0f));
        float dp = (float) AndroidUtilities.dp(z ? 32.0f : 24.0f);
        this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, dp, dp);
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0248  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x04e8 A[SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r33) {
        /*
            r32 = this;
            r0 = r32
            r8 = r33
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
            if (r13 == 0) goto L_0x0024
            goto L_0x0026
        L_0x0024:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x0026:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r1 = 0
            r2 = 0
        L_0x002c:
            r7 = 3
            if (r1 >= r7) goto L_0x003e
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
            r3 = r3[r1]
            int r3 = r3.id
            if (r3 == 0) goto L_0x003b
            int r2 = r2 + 1
        L_0x003b:
            int r1 = r1 + 1
            goto L_0x002c
        L_0x003e:
            int r1 = r0.currentStyle
            if (r1 == 0) goto L_0x004e
            if (r1 != r11) goto L_0x0045
            goto L_0x004e
        L_0x0045:
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r16 = r1
            goto L_0x0050
        L_0x004e:
            r16 = 0
        L_0x0050:
            boolean r1 = r0.centered
            r17 = 1082130432(0x40800000, float:4.0)
            r18 = 1090519040(0x41000000, float:8.0)
            r6 = 2
            if (r1 == 0) goto L_0x0070
            int r1 = r32.getMeasuredWidth()
            int r2 = r2 * r15
            int r1 = r1 - r2
            if (r13 == 0) goto L_0x0065
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x0067
        L_0x0065:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x0067:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r6
            r19 = r1
            goto L_0x0072
        L_0x0070:
            r19 = r16
        L_0x0072:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0084
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x0084
            r1 = 1
            goto L_0x0085
        L_0x0084:
            r1 = 0
        L_0x0085:
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x0095
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x00a7
        L_0x0095:
            if (r2 == r7) goto L_0x00a7
            android.graphics.Paint r2 = r0.paint
            if (r1 == 0) goto L_0x009e
            java.lang.String r1 = "returnToCallMutedBackground"
            goto L_0x00a0
        L_0x009e:
            java.lang.String r1 = "returnToCallBackground"
        L_0x00a0:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setColor(r1)
        L_0x00a7:
            r1 = 0
            r20 = 0
        L_0x00aa:
            if (r1 >= r7) goto L_0x00bb
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r1]
            int r2 = r2.id
            if (r2 == 0) goto L_0x00b8
            int r20 = r20 + 1
        L_0x00b8:
            int r1 = r1 + 1
            goto L_0x00aa
        L_0x00bb:
            int r1 = r0.currentStyle
            r5 = 5
            if (r1 == 0) goto L_0x00ce
            if (r1 == r9) goto L_0x00ce
            if (r1 == r7) goto L_0x00ce
            if (r1 == r10) goto L_0x00ce
            if (r1 == r5) goto L_0x00ce
            if (r1 != r11) goto L_0x00cb
            goto L_0x00ce
        L_0x00cb:
            r21 = 0
            goto L_0x00d0
        L_0x00ce:
            r21 = 1
        L_0x00d0:
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 0
            if (r21 == 0) goto L_0x0101
            if (r1 != r11) goto L_0x00de
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            goto L_0x00df
        L_0x00de:
            r1 = 0
        L_0x00df:
            float r3 = -r1
            int r2 = r32.getMeasuredWidth()
            float r2 = (float) r2
            float r4 = r2 + r1
            int r2 = r32.getMeasuredHeight()
            float r2 = (float) r2
            float r24 = r2 + r1
            r25 = 255(0xff, float:3.57E-43)
            r26 = 31
            r1 = r33
            r2 = r3
            r12 = 5
            r5 = r24
            r12 = 2
            r6 = r25
            r7 = r26
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0102
        L_0x0101:
            r12 = 2
        L_0x0102:
            r6 = 2
        L_0x0103:
            if (r6 < 0) goto L_0x04fb
            r1 = 0
        L_0x0106:
            if (r1 >= r12) goto L_0x04f1
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0115
            float r3 = r0.transitionProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 != 0) goto L_0x0115
        L_0x0112:
            r9 = 5
            goto L_0x04e8
        L_0x0115:
            if (r1 != 0) goto L_0x011a
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.animatingStates
            goto L_0x011c
        L_0x011a:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
        L_0x011c:
            if (r1 != r9) goto L_0x012d
            float r4 = r0.transitionProgress
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x012d
            r4 = r3[r6]
            int r4 = r4.animationType
            if (r4 == r9) goto L_0x012d
            goto L_0x0112
        L_0x012d:
            r4 = r3[r6]
            org.telegram.messenger.ImageReceiver r4 = r4.imageReceiver
            boolean r5 = r4.hasImageSet()
            if (r5 != 0) goto L_0x013a
            goto L_0x0112
        L_0x013a:
            if (r1 != 0) goto L_0x015e
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0155
            int r5 = r32.getMeasuredWidth()
            int r7 = r20 * r15
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x014c
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x014e
        L_0x014c:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x014e:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x0157
        L_0x0155:
            r5 = r16
        L_0x0157:
            int r7 = r15 * r6
            int r5 = r5 + r7
            r4.setImageX(r5)
            goto L_0x0165
        L_0x015e:
            int r5 = r15 * r6
            int r5 = r19 + r5
            r4.setImageX(r5)
        L_0x0165:
            int r5 = r0.currentStyle
            if (r5 == 0) goto L_0x017c
            if (r5 != r11) goto L_0x016c
            goto L_0x017c
        L_0x016c:
            if (r5 != r10) goto L_0x0171
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x0173
        L_0x0171:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0173:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setImageY(r5)
            goto L_0x0188
        L_0x017c:
            int r5 = r32.getMeasuredHeight()
            int r5 = r5 - r14
            float r5 = (float) r5
            r7 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r7
            r4.setImageY(r5)
        L_0x0188:
            float r5 = r0.transitionProgress
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0241
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r9) goto L_0x01b1
            r33.save()
            float r5 = r0.transitionProgress
            float r7 = r2 - r5
            float r5 = r2 - r5
            float r11 = r4.getCenterX()
            float r10 = r4.getCenterY()
            r8.scale(r7, r5, r11, r10)
            float r5 = r0.transitionProgress
            float r5 = r2 - r5
        L_0x01ae:
            r7 = 1
            goto L_0x0244
        L_0x01b1:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != 0) goto L_0x01cc
            r33.save()
            float r5 = r0.transitionProgress
            float r7 = r4.getCenterX()
            float r10 = r4.getCenterY()
            r8.scale(r5, r5, r7, r10)
            float r5 = r0.transitionProgress
            goto L_0x01ae
        L_0x01cc:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r12) goto L_0x020c
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x01ed
            int r5 = r32.getMeasuredWidth()
            int r7 = r20 * r15
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x01e4
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x01e6
        L_0x01e4:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x01e6:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x01ef
        L_0x01ed:
            r5 = r16
        L_0x01ef:
            int r7 = r15 * r6
            int r5 = r5 + r7
            r7 = r3[r6]
            int r7 = r7.moveFromIndex
            int r7 = r7 * r15
            int r7 = r19 + r7
            float r5 = (float) r5
            float r10 = r0.transitionProgress
            float r5 = r5 * r10
            float r7 = (float) r7
            float r10 = r2 - r10
            float r7 = r7 * r10
            float r5 = r5 + r7
            int r5 = (int) r5
            r4.setImageX(r5)
            goto L_0x0241
        L_0x020c:
            r5 = r3[r6]
            int r5 = r5.animationType
            r7 = -1
            if (r5 != r7) goto L_0x0241
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0241
            int r5 = r32.getMeasuredWidth()
            int r7 = r20 * r15
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0225
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0227
        L_0x0225:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0227:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            int r7 = r15 * r6
            int r5 = r5 + r7
            int r7 = r19 + r7
            float r5 = (float) r5
            float r10 = r0.transitionProgress
            float r5 = r5 * r10
            float r7 = (float) r7
            float r10 = r2 - r10
            float r7 = r7 * r10
            float r5 = r5 + r7
            int r5 = (int) r5
            r4.setImageX(r5)
        L_0x0241:
            r5 = 1065353216(0x3var_, float:1.0)
            r7 = 0
        L_0x0244:
            int r10 = r3.length
            int r10 = r10 - r9
            if (r6 == r10) goto L_0x04bf
            int r10 = r0.currentStyle
            r11 = 1097859072(0x41700000, float:15.0)
            r27 = 1101529088(0x41a80000, float:21.0)
            java.lang.String r28 = "voipgroup_speakingText"
            r29 = 1117323264(0x42990000, float:76.5)
            r30 = 1095761920(0x41500000, float:13.0)
            r31 = 1099431936(0x41880000, float:17.0)
            if (r10 == r9) goto L_0x03dd
            r12 = 3
            if (r10 == r12) goto L_0x03dd
            r12 = 5
            if (r10 != r12) goto L_0x0260
            goto L_0x03dd
        L_0x0260:
            r12 = 4
            if (r10 == r12) goto L_0x02b6
            r12 = 10
            if (r10 != r12) goto L_0x0268
            goto L_0x02b6
        L_0x0268:
            if (r21 == 0) goto L_0x0282
            float r3 = r4.getCenterX()
            float r10 = r4.getCenterY()
            if (r13 == 0) goto L_0x0276
            r30 = 1099431936(0x41880000, float:17.0)
        L_0x0276:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r11 = (float) r11
            android.graphics.Paint r12 = r0.xRefP
            r8.drawCircle(r3, r10, r11, r12)
            goto L_0x04bf
        L_0x0282:
            android.graphics.Paint r3 = r0.paint
            int r3 = r3.getAlpha()
            int r10 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x0295
            android.graphics.Paint r10 = r0.paint
            float r11 = (float) r3
            float r11 = r11 * r5
            int r11 = (int) r11
            r10.setAlpha(r11)
        L_0x0295:
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            if (r13 == 0) goto L_0x02a1
            r30 = 1099431936(0x41880000, float:17.0)
        L_0x02a1:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r12 = (float) r12
            android.graphics.Paint r9 = r0.paint
            r8.drawCircle(r10, r11, r12, r9)
            int r9 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x04bf
            android.graphics.Paint r9 = r0.paint
            r9.setAlpha(r3)
            goto L_0x04bf
        L_0x02b6:
            float r9 = r4.getCenterX()
            float r10 = r4.getCenterY()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r31)
            float r12 = (float) r12
            android.graphics.Paint r2 = r0.xRefP
            r8.drawCircle(r9, r10, r12, r2)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x02e2
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r9.<init>(r10, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
        L_0x02e2:
            int r2 = r0.currentStyle
            r9 = 10
            if (r2 != r9) goto L_0x02fd
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r28)
            float r10 = r5 * r29
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r2.setColor(r9)
            goto L_0x0313
        L_0x02fd:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.lang.String r9 = "voipgroup_listeningText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            float r10 = r5 * r29
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r2.setColor(r9)
        L_0x0313:
            long r9 = java.lang.System.currentTimeMillis()
            r2 = r3[r6]
            long r27 = r2.lastUpdateTime
            long r27 = r9 - r27
            r29 = 100
            int r2 = (r27 > r29 ? 1 : (r27 == r29 ? 0 : -1))
            if (r2 <= 0) goto L_0x03b4
            r2 = r3[r6]
            long unused = r2.lastUpdateTime = r9
            int r2 = r0.currentStyle
            r9 = 10
            if (r2 != r9) goto L_0x0368
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x035d
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r23 ? 1 : (r2 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x035d
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 1
            r2.setShowWaves(r10, r0)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r11
            r10 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            double r11 = (double) r2
            r10.setAmplitude(r11)
            goto L_0x03b6
        L_0x035d:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 0
            r2.setShowWaves(r10, r0)
            goto L_0x03b6
        L_0x0368:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            long r10 = (long) r2
            r2 = r3[r6]
            long r27 = r2.lastSpeakTime
            long r10 = r10 - r27
            r27 = 5
            int r2 = (r10 > r27 ? 1 : (r10 == r27 ? 0 : -1))
            if (r2 > 0) goto L_0x039e
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 1
            r2.setShowWaves(r10, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.util.Random r10 = r0.random
            int r10 = r10.nextInt()
            int r10 = r10 % 100
            double r10 = (double) r10
            r2.setAmplitude(r10)
            goto L_0x03b6
        L_0x039e:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 0
            r2.setShowWaves(r10, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 0
            r2.setAmplitude(r10)
            goto L_0x03b6
        L_0x03b4:
            r9 = 10
        L_0x03b6:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r2.draw(r8, r10, r11, r0)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            r9 = 5
            goto L_0x04c2
        L_0x03dd:
            r9 = 10
            float r2 = r4.getCenterX()
            float r10 = r4.getCenterY()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r12 = (float) r12
            android.graphics.Paint r9 = r0.xRefP
            r8.drawCircle(r2, r10, r12, r9)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x0425
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x0413
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r9.<init>(r10, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
            goto L_0x0425
        L_0x0413:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r9.<init>(r10, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
        L_0x0425:
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x043e
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r28)
            float r10 = r5 * r29
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r2.setColor(r9)
        L_0x043e:
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x046c
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r23 ? 1 : (r2 == r23 ? 0 : -1))
            if (r2 <= 0) goto L_0x046c
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 1
            r2.setShowWaves(r9, r0)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r11
            r10 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r10.wavesDrawable
            double r11 = (double) r2
            r10.setAmplitude(r11)
            r10 = 0
            goto L_0x0477
        L_0x046c:
            r9 = 1
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r10 = 0
            r2.setShowWaves(r10, r0)
        L_0x0477:
            int r2 = r0.currentStyle
            r11 = 5
            if (r2 != r11) goto L_0x0492
            long r11 = android.os.SystemClock.uptimeMillis()
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            long r9 = r2.lastSpeakTime
            long r11 = r11 - r9
            r9 = 500(0x1f4, double:2.47E-321)
            int r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0492
            java.lang.Runnable r2 = r0.updateDelegate
            r2.run()
        L_0x0492:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x04b4
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r2.draw(r8, r10, r11, r0)
            r32.invalidate()
        L_0x04b4:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            goto L_0x04c2
        L_0x04bf:
            r9 = 5
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x04c2:
            r4.setAlpha(r5)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x04e0
            r33.save()
            float r3 = r4.getCenterX()
            float r5 = r4.getCenterY()
            r8.scale(r2, r2, r3, r5)
            r4.draw(r8)
            r33.restore()
            goto L_0x04e3
        L_0x04e0:
            r4.draw(r8)
        L_0x04e3:
            if (r7 == 0) goto L_0x04e8
            r33.restore()
        L_0x04e8:
            int r1 = r1 + 1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            goto L_0x0106
        L_0x04f1:
            r9 = 5
            int r6 = r6 + -1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            goto L_0x0103
        L_0x04fb:
            if (r21 == 0) goto L_0x0500
            r33.restore()
        L_0x0500:
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
