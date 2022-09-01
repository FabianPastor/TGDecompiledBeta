package org.telegram.ui.Components.Reactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
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
    /* access modifiers changed from: private */
    public final ReactionsLayoutInBubble.VisibleReaction reaction;
    long startTime;
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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0345  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0401  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ReactionsEffectOverlay(android.content.Context r37, org.telegram.ui.ActionBar.BaseFragment r38, org.telegram.ui.Components.ReactionsContainerLayout r39, org.telegram.ui.Cells.ChatMessageCell r40, android.view.View r41, float r42, float r43, org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction r44, int r45, int r46) {
        /*
            r36 = this;
            r13 = r36
            r14 = r37
            r3 = r38
            r0 = r39
            r15 = r40
            r1 = r41
            r12 = r44
            r11 = r45
            r10 = r46
            r36.<init>()
            r9 = 2
            int[] r2 = new int[r9]
            r13.loc = r2
            r8 = 0
            r13.holderView = r8
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r13.avatars = r2
            org.telegram.messenger.MessageObject r2 = r40.getMessageObject()
            int r2 = r2.getId()
            r13.messageId = r2
            org.telegram.messenger.MessageObject r2 = r40.getMessageObject()
            long r4 = r2.getGroupId()
            r13.groupId = r4
            r13.reaction = r12
            r13.animationType = r10
            r13.cell = r15
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$ReactionButton r2 = r15.getReactionButton(r12)
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x004b
            r4 = r3
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            r5 = r4
            goto L_0x004c
        L_0x004b:
            r5 = r8
        L_0x004c:
            if (r0 == 0) goto L_0x0082
            r4 = 0
        L_0x004f:
            org.telegram.ui.Components.RecyclerListView r6 = r0.recyclerListView
            int r6 = r6.getChildCount()
            if (r4 >= r6) goto L_0x0082
            org.telegram.ui.Components.RecyclerListView r6 = r0.recyclerListView
            android.view.View r6 = r6.getChildAt(r4)
            boolean r6 = r6 instanceof org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView
            if (r6 == 0) goto L_0x007e
            org.telegram.ui.Components.RecyclerListView r6 = r0.recyclerListView
            android.view.View r6 = r6.getChildAt(r4)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r6 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r6
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r6 = r6.currentReaction
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r9 = r13.reaction
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x007e
            org.telegram.ui.Components.RecyclerListView r0 = r0.recyclerListView
            android.view.View r0 = r0.getChildAt(r4)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r0 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r0
            r13.holderView = r0
            goto L_0x0082
        L_0x007e:
            int r4 = r4 + 1
            r9 = 2
            goto L_0x004f
        L_0x0082:
            r17 = 0
            r9 = 1
            if (r10 != r9) goto L_0x024f
            java.util.Random r6 = new java.util.Random
            r6.<init>()
            org.telegram.messenger.MessageObject r9 = r40.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r9 = r9.reactions
            if (r9 == 0) goto L_0x00a1
            org.telegram.messenger.MessageObject r9 = r40.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r9 = r9.reactions
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessagePeerReaction> r9 = r9.recent_reactions
            goto L_0x00a2
        L_0x00a1:
            r9 = r8
        L_0x00a2:
            if (r9 == 0) goto L_0x024f
            if (r5 == 0) goto L_0x024f
            long r19 = r5.getDialogId()
            int r21 = (r19 > r17 ? 1 : (r19 == r17 ? 0 : -1))
            if (r21 >= 0) goto L_0x024f
            r7 = 0
        L_0x00af:
            int r4 = r9.size()
            if (r7 >= r4) goto L_0x024f
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r4 = r13.reaction
            java.lang.Object r21 = r9.get(r7)
            r0 = r21
            org.telegram.tgnet.TLRPC$MessagePeerReaction r0 = (org.telegram.tgnet.TLRPC$MessagePeerReaction) r0
            org.telegram.tgnet.TLRPC$Reaction r0 = r0.reaction
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0240
            java.lang.Object r0 = r9.get(r7)
            org.telegram.tgnet.TLRPC$MessagePeerReaction r0 = (org.telegram.tgnet.TLRPC$MessagePeerReaction) r0
            boolean r0 = r0.unread
            if (r0 == 0) goto L_0x0240
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            org.telegram.messenger.ImageReceiver r4 = new org.telegram.messenger.ImageReceiver
            r4.<init>()
            java.lang.Object r21 = r9.get(r7)
            r8 = r21
            org.telegram.tgnet.TLRPC$MessagePeerReaction r8 = (org.telegram.tgnet.TLRPC$MessagePeerReaction) r8
            org.telegram.tgnet.TLRPC$Peer r8 = r8.peer_id
            r21 = r9
            long r8 = org.telegram.messenger.MessageObject.getPeerId(r8)
            int r24 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1))
            if (r24 >= 0) goto L_0x0106
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r45)
            long r8 = -r8
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r8)
            if (r3 != 0) goto L_0x00ff
            goto L_0x0114
        L_0x00ff:
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            r4.setForUserOrChat(r3, r0)
            goto L_0x011e
        L_0x0106:
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r45)
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            if (r3 != 0) goto L_0x0118
        L_0x0114:
            r31 = r6
            goto L_0x0244
        L_0x0118:
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            r4.setForUserOrChat(r3, r0)
        L_0x011e:
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle
            r8 = 0
            r0.<init>()
            r0.imageReceiver = r4
            r3 = 1056964608(0x3var_, float:0.5)
            r0.fromX = r3
            r0.fromY = r3
            r3 = 1050253722(0x3e99999a, float:0.3)
            int r4 = r6.nextInt()
            int r4 = r4 % 100
            int r4 = java.lang.Math.abs(r4)
            float r4 = (float) r4
            r9 = 1120403456(0x42CLASSNAME, float:100.0)
            float r4 = r4 / r9
            r23 = 1036831949(0x3dcccccd, float:0.1)
            float r4 = r4 * r23
            float r4 = r4 + r3
            r0.jumpY = r4
            int r3 = r6.nextInt()
            int r3 = r3 % 100
            int r3 = java.lang.Math.abs(r3)
            float r3 = (float) r3
            float r3 = r3 / r9
            r4 = 1053609165(0x3ecccccd, float:0.4)
            float r3 = r3 * r4
            r22 = 1061997773(0x3f4ccccd, float:0.8)
            float r3 = r3 + r22
            r0.randomScale = r3
            int r3 = r6.nextInt()
            int r3 = r3 % 100
            int r3 = java.lang.Math.abs(r3)
            int r3 = r3 * 60
            float r3 = (float) r3
            float r3 = r3 / r9
            r0.randomRotation = r3
            r3 = 1137180672(0x43CLASSNAME, float:400.0)
            int r23 = r6.nextInt()
            int r23 = r23 % 100
            int r8 = java.lang.Math.abs(r23)
            float r8 = (float) r8
            float r8 = r8 / r9
            r23 = 1128792064(0x43480000, float:200.0)
            float r8 = r8 * r23
            float r8 = r8 + r3
            int r3 = (int) r8
            r0.leftTime = r3
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r3 = r13.avatars
            boolean r3 = r3.isEmpty()
            r8 = 1058642330(0x3var_a, float:0.6)
            r23 = 1045220557(0x3e4ccccd, float:0.2)
            if (r3 == 0) goto L_0x01b7
            int r3 = r6.nextInt()
            int r3 = r3 % 100
            int r3 = java.lang.Math.abs(r3)
            float r3 = (float) r3
            float r3 = r3 * r8
            float r3 = r3 / r9
            float r3 = r3 + r23
            r0.toX = r3
            int r3 = r6.nextInt()
            int r3 = r3 % 100
            int r3 = java.lang.Math.abs(r3)
            float r3 = (float) r3
            float r3 = r3 * r4
            float r3 = r3 / r9
            r0.toY = r3
            r31 = r6
            goto L_0x023a
        L_0x01b7:
            r3 = 0
            r25 = 0
            r26 = 0
            r27 = 0
        L_0x01be:
            r4 = 10
            if (r3 >= r4) goto L_0x0230
            int r4 = r6.nextInt()
            int r4 = r4 % 100
            int r4 = java.lang.Math.abs(r4)
            float r4 = (float) r4
            float r4 = r4 * r8
            float r4 = r4 / r9
            float r4 = r4 + r23
            int r29 = r6.nextInt()
            int r29 = r29 % 100
            int r8 = java.lang.Math.abs(r29)
            float r8 = (float) r8
            r28 = 1053609165(0x3ecccccd, float:0.4)
            float r8 = r8 * r28
            float r8 = r8 / r9
            float r8 = r8 + r23
            r29 = 1325400064(0x4var_, float:2.14748365E9)
            r31 = r6
            r9 = 0
        L_0x01ea:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r6 = r13.avatars
            int r6 = r6.size()
            if (r9 >= r6) goto L_0x021a
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r6 = r13.avatars
            java.lang.Object r6 = r6.get(r9)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r6 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r6
            float r6 = r6.toX
            float r6 = r6 - r4
            r32 = r4
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r4 = r13.avatars
            java.lang.Object r4 = r4.get(r9)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r4 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r4
            float r4 = r4.toY
            float r4 = r4 - r8
            float r6 = r6 * r6
            float r4 = r4 * r4
            float r6 = r6 + r4
            int r4 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r4 >= 0) goto L_0x0215
            r29 = r6
        L_0x0215:
            int r9 = r9 + 1
            r4 = r32
            goto L_0x01ea
        L_0x021a:
            r32 = r4
            int r4 = (r29 > r27 ? 1 : (r29 == r27 ? 0 : -1))
            if (r4 <= 0) goto L_0x0226
            r26 = r8
            r27 = r29
            r25 = r32
        L_0x0226:
            int r3 = r3 + 1
            r6 = r31
            r8 = 1058642330(0x3var_a, float:0.6)
            r9 = 1120403456(0x42CLASSNAME, float:100.0)
            goto L_0x01be
        L_0x0230:
            r31 = r6
            r3 = r25
            r0.toX = r3
            r3 = r26
            r0.toY = r3
        L_0x023a:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r3 = r13.avatars
            r3.add(r0)
            goto L_0x0244
        L_0x0240:
            r31 = r6
            r21 = r9
        L_0x0244:
            int r7 = r7 + 1
            r3 = r38
            r9 = r21
            r6 = r31
            r8 = 0
            goto L_0x00af
        L_0x024f:
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r0 = r13.holderView
            if (r0 != 0) goto L_0x025f
            r3 = 0
            int r4 = (r42 > r3 ? 1 : (r42 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x025d
            int r4 = (r43 > r3 ? 1 : (r43 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x025d
            goto L_0x025f
        L_0x025d:
            r8 = 0
            goto L_0x0260
        L_0x025f:
            r8 = 1
        L_0x0260:
            r3 = 1073741824(0x40000000, float:2.0)
            if (r1 == 0) goto L_0x02af
            int[] r0 = r13.loc
            r1.getLocationOnScreen(r0)
            int[] r0 = r13.loc
            r2 = 0
            r4 = r0[r2]
            float r2 = (float) r4
            r4 = 1
            r0 = r0[r4]
            float r0 = (float) r0
            int r4 = r41.getWidth()
            float r4 = (float) r4
            float r6 = r41.getScaleX()
            float r4 = r4 * r6
            boolean r6 = r1 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji
            if (r6 == 0) goto L_0x02a9
            r6 = r1
            org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r6 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r6
            float r6 = r6.bigReactionSelectedProgress
            r7 = 0
            int r9 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x02a9
            r4 = 1065353216(0x3var_, float:1.0)
            float r6 = r6 * r3
            float r6 = r6 + r4
            int r4 = r41.getWidth()
            float r4 = (float) r4
            float r4 = r4 * r6
            int r6 = r41.getWidth()
            float r6 = (float) r6
            float r6 = r4 - r6
            float r6 = r6 / r3
            float r2 = r2 - r6
            int r1 = r41.getWidth()
            float r1 = (float) r1
            float r1 = r4 - r1
            float r0 = r0 - r1
        L_0x02a9:
            r19 = r0
            r16 = r2
            r2 = r4
            goto L_0x02e9
        L_0x02af:
            if (r0 == 0) goto L_0x02ec
            int[] r1 = r13.loc
            r0.getLocationOnScreen(r1)
            int[] r0 = r13.loc
            r1 = 0
            r0 = r0[r1]
            float r0 = (float) r0
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r1 = r13.holderView
            org.telegram.ui.Components.BackupImageView r1 = r1.enterImageView
            float r1 = r1.getX()
            float r0 = r0 + r1
            int[] r1 = r13.loc
            r2 = 1
            r1 = r1[r2]
            float r1 = (float) r1
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r13.holderView
            org.telegram.ui.Components.BackupImageView r2 = r2.enterImageView
            float r2 = r2.getY()
            float r1 = r1 + r2
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r13.holderView
            org.telegram.ui.Components.BackupImageView r2 = r2.enterImageView
            int r2 = r2.getWidth()
            float r2 = (float) r2
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r4 = r13.holderView
            float r4 = r4.getScaleX()
            float r2 = r2 * r4
        L_0x02e5:
            r16 = r0
            r19 = r1
        L_0x02e9:
            r7 = 0
        L_0x02ea:
            r9 = 2
            goto L_0x0343
        L_0x02ec:
            if (r2 == 0) goto L_0x0324
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
            r4 = 1
            r1 = r1[r4]
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r4 = r15.reactionsLayoutInBubble
            int r4 = r4.y
            int r1 = r1 + r4
            int r4 = r2.y
            int r1 = r1 + r4
            float r1 = (float) r1
            org.telegram.messenger.ImageReceiver r4 = r2.imageReceiver
            float r4 = r4.getImageY()
            float r1 = r1 + r4
            org.telegram.messenger.ImageReceiver r2 = r2.imageReceiver
            float r2 = r2.getImageHeight()
            goto L_0x02e5
        L_0x0324:
            android.view.ViewParent r0 = r40.getParent()
            android.view.View r0 = (android.view.View) r0
            int[] r1 = r13.loc
            r0.getLocationInWindow(r1)
            int[] r0 = r13.loc
            r7 = 0
            r1 = r0[r7]
            float r1 = (float) r1
            float r1 = r1 + r42
            r2 = 1
            r0 = r0[r2]
            float r0 = (float) r0
            float r0 = r0 + r43
            r19 = r0
            r16 = r1
            r2 = 0
            goto L_0x02ea
        L_0x0343:
            if (r10 != r9) goto L_0x0356
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r1 = (float) r0
            float r1 = r1 * r3
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 / r3
            int r1 = (int) r1
            r4 = r0
            r3 = r1
            r6 = 1
            goto L_0x0388
        L_0x0356:
            r6 = 1
            if (r10 != r6) goto L_0x0364
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r1 = sizeForAroundReaction()
            goto L_0x0386
        L_0x0364:
            r0 = 1135542272(0x43avar_, float:350.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r1.x
            int r1 = r1.y
            int r1 = java.lang.Math.min(r3, r1)
            int r0 = java.lang.Math.min(r0, r1)
            float r0 = (float) r0
            r1 = 1061997773(0x3f4ccccd, float:0.8)
            float r0 = r0 * r1
            int r0 = java.lang.Math.round(r0)
            int r1 = sizeForBigReaction()
        L_0x0386:
            r4 = r0
            r3 = r1
        L_0x0388:
            int r1 = r4 >> 1
            int r0 = r3 >> 1
            float r6 = (float) r1
            float r21 = r2 / r6
            r2 = 0
            r13.animateInProgress = r2
            r13.animateOutProgress = r2
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r14)
            r13.container = r6
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1 r2 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1
            r15 = r0
            r0 = r2
            r41 = r1
            r1 = r36
            r20 = r15
            r15 = r2
            r2 = r37
            r33 = r3
            r3 = r38
            r34 = r4
            r4 = r40
            r35 = r6
            r22 = 1
            r6 = r41
            r7 = r46
            r23 = 0
            r9 = r21
            r10 = r16
            r11 = r19
            r12 = r44
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r13.windowView = r15
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r0.<init>(r14)
            r13.effectImageView = r0
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r8 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r8.<init>(r14)
            r13.emojiImageView = r8
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r9 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r9.<init>(r14)
            r13.emojiStaticImageView = r9
            r1 = r44
            java.lang.String r2 = r1.emojicon
            if (r2 == 0) goto L_0x03f6
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r45)
            java.util.HashMap r2 = r2.getReactionsMap()
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r3 = r13.reaction
            java.lang.String r3 = r3.emojicon
            java.lang.Object r2 = r2.get(r3)
            org.telegram.tgnet.TLRPC$TL_availableReaction r2 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r2
            r10 = r2
            goto L_0x03f8
        L_0x03f6:
            r10 = r23
        L_0x03f8:
            if (r10 != 0) goto L_0x0406
            long r2 = r1.documentId
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 == 0) goto L_0x0401
            goto L_0x0406
        L_0x0401:
            r11 = 1
            r13.dismissed = r11
            goto L_0x0683
        L_0x0406:
            r11 = 1
            if (r10 == 0) goto L_0x053f
            java.lang.String r1 = "_"
            r12 = r46
            r14 = 2
            if (r12 == r14) goto L_0x049f
            if (r12 != r11) goto L_0x0415
            org.telegram.tgnet.TLRPC$Document r2 = r10.around_animation
            goto L_0x0417
        L_0x0415:
            org.telegram.tgnet.TLRPC$Document r2 = r10.effect_animation
        L_0x0417:
            if (r12 != r11) goto L_0x041e
            java.lang.String r3 = getFilterForAroundAnimation()
            goto L_0x0432
        L_0x041e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = r33
            r3.append(r4)
            r3.append(r1)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
        L_0x0432:
            r26 = r3
            org.telegram.messenger.ImageReceiver r3 = r0.getImageReceiver()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = uniqPrefix
            int r6 = r5 + 1
            uniqPrefix = r6
            r4.append(r5)
            r4.append(r1)
            org.telegram.messenger.MessageObject r5 = r40.getMessageObject()
            int r5 = r5.getId()
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r3.setUniqKeyPrefix(r4)
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r24 = r0
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (int) r29, (java.lang.Object) r30)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            r15 = 0
            r2.setAutoRepeat(r15)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            r2.setAllowStartAnimation(r15)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            if (r2 == 0) goto L_0x04a0
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            r2.setCurrentFrame(r15, r15)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            r2.start()
            goto L_0x04a0
        L_0x049f:
            r15 = 0
        L_0x04a0:
            if (r12 != r14) goto L_0x04f0
            org.telegram.tgnet.TLRPC$Document r2 = r10.appear_animation
            org.telegram.messenger.ImageReceiver r3 = r8.getImageReceiver()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = uniqPrefix
            int r6 = r5 + 1
            uniqPrefix = r6
            r4.append(r5)
            r4.append(r1)
            org.telegram.messenger.MessageObject r5 = r40.getMessageObject()
            int r5 = r5.getId()
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r3.setUniqKeyPrefix(r4)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = r20
            r3.append(r4)
            r3.append(r1)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r8
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x057c
        L_0x04f0:
            r4 = r20
            if (r12 != 0) goto L_0x057c
            org.telegram.tgnet.TLRPC$Document r2 = r10.activate_animation
            org.telegram.messenger.ImageReceiver r3 = r8.getImageReceiver()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r6 = uniqPrefix
            int r7 = r6 + 1
            uniqPrefix = r7
            r5.append(r6)
            r5.append(r1)
            org.telegram.messenger.MessageObject r6 = r40.getMessageObject()
            int r6 = r6.getId()
            r5.append(r6)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            r3.setUniqKeyPrefix(r5)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            r3.append(r1)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r8
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x057c
        L_0x053f:
            r12 = r46
            r14 = 2
            r15 = 0
            if (r12 != 0) goto L_0x0552
            org.telegram.ui.Components.AnimatedEmojiDrawable r2 = new org.telegram.ui.Components.AnimatedEmojiDrawable
            long r3 = r1.documentId
            r5 = r45
            r2.<init>((int) r11, (int) r5, (long) r3)
            r8.setAnimatedReactionDrawable(r2)
            goto L_0x0560
        L_0x0552:
            r5 = r45
            if (r12 != r14) goto L_0x0560
            org.telegram.ui.Components.AnimatedEmojiDrawable r2 = new org.telegram.ui.Components.AnimatedEmojiDrawable
            long r3 = r1.documentId
            r2.<init>((int) r14, (int) r5, (long) r3)
            r8.setAnimatedReactionDrawable(r2)
        L_0x0560:
            if (r12 == 0) goto L_0x0564
            if (r12 != r11) goto L_0x057c
        L_0x0564:
            org.telegram.ui.Components.AnimatedEmojiDrawable r2 = new org.telegram.ui.Components.AnimatedEmojiDrawable
            long r3 = r1.documentId
            r2.<init>((int) r14, (int) r5, (long) r3)
            if (r12 != 0) goto L_0x056f
            r7 = 1
            goto L_0x0570
        L_0x056f:
            r7 = 0
        L_0x0570:
            org.telegram.ui.Components.Reactions.AnimatedEmojiEffect r1 = org.telegram.ui.Components.Reactions.AnimatedEmojiEffect.createFrom(r2, r7, r11)
            r0.setAnimatedEmojiEffect(r1)
            android.widget.FrameLayout r1 = r13.windowView
            r1.setClipChildren(r15)
        L_0x057c:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAutoRepeat(r15)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAllowStartAnimation(r15)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            if (r1 == 0) goto L_0x05c5
            if (r12 != r14) goto L_0x05af
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            int r2 = r2.getFramesCount()
            int r2 = r2 - r11
            r1.setCurrentFrame(r2, r15)
            goto L_0x05c5
        L_0x05af:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r15, r15)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x05c5:
            r2 = r41
            r1 = r34
            int r4 = r1 - r2
            int r3 = r4 >> 1
            if (r12 != r11) goto L_0x05d0
            r4 = r3
        L_0x05d0:
            r5 = r35
            r5.addView(r8)
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            r6.width = r2
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            r6.height = r2
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r6.topMargin = r3
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            r6.leftMargin = r4
            if (r12 == r11) goto L_0x062b
            if (r10 == 0) goto L_0x060c
            org.telegram.messenger.ImageReceiver r21 = r9.getImageReceiver()
            org.telegram.tgnet.TLRPC$Document r6 = r10.center_icon
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForDocument(r6)
            r24 = 0
            r27 = 1
            java.lang.String r23 = "40_40_lastframe"
            java.lang.String r25 = "webp"
            r26 = r10
            r21.setImage(r22, r23, r24, r25, r26, r27)
        L_0x060c:
            r5.addView(r9)
            android.view.ViewGroup$LayoutParams r6 = r9.getLayoutParams()
            r6.width = r2
            android.view.ViewGroup$LayoutParams r6 = r9.getLayoutParams()
            r6.height = r2
            android.view.ViewGroup$LayoutParams r2 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.topMargin = r3
            android.view.ViewGroup$LayoutParams r2 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.leftMargin = r4
        L_0x062b:
            android.widget.FrameLayout r2 = r13.windowView
            r2.addView(r5)
            android.view.ViewGroup$LayoutParams r2 = r5.getLayoutParams()
            r2.width = r1
            android.view.ViewGroup$LayoutParams r2 = r5.getLayoutParams()
            r2.height = r1
            android.view.ViewGroup$LayoutParams r2 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r6 = -r3
            r2.topMargin = r6
            android.view.ViewGroup$LayoutParams r2 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r7 = -r4
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
            float r0 = (float) r4
            r5.setPivotX(r0)
            float r0 = (float) r3
            r5.setPivotY(r0)
        L_0x0683:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, android.view.View, float, float, org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, int, int):void");
    }

    public static String getFilterForAroundAnimation() {
        return sizeForAroundReaction() + "_" + sizeForAroundReaction() + "_nolimit_pcache";
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

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0063, code lost:
        if (r12 != 2) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006d, code lost:
        if (r1.isShowing() == false) goto L_0x0070;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void show(org.telegram.ui.ActionBar.BaseFragment r16, org.telegram.ui.Components.ReactionsContainerLayout r17, org.telegram.ui.Cells.ChatMessageCell r18, android.view.View r19, float r20, float r21, org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction r22, int r23, int r24) {
        /*
            r11 = r16
            r12 = r24
            if (r18 == 0) goto L_0x00c8
            if (r22 == 0) goto L_0x00c8
            if (r11 == 0) goto L_0x00c8
            android.app.Activity r0 = r16.getParentActivity()
            if (r0 != 0) goto L_0x0012
            goto L_0x00c8
        L_0x0012:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "view_animations"
            r13 = 1
            boolean r0 = r0.getBoolean(r1, r13)
            if (r0 != 0) goto L_0x0020
            return
        L_0x0020:
            r14 = 2
            if (r12 == r14) goto L_0x0025
            if (r12 != 0) goto L_0x0036
        L_0x0025:
            r1 = 0
            r4 = 0
            r5 = 0
            r8 = 1
            r0 = r16
            r2 = r18
            r3 = r19
            r6 = r22
            r7 = r23
            show(r0, r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x0036:
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r15 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay
            android.app.Activity r1 = r16.getParentActivity()
            r0 = r15
            r2 = r16
            r3 = r17
            r4 = r18
            r5 = r19
            r6 = r20
            r7 = r21
            r8 = r22
            r9 = r23
            r10 = r24
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            if (r12 != r13) goto L_0x0057
            currentShortOverlay = r15
            goto L_0x0059
        L_0x0057:
            currentOverlay = r15
        L_0x0059:
            r0 = 0
            boolean r1 = r11 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x0070
            r1 = r11
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            if (r12 == 0) goto L_0x0065
            if (r12 != r14) goto L_0x0070
        L_0x0065:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r1.scrimPopupWindow
            if (r1 == 0) goto L_0x0070
            boolean r1 = r1.isShowing()
            if (r1 == 0) goto L_0x0070
            goto L_0x0071
        L_0x0070:
            r13 = 0
        L_0x0071:
            r15.useWindow = r13
            if (r13 == 0) goto L_0x009b
            android.view.WindowManager$LayoutParams r0 = new android.view.WindowManager$LayoutParams
            r0.<init>()
            r1 = -1
            r0.height = r1
            r0.width = r1
            r1 = 1000(0x3e8, float:1.401E-42)
            r0.type = r1
            r1 = 65816(0x10118, float:9.2228E-41)
            r0.flags = r1
            r1 = -3
            r0.format = r1
            android.app.Activity r1 = r16.getParentActivity()
            android.view.WindowManager r1 = r1.getWindowManager()
            r15.windowManager = r1
            android.widget.FrameLayout r2 = r15.windowView
            r1.addView(r2, r0)
            goto L_0x00b0
        L_0x009b:
            android.app.Activity r0 = r16.getParentActivity()
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            r15.decorView = r0
            android.widget.FrameLayout r1 = r15.windowView
            r0.addView(r1)
        L_0x00b0:
            r18.invalidate()
            org.telegram.messenger.MessageObject$GroupedMessages r0 = r18.getCurrentMessagesGroup()
            if (r0 == 0) goto L_0x00c8
            android.view.ViewParent r0 = r18.getParent()
            if (r0 == 0) goto L_0x00c8
            android.view.ViewParent r0 = r18.getParent()
            android.view.View r0 = (android.view.View) r0
            r0.invalidate()
        L_0x00c8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.show(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, android.view.View, float, float, org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, int, int):void");
    }

    public static void startAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.started = true;
            reactionsEffectOverlay.startTime = System.currentTimeMillis();
            if (currentOverlay.animationType == 0 && System.currentTimeMillis() - lastHapticTime > 200) {
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
            reactionsEffectOverlay.startTime = System.currentTimeMillis();
            if (currentShortOverlay.animationType == 1 && System.currentTimeMillis() - lastHapticTime > 200) {
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

    public static boolean isPlaying(int i, long j, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null) {
            return false;
        }
        int i2 = reactionsEffectOverlay.animationType;
        if (i2 != 2 && i2 != 0) {
            return false;
        }
        long j2 = reactionsEffectOverlay.groupId;
        if (((j2 == 0 || j != j2) && i != reactionsEffectOverlay.messageId) || !reactionsEffectOverlay.reaction.equals(visibleReaction)) {
            return false;
        }
        return true;
    }

    private class AnimationView extends BackupImageView {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        boolean attached;
        AnimatedEmojiEffect emojiEffect;
        boolean wasPlaying;

        public AnimationView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.animatedEmojiDrawable.setAlpha(255);
                this.animatedEmojiDrawable.draw(canvas);
                this.wasPlaying = true;
                return;
            }
            AnimatedEmojiEffect animatedEmojiEffect = this.emojiEffect;
            if (animatedEmojiEffect != null) {
                animatedEmojiEffect.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.emojiEffect.draw(canvas);
                this.wasPlaying = true;
                return;
            }
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

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.addView((View) this);
            }
            AnimatedEmojiEffect animatedEmojiEffect = this.emojiEffect;
            if (animatedEmojiEffect != null) {
                animatedEmojiEffect.setView(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView((Drawable.Callback) this);
            }
            AnimatedEmojiEffect animatedEmojiEffect = this.emojiEffect;
            if (animatedEmojiEffect != null) {
                animatedEmojiEffect.removeView(this);
            }
        }

        public void setAnimatedReactionDrawable(AnimatedEmojiDrawable animatedEmojiDrawable2) {
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView((Drawable.Callback) this);
            }
            this.animatedEmojiDrawable = animatedEmojiDrawable2;
            if (this.attached && animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.addView((View) this);
            }
        }

        public void setAnimatedEmojiEffect(AnimatedEmojiEffect animatedEmojiEffect) {
            this.emojiEffect = animatedEmojiEffect;
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
        return (int) ((((float) AndroidUtilities.dp(40.0f)) * 2.0f) / AndroidUtilities.density);
    }

    public static void dismissAll() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.dismissed = true;
        }
        ReactionsEffectOverlay reactionsEffectOverlay2 = currentShortOverlay;
        if (reactionsEffectOverlay2 != null) {
            reactionsEffectOverlay2.dismissed = true;
        }
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
