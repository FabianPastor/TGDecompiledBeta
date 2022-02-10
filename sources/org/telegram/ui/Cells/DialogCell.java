package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawPlay;
    private boolean drawReactionMention;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private int drawScam;
    private boolean drawVerified;
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
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
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
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.thumbImage.onAttachedToWindow();
        resetPinnedArchiveState();
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
            if (z) {
                try {
                    buildLayout();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
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

    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0660, code lost:
        if (r2.post_messages == false) goto L_0x063c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x066c, code lost:
        if (r2.kicked != false) goto L_0x063c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x17cd A[Catch:{ Exception -> 0x186e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x17ed A[Catch:{ Exception -> 0x186e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x1843 A[Catch:{ Exception -> 0x186e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x1846 A[Catch:{ Exception -> 0x186e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1032:0x1879  */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x19b1  */
    /* JADX WARNING: Removed duplicated region for block: B:1116:0x1a50 A[Catch:{ Exception -> 0x1a7b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1119:0x1a6b A[Catch:{ Exception -> 0x1a7b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1127:0x1a87  */
    /* JADX WARNING: Removed duplicated region for block: B:1144:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x039c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x061c  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x0677  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0702  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0c7b A[SYNTHETIC, Splitter:B:545:0x0c7b] */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0c9a  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0cc8  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0cfe  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0ea1  */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0eb1  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0f4e  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x1019  */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x101e  */
    /* JADX WARNING: Removed duplicated region for block: B:711:0x1025  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x1028  */
    /* JADX WARNING: Removed duplicated region for block: B:741:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x1103  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x1109  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x111a  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x1176  */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x11b7  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x11c3  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x11d3  */
    /* JADX WARNING: Removed duplicated region for block: B:796:0x11eb  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x11fb  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x1234  */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x125d  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x12e1  */
    /* JADX WARNING: Removed duplicated region for block: B:834:0x12f7  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x136c A[Catch:{ Exception -> 0x138b }] */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x136f A[Catch:{ Exception -> 0x138b }] */
    /* JADX WARNING: Removed duplicated region for block: B:865:0x1399  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x1449  */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x14fd  */
    /* JADX WARNING: Removed duplicated region for block: B:883:0x1522  */
    /* JADX WARNING: Removed duplicated region for block: B:888:0x1552  */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x16c1  */
    /* JADX WARNING: Removed duplicated region for block: B:985:0x177c  */
    /* JADX WARNING: Removed duplicated region for block: B:986:0x1785  */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x17ab A[Catch:{ Exception -> 0x186e }] */
    /* JADX WARNING: Removed duplicated region for block: B:997:0x17b4 A[ADDED_TO_REGION, Catch:{ Exception -> 0x186e }] */
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
            r1.drawNameGroup = r7
            r1.drawNameBroadcast = r7
            r1.drawNameLock = r7
            r1.drawNameBot = r7
            r1.drawVerified = r7
            r1.drawScam = r7
            r1.drawPinBackground = r7
            r1.hasMessageThumb = r7
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 != 0) goto L_0x00de
            boolean r11 = r1.useMeForMyMessages
            if (r11 != 0) goto L_0x00de
            r11 = 1
            goto L_0x00df
        L_0x00de:
            r11 = 0
        L_0x00df:
            r12 = -1
            r1.printingStringType = r12
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x00f8
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00ee
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x00f2
        L_0x00ee:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x00f5
        L_0x00f2:
            java.lang.String r13 = "%2$s: ⁨%1$s⁩"
            goto L_0x0106
        L_0x00f5:
            java.lang.String r13 = "⁨%s⁩"
            goto L_0x010a
        L_0x00f8:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x0100
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x0104
        L_0x0100:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0108
        L_0x0104:
            java.lang.String r13 = "%2$s: %1$s"
        L_0x0106:
            r14 = 1
            goto L_0x010b
        L_0x0108:
            java.lang.String r13 = "%1$s"
        L_0x010a:
            r14 = 0
        L_0x010b:
            org.telegram.messenger.MessageObject r15 = r1.message
            if (r15 == 0) goto L_0x0112
            java.lang.CharSequence r15 = r15.messageText
            goto L_0x0113
        L_0x0112:
            r15 = 0
        L_0x0113:
            boolean r12 = r15 instanceof android.text.Spannable
            if (r12 == 0) goto L_0x014f
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r15)
            int r15 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderlineBold> r5 = org.telegram.ui.Components.URLSpanNoUnderlineBold.class
            java.lang.Object[] r5 = r12.getSpans(r7, r15, r5)
            org.telegram.ui.Components.URLSpanNoUnderlineBold[] r5 = (org.telegram.ui.Components.URLSpanNoUnderlineBold[]) r5
            int r15 = r5.length
            r2 = 0
        L_0x012a:
            if (r2 >= r15) goto L_0x0136
            r3 = r5[r2]
            r12.removeSpan(r3)
            int r2 = r2 + 1
            r3 = 1099431936(0x41880000, float:17.0)
            goto L_0x012a
        L_0x0136:
            int r2 = r12.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderline> r3 = org.telegram.ui.Components.URLSpanNoUnderline.class
            java.lang.Object[] r2 = r12.getSpans(r7, r2, r3)
            org.telegram.ui.Components.URLSpanNoUnderline[] r2 = (org.telegram.ui.Components.URLSpanNoUnderline[]) r2
            int r3 = r2.length
            r5 = 0
        L_0x0144:
            if (r5 >= r3) goto L_0x014e
            r15 = r2[r5]
            r12.removeSpan(r15)
            int r5 = r5 + 1
            goto L_0x0144
        L_0x014e:
            r15 = r12
        L_0x014f:
            r1.lastMessageString = r15
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            r3 = 1117782016(0x42a00000, float:80.0)
            r5 = 1118044160(0x42a40000, float:82.0)
            r12 = 33
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 1102053376(0x41b00000, float:22.0)
            r22 = 1117257728(0x42980000, float:76.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            java.lang.String r9 = ""
            r4 = 2
            r24 = 1099956224(0x41900000, float:18.0)
            if (r2 == 0) goto L_0x039c
            int r0 = r2.type
            if (r0 != r4) goto L_0x01ed
            r1.drawNameLock = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x01b2
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0177
            goto L_0x01b2
        L_0x0177:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0198
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02be
        L_0x0198:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x01b2:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01d3
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02be
        L_0x01d3:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x01ed:
            boolean r2 = r2.verified
            r1.drawVerified = r2
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x0292
            if (r0 != r6) goto L_0x0292
            r1.drawNameGroup = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x024b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0202
            goto L_0x024b
        L_0x0202:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x022a
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x021f
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0221
        L_0x021f:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0221:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02be
        L_0x022a:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r2
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x023a
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x023c
        L_0x023a:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x023c:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x024b:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0272
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0268
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x026a
        L_0x0268:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x026a:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02be
        L_0x0272:
            int r0 = r40.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r2
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0282
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0284
        L_0x0282:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0284:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x0292:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x02ad
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x029b
            goto L_0x02ad
        L_0x029b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x02a6
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x02a6:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x02ad:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x02b8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x02be
        L_0x02b8:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
        L_0x02be:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r2 = r0.type
            if (r2 != r6) goto L_0x034a
            r0 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            boolean r3 = r2.isMedia
            if (r3 == 0) goto L_0x02fa
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r2[r3]
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
            r2[r7] = r3
            java.lang.String r2 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.String r11 = "chats_attachMessage"
            r3.<init>(r11, r5)
            int r5 = r2.length()
            r2.setSpan(r3, r7, r5, r12)
            goto L_0x0336
        L_0x02fa:
            java.lang.String r2 = r2.message
            int r3 = r2.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x0308
            java.lang.String r2 = r2.substring(r7, r5)
        L_0x0308:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0328
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0311
            goto L_0x0328
        L_0x0311:
            java.lang.Object[] r3 = new java.lang.Object[r4]
            r5 = 10
            r11 = 32
            java.lang.String r2 = r2.replace(r5, r11)
            r3[r7] = r2
            r3[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r3)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x0336
        L_0x0328:
            java.lang.Object[] r3 = new java.lang.Object[r4]
            r3[r7] = r2
            r3[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r3)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x0336:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r7)
            r3 = 0
            goto L_0x0358
        L_0x034a:
            java.lang.String r2 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0356
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r0[r3]
        L_0x0356:
            r0 = 0
            r3 = 1
        L_0x0358:
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.date
            long r11 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            int r11 = r11.unread_count
            if (r11 == 0) goto L_0x0378
            r1.drawCount = r6
            java.lang.Object[] r12 = new java.lang.Object[r6]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r12[r7] = r11
            java.lang.String r11 = "%d"
            java.lang.String r11 = java.lang.String.format(r11, r12)
            goto L_0x037b
        L_0x0378:
            r1.drawCount = r7
            r11 = 0
        L_0x037b:
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            boolean r13 = r12.sent
            if (r13 == 0) goto L_0x0386
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            goto L_0x038a
        L_0x0386:
            r1.drawCheck1 = r7
            r1.drawCheck2 = r7
        L_0x038a:
            r1.drawClock = r7
            r1.drawError = r7
            java.lang.String r12 = r12.name
            r7 = r0
            r26 = r8
            r13 = r11
            r4 = r12
            r0 = 1
            r11 = -1
            r14 = 0
            r8 = r5
            r5 = 0
            goto L_0x1174
        L_0x039c:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x03b7
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x03a5
            goto L_0x03b7
        L_0x03a5:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03b0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x03c8
        L_0x03b0:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x03c8
        L_0x03b7:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03c2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r2
            goto L_0x03c8
        L_0x03c2:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
        L_0x03c8:
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 == 0) goto L_0x0451
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x060a
            r1.drawNameLock = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0416
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x03db
            goto L_0x0416
        L_0x03db:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03fc
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060a
        L_0x03fc:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x060a
        L_0x0416:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0437
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060a
        L_0x0437:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x060a
        L_0x0451:
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x060a
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x0561
            boolean r12 = r2.scam
            if (r12 == 0) goto L_0x0465
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0475
        L_0x0465:
            boolean r12 = r2.fake
            if (r12 == 0) goto L_0x0471
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x0475
        L_0x0471:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0475:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x060a
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x04f2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0483
            goto L_0x04f2
        L_0x0483:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            r26 = r8
            long r7 = r2.id
            r27 = 0
            int r5 = (r7 > r27 ? 1 : (r7 == r27 ? 0 : -1))
            if (r5 < 0) goto L_0x04a7
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x049c
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x049c
            goto L_0x04a7
        L_0x049c:
            r1.drawNameGroup = r6
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04b1
        L_0x04a7:
            r1.drawNameBroadcast = r6
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04b1:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x04d1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x04c6
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04c8
        L_0x04c6:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04c8:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060c
        L_0x04d1:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x04e1
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e3
        L_0x04e1:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e3:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x060c
        L_0x04f2:
            r26 = r8
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            long r7 = r2.id
            r27 = 0
            int r3 = (r7 > r27 ? 1 : (r7 == r27 ? 0 : -1))
            if (r3 < 0) goto L_0x0516
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x050b
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x050b
            goto L_0x0516
        L_0x050b:
            r1.drawNameGroup = r6
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x0520
        L_0x0516:
            r1.drawNameBroadcast = r6
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x0520:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0540
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x0535
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0537
        L_0x0535:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0537:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060c
        L_0x0540:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x0550
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0552
        L_0x0550:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0552:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x060c
        L_0x0561:
            r26 = r8
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x060c
            boolean r7 = r2.scam
            if (r7 == 0) goto L_0x0573
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0583
        L_0x0573:
            boolean r7 = r2.fake
            if (r7 == 0) goto L_0x057f
            r1.drawScam = r4
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x0583
        L_0x057f:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0583:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x060c
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x060c
            r1.drawNameBot = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x05d1
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0598
            goto L_0x05d1
        L_0x0598:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x05b8
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060c
        L_0x05b8:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x060c
        L_0x05d1:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x05f1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x060c
        L_0x05f1:
            int r2 = r40.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x060c
        L_0x060a:
            r26 = r8
        L_0x060c:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x0618
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0618
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            int r2 = r2.date
        L_0x0618:
            boolean r3 = r1.isDialogCell
            if (r3 == 0) goto L_0x0672
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            long r7 = r1.currentDialogId
            r5 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r3.getDraft(r7, r5)
            r1.draftMessage = r3
            if (r3 == 0) goto L_0x0648
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x063e
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.reply_to_msg_id
            if (r3 == 0) goto L_0x063c
            goto L_0x063e
        L_0x063c:
            r2 = 0
            goto L_0x066f
        L_0x063e:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.date
            if (r2 <= r3) goto L_0x0648
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x063c
        L_0x0648:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0662
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x0662
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x0662
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x063c
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x063c
        L_0x0662:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x0675
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x063c
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x0675
            goto L_0x063c
        L_0x066f:
            r1.draftMessage = r2
            goto L_0x0675
        L_0x0672:
            r2 = 0
            r1.draftMessage = r2
        L_0x0675:
            if (r0 == 0) goto L_0x0702
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r7 = r1.currentDialogId
            r3 = 0
            java.lang.Integer r2 = r2.getPrintingStringType(r7, r3)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x069e
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x069f
        L_0x069e:
            r2 = 0
        L_0x069f:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>()
            java.lang.String[] r5 = new java.lang.String[r6]
            java.lang.String r7 = "..."
            r8 = 0
            r5[r8] = r7
            java.lang.String[] r7 = new java.lang.String[r6]
            r7[r8] = r9
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r5, r7)
            int r5 = r1.printingStringType
            r7 = 5
            if (r5 != r7) goto L_0x06c3
            java.lang.String r5 = r0.toString()
            java.lang.String r7 = "**oo**"
            int r5 = r5.indexOf(r7)
            goto L_0x06c4
        L_0x06c3:
            r5 = -1
        L_0x06c4:
            if (r5 < 0) goto L_0x06e0
            android.text.SpannableStringBuilder r0 = r3.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r7 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r7 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r7)
            int r7 = r7.getIntrinsicWidth()
            r2.<init>(r7)
            int r7 = r5 + 6
            r8 = 0
            r0.setSpan(r2, r5, r7, r8)
            goto L_0x06f3
        L_0x06e0:
            r8 = 0
            java.lang.String r7 = " "
            android.text.SpannableStringBuilder r7 = r3.append(r7)
            android.text.SpannableStringBuilder r0 = r7.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r7 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r7.<init>(r2)
            r0.setSpan(r7, r8, r6, r8)
        L_0x06f3:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r16 = r5
            r0 = 1
            r2 = 2
            r4 = 0
            r5 = 0
            r7 = 0
            goto L_0x0f5c
        L_0x0702:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            r2 = 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x07a3
            r0 = 2131625336(0x7f0e0578, float:1.8877877E38)
            java.lang.String r3 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0748
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x073f
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0727
            goto L_0x073f
        L_0x0727:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.String r7 = "chats_draft"
            r2.<init>(r7, r5)
            int r5 = r0.length()
            r7 = 33
            r12 = 0
            r3.setSpan(r2, r12, r5, r7)
            goto L_0x07a1
        L_0x073f:
            r12 = 0
            r7 = r0
            r3 = r9
        L_0x0742:
            r0 = 1
            r2 = 2
            r4 = 0
            r5 = 0
            goto L_0x07bb
        L_0x0748:
            r12 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r5 = r3.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r5 <= r7) goto L_0x0759
            java.lang.String r3 = r3.substring(r12, r7)
        L_0x0759:
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r3)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC$DraftMessage) r3, (android.text.Spannable) r5, (int) r2)
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r4]
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r5)
            r2[r12] = r3
            r2[r6] = r0
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r2)
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x078e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x078e
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.String r7 = "chats_draft"
            r3.<init>(r7, r5)
            int r5 = r0.length()
            int r5 = r5 + r6
            r7 = 33
            r8 = 0
            r2.setSpan(r3, r8, r5, r7)
            goto L_0x078f
        L_0x078e:
            r8 = 0
        L_0x078f:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r8)
        L_0x07a1:
            r7 = r0
            goto L_0x0742
        L_0x07a3:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x07bf
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r0 = 2131625916(0x7f0e07bc, float:1.8879053E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x07b6:
            r0 = 1
            r2 = 2
            r4 = 1
            r5 = 0
            r7 = 0
        L_0x07bb:
            r16 = -1
            goto L_0x0f5c
        L_0x07bf:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x0856
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x0835
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x07db
            r0 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07b6
        L_0x07db:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x07f4
            r0 = 2131624547(0x7f0e0263, float:1.8876277E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r5 = 0
            r2[r5] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x07b6
        L_0x07f4:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x0802
            r0 = 2131625437(0x7f0e05dd, float:1.8878082E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07b6
        L_0x0802:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x0853
            long r2 = r0.admin_id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r7 = r0.getClientUserId()
            int r0 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x082b
            r0 = 2131625425(0x7f0e05d1, float:1.8878058E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r5 = 0
            r2[r5] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x07b6
        L_0x082b:
            r0 = 2131625424(0x7f0e05d0, float:1.8878056E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07b6
        L_0x0835:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x0853
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x0853
            r0 = 2131627678(0x7f0e0e9e, float:1.8882627E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r4 = 1
            r5 = 0
            r7 = 0
            r11 = 0
            goto L_0x07bb
        L_0x0853:
            r3 = r9
            goto L_0x07b6
        L_0x0856:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r3 = r1.message
            long r7 = r3.getFromChatId()
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r3 == 0) goto L_0x087a
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
            r5 = 0
            goto L_0x088b
        L_0x087a:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r7 = -r7
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r5)
            r5 = r3
            r3 = 0
        L_0x088b:
            int r7 = r1.dialogsType
            r8 = 3
            if (r7 != r8) goto L_0x08aa
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r7 == 0) goto L_0x08aa
            r0 = 2131627678(0x7f0e0e9e, float:1.8882627E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 1
            r5 = 0
            r7 = 0
            r11 = 0
            goto L_0x0var_
        L_0x08aa:
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x08c2
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 != 0) goto L_0x08c2
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x08c2
            java.lang.CharSequence r0 = r40.formatArchivedDialogNames()
            r3 = r0
            r0 = 1
            r2 = 2
        L_0x08bd:
            r4 = 0
        L_0x08be:
            r5 = 0
            r7 = 0
            goto L_0x0var_
        L_0x08c2:
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x08ed
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 == 0) goto L_0x08e2
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x08e0
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x08e2
        L_0x08e0:
            r15 = r9
            r11 = 0
        L_0x08e2:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r3 = r15
            r0 = 1
            r2 = 2
        L_0x08eb:
            r4 = 1
            goto L_0x08be
        L_0x08ed:
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 == 0) goto L_0x09ee
            int r7 = r1.currentDialogFolderId
            if (r7 != 0) goto L_0x09ee
            org.telegram.tgnet.TLRPC$EncryptedChat r7 = r1.encryptedChat
            if (r7 != 0) goto L_0x09ee
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.needDrawBluredPreview()
            if (r7 != 0) goto L_0x09ee
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isPhoto()
            if (r7 != 0) goto L_0x091b
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isNewGif()
            if (r7 != 0) goto L_0x091b
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isVideo()
            if (r7 == 0) goto L_0x09ee
        L_0x091b:
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isWebpage()
            if (r7 == 0) goto L_0x092e
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
            java.lang.String r7 = r7.type
            goto L_0x092f
        L_0x092e:
            r7 = 0
        L_0x092f:
            java.lang.String r8 = "app"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x09ee
            java.lang.String r8 = "profile"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x09ee
            java.lang.String r8 = "article"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x09ee
            if (r7 == 0) goto L_0x0951
            java.lang.String r8 = "telegram_"
            boolean r7 = r7.startsWith(r8)
            if (r7 != 0) goto L_0x09ee
        L_0x0951:
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            int r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r12)
            if (r7 != r8) goto L_0x096a
            r8 = 0
        L_0x096a:
            if (r7 == 0) goto L_0x09ee
            r1.hasMessageThumb = r6
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            r1.drawPlay = r12
            java.lang.String r12 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.mediaExists
            if (r2 != 0) goto L_0x09b9
            int r2 = r1.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r2 = r2.canDownloadMedia((org.telegram.messenger.MessageObject) r4)
            if (r2 != 0) goto L_0x09b9
            int r2 = r1.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r12)
            if (r2 == 0) goto L_0x099b
            goto L_0x09b9
        L_0x099b:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage
            r30 = 0
            r31 = 0
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLObject r4 = r4.photoThumbsObject
            org.telegram.messenger.ImageLocation r32 = org.telegram.messenger.ImageLocation.getForObject(r7, r4)
            r34 = 0
            org.telegram.messenger.MessageObject r4 = r1.message
            r36 = 0
            java.lang.String r33 = "20_20"
            r29 = r2
            r35 = r4
            r29.setImage((org.telegram.messenger.ImageLocation) r30, (java.lang.String) r31, (org.telegram.messenger.ImageLocation) r32, (java.lang.String) r33, (android.graphics.drawable.Drawable) r34, (java.lang.Object) r35, (int) r36)
            goto L_0x09ec
        L_0x09b9:
            org.telegram.messenger.MessageObject r2 = r1.message
            int r4 = r2.type
            if (r4 != r6) goto L_0x09c9
            if (r8 == 0) goto L_0x09c5
            int r4 = r8.size
            r12 = r4
            goto L_0x09c6
        L_0x09c5:
            r12 = 0
        L_0x09c6:
            r34 = r12
            goto L_0x09cb
        L_0x09c9:
            r34 = 0
        L_0x09cb:
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForObject(r8, r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r32 = org.telegram.messenger.ImageLocation.getForObject(r7, r2)
            r35 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r37 = 0
            java.lang.String r31 = "20_20"
            java.lang.String r33 = "20_20"
            r29 = r4
            r36 = r2
            r29.setImage(r30, r31, r32, r33, r34, r35, r36, r37)
        L_0x09ec:
            r2 = 0
            goto L_0x09ef
        L_0x09ee:
            r2 = 1
        L_0x09ef:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x0cf5
            long r7 = r4.id
            r29 = 0
            int r12 = (r7 > r29 ? 1 : (r7 == r29 ? 0 : -1))
            if (r12 <= 0) goto L_0x0cf5
            if (r5 != 0) goto L_0x0cf5
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x0a0b
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isMegagroup(r4)
            if (r4 == 0) goto L_0x0cf5
        L_0x0a0b:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isOutOwner()
            if (r4 == 0) goto L_0x0a1d
            r3 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0a64
        L_0x0a1d:
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x0a2d
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from
            if (r4 == 0) goto L_0x0a2d
            java.lang.String r4 = r4.from_name
            if (r4 == 0) goto L_0x0a2d
            r3 = r4
            goto L_0x0a64
        L_0x0a2d:
            if (r3 == 0) goto L_0x0a62
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0a43
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0a38
            goto L_0x0a43
        L_0x0a38:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x0a64
        L_0x0a43:
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r4 == 0) goto L_0x0a53
            r3 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0a64
        L_0x0a53:
            java.lang.String r4 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r4, r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x0a64
        L_0x0a62:
            java.lang.String r3 = "DELETED"
        L_0x0a64:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x0a7d
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r4 = 0
            r2[r4] = r0
            r2[r6] = r3
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0a7a:
            r2 = r0
            goto L_0x0CLASSNAME
        L_0x0a7d:
            r4 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0af2
            java.lang.String r0 = r5.toString()
            int r5 = r0.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r5 <= r7) goto L_0x0a94
            java.lang.CharSequence r0 = r0.subSequence(r4, r7)
        L_0x0a94:
            if (r2 != 0) goto L_0x0a98
            r2 = r9
            goto L_0x0ac6
        L_0x0a98:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0aa3
            java.lang.String r2 = "📹 "
            goto L_0x0ac6
        L_0x0aa3:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0aae
            java.lang.String r2 = "🎤 "
            goto L_0x0ac6
        L_0x0aae:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0ab9
            java.lang.String r2 = "🎧 "
            goto L_0x0ac6
        L_0x0ab9:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x0ac4
            java.lang.String r2 = "🖼 "
            goto L_0x0ac6
        L_0x0ac4:
            java.lang.String r2 = "📎 "
        L_0x0ac6:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            java.lang.CharSequence r0 = r0.caption
            r7 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r5, r0, r4, r7)
            r5 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r5]
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            android.text.SpannableStringBuilder r2 = r5.append(r2)
            r4 = 0
            r0[r4] = r2
            r0[r6] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r0)
            goto L_0x0a7a
        L_0x0af2:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0be3
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0be3
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0b38
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b27
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bab
        L_0x0b27:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r4] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bab
        L_0x0b38:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0b62
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b52
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bab
        L_0x0b52:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r4] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0bab
        L_0x0b62:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0b69
            java.lang.String r0 = r2.title
            goto L_0x0bab
        L_0x0b69:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0ba7
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r2 < r4) goto L_0x0b8e
            r2 = 2
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r5 = 0
            r4[r5] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r6] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0bab
        L_0x0b8e:
            r2 = 2
            r5 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r4[r5] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r6] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0bab
        L_0x0ba7:
            java.lang.String r0 = r15.toString()
        L_0x0bab:
            r2 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r2, r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bdd }
            java.lang.String r4 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0bdd }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0bdd }
            if (r14 == 0) goto L_0x0bd1
            int r4 = r3.length()     // Catch:{ Exception -> 0x0bdd }
            r5 = 2
            int r4 = r4 + r5
            goto L_0x0bd2
        L_0x0bd1:
            r4 = 0
        L_0x0bd2:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0bdd }
            r7 = 33
            r2.setSpan(r0, r4, r5, r7)     // Catch:{ Exception -> 0x0bdd }
            goto L_0x0CLASSNAME
        L_0x0bdd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0CLASSNAME
        L_0x0be3:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0CLASSNAME
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0bf8
            r2 = r0
        L_0x0bf8:
            int r0 = r40.getMeasuredWidth()
            r4 = 1121058816(0x42d20000, float:105.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            if (r14 == 0) goto L_0x0c1f
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0CLASSNAME
            float r0 = (float) r0
            java.lang.String r4 = r3.toString()
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0CLASSNAME:
            float r0 = (float) r0
            java.lang.String r4 = ": "
            float r4 = r10.measureText(r4)
            float r0 = r0 - r4
            int r0 = (int) r0
        L_0x0c1f:
            if (r0 <= 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r0, r10, r7)
            java.lang.String r2 = r0.toString()
            goto L_0x0c4a
        L_0x0CLASSNAME:
            r5 = 0
            goto L_0x0c4a
        L_0x0CLASSNAME:
            r5 = 0
            int r0 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r0 <= r4) goto L_0x0CLASSNAME
            java.lang.CharSequence r2 = r2.subSequence(r5, r4)
        L_0x0CLASSNAME:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r2)
        L_0x0c4a:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r2, (android.text.Spannable) r0, (int) r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r4[r5] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            goto L_0x0a7a
        L_0x0CLASSNAME:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r9)
            goto L_0x0a7a
        L_0x0CLASSNAME:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0c7b
        L_0x0CLASSNAME:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0c9a
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0c9a
        L_0x0c7b:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r4 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0CLASSNAME }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            int r4 = r4 + r6
            r5 = 33
            r7 = 0
            r2.setSpan(r0, r7, r4, r5)     // Catch:{ Exception -> 0x0CLASSNAME }
            r0 = r4
            goto L_0x0c9c
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r4 = 0
        L_0x0CLASSNAME:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0c9c
        L_0x0c9a:
            r0 = 0
            r4 = 0
        L_0x0c9c:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r8 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r7, r8)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0cc4
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r5 == 0) goto L_0x0cc4
            r2 = r5
        L_0x0cc4:
            boolean r5 = r1.hasMessageThumb
            if (r5 == 0) goto L_0x0ced
            boolean r5 = r2 instanceof android.text.SpannableStringBuilder
            if (r5 != 0) goto L_0x0cd2
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            r2 = r5
        L_0x0cd2:
            r5 = r2
            android.text.SpannableStringBuilder r5 = (android.text.SpannableStringBuilder) r5
            java.lang.String r7 = " "
            r5.insert(r4, r7)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r7 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r26 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.<init>(r8)
            int r8 = r4 + 1
            r13 = 33
            r5.setSpan(r7, r4, r8, r13)
        L_0x0ced:
            r5 = r0
            r7 = r3
            r0 = 1
            r4 = 0
            r3 = r2
            r2 = 2
            goto L_0x0var_
        L_0x0cf5:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0cfe
        L_0x0cfb:
            r2 = 2
            goto L_0x0ead
        L_0x0cfe:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0d1c
            org.telegram.tgnet.TLRPC$Photo r4 = r3.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0d1c
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d1c
            r0 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0cfb
        L_0x0d1c:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0d34
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0d34
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d34
            r0 = 2131624430(0x7f0e01ee, float:1.887604E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0cfb
        L_0x0d34:
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0de8
            if (r2 != 0) goto L_0x0d3c
            r0 = r9
            goto L_0x0d68
        L_0x0d3c:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0d45
            java.lang.String r0 = "📹 "
            goto L_0x0d68
        L_0x0d45:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0d50
            java.lang.String r0 = "🎤 "
            goto L_0x0d68
        L_0x0d50:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0d5b
            java.lang.String r0 = "🎧 "
            goto L_0x0d68
        L_0x0d5b:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0d66
            java.lang.String r0 = "🖼 "
            goto L_0x0d68
        L_0x0d66:
            java.lang.String r0 = "📎 "
        L_0x0d68:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0dc7
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0dc7
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            r4 = 1122893824(0x42ee0000, float:119.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r14 == 0) goto L_0x0d9f
            r4 = 0
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x0d9e
            float r3 = (float) r3
            java.lang.String r5 = ": "
            float r5 = r10.measureText(r5)
            float r3 = r3 - r5
            int r3 = (int) r3
            goto L_0x0d9f
        L_0x0d9e:
            throw r4
        L_0x0d9f:
            if (r3 <= 0) goto L_0x0db6
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r5 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r3, r10, r5)
            java.lang.String r2 = r2.toString()
        L_0x0db6:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0cfb
        L_0x0dc7:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.caption
            r2.<init>(r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.entities
            java.lang.CharSequence r3 = r3.caption
            r5 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r4, r3, r2, r5)
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            android.text.SpannableStringBuilder r0 = r3.append(r2)
            goto L_0x0cfb
        L_0x0de8:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0e06
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0e03:
            r2 = 2
            goto L_0x0e99
        L_0x0e06:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0e26
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
            goto L_0x0e03
        L_0x0e26:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0e2d
            java.lang.String r0 = r3.title
            goto L_0x0e03
        L_0x0e2d:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0e4c
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
            goto L_0x0e99
        L_0x0e4c:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0e84
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e84
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
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
            goto L_0x0e90
        L_0x0e84:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r3, (android.text.Spannable) r0, (int) r4)
        L_0x0e90:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
        L_0x0e99:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            if (r4 == 0) goto L_0x0ead
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0ead
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r10 = r3[r4]
        L_0x0ead:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0f4e
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0eed
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0eed
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            int r8 = r26 + 95
            int r8 = r8 + 6
            float r4 = (float) r8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r4, r3, r10, r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0efe
        L_0x0eed:
            r5 = 0
            int r3 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0efa
            java.lang.CharSequence r0 = r0.subSequence(r5, r4)
        L_0x0efa:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0efe:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0var_
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0var_:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r4 = " "
            r5 = 0
            r3.insert(r5, r4)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r4 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r26 + 6
            float r7 = (float) r8
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.<init>(r7)
            r7 = 33
            r3.setSpan(r4, r5, r6, r7)
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r4 = r4[r7]
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r7 = 1099431936(0x41880000, float:17.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r8, r5)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0f4a
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x0f4a
            goto L_0x0f4b
        L_0x0f4a:
            r3 = r0
        L_0x0f4b:
            r0 = 1
            goto L_0x08bd
        L_0x0f4e:
            r3 = r0
            r0 = 1
            goto L_0x08eb
        L_0x0var_:
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x07bb
            java.lang.CharSequence r7 = r40.formatArchivedDialogNames()
            goto L_0x07bb
        L_0x0f5c:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            if (r8 == 0) goto L_0x0var_
            int r8 = r8.date
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            int r8 = r1.lastMessageDate
            if (r8 == 0) goto L_0x0var_
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.date
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            r8 = r9
        L_0x0var_:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x0var_
            r12 = 0
            r1.drawCheck1 = r12
            r1.drawCheck2 = r12
            r1.drawClock = r12
            r1.drawCount = r12
            r1.drawMention = r12
            r1.drawReactionMention = r12
            r1.drawError = r12
            r11 = 0
            r13 = 0
            r14 = 0
            goto L_0x109f
        L_0x0var_:
            r12 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0fdb
            int r13 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r13 + r14
            if (r15 <= 0) goto L_0x0fd2
            if (r13 <= r14) goto L_0x0fbc
            r1.drawCount = r6
            r1.drawMention = r12
            java.lang.Object[] r15 = new java.lang.Object[r6]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15[r12] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r15)
            goto L_0x0fd7
        L_0x0fbc:
            r1.drawCount = r12
            r1.drawMention = r6
            java.lang.Object[] r15 = new java.lang.Object[r6]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15[r12] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r15)
            r14 = r13
            r13 = 0
            goto L_0x0fd8
        L_0x0fd2:
            r1.drawCount = r12
            r1.drawMention = r12
            r13 = 0
        L_0x0fd7:
            r14 = 0
        L_0x0fd8:
            r1.drawReactionMention = r12
            goto L_0x102a
        L_0x0fdb:
            boolean r14 = r1.clearingDialog
            if (r14 == 0) goto L_0x0fe5
            r1.drawCount = r12
            r11 = 0
            r12 = 0
        L_0x0fe3:
            r13 = 0
            goto L_0x1015
        L_0x0fe5:
            int r14 = r1.unreadCount
            if (r14 == 0) goto L_0x1009
            if (r14 != r6) goto L_0x0ff7
            int r15 = r1.mentionCount
            if (r14 != r15) goto L_0x0ff7
            if (r13 == 0) goto L_0x0ff7
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x1009
        L_0x0ff7:
            r1.drawCount = r6
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r12 = 0
            r13[r12] = r14
            java.lang.String r14 = "%d"
            java.lang.String r13 = java.lang.String.format(r14, r13)
            goto L_0x1015
        L_0x1009:
            r12 = 0
            boolean r13 = r1.markUnread
            if (r13 == 0) goto L_0x1012
            r1.drawCount = r6
            r13 = r9
            goto L_0x1015
        L_0x1012:
            r1.drawCount = r12
            goto L_0x0fe3
        L_0x1015:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x101e
            r1.drawMention = r6
            java.lang.String r14 = "@"
            goto L_0x1021
        L_0x101e:
            r1.drawMention = r12
            r14 = 0
        L_0x1021:
            int r15 = r1.reactionMentionCount
            if (r15 == 0) goto L_0x1028
            r1.drawReactionMention = r6
            goto L_0x102a
        L_0x1028:
            r1.drawReactionMention = r12
        L_0x102a:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x1096
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x1096
            if (r11 == 0) goto L_0x1096
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x1096
            boolean r11 = r11.isSending()
            if (r11 == 0) goto L_0x1052
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r6
            r1.drawError = r11
            goto L_0x109f
        L_0x1052:
            r11 = 0
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isSendError()
            if (r12 == 0) goto L_0x1068
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r6
            r1.drawCount = r11
            r1.drawMention = r11
            goto L_0x109f
        L_0x1068:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isSent()
            if (r11 == 0) goto L_0x1094
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isUnread()
            if (r11 == 0) goto L_0x1089
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x1087
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x1087
            goto L_0x1089
        L_0x1087:
            r11 = 0
            goto L_0x108a
        L_0x1089:
            r11 = 1
        L_0x108a:
            r1.drawCheck1 = r11
            r1.drawCheck2 = r6
            r11 = 0
            r1.drawClock = r11
            r1.drawError = r11
            goto L_0x109f
        L_0x1094:
            r11 = 0
            goto L_0x109f
        L_0x1096:
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r11
        L_0x109f:
            r1.promoDialog = r11
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r15 = r1.dialogsType
            if (r15 != 0) goto L_0x1103
            r15 = r3
            long r2 = r1.currentDialogId
            boolean r2 = r11.isPromoDialog(r2, r6)
            if (r2 == 0) goto L_0x1104
            r1.drawPinBackground = r6
            r1.promoDialog = r6
            int r2 = r11.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x10c9
            r2 = 2131628347(0x7f0e113b, float:1.8883984E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x10c7:
            r8 = r2
            goto L_0x1104
        L_0x10c9:
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x1104
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r11.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x10f3
            r2 = 2131627407(0x7f0e0d8f, float:1.8882078E38)
            java.lang.String r3 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x10f3:
            java.lang.String r3 = r11.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x10c7
            java.lang.String r3 = r11.promoPsaMessage
            r8 = 0
            r1.hasMessageThumb = r8
            r8 = r2
            r2 = r3
            goto L_0x1105
        L_0x1103:
            r15 = r3
        L_0x1104:
            r2 = r15
        L_0x1105:
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x111a
            r3 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r11 = "ArchivedChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
        L_0x1112:
            r11 = r16
            r39 = r4
            r4 = r3
            r3 = r39
            goto L_0x1174
        L_0x111a:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x1121
            java.lang.String r3 = r3.title
            goto L_0x1164
        L_0x1121:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x1163
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x1135
            r3 = 2131627544(0x7f0e0e18, float:1.8882355E38)
            java.lang.String r11 = "RepliesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x1164
        L_0x1135:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x115c
            boolean r3 = r1.useMeForMyMessages
            if (r3 == 0) goto L_0x114b
            r3 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.String r11 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x1164
        L_0x114b:
            int r3 = r1.dialogsType
            r11 = 3
            if (r3 != r11) goto L_0x1152
            r1.drawPinBackground = r6
        L_0x1152:
            r3 = 2131627677(0x7f0e0e9d, float:1.8882625E38)
            java.lang.String r11 = "SavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x1164
        L_0x115c:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x1164
        L_0x1163:
            r3 = r9
        L_0x1164:
            int r11 = r3.length()
            if (r11 != 0) goto L_0x1112
            r3 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r11 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x1112
        L_0x1174:
            if (r0 == 0) goto L_0x11b7
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r8)
            r15 = r13
            double r12 = (double) r0
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r8
            r32 = r12
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.timeLayout = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x11ad
            int r0 = r40.getMeasuredWidth()
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            int r0 = r0 - r12
            r1.timeLeft = r0
            goto L_0x11b5
        L_0x11ad:
            r8 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeLeft = r0
        L_0x11b5:
            r0 = r12
            goto L_0x11bf
        L_0x11b7:
            r15 = r13
            r8 = 0
            r1.timeLayout = r8
            r8 = 0
            r1.timeLeft = r8
            r0 = 0
        L_0x11bf:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x11d3
            int r8 = r40.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r8 = r8 - r13
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r8 = r8 - r13
            int r8 = r8 - r0
            goto L_0x11e7
        L_0x11d3:
            int r8 = r40.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r8 = r8 - r13
            r13 = 1117388800(0x429a0000, float:77.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r8 = r8 - r13
            int r8 = r8 - r0
            int r13 = r1.nameLeft
            int r13 = r13 + r0
            r1.nameLeft = r13
        L_0x11e7:
            boolean r13 = r1.drawNameLock
            if (r13 == 0) goto L_0x11fb
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
        L_0x11f7:
            int r13 = r13 + r16
            int r8 = r8 - r13
            goto L_0x122e
        L_0x11fb:
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x120c
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x11f7
        L_0x120c:
            boolean r13 = r1.drawNameBroadcast
            if (r13 == 0) goto L_0x121d
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x11f7
        L_0x121d:
            boolean r13 = r1.drawNameBot
            if (r13 == 0) goto L_0x122e
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x11f7
        L_0x122e:
            boolean r13 = r1.drawClock
            r16 = 1084227584(0x40a00000, float:5.0)
            if (r13 == 0) goto L_0x125d
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r13 = r13 + r18
            int r8 = r8 - r13
            boolean r18 = org.telegram.messenger.LocaleController.isRTL
            if (r18 != 0) goto L_0x124c
            int r0 = r1.timeLeft
            int r0 = r0 - r13
            r1.clockDrawLeft = r0
            goto L_0x12d3
        L_0x124c:
            int r12 = r1.timeLeft
            int r12 = r12 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r12 = r12 + r0
            r1.clockDrawLeft = r12
            int r0 = r1.nameLeft
            int r0 = r0 + r13
            r1.nameLeft = r0
            goto L_0x12d3
        L_0x125d:
            boolean r12 = r1.drawCheck2
            if (r12 == 0) goto L_0x12d3
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r12 = r12.getIntrinsicWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r12 = r12 + r13
            int r8 = r8 - r12
            boolean r13 = r1.drawCheck1
            if (r13 == 0) goto L_0x12ba
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            r18 = 1090519040(0x41000000, float:8.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r13 = r13 - r18
            int r8 = r8 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x1293
            int r0 = r1.timeLeft
            int r0 = r0 - r12
            r1.halfCheckDrawLeft = r0
            r12 = 1085276160(0x40b00000, float:5.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r12
            r1.checkDrawLeft = r0
            goto L_0x12d3
        L_0x1293:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r13 = r13 + r0
            r1.checkDrawLeft = r13
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r13 = r13 + r0
            r1.halfCheckDrawLeft = r13
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            int r12 = r12 + r13
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r0 = r0 + r12
            r1.nameLeft = r0
            goto L_0x12d3
        L_0x12ba:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x12c4
            int r0 = r1.timeLeft
            int r0 = r0 - r12
            r1.checkDrawLeft1 = r0
            goto L_0x12d3
        L_0x12c4:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r13 = r13 + r0
            r1.checkDrawLeft1 = r13
            int r0 = r1.nameLeft
            int r0 = r0 + r12
            r1.nameLeft = r0
        L_0x12d3:
            boolean r0 = r1.dialogMuted
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x12f7
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x12f7
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x12f7
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x1331
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
            goto L_0x1331
        L_0x12f7:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1311
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x1331
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
            goto L_0x1331
        L_0x1311:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x1331
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r1.drawScam
            if (r12 != r6) goto L_0x1320
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1322
        L_0x1320:
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1322:
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x1331
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
        L_0x1331:
            r16 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r8 = java.lang.Math.max(r0, r8)
            r0 = 10
            r12 = 32
            java.lang.String r0 = r4.replace(r0, r12)     // Catch:{ Exception -> 0x138b }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x138b }
            int r12 = r1.paintIndex     // Catch:{ Exception -> 0x138b }
            r4 = r4[r12]     // Catch:{ Exception -> 0x138b }
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x138b }
            int r12 = r8 - r12
            float r12 = (float) r12     // Catch:{ Exception -> 0x138b }
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x138b }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r4, r12, r13)     // Catch:{ Exception -> 0x138b }
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x138b }
            if (r4 == 0) goto L_0x136f
            boolean r4 = r4.hasHighlightedWords()     // Catch:{ Exception -> 0x138b }
            if (r4 == 0) goto L_0x136f
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x138b }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x138b }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x138b }
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x138b }
            if (r4 == 0) goto L_0x136f
            r30 = r4
            goto L_0x1371
        L_0x136f:
            r30 = r0
        L_0x1371:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x138b }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x138b }
            int r12 = r1.paintIndex     // Catch:{ Exception -> 0x138b }
            r31 = r4[r12]     // Catch:{ Exception -> 0x138b }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x138b }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r32 = r8
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x138b }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x138b }
            goto L_0x138f
        L_0x138b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x138f:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1449
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1399
            goto L_0x1449
        L_0x1399:
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
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.checkDrawTop = r12
            int r4 = r40.getMeasuredWidth()
            r12 = 1119748096(0x42be0000, float:95.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r4 = r4 - r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x13fb
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r12
            r1.messageLeft = r12
            int r12 = r40.getMeasuredWidth()
            r13 = 1115684864(0x42800000, float:64.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r13 = r26 + 11
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r12 - r13
            goto L_0x1410
        L_0x13fb:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r12
            r1.messageLeft = r12
            r12 = 1092616192(0x41200000, float:10.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r13 = 1116078080(0x42860000, float:67.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r12
        L_0x1410:
            org.telegram.messenger.ImageReceiver r6 = r1.avatarImage
            float r12 = (float) r12
            r17 = r4
            float r4 = (float) r0
            r21 = 1113063424(0x42580000, float:54.0)
            r22 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r9 = (float) r9
            r25 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r11 = (float) r11
            r6.setImageCoords(r12, r4, r9, r11)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r13
            r9 = 1106247680(0x41var_, float:30.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            r11 = r26
            float r12 = (float) r11
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r4.setImageCoords(r6, r9, r13, r12)
            r4 = r0
            r23 = r5
            goto L_0x14f9
        L_0x1449:
            r22 = r9
            r25 = r11
            r11 = r26
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
            if (r6 == 0) goto L_0x14b2
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r40.getMeasuredWidth()
            r9 = 1115947008(0x42840000, float:66.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 - r9
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r6 - r9
            goto L_0x14c7
        L_0x14b2:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r6
        L_0x14c7:
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            float r6 = (float) r6
            float r13 = (float) r0
            r17 = 1113587712(0x42600000, float:56.0)
            r21 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r23 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            r12.setImageCoords(r6, r13, r4, r5)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r9
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r0
            float r6 = (float) r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r9 = (float) r9
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r12 = (float) r12
            r4.setImageCoords(r5, r6, r9, r12)
            r4 = r0
            r17 = r21
        L_0x14f9:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x151e
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1516
            int r0 = r40.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x151e
        L_0x1516:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x151e:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1552
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x153c
            int r0 = r40.getMeasuredWidth()
            r5 = 1107820544(0x42080000, float:34.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.errorLeft = r0
            goto L_0x154e
        L_0x153c:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x154e:
            r0 = r17
            goto L_0x16bf
        L_0x1552:
            if (r15 != 0) goto L_0x1582
            if (r14 != 0) goto L_0x1582
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x155b
            goto L_0x1582
        L_0x155b:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x157c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x157c
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x157c:
            r5 = 0
            r1.drawCount = r5
            r1.drawMention = r5
            goto L_0x154e
        L_0x1582:
            if (r15 == 0) goto L_0x15e4
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r13 = r15
            float r5 = r5.measureText(r13)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r13
            r32 = r5
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x15d0
            int r0 = r40.getMeasuredWidth()
            int r5 = r1.countWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r5
            r1.countLeft = r0
            goto L_0x15e0
        L_0x15d0:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x15e0:
            r5 = 1
            r1.drawCount = r5
            goto L_0x15e7
        L_0x15e4:
            r5 = 0
            r1.countWidth = r5
        L_0x15e7:
            if (r14 == 0) goto L_0x166a
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x161d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r14)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.mentionWidth
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r14
            r32 = r5
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.mentionLayout = r0
            goto L_0x1623
        L_0x161d:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.mentionWidth = r0
        L_0x1623:
            int r0 = r1.mentionWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x164b
            int r0 = r40.getMeasuredWidth()
            int r5 = r1.mentionWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x1646
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x1647
        L_0x1646:
            r5 = 0
        L_0x1647:
            int r0 = r0 - r5
            r1.mentionLeft = r0
            goto L_0x1667
        L_0x164b:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x1659
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r6 + r9
            goto L_0x165a
        L_0x1659:
            r6 = 0
        L_0x165a:
            int r5 = r5 + r6
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x1667:
            r5 = 1
            r1.drawMention = r5
        L_0x166a:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x154e
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x16a1
            int r0 = r40.getMeasuredWidth()
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            int r5 = r1.mentionWidth
            if (r5 == 0) goto L_0x168f
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x1690
        L_0x168f:
            r5 = 0
        L_0x1690:
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x169b
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x169c
        L_0x169b:
            r5 = 0
        L_0x169c:
            int r0 = r0 - r5
            r1.reactionMentionLeft = r0
            goto L_0x154e
        L_0x16a1:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x16af
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r6 + r9
            goto L_0x16b0
        L_0x16af:
            r6 = 0
        L_0x16b0:
            int r5 = r5 + r6
            r1.reactionMentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
            goto L_0x154e
        L_0x16bf:
            if (r3 == 0) goto L_0x170b
            if (r2 != 0) goto L_0x16c6
            r9 = r22
            goto L_0x16c7
        L_0x16c6:
            r9 = r2
        L_0x16c7:
            int r2 = r9.length()
            r3 = 150(0x96, float:2.1E-43)
            if (r2 <= r3) goto L_0x16d4
            r2 = 0
            java.lang.CharSequence r9 = r9.subSequence(r2, r3)
        L_0x16d4:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x16dc
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x16de
        L_0x16dc:
            if (r7 == 0) goto L_0x16e3
        L_0x16de:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            goto L_0x16e7
        L_0x16e3:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r9)
        L_0x16e7:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r5 = 1099431936(0x41880000, float:17.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r6)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x170b
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x170b
            r2 = r3
        L_0x170b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = java.lang.Math.max(r3, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x171b
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x1772
        L_0x171b:
            if (r7 == 0) goto L_0x1772
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x1726
            int r5 = r1.currentDialogFolderDialogsCount
            r6 = 1
            if (r5 != r6) goto L_0x1772
        L_0x1726:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1758 }
            if (r0 == 0) goto L_0x173d
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1758 }
            if (r0 == 0) goto L_0x173d
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1758 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1758 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x1758 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r7, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)     // Catch:{ Exception -> 0x1758 }
            if (r0 == 0) goto L_0x173d
            r7 = r0
        L_0x173d:
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1758 }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1758 }
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1758 }
            r38 = 1
            r29 = r7
            r31 = r3
            r37 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x1758 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1758 }
            goto L_0x175c
        L_0x1758:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x175c:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            r0.setImageY(r4)
            goto L_0x179a
        L_0x1772:
            r5 = 0
            r1.messageNameLayout = r5
            if (r0 != 0) goto L_0x1785
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x177c
            goto L_0x1785
        L_0x177c:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x179a
        L_0x1785:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1101529088(0x41a80000, float:21.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x179a:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x186e }
            if (r0 != 0) goto L_0x17a2
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x186e }
            if (r4 == 0) goto L_0x17b4
        L_0x17a2:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x186e }
            if (r4 == 0) goto L_0x17b4
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x186e }
            r5 = 1
            if (r4 <= r5) goto L_0x17b4
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x186e }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x186e }
            r10 = r0[r2]     // Catch:{ Exception -> 0x186e }
            r2 = r7
            r7 = 0
            goto L_0x17c9
        L_0x17b4:
            if (r0 != 0) goto L_0x17ba
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x17bc
        L_0x17ba:
            if (r7 == 0) goto L_0x17c9
        L_0x17bc:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x186e }
            int r0 = r3 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x186e }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x186e }
            java.lang.CharSequence r2 = android.text.TextUtils.ellipsize(r2, r10, r0, r4)     // Catch:{ Exception -> 0x186e }
        L_0x17c9:
            boolean r0 = r2 instanceof android.text.Spannable     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x17e9
            r0 = r2
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x186e }
            int r4 = r0.length()     // Catch:{ Exception -> 0x186e }
            java.lang.Class<android.text.style.ClickableSpan> r5 = android.text.style.ClickableSpan.class
            r6 = 0
            java.lang.Object[] r4 = r0.getSpans(r6, r4, r5)     // Catch:{ Exception -> 0x186e }
            android.text.style.ClickableSpan[] r4 = (android.text.style.ClickableSpan[]) r4     // Catch:{ Exception -> 0x186e }
            int r5 = r4.length     // Catch:{ Exception -> 0x186e }
            r6 = 0
        L_0x17df:
            if (r6 >= r5) goto L_0x17e9
            r9 = r4[r6]     // Catch:{ Exception -> 0x186e }
            r0.removeSpan(r9)     // Catch:{ Exception -> 0x186e }
            int r6 = r6 + 1
            goto L_0x17df
        L_0x17e9:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x186e }
            if (r0 != 0) goto L_0x1825
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x17f2
            goto L_0x1825
        L_0x17f2:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x180d
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x186e }
            int r0 = r0 + r11
            int r3 = r3 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x180d
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x186e }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x186e }
            int r4 = r11 + r5
            int r0 = r0 - r4
            r1.messageLeft = r0     // Catch:{ Exception -> 0x186e }
        L_0x180d:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x186e }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x186e }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r2
            r31 = r10
            r32 = r3
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x186e }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x186e }
            goto L_0x1858
        L_0x1825:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x186e }
            if (r0 == 0) goto L_0x1832
            if (r7 == 0) goto L_0x1832
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x186e }
            int r3 = r3 + r0
        L_0x1832:
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x186e }
            r33 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x186e }
            float r0 = (float) r0     // Catch:{ Exception -> 0x186e }
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x186e }
            if (r7 == 0) goto L_0x1846
            r38 = 1
            goto L_0x1848
        L_0x1846:
            r38 = 2
        L_0x1848:
            r29 = r2
            r30 = r10
            r31 = r3
            r34 = r0
            r37 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x186e }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x186e }
        L_0x1858:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x186e }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilers     // Catch:{ Exception -> 0x186e }
            r0.addAll(r2)     // Catch:{ Exception -> 0x186e }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilers     // Catch:{ Exception -> 0x186e }
            r0.clear()     // Catch:{ Exception -> 0x186e }
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x186e }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x186e }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilers     // Catch:{ Exception -> 0x186e }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r4)     // Catch:{ Exception -> 0x186e }
            goto L_0x1875
        L_0x186e:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1875:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x19b1
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x193a
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x193a
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x18c7
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x18c7
            int r2 = r1.drawScam
            if (r2 != 0) goto L_0x18c7
            int r2 = r1.nameLeft
            double r6 = (double) r2
            double r9 = (double) r8
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r2.getIntrinsicWidth()
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r2 = (int) r6
            r1.nameMuteLeft = r2
            goto L_0x1922
        L_0x18c7:
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x18f1
            int r2 = r1.nameLeft
            double r6 = (double) r2
            double r9 = (double) r8
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r2.getIntrinsicWidth()
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r2 = (int) r6
            r1.nameMuteLeft = r2
            goto L_0x1922
        L_0x18f1:
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1922
            int r2 = r1.nameLeft
            double r6 = (double) r2
            double r9 = (double) r8
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r9
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r2 = r1.drawScam
            r9 = 1
            if (r2 != r9) goto L_0x1914
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1916
        L_0x1914:
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1916:
            int r2 = r2.getIntrinsicWidth()
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r2 = (int) r6
            r1.nameMuteLeft = r2
        L_0x1922:
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x193a
            double r6 = (double) r8
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x193a
            int r0 = r1.nameLeft
            double r8 = (double) r0
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r6
            int r0 = (int) r8
            r1.nameLeft = r0
        L_0x193a:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x197b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x197b
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x194b:
            if (r2 >= r0) goto L_0x1971
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r2)
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 != 0) goto L_0x1970
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineWidth(r2)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r5
            int r5 = (int) r7
            int r4 = java.lang.Math.min(r4, r5)
            int r2 = r2 + 1
            goto L_0x194b
        L_0x1970:
            r4 = 0
        L_0x1971:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x197b
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x197b:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1a3b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a3b
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1a3b
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r2 = (double) r3
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x1a3b
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r2
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x1a3b
        L_0x19b1:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1a00
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a00
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r8
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x19e5
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r8
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x19e5
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x19e5:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x19f1
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x19f1
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1a00
        L_0x19f1:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1a00:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a23
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a23
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1a0d:
            if (r3 >= r0) goto L_0x1a1c
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1a0d
        L_0x1a1c:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1a23:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1a3b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a3b
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1a3b:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a7f
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1a7f
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1a7b }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1a7b }
            r5 = r23
            r2 = 1
            if (r5 < r0) goto L_0x1a52
            int r5 = r0 + -1
        L_0x1a52:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1a7b }
            float r0 = r0.getPrimaryHorizontal(r5)     // Catch:{ Exception -> 0x1a7b }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1a7b }
            int r5 = r5 + r2
            float r2 = r3.getPrimaryHorizontal(r5)     // Catch:{ Exception -> 0x1a7b }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1a7b }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1a7b }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1a7b }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1a7b }
            if (r0 == 0) goto L_0x1a72
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1a7b }
            int r0 = r0 + r2
        L_0x1a72:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1a7b }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1a7b }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1a7b }
            goto L_0x1a7f
        L_0x1a7b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1a7f:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1acd
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1acd
            if (r25 < 0) goto L_0x1aa4
            int r11 = r25 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r11 >= r0) goto L_0x1aa4
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r25
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r11)
            goto L_0x1ab2
        L_0x1aa4:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1ab2:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1abe
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1acd
        L_0x1abe:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1acd:
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

    /* JADX WARNING: Removed duplicated region for block: B:132:0x01f6  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0554  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0558  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x055d  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0199  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r25, boolean r26) {
        /*
            r24 = this;
            r0 = r24
            org.telegram.ui.Cells.DialogCell$CustomDialog r1 = r0.customDialog
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x003d
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
        L_0x003a:
            r1 = 0
            goto L_0x0547
        L_0x003d:
            int r1 = r0.unreadCount
            int r6 = r0.reactionMentionCount
            if (r6 == 0) goto L_0x0045
            r6 = 1
            goto L_0x0046
        L_0x0045:
            r6 = 0
        L_0x0046:
            boolean r7 = r0.markUnread
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x0110
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            long r9 = r0.currentDialogId
            java.lang.Object r8 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 == 0) goto L_0x0105
            if (r25 != 0) goto L_0x0112
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
            if (r9 == 0) goto L_0x008a
            boolean r9 = r9.isUnread()
            if (r9 == 0) goto L_0x008a
            r9 = 1
            goto L_0x008b
        L_0x008a:
            r9 = 0
        L_0x008b:
            r0.lastUnreadState = r9
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r9 == 0) goto L_0x00a0
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r9)
            int r9 = r9.getArchiveUnreadCount()
            r0.unreadCount = r9
            r0.mentionCount = r5
            goto L_0x00ac
        L_0x00a0:
            int r9 = r8.unread_count
            r0.unreadCount = r9
            int r9 = r8.unread_mentions_count
            r0.mentionCount = r9
            int r9 = r8.unread_reactions_count
            r0.reactionMentionCount = r9
        L_0x00ac:
            boolean r9 = r8.unread_mark
            r0.markUnread = r9
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x00b9
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.edit_date
            goto L_0x00ba
        L_0x00b9:
            r9 = 0
        L_0x00ba:
            r0.currentEditDate = r9
            int r9 = r8.last_message_date
            r0.lastMessageDate = r9
            int r9 = r0.dialogsType
            r10 = 7
            r11 = 8
            if (r9 == r10) goto L_0x00d8
            if (r9 != r11) goto L_0x00ca
            goto L_0x00d8
        L_0x00ca:
            int r9 = r0.currentDialogFolderId
            if (r9 != 0) goto L_0x00d4
            boolean r8 = r8.pinned
            if (r8 == 0) goto L_0x00d4
            r8 = 1
            goto L_0x00d5
        L_0x00d4:
            r8 = 0
        L_0x00d5:
            r0.drawPin = r8
            goto L_0x00fa
        L_0x00d8:
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.messenger.MessagesController$DialogFilter[] r9 = r9.selectedDialogFilter
            int r10 = r0.dialogsType
            if (r10 != r11) goto L_0x00e6
            r10 = 1
            goto L_0x00e7
        L_0x00e6:
            r10 = 0
        L_0x00e7:
            r9 = r9[r10]
            if (r9 == 0) goto L_0x00f7
            org.telegram.messenger.support.LongSparseIntArray r9 = r9.pinnedDialogs
            long r10 = r8.id
            int r8 = r9.indexOfKey(r10)
            if (r8 < 0) goto L_0x00f7
            r8 = 1
            goto L_0x00f8
        L_0x00f7:
            r8 = 0
        L_0x00f8:
            r0.drawPin = r8
        L_0x00fa:
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x0112
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.send_state
            r0.lastSendState = r8
            goto L_0x0112
        L_0x0105:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0112
        L_0x0110:
            r0.drawPin = r5
        L_0x0112:
            if (r25 == 0) goto L_0x0255
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 == 0) goto L_0x0135
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r8 = r25 & r8
            if (r8 == 0) goto L_0x0135
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            long r9 = r9.id
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r9)
            r0.user = r8
            r24.invalidate()
        L_0x0135:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x015f
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_USER_PRINT
            r8 = r25 & r8
            if (r8 == 0) goto L_0x015f
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r9 = r0.currentDialogId
            java.lang.CharSequence r8 = r8.getPrintingString(r9, r5, r4)
            java.lang.CharSequence r9 = r0.lastPrintString
            if (r9 == 0) goto L_0x0151
            if (r8 == 0) goto L_0x015d
        L_0x0151:
            if (r9 != 0) goto L_0x0155
            if (r8 != 0) goto L_0x015d
        L_0x0155:
            if (r9 == 0) goto L_0x015f
            boolean r8 = r9.equals(r8)
            if (r8 != 0) goto L_0x015f
        L_0x015d:
            r8 = 1
            goto L_0x0160
        L_0x015f:
            r8 = 0
        L_0x0160:
            if (r8 != 0) goto L_0x0173
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_MESSAGE_TEXT
            r9 = r25 & r9
            if (r9 == 0) goto L_0x0173
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x0173
            java.lang.CharSequence r9 = r9.messageText
            java.lang.CharSequence r10 = r0.lastMessageString
            if (r9 == r10) goto L_0x0173
            r8 = 1
        L_0x0173:
            if (r8 != 0) goto L_0x01a1
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01a1
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            if (r9 == 0) goto L_0x01a1
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            long r10 = r10.id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            boolean r10 = r9.call_active
            if (r10 == 0) goto L_0x019b
            boolean r9 = r9.call_not_empty
            if (r9 == 0) goto L_0x019b
            r9 = 1
            goto L_0x019c
        L_0x019b:
            r9 = 0
        L_0x019c:
            boolean r10 = r0.hasCall
            if (r9 == r10) goto L_0x01a1
            r8 = 1
        L_0x01a1:
            if (r8 != 0) goto L_0x01ae
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01ae
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            if (r9 != 0) goto L_0x01ae
            r8 = 1
        L_0x01ae:
            if (r8 != 0) goto L_0x01bb
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01bb
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            if (r9 != 0) goto L_0x01bb
            r8 = 1
        L_0x01bb:
            if (r8 != 0) goto L_0x01c8
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01c8
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            if (r9 != 0) goto L_0x01c8
            r8 = 1
        L_0x01c8:
            if (r8 != 0) goto L_0x01d5
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r9 = r25 & r9
            if (r9 == 0) goto L_0x01d5
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            if (r9 != 0) goto L_0x01d5
            r8 = 1
        L_0x01d5:
            if (r8 != 0) goto L_0x0238
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE
            r9 = r25 & r9
            if (r9 == 0) goto L_0x0238
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x01f2
            boolean r10 = r0.lastUnreadState
            boolean r9 = r9.isUnread()
            if (r10 == r9) goto L_0x01f2
            org.telegram.messenger.MessageObject r8 = r0.message
            boolean r8 = r8.isUnread()
            r0.lastUnreadState = r8
            r8 = 1
        L_0x01f2:
            boolean r9 = r0.isDialogCell
            if (r9 == 0) goto L_0x0238
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r9 = r9.dialogs_dict
            long r10 = r0.currentDialogId
            java.lang.Object r9 = r9.get(r10)
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC$Dialog) r9
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r10 == 0) goto L_0x0216
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)
            int r10 = r10.getArchiveUnreadCount()
        L_0x0214:
            r11 = 0
            goto L_0x021f
        L_0x0216:
            if (r9 == 0) goto L_0x021d
            int r10 = r9.unread_count
            int r11 = r9.unread_mentions_count
            goto L_0x021f
        L_0x021d:
            r10 = 0
            goto L_0x0214
        L_0x021f:
            if (r9 == 0) goto L_0x0238
            int r12 = r0.unreadCount
            if (r12 != r10) goto L_0x022f
            boolean r12 = r0.markUnread
            boolean r13 = r9.unread_mark
            if (r12 != r13) goto L_0x022f
            int r12 = r0.mentionCount
            if (r12 == r11) goto L_0x0238
        L_0x022f:
            r0.unreadCount = r10
            r0.mentionCount = r11
            boolean r8 = r9.unread_mark
            r0.markUnread = r8
            r8 = 1
        L_0x0238:
            if (r8 != 0) goto L_0x024f
            int r9 = org.telegram.messenger.MessagesController.UPDATE_MASK_SEND_STATE
            r9 = r25 & r9
            if (r9 == 0) goto L_0x024f
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x024f
            int r10 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.send_state
            if (r10 == r9) goto L_0x024f
            r0.lastSendState = r9
            r8 = 1
        L_0x024f:
            if (r8 != 0) goto L_0x0255
            r24.invalidate()
            return
        L_0x0255:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r3 = r0.currentDialogFolderId
            r8 = 0
            if (r3 == 0) goto L_0x0272
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r3 = r24.findFolderTopMessage()
            r0.message = r3
            if (r3 == 0) goto L_0x0270
            long r10 = r3.getDialogId()
            goto L_0x028b
        L_0x0270:
            r10 = r8
            goto L_0x028b
        L_0x0272:
            boolean r3 = r0.isDialogCell
            if (r3 == 0) goto L_0x0286
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r10 = r0.currentDialogId
            boolean r3 = r3.isDialogMuted(r10)
            if (r3 == 0) goto L_0x0286
            r3 = 1
            goto L_0x0287
        L_0x0286:
            r3 = 0
        L_0x0287:
            r0.dialogMuted = r3
            long r10 = r0.currentDialogId
        L_0x028b:
            int r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x0332
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)
            if (r3 == 0) goto L_0x02c0
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r8 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r8)
            r0.encryptedChat = r3
            if (r3 == 0) goto L_0x030a
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r0.encryptedChat
            long r8 = r8.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
            goto L_0x030a
        L_0x02c0:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r3 == 0) goto L_0x02d7
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r8 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
            goto L_0x030a
        L_0x02d7:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r8 = -r10
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r8)
            r0.chat = r3
            boolean r8 = r0.isDialogCell
            if (r8 != 0) goto L_0x030a
            if (r3 == 0) goto L_0x030a
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            if (r3 == 0) goto L_0x030a
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r8 = r8.migrated_to
            long r8 = r8.channel_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r8)
            if (r3 == 0) goto L_0x030a
            r0.chat = r3
        L_0x030a:
            boolean r3 = r0.useMeForMyMessages
            if (r3 == 0) goto L_0x0332
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x0332
            org.telegram.messenger.MessageObject r3 = r0.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x0332
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r8 = r8.clientUserId
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
        L_0x0332:
            int r3 = r0.currentDialogFolderId
            r8 = 2
            if (r3 == 0) goto L_0x034f
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r3.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r8)
            org.telegram.messenger.ImageReceiver r9 = r0.avatarImage
            r10 = 0
            r11 = 0
            org.telegram.ui.Components.AvatarDrawable r12 = r0.avatarDrawable
            r13 = 0
            org.telegram.tgnet.TLRPC$User r14 = r0.user
            r15 = 0
            r9.setImage(r10, r11, r12, r13, r14, r15)
            goto L_0x03b1
        L_0x034f:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x039f
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x0376
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r9 = 12
            r3.setAvatarType(r9)
            org.telegram.messenger.ImageReceiver r10 = r0.avatarImage
            r11 = 0
            r12 = 0
            org.telegram.ui.Components.AvatarDrawable r13 = r0.avatarDrawable
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r0.user
            r16 = 0
            r10.setImage(r11, r12, r13, r14, r15, r16)
            goto L_0x03b1
        L_0x0376:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x0395
            boolean r3 = r0.useMeForMyMessages
            if (r3 != 0) goto L_0x0395
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r9 = r0.avatarImage
            r10 = 0
            r11 = 0
            org.telegram.ui.Components.AvatarDrawable r12 = r0.avatarDrawable
            r13 = 0
            org.telegram.tgnet.TLRPC$User r14 = r0.user
            r15 = 0
            r9.setImage(r10, r11, r12, r13, r14, r15)
            goto L_0x03b1
        L_0x0395:
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r9 = r0.user
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setForUserOrChat(r9, r10)
            goto L_0x03b1
        L_0x039f:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            if (r3 == 0) goto L_0x03b1
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setForUserOrChat(r9, r10)
        L_0x03b1:
            r9 = 150(0x96, double:7.4E-322)
            r11 = 220(0xdc, double:1.087E-321)
            if (r26 == 0) goto L_0x04f0
            int r3 = r0.unreadCount
            if (r1 != r3) goto L_0x03bf
            boolean r3 = r0.markUnread
            if (r7 == r3) goto L_0x04f0
        L_0x03bf:
            long r13 = java.lang.System.currentTimeMillis()
            long r2 = r0.lastDialogChangedTime
            long r13 = r13 - r2
            r2 = 100
            int r16 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r16 <= 0) goto L_0x04f0
            android.animation.ValueAnimator r2 = r0.countAnimator
            if (r2 == 0) goto L_0x03d3
            r2.cancel()
        L_0x03d3:
            float[] r2 = new float[r8]
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
            if (r1 == 0) goto L_0x03f6
            boolean r2 = r0.markUnread
            if (r2 == 0) goto L_0x03fd
        L_0x03f6:
            boolean r2 = r0.markUnread
            if (r2 != 0) goto L_0x041d
            if (r7 != 0) goto L_0x03fd
            goto L_0x041d
        L_0x03fd:
            int r2 = r0.unreadCount
            if (r2 != 0) goto L_0x040e
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r9)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x042c
        L_0x040e:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r13 = 430(0x1ae, double:2.124E-321)
            r2.setDuration(r13)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x042c
        L_0x041d:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r11)
            android.animation.ValueAnimator r2 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
        L_0x042c:
            boolean r2 = r0.drawCount
            if (r2 == 0) goto L_0x04da
            android.text.StaticLayout r2 = r0.countLayout
            if (r2 == 0) goto L_0x04da
            java.lang.String r2 = java.lang.String.valueOf(r1)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r7 = r2.length()
            int r13 = r3.length()
            if (r7 != r13) goto L_0x04d6
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r2)
            android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
            r13.<init>(r3)
            android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder
            r14.<init>(r3)
            r4 = 0
        L_0x0458:
            int r15 = r2.length()
            if (r4 >= r15) goto L_0x048a
            char r15 = r2.charAt(r4)
            char r9 = r3.charAt(r4)
            if (r15 != r9) goto L_0x047b
            org.telegram.ui.Components.EmptyStubSpan r9 = new org.telegram.ui.Components.EmptyStubSpan
            r9.<init>()
            int r10 = r4 + 1
            r7.setSpan(r9, r4, r10, r5)
            org.telegram.ui.Components.EmptyStubSpan r9 = new org.telegram.ui.Components.EmptyStubSpan
            r9.<init>()
            r13.setSpan(r9, r4, r10, r5)
            goto L_0x0485
        L_0x047b:
            org.telegram.ui.Components.EmptyStubSpan r9 = new org.telegram.ui.Components.EmptyStubSpan
            r9.<init>()
            int r10 = r4 + 1
            r14.setSpan(r9, r4, r10, r5)
        L_0x0485:
            int r4 = r4 + 1
            r9 = 150(0x96, double:7.4E-322)
            goto L_0x0458
        L_0x048a:
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r4.measureText(r2)
            double r9 = (double) r2
            double r9 = java.lang.Math.ceil(r9)
            int r2 = (int) r9
            int r2 = java.lang.Math.max(r3, r2)
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r3
            r17 = r7
            r19 = r2
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countOldLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = r3
            r17 = r14
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countAnimationStableLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = r3
            r17 = r13
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.countAnimationInLayout = r3
            goto L_0x04da
        L_0x04d6:
            android.text.StaticLayout r2 = r0.countLayout
            r0.countOldLayout = r2
        L_0x04da:
            int r2 = r0.countWidth
            r0.countWidthOld = r2
            int r2 = r0.countLeft
            r0.countLeftOld = r2
            int r2 = r0.unreadCount
            if (r2 <= r1) goto L_0x04e8
            r1 = 1
            goto L_0x04e9
        L_0x04e8:
            r1 = 0
        L_0x04e9:
            r0.countAnimationIncrement = r1
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x04f0:
            int r1 = r0.reactionMentionCount
            if (r1 == 0) goto L_0x04f6
            r4 = 1
            goto L_0x04f7
        L_0x04f6:
            r4 = 0
        L_0x04f7:
            if (r26 == 0) goto L_0x003a
            if (r4 == r6) goto L_0x003a
            android.animation.ValueAnimator r1 = r0.reactionsMentionsAnimator
            if (r1 == 0) goto L_0x0502
            r1.cancel()
        L_0x0502:
            r1 = 0
            r0.reactionsMentionsChangeProgress = r1
            float[] r2 = new float[r8]
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
            if (r4 == 0) goto L_0x0534
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.setDuration(r11)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
            goto L_0x0542
        L_0x0534:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r3 = 150(0x96, double:7.4E-322)
            r2.setDuration(r3)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
        L_0x0542:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.start()
        L_0x0547:
            int r2 = r24.getMeasuredWidth()
            if (r2 != 0) goto L_0x0558
            int r2 = r24.getMeasuredHeight()
            if (r2 == 0) goto L_0x0554
            goto L_0x0558
        L_0x0554:
            r24.requestLayout()
            goto L_0x055b
        L_0x0558:
            r24.buildLayout()
        L_0x055b:
            if (r26 != 0) goto L_0x056e
            boolean r2 = r0.dialogMuted
            if (r2 == 0) goto L_0x0564
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0565
        L_0x0564:
            r2 = 0
        L_0x0565:
            r0.dialogMutedProgress = r2
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x056e
            r1.cancel()
        L_0x056e:
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
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0697, code lost:
        if (r0.type == 2) goto L_0x06b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0e14, code lost:
        if (r8.reactionsMentionsChangeProgress != 1.0f) goto L_0x0e19;
     */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0622  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0631  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0671  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x06ec  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0761  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x084d  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x090c  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x0922  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0960  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0963  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x096d  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x0970  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x09b8  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x09bf  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0d58  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0e0e  */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0e17  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0e4f  */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0e52  */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x0e5f  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0ec2  */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0f2f  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x0f4c  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x1015  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x12b6  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x12ca  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x12df  */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x12f9  */
    /* JADX WARNING: Removed duplicated region for block: B:695:0x1325  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x138a  */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x13e3  */
    /* JADX WARNING: Removed duplicated region for block: B:732:0x13f8  */
    /* JADX WARNING: Removed duplicated region for block: B:741:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x143c  */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x146c  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1480  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x14a8  */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x14ca  */
    /* JADX WARNING: Removed duplicated region for block: B:789:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r31) {
        /*
            r30 = this;
            r8 = r30
            r9 = r31
            long r0 = r8.currentDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0011
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            int r0 = r8.currentDialogFolderId
            r10 = 0
            r11 = 0
            if (r0 == 0) goto L_0x003e
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x003e
            float r0 = r0.outProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x003e
            float r0 = r8.translationX
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x003e
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r9.clipRect(r10, r10, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
            return
        L_0x003e:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r8.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x004e
            r2 = 17
        L_0x004e:
            r12 = r2
            r8.lastUpdateTime = r0
            float r0 = r8.clipProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x007d
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x007d
            r31.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r30.getMeasuredHeight()
            int r3 = r8.bottomClip
            float r3 = (float) r3
            float r4 = r8.clipProgress
            float r3 = r3 * r4
            int r3 = (int) r3
            int r2 = r2 - r3
            float r2 = (float) r2
            r9.clipRect(r11, r0, r1, r2)
        L_0x007d:
            float r0 = r8.translationX
            r14 = 4
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 1073741824(0x40000000, float:2.0)
            r17 = 1082130432(0x40800000, float:4.0)
            r6 = 1
            r5 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x00b0
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0094
            goto L_0x00b0
        L_0x0094:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x00ab
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r10
        L_0x00ab:
            r7 = 1065353216(0x3var_, float:1.0)
            r15 = 1
            goto L_0x049d
        L_0x00b0:
            r31.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r4 = "chats_archivePinBackground"
            java.lang.String r3 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00f9
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00dc
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628293(0x7f0e1105, float:1.8883875E38)
            java.lang.String r7 = "UnhideFromTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628293(0x7f0e1105, float:1.8883875E38)
            goto L_0x0119
        L_0x00dc:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131625909(0x7f0e07b5, float:1.887904E38)
            java.lang.String r7 = "HideOnTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131625909(0x7f0e07b5, float:1.887904E38)
            goto L_0x0119
        L_0x00f9:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131627398(0x7f0e0d86, float:1.888206E38)
            java.lang.String r7 = "PsaHide"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627398(0x7f0e0d86, float:1.888206E38)
        L_0x0119:
            r29 = r7
            r7 = r1
            r1 = r29
            goto L_0x0215
        L_0x0120:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01f7
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r7 = 3
            if (r2 != r7) goto L_0x015f
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x014e
            r2 = 2131628099(0x7f0e1043, float:1.8883481E38)
            java.lang.String r7 = "SwipeUnmute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628099(0x7f0e1043, float:1.8883481E38)
            goto L_0x0119
        L_0x014e:
            r2 = 2131628087(0x7f0e1037, float:1.8883457E38)
            java.lang.String r7 = "SwipeMute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628087(0x7f0e1037, float:1.8883457E38)
            goto L_0x0119
        L_0x015f:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r14) goto L_0x0180
            r2 = 2131628084(0x7f0e1034, float:1.888345E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r2 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131628084(0x7f0e1034, float:1.888345E38)
            goto L_0x0119
        L_0x0180:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r6) goto L_0x01b5
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x01a3
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x0191
            goto L_0x01a3
        L_0x0191:
            r2 = 2131628086(0x7f0e1036, float:1.8883455E38)
            java.lang.String r7 = "SwipeMarkAsUnread"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131628086(0x7f0e1036, float:1.8883455E38)
            goto L_0x0119
        L_0x01a3:
            r2 = 2131628085(0x7f0e1035, float:1.8883453E38)
            java.lang.String r7 = "SwipeMarkAsRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131628085(0x7f0e1035, float:1.8883453E38)
            goto L_0x0119
        L_0x01b5:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01e5
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01d3
            r2 = 2131628100(0x7f0e1044, float:1.8883483E38)
            java.lang.String r7 = "SwipeUnpin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131628100(0x7f0e1044, float:1.8883483E38)
            goto L_0x0119
        L_0x01d3:
            r2 = 2131628088(0x7f0e1038, float:1.8883459E38)
            java.lang.String r7 = "SwipePin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131628088(0x7f0e1038, float:1.8883459E38)
            goto L_0x0119
        L_0x01e5:
            r2 = 2131624319(0x7f0e017f, float:1.8875814E38)
            java.lang.String r7 = "Archive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624319(0x7f0e017f, float:1.8875814E38)
            goto L_0x0119
        L_0x01f7:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628284(0x7f0e10fc, float:1.8883856E38)
            java.lang.String r7 = "Unarchive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628284(0x7f0e10fc, float:1.8883856E38)
            goto L_0x0119
        L_0x0215:
            boolean r14 = r8.swipeCanceled
            if (r14 == 0) goto L_0x0222
            org.telegram.ui.Components.RLottieDrawable r14 = r8.lastDrawTranslationDrawable
            if (r14 == 0) goto L_0x0222
            r8.translationDrawable = r14
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x0228
        L_0x0222:
            org.telegram.ui.Components.RLottieDrawable r14 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r14
            r8.lastDrawSwipeMessageStringId = r2
        L_0x0228:
            r14 = r2
            boolean r2 = r8.translationAnimationStarted
            if (r2 != 0) goto L_0x024f
            float r2 = r8.translationX
            float r2 = java.lang.Math.abs(r2)
            r19 = 1110179840(0x422CLASSNAME, float:43.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r10 = (float) r10
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x024f
            r8.translationAnimationStarted = r6
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.start()
        L_0x024f:
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            float r10 = r8.translationX
            float r10 = r10 + r2
            float r2 = r8.currentRevealProgress
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x02d0
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r0 = (float) r0
            float r2 = r10 - r0
            r0 = 0
            int r5 = r30.getMeasuredWidth()
            float r5 = (float) r5
            int r6 = r30.getMeasuredHeight()
            float r6 = (float) r6
            android.graphics.Paint r21 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r27 = r1
            r1 = r31
            r22 = r3
            r3 = r0
            r0 = r4
            r4 = r5
            r5 = r6
            r6 = r21
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x02d3
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x029d
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r1
        L_0x029d:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x02d3
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r1
            goto L_0x02d3
        L_0x02d0:
            r27 = r1
            r0 = r4
        L_0x02d3:
            int r1 = r30.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            r3 = 2
            int r2 = r2 / r3
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x02f3
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02f0
            goto L_0x02f3
        L_0x02f0:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x02f5
        L_0x02f3:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x02f5:
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
            int r4 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r4 <= 0) goto L_0x039d
            r31.save()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r4 = (float) r4
            float r4 = r10 - r4
            int r6 = r30.getMeasuredWidth()
            float r6 = (float) r6
            int r15 = r30.getMeasuredHeight()
            float r15 = (float) r15
            r9.clipRect(r4, r11, r6, r15)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r4.setColor(r7)
            int r4 = r3 * r3
            int r6 = r30.getMeasuredHeight()
            int r6 = r5 - r6
            int r7 = r30.getMeasuredHeight()
            int r7 = r5 - r7
            int r6 = r6 * r7
            int r4 = r4 + r6
            double r6 = (double) r4
            double r6 = java.lang.Math.sqrt(r6)
            float r4 = (float) r6
            float r3 = (float) r3
            float r5 = (float) r5
            android.view.animation.AccelerateInterpolator r6 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r7 = r8.currentRevealProgress
            float r6 = r6.getInterpolation(r7)
            float r4 = r4 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r3, r5, r4, r6)
            r31.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x036a
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r15 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
            goto L_0x036b
        L_0x036a:
            r15 = 1
        L_0x036b:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x039e
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r5 = "Line 1.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r5 = "Line 2.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r0 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r4 = "Line 3.**"
            r3.setLayerColor(r4, r0)
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r0.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r15
            goto L_0x039e
        L_0x039d:
            r15 = 1
        L_0x039e:
            r31.save()
            float r0 = (float) r1
            float r1 = (float) r2
            r9.translate(r0, r1)
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            r7 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x03cd
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x03cd
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r1 = r8.interpolator
            float r0 = r1.getInterpolation(r0)
            float r0 = r0 + r7
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            int r1 = r1.getIntrinsicWidth()
            r2 = 2
            int r1 = r1 / r2
            float r1 = (float) r1
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r2
            float r2 = (float) r3
            r9.scale(r0, r0, r1, r2)
        L_0x03cd:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r1)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.draw(r9)
            r31.restore()
            int r0 = r30.getMeasuredWidth()
            float r0 = (float) r0
            int r1 = r30.getMeasuredHeight()
            float r1 = (float) r1
            r9.clipRect(r10, r11, r0, r1)
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1 = r27
            float r0 = r0.measureText(r1)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            int r0 = (int) r2
            int r2 = r8.swipeMessageTextId
            if (r2 != r14) goto L_0x0402
            int r2 = r8.swipeMessageWidth
            int r3 = r30.getMeasuredWidth()
            if (r2 == r3) goto L_0x044e
        L_0x0402:
            r8.swipeMessageTextId = r14
            int r2 = r30.getMeasuredWidth()
            r8.swipeMessageWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r21 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r22 = java.lang.Math.min(r3, r0)
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_CENTER
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r2
            r20 = r1
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r8.swipeMessageTextLayout = r2
            int r2 = r2.getLineCount()
            if (r2 <= r15) goto L_0x044e
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r21 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r3 = 1118044160(0x42a40000, float:82.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r22 = java.lang.Math.min(r3, r0)
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_CENTER
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r2
            r20 = r1
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r8.swipeMessageTextLayout = r2
        L_0x044e:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x049a
            r31.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            if (r0 <= r15) goto L_0x0464
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x0465
        L_0x0464:
            r0 = 0
        L_0x0465:
            int r1 = r30.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            android.text.StaticLayout r2 = r8.swipeMessageTextLayout
            int r2 = r2.getWidth()
            float r2 = (float) r2
            float r2 = r2 / r16
            float r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0487
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0484
            goto L_0x0487
        L_0x0484:
            r2 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x0489
        L_0x0487:
            r2 = 1112014848(0x42480000, float:50.0)
        L_0x0489:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 + r0
            r9.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r9)
            r31.restore()
        L_0x049a:
            r31.restore()
        L_0x049d:
            float r0 = r8.translationX
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x04ab
            r31.save()
            float r0 = r8.translationX
            r9.translate(r0, r11)
        L_0x04ab:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x04c2
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x04c2:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x04f8
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x04d0
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x04f8
        L_0x04d0:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r7)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0520
        L_0x04f8:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0500
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x0520
        L_0x0500:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0520:
            float r0 = r8.translationX
            java.lang.String r10 = "windowBackgroundWhite"
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0533
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x052f
            goto L_0x0533
        L_0x052f:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x05ec
        L_0x0533:
            r31.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r30.getMeasuredWidth()
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r30.getMeasuredHeight()
            float r3 = (float) r3
            r0.set(r1, r11, r2, r3)
            android.graphics.RectF r0 = r8.rect
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r3
            float r3 = r8.cornerProgress
            float r1 = r1 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r2, r1, r3)
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x05b5
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0585
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x05b5
        L_0x0585:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r7)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r3
            float r3 = r8.cornerProgress
            float r1 = r1 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r2, r1, r3)
            goto L_0x05be
        L_0x05b5:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x05c1
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x05be
            goto L_0x05c1
        L_0x05be:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x05e9
        L_0x05c1:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            r14 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r1 = (float) r1
            float r2 = r8.cornerProgress
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r1, r2, r3)
        L_0x05e9:
            r31.restore()
        L_0x05ec:
            float r0 = r8.translationX
            r19 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0607
            float r0 = r8.cornerProgress
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 >= 0) goto L_0x061c
            float r1 = (float) r12
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0619
            r8.cornerProgress = r7
            goto L_0x0619
        L_0x0607:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x061c
            float r1 = (float) r12
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x0619
            r8.cornerProgress = r11
        L_0x0619:
            r20 = 1
            goto L_0x061e
        L_0x061c:
            r20 = 0
        L_0x061e:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x0631
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x0669
        L_0x0631:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x0644
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x0669
        L_0x0644:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x0657
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x0669
        L_0x0657:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x0669
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x0669:
            android.text.StaticLayout r0 = r8.nameLayout
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x06ec
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x068c
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
            goto L_0x06c7
        L_0x068c:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x06b1
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x069a
            int r0 = r0.type
            r6 = 2
            if (r0 != r6) goto L_0x069b
            goto L_0x06b2
        L_0x069a:
            r6 = 2
        L_0x069b:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x06c7
        L_0x06b1:
            r6 = 2
        L_0x06b2:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x06c7:
            r31.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x06d9
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x06d6
            goto L_0x06d9
        L_0x06d6:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x06db
        L_0x06d9:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x06db:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r31.restore()
            goto L_0x06ed
        L_0x06ec:
            r6 = 2
        L_0x06ed:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0709
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0709
            r31.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r31.restore()
        L_0x0709:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x075d
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0721
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0744
        L_0x0721:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x0735
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0744
        L_0x0735:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x0744:
            r31.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x0756 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0756 }
            goto L_0x075a
        L_0x0756:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x075a:
            r31.restore()
        L_0x075d:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x0849
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0795
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x077f
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x07aa
        L_0x077f:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x07aa
        L_0x0795:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x07aa:
            r31.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            r31.save()     // Catch:{ Exception -> 0x07e9 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07e9 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r9, r0)     // Catch:{ Exception -> 0x07e9 }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07e9 }
            r0.draw(r9)     // Catch:{ Exception -> 0x07e9 }
            r31.restore()     // Catch:{ Exception -> 0x07e9 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07e9 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x07e9 }
        L_0x07cc:
            boolean r1 = r0.hasNext()     // Catch:{ Exception -> 0x07e9 }
            if (r1 == 0) goto L_0x07ed
            java.lang.Object r1 = r0.next()     // Catch:{ Exception -> 0x07e9 }
            org.telegram.ui.Components.spoilers.SpoilerEffect r1 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r1     // Catch:{ Exception -> 0x07e9 }
            android.text.StaticLayout r2 = r8.messageLayout     // Catch:{ Exception -> 0x07e9 }
            android.text.TextPaint r2 = r2.getPaint()     // Catch:{ Exception -> 0x07e9 }
            int r2 = r2.getColor()     // Catch:{ Exception -> 0x07e9 }
            r1.setColor(r2)     // Catch:{ Exception -> 0x07e9 }
            r1.draw(r9)     // Catch:{ Exception -> 0x07e9 }
            goto L_0x07cc
        L_0x07e9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07ed:
            r31.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0849
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0849
            r31.save()
            int r1 = r8.printingStringType
            if (r1 == r15) goto L_0x081e
            r2 = 4
            if (r1 != r2) goto L_0x0805
            goto L_0x081e
        L_0x0805:
            int r1 = r8.statusDrawableLeft
            float r1 = (float) r1
            int r2 = r8.messageTop
            float r2 = (float) r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r0.getIntrinsicHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 / r16
            float r2 = r2 + r3
            r9.translate(r1, r2)
            goto L_0x0830
        L_0x081e:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r15) goto L_0x082a
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x082b
        L_0x082a:
            r1 = 0
        L_0x082b:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0830:
            r0.draw(r9)
            int r1 = r8.statusDrawableLeft
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r4 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r4 = r4 + r0
            r8.invalidate(r1, r2, r3, r4)
            r31.restore()
        L_0x0849:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x090c
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0855
            r1 = 2
            goto L_0x0856
        L_0x0855:
            r1 = 0
        L_0x0856:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x085d
            r1 = 4
            goto L_0x085e
        L_0x085d:
            r1 = 0
        L_0x085e:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x086c
            if (r1 == r0) goto L_0x086c
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x086c
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x086c:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x0872
            int r0 = r8.animateToStatusDrawableParams
        L_0x0872:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x0879
            r18 = 1
            goto L_0x087b
        L_0x0879:
            r18 = 0
        L_0x087b:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x0883
            r2 = 4
            r23 = 1
            goto L_0x0886
        L_0x0883:
            r2 = 4
            r23 = 0
        L_0x0886:
            r0 = r0 & r2
            if (r0 == 0) goto L_0x088b
            r0 = 1
            goto L_0x088c
        L_0x088b:
            r0 = 0
        L_0x088c:
            if (r1 == 0) goto L_0x08e6
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x0896
            r3 = 1
            goto L_0x0897
        L_0x0896:
            r3 = 0
        L_0x0897:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x089e
            r2 = 4
            r4 = 1
            goto L_0x08a0
        L_0x089e:
            r2 = 4
            r4 = 0
        L_0x08a0:
            r1 = r1 & r2
            if (r1 == 0) goto L_0x08a5
            r5 = 1
            goto L_0x08a6
        L_0x08a5:
            r5 = 0
        L_0x08a6:
            if (r18 != 0) goto L_0x08cd
            if (r3 != 0) goto L_0x08cd
            if (r5 == 0) goto L_0x08cd
            if (r4 != 0) goto L_0x08cd
            if (r23 == 0) goto L_0x08cd
            if (r0 == 0) goto L_0x08cd
            r24 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r30
            r2 = r31
            r3 = r18
            r4 = r23
            r18 = r5
            r5 = r0
            r23 = 2
            r6 = r24
            r14 = 1065353216(0x3var_, float:1.0)
            r7 = r18
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x08f7
        L_0x08cd:
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            float r1 = r8.statusDrawableProgress
            float r7 = r14 - r1
            r1 = r30
            r2 = r31
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r18
            r4 = r23
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x08f7
        L_0x08e6:
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r30
            r2 = r31
            r3 = r18
            r4 = r23
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x08f7:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x08ff
            r7 = 2
            goto L_0x0900
        L_0x08ff:
            r7 = 0
        L_0x0900:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0907
            r1 = 4
            goto L_0x0908
        L_0x0907:
            r1 = 0
        L_0x0908:
            int r0 = r0 + r1
            r8.lastStatusDrawableParams = r0
            goto L_0x090e
        L_0x090c:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x090e:
            boolean r0 = r8.dialogMuted
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x091a
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x09bf
        L_0x091a:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x09bf
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x09bf
            if (r0 == 0) goto L_0x093b
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x093b
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x0937
            r8.dialogMutedProgress = r14
            goto L_0x0953
        L_0x0937:
            r30.invalidate()
            goto L_0x0953
        L_0x093b:
            if (r0 != 0) goto L_0x0953
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0953
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x0950
            r8.dialogMutedProgress = r11
            goto L_0x0953
        L_0x0950:
            r30.invalidate()
        L_0x0953:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0963
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0960
            goto L_0x0963
        L_0x0960:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0964
        L_0x0963:
            r5 = 0
        L_0x0964:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0970
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x0972
        L_0x0970:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0972:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x09b8
            r31.save()
            float r0 = r8.dialogMutedProgress
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerX()
            float r2 = (float) r2
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerY()
            float r3 = (float) r3
            r9.scale(r0, r0, r2, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            float r2 = r8.dialogMutedProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r2 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r2)
            r31.restore()
            goto L_0x0a2e
        L_0x09b8:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a2e
        L_0x09bf:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0a00
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09d3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09d0
            goto L_0x09d3
        L_0x09d0:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09d5
        L_0x09d3:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09d5:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09ec
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09e9
            goto L_0x09ec
        L_0x09e9:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09ee
        L_0x09ec:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09ee:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a2e
        L_0x0a00:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a2e
            if (r0 != r15) goto L_0x0a09
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a0b
        L_0x0a09:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a0b:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a19
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a16
            goto L_0x0a19
        L_0x0a16:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a1b
        L_0x0a19:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a1b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            if (r0 != r15) goto L_0x0a29
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a2b
        L_0x0a29:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a2b:
            r0.draw(r9)
        L_0x0a2e:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a38
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0a50
        L_0x0a38:
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
            r0.draw(r9)
        L_0x0a50:
            boolean r0 = r8.drawError
            r2 = 1085276160(0x40b00000, float:5.5)
            r3 = 1102577664(0x41b80000, float:23.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0aa9
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r6 = r8.reorderIconProgress
            float r6 = r14 - r6
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r6 = (float) r1
            int r7 = r8.errorTop
            float r7 = (float) r7
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r18
            float r1 = (float) r1
            int r15 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r15 = r15 + r3
            float r3 = (float) r15
            r0.set(r6, r7, r1, r3)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r1 * r5
            float r1 = r1 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r9.drawRoundRect(r0, r3, r1, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r9)
            goto L_0x0ebc
        L_0x0aa9:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0ae2
            boolean r6 = r8.drawMention
            if (r6 != 0) goto L_0x0ae2
            float r6 = r8.countChangeProgress
            int r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0ae2
            boolean r6 = r8.drawReactionMention
            if (r6 != 0) goto L_0x0ae2
            float r6 = r8.reactionsMentionsChangeProgress
            int r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x0ac2
            goto L_0x0ae2
        L_0x0ac2:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0ebc
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r5 = r14 - r2
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r9)
            goto L_0x0ebc
        L_0x0ae2:
            if (r0 != 0) goto L_0x0aef
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0aeb
            goto L_0x0aef
        L_0x0aeb:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0d54
        L_0x0aef:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0afc
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0afc
            float r6 = r8.countChangeProgress
            float r6 = r14 - r6
            goto L_0x0afe
        L_0x0afc:
            float r6 = r8.countChangeProgress
        L_0x0afe:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0c7a
            if (r0 != 0) goto L_0x0b06
            goto L_0x0c7a
        L_0x0b06:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b12
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b0f
            goto L_0x0b12
        L_0x0b0f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b14
        L_0x0b12:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b14:
            float r7 = r8.reorderIconProgress
            float r7 = r14 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r15 = r8.reorderIconProgress
            float r15 = r14 - r15
            float r15 = r15 * r1
            int r15 = (int) r15
            r7.setAlpha(r15)
            float r7 = r6 * r16
            int r15 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r15 <= 0) goto L_0x0b33
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0b34
        L_0x0b33:
            r15 = r7
        L_0x0b34:
            int r4 = r8.countLeft
            float r4 = (float) r4
            float r4 = r4 * r15
            int r11 = r8.countLeftOld
            float r11 = (float) r11
            float r25 = r14 - r15
            float r11 = r11 * r25
            float r4 = r4 + r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r11 = (float) r11
            float r11 = r4 - r11
            android.graphics.RectF r2 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r5 = r8.countWidth
            float r5 = (float) r5
            float r5 = r5 * r15
            float r5 = r5 + r11
            int r14 = r8.countWidthOld
            float r14 = (float) r14
            float r14 = r14 * r25
            float r5 = r5 + r14
            r14 = 1093664768(0x41300000, float:11.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r5 = r5 + r14
            int r14 = r8.countTop
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r14 = r14 + r28
            float r14 = (float) r14
            r2.set(r11, r1, r5, r14)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0b82
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r2 = r2.getInterpolation(r7)
            float r2 = r2 * r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r2 + r1
            goto L_0x0b98
        L_0x0b82:
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r7 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 - r7
            float r6 = r6 * r16
            float r6 = r1 - r6
            float r5 = r5.getInterpolation(r6)
            float r5 = r5 * r2
            float r2 = r5 + r1
        L_0x0b98:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r5 = r8.rect
            float r5 = r5.centerY()
            r9.scale(r2, r2, r1, r5)
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1094189056(0x41380000, float:11.5)
            float r6 = r2 * r5
            float r2 = r2 * r5
            r9.drawRoundRect(r1, r6, r2, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            if (r0 == 0) goto L_0x0bd1
            r31.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r4, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r31.restore()
        L_0x0bd1:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = (float) r0
            float r5 = r2 * r15
            int r5 = (int) r5
            r1.setAlpha(r5)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0c0e
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0bf0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0bf5
        L_0x0bf0:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0bf5:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r1 = r1 + r5
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r9)
            r31.restore()
            goto L_0x0c3b
        L_0x0c0e:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0c3b
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0c1e
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0CLASSNAME
        L_0x0c1e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0CLASSNAME:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r1 = r1 + r5
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r9)
            r31.restore()
        L_0x0c3b:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r2 * r25
            int r2 = (int) r2
            r1.setAlpha(r2)
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0CLASSNAME:
            float r1 = (float) r1
            float r1 = r1 * r15
            int r2 = r8.countTop
            float r2 = (float) r2
            float r1 = r1 + r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r1 = r1 + r2
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r9)
            r31.restore()
        L_0x0CLASSNAME:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r31.restore()
            goto L_0x0aeb
        L_0x0c7a:
            if (r0 != 0) goto L_0x0c7d
            goto L_0x0c7f
        L_0x0c7d:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0c7f:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0c8b
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0CLASSNAME
            goto L_0x0c8b
        L_0x0CLASSNAME:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0c8d
        L_0x0c8b:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0c8d:
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r4 = (int) r5
            r0.setAlpha(r4)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r8.reorderIconProgress
            float r5 = r2 - r5
            float r5 = r5 * r1
            int r1 = (int) r5
            r4.setAlpha(r1)
            int r1 = r8.countLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r4
            android.graphics.RectF r2 = r8.rect
            float r4 = (float) r1
            int r5 = r8.countTop
            float r5 = (float) r5
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
            r2.set(r4, r5, r1, r11)
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0d25
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x0d13
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r4 = r8.reorderIconProgress
            float r5 = r1 - r4
            r4 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r4
            int r4 = (int) r5
            r2.setAlpha(r4)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r4 = r8.pinLeft
            int r5 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r4, (int) r5)
            r31.save()
            float r5 = r1 - r6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r1 = r1.getBounds()
            int r1 = r1.centerX()
            float r1 = (float) r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerY()
            float r2 = (float) r2
            r9.scale(r5, r5, r1, r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r9)
            r31.restore()
        L_0x0d13:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerY()
            r9.scale(r6, r6, r1, r2)
        L_0x0d25:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r2 * r4
            float r2 = r2 * r4
            r9.drawRoundRect(r1, r5, r2, r0)
            if (r7 == 0) goto L_0x0d4b
            r31.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r0, r1)
            r7.draw(r9)
            r31.restore()
        L_0x0d4b:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0d54
            r31.restore()
        L_0x0d54:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0e0a
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r2 = r8.reorderIconProgress
            float r5 = r1 - r2
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r2
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r4 = r8.countTop
            float r4 = (float) r4
            int r5 = r8.mentionWidth
            int r0 = r0 + r5
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            float r0 = (float) r0
            int r5 = r8.countTop
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = r5 + r6
            float r5 = (float) r5
            r1.set(r2, r4, r0, r5)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0d96
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0d96
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0d98
        L_0x0d96:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0d98:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r2 * r4
            float r2 = r2 * r4
            r9.drawRoundRect(r1, r5, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0dd3
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            r31.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r9)
            r31.restore()
            goto L_0x0e0a
        L_0x0dd3:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r1 - r2
            int r2 = r8.countTop
            r4 = 1078774989(0x404ccccd, float:3.2)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r1, r2, r4, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r9)
        L_0x0e0a:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0e17
            float r0 = r8.reactionsMentionsChangeProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0ebc
            goto L_0x0e19
        L_0x0e17:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0e19:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r2 = r8.reorderIconProgress
            float r5 = r1 - r2
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            int r0 = r8.reactionMentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r4 = r8.countTop
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r5
            float r0 = (float) r0
            int r5 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = r5 + r3
            float r3 = (float) r5
            r1.set(r2, r4, r0, r3)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0e52
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0e52
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0e54
        L_0x0e52:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0e54:
            r31.save()
            float r1 = r8.reactionsMentionsChangeProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0e75
            boolean r3 = r8.drawReactionMention
            if (r3 == 0) goto L_0x0e64
            goto L_0x0e66
        L_0x0e64:
            float r1 = r2 - r1
        L_0x0e66:
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r1, r1, r2, r3)
        L_0x0e75:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r5 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r5, r2, r0)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            int r1 = r8.reactionMentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
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
            r0.draw(r9)
            r31.restore()
        L_0x0ebc:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0ee0
            r31.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r0 + r1
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r9.scale(r0, r0, r1, r2)
        L_0x0ee0:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0eee
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0eee
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0ef3
        L_0x0eee:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0ef3:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0f2b
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0f2b
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
            r0.draw(r9)
        L_0x0f2b:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0var_
            r31.restore()
        L_0x0var_:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x1012
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x1012
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1015
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x1015
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x1015
            boolean r0 = r30.isOnline()
            if (r0 != 0) goto L_0x0var_
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x100e
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0f6b
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
            goto L_0x0f6b
        L_0x0var_:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0f6d
        L_0x0f6b:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f6d:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0var_
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0fa7
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0fa1
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0f9f
            goto L_0x0fa1
        L_0x0f9f:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fa1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0fa7:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r1 = (float) r1
            float r2 = (float) r2
            r3 = 1088421888(0x40e00000, float:7.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.onlineProgress
            float r3 = r3 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r1, r2, r3, r4)
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
            r9.drawCircle(r1, r2, r3, r4)
            if (r0 == 0) goto L_0x0ff9
            float r0 = r8.onlineProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x100e
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 + r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x100c
            r8.onlineProgress = r1
            goto L_0x100c
        L_0x0ff9:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x100e
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x100c
            r8.onlineProgress = r1
        L_0x100c:
            r6 = 1
            goto L_0x1010
        L_0x100e:
            r6 = r20
        L_0x1010:
            r20 = r6
        L_0x1012:
            r2 = 0
            goto L_0x12f3
        L_0x1015:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x1012
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x1023
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x1023
            r6 = 1
            goto L_0x1024
        L_0x1023:
            r6 = 0
        L_0x1024:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x102f
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1012
        L_0x102f:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x1044
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x1044
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r0
            goto L_0x1046
        L_0x1044:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x1046:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x1058
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x1055
            goto L_0x1058
        L_0x1055:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x105a
        L_0x1058:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x105a:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x107d
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x1076
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1074
            goto L_0x1076
        L_0x1074:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1076:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x1094
        L_0x107d:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x108e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x108c
            goto L_0x108e
        L_0x108c:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x108e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x1094:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r8.resourcesProvider
            int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r2.setColor(r3)
            float r2 = (float) r1
            float r0 = (float) r0
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.chatCallProgress
            float r3 = r3 * r4
            float r3 = r3 * r5
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            java.lang.String r6 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            r3 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.chatCallProgress
            float r3 = r3 * r4
            float r3 = r3 * r5
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            if (r3 != 0) goto L_0x1109
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r11 = r8.innerProgress
        L_0x1101:
            float r6 = r6 * r11
            float r4 = r4 - r6
            r6 = r4
        L_0x1105:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x1206
        L_0x1109:
            r4 = 1
            if (r3 != r4) goto L_0x1130
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            float r14 = r8.innerProgress
        L_0x112b:
            float r11 = r11 * r14
            float r6 = r6 + r11
            goto L_0x1206
        L_0x1130:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 2
            if (r3 != r6) goto L_0x1153
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            goto L_0x1101
        L_0x1153:
            r4 = 3
            if (r3 != r4) goto L_0x1176
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            goto L_0x112b
        L_0x1176:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 4
            if (r3 != r6) goto L_0x119a
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            goto L_0x1101
        L_0x119a:
            r4 = 5
            if (r3 != r4) goto L_0x11be
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            goto L_0x112b
        L_0x11be:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 6
            if (r3 != r6) goto L_0x11e5
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            float r11 = r11 * r14
            float r6 = r6 - r11
            goto L_0x1105
        L_0x11e5:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r6 = r8.innerProgress
            float r4 = r4 * r6
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            goto L_0x112b
        L_0x1206:
            float r11 = r8.chatCallProgress
            int r11 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r11 < 0) goto L_0x1210
            int r11 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r11 >= 0) goto L_0x121c
        L_0x1210:
            r31.save()
            float r4 = r8.chatCallProgress
            float r11 = r4 * r5
            float r4 = r4 * r5
            r9.scale(r11, r4, r2, r0)
        L_0x121c:
            android.graphics.RectF r2 = r8.rect
            r4 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = r1 - r11
            float r11 = (float) r11
            float r14 = r0 - r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r15 = r15 + r1
            float r15 = (float) r15
            float r3 = r3 + r0
            r2.set(r11, r14, r15, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r11
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r4, r11)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r4
            float r3 = (float) r3
            float r4 = r0 - r6
            r11 = 1077936128(0x40400000, float:3.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r1 - r11
            float r11 = (float) r11
            float r0 = r0 + r6
            r2.set(r3, r4, r11, r0)
            android.graphics.RectF r2 = r8.rect
            r3 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r11
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r6, r3, r11)
            android.graphics.RectF r2 = r8.rect
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r3 = (float) r3
            r6 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 + r6
            float r1 = (float) r1
            r2.set(r3, r4, r1, r0)
            android.graphics.RectF r0 = r8.rect
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r3 = (float) r3
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r0, r2, r3, r4)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x12a3
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x12a6
        L_0x12a3:
            r31.restore()
        L_0x12a6:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x12c6
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x12c6
            r2 = 0
            r8.progressStage = r2
        L_0x12c6:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x12df
            float r0 = r8.chatCallProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x12dd
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x12dd
            r8.chatCallProgress = r2
        L_0x12dd:
            r2 = 0
            goto L_0x12f1
        L_0x12df:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x12f1
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x12f1
            r8.chatCallProgress = r2
        L_0x12f1:
            r20 = 1
        L_0x12f3:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x12fc
            r31.restore()
        L_0x12fc:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1321
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x1321
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x1321
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
        L_0x1321:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x1382
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x1345
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1335
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1335
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1345
        L_0x1335:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x133e
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x133e
            goto L_0x1345
        L_0x133e:
            r0 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1346
        L_0x1345:
            r1 = 0
        L_0x1346:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1367
            r2 = 0
            int r0 = r30.getMeasuredHeight()
            r3 = 1
            int r0 = r0 - r3
            float r0 = (float) r0
            int r4 = r30.getMeasuredWidth()
            int r4 = r4 - r1
            float r4 = (float) r4
            int r1 = r30.getMeasuredHeight()
            int r1 = r1 - r3
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r3 = r0
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x1382
        L_0x1367:
            float r2 = (float) r1
            int r0 = r30.getMeasuredHeight()
            r11 = 1
            int r0 = r0 - r11
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            int r0 = r0 - r11
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x1383
        L_0x1382:
            r11 = 1
        L_0x1383:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x13d3
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1394
            r31.restore()
            goto L_0x13d3
        L_0x1394:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r30.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x13d3:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x13e1
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x13df
            goto L_0x13e1
        L_0x13df:
            r1 = 0
            goto L_0x140c
        L_0x13e1:
            if (r0 == 0) goto L_0x13f8
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13df
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x13f6
            r8.reorderIconProgress = r1
        L_0x13f6:
            r1 = 0
            goto L_0x140a
        L_0x13f8:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x140c
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x140a
            r8.reorderIconProgress = r1
        L_0x140a:
            r6 = 1
            goto L_0x140e
        L_0x140c:
            r6 = r20
        L_0x140e:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x143c
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1468
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1425
            r8.archiveBackgroundProgress = r1
        L_0x1425:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1467
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x1467
        L_0x143c:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1468
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1451
            r8.archiveBackgroundProgress = r1
        L_0x1451:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1467
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1467:
            r6 = 1
        L_0x1468:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x147c
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x147b
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x147b:
            r6 = 1
        L_0x147c:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x14a8
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1494
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1494
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x1494:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x14c8
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x14c7
            r8.currentRevealProgress = r1
            goto L_0x14c7
        L_0x14a8:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x14b4
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x14b4:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x14c8
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14c7
            r8.currentRevealProgress = r1
        L_0x14c7:
            r6 = 1
        L_0x14c8:
            if (r6 == 0) goto L_0x14cd
            r30.invalidate()
        L_0x14cd:
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        PullForegroundDrawable pullForegroundDrawable;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!isFolderCell() || (pullForegroundDrawable = this.archivedChatsDrawable) == null || !SharedConfig.archiveHidden || pullForegroundDrawable.pullProgress != 0.0f) {
            accessibilityNodeInfo.addAction(16);
            accessibilityNodeInfo.addAction(32);
            return;
        }
        accessibilityNodeInfo.setVisibleToUser(false);
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
        int i = this.unreadCount;
        if (i > 0) {
            sb.append(LocaleController.formatPluralString("NewMessages", i));
            sb.append(". ");
        }
        MessageObject messageObject = this.message;
        if (messageObject == null || this.currentDialogFolderId != 0) {
            accessibilityEvent.setContentDescription(sb.toString());
            return;
        }
        int i2 = this.lastMessageDate;
        if (i2 == 0) {
            i2 = messageObject.messageOwner.date;
        }
        String formatDateAudio = LocaleController.formatDateAudio((long) i2, true);
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
            sb.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && !TextUtils.isEmpty(this.message.caption)) {
                sb.append(". ");
                sb.append(this.message.caption);
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

    public MessageObject getMessage() {
        return this.message;
    }
}
