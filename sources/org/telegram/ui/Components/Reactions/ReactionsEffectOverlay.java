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

    /* JADX WARNING: Removed duplicated region for block: B:109:0x05ae  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x02f7  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0377  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ReactionsEffectOverlay(android.content.Context r32, org.telegram.ui.ActionBar.BaseFragment r33, org.telegram.ui.Components.ReactionsContainerLayout r34, org.telegram.ui.Cells.ChatMessageCell r35, float r36, float r37, java.lang.String r38, int r39, int r40) {
        /*
            r31 = this;
            r13 = r31
            r14 = r32
            r3 = r33
            r0 = r34
            r15 = r35
            r12 = r38
            r11 = r40
            r31.<init>()
            r10 = 2
            int[] r1 = new int[r10]
            r13.loc = r1
            r1 = 0
            r13.holderView = r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r13.avatars = r2
            org.telegram.messenger.MessageObject r2 = r35.getMessageObject()
            int r2 = r2.getId()
            r13.messageId = r2
            org.telegram.messenger.MessageObject r2 = r35.getMessageObject()
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
            if (r11 != r8) goto L_0x023c
            java.util.Random r4 = new java.util.Random
            r4.<init>()
            org.telegram.messenger.MessageObject r5 = r35.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            if (r5 == 0) goto L_0x0090
            org.telegram.messenger.MessageObject r5 = r35.getMessageObject()
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messagePeerReaction> r5 = r5.recent_reactions
            goto L_0x0091
        L_0x0090:
            r5 = r1
        L_0x0091:
            if (r5 == 0) goto L_0x023c
            if (r6 == 0) goto L_0x023c
            long r16 = r6.getDialogId()
            r18 = 0
            int r7 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r7 >= 0) goto L_0x023c
            r7 = 0
        L_0x00a0:
            int r10 = r5.size()
            if (r7 >= r10) goto L_0x023c
            java.lang.Object r10 = r5.get(r7)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r10 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r10
            java.lang.String r10 = r10.reaction
            boolean r10 = r12.equals(r10)
            if (r10 == 0) goto L_0x022c
            java.lang.Object r10 = r5.get(r7)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r10 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r10
            boolean r10 = r10.unread
            if (r10 == 0) goto L_0x022c
            org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable
            r10.<init>()
            org.telegram.messenger.ImageReceiver r8 = new org.telegram.messenger.ImageReceiver
            r8.<init>()
            java.lang.Object r17 = r5.get(r7)
            r9 = r17
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r9 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r9
            org.telegram.tgnet.TLRPC$Peer r9 = r9.peer_id
            long r0 = org.telegram.messenger.MessageObject.getPeerId(r9)
            int r9 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r9 >= 0) goto L_0x00f2
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r39)
            long r0 = -r0
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r9.getChat(r0)
            if (r0 != 0) goto L_0x00eb
            goto L_0x022c
        L_0x00eb:
            r10.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            r8.setForUserOrChat(r0, r10)
            goto L_0x0108
        L_0x00f2:
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r39)
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r9.getUser(r0)
            if (r0 != 0) goto L_0x0102
            goto L_0x022c
        L_0x0102:
            r10.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            r8.setForUserOrChat(r0, r10)
        L_0x0108:
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle
            r1 = 0
            r0.<init>()
            r0.imageReceiver = r8
            r8 = 1056964608(0x3var_, float:0.5)
            r0.fromX = r8
            r0.fromY = r8
            r8 = 1050253722(0x3e99999a, float:0.3)
            int r9 = r4.nextInt()
            int r9 = r9 % 100
            int r9 = java.lang.Math.abs(r9)
            float r9 = (float) r9
            r10 = 1120403456(0x42CLASSNAME, float:100.0)
            float r9 = r9 / r10
            r17 = 1036831949(0x3dcccccd, float:0.1)
            float r9 = r9 * r17
            float r9 = r9 + r8
            r0.jumpY = r9
            r8 = 1061997773(0x3f4ccccd, float:0.8)
            int r9 = r4.nextInt()
            int r9 = r9 % 100
            int r9 = java.lang.Math.abs(r9)
            float r9 = (float) r9
            float r9 = r9 / r10
            r17 = 1053609165(0x3ecccccd, float:0.4)
            float r9 = r9 * r17
            float r9 = r9 + r8
            r0.randomScale = r9
            int r8 = r4.nextInt()
            int r8 = r8 % 100
            int r8 = java.lang.Math.abs(r8)
            int r8 = r8 * 60
            float r8 = (float) r8
            float r8 = r8 / r10
            r0.randomRotation = r8
            r8 = 1137180672(0x43CLASSNAME, float:400.0)
            int r9 = r4.nextInt()
            int r9 = r9 % 100
            int r9 = java.lang.Math.abs(r9)
            float r9 = (float) r9
            float r9 = r9 / r10
            r22 = 1128792064(0x43480000, float:200.0)
            float r9 = r9 * r22
            float r9 = r9 + r8
            int r8 = (int) r9
            r0.leftTime = r8
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r8 = r13.avatars
            boolean r8 = r8.isEmpty()
            r9 = 1058642330(0x3var_a, float:0.6)
            r22 = 1045220557(0x3e4ccccd, float:0.2)
            if (r8 == 0) goto L_0x01a2
            int r8 = r4.nextInt()
            int r8 = r8 % 100
            int r8 = java.lang.Math.abs(r8)
            float r8 = (float) r8
            float r8 = r8 * r9
            float r8 = r8 / r10
            float r8 = r8 + r22
            r0.toX = r8
            int r8 = r4.nextInt()
            int r8 = r8 % 100
            int r8 = java.lang.Math.abs(r8)
            float r8 = (float) r8
            float r8 = r8 * r17
            float r8 = r8 / r10
            r0.toY = r8
            r28 = r4
            r29 = r5
            goto L_0x0226
        L_0x01a2:
            r1 = 0
            r8 = 0
            r23 = 0
            r24 = 0
        L_0x01a8:
            r10 = 10
            if (r8 >= r10) goto L_0x021c
            int r10 = r4.nextInt()
            int r10 = r10 % 100
            int r10 = java.lang.Math.abs(r10)
            float r10 = (float) r10
            float r10 = r10 * r9
            r25 = 1120403456(0x42CLASSNAME, float:100.0)
            float r10 = r10 / r25
            float r10 = r10 + r22
            int r26 = r4.nextInt()
            int r26 = r26 % 100
            int r9 = java.lang.Math.abs(r26)
            float r9 = (float) r9
            float r9 = r9 * r17
            float r9 = r9 / r25
            float r9 = r9 + r22
            r26 = 1325400064(0x4var_, float:2.14748365E9)
            r28 = r4
            r3 = 0
        L_0x01d5:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r4 = r13.avatars
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0205
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r4 = r13.avatars
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r4 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r4
            float r4 = r4.toX
            float r4 = r4 - r10
            r29 = r5
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r5 = r13.avatars
            java.lang.Object r5 = r5.get(r3)
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r5 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r5
            float r5 = r5.toY
            float r5 = r5 - r9
            float r4 = r4 * r4
            float r5 = r5 * r5
            float r4 = r4 + r5
            int r5 = (r4 > r26 ? 1 : (r4 == r26 ? 0 : -1))
            if (r5 >= 0) goto L_0x0200
            r26 = r4
        L_0x0200:
            int r3 = r3 + 1
            r5 = r29
            goto L_0x01d5
        L_0x0205:
            r29 = r5
            int r3 = (r26 > r24 ? 1 : (r26 == r24 ? 0 : -1))
            if (r3 <= 0) goto L_0x0210
            r23 = r9
            r1 = r10
            r24 = r26
        L_0x0210:
            int r8 = r8 + 1
            r3 = r33
            r4 = r28
            r5 = r29
            r9 = 1058642330(0x3var_a, float:0.6)
            goto L_0x01a8
        L_0x021c:
            r28 = r4
            r29 = r5
            r0.toX = r1
            r1 = r23
            r0.toY = r1
        L_0x0226:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r1 = r13.avatars
            r1.add(r0)
            goto L_0x0230
        L_0x022c:
            r28 = r4
            r29 = r5
        L_0x0230:
            int r7 = r7 + 1
            r3 = r33
            r4 = r28
            r5 = r29
            r1 = 0
            r8 = 1
            goto L_0x00a0
        L_0x023c:
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r0 = r13.holderView
            if (r0 != 0) goto L_0x024c
            r1 = 0
            int r3 = (r36 > r1 ? 1 : (r36 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x024a
            int r3 = (r37 > r1 ? 1 : (r37 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x024a
            goto L_0x024c
        L_0x024a:
            r9 = 0
            goto L_0x024d
        L_0x024c:
            r9 = 1
        L_0x024d:
            if (r0 == 0) goto L_0x028c
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
        L_0x0288:
            r7 = 2
            r8 = 1
            r10 = 0
            goto L_0x02ed
        L_0x028c:
            if (r2 == 0) goto L_0x02ce
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
            goto L_0x0288
        L_0x02ce:
            android.view.ViewParent r0 = r35.getParent()
            android.view.View r0 = (android.view.View) r0
            int[] r1 = r13.loc
            r0.getLocationInWindow(r1)
            int[] r0 = r13.loc
            r10 = 0
            r1 = r0[r10]
            float r1 = (float) r1
            float r1 = r1 + r36
            r8 = 1
            r0 = r0[r8]
            float r0 = (float) r0
            float r0 = r0 + r37
            r17 = r0
            r16 = r1
            r1 = 0
            r7 = 2
        L_0x02ed:
            if (r11 != r7) goto L_0x02f7
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
        L_0x02f5:
            r5 = r0
            goto L_0x0305
        L_0x02f7:
            if (r11 != r8) goto L_0x0300
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x02f5
        L_0x0300:
            int r0 = sizeForBigReaction()
            goto L_0x02f5
        L_0x0305:
            r0 = 1073741824(0x40000000, float:2.0)
            float r2 = (float) r5
            float r2 = r2 * r0
            float r0 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 / r0
            int r4 = (int) r2
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
            r34 = r0
            r15 = r1
            r1 = r31
            r19 = r15
            r15 = r2
            r2 = r32
            r36 = r3
            r3 = r33
            r20 = r15
            r15 = r4
            r4 = r35
            r30 = r5
            r5 = r38
            r21 = 2
            r7 = r36
            r22 = r15
            r15 = 1
            r8 = r40
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
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r39)
            java.util.HashMap r1 = r1.getReactionsMap()
            java.lang.Object r1 = r1.get(r15)
            r10 = r1
            org.telegram.tgnet.TLRPC$TL_availableReaction r10 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r10
            if (r10 == 0) goto L_0x05ae
            java.lang.String r11 = "_"
            r12 = r40
            r1 = 2
            if (r12 == r1) goto L_0x0406
            r1 = 1
            if (r12 != r1) goto L_0x0384
            org.telegram.tgnet.TLRPC$Document r1 = r10.around_animation
            goto L_0x0386
        L_0x0384:
            org.telegram.tgnet.TLRPC$Document r1 = r10.effect_animation
        L_0x0386:
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = uniqPrefix
            int r5 = r4 + 1
            uniqPrefix = r5
            r3.append(r4)
            r3.append(r11)
            org.telegram.messenger.MessageObject r4 = r35.getMessageObject()
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
            if (r1 == 0) goto L_0x0404
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r14, r14)
            org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x0404:
            r1 = 2
            goto L_0x0407
        L_0x0406:
            r14 = 0
        L_0x0407:
            if (r12 != r1) goto L_0x0456
            org.telegram.tgnet.TLRPC$Document r1 = r10.appear_animation
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = uniqPrefix
            int r5 = r4 + 1
            uniqPrefix = r5
            r3.append(r4)
            r3.append(r11)
            org.telegram.messenger.MessageObject r4 = r35.getMessageObject()
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
            goto L_0x04a4
        L_0x0456:
            r3 = r20
            if (r12 != 0) goto L_0x04a4
            org.telegram.tgnet.TLRPC$Document r1 = r10.activate_animation
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = uniqPrefix
            int r6 = r5 + 1
            uniqPrefix = r6
            r4.append(r5)
            r4.append(r11)
            org.telegram.messenger.MessageObject r5 = r35.getMessageObject()
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
        L_0x04a4:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAutoRepeat(r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAllowStartAnimation(r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            if (r1 == 0) goto L_0x04ef
            r1 = 2
            if (r12 != r1) goto L_0x04d9
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            int r2 = r2.getFramesCount()
            r3 = 1
            int r2 = r2 - r3
            r1.setCurrentFrame(r2, r14)
            goto L_0x04ef
        L_0x04d9:
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r14, r14)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x04ef:
            r2 = r36
            r1 = r30
            int r5 = r1 - r2
            int r3 = r5 >> 1
            r4 = 1
            if (r12 != r4) goto L_0x04fb
            r5 = r3
        L_0x04fb:
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
            if (r12 == r6) goto L_0x0536
            org.telegram.messenger.ImageReceiver r23 = r9.getImageReceiver()
            org.telegram.tgnet.TLRPC$Document r6 = r10.static_icon
            org.telegram.messenger.ImageLocation r24 = org.telegram.messenger.ImageLocation.getForDocument(r6)
            r26 = 0
            r29 = 1
            java.lang.String r25 = "40_40"
            java.lang.String r27 = "webp"
            r28 = r10
            r23.setImage(r24, r25, r26, r27, r28, r29)
        L_0x0536:
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
            goto L_0x05b1
        L_0x05ae:
            r0 = 1
            r13.dismissed = r0
        L_0x05b1:
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
        return Math.round(((float) Math.min(dp, Math.min(point.x, point.y))) * 0.8f);
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
