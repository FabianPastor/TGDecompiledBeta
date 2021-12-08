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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;

public class AvatarsImageView extends FrameLayout {
    public static final int STYLE_GROUP_CALL_TOOLTIP = 10;
    public static final int STYLE_MESSAGE_SEEN = 11;
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

    public void commitTransition(boolean animated) {
        if (!this.wasDraw || !animated) {
            this.transitionProgress = 1.0f;
            swapStates();
            return;
        }
        DrawingState[] removedStates = new DrawingState[3];
        boolean changed = false;
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr = this.currentStates;
            removedStates[i] = drawingStateArr[i];
            if (drawingStateArr[i].id != this.animatingStates[i].id) {
                changed = true;
            } else {
                long unused = this.currentStates[i].lastSpeakTime = this.animatingStates[i].lastSpeakTime;
            }
        }
        if (!changed) {
            this.transitionProgress = 1.0f;
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            boolean found = false;
            int j = 0;
            while (true) {
                if (j >= 3) {
                    break;
                } else if (this.currentStates[j].id == this.animatingStates[i2].id) {
                    found = true;
                    removedStates[j] = null;
                    if (i2 == j) {
                        int unused2 = this.animatingStates[i2].animationType = -1;
                        GroupCallUserCell.AvatarWavesDrawable wavesDrawable = this.animatingStates[i2].wavesDrawable;
                        GroupCallUserCell.AvatarWavesDrawable unused3 = this.animatingStates[i2].wavesDrawable = this.currentStates[i2].wavesDrawable;
                        GroupCallUserCell.AvatarWavesDrawable unused4 = this.currentStates[i2].wavesDrawable = wavesDrawable;
                    } else {
                        int unused5 = this.animatingStates[i2].animationType = 2;
                        int unused6 = this.animatingStates[i2].moveFromIndex = j;
                    }
                } else {
                    j++;
                }
            }
            if (!found) {
                int unused7 = this.animatingStates[i2].animationType = 0;
            }
        }
        for (int i3 = 0; i3 < 3; i3++) {
            if (removedStates[i3] != null) {
                int unused8 = removedStates[i3].animationType = 1;
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
            public void onAnimationEnd(Animator animation) {
                if (AvatarsImageView.this.transitionProgressAnimator != null) {
                    AvatarsImageView.this.transitionProgress = 1.0f;
                    AvatarsImageView.this.swapStates();
                    if (AvatarsImageView.this.updateAfterTransition) {
                        AvatarsImageView.this.updateAfterTransition = false;
                        if (AvatarsImageView.this.updateDelegate != null) {
                            AvatarsImageView.this.updateDelegate.run();
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

    /* renamed from: lambda$commitTransition$0$org-telegram-ui-Components-AvatarsImageView  reason: not valid java name */
    public /* synthetic */ void m2010x4d01b158(ValueAnimator valueAnimator) {
        this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: private */
    public void swapStates() {
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr = this.currentStates;
            DrawingState state = drawingStateArr[i];
            DrawingState[] drawingStateArr2 = this.animatingStates;
            drawingStateArr[i] = drawingStateArr2[i];
            drawingStateArr2[i] = state;
        }
    }

    public void updateAfterTransitionEnd() {
        this.updateAfterTransition = true;
    }

    public void setDelegate(Runnable delegate) {
        this.updateDelegate = delegate;
    }

    public void setStyle(int currentStyle2) {
        this.currentStyle = currentStyle2;
        invalidate();
    }

    private static class DrawingState {
        public static final int ANIMATION_TYPE_IN = 0;
        public static final int ANIMATION_TYPE_MOVE = 2;
        public static final int ANIMATION_TYPE_NONE = -1;
        public static final int ANIMATION_TYPE_OUT = 1;
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
        TLRPC.TL_groupCallParticipant participant;
        /* access modifiers changed from: private */
        public GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

        private DrawingState() {
        }
    }

    public AvatarsImageView(Context context, boolean inCall) {
        super(context);
        for (int a = 0; a < 3; a++) {
            this.currentStates[a] = new DrawingState();
            ImageReceiver unused = this.currentStates[a].imageReceiver = new ImageReceiver(this);
            this.currentStates[a].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[a].avatarDrawable = new AvatarDrawable();
            this.currentStates[a].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            this.animatingStates[a] = new DrawingState();
            ImageReceiver unused3 = this.animatingStates[a].imageReceiver = new ImageReceiver(this);
            this.animatingStates[a].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[a].avatarDrawable = new AvatarDrawable();
            this.animatingStates[a].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        }
        this.isInCall = inCall;
        setWillNotDraw(false);
        this.xRefP.setColor(0);
        this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setObject(int index, int account, TLObject object) {
        long unused = this.animatingStates[index].id = 0;
        this.animatingStates[index].participant = null;
        if (object == null) {
            this.animatingStates[index].imageReceiver.setImageBitmap((Drawable) null);
            invalidate();
            return;
        }
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        long unused2 = this.animatingStates[index].lastSpeakTime = -1;
        if (object instanceof TLRPC.TL_groupCallParticipant) {
            TLRPC.TL_groupCallParticipant participant = (TLRPC.TL_groupCallParticipant) object;
            this.animatingStates[index].participant = participant;
            long id = MessageObject.getPeerId(participant.peer);
            if (DialogObject.isUserDialog(id)) {
                currentUser = MessagesController.getInstance(account).getUser(Long.valueOf(id));
                this.animatingStates[index].avatarDrawable.setInfo(currentUser);
            } else {
                currentChat = MessagesController.getInstance(account).getChat(Long.valueOf(-id));
                this.animatingStates[index].avatarDrawable.setInfo(currentChat);
            }
            if (this.currentStyle != 4) {
                long unused3 = this.animatingStates[index].lastSpeakTime = (long) participant.active_date;
            } else if (id == AccountInstance.getInstance(account).getUserConfig().getClientUserId()) {
                long unused4 = this.animatingStates[index].lastSpeakTime = 0;
            } else if (this.isInCall) {
                long unused5 = this.animatingStates[index].lastSpeakTime = participant.lastActiveDate;
            } else {
                long unused6 = this.animatingStates[index].lastSpeakTime = (long) participant.active_date;
            }
            long unused7 = this.animatingStates[index].id = id;
        } else if (object instanceof TLRPC.User) {
            currentUser = (TLRPC.User) object;
            this.animatingStates[index].avatarDrawable.setInfo(currentUser);
            long unused8 = this.animatingStates[index].id = currentUser.id;
        } else {
            currentChat = (TLRPC.Chat) object;
            this.animatingStates[index].avatarDrawable.setInfo(currentChat);
            long unused9 = this.animatingStates[index].id = -currentChat.id;
        }
        if (currentUser != null) {
            this.animatingStates[index].imageReceiver.setForUserOrChat(currentUser, this.animatingStates[index].avatarDrawable);
        } else {
            this.animatingStates[index].imageReceiver.setForUserOrChat(currentChat, this.animatingStates[index].avatarDrawable);
        }
        int i = this.currentStyle;
        boolean bigAvatars = i == 4 || i == 10;
        this.animatingStates[index].imageReceiver.setRoundRadius(AndroidUtilities.dp(bigAvatars ? 16.0f : 12.0f));
        int size = AndroidUtilities.dp(bigAvatars ? 32.0f : 24.0f);
        this.animatingStates[index].imageReceiver.setImageCoords(0.0f, 0.0f, (float) size, (float) size);
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0519  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0525  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x053a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0542 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r37) {
        /*
            r36 = this;
            r0 = r36
            r8 = r37
            r9 = 1
            r0.wasDraw = r9
            int r1 = r0.currentStyle
            r10 = 4
            r12 = 10
            if (r1 == r10) goto L_0x0013
            if (r1 != r12) goto L_0x0011
            goto L_0x0013
        L_0x0011:
            r1 = 0
            goto L_0x0014
        L_0x0013:
            r1 = 1
        L_0x0014:
            r13 = r1
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r13 == 0) goto L_0x001c
            r2 = 1107296256(0x42000000, float:32.0)
            goto L_0x001e
        L_0x001c:
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x001e:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0.currentStyle
            r15 = 11
            if (r2 != r15) goto L_0x0031
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r16 = r1
            goto L_0x003c
        L_0x0031:
            if (r13 == 0) goto L_0x0034
            goto L_0x0036
        L_0x0034:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x0036:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r16 = r1
        L_0x003c:
            r1 = 0
            r2 = 0
            r17 = r1
        L_0x0040:
            r3 = 0
            r7 = 3
            if (r2 >= r7) goto L_0x0056
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.currentStates
            r1 = r1[r2]
            long r5 = r1.id
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0053
            int r17 = r17 + 1
        L_0x0053:
            int r2 = r2 + 1
            goto L_0x0040
        L_0x0056:
            int r1 = r0.currentStyle
            if (r1 == 0) goto L_0x0066
            if (r1 == r12) goto L_0x0066
            if (r1 != r15) goto L_0x005f
            goto L_0x0066
        L_0x005f:
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0067
        L_0x0066:
            r1 = 0
        L_0x0067:
            r18 = r1
            boolean r1 = r0.centered
            r19 = 1082130432(0x40800000, float:4.0)
            r20 = 1090519040(0x41000000, float:8.0)
            r6 = 2
            if (r1 == 0) goto L_0x0087
            int r1 = r36.getMeasuredWidth()
            int r2 = r17 * r16
            int r1 = r1 - r2
            if (r13 == 0) goto L_0x007e
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x0080
        L_0x007e:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x0080:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r6
            goto L_0x0089
        L_0x0087:
            r1 = r18
        L_0x0089:
            r21 = r1
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x009d
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x009d
            r1 = 1
            goto L_0x009e
        L_0x009d:
            r1 = 0
        L_0x009e:
            r22 = r1
            int r1 = r0.currentStyle
            if (r1 != r10) goto L_0x00b0
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x00c2
        L_0x00b0:
            if (r1 == r7) goto L_0x00c2
            android.graphics.Paint r1 = r0.paint
            if (r22 == 0) goto L_0x00b9
            java.lang.String r2 = "returnToCallMutedBackground"
            goto L_0x00bb
        L_0x00b9:
            java.lang.String r2 = "returnToCallBackground"
        L_0x00bb:
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
        L_0x00c2:
            r1 = 0
            r2 = 0
            r23 = r1
        L_0x00c6:
            if (r2 >= r7) goto L_0x00d9
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r2]
            long r24 = r1.id
            int r1 = (r24 > r3 ? 1 : (r24 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x00d6
            int r23 = r23 + 1
        L_0x00d6:
            int r2 = r2 + 1
            goto L_0x00c6
        L_0x00d9:
            int r1 = r0.currentStyle
            r5 = 5
            if (r1 == 0) goto L_0x00ed
            if (r1 == r9) goto L_0x00ed
            if (r1 == r7) goto L_0x00ed
            if (r1 == r10) goto L_0x00ed
            if (r1 == r5) goto L_0x00ed
            if (r1 == r12) goto L_0x00ed
            if (r1 != r15) goto L_0x00eb
            goto L_0x00ed
        L_0x00eb:
            r2 = 0
            goto L_0x00ee
        L_0x00ed:
            r2 = 1
        L_0x00ee:
            r24 = r2
            r25 = 1098907648(0x41800000, float:16.0)
            r26 = 0
            if (r24 == 0) goto L_0x0126
            if (r1 != r12) goto L_0x00fe
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r25)
            float r1 = (float) r1
            goto L_0x00ff
        L_0x00fe:
            r1 = 0
        L_0x00ff:
            r4 = r1
            float r2 = -r4
            float r3 = -r4
            int r1 = r36.getMeasuredWidth()
            float r1 = (float) r1
            float r27 = r1 + r4
            int r1 = r36.getMeasuredHeight()
            float r1 = (float) r1
            float r28 = r1 + r4
            r29 = 255(0xff, float:3.57E-43)
            r30 = 31
            r1 = r37
            r31 = r4
            r4 = r27
            r11 = 5
            r5 = r28
            r11 = 2
            r6 = r29
            r7 = r30
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0127
        L_0x0126:
            r11 = 2
        L_0x0127:
            r1 = 2
        L_0x0128:
            if (r1 < 0) goto L_0x055d
            r2 = 0
        L_0x012b:
            if (r2 >= r11) goto L_0x054f
            r3 = 1065353216(0x3var_, float:1.0)
            if (r2 != 0) goto L_0x013c
            float r4 = r0.transitionProgress
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 != 0) goto L_0x013c
            r30 = r13
            r10 = 5
            goto L_0x0542
        L_0x013c:
            if (r2 != 0) goto L_0x0141
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r4 = r0.animatingStates
            goto L_0x0143
        L_0x0141:
            org.telegram.ui.Components.AvatarsImageView$DrawingState[] r4 = r0.currentStates
        L_0x0143:
            if (r2 != r9) goto L_0x0158
            float r5 = r0.transitionProgress
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0158
            r5 = r4[r1]
            int r5 = r5.animationType
            if (r5 == r9) goto L_0x0158
            r30 = r13
            r10 = 5
            goto L_0x0542
        L_0x0158:
            r5 = r4[r1]
            org.telegram.messenger.ImageReceiver r5 = r5.imageReceiver
            boolean r6 = r5.hasImageSet()
            if (r6 != 0) goto L_0x0169
            r30 = r13
            r10 = 5
            goto L_0x0542
        L_0x0169:
            if (r2 != 0) goto L_0x018d
            boolean r6 = r0.centered
            if (r6 == 0) goto L_0x0184
            int r6 = r36.getMeasuredWidth()
            int r7 = r23 * r16
            int r6 = r6 - r7
            if (r13 == 0) goto L_0x017b
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x017d
        L_0x017b:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x017d:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 / r11
            goto L_0x0186
        L_0x0184:
            r6 = r18
        L_0x0186:
            int r7 = r16 * r1
            int r7 = r7 + r6
            r5.setImageX(r7)
            goto L_0x0194
        L_0x018d:
            int r6 = r16 * r1
            int r6 = r21 + r6
            r5.setImageX(r6)
        L_0x0194:
            int r6 = r0.currentStyle
            if (r6 == 0) goto L_0x01ad
            if (r6 == r12) goto L_0x01ad
            if (r6 != r15) goto L_0x019d
            goto L_0x01ad
        L_0x019d:
            if (r6 != r10) goto L_0x01a2
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x01a4
        L_0x01a2:
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x01a4:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r5.setImageY(r6)
            goto L_0x01b9
        L_0x01ad:
            int r6 = r36.getMeasuredHeight()
            int r6 = r6 - r14
            float r6 = (float) r6
            r7 = 1073741824(0x40000000, float:2.0)
            float r6 = r6 / r7
            r5.setImageY(r6)
        L_0x01b9:
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            float r15 = r0.transitionProgress
            int r15 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
            if (r15 == 0) goto L_0x0280
            r15 = r4[r1]
            int r15 = r15.animationType
            if (r15 != r9) goto L_0x01e5
            r37.save()
            float r15 = r0.transitionProgress
            float r12 = r3 - r15
            float r15 = r3 - r15
            float r10 = r5.getCenterX()
            float r9 = r5.getCenterY()
            r8.scale(r12, r15, r10, r9)
            r6 = 1
            float r9 = r0.transitionProgress
            float r7 = r3 - r9
            goto L_0x0284
        L_0x01e5:
            r9 = r4[r1]
            int r9 = r9.animationType
            if (r9 != 0) goto L_0x0202
            r37.save()
            float r9 = r0.transitionProgress
            float r10 = r5.getCenterX()
            float r12 = r5.getCenterY()
            r8.scale(r9, r9, r10, r12)
            float r7 = r0.transitionProgress
            r6 = 1
            goto L_0x0284
        L_0x0202:
            r9 = r4[r1]
            int r9 = r9.animationType
            if (r9 != r11) goto L_0x0244
            boolean r9 = r0.centered
            if (r9 == 0) goto L_0x0223
            int r9 = r36.getMeasuredWidth()
            int r10 = r23 * r16
            int r9 = r9 - r10
            if (r13 == 0) goto L_0x021a
            r10 = 1090519040(0x41000000, float:8.0)
            goto L_0x021c
        L_0x021a:
            r10 = 1082130432(0x40800000, float:4.0)
        L_0x021c:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r9 = r9 / r11
            goto L_0x0225
        L_0x0223:
            r9 = r18
        L_0x0225:
            int r10 = r16 * r1
            int r10 = r10 + r9
            r12 = r4[r1]
            int r12 = r12.moveFromIndex
            int r12 = r12 * r16
            int r12 = r21 + r12
            float r15 = (float) r10
            float r11 = r0.transitionProgress
            float r15 = r15 * r11
            r32 = r6
            float r6 = (float) r12
            float r11 = r3 - r11
            float r6 = r6 * r11
            float r15 = r15 + r6
            int r6 = (int) r15
            r5.setImageX(r6)
            goto L_0x027f
        L_0x0244:
            r32 = r6
            r6 = r4[r1]
            int r6 = r6.animationType
            r9 = -1
            if (r6 != r9) goto L_0x027f
            boolean r6 = r0.centered
            if (r6 == 0) goto L_0x027f
            int r6 = r36.getMeasuredWidth()
            int r9 = r23 * r16
            int r6 = r6 - r9
            if (r13 == 0) goto L_0x025f
            r9 = 1090519040(0x41000000, float:8.0)
            goto L_0x0261
        L_0x025f:
            r9 = 1082130432(0x40800000, float:4.0)
        L_0x0261:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 - r9
            r9 = 2
            int r6 = r6 / r9
            int r10 = r16 * r1
            int r10 = r10 + r6
            int r11 = r16 * r1
            int r11 = r21 + r11
            float r12 = (float) r10
            float r15 = r0.transitionProgress
            float r12 = r12 * r15
            float r9 = (float) r11
            float r15 = r3 - r15
            float r9 = r9 * r15
            float r12 = r12 + r9
            int r9 = (int) r12
            r5.setImageX(r9)
            goto L_0x0282
        L_0x027f:
            goto L_0x0282
        L_0x0280:
            r32 = r6
        L_0x0282:
            r6 = r32
        L_0x0284:
            r9 = 1065353216(0x3var_, float:1.0)
            int r10 = r4.length
            r11 = 1
            int r10 = r10 - r11
            if (r1 == r10) goto L_0x0519
            int r10 = r0.currentStyle
            r15 = 1101529088(0x41a80000, float:21.0)
            java.lang.String r32 = "voipgroup_speakingText"
            r33 = 1117323264(0x42990000, float:76.5)
            r34 = 1095761920(0x41500000, float:13.0)
            r35 = 1099431936(0x41880000, float:17.0)
            if (r10 == r11) goto L_0x0435
            r11 = 3
            if (r10 == r11) goto L_0x0435
            r11 = 5
            if (r10 != r11) goto L_0x02a3
            r30 = r13
            goto L_0x0437
        L_0x02a3:
            r11 = 4
            if (r10 == r11) goto L_0x0306
            r11 = 10
            if (r10 != r11) goto L_0x02ab
            goto L_0x0306
        L_0x02ab:
            if (r24 == 0) goto L_0x02c8
            float r10 = r5.getCenterX()
            float r11 = r5.getCenterY()
            if (r13 == 0) goto L_0x02b9
            r34 = 1099431936(0x41880000, float:17.0)
        L_0x02b9:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r34)
            float r12 = (float) r12
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r10, r11, r12, r15)
            r30 = r13
            r10 = 5
            goto L_0x051c
        L_0x02c8:
            android.graphics.Paint r10 = r0.paint
            int r10 = r10.getAlpha()
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x02db
            android.graphics.Paint r11 = r0.paint
            float r12 = (float) r10
            float r12 = r12 * r7
            int r12 = (int) r12
            r11.setAlpha(r12)
        L_0x02db:
            float r11 = r5.getCenterX()
            float r12 = r5.getCenterY()
            if (r13 == 0) goto L_0x02e7
            r34 = 1099431936(0x41880000, float:17.0)
        L_0x02e7:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r34)
            float r15 = (float) r15
            android.graphics.Paint r3 = r0.paint
            r8.drawCircle(r11, r12, r15, r3)
            r3 = 1065353216(0x3var_, float:1.0)
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x0301
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r10)
            r30 = r13
            r10 = 5
            goto L_0x051c
        L_0x0301:
            r30 = r13
            r10 = 5
            goto L_0x051c
        L_0x0306:
            float r3 = r5.getCenterX()
            float r10 = r5.getCenterY()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r35)
            float r11 = (float) r11
            android.graphics.Paint r12 = r0.xRefP
            r8.drawCircle(r3, r10, r11, r12)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            if (r3 != 0) goto L_0x0332
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r10
        L_0x0332:
            int r3 = r0.currentStyle
            r10 = 10
            if (r3 != r10) goto L_0x034d
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r32)
            float r11 = r7 * r33
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r3.setColor(r10)
            goto L_0x0363
        L_0x034d:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            java.lang.String r10 = "voipgroup_listeningText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r11 = r7 * r33
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r3.setColor(r10)
        L_0x0363:
            long r10 = java.lang.System.currentTimeMillis()
            r3 = r4[r1]
            long r32 = r3.lastUpdateTime
            long r32 = r10 - r32
            r34 = 100
            int r3 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r3 <= 0) goto L_0x040c
            r3 = r4[r1]
            long unused = r3.lastUpdateTime = r10
            int r3 = r0.currentStyle
            r12 = 10
            if (r3 != r12) goto L_0x03be
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            if (r3 == 0) goto L_0x03b1
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            int r3 = (r3 > r26 ? 1 : (r3 == r26 ? 0 : -1))
            if (r3 <= 0) goto L_0x03b1
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r15 = 1
            r3.setShowWaves(r15, r0)
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            r15 = 1097859072(0x41700000, float:15.0)
            float r3 = r3 * r15
            r15 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r15 = r15.wavesDrawable
            r30 = r13
            double r12 = (double) r3
            r15.setAmplitude(r12)
            goto L_0x040e
        L_0x03b1:
            r30 = r13
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r12 = 0
            r3.setShowWaves(r12, r0)
            goto L_0x040e
        L_0x03be:
            r30 = r13
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            int r3 = r3.getCurrentTime()
            long r12 = (long) r3
            r3 = r4[r1]
            long r32 = r3.lastSpeakTime
            long r12 = r12 - r32
            r32 = 5
            int r3 = (r12 > r32 ? 1 : (r12 == r32 ? 0 : -1))
            if (r3 > 0) goto L_0x03f6
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r12 = 1
            r3.setShowWaves(r12, r0)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            java.util.Random r12 = r0.random
            int r12 = r12.nextInt()
            int r12 = r12 % 100
            double r12 = (double) r12
            r3.setAmplitude(r12)
            goto L_0x040e
        L_0x03f6:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r12 = 0
            r3.setShowWaves(r12, r0)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r12 = 0
            r3.setAmplitude(r12)
            goto L_0x040e
        L_0x040c:
            r30 = r13
        L_0x040e:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r3.update()
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r12 = r5.getCenterX()
            float r13 = r5.getCenterY()
            r3.draw(r8, r12, r13, r0)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r9 = r3.getAvatarScale()
            r10 = 5
            goto L_0x051c
        L_0x0435:
            r30 = r13
        L_0x0437:
            float r3 = r5.getCenterX()
            float r10 = r5.getCenterY()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r34)
            float r11 = (float) r11
            android.graphics.Paint r12 = r0.xRefP
            r8.drawCircle(r3, r10, r11, r12)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            if (r3 != 0) goto L_0x047d
            int r3 = r0.currentStyle
            r10 = 5
            if (r3 != r10) goto L_0x046b
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r11 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r10
            goto L_0x047d
        L_0x046b:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r10
        L_0x047d:
            int r3 = r0.currentStyle
            r10 = 5
            if (r3 != r10) goto L_0x0496
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r32)
            float r11 = r7 * r33
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r3.setColor(r10)
        L_0x0496:
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            if (r3 == 0) goto L_0x04c6
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            int r3 = (r3 > r26 ? 1 : (r3 == r26 ? 0 : -1))
            if (r3 <= 0) goto L_0x04c6
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r10 = 1
            r3.setShowWaves(r10, r0)
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            r11 = 1097859072(0x41700000, float:15.0)
            float r3 = r3 * r11
            r11 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r11.wavesDrawable
            double r12 = (double) r3
            r11.setAmplitude(r12)
            r11 = 0
            goto L_0x04d1
        L_0x04c6:
            r10 = 1
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r11 = 0
            r3.setShowWaves(r11, r0)
        L_0x04d1:
            int r3 = r0.currentStyle
            r12 = 5
            if (r3 != r12) goto L_0x04ec
            long r12 = android.os.SystemClock.uptimeMillis()
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            long r10 = r3.lastSpeakTime
            long r12 = r12 - r10
            r10 = 500(0x1f4, double:2.47E-321)
            int r3 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x04ec
            java.lang.Runnable r3 = r0.updateDelegate
            r3.run()
        L_0x04ec:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r3.update()
            int r3 = r0.currentStyle
            r10 = 5
            if (r3 != r10) goto L_0x050e
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r11 = r5.getCenterX()
            float r12 = r5.getCenterY()
            r3.draw(r8, r11, r12, r0)
            r36.invalidate()
        L_0x050e:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r9 = r3.getAvatarScale()
            goto L_0x051c
        L_0x0519:
            r30 = r13
            r10 = 5
        L_0x051c:
            r5.setAlpha(r7)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x053a
            r37.save()
            float r3 = r5.getCenterX()
            float r11 = r5.getCenterY()
            r8.scale(r9, r9, r3, r11)
            r5.draw(r8)
            r37.restore()
            goto L_0x053d
        L_0x053a:
            r5.draw(r8)
        L_0x053d:
            if (r6 == 0) goto L_0x0542
            r37.restore()
        L_0x0542:
            int r2 = r2 + 1
            r13 = r30
            r9 = 1
            r10 = 4
            r11 = 2
            r12 = 10
            r15 = 11
            goto L_0x012b
        L_0x054f:
            r30 = r13
            r10 = 5
            int r1 = r1 + -1
            r9 = 1
            r10 = 4
            r11 = 2
            r12 = 10
            r15 = 11
            goto L_0x0128
        L_0x055d:
            r30 = r13
            if (r24 == 0) goto L_0x0564
            r37.restore()
        L_0x0564:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsImageView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasDraw = false;
        for (int a = 0; a < 3; a++) {
            this.currentStates[a].imageReceiver.onDetachedFromWindow();
            this.animatingStates[a].imageReceiver.onDetachedFromWindow();
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int a = 0; a < 3; a++) {
            this.currentStates[a].imageReceiver.onAttachedToWindow();
            this.animatingStates[a].imageReceiver.onAttachedToWindow();
        }
    }

    public void setCentered(boolean centered2) {
        this.centered = centered2;
    }

    public void setCount(int count2) {
        this.count = count2;
        requestLayout();
    }

    public void reset() {
        for (int i = 0; i < this.animatingStates.length; i++) {
            setObject(0, 0, (TLObject) null);
        }
    }
}
