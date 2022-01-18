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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v67, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v233, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v241, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v474, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v481, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v482, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v483, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v486, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v488, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v489, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v490, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v491, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: android.text.SpannableStringBuilder} */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x0655, code lost:
        if (r2.post_messages == false) goto L_0x0631;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0661, code lost:
        if (r2.kicked != false) goto L_0x0631;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0var_, code lost:
        if (r3 != null) goto L_0x0f3d;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x193e  */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x19dd A[Catch:{ Exception -> 0x1a08 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1083:0x19f8 A[Catch:{ Exception -> 0x1a08 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1091:0x1a14  */
    /* JADX WARNING: Removed duplicated region for block: B:1107:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x039b  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0bb6 A[Catch:{ Exception -> 0x0bc9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0bbd A[Catch:{ Exception -> 0x0bc9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0CLASSNAME A[SYNTHETIC, Splitter:B:542:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0cee  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e92  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0ea2  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0f4c  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0f5b  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x1036  */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x10a0  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x10f8  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x110b  */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x1168  */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x11b2  */
    /* JADX WARNING: Removed duplicated region for block: B:782:0x11c2  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x11da  */
    /* JADX WARNING: Removed duplicated region for block: B:787:0x11e9  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x1222  */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x1250  */
    /* JADX WARNING: Removed duplicated region for block: B:821:0x12d9  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x12ef  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x1365 A[Catch:{ Exception -> 0x1384 }] */
    /* JADX WARNING: Removed duplicated region for block: B:847:0x1368 A[Catch:{ Exception -> 0x1384 }] */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x1392  */
    /* JADX WARNING: Removed duplicated region for block: B:860:0x1442  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x14f7  */
    /* JADX WARNING: Removed duplicated region for block: B:873:0x151c  */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x154a  */
    /* JADX WARNING: Removed duplicated region for block: B:911:0x166b  */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1728  */
    /* JADX WARNING: Removed duplicated region for block: B:955:0x1731  */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x1757 A[Catch:{ Exception -> 0x17fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x1760 A[ADDED_TO_REGION, Catch:{ Exception -> 0x17fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x177b A[Catch:{ Exception -> 0x17fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:988:0x17d0 A[Catch:{ Exception -> 0x17fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x17d3 A[Catch:{ Exception -> 0x17fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x1806  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r39 = this;
            r1 = r39
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
            r4 = 33
            r20 = 1102053376(0x41b00000, float:22.0)
            r21 = 1117257728(0x42980000, float:76.0)
            r22 = 1099956224(0x41900000, float:18.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            java.lang.String r12 = ""
            r9 = 2
            if (r2 == 0) goto L_0x039b
            int r0 = r2.type
            if (r0 != r9) goto L_0x01eb
            r1.drawNameLock = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x01b0
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0175
            goto L_0x01b0
        L_0x0175:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0196
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x0196:
            int r0 = r39.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x01b0:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01d1
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x01d1:
            int r0 = r39.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x01eb:
            boolean r2 = r2.verified
            r1.drawVerified = r2
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x0290
            if (r0 != r6) goto L_0x0290
            r1.drawNameGroup = r6
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0249
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0200
            goto L_0x0249
        L_0x0200:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0228
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x021d
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x021f
        L_0x021d:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x021f:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x0228:
            int r0 = r39.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r2
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0238
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x023a
        L_0x0238:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x023a:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x0249:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0270
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0266
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0268
        L_0x0266:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0268:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 + r2
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x0270:
            int r0 = r39.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r2
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0280
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0282
        L_0x0280:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0282:
            int r2 = r2.getIntrinsicWidth()
            int r0 = r0 - r2
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x0290:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x02ab
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0299
            goto L_0x02ab
        L_0x0299:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x02a4
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x02a4:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x02ab:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x02b6
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x02bc
        L_0x02b6:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
        L_0x02bc:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r2 = r0.type
            if (r2 != r6) goto L_0x034a
            r0 = 2131625807(0x7f0e074f, float:1.8878832E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r1.customDialog
            boolean r3 = r2.isMedia
            if (r3 == 0) goto L_0x02f8
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
            r2.setSpan(r3, r7, r5, r4)
            goto L_0x0334
        L_0x02f8:
            java.lang.String r2 = r2.message
            int r3 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0306
            java.lang.String r2 = r2.substring(r7, r4)
        L_0x0306:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0326
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x030f
            goto L_0x0326
        L_0x030f:
            java.lang.Object[] r3 = new java.lang.Object[r9]
            r4 = 10
            r5 = 32
            java.lang.String r2 = r2.replace(r4, r5)
            r3[r7] = r2
            r3[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r3)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x0334
        L_0x0326:
            java.lang.Object[] r3 = new java.lang.Object[r9]
            r3[r7] = r2
            r3[r6] = r0
            java.lang.String r2 = java.lang.String.format(r13, r3)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x0334:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r3 = r3[r4]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r4 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
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
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            int r4 = r4.date
            long r4 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.unread_count
            if (r5 == 0) goto L_0x0378
            r1.drawCount = r6
            java.lang.Object[] r11 = new java.lang.Object[r6]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r11[r7] = r5
            java.lang.String r5 = "%d"
            java.lang.String r5 = java.lang.String.format(r5, r11)
            goto L_0x037b
        L_0x0378:
            r1.drawCount = r7
            r5 = 0
        L_0x037b:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            boolean r13 = r11.sent
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
            java.lang.String r11 = r11.name
            r6 = r3
            r7 = r5
            r9 = -1
            r13 = 0
            r3 = r2
            r5 = r4
            r4 = 0
            r2 = r0
            r0 = 1
            goto L_0x1166
        L_0x039b:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x03b6
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x03a4
            goto L_0x03b6
        L_0x03a4:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03af
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r2
            goto L_0x03c7
        L_0x03af:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x03c7
        L_0x03b6:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03c1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r2
            goto L_0x03c7
        L_0x03c1:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
        L_0x03c7:
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 == 0) goto L_0x0450
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x0602
            r1.drawNameLock = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0415
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x03da
            goto L_0x0415
        L_0x03da:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x03fb
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x03fb:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x0602
        L_0x0415:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0436
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x0436:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x0602
        L_0x0450:
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x0602
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x055c
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x0464
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0474
        L_0x0464:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x0470
            r1.drawScam = r9
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x0474
        L_0x0470:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0474:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x0602
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x04ef
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0482
            goto L_0x04ef
        L_0x0482:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            long r4 = r2.id
            r25 = 0
            int r27 = (r4 > r25 ? 1 : (r4 == r25 ? 0 : -1))
            if (r27 < 0) goto L_0x04a4
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0499
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0499
            goto L_0x04a4
        L_0x0499:
            r1.drawNameGroup = r6
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04ae
        L_0x04a4:
            r1.drawNameBroadcast = r6
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04ae:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x04ce
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x04c3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04c5
        L_0x04c3:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04c5:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x04ce:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x04de
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e0
        L_0x04de:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e0:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x0602
        L_0x04ef:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            long r3 = r2.id
            r25 = 0
            int r27 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
            if (r27 < 0) goto L_0x0511
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0506
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0506
            goto L_0x0511
        L_0x0506:
            r1.drawNameGroup = r6
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x051b
        L_0x0511:
            r1.drawNameBroadcast = r6
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x051b:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x053b
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x0530
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0532
        L_0x0530:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0532:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x053b:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x054b
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x054d
        L_0x054b:
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x054d:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x0602
        L_0x055c:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x0602
            boolean r4 = r2.scam
            if (r4 == 0) goto L_0x056c
            r1.drawScam = r6
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x057c
        L_0x056c:
            boolean r4 = r2.fake
            if (r4 == 0) goto L_0x0578
            r1.drawScam = r9
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r2.checkText()
            goto L_0x057c
        L_0x0578:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x057c:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x0602
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x0602
            r1.drawNameBot = r6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x05ca
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0591
            goto L_0x05ca
        L_0x0591:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x05b1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x05b1:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x0602
        L_0x05ca:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x05ea
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x0602
        L_0x05ea:
            int r2 = r39.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r3
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
        L_0x0602:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x060e
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x060e
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            int r2 = r2.date
        L_0x060e:
            boolean r3 = r1.isDialogCell
            if (r3 == 0) goto L_0x0667
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            long r4 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r3.getDraft(r4, r7)
            r1.draftMessage = r3
            if (r3 == 0) goto L_0x063d
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0633
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.reply_to_msg_id
            if (r3 == 0) goto L_0x0631
            goto L_0x0633
        L_0x0631:
            r2 = 0
            goto L_0x0664
        L_0x0633:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            int r3 = r3.date
            if (r2 <= r3) goto L_0x063d
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x0631
        L_0x063d:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0657
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x0657
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x0657
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x0631
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x0631
        L_0x0657:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x066a
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x0631
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x066a
            goto L_0x0631
        L_0x0664:
            r1.draftMessage = r2
            goto L_0x066a
        L_0x0667:
            r2 = 0
            r1.draftMessage = r2
        L_0x066a:
            if (r0 == 0) goto L_0x06f3
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r3 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r3, r7)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0692
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x0693
        L_0x0692:
            r2 = 0
        L_0x0693:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>()
            java.lang.String[] r4 = new java.lang.String[r6]
            java.lang.String r5 = "..."
            r4[r7] = r5
            java.lang.String[] r5 = new java.lang.String[r6]
            r5[r7] = r12
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r4, r5)
            int r4 = r1.printingStringType
            r5 = 5
            if (r4 != r5) goto L_0x06b6
            java.lang.String r4 = r0.toString()
            java.lang.String r5 = "**oo**"
            int r4 = r4.indexOf(r5)
            goto L_0x06b7
        L_0x06b6:
            r4 = -1
        L_0x06b7:
            if (r4 < 0) goto L_0x06d2
            android.text.SpannableStringBuilder r0 = r3.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r5 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r5 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r5)
            int r5 = r5.getIntrinsicWidth()
            r2.<init>(r5)
            int r5 = r4 + 6
            r0.setSpan(r2, r4, r5, r7)
            goto L_0x06e4
        L_0x06d2:
            java.lang.String r5 = " "
            android.text.SpannableStringBuilder r5 = r3.append(r5)
            android.text.SpannableStringBuilder r0 = r5.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r5 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r5.<init>(r2)
            r0.setSpan(r5, r7, r6, r7)
        L_0x06e4:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r16 = r4
            r0 = 0
            r2 = 2
            r4 = 0
            r6 = 0
            r7 = 1
            goto L_0x0var_
        L_0x06f3:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            r2 = 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x078c
            r0 = 2131625333(0x7f0e0575, float:1.887787E38)
            java.lang.String r3 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0734
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x072f
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0718
            goto L_0x072f
        L_0x0718:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r5 = "chats_draft"
            r2.<init>(r5, r4)
            int r4 = r0.length()
            r5 = 33
            r3.setSpan(r2, r7, r4, r5)
            goto L_0x0730
        L_0x072f:
            r3 = r12
        L_0x0730:
            r2 = 2
            r4 = 0
            r6 = 0
            goto L_0x07a2
        L_0x0734:
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x0744
            java.lang.String r3 = r3.substring(r7, r5)
        L_0x0744:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r3)
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC$DraftMessage) r3, (android.text.Spannable) r4, (int) r2)
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r9]
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r4)
            r2[r7] = r3
            r2[r6] = r0
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r2)
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0777
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x0777
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.String r5 = "chats_draft"
            r3.<init>(r5, r4)
            int r4 = r0.length()
            int r4 = r4 + r6
            r5 = 33
            r2.setSpan(r3, r7, r4, r5)
        L_0x0777:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r3 = r3[r4]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r4 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r7)
            goto L_0x0730
        L_0x078c:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x07a7
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r0 = 2131625912(0x7f0e07b8, float:1.8879045E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x079f:
            r0 = 0
            r2 = 2
            r4 = 0
        L_0x07a2:
            r7 = 1
        L_0x07a3:
            r16 = -1
            goto L_0x0var_
        L_0x07a7:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x083a
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x081b
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r10 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x07c3
            r0 = 2131625433(0x7f0e05d9, float:1.8878074E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x079f
        L_0x07c3:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x07db
            r0 = 2131624546(0x7f0e0262, float:1.8876275E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x079f
        L_0x07db:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x07e9
            r0 = 2131625434(0x7f0e05da, float:1.8878076E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x079f
        L_0x07e9:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x0837
            long r2 = r0.admin_id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r4 = r0.getClientUserId()
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x0811
            r0 = 2131625422(0x7f0e05ce, float:1.8878052E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r7] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x079f
        L_0x0811:
            r0 = 2131625421(0x7f0e05cd, float:1.887805E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x079f
        L_0x081b:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x0837
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x0837
            r0 = 2131627672(0x7f0e0e98, float:1.8882615E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r4 = 0
            r11 = 0
            goto L_0x07a3
        L_0x0837:
            r3 = r12
            goto L_0x079f
        L_0x083a:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r3 = r1.message
            long r3 = r3.getFromChatId()
            boolean r5 = org.telegram.messenger.DialogObject.isUserDialog(r3)
            if (r5 == 0) goto L_0x085e
            int r5 = r1.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
            r4 = 0
            goto L_0x086f
        L_0x085e:
            int r5 = r1.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r5.getChat(r3)
            r4 = r3
            r3 = 0
        L_0x086f:
            int r5 = r1.dialogsType
            r2 = 3
            if (r5 != r2) goto L_0x088e
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r2 == 0) goto L_0x088e
            r0 = 2131627672(0x7f0e0e98, float:1.8882615E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 0
            r6 = 0
            r7 = 1
            r11 = 0
            goto L_0x0var_
        L_0x088e:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x08a4
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 != 0) goto L_0x08a4
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x08a4
            java.lang.CharSequence r0 = r39.formatArchivedDialogNames()
            r3 = r0
            r0 = 0
            r2 = 2
            r4 = 0
            goto L_0x0var_
        L_0x08a4:
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r2 == 0) goto L_0x08d1
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 == 0) goto L_0x08c4
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x08c2
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x08c4
        L_0x08c2:
            r15 = r12
            r11 = 0
        L_0x08c4:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            r3 = r15
            r0 = 0
            r2 = 2
            r4 = 0
        L_0x08ce:
            r7 = 1
            goto L_0x0var_
        L_0x08d1:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x09d2
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x09d2
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r1.encryptedChat
            if (r2 != 0) goto L_0x09d2
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.needDrawBluredPreview()
            if (r2 != 0) goto L_0x09d2
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 != 0) goto L_0x08ff
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isNewGif()
            if (r2 != 0) goto L_0x08ff
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x09d2
        L_0x08ff:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isWebpage()
            if (r2 == 0) goto L_0x0912
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            java.lang.String r2 = r2.type
            goto L_0x0913
        L_0x0912:
            r2 = 0
        L_0x0913:
            java.lang.String r5 = "app"
            boolean r5 = r5.equals(r2)
            if (r5 != 0) goto L_0x09d2
            java.lang.String r5 = "profile"
            boolean r5 = r5.equals(r2)
            if (r5 != 0) goto L_0x09d2
            java.lang.String r5 = "article"
            boolean r5 = r5.equals(r2)
            if (r5 != 0) goto L_0x09d2
            if (r2 == 0) goto L_0x0935
            java.lang.String r5 = "telegram_"
            boolean r2 = r2.startsWith(r5)
            if (r2 != 0) goto L_0x09d2
        L_0x0935:
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.photoThumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r5)
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.photoThumbs
            int r7 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r7)
            if (r2 != r5) goto L_0x094e
            r5 = 0
        L_0x094e:
            if (r2 == 0) goto L_0x09d2
            r1.hasMessageThumb = r6
            org.telegram.messenger.MessageObject r7 = r1.message
            boolean r7 = r7.isVideo()
            r1.drawPlay = r7
            java.lang.String r7 = org.telegram.messenger.FileLoader.getAttachFileName(r5)
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.mediaExists
            if (r9 != 0) goto L_0x099d
            int r9 = r1.currentAccount
            org.telegram.messenger.DownloadController r9 = org.telegram.messenger.DownloadController.getInstance(r9)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r9.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r6 != 0) goto L_0x099d
            int r6 = r1.currentAccount
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)
            boolean r6 = r6.isLoadingFile(r7)
            if (r6 == 0) goto L_0x097f
            goto L_0x099d
        L_0x097f:
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            r29 = 0
            r30 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r31 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            r33 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r35 = 0
            java.lang.String r32 = "20_20"
            r28 = r5
            r34 = r2
            r28.setImage((org.telegram.messenger.ImageLocation) r29, (java.lang.String) r30, (org.telegram.messenger.ImageLocation) r31, (java.lang.String) r32, (android.graphics.drawable.Drawable) r33, (java.lang.Object) r34, (int) r35)
            goto L_0x09d0
        L_0x099d:
            org.telegram.messenger.MessageObject r6 = r1.message
            int r7 = r6.type
            r9 = 1
            if (r7 != r9) goto L_0x09ad
            if (r5 == 0) goto L_0x09a9
            int r7 = r5.size
            goto L_0x09aa
        L_0x09a9:
            r7 = 0
        L_0x09aa:
            r33 = r7
            goto L_0x09af
        L_0x09ad:
            r33 = 0
        L_0x09af:
            org.telegram.messenger.ImageReceiver r7 = r1.thumbImage
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r29 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r31 = org.telegram.messenger.ImageLocation.getForObject(r2, r5)
            r34 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r36 = 0
            java.lang.String r30 = "20_20"
            java.lang.String r32 = "20_20"
            r28 = r7
            r35 = r2
            r28.setImage(r29, r30, r31, r32, r33, r34, r35, r36)
        L_0x09d0:
            r2 = 0
            goto L_0x09d3
        L_0x09d2:
            r2 = 1
        L_0x09d3:
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            if (r5 == 0) goto L_0x0ce5
            long r6 = r5.id
            r28 = 0
            int r9 = (r6 > r28 ? 1 : (r6 == r28 ? 0 : -1))
            if (r9 <= 0) goto L_0x0ce5
            if (r4 != 0) goto L_0x0ce5
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r4 == 0) goto L_0x09ef
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isMegagroup(r4)
            if (r4 == 0) goto L_0x0ce5
        L_0x09ef:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isOutOwner()
            if (r4 == 0) goto L_0x0a01
            r3 = 2131625807(0x7f0e074f, float:1.8878832E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0a48
        L_0x0a01:
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x0a11
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from
            if (r4 == 0) goto L_0x0a11
            java.lang.String r4 = r4.from_name
            if (r4 == 0) goto L_0x0a11
            r3 = r4
            goto L_0x0a48
        L_0x0a11:
            if (r3 == 0) goto L_0x0a46
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0a27
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0a1c
            goto L_0x0a27
        L_0x0a1c:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r12)
            goto L_0x0a48
        L_0x0a27:
            boolean r4 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r4 == 0) goto L_0x0a37
            r3 = 2131625899(0x7f0e07ab, float:1.8879019E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0a48
        L_0x0a37:
            java.lang.String r4 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r4, r3)
            java.lang.String r4 = "\n"
            java.lang.String r3 = r3.replace(r4, r12)
            goto L_0x0a48
        L_0x0a46:
            java.lang.String r3 = "DELETED"
        L_0x0a48:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x0a62
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r4 = 0
            r2[r4] = r0
            r5 = 1
            r2[r5] = r3
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0a5f:
            r2 = r0
            goto L_0x0CLASSNAME
        L_0x0a62:
            r4 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0ad8
            java.lang.String r0 = r5.toString()
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0a79
            java.lang.CharSequence r0 = r0.subSequence(r4, r6)
        L_0x0a79:
            if (r2 != 0) goto L_0x0a7d
            r2 = r12
            goto L_0x0aab
        L_0x0a7d:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0a88
            java.lang.String r2 = "📹 "
            goto L_0x0aab
        L_0x0a88:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0a93
            java.lang.String r2 = "🎤 "
            goto L_0x0aab
        L_0x0a93:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0a9e
            java.lang.String r2 = "🎧 "
            goto L_0x0aab
        L_0x0a9e:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x0aa9
            java.lang.String r2 = "🖼 "
            goto L_0x0aab
        L_0x0aa9:
            java.lang.String r2 = "📎 "
        L_0x0aab:
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            java.lang.CharSequence r0 = r0.caption
            r6 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r5, r0, r4, r6)
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
            goto L_0x0a5f
        L_0x0ad8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0bcf
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0bcf
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r10 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0b1e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b0d
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r5 = 0
            r0[r5] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b50
        L_0x0b0d:
            r4 = 1
            r5 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r5] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b50
        L_0x0b1e:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0b4a
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0b39
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r5 = 0
            r0[r5] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b50
        L_0x0b39:
            r4 = 1
            r5 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r5] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b50
        L_0x0b4a:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0b52
            java.lang.String r0 = r2.title
        L_0x0b50:
            r6 = 1
            goto L_0x0b97
        L_0x0b52:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0b92
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r2 < r4) goto L_0x0b78
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
            goto L_0x0b97
        L_0x0b78:
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
            goto L_0x0b97
        L_0x0b92:
            r6 = 1
            java.lang.String r0 = r15.toString()
        L_0x0b97:
            r2 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r2, r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r2 = 0
            r4[r2] = r0
            r4[r6] = r3
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bc9 }
            java.lang.String r4 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0bc9 }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0bc9 }
            if (r14 == 0) goto L_0x0bbd
            int r4 = r3.length()     // Catch:{ Exception -> 0x0bc9 }
            r5 = 2
            int r4 = r4 + r5
            goto L_0x0bbe
        L_0x0bbd:
            r4 = 0
        L_0x0bbe:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0bc9 }
            r6 = 33
            r2.setSpan(r0, r4, r5, r6)     // Catch:{ Exception -> 0x0bc9 }
            goto L_0x0CLASSNAME
        L_0x0bc9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0CLASSNAME
        L_0x0bcf:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0CLASSNAME
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0be4
            r2 = r0
        L_0x0be4:
            int r0 = r39.getMeasuredWidth()
            r4 = 1121058816(0x42d20000, float:105.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            if (r14 == 0) goto L_0x0c0b
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
        L_0x0c0b:
            if (r0 <= 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r0, r10, r6)
            java.lang.String r2 = r0.toString()
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = 0
            int r0 = r2.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r0 <= r4) goto L_0x0CLASSNAME
            java.lang.CharSequence r2 = r2.subSequence(r5, r4)
        L_0x0CLASSNAME:
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r2)
        L_0x0CLASSNAME:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r2, (android.text.Spannable) r0, (int) r4)
            r2 = 2
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r2]
            r4[r5] = r0
            r2 = 1
            r4[r2] = r3
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r13, r4)
            goto L_0x0a5f
        L_0x0CLASSNAME:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r12)
            goto L_0x0a5f
        L_0x0CLASSNAME:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0c5e
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0CLASSNAME
        L_0x0c5e:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r4 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider     // Catch:{ Exception -> 0x0CLASSNAME }
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r4 = r3.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            r5 = 1
            int r4 = r4 + r5
            r5 = 33
            r6 = 0
            r2.setSpan(r0, r6, r4, r5)     // Catch:{ Exception -> 0x0c7f }
            r0 = r4
            goto L_0x0c8a
        L_0x0c7f:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r4 = 0
        L_0x0CLASSNAME:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0c8a
        L_0x0CLASSNAME:
            r0 = 0
            r4 = 0
        L_0x0c8a:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r5 = r5[r6]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r6 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r6 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r5, r7, r6)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0cb4
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            if (r5 == 0) goto L_0x0cb4
            r2 = r5
        L_0x0cb4:
            boolean r5 = r1.hasMessageThumb
            if (r5 == 0) goto L_0x0cdd
            boolean r5 = r2 instanceof android.text.SpannableStringBuilder
            if (r5 != 0) goto L_0x0cc2
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
            r5.<init>(r2)
            r2 = r5
        L_0x0cc2:
            r5 = r2
            android.text.SpannableStringBuilder r5 = (android.text.SpannableStringBuilder) r5
            java.lang.String r6 = " "
            r5.insert(r4, r6)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r6 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r7 = r8 + 6
            float r7 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.<init>(r7)
            int r7 = r4 + 1
            r9 = 33
            r5.setSpan(r6, r4, r7, r9)
        L_0x0cdd:
            r4 = r0
            r0 = r3
            r6 = 1
            r7 = 0
            r3 = r2
            r2 = 2
            goto L_0x0var_
        L_0x0ce5:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0cee
        L_0x0ceb:
            r2 = 2
            goto L_0x0e9e
        L_0x0cee:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0d0c
            org.telegram.tgnet.TLRPC$Photo r4 = r3.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0d0c
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d0c
            r0 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0ceb
        L_0x0d0c:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0d24
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0d24
            int r4 = r3.ttl_seconds
            if (r4 == 0) goto L_0x0d24
            r0 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0ceb
        L_0x0d24:
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0dd8
            if (r2 != 0) goto L_0x0d2c
            r0 = r12
            goto L_0x0d58
        L_0x0d2c:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0d35
            java.lang.String r0 = "📹 "
            goto L_0x0d58
        L_0x0d35:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0d40
            java.lang.String r0 = "🎤 "
            goto L_0x0d58
        L_0x0d40:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0d4b
            java.lang.String r0 = "🎧 "
            goto L_0x0d58
        L_0x0d4b:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0d56
            java.lang.String r0 = "🖼 "
            goto L_0x0d58
        L_0x0d56:
            java.lang.String r0 = "📎 "
        L_0x0d58:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0db7
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0db7
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r39.getMeasuredWidth()
            r4 = 1122893824(0x42ee0000, float:119.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r14 == 0) goto L_0x0d8f
            r4 = 0
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x0d8e
            float r3 = (float) r3
            java.lang.String r5 = ": "
            float r5 = r10.measureText(r5)
            float r3 = r3 - r5
            int r3 = (int) r3
            goto L_0x0d8f
        L_0x0d8e:
            throw r4
        L_0x0d8f:
            if (r3 <= 0) goto L_0x0da6
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.String r4 = (java.lang.String) r4
            r5 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r4, r3, r10, r5)
            java.lang.String r2 = r2.toString()
        L_0x0da6:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0ceb
        L_0x0db7:
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
            goto L_0x0ceb
        L_0x0dd8:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0df6
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0df3:
            r2 = 2
            goto L_0x0e8a
        L_0x0df6:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0e16
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
            goto L_0x0df3
        L_0x0e16:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0e1d
            java.lang.String r0 = r3.title
            goto L_0x0df3
        L_0x0e1d:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0e3d
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
            goto L_0x0e8a
        L_0x0e3d:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0e75
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e75
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r39.getMeasuredWidth()
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
            goto L_0x0e81
        L_0x0e75:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            r4 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r3, (android.text.Spannable) r0, (int) r4)
        L_0x0e81:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
        L_0x0e8a:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            if (r4 == 0) goto L_0x0e9e
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0e9e
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r10 = r3[r4]
        L_0x0e9e:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0ede
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0ede
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r39.getMeasuredWidth()
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
            goto L_0x0eef
        L_0x0ede:
            r5 = 0
            int r3 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r3 <= r4) goto L_0x0eeb
            java.lang.CharSequence r0 = r0.subSequence(r5, r4)
        L_0x0eeb:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0eef:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0ef9
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0ef9:
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
            if (r4 == 0) goto L_0x0f3c
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            if (r3 == 0) goto L_0x0f3c
            goto L_0x0f3d
        L_0x0f3c:
            r3 = r0
        L_0x0f3d:
            r0 = 0
            r4 = 0
            r6 = 1
            r7 = 0
            goto L_0x0var_
        L_0x0var_:
            r3 = r0
            r0 = 0
            r4 = 0
            r6 = 1
            goto L_0x08ce
        L_0x0var_:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x0var_
            java.lang.CharSequence r0 = r39.formatArchivedDialogNames()
        L_0x0var_:
            r16 = -1
            r38 = r7
            r7 = r6
            r6 = r38
        L_0x0var_:
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x0var_
            int r5 = r5.date
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0f7c
        L_0x0var_:
            int r5 = r1.lastMessageDate
            if (r5 == 0) goto L_0x0f6d
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0f7c
        L_0x0f6d:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x0f7b
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.date
            long r13 = (long) r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0f7c
        L_0x0f7b:
            r5 = r12
        L_0x0f7c:
            org.telegram.messenger.MessageObject r9 = r1.message
            if (r9 != 0) goto L_0x0var_
            r13 = 0
            r1.drawCheck1 = r13
            r1.drawCheck2 = r13
            r1.drawClock = r13
            r1.drawCount = r13
            r1.drawMention = r13
            r1.drawError = r13
            r2 = 0
            r9 = 0
        L_0x0f8f:
            r11 = 0
            goto L_0x1094
        L_0x0var_:
            r13 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0fd4
            int r9 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r9 + r14
            if (r15 <= 0) goto L_0x0fcd
            if (r9 <= r14) goto L_0x0fb6
            r15 = 1
            r1.drawCount = r15
            r1.drawMention = r13
            java.lang.Object[] r2 = new java.lang.Object[r15]
            int r9 = r9 + r14
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r2[r13] = r9
            java.lang.String r9 = "%d"
            java.lang.String r2 = java.lang.String.format(r9, r2)
            goto L_0x0fd2
        L_0x0fb6:
            r15 = 1
            r1.drawCount = r13
            r1.drawMention = r15
            java.lang.Object[] r2 = new java.lang.Object[r15]
            int r9 = r9 + r14
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r2[r13] = r9
            java.lang.String r9 = "%d"
            java.lang.String r2 = java.lang.String.format(r9, r2)
            r9 = r2
            r2 = 0
            goto L_0x101e
        L_0x0fcd:
            r1.drawCount = r13
            r1.drawMention = r13
            r2 = 0
        L_0x0fd2:
            r9 = 0
            goto L_0x101e
        L_0x0fd4:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0fdf
            r1.drawCount = r13
            r2 = 0
            r9 = 1
            r11 = 0
            r14 = 0
            goto L_0x1012
        L_0x0fdf:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x1005
            r13 = 1
            if (r2 != r13) goto L_0x0ff2
            int r13 = r1.mentionCount
            if (r2 != r13) goto L_0x0ff2
            if (r9 == 0) goto L_0x0ff2
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            boolean r9 = r9.mentioned
            if (r9 != 0) goto L_0x1005
        L_0x0ff2:
            r9 = 1
            r1.drawCount = r9
            java.lang.Object[] r13 = new java.lang.Object[r9]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r13[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r13)
            goto L_0x1012
        L_0x1005:
            r9 = 1
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x100f
            r1.drawCount = r9
            r2 = r12
            goto L_0x1012
        L_0x100f:
            r1.drawCount = r14
            r2 = 0
        L_0x1012:
            int r13 = r1.mentionCount
            if (r13 == 0) goto L_0x101b
            r1.drawMention = r9
            java.lang.String r9 = "@"
            goto L_0x101e
        L_0x101b:
            r1.drawMention = r14
            goto L_0x0fd2
        L_0x101e:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isOut()
            if (r13 == 0) goto L_0x108b
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 != 0) goto L_0x108b
            if (r11 == 0) goto L_0x108b
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r13 != 0) goto L_0x108b
            boolean r11 = r11.isSending()
            if (r11 == 0) goto L_0x1047
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r13 = 1
            r1.drawClock = r13
            r1.drawError = r11
            goto L_0x1094
        L_0x1047:
            r11 = 0
            r13 = 1
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x105e
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r13
            r1.drawCount = r11
            r1.drawMention = r11
            goto L_0x1094
        L_0x105e:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isSent()
            if (r11 == 0) goto L_0x0f8f
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isUnread()
            if (r11 == 0) goto L_0x107f
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x107d
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x107d
            goto L_0x107f
        L_0x107d:
            r11 = 0
            goto L_0x1080
        L_0x107f:
            r11 = 1
        L_0x1080:
            r1.drawCheck1 = r11
            r11 = 1
            r1.drawCheck2 = r11
            r11 = 0
            r1.drawClock = r11
            r1.drawError = r11
            goto L_0x1094
        L_0x108b:
            r11 = 0
            r1.drawCheck1 = r11
            r1.drawCheck2 = r11
            r1.drawClock = r11
            r1.drawError = r11
        L_0x1094:
            r1.promoDialog = r11
            int r11 = r1.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r13 = r1.dialogsType
            if (r13 != 0) goto L_0x10f4
            long r13 = r1.currentDialogId
            r15 = 1
            boolean r13 = r11.isPromoDialog(r13, r15)
            if (r13 == 0) goto L_0x10f4
            r1.drawPinBackground = r15
            r1.promoDialog = r15
            int r13 = r11.promoDialogType
            int r14 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r13 != r14) goto L_0x10bd
            r5 = 2131628339(0x7f0e1133, float:1.8883968E38)
            java.lang.String r11 = "UseProxySponsor"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            goto L_0x10f4
        L_0x10bd:
            int r14 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r13 != r14) goto L_0x10f4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = "PsaType_"
            r5.append(r13)
            java.lang.String r13 = r11.promoPsaType
            r5.append(r13)
            java.lang.String r5 = r5.toString()
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5)
            boolean r13 = android.text.TextUtils.isEmpty(r5)
            if (r13 == 0) goto L_0x10e7
            r5 = 2131627403(0x7f0e0d8b, float:1.888207E38)
            java.lang.String r13 = "PsaTypeDefault"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
        L_0x10e7:
            java.lang.String r13 = r11.promoPsaMessage
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 != 0) goto L_0x10f4
            java.lang.String r3 = r11.promoPsaMessage
            r11 = 0
            r1.hasMessageThumb = r11
        L_0x10f4:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x110b
            r11 = 2131624334(0x7f0e018e, float:1.8875845E38)
            java.lang.String r13 = "ArchivedChats"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
        L_0x1101:
            r13 = r9
            r9 = r16
            r38 = r2
            r2 = r0
            r0 = r7
            r7 = r38
            goto L_0x1166
        L_0x110b:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            if (r11 == 0) goto L_0x1112
            java.lang.String r11 = r11.title
            goto L_0x1156
        L_0x1112:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            if (r11 == 0) goto L_0x1155
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r11)
            if (r11 == 0) goto L_0x1126
            r11 = 2131627538(0x7f0e0e12, float:1.8882343E38)
            java.lang.String r13 = "RepliesTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            goto L_0x1156
        L_0x1126:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 == 0) goto L_0x114e
            boolean r11 = r1.useMeForMyMessages
            if (r11 == 0) goto L_0x113c
            r11 = 2131625807(0x7f0e074f, float:1.8878832E38)
            java.lang.String r13 = "FromYou"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            goto L_0x1156
        L_0x113c:
            int r11 = r1.dialogsType
            r13 = 3
            if (r11 != r13) goto L_0x1144
            r11 = 1
            r1.drawPinBackground = r11
        L_0x1144:
            r11 = 2131627671(0x7f0e0e97, float:1.8882613E38)
            java.lang.String r13 = "SavedMessages"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            goto L_0x1156
        L_0x114e:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            goto L_0x1156
        L_0x1155:
            r11 = r12
        L_0x1156:
            int r13 = r11.length()
            if (r13 != 0) goto L_0x1101
            r11 = 2131625899(0x7f0e07ab, float:1.8879019E38)
            java.lang.String r13 = "HiddenName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            goto L_0x1101
        L_0x1166:
            if (r0 == 0) goto L_0x11a7
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r5)
            double r14 = (double) r0
            double r14 = java.lang.Math.ceil(r14)
            int r0 = (int) r14
            android.text.StaticLayout r14 = new android.text.StaticLayout
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r14
            r29 = r5
            r31 = r0
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)
            r1.timeLayout = r14
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x119e
            int r5 = r39.getMeasuredWidth()
            r14 = 1097859072(0x41700000, float:15.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r5 = r5 - r14
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x11ae
        L_0x119e:
            r14 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.timeLeft = r5
            goto L_0x11ae
        L_0x11a7:
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r0 = 0
        L_0x11ae:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x11c2
            int r5 = r39.getMeasuredWidth()
            int r14 = r1.nameLeft
            int r5 = r5 - r14
            r14 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r5 = r5 - r14
            int r5 = r5 - r0
            goto L_0x11d6
        L_0x11c2:
            int r5 = r39.getMeasuredWidth()
            int r14 = r1.nameLeft
            int r5 = r5 - r14
            r14 = 1117388800(0x429a0000, float:77.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r5 = r5 - r14
            int r5 = r5 - r0
            int r14 = r1.nameLeft
            int r14 = r14 + r0
            r1.nameLeft = r14
        L_0x11d6:
            boolean r14 = r1.drawNameLock
            if (r14 == 0) goto L_0x11e9
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
        L_0x11e6:
            int r14 = r14 + r15
            int r5 = r5 - r14
            goto L_0x121c
        L_0x11e9:
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x11fa
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r15 = r15.getIntrinsicWidth()
            goto L_0x11e6
        L_0x11fa:
            boolean r14 = r1.drawNameBroadcast
            if (r14 == 0) goto L_0x120b
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r15 = r15.getIntrinsicWidth()
            goto L_0x11e6
        L_0x120b:
            boolean r14 = r1.drawNameBot
            if (r14 == 0) goto L_0x121c
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r15 = r15.getIntrinsicWidth()
            goto L_0x11e6
        L_0x121c:
            boolean r14 = r1.drawClock
            r15 = 1084227584(0x40a00000, float:5.0)
            if (r14 == 0) goto L_0x1250
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 + r16
            int r5 = r5 - r14
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            if (r16 != 0) goto L_0x123b
            int r0 = r1.timeLeft
            int r0 = r0 - r14
            r1.clockDrawLeft = r0
            r16 = r5
            goto L_0x124c
        L_0x123b:
            r16 = r5
            int r5 = r1.timeLeft
            int r5 = r5 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r5 = r5 + r0
            r1.clockDrawLeft = r5
            int r0 = r1.nameLeft
            int r0 = r0 + r14
            r1.nameLeft = r0
        L_0x124c:
            r5 = r16
            goto L_0x12cb
        L_0x1250:
            boolean r14 = r1.drawCheck2
            if (r14 == 0) goto L_0x12cb
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r14 = r14.getIntrinsicWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 + r16
            int r5 = r5 - r14
            boolean r15 = r1.drawCheck1
            if (r15 == 0) goto L_0x12b0
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            r18 = 1090519040(0x41000000, float:8.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r15 = r15 - r18
            int r5 = r5 - r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x1287
            int r0 = r1.timeLeft
            int r0 = r0 - r14
            r1.halfCheckDrawLeft = r0
            r14 = 1085276160(0x40b00000, float:5.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r14
            r1.checkDrawLeft = r0
            goto L_0x12cb
        L_0x1287:
            int r15 = r1.timeLeft
            int r15 = r15 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r0
            r1.checkDrawLeft = r15
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r0
            r1.halfCheckDrawLeft = r15
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r15 = 1090519040(0x41000000, float:8.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 - r15
            int r0 = r0 + r14
            r1.nameLeft = r0
            goto L_0x12cb
        L_0x12b0:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x12ba
            int r0 = r1.timeLeft
            int r0 = r0 - r14
            r1.checkDrawLeft1 = r0
            goto L_0x12cb
        L_0x12ba:
            int r15 = r1.timeLeft
            int r15 = r15 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r0
            r1.checkDrawLeft1 = r15
            int r0 = r1.nameLeft
            int r0 = r0 + r14
            r1.nameLeft = r0
        L_0x12cb:
            boolean r0 = r1.dialogMuted
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x12ef
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x12ef
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x12ef
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r15 = r15.getIntrinsicWidth()
            int r0 = r0 + r15
            int r5 = r5 - r0
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x132a
            int r15 = r1.nameLeft
            int r15 = r15 + r0
            r1.nameLeft = r15
            goto L_0x132a
        L_0x12ef:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1309
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r15 = r15.getIntrinsicWidth()
            int r0 = r0 + r15
            int r5 = r5 - r0
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x132a
            int r15 = r1.nameLeft
            int r15 = r15 + r0
            r1.nameLeft = r15
            goto L_0x132a
        L_0x1309:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x132a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r1.drawScam
            r14 = 1
            if (r15 != r14) goto L_0x1319
            org.telegram.ui.Components.ScamDrawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x131b
        L_0x1319:
            org.telegram.ui.Components.ScamDrawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x131b:
            int r14 = r14.getIntrinsicWidth()
            int r0 = r0 + r14
            int r5 = r5 - r0
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x132a
            int r14 = r1.nameLeft
            int r14 = r14 + r0
            r1.nameLeft = r14
        L_0x132a:
            r14 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r5 = java.lang.Math.max(r0, r5)
            r0 = 10
            r15 = 32
            java.lang.String r0 = r11.replace(r0, r15)     // Catch:{ Exception -> 0x1384 }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1384 }
            int r15 = r1.paintIndex     // Catch:{ Exception -> 0x1384 }
            r11 = r11[r15]     // Catch:{ Exception -> 0x1384 }
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ Exception -> 0x1384 }
            int r15 = r5 - r15
            float r15 = (float) r15     // Catch:{ Exception -> 0x1384 }
            android.text.TextUtils$TruncateAt r14 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1384 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r11, r15, r14)     // Catch:{ Exception -> 0x1384 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x1384 }
            if (r11 == 0) goto L_0x1368
            boolean r11 = r11.hasHighlightedWords()     // Catch:{ Exception -> 0x1384 }
            if (r11 == 0) goto L_0x1368
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x1384 }
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords     // Catch:{ Exception -> 0x1384 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r14 = r1.resourcesProvider     // Catch:{ Exception -> 0x1384 }
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r11, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r14)     // Catch:{ Exception -> 0x1384 }
            if (r11 == 0) goto L_0x1368
            r29 = r11
            goto L_0x136a
        L_0x1368:
            r29 = r0
        L_0x136a:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1384 }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1384 }
            int r14 = r1.paintIndex     // Catch:{ Exception -> 0x1384 }
            r30 = r11[r14]     // Catch:{ Exception -> 0x1384 }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1384 }
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r0
            r31 = r5
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x1384 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x1384 }
            goto L_0x1388
        L_0x1384:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1388:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1442
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1392
            goto L_0x1442
        L_0x1392:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11 = 1106771968(0x41var_, float:31.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.messageNameTop = r11
            r11 = 1098907648(0x41800000, float:16.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.timeTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.errorTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.pinTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.countTop = r11
            r11 = 1099431936(0x41880000, float:17.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.checkDrawTop = r14
            int r11 = r39.getMeasuredWidth()
            r14 = 1119748096(0x42be0000, float:95.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r11 = r11 - r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x13f4
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r14
            r1.messageLeft = r14
            int r14 = r39.getMeasuredWidth()
            r15 = 1115684864(0x42800000, float:64.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 - r15
            int r15 = r8 + 11
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r14 - r15
            goto L_0x1409
        L_0x13f4:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r14
            r1.messageLeft = r14
            r14 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r14
        L_0x1409:
            r17 = r11
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r14 = (float) r14
            r19 = r12
            float r12 = (float) r0
            r20 = 1113063424(0x42580000, float:54.0)
            r21 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r9 = (float) r9
            r24 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r4 = (float) r4
            r11.setImageCoords(r14, r12, r9, r4)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r9 = (float) r15
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            float r12 = (float) r8
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r14 = (float) r14
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r4.setImageCoords(r9, r11, r14, r12)
            r4 = r0
            r11 = r17
            goto L_0x14f3
        L_0x1442:
            r24 = r4
            r21 = r9
            r19 = r12
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
            int r4 = r39.getMeasuredWidth()
            r9 = 1119485952(0x42ba0000, float:93.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r11 = r4 - r9
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x14ac
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r39.getMeasuredWidth()
            r9 = 1115947008(0x42840000, float:66.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r4 = r4 - r9
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r4 - r9
            goto L_0x14c1
        L_0x14ac:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r4
        L_0x14c1:
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            float r4 = (float) r4
            float r14 = (float) r0
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r17 = 1113587712(0x42600000, float:56.0)
            r20 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            r12.setImageCoords(r4, r14, r15, r11)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r9 = (float) r9
            r11 = 1106771968(0x41var_, float:31.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r12 = (float) r12
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r14 = (float) r14
            r4.setImageCoords(r9, r11, r12, r14)
            r4 = r0
            r11 = r20
        L_0x14f3:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1518
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1510
            int r0 = r39.getMeasuredWidth()
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r9 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.pinLeft = r0
            goto L_0x1518
        L_0x1510:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x1518:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x154a
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r11 = r11 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x1536
            int r0 = r39.getMeasuredWidth()
            r7 = 1107820544(0x42080000, float:34.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            r1.errorLeft = r0
            goto L_0x1669
        L_0x1536:
            r7 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.errorLeft = r7
            int r7 = r1.messageLeft
            int r7 = r7 + r0
            r1.messageLeft = r7
            int r7 = r1.messageNameLeft
            int r7 = r7 + r0
            r1.messageNameLeft = r7
            goto L_0x1669
        L_0x154a:
            if (r7 != 0) goto L_0x1576
            if (r13 == 0) goto L_0x154f
            goto L_0x1576
        L_0x154f:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x156f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            int r11 = r11 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x156f
            int r7 = r1.messageLeft
            int r7 = r7 + r0
            r1.messageLeft = r7
            int r7 = r1.messageNameLeft
            int r7 = r7 + r0
            r1.messageNameLeft = r7
        L_0x156f:
            r7 = 0
            r1.drawCount = r7
            r1.drawMention = r7
            goto L_0x1669
        L_0x1576:
            if (r7 == 0) goto L_0x15dc
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r7)
            double r14 = (double) r9
            double r14 = java.lang.Math.ceil(r14)
            int r9 = (int) r14
            int r0 = java.lang.Math.max(r0, r9)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.countWidth
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_CENTER
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r0
            r29 = r7
            r31 = r9
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r7
            int r11 = r11 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x15c6
            int r0 = r39.getMeasuredWidth()
            int r7 = r1.countWidth
            int r0 = r0 - r7
            r7 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r9
            r1.countLeft = r0
            goto L_0x15d8
        L_0x15c6:
            r7 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.countLeft = r9
            int r7 = r1.messageLeft
            int r7 = r7 + r0
            r1.messageLeft = r7
            int r7 = r1.messageNameLeft
            int r7 = r7 + r0
            r1.messageNameLeft = r7
        L_0x15d8:
            r7 = 1
            r1.drawCount = r7
            goto L_0x15df
        L_0x15dc:
            r7 = 0
            r1.countWidth = r7
        L_0x15df:
            if (r13 == 0) goto L_0x1669
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1617
            r7 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r7.measureText(r13)
            double r14 = (double) r7
            double r14 = java.lang.Math.ceil(r14)
            int r7 = (int) r14
            int r0 = java.lang.Math.max(r0, r7)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r7 = r1.mentionWidth
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_CENTER
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r0
            r29 = r13
            r31 = r7
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)
            r1.mentionLayout = r0
            goto L_0x161f
        L_0x1617:
            r7 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.mentionWidth = r0
        L_0x161f:
            int r0 = r1.mentionWidth
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r7
            int r11 = r11 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x1648
            int r0 = r39.getMeasuredWidth()
            int r7 = r1.mentionWidth
            int r0 = r0 - r7
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            int r7 = r1.countWidth
            if (r7 == 0) goto L_0x1643
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r9
            goto L_0x1644
        L_0x1643:
            r7 = 0
        L_0x1644:
            int r0 = r0 - r7
            r1.mentionLeft = r0
            goto L_0x1666
        L_0x1648:
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x1658
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r9 = r9 + r12
            goto L_0x1659
        L_0x1658:
            r9 = 0
        L_0x1659:
            int r7 = r7 + r9
            r1.mentionLeft = r7
            int r7 = r1.messageLeft
            int r7 = r7 + r0
            r1.messageLeft = r7
            int r7 = r1.messageNameLeft
            int r7 = r7 + r0
            r1.messageNameLeft = r7
        L_0x1666:
            r7 = 1
            r1.drawMention = r7
        L_0x1669:
            if (r6 == 0) goto L_0x16b5
            if (r3 != 0) goto L_0x1670
            r12 = r19
            goto L_0x1671
        L_0x1670:
            r12 = r3
        L_0x1671:
            int r0 = r12.length()
            r3 = 150(0x96, float:2.1E-43)
            if (r0 <= r3) goto L_0x167e
            r6 = 0
            java.lang.CharSequence r12 = r12.subSequence(r6, r3)
        L_0x167e:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1686
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1688
        L_0x1686:
            if (r2 == 0) goto L_0x168d
        L_0x1688:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r12)
            goto L_0x1691
        L_0x168d:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r12)
        L_0x1691:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r3 = r3[r6]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r6, r7)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x16b5
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            if (r0 == 0) goto L_0x16b5
            r3 = r0
        L_0x16b5:
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = java.lang.Math.max(r0, r11)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x16c7
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x171e
        L_0x16c7:
            if (r2 == 0) goto L_0x171e
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x16d2
            int r7 = r1.currentDialogFolderDialogsCount
            r9 = 1
            if (r7 != r9) goto L_0x171e
        L_0x16d2:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1704 }
            if (r0 == 0) goto L_0x16e9
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1704 }
            if (r0 == 0) goto L_0x16e9
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1704 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1704 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider     // Catch:{ Exception -> 0x1704 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)     // Catch:{ Exception -> 0x1704 }
            if (r0 == 0) goto L_0x16e9
            r2 = r0
        L_0x16e9:
            android.text.TextPaint r29 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1704 }
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1704 }
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            android.text.TextUtils$TruncateAt r35 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1704 }
            r37 = 1
            r28 = r2
            r30 = r6
            r36 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r28, r29, r30, r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x1704 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1704 }
            goto L_0x1708
        L_0x1704:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1708:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            float r4 = (float) r4
            r0.setImageY(r4)
            goto L_0x1746
        L_0x171e:
            r7 = 0
            r1.messageNameLayout = r7
            if (r0 != 0) goto L_0x1731
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1728
            goto L_0x1731
        L_0x1728:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1746
        L_0x1731:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r7 = 1101529088(0x41a80000, float:21.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1746:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x17fb }
            if (r0 != 0) goto L_0x174e
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17fb }
            if (r4 == 0) goto L_0x1760
        L_0x174e:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x17fb }
            if (r4 == 0) goto L_0x1760
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x17fb }
            r7 = 1
            if (r4 <= r7) goto L_0x1760
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x17fb }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x17fb }
            r10 = r0[r3]     // Catch:{ Exception -> 0x17fb }
            r3 = r2
            r2 = 0
            goto L_0x1777
        L_0x1760:
            if (r0 != 0) goto L_0x1766
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17fb }
            if (r0 == 0) goto L_0x1768
        L_0x1766:
            if (r2 == 0) goto L_0x1777
        L_0x1768:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x17fb }
            int r0 = r6 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x17fb }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x17fb }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r10, r0, r4)     // Catch:{ Exception -> 0x17fb }
        L_0x1777:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x17fb }
            if (r0 != 0) goto L_0x17b2
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x17fb }
            if (r0 == 0) goto L_0x1780
            goto L_0x17b2
        L_0x1780:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x17fb }
            if (r0 == 0) goto L_0x179a
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x17fb }
            int r0 = r0 + r8
            int r6 = r6 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x17fb }
            if (r0 == 0) goto L_0x179a
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x17fb }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x17fb }
            int r8 = r8 + r4
            int r0 = r0 - r8
            r1.messageLeft = r0     // Catch:{ Exception -> 0x17fb }
        L_0x179a:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x17fb }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x17fb }
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r0
            r29 = r3
            r30 = r10
            r31 = r6
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x17fb }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x17fb }
            goto L_0x17e5
        L_0x17b2:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x17fb }
            if (r0 == 0) goto L_0x17bf
            if (r2 == 0) goto L_0x17bf
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x17fb }
            int r6 = r6 + r0
        L_0x17bf:
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x17fb }
            r32 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x17fb }
            float r0 = (float) r0     // Catch:{ Exception -> 0x17fb }
            r34 = 0
            android.text.TextUtils$TruncateAt r35 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x17fb }
            if (r2 == 0) goto L_0x17d3
            r37 = 1
            goto L_0x17d5
        L_0x17d3:
            r37 = 2
        L_0x17d5:
            r28 = r3
            r29 = r10
            r30 = r6
            r33 = r0
            r36 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r28, r29, r30, r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x17fb }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x17fb }
        L_0x17e5:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x17fb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilers     // Catch:{ Exception -> 0x17fb }
            r0.addAll(r2)     // Catch:{ Exception -> 0x17fb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilers     // Catch:{ Exception -> 0x17fb }
            r0.clear()     // Catch:{ Exception -> 0x17fb }
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x17fb }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x17fb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r1.spoilers     // Catch:{ Exception -> 0x17fb }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r3)     // Catch:{ Exception -> 0x17fb }
            goto L_0x1802
        L_0x17fb:
            r0 = move-exception
            r2 = 0
            r1.messageLayout = r2
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1802:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x193e
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x18c7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18c7
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x1854
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x1854
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x1854
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
            goto L_0x18af
        L_0x1854:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x187e
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
            goto L_0x18af
        L_0x187e:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x18af
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
            if (r4 != r9) goto L_0x18a1
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x18a3
        L_0x18a1:
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x18a3:
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
        L_0x18af:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x18c7
            double r4 = (double) r5
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x18c7
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r4
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x18c7:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1908
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1908
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x18d8:
            if (r2 >= r0) goto L_0x18fe
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x18fd
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
            goto L_0x18d8
        L_0x18fd:
            r3 = 0
        L_0x18fe:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x1908
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x1908:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x19c8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19c8
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x19c8
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x19c8
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x19c8
        L_0x193e:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x198d
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x198d
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r5
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1972
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r5
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1972
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1972:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x197e
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x197e
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x198d
        L_0x197e:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x198d:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x19b0
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19b0
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x199a:
            if (r3 >= r0) goto L_0x19a9
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x199a
        L_0x19a9:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x19b0:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x19c8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x19c8
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x19c8:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a0c
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1a0c
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1a08 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1a08 }
            r4 = r24
            r2 = 1
            if (r4 < r0) goto L_0x19df
            int r4 = r0 + -1
        L_0x19df:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1a08 }
            float r0 = r0.getPrimaryHorizontal(r4)     // Catch:{ Exception -> 0x1a08 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1a08 }
            int r4 = r4 + r2
            float r2 = r3.getPrimaryHorizontal(r4)     // Catch:{ Exception -> 0x1a08 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1a08 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1a08 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1a08 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1a08 }
            if (r0 == 0) goto L_0x19ff
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1a08 }
            int r0 = r0 + r2
        L_0x19ff:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1a08 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1a08 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1a08 }
            goto L_0x1a0c
        L_0x1a08:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1a0c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1a5a
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1a5a
            if (r21 < 0) goto L_0x1a31
            int r9 = r21 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r9 >= r0) goto L_0x1a31
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r21
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r9)
            goto L_0x1a3f
        L_0x1a31:
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x1a3f:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1a4b
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1a5a
        L_0x1a4b:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1a5a:
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
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a5a  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x0aa8  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0d4f  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0e07  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0e3c  */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x0e74  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0e91  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0f5a  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x11f4  */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x1208  */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x121d  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x1237  */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x1263  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x12c8  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x1321  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x1338  */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x1354  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x137e  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x13ae  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x13c4  */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x13ee  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x1410  */
    /* JADX WARNING: Removed duplicated region for block: B:767:? A[RETURN, SYNTHETIC] */
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
            r2 = 2131628285(0x7f0e10fd, float:1.8883858E38)
            java.lang.String r7 = "UnhideFromTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628285(0x7f0e10fd, float:1.8883858E38)
            goto L_0x0119
        L_0x00dc:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            java.lang.String r7 = "HideOnTop"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            goto L_0x0119
        L_0x00f9:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131627394(0x7f0e0d82, float:1.8882051E38)
            java.lang.String r7 = "PsaHide"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627394(0x7f0e0d82, float:1.8882051E38)
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
            r2 = 2131628091(0x7f0e103b, float:1.8883465E38)
            java.lang.String r7 = "SwipeUnmute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628091(0x7f0e103b, float:1.8883465E38)
            goto L_0x0119
        L_0x014e:
            r2 = 2131628079(0x7f0e102f, float:1.888344E38)
            java.lang.String r7 = "SwipeMute"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131628079(0x7f0e102f, float:1.888344E38)
            goto L_0x0119
        L_0x015f:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r14) goto L_0x0180
            r2 = 2131628076(0x7f0e102c, float:1.8883434E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            java.lang.String r2 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131628076(0x7f0e102c, float:1.8883434E38)
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
            r2 = 2131628078(0x7f0e102e, float:1.8883439E38)
            java.lang.String r7 = "SwipeMarkAsUnread"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131628078(0x7f0e102e, float:1.8883439E38)
            goto L_0x0119
        L_0x01a3:
            r2 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r7 = "SwipeMarkAsRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131628077(0x7f0e102d, float:1.8883436E38)
            goto L_0x0119
        L_0x01b5:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01e5
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01d3
            r2 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.String r7 = "SwipeUnpin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131628092(0x7f0e103c, float:1.8883467E38)
            goto L_0x0119
        L_0x01d3:
            r2 = 2131628080(0x7f0e1030, float:1.8883443E38)
            java.lang.String r7 = "SwipePin"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131628080(0x7f0e1030, float:1.8883443E38)
            goto L_0x0119
        L_0x01e5:
            r2 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r7 = "Archive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624318(0x7f0e017e, float:1.8875812E38)
            goto L_0x0119
        L_0x01f7:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r8.resourcesProvider
            int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2 = 2131628276(0x7f0e10f4, float:1.888384E38)
            java.lang.String r7 = "Unarchive"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131628276(0x7f0e10f4, float:1.888384E38)
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
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x0aa8
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
            goto L_0x0e01
        L_0x0aa8:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0ad7
            boolean r5 = r8.drawMention
            if (r5 != 0) goto L_0x0ad7
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0ab7
            goto L_0x0ad7
        L_0x0ab7:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0e01
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
            goto L_0x0e01
        L_0x0ad7:
            if (r0 != 0) goto L_0x0ae4
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0ae0
            goto L_0x0ae4
        L_0x0ae0:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0d4b
        L_0x0ae4:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0af1
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x0af1
            float r5 = r8.countChangeProgress
            float r5 = r14 - r5
            goto L_0x0af3
        L_0x0af1:
            float r5 = r8.countChangeProgress
        L_0x0af3:
            android.text.StaticLayout r6 = r8.countOldLayout
            if (r6 == 0) goto L_0x0CLASSNAME
            if (r0 != 0) goto L_0x0afb
            goto L_0x0CLASSNAME
        L_0x0afb:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b07
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b04
            goto L_0x0b07
        L_0x0b04:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b09
        L_0x0b07:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b09:
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
            if (r7 <= 0) goto L_0x0b28
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0b29
        L_0x0b28:
            r7 = r6
        L_0x0b29:
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
            if (r1 > 0) goto L_0x0b79
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r6)
            float r3 = r3 * r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 + r1
            goto L_0x0b8f
        L_0x0b79:
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
        L_0x0b8f:
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
            if (r0 == 0) goto L_0x0bc8
            r31.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r15, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r31.restore()
        L_0x0bc8:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r4 = r3 * r7
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0be7
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0bec
        L_0x0be7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0bec:
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
        L_0x0CLASSNAME:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0CLASSNAME
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0c1a
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0c1a:
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
            if (r1 == 0) goto L_0x0CLASSNAME
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r23
            int r3 = (int) r3
            r1.setAlpha(r3)
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0c4b
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x0c4f
        L_0x0c4b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0c4f:
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
        L_0x0CLASSNAME:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r31.restore()
            goto L_0x0ae0
        L_0x0CLASSNAME:
            if (r0 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.text.StaticLayout r6 = r8.countLayout
        L_0x0CLASSNAME:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0CLASSNAME
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0c7f
            goto L_0x0CLASSNAME
        L_0x0c7f:
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
            if (r3 == 0) goto L_0x0d1c
            boolean r3 = r8.drawPin
            if (r3 == 0) goto L_0x0d0a
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
        L_0x0d0a:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r5, r5, r1, r3)
        L_0x0d1c:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r7 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r7, r3, r0)
            if (r6 == 0) goto L_0x0d42
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
        L_0x0d42:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0d4b
            r31.restore()
        L_0x0d4b:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0e01
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
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
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
        L_0x0e01:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0e25
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
        L_0x0e25:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0e33
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0e33
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0e38
        L_0x0e33:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0e38:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0e70
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0e70
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
        L_0x0e70:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0e77
            r31.restore()
        L_0x0e77:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0var_
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0f5a
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0f5a
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x0f5a
            boolean r0 = r30.isOnline()
            if (r0 != 0) goto L_0x0e9e
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0var_
        L_0x0e9e:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0eb0
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0ead
            goto L_0x0eb0
        L_0x0ead:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0eb2
        L_0x0eb0:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0eb2:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0ed5
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0ece
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0ecc
            goto L_0x0ece
        L_0x0ecc:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ece:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0eec
        L_0x0ed5:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0ee6
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0ee4
            goto L_0x0ee6
        L_0x0ee4:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ee6:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0eec:
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
            if (r0 == 0) goto L_0x0f3e
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
        L_0x0f3e:
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
        L_0x0var_:
            r2 = 0
            goto L_0x1231
        L_0x0f5a:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0var_
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0var_
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0var_
            r6 = 1
            goto L_0x0var_
        L_0x0var_:
            r6 = 0
        L_0x0var_:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x0var_
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0var_
        L_0x0var_:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0var_
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0var_
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r5 = r2 - r0
            goto L_0x0f8b
        L_0x0var_:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0f8b:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0f9d
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0f9a
            goto L_0x0f9d
        L_0x0f9a:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0f9f
        L_0x0f9d:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f9f:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0fc2
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0fbb
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0fb9
            goto L_0x0fbb
        L_0x0fb9:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fbb:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0fd9
        L_0x0fc2:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0fd3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0fd1
            goto L_0x0fd3
        L_0x0fd1:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0fd3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0fd9:
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
            if (r3 != 0) goto L_0x104e
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
        L_0x1046:
            float r11 = r11 * r14
            float r6 = r6 - r11
            r11 = r6
        L_0x104a:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x1147
        L_0x104e:
            r6 = 1
            if (r3 != r6) goto L_0x1075
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
        L_0x1070:
            float r14 = r14 * r15
            float r11 = r11 + r14
            goto L_0x1147
        L_0x1075:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 2
            if (r3 != r11) goto L_0x1098
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
            goto L_0x1046
        L_0x1098:
            r6 = 3
            if (r3 != r6) goto L_0x10b9
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
            goto L_0x1070
        L_0x10b9:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 4
            if (r3 != r11) goto L_0x10db
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
            goto L_0x1046
        L_0x10db:
            r6 = 5
            if (r3 != r6) goto L_0x10ff
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
            goto L_0x1070
        L_0x10ff:
            r6 = 1065353216(0x3var_, float:1.0)
            r11 = 6
            if (r3 != r11) goto L_0x1126
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
            goto L_0x104a
        L_0x1126:
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
            goto L_0x1070
        L_0x1147:
            float r14 = r8.chatCallProgress
            int r14 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r14 < 0) goto L_0x1151
            int r14 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r14 >= 0) goto L_0x115d
        L_0x1151:
            r31.save()
            float r6 = r8.chatCallProgress
            float r14 = r6 * r5
            float r6 = r6 * r5
            r9.scale(r14, r6, r2, r0)
        L_0x115d:
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
            if (r0 < 0) goto L_0x11e1
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x11e4
        L_0x11e1:
            r31.restore()
        L_0x11e4:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x1204
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x1204
            r2 = 0
            r8.progressStage = r2
        L_0x1204:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x121d
            float r0 = r8.chatCallProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x121b
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x121b
            r8.chatCallProgress = r2
        L_0x121b:
            r2 = 0
            goto L_0x122f
        L_0x121d:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x122f
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x122f
            r8.chatCallProgress = r2
        L_0x122f:
            r20 = 1
        L_0x1231:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x123a
            r31.restore()
        L_0x123a:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x125f
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x125f
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x125f
            r31.save()
            int r0 = r30.getMeasuredWidth()
            int r1 = r30.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r31.restore()
        L_0x125f:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x12c0
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x1283
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1273
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1273
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1283
        L_0x1273:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x127c
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x127c
            goto L_0x1283
        L_0x127c:
            r0 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1284
        L_0x1283:
            r1 = 0
        L_0x1284:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x12a5
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
            goto L_0x12c0
        L_0x12a5:
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
            goto L_0x12c1
        L_0x12c0:
            r7 = 1
        L_0x12c1:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x1311
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x12d2
            r31.restore()
            goto L_0x1311
        L_0x12d2:
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
        L_0x1311:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x131f
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x131d
            goto L_0x131f
        L_0x131d:
            r1 = 0
            goto L_0x134e
        L_0x131f:
            if (r0 == 0) goto L_0x1338
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x131d
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1336
            r8.reorderIconProgress = r1
        L_0x1336:
            r1 = 0
            goto L_0x134c
        L_0x1338:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x134e
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x134c
            r8.reorderIconProgress = r1
        L_0x134c:
            r6 = 1
            goto L_0x1350
        L_0x134e:
            r6 = r20
        L_0x1350:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x137e
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x13aa
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1367
            r8.archiveBackgroundProgress = r1
        L_0x1367:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x13a9
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x13a9
        L_0x137e:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13aa
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1393
            r8.archiveBackgroundProgress = r1
        L_0x1393:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x13a9
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x13a9:
            r6 = 1
        L_0x13aa:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x13c0
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x13bf
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x13bf:
            r6 = 1
        L_0x13c0:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x13ee
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x13da
            float r2 = (float) r12
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x13da
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x13da:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x140e
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x140d
            r8.currentRevealProgress = r1
            goto L_0x140d
        L_0x13ee:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x13fa
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x13fa:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x140e
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x140d
            r8.currentRevealProgress = r1
        L_0x140d:
            r6 = 1
        L_0x140e:
            if (r6 == 0) goto L_0x1413
            r30.invalidate()
        L_0x1413:
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
