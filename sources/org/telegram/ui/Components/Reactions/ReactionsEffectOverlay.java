package org.telegram.ui.Components.Reactions;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ReactionsContainerLayout;

public class ReactionsEffectOverlay {
    public static ReactionsEffectOverlay currentOverlay;
    private static int unicPrefix;
    float animateInProgress;
    float animateOutProgress;
    /* access modifiers changed from: private */
    public final FrameLayout container;
    /* access modifiers changed from: private */
    public float dismissProgress;
    /* access modifiers changed from: private */
    public boolean dismissed;
    private final AnimationView effectImageView;
    /* access modifiers changed from: private */
    public final AnimationView emojiImageView;
    private final long groupId;
    /* access modifiers changed from: private */
    public float lastDrawnToX;
    /* access modifiers changed from: private */
    public float lastDrawnToY;
    int[] loc = new int[2];
    /* access modifiers changed from: private */
    public final int messageId;
    private final String reaction;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    FrameLayout windowView;

    static /* synthetic */ float access$116(ReactionsEffectOverlay reactionsEffectOverlay, float f) {
        float f2 = reactionsEffectOverlay.dismissProgress + f;
        reactionsEffectOverlay.dismissProgress = f2;
        return f2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ReactionsEffectOverlay(android.content.Context r27, org.telegram.ui.ActionBar.BaseFragment r28, org.telegram.ui.Components.ReactionsContainerLayout r29, org.telegram.ui.Cells.ChatMessageCell r30, float r31, float r32, java.lang.String r33, int r34) {
        /*
            r26 = this;
            r11 = r26
            r12 = r27
            r0 = r29
            r13 = r30
            r14 = r33
            r26.<init>()
            r1 = 2
            int[] r1 = new int[r1]
            r11.loc = r1
            org.telegram.messenger.MessageObject r1 = r30.getMessageObject()
            int r1 = r1.getId()
            r11.messageId = r1
            org.telegram.messenger.MessageObject r1 = r30.getMessageObject()
            long r1 = r1.getGroupId()
            r11.groupId = r1
            r11.reaction = r14
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$ReactionButton r1 = r13.getReactionButton(r14)
            r15 = 0
            if (r0 == 0) goto L_0x0056
            r2 = 0
        L_0x0030:
            org.telegram.ui.Components.RecyclerListView r3 = r0.recyclerListView
            int r3 = r3.getChildCount()
            if (r2 >= r3) goto L_0x0056
            org.telegram.ui.Components.RecyclerListView r3 = r0.recyclerListView
            android.view.View r3 = r3.getChildAt(r2)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r3 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r3
            org.telegram.tgnet.TLRPC$TL_availableReaction r3 = r3.currentReaction
            java.lang.String r3 = r3.reaction
            boolean r3 = r3.equals(r14)
            if (r3 == 0) goto L_0x0053
            org.telegram.ui.Components.RecyclerListView r3 = r0.recyclerListView
            android.view.View r2 = r3.getChildAt(r2)
            org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r2
            goto L_0x0057
        L_0x0053:
            int r2 = r2 + 1
            goto L_0x0030
        L_0x0056:
            r2 = 0
        L_0x0057:
            r3 = 1
            if (r2 == 0) goto L_0x005c
            r8 = 1
            goto L_0x005d
        L_0x005c:
            r8 = 0
        L_0x005d:
            r4 = 0
            if (r2 == 0) goto L_0x0091
            int[] r1 = r11.loc
            r0.getLocationOnScreen(r1)
            int[] r0 = r11.loc
            r0 = r0[r15]
            float r0 = (float) r0
            float r1 = r2.getX()
            float r0 = r0 + r1
            org.telegram.ui.Components.BackupImageView r1 = r2.backupImageView
            float r1 = r1.getX()
            float r0 = r0 + r1
            int[] r1 = r11.loc
            r1 = r1[r3]
            float r1 = (float) r1
            float r3 = r2.getY()
            float r1 = r1 + r3
            org.telegram.ui.Components.BackupImageView r3 = r2.backupImageView
            float r3 = r3.getY()
            float r1 = r1 + r3
            org.telegram.ui.Components.BackupImageView r2 = r2.backupImageView
            int r2 = r2.getWidth()
            float r2 = (float) r2
            r9 = r0
            r10 = r1
            goto L_0x00e9
        L_0x0091:
            if (r1 == 0) goto L_0x00cf
            int[] r0 = r11.loc
            r13.getLocationInWindow(r0)
            int[] r0 = r11.loc
            r0 = r0[r15]
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r2 = r13.reactionsLayoutInBubble
            int r2 = r2.x
            int r0 = r0 + r2
            int r2 = r1.x
            int r0 = r0 + r2
            float r0 = (float) r0
            org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
            float r2 = r2.getImageX()
            float r0 = r0 + r2
            int[] r2 = r11.loc
            r2 = r2[r3]
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r3 = r13.reactionsLayoutInBubble
            int r3 = r3.y
            int r2 = r2 + r3
            int r3 = r1.y
            int r2 = r2 + r3
            float r2 = (float) r2
            org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
            float r3 = r3.getImageY()
            float r2 = r2 + r3
            org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
            float r3 = r3.getImageHeight()
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.getImageWidth()
            r9 = r0
            r10 = r2
            r2 = r3
            goto L_0x00e9
        L_0x00cf:
            android.view.ViewParent r0 = r30.getParent()
            android.view.View r0 = (android.view.View) r0
            int[] r1 = r11.loc
            r0.getLocationInWindow(r1)
            int[] r0 = r11.loc
            r1 = r0[r15]
            float r1 = (float) r1
            float r1 = r1 + r31
            r0 = r0[r3]
            float r0 = (float) r0
            float r0 = r0 + r32
            r10 = r0
            r9 = r1
            r2 = 0
        L_0x00e9:
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
            int r7 = java.lang.Math.round(r0)
            r0 = 1073741824(0x40000000, float:2.0)
            float r1 = (float) r7
            float r1 = r1 * r0
            float r0 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 / r0
            int r6 = (int) r1
            int r5 = r7 >> 1
            int r3 = r6 >> 1
            float r0 = (float) r5
            float r16 = r2 / r0
            r11.animateInProgress = r4
            r11.animateOutProgress = r4
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r12)
            r11.container = r4
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1 r2 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1
            r0 = r2
            r1 = r26
            r15 = r2
            r2 = r27
            r13 = r3
            r3 = r28
            r17 = r4
            r4 = r30
            r28 = r5
            r5 = r33
            r29 = r13
            r13 = r6
            r6 = r28
            r18 = r7
            r7 = r16
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r11.windowView = r15
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r0 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r0.<init>(r11, r12)
            r11.effectImageView = r0
            org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r8 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView
            r8.<init>(r11, r12)
            r11.emojiImageView = r8
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r34)
            java.util.HashMap r1 = r1.getReactionsMap()
            java.lang.Object r1 = r1.get(r14)
            org.telegram.tgnet.TLRPC$TL_availableReaction r1 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r1
            if (r1 == 0) goto L_0x02c4
            org.telegram.tgnet.TLRPC$Document r2 = r1.effect_animation
            org.telegram.messenger.ImageReceiver r3 = r0.getImageReceiver()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = unicPrefix
            int r6 = r5 + 1
            unicPrefix = r6
            r4.append(r5)
            java.lang.String r5 = "_"
            r4.append(r5)
            org.telegram.messenger.MessageObject r6 = r30.getMessageObject()
            int r6 = r6.getId()
            r4.append(r6)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.setUniqKeyPrefix(r4)
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            r2.append(r5)
            r2.append(r13)
            java.lang.String r3 = "_pcache"
            r2.append(r3)
            java.lang.String r21 = r2.toString()
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r19 = r0
            r19.setImage((org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (org.telegram.messenger.ImageLocation) r22, (java.lang.String) r23, (int) r24, (java.lang.Object) r25)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            r3 = 0
            r2.setAutoRepeat(r3)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            r2.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            if (r2 == 0) goto L_0x01e6
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            r2.setCurrentFrame(r3, r3)
            org.telegram.messenger.ImageReceiver r2 = r0.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
            r2.start()
        L_0x01e6:
            org.telegram.tgnet.TLRPC$Document r1 = r1.activate_animation
            org.telegram.messenger.ImageReceiver r2 = r8.getImageReceiver()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = unicPrefix
            int r6 = r4 + 1
            unicPrefix = r6
            r3.append(r4)
            r3.append(r5)
            org.telegram.messenger.MessageObject r4 = r30.getMessageObject()
            int r4 = r4.getId()
            r3.append(r4)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r2.setUniqKeyPrefix(r3)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = r29
            r1.append(r3)
            r1.append(r5)
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r1 = r8
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r2 = 0
            r1.setAutoRepeat(r2)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            r1.setAllowStartAnimation(r2)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            if (r1 == 0) goto L_0x0261
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.setCurrentFrame(r2, r2)
            org.telegram.messenger.ImageReceiver r1 = r8.getImageReceiver()
            org.telegram.ui.Components.RLottieDrawable r1 = r1.getLottieAnimation()
            r1.start()
        L_0x0261:
            r2 = r28
            r1 = r18
            int r7 = r1 - r2
            int r3 = r7 >> 1
            r4 = r17
            r4.addView(r8)
            android.view.ViewGroup$LayoutParams r5 = r8.getLayoutParams()
            r5.width = r2
            android.view.ViewGroup$LayoutParams r5 = r8.getLayoutParams()
            r5.height = r2
            android.view.ViewGroup$LayoutParams r2 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.topMargin = r3
            android.view.ViewGroup$LayoutParams r2 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r2.leftMargin = r7
            r4.addView(r0)
            android.view.ViewGroup$LayoutParams r2 = r0.getLayoutParams()
            r2.width = r1
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r0.height = r1
            android.widget.FrameLayout r0 = r11.windowView
            r0.addView(r4)
            android.view.ViewGroup$LayoutParams r0 = r4.getLayoutParams()
            r0.width = r1
            android.view.ViewGroup$LayoutParams r0 = r4.getLayoutParams()
            r0.height = r1
            android.view.ViewGroup$LayoutParams r0 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r1 = -r3
            r0.topMargin = r1
            android.view.ViewGroup$LayoutParams r0 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r1 = -r7
            r0.leftMargin = r1
            float r0 = (float) r7
            r4.setPivotX(r0)
            float r0 = (float) r3
            r4.setPivotY(r0)
        L_0x02c4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, float, float, java.lang.String, int):void");
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i) {
        ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsContainerLayout, chatMessageCell, f, f2, str, i);
        currentOverlay = reactionsEffectOverlay;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.type = 1000;
        layoutParams.flags = 65816;
        layoutParams.format = -3;
        WindowManager windowManager2 = baseFragment.getParentActivity().getWindowManager();
        reactionsEffectOverlay.windowManager = windowManager2;
        windowManager2.addView(reactionsEffectOverlay.windowView, layoutParams);
        chatMessageCell.invalidate();
        if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getParent() != null) {
            ((View) chatMessageCell.getParent()).invalidate();
        }
    }

    public static void removeCurrent(boolean z) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            if (z) {
                try {
                    reactionsEffectOverlay.windowManager.removeView(reactionsEffectOverlay.windowView);
                } catch (Exception unused) {
                }
            } else {
                reactionsEffectOverlay.dismissed = true;
            }
        }
        currentOverlay = null;
    }

    public static boolean isPlaying(int i, long j, String str) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null) {
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

        public AnimationView(ReactionsEffectOverlay reactionsEffectOverlay, Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (getImageReceiver().getLottieAnimation() != null && getImageReceiver().getLottieAnimation().isRunning()) {
                this.wasPlaying = true;
            }
            if (!this.wasPlaying && getImageReceiver().getLottieAnimation() != null && !getImageReceiver().getLottieAnimation().isRunning()) {
                getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                getImageReceiver().getLottieAnimation().start();
            }
            super.onDraw(canvas);
        }
    }

    public static void onScrolled(int i) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= (float) i;
        }
    }
}
