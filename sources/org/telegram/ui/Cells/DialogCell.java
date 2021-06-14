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
    private boolean attachedToWindow;
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
        this.attachedToWindow = false;
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
        this.attachedToWindow = true;
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
    /* JADX WARNING: Removed duplicated region for block: B:1035:0x185d  */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x18fa A[Catch:{ Exception -> 0x1927 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1072:0x18fd A[Catch:{ Exception -> 0x1927 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x1917 A[Catch:{ Exception -> 0x1927 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1083:0x1933  */
    /* JADX WARNING: Removed duplicated region for block: B:1092:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b2e A[Catch:{ Exception -> 0x0b41 }] */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b35 A[Catch:{ Exception -> 0x0b41 }] */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0be0 A[SYNTHETIC, Splitter:B:531:0x0be0] */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0bfe  */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0c2a  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x0c5f  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0dfd  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0e9b  */
    /* JADX WARNING: Removed duplicated region for block: B:650:0x0ea4  */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0eb1  */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0eb9  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0ed6  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0ee8  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0f8c  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x1009  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x1014  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x104d  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x105f  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x10b1  */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x10bd  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x10fc  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x1107  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x1117  */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x112f  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x113e  */
    /* JADX WARNING: Removed duplicated region for block: B:787:0x1177  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x119f  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x1222  */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x1238  */
    /* JADX WARNING: Removed duplicated region for block: B:834:0x12ac A[Catch:{ Exception -> 0x12cb }] */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x12af A[Catch:{ Exception -> 0x12cb }] */
    /* JADX WARNING: Removed duplicated region for block: B:843:0x12d9  */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x1382  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x1433  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x1458  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x1488  */
    /* JADX WARNING: Removed duplicated region for block: B:903:0x15a9  */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x166b  */
    /* JADX WARNING: Removed duplicated region for block: B:946:0x1674  */
    /* JADX WARNING: Removed duplicated region for block: B:956:0x169a A[Catch:{ Exception -> 0x1723 }] */
    /* JADX WARNING: Removed duplicated region for block: B:957:0x16a6 A[ADDED_TO_REGION, Catch:{ Exception -> 0x1723 }] */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x16c3 A[Catch:{ Exception -> 0x1723 }] */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1711 A[Catch:{ Exception -> 0x1723 }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x1714 A[Catch:{ Exception -> 0x1723 }] */
    /* JADX WARNING: Removed duplicated region for block: B:987:0x172b  */
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
            r0 = 2131625668(0x7f0e06c4, float:1.887855E38)
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
            goto L_0x10bb
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
            goto L_0x0ead
        L_0x0679:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0707
            r0 = 2131625229(0x7f0e050d, float:1.887766E38)
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
            r0 = 2131625767(0x7f0e0727, float:1.8878751E38)
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
            r0 = 2131625324(0x7f0e056c, float:1.8877853E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071a
        L_0x073a:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0752
            r0 = 2131624517(0x7f0e0245, float:1.8876216E38)
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
            r0 = 2131625325(0x7f0e056d, float:1.8877855E38)
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
            r0 = 2131625313(0x7f0e0561, float:1.887783E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x071a
        L_0x0786:
            r0 = 2131625312(0x7f0e0560, float:1.8877828E38)
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
            r0 = 2131627283(0x7f0e0d13, float:1.8881826E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r8 = 0
            r10 = 0
            goto L_0x0ead
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
            r0 = 2131627283(0x7f0e0d13, float:1.8881826E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r0 = 0
            r2 = 2
            r5 = 0
            r6 = 1
            r8 = 0
            r10 = 0
            goto L_0x0ea0
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
            goto L_0x0ea0
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
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (android.graphics.drawable.Drawable) r29, (java.lang.Object) r30, (int) r31)
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
            if (r6 == 0) goto L_0x0CLASSNAME
            int r8 = r6.id
            if (r8 <= 0) goto L_0x0CLASSNAME
            if (r3 != 0) goto L_0x0CLASSNAME
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x095f
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0CLASSNAME
        L_0x095f:
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L_0x0971
            r2 = 2131625668(0x7f0e06c4, float:1.887855E38)
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
            r2 = 2131625758(0x7f0e071e, float:1.8878733E38)
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
            goto L_0x0bce
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
            if (r3 == 0) goto L_0x0b47
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0b47
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
            goto L_0x0ac2
        L_0x0a83:
            r5 = 1
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ac2
        L_0x0a94:
            r5 = 1
            r6 = 0
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0abc
            r8 = 18
            if (r11 < r8) goto L_0x0aad
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ac2
        L_0x0aad:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ac2
        L_0x0abc:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r5 == 0) goto L_0x0ac8
            java.lang.String r0 = r3.title
        L_0x0ac2:
            r3 = 32
            r5 = 10
            r8 = 1
            goto L_0x0b11
        L_0x0ac8:
            int r3 = r0.type
            r5 = 14
            if (r3 != r5) goto L_0x0b06
            r3 = 18
            if (r11 < r3) goto L_0x0aec
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r6 = 0
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8 = 1
            r5[r8] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0b0d
        L_0x0aec:
            r3 = 2
            r6 = 0
            r8 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r5[r8] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0b0d
        L_0x0b06:
            r8 = 1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0b0d:
            r3 = 32
            r5 = 10
        L_0x0b11:
            java.lang.String r0 = r0.replace(r5, r3)
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r3 = 0
            r5[r3] = r0
            r5[r8] = r2
            java.lang.String r0 = java.lang.String.format(r12, r5)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0b41 }
            java.lang.String r5 = "chats_attachMessage"
            r0.<init>(r5)     // Catch:{ Exception -> 0x0b41 }
            if (r13 == 0) goto L_0x0b35
            int r5 = r2.length()     // Catch:{ Exception -> 0x0b41 }
            r6 = 2
            int r5 = r5 + r6
            goto L_0x0b36
        L_0x0b35:
            r5 = 0
        L_0x0b36:
            int r6 = r3.length()     // Catch:{ Exception -> 0x0b41 }
            r8 = 33
            r3.setSpan(r0, r5, r6, r8)     // Catch:{ Exception -> 0x0b41 }
            goto L_0x0bce
        L_0x0b41:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0bce
        L_0x0b47:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0bc8
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b9d
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b5c
            r3 = r0
        L_0x0b5c:
            int r0 = r44.getMeasuredWidth()
            r5 = 1121058816(0x42d20000, float:105.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            if (r13 == 0) goto L_0x0b83
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 != 0) goto L_0x0b7a
            float r0 = (float) r0
            java.lang.String r5 = r2.toString()
            float r5 = r9.measureText(r5)
            float r0 = r0 - r5
            int r0 = (int) r0
        L_0x0b7a:
            float r0 = (float) r0
            java.lang.String r5 = ": "
            float r5 = r9.measureText(r5)
            float r0 = r0 - r5
            int r0 = (int) r0
        L_0x0b83:
            if (r0 <= 0) goto L_0x0b9b
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            java.lang.String r5 = (java.lang.String) r5
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r3, r5, r0, r9, r8)
            java.lang.String r3 = r0.toString()
            goto L_0x0bb6
        L_0x0b9b:
            r6 = 0
            goto L_0x0bb6
        L_0x0b9d:
            r6 = 0
            int r0 = r3.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r0 <= r5) goto L_0x0baa
            java.lang.String r3 = r3.substring(r6, r5)
        L_0x0baa:
            r5 = 32
            r8 = 10
            java.lang.String r0 = r3.replace(r8, r5)
            java.lang.String r3 = r0.trim()
        L_0x0bb6:
            r5 = 2
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r6] = r3
            r3 = 1
            r0[r3] = r2
            java.lang.String r0 = java.lang.String.format(r12, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09da
        L_0x0bc8:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            goto L_0x09da
        L_0x0bce:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0bd6
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0be0
        L_0x0bd6:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0bfe
            int r0 = r3.length()
            if (r0 <= 0) goto L_0x0bfe
        L_0x0be0:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bf7 }
            java.lang.String r5 = "chats_nameMessage"
            r0.<init>(r5)     // Catch:{ Exception -> 0x0bf7 }
            int r5 = r2.length()     // Catch:{ Exception -> 0x0bf7 }
            r6 = 1
            int r5 = r5 + r6
            r6 = 33
            r8 = 0
            r3.setSpan(r0, r8, r5, r6)     // Catch:{ Exception -> 0x0bf5 }
            r0 = r5
            goto L_0x0CLASSNAME
        L_0x0bf5:
            r0 = move-exception
            goto L_0x0bf9
        L_0x0bf7:
            r0 = move-exception
            r5 = 0
        L_0x0bf9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0CLASSNAME
        L_0x0bfe:
            r0 = 0
            r5 = 0
        L_0x0CLASSNAME:
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r6 = r6[r8]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r6, r8, r11)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.hasHighlightedWords()
            if (r6 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r6)
            if (r6 == 0) goto L_0x0CLASSNAME
            r3 = r6
        L_0x0CLASSNAME:
            boolean r6 = r1.hasMessageThumb
            if (r6 == 0) goto L_0x0c4f
            boolean r6 = r3 instanceof android.text.SpannableStringBuilder
            if (r6 != 0) goto L_0x0CLASSNAME
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            r3 = r6
        L_0x0CLASSNAME:
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
        L_0x0c4f:
            r8 = r0
            r0 = r2
            r2 = 2
            r5 = 1
            r6 = 0
            goto L_0x0ea0
        L_0x0CLASSNAME:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0c5f
        L_0x0c5c:
            r2 = 2
            goto L_0x0df9
        L_0x0c5f:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0c7d
            org.telegram.tgnet.TLRPC$Photo r3 = r2.photo
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r3 == 0) goto L_0x0c7d
            int r3 = r2.ttl_seconds
            if (r3 == 0) goto L_0x0c7d
            r0 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0c5c
        L_0x0c7d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r3 == 0) goto L_0x0CLASSNAME
            int r3 = r2.ttl_seconds
            if (r3 == 0) goto L_0x0CLASSNAME
            r0 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0c5c
        L_0x0CLASSNAME:
            java.lang.CharSequence r3 = r0.caption
            if (r3 == 0) goto L_0x0d3d
            if (r5 != 0) goto L_0x0c9d
            r0 = r4
            goto L_0x0cc9
        L_0x0c9d:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0ca6
            java.lang.String r0 = "📹 "
            goto L_0x0cc9
        L_0x0ca6:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0cb1
            java.lang.String r0 = "🎤 "
            goto L_0x0cc9
        L_0x0cb1:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0cbc
            java.lang.String r0 = "🎧 "
            goto L_0x0cc9
        L_0x0cbc:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0cc7
            java.lang.String r0 = "🖼 "
            goto L_0x0cc9
        L_0x0cc7:
            java.lang.String r0 = "📎 "
        L_0x0cc9:
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
            if (r6 == 0) goto L_0x0cff
            float r3 = (float) r3
            java.lang.String r6 = ": "
            float r6 = r9.measureText(r6)
            float r3 = r3 - r6
            int r3 = (int) r3
            goto L_0x0d00
        L_0x0cff:
            throw r5
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
            goto L_0x0c5c
        L_0x0d28:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0c5c
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
            goto L_0x0de5
        L_0x0d5b:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r3 == 0) goto L_0x0d7b
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
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r3 == 0) goto L_0x0d82
            java.lang.String r0 = r2.title
            goto L_0x0d58
        L_0x0d82:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0da2
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
            goto L_0x0de5
        L_0x0da2:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0dda
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0dda
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
            goto L_0x0dde
        L_0x0dda:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0dde:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0de5:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            if (r5 == 0) goto L_0x0df9
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0df9
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r9 = r3[r5]
        L_0x0df9:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0e9b
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0e39
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0e39
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
            goto L_0x0e4a
        L_0x0e39:
            r6 = 0
            int r3 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x0e46
            java.lang.CharSequence r0 = r0.subSequence(r6, r5)
        L_0x0e46:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e4a:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0e54
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0e54:
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
            if (r5 == 0) goto L_0x0e95
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r5)
            if (r3 == 0) goto L_0x0e95
            goto L_0x0e96
        L_0x0e95:
            r3 = r0
        L_0x0e96:
            r0 = 0
            r5 = 1
            r6 = 0
            goto L_0x0812
        L_0x0e9b:
            r3 = r0
            r0 = 0
            r5 = 1
            goto L_0x0843
        L_0x0ea0:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0ea8
            java.lang.CharSequence r0 = r44.formatArchivedDialogNames()
        L_0x0ea8:
            r43 = r6
            r6 = r5
            r5 = r43
        L_0x0ead:
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            if (r11 == 0) goto L_0x0eb9
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ed2
        L_0x0eb9:
            int r11 = r1.lastMessageDate
            if (r11 == 0) goto L_0x0ec3
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ed2
        L_0x0ec3:
            org.telegram.messenger.MessageObject r11 = r1.message
            if (r11 == 0) goto L_0x0ed1
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0ed2
        L_0x0ed1:
            r11 = r4
        L_0x0ed2:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 != 0) goto L_0x0ee8
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
            goto L_0x0fec
        L_0x0ee8:
            r13 = 0
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0f2a
            int r12 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r12 + r14
            if (r15 <= 0) goto L_0x0var_
            if (r12 <= r14) goto L_0x0f0c
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
        L_0x0f0c:
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
            goto L_0x0var_
        L_0x0var_:
            r1.drawCount = r13
            r1.drawMention = r13
            r2 = 0
        L_0x0var_:
            r12 = 0
            goto L_0x0var_
        L_0x0f2a:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0var_
            r1.drawCount = r13
            r2 = 0
            r10 = 0
            r12 = 1
            r14 = 0
            goto L_0x0var_
        L_0x0var_:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0f5b
            r13 = 1
            if (r2 != r13) goto L_0x0var_
            int r13 = r1.mentionCount
            if (r2 != r13) goto L_0x0var_
            if (r12 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            boolean r12 = r12.mentioned
            if (r12 != 0) goto L_0x0f5b
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
        L_0x0f5b:
            r12 = 1
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0var_
            r1.drawCount = r12
            r2 = r4
            goto L_0x0var_
        L_0x0var_:
            r1.drawCount = r14
            r2 = 0
        L_0x0var_:
            int r13 = r1.mentionCount
            if (r13 == 0) goto L_0x0var_
            r1.drawMention = r12
            java.lang.String r12 = "@"
            goto L_0x0var_
        L_0x0var_:
            r1.drawMention = r14
            goto L_0x0var_
        L_0x0var_:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isOut()
            if (r13 == 0) goto L_0x0fe3
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 != 0) goto L_0x0fe3
            if (r10 == 0) goto L_0x0fe3
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r13 != 0) goto L_0x0fe3
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0f9d
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r13 = 1
            r1.drawClock = r13
            r1.drawError = r10
            goto L_0x0fec
        L_0x0f9d:
            r10 = 0
            r13 = 1
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x0fb4
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r13
            r1.drawCount = r10
            r1.drawMention = r10
            goto L_0x0fec
        L_0x0fb4:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x0fe1
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x0fd5
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0fd3
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0fd3
            goto L_0x0fd5
        L_0x0fd3:
            r10 = 0
            goto L_0x0fd6
        L_0x0fd5:
            r10 = 1
        L_0x0fd6:
            r1.drawCheck1 = r10
            r10 = 1
            r1.drawCheck2 = r10
            r10 = 0
            r1.drawClock = r10
            r1.drawError = r10
            goto L_0x0fec
        L_0x0fe1:
            r10 = 0
            goto L_0x0fec
        L_0x0fe3:
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r10
        L_0x0fec:
            r1.promoDialog = r10
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r13 = r1.dialogsType
            if (r13 != 0) goto L_0x1049
            long r13 = r1.currentDialogId
            r15 = 1
            boolean r13 = r10.isPromoDialog(r13, r15)
            if (r13 == 0) goto L_0x1049
            r1.drawPinBackground = r15
            r1.promoDialog = r15
            int r13 = r10.promoDialogType
            if (r13 != 0) goto L_0x1014
            r10 = 2131627891(0x7f0e0var_, float:1.888306E38)
            java.lang.String r11 = "UseProxySponsor"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r11 = r10
            goto L_0x1049
        L_0x1014:
            if (r13 != r15) goto L_0x1049
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "PsaType_"
            r11.append(r13)
            java.lang.String r13 = r10.promoPsaType
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11)
            boolean r13 = android.text.TextUtils.isEmpty(r11)
            if (r13 == 0) goto L_0x103c
            r11 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
            java.lang.String r13 = "PsaTypeDefault"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
        L_0x103c:
            java.lang.String r13 = r10.promoPsaMessage
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 != 0) goto L_0x1049
            java.lang.String r3 = r10.promoPsaMessage
            r10 = 0
            r1.hasMessageThumb = r10
        L_0x1049:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x105f
            r10 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.String r13 = "ArchivedChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
        L_0x1056:
            r16 = r5
            r5 = r9
            r9 = r12
            r12 = r2
            r2 = r0
            r0 = r6
            r6 = r8
            goto L_0x10bb
        L_0x105f:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x1067
            java.lang.String r10 = r10.title
        L_0x1065:
            r13 = r10
            goto L_0x10ab
        L_0x1067:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            if (r10 == 0) goto L_0x10aa
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r10)
            if (r10 == 0) goto L_0x107b
            r10 = 2131627171(0x7f0e0ca3, float:1.8881599E38)
            java.lang.String r13 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x1065
        L_0x107b:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x10a3
            boolean r10 = r1.useMeForMyMessages
            if (r10 == 0) goto L_0x1091
            r10 = 2131625668(0x7f0e06c4, float:1.887855E38)
            java.lang.String r13 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x1065
        L_0x1091:
            int r10 = r1.dialogsType
            r13 = 3
            if (r10 != r13) goto L_0x1099
            r10 = 1
            r1.drawPinBackground = r10
        L_0x1099:
            r10 = 2131627282(0x7f0e0d12, float:1.8881824E38)
            java.lang.String r13 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x1065
        L_0x10a3:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x1065
        L_0x10aa:
            r13 = r4
        L_0x10ab:
            int r10 = r13.length()
            if (r10 != 0) goto L_0x1056
            r10 = 2131625758(0x7f0e071e, float:1.8878733E38)
            java.lang.String r13 = "HiddenName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x1056
        L_0x10bb:
            if (r0 == 0) goto L_0x10fc
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
            if (r8 != 0) goto L_0x10f3
            int r8 = r44.getMeasuredWidth()
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            int r8 = r8 - r0
            r1.timeLeft = r8
            goto L_0x1103
        L_0x10f3:
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeLeft = r8
            goto L_0x1103
        L_0x10fc:
            r8 = 0
            r1.timeLayout = r8
            r8 = 0
            r1.timeLeft = r8
            r0 = 0
        L_0x1103:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x1117
            int r8 = r44.getMeasuredWidth()
            int r10 = r1.nameLeft
            int r8 = r8 - r10
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            int r8 = r8 - r0
            goto L_0x112b
        L_0x1117:
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
        L_0x112b:
            boolean r10 = r1.drawNameLock
            if (r10 == 0) goto L_0x113e
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
        L_0x113b:
            int r10 = r10 + r11
            int r8 = r8 - r10
            goto L_0x1171
        L_0x113e:
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x114f
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x113b
        L_0x114f:
            boolean r10 = r1.drawNameBroadcast
            if (r10 == 0) goto L_0x1160
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x113b
        L_0x1160:
            boolean r10 = r1.drawNameBot
            if (r10 == 0) goto L_0x1171
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x113b
        L_0x1171:
            boolean r10 = r1.drawClock
            r11 = 1084227584(0x40a00000, float:5.0)
            if (r10 == 0) goto L_0x119f
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r14
            int r8 = r8 - r10
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x118e
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.clockDrawLeft = r0
            goto L_0x1214
        L_0x118e:
            int r14 = r1.timeLeft
            int r14 = r14 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r0
            r1.clockDrawLeft = r14
            int r0 = r1.nameLeft
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x1214
        L_0x119f:
            boolean r10 = r1.drawCheck2
            if (r10 == 0) goto L_0x1214
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r10 = r10.getIntrinsicWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r14
            int r8 = r8 - r10
            boolean r14 = r1.drawCheck1
            if (r14 == 0) goto L_0x11fb
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r14 = r14.getIntrinsicWidth()
            r15 = 1090519040(0x41000000, float:8.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 - r15
            int r8 = r8 - r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x11d4
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.halfCheckDrawLeft = r0
            r10 = 1085276160(0x40b00000, float:5.5)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 - r10
            r1.checkDrawLeft = r0
            goto L_0x1214
        L_0x11d4:
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
            goto L_0x1214
        L_0x11fb:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x1205
            int r0 = r1.timeLeft
            int r0 = r0 - r10
            r1.checkDrawLeft1 = r0
            goto L_0x1214
        L_0x1205:
            int r14 = r1.timeLeft
            int r14 = r14 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r0
            r1.checkDrawLeft1 = r14
            int r0 = r1.nameLeft
            int r0 = r0 + r10
            r1.nameLeft = r0
        L_0x1214:
            boolean r0 = r1.dialogMuted
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1238
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x1238
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x1238
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x1273
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
            goto L_0x1273
        L_0x1238:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1252
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x1273
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
            goto L_0x1273
        L_0x1252:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x1273
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r10 = r1.drawScam
            r11 = 1
            if (r10 != r11) goto L_0x1262
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1264
        L_0x1262:
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1264:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x1273
            int r10 = r1.nameLeft
            int r10 = r10 + r0
            r1.nameLeft = r10
        L_0x1273:
            r32 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r15 = java.lang.Math.max(r0, r8)
            r8 = 32
            r10 = 10
            java.lang.String r0 = r13.replace(r10, r8)     // Catch:{ Exception -> 0x12cb }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x12cb }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x12cb }
            r8 = r8[r10]     // Catch:{ Exception -> 0x12cb }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r32)     // Catch:{ Exception -> 0x12cb }
            int r10 = r15 - r10
            float r10 = (float) r10     // Catch:{ Exception -> 0x12cb }
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x12cb }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r8, r10, r11)     // Catch:{ Exception -> 0x12cb }
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x12cb }
            if (r8 == 0) goto L_0x12af
            boolean r8 = r8.hasHighlightedWords()     // Catch:{ Exception -> 0x12cb }
            if (r8 == 0) goto L_0x12af
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x12cb }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x12cb }
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8)     // Catch:{ Exception -> 0x12cb }
            if (r8 == 0) goto L_0x12af
            r25 = r8
            goto L_0x12b1
        L_0x12af:
            r25 = r0
        L_0x12b1:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x12cb }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x12cb }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x12cb }
            r26 = r8[r10]     // Catch:{ Exception -> 0x12cb }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x12cb }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r27 = r15
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x12cb }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x12cb }
            goto L_0x12cf
        L_0x12cb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x12cf:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1382
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x12d9
            goto L_0x1382
        L_0x12d9:
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
            if (r10 == 0) goto L_0x133b
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
            goto L_0x1350
        L_0x133b:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r10
            r1.messageLeft = r10
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r11 = 1116078080(0x42860000, float:67.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r10
        L_0x1350:
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
            goto L_0x142c
        L_0x1382:
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
            if (r4 == 0) goto L_0x13e8
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
            goto L_0x13fd
        L_0x13e8:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10 = 1116340224(0x428a0000, float:69.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r4
        L_0x13fd:
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
        L_0x142c:
            r4 = r0
            r8 = r21
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1454
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x144c
            int r0 = r44.getMeasuredWidth()
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 - r10
            r1.pinLeft = r0
            goto L_0x1454
        L_0x144c:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x1454:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1488
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r8 = r8 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x1471
            int r0 = r44.getMeasuredWidth()
            r9 = 1107820544(0x42080000, float:34.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.errorLeft = r0
            goto L_0x1483
        L_0x1471:
            r9 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.errorLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x1483:
            r17 = r6
            r6 = r15
            goto L_0x15a7
        L_0x1488:
            if (r12 != 0) goto L_0x14b3
            if (r9 == 0) goto L_0x148d
            goto L_0x14b3
        L_0x148d:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x14ad
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 + r9
            int r8 = r8 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x14ad
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x14ad:
            r9 = 0
            r1.drawCount = r9
            r1.drawMention = r9
            goto L_0x1483
        L_0x14b3:
            if (r12 == 0) goto L_0x1513
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
            if (r10 != 0) goto L_0x14ff
            int r0 = r44.getMeasuredWidth()
            int r10 = r1.countWidth
            int r0 = r0 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r10
            r1.countLeft = r0
            goto L_0x150f
        L_0x14ff:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r10
            int r10 = r1.messageLeft
            int r10 = r10 + r0
            r1.messageLeft = r10
            int r10 = r1.messageNameLeft
            int r10 = r10 + r0
            r1.messageNameLeft = r10
        L_0x150f:
            r10 = 1
            r1.drawCount = r10
            goto L_0x1516
        L_0x1513:
            r10 = 0
            r1.countWidth = r10
        L_0x1516:
            r0 = r8
            if (r9 == 0) goto L_0x15a3
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x1553
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
            goto L_0x155c
        L_0x1553:
            r17 = r6
            r6 = r15
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.mentionWidth = r8
        L_0x155c:
            int r8 = r1.mentionWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r8 = r8 + r9
            int r0 = r0 - r8
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x1583
            int r8 = r44.getMeasuredWidth()
            int r9 = r1.mentionWidth
            int r8 = r8 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r8 = r8 - r9
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x157e
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r9 = r9 + r10
            goto L_0x157f
        L_0x157e:
            r9 = 0
        L_0x157f:
            int r8 = r8 - r9
            r1.mentionLeft = r8
            goto L_0x159f
        L_0x1583:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x1591
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r10 = r10 + r11
            goto L_0x1592
        L_0x1591:
            r10 = 0
        L_0x1592:
            int r9 = r9 + r10
            r1.mentionLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r8
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r8
            r1.messageNameLeft = r9
        L_0x159f:
            r8 = 1
            r1.drawMention = r8
            goto L_0x15a6
        L_0x15a3:
            r17 = r6
            r6 = r15
        L_0x15a6:
            r8 = r0
        L_0x15a7:
            if (r16 == 0) goto L_0x15fb
            if (r3 != 0) goto L_0x15ad
            r3 = r20
        L_0x15ad:
            java.lang.String r0 = r3.toString()
            int r3 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r3 <= r9) goto L_0x15be
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r9)
        L_0x15be:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x15c6
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x15c8
        L_0x15c6:
            if (r2 == 0) goto L_0x15d1
        L_0x15c8:
            r3 = 32
            r9 = 10
            java.lang.String r0 = r0.replace(r9, r3)
            goto L_0x15d9
        L_0x15d1:
            java.lang.String r3 = "\n\n"
            java.lang.String r9 = "\n"
            java.lang.String r0 = r0.replace(r3, r9)
        L_0x15d9:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r3 = r3[r9]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r9 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r9, r10)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x15fb
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r0)
            if (r0 == 0) goto L_0x15fb
            r3 = r0
        L_0x15fb:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r8 = java.lang.Math.max(r0, r8)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x160b
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x1661
        L_0x160b:
            if (r2 == 0) goto L_0x1661
            int r9 = r1.currentDialogFolderId
            if (r9 == 0) goto L_0x1616
            int r9 = r1.currentDialogFolderDialogsCount
            r10 = 1
            if (r9 != r10) goto L_0x1661
        L_0x1616:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1646 }
            if (r0 == 0) goto L_0x162b
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1646 }
            if (r0 == 0) goto L_0x162b
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1646 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1646 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x1646 }
            if (r0 == 0) goto L_0x162b
            r2 = r0
        L_0x162b:
            android.text.TextPaint r34 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1646 }
            android.text.Layout$Alignment r36 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1646 }
            r37 = 1065353216(0x3var_, float:1.0)
            r38 = 0
            r39 = 0
            android.text.TextUtils$TruncateAt r40 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1646 }
            r42 = 1
            r33 = r2
            r35 = r8
            r41 = r8
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r33, r34, r35, r36, r37, r38, r39, r40, r41, r42)     // Catch:{ Exception -> 0x1646 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1646 }
            goto L_0x164a
        L_0x1646:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x164a:
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
            goto L_0x1689
        L_0x1661:
            r9 = 0
            r1.messageNameLayout = r9
            if (r0 != 0) goto L_0x1674
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x166b
            goto L_0x1674
        L_0x166b:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1689
        L_0x1674:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r10 = 1101529088(0x41a80000, float:21.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = r4 + r10
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1689:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1723 }
            if (r0 != 0) goto L_0x1691
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1723 }
            if (r4 == 0) goto L_0x16a6
        L_0x1691:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1723 }
            if (r4 == 0) goto L_0x16a6
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1723 }
            r10 = 1
            if (r4 <= r10) goto L_0x16a6
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1723 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x1723 }
            r5 = r0[r3]     // Catch:{ Exception -> 0x1723 }
            r24 = r2
            r25 = r5
            r2 = r9
            goto L_0x16bf
        L_0x16a6:
            if (r0 != 0) goto L_0x16ac
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1723 }
            if (r0 == 0) goto L_0x16ae
        L_0x16ac:
            if (r2 == 0) goto L_0x16bb
        L_0x16ae:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r32)     // Catch:{ Exception -> 0x1723 }
            int r0 = r8 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1723 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1723 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r5, r0, r4)     // Catch:{ Exception -> 0x1723 }
        L_0x16bb:
            r24 = r3
            r25 = r5
        L_0x16bf:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1723 }
            if (r0 != 0) goto L_0x16f5
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1723 }
            if (r0 == 0) goto L_0x16c8
            goto L_0x16f5
        L_0x16c8:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1723 }
            if (r0 == 0) goto L_0x16e0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x1723 }
            int r0 = r0 + r7
            int r8 = r8 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1723 }
            if (r0 == 0) goto L_0x16e0
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x1723 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x1723 }
            int r7 = r7 + r2
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x1723 }
        L_0x16e0:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1723 }
            android.text.Layout$Alignment r13 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1723 }
            r14 = 1065353216(0x3var_, float:1.0)
            r15 = 0
            r16 = 0
            r9 = r0
            r10 = r24
            r11 = r25
            r12 = r8
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x1723 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1723 }
            goto L_0x1727
        L_0x16f5:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1723 }
            if (r0 == 0) goto L_0x1700
            if (r2 == 0) goto L_0x1700
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x1723 }
            int r8 = r8 + r0
        L_0x1700:
            android.text.Layout$Alignment r27 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1723 }
            r28 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x1723 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x1723 }
            r30 = 0
            android.text.TextUtils$TruncateAt r31 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1723 }
            if (r2 == 0) goto L_0x1714
            r33 = 1
            goto L_0x1716
        L_0x1714:
            r33 = 2
        L_0x1716:
            r26 = r8
            r29 = r0
            r32 = r8
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r24, r25, r26, r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x1723 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x1723 }
            goto L_0x1727
        L_0x1723:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1727:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x185d
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x17e6
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17e6
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x1777
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x1777
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x1777
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
            goto L_0x17ce
        L_0x1777:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x179f
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
            goto L_0x17ce
        L_0x179f:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x17ce
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
            if (r7 != r9) goto L_0x17c0
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x17c2
        L_0x17c0:
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x17c2:
            int r7 = r7.getIntrinsicWidth()
            double r9 = (double) r7
            java.lang.Double.isNaN(r9)
            double r4 = r4 - r9
            int r4 = (int) r4
            r1.nameMuteLeft = r4
        L_0x17ce:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x17e6
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x17e6
            int r0 = r1.nameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.nameLeft = r0
        L_0x17e6:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1827
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1827
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x17f7:
            if (r2 >= r0) goto L_0x181d
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x181c
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
            goto L_0x17f7
        L_0x181c:
            r3 = 0
        L_0x181d:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x1827
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x1827:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x18e5
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18e5
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x18e5
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r8
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x18e5
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x18e5
        L_0x185d:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x18aa
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18aa
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r6
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1891
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1891
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1891:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x189d
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x189d
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x18aa
        L_0x189d:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x18aa:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18cd
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18cd
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x18b7:
            if (r3 >= r0) goto L_0x18c6
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x18b7
        L_0x18c6:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x18cd:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x18e5
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x18e5
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x18e5:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x192b
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x192b
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1927 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1927 }
            r8 = r17
            r2 = 1
            if (r8 < r0) goto L_0x18fd
            int r6 = r0 + -1
            goto L_0x18fe
        L_0x18fd:
            r6 = r8
        L_0x18fe:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x1927 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1927 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1927 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1927 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x1927 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x1927 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x1927 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x1927 }
            if (r0 == 0) goto L_0x191e
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x1927 }
            int r0 = r0 + r2
        L_0x191e:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x1927 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1927 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x1927 }
            goto L_0x192b
        L_0x1927:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x192b:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x195a
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x195a
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x194b
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x195a
        L_0x194b:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x195a:
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
            org.telegram.ui.Cells.-$$Lambda$DialogCell$tmNqj-NiK7sTMFrlcDIRZtThiuY r3 = new org.telegram.ui.Cells.-$$Lambda$DialogCell$tmNqj-NiK7sTMFrlcDIRZtThiuY
            r3.<init>()
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
    /* JADX WARNING: Removed duplicated region for block: B:574:0x0eda  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x114c  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x1160  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x1173  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x118d  */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x11b9  */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x1222  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x1279  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x128c  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x12a6  */
    /* JADX WARNING: Removed duplicated region for block: B:729:0x12d0  */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x12fe  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x1312  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x1338  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1358  */
    /* JADX WARNING: Removed duplicated region for block: B:768:? A[RETURN, SYNTHETIC] */
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
            r2 = 2131627838(0x7f0e0f3e, float:1.8882952E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x00d6:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r2 = 2131625762(0x7f0e0722, float:1.8878741E38)
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
            r2 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
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
            r2 = 2131627657(0x7f0e0e89, float:1.8882585E38)
            java.lang.String r3 = "SwipeUnmute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x012f:
            r2 = 2131627649(0x7f0e0e81, float:1.8882568E38)
            java.lang.String r3 = "SwipeMute"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x013d:
            int r2 = r8.currentAccount
            int r2 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r2)
            if (r2 != r14) goto L_0x0159
            r2 = 2131627646(0x7f0e0e7e, float:1.8882562E38)
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
            r2 = 2131627648(0x7f0e0e80, float:1.8882566E38)
            java.lang.String r3 = "SwipeMarkAsUnread"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x0178:
            r2 = 2131627647(0x7f0e0e7f, float:1.8882564E38)
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
            r2 = 2131627658(0x7f0e0e8a, float:1.8882587E38)
            java.lang.String r3 = "SwipeUnpin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01a2:
            r2 = 2131627650(0x7f0e0e82, float:1.888257E38)
            java.lang.String r3 = "SwipePin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01b1:
            r2 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r3 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r4
            goto L_0x0105
        L_0x01c0:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r2 = 2131627829(0x7f0e0var_, float:1.8882933E38)
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
            if (r0 == 0) goto L_0x0ed3
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0ed3
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0ed6
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0ed6
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0ed6
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x0e16
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0dfe
            int r0 = r0.expires
            int r2 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0e14
        L_0x0dfe:
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r8.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x0e16
        L_0x0e14:
            r6 = 1
            goto L_0x0e17
        L_0x0e16:
            r6 = 0
        L_0x0e17:
            if (r6 != 0) goto L_0x0e20
            float r0 = r8.onlineProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0ecf
        L_0x0e20:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0e32
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0e2f
            goto L_0x0e32
        L_0x0e2f:
            r15 = 1090519040(0x41000000, float:8.0)
            goto L_0x0e34
        L_0x0e32:
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e34:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0e57
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e50
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e4e
            goto L_0x0e50
        L_0x0e4e:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e50:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0e6e
        L_0x0e57:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0e68
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0e66
            goto L_0x0e68
        L_0x0e66:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0e68:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0e6e:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
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
            if (r6 == 0) goto L_0x0eba
            float r0 = r8.onlineProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x0ecf
            float r1 = (float) r12
            float r1 = r1 / r20
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x0ecd
            r8.onlineProgress = r14
            goto L_0x0ecd
        L_0x0eba:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ecf
            float r2 = (float) r12
            float r2 = r2 / r20
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0ecd
            r8.onlineProgress = r1
        L_0x0ecd:
            r6 = 1
            goto L_0x0ed1
        L_0x0ecf:
            r6 = r21
        L_0x0ed1:
            r21 = r6
        L_0x0ed3:
            r2 = 0
            goto L_0x1187
        L_0x0ed6:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0ed3
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0ee4
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0ee4
            r6 = 1
            goto L_0x0ee5
        L_0x0ee4:
            r6 = 0
        L_0x0ee5:
            r8.hasCall = r6
            if (r6 != 0) goto L_0x0ef0
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0ed3
        L_0x0ef0:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0var_
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0var_
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r5 = r14 - r0
            goto L_0x0var_
        L_0x0var_:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0var_
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
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
            if (r2 == 0) goto L_0x0f3c
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
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
            float r2 = r2 + r1
            goto L_0x0var_
        L_0x0f3c:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0f4d
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0f4b
            goto L_0x0f4d
        L_0x0f4b:
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0f4d:
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
            if (r3 != 0) goto L_0x0fbd
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
        L_0x0fb8:
            float r10 = r10 * r11
            float r6 = r6 - r10
            goto L_0x10a8
        L_0x0fbd:
            r6 = 1
            if (r3 != r6) goto L_0x0fe2
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
        L_0x0fdd:
            float r10 = r10 * r11
            float r6 = r6 + r10
            goto L_0x10a8
        L_0x0fe2:
            r6 = 2
            if (r3 != r6) goto L_0x1003
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
            goto L_0x0fb8
        L_0x1003:
            r6 = 3
            if (r3 != r6) goto L_0x1022
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
            goto L_0x0fdd
        L_0x1022:
            r6 = 4
            if (r3 != r6) goto L_0x1042
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
            goto L_0x0fb8
        L_0x1042:
            r6 = 5
            if (r3 != r6) goto L_0x1064
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
            goto L_0x0fdd
        L_0x1064:
            r6 = 6
            if (r3 != r6) goto L_0x1089
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
            goto L_0x10a8
        L_0x1089:
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
            goto L_0x0fdd
        L_0x10a8:
            float r10 = r8.chatCallProgress
            int r10 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r10 < 0) goto L_0x10b2
            int r10 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r10 >= 0) goto L_0x10be
        L_0x10b2:
            r32.save()
            float r10 = r8.chatCallProgress
            float r11 = r10 * r5
            float r10 = r10 * r5
            r9.scale(r11, r10, r2, r0)
        L_0x10be:
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
            if (r0 < 0) goto L_0x113b
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x113e
        L_0x113b:
            r32.restore()
        L_0x113e:
            float r0 = r8.innerProgress
            float r1 = (float) r12
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 < 0) goto L_0x115c
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x115c
            r2 = 0
            r8.progressStage = r2
        L_0x115c:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x1173
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 >= 0) goto L_0x1171
            float r1 = r1 / r20
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x1171
            r8.chatCallProgress = r14
        L_0x1171:
            r2 = 0
            goto L_0x1185
        L_0x1173:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x1185
            float r1 = r1 / r20
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x1185
            r8.chatCallProgress = r2
        L_0x1185:
            r21 = 1
        L_0x1187:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1190
            r32.restore()
        L_0x1190:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x11b5
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x11b5
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x11b5
            r32.save()
            int r0 = r31.getMeasuredWidth()
            int r1 = r31.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r32.restore()
        L_0x11b5:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x121a
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x11d9
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x11c9
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x11c9
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x11d9
        L_0x11c9:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x11d2
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x11d2
            goto L_0x11d9
        L_0x11d2:
            r0 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x11da
        L_0x11d9:
            r2 = 0
        L_0x11da:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x11ff
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
            goto L_0x121a
        L_0x11ff:
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
            goto L_0x121b
        L_0x121a:
            r10 = 1
        L_0x121b:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x1269
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x122c
            r32.restore()
            goto L_0x1269
        L_0x122c:
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
        L_0x1269:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x1277
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x1275
            goto L_0x1277
        L_0x1275:
            r1 = 0
            goto L_0x12a0
        L_0x1277:
            if (r0 == 0) goto L_0x128c
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x1275
            float r1 = (float) r12
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x128a
            r8.reorderIconProgress = r14
        L_0x128a:
            r1 = 0
            goto L_0x129e
        L_0x128c:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x12a0
            float r2 = (float) r12
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x129e
            r8.reorderIconProgress = r1
        L_0x129e:
            r6 = 1
            goto L_0x12a2
        L_0x12a0:
            r6 = r21
        L_0x12a2:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x12d0
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x12fa
            float r2 = (float) r12
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x12b9
            r8.archiveBackgroundProgress = r1
        L_0x12b9:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12f9
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x12f9
        L_0x12d0:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x12fa
            float r1 = (float) r12
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x12e3
            r8.archiveBackgroundProgress = r14
        L_0x12e3:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x12f9
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x12f9:
            r6 = 1
        L_0x12fa:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x130e
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r12
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x130d
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x130d:
            r6 = 1
        L_0x130e:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x1338
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x1324
            float r1 = (float) r12
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x1324
            r8.currentRevealBounceProgress = r14
            r6 = 1
        L_0x1324:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r1 >= 0) goto L_0x1356
            float r1 = (float) r12
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x1355
            r8.currentRevealProgress = r14
            goto L_0x1355
        L_0x1338:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x1342
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x1342:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1356
            float r2 = (float) r12
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1355
            r8.currentRevealProgress = r1
        L_0x1355:
            r6 = 1
        L_0x1356:
            if (r6 == 0) goto L_0x135b
            r31.invalidate()
        L_0x135b:
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
