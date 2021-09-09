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
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
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
        this(dialogsActivity, context, z, z2, UserConfig.selectedAccount);
    }

    public DialogCell(DialogsActivity dialogsActivity, Context context, boolean z, boolean z2, int i) {
        super(context);
        this.thumbImage = new ImageReceiver(this);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.countChangeProgress = 1.0f;
        this.rect = new RectF();
        this.lastStatusDrawableParams = -1;
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
    }

    private void checkOnline() {
        TLRPC$User user2;
        if (!(this.user == null || (user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user.id))) == null)) {
            this.user = user2;
        }
        this.onlineProgress = isOnline() ? 1.0f : 0.0f;
    }

    private boolean isOnline() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null && !tLRPC$User.self) {
            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
            if (tLRPC$UserStatus != null && tLRPC$UserStatus.expires <= 0 && MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.user.id))) {
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
            if (DialogObject.isSecretDialogId(tLRPC$Dialog.id)) {
                TLRPC$EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (tLRPC$Dialog.id >> 32)));
                tLRPC$User = encryptedChat2 != null ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id)) : null;
            } else {
                int i2 = (int) tLRPC$Dialog.id;
                if (i2 > 0) {
                    tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                } else {
                    tLRPC$Chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                    tLRPC$User = null;
                }
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
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived")), length, length2, 33);
            }
            if (spannableStringBuilder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v179, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v171, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v172, resolved type: java.lang.String} */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0613, code lost:
        if (r8.post_messages == false) goto L_0x05ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x061f, code lost:
        if (r8.kicked != false) goto L_0x05ef;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x18bd  */
    /* JADX WARNING: Removed duplicated region for block: B:1082:0x195c A[Catch:{ Exception -> 0x1989 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1083:0x195f A[Catch:{ Exception -> 0x1989 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1086:0x1979 A[Catch:{ Exception -> 0x1989 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1094:0x1995  */
    /* JADX WARNING: Removed duplicated region for block: B:1106:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0c2b A[SYNTHETIC, Splitter:B:542:0x0c2b] */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:637:0x0e3f  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0e46  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e4b  */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0ee7  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0eef  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0eff  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x0fd8  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x1043  */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x1097  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x109d  */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x10b1  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x110d  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x114e  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x115b  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x116b  */
    /* JADX WARNING: Removed duplicated region for block: B:788:0x1183  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x1192  */
    /* JADX WARNING: Removed duplicated region for block: B:801:0x11cb  */
    /* JADX WARNING: Removed duplicated region for block: B:805:0x11f4  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1278  */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x128e  */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x1302 A[Catch:{ Exception -> 0x1321 }] */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1305 A[Catch:{ Exception -> 0x1321 }] */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x132f  */
    /* JADX WARNING: Removed duplicated region for block: B:862:0x13da  */
    /* JADX WARNING: Removed duplicated region for block: B:869:0x148d  */
    /* JADX WARNING: Removed duplicated region for block: B:875:0x14b2  */
    /* JADX WARNING: Removed duplicated region for block: B:880:0x14e0  */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x15fa  */
    /* JADX WARNING: Removed duplicated region for block: B:956:0x16be  */
    /* JADX WARNING: Removed duplicated region for block: B:957:0x16c7  */
    /* JADX WARNING: Removed duplicated region for block: B:967:0x16ed A[Catch:{ Exception -> 0x177d }] */
    /* JADX WARNING: Removed duplicated region for block: B:968:0x16f6 A[ADDED_TO_REGION, Catch:{ Exception -> 0x177d }] */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1712 A[Catch:{ Exception -> 0x177d }] */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x1767 A[Catch:{ Exception -> 0x177d }] */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x176a A[Catch:{ Exception -> 0x177d }] */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x1785  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r38 = this;
            r1 = r38
            boolean r0 = r1.useForceThreeLines
            r2 = 1099431936(0x41880000, float:17.0)
            r3 = 18
            r4 = 1098907648(0x41800000, float:16.0)
            r5 = 1
            r6 = 0
            if (r0 != 0) goto L_0x005b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0013
            goto L_0x005b
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
            java.lang.String r8 = "chats_message"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r0.linkColor = r8
            r7.setColor(r8)
            r1.paintIndex = r6
            r0 = 19
            r7 = 19
            goto L_0x00a4
        L_0x005b:
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
            java.lang.String r8 = "chats_message_threeLines"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r0.linkColor = r8
            r7.setColor(r8)
            r1.paintIndex = r5
            r7 = 18
        L_0x00a4:
            r1.currentDialogFolderDialogsCount = r6
            boolean r0 = r1.isDialogCell
            if (r0 == 0) goto L_0x00b7
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r9 = r1.currentDialogId
            java.lang.CharSequence r0 = r0.getPrintingString(r9, r6, r5)
            goto L_0x00b8
        L_0x00b7:
            r0 = 0
        L_0x00b8:
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
            if (r10 != 0) goto L_0x00dc
            boolean r10 = r1.useMeForMyMessages
            if (r10 != 0) goto L_0x00dc
            r10 = 1
            goto L_0x00dd
        L_0x00dc:
            r10 = 0
        L_0x00dd:
            r11 = -1
            r1.printingStringType = r11
            int r12 = android.os.Build.VERSION.SDK_INT
            if (r12 < r3) goto L_0x00f6
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
            r1.lastMessageString = r15
            org.telegram.ui.Cells.DialogCell$CustomDialog r15 = r1.customDialog
            r16 = 1117782016(0x42a00000, float:80.0)
            r17 = 1118044160(0x42a40000, float:82.0)
            r11 = 32
            r20 = 1101004800(0x41a00000, float:20.0)
            r2 = 33
            r21 = 1102053376(0x41b00000, float:22.0)
            r3 = 150(0x96, float:2.1E-43)
            r23 = 1117257728(0x42980000, float:76.0)
            r24 = 1099956224(0x41900000, float:18.0)
            r25 = 1117519872(0x429CLASSNAME, float:78.0)
            r8 = 2
            java.lang.String r4 = ""
            if (r15 == 0) goto L_0x035a
            int r0 = r15.type
            if (r0 != r8) goto L_0x01b3
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0178
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x013d
            goto L_0x0178
        L_0x013d:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x015e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x015e:
            int r0 = r38.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0178:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0199
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0199:
            int r0 = r38.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x01b3:
            boolean r10 = r15.verified
            r1.drawVerified = r10
            boolean r10 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r10 == 0) goto L_0x0258
            if (r0 != r5) goto L_0x0258
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0211
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c8
            goto L_0x0211
        L_0x01c8:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01f0
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01e5
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01e7
        L_0x01e5:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01e7:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x01f0:
            int r0 = r38.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0200
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0202
        L_0x0200:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0202:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0211:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0238
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x022e
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0230
        L_0x022e:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0230:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0238:
            int r0 = r38.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0248
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x024a
        L_0x0248:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x024a:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0258:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0273
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0261
            goto L_0x0273
        L_0x0261:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x026c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x026c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0273:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x027e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x027e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
        L_0x0284:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r10 = r0.type
            if (r10 != r5) goto L_0x030a
            r0 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r10 = r1.customDialog
            boolean r12 = r10.isMedia
            if (r12 == 0) goto L_0x02be
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
            java.lang.String r13 = "chats_attachMessage"
            r12.<init>(r13)
            int r13 = r10.length()
            r10.setSpan(r12, r6, r13, r2)
            goto L_0x02f6
        L_0x02be:
            java.lang.String r2 = r10.message
            int r10 = r2.length()
            if (r10 <= r3) goto L_0x02ca
            java.lang.String r2 = r2.substring(r6, r3)
        L_0x02ca:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x02e8
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x02d3
            goto L_0x02e8
        L_0x02d3:
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r12 = 10
            java.lang.String r2 = r2.replace(r12, r11)
            r10[r6] = r2
            r10[r5] = r0
            java.lang.String r2 = java.lang.String.format(r13, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r2)
            goto L_0x02f6
        L_0x02e8:
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r10[r6] = r2
            r10[r5] = r0
            java.lang.String r2 = java.lang.String.format(r13, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x02f6:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r2 = r2[r12]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r10, r2, r12, r6)
            r10 = 0
            goto L_0x0318
        L_0x030a:
            java.lang.String r2 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0316
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r0[r9]
        L_0x0316:
            r0 = 0
            r10 = 1
        L_0x0318:
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            int r13 = r13.unread_count
            if (r13 == 0) goto L_0x0338
            r1.drawCount = r5
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r14[r6] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r14)
            goto L_0x033b
        L_0x0338:
            r1.drawCount = r6
            r13 = 0
        L_0x033b:
            org.telegram.ui.Cells.DialogCell$CustomDialog r14 = r1.customDialog
            boolean r15 = r14.sent
            if (r15 == 0) goto L_0x0346
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            goto L_0x034a
        L_0x0346:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
        L_0x034a:
            r1.drawClock = r6
            r1.drawError = r6
            java.lang.String r14 = r14.name
            r3 = r2
            r8 = r13
            r11 = -1
            r15 = 1
            r2 = r0
            r13 = r12
            r0 = r14
            r14 = 0
            goto L_0x110b
        L_0x035a:
            boolean r15 = r1.useForceThreeLines
            if (r15 != 0) goto L_0x0375
            boolean r15 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r15 == 0) goto L_0x0363
            goto L_0x0375
        L_0x0363:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x036e
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r15
            goto L_0x0386
        L_0x036e:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r15
            goto L_0x0386
        L_0x0375:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0380
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r15
            goto L_0x0386
        L_0x0380:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r15
        L_0x0386:
            org.telegram.tgnet.TLRPC$EncryptedChat r15 = r1.encryptedChat
            if (r15 == 0) goto L_0x0415
            int r15 = r1.currentDialogFolderId
            if (r15 != 0) goto L_0x05be
            r1.drawNameLock = r5
            boolean r15 = r1.useForceThreeLines
            if (r15 != 0) goto L_0x03d7
            boolean r15 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r15 == 0) goto L_0x0399
            goto L_0x03d7
        L_0x0399:
            r15 = 1099169792(0x41840000, float:16.5)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLockTop = r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x03bb
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 + r16
            r1.nameLeft = r15
            goto L_0x05be
        L_0x03bb:
            int r15 = r38.getMeasuredWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r15 = r15 - r16
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 - r16
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r15
            goto L_0x05be
        L_0x03d7:
            r15 = 1095237632(0x41480000, float:12.5)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLockTop = r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x03f9
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 + r16
            r1.nameLeft = r15
            goto L_0x05be
        L_0x03f9:
            int r15 = r38.getMeasuredWidth()
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r15 = r15 - r16
            android.graphics.drawable.Drawable r16 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r16 = r16.getIntrinsicWidth()
            int r15 = r15 - r16
            r1.nameLockLeft = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r15
            goto L_0x05be
        L_0x0415:
            int r15 = r1.currentDialogFolderId
            if (r15 != 0) goto L_0x05be
            org.telegram.tgnet.TLRPC$Chat r15 = r1.chat
            if (r15 == 0) goto L_0x0518
            boolean r11 = r15.scam
            if (r11 == 0) goto L_0x0429
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r11.checkText()
            goto L_0x0439
        L_0x0429:
            boolean r11 = r15.fake
            if (r11 == 0) goto L_0x0435
            r1.drawScam = r8
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r11.checkText()
            goto L_0x0439
        L_0x0435:
            boolean r11 = r15.verified
            r1.drawVerified = r11
        L_0x0439:
            boolean r11 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r11 == 0) goto L_0x05be
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x04af
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x0446
            goto L_0x04af
        L_0x0446:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            int r15 = r11.id
            if (r15 < 0) goto L_0x0464
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x0459
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x0459
            goto L_0x0464
        L_0x0459:
            r1.drawNameGroup = r5
            r11 = 1099694080(0x418CLASSNAME, float:17.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
            goto L_0x046e
        L_0x0464:
            r1.drawNameBroadcast = r5
            r11 = 1099169792(0x41840000, float:16.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
        L_0x046e:
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x048e
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r15 = r1.drawNameGroup
            if (r15 == 0) goto L_0x0483
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0485
        L_0x0483:
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0485:
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 + r15
            r1.nameLeft = r11
            goto L_0x05be
        L_0x048e:
            int r11 = r38.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r11 = r11 - r15
            boolean r15 = r1.drawNameGroup
            if (r15 == 0) goto L_0x049e
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04a0
        L_0x049e:
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04a0:
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 - r15
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r11
            goto L_0x05be
        L_0x04af:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            int r15 = r11.id
            if (r15 < 0) goto L_0x04cd
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x04c2
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = r11.megagroup
            if (r11 != 0) goto L_0x04c2
            goto L_0x04cd
        L_0x04c2:
            r1.drawNameGroup = r5
            r11 = 1096286208(0x41580000, float:13.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
            goto L_0x04d7
        L_0x04cd:
            r1.drawNameBroadcast = r5
            r11 = 1095237632(0x41480000, float:12.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
        L_0x04d7:
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x04f7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            boolean r15 = r1.drawNameGroup
            if (r15 == 0) goto L_0x04ec
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04ee
        L_0x04ec:
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04ee:
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 + r15
            r1.nameLeft = r11
            goto L_0x05be
        L_0x04f7:
            int r11 = r38.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r11 = r11 - r15
            boolean r15 = r1.drawNameGroup
            if (r15 == 0) goto L_0x0507
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0509
        L_0x0507:
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0509:
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 - r15
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r11
            goto L_0x05be
        L_0x0518:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            if (r11 == 0) goto L_0x05be
            boolean r15 = r11.scam
            if (r15 == 0) goto L_0x0528
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r11.checkText()
            goto L_0x0538
        L_0x0528:
            boolean r15 = r11.fake
            if (r15 == 0) goto L_0x0534
            r1.drawScam = r8
            org.telegram.ui.Components.ScamDrawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r11.checkText()
            goto L_0x0538
        L_0x0534:
            boolean r11 = r11.verified
            r1.drawVerified = r11
        L_0x0538:
            boolean r11 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r11 == 0) goto L_0x05be
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = r11.bot
            if (r11 == 0) goto L_0x05be
            r1.drawNameBot = r5
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0586
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x054d
            goto L_0x0586
        L_0x054d:
            r11 = 1099169792(0x41840000, float:16.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x056d
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 + r15
            r1.nameLeft = r11
            goto L_0x05be
        L_0x056d:
            int r11 = r38.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r11 = r11 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 - r15
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r11
            goto L_0x05be
        L_0x0586:
            r11 = 1095237632(0x41480000, float:12.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLockTop = r11
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x05a6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 + r15
            r1.nameLeft = r11
            goto L_0x05be
        L_0x05a6:
            int r11 = r38.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r11 = r11 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r15 = r15.getIntrinsicWidth()
            int r11 = r11 - r15
            r1.nameLockLeft = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r11
        L_0x05be:
            int r11 = r1.lastMessageDate
            if (r11 != 0) goto L_0x05ca
            org.telegram.messenger.MessageObject r15 = r1.message
            if (r15 == 0) goto L_0x05ca
            org.telegram.tgnet.TLRPC$Message r11 = r15.messageOwner
            int r11 = r11.date
        L_0x05ca:
            boolean r15 = r1.isDialogCell
            if (r15 == 0) goto L_0x0625
            int r15 = r1.currentAccount
            org.telegram.messenger.MediaDataController r15 = org.telegram.messenger.MediaDataController.getInstance(r15)
            r17 = r9
            long r8 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r15.getDraft(r8, r6)
            r1.draftMessage = r8
            if (r8 == 0) goto L_0x05fb
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x05f1
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.reply_to_msg_id
            if (r8 == 0) goto L_0x05ef
            goto L_0x05f1
        L_0x05ef:
            r8 = 0
            goto L_0x0622
        L_0x05f1:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.date
            if (r11 <= r8) goto L_0x05fb
            int r8 = r1.unreadCount
            if (r8 != 0) goto L_0x05ef
        L_0x05fb:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0615
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r9 = r8.megagroup
            if (r9 != 0) goto L_0x0615
            boolean r9 = r8.creator
            if (r9 != 0) goto L_0x0615
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r8 = r8.admin_rights
            if (r8 == 0) goto L_0x05ef
            boolean r8 = r8.post_messages
            if (r8 == 0) goto L_0x05ef
        L_0x0615:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x062a
            boolean r9 = r8.left
            if (r9 != 0) goto L_0x05ef
            boolean r8 = r8.kicked
            if (r8 == 0) goto L_0x062a
            goto L_0x05ef
        L_0x0622:
            r1.draftMessage = r8
            goto L_0x062a
        L_0x0625:
            r17 = r9
            r8 = 0
            r1.draftMessage = r8
        L_0x062a:
            if (r0 == 0) goto L_0x06bd
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r8, r6)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0652
            int r2 = r2.getIntrinsicWidth()
            r8 = 1077936128(0x40400000, float:3.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r8
            goto L_0x0653
        L_0x0652:
            r2 = 0
        L_0x0653:
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>()
            int r9 = r1.printingStringType
            r11 = 5
            if (r9 != r11) goto L_0x0668
            java.lang.String r9 = r0.toString()
            java.lang.String r11 = "**oo**"
            int r11 = r9.indexOf(r11)
            goto L_0x0669
        L_0x0668:
            r11 = -1
        L_0x0669:
            if (r11 <= 0) goto L_0x0692
            java.lang.String[] r2 = new java.lang.String[r5]
            java.lang.String r9 = "..."
            r2[r6] = r9
            java.lang.String[] r9 = new java.lang.String[r5]
            r9[r6] = r4
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r2, r9)
            android.text.SpannableStringBuilder r0 = r8.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r2 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r9 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r9 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r9)
            int r9 = r9.getIntrinsicWidth()
            r2.<init>(r9)
            int r9 = r11 + 6
            r0.setSpan(r2, r11, r9, r6)
            goto L_0x06b2
        L_0x0692:
            java.lang.String r9 = " "
            android.text.SpannableStringBuilder r9 = r8.append(r9)
            java.lang.String[] r12 = new java.lang.String[r5]
            java.lang.String r13 = "..."
            r12[r6] = r13
            java.lang.String[] r13 = new java.lang.String[r5]
            r13[r6] = r4
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r12, r13)
            android.text.SpannableStringBuilder r0 = r9.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r9 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r9.<init>(r2)
            r0.setSpan(r9, r6, r5, r6)
        L_0x06b2:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 0
            r2 = 2
            r3 = 1
            goto L_0x0760
        L_0x06bd:
            r8 = 0
            r1.lastPrintString = r8
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0748
            r0 = 2131625260(0x7f0e052c, float:1.8877723E38)
            java.lang.String r8 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x06f9
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x06f3
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x06e0
            goto L_0x06f3
        L_0x06e0:
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r9 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r11 = "chats_draft"
            r9.<init>(r11)
            int r11 = r0.length()
            r8.setSpan(r9, r6, r11, r2)
            goto L_0x06f4
        L_0x06f3:
            r8 = r4
        L_0x06f4:
            r9 = r17
            r2 = 2
            r3 = 1
            goto L_0x075f
        L_0x06f9:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            int r9 = r8.length()
            if (r9 <= r3) goto L_0x0707
            java.lang.String r8 = r8.substring(r6, r3)
        L_0x0707:
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r9 = 32
            r12 = 10
            java.lang.String r8 = r8.replace(r12, r9)
            r11[r6] = r8
            r11[r5] = r0
            java.lang.String r8 = java.lang.String.format(r13, r11)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r8)
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x0735
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 != 0) goto L_0x0735
            org.telegram.ui.Components.ForegroundColorSpanThemable r9 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r11 = "chats_draft"
            r9.<init>(r11)
            int r11 = r0.length()
            int r11 = r11 + r5
            r8.setSpan(r9, r6, r11, r2)
        L_0x0735:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r2 = r2[r9]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r2, r9, r6)
            goto L_0x06f4
        L_0x0748:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x0763
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625820(0x7f0e075c, float:1.8878859E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x075b:
            r0 = 0
            r2 = 2
            r3 = 1
            r6 = 1
        L_0x075f:
            r11 = -1
        L_0x0760:
            r12 = 0
            goto L_0x0efb
        L_0x0763:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x07fb
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x07d7
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r9 = r2[r8]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x077f
            r0 = 2131625355(0x7f0e058b, float:1.8877916E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x075b
        L_0x077f:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0797
            r0 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r2[r6] = r8
            java.lang.String r8 = "AwaitingEncryption"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r0, r2)
            goto L_0x075b
        L_0x0797:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x07a5
            r0 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x075b
        L_0x07a5:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x07d5
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x07cb
            r0 = 2131625344(0x7f0e0580, float:1.8877893E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r2[r6] = r8
            java.lang.String r8 = "EncryptedChatStartedOutgoing"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r0, r2)
            goto L_0x075b
        L_0x07cb:
            r0 = 2131625343(0x7f0e057f, float:1.8877891E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x075b
        L_0x07d5:
            r8 = r4
            goto L_0x075b
        L_0x07d7:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x07f6
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x07f6
            r0 = 2131627446(0x7f0e0db6, float:1.8882157E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r9 = r17
            r0 = 0
            r2 = 2
            r3 = 0
            r6 = 1
            r10 = 0
            goto L_0x075f
        L_0x07f6:
            r8 = r4
            r9 = r17
            goto L_0x075b
        L_0x07fb:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r8 = r1.message
            int r8 = r8.getFromChatId()
            if (r8 <= 0) goto L_0x081b
            int r9 = r1.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r8 = r9.getUser(r8)
            r9 = 0
            goto L_0x082c
        L_0x081b:
            int r9 = r1.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            int r8 = -r8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r8 = r9.getChat(r8)
            r9 = r8
            r8 = 0
        L_0x082c:
            int r11 = r1.dialogsType
            r15 = 3
            if (r11 != r15) goto L_0x084c
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 == 0) goto L_0x084c
            r0 = 2131627446(0x7f0e0db6, float:1.8882157E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r8 = r0
            r9 = r17
            r0 = 1
            r2 = 2
            r3 = 0
            r10 = 0
        L_0x0849:
            r11 = 0
            goto L_0x0eeb
        L_0x084c:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0864
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 != 0) goto L_0x0864
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0864
            java.lang.CharSequence r0 = r38.formatArchivedDialogNames()
            r8 = r0
            r9 = r17
            r0 = 0
        L_0x0860:
            r2 = 2
        L_0x0861:
            r3 = 0
            r6 = 1
            goto L_0x0849
        L_0x0864:
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r11 == 0) goto L_0x0892
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0885
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x0882
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0885
        L_0x0882:
            r0 = r4
            r10 = 0
            goto L_0x0889
        L_0x0885:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0889:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r9 = r2[r8]
            r8 = r0
            r0 = 1
            goto L_0x0860
        L_0x0892:
            boolean r11 = android.text.TextUtils.isEmpty(r0)
            if (r11 == 0) goto L_0x0992
            int r11 = r1.currentDialogFolderId
            if (r11 != 0) goto L_0x0992
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r1.encryptedChat
            if (r11 != 0) goto L_0x0992
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.needDrawBluredPreview()
            if (r11 != 0) goto L_0x0992
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isPhoto()
            if (r11 != 0) goto L_0x08c0
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isNewGif()
            if (r11 != 0) goto L_0x08c0
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVideo()
            if (r11 == 0) goto L_0x0992
        L_0x08c0:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isWebpage()
            if (r11 == 0) goto L_0x08d3
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            org.telegram.tgnet.TLRPC$WebPage r11 = r11.webpage
            java.lang.String r11 = r11.type
            goto L_0x08d4
        L_0x08d3:
            r11 = 0
        L_0x08d4:
            java.lang.String r15 = "app"
            boolean r15 = r15.equals(r11)
            if (r15 != 0) goto L_0x0992
            java.lang.String r15 = "profile"
            boolean r15 = r15.equals(r11)
            if (r15 != 0) goto L_0x0992
            java.lang.String r15 = "article"
            boolean r15 = r15.equals(r11)
            if (r15 != 0) goto L_0x0992
            if (r11 == 0) goto L_0x08f6
            java.lang.String r15 = "telegram_"
            boolean r11 = r11.startsWith(r15)
            if (r11 != 0) goto L_0x0992
        L_0x08f6:
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            r15 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r11 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r15)
            org.telegram.messenger.MessageObject r15 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r15 = r15.photoThumbs
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r15, r2)
            if (r11 != r2) goto L_0x090f
            r2 = 0
        L_0x090f:
            if (r11 == 0) goto L_0x0992
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isVideo()
            r1.drawPlay = r15
            java.lang.String r15 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.mediaExists
            if (r3 != 0) goto L_0x095e
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r3 = r3.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r3 != 0) goto L_0x095e
            int r3 = r1.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            boolean r3 = r3.isLoadingFile(r15)
            if (r3 == 0) goto L_0x0940
            goto L_0x095e
        L_0x0940:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage
            r28 = 0
            r29 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForObject(r11, r3)
            r32 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r34 = 0
            java.lang.String r31 = "20_20"
            r27 = r2
            r33 = r3
            r27.setImage((org.telegram.messenger.ImageLocation) r28, (java.lang.String) r29, (org.telegram.messenger.ImageLocation) r30, (java.lang.String) r31, (android.graphics.drawable.Drawable) r32, (java.lang.Object) r33, (int) r34)
            goto L_0x0990
        L_0x095e:
            org.telegram.messenger.MessageObject r3 = r1.message
            int r6 = r3.type
            if (r6 != r5) goto L_0x096d
            if (r2 == 0) goto L_0x0969
            int r6 = r2.size
            goto L_0x096a
        L_0x0969:
            r6 = 0
        L_0x096a:
            r32 = r6
            goto L_0x096f
        L_0x096d:
            r32 = 0
        L_0x096f:
            org.telegram.messenger.ImageReceiver r6 = r1.thumbImage
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForObject(r2, r3)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForObject(r11, r2)
            r33 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r35 = 0
            java.lang.String r29 = "20_20"
            java.lang.String r31 = "20_20"
            r27 = r6
            r34 = r2
            r27.setImage(r28, r29, r30, r31, r32, r33, r34, r35)
        L_0x0990:
            r2 = 0
            goto L_0x0993
        L_0x0992:
            r2 = 1
        L_0x0993:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0ca0
            int r6 = r3.id
            if (r6 <= 0) goto L_0x0ca0
            if (r9 != 0) goto L_0x0ca0
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x09ab
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isMegagroup(r3)
            if (r3 == 0) goto L_0x0ca0
        L_0x09ab:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x09bd
            r3 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            goto L_0x0a0e
        L_0x09bd:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x09cc
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            if (r3 == 0) goto L_0x09cc
            java.lang.String r3 = r3.from_name
            if (r3 == 0) goto L_0x09cc
            goto L_0x0a0e
        L_0x09cc:
            if (r8 == 0) goto L_0x0a01
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x09e2
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09d7
            goto L_0x09e2
        L_0x09d7:
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r8)
            java.lang.String r6 = "\n"
            java.lang.String r3 = r3.replace(r6, r4)
            goto L_0x0a0e
        L_0x09e2:
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r8)
            if (r3 == 0) goto L_0x09f2
            r3 = 2131625807(0x7f0e074f, float:1.8878832E38)
            java.lang.String r6 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            goto L_0x0a0e
        L_0x09f2:
            java.lang.String r3 = r8.first_name
            java.lang.String r6 = r8.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r6)
            java.lang.String r6 = "\n"
            java.lang.String r3 = r3.replace(r6, r4)
            goto L_0x0a0e
        L_0x0a01:
            if (r9 == 0) goto L_0x0a0c
            java.lang.String r3 = r9.title
            java.lang.String r6 = "\n"
            java.lang.String r3 = r3.replace(r6, r4)
            goto L_0x0a0e
        L_0x0a0c:
            java.lang.String r3 = "DELETED"
        L_0x0a0e:
            boolean r6 = android.text.TextUtils.isEmpty(r0)
            if (r6 != 0) goto L_0x0a29
            r6 = 2
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r6 = 0
            r2[r6] = r0
            r2[r5] = r3
            java.lang.String r0 = java.lang.String.format(r13, r2)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x0a24:
            r2 = r0
            r9 = r17
            goto L_0x0CLASSNAME
        L_0x0a29:
            r6 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r8 = r0.caption
            if (r8 == 0) goto L_0x0a9b
            java.lang.String r0 = r8.toString()
            int r8 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x0a40
            java.lang.String r0 = r0.substring(r6, r9)
        L_0x0a40:
            if (r2 != 0) goto L_0x0a45
            r2 = r4
        L_0x0a43:
            r6 = 2
            goto L_0x0a74
        L_0x0a45:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0a50
            java.lang.String r2 = "📹 "
            goto L_0x0a43
        L_0x0a50:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x0a5b
            java.lang.String r2 = "🎤 "
            goto L_0x0a43
        L_0x0a5b:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x0a66
            java.lang.String r2 = "🎧 "
            goto L_0x0a43
        L_0x0a66:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x0a71
            java.lang.String r2 = "🖼 "
            goto L_0x0a43
        L_0x0a71:
            java.lang.String r2 = "📎 "
            goto L_0x0a43
        L_0x0a74:
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            r2 = 32
            r9 = 10
            java.lang.String r0 = r0.replace(r9, r2)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r2 = 0
            r8[r2] = r0
            r8[r5] = r3
            java.lang.String r0 = java.lang.String.format(r13, r8)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0a24
        L_0x0a9b:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0b88
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0b88
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0add
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            r0 = 18
            if (r12 < r0) goto L_0x0acd
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r6 = 0
            r0[r6] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b0a
        L_0x0acd:
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r6] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b0a
        L_0x0add:
            r6 = 0
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0b04
            r8 = 18
            if (r12 < r8) goto L_0x0af5
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r6] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b0a
        L_0x0af5:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r6] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0b0a
        L_0x0b04:
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r6 == 0) goto L_0x0b0f
            java.lang.String r0 = r2.title
        L_0x0b0a:
            r2 = 32
            r6 = 10
            goto L_0x0b52
        L_0x0b0f:
            int r2 = r0.type
            r6 = 14
            if (r2 != r6) goto L_0x0b4b
            r2 = 18
            if (r12 < r2) goto L_0x0b32
            r2 = 2
            java.lang.Object[] r6 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r8 = 0
            r6[r8] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r6[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r6)
            goto L_0x0b0a
        L_0x0b32:
            r2 = 2
            r8 = 0
            java.lang.Object[] r6 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r6[r8] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r6[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r6)
            goto L_0x0b0a
        L_0x0b4b:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            goto L_0x0b0a
        L_0x0b52:
            java.lang.String r0 = r0.replace(r6, r2)
            r2 = 2
            java.lang.Object[] r6 = new java.lang.Object[r2]
            r2 = 0
            r6[r2] = r0
            r6[r5] = r3
            java.lang.String r0 = java.lang.String.format(r13, r6)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0b82 }
            java.lang.String r6 = "chats_attachMessage"
            r0.<init>(r6)     // Catch:{ Exception -> 0x0b82 }
            if (r14 == 0) goto L_0x0b76
            int r6 = r3.length()     // Catch:{ Exception -> 0x0b82 }
            r8 = 2
            int r6 = r6 + r8
            goto L_0x0b77
        L_0x0b76:
            r6 = 0
        L_0x0b77:
            int r8 = r2.length()     // Catch:{ Exception -> 0x0b82 }
            r11 = 33
            r2.setSpan(r0, r6, r8, r11)     // Catch:{ Exception -> 0x0b82 }
            goto L_0x0CLASSNAME
        L_0x0b82:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0CLASSNAME
        L_0x0b88:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0CLASSNAME
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0be6
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b9d
            r2 = r0
        L_0x0b9d:
            int r0 = r38.getMeasuredWidth()
            r6 = 1121058816(0x42d20000, float:105.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            if (r14 == 0) goto L_0x0bca
            boolean r6 = android.text.TextUtils.isEmpty(r3)
            if (r6 != 0) goto L_0x0bbe
            float r0 = (float) r0
            java.lang.String r6 = r3.toString()
            r8 = r17
            float r6 = r8.measureText(r6)
            float r0 = r0 - r6
            int r0 = (int) r0
            goto L_0x0bc0
        L_0x0bbe:
            r8 = r17
        L_0x0bc0:
            float r0 = (float) r0
            java.lang.String r6 = ": "
            float r6 = r8.measureText(r6)
            float r0 = r0 - r6
            int r0 = (int) r0
            goto L_0x0bcc
        L_0x0bca:
            r8 = r17
        L_0x0bcc:
            if (r0 <= 0) goto L_0x0be4
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.String r6 = (java.lang.String) r6
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r6, r0, r8, r11)
            java.lang.String r2 = r0.toString()
            goto L_0x0CLASSNAME
        L_0x0be4:
            r9 = 0
            goto L_0x0CLASSNAME
        L_0x0be6:
            r8 = r17
            r9 = 0
            int r0 = r2.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r0 <= r6) goto L_0x0bf5
            java.lang.String r2 = r2.substring(r9, r6)
        L_0x0bf5:
            r6 = 32
            r11 = 10
            java.lang.String r0 = r2.replace(r11, r6)
            java.lang.String r2 = r0.trim()
        L_0x0CLASSNAME:
            r6 = 2
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r9] = r2
            r0[r5] = r3
            java.lang.String r0 = java.lang.String.format(r13, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r8 = r17
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x0CLASSNAME:
            r2 = r0
            r9 = r8
        L_0x0CLASSNAME:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0c2b
        L_0x0CLASSNAME:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0CLASSNAME
        L_0x0c2b:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0CLASSNAME }
            java.lang.String r6 = "chats_nameMessage"
            r0.<init>(r6)     // Catch:{ Exception -> 0x0CLASSNAME }
            int r6 = r3.length()     // Catch:{ Exception -> 0x0CLASSNAME }
            int r6 = r6 + r5
            r8 = 33
            r11 = 0
            r2.setSpan(r0, r11, r6, r8)     // Catch:{ Exception -> 0x0c3f }
            r0 = r6
            goto L_0x0c4a
        L_0x0c3f:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r6 = 0
        L_0x0CLASSNAME:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0c4a
        L_0x0CLASSNAME:
            r0 = 0
            r6 = 0
        L_0x0c4a:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r8 = r8[r11]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r12 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r8, r11, r12)
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r8)
            if (r8 == 0) goto L_0x0CLASSNAME
            r2 = r8
        L_0x0CLASSNAME:
            boolean r8 = r1.hasMessageThumb
            if (r8 == 0) goto L_0x0CLASSNAME
            boolean r8 = r2 instanceof android.text.SpannableStringBuilder
            if (r8 != 0) goto L_0x0c7e
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r2)
            r2 = r8
        L_0x0c7e:
            r8 = r2
            android.text.SpannableStringBuilder r8 = (android.text.SpannableStringBuilder) r8
            java.lang.String r11 = " "
            r8.insert(r6, r11)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r11 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r12 = r7 + 6
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.<init>(r12)
            int r12 = r6 + 1
            r13 = 33
            r8.setSpan(r11, r6, r12, r13)
        L_0x0CLASSNAME:
            r11 = r0
            r8 = r2
            r0 = 0
            r2 = 2
            r6 = 1
            goto L_0x0eeb
        L_0x0ca0:
            r8 = r17
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0cac
        L_0x0ca8:
            r9 = r8
            r2 = 2
            goto L_0x0e47
        L_0x0cac:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0cca
            org.telegram.tgnet.TLRPC$Photo r6 = r3.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x0cca
            int r6 = r3.ttl_seconds
            if (r6 == 0) goto L_0x0cca
            r0 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0ca8
        L_0x0cca:
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x0ce2
            org.telegram.tgnet.TLRPC$Document r6 = r3.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x0ce2
            int r6 = r3.ttl_seconds
            if (r6 == 0) goto L_0x0ce2
            r0 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0ca8
        L_0x0ce2:
            java.lang.CharSequence r6 = r0.caption
            if (r6 == 0) goto L_0x0d8a
            if (r2 != 0) goto L_0x0cea
            r0 = r4
            goto L_0x0d16
        L_0x0cea:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0cf3
            java.lang.String r0 = "📹 "
            goto L_0x0d16
        L_0x0cf3:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0cfe
            java.lang.String r0 = "🎤 "
            goto L_0x0d16
        L_0x0cfe:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0d09
            java.lang.String r0 = "🎧 "
            goto L_0x0d16
        L_0x0d09:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0d14
            java.lang.String r0 = "🖼 "
            goto L_0x0d16
        L_0x0d14:
            java.lang.String r0 = "📎 "
        L_0x0d16:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0d75
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d75
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r38.getMeasuredWidth()
            r6 = 1122893824(0x42ee0000, float:119.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            if (r14 == 0) goto L_0x0d4d
            r6 = 0
            boolean r9 = android.text.TextUtils.isEmpty(r6)
            if (r9 == 0) goto L_0x0d4c
            float r3 = (float) r3
            java.lang.String r9 = ": "
            float r9 = r8.measureText(r9)
            float r3 = r3 - r9
            int r3 = (int) r3
            goto L_0x0d4d
        L_0x0d4c:
            throw r6
        L_0x0d4d:
            if (r3 <= 0) goto L_0x0d64
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.String r6 = (java.lang.String) r6
            r9 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r6, r3, r8, r9)
            java.lang.String r2 = r2.toString()
        L_0x0d64:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0ca8
        L_0x0d75:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0ca8
        L_0x0d8a:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0da8
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "📊 "
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Poll r2 = r3.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0da5:
            r2 = 2
            goto L_0x0e31
        L_0x0da8:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0dc8
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
            goto L_0x0da5
        L_0x0dc8:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r2 == 0) goto L_0x0dcf
            java.lang.String r0 = r3.title
            goto L_0x0da5
        L_0x0dcf:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0dee
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r6 = 0
            r3[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r3[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0e31
        L_0x0dee:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0e26
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e26
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r38.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.String r6 = (java.lang.String) r6
            r9 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r3, r8, r9)
            java.lang.String r0 = r0.toString()
            goto L_0x0e2a
        L_0x0e26:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0e2a:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0e31:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            if (r6 == 0) goto L_0x0e46
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0e46
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r9 = r3[r6]
            goto L_0x0e47
        L_0x0e46:
            r9 = r8
        L_0x0e47:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0ee7
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0e87
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0e87
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r38.getMeasuredWidth()
            int r6 = r7 + 95
            int r6 = r6 + 6
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r8 = 0
            java.lang.Object r6 = r6.get(r8)
            java.lang.String r6 = (java.lang.String) r6
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r3, r9, r11)
            java.lang.String r0 = r0.toString()
            goto L_0x0e98
        L_0x0e87:
            r8 = 0
            int r3 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r3 <= r6) goto L_0x0e94
            java.lang.CharSequence r0 = r0.subSequence(r8, r6)
        L_0x0e94:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e98:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0ea2
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0ea2:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r6 = " "
            r8 = 0
            r3.insert(r8, r6)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r6 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r11 = r7 + 6
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.<init>(r11)
            r11 = 33
            r3.setSpan(r6, r8, r5, r11)
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r6 = r6[r11]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r11 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r6, r12, r8)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.hasHighlightedWords()
            if (r6 == 0) goto L_0x0ee3
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r6)
            if (r3 == 0) goto L_0x0ee3
            r8 = r3
            goto L_0x0ee4
        L_0x0ee3:
            r8 = r0
        L_0x0ee4:
            r0 = 0
            goto L_0x0861
        L_0x0ee7:
            r8 = r0
            r0 = 1
            goto L_0x0861
        L_0x0eeb:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x0ef3
            java.lang.CharSequence r3 = r38.formatArchivedDialogNames()
        L_0x0ef3:
            r12 = r11
            r11 = -1
            r37 = r6
            r6 = r0
            r0 = r3
            r3 = r37
        L_0x0efb:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0var_
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0var_
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0var_:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0f1f
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0var_
        L_0x0f1f:
            r13 = r4
        L_0x0var_:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0var_
            r15 = 0
            r1.drawCheck1 = r15
            r1.drawCheck2 = r15
            r1.drawClock = r15
            r1.drawCount = r15
            r1.drawMention = r15
            r1.drawError = r15
            r2 = 0
            r10 = 0
            r14 = 0
            goto L_0x1035
        L_0x0var_:
            r15 = 0
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0var_
            int r2 = r1.unreadCount
            int r14 = r1.mentionCount
            int r17 = r2 + r14
            if (r17 <= 0) goto L_0x0var_
            if (r2 <= r14) goto L_0x0f5a
            r1.drawCount = r5
            r1.drawMention = r15
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r15[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r15)
            goto L_0x0var_
        L_0x0f5a:
            r1.drawCount = r15
            r1.drawMention = r5
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r15[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r15)
            r14 = r2
            r2 = 0
            goto L_0x0fc0
        L_0x0var_:
            r14 = 0
            r1.drawCount = r14
            r1.drawMention = r14
            r2 = 0
        L_0x0var_:
            r14 = 0
            goto L_0x0fc0
        L_0x0var_:
            r2 = 0
            boolean r15 = r1.clearingDialog
            if (r15 == 0) goto L_0x0var_
            r1.drawCount = r2
            r2 = 0
            r10 = 0
            r15 = 0
            goto L_0x0fb4
        L_0x0var_:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0fa8
            if (r2 != r5) goto L_0x0var_
            int r15 = r1.mentionCount
            if (r2 != r15) goto L_0x0var_
            if (r14 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r14 = r14.messageOwner
            boolean r14 = r14.mentioned
            if (r14 != 0) goto L_0x0fa8
        L_0x0var_:
            r1.drawCount = r5
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15 = 0
            r14[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            goto L_0x0fb4
        L_0x0fa8:
            r15 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0fb1
            r1.drawCount = r5
            r2 = r4
            goto L_0x0fb4
        L_0x0fb1:
            r1.drawCount = r15
            r2 = 0
        L_0x0fb4:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0fbd
            r1.drawMention = r5
            java.lang.String r14 = "@"
            goto L_0x0fc0
        L_0x0fbd:
            r1.drawMention = r15
            goto L_0x0var_
        L_0x0fc0:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x102c
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x102c
            if (r10 == 0) goto L_0x102c
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x102c
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0fe8
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r5
            r1.drawError = r10
            goto L_0x1035
        L_0x0fe8:
            r10 = 0
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isSendError()
            if (r15 == 0) goto L_0x0ffe
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r5
            r1.drawCount = r10
            r1.drawMention = r10
            goto L_0x1035
        L_0x0ffe:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x102a
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x101f
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x101d
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x101d
            goto L_0x101f
        L_0x101d:
            r10 = 0
            goto L_0x1020
        L_0x101f:
            r10 = 1
        L_0x1020:
            r1.drawCheck1 = r10
            r1.drawCheck2 = r5
            r10 = 0
            r1.drawClock = r10
            r1.drawError = r10
            goto L_0x1035
        L_0x102a:
            r10 = 0
            goto L_0x1035
        L_0x102c:
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r10
        L_0x1035:
            r1.promoDialog = r10
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r15 = r1.dialogsType
            r17 = r2
            if (r15 != 0) goto L_0x1097
            r15 = r3
            long r2 = r1.currentDialogId
            boolean r2 = r10.isPromoDialog(r2, r5)
            if (r2 == 0) goto L_0x1098
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r2 = r10.promoDialogType
            if (r2 != 0) goto L_0x105f
            r2 = 2131628080(0x7f0e1030, float:1.8883443E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x105d:
            r13 = r2
            goto L_0x1098
        L_0x105f:
            if (r2 != r5) goto L_0x1098
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r10.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x1087
            r2 = 2131627239(0x7f0e0ce7, float:1.8881737E38)
            java.lang.String r3 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x1087:
            java.lang.String r3 = r10.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x105d
            java.lang.String r3 = r10.promoPsaMessage
            r8 = 0
            r1.hasMessageThumb = r8
            r13 = r2
            r2 = r3
            goto L_0x1099
        L_0x1097:
            r15 = r3
        L_0x1098:
            r2 = r8
        L_0x1099:
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x10b1
            r3 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r8 = "ArchivedChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
        L_0x10a6:
            r10 = r6
            r6 = r12
            r8 = r17
            r37 = r2
            r2 = r0
            r0 = r3
            r3 = r37
            goto L_0x110b
        L_0x10b1:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x10b8
            java.lang.String r3 = r3.title
            goto L_0x10fb
        L_0x10b8:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x10fa
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r3)
            if (r3 == 0) goto L_0x10cc
            r3 = 2131627326(0x7f0e0d3e, float:1.8881913E38)
            java.lang.String r8 = "RepliesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x10fb
        L_0x10cc:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x10f3
            boolean r3 = r1.useMeForMyMessages
            if (r3 == 0) goto L_0x10e2
            r3 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r8 = "FromYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x10fb
        L_0x10e2:
            int r3 = r1.dialogsType
            r8 = 3
            if (r3 != r8) goto L_0x10e9
            r1.drawPinBackground = r5
        L_0x10e9:
            r3 = 2131627445(0x7f0e0db5, float:1.8882155E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x10fb
        L_0x10f3:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x10fb
        L_0x10fa:
            r3 = r4
        L_0x10fb:
            int r8 = r3.length()
            if (r8 != 0) goto L_0x10a6
            r3 = 2131625807(0x7f0e074f, float:1.8878832E38)
            java.lang.String r8 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x10a6
        L_0x110b:
            if (r15 == 0) goto L_0x114e
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r12 = r12.measureText(r13)
            r17 = r6
            double r5 = (double) r12
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            android.text.StaticLayout r6 = new android.text.StaticLayout
            android.text.TextPaint r29 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_NORMAL
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            r27 = r6
            r28 = r13
            r30 = r5
            r27.<init>(r28, r29, r30, r31, r32, r33, r34)
            r1.timeLayout = r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x1145
            int r6 = r38.getMeasuredWidth()
            r12 = 1097859072(0x41700000, float:15.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r6 = r6 - r12
            int r6 = r6 - r5
            r1.timeLeft = r6
            goto L_0x1157
        L_0x1145:
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.timeLeft = r6
            goto L_0x1157
        L_0x114e:
            r17 = r6
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r5 = 0
        L_0x1157:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x116b
            int r6 = r38.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r6 = r6 - r12
            r12 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r6 = r6 - r12
            int r6 = r6 - r5
            goto L_0x117f
        L_0x116b:
            int r6 = r38.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r6 = r6 - r12
            r12 = 1117388800(0x429a0000, float:77.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r6 = r6 - r12
            int r6 = r6 - r5
            int r12 = r1.nameLeft
            int r12 = r12 + r5
            r1.nameLeft = r12
        L_0x117f:
            boolean r12 = r1.drawNameLock
            if (r12 == 0) goto L_0x1192
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r13 = r13.getIntrinsicWidth()
        L_0x118f:
            int r12 = r12 + r13
            int r6 = r6 - r12
            goto L_0x11c5
        L_0x1192:
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x11a3
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r13 = r13.getIntrinsicWidth()
            goto L_0x118f
        L_0x11a3:
            boolean r12 = r1.drawNameBroadcast
            if (r12 == 0) goto L_0x11b4
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r13 = r13.getIntrinsicWidth()
            goto L_0x118f
        L_0x11b4:
            boolean r12 = r1.drawNameBot
            if (r12 == 0) goto L_0x11c5
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            goto L_0x118f
        L_0x11c5:
            boolean r12 = r1.drawClock
            r13 = 1084227584(0x40a00000, float:5.0)
            if (r12 == 0) goto L_0x11f4
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r12 = r12.getIntrinsicWidth()
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 + r18
            int r6 = r6 - r12
            boolean r18 = org.telegram.messenger.LocaleController.isRTL
            if (r18 != 0) goto L_0x11e3
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.clockDrawLeft = r5
            goto L_0x126a
        L_0x11e3:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r15 + r5
            r1.clockDrawLeft = r15
            int r5 = r1.nameLeft
            int r5 = r5 + r12
            r1.nameLeft = r5
            goto L_0x126a
        L_0x11f4:
            boolean r12 = r1.drawCheck2
            if (r12 == 0) goto L_0x126a
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r12 = r12.getIntrinsicWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 + r15
            int r6 = r6 - r12
            boolean r15 = r1.drawCheck1
            if (r15 == 0) goto L_0x1251
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            r22 = 1090519040(0x41000000, float:8.0)
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r15 = r15 - r22
            int r6 = r6 - r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x122a
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.halfCheckDrawLeft = r5
            r12 = 1085276160(0x40b00000, float:5.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 - r12
            r1.checkDrawLeft = r5
            goto L_0x126a
        L_0x122a:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r15 + r5
            r1.checkDrawLeft = r15
            r5 = 1085276160(0x40b00000, float:5.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = r15 + r5
            r1.halfCheckDrawLeft = r15
            int r5 = r1.nameLeft
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r13 = r13.getIntrinsicWidth()
            int r12 = r12 + r13
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r5 = r5 + r12
            r1.nameLeft = r5
            goto L_0x126a
        L_0x1251:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x125b
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.checkDrawLeft1 = r5
            goto L_0x126a
        L_0x125b:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r15 + r5
            r1.checkDrawLeft1 = r15
            int r5 = r1.nameLeft
            int r5 = r5 + r12
            r1.nameLeft = r5
        L_0x126a:
            boolean r5 = r1.dialogMuted
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r5 == 0) goto L_0x128e
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x128e
            int r5 = r1.drawScam
            if (r5 != 0) goto L_0x128e
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r13 = r13.getIntrinsicWidth()
            int r5 = r5 + r13
            int r6 = r6 - r5
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c9
            int r13 = r1.nameLeft
            int r13 = r13 + r5
            r1.nameLeft = r13
            goto L_0x12c9
        L_0x128e:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x12a8
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r13 = r13.getIntrinsicWidth()
            int r5 = r5 + r13
            int r6 = r6 - r5
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c9
            int r13 = r1.nameLeft
            int r13 = r13 + r5
            r1.nameLeft = r13
            goto L_0x12c9
        L_0x12a8:
            int r5 = r1.drawScam
            if (r5 == 0) goto L_0x12c9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r1.drawScam
            r15 = 1
            if (r13 != r15) goto L_0x12b8
            org.telegram.ui.Components.ScamDrawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x12ba
        L_0x12b8:
            org.telegram.ui.Components.ScamDrawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x12ba:
            int r13 = r13.getIntrinsicWidth()
            int r5 = r5 + r13
            int r6 = r6 - r5
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12c9
            int r13 = r1.nameLeft
            int r13 = r13 + r5
            r1.nameLeft = r13
        L_0x12c9:
            r5 = 1094713344(0x41400000, float:12.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = java.lang.Math.max(r13, r6)
            r13 = 32
            r15 = 10
            java.lang.String r0 = r0.replace(r15, r13)     // Catch:{ Exception -> 0x1321 }
            android.text.TextPaint[] r13 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1321 }
            int r15 = r1.paintIndex     // Catch:{ Exception -> 0x1321 }
            r13 = r13[r15]     // Catch:{ Exception -> 0x1321 }
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x1321 }
            int r15 = r6 - r15
            float r15 = (float) r15     // Catch:{ Exception -> 0x1321 }
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1321 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r13, r15, r12)     // Catch:{ Exception -> 0x1321 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x1321 }
            if (r12 == 0) goto L_0x1305
            boolean r12 = r12.hasHighlightedWords()     // Catch:{ Exception -> 0x1321 }
            if (r12 == 0) goto L_0x1305
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x1321 }
            java.util.ArrayList<java.lang.String> r12 = r12.highlightedWords     // Catch:{ Exception -> 0x1321 }
            java.lang.CharSequence r12 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r12)     // Catch:{ Exception -> 0x1321 }
            if (r12 == 0) goto L_0x1305
            r28 = r12
            goto L_0x1307
        L_0x1305:
            r28 = r0
        L_0x1307:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1321 }
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1321 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1321 }
            r29 = r12[r13]     // Catch:{ Exception -> 0x1321 }
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1321 }
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            r27 = r0
            r30 = r6
            r27.<init>(r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x1321 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x1321 }
            goto L_0x1325
        L_0x1321:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1325:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x13da
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x132f
            goto L_0x13da
        L_0x132f:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12 = 1106771968(0x41var_, float:31.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.messageNameTop = r12
            r12 = 1098907648(0x41800000, float:16.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.timeTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.errorTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.pinTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.countTop = r12
            r12 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.checkDrawTop = r13
            int r12 = r38.getMeasuredWidth()
            r13 = 1119748096(0x42be0000, float:95.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x1391
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            int r13 = r38.getMeasuredWidth()
            r15 = 1115684864(0x42800000, float:64.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 - r15
            int r15 = r7 + 11
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r13 - r15
            goto L_0x13a6
        L_0x1391:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r13
        L_0x13a6:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r13 = (float) r13
            r23 = r4
            float r4 = (float) r0
            r19 = 1113063424(0x42580000, float:54.0)
            r25 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r12 = (float) r12
            r26 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r11 = (float) r11
            r5.setImageCoords(r13, r4, r12, r11)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r15
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            float r12 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r4.setImageCoords(r5, r11, r13, r12)
            goto L_0x1486
        L_0x13da:
            r23 = r4
            r26 = r11
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
            int r4 = r38.getMeasuredWidth()
            r5 = 1119485952(0x42ba0000, float:93.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r12 = r4 - r5
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1442
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r38.getMeasuredWidth()
            r5 = 1115947008(0x42840000, float:66.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            r5 = 1106771968(0x41var_, float:31.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r4 - r5
            goto L_0x1457
        L_0x1442:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1116340224(0x428a0000, float:69.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r4
        L_0x1457:
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r4 = (float) r4
            float r13 = (float) r0
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r19 = 1113587712(0x42600000, float:56.0)
            r25 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r12 = (float) r12
            r11.setImageCoords(r4, r13, r15, r12)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r5
            r11 = 1106771968(0x41var_, float:31.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r13 = (float) r13
            r4.setImageCoords(r5, r11, r12, r13)
        L_0x1486:
            r4 = r0
            r12 = r25
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x14ae
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x14a6
            int r0 = r38.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x14ae
        L_0x14a6:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x14ae:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x14e0
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r12 = r12 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x14cb
            int r0 = r38.getMeasuredWidth()
            r5 = 1107820544(0x42080000, float:34.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.errorLeft = r0
            goto L_0x14dd
        L_0x14cb:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x14dd:
            r11 = r6
            goto L_0x15f8
        L_0x14e0:
            if (r8 != 0) goto L_0x150b
            if (r14 == 0) goto L_0x14e5
            goto L_0x150b
        L_0x14e5:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1505
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            int r12 = r12 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x1505
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x1505:
            r5 = 0
            r1.drawCount = r5
            r1.drawMention = r5
            goto L_0x14dd
        L_0x150b:
            if (r8 == 0) goto L_0x156e
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r8)
            r11 = r6
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r29 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_CENTER
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            r27 = r0
            r28 = r8
            r30 = r5
            r27.<init>(r28, r29, r30, r31, r32, r33, r34)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r12 = r12 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x155a
            int r0 = r38.getMeasuredWidth()
            int r5 = r1.countWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r5
            r1.countLeft = r0
            goto L_0x156a
        L_0x155a:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x156a:
            r5 = 1
            r1.drawCount = r5
            goto L_0x1572
        L_0x156e:
            r11 = r6
            r5 = 0
            r1.countWidth = r5
        L_0x1572:
            if (r14 == 0) goto L_0x15f8
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x15aa
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r14)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r29 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.mentionWidth
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_CENTER
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            r27 = r0
            r28 = r14
            r30 = r5
            r27.<init>(r28, r29, r30, r31, r32, r33, r34)
            r1.mentionLayout = r0
            goto L_0x15b2
        L_0x15aa:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.mentionWidth = r0
        L_0x15b2:
            int r0 = r1.mentionWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r12 = r12 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x15d9
            int r0 = r38.getMeasuredWidth()
            int r5 = r1.mentionWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r5
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x15d4
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x15d5
        L_0x15d4:
            r5 = 0
        L_0x15d5:
            int r0 = r0 - r5
            r1.mentionLeft = r0
            goto L_0x15f5
        L_0x15d9:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x15e7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r6 = r6 + r8
            goto L_0x15e8
        L_0x15e7:
            r6 = 0
        L_0x15e8:
            int r5 = r5 + r6
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x15f5:
            r5 = 1
            r1.drawMention = r5
        L_0x15f8:
            if (r10 == 0) goto L_0x164c
            if (r3 != 0) goto L_0x15fe
            r3 = r23
        L_0x15fe:
            java.lang.String r0 = r3.toString()
            int r3 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x160f
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r5)
        L_0x160f:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x1617
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1619
        L_0x1617:
            if (r2 == 0) goto L_0x1622
        L_0x1619:
            r3 = 32
            r5 = 10
            java.lang.String r0 = r0.replace(r5, r3)
            goto L_0x162a
        L_0x1622:
            java.lang.String r3 = "\n\n"
            java.lang.String r5 = "\n"
            java.lang.String r0 = r0.replace(r3, r5)
        L_0x162a:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r5 = 1099431936(0x41880000, float:17.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r5, r6)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x164c
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r0)
            if (r0 == 0) goto L_0x164c
            r3 = r0
        L_0x164c:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = java.lang.Math.max(r0, r12)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x165e
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x16b4
        L_0x165e:
            if (r2 == 0) goto L_0x16b4
            int r6 = r1.currentDialogFolderId
            if (r6 == 0) goto L_0x1669
            int r6 = r1.currentDialogFolderDialogsCount
            r8 = 1
            if (r6 != r8) goto L_0x16b4
        L_0x1669:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1699 }
            if (r0 == 0) goto L_0x167e
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1699 }
            if (r0 == 0) goto L_0x167e
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1699 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1699 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x1699 }
            if (r0 == 0) goto L_0x167e
            r2 = r0
        L_0x167e:
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1699 }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1699 }
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            android.text.TextUtils$TruncateAt r34 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1699 }
            r36 = 1
            r27 = r2
            r29 = r5
            r35 = r5
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x1699 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1699 }
            goto L_0x169d
        L_0x1699:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x169d:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r6 = 1109393408(0x42200000, float:40.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            float r4 = (float) r4
            r0.setImageY(r4)
            r6 = 0
            goto L_0x16dc
        L_0x16b4:
            r6 = 0
            r1.messageNameLayout = r6
            if (r0 != 0) goto L_0x16c7
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x16be
            goto L_0x16c7
        L_0x16be:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x16dc
        L_0x16c7:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r8 = 1101529088(0x41a80000, float:21.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 + r8
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x16dc:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x177d }
            if (r0 != 0) goto L_0x16e4
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x177d }
            if (r4 == 0) goto L_0x16f6
        L_0x16e4:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x177d }
            if (r4 == 0) goto L_0x16f6
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x177d }
            r8 = 1
            if (r4 <= r8) goto L_0x16f6
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x177d }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x177d }
            r9 = r0[r3]     // Catch:{ Exception -> 0x177d }
            r3 = r2
            r8 = r6
            goto L_0x170e
        L_0x16f6:
            if (r0 != 0) goto L_0x16fc
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x177d }
            if (r0 == 0) goto L_0x16fe
        L_0x16fc:
            if (r2 == 0) goto L_0x170d
        L_0x16fe:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x177d }
            int r0 = r5 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x177d }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x177d }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r9, r0, r4)     // Catch:{ Exception -> 0x177d }
        L_0x170d:
            r8 = r2
        L_0x170e:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x177d }
            if (r0 != 0) goto L_0x1749
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x177d }
            if (r0 == 0) goto L_0x1717
            goto L_0x1749
        L_0x1717:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x177d }
            if (r0 == 0) goto L_0x1731
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x177d }
            int r0 = r0 + r7
            int r5 = r5 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x177d }
            if (r0 == 0) goto L_0x1731
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x177d }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x177d }
            int r7 = r7 + r4
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x177d }
        L_0x1731:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x177d }
            android.text.Layout$Alignment r31 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x177d }
            r32 = 1065353216(0x3var_, float:1.0)
            r33 = 0
            r34 = 0
            r27 = r0
            r28 = r3
            r29 = r9
            r30 = r5
            r27.<init>(r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x177d }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x177d }
            goto L_0x1781
        L_0x1749:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x177d }
            if (r0 == 0) goto L_0x1756
            if (r8 == 0) goto L_0x1756
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x177d }
            int r5 = r5 + r0
        L_0x1756:
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x177d }
            r31 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x177d }
            float r0 = (float) r0     // Catch:{ Exception -> 0x177d }
            r33 = 0
            android.text.TextUtils$TruncateAt r34 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x177d }
            if (r8 == 0) goto L_0x176a
            r36 = 1
            goto L_0x176c
        L_0x176a:
            r36 = 2
        L_0x176c:
            r27 = r3
            r28 = r9
            r29 = r5
            r32 = r0
            r35 = r5
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x177d }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x177d }
            goto L_0x1781
        L_0x177d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1781:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x18bd
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1846
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1846
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x17d3
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x17d3
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x17d3
            int r4 = r1.nameLeft
            double r6 = (double) r4
            double r8 = (double) r11
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r8
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r4 = r4.getIntrinsicWidth()
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            int r4 = (int) r6
            r1.nameMuteLeft = r4
            goto L_0x182e
        L_0x17d3:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x17fd
            int r4 = r1.nameLeft
            double r6 = (double) r4
            double r8 = (double) r11
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r8
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            int r4 = (int) r6
            r1.nameMuteLeft = r4
            goto L_0x182e
        L_0x17fd:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x182e
            int r4 = r1.nameLeft
            double r6 = (double) r4
            double r8 = (double) r11
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r8
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            int r4 = r1.drawScam
            r8 = 1
            if (r4 != r8) goto L_0x1820
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1822
        L_0x1820:
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1822:
            int r4 = r4.getIntrinsicWidth()
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r6 = r6 - r8
            int r4 = (int) r6
            r1.nameMuteLeft = r4
        L_0x182e:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1846
            double r6 = (double) r11
            int r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x1846
            int r0 = r1.nameLeft
            double r8 = (double) r0
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r2
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r6
            int r0 = (int) r8
            r1.nameLeft = r0
        L_0x1846:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1887
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1887
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x1857:
            if (r2 >= r0) goto L_0x187d
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r6 = 0
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L_0x187c
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineWidth(r2)
            double r6 = (double) r4
            double r6 = java.lang.Math.ceil(r6)
            double r8 = (double) r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r6
            int r4 = (int) r8
            int r3 = java.lang.Math.min(r3, r4)
            int r2 = r2 + 1
            goto L_0x1857
        L_0x187c:
            r3 = 0
        L_0x187d:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x1887
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x1887:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1947
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1947
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x1947
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r5
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x1947
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x1947
        L_0x18bd:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x190c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x190c
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r11
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x18f1
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r11
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x18f1
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x18f1:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x18fd
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x18fd
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x190c
        L_0x18fd:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x190c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x192f
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x192f
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1919:
            if (r3 >= r0) goto L_0x1928
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1919
        L_0x1928:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x192f:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1947
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1947
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1947:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x198d
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x198d
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1989 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1989 }
            r12 = r17
            r2 = 1
            if (r12 < r0) goto L_0x195f
            int r6 = r0 + -1
            goto L_0x1960
        L_0x195f:
            r6 = r12
        L_0x1960:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1989 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1989 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1989 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1989 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1989 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1989 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1989 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1989 }
            if (r0 == 0) goto L_0x1980
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1989 }
            int r0 = r0 + r2
        L_0x1980:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1989 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1989 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1989 }
            goto L_0x198d
        L_0x1989:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x198d:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x19cd
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x19cd
            if (r26 < 0) goto L_0x19a6
            r11 = r26
            float r0 = r0.getPrimaryHorizontal(r11)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            int r11 = r11 + r3
            float r2 = r2.getPrimaryHorizontal(r11)
            goto L_0x19b2
        L_0x19a6:
            r2 = 0
            r3 = 1
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getPrimaryHorizontal(r3)
        L_0x19b2:
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x19be
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x19cd
        L_0x19be:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x19cd:
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

    /* JADX WARNING: Removed duplicated region for block: B:127:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x018f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r0.customDialog
            r3 = 0
            r4 = 1
            r5 = 0
            if (r2 == 0) goto L_0x003d
            int r1 = r2.date
            r0.lastMessageDate = r1
            int r1 = r2.unread_count
            if (r1 == 0) goto L_0x0014
            goto L_0x0015
        L_0x0014:
            r4 = 0
        L_0x0015:
            r0.lastUnreadState = r4
            r0.unreadCount = r1
            boolean r1 = r2.pinned
            r0.drawPin = r1
            boolean r1 = r2.muted
            r0.dialogMuted = r1
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            int r4 = r2.id
            java.lang.String r2 = r2.name
            r1.setInfo(r4, r2, r3)
            org.telegram.messenger.ImageReceiver r5 = r0.avatarImage
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 0
            r10 = 0
            java.lang.String r7 = "50_50"
            r5.setImage(r6, r7, r8, r9, r10)
            org.telegram.messenger.ImageReceiver r1 = r0.thumbImage
            r1.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x04c6
        L_0x003d:
            int r2 = r0.unreadCount
            boolean r6 = r0.markUnread
            boolean r7 = r0.isDialogCell
            if (r7 == 0) goto L_0x0105
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r7 = r7.dialogs_dict
            long r8 = r0.currentDialogId
            java.lang.Object r7 = r7.get(r8)
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC$Dialog) r7
            if (r7 == 0) goto L_0x00fa
            if (r1 != 0) goto L_0x0107
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r9 = r7.id
            boolean r8 = r8.isClearingDialog(r9)
            r0.clearingDialog = r8
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r8.dialogMessage
            long r9 = r7.id
            java.lang.Object r8 = r8.get(r9)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r0.message = r8
            if (r8 == 0) goto L_0x0083
            boolean r8 = r8.isUnread()
            if (r8 == 0) goto L_0x0083
            r8 = 1
            goto L_0x0084
        L_0x0083:
            r8 = 0
        L_0x0084:
            r0.lastUnreadState = r8
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r8 == 0) goto L_0x0099
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r8)
            int r8 = r8.getArchiveUnreadCount()
            r0.unreadCount = r8
            r0.mentionCount = r5
            goto L_0x00a1
        L_0x0099:
            int r8 = r7.unread_count
            r0.unreadCount = r8
            int r8 = r7.unread_mentions_count
            r0.mentionCount = r8
        L_0x00a1:
            boolean r8 = r7.unread_mark
            r0.markUnread = r8
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x00ae
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.edit_date
            goto L_0x00af
        L_0x00ae:
            r8 = 0
        L_0x00af:
            r0.currentEditDate = r8
            int r8 = r7.last_message_date
            r0.lastMessageDate = r8
            int r8 = r0.dialogsType
            r9 = 7
            r10 = 8
            if (r8 == r9) goto L_0x00cd
            if (r8 != r10) goto L_0x00bf
            goto L_0x00cd
        L_0x00bf:
            int r8 = r0.currentDialogFolderId
            if (r8 != 0) goto L_0x00c9
            boolean r7 = r7.pinned
            if (r7 == 0) goto L_0x00c9
            r7 = 1
            goto L_0x00ca
        L_0x00c9:
            r7 = 0
        L_0x00ca:
            r0.drawPin = r7
            goto L_0x00ef
        L_0x00cd:
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.messenger.MessagesController$DialogFilter[] r8 = r8.selectedDialogFilter
            int r9 = r0.dialogsType
            if (r9 != r10) goto L_0x00db
            r9 = 1
            goto L_0x00dc
        L_0x00db:
            r9 = 0
        L_0x00dc:
            r8 = r8[r9]
            if (r8 == 0) goto L_0x00ec
            android.util.LongSparseArray<java.lang.Integer> r8 = r8.pinnedDialogs
            long r9 = r7.id
            int r7 = r8.indexOfKey(r9)
            if (r7 < 0) goto L_0x00ec
            r7 = 1
            goto L_0x00ed
        L_0x00ec:
            r7 = 0
        L_0x00ed:
            r0.drawPin = r7
        L_0x00ef:
            org.telegram.messenger.MessageObject r7 = r0.message
            if (r7 == 0) goto L_0x0107
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            int r7 = r7.send_state
            r0.lastSendState = r7
            goto L_0x0107
        L_0x00fa:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0107
        L_0x0105:
            r0.drawPin = r5
        L_0x0107:
            if (r1 == 0) goto L_0x0238
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            if (r7 == 0) goto L_0x0128
            r7 = r1 & 4
            if (r7 == 0) goto L_0x0128
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            int r8 = r8.id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
            r0.user = r7
            r18.invalidate()
        L_0x0128:
            boolean r7 = r0.isDialogCell
            if (r7 == 0) goto L_0x0150
            r7 = r1 & 64
            if (r7 == 0) goto L_0x0150
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r0.currentDialogId
            java.lang.CharSequence r7 = r7.getPrintingString(r8, r5, r4)
            java.lang.CharSequence r8 = r0.lastPrintString
            if (r8 == 0) goto L_0x0142
            if (r7 == 0) goto L_0x014e
        L_0x0142:
            if (r8 != 0) goto L_0x0146
            if (r7 != 0) goto L_0x014e
        L_0x0146:
            if (r8 == 0) goto L_0x0150
            boolean r7 = r8.equals(r7)
            if (r7 != 0) goto L_0x0150
        L_0x014e:
            r7 = 1
            goto L_0x0151
        L_0x0150:
            r7 = 0
        L_0x0151:
            if (r7 != 0) goto L_0x0164
            r8 = 32768(0x8000, float:4.5918E-41)
            r8 = r8 & r1
            if (r8 == 0) goto L_0x0164
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x0164
            java.lang.CharSequence r8 = r8.messageText
            java.lang.CharSequence r9 = r0.lastMessageString
            if (r8 == r9) goto L_0x0164
            r7 = 1
        L_0x0164:
            if (r7 != 0) goto L_0x0190
            r8 = r1 & 8192(0x2000, float:1.14794E-41)
            if (r8 == 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 == 0) goto L_0x0190
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            int r9 = r9.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r9)
            boolean r9 = r8.call_active
            if (r9 == 0) goto L_0x018a
            boolean r8 = r8.call_not_empty
            if (r8 == 0) goto L_0x018a
            r8 = 1
            goto L_0x018b
        L_0x018a:
            r8 = 0
        L_0x018b:
            boolean r9 = r0.hasCall
            if (r8 == r9) goto L_0x0190
            r7 = 1
        L_0x0190:
            if (r7 != 0) goto L_0x019b
            r8 = r1 & 2
            if (r8 == 0) goto L_0x019b
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 != 0) goto L_0x019b
            r7 = 1
        L_0x019b:
            if (r7 != 0) goto L_0x01a6
            r8 = r1 & 1
            if (r8 == 0) goto L_0x01a6
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 != 0) goto L_0x01a6
            r7 = 1
        L_0x01a6:
            if (r7 != 0) goto L_0x01b1
            r8 = r1 & 8
            if (r8 == 0) goto L_0x01b1
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 != 0) goto L_0x01b1
            r7 = 1
        L_0x01b1:
            if (r7 != 0) goto L_0x01bc
            r8 = r1 & 16
            if (r8 == 0) goto L_0x01bc
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 != 0) goto L_0x01bc
            r7 = 1
        L_0x01bc:
            if (r7 != 0) goto L_0x021d
            r8 = r1 & 256(0x100, float:3.59E-43)
            if (r8 == 0) goto L_0x021d
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x01d7
            boolean r9 = r0.lastUnreadState
            boolean r8 = r8.isUnread()
            if (r9 == r8) goto L_0x01d7
            org.telegram.messenger.MessageObject r7 = r0.message
            boolean r7 = r7.isUnread()
            r0.lastUnreadState = r7
            r7 = 1
        L_0x01d7:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x021d
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            long r9 = r0.currentDialogId
            java.lang.Object r8 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r9 == 0) goto L_0x01fb
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r9)
            int r9 = r9.getArchiveUnreadCount()
        L_0x01f9:
            r10 = 0
            goto L_0x0204
        L_0x01fb:
            if (r8 == 0) goto L_0x0202
            int r9 = r8.unread_count
            int r10 = r8.unread_mentions_count
            goto L_0x0204
        L_0x0202:
            r9 = 0
            goto L_0x01f9
        L_0x0204:
            if (r8 == 0) goto L_0x021d
            int r11 = r0.unreadCount
            if (r11 != r9) goto L_0x0214
            boolean r11 = r0.markUnread
            boolean r12 = r8.unread_mark
            if (r11 != r12) goto L_0x0214
            int r11 = r0.mentionCount
            if (r11 == r10) goto L_0x021d
        L_0x0214:
            r0.unreadCount = r9
            r0.mentionCount = r10
            boolean r7 = r8.unread_mark
            r0.markUnread = r7
            r7 = 1
        L_0x021d:
            if (r7 != 0) goto L_0x0232
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x0232
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x0232
            int r8 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r8 == r1) goto L_0x0232
            r0.lastSendState = r1
            r7 = 1
        L_0x0232:
            if (r7 != 0) goto L_0x0238
            r18.invalidate()
            return
        L_0x0238:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r7 = 0
            if (r1 == 0) goto L_0x0255
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r18.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x0253
            long r9 = r1.getDialogId()
            goto L_0x026e
        L_0x0253:
            r9 = r7
            goto L_0x026e
        L_0x0255:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x0269
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r9 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r9)
            if (r1 == 0) goto L_0x0269
            r1 = 1
            goto L_0x026a
        L_0x0269:
            r1 = 0
        L_0x026a:
            r0.dialogMuted = r1
            long r9 = r0.currentDialogId
        L_0x026e:
            int r1 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x030f
            int r1 = (int) r9
            r3 = 32
            long r7 = r9 >> r3
            int r3 = (int) r7
            if (r1 == 0) goto L_0x02c1
            if (r1 >= 0) goto L_0x02b0
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            r0.chat = r1
            boolean r3 = r0.isDialogCell
            if (r3 != 0) goto L_0x02e7
            if (r1 == 0) goto L_0x02e7
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02e7
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            int r3 = r3.channel_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            if (r1 == 0) goto L_0x02e7
            r0.chat = r1
            goto L_0x02e7
        L_0x02b0:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            r0.user = r1
            goto L_0x02e7
        L_0x02c1:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r3)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02e7
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r0.encryptedChat
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            r0.user = r1
        L_0x02e7:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x030f
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x030f
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x030f
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            r0.user = r1
        L_0x030f:
            int r1 = r0.currentDialogFolderId
            r3 = 2
            if (r1 == 0) goto L_0x032c
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r1.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r3)
            org.telegram.messenger.ImageReceiver r7 = r0.avatarImage
            r8 = 0
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$User r12 = r0.user
            r13 = 0
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x038d
        L_0x032c:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x037b
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r1 == 0) goto L_0x0352
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r7 = 12
            r1.setAvatarType(r7)
            org.telegram.messenger.ImageReceiver r8 = r0.avatarImage
            r9 = 0
            r10 = 0
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            org.telegram.tgnet.TLRPC$User r13 = r0.user
            r14 = 0
            r8.setImage(r9, r10, r11, r12, r13, r14)
            goto L_0x038d
        L_0x0352:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x0371
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x0371
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r7 = r0.avatarImage
            r8 = 0
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$User r12 = r0.user
            r13 = 0
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x038d
        L_0x0371:
            org.telegram.messenger.ImageReceiver r1 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r1.setForUserOrChat(r7, r8)
            goto L_0x038d
        L_0x037b:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x038d
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            org.telegram.messenger.ImageReceiver r1 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r7 = r0.chat
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r1.setForUserOrChat(r7, r8)
        L_0x038d:
            if (r20 == 0) goto L_0x04c6
            int r1 = r0.unreadCount
            if (r2 != r1) goto L_0x0397
            boolean r1 = r0.markUnread
            if (r6 == r1) goto L_0x04c6
        L_0x0397:
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r0.lastDialogChangedTime
            long r7 = r7 - r9
            r9 = 100
            int r1 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r1 <= 0) goto L_0x04c6
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x03ab
            r1.cancel()
        L_0x03ab:
            float[] r1 = new float[r3]
            r1 = {0, NUM} // fill-array
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.countAnimator = r1
            org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Cells.DialogCell$$ExternalSyntheticLambda1
            r3.<init>(r0)
            r1.addUpdateListener(r3)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Cells.DialogCell$1 r3 = new org.telegram.ui.Cells.DialogCell$1
            r3.<init>()
            r1.addListener(r3)
            if (r2 == 0) goto L_0x03ce
            boolean r1 = r0.markUnread
            if (r1 == 0) goto L_0x03d5
        L_0x03ce:
            boolean r1 = r0.markUnread
            if (r1 != 0) goto L_0x03f7
            if (r6 != 0) goto L_0x03d5
            goto L_0x03f7
        L_0x03d5:
            int r1 = r0.unreadCount
            if (r1 != 0) goto L_0x03e8
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 150(0x96, double:7.4E-322)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x0408
        L_0x03e8:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 430(0x1ae, double:2.124E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x0408
        L_0x03f7:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 220(0xdc, double:1.087E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r1.setInterpolator(r3)
        L_0x0408:
            boolean r1 = r0.drawCount
            if (r1 == 0) goto L_0x04b1
            android.text.StaticLayout r1 = r0.countLayout
            if (r1 == 0) goto L_0x04b1
            java.lang.String r1 = java.lang.String.valueOf(r2)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r6 = r1.length()
            int r7 = r3.length()
            if (r6 != r7) goto L_0x04ad
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r1)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r3)
            r8 = 0
        L_0x0434:
            int r10 = r1.length()
            if (r8 >= r10) goto L_0x0464
            char r10 = r1.charAt(r8)
            char r11 = r3.charAt(r8)
            if (r10 != r11) goto L_0x0457
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r9.setSpan(r10, r8, r11, r5)
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            r6.setSpan(r10, r8, r11, r5)
            goto L_0x0461
        L_0x0457:
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r7.setSpan(r10, r8, r11, r5)
        L_0x0461:
            int r8 = r8 + 1
            goto L_0x0434
        L_0x0464:
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.measureText(r1)
            double r10 = (double) r1
            double r10 = java.lang.Math.ceil(r10)
            int r1 = (int) r10
            int r1 = java.lang.Math.max(r3, r1)
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_CENTER
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 0
            r15 = 0
            r8 = r3
            r11 = r1
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r0.countOldLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_CENTER
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r3
            r11 = r7
            r13 = r1
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            r0.countAnimationStableLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_CENTER
            r10 = r3
            r11 = r6
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            r0.countAnimationInLayout = r3
            goto L_0x04b1
        L_0x04ad:
            android.text.StaticLayout r1 = r0.countLayout
            r0.countOldLayout = r1
        L_0x04b1:
            int r1 = r0.countWidth
            r0.countWidthOld = r1
            int r1 = r0.countLeft
            r0.countLeftOld = r1
            int r1 = r0.unreadCount
            if (r1 <= r2) goto L_0x04be
            goto L_0x04bf
        L_0x04be:
            r4 = 0
        L_0x04bf:
            r0.countAnimationIncrement = r4
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x04c6:
            int r1 = r18.getMeasuredWidth()
            if (r1 != 0) goto L_0x04d7
            int r1 = r18.getMeasuredHeight()
            if (r1 == 0) goto L_0x04d3
            goto L_0x04d7
        L_0x04d3:
            r18.requestLayout()
            goto L_0x04da
        L_0x04d7:
            r18.buildLayout()
        L_0x04da:
            if (r20 != 0) goto L_0x04e6
            boolean r1 = r0.dialogMuted
            if (r1 == 0) goto L_0x04e3
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x04e4
        L_0x04e3:
            r1 = 0
        L_0x04e4:
            r0.dialogMutedProgress = r1
        L_0x04e6:
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
    /* JADX WARNING: Removed duplicated region for block: B:186:0x05e0  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x05ef  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x062f  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x06bf  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x087d  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0892  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08d0  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x08d3  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x08dd  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x08e0  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x0928  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x092f  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x09ca  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a18  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0d60  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0d93  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0dcb  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0de8  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0eab  */
    /* JADX WARNING: Removed duplicated region for block: B:637:0x1121  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x1135  */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x1148  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x1162  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x118e  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x11f7  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x124e  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1261  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x127b  */
    /* JADX WARNING: Removed duplicated region for block: B:720:0x12a5  */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x12d3  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x12e7  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x130d  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x132d  */
    /* JADX WARNING: Removed duplicated region for block: B:759:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r32) {
        /*
            r31 = this;
            r8 = r31
            r9 = r32
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
            r32.save()
            int r0 = r31.getMeasuredWidth()
            int r1 = r31.getMeasuredHeight()
            r9.clipRect(r10, r10, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r32.restore()
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
            r32.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r31.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r31.getMeasuredHeight()
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
            r7 = 2
            r17 = 1082130432(0x40800000, float:4.0)
            r6 = 1
            r5 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x00b1
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0095
            goto L_0x00b1
        L_0x0095:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x00ac
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r10
        L_0x00ac:
            r6 = 1065353216(0x3var_, float:1.0)
            r15 = 1
            goto L_0x0461
        L_0x00b1:
            r32.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r18 = "chats_archivePinBackground"
            java.lang.String r19 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00ec
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00d6
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r2 = 2131628027(0x7f0e0ffb, float:1.8883335E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x00d6:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r2 = 2131625813(0x7f0e0755, float:1.8878845E38)
            java.lang.String r3 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x00ec:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0108
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r2 = 2131627230(0x7f0e0cde, float:1.8881719E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r4
        L_0x0105:
            r4 = r1
            goto L_0x01d7
        L_0x0108:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01c0
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r3 = 3
            if (r2 != r3) goto L_0x013d
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x012f
            r2 = 2131627845(0x7f0e0var_, float:1.8882966E38)
            java.lang.String r3 = "SwipeUnmute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x012f:
            r2 = 2131627833(0x7f0e0var_, float:1.8882942E38)
            java.lang.String r3 = "SwipeMute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x013d:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r14) goto L_0x0159
            r2 = 2131627830(0x7f0e0var_, float:1.8882936E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r2)
            java.lang.String r0 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x0159:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r6) goto L_0x0187
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x0178
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x016a
            goto L_0x0178
        L_0x016a:
            r2 = 2131627832(0x7f0e0var_, float:1.888294E38)
            java.lang.String r3 = "SwipeMarkAsUnread"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x0178:
            r2 = 2131627831(0x7f0e0var_, float:1.8882938E38)
            java.lang.String r3 = "SwipeMarkAsRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x0187:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01b1
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01a2
            r2 = 2131627846(0x7f0e0var_, float:1.8882968E38)
            java.lang.String r3 = "SwipeUnpin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01a2:
            r2 = 2131627834(0x7f0e0f3a, float:1.8882944E38)
            java.lang.String r3 = "SwipePin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01b1:
            r2 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r3 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01c0:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r2 = 2131628018(0x7f0e0ff2, float:1.8883317E38)
            java.lang.String r3 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01d7:
            boolean r1 = r8.swipeCanceled
            if (r1 == 0) goto L_0x01e4
            org.telegram.ui.Components.RLottieDrawable r1 = r8.lastDrawTranslationDrawable
            if (r1 == 0) goto L_0x01e4
            r8.translationDrawable = r1
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x01ea
        L_0x01e4:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r1
            r8.lastDrawSwipeMessageStringId = r2
        L_0x01ea:
            boolean r1 = r8.translationAnimationStarted
            if (r1 != 0) goto L_0x0210
            float r1 = r8.translationX
            float r1 = java.lang.Math.abs(r1)
            r20 = 1110179840(0x422CLASSNAME, float:43.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r14 = (float) r14
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x0210
            r8.translationAnimationStarted = r6
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.start()
        L_0x0210:
            int r1 = r31.getMeasuredWidth()
            float r1 = (float) r1
            float r14 = r8.translationX
            float r14 = r14 + r1
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 >= 0) goto L_0x0294
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r0 = (float) r0
            float r0 = r14 - r0
            r20 = 0
            int r1 = r31.getMeasuredWidth()
            float r1 = (float) r1
            int r5 = r31.getMeasuredHeight()
            float r5 = (float) r5
            android.graphics.Paint r22 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r23 = r1
            r1 = r32
            r28 = r2
            r2 = r0
            r0 = r3
            r3 = r20
            r29 = r4
            r4 = r23
            r6 = r22
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0299
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x0262
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r19)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r10
        L_0x0262:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x0299
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r19)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r19)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r19)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r10
            goto L_0x0299
        L_0x0294:
            r28 = r2
            r0 = r3
            r29 = r4
        L_0x0299:
            int r1 = r31.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r7
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x02b8
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02b5
            goto L_0x02b8
        L_0x02b5:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x02ba
        L_0x02b8:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x02ba:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r7
            int r3 = r3 + r1
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r7
            int r4 = r4 + r2
            float r5 = r8.currentRevealProgress
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 <= 0) goto L_0x0363
            r32.save()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r5 = (float) r5
            float r5 = r14 - r5
            int r6 = r31.getMeasuredWidth()
            float r6 = (float) r6
            int r15 = r31.getMeasuredHeight()
            float r15 = (float) r15
            r9.clipRect(r5, r11, r6, r15)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r6 = r29
            r5.setColor(r6)
            int r5 = r3 * r3
            int r6 = r31.getMeasuredHeight()
            int r6 = r4 - r6
            int r15 = r31.getMeasuredHeight()
            int r15 = r4 - r15
            int r6 = r6 * r15
            int r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.sqrt(r5)
            float r5 = (float) r5
            float r3 = (float) r3
            float r4 = (float) r4
            android.view.animation.AccelerateInterpolator r6 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r15 = r8.currentRevealProgress
            float r6 = r6.getInterpolation(r15)
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r3, r4, r5, r6)
            r32.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x0330
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r15 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
            goto L_0x0331
        L_0x0330:
            r15 = 1
        L_0x0331:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x0364
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r5 = "Line 1.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r5 = "Line 2.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r5 = "Line 3.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r15
            goto L_0x0364
        L_0x0363:
            r15 = 1
        L_0x0364:
            r32.save()
            float r1 = (float) r1
            float r2 = (float) r2
            r9.translate(r1, r2)
            float r1 = r8.currentRevealBounceProgress
            int r2 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            r6 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x0392
            int r2 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x0392
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r2 = r8.interpolator
            float r1 = r2.getInterpolation(r1)
            float r1 = r1 + r6
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r7
            float r2 = (float) r2
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r7
            float r3 = (float) r3
            r9.scale(r1, r1, r2, r3)
        L_0x0392:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r10, (int) r10)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.draw(r9)
            r32.restore()
            int r1 = r31.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r31.getMeasuredHeight()
            float r2 = (float) r2
            r9.clipRect(r14, r11, r1, r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r1.measureText(r0)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = r8.swipeMessageTextId
            r3 = r28
            if (r2 != r3) goto L_0x03c6
            int r2 = r8.swipeMessageWidth
            int r4 = r31.getMeasuredWidth()
            if (r2 == r4) goto L_0x0412
        L_0x03c6:
            r8.swipeMessageTextId = r3
            int r2 = r31.getMeasuredWidth()
            r8.swipeMessageWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r22 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r23 = java.lang.Math.min(r3, r1)
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_CENTER
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r2
            r21 = r0
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r8.swipeMessageTextLayout = r2
            int r2 = r2.getLineCount()
            if (r2 <= r15) goto L_0x0412
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r22 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r3 = 1118044160(0x42a40000, float:82.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r23 = java.lang.Math.min(r3, r1)
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_CENTER
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r2
            r21 = r0
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r8.swipeMessageTextLayout = r2
        L_0x0412:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x045e
            r32.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            if (r0 <= r15) goto L_0x0428
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x0429
        L_0x0428:
            r0 = 0
        L_0x0429:
            int r1 = r31.getMeasuredWidth()
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
            if (r2 != 0) goto L_0x044b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0448
            goto L_0x044b
        L_0x0448:
            r2 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x044d
        L_0x044b:
            r2 = 1112014848(0x42480000, float:50.0)
        L_0x044d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 + r0
            r9.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r9)
            r32.restore()
        L_0x045e:
            r32.restore()
        L_0x0461:
            float r0 = r8.translationX
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x046f
            r32.save()
            float r0 = r8.translationX
            r9.translate(r0, r11)
        L_0x046f:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x048a
            r2 = 0
            r3 = 0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r31.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r32
            r14 = 1065353216(0x3var_, float:1.0)
            r6 = r0
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x048c
        L_0x048a:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x048c:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x04bf
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x049a
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x04bf
        L_0x049a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r10, r1, r2, r14)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r31.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r32
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x04e5
        L_0x04bf:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x04c7
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x04e5
        L_0x04c7:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r31.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r32
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x04e5:
            float r0 = r8.translationX
            java.lang.String r18 = "windowBackgroundWhite"
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x04f8
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x04f4
            goto L_0x04f8
        L_0x04f4:
            r19 = 1090519040(0x41000000, float:8.0)
            goto L_0x05aa
        L_0x04f8:
            r32.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r31.getMeasuredWidth()
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r31.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r31.getMeasuredHeight()
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
            if (r0 == 0) goto L_0x0575
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0548
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0575
        L_0x0548:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r10, r1, r2, r14)
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
            goto L_0x057e
        L_0x0575:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0581
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x057e
            goto L_0x0581
        L_0x057e:
            r19 = 1090519040(0x41000000, float:8.0)
            goto L_0x05a7
        L_0x0581:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            r19 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r2 = r8.cornerProgress
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r1, r2, r3)
        L_0x05a7:
            r32.restore()
        L_0x05aa:
            float r0 = r8.translationX
            r20 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x05c5
            float r0 = r8.cornerProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x05da
            float r1 = (float) r12
            float r1 = r1 / r20
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x05d7
            r8.cornerProgress = r14
            goto L_0x05d7
        L_0x05c5:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x05da
            float r1 = (float) r12
            float r1 = r1 / r20
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x05d7
            r8.cornerProgress = r11
        L_0x05d7:
            r21 = 1
            goto L_0x05dc
        L_0x05da:
            r21 = 0
        L_0x05dc:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x05ef
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x0627
        L_0x05ef:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x0602
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x0627
        L_0x0602:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x0615
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x0627
        L_0x0615:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x0627
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x0627:
            android.text.StaticLayout r0 = r8.nameLayout
            r22 = 1092616192(0x41200000, float:10.0)
            r23 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x069f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0647
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x067b
        L_0x0647:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0668
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x0654
            int r0 = r0.type
            if (r0 != r7) goto L_0x0654
            goto L_0x0668
        L_0x0654:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x067b
        L_0x0668:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x067b:
            r32.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x068d
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x068a
            goto L_0x068d
        L_0x068a:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x068f
        L_0x068d:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x068f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r32.restore()
        L_0x069f:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x06bb
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x06bb
            r32.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r32.restore()
        L_0x06bb:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x0709
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06d1
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x06f0
        L_0x06d1:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x06e3
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x06f0
        L_0x06e3:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x06f0:
            r32.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x0702 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0702 }
            goto L_0x0706
        L_0x0702:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0706:
            r32.restore()
        L_0x0709:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x07c2
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x073d
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0729
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0750
        L_0x0729:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0750
        L_0x073d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0750:
            r32.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x0762 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0762 }
            goto L_0x0766
        L_0x0762:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0766:
            r32.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x07c2
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x07c2
            r32.save()
            int r1 = r8.printingStringType
            if (r1 == r15) goto L_0x0797
            r2 = 4
            if (r1 != r2) goto L_0x077e
            goto L_0x0797
        L_0x077e:
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
            goto L_0x07a9
        L_0x0797:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r15) goto L_0x07a3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            goto L_0x07a4
        L_0x07a3:
            r1 = 0
        L_0x07a4:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x07a9:
            r0.draw(r9)
            int r1 = r8.statusDrawableLeft
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r4 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r4 = r4 + r0
            r8.invalidate(r1, r2, r3, r4)
            r32.restore()
        L_0x07c2:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x087d
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x07ce
            r1 = 2
            goto L_0x07cf
        L_0x07ce:
            r1 = 0
        L_0x07cf:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x07d6
            r1 = 4
            goto L_0x07d7
        L_0x07d6:
            r1 = 0
        L_0x07d7:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x07e5
            if (r1 == r0) goto L_0x07e5
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x07e5
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x07e5:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x07eb
            int r0 = r8.animateToStatusDrawableParams
        L_0x07eb:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x07f2
            r24 = 1
            goto L_0x07f4
        L_0x07f2:
            r24 = 0
        L_0x07f4:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x07fc
            r2 = 4
            r25 = 1
            goto L_0x07ff
        L_0x07fc:
            r2 = 4
            r25 = 0
        L_0x07ff:
            r0 = r0 & r2
            if (r0 == 0) goto L_0x0804
            r0 = 1
            goto L_0x0805
        L_0x0804:
            r0 = 0
        L_0x0805:
            if (r1 == 0) goto L_0x0858
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x080f
            r3 = 1
            goto L_0x0810
        L_0x080f:
            r3 = 0
        L_0x0810:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x0817
            r2 = 4
            r4 = 1
            goto L_0x0819
        L_0x0817:
            r2 = 4
            r4 = 0
        L_0x0819:
            r1 = r1 & r2
            if (r1 == 0) goto L_0x081e
            r5 = 1
            goto L_0x081f
        L_0x081e:
            r5 = 0
        L_0x081f:
            if (r24 != 0) goto L_0x0840
            if (r3 != 0) goto L_0x0840
            if (r5 == 0) goto L_0x0840
            if (r4 != 0) goto L_0x0840
            if (r25 == 0) goto L_0x0840
            if (r0 == 0) goto L_0x0840
            r6 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r31
            r2 = r32
            r3 = r24
            r4 = r25
            r24 = r5
            r5 = r0
            r10 = 2
            r7 = r24
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0868
        L_0x0840:
            r10 = 2
            r6 = 0
            float r1 = r8.statusDrawableProgress
            float r7 = r14 - r1
            r1 = r31
            r2 = r32
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r24
            r4 = r25
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0868
        L_0x0858:
            r10 = 2
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r31
            r2 = r32
            r3 = r24
            r4 = r25
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0868:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0870
            r7 = 2
            goto L_0x0871
        L_0x0870:
            r7 = 0
        L_0x0871:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0878
            r2 = 4
            goto L_0x0879
        L_0x0878:
            r2 = 0
        L_0x0879:
            int r0 = r0 + r2
            r8.lastStatusDrawableParams = r0
            goto L_0x087e
        L_0x087d:
            r10 = 2
        L_0x087e:
            boolean r0 = r8.dialogMuted
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x088a
            float r2 = r8.dialogMutedProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x092f
        L_0x088a:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x092f
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x092f
            if (r0 == 0) goto L_0x08ab
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x08ab
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x08a7
            r8.dialogMutedProgress = r14
            goto L_0x08c3
        L_0x08a7:
            r31.invalidate()
            goto L_0x08c3
        L_0x08ab:
            if (r0 != 0) goto L_0x08c3
            float r0 = r8.dialogMutedProgress
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x08c3
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r2
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x08c0
            r8.dialogMutedProgress = r11
            goto L_0x08c3
        L_0x08c0:
            r31.invalidate()
        L_0x08c3:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x08d3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x08d0
            goto L_0x08d3
        L_0x08d0:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x08d4
        L_0x08d3:
            r5 = 0
        L_0x08d4:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x08e0
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x08e2
        L_0x08e0:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x08e2:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0928
            r32.save()
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
            r32.restore()
            goto L_0x099e
        L_0x0928:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x099e
        L_0x092f:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0970
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0943
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0940
            goto L_0x0943
        L_0x0940:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0945
        L_0x0943:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0945:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x095c
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0959
            goto L_0x095c
        L_0x0959:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x095e
        L_0x095c:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x095e:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x099e
        L_0x0970:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x099e
            if (r0 != r15) goto L_0x0979
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x097b
        L_0x0979:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x097b:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0989
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0986
            goto L_0x0989
        L_0x0986:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x098b
        L_0x0989:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x098b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            if (r0 != r15) goto L_0x0999
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x099b
        L_0x0999:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x099b:
            r0.draw(r9)
        L_0x099e:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x09a8
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x09c0
        L_0x09a8:
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
        L_0x09c0:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x0a18
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
            goto L_0x0d5a
        L_0x0a18:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0a47
            boolean r5 = r8.drawMention
            if (r5 != 0) goto L_0x0a47
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0a27
            goto L_0x0a47
        L_0x0a27:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0d5a
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
            goto L_0x0d5a
        L_0x0a47:
            if (r0 != 0) goto L_0x0a4f
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0ca8
        L_0x0a4f:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0a5c
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x0a5c
            float r5 = r8.countChangeProgress
            float r5 = r14 - r5
            goto L_0x0a5e
        L_0x0a5c:
            float r5 = r8.countChangeProgress
        L_0x0a5e:
            android.text.StaticLayout r6 = r8.countOldLayout
            if (r6 == 0) goto L_0x0bd4
            if (r0 != 0) goto L_0x0a66
            goto L_0x0bd4
        L_0x0a66:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0a72
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0a6f
            goto L_0x0a72
        L_0x0a6f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0a74
        L_0x0a72:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0a74:
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
            if (r7 <= 0) goto L_0x0a93
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0a94
        L_0x0a93:
            r7 = r6
        L_0x0a94:
            int r15 = r8.countLeft
            float r15 = (float) r15
            float r15 = r15 * r7
            int r4 = r8.countLeftOld
            float r4 = (float) r4
            float r25 = r14 - r7
            float r4 = r4 * r25
            float r15 = r15 + r4
            r4 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r15 - r4
            android.graphics.RectF r11 = r8.rect
            int r10 = r8.countTop
            float r10 = (float) r10
            int r1 = r8.countWidth
            float r1 = (float) r1
            float r1 = r1 * r7
            float r1 = r1 + r4
            int r3 = r8.countWidthOld
            float r3 = (float) r3
            float r3 = r3 * r25
            float r1 = r1 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r1 = r1 + r3
            int r3 = r8.countTop
            int r30 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 + r30
            float r3 = (float) r3
            r11.set(r4, r10, r1, r3)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0adf
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r6)
            goto L_0x0aef
        L_0x0adf:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r4 = 1056964608(0x3var_, float:0.5)
            float r5 = r5 - r4
            float r5 = r5 * r16
            float r5 = r14 - r5
            float r3 = r3.getInterpolation(r5)
        L_0x0aef:
            float r3 = r3 * r1
            float r3 = r3 + r14
            r32.save()
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
            if (r0 == 0) goto L_0x0b2b
            r32.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r15, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r32.restore()
        L_0x0b2b:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r4 = r3 * r7
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0b68
            r32.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0b4a
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x0b4f
        L_0x0b4a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r1 = -r1
        L_0x0b4f:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r15, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r9)
            r32.restore()
            goto L_0x0b95
        L_0x0b68:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0b95
            r32.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0b78
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x0b7d
        L_0x0b78:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r1 = -r1
        L_0x0b7d:
            float r1 = (float) r1
            float r1 = r1 * r25
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r15, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r9)
            r32.restore()
        L_0x0b95:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0bca
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r25
            int r3 = (int) r3
            r1.setAlpha(r3)
            r32.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0bae
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r1 = -r1
            goto L_0x0bb2
        L_0x0bae:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x0bb2:
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
            r32.restore()
        L_0x0bca:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r32.restore()
            goto L_0x0ca8
        L_0x0bd4:
            if (r0 != 0) goto L_0x0bd7
            goto L_0x0bd9
        L_0x0bd7:
            android.text.StaticLayout r6 = r8.countLayout
        L_0x0bd9:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0be5
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0be2
            goto L_0x0be5
        L_0x0be2:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0be7
        L_0x0be5:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0be7:
            float r1 = r8.reorderIconProgress
            float r1 = r14 - r1
            r3 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r3
            int r1 = (int) r1
            r0.setAlpha(r1)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r8.reorderIconProgress
            float r4 = r14 - r4
            float r4 = r4 * r3
            int r3 = (int) r4
            r1.setAlpha(r3)
            int r1 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r3
            android.graphics.RectF r3 = r8.rect
            float r4 = (float) r1
            int r7 = r8.countTop
            float r7 = (float) r7
            int r10 = r8.countWidth
            int r1 = r1 + r10
            r10 = 1093664768(0x41300000, float:11.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r1 = r1 + r10
            float r1 = (float) r1
            int r10 = r8.countTop
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r10 = r10 + r11
            float r10 = (float) r10
            r3.set(r4, r7, r1, r10)
            int r1 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0c7b
            boolean r1 = r8.drawPin
            if (r1 == 0) goto L_0x0CLASSNAME
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r3 = r8.reorderIconProgress
            float r3 = r14 - r3
            r4 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            r1.setAlpha(r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r3 = r8.pinLeft
            int r4 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r3, (int) r4)
            r32.save()
            float r1 = r14 - r5
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerX()
            float r3 = (float) r3
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r4 = r4.getBounds()
            int r4 = r4.centerY()
            float r4 = (float) r4
            r9.scale(r1, r1, r3, r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r9)
            r32.restore()
        L_0x0CLASSNAME:
            r32.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r5, r5, r1, r3)
        L_0x0c7b:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r7 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r7, r3, r0)
            if (r6 == 0) goto L_0x0ca1
            r32.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r1 + r3
            float r1 = (float) r1
            r9.translate(r0, r1)
            r6.draw(r9)
            r32.restore()
        L_0x0ca1:
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x0ca8
            r32.restore()
        L_0x0ca8:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0d5a
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r1 = r8.reorderIconProgress
            float r5 = r14 - r1
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
            if (r0 == 0) goto L_0x0cea
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0cea
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0cec
        L_0x0cea:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0cec:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0d25
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            float r5 = r14 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            r32.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r9)
            r32.restore()
            goto L_0x0d5a
        L_0x0d25:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            float r5 = r14 - r1
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
        L_0x0d5a:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0d7c
            r32.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 + r14
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r9.scale(r0, r0, r1, r2)
        L_0x0d7c:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0d8a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0d8a
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0d8f
        L_0x0d8a:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0d8f:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0dc7
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0dc7
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
        L_0x0dc7:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0dce
            r32.restore()
        L_0x0dce:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0ea8
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0ea8
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0eab
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0eab
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x0eab
            boolean r0 = r31.isOnline()
            if (r0 != 0) goto L_0x0df5
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0ea4
        L_0x0df5:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e07
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e04
            goto L_0x0e07
        L_0x0e04:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0e09
        L_0x0e07:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e09:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0e2c
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0e25
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0e23
            goto L_0x0e25
        L_0x0e23:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e25:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0e43
        L_0x0e2c:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0e3d
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0e3b
            goto L_0x0e3d
        L_0x0e3b:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e3d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0e43:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r18)
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
            java.lang.String r4 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
            r3 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r8.onlineProgress
            float r3 = r3 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r1, r2, r3, r4)
            if (r0 == 0) goto L_0x0e8f
            float r0 = r8.onlineProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x0ea4
            float r1 = (float) r12
            float r1 = r1 / r20
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x0ea2
            r8.onlineProgress = r14
            goto L_0x0ea2
        L_0x0e8f:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ea4
            float r2 = (float) r12
            float r2 = r2 / r20
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0ea2
            r8.onlineProgress = r1
        L_0x0ea2:
            r6 = 1
            goto L_0x0ea6
        L_0x0ea4:
            r6 = r21
        L_0x0ea6:
            r21 = r6
        L_0x0ea8:
            r2 = 0
            goto L_0x115c
        L_0x0eab:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0ea8
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0eb9
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0eb9
            r6 = 1
            goto L_0x0eba
        L_0x0eb9:
            r6 = 0
        L_0x0eba:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x0ec5
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0ea8
        L_0x0ec5:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0ed8
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0ed8
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r5 = r14 - r0
            goto L_0x0eda
        L_0x0ed8:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0eda:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0eec
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0ee9
            goto L_0x0eec
        L_0x0ee9:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0eee
        L_0x0eec:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0eee:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0f0a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
            goto L_0x0f0a
        L_0x0var_:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f0a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0var_
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0var_
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0var_:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
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
            java.lang.String r4 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
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
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r3.setColor(r4)
            int r3 = r8.progressStage
            r4 = 1077936128(0x40400000, float:3.0)
            if (r3 != 0) goto L_0x0var_
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
        L_0x0f8d:
            float r10 = r10 * r11
            float r6 = r6 - r10
            goto L_0x107d
        L_0x0var_:
            r6 = 1
            if (r3 != r6) goto L_0x0fb7
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
        L_0x0fb2:
            float r10 = r10 * r11
            float r6 = r6 + r10
            goto L_0x107d
        L_0x0fb7:
            r6 = 2
            if (r3 != r6) goto L_0x0fd8
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x0f8d
        L_0x0fd8:
            r6 = 3
            if (r3 != r6) goto L_0x0ff7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x0fb2
        L_0x0ff7:
            r6 = 4
            if (r3 != r6) goto L_0x1017
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x0f8d
        L_0x1017:
            r6 = 5
            if (r3 != r6) goto L_0x1039
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x0fb2
        L_0x1039:
            r6 = 6
            if (r3 != r6) goto L_0x105e
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r10 = (float) r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            float r15 = r8.innerProgress
            float r11 = r11 * r15
            float r10 = r10 - r11
            r6 = r10
            goto L_0x107d
        L_0x105e:
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r10 = r8.innerProgress
            float r6 = r6 * r10
            float r3 = r3 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r10 = (float) r10
            float r11 = r8.innerProgress
            goto L_0x0fb2
        L_0x107d:
            float r10 = r8.chatCallProgress
            int r10 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r10 < 0) goto L_0x1087
            int r10 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r10 >= 0) goto L_0x1093
        L_0x1087:
            r32.save()
            float r10 = r8.chatCallProgress
            float r11 = r10 * r5
            float r10 = r10 * r5
            r9.scale(r11, r10, r2, r0)
        L_0x1093:
            android.graphics.RectF r2 = r8.rect
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r11 = r0 - r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r15 + r1
            float r15 = (float) r15
            float r3 = r3 + r0
            r2.set(r10, r11, r15, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r10 = (float) r10
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r10, r11)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r10
            float r3 = (float) r3
            float r10 = r0 - r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = r1 - r11
            float r11 = (float) r11
            float r0 = r0 + r6
            r2.set(r3, r10, r11, r0)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r6, r11)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r1
            float r3 = (float) r3
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r4
            float r1 = (float) r1
            r2.set(r3, r10, r1, r0)
            android.graphics.RectF r0 = r8.rect
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r2 = (float) r2
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r0, r1, r2, r3)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 < 0) goto L_0x1110
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x1113
        L_0x1110:
            r32.restore()
        L_0x1113:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 < 0) goto L_0x1131
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x1131
            r2 = 0
            r8.progressStage = r2
        L_0x1131:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x1148
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 >= 0) goto L_0x1146
            float r1 = r1 / r20
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x1146
            r8.chatCallProgress = r14
        L_0x1146:
            r2 = 0
            goto L_0x115a
        L_0x1148:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x115a
            float r1 = r1 / r20
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x115a
            r8.chatCallProgress = r2
        L_0x115a:
            r21 = 1
        L_0x115c:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1165
            r32.restore()
        L_0x1165:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x118a
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x118a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x118a
            r32.save()
            int r0 = r31.getMeasuredWidth()
            int r1 = r31.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r32.restore()
        L_0x118a:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x11ef
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x11ae
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x119e
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x119e
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x11ae
        L_0x119e:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x11a7
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x11a7
            goto L_0x11ae
        L_0x11a7:
            r0 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x11af
        L_0x11ae:
            r2 = 0
        L_0x11af:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x11d4
            r0 = 0
            int r1 = r31.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r31.getMeasuredWidth()
            int r1 = r1 - r2
            float r5 = (float) r1
            int r1 = r31.getMeasuredHeight()
            int r1 = r1 - r3
            float r6 = (float) r1
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r32
            r2 = r0
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r10
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x11ef
        L_0x11d4:
            float r2 = (float) r2
            int r0 = r31.getMeasuredHeight()
            r10 = 1
            int r0 = r0 - r10
            float r3 = (float) r0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r31.getMeasuredHeight()
            int r0 = r0 - r10
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r32
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x11f0
        L_0x11ef:
            r10 = 1
        L_0x11f0:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x123e
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1201
            r32.restore()
            goto L_0x123e
        L_0x1201:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r32
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r31.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r31.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r31.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r32
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x123e:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x124c
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x124a
            goto L_0x124c
        L_0x124a:
            r1 = 0
            goto L_0x1275
        L_0x124c:
            if (r0 == 0) goto L_0x1261
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x124a
            float r1 = (float) r12
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x125f
            r8.reorderIconProgress = r14
        L_0x125f:
            r1 = 0
            goto L_0x1273
        L_0x1261:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1275
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1273
            r8.reorderIconProgress = r1
        L_0x1273:
            r6 = 1
            goto L_0x1277
        L_0x1275:
            r6 = r21
        L_0x1277:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x12a5
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x12cf
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x128e
            r8.archiveBackgroundProgress = r1
        L_0x128e:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12ce
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x12ce
        L_0x12a5:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x12cf
            float r1 = (float) r12
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x12b8
            r8.archiveBackgroundProgress = r14
        L_0x12b8:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12ce
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x12ce:
            r6 = 1
        L_0x12cf:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x12e3
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x12e2
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x12e2:
            r6 = 1
        L_0x12e3:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x130d
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x12f9
            float r1 = (float) r12
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x12f9
            r8.currentRevealBounceProgress = r14
            r6 = 1
        L_0x12f9:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x132b
            float r1 = (float) r12
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x132a
            r8.currentRevealProgress = r14
            goto L_0x132a
        L_0x130d:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x1317
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x1317:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x132b
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x132a
            r8.currentRevealProgress = r1
        L_0x132a:
            r6 = 1
        L_0x132b:
            if (r6 == 0) goto L_0x1330
            r31.invalidate()
        L_0x1330:
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
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.message.messageOwner.from_id.user_id))) != null) {
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
