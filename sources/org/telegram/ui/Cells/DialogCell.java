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
    private RectF rect;
    private float reorderIconProgress;
    private final Theme.ResourcesProvider resourcesProvider;
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
        this.countChangeProgress = 1.0f;
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

    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0626, code lost:
        if (r2.post_messages == false) goto L_0x0602;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0632, code lost:
        if (r2.kicked != false) goto L_0x0602;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x17e5  */
    /* JADX WARNING: Removed duplicated region for block: B:1051:0x191d  */
    /* JADX WARNING: Removed duplicated region for block: B:1087:0x19ba A[Catch:{ Exception -> 0x19e5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1090:0x19d5 A[Catch:{ Exception -> 0x19e5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1098:0x19f1  */
    /* JADX WARNING: Removed duplicated region for block: B:1112:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x035f  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x05e0  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x06d9  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x09d1  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0baf A[Catch:{ Exception -> 0x0bc1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0bb6 A[Catch:{ Exception -> 0x0bc1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0CLASSNAME A[SYNTHETIC, Splitter:B:546:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0cf6  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e97  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0f4c  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x1026  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x1092  */
    /* JADX WARNING: Removed duplicated region for block: B:753:0x10eb  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x10f0  */
    /* JADX WARNING: Removed duplicated region for block: B:758:0x1103  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x1160  */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x11a2  */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x11af  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x11bf  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x11d7  */
    /* JADX WARNING: Removed duplicated region for block: B:795:0x11e7  */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x1220  */
    /* JADX WARNING: Removed duplicated region for block: B:810:0x1249  */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x12cd  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x12e3  */
    /* JADX WARNING: Removed duplicated region for block: B:853:0x1359 A[Catch:{ Exception -> 0x1378 }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x135c A[Catch:{ Exception -> 0x1378 }] */
    /* JADX WARNING: Removed duplicated region for block: B:862:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x1432  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x14e5  */
    /* JADX WARNING: Removed duplicated region for block: B:880:0x150a  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x153a  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x1657  */
    /* JADX WARNING: Removed duplicated region for block: B:961:0x171e  */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x1727  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x174d A[Catch:{ Exception -> 0x17da }] */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x1755 A[ADDED_TO_REGION, Catch:{ Exception -> 0x17da }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x1775 A[Catch:{ Exception -> 0x17da }] */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x17c7 A[Catch:{ Exception -> 0x17da }] */
    /* JADX WARNING: Removed duplicated region for block: B:997:0x17ca A[Catch:{ Exception -> 0x17da }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r50 = this;
            r1 = r50
            boolean r0 = r1.useForceThreeLines
            r2 = 1099431936(0x41880000, float:17.0)
            r3 = 18
            r4 = 1098907648(0x41800000, float:16.0)
            r5 = 1
            r6 = 0
            if (r0 != 0) goto L_0x005d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0013
            goto L_0x005d
        L_0x0013:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r7 = r0[r6]
            r0 = r0[r6]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_message"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            r0.linkColor = r8
            r7.setColor(r8)
            r1.paintIndex = r6
            r0 = 19
            r7 = 19
            goto L_0x00a8
        L_0x005d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r5]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r5]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r5]
            r7 = 1097859072(0x41700000, float:15.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r5]
            r7 = 1097859072(0x41700000, float:15.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r7 = r0[r5]
            r0 = r0[r5]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_message_threeLines"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            r0.linkColor = r8
            r7.setColor(r8)
            r1.paintIndex = r5
            r7 = 18
        L_0x00a8:
            r1.currentDialogFolderDialogsCount = r6
            boolean r0 = r1.isDialogCell
            if (r0 == 0) goto L_0x00bb
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r9 = r1.currentDialogId
            java.lang.CharSequence r0 = r0.getPrintingString(r9, r6, r5)
            goto L_0x00bc
        L_0x00bb:
            r0 = 0
        L_0x00bc:
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r10 = r1.paintIndex
            r9 = r9[r10]
            r1.drawNameGroup = r6
            r1.drawNameBroadcast = r6
            r1.drawNameLock = r6
            r1.drawNameBot = r6
            r1.drawVerified = r6
            r1.drawScam = r6
            r1.drawPinBackground = r6
            r1.hasMessageThumb = r6
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 != 0) goto L_0x00e0
            boolean r10 = r1.useMeForMyMessages
            if (r10 != 0) goto L_0x00e0
            r10 = 1
            goto L_0x00e1
        L_0x00e0:
            r10 = 0
        L_0x00e1:
            r11 = -1
            r1.printingStringType = r11
            int r12 = android.os.Build.VERSION.SDK_INT
            if (r12 < r3) goto L_0x00fa
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x00f0
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x00f4
        L_0x00f0:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x00f7
        L_0x00f4:
            java.lang.String r13 = "%2$s: ⁨%1$s⁩"
            goto L_0x0108
        L_0x00f7:
            java.lang.String r13 = "⁨%s⁩"
            goto L_0x010c
        L_0x00fa:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x0102
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x0106
        L_0x0102:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x010a
        L_0x0106:
            java.lang.String r13 = "%2$s: %1$s"
        L_0x0108:
            r14 = 1
            goto L_0x010d
        L_0x010a:
            java.lang.String r13 = "%1$s"
        L_0x010c:
            r14 = 0
        L_0x010d:
            org.telegram.messenger.MessageObject r15 = r1.message
            if (r15 == 0) goto L_0x0114
            java.lang.CharSequence r15 = r15.messageText
            goto L_0x0115
        L_0x0114:
            r15 = 0
        L_0x0115:
            r1.lastMessageString = r15
            org.telegram.ui.Cells.DialogCell$CustomDialog r15 = r1.customDialog
            r16 = 1117782016(0x42a00000, float:80.0)
            r17 = 1118044160(0x42a40000, float:82.0)
            r11 = 32
            r20 = 1101004800(0x41a00000, float:20.0)
            r2 = 33
            r22 = 1102053376(0x41b00000, float:22.0)
            r3 = 150(0x96, float:2.1E-43)
            r24 = 1117257728(0x42980000, float:76.0)
            r25 = 1099956224(0x41900000, float:18.0)
            r26 = 1117519872(0x429CLASSNAME, float:78.0)
            r8 = 2
            java.lang.String r4 = ""
            if (r15 == 0) goto L_0x035f
            int r0 = r15.type
            if (r0 != r8) goto L_0x01b7
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x017c
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0141
            goto L_0x017c
        L_0x0141:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0162
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0162:
            int r0 = r50.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x017c:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x019d
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0288
        L_0x019d:
            int r0 = r50.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x01b7:
            boolean r10 = r15.verified
            r1.drawVerified = r10
            boolean r10 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r10 == 0) goto L_0x025c
            if (r0 != r5) goto L_0x025c
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0215
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01cc
            goto L_0x0215
        L_0x01cc:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01f4
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01e9
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01eb
        L_0x01e9:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01eb:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0288
        L_0x01f4:
            int r0 = r50.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0204
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0206
        L_0x0204:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0206:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0215:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x023c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0232
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0234
        L_0x0232:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0234:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0288
        L_0x023c:
            int r0 = r50.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x024c
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x024e
        L_0x024c:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x024e:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x025c:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0277
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0265
            goto L_0x0277
        L_0x0265:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0270
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0270:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0277:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0282
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0282:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
        L_0x0288:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r10 = r0.type
            if (r10 != r5) goto L_0x0310
            r0 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r10 = r1.customDialog
            boolean r12 = r10.isMedia
            if (r12 == 0) goto L_0x02c4
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r9 = r9[r10]
            java.lang.Object[] r10 = new java.lang.Object[r5]
            org.telegram.messenger.MessageObject r12 = r1.message
            java.lang.CharSequence r12 = r12.messageText
            r10[r6] = r12
            java.lang.String r10 = java.lang.String.format(r13, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r10)
            org.telegram.ui.Components.ForegroundColorSpanThemable r12 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider
            java.lang.String r14 = "chats_attachMessage"
            r12.<init>(r14, r13)
            int r13 = r10.length()
            r10.setSpan(r12, r6, r13, r2)
            goto L_0x02fc
        L_0x02c4:
            java.lang.String r2 = r10.message
            int r10 = r2.length()
            if (r10 <= r3) goto L_0x02d0
            java.lang.String r2 = r2.substring(r6, r3)
        L_0x02d0:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x02ee
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x02d9
            goto L_0x02ee
        L_0x02d9:
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r12 = 10
            java.lang.String r2 = r2.replace(r12, r11)
            r10[r6] = r2
            r10[r5] = r0
            java.lang.String r2 = java.lang.String.format(r13, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x02fc
        L_0x02ee:
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r10[r6] = r2
            r10[r5] = r0
            java.lang.String r2 = java.lang.String.format(r13, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x02fc:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r2 = r2[r12]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r10, r2, r12, r6)
            r10 = 0
            goto L_0x031e
        L_0x0310:
            java.lang.String r2 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x031c
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r0[r9]
        L_0x031c:
            r0 = 0
            r10 = 1
        L_0x031e:
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            int r13 = r13.unread_count
            if (r13 == 0) goto L_0x033e
            r1.drawCount = r5
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r14[r6] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r14)
            goto L_0x0341
        L_0x033e:
            r1.drawCount = r6
            r13 = 0
        L_0x0341:
            org.telegram.ui.Cells.DialogCell$CustomDialog r14 = r1.customDialog
            boolean r15 = r14.sent
            if (r15 == 0) goto L_0x034c
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            goto L_0x0350
        L_0x034c:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
        L_0x0350:
            r1.drawClock = r6
            r1.drawError = r6
            java.lang.String r14 = r14.name
            r8 = r0
            r11 = r7
            r0 = r14
            r3 = 0
            r7 = -1
            r14 = r4
            r4 = r9
            goto L_0x115e
        L_0x035f:
            boolean r15 = r1.useForceThreeLines
            if (r15 != 0) goto L_0x037a
            boolean r15 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r15 == 0) goto L_0x0368
            goto L_0x037a
        L_0x0368:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0373
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r15
            goto L_0x038b
        L_0x0373:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r15
            goto L_0x038b
        L_0x037a:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0385
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLeft = r15
            goto L_0x038b
        L_0x0385:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r15
        L_0x038b:
            org.telegram.tgnet.TLRPC$EncryptedChat r15 = r1.encryptedChat
            if (r15 == 0) goto L_0x041a
            int r15 = r1.currentDialogFolderId
            if (r15 != 0) goto L_0x05cf
            r1.drawNameLock = r5
            boolean r15 = r1.useForceThreeLines
            if (r15 != 0) goto L_0x03dc
            boolean r15 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r15 == 0) goto L_0x039e
            goto L_0x03dc
        L_0x039e:
            r15 = 1099169792(0x41840000, float:16.5)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLockTop = r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x03c0
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 + r16
            r1.nameLeft = r15
            goto L_0x05cf
        L_0x03c0:
            int r15 = r50.getMeasuredWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r15 = r15 - r16
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 - r16
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r15
            goto L_0x05cf
        L_0x03dc:
            r15 = 1095237632(0x41480000, float:12.5)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLockTop = r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x03fe
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 + r16
            r1.nameLeft = r15
            goto L_0x05cf
        L_0x03fe:
            int r15 = r50.getMeasuredWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r15 = r15 - r16
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 - r16
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r15
            goto L_0x05cf
        L_0x041a:
            int r15 = r1.currentDialogFolderId
            if (r15 != 0) goto L_0x05cf
            org.telegram.tgnet.TLRPC$Chat r15 = r1.chat
            if (r15 == 0) goto L_0x0527
            boolean r11 = r15.scam
            if (r11 == 0) goto L_0x042e
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r11.checkText()
            goto L_0x043e
        L_0x042e:
            boolean r11 = r15.fake
            if (r11 == 0) goto L_0x043a
            r1.drawScam = r8
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r11.checkText()
            goto L_0x043e
        L_0x043a:
            boolean r11 = r15.verified
            r1.drawVerified = r11
        L_0x043e:
            boolean r11 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r11 == 0) goto L_0x05cf
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x04b9
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x044c
            goto L_0x04b9
        L_0x044c:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            long r2 = r11.id
            r28 = 0
            int r17 = (r2 > r28 ? 1 : (r2 == r28 ? 0 : -1))
            if (r17 < 0) goto L_0x046e
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r2 == 0) goto L_0x0463
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0463
            goto L_0x046e
        L_0x0463:
            r1.drawNameGroup = r5
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x0478
        L_0x046e:
            r1.drawNameBroadcast = r5
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x0478:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0498
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x048d
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x048f
        L_0x048d:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x048f:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x05cf
        L_0x0498:
            int r2 = r50.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x04a8
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04aa
        L_0x04a8:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04aa:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r2
            goto L_0x05cf
        L_0x04b9:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            r11 = r7
            long r6 = r2.id
            r28 = 0
            int r16 = (r6 > r28 ? 1 : (r6 == r28 ? 0 : -1))
            if (r16 < 0) goto L_0x04dc
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x04d1
            goto L_0x04dc
        L_0x04d1:
            r1.drawNameGroup = r5
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04e6
        L_0x04dc:
            r1.drawNameBroadcast = r5
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04e6:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0506
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x04fb
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04fd
        L_0x04fb:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04fd:
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 + r6
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x0506:
            int r2 = r50.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r2 = r2 - r6
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x0516
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0518
        L_0x0516:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0518:
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 - r6
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x0527:
            r11 = r7
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x05d0
            boolean r6 = r2.scam
            if (r6 == 0) goto L_0x0538
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0548
        L_0x0538:
            boolean r6 = r2.fake
            if (r6 == 0) goto L_0x0544
            r1.drawScam = r8
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x0548
        L_0x0544:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0548:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x05d0
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x05d0
            r1.drawNameBot = r5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0596
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x055d
            goto L_0x0596
        L_0x055d:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x057d
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 + r6
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x057d:
            int r2 = r50.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r2 = r2 - r6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 - r6
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x0596:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x05b6
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 + r6
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x05b6:
            int r2 = r50.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r2 = r2 - r6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r2 = r2 - r6
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x05d0
        L_0x05cf:
            r11 = r7
        L_0x05d0:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x05dc
            org.telegram.messenger.MessageObject r6 = r1.message
            if (r6 == 0) goto L_0x05dc
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            int r2 = r2.date
        L_0x05dc:
            boolean r6 = r1.isDialogCell
            if (r6 == 0) goto L_0x0638
            int r6 = r1.currentAccount
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
            r16 = r9
            long r8 = r1.currentDialogId
            r3 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r6 = r6.getDraft(r8, r3)
            r1.draftMessage = r6
            if (r6 == 0) goto L_0x060e
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0604
            org.telegram.tgnet.TLRPC$DraftMessage r6 = r1.draftMessage
            int r6 = r6.reply_to_msg_id
            if (r6 == 0) goto L_0x0602
            goto L_0x0604
        L_0x0602:
            r2 = 0
            goto L_0x0635
        L_0x0604:
            org.telegram.tgnet.TLRPC$DraftMessage r6 = r1.draftMessage
            int r6 = r6.date
            if (r2 <= r6) goto L_0x060e
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x0602
        L_0x060e:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0628
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r6 = r2.megagroup
            if (r6 != 0) goto L_0x0628
            boolean r6 = r2.creator
            if (r6 != 0) goto L_0x0628
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x0602
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x0602
        L_0x0628:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x063d
            boolean r6 = r2.left
            if (r6 != 0) goto L_0x0602
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x063d
            goto L_0x0602
        L_0x0635:
            r1.draftMessage = r2
            goto L_0x063d
        L_0x0638:
            r16 = r9
            r2 = 0
            r1.draftMessage = r2
        L_0x063d:
            if (r0 == 0) goto L_0x06d9
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r1.currentDialogId
            r3 = 0
            java.lang.Integer r2 = r2.getPrintingStringType(r8, r3)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0666
            int r2 = r2.getIntrinsicWidth()
            r6 = 1077936128(0x40400000, float:3.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r6
            goto L_0x0667
        L_0x0666:
            r2 = 0
        L_0x0667:
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>()
            int r8 = r1.printingStringType
            r9 = 5
            if (r8 != r9) goto L_0x067c
            java.lang.String r8 = r0.toString()
            java.lang.String r9 = "**oo**"
            int r8 = r8.indexOf(r9)
            goto L_0x067d
        L_0x067c:
            r8 = -1
        L_0x067d:
            if (r8 <= 0) goto L_0x06a7
            java.lang.String[] r2 = new java.lang.String[r5]
            java.lang.String r9 = "..."
            r3 = 0
            r2[r3] = r9
            java.lang.String[] r9 = new java.lang.String[r5]
            r9[r3] = r4
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r2, r9)
            android.text.SpannableStringBuilder r0 = r6.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r9 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r9 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r9)
            int r9 = r9.getIntrinsicWidth()
            r2.<init>(r9)
            int r9 = r8 + 6
            r0.setSpan(r2, r8, r9, r3)
            goto L_0x06c8
        L_0x06a7:
            r3 = 0
            java.lang.String r9 = " "
            android.text.SpannableStringBuilder r9 = r6.append(r9)
            java.lang.String[] r12 = new java.lang.String[r5]
            java.lang.String r13 = "..."
            r12[r3] = r13
            java.lang.String[] r13 = new java.lang.String[r5]
            r13[r3] = r4
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r12, r13)
            android.text.SpannableStringBuilder r0 = r9.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r9 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r9.<init>(r2)
            r0.setSpan(r9, r3, r5, r3)
        L_0x06c8:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r7 = r6
            r18 = r8
            r16 = r9
            r0 = 0
            r2 = 2
            r6 = 0
            r8 = 0
            goto L_0x0var_
        L_0x06d9:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0777
            r0 = 2131625273(0x7f0e0539, float:1.887775E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x071c
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0714
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x06fc
            goto L_0x0714
        L_0x06fc:
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_draft"
            r2.<init>(r9, r8)
            int r8 = r0.length()
            r3 = 0
            r9 = 33
            r6.setSpan(r2, r3, r8, r9)
            goto L_0x0774
        L_0x0714:
            r3 = 0
            r8 = r0
            r7 = r4
        L_0x0717:
            r0 = 0
            r2 = 2
            r6 = 0
            goto L_0x0791
        L_0x071c:
            r3 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r6 = r2.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r6 <= r8) goto L_0x072d
            java.lang.String r2 = r2.substring(r3, r8)
        L_0x072d:
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r6 = 32
            r9 = 10
            java.lang.String r2 = r2.replace(r9, r6)
            r8[r3] = r2
            r8[r5] = r0
            java.lang.String r2 = java.lang.String.format(r13, r8)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x0761
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 != 0) goto L_0x0761
            org.telegram.ui.Components.ForegroundColorSpanThemable r6 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_draft"
            r6.<init>(r9, r8)
            int r8 = r0.length()
            int r8 = r8 + r5
            r3 = 33
            r9 = 0
            r2.setSpan(r6, r9, r8, r3)
            goto L_0x0762
        L_0x0761:
            r9 = 0
        L_0x0762:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r3 = r3[r6]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r6 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r6, r9)
        L_0x0774:
            r8 = r0
            r7 = r6
            goto L_0x0717
        L_0x0777:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x0795
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625833(0x7f0e0769, float:1.8878885E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x078a:
            r7 = r6
        L_0x078b:
            r16 = r9
        L_0x078d:
            r0 = 1
            r2 = 2
            r6 = 0
            r8 = 0
        L_0x0791:
            r18 = -1
            goto L_0x0var_
        L_0x0795:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x0831
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x080f
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r9 = r2[r6]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x07b1
            r0 = 2131625368(0x7f0e0598, float:1.8877942E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x078a
        L_0x07b1:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x07ca
            r0 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r6 = r1.user
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r3 = 0
            r2[r3] = r6
            java.lang.String r6 = "AwaitingEncryption"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            goto L_0x078a
        L_0x07ca:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x07d8
            r0 = 2131625369(0x7f0e0599, float:1.8877944E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x078a
        L_0x07d8:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x080c
            long r12 = r0.admin_id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r16 = r0.getClientUserId()
            int r0 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r0 != 0) goto L_0x0801
            r0 = 2131625357(0x7f0e058d, float:1.887792E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r6 = r1.user
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r6)
            r3 = 0
            r2[r3] = r6
            java.lang.String r6 = "EncryptedChatStartedOutgoing"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            goto L_0x078a
        L_0x0801:
            r0 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x078a
        L_0x080c:
            r7 = r4
            goto L_0x078b
        L_0x080f:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x082e
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x082e
            r0 = 2131627474(0x7f0e0dd2, float:1.8882213E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r7 = r6
            r0 = 1
            r2 = 2
            r5 = 0
            r6 = 0
            r8 = 0
            r10 = 0
            goto L_0x0791
        L_0x082e:
            r7 = r4
            goto L_0x078d
        L_0x0831:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r2 = r1.message
            long r8 = r2.getFromChatId()
            boolean r2 = org.telegram.messenger.DialogObject.isUserDialog(r8)
            if (r2 == 0) goto L_0x0855
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r6 = 0
            goto L_0x0866
        L_0x0855:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = -r8
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r6 = r2
            r2 = 0
        L_0x0866:
            int r8 = r1.dialogsType
            r9 = 3
            if (r8 != r9) goto L_0x0885
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x0885
            r0 = 2131627474(0x7f0e0dd2, float:1.8882213E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r7 = r0
            r0 = 1
            r2 = 2
            r5 = 0
            r6 = 0
            r8 = 0
            r10 = 0
            goto L_0x0f3e
        L_0x0885:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x089c
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x089c
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x089c
            java.lang.CharSequence r0 = r50.formatArchivedDialogNames()
            r7 = r0
            r0 = 0
        L_0x0897:
            r2 = 2
        L_0x0898:
            r6 = 0
            r8 = 0
            goto L_0x0f3e
        L_0x089c:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r8 == 0) goto L_0x08cc
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x08bd
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x08ba
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x08bd
        L_0x08ba:
            r0 = r4
            r10 = 0
            goto L_0x08c1
        L_0x08bd:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x08c1:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r9 = r2[r6]
            r7 = r0
            r16 = r9
            r0 = 1
            goto L_0x0897
        L_0x08cc:
            boolean r8 = android.text.TextUtils.isEmpty(r0)
            if (r8 == 0) goto L_0x09cc
            int r8 = r1.currentDialogFolderId
            if (r8 != 0) goto L_0x09cc
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r1.encryptedChat
            if (r8 != 0) goto L_0x09cc
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.needDrawBluredPreview()
            if (r8 != 0) goto L_0x09cc
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 != 0) goto L_0x08fa
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isNewGif()
            if (r8 != 0) goto L_0x08fa
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x09cc
        L_0x08fa:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isWebpage()
            if (r8 == 0) goto L_0x090d
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r8 = r8.type
            goto L_0x090e
        L_0x090d:
            r8 = 0
        L_0x090e:
            java.lang.String r9 = "app"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x09cc
            java.lang.String r9 = "profile"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x09cc
            java.lang.String r9 = "article"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x09cc
            if (r8 == 0) goto L_0x0930
            java.lang.String r9 = "telegram_"
            boolean r8 = r8.startsWith(r9)
            if (r8 != 0) goto L_0x09cc
        L_0x0930:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r3)
            if (r8 != r3) goto L_0x0949
            r3 = 0
        L_0x0949:
            if (r8 == 0) goto L_0x09cc
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.isVideo()
            r1.drawPlay = r9
            java.lang.String r9 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.mediaExists
            if (r7 != 0) goto L_0x0998
            int r7 = r1.currentAccount
            org.telegram.messenger.DownloadController r7 = org.telegram.messenger.DownloadController.getInstance(r7)
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r7 = r7.canDownloadMedia((org.telegram.messenger.MessageObject) r15)
            if (r7 != 0) goto L_0x0998
            int r7 = r1.currentAccount
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)
            boolean r7 = r7.isLoadingFile(r9)
            if (r7 == 0) goto L_0x097a
            goto L_0x0998
        L_0x097a:
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            r31 = 0
            r32 = 0
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLObject r7 = r7.photoThumbsObject
            org.telegram.messenger.ImageLocation r33 = org.telegram.messenger.ImageLocation.getForObject(r8, r7)
            r35 = 0
            org.telegram.messenger.MessageObject r7 = r1.message
            r37 = 0
            java.lang.String r34 = "20_20"
            r30 = r3
            r36 = r7
            r30.setImage((org.telegram.messenger.ImageLocation) r31, (java.lang.String) r32, (org.telegram.messenger.ImageLocation) r33, (java.lang.String) r34, (android.graphics.drawable.Drawable) r35, (java.lang.Object) r36, (int) r37)
            goto L_0x09ca
        L_0x0998:
            org.telegram.messenger.MessageObject r7 = r1.message
            int r9 = r7.type
            if (r9 != r5) goto L_0x09a7
            if (r3 == 0) goto L_0x09a3
            int r9 = r3.size
            goto L_0x09a4
        L_0x09a3:
            r9 = 0
        L_0x09a4:
            r35 = r9
            goto L_0x09a9
        L_0x09a7:
            r35 = 0
        L_0x09a9:
            org.telegram.messenger.ImageReceiver r9 = r1.thumbImage
            org.telegram.tgnet.TLObject r7 = r7.photoThumbsObject
            org.telegram.messenger.ImageLocation r31 = org.telegram.messenger.ImageLocation.getForObject(r3, r7)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r33 = org.telegram.messenger.ImageLocation.getForObject(r8, r3)
            r36 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r38 = 0
            java.lang.String r32 = "20_20"
            java.lang.String r34 = "20_20"
            r30 = r9
            r37 = r3
            r30.setImage(r31, r32, r33, r34, r35, r36, r37, r38)
        L_0x09ca:
            r8 = 0
            goto L_0x09cd
        L_0x09cc:
            r8 = 1
        L_0x09cd:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0ceb
            r7 = r6
            long r5 = r3.id
            r30 = 0
            int r15 = (r5 > r30 ? 1 : (r5 == r30 ? 0 : -1))
            if (r15 <= 0) goto L_0x0ceb
            if (r7 != 0) goto L_0x0ceb
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x09ea
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isMegagroup(r3)
            if (r3 == 0) goto L_0x0ceb
        L_0x09ea:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x09fc
            r2 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.String r3 = "FromYou"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0a43
        L_0x09fc:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x0a0c
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            if (r3 == 0) goto L_0x0a0c
            java.lang.String r3 = r3.from_name
            if (r3 == 0) goto L_0x0a0c
            r2 = r3
            goto L_0x0a43
        L_0x0a0c:
            if (r2 == 0) goto L_0x0a41
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0a22
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a17
            goto L_0x0a22
        L_0x0a17:
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            java.lang.String r3 = "\n"
            java.lang.String r2 = r2.replace(r3, r4)
            goto L_0x0a43
        L_0x0a22:
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r2)
            if (r3 == 0) goto L_0x0a32
            r2 = 2131625820(0x7f0e075c, float:1.8878859E38)
            java.lang.String r3 = "HiddenName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x0a43
        L_0x0a32:
            java.lang.String r3 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r3, r2)
            java.lang.String r3 = "\n"
            java.lang.String r2 = r2.replace(r3, r4)
            goto L_0x0a43
        L_0x0a41:
            java.lang.String r2 = "DELETED"
        L_0x0a43:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0a5d
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r3 = 0
            r5[r3] = r0
            r6 = 1
            r5[r6] = r2
            java.lang.String r0 = java.lang.String.format(r13, r5)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0a5a:
            r6 = r0
            goto L_0x0c5e
        L_0x0a5d:
            r3 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0ad0
            java.lang.String r0 = r5.toString()
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0a74
            java.lang.String r0 = r0.substring(r3, r6)
        L_0x0a74:
            if (r8 != 0) goto L_0x0a79
            r5 = r4
        L_0x0a77:
            r6 = 2
            goto L_0x0aa8
        L_0x0a79:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVideo()
            if (r5 == 0) goto L_0x0a84
            java.lang.String r5 = "📹 "
            goto L_0x0a77
        L_0x0a84:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVoice()
            if (r5 == 0) goto L_0x0a8f
            java.lang.String r5 = "🎤 "
            goto L_0x0a77
        L_0x0a8f:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isMusic()
            if (r5 == 0) goto L_0x0a9a
            java.lang.String r5 = "🎧 "
            goto L_0x0a77
        L_0x0a9a:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isPhoto()
            if (r5 == 0) goto L_0x0aa5
            java.lang.String r5 = "🖼 "
            goto L_0x0a77
        L_0x0aa5:
            java.lang.String r5 = "📎 "
            goto L_0x0a77
        L_0x0aa8:
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r5 = 32
            r12 = 10
            java.lang.String r0 = r0.replace(r12, r5)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r3 = 0
            r8[r3] = r0
            r5 = 1
            r8[r5] = r2
            java.lang.String r0 = java.lang.String.format(r13, r8)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0a5a
        L_0x0ad0:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            if (r5 == 0) goto L_0x0bc9
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0bc9
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r5 = r0[r5]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0b14
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
            r0 = 18
            if (r12 < r0) goto L_0x0b03
            r8 = 1
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
            java.lang.String r6 = r6.question
            r3 = 0
            r0[r3] = r6
            java.lang.String r6 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            goto L_0x0b2c
        L_0x0b03:
            r3 = 0
            r8 = 1
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
            java.lang.String r6 = r6.question
            r0[r3] = r6
            java.lang.String r6 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            goto L_0x0b2c
        L_0x0b14:
            r3 = 0
            r8 = 1
            boolean r9 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r9 == 0) goto L_0x0b3e
            r9 = 18
            if (r12 < r9) goto L_0x0b2f
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r0[r3] = r6
            java.lang.String r6 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r6, r0)
        L_0x0b2c:
            r6 = 32
            goto L_0x0b47
        L_0x0b2f:
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r0[r3] = r6
            java.lang.String r6 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            goto L_0x0b44
        L_0x0b3e:
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r8 == 0) goto L_0x0b4a
            java.lang.String r0 = r6.title
        L_0x0b44:
            r6 = 32
            r8 = 1
        L_0x0b47:
            r9 = 10
            goto L_0x0b90
        L_0x0b4a:
            int r6 = r0.type
            r8 = 14
            if (r6 != r8) goto L_0x0b88
            r6 = 18
            if (r12 < r6) goto L_0x0b6e
            r6 = 2
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.String r0 = r0.getMusicAuthor()
            r3 = 0
            r7[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8 = 1
            r7[r8] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            goto L_0x0b2c
        L_0x0b6e:
            r3 = 0
            r6 = 2
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.String r0 = r0.getMusicAuthor()
            r9[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r9[r8] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r9)
            goto L_0x0b2c
        L_0x0b88:
            r8 = 1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            goto L_0x0b2c
        L_0x0b90:
            java.lang.String r0 = r0.replace(r9, r6)
            r6 = 2
            java.lang.Object[] r12 = new java.lang.Object[r6]
            r3 = 0
            r12[r3] = r0
            r12[r8] = r2
            java.lang.String r0 = java.lang.String.format(r13, r12)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bc1 }
            java.lang.String r8 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x0bc1 }
            r0.<init>(r8, r12)     // Catch:{ Exception -> 0x0bc1 }
            if (r14 == 0) goto L_0x0bb6
            int r8 = r2.length()     // Catch:{ Exception -> 0x0bc1 }
            r7 = 2
            int r8 = r8 + r7
            goto L_0x0bb7
        L_0x0bb6:
            r8 = 0
        L_0x0bb7:
            int r12 = r6.length()     // Catch:{ Exception -> 0x0bc1 }
            r13 = 33
            r6.setSpan(r0, r8, r12, r13)     // Catch:{ Exception -> 0x0bc1 }
            goto L_0x0bc5
        L_0x0bc1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0bc5:
            r16 = r5
            goto L_0x0c5e
        L_0x0bc9:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            java.lang.String r5 = r5.message
            if (r5 == 0) goto L_0x0CLASSNAME
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0bde
            r5 = r0
        L_0x0bde:
            int r0 = r50.getMeasuredWidth()
            r6 = 1121058816(0x42d20000, float:105.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            if (r14 == 0) goto L_0x0c0b
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            if (r6 != 0) goto L_0x0bff
            float r0 = (float) r0
            java.lang.String r6 = r2.toString()
            r12 = r16
            float r6 = r12.measureText(r6)
            float r0 = r0 - r6
            int r0 = (int) r0
            goto L_0x0CLASSNAME
        L_0x0bff:
            r12 = r16
        L_0x0CLASSNAME:
            float r0 = (float) r0
            java.lang.String r6 = ": "
            float r6 = r12.measureText(r6)
            float r0 = r0 - r6
            int r0 = (int) r0
            goto L_0x0c0d
        L_0x0c0b:
            r12 = r16
        L_0x0c0d:
            if (r0 <= 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r3 = 0
            java.lang.Object r6 = r6.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r5, r6, r0, r12, r8)
            java.lang.String r5 = r0.toString()
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r3 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r12 = r16
            r3 = 0
            int r0 = r5.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r0 <= r6) goto L_0x0CLASSNAME
            java.lang.String r5 = r5.substring(r3, r6)
        L_0x0CLASSNAME:
            r6 = 32
            r8 = 10
            java.lang.String r0 = r5.replace(r8, r6)
            java.lang.String r5 = r0.trim()
        L_0x0CLASSNAME:
            r6 = 2
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r3] = r5
            r5 = 1
            r0[r5] = r2
            java.lang.String r0 = java.lang.String.format(r13, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r6 = r0
            r16 = r12
            goto L_0x0c5e
        L_0x0CLASSNAME:
            r12 = r16
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            goto L_0x0a5a
        L_0x0c5e:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r5 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider     // Catch:{ Exception -> 0x0CLASSNAME }
            r0.<init>(r5, r8)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r5 = r2.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            r8 = 1
            int r5 = r5 + r8
            r3 = 33
            r8 = 0
            r6.setSpan(r0, r8, r5, r3)     // Catch:{ Exception -> 0x0CLASSNAME }
            r0 = r5
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0c8b
        L_0x0CLASSNAME:
            r0 = move-exception
            r5 = 0
        L_0x0c8b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = 0
            r5 = 0
        L_0x0CLASSNAME:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r8 = r8[r12]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r3 = 0
            java.lang.CharSequence r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r8, r12, r3)
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x0cba
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r6, (java.util.ArrayList<java.lang.String>) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)
            if (r8 == 0) goto L_0x0cba
            r6 = r8
        L_0x0cba:
            boolean r8 = r1.hasMessageThumb
            if (r8 == 0) goto L_0x0ce3
            boolean r8 = r6 instanceof android.text.SpannableStringBuilder
            if (r8 != 0) goto L_0x0cc8
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r6)
            r6 = r8
        L_0x0cc8:
            r8 = r6
            android.text.SpannableStringBuilder r8 = (android.text.SpannableStringBuilder) r8
            java.lang.String r12 = " "
            r8.insert(r5, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r13 = r11 + 6
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.<init>(r13)
            int r13 = r5 + 1
            r14 = 33
            r8.setSpan(r12, r5, r13, r14)
        L_0x0ce3:
            r8 = r2
            r7 = r6
            r2 = 2
            r5 = 1
            r6 = r0
            r0 = 0
            goto L_0x0f3e
        L_0x0ceb:
            r12 = r16
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0cf6
        L_0x0cf3:
            r2 = 2
            goto L_0x0e93
        L_0x0cf6:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0d14
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r5 == 0) goto L_0x0d14
            int r5 = r2.ttl_seconds
            if (r5 == 0) goto L_0x0d14
            r0 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0cf3
        L_0x0d14:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0d2c
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r5 == 0) goto L_0x0d2c
            int r5 = r2.ttl_seconds
            if (r5 == 0) goto L_0x0d2c
            r0 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0cf3
        L_0x0d2c:
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0dd4
            if (r8 != 0) goto L_0x0d34
            r0 = r4
            goto L_0x0d60
        L_0x0d34:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0d3d
            java.lang.String r0 = "📹 "
            goto L_0x0d60
        L_0x0d3d:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0d48
            java.lang.String r0 = "🎤 "
            goto L_0x0d60
        L_0x0d48:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0d53
            java.lang.String r0 = "🎧 "
            goto L_0x0d60
        L_0x0d53:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0d5e
            java.lang.String r0 = "🖼 "
            goto L_0x0d60
        L_0x0d5e:
            java.lang.String r0 = "📎 "
        L_0x0d60:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0dbf
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0dbf
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r5 = r50.getMeasuredWidth()
            r6 = 1122893824(0x42ee0000, float:119.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            if (r14 == 0) goto L_0x0d97
            r6 = 0
            boolean r8 = android.text.TextUtils.isEmpty(r6)
            if (r8 == 0) goto L_0x0d96
            float r5 = (float) r5
            java.lang.String r8 = ": "
            float r8 = r12.measureText(r8)
            float r5 = r5 - r8
            int r5 = (int) r5
            goto L_0x0d97
        L_0x0d96:
            throw r6
        L_0x0d97:
            if (r5 <= 0) goto L_0x0dae
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r3 = 0
            java.lang.Object r6 = r6.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r6, r5, r12, r8)
            java.lang.String r2 = r2.toString()
        L_0x0dae:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r0)
            r5.append(r2)
            java.lang.String r0 = r5.toString()
            goto L_0x0cf3
        L_0x0dbf:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0cf3
        L_0x0dd4:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0df2
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "📊 "
            r0.append(r5)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0def:
            r2 = 2
            goto L_0x0e7e
        L_0x0df2:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0e12
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
            goto L_0x0def
        L_0x0e12:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r5 == 0) goto L_0x0e19
            java.lang.String r0 = r2.title
            goto L_0x0def
        L_0x0e19:
            int r2 = r0.type
            r5 = 14
            if (r2 != r5) goto L_0x0e39
            r2 = 2
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r3 = 0
            r5[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r6 = 1
            r5[r6] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0e7e
        L_0x0e39:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0e71
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e71
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r5 = r50.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r3 = 0
            java.lang.Object r6 = r6.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r5, r12, r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0e75
        L_0x0e71:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0e75:
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
        L_0x0e7e:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            if (r6 == 0) goto L_0x0e93
            boolean r5 = r5.isMediaEmpty()
            if (r5 != 0) goto L_0x0e93
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r5 = r5[r6]
            r12 = r5
        L_0x0e93:
            boolean r5 = r1.hasMessageThumb
            if (r5 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0ed3
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0ed3
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r5 = r50.getMeasuredWidth()
            int r7 = r11 + 95
            int r7 = r7 + 6
            float r6 = (float) r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r3 = 0
            java.lang.Object r6 = r6.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r5, r12, r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0ee4
        L_0x0ed3:
            r3 = 0
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0ee0
            java.lang.CharSequence r0 = r0.subSequence(r3, r6)
        L_0x0ee0:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0ee4:
            boolean r5 = r0 instanceof android.text.SpannableStringBuilder
            if (r5 != 0) goto L_0x0eee
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r0)
            r0 = r5
        L_0x0eee:
            r5 = r0
            android.text.SpannableStringBuilder r5 = (android.text.SpannableStringBuilder) r5
            java.lang.String r6 = " "
            r3 = 0
            r5.insert(r3, r6)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r6 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r7 = r11 + 6
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.<init>(r7)
            r7 = 33
            r8 = 1
            r5.setSpan(r6, r3, r8, r7)
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r7 = r1.paintIndex
            r6 = r6[r7]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r7 = 1099431936(0x41880000, float:17.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.messenger.Emoji.replaceEmoji(r5, r6, r8, r3)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.hasHighlightedWords()
            if (r6 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r5, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r5 == 0) goto L_0x0var_
            r7 = r5
            goto L_0x0var_
        L_0x0var_:
            r7 = r0
        L_0x0var_:
            r16 = r12
            r0 = 0
            goto L_0x0f3b
        L_0x0var_:
            r7 = r0
            r16 = r12
            r0 = 1
        L_0x0f3b:
            r5 = 1
            goto L_0x0898
        L_0x0f3e:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x0791
            java.lang.CharSequence r8 = r50.formatArchivedDialogNames()
            goto L_0x0791
        L_0x0var_:
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r1.draftMessage
            if (r12 == 0) goto L_0x0var_
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0f6d
        L_0x0var_:
            int r12 = r1.lastMessageDate
            if (r12 == 0) goto L_0x0f5e
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0f6d
        L_0x0f5e:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 == 0) goto L_0x0f6c
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0f6d
        L_0x0f6c:
            r12 = r4
        L_0x0f6d:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x0var_
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawCount = r3
            r1.drawMention = r3
            r1.drawError = r3
            r2 = 0
            r13 = 0
            goto L_0x1086
        L_0x0var_:
            r3 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0fc4
            int r13 = r1.unreadCount
            int r14 = r1.mentionCount
            int r17 = r13 + r14
            if (r17 <= 0) goto L_0x0fbd
            if (r13 <= r14) goto L_0x0fa6
            r9 = 1
            r1.drawCount = r9
            r1.drawMention = r3
            java.lang.Object[] r2 = new java.lang.Object[r9]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r2[r3] = r13
            java.lang.String r13 = "%d"
            java.lang.String r2 = java.lang.String.format(r13, r2)
            goto L_0x0fc2
        L_0x0fa6:
            r9 = 1
            r1.drawCount = r3
            r1.drawMention = r9
            java.lang.Object[] r2 = new java.lang.Object[r9]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r2[r3] = r13
            java.lang.String r13 = "%d"
            java.lang.String r2 = java.lang.String.format(r13, r2)
            r13 = r2
            r2 = 0
            goto L_0x100e
        L_0x0fbd:
            r1.drawCount = r3
            r1.drawMention = r3
            r2 = 0
        L_0x0fc2:
            r13 = 0
            goto L_0x100e
        L_0x0fc4:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0fcf
            r1.drawCount = r3
            r2 = 0
            r3 = 0
            r9 = 1
            r10 = 0
            goto L_0x1002
        L_0x0fcf:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0ff5
            r9 = 1
            if (r2 != r9) goto L_0x0fe2
            int r14 = r1.mentionCount
            if (r2 != r14) goto L_0x0fe2
            if (r13 == 0) goto L_0x0fe2
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x0ff5
        L_0x0fe2:
            r9 = 1
            r1.drawCount = r9
            java.lang.Object[] r13 = new java.lang.Object[r9]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = 0
            r13[r3] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r13)
            goto L_0x1002
        L_0x0ff5:
            r3 = 0
            r9 = 1
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0fff
            r1.drawCount = r9
            r2 = r4
            goto L_0x1002
        L_0x0fff:
            r1.drawCount = r3
            r2 = 0
        L_0x1002:
            int r13 = r1.mentionCount
            if (r13 == 0) goto L_0x100b
            r1.drawMention = r9
            java.lang.String r13 = "@"
            goto L_0x100e
        L_0x100b:
            r1.drawMention = r3
            goto L_0x0fc2
        L_0x100e:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isOut()
            if (r14 == 0) goto L_0x107d
            org.telegram.tgnet.TLRPC$DraftMessage r14 = r1.draftMessage
            if (r14 != 0) goto L_0x107d
            if (r10 == 0) goto L_0x107d
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r14 = r14.action
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r14 != 0) goto L_0x107d
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x1037
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r9 = 1
            r1.drawClock = r9
            r1.drawError = r3
            goto L_0x1086
        L_0x1037:
            r3 = 0
            r9 = 1
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSendError()
            if (r10 == 0) goto L_0x104e
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawError = r9
            r1.drawCount = r3
            r1.drawMention = r3
            goto L_0x107b
        L_0x104e:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x107b
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x106f
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x106d
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x106d
            goto L_0x106f
        L_0x106d:
            r10 = 0
            goto L_0x1070
        L_0x106f:
            r10 = 1
        L_0x1070:
            r1.drawCheck1 = r10
            r9 = 1
            r1.drawCheck2 = r9
            r3 = 0
            r1.drawClock = r3
            r1.drawError = r3
            goto L_0x1086
        L_0x107b:
            r3 = 0
            goto L_0x1086
        L_0x107d:
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawError = r3
        L_0x1086:
            r1.promoDialog = r3
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r14 = r1.dialogsType
            if (r14 != 0) goto L_0x10eb
            r14 = r4
            long r3 = r1.currentDialogId
            r9 = 1
            boolean r3 = r10.isPromoDialog(r3, r9)
            if (r3 == 0) goto L_0x10ec
            r1.drawPinBackground = r9
            r1.promoDialog = r9
            int r3 = r10.promoDialogType
            int r4 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r3 != r4) goto L_0x10b1
            r3 = 2131628113(0x7f0e1051, float:1.888351E38)
            java.lang.String r4 = "UseProxySponsor"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r12 = r3
            goto L_0x10ec
        L_0x10b1:
            int r4 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r3 != r4) goto L_0x10ec
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "PsaType_"
            r3.append(r4)
            java.lang.String r4 = r10.promoPsaType
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 == 0) goto L_0x10db
            r3 = 2131627267(0x7f0e0d03, float:1.8881794E38)
            java.lang.String r4 = "PsaTypeDefault"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x10db:
            r4 = r3
            java.lang.String r3 = r10.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x10e9
            java.lang.String r7 = r10.promoPsaMessage
            r3 = 0
            r1.hasMessageThumb = r3
        L_0x10e9:
            r12 = r4
            goto L_0x10ec
        L_0x10eb:
            r14 = r4
        L_0x10ec:
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x1103
            r4 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r10 = "ArchivedChats"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
        L_0x10f9:
            r10 = r0
            r0 = r4
            r3 = r13
            r4 = r16
            r13 = r2
            r2 = r7
            r7 = r18
            goto L_0x115e
        L_0x1103:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x110a
            java.lang.String r4 = r4.title
            goto L_0x114e
        L_0x110a:
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            if (r4 == 0) goto L_0x114d
            boolean r4 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r4)
            if (r4 == 0) goto L_0x111e
            r4 = 2131627354(0x7f0e0d5a, float:1.888197E38)
            java.lang.String r10 = "RepliesTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            goto L_0x114e
        L_0x111e:
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r4)
            if (r4 == 0) goto L_0x1146
            boolean r4 = r1.useMeForMyMessages
            if (r4 == 0) goto L_0x1134
            r4 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            goto L_0x114e
        L_0x1134:
            int r4 = r1.dialogsType
            r10 = 3
            if (r4 != r10) goto L_0x113c
            r4 = 1
            r1.drawPinBackground = r4
        L_0x113c:
            r4 = 2131627473(0x7f0e0dd1, float:1.8882211E38)
            java.lang.String r10 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            goto L_0x114e
        L_0x1146:
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            goto L_0x114e
        L_0x114d:
            r4 = r14
        L_0x114e:
            int r10 = r4.length()
            if (r10 != 0) goto L_0x10f9
            r4 = 2131625820(0x7f0e075c, float:1.8878859E38)
            java.lang.String r10 = "HiddenName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            goto L_0x10f9
        L_0x115e:
            if (r5 == 0) goto L_0x11a2
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r5 = r5.measureText(r12)
            r18 = r10
            double r9 = (double) r5
            double r9 = java.lang.Math.ceil(r9)
            int r5 = (int) r9
            android.text.StaticLayout r9 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r9
            r30 = r12
            r32 = r5
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.timeLayout = r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x1198
            int r9 = r50.getMeasuredWidth()
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r9 = r9 - r5
            r1.timeLeft = r9
            goto L_0x11a0
        L_0x1198:
            r9 = 1097859072(0x41700000, float:15.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.timeLeft = r9
        L_0x11a0:
            r9 = r5
            goto L_0x11ab
        L_0x11a2:
            r18 = r10
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r9 = 0
        L_0x11ab:
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 != 0) goto L_0x11bf
            int r10 = r50.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r10 = r10 - r12
            r12 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r10 = r10 - r12
            int r10 = r10 - r9
            goto L_0x11d3
        L_0x11bf:
            int r10 = r50.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r10 = r10 - r12
            r12 = 1117388800(0x429a0000, float:77.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r10 = r10 - r12
            int r10 = r10 - r9
            int r12 = r1.nameLeft
            int r12 = r12 + r9
            r1.nameLeft = r12
        L_0x11d3:
            boolean r12 = r1.drawNameLock
            if (r12 == 0) goto L_0x11e7
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r17 = r17.getIntrinsicWidth()
        L_0x11e3:
            int r12 = r12 + r17
            int r10 = r10 - r12
            goto L_0x121a
        L_0x11e7:
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x11f8
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r17 = r17.getIntrinsicWidth()
            goto L_0x11e3
        L_0x11f8:
            boolean r12 = r1.drawNameBroadcast
            if (r12 == 0) goto L_0x1209
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r17 = r17.getIntrinsicWidth()
            goto L_0x11e3
        L_0x1209:
            boolean r12 = r1.drawNameBot
            if (r12 == 0) goto L_0x121a
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r17 = r17.getIntrinsicWidth()
            goto L_0x11e3
        L_0x121a:
            boolean r12 = r1.drawClock
            r17 = 1084227584(0x40a00000, float:5.0)
            if (r12 == 0) goto L_0x1249
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r12 = r12.getIntrinsicWidth()
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r12 = r12 + r23
            int r10 = r10 - r12
            boolean r23 = org.telegram.messenger.LocaleController.isRTL
            if (r23 != 0) goto L_0x1238
            int r9 = r1.timeLeft
            int r9 = r9 - r12
            r1.clockDrawLeft = r9
            goto L_0x12bf
        L_0x1238:
            int r5 = r1.timeLeft
            int r5 = r5 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = r5 + r9
            r1.clockDrawLeft = r5
            int r5 = r1.nameLeft
            int r5 = r5 + r12
            r1.nameLeft = r5
            goto L_0x12bf
        L_0x1249:
            boolean r5 = r1.drawCheck2
            if (r5 == 0) goto L_0x12bf
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r5 = r5.getIntrinsicWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = r5 + r12
            int r10 = r10 - r5
            boolean r12 = r1.drawCheck1
            if (r12 == 0) goto L_0x12a6
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r12 = r12.getIntrinsicWidth()
            r27 = 1090519040(0x41000000, float:8.0)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r27)
            int r12 = r12 - r27
            int r10 = r10 - r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x127f
            int r9 = r1.timeLeft
            int r9 = r9 - r5
            r1.halfCheckDrawLeft = r9
            r5 = 1085276160(0x40b00000, float:5.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r9 = r9 - r5
            r1.checkDrawLeft = r9
            goto L_0x12bf
        L_0x127f:
            int r12 = r1.timeLeft
            int r12 = r12 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r12 = r12 + r9
            r1.checkDrawLeft = r12
            r9 = 1085276160(0x40b00000, float:5.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r12 = r12 + r9
            r1.halfCheckDrawLeft = r12
            int r9 = r1.nameLeft
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r12 = r12.getIntrinsicWidth()
            int r5 = r5 + r12
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 - r12
            int r9 = r9 + r5
            r1.nameLeft = r9
            goto L_0x12bf
        L_0x12a6:
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x12b0
            int r9 = r1.timeLeft
            int r9 = r9 - r5
            r1.checkDrawLeft1 = r9
            goto L_0x12bf
        L_0x12b0:
            int r12 = r1.timeLeft
            int r12 = r12 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r12 = r12 + r9
            r1.checkDrawLeft1 = r12
            int r9 = r1.nameLeft
            int r9 = r9 + r5
            r1.nameLeft = r9
        L_0x12bf:
            boolean r5 = r1.dialogMuted
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r5 == 0) goto L_0x12e3
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x12e3
            int r5 = r1.drawScam
            if (r5 != 0) goto L_0x12e3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r9 = r9.getIntrinsicWidth()
            int r5 = r5 + r9
            int r10 = r10 - r5
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x131e
            int r9 = r1.nameLeft
            int r9 = r9 + r5
            r1.nameLeft = r9
            goto L_0x131e
        L_0x12e3:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x12fd
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            int r5 = r5 + r9
            int r10 = r10 - r5
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x131e
            int r9 = r1.nameLeft
            int r9 = r9 + r5
            r1.nameLeft = r9
            goto L_0x131e
        L_0x12fd:
            int r5 = r1.drawScam
            if (r5 == 0) goto L_0x131e
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r9 = r1.drawScam
            r15 = 1
            if (r9 != r15) goto L_0x130d
            org.telegram.ui.Components.ScamDrawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x130f
        L_0x130d:
            org.telegram.ui.Components.ScamDrawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x130f:
            int r15 = r15.getIntrinsicWidth()
            int r5 = r5 + r15
            int r10 = r10 - r5
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x131e
            int r15 = r1.nameLeft
            int r15 = r15 + r5
            r1.nameLeft = r15
        L_0x131e:
            r5 = 1094713344(0x41400000, float:12.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r10 = java.lang.Math.max(r15, r10)
            r9 = 10
            r15 = 32
            java.lang.String r0 = r0.replace(r9, r15)     // Catch:{ Exception -> 0x1378 }
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1378 }
            int r15 = r1.paintIndex     // Catch:{ Exception -> 0x1378 }
            r9 = r9[r15]     // Catch:{ Exception -> 0x1378 }
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x1378 }
            int r15 = r10 - r15
            float r15 = (float) r15     // Catch:{ Exception -> 0x1378 }
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1378 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r9, r15, r12)     // Catch:{ Exception -> 0x1378 }
            org.telegram.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x1378 }
            if (r9 == 0) goto L_0x135c
            boolean r9 = r9.hasHighlightedWords()     // Catch:{ Exception -> 0x1378 }
            if (r9 == 0) goto L_0x135c
            org.telegram.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x1378 }
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords     // Catch:{ Exception -> 0x1378 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x1378 }
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x1378 }
            if (r9 == 0) goto L_0x135c
            r30 = r9
            goto L_0x135e
        L_0x135c:
            r30 = r0
        L_0x135e:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1378 }
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1378 }
            int r12 = r1.paintIndex     // Catch:{ Exception -> 0x1378 }
            r31 = r9[r12]     // Catch:{ Exception -> 0x1378 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1378 }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r32 = r10
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x1378 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x1378 }
            goto L_0x137c
        L_0x1378:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x137c:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1432
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1386
            goto L_0x1432
        L_0x1386:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.messageNameTop = r9
            r9 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.timeTop = r9
            r9 = 1109131264(0x421CLASSNAME, float:39.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.errorTop = r9
            r9 = 1109131264(0x421CLASSNAME, float:39.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.pinTop = r9
            r9 = 1109131264(0x421CLASSNAME, float:39.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.countTop = r9
            r9 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.checkDrawTop = r12
            int r9 = r50.getMeasuredWidth()
            r12 = 1119748096(0x42be0000, float:95.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r9 = r9 - r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x13e8
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r12
            r1.messageLeft = r12
            int r12 = r50.getMeasuredWidth()
            r15 = 1115684864(0x42800000, float:64.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = r12 - r15
            int r15 = r11 + 11
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r12 - r15
            goto L_0x13fd
        L_0x13e8:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.messageNameLeft = r12
            r1.messageLeft = r12
            r12 = 1092616192(0x41200000, float:10.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r12
        L_0x13fd:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r12 = (float) r12
            r19 = r9
            float r9 = (float) r0
            r24 = 1113063424(0x42580000, float:54.0)
            r37 = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r14 = (float) r14
            r38 = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r7 = (float) r7
            r5.setImageCoords(r12, r9, r14, r7)
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            float r7 = (float) r15
            r9 = 1106247680(0x41var_, float:30.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            float r12 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r14 = (float) r14
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r5.setImageCoords(r7, r9, r14, r12)
            r5 = r0
            goto L_0x14e1
        L_0x1432:
            r38 = r7
            r37 = r14
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameTop = r5
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.pinTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.countTop = r5
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.checkDrawTop = r5
            int r5 = r50.getMeasuredWidth()
            r7 = 1119485952(0x42ba0000, float:93.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r9 = r5 - r7
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x149a
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            int r5 = r50.getMeasuredWidth()
            r7 = 1115947008(0x42840000, float:66.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r5 - r7
            goto L_0x14af
        L_0x149a:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 1116340224(0x428a0000, float:69.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r5
        L_0x14af:
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            float r5 = (float) r5
            float r14 = (float) r0
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r19 = 1113587712(0x42600000, float:56.0)
            r24 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r9 = (float) r9
            r12.setImageCoords(r5, r14, r15, r9)
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            float r7 = (float) r7
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r25)
            float r12 = (float) r12
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r25)
            float r14 = (float) r14
            r5.setImageCoords(r7, r9, r12, r14)
            r5 = r0
            r19 = r24
        L_0x14e1:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1506
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x14fe
            int r0 = r50.getMeasuredWidth()
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r7 = r7.getIntrinsicWidth()
            int r0 = r0 - r7
            r7 = 1096810496(0x41600000, float:14.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            r1.pinLeft = r0
            goto L_0x1506
        L_0x14fe:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x1506:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x153a
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r19 = r19 - r0
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x1524
            int r0 = r50.getMeasuredWidth()
            r3 = 1107820544(0x42080000, float:34.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            r1.errorLeft = r0
            goto L_0x1536
        L_0x1524:
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.errorLeft = r3
            int r3 = r1.messageLeft
            int r3 = r3 + r0
            r1.messageLeft = r3
            int r3 = r1.messageNameLeft
            int r3 = r3 + r0
            r1.messageNameLeft = r3
        L_0x1536:
            r0 = r19
            goto L_0x1655
        L_0x153a:
            if (r13 != 0) goto L_0x1566
            if (r3 == 0) goto L_0x153f
            goto L_0x1566
        L_0x153f:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1560
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r3
            int r19 = r19 - r0
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x1560
            int r3 = r1.messageLeft
            int r3 = r3 + r0
            r1.messageLeft = r3
            int r3 = r1.messageNameLeft
            int r3 = r3 + r0
            r1.messageNameLeft = r3
        L_0x1560:
            r3 = 0
            r1.drawCount = r3
            r1.drawMention = r3
            goto L_0x1536
        L_0x1566:
            if (r13 == 0) goto L_0x15c9
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r13)
            double r14 = (double) r9
            double r14 = java.lang.Math.ceil(r14)
            int r9 = (int) r14
            int r0 = java.lang.Math.max(r0, r9)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.countWidth
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r13
            r32 = r9
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 + r9
            int r19 = r19 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x15b5
            int r0 = r50.getMeasuredWidth()
            int r9 = r1.countWidth
            int r0 = r0 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r9
            r1.countLeft = r0
            goto L_0x15c5
        L_0x15b5:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.countLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x15c5:
            r9 = 1
            r1.drawCount = r9
            goto L_0x15cc
        L_0x15c9:
            r7 = 0
            r1.countWidth = r7
        L_0x15cc:
            if (r3 == 0) goto L_0x1536
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1604
            r12 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r12 = r12.measureText(r3)
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            int r0 = java.lang.Math.max(r0, r12)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r12 = r1.mentionWidth
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r3
            r32 = r12
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.mentionLayout = r0
            goto L_0x160c
        L_0x1604:
            r3 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.mentionWidth = r0
        L_0x160c:
            int r0 = r1.mentionWidth
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 + r3
            int r19 = r19 - r0
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x1634
            int r0 = r50.getMeasuredWidth()
            int r3 = r1.mentionWidth
            int r0 = r0 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r3
            int r3 = r1.countWidth
            if (r3 == 0) goto L_0x162f
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r3 = r3 + r12
            goto L_0x1630
        L_0x162f:
            r3 = 0
        L_0x1630:
            int r0 = r0 - r3
            r1.mentionLeft = r0
            goto L_0x1650
        L_0x1634:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r12 = r1.countWidth
            if (r12 == 0) goto L_0x1642
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r12 = r12 + r13
            goto L_0x1643
        L_0x1642:
            r12 = 0
        L_0x1643:
            int r3 = r3 + r12
            r1.mentionLeft = r3
            int r3 = r1.messageLeft
            int r3 = r3 + r0
            r1.messageLeft = r3
            int r3 = r1.messageNameLeft
            int r3 = r3 + r0
            r1.messageNameLeft = r3
        L_0x1650:
            r3 = 1
            r1.drawMention = r3
            goto L_0x1536
        L_0x1655:
            if (r18 == 0) goto L_0x16ab
            if (r2 != 0) goto L_0x165b
            r2 = r37
        L_0x165b:
            java.lang.String r2 = r2.toString()
            int r3 = r2.length()
            r12 = 150(0x96, float:2.1E-43)
            if (r3 <= r12) goto L_0x166c
            r3 = 0
            java.lang.String r2 = r2.substring(r3, r12)
        L_0x166c:
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x1674
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x1676
        L_0x1674:
            if (r8 == 0) goto L_0x167f
        L_0x1676:
            r7 = 32
            r12 = 10
            java.lang.String r2 = r2.replace(r12, r7)
            goto L_0x1687
        L_0x167f:
            java.lang.String r7 = "\n\n"
            java.lang.String r12 = "\n"
            java.lang.String r2 = r2.replace(r7, r12)
        L_0x1687:
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r7 = r7[r12]
            android.graphics.Paint$FontMetricsInt r7 = r7.getFontMetricsInt()
            r12 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r3 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r7, r12, r3)
            org.telegram.messenger.MessageObject r7 = r1.message
            if (r7 == 0) goto L_0x16ab
            java.util.ArrayList<java.lang.String> r7 = r7.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)
            if (r7 == 0) goto L_0x16ab
            r2 = r7
        L_0x16ab:
            r7 = 1094713344(0x41400000, float:12.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = java.lang.Math.max(r12, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x16bd
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x1714
        L_0x16bd:
            if (r8 == 0) goto L_0x1714
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x16c8
            int r12 = r1.currentDialogFolderDialogsCount
            r9 = 1
            if (r12 != r9) goto L_0x1714
        L_0x16c8:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x16fa }
            if (r0 == 0) goto L_0x16df
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x16fa }
            if (r0 == 0) goto L_0x16df
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x16fa }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x16fa }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x16fa }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r8, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x16fa }
            if (r0 == 0) goto L_0x16df
            r8 = r0
        L_0x16df:
            android.text.TextPaint r40 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x16fa }
            android.text.Layout$Alignment r42 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16fa }
            r43 = 1065353216(0x3var_, float:1.0)
            r44 = 0
            r45 = 0
            android.text.TextUtils$TruncateAt r46 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16fa }
            r48 = 1
            r39 = r8
            r41 = r7
            r47 = r7
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r39, r40, r41, r42, r43, r44, r45, r46, r47, r48)     // Catch:{ Exception -> 0x16fa }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x16fa }
            goto L_0x16fe
        L_0x16fa:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16fe:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r12 = 1109393408(0x42200000, float:40.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 + r12
            float r5 = (float) r5
            r0.setImageY(r5)
            goto L_0x173c
        L_0x1714:
            r12 = 0
            r1.messageNameLayout = r12
            if (r0 != 0) goto L_0x1727
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x171e
            goto L_0x1727
        L_0x171e:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x173c
        L_0x1727:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r12 = 1101529088(0x41a80000, float:21.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 + r12
            float r5 = (float) r5
            r0.setImageY(r5)
        L_0x173c:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x17da }
            if (r0 != 0) goto L_0x1744
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17da }
            if (r5 == 0) goto L_0x1755
        L_0x1744:
            int r5 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x17da }
            if (r5 == 0) goto L_0x1755
            int r5 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x17da }
            r9 = 1
            if (r5 <= r9) goto L_0x1755
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x17da }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x17da }
            r4 = r0[r2]     // Catch:{ Exception -> 0x17da }
            r2 = 0
            goto L_0x1771
        L_0x1755:
            if (r0 != 0) goto L_0x175b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17da }
            if (r0 == 0) goto L_0x175d
        L_0x175b:
            if (r8 == 0) goto L_0x176c
        L_0x175d:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x17da }
            int r0 = r7 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x17da }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x17da }
            java.lang.CharSequence r2 = android.text.TextUtils.ellipsize(r2, r4, r0, r5)     // Catch:{ Exception -> 0x17da }
        L_0x176c:
            r49 = r8
            r8 = r2
            r2 = r49
        L_0x1771:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x17da }
            if (r0 != 0) goto L_0x17a9
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17da }
            if (r0 == 0) goto L_0x177a
            goto L_0x17a9
        L_0x177a:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x17da }
            if (r0 == 0) goto L_0x1795
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x17da }
            int r0 = r0 + r11
            int r7 = r7 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x17da }
            if (r0 == 0) goto L_0x1795
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x17da }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x17da }
            int r2 = r11 + r5
            int r0 = r0 - r2
            r1.messageLeft = r0     // Catch:{ Exception -> 0x17da }
        L_0x1795:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x17da }
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x17da }
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r0
            r13 = r8
            r14 = r4
            r15 = r7
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x17da }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x17da }
            goto L_0x17e1
        L_0x17a9:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x17da }
            if (r0 == 0) goto L_0x17b6
            if (r2 == 0) goto L_0x17b6
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x17da }
            int r7 = r7 + r0
        L_0x17b6:
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x17da }
            r16 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x17da }
            float r0 = (float) r0     // Catch:{ Exception -> 0x17da }
            r18 = 0
            android.text.TextUtils$TruncateAt r19 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x17da }
            if (r2 == 0) goto L_0x17ca
            r21 = 1
            goto L_0x17cc
        L_0x17ca:
            r21 = 2
        L_0x17cc:
            r12 = r8
            r13 = r4
            r14 = r7
            r17 = r0
            r20 = r7
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x17da }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x17da }
            goto L_0x17e1
        L_0x17da:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x17e1:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x191d
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x18a6
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18a6
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r4 = r3.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x1833
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1833
            int r2 = r1.drawScam
            if (r2 != 0) goto L_0x1833
            int r2 = r1.nameLeft
            double r11 = (double) r2
            double r13 = (double) r10
            java.lang.Double.isNaN(r13)
            double r13 = r13 - r4
            java.lang.Double.isNaN(r11)
            double r11 = r11 + r13
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r2.getIntrinsicWidth()
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            int r2 = (int) r11
            r1.nameMuteLeft = r2
            goto L_0x188e
        L_0x1833:
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x185d
            int r2 = r1.nameLeft
            double r11 = (double) r2
            double r13 = (double) r10
            java.lang.Double.isNaN(r13)
            double r13 = r13 - r4
            java.lang.Double.isNaN(r11)
            double r11 = r11 + r13
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r2.getIntrinsicWidth()
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            int r2 = (int) r11
            r1.nameMuteLeft = r2
            goto L_0x188e
        L_0x185d:
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x188e
            int r2 = r1.nameLeft
            double r11 = (double) r2
            double r13 = (double) r10
            java.lang.Double.isNaN(r13)
            double r13 = r13 - r4
            java.lang.Double.isNaN(r11)
            double r11 = r11 + r13
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            int r2 = r1.drawScam
            r8 = 1
            if (r2 != r8) goto L_0x1880
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1882
        L_0x1880:
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1882:
            int r2 = r2.getIntrinsicWidth()
            double r13 = (double) r2
            java.lang.Double.isNaN(r13)
            double r11 = r11 - r13
            int r2 = (int) r11
            r1.nameMuteLeft = r2
        L_0x188e:
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x18a6
            double r10 = (double) r10
            int r0 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r0 >= 0) goto L_0x18a6
            int r0 = r1.nameLeft
            double r12 = (double) r0
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r4
            java.lang.Double.isNaN(r12)
            double r12 = r12 + r10
            int r0 = (int) r12
            r1.nameLeft = r0
        L_0x18a6:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18e7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18e7
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x18b7:
            if (r2 >= r0) goto L_0x18dd
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r2)
            r8 = 0
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x18dc
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineWidth(r2)
            double r10 = (double) r5
            double r10 = java.lang.Math.ceil(r10)
            double r12 = (double) r7
            java.lang.Double.isNaN(r12)
            double r12 = r12 - r10
            int r5 = (int) r12
            int r4 = java.lang.Math.min(r4, r5)
            int r2 = r2 + 1
            goto L_0x18b7
        L_0x18dc:
            r4 = 0
        L_0x18dd:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x18e7
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x18e7:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x19a7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19a7
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x19a7
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r7 = (double) r7
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x19a7
            int r0 = r1.messageNameLeft
            double r10 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r4
            java.lang.Double.isNaN(r10)
            double r10 = r10 + r7
            int r0 = (int) r10
            r1.messageNameLeft = r0
            goto L_0x19a7
        L_0x191d:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x196c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x196c
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r10
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1951
            android.text.StaticLayout r3 = r1.nameLayout
            float r4 = r3.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            double r7 = (double) r10
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x1951
            int r2 = r1.nameLeft
            double r10 = (double) r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r4
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r7
            int r2 = (int) r10
            r1.nameLeft = r2
        L_0x1951:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x195d
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x195d
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x196c
        L_0x195d:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x196c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x198f
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x198f
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r4 = 0
        L_0x1979:
            if (r4 >= r0) goto L_0x1988
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r4)
            float r2 = java.lang.Math.min(r2, r5)
            int r4 = r4 + 1
            goto L_0x1979
        L_0x1988:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x198f:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x19a7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19a7
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x19a7:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x19e9
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x19e9
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x19e5 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x19e5 }
            r2 = 1
            if (r6 < r0) goto L_0x19bc
            int r6 = r0 + -1
        L_0x19bc:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x19e5 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x19e5 }
            android.text.StaticLayout r4 = r1.messageLayout     // Catch:{ Exception -> 0x19e5 }
            int r6 = r6 + r2
            float r2 = r4.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x19e5 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x19e5 }
            double r4 = (double) r0     // Catch:{ Exception -> 0x19e5 }
            double r4 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x19e5 }
            int r0 = (int) r4     // Catch:{ Exception -> 0x19e5 }
            if (r0 == 0) goto L_0x19dc
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x19e5 }
            int r0 = r0 + r2
        L_0x19dc:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x19e5 }
            int r4 = r1.messageLeft     // Catch:{ Exception -> 0x19e5 }
            int r4 = r4 + r0
            r2.setImageX(r4)     // Catch:{ Exception -> 0x19e5 }
            goto L_0x19e9
        L_0x19e5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x19e9:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a37
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1a37
            if (r38 < 0) goto L_0x1a0e
            int r7 = r38 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r7 >= r0) goto L_0x1a0e
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r38
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r7)
            goto L_0x1a1c
        L_0x1a0e:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1a1c:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1a28
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1a37
        L_0x1a28:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1a37:
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

    /* JADX WARNING: Removed duplicated region for block: B:127:0x01ea  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0245  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x018f  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0194  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            org.telegram.ui.Cells.DialogCell$CustomDialog r1 = r0.customDialog
            r2 = 0
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x003c
            int r5 = r1.date
            r0.lastMessageDate = r5
            int r5 = r1.unread_count
            if (r5 == 0) goto L_0x0012
            goto L_0x0013
        L_0x0012:
            r3 = 0
        L_0x0013:
            r0.lastUnreadState = r3
            r0.unreadCount = r5
            boolean r3 = r1.pinned
            r0.drawPin = r3
            boolean r3 = r1.muted
            r0.dialogMuted = r3
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            int r4 = r1.id
            long r4 = (long) r4
            java.lang.String r1 = r1.name
            r3.setInfo(r4, r1, r2)
            org.telegram.messenger.ImageReceiver r6 = r0.avatarImage
            r7 = 0
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r10 = 0
            r11 = 0
            java.lang.String r8 = "50_50"
            r6.setImage(r7, r8, r9, r10, r11)
            org.telegram.messenger.ImageReceiver r1 = r0.thumbImage
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
            goto L_0x04dd
        L_0x003c:
            int r1 = r0.unreadCount
            boolean r5 = r0.markUnread
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x0104
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            if (r6 == 0) goto L_0x00f9
            if (r19 != 0) goto L_0x0106
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r6.id
            boolean r7 = r7.isClearingDialog(r8)
            r0.clearingDialog = r7
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r7 = r7.dialogMessage
            long r8 = r6.id
            java.lang.Object r7 = r7.get(r8)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            r0.message = r7
            if (r7 == 0) goto L_0x0082
            boolean r7 = r7.isUnread()
            if (r7 == 0) goto L_0x0082
            r7 = 1
            goto L_0x0083
        L_0x0082:
            r7 = 0
        L_0x0083:
            r0.lastUnreadState = r7
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r7 == 0) goto L_0x0098
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)
            int r7 = r7.getArchiveUnreadCount()
            r0.unreadCount = r7
            r0.mentionCount = r4
            goto L_0x00a0
        L_0x0098:
            int r7 = r6.unread_count
            r0.unreadCount = r7
            int r7 = r6.unread_mentions_count
            r0.mentionCount = r7
        L_0x00a0:
            boolean r7 = r6.unread_mark
            r0.markUnread = r7
            org.telegram.messenger.MessageObject r7 = r0.message
            if (r7 == 0) goto L_0x00ad
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            int r7 = r7.edit_date
            goto L_0x00ae
        L_0x00ad:
            r7 = 0
        L_0x00ae:
            r0.currentEditDate = r7
            int r7 = r6.last_message_date
            r0.lastMessageDate = r7
            int r7 = r0.dialogsType
            r8 = 7
            r9 = 8
            if (r7 == r8) goto L_0x00cc
            if (r7 != r9) goto L_0x00be
            goto L_0x00cc
        L_0x00be:
            int r7 = r0.currentDialogFolderId
            if (r7 != 0) goto L_0x00c8
            boolean r6 = r6.pinned
            if (r6 == 0) goto L_0x00c8
            r6 = 1
            goto L_0x00c9
        L_0x00c8:
            r6 = 0
        L_0x00c9:
            r0.drawPin = r6
            goto L_0x00ee
        L_0x00cc:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.messenger.MessagesController$DialogFilter[] r7 = r7.selectedDialogFilter
            int r8 = r0.dialogsType
            if (r8 != r9) goto L_0x00da
            r8 = 1
            goto L_0x00db
        L_0x00da:
            r8 = 0
        L_0x00db:
            r7 = r7[r8]
            if (r7 == 0) goto L_0x00eb
            org.telegram.messenger.support.LongSparseIntArray r7 = r7.pinnedDialogs
            long r8 = r6.id
            int r6 = r7.indexOfKey(r8)
            if (r6 < 0) goto L_0x00eb
            r6 = 1
            goto L_0x00ec
        L_0x00eb:
            r6 = 0
        L_0x00ec:
            r0.drawPin = r6
        L_0x00ee:
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0106
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.send_state
            r0.lastSendState = r6
            goto L_0x0106
        L_0x00f9:
            r0.unreadCount = r4
            r0.mentionCount = r4
            r0.currentEditDate = r4
            r0.lastMessageDate = r4
            r0.clearingDialog = r4
            goto L_0x0106
        L_0x0104:
            r0.drawPin = r4
        L_0x0106:
            if (r19 == 0) goto L_0x0249
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 == 0) goto L_0x0129
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r6 = r19 & r6
            if (r6 == 0) goto L_0x0129
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            long r7 = r7.id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            r0.user = r6
            r18.invalidate()
        L_0x0129:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x0153
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_USER_PRINT
            r6 = r19 & r6
            if (r6 == 0) goto L_0x0153
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            long r7 = r0.currentDialogId
            java.lang.CharSequence r6 = r6.getPrintingString(r7, r4, r3)
            java.lang.CharSequence r7 = r0.lastPrintString
            if (r7 == 0) goto L_0x0145
            if (r6 == 0) goto L_0x0151
        L_0x0145:
            if (r7 != 0) goto L_0x0149
            if (r6 != 0) goto L_0x0151
        L_0x0149:
            if (r7 == 0) goto L_0x0153
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0153
        L_0x0151:
            r6 = 1
            goto L_0x0154
        L_0x0153:
            r6 = 0
        L_0x0154:
            if (r6 != 0) goto L_0x0167
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_MESSAGE_TEXT
            r7 = r19 & r7
            if (r7 == 0) goto L_0x0167
            org.telegram.messenger.MessageObject r7 = r0.message
            if (r7 == 0) goto L_0x0167
            java.lang.CharSequence r7 = r7.messageText
            java.lang.CharSequence r8 = r0.lastMessageString
            if (r7 == r8) goto L_0x0167
            r6 = 1
        L_0x0167:
            if (r6 != 0) goto L_0x0195
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT
            r7 = r19 & r7
            if (r7 == 0) goto L_0x0195
            org.telegram.tgnet.TLRPC$Chat r7 = r0.chat
            if (r7 == 0) goto L_0x0195
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            long r8 = r8.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r8)
            boolean r8 = r7.call_active
            if (r8 == 0) goto L_0x018f
            boolean r7 = r7.call_not_empty
            if (r7 == 0) goto L_0x018f
            r7 = 1
            goto L_0x0190
        L_0x018f:
            r7 = 0
        L_0x0190:
            boolean r8 = r0.hasCall
            if (r7 == r8) goto L_0x0195
            r6 = 1
        L_0x0195:
            if (r6 != 0) goto L_0x01a2
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r7 = r19 & r7
            if (r7 == 0) goto L_0x01a2
            org.telegram.tgnet.TLRPC$Chat r7 = r0.chat
            if (r7 != 0) goto L_0x01a2
            r6 = 1
        L_0x01a2:
            if (r6 != 0) goto L_0x01af
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r7 = r19 & r7
            if (r7 == 0) goto L_0x01af
            org.telegram.tgnet.TLRPC$Chat r7 = r0.chat
            if (r7 != 0) goto L_0x01af
            r6 = 1
        L_0x01af:
            if (r6 != 0) goto L_0x01bc
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r7 = r19 & r7
            if (r7 == 0) goto L_0x01bc
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            if (r7 != 0) goto L_0x01bc
            r6 = 1
        L_0x01bc:
            if (r6 != 0) goto L_0x01c9
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r7 = r19 & r7
            if (r7 == 0) goto L_0x01c9
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            if (r7 != 0) goto L_0x01c9
            r6 = 1
        L_0x01c9:
            if (r6 != 0) goto L_0x022c
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE
            r7 = r19 & r7
            if (r7 == 0) goto L_0x022c
            org.telegram.messenger.MessageObject r7 = r0.message
            if (r7 == 0) goto L_0x01e6
            boolean r8 = r0.lastUnreadState
            boolean r7 = r7.isUnread()
            if (r8 == r7) goto L_0x01e6
            org.telegram.messenger.MessageObject r6 = r0.message
            boolean r6 = r6.isUnread()
            r0.lastUnreadState = r6
            r6 = 1
        L_0x01e6:
            boolean r7 = r0.isDialogCell
            if (r7 == 0) goto L_0x022c
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r7 = r7.dialogs_dict
            long r8 = r0.currentDialogId
            java.lang.Object r7 = r7.get(r8)
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC$Dialog) r7
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r8 == 0) goto L_0x020a
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r8)
            int r8 = r8.getArchiveUnreadCount()
        L_0x0208:
            r9 = 0
            goto L_0x0213
        L_0x020a:
            if (r7 == 0) goto L_0x0211
            int r8 = r7.unread_count
            int r9 = r7.unread_mentions_count
            goto L_0x0213
        L_0x0211:
            r8 = 0
            goto L_0x0208
        L_0x0213:
            if (r7 == 0) goto L_0x022c
            int r10 = r0.unreadCount
            if (r10 != r8) goto L_0x0223
            boolean r10 = r0.markUnread
            boolean r11 = r7.unread_mark
            if (r10 != r11) goto L_0x0223
            int r10 = r0.mentionCount
            if (r10 == r9) goto L_0x022c
        L_0x0223:
            r0.unreadCount = r8
            r0.mentionCount = r9
            boolean r6 = r7.unread_mark
            r0.markUnread = r6
            r6 = 1
        L_0x022c:
            if (r6 != 0) goto L_0x0243
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_SEND_STATE
            r7 = r19 & r7
            if (r7 == 0) goto L_0x0243
            org.telegram.messenger.MessageObject r7 = r0.message
            if (r7 == 0) goto L_0x0243
            int r8 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            int r7 = r7.send_state
            if (r8 == r7) goto L_0x0243
            r0.lastSendState = r7
            r6 = 1
        L_0x0243:
            if (r6 != 0) goto L_0x0249
            r18.invalidate()
            return
        L_0x0249:
            r0.user = r2
            r0.chat = r2
            r0.encryptedChat = r2
            int r2 = r0.currentDialogFolderId
            r6 = 0
            if (r2 == 0) goto L_0x0266
            r0.dialogMuted = r4
            org.telegram.messenger.MessageObject r2 = r18.findFolderTopMessage()
            r0.message = r2
            if (r2 == 0) goto L_0x0264
            long r8 = r2.getDialogId()
            goto L_0x027f
        L_0x0264:
            r8 = r6
            goto L_0x027f
        L_0x0266:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x027a
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r0.currentDialogId
            boolean r2 = r2.isDialogMuted(r8)
            if (r2 == 0) goto L_0x027a
            r2 = 1
            goto L_0x027b
        L_0x027a:
            r2 = 0
        L_0x027b:
            r0.dialogMuted = r2
            long r8 = r0.currentDialogId
        L_0x027f:
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x0326
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)
            if (r2 == 0) goto L_0x02b4
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r6)
            r0.encryptedChat = r2
            if (r2 == 0) goto L_0x02fe
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r0.encryptedChat
            long r6 = r6.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            goto L_0x02fe
        L_0x02b4:
            boolean r2 = org.telegram.messenger.DialogObject.isUserDialog(r8)
            if (r2 == 0) goto L_0x02cb
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            goto L_0x02fe
        L_0x02cb:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r6 = -r8
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r0.chat = r2
            boolean r6 = r0.isDialogCell
            if (r6 != 0) goto L_0x02fe
            if (r2 == 0) goto L_0x02fe
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            if (r2 == 0) goto L_0x02fe
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r6 = r6.migrated_to
            long r6 = r6.channel_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            if (r2 == 0) goto L_0x02fe
            r0.chat = r2
        L_0x02fe:
            boolean r2 = r0.useMeForMyMessages
            if (r2 == 0) goto L_0x0326
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x0326
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isOutOwner()
            if (r2 == 0) goto L_0x0326
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r6 = r0.currentAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            long r6 = r6.clientUserId
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
        L_0x0326:
            int r2 = r0.currentDialogFolderId
            r6 = 2
            if (r2 == 0) goto L_0x0343
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r2.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setAvatarType(r6)
            org.telegram.messenger.ImageReceiver r7 = r0.avatarImage
            r8 = 0
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$User r12 = r0.user
            r13 = 0
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x03a4
        L_0x0343:
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x0392
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            boolean r2 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r2)
            if (r2 == 0) goto L_0x0369
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r7 = 12
            r2.setAvatarType(r7)
            org.telegram.messenger.ImageReceiver r8 = r0.avatarImage
            r9 = 0
            r10 = 0
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            org.telegram.tgnet.TLRPC$User r13 = r0.user
            r14 = 0
            r8.setImage(r9, r10, r11, r12, r13, r14)
            goto L_0x03a4
        L_0x0369:
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r2 == 0) goto L_0x0388
            boolean r2 = r0.useMeForMyMessages
            if (r2 != 0) goto L_0x0388
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setAvatarType(r3)
            org.telegram.messenger.ImageReceiver r7 = r0.avatarImage
            r8 = 0
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$User r12 = r0.user
            r13 = 0
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x03a4
        L_0x0388:
            org.telegram.messenger.ImageReceiver r2 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r2.setForUserOrChat(r7, r8)
            goto L_0x03a4
        L_0x0392:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            if (r2 == 0) goto L_0x03a4
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.messenger.ImageReceiver r2 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r7 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r2.setForUserOrChat(r7, r8)
        L_0x03a4:
            if (r20 == 0) goto L_0x04dd
            int r2 = r0.unreadCount
            if (r1 != r2) goto L_0x03ae
            boolean r2 = r0.markUnread
            if (r5 == r2) goto L_0x04dd
        L_0x03ae:
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r0.lastDialogChangedTime
            long r7 = r7 - r9
            r9 = 100
            int r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x04dd
            android.animation.ValueAnimator r2 = r0.countAnimator
            if (r2 == 0) goto L_0x03c2
            r2.cancel()
        L_0x03c2:
            float[] r2 = new float[r6]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.countAnimator = r2
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda1
            r6.<init>(r0)
            r2.addUpdateListener(r6)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Cells.DialogCell$1 r6 = new org.telegram.ui.Cells.DialogCell$1
            r6.<init>()
            r2.addListener(r6)
            if (r1 == 0) goto L_0x03e5
            boolean r2 = r0.markUnread
            if (r2 == 0) goto L_0x03ec
        L_0x03e5:
            boolean r2 = r0.markUnread
            if (r2 != 0) goto L_0x040e
            if (r5 != 0) goto L_0x03ec
            goto L_0x040e
        L_0x03ec:
            int r2 = r0.unreadCount
            if (r2 != 0) goto L_0x03ff
            android.animation.ValueAnimator r2 = r0.countAnimator
            r5 = 150(0x96, double:7.4E-322)
            r2.setDuration(r5)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r5)
            goto L_0x041f
        L_0x03ff:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r5 = 430(0x1ae, double:2.124E-321)
            r2.setDuration(r5)
            android.animation.ValueAnimator r2 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r2.setInterpolator(r5)
            goto L_0x041f
        L_0x040e:
            android.animation.ValueAnimator r2 = r0.countAnimator
            r5 = 220(0xdc, double:1.087E-321)
            r2.setDuration(r5)
            android.animation.ValueAnimator r2 = r0.countAnimator
            android.view.animation.OvershootInterpolator r5 = new android.view.animation.OvershootInterpolator
            r5.<init>()
            r2.setInterpolator(r5)
        L_0x041f:
            boolean r2 = r0.drawCount
            if (r2 == 0) goto L_0x04c8
            android.text.StaticLayout r2 = r0.countLayout
            if (r2 == 0) goto L_0x04c8
            java.lang.String r2 = java.lang.String.valueOf(r1)
            int r5 = r0.unreadCount
            java.lang.String r5 = java.lang.String.valueOf(r5)
            int r6 = r2.length()
            int r7 = r5.length()
            if (r6 != r7) goto L_0x04c4
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r2)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r5)
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r5)
            r8 = 0
        L_0x044b:
            int r10 = r2.length()
            if (r8 >= r10) goto L_0x047b
            char r10 = r2.charAt(r8)
            char r11 = r5.charAt(r8)
            if (r10 != r11) goto L_0x046e
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r9.setSpan(r10, r8, r11, r4)
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            r6.setSpan(r10, r8, r11, r4)
            goto L_0x0478
        L_0x046e:
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r7.setSpan(r10, r8, r11, r4)
        L_0x0478:
            int r8 = r8 + 1
            goto L_0x044b
        L_0x047b:
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r8.measureText(r2)
            double r10 = (double) r2
            double r10 = java.lang.Math.ceil(r10)
            int r2 = (int) r10
            int r2 = java.lang.Math.max(r5, r2)
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_CENTER
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 0
            r15 = 0
            r8 = r5
            r11 = r2
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r0.countOldLayout = r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_CENTER
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r5
            r11 = r7
            r13 = r2
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            r0.countAnimationStableLayout = r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_CENTER
            r10 = r5
            r11 = r6
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            r0.countAnimationInLayout = r5
            goto L_0x04c8
        L_0x04c4:
            android.text.StaticLayout r2 = r0.countLayout
            r0.countOldLayout = r2
        L_0x04c8:
            int r2 = r0.countWidth
            r0.countWidthOld = r2
            int r2 = r0.countLeft
            r0.countLeftOld = r2
            int r2 = r0.unreadCount
            if (r2 <= r1) goto L_0x04d5
            goto L_0x04d6
        L_0x04d5:
            r3 = 0
        L_0x04d6:
            r0.countAnimationIncrement = r3
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x04dd:
            int r1 = r18.getMeasuredWidth()
            if (r1 != 0) goto L_0x04ee
            int r1 = r18.getMeasuredHeight()
            if (r1 == 0) goto L_0x04ea
            goto L_0x04ee
        L_0x04ea:
            r18.requestLayout()
            goto L_0x04f1
        L_0x04ee:
            r18.buildLayout()
        L_0x04f1:
            if (r20 != 0) goto L_0x04fd
            boolean r1 = r0.dialogMuted
            if (r1 == 0) goto L_0x04fa
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x04fb
        L_0x04fa:
            r1 = 0
        L_0x04fb:
            r0.dialogMutedProgress = r1
        L_0x04fd:
            r18.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$0(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
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
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0622  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0631  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0671  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x06ec  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0761  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0820  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x08df  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0933  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0936  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0943  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x0952  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x098b  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0992  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0a2d  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a7b  */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x0d22  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0dda  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0e0f  */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0e47  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0e64  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x0f2d  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x11c7  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x11db  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x11f0  */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x120a  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x1236  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x129b  */
    /* JADX WARNING: Removed duplicated region for block: B:701:0x12f4  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x130b  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x1327  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x1351  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x1381  */
    /* JADX WARNING: Removed duplicated region for block: B:741:0x1397  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x13c1  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x13e3  */
    /* JADX WARNING: Removed duplicated region for block: B:763:? A[RETURN, SYNTHETIC] */
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
            r2 = 2131628060(0x7f0e101c, float:1.8883402E38)
            java.lang.String r7 = "UnhideFromTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628060(0x7f0e101c, float:1.8883402E38)
            goto L_0x0119
        L_0x00dc:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131625826(0x7f0e0762, float:1.887887E38)
            java.lang.String r7 = "HideOnTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131625826(0x7f0e0762, float:1.887887E38)
            goto L_0x0119
        L_0x00f9:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131627258(0x7f0e0cfa, float:1.8881775E38)
            java.lang.String r7 = "PsaHide"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627258(0x7f0e0cfa, float:1.8881775E38)
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
            r2 = 2131627878(0x7f0e0var_, float:1.8883033E38)
            java.lang.String r7 = "SwipeUnmute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131627878(0x7f0e0var_, float:1.8883033E38)
            goto L_0x0119
        L_0x014e:
            r2 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r7 = "SwipeMute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            goto L_0x0119
        L_0x015f:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r14) goto L_0x0180
            r2 = 2131627863(0x7f0e0var_, float:1.8883002E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r2 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131627863(0x7f0e0var_, float:1.8883002E38)
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
            r2 = 2131627865(0x7f0e0var_, float:1.8883006E38)
            java.lang.String r7 = "SwipeMarkAsUnread"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131627865(0x7f0e0var_, float:1.8883006E38)
            goto L_0x0119
        L_0x01a3:
            r2 = 2131627864(0x7f0e0var_, float:1.8883004E38)
            java.lang.String r7 = "SwipeMarkAsRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131627864(0x7f0e0var_, float:1.8883004E38)
            goto L_0x0119
        L_0x01b5:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01e5
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01d3
            r2 = 2131627879(0x7f0e0var_, float:1.8883035E38)
            java.lang.String r7 = "SwipeUnpin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131627879(0x7f0e0var_, float:1.8883035E38)
            goto L_0x0119
        L_0x01d3:
            r2 = 2131627867(0x7f0e0f5b, float:1.888301E38)
            java.lang.String r7 = "SwipePin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131627867(0x7f0e0f5b, float:1.888301E38)
            goto L_0x0119
        L_0x01e5:
            r2 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r7 = "Archive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624302(0x7f0e016e, float:1.887578E38)
            goto L_0x0119
        L_0x01f7:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628051(0x7f0e1013, float:1.8883384E38)
            java.lang.String r7 = "Unarchive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628051(0x7f0e1013, float:1.8883384E38)
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
            if (r0 == 0) goto L_0x081c
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
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07bc }
            r0.draw(r9)     // Catch:{ Exception -> 0x07bc }
            goto L_0x07c0
        L_0x07bc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07c0:
            r31.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x081c
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x081c
            r31.save()
            int r1 = r8.printingStringType
            if (r1 == r15) goto L_0x07f1
            r2 = 4
            if (r1 != r2) goto L_0x07d8
            goto L_0x07f1
        L_0x07d8:
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
            goto L_0x0803
        L_0x07f1:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r15) goto L_0x07fd
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x07fe
        L_0x07fd:
            r1 = 0
        L_0x07fe:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0803:
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
        L_0x081c:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x08df
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0828
            r1 = 2
            goto L_0x0829
        L_0x0828:
            r1 = 0
        L_0x0829:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0830
            r1 = 4
            goto L_0x0831
        L_0x0830:
            r1 = 0
        L_0x0831:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x083f
            if (r1 == r0) goto L_0x083f
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x083f
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x083f:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x0845
            int r0 = r8.animateToStatusDrawableParams
        L_0x0845:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x084c
            r18 = 1
            goto L_0x084e
        L_0x084c:
            r18 = 0
        L_0x084e:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x0856
            r2 = 4
            r23 = 1
            goto L_0x0859
        L_0x0856:
            r2 = 4
            r23 = 0
        L_0x0859:
            r0 = r0 & r2
            if (r0 == 0) goto L_0x085e
            r0 = 1
            goto L_0x085f
        L_0x085e:
            r0 = 0
        L_0x085f:
            if (r1 == 0) goto L_0x08b9
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x0869
            r3 = 1
            goto L_0x086a
        L_0x0869:
            r3 = 0
        L_0x086a:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x0871
            r2 = 4
            r4 = 1
            goto L_0x0873
        L_0x0871:
            r2 = 4
            r4 = 0
        L_0x0873:
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0878
            r5 = 1
            goto L_0x0879
        L_0x0878:
            r5 = 0
        L_0x0879:
            if (r18 != 0) goto L_0x08a0
            if (r3 != 0) goto L_0x08a0
            if (r5 == 0) goto L_0x08a0
            if (r4 != 0) goto L_0x08a0
            if (r23 == 0) goto L_0x08a0
            if (r0 == 0) goto L_0x08a0
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
            goto L_0x08ca
        L_0x08a0:
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
            goto L_0x08ca
        L_0x08b9:
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r30
            r2 = r31
            r3 = r18
            r4 = r23
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x08ca:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x08d2
            r7 = 2
            goto L_0x08d3
        L_0x08d2:
            r7 = 0
        L_0x08d3:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x08da
            r1 = 4
            goto L_0x08db
        L_0x08da:
            r1 = 0
        L_0x08db:
            int r0 = r0 + r1
            r8.lastStatusDrawableParams = r0
            goto L_0x08e1
        L_0x08df:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x08e1:
            boolean r0 = r8.dialogMuted
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x08ed
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0992
        L_0x08ed:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x0992
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x0992
            if (r0 == 0) goto L_0x090e
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x090e
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x090a
            r8.dialogMutedProgress = r14
            goto L_0x0926
        L_0x090a:
            r30.invalidate()
            goto L_0x0926
        L_0x090e:
            if (r0 != 0) goto L_0x0926
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0926
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x0923
            r8.dialogMutedProgress = r11
            goto L_0x0926
        L_0x0923:
            r30.invalidate()
        L_0x0926:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0936
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0933
            goto L_0x0936
        L_0x0933:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0937
        L_0x0936:
            r5 = 0
        L_0x0937:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0943
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x0945
        L_0x0943:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0945:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x098b
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
            goto L_0x0a01
        L_0x098b:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a01
        L_0x0992:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x09d3
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09a6
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09a3
            goto L_0x09a6
        L_0x09a3:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09a8
        L_0x09a6:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09a8:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09bf
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09bc
            goto L_0x09bf
        L_0x09bc:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x09c1
        L_0x09bf:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x09c1:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a01
        L_0x09d3:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a01
            if (r0 != r15) goto L_0x09dc
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x09de
        L_0x09dc:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x09de:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09ec
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09e9
            goto L_0x09ec
        L_0x09e9:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x09ee
        L_0x09ec:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x09ee:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            if (r0 != r15) goto L_0x09fc
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x09fe
        L_0x09fc:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x09fe:
            r0.draw(r9)
        L_0x0a01:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a0b
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0a23
        L_0x0a0b:
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
        L_0x0a23:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x0a7b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r5 = r8.reorderIconProgress
            float r5 = r14 - r5
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r5 = (float) r1
            int r6 = r8.errorTop
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r7
            float r1 = (float) r1
            int r7 = r8.errorTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r7 = r7 + r2
            float r2 = (float) r7
            r0.set(r5, r6, r1, r2)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r1 * r3
            float r1 = r1 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r9.drawRoundRect(r0, r2, r1, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r9)
            goto L_0x0dd4
        L_0x0a7b:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0aaa
            boolean r5 = r8.drawMention
            if (r5 != 0) goto L_0x0aaa
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0a8a
            goto L_0x0aaa
        L_0x0a8a:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0dd4
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
            goto L_0x0dd4
        L_0x0aaa:
            if (r0 != 0) goto L_0x0ab7
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0ab3
            goto L_0x0ab7
        L_0x0ab3:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0d1e
        L_0x0ab7:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0ac4
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x0ac4
            float r5 = r8.countChangeProgress
            float r5 = r14 - r5
            goto L_0x0ac6
        L_0x0ac4:
            float r5 = r8.countChangeProgress
        L_0x0ac6:
            android.text.StaticLayout r6 = r8.countOldLayout
            if (r6 == 0) goto L_0x0CLASSNAME
            if (r0 != 0) goto L_0x0ace
            goto L_0x0CLASSNAME
        L_0x0ace:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0ada
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0ad7
            goto L_0x0ada
        L_0x0ad7:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0adc
        L_0x0ada:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0adc:
            float r6 = r8.reorderIconProgress
            float r6 = r14 - r6
            float r6 = r6 * r1
            int r6 = (int) r6
            r0.setAlpha(r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r14 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r6.setAlpha(r7)
            float r6 = r5 * r16
            int r7 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r7 <= 0) goto L_0x0afb
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0afc
        L_0x0afb:
            r7 = r6
        L_0x0afc:
            int r15 = r8.countLeft
            float r15 = (float) r15
            float r15 = r15 * r7
            int r4 = r8.countLeftOld
            float r4 = (float) r4
            float r23 = r14 - r7
            float r4 = r4 * r23
            float r15 = r15 + r4
            r4 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r15 - r4
            android.graphics.RectF r11 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r3 = r8.countWidth
            float r3 = (float) r3
            float r3 = r3 * r7
            float r3 = r3 + r4
            int r14 = r8.countWidthOld
            float r14 = (float) r14
            float r14 = r14 * r23
            float r3 = r3 + r14
            r14 = 1093664768(0x41300000, float:11.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r3 = r3 + r14
            int r14 = r8.countTop
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r14 = r14 + r28
            float r14 = (float) r14
            r11.set(r4, r1, r3, r14)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0b4c
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r6)
            float r3 = r3 * r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 + r1
            goto L_0x0b62
        L_0x0b4c:
            r1 = 1065353216(0x3var_, float:1.0)
            r3 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r6 = 1056964608(0x3var_, float:0.5)
            float r5 = r5 - r6
            float r5 = r5 * r16
            float r5 = r1 - r5
            float r4 = r4.getInterpolation(r5)
            float r4 = r4 * r3
            float r3 = r4 + r1
        L_0x0b62:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r3, r3, r1, r4)
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r5, r3, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            if (r0 == 0) goto L_0x0b9b
            r31.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r15, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r31.restore()
        L_0x0b9b:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r4 = r3 * r7
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0bd8
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0bba
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0bbf
        L_0x0bba:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0bbf:
            float r1 = (float) r1
            float r1 = r1 * r23
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r15, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r9)
            r31.restore()
            goto L_0x0CLASSNAME
        L_0x0bd8:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0be8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0bed
        L_0x0be8:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0bed:
            float r1 = (float) r1
            float r1 = r1 * r23
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r15, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r9)
            r31.restore()
        L_0x0CLASSNAME:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0c3a
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r23
            int r3 = (int) r3
            r1.setAlpha(r3)
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0c1e
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x0CLASSNAME
        L_0x0c1e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0CLASSNAME:
            float r1 = (float) r1
            float r1 = r1 * r7
            int r3 = r8.countTop
            float r3 = (float) r3
            float r1 = r1 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            float r1 = r1 + r3
            r9.translate(r15, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r9)
            r31.restore()
        L_0x0c3a:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r31.restore()
            goto L_0x0ab3
        L_0x0CLASSNAME:
            if (r0 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.text.StaticLayout r6 = r8.countLayout
        L_0x0CLASSNAME:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0CLASSNAME
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0CLASSNAME:
            float r1 = r8.reorderIconProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r1 = r3 - r1
            r4 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r4
            int r1 = (int) r1
            r0.setAlpha(r1)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r3 - r7
            float r7 = r7 * r4
            int r3 = (int) r7
            r1.setAlpha(r3)
            int r1 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r3
            android.graphics.RectF r3 = r8.rect
            float r4 = (float) r1
            int r7 = r8.countTop
            float r7 = (float) r7
            int r11 = r8.countWidth
            int r1 = r1 + r11
            r11 = 1093664768(0x41300000, float:11.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = r1 + r11
            float r1 = (float) r1
            int r11 = r8.countTop
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r11 = r11 + r14
            float r11 = (float) r11
            r3.set(r4, r7, r1, r11)
            r1 = 1065353216(0x3var_, float:1.0)
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0cef
            boolean r3 = r8.drawPin
            if (r3 == 0) goto L_0x0cdd
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r4 = r8.reorderIconProgress
            float r4 = r1 - r4
            r7 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r7
            int r4 = (int) r4
            r3.setAlpha(r4)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r4 = r8.pinLeft
            int r7 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r3, (int) r4, (int) r7)
            r31.save()
            float r3 = r1 - r5
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r1 = r1.getBounds()
            int r1 = r1.centerX()
            float r1 = (float) r1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r4 = r4.getBounds()
            int r4 = r4.centerY()
            float r4 = (float) r4
            r9.scale(r3, r3, r1, r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r9)
            r31.restore()
        L_0x0cdd:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r5, r5, r1, r3)
        L_0x0cef:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r7 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r7, r3, r0)
            if (r6 == 0) goto L_0x0d15
            r31.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r1 + r3
            float r1 = (float) r1
            r9.translate(r0, r1)
            r6.draw(r9)
            r31.restore()
        L_0x0d15:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0d1e
            r31.restore()
        L_0x0d1e:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0dd4
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r3 = r8.reorderIconProgress
            float r5 = r1 - r3
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r3 = (float) r0
            int r4 = r8.countTop
            float r4 = (float) r4
            int r5 = r8.mentionWidth
            int r0 = r0 + r5
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            float r0 = (float) r0
            int r5 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r5 = r5 + r2
            float r2 = (float) r5
            r1.set(r3, r4, r0, r2)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0d60
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0d60
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0d62
        L_0x0d60:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0d62:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0d9d
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
            goto L_0x0dd4
        L_0x0d9d:
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
            r3 = 1078774989(0x404ccccd, float:3.2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r1, r2, r3, r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r9)
        L_0x0dd4:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0df8
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
        L_0x0df8:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0e06
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0e06
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0e0b
        L_0x0e06:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0e0b:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0e43
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0e43
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
        L_0x0e43:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0e4a
            r31.restore()
        L_0x0e4a:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0f2a
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0f2a
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0f2d
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0f2d
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x0f2d
            boolean r0 = r30.isOnline()
            if (r0 != 0) goto L_0x0e71
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0var_
        L_0x0e71:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e83
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e80
            goto L_0x0e83
        L_0x0e80:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0e85
        L_0x0e83:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e85:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0ea8
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0ea1
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0e9f
            goto L_0x0ea1
        L_0x0e9f:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ea1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0ebf
        L_0x0ea8:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0eb9
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0eb7
            goto L_0x0eb9
        L_0x0eb7:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0eb9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0ebf:
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
            if (r0 == 0) goto L_0x0var_
            float r0 = r8.onlineProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0var_
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 + r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0var_
            r8.onlineProgress = r1
            goto L_0x0var_
        L_0x0var_:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            float r2 = (float) r12
            float r2 = r2 / r19
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0var_
            r8.onlineProgress = r1
        L_0x0var_:
            r6 = 1
            goto L_0x0var_
        L_0x0var_:
            r6 = r20
        L_0x0var_:
            r20 = r6
        L_0x0f2a:
            r2 = 0
            goto L_0x1204
        L_0x0f2d:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0f2a
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0f3b
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0f3b
            r6 = 1
            goto L_0x0f3c
        L_0x0f3b:
            r6 = 0
        L_0x0f3c:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x0var_
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0f2a
        L_0x0var_:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0f5c
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0f5c
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r0
            goto L_0x0f5e
        L_0x0f5c:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0f5e:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0var_
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0f6d
            goto L_0x0var_
        L_0x0f6d:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0var_
        L_0x0var_:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0f8e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0f8c
            goto L_0x0f8e
        L_0x0f8c:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f8e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0fac
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0fa6
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0fa4
            goto L_0x0fa6
        L_0x0fa4:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fa6:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0fac:
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
            r4 = 1077936128(0x40400000, float:3.0)
            if (r3 != 0) goto L_0x1021
            r6 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r11 = (float) r11
            float r14 = r8.innerProgress
        L_0x1019:
            float r11 = r11 * r14
            float r6 = r6 - r11
            r11 = r6
        L_0x101d:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x111a
        L_0x1021:
            r6 = 1
            if (r3 != r6) goto L_0x1048
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r11 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r14 = (float) r14
            float r15 = r8.innerProgress
        L_0x1043:
            float r14 = r14 * r15
            float r11 = r11 + r14
            goto L_0x111a
        L_0x1048:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 2
            if (r3 != r11) goto L_0x106b
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            goto L_0x1019
        L_0x106b:
            r6 = 3
            if (r3 != r6) goto L_0x108c
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r11 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r14 = (float) r14
            float r15 = r8.innerProgress
            goto L_0x1043
        L_0x108c:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 4
            if (r3 != r11) goto L_0x10ae
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r11 = (float) r11
            float r14 = r8.innerProgress
            goto L_0x1019
        L_0x10ae:
            r6 = 5
            if (r3 != r6) goto L_0x10d2
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r11 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r14 = (float) r14
            float r15 = r8.innerProgress
            goto L_0x1043
        L_0x10d2:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 6
            if (r3 != r11) goto L_0x10f9
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r11 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r14 = (float) r14
            float r15 = r8.innerProgress
            float r14 = r14 * r15
            float r11 = r11 - r14
            goto L_0x101d
        L_0x10f9:
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r11 = (float) r11
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r14 = (float) r14
            float r15 = r8.innerProgress
            goto L_0x1043
        L_0x111a:
            float r14 = r8.chatCallProgress
            int r14 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r14 < 0) goto L_0x1124
            int r14 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r14 >= 0) goto L_0x1130
        L_0x1124:
            r31.save()
            float r6 = r8.chatCallProgress
            float r14 = r6 * r5
            float r6 = r6 * r5
            r9.scale(r14, r6, r2, r0)
        L_0x1130:
            android.graphics.RectF r2 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r14 = r1 - r14
            float r14 = (float) r14
            float r15 = r0 - r3
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = r1 + r16
            float r7 = (float) r7
            float r3 = r3 + r0
            r2.set(r14, r15, r7, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r7
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r6, r7)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r6
            float r3 = (float) r3
            float r6 = r0 - r11
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r1 - r7
            float r7 = (float) r7
            float r0 = r0 + r11
            r2.set(r3, r6, r7, r0)
            android.graphics.RectF r2 = r8.rect
            r3 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r11
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r7, r3, r11)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r1
            float r3 = (float) r3
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r4
            float r1 = (float) r1
            r2.set(r3, r6, r1, r0)
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
            if (r0 < 0) goto L_0x11b4
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x11b7
        L_0x11b4:
            r31.restore()
        L_0x11b7:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x11d7
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x11d7
            r2 = 0
            r8.progressStage = r2
        L_0x11d7:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x11f0
            float r0 = r8.chatCallProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x11ee
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x11ee
            r8.chatCallProgress = r2
        L_0x11ee:
            r2 = 0
            goto L_0x1202
        L_0x11f0:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x1202
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x1202
            r8.chatCallProgress = r2
        L_0x1202:
            r20 = 1
        L_0x1204:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x120d
            r31.restore()
        L_0x120d:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1232
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x1232
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x1232
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
        L_0x1232:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x1293
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x1256
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1246
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1246
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1256
        L_0x1246:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x124f
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x124f
            goto L_0x1256
        L_0x124f:
            r0 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1257
        L_0x1256:
            r1 = 0
        L_0x1257:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1278
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
            goto L_0x1293
        L_0x1278:
            float r2 = (float) r1
            int r0 = r30.getMeasuredHeight()
            r7 = 1
            int r0 = r0 - r7
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            int r0 = r0 - r7
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x1294
        L_0x1293:
            r7 = 1
        L_0x1294:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x12e4
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x12a5
            r31.restore()
            goto L_0x12e4
        L_0x12a5:
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
        L_0x12e4:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x12f2
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x12f0
            goto L_0x12f2
        L_0x12f0:
            r1 = 0
            goto L_0x1321
        L_0x12f2:
            if (r0 == 0) goto L_0x130b
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x12f0
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1309
            r8.reorderIconProgress = r1
        L_0x1309:
            r1 = 0
            goto L_0x131f
        L_0x130b:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1321
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x131f
            r8.reorderIconProgress = r1
        L_0x131f:
            r6 = 1
            goto L_0x1323
        L_0x1321:
            r6 = r20
        L_0x1323:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1351
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x137d
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x133a
            r8.archiveBackgroundProgress = r1
        L_0x133a:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x137c
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x137c
        L_0x1351:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x137d
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1366
            r8.archiveBackgroundProgress = r1
        L_0x1366:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x137c
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x137c:
            r6 = 1
        L_0x137d:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x1393
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x1392
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x1392:
            r6 = 1
        L_0x1393:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x13c1
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13ad
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x13ad
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x13ad:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13e1
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x13e0
            r8.currentRevealProgress = r1
            goto L_0x13e0
        L_0x13c1:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x13cd
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x13cd:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x13e1
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x13e0
            r8.currentRevealProgress = r1
        L_0x13e0:
            r6 = 1
        L_0x13e1:
            if (r6 == 0) goto L_0x13e6
            r30.invalidate()
        L_0x13e6:
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
        this.statusDrawableAnimator.addUpdateListener(new DialogCell$$ExternalSyntheticLambda0(this));
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
    public /* synthetic */ void lambda$createStatusDrawableAnimator$1(ValueAnimator valueAnimator) {
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
