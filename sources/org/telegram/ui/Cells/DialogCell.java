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
    private boolean drawCount2;
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
    /* JADX WARNING: Removed duplicated region for block: B:1011:0x17ff  */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1808  */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x182e A[Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1023:0x1837 A[ADDED_TO_REGION, Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1030:0x1850 A[Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1042:0x1882 A[Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1057:0x18d8 A[Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1058:0x18db A[Catch:{ Exception -> 0x1903 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1065:0x190e  */
    /* JADX WARNING: Removed duplicated region for block: B:1113:0x1a46  */
    /* JADX WARNING: Removed duplicated region for block: B:1149:0x1ae5 A[Catch:{ Exception -> 0x1b10 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1152:0x1b00 A[Catch:{ Exception -> 0x1b10 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1160:0x1b1c  */
    /* JADX WARNING: Removed duplicated region for block: B:1180:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x039c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x061c  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x0677  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0702  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0cfe A[SYNTHETIC, Splitter:B:571:0x0cfe] */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0d1d  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0d4b  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0d81  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0fd1  */
    /* JADX WARNING: Removed duplicated region for block: B:690:0x0fd9  */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x0fe3  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0feb  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1008  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x101c  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x109c  */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x10a1  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x10a8  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x112e  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x1186  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x118c  */
    /* JADX WARNING: Removed duplicated region for block: B:787:0x119d  */
    /* JADX WARNING: Removed duplicated region for block: B:810:0x11f9  */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x123a  */
    /* JADX WARNING: Removed duplicated region for block: B:818:0x1246  */
    /* JADX WARNING: Removed duplicated region for block: B:819:0x1256  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x126e  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x127e  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x12b7  */
    /* JADX WARNING: Removed duplicated region for block: B:839:0x12e0  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x1364  */
    /* JADX WARNING: Removed duplicated region for block: B:860:0x137a  */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x13ef A[Catch:{ Exception -> 0x140e }] */
    /* JADX WARNING: Removed duplicated region for block: B:883:0x13f2 A[Catch:{ Exception -> 0x140e }] */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x141c  */
    /* JADX WARNING: Removed duplicated region for block: B:896:0x14cc  */
    /* JADX WARNING: Removed duplicated region for block: B:903:0x1580  */
    /* JADX WARNING: Removed duplicated region for block: B:909:0x15a5  */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x15d5  */
    /* JADX WARNING: Removed duplicated region for block: B:968:0x1744  */
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
            r24 = 1099956224(0x41900000, float:18.0)
            r4 = 2
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
            r0 = 2131625931(0x7f0e07cb, float:1.8879084E38)
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
            goto L_0x11f7
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
            goto L_0x0fdf
        L_0x0702:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            r2 = 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x07a3
            r0 = 2131625439(0x7f0e05df, float:1.8878086E38)
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
            r0 = 2131626037(0x7f0e0835, float:1.8879299E38)
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
            goto L_0x0fdf
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
            r0 = 2131625542(0x7f0e0646, float:1.8878295E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07b6
        L_0x07db:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x07f4
            r0 = 2131624575(0x7f0e027f, float:1.8876334E38)
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
            r0 = 2131625543(0x7f0e0647, float:1.8878297E38)
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
            r0 = 2131625531(0x7f0e063b, float:1.8878273E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r5 = 0
            r2[r5] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x07b6
        L_0x082b:
            r0 = 2131625530(0x7f0e063a, float:1.887827E38)
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
            r0 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
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
            r1.drawCount2 = r6
            int r7 = r1.dialogsType
            if (r7 != r4) goto L_0x0914
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x090f
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x08d0
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x08d0
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x08ac
            java.lang.String r0 = "Subscribers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0910
        L_0x08ac:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x08c2
            r0 = 2131624873(0x7f0e03a9, float:1.8876938E38)
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0910
        L_0x08c2:
            r0 = 2131624876(0x7f0e03ac, float:1.8876944E38)
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0910
        L_0x08d0:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            int r2 = r0.participants_count
            if (r2 == 0) goto L_0x08dd
            java.lang.String r0 = "Members"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralStringComma(r0, r2)
            goto L_0x0910
        L_0x08dd:
            boolean r2 = r0.has_geo
            if (r2 == 0) goto L_0x08eb
            r0 = 2131626431(0x7f0e09bf, float:1.8880098E38)
            java.lang.String r2 = "MegaLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0910
        L_0x08eb:
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0901
            r0 = 2131626432(0x7f0e09c0, float:1.88801E38)
            java.lang.String r2 = "MegaPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0910
        L_0x0901:
            r0 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            java.lang.String r2 = "MegaPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0910
        L_0x090f:
            r0 = r9
        L_0x0910:
            r2 = 0
            r1.drawCount2 = r2
            goto L_0x0928
        L_0x0914:
            r8 = 3
            if (r7 != r8) goto L_0x0931
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r7 == 0) goto L_0x0931
            r0 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0928:
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 1
            r5 = 0
            r7 = 0
            r11 = 0
            goto L_0x0fd5
        L_0x0931:
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0949
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 != 0) goto L_0x0949
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x0949
            java.lang.CharSequence r0 = r40.formatArchivedDialogNames()
            r3 = r0
            r0 = 1
            r2 = 2
        L_0x0944:
            r4 = 0
        L_0x0945:
            r5 = 0
            r7 = 0
            goto L_0x0fd5
        L_0x0949:
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0970
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 == 0) goto L_0x0965
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0965
            r15 = r9
            r11 = 0
        L_0x0965:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r3 = r15
            r0 = 1
            r2 = 2
        L_0x096e:
            r4 = 1
            goto L_0x0945
        L_0x0970:
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 == 0) goto L_0x0a71
            int r7 = r1.currentDialogFolderId
            if (r7 != 0) goto L_0x0a71
            org.telegram.tgnet.TLRPC$EncryptedChat r7 = r1.encryptedChat
            if (r7 != 0) goto L_0x0a71
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.needDrawBluredPreview()
            if (r7 != 0) goto L_0x0a71
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isPhoto()
            if (r7 != 0) goto L_0x099e
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isNewGif()
            if (r7 != 0) goto L_0x099e
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isVideo()
            if (r7 == 0) goto L_0x0a71
        L_0x099e:
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isWebpage()
            if (r7 == 0) goto L_0x09b1
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
            java.lang.String r7 = r7.type
            goto L_0x09b2
        L_0x09b1:
            r7 = 0
        L_0x09b2:
            java.lang.String r8 = "app"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x0a71
            java.lang.String r8 = "profile"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x0a71
            java.lang.String r8 = "article"
            boolean r8 = r8.equals(r7)
            if (r8 != 0) goto L_0x0a71
            if (r7 == 0) goto L_0x09d4
            java.lang.String r8 = "telegram_"
            boolean r7 = r7.startsWith(r8)
            if (r7 != 0) goto L_0x0a71
        L_0x09d4:
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            int r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r12)
            if (r7 != r8) goto L_0x09ed
            r8 = 0
        L_0x09ed:
            if (r7 == 0) goto L_0x0a71
            r1.hasMessageThumb = r6
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            r1.drawPlay = r12
            java.lang.String r12 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.mediaExists
            if (r2 != 0) goto L_0x0a3c
            int r2 = r1.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r2 = r2.canDownloadMedia((org.telegram.messenger.MessageObject) r4)
            if (r2 != 0) goto L_0x0a3c
            int r2 = r1.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r12)
            if (r2 == 0) goto L_0x0a1e
            goto L_0x0a3c
        L_0x0a1e:
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
            goto L_0x0a6f
        L_0x0a3c:
            org.telegram.messenger.MessageObject r2 = r1.message
            int r4 = r2.type
            if (r4 != r6) goto L_0x0a4c
            if (r8 == 0) goto L_0x0a48
            int r4 = r8.size
            r12 = r4
            goto L_0x0a49
        L_0x0a48:
            r12 = 0
        L_0x0a49:
            r34 = r12
            goto L_0x0a4e
        L_0x0a4c:
            r34 = 0
        L_0x0a4e:
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
        L_0x0a6f:
            r2 = 0
            goto L_0x0a72
        L_0x0a71:
            r2 = 1
        L_0x0a72:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x0d78
            long r7 = r4.id
            r29 = 0
            int r12 = (r7 > r29 ? 1 : (r7 == r29 ? 0 : -1))
            if (r12 <= 0) goto L_0x0d78
            if (r5 != 0) goto L_0x0d78
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x0a8e
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isMegagroup(r4)
            if (r4 == 0) goto L_0x0d78
        L_0x0a8e:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isOutOwner()
            if (r4 == 0) goto L_0x0aa0
            r3 = 2131625931(0x7f0e07cb, float:1.8879084E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0ae7
        L_0x0aa0:
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x0ab0
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from
            if (r4 == 0) goto L_0x0ab0
            java.lang.String r4 = r4.from_name
            if (r4 == 0) goto L_0x0ab0
            r3 = r4
            goto L_0x0ae7
        L_0x0ab0:
            if (r3 == 0) goto L_0x0ae5
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0ac6
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0abb
            goto L_0x0ac6
        L_0x0abb:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x0ae7
        L_0x0ac6:
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r4 == 0) goto L_0x0ad6
            r3 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0ae7
        L_0x0ad6:
            java.lang.String r4 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r4, r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r9)
            goto L_0x0ae7
        L_0x0ae5:
            java.lang.String r3 = "DELETED"
        L_0x0ae7:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x0b00
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r4 = 0
            r2[r4] = r0
            r2[r6] = r3
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0afd:
            r2 = r0
            goto L_0x0cec
        L_0x0b00:
            r4 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0b75
            java.lang.String r0 = r5.toString()
            int r5 = r0.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r5 <= r7) goto L_0x0b17
            java.lang.CharSequence r0 = r0.subSequence(r4, r7)
        L_0x0b17:
            if (r2 != 0) goto L_0x0b1b
            r2 = r9
            goto L_0x0b49
        L_0x0b1b:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0b26
            java.lang.String r2 = "📹 "
            goto L_0x0b49
        L_0x0b26:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0b31
            java.lang.String r2 = "🎤 "
            goto L_0x0b49
        L_0x0b31:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0b3c
            java.lang.String r2 = "🎧 "
            goto L_0x0b49
        L_0x0b3c:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x0b47
            java.lang.String r2 = "🖼 "
            goto L_0x0b49
        L_0x0b47:
            java.lang.String r2 = "📎 "
        L_0x0b49:
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
            goto L_0x0afd
        L_0x0b75:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0CLASSNAME
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0CLASSNAME
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0bbb
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0baa
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0c2e
        L_0x0baa:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r4] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0c2e
        L_0x0bbb:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0be5
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0bd5
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r4 = 0
            r0[r4] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0c2e
        L_0x0bd5:
            r4 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r4] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0c2e
        L_0x0be5:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0bec
            java.lang.String r0 = r2.title
            goto L_0x0c2e
        L_0x0bec:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0c2a
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r2 < r4) goto L_0x0CLASSNAME
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
            goto L_0x0c2e
        L_0x0CLASSNAME:
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
            goto L_0x0c2e
        L_0x0c2a:
            java.lang.String r0 = r15.toString()
        L_0x0c2e:
            r2 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r2, r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r4 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0CLASSNAME }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0CLASSNAME }
            if (r14 == 0) goto L_0x0CLASSNAME
            int r4 = r3.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            r5 = 2
            int r4 = r4 + r5
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = 0
        L_0x0CLASSNAME:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            r7 = 33
            r2.setSpan(r0, r4, r5, r7)     // Catch:{ Exception -> 0x0CLASSNAME }
            goto L_0x0cec
        L_0x0CLASSNAME:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0cec
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0ce6
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0cbc
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0c7b
            r2 = r0
        L_0x0c7b:
            int r0 = r40.getMeasuredWidth()
            r4 = 1121058816(0x42d20000, float:105.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            if (r14 == 0) goto L_0x0ca2
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
        L_0x0ca2:
            if (r0 <= 0) goto L_0x0cba
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r0, r10, r7)
            java.lang.String r2 = r0.toString()
            goto L_0x0ccd
        L_0x0cba:
            r5 = 0
            goto L_0x0ccd
        L_0x0cbc:
            r5 = 0
            int r0 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r0 <= r4) goto L_0x0cc9
            java.lang.CharSequence r2 = r2.subSequence(r5, r4)
        L_0x0cc9:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r2)
        L_0x0ccd:
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
            goto L_0x0afd
        L_0x0ce6:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r9)
            goto L_0x0afd
        L_0x0cec:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0cf4
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0cfe
        L_0x0cf4:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0d1d
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0d1d
        L_0x0cfe:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0d16 }
            java.lang.String r4 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0d16 }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0d16 }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0d16 }
            int r4 = r4 + r6
            r5 = 33
            r7 = 0
            r2.setSpan(r0, r7, r4, r5)     // Catch:{ Exception -> 0x0d14 }
            r0 = r4
            goto L_0x0d1f
        L_0x0d14:
            r0 = move-exception
            goto L_0x0d18
        L_0x0d16:
            r0 = move-exception
            r4 = 0
        L_0x0d18:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0d1f
        L_0x0d1d:
            r0 = 0
            r4 = 0
        L_0x0d1f:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r8 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r7, r8)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0d47
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r5 == 0) goto L_0x0d47
            r2 = r5
        L_0x0d47:
            boolean r5 = r1.hasMessageThumb
            if (r5 == 0) goto L_0x0d70
            boolean r5 = r2 instanceof android.text.SpannableStringBuilder
            if (r5 != 0) goto L_0x0d55
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            r2 = r5
        L_0x0d55:
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
        L_0x0d70:
            r5 = r0
            r7 = r3
            r0 = 1
            r4 = 0
            r3 = r2
            r2 = 2
            goto L_0x0fd5
        L_0x0d78:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0d81
        L_0x0d7e:
            r2 = 2
            goto L_0x0var_
        L_0x0d81:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0d9f
            org.telegram.tgnet.TLRPC$Photo r4 = r3.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0d9f
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d9f
            r0 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0d7e
        L_0x0d9f:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0db7
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0db7
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0db7
            r0 = 2131624450(0x7f0e0202, float:1.887608E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0d7e
        L_0x0db7:
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0e6b
            if (r2 != 0) goto L_0x0dbf
            r0 = r9
            goto L_0x0deb
        L_0x0dbf:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0dc8
            java.lang.String r0 = "📹 "
            goto L_0x0deb
        L_0x0dc8:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0dd3
            java.lang.String r0 = "🎤 "
            goto L_0x0deb
        L_0x0dd3:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0dde
            java.lang.String r0 = "🎧 "
            goto L_0x0deb
        L_0x0dde:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0de9
            java.lang.String r0 = "🖼 "
            goto L_0x0deb
        L_0x0de9:
            java.lang.String r0 = "📎 "
        L_0x0deb:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0e4a
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0e4a
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r40.getMeasuredWidth()
            r4 = 1122893824(0x42ee0000, float:119.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r14 == 0) goto L_0x0e22
            r4 = 0
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x0e21
            float r3 = (float) r3
            java.lang.String r5 = ": "
            float r5 = r10.measureText(r5)
            float r3 = r3 - r5
            int r3 = (int) r3
            goto L_0x0e22
        L_0x0e21:
            throw r4
        L_0x0e22:
            if (r3 <= 0) goto L_0x0e39
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r5 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r3, r10, r5)
            java.lang.String r2 = r2.toString()
        L_0x0e39:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0d7e
        L_0x0e4a:
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
            goto L_0x0d7e
        L_0x0e6b:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0e89
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0e86:
            r2 = 2
            goto L_0x0f1c
        L_0x0e89:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0ea9
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
            goto L_0x0e86
        L_0x0ea9:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0eb0
            java.lang.String r0 = r3.title
            goto L_0x0e86
        L_0x0eb0:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0ecf
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
            goto L_0x0f1c
        L_0x0ecf:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0var_
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
            goto L_0x0var_
        L_0x0var_:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r3, (android.text.Spannable) r0, (int) r4)
        L_0x0var_:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
        L_0x0f1c:
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
            if (r3 == 0) goto L_0x0fd1
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0var_
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
            goto L_0x0var_
        L_0x0var_:
            r5 = 0
            int r3 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0f7d
            java.lang.CharSequence r0 = r0.subSequence(r5, r4)
        L_0x0f7d:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0var_:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0f8b
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0f8b:
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
            if (r4 == 0) goto L_0x0fcd
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x0fcd
            goto L_0x0fce
        L_0x0fcd:
            r3 = r0
        L_0x0fce:
            r0 = 1
            goto L_0x0944
        L_0x0fd1:
            r3 = r0
            r0 = 1
            goto L_0x096e
        L_0x0fd5:
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x07bb
            java.lang.CharSequence r7 = r40.formatArchivedDialogNames()
            goto L_0x07bb
        L_0x0fdf:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            if (r8 == 0) goto L_0x0feb
            int r8 = r8.date
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x1004
        L_0x0feb:
            int r8 = r1.lastMessageDate
            if (r8 == 0) goto L_0x0ff5
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x1004
        L_0x0ff5:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x1003
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.date
            long r13 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x1004
        L_0x1003:
            r8 = r9
        L_0x1004:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x101c
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
            goto L_0x1122
        L_0x101c:
            r12 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x105e
            int r13 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r13 + r14
            if (r15 <= 0) goto L_0x1055
            if (r13 <= r14) goto L_0x103f
            r1.drawCount = r6
            r1.drawMention = r12
            java.lang.Object[] r15 = new java.lang.Object[r6]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15[r12] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r15)
            goto L_0x105a
        L_0x103f:
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
            goto L_0x105b
        L_0x1055:
            r1.drawCount = r12
            r1.drawMention = r12
            r13 = 0
        L_0x105a:
            r14 = 0
        L_0x105b:
            r1.drawReactionMention = r12
            goto L_0x10ad
        L_0x105e:
            boolean r14 = r1.clearingDialog
            if (r14 == 0) goto L_0x1068
            r1.drawCount = r12
            r11 = 0
            r12 = 0
        L_0x1066:
            r13 = 0
            goto L_0x1098
        L_0x1068:
            int r14 = r1.unreadCount
            if (r14 == 0) goto L_0x108c
            if (r14 != r6) goto L_0x107a
            int r15 = r1.mentionCount
            if (r14 != r15) goto L_0x107a
            if (r13 == 0) goto L_0x107a
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x108c
        L_0x107a:
            r1.drawCount = r6
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r12 = 0
            r13[r12] = r14
            java.lang.String r14 = "%d"
            java.lang.String r13 = java.lang.String.format(r14, r13)
            goto L_0x1098
        L_0x108c:
            r12 = 0
            boolean r13 = r1.markUnread
            if (r13 == 0) goto L_0x1095
            r1.drawCount = r6
            r13 = r9
            goto L_0x1098
        L_0x1095:
            r1.drawCount = r12
            goto L_0x1066
        L_0x1098:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x10a1
            r1.drawMention = r6
            java.lang.String r14 = "@"
            goto L_0x10a4
        L_0x10a1:
            r1.drawMention = r12
            r14 = 0
        L_0x10a4:
            int r15 = r1.reactionMentionCount
            if (r15 <= 0) goto L_0x10ab
            r1.drawReactionMention = r6
            goto L_0x10ad
        L_0x10ab:
            r1.drawReactionMention = r12
        L_0x10ad:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x1119
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x1119
            if (r11 == 0) goto L_0x1119
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x1119
            boolean r11 = r11.isSending()
            if (r11 == 0) goto L_0x10d5
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r6
            r1.drawError = r11
            goto L_0x1122
        L_0x10d5:
            r11 = 0
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isSendError()
            if (r12 == 0) goto L_0x10eb
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r6
            r1.drawCount = r11
            r1.drawMention = r11
            goto L_0x1122
        L_0x10eb:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isSent()
            if (r11 == 0) goto L_0x1117
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isUnread()
            if (r11 == 0) goto L_0x110c
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x110a
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x110a
            goto L_0x110c
        L_0x110a:
            r11 = 0
            goto L_0x110d
        L_0x110c:
            r11 = 1
        L_0x110d:
            r1.drawCheck1 = r11
            r1.drawCheck2 = r6
            r11 = 0
            r1.drawClock = r11
            r1.drawError = r11
            goto L_0x1122
        L_0x1117:
            r11 = 0
            goto L_0x1122
        L_0x1119:
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r11
        L_0x1122:
            r1.promoDialog = r11
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r15 = r1.dialogsType
            if (r15 != 0) goto L_0x1186
            r15 = r3
            long r2 = r1.currentDialogId
            boolean r2 = r11.isPromoDialog(r2, r6)
            if (r2 == 0) goto L_0x1187
            r1.drawPinBackground = r6
            r1.promoDialog = r6
            int r2 = r11.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x114c
            r2 = 2131628557(0x7f0e120d, float:1.888441E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x114a:
            r8 = r2
            goto L_0x1187
        L_0x114c:
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x1187
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r11.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((java.lang.String) r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x1176
            r2 = 2131627570(0x7f0e0e32, float:1.8882408E38)
            java.lang.String r3 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x1176:
            java.lang.String r3 = r11.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x114a
            java.lang.String r3 = r11.promoPsaMessage
            r8 = 0
            r1.hasMessageThumb = r8
            r8 = r2
            r2 = r3
            goto L_0x1188
        L_0x1186:
            r15 = r3
        L_0x1187:
            r2 = r15
        L_0x1188:
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x119d
            r3 = 2131624349(0x7f0e019d, float:1.8875875E38)
            java.lang.String r11 = "ArchivedChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
        L_0x1195:
            r11 = r16
            r39 = r4
            r4 = r3
            r3 = r39
            goto L_0x11f7
        L_0x119d:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x11a4
            java.lang.String r3 = r3.title
            goto L_0x11e7
        L_0x11a4:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x11e6
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x11b8
            r3 = 2131627724(0x7f0e0ecc, float:1.888272E38)
            java.lang.String r11 = "RepliesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x11e7
        L_0x11b8:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x11df
            boolean r3 = r1.useMeForMyMessages
            if (r3 == 0) goto L_0x11ce
            r3 = 2131625931(0x7f0e07cb, float:1.8879084E38)
            java.lang.String r11 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x11e7
        L_0x11ce:
            int r3 = r1.dialogsType
            r11 = 3
            if (r3 != r11) goto L_0x11d5
            r1.drawPinBackground = r6
        L_0x11d5:
            r3 = 2131627865(0x7f0e0var_, float:1.8883006E38)
            java.lang.String r11 = "SavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x11e7
        L_0x11df:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x11e7
        L_0x11e6:
            r3 = r9
        L_0x11e7:
            int r11 = r3.length()
            if (r11 != 0) goto L_0x1195
            r3 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r11 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x1195
        L_0x11f7:
            if (r0 == 0) goto L_0x123a
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
            if (r0 != 0) goto L_0x1230
            int r0 = r40.getMeasuredWidth()
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            int r0 = r0 - r12
            r1.timeLeft = r0
            goto L_0x1238
        L_0x1230:
            r8 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeLeft = r0
        L_0x1238:
            r0 = r12
            goto L_0x1242
        L_0x123a:
            r15 = r13
            r8 = 0
            r1.timeLayout = r8
            r8 = 0
            r1.timeLeft = r8
            r0 = 0
        L_0x1242:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x1256
            int r8 = r40.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r8 = r8 - r13
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r8 = r8 - r13
            int r8 = r8 - r0
            goto L_0x126a
        L_0x1256:
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
        L_0x126a:
            boolean r13 = r1.drawNameLock
            if (r13 == 0) goto L_0x127e
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
        L_0x127a:
            int r13 = r13 + r16
            int r8 = r8 - r13
            goto L_0x12b1
        L_0x127e:
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x128f
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x127a
        L_0x128f:
            boolean r13 = r1.drawNameBroadcast
            if (r13 == 0) goto L_0x12a0
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x127a
        L_0x12a0:
            boolean r13 = r1.drawNameBot
            if (r13 == 0) goto L_0x12b1
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r16 = r16.getIntrinsicWidth()
            goto L_0x127a
        L_0x12b1:
            boolean r13 = r1.drawClock
            r16 = 1084227584(0x40a00000, float:5.0)
            if (r13 == 0) goto L_0x12e0
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r13 = r13 + r18
            int r8 = r8 - r13
            boolean r18 = org.telegram.messenger.LocaleController.isRTL
            if (r18 != 0) goto L_0x12cf
            int r0 = r1.timeLeft
            int r0 = r0 - r13
            r1.clockDrawLeft = r0
            goto L_0x1356
        L_0x12cf:
            int r12 = r1.timeLeft
            int r12 = r12 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r12 = r12 + r0
            r1.clockDrawLeft = r12
            int r0 = r1.nameLeft
            int r0 = r0 + r13
            r1.nameLeft = r0
            goto L_0x1356
        L_0x12e0:
            boolean r12 = r1.drawCheck2
            if (r12 == 0) goto L_0x1356
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r12 = r12.getIntrinsicWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r12 = r12 + r13
            int r8 = r8 - r12
            boolean r13 = r1.drawCheck1
            if (r13 == 0) goto L_0x133d
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            r18 = 1090519040(0x41000000, float:8.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r13 = r13 - r18
            int r8 = r8 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x1316
            int r0 = r1.timeLeft
            int r0 = r0 - r12
            r1.halfCheckDrawLeft = r0
            r12 = 1085276160(0x40b00000, float:5.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r12
            r1.checkDrawLeft = r0
            goto L_0x1356
        L_0x1316:
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
            goto L_0x1356
        L_0x133d:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x1347
            int r0 = r1.timeLeft
            int r0 = r0 - r12
            r1.checkDrawLeft1 = r0
            goto L_0x1356
        L_0x1347:
            int r13 = r1.timeLeft
            int r13 = r13 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r13 = r13 + r0
            r1.checkDrawLeft1 = r13
            int r0 = r1.nameLeft
            int r0 = r0 + r12
            r1.nameLeft = r0
        L_0x1356:
            boolean r0 = r1.dialogMuted
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x137a
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x137a
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x137a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x13b4
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
            goto L_0x13b4
        L_0x137a:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1394
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x13b4
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
            goto L_0x13b4
        L_0x1394:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x13b4
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r1.drawScam
            if (r12 != r6) goto L_0x13a3
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x13a5
        L_0x13a3:
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x13a5:
            int r12 = r12.getIntrinsicWidth()
            int r0 = r0 + r12
            int r8 = r8 - r0
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x13b4
            int r12 = r1.nameLeft
            int r12 = r12 + r0
            r1.nameLeft = r12
        L_0x13b4:
            r16 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r8 = java.lang.Math.max(r0, r8)
            r0 = 10
            r12 = 32
            java.lang.String r0 = r4.replace(r0, r12)     // Catch:{ Exception -> 0x140e }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x140e }
            int r12 = r1.paintIndex     // Catch:{ Exception -> 0x140e }
            r4 = r4[r12]     // Catch:{ Exception -> 0x140e }
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x140e }
            int r12 = r8 - r12
            float r12 = (float) r12     // Catch:{ Exception -> 0x140e }
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x140e }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r4, r12, r13)     // Catch:{ Exception -> 0x140e }
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x140e }
            if (r4 == 0) goto L_0x13f2
            boolean r4 = r4.hasHighlightedWords()     // Catch:{ Exception -> 0x140e }
            if (r4 == 0) goto L_0x13f2
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x140e }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x140e }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x140e }
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x140e }
            if (r4 == 0) goto L_0x13f2
            r30 = r4
            goto L_0x13f4
        L_0x13f2:
            r30 = r0
        L_0x13f4:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x140e }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x140e }
            int r12 = r1.paintIndex     // Catch:{ Exception -> 0x140e }
            r31 = r4[r12]     // Catch:{ Exception -> 0x140e }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x140e }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r32 = r8
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x140e }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x140e }
            goto L_0x1412
        L_0x140e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1412:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x14cc
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x141c
            goto L_0x14cc
        L_0x141c:
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
            if (r12 == 0) goto L_0x147e
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
            goto L_0x1493
        L_0x147e:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r12
            r1.messageLeft = r12
            r12 = 1092616192(0x41200000, float:10.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r13 = 1116078080(0x42860000, float:67.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r12
        L_0x1493:
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
            goto L_0x157c
        L_0x14cc:
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
            if (r6 == 0) goto L_0x1535
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
            goto L_0x154a
        L_0x1535:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r6
        L_0x154a:
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
        L_0x157c:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x15a1
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1599
            int r0 = r40.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x15a1
        L_0x1599:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x15a1:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x15d5
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x15bf
            int r0 = r40.getMeasuredWidth()
            r5 = 1107820544(0x42080000, float:34.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.errorLeft = r0
            goto L_0x15d1
        L_0x15bf:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x15d1:
            r0 = r17
            goto L_0x1742
        L_0x15d5:
            if (r15 != 0) goto L_0x1605
            if (r14 != 0) goto L_0x1605
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x15de
            goto L_0x1605
        L_0x15de:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x15ff
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x15ff
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x15ff:
            r5 = 0
            r1.drawCount = r5
            r1.drawMention = r5
            goto L_0x15d1
        L_0x1605:
            if (r15 == 0) goto L_0x1667
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
            if (r5 != 0) goto L_0x1653
            int r0 = r40.getMeasuredWidth()
            int r5 = r1.countWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r5
            r1.countLeft = r0
            goto L_0x1663
        L_0x1653:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x1663:
            r5 = 1
            r1.drawCount = r5
            goto L_0x166a
        L_0x1667:
            r5 = 0
            r1.countWidth = r5
        L_0x166a:
            if (r14 == 0) goto L_0x16ed
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x16a0
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
            goto L_0x16a6
        L_0x16a0:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.mentionWidth = r0
        L_0x16a6:
            int r0 = r1.mentionWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x16ce
            int r0 = r40.getMeasuredWidth()
            int r5 = r1.mentionWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x16c9
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x16ca
        L_0x16c9:
            r5 = 0
        L_0x16ca:
            int r0 = r0 - r5
            r1.mentionLeft = r0
            goto L_0x16ea
        L_0x16ce:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x16dc
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r6 + r9
            goto L_0x16dd
        L_0x16dc:
            r6 = 0
        L_0x16dd:
            int r5 = r5 + r6
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x16ea:
            r5 = 1
            r1.drawMention = r5
        L_0x16ed:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x15d1
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1724
            int r0 = r40.getMeasuredWidth()
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            int r5 = r1.mentionWidth
            if (r5 == 0) goto L_0x1712
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x1713
        L_0x1712:
            r5 = 0
        L_0x1713:
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x171e
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x171f
        L_0x171e:
            r5 = 0
        L_0x171f:
            int r0 = r0 - r5
            r1.reactionMentionLeft = r0
            goto L_0x15d1
        L_0x1724:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x1732
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r6 + r9
            goto L_0x1733
        L_0x1732:
            r6 = 0
        L_0x1733:
            int r5 = r5 + r6
            r1.reactionMentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
            goto L_0x15d1
        L_0x1742:
            if (r3 == 0) goto L_0x178e
            if (r2 != 0) goto L_0x1749
            r9 = r22
            goto L_0x174a
        L_0x1749:
            r9 = r2
        L_0x174a:
            int r2 = r9.length()
            r3 = 150(0x96, float:2.1E-43)
            if (r2 <= r3) goto L_0x1757
            r2 = 0
            java.lang.CharSequence r9 = r9.subSequence(r2, r3)
        L_0x1757:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x175f
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x1761
        L_0x175f:
            if (r7 == 0) goto L_0x1766
        L_0x1761:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            goto L_0x176a
        L_0x1766:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r9)
        L_0x176a:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r5 = 1099431936(0x41880000, float:17.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r6)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x178e
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x178e
            r2 = r3
        L_0x178e:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = java.lang.Math.max(r3, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x179e
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x17f5
        L_0x179e:
            if (r7 == 0) goto L_0x17f5
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x17a9
            int r5 = r1.currentDialogFolderDialogsCount
            r6 = 1
            if (r5 != r6) goto L_0x17f5
        L_0x17a9:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x17db }
            if (r0 == 0) goto L_0x17c0
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x17db }
            if (r0 == 0) goto L_0x17c0
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x17db }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x17db }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x17db }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r7, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)     // Catch:{ Exception -> 0x17db }
            if (r0 == 0) goto L_0x17c0
            r7 = r0
        L_0x17c0:
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x17db }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x17db }
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x17db }
            r38 = 1
            r29 = r7
            r31 = r3
            r37 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x17db }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x17db }
            goto L_0x17df
        L_0x17db:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x17df:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            r0.setImageY(r4)
            goto L_0x181d
        L_0x17f5:
            r5 = 0
            r1.messageNameLayout = r5
            if (r0 != 0) goto L_0x1808
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x17ff
            goto L_0x1808
        L_0x17ff:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x181d
        L_0x1808:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1101529088(0x41a80000, float:21.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x181d:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1903 }
            if (r0 != 0) goto L_0x1825
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1903 }
            if (r4 == 0) goto L_0x1837
        L_0x1825:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1903 }
            if (r4 == 0) goto L_0x1837
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1903 }
            r5 = 1
            if (r4 <= r5) goto L_0x1837
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1903 }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x1903 }
            r10 = r0[r2]     // Catch:{ Exception -> 0x1903 }
            r2 = r7
            r7 = 0
            goto L_0x184c
        L_0x1837:
            if (r0 != 0) goto L_0x183d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x183f
        L_0x183d:
            if (r7 == 0) goto L_0x184c
        L_0x183f:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x1903 }
            int r0 = r3 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1903 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1903 }
            java.lang.CharSequence r2 = android.text.TextUtils.ellipsize(r2, r10, r0, r4)     // Catch:{ Exception -> 0x1903 }
        L_0x184c:
            boolean r0 = r2 instanceof android.text.Spannable     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x187e
            r0 = r2
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x1903 }
            int r4 = r0.length()     // Catch:{ Exception -> 0x1903 }
            java.lang.Class<android.text.style.CharacterStyle> r5 = android.text.style.CharacterStyle.class
            r6 = 0
            java.lang.Object[] r4 = r0.getSpans(r6, r4, r5)     // Catch:{ Exception -> 0x1903 }
            android.text.style.CharacterStyle[] r4 = (android.text.style.CharacterStyle[]) r4     // Catch:{ Exception -> 0x1903 }
            int r5 = r4.length     // Catch:{ Exception -> 0x1903 }
            r6 = 0
        L_0x1862:
            if (r6 >= r5) goto L_0x187e
            r9 = r4[r6]     // Catch:{ Exception -> 0x1903 }
            boolean r13 = r9 instanceof android.text.style.ClickableSpan     // Catch:{ Exception -> 0x1903 }
            if (r13 != 0) goto L_0x1878
            boolean r13 = r9 instanceof android.text.style.StyleSpan     // Catch:{ Exception -> 0x1903 }
            if (r13 == 0) goto L_0x187b
            r13 = r9
            android.text.style.StyleSpan r13 = (android.text.style.StyleSpan) r13     // Catch:{ Exception -> 0x1903 }
            int r13 = r13.getStyle()     // Catch:{ Exception -> 0x1903 }
            r14 = 1
            if (r13 != r14) goto L_0x187b
        L_0x1878:
            r0.removeSpan(r9)     // Catch:{ Exception -> 0x1903 }
        L_0x187b:
            int r6 = r6 + 1
            goto L_0x1862
        L_0x187e:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1903 }
            if (r0 != 0) goto L_0x18ba
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x1887
            goto L_0x18ba
        L_0x1887:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x18a2
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1903 }
            int r0 = r0 + r11
            int r3 = r3 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x18a2
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x1903 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1903 }
            int r4 = r11 + r5
            int r0 = r0 - r4
            r1.messageLeft = r0     // Catch:{ Exception -> 0x1903 }
        L_0x18a2:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1903 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1903 }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r2
            r31 = r10
            r32 = r3
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x1903 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1903 }
            goto L_0x18ed
        L_0x18ba:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1903 }
            if (r0 == 0) goto L_0x18c7
            if (r7 == 0) goto L_0x18c7
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1903 }
            int r3 = r3 + r0
        L_0x18c7:
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1903 }
            r33 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x1903 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x1903 }
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1903 }
            if (r7 == 0) goto L_0x18db
            r38 = 1
            goto L_0x18dd
        L_0x18db:
            r38 = 2
        L_0x18dd:
            r29 = r2
            r30 = r10
            r31 = r3
            r34 = r0
            r37 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x1903 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1903 }
        L_0x18ed:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x1903 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilers     // Catch:{ Exception -> 0x1903 }
            r0.addAll(r2)     // Catch:{ Exception -> 0x1903 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilers     // Catch:{ Exception -> 0x1903 }
            r0.clear()     // Catch:{ Exception -> 0x1903 }
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1903 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x1903 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilers     // Catch:{ Exception -> 0x1903 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r4)     // Catch:{ Exception -> 0x1903 }
            goto L_0x190a
        L_0x1903:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x190a:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1a46
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x19cf
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19cf
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x195c
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x195c
            int r2 = r1.drawScam
            if (r2 != 0) goto L_0x195c
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
            goto L_0x19b7
        L_0x195c:
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x1986
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
            goto L_0x19b7
        L_0x1986:
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x19b7
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
            if (r2 != r9) goto L_0x19a9
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x19ab
        L_0x19a9:
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x19ab:
            int r2 = r2.getIntrinsicWidth()
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r6 = r6 - r9
            int r2 = (int) r6
            r1.nameMuteLeft = r2
        L_0x19b7:
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x19cf
            double r6 = (double) r8
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x19cf
            int r0 = r1.nameLeft
            double r8 = (double) r0
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r6
            int r0 = (int) r8
            r1.nameLeft = r0
        L_0x19cf:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a10
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a10
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x19e0:
            if (r2 >= r0) goto L_0x1a06
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r2)
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 != 0) goto L_0x1a05
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
            goto L_0x19e0
        L_0x1a05:
            r4 = 0
        L_0x1a06:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x1a10
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x1a10:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1ad0
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1ad0
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1ad0
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r2 = (double) r3
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x1ad0
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r2
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x1ad0
        L_0x1a46:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1a95
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1a95
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r8
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1a7a
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r8
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1a7a
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1a7a:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1a86
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1a86
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1a95
        L_0x1a86:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1a95:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1ab8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1ab8
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1aa2:
            if (r3 >= r0) goto L_0x1ab1
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1aa2
        L_0x1ab1:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1ab8:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1ad0
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1ad0
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1ad0:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1b14
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1b14
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1b10 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1b10 }
            r5 = r23
            r2 = 1
            if (r5 < r0) goto L_0x1ae7
            int r5 = r0 + -1
        L_0x1ae7:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1b10 }
            float r0 = r0.getPrimaryHorizontal(r5)     // Catch:{ Exception -> 0x1b10 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1b10 }
            int r5 = r5 + r2
            float r2 = r3.getPrimaryHorizontal(r5)     // Catch:{ Exception -> 0x1b10 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1b10 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1b10 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1b10 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1b10 }
            if (r0 == 0) goto L_0x1b07
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1b10 }
            int r0 = r0 + r2
        L_0x1b07:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1b10 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1b10 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1b10 }
            goto L_0x1b14
        L_0x1b10:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1b14:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1b62
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1b62
            if (r25 < 0) goto L_0x1b39
            int r11 = r25 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r11 >= r0) goto L_0x1b39
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r25
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r11)
            goto L_0x1b47
        L_0x1b39:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1b47:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1b53
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1b62
        L_0x1b53:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1b62:
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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0265  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0573  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x057c  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01a6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r24, boolean r25) {
        /*
            r23 = this;
            r0 = r23
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
            goto L_0x0566
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
            if (r8 == 0) goto L_0x0114
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            long r9 = r0.currentDialogId
            java.lang.Object r8 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 == 0) goto L_0x0107
            if (r24 != 0) goto L_0x0116
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
            if (r9 == 0) goto L_0x00a2
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r9)
            int r9 = r9.getArchiveUnreadCount()
            r0.unreadCount = r9
            r0.mentionCount = r5
            r0.reactionMentionCount = r5
            goto L_0x00ae
        L_0x00a2:
            int r9 = r8.unread_count
            r0.unreadCount = r9
            int r9 = r8.unread_mentions_count
            r0.mentionCount = r9
            int r9 = r8.unread_reactions_count
            r0.reactionMentionCount = r9
        L_0x00ae:
            boolean r9 = r8.unread_mark
            r0.markUnread = r9
            org.telegram.messenger.MessageObject r9 = r0.message
            if (r9 == 0) goto L_0x00bb
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.edit_date
            goto L_0x00bc
        L_0x00bb:
            r9 = 0
        L_0x00bc:
            r0.currentEditDate = r9
            int r9 = r8.last_message_date
            r0.lastMessageDate = r9
            int r9 = r0.dialogsType
            r10 = 7
            r11 = 8
            if (r9 == r10) goto L_0x00da
            if (r9 != r11) goto L_0x00cc
            goto L_0x00da
        L_0x00cc:
            int r9 = r0.currentDialogFolderId
            if (r9 != 0) goto L_0x00d6
            boolean r8 = r8.pinned
            if (r8 == 0) goto L_0x00d6
            r8 = 1
            goto L_0x00d7
        L_0x00d6:
            r8 = 0
        L_0x00d7:
            r0.drawPin = r8
            goto L_0x00fc
        L_0x00da:
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.messenger.MessagesController$DialogFilter[] r9 = r9.selectedDialogFilter
            int r10 = r0.dialogsType
            if (r10 != r11) goto L_0x00e8
            r10 = 1
            goto L_0x00e9
        L_0x00e8:
            r10 = 0
        L_0x00e9:
            r9 = r9[r10]
            if (r9 == 0) goto L_0x00f9
            org.telegram.messenger.support.LongSparseIntArray r9 = r9.pinnedDialogs
            long r10 = r8.id
            int r8 = r9.indexOfKey(r10)
            if (r8 < 0) goto L_0x00f9
            r8 = 1
            goto L_0x00fa
        L_0x00f9:
            r8 = 0
        L_0x00fa:
            r0.drawPin = r8
        L_0x00fc:
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x0116
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.send_state
            r0.lastSendState = r8
            goto L_0x0116
        L_0x0107:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.reactionMentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0116
        L_0x0114:
            r0.drawPin = r5
        L_0x0116:
            int r8 = r0.dialogsType
            r9 = 2
            if (r8 != r9) goto L_0x011d
            r0.drawPin = r5
        L_0x011d:
            if (r24 == 0) goto L_0x0269
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 == 0) goto L_0x0140
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r8 = r24 & r8
            if (r8 == 0) goto L_0x0140
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            long r10 = r10.id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            r0.user = r8
            r23.invalidate()
        L_0x0140:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x016a
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_USER_PRINT
            r8 = r24 & r8
            if (r8 == 0) goto L_0x016a
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r10 = r0.currentDialogId
            java.lang.CharSequence r8 = r8.getPrintingString(r10, r5, r4)
            java.lang.CharSequence r10 = r0.lastPrintString
            if (r10 == 0) goto L_0x015c
            if (r8 == 0) goto L_0x0168
        L_0x015c:
            if (r10 != 0) goto L_0x0160
            if (r8 != 0) goto L_0x0168
        L_0x0160:
            if (r10 == 0) goto L_0x016a
            boolean r8 = r10.equals(r8)
            if (r8 != 0) goto L_0x016a
        L_0x0168:
            r8 = 1
            goto L_0x016b
        L_0x016a:
            r8 = 0
        L_0x016b:
            if (r8 != 0) goto L_0x017e
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_MESSAGE_TEXT
            r10 = r24 & r10
            if (r10 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x017e
            java.lang.CharSequence r10 = r10.messageText
            java.lang.CharSequence r11 = r0.lastMessageString
            if (r10 == r11) goto L_0x017e
            r8 = 1
        L_0x017e:
            if (r8 != 0) goto L_0x01ac
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01ac
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 == 0) goto L_0x01ac
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            long r11 = r11.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
            boolean r11 = r10.call_active
            if (r11 == 0) goto L_0x01a6
            boolean r10 = r10.call_not_empty
            if (r10 == 0) goto L_0x01a6
            r10 = 1
            goto L_0x01a7
        L_0x01a6:
            r10 = 0
        L_0x01a7:
            boolean r11 = r0.hasCall
            if (r10 == r11) goto L_0x01ac
            r8 = 1
        L_0x01ac:
            if (r8 != 0) goto L_0x01b9
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01b9
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 != 0) goto L_0x01b9
            r8 = 1
        L_0x01b9:
            if (r8 != 0) goto L_0x01c6
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01c6
            org.telegram.tgnet.TLRPC$Chat r10 = r0.chat
            if (r10 != 0) goto L_0x01c6
            r8 = 1
        L_0x01c6:
            if (r8 != 0) goto L_0x01d3
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01d3
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            if (r10 != 0) goto L_0x01d3
            r8 = 1
        L_0x01d3:
            if (r8 != 0) goto L_0x01e0
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r10 = r24 & r10
            if (r10 == 0) goto L_0x01e0
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            if (r10 != 0) goto L_0x01e0
            r8 = 1
        L_0x01e0:
            if (r8 != 0) goto L_0x024c
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE
            r10 = r24 & r10
            if (r10 == 0) goto L_0x024c
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x01fd
            boolean r11 = r0.lastUnreadState
            boolean r10 = r10.isUnread()
            if (r11 == r10) goto L_0x01fd
            org.telegram.messenger.MessageObject r8 = r0.message
            boolean r8 = r8.isUnread()
            r0.lastUnreadState = r8
            r8 = 1
        L_0x01fd:
            boolean r10 = r0.isDialogCell
            if (r10 == 0) goto L_0x024c
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r10 = r10.dialogs_dict
            long r11 = r0.currentDialogId
            java.lang.Object r10 = r10.get(r11)
            org.telegram.tgnet.TLRPC$Dialog r10 = (org.telegram.tgnet.TLRPC$Dialog) r10
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r11 == 0) goto L_0x0222
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r11 = org.telegram.messenger.MessagesStorage.getInstance(r11)
            int r11 = r11.getArchiveUnreadCount()
        L_0x021f:
            r12 = 0
            r13 = 0
            goto L_0x022d
        L_0x0222:
            if (r10 == 0) goto L_0x022b
            int r11 = r10.unread_count
            int r12 = r10.unread_mentions_count
            int r13 = r10.unread_reactions_count
            goto L_0x022d
        L_0x022b:
            r11 = 0
            goto L_0x021f
        L_0x022d:
            if (r10 == 0) goto L_0x024c
            int r14 = r0.unreadCount
            if (r14 != r11) goto L_0x0241
            boolean r14 = r0.markUnread
            boolean r15 = r10.unread_mark
            if (r14 != r15) goto L_0x0241
            int r14 = r0.mentionCount
            if (r14 != r12) goto L_0x0241
            int r14 = r0.reactionMentionCount
            if (r14 == r13) goto L_0x024c
        L_0x0241:
            r0.unreadCount = r11
            r0.mentionCount = r12
            boolean r8 = r10.unread_mark
            r0.markUnread = r8
            r0.reactionMentionCount = r13
            r8 = 1
        L_0x024c:
            if (r8 != 0) goto L_0x0263
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_SEND_STATE
            r10 = r24 & r10
            if (r10 == 0) goto L_0x0263
            org.telegram.messenger.MessageObject r10 = r0.message
            if (r10 == 0) goto L_0x0263
            int r11 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            int r10 = r10.send_state
            if (r11 == r10) goto L_0x0263
            r0.lastSendState = r10
            r8 = 1
        L_0x0263:
            if (r8 != 0) goto L_0x0269
            r23.invalidate()
            return
        L_0x0269:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r3 = r0.currentDialogFolderId
            r10 = 0
            if (r3 == 0) goto L_0x0286
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r3 = r23.findFolderTopMessage()
            r0.message = r3
            if (r3 == 0) goto L_0x0284
            long r12 = r3.getDialogId()
            goto L_0x029f
        L_0x0284:
            r12 = r10
            goto L_0x029f
        L_0x0286:
            boolean r3 = r0.isDialogCell
            if (r3 == 0) goto L_0x029a
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r12 = r0.currentDialogId
            boolean r3 = r3.isDialogMuted(r12)
            if (r3 == 0) goto L_0x029a
            r3 = 1
            goto L_0x029b
        L_0x029a:
            r3 = 0
        L_0x029b:
            r0.dialogMuted = r3
            long r12 = r0.currentDialogId
        L_0x029f:
            int r3 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x0346
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r12)
            if (r3 == 0) goto L_0x02d4
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r8 = org.telegram.messenger.DialogObject.getEncryptedChatId(r12)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r8)
            r0.encryptedChat = r3
            if (r3 == 0) goto L_0x031e
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r0.encryptedChat
            long r10 = r8.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
            goto L_0x031e
        L_0x02d4:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r12)
            if (r3 == 0) goto L_0x02eb
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r8 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
            goto L_0x031e
        L_0x02eb:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r10 = -r12
            java.lang.Long r8 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r8)
            r0.chat = r3
            boolean r8 = r0.isDialogCell
            if (r8 != 0) goto L_0x031e
            if (r3 == 0) goto L_0x031e
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            if (r3 == 0) goto L_0x031e
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r8 = r8.migrated_to
            long r10 = r8.channel_id
            java.lang.Long r8 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r8)
            if (r3 == 0) goto L_0x031e
            r0.chat = r3
        L_0x031e:
            boolean r3 = r0.useMeForMyMessages
            if (r3 == 0) goto L_0x0346
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x0346
            org.telegram.messenger.MessageObject r3 = r0.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x0346
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r10 = r8.clientUserId
            java.lang.Long r8 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r8)
            r0.user = r3
        L_0x0346:
            int r3 = r0.currentDialogFolderId
            if (r3 == 0) goto L_0x0363
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
            goto L_0x03c6
        L_0x0363:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            if (r3 == 0) goto L_0x03b4
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x038a
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
            goto L_0x03c6
        L_0x038a:
            org.telegram.tgnet.TLRPC$User r3 = r0.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x03aa
            boolean r3 = r0.useMeForMyMessages
            if (r3 != 0) goto L_0x03aa
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
            goto L_0x03c6
        L_0x03aa:
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setForUserOrChat(r8, r10)
            goto L_0x03c6
        L_0x03b4:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            if (r3 == 0) goto L_0x03c6
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setForUserOrChat(r8, r10)
        L_0x03c6:
            r10 = 150(0x96, double:7.4E-322)
            r12 = 220(0xdc, double:1.087E-321)
            if (r25 == 0) goto L_0x050f
            int r3 = r0.unreadCount
            if (r1 != r3) goto L_0x03d4
            boolean r3 = r0.markUnread
            if (r7 == r3) goto L_0x050f
        L_0x03d4:
            long r14 = java.lang.System.currentTimeMillis()
            long r2 = r0.lastDialogChangedTime
            long r14 = r14 - r2
            r2 = 100
            int r16 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r16 <= 0) goto L_0x050f
            android.animation.ValueAnimator r2 = r0.countAnimator
            if (r2 == 0) goto L_0x03e8
            r2.cancel()
        L_0x03e8:
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
            if (r1 == 0) goto L_0x040b
            boolean r2 = r0.markUnread
            if (r2 == 0) goto L_0x0412
        L_0x040b:
            boolean r2 = r0.markUnread
            if (r2 != 0) goto L_0x0432
            if (r7 != 0) goto L_0x0412
            goto L_0x0432
        L_0x0412:
            int r2 = r0.unreadCount
            if (r2 != 0) goto L_0x0423
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r10)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x0441
        L_0x0423:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r14 = 430(0x1ae, double:2.124E-321)
            r2.setDuration(r14)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
            goto L_0x0441
        L_0x0432:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r2.setDuration(r12)
            android.animation.ValueAnimator r2 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
        L_0x0441:
            boolean r2 = r0.drawCount
            if (r2 == 0) goto L_0x04f9
            boolean r2 = r0.drawCount2
            if (r2 == 0) goto L_0x04f9
            android.text.StaticLayout r2 = r0.countLayout
            if (r2 == 0) goto L_0x04f9
            java.lang.String r2 = java.lang.String.valueOf(r1)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r7 = r2.length()
            int r14 = r3.length()
            if (r7 != r14) goto L_0x04f5
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r2)
            android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder
            r14.<init>(r3)
            android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder
            r15.<init>(r3)
            r4 = 0
        L_0x0471:
            int r8 = r2.length()
            if (r4 >= r8) goto L_0x04a3
            char r8 = r2.charAt(r4)
            char r10 = r3.charAt(r4)
            if (r8 != r10) goto L_0x0494
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            int r10 = r4 + 1
            r7.setSpan(r8, r4, r10, r5)
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            r14.setSpan(r8, r4, r10, r5)
            goto L_0x049e
        L_0x0494:
            org.telegram.ui.Components.EmptyStubSpan r8 = new org.telegram.ui.Components.EmptyStubSpan
            r8.<init>()
            int r10 = r4 + 1
            r15.setSpan(r8, r4, r10, r5)
        L_0x049e:
            int r4 = r4 + 1
            r10 = 150(0x96, double:7.4E-322)
            goto L_0x0471
        L_0x04a3:
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
            goto L_0x04f9
        L_0x04f5:
            android.text.StaticLayout r2 = r0.countLayout
            r0.countOldLayout = r2
        L_0x04f9:
            int r2 = r0.countWidth
            r0.countWidthOld = r2
            int r2 = r0.countLeft
            r0.countLeftOld = r2
            int r2 = r0.unreadCount
            if (r2 <= r1) goto L_0x0507
            r1 = 1
            goto L_0x0508
        L_0x0507:
            r1 = 0
        L_0x0508:
            r0.countAnimationIncrement = r1
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x050f:
            int r1 = r0.reactionMentionCount
            if (r1 == 0) goto L_0x0515
            r4 = 1
            goto L_0x0516
        L_0x0515:
            r4 = 0
        L_0x0516:
            if (r25 == 0) goto L_0x003a
            if (r4 == r6) goto L_0x003a
            android.animation.ValueAnimator r1 = r0.reactionsMentionsAnimator
            if (r1 == 0) goto L_0x0521
            r1.cancel()
        L_0x0521:
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
            if (r4 == 0) goto L_0x0553
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.setDuration(r12)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r2.setInterpolator(r3)
            goto L_0x0561
        L_0x0553:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r3 = 150(0x96, double:7.4E-322)
            r2.setDuration(r3)
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r3)
        L_0x0561:
            android.animation.ValueAnimator r2 = r0.reactionsMentionsAnimator
            r2.start()
        L_0x0566:
            int r2 = r23.getMeasuredWidth()
            if (r2 != 0) goto L_0x0577
            int r2 = r23.getMeasuredHeight()
            if (r2 == 0) goto L_0x0573
            goto L_0x0577
        L_0x0573:
            r23.requestLayout()
            goto L_0x057a
        L_0x0577:
            r23.buildLayout()
        L_0x057a:
            if (r25 != 0) goto L_0x058d
            boolean r2 = r0.dialogMuted
            if (r2 == 0) goto L_0x0583
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0584
        L_0x0583:
            r2 = 0
        L_0x0584:
            r0.dialogMutedProgress = r2
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x058d
            r1.cancel()
        L_0x058d:
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
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x066f, code lost:
        if (r0.type == 2) goto L_0x068a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0e0b, code lost:
        if (r8.reactionsMentionsChangeProgress != 1.0f) goto L_0x0e10;
     */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x05fa  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0609  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0649  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x06e5  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0739  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0834  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x08f6  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0911  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x094f  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0952  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x095c  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x095f  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x096e  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x09a7  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x09ae  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a4d  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0a9a  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0d4f  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0e05  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0e0e  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0e4b  */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0eae  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0ee3  */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0f1b  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x1001  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x12a2  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x12b6  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x12cb  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x12e5  */
    /* JADX WARNING: Removed duplicated region for block: B:701:0x1311  */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x1376  */
    /* JADX WARNING: Removed duplicated region for block: B:732:0x13cf  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x13e4  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x13fe  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x1428  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1458  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x146c  */
    /* JADX WARNING: Removed duplicated region for block: B:782:0x1494  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x14b6  */
    /* JADX WARNING: Removed duplicated region for block: B:797:? A[RETURN, SYNTHETIC] */
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
            if (r0 == 0) goto L_0x0042
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0042
            float r0 = r0.outProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            float r0 = r8.translationX
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0042
            boolean r0 = r8.drawingForBlur
            if (r0 != 0) goto L_0x0041
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r9.clipRect(r10, r10, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
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
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0081
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x0081
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
        L_0x0081:
            float r0 = r8.translationX
            r14 = 1090519040(0x41000000, float:8.0)
            r15 = 4
            r16 = 1073741824(0x40000000, float:2.0)
            r17 = 1082130432(0x40800000, float:4.0)
            r6 = 1
            r5 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x00b4
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0098
            goto L_0x00b4
        L_0x0098:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x00af
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r10
        L_0x00af:
            r7 = 1065353216(0x3var_, float:1.0)
            r14 = 1
            goto L_0x04a1
        L_0x00b4:
            r31.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r4 = "chats_archivePinBackground"
            java.lang.String r3 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00fd
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00e0
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628501(0x7f0e11d5, float:1.8884296E38)
            java.lang.String r7 = "UnhideFromTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628501(0x7f0e11d5, float:1.8884296E38)
            goto L_0x011d
        L_0x00e0:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.String r7 = "HideOnTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131626030(0x7f0e082e, float:1.8879285E38)
            goto L_0x011d
        L_0x00fd:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0124
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131627561(0x7f0e0e29, float:1.888239E38)
            java.lang.String r7 = "PsaHide"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627561(0x7f0e0e29, float:1.888239E38)
        L_0x011d:
            r29 = r7
            r7 = r1
            r1 = r29
            goto L_0x0219
        L_0x0124:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01fb
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r7 = 3
            if (r2 != r7) goto L_0x0163
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x0152
            r2 = 2131628303(0x7f0e110f, float:1.8883895E38)
            java.lang.String r7 = "SwipeUnmute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628303(0x7f0e110f, float:1.8883895E38)
            goto L_0x011d
        L_0x0152:
            r2 = 2131628291(0x7f0e1103, float:1.888387E38)
            java.lang.String r7 = "SwipeMute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628291(0x7f0e1103, float:1.888387E38)
            goto L_0x011d
        L_0x0163:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r15) goto L_0x0184
            r2 = 2131628288(0x7f0e1100, float:1.8883864E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r2 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131628288(0x7f0e1100, float:1.8883864E38)
            goto L_0x011d
        L_0x0184:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r6) goto L_0x01b9
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x01a7
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x0195
            goto L_0x01a7
        L_0x0195:
            r2 = 2131628290(0x7f0e1102, float:1.8883869E38)
            java.lang.String r7 = "SwipeMarkAsUnread"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131628290(0x7f0e1102, float:1.8883869E38)
            goto L_0x011d
        L_0x01a7:
            r2 = 2131628289(0x7f0e1101, float:1.8883866E38)
            java.lang.String r7 = "SwipeMarkAsRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131628289(0x7f0e1101, float:1.8883866E38)
            goto L_0x011d
        L_0x01b9:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01e9
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01d7
            r2 = 2131628304(0x7f0e1110, float:1.8883897E38)
            java.lang.String r7 = "SwipeUnpin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131628304(0x7f0e1110, float:1.8883897E38)
            goto L_0x011d
        L_0x01d7:
            r2 = 2131628292(0x7f0e1104, float:1.8883873E38)
            java.lang.String r7 = "SwipePin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131628292(0x7f0e1104, float:1.8883873E38)
            goto L_0x011d
        L_0x01e9:
            r2 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r7 = "Archive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624333(0x7f0e018d, float:1.8875843E38)
            goto L_0x011d
        L_0x01fb:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628492(0x7f0e11cc, float:1.8884278E38)
            java.lang.String r7 = "Unarchive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628492(0x7f0e11cc, float:1.8884278E38)
            goto L_0x011d
        L_0x0219:
            boolean r15 = r8.swipeCanceled
            if (r15 == 0) goto L_0x0226
            org.telegram.ui.Components.RLottieDrawable r15 = r8.lastDrawTranslationDrawable
            if (r15 == 0) goto L_0x0226
            r8.translationDrawable = r15
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x022c
        L_0x0226:
            org.telegram.ui.Components.RLottieDrawable r15 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r15
            r8.lastDrawSwipeMessageStringId = r2
        L_0x022c:
            r15 = r2
            boolean r2 = r8.translationAnimationStarted
            if (r2 != 0) goto L_0x0253
            float r2 = r8.translationX
            float r2 = java.lang.Math.abs(r2)
            r19 = 1110179840(0x422CLASSNAME, float:43.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r10 = (float) r10
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0253
            r8.translationAnimationStarted = r6
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.start()
        L_0x0253:
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            float r10 = r8.translationX
            float r10 = r10 + r2
            float r2 = r8.currentRevealProgress
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x02d4
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
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
            if (r1 != 0) goto L_0x02d7
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x02a1
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r22)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r1
        L_0x02a1:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x02d7
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
            goto L_0x02d7
        L_0x02d4:
            r27 = r1
            r0 = r4
        L_0x02d7:
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
            if (r2 != 0) goto L_0x02f7
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02f4
            goto L_0x02f7
        L_0x02f4:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x02f9
        L_0x02f7:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x02f9:
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
            if (r4 <= 0) goto L_0x03a1
            r31.save()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r4 = (float) r4
            float r4 = r10 - r4
            int r6 = r30.getMeasuredWidth()
            float r6 = (float) r6
            int r14 = r30.getMeasuredHeight()
            float r14 = (float) r14
            r9.clipRect(r4, r11, r6, r14)
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
            if (r3 != 0) goto L_0x036e
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r0)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r14 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14
            goto L_0x036f
        L_0x036e:
            r14 = 1
        L_0x036f:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x03a2
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
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r14
            goto L_0x03a2
        L_0x03a1:
            r14 = 1
        L_0x03a2:
            r31.save()
            float r0 = (float) r1
            float r1 = (float) r2
            r9.translate(r0, r1)
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            r7 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x03d1
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x03d1
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
        L_0x03d1:
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
            if (r2 != r15) goto L_0x0406
            int r2 = r8.swipeMessageWidth
            int r3 = r30.getMeasuredWidth()
            if (r2 == r3) goto L_0x0452
        L_0x0406:
            r8.swipeMessageTextId = r15
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
            if (r2 <= r14) goto L_0x0452
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
        L_0x0452:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x049e
            r31.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            if (r0 <= r14) goto L_0x0468
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x0469
        L_0x0468:
            r0 = 0
        L_0x0469:
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
            if (r2 != 0) goto L_0x048b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0488
            goto L_0x048b
        L_0x0488:
            r2 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x048d
        L_0x048b:
            r2 = 1112014848(0x42480000, float:50.0)
        L_0x048d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 + r0
            r9.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r9)
            r31.restore()
        L_0x049e:
            r31.restore()
        L_0x04a1:
            float r0 = r8.translationX
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x04af
            r31.save()
            float r0 = r8.translationX
            r9.translate(r0, r11)
        L_0x04af:
            r10 = 1090519040(0x41000000, float:8.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r0 = (float) r0
            float r1 = r8.cornerProgress
            float r0 = r0 * r1
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x04d4
            android.graphics.RectF r1 = r8.rect
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r30.getMeasuredHeight()
            float r3 = (float) r3
            r1.set(r11, r11, r2, r3)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r9.drawRoundRect(r1, r0, r0, r2)
        L_0x04d4:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x050a
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x04e2
            float r1 = r8.archiveBackgroundProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x050a
        L_0x04e2:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            r4 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r2, r3, r7)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r30.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r30.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0532
        L_0x050a:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x0512
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x0532
        L_0x0512:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            r2 = 0
            r3 = 0
            int r1 = r30.getMeasuredWidth()
            float r4 = (float) r1
            int r1 = r30.getMeasuredHeight()
            float r5 = (float) r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0532:
            float r1 = r8.translationX
            java.lang.String r15 = "windowBackgroundWhite"
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0540
            float r1 = r8.cornerProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x05c4
        L_0x0540:
            r31.save()
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            int r2 = r30.getMeasuredWidth()
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r30.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r30.getMeasuredHeight()
            float r4 = (float) r4
            r1.set(r2, r11, r3, r4)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r1, r0, r0, r2)
            boolean r1 = r8.isSelected
            if (r1 == 0) goto L_0x057b
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r9.drawRoundRect(r1, r0, r0, r2)
        L_0x057b:
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x05a5
            boolean r1 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r1 == 0) goto L_0x0589
            float r1 = r8.archiveBackgroundProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x05a5
        L_0x0589:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            float r3 = r8.archiveBackgroundProgress
            r4 = 0
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r2, r3, r7)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r1, r0, r0, r2)
            goto L_0x05c1
        L_0x05a5:
            boolean r1 = r8.drawPin
            if (r1 != 0) goto L_0x05ad
            boolean r1 = r8.drawPinBackground
            if (r1 == 0) goto L_0x05c1
        L_0x05ad:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_pinnedOverlay"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.setColor(r2)
            android.graphics.RectF r1 = r8.rect
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r1, r0, r0, r2)
        L_0x05c1:
            r31.restore()
        L_0x05c4:
            float r0 = r8.translationX
            r19 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x05df
            float r0 = r8.cornerProgress
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 >= 0) goto L_0x05f4
            float r1 = (float) r12
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x05f1
            r8.cornerProgress = r7
            goto L_0x05f1
        L_0x05df:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x05f4
            float r1 = (float) r12
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x05f1
            r8.cornerProgress = r11
        L_0x05f1:
            r20 = 1
            goto L_0x05f6
        L_0x05f4:
            r20 = 0
        L_0x05f6:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x0609
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x0641
        L_0x0609:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x061c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x0641
        L_0x061c:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x062f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x0641
        L_0x062f:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x0641
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x0641:
            android.text.StaticLayout r0 = r8.nameLayout
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x06c4
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0664
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
            goto L_0x069f
        L_0x0664:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0689
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x0672
            int r0 = r0.type
            r6 = 2
            if (r0 != r6) goto L_0x0673
            goto L_0x068a
        L_0x0672:
            r6 = 2
        L_0x0673:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x069f
        L_0x0689:
            r6 = 2
        L_0x068a:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x069f:
            r31.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x06b1
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x06ae
            goto L_0x06b1
        L_0x06ae:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x06b3
        L_0x06b1:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x06b3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r31.restore()
            goto L_0x06c5
        L_0x06c4:
            r6 = 2
        L_0x06c5:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x06e1
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x06e1
            r31.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r31.restore()
        L_0x06e1:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x0735
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06f9
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x071c
        L_0x06f9:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x070d
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x071c
        L_0x070d:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x071c:
            r31.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x072e }
            r0.draw(r9)     // Catch:{ Exception -> 0x072e }
            goto L_0x0732
        L_0x072e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0732:
            r31.restore()
        L_0x0735:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x0830
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x076d
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0757
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0782
        L_0x0757:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0782
        L_0x076d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r3 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0782:
            r31.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x07cf
            r31.save()     // Catch:{ Exception -> 0x07ca }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07ca }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r9, r0)     // Catch:{ Exception -> 0x07ca }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07ca }
            r0.draw(r9)     // Catch:{ Exception -> 0x07ca }
            r31.restore()     // Catch:{ Exception -> 0x07ca }
            r0 = 0
        L_0x07a7:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x07ca }
            int r1 = r1.size()     // Catch:{ Exception -> 0x07ca }
            if (r0 >= r1) goto L_0x07d4
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x07ca }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ Exception -> 0x07ca }
            org.telegram.ui.Components.spoilers.SpoilerEffect r1 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r1     // Catch:{ Exception -> 0x07ca }
            android.text.StaticLayout r2 = r8.messageLayout     // Catch:{ Exception -> 0x07ca }
            android.text.TextPaint r2 = r2.getPaint()     // Catch:{ Exception -> 0x07ca }
            int r2 = r2.getColor()     // Catch:{ Exception -> 0x07ca }
            r1.setColor(r2)     // Catch:{ Exception -> 0x07ca }
            r1.draw(r9)     // Catch:{ Exception -> 0x07ca }
            int r0 = r0 + 1
            goto L_0x07a7
        L_0x07ca:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07d4
        L_0x07cf:
            android.text.StaticLayout r0 = r8.messageLayout
            r0.draw(r9)
        L_0x07d4:
            r31.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0830
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0830
            r31.save()
            int r1 = r8.printingStringType
            if (r1 == r14) goto L_0x0805
            r2 = 4
            if (r1 != r2) goto L_0x07ec
            goto L_0x0805
        L_0x07ec:
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
            goto L_0x0817
        L_0x0805:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r14) goto L_0x0811
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x0812
        L_0x0811:
            r1 = 0
        L_0x0812:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0817:
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
        L_0x0830:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x08f6
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x083c
            r1 = 2
            goto L_0x083d
        L_0x083c:
            r1 = 0
        L_0x083d:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0844
            r1 = 4
            goto L_0x0845
        L_0x0844:
            r1 = 0
        L_0x0845:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x0853
            if (r1 == r0) goto L_0x0853
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x0853
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x0853:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x0859
            int r0 = r8.animateToStatusDrawableParams
        L_0x0859:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x0860
            r18 = 1
            goto L_0x0862
        L_0x0860:
            r18 = 0
        L_0x0862:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x086a
            r2 = 4
            r23 = 1
            goto L_0x086d
        L_0x086a:
            r2 = 4
            r23 = 0
        L_0x086d:
            r0 = r0 & r2
            if (r0 == 0) goto L_0x0872
            r0 = 1
            goto L_0x0873
        L_0x0872:
            r0 = 0
        L_0x0873:
            if (r1 == 0) goto L_0x08cf
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x087d
            r3 = 1
            goto L_0x087e
        L_0x087d:
            r3 = 0
        L_0x087e:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x0885
            r2 = 4
            r4 = 1
            goto L_0x0887
        L_0x0885:
            r2 = 4
            r4 = 0
        L_0x0887:
            r1 = r1 & r2
            if (r1 == 0) goto L_0x088c
            r5 = 1
            goto L_0x088d
        L_0x088c:
            r5 = 0
        L_0x088d:
            if (r18 != 0) goto L_0x08b5
            if (r3 != 0) goto L_0x08b5
            if (r5 == 0) goto L_0x08b5
            if (r4 != 0) goto L_0x08b5
            if (r23 == 0) goto L_0x08b5
            if (r0 == 0) goto L_0x08b5
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
            r10 = 2
            r14 = 1065353216(0x3var_, float:1.0)
            r7 = r18
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x08e1
        L_0x08b5:
            r10 = 2
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
            goto L_0x08e1
        L_0x08cf:
            r10 = 2
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r30
            r2 = r31
            r3 = r18
            r4 = r23
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x08e1:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x08e9
            r7 = 2
            goto L_0x08ea
        L_0x08e9:
            r7 = 0
        L_0x08ea:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x08f1
            r1 = 4
            goto L_0x08f2
        L_0x08f1:
            r1 = 0
        L_0x08f2:
            int r0 = r0 + r1
            r8.lastStatusDrawableParams = r0
            goto L_0x08f9
        L_0x08f6:
            r10 = 2
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x08f9:
            int r0 = r8.dialogsType
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 == r10) goto L_0x09ae
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0909
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x09ae
        L_0x0909:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x09ae
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x09ae
            if (r0 == 0) goto L_0x092a
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x092a
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x0926
            r8.dialogMutedProgress = r14
            goto L_0x0942
        L_0x0926:
            r30.invalidate()
            goto L_0x0942
        L_0x092a:
            if (r0 != 0) goto L_0x0942
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0942
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x093f
            r8.dialogMutedProgress = r11
            goto L_0x0942
        L_0x093f:
            r30.invalidate()
        L_0x0942:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0952
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x094f
            goto L_0x0952
        L_0x094f:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0953
        L_0x0952:
            r5 = 0
        L_0x0953:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x095f
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x0961
        L_0x095f:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0961:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x09a7
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
            goto L_0x0a1f
        L_0x09a7:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a1f
        L_0x09ae:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x09ef
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09c2
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09bf
            goto L_0x09c2
        L_0x09bf:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09c4
        L_0x09c2:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09c4:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09db
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09d8
            goto L_0x09db
        L_0x09d8:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09dd
        L_0x09db:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09dd:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a1f
        L_0x09ef:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a1f
            r2 = 1
            if (r0 != r2) goto L_0x09f9
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x09fb
        L_0x09f9:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x09fb:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a09
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a06
            goto L_0x0a09
        L_0x0a06:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a0b
        L_0x0a09:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a0b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            r2 = 1
            if (r0 != r2) goto L_0x0a1a
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a1c
        L_0x0a1a:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a1c:
            r0.draw(r9)
        L_0x0a1f:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a29
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0a41
        L_0x0a29:
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
        L_0x0a41:
            boolean r0 = r8.drawError
            r2 = 1085276160(0x40b00000, float:5.5)
            r3 = 1102577664(0x41b80000, float:23.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0a9a
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
            int r11 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r11 = r11 + r3
            float r3 = (float) r11
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
            goto L_0x0ea8
        L_0x0a9a:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0aa2
            boolean r6 = r8.drawMention
            if (r6 == 0) goto L_0x0aa6
        L_0x0aa2:
            boolean r6 = r8.drawCount2
            if (r6 != 0) goto L_0x0ad7
        L_0x0aa6:
            float r6 = r8.countChangeProgress
            int r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0ad7
            boolean r6 = r8.drawReactionMention
            if (r6 != 0) goto L_0x0ad7
            float r6 = r8.reactionsMentionsChangeProgress
            int r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x0ab7
            goto L_0x0ad7
        L_0x0ab7:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0ea8
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
            goto L_0x0ea8
        L_0x0ad7:
            if (r0 == 0) goto L_0x0add
            boolean r0 = r8.drawCount2
            if (r0 != 0) goto L_0x0ae3
        L_0x0add:
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0d49
        L_0x0ae3:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0af0
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0af0
            float r6 = r8.countChangeProgress
            float r6 = r14 - r6
            goto L_0x0af2
        L_0x0af0:
            float r6 = r8.countChangeProgress
        L_0x0af2:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0c6e
            if (r0 != 0) goto L_0x0afa
            goto L_0x0c6e
        L_0x0afa:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b06
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b03
            goto L_0x0b06
        L_0x0b03:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b08
        L_0x0b06:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b08:
            float r7 = r8.reorderIconProgress
            float r7 = r14 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r11 = r8.reorderIconProgress
            float r11 = r14 - r11
            float r11 = r11 * r1
            int r11 = (int) r11
            r7.setAlpha(r11)
            float r7 = r6 * r16
            int r11 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r11 <= 0) goto L_0x0b27
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x0b28
        L_0x0b27:
            r11 = r7
        L_0x0b28:
            int r4 = r8.countLeft
            float r4 = (float) r4
            float r4 = r4 * r11
            int r10 = r8.countLeftOld
            float r10 = (float) r10
            float r25 = r14 - r11
            float r10 = r10 * r25
            float r4 = r4 + r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r10 = (float) r10
            float r10 = r4 - r10
            android.graphics.RectF r2 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r5 = r8.countWidth
            float r5 = (float) r5
            float r5 = r5 * r11
            float r5 = r5 + r10
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
            r2.set(r10, r1, r5, r14)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0b76
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r2 = r2.getInterpolation(r7)
            float r2 = r2 * r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r2 = r2 + r1
            goto L_0x0b8c
        L_0x0b76:
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
        L_0x0b8c:
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
            if (r0 == 0) goto L_0x0bc5
            r31.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r4, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r31.restore()
        L_0x0bc5:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = (float) r0
            float r5 = r2 * r11
            int r5 = (int) r5
            r1.setAlpha(r5)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0be4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0be9
        L_0x0be4:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0be9:
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
            goto L_0x0c2f
        L_0x0CLASSNAME:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0c2f
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
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
        L_0x0c2f:
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
            goto L_0x0c4c
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0c4c:
            float r1 = (float) r1
            float r1 = r1 * r11
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
            goto L_0x0d49
        L_0x0c6e:
            if (r0 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0CLASSNAME:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0c7f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0c7c
            goto L_0x0c7f
        L_0x0c7c:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0CLASSNAME
        L_0x0c7f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0CLASSNAME:
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
            int r10 = r8.countWidth
            int r1 = r1 + r10
            r10 = 1093664768(0x41300000, float:11.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r1 = r1 + r10
            float r1 = (float) r1
            int r10 = r8.countTop
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r10 = r10 + r11
            float r10 = (float) r10
            r2.set(r4, r5, r1, r10)
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0d19
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x0d07
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
        L_0x0d07:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerY()
            r9.scale(r6, r6, r1, r2)
        L_0x0d19:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r2 * r4
            float r2 = r2 * r4
            r9.drawRoundRect(r1, r5, r2, r0)
            if (r7 == 0) goto L_0x0d3f
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
        L_0x0d3f:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0d4b
            r31.restore()
            goto L_0x0d4b
        L_0x0d49:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0d4b:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0e01
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
            if (r0 == 0) goto L_0x0d8d
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0d8d
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0d8f
        L_0x0d8d:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0d8f:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r2 * r4
            float r2 = r2 * r4
            r9.drawRoundRect(r1, r5, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0dca
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
            goto L_0x0e01
        L_0x0dca:
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
        L_0x0e01:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0e0e
            float r0 = r8.reactionsMentionsChangeProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0ea8
            goto L_0x0e10
        L_0x0e0e:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0e10:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
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
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            r31.save()
            float r1 = r8.reactionsMentionsChangeProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0e61
            boolean r3 = r8.drawReactionMention
            if (r3 == 0) goto L_0x0e50
            goto L_0x0e52
        L_0x0e50:
            float r1 = r2 - r1
        L_0x0e52:
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r1, r1, r2, r3)
        L_0x0e61:
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
        L_0x0ea8:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0ecc
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
        L_0x0ecc:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0eda
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0eda
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0edf
        L_0x0eda:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0edf:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
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
            r0.draw(r9)
        L_0x0var_:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0f1e
            r31.restore()
        L_0x0f1e:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0ffe
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0ffe
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1001
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x1001
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x1001
            boolean r0 = r30.isOnline()
            if (r0 != 0) goto L_0x0var_
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0ffa
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0var_
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x0var_
        L_0x0var_:
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0f7c
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
            goto L_0x0var_
        L_0x0f7c:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0f8d
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0f8b
            goto L_0x0f8d
        L_0x0f8b:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f8d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0var_:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
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
            if (r0 == 0) goto L_0x0fe5
            float r0 = r8.onlineProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0ffa
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 + r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0ff8
            r8.onlineProgress = r1
            goto L_0x0ff8
        L_0x0fe5:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ffa
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0ff8
            r8.onlineProgress = r1
        L_0x0ff8:
            r6 = 1
            goto L_0x0ffc
        L_0x0ffa:
            r6 = r20
        L_0x0ffc:
            r20 = r6
        L_0x0ffe:
            r2 = 0
            goto L_0x12df
        L_0x1001:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0ffe
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x100f
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x100f
            r6 = 1
            goto L_0x1010
        L_0x100f:
            r6 = 0
        L_0x1010:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x101b
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0ffe
        L_0x101b:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x1030
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x1030
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r0
            goto L_0x1032
        L_0x1030:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x1032:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x1044
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x1041
            goto L_0x1044
        L_0x1041:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x1046
        L_0x1044:
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1046:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x1069
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x1062
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1060
            goto L_0x1062
        L_0x1060:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1062:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x1080
        L_0x1069:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x107a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1078
            goto L_0x107a
        L_0x1078:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x107a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x1080:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r8.resourcesProvider
            int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
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
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            if (r3 != 0) goto L_0x10f5
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
            float r10 = r8.innerProgress
        L_0x10ed:
            float r6 = r6 * r10
            float r4 = r4 - r6
            r6 = r4
        L_0x10f1:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x11f2
        L_0x10f5:
            r4 = 1
            if (r3 != r4) goto L_0x111c
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
        L_0x1117:
            float r10 = r10 * r11
            float r6 = r6 + r10
            goto L_0x11f2
        L_0x111c:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 2
            if (r3 != r6) goto L_0x113f
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
            float r10 = r8.innerProgress
            goto L_0x10ed
        L_0x113f:
            r4 = 3
            if (r3 != r4) goto L_0x1162
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x1117
        L_0x1162:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 4
            if (r3 != r6) goto L_0x1186
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
            float r10 = r8.innerProgress
            goto L_0x10ed
        L_0x1186:
            r4 = 5
            if (r3 != r4) goto L_0x11aa
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x1117
        L_0x11aa:
            r4 = 1065353216(0x3var_, float:1.0)
            r6 = 6
            if (r3 != r6) goto L_0x11d1
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            float r10 = r10 * r11
            float r6 = r6 - r10
            goto L_0x10f1
        L_0x11d1:
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
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x1117
        L_0x11f2:
            float r10 = r8.chatCallProgress
            int r10 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r10 < 0) goto L_0x11fc
            int r10 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r10 >= 0) goto L_0x1208
        L_0x11fc:
            r31.save()
            float r4 = r8.chatCallProgress
            float r10 = r4 * r5
            float r4 = r4 * r5
            r9.scale(r10, r4, r2, r0)
        L_0x1208:
            android.graphics.RectF r2 = r8.rect
            r4 = 1065353216(0x3var_, float:1.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r11 = r0 - r3
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r14 = r14 + r1
            float r14 = (float) r14
            float r3 = r3 + r0
            r2.set(r10, r11, r14, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r4, r10)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r4
            float r3 = (float) r3
            float r4 = r0 - r6
            r10 = 1077936128(0x40400000, float:3.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r0 = r0 + r6
            r2.set(r3, r4, r10, r0)
            android.graphics.RectF r2 = r8.rect
            r3 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r6, r3, r10)
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
            if (r0 < 0) goto L_0x128f
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1292
        L_0x128f:
            r31.restore()
        L_0x1292:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x12b2
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x12b2
            r2 = 0
            r8.progressStage = r2
        L_0x12b2:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x12cb
            float r0 = r8.chatCallProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x12c9
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x12c9
            r8.chatCallProgress = r2
        L_0x12c9:
            r2 = 0
            goto L_0x12dd
        L_0x12cb:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x12dd
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x12dd
            r8.chatCallProgress = r2
        L_0x12dd:
            r20 = 1
        L_0x12df:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x12e8
            r31.restore()
        L_0x12e8:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x130d
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x130d
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x130d
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
        L_0x130d:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x136e
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x1331
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1321
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1321
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1331
        L_0x1321:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x132a
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x132a
            goto L_0x1331
        L_0x132a:
            r0 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1332
        L_0x1331:
            r1 = 0
        L_0x1332:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1353
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
            goto L_0x136e
        L_0x1353:
            float r2 = (float) r1
            int r0 = r30.getMeasuredHeight()
            r10 = 1
            int r0 = r0 - r10
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            int r0 = r0 - r10
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x136f
        L_0x136e:
            r10 = 1
        L_0x136f:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x13bf
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1380
            r31.restore()
            goto L_0x13bf
        L_0x1380:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
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
        L_0x13bf:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x13cd
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x13cb
            goto L_0x13cd
        L_0x13cb:
            r1 = 0
            goto L_0x13f8
        L_0x13cd:
            if (r0 == 0) goto L_0x13e4
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13cb
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x13e2
            r8.reorderIconProgress = r1
        L_0x13e2:
            r1 = 0
            goto L_0x13f6
        L_0x13e4:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x13f8
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x13f6
            r8.reorderIconProgress = r1
        L_0x13f6:
            r6 = 1
            goto L_0x13fa
        L_0x13f8:
            r6 = r20
        L_0x13fa:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1428
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1454
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1411
            r8.archiveBackgroundProgress = r1
        L_0x1411:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1453
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x1453
        L_0x1428:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1454
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x143d
            r8.archiveBackgroundProgress = r1
        L_0x143d:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1453
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1453:
            r6 = 1
        L_0x1454:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x1468
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x1467
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x1467:
            r6 = 1
        L_0x1468:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x1494
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1480
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1480
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x1480:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x14b4
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x14b3
            r8.currentRevealProgress = r1
            goto L_0x14b3
        L_0x1494:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x14a0
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x14a0:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x14b4
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14b3
            r8.currentRevealProgress = r1
        L_0x14b3:
            r6 = 1
        L_0x14b4:
            if (r6 == 0) goto L_0x14b9
            r30.invalidate()
        L_0x14b9:
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
