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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$User;
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

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0022, code lost:
        r0 = r0.status;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkOnline() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$User r0 = r2.user
            if (r0 == 0) goto L_0x001a
            int r0 = r2.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r1 = r2.user
            int r1 = r1.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 == 0) goto L_0x001a
            r2.user = r0
        L_0x001a:
            org.telegram.tgnet.TLRPC$User r0 = r2.user
            if (r0 == 0) goto L_0x004c
            boolean r1 = r0.self
            if (r1 != 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0034
            int r0 = r0.expires
            int r1 = r2.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            if (r0 > r1) goto L_0x004a
        L_0x0034:
            int r0 = r2.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r1 = r2.user
            int r1 = r1.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.containsKey(r1)
            if (r0 == 0) goto L_0x004c
        L_0x004a:
            r0 = 1
            goto L_0x004d
        L_0x004c:
            r0 = 0
        L_0x004d:
            if (r0 == 0) goto L_0x0052
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0053
        L_0x0052:
            r0 = 0
        L_0x0053:
            r2.onlineProgress = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.checkOnline():void");
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

    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0609, code lost:
        if (r2.post_messages == false) goto L_0x05e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0615, code lost:
        if (r2.kicked != false) goto L_0x05e5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1029:0x1856  */
    /* JADX WARNING: Removed duplicated region for block: B:1065:0x18f3 A[Catch:{ Exception -> 0x1920 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1066:0x18f6 A[Catch:{ Exception -> 0x1920 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1069:0x1910 A[Catch:{ Exception -> 0x1920 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1077:0x192c  */
    /* JADX WARNING: Removed duplicated region for block: B:1086:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0b25 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x0b2c A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0bd7 A[SYNTHETIC, Splitter:B:528:0x0bd7] */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0bf5  */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0df6  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e94  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e9d  */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0eaa  */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0eb2  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0ecf  */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0ee1  */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1002  */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x100d  */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x1046  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x1058  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x10aa  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x10b6  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x10f5  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x1100  */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x1110  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x1128  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x1137  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x1170  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x1198  */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x121b  */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x1231  */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x12a5 A[Catch:{ Exception -> 0x12c4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:829:0x12a8 A[Catch:{ Exception -> 0x12c4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x12d2  */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x137b  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x142c  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x1451  */
    /* JADX WARNING: Removed duplicated region for block: B:860:0x1481  */
    /* JADX WARNING: Removed duplicated region for block: B:897:0x15a2  */
    /* JADX WARNING: Removed duplicated region for block: B:939:0x1664  */
    /* JADX WARNING: Removed duplicated region for block: B:940:0x166d  */
    /* JADX WARNING: Removed duplicated region for block: B:950:0x1693 A[Catch:{ Exception -> 0x171c }] */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x169f A[ADDED_TO_REGION, Catch:{ Exception -> 0x171c }] */
    /* JADX WARNING: Removed duplicated region for block: B:959:0x16bc A[Catch:{ Exception -> 0x171c }] */
    /* JADX WARNING: Removed duplicated region for block: B:974:0x170a A[Catch:{ Exception -> 0x171c }] */
    /* JADX WARNING: Removed duplicated region for block: B:975:0x170d A[Catch:{ Exception -> 0x171c }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x1724  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r44 = this;
            r1 = r44
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
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r3) goto L_0x00f6
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x00ec
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x00f0
        L_0x00ec:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x00f3
        L_0x00f0:
            java.lang.String r12 = "%2$s: ⁨%1$s⁩"
            goto L_0x0104
        L_0x00f3:
            java.lang.String r12 = "⁨%s⁩"
            goto L_0x0108
        L_0x00f6:
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x00fe
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x0102
        L_0x00fe:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x0106
        L_0x0102:
            java.lang.String r12 = "%2$s: %1$s"
        L_0x0104:
            r13 = 1
            goto L_0x0109
        L_0x0106:
            java.lang.String r12 = "%1$s"
        L_0x0108:
            r13 = 0
        L_0x0109:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 == 0) goto L_0x0110
            java.lang.CharSequence r14 = r14.messageText
            goto L_0x0111
        L_0x0110:
            r14 = 0
        L_0x0111:
            r1.lastMessageString = r14
            org.telegram.ui.Cells.DialogCell$CustomDialog r14 = r1.customDialog
            r15 = 1117782016(0x42a00000, float:80.0)
            r16 = 1118044160(0x42a40000, float:82.0)
            r18 = 1101004800(0x41a00000, float:20.0)
            r3 = 33
            r20 = 1102053376(0x41b00000, float:22.0)
            r8 = 150(0x96, float:2.1E-43)
            r21 = 1117257728(0x42980000, float:76.0)
            r22 = 1099956224(0x41900000, float:18.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            r2 = 2
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x0358
            int r0 = r14.type
            if (r0 != r2) goto L_0x01b1
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0176
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x013b
            goto L_0x0176
        L_0x013b:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x015c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0282
        L_0x015c:
            int r0 = r44.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x0176:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0197
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0282
        L_0x0197:
            int r0 = r44.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x01b1:
            boolean r10 = r14.verified
            r1.drawVerified = r10
            boolean r10 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r10 == 0) goto L_0x0256
            if (r0 != r5) goto L_0x0256
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x020f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c6
            goto L_0x020f
        L_0x01c6:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01ee
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01e3
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01e5
        L_0x01e3:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01e5:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0282
        L_0x01ee:
            int r0 = r44.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01fe
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0200
        L_0x01fe:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0200:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x020f:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0236
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x022c
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x022e
        L_0x022c:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x022e:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0282
        L_0x0236:
            int r0 = r44.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0246
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0248
        L_0x0246:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0248:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x0256:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0271
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x025f
            goto L_0x0271
        L_0x025f:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x026a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x026a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x0271:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x027c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x0282
        L_0x027c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
        L_0x0282:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r10 = r0.type
            if (r10 != r5) goto L_0x030a
            r0 = 2131625643(0x7f0e06ab, float:1.88785E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r10 = r1.customDialog
            boolean r11 = r10.isMedia
            if (r11 == 0) goto L_0x02bc
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r9 = r9[r10]
            java.lang.Object[] r10 = new java.lang.Object[r5]
            org.telegram.messenger.MessageObject r11 = r1.message
            java.lang.CharSequence r11 = r11.messageText
            r10[r6] = r11
            java.lang.String r10 = java.lang.String.format(r12, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r10)
            org.telegram.ui.Components.ForegroundColorSpanThemable r11 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r12 = "chats_attachMessage"
            r11.<init>(r12)
            int r12 = r10.length()
            r10.setSpan(r11, r6, r12, r3)
            goto L_0x02f6
        L_0x02bc:
            java.lang.String r3 = r10.message
            int r10 = r3.length()
            if (r10 <= r8) goto L_0x02c8
            java.lang.String r3 = r3.substring(r6, r8)
        L_0x02c8:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x02e8
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x02d1
            goto L_0x02e8
        L_0x02d1:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r11 = 32
            r13 = 10
            java.lang.String r3 = r3.replace(r13, r11)
            r10[r6] = r3
            r10[r5] = r0
            java.lang.String r3 = java.lang.String.format(r12, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r3)
            goto L_0x02f6
        L_0x02e8:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r10[r6] = r3
            r10[r5] = r0
            java.lang.String r3 = java.lang.String.format(r12, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r3)
        L_0x02f6:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r3 = r3[r11]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r10, r3, r11, r6)
            r10 = 0
            goto L_0x0318
        L_0x030a:
            java.lang.String r3 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0316
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r0[r9]
        L_0x0316:
            r0 = 0
            r10 = 1
        L_0x0318:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.unread_count
            if (r12 == 0) goto L_0x0338
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13[r6] = r12
            java.lang.String r12 = "%d"
            java.lang.String r12 = java.lang.String.format(r12, r13)
            goto L_0x033b
        L_0x0338:
            r1.drawCount = r6
            r12 = 0
        L_0x033b:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            boolean r14 = r13.sent
            if (r14 == 0) goto L_0x0346
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            goto L_0x034a
        L_0x0346:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
        L_0x034a:
            r1.drawClock = r6
            r1.drawError = r6
            java.lang.String r13 = r13.name
            r2 = r0
            r5 = r9
            r16 = r10
            r0 = 1
            r9 = 0
            goto L_0x10b4
        L_0x0358:
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x0373
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0361
            goto L_0x0373
        L_0x0361:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x036c
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r14
            goto L_0x0384
        L_0x036c:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x0384
        L_0x0373:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x037e
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r14
            goto L_0x0384
        L_0x037e:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
        L_0x0384:
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 == 0) goto L_0x040d
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x05b6
            r1.drawNameLock = r5
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x03d2
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0397
            goto L_0x03d2
        L_0x0397:
            r14 = 1099169792(0x41840000, float:16.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03b8
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x05b6
        L_0x03b8:
            int r14 = r44.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x05b6
        L_0x03d2:
            r14 = 1095237632(0x41480000, float:12.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03f3
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x05b6
        L_0x03f3:
            int r14 = r44.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
            goto L_0x05b6
        L_0x040d:
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x05b6
            org.telegram.tgnet.TLRPC$Chat r14 = r1.chat
            if (r14 == 0) goto L_0x0510
            boolean r8 = r14.scam
            if (r8 == 0) goto L_0x0421
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r8.checkText()
            goto L_0x0431
        L_0x0421:
            boolean r8 = r14.fake
            if (r8 == 0) goto L_0x042d
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r8.checkText()
            goto L_0x0431
        L_0x042d:
            boolean r8 = r14.verified
            r1.drawVerified = r8
        L_0x0431:
            boolean r8 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r8 == 0) goto L_0x05b6
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x04a7
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x043e
            goto L_0x04a7
        L_0x043e:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r14 = r8.id
            if (r14 < 0) goto L_0x045c
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0451
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x0451
            goto L_0x045c
        L_0x0451:
            r1.drawNameGroup = r5
            r8 = 1099694080(0x418CLASSNAME, float:17.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            goto L_0x0466
        L_0x045c:
            r1.drawNameBroadcast = r5
            r8 = 1099169792(0x41840000, float:16.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
        L_0x0466:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x0486
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x047b
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x047d
        L_0x047b:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x047d:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x0486:
            int r8 = r44.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r8 = r8 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x0496
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0498
        L_0x0496:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0498:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x04a7:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r14 = r8.id
            if (r14 < 0) goto L_0x04c5
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x04ba
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x04ba
            goto L_0x04c5
        L_0x04ba:
            r1.drawNameGroup = r5
            r8 = 1096286208(0x41580000, float:13.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            goto L_0x04cf
        L_0x04c5:
            r1.drawNameBroadcast = r5
            r8 = 1095237632(0x41480000, float:12.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
        L_0x04cf:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x04ef
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x04e4
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e6
        L_0x04e4:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e6:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x04ef:
            int r8 = r44.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r8 = r8 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x04ff
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0501
        L_0x04ff:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0501:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x0510:
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            if (r8 == 0) goto L_0x05b6
            boolean r14 = r8.scam
            if (r14 == 0) goto L_0x0520
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r8.checkText()
            goto L_0x0530
        L_0x0520:
            boolean r14 = r8.fake
            if (r14 == 0) goto L_0x052c
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r8.checkText()
            goto L_0x0530
        L_0x052c:
            boolean r8 = r8.verified
            r1.drawVerified = r8
        L_0x0530:
            boolean r8 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r8 == 0) goto L_0x05b6
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = r8.bot
            if (r8 == 0) goto L_0x05b6
            r1.drawNameBot = r5
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x057e
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x0545
            goto L_0x057e
        L_0x0545:
            r8 = 1099169792(0x41840000, float:16.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x0565
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x0565:
            int r8 = r44.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r8 = r8 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x057e:
            r8 = 1095237632(0x41480000, float:12.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x059e
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b6
        L_0x059e:
            int r8 = r44.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r8 = r8 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r8
        L_0x05b6:
            int r8 = r1.lastMessageDate
            if (r8 != 0) goto L_0x05c2
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 == 0) goto L_0x05c2
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            int r8 = r8.date
        L_0x05c2:
            boolean r14 = r1.isDialogCell
            if (r14 == 0) goto L_0x061b
            int r14 = r1.currentAccount
            org.telegram.messenger.MediaDataController r14 = org.telegram.messenger.MediaDataController.getInstance(r14)
            long r2 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r14.getDraft(r2, r6)
            r1.draftMessage = r2
            if (r2 == 0) goto L_0x05f1
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x05e7
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x05e5
            goto L_0x05e7
        L_0x05e5:
            r2 = 0
            goto L_0x0618
        L_0x05e7:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            int r2 = r2.date
            if (r8 <= r2) goto L_0x05f1
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x05e5
        L_0x05f1:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x060b
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x060b
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x060b
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x05e5
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x05e5
        L_0x060b:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x061e
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x05e5
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x061e
            goto L_0x05e5
        L_0x0618:
            r1.draftMessage = r2
            goto L_0x061e
        L_0x061b:
            r2 = 0
            r1.draftMessage = r2
        L_0x061e:
            if (r0 == 0) goto L_0x0679
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r8, r6)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0646
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x0647
        L_0x0646:
            r2 = 0
        L_0x0647:
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>()
            java.lang.String r8 = " "
            android.text.SpannableStringBuilder r8 = r3.append(r8)
            java.lang.String[] r9 = new java.lang.String[r5]
            java.lang.String r11 = "..."
            r9[r6] = r11
            java.lang.String[] r11 = new java.lang.String[r5]
            r11[r6] = r4
            java.lang.CharSequence r0 = android.text.TextUtils.replace(r0, r9, r11)
            android.text.SpannableStringBuilder r0 = r8.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r8 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r8.<init>(r2)
            r0.setSpan(r8, r6, r5, r6)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 0
        L_0x0673:
            r2 = 2
            r5 = 0
        L_0x0675:
            r6 = 1
            r8 = 0
            goto L_0x0ea6
        L_0x0679:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0707
            r0 = 2131625204(0x7f0e04f4, float:1.887761E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x06b3
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x06b1
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x069c
            goto L_0x06b1
        L_0x069c:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r8 = "chats_draft"
            r2.<init>(r8)
            int r8 = r0.length()
            r11 = 33
            r3.setSpan(r2, r6, r8, r11)
            goto L_0x0673
        L_0x06b1:
            r3 = r4
            goto L_0x0673
        L_0x06b3:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r3 = r2.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r3 <= r8) goto L_0x06c3
            java.lang.String r2 = r2.substring(r6, r8)
        L_0x06c3:
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            r3 = 32
            r11 = 10
            java.lang.String r2 = r2.replace(r11, r3)
            r8[r6] = r2
            r8[r5] = r0
            java.lang.String r2 = java.lang.String.format(r12, r8)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x06f3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x06f3
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r8 = "chats_draft"
            r3.<init>(r8)
            int r8 = r0.length()
            int r8 = r8 + r5
            r11 = 33
            r2.setSpan(r3, r6, r8, r11)
        L_0x06f3:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r3 = r3[r8]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r8, r6)
            goto L_0x0673
        L_0x0707:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x071e
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625741(0x7f0e070d, float:1.8878699E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x071a:
            r0 = 0
            r2 = 2
            goto L_0x0675
        L_0x071e:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x07af
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x0790
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x073a
            r0 = 2131625299(0x7f0e0553, float:1.8877802E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071a
        L_0x073a:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0752
            r0 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x071a
        L_0x0752:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x0760
            r0 = 2131625300(0x7f0e0554, float:1.8877804E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071a
        L_0x0760:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x07ac
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x0786
            r0 = 2131625288(0x7f0e0548, float:1.887778E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x071a
        L_0x0786:
            r0 = 2131625287(0x7f0e0547, float:1.8877778E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071a
        L_0x0790:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x07ac
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x07ac
            r0 = 2131627220(0x7f0e0cd4, float:1.8881698E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r8 = 0
            r10 = 0
            goto L_0x0ea6
        L_0x07ac:
            r3 = r4
            goto L_0x071a
        L_0x07af:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            org.telegram.messenger.MessageObject r2 = r1.message
            int r2 = r2.getFromChatId()
            if (r2 <= 0) goto L_0x07cf
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            r3 = 0
            goto L_0x07e0
        L_0x07cf:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            r3 = r2
            r2 = 0
        L_0x07e0:
            int r8 = r1.dialogsType
            r14 = 3
            if (r8 != r14) goto L_0x07ff
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x07ff
            r0 = 2131627220(0x7f0e0cd4, float:1.8881698E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r0 = 0
            r2 = 2
            r5 = 0
            r6 = 1
            r8 = 0
            r10 = 0
            goto L_0x0e99
        L_0x07ff:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x0815
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x0815
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x0815
            java.lang.CharSequence r0 = r44.formatArchivedDialogNames()
            r3 = r0
            r0 = 0
            r2 = 2
        L_0x0812:
            r8 = 0
            goto L_0x0e99
        L_0x0815:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r8 == 0) goto L_0x0845
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0836
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x0833
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0836
        L_0x0833:
            r0 = r4
            r10 = 0
            goto L_0x083a
        L_0x0836:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x083a:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            r3 = r0
            r0 = 0
            r2 = 2
        L_0x0843:
            r6 = 1
            goto L_0x0812
        L_0x0845:
            boolean r8 = android.text.TextUtils.isEmpty(r0)
            if (r8 == 0) goto L_0x0946
            int r8 = r1.currentDialogFolderId
            if (r8 != 0) goto L_0x0946
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r1.encryptedChat
            if (r8 != 0) goto L_0x0946
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.needDrawBluredPreview()
            if (r8 != 0) goto L_0x0946
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 != 0) goto L_0x0873
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isNewGif()
            if (r8 != 0) goto L_0x0873
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x0946
        L_0x0873:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isWebpage()
            if (r8 == 0) goto L_0x0886
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r8 = r8.type
            goto L_0x0887
        L_0x0886:
            r8 = 0
        L_0x0887:
            java.lang.String r14 = "app"
            boolean r14 = r14.equals(r8)
            if (r14 != 0) goto L_0x0946
            java.lang.String r14 = "profile"
            boolean r14 = r14.equals(r8)
            if (r14 != 0) goto L_0x0946
            java.lang.String r14 = "article"
            boolean r14 = r14.equals(r8)
            if (r14 != 0) goto L_0x0946
            if (r8 == 0) goto L_0x08a9
            java.lang.String r14 = "telegram_"
            boolean r8 = r8.startsWith(r14)
            if (r8 != 0) goto L_0x0946
        L_0x08a9:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r14 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r14)
            org.telegram.messenger.MessageObject r14 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.photoThumbs
            int r15 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15)
            if (r8 != r14) goto L_0x08c2
            r14 = 0
        L_0x08c2:
            if (r8 == 0) goto L_0x0946
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isVideo()
            r1.drawPlay = r15
            java.lang.String r15 = org.telegram.messenger.FileLoader.getAttachFileName(r14)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.mediaExists
            if (r6 != 0) goto L_0x0911
            int r6 = r1.currentAccount
            org.telegram.messenger.DownloadController r6 = org.telegram.messenger.DownloadController.getInstance(r6)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r6.canDownloadMedia((org.telegram.messenger.MessageObject) r5)
            if (r5 != 0) goto L_0x0911
            int r5 = r1.currentAccount
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r5)
            boolean r5 = r5.isLoadingFile(r15)
            if (r5 == 0) goto L_0x08f3
            goto L_0x0911
        L_0x08f3:
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            r25 = 0
            r26 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r8, r6)
            r29 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            r31 = 0
            java.lang.String r28 = "20_20"
            r24 = r5
            r30 = r6
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (java.lang.String) r29, (java.lang.Object) r30, (int) r31)
            goto L_0x0944
        L_0x0911:
            org.telegram.messenger.MessageObject r5 = r1.message
            int r6 = r5.type
            r15 = 1
            if (r6 != r15) goto L_0x0921
            if (r14 == 0) goto L_0x091d
            int r6 = r14.size
            goto L_0x091e
        L_0x091d:
            r6 = 0
        L_0x091e:
            r29 = r6
            goto L_0x0923
        L_0x0921:
            r29 = 0
        L_0x0923:
            org.telegram.messenger.ImageReceiver r6 = r1.thumbImage
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForObject(r14, r5)
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r8, r5)
            r30 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            r32 = 0
            java.lang.String r26 = "20_20"
            java.lang.String r28 = "20_20"
            r24 = r6
            r31 = r5
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x0944:
            r5 = 0
            goto L_0x0947
        L_0x0946:
            r5 = 1
        L_0x0947:
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            if (r6 == 0) goto L_0x0c4d
            int r8 = r6.id
            if (r8 <= 0) goto L_0x0c4d
            if (r3 != 0) goto L_0x0c4d
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x095f
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0c4d
        L_0x095f:
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L_0x0971
            r2 = 2131625643(0x7f0e06ab, float:1.88785E38)
            java.lang.String r3 = "FromYou"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x09c3
        L_0x0971:
            org.telegram.messenger.MessageObject r6 = r1.message
            if (r6 == 0) goto L_0x0981
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0981
            java.lang.String r6 = r6.from_name
            if (r6 == 0) goto L_0x0981
            r2 = r6
            goto L_0x09c3
        L_0x0981:
            if (r2 == 0) goto L_0x09b6
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0997
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x098c
            goto L_0x0997
        L_0x098c:
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            java.lang.String r3 = "\n"
            java.lang.String r2 = r2.replace(r3, r4)
            goto L_0x09c3
        L_0x0997:
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r2)
            if (r3 == 0) goto L_0x09a7
            r2 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.String r3 = "HiddenName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x09c3
        L_0x09a7:
            java.lang.String r3 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r3, r2)
            java.lang.String r3 = "\n"
            java.lang.String r2 = r2.replace(r3, r4)
            goto L_0x09c3
        L_0x09b6:
            if (r3 == 0) goto L_0x09c1
            java.lang.String r2 = r3.title
            java.lang.String r3 = "\n"
            java.lang.String r2 = r2.replace(r3, r4)
            goto L_0x09c3
        L_0x09c1:
            java.lang.String r2 = "DELETED"
        L_0x09c3:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x09dd
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r3 = 0
            r5[r3] = r0
            r6 = 1
            r5[r6] = r2
            java.lang.String r0 = java.lang.String.format(r12, r5)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
        L_0x09da:
            r3 = r0
            goto L_0x0bc5
        L_0x09dd:
            r3 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r6 = r0.caption
            if (r6 == 0) goto L_0x0a50
            java.lang.String r0 = r6.toString()
            int r6 = r0.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r6 <= r8) goto L_0x09f4
            java.lang.String r0 = r0.substring(r3, r8)
        L_0x09f4:
            if (r5 != 0) goto L_0x09f9
            r3 = r4
        L_0x09f7:
            r5 = 2
            goto L_0x0a28
        L_0x09f9:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x0a04
            java.lang.String r3 = "📹 "
            goto L_0x09f7
        L_0x0a04:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x0a0f
            java.lang.String r3 = "🎤 "
            goto L_0x09f7
        L_0x0a0f:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x0a1a
            java.lang.String r3 = "🎧 "
            goto L_0x09f7
        L_0x0a1a:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x0a25
            java.lang.String r3 = "🖼 "
            goto L_0x09f7
        L_0x0a25:
            java.lang.String r3 = "📎 "
            goto L_0x09f7
        L_0x0a28:
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r3 = 32
            r8 = 10
            java.lang.String r0 = r0.replace(r8, r3)
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            r3 = 0
            r6[r3] = r0
            r3 = 1
            r6[r3] = r2
            java.lang.String r0 = java.lang.String.format(r12, r6)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09da
        L_0x0a50:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            if (r3 == 0) goto L_0x0b3e
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0b3e
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r0[r3]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0a94
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            r0 = 18
            if (r11 < r0) goto L_0x0a83
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r6 = 0
            r0[r6] = r3
            java.lang.String r3 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abb
        L_0x0a83:
            r5 = 1
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abb
        L_0x0a94:
            r5 = 1
            r6 = 0
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0ac1
            r8 = 18
            if (r11 < r8) goto L_0x0aad
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abb
        L_0x0aad:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
        L_0x0abb:
            r3 = 32
            r5 = 10
            r8 = 1
            goto L_0x0b08
        L_0x0ac1:
            int r3 = r0.type
            r5 = 14
            if (r3 != r5) goto L_0x0afd
            r3 = 18
            if (r11 < r3) goto L_0x0ae4
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8 = 1
            r5[r8] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0b04
        L_0x0ae4:
            r3 = 2
            r8 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r5[r8] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0b04
        L_0x0afd:
            r8 = 1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0b04:
            r3 = 32
            r5 = 10
        L_0x0b08:
            java.lang.String r0 = r0.replace(r5, r3)
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r3 = 0
            r5[r3] = r0
            r5[r8] = r2
            java.lang.String r0 = java.lang.String.format(r12, r5)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r5 = "chats_attachMessage"
            r0.<init>(r5)     // Catch:{ Exception -> 0x0b38 }
            if (r13 == 0) goto L_0x0b2c
            int r5 = r2.length()     // Catch:{ Exception -> 0x0b38 }
            r6 = 2
            int r5 = r5 + r6
            goto L_0x0b2d
        L_0x0b2c:
            r5 = 0
        L_0x0b2d:
            int r6 = r3.length()     // Catch:{ Exception -> 0x0b38 }
            r8 = 33
            r3.setSpan(r0, r5, r6, r8)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0bc5
        L_0x0b38:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0bc5
        L_0x0b3e:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0bbf
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b94
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b53
            r3 = r0
        L_0x0b53:
            int r0 = r44.getMeasuredWidth()
            r5 = 1121058816(0x42d20000, float:105.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            if (r13 == 0) goto L_0x0b7a
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 != 0) goto L_0x0b71
            float r0 = (float) r0
            java.lang.String r5 = r2.toString()
            float r5 = r9.measureText(r5)
            float r0 = r0 - r5
            int r0 = (int) r0
        L_0x0b71:
            float r0 = (float) r0
            java.lang.String r5 = ": "
            float r5 = r9.measureText(r5)
            float r0 = r0 - r5
            int r0 = (int) r0
        L_0x0b7a:
            if (r0 <= 0) goto L_0x0b92
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r3, r5, r0, r9, r8)
            java.lang.String r3 = r0.toString()
            goto L_0x0bad
        L_0x0b92:
            r6 = 0
            goto L_0x0bad
        L_0x0b94:
            r6 = 0
            int r0 = r3.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r0 <= r5) goto L_0x0ba1
            java.lang.String r3 = r3.substring(r6, r5)
        L_0x0ba1:
            r5 = 32
            r8 = 10
            java.lang.String r0 = r3.replace(r8, r5)
            java.lang.String r3 = r0.trim()
        L_0x0bad:
            r5 = 2
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r6] = r3
            r3 = 1
            r0[r3] = r2
            java.lang.String r0 = java.lang.String.format(r12, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09da
        L_0x0bbf:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            goto L_0x09da
        L_0x0bc5:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0bcd
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0bd7
        L_0x0bcd:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0bf5
            int r0 = r3.length()
            if (r0 <= 0) goto L_0x0bf5
        L_0x0bd7:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bee }
            java.lang.String r5 = "chats_nameMessage"
            r0.<init>(r5)     // Catch:{ Exception -> 0x0bee }
            int r5 = r2.length()     // Catch:{ Exception -> 0x0bee }
            r6 = 1
            int r5 = r5 + r6
            r6 = 33
            r8 = 0
            r3.setSpan(r0, r8, r5, r6)     // Catch:{ Exception -> 0x0bec }
            r0 = r5
            goto L_0x0bf7
        L_0x0bec:
            r0 = move-exception
            goto L_0x0bf0
        L_0x0bee:
            r0 = move-exception
            r5 = 0
        L_0x0bf0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0bf7
        L_0x0bf5:
            r0 = 0
            r5 = 0
        L_0x0bf7:
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r6 = r6[r8]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r6, r8, r11)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.hasHighlightedWords()
            if (r6 == 0) goto L_0x0c1d
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r6)
            if (r6 == 0) goto L_0x0c1d
            r3 = r6
        L_0x0c1d:
            boolean r6 = r1.hasMessageThumb
            if (r6 == 0) goto L_0x0CLASSNAME
            boolean r6 = r3 instanceof android.text.SpannableStringBuilder
            if (r6 != 0) goto L_0x0c2b
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            r3 = r6
        L_0x0c2b:
            r6 = r3
            android.text.SpannableStringBuilder r6 = (android.text.SpannableStringBuilder) r6
            java.lang.String r8 = " "
            r6.insert(r5, r8)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r8 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r11 = r7 + 6
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r8.<init>(r11)
            int r11 = r5 + 1
            r12 = 33
            r6.setSpan(r8, r5, r11, r12)
        L_0x0CLASSNAME:
            r8 = r0
            r0 = r2
            r2 = 2
            r5 = 1
            r6 = 0
            goto L_0x0e99
        L_0x0c4d:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 2
            goto L_0x0df2
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Photo r3 = r2.photo
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r3 == 0) goto L_0x0CLASSNAME
            int r3 = r2.ttl_seconds
            if (r3 == 0) goto L_0x0CLASSNAME
            r0 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x0c8c
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r3 == 0) goto L_0x0c8c
            int r3 = r2.ttl_seconds
            if (r3 == 0) goto L_0x0c8c
            r0 = 2131624398(0x7f0e01ce, float:1.8875975E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0CLASSNAME
        L_0x0c8c:
            java.lang.CharSequence r3 = r0.caption
            if (r3 == 0) goto L_0x0d3d
            if (r5 != 0) goto L_0x0CLASSNAME
            r0 = r4
            goto L_0x0cc0
        L_0x0CLASSNAME:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0c9d
            java.lang.String r0 = "📹 "
            goto L_0x0cc0
        L_0x0c9d:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0ca8
            java.lang.String r0 = "🎤 "
            goto L_0x0cc0
        L_0x0ca8:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0cb3
            java.lang.String r0 = "🎧 "
            goto L_0x0cc0
        L_0x0cb3:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0cbe
            java.lang.String r0 = "🖼 "
            goto L_0x0cc0
        L_0x0cbe:
            java.lang.String r0 = "📎 "
        L_0x0cc0:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0d28
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d28
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r44.getMeasuredWidth()
            r5 = 1122893824(0x42ee0000, float:119.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 - r5
            if (r13 == 0) goto L_0x0d00
            r5 = 0
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x0cf7
            float r3 = (float) r3
            java.lang.String r6 = r5.toString()
            float r5 = r9.measureText(r6)
            float r3 = r3 - r5
            int r3 = (int) r3
        L_0x0cf7:
            float r3 = (float) r3
            java.lang.String r5 = ": "
            float r5 = r9.measureText(r5)
            float r3 = r3 - r5
            int r3 = (int) r3
        L_0x0d00:
            if (r3 <= 0) goto L_0x0d17
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r5, r3, r9, r6)
            java.lang.String r2 = r2.toString()
        L_0x0d17:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0CLASSNAME
        L_0x0d28:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0CLASSNAME
        L_0x0d3d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0d5b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "📊 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0d58:
            r2 = 2
            goto L_0x0dde
        L_0x0d5b:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0d7b
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
            goto L_0x0d58
        L_0x0d7b:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0d9b
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r5 = 0
            r3[r5] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0dde
        L_0x0d9b:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0dd3
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0dd3
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r44.getMeasuredWidth()
            r5 = 1119748096(0x42be0000, float:95.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 - r5
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r6 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r5, r3, r9, r6)
            java.lang.String r0 = r0.toString()
            goto L_0x0dd7
        L_0x0dd3:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0dd7:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0dde:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            if (r5 == 0) goto L_0x0df2
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0df2
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r9 = r3[r5]
        L_0x0df2:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0e94
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0e32
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0e32
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r44.getMeasuredWidth()
            int r5 = r7 + 95
            int r5 = r5 + 6
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 - r5
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r5, r3, r9, r8)
            java.lang.String r0 = r0.toString()
            goto L_0x0e43
        L_0x0e32:
            r6 = 0
            int r3 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x0e3f
            java.lang.CharSequence r0 = r0.subSequence(r6, r5)
        L_0x0e3f:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e43:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0e4d
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0e4d:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r5 = " "
            r6 = 0
            r3.insert(r6, r5)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r5 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r7 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r5.<init>(r8)
            r8 = 33
            r11 = 1
            r3.setSpan(r5, r6, r11, r8)
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r5 = r5[r8]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r8 = 1099431936(0x41880000, float:17.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r5, r11, r6)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0e8e
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r5)
            if (r3 == 0) goto L_0x0e8e
            goto L_0x0e8f
        L_0x0e8e:
            r3 = r0
        L_0x0e8f:
            r0 = 0
            r5 = 1
            r6 = 0
            goto L_0x0812
        L_0x0e94:
            r3 = r0
            r0 = 0
            r5 = 1
            goto L_0x0843
        L_0x0e99:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0ea1
            java.lang.CharSequence r0 = r44.formatArchivedDialogNames()
        L_0x0ea1:
            r43 = r6
            r6 = r5
            r5 = r43
        L_0x0ea6:
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            if (r11 == 0) goto L_0x0eb2
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ecb
        L_0x0eb2:
            int r11 = r1.lastMessageDate
            if (r11 == 0) goto L_0x0ebc
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ecb
        L_0x0ebc:
            org.telegram.messenger.MessageObject r11 = r1.message
            if (r11 == 0) goto L_0x0eca
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ecb
        L_0x0eca:
            r11 = r4
        L_0x0ecb:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 != 0) goto L_0x0ee1
            r13 = 0
            r1.drawCheck1 = r13
            r1.drawCheck2 = r13
            r1.drawClock = r13
            r1.drawCount = r13
            r1.drawMention = r13
            r1.drawError = r13
            r2 = 0
            r10 = 0
            r12 = 0
            goto L_0x0fe5
        L_0x0ee1:
            r13 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0var_
            int r12 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r12 + r14
            if (r15 <= 0) goto L_0x0f1c
            if (r12 <= r14) goto L_0x0var_
            r15 = 1
            r1.drawCount = r15
            r1.drawMention = r13
            java.lang.Object[] r2 = new java.lang.Object[r15]
            int r12 = r12 + r14
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r2[r13] = r12
            java.lang.String r12 = "%d"
            java.lang.String r2 = java.lang.String.format(r12, r2)
            goto L_0x0var_
        L_0x0var_:
            r15 = 1
            r1.drawCount = r13
            r1.drawMention = r15
            java.lang.Object[] r2 = new java.lang.Object[r15]
            int r12 = r12 + r14
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r2[r13] = r12
            java.lang.String r12 = "%d"
            java.lang.String r2 = java.lang.String.format(r12, r2)
            r12 = r2
            r2 = 0
            goto L_0x0f6d
        L_0x0f1c:
            r1.drawCount = r13
            r1.drawMention = r13
            r2 = 0
        L_0x0var_:
            r12 = 0
            goto L_0x0f6d
        L_0x0var_:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0f2e
            r1.drawCount = r13
            r2 = 0
            r10 = 0
            r12 = 1
            r14 = 0
            goto L_0x0var_
        L_0x0f2e:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0var_
            r13 = 1
            if (r2 != r13) goto L_0x0var_
            int r13 = r1.mentionCount
            if (r2 != r13) goto L_0x0var_
            if (r12 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            boolean r12 = r12.mentioned
            if (r12 != 0) goto L_0x0var_
        L_0x0var_:
            r12 = 1
            r1.drawCount = r12
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r13[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r13)
            goto L_0x0var_
        L_0x0var_:
            r12 = 1
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0f5e
            r1.drawCount = r12
            r2 = r4
            goto L_0x0var_
        L_0x0f5e:
            r1.drawCount = r14
            r2 = 0
        L_0x0var_:
            int r13 = r1.mentionCount
            if (r13 == 0) goto L_0x0f6a
            r1.drawMention = r12
            java.lang.String r12 = "@"
            goto L_0x0f6d
        L_0x0f6a:
            r1.drawMention = r14
            goto L_0x0var_
        L_0x0f6d:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isOut()
            if (r13 == 0) goto L_0x0fdc
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 != 0) goto L_0x0fdc
            if (r10 == 0) goto L_0x0fdc
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r13 != 0) goto L_0x0fdc
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0var_
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r13 = 1
            r1.drawClock = r13
            r1.drawError = r10
            goto L_0x0fe5
        L_0x0var_:
            r10 = 0
            r13 = 1
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x0fad
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r13
            r1.drawCount = r10
            r1.drawMention = r10
            goto L_0x0fe5
        L_0x0fad:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x0fda
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x0fce
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0fcc
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0fcc
            goto L_0x0fce
        L_0x0fcc:
            r10 = 0
            goto L_0x0fcf
        L_0x0fce:
            r10 = 1
        L_0x0fcf:
            r1.drawCheck1 = r10
            r10 = 1
            r1.drawCheck2 = r10
            r10 = 0
            r1.drawClock = r10
            r1.drawError = r10
            goto L_0x0fe5
        L_0x0fda:
            r10 = 0
            goto L_0x0fe5
        L_0x0fdc:
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r10
        L_0x0fe5:
            r1.promoDialog = r10
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r13 = r1.dialogsType
            if (r13 != 0) goto L_0x1042
            long r13 = r1.currentDialogId
            r15 = 1
            boolean r13 = r10.isPromoDialog(r13, r15)
            if (r13 == 0) goto L_0x1042
            r1.drawPinBackground = r15
            r1.promoDialog = r15
            int r13 = r10.promoDialogType
            if (r13 != 0) goto L_0x100d
            r10 = 2131627797(0x7f0e0var_, float:1.8882869E38)
            java.lang.String r11 = "UseProxySponsor"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r11 = r10
            goto L_0x1042
        L_0x100d:
            if (r13 != r15) goto L_0x1042
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "PsaType_"
            r11.append(r13)
            java.lang.String r13 = r10.promoPsaType
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11)
            boolean r13 = android.text.TextUtils.isEmpty(r11)
            if (r13 == 0) goto L_0x1035
            r11 = 2131627026(0x7f0e0CLASSNAME, float:1.8881305E38)
            java.lang.String r13 = "PsaTypeDefault"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
        L_0x1035:
            java.lang.String r13 = r10.promoPsaMessage
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 != 0) goto L_0x1042
            java.lang.String r3 = r10.promoPsaMessage
            r10 = 0
            r1.hasMessageThumb = r10
        L_0x1042:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x1058
            r10 = 2131624304(0x7f0e0170, float:1.8875784E38)
            java.lang.String r13 = "ArchivedChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
        L_0x104f:
            r16 = r5
            r5 = r9
            r9 = r12
            r12 = r2
            r2 = r0
            r0 = r6
            r6 = r8
            goto L_0x10b4
        L_0x1058:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x1060
            java.lang.String r10 = r10.title
        L_0x105e:
            r13 = r10
            goto L_0x10a4
        L_0x1060:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            if (r10 == 0) goto L_0x10a3
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r10)
            if (r10 == 0) goto L_0x1074
            r10 = 2131627108(0x7f0e0CLASSNAME, float:1.8881471E38)
            java.lang.String r13 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x105e
        L_0x1074:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x109c
            boolean r10 = r1.useMeForMyMessages
            if (r10 == 0) goto L_0x108a
            r10 = 2131625643(0x7f0e06ab, float:1.88785E38)
            java.lang.String r13 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x105e
        L_0x108a:
            int r10 = r1.dialogsType
            r13 = 3
            if (r10 != r13) goto L_0x1092
            r10 = 1
            r1.drawPinBackground = r10
        L_0x1092:
            r10 = 2131627219(0x7f0e0cd3, float:1.8881696E38)
            java.lang.String r13 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x105e
        L_0x109c:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x105e
        L_0x10a3:
            r13 = r4
        L_0x10a4:
            int r10 = r13.length()
            if (r10 != 0) goto L_0x104f
            r10 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.String r13 = "HiddenName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x104f
        L_0x10b4:
            if (r0 == 0) goto L_0x10f5
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r11)
            double r14 = (double) r0
            double r14 = java.lang.Math.ceil(r14)
            int r0 = (int) r14
            android.text.StaticLayout r8 = new android.text.StaticLayout
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r8
            r25 = r11
            r27 = r0
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.timeLayout = r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x10ec
            int r8 = r44.getMeasuredWidth()
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            int r8 = r8 - r0
            r1.timeLeft = r8
            goto L_0x10fc
        L_0x10ec:
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeLeft = r8
            goto L_0x10fc
        L_0x10f5:
            r8 = 0
            r1.timeLayout = r8
            r8 = 0
            r1.timeLeft = r8
            r0 = 0
        L_0x10fc:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x1110
            int r8 = r44.getMeasuredWidth()
            int r10 = r1.nameLeft
            int r8 = r8 - r10
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            int r8 = r8 - r0
            goto L_0x1124
        L_0x1110:
            int r8 = r44.getMeasuredWidth()
            int r10 = r1.nameLeft
            int r8 = r8 - r10
            r10 = 1117388800(0x429a0000, float:77.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            int r8 = r8 - r0
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
        L_0x1124:
            boolean r10 = r1.drawNameLock
            if (r10 == 0) goto L_0x1137
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
        L_0x1134:
            int r10 = r10 + r11
            int r8 = r8 - r10
            goto L_0x116a
        L_0x1137:
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x1148
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x1134
        L_0x1148:
            boolean r10 = r1.drawNameBroadcast
            if (r10 == 0) goto L_0x1159
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x1134
        L_0x1159:
            boolean r10 = r1.drawNameBot
            if (r10 == 0) goto L_0x116a
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x1134
        L_0x116a:
            boolean r10 = r1.drawClock
            r11 = 1084227584(0x40a00000, float:5.0)
            if (r10 == 0) goto L_0x1198
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r14
            int r8 = r8 - r10
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x1187
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.clockDrawLeft = r0
            goto L_0x120d
        L_0x1187:
            int r14 = r1.timeLeft
            int r14 = r14 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r0
            r1.clockDrawLeft = r14
            int r0 = r1.nameLeft
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x120d
        L_0x1198:
            boolean r10 = r1.drawCheck2
            if (r10 == 0) goto L_0x120d
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r10 = r10.getIntrinsicWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r14
            int r8 = r8 - r10
            boolean r14 = r1.drawCheck1
            if (r14 == 0) goto L_0x11f4
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r14 = r14.getIntrinsicWidth()
            r15 = 1090519040(0x41000000, float:8.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 - r15
            int r8 = r8 - r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x11cd
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.halfCheckDrawLeft = r0
            r10 = 1085276160(0x40b00000, float:5.5)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 - r10
            r1.checkDrawLeft = r0
            goto L_0x120d
        L_0x11cd:
            int r14 = r1.timeLeft
            int r14 = r14 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r0
            r1.checkDrawLeft = r14
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r14 = r14 + r0
            r1.halfCheckDrawLeft = r14
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r11 = r11.getIntrinsicWidth()
            int r10 = r10 + r11
            r11 = 1090519040(0x41000000, float:8.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x120d
        L_0x11f4:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x11fe
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.checkDrawLeft1 = r0
            goto L_0x120d
        L_0x11fe:
            int r14 = r1.timeLeft
            int r14 = r14 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r0
            r1.checkDrawLeft1 = r14
            int r0 = r1.nameLeft
            int r0 = r0 + r10
            r1.nameLeft = r0
        L_0x120d:
            boolean r0 = r1.dialogMuted
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1231
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x1231
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x1231
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x126c
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
            goto L_0x126c
        L_0x1231:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x124b
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x126c
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
            goto L_0x126c
        L_0x124b:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x126c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r10 = r1.drawScam
            r11 = 1
            if (r10 != r11) goto L_0x125b
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x125d
        L_0x125b:
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x125d:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x126c
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
        L_0x126c:
            r32 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r15 = java.lang.Math.max(r0, r8)
            r8 = 32
            r10 = 10
            java.lang.String r0 = r13.replace(r10, r8)     // Catch:{ Exception -> 0x12c4 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x12c4 }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x12c4 }
            r8 = r8[r10]     // Catch:{ Exception -> 0x12c4 }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r32)     // Catch:{ Exception -> 0x12c4 }
            int r10 = r15 - r10
            float r10 = (float) r10     // Catch:{ Exception -> 0x12c4 }
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x12c4 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r8, r10, r11)     // Catch:{ Exception -> 0x12c4 }
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x12c4 }
            if (r8 == 0) goto L_0x12a8
            boolean r8 = r8.hasHighlightedWords()     // Catch:{ Exception -> 0x12c4 }
            if (r8 == 0) goto L_0x12a8
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x12c4 }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x12c4 }
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8)     // Catch:{ Exception -> 0x12c4 }
            if (r8 == 0) goto L_0x12a8
            r25 = r8
            goto L_0x12aa
        L_0x12a8:
            r25 = r0
        L_0x12aa:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x12c4 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x12c4 }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x12c4 }
            r26 = r8[r10]     // Catch:{ Exception -> 0x12c4 }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x12c4 }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r27 = r15
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x12c4 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x12c4 }
            goto L_0x12c8
        L_0x12c4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x12c8:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x137b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x12d2
            goto L_0x137b
        L_0x12d2:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8 = 1106771968(0x41var_, float:31.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.messageNameTop = r8
            r8 = 1098907648(0x41800000, float:16.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeTop = r8
            r8 = 1109131264(0x421CLASSNAME, float:39.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.errorTop = r8
            r8 = 1109131264(0x421CLASSNAME, float:39.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.pinTop = r8
            r8 = 1109131264(0x421CLASSNAME, float:39.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.countTop = r8
            r8 = 1099431936(0x41880000, float:17.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.checkDrawTop = r10
            int r8 = r44.getMeasuredWidth()
            r10 = 1119748096(0x42be0000, float:95.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x1334
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r10
            r1.messageLeft = r10
            int r10 = r44.getMeasuredWidth()
            r11 = 1115684864(0x42800000, float:64.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            int r11 = r7 + 11
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r10 - r11
            goto L_0x1349
        L_0x1334:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r10
            r1.messageLeft = r10
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r11 = 1116078080(0x42860000, float:67.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r10
        L_0x1349:
            org.telegram.messenger.ImageReceiver r13 = r1.avatarImage
            float r10 = (float) r10
            float r14 = (float) r0
            r17 = 1113063424(0x42580000, float:54.0)
            r20 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r21 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r8 = (float) r8
            r13.setImageCoords(r10, r14, r4, r8)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r8 = (float) r11
            r10 = 1106247680(0x41var_, float:30.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r0
            float r10 = (float) r10
            float r11 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r13 = (float) r13
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r4.setImageCoords(r8, r10, r13, r11)
            goto L_0x1425
        L_0x137b:
            r20 = r4
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
            int r4 = r44.getMeasuredWidth()
            r8 = 1119485952(0x42ba0000, float:93.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r4 - r8
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x13e1
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r44.getMeasuredWidth()
            r10 = 1115947008(0x42840000, float:66.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = r4 - r10
            r10 = 1106771968(0x41var_, float:31.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r4 - r10
            goto L_0x13f6
        L_0x13e1:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10 = 1116340224(0x428a0000, float:69.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r4
        L_0x13f6:
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r4 = (float) r4
            float r13 = (float) r0
            r14 = 1113587712(0x42600000, float:56.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r17 = 1113587712(0x42600000, float:56.0)
            r21 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r8 = (float) r8
            r11.setImageCoords(r4, r13, r14, r8)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r8 = (float) r10
            r10 = 1106771968(0x41var_, float:31.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r0
            float r10 = (float) r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r11 = (float) r11
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r13 = (float) r13
            r4.setImageCoords(r8, r10, r11, r13)
        L_0x1425:
            r4 = r0
            r8 = r21
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x144d
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1445
            int r0 = r44.getMeasuredWidth()
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 - r10
            r1.pinLeft = r0
            goto L_0x144d
        L_0x1445:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x144d:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1481
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r8 = r8 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x146a
            int r0 = r44.getMeasuredWidth()
            r9 = 1107820544(0x42080000, float:34.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.errorLeft = r0
            goto L_0x147c
        L_0x146a:
            r9 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.errorLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x147c:
            r17 = r6
            r6 = r15
            goto L_0x15a0
        L_0x1481:
            if (r12 != 0) goto L_0x14ac
            if (r9 == 0) goto L_0x1486
            goto L_0x14ac
        L_0x1486:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x14a6
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 + r9
            int r8 = r8 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x14a6
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x14a6:
            r9 = 0
            r1.drawCount = r9
            r1.drawMention = r9
            goto L_0x147c
        L_0x14ac:
            if (r12 == 0) goto L_0x150c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r10 = r10.measureText(r12)
            double r10 = (double) r10
            double r10 = java.lang.Math.ceil(r10)
            int r10 = (int) r10
            int r0 = java.lang.Math.max(r0, r10)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r10 = r1.countWidth
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_CENTER
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r25 = r12
            r27 = r10
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 != 0) goto L_0x14f8
            int r0 = r44.getMeasuredWidth()
            int r10 = r1.countWidth
            int r0 = r0 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r10
            r1.countLeft = r0
            goto L_0x1508
        L_0x14f8:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r10
            int r10 = r1.messageLeft
            int r10 = r10 + r0
            r1.messageLeft = r10
            int r10 = r1.messageNameLeft
            int r10 = r10 + r0
            r1.messageNameLeft = r10
        L_0x1508:
            r10 = 1
            r1.drawCount = r10
            goto L_0x150f
        L_0x150c:
            r10 = 0
            r1.countWidth = r10
        L_0x150f:
            r0 = r8
            if (r9 == 0) goto L_0x159c
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x154c
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r32)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r10 = r10.measureText(r9)
            double r10 = (double) r10
            double r10 = java.lang.Math.ceil(r10)
            int r10 = (int) r10
            int r8 = java.lang.Math.max(r8, r10)
            r1.mentionWidth = r8
            android.text.StaticLayout r14 = new android.text.StaticLayout
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r11 = r1.mentionWidth
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_CENTER
            r13 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r21 = 0
            r8 = r14
            r33 = r14
            r14 = r17
            r17 = r6
            r6 = r15
            r15 = r21
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r8 = r33
            r1.mentionLayout = r8
            goto L_0x1555
        L_0x154c:
            r17 = r6
            r6 = r15
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.mentionWidth = r8
        L_0x1555:
            int r8 = r1.mentionWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r8 = r8 + r9
            int r0 = r0 - r8
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x157c
            int r8 = r44.getMeasuredWidth()
            int r9 = r1.mentionWidth
            int r8 = r8 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r8 = r8 - r9
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x1577
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r9 = r9 + r10
            goto L_0x1578
        L_0x1577:
            r9 = 0
        L_0x1578:
            int r8 = r8 - r9
            r1.mentionLeft = r8
            goto L_0x1598
        L_0x157c:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x158a
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r10 = r10 + r11
            goto L_0x158b
        L_0x158a:
            r10 = 0
        L_0x158b:
            int r9 = r9 + r10
            r1.mentionLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r8
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r8
            r1.messageNameLeft = r9
        L_0x1598:
            r8 = 1
            r1.drawMention = r8
            goto L_0x159f
        L_0x159c:
            r17 = r6
            r6 = r15
        L_0x159f:
            r8 = r0
        L_0x15a0:
            if (r16 == 0) goto L_0x15f4
            if (r3 != 0) goto L_0x15a6
            r3 = r20
        L_0x15a6:
            java.lang.String r0 = r3.toString()
            int r3 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r3 <= r9) goto L_0x15b7
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r9)
        L_0x15b7:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x15bf
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x15c1
        L_0x15bf:
            if (r2 == 0) goto L_0x15ca
        L_0x15c1:
            r3 = 32
            r9 = 10
            java.lang.String r0 = r0.replace(r9, r3)
            goto L_0x15d2
        L_0x15ca:
            java.lang.String r3 = "\n\n"
            java.lang.String r9 = "\n"
            java.lang.String r0 = r0.replace(r3, r9)
        L_0x15d2:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r3 = r3[r9]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r9 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r9, r10)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x15f4
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r0)
            if (r0 == 0) goto L_0x15f4
            r3 = r0
        L_0x15f4:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r8 = java.lang.Math.max(r0, r8)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1604
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x165a
        L_0x1604:
            if (r2 == 0) goto L_0x165a
            int r9 = r1.currentDialogFolderId
            if (r9 == 0) goto L_0x160f
            int r9 = r1.currentDialogFolderDialogsCount
            r10 = 1
            if (r9 != r10) goto L_0x165a
        L_0x160f:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x163f }
            if (r0 == 0) goto L_0x1624
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x163f }
            if (r0 == 0) goto L_0x1624
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x163f }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x163f }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x163f }
            if (r0 == 0) goto L_0x1624
            r2 = r0
        L_0x1624:
            android.text.TextPaint r34 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x163f }
            android.text.Layout$Alignment r36 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x163f }
            r37 = 1065353216(0x3var_, float:1.0)
            r38 = 0
            r39 = 0
            android.text.TextUtils$TruncateAt r40 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x163f }
            r42 = 1
            r33 = r2
            r35 = r8
            r41 = r8
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r33, r34, r35, r36, r37, r38, r39, r40, r41, r42)     // Catch:{ Exception -> 0x163f }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x163f }
            goto L_0x1643
        L_0x163f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1643:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r9 = 1109393408(0x42200000, float:40.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r4 = r4 + r9
            float r4 = (float) r4
            r0.setImageY(r4)
            r9 = 0
            goto L_0x1682
        L_0x165a:
            r9 = 0
            r1.messageNameLayout = r9
            if (r0 != 0) goto L_0x166d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1664
            goto L_0x166d
        L_0x1664:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1682
        L_0x166d:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r10 = 1101529088(0x41a80000, float:21.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = r4 + r10
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1682:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x171c }
            if (r0 != 0) goto L_0x168a
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x171c }
            if (r4 == 0) goto L_0x169f
        L_0x168a:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x171c }
            if (r4 == 0) goto L_0x169f
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x171c }
            r10 = 1
            if (r4 <= r10) goto L_0x169f
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x171c }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x171c }
            r5 = r0[r3]     // Catch:{ Exception -> 0x171c }
            r24 = r2
            r25 = r5
            r2 = r9
            goto L_0x16b8
        L_0x169f:
            if (r0 != 0) goto L_0x16a5
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x171c }
            if (r0 == 0) goto L_0x16a7
        L_0x16a5:
            if (r2 == 0) goto L_0x16b4
        L_0x16a7:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)     // Catch:{ Exception -> 0x171c }
            int r0 = r8 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x171c }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x171c }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r5, r0, r4)     // Catch:{ Exception -> 0x171c }
        L_0x16b4:
            r24 = r3
            r25 = r5
        L_0x16b8:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x171c }
            if (r0 != 0) goto L_0x16ee
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x171c }
            if (r0 == 0) goto L_0x16c1
            goto L_0x16ee
        L_0x16c1:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x171c }
            if (r0 == 0) goto L_0x16d9
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x171c }
            int r0 = r0 + r7
            int r8 = r8 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x171c }
            if (r0 == 0) goto L_0x16d9
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x171c }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x171c }
            int r7 = r7 + r2
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x171c }
        L_0x16d9:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x171c }
            android.text.Layout$Alignment r13 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x171c }
            r14 = 1065353216(0x3var_, float:1.0)
            r15 = 0
            r16 = 0
            r9 = r0
            r10 = r24
            r11 = r25
            r12 = r8
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x171c }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x171c }
            goto L_0x1720
        L_0x16ee:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x171c }
            if (r0 == 0) goto L_0x16f9
            if (r2 == 0) goto L_0x16f9
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x171c }
            int r8 = r8 + r0
        L_0x16f9:
            android.text.Layout$Alignment r27 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x171c }
            r28 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x171c }
            float r0 = (float) r0     // Catch:{ Exception -> 0x171c }
            r30 = 0
            android.text.TextUtils$TruncateAt r31 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x171c }
            if (r2 == 0) goto L_0x170d
            r33 = 1
            goto L_0x170f
        L_0x170d:
            r33 = 2
        L_0x170f:
            r26 = r8
            r29 = r0
            r32 = r8
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r24, r25, r26, r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x171c }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x171c }
            goto L_0x1720
        L_0x171c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1720:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1856
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x17df
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17df
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x1770
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x1770
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x1770
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r9
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r7 = r7.getIntrinsicWidth()
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            int r4 = (int) r4
            r1.nameMuteLeft = r4
            goto L_0x17c7
        L_0x1770:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x1798
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r9
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r7 = r7.getIntrinsicWidth()
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            int r4 = (int) r4
            r1.nameMuteLeft = r4
            goto L_0x17c7
        L_0x1798:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x17c7
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r9
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            int r7 = r1.drawScam
            r9 = 1
            if (r7 != r9) goto L_0x17b9
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x17bb
        L_0x17b9:
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x17bb:
            int r7 = r7.getIntrinsicWidth()
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            int r4 = (int) r4
            r1.nameMuteLeft = r4
        L_0x17c7:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x17df
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x17df
            int r0 = r1.nameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.nameLeft = r0
        L_0x17df:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1820
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1820
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x17f0:
            if (r2 >= r0) goto L_0x1816
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x1815
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            double r6 = (double) r8
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r4 = (int) r6
            int r3 = java.lang.Math.min(r3, r4)
            int r2 = r2 + 1
            goto L_0x17f0
        L_0x1815:
            r3 = 0
        L_0x1816:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x1820
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x1820:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x18de
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18de
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x18de
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r8
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x18de
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x18de
        L_0x1856:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x18a3
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18a3
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r6
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x188a
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x188a
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x188a:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1896
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1896
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x18a3
        L_0x1896:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x18a3:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18c6
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18c6
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x18b0:
            if (r3 >= r0) goto L_0x18bf
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x18b0
        L_0x18bf:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x18c6:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x18de
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18de
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x18de:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1924
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1924
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1920 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1920 }
            r8 = r17
            r2 = 1
            if (r8 < r0) goto L_0x18f6
            int r6 = r0 + -1
            goto L_0x18f7
        L_0x18f6:
            r6 = r8
        L_0x18f7:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1920 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1920 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1920 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1920 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1920 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1920 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1920 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1920 }
            if (r0 == 0) goto L_0x1917
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1920 }
            int r0 = r0 + r2
        L_0x1917:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1920 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1920 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1920 }
            goto L_0x1924
        L_0x1920:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1924:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1953
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1953
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x1944
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1953
        L_0x1944:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1953:
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
    public void update(int r22, boolean r23) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
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
            goto L_0x04e1
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
            r21.invalidate()
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
            r21.invalidate()
            return
        L_0x0238:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r7 = 0
            if (r1 == 0) goto L_0x0255
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r21.findFolderTopMessage()
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
            if (r1 == 0) goto L_0x032d
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
            goto L_0x03a8
        L_0x032d:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x038c
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r1 == 0) goto L_0x0353
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
            goto L_0x03a8
        L_0x0353:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x0372
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x0372
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
            goto L_0x03a8
        L_0x0372:
            org.telegram.messenger.ImageReceiver r14 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUser(r1, r5)
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r18 = 0
            org.telegram.tgnet.TLRPC$User r7 = r0.user
            r20 = 0
            java.lang.String r16 = "50_50"
            r17 = r1
            r19 = r7
            r14.setImage(r15, r16, r17, r18, r19, r20)
            goto L_0x03a8
        L_0x038c:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x03a8
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            org.telegram.messenger.ImageReceiver r8 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForChat(r1, r5)
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            org.telegram.tgnet.TLRPC$Chat r13 = r0.chat
            r14 = 0
            java.lang.String r10 = "50_50"
            r8.setImage(r9, r10, r11, r12, r13, r14)
        L_0x03a8:
            if (r23 == 0) goto L_0x04e1
            int r1 = r0.unreadCount
            if (r2 != r1) goto L_0x03b2
            boolean r1 = r0.markUnread
            if (r6 == r1) goto L_0x04e1
        L_0x03b2:
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r0.lastDialogChangedTime
            long r7 = r7 - r9
            r9 = 100
            int r1 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r1 <= 0) goto L_0x04e1
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x03c6
            r1.cancel()
        L_0x03c6:
            float[] r1 = new float[r3]
            r1 = {0, NUM} // fill-array
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.countAnimator = r1
            org.telegram.ui.Cells.-$$Lambda$DialogCell$tmNqj-NiK7sTMFrlcDIRZtThiuY r3 = new org.telegram.ui.Cells.-$$Lambda$DialogCell$tmNqj-NiK7sTMFrlcDIRZtThiuY
            r3.<init>()
            r1.addUpdateListener(r3)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Cells.DialogCell$1 r3 = new org.telegram.ui.Cells.DialogCell$1
            r3.<init>()
            r1.addListener(r3)
            if (r2 == 0) goto L_0x03e9
            boolean r1 = r0.markUnread
            if (r1 == 0) goto L_0x03f0
        L_0x03e9:
            boolean r1 = r0.markUnread
            if (r1 != 0) goto L_0x0412
            if (r6 != 0) goto L_0x03f0
            goto L_0x0412
        L_0x03f0:
            int r1 = r0.unreadCount
            if (r1 != 0) goto L_0x0403
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 150(0x96, double:7.4E-322)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x0423
        L_0x0403:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 430(0x1ae, double:2.124E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x0423
        L_0x0412:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 220(0xdc, double:1.087E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r1.setInterpolator(r3)
        L_0x0423:
            boolean r1 = r0.drawCount
            if (r1 == 0) goto L_0x04cc
            android.text.StaticLayout r1 = r0.countLayout
            if (r1 == 0) goto L_0x04cc
            java.lang.String r1 = java.lang.String.valueOf(r2)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r6 = r1.length()
            int r7 = r3.length()
            if (r6 != r7) goto L_0x04c8
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r1)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r3)
            r8 = 0
        L_0x044f:
            int r10 = r1.length()
            if (r8 >= r10) goto L_0x047f
            char r10 = r1.charAt(r8)
            char r11 = r3.charAt(r8)
            if (r10 != r11) goto L_0x0472
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r9.setSpan(r10, r8, r11, r5)
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            r6.setSpan(r10, r8, r11, r5)
            goto L_0x047c
        L_0x0472:
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r7.setSpan(r10, r8, r11, r5)
        L_0x047c:
            int r8 = r8 + 1
            goto L_0x044f
        L_0x047f:
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
            goto L_0x04cc
        L_0x04c8:
            android.text.StaticLayout r1 = r0.countLayout
            r0.countOldLayout = r1
        L_0x04cc:
            int r1 = r0.countWidth
            r0.countWidthOld = r1
            int r1 = r0.countLeft
            r0.countLeftOld = r1
            int r1 = r0.unreadCount
            if (r1 <= r2) goto L_0x04d9
            goto L_0x04da
        L_0x04d9:
            r4 = 0
        L_0x04da:
            r0.countAnimationIncrement = r4
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x04e1:
            int r1 = r21.getMeasuredWidth()
            if (r1 != 0) goto L_0x04f2
            int r1 = r21.getMeasuredHeight()
            if (r1 == 0) goto L_0x04ee
            goto L_0x04f2
        L_0x04ee:
            r21.requestLayout()
            goto L_0x04f5
        L_0x04f2:
            r21.buildLayout()
        L_0x04f5:
            r21.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$0 */
    public /* synthetic */ void lambda$update$0$DialogCell(ValueAnimator valueAnimator) {
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
    /* JADX WARNING: Removed duplicated region for block: B:184:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x06cf  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x07d7  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x08c3  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0960  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x09ae  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0d13  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0d48  */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0d82  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0d9f  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0e93  */
    /* JADX WARNING: Removed duplicated region for block: B:628:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x1147  */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x115c  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x1176  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x118e  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x11f2  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x1249  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x1260  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x127c  */
    /* JADX WARNING: Removed duplicated region for block: B:711:0x12a6  */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x12d6  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x12ec  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x1316  */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x1338  */
    /* JADX WARNING: Removed duplicated region for block: B:750:? A[RETURN, SYNTHETIC] */
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
            if (r0 == 0) goto L_0x002a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x002a
            float r1 = r0.outProgress
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x002a
            float r1 = r8.translationX
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x002a
            r0.draw(r9)
            return
        L_0x002a:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r8.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x003a
            r2 = 17
        L_0x003a:
            r11 = r2
            r8.lastUpdateTime = r0
            float r0 = r8.clipProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0069
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x0069
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
            r9.clipRect(r10, r0, r1, r2)
        L_0x0069:
            float r0 = r8.translationX
            r13 = 4
            r14 = 1090519040(0x41000000, float:8.0)
            r7 = 2
            r16 = 1082130432(0x40800000, float:4.0)
            r6 = 0
            r5 = 1
            r4 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x009c
            float r0 = r8.cornerProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0080
            goto L_0x009c
        L_0x0080:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x0097
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r10)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r6
        L_0x0097:
            r6 = 1065353216(0x3var_, float:1.0)
            r14 = 1
            goto L_0x046f
        L_0x009c:
            r31.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r17 = "chats_archivePinBackground"
            java.lang.String r18 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00dd
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00c4
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r2 = 2131627750(0x7f0e0ee6, float:1.8882773E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131627750(0x7f0e0ee6, float:1.8882773E38)
            goto L_0x00f9
        L_0x00c4:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131625736(0x7f0e0708, float:1.8878688E38)
            java.lang.String r3 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131625736(0x7f0e0708, float:1.8878688E38)
            goto L_0x00f9
        L_0x00dd:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x0100
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627017(0x7f0e0CLASSNAME, float:1.8881287E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r2
            r2 = 2131627017(0x7f0e0CLASSNAME, float:1.8881287E38)
        L_0x00f9:
            r29 = r3
            r3 = r1
            r1 = r29
            goto L_0x01eb
        L_0x0100:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x01d1
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            r3 = 3
            if (r2 != r3) goto L_0x013b
            boolean r2 = r8.dialogMuted
            if (r2 == 0) goto L_0x012a
            r2 = 2131627577(0x7f0e0e39, float:1.8882422E38)
            java.lang.String r3 = "SwipeUnmute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r2
            r2 = 2131627577(0x7f0e0e39, float:1.8882422E38)
            goto L_0x00f9
        L_0x012a:
            r2 = 2131627569(0x7f0e0e31, float:1.8882406E38)
            java.lang.String r3 = "SwipeMute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r2
            r2 = 2131627569(0x7f0e0e31, float:1.8882406E38)
            goto L_0x00f9
        L_0x013b:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r13) goto L_0x015a
            r2 = 2131627566(0x7f0e0e2e, float:1.88824E38)
            java.lang.String r0 = "SwipeDeleteChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r2)
            java.lang.String r0 = "dialogSwipeRemove"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r2
            r2 = 2131627566(0x7f0e0e2e, float:1.88824E38)
            goto L_0x00f9
        L_0x015a:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r5) goto L_0x018f
            int r2 = r8.unreadCount
            if (r2 > 0) goto L_0x017d
            boolean r2 = r8.markUnread
            if (r2 == 0) goto L_0x016b
            goto L_0x017d
        L_0x016b:
            r2 = 2131627568(0x7f0e0e30, float:1.8882404E38)
            java.lang.String r3 = "SwipeMarkAsUnread"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r2
            r2 = 2131627568(0x7f0e0e30, float:1.8882404E38)
            goto L_0x00f9
        L_0x017d:
            r2 = 2131627567(0x7f0e0e2f, float:1.8882402E38)
            java.lang.String r3 = "SwipeMarkAsRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r2
            r2 = 2131627567(0x7f0e0e2f, float:1.8882402E38)
            goto L_0x00f9
        L_0x018f:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != 0) goto L_0x01bf
            boolean r2 = r8.drawPin
            if (r2 == 0) goto L_0x01ad
            r2 = 2131627578(0x7f0e0e3a, float:1.8882424E38)
            java.lang.String r3 = "SwipeUnpin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r2
            r2 = 2131627578(0x7f0e0e3a, float:1.8882424E38)
            goto L_0x00f9
        L_0x01ad:
            r2 = 2131627570(0x7f0e0e32, float:1.8882408E38)
            java.lang.String r3 = "SwipePin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r2
            r2 = 2131627570(0x7f0e0e32, float:1.8882408E38)
            goto L_0x00f9
        L_0x01bf:
            r2 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r3 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r2
            r2 = 2131624288(0x7f0e0160, float:1.8875751E38)
            goto L_0x00f9
        L_0x01d1:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r2 = 2131627741(0x7f0e0edd, float:1.8882755E38)
            java.lang.String r3 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r2
            r2 = 2131627741(0x7f0e0edd, float:1.8882755E38)
            goto L_0x00f9
        L_0x01eb:
            boolean r6 = r8.swipeCanceled
            if (r6 == 0) goto L_0x01f6
            org.telegram.ui.Components.RLottieDrawable r2 = r8.lastDrawTranslationDrawable
            r8.translationDrawable = r2
            int r2 = r8.lastDrawSwipeMessageStringId
            goto L_0x01fc
        L_0x01f6:
            org.telegram.ui.Components.RLottieDrawable r6 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r6
            r8.lastDrawSwipeMessageStringId = r2
        L_0x01fc:
            r6 = r2
            boolean r2 = r8.translationAnimationStarted
            if (r2 != 0) goto L_0x0223
            float r2 = r8.translationX
            float r2 = java.lang.Math.abs(r2)
            r20 = 1110179840(0x422CLASSNAME, float:43.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r13 = (float) r13
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 <= 0) goto L_0x0223
            r8.translationAnimationStarted = r5
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setProgress(r10)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.start()
        L_0x0223:
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            float r13 = r8.translationX
            float r13 = r13 + r2
            float r2 = r8.currentRevealProgress
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x02a0
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r0 = (float) r0
            float r2 = r13 - r0
            r0 = 0
            int r4 = r30.getMeasuredWidth()
            float r4 = (float) r4
            int r5 = r30.getMeasuredHeight()
            float r5 = (float) r5
            android.graphics.Paint r22 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r15 = r1
            r1 = r31
            r27 = r3
            r3 = r0
            r0 = r6
            r14 = 0
            r6 = r22
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x02a5
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x026e
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14
        L_0x026e:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x02a5
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r18)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r14
            goto L_0x02a5
        L_0x02a0:
            r15 = r1
            r27 = r3
            r0 = r6
            r14 = 0
        L_0x02a5:
            int r1 = r30.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r7
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x02c4
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02c1
            goto L_0x02c4
        L_0x02c1:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x02c6
        L_0x02c4:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x02c6:
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
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 <= 0) goto L_0x0371
            r31.save()
            r5 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r6
            float r5 = r13 - r5
            int r6 = r30.getMeasuredWidth()
            float r6 = (float) r6
            int r14 = r30.getMeasuredHeight()
            float r14 = (float) r14
            r9.clipRect(r5, r10, r6, r14)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r6 = r27
            r5.setColor(r6)
            int r5 = r3 * r3
            int r6 = r30.getMeasuredHeight()
            int r6 = r4 - r6
            int r14 = r30.getMeasuredHeight()
            int r14 = r4 - r14
            int r6 = r6 * r14
            int r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.sqrt(r5)
            float r5 = (float) r5
            float r3 = (float) r3
            float r4 = (float) r4
            android.view.animation.AccelerateInterpolator r6 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r14 = r8.currentRevealProgress
            float r6 = r6.getInterpolation(r14)
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r3, r4, r5, r6)
            r31.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x033e
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r14 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14
            goto L_0x033f
        L_0x033e:
            r14 = 1
        L_0x033f:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x0372
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r5 = "Line 1.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r5 = "Line 2.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r5 = "Line 3.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r14
            goto L_0x0372
        L_0x0371:
            r14 = 1
        L_0x0372:
            r31.save()
            float r1 = (float) r1
            float r2 = (float) r2
            r9.translate(r1, r2)
            float r1 = r8.currentRevealBounceProgress
            int r2 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            r6 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x03a0
            int r2 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x03a0
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
        L_0x03a0:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r2 = 0
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r2, (int) r2)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.draw(r9)
            r31.restore()
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r30.getMeasuredHeight()
            float r2 = (float) r2
            r9.clipRect(r13, r10, r1, r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r1.measureText(r15)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = r8.swipeMessageTextId
            if (r2 != r0) goto L_0x03d3
            int r2 = r8.swipeMessageWidth
            int r3 = r30.getMeasuredWidth()
            if (r2 == r3) goto L_0x041f
        L_0x03d3:
            r8.swipeMessageTextId = r0
            int r0 = r30.getMeasuredWidth()
            r8.swipeMessageWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r21 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r2 = 1117782016(0x42a00000, float:80.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r22 = java.lang.Math.min(r2, r1)
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_CENTER
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r0
            r20 = r15
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r8.swipeMessageTextLayout = r0
            int r0 = r0.getLineCount()
            if (r0 <= r14) goto L_0x041f
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r21 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r2 = 1118044160(0x42a40000, float:82.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r22 = java.lang.Math.min(r2, r1)
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_CENTER
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r0
            r20 = r15
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r8.swipeMessageTextLayout = r0
        L_0x041f:
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            if (r0 == 0) goto L_0x046c
            r31.save()
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            int r0 = r0.getLineCount()
            if (r0 <= r14) goto L_0x0435
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x0436
        L_0x0435:
            r0 = 0
        L_0x0436:
            int r1 = r30.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            android.text.StaticLayout r2 = r8.swipeMessageTextLayout
            int r2 = r2.getWidth()
            float r2 = (float) r2
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
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
            r9.translate(r1, r2)
            android.text.StaticLayout r0 = r8.swipeMessageTextLayout
            r0.draw(r9)
            r31.restore()
        L_0x046c:
            r31.restore()
        L_0x046f:
            float r0 = r8.translationX
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x047d
            r31.save()
            float r0 = r8.translationX
            r9.translate(r0, r10)
        L_0x047d:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x0498
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r31
            r13 = 1065353216(0x3var_, float:1.0)
            r6 = r0
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x049a
        L_0x0498:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x049a:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x04ce
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x04a8
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x04ce
        L_0x04a8:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r13)
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
            goto L_0x04f4
        L_0x04ce:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x04d6
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x04f4
        L_0x04d6:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
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
        L_0x04f4:
            float r0 = r8.translationX
            java.lang.String r15 = "windowBackgroundWhite"
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0507
            float r0 = r8.cornerProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0503
            goto L_0x0507
        L_0x0503:
            r17 = 1090519040(0x41000000, float:8.0)
            goto L_0x05ba
        L_0x0507:
            r31.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
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
            r0.set(r1, r10, r2, r3)
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
            if (r0 == 0) goto L_0x0585
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0557
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0585
        L_0x0557:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r13)
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
            goto L_0x058e
        L_0x0585:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0591
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x058e
            goto L_0x0591
        L_0x058e:
            r17 = 1090519040(0x41000000, float:8.0)
            goto L_0x05b7
        L_0x0591:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            r17 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r1 = (float) r1
            float r2 = r8.cornerProgress
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r1, r2, r3)
        L_0x05b7:
            r31.restore()
        L_0x05ba:
            float r0 = r8.translationX
            r19 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x05d5
            float r0 = r8.cornerProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x05ea
            float r1 = (float) r11
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x05e7
            r8.cornerProgress = r13
            goto L_0x05e7
        L_0x05d5:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r1 <= 0) goto L_0x05ea
            float r1 = (float) r11
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 >= 0) goto L_0x05e7
            r8.cornerProgress = r10
        L_0x05e7:
            r20 = 1
            goto L_0x05ec
        L_0x05ea:
            r20 = 0
        L_0x05ec:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x05ff
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x0637
        L_0x05ff:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x0612
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x0637
        L_0x0612:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x0625
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x0637
        L_0x0625:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x0637
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x0637:
            android.text.StaticLayout r0 = r8.nameLayout
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x06af
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0657
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x068b
        L_0x0657:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0678
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x0664
            int r0 = r0.type
            if (r0 != r7) goto L_0x0664
            goto L_0x0678
        L_0x0664:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x068b
        L_0x0678:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x068b:
            r31.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x069d
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x069a
            goto L_0x069d
        L_0x069a:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x069f
        L_0x069d:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x069f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r31.restore()
        L_0x06af:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x06cb
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x06cb
            r31.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r31.restore()
        L_0x06cb:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x0719
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06e1
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0700
        L_0x06e1:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x06f3
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0700
        L_0x06f3:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x0700:
            r31.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x0712 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0712 }
            goto L_0x0716
        L_0x0712:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0716:
            r31.restore()
        L_0x0719:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x07d3
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x074d
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0739
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0760
        L_0x0739:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0760
        L_0x074d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0760:
            r31.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x0772 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0772 }
            goto L_0x0776
        L_0x0772:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0776:
            r31.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x07d3
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x07d3
            r31.save()
            int r1 = r8.printingStringType
            if (r1 == r14) goto L_0x07a8
            r2 = 4
            if (r1 != r2) goto L_0x078e
            goto L_0x07a8
        L_0x078e:
            int r1 = r8.statusDrawableLeft
            float r1 = (float) r1
            int r2 = r8.messageTop
            float r2 = (float) r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r0.getIntrinsicHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            float r2 = r2 + r3
            r9.translate(r1, r2)
            goto L_0x07ba
        L_0x07a8:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r14) goto L_0x07b4
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x07b5
        L_0x07b4:
            r6 = 0
        L_0x07b5:
            int r3 = r3 + r6
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x07ba:
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
        L_0x07d3:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x088a
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x07df
            r6 = 2
            goto L_0x07e0
        L_0x07df:
            r6 = 0
        L_0x07e0:
            int r0 = r0 + r6
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x07e7
            r6 = 4
            goto L_0x07e8
        L_0x07e7:
            r6 = 0
        L_0x07e8:
            int r0 = r0 + r6
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x07f6
            if (r1 == r0) goto L_0x07f6
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x07f6
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x07f6:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x07fc
            int r0 = r8.animateToStatusDrawableParams
        L_0x07fc:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x0803
            r23 = 1
            goto L_0x0805
        L_0x0803:
            r23 = 0
        L_0x0805:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x080d
            r2 = 4
            r24 = 1
            goto L_0x0810
        L_0x080d:
            r2 = 4
            r24 = 0
        L_0x0810:
            r0 = r0 & r2
            if (r0 == 0) goto L_0x0815
            r0 = 1
            goto L_0x0816
        L_0x0815:
            r0 = 0
        L_0x0816:
            if (r1 == 0) goto L_0x0867
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x0820
            r3 = 1
            goto L_0x0821
        L_0x0820:
            r3 = 0
        L_0x0821:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x0828
            r2 = 4
            r4 = 1
            goto L_0x082a
        L_0x0828:
            r2 = 4
            r4 = 0
        L_0x082a:
            r1 = r1 & r2
            if (r1 == 0) goto L_0x082f
            r5 = 1
            goto L_0x0830
        L_0x082f:
            r5 = 0
        L_0x0830:
            if (r23 != 0) goto L_0x0850
            if (r3 != 0) goto L_0x0850
            if (r5 == 0) goto L_0x0850
            if (r4 != 0) goto L_0x0850
            if (r24 == 0) goto L_0x0850
            if (r0 == 0) goto L_0x0850
            r6 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r30
            r2 = r31
            r3 = r23
            r4 = r24
            r23 = r5
            r5 = r0
            r7 = r23
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0876
        L_0x0850:
            r6 = 0
            float r1 = r8.statusDrawableProgress
            float r7 = r13 - r1
            r1 = r30
            r2 = r31
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r23
            r4 = r24
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0876
        L_0x0867:
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r30
            r2 = r31
            r3 = r23
            r4 = r24
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0876:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x087e
            r7 = 2
            goto L_0x087f
        L_0x087e:
            r7 = 0
        L_0x087f:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0886
            r6 = 4
            goto L_0x0887
        L_0x0886:
            r6 = 0
        L_0x0887:
            int r0 = r0 + r6
            r8.lastStatusDrawableParams = r0
        L_0x088a:
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x08c3
            boolean r0 = r8.drawVerified
            if (r0 != 0) goto L_0x08c3
            int r0 = r8.drawScam
            if (r0 != 0) goto L_0x08c3
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x08a6
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08a3
            goto L_0x08a6
        L_0x08a3:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x08a7
        L_0x08a6:
            r4 = 0
        L_0x08a7:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 - r2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08b3
            r2 = 1096286208(0x41580000, float:13.5)
            goto L_0x08b5
        L_0x08b3:
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x08b5:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0932
        L_0x08c3:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0904
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x08d7
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08d4
            goto L_0x08d7
        L_0x08d4:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x08d9
        L_0x08d7:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x08d9:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x08f0
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08ed
            goto L_0x08f0
        L_0x08ed:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x08f2
        L_0x08f0:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x08f2:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0932
        L_0x0904:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0932
            if (r0 != r14) goto L_0x090d
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x090f
        L_0x090d:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x090f:
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x091d
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x091a
            goto L_0x091d
        L_0x091a:
            r2 = 1097859072(0x41700000, float:15.0)
            goto L_0x091f
        L_0x091d:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x091f:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            int r0 = r8.drawScam
            if (r0 != r14) goto L_0x092d
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x092f
        L_0x092d:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x092f:
            r0.draw(r9)
        L_0x0932:
            boolean r0 = r8.drawReorder
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x093e
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0956
        L_0x093e:
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
        L_0x0956:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x09ae
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r5 = r8.reorderIconProgress
            float r5 = r13 - r5
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
            goto L_0x0d0d
        L_0x09ae:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x09dd
            boolean r5 = r8.drawMention
            if (r5 != 0) goto L_0x09dd
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r5 == 0) goto L_0x09bd
            goto L_0x09dd
        L_0x09bd:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0d0d
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r1
            int r1 = (int) r2
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r9)
            goto L_0x0d0d
        L_0x09dd:
            if (r0 != 0) goto L_0x09ea
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x09e6
            goto L_0x09ea
        L_0x09e6:
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0CLASSNAME
        L_0x09ea:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x09f7
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x09f7
            float r5 = r8.countChangeProgress
            float r5 = r13 - r5
            goto L_0x09f9
        L_0x09f7:
            float r5 = r8.countChangeProgress
        L_0x09f9:
            android.text.StaticLayout r6 = r8.countOldLayout
            if (r6 == 0) goto L_0x0b7b
            if (r0 != 0) goto L_0x0a01
            goto L_0x0b7b
        L_0x0a01:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0a0d
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0a0a
            goto L_0x0a0d
        L_0x0a0a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0a0f
        L_0x0a0d:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0a0f:
            float r6 = r8.reorderIconProgress
            float r6 = r13 - r6
            float r6 = r6 * r1
            int r6 = (int) r6
            r0.setAlpha(r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r13 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r6.setAlpha(r7)
            r6 = 1073741824(0x40000000, float:2.0)
            float r7 = r5 * r6
            int r6 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r6 <= 0) goto L_0x0a30
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0a31
        L_0x0a30:
            r6 = r7
        L_0x0a31:
            int r14 = r8.countLeft
            float r14 = (float) r14
            float r14 = r14 * r6
            int r4 = r8.countLeftOld
            float r4 = (float) r4
            float r24 = r13 - r6
            float r4 = r4 * r24
            float r14 = r14 + r4
            r4 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r14 - r4
            android.graphics.RectF r10 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r3 = r8.countWidth
            float r3 = (float) r3
            float r3 = r3 * r6
            float r3 = r3 + r4
            int r13 = r8.countWidthOld
            float r13 = (float) r13
            float r13 = r13 * r24
            float r3 = r3 + r13
            r13 = 1093664768(0x41300000, float:11.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r3 = r3 + r13
            int r13 = r8.countTop
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r13 + r28
            float r13 = (float) r13
            r10.set(r4, r1, r3, r13)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x0a81
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r7)
            float r3 = r3 * r1
            r1 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 + r1
            goto L_0x0a99
        L_0x0a81:
            r1 = 1065353216(0x3var_, float:1.0)
            r3 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r7 = 1056964608(0x3var_, float:0.5)
            float r5 = r5 - r7
            r7 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 * r7
            float r5 = r1 - r5
            float r4 = r4.getInterpolation(r5)
            float r4 = r4 * r3
            float r3 = r4 + r1
        L_0x0a99:
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
            if (r0 == 0) goto L_0x0ad2
            r31.save()
            int r0 = r8.countTop
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 + r1
            float r0 = (float) r0
            r9.translate(r14, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r31.restore()
        L_0x0ad2:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r4 = r3 * r6
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x0b0f
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0af1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0af6
        L_0x0af1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0af6:
            float r1 = (float) r1
            float r1 = r1 * r24
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r14, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r9)
            r31.restore()
            goto L_0x0b3c
        L_0x0b0f:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x0b3c
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0b1f
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0b24
        L_0x0b1f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0b24:
            float r1 = (float) r1
            float r1 = r1 * r24
            int r4 = r8.countTop
            float r4 = (float) r4
            float r1 = r1 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            float r1 = r1 + r4
            r9.translate(r14, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r9)
            r31.restore()
        L_0x0b3c:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0b71
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r24
            int r3 = (int) r3
            r1.setAlpha(r3)
            r31.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0b55
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x0b59
        L_0x0b55:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0b59:
            float r1 = (float) r1
            float r1 = r1 * r6
            int r3 = r8.countTop
            float r3 = (float) r3
            float r1 = r1 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r1 = r1 + r3
            r9.translate(r14, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r9)
            r31.restore()
        L_0x0b71:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r31.restore()
            goto L_0x09e6
        L_0x0b7b:
            if (r0 != 0) goto L_0x0b7e
            goto L_0x0b80
        L_0x0b7e:
            android.text.StaticLayout r6 = r8.countLayout
        L_0x0b80:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b8c
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b89
            goto L_0x0b8c
        L_0x0b89:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b8e
        L_0x0b8c:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b8e:
            float r1 = r8.reorderIconProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r4 = (int) r4
            r0.setAlpha(r4)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r3 - r7
            float r7 = r7 * r1
            int r1 = (int) r7
            r4.setAlpha(r1)
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
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r10 = r10 + r13
            float r10 = (float) r10
            r3.set(r4, r7, r1, r10)
            r1 = 1065353216(0x3var_, float:1.0)
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0CLASSNAME
            boolean r3 = r8.drawPin
            if (r3 == 0) goto L_0x0CLASSNAME
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
            float r4 = r1 - r5
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r1 = r1.getBounds()
            int r1 = r1.centerX()
            float r1 = (float) r1
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerY()
            float r3 = (float) r3
            r9.scale(r4, r4, r1, r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r1.draw(r9)
            r31.restore()
        L_0x0CLASSNAME:
            r31.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r5, r5, r1, r3)
        L_0x0CLASSNAME:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r7 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r7, r3, r0)
            if (r6 == 0) goto L_0x0c4c
            r31.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r1 + r3
            float r1 = (float) r1
            r9.translate(r0, r1)
            r6.draw(r9)
            r31.restore()
        L_0x0c4c:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0CLASSNAME
            r31.restore()
        L_0x0CLASSNAME:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0d0d
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r3 = r8.reorderIconProgress
            float r4 = r1 - r3
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
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
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0CLASSNAME
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0CLASSNAME:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0cd4
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
            r0.setAlpha(r1)
            r31.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r9)
            r31.restore()
            goto L_0x0d0d
        L_0x0cd4:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            r2 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r3
            int r2 = r8.countTop
            r3 = 1078774989(0x404ccccd, float:3.2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2, (int) r3, (int) r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r9)
        L_0x0d0d:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0d31
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
        L_0x0d31:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0d3f
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0d3f
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0d44
        L_0x0d3f:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0d44:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0d7d
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0d7d
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            float r0 = r0.getCenterX()
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r1 = r1.getIntrinsicWidth()
            r10 = 2
            int r1 = r1 / r10
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            org.telegram.messenger.ImageReceiver r1 = r8.thumbImage
            float r1 = r1.getCenterY()
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r2 = r2.getIntrinsicHeight()
            int r2 = r2 / r10
            float r2 = (float) r2
            float r1 = r1 - r2
            int r1 = (int) r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r0, (int) r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            r0.draw(r9)
            goto L_0x0d7e
        L_0x0d7d:
            r10 = 2
        L_0x0d7e:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0d85
            r31.restore()
        L_0x0d85:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0e8c
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0e8c
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0e8f
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0e8f
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0e8f
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x0dcd
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0db5
            int r0 = r0.expires
            int r2 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0dcb
        L_0x0db5:
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r8.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x0dcd
        L_0x0dcb:
            r6 = 1
            goto L_0x0dce
        L_0x0dcd:
            r6 = 0
        L_0x0dce:
            if (r6 != 0) goto L_0x0dd7
            float r0 = r8.onlineProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0e88
        L_0x0dd7:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0de9
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0de6
            goto L_0x0de9
        L_0x0de6:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x0deb
        L_0x0de9:
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0deb:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0e0e
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e07
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e05
            goto L_0x0e07
        L_0x0e05:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e07:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0e25
        L_0x0e0e:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e1f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e1d
            goto L_0x0e1f
        L_0x0e1d:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e1f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0e25:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r2.setColor(r3)
            float r1 = (float) r1
            float r0 = (float) r0
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r8.onlineProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r1, r0, r2, r3)
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r3 = "chats_onlineCircle"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r8.onlineProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r1, r0, r2, r3)
            if (r6 == 0) goto L_0x0e73
            float r0 = r8.onlineProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0e88
            float r2 = (float) r11
            float r2 = r2 / r19
            float r0 = r0 + r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0e86
            r8.onlineProgress = r1
            goto L_0x0e86
        L_0x0e73:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e88
            float r2 = (float) r11
            float r2 = r2 / r19
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0e86
            r8.onlineProgress = r1
        L_0x0e86:
            r5 = 1
            goto L_0x0e8a
        L_0x0e88:
            r5 = r20
        L_0x0e8a:
            r20 = r5
        L_0x0e8c:
            r2 = 0
            goto L_0x1170
        L_0x0e8f:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0e8c
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0e9d
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0e9d
            r6 = 1
            goto L_0x0e9e
        L_0x0e9d:
            r6 = 0
        L_0x0e9e:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x0ea9
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0e8c
        L_0x0ea9:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0ebe
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0ebe
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r0
            goto L_0x0ec0
        L_0x0ebe:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0ec0:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0ed2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0ecf
            goto L_0x0ed2
        L_0x0ecf:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x0ed4
        L_0x0ed2:
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ed4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0ef7
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0ef0
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0eee
            goto L_0x0ef0
        L_0x0eee:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ef0:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0f0e
        L_0x0ef7:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0var_
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r21 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0f0e:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r2.setColor(r3)
            float r2 = (float) r1
            float r0 = (float) r0
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r5 = r8.chatCallProgress
            float r3 = r3 * r5
            float r3 = r3 * r4
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r2, r0, r3, r5)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "chats_onlineCircle"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            r3 = 1091567616(0x41100000, float:9.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r5 = r8.chatCallProgress
            float r3 = r3 * r5
            float r3 = r3 * r4
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r2, r0, r3, r5)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setColor(r5)
            int r3 = r8.progressStage
            r5 = 1077936128(0x40400000, float:3.0)
            if (r3 != 0) goto L_0x0f7f
            r6 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r13 = 1073741824(0x40000000, float:2.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r8.innerProgress
            float r13 = r13 * r14
            float r6 = r6 - r13
        L_0x0f7a:
            r7 = r6
        L_0x0f7b:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x1086
        L_0x0f7f:
            r6 = 1
            if (r3 != r6) goto L_0x0fa7
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r14 = (float) r14
            float r7 = r8.innerProgress
            float r14 = r14 * r7
            float r7 = r13 + r14
            goto L_0x1086
        L_0x0fa7:
            r6 = 1065353216(0x3var_, float:1.0)
            if (r3 != r10) goto L_0x0fce
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            r6 = 1073741824(0x40000000, float:2.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r7 = r8.innerProgress
            float r6 = r6 * r7
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r7 = (float) r7
            float r13 = r8.innerProgress
        L_0x0fca:
            float r7 = r7 * r13
            float r6 = r6 - r7
            goto L_0x0f7a
        L_0x0fce:
            r6 = 3
            if (r3 != r6) goto L_0x0ff4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            r6 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            float r13 = r8.innerProgress
            float r7 = r7 * r13
            float r3 = r3 - r7
            r7 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r13 = (float) r13
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r14 = r8.innerProgress
            float r6 = r6 * r14
            float r6 = r6 + r13
            goto L_0x0f7a
        L_0x0ff4:
            r6 = 4
            r7 = 1065353216(0x3var_, float:1.0)
            if (r3 != r6) goto L_0x1017
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r7 = r8.innerProgress
            float r6 = r6 * r7
            float r3 = r3 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r7 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r13 = r8.innerProgress
            goto L_0x0fca
        L_0x1017:
            r6 = 5
            if (r3 != r6) goto L_0x103d
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r7 = r8.innerProgress
            float r6 = r6 * r7
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r13 = (float) r13
            float r14 = r8.innerProgress
        L_0x1039:
            float r13 = r13 * r14
            float r7 = r7 + r13
            goto L_0x1086
        L_0x103d:
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 6
            if (r3 != r7) goto L_0x1064
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r7 = r8.innerProgress
            float r6 = r6 * r7
            float r3 = r3 + r6
            r6 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r13 = (float) r13
            float r14 = r8.innerProgress
            float r13 = r13 * r14
            float r7 = r7 - r13
            goto L_0x0f7b
        L_0x1064:
            r6 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r7 = r8.innerProgress
            float r6 = r6 * r7
            float r3 = r3 - r6
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            r13 = 1073741824(0x40000000, float:2.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r8.innerProgress
            goto L_0x1039
        L_0x1086:
            float r13 = r8.chatCallProgress
            int r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r13 < 0) goto L_0x1090
            int r13 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r13 >= 0) goto L_0x109c
        L_0x1090:
            r31.save()
            float r6 = r8.chatCallProgress
            float r13 = r6 * r4
            float r6 = r6 * r4
            r9.scale(r13, r6, r2, r0)
        L_0x109c:
            android.graphics.RectF r2 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r13 = r1 - r13
            float r13 = (float) r13
            float r14 = r0 - r3
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r10 = r1 + r16
            float r10 = (float) r10
            float r3 = r3 + r0
            r2.set(r13, r14, r10, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r6, r10)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r6
            float r3 = (float) r3
            float r6 = r0 - r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r0 = r0 + r7
            r2.set(r3, r6, r10, r0)
            android.graphics.RectF r2 = r8.rect
            r3 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r7, r3, r10)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r1
            float r3 = (float) r3
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r1 = r1 + r5
            float r1 = (float) r1
            r2.set(r3, r6, r1, r0)
            android.graphics.RectF r0 = r8.rect
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r3 = (float) r3
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r0, r2, r3, r5)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x1120
            int r0 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1123
        L_0x1120:
            r31.restore()
        L_0x1123:
            float r0 = r8.innerProgress
            float r1 = (float) r11
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x1143
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x1143
            r2 = 0
            r8.progressStage = r2
        L_0x1143:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x115c
            float r0 = r8.chatCallProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x115a
            float r1 = r1 / r19
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x115a
            r8.chatCallProgress = r2
        L_0x115a:
            r2 = 0
            goto L_0x116e
        L_0x115c:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x116e
            float r1 = r1 / r19
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x116e
            r8.chatCallProgress = r2
        L_0x116e:
            r20 = 1
        L_0x1170:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1179
            r31.restore()
        L_0x1179:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x118a
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x118a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x118a
            r0.draw(r9)
        L_0x118a:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x11ea
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
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x11af
        L_0x11ae:
            r6 = 0
        L_0x11af:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x11cf
            r2 = 0
            int r0 = r30.getMeasuredHeight()
            r1 = 1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            int r0 = r0 - r6
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            int r0 = r0 - r1
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x11ea
        L_0x11cf:
            float r2 = (float) r6
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
            goto L_0x11eb
        L_0x11ea:
            r7 = 1
        L_0x11eb:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x1239
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x11fc
            r31.restore()
            goto L_0x1239
        L_0x11fc:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
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
        L_0x1239:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x1247
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x1245
            goto L_0x1247
        L_0x1245:
            r1 = 0
            goto L_0x1276
        L_0x1247:
            if (r0 == 0) goto L_0x1260
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1245
            float r2 = (float) r11
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x125e
            r8.reorderIconProgress = r1
        L_0x125e:
            r1 = 0
            goto L_0x1274
        L_0x1260:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1276
            float r2 = (float) r11
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1274
            r8.reorderIconProgress = r1
        L_0x1274:
            r5 = 1
            goto L_0x1278
        L_0x1276:
            r5 = r20
        L_0x1278:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x12a6
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x12d2
            float r2 = (float) r11
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x128f
            r8.archiveBackgroundProgress = r1
        L_0x128f:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12d1
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x12d1
        L_0x12a6:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x12d2
            float r2 = (float) r11
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x12bb
            r8.archiveBackgroundProgress = r1
        L_0x12bb:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12d1
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x12d1:
            r5 = 1
        L_0x12d2:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x12e8
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r11
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x12e7
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x12e7:
            r5 = 1
        L_0x12e8:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x1316
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1302
            float r2 = (float) r11
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1302
            r8.currentRevealBounceProgress = r1
            r5 = 1
        L_0x1302:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1336
            float r2 = (float) r11
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1335
            r8.currentRevealProgress = r1
            goto L_0x1335
        L_0x1316:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x1322
            r8.currentRevealBounceProgress = r1
            r5 = 1
        L_0x1322:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1336
            float r2 = (float) r11
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1335
            r8.currentRevealProgress = r1
        L_0x1335:
            r5 = 1
        L_0x1336:
            if (r5 == 0) goto L_0x133b
            r30.invalidate()
        L_0x133b:
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
        this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DialogCell.this.lambda$createStatusDrawableAnimator$1$DialogCell(valueAnimator);
            }
        });
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
    /* renamed from: lambda$createStatusDrawableAnimator$1 */
    public /* synthetic */ void lambda$createStatusDrawableAnimator$1$DialogCell(ValueAnimator valueAnimator) {
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
        if (!isFolderCell() || (pullForegroundDrawable = this.archivedChatsDrawable) == null || pullForegroundDrawable.pullProgress != 0.0f) {
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
