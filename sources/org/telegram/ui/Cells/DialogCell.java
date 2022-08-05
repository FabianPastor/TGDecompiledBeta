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
    private TLRPC$EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
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
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
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
                str = LocaleController.getString("HiddenName", NUM);
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

    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0472, code lost:
        if (r2.post_messages == false) goto L_0x044e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x047e, code lost:
        if (r2.kicked != false) goto L_0x044e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0ec5, code lost:
        if (r3 != null) goto L_0x0ec9;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1001:0x1753  */
    /* JADX WARNING: Removed duplicated region for block: B:1002:0x175c  */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1782 A[Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x178b A[ADDED_TO_REGION, Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x17a6 A[Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1032:0x17d8 A[Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x182d A[Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1048:0x1830 A[Catch:{ Exception -> 0x1868 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1055:0x1873  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x19da  */
    /* JADX WARNING: Removed duplicated region for block: B:1144:0x1a7d A[Catch:{ Exception -> 0x1aa8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1147:0x1a98 A[Catch:{ Exception -> 0x1aa8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1155:0x1ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:1175:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x042f  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0484  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x050e  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0add A[Catch:{ Exception -> 0x0af0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0ae4 A[Catch:{ Exception -> 0x0af0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0ba5 A[SYNTHETIC, Splitter:B:525:0x0ba5] */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0bc5  */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0bf3  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0e1e  */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0ece  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0ed8  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0ee6  */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x0eee  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0f0b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0f1f  */
    /* JADX WARNING: Removed duplicated region for block: B:742:0x1041  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x10a0  */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x10a9  */
    /* JADX WARNING: Removed duplicated region for block: B:762:0x10b9  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x110b  */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x1117  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x1161  */
    /* JADX WARNING: Removed duplicated region for block: B:794:0x1171  */
    /* JADX WARNING: Removed duplicated region for block: B:797:0x1189  */
    /* JADX WARNING: Removed duplicated region for block: B:800:0x119d  */
    /* JADX WARNING: Removed duplicated region for block: B:805:0x11cb  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1254  */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x126a  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x12cd A[Catch:{ Exception -> 0x132b }] */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x130c A[Catch:{ Exception -> 0x132b }] */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x130f A[Catch:{ Exception -> 0x132b }] */
    /* JADX WARNING: Removed duplicated region for block: B:865:0x1339  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x13eb  */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x14a1  */
    /* JADX WARNING: Removed duplicated region for block: B:883:0x14c6  */
    /* JADX WARNING: Removed duplicated region for block: B:888:0x14f6  */
    /* JADX WARNING: Removed duplicated region for block: B:958:0x1696  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r36 = this;
            r1 = r36
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
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 != 0) goto L_0x00da
            boolean r11 = r1.useMeForMyMessages
            if (r11 != 0) goto L_0x00da
            r11 = 1
            goto L_0x00db
        L_0x00da:
            r11 = 0
        L_0x00db:
            r12 = -1
            r1.printingStringType = r12
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x00f4
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00ea
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x00ee
        L_0x00ea:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x00f1
        L_0x00ee:
            java.lang.String r13 = "%2$s: ⁨%1$s⁩"
            goto L_0x0102
        L_0x00f1:
            java.lang.String r13 = "⁨%s⁩"
            goto L_0x0106
        L_0x00f4:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00fc
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x0100
        L_0x00fc:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0104
        L_0x0100:
            java.lang.String r13 = "%2$s: %1$s"
        L_0x0102:
            r14 = 1
            goto L_0x0107
        L_0x0104:
            java.lang.String r13 = "%1$s"
        L_0x0106:
            r14 = 0
        L_0x0107:
            org.telegram.messenger.MessageObject r15 = r1.message
            if (r15 == 0) goto L_0x010e
            java.lang.CharSequence r15 = r15.messageText
            goto L_0x010f
        L_0x010e:
            r15 = 0
        L_0x010f:
            boolean r12 = r15 instanceof android.text.Spannable
            if (r12 == 0) goto L_0x014b
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r15)
            int r15 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderlineBold> r5 = org.telegram.ui.Components.URLSpanNoUnderlineBold.class
            java.lang.Object[] r5 = r12.getSpans(r7, r15, r5)
            org.telegram.ui.Components.URLSpanNoUnderlineBold[] r5 = (org.telegram.ui.Components.URLSpanNoUnderlineBold[]) r5
            int r15 = r5.length
            r2 = 0
        L_0x0126:
            if (r2 >= r15) goto L_0x0132
            r3 = r5[r2]
            r12.removeSpan(r3)
            int r2 = r2 + 1
            r3 = 1099431936(0x41880000, float:17.0)
            goto L_0x0126
        L_0x0132:
            int r2 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderline> r3 = org.telegram.ui.Components.URLSpanNoUnderline.class
            java.lang.Object[] r2 = r12.getSpans(r7, r2, r3)
            org.telegram.ui.Components.URLSpanNoUnderline[] r2 = (org.telegram.ui.Components.URLSpanNoUnderline[]) r2
            int r3 = r2.length
            r5 = 0
        L_0x0140:
            if (r5 >= r3) goto L_0x014a
            r15 = r2[r5]
            r12.removeSpan(r15)
            int r5 = r5 + 1
            goto L_0x0140
        L_0x014a:
            r15 = r12
        L_0x014b:
            r1.lastMessageString = r15
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            r3 = 1102053376(0x41b00000, float:22.0)
            r5 = 1117257728(0x42980000, float:76.0)
            r12 = 1117519872(0x429CLASSNAME, float:78.0)
            r20 = 1101004800(0x41a00000, float:20.0)
            r21 = 1099956224(0x41900000, float:18.0)
            java.lang.String r9 = ""
            r4 = 2
            if (r2 == 0) goto L_0x02ef
            int r0 = r2.type
            if (r0 != r4) goto L_0x01e4
            r1.drawNameLock = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x01a9
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x016d
            goto L_0x01a9
        L_0x016d:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0190
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r0
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x0214
        L_0x0190:
            int r0 = r36.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0214
        L_0x01a9:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01cb
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockLeft = r0
            r0 = 1118044160(0x42a40000, float:82.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x0214
        L_0x01cb:
            int r0 = r36.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r0
            goto L_0x0214
        L_0x01e4:
            boolean r0 = r2.verified
            r1.drawVerified = r0
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0203
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01f1
            goto L_0x0203
        L_0x01f1:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01fc
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r0
            goto L_0x0214
        L_0x01fc:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0214
        L_0x0203:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x020e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLeft = r0
            goto L_0x0214
        L_0x020e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r0
        L_0x0214:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r2 = r0.type
            if (r2 != r6) goto L_0x02a2
            r0 = 2131626090(0x7f0e086a, float:1.8879406E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            boolean r11 = r2.isMedia
            if (r11 == 0) goto L_0x0252
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
            goto L_0x028e
        L_0x0252:
            java.lang.String r2 = r2.message
            int r11 = r2.length()
            r14 = 150(0x96, float:2.1E-43)
            if (r11 <= r14) goto L_0x0260
            java.lang.String r2 = r2.substring(r7, r14)
        L_0x0260:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0280
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x0269
            goto L_0x0280
        L_0x0269:
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r14 = 10
            r15 = 32
            java.lang.String r2 = r2.replace(r14, r15)
            r11[r7] = r2
            r11[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r11)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x028e
        L_0x0280:
            java.lang.Object[] r11 = new java.lang.Object[r4]
            r11[r7] = r2
            r11[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r11)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x028e:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r13 = r1.paintIndex
            r11 = r11[r13]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r11, r13, r7)
            r11 = 0
            goto L_0x02b0
        L_0x02a2:
            java.lang.String r2 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x02ae
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r10 = r0[r10]
        L_0x02ae:
            r0 = 0
            r11 = 1
        L_0x02b0:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            org.telegram.ui.Cells.DialogCell$CustomDialog r14 = r1.customDialog
            int r14 = r14.unread_count
            if (r14 == 0) goto L_0x02d0
            r1.drawCount = r6
            java.lang.Object[] r15 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r15[r7] = r14
            java.lang.String r14 = "%d"
            java.lang.String r14 = java.lang.String.format(r14, r15)
            goto L_0x02d3
        L_0x02d0:
            r1.drawCount = r7
            r14 = 0
        L_0x02d3:
            org.telegram.ui.Cells.DialogCell$CustomDialog r15 = r1.customDialog
            boolean r4 = r15.sent
            if (r4 == 0) goto L_0x02de
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            goto L_0x02e2
        L_0x02de:
            r1.drawCheck1 = r7
            r1.drawCheck2 = r7
        L_0x02e2:
            r1.drawClock = r7
            r1.drawError = r7
            java.lang.String r4 = r15.name
            r3 = r2
            r15 = r14
            r12 = -1
            r14 = 0
        L_0x02ec:
            r2 = r0
            goto L_0x1115
        L_0x02ef:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x030a
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02f8
            goto L_0x030a
        L_0x02f8:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0303
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r2
            goto L_0x031b
        L_0x0303:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r2
            goto L_0x031b
        L_0x030a:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0315
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLeft = r2
            goto L_0x031b
        L_0x0315:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r2
        L_0x031b:
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 == 0) goto L_0x03a8
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x041d
            r1.drawNameLock = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x036b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x032e
            goto L_0x036b
        L_0x032e:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0351
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r2
            r2 = 1117782016(0x42a00000, float:80.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 + r4
            r1.nameLeft = r2
            goto L_0x041d
        L_0x0351:
            int r2 = r36.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 - r4
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r2
            goto L_0x041d
        L_0x036b:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x038e
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockLeft = r2
            r2 = 1118044160(0x42a40000, float:82.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 + r4
            r1.nameLeft = r2
            goto L_0x041d
        L_0x038e:
            int r2 = r36.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r2 = r2 - r4
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r2
            goto L_0x041d
        L_0x03a8:
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x041d
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x03ce
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x03bc
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x041d
        L_0x03bc:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x03c9
            r4 = 2
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x041d
        L_0x03c9:
            boolean r2 = r2.verified
            r1.drawVerified = r2
            goto L_0x041d
        L_0x03ce:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x041d
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x03de
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x03ef
        L_0x03de:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x03eb
            r4 = 2
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x03ef
        L_0x03eb:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x03ef:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            boolean r2 = r2.isPremiumUser(r4)
            if (r2 == 0) goto L_0x0417
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            long r3 = r2.clientUserId
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            r24 = r13
            long r12 = r2.id
            int r2 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x0419
            r2 = 0
            int r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0419
            r2 = 1
            goto L_0x041a
        L_0x0417:
            r24 = r13
        L_0x0419:
            r2 = 0
        L_0x041a:
            r1.drawPremium = r2
            goto L_0x041f
        L_0x041d:
            r24 = r13
        L_0x041f:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x042b
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x042b
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            int r2 = r2.date
        L_0x042b:
            boolean r3 = r1.isDialogCell
            if (r3 == 0) goto L_0x0484
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            long r12 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r3.getDraft(r12, r7)
            r1.draftMessage = r3
            if (r3 == 0) goto L_0x045a
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0450
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.reply_to_msg_id
            if (r3 == 0) goto L_0x044e
            goto L_0x0450
        L_0x044e:
            r2 = 0
            goto L_0x0481
        L_0x0450:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.date
            if (r2 <= r3) goto L_0x045a
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x044e
        L_0x045a:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0474
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x0474
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x0474
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x044e
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x044e
        L_0x0474:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x0487
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x044e
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x0487
            goto L_0x044e
        L_0x0481:
            r1.draftMessage = r2
            goto L_0x0487
        L_0x0484:
            r2 = 0
            r1.draftMessage = r2
        L_0x0487:
            if (r0 == 0) goto L_0x050e
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r3, r7)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x04af
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x04b0
        L_0x04af:
            r2 = 0
        L_0x04b0:
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
            if (r4 != r10) goto L_0x04d3
            java.lang.String r4 = r0.toString()
            java.lang.String r10 = "**oo**"
            int r12 = r4.indexOf(r10)
            goto L_0x04d4
        L_0x04d3:
            r12 = -1
        L_0x04d4:
            if (r12 < 0) goto L_0x04ef
            android.text.SpannableStringBuilder r0 = r3.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r4 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r4 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r4)
            int r4 = r4.getIntrinsicWidth()
            r2.<init>(r4)
            int r4 = r12 + 6
            r0.setSpan(r2, r12, r4, r7)
            goto L_0x0501
        L_0x04ef:
            java.lang.String r4 = " "
            android.text.SpannableStringBuilder r4 = r3.append(r4)
            android.text.SpannableStringBuilder r0 = r4.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r4.<init>(r2)
            r0.setSpan(r4, r7, r6, r7)
        L_0x0501:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r0 = 0
            r2 = 2
            r4 = 0
            r6 = 0
            r7 = 1
            goto L_0x0ee2
        L_0x050e:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            r2 = 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x05bc
            r0 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r3 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0550
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x054a
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0533
            goto L_0x054a
        L_0x0533:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r12 = "chats_draft"
            r2.<init>(r12, r4)
            int r4 = r0.length()
            r12 = 33
            r3.setSpan(r2, r7, r4, r12)
            goto L_0x054b
        L_0x054a:
            r3 = r9
        L_0x054b:
            r2 = 2
            r4 = 0
            r6 = 0
            goto L_0x05d4
        L_0x0550:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r12 = 150(0x96, float:2.1E-43)
            if (r4 <= r12) goto L_0x0560
            java.lang.String r3 = r3.substring(r7, r12)
        L_0x0560:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r3)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC$DraftMessage) r3, (android.text.Spannable) r4, (int) r2)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            if (r2 == 0) goto L_0x057d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            if (r2 == 0) goto L_0x057d
            if (r10 != 0) goto L_0x0576
            r3 = 0
            goto L_0x057a
        L_0x0576:
            android.graphics.Paint$FontMetricsInt r3 = r10.getFontMetricsInt()
        L_0x057a:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r2, r4, r3)
        L_0x057d:
            r2 = 2
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r2]
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            r3[r7] = r2
            r3[r6] = r0
            r13 = r24
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r3)
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x05a9
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x05a9
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r12 = "chats_draft"
            r3.<init>(r12, r4)
            int r4 = r0.length()
            int r4 = r4 + r6
            r12 = 33
            r2.setSpan(r3, r7, r4, r12)
        L_0x05a9:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r3 = r3[r4]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r4, r7)
            goto L_0x054b
        L_0x05bc:
            r13 = r24
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x05d8
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r0 = 2131626205(0x7f0e08dd, float:1.887964E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x05d1:
            r0 = 0
            r2 = 2
            r4 = 0
        L_0x05d4:
            r7 = 1
        L_0x05d5:
            r12 = -1
            goto L_0x0ee2
        L_0x05d8:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x066b
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x064c
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x05f4
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d1
        L_0x05f4:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x060c
            r0 = 2131624651(0x7f0e02cb, float:1.8876488E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x05d1
        L_0x060c:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x061a
            r0 = 2131625691(0x7f0e06db, float:1.8878597E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d1
        L_0x061a:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x0668
            long r2 = r0.admin_id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r12 = r0.getClientUserId()
            int r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x0642
            r0 = 2131625679(0x7f0e06cf, float:1.8878573E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x05d1
        L_0x0642:
            r0 = 2131625678(0x7f0e06ce, float:1.887857E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x05d1
        L_0x064c:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x0668
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x0668
            r0 = 2131628154(0x7f0e107a, float:1.8883593E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r4 = 0
            r11 = 0
            goto L_0x05d5
        L_0x0668:
            r3 = r9
            goto L_0x05d1
        L_0x066b:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r3 = r1.message
            long r3 = r3.getFromChatId()
            boolean r12 = org.telegram.messenger.DialogObject.isUserDialog(r3)
            if (r12 == 0) goto L_0x068f
            int r12 = r1.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r12.getUser(r3)
            r4 = 0
            goto L_0x06a0
        L_0x068f:
            int r12 = r1.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r12.getChat(r3)
            r4 = r3
            r3 = 0
        L_0x06a0:
            r1.drawCount2 = r6
            int r12 = r1.dialogsType
            r5 = 2
            if (r12 != r5) goto L_0x0729
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0725
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x06e6
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x06e6
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x06c2
            java.lang.String r0 = "Subscribers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0726
        L_0x06c2:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x06d8
            r0 = 2131624965(0x7f0e0405, float:1.8877125E38)
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0726
        L_0x06d8:
            r0 = 2131624968(0x7f0e0408, float:1.887713E38)
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0726
        L_0x06e6:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x06f3
            java.lang.String r0 = "Members"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0726
        L_0x06f3:
            boolean r2 = r0.has_geo
            if (r2 == 0) goto L_0x0701
            r0 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            java.lang.String r2 = "MegaLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0726
        L_0x0701:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0717
            r0 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r2 = "MegaPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0726
        L_0x0717:
            r0 = 2131626649(0x7f0e0a99, float:1.888054E38)
            java.lang.String r2 = "MegaPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0726
        L_0x0725:
            r0 = r9
        L_0x0726:
            r1.drawCount2 = r7
            goto L_0x073d
        L_0x0729:
            r5 = 3
            if (r12 != r5) goto L_0x0746
            org.telegram.tgnet.TLRPC$User r5 = r1.user
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r5)
            if (r5 == 0) goto L_0x0746
            r0 = 2131628154(0x7f0e107a, float:1.8883593E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x073d:
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 0
            r6 = 0
            r7 = 1
            r11 = 0
            goto L_0x0ed4
        L_0x0746:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x075c
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 != 0) goto L_0x075c
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x075c
            java.lang.CharSequence r0 = r36.formatArchivedDialogNames()
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 0
            goto L_0x0ed4
        L_0x075c:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x0785
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 == 0) goto L_0x0778
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0778
            r15 = r9
            r11 = 0
        L_0x0778:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r3 = r15
            r0 = 0
            r2 = 2
            r4 = 0
        L_0x0782:
            r7 = 1
            goto L_0x0ed4
        L_0x0785:
            boolean r5 = android.text.TextUtils.isEmpty(r0)
            if (r5 == 0) goto L_0x0884
            int r5 = r1.currentDialogFolderId
            if (r5 != 0) goto L_0x0884
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r1.encryptedChat
            if (r5 != 0) goto L_0x0884
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.needDrawBluredPreview()
            if (r5 != 0) goto L_0x0884
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isPhoto()
            if (r5 != 0) goto L_0x07b3
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isNewGif()
            if (r5 != 0) goto L_0x07b3
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVideo()
            if (r5 == 0) goto L_0x0884
        L_0x07b3:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isWebpage()
            if (r5 == 0) goto L_0x07c6
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            java.lang.String r5 = r5.type
            goto L_0x07c7
        L_0x07c6:
            r5 = 0
        L_0x07c7:
            java.lang.String r12 = "app"
            boolean r12 = r12.equals(r5)
            if (r12 != 0) goto L_0x0884
            java.lang.String r12 = "profile"
            boolean r12 = r12.equals(r5)
            if (r12 != 0) goto L_0x0884
            java.lang.String r12 = "article"
            boolean r12 = r12.equals(r5)
            if (r12 != 0) goto L_0x0884
            if (r5 == 0) goto L_0x07e9
            java.lang.String r12 = "telegram_"
            boolean r5 = r5.startsWith(r12)
            if (r5 != 0) goto L_0x0884
        L_0x07e9:
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.photoThumbs
            r12 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r12)
            org.telegram.messenger.MessageObject r12 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r12.photoThumbs
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r2)
            if (r5 != r2) goto L_0x0802
            r2 = 0
        L_0x0802:
            if (r5 == 0) goto L_0x0884
            r1.hasMessageThumb = r6
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            r1.drawPlay = r12
            java.lang.String r12 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.mediaExists
            if (r7 != 0) goto L_0x0851
            int r7 = r1.currentAccount
            org.telegram.messenger.DownloadController r7 = org.telegram.messenger.DownloadController.getInstance(r7)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r7.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r6 != 0) goto L_0x0851
            int r6 = r1.currentAccount
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)
            boolean r6 = r6.isLoadingFile(r12)
            if (r6 == 0) goto L_0x0833
            goto L_0x0851
        L_0x0833:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage
            r26 = 0
            r27 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r30 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            r32 = 0
            java.lang.String r29 = "20_20"
            r25 = r2
            r31 = r5
            r25.setImage((org.telegram.messenger.ImageLocation) r26, (java.lang.String) r27, (org.telegram.messenger.ImageLocation) r28, (java.lang.String) r29, (android.graphics.drawable.Drawable) r30, (java.lang.Object) r31, (int) r32)
            goto L_0x0882
        L_0x0851:
            org.telegram.messenger.MessageObject r6 = r1.message
            int r7 = r6.type
            r12 = 1
            if (r7 != r12) goto L_0x085d
            if (r2 == 0) goto L_0x085d
            int r7 = r2.size
            goto L_0x085e
        L_0x085d:
            r7 = 0
        L_0x085e:
            org.telegram.messenger.ImageReceiver r12 = r1.thumbImage
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r26 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForObject(r5, r2)
            long r5 = (long) r7
            r32 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r34 = 0
            java.lang.String r27 = "20_20"
            java.lang.String r29 = "20_20"
            r25 = r12
            r30 = r5
            r33 = r2
            r25.setImage(r26, r27, r28, r29, r30, r32, r33, r34)
        L_0x0882:
            r2 = 0
            goto L_0x0885
        L_0x0884:
            r2 = 1
        L_0x0885:
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            if (r5 == 0) goto L_0x0CLASSNAME
            long r6 = r5.id
            r25 = 0
            int r27 = (r6 > r25 ? 1 : (r6 == r25 ? 0 : -1))
            if (r27 <= 0) goto L_0x0CLASSNAME
            if (r4 != 0) goto L_0x0CLASSNAME
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r4 == 0) goto L_0x08a1
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isMegagroup(r4)
            if (r4 == 0) goto L_0x0CLASSNAME
        L_0x08a1:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isOutOwner()
            if (r4 == 0) goto L_0x08b3
            r3 = 2131626090(0x7f0e086a, float:1.8879406E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x08fa
        L_0x08b3:
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x08c3
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from
            if (r4 == 0) goto L_0x08c3
            java.lang.String r4 = r4.from_name
            if (r4 == 0) goto L_0x08c3
            r3 = r4
            goto L_0x08fa
        L_0x08c3:
            if (r3 == 0) goto L_0x08f8
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x08d9
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x08ce
            goto L_0x08d9
        L_0x08ce:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x08fa
        L_0x08d9:
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r4 == 0) goto L_0x08e9
            r3 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x08fa
        L_0x08e9:
            java.lang.String r4 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r4, r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x08fa
        L_0x08f8:
            java.lang.String r3 = "DELETED"
        L_0x08fa:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x0914
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r4 = 0
            r2[r4] = r0
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0911:
            r2 = r0
            goto L_0x0b93
        L_0x0914:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x09ff
            java.lang.String r0 = r4.toString()
            if (r2 != 0) goto L_0x0922
            r2 = r9
            goto L_0x0950
        L_0x0922:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x092d
            java.lang.String r2 = "📹 "
            goto L_0x0950
        L_0x092d:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0938
            java.lang.String r2 = "🎤 "
            goto L_0x0950
        L_0x0938:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0943
            java.lang.String r2 = "🎧 "
            goto L_0x0950
        L_0x0943:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x094e
            java.lang.String r2 = "🖼 "
            goto L_0x0950
        L_0x094e:
            java.lang.String r2 = "📎 "
        L_0x0950:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x09b1
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x09b1
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r4 = r36.getMeasuredWidth()
            r5 = 1122893824(0x42ee0000, float:119.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            if (r14 == 0) goto L_0x098f
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x0986
            float r4 = (float) r4
            java.lang.String r5 = r3.toString()
            float r5 = r10.measureText(r5)
            float r4 = r4 - r5
            int r4 = (int) r4
        L_0x0986:
            float r4 = (float) r4
            java.lang.String r5 = ": "
            float r5 = r10.measureText(r5)
            float r4 = r4 - r5
            int r4 = (int) r4
        L_0x098f:
            if (r4 <= 0) goto L_0x09a6
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r5, r4, r10, r6)
            java.lang.String r0 = r0.toString()
        L_0x09a6:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r2)
            android.text.SpannableStringBuilder r0 = r4.append(r0)
            goto L_0x0911
        L_0x09b1:
            int r4 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x09be
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r5)
        L_0x09be:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r0)
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            r6 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r5, r0, r4, r6)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x09e3
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            if (r0 == 0) goto L_0x09e3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            if (r10 != 0) goto L_0x09dc
            r5 = 0
            goto L_0x09e0
        L_0x09dc:
            android.graphics.Paint$FontMetricsInt r5 = r10.getFontMetricsInt()
        L_0x09e0:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r0, r4, r5)
        L_0x09e3:
            r5 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r5]
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            android.text.SpannableStringBuilder r2 = r5.append(r2)
            r4 = 0
            r0[r4] = r2
            r2 = 1
            r0[r2] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r0)
            goto L_0x0911
        L_0x09ff:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0af6
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0af6
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0a45
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0a34
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r5 = 0
            r0[r5] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0a77
        L_0x0a34:
            r4 = 1
            r5 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r5] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0a77
        L_0x0a45:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0a71
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0a60
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r5 = 0
            r0[r5] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0a77
        L_0x0a60:
            r4 = 1
            r5 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r5] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0a77
        L_0x0a71:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0a79
            java.lang.String r0 = r2.title
        L_0x0a77:
            r6 = 1
            goto L_0x0abe
        L_0x0a79:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0ab9
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r2 < r4) goto L_0x0a9f
            r2 = 2
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r5 = 0
            r4[r5] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r6 = 1
            r4[r6] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0abe
        L_0x0a9f:
            r2 = 2
            r5 = 0
            r6 = 1
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r4[r5] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r6] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0abe
        L_0x0ab9:
            r6 = 1
            java.lang.String r0 = r15.toString()
        L_0x0abe:
            r2 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r2, r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0af0 }
            java.lang.String r4 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0af0 }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0af0 }
            if (r14 == 0) goto L_0x0ae4
            int r4 = r3.length()     // Catch:{ Exception -> 0x0af0 }
            r5 = 2
            int r4 = r4 + r5
            goto L_0x0ae5
        L_0x0ae4:
            r4 = 0
        L_0x0ae5:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0af0 }
            r6 = 33
            r2.setSpan(r0, r4, r5, r6)     // Catch:{ Exception -> 0x0af0 }
            goto L_0x0b93
        L_0x0af0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0b93
        L_0x0af6:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0b8d
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b4c
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b0b
            r2 = r0
        L_0x0b0b:
            int r0 = r36.getMeasuredWidth()
            r4 = 1121058816(0x42d20000, float:105.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            if (r14 == 0) goto L_0x0b32
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0b29
            float r0 = (float) r0
            java.lang.String r4 = r3.toString()
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0b29:
            float r0 = (float) r0
            java.lang.String r4 = ": "
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0b32:
            if (r0 <= 0) goto L_0x0b4a
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r0, r10, r6)
            java.lang.String r2 = r0.toString()
            goto L_0x0b5d
        L_0x0b4a:
            r5 = 0
            goto L_0x0b5d
        L_0x0b4c:
            r5 = 0
            int r0 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r0 <= r4) goto L_0x0b59
            java.lang.CharSequence r2 = r2.subSequence(r5, r4)
        L_0x0b59:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r2)
        L_0x0b5d:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r2, (android.text.Spannable) r0, (int) r4)
            org.telegram.messenger.MessageObject r2 = r1.message
            if (r2 == 0) goto L_0x0b7e
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            if (r2 == 0) goto L_0x0b7e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            if (r10 != 0) goto L_0x0b77
            r4 = 0
            goto L_0x0b7b
        L_0x0b77:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0b7b:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r2, r0, r4)
        L_0x0b7e:
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r2 = 1
            r4[r2] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            goto L_0x0911
        L_0x0b8d:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r9)
            goto L_0x0911
        L_0x0b93:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0b9b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0ba5
        L_0x0b9b:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0bc5
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0bc5
        L_0x0ba5:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bbe }
            java.lang.String r4 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0bbe }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0bbe }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0bbe }
            r5 = 1
            int r4 = r4 + r5
            r5 = 33
            r6 = 0
            r2.setSpan(r0, r6, r4, r5)     // Catch:{ Exception -> 0x0bbc }
            r0 = r4
            goto L_0x0bc7
        L_0x0bbc:
            r0 = move-exception
            goto L_0x0bc0
        L_0x0bbe:
            r0 = move-exception
            r4 = 0
        L_0x0bc0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0bc7
        L_0x0bc5:
            r0 = 0
            r4 = 0
        L_0x0bc7:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r5 = r5[r6]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r7 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r6, r7)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0bef
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            if (r5 == 0) goto L_0x0bef
            r2 = r5
        L_0x0bef:
            boolean r5 = r1.hasMessageThumb
            if (r5 == 0) goto L_0x0c3f
            boolean r5 = r2 instanceof android.text.SpannableStringBuilder
            if (r5 != 0) goto L_0x0bfd
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            r2 = r5
        L_0x0bfd:
            r5 = r2
            android.text.SpannableStringBuilder r5 = (android.text.SpannableStringBuilder) r5
            int r6 = r5.length()
            if (r4 < r6) goto L_0x0CLASSNAME
            java.lang.String r4 = " "
            r5.append(r4)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r6 = r8 + 6
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.<init>(r6)
            int r6 = r5.length()
            r7 = 1
            int r6 = r6 - r7
            int r7 = r5.length()
            r12 = 33
            r5.setSpan(r4, r6, r7, r12)
            goto L_0x0c3f
        L_0x0CLASSNAME:
            java.lang.String r6 = " "
            r5.insert(r4, r6)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r6 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r7 = r8 + 6
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.<init>(r7)
            int r7 = r4 + 1
            r12 = 33
            r5.setSpan(r6, r4, r7, r12)
        L_0x0c3f:
            r4 = r0
            r0 = r3
            r6 = 1
            r7 = 0
            r3 = r2
            r2 = 2
            goto L_0x0ed4
        L_0x0CLASSNAME:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0CLASSNAME
        L_0x0c4d:
            r2 = 2
            goto L_0x0e2a
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0c6e
            org.telegram.tgnet.TLRPC$Photo r4 = r3.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0c6e
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0c6e
            r0 = 2131624520(0x7f0e0248, float:1.8876222E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0c4d
        L_0x0c6e:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0CLASSNAME
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0CLASSNAME
            r0 = 2131624526(0x7f0e024e, float:1.8876234E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0c4d
        L_0x0CLASSNAME:
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0d4f
            if (r2 != 0) goto L_0x0c8e
            r0 = r9
            goto L_0x0cba
        L_0x0c8e:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "📹 "
            goto L_0x0cba
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0ca2
            java.lang.String r0 = "🎤 "
            goto L_0x0cba
        L_0x0ca2:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0cad
            java.lang.String r0 = "🎧 "
            goto L_0x0cba
        L_0x0cad:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0cb8
            java.lang.String r0 = "🖼 "
            goto L_0x0cba
        L_0x0cb8:
            java.lang.String r0 = "📎 "
        L_0x0cba:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0d19
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d19
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r36.getMeasuredWidth()
            r4 = 1122893824(0x42ee0000, float:119.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r14 == 0) goto L_0x0cf1
            r4 = 0
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x0cf0
            float r3 = (float) r3
            java.lang.String r5 = ": "
            float r5 = r10.measureText(r5)
            float r3 = r3 - r5
            int r3 = (int) r3
            goto L_0x0cf1
        L_0x0cf0:
            throw r4
        L_0x0cf1:
            if (r3 <= 0) goto L_0x0d08
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r5 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r3, r10, r5)
            java.lang.String r2 = r2.toString()
        L_0x0d08:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0c4d
        L_0x0d19:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.caption
            r2.<init>(r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0d44
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            if (r4 == 0) goto L_0x0d44
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.entities
            java.lang.CharSequence r3 = r3.caption
            r5 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r4, r3, r2, r5)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            if (r10 != 0) goto L_0x0d3d
            r4 = 0
            goto L_0x0d41
        L_0x0d3d:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0d41:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r3, r2, r4)
        L_0x0d44:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            android.text.SpannableStringBuilder r0 = r3.append(r2)
            goto L_0x0c4d
        L_0x0d4f:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0d6d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0d6a:
            r2 = 2
            goto L_0x0e16
        L_0x0d6d:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0d8d
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
            goto L_0x0d6a
        L_0x0d8d:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0d94
            java.lang.String r0 = r3.title
            goto L_0x0d6a
        L_0x0d94:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0db4
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r4 = 0
            r3[r4] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0e16
        L_0x0db4:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0dec
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0dec
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r36.getMeasuredWidth()
            r4 = 1119748096(0x42be0000, float:95.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r5 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r4, r3, r10, r5)
            java.lang.String r0 = r0.toString()
            goto L_0x0e0d
        L_0x0dec:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r3, (android.text.Spannable) r0, (int) r4)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0e0d
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            if (r3 == 0) goto L_0x0e0d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            if (r10 != 0) goto L_0x0e06
            r4 = 0
            goto L_0x0e0a
        L_0x0e06:
            android.graphics.Paint$FontMetricsInt r4 = r10.getFontMetricsInt()
        L_0x0e0a:
            org.telegram.messenger.MediaDataController.addAnimatedEmojiSpans(r3, r0, r4)
        L_0x0e0d:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
        L_0x0e16:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            if (r4 == 0) goto L_0x0e2a
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0e2a
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r10 = r3[r4]
        L_0x0e2a:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0ece
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0e6a
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0e6a
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r36.getMeasuredWidth()
            int r4 = r8 + 95
            int r4 = r4 + 6
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r4, r3, r10, r6)
            java.lang.String r0 = r0.toString()
            goto L_0x0e7b
        L_0x0e6a:
            r5 = 0
            int r3 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0e77
            java.lang.CharSequence r0 = r0.subSequence(r5, r4)
        L_0x0e77:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e7b:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0e85
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0e85:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r4 = " "
            r5 = 0
            r3.insert(r5, r4)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r6 = r8 + 6
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.<init>(r6)
            r6 = 33
            r7 = 1
            r3.setSpan(r4, r5, r7, r6)
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r4 = r4[r6]
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r7, r5)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0ec8
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x0ec8
            goto L_0x0ec9
        L_0x0ec8:
            r3 = r0
        L_0x0ec9:
            r0 = 0
            r4 = 0
            r6 = 1
            r7 = 0
            goto L_0x0ed4
        L_0x0ece:
            r3 = r0
            r0 = 0
            r4 = 0
            r6 = 1
            goto L_0x0782
        L_0x0ed4:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x0edc
            java.lang.CharSequence r0 = r36.formatArchivedDialogNames()
        L_0x0edc:
            r12 = -1
            r35 = r7
            r7 = r6
            r6 = r35
        L_0x0ee2:
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x0eee
            int r5 = r5.date
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0eee:
            int r5 = r1.lastMessageDate
            if (r5 == 0) goto L_0x0ef8
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0ef8:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.date
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            r5 = r9
        L_0x0var_:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x0f1f
            r14 = 0
            r1.drawCheck1 = r14
            r1.drawCheck2 = r14
            r1.drawClock = r14
            r1.drawCount = r14
            r1.drawMention = r14
            r1.drawReactionMention = r14
            r1.drawError = r14
            r2 = 0
            r11 = 0
            r14 = 0
            goto L_0x1034
        L_0x0f1f:
            r14 = 0
            int r15 = r1.currentDialogFolderId
            if (r15 == 0) goto L_0x0var_
            int r13 = r1.unreadCount
            int r15 = r1.mentionCount
            int r16 = r13 + r15
            if (r16 <= 0) goto L_0x0f5b
            if (r13 <= r15) goto L_0x0var_
            r2 = 1
            r1.drawCount = r2
            r1.drawMention = r14
            java.lang.Object[] r14 = new java.lang.Object[r2]
            int r13 = r13 + r15
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15 = 0
            r14[r15] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r14)
            r2 = 0
            goto L_0x0var_
        L_0x0var_:
            r2 = 1
            r1.drawCount = r14
            r1.drawMention = r2
            java.lang.Object[] r14 = new java.lang.Object[r2]
            int r13 = r13 + r15
            java.lang.Integer r2 = java.lang.Integer.valueOf(r13)
            r15 = 0
            r14[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            goto L_0x0var_
        L_0x0f5b:
            r15 = 0
            r1.drawCount = r15
            r1.drawMention = r15
            r2 = 0
        L_0x0var_:
            r13 = 0
        L_0x0var_:
            r1.drawReactionMention = r15
            r14 = r2
            r2 = r13
            goto L_0x0fbc
        L_0x0var_:
            r15 = 0
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0var_
            r1.drawCount = r15
            r2 = 0
            r11 = 0
            r13 = 1
            r15 = 0
            goto L_0x0fa6
        L_0x0var_:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0var_
            r14 = 1
            if (r2 != r14) goto L_0x0var_
            int r14 = r1.mentionCount
            if (r2 != r14) goto L_0x0var_
            if (r13 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x0var_
        L_0x0var_:
            r13 = 1
            r1.drawCount = r13
            java.lang.Object[] r14 = new java.lang.Object[r13]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15 = 0
            r14[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            goto L_0x0fa6
        L_0x0var_:
            r13 = 1
            r15 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0fa3
            r1.drawCount = r13
            r2 = r9
            goto L_0x0fa6
        L_0x0fa3:
            r1.drawCount = r15
            r2 = 0
        L_0x0fa6:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0faf
            r1.drawMention = r13
            java.lang.String r14 = "@"
            goto L_0x0fb2
        L_0x0faf:
            r1.drawMention = r15
            r14 = 0
        L_0x0fb2:
            int r15 = r1.reactionMentionCount
            if (r15 <= 0) goto L_0x0fb9
            r1.drawReactionMention = r13
            goto L_0x0fbc
        L_0x0fb9:
            r13 = 0
            r1.drawReactionMention = r13
        L_0x0fbc:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isOut()
            if (r13 == 0) goto L_0x102b
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 != 0) goto L_0x102b
            if (r11 == 0) goto L_0x102b
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r13 != 0) goto L_0x102b
            boolean r11 = r11.isSending()
            if (r11 == 0) goto L_0x0fe5
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r13 = 1
            r1.drawClock = r13
            r1.drawError = r11
            goto L_0x1034
        L_0x0fe5:
            r11 = 0
            r13 = 1
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isSendError()
            if (r15 == 0) goto L_0x0ffc
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r13
            r1.drawCount = r11
            r1.drawMention = r11
            goto L_0x1034
        L_0x0ffc:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isSent()
            if (r11 == 0) goto L_0x1029
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isUnread()
            if (r11 == 0) goto L_0x101d
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x101b
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x101b
            goto L_0x101d
        L_0x101b:
            r11 = 0
            goto L_0x101e
        L_0x101d:
            r11 = 1
        L_0x101e:
            r1.drawCheck1 = r11
            r11 = 1
            r1.drawCheck2 = r11
            r11 = 0
            r1.drawClock = r11
            r1.drawError = r11
            goto L_0x1034
        L_0x1029:
            r11 = 0
            goto L_0x1034
        L_0x102b:
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r11
        L_0x1034:
            r1.promoDialog = r11
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r13 = r1.dialogsType
            r15 = r2
            if (r13 != 0) goto L_0x10a0
            r13 = r3
            long r2 = r1.currentDialogId
            r16 = r4
            r4 = 1
            boolean r2 = r11.isPromoDialog(r2, r4)
            if (r2 == 0) goto L_0x10a3
            r1.drawPinBackground = r4
            r1.promoDialog = r4
            int r2 = r11.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x1066
            r2 = 2131628880(0x7f0e1350, float:1.8885065E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x1060:
            r35 = r13
            r13 = r2
            r2 = r35
            goto L_0x10a5
        L_0x1066:
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x10a3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r11.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((java.lang.String) r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x1090
            r2 = 2131627837(0x7f0e0f3d, float:1.888295E38)
            java.lang.String r3 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x1090:
            java.lang.String r3 = r11.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1060
            java.lang.String r3 = r11.promoPsaMessage
            r4 = 0
            r1.hasMessageThumb = r4
            r13 = r2
            r2 = r3
            goto L_0x10a5
        L_0x10a0:
            r13 = r3
            r16 = r4
        L_0x10a3:
            r2 = r13
            r13 = r5
        L_0x10a5:
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x10b9
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = "ArchivedChats"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x10b2:
            r3 = r2
            r11 = r6
            r6 = r7
            r7 = r16
            goto L_0x02ec
        L_0x10b9:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x10c1
            java.lang.String r3 = r3.title
        L_0x10bf:
            r4 = r3
            goto L_0x1105
        L_0x10c1:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x1104
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x10d5
            r3 = 2131627995(0x7f0e0fdb, float:1.888327E38)
            java.lang.String r4 = "RepliesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x10bf
        L_0x10d5:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x10fd
            boolean r3 = r1.useMeForMyMessages
            if (r3 == 0) goto L_0x10eb
            r3 = 2131626090(0x7f0e086a, float:1.8879406E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x10bf
        L_0x10eb:
            int r3 = r1.dialogsType
            r4 = 3
            if (r3 != r4) goto L_0x10f3
            r3 = 1
            r1.drawPinBackground = r3
        L_0x10f3:
            r3 = 2131628153(0x7f0e1079, float:1.888359E38)
            java.lang.String r4 = "SavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x10bf
        L_0x10fd:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x10bf
        L_0x1104:
            r4 = r9
        L_0x1105:
            int r3 = r4.length()
            if (r3 != 0) goto L_0x10b2
            r3 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x10b2
        L_0x1115:
            if (r6 == 0) goto L_0x1156
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r13)
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r5
            r26 = r13
            r28 = r0
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.timeLayout = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x114d
            int r5 = r36.getMeasuredWidth()
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x115d
        L_0x114d:
            r6 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.timeLeft = r5
            goto L_0x115d
        L_0x1156:
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r0 = 0
        L_0x115d:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1171
            int r5 = r36.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            goto L_0x1185
        L_0x1171:
            int r5 = r36.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1117388800(0x429a0000, float:77.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x1185:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x1197
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r6 = r6 + r13
            int r5 = r5 - r6
        L_0x1197:
            boolean r6 = r1.drawClock
            r13 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x11cb
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r6 = r6 + r16
            int r5 = r5 - r6
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            if (r16 != 0) goto L_0x11b6
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.clockDrawLeft = r0
            r16 = r5
            goto L_0x11c7
        L_0x11b6:
            r16 = r5
            int r5 = r1.timeLeft
            int r5 = r5 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r5 = r5 + r0
            r1.clockDrawLeft = r5
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x11c7:
            r5 = r16
            goto L_0x1246
        L_0x11cb:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x1246
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r6 = r6 + r16
            int r5 = r5 - r6
            boolean r13 = r1.drawCheck1
            if (r13 == 0) goto L_0x122b
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            r18 = 1090519040(0x41000000, float:8.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r13 = r13 - r18
            int r5 = r5 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x1202
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.halfCheckDrawLeft = r0
            r6 = 1085276160(0x40b00000, float:5.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x1246
        L_0x1202:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r13 = r13 + r0
            r1.checkDrawLeft = r13
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r13 = r13 + r0
            r1.halfCheckDrawLeft = r13
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            int r6 = r6 + r13
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r6 = r6 - r13
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x1246
        L_0x122b:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x1235
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft1 = r0
            goto L_0x1246
        L_0x1235:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r13 = r13 + r0
            r1.checkDrawLeft1 = r13
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x1246:
            boolean r0 = r1.dialogMuted
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x126a
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x126a
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x126a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r13 = r13.getIntrinsicWidth()
            int r0 = r0 + r13
            int r5 = r5 - r0
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c3
            int r13 = r1.nameLeft
            int r13 = r13 + r0
            r1.nameLeft = r13
            goto L_0x12c3
        L_0x126a:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1284
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r13 = r13.getIntrinsicWidth()
            int r0 = r0 + r13
            int r5 = r5 - r0
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c3
            int r13 = r1.nameLeft
            int r13 = r13 + r0
            r1.nameLeft = r13
            goto L_0x12c3
        L_0x1284:
            boolean r0 = r1.drawPremium
            if (r0 == 0) goto L_0x12a2
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.Premium.PremiumGradient r13 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r13 = r13.premiumStarDrawableMini
            int r13 = r13.getIntrinsicWidth()
            int r0 = r0 + r13
            int r5 = r5 - r0
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c3
            int r13 = r1.nameLeft
            int r13 = r13 + r0
            r1.nameLeft = r13
            goto L_0x12c3
        L_0x12a2:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x12c3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r13 = r1.drawScam
            r6 = 1
            if (r13 != r6) goto L_0x12b2
            org.telegram.ui.Components.ScamDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x12b4
        L_0x12b2:
            org.telegram.ui.Components.ScamDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x12b4:
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 + r6
            int r5 = r5 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x12c3
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x12c3:
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x132b }
            int r0 = r5 - r0
            if (r0 >= 0) goto L_0x12ce
            r0 = 0
        L_0x12ce:
            r13 = 10
            r6 = 32
            java.lang.String r4 = r4.replace(r13, r6)     // Catch:{ Exception -> 0x132b }
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x132b }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x132b }
            r6 = r6[r13]     // Catch:{ Exception -> 0x132b }
            float r0 = (float) r0     // Catch:{ Exception -> 0x132b }
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x132b }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r4, r6, r0, r13)     // Catch:{ Exception -> 0x132b }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x132b }
            int r6 = r1.paintIndex     // Catch:{ Exception -> 0x132b }
            r4 = r4[r6]     // Catch:{ Exception -> 0x132b }
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()     // Catch:{ Exception -> 0x132b }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x132b }
            r13 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r4, r6, r13)     // Catch:{ Exception -> 0x132b }
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x132b }
            if (r4 == 0) goto L_0x130f
            boolean r4 = r4.hasHighlightedWords()     // Catch:{ Exception -> 0x132b }
            if (r4 == 0) goto L_0x130f
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x132b }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x132b }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider     // Catch:{ Exception -> 0x132b }
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)     // Catch:{ Exception -> 0x132b }
            if (r4 == 0) goto L_0x130f
            r26 = r4
            goto L_0x1311
        L_0x130f:
            r26 = r0
        L_0x1311:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x132b }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x132b }
            int r6 = r1.paintIndex     // Catch:{ Exception -> 0x132b }
            r27 = r4[r6]     // Catch:{ Exception -> 0x132b }
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x132b }
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r28 = r5
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)     // Catch:{ Exception -> 0x132b }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x132b }
            goto L_0x132f
        L_0x132b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x132f:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x13eb
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1339
            goto L_0x13eb
        L_0x1339:
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
            int r4 = r36.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x139d
            r6 = 1102053376(0x41b00000, float:22.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r36.getMeasuredWidth()
            r13 = 1115684864(0x42800000, float:64.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r6 = r6 - r13
            int r13 = r8 + 11
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r6 - r13
            goto L_0x13b4
        L_0x139d:
            r6 = 1117257728(0x42980000, float:76.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r13 = 1116078080(0x42860000, float:67.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r6
        L_0x13b4:
            r17 = r4
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            float r6 = (float) r6
            r19 = r9
            float r9 = (float) r0
            r22 = 1113063424(0x42580000, float:54.0)
            r23 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r12 = (float) r12
            r24 = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r7 = (float) r7
            r4.setImageCoords(r6, r9, r12, r7)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r13
            r7 = 1106247680(0x41var_, float:30.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r0
            float r7 = (float) r7
            float r9 = (float) r8
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r12 = (float) r12
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r4.setImageCoords(r6, r7, r12, r9)
            r4 = r0
            goto L_0x149d
        L_0x13eb:
            r24 = r7
            r19 = r9
            r23 = r12
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
            int r4 = r36.getMeasuredWidth()
            r6 = 1119485952(0x42ba0000, float:93.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1454
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r36.getMeasuredWidth()
            r7 = 1115947008(0x42840000, float:66.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r6 - r7
            goto L_0x146b
        L_0x1454:
            r6 = 1117519872(0x429CLASSNAME, float:78.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 1116340224(0x428a0000, float:69.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r6
        L_0x146b:
            org.telegram.messenger.ImageReceiver r9 = r1.avatarImage
            float r6 = (float) r6
            float r12 = (float) r0
            r13 = 1113587712(0x42600000, float:56.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r17 = 1113587712(0x42600000, float:56.0)
            r22 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r9.setImageCoords(r6, r12, r13, r4)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r0
            float r7 = (float) r7
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r9 = (float) r9
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r12 = (float) r12
            r4.setImageCoords(r6, r7, r9, r12)
            r4 = r0
            r17 = r22
        L_0x149d:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x14c2
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x14ba
            int r0 = r36.getMeasuredWidth()
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.pinLeft = r0
            goto L_0x14c2
        L_0x14ba:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x14c2:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x14f6
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x14e0
            int r0 = r36.getMeasuredWidth()
            r6 = 1107820544(0x42080000, float:34.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.errorLeft = r0
            goto L_0x14f2
        L_0x14e0:
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.errorLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x14f2:
            r0 = r17
            goto L_0x1694
        L_0x14f6:
            if (r15 != 0) goto L_0x1526
            if (r14 != 0) goto L_0x1526
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x14ff
            goto L_0x1526
        L_0x14ff:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1520
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            int r17 = r17 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1520
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x1520:
            r6 = 0
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x14f2
        L_0x1526:
            if (r15 == 0) goto L_0x1589
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r6 = r6.measureText(r15)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            int r6 = (int) r6
            int r0 = java.lang.Math.max(r0, r6)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r6 = r1.countWidth
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_CENTER
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r26 = r15
            r28 = r6
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 + r6
            int r17 = r17 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x1575
            int r0 = r36.getMeasuredWidth()
            int r6 = r1.countWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r6
            r1.countLeft = r0
            goto L_0x1585
        L_0x1575:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.countLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x1585:
            r6 = 1
            r1.drawCount = r6
            goto L_0x158c
        L_0x1589:
            r6 = 0
            r1.countWidth = r6
        L_0x158c:
            if (r14 == 0) goto L_0x1614
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x15c4
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r6 = r6.measureText(r14)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            int r6 = (int) r6
            int r0 = java.lang.Math.max(r0, r6)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r6 = r1.mentionWidth
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_CENTER
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r26 = r14
            r28 = r6
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.mentionLayout = r0
            goto L_0x15cc
        L_0x15c4:
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.mentionWidth = r0
        L_0x15cc:
            int r0 = r1.mentionWidth
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 + r6
            int r17 = r17 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x15f4
            int r0 = r36.getMeasuredWidth()
            int r6 = r1.mentionWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r6
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x15ef
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            goto L_0x15f0
        L_0x15ef:
            r6 = 0
        L_0x15f0:
            int r0 = r0 - r6
            r1.mentionLeft = r0
            goto L_0x1610
        L_0x15f4:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r7 = r1.countWidth
            if (r7 == 0) goto L_0x1602
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r9
            goto L_0x1603
        L_0x1602:
            r7 = 0
        L_0x1603:
            int r6 = r6 + r7
            r1.mentionLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x1610:
            r6 = 1
            r1.drawMention = r6
            goto L_0x1617
        L_0x1614:
            r6 = 0
            r1.mentionWidth = r6
        L_0x1617:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x14f2
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x165c
            int r0 = r36.getMeasuredWidth()
            r6 = 1107296256(0x42000000, float:32.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.reactionMentionLeft = r0
            boolean r6 = r1.drawMention
            if (r6 == 0) goto L_0x1646
            int r6 = r1.mentionWidth
            if (r6 == 0) goto L_0x1642
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            goto L_0x1643
        L_0x1642:
            r6 = 0
        L_0x1643:
            int r0 = r0 - r6
            r1.reactionMentionLeft = r0
        L_0x1646:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x14f2
            int r0 = r1.reactionMentionLeft
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x1656
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            goto L_0x1657
        L_0x1656:
            r6 = 0
        L_0x1657:
            int r0 = r0 - r6
            r1.reactionMentionLeft = r0
            goto L_0x14f2
        L_0x165c:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.reactionMentionLeft = r6
            boolean r7 = r1.drawMention
            if (r7 == 0) goto L_0x1674
            int r7 = r1.mentionWidth
            if (r7 == 0) goto L_0x1670
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r9
            goto L_0x1671
        L_0x1670:
            r7 = 0
        L_0x1671:
            int r6 = r6 + r7
            r1.reactionMentionLeft = r6
        L_0x1674:
            boolean r6 = r1.drawCount
            if (r6 == 0) goto L_0x1688
            int r6 = r1.reactionMentionLeft
            int r7 = r1.countWidth
            if (r7 == 0) goto L_0x1684
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r9
            goto L_0x1685
        L_0x1684:
            r7 = 0
        L_0x1685:
            int r6 = r6 + r7
            r1.reactionMentionLeft = r6
        L_0x1688:
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
            goto L_0x14f2
        L_0x1694:
            if (r11 == 0) goto L_0x16e0
            if (r3 != 0) goto L_0x169b
            r9 = r19
            goto L_0x169c
        L_0x169b:
            r9 = r3
        L_0x169c:
            int r3 = r9.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r3 <= r6) goto L_0x16a9
            r3 = 0
            java.lang.CharSequence r9 = r9.subSequence(r3, r6)
        L_0x16a9:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x16b1
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x16b3
        L_0x16b1:
            if (r2 == 0) goto L_0x16b8
        L_0x16b3:
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            goto L_0x16bc
        L_0x16b8:
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r9)
        L_0x16bc:
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r6 = r6[r7]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r7 = 1099431936(0x41880000, float:17.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r6, r7, r9)
            org.telegram.messenger.MessageObject r6 = r1.message
            if (r6 == 0) goto L_0x16e0
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r6 == 0) goto L_0x16e0
            r3 = r6
        L_0x16e0:
            r6 = 1094713344(0x41400000, float:12.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = java.lang.Math.max(r7, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x16f2
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x1749
        L_0x16f2:
            if (r2 == 0) goto L_0x1749
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x16fd
            int r7 = r1.currentDialogFolderDialogsCount
            r9 = 1
            if (r7 != r9) goto L_0x1749
        L_0x16fd:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x172f }
            if (r0 == 0) goto L_0x1714
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x172f }
            if (r0 == 0) goto L_0x1714
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x172f }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x172f }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x172f }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)     // Catch:{ Exception -> 0x172f }
            if (r0 == 0) goto L_0x1714
            r2 = r0
        L_0x1714:
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x172f }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x172f }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            android.text.TextUtils$TruncateAt r32 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x172f }
            r34 = 1
            r25 = r2
            r27 = r6
            r33 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r25, r26, r27, r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x172f }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x172f }
            goto L_0x1733
        L_0x172f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1733:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            float r4 = (float) r4
            r0.setImageY(r4)
            goto L_0x1771
        L_0x1749:
            r7 = 0
            r1.messageNameLayout = r7
            if (r0 != 0) goto L_0x175c
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1753
            goto L_0x175c
        L_0x1753:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1771
        L_0x175c:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1101529088(0x41a80000, float:21.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1771:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1868 }
            if (r0 != 0) goto L_0x1779
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1868 }
            if (r4 == 0) goto L_0x178b
        L_0x1779:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1868 }
            if (r4 == 0) goto L_0x178b
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1868 }
            r7 = 1
            if (r4 <= r7) goto L_0x178b
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1868 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x1868 }
            r10 = r0[r3]     // Catch:{ Exception -> 0x1868 }
            r3 = r2
            r2 = 0
            goto L_0x17a2
        L_0x178b:
            if (r0 != 0) goto L_0x1791
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x1793
        L_0x1791:
            if (r2 == 0) goto L_0x17a2
        L_0x1793:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1868 }
            int r0 = r6 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1868 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1868 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r10, r0, r4)     // Catch:{ Exception -> 0x1868 }
        L_0x17a2:
            boolean r0 = r3 instanceof android.text.Spannable     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x17d4
            r0 = r3
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x1868 }
            int r4 = r0.length()     // Catch:{ Exception -> 0x1868 }
            java.lang.Class<android.text.style.CharacterStyle> r7 = android.text.style.CharacterStyle.class
            r9 = 0
            java.lang.Object[] r4 = r0.getSpans(r9, r4, r7)     // Catch:{ Exception -> 0x1868 }
            android.text.style.CharacterStyle[] r4 = (android.text.style.CharacterStyle[]) r4     // Catch:{ Exception -> 0x1868 }
            int r7 = r4.length     // Catch:{ Exception -> 0x1868 }
            r9 = 0
        L_0x17b8:
            if (r9 >= r7) goto L_0x17d4
            r11 = r4[r9]     // Catch:{ Exception -> 0x1868 }
            boolean r12 = r11 instanceof android.text.style.ClickableSpan     // Catch:{ Exception -> 0x1868 }
            if (r12 != 0) goto L_0x17ce
            boolean r12 = r11 instanceof android.text.style.StyleSpan     // Catch:{ Exception -> 0x1868 }
            if (r12 == 0) goto L_0x17d1
            r12 = r11
            android.text.style.StyleSpan r12 = (android.text.style.StyleSpan) r12     // Catch:{ Exception -> 0x1868 }
            int r12 = r12.getStyle()     // Catch:{ Exception -> 0x1868 }
            r13 = 1
            if (r12 != r13) goto L_0x17d1
        L_0x17ce:
            r0.removeSpan(r11)     // Catch:{ Exception -> 0x1868 }
        L_0x17d1:
            int r9 = r9 + 1
            goto L_0x17b8
        L_0x17d4:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1868 }
            if (r0 != 0) goto L_0x180f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x17dd
            goto L_0x180f
        L_0x17dd:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x17f7
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1868 }
            int r0 = r0 + r8
            int r6 = r6 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x17f7
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x1868 }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1868 }
            int r8 = r8 + r4
            int r0 = r0 - r8
            r1.messageLeft = r0     // Catch:{ Exception -> 0x1868 }
        L_0x17f7:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1868 }
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1868 }
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r26 = r3
            r27 = r10
            r28 = r6
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)     // Catch:{ Exception -> 0x1868 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1868 }
            goto L_0x1842
        L_0x180f:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1868 }
            if (r0 == 0) goto L_0x181c
            if (r2 == 0) goto L_0x181c
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1868 }
            int r6 = r6 + r0
        L_0x181c:
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1868 }
            r29 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x1868 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x1868 }
            r31 = 0
            android.text.TextUtils$TruncateAt r32 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1868 }
            if (r2 == 0) goto L_0x1830
            r34 = 1
            goto L_0x1832
        L_0x1830:
            r34 = 2
        L_0x1832:
            r25 = r3
            r26 = r10
            r27 = r6
            r30 = r0
            r33 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r25, r26, r27, r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x1868 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1868 }
        L_0x1842:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x1868 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilers     // Catch:{ Exception -> 0x1868 }
            r0.addAll(r2)     // Catch:{ Exception -> 0x1868 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilers     // Catch:{ Exception -> 0x1868 }
            r0.clear()     // Catch:{ Exception -> 0x1868 }
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1868 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x1868 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r1.spoilers     // Catch:{ Exception -> 0x1868 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r3)     // Catch:{ Exception -> 0x1868 }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r0 = r1.animatedEmojiStack     // Catch:{ Exception -> 0x1868 }
            r2 = 1
            android.text.Layout[] r3 = new android.text.Layout[r2]     // Catch:{ Exception -> 0x1868 }
            android.text.StaticLayout r2 = r1.messageLayout     // Catch:{ Exception -> 0x1868 }
            r4 = 0
            r3[r4] = r2     // Catch:{ Exception -> 0x1868 }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r0 = org.telegram.ui.Components.AnimatedEmojiSpan.update((int) r4, (android.view.View) r1, (org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans) r0, (android.text.Layout[]) r3)     // Catch:{ Exception -> 0x1868 }
            r1.animatedEmojiStack = r0     // Catch:{ Exception -> 0x1868 }
            goto L_0x186f
        L_0x1868:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x186f:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x19da
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1963
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1963
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x18c2
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x18c2
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x18c2
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
            goto L_0x194b
        L_0x18c2:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x18ec
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
            goto L_0x194b
        L_0x18ec:
            boolean r4 = r1.drawPremium
            if (r4 == 0) goto L_0x191a
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            org.telegram.ui.Components.Premium.PremiumGradient r4 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r4 = r4.premiumStarDrawableMini
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
            goto L_0x194b
        L_0x191a:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x194b
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = r1.drawScam
            r9 = 1
            if (r4 != r9) goto L_0x193d
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x193f
        L_0x193d:
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x193f:
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
        L_0x194b:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1963
            double r4 = (double) r5
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x1963
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r4
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x1963:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x19a4
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19a4
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x1974:
            if (r2 >= r0) goto L_0x199a
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x1999
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            double r7 = (double) r6
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r4
            int r4 = (int) r7
            int r3 = java.lang.Math.min(r3, r4)
            int r2 = r2 + 1
            goto L_0x1974
        L_0x1999:
            r3 = 0
        L_0x199a:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x19a4
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x19a4:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1a68
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a68
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1a68
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x1a68
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x1a68
        L_0x19da:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1a2d
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a2d
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r5
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1a0e
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r5
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1a0e
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1a0e:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1a1e
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1a1e
            boolean r2 = r1.drawPremium
            if (r2 != 0) goto L_0x1a1e
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1a2d
        L_0x1a1e:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1a2d:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a50
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a50
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1a3a:
            if (r3 >= r0) goto L_0x1a49
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1a3a
        L_0x1a49:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1a50:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1a68
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a68
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1a68:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1aac
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1aac
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1aa8 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1aa8 }
            r7 = r24
            r2 = 1
            if (r7 < r0) goto L_0x1a7f
            int r7 = r0 + -1
        L_0x1a7f:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1aa8 }
            float r0 = r0.getPrimaryHorizontal(r7)     // Catch:{ Exception -> 0x1aa8 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1aa8 }
            int r7 = r7 + r2
            float r2 = r3.getPrimaryHorizontal(r7)     // Catch:{ Exception -> 0x1aa8 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1aa8 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1aa8 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1aa8 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1aa8 }
            if (r0 == 0) goto L_0x1a9f
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1aa8 }
            int r0 = r0 + r2
        L_0x1a9f:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1aa8 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1aa8 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1aa8 }
            goto L_0x1aac
        L_0x1aa8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1aac:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1afa
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1afa
            if (r23 < 0) goto L_0x1ad1
            int r12 = r23 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r12 >= r0) goto L_0x1ad1
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r23
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r12)
            goto L_0x1adf
        L_0x1ad1:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1adf:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1aeb
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1afa
        L_0x1aeb:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1afa:
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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x01e3  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0572  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r24, boolean r25) {
        /*
            r23 = this;
            r0 = r23
            org.telegram.ui.Cells.DialogCell$CustomDialog r1 = r0.customDialog
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x003e
            int r6 = r1.date
            r0.lastMessageDate = r6
            int r6 = r1.unread_count
            if (r6 == 0) goto L_0x0012
            goto L_0x0013
        L_0x0012:
            r4 = 0
        L_0x0013:
            r0.lastUnreadState = r4
            r0.unreadCount = r6
            boolean r4 = r1.pinned
            r0.drawPin = r4
            boolean r4 = r1.muted
            r0.dialogMuted = r4
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            int r5 = r1.id
            long r5 = (long) r5
            java.lang.String r1 = r1.name
            r4.setInfo(r5, r1, r3)
            org.telegram.messenger.ImageReceiver r7 = r0.avatarImage
            r8 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            r12 = 0
            java.lang.String r9 = "50_50"
            r7.setImage(r8, r9, r10, r11, r12)
            org.telegram.messenger.ImageReceiver r1 = r0.thumbImage
            r1.setImageBitmap((android.graphics.drawable.Drawable) r3)
        L_0x003b:
            r1 = 0
            goto L_0x0561
        L_0x003e:
            int r1 = r0.unreadCount
            int r6 = r0.reactionMentionCount
            if (r6 == 0) goto L_0x0046
            r6 = 1
            goto L_0x0047
        L_0x0046:
            r6 = 0
        L_0x0047:
            boolean r7 = r0.markUnread
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x0115
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            long r9 = r0.currentDialogId
            java.lang.Object r8 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 == 0) goto L_0x0108
            if (r24 != 0) goto L_0x0117
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            long r10 = r8.id
            boolean r9 = r9.isClearingDialog(r10)
            r0.clearingDialog = r9
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r9 = r9.dialogMessage
            long r10 = r8.id
            java.lang.Object r9 = r9.get(r10)
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            r0.message = r9
            if (r9 == 0) goto L_0x008b
            boolean r9 = r9.isUnread()
            if (r9 == 0) goto L_0x008b
            r9 = 1
            goto L_0x008c
        L_0x008b:
            r9 = 0
        L_0x008c:
            r0.lastUnreadState = r9
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r9 == 0) goto L_0x00a3
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r9)
            int r9 = r9.getArchiveUnreadCount()
            r0.unreadCount = r9
            r0.mentionCount = r5
            r0.reactionMentionCount = r5
            goto L_0x00af
        L_0x00a3:
            int r9 = r8.unread_count
            r0.unreadCount = r9
            int r9 = r8.unread_mentions_count
            r0.mentionCount = r9
            int r9 = r8.unread_reactions_count
            r0.reactionMentionCount = r9
        L_0x00af:
            boolean r9 = r8.unread_mark
            r0.markUnread = r9
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.edit_date
            goto L_0x00bd
        L_0x00bc:
            r9 = 0
        L_0x00bd:
            r0.currentEditDate = r9
            int r9 = r8.last_message_date
            r0.lastMessageDate = r9
            int r9 = r0.dialogsType
            r10 = 7
            r11 = 8
            if (r9 == r10) goto L_0x00db
            if (r9 != r11) goto L_0x00cd
            goto L_0x00db
        L_0x00cd:
            int r9 = r0.currentDialogFolderId
            if (r9 != 0) goto L_0x00d7
            boolean r8 = r8.pinned
            if (r8 == 0) goto L_0x00d7
            r8 = 1
            goto L_0x00d8
        L_0x00d7:
            r8 = 0
        L_0x00d8:
            r0.drawPin = r8
            goto L_0x00fd
        L_0x00db:
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.messenger.MessagesController$DialogFilter[] r9 = r9.selectedDialogFilter
            int r10 = r0.dialogsType
            if (r10 != r11) goto L_0x00e9
            r10 = 1
            goto L_0x00ea
        L_0x00e9:
            r10 = 0
        L_0x00ea:
            r9 = r9[r10]
            if (r9 == 0) goto L_0x00fa
            org.telegram.messenger.support.LongSparseIntArray r9 = r9.pinnedDialogs
            long r10 = r8.id
            int r8 = r9.indexOfKey(r10)
            if (r8 < 0) goto L_0x00fa
            r8 = 1
            goto L_0x00fb
        L_0x00fa:
            r8 = 0
        L_0x00fb:
            r0.drawPin = r8
        L_0x00fd:
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x0117
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.send_state
            r0.lastSendState = r8
            goto L_0x0117
        L_0x0108:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.reactionMentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0117
        L_0x0115:
            r0.drawPin = r5
        L_0x0117:
            int r8 = r0.dialogsType
            r9 = 2
            if (r8 != r9) goto L_0x011e
            r0.drawPin = r5
        L_0x011e:
            if (r24 == 0) goto L_0x0264
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 == 0) goto L_0x0141
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r8 = r24 & r8
            if (r8 == 0) goto L_0x0141
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            long r10 = r10.id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            r0.user = r8
            r23.invalidate()
        L_0x0141:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x016b
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_USER_PRINT
            r8 = r24 & r8
            if (r8 == 0) goto L_0x016b
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r10 = r0.currentDialogId
            java.lang.CharSequence r8 = r8.getPrintingString(r10, r5, r4)
            java.lang.CharSequence r10 = r0.lastPrintString
            if (r10 == 0) goto L_0x015d
            if (r8 == 0) goto L_0x0169
        L_0x015d:
            if (r10 != 0) goto L_0x0161
            if (r8 != 0) goto L_0x0169
        L_0x0161:
            if (r10 == 0) goto L_0x016b
            boolean r8 = r10.equals(r8)
            if (r8 != 0) goto L_0x016b
        L_0x0169:
            r8 = 1
            goto L_0x016c
        L_0x016b:
            r8 = 0
        L_0x016c:
            if (r8 != 0) goto L_0x017f
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_MESSAGE_TEXT
            r10 = r24 & r10
            if (r10 == 0) goto L_0x017f
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x017f
            java.lang.CharSequence r10 = r10.messageText
            java.lang.CharSequence r11 = r0.lastMessageString
            if (r10 == r11) goto L_0x017f
            r8 = 1
        L_0x017f:
            if (r8 != 0) goto L_0x01ad
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01ad
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 == 0) goto L_0x01ad
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            long r11 = r11.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
            boolean r11 = r10.call_active
            if (r11 == 0) goto L_0x01a7
            boolean r10 = r10.call_not_empty
            if (r10 == 0) goto L_0x01a7
            r10 = 1
            goto L_0x01a8
        L_0x01a7:
            r10 = 0
        L_0x01a8:
            boolean r11 = r0.hasCall
            if (r10 == r11) goto L_0x01ad
            r8 = 1
        L_0x01ad:
            if (r8 != 0) goto L_0x01ba
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01ba
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 != 0) goto L_0x01ba
            r8 = 1
        L_0x01ba:
            if (r8 != 0) goto L_0x01c7
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01c7
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 != 0) goto L_0x01c7
            r8 = 1
        L_0x01c7:
            if (r8 != 0) goto L_0x01d4
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01d4
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            if (r10 != 0) goto L_0x01d4
            r8 = 1
        L_0x01d4:
            if (r8 != 0) goto L_0x01e1
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01e1
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            if (r10 != 0) goto L_0x01e1
            r8 = 1
        L_0x01e1:
            if (r8 != 0) goto L_0x0247
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x01f8
            boolean r11 = r0.lastUnreadState
            boolean r10 = r10.isUnread()
            if (r11 == r10) goto L_0x01f8
            org.telegram.messenger.MessageObject r8 = r0.message
            boolean r8 = r8.isUnread()
            r0.lastUnreadState = r8
            r8 = 1
        L_0x01f8:
            boolean r10 = r0.isDialogCell
            if (r10 == 0) goto L_0x0247
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r10 = r10.dialogs_dict
            long r11 = r0.currentDialogId
            java.lang.Object r10 = r10.get(r11)
            org.telegram.tgnet.TLRPC$Dialog r10 = (org.telegram.tgnet.TLRPC$Dialog) r10
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r11 == 0) goto L_0x021d
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r11 = org.telegram.messenger.MessagesStorage.getInstance(r11)
            int r11 = r11.getArchiveUnreadCount()
        L_0x021a:
            r12 = 0
            r13 = 0
            goto L_0x0228
        L_0x021d:
            if (r10 == 0) goto L_0x0226
            int r11 = r10.unread_count
            int r12 = r10.unread_mentions_count
            int r13 = r10.unread_reactions_count
            goto L_0x0228
        L_0x0226:
            r11 = 0
            goto L_0x021a
        L_0x0228:
            if (r10 == 0) goto L_0x0247
            int r14 = r0.unreadCount
            if (r14 != r11) goto L_0x023c
            boolean r14 = r0.markUnread
            boolean r15 = r10.unread_mark
            if (r14 != r15) goto L_0x023c
            int r14 = r0.mentionCount
            if (r14 != r12) goto L_0x023c
            int r14 = r0.reactionMentionCount
            if (r14 == r13) goto L_0x0247
        L_0x023c:
            r0.unreadCount = r11
            r0.mentionCount = r12
            boolean r8 = r10.unread_mark
            r0.markUnread = r8
            r0.reactionMentionCount = r13
            r8 = 1
        L_0x0247:
            if (r8 != 0) goto L_0x025e
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_SEND_STATE
            r10 = r24 & r10
            if (r10 == 0) goto L_0x025e
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x025e
            int r11 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            int r10 = r10.send_state
            if (r11 == r10) goto L_0x025e
            r0.lastSendState = r10
            r8 = 1
        L_0x025e:
            if (r8 != 0) goto L_0x0264
            r23.invalidate()
            return
        L_0x0264:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r8 = r0.currentDialogFolderId
            r10 = 0
            if (r8 == 0) goto L_0x0281
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r8 = r23.findFolderTopMessage()
            r0.message = r8
            if (r8 == 0) goto L_0x027f
            long r12 = r8.getDialogId()
            goto L_0x029a
        L_0x027f:
            r12 = r10
            goto L_0x029a
        L_0x0281:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x0295
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r12 = r0.currentDialogId
            boolean r8 = r8.isDialogMuted(r12)
            if (r8 == 0) goto L_0x0295
            r8 = 1
            goto L_0x0296
        L_0x0295:
            r8 = 0
        L_0x0296:
            r0.dialogMuted = r8
            long r12 = r0.currentDialogId
        L_0x029a:
            int r8 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x0341
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r12)
            if (r8 == 0) goto L_0x02cf
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r12)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r8.getEncryptedChat(r10)
            r0.encryptedChat = r8
            if (r8 == 0) goto L_0x0319
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r0.encryptedChat
            long r10 = r10.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            r0.user = r8
            goto L_0x0319
        L_0x02cf:
            boolean r8 = org.telegram.messenger.DialogObject.isUserDialog(r12)
            if (r8 == 0) goto L_0x02e6
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.Long r10 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            r0.user = r8
            goto L_0x0319
        L_0x02e6:
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r10 = -r12
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r10)
            r0.chat = r8
            boolean r10 = r0.isDialogCell
            if (r10 != 0) goto L_0x0319
            if (r8 == 0) goto L_0x0319
            org.telegram.tgnet.TLRPC$InputChannel r8 = r8.migrated_to
            if (r8 == 0) goto L_0x0319
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r10 = r10.migrated_to
            long r10 = r10.channel_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r10)
            if (r8 == 0) goto L_0x0319
            r0.chat = r8
        L_0x0319:
            boolean r8 = r0.useMeForMyMessages
            if (r8 == 0) goto L_0x0341
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 == 0) goto L_0x0341
            org.telegram.messenger.MessageObject r8 = r0.message
            boolean r8 = r8.isOutOwner()
            if (r8 == 0) goto L_0x0341
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            int r10 = r0.currentAccount
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
            long r10 = r10.clientUserId
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            r0.user = r8
        L_0x0341:
            int r8 = r0.currentDialogFolderId
            if (r8 == 0) goto L_0x035e
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r3.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r9)
            org.telegram.messenger.ImageReceiver r10 = r0.avatarImage
            r11 = 0
            r12 = 0
            org.telegram.ui.Components.AvatarDrawable r13 = r0.avatarDrawable
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r0.user
            r16 = 0
            r10.setImage(r11, r12, r13, r14, r15, r16)
            goto L_0x03c1
        L_0x035e:
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 == 0) goto L_0x03af
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r10.setInfo((org.telegram.tgnet.TLRPC$User) r8)
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r8)
            if (r8 == 0) goto L_0x0385
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r8 = 12
            r3.setAvatarType(r8)
            org.telegram.messenger.ImageReceiver r10 = r0.avatarImage
            r11 = 0
            r12 = 0
            org.telegram.ui.Components.AvatarDrawable r13 = r0.avatarDrawable
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r0.user
            r16 = 0
            r10.setImage(r11, r12, r13, r14, r15, r16)
            goto L_0x03c1
        L_0x0385:
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x03a5
            boolean r8 = r0.useMeForMyMessages
            if (r8 != 0) goto L_0x03a5
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r10 = r0.avatarImage
            r11 = 0
            r12 = 0
            org.telegram.ui.Components.AvatarDrawable r13 = r0.avatarDrawable
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r0.user
            r16 = 0
            r10.setImage(r11, r12, r13, r14, r15, r16)
            goto L_0x03c1
        L_0x03a5:
            org.telegram.messenger.ImageReceiver r8 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r8.setForUserOrChat(r10, r11, r3, r4)
            goto L_0x03c1
        L_0x03af:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            if (r3 == 0) goto L_0x03c1
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setForUserOrChat(r8, r10)
        L_0x03c1:
            r10 = 150(0x96, double:7.4E-322)
            r12 = 220(0xdc, double:1.087E-321)
            if (r25 == 0) goto L_0x050a
            int r3 = r0.unreadCount
            if (r1 != r3) goto L_0x03cf
            boolean r3 = r0.markUnread
            if (r7 == r3) goto L_0x050a
        L_0x03cf:
            long r14 = java.lang.System.currentTimeMillis()
            long r2 = r0.lastDialogChangedTime
            long r14 = r14 - r2
            r2 = 100
            int r16 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r16 <= 0) goto L_0x050a
            android.animation.ValueAnimator r2 = r0.countAnimator
            if (r2 == 0) goto L_0x03e3
            r2.cancel()
        L_0x03e3:
            float[] r2 = new float[r9]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.countAnimator = r2
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda2
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Cells.DialogCell$1 r3 = new org.telegram.ui.Cells.DialogCell$1
            r3.<init>()
            r2.addListener(r3)
            if (r1 == 0) goto L_0x0406
            boolean r2 = r0.markUnread
            if (r2 == 0) goto L_0x040d
        L_0x0406:
            boolean r2 = r0.markUnread
            if (r2 != 0) goto L_0x042d
            if (r7 != 0) goto L_0x040d
            goto L_0x042d
        L_0x040d:
            int r2 = r0.unreadCount
            if (r2 != 0) goto L_0x041e
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r10)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x043c
        L_0x041e:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r14 = 430(0x1ae, double:2.124E-321)
            r2.setDuration(r14)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x043c
        L_0x042d:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r12)
            android.animation.ValueAnimator r2 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
        L_0x043c:
            boolean r2 = r0.drawCount
            if (r2 == 0) goto L_0x04f4
            boolean r2 = r0.drawCount2
            if (r2 == 0) goto L_0x04f4
            android.text.StaticLayout r2 = r0.countLayout
            if (r2 == 0) goto L_0x04f4
            java.lang.String r2 = java.lang.String.valueOf(r1)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r7 = r2.length()
            int r14 = r3.length()
            if (r7 != r14) goto L_0x04f0
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r2)
            android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder
            r14.<init>(r3)
            android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder
            r15.<init>(r3)
            r4 = 0
        L_0x046c:
            int r8 = r2.length()
            if (r4 >= r8) goto L_0x049e
            char r8 = r2.charAt(r4)
            char r10 = r3.charAt(r4)
            if (r8 != r10) goto L_0x048f
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            int r10 = r4 + 1
            r7.setSpan(r8, r4, r10, r5)
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            r14.setSpan(r8, r4, r10, r5)
            goto L_0x0499
        L_0x048f:
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            int r10 = r4 + 1
            r15.setSpan(r8, r4, r10, r5)
        L_0x0499:
            int r4 = r4 + 1
            r10 = 150(0x96, double:7.4E-322)
            goto L_0x046c
        L_0x049e:
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r4.measureText(r2)
            double r10 = (double) r2
            double r10 = java.lang.Math.ceil(r10)
            int r2 = (int) r10
            int r2 = java.lang.Math.max(r3, r2)
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r17 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_CENTER
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r4 = r15
            r15 = r3
            r16 = r7
            r18 = r2
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r0.countOldLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r17 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_CENTER
            r15 = r3
            r16 = r4
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r0.countAnimationStableLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r16 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r18 = android.text.Layout.Alignment.ALIGN_CENTER
            r19 = 1065353216(0x3var_, float:1.0)
            r20 = 0
            r21 = 0
            r4 = r14
            r14 = r3
            r15 = r4
            r17 = r2
            r14.<init>(r15, r16, r17, r18, r19, r20, r21)
            r0.countAnimationInLayout = r3
            goto L_0x04f4
        L_0x04f0:
            android.text.StaticLayout r2 = r0.countLayout
            r0.countOldLayout = r2
        L_0x04f4:
            int r2 = r0.countWidth
            r0.countWidthOld = r2
            int r2 = r0.countLeft
            r0.countLeftOld = r2
            int r2 = r0.unreadCount
            if (r2 <= r1) goto L_0x0502
            r1 = 1
            goto L_0x0503
        L_0x0502:
            r1 = 0
        L_0x0503:
            r0.countAnimationIncrement = r1
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x050a:
            int r1 = r0.reactionMentionCount
            if (r1 == 0) goto L_0x0510
            r4 = 1
            goto L_0x0511
        L_0x0510:
            r4 = 0
        L_0x0511:
            if (r25 == 0) goto L_0x003b
            if (r4 == r6) goto L_0x003b
            android.animation.ValueAnimator r1 = r0.reactionsMentionsAnimator
            if (r1 == 0) goto L_0x051c
            r1.cancel()
        L_0x051c:
            r1 = 0
            r0.reactionsMentionsChangeProgress = r1
            float[] r2 = new float[r9]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.reactionsMentionsAnimator = r2
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            org.telegram.ui.Cells.DialogCell$2 r3 = new org.telegram.ui.Cells.DialogCell$2
            r3.<init>()
            r2.addListener(r3)
            if (r4 == 0) goto L_0x054e
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.setDuration(r12)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
            goto L_0x055c
        L_0x054e:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r3 = 150(0x96, double:7.4E-322)
            r2.setDuration(r3)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
        L_0x055c:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.start()
        L_0x0561:
            int r2 = r23.getMeasuredWidth()
            if (r2 != 0) goto L_0x0572
            int r2 = r23.getMeasuredHeight()
            if (r2 == 0) goto L_0x056e
            goto L_0x0572
        L_0x056e:
            r23.requestLayout()
            goto L_0x0575
        L_0x0572:
            r23.buildLayout()
        L_0x0575:
            if (r25 != 0) goto L_0x0588
            boolean r2 = r0.dialogMuted
            if (r2 == 0) goto L_0x057e
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x057f
        L_0x057e:
            r2 = 0
        L_0x057f:
            r0.dialogMutedProgress = r2
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x0588
            r1.cancel()
        L_0x0588:
            r23.invalidate()
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
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0639, code lost:
        if (r0.type == 2) goto L_0x0654;
     */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0703  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x07f8  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0848  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x084a  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0858  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0923  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0946  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0987  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x0991  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0994  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x09a3  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x09dc  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x09e3  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0ab6  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0ef0  */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x0f5b  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x103b  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x12bd  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x12d1  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x12e4  */
    /* JADX WARNING: Removed duplicated region for block: B:692:0x1303  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x132f  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x1393  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x13ec  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x13ff  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x1417  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x1441  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1470  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1485  */
    /* JADX WARNING: Removed duplicated region for block: B:783:0x14af  */
    /* JADX WARNING: Removed duplicated region for block: B:794:0x14d2  */
    /* JADX WARNING: Removed duplicated region for block: B:799:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r35) {
        /*
            r34 = this;
            r8 = r34
            r7 = r35
            long r0 = r8.currentDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0011
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            int r0 = r8.currentDialogFolderId
            r15 = 0
            r14 = 0
            if (r0 == 0) goto L_0x0042
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0042
            float r0 = r0.outProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            float r0 = r8.translationX
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            boolean r0 = r8.drawingForBlur
            if (r0 != 0) goto L_0x0041
            r35.save()
            int r0 = r34.getMeasuredWidth()
            int r1 = r34.getMeasuredHeight()
            r7.clipRect(r15, r15, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r7)
            r35.restore()
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
            r12 = r2
            r8.lastUpdateTime = r0
            float r0 = r8.clipProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0081
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x0081
            r35.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r34.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r34.getMeasuredHeight()
            int r3 = r8.bottomClip
            float r3 = (float) r3
            float r4 = r8.clipProgress
            float r3 = r3 * r4
            int r3 = (int) r3
            int r2 = r2 - r3
            float r2 = (float) r2
            r7.clipRect(r14, r0, r1, r2)
        L_0x0081:
            float r0 = r8.translationX
            r18 = 1090519040(0x41000000, float:8.0)
            r11 = 4
            r19 = 1073741824(0x40000000, float:2.0)
            r20 = 1082130432(0x40800000, float:4.0)
            r9 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x00b4
            float r0 = r8.cornerProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0098
            goto L_0x00b4
        L_0x0098:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x00af
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r14)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r15
        L_0x00af:
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 1
            goto L_0x04a8
        L_0x00b4:
            r35.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r5 = "chats_archivePinBackground"
            java.lang.String r4 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00fd
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00e0
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628814(0x7f0e130e, float:1.8884931E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628814(0x7f0e130e, float:1.8884931E38)
            goto L_0x011d
        L_0x00e0:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            java.lang.String r3 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            goto L_0x011d
        L_0x00fd:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0124
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131627828(0x7f0e0var_, float:1.8882931E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627828(0x7f0e0var_, float:1.8882931E38)
        L_0x011d:
            r33 = r3
            r3 = r1
            r1 = r33
            goto L_0x0219
        L_0x0124:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01fb
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r3 = 3
            if (r2 != r3) goto L_0x0163
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x0152
            r2 = 2131628602(0x7f0e123a, float:1.8884501E38)
            java.lang.String r3 = "SwipeUnmute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628602(0x7f0e123a, float:1.8884501E38)
            goto L_0x011d
        L_0x0152:
            r2 = 2131628590(0x7f0e122e, float:1.8884477E38)
            java.lang.String r3 = "SwipeMute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628590(0x7f0e122e, float:1.8884477E38)
            goto L_0x011d
        L_0x0163:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r11) goto L_0x0184
            r2 = 2131628587(0x7f0e122b, float:1.888447E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r2 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131628587(0x7f0e122b, float:1.888447E38)
            goto L_0x011d
        L_0x0184:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r9) goto L_0x01b9
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x01a7
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x0195
            goto L_0x01a7
        L_0x0195:
            r2 = 2131628589(0x7f0e122d, float:1.8884475E38)
            java.lang.String r3 = "SwipeMarkAsUnread"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131628589(0x7f0e122d, float:1.8884475E38)
            goto L_0x011d
        L_0x01a7:
            r2 = 2131628588(0x7f0e122c, float:1.8884473E38)
            java.lang.String r3 = "SwipeMarkAsRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131628588(0x7f0e122c, float:1.8884473E38)
            goto L_0x011d
        L_0x01b9:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01e9
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01d7
            r2 = 2131628603(0x7f0e123b, float:1.8884503E38)
            java.lang.String r3 = "SwipeUnpin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131628603(0x7f0e123b, float:1.8884503E38)
            goto L_0x011d
        L_0x01d7:
            r2 = 2131628591(0x7f0e122f, float:1.888448E38)
            java.lang.String r3 = "SwipePin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131628591(0x7f0e122f, float:1.888448E38)
            goto L_0x011d
        L_0x01e9:
            r2 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r3 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            goto L_0x011d
        L_0x01fb:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628805(0x7f0e1305, float:1.8884913E38)
            java.lang.String r3 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628805(0x7f0e1305, float:1.8884913E38)
            goto L_0x011d
        L_0x0219:
            boolean r11 = r8.swipeCanceled
            if (r11 == 0) goto L_0x0226
            org.telegram.ui.Components.RLottieDrawable r11 = r8.lastDrawTranslationDrawable
            if (r11 == 0) goto L_0x0226
            r8.translationDrawable = r11
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x022c
        L_0x0226:
            org.telegram.ui.Components.RLottieDrawable r11 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r11
            r8.lastDrawSwipeMessageStringId = r2
        L_0x022c:
            r11 = r2
            boolean r2 = r8.translationAnimationStarted
            if (r2 != 0) goto L_0x0253
            float r2 = r8.translationX
            float r2 = java.lang.Math.abs(r2)
            r17 = 1110179840(0x422CLASSNAME, float:43.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0253
            r8.translationAnimationStarted = r9
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setProgress(r14)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.start()
        L_0x0253:
            int r2 = r34.getMeasuredWidth()
            float r2 = (float) r2
            float r10 = r8.translationX
            float r10 = r10 + r2
            float r2 = r8.currentRevealProgress
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x02d6
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r0 = (float) r0
            float r2 = r10 - r0
            r0 = 0
            int r6 = r34.getMeasuredWidth()
            float r6 = (float) r6
            int r9 = r34.getMeasuredHeight()
            float r9 = (float) r9
            android.graphics.Paint r21 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r29 = r1
            r1 = r35
            r30 = r3
            r3 = r0
            r0 = r4
            r4 = r6
            r22 = r5
            r5 = r9
            r9 = 1065353216(0x3var_, float:1.0)
            r6 = r21
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x02de
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x02a4
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
        L_0x02a4:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x02de
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r0 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r2 = "Line 3.**"
            r1.setLayerColor(r2, r0)
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r0.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r15
            goto L_0x02de
        L_0x02d6:
            r29 = r1
            r30 = r3
            r22 = r5
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x02de:
            int r0 = r34.getMeasuredWidth()
            r1 = 1110179840(0x422CLASSNAME, float:43.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            int r1 = r1.getIntrinsicWidth()
            r2 = 2
            int r1 = r1 / r2
            int r0 = r0 - r1
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x02fe
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x02fb
            goto L_0x02fe
        L_0x02fb:
            r1 = 1091567616(0x41100000, float:9.0)
            goto L_0x0300
        L_0x02fe:
            r1 = 1094713344(0x41400000, float:12.0)
        L_0x0300:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            r3 = 2
            int r2 = r2 / r3
            int r2 = r2 + r0
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r3
            int r4 = r4 + r1
            float r3 = r8.currentRevealProgress
            int r3 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x03a8
            r35.save()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r3 = r10 - r3
            int r5 = r34.getMeasuredWidth()
            float r5 = (float) r5
            int r6 = r34.getMeasuredHeight()
            float r6 = (float) r6
            r7.clipRect(r3, r14, r5, r6)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r5 = r30
            r3.setColor(r5)
            int r3 = r2 * r2
            int r5 = r34.getMeasuredHeight()
            int r5 = r4 - r5
            int r6 = r34.getMeasuredHeight()
            int r6 = r4 - r6
            int r5 = r5 * r6
            int r3 = r3 + r5
            double r5 = (double) r3
            double r5 = java.lang.Math.sqrt(r5)
            float r3 = (float) r5
            float r2 = (float) r2
            float r4 = (float) r4
            android.view.animation.AccelerateInterpolator r5 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r6 = r8.currentRevealProgress
            float r5 = r5.getInterpolation(r6)
            float r3 = r3 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7.drawCircle(r2, r4, r3, r5)
            r35.restore()
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r2 != 0) goto L_0x0376
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r4 = "Arrow.**"
            r2.setLayerColor(r4, r3)
            r2 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r2
        L_0x0376:
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r2 != 0) goto L_0x03a8
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r2.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r4 = "Line 1.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r4 = "Line 2.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r4 = "Line 3.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r2.commitApplyLayerColors()
            r2 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r2
        L_0x03a8:
            r35.save()
            float r0 = (float) r0
            float r1 = (float) r1
            r7.translate(r0, r1)
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x03d5
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 == 0) goto L_0x03d5
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r1 = r8.interpolator
            float r0 = r1.getInterpolation(r0)
            float r0 = r0 + r9
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            int r1 = r1.getIntrinsicWidth()
            r2 = 2
            int r1 = r1 / r2
            float r1 = (float) r1
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r2
            float r2 = (float) r3
            r7.scale(r0, r0, r1, r2)
        L_0x03d5:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r15, (int) r15)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.draw(r7)
            r35.restore()
            int r0 = r34.getMeasuredWidth()
            float r0 = (float) r0
            int r1 = r34.getMeasuredHeight()
            float r1 = (float) r1
            r7.clipRect(r10, r14, r0, r1)
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r3 = r29
            float r0 = r0.measureText(r3)
            double r0 = (double) r0
            double r0 = java.lang.Math.ceil(r0)
            int r0 = (int) r0
            int r1 = r8.swipeMessageTextId
            if (r1 != r11) goto L_0x0409
            int r1 = r8.swipeMessageWidth
            int r2 = r34.getMeasuredWidth()
            if (r1 == r2) goto L_0x0456
        L_0x0409:
            r8.swipeMessageTextId = r11
            int r1 = r34.getMeasuredWidth()
            r8.swipeMessageWidth = r1
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.TextPaint r23 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r2 = 1117782016(0x42a00000, float:80.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r24 = java.lang.Math.min(r2, r0)
            android.text.Layout$Alignment r25 = android.text.Layout.Alignment.ALIGN_CENTER
            r26 = 1065353216(0x3var_, float:1.0)
            r27 = 0
            r28 = 0
            r21 = r1
            r22 = r3
            r21.<init>(r22, r23, r24, r25, r26, r27, r28)
            r8.swipeMessageTextLayout = r1
            int r1 = r1.getLineCount()
            r2 = 1
            if (r1 <= r2) goto L_0x0456
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.TextPaint r23 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r2 = 1118044160(0x42a40000, float:82.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r24 = java.lang.Math.min(r2, r0)
            android.text.Layout$Alignment r25 = android.text.Layout.Alignment.ALIGN_CENTER
            r26 = 1065353216(0x3var_, float:1.0)
            r27 = 0
            r28 = 0
            r21 = r1
            r22 = r3
            r21.<init>(r22, r23, r24, r25, r26, r27, r28)
            r8.swipeMessageTextLayout = r1
        L_0x0456:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x04a4
            r35.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            r10 = 1
            if (r0 <= r10) goto L_0x046d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x046e
        L_0x046d:
            r0 = 0
        L_0x046e:
            int r1 = r34.getMeasuredWidth()
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
            if (r2 != 0) goto L_0x0490
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x048d
            goto L_0x0490
        L_0x048d:
            r2 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x0492
        L_0x0490:
            r2 = 1112014848(0x42480000, float:50.0)
        L_0x0492:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 + r0
            r7.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r7)
            r35.restore()
            goto L_0x04a5
        L_0x04a4:
            r10 = 1
        L_0x04a5:
            r35.restore()
        L_0x04a8:
            float r0 = r8.translationX
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x04b6
            r35.save()
            float r0 = r8.translationX
            r7.translate(r0, r14)
        L_0x04b6:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r0 = (float) r0
            float r1 = r8.cornerProgress
            float r0 = r0 * r1
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x04d9
            android.graphics.RectF r1 = r8.rect
            int r2 = r34.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r34.getMeasuredHeight()
            float r3 = (float) r3
            r1.set(r14, r14, r2, r3)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r7.drawRoundRect(r1, r0, r0, r2)
        L_0x04d9:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x050e
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x04e7
            float r1 = r8.archiveBackgroundProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x050e
        L_0x04e7:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r2, r3, r9)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r34.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r34.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0536
        L_0x050e:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x0516
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x0536
        L_0x0516:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r34.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r34.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0536:
            float r1 = r8.translationX
            java.lang.String r6 = "windowBackgroundWhite"
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x0544
            float r1 = r8.cornerProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x05c7
        L_0x0544:
            r35.save()
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            int r2 = r34.getMeasuredWidth()
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r34.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r34.getMeasuredHeight()
            float r4 = (float) r4
            r1.set(r2, r14, r3, r4)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7.drawRoundRect(r1, r0, r0, r2)
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x057f
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r7.drawRoundRect(r1, r0, r0, r2)
        L_0x057f:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x05a8
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x058d
            float r1 = r8.archiveBackgroundProgress
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x05a8
        L_0x058d:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r2, r3, r9)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7.drawRoundRect(r1, r0, r0, r2)
            goto L_0x05c4
        L_0x05a8:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x05b0
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x05c4
        L_0x05b0:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7.drawRoundRect(r1, r0, r0, r2)
        L_0x05c4:
            r35.restore()
        L_0x05c7:
            float r0 = r8.translationX
            r21 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x05e2
            float r0 = r8.cornerProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x05f7
            float r1 = (float) r12
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x05f4
            r8.cornerProgress = r9
            goto L_0x05f4
        L_0x05e2:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x05f7
            float r1 = (float) r12
            float r1 = r1 / r21
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x05f4
            r8.cornerProgress = r14
        L_0x05f4:
            r22 = 1
            goto L_0x05f9
        L_0x05f7:
            r22 = 0
        L_0x05f9:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x060b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r7)
        L_0x060b:
            android.text.StaticLayout r0 = r8.nameLayout
            r23 = 1092616192(0x41200000, float:10.0)
            r24 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x068e
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x062e
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            r1 = 2
            goto L_0x0669
        L_0x062e:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0653
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x063c
            int r0 = r0.type
            r1 = 2
            if (r0 != r1) goto L_0x063d
            goto L_0x0654
        L_0x063c:
            r1 = 2
        L_0x063d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r2 = r8.paintIndex
            r3 = r0[r2]
            r0 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r4 = "chats_name"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r3.setColor(r2)
            goto L_0x0669
        L_0x0653:
            r1 = 2
        L_0x0654:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r2 = r8.paintIndex
            r3 = r0[r2]
            r0 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r4 = "chats_secretName"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r3.setColor(r2)
        L_0x0669:
            r35.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x067b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0678
            goto L_0x067b
        L_0x0678:
            r2 = 1095761920(0x41500000, float:13.0)
            goto L_0x067d
        L_0x067b:
            r2 = 1092616192(0x41200000, float:10.0)
        L_0x067d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r7.translate(r0, r2)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r7)
            r35.restore()
            goto L_0x068f
        L_0x068e:
            r1 = 2
        L_0x068f:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x06ab
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x06ab
            r35.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r2 = r8.timeTop
            float r2 = (float) r2
            r7.translate(r0, r2)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r7)
            r35.restore()
        L_0x06ab:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x06ff
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06c3
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r0.setColor(r2)
            goto L_0x06e6
        L_0x06c3:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x06d7
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_draft"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r0.setColor(r2)
            goto L_0x06e6
        L_0x06d7:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessage_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r0.setColor(r2)
        L_0x06e6:
            r35.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r2 = r8.messageNameTop
            float r2 = (float) r2
            r7.translate(r0, r2)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x06f8 }
            r0.draw(r7)     // Catch:{ Exception -> 0x06f8 }
            goto L_0x06fc
        L_0x06f8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06fc:
            r35.restore()
        L_0x06ff:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x084a
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0737
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0721
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r3 = r0[r2]
            r0 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r4 = "chats_nameMessageArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r3.setColor(r2)
            goto L_0x074c
        L_0x0721:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r3 = r0[r2]
            r0 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r4 = "chats_messageArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r3.setColor(r2)
            goto L_0x074c
        L_0x0737:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r3 = r0[r2]
            r0 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r4 = "chats_message"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0.linkColor = r2
            r3.setColor(r2)
        L_0x074c:
            r35.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r2 = r8.messageTop
            float r2 = (float) r2
            r7.translate(r0, r2)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07ca
            r35.save()     // Catch:{ Exception -> 0x07bc }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07bc }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r7, r0)     // Catch:{ Exception -> 0x07bc }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07bc }
            r0.draw(r7)     // Catch:{ Exception -> 0x07bc }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07bc }
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r11 = r8.animatedEmojiStack     // Catch:{ Exception -> 0x07bc }
            r2 = -1114007142(0xffffffffbd99999a, float:-0.075)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r8.spoilers     // Catch:{ Exception -> 0x07bc }
            r4 = 0
            r5 = 0
            r17 = 0
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 1065353216(0x3var_, float:1.0)
            r9 = r35
            r27 = r6
            r6 = 1
            r10 = r0
            r31 = r12
            r12 = r2
            r13 = r3
            r3 = 0
            r14 = r4
            r4 = 0
            r15 = r5
            r16 = r17
            r17 = r25
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r9, r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x07ba }
            r35.restore()     // Catch:{ Exception -> 0x07ba }
            r15 = 0
        L_0x0797:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07ba }
            int r0 = r0.size()     // Catch:{ Exception -> 0x07ba }
            if (r15 >= r0) goto L_0x07eb
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07ba }
            java.lang.Object r0 = r0.get(r15)     // Catch:{ Exception -> 0x07ba }
            org.telegram.ui.Components.spoilers.SpoilerEffect r0 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r0     // Catch:{ Exception -> 0x07ba }
            android.text.StaticLayout r2 = r8.messageLayout     // Catch:{ Exception -> 0x07ba }
            android.text.TextPaint r2 = r2.getPaint()     // Catch:{ Exception -> 0x07ba }
            int r2 = r2.getColor()     // Catch:{ Exception -> 0x07ba }
            r0.setColor(r2)     // Catch:{ Exception -> 0x07ba }
            r0.draw(r7)     // Catch:{ Exception -> 0x07ba }
            int r15 = r15 + 1
            goto L_0x0797
        L_0x07ba:
            r0 = move-exception
            goto L_0x07c6
        L_0x07bc:
            r0 = move-exception
            r27 = r6
            r31 = r12
            r3 = 0
            r4 = 0
            r6 = 1
            r26 = 1065353216(0x3var_, float:1.0)
        L_0x07c6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07eb
        L_0x07ca:
            r27 = r6
            r31 = r12
            r3 = 0
            r4 = 0
            r6 = 1
            r26 = 1065353216(0x3var_, float:1.0)
            android.text.StaticLayout r0 = r8.messageLayout
            r0.draw(r7)
            android.text.StaticLayout r10 = r8.messageLayout
            org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans r11 = r8.animatedEmojiStack
            r12 = -1114007142(0xffffffffbd99999a, float:-0.075)
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1065353216(0x3var_, float:1.0)
            r9 = r35
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r9, r10, r11, r12, r13, r14, r15, r16, r17)
        L_0x07eb:
            r35.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0848
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0848
            r35.save()
            int r2 = r8.printingStringType
            r9 = 4
            if (r2 == r6) goto L_0x081c
            if (r2 != r9) goto L_0x0803
            goto L_0x081c
        L_0x0803:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r5 = r8.messageTop
            float r5 = (float) r5
            r10 = 1099956224(0x41900000, float:18.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = r0.getIntrinsicHeight()
            int r10 = r10 - r11
            float r10 = (float) r10
            float r10 = r10 / r19
            float r5 = r5 + r10
            r7.translate(r2, r5)
            goto L_0x082e
        L_0x081c:
            int r5 = r8.statusDrawableLeft
            float r5 = (float) r5
            int r10 = r8.messageTop
            if (r2 != r6) goto L_0x0828
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r26)
            goto L_0x0829
        L_0x0828:
            r15 = 0
        L_0x0829:
            int r10 = r10 + r15
            float r2 = (float) r10
            r7.translate(r5, r2)
        L_0x082e:
            r0.draw(r7)
            int r2 = r8.statusDrawableLeft
            int r5 = r8.messageTop
            int r10 = r0.getIntrinsicWidth()
            int r10 = r10 + r2
            int r11 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r11 = r11 + r0
            r8.invalidate(r2, r5, r10, r11)
            r35.restore()
            goto L_0x0854
        L_0x0848:
            r9 = 4
            goto L_0x0854
        L_0x084a:
            r27 = r6
            r31 = r12
            r3 = 0
            r4 = 0
            r6 = 1
            r9 = 4
            r26 = 1065353216(0x3var_, float:1.0)
        L_0x0854:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0923
            boolean r0 = r8.drawClock
            boolean r2 = r8.drawCheck1
            if (r2 == 0) goto L_0x0860
            r15 = 2
            goto L_0x0861
        L_0x0860:
            r15 = 0
        L_0x0861:
            int r0 = r0 + r15
            boolean r2 = r8.drawCheck2
            if (r2 == 0) goto L_0x0868
            r15 = 4
            goto L_0x0869
        L_0x0868:
            r15 = 0
        L_0x0869:
            int r0 = r0 + r15
            int r2 = r8.lastStatusDrawableParams
            if (r2 < 0) goto L_0x0877
            if (r2 == r0) goto L_0x0877
            boolean r5 = r8.statusDrawableAnimationInProgress
            if (r5 != 0) goto L_0x0877
            r8.createStatusDrawableAnimator(r2, r0)
        L_0x0877:
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 == 0) goto L_0x087d
            int r0 = r8.animateToStatusDrawableParams
        L_0x087d:
            r5 = r0 & 1
            if (r5 == 0) goto L_0x0883
            r10 = 1
            goto L_0x0884
        L_0x0883:
            r10 = 0
        L_0x0884:
            r5 = r0 & 2
            if (r5 == 0) goto L_0x088a
            r11 = 1
            goto L_0x088b
        L_0x088a:
            r11 = 0
        L_0x088b:
            r0 = r0 & r9
            if (r0 == 0) goto L_0x0890
            r0 = 1
            goto L_0x0891
        L_0x0890:
            r0 = 0
        L_0x0891:
            if (r2 == 0) goto L_0x08fa
            int r2 = r8.animateFromStatusDrawableParams
            r5 = r2 & 1
            if (r5 == 0) goto L_0x089b
            r5 = 1
            goto L_0x089c
        L_0x089b:
            r5 = 0
        L_0x089c:
            r12 = r2 & 2
            if (r12 == 0) goto L_0x08a2
            r12 = 1
            goto L_0x08a3
        L_0x08a2:
            r12 = 0
        L_0x08a3:
            r2 = r2 & r9
            if (r2 == 0) goto L_0x08a8
            r13 = 1
            goto L_0x08a9
        L_0x08a8:
            r13 = 0
        L_0x08a9:
            if (r10 != 0) goto L_0x08cf
            if (r5 != 0) goto L_0x08cf
            if (r13 == 0) goto L_0x08cf
            if (r12 != 0) goto L_0x08cf
            if (r11 == 0) goto L_0x08cf
            if (r0 == 0) goto L_0x08cf
            r12 = 1
            float r13 = r8.statusDrawableProgress
            r14 = 2
            r1 = r34
            r2 = r35
            r15 = 0
            r3 = r10
            r10 = 0
            r4 = r11
            r5 = r0
            r11 = r27
            r9 = 1
            r6 = r12
            r12 = r7
            r7 = r13
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            r13 = r11
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x090e
        L_0x08cf:
            r6 = r27
            r9 = 1
            r14 = 2
            r15 = 0
            r16 = 0
            float r1 = r8.statusDrawableProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r17 = r3 - r1
            r1 = r34
            r2 = r35
            r9 = 1065353216(0x3var_, float:1.0)
            r3 = r5
            r5 = 0
            r4 = r12
            r12 = 0
            r5 = r13
            r13 = r6
            r6 = r16
            r12 = r7
            r7 = r17
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            r6 = 0
            float r7 = r8.statusDrawableProgress
            r3 = r10
            r4 = r11
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x090e
        L_0x08fa:
            r12 = r7
            r13 = r27
            r9 = 1065353216(0x3var_, float:1.0)
            r14 = 2
            r15 = 0
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r34
            r2 = r35
            r3 = r10
            r4 = r11
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x090e:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0916
            r1 = 2
            goto L_0x0917
        L_0x0916:
            r1 = 0
        L_0x0917:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x091e
            r1 = 4
            goto L_0x091f
        L_0x091e:
            r1 = 0
        L_0x091f:
            int r0 = r0 + r1
            r8.lastStatusDrawableParams = r0
            goto L_0x092a
        L_0x0923:
            r12 = r7
            r13 = r27
            r9 = 1065353216(0x3var_, float:1.0)
            r14 = 2
            r15 = 0
        L_0x092a:
            int r0 = r8.dialogsType
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 == r14) goto L_0x09e3
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x093a
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r2 <= 0) goto L_0x09e3
        L_0x093a:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x09e3
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x09e3
            boolean r2 = r8.drawPremium
            if (r2 != 0) goto L_0x09e3
            if (r0 == 0) goto L_0x095f
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x095f
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x095b
            r8.dialogMutedProgress = r9
            goto L_0x0977
        L_0x095b:
            r34.invalidate()
            goto L_0x0977
        L_0x095f:
            if (r0 != 0) goto L_0x0977
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r2 == 0) goto L_0x0977
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r0 >= 0) goto L_0x0974
            r8.dialogMutedProgress = r15
            goto L_0x0977
        L_0x0974:
            r34.invalidate()
        L_0x0977:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0987
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0984
            goto L_0x0987
        L_0x0984:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0988
        L_0x0987:
            r3 = 0
        L_0x0988:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0994
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x0996
        L_0x0994:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0996:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x09dc
            r35.save()
            float r0 = r8.dialogMutedProgress
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerX()
            float r2 = (float) r2
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerY()
            float r3 = (float) r3
            r12.scale(r0, r0, r2, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            float r2 = r8.dialogMutedProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r12)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r2 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r2)
            r35.restore()
            goto L_0x0a88
        L_0x09dc:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r12)
            goto L_0x0a88
        L_0x09e3:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0a2e
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09fc
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09f9
            goto L_0x09fc
        L_0x09f9:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09fe
        L_0x09fc:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x09fe:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a1a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a17
            goto L_0x0a1a
        L_0x0a17:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a1c
        L_0x0a1a:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x0a1c:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r12)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r12)
            goto L_0x0a88
        L_0x0a2e:
            boolean r0 = r8.drawPremium
            if (r0 == 0) goto L_0x0a58
            org.telegram.ui.Components.Premium.PremiumGradient r0 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r0 = r0.premiumStarDrawableMini
            int r2 = r8.nameMuteLeft
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 - r3
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a4b
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a48
            goto L_0x0a4b
        L_0x0a48:
            r3 = 1098383360(0x41780000, float:15.5)
            goto L_0x0a4d
        L_0x0a4b:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a4d:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            r0.draw(r12)
            goto L_0x0a88
        L_0x0a58:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a88
            r2 = 1
            if (r0 != r2) goto L_0x0a62
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a64
        L_0x0a62:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a64:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a72
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a6f
            goto L_0x0a72
        L_0x0a6f:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a74
        L_0x0a72:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a74:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            r2 = 1
            if (r0 != r2) goto L_0x0a83
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a85
        L_0x0a83:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a85:
            r0.draw(r12)
        L_0x0a88:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a92
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0aaa
        L_0x0a92:
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
            r0.draw(r12)
        L_0x0aaa:
            boolean r0 = r8.drawError
            r2 = 1085276160(0x40b00000, float:5.5)
            r3 = 1102577664(0x41b80000, float:23.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0b02
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r6 = r8.reorderIconProgress
            float r6 = r9 - r6
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r6 = (float) r1
            int r7 = r8.errorTop
            float r7 = (float) r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r10
            float r1 = (float) r1
            int r10 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r10 = r10 + r3
            float r3 = (float) r10
            r0.set(r6, r7, r1, r3)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r1 * r5
            float r1 = r1 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r12.drawRoundRect(r0, r3, r1, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r12)
            goto L_0x0eea
        L_0x0b02:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0b0a
            boolean r6 = r8.drawMention
            if (r6 == 0) goto L_0x0b0e
        L_0x0b0a:
            boolean r6 = r8.drawCount2
            if (r6 != 0) goto L_0x0b3f
        L_0x0b0e:
            float r6 = r8.countChangeProgress
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x0b3f
            boolean r6 = r8.drawReactionMention
            if (r6 != 0) goto L_0x0b3f
            float r6 = r8.reactionsMentionsChangeProgress
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x0b1f
            goto L_0x0b3f
        L_0x0b1f:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0eea
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r6 = r9 - r2
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r12)
            goto L_0x0eea
        L_0x0b3f:
            if (r0 == 0) goto L_0x0b45
            boolean r0 = r8.drawCount2
            if (r0 != 0) goto L_0x0b4b
        L_0x0b45:
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0d9e
        L_0x0b4b:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0b58
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0b58
            float r6 = r8.countChangeProgress
            float r6 = r9 - r6
            goto L_0x0b5a
        L_0x0b58:
            float r6 = r8.countChangeProgress
        L_0x0b5a:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0ccc
            if (r0 != 0) goto L_0x0b62
            goto L_0x0ccc
        L_0x0b62:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b6e
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b6b
            goto L_0x0b6e
        L_0x0b6b:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b70
        L_0x0b6e:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b70:
            float r7 = r8.reorderIconProgress
            float r7 = r9 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r10 = r8.reorderIconProgress
            float r10 = r9 - r10
            float r10 = r10 * r1
            int r10 = (int) r10
            r7.setAlpha(r10)
            float r7 = r6 * r19
            int r10 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r10 <= 0) goto L_0x0b8f
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0b90
        L_0x0b8f:
            r10 = r7
        L_0x0b90:
            int r11 = r8.countLeft
            float r11 = (float) r11
            float r11 = r11 * r10
            int r4 = r8.countLeftOld
            float r4 = (float) r4
            float r25 = r9 - r10
            float r4 = r4 * r25
            float r11 = r11 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            float r4 = r11 - r4
            android.graphics.RectF r15 = r8.rect
            int r14 = r8.countTop
            float r14 = (float) r14
            int r2 = r8.countWidth
            float r2 = (float) r2
            float r2 = r2 * r10
            float r2 = r2 + r4
            int r1 = r8.countWidthOld
            float r1 = (float) r1
            float r1 = r1 * r25
            float r2 = r2 + r1
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r2 + r1
            int r1 = r8.countTop
            int r29 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r29
            float r1 = (float) r1
            r15.set(r4, r14, r2, r1)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0bd9
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r2 = r2.getInterpolation(r7)
            goto L_0x0be9
        L_0x0bd9:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r4 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 - r4
            float r6 = r6 * r19
            float r6 = r9 - r6
            float r2 = r2.getInterpolation(r6)
        L_0x0be9:
            float r2 = r2 * r1
            float r2 = r2 + r9
            r35.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r12.scale(r2, r2, r1, r4)
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r2 * r5
            float r2 = r2 * r5
            r12.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            if (r0 == 0) goto L_0x0CLASSNAME
            r35.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 + r1
            float r0 = (float) r0
            r12.translate(r11, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r12)
            r35.restore()
        L_0x0CLASSNAME:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = (float) r0
            float r4 = r2 * r10
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            r35.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
        L_0x0CLASSNAME:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r1 = r1 + r4
            r12.translate(r11, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r12)
            r35.restore()
            goto L_0x0c8d
        L_0x0CLASSNAME:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0c8d
            r35.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
        L_0x0CLASSNAME:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r1 = r1 + r4
            r12.translate(r11, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r12)
            r35.restore()
        L_0x0c8d:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0cc2
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r2 * r25
            int r2 = (int) r2
            r1.setAlpha(r2)
            r35.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0ca6
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r1 = -r1
            goto L_0x0caa
        L_0x0ca6:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
        L_0x0caa:
            float r1 = (float) r1
            float r1 = r1 * r10
            int r2 = r8.countTop
            float r2 = (float) r2
            float r1 = r1 + r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r2 = (float) r2
            float r1 = r1 + r2
            r12.translate(r11, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r12)
            r35.restore()
        L_0x0cc2:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r35.restore()
            goto L_0x0d9e
        L_0x0ccc:
            if (r0 != 0) goto L_0x0ccf
            goto L_0x0cd1
        L_0x0ccf:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0cd1:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0cdd
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0cda
            goto L_0x0cdd
        L_0x0cda:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0cdf
        L_0x0cdd:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0cdf:
            float r1 = r8.reorderIconProgress
            float r1 = r9 - r1
            r2 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r2
            int r1 = (int) r1
            r0.setAlpha(r1)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r8.reorderIconProgress
            float r4 = r9 - r4
            float r4 = r4 * r2
            int r2 = (int) r4
            r1.setAlpha(r2)
            int r1 = r8.countLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r4
            android.graphics.RectF r2 = r8.rect
            float r4 = (float) r1
            int r10 = r8.countTop
            float r10 = (float) r10
            int r11 = r8.countWidth
            int r1 = r1 + r11
            r11 = 1093664768(0x41300000, float:11.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = r1 + r11
            float r1 = (float) r1
            int r11 = r8.countTop
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r11 = r11 + r14
            float r11 = (float) r11
            r2.set(r4, r10, r1, r11)
            int r1 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r1 == 0) goto L_0x0d73
            boolean r1 = r8.drawPin
            if (r1 == 0) goto L_0x0d61
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r9 - r2
            r4 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r4
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r2 = r8.pinLeft
            int r4 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r2, (int) r4)
            r35.save()
            float r1 = r9 - r6
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerX()
            float r2 = (float) r2
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r4 = r4.getBounds()
            int r4 = r4.centerY()
            float r4 = (float) r4
            r12.scale(r1, r1, r2, r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r12)
            r35.restore()
        L_0x0d61:
            r35.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerY()
            r12.scale(r6, r6, r1, r2)
        L_0x0d73:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r2 * r5
            float r2 = r2 * r5
            r12.drawRoundRect(r1, r4, r2, r0)
            if (r7 == 0) goto L_0x0d97
            r35.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r1 + r2
            float r1 = (float) r1
            r12.translate(r0, r1)
            r7.draw(r12)
            r35.restore()
        L_0x0d97:
            int r0 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0d9e
            r35.restore()
        L_0x0d9e:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0e4e
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r1 = r8.reorderIconProgress
            float r6 = r9 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r2
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r4 = r8.countTop
            float r4 = (float) r4
            int r6 = r8.mentionWidth
            int r0 = r0 + r6
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            float r0 = (float) r0
            int r6 = r8.countTop
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r6 + r7
            float r6 = (float) r6
            r1.set(r2, r4, r0, r6)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0de0
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0de0
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0de2
        L_0x0de0:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0de2:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r2 * r5
            float r2 = r2 * r5
            r12.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0e19
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            float r6 = r9 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            r35.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r1 = r1 + r2
            float r1 = (float) r1
            r12.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r12)
            r35.restore()
            goto L_0x0e4e
        L_0x0e19:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            float r6 = r9 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r1 = r1 - r2
            int r2 = r8.countTop
            r4 = 1078774989(0x404ccccd, float:3.2)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r1, r2, r4, r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r12)
        L_0x0e4e:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0e58
            float r0 = r8.reactionsMentionsChangeProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0eea
        L_0x0e58:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            float r1 = r8.reorderIconProgress
            float r6 = r9 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            int r0 = r8.reactionMentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r4 = r8.countTop
            float r4 = (float) r4
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r6
            float r0 = (float) r0
            int r6 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r6 + r3
            float r3 = (float) r6
            r1.set(r2, r4, r0, r3)
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            r35.save()
            float r1 = r8.reactionsMentionsChangeProgress
            int r2 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0ea7
            boolean r2 = r8.drawReactionMention
            if (r2 == 0) goto L_0x0e96
            goto L_0x0e98
        L_0x0e96:
            float r1 = r9 - r1
        L_0x0e98:
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r12.scale(r1, r1, r2, r3)
        L_0x0ea7:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r2 * r5
            float r2 = r2 * r5
            r12.drawRoundRect(r1, r3, r2, r0)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            float r1 = r8.reorderIconProgress
            float r6 = r9 - r1
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
            r0.draw(r12)
            r35.restore()
        L_0x0eea:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0f0c
            r35.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 + r9
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r12.scale(r0, r0, r1, r2)
        L_0x0f0c:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0f1a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0f1a
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0f1f
        L_0x0f1a:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r12)
        L_0x0f1f:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r12)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0var_
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
            r0.draw(r12)
        L_0x0var_:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0f5e
            r35.restore()
        L_0x0f5e:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x12f9
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x12f9
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x103b
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x103b
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x103b
            boolean r0 = r34.isOnline()
            if (r0 != 0) goto L_0x0var_
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x12f9
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0var_
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
        L_0x0var_:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0fb8
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0fb1
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0faf
            goto L_0x0fb1
        L_0x0faf:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fb1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0fcf
        L_0x0fb8:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0fc9
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0fc7
            goto L_0x0fc9
        L_0x0fc7:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fc9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0fcf:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r13, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r1 = (float) r1
            float r2 = (float) r2
            r3 = 1088421888(0x40e00000, float:7.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.onlineProgress
            float r3 = r3 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r12.drawCircle(r1, r2, r3, r4)
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
            r12.drawCircle(r1, r2, r3, r4)
            if (r0 == 0) goto L_0x1021
            float r0 = r8.onlineProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x12f9
            r10 = r31
            float r1 = (float) r10
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x1036
            r8.onlineProgress = r9
            goto L_0x1036
        L_0x1021:
            r10 = r31
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1038
            float r2 = (float) r10
            float r2 = r2 / r21
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1036
            r8.onlineProgress = r1
        L_0x1036:
            r22 = 1
        L_0x1038:
            r2 = 0
            goto L_0x12fd
        L_0x103b:
            r10 = r31
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x1038
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x104b
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x104b
            r15 = 1
            goto L_0x104c
        L_0x104b:
            r15 = 0
        L_0x104c:
            r8.hasCall = r15
            if (r15 != 0) goto L_0x1057
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1038
        L_0x1057:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x106a
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x106a
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r6 = r9 - r0
            goto L_0x106c
        L_0x106a:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x106c:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x107a
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x107c
        L_0x107a:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x107c:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x109f
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x1098
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1096
            goto L_0x1098
        L_0x1096:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1098:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x10b6
        L_0x109f:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x10b0
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x10ae
            goto L_0x10b0
        L_0x10ae:
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x10b0:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x10b6:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r8.resourcesProvider
            int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r13, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
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
            r12.drawCircle(r2, r0, r3, r4)
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
            r12.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r13, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            if (r3 != 0) goto L_0x1126
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
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
            float r14 = r8.innerProgress
        L_0x1121:
            float r5 = r5 * r14
            float r4 = r4 - r5
            goto L_0x1215
        L_0x1126:
            r4 = 1
            if (r3 != r4) goto L_0x114b
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r14 = r8.innerProgress
        L_0x1146:
            float r5 = r5 * r14
            float r4 = r4 + r5
            goto L_0x1215
        L_0x114b:
            r4 = 2
            if (r3 != r4) goto L_0x116c
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
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
            float r14 = r8.innerProgress
            goto L_0x1121
        L_0x116c:
            r4 = 3
            if (r3 != r4) goto L_0x118d
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r14 = r8.innerProgress
            goto L_0x1146
        L_0x118d:
            r4 = 4
            if (r3 != r4) goto L_0x11af
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
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
            float r14 = r8.innerProgress
            goto L_0x1121
        L_0x11af:
            r4 = 5
            if (r3 != r4) goto L_0x11d1
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r5 = (float) r5
            float r14 = r8.innerProgress
            goto L_0x1146
        L_0x11d1:
            r4 = 6
            if (r3 != r4) goto L_0x11f6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r14 = (float) r14
            float r15 = r8.innerProgress
            float r14 = r14 * r15
            float r5 = r5 - r14
            r4 = r5
            goto L_0x1215
        L_0x11f6:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r14 = r8.innerProgress
            goto L_0x1146
        L_0x1215:
            float r5 = r8.chatCallProgress
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 < 0) goto L_0x121f
            int r5 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r5 >= 0) goto L_0x122b
        L_0x121f:
            r35.save()
            float r5 = r8.chatCallProgress
            float r14 = r5 * r6
            float r5 = r5 * r6
            r12.scale(r14, r5, r2, r0)
        L_0x122b:
            android.graphics.RectF r2 = r8.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r1 - r5
            float r5 = (float) r5
            float r14 = r0 - r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r15 = r15 + r1
            float r15 = (float) r15
            float r3 = r3 + r0
            r2.set(r5, r14, r15, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r5 = (float) r5
            android.graphics.Paint r14 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r12.drawRoundRect(r2, r3, r5, r14)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r5
            float r3 = (float) r3
            float r5 = r0 - r4
            r14 = 1077936128(0x40400000, float:3.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r1 - r14
            float r14 = (float) r14
            float r0 = r0 + r4
            r2.set(r3, r5, r14, r0)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            android.graphics.Paint r14 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r12.drawRoundRect(r2, r3, r4, r14)
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r12.drawRoundRect(r0, r1, r2, r3)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x12ac
            int r0 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x12af
        L_0x12ac:
            r35.restore()
        L_0x12af:
            float r0 = r8.innerProgress
            float r1 = (float) r10
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x12cd
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x12cd
            r2 = 0
            r8.progressStage = r2
        L_0x12cd:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x12e4
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 >= 0) goto L_0x12e2
            float r1 = r1 / r21
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x12e2
            r8.chatCallProgress = r9
        L_0x12e2:
            r2 = 0
            goto L_0x12f6
        L_0x12e4:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x12f6
            float r1 = r1 / r21
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x12f6
            r8.chatCallProgress = r2
        L_0x12f6:
            r22 = 1
            goto L_0x12fd
        L_0x12f9:
            r10 = r31
            goto L_0x1038
        L_0x12fd:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1306
            r35.restore()
        L_0x1306:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x132b
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x132b
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x132b
            r35.save()
            int r0 = r34.getMeasuredWidth()
            int r1 = r34.getMeasuredHeight()
            r2 = 0
            r12.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r12)
            r35.restore()
        L_0x132b:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x138b
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x134f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x133f
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x133f
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x134f
        L_0x133f:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1348
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x1348
            goto L_0x134f
        L_0x1348:
            r0 = 1116733440(0x42900000, float:72.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1350
        L_0x134f:
            r15 = 0
        L_0x1350:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1370
            r2 = 0
            int r0 = r34.getMeasuredHeight()
            r1 = 1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r34.getMeasuredWidth()
            int r0 = r0 - r15
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            int r0 = r0 - r1
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r35
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x138b
        L_0x1370:
            float r2 = (float) r15
            int r0 = r34.getMeasuredHeight()
            r14 = 1
            int r0 = r0 - r14
            float r3 = (float) r0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            int r0 = r0 - r14
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r35
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x138c
        L_0x138b:
            r14 = 1
        L_0x138c:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x13dc
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x139d
            r35.restore()
            goto L_0x13dc
        L_0x139d:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r13, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r34.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x13dc:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x13ea
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x13e8
            goto L_0x13ea
        L_0x13e8:
            r1 = 0
            goto L_0x1413
        L_0x13ea:
            if (r0 == 0) goto L_0x13ff
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x13e8
            float r1 = (float) r10
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x13fd
            r8.reorderIconProgress = r9
        L_0x13fd:
            r1 = 0
            goto L_0x1411
        L_0x13ff:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1413
            float r2 = (float) r10
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1411
            r8.reorderIconProgress = r1
        L_0x1411:
            r22 = 1
        L_0x1413:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1441
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x146c
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x142a
            r8.archiveBackgroundProgress = r1
        L_0x142a:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x146a
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x146a
        L_0x1441:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x146c
            float r1 = (float) r10
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x1454
            r8.archiveBackgroundProgress = r9
        L_0x1454:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x146a
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x146a:
            r22 = 1
        L_0x146c:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x1481
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r10
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x147f
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x147f:
            r22 = 1
        L_0x1481:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x14af
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x1498
            float r1 = (float) r10
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x1498
            r8.currentRevealBounceProgress = r9
            r22 = 1
        L_0x1498:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x14ac
            float r1 = (float) r10
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x14cf
            r8.currentRevealProgress = r9
            goto L_0x14cf
        L_0x14ac:
            r9 = r22
            goto L_0x14d0
        L_0x14af:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x14ba
            r8.currentRevealBounceProgress = r1
            r9 = 1
            goto L_0x14bc
        L_0x14ba:
            r9 = r22
        L_0x14bc:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x14d0
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14cf
            r8.currentRevealProgress = r1
        L_0x14cf:
            r9 = 1
        L_0x14d0:
            if (r9 == 0) goto L_0x14d5
            r34.invalidate()
        L_0x14d5:
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
        if (i != NUM || (dialogsActivity = this.parentFragment) == null) {
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
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("AccActionChatPreview", NUM)));
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
            sb.append(LocaleController.getString("ArchivedChats", NUM));
            sb.append(". ");
        } else {
            if (this.encryptedChat != null) {
                sb.append(LocaleController.getString("AccDescrSecretChat", NUM));
                sb.append(". ");
            }
            TLRPC$User tLRPC$User = this.user;
            if (tLRPC$User != null) {
                if (UserObject.isReplyUser(tLRPC$User)) {
                    sb.append(LocaleController.getString("RepliesTitle", NUM));
                } else {
                    if (this.user.bot) {
                        sb.append(LocaleController.getString("Bot", NUM));
                        sb.append(". ");
                    }
                    TLRPC$User tLRPC$User2 = this.user;
                    if (tLRPC$User2.self) {
                        sb.append(LocaleController.getString("SavedMessages", NUM));
                    } else {
                        sb.append(ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name));
                    }
                }
                sb.append(". ");
            } else {
                TLRPC$Chat tLRPC$Chat = this.chat;
                if (tLRPC$Chat != null) {
                    if (tLRPC$Chat.broadcast) {
                        sb.append(LocaleController.getString("AccDescrChannel", NUM));
                    } else {
                        sb.append(LocaleController.getString("AccDescrGroup", NUM));
                    }
                    sb.append(". ");
                    sb.append(this.chat.title);
                    sb.append(". ");
                }
            }
        }
        if (this.drawVerified) {
            sb.append(LocaleController.getString("AccDescrVerified", NUM));
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
            sb.append(LocaleController.getString("AccDescrMentionReaction", NUM));
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
            sb.append(LocaleController.formatString("AccDescrSentDate", NUM, formatDateAudio));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", NUM, formatDateAudio));
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
