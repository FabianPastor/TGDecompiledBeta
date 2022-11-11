package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import j$.util.Comparator$CC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$ForumTopic;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumBubbleDrawable;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes3.dex */
public class DialogCell extends BaseCell {
    private int animateFromStatusDrawableParams;
    private int animateToStatusDrawableParams;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack2;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack3;
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private boolean applyName;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
    private PullForegroundDrawable archivedChatsDrawable;
    private AvatarDrawable avatarDrawable;
    public ImageReceiver avatarImage;
    private int bottomClip;
    private Paint buttonBackgroundPaint;
    private StaticLayout buttonLayout;
    private int buttonTop;
    CanvasButton canvasButton;
    private TLRPC$Chat chat;
    private float chatCallProgress;
    private CheckBox2 checkBox;
    private int checkDrawLeft;
    private int checkDrawLeft1;
    private int checkDrawTop;
    public float chekBoxPaddingTop;
    private boolean clearingDialog;
    private float clipProgress;
    private int clockDrawLeft;
    private float cornerProgress;
    private StaticLayout countAnimationInLayout;
    private boolean countAnimationIncrement;
    private StaticLayout countAnimationStableLayout;
    private ValueAnimator countAnimator;
    private float countChangeProgress;
    private StaticLayout countLayout;
    private int countLeft;
    private int countLeftOld;
    private StaticLayout countOldLayout;
    private int countTop;
    private int countWidth;
    private int countWidthOld;
    private int currentAccount;
    private int currentDialogFolderDialogsCount;
    private int currentDialogFolderId;
    private long currentDialogId;
    private int currentEditDate;
    private TextPaint currentMessagePaint;
    private float currentRevealBounceProgress;
    private float currentRevealProgress;
    private CustomDialog customDialog;
    DialogCellDelegate delegate;
    private boolean dialogMuted;
    private float dialogMutedProgress;
    private int dialogsType;
    private TLRPC$DraftMessage draftMessage;
    public boolean drawAvatar;
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClock;
    private boolean drawCount;
    private boolean drawCount2;
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawPinForced;
    private boolean[] drawPlay;
    private boolean drawPremium;
    private boolean drawReactionMention;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private int drawScam;
    private boolean drawUnmute;
    private boolean drawVerified;
    public boolean drawingForBlur;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emojiStatus;
    private TLRPC$EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
    private Paint fadePaint;
    private Paint fadePaintBack;
    private int folderId;
    protected boolean forbidDraft;
    protected boolean forbidVerified;
    public TLRPC$TL_forumTopic forumTopic;
    public boolean fullSeparator;
    public boolean fullSeparator2;
    private ArrayList<MessageObject> groupMessages;
    private int halfCheckDrawLeft;
    private boolean hasCall;
    private boolean hasNameInMessage;
    private boolean hasUnmutedTopics;
    private boolean hasVideoThumb;
    public int heightDefault;
    public int heightThreeLines;
    private int index;
    private float innerProgress;
    private BounceInterpolator interpolator;
    private boolean isDialogCell;
    private boolean isForum;
    private boolean isSelected;
    private boolean isSliding;
    private boolean isTopic;
    long lastDialogChangedTime;
    private int lastDrawSwipeMessageStringId;
    private RLottieDrawable lastDrawTranslationDrawable;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString;
    private int lastSendState;
    int lastSize;
    private int lastStatusDrawableParams;
    private boolean lastTopicMessageUnread;
    private boolean lastUnreadState;
    private long lastUpdateTime;
    private int lock2Left;
    private boolean markUnread;
    private int mentionCount;
    private StaticLayout mentionLayout;
    private int mentionLeft;
    private int mentionWidth;
    private MessageObject message;
    private int messageId;
    private StaticLayout messageLayout;
    private int messageLeft;
    private StaticLayout messageNameLayout;
    private int messageNameLeft;
    private int messageNameTop;
    public int messagePaddingStart;
    private int messageTop;
    boolean moving;
    private boolean nameIsEllipsized;
    private StaticLayout nameLayout;
    private boolean nameLayoutEllipsizeByGradient;
    private boolean nameLayoutEllipsizeLeft;
    private boolean nameLayoutFits;
    private float nameLayoutTranslateX;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private int nameWidth;
    private boolean needEmoji;
    private float onlineProgress;
    private int paintIndex;
    private DialogsActivity parentFragment;
    private int pinLeft;
    private int pinTop;
    private DialogsAdapter.DialogsPreloader preloader;
    private int printingStringType;
    private int progressStage;
    private boolean promoDialog;
    private int reactionMentionCount;
    private int reactionMentionLeft;
    private ValueAnimator reactionsMentionsAnimator;
    private float reactionsMentionsChangeProgress;
    private int readOutboxMaxId;
    private RectF rect;
    private float reorderIconProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private List<SpoilerEffect> spoilers;
    private List<SpoilerEffect> spoilers2;
    private Stack<SpoilerEffect> spoilersPool;
    private Stack<SpoilerEffect> spoilersPool2;
    private boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private int statusDrawableLeft;
    private float statusDrawableProgress;
    public boolean swipeCanceled;
    private int swipeMessageTextId;
    private StaticLayout swipeMessageTextLayout;
    private int swipeMessageWidth;
    private ImageReceiver[] thumbImage;
    int thumbSize;
    private int thumbsCount;
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop;
    private int topClip;
    int topMessageTopicEndIndex;
    int topMessageTopicStartIndex;
    private Paint topicCounterPaint;
    private boolean topicMuted;
    protected int translateY;
    private boolean translationAnimationStarted;
    private RLottieDrawable translationDrawable;
    private float translationX;
    private boolean twoLinesForName;
    private int unreadCount;
    public boolean useForceThreeLines;
    private boolean useMeForMyMessages;
    public boolean useSeparator;
    private TLRPC$User user;

    /* loaded from: classes3.dex */
    public static class BounceInterpolator implements Interpolator {
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            if (f < 0.33f) {
                return (f / 0.33f) * 0.1f;
            }
            float f2 = f - 0.33f;
            return f2 < 0.33f ? 0.1f - ((f2 / 0.34f) * 0.15f) : (((f2 - 0.34f) / 0.33f) * 0.05f) - 0.05f;
        }
    }

    /* loaded from: classes3.dex */
    public static class CustomDialog {
        public int date;
        public int id;
        public boolean isMedia;
        public String message;
        public boolean muted;
        public String name;
        public boolean pinned;
        public int sent = -1;
        public int type;
        public int unread_count;
        public boolean verified;
    }

    /* loaded from: classes3.dex */
    public interface DialogCellDelegate {
        boolean canClickButtonInside();

        void onButtonClicked(DialogCell dialogCell);

        void onButtonLongPress(DialogCell dialogCell);
    }

    protected boolean drawLock2() {
        return false;
    }

    @Override // org.telegram.ui.Cells.BaseCell, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setMoving(boolean z) {
        this.moving = z;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public void setForumTopic(TLRPC$TL_forumTopic tLRPC$TL_forumTopic, long j, MessageObject messageObject, boolean z) {
        this.forumTopic = tLRPC$TL_forumTopic;
        boolean z2 = true;
        this.isTopic = tLRPC$TL_forumTopic != null;
        if (this.currentDialogId != j) {
            this.lastStatusDrawableParams = -1;
        }
        ForumBubbleDrawable[] forumBubbleDrawableArr = messageObject.topicIconDrawable;
        if (forumBubbleDrawableArr[0] != null) {
            forumBubbleDrawableArr[0].setColor(tLRPC$TL_forumTopic.icon_color);
        }
        this.currentDialogId = j;
        this.lastDialogChangedTime = System.currentTimeMillis();
        this.message = messageObject;
        this.isDialogCell = false;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        this.lastMessageDate = tLRPC$Message.date;
        this.currentEditDate = tLRPC$Message.edit_date;
        this.markUnread = false;
        this.messageId = messageObject.getId();
        if (!messageObject.isUnread()) {
            z2 = false;
        }
        this.lastUnreadState = z2;
        MessageObject messageObject2 = this.message;
        if (messageObject2 != null) {
            this.lastSendState = messageObject2.messageOwner.send_state;
        }
        if (!z) {
            this.lastStatusDrawableParams = -1;
        }
        if (tLRPC$TL_forumTopic != null) {
            this.groupMessages = tLRPC$TL_forumTopic.groupedMessages;
        }
        update(0, z);
    }

    /* loaded from: classes3.dex */
    public static class FixedWidthSpan extends ReplacementSpan {
        private int width;

        @Override // android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        }

        public FixedWidthSpan(int i) {
            this.width = i;
        }

        @Override // android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            if (fontMetricsInt == null) {
                fontMetricsInt = paint.getFontMetricsInt();
            }
            if (fontMetricsInt != null) {
                int i3 = 1 - (fontMetricsInt.descent - fontMetricsInt.ascent);
                fontMetricsInt.descent = i3;
                fontMetricsInt.bottom = i3;
                fontMetricsInt.ascent = -1;
                fontMetricsInt.top = -1;
            }
            return this.width;
        }
    }

    public DialogCell(DialogsActivity dialogsActivity, Context context, boolean z, boolean z2) {
        this(dialogsActivity, context, z, z2, UserConfig.selectedAccount, null);
    }

    public DialogCell(DialogsActivity dialogsActivity, Context context, boolean z, boolean z2, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.drawAvatar = true;
        this.messagePaddingStart = 72;
        this.heightDefault = 72;
        this.heightThreeLines = 78;
        this.chekBoxPaddingTop = 42.0f;
        this.hasUnmutedTopics = false;
        this.thumbImage = new ImageReceiver[3];
        this.drawPlay = new boolean[3];
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.spoilersPool = new Stack<>();
        this.spoilers = new ArrayList();
        this.spoilersPool2 = new Stack<>();
        this.spoilers2 = new ArrayList();
        this.drawCount2 = true;
        this.countChangeProgress = 1.0f;
        this.reactionsMentionsChangeProgress = 1.0f;
        this.rect = new RectF();
        this.lastStatusDrawableParams = -1;
        this.readOutboxMaxId = -1;
        this.resourcesProvider = resourcesProvider;
        this.parentFragment = dialogsActivity;
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        int i2 = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.thumbImage;
            if (i2 < imageReceiverArr.length) {
                imageReceiverArr[i2] = new ImageReceiver(this);
                this.thumbImage[i2].setRoundRadius(AndroidUtilities.dp(2.0f));
                i2++;
            } else {
                this.useForceThreeLines = z2;
                this.currentAccount = i;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(22.0f));
                this.emojiStatus = swapAnimatedEmojiDrawable;
                swapAnimatedEmojiDrawable.center = false;
                return;
            }
        }
    }

    public void setDialog(TLRPC$Dialog tLRPC$Dialog, int i, int i2) {
        if (this.currentDialogId != tLRPC$Dialog.id) {
            ValueAnimator valueAnimator = this.statusDrawableAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.statusDrawableAnimator.cancel();
            }
            this.statusDrawableAnimationInProgress = false;
            this.lastStatusDrawableParams = -1;
        }
        this.currentDialogId = tLRPC$Dialog.id;
        this.lastDialogChangedTime = System.currentTimeMillis();
        this.isDialogCell = true;
        if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
            this.currentDialogFolderId = ((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id;
            PullForegroundDrawable pullForegroundDrawable = this.archivedChatsDrawable;
            if (pullForegroundDrawable != null) {
                pullForegroundDrawable.setCell(this);
            }
        } else {
            this.currentDialogFolderId = 0;
        }
        this.dialogsType = i;
        this.folderId = i2;
        this.messageId = 0;
        update(0, false);
        checkOnline();
        checkGroupCall();
        checkChatTheme();
    }

    public void setDialogIndex(int i) {
        this.index = i;
    }

    public void setDialog(CustomDialog customDialog) {
        this.customDialog = customDialog;
        this.messageId = 0;
        update(0);
        checkOnline();
        checkGroupCall();
        checkChatTheme();
    }

    private void checkOnline() {
        TLRPC$User user;
        if (this.user != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.user.id))) != null) {
            this.user = user;
        }
        this.onlineProgress = isOnline() ? 1.0f : 0.0f;
    }

    private boolean isOnline() {
        TLRPC$User tLRPC$User;
        if (!isForumCell() && (tLRPC$User = this.user) != null && !tLRPC$User.self) {
            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
            if (tLRPC$UserStatus != null && tLRPC$UserStatus.expires <= 0 && MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(this.user.id))) {
                return true;
            }
            TLRPC$UserStatus tLRPC$UserStatus2 = this.user.status;
            if (tLRPC$UserStatus2 != null && tLRPC$UserStatus2.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                return true;
            }
        }
        return false;
    }

    private void checkGroupCall() {
        TLRPC$Chat tLRPC$Chat = this.chat;
        boolean z = tLRPC$Chat != null && tLRPC$Chat.call_active && tLRPC$Chat.call_not_empty;
        this.hasCall = z;
        this.chatCallProgress = z ? 1.0f : 0.0f;
    }

    private void checkChatTheme() {
        TLRPC$Message tLRPC$Message;
        MessageObject messageObject = this.message;
        if (messageObject == null || (tLRPC$Message = messageObject.messageOwner) == null) {
            return;
        }
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionSetChatTheme) || !this.lastUnreadState) {
            return;
        }
        ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.currentDialogId, ((TLRPC$TL_messageActionSetChatTheme) tLRPC$MessageAction).emoticon, false);
    }

    public void setDialog(long j, MessageObject messageObject, int i, boolean z, boolean z2) {
        if (this.currentDialogId != j) {
            this.lastStatusDrawableParams = -1;
        }
        this.currentDialogId = j;
        this.lastDialogChangedTime = System.currentTimeMillis();
        this.message = messageObject;
        this.useMeForMyMessages = z;
        this.isDialogCell = false;
        this.lastMessageDate = i;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        this.markUnread = false;
        this.messageId = messageObject != null ? messageObject.getId() : 0;
        this.mentionCount = 0;
        this.reactionMentionCount = 0;
        this.lastUnreadState = messageObject != null && messageObject.isUnread();
        MessageObject messageObject2 = this.message;
        if (messageObject2 != null) {
            this.lastSendState = messageObject2.messageOwner.send_state;
        }
        update(0, z2);
    }

    public long getDialogId() {
        return this.currentDialogId;
    }

    public int getDialogIndex() {
        return this.index;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public void setPreloader(DialogsAdapter.DialogsPreloader dialogsPreloader) {
        this.preloader = dialogsPreloader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSliding = false;
        this.drawRevealBackground = false;
        this.currentRevealProgress = 0.0f;
        this.reorderIconProgress = (!getIsPinned() || !this.drawReorder) ? 0.0f : 1.0f;
        this.avatarImage.onDetachedFromWindow();
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.thumbImage;
            if (i >= imageReceiverArr.length) {
                break;
            }
            imageReceiverArr[i].onDetachedFromWindow();
            i++;
        }
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
            this.translationDrawable.setProgress(0.0f);
            this.translationDrawable.setCallback(null);
            this.translationDrawable = null;
            this.translationAnimationStarted = false;
        }
        DialogsAdapter.DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.remove(this.currentDialogId);
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emojiStatus;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.detach();
        }
        AnimatedEmojiSpan.release(this, this.animatedEmojiStack);
        AnimatedEmojiSpan.release(this, this.animatedEmojiStack2);
        AnimatedEmojiSpan.release(this, this.animatedEmojiStack3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.thumbImage;
            if (i < imageReceiverArr.length) {
                imageReceiverArr[i].onAttachedToWindow();
                i++;
            } else {
                resetPinnedArchiveState();
                this.animatedEmojiStack = AnimatedEmojiSpan.update(0, this, this.animatedEmojiStack, this.messageLayout);
                this.animatedEmojiStack2 = AnimatedEmojiSpan.update(0, this, this.animatedEmojiStack2, this.messageNameLayout);
                this.animatedEmojiStack3 = AnimatedEmojiSpan.update(0, this, this.animatedEmojiStack3, this.buttonLayout);
                return;
            }
        }
    }

    public void resetPinnedArchiveState() {
        boolean z = SharedConfig.archiveHidden;
        this.archiveHidden = z;
        float f = 1.0f;
        float f2 = z ? 0.0f : 1.0f;
        this.archiveBackgroundProgress = f2;
        this.avatarDrawable.setArchivedAvatarHiddenProgress(f2);
        this.clipProgress = 0.0f;
        this.isSliding = false;
        if (!getIsPinned() || !this.drawReorder) {
            f = 0.0f;
        }
        this.reorderIconProgress = f;
        this.cornerProgress = 0.0f;
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emojiStatus;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.attach();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        if (this.isTopic) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? this.heightThreeLines : this.heightDefault) + (this.useSeparator ? 1 : 0));
            checkTwoLinesForName();
        }
        if (isForumCell()) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 86.0f : (this.useSeparator ? 1 : 0) + 91));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? this.heightThreeLines : this.heightDefault) + (this.useSeparator ? 1 : 0) + (this.twoLinesForName ? AndroidUtilities.dp(20.0f) : 0));
        }
        this.topClip = 0;
        this.bottomClip = getMeasuredHeight();
    }

    private void checkTwoLinesForName() {
        this.twoLinesForName = false;
        if (this.isTopic) {
            buildLayout();
            if (!this.nameIsEllipsized) {
                return;
            }
            this.twoLinesForName = true;
            buildLayout();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentDialogId == 0 && this.customDialog == null) {
            return;
        }
        if (this.checkBox != null) {
            int dp = AndroidUtilities.dp(this.messagePaddingStart - ((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 29 : 27));
            if (LocaleController.isRTL) {
                dp = (i3 - i) - dp;
            }
            int dp2 = AndroidUtilities.dp(this.chekBoxPaddingTop + ((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 6 : 0));
            CheckBox2 checkBox2 = this.checkBox;
            checkBox2.layout(dp, dp2, checkBox2.getMeasuredWidth() + dp, this.checkBox.getMeasuredHeight() + dp2);
        }
        int measuredHeight = (getMeasuredHeight() + getMeasuredWidth()) << 16;
        if (measuredHeight == this.lastSize) {
            return;
        }
        this.lastSize = measuredHeight;
        try {
            buildLayout();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean getHasUnread() {
        return this.unreadCount != 0 || this.markUnread;
    }

    public boolean getIsMuted() {
        return this.dialogMuted;
    }

    public boolean getIsPinned() {
        return this.drawPin || this.drawPinForced;
    }

    public void setPinForced(boolean z) {
        this.drawPinForced = z;
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            buildLayout();
        }
        invalidate();
    }

    private CharSequence formatArchivedDialogNames() {
        TLRPC$User tLRPC$User;
        String replace;
        ArrayList<TLRPC$Dialog> dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = dialogs.size();
        for (int i = 0; i < size; i++) {
            TLRPC$Dialog tLRPC$Dialog = dialogs.get(i);
            TLRPC$Chat tLRPC$Chat = null;
            if (DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                TLRPC$EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(tLRPC$Dialog.id)));
                tLRPC$User = encryptedChat != null ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id)) : null;
            } else if (DialogObject.isUserDialog(tLRPC$Dialog.id)) {
                tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$Dialog.id));
            } else {
                tLRPC$Chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog.id));
                tLRPC$User = null;
            }
            if (tLRPC$Chat != null) {
                replace = tLRPC$Chat.title.replace('\n', ' ');
            } else if (tLRPC$User == null) {
                continue;
            } else if (UserObject.isDeleted(tLRPC$User)) {
                replace = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                replace = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name).replace('\n', ' ');
            }
            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append((CharSequence) ", ");
            }
            int length = spannableStringBuilder.length();
            int length2 = replace.length() + length;
            spannableStringBuilder.append((CharSequence) replace);
            if (tLRPC$Dialog.unread_count > 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived", this.resourcesProvider)), length, length2, 33);
            }
            if (spannableStringBuilder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(41:422|423|(1:454)(5:427|428|429|430|(34:432|433|434|435|(1:445)(1:439)|440|(2:441|(1:443)(1:444))|203|(1:205)|206|207|208|(1:210)(1:394)|211|212|213|214|(1:390)(1:(2:221|(1:384)(1:227)))|228|(3:230|(3:232|(2:241|242)|239)|243)|244|(10:249|(2:251|(1:253))|254|255|256|(6:258|(6:262|(1:264)|265|(1:291)(2:269|(1:271)(2:277|(1:279)(2:280|(3:282|(1:284)(1:286)|285))))|272|(2:274|(1:276)))|292|(4:296|(1:(1:306)(2:298|(1:300)(2:301|302)))|303|(1:305))|307|(2:313|(1:315)))(6:334|(6:338|(1:340)|341|(4:343|(1:345)|346|(1:348))|349|(1:359))|360|(4:364|(1:366)|367|368)|369|(1:373))|316|(3:(1:330)(1:325)|326|(1:328)(1:329))|331|332)|375|(1:378)|379|(1:381)(1:383)|382|255|256|(0)(0)|316|(6:318|320|(1:323)|330|326|(0)(0))|331|332))|449|433|434|435|(1:437)|445|440|(3:441|(0)(0)|443)|203|(0)|206|207|208|(0)(0)|211|212|213|214|(1:216)|386|388|390|228|(0)|244|(11:246|249|(0)|254|255|256|(0)(0)|316|(0)|331|332)|375|(1:378)|379|(0)(0)|382|255|256|(0)(0)|316|(0)|331|332) */
    /* JADX WARN: Can't wrap try/catch for region: R(74:1|(1:1309)(1:5)|6|(1:1308)(1:12)|13|(1:1307)(1:17)|18|(1:20)|21|(2:23|(1:1296)(1:27))(2:1297|(1:1306)(1:1301))|28|(1:30)(1:1291)|31|(7:33|(1:35)|36|37|(1:39)|40|41)|42|(9:44|(2:46|(2:621|(1:623)(1:624))(2:50|(1:52)(1:620)))(4:625|(1:642)(1:629)|630|(2:638|(1:640)(1:641))(2:634|(1:636)(1:637)))|53|(3:55|(1:57)(4:607|(1:609)|610|(1:615)(1:614))|58)(3:616|(1:618)|619)|59|(1:61)(1:606)|62|(1:64)(1:(1:602)(1:(1:604)(1:605)))|65)(32:643|(2:1287|(1:1289)(1:1290))(2:647|(1:649)(1:1286))|650|(2:652|(2:654|(2:662|(1:664)(1:665))(2:658|(1:660)(1:661))))(2:1234|(2:1236|(2:1238|(1:1240)(2:1241|(1:1243)(3:1244|(1:1250)(1:1248)|1249)))(2:1251|(34:1253|(1:1255)(2:1276|(1:1278)(3:1279|(1:1285)(1:1283)|1284))|1256|(2:1258|(24:1262|1263|(2:1265|(2:1270|(1:1272)(1:1273))(1:1269))|667|(1:671)|672|(2:674|(1:678))(2:1230|(1:1232)(1:1233))|679|(6:1208|(2:1210|(2:1212|(2:1214|(1:1216))))|1218|(2:1220|(1:1222))|1224|(16:1226|(1:1228)|686|(3:688|(1:692)|693)(1:(15:839|(1:841)(1:850)|842|(1:844)(1:849)|(1:846)(1:848)|847|697|(1:699)(2:831|(1:833)(2:834|(1:836)(1:837)))|700|(1:702)(5:748|(4:750|(1:(1:753)(2:804|755))(1:805)|754|755)(7:806|(1:808)(6:818|(2:827|(1:829)(1:830))(1:826)|810|(1:812)(1:817)|813|(1:815)(1:816))|809|810|(0)(0)|813|(0)(0))|756|(2:761|(2:763|(1:765)(2:766|(1:768)(2:769|(3:771|(3:773|(1:775)(1:778)|776)(2:779|(3:781|(1:793)(1:785)|786)(3:794|(1:802)(1:800)|801))|777)))))|803)|703|(2:705|(2:707|(1:709)(2:710|(4:712|(1:714)|715|(1:717)))))|718|(1:720)(3:722|(2:724|(1:726)(1:731))(2:732|(2:734|(1:736)(2:737|(2:739|(1:741)(3:742|(1:744)|745))(1:746)))(1:747))|(1:730))|721)(12:851|(4:853|(2:855|(2:862|861)(1:859))(7:863|(1:865)|866|(3:870|(1:872)(1:874)|873)|875|(1:879)|880)|860|861)(3:881|(1:883)(2:885|(3:887|(2:889|(1:891)(2:892|(1:894)(2:895|(1:897)(2:898|(2:900|(1:902)(1:903))))))(2:905|(2:909|695))|904)(13:910|(1:912)(1:1203)|913|(2:927|(2:929|(10:931|(10:933|(1:935)(3:1196|(1:1198)(1:1200)|1199)|936|(1:938)(7:946|(3:948|(4:950|(2:952|(2:954|(1:956)(2:959|(1:961)(1:962))))|963|(1:965)(2:966|(1:968)(2:969|(1:971)(1:972))))(1:973)|957)(2:974|(7:979|(2:988|(2:1001|(4:1060|(2:1062|(5:1064|(1:1076)|1070|(1:1074)|1075)(2:1077|(2:1084|(2:1091|(4:1093|(1:1095)(2:1117|(1:1119)(2:1120|(1:1122)(2:1123|(1:1125)(2:1126|(1:1128)(1:1129)))))|1096|(3:1109|(3:1111|(1:1113)(1:1115)|1114)|1116)(4:1100|(2:1102|(1:1104)(1:1105))|(1:1107)|1108))(2:1130|(3:1132|(3:1134|(1:1136)(1:1139)|1137)(3:1140|(1:1142)(1:1144)|1143)|1138)(4:1145|(1:1147)(2:1153|(1:1155)(2:1156|(1:1158)(2:1159|(1:1161)(3:1162|(2:1168|(2:1170|(3:1172|(1:1174)(1:1176)|1175)))(1:1166)|1167))))|1148|(2:1150|(1:1152)))))(1:1090))(1:1083)))|1177|(6:1179|(3:1191|(1:1193)|1194)(1:1183)|1184|(1:1186)|1187|(1:1189))(1:1195))(12:1012|(2:1016|(3:1018|(1:1024)(1:1022)|1023))|1025|(1:1059)(5:1029|1030|1031|1032|1033)|1034|(1:1038)|1039|(4:1041|(1:1043)|1044|(1:1046)(1:1047))|1048|942|(1:944)|945))(8:994|(1:1000)(1:998)|999|940|941|942|(0)|945))(1:985)|986|987|942|(0)|945)(1:978))|958|941|942|(0)|945)|939|940|941|942|(0)|945)|1201|(0)(0)|939|940|941|942|(0)|945)))|1202|1201|(0)(0)|939|940|941|942|(0)|945))|884)|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))(1:1275)|1274|1263|(0)|667|(2:669|671)|672|(0)(0)|679|(2:681|1204)|1208|(0)|1218|(0)|1224|(0)|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))))|666|667|(0)|672|(0)(0)|679|(0)|1208|(0)|1218|(0)|1224|(0)|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721)|(2:67|(1:69)(1:599))(1:600)|70|(3:72|(1:74)(1:597)|75)(1:598)|76|(1:78)(1:596)|79|(1:81)|82|(2:84|(1:86)(1:583))(2:584|(2:586|(2:588|(1:590)(1:591))(2:592|(1:594)(1:595))))|87|(2:553|(2:580|(1:582))(2:557|(2:559|(1:561))(2:562|(2:564|(1:566))(2:567|(4:569|(1:571)(1:575)|572|(1:574))))))(2:91|(1:93))|94|(16:95|96|(1:98)|99|(3:101|(1:103)(1:105)|104)|106|(1:108)(1:550)|109|(1:111)|112|(1:549)(1:118)|119|(1:121)(1:548)|122|(1:547)(1:126)|127)|128|(4:531|(1:533)(1:545)|534|(2:535|(3:537|(2:539|540)(2:542|543)|541)(1:544)))(8:132|(1:134)(1:530)|135|(1:137)(1:529)|138|(1:140)(1:528)|141|(2:142|(3:144|(2:146|147)(2:149|150)|148)(1:151)))|152|(1:154)|155|(2:157|(1:159)(1:160))|161|(2:163|(1:165)(1:458))(1:(4:(3:470|(1:472)(1:526)|473)(1:527)|(5:475|(1:477)(1:524)|478|(3:480|(1:482)(1:518)|483)(3:519|(1:521)(1:523)|522)|484)(1:525)|485|(2:487|(4:489|(3:491|(1:493)(1:495)|494)|496|(3:498|(1:500)(1:502)|501))(5:503|(3:505|(1:507)(1:509)|508)|510|(3:512|(1:514)(1:516)|515)|517)))(3:463|(2:465|(1:467))|468))|(7:(1:168)|169|(1:171)|172|(1:183)(1:176)|177|(1:181))|184|(1:457)(1:188)|189|(5:191|(1:396)(1:195)|196|(2:197|(1:199)(1:200))|201)(2:397|(41:422|423|(1:454)(5:427|428|429|430|(34:432|433|434|435|(1:445)(1:439)|440|(2:441|(1:443)(1:444))|203|(1:205)|206|207|208|(1:210)(1:394)|211|212|213|214|(1:390)(1:(2:221|(1:384)(1:227)))|228|(3:230|(3:232|(2:241|242)|239)|243)|244|(10:249|(2:251|(1:253))|254|255|256|(6:258|(6:262|(1:264)|265|(1:291)(2:269|(1:271)(2:277|(1:279)(2:280|(3:282|(1:284)(1:286)|285))))|272|(2:274|(1:276)))|292|(4:296|(1:(1:306)(2:298|(1:300)(2:301|302)))|303|(1:305))|307|(2:313|(1:315)))(6:334|(6:338|(1:340)|341|(4:343|(1:345)|346|(1:348))|349|(1:359))|360|(4:364|(1:366)|367|368)|369|(1:373))|316|(3:(1:330)(1:325)|326|(1:328)(1:329))|331|332)|375|(1:378)|379|(1:381)(1:383)|382|255|256|(0)(0)|316|(6:318|320|(1:323)|330|326|(0)(0))|331|332))|449|433|434|435|(1:437)|445|440|(3:441|(0)(0)|443)|203|(0)|206|207|208|(0)(0)|211|212|213|214|(1:216)|386|388|390|228|(0)|244|(11:246|249|(0)|254|255|256|(0)(0)|316|(0)|331|332)|375|(1:378)|379|(0)(0)|382|255|256|(0)(0)|316|(0)|331|332)(2:401|(4:406|(1:416)(1:410)|411|(2:412|(1:414)(1:415)))(1:405)))|202|203|(0)|206|207|208|(0)(0)|211|212|213|214|(0)|386|388|390|228|(0)|244|(0)|375|(0)|379|(0)(0)|382|255|256|(0)(0)|316|(0)|331|332|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(89:1|(1:1309)(1:5)|6|(1:1308)(1:12)|13|(1:1307)(1:17)|18|(1:20)|21|(2:23|(1:1296)(1:27))(2:1297|(1:1306)(1:1301))|28|(1:30)(1:1291)|31|(7:33|(1:35)|36|37|(1:39)|40|41)|42|(9:44|(2:46|(2:621|(1:623)(1:624))(2:50|(1:52)(1:620)))(4:625|(1:642)(1:629)|630|(2:638|(1:640)(1:641))(2:634|(1:636)(1:637)))|53|(3:55|(1:57)(4:607|(1:609)|610|(1:615)(1:614))|58)(3:616|(1:618)|619)|59|(1:61)(1:606)|62|(1:64)(1:(1:602)(1:(1:604)(1:605)))|65)(32:643|(2:1287|(1:1289)(1:1290))(2:647|(1:649)(1:1286))|650|(2:652|(2:654|(2:662|(1:664)(1:665))(2:658|(1:660)(1:661))))(2:1234|(2:1236|(2:1238|(1:1240)(2:1241|(1:1243)(3:1244|(1:1250)(1:1248)|1249)))(2:1251|(34:1253|(1:1255)(2:1276|(1:1278)(3:1279|(1:1285)(1:1283)|1284))|1256|(2:1258|(24:1262|1263|(2:1265|(2:1270|(1:1272)(1:1273))(1:1269))|667|(1:671)|672|(2:674|(1:678))(2:1230|(1:1232)(1:1233))|679|(6:1208|(2:1210|(2:1212|(2:1214|(1:1216))))|1218|(2:1220|(1:1222))|1224|(16:1226|(1:1228)|686|(3:688|(1:692)|693)(1:(15:839|(1:841)(1:850)|842|(1:844)(1:849)|(1:846)(1:848)|847|697|(1:699)(2:831|(1:833)(2:834|(1:836)(1:837)))|700|(1:702)(5:748|(4:750|(1:(1:753)(2:804|755))(1:805)|754|755)(7:806|(1:808)(6:818|(2:827|(1:829)(1:830))(1:826)|810|(1:812)(1:817)|813|(1:815)(1:816))|809|810|(0)(0)|813|(0)(0))|756|(2:761|(2:763|(1:765)(2:766|(1:768)(2:769|(3:771|(3:773|(1:775)(1:778)|776)(2:779|(3:781|(1:793)(1:785)|786)(3:794|(1:802)(1:800)|801))|777)))))|803)|703|(2:705|(2:707|(1:709)(2:710|(4:712|(1:714)|715|(1:717)))))|718|(1:720)(3:722|(2:724|(1:726)(1:731))(2:732|(2:734|(1:736)(2:737|(2:739|(1:741)(3:742|(1:744)|745))(1:746)))(1:747))|(1:730))|721)(12:851|(4:853|(2:855|(2:862|861)(1:859))(7:863|(1:865)|866|(3:870|(1:872)(1:874)|873)|875|(1:879)|880)|860|861)(3:881|(1:883)(2:885|(3:887|(2:889|(1:891)(2:892|(1:894)(2:895|(1:897)(2:898|(2:900|(1:902)(1:903))))))(2:905|(2:909|695))|904)(13:910|(1:912)(1:1203)|913|(2:927|(2:929|(10:931|(10:933|(1:935)(3:1196|(1:1198)(1:1200)|1199)|936|(1:938)(7:946|(3:948|(4:950|(2:952|(2:954|(1:956)(2:959|(1:961)(1:962))))|963|(1:965)(2:966|(1:968)(2:969|(1:971)(1:972))))(1:973)|957)(2:974|(7:979|(2:988|(2:1001|(4:1060|(2:1062|(5:1064|(1:1076)|1070|(1:1074)|1075)(2:1077|(2:1084|(2:1091|(4:1093|(1:1095)(2:1117|(1:1119)(2:1120|(1:1122)(2:1123|(1:1125)(2:1126|(1:1128)(1:1129)))))|1096|(3:1109|(3:1111|(1:1113)(1:1115)|1114)|1116)(4:1100|(2:1102|(1:1104)(1:1105))|(1:1107)|1108))(2:1130|(3:1132|(3:1134|(1:1136)(1:1139)|1137)(3:1140|(1:1142)(1:1144)|1143)|1138)(4:1145|(1:1147)(2:1153|(1:1155)(2:1156|(1:1158)(2:1159|(1:1161)(3:1162|(2:1168|(2:1170|(3:1172|(1:1174)(1:1176)|1175)))(1:1166)|1167))))|1148|(2:1150|(1:1152)))))(1:1090))(1:1083)))|1177|(6:1179|(3:1191|(1:1193)|1194)(1:1183)|1184|(1:1186)|1187|(1:1189))(1:1195))(12:1012|(2:1016|(3:1018|(1:1024)(1:1022)|1023))|1025|(1:1059)(5:1029|1030|1031|1032|1033)|1034|(1:1038)|1039|(4:1041|(1:1043)|1044|(1:1046)(1:1047))|1048|942|(1:944)|945))(8:994|(1:1000)(1:998)|999|940|941|942|(0)|945))(1:985)|986|987|942|(0)|945)(1:978))|958|941|942|(0)|945)|939|940|941|942|(0)|945)|1201|(0)(0)|939|940|941|942|(0)|945)))|1202|1201|(0)(0)|939|940|941|942|(0)|945))|884)|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))(1:1275)|1274|1263|(0)|667|(2:669|671)|672|(0)(0)|679|(2:681|1204)|1208|(0)|1218|(0)|1224|(0)|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721))))|666|667|(0)|672|(0)(0)|679|(0)|1208|(0)|1218|(0)|1224|(0)|685|686|(0)(0)|694|695|696|697|(0)(0)|700|(0)(0)|703|(0)|718|(0)(0)|721)|(2:67|(1:69)(1:599))(1:600)|70|(3:72|(1:74)(1:597)|75)(1:598)|76|(1:78)(1:596)|79|(1:81)|82|(2:84|(1:86)(1:583))(2:584|(2:586|(2:588|(1:590)(1:591))(2:592|(1:594)(1:595))))|87|(2:553|(2:580|(1:582))(2:557|(2:559|(1:561))(2:562|(2:564|(1:566))(2:567|(4:569|(1:571)(1:575)|572|(1:574))))))(2:91|(1:93))|94|95|96|(1:98)|99|(3:101|(1:103)(1:105)|104)|106|(1:108)(1:550)|109|(1:111)|112|(1:549)(1:118)|119|(1:121)(1:548)|122|(1:547)(1:126)|127|128|(4:531|(1:533)(1:545)|534|(2:535|(3:537|(2:539|540)(2:542|543)|541)(1:544)))(8:132|(1:134)(1:530)|135|(1:137)(1:529)|138|(1:140)(1:528)|141|(2:142|(3:144|(2:146|147)(2:149|150)|148)(1:151)))|152|(1:154)|155|(2:157|(1:159)(1:160))|161|(2:163|(1:165)(1:458))(1:(4:(3:470|(1:472)(1:526)|473)(1:527)|(5:475|(1:477)(1:524)|478|(3:480|(1:482)(1:518)|483)(3:519|(1:521)(1:523)|522)|484)(1:525)|485|(2:487|(4:489|(3:491|(1:493)(1:495)|494)|496|(3:498|(1:500)(1:502)|501))(5:503|(3:505|(1:507)(1:509)|508)|510|(3:512|(1:514)(1:516)|515)|517)))(3:463|(2:465|(1:467))|468))|(7:(1:168)|169|(1:171)|172|(1:183)(1:176)|177|(1:181))|184|(1:457)(1:188)|189|(5:191|(1:396)(1:195)|196|(2:197|(1:199)(1:200))|201)(2:397|(41:422|423|(1:454)(5:427|428|429|430|(34:432|433|434|435|(1:445)(1:439)|440|(2:441|(1:443)(1:444))|203|(1:205)|206|207|208|(1:210)(1:394)|211|212|213|214|(1:390)(1:(2:221|(1:384)(1:227)))|228|(3:230|(3:232|(2:241|242)|239)|243)|244|(10:249|(2:251|(1:253))|254|255|256|(6:258|(6:262|(1:264)|265|(1:291)(2:269|(1:271)(2:277|(1:279)(2:280|(3:282|(1:284)(1:286)|285))))|272|(2:274|(1:276)))|292|(4:296|(1:(1:306)(2:298|(1:300)(2:301|302)))|303|(1:305))|307|(2:313|(1:315)))(6:334|(6:338|(1:340)|341|(4:343|(1:345)|346|(1:348))|349|(1:359))|360|(4:364|(1:366)|367|368)|369|(1:373))|316|(3:(1:330)(1:325)|326|(1:328)(1:329))|331|332)|375|(1:378)|379|(1:381)(1:383)|382|255|256|(0)(0)|316|(6:318|320|(1:323)|330|326|(0)(0))|331|332))|449|433|434|435|(1:437)|445|440|(3:441|(0)(0)|443)|203|(0)|206|207|208|(0)(0)|211|212|213|214|(1:216)|386|388|390|228|(0)|244|(11:246|249|(0)|254|255|256|(0)(0)|316|(0)|331|332)|375|(1:378)|379|(0)(0)|382|255|256|(0)(0)|316|(0)|331|332)(2:401|(4:406|(1:416)(1:410)|411|(2:412|(1:414)(1:415)))(1:405)))|202|203|(0)|206|207|208|(0)(0)|211|212|213|214|(0)|386|388|390|228|(0)|244|(0)|375|(0)|379|(0)(0)|382|255|256|(0)(0)|316|(0)|331|332|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:1088:0x184b, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1185:0x1a8c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1186:0x1a8d, code lost:
        r49.messageLayout = null;
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:250:0x058c, code lost:
        if (r4.post_messages == false) goto L685;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x0598, code lost:
        if (r4.kicked != false) goto L685;
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x05a6, code lost:
        if (r49.isTopic == false) goto L685;
     */
    /* JADX WARN: Code restructure failed: missing block: B:637:0x0e21, code lost:
        if (r4 != null) goto L987;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1026:0x1763  */
    /* JADX WARN: Removed duplicated region for block: B:1052:0x17cf  */
    /* JADX WARN: Removed duplicated region for block: B:1064:0x17fe  */
    /* JADX WARN: Removed duplicated region for block: B:1096:0x1860  */
    /* JADX WARN: Removed duplicated region for block: B:1103:0x1870 A[LOOP:8: B:1101:0x186b->B:1103:0x1870, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:1123:0x18ca  */
    /* JADX WARN: Removed duplicated region for block: B:1127:0x18e9 A[Catch: Exception -> 0x193f, TryCatch #0 {Exception -> 0x193f, blocks: (B:1125:0x18e3, B:1127:0x18e9, B:1128:0x193c), top: B:1303:0x18e3 }] */
    /* JADX WARN: Removed duplicated region for block: B:1128:0x193c A[Catch: Exception -> 0x193f, TRY_LEAVE, TryCatch #0 {Exception -> 0x193f, blocks: (B:1125:0x18e3, B:1127:0x18e9, B:1128:0x193c), top: B:1303:0x18e3 }] */
    /* JADX WARN: Removed duplicated region for block: B:1132:0x1953 A[Catch: Exception -> 0x1a8c, TryCatch #3 {Exception -> 0x1a8c, blocks: (B:1130:0x194f, B:1132:0x1953, B:1140:0x196d, B:1143:0x1973, B:1145:0x1979, B:1147:0x197d, B:1149:0x1990, B:1151:0x19bf, B:1153:0x19c3, B:1155:0x19d7, B:1157:0x19dd, B:1159:0x19e1, B:1162:0x19ee, B:1161:0x19eb, B:1163:0x19f1, B:1165:0x19f5, B:1168:0x19fa, B:1170:0x19fe, B:1172:0x1a11, B:1173:0x1a26, B:1183:0x1a76, B:1174:0x1a40, B:1177:0x1a47, B:1178:0x1a4e, B:1182:0x1a66, B:1150:0x19ae, B:1134:0x1957, B:1136:0x195b, B:1138:0x1960), top: B:1309:0x194f }] */
    /* JADX WARN: Removed duplicated region for block: B:1153:0x19c3 A[Catch: Exception -> 0x1a8c, TryCatch #3 {Exception -> 0x1a8c, blocks: (B:1130:0x194f, B:1132:0x1953, B:1140:0x196d, B:1143:0x1973, B:1145:0x1979, B:1147:0x197d, B:1149:0x1990, B:1151:0x19bf, B:1153:0x19c3, B:1155:0x19d7, B:1157:0x19dd, B:1159:0x19e1, B:1162:0x19ee, B:1161:0x19eb, B:1163:0x19f1, B:1165:0x19f5, B:1168:0x19fa, B:1170:0x19fe, B:1172:0x1a11, B:1173:0x1a26, B:1183:0x1a76, B:1174:0x1a40, B:1177:0x1a47, B:1178:0x1a4e, B:1182:0x1a66, B:1150:0x19ae, B:1134:0x1957, B:1136:0x195b, B:1138:0x1960), top: B:1309:0x194f }] */
    /* JADX WARN: Removed duplicated region for block: B:1165:0x19f5 A[Catch: Exception -> 0x1a8c, TryCatch #3 {Exception -> 0x1a8c, blocks: (B:1130:0x194f, B:1132:0x1953, B:1140:0x196d, B:1143:0x1973, B:1145:0x1979, B:1147:0x197d, B:1149:0x1990, B:1151:0x19bf, B:1153:0x19c3, B:1155:0x19d7, B:1157:0x19dd, B:1159:0x19e1, B:1162:0x19ee, B:1161:0x19eb, B:1163:0x19f1, B:1165:0x19f5, B:1168:0x19fa, B:1170:0x19fe, B:1172:0x1a11, B:1173:0x1a26, B:1183:0x1a76, B:1174:0x1a40, B:1177:0x1a47, B:1178:0x1a4e, B:1182:0x1a66, B:1150:0x19ae, B:1134:0x1957, B:1136:0x195b, B:1138:0x1960), top: B:1309:0x194f }] */
    /* JADX WARN: Removed duplicated region for block: B:1170:0x19fe A[Catch: Exception -> 0x1a8c, TryCatch #3 {Exception -> 0x1a8c, blocks: (B:1130:0x194f, B:1132:0x1953, B:1140:0x196d, B:1143:0x1973, B:1145:0x1979, B:1147:0x197d, B:1149:0x1990, B:1151:0x19bf, B:1153:0x19c3, B:1155:0x19d7, B:1157:0x19dd, B:1159:0x19e1, B:1162:0x19ee, B:1161:0x19eb, B:1163:0x19f1, B:1165:0x19f5, B:1168:0x19fa, B:1170:0x19fe, B:1172:0x1a11, B:1173:0x1a26, B:1183:0x1a76, B:1174:0x1a40, B:1177:0x1a47, B:1178:0x1a4e, B:1182:0x1a66, B:1150:0x19ae, B:1134:0x1957, B:1136:0x195b, B:1138:0x1960), top: B:1309:0x194f }] */
    /* JADX WARN: Removed duplicated region for block: B:1176:0x1a45 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:1180:0x1a61  */
    /* JADX WARN: Removed duplicated region for block: B:1181:0x1a64  */
    /* JADX WARN: Removed duplicated region for block: B:1189:0x1aa7  */
    /* JADX WARN: Removed duplicated region for block: B:1245:0x1CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1286:0x1cd8  */
    /* JADX WARN: Removed duplicated region for block: B:1297:0x1d15  */
    /* JADX WARN: Removed duplicated region for block: B:1298:0x1d1d  */
    /* JADX WARN: Removed duplicated region for block: B:1333:0x18c6 A[EDGE_INSN: B:1333:0x18c6->B:1121:0x18c6 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1334:0x1560 A[EDGE_INSN: B:1334:0x1560->B:940:0x1560 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x04ca  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x0518  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x0524  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0543  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x055c  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x057c  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x0592  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x059e  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x05b0  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x0602  */
    /* JADX WARN: Removed duplicated region for block: B:384:0x08cb  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x08d3  */
    /* JADX WARN: Removed duplicated region for block: B:493:0x0aa6  */
    /* JADX WARN: Removed duplicated region for block: B:642:0x0e2c  */
    /* JADX WARN: Removed duplicated region for block: B:646:0x0e38  */
    /* JADX WARN: Removed duplicated region for block: B:647:0x0e40  */
    /* JADX WARN: Removed duplicated region for block: B:656:0x0e5e  */
    /* JADX WARN: Removed duplicated region for block: B:657:0x0e70  */
    /* JADX WARN: Removed duplicated region for block: B:686:0x0eed  */
    /* JADX WARN: Removed duplicated region for block: B:687:0x0ef2  */
    /* JADX WARN: Removed duplicated region for block: B:690:0x0ef9  */
    /* JADX WARN: Removed duplicated region for block: B:691:0x0efc  */
    /* JADX WARN: Removed duplicated region for block: B:744:0x0fbc  */
    /* JADX WARN: Removed duplicated region for block: B:759:0x1012  */
    /* JADX WARN: Removed duplicated region for block: B:761:0x1024  */
    /* JADX WARN: Removed duplicated region for block: B:788:0x1088  */
    /* JADX WARN: Removed duplicated region for block: B:792:0x10c9  */
    /* JADX WARN: Removed duplicated region for block: B:795:0x10d7  */
    /* JADX WARN: Removed duplicated region for block: B:800:0x110a  */
    /* JADX WARN: Removed duplicated region for block: B:803:0x110f  */
    /* JADX WARN: Removed duplicated region for block: B:804:0x1122  */
    /* JADX WARN: Removed duplicated region for block: B:807:0x1140  */
    /* JADX WARN: Removed duplicated region for block: B:810:0x1159  */
    /* JADX WARN: Removed duplicated region for block: B:814:0x1187  */
    /* JADX WARN: Removed duplicated region for block: B:843:0x124d  */
    /* JADX WARN: Removed duplicated region for block: B:866:0x12b9  */
    /* JADX WARN: Removed duplicated region for block: B:869:0x12c6 A[Catch: Exception -> 0x139f, TryCatch #1 {Exception -> 0x139f, blocks: (B:864:0x12b0, B:867:0x12ba, B:869:0x12c6, B:873:0x12e0, B:874:0x12e9, B:878:0x12ff, B:880:0x1305, B:881:0x1311, B:883:0x1327, B:885:0x132d, B:889:0x133e, B:891:0x1342, B:893:0x137e, B:895:0x1382, B:897:0x138a, B:899:0x1394, B:892:0x1361), top: B:1305:0x12b0 }] */
    /* JADX WARN: Removed duplicated region for block: B:876:0x12fc  */
    /* JADX WARN: Removed duplicated region for block: B:877:0x12fe  */
    /* JADX WARN: Removed duplicated region for block: B:880:0x1305 A[Catch: Exception -> 0x139f, TryCatch #1 {Exception -> 0x139f, blocks: (B:864:0x12b0, B:867:0x12ba, B:869:0x12c6, B:873:0x12e0, B:874:0x12e9, B:878:0x12ff, B:880:0x1305, B:881:0x1311, B:883:0x1327, B:885:0x132d, B:889:0x133e, B:891:0x1342, B:893:0x137e, B:895:0x1382, B:897:0x138a, B:899:0x1394, B:892:0x1361), top: B:1305:0x12b0 }] */
    /* JADX WARN: Removed duplicated region for block: B:891:0x1342 A[Catch: Exception -> 0x139f, TryCatch #1 {Exception -> 0x139f, blocks: (B:864:0x12b0, B:867:0x12ba, B:869:0x12c6, B:873:0x12e0, B:874:0x12e9, B:878:0x12ff, B:880:0x1305, B:881:0x1311, B:883:0x1327, B:885:0x132d, B:889:0x133e, B:891:0x1342, B:893:0x137e, B:895:0x1382, B:897:0x138a, B:899:0x1394, B:892:0x1361), top: B:1305:0x12b0 }] */
    /* JADX WARN: Removed duplicated region for block: B:892:0x1361 A[Catch: Exception -> 0x139f, TryCatch #1 {Exception -> 0x139f, blocks: (B:864:0x12b0, B:867:0x12ba, B:869:0x12c6, B:873:0x12e0, B:874:0x12e9, B:878:0x12ff, B:880:0x1305, B:881:0x1311, B:883:0x1327, B:885:0x132d, B:889:0x133e, B:891:0x1342, B:893:0x137e, B:895:0x1382, B:897:0x138a, B:899:0x1394, B:892:0x1361), top: B:1305:0x12b0 }] */
    /* JADX WARN: Removed duplicated region for block: B:930:0x14e0  */
    /* JADX WARN: Removed duplicated region for block: B:931:0x14fe  */
    /* JADX WARN: Removed duplicated region for block: B:935:0x1533  */
    /* JADX WARN: Removed duplicated region for block: B:942:0x1565  */
    /* JADX WARN: Removed duplicated region for block: B:945:0x1574  */
    /* JADX WARN: Removed duplicated region for block: B:951:0x1599  */
    /* JADX WARN: Removed duplicated region for block: B:955:0x15c7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void buildLayout() {
        /*
            Method dump skipped, instructions count: 7472
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
    }

    private void updateThumbsPosition() {
        if (this.thumbsCount > 0) {
            StaticLayout staticLayout = isForumCell() ? this.buttonLayout : this.messageLayout;
            if (staticLayout == null) {
                return;
            }
            try {
                CharSequence text = staticLayout.getText();
                if (!(text instanceof Spannable)) {
                    return;
                }
                FixedWidthSpan[] fixedWidthSpanArr = (FixedWidthSpan[]) ((Spannable) text).getSpans(0, text.length(), FixedWidthSpan.class);
                if (fixedWidthSpanArr == null || fixedWidthSpanArr.length <= 0) {
                    return;
                }
                int spanStart = ((Spannable) text).getSpanStart(fixedWidthSpanArr[0]);
                if (spanStart < 0) {
                    spanStart = 0;
                }
                int ceil = (int) Math.ceil(Math.min(staticLayout.getPrimaryHorizontal(spanStart), staticLayout.getPrimaryHorizontal(spanStart + 1)));
                if (ceil != 0) {
                    ceil += AndroidUtilities.dp(3.0f);
                }
                for (int i = 0; i < this.thumbsCount; i++) {
                    this.thumbImage[i].setImageX(this.messageLeft + ceil + AndroidUtilities.dp((this.thumbSize + 2) * i));
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private CharSequence applyThumbs(CharSequence charSequence) {
        if (this.thumbsCount > 0) {
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
            valueOf.insert(0, (CharSequence) " ");
            valueOf.setSpan(new FixedWidthSpan(AndroidUtilities.dp((((this.thumbSize + 2) * this.thumbsCount) - 2) + 5)), 0, 1, 33);
            return valueOf;
        }
        return charSequence;
    }

    private CharSequence formatTopicsNames() {
        int i;
        boolean z;
        int i2;
        this.topMessageTopicStartIndex = 0;
        this.topMessageTopicEndIndex = 0;
        if (this.chat != null) {
            ArrayList<TLRPC$TL_forumTopic> topics = MessagesController.getInstance(this.currentAccount).getTopicsController().getTopics(this.chat.id);
            if (topics != null && !topics.isEmpty()) {
                ArrayList arrayList = new ArrayList(topics);
                Collections.sort(arrayList, Comparator$CC.comparingInt(DialogCell$$ExternalSyntheticLambda6.INSTANCE));
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                MessageObject messageObject = this.message;
                boolean z2 = true;
                if (messageObject != null) {
                    i = MessageObject.getTopicId(messageObject.messageOwner);
                    TLRPC$TL_forumTopic findTopic = MessagesController.getInstance(this.currentAccount).getTopicsController().findTopic(this.chat.id, i);
                    if (findTopic != null) {
                        CharSequence topicSpannedName = ForumUtilities.getTopicSpannedName(findTopic, this.currentMessagePaint);
                        spannableStringBuilder.append(topicSpannedName);
                        i2 = findTopic.unread_count > 0 ? topicSpannedName.length() : 0;
                        this.topMessageTopicStartIndex = 0;
                        this.topMessageTopicEndIndex = topicSpannedName.length();
                        if (this.message.isOutOwner()) {
                            this.lastTopicMessageUnread = findTopic.read_inbox_max_id < this.message.getId();
                        } else {
                            this.lastTopicMessageUnread = findTopic.unread_count > 0;
                        }
                    } else {
                        i2 = 0;
                    }
                    if (this.lastTopicMessageUnread) {
                        spannableStringBuilder.append((CharSequence) " ");
                        spannableStringBuilder.setSpan(new FixedWidthSpan(AndroidUtilities.dp(3.0f)), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
                        z = true;
                    } else {
                        z = false;
                    }
                } else {
                    i = 0;
                    z = false;
                    i2 = 0;
                }
                for (int i3 = 0; i3 < Math.min(5, arrayList.size()); i3++) {
                    if (((TLRPC$TL_forumTopic) arrayList.get(i3)).id != i) {
                        if (spannableStringBuilder.length() != 0) {
                            if (z2 && z) {
                                spannableStringBuilder.append((CharSequence) " ");
                            } else {
                                spannableStringBuilder.append((CharSequence) ", ");
                            }
                        }
                        spannableStringBuilder.append(ForumUtilities.getTopicSpannedName((TLRPC$ForumTopic) arrayList.get(i3), this.currentMessagePaint));
                        z2 = false;
                    }
                }
                if (i2 > 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_name", this.resourcesProvider)), 0, Math.min(spannableStringBuilder.length(), i2 + 2), 0);
                }
                return spannableStringBuilder;
            } else if (MessagesController.getInstance(this.currentAccount).getTopicsController().endIsReached(this.chat.id)) {
                return "no created topics";
            } else {
                MessagesController.getInstance(this.currentAccount).getTopicsController().preloadTopics(this.chat.id);
                return "Loading...";
            }
        }
        return null;
    }

    protected boolean isForumCell() {
        TLRPC$Chat tLRPC$Chat;
        return !isDialogFolder() && (tLRPC$Chat = this.chat) != null && tLRPC$Chat.forum && !this.isTopic;
    }

    private void drawCheckStatus(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f) {
        if (f != 0.0f || z4) {
            float f2 = (f * 0.5f) + 0.5f;
            if (z) {
                BaseCell.setDrawableBounds(Theme.dialogs_clockDrawable, this.clockDrawLeft, this.checkDrawTop);
                if (f != 1.0f) {
                    canvas.save();
                    canvas.scale(f2, f2, Theme.dialogs_clockDrawable.getBounds().centerX(), Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                    Theme.dialogs_clockDrawable.setAlpha((int) (255.0f * f));
                }
                Theme.dialogs_clockDrawable.draw(canvas);
                if (f != 1.0f) {
                    canvas.restore();
                    Theme.dialogs_clockDrawable.setAlpha(255);
                }
                invalidate();
            } else if (!z3) {
            } else {
                if (z2) {
                    BaseCell.setDrawableBounds(Theme.dialogs_halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
                    if (z4) {
                        canvas.save();
                        canvas.scale(f2, f2, Theme.dialogs_halfCheckDrawable.getBounds().centerX(), Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        Theme.dialogs_halfCheckDrawable.setAlpha((int) (f * 255.0f));
                    }
                    if (!z4 && f != 0.0f) {
                        canvas.save();
                        canvas.scale(f2, f2, Theme.dialogs_halfCheckDrawable.getBounds().centerX(), Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        int i = (int) (255.0f * f);
                        Theme.dialogs_halfCheckDrawable.setAlpha(i);
                        Theme.dialogs_checkReadDrawable.setAlpha(i);
                    }
                    Theme.dialogs_halfCheckDrawable.draw(canvas);
                    if (z4) {
                        canvas.restore();
                        canvas.save();
                        canvas.translate(AndroidUtilities.dp(4.0f) * (1.0f - f), 0.0f);
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_checkReadDrawable, this.checkDrawLeft, this.checkDrawTop);
                    Theme.dialogs_checkReadDrawable.draw(canvas);
                    if (z4) {
                        canvas.restore();
                        Theme.dialogs_halfCheckDrawable.setAlpha(255);
                    }
                    if (z4 || f == 0.0f) {
                        return;
                    }
                    canvas.restore();
                    Theme.dialogs_halfCheckDrawable.setAlpha(255);
                    Theme.dialogs_checkReadDrawable.setAlpha(255);
                    return;
                }
                BaseCell.setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft1, this.checkDrawTop);
                if (f != 1.0f) {
                    canvas.save();
                    canvas.scale(f2, f2, Theme.dialogs_checkDrawable.getBounds().centerX(), Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                    Theme.dialogs_checkDrawable.setAlpha((int) (255.0f * f));
                }
                Theme.dialogs_checkDrawable.draw(canvas);
                if (f == 1.0f) {
                    return;
                }
                canvas.restore();
                Theme.dialogs_checkDrawable.setAlpha(255);
            }
        }
    }

    public boolean isPointInsideAvatar(float f, float f2) {
        return !LocaleController.isRTL ? f >= 0.0f && f < ((float) AndroidUtilities.dp(60.0f)) : f >= ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) && f < ((float) getMeasuredWidth());
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    public boolean checkCurrentDialogIndex(boolean z) {
        MessageObject messageObject;
        MessageObject messageObject2;
        DialogsActivity dialogsActivity = this.parentFragment;
        boolean z2 = false;
        if (dialogsActivity == null) {
            return false;
        }
        ArrayList<TLRPC$Dialog> dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, z);
        if (this.index < dialogsArray.size()) {
            TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(this.index);
            boolean z3 = true;
            MessageObject messageObject3 = null;
            TLRPC$Dialog tLRPC$Dialog2 = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
            TLRPC$DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId, 0);
            if (this.currentDialogFolderId != 0) {
                messageObject = findFolderTopMessage();
                this.groupMessages = null;
            } else {
                ArrayList<MessageObject> arrayList = MessagesController.getInstance(this.currentAccount).dialogMessage.get(tLRPC$Dialog.id);
                this.groupMessages = arrayList;
                if (arrayList != null && arrayList.size() > 0) {
                    messageObject3 = this.groupMessages.get(0);
                }
                messageObject = messageObject3;
            }
            if (this.currentDialogId != tLRPC$Dialog.id || (((messageObject2 = this.message) != null && messageObject2.getId() != tLRPC$Dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tLRPC$Dialog.unread_count || this.mentionCount != tLRPC$Dialog.unread_mentions_count || this.markUnread != tLRPC$Dialog.unread_mark || this.message != messageObject || draft != this.draftMessage || this.drawPin != tLRPC$Dialog.pinned))) {
                boolean z4 = this.currentDialogId != tLRPC$Dialog.id;
                boolean z5 = this.isForum != MessagesController.getInstance(this.currentAccount).isForum(tLRPC$Dialog.id);
                this.isForum = MessagesController.getInstance(this.currentAccount).isForum(tLRPC$Dialog.id);
                this.currentDialogId = tLRPC$Dialog.id;
                if (z4) {
                    this.lastDialogChangedTime = System.currentTimeMillis();
                    ValueAnimator valueAnimator = this.statusDrawableAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.statusDrawableAnimator.cancel();
                    }
                    this.statusDrawableAnimationInProgress = false;
                    this.lastStatusDrawableParams = -1;
                }
                boolean z6 = tLRPC$Dialog instanceof TLRPC$TL_dialogFolder;
                if (z6) {
                    this.currentDialogFolderId = ((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id;
                } else {
                    this.currentDialogFolderId = 0;
                }
                int i = this.dialogsType;
                if (i == 7 || i == 8) {
                    MessagesController.DialogFilter dialogFilter = MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType == 8 ? (char) 1 : (char) 0];
                    if (!(tLRPC$Dialog instanceof TLRPC$TL_dialog) || tLRPC$Dialog2 == null || dialogFilter == null || dialogFilter.pinnedDialogs.indexOfKey(tLRPC$Dialog.id) < 0 || dialogFilter.pinnedDialogs.indexOfKey(tLRPC$Dialog2.id) >= 0) {
                        z3 = false;
                    }
                    this.fullSeparator = z3;
                    this.fullSeparator2 = false;
                } else {
                    this.fullSeparator = (tLRPC$Dialog instanceof TLRPC$TL_dialog) && tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
                    if (!z6 || tLRPC$Dialog2 == null || tLRPC$Dialog2.pinned) {
                        z3 = false;
                    }
                    this.fullSeparator2 = z3;
                }
                update(0, !z4);
                if (z4) {
                    this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
                }
                checkOnline();
                checkGroupCall();
                checkChatTheme();
                z2 = z5;
            }
        }
        if (z2) {
            requestLayout();
        }
        return z2;
    }

    public void animateArchiveAvatar() {
        if (this.avatarDrawable.getAvatarType() != 2) {
            return;
        }
        this.animatingArchiveAvatar = true;
        this.animatingArchiveAvatarProgress = 0.0f;
        Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
        Theme.dialogs_archiveAvatarDrawable.start();
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox == null) {
            CheckBox2 checkBox2 = new CheckBox2(getContext(), 21, this.resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox);
        }
        this.checkBox.setChecked(z, z2);
    }

    private MessageObject findFolderTopMessage() {
        DialogsActivity dialogsActivity = this.parentFragment;
        if (dialogsActivity == null) {
            return null;
        }
        ArrayList<TLRPC$Dialog> dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        if (dialogsArray.isEmpty()) {
            return null;
        }
        int size = dialogsArray.size();
        MessageObject messageObject = null;
        for (int i = 0; i < size; i++) {
            TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(i);
            ArrayList<MessageObject> arrayList = MessagesController.getInstance(this.currentAccount).dialogMessage.get(tLRPC$Dialog.id);
            MessageObject messageObject2 = (arrayList == null || arrayList.size() <= 0) ? null : arrayList.get(0);
            if (messageObject2 != null && (messageObject == null || messageObject2.messageOwner.date > messageObject.messageOwner.date)) {
                messageObject = messageObject2;
            }
            if (tLRPC$Dialog.pinnedNum == 0 && messageObject != null) {
                break;
            }
        }
        return messageObject;
    }

    public boolean isFolderCell() {
        return this.currentDialogFolderId != 0;
    }

    public boolean update(int i) {
        return update(i, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:153:0x02b8  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x02ef  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x03a7  */
    /* JADX WARN: Removed duplicated region for block: B:224:0x03ac  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x03b0  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x03b2  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x03bf  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x03d2  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x0425  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x04cc  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x04f0  */
    /* JADX WARN: Removed duplicated region for block: B:302:0x058a  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0619  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x06a5  */
    /* JADX WARN: Removed duplicated region for block: B:336:0x06b5  */
    /* JADX WARN: Removed duplicated region for block: B:337:0x06b7  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x06c3  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x06c5  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01a8  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01b9  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01bd  */
    /* JADX WARN: Type inference failed for: r3v103 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$EncryptedChat] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean update(int r27, boolean r28) {
        /*
            Method dump skipped, instructions count: 1912
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$2(ValueAnimator valueAnimator) {
        this.reactionsMentionsChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private int getTopicId() {
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic = this.forumTopic;
        if (tLRPC$TL_forumTopic == null) {
            return 0;
        }
        return tLRPC$TL_forumTopic.id;
    }

    @Override // android.view.View
    public float getTranslationX() {
        return this.translationX;
    }

    @Override // android.view.View
    public void setTranslationX(float f) {
        if (f == this.translationX) {
            return;
        }
        this.translationX = f;
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        boolean z = false;
        if (rLottieDrawable != null && f == 0.0f) {
            rLottieDrawable.setProgress(0.0f);
            this.translationAnimationStarted = false;
            this.archiveHidden = SharedConfig.archiveHidden;
            this.currentRevealProgress = 0.0f;
            this.isSliding = false;
        }
        float f2 = this.translationX;
        if (f2 != 0.0f) {
            this.isSliding = true;
        } else {
            this.currentRevealBounceProgress = 0.0f;
            this.currentRevealProgress = 0.0f;
            this.drawRevealBackground = false;
        }
        if (this.isSliding && !this.swipeCanceled) {
            boolean z2 = this.drawRevealBackground;
            if (Math.abs(f2) >= getMeasuredWidth() * 0.45f) {
                z = true;
            }
            this.drawRevealBackground = z;
            if (z2 != z && this.archiveHidden == SharedConfig.archiveHidden) {
                try {
                    performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
            }
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:662:0x124d, code lost:
        if (r40.reactionsMentionsChangeProgress != 1.0f) goto L636;
     */
    /* JADX WARN: Removed duplicated region for block: B:274:0x0878  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x099c  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x0b3e  */
    /* JADX WARN: Removed duplicated region for block: B:401:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:443:0x0c7f  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:447:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:452:0x0ced  */
    /* JADX WARN: Removed duplicated region for block: B:458:0x0d03  */
    /* JADX WARN: Removed duplicated region for block: B:473:0x0d4b  */
    /* JADX WARN: Removed duplicated region for block: B:521:0x0e0d  */
    /* JADX WARN: Removed duplicated region for block: B:526:0x0e35  */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0e83  */
    /* JADX WARN: Removed duplicated region for block: B:675:0x12f0  */
    /* JADX WARN: Removed duplicated region for block: B:678:0x1312  */
    /* JADX WARN: Removed duplicated region for block: B:687:0x1329  */
    /* JADX WARN: Removed duplicated region for block: B:696:0x1371  */
    /* JADX WARN: Removed duplicated region for block: B:699:0x1378  */
    /* JADX WARN: Removed duplicated region for block: B:748:0x1459  */
    /* JADX WARN: Removed duplicated region for block: B:812:0x1642  */
    /* JADX WARN: Removed duplicated region for block: B:817:0x16d1  */
    /* JADX WARN: Removed duplicated region for block: B:822:0x16e8  */
    /* JADX WARN: Removed duplicated region for block: B:827:0x16fc  */
    /* JADX WARN: Removed duplicated region for block: B:833:0x1711  */
    /* JADX WARN: Removed duplicated region for block: B:842:0x1730  */
    /* JADX WARN: Removed duplicated region for block: B:845:0x1737  */
    /* JADX WARN: Removed duplicated region for block: B:852:0x175c  */
    /* JADX WARN: Removed duplicated region for block: B:874:0x17c3  */
    /* JADX WARN: Removed duplicated region for block: B:880:0x1810  */
    /* JADX WARN: Removed duplicated region for block: B:885:0x181c  */
    /* JADX WARN: Removed duplicated region for block: B:891:0x1831  */
    /* JADX WARN: Removed duplicated region for block: B:899:0x1849  */
    /* JADX WARN: Removed duplicated region for block: B:907:0x1873  */
    /* JADX WARN: Removed duplicated region for block: B:918:0x18a4  */
    /* JADX WARN: Removed duplicated region for block: B:924:0x18b9  */
    /* JADX WARN: Removed duplicated region for block: B:932:0x18d6  */
    /* JADX WARN: Removed duplicated region for block: B:935:0x18e4  */
    /* JADX WARN: Removed duplicated region for block: B:946:0x1909  */
    /* JADX WARN: Removed duplicated region for block: B:962:? A[RETURN, SYNTHETIC] */
    @Override // android.view.View
    @android.annotation.SuppressLint({"DrawAllocation"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onDraw(android.graphics.Canvas r41) {
        /*
            Method dump skipped, instructions count: 6446
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$3() {
        this.delegate.onButtonClicked(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$4() {
        this.delegate.onButtonLongPress(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createStatusDrawableAnimator(int i, int i2) {
        this.statusDrawableProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.statusDrawableAnimator = ofFloat;
        ofFloat.setDuration(220L);
        this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animateFromStatusDrawableParams = i;
        this.animateToStatusDrawableParams = i2;
        this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DialogCell.this.lambda$createStatusDrawableAnimator$5(valueAnimator);
            }
        });
        this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.DialogCell.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                int i3 = (DialogCell.this.drawClock ? 1 : 0) + (DialogCell.this.drawCheck1 ? 2 : 0) + (DialogCell.this.drawCheck2 ? 4 : 0);
                if (DialogCell.this.animateToStatusDrawableParams == i3) {
                    DialogCell.this.statusDrawableAnimationInProgress = false;
                    DialogCell dialogCell = DialogCell.this;
                    dialogCell.lastStatusDrawableParams = dialogCell.animateToStatusDrawableParams;
                } else {
                    DialogCell dialogCell2 = DialogCell.this;
                    dialogCell2.createStatusDrawableAnimator(dialogCell2.animateToStatusDrawableParams, i3);
                }
                DialogCell.this.invalidate();
            }
        });
        this.statusDrawableAnimationInProgress = true;
        this.statusDrawableAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createStatusDrawableAnimator$5(ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void startOutAnimation() {
        PullForegroundDrawable pullForegroundDrawable = this.archivedChatsDrawable;
        if (pullForegroundDrawable != null) {
            pullForegroundDrawable.outCy = this.avatarImage.getCenterY();
            this.archivedChatsDrawable.outCx = this.avatarImage.getCenterX();
            this.archivedChatsDrawable.outRadius = this.avatarImage.getImageWidth() / 2.0f;
            this.archivedChatsDrawable.outImageSize = this.avatarImage.getBitmapWidth();
            this.archivedChatsDrawable.startOutAnimation();
        }
    }

    public void onReorderStateChanged(boolean z, boolean z2) {
        if ((!getIsPinned() && z) || this.drawReorder == z) {
            if (getIsPinned()) {
                return;
            }
            this.drawReorder = false;
            return;
        }
        this.drawReorder = z;
        float f = 1.0f;
        if (z2) {
            if (z) {
                f = 0.0f;
            }
            this.reorderIconProgress = f;
        } else {
            if (!z) {
                f = 0.0f;
            }
            this.reorderIconProgress = f;
        }
        invalidate();
    }

    public void setSliding(boolean z) {
        this.isSliding = z;
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.translationDrawable || drawable == Theme.dialogs_archiveAvatarDrawable) {
            invalidate(drawable.getBounds());
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        DialogsActivity dialogsActivity;
        if (i == R.id.acc_action_chat_preview && (dialogsActivity = this.parentFragment) != null) {
            dialogsActivity.showChatPreview(this);
            return true;
        }
        return super.performAccessibilityAction(i, bundle);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        PullForegroundDrawable pullForegroundDrawable;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (isFolderCell() && (pullForegroundDrawable = this.archivedChatsDrawable) != null && SharedConfig.archiveHidden && pullForegroundDrawable.pullProgress == 0.0f) {
            accessibilityNodeInfo.setVisibleToUser(false);
        } else {
            accessibilityNodeInfo.addAction(16);
            accessibilityNodeInfo.addAction(32);
            if (!isFolderCell() && this.parentFragment != null && Build.VERSION.SDK_INT >= 21) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_chat_preview, LocaleController.getString("AccActionChatPreview", R.string.AccActionChatPreview)));
            }
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null || !checkBox2.isChecked()) {
            return;
        }
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(true);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        MessageObject captionMessage;
        TLRPC$User user;
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        StringBuilder sb = new StringBuilder();
        if (this.currentDialogFolderId == 1) {
            sb.append(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
            sb.append(". ");
        } else {
            if (this.encryptedChat != null) {
                sb.append(LocaleController.getString("AccDescrSecretChat", R.string.AccDescrSecretChat));
                sb.append(". ");
            }
            TLRPC$User tLRPC$User = this.user;
            if (tLRPC$User != null) {
                if (UserObject.isReplyUser(tLRPC$User)) {
                    sb.append(LocaleController.getString("RepliesTitle", R.string.RepliesTitle));
                } else {
                    if (this.user.bot) {
                        sb.append(LocaleController.getString("Bot", R.string.Bot));
                        sb.append(". ");
                    }
                    TLRPC$User tLRPC$User2 = this.user;
                    if (tLRPC$User2.self) {
                        sb.append(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                    } else {
                        sb.append(ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name));
                    }
                }
                sb.append(". ");
            } else {
                TLRPC$Chat tLRPC$Chat = this.chat;
                if (tLRPC$Chat != null) {
                    if (tLRPC$Chat.broadcast) {
                        sb.append(LocaleController.getString("AccDescrChannel", R.string.AccDescrChannel));
                    } else {
                        sb.append(LocaleController.getString("AccDescrGroup", R.string.AccDescrGroup));
                    }
                    sb.append(". ");
                    sb.append(this.chat.title);
                    sb.append(". ");
                }
            }
        }
        if (this.drawVerified) {
            sb.append(LocaleController.getString("AccDescrVerified", R.string.AccDescrVerified));
            sb.append(". ");
        }
        int i = this.unreadCount;
        if (i > 0) {
            sb.append(LocaleController.formatPluralString("NewMessages", i, new Object[0]));
            sb.append(". ");
        }
        int i2 = this.mentionCount;
        if (i2 > 0) {
            sb.append(LocaleController.formatPluralString("AccDescrMentionCount", i2, new Object[0]));
            sb.append(". ");
        }
        if (this.reactionMentionCount > 0) {
            sb.append(LocaleController.getString("AccDescrMentionReaction", R.string.AccDescrMentionReaction));
            sb.append(". ");
        }
        MessageObject messageObject = this.message;
        if (messageObject == null || this.currentDialogFolderId != 0) {
            accessibilityEvent.setContentDescription(sb.toString());
            return;
        }
        int i3 = this.lastMessageDate;
        if (i3 == 0) {
            i3 = messageObject.messageOwner.date;
        }
        String formatDateAudio = LocaleController.formatDateAudio(i3, true);
        if (this.message.isOut()) {
            sb.append(LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, formatDateAudio));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, formatDateAudio));
        }
        sb.append(". ");
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.message.messageOwner.from_id.user_id))) != null) {
            sb.append(ContactsController.formatName(user.first_name, user.last_name));
            sb.append(". ");
        }
        if (this.encryptedChat == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && (captionMessage = getCaptionMessage()) != null && !TextUtils.isEmpty(captionMessage.caption)) {
                if (sb2.length() > 0) {
                    sb2.append(". ");
                }
                sb2.append(captionMessage);
            }
            StaticLayout staticLayout = this.messageLayout;
            int length = staticLayout == null ? -1 : staticLayout.getText().length();
            if (length > 0) {
                int length2 = sb2.length();
                int indexOf = sb2.indexOf("\n", length);
                if (indexOf < length2 && indexOf >= 0) {
                    length2 = indexOf;
                }
                int indexOf2 = sb2.indexOf("\t", length);
                if (indexOf2 < length2 && indexOf2 >= 0) {
                    length2 = indexOf2;
                }
                int indexOf3 = sb2.indexOf(" ", length);
                if (indexOf3 < length2 && indexOf3 >= 0) {
                    length2 = indexOf3;
                }
                sb.append(sb2.substring(0, length2));
            } else {
                sb.append((CharSequence) sb2);
            }
        }
        accessibilityEvent.setContentDescription(sb.toString());
    }

    private MessageObject getCaptionMessage() {
        CharSequence charSequence;
        if (this.groupMessages == null) {
            MessageObject messageObject = this.message;
            if (messageObject != null && messageObject.caption != null) {
                return messageObject;
            }
            return null;
        }
        MessageObject messageObject2 = null;
        int i = 0;
        for (int i2 = 0; i2 < this.groupMessages.size(); i2++) {
            MessageObject messageObject3 = this.groupMessages.get(i2);
            if (messageObject3 != null && (charSequence = messageObject3.caption) != null) {
                if (!TextUtils.isEmpty(charSequence)) {
                    i++;
                }
                messageObject2 = messageObject3;
            }
        }
        if (i <= 1) {
            return messageObject2;
        }
        return null;
    }

    public void updateMessageThumbs() {
        String str;
        String restrictionReason = MessagesController.getRestrictionReason(this.message.messageOwner.restriction_reason);
        ArrayList<MessageObject> arrayList = this.groupMessages;
        int i = 40;
        String str2 = "article";
        int i2 = 1;
        if (arrayList != null && arrayList.size() > 1 && TextUtils.isEmpty(restrictionReason) && this.currentDialogFolderId == 0 && this.encryptedChat == null) {
            this.thumbsCount = 0;
            this.hasVideoThumb = false;
            Collections.sort(this.groupMessages, DialogCell$$ExternalSyntheticLambda5.INSTANCE);
            int i3 = 0;
            while (i3 < this.groupMessages.size()) {
                MessageObject messageObject = this.groupMessages.get(i3);
                if (messageObject != null && !messageObject.needDrawBluredPreview() && (messageObject.isPhoto() || messageObject.isNewGif() || messageObject.isVideo() || messageObject.isRoundVideo())) {
                    String str3 = messageObject.isWebpage() ? messageObject.messageOwner.media.webpage.type : null;
                    if (!"app".equals(str3) && !"profile".equals(str3) && !str2.equals(str3) && (str3 == null || !str3.startsWith("telegram_"))) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, i);
                        TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize == closestPhotoSizeWithSize2) {
                            closestPhotoSizeWithSize2 = null;
                        }
                        if (closestPhotoSizeWithSize != null) {
                            this.hasVideoThumb = this.hasVideoThumb || messageObject.isVideo() || messageObject.isRoundVideo();
                            if (i3 < 2) {
                                this.thumbsCount += i2;
                                this.drawPlay[i3] = messageObject.isVideo() || messageObject.isRoundVideo();
                                str = str2;
                                this.thumbImage[i3].setImage(ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "20_20", ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), "20_20", (messageObject.type != i2 || closestPhotoSizeWithSize2 == null) ? 0 : closestPhotoSizeWithSize2.size, null, messageObject, 0);
                                this.thumbImage[i3].setRoundRadius(messageObject.isRoundVideo() ? AndroidUtilities.dp(18.0f) : AndroidUtilities.dp(2.0f));
                                this.needEmoji = false;
                                i3++;
                                str2 = str;
                                i = 40;
                                i2 = 1;
                            }
                        }
                    }
                }
                str = str2;
                i3++;
                str2 = str;
                i = 40;
                i2 = 1;
            }
            return;
        }
        MessageObject messageObject2 = this.message;
        if (messageObject2 == null || this.currentDialogFolderId != 0) {
            return;
        }
        this.thumbsCount = 0;
        this.hasVideoThumb = false;
        if (messageObject2.needDrawBluredPreview()) {
            return;
        }
        if (!this.message.isPhoto() && !this.message.isNewGif() && !this.message.isVideo() && !this.message.isRoundVideo()) {
            return;
        }
        String str4 = this.message.isWebpage() ? this.message.messageOwner.media.webpage.type : null;
        if ("app".equals(str4) || "profile".equals(str4) || str2.equals(str4)) {
            return;
        }
        if (str4 != null && str4.startsWith("telegram_")) {
            return;
        }
        TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 40);
        TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, AndroidUtilities.getPhotoSize());
        TLRPC$PhotoSize tLRPC$PhotoSize = closestPhotoSizeWithSize3 == closestPhotoSizeWithSize4 ? null : closestPhotoSizeWithSize4;
        if (closestPhotoSizeWithSize3 == null) {
            return;
        }
        this.hasVideoThumb = this.hasVideoThumb || this.message.isVideo() || this.message.isRoundVideo();
        int i4 = this.thumbsCount;
        if (i4 >= 3) {
            return;
        }
        this.thumbsCount = i4 + 1;
        this.drawPlay[0] = this.message.isVideo() || this.message.isRoundVideo();
        MessageObject messageObject3 = this.message;
        this.thumbImage[0].setImage(ImageLocation.getForObject(tLRPC$PhotoSize, messageObject3.photoThumbsObject), "20_20", ImageLocation.getForObject(closestPhotoSizeWithSize3, this.message.photoThumbsObject), "20_20", (messageObject3.type != 1 || tLRPC$PhotoSize == null) ? 0 : tLRPC$PhotoSize.size, null, this.message, 0);
        this.thumbImage[0].setRoundRadius(this.message.isRoundVideo() ? AndroidUtilities.dp(18.0f) : AndroidUtilities.dp(2.0f));
        this.needEmoji = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateMessageThumbs$6(MessageObject messageObject, MessageObject messageObject2) {
        return messageObject.getId() - messageObject2.getId();
    }

    public String getMessageNameString() {
        TLRPC$Chat chat;
        String str;
        TLRPC$Message tLRPC$Message;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        String str2;
        TLRPC$Message tLRPC$Message2;
        TLRPC$User user;
        MessageObject messageObject = this.message;
        TLRPC$User tLRPC$User = null;
        if (messageObject == null) {
            return null;
        }
        long fromChatId = messageObject.getFromChatId();
        if (DialogObject.isUserDialog(fromChatId)) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromChatId));
            chat = null;
        } else {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromChatId));
        }
        if (this.message.isOutOwner()) {
            return LocaleController.getString("FromYou", R.string.FromYou);
        }
        MessageObject messageObject2 = this.message;
        if (messageObject2 != null && (tLRPC$Message2 = messageObject2.messageOwner) != null && (tLRPC$Message2.from_id instanceof TLRPC$TL_peerUser) && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.message.messageOwner.from_id.user_id))) != null) {
            return UserObject.getFirstName(user).replace("\n", "");
        }
        MessageObject messageObject3 = this.message;
        if (messageObject3 != null && (tLRPC$Message = messageObject3.messageOwner) != null && (tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from) != null && (str2 = tLRPC$MessageFwdHeader.from_name) != null) {
            return str2;
        }
        if (tLRPC$User == null) {
            return (chat == null || (str = chat.title) == null) ? "DELETED" : str.replace("\n", "");
        } else if (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) {
            if (UserObject.isDeleted(tLRPC$User)) {
                return LocaleController.getString("HiddenName", R.string.HiddenName);
            }
            return ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name).replace("\n", "");
        } else {
            return UserObject.getFirstName(tLRPC$User).replace("\n", "");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0260  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0274 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r16v0, types: [org.telegram.ui.Cells.DialogCell, android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r2v5, types: [android.text.SpannableStringBuilder, java.lang.CharSequence, android.text.Spannable] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r3v11, types: [java.lang.CharSequence[]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.text.SpannableStringBuilder getMessageStringFormatted(java.lang.String r17, java.lang.String r18, java.lang.CharSequence r19, boolean r20) {
        /*
            Method dump skipped, instructions count: 821
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.getMessageStringFormatted(java.lang.String, java.lang.String, java.lang.CharSequence, boolean):android.text.SpannableStringBuilder");
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        CanvasButton canvasButton;
        DialogCellDelegate dialogCellDelegate = this.delegate;
        if ((dialogCellDelegate == null || dialogCellDelegate.canClickButtonInside()) && (canvasButton = this.canvasButton) != null && this.buttonLayout != null && canvasButton.checkTouchEvent(motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setClipProgress(float f) {
        this.clipProgress = f;
        invalidate();
    }

    public float getClipProgress() {
        return this.clipProgress;
    }

    public void setTopClip(int i) {
        this.topClip = i;
    }

    public void setBottomClip(int i) {
        this.bottomClip = i;
    }

    public void setArchivedPullAnimation(PullForegroundDrawable pullForegroundDrawable) {
        this.archivedChatsDrawable = pullForegroundDrawable;
    }

    public int getCurrentDialogFolderId() {
        return this.currentDialogFolderId;
    }

    public boolean isDialogFolder() {
        return this.currentDialogFolderId > 0;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public void setDialogCellDelegate(DialogCellDelegate dialogCellDelegate) {
        this.delegate = dialogCellDelegate;
    }
}
