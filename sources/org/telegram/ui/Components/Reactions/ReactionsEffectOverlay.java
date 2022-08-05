package org.telegram.ui.Components.Reactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ReactionsContainerLayout;

public class ReactionsEffectOverlay {
    @SuppressLint({"StaticFieldLeak"})
    public static ReactionsEffectOverlay currentOverlay;
    @SuppressLint({"StaticFieldLeak"})
    public static ReactionsEffectOverlay currentShortOverlay;
    private static long lastHapticTime;
    private static int uniqPrefix;
    float animateInProgress;
    float animateOutProgress;
    /* access modifiers changed from: private */
    public final int animationType;
    ArrayList<AvatarParticle> avatars = new ArrayList<>();
    private ChatMessageCell cell;
    /* access modifiers changed from: private */
    public final FrameLayout container;
    private ViewGroup decorView;
    /* access modifiers changed from: private */
    public float dismissProgress;
    /* access modifiers changed from: private */
    public boolean dismissed;
    /* access modifiers changed from: private */
    public final AnimationView effectImageView;
    /* access modifiers changed from: private */
    public final AnimationView emojiImageView;
    /* access modifiers changed from: private */
    public final AnimationView emojiStaticImageView;
    /* access modifiers changed from: private */
    public boolean finished;
    private final long groupId;
    /* access modifiers changed from: private */
    public ReactionsContainerLayout.ReactionHolderView holderView = null;
    /* access modifiers changed from: private */
    public float lastDrawnToX;
    /* access modifiers changed from: private */
    public float lastDrawnToY;
    int[] loc = new int[2];
    /* access modifiers changed from: private */
    public final int messageId;
    private final String reaction;
    /* access modifiers changed from: private */
    public boolean started;
    private boolean useWindow;
    /* access modifiers changed from: private */
    public boolean wasScrolled;
    private WindowManager windowManager;
    FrameLayout windowView;

    static /* synthetic */ float access$216(ReactionsEffectOverlay reactionsEffectOverlay, float f) {
        float f2 = reactionsEffectOverlay.dismissProgress + f;
        reactionsEffectOverlay.dismissProgress = f2;
        return f2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:110:0x05ce  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x02e7  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0397  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ReactionsEffectOverlay(android.content.Context r33, org.telegram.ui.ActionBar.BaseFragment r34, org.telegram.ui.Components.ReactionsContainerLayout r35, org.telegram.ui.Cells.ChatMessageCell r36, float r37, float r38, java.lang.String r39, int r40, int r41) {
        /*
            r32 = this;
            r13 = r32
            r14 = r33
            r3 = r34
            r0 = r35
            r15 = r36
            r12 = r39
            r11 = r41
            r32.<init>()
            r10 = 2
            int[] r1 = new int[r10]
            r13.loc = r1
            r1 = 0
            r13.holderView = r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r13.avatars = r2
            org.telegram.messenger.MessageObject r2 = r36.getMessageObject()
            int r2 = r2.getId()
            r13.messageId = r2
            org.telegram.messenger.MessageObject r2 = r36.getMessageObject()
            long r4 = r2.getGroupId()
            r13.groupId = r4
            r13.reaction = r12
            r13.animationType = r11
            r13.cell = r15
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$ReactionButton r2 = r15.getReactionButton(r12)
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0047
            r4 = r3
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            r6 = r4
            goto L_0x0048
        L_0x0047:
            r6 = r1
        L_0x0048:
            if (r0 == 0) goto L_0x0073
            r4 = 0
        L_0x004b:
            org.telegram.ui.Components.RecyclerListView r5 = r0.recyclerListView
            int r5 = r5.getChildCount()
            if (r4 >= r5) goto L_0x0073
            org.telegram.ui.Components.RecyclerListView r5 = r0.recyclerListView
            android.view.View r5 = r5.getChildAt(r4)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r5 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r5
            org.telegram.tgnet.TLRPC$TL_availableReaction r5 = r5.currentReaction
            java.lang.String r5 = r5.reaction
            boolean r5 = r5.equals(r12)
            if (r5 == 0) goto L_0x0070
            org.telegram.ui.Components.RecyclerListView r0 = r0.recyclerListView
            android.view.View r0 = r0.getChildAt(r4)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r0 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r0
            r13.holderView = r0
            goto L_0x0073
        L_0x0070:
            int r4 = r4 + 1
            goto L_0x004b
        L_0x0073:
            r8 = 1
            if (r11 != r8) goto L_0x0235
            java.util.Random r5 = new java.util.Random
            r5.<init>()
            org.telegram.messenger.MessageObject r7 = r36.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r7 = r7.reactions
            if (r7 == 0) goto L_0x0090
            org.telegram.messenger.MessageObject r7 = r36.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r7 = r7.reactions
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messagePeerReaction> r7 = r7.recent_reactions
            goto L_0x0091
        L_0x0090:
            r7 = r1
        L_0x0091:
            if (r7 == 0) goto L_0x0235
            if (r6 == 0) goto L_0x0235
            long r16 = r6.getDialogId()
            r18 = 0
            int r20 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r20 >= 0) goto L_0x0235
            r10 = 0
        L_0x00a0:
            int r8 = r7.size()
            if (r10 >= r8) goto L_0x0235
            java.lang.Object r8 = r7.get(r10)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r8 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r8
            java.lang.String r8 = r8.reaction
            boolean r8 = r12.equals(r8)
            if (r8 == 0) goto L_0x022a
            java.lang.Object r8 = r7.get(r10)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r8 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r8
            boolean r8 = r8.unread
            if (r8 == 0) goto L_0x022a
            org.telegram.ui.Components.AvatarDrawable r8 = new org.telegram.ui.Components.AvatarDrawable
            r8.<init>()
            org.telegram.messenger.ImageReceiver r9 = new org.telegram.messenger.ImageReceiver
            r9.<init>()
            java.lang.Object r20 = r7.get(r10)
            r4 = r20
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r4 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r4
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            long r0 = org.telegram.messenger.MessageObject.getPeerId(r4)
            int r4 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r4 >= 0) goto L_0x00f2
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r40)
            long r0 = -r0
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r4.getChat(r0)
            if (r0 != 0) goto L_0x00eb
            goto L_0x022a
        L_0x00eb:
            r8.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            r9.setForUserOrChat(r0, r8)
            goto L_0x0108
        L_0x00f2:
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r40)
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r4.getUser(r0)
            if (r0 != 0) goto L_0x0102
            goto L_0x022a
        L_0x0102:
            r8.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            r9.setForUserOrChat(r0, r8)
        L_0x0108:
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle
            r1 = 0
            r0.<init>()
            r0.imageReceiver = r9
            r4 = 1056964608(0x3var_, float:0.5)
            r0.fromX = r4
            r0.fromY = r4
            r4 = 1050253722(0x3e99999a, float:0.3)
            int r8 = r5.nextInt()
            int r8 = r8 % 100
            int r8 = java.lang.Math.abs(r8)
            float r8 = (float) r8
            r9 = 1120403456(0x42CLASSNAME, float:100.0)
            float r8 = r8 / r9
            r20 = 1036831949(0x3dcccccd, float:0.1)
            float r8 = r8 * r20
            float r8 = r8 + r4
            r0.jumpY = r8
            int r4 = r5.nextInt()
            int r4 = r4 % 100
            int r4 = java.lang.Math.abs(r4)
            float r4 = (float) r4
            float r4 = r4 / r9
            r8 = 1053609165(0x3ecccccd, float:0.4)
            float r4 = r4 * r8
            r20 = 1061997773(0x3f4ccccd, float:0.8)
            float r4 = r4 + r20
            r0.randomScale = r4
            int r4 = r5.nextInt()
            int r4 = r4 % 100
            int r4 = java.lang.Math.abs(r4)
            int r4 = r4 * 60
            float r4 = (float) r4
            float r4 = r4 / r9
            r0.randomRotation = r4
            r4 = 1137180672(0x43CLASSNAME, float:400.0)
            int r20 = r5.nextInt()
            int r20 = r20 % 100
            int r1 = java.lang.Math.abs(r20)
            float r1 = (float) r1
            float r1 = r1 / r9
            r20 = 1128792064(0x43480000, float:200.0)
            float r1 = r1 * r20
            float r1 = r1 + r4
            int r1 = (int) r1
            r0.leftTime = r1
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r1 = r13.avatars
            boolean r1 = r1.isEmpty()
            r4 = 1058642330(0x3var_a, float:0.6)
            r20 = 1045220557(0x3e4ccccd, float:0.2)
            if (r1 == 0) goto L_0x01a1
            int r1 = r5.nextInt()
            int r1 = r1 % 100
            int r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            float r1 = r1 * r4
            float r1 = r1 / r9
            float r1 = r1 + r20
            r0.toX = r1
            int r1 = r5.nextInt()
            int r1 = r1 % 100
            int r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            float r1 = r1 * r8
            float r1 = r1 / r9
            r0.toY = r1
            r30 = r5
            goto L_0x0224
        L_0x01a1:
            r1 = 0
            r23 = 0
            r24 = 0
            r25 = 0
        L_0x01a8:
            r8 = 10
            if (r1 >= r8) goto L_0x021a
            int r8 = r5.nextInt()
            int r8 = r8 % 100
            int r8 = java.lang.Math.abs(r8)
            float r8 = (float) r8
            float r8 = r8 * r4
            float r8 = r8 / r9
            float r8 = r8 + r20
            int r27 = r5.nextInt()
            int r27 = r27 % 100
            int r4 = java.lang.Math.abs(r27)
            float r4 = (float) r4
            r26 = 1053609165(0x3ecccccd, float:0.4)
            float r4 = r4 * r26
            float r4 = r4 / r9
            float r4 = r4 + r20
            r27 = 1325400064(0x4var_, float:2.14748365E9)
            r9 = 0
        L_0x01d2:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r3 = r13.avatars
            int r3 = r3.size()
            if (r9 >= r3) goto L_0x0202
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r3 = r13.avatars
            java.lang.Object r3 = r3.get(r9)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r3 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r3
            float r3 = r3.toX
            float r3 = r3 - r8
            r30 = r5
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r5 = r13.avatars
            java.lang.Object r5 = r5.get(r9)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r5 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r5
            float r5 = r5.toY
            float r5 = r5 - r4
            float r3 = r3 * r3
            float r5 = r5 * r5
            float r3 = r3 + r5
            int r5 = (r3 > r27 ? 1 : (r3 == r27 ? 0 : -1))
            if (r5 >= 0) goto L_0x01fd
            r27 = r3
        L_0x01fd:
            int r9 = r9 + 1
            r5 = r30
            goto L_0x01d2
        L_0x0202:
            r30 = r5
            int r3 = (r27 > r25 ? 1 : (r27 == r25 ? 0 : -1))
            if (r3 <= 0) goto L_0x020e
            r24 = r4
            r23 = r8
            r25 = r27
        L_0x020e:
            int r1 = r1 + 1
            r3 = r34
            r5 = r30
            r4 = 1058642330(0x3var_a, float:0.6)
            r9 = 1120403456(0x42CLASSNAME, float:100.0)
            goto L_0x01a8
        L_0x021a:
            r30 = r5
            r1 = r23
            r0.toX = r1
            r1 = r24
            r0.toY = r1
        L_0x0224:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r1 = r13.avatars
            r1.add(r0)
            goto L_0x022c
        L_0x022a:
            r30 = r5
        L_0x022c:
            int r10 = r10 + 1
            r3 = r34
            r5 = r30
            r1 = 0
            goto L_0x00a0
        L_0x0235:
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r0 = r13.holderView
            if (r0 != 0) goto L_0x0245
            r1 = 0
            int r3 = (r37 > r1 ? 1 : (r37 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0243
            int r3 = (r38 > r1 ? 1 : (r38 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0243
            goto L_0x0245
        L_0x0243:
            r9 = 0
            goto L_0x0246
        L_0x0245:
            r9 = 1
        L_0x0246:
            if (r0 == 0) goto L_0x0284
            int[] r1 = r13.loc
            r0.getLocationOnScreen(r1)
            int[] r0 = r13.loc
            r1 = 0
            r0 = r0[r1]
            float r0 = (float) r0
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r1 = r13.holderView
            org.telegram.ui.Components.BackupImageView r1 = r1.backupImageView
            float r1 = r1.getX()
            float r0 = r0 + r1
            int[] r1 = r13.loc
            r2 = 1
            r1 = r1[r2]
            float r1 = (float) r1
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r13.holderView
            org.telegram.ui.Components.BackupImageView r2 = r2.backupImageView
            float r2 = r2.getY()
            float r1 = r1 + r2
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r13.holderView
            org.telegram.ui.Components.BackupImageView r2 = r2.backupImageView
            int r2 = r2.getWidth()
            float r2 = (float) r2
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r3 = r13.holderView
            float r3 = r3.getScaleX()
            float r2 = r2 * r3
            r16 = r0
            r17 = r1
            r1 = r2
        L_0x0281:
            r8 = 2
            r10 = 0
            goto L_0x02e5
        L_0x0284:
            if (r2 == 0) goto L_0x02c6
            int[] r0 = r13.loc
            r15.getLocationInWindow(r0)
            int[] r0 = r13.loc
            r1 = 0
            r0 = r0[r1]
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r1 = r15.reactionsLayoutInBubble
            int r1 = r1.x
            int r0 = r0 + r1
            int r1 = r2.x
            int r0 = r0 + r1
            float r0 = (float) r0
            org.telegram.messenger.ImageReceiver r1 = r2.imageReceiver
            float r1 = r1.getImageX()
            float r0 = r0 + r1
            int[] r1 = r13.loc
            r3 = 1
            r1 = r1[r3]
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r3 = r15.reactionsLayoutInBubble
            int r3 = r3.y
            int r1 = r1 + r3
            int r3 = r2.y
            int r1 = r1 + r3
            float r1 = (float) r1
            org.telegram.messenger.ImageReceiver r3 = r2.imageReceiver
            float r3 = r3.getImageY()
            float r1 = r1 + r3
            org.telegram.messenger.ImageReceiver r3 = r2.imageReceiver
            float r3 = r3.getImageHeight()
            org.telegram.messenger.ImageReceiver r2 = r2.imageReceiver
            r2.getImageWidth()
            r16 = r0
            r17 = r1
            r1 = r3
            goto L_0x0281
        L_0x02c6:
            android.view.ViewParent r0 = r36.getParent()
            android.view.View r0 = (android.view.View) r0
            int[] r1 = r13.loc
            r0.getLocationInWindow(r1)
            int[] r0 = r13.loc
            r10 = 0
            r1 = r0[r10]
            float r1 = (float) r1
            float r1 = r1 + r37
            r2 = 1
            r0 = r0[r2]
            float r0 = (float) r0
            float r0 = r0 + r38
            r17 = r0
            r16 = r1
            r1 = 0
            r8 = 2
        L_0x02e5:
            if (r11 != r8) goto L_0x02fa
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r2 = 1073741824(0x40000000, float:2.0)
            float r3 = (float) r0
            float r3 = r3 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r2
            int r2 = (int) r3
            r5 = r0
            r4 = r2
            r7 = 1
            goto L_0x032c
        L_0x02fa:
            r7 = 1
            if (r11 != r7) goto L_0x0308
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = sizeForAroundReaction()
            goto L_0x032a
        L_0x0308:
            r0 = 1135542272(0x43avar_, float:350.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.min(r3, r2)
            int r0 = java.lang.Math.min(r0, r2)
            float r0 = (float) r0
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            float r0 = r0 * r2
            int r0 = java.lang.Math.round(r0)
            int r2 = sizeForBigReaction()
        L_0x032a:
            r5 = r0
            r4 = r2
        L_0x032c:
            int r3 = r5 >> 1
            int r2 = r4 >> 1
            float r0 = (float) r3
            float r18 = r1 / r0
            r0 = 0
            r13.animateInProgress = r0
            r13.animateOutProgress = r0
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r14)
            r13.container = r1
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1 r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1
            r35 = r0
            r15 = r1
            r1 = r32
            r19 = r15
            r15 = r2
            r2 = r33
            r37 = r3
            r3 = r34
            r20 = r15
            r15 = r4
            r4 = r36
            r31 = r5
            r5 = r39
            r21 = 1
            r7 = r37
            r22 = r15
            r15 = 1
            r21 = 2
            r8 = r41
            r15 = 2
            r10 = r18
            r15 = r11
            r11 = r16
            r15 = r12
            r12 = r17
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r13.windowView = r0
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r0.<init>(r14)
            r13.effectImageView = r0
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r8 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r8.<init>(r14)
            r13.emojiImageView = r8
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r9 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r9.<init>(r14)
            r13.emojiStaticImageView = r9
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r40)
            java.util.HashMap r1 = r1.getReactionsMap()
            java.lang.Object r1 = r1.get(r15)
            r10 = r1
            org.telegram.tgnet.TLRPC$TL_availableReaction r10 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r10
            if (r10 == 0) goto L_0x05ce
            java.lang.String r11 = "_"
            r12 = r41
            r1 = 2
            if (r12 == r1) goto L_0x0426
            r1 = 1
            if (r12 != r1) goto L_0x03a4
            org.telegram.tgnet.TLRPC$Document r1 = r10.around_animation
            goto L_0x03a6
        L_0x03a4:
            org.telegram.tgnet.TLRPC$Document r1 = r10.effect_animation
        L_0x03a6:
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = uniqPrefix
            int r5 = r4 + 1
            uniqPrefix = r5
            r3.append(r4)
            r3.append(r11)
            org.telegram.messenger.MessageObject r4 = r36.getMessageObject()
            int r4 = r4.getId()
            r3.append(r4)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            r2.setUniqKeyPrefix(r3)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = r22
            r1.append(r3)
            r1.append(r11)
            r1.append(r3)
            java.lang.String r3 = "_pcache"
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r0
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            r14 = 0
            r1.setAutoRepeat(r14)
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            r1.setAllowStartAnimation(r14)
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            if (r1 == 0) goto L_0x0424
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r14, r14)
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x0424:
            r1 = 2
            goto L_0x0427
        L_0x0426:
            r14 = 0
        L_0x0427:
            if (r12 != r1) goto L_0x0476
            org.telegram.tgnet.TLRPC$Document r1 = r10.appear_animation
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = uniqPrefix
            int r5 = r4 + 1
            uniqPrefix = r5
            r3.append(r4)
            r3.append(r11)
            org.telegram.messenger.MessageObject r4 = r36.getMessageObject()
            int r4 = r4.getId()
            r3.append(r4)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            r2.setUniqKeyPrefix(r3)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = r20
            r1.append(r3)
            r1.append(r11)
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r8
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x04c4
        L_0x0476:
            r3 = r20
            if (r12 != 0) goto L_0x04c4
            org.telegram.tgnet.TLRPC$Document r1 = r10.activate_animation
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = uniqPrefix
            int r6 = r5 + 1
            uniqPrefix = r6
            r4.append(r5)
            r4.append(r11)
            org.telegram.messenger.MessageObject r5 = r36.getMessageObject()
            int r5 = r5.getId()
            r4.append(r5)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            r2.setUniqKeyPrefix(r4)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r11)
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r8
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
        L_0x04c4:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAutoRepeat(r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAllowStartAnimation(r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            if (r1 == 0) goto L_0x050f
            r1 = 2
            if (r12 != r1) goto L_0x04f9
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            int r2 = r2.getFramesCount()
            r3 = 1
            int r2 = r2 - r3
            r1.setCurrentFrame(r2, r14)
            goto L_0x050f
        L_0x04f9:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r14, r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x050f:
            r2 = r37
            r1 = r31
            int r5 = r1 - r2
            int r3 = r5 >> 1
            r4 = 1
            if (r12 != r4) goto L_0x051b
            r5 = r3
        L_0x051b:
            r4 = r19
            r4.addView(r8)
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            r6.width = r2
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            r6.height = r2
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r6.topMargin = r3
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r6.leftMargin = r5
            r6 = 1
            if (r12 == r6) goto L_0x0556
            org.telegram.messenger.ImageReceiver r23 = r9.getImageReceiver()
            org.telegram.tgnet.TLRPC$Document r6 = r10.center_icon
            org.telegram.messenger.ImageLocation r24 = org.telegram.messenger.ImageLocation.getForDocument(r6)
            r26 = 0
            r29 = 1
            java.lang.String r25 = "40_40_lastframe"
            java.lang.String r27 = "webp"
            r28 = r10
            r23.setImage(r24, r25, r26, r27, r28, r29)
        L_0x0556:
            r4.addView(r9)
            android.view.ViewGroup$LayoutParams r6 = r9.getLayoutParams()
            r6.width = r2
            android.view.ViewGroup$LayoutParams r6 = r9.getLayoutParams()
            r6.height = r2
            android.view.ViewGroup$LayoutParams r2 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.topMargin = r3
            android.view.ViewGroup$LayoutParams r2 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.leftMargin = r5
            android.widget.FrameLayout r2 = r13.windowView
            r2.addView(r4)
            android.view.ViewGroup$LayoutParams r2 = r4.getLayoutParams()
            r2.width = r1
            android.view.ViewGroup$LayoutParams r2 = r4.getLayoutParams()
            r2.height = r1
            android.view.ViewGroup$LayoutParams r2 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r6 = -r3
            r2.topMargin = r6
            android.view.ViewGroup$LayoutParams r2 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r7 = -r5
            r2.leftMargin = r7
            android.widget.FrameLayout r2 = r13.windowView
            r2.addView(r0)
            android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
            r2.width = r1
            android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
            r2.height = r1
            android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
            r2.width = r1
            android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
            r2.height = r1
            android.view.ViewGroup$LayoutParams r1 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r1.topMargin = r6
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r0.leftMargin = r7
            float r0 = (float) r5
            r4.setPivotX(r0)
            float r0 = (float) r3
            r4.setPivotY(r0)
            goto L_0x05d1
        L_0x05ce:
            r0 = 1
            r13.dismissed = r0
        L_0x05d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, float, float, java.lang.String, int, int):void");
    }

    /* access modifiers changed from: private */
    public void removeCurrentView() {
        try {
            if (this.useWindow) {
                this.windowManager.removeView(this.windowView);
            } else {
                this.decorView.removeView(this.windowView);
            }
        } catch (Exception unused) {
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i, int i2) {
        ActionBarPopupWindow actionBarPopupWindow;
        BaseFragment baseFragment2 = baseFragment;
        int i3 = i2;
        if (chatMessageCell != null && str != null && baseFragment2 != null && baseFragment.getParentActivity() != null) {
            boolean z = true;
            if (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
                if (i3 == 2 || i3 == 0) {
                    show(baseFragment, (ReactionsContainerLayout) null, chatMessageCell, 0.0f, 0.0f, str, i, 1);
                }
                ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsContainerLayout, chatMessageCell, f, f2, str, i, i2);
                if (i3 == 1) {
                    currentShortOverlay = reactionsEffectOverlay;
                } else {
                    currentOverlay = reactionsEffectOverlay;
                }
                if (!(baseFragment2 instanceof ChatActivity) || (actionBarPopupWindow = ((ChatActivity) baseFragment2).scrimPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
                    z = false;
                }
                reactionsEffectOverlay.useWindow = z;
                if (z) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.height = -1;
                    layoutParams.width = -1;
                    layoutParams.type = 1000;
                    layoutParams.flags = 65816;
                    layoutParams.format = -3;
                    WindowManager windowManager2 = baseFragment.getParentActivity().getWindowManager();
                    reactionsEffectOverlay.windowManager = windowManager2;
                    windowManager2.addView(reactionsEffectOverlay.windowView, layoutParams);
                } else {
                    FrameLayout frameLayout = (FrameLayout) baseFragment.getParentActivity().getWindow().getDecorView();
                    reactionsEffectOverlay.decorView = frameLayout;
                    frameLayout.addView(reactionsEffectOverlay.windowView);
                }
                chatMessageCell.invalidate();
                if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getParent() != null) {
                    ((View) chatMessageCell.getParent()).invalidate();
                }
            }
        }
    }

    public static void startAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType == 0 && System.currentTimeMillis() - lastHapticTime > 200) {
                lastHapticTime = System.currentTimeMillis();
                currentOverlay.cell.performHapticFeedback(3);
                return;
            }
            return;
        }
        startShortAnimation();
        ReactionsEffectOverlay reactionsEffectOverlay2 = currentShortOverlay;
        if (reactionsEffectOverlay2 != null) {
            reactionsEffectOverlay2.cell.reactionsLayoutInBubble.animateReaction(reactionsEffectOverlay2.reaction);
        }
    }

    public static void startShortAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentShortOverlay;
        if (reactionsEffectOverlay != null && !reactionsEffectOverlay.started) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType == 1 && System.currentTimeMillis() - lastHapticTime > 200) {
                lastHapticTime = System.currentTimeMillis();
                currentShortOverlay.cell.performHapticFeedback(3);
            }
        }
    }

    public static void removeCurrent(boolean z) {
        int i = 0;
        while (i < 2) {
            ReactionsEffectOverlay reactionsEffectOverlay = i == 0 ? currentOverlay : currentShortOverlay;
            if (reactionsEffectOverlay != null) {
                if (z) {
                    reactionsEffectOverlay.removeCurrentView();
                } else {
                    reactionsEffectOverlay.dismissed = true;
                }
            }
            i++;
        }
        currentShortOverlay = null;
        currentOverlay = null;
    }

    public static boolean isPlaying(int i, long j, String str) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null) {
            return false;
        }
        int i2 = reactionsEffectOverlay.animationType;
        if (i2 != 2 && i2 != 0) {
            return false;
        }
        long j2 = reactionsEffectOverlay.groupId;
        if (((j2 == 0 || j != j2) && i != reactionsEffectOverlay.messageId) || !reactionsEffectOverlay.reaction.equals(str)) {
            return false;
        }
        return true;
    }

    private class AnimationView extends BackupImageView {
        boolean wasPlaying;

        public AnimationView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (getImageReceiver().getLottieAnimation() != null && getImageReceiver().getLottieAnimation().isRunning()) {
                this.wasPlaying = true;
            }
            if (!this.wasPlaying && getImageReceiver().getLottieAnimation() != null && !getImageReceiver().getLottieAnimation().isRunning()) {
                if (ReactionsEffectOverlay.this.animationType == 2) {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                } else {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    getImageReceiver().getLottieAnimation().start();
                }
            }
            super.onDraw(canvas);
        }
    }

    public static void onScrolled(int i) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= (float) i;
            if (i != 0) {
                reactionsEffectOverlay.wasScrolled = true;
            }
        }
    }

    public static int sizeForBigReaction() {
        int dp = AndroidUtilities.dp(350.0f);
        Point point = AndroidUtilities.displaySize;
        return (int) (((float) Math.round(((float) Math.min(dp, Math.min(point.x, point.y))) * 0.7f)) / AndroidUtilities.density);
    }

    public static int sizeForAroundReaction() {
        return (int) ((((float) AndroidUtilities.dp(80.0f)) * 2.0f) / AndroidUtilities.density);
    }

    private class AvatarParticle {
        float currentRotation;
        float fromX;
        float fromY;
        float globalTranslationY;
        ImageReceiver imageReceiver;
        boolean incrementRotation;
        float jumpY;
        public int leftTime;
        float outProgress;
        float progress;
        float randomRotation;
        float randomScale;
        float toX;
        float toY;

        private AvatarParticle(ReactionsEffectOverlay reactionsEffectOverlay) {
        }
    }
}
