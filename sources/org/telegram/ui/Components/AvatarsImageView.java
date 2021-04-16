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

    public void setObject(int i, int i2, TLObject tLObject) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$Chat tLRPC$Chat2;
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
            } else {
                long unused5 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
            }
            int unused6 = this.animatingStates[i].id = peerId;
            tLRPC$Chat = tLRPC$Chat2;
        } else if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User3 = (TLRPC$User) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$User3);
            int unused7 = this.animatingStates[i].id = tLRPC$User3.id;
            tLRPC$User = tLRPC$User3;
            tLRPC$Chat = null;
        } else {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat);
            int unused8 = this.animatingStates[i].id = -tLRPC$Chat.id;
        }
        if (tLRPC$User != null) {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$User, this.animatingStates[i].avatarDrawable);
        } else {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$Chat, this.animatingStates[i].avatarDrawable);
        }
        this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(this.currentStyle == 4 ? 16.0f : 12.0f));
        float dp = (float) AndroidUtilities.dp(this.currentStyle == 4 ? 32.0f : 24.0f);
        this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, dp, dp);
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0231  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0475  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x047a  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0499  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x047d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:205:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e3  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r29) {
        /*
            r28 = this;
            r0 = r28
            r8 = r29
            r9 = 1
            r0.wasDraw = r9
            int r1 = r0.currentStyle
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            r10 = 4
            if (r1 != r10) goto L_0x0011
            r1 = 1107296256(0x42000000, float:32.0)
            goto L_0x0013
        L_0x0011:
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x0013:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentStyle
            if (r1 != r10) goto L_0x001c
            goto L_0x001e
        L_0x001c:
            r2 = 1101004800(0x41a00000, float:20.0)
        L_0x001e:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1 = 0
            r2 = 0
        L_0x0024:
            r14 = 3
            if (r1 >= r14) goto L_0x0036
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
            r3 = r3[r1]
            int r3 = r3.id
            if (r3 == 0) goto L_0x0033
            int r2 = r2 + 1
        L_0x0033:
            int r1 = r1 + 1
            goto L_0x0024
        L_0x0036:
            boolean r1 = r0.centered
            r15 = 1092616192(0x41200000, float:10.0)
            r16 = 1082130432(0x40800000, float:4.0)
            r17 = 1090519040(0x41000000, float:8.0)
            r7 = 2
            if (r1 == 0) goto L_0x0058
            int r1 = r28.getMeasuredWidth()
            int r2 = r2 * r12
            int r1 = r1 - r2
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x004f
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x0051
        L_0x004f:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x0051:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r7
            goto L_0x0063
        L_0x0058:
            int r1 = r0.currentStyle
            if (r1 != 0) goto L_0x005f
            r18 = 0
            goto L_0x0065
        L_0x005f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
        L_0x0063:
            r18 = r1
        L_0x0065:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0077
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x0077
            r1 = 1
            goto L_0x0078
        L_0x0077:
            r1 = 0
        L_0x0078:
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x0088
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x009a
        L_0x0088:
            if (r2 == r14) goto L_0x009a
            android.graphics.Paint r2 = r0.paint
            if (r1 == 0) goto L_0x0091
            java.lang.String r1 = "returnToCallMutedBackground"
            goto L_0x0093
        L_0x0091:
            java.lang.String r1 = "returnToCallBackground"
        L_0x0093:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setColor(r1)
        L_0x009a:
            r1 = 0
            r19 = 0
        L_0x009d:
            if (r1 >= r14) goto L_0x00ae
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r1]
            int r2 = r2.id
            if (r2 == 0) goto L_0x00ab
            int r19 = r19 + 1
        L_0x00ab:
            int r1 = r1 + 1
            goto L_0x009d
        L_0x00ae:
            int r1 = r0.currentStyle
            r6 = 5
            if (r1 == 0) goto L_0x00bf
            if (r1 == r9) goto L_0x00bf
            if (r1 == r14) goto L_0x00bf
            if (r1 == r10) goto L_0x00bf
            if (r1 != r6) goto L_0x00bc
            goto L_0x00bf
        L_0x00bc:
            r20 = 0
            goto L_0x00c1
        L_0x00bf:
            r20 = 1
        L_0x00c1:
            if (r20 == 0) goto L_0x00df
            r2 = 0
            r3 = 0
            int r1 = r28.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r28.getMeasuredHeight()
            float r5 = (float) r1
            r21 = 255(0xff, float:3.57E-43)
            r22 = 31
            r1 = r29
            r13 = 5
            r6 = r21
            r13 = 2
            r7 = r22
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x00e0
        L_0x00df:
            r13 = 2
        L_0x00e0:
            r7 = 2
        L_0x00e1:
            if (r7 < 0) goto L_0x0497
            r1 = 0
        L_0x00e4:
            if (r1 >= r13) goto L_0x0489
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x00f6
            float r3 = r0.transitionProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 != 0) goto L_0x00f6
        L_0x00f0:
            r23 = r11
            r10 = 5
            r13 = 1
            goto L_0x047d
        L_0x00f6:
            if (r1 != 0) goto L_0x00fb
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.animatingStates
            goto L_0x00fd
        L_0x00fb:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r3 = r0.currentStates
        L_0x00fd:
            if (r1 != r9) goto L_0x010e
            float r4 = r0.transitionProgress
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x010e
            r4 = r3[r7]
            int r4 = r4.animationType
            if (r4 == r9) goto L_0x010e
            goto L_0x00f0
        L_0x010e:
            r4 = r3[r7]
            org.telegram.messenger.ImageReceiver r4 = r4.imageReceiver
            boolean r5 = r4.hasImageSet()
            if (r5 != 0) goto L_0x011b
            goto L_0x00f0
        L_0x011b:
            if (r1 != 0) goto L_0x0143
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0138
            int r5 = r28.getMeasuredWidth()
            int r6 = r19 * r12
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x012f
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x0131
        L_0x012f:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x0131:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r13
            goto L_0x013c
        L_0x0138:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
        L_0x013c:
            int r6 = r12 * r7
            int r5 = r5 + r6
            r4.setImageX(r5)
            goto L_0x014a
        L_0x0143:
            int r5 = r12 * r7
            int r5 = r18 + r5
            r4.setImageX(r5)
        L_0x014a:
            int r5 = r0.currentStyle
            if (r5 != 0) goto L_0x015b
            int r5 = r28.getMeasuredHeight()
            int r5 = r5 - r11
            float r5 = (float) r5
            r6 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r6
            r4.setImageY(r5)
            goto L_0x016a
        L_0x015b:
            if (r5 != r10) goto L_0x0160
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x0162
        L_0x0160:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0162:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setImageY(r5)
        L_0x016a:
            float r5 = r0.transitionProgress
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0229
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != r9) goto L_0x0193
            r29.save()
            float r5 = r0.transitionProgress
            float r6 = r2 - r5
            float r5 = r2 - r5
            float r14 = r4.getCenterX()
            float r9 = r4.getCenterY()
            r8.scale(r6, r5, r14, r9)
            float r5 = r0.transitionProgress
            float r5 = r2 - r5
        L_0x0190:
            r6 = 1
            goto L_0x022c
        L_0x0193:
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != 0) goto L_0x01ae
            r29.save()
            float r5 = r0.transitionProgress
            float r6 = r4.getCenterX()
            float r9 = r4.getCenterY()
            r8.scale(r5, r5, r6, r9)
            float r5 = r0.transitionProgress
            goto L_0x0190
        L_0x01ae:
            r5 = r3[r7]
            int r5 = r5.animationType
            if (r5 != r13) goto L_0x01f2
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x01d1
            int r5 = r28.getMeasuredWidth()
            int r6 = r19 * r12
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x01c8
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x01ca
        L_0x01c8:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x01ca:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r13
            goto L_0x01d5
        L_0x01d1:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
        L_0x01d5:
            int r6 = r12 * r7
            int r5 = r5 + r6
            r6 = r3[r7]
            int r6 = r6.moveFromIndex
            int r6 = r6 * r12
            int r6 = r18 + r6
            float r5 = (float) r5
            float r9 = r0.transitionProgress
            float r5 = r5 * r9
            float r6 = (float) r6
            float r9 = r2 - r9
            float r6 = r6 * r9
            float r5 = r5 + r6
            int r5 = (int) r5
            r4.setImageX(r5)
            goto L_0x0229
        L_0x01f2:
            r5 = r3[r7]
            int r5 = r5.animationType
            r6 = -1
            if (r5 != r6) goto L_0x0229
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0229
            int r5 = r28.getMeasuredWidth()
            int r6 = r19 * r12
            int r5 = r5 - r6
            int r6 = r0.currentStyle
            if (r6 != r10) goto L_0x020d
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x020f
        L_0x020d:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x020f:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 / r13
            int r6 = r12 * r7
            int r5 = r5 + r6
            int r6 = r18 + r6
            float r5 = (float) r5
            float r9 = r0.transitionProgress
            float r5 = r5 * r9
            float r6 = (float) r6
            float r9 = r2 - r9
            float r6 = r6 * r9
            float r5 = r5 + r6
            int r5 = (int) r5
            r4.setImageX(r5)
        L_0x0229:
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
        L_0x022c:
            int r9 = r3.length
            r14 = 1
            int r9 = r9 - r14
            if (r7 == r9) goto L_0x0453
            int r9 = r0.currentStyle
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1117323264(0x42990000, float:76.5)
            r26 = 1095761920(0x41500000, float:13.0)
            r27 = 1099431936(0x41880000, float:17.0)
            if (r9 == r14) goto L_0x0367
            r14 = 3
            if (r9 == r14) goto L_0x0367
            r13 = 5
            if (r9 != r13) goto L_0x0245
            goto L_0x0367
        L_0x0245:
            if (r9 != r10) goto L_0x0315
            float r9 = r4.getCenterX()
            float r13 = r4.getCenterY()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r27)
            float r14 = (float) r14
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r9, r13, r14, r15)
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            if (r9 != 0) goto L_0x0273
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r13 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r27)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r13.<init>(r14, r15)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r9.wavesDrawable = r13
        L_0x0273:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            java.lang.String r13 = "voipgroup_listeningText"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            float r14 = r5 * r25
            int r14 = (int) r14
            int r13 = androidx.core.graphics.ColorUtils.setAlphaComponent(r13, r14)
            r9.setColor(r13)
            long r13 = java.lang.System.currentTimeMillis()
            r9 = r3[r7]
            long r24 = r9.lastUpdateTime
            long r24 = r13 - r24
            r26 = 100
            int r9 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1))
            if (r9 <= 0) goto L_0x02eb
            r9 = r3[r7]
            long unused = r9.lastUpdateTime = r13
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9)
            int r9 = r9.getCurrentTime()
            long r13 = (long) r9
            r9 = r3[r7]
            long r24 = r9.lastSpeakTime
            long r13 = r13 - r24
            r24 = 5
            int r9 = (r13 > r24 ? 1 : (r13 == r24 ? 0 : -1))
            if (r9 > 0) goto L_0x02d6
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r13 = 1
            r9.setShowWaves(r13, r0)
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            java.util.Random r13 = r0.random
            int r13 = r13.nextInt()
            int r13 = r13 % 100
            double r13 = (double) r13
            r9.setAmplitude(r13)
            goto L_0x02eb
        L_0x02d6:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r13 = 0
            r9.setShowWaves(r13, r0)
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r13 = 0
            r9.setAmplitude(r13)
        L_0x02eb:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r9.update()
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            float r13 = r4.getCenterX()
            float r14 = r4.getCenterY()
            r9.draw(r8, r13, r14, r0)
            r3 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r3 = r3.getAvatarScale()
            r23 = r11
            r10 = 5
            r13 = 1
            goto L_0x0459
        L_0x0315:
            if (r20 == 0) goto L_0x0331
            float r3 = r4.getCenterX()
            float r9 = r4.getCenterY()
            int r13 = r0.currentStyle
            if (r13 != r10) goto L_0x0325
            r26 = 1099431936(0x41880000, float:17.0)
        L_0x0325:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r26)
            float r13 = (float) r13
            android.graphics.Paint r14 = r0.xRefP
            r8.drawCircle(r3, r9, r13, r14)
            goto L_0x0453
        L_0x0331:
            android.graphics.Paint r3 = r0.paint
            int r3 = r3.getAlpha()
            int r9 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x0344
            android.graphics.Paint r9 = r0.paint
            float r13 = (float) r3
            float r13 = r13 * r5
            int r13 = (int) r13
            r9.setAlpha(r13)
        L_0x0344:
            float r9 = r4.getCenterX()
            float r13 = r4.getCenterY()
            int r14 = r0.currentStyle
            if (r14 != r10) goto L_0x0352
            r26 = 1099431936(0x41880000, float:17.0)
        L_0x0352:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r26)
            float r14 = (float) r14
            android.graphics.Paint r15 = r0.paint
            r8.drawCircle(r9, r13, r14, r15)
            int r9 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x0453
            android.graphics.Paint r9 = r0.paint
            r9.setAlpha(r3)
            goto L_0x0453
        L_0x0367:
            float r9 = r4.getCenterX()
            float r13 = r4.getCenterY()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r26)
            float r14 = (float) r14
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r9, r13, r14, r15)
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            if (r9 != 0) goto L_0x03af
            int r9 = r0.currentStyle
            r13 = 5
            if (r9 != r13) goto L_0x039d
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r13 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r14 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r15 = 1098907648(0x41800000, float:16.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r13.<init>(r14, r15)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r9.wavesDrawable = r13
            goto L_0x03af
        L_0x039d:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r13 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r27)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r13.<init>(r14, r15)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r9.wavesDrawable = r13
        L_0x03af:
            int r9 = r0.currentStyle
            r13 = 5
            if (r9 != r13) goto L_0x03ca
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            java.lang.String r13 = "voipgroup_speakingText"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            float r14 = r5 * r25
            int r14 = (int) r14
            int r13 = androidx.core.graphics.ColorUtils.setAlphaComponent(r13, r14)
            r9.setColor(r13)
        L_0x03ca:
            r9 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r9.participant
            if (r9 == 0) goto L_0x03fd
            r9 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r9.participant
            float r9 = r9.amplitude
            r13 = 0
            int r9 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r9 <= 0) goto L_0x03fd
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r13 = 1
            r9.setShowWaves(r13, r0)
            r9 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r9.participant
            float r9 = r9.amplitude
            r14 = 1097859072(0x41700000, float:15.0)
            float r9 = r9 * r14
            r14 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r14 = r14.wavesDrawable
            r23 = r11
            double r10 = (double) r9
            r14.setAmplitude(r10)
            r10 = 0
            goto L_0x040a
        L_0x03fd:
            r23 = r11
            r13 = 1
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r10 = 0
            r9.setShowWaves(r10, r0)
        L_0x040a:
            int r9 = r0.currentStyle
            r11 = 5
            if (r9 != r11) goto L_0x0426
            long r24 = android.os.SystemClock.uptimeMillis()
            r9 = r3[r7]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r9.participant
            long r10 = r9.lastSpeakTime
            long r24 = r24 - r10
            r9 = 500(0x1f4, double:2.47E-321)
            int r11 = (r24 > r9 ? 1 : (r24 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x0426
            java.lang.Runnable r9 = r0.updateDelegate
            r9.run()
        L_0x0426:
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            r9.update()
            int r9 = r0.currentStyle
            r10 = 5
            if (r9 != r10) goto L_0x0448
            r9 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            float r11 = r4.getCenterX()
            float r14 = r4.getCenterY()
            r9.draw(r8, r11, r14, r0)
            r28.invalidate()
        L_0x0448:
            r3 = r3[r7]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r3 = r3.getAvatarScale()
            goto L_0x0459
        L_0x0453:
            r23 = r11
            r10 = 5
            r13 = 1
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0459:
            r4.setAlpha(r5)
            int r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x0475
            r29.save()
            float r2 = r4.getCenterX()
            float r5 = r4.getCenterY()
            r8.scale(r3, r3, r2, r5)
            r4.draw(r8)
            r29.restore()
            goto L_0x0478
        L_0x0475:
            r4.draw(r8)
        L_0x0478:
            if (r6 == 0) goto L_0x047d
            r29.restore()
        L_0x047d:
            int r1 = r1 + 1
            r11 = r23
            r9 = 1
            r10 = 4
            r13 = 2
            r14 = 3
            r15 = 1092616192(0x41200000, float:10.0)
            goto L_0x00e4
        L_0x0489:
            r23 = r11
            r10 = 5
            r13 = 1
            int r7 = r7 + -1
            r9 = 1
            r10 = 4
            r13 = 2
            r14 = 3
            r15 = 1092616192(0x41200000, float:10.0)
            goto L_0x00e1
        L_0x0497:
            if (r20 == 0) goto L_0x049c
            r29.restore()
        L_0x049c:
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
