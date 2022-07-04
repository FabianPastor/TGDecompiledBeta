package org.telegram.ui.Components.Reactions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;

public class ReactionsEffectOverlay {
    public static final int LONG_ANIMATION = 0;
    public static final int ONLY_MOVE_ANIMATION = 2;
    public static final int SHORT_ANIMATION = 1;
    public static ReactionsEffectOverlay currentOverlay;
    public static ReactionsEffectOverlay currentShortOverlay;
    private static long lastHapticTime;
    private static int uniqPrefix;
    boolean animateIn;
    float animateInProgress;
    float animateOutProgress;
    /* access modifiers changed from: private */
    public final int animationType;
    ArrayList<AvatarParticle> avatars;
    BackupImageView backupImageView;
    private ChatMessageCell cell;
    /* access modifiers changed from: private */
    public final FrameLayout container;
    private final int currentAccount;
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
    private final BaseFragment fragment;
    private final long groupId;
    /* access modifiers changed from: private */
    public ReactionsContainerLayout.ReactionHolderView holderView;
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

    static /* synthetic */ float access$216(ReactionsEffectOverlay x0, float x1) {
        float f = x0.dismissProgress + x1;
        x0.dismissProgress = f;
        return f;
    }

    private ReactionsEffectOverlay(Context context, BaseFragment fragment2, ReactionsContainerLayout reactionsLayout, ChatMessageCell cell2, float x, float y, String reaction2, int currentAccount2, int animationType2) {
        float fromHeight;
        float fromY;
        float fromX;
        int sizeForFilter;
        int size;
        int sizeForFilter2;
        int i;
        int i2;
        int leftOffset;
        ArrayList<TLRPC.TL_messagePeerReaction> recentReactions;
        Random random;
        ImageReceiver imageReceiver;
        Context context2 = context;
        BaseFragment baseFragment = fragment2;
        ReactionsContainerLayout reactionsContainerLayout = reactionsLayout;
        ChatMessageCell chatMessageCell = cell2;
        String str = reaction2;
        int i3 = animationType2;
        AnonymousClass1 r0 = null;
        this.holderView = null;
        this.avatars = new ArrayList<>();
        this.fragment = baseFragment;
        this.messageId = cell2.getMessageObject().getId();
        this.groupId = cell2.getMessageObject().getGroupId();
        this.reaction = str;
        this.animationType = i3;
        this.currentAccount = currentAccount2;
        this.cell = chatMessageCell;
        ReactionsLayoutInBubble.ReactionButton reactionButton = chatMessageCell.getReactionButton(str);
        ChatActivity chatActivity = baseFragment instanceof ChatActivity ? (ChatActivity) baseFragment : null;
        if (reactionsContainerLayout != null) {
            int i4 = 0;
            while (true) {
                if (i4 >= reactionsContainerLayout.recyclerListView.getChildCount()) {
                    break;
                } else if (((ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout.recyclerListView.getChildAt(i4)).currentReaction.reaction.equals(str)) {
                    this.holderView = (ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout.recyclerListView.getChildAt(i4);
                    break;
                } else {
                    i4++;
                }
            }
        }
        if (i3 == 1) {
            Random random2 = new Random();
            ArrayList<TLRPC.TL_messagePeerReaction> recentReactions2 = cell2.getMessageObject().messageOwner.reactions != null ? cell2.getMessageObject().messageOwner.reactions.recent_reactions : null;
            if (recentReactions2 == null || chatActivity == null || chatActivity.getDialogId() >= 0) {
                ArrayList<TLRPC.TL_messagePeerReaction> arrayList = recentReactions2;
            } else {
                int i5 = 0;
                while (i5 < recentReactions2.size()) {
                    if (!str.equals(recentReactions2.get(i5).reaction) || !recentReactions2.get(i5).unread) {
                        random = random2;
                        recentReactions = recentReactions2;
                    } else {
                        AvatarDrawable avatarDrawable = new AvatarDrawable();
                        ImageReceiver imageReceiver2 = new ImageReceiver();
                        random = random2;
                        long peerId = MessageObject.getPeerId(recentReactions2.get(i5).peer_id);
                        if (peerId < 0) {
                            TLRPC.Chat chat = MessagesController.getInstance(currentAccount2).getChat(Long.valueOf(-peerId));
                            if (chat == null) {
                                recentReactions = recentReactions2;
                            } else {
                                avatarDrawable.setInfo(chat);
                                imageReceiver = imageReceiver2;
                                imageReceiver.setForUserOrChat(chat, avatarDrawable);
                            }
                        } else {
                            imageReceiver = imageReceiver2;
                            TLRPC.User user = MessagesController.getInstance(currentAccount2).getUser(Long.valueOf(peerId));
                            if (user == null) {
                                recentReactions = recentReactions2;
                            } else {
                                avatarDrawable.setInfo(user);
                                imageReceiver.setForUserOrChat(user, avatarDrawable);
                            }
                        }
                        AvatarParticle avatarParticle = new AvatarParticle();
                        avatarParticle.imageReceiver = imageReceiver;
                        avatarParticle.fromX = 0.5f;
                        avatarParticle.fromY = 0.5f;
                        avatarParticle.jumpY = ((((float) Math.abs(random.nextInt() % 100)) / 100.0f) * 0.1f) + 0.3f;
                        avatarParticle.randomScale = ((((float) Math.abs(random.nextInt() % 100)) / 100.0f) * 0.4f) + 0.8f;
                        avatarParticle.randomRotation = ((float) (Math.abs(random.nextInt() % 100) * 60)) / 100.0f;
                        avatarParticle.leftTime = (int) (((((float) Math.abs(random.nextInt() % 100)) / 100.0f) * 200.0f) + 400.0f);
                        if (this.avatars.isEmpty()) {
                            avatarParticle.toX = ((((float) Math.abs(random.nextInt() % 100)) * 0.6f) / 100.0f) + 0.2f;
                            avatarParticle.toY = (((float) Math.abs(random.nextInt() % 100)) * 0.4f) / 100.0f;
                            long j = peerId;
                            recentReactions = recentReactions2;
                            AvatarDrawable avatarDrawable2 = avatarDrawable;
                        } else {
                            float bestDistance = 0.0f;
                            float bestX = 0.0f;
                            float bestY = 0.0f;
                            int k = 0;
                            while (true) {
                                long peerId2 = peerId;
                                if (k >= 10) {
                                    break;
                                }
                                float randX = ((((float) Math.abs(random.nextInt() % 100)) * 0.6f) / 100.0f) + 0.2f;
                                float randY = ((((float) Math.abs(random.nextInt() % 100)) * 0.4f) / 100.0f) + 0.2f;
                                float minDistance = 2.14748365E9f;
                                ArrayList<TLRPC.TL_messagePeerReaction> recentReactions3 = recentReactions2;
                                int j2 = 0;
                                while (j2 < this.avatars.size()) {
                                    float rx = this.avatars.get(j2).toX - randX;
                                    AvatarDrawable avatarDrawable3 = avatarDrawable;
                                    float ry = this.avatars.get(j2).toY - randY;
                                    float distance = (rx * rx) + (ry * ry);
                                    if (distance < minDistance) {
                                        minDistance = distance;
                                    }
                                    j2++;
                                    int i6 = currentAccount2;
                                    avatarDrawable = avatarDrawable3;
                                }
                                AvatarDrawable avatarDrawable4 = avatarDrawable;
                                if (minDistance > bestDistance) {
                                    bestDistance = minDistance;
                                    bestX = randX;
                                    bestY = randY;
                                }
                                k++;
                                int i7 = currentAccount2;
                                peerId = peerId2;
                                recentReactions2 = recentReactions3;
                                avatarDrawable = avatarDrawable4;
                            }
                            recentReactions = recentReactions2;
                            AvatarDrawable avatarDrawable5 = avatarDrawable;
                            avatarParticle.toX = bestX;
                            avatarParticle.toY = bestY;
                        }
                        this.avatars.add(avatarParticle);
                    }
                    i5++;
                    Context context3 = context;
                    BaseFragment baseFragment2 = fragment2;
                    int i8 = currentAccount2;
                    random2 = random;
                    recentReactions2 = recentReactions;
                    r0 = null;
                }
                ArrayList<TLRPC.TL_messagePeerReaction> arrayList2 = recentReactions2;
            }
        }
        ReactionsContainerLayout.ReactionHolderView reactionHolderView = this.holderView;
        final boolean fromHolder = (reactionHolderView == null && (x == 0.0f || y == 0.0f)) ? false : true;
        if (reactionHolderView != null) {
            reactionHolderView.getLocationOnScreen(this.loc);
            fromX = ((float) this.loc[0]) + this.holderView.backupImageView.getX();
            fromY = ((float) this.loc[1]) + this.holderView.backupImageView.getY();
            fromHeight = ((float) this.holderView.backupImageView.getWidth()) * this.holderView.getScaleX();
        } else if (reactionButton != null) {
            chatMessageCell.getLocationInWindow(this.loc);
            float fromX2 = ((float) (this.loc[0] + chatMessageCell.reactionsLayoutInBubble.x + reactionButton.x)) + reactionButton.imageReceiver.getImageX();
            float fromY2 = ((float) (this.loc[1] + chatMessageCell.reactionsLayoutInBubble.y + reactionButton.y)) + reactionButton.imageReceiver.getImageY();
            float fromHeight2 = reactionButton.imageReceiver.getImageHeight();
            float imageWidth = reactionButton.imageReceiver.getImageWidth();
            fromX = fromX2;
            fromY = fromY2;
            fromHeight = fromHeight2;
        } else {
            ((View) cell2.getParent()).getLocationInWindow(this.loc);
            int[] iArr = this.loc;
            fromY = ((float) iArr[1]) + y;
            fromX = ((float) iArr[0]) + x;
            fromHeight = 0.0f;
        }
        if (i3 == 2) {
            int size2 = AndroidUtilities.dp(34.0f);
            sizeForFilter = (int) ((((float) size2) * 2.0f) / AndroidUtilities.density);
            size = size2;
        } else if (i3 == 1) {
            int size3 = AndroidUtilities.dp(80.0f);
            sizeForFilter = (int) ((((float) size3) * 2.0f) / AndroidUtilities.density);
            size = size3;
        } else {
            int size4 = Math.round(((float) Math.min(AndroidUtilities.dp(350.0f), Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.8f);
            sizeForFilter = sizeForBigReaction();
            size = size4;
        }
        int emojiSize = size >> 1;
        this.animateInProgress = 0.0f;
        this.animateOutProgress = 0.0f;
        FrameLayout frameLayout = new FrameLayout(context);
        this.container = frameLayout;
        int emojiSizeForFilter = sizeForFilter >> 1;
        int emojiSize2 = emojiSize;
        int size5 = size;
        final BaseFragment baseFragment3 = fragment2;
        FrameLayout frameLayout2 = frameLayout;
        final ChatMessageCell chatMessageCell2 = cell2;
        ReactionsLayoutInBubble.ReactionButton reactionButton2 = reactionButton;
        final String str2 = reaction2;
        int sizeForFilter3 = sizeForFilter;
        final ChatActivity chatActivity2 = chatActivity;
        final int i9 = emojiSize2;
        final int i10 = animationType2;
        final float f = fromHeight / ((float) emojiSize);
        final float f2 = fromX;
        final float f3 = fromY;
        this.windowView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:156:0x0446  */
            /* JADX WARNING: Removed duplicated region for block: B:202:0x0691  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void dispatchDraw(android.graphics.Canvas r41) {
                /*
                    r40 = this;
                    r0 = r40
                    r1 = r41
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    boolean r2 = r2.dismissed
                    r3 = 1037726734(0x3dda740e, float:0.10666667)
                    r4 = 1065353216(0x3var_, float:1.0)
                    if (r2 == 0) goto L_0x0052
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.dismissProgress
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 == 0) goto L_0x0037
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.access$216(r2, r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.dismissProgress
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 <= 0) goto L_0x0037
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float unused = r2.dismissProgress = r4
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda0
                    r2.<init>(r0)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                L_0x0037:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.dismissProgress
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 == 0) goto L_0x004e
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.dismissProgress
                    float r4 = r4 - r2
                    r0.setAlpha(r4)
                    super.dispatchDraw(r41)
                L_0x004e:
                    r40.invalidate()
                    return
                L_0x0052:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    boolean r2 = r2.started
                    if (r2 != 0) goto L_0x005e
                    r40.invalidate()
                    return
                L_0x005e:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r2.holderView
                    r5 = 0
                    if (r2 == 0) goto L_0x007d
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r2.holderView
                    org.telegram.ui.Components.BackupImageView r2 = r2.backupImageView
                    r2.setAlpha(r5)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r2 = r2.holderView
                    org.telegram.ui.Components.BackupImageView r2 = r2.pressedBackupImageView
                    r2.setAlpha(r5)
                L_0x007d:
                    org.telegram.ui.ActionBar.BaseFragment r2 = r3
                    boolean r6 = r2 instanceof org.telegram.ui.ChatActivity
                    r7 = 0
                    if (r6 == 0) goto L_0x0091
                    org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r6 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int r6 = r6.messageId
                    org.telegram.ui.Cells.ChatMessageCell r2 = r2.findMessageCell(r6, r7)
                    goto L_0x0093
                L_0x0091:
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                L_0x0093:
                    org.telegram.ui.Cells.ChatMessageCell r6 = r4
                    org.telegram.messenger.MessageObject r6 = r6.getMessageObject()
                    boolean r6 = r6.shouldDrawReactionsInLayout()
                    r8 = 1101004800(0x41a00000, float:20.0)
                    if (r6 == 0) goto L_0x00a7
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    float r6 = (float) r6
                    goto L_0x00ae
                L_0x00a7:
                    r6 = 1096810496(0x41600000, float:14.0)
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    float r6 = (float) r6
                L_0x00ae:
                    r9 = 1073741824(0x40000000, float:2.0)
                    r10 = 1
                    if (r2 == 0) goto L_0x011a
                    org.telegram.ui.Cells.ChatMessageCell r11 = r4
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r12 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r12 = r12.loc
                    r11.getLocationInWindow(r12)
                    org.telegram.ui.Cells.ChatMessageCell r11 = r4
                    java.lang.String r12 = r5
                    org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$ReactionButton r11 = r11.getReactionButton(r12)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r12 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r12 = r12.loc
                    r12 = r12[r7]
                    org.telegram.ui.Cells.ChatMessageCell r13 = r4
                    org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r13 = r13.reactionsLayoutInBubble
                    int r13 = r13.x
                    int r12 = r12 + r13
                    float r12 = (float) r12
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r13 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r13 = r13.loc
                    r13 = r13[r10]
                    org.telegram.ui.Cells.ChatMessageCell r14 = r4
                    org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r14 = r14.reactionsLayoutInBubble
                    int r14 = r14.y
                    int r13 = r13 + r14
                    float r13 = (float) r13
                    if (r11 == 0) goto L_0x00f8
                    int r14 = r11.x
                    float r14 = (float) r14
                    org.telegram.messenger.ImageReceiver r15 = r11.imageReceiver
                    float r15 = r15.getImageX()
                    float r14 = r14 + r15
                    float r12 = r12 + r14
                    int r14 = r11.y
                    float r14 = (float) r14
                    org.telegram.messenger.ImageReceiver r15 = r11.imageReceiver
                    float r15 = r15.getImageY()
                    float r14 = r14 + r15
                    float r13 = r13 + r14
                L_0x00f8:
                    org.telegram.ui.ChatActivity r14 = r6
                    if (r14 == 0) goto L_0x00ff
                    float r14 = r14.drawingChatLisViewYoffset
                    float r13 = r13 + r14
                L_0x00ff:
                    boolean r14 = r2.drawPinnedBottom
                    if (r14 == 0) goto L_0x010f
                    boolean r14 = r2.shouldDrawTimeOnMedia()
                    if (r14 != 0) goto L_0x010f
                    int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    float r14 = (float) r14
                    float r13 = r13 + r14
                L_0x010f:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r14 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float unused = r14.lastDrawnToX = r12
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r14 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float unused = r14.lastDrawnToY = r13
                    goto L_0x0126
                L_0x011a:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r11 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r12 = r11.lastDrawnToX
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r11 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r13 = r11.lastDrawnToY
                L_0x0126:
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.app.Activity r11 = r11.getParentActivity()
                    if (r11 == 0) goto L_0x06a7
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.view.View r11 = r11.getFragmentView()
                    android.view.ViewParent r11 = r11.getParent()
                    if (r11 == 0) goto L_0x06a7
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.view.View r11 = r11.getFragmentView()
                    int r11 = r11.getVisibility()
                    if (r11 != 0) goto L_0x06a7
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.view.View r11 = r11.getFragmentView()
                    if (r11 == 0) goto L_0x06a7
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.view.View r11 = r11.getFragmentView()
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r14 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r14 = r14.loc
                    r11.getLocationOnScreen(r14)
                    org.telegram.ui.ActionBar.BaseFragment r11 = r3
                    android.view.View r11 = r11.getFragmentView()
                    android.view.ViewParent r11 = r11.getParent()
                    android.view.View r11 = (android.view.View) r11
                    float r11 = r11.getAlpha()
                    r0.setAlpha(r11)
                    int r11 = r7
                    float r14 = (float) r11
                    float r14 = r14 - r6
                    float r14 = r14 / r9
                    float r14 = r12 - r14
                    float r11 = (float) r11
                    float r11 = r11 - r6
                    float r11 = r11 / r9
                    float r11 = r13 - r11
                    int r15 = r8
                    if (r15 == r10) goto L_0x01b4
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r15 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r15 = r15.loc
                    r15 = r15[r7]
                    float r15 = (float) r15
                    int r15 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
                    if (r15 >= 0) goto L_0x0190
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r15 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r15 = r15.loc
                    r15 = r15[r7]
                    float r14 = (float) r15
                L_0x0190:
                    int r15 = r7
                    float r15 = (float) r15
                    float r15 = r15 + r14
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r8 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r8 = r8.loc
                    r8 = r8[r7]
                    int r17 = r40.getMeasuredWidth()
                    int r8 = r8 + r17
                    float r8 = (float) r8
                    int r8 = (r15 > r8 ? 1 : (r15 == r8 ? 0 : -1))
                    if (r8 <= 0) goto L_0x01b4
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r8 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    int[] r8 = r8.loc
                    r8 = r8[r7]
                    int r15 = r40.getMeasuredWidth()
                    int r8 = r8 + r15
                    int r15 = r7
                    int r8 = r8 - r15
                    float r14 = (float) r8
                L_0x01b4:
                    org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r15 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r15 = r15.animateOutProgress
                    float r8 = r8.getInterpolation(r15)
                    int r15 = r8
                    r7 = 2
                    if (r15 != r7) goto L_0x01d0
                    org.telegram.ui.Components.CubicBezierInterpolator r15 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
                    float r15 = r15.getInterpolation(r8)
                    org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                    float r9 = r9.getInterpolation(r8)
                    goto L_0x01ef
                L_0x01d0:
                    boolean r9 = r9
                    if (r9 == 0) goto L_0x01e9
                    org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r15 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r15 = r15.animateInProgress
                    float r15 = r9.getInterpolation(r15)
                    org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r3 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r3 = r3.animateInProgress
                    float r9 = r9.getInterpolation(r3)
                    goto L_0x01ef
                L_0x01e9:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r3 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r3 = r3.animateInProgress
                    r9 = r3
                    r15 = r3
                L_0x01ef:
                    float r3 = r4 - r15
                    float r5 = r10
                    float r3 = r3 * r5
                    float r3 = r3 + r15
                    int r5 = r7
                    float r5 = (float) r5
                    float r5 = r6 / r5
                    int r7 = r8
                    if (r7 != r10) goto L_0x0207
                    r7 = r14
                    r21 = r11
                    r3 = 1065353216(0x3var_, float:1.0)
                    r10 = r21
                    goto L_0x021b
                L_0x0207:
                    float r7 = r11
                    float r21 = r4 - r15
                    float r7 = r7 * r21
                    float r21 = r14 * r15
                    float r7 = r7 + r21
                    float r10 = r12
                    float r22 = r4 - r9
                    float r10 = r10 * r22
                    float r22 = r11 * r9
                    float r10 = r10 + r22
                L_0x021b:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r4 = r4.effectImageView
                    r4.setTranslationX(r7)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r4 = r4.effectImageView
                    r4.setTranslationY(r10)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r4 = r4.effectImageView
                    r23 = r2
                    r22 = 1065353216(0x3var_, float:1.0)
                    float r2 = r22 - r8
                    r4.setAlpha(r2)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    r2.setScaleX(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    r2.setScaleY(r3)
                    int r2 = r8
                    r4 = 2
                    if (r2 != r4) goto L_0x0274
                    float r4 = r10
                    r22 = 1065353216(0x3var_, float:1.0)
                    float r24 = r22 - r15
                    float r4 = r4 * r24
                    float r24 = r5 * r15
                    float r3 = r4 + r24
                    float r4 = r11
                    float r24 = r22 - r15
                    float r4 = r4 * r24
                    float r24 = r12 * r15
                    float r7 = r4 + r24
                    float r4 = r12
                    float r24 = r22 - r9
                    float r4 = r4 * r24
                    float r24 = r13 * r9
                    float r10 = r4 + r24
                    goto L_0x0293
                L_0x0274:
                    r22 = 1065353216(0x3var_, float:1.0)
                    r4 = 0
                    int r24 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r24 == 0) goto L_0x0293
                    float r4 = r22 - r8
                    float r4 = r4 * r3
                    float r24 = r5 * r8
                    float r3 = r4 + r24
                    float r4 = r22 - r8
                    float r4 = r4 * r7
                    float r24 = r12 * r8
                    float r7 = r4 + r24
                    float r4 = r22 - r8
                    float r4 = r4 * r10
                    float r24 = r13 * r8
                    float r10 = r4 + r24
                L_0x0293:
                    r4 = 1
                    if (r2 == r4) goto L_0x02af
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.emojiStaticImageView
                    r4 = 1060320051(0x3var_, float:0.7)
                    int r24 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r24 <= 0) goto L_0x02ab
                    float r25 = r8 - r4
                    r4 = 1050253722(0x3e99999a, float:0.3)
                    float r4 = r25 / r4
                    goto L_0x02ac
                L_0x02ab:
                    r4 = 0
                L_0x02ac:
                    r2.setAlpha(r4)
                L_0x02af:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    android.widget.FrameLayout r2 = r2.container
                    r2.setTranslationX(r7)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    android.widget.FrameLayout r2 = r2.container
                    r2.setTranslationY(r10)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    android.widget.FrameLayout r2 = r2.container
                    r2.setScaleX(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    android.widget.FrameLayout r2 = r2.container
                    r2.setScaleY(r3)
                    super.dispatchDraw(r41)
                    int r2 = r8
                    r4 = 1
                    if (r2 == r4) goto L_0x02e5
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.emojiImageView
                    boolean r2 = r2.wasPlaying
                    if (r2 == 0) goto L_0x0318
                L_0x02e5:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.animateInProgress
                    r4 = 1065353216(0x3var_, float:1.0)
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 == 0) goto L_0x0318
                    boolean r2 = r9
                    if (r2 == 0) goto L_0x02ff
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r4 = r2.animateInProgress
                    r25 = 1027292903(0x3d3b3ee7, float:0.NUM)
                    float r4 = r4 + r25
                    r2.animateInProgress = r4
                    goto L_0x030a
                L_0x02ff:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r4 = r2.animateInProgress
                    r26 = 1033171465(0x3d94var_, float:0.07272727)
                    float r4 = r4 + r26
                    r2.animateInProgress = r4
                L_0x030a:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.animateInProgress
                    r4 = 1065353216(0x3var_, float:1.0)
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 <= 0) goto L_0x0318
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    r2.animateInProgress = r4
                L_0x0318:
                    int r2 = r8
                    r4 = 2
                    if (r2 == r4) goto L_0x0398
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    boolean r2 = r2.wasScrolled
                    if (r2 == 0) goto L_0x0329
                    int r2 = r8
                    if (r2 == 0) goto L_0x0398
                L_0x0329:
                    int r2 = r8
                    r4 = 1
                    if (r2 == r4) goto L_0x035c
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.emojiImageView
                    boolean r2 = r2.wasPlaying
                    if (r2 == 0) goto L_0x035c
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.emojiImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    if (r2 == 0) goto L_0x035c
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.emojiImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    boolean r2 = r2.isRunning()
                    if (r2 == 0) goto L_0x0398
                L_0x035c:
                    int r2 = r8
                    r4 = 1
                    if (r2 != r4) goto L_0x0394
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    boolean r2 = r2.wasPlaying
                    if (r2 == 0) goto L_0x0390
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    if (r2 == 0) goto L_0x0390
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    boolean r2 = r2.isRunning()
                    if (r2 != 0) goto L_0x0390
                    goto L_0x0398
                L_0x0390:
                    r27 = r3
                    goto L_0x0432
                L_0x0394:
                    r27 = r3
                    goto L_0x0432
                L_0x0398:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.animateOutProgress
                    r4 = 1065353216(0x3var_, float:1.0)
                    int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r2 == 0) goto L_0x0430
                    int r2 = r8
                    r4 = 1
                    if (r2 != r4) goto L_0x03b0
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    r4 = 1065353216(0x3var_, float:1.0)
                    r2.animateOutProgress = r4
                    r27 = r3
                    goto L_0x03c6
                L_0x03b0:
                    r4 = 2
                    if (r2 != r4) goto L_0x03b6
                    r2 = 1135542272(0x43avar_, float:350.0)
                    goto L_0x03b8
                L_0x03b6:
                    r2 = 1130102784(0x435CLASSNAME, float:220.0)
                L_0x03b8:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    r27 = r3
                    float r3 = r4.animateOutProgress
                    r26 = 1098907648(0x41800000, float:16.0)
                    float r28 = r26 / r2
                    float r3 = r3 + r28
                    r4.animateOutProgress = r3
                L_0x03c6:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.animateOutProgress
                    r3 = 1060320051(0x3var_, float:0.7)
                    int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                    if (r2 <= 0) goto L_0x03dc
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    boolean r2 = r2.finished
                    if (r2 != 0) goto L_0x03dc
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.startShortAnimation()
                L_0x03dc:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    float r2 = r2.animateOutProgress
                    r3 = 1065353216(0x3var_, float:1.0)
                    int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                    if (r2 < 0) goto L_0x0432
                    int r2 = r8
                    if (r2 == 0) goto L_0x03ed
                    r3 = 2
                    if (r2 != r3) goto L_0x03f6
                L_0x03ed:
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                    org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble r2 = r2.reactionsLayoutInBubble
                    java.lang.String r3 = r5
                    r2.animateReaction(r3)
                L_0x03f6:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    r3 = 1065353216(0x3var_, float:1.0)
                    r2.animateOutProgress = r3
                    int r2 = r8
                    r3 = 0
                    r4 = 1
                    if (r2 != r4) goto L_0x0405
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.currentShortOverlay = r3
                    goto L_0x0407
                L_0x0405:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.currentOverlay = r3
                L_0x0407:
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                    r2.invalidate()
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                    org.telegram.messenger.MessageObject$GroupedMessages r2 = r2.getCurrentMessagesGroup()
                    if (r2 == 0) goto L_0x0427
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                    android.view.ViewParent r2 = r2.getParent()
                    if (r2 == 0) goto L_0x0427
                    org.telegram.ui.Cells.ChatMessageCell r2 = r4
                    android.view.ViewParent r2 = r2.getParent()
                    android.view.View r2 = (android.view.View) r2
                    r2.invalidate()
                L_0x0427:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda1
                    r2.<init>(r0)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                    goto L_0x0432
                L_0x0430:
                    r27 = r3
                L_0x0432:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r2 = r2.avatars
                    boolean r2 = r2.isEmpty()
                    if (r2 != 0) goto L_0x0691
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    boolean r2 = r2.wasPlaying
                    if (r2 == 0) goto L_0x0691
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    r3 = 0
                L_0x0455:
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r4 = r4.avatars
                    int r4 = r4.size()
                    if (r3 >= r4) goto L_0x067c
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r4 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r4 = r4.avatars
                    java.lang.Object r4 = r4.get(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r4 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r4
                    r20 = r5
                    float r5 = r4.progress
                    if (r2 == 0) goto L_0x04cb
                    boolean r24 = r2.isRunning()
                    if (r24 == 0) goto L_0x04cb
                    r24 = r2
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    r28 = r6
                    r29 = r7
                    long r6 = r2.getDuration()
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r2 = r2.getLottieAnimation()
                    int r2 = r2.getFramesCount()
                    r30 = r8
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r8 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r8 = r8.effectImageView
                    org.telegram.messenger.ImageReceiver r8 = r8.getImageReceiver()
                    org.telegram.ui.Components.RLottieDrawable r8 = r8.getLottieAnimation()
                    int r8 = r8.getCurrentFrame()
                    r31 = r9
                    float r9 = (float) r6
                    r32 = r10
                    float r10 = (float) r6
                    r33 = r6
                    float r6 = (float) r8
                    float r7 = (float) r2
                    float r6 = r6 / r7
                    float r10 = r10 * r6
                    float r9 = r9 - r10
                    int r6 = (int) r9
                    int r7 = r4.leftTime
                    if (r6 >= r7) goto L_0x04c8
                    r7 = 1
                    goto L_0x04c9
                L_0x04c8:
                    r7 = 0
                L_0x04c9:
                    r2 = r7
                    goto L_0x04d8
                L_0x04cb:
                    r24 = r2
                    r28 = r6
                    r29 = r7
                    r30 = r8
                    r31 = r9
                    r32 = r10
                    r2 = 1
                L_0x04d8:
                    if (r2 == 0) goto L_0x050d
                    float r6 = r4.outProgress
                    r7 = 1065353216(0x3var_, float:1.0)
                    int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                    if (r6 == 0) goto L_0x050d
                    float r6 = r4.outProgress
                    r8 = 1037726734(0x3dda740e, float:0.10666667)
                    float r6 = r6 + r8
                    r4.outProgress = r6
                    float r6 = r4.outProgress
                    int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                    if (r6 <= 0) goto L_0x0510
                    r4.outProgress = r7
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r6 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r6 = r6.avatars
                    r6.remove(r3)
                    int r3 = r3 + -1
                    r37 = r11
                    r38 = r12
                    r39 = r13
                    r7 = 1
                    r12 = 1027292903(0x3d3b3ee7, float:0.NUM)
                    r13 = 1101004800(0x41a00000, float:20.0)
                    r16 = 1098907648(0x41800000, float:16.0)
                    r18 = 1073741824(0x40000000, float:2.0)
                    goto L_0x0665
                L_0x050d:
                    r8 = 1037726734(0x3dda740e, float:0.10666667)
                L_0x0510:
                    r6 = 1056964608(0x3var_, float:0.5)
                    int r7 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                    if (r7 >= 0) goto L_0x051c
                    float r6 = r5 / r6
                    r7 = r6
                    r6 = 1065353216(0x3var_, float:1.0)
                    goto L_0x0523
                L_0x051c:
                    float r7 = r5 - r6
                    float r7 = r7 / r6
                    r6 = 1065353216(0x3var_, float:1.0)
                    float r7 = r6 - r7
                L_0x0523:
                    float r9 = r4.fromX
                    float r10 = r6 - r5
                    float r9 = r9 * r10
                    float r10 = r4.toX
                    float r10 = r10 * r5
                    float r9 = r9 + r10
                    float r10 = r4.fromY
                    float r19 = r6 - r5
                    float r10 = r10 * r19
                    float r6 = r4.toY
                    float r6 = r6 * r5
                    float r10 = r10 + r6
                    float r6 = r4.jumpY
                    float r6 = r6 * r7
                    float r10 = r10 - r6
                    float r6 = r4.randomScale
                    float r6 = r6 * r5
                    float r8 = r4.outProgress
                    r22 = 1065353216(0x3var_, float:1.0)
                    float r8 = r22 - r8
                    float r6 = r6 * r8
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r8 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r8 = r8.effectImageView
                    float r8 = r8.getX()
                    r33 = r2
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    int r2 = r2.getWidth()
                    float r2 = (float) r2
                    r34 = r7
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r7 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r7 = r7.effectImageView
                    float r7 = r7.getScaleX()
                    float r2 = r2 * r7
                    float r2 = r2 * r9
                    float r8 = r8 + r2
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r2 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r2 = r2.effectImageView
                    float r2 = r2.getY()
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r7 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r7 = r7.effectImageView
                    int r7 = r7.getHeight()
                    float r7 = (float) r7
                    r35 = r9
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r9 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AnimationView r9 = r9.effectImageView
                    float r9 = r9.getScaleY()
                    float r7 = r7 * r9
                    float r7 = r7 * r10
                    float r2 = r2 + r7
                    r7 = 1098907648(0x41800000, float:16.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r7 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r7 = r7.avatars
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r7 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r7
                    org.telegram.messenger.ImageReceiver r7 = r7.imageReceiver
                    r36 = r10
                    float r10 = (float) r9
                    r18 = 1073741824(0x40000000, float:2.0)
                    float r10 = r10 / r18
                    float r10 = r8 - r10
                    r37 = r11
                    float r11 = (float) r9
                    float r11 = r11 / r18
                    float r11 = r2 - r11
                    r38 = r12
                    float r12 = (float) r9
                    r39 = r13
                    float r13 = (float) r9
                    r7.setImageCoords(r10, r11, r12, r13)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r7 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r7 = r7.avatars
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r7 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r7
                    org.telegram.messenger.ImageReceiver r7 = r7.imageReceiver
                    int r10 = r9 >> 1
                    r7.setRoundRadius((int) r10)
                    r41.save()
                    float r7 = r4.globalTranslationY
                    r10 = 0
                    r1.translate(r10, r7)
                    r1.scale(r6, r6, r8, r2)
                    float r7 = r4.currentRotation
                    r1.rotate(r7, r8, r2)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay r7 = org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.this
                    java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle> r7 = r7.avatars
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$AvatarParticle r7 = (org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AvatarParticle) r7
                    org.telegram.messenger.ImageReceiver r7 = r7.imageReceiver
                    r7.draw(r1)
                    r41.restore()
                    float r7 = r4.progress
                    r11 = 1065353216(0x3var_, float:1.0)
                    int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                    if (r7 >= 0) goto L_0x0610
                    float r7 = r4.progress
                    r12 = 1027292903(0x3d3b3ee7, float:0.NUM)
                    float r7 = r7 + r12
                    r4.progress = r7
                    float r7 = r4.progress
                    int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                    if (r7 <= 0) goto L_0x0613
                    r4.progress = r11
                    goto L_0x0613
                L_0x0610:
                    r12 = 1027292903(0x3d3b3ee7, float:0.NUM)
                L_0x0613:
                    int r7 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
                    if (r7 < 0) goto L_0x062c
                    float r7 = r4.globalTranslationY
                    r13 = 1101004800(0x41a00000, float:20.0)
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                    float r10 = (float) r10
                    r16 = 1098907648(0x41800000, float:16.0)
                    float r10 = r10 * r16
                    r22 = 1140457472(0x43fa0000, float:500.0)
                    float r10 = r10 / r22
                    float r7 = r7 + r10
                    r4.globalTranslationY = r7
                    goto L_0x0630
                L_0x062c:
                    r13 = 1101004800(0x41a00000, float:20.0)
                    r16 = 1098907648(0x41800000, float:16.0)
                L_0x0630:
                    boolean r7 = r4.incrementRotation
                    r10 = 1132068864(0x437a0000, float:250.0)
                    if (r7 == 0) goto L_0x064e
                    float r7 = r4.currentRotation
                    float r11 = r4.randomRotation
                    float r11 = r11 / r10
                    float r7 = r7 + r11
                    r4.currentRotation = r7
                    float r7 = r4.currentRotation
                    float r10 = r4.randomRotation
                    int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
                    if (r7 <= 0) goto L_0x064b
                    r7 = 0
                    r4.incrementRotation = r7
                    r7 = 1
                    goto L_0x0665
                L_0x064b:
                    r7 = 0
                    r7 = 1
                    goto L_0x0665
                L_0x064e:
                    r7 = 0
                    float r11 = r4.currentRotation
                    float r7 = r4.randomRotation
                    float r7 = r7 / r10
                    float r11 = r11 - r7
                    r4.currentRotation = r11
                    float r7 = r4.currentRotation
                    float r10 = r4.randomRotation
                    float r10 = -r10
                    int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
                    if (r7 >= 0) goto L_0x0664
                    r7 = 1
                    r4.incrementRotation = r7
                    goto L_0x0665
                L_0x0664:
                    r7 = 1
                L_0x0665:
                    int r3 = r3 + r7
                    r5 = r20
                    r2 = r24
                    r6 = r28
                    r7 = r29
                    r8 = r30
                    r9 = r31
                    r10 = r32
                    r11 = r37
                    r12 = r38
                    r13 = r39
                    goto L_0x0455
                L_0x067c:
                    r24 = r2
                    r20 = r5
                    r28 = r6
                    r29 = r7
                    r30 = r8
                    r31 = r9
                    r32 = r10
                    r37 = r11
                    r38 = r12
                    r39 = r13
                    goto L_0x06a3
                L_0x0691:
                    r20 = r5
                    r28 = r6
                    r29 = r7
                    r30 = r8
                    r31 = r9
                    r32 = r10
                    r37 = r11
                    r38 = r12
                    r39 = r13
                L_0x06a3:
                    r40.invalidate()
                    return
                L_0x06a7:
                    r23 = r2
                    r28 = r6
                    r38 = r12
                    r39 = r13
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AnonymousClass1.dispatchDraw(android.graphics.Canvas):void");
            }

            /* renamed from: lambda$dispatchDraw$0$org-telegram-ui-Components-Reactions-ReactionsEffectOverlay$1  reason: not valid java name */
            public /* synthetic */ void m1294xd3d15369() {
                ReactionsEffectOverlay.this.removeCurrentView();
            }

            /* renamed from: lambda$dispatchDraw$1$org-telegram-ui-Components-Reactions-ReactionsEffectOverlay$1  reason: not valid java name */
            public /* synthetic */ void m1295xCLASSNAMEd7aa() {
                ReactionsEffectOverlay.this.removeCurrentView();
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                for (int i = 0; i < ReactionsEffectOverlay.this.avatars.size(); i++) {
                    ReactionsEffectOverlay.this.avatars.get(i).imageReceiver.onAttachedToWindow();
                }
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                for (int i = 0; i < ReactionsEffectOverlay.this.avatars.size(); i++) {
                    ReactionsEffectOverlay.this.avatars.get(i).imageReceiver.onDetachedFromWindow();
                }
            }
        };
        Context context4 = context;
        AnimationView animationView = new AnimationView(context4);
        this.effectImageView = animationView;
        AnimationView animationView2 = new AnimationView(context4);
        this.emojiImageView = animationView2;
        AnimationView animationView3 = new AnimationView(context4);
        this.emojiStaticImageView = animationView3;
        TLRPC.TL_availableReaction availableReaction = MediaDataController.getInstance(currentAccount2).getReactionsMap().get(reaction2);
        if (availableReaction != null) {
            int i11 = animationType2;
            if (i11 != 2) {
                TLRPC.Document document = i11 == 1 ? availableReaction.around_animation : availableReaction.effect_animation;
                ImageReceiver imageReceiver3 = animationView.getImageReceiver();
                StringBuilder sb = new StringBuilder();
                int i12 = uniqPrefix;
                uniqPrefix = i12 + 1;
                sb.append(i12);
                sb.append("_");
                sb.append(cell2.getMessageObject().getId());
                sb.append("_");
                imageReceiver3.setUniqKeyPrefix(sb.toString());
                ImageLocation forDocument = ImageLocation.getForDocument(document);
                StringBuilder sb2 = new StringBuilder();
                sizeForFilter2 = sizeForFilter3;
                sb2.append(sizeForFilter2);
                sb2.append("_");
                sb2.append(sizeForFilter2);
                sb2.append("_pcache");
                animationView.setImage(forDocument, sb2.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
                animationView.getImageReceiver().setAutoRepeat(0);
                animationView.getImageReceiver().setAllowStartAnimation(false);
                if (animationView.getImageReceiver().getLottieAnimation() != null) {
                    animationView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    animationView.getImageReceiver().getLottieAnimation().start();
                }
            } else {
                sizeForFilter2 = sizeForFilter3;
            }
            if (i11 == 2) {
                TLRPC.Document document2 = availableReaction.appear_animation;
                ImageReceiver imageReceiver4 = animationView2.getImageReceiver();
                StringBuilder sb3 = new StringBuilder();
                int i13 = uniqPrefix;
                uniqPrefix = i13 + 1;
                sb3.append(i13);
                sb3.append("_");
                sb3.append(cell2.getMessageObject().getId());
                sb3.append("_");
                imageReceiver4.setUniqKeyPrefix(sb3.toString());
                ImageLocation forDocument2 = ImageLocation.getForDocument(document2);
                StringBuilder sb4 = new StringBuilder();
                int emojiSizeForFilter2 = emojiSizeForFilter;
                sb4.append(emojiSizeForFilter2);
                sb4.append("_");
                sb4.append(emojiSizeForFilter2);
                TLRPC.Document document3 = document2;
                int i14 = sizeForFilter2;
                i = i11;
                animationView2.setImage(forDocument2, sb4.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
                int i15 = emojiSizeForFilter2;
            } else {
                i = i11;
                int emojiSizeForFilter3 = emojiSizeForFilter;
                if (i == 0) {
                    TLRPC.Document document4 = availableReaction.activate_animation;
                    ImageReceiver imageReceiver5 = animationView2.getImageReceiver();
                    StringBuilder sb5 = new StringBuilder();
                    int i16 = uniqPrefix;
                    uniqPrefix = i16 + 1;
                    sb5.append(i16);
                    sb5.append("_");
                    sb5.append(cell2.getMessageObject().getId());
                    sb5.append("_");
                    imageReceiver5.setUniqKeyPrefix(sb5.toString());
                    ImageLocation forDocument3 = ImageLocation.getForDocument(document4);
                    StringBuilder sb6 = new StringBuilder();
                    int emojiSizeForFilter4 = emojiSizeForFilter3;
                    sb6.append(emojiSizeForFilter4);
                    sb6.append("_");
                    sb6.append(emojiSizeForFilter4);
                    int i17 = emojiSizeForFilter4;
                    TLRPC.Document document5 = document4;
                    animationView2.setImage(forDocument3, sb6.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
                }
            }
            animationView2.getImageReceiver().setAutoRepeat(0);
            animationView2.getImageReceiver().setAllowStartAnimation(false);
            if (animationView2.getImageReceiver().getLottieAnimation() == null) {
                i2 = 1;
            } else if (i == 2) {
                i2 = 1;
                animationView2.getImageReceiver().getLottieAnimation().setCurrentFrame(animationView2.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
            } else {
                i2 = 1;
                animationView2.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                animationView2.getImageReceiver().getLottieAnimation().start();
            }
            int emojiSize3 = emojiSize2;
            int size6 = size5;
            int topOffset = (size6 - emojiSize3) >> i2;
            if (i == i2) {
                leftOffset = topOffset;
            } else {
                leftOffset = size6 - emojiSize3;
            }
            FrameLayout frameLayout3 = frameLayout2;
            frameLayout3.addView(animationView2);
            animationView2.getLayoutParams().width = emojiSize3;
            animationView2.getLayoutParams().height = emojiSize3;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).topMargin = topOffset;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).leftMargin = leftOffset;
            if (i != i2) {
                animationView3.getImageReceiver().setImage(ImageLocation.getForDocument(availableReaction.center_icon), "40_40_lastframe", (Drawable) null, "webp", availableReaction, 1);
            }
            frameLayout3.addView(animationView3);
            animationView3.getLayoutParams().width = emojiSize3;
            animationView3.getLayoutParams().height = emojiSize3;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).topMargin = topOffset;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).leftMargin = leftOffset;
            this.windowView.addView(frameLayout3);
            frameLayout3.getLayoutParams().width = size6;
            frameLayout3.getLayoutParams().height = size6;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).topMargin = -topOffset;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).leftMargin = -leftOffset;
            this.windowView.addView(animationView);
            animationView.getLayoutParams().width = size6;
            animationView.getLayoutParams().height = size6;
            animationView.getLayoutParams().width = size6;
            animationView.getLayoutParams().height = size6;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).topMargin = -topOffset;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).leftMargin = -leftOffset;
            frameLayout3.setPivotX((float) leftOffset);
            frameLayout3.setPivotY((float) topOffset);
            return;
        }
        int i18 = animationType2;
        int i19 = emojiSize2;
        int i20 = emojiSizeForFilter;
        int i21 = size5;
        int i22 = sizeForFilter3;
        this.dismissed = true;
    }

    /* access modifiers changed from: private */
    public void removeCurrentView() {
        try {
            if (this.useWindow) {
                this.windowManager.removeView(this.windowView);
            } else {
                this.decorView.removeView(this.windowView);
            }
        } catch (Exception e) {
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsLayout, ChatMessageCell cell2, float x, float y, String reaction2, int currentAccount2, int animationType2) {
        BaseFragment baseFragment2 = baseFragment;
        int i = animationType2;
        if (cell2 != null && reaction2 != null && baseFragment2 != null && baseFragment.getParentActivity() != null && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            if (i == 2 || i == 0) {
                show(baseFragment, (ReactionsContainerLayout) null, cell2, 0.0f, 0.0f, reaction2, currentAccount2, 1);
            }
            ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsLayout, cell2, x, y, reaction2, currentAccount2, animationType2);
            if (i == 1) {
                currentShortOverlay = reactionsEffectOverlay;
            } else {
                currentOverlay = reactionsEffectOverlay;
            }
            boolean useWindow2 = false;
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                if (chatActivity.scrimPopupWindow != null && chatActivity.scrimPopupWindow.isShowing()) {
                    useWindow2 = true;
                }
            }
            reactionsEffectOverlay.useWindow = useWindow2;
            if (useWindow2) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.height = -1;
                lp.width = -1;
                lp.type = 1000;
                lp.flags = 65816;
                lp.format = -3;
                WindowManager windowManager2 = baseFragment.getParentActivity().getWindowManager();
                reactionsEffectOverlay.windowManager = windowManager2;
                windowManager2.addView(reactionsEffectOverlay.windowView, lp);
            } else {
                FrameLayout frameLayout = (FrameLayout) baseFragment.getParentActivity().getWindow().getDecorView();
                reactionsEffectOverlay.decorView = frameLayout;
                frameLayout.addView(reactionsEffectOverlay.windowView);
            }
            cell2.invalidate();
            if (cell2.getCurrentMessagesGroup() != null && cell2.getParent() != null) {
                ((View) cell2.getParent()).invalidate();
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
            reactionsEffectOverlay2.cell.reactionsLayoutInBubble.animateReaction(currentShortOverlay.reaction);
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

    public static void removeCurrent(boolean instant) {
        int i = 0;
        while (i < 2) {
            ReactionsEffectOverlay overlay = i == 0 ? currentOverlay : currentShortOverlay;
            if (overlay != null) {
                if (instant) {
                    overlay.removeCurrentView();
                } else {
                    overlay.dismissed = true;
                }
            }
            i++;
        }
        currentShortOverlay = null;
        currentOverlay = null;
    }

    public static boolean isPlaying(int messageId2, long groupId2, String reaction2) {
        int i;
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null || ((i = reactionsEffectOverlay.animationType) != 2 && i != 0)) {
            return false;
        }
        long j = reactionsEffectOverlay.groupId;
        if (((j == 0 || groupId2 != j) && messageId2 != reactionsEffectOverlay.messageId) || !reactionsEffectOverlay.reaction.equals(reaction2)) {
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

    public static void onScrolled(int dy) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= (float) dy;
            if (dy != 0) {
                reactionsEffectOverlay.wasScrolled = true;
            }
        }
    }

    public static int sizeForBigReaction() {
        return (int) (((float) Math.round(((float) Math.min(AndroidUtilities.dp(350.0f), Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.7f)) / AndroidUtilities.density);
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

        private AvatarParticle() {
        }
    }
}
