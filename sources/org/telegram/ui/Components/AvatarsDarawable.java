package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
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

public class AvatarsDarawable {
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
    Random random;
    public long transitionDuration = 220;
    private boolean transitionInProgress;
    float transitionProgress = 1.0f;
    ValueAnimator transitionProgressAnimator;
    boolean updateAfterTransition;
    Runnable updateDelegate;
    boolean wasDraw;
    public int width;
    private Paint xRefP = new Paint(1);

    public void commitTransition(boolean z) {
        commitTransition(z, true);
    }

    public void setTransitionProgress(float f) {
        if (this.transitionInProgress && this.transitionProgress != f) {
            this.transitionProgress = f;
            if (f == 1.0f) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
    }

    public void commitTransition(boolean z, boolean z2) {
        boolean z3;
        if (!this.wasDraw || !z) {
            this.transitionProgress = 1.0f;
            swapStates();
            return;
        }
        DrawingState[] drawingStateArr = new DrawingState[3];
        boolean z4 = false;
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr2 = this.currentStates;
            drawingStateArr[i] = drawingStateArr2[i];
            if (drawingStateArr2[i].id != this.animatingStates[i].id) {
                z4 = true;
            } else {
                long unused = this.currentStates[i].lastSpeakTime = this.animatingStates[i].lastSpeakTime;
            }
        }
        if (!z4) {
            this.transitionProgress = 1.0f;
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= 3) {
                    z3 = false;
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
                    z3 = true;
                } else {
                    i3++;
                }
            }
            if (!z3) {
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
            if (this.transitionInProgress) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
        this.transitionProgress = 0.0f;
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.transitionProgressAnimator = ofFloat;
            ofFloat.addUpdateListener(new AvatarsDarawable$$ExternalSyntheticLambda0(this));
            this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AvatarsDarawable avatarsDarawable = AvatarsDarawable.this;
                    if (avatarsDarawable.transitionProgressAnimator != null) {
                        avatarsDarawable.transitionProgress = 1.0f;
                        avatarsDarawable.swapStates();
                        AvatarsDarawable avatarsDarawable2 = AvatarsDarawable.this;
                        if (avatarsDarawable2.updateAfterTransition) {
                            avatarsDarawable2.updateAfterTransition = false;
                            Runnable runnable = avatarsDarawable2.updateDelegate;
                            if (runnable != null) {
                                runnable.run();
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

    /* access modifiers changed from: private */
    public void invalidate() {
        View view = this.parent;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setSize(int i) {
        this.overrideSize = i;
    }

    public void animateFromState(AvatarsDarawable avatarsDarawable, int i, boolean z) {
        ValueAnimator valueAnimator = avatarsDarawable.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            if (this.transitionInProgress) {
                this.transitionInProgress = false;
                swapStates();
            }
        }
        TLObject[] tLObjectArr = new TLObject[3];
        for (int i2 = 0; i2 < 3; i2++) {
            tLObjectArr[i2] = this.currentStates[i2].object;
            setObject(i2, i, avatarsDarawable.currentStates[i2].object);
        }
        commitTransition(false);
        for (int i3 = 0; i3 < 3; i3++) {
            setObject(i3, i, tLObjectArr[i3]);
        }
        this.wasDraw = true;
        commitTransition(true, z);
    }

    public void setAlpha(float f) {
        this.overrideAlpha = f;
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
        /* access modifiers changed from: private */
        public TLObject object;
        TLRPC$TL_groupCallParticipant participant;
        /* access modifiers changed from: private */
        public GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

        private DrawingState() {
        }
    }

    public AvatarsDarawable(View view, boolean z) {
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.random = new Random();
        this.parent = view;
        for (int i = 0; i < 3; i++) {
            this.currentStates[i] = new DrawingState();
            ImageReceiver unused = this.currentStates[i].imageReceiver = new ImageReceiver(view);
            this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused2 = this.currentStates[i].avatarDrawable = new AvatarDrawable();
            this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            this.animatingStates[i] = new DrawingState();
            ImageReceiver unused3 = this.animatingStates[i].imageReceiver = new ImageReceiver(view);
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            AvatarDrawable unused4 = this.animatingStates[i].avatarDrawable = new AvatarDrawable();
            this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        }
        this.isInCall = z;
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
        TLObject unused3 = this.animatingStates[i].object = tLObject;
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
                long unused4 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
            } else if (peerId == AccountInstance.getInstance(i2).getUserConfig().getClientUserId()) {
                long unused5 = this.animatingStates[i].lastSpeakTime = 0;
            } else if (this.isInCall) {
                long unused6 = this.animatingStates[i].lastSpeakTime = tLRPC$TL_groupCallParticipant.lastActiveDate;
            } else {
                long unused7 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
            }
            long unused8 = this.animatingStates[i].id = peerId;
            tLRPC$Chat = tLRPC$Chat2;
        } else if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User2 = (TLRPC$User) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$User2);
            long unused9 = this.animatingStates[i].id = tLRPC$User2.id;
            tLRPC$User = tLRPC$User2;
            tLRPC$Chat = null;
        } else {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
            this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat);
            long unused10 = this.animatingStates[i].id = -tLRPC$Chat.id;
        }
        if (tLRPC$User != null) {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$User, this.animatingStates[i].avatarDrawable);
        } else {
            this.animatingStates[i].imageReceiver.setForUserOrChat(tLRPC$Chat, this.animatingStates[i].avatarDrawable);
        }
        int i3 = this.currentStyle;
        this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(i3 == 4 || i3 == 10 ? 16.0f : 12.0f));
        float size = (float) getSize();
        this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, size, size);
        invalidate();
    }

    /* JADX WARNING: Removed duplicated region for block: B:146:0x025f  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x04e8  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x04fd  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0505 A[SYNTHETIC] */
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
            int r14 = r32.getSize()
            int r1 = r0.currentStyle
            r15 = 11
            if (r1 != r15) goto L_0x0027
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
        L_0x0024:
            r16 = r1
            goto L_0x003f
        L_0x0027:
            int r1 = r0.overrideSize
            if (r1 == 0) goto L_0x0033
            float r1 = (float) r1
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            float r1 = r1 * r2
            int r1 = (int) r1
            goto L_0x0024
        L_0x0033:
            if (r13 == 0) goto L_0x0038
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            goto L_0x003a
        L_0x0038:
            r1 = 1101004800(0x41a00000, float:20.0)
        L_0x003a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0024
        L_0x003f:
            r1 = 0
            r2 = 0
        L_0x0041:
            r3 = 0
            r7 = 3
            if (r1 >= r7) goto L_0x0057
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r5 = r0.currentStates
            r5 = r5[r1]
            long r5 = r5.id
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x0054
            int r2 = r2 + 1
        L_0x0054:
            int r1 = r1 + 1
            goto L_0x0041
        L_0x0057:
            int r1 = r0.currentStyle
            if (r1 == 0) goto L_0x0069
            if (r1 == r11) goto L_0x0069
            if (r1 != r15) goto L_0x0060
            goto L_0x0069
        L_0x0060:
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r17 = r1
            goto L_0x006b
        L_0x0069:
            r17 = 0
        L_0x006b:
            boolean r1 = r0.centered
            r18 = 1082130432(0x40800000, float:4.0)
            r19 = 1090519040(0x41000000, float:8.0)
            r6 = 2
            if (r1 == 0) goto L_0x0089
            int r1 = r0.width
            int r2 = r2 * r16
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
            r20 = r1
            goto L_0x008b
        L_0x0089:
            r20 = r17
        L_0x008b:
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
            int r2 = r0.currentStyle
            if (r2 != r10) goto L_0x00ae
            android.graphics.Paint r1 = r0.paint
            java.lang.String r2 = "inappPlayerBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            goto L_0x00c0
        L_0x00ae:
            if (r2 == r7) goto L_0x00c0
            android.graphics.Paint r2 = r0.paint
            if (r1 == 0) goto L_0x00b7
            java.lang.String r1 = "returnToCallMutedBackground"
            goto L_0x00b9
        L_0x00b7:
            java.lang.String r1 = "returnToCallBackground"
        L_0x00b9:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setColor(r1)
        L_0x00c0:
            r1 = 0
            r21 = 0
        L_0x00c3:
            if (r1 >= r7) goto L_0x00d6
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r2 = r0.animatingStates
            r2 = r2[r1]
            long r22 = r2.id
            int r2 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x00d3
            int r21 = r21 + 1
        L_0x00d3:
            int r1 = r1 + 1
            goto L_0x00c3
        L_0x00d6:
            int r1 = r0.currentStyle
            r5 = 5
            if (r1 == 0) goto L_0x00eb
            if (r1 == r9) goto L_0x00eb
            if (r1 == r7) goto L_0x00eb
            if (r1 == r10) goto L_0x00eb
            if (r1 == r5) goto L_0x00eb
            if (r1 == r11) goto L_0x00eb
            if (r1 != r15) goto L_0x00e8
            goto L_0x00eb
        L_0x00e8:
            r22 = 0
            goto L_0x00ed
        L_0x00eb:
            r22 = 1
        L_0x00ed:
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 0
            if (r22 == 0) goto L_0x011a
            if (r1 != r11) goto L_0x00fb
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            goto L_0x00fc
        L_0x00fb:
            r1 = 0
        L_0x00fc:
            float r3 = -r1
            int r2 = r0.width
            float r2 = (float) r2
            float r4 = r2 + r1
            int r2 = r0.height
            float r2 = (float) r2
            float r25 = r2 + r1
            r26 = 255(0xff, float:3.57E-43)
            r27 = 31
            r1 = r33
            r2 = r3
            r12 = 5
            r5 = r25
            r12 = 2
            r6 = r26
            r7 = r27
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x011b
        L_0x011a:
            r12 = 2
        L_0x011b:
            r6 = 2
        L_0x011c:
            if (r6 < 0) goto L_0x051c
            r1 = 0
        L_0x011f:
            if (r1 >= r12) goto L_0x0510
            r2 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x012e
            float r3 = r0.transitionProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 != 0) goto L_0x012e
        L_0x012b:
            r9 = 5
            goto L_0x0505
        L_0x012e:
            if (r1 != 0) goto L_0x0133
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r3 = r0.animatingStates
            goto L_0x0135
        L_0x0133:
            org.telegram.ui.Components.AvatarsDarawable$DrawingState[] r3 = r0.currentStates
        L_0x0135:
            if (r1 != r9) goto L_0x0146
            float r4 = r0.transitionProgress
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0146
            r4 = r3[r6]
            int r4 = r4.animationType
            if (r4 == r9) goto L_0x0146
            goto L_0x012b
        L_0x0146:
            r4 = r3[r6]
            org.telegram.messenger.ImageReceiver r4 = r4.imageReceiver
            boolean r5 = r4.hasImageSet()
            if (r5 != 0) goto L_0x0153
            goto L_0x012b
        L_0x0153:
            if (r1 != 0) goto L_0x0175
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x016c
            int r5 = r0.width
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0163
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x0165
        L_0x0163:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x0165:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x016e
        L_0x016c:
            r5 = r17
        L_0x016e:
            int r7 = r16 * r6
            int r5 = r5 + r7
            r4.setImageX(r5)
            goto L_0x017c
        L_0x0175:
            int r5 = r16 * r6
            int r5 = r20 + r5
            r4.setImageX(r5)
        L_0x017c:
            int r5 = r0.currentStyle
            r7 = 1073741824(0x40000000, float:2.0)
            if (r5 == 0) goto L_0x0197
            if (r5 == r11) goto L_0x0197
            if (r5 != r15) goto L_0x0187
            goto L_0x0197
        L_0x0187:
            if (r5 != r10) goto L_0x018c
            r5 = 1090519040(0x41000000, float:8.0)
            goto L_0x018e
        L_0x018c:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x018e:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setImageY(r5)
            goto L_0x019f
        L_0x0197:
            int r5 = r0.height
            int r5 = r5 - r14
            float r5 = (float) r5
            float r5 = r5 / r7
            r4.setImageY(r5)
        L_0x019f:
            float r5 = r0.transitionProgress
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0254
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r9) goto L_0x01c8
            r33.save()
            float r5 = r0.transitionProgress
            float r15 = r2 - r5
            float r5 = r2 - r5
            float r7 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r8.scale(r15, r5, r7, r11)
            float r5 = r0.transitionProgress
            float r5 = r2 - r5
        L_0x01c5:
            r7 = 1
            goto L_0x0257
        L_0x01c8:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != 0) goto L_0x01e3
            r33.save()
            float r5 = r0.transitionProgress
            float r7 = r4.getCenterX()
            float r11 = r4.getCenterY()
            r8.scale(r5, r5, r7, r11)
            float r5 = r0.transitionProgress
            goto L_0x01c5
        L_0x01e3:
            r5 = r3[r6]
            int r5 = r5.animationType
            if (r5 != r12) goto L_0x0221
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0202
            int r5 = r0.width
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x01f9
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x01fb
        L_0x01f9:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x01fb:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r5 = r5 / r12
            goto L_0x0204
        L_0x0202:
            r5 = r17
        L_0x0204:
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
            goto L_0x0254
        L_0x0221:
            r5 = r3[r6]
            int r5 = r5.animationType
            r7 = -1
            if (r5 != r7) goto L_0x0254
            boolean r5 = r0.centered
            if (r5 == 0) goto L_0x0254
            int r5 = r0.width
            int r7 = r21 * r16
            int r5 = r5 - r7
            if (r13 == 0) goto L_0x0238
            r7 = 1090519040(0x41000000, float:8.0)
            goto L_0x023a
        L_0x0238:
            r7 = 1082130432(0x40800000, float:4.0)
        L_0x023a:
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
        L_0x0254:
            r5 = 1065353216(0x3var_, float:1.0)
            r7 = 0
        L_0x0257:
            float r11 = r0.overrideAlpha
            float r5 = r5 * r11
            int r11 = r3.length
            int r11 = r11 - r9
            if (r6 == r11) goto L_0x04dc
            int r11 = r0.currentStyle
            r15 = 1097859072(0x41700000, float:15.0)
            r28 = 1101529088(0x41a80000, float:21.0)
            java.lang.String r29 = "voipgroup_speakingText"
            r30 = 1099431936(0x41880000, float:17.0)
            r31 = 1117323264(0x42990000, float:76.5)
            if (r11 == r9) goto L_0x03f3
            r12 = 3
            if (r11 == r12) goto L_0x03f3
            r12 = 5
            if (r11 != r12) goto L_0x0275
            goto L_0x03f3
        L_0x0275:
            if (r11 == r10) goto L_0x02c6
            r12 = 10
            if (r11 != r12) goto L_0x027c
            goto L_0x02c6
        L_0x027c:
            int r3 = r32.getSize()
            float r3 = (float) r3
            r11 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r3 = r3 + r11
            if (r22 == 0) goto L_0x029b
            float r11 = r4.getCenterX()
            float r12 = r4.getCenterY()
            android.graphics.Paint r15 = r0.xRefP
            r8.drawCircle(r11, r12, r3, r15)
            goto L_0x04dc
        L_0x029b:
            android.graphics.Paint r11 = r0.paint
            int r11 = r11.getAlpha()
            int r12 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r12 == 0) goto L_0x02ae
            android.graphics.Paint r12 = r0.paint
            float r15 = (float) r11
            float r15 = r15 * r5
            int r15 = (int) r15
            r12.setAlpha(r15)
        L_0x02ae:
            float r12 = r4.getCenterX()
            float r15 = r4.getCenterY()
            android.graphics.Paint r10 = r0.paint
            r8.drawCircle(r12, r15, r3, r10)
            int r3 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x04dc
            android.graphics.Paint r3 = r0.paint
            r3.setAlpha(r11)
            goto L_0x04dc
        L_0x02c6:
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r12 = (float) r12
            android.graphics.Paint r2 = r0.xRefP
            r8.drawCircle(r10, r11, r12, r2)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x02f2
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r10.<init>(r11, r12)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r10
        L_0x02f2:
            int r2 = r0.currentStyle
            r10 = 10
            if (r2 != r10) goto L_0x030d
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r11 = r5 * r31
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r2.setColor(r10)
            goto L_0x0323
        L_0x030d:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.lang.String r10 = "voipgroup_listeningText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r11 = r5 * r31
            int r11 = (int) r11
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r10, r11)
            r2.setColor(r10)
        L_0x0323:
            long r10 = java.lang.System.currentTimeMillis()
            r2 = r3[r6]
            long r28 = r2.lastUpdateTime
            long r28 = r10 - r28
            r30 = 100
            int r2 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1))
            if (r2 <= 0) goto L_0x03ca
            r2 = r3[r6]
            long unused = r2.lastUpdateTime = r10
            int r2 = r0.currentStyle
            r10 = 10
            if (r2 != r10) goto L_0x037b
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x036e
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x036e
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r11 = r0.parent
            r2.setShowWaves(r9, r11)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r15
            r11 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r11.wavesDrawable
            double r9 = (double) r2
            r11.setAmplitude(r9)
            goto L_0x03ca
        L_0x036e:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 0
            r2.setShowWaves(r10, r9)
            goto L_0x03ca
        L_0x037b:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            long r9 = (long) r2
            r2 = r3[r6]
            long r28 = r2.lastSpeakTime
            long r9 = r9 - r28
            r28 = 5
            int r2 = (r9 > r28 ? 1 : (r9 == r28 ? 0 : -1))
            if (r2 > 0) goto L_0x03b3
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 1
            r2.setShowWaves(r10, r9)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            java.util.Random r9 = r0.random
            int r9 = r9.nextInt()
            int r9 = r9 % 100
            double r9 = (double) r9
            r2.setAmplitude(r9)
            goto L_0x03ca
        L_0x03b3:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 0
            r2.setShowWaves(r10, r9)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r9 = 0
            r2.setAmplitude(r9)
        L_0x03ca:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r9 = r4.getCenterX()
            float r10 = r4.getCenterY()
            android.view.View r11 = r0.parent
            r2.draw(r8, r9, r10, r11)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            r9 = 5
            goto L_0x04df
        L_0x03f3:
            float r2 = r4.getCenterX()
            float r9 = r4.getCenterY()
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            android.graphics.Paint r11 = r0.xRefP
            r8.drawCircle(r2, r9, r10, r11)
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            if (r2 != 0) goto L_0x043b
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x0429
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
            goto L_0x043b
        L_0x0429:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r9.<init>(r10, r11)
            org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r2.wavesDrawable = r9
        L_0x043b:
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x0454
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r29)
            float r10 = r5 * r31
            int r10 = (int) r10
            int r9 = androidx.core.graphics.ColorUtils.setAlphaComponent(r9, r10)
            r2.setColor(r9)
        L_0x0454:
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            if (r2 == 0) goto L_0x0484
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            int r2 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x0484
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r9 = r0.parent
            r10 = 1
            r2.setShowWaves(r10, r9)
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            float r2 = r2.amplitude
            float r2 = r2 * r15
            r9 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = r9.wavesDrawable
            double r11 = (double) r2
            r9.setAmplitude(r11)
            r11 = 0
            goto L_0x0491
        L_0x0484:
            r10 = 1
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            android.view.View r9 = r0.parent
            r11 = 0
            r2.setShowWaves(r11, r9)
        L_0x0491:
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x04ad
            long r28 = android.os.SystemClock.uptimeMillis()
            r2 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            long r10 = r2.lastSpeakTime
            long r28 = r28 - r10
            r9 = 500(0x1f4, double:2.47E-321)
            int r2 = (r28 > r9 ? 1 : (r28 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x04ad
            java.lang.Runnable r2 = r0.updateDelegate
            r2.run()
        L_0x04ad:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            r2.update()
            int r2 = r0.currentStyle
            r9 = 5
            if (r2 != r9) goto L_0x04d1
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r10 = r4.getCenterX()
            float r11 = r4.getCenterY()
            android.view.View r15 = r0.parent
            r2.draw(r8, r10, r11, r15)
            r32.invalidate()
        L_0x04d1:
            r2 = r3[r6]
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r2.wavesDrawable
            float r2 = r2.getAvatarScale()
            goto L_0x04df
        L_0x04dc:
            r9 = 5
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x04df:
            r4.setAlpha(r5)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x04fd
            r33.save()
            float r3 = r4.getCenterX()
            float r5 = r4.getCenterY()
            r8.scale(r2, r2, r3, r5)
            r4.draw(r8)
            r33.restore()
            goto L_0x0500
        L_0x04fd:
            r4.draw(r8)
        L_0x0500:
            if (r7 == 0) goto L_0x0505
            r33.restore()
        L_0x0505:
            int r1 = r1 + 1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            r15 = 11
            goto L_0x011f
        L_0x0510:
            r9 = 5
            int r6 = r6 + -1
            r9 = 1
            r10 = 4
            r11 = 10
            r12 = 2
            r15 = 11
            goto L_0x011c
        L_0x051c:
            if (r22 == 0) goto L_0x0521
            r33.restore()
        L_0x0521:
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
        for (int i = 0; i < 3; i++) {
            this.currentStates[i].imageReceiver.onDetachedFromWindow();
            this.animatingStates[i].imageReceiver.onDetachedFromWindow();
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    public void onAttachedToWindow() {
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
