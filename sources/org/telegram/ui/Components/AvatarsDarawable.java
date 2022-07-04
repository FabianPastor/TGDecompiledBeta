package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
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

public class AvatarsDarawable {
    public static final int STYLE_GROUP_CALL_TOOLTIP = 10;
    public static final int STYLE_MESSAGE_SEEN = 11;
    DrawingState[] animatingStates = new DrawingState[3];
    boolean centered;
    public int count;
    DrawingState[] currentStates = new DrawingState[3];
    int currentStyle;
    public int height;
    private boolean isInCall;
    private float overrideAlpha = 1.0f;
    private int overrideSize;
    private Paint paint = new Paint(1);
    View parent;
    Random random = new Random();
    public long transitionDuration = 220;
    private boolean transitionInProgress;
    public Interpolator transitionInterpolator = CubicBezierInterpolator.DEFAULT;
    float transitionProgress = 1.0f;
    ValueAnimator transitionProgressAnimator;
    boolean updateAfterTransition;
    Runnable updateDelegate;
    boolean wasDraw;
    public int width;
    private Paint xRefP = new Paint(1);

    public void commitTransition(boolean animated) {
        commitTransition(animated, true);
    }

    public void setTransitionProgress(float transitionProgress2) {
        if (this.transitionInProgress && this.transitionProgress != transitionProgress2) {
            this.transitionProgress = transitionProgress2;
            if (transitionProgress2 == 1.0f) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
    }

    public void commitTransition(boolean animated, boolean createAnimator) {
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
            if (this.transitionInProgress) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
        this.transitionProgress = 0.0f;
        if (createAnimator) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.transitionProgressAnimator = ofFloat;
            ofFloat.addUpdateListener(new AvatarsDarawable$$ExternalSyntheticLambda0(this));
            this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (AvatarsDarawable.this.transitionProgressAnimator != null) {
                        AvatarsDarawable.this.transitionProgress = 1.0f;
                        AvatarsDarawable.this.swapStates();
                        if (AvatarsDarawable.this.updateAfterTransition) {
                            AvatarsDarawable.this.updateAfterTransition = false;
                            if (AvatarsDarawable.this.updateDelegate != null) {
                                AvatarsDarawable.this.updateDelegate.run();
                            }
                        }
                        AvatarsDarawable.this.invalidate();
                    }
                    AvatarsDarawable.this.transitionProgressAnimator = null;
                }
            });
            this.transitionProgressAnimator.setDuration(this.transitionDuration);
            this.transitionProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.transitionProgressAnimator.start();
        } else {
            this.transitionInProgress = true;
        }
        invalidate();
    }

    /* renamed from: lambda$commitTransition$0$org-telegram-ui-Components-AvatarsDarawable  reason: not valid java name */
    public /* synthetic */ void m563x32cd51d(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: private */
    public void invalidate() {
        View view = this.parent;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setSize(int size) {
        this.overrideSize = size;
    }

    public void animateFromState(AvatarsDarawable avatarsDarawable, int currentAccount, boolean createAnimator) {
        ValueAnimator valueAnimator = avatarsDarawable.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            if (this.transitionInProgress) {
                this.transitionInProgress = false;
                swapStates();
            }
        }
        TLObject[] objects = new TLObject[3];
        for (int i = 0; i < 3; i++) {
            objects[i] = this.currentStates[i].object;
            setObject(i, currentAccount, avatarsDarawable.currentStates[i].object);
        }
        commitTransition(false);
        for (int i2 = 0; i2 < 3; i2++) {
            setObject(i2, currentAccount, objects[i2]);
        }
        this.wasDraw = true;
        commitTransition(true, createAnimator);
    }

    public void setAlpha(float alpha) {
        this.overrideAlpha = alpha;
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
        /* access modifiers changed from: private */
        public TLObject object;
        TLRPC.TL_groupCallParticipant participant;
        /* access modifiers changed from: private */
        public GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

        private DrawingState() {
        }
    }

    public AvatarsDarawable(View parent2, boolean inCall) {
        this.parent = parent2;
        for (int a = 0; a < 3; a++) {
            this.currentStates[a] = new DrawingState();
            ImageReceiver unused = this.currentStates[a].imageReceiver = new ImageReceiver(parent2);
            this.currentStates[a].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[a].avatarDrawable = new AvatarDrawable();
            this.currentStates[a].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            this.animatingStates[a] = new DrawingState();
            ImageReceiver unused3 = this.animatingStates[a].imageReceiver = new ImageReceiver(parent2);
            this.animatingStates[a].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[a].avatarDrawable = new AvatarDrawable();
            this.animatingStates[a].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        }
        this.isInCall = inCall;
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
        TLObject unused3 = this.animatingStates[index].object = object;
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
                long unused4 = this.animatingStates[index].lastSpeakTime = (long) participant.active_date;
            } else if (id == AccountInstance.getInstance(account).getUserConfig().getClientUserId()) {
                long unused5 = this.animatingStates[index].lastSpeakTime = 0;
            } else if (this.isInCall) {
                long unused6 = this.animatingStates[index].lastSpeakTime = participant.lastActiveDate;
            } else {
                long unused7 = this.animatingStates[index].lastSpeakTime = (long) participant.active_date;
            }
            long unused8 = this.animatingStates[index].id = id;
        } else if (object instanceof TLRPC.User) {
            currentUser = (TLRPC.User) object;
            this.animatingStates[index].avatarDrawable.setInfo(currentUser);
            long unused9 = this.animatingStates[index].id = currentUser.id;
        } else {
            currentChat = (TLRPC.Chat) object;
            this.animatingStates[index].avatarDrawable.setInfo(currentChat);
            long unused10 = this.animatingStates[index].id = -currentChat.id;
        }
        if (currentUser != null) {
            this.animatingStates[index].imageReceiver.setForUserOrChat(currentUser, this.animatingStates[index].avatarDrawable);
        } else {
            this.animatingStates[index].imageReceiver.setForUserOrChat(currentChat, this.animatingStates[index].avatarDrawable);
        }
        int i = this.currentStyle;
        this.animatingStates[index].imageReceiver.setRoundRadius(AndroidUtilities.dp(i == 4 || i == 10 ? 16.0f : 12.0f));
        int size = getSize();
        this.animatingStates[index].imageReceiver.setImageCoords(0.0f, 0.0f, (float) size, (float) size);
        invalidate();
    }

    /* JADX WARNING: Removed duplicated region for block: B:218:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0541  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0546  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0549 A[SYNTHETIC] */
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
            int r14 = r36.getSize()
            int r1 = r0.currentStyle
            r15 = 11
            if (r1 != r15) goto L_0x0028
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r16 = r1
            goto L_0x0043
        L_0x0028:
            int r1 = r0.overrideSize
            if (r1 == 0) goto L_0x0036
            float r1 = (float) r1
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            float r1 = r1 * r2
            int r1 = (int) r1
            r16 = r1
            goto L_0x0043
        L_0x0036:
            if (r13 == 0) goto L_0x003b
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            goto L_0x003d
        L_0x003b:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x003d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r16 = r1
        L_0x0043:
            r1 = 0
            r2 = 0
            r17 = r1
        L_0x0047:
            r3 = 0
            r7 = 3
            if (r2 >= r7) goto L_0x005d
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r1 = r0.currentStates
            r1 = r1[r2]
            long r5 = r1.id
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x005a
            int r17 = r17 + 1
        L_0x005a:
            int r2 = r2 + 1
            goto L_0x0047
        L_0x005d:
            int r1 = r0.currentStyle
            if (r1 == 0) goto L_0x006d
            if (r1 == r12) goto L_0x006d
            if (r1 != r15) goto L_0x0066
            goto L_0x006d
        L_0x0066:
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x006e
        L_0x006d:
            r1 = 0
        L_0x006e:
            r18 = r1
            boolean r1 = r0.centered
            r19 = 1082130432(0x40800000, float:4.0)
            r20 = 1090519040(0x41000000, float:8.0)
            r6 = 2
            if (r1 == 0) goto L_0x008c
            int r1 = r0.width
            int r2 = r17 * r16
            int r1 = r1 - r2
            if (r13 == 0) goto L_0x0083
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x0085
        L_0x0083:
            r2 = 1082130432(0x40800000, float:4.0)
        L_0x0085:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r1 = r1 / r6
            goto L_0x008e
        L_0x008c:
            r1 = r18
        L_0x008e:
            r21 = r1
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00a2
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r1 = r1.isMicMute()
            if (r1 == 0) goto L_0x00a2
            r1 = 1
            goto L_0x00a3
        L_0x00a2:
            r1 = 0
        L_0x00a3:
            r22 = r1
            int r1 = r0.currentStyle
            if (r1 != r10) goto L_0x00b5
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x00c7
        L_0x00b5:
            if (r1 == r7) goto L_0x00c7
            android.graphics.Paint r1 = r0.paint
            if (r22 == 0) goto L_0x00be
            java.lang.String r2 = "returnToCallMutedBackground"
            goto L_0x00c0
        L_0x00be:
            java.lang.String r2 = "returnToCallBackground"
        L_0x00c0:
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
        L_0x00c7:
            r1 = 0
            r2 = 0
            r23 = r1
        L_0x00cb:
            if (r2 >= r7) goto L_0x00de
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r1 = r0.animatingStates
            r1 = r1[r2]
            long r24 = r1.id
            int r1 = (r24 > r3 ? 1 : (r24 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x00db
            int r23 = r23 + 1
        L_0x00db:
            int r2 = r2 + 1
            goto L_0x00cb
        L_0x00de:
            int r1 = r0.currentStyle
            r5 = 5
            if (r1 == 0) goto L_0x00f2
            if (r1 == r9) goto L_0x00f2
            if (r1 == r7) goto L_0x00f2
            if (r1 == r10) goto L_0x00f2
            if (r1 == r5) goto L_0x00f2
            if (r1 == r12) goto L_0x00f2
            if (r1 != r15) goto L_0x00f0
            goto L_0x00f2
        L_0x00f0:
            r2 = 0
            goto L_0x00f3
        L_0x00f2:
            r2 = 1
        L_0x00f3:
            r24 = r2
            r25 = 1098907648(0x41800000, float:16.0)
            r26 = 0
            if (r24 == 0) goto L_0x0127
            if (r1 != r12) goto L_0x0103
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r25)
            float r1 = (float) r1
            goto L_0x0104
        L_0x0103:
            r1 = 0
        L_0x0104:
            r4 = r1
            float r2 = -r4
            float r3 = -r4
            int r1 = r0.width
            float r1 = (float) r1
            float r27 = r1 + r4
            int r1 = r0.height
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
            goto L_0x0128
        L_0x0127:
            r11 = 2
        L_0x0128:
            r1 = 2
        L_0x0129:
            if (r1 < 0) goto L_0x0560
            r2 = 0
        L_0x012c:
            if (r2 >= r11) goto L_0x0554
            r3 = 1065353216(0x3var_, float:1.0)
            if (r2 != 0) goto L_0x013b
            float r4 = r0.transitionProgress
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 != 0) goto L_0x013b
            r9 = 5
            goto L_0x0549
        L_0x013b:
            if (r2 != 0) goto L_0x0140
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r4 = r0.animatingStates
            goto L_0x0142
        L_0x0140:
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r4 = r0.currentStates
        L_0x0142:
            if (r2 != r9) goto L_0x0155
            float r5 = r0.transitionProgress
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0155
            r5 = r4[r1]
            int r5 = r5.animationType
            if (r5 == r9) goto L_0x0155
            r9 = 5
            goto L_0x0549
        L_0x0155:
            r5 = r4[r1]
            org.telegram.messenger.ImageReceiver r5 = r5.imageReceiver
            boolean r6 = r5.hasImageSet()
            if (r6 != 0) goto L_0x0164
            r9 = 5
            goto L_0x0549
        L_0x0164:
            if (r2 != 0) goto L_0x0186
            boolean r6 = r0.centered
            if (r6 == 0) goto L_0x017d
            int r6 = r0.width
            int r7 = r23 * r16
            int r6 = r6 - r7
            if (r13 == 0) goto L_0x0174
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0176
        L_0x0174:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0176:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 / r11
            goto L_0x017f
        L_0x017d:
            r6 = r18
        L_0x017f:
            int r7 = r16 * r1
            int r7 = r7 + r6
            r5.setImageX(r7)
            goto L_0x018d
        L_0x0186:
            int r6 = r16 * r1
            int r6 = r21 + r6
            r5.setImageX(r6)
        L_0x018d:
            int r6 = r0.currentStyle
            r7 = 1073741824(0x40000000, float:2.0)
            if (r6 == 0) goto L_0x01a8
            if (r6 == r12) goto L_0x01a8
            if (r6 != r15) goto L_0x0198
            goto L_0x01a8
        L_0x0198:
            if (r6 != r10) goto L_0x019d
            r6 = 1090519040(0x41000000, float:8.0)
            goto L_0x019f
        L_0x019d:
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x019f:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r5.setImageY(r6)
            goto L_0x01b0
        L_0x01a8:
            int r6 = r0.height
            int r6 = r6 - r14
            float r6 = (float) r6
            float r6 = r6 / r7
            r5.setImageY(r6)
        L_0x01b0:
            r6 = 0
            r29 = 1065353216(0x3var_, float:1.0)
            float r15 = r0.transitionProgress
            int r15 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
            if (r15 == 0) goto L_0x0270
            r15 = r4[r1]
            int r15 = r15.animationType
            if (r15 != r9) goto L_0x01dc
            r37.save()
            float r15 = r0.transitionProgress
            float r7 = r3 - r15
            float r15 = r3 - r15
            float r12 = r5.getCenterX()
            float r10 = r5.getCenterY()
            r8.scale(r7, r15, r12, r10)
            r6 = 1
            float r7 = r0.transitionProgress
            float r29 = r3 - r7
            goto L_0x0270
        L_0x01dc:
            r7 = r4[r1]
            int r7 = r7.animationType
            if (r7 != 0) goto L_0x01fb
            r37.save()
            float r7 = r0.transitionProgress
            float r10 = r5.getCenterX()
            float r12 = r5.getCenterY()
            r8.scale(r7, r7, r10, r12)
            float r7 = r0.transitionProgress
            r6 = 1
            r29 = r7
            goto L_0x0270
        L_0x01fb:
            r7 = r4[r1]
            int r7 = r7.animationType
            if (r7 != r11) goto L_0x0239
            boolean r7 = r0.centered
            if (r7 == 0) goto L_0x021a
            int r7 = r0.width
            int r10 = r23 * r16
            int r7 = r7 - r10
            if (r13 == 0) goto L_0x0211
            r10 = 1090519040(0x41000000, float:8.0)
            goto L_0x0213
        L_0x0211:
            r10 = 1082130432(0x40800000, float:4.0)
        L_0x0213:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 - r10
            int r7 = r7 / r11
            goto L_0x021c
        L_0x021a:
            r7 = r18
        L_0x021c:
            int r10 = r16 * r1
            int r10 = r10 + r7
            r12 = r4[r1]
            int r12 = r12.moveFromIndex
            int r12 = r12 * r16
            int r12 = r21 + r12
            float r15 = (float) r10
            float r9 = r0.transitionProgress
            float r15 = r15 * r9
            float r11 = (float) r12
            float r9 = r3 - r9
            float r11 = r11 * r9
            float r15 = r15 + r11
            int r9 = (int) r15
            r5.setImageX(r9)
            goto L_0x0270
        L_0x0239:
            r7 = r4[r1]
            int r7 = r7.animationType
            r9 = -1
            if (r7 != r9) goto L_0x0270
            boolean r7 = r0.centered
            if (r7 == 0) goto L_0x0270
            int r7 = r0.width
            int r9 = r23 * r16
            int r7 = r7 - r9
            if (r13 == 0) goto L_0x0250
            r9 = 1090519040(0x41000000, float:8.0)
            goto L_0x0252
        L_0x0250:
            r9 = 1082130432(0x40800000, float:4.0)
        L_0x0252:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r7 = r7 - r9
            r9 = 2
            int r7 = r7 / r9
            int r10 = r16 * r1
            int r10 = r10 + r7
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
        L_0x0270:
            float r7 = r0.overrideAlpha
            float r7 = r7 * r29
            r9 = 1065353216(0x3var_, float:1.0)
            int r10 = r4.length
            r11 = 1
            int r10 = r10 - r11
            if (r1 == r10) goto L_0x051e
            int r10 = r0.currentStyle
            r15 = 1101529088(0x41a80000, float:21.0)
            java.lang.String r29 = "voipgroup_speakingText"
            r32 = 1099431936(0x41880000, float:17.0)
            r33 = 1117323264(0x42990000, float:76.5)
            if (r10 == r11) goto L_0x0431
            r11 = 3
            if (r10 == r11) goto L_0x0431
            r11 = 5
            if (r10 != r11) goto L_0x0291
            r34 = r9
            goto L_0x0433
        L_0x0291:
            r11 = 4
            if (r10 == r11) goto L_0x02f0
            r11 = 10
            if (r10 != r11) goto L_0x0299
            goto L_0x02f0
        L_0x0299:
            int r10 = r36.getSize()
            float r10 = (float) r10
            r11 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r10 = r10 + r11
            if (r24 == 0) goto L_0x02bb
            float r11 = r5.getCenterX()
            float r12 = r5.getCenterY()
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r11, r12, r10, r15)
            r34 = r9
            r9 = 5
            goto L_0x0521
        L_0x02bb:
            android.graphics.Paint r11 = r0.paint
            int r11 = r11.getAlpha()
            int r12 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r12 == 0) goto L_0x02ce
            android.graphics.Paint r12 = r0.paint
            float r15 = (float) r11
            float r15 = r15 * r7
            int r15 = (int) r15
            r12.setAlpha(r15)
        L_0x02ce:
            float r12 = r5.getCenterX()
            float r15 = r5.getCenterY()
            android.graphics.Paint r3 = r0.paint
            r8.drawCircle(r12, r15, r10, r3)
            r3 = 1065353216(0x3var_, float:1.0)
            int r12 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r12 == 0) goto L_0x02eb
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r11)
            r34 = r9
            r9 = 5
            goto L_0x0521
        L_0x02eb:
            r34 = r9
            r9 = 5
            goto L_0x0521
        L_0x02f0:
            float r3 = r5.getCenterX()
            float r10 = r5.getCenterY()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r11 = (float) r11
            android.graphics.Paint r12 = r0.xRefP
            r8.drawCircle(r3, r10, r11, r12)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            if (r3 != 0) goto L_0x031c
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r10
        L_0x031c:
            int r3 = r0.currentStyle
            r10 = 10
            if (r3 != r10) goto L_0x0337
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r11 = r7 * r33
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r3.setColor(r10)
            goto L_0x034d
        L_0x0337:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            java.lang.String r10 = "voipgroup_listeningText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r11 = r7 * r33
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r3.setColor(r10)
        L_0x034d:
            long r10 = java.lang.System.currentTimeMillis()
            r3 = r4[r1]
            long r32 = r3.lastUpdateTime
            long r32 = r10 - r32
            r34 = 100
            int r3 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r3 <= 0) goto L_0x0403
            r3 = r4[r1]
            long unused = r3.lastUpdateTime = r10
            int r3 = r0.currentStyle
            r12 = 10
            if (r3 != r12) goto L_0x03b0
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            if (r3 == 0) goto L_0x039f
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            int r3 = (r3 > r26 ? 1 : (r3 == r26 ? 0 : -1))
            if (r3 <= 0) goto L_0x039f
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            android.view.View r15 = r0.parent
            r12 = 1
            r3.setShowWaves(r12, r15)
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            r12 = 1097859072(0x41700000, float:15.0)
            float r3 = r3 * r12
            r12 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r12 = r12.wavesDrawable
            r34 = r9
            r32 = r10
            double r9 = (double) r3
            r12.setAmplitude(r9)
            goto L_0x0407
        L_0x039f:
            r34 = r9
            r32 = r10
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 0
            r3.setShowWaves(r10, r9)
            goto L_0x0407
        L_0x03b0:
            r34 = r9
            r32 = r10
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            int r3 = r3.getCurrentTime()
            long r9 = (long) r3
            r3 = r4[r1]
            long r11 = r3.lastSpeakTime
            long r9 = r9 - r11
            r11 = 5
            int r3 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x03eb
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 1
            r3.setShowWaves(r10, r9)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            java.util.Random r9 = r0.random
            int r9 = r9.nextInt()
            int r9 = r9 % 100
            double r9 = (double) r9
            r3.setAmplitude(r9)
            goto L_0x0407
        L_0x03eb:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 0
            r3.setShowWaves(r10, r9)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r9 = 0
            r3.setAmplitude(r9)
            goto L_0x0407
        L_0x0403:
            r34 = r9
            r32 = r10
        L_0x0407:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r3.update()
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r9 = r5.getCenterX()
            float r10 = r5.getCenterY()
            android.view.View r11 = r0.parent
            r3.draw(r8, r9, r10, r11)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r9 = r3.getAvatarScale()
            r3 = r9
            r9 = 5
            goto L_0x0523
        L_0x0431:
            r34 = r9
        L_0x0433:
            float r3 = r5.getCenterX()
            float r9 = r5.getCenterY()
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            android.graphics.Paint r11 = r0.xRefP
            r8.drawCircle(r3, r9, r10, r11)
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            if (r3 != 0) goto L_0x047b
            int r3 = r0.currentStyle
            r9 = 5
            if (r3 != r9) goto L_0x0469
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r9
            goto L_0x047b
        L_0x0469:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r3.wavesDrawable = r9
        L_0x047b:
            int r3 = r0.currentStyle
            r9 = 5
            if (r3 != r9) goto L_0x0494
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r10 = r7 * r33
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r3.setColor(r9)
        L_0x0494:
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
            android.view.View r9 = r0.parent
            r10 = 1
            r3.setShowWaves(r10, r9)
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            float r3 = r3.amplitude
            r9 = 1097859072(0x41700000, float:15.0)
            float r3 = r3 * r9
            r9 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            double r11 = (double) r3
            r9.setAmplitude(r11)
            r11 = 0
            goto L_0x04d3
        L_0x04c6:
            r10 = 1
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            android.view.View r9 = r0.parent
            r11 = 0
            r3.setShowWaves(r11, r9)
        L_0x04d3:
            int r3 = r0.currentStyle
            r9 = 5
            if (r3 != r9) goto L_0x04ef
            long r32 = android.os.SystemClock.uptimeMillis()
            r3 = r4[r1]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            long r10 = r3.lastSpeakTime
            long r32 = r32 - r10
            r9 = 500(0x1f4, double:2.47E-321)
            int r3 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x04ef
            java.lang.Runnable r3 = r0.updateDelegate
            r3.run()
        L_0x04ef:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            r3.update()
            int r3 = r0.currentStyle
            r9 = 5
            if (r3 != r9) goto L_0x0513
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r10 = r5.getCenterX()
            float r11 = r5.getCenterY()
            android.view.View r12 = r0.parent
            r3.draw(r8, r10, r11, r12)
            r36.invalidate()
        L_0x0513:
            r3 = r4[r1]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r3 = r3.wavesDrawable
            float r3 = r3.getAvatarScale()
            goto L_0x0523
        L_0x051e:
            r34 = r9
            r9 = 5
        L_0x0521:
            r3 = r34
        L_0x0523:
            r5.setAlpha(r7)
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r10 == 0) goto L_0x0541
            r37.save()
            float r10 = r5.getCenterX()
            float r11 = r5.getCenterY()
            r8.scale(r3, r3, r10, r11)
            r5.draw(r8)
            r37.restore()
            goto L_0x0544
        L_0x0541:
            r5.draw(r8)
        L_0x0544:
            if (r6 == 0) goto L_0x0549
            r37.restore()
        L_0x0549:
            int r2 = r2 + 1
            r9 = 1
            r10 = 4
            r11 = 2
            r12 = 10
            r15 = 11
            goto L_0x012c
        L_0x0554:
            r9 = 5
            int r1 = r1 + -1
            r9 = 1
            r10 = 4
            r11 = 2
            r12 = 10
            r15 = 11
            goto L_0x0129
        L_0x0560:
            if (r24 == 0) goto L_0x0565
            r37.restore()
        L_0x0565:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsDarawable.onDraw(android.graphics.Canvas):void");
    }

    private int getSize() {
        int i = this.overrideSize;
        if (i != 0) {
            return i;
        }
        int i2 = this.currentStyle;
        return AndroidUtilities.dp(i2 == 4 || i2 == 10 ? 32.0f : 24.0f);
    }

    public void onDetachedFromWindow() {
        this.wasDraw = false;
        for (int a = 0; a < 3; a++) {
            this.currentStates[a].imageReceiver.onDetachedFromWindow();
            this.animatingStates[a].imageReceiver.onDetachedFromWindow();
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    public void onAttachedToWindow() {
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
        View view = this.parent;
        if (view != null) {
            view.requestLayout();
        }
    }

    public void reset() {
        for (int i = 0; i < this.animatingStates.length; i++) {
            setObject(0, 0, (TLObject) null);
        }
    }
}
