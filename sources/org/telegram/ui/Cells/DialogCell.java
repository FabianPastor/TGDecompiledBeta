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
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
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
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.DialogsActivity;

public class DialogCell extends BaseCell {
    private int animateFromStatusDrawableParams;
    /* access modifiers changed from: private */
    public int animateToStatusDrawableParams;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
    private PullForegroundDrawable archivedChatsDrawable;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int bottomClip;
    private TLRPC$Chat chat;
    private float chatCallProgress;
    private CheckBox2 checkBox;
    private int checkDrawLeft;
    private int checkDrawLeft1;
    private int checkDrawTop;
    private boolean clearingDialog;
    private float clipProgress;
    private int clockDrawLeft;
    private float cornerProgress;
    /* access modifiers changed from: private */
    public StaticLayout countAnimationInLayout;
    private boolean countAnimationIncrement;
    /* access modifiers changed from: private */
    public StaticLayout countAnimationStableLayout;
    private ValueAnimator countAnimator;
    /* access modifiers changed from: private */
    public float countChangeProgress;
    private StaticLayout countLayout;
    private int countLeft;
    private int countLeftOld;
    /* access modifiers changed from: private */
    public StaticLayout countOldLayout;
    private int countTop;
    private int countWidth;
    private int countWidthOld;
    private int currentAccount;
    private int currentDialogFolderDialogsCount;
    private int currentDialogFolderId;
    private long currentDialogId;
    private int currentEditDate;
    private float currentRevealBounceProgress;
    private float currentRevealProgress;
    private CustomDialog customDialog;
    private boolean dialogMuted;
    private float dialogMutedProgress;
    private int dialogsType;
    private TLRPC$DraftMessage draftMessage;
    /* access modifiers changed from: private */
    public boolean drawCheck1;
    /* access modifiers changed from: private */
    public boolean drawCheck2;
    /* access modifiers changed from: private */
    public boolean drawClock;
    private boolean drawCount;
    private boolean drawCount2;
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawPlay;
    private boolean drawPremium;
    private boolean drawReactionMention;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private int drawScam;
    private boolean drawVerified;
    public boolean drawingForBlur;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emojiStatus;
    private TLRPC$EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
    private Paint fadePaint;
    private Paint fadePaintBack;
    private int folderId;
    public boolean fullSeparator;
    public boolean fullSeparator2;
    private int halfCheckDrawLeft;
    private boolean hasCall;
    private boolean hasMessageThumb;
    private int index;
    private float innerProgress;
    private BounceInterpolator interpolator;
    private boolean isDialogCell;
    private boolean isSelected;
    private boolean isSliding;
    long lastDialogChangedTime;
    private int lastDrawSwipeMessageStringId;
    private RLottieDrawable lastDrawTranslationDrawable;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString;
    private int lastSendState;
    int lastSize;
    /* access modifiers changed from: private */
    public int lastStatusDrawableParams;
    private boolean lastUnreadState;
    private long lastUpdateTime;
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
    private int messageTop;
    boolean moving;
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
    /* access modifiers changed from: private */
    public float reactionsMentionsChangeProgress;
    private RectF rect;
    private float reorderIconProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private List<SpoilerEffect> spoilers;
    private Stack<SpoilerEffect> spoilersPool;
    /* access modifiers changed from: private */
    public boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private int statusDrawableLeft;
    private float statusDrawableProgress;
    public boolean swipeCanceled;
    private int swipeMessageTextId;
    private StaticLayout swipeMessageTextLayout;
    private int swipeMessageWidth;
    private ImageReceiver thumbImage;
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop;
    private int topClip;
    private boolean translationAnimationStarted;
    private RLottieDrawable translationDrawable;
    private float translationX;
    private int unreadCount;
    public boolean useForceThreeLines;
    private boolean useMeForMyMessages;
    public boolean useSeparator;
    private TLRPC$User user;

    public static class BounceInterpolator implements Interpolator {
        public float getInterpolation(float f) {
            if (f < 0.33f) {
                return (f / 0.33f) * 0.1f;
            }
            float f2 = f - 0.33f;
            return f2 < 0.33f ? 0.1f - ((f2 / 0.34f) * 0.15f) : (((f2 - 0.34f) / 0.33f) * 0.05f) - 89.6f;
        }
    }

    public static class CustomDialog {
        public int date;
        public int id;
        public boolean isMedia;
        public String message;
        public boolean muted;
        public String name;
        public boolean pinned;
        public boolean sent;
        public int type;
        public int unread_count;
        public boolean verified;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setMoving(boolean z) {
        this.moving = z;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public static class FixedWidthSpan extends ReplacementSpan {
        private int width;

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        }

        public FixedWidthSpan(int i) {
            this.width = i;
        }

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
        this(dialogsActivity, context, z, z2, UserConfig.selectedAccount, (Theme.ResourcesProvider) null);
    }

    public DialogCell(DialogsActivity dialogsActivity, Context context, boolean z, boolean z2, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.thumbImage = new ImageReceiver(this);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.spoilersPool = new Stack<>();
        this.spoilers = new ArrayList();
        this.drawCount2 = true;
        this.countChangeProgress = 1.0f;
        this.reactionsMentionsChangeProgress = 1.0f;
        this.rect = new RectF();
        this.lastStatusDrawableParams = -1;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = dialogsActivity;
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.thumbImage.setRoundRadius(AndroidUtilities.dp(2.0f));
        this.useForceThreeLines = z2;
        this.currentAccount = i;
        if (z) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider2);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox);
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(22.0f));
        this.emojiStatus = swapAnimatedEmojiDrawable;
        swapAnimatedEmojiDrawable.center = false;
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

    public void setDialog(CustomDialog customDialog2) {
        this.customDialog = customDialog2;
        this.messageId = 0;
        update(0);
        checkOnline();
        checkGroupCall();
        checkChatTheme();
    }

    private void checkOnline() {
        TLRPC$User user2;
        if (!(this.user == null || (user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.user.id))) == null)) {
            this.user = user2;
        }
        this.onlineProgress = isOnline() ? 1.0f : 0.0f;
    }

    private boolean isOnline() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null && !tLRPC$User.self) {
            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
            if (tLRPC$UserStatus != null && tLRPC$UserStatus.expires <= 0 && MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(this.user.id))) {
                return true;
            }
            TLRPC$UserStatus tLRPC$UserStatus2 = this.user.status;
            if (tLRPC$UserStatus2 == null || tLRPC$UserStatus2.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                return false;
            }
            return true;
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
        if (messageObject != null && (tLRPC$Message = messageObject.messageOwner) != null) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionSetChatTheme) && this.lastUnreadState) {
                ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.currentDialogId, ((TLRPC$TL_messageActionSetChatTheme) tLRPC$MessageAction).emoticon, false);
            }
        }
    }

    public void setDialog(long j, MessageObject messageObject, int i, boolean z) {
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
        update(0);
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSliding = false;
        this.drawRevealBackground = false;
        this.currentRevealProgress = 0.0f;
        this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
        this.avatarImage.onDetachedFromWindow();
        this.thumbImage.onDetachedFromWindow();
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
            this.translationDrawable.setProgress(0.0f);
            this.translationDrawable.setCallback((Drawable.Callback) null);
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
        AnimatedEmojiSpan.release((View) this, this.animatedEmojiStack);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.thumbImage.onAttachedToWindow();
        resetPinnedArchiveState();
        this.animatedEmojiStack = AnimatedEmojiSpan.update(0, (View) this, this.animatedEmojiStack, this.messageLayout);
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
        if (!this.drawPin || !this.drawReorder) {
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 78.0f : 72.0f) + (this.useSeparator ? 1 : 0));
        this.topClip = 0;
        this.bottomClip = getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                float f = 45.0f;
                if (LocaleController.isRTL) {
                    int i6 = i3 - i;
                    if (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) {
                        f = 43.0f;
                    }
                    i5 = i6 - AndroidUtilities.dp(f);
                } else {
                    if (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) {
                        f = 43.0f;
                    }
                    i5 = AndroidUtilities.dp(f);
                }
                int dp = AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 48.0f : 42.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(i5, dp, checkBox2.getMeasuredWidth() + i5, this.checkBox.getMeasuredHeight() + dp);
            }
            int measuredHeight = (getMeasuredHeight() + getMeasuredWidth()) << 16;
            if (measuredHeight != this.lastSize) {
                this.lastSize = measuredHeight;
                try {
                    buildLayout();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    public boolean getHasUnread() {
        return this.unreadCount != 0 || this.markUnread;
    }

    public boolean getIsMuted() {
        return this.dialogMuted;
    }

    public boolean getIsPinned() {
        return this.drawPin;
    }

    private CharSequence formatArchivedDialogNames() {
        TLRPC$User tLRPC$User;
        String str;
        ArrayList<TLRPC$Dialog> dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = dialogs.size();
        for (int i = 0; i < size; i++) {
            TLRPC$Dialog tLRPC$Dialog = dialogs.get(i);
            TLRPC$Chat tLRPC$Chat = null;
            if (DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                TLRPC$EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(tLRPC$Dialog.id)));
                tLRPC$User = encryptedChat2 != null ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat2.user_id)) : null;
            } else if (DialogObject.isUserDialog(tLRPC$Dialog.id)) {
                tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$Dialog.id));
            } else {
                tLRPC$Chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog.id));
                tLRPC$User = null;
            }
            if (tLRPC$Chat != null) {
                str = tLRPC$Chat.title.replace(10, ' ');
            } else if (tLRPC$User == null) {
                continue;
            } else if (UserObject.isDeleted(tLRPC$User)) {
                str = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                str = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name).replace(10, ' ');
            }
            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append(", ");
            }
            int length = spannableStringBuilder.length();
            int length2 = str.length() + length;
            spannableStringBuilder.append(str);
            if (tLRPC$Dialog.unread_count > 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived", this.resourcesProvider)), length, length2, 33);
            }
            if (spannableStringBuilder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:201:0x04c4, code lost:
        if (r2.post_messages == false) goto L_0x04a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x04d0, code lost:
        if (r2.kicked != false) goto L_0x04a0;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Removed duplicated region for block: B:1010:0x17bf  */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x17c9  */
    /* JADX WARNING: Removed duplicated region for block: B:1023:0x17fa  */
    /* JADX WARNING: Removed duplicated region for block: B:1051:0x1878  */
    /* JADX WARNING: Removed duplicated region for block: B:1052:0x1881  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:1062:0x18a7 A[Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1063:0x18b1 A[ADDED_TO_REGION, Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x18cd A[Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1083:0x18ff A[Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1098:0x1951 A[Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1099:0x1954 A[Catch:{ Exception -> 0x1987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x1992  */
    /* JADX WARNING: Removed duplicated region for block: B:1160:0x1b05  */
    /* JADX WARNING: Removed duplicated region for block: B:1204:0x1bc2 A[Catch:{ Exception -> 0x1bed }] */
    /* JADX WARNING: Removed duplicated region for block: B:1207:0x1bdd A[Catch:{ Exception -> 0x1bed }] */
    /* JADX WARNING: Removed duplicated region for block: B:1215:0x1bf9  */
    /* JADX WARNING: Removed duplicated region for block: B:1235:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0481  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04d6  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x04db  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0561  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x079a  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0CLASSNAME A[SYNTHETIC, Splitter:B:563:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x0cb4  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0ce2  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0d3e  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0fb0  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0fb8  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x0fc2  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0fca  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0fe7  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x0ffb  */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x110b  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x1161  */
    /* JADX WARNING: Removed duplicated region for block: B:795:0x116c  */
    /* JADX WARNING: Removed duplicated region for block: B:818:0x11c4  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x1204  */
    /* JADX WARNING: Removed duplicated region for block: B:825:0x1210  */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x1222  */
    /* JADX WARNING: Removed duplicated region for block: B:829:0x123c  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1254  */
    /* JADX WARNING: Removed duplicated region for block: B:836:0x1280  */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x1320  */
    /* JADX WARNING: Removed duplicated region for block: B:887:0x13ad A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:890:0x13ba A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x1413 A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:902:0x1416 A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:907:0x1440 A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:908:0x1449 A[Catch:{ Exception -> 0x1456 }] */
    /* JADX WARNING: Removed duplicated region for block: B:916:0x1464  */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x1516  */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x15c8  */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x15ed  */
    /* JADX WARNING: Removed duplicated region for block: B:939:0x161d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r40 = this;
            r1 = r40
            boolean r0 = r1.useForceThreeLines
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 18
            r5 = 1098907648(0x41800000, float:16.0)
            r6 = 1
            r7 = 0
            if (r0 != 0) goto L_0x005f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0015
            goto L_0x005f
        L_0x0015:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r8 = r0[r7]
            r0 = r0[r7]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            java.lang.String r10 = "chats_message"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r0.linkColor = r9
            r8.setColor(r9)
            r1.paintIndex = r7
            r0 = 19
            r8 = 19
            goto L_0x00a6
        L_0x005f:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r6]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r6]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r6]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r6]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            r0.setTextSize(r8)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r8 = r0[r6]
            r0 = r0[r6]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            java.lang.String r10 = "chats_message_threeLines"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r0.linkColor = r9
            r8.setColor(r9)
            r1.paintIndex = r6
            r8 = 18
        L_0x00a6:
            r1.currentDialogFolderDialogsCount = r7
            boolean r0 = r1.isDialogCell
            if (r0 == 0) goto L_0x00b9
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r10 = r1.currentDialogId
            java.lang.CharSequence r0 = r0.getPrintingString(r10, r7, r6)
            goto L_0x00ba
        L_0x00b9:
            r0 = 0
        L_0x00ba:
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r10 = r10[r11]
            r1.drawNameLock = r7
            r1.drawVerified = r7
            r1.drawPremium = r7
            r1.drawScam = r7
            r1.drawPinBackground = r7
            r1.hasMessageThumb = r7
            r1.nameLayoutEllipsizeByGradient = r7
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 != 0) goto L_0x00dc
            boolean r11 = r1.useMeForMyMessages
            if (r11 != 0) goto L_0x00dc
            r11 = 1
            goto L_0x00dd
        L_0x00dc:
            r11 = 0
        L_0x00dd:
            r12 = -1
            r1.printingStringType = r12
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x00f6
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00ec
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x00f0
        L_0x00ec:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x00f3
        L_0x00f0:
            java.lang.String r13 = "%2$s: ⁨%1$s⁩"
            goto L_0x0104
        L_0x00f3:
            java.lang.String r13 = "⁨%s⁩"
            goto L_0x0108
        L_0x00f6:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00fe
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x0102
        L_0x00fe:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0106
        L_0x0102:
            java.lang.String r13 = "%2$s: %1$s"
        L_0x0104:
            r14 = 1
            goto L_0x0109
        L_0x0106:
            java.lang.String r13 = "%1$s"
        L_0x0108:
            r14 = 0
        L_0x0109:
            org.telegram.messenger.MessageObject r15 = r1.message
            if (r15 == 0) goto L_0x0110
            java.lang.CharSequence r15 = r15.messageText
            goto L_0x0111
        L_0x0110:
            r15 = 0
        L_0x0111:
            boolean r12 = r15 instanceof android.text.Spannable
            if (r12 == 0) goto L_0x014d
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r15)
            int r15 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderlineBold> r5 = org.telegram.ui.Components.URLSpanNoUnderlineBold.class
            java.lang.Object[] r5 = r12.getSpans(r7, r15, r5)
            org.telegram.ui.Components.URLSpanNoUnderlineBold[] r5 = (org.telegram.ui.Components.URLSpanNoUnderlineBold[]) r5
            int r15 = r5.length
            r2 = 0
        L_0x0128:
            if (r2 >= r15) goto L_0x0134
            r3 = r5[r2]
            r12.removeSpan(r3)
            int r2 = r2 + 1
            r3 = 1099431936(0x41880000, float:17.0)
            goto L_0x0128
        L_0x0134:
            int r2 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderline> r3 = org.telegram.ui.Components.URLSpanNoUnderline.class
            java.lang.Object[] r2 = r12.getSpans(r7, r2, r3)
            org.telegram.ui.Components.URLSpanNoUnderline[] r2 = (org.telegram.ui.Components.URLSpanNoUnderline[]) r2
            int r3 = r2.length
            r5 = 0
        L_0x0142:
            if (r5 >= r3) goto L_0x014c
            r15 = r2[r5]
            r12.removeSpan(r15)
            int r5 = r5 + 1
            goto L_0x0142
        L_0x014c:
            r15 = r12
        L_0x014d:
            r1.lastMessageString = r15
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            r3 = 1102053376(0x41b00000, float:22.0)
            r5 = 1117257728(0x42980000, float:76.0)
            r12 = 1117519872(0x429CLASSNAME, float:78.0)
            r21 = 1101004800(0x41a00000, float:20.0)
            r22 = 1099956224(0x41900000, float:18.0)
            java.lang.String r9 = ""
            r4 = 2
            if (r2 == 0) goto L_0x02f5
            int r0 = r2.type
            if (r0 != r4) goto L_0x01e6
            r1.drawNameLock = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x01ab
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x016f
            goto L_0x01ab
        L_0x016f:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0192
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r0
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x0216
        L_0x0192:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0216
        L_0x01ab:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01cd
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockLeft = r0
            r0 = 1118044160(0x42a40000, float:82.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x0216
        L_0x01cd:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r0
            goto L_0x0216
        L_0x01e6:
            boolean r0 = r2.verified
            r1.drawVerified = r0
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0205
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01f3
            goto L_0x0205
        L_0x01f3:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01fe
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r0
            goto L_0x0216
        L_0x01fe:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0216
        L_0x0205:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0210
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLeft = r0
            goto L_0x0216
        L_0x0210:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r0
        L_0x0216:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r2 = r0.type
            if (r2 != r6) goto L_0x02a3
            int r0 = org.telegram.messenger.R.string.FromYou
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            boolean r11 = r2.isMedia
            if (r11 == 0) goto L_0x0253
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r10 = r2[r10]
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.messenger.MessageObject r11 = r1.message
            java.lang.CharSequence r11 = r11.messageText
            r2[r7] = r11
            java.lang.String r2 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            org.telegram.ui.Components.ForegroundColorSpanThemable r11 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider
            java.lang.String r14 = "chats_attachMessage"
            r11.<init>(r14, r13)
            int r13 = r2.length()
            r14 = 33
            r2.setSpan(r11, r7, r13, r14)
            goto L_0x028f
        L_0x0253:
            java.lang.String r2 = r2.message
            int r11 = r2.length()
            r14 = 150(0x96, float:2.1E-43)
            if (r11 <= r14) goto L_0x0261
            java.lang.String r2 = r2.substring(r7, r14)
        L_0x0261:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0281
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x026a
            goto L_0x0281
        L_0x026a:
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r14 = 10
            r15 = 32
            java.lang.String r2 = r2.replace(r14, r15)
            r11[r7] = r2
            r11[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r11)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x028f
        L_0x0281:
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r7] = r2
            r11[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r11)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x028f:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r13 = r1.paintIndex
            r11 = r11[r13]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r11, r13, r7)
            r11 = 0
            goto L_0x02b1
        L_0x02a3:
            java.lang.String r2 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x02af
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r10 = r0[r10]
        L_0x02af:
            r0 = 0
            r11 = 1
        L_0x02b1:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            org.telegram.ui.Cells.DialogCell$CustomDialog r14 = r1.customDialog
            int r14 = r14.unread_count
            if (r14 == 0) goto L_0x02d1
            r1.drawCount = r6
            java.lang.Object[] r15 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r15[r7] = r14
            java.lang.String r14 = "%d"
            java.lang.String r14 = java.lang.String.format(r14, r15)
            goto L_0x02d4
        L_0x02d1:
            r1.drawCount = r7
            r14 = 0
        L_0x02d4:
            org.telegram.ui.Cells.DialogCell$CustomDialog r15 = r1.customDialog
            boolean r4 = r15.sent
            if (r4 == 0) goto L_0x02df
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            goto L_0x02e3
        L_0x02df:
            r1.drawCheck1 = r7
            r1.drawCheck2 = r7
        L_0x02e3:
            r1.drawClock = r7
            r1.drawError = r7
            java.lang.String r4 = r15.name
            r3 = r2
            r29 = r8
            r7 = r11
            r5 = 0
            r8 = 0
            r12 = -1
            r2 = r0
            r11 = r4
            r4 = 1
            goto L_0x11c2
        L_0x02f5:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0310
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02fe
            goto L_0x0310
        L_0x02fe:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0309
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r2
            goto L_0x0321
        L_0x0309:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x0321
        L_0x0310:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x031b
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLeft = r2
            goto L_0x0321
        L_0x031b:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r2
        L_0x0321:
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 == 0) goto L_0x03ae
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x046f
            r1.drawNameLock = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0371
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0334
            goto L_0x0371
        L_0x0334:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0357
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r2
            r2 = 1117782016(0x42a00000, float:80.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 + r4
            r1.nameLeft = r2
            goto L_0x046f
        L_0x0357:
            int r2 = r40.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 - r4
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x046f
        L_0x0371:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0394
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockLeft = r2
            r2 = 1118044160(0x42a40000, float:82.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 + r4
            r1.nameLeft = r2
            goto L_0x046f
        L_0x0394:
            int r2 = r40.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 - r4
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r2
            goto L_0x046f
        L_0x03ae:
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x046f
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x03d7
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x03c3
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x046f
        L_0x03c3:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x03d1
            r4 = 2
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x046f
        L_0x03d1:
            boolean r2 = r2.verified
            r1.drawVerified = r2
            goto L_0x046f
        L_0x03d7:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x046f
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x03e7
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x03f8
        L_0x03e7:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x03f4
            r4 = 2
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x03f8
        L_0x03f4:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x03f8:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            boolean r2 = r2.isPremiumUser(r4)
            if (r2 == 0) goto L_0x0420
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            long r3 = r2.clientUserId
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            r25 = r13
            long r12 = r2.id
            int r2 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x0422
            r2 = 0
            int r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0422
            r2 = 1
            goto L_0x0423
        L_0x0420:
            r25 = r13
        L_0x0422:
            r2 = 0
        L_0x0423:
            r1.drawPremium = r2
            if (r2 == 0) goto L_0x0471
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            org.telegram.tgnet.TLRPC$EmojiStatus r2 = r2.emoji_status
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatusUntil
            if (r3 == 0) goto L_0x044d
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r2 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r2
            int r2 = r2.until
            long r3 = java.lang.System.currentTimeMillis()
            r12 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 / r12
            int r4 = (int) r3
            if (r2 <= r4) goto L_0x044d
            r1.nameLayoutEllipsizeByGradient = r6
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r2 = r1.emojiStatus
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            org.telegram.tgnet.TLRPC$EmojiStatus r3 = r3.emoji_status
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r3 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r3
            long r3 = r3.document_id
            r2.set((long) r3, (boolean) r7)
            goto L_0x0471
        L_0x044d:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            org.telegram.tgnet.TLRPC$EmojiStatus r2 = r2.emoji_status
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatus
            if (r3 == 0) goto L_0x0461
            r1.nameLayoutEllipsizeByGradient = r6
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r3 = r1.emojiStatus
            org.telegram.tgnet.TLRPC$TL_emojiStatus r2 = (org.telegram.tgnet.TLRPC$TL_emojiStatus) r2
            long r12 = r2.document_id
            r3.set((long) r12, (boolean) r7)
            goto L_0x0471
        L_0x0461:
            r1.nameLayoutEllipsizeByGradient = r6
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r2 = r1.emojiStatus
            org.telegram.ui.Components.Premium.PremiumGradient r3 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r3 = r3.premiumStarDrawableMini
            r2.set((android.graphics.drawable.Drawable) r3, (boolean) r7)
            goto L_0x0471
        L_0x046f:
            r25 = r13
        L_0x0471:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x047d
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x047d
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            int r2 = r2.date
        L_0x047d:
            boolean r3 = r1.isDialogCell
            if (r3 == 0) goto L_0x04d6
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            long r12 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r3.getDraft(r12, r7)
            r1.draftMessage = r3
            if (r3 == 0) goto L_0x04ac
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x04a2
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.reply_to_msg_id
            if (r3 == 0) goto L_0x04a0
            goto L_0x04a2
        L_0x04a0:
            r2 = 0
            goto L_0x04d3
        L_0x04a2:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.date
            if (r2 <= r3) goto L_0x04ac
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x04a0
        L_0x04ac:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x04c6
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x04c6
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x04c6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x04a0
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x04a0
        L_0x04c6:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x04d9
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x04a0
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x04d9
            goto L_0x04a0
        L_0x04d3:
            r1.draftMessage = r2
            goto L_0x04d9
        L_0x04d6:
            r2 = 0
            r1.draftMessage = r2
        L_0x04d9:
            if (r0 == 0) goto L_0x0561
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r3, r7)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0501
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x0502
        L_0x0501:
            r2 = 0
        L_0x0502:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>()
            java.lang.String[] r4 = new java.lang.String[r6]
            java.lang.String r10 = "..."
            r4[r7] = r10
            java.lang.String[] r10 = new java.lang.String[r6]
            r10[r7] = r9
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r4, r10)
            int r4 = r1.printingStringType
            r10 = 5
            if (r4 != r10) goto L_0x0525
            java.lang.String r4 = r0.toString()
            java.lang.String r10 = "**oo**"
            int r12 = r4.indexOf(r10)
            goto L_0x0526
        L_0x0525:
            r12 = -1
        L_0x0526:
            if (r12 < 0) goto L_0x0541
            android.text.SpannableStringBuilder r0 = r3.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r4 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r4 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r4)
            int r4 = r4.getIntrinsicWidth()
            r2.<init>(r4)
            int r4 = r12 + 6
            r0.setSpan(r2, r12, r4, r7)
            goto L_0x0553
        L_0x0541:
            java.lang.String r4 = " "
            android.text.SpannableStringBuilder r4 = r3.append(r4)
            android.text.SpannableStringBuilder r0 = r4.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r4.<init>(r2)
            r0.setSpan(r4, r7, r6, r7)
        L_0x0553:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r29 = r8
            r0 = 0
            r2 = 2
            r4 = 1
            r8 = 0
            goto L_0x0fbe
        L_0x0561:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            r2 = 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x0611
            int r0 = org.telegram.messenger.R.string.Draft
            java.lang.String r3 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x05a5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x059e
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0585
            goto L_0x059e
        L_0x0585:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r12 = "chats_draft"
            r2.<init>(r12, r4)
            int r4 = r0.length()
            r12 = 33
            r3.setSpan(r2, r7, r4, r12)
        L_0x059b:
            r29 = r8
            goto L_0x05a1
        L_0x059e:
            r29 = r8
            r3 = r9
        L_0x05a1:
            r2 = 2
            r4 = 1
            goto L_0x062b
        L_0x05a5:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r12 = 150(0x96, float:2.1E-43)
            if (r4 <= r12) goto L_0x05b5
            java.lang.String r3 = r3.substring(r7, r12)
        L_0x05b5:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r3)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC$DraftMessage) r3, (android.text.Spannable) r4, (int) r2)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            if (r2 == 0) goto L_0x05d2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            if (r2 == 0) goto L_0x05d2
            if (r10 != 0) goto L_0x05cb
            r3 = 0
            goto L_0x05cf
        L_0x05cb:
            android.graphics.Paint$FontMetricsInt r3 = r10.getFontMetricsInt()
        L_0x05cf:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r2, r4, r3)
        L_0x05d2:
            r2 = 2
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r2]
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            r3[r7] = r2
            r3[r6] = r0
            r13 = r25
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r3)
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x05fe
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x05fe
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r12 = "chats_draft"
            r3.<init>(r12, r4)
            int r4 = r0.length()
            int r4 = r4 + r6
            r12 = 33
            r2.setSpan(r3, r7, r4, r12)
        L_0x05fe:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r3 = r3[r4]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r7)
            goto L_0x059b
        L_0x0611:
            r13 = r25
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x062f
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            int r0 = org.telegram.messenger.R.string.HistoryCleared
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0625:
            r29 = r8
        L_0x0627:
            r0 = 0
            r2 = 2
            r4 = 1
            r7 = 1
        L_0x062b:
            r8 = 0
        L_0x062c:
            r12 = -1
            goto L_0x0fbe
        L_0x062f:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x06c2
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x069e
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x064a
            int r0 = org.telegram.messenger.R.string.EncryptionProcessing
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x064a:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0661
            int r0 = org.telegram.messenger.R.string.AwaitingEncryption
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x0625
        L_0x0661:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x066e
            int r0 = org.telegram.messenger.R.string.EncryptionRejected
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x066e:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x06bd
            long r2 = r0.admin_id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r12 = r0.getClientUserId()
            int r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x0695
            int r0 = org.telegram.messenger.R.string.EncryptedChatStartedOutgoing
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x0625
        L_0x0695:
            int r0 = org.telegram.messenger.R.string.EncryptedChatStartedIncoming
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0625
        L_0x069e:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x06bd
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x06bd
            int r0 = org.telegram.messenger.R.string.SavedMessagesInfo
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r29 = r8
            r0 = 0
            r2 = 2
            r4 = 0
            r7 = 1
            r8 = 0
            r11 = 0
            goto L_0x062c
        L_0x06bd:
            r29 = r8
            r3 = r9
            goto L_0x0627
        L_0x06c2:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r3 = r1.message
            long r3 = r3.getFromChatId()
            boolean r12 = org.telegram.messenger.DialogObject.isUserDialog(r3)
            if (r12 == 0) goto L_0x06e6
            int r12 = r1.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r12.getUser(r3)
            r4 = 0
            goto L_0x06f7
        L_0x06e6:
            int r12 = r1.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r12.getChat(r3)
            r4 = r3
            r3 = 0
        L_0x06f7:
            r1.drawCount2 = r6
            r25 = r3
            long r2 = r1.currentDialogId
            r26 = 0
            int r28 = (r2 > r26 ? 1 : (r2 == r26 ? 0 : -1))
            if (r28 <= 0) goto L_0x0792
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isOutOwner()
            if (r2 == 0) goto L_0x0792
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r2 = r2.reactions
            if (r2 == 0) goto L_0x0792
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessagePeerReaction> r2 = r2.recent_reactions
            if (r2 == 0) goto L_0x0792
            java.lang.Object r2 = r2.get(r7)
            org.telegram.tgnet.TLRPC$MessagePeerReaction r2 = (org.telegram.tgnet.TLRPC$MessagePeerReaction) r2
            org.telegram.tgnet.TLRPC$Peer r3 = r2.peer_id
            r26 = r13
            long r12 = r3.user_id
            r28 = 0
            int r3 = (r12 > r28 ? 1 : (r12 == r28 ? 0 : -1))
            if (r3 == 0) goto L_0x078f
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            r29 = r8
            long r7 = r3.clientUserId
            int r3 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r3 == 0) goto L_0x0796
            org.telegram.tgnet.TLRPC$Reaction r2 = r2.reaction
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(r2)
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r7 = r1.paintIndex
            r10 = r3[r7]
            java.lang.String r3 = r2.emojicon
            if (r3 == 0) goto L_0x0756
            int r2 = org.telegram.messenger.R.string.ReactionInDialog
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r8 = 0
            r7[r8] = r3
            java.lang.String r3 = "ReactionInDialog"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r7)
        L_0x0754:
            r3 = 1
            goto L_0x0798
        L_0x0756:
            r8 = 0
            int r3 = org.telegram.messenger.R.string.ReactionInDialog
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.String r12 = "**reaction**"
            r7[r8] = r12
            java.lang.String r8 = "ReactionInDialog"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r8, r3, r7)
            java.lang.String r7 = "**reaction**"
            int r7 = r3.indexOf(r7)
            java.lang.String r8 = "**reaction**"
            java.lang.String r12 = "d"
            java.lang.String r3 = r3.replace(r8, r12)
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r3)
            org.telegram.ui.Components.AnimatedEmojiSpan r3 = new org.telegram.ui.Components.AnimatedEmojiSpan
            long r12 = r2.documentId
            if (r10 != 0) goto L_0x0780
            r2 = 0
            goto L_0x0784
        L_0x0780:
            android.graphics.Paint$FontMetricsInt r2 = r10.getFontMetricsInt()
        L_0x0784:
            r3.<init>((long) r12, (android.graphics.Paint.FontMetricsInt) r2)
            int r2 = r7 + 1
            r12 = 0
            r8.setSpan(r3, r7, r2, r12)
            r2 = r8
            goto L_0x0754
        L_0x078f:
            r29 = r8
            goto L_0x0796
        L_0x0792:
            r29 = r8
            r26 = r13
        L_0x0796:
            r2 = r9
            r3 = 0
        L_0x0798:
            if (r3 == 0) goto L_0x07a2
            r3 = r2
        L_0x079b:
            r0 = 0
            r2 = 2
        L_0x079d:
            r4 = 1
            r7 = 1
        L_0x079f:
            r8 = 0
            goto L_0x0fb4
        L_0x07a2:
            int r2 = r1.dialogsType
            r3 = 2
            if (r2 != r3) goto L_0x0825
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0820
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x07e4
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x07e4
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x07c2
            java.lang.String r0 = "Subscribers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0821
        L_0x07c2:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x07d7
            int r0 = org.telegram.messenger.R.string.ChannelPrivate
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0821
        L_0x07d7:
            int r0 = org.telegram.messenger.R.string.ChannelPublic
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0821
        L_0x07e4:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x07f1
            java.lang.String r0 = "Members"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0821
        L_0x07f1:
            boolean r2 = r0.has_geo
            if (r2 == 0) goto L_0x07fe
            int r0 = org.telegram.messenger.R.string.MegaLocation
            java.lang.String r2 = "MegaLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0821
        L_0x07fe:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0813
            int r0 = org.telegram.messenger.R.string.MegaPrivate
            java.lang.String r2 = "MegaPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0821
        L_0x0813:
            int r0 = org.telegram.messenger.R.string.MegaPublic
            java.lang.String r2 = "MegaPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0821
        L_0x0820:
            r0 = r9
        L_0x0821:
            r2 = 0
            r1.drawCount2 = r2
            goto L_0x0838
        L_0x0825:
            r3 = 3
            if (r2 != r3) goto L_0x0841
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r2 == 0) goto L_0x0841
            int r0 = org.telegram.messenger.R.string.SavedMessagesInfo
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0838:
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 0
            r7 = 1
            r8 = 0
            r11 = 0
            goto L_0x0fb4
        L_0x0841:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0858
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 != 0) goto L_0x0858
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0858
            java.lang.CharSequence r0 = r40.formatArchivedDialogNames()
            r3 = r0
            r0 = 0
            r2 = 2
        L_0x0854:
            r4 = 1
            r7 = 0
            goto L_0x079f
        L_0x0858:
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r2 == 0) goto L_0x087e
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 == 0) goto L_0x0874
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0874
            r15 = r9
            r11 = 0
        L_0x0874:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r0 = r0[r2]
            r10 = r0
            r3 = r15
            goto L_0x079b
        L_0x087e:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x097c
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x097c
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 != 0) goto L_0x097c
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.needDrawBluredPreview()
            if (r2 != 0) goto L_0x097c
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 != 0) goto L_0x08ac
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isNewGif()
            if (r2 != 0) goto L_0x08ac
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x097c
        L_0x08ac:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isWebpage()
            if (r2 == 0) goto L_0x08bf
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            java.lang.String r2 = r2.type
            goto L_0x08c0
        L_0x08bf:
            r2 = 0
        L_0x08c0:
            java.lang.String r3 = "app"
            boolean r3 = r3.equals(r2)
            if (r3 != 0) goto L_0x097c
            java.lang.String r3 = "profile"
            boolean r3 = r3.equals(r2)
            if (r3 != 0) goto L_0x097c
            java.lang.String r3 = "article"
            boolean r3 = r3.equals(r2)
            if (r3 != 0) goto L_0x097c
            if (r2 == 0) goto L_0x08e2
            java.lang.String r3 = "telegram_"
            boolean r2 = r2.startsWith(r3)
            if (r2 != 0) goto L_0x097c
        L_0x08e2:
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.photoThumbs
            r3 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.photoThumbs
            int r7 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7)
            if (r2 != r3) goto L_0x08fb
            r3 = 0
        L_0x08fb:
            if (r2 == 0) goto L_0x097c
            r1.hasMessageThumb = r6
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isVideo()
            r1.drawPlay = r7
            java.lang.String r7 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.mediaExists
            if (r8 != 0) goto L_0x094a
            int r8 = r1.currentAccount
            org.telegram.messenger.DownloadController r8 = org.telegram.messenger.DownloadController.getInstance(r8)
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r8 = r8.canDownloadMedia((org.telegram.messenger.MessageObject) r12)
            if (r8 != 0) goto L_0x094a
            int r8 = r1.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r8)
            boolean r7 = r8.isLoadingFile(r7)
            if (r7 == 0) goto L_0x092c
            goto L_0x094a
        L_0x092c:
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            r31 = 0
            r32 = 0
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLObject r7 = r7.photoThumbsObject
            org.telegram.messenger.ImageLocation r33 = org.telegram.messenger.ImageLocation.getForObject(r2, r7)
            r35 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r37 = 0
            java.lang.String r34 = "20_20"
            r30 = r3
            r36 = r2
            r30.setImage((org.telegram.messenger.ImageLocation) r31, (java.lang.String) r32, (org.telegram.messenger.ImageLocation) r33, (java.lang.String) r34, (android.graphics.drawable.Drawable) r35, (java.lang.Object) r36, (int) r37)
            goto L_0x097a
        L_0x094a:
            org.telegram.messenger.MessageObject r7 = r1.message
            int r8 = r7.type
            if (r8 != r6) goto L_0x0955
            if (r3 == 0) goto L_0x0955
            int r8 = r3.size
            goto L_0x0956
        L_0x0955:
            r8 = 0
        L_0x0956:
            org.telegram.messenger.ImageReceiver r12 = r1.thumbImage
            org.telegram.tgnet.TLObject r7 = r7.photoThumbsObject
            org.telegram.messenger.ImageLocation r31 = org.telegram.messenger.ImageLocation.getForObject(r3, r7)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r33 = org.telegram.messenger.ImageLocation.getForObject(r2, r3)
            long r2 = (long) r8
            r37 = 0
            org.telegram.messenger.MessageObject r7 = r1.message
            r39 = 0
            java.lang.String r32 = "20_20"
            java.lang.String r34 = "20_20"
            r30 = r12
            r35 = r2
            r38 = r7
            r30.setImage(r31, r32, r33, r34, r35, r37, r38, r39)
        L_0x097a:
            r2 = 0
            goto L_0x097d
        L_0x097c:
            r2 = 1
        L_0x097d:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            r7 = 130(0x82, float:1.82E-43)
            if (r3 == 0) goto L_0x0d35
            long r12 = r3.id
            r30 = 0
            int r8 = (r12 > r30 ? 1 : (r12 == r30 ? 0 : -1))
            if (r8 <= 0) goto L_0x0d35
            if (r4 != 0) goto L_0x0d35
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x099b
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isMegagroup(r3)
            if (r3 == 0) goto L_0x0d35
        L_0x099b:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x09ac
            int r3 = org.telegram.messenger.R.string.FromYou
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x09f3
        L_0x09ac:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x09bb
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            if (r3 == 0) goto L_0x09bb
            java.lang.String r3 = r3.from_name
            if (r3 == 0) goto L_0x09bb
            goto L_0x09f3
        L_0x09bb:
            if (r25 == 0) goto L_0x09f1
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x09d1
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09c6
            goto L_0x09d1
        L_0x09c6:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r25)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x09f3
        L_0x09d1:
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r25)
            if (r3 == 0) goto L_0x09e0
            int r3 = org.telegram.messenger.R.string.HiddenName
            java.lang.String r4 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x09f3
        L_0x09e0:
            r3 = r25
            java.lang.String r4 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r4, r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x09f3
        L_0x09f1:
            java.lang.String r3 = "DELETED"
        L_0x09f3:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x0a0e
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r4 = 0
            r2[r4] = r0
            r2[r6] = r3
            r13 = r26
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0a0b:
            r2 = r0
            goto L_0x0CLASSNAME
        L_0x0a0e:
            r13 = r26
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0af8
            java.lang.String r0 = r4.toString()
            if (r2 != 0) goto L_0x0a1e
            r2 = r9
            goto L_0x0a4c
        L_0x0a1e:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0a29
            java.lang.String r2 = "📹 "
            goto L_0x0a4c
        L_0x0a29:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0a34
            java.lang.String r2 = "🎤 "
            goto L_0x0a4c
        L_0x0a34:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0a3f
            java.lang.String r2 = "🎧 "
            goto L_0x0a4c
        L_0x0a3f:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x0a4a
            java.lang.String r2 = "🖼 "
            goto L_0x0a4c
        L_0x0a4a:
            java.lang.String r2 = "📎 "
        L_0x0a4c:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0aab
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0aab
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r4 = r40.getMeasuredWidth()
            r8 = 1122893824(0x42ee0000, float:119.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            if (r14 == 0) goto L_0x0a8b
            boolean r8 = android.text.TextUtils.isEmpty(r3)
            if (r8 != 0) goto L_0x0a82
            float r4 = (float) r4
            java.lang.String r8 = r3.toString()
            float r8 = r10.measureText(r8)
            float r4 = r4 - r8
            int r4 = (int) r4
        L_0x0a82:
            float r4 = (float) r4
            java.lang.String r8 = ": "
            float r8 = r10.measureText(r8)
            float r4 = r4 - r8
            int r4 = (int) r4
        L_0x0a8b:
            if (r4 <= 0) goto L_0x0aa0
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            r12 = 0
            java.lang.Object r8 = r8.get(r12)
            java.lang.String r8 = (java.lang.String) r8
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r8, r4, r10, r7)
            java.lang.String r0 = r0.toString()
        L_0x0aa0:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r2)
            android.text.SpannableStringBuilder r0 = r4.append(r0)
            goto L_0x0a0b
        L_0x0aab:
            int r4 = r0.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r4 <= r7) goto L_0x0ab8
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r7)
        L_0x0ab8:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r0)
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r7.entities
            r8 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r7, r0, r4, r8)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x0add
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            if (r0 == 0) goto L_0x0add
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            if (r10 != 0) goto L_0x0ad6
            r7 = 0
            goto L_0x0ada
        L_0x0ad6:
            android.graphics.Paint$FontMetricsInt r7 = r10.getFontMetricsInt()
        L_0x0ada:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r0, r4, r7)
        L_0x0add:
            r7 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r7]
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            android.text.SpannableStringBuilder r2 = r7.append(r2)
            r4 = 0
            r0[r4] = r2
            r0[r6] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r0)
            goto L_0x0a0b
        L_0x0af8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0be9
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0be9
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0b3e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b2d
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bb1
        L_0x0b2d:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r4] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bb1
        L_0x0b3e:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0b68
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b58
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bb1
        L_0x0b58:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r4] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bb1
        L_0x0b68:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0b6f
            java.lang.String r0 = r2.title
            goto L_0x0bb1
        L_0x0b6f:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0bad
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r2 < r4) goto L_0x0b94
            r2 = 2
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r7 = 0
            r4[r7] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r6] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0bb1
        L_0x0b94:
            r2 = 2
            r7 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r4[r7] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r6] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0bb1
        L_0x0bad:
            java.lang.String r0 = r15.toString()
        L_0x0bb1:
            r2 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r2, r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0be3 }
            java.lang.String r4 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x0be3 }
            r0.<init>(r4, r7)     // Catch:{ Exception -> 0x0be3 }
            if (r14 == 0) goto L_0x0bd7
            int r4 = r3.length()     // Catch:{ Exception -> 0x0be3 }
            r7 = 2
            int r4 = r4 + r7
            goto L_0x0bd8
        L_0x0bd7:
            r4 = 0
        L_0x0bd8:
            int r7 = r2.length()     // Catch:{ Exception -> 0x0be3 }
            r8 = 33
            r2.setSpan(r0, r4, r7, r8)     // Catch:{ Exception -> 0x0be3 }
            goto L_0x0CLASSNAME
        L_0x0be3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0CLASSNAME
        L_0x0be9:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0c7d
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0c3d
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0bfe
            r2 = r0
        L_0x0bfe:
            int r0 = r40.getMeasuredWidth()
            r4 = 1121058816(0x42d20000, float:105.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            if (r14 == 0) goto L_0x0CLASSNAME
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0c1c
            float r0 = (float) r0
            java.lang.String r4 = r3.toString()
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0c1c:
            float r0 = (float) r0
            java.lang.String r4 = ": "
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0CLASSNAME:
            if (r0 <= 0) goto L_0x0c3b
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r8 = 0
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r0, r10, r7)
            java.lang.String r2 = r0.toString()
            goto L_0x0c4e
        L_0x0c3b:
            r8 = 0
            goto L_0x0c4e
        L_0x0c3d:
            r8 = 0
            int r0 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r0 <= r4) goto L_0x0c4a
            java.lang.CharSequence r2 = r2.subSequence(r8, r4)
        L_0x0c4a:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r2)
        L_0x0c4e:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r2, (android.text.Spannable) r0, (int) r4)
            org.telegram.messenger.MessageObject r2 = r1.message
            if (r2 == 0) goto L_0x0c6f
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            if (r2 == 0) goto L_0x0c6f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            if (r10 != 0) goto L_0x0CLASSNAME
            r4 = 0
            goto L_0x0c6c
        L_0x0CLASSNAME:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0c6c:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r2, r0, r4)
        L_0x0c6f:
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            goto L_0x0a0b
        L_0x0c7d:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r9)
            goto L_0x0a0b
        L_0x0CLASSNAME:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0c8b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0CLASSNAME
        L_0x0c8b:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0cb4
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0cb4
        L_0x0CLASSNAME:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0cad }
            java.lang.String r4 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x0cad }
            r0.<init>(r4, r7)     // Catch:{ Exception -> 0x0cad }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0cad }
            int r4 = r4 + r6
            r7 = 33
            r8 = 0
            r2.setSpan(r0, r8, r4, r7)     // Catch:{ Exception -> 0x0cab }
            r0 = r4
            goto L_0x0cb6
        L_0x0cab:
            r0 = move-exception
            goto L_0x0caf
        L_0x0cad:
            r0 = move-exception
            r4 = 0
        L_0x0caf:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0cb6
        L_0x0cb4:
            r0 = 0
            r4 = 0
        L_0x0cb6:
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r7 = r7[r8]
            android.graphics.Paint$FontMetricsInt r7 = r7.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r12 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r7, r8, r12)
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.hasHighlightedWords()
            if (r7 == 0) goto L_0x0cde
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<java.lang.String> r7 = r7.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            if (r7 == 0) goto L_0x0cde
            r2 = r7
        L_0x0cde:
            boolean r7 = r1.hasMessageThumb
            if (r7 == 0) goto L_0x0d2d
            boolean r7 = r2 instanceof android.text.SpannableStringBuilder
            if (r7 != 0) goto L_0x0cec
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r2)
            r2 = r7
        L_0x0cec:
            r7 = r2
            android.text.SpannableStringBuilder r7 = (android.text.SpannableStringBuilder) r7
            int r8 = r7.length()
            if (r4 < r8) goto L_0x0d15
            java.lang.String r4 = " "
            r7.append(r4)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r29 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r4.<init>(r8)
            int r8 = r7.length()
            int r8 = r8 - r6
            int r12 = r7.length()
            r13 = 33
            r7.setSpan(r4, r8, r12, r13)
            goto L_0x0d2d
        L_0x0d15:
            java.lang.String r8 = " "
            r7.insert(r4, r8)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r8 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r12 = r29 + 6
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r8.<init>(r12)
            int r12 = r4 + 1
            r13 = 33
            r7.setSpan(r8, r4, r12, r13)
        L_0x0d2d:
            r8 = r0
            r0 = r3
            r4 = 1
            r7 = 0
            r3 = r2
            r2 = 2
            goto L_0x0fb4
        L_0x0d35:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0d3e
        L_0x0d3b:
            r2 = 2
            goto L_0x0var_
        L_0x0d3e:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0d5b
            org.telegram.tgnet.TLRPC$Photo r4 = r3.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0d5b
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d5b
            int r0 = org.telegram.messenger.R.string.AttachPhotoExpired
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0d3b
        L_0x0d5b:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0d72
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0d72
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d72
            int r0 = org.telegram.messenger.R.string.AttachVideoExpired
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0d3b
        L_0x0d72:
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0e39
            if (r2 != 0) goto L_0x0d7a
            r0 = r9
            goto L_0x0da6
        L_0x0d7a:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0d83
            java.lang.String r0 = "📹 "
            goto L_0x0da6
        L_0x0d83:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0d8e
            java.lang.String r0 = "🎤 "
            goto L_0x0da6
        L_0x0d8e:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0d99
            java.lang.String r0 = "🎧 "
            goto L_0x0da6
        L_0x0d99:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0da4
            java.lang.String r0 = "🖼 "
            goto L_0x0da6
        L_0x0da4:
            java.lang.String r0 = "📎 "
        L_0x0da6:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0e03
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0e03
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            r4 = 1122893824(0x42ee0000, float:119.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r14 == 0) goto L_0x0ddd
            r4 = 0
            boolean r8 = android.text.TextUtils.isEmpty(r4)
            if (r8 == 0) goto L_0x0ddc
            float r3 = (float) r3
            java.lang.String r8 = ": "
            float r8 = r10.measureText(r8)
            float r3 = r3 - r8
            int r3 = (int) r3
            goto L_0x0ddd
        L_0x0ddc:
            throw r4
        L_0x0ddd:
            if (r3 <= 0) goto L_0x0df2
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r8 = 0
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r3, r10, r7)
            java.lang.String r2 = r2.toString()
        L_0x0df2:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0d3b
        L_0x0e03:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.caption
            r2.<init>(r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0e2e
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            if (r4 == 0) goto L_0x0e2e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.entities
            java.lang.CharSequence r3 = r3.caption
            r8 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r4, r3, r2, r8)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            if (r10 != 0) goto L_0x0e27
            r4 = 0
            goto L_0x0e2b
        L_0x0e27:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0e2b:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r3, r2, r4)
        L_0x0e2e:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            android.text.SpannableStringBuilder r0 = r3.append(r2)
            goto L_0x0d3b
        L_0x0e39:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0e57
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0e54:
            r2 = 2
            goto L_0x0efd
        L_0x0e57:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0e77
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "🎮 "
            r0.append(r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            goto L_0x0e54
        L_0x0e77:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0e7e
            java.lang.String r0 = r3.title
            goto L_0x0e54
        L_0x0e7e:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0e9d
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r4 = 0
            r3[r4] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r3[r6] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0efd
        L_0x0e9d:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0ed3
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0ed3
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            r4 = 1119748096(0x42be0000, float:95.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r8 = 0
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r4, r3, r10, r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0ef4
        L_0x0ed3:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r3, (android.text.Spannable) r0, (int) r4)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0ef4
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            if (r3 == 0) goto L_0x0ef4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            if (r10 != 0) goto L_0x0eed
            r4 = 0
            goto L_0x0ef1
        L_0x0eed:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0ef1:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r3, r0, r4)
        L_0x0ef4:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
        L_0x0efd:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            if (r4 == 0) goto L_0x0var_
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0var_
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r10 = r3[r4]
        L_0x0var_:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0fb0
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0f4f
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0f4f
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            int r8 = r29 + 95
            int r8 = r8 + 6
            float r4 = (float) r8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r8 = 0
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r4, r3, r10, r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0var_
        L_0x0f4f:
            r8 = 0
            int r3 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0f5c
            java.lang.CharSequence r0 = r0.subSequence(r8, r4)
        L_0x0f5c:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0var_:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0f6a
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0f6a:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r4 = " "
            r7 = 0
            r3.insert(r7, r4)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r29 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r4.<init>(r8)
            r8 = 33
            r3.setSpan(r4, r7, r6, r8)
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r4 = r4[r8]
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r8 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r12, r7)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0fac
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r3 == 0) goto L_0x0fac
            goto L_0x0fad
        L_0x0fac:
            r3 = r0
        L_0x0fad:
            r0 = 0
            goto L_0x0854
        L_0x0fb0:
            r3 = r0
            r0 = 0
            goto L_0x079d
        L_0x0fb4:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x062c
            java.lang.CharSequence r0 = r40.formatArchivedDialogNames()
            goto L_0x062c
        L_0x0fbe:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0fca
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0fe3
        L_0x0fca:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0fd4
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0fe3
        L_0x0fd4:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0fe2
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0fe3
        L_0x0fe2:
            r13 = r9
        L_0x0fe3:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0ffb
            r15 = 0
            r1.drawCheck1 = r15
            r1.drawCheck2 = r15
            r1.drawClock = r15
            r1.drawCount = r15
            r1.drawMention = r15
            r1.drawReactionMention = r15
            r1.drawError = r15
            r2 = 0
            r5 = 0
        L_0x0ff8:
            r11 = 0
            goto L_0x10ff
        L_0x0ffb:
            r15 = 0
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x103d
            int r2 = r1.unreadCount
            int r14 = r1.mentionCount
            int r16 = r2 + r14
            if (r16 <= 0) goto L_0x1034
            if (r2 <= r14) goto L_0x101e
            r1.drawCount = r6
            r1.drawMention = r15
            java.lang.Object[] r5 = new java.lang.Object[r6]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r5[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r5)
            goto L_0x1039
        L_0x101e:
            r1.drawCount = r15
            r1.drawMention = r6
            java.lang.Object[] r5 = new java.lang.Object[r6]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r5[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r5)
            r5 = r2
            r2 = 0
            goto L_0x103a
        L_0x1034:
            r1.drawCount = r15
            r1.drawMention = r15
            r2 = 0
        L_0x1039:
            r5 = 0
        L_0x103a:
            r1.drawReactionMention = r15
            goto L_0x108c
        L_0x103d:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x1047
            r1.drawCount = r15
            r2 = 0
            r11 = 0
            r14 = 0
            goto L_0x1077
        L_0x1047:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x106b
            if (r2 != r6) goto L_0x1059
            int r5 = r1.mentionCount
            if (r2 != r5) goto L_0x1059
            if (r14 == 0) goto L_0x1059
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            boolean r5 = r5.mentioned
            if (r5 != 0) goto L_0x106b
        L_0x1059:
            r1.drawCount = r6
            java.lang.Object[] r5 = new java.lang.Object[r6]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r5[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r5)
            goto L_0x1077
        L_0x106b:
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x1074
            r1.drawCount = r6
            r2 = r9
            goto L_0x1077
        L_0x1074:
            r1.drawCount = r14
            r2 = 0
        L_0x1077:
            int r5 = r1.mentionCount
            if (r5 == 0) goto L_0x1080
            r1.drawMention = r6
            java.lang.String r5 = "@"
            goto L_0x1083
        L_0x1080:
            r1.drawMention = r14
            r5 = 0
        L_0x1083:
            int r15 = r1.reactionMentionCount
            if (r15 <= 0) goto L_0x108a
            r1.drawReactionMention = r6
            goto L_0x108c
        L_0x108a:
            r1.drawReactionMention = r14
        L_0x108c:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isOut()
            if (r14 == 0) goto L_0x10f6
            org.telegram.tgnet.TLRPC$DraftMessage r14 = r1.draftMessage
            if (r14 != 0) goto L_0x10f6
            if (r11 == 0) goto L_0x10f6
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r14 = r14.action
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r14 != 0) goto L_0x10f6
            boolean r11 = r11.isSending()
            if (r11 == 0) goto L_0x10b4
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r6
            r1.drawError = r11
            goto L_0x10ff
        L_0x10b4:
            r11 = 0
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x10ca
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r6
            r1.drawCount = r11
            r1.drawMention = r11
            goto L_0x10ff
        L_0x10ca:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isSent()
            if (r11 == 0) goto L_0x0ff8
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isUnread()
            if (r11 == 0) goto L_0x10eb
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x10e9
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x10e9
            goto L_0x10eb
        L_0x10e9:
            r11 = 0
            goto L_0x10ec
        L_0x10eb:
            r11 = 1
        L_0x10ec:
            r1.drawCheck1 = r11
            r1.drawCheck2 = r6
            r11 = 0
            r1.drawClock = r11
            r1.drawError = r11
            goto L_0x10ff
        L_0x10f6:
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r11
        L_0x10ff:
            r1.promoDialog = r11
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r14 = r1.dialogsType
            if (r14 != 0) goto L_0x115d
            long r14 = r1.currentDialogId
            boolean r14 = r11.isPromoDialog(r14, r6)
            if (r14 == 0) goto L_0x115d
            r1.drawPinBackground = r6
            r1.promoDialog = r6
            int r14 = r11.promoDialogType
            int r15 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r14 != r15) goto L_0x1127
            int r11 = org.telegram.messenger.R.string.UseProxySponsor
            java.lang.String r13 = "UseProxySponsor"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r13 = r11
            goto L_0x115d
        L_0x1127:
            int r15 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r14 != r15) goto L_0x115d
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "PsaType_"
            r13.append(r14)
            java.lang.String r14 = r11.promoPsaType
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString((java.lang.String) r13)
            boolean r14 = android.text.TextUtils.isEmpty(r13)
            if (r14 == 0) goto L_0x1150
            int r13 = org.telegram.messenger.R.string.PsaTypeDefault
            java.lang.String r14 = "PsaTypeDefault"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
        L_0x1150:
            java.lang.String r14 = r11.promoPsaMessage
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x115d
            java.lang.String r3 = r11.promoPsaMessage
            r11 = 0
            r1.hasMessageThumb = r11
        L_0x115d:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x116c
            int r11 = org.telegram.messenger.R.string.ArchivedChats
            java.lang.String r14 = "ArchivedChats"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
        L_0x1169:
            r14 = r2
            r2 = r0
            goto L_0x11c2
        L_0x116c:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            if (r11 == 0) goto L_0x1173
            java.lang.String r11 = r11.title
            goto L_0x11b3
        L_0x1173:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            if (r11 == 0) goto L_0x11b2
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r11)
            if (r11 == 0) goto L_0x1186
            int r11 = org.telegram.messenger.R.string.RepliesTitle
            java.lang.String r14 = "RepliesTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x11b3
        L_0x1186:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 == 0) goto L_0x11ab
            boolean r11 = r1.useMeForMyMessages
            if (r11 == 0) goto L_0x119b
            int r11 = org.telegram.messenger.R.string.FromYou
            java.lang.String r14 = "FromYou"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x11b3
        L_0x119b:
            int r11 = r1.dialogsType
            r14 = 3
            if (r11 != r14) goto L_0x11a2
            r1.drawPinBackground = r6
        L_0x11a2:
            int r11 = org.telegram.messenger.R.string.SavedMessages
            java.lang.String r14 = "SavedMessages"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x11b3
        L_0x11ab:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            goto L_0x11b3
        L_0x11b2:
            r11 = r9
        L_0x11b3:
            int r14 = r11.length()
            if (r14 != 0) goto L_0x1169
            int r11 = org.telegram.messenger.R.string.HiddenName
            java.lang.String r14 = "HiddenName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x1169
        L_0x11c2:
            if (r4 == 0) goto L_0x1204
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r13)
            r15 = r7
            double r6 = (double) r0
            double r6 = java.lang.Math.ceil(r6)
            int r0 = (int) r6
            android.text.StaticLayout r6 = new android.text.StaticLayout
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r6
            r31 = r13
            r33 = r0
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r1.timeLayout = r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x11fb
            int r6 = r40.getMeasuredWidth()
            r7 = 1097859072(0x41700000, float:15.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 - r0
            r1.timeLeft = r6
            goto L_0x120c
        L_0x11fb:
            r7 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.timeLeft = r6
            goto L_0x120c
        L_0x1204:
            r15 = r7
            r6 = 0
            r1.timeLayout = r6
            r6 = 0
            r1.timeLeft = r6
            r0 = 0
        L_0x120c:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x1222
            int r6 = r40.getMeasuredWidth()
            int r7 = r1.nameLeft
            int r6 = r6 - r7
            r7 = 1096810496(0x41600000, float:14.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 - r0
            r1.nameWidth = r6
            goto L_0x1238
        L_0x1222:
            int r6 = r40.getMeasuredWidth()
            int r7 = r1.nameLeft
            int r6 = r6 - r7
            r7 = 1117388800(0x429a0000, float:77.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r6 = r6 - r0
            r1.nameWidth = r6
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x1238:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x124e
            int r6 = r1.nameWidth
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r7 = r7 + r13
            int r6 = r6 - r7
            r1.nameWidth = r6
        L_0x124e:
            boolean r6 = r1.drawClock
            r7 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x1280
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r13
            int r13 = r1.nameWidth
            int r13 = r13 - r6
            r1.nameWidth = r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x126f
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.clockDrawLeft = r0
            goto L_0x12fc
        L_0x126f:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r13 = r13 + r0
            r1.clockDrawLeft = r13
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x12fc
        L_0x1280:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x12fc
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r13
            int r13 = r1.nameWidth
            int r13 = r13 - r6
            r1.nameWidth = r13
            boolean r4 = r1.drawCheck1
            if (r4 == 0) goto L_0x12e3
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r4 = r4.getIntrinsicWidth()
            r20 = 1090519040(0x41000000, float:8.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r4 = r4 - r20
            int r13 = r13 - r4
            r1.nameWidth = r13
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x12bc
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.halfCheckDrawLeft = r0
            r4 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.checkDrawLeft = r0
            goto L_0x12fc
        L_0x12bc:
            int r4 = r1.timeLeft
            int r4 = r4 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r0
            r1.checkDrawLeft = r4
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = r4 + r0
            r1.halfCheckDrawLeft = r4
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r4 = r4.getIntrinsicWidth()
            int r6 = r6 + r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r6 - r4
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x12fc
        L_0x12e3:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x12ed
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft1 = r0
            goto L_0x12fc
        L_0x12ed:
            int r4 = r1.timeLeft
            int r4 = r4 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r0
            r1.checkDrawLeft1 = r4
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x12fc:
            boolean r0 = r1.drawPremium
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1320
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r1.emojiStatus
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            if (r0 == 0) goto L_0x1320
            r0 = 1108344832(0x42100000, float:36.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = r1.nameWidth
            int r4 = r4 - r0
            r1.nameWidth = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x13a2
            int r4 = r1.nameLeft
            int r4 = r4 + r0
            r1.nameLeft = r4
            goto L_0x13a2
        L_0x1320:
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x1346
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x1346
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x1346
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r4 = r4.getIntrinsicWidth()
            int r0 = r0 + r4
            int r4 = r1.nameWidth
            int r4 = r4 - r0
            r1.nameWidth = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x13a2
            int r4 = r1.nameLeft
            int r4 = r4 + r0
            r1.nameLeft = r4
            goto L_0x13a2
        L_0x1346:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1364
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            int r0 = r0 + r4
            int r4 = r1.nameWidth
            int r4 = r4 - r0
            r1.nameWidth = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x13a2
            int r4 = r1.nameLeft
            int r4 = r4 + r0
            r1.nameLeft = r4
            goto L_0x13a2
        L_0x1364:
            boolean r0 = r1.drawPremium
            if (r0 == 0) goto L_0x137d
            r0 = 1108344832(0x42100000, float:36.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = r1.nameWidth
            int r4 = r4 - r0
            r1.nameWidth = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x13a2
            int r4 = r1.nameLeft
            int r4 = r4 + r0
            r1.nameLeft = r4
            goto L_0x13a2
        L_0x137d:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x13a2
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r1.drawScam
            r7 = 1
            if (r4 != r7) goto L_0x138d
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x138f
        L_0x138d:
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x138f:
            int r7 = r7.getIntrinsicWidth()
            int r0 = r0 + r7
            int r7 = r1.nameWidth
            int r7 = r7 - r0
            r1.nameWidth = r7
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x13a2
            int r7 = r1.nameLeft
            int r7 = r7 + r0
            r1.nameLeft = r7
        L_0x13a2:
            r7 = 1094713344(0x41400000, float:12.0)
            int r0 = r1.nameWidth     // Catch:{ Exception -> 0x1456 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x1456 }
            int r0 = r0 - r13
            if (r0 >= 0) goto L_0x13ae
            r0 = 0
        L_0x13ae:
            r13 = 10
            r4 = 32
            java.lang.String r4 = r11.replace(r13, r4)     // Catch:{ Exception -> 0x1456 }
            boolean r11 = r1.nameLayoutEllipsizeByGradient     // Catch:{ Exception -> 0x1456 }
            if (r11 == 0) goto L_0x13dd
            int r11 = r4.length()     // Catch:{ Exception -> 0x1456 }
            android.text.TextPaint[] r13 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1456 }
            int r6 = r1.paintIndex     // Catch:{ Exception -> 0x1456 }
            r6 = r13[r6]     // Catch:{ Exception -> 0x1456 }
            float r13 = (float) r0     // Catch:{ Exception -> 0x1456 }
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1456 }
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r4, r6, r13, r7)     // Catch:{ Exception -> 0x1456 }
            int r6 = r6.length()     // Catch:{ Exception -> 0x1456 }
            if (r11 != r6) goto L_0x13d3
            r6 = 1
            goto L_0x13d4
        L_0x13d3:
            r6 = 0
        L_0x13d4:
            r1.nameLayoutFits = r6     // Catch:{ Exception -> 0x1456 }
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x1456 }
            int r0 = r0 + r6
        L_0x13dd:
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1456 }
            int r7 = r1.paintIndex     // Catch:{ Exception -> 0x1456 }
            r6 = r6[r7]     // Catch:{ Exception -> 0x1456 }
            float r7 = (float) r0     // Catch:{ Exception -> 0x1456 }
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1456 }
            java.lang.CharSequence r4 = android.text.TextUtils.ellipsize(r4, r6, r7, r11)     // Catch:{ Exception -> 0x1456 }
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1456 }
            int r7 = r1.paintIndex     // Catch:{ Exception -> 0x1456 }
            r6 = r6[r7]     // Catch:{ Exception -> 0x1456 }
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()     // Catch:{ Exception -> 0x1456 }
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)     // Catch:{ Exception -> 0x1456 }
            r11 = 0
            java.lang.CharSequence r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r7, r11)     // Catch:{ Exception -> 0x1456 }
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x1456 }
            if (r6 == 0) goto L_0x1416
            boolean r6 = r6.hasHighlightedWords()     // Catch:{ Exception -> 0x1456 }
            if (r6 == 0) goto L_0x1416
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x1456 }
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords     // Catch:{ Exception -> 0x1456 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x1456 }
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r4, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)     // Catch:{ Exception -> 0x1456 }
            if (r6 == 0) goto L_0x1416
            r31 = r6
            goto L_0x1418
        L_0x1416:
            r31 = r4
        L_0x1418:
            android.text.StaticLayout r4 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1456 }
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1456 }
            int r7 = r1.paintIndex     // Catch:{ Exception -> 0x1456 }
            r32 = r6[r7]     // Catch:{ Exception -> 0x1456 }
            int r6 = r1.nameWidth     // Catch:{ Exception -> 0x1456 }
            int r33 = java.lang.Math.max(r0, r6)     // Catch:{ Exception -> 0x1456 }
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1456 }
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r4
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x1456 }
            r1.nameLayout = r4     // Catch:{ Exception -> 0x1456 }
            boolean r0 = r1.nameLayoutEllipsizeByGradient     // Catch:{ Exception -> 0x1456 }
            if (r0 == 0) goto L_0x1449
            r6 = 0
            boolean r0 = r4.isRtlCharAt(r6)     // Catch:{ Exception -> 0x1456 }
            if (r0 == 0) goto L_0x1449
            r0 = 1108344832(0x42100000, float:36.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x1456 }
            int r0 = -r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1456 }
            goto L_0x144a
        L_0x1449:
            r0 = 0
        L_0x144a:
            r1.nameLayoutTranslateX = r0     // Catch:{ Exception -> 0x1456 }
            android.text.StaticLayout r0 = r1.nameLayout     // Catch:{ Exception -> 0x1456 }
            r4 = 0
            boolean r0 = r0.isRtlCharAt(r4)     // Catch:{ Exception -> 0x1456 }
            r1.nameLayoutEllipsizeLeft = r0     // Catch:{ Exception -> 0x1456 }
            goto L_0x145a
        L_0x1456:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x145a:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1516
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1464
            goto L_0x1516
        L_0x1464:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4 = 1106771968(0x41var_, float:31.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameTop = r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.timeTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.errorTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.pinTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.countTop = r4
            r4 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.checkDrawTop = r6
            int r4 = r40.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x14c8
            r6 = 1102053376(0x41b00000, float:22.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r40.getMeasuredWidth()
            r7 = 1115684864(0x42800000, float:64.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r7 = r29 + 11
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r6 - r7
            goto L_0x14df
        L_0x14c8:
            r6 = 1117257728(0x42980000, float:76.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 1116078080(0x42860000, float:67.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r6
        L_0x14df:
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r6 = (float) r6
            float r13 = (float) r0
            r16 = 1113063424(0x42580000, float:54.0)
            r17 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            r23 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r9 = (float) r9
            r11.setImageCoords(r6, r13, r4, r9)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r7
            r7 = 1106247680(0x41var_, float:30.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r0
            float r7 = (float) r7
            r9 = r29
            float r11 = (float) r9
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r13 = (float) r13
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r4.setImageCoords(r6, r7, r13, r11)
            r6 = r0
            r24 = r12
            goto L_0x15c4
        L_0x1516:
            r23 = r9
            r9 = r29
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4 = 1107296256(0x42000000, float:32.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameTop = r4
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.timeTop = r4
            r4 = 1110179840(0x422CLASSNAME, float:43.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.errorTop = r4
            r4 = 1110179840(0x422CLASSNAME, float:43.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.pinTop = r4
            r4 = 1110179840(0x422CLASSNAME, float:43.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.countTop = r4
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.checkDrawTop = r4
            int r4 = r40.getMeasuredWidth()
            r6 = 1119485952(0x42ba0000, float:93.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x157d
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r40.getMeasuredWidth()
            r7 = 1115947008(0x42840000, float:66.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r6 - r7
            goto L_0x1594
        L_0x157d:
            r6 = 1117519872(0x429CLASSNAME, float:78.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 1116340224(0x428a0000, float:69.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r6
        L_0x1594:
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r6 = (float) r6
            float r13 = (float) r0
            r16 = 1113587712(0x42600000, float:56.0)
            r17 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            r24 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r12 = (float) r12
            r11.setImageCoords(r6, r13, r4, r12)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r0
            float r7 = (float) r7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r12 = (float) r12
            r4.setImageCoords(r6, r7, r11, r12)
            r6 = r0
        L_0x15c4:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x15e9
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x15e1
            int r0 = r40.getMeasuredWidth()
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r4 = r4.getIntrinsicWidth()
            int r0 = r0 - r4
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.pinLeft = r0
            goto L_0x15e9
        L_0x15e1:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x15e9:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x161d
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x1607
            int r0 = r40.getMeasuredWidth()
            r4 = 1107820544(0x42080000, float:34.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.errorLeft = r0
            goto L_0x1619
        L_0x1607:
            r4 = 1093664768(0x41300000, float:11.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.errorLeft = r4
            int r4 = r1.messageLeft
            int r4 = r4 + r0
            r1.messageLeft = r4
            int r4 = r1.messageNameLeft
            int r4 = r4 + r0
            r1.messageNameLeft = r4
        L_0x1619:
            r0 = r17
            goto L_0x17bb
        L_0x161d:
            if (r14 != 0) goto L_0x164d
            if (r5 != 0) goto L_0x164d
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x1626
            goto L_0x164d
        L_0x1626:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1647
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            int r17 = r17 - r0
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1647
            int r4 = r1.messageLeft
            int r4 = r4 + r0
            r1.messageLeft = r4
            int r4 = r1.messageNameLeft
            int r4 = r4 + r0
            r1.messageNameLeft = r4
        L_0x1647:
            r4 = 0
            r1.drawCount = r4
            r1.drawMention = r4
            goto L_0x1619
        L_0x164d:
            if (r14 == 0) goto L_0x16b0
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r4.measureText(r14)
            double r11 = (double) r4
            double r11 = java.lang.Math.ceil(r11)
            int r4 = (int) r11
            int r0 = java.lang.Math.max(r0, r4)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r4 = r1.countWidth
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_CENTER
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r0
            r31 = r14
            r33 = r4
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r4
            int r17 = r17 - r0
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x169c
            int r0 = r40.getMeasuredWidth()
            int r4 = r1.countWidth
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r4
            r1.countLeft = r0
            goto L_0x16ac
        L_0x169c:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.countLeft = r4
            int r4 = r1.messageLeft
            int r4 = r4 + r0
            r1.messageLeft = r4
            int r4 = r1.messageNameLeft
            int r4 = r4 + r0
            r1.messageNameLeft = r4
        L_0x16ac:
            r4 = 1
            r1.drawCount = r4
            goto L_0x16b3
        L_0x16b0:
            r7 = 0
            r1.countWidth = r7
        L_0x16b3:
            if (r5 == 0) goto L_0x173b
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x16eb
            r7 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r7.measureText(r5)
            double r11 = (double) r7
            double r11 = java.lang.Math.ceil(r11)
            int r7 = (int) r11
            int r0 = java.lang.Math.max(r0, r7)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r7 = r1.mentionWidth
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_CENTER
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r0
            r31 = r5
            r33 = r7
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r1.mentionLayout = r0
            goto L_0x16f3
        L_0x16eb:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.mentionWidth = r0
        L_0x16f3:
            int r0 = r1.mentionWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x171b
            int r0 = r40.getMeasuredWidth()
            int r5 = r1.mentionWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x1716
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r5 = r5 + r7
            goto L_0x1717
        L_0x1716:
            r5 = 0
        L_0x1717:
            int r0 = r0 - r5
            r1.mentionLeft = r0
            goto L_0x1737
        L_0x171b:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r1.countWidth
            if (r7 == 0) goto L_0x1729
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r11
            goto L_0x172a
        L_0x1729:
            r7 = 0
        L_0x172a:
            int r5 = r5 + r7
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x1737:
            r4 = 1
            r1.drawMention = r4
            goto L_0x173e
        L_0x173b:
            r5 = 0
            r1.mentionWidth = r5
        L_0x173e:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x1619
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1783
            int r0 = r40.getMeasuredWidth()
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.reactionMentionLeft = r0
            boolean r5 = r1.drawMention
            if (r5 == 0) goto L_0x176d
            int r5 = r1.mentionWidth
            if (r5 == 0) goto L_0x1769
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r5 = r5 + r7
            goto L_0x176a
        L_0x1769:
            r5 = 0
        L_0x176a:
            int r0 = r0 - r5
            r1.reactionMentionLeft = r0
        L_0x176d:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x1619
            int r0 = r1.reactionMentionLeft
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x177d
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r5 = r5 + r7
            goto L_0x177e
        L_0x177d:
            r5 = 0
        L_0x177e:
            int r0 = r0 - r5
            r1.reactionMentionLeft = r0
            goto L_0x1619
        L_0x1783:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.reactionMentionLeft = r5
            boolean r7 = r1.drawMention
            if (r7 == 0) goto L_0x179b
            int r7 = r1.mentionWidth
            if (r7 == 0) goto L_0x1797
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r11
            goto L_0x1798
        L_0x1797:
            r7 = 0
        L_0x1798:
            int r5 = r5 + r7
            r1.reactionMentionLeft = r5
        L_0x179b:
            boolean r5 = r1.drawCount
            if (r5 == 0) goto L_0x17af
            int r5 = r1.reactionMentionLeft
            int r7 = r1.countWidth
            if (r7 == 0) goto L_0x17ab
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r11
            goto L_0x17ac
        L_0x17ab:
            r7 = 0
        L_0x17ac:
            int r5 = r5 + r7
            r1.reactionMentionLeft = r5
        L_0x17af:
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
            goto L_0x1619
        L_0x17bb:
            if (r15 == 0) goto L_0x1805
            if (r3 != 0) goto L_0x17c1
            r3 = r23
        L_0x17c1:
            int r5 = r3.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r5 <= r7) goto L_0x17ce
            r5 = 0
            java.lang.CharSequence r3 = r3.subSequence(r5, r7)
        L_0x17ce:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x17d6
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x17d8
        L_0x17d6:
            if (r2 == 0) goto L_0x17dd
        L_0x17d8:
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r3)
            goto L_0x17e1
        L_0x17dd:
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r3)
        L_0x17e1:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r7 = 1099431936(0x41880000, float:17.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r11 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r5, r7, r11)
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x1805
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r5 == 0) goto L_0x1805
            r3 = r5
        L_0x1805:
            r5 = 1094713344(0x41400000, float:12.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = java.lang.Math.max(r7, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1817
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x186e
        L_0x1817:
            if (r2 == 0) goto L_0x186e
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x1822
            int r7 = r1.currentDialogFolderDialogsCount
            r4 = 1
            if (r7 != r4) goto L_0x186e
        L_0x1822:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1854 }
            if (r0 == 0) goto L_0x1839
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1854 }
            if (r0 == 0) goto L_0x1839
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1854 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1854 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x1854 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)     // Catch:{ Exception -> 0x1854 }
            if (r0 == 0) goto L_0x1839
            r2 = r0
        L_0x1839:
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1854 }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1854 }
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1854 }
            r38 = 1
            r29 = r2
            r31 = r5
            r37 = r5
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x1854 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1854 }
            goto L_0x1858
        L_0x1854:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1858:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            float r6 = (float) r6
            r0.setImageY(r6)
            goto L_0x1896
        L_0x186e:
            r7 = 0
            r1.messageNameLayout = r7
            if (r0 != 0) goto L_0x1881
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1878
            goto L_0x1881
        L_0x1878:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1896
        L_0x1881:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1101529088(0x41a80000, float:21.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            float r6 = (float) r6
            r0.setImageY(r6)
        L_0x1896:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1987 }
            if (r0 != 0) goto L_0x189e
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x18b1
        L_0x189e:
            int r6 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x18b1
            int r6 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1987 }
            r4 = 1
            if (r6 <= r4) goto L_0x18b1
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1987 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x1987 }
            r10 = r0[r3]     // Catch:{ Exception -> 0x1987 }
            r3 = r2
            r0 = r10
            r2 = 0
            goto L_0x18c9
        L_0x18b1:
            if (r0 != 0) goto L_0x18b7
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1987 }
            if (r0 == 0) goto L_0x18b9
        L_0x18b7:
            if (r2 == 0) goto L_0x18c8
        L_0x18b9:
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x1987 }
            int r0 = r5 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1987 }
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1987 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r10, r0, r6)     // Catch:{ Exception -> 0x1987 }
        L_0x18c8:
            r0 = r10
        L_0x18c9:
            boolean r6 = r3 instanceof android.text.Spannable     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x18fb
            r6 = r3
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x1987 }
            int r7 = r6.length()     // Catch:{ Exception -> 0x1987 }
            java.lang.Class<android.text.style.CharacterStyle> r10 = android.text.style.CharacterStyle.class
            r11 = 0
            java.lang.Object[] r7 = r6.getSpans(r11, r7, r10)     // Catch:{ Exception -> 0x1987 }
            android.text.style.CharacterStyle[] r7 = (android.text.style.CharacterStyle[]) r7     // Catch:{ Exception -> 0x1987 }
            int r10 = r7.length     // Catch:{ Exception -> 0x1987 }
            r11 = 0
        L_0x18df:
            if (r11 >= r10) goto L_0x18fb
            r12 = r7[r11]     // Catch:{ Exception -> 0x1987 }
            boolean r13 = r12 instanceof android.text.style.ClickableSpan     // Catch:{ Exception -> 0x1987 }
            if (r13 != 0) goto L_0x18f5
            boolean r13 = r12 instanceof android.text.style.StyleSpan     // Catch:{ Exception -> 0x1987 }
            if (r13 == 0) goto L_0x18f8
            r13 = r12
            android.text.style.StyleSpan r13 = (android.text.style.StyleSpan) r13     // Catch:{ Exception -> 0x1987 }
            int r13 = r13.getStyle()     // Catch:{ Exception -> 0x1987 }
            r4 = 1
            if (r13 != r4) goto L_0x18f8
        L_0x18f5:
            r6.removeSpan(r12)     // Catch:{ Exception -> 0x1987 }
        L_0x18f8:
            int r11 = r11 + 1
            goto L_0x18df
        L_0x18fb:
            boolean r6 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1987 }
            if (r6 != 0) goto L_0x1933
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x1904
            goto L_0x1933
        L_0x1904:
            boolean r2 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1987 }
            if (r2 == 0) goto L_0x191f
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1987 }
            int r6 = r6 + r9
            int r5 = r5 + r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x191f
            int r6 = r1.messageLeft     // Catch:{ Exception -> 0x1987 }
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1987 }
            int r2 = r9 + r7
            int r6 = r6 - r2
            r1.messageLeft = r6     // Catch:{ Exception -> 0x1987 }
        L_0x191f:
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1987 }
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1987 }
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r2
            r11 = r3
            r12 = r0
            r13 = r5
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x1987 }
            r1.messageLayout = r2     // Catch:{ Exception -> 0x1987 }
            goto L_0x1961
        L_0x1933:
            boolean r6 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1987 }
            if (r6 == 0) goto L_0x1940
            if (r2 == 0) goto L_0x1940
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x1987 }
            int r5 = r5 + r7
        L_0x1940:
            android.text.Layout$Alignment r13 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1987 }
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x1987 }
            float r15 = (float) r6     // Catch:{ Exception -> 0x1987 }
            r16 = 0
            android.text.TextUtils$TruncateAt r17 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1987 }
            if (r2 == 0) goto L_0x1954
            r19 = 1
            goto L_0x1956
        L_0x1954:
            r19 = 2
        L_0x1956:
            r10 = r3
            r11 = r0
            r12 = r5
            r18 = r5
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x1987 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1987 }
        L_0x1961:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x1987 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilers     // Catch:{ Exception -> 0x1987 }
            r0.addAll(r2)     // Catch:{ Exception -> 0x1987 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilers     // Catch:{ Exception -> 0x1987 }
            r0.clear()     // Catch:{ Exception -> 0x1987 }
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1987 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x1987 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r1.spoilers     // Catch:{ Exception -> 0x1987 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r3)     // Catch:{ Exception -> 0x1987 }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r0 = r1.animatedEmojiStack     // Catch:{ Exception -> 0x1987 }
            r2 = 1
            android.text.Layout[] r3 = new android.text.Layout[r2]     // Catch:{ Exception -> 0x1987 }
            android.text.StaticLayout r2 = r1.messageLayout     // Catch:{ Exception -> 0x1987 }
            r6 = 0
            r3[r6] = r2     // Catch:{ Exception -> 0x1987 }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r0 = org.telegram.ui.Components.AnimatedEmojiSpan.update((int) r6, (android.view.View) r1, (org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans) r0, (android.text.Layout[]) r3)     // Catch:{ Exception -> 0x1987 }
            r1.animatedEmojiStack = r0     // Catch:{ Exception -> 0x1987 }
            goto L_0x198e
        L_0x1987:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x198e:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1b05
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1a8e
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a8e
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r6 = r1.nameLayoutEllipsizeByGradient
            if (r6 == 0) goto L_0x19b9
            int r6 = r1.nameWidth
            double r6 = (double) r6
            double r2 = java.lang.Math.min(r6, r2)
        L_0x19b9:
            boolean r6 = r1.dialogMuted
            if (r6 == 0) goto L_0x19ee
            boolean r6 = r1.drawVerified
            if (r6 != 0) goto L_0x19ee
            int r6 = r1.drawScam
            if (r6 != 0) goto L_0x19ee
            int r6 = r1.nameLeft
            double r6 = (double) r6
            int r9 = r1.nameWidth
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r6 = (int) r6
            r1.nameMuteLeft = r6
            goto L_0x1a73
        L_0x19ee:
            boolean r6 = r1.drawVerified
            if (r6 == 0) goto L_0x1a1a
            int r6 = r1.nameLeft
            double r6 = (double) r6
            int r9 = r1.nameWidth
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r6 = (int) r6
            r1.nameMuteLeft = r6
            goto L_0x1a73
        L_0x1a1a:
            boolean r6 = r1.drawPremium
            if (r6 == 0) goto L_0x1a40
            int r6 = r1.nameLeft
            double r6 = (double) r6
            int r9 = r1.nameWidth
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            double r11 = (double) r0
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r6 = (int) r6
            r1.nameMuteLeft = r6
            goto L_0x1a73
        L_0x1a40:
            int r6 = r1.drawScam
            if (r6 == 0) goto L_0x1a73
            int r6 = r1.nameLeft
            double r6 = (double) r6
            int r9 = r1.nameWidth
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r9 = r1.drawScam
            r4 = 1
            if (r9 != r4) goto L_0x1a65
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1a67
        L_0x1a65:
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1a67:
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r6 = (int) r6
            r1.nameMuteLeft = r6
        L_0x1a73:
            r6 = 0
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x1a8e
            int r0 = r1.nameWidth
            double r6 = (double) r0
            int r9 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r9 >= 0) goto L_0x1a8e
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r9 = (double) r0
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            int r0 = (int) r6
            r1.nameLeft = r0
        L_0x1a8e:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1acf
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1acf
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x1a9f:
            if (r2 >= r0) goto L_0x1ac5
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineLeft(r2)
            r7 = 0
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x1ac4
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineWidth(r2)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r6
            int r6 = (int) r9
            int r3 = java.lang.Math.min(r3, r6)
            int r2 = r2 + 1
            goto L_0x1a9f
        L_0x1ac4:
            r3 = 0
        L_0x1ac5:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x1acf
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x1acf:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1baf
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1baf
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1baf
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r5 = (double) r5
            int r0 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1baf
            int r0 = r1.messageNameLeft
            double r9 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r2
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r5
            int r0 = (int) r9
            r1.messageNameLeft = r0
            goto L_0x1baf
        L_0x1b05:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1b74
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1b74
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            boolean r2 = r1.nameLayoutEllipsizeByGradient
            if (r2 == 0) goto L_0x1b21
            int r2 = r1.nameWidth
            float r2 = (float) r2
            float r0 = java.lang.Math.min(r2, r0)
        L_0x1b21:
            int r2 = r1.nameWidth
            float r2 = (float) r2
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x1b55
            android.text.StaticLayout r2 = r1.nameLayout
            r3 = 0
            float r2 = r2.getLineWidth(r3)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            boolean r5 = r1.nameLayoutEllipsizeByGradient
            if (r5 == 0) goto L_0x1b3f
            int r5 = r1.nameWidth
            double r5 = (double) r5
            double r2 = java.lang.Math.min(r5, r2)
        L_0x1b3f:
            int r5 = r1.nameWidth
            double r6 = (double) r5
            int r9 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r9 >= 0) goto L_0x1b55
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r9
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1b55:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1b65
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1b65
            boolean r2 = r1.drawPremium
            if (r2 != 0) goto L_0x1b65
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1b74
        L_0x1b65:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1b74:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1b97
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1b97
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1b81:
            if (r3 >= r0) goto L_0x1b90
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r5)
            int r3 = r3 + 1
            goto L_0x1b81
        L_0x1b90:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1b97:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1baf
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1baf
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1baf:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1bf1
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1bf1
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1bed }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1bed }
            r2 = 1
            if (r8 < r0) goto L_0x1bc4
            int r8 = r0 + -1
        L_0x1bc4:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1bed }
            float r0 = r0.getPrimaryHorizontal(r8)     // Catch:{ Exception -> 0x1bed }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1bed }
            int r8 = r8 + r2
            float r2 = r3.getPrimaryHorizontal(r8)     // Catch:{ Exception -> 0x1bed }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1bed }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1bed }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1bed }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1bed }
            if (r0 == 0) goto L_0x1be4
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1bed }
            int r0 = r0 + r2
        L_0x1be4:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1bed }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1bed }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1bed }
            goto L_0x1bf1
        L_0x1bed:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1bf1:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1c3f
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1c3f
            if (r24 < 0) goto L_0x1CLASSNAME
            int r12 = r24 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r12 >= r0) goto L_0x1CLASSNAME
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r24
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r12)
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1CLASSNAME:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1CLASSNAME
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1c3f
        L_0x1CLASSNAME:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1c3f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
    }

    private void drawCheckStatus(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f) {
        if (f != 0.0f || z4) {
            float f2 = (f * 0.5f) + 0.5f;
            if (z) {
                BaseCell.setDrawableBounds(Theme.dialogs_clockDrawable, this.clockDrawLeft, this.checkDrawTop);
                if (f != 1.0f) {
                    canvas.save();
                    canvas.scale(f2, f2, (float) Theme.dialogs_clockDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
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
                        canvas.scale(f2, f2, (float) Theme.dialogs_halfCheckDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        Theme.dialogs_halfCheckDrawable.setAlpha((int) (f * 255.0f));
                    }
                    if (!z4 && f != 0.0f) {
                        canvas.save();
                        canvas.scale(f2, f2, (float) Theme.dialogs_halfCheckDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        int i = (int) (255.0f * f);
                        Theme.dialogs_halfCheckDrawable.setAlpha(i);
                        Theme.dialogs_checkReadDrawable.setAlpha(i);
                    }
                    Theme.dialogs_halfCheckDrawable.draw(canvas);
                    if (z4) {
                        canvas.restore();
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(4.0f)) * (1.0f - f), 0.0f);
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_checkReadDrawable, this.checkDrawLeft, this.checkDrawTop);
                    Theme.dialogs_checkReadDrawable.draw(canvas);
                    if (z4) {
                        canvas.restore();
                        Theme.dialogs_halfCheckDrawable.setAlpha(255);
                    }
                    if (!z4 && f != 0.0f) {
                        canvas.restore();
                        Theme.dialogs_halfCheckDrawable.setAlpha(255);
                        Theme.dialogs_checkReadDrawable.setAlpha(255);
                        return;
                    }
                    return;
                }
                BaseCell.setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft1, this.checkDrawTop);
                if (f != 1.0f) {
                    canvas.save();
                    canvas.scale(f2, f2, (float) Theme.dialogs_checkDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                    Theme.dialogs_checkDrawable.setAlpha((int) (255.0f * f));
                }
                Theme.dialogs_checkDrawable.draw(canvas);
                if (f != 1.0f) {
                    canvas.restore();
                    Theme.dialogs_checkDrawable.setAlpha(255);
                }
            }
        }
    }

    public boolean isPointInsideAvatar(float f, float f2) {
        if (!LocaleController.isRTL) {
            if (f < 0.0f || f >= ((float) AndroidUtilities.dp(60.0f))) {
                return false;
            }
            return true;
        } else if (f < ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) || f >= ((float) getMeasuredWidth())) {
            return false;
        } else {
            return true;
        }
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    public void checkCurrentDialogIndex(boolean z) {
        MessageObject messageObject;
        MessageObject messageObject2;
        DialogsActivity dialogsActivity = this.parentFragment;
        if (dialogsActivity != null) {
            ArrayList<TLRPC$Dialog> dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, z);
            if (this.index < dialogsArray.size()) {
                TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(this.index);
                boolean z2 = true;
                TLRPC$Dialog tLRPC$Dialog2 = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
                TLRPC$DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId, 0);
                if (this.currentDialogFolderId != 0) {
                    messageObject = findFolderTopMessage();
                } else {
                    messageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(tLRPC$Dialog.id);
                }
                if (this.currentDialogId != tLRPC$Dialog.id || (((messageObject2 = this.message) != null && messageObject2.getId() != tLRPC$Dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tLRPC$Dialog.unread_count || this.mentionCount != tLRPC$Dialog.unread_mentions_count || this.markUnread != tLRPC$Dialog.unread_mark || this.message != messageObject || draft != this.draftMessage || this.drawPin != tLRPC$Dialog.pinned))) {
                    long j = this.currentDialogId;
                    long j2 = tLRPC$Dialog.id;
                    boolean z3 = j != j2;
                    this.currentDialogId = j2;
                    if (z3) {
                        this.lastDialogChangedTime = System.currentTimeMillis();
                        ValueAnimator valueAnimator = this.statusDrawableAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.removeAllListeners();
                            this.statusDrawableAnimator.cancel();
                        }
                        this.statusDrawableAnimationInProgress = false;
                        this.lastStatusDrawableParams = -1;
                    }
                    boolean z4 = tLRPC$Dialog instanceof TLRPC$TL_dialogFolder;
                    if (z4) {
                        this.currentDialogFolderId = ((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id;
                    } else {
                        this.currentDialogFolderId = 0;
                    }
                    int i = this.dialogsType;
                    if (i == 7 || i == 8) {
                        MessagesController.DialogFilter dialogFilter = MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType == 8 ? (char) 1 : 0];
                        if (!(tLRPC$Dialog instanceof TLRPC$TL_dialog) || tLRPC$Dialog2 == null || dialogFilter == null || dialogFilter.pinnedDialogs.indexOfKey(tLRPC$Dialog.id) < 0 || dialogFilter.pinnedDialogs.indexOfKey(tLRPC$Dialog2.id) >= 0) {
                            z2 = false;
                        }
                        this.fullSeparator = z2;
                        this.fullSeparator2 = false;
                    } else {
                        this.fullSeparator = (tLRPC$Dialog instanceof TLRPC$TL_dialog) && tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
                        if (!z4 || tLRPC$Dialog2 == null || tLRPC$Dialog2.pinned) {
                            z2 = false;
                        }
                        this.fullSeparator2 = z2;
                    }
                    update(0, !z3);
                    if (z3) {
                        this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
                    }
                    checkOnline();
                    checkGroupCall();
                    checkChatTheme();
                }
            }
        }
    }

    public void animateArchiveAvatar() {
        if (this.avatarDrawable.getAvatarType() == 2) {
            this.animatingArchiveAvatar = true;
            this.animatingArchiveAvatarProgress = 0.0f;
            Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
            Theme.dialogs_archiveAvatarDrawable.start();
            invalidate();
        }
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        }
    }

    private MessageObject findFolderTopMessage() {
        DialogsActivity dialogsActivity = this.parentFragment;
        MessageObject messageObject = null;
        if (dialogsActivity == null) {
            return null;
        }
        ArrayList<TLRPC$Dialog> dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        if (!dialogsArray.isEmpty()) {
            int size = dialogsArray.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(i);
                MessageObject messageObject2 = MessagesController.getInstance(this.currentAccount).dialogMessage.get(tLRPC$Dialog.id);
                if (messageObject2 != null && (messageObject == null || messageObject2.messageOwner.date > messageObject.messageOwner.date)) {
                    messageObject = messageObject2;
                }
                if (tLRPC$Dialog.pinnedNum == 0 && messageObject != null) {
                    break;
                }
            }
        }
        return messageObject;
    }

    public boolean isFolderCell() {
        return this.currentDialogFolderId != 0;
    }

    public void update(int i) {
        update(i, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:110:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x024b  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x05f4  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x05f8  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x05fd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r25, boolean r26) {
        /*
            r24 = this;
            r0 = r24
            r1 = r26
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r0.customDialog
            r4 = 0
            r5 = 1
            r6 = 0
            if (r2 == 0) goto L_0x0040
            int r7 = r2.date
            r0.lastMessageDate = r7
            int r7 = r2.unread_count
            if (r7 == 0) goto L_0x0014
            goto L_0x0015
        L_0x0014:
            r5 = 0
        L_0x0015:
            r0.lastUnreadState = r5
            r0.unreadCount = r7
            boolean r5 = r2.pinned
            r0.drawPin = r5
            boolean r5 = r2.muted
            r0.dialogMuted = r5
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            int r6 = r2.id
            long r6 = (long) r6
            java.lang.String r2 = r2.name
            r5.setInfo(r6, r2, r4)
            org.telegram.messenger.ImageReceiver r8 = r0.avatarImage
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            r13 = 0
            java.lang.String r10 = "50_50"
            r8.setImage(r9, r10, r11, r12, r13)
            org.telegram.messenger.ImageReceiver r2 = r0.thumbImage
            r2.setImageBitmap((android.graphics.drawable.Drawable) r4)
        L_0x003d:
            r2 = 0
            goto L_0x05e7
        L_0x0040:
            int r2 = r0.unreadCount
            int r7 = r0.reactionMentionCount
            if (r7 == 0) goto L_0x0048
            r7 = 1
            goto L_0x0049
        L_0x0048:
            r7 = 0
        L_0x0049:
            boolean r8 = r0.markUnread
            boolean r9 = r0.isDialogCell
            if (r9 == 0) goto L_0x0117
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r9 = r9.dialogs_dict
            long r10 = r0.currentDialogId
            java.lang.Object r9 = r9.get(r10)
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC$Dialog) r9
            if (r9 == 0) goto L_0x010a
            if (r25 != 0) goto L_0x0119
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            long r11 = r9.id
            boolean r10 = r10.isClearingDialog(r11)
            r0.clearingDialog = r10
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r10 = r10.dialogMessage
            long r11 = r9.id
            java.lang.Object r10 = r10.get(r11)
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            r0.message = r10
            if (r10 == 0) goto L_0x008d
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x008d
            r10 = 1
            goto L_0x008e
        L_0x008d:
            r10 = 0
        L_0x008e:
            r0.lastUnreadState = r10
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r10 == 0) goto L_0x00a5
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)
            int r10 = r10.getArchiveUnreadCount()
            r0.unreadCount = r10
            r0.mentionCount = r6
            r0.reactionMentionCount = r6
            goto L_0x00b1
        L_0x00a5:
            int r10 = r9.unread_count
            r0.unreadCount = r10
            int r10 = r9.unread_mentions_count
            r0.mentionCount = r10
            int r10 = r9.unread_reactions_count
            r0.reactionMentionCount = r10
        L_0x00b1:
            boolean r10 = r9.unread_mark
            r0.markUnread = r10
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x00be
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            int r10 = r10.edit_date
            goto L_0x00bf
        L_0x00be:
            r10 = 0
        L_0x00bf:
            r0.currentEditDate = r10
            int r10 = r9.last_message_date
            r0.lastMessageDate = r10
            int r10 = r0.dialogsType
            r11 = 7
            r12 = 8
            if (r10 == r11) goto L_0x00dd
            if (r10 != r12) goto L_0x00cf
            goto L_0x00dd
        L_0x00cf:
            int r10 = r0.currentDialogFolderId
            if (r10 != 0) goto L_0x00d9
            boolean r9 = r9.pinned
            if (r9 == 0) goto L_0x00d9
            r9 = 1
            goto L_0x00da
        L_0x00d9:
            r9 = 0
        L_0x00da:
            r0.drawPin = r9
            goto L_0x00ff
        L_0x00dd:
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.messenger.MessagesController$DialogFilter[] r10 = r10.selectedDialogFilter
            int r11 = r0.dialogsType
            if (r11 != r12) goto L_0x00eb
            r11 = 1
            goto L_0x00ec
        L_0x00eb:
            r11 = 0
        L_0x00ec:
            r10 = r10[r11]
            if (r10 == 0) goto L_0x00fc
            org.telegram.messenger.support.LongSparseIntArray r10 = r10.pinnedDialogs
            long r11 = r9.id
            int r9 = r10.indexOfKey(r11)
            if (r9 < 0) goto L_0x00fc
            r9 = 1
            goto L_0x00fd
        L_0x00fc:
            r9 = 0
        L_0x00fd:
            r0.drawPin = r9
        L_0x00ff:
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x0119
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.send_state
            r0.lastSendState = r9
            goto L_0x0119
        L_0x010a:
            r0.unreadCount = r6
            r0.mentionCount = r6
            r0.reactionMentionCount = r6
            r0.currentEditDate = r6
            r0.lastMessageDate = r6
            r0.clearingDialog = r6
            goto L_0x0119
        L_0x0117:
            r0.drawPin = r6
        L_0x0119:
            int r9 = r0.dialogsType
            r10 = 2
            if (r9 != r10) goto L_0x0120
            r0.drawPin = r6
        L_0x0120:
            if (r25 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            if (r9 == 0) goto L_0x0143
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r9 = r25 & r9
            if (r9 == 0) goto L_0x0143
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$User r11 = r0.user
            long r11 = r11.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r11)
            r0.user = r9
            r24.invalidate()
        L_0x0143:
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            if (r9 == 0) goto L_0x01a9
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_EMOJI_STATUS
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01a9
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$User r11 = r0.user
            long r11 = r11.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r11)
            r0.user = r9
            org.telegram.tgnet.TLRPC$EmojiStatus r9 = r9.emoji_status
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatusUntil
            if (r11 == 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r9 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r9
            int r9 = r9.until
            long r11 = java.lang.System.currentTimeMillis()
            r13 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 / r13
            int r12 = (int) r11
            if (r9 <= r12) goto L_0x0185
            r0.nameLayoutEllipsizeByGradient = r5
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r9 = r0.emojiStatus
            org.telegram.tgnet.TLRPC$User r11 = r0.user
            org.telegram.tgnet.TLRPC$EmojiStatus r11 = r11.emoji_status
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r11 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r11
            long r11 = r11.document_id
            r9.set((long) r11, (boolean) r1)
            goto L_0x01a6
        L_0x0185:
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            org.telegram.tgnet.TLRPC$EmojiStatus r9 = r9.emoji_status
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatus
            if (r11 == 0) goto L_0x0199
            r0.nameLayoutEllipsizeByGradient = r5
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r11 = r0.emojiStatus
            org.telegram.tgnet.TLRPC$TL_emojiStatus r9 = (org.telegram.tgnet.TLRPC$TL_emojiStatus) r9
            long r12 = r9.document_id
            r11.set((long) r12, (boolean) r1)
            goto L_0x01a6
        L_0x0199:
            r0.nameLayoutEllipsizeByGradient = r5
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r9 = r0.emojiStatus
            org.telegram.ui.Components.Premium.PremiumGradient r11 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r11 = r11.premiumStarDrawableMini
            r9.set((android.graphics.drawable.Drawable) r11, (boolean) r1)
        L_0x01a6:
            r24.invalidate()
        L_0x01a9:
            boolean r9 = r0.isDialogCell
            if (r9 == 0) goto L_0x01d3
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_USER_PRINT
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01d3
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            long r11 = r0.currentDialogId
            java.lang.CharSequence r9 = r9.getPrintingString(r11, r6, r5)
            java.lang.CharSequence r11 = r0.lastPrintString
            if (r11 == 0) goto L_0x01c5
            if (r9 == 0) goto L_0x01d1
        L_0x01c5:
            if (r11 != 0) goto L_0x01c9
            if (r9 != 0) goto L_0x01d1
        L_0x01c9:
            if (r11 == 0) goto L_0x01d3
            boolean r9 = r11.equals(r9)
            if (r9 != 0) goto L_0x01d3
        L_0x01d1:
            r9 = 1
            goto L_0x01d4
        L_0x01d3:
            r9 = 0
        L_0x01d4:
            if (r9 != 0) goto L_0x01e7
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_MESSAGE_TEXT
            r11 = r25 & r11
            if (r11 == 0) goto L_0x01e7
            org.telegram.messenger.MessageObject r11 = r0.message
            if (r11 == 0) goto L_0x01e7
            java.lang.CharSequence r11 = r11.messageText
            java.lang.CharSequence r12 = r0.lastMessageString
            if (r11 == r12) goto L_0x01e7
            r9 = 1
        L_0x01e7:
            if (r9 != 0) goto L_0x0215
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT
            r11 = r25 & r11
            if (r11 == 0) goto L_0x0215
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            if (r11 == 0) goto L_0x0215
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            org.telegram.tgnet.TLRPC$Chat r12 = r0.chat
            long r12 = r12.id
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            boolean r12 = r11.call_active
            if (r12 == 0) goto L_0x020f
            boolean r11 = r11.call_not_empty
            if (r11 == 0) goto L_0x020f
            r11 = 1
            goto L_0x0210
        L_0x020f:
            r11 = 0
        L_0x0210:
            boolean r12 = r0.hasCall
            if (r11 == r12) goto L_0x0215
            r9 = 1
        L_0x0215:
            if (r9 != 0) goto L_0x0222
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r11 = r25 & r11
            if (r11 == 0) goto L_0x0222
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            if (r11 != 0) goto L_0x0222
            r9 = 1
        L_0x0222:
            if (r9 != 0) goto L_0x022f
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r11 = r25 & r11
            if (r11 == 0) goto L_0x022f
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            if (r11 != 0) goto L_0x022f
            r9 = 1
        L_0x022f:
            if (r9 != 0) goto L_0x023c
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r11 = r25 & r11
            if (r11 == 0) goto L_0x023c
            org.telegram.tgnet.TLRPC$User r11 = r0.user
            if (r11 != 0) goto L_0x023c
            r9 = 1
        L_0x023c:
            if (r9 != 0) goto L_0x0249
            int r11 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r11 = r25 & r11
            if (r11 == 0) goto L_0x0249
            org.telegram.tgnet.TLRPC$User r11 = r0.user
            if (r11 != 0) goto L_0x0249
            r9 = 1
        L_0x0249:
            if (r9 != 0) goto L_0x02af
            org.telegram.messenger.MessageObject r11 = r0.message
            if (r11 == 0) goto L_0x0260
            boolean r12 = r0.lastUnreadState
            boolean r11 = r11.isUnread()
            if (r12 == r11) goto L_0x0260
            org.telegram.messenger.MessageObject r9 = r0.message
            boolean r9 = r9.isUnread()
            r0.lastUnreadState = r9
            r9 = 1
        L_0x0260:
            boolean r11 = r0.isDialogCell
            if (r11 == 0) goto L_0x02af
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r11 = r11.dialogs_dict
            long r12 = r0.currentDialogId
            java.lang.Object r11 = r11.get(r12)
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC$Dialog) r11
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r12 == 0) goto L_0x0285
            int r12 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r12 = org.telegram.messenger.MessagesStorage.getInstance(r12)
            int r12 = r12.getArchiveUnreadCount()
        L_0x0282:
            r13 = 0
            r14 = 0
            goto L_0x0290
        L_0x0285:
            if (r11 == 0) goto L_0x028e
            int r12 = r11.unread_count
            int r13 = r11.unread_mentions_count
            int r14 = r11.unread_reactions_count
            goto L_0x0290
        L_0x028e:
            r12 = 0
            goto L_0x0282
        L_0x0290:
            if (r11 == 0) goto L_0x02af
            int r15 = r0.unreadCount
            if (r15 != r12) goto L_0x02a4
            boolean r15 = r0.markUnread
            boolean r3 = r11.unread_mark
            if (r15 != r3) goto L_0x02a4
            int r3 = r0.mentionCount
            if (r3 != r13) goto L_0x02a4
            int r3 = r0.reactionMentionCount
            if (r3 == r14) goto L_0x02af
        L_0x02a4:
            r0.unreadCount = r12
            r0.mentionCount = r13
            boolean r3 = r11.unread_mark
            r0.markUnread = r3
            r0.reactionMentionCount = r14
            r9 = 1
        L_0x02af:
            if (r9 != 0) goto L_0x02c6
            int r3 = org.telegram.messenger.MessagesController.UPDATE_MASK_SEND_STATE
            r3 = r25 & r3
            if (r3 == 0) goto L_0x02c6
            org.telegram.messenger.MessageObject r3 = r0.message
            if (r3 == 0) goto L_0x02c6
            int r11 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.send_state
            if (r11 == r3) goto L_0x02c6
            r0.lastSendState = r3
            r9 = 1
        L_0x02c6:
            if (r9 != 0) goto L_0x02cc
            r24.invalidate()
            return
        L_0x02cc:
            r0.user = r4
            r0.chat = r4
            r0.encryptedChat = r4
            int r3 = r0.currentDialogFolderId
            r11 = 0
            if (r3 == 0) goto L_0x02e9
            r0.dialogMuted = r6
            org.telegram.messenger.MessageObject r3 = r24.findFolderTopMessage()
            r0.message = r3
            if (r3 == 0) goto L_0x02e7
            long r13 = r3.getDialogId()
            goto L_0x0302
        L_0x02e7:
            r13 = r11
            goto L_0x0302
        L_0x02e9:
            boolean r3 = r0.isDialogCell
            if (r3 == 0) goto L_0x02fd
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r13 = r0.currentDialogId
            boolean r3 = r3.isDialogMuted(r13)
            if (r3 == 0) goto L_0x02fd
            r3 = 1
            goto L_0x02fe
        L_0x02fd:
            r3 = 0
        L_0x02fe:
            r0.dialogMuted = r3
            long r13 = r0.currentDialogId
        L_0x0302:
            int r3 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x03a9
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r13)
            if (r3 == 0) goto L_0x0337
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r9 = org.telegram.messenger.DialogObject.getEncryptedChatId(r13)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r9)
            r0.encryptedChat = r3
            if (r3 == 0) goto L_0x0381
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r0.encryptedChat
            long r11 = r9.user_id
            java.lang.Long r9 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r9)
            r0.user = r3
            goto L_0x0381
        L_0x0337:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r3 == 0) goto L_0x034e
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r9 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r9)
            r0.user = r3
            goto L_0x0381
        L_0x034e:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r11 = -r13
            java.lang.Long r9 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r9)
            r0.chat = r3
            boolean r9 = r0.isDialogCell
            if (r9 != 0) goto L_0x0381
            if (r3 == 0) goto L_0x0381
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            if (r3 == 0) goto L_0x0381
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r9 = r9.migrated_to
            long r11 = r9.channel_id
            java.lang.Long r9 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r9)
            if (r3 == 0) goto L_0x0381
            r0.chat = r3
        L_0x0381:
            boolean r3 = r0.useMeForMyMessages
            if (r3 == 0) goto L_0x03a9
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x03a9
            org.telegram.messenger.MessageObject r3 = r0.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x03a9
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r9 = r0.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            long r11 = r9.clientUserId
            java.lang.Long r9 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r9)
            r0.user = r3
        L_0x03a9:
            int r3 = r0.currentDialogFolderId
            if (r3 == 0) goto L_0x03d0
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r3.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r10)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r17 = 0
            r18 = 0
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r20 = 0
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            r22 = 0
            r16 = r3
            r19 = r4
            r21 = r9
            r16.setImage(r17, r18, r19, r20, r21, r22)
            goto L_0x0445
        L_0x03d0:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x0433
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x0400
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r4 = 12
            r3.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r17 = 0
            r18 = 0
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r20 = 0
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            r22 = 0
            r16 = r3
            r19 = r4
            r21 = r9
            r16.setImage(r17, r18, r19, r20, r21, r22)
            goto L_0x0445
        L_0x0400:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x0429
            boolean r3 = r0.useMeForMyMessages
            if (r3 != 0) goto L_0x0429
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r5)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r17 = 0
            r18 = 0
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r20 = 0
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            r22 = 0
            r16 = r3
            r19 = r4
            r21 = r9
            r16.setImage(r17, r18, r19, r20, r21, r22)
            goto L_0x0445
        L_0x0429:
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r3.setForUserOrChat(r9, r11, r4, r5)
            goto L_0x0445
        L_0x0433:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            if (r3 == 0) goto L_0x0445
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r4 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r3.setForUserOrChat(r4, r9)
        L_0x0445:
            r3 = 150(0x96, double:7.4E-322)
            r11 = 220(0xdc, double:1.087E-321)
            if (r1 == 0) goto L_0x058f
            int r9 = r0.unreadCount
            if (r2 != r9) goto L_0x0453
            boolean r9 = r0.markUnread
            if (r8 == r9) goto L_0x058f
        L_0x0453:
            long r13 = java.lang.System.currentTimeMillis()
            long r5 = r0.lastDialogChangedTime
            long r13 = r13 - r5
            r5 = 100
            int r16 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r16 <= 0) goto L_0x058f
            android.animation.ValueAnimator r5 = r0.countAnimator
            if (r5 == 0) goto L_0x0467
            r5.cancel()
        L_0x0467:
            float[] r5 = new float[r10]
            r5 = {0, NUM} // fill-array
            android.animation.ValueAnimator r5 = android.animation.ValueAnimator.ofFloat(r5)
            r0.countAnimator = r5
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda2 r6 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda2
            r6.<init>(r0)
            r5.addUpdateListener(r6)
            android.animation.ValueAnimator r5 = r0.countAnimator
            org.telegram.ui.Cells.DialogCell$1 r6 = new org.telegram.ui.Cells.DialogCell$1
            r6.<init>()
            r5.addListener(r6)
            if (r2 == 0) goto L_0x048a
            boolean r5 = r0.markUnread
            if (r5 == 0) goto L_0x0491
        L_0x048a:
            boolean r5 = r0.markUnread
            if (r5 != 0) goto L_0x04b1
            if (r8 != 0) goto L_0x0491
            goto L_0x04b1
        L_0x0491:
            int r5 = r0.unreadCount
            if (r5 != 0) goto L_0x04a2
            android.animation.ValueAnimator r5 = r0.countAnimator
            r5.setDuration(r3)
            android.animation.ValueAnimator r5 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r5.setInterpolator(r6)
            goto L_0x04c0
        L_0x04a2:
            android.animation.ValueAnimator r5 = r0.countAnimator
            r13 = 430(0x1ae, double:2.124E-321)
            r5.setDuration(r13)
            android.animation.ValueAnimator r5 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r5.setInterpolator(r6)
            goto L_0x04c0
        L_0x04b1:
            android.animation.ValueAnimator r5 = r0.countAnimator
            r5.setDuration(r11)
            android.animation.ValueAnimator r5 = r0.countAnimator
            android.view.animation.OvershootInterpolator r6 = new android.view.animation.OvershootInterpolator
            r6.<init>()
            r5.setInterpolator(r6)
        L_0x04c0:
            boolean r5 = r0.drawCount
            if (r5 == 0) goto L_0x0577
            boolean r5 = r0.drawCount2
            if (r5 == 0) goto L_0x0577
            android.text.StaticLayout r5 = r0.countLayout
            if (r5 == 0) goto L_0x0577
            java.lang.String r5 = java.lang.String.valueOf(r2)
            int r6 = r0.unreadCount
            java.lang.String r6 = java.lang.String.valueOf(r6)
            int r8 = r5.length()
            int r13 = r6.length()
            if (r8 != r13) goto L_0x0571
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r5)
            android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
            r13.<init>(r6)
            android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder
            r14.<init>(r6)
            r9 = 0
        L_0x04f0:
            int r15 = r5.length()
            if (r9 >= r15) goto L_0x0524
            char r15 = r5.charAt(r9)
            char r3 = r6.charAt(r9)
            if (r15 != r3) goto L_0x0514
            org.telegram.ui.Components.EmptyStubSpan r3 = new org.telegram.ui.Components.EmptyStubSpan
            r3.<init>()
            int r4 = r9 + 1
            r15 = 0
            r8.setSpan(r3, r9, r4, r15)
            org.telegram.ui.Components.EmptyStubSpan r3 = new org.telegram.ui.Components.EmptyStubSpan
            r3.<init>()
            r13.setSpan(r3, r9, r4, r15)
            goto L_0x051f
        L_0x0514:
            r15 = 0
            org.telegram.ui.Components.EmptyStubSpan r3 = new org.telegram.ui.Components.EmptyStubSpan
            r3.<init>()
            int r4 = r9 + 1
            r14.setSpan(r3, r9, r4, r15)
        L_0x051f:
            int r9 = r9 + 1
            r3 = 150(0x96, double:7.4E-322)
            goto L_0x04f0
        L_0x0524:
            r15 = 0
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r4.measureText(r5)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r3 = java.lang.Math.max(r3, r4)
            android.text.StaticLayout r4 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r4
            r17 = r8
            r19 = r3
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countOldLayout = r4
            android.text.StaticLayout r4 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = r4
            r17 = r14
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countAnimationStableLayout = r4
            android.text.StaticLayout r4 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = r4
            r17 = r13
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countAnimationInLayout = r4
            goto L_0x0578
        L_0x0571:
            r15 = 0
            android.text.StaticLayout r3 = r0.countLayout
            r0.countOldLayout = r3
            goto L_0x0578
        L_0x0577:
            r15 = 0
        L_0x0578:
            int r3 = r0.countWidth
            r0.countWidthOld = r3
            int r3 = r0.countLeft
            r0.countLeftOld = r3
            int r3 = r0.unreadCount
            if (r3 <= r2) goto L_0x0586
            r2 = 1
            goto L_0x0587
        L_0x0586:
            r2 = 0
        L_0x0587:
            r0.countAnimationIncrement = r2
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.start()
            goto L_0x0590
        L_0x058f:
            r15 = 0
        L_0x0590:
            int r2 = r0.reactionMentionCount
            if (r2 == 0) goto L_0x0596
            r5 = 1
            goto L_0x0597
        L_0x0596:
            r5 = 0
        L_0x0597:
            if (r1 == 0) goto L_0x003d
            if (r5 == r7) goto L_0x003d
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            if (r2 == 0) goto L_0x05a2
            r2.cancel()
        L_0x05a2:
            r2 = 0
            r0.reactionsMentionsChangeProgress = r2
            float[] r3 = new float[r10]
            r3 = {0, NUM} // fill-array
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofFloat(r3)
            r0.reactionsMentionsAnimator = r3
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda0
            r4.<init>(r0)
            r3.addUpdateListener(r4)
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            org.telegram.ui.Cells.DialogCell$2 r4 = new org.telegram.ui.Cells.DialogCell$2
            r4.<init>()
            r3.addListener(r4)
            if (r5 == 0) goto L_0x05d4
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            r3.setDuration(r11)
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            android.view.animation.OvershootInterpolator r4 = new android.view.animation.OvershootInterpolator
            r4.<init>()
            r3.setInterpolator(r4)
            goto L_0x05e2
        L_0x05d4:
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            r4 = 150(0x96, double:7.4E-322)
            r3.setDuration(r4)
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r3.setInterpolator(r4)
        L_0x05e2:
            android.animation.ValueAnimator r3 = r0.reactionsMentionsAnimator
            r3.start()
        L_0x05e7:
            int r3 = r24.getMeasuredWidth()
            if (r3 != 0) goto L_0x05f8
            int r3 = r24.getMeasuredHeight()
            if (r3 == 0) goto L_0x05f4
            goto L_0x05f8
        L_0x05f4:
            r24.requestLayout()
            goto L_0x05fb
        L_0x05f8:
            r24.buildLayout()
        L_0x05fb:
            if (r1 != 0) goto L_0x060e
            boolean r1 = r0.dialogMuted
            if (r1 == 0) goto L_0x0604
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0605
        L_0x0604:
            r3 = 0
        L_0x0605:
            r0.dialogMutedProgress = r3
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x060e
            r1.cancel()
        L_0x060e:
            r24.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$0(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(ValueAnimator valueAnimator) {
        this.reactionsMentionsChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getTranslationX() {
        return this.translationX;
    }

    public void setTranslationX(float f) {
        float f2 = (float) ((int) f);
        this.translationX = f2;
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        boolean z = false;
        if (rLottieDrawable != null && f2 == 0.0f) {
            rLottieDrawable.setProgress(0.0f);
            this.translationAnimationStarted = false;
            this.archiveHidden = SharedConfig.archiveHidden;
            this.currentRevealProgress = 0.0f;
            this.isSliding = false;
        }
        float f3 = this.translationX;
        if (f3 != 0.0f) {
            this.isSliding = true;
        } else {
            this.currentRevealBounceProgress = 0.0f;
            this.currentRevealProgress = 0.0f;
            this.drawRevealBackground = false;
        }
        if (this.isSliding && !this.swipeCanceled) {
            boolean z2 = this.drawRevealBackground;
            if (Math.abs(f3) >= ((float) getMeasuredWidth()) * 0.45f) {
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

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x06bf, code lost:
        if (r0.type == 2) goto L_0x06da;
     */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x05cf  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x05e5  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0773  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0799  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x07ed  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x08e5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0908  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x090d  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x092d  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x093a  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x09fe  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0a20  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0a5e  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0a6e  */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0ab6  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0abd  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0bf0  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0c3e  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x102e  */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x1061  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x1099  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x10b6  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x117a  */
    /* JADX WARNING: Removed duplicated region for block: B:709:0x13fc  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1425  */
    /* JADX WARNING: Removed duplicated region for block: B:730:0x1444  */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x146f  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x14d5  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x152e  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x1541  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x1559  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x1583  */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x15b2  */
    /* JADX WARNING: Removed duplicated region for block: B:810:0x15c6  */
    /* JADX WARNING: Removed duplicated region for block: B:818:0x15e1  */
    /* JADX WARNING: Removed duplicated region for block: B:821:0x15ef  */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1612  */
    /* JADX WARNING: Removed duplicated region for block: B:837:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r41) {
        /*
            r40 = this;
            r8 = r40
            r15 = r41
            long r0 = r8.currentDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0011
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            int r0 = r8.currentDialogFolderId
            r14 = 0
            r13 = 0
            if (r0 == 0) goto L_0x0042
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0042
            float r0 = r0.outProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            float r0 = r8.translationX
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            boolean r0 = r8.drawingForBlur
            if (r0 != 0) goto L_0x0041
            r41.save()
            int r0 = r40.getMeasuredWidth()
            int r1 = r40.getMeasuredHeight()
            r15.clipRect(r14, r14, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r15)
            r41.restore()
        L_0x0041:
            return
        L_0x0042:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r8.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0052
            r2 = 17
        L_0x0052:
            r11 = r2
            r8.lastUpdateTime = r0
            float r0 = r8.clipProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0081
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x0081
            r41.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r40.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r40.getMeasuredHeight()
            int r3 = r8.bottomClip
            float r3 = (float) r3
            float r4 = r8.clipProgress
            float r3 = r3 * r4
            int r3 = (int) r3
            int r2 = r2 - r3
            float r2 = (float) r2
            r15.clipRect(r13, r0, r1, r2)
        L_0x0081:
            float r0 = r8.translationX
            r18 = 1090519040(0x41000000, float:8.0)
            r10 = 4
            r19 = 1073741824(0x40000000, float:2.0)
            r20 = 1082130432(0x40800000, float:4.0)
            r7 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x00b3
            float r0 = r8.cornerProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0098
            goto L_0x00b3
        L_0x0098:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x00af
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r13)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r14
        L_0x00af:
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x046f
        L_0x00b3:
            r41.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r5 = "chats_archivePinBackground"
            java.lang.String r4 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00f4
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00db
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = org.telegram.messenger.R.string.UnhideFromTop
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x00db:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = org.telegram.messenger.R.string.HideOnTop
            java.lang.String r3 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x00f4:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0113
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = org.telegram.messenger.R.string.PsaHide
            java.lang.String r3 = "PsaHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r9
        L_0x0110:
            r9 = r1
            goto L_0x01e3
        L_0x0113:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01c9
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r3 = 3
            if (r2 != r3) goto L_0x014a
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x013d
            int r2 = org.telegram.messenger.R.string.SwipeUnmute
            java.lang.String r3 = "SwipeUnmute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x013d:
            int r2 = org.telegram.messenger.R.string.SwipeMute
            java.lang.String r3 = "SwipeMute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x014a:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r10) goto L_0x0167
            int r2 = org.telegram.messenger.R.string.SwipeDeleteChat
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r9 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x0167:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r7) goto L_0x0193
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x0185
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x0178
            goto L_0x0185
        L_0x0178:
            int r2 = org.telegram.messenger.R.string.SwipeMarkAsUnread
            java.lang.String r3 = "SwipeMarkAsUnread"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x0185:
            int r2 = org.telegram.messenger.R.string.SwipeMarkAsRead
            java.lang.String r3 = "SwipeMarkAsRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x0193:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01bb
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01ad
            int r2 = org.telegram.messenger.R.string.SwipeUnpin
            java.lang.String r3 = "SwipeUnpin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x01ad:
            int r2 = org.telegram.messenger.R.string.SwipePin
            java.lang.String r3 = "SwipePin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x01bb:
            int r2 = org.telegram.messenger.R.string.Archive
            java.lang.String r3 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x01c9:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = org.telegram.messenger.R.string.Unarchive
            java.lang.String r3 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r9
            goto L_0x0110
        L_0x01e3:
            boolean r1 = r8.swipeCanceled
            if (r1 == 0) goto L_0x01f0
            org.telegram.ui.Components.RLottieDrawable r1 = r8.lastDrawTranslationDrawable
            if (r1 == 0) goto L_0x01f0
            r8.translationDrawable = r1
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x01f6
        L_0x01f0:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r1
            r8.lastDrawSwipeMessageStringId = r2
        L_0x01f6:
            boolean r1 = r8.translationAnimationStarted
            if (r1 != 0) goto L_0x021c
            float r1 = r8.translationX
            float r1 = java.lang.Math.abs(r1)
            r17 = 1110179840(0x422CLASSNAME, float:43.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 <= 0) goto L_0x021c
            r8.translationAnimationStarted = r7
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setProgress(r13)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.start()
        L_0x021c:
            int r1 = r40.getMeasuredWidth()
            float r1 = (float) r1
            float r10 = r8.translationX
            float r10 = r10 + r1
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 >= 0) goto L_0x02a3
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r0 = (float) r0
            float r0 = r10 - r0
            r17 = 0
            int r1 = r40.getMeasuredWidth()
            float r1 = (float) r1
            int r6 = r40.getMeasuredHeight()
            float r6 = (float) r6
            android.graphics.Paint r22 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r23 = r1
            r1 = r41
            r29 = r2
            r2 = r0
            r0 = r3
            r3 = r17
            r17 = r4
            r4 = r23
            r23 = r5
            r5 = r6
            r6 = r22
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x02a8
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x0271
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14
        L_0x0271:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x02a8
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r14
            goto L_0x02a8
        L_0x02a3:
            r29 = r2
            r0 = r3
            r23 = r5
        L_0x02a8:
            int r1 = r40.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            r3 = 2
            int r2 = r2 / r3
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x02c8
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02c5
            goto L_0x02c8
        L_0x02c5:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x02ca
        L_0x02c8:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x02ca:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            r4 = 2
            int r3 = r3 / r4
            int r3 = r3 + r1
            org.telegram.ui.Components.RLottieDrawable r5 = r8.translationDrawable
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r4
            int r5 = r5 + r2
            float r4 = r8.currentRevealProgress
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 <= 0) goto L_0x036e
            r41.save()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r4 = r10 - r4
            int r6 = r40.getMeasuredWidth()
            float r6 = (float) r6
            int r14 = r40.getMeasuredHeight()
            float r14 = (float) r14
            r15.clipRect(r4, r13, r6, r14)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r4.setColor(r9)
            int r4 = r3 * r3
            int r6 = r40.getMeasuredHeight()
            int r6 = r5 - r6
            int r9 = r40.getMeasuredHeight()
            int r9 = r5 - r9
            int r6 = r6 * r9
            int r4 = r4 + r6
            double r13 = (double) r4
            double r13 = java.lang.Math.sqrt(r13)
            float r4 = (float) r13
            float r3 = (float) r3
            float r5 = (float) r5
            android.view.animation.AccelerateInterpolator r6 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r9 = r8.currentRevealProgress
            float r6 = r6.getInterpolation(r9)
            float r4 = r4 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r15.drawCircle(r3, r5, r4, r6)
            r41.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x033d
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r23)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r7
        L_0x033d:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x036e
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r23)
            java.lang.String r5 = "Line 1.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r23)
            java.lang.String r5 = "Line 2.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r23)
            java.lang.String r5 = "Line 3.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r7
        L_0x036e:
            r41.save()
            float r1 = (float) r1
            float r2 = (float) r2
            r15.translate(r1, r2)
            float r1 = r8.currentRevealBounceProgress
            r2 = 0
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            r14 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x039e
            int r2 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x039e
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r2 = r8.interpolator
            float r1 = r2.getInterpolation(r1)
            float r1 = r1 + r14
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            r3 = 2
            int r2 = r2 / r3
            float r2 = (float) r2
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r3
            float r3 = (float) r4
            r15.scale(r1, r1, r2, r3)
        L_0x039e:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r2 = 0
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r2, (int) r2)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.draw(r15)
            r41.restore()
            int r1 = r40.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r40.getMeasuredHeight()
            float r2 = (float) r2
            r3 = 0
            r15.clipRect(r10, r3, r1, r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r1.measureText(r0)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = r8.swipeMessageTextId
            r3 = r29
            if (r2 != r3) goto L_0x03d4
            int r2 = r8.swipeMessageWidth
            int r4 = r40.getMeasuredWidth()
            if (r2 == r4) goto L_0x0420
        L_0x03d4:
            r8.swipeMessageTextId = r3
            int r2 = r40.getMeasuredWidth()
            r8.swipeMessageWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r23 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r24 = java.lang.Math.min(r3, r1)
            android.text.Layout$Alignment r25 = android.text.Layout.Alignment.ALIGN_CENTER
            r26 = 1065353216(0x3var_, float:1.0)
            r27 = 0
            r28 = 0
            r21 = r2
            r22 = r0
            r21.<init>(r22, r23, r24, r25, r26, r27, r28)
            r8.swipeMessageTextLayout = r2
            int r2 = r2.getLineCount()
            if (r2 <= r7) goto L_0x0420
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r23 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r3 = 1118044160(0x42a40000, float:82.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r24 = java.lang.Math.min(r3, r1)
            android.text.Layout$Alignment r25 = android.text.Layout.Alignment.ALIGN_CENTER
            r26 = 1065353216(0x3var_, float:1.0)
            r27 = 0
            r28 = 0
            r21 = r2
            r22 = r0
            r21.<init>(r22, r23, r24, r25, r26, r27, r28)
            r8.swipeMessageTextLayout = r2
        L_0x0420:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x046c
            r41.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            if (r0 <= r7) goto L_0x0436
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x0437
        L_0x0436:
            r0 = 0
        L_0x0437:
            int r1 = r40.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            android.text.StaticLayout r2 = r8.swipeMessageTextLayout
            int r2 = r2.getWidth()
            float r2 = (float) r2
            float r2 = r2 / r19
            float r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0459
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0456
            goto L_0x0459
        L_0x0456:
            r2 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x045b
        L_0x0459:
            r2 = 1112014848(0x42480000, float:50.0)
        L_0x045b:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 + r0
            r15.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r15)
            r41.restore()
        L_0x046c:
            r41.restore()
        L_0x046f:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x047e
            r41.save()
            float r0 = r8.translationX
            r15.translate(r0, r1)
        L_0x047e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r0 = (float) r0
            float r1 = r8.cornerProgress
            float r0 = r0 * r1
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x04a3
            android.graphics.RectF r1 = r8.rect
            int r2 = r40.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r40.getMeasuredHeight()
            float r3 = (float) r3
            r4 = 0
            r1.set(r4, r4, r2, r3)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r15.drawRoundRect(r1, r0, r0, r2)
            goto L_0x04a4
        L_0x04a3:
            r4 = 0
        L_0x04a4:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x04da
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x04b2
            float r1 = r8.archiveBackgroundProgress
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x04da
        L_0x04b2:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            r4 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r2, r3, r14)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r40.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r40.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r41
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0502
        L_0x04da:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x04e2
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x0502
        L_0x04e2:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r40.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r40.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r41
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0502:
            float r1 = r8.translationX
            java.lang.String r13 = "windowBackgroundWhite"
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0511
            float r1 = r8.cornerProgress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0597
        L_0x0511:
            r41.save()
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r13, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            int r2 = r40.getMeasuredWidth()
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r40.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r40.getMeasuredHeight()
            float r4 = (float) r4
            r5 = 0
            r1.set(r2, r5, r3, r4)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r15.drawRoundRect(r1, r0, r0, r2)
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x054d
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r15.drawRoundRect(r1, r0, r0, r2)
        L_0x054d:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x0578
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x055c
            float r1 = r8.archiveBackgroundProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0578
        L_0x055c:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            r4 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r2, r3, r14)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r15.drawRoundRect(r1, r0, r0, r2)
            goto L_0x0594
        L_0x0578:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x0580
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x0594
        L_0x0580:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r15.drawRoundRect(r1, r0, r0, r2)
        L_0x0594:
            r41.restore()
        L_0x0597:
            float r0 = r8.translationX
            r21 = 1125515264(0x43160000, float:150.0)
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x05b3
            float r0 = r8.cornerProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x05c9
            float r1 = (float) r11
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x05c6
            r8.cornerProgress = r14
            goto L_0x05c6
        L_0x05b3:
            float r0 = r8.cornerProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x05c9
            float r2 = (float) r11
            float r2 = r2 / r21
            float r0 = r0 - r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x05c6
            r8.cornerProgress = r1
        L_0x05c6:
            r22 = 1
            goto L_0x05cb
        L_0x05c9:
            r22 = 0
        L_0x05cb:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x05dd
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r15)
        L_0x05dd:
            android.text.StaticLayout r0 = r8.nameLayout
            r23 = 1092616192(0x41200000, float:10.0)
            r24 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x0773
            boolean r0 = r8.nameLayoutEllipsizeByGradient
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            if (r0 == 0) goto L_0x0697
            boolean r0 = r8.nameLayoutFits
            if (r0 != 0) goto L_0x0697
            boolean r0 = r8.nameLayoutEllipsizeLeft
            if (r0 == 0) goto L_0x0633
            android.graphics.Paint r0 = r8.fadePaint
            if (r0 != 0) goto L_0x0633
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.fadePaint = r0
            android.graphics.LinearGradient r1 = new android.graphics.LinearGradient
            r31 = 0
            r32 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            r34 = 0
            r3 = 2
            int[] r4 = new int[r3]
            r4 = {-1, 0} // fill-array
            float[] r5 = new float[r3]
            r5 = {0, NUM} // fill-array
            android.graphics.Shader$TileMode r37 = android.graphics.Shader.TileMode.CLAMP
            r30 = r1
            r33 = r2
            r35 = r4
            r36 = r5
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r0.setShader(r1)
            android.graphics.Paint r0 = r8.fadePaint
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.DST_OUT
            r1.<init>(r2)
            r0.setXfermode(r1)
            goto L_0x0672
        L_0x0633:
            android.graphics.Paint r0 = r8.fadePaintBack
            if (r0 != 0) goto L_0x0672
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.fadePaintBack = r0
            android.graphics.LinearGradient r1 = new android.graphics.LinearGradient
            r31 = 0
            r32 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            r34 = 0
            r3 = 2
            int[] r4 = new int[r3]
            r4 = {0, -1} // fill-array
            float[] r5 = new float[r3]
            r5 = {0, NUM} // fill-array
            android.graphics.Shader$TileMode r37 = android.graphics.Shader.TileMode.CLAMP
            r30 = r1
            r33 = r2
            r35 = r4
            r36 = r5
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r0.setShader(r1)
            android.graphics.Paint r0 = r8.fadePaintBack
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.DST_OUT
            r1.<init>(r2)
            r0.setXfermode(r1)
        L_0x0672:
            r2 = 0
            r3 = 0
            int r0 = r40.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r40.getMeasuredHeight()
            float r5 = (float) r0
            r6 = 255(0xff, float:3.57E-43)
            r0 = 31
            r1 = r41
            r10 = 1
            r7 = r0
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            int r0 = r8.nameLeft
            int r1 = r8.nameWidth
            int r1 = r1 + r0
            int r2 = r40.getMeasuredHeight()
            r7 = 0
            r15.clipRect(r0, r7, r1, r2)
            goto L_0x0699
        L_0x0697:
            r7 = 0
            r10 = 1
        L_0x0699:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06b4
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            r6 = 2
            goto L_0x06ef
        L_0x06b4:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x06d9
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x06c2
            int r0 = r0.type
            r6 = 2
            if (r0 != r6) goto L_0x06c3
            goto L_0x06da
        L_0x06c2:
            r6 = 2
        L_0x06c3:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x06ef
        L_0x06d9:
            r6 = 2
        L_0x06da:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x06ef:
            r41.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            float r1 = r8.nameLayoutTranslateX
            float r0 = r0 + r1
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x0704
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x0701
            goto L_0x0704
        L_0x0701:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x0706
        L_0x0704:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x0706:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r15.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r15)
            r41.restore()
            boolean r0 = r8.nameLayoutEllipsizeByGradient
            if (r0 == 0) goto L_0x0775
            boolean r0 = r8.nameLayoutFits
            if (r0 != 0) goto L_0x0775
            r41.save()
            boolean r0 = r8.nameLayoutEllipsizeLeft
            if (r0 == 0) goto L_0x0745
            int r0 = r8.nameLeft
            float r0 = (float) r0
            r1 = 0
            r15.translate(r0, r1)
            r2 = 0
            r3 = 0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r0
            int r0 = r40.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r0 = r8.fadePaint
            r1 = r41
            r16 = 2
            r6 = r0
            r1.drawRect(r2, r3, r4, r5, r6)
            r17 = 0
            goto L_0x076c
        L_0x0745:
            r16 = 2
            int r0 = r8.nameLeft
            int r1 = r8.nameWidth
            int r0 = r0 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r1
            float r0 = (float) r0
            r6 = 0
            r15.translate(r0, r6)
            r2 = 0
            r3 = 0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r0
            int r0 = r40.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r0 = r8.fadePaintBack
            r1 = r41
            r17 = 0
            r6 = r0
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x076c:
            r41.restore()
            r41.restore()
            goto L_0x0779
        L_0x0773:
            r7 = 0
            r10 = 1
        L_0x0775:
            r16 = 2
            r17 = 0
        L_0x0779:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0795
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0795
            r41.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r15.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r15)
            r41.restore()
        L_0x0795:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x07e9
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x07ad
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x07d0
        L_0x07ad:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x07c1
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x07d0
        L_0x07c1:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x07d0:
            r41.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r15.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x07e2 }
            r0.draw(r15)     // Catch:{ Exception -> 0x07e2 }
            goto L_0x07e6
        L_0x07e2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07e6:
            r41.restore()
        L_0x07e9:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x092d
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0821
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x080b
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0836
        L_0x080b:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0836
        L_0x0821:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0836:
            r41.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r15.translate(r0, r1)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x08b0
            r41.save()     // Catch:{ Exception -> 0x08a4 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x08a4 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r15, r0)     // Catch:{ Exception -> 0x08a4 }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x08a4 }
            r0.draw(r15)     // Catch:{ Exception -> 0x08a4 }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x08a4 }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r1 = r8.animatedEmojiStack     // Catch:{ Exception -> 0x08a4 }
            r2 = -1114007142(0xffffffffbd99999a, float:-0.075)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r8.spoilers     // Catch:{ Exception -> 0x08a4 }
            r4 = 0
            r5 = 0
            r6 = 0
            r25 = 1065353216(0x3var_, float:1.0)
            r9 = r41
            r10 = r0
            r38 = r11
            r11 = r1
            r12 = r2
            r1 = r13
            r2 = 0
            r13 = r3
            r3 = 1065353216(0x3var_, float:1.0)
            r14 = r4
            r4 = r15
            r15 = r5
            r16 = r6
            r17 = r25
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r9, r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x08a2 }
            r41.restore()     // Catch:{ Exception -> 0x08a2 }
            r14 = 0
        L_0x087f:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x08a2 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x08a2 }
            if (r14 >= r0) goto L_0x08cf
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x08a2 }
            java.lang.Object r0 = r0.get(r14)     // Catch:{ Exception -> 0x08a2 }
            org.telegram.ui.Components.spoilers.SpoilerEffect r0 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r0     // Catch:{ Exception -> 0x08a2 }
            android.text.StaticLayout r5 = r8.messageLayout     // Catch:{ Exception -> 0x08a2 }
            android.text.TextPaint r5 = r5.getPaint()     // Catch:{ Exception -> 0x08a2 }
            int r5 = r5.getColor()     // Catch:{ Exception -> 0x08a2 }
            r0.setColor(r5)     // Catch:{ Exception -> 0x08a2 }
            r0.draw(r4)     // Catch:{ Exception -> 0x08a2 }
            int r14 = r14 + 1
            goto L_0x087f
        L_0x08a2:
            r0 = move-exception
            goto L_0x08ac
        L_0x08a4:
            r0 = move-exception
            r38 = r11
            r1 = r13
            r4 = r15
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x08ac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x08cf
        L_0x08b0:
            r38 = r11
            r1 = r13
            r4 = r15
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
            android.text.StaticLayout r0 = r8.messageLayout
            r0.draw(r4)
            android.text.StaticLayout r10 = r8.messageLayout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r11 = r8.animatedEmojiStack
            r12 = -1114007142(0xffffffffbd99999a, float:-0.075)
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1065353216(0x3var_, float:1.0)
            r9 = r41
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r9, r10, r11, r12, r13, r14, r15, r16, r17)
        L_0x08cf:
            r41.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0934
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0934
            r41.save()
            int r5 = r8.printingStringType
            r9 = 1
            r10 = 4
            if (r5 == r9) goto L_0x0901
            if (r5 != r10) goto L_0x08e8
            goto L_0x0901
        L_0x08e8:
            int r5 = r8.statusDrawableLeft
            float r5 = (float) r5
            int r6 = r8.messageTop
            float r6 = (float) r6
            r11 = 1099956224(0x41900000, float:18.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r0.getIntrinsicHeight()
            int r11 = r11 - r12
            float r11 = (float) r11
            float r11 = r11 / r19
            float r6 = r6 + r11
            r4.translate(r5, r6)
            goto L_0x0913
        L_0x0901:
            int r6 = r8.statusDrawableLeft
            float r6 = (float) r6
            int r11 = r8.messageTop
            if (r5 != r9) goto L_0x090d
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x090e
        L_0x090d:
            r14 = 0
        L_0x090e:
            int r11 = r11 + r14
            float r5 = (float) r11
            r4.translate(r6, r5)
        L_0x0913:
            r0.draw(r4)
            int r5 = r8.statusDrawableLeft
            int r6 = r8.messageTop
            int r11 = r0.getIntrinsicWidth()
            int r11 = r11 + r5
            int r12 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r12 = r12 + r0
            r8.invalidate(r5, r6, r11, r12)
            r41.restore()
            goto L_0x0936
        L_0x092d:
            r38 = r11
            r1 = r13
            r4 = r15
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0934:
            r9 = 1
            r10 = 4
        L_0x0936:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x09fe
            boolean r0 = r8.drawClock
            boolean r5 = r8.drawCheck1
            if (r5 == 0) goto L_0x0942
            r14 = 2
            goto L_0x0943
        L_0x0942:
            r14 = 0
        L_0x0943:
            int r0 = r0 + r14
            boolean r5 = r8.drawCheck2
            if (r5 == 0) goto L_0x094a
            r14 = 4
            goto L_0x094b
        L_0x094a:
            r14 = 0
        L_0x094b:
            int r0 = r0 + r14
            int r5 = r8.lastStatusDrawableParams
            if (r5 < 0) goto L_0x0959
            if (r5 == r0) goto L_0x0959
            boolean r6 = r8.statusDrawableAnimationInProgress
            if (r6 != 0) goto L_0x0959
            r8.createStatusDrawableAnimator(r5, r0)
        L_0x0959:
            boolean r5 = r8.statusDrawableAnimationInProgress
            if (r5 == 0) goto L_0x095f
            int r0 = r8.animateToStatusDrawableParams
        L_0x095f:
            r6 = r0 & 1
            if (r6 == 0) goto L_0x0965
            r11 = 1
            goto L_0x0966
        L_0x0965:
            r11 = 0
        L_0x0966:
            r6 = r0 & 2
            if (r6 == 0) goto L_0x096c
            r12 = 1
            goto L_0x096d
        L_0x096c:
            r12 = 0
        L_0x096d:
            r0 = r0 & r10
            if (r0 == 0) goto L_0x0972
            r0 = 1
            goto L_0x0973
        L_0x0972:
            r0 = 0
        L_0x0973:
            if (r5 == 0) goto L_0x09d7
            int r5 = r8.animateFromStatusDrawableParams
            r6 = r5 & 1
            if (r6 == 0) goto L_0x097d
            r6 = 1
            goto L_0x097e
        L_0x097d:
            r6 = 0
        L_0x097e:
            r13 = r5 & 2
            if (r13 == 0) goto L_0x0984
            r13 = 1
            goto L_0x0985
        L_0x0984:
            r13 = 0
        L_0x0985:
            r5 = r5 & r10
            if (r5 == 0) goto L_0x098a
            r5 = 1
            goto L_0x098b
        L_0x098a:
            r5 = 0
        L_0x098b:
            if (r11 != 0) goto L_0x09b0
            if (r6 != 0) goto L_0x09b0
            if (r5 == 0) goto L_0x09b0
            if (r13 != 0) goto L_0x09b0
            if (r12 == 0) goto L_0x09b0
            if (r0 == 0) goto L_0x09b0
            r6 = 1
            float r13 = r8.statusDrawableProgress
            r14 = r1
            r1 = r40
            r15 = 0
            r2 = r41
            r5 = 1065353216(0x3var_, float:1.0)
            r3 = r11
            r11 = r4
            r4 = r12
            r12 = 1065353216(0x3var_, float:1.0)
            r5 = r0
            r7 = r13
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            r10 = r11
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x09e9
        L_0x09b0:
            r14 = r1
            r7 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            r15 = 0
            r16 = 0
            float r1 = r8.statusDrawableProgress
            float r17 = r4 - r1
            r1 = r40
            r2 = r41
            r3 = r6
            r6 = 1065353216(0x3var_, float:1.0)
            r4 = r13
            r13 = 1065353216(0x3var_, float:1.0)
            r6 = r16
            r10 = r7
            r7 = r17
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            r6 = 0
            float r7 = r8.statusDrawableProgress
            r3 = r11
            r4 = r12
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x09e9
        L_0x09d7:
            r14 = r1
            r10 = r4
            r13 = 1065353216(0x3var_, float:1.0)
            r15 = 0
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r40
            r2 = r41
            r3 = r11
            r4 = r12
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x09e9:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x09f1
            r1 = 2
            goto L_0x09f2
        L_0x09f1:
            r1 = 0
        L_0x09f2:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x09f9
            r1 = 4
            goto L_0x09fa
        L_0x09f9:
            r1 = 0
        L_0x09fa:
            int r0 = r0 + r1
            r8.lastStatusDrawableParams = r0
            goto L_0x0a03
        L_0x09fe:
            r14 = r1
            r10 = r4
            r13 = 1065353216(0x3var_, float:1.0)
            r15 = 0
        L_0x0a03:
            int r0 = r8.dialogsType
            r1 = 1132396544(0x437var_, float:255.0)
            r7 = 2
            if (r0 == r7) goto L_0x0abd
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0a14
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r2 <= 0) goto L_0x0abd
        L_0x0a14:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x0abd
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x0abd
            boolean r2 = r8.drawPremium
            if (r2 != 0) goto L_0x0abd
            if (r0 == 0) goto L_0x0a39
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0a39
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a35
            r8.dialogMutedProgress = r13
            goto L_0x0a51
        L_0x0a35:
            r40.invalidate()
            goto L_0x0a51
        L_0x0a39:
            if (r0 != 0) goto L_0x0a51
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r2 == 0) goto L_0x0a51
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a4e
            r8.dialogMutedProgress = r15
            goto L_0x0a51
        L_0x0a4e:
            r40.invalidate()
        L_0x0a51:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a61
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a5e
            goto L_0x0a61
        L_0x0a5e:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0a62
        L_0x0a61:
            r3 = 0
        L_0x0a62:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a6e
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x0a70
        L_0x0a6e:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0a70:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0ab6
            r41.save()
            float r0 = r8.dialogMutedProgress
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerX()
            float r2 = (float) r2
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerY()
            float r3 = (float) r3
            r10.scale(r0, r0, r2, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            float r2 = r8.dialogMutedProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r10)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r2 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r2)
            r41.restore()
            goto L_0x0bc4
        L_0x0ab6:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r10)
            goto L_0x0bc4
        L_0x0abd:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0b09
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0ad6
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0ad3
            goto L_0x0ad6
        L_0x0ad3:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0ad8
        L_0x0ad6:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x0ad8:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0af4
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0af1
            goto L_0x0af4
        L_0x0af1:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0af6
        L_0x0af4:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x0af6:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r10)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r10)
            goto L_0x0bc4
        L_0x0b09:
            boolean r0 = r8.drawPremium
            if (r0 == 0) goto L_0x0b96
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r8.emojiStatus
            if (r0 == 0) goto L_0x0b70
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0b24
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0b21
            goto L_0x0b24
        L_0x0b21:
            r3 = 1098383360(0x41780000, float:15.5)
            goto L_0x0b26
        L_0x0b24:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0b26:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r3 = r3 - r4
            int r4 = r8.nameMuteLeft
            r5 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            boolean r5 = r8.useForceThreeLines
            if (r5 != 0) goto L_0x0b44
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0b41
            goto L_0x0b44
        L_0x0b41:
            r5 = 1098383360(0x41780000, float:15.5)
            goto L_0x0b46
        L_0x0b44:
            r5 = 1095237632(0x41480000, float:12.5)
        L_0x0b46:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 - r6
            r6 = 1102053376(0x41b00000, float:22.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            r0.setBounds(r2, r3, r4, r5)
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r8.emojiStatus
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_verifiedBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.setColor(r2)
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r8.emojiStatus
            r0.draw(r10)
            goto L_0x0bc4
        L_0x0b70:
            org.telegram.ui.Components.Premium.PremiumGradient r0 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r0 = r0.premiumStarDrawableMini
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0b89
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0b86
            goto L_0x0b89
        L_0x0b86:
            r3 = 1098383360(0x41780000, float:15.5)
            goto L_0x0b8b
        L_0x0b89:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0b8b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            r0.draw(r10)
            goto L_0x0bc4
        L_0x0b96:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0bc4
            if (r0 != r9) goto L_0x0b9f
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0ba1
        L_0x0b9f:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0ba1:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0baf
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0bac
            goto L_0x0baf
        L_0x0bac:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0bb1
        L_0x0baf:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0bb1:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            if (r0 != r9) goto L_0x0bbf
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0bc1
        L_0x0bbf:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0bc1:
            r0.draw(r10)
        L_0x0bc4:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0bce
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0be6
        L_0x0bce:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            int r2 = r8.pinLeft
            int r3 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            r0.draw(r10)
        L_0x0be6:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1084227584(0x40a00000, float:5.0)
            r4 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0c3e
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r5 = r8.reorderIconProgress
            float r6 = r13 - r5
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r5 = (float) r1
            int r6 = r8.errorTop
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r11
            float r1 = (float) r1
            int r11 = r8.errorTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r11 = r11 + r2
            float r2 = (float) r11
            r0.set(r5, r6, r1, r2)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r1 * r4
            float r1 = r1 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r10.drawRoundRect(r0, r2, r1, r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r4
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r10)
            goto L_0x1028
        L_0x0c3e:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r5 = r8.drawMention
            if (r5 == 0) goto L_0x0c4a
        L_0x0CLASSNAME:
            boolean r5 = r8.drawCount2
            if (r5 != 0) goto L_0x0c7b
        L_0x0c4a:
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r5 != 0) goto L_0x0c7b
            boolean r5 = r8.drawReactionMention
            if (r5 != 0) goto L_0x0c7b
            float r5 = r8.reactionsMentionsChangeProgress
            int r5 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r5 == 0) goto L_0x0c5b
            goto L_0x0c7b
        L_0x0c5b:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x1028
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r6 = r13 - r2
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r10)
            goto L_0x1028
        L_0x0c7b:
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r0 = r8.drawCount2
            if (r0 != 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0edc
        L_0x0CLASSNAME:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x0CLASSNAME
            float r5 = r8.countChangeProgress
            float r6 = r13 - r5
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            float r6 = r8.countChangeProgress
        L_0x0CLASSNAME:
            android.text.StaticLayout r5 = r8.countOldLayout
            if (r5 == 0) goto L_0x0e0a
            if (r0 != 0) goto L_0x0c9e
            goto L_0x0e0a
        L_0x0c9e:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0caa
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0ca7
            goto L_0x0caa
        L_0x0ca7:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0cac
        L_0x0caa:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0cac:
            float r5 = r8.reorderIconProgress
            float r5 = r13 - r5
            float r5 = r5 * r1
            int r5 = (int) r5
            r0.setAlpha(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r11 = r8.reorderIconProgress
            float r11 = r13 - r11
            float r11 = r11 * r1
            int r11 = (int) r11
            r5.setAlpha(r11)
            float r5 = r6 * r19
            int r11 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r11 <= 0) goto L_0x0ccb
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x0ccc
        L_0x0ccb:
            r11 = r5
        L_0x0ccc:
            int r12 = r8.countLeft
            float r12 = (float) r12
            float r12 = r12 * r11
            int r9 = r8.countLeftOld
            float r9 = (float) r9
            float r16 = r13 - r11
            float r9 = r9 * r16
            float r12 = r12 + r9
            r9 = 1085276160(0x40b00000, float:5.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r9 = r12 - r9
            android.graphics.RectF r3 = r8.rect
            int r15 = r8.countTop
            float r15 = (float) r15
            int r7 = r8.countWidth
            float r7 = (float) r7
            float r7 = r7 * r11
            float r7 = r7 + r9
            int r1 = r8.countWidthOld
            float r1 = (float) r1
            float r1 = r1 * r16
            float r7 = r7 + r1
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r7 = r7 + r1
            int r1 = r8.countTop
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r26
            float r1 = (float) r1
            r3.set(r9, r15, r7, r1)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0d17
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r5)
            goto L_0x0d27
        L_0x0d17:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r5 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 - r5
            float r6 = r6 * r19
            float r6 = r13 - r6
            float r3 = r3.getInterpolation(r6)
        L_0x0d27:
            float r3 = r3 * r1
            float r3 = r3 + r13
            r41.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r5 = r8.rect
            float r5 = r5.centerY()
            r10.scale(r3, r3, r1, r5)
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r3 * r4
            float r3 = r3 * r4
            r10.drawRoundRect(r1, r5, r3, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            if (r0 == 0) goto L_0x0d61
            r41.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r1
            float r0 = (float) r0
            r10.translate(r12, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r10)
            r41.restore()
        L_0x0d61:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r5 = r3 * r11
            int r5 = (int) r5
            r1.setAlpha(r5)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0d9e
            r41.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0d80
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x0d85
        L_0x0d80:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
        L_0x0d85:
            float r1 = (float) r1
            float r1 = r1 * r16
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r1 = r1 + r5
            r10.translate(r12, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r10)
            r41.restore()
            goto L_0x0dcb
        L_0x0d9e:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0dcb
            r41.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0dae
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x0db3
        L_0x0dae:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
        L_0x0db3:
            float r1 = (float) r1
            float r1 = r1 * r16
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r1 = r1 + r5
            r10.translate(r12, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r10)
            r41.restore()
        L_0x0dcb:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0e00
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r16
            int r3 = (int) r3
            r1.setAlpha(r3)
            r41.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0de4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
            goto L_0x0de8
        L_0x0de4:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
        L_0x0de8:
            float r1 = (float) r1
            float r1 = r1 * r11
            int r3 = r8.countTop
            float r3 = (float) r3
            float r1 = r1 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r3 = (float) r3
            float r1 = r1 + r3
            r10.translate(r12, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r10)
            r41.restore()
        L_0x0e00:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r41.restore()
            goto L_0x0edc
        L_0x0e0a:
            if (r0 != 0) goto L_0x0e0d
            goto L_0x0e0f
        L_0x0e0d:
            android.text.StaticLayout r5 = r8.countLayout
        L_0x0e0f:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0e1b
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0e18
            goto L_0x0e1b
        L_0x0e18:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0e1d
        L_0x0e1b:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0e1d:
            float r1 = r8.reorderIconProgress
            float r1 = r13 - r1
            r3 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r3
            int r1 = (int) r1
            r0.setAlpha(r1)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r13 - r7
            float r7 = r7 * r3
            int r3 = (int) r7
            r1.setAlpha(r3)
            int r1 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r3
            android.graphics.RectF r3 = r8.rect
            float r7 = (float) r1
            int r9 = r8.countTop
            float r9 = (float) r9
            int r11 = r8.countWidth
            int r1 = r1 + r11
            r11 = 1093664768(0x41300000, float:11.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = r1 + r11
            float r1 = (float) r1
            int r11 = r8.countTop
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r11 = r11 + r12
            float r11 = (float) r11
            r3.set(r7, r9, r1, r11)
            int r1 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0eb1
            boolean r1 = r8.drawPin
            if (r1 == 0) goto L_0x0e9f
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r3 = r8.reorderIconProgress
            float r3 = r13 - r3
            r7 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r7
            int r3 = (int) r3
            r1.setAlpha(r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r3 = r8.pinLeft
            int r7 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r3, (int) r7)
            r41.save()
            float r1 = r13 - r6
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerX()
            float r3 = (float) r3
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r7 = r7.getBounds()
            int r7 = r7.centerY()
            float r7 = (float) r7
            r10.scale(r1, r1, r3, r7)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r10)
            r41.restore()
        L_0x0e9f:
            r41.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r10.scale(r6, r6, r1, r3)
        L_0x0eb1:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r3 * r4
            float r3 = r3 * r4
            r10.drawRoundRect(r1, r7, r3, r0)
            if (r5 == 0) goto L_0x0ed5
            r41.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r1 + r3
            float r1 = (float) r1
            r10.translate(r0, r1)
            r5.draw(r10)
            r41.restore()
        L_0x0ed5:
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0edc
            r41.restore()
        L_0x0edc:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0f8c
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r3 = (float) r0
            int r5 = r8.countTop
            float r5 = (float) r5
            int r6 = r8.mentionWidth
            int r0 = r0 + r6
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            float r0 = (float) r0
            int r6 = r8.countTop
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r6 + r7
            float r6 = (float) r6
            r1.set(r3, r5, r0, r6)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0f1e
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0f1e
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0var_
        L_0x0f1e:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0var_:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r3 * r4
            float r3 = r3 * r4
            r10.drawRoundRect(r1, r5, r3, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0var_
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            r41.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r1 + r3
            float r1 = (float) r1
            r10.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r10)
            r41.restore()
            goto L_0x0f8c
        L_0x0var_:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r1 - r3
            int r3 = r8.countTop
            r5 = 1078774989(0x404ccccd, float:3.2)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r1, r3, r5, r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r10)
        L_0x0f8c:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0var_
            float r0 = r8.reactionsMentionsChangeProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x1028
        L_0x0var_:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            int r0 = r8.reactionMentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r3 = (float) r0
            int r5 = r8.countTop
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r6
            float r0 = (float) r0
            int r6 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r6 + r2
            float r2 = (float) r6
            r1.set(r3, r5, r0, r2)
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            r41.save()
            float r1 = r8.reactionsMentionsChangeProgress
            int r2 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x0fe5
            boolean r2 = r8.drawReactionMention
            if (r2 == 0) goto L_0x0fd4
            goto L_0x0fd6
        L_0x0fd4:
            float r1 = r13 - r1
        L_0x0fd6:
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r10.scale(r1, r1, r2, r3)
        L_0x0fe5:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r2 * r4
            float r2 = r2 * r4
            r10.drawRoundRect(r1, r3, r2, r0)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            int r1 = r8.reactionMentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r1 - r2
            int r2 = r8.countTop
            r3 = 1081291571(0x40733333, float:3.8)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r1, r2, r3, r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            r0.draw(r10)
            r41.restore()
        L_0x1028:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x104a
            r41.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 + r13
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r10.scale(r0, r0, r1, r2)
        L_0x104a:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1058
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x1058
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x105d
        L_0x1058:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r10)
        L_0x105d:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x1095
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r10)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x1095
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            float r0 = r0.getCenterX()
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r1 = r1.getIntrinsicWidth()
            r2 = 2
            int r1 = r1 / r2
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            org.telegram.messenger.ImageReceiver r1 = r8.thumbImage
            float r1 = r1.getCenterY()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r2
            float r2 = (float) r3
            float r1 = r1 - r2
            int r1 = (int) r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r0, (int) r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            r0.draw(r10)
        L_0x1095:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x109c
            r41.restore()
        L_0x109c:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x143a
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x143a
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x117a
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x117a
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x117a
            boolean r0 = r40.isOnline()
            if (r0 != 0) goto L_0x10c3
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x143a
        L_0x10c3:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x10d1
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x10d3
        L_0x10d1:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x10d3:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x10f6
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x10ef
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x10ed
            goto L_0x10ef
        L_0x10ed:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x10ef:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x110d
        L_0x10f6:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1107
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1105
            goto L_0x1107
        L_0x1105:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1107:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x110d:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r14, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r1 = (float) r1
            float r2 = (float) r2
            r3 = 1088421888(0x40e00000, float:7.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.onlineProgress
            float r3 = r3 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r1, r2, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            java.lang.String r5 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            r3 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.onlineProgress
            float r3 = r3 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r1, r2, r3, r4)
            if (r0 == 0) goto L_0x115f
            float r0 = r8.onlineProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x143a
            r11 = r38
            float r1 = (float) r11
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1174
            r8.onlineProgress = r13
            goto L_0x1174
        L_0x115f:
            r11 = r38
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1176
            float r2 = (float) r11
            float r2 = r2 / r21
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1174
            r8.onlineProgress = r1
        L_0x1174:
            r22 = 1
        L_0x1176:
            r2 = 0
            r9 = 0
            goto L_0x143e
        L_0x117a:
            r11 = r38
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x1176
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x118a
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x118a
            r0 = 1
            goto L_0x118b
        L_0x118a:
            r0 = 0
        L_0x118b:
            r8.hasCall = r0
            if (r0 != 0) goto L_0x1196
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1176
        L_0x1196:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x11a9
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x11a9
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r6 = r13 - r0
            goto L_0x11ab
        L_0x11a9:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x11ab:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x11b9
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x11bb
        L_0x11b9:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x11bb:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x11de
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x11d7
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x11d5
            goto L_0x11d7
        L_0x11d5:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x11d7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x11f5
        L_0x11de:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x11ef
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x11ed
            goto L_0x11ef
        L_0x11ed:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x11ef:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x11f5:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r8.resourcesProvider
            int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r14, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r2.setColor(r3)
            float r2 = (float) r1
            float r0 = (float) r0
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.chatCallProgress
            float r3 = r3 * r4
            float r3 = r3 * r6
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            java.lang.String r5 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            r3 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.chatCallProgress
            float r3 = r3 * r4
            float r3 = r3 * r6
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r14, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            if (r3 != 0) goto L_0x1265
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r9 = r8.innerProgress
        L_0x1260:
            float r5 = r5 * r9
            float r4 = r4 - r5
            goto L_0x1354
        L_0x1265:
            r4 = 1
            if (r3 != r4) goto L_0x128a
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r9 = r8.innerProgress
        L_0x1285:
            float r5 = r5 * r9
            float r4 = r4 + r5
            goto L_0x1354
        L_0x128a:
            r4 = 2
            if (r3 != r4) goto L_0x12ab
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r9 = r8.innerProgress
            goto L_0x1260
        L_0x12ab:
            r4 = 3
            if (r3 != r4) goto L_0x12cc
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r9 = r8.innerProgress
            goto L_0x1285
        L_0x12cc:
            r4 = 4
            if (r3 != r4) goto L_0x12ee
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r9 = r8.innerProgress
            goto L_0x1260
        L_0x12ee:
            r4 = 5
            if (r3 != r4) goto L_0x1310
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r9 = r8.innerProgress
            goto L_0x1285
        L_0x1310:
            r4 = 6
            if (r3 != r4) goto L_0x1335
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r9 = (float) r9
            float r15 = r8.innerProgress
            float r9 = r9 * r15
            float r5 = r5 - r9
            r4 = r5
            goto L_0x1354
        L_0x1335:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r9 = r8.innerProgress
            goto L_0x1285
        L_0x1354:
            float r5 = r8.chatCallProgress
            int r5 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r5 < 0) goto L_0x135e
            int r5 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r5 >= 0) goto L_0x136a
        L_0x135e:
            r41.save()
            float r5 = r8.chatCallProgress
            float r9 = r5 * r6
            float r5 = r5 * r6
            r10.scale(r9, r5, r2, r0)
        L_0x136a:
            android.graphics.RectF r2 = r8.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r5 = r1 - r5
            float r5 = (float) r5
            float r9 = r0 - r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r15 + r1
            float r15 = (float) r15
            float r3 = r3 + r0
            r2.set(r5, r9, r15, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            android.graphics.Paint r9 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawRoundRect(r2, r3, r5, r9)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r5
            float r3 = (float) r3
            float r5 = r0 - r4
            r9 = 1077936128(0x40400000, float:3.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r1 - r9
            float r9 = (float) r9
            float r0 = r0 + r4
            r2.set(r3, r5, r9, r0)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r4 = (float) r4
            android.graphics.Paint r9 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawRoundRect(r2, r3, r4, r9)
            android.graphics.RectF r2 = r8.rect
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r3 = (float) r3
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r4
            float r1 = (float) r1
            r2.set(r3, r5, r1, r0)
            android.graphics.RectF r0 = r8.rect
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r2 = (float) r2
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawRoundRect(r0, r1, r2, r3)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x13eb
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x13ee
        L_0x13eb:
            r41.restore()
        L_0x13ee:
            float r0 = r8.innerProgress
            float r1 = (float) r11
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x140d
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x140d
            r9 = 0
            r8.progressStage = r9
            goto L_0x140e
        L_0x140d:
            r9 = 0
        L_0x140e:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x1425
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x1423
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1423
            r8.chatCallProgress = r13
        L_0x1423:
            r2 = 0
            goto L_0x1437
        L_0x1425:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x1437
            float r1 = r1 / r21
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x1437
            r8.chatCallProgress = r2
        L_0x1437:
            r22 = 1
            goto L_0x143e
        L_0x143a:
            r11 = r38
            goto L_0x1176
        L_0x143e:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1447
            r41.restore()
        L_0x1447:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x146b
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x146b
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x146b
            r41.save()
            int r0 = r40.getMeasuredWidth()
            int r1 = r40.getMeasuredHeight()
            r10.clipRect(r9, r9, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r10)
            r41.restore()
        L_0x146b:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x14cd
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x148f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x147f
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x147f
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x148f
        L_0x147f:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1488
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x1488
            goto L_0x148f
        L_0x1488:
            r0 = 1116733440(0x42900000, float:72.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1490
        L_0x148f:
            r0 = 0
        L_0x1490:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x14b2
            r2 = 0
            int r1 = r40.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r40.getMeasuredWidth()
            int r1 = r1 - r0
            float r0 = (float) r1
            int r1 = r40.getMeasuredHeight()
            int r1 = r1 - r3
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r41
            r3 = r4
            r4 = r0
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x14cd
        L_0x14b2:
            float r2 = (float) r0
            int r0 = r40.getMeasuredHeight()
            r15 = 1
            int r0 = r0 - r15
            float r3 = (float) r0
            int r0 = r40.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r40.getMeasuredHeight()
            int r0 = r0 - r15
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r41
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x14ce
        L_0x14cd:
            r15 = 1
        L_0x14ce:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x151e
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x14df
            r41.restore()
            goto L_0x151e
        L_0x14df:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r14, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r40.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r41
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r40.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r40.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r40.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r41
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x151e:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x152c
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x152a
            goto L_0x152c
        L_0x152a:
            r1 = 0
            goto L_0x1555
        L_0x152c:
            if (r0 == 0) goto L_0x1541
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x152a
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x153f
            r8.reorderIconProgress = r13
        L_0x153f:
            r1 = 0
            goto L_0x1553
        L_0x1541:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1555
            float r2 = (float) r11
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1553
            r8.reorderIconProgress = r1
        L_0x1553:
            r22 = 1
        L_0x1555:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1583
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x15ae
            float r2 = (float) r11
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x156c
            r8.archiveBackgroundProgress = r1
        L_0x156c:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x15ac
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x15ac
        L_0x1583:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x15ae
            float r1 = (float) r11
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1596
            r8.archiveBackgroundProgress = r13
        L_0x1596:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x15ac
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x15ac:
            r22 = 1
        L_0x15ae:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x15c2
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r11
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x15c0
            r8.animatingArchiveAvatarProgress = r7
            r8.animatingArchiveAvatar = r9
        L_0x15c0:
            r22 = 1
        L_0x15c2:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x15ef
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x15d9
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x15d9
            r8.currentRevealBounceProgress = r13
            r7 = 1
            goto L_0x15db
        L_0x15d9:
            r7 = r22
        L_0x15db:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x1610
            float r1 = (float) r11
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x160f
            r8.currentRevealProgress = r13
            goto L_0x160f
        L_0x15ef:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x15fa
            r8.currentRevealBounceProgress = r1
            r7 = 1
            goto L_0x15fc
        L_0x15fa:
            r7 = r22
        L_0x15fc:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1610
            float r2 = (float) r11
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x160f
            r8.currentRevealProgress = r1
        L_0x160f:
            r7 = 1
        L_0x1610:
            if (r7 == 0) goto L_0x1615
            r40.invalidate()
        L_0x1615:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: private */
    public void createStatusDrawableAnimator(int i, int i2) {
        this.statusDrawableProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.statusDrawableAnimator = ofFloat;
        ofFloat.setDuration(220);
        this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animateFromStatusDrawableParams = i;
        this.animateToStatusDrawableParams = i2;
        this.statusDrawableAnimator.addUpdateListener(new DialogCell$$ExternalSyntheticLambda1(this));
        this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                int i = (DialogCell.this.drawClock ? 1 : 0) + (DialogCell.this.drawCheck1 ? 2 : 0) + (DialogCell.this.drawCheck2 ? 4 : 0);
                if (DialogCell.this.animateToStatusDrawableParams != i) {
                    DialogCell dialogCell = DialogCell.this;
                    dialogCell.createStatusDrawableAnimator(dialogCell.animateToStatusDrawableParams, i);
                } else {
                    boolean unused = DialogCell.this.statusDrawableAnimationInProgress = false;
                    DialogCell dialogCell2 = DialogCell.this;
                    int unused2 = dialogCell2.lastStatusDrawableParams = dialogCell2.animateToStatusDrawableParams;
                }
                DialogCell.this.invalidate();
            }
        });
        this.statusDrawableAnimationInProgress = true;
        this.statusDrawableAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createStatusDrawableAnimator$2(ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void startOutAnimation() {
        PullForegroundDrawable pullForegroundDrawable = this.archivedChatsDrawable;
        if (pullForegroundDrawable != null) {
            pullForegroundDrawable.outCy = this.avatarImage.getCenterY();
            this.archivedChatsDrawable.outCx = this.avatarImage.getCenterX();
            this.archivedChatsDrawable.outRadius = this.avatarImage.getImageWidth() / 2.0f;
            this.archivedChatsDrawable.outImageSize = (float) this.avatarImage.getBitmapWidth();
            this.archivedChatsDrawable.startOutAnimation();
        }
    }

    public void onReorderStateChanged(boolean z, boolean z2) {
        boolean z3 = this.drawPin;
        if ((z3 || !z) && this.drawReorder != z) {
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
        } else if (!z3) {
            this.drawReorder = false;
        }
    }

    public void setSliding(boolean z) {
        this.isSliding = z;
    }

    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.translationDrawable || drawable == Theme.dialogs_archiveAvatarDrawable) {
            invalidate(drawable.getBounds());
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        DialogsActivity dialogsActivity;
        if (i != R.id.acc_action_chat_preview || (dialogsActivity = this.parentFragment) == null) {
            return super.performAccessibilityAction(i, bundle);
        }
        dialogsActivity.showChatPreview(this);
        return true;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        PullForegroundDrawable pullForegroundDrawable;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!isFolderCell() || (pullForegroundDrawable = this.archivedChatsDrawable) == null || !SharedConfig.archiveHidden || pullForegroundDrawable.pullProgress != 0.0f) {
            accessibilityNodeInfo.addAction(16);
            accessibilityNodeInfo.addAction(32);
            if (!isFolderCell() && this.parentFragment != null && Build.VERSION.SDK_INT >= 21) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_chat_preview, LocaleController.getString("AccActionChatPreview", R.string.AccActionChatPreview)));
            }
        } else {
            accessibilityNodeInfo.setVisibleToUser(false);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            accessibilityNodeInfo.setClassName("android.widget.CheckBox");
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        TLRPC$User user2;
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
        String formatDateAudio = LocaleController.formatDateAudio((long) i3, true);
        if (this.message.isOut()) {
            sb.append(LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, formatDateAudio));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, formatDateAudio));
        }
        sb.append(". ");
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.message.messageOwner.from_id.user_id))) != null) {
            sb.append(ContactsController.formatName(user2.first_name, user2.last_name));
            sb.append(". ");
        }
        if (this.encryptedChat == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && !TextUtils.isEmpty(this.message.caption)) {
                sb2.append(". ");
                sb2.append(this.message.caption);
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
                sb.append(sb2);
            }
        }
        accessibilityEvent.setContentDescription(sb.toString());
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
}
