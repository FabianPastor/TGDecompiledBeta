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

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r0.status;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkOnline() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$User r0 = r2.user
            if (r0 == 0) goto L_0x0032
            boolean r1 = r0.self
            if (r1 != 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x001a
            int r0 = r0.expires
            int r1 = r2.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            if (r0 > r1) goto L_0x0030
        L_0x001a:
            int r0 = r2.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r1 = r2.user
            int r1 = r1.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.containsKey(r1)
            if (r0 == 0) goto L_0x0032
        L_0x0030:
            r0 = 1
            goto L_0x0033
        L_0x0032:
            r0 = 0
        L_0x0033:
            if (r0 == 0) goto L_0x0038
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0039
        L_0x0038:
            r0 = 0
        L_0x0039:
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

    /* JADX WARNING: Code restructure failed: missing block: B:264:0x060b, code lost:
        if (r2.post_messages == false) goto L_0x05e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0617, code lost:
        if (r2.kicked != false) goto L_0x05e7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1019:0x1800  */
    /* JADX WARNING: Removed duplicated region for block: B:1055:0x18a1 A[Catch:{ Exception -> 0x18ce }] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x18a4 A[Catch:{ Exception -> 0x18ce }] */
    /* JADX WARNING: Removed duplicated region for block: B:1059:0x18be A[Catch:{ Exception -> 0x18ce }] */
    /* JADX WARNING: Removed duplicated region for block: B:1067:0x18da  */
    /* JADX WARNING: Removed duplicated region for block: B:1076:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x09bb  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0ba2 A[SYNTHETIC, Splitter:B:523:0x0ba2] */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0bbf  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0beb  */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:616:0x0db8  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0e53  */
    /* JADX WARNING: Removed duplicated region for block: B:637:0x0e5b  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e69  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0e71  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0e8e  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0f3e  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0fb7  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x0fc2  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x0ffb  */
    /* JADX WARNING: Removed duplicated region for block: B:725:0x100d  */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x1069  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x10aa  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x10b7  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x10c7  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x10df  */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x10ee  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1127  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x114f  */
    /* JADX WARNING: Removed duplicated region for block: B:794:0x11d2  */
    /* JADX WARNING: Removed duplicated region for block: B:797:0x11e8  */
    /* JADX WARNING: Removed duplicated region for block: B:819:0x125c A[Catch:{ Exception -> 0x127b }] */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x125f A[Catch:{ Exception -> 0x127b }] */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x1289  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x1330  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x13dd  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x1402  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1432  */
    /* JADX WARNING: Removed duplicated region for block: B:887:0x154d  */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x160b  */
    /* JADX WARNING: Removed duplicated region for block: B:930:0x1614  */
    /* JADX WARNING: Removed duplicated region for block: B:940:0x163a A[Catch:{ Exception -> 0x16bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1643 A[ADDED_TO_REGION, Catch:{ Exception -> 0x16bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:949:0x165d A[Catch:{ Exception -> 0x16bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:964:0x16ab A[Catch:{ Exception -> 0x16bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x16ae A[Catch:{ Exception -> 0x16bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:971:0x16c4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r35 = this;
            r1 = r35
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
            r19 = 1101004800(0x41a00000, float:20.0)
            r3 = 33
            r20 = 1102053376(0x41b00000, float:22.0)
            r8 = 150(0x96, float:2.1E-43)
            r21 = 1117257728(0x42980000, float:76.0)
            r22 = 1099956224(0x41900000, float:18.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            r2 = 2
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x035a
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
            int r0 = r35.getMeasuredWidth()
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
            int r0 = r35.getMeasuredWidth()
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
            int r0 = r35.getMeasuredWidth()
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
            int r0 = r35.getMeasuredWidth()
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
            r0 = 2131625582(0x7f0e066e, float:1.8878376E38)
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
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
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
            r15 = r9
            r16 = r10
            r10 = r12
            r8 = 1
            r9 = 0
            r12 = r11
            goto L_0x1067
        L_0x035a:
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x0375
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0363
            goto L_0x0375
        L_0x0363:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x036e
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r14
            goto L_0x0386
        L_0x036e:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x0386
        L_0x0375:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x0380
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r14
            goto L_0x0386
        L_0x0380:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
        L_0x0386:
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 == 0) goto L_0x040f
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x05b8
            r1.drawNameLock = r5
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x03d4
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0399
            goto L_0x03d4
        L_0x0399:
            r14 = 1099169792(0x41840000, float:16.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03ba
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x05b8
        L_0x03ba:
            int r14 = r35.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x05b8
        L_0x03d4:
            r14 = 1095237632(0x41480000, float:12.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03f5
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x05b8
        L_0x03f5:
            int r14 = r35.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
            goto L_0x05b8
        L_0x040f:
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x05b8
            org.telegram.tgnet.TLRPC$Chat r14 = r1.chat
            if (r14 == 0) goto L_0x0512
            boolean r8 = r14.scam
            if (r8 == 0) goto L_0x0423
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r8.checkText()
            goto L_0x0433
        L_0x0423:
            boolean r8 = r14.fake
            if (r8 == 0) goto L_0x042f
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r8.checkText()
            goto L_0x0433
        L_0x042f:
            boolean r8 = r14.verified
            r1.drawVerified = r8
        L_0x0433:
            boolean r8 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r8 == 0) goto L_0x05b8
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x04a9
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x0440
            goto L_0x04a9
        L_0x0440:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r14 = r8.id
            if (r14 < 0) goto L_0x045e
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0453
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x0453
            goto L_0x045e
        L_0x0453:
            r1.drawNameGroup = r5
            r8 = 1099694080(0x418CLASSNAME, float:17.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            goto L_0x0468
        L_0x045e:
            r1.drawNameBroadcast = r5
            r8 = 1099169792(0x41840000, float:16.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
        L_0x0468:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x0488
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x047d
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x047f
        L_0x047d:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x047f:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x0488:
            int r8 = r35.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r8 = r8 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x0498
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x049a
        L_0x0498:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x049a:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x04a9:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r14 = r8.id
            if (r14 < 0) goto L_0x04c7
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x04bc
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x04bc
            goto L_0x04c7
        L_0x04bc:
            r1.drawNameGroup = r5
            r8 = 1096286208(0x41580000, float:13.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            goto L_0x04d1
        L_0x04c7:
            r1.drawNameBroadcast = r5
            r8 = 1095237632(0x41480000, float:12.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
        L_0x04d1:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x04f1
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x04e6
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e8
        L_0x04e6:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e8:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x04f1:
            int r8 = r35.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r8 = r8 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x0501
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0503
        L_0x0501:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0503:
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x0512:
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            if (r8 == 0) goto L_0x05b8
            boolean r14 = r8.scam
            if (r14 == 0) goto L_0x0522
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r8.checkText()
            goto L_0x0532
        L_0x0522:
            boolean r14 = r8.fake
            if (r14 == 0) goto L_0x052e
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r8.checkText()
            goto L_0x0532
        L_0x052e:
            boolean r8 = r8.verified
            r1.drawVerified = r8
        L_0x0532:
            boolean r8 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r8 == 0) goto L_0x05b8
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = r8.bot
            if (r8 == 0) goto L_0x05b8
            r1.drawNameBot = r5
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x0580
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x0547
            goto L_0x0580
        L_0x0547:
            r8 = 1099169792(0x41840000, float:16.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x0567
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x0567:
            int r8 = r35.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r8 = r8 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x0580:
            r8 = 1095237632(0x41480000, float:12.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x05a0
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 + r14
            r1.nameLeft = r8
            goto L_0x05b8
        L_0x05a0:
            int r8 = r35.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r8 = r8 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r8 = r8 - r14
            r1.nameLockLeft = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r8
        L_0x05b8:
            int r8 = r1.lastMessageDate
            if (r8 != 0) goto L_0x05c4
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 == 0) goto L_0x05c4
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            int r8 = r8.date
        L_0x05c4:
            boolean r14 = r1.isDialogCell
            if (r14 == 0) goto L_0x061d
            int r14 = r1.currentAccount
            org.telegram.messenger.MediaDataController r14 = org.telegram.messenger.MediaDataController.getInstance(r14)
            long r2 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r14.getDraft(r2, r6)
            r1.draftMessage = r2
            if (r2 == 0) goto L_0x05f3
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x05e9
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x05e7
            goto L_0x05e9
        L_0x05e7:
            r2 = 0
            goto L_0x061a
        L_0x05e9:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            int r2 = r2.date
            if (r8 <= r2) goto L_0x05f3
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x05e7
        L_0x05f3:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x060d
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x060d
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x060d
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x05e7
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x05e7
        L_0x060d:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x0620
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x05e7
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x0620
            goto L_0x05e7
        L_0x061a:
            r1.draftMessage = r2
            goto L_0x0620
        L_0x061d:
            r2 = 0
            r1.draftMessage = r2
        L_0x0620:
            if (r0 == 0) goto L_0x067a
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r8, r6)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0648
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x0649
        L_0x0648:
            r2 = 0
        L_0x0649:
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
        L_0x0675:
            r2 = 2
        L_0x0676:
            r8 = 1
        L_0x0677:
            r11 = 0
            goto L_0x0e65
        L_0x067a:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0708
            r0 = 2131625150(0x7f0e04be, float:1.88775E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x06b4
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x06b2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x069d
            goto L_0x06b2
        L_0x069d:
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r8 = "chats_draft"
            r2.<init>(r8)
            int r8 = r0.length()
            r11 = 33
            r3.setSpan(r2, r6, r8, r11)
            goto L_0x0675
        L_0x06b2:
            r3 = r4
            goto L_0x0675
        L_0x06b4:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r3 = r2.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r3 <= r8) goto L_0x06c4
            java.lang.String r2 = r2.substring(r6, r8)
        L_0x06c4:
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
            if (r3 != 0) goto L_0x06f4
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x06f4
            org.telegram.ui.Components.ForegroundColorSpanThemable r3 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r8 = "chats_draft"
            r3.<init>(r8)
            int r8 = r0.length()
            int r8 = r8 + r5
            r11 = 33
            r2.setSpan(r3, r6, r8, r11)
        L_0x06f4:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r3 = r3[r8]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r19)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r8, r6)
            goto L_0x0675
        L_0x0708:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x0720
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625666(0x7f0e06c2, float:1.8878546E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x071b:
            r0 = 0
            r2 = 2
            r6 = 1
            goto L_0x0676
        L_0x0720:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x07b2
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x0792
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x073c
            r0 = 2131625241(0x7f0e0519, float:1.8877684E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071b
        L_0x073c:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0754
            r0 = 2131624478(0x7f0e021e, float:1.8876137E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x071b
        L_0x0754:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x0762
            r0 = 2131625242(0x7f0e051a, float:1.8877686E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071b
        L_0x0762:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x07af
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x0788
            r0 = 2131625230(0x7f0e050e, float:1.8877662E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x071b
        L_0x0788:
            r0 = 2131625229(0x7f0e050d, float:1.887766E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x071b
        L_0x0792:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x07af
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x07af
            r0 = 2131627095(0x7f0e0CLASSNAME, float:1.8881445E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 0
            r2 = 2
            r6 = 1
            r8 = 0
            r10 = 0
            goto L_0x0677
        L_0x07af:
            r3 = r4
            goto L_0x071b
        L_0x07b2:
            int r0 = r0.getFromChatId()
            if (r0 <= 0) goto L_0x07c8
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            r2 = 0
            goto L_0x07d9
        L_0x07c8:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            r2 = r0
            r0 = 0
        L_0x07d9:
            int r3 = r1.dialogsType
            r8 = 3
            if (r3 != r8) goto L_0x07f8
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x07f8
            r0 = 2131627095(0x7f0e0CLASSNAME, float:1.8881445E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r0 = 0
            r2 = 2
            r6 = 0
            r8 = 1
            r10 = 0
        L_0x07f5:
            r11 = 0
            goto L_0x0e57
        L_0x07f8:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x080e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x080e
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x080e
            java.lang.CharSequence r0 = r35.formatArchivedDialogNames()
            r3 = r0
            r0 = 1
            r2 = 2
        L_0x080b:
            r6 = 0
            r8 = 0
            goto L_0x07f5
        L_0x080e:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r3.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r8 == 0) goto L_0x083f
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x082f
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x082c
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x082f
        L_0x082c:
            r0 = r4
            r10 = 0
            goto L_0x0833
        L_0x082f:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0833:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            r3 = r0
            r0 = 1
            r2 = 2
        L_0x083c:
            r6 = 0
            r8 = 1
            goto L_0x07f5
        L_0x083f:
            int r8 = r1.currentDialogFolderId
            if (r8 != 0) goto L_0x0937
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r1.encryptedChat
            if (r8 != 0) goto L_0x0937
            boolean r3 = r3.needDrawBluredPreview()
            if (r3 != 0) goto L_0x0937
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 != 0) goto L_0x0865
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isNewGif()
            if (r3 != 0) goto L_0x0865
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x0937
        L_0x0865:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isWebpage()
            if (r3 == 0) goto L_0x0878
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            java.lang.String r3 = r3.type
            goto L_0x0879
        L_0x0878:
            r3 = 0
        L_0x0879:
            java.lang.String r8 = "app"
            boolean r8 = r8.equals(r3)
            if (r8 != 0) goto L_0x0937
            java.lang.String r8 = "profile"
            boolean r8 = r8.equals(r3)
            if (r8 != 0) goto L_0x0937
            java.lang.String r8 = "article"
            boolean r8 = r8.equals(r3)
            if (r8 != 0) goto L_0x0937
            if (r3 == 0) goto L_0x089b
            java.lang.String r8 = "telegram_"
            boolean r3 = r3.startsWith(r8)
            if (r3 != 0) goto L_0x0937
        L_0x089b:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.photoThumbs
            r8 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            int r14 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r14)
            if (r3 != r8) goto L_0x08b4
            r8 = 0
        L_0x08b4:
            if (r3 == 0) goto L_0x0937
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVideo()
            r1.drawPlay = r14
            java.lang.String r14 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.mediaExists
            if (r15 != 0) goto L_0x0903
            int r15 = r1.currentAccount
            org.telegram.messenger.DownloadController r15 = org.telegram.messenger.DownloadController.getInstance(r15)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r15.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r6 != 0) goto L_0x0903
            int r6 = r1.currentAccount
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)
            boolean r6 = r6.isLoadingFile(r14)
            if (r6 == 0) goto L_0x08e5
            goto L_0x0903
        L_0x08e5:
            org.telegram.messenger.ImageReceiver r6 = r1.thumbImage
            r25 = 0
            r26 = 0
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLObject r8 = r8.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r3, r8)
            r29 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r31 = 0
            java.lang.String r28 = "20_20"
            r24 = r6
            r30 = r3
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (java.lang.String) r29, (java.lang.Object) r30, (int) r31)
            goto L_0x0935
        L_0x0903:
            org.telegram.messenger.MessageObject r6 = r1.message
            int r14 = r6.type
            if (r14 != r5) goto L_0x0912
            if (r8 == 0) goto L_0x090e
            int r14 = r8.size
            goto L_0x090f
        L_0x090e:
            r14 = 0
        L_0x090f:
            r29 = r14
            goto L_0x0914
        L_0x0912:
            r29 = 0
        L_0x0914:
            org.telegram.messenger.ImageReceiver r14 = r1.thumbImage
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForObject(r8, r6)
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r3, r6)
            r30 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r32 = 0
            java.lang.String r26 = "20_20"
            java.lang.String r28 = "20_20"
            r24 = r14
            r31 = r3
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x0935:
            r3 = 0
            goto L_0x0938
        L_0x0937:
            r3 = 1
        L_0x0938:
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            if (r6 == 0) goto L_0x0CLASSNAME
            int r8 = r6.id
            if (r8 <= 0) goto L_0x0CLASSNAME
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x0950
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0CLASSNAME
        L_0x0950:
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L_0x0963
            r0 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0961:
            r6 = r0
            goto L_0x09b5
        L_0x0963:
            org.telegram.messenger.MessageObject r6 = r1.message
            if (r6 == 0) goto L_0x0972
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0972
            java.lang.String r6 = r6.from_name
            if (r6 == 0) goto L_0x0972
            goto L_0x09b5
        L_0x0972:
            if (r0 == 0) goto L_0x09a7
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0988
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x097d
            goto L_0x0988
        L_0x097d:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0961
        L_0x0988:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r2 == 0) goto L_0x0998
            r0 = 2131625657(0x7f0e06b9, float:1.8878528E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0961
        L_0x0998:
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0961
        L_0x09a7:
            if (r2 == 0) goto L_0x09b2
            java.lang.String r0 = r2.title
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0961
        L_0x09b2:
            java.lang.String r0 = "DELETED"
            goto L_0x0961
        L_0x09b5:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r2 = r0.caption
            if (r2 == 0) goto L_0x0a28
            java.lang.String r0 = r2.toString()
            int r2 = r0.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r2 <= r8) goto L_0x09cc
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r8)
        L_0x09cc:
            if (r3 != 0) goto L_0x09d1
            r2 = r4
        L_0x09cf:
            r3 = 2
            goto L_0x0a00
        L_0x09d1:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x09dc
            java.lang.String r2 = "📹 "
            goto L_0x09cf
        L_0x09dc:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isVoice()
            if (r2 == 0) goto L_0x09e7
            java.lang.String r2 = "🎤 "
            goto L_0x09cf
        L_0x09e7:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isMusic()
            if (r2 == 0) goto L_0x09f2
            java.lang.String r2 = "🎧 "
            goto L_0x09cf
        L_0x09f2:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.isPhoto()
            if (r2 == 0) goto L_0x09fd
            java.lang.String r2 = "🖼 "
            goto L_0x09cf
        L_0x09fd:
            java.lang.String r2 = "📎 "
            goto L_0x09cf
        L_0x0a00:
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r2 = 32
            r11 = 10
            java.lang.String r0 = r0.replace(r11, r2)
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r2 = 0
            r8[r2] = r0
            r8[r5] = r6
            java.lang.String r0 = java.lang.String.format(r12, r8)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b8f
        L_0x0a28:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            if (r2 == 0) goto L_0x0b0c
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0b0c
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0a6b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            r0 = 18
            if (r11 < r0) goto L_0x0a5b
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r3 = 0
            r0[r3] = r2
            java.lang.String r2 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0ad2
        L_0x0a5b:
            r3 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0[r3] = r2
            java.lang.String r2 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0ad2
        L_0x0a6b:
            r3 = 0
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0a92
            r8 = 18
            if (r11 < r8) goto L_0x0a83
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r3] = r2
            java.lang.String r2 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0ad2
        L_0x0a83:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r0[r3] = r2
            java.lang.String r2 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r2, r0)
            goto L_0x0ad2
        L_0x0a92:
            int r2 = r0.type
            r8 = 14
            if (r2 != r8) goto L_0x0acc
            r2 = 18
            if (r11 < r2) goto L_0x0ab4
            r2 = 2
            java.lang.Object[] r8 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0ad2
        L_0x0ab4:
            r2 = 2
            java.lang.Object[] r8 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0ad2
        L_0x0acc:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0ad2:
            r2 = 32
            r3 = 10
            java.lang.String r0 = r0.replace(r3, r2)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = 0
            r3[r2] = r0
            r3[r5] = r6
            java.lang.String r0 = java.lang.String.format(r12, r3)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0b06 }
            java.lang.String r3 = "chats_attachMessage"
            r0.<init>(r3)     // Catch:{ Exception -> 0x0b06 }
            if (r13 == 0) goto L_0x0afa
            int r3 = r6.length()     // Catch:{ Exception -> 0x0b06 }
            r8 = 2
            int r3 = r3 + r8
            goto L_0x0afb
        L_0x0afa:
            r3 = 0
        L_0x0afb:
            int r8 = r2.length()     // Catch:{ Exception -> 0x0b06 }
            r11 = 33
            r2.setSpan(r0, r3, r8, r11)     // Catch:{ Exception -> 0x0b06 }
            goto L_0x0b90
        L_0x0b06:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0b90
        L_0x0b0c:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            if (r2 == 0) goto L_0x0b8b
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b62
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b21
            r2 = r0
        L_0x0b21:
            int r0 = r35.getMeasuredWidth()
            r3 = 1121058816(0x42d20000, float:105.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            if (r13 == 0) goto L_0x0b48
            boolean r3 = android.text.TextUtils.isEmpty(r6)
            if (r3 != 0) goto L_0x0b3f
            float r0 = (float) r0
            java.lang.String r3 = r6.toString()
            float r3 = r9.measureText(r3)
            float r0 = r0 - r3
            int r0 = (int) r0
        L_0x0b3f:
            float r0 = (float) r0
            java.lang.String r3 = ": "
            float r3 = r9.measureText(r3)
            float r0 = r0 - r3
            int r0 = (int) r0
        L_0x0b48:
            if (r0 <= 0) goto L_0x0b60
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            r8 = 0
            java.lang.Object r3 = r3.get(r8)
            java.lang.String r3 = (java.lang.String) r3
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r3, r0, r9, r11)
            java.lang.String r2 = r0.toString()
            goto L_0x0b7b
        L_0x0b60:
            r8 = 0
            goto L_0x0b7b
        L_0x0b62:
            r8 = 0
            int r0 = r2.length()
            r3 = 150(0x96, float:2.1E-43)
            if (r0 <= r3) goto L_0x0b6f
            java.lang.String r2 = r2.substring(r8, r3)
        L_0x0b6f:
            r3 = 32
            r11 = 10
            java.lang.String r0 = r2.replace(r11, r3)
            java.lang.String r2 = r0.trim()
        L_0x0b7b:
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r8] = r2
            r0[r5] = r6
            java.lang.String r0 = java.lang.String.format(r12, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b8f
        L_0x0b8b:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x0b8f:
            r2 = r0
        L_0x0b90:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0b98
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0ba2
        L_0x0b98:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0bbf
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0bbf
        L_0x0ba2:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0bb8 }
            java.lang.String r3 = "chats_nameMessage"
            r0.<init>(r3)     // Catch:{ Exception -> 0x0bb8 }
            int r3 = r6.length()     // Catch:{ Exception -> 0x0bb8 }
            int r3 = r3 + r5
            r8 = 33
            r11 = 0
            r2.setSpan(r0, r11, r3, r8)     // Catch:{ Exception -> 0x0bb6 }
            r0 = r3
            goto L_0x0bc1
        L_0x0bb6:
            r0 = move-exception
            goto L_0x0bba
        L_0x0bb8:
            r0 = move-exception
            r3 = 0
        L_0x0bba:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0bc1
        L_0x0bbf:
            r0 = 0
            r3 = 0
        L_0x0bc1:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r8 = r8[r11]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r12 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r8, r11, r12)
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x0be7
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r8)
            if (r8 == 0) goto L_0x0be7
            r2 = r8
        L_0x0be7:
            boolean r8 = r1.hasMessageThumb
            if (r8 == 0) goto L_0x0CLASSNAME
            boolean r8 = r2 instanceof android.text.SpannableStringBuilder
            if (r8 != 0) goto L_0x0bf5
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r2)
            r2 = r8
        L_0x0bf5:
            r8 = r2
            android.text.SpannableStringBuilder r8 = (android.text.SpannableStringBuilder) r8
            java.lang.String r11 = " "
            r8.insert(r3, r11)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r11 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r12 = r7 + 6
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.<init>(r12)
            int r12 = r3 + 1
            r13 = 33
            r8.setSpan(r11, r3, r12, r13)
        L_0x0CLASSNAME:
            r11 = r0
            r3 = r2
            r0 = 1
            r2 = 2
            r8 = 0
            goto L_0x0e57
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Photo r6 = r2.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x0CLASSNAME
            int r6 = r2.ttl_seconds
            if (r6 == 0) goto L_0x0CLASSNAME
            r0 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0CLASSNAME:
            r2 = 2
            goto L_0x0db4
        L_0x0CLASSNAME:
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x0c4f
            org.telegram.tgnet.TLRPC$Document r6 = r2.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x0c4f
            int r6 = r2.ttl_seconds
            if (r6 == 0) goto L_0x0c4f
            r0 = 2131624383(0x7f0e01bf, float:1.8875944E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0CLASSNAME
        L_0x0c4f:
            java.lang.CharSequence r6 = r0.caption
            if (r6 == 0) goto L_0x0d00
            if (r3 != 0) goto L_0x0CLASSNAME
            r0 = r4
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "📹 "
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0c6b
            java.lang.String r0 = "🎤 "
            goto L_0x0CLASSNAME
        L_0x0c6b:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "🎧 "
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "🖼 "
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r0 = "📎 "
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0ceb
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0ceb
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r35.getMeasuredWidth()
            r6 = 1122893824(0x42ee0000, float:119.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            if (r13 == 0) goto L_0x0cc3
            r6 = 0
            boolean r8 = android.text.TextUtils.isEmpty(r6)
            if (r8 != 0) goto L_0x0cba
            float r3 = (float) r3
            java.lang.String r8 = r6.toString()
            float r6 = r9.measureText(r8)
            float r3 = r3 - r6
            int r3 = (int) r3
        L_0x0cba:
            float r3 = (float) r3
            java.lang.String r6 = ": "
            float r6 = r9.measureText(r6)
            float r3 = r3 - r6
            int r3 = (int) r3
        L_0x0cc3:
            if (r3 <= 0) goto L_0x0cda
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r8 = 0
            java.lang.Object r6 = r6.get(r8)
            java.lang.String r6 = (java.lang.String) r6
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r6, r3, r9, r8)
            java.lang.String r2 = r2.toString()
        L_0x0cda:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0CLASSNAME
        L_0x0ceb:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0CLASSNAME
        L_0x0d00:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0d1e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "📊 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0d1b:
            r2 = 2
            goto L_0x0da0
        L_0x0d1e:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0d3e
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
            goto L_0x0d1b
        L_0x0d3e:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0d5d
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
            goto L_0x0da0
        L_0x0d5d:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0d95
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d95
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r35.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r8 = 0
            java.lang.Object r6 = r6.get(r8)
            java.lang.String r6 = (java.lang.String) r6
            r8 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r3, r9, r8)
            java.lang.String r0 = r0.toString()
            goto L_0x0d99
        L_0x0d95:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0d99:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0da0:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            if (r6 == 0) goto L_0x0db4
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0db4
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r9 = r3[r6]
        L_0x0db4:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0e53
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0df4
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0df4
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r35.getMeasuredWidth()
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
            goto L_0x0e05
        L_0x0df4:
            r8 = 0
            int r3 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r3 <= r6) goto L_0x0e01
            java.lang.CharSequence r0 = r0.subSequence(r8, r6)
        L_0x0e01:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e05:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0e0f
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0e0f:
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
            if (r6 == 0) goto L_0x0e4f
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r6)
            if (r3 == 0) goto L_0x0e4f
            goto L_0x0e50
        L_0x0e4f:
            r3 = r0
        L_0x0e50:
            r0 = 1
            goto L_0x080b
        L_0x0e53:
            r3 = r0
            r0 = 1
            goto L_0x083c
        L_0x0e57:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x0e5f
            java.lang.CharSequence r6 = r35.formatArchivedDialogNames()
        L_0x0e5f:
            r34 = r8
            r8 = r0
            r0 = r6
            r6 = r34
        L_0x0e65:
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r1.draftMessage
            if (r12 == 0) goto L_0x0e71
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0e8a
        L_0x0e71:
            int r12 = r1.lastMessageDate
            if (r12 == 0) goto L_0x0e7b
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0e8a
        L_0x0e7b:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 == 0) goto L_0x0e89
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0e8a
        L_0x0e89:
            r12 = r4
        L_0x0e8a:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x0ea0
            r14 = 0
            r1.drawCheck1 = r14
            r1.drawCheck2 = r14
            r1.drawClock = r14
            r1.drawCount = r14
            r1.drawMention = r14
            r1.drawError = r14
            r2 = 0
            r10 = 0
            r13 = 0
            goto L_0x0f9b
        L_0x0ea0:
            r14 = 0
            int r15 = r1.currentDialogFolderId
            if (r15 == 0) goto L_0x0ee0
            int r13 = r1.unreadCount
            int r15 = r1.mentionCount
            int r16 = r13 + r15
            if (r16 <= 0) goto L_0x0ed9
            if (r13 <= r15) goto L_0x0ec3
            r1.drawCount = r5
            r1.drawMention = r14
            java.lang.Object[] r2 = new java.lang.Object[r5]
            int r13 = r13 + r15
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r2[r14] = r13
            java.lang.String r13 = "%d"
            java.lang.String r2 = java.lang.String.format(r13, r2)
            goto L_0x0ede
        L_0x0ec3:
            r1.drawCount = r14
            r1.drawMention = r5
            java.lang.Object[] r2 = new java.lang.Object[r5]
            int r13 = r13 + r15
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r2[r14] = r13
            java.lang.String r13 = "%d"
            java.lang.String r2 = java.lang.String.format(r13, r2)
            r13 = r2
            r2 = 0
            goto L_0x0var_
        L_0x0ed9:
            r1.drawCount = r14
            r1.drawMention = r14
            r2 = 0
        L_0x0ede:
            r13 = 0
            goto L_0x0var_
        L_0x0ee0:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0eea
            r1.drawCount = r14
            r2 = 0
            r10 = 0
            r14 = 0
            goto L_0x0f1a
        L_0x0eea:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0f0e
            if (r2 != r5) goto L_0x0efc
            int r14 = r1.mentionCount
            if (r2 != r14) goto L_0x0efc
            if (r13 == 0) goto L_0x0efc
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x0f0e
        L_0x0efc:
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14 = 0
            r13[r14] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r13)
            goto L_0x0f1a
        L_0x0f0e:
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0var_
            r1.drawCount = r5
            r2 = r4
            goto L_0x0f1a
        L_0x0var_:
            r1.drawCount = r14
            r2 = 0
        L_0x0f1a:
            int r13 = r1.mentionCount
            if (r13 == 0) goto L_0x0var_
            r1.drawMention = r5
            java.lang.String r13 = "@"
            goto L_0x0var_
        L_0x0var_:
            r1.drawMention = r14
            goto L_0x0ede
        L_0x0var_:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isOut()
            if (r14 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$DraftMessage r14 = r1.draftMessage
            if (r14 != 0) goto L_0x0var_
            if (r10 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r14 = r14.action
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r14 != 0) goto L_0x0var_
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0f4e
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r5
            r1.drawError = r10
            goto L_0x0f9b
        L_0x0f4e:
            r10 = 0
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x0var_
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r5
            r1.drawCount = r10
            r1.drawMention = r10
            goto L_0x0f9b
        L_0x0var_:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            r10 = 0
            goto L_0x0var_
        L_0x0var_:
            r10 = 1
        L_0x0var_:
            r1.drawCheck1 = r10
            r1.drawCheck2 = r5
            r10 = 0
            r1.drawClock = r10
            r1.drawError = r10
            goto L_0x0f9b
        L_0x0var_:
            r10 = 0
            goto L_0x0f9b
        L_0x0var_:
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r10
        L_0x0f9b:
            r1.promoDialog = r10
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r14 = r1.dialogsType
            if (r14 != 0) goto L_0x0ff7
            long r14 = r1.currentDialogId
            boolean r14 = r10.isPromoDialog(r14, r5)
            if (r14 == 0) goto L_0x0ff7
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r14 = r10.promoDialogType
            if (r14 != 0) goto L_0x0fc2
            r10 = 2131627654(0x7f0e0e86, float:1.8882579E38)
            java.lang.String r12 = "UseProxySponsor"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r12 = r10
            goto L_0x0ff7
        L_0x0fc2:
            if (r14 != r5) goto L_0x0ff7
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r14 = "PsaType_"
            r12.append(r14)
            java.lang.String r14 = r10.promoPsaType
            r12.append(r14)
            java.lang.String r12 = r12.toString()
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12)
            boolean r14 = android.text.TextUtils.isEmpty(r12)
            if (r14 == 0) goto L_0x0fea
            r12 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            java.lang.String r14 = "PsaTypeDefault"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
        L_0x0fea:
            java.lang.String r14 = r10.promoPsaMessage
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x0ff7
            java.lang.String r3 = r10.promoPsaMessage
            r10 = 0
            r1.hasMessageThumb = r10
        L_0x0ff7:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x100d
            r10 = 2131624289(0x7f0e0161, float:1.8875754E38)
            java.lang.String r14 = "ArchivedChats"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
        L_0x1004:
            r16 = r6
            r15 = r9
            r6 = r11
            r9 = r13
            r13 = r10
            r10 = r2
            r2 = r0
            goto L_0x1067
        L_0x100d:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x1014
            java.lang.String r10 = r10.title
            goto L_0x1057
        L_0x1014:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            if (r10 == 0) goto L_0x1056
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r10)
            if (r10 == 0) goto L_0x1028
            r10 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            java.lang.String r14 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x1057
        L_0x1028:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x104f
            boolean r10 = r1.useMeForMyMessages
            if (r10 == 0) goto L_0x103e
            r10 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.String r14 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x1057
        L_0x103e:
            int r10 = r1.dialogsType
            r14 = 3
            if (r10 != r14) goto L_0x1045
            r1.drawPinBackground = r5
        L_0x1045:
            r10 = 2131627094(0x7f0e0CLASSNAME, float:1.8881443E38)
            java.lang.String r14 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x1057
        L_0x104f:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x1057
        L_0x1056:
            r10 = r4
        L_0x1057:
            int r14 = r10.length()
            if (r14 != 0) goto L_0x1004
            r10 = 2131625657(0x7f0e06b9, float:1.8878528E38)
            java.lang.String r14 = "HiddenName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x1004
        L_0x1067:
            if (r8 == 0) goto L_0x10aa
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r12)
            r32 = r6
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r5
            r25 = r12
            r27 = r0
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.timeLayout = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x10a1
            int r5 = r35.getMeasuredWidth()
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x10b3
        L_0x10a1:
            r5 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeLeft = r5
            goto L_0x10b3
        L_0x10aa:
            r32 = r6
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r0 = 0
        L_0x10b3:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x10c7
            int r5 = r35.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            goto L_0x10db
        L_0x10c7:
            int r5 = r35.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1117388800(0x429a0000, float:77.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x10db:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x10ee
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
        L_0x10eb:
            int r6 = r6 + r8
            int r5 = r5 - r6
            goto L_0x1121
        L_0x10ee:
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x10ff
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x10eb
        L_0x10ff:
            boolean r6 = r1.drawNameBroadcast
            if (r6 == 0) goto L_0x1110
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x10eb
        L_0x1110:
            boolean r6 = r1.drawNameBot
            if (r6 == 0) goto L_0x1121
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x10eb
        L_0x1121:
            boolean r6 = r1.drawClock
            r8 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x114f
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r11
            int r5 = r5 - r6
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x113e
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.clockDrawLeft = r0
            goto L_0x11c4
        L_0x113e:
            int r11 = r1.timeLeft
            int r11 = r11 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r0
            r1.clockDrawLeft = r11
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x11c4
        L_0x114f:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x11c4
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r11
            int r5 = r5 - r6
            boolean r11 = r1.drawCheck1
            if (r11 == 0) goto L_0x11ab
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r11 = r11.getIntrinsicWidth()
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 - r12
            int r5 = r5 - r11
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x1184
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.halfCheckDrawLeft = r0
            r6 = 1085276160(0x40b00000, float:5.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x11c4
        L_0x1184:
            int r11 = r1.timeLeft
            int r11 = r11 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r0
            r1.checkDrawLeft = r11
            r0 = 1085276160(0x40b00000, float:5.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r11 = r11 + r0
            r1.halfCheckDrawLeft = r11
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r8 = r8.getIntrinsicWidth()
            int r6 = r6 + r8
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 - r8
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x11c4
        L_0x11ab:
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x11b5
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft1 = r0
            goto L_0x11c4
        L_0x11b5:
            int r11 = r1.timeLeft
            int r11 = r11 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r0
            r1.checkDrawLeft1 = r11
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x11c4:
            boolean r0 = r1.dialogMuted
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x11e8
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x11e8
            int r0 = r1.drawScam
            if (r0 != 0) goto L_0x11e8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x1223
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
            goto L_0x1223
        L_0x11e8:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1202
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x1223
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
            goto L_0x1223
        L_0x1202:
            int r0 = r1.drawScam
            if (r0 == 0) goto L_0x1223
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r1.drawScam
            r11 = 1
            if (r8 != r11) goto L_0x1212
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1214
        L_0x1212:
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1214:
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x1223
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
        L_0x1223:
            r33 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r5 = java.lang.Math.max(r0, r5)
            r8 = 32
            r11 = 10
            java.lang.String r0 = r13.replace(r11, r8)     // Catch:{ Exception -> 0x127b }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x127b }
            int r11 = r1.paintIndex     // Catch:{ Exception -> 0x127b }
            r8 = r8[r11]     // Catch:{ Exception -> 0x127b }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r33)     // Catch:{ Exception -> 0x127b }
            int r11 = r5 - r11
            float r11 = (float) r11     // Catch:{ Exception -> 0x127b }
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x127b }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r8, r11, r12)     // Catch:{ Exception -> 0x127b }
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x127b }
            if (r8 == 0) goto L_0x125f
            boolean r8 = r8.hasHighlightedWords()     // Catch:{ Exception -> 0x127b }
            if (r8 == 0) goto L_0x125f
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x127b }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x127b }
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8)     // Catch:{ Exception -> 0x127b }
            if (r8 == 0) goto L_0x125f
            r25 = r8
            goto L_0x1261
        L_0x125f:
            r25 = r0
        L_0x1261:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x127b }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x127b }
            int r11 = r1.paintIndex     // Catch:{ Exception -> 0x127b }
            r26 = r8[r11]     // Catch:{ Exception -> 0x127b }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x127b }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r27 = r5
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x127b }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x127b }
            goto L_0x127f
        L_0x127b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x127f:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1330
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1289
            goto L_0x1330
        L_0x1289:
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
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.checkDrawTop = r11
            int r8 = r35.getMeasuredWidth()
            r11 = 1119748096(0x42be0000, float:95.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r8 = r8 - r11
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x12eb
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r11
            r1.messageLeft = r11
            int r11 = r35.getMeasuredWidth()
            r12 = 1115684864(0x42800000, float:64.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 - r12
            int r12 = r7 + 11
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r11 - r12
            goto L_0x1300
        L_0x12eb:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r11
            r1.messageLeft = r11
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 1116078080(0x42860000, float:67.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r12 + r11
        L_0x1300:
            org.telegram.messenger.ImageReceiver r13 = r1.avatarImage
            float r11 = (float) r11
            float r14 = (float) r0
            r17 = 1113063424(0x42580000, float:54.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            r21 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r13.setImageCoords(r11, r14, r6, r4)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r12
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            float r12 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r4.setImageCoords(r6, r11, r13, r12)
            goto L_0x13d8
        L_0x1330:
            r21 = r4
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
            int r4 = r35.getMeasuredWidth()
            r6 = 1119485952(0x42ba0000, float:93.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r4 - r6
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1396
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r35.getMeasuredWidth()
            r6 = 1115947008(0x42840000, float:66.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r4 - r6
            goto L_0x13ab
        L_0x1396:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 1116340224(0x428a0000, float:69.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r4
        L_0x13ab:
            org.telegram.messenger.ImageReceiver r11 = r1.avatarImage
            float r4 = (float) r4
            float r12 = (float) r0
            r13 = 1113587712(0x42600000, float:56.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14 = 1113587712(0x42600000, float:56.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r11.setImageCoords(r4, r12, r13, r14)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r6
            r11 = 1106771968(0x41var_, float:31.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r0
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r13 = (float) r13
            r4.setImageCoords(r6, r11, r12, r13)
        L_0x13d8:
            r4 = r0
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x13fe
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x13f6
            int r0 = r35.getMeasuredWidth()
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.pinLeft = r0
            goto L_0x13fe
        L_0x13f6:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x13fe:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1432
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x141b
            int r0 = r35.getMeasuredWidth()
            r6 = 1107820544(0x42080000, float:34.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.errorLeft = r0
            goto L_0x142d
        L_0x141b:
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.errorLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x142d:
            r23 = r5
            r5 = r15
            goto L_0x154b
        L_0x1432:
            if (r10 != 0) goto L_0x145d
            if (r9 == 0) goto L_0x1437
            goto L_0x145d
        L_0x1437:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1457
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1457
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x1457:
            r6 = 0
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x142d
        L_0x145d:
            if (r10 == 0) goto L_0x14bd
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r6 = r6.measureText(r10)
            double r11 = (double) r6
            double r11 = java.lang.Math.ceil(r11)
            int r6 = (int) r11
            int r0 = java.lang.Math.max(r0, r6)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r6 = r1.countWidth
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_CENTER
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r25 = r10
            r27 = r6
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r6
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x14a9
            int r0 = r35.getMeasuredWidth()
            int r6 = r1.countWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r6
            r1.countLeft = r0
            goto L_0x14b9
        L_0x14a9:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.countLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x14b9:
            r6 = 1
            r1.drawCount = r6
            goto L_0x14c0
        L_0x14bd:
            r6 = 0
            r1.countWidth = r6
        L_0x14c0:
            r0 = r8
            if (r9 == 0) goto L_0x1547
            int r6 = r1.currentDialogFolderId
            if (r6 == 0) goto L_0x14f6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r33)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r8 = r8.measureText(r9)
            double r10 = (double) r8
            double r10 = java.lang.Math.ceil(r10)
            int r8 = (int) r10
            int r6 = java.lang.Math.max(r6, r8)
            r1.mentionWidth = r6
            android.text.StaticLayout r6 = new android.text.StaticLayout
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r11 = r1.mentionWidth
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_CENTER
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 0
            r17 = 0
            r8 = r6
            r23 = r5
            r5 = r15
            r15 = r17
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r1.mentionLayout = r6
            goto L_0x14ff
        L_0x14f6:
            r23 = r5
            r5 = r15
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.mentionWidth = r6
        L_0x14ff:
            int r6 = r1.mentionWidth
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = r6 + r8
            int r8 = r0 - r6
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1527
            int r0 = r35.getMeasuredWidth()
            int r6 = r1.mentionWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r6
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x1522
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = r6 + r9
            goto L_0x1523
        L_0x1522:
            r6 = 0
        L_0x1523:
            int r0 = r0 - r6
            r1.mentionLeft = r0
            goto L_0x1543
        L_0x1527:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x1535
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r9 = r9 + r10
            goto L_0x1536
        L_0x1535:
            r9 = 0
        L_0x1536:
            int r0 = r0 + r9
            r1.mentionLeft = r0
            int r0 = r1.messageLeft
            int r0 = r0 + r6
            r1.messageLeft = r0
            int r0 = r1.messageNameLeft
            int r0 = r0 + r6
            r1.messageNameLeft = r0
        L_0x1543:
            r6 = 1
            r1.drawMention = r6
            goto L_0x154b
        L_0x1547:
            r23 = r5
            r5 = r15
            r8 = r0
        L_0x154b:
            if (r16 == 0) goto L_0x159f
            if (r3 != 0) goto L_0x1551
            r3 = r21
        L_0x1551:
            java.lang.String r0 = r3.toString()
            int r3 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r3 <= r6) goto L_0x1562
            r3 = 0
            java.lang.String r0 = r0.substring(r3, r6)
        L_0x1562:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x156a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x156c
        L_0x156a:
            if (r2 == 0) goto L_0x1575
        L_0x156c:
            r3 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r3)
            goto L_0x157d
        L_0x1575:
            java.lang.String r3 = "\n\n"
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r3, r6)
        L_0x157d:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r3 = r3[r6]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r6, r9)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x159f
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r0)
            if (r0 == 0) goto L_0x159f
            r3 = r0
        L_0x159f:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r6 = java.lang.Math.max(r0, r8)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x15af
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x1601
        L_0x15af:
            if (r2 == 0) goto L_0x1601
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x15ba
            int r8 = r1.currentDialogFolderDialogsCount
            r9 = 1
            if (r8 != r9) goto L_0x1601
        L_0x15ba:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15e6 }
            if (r0 == 0) goto L_0x15cf
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x15e6 }
            if (r0 == 0) goto L_0x15cf
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15e6 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x15e6 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x15e6 }
            if (r0 == 0) goto L_0x15cf
            r2 = r0
        L_0x15cf:
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x15e6 }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x15e6 }
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 0
            r15 = 0
            android.text.TextUtils$TruncateAt r16 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x15e6 }
            r18 = 1
            r9 = r2
            r11 = r6
            r17 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x15e6 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x15e6 }
            goto L_0x15ea
        L_0x15e6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x15ea:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r8 = 1109393408(0x42200000, float:40.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 + r8
            float r4 = (float) r4
            r0.setImageY(r4)
            r8 = 0
            goto L_0x1629
        L_0x1601:
            r8 = 0
            r1.messageNameLayout = r8
            if (r0 != 0) goto L_0x1614
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x160b
            goto L_0x1614
        L_0x160b:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1629
        L_0x1614:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r9 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r4 = r4 + r9
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1629:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16bc }
            if (r0 != 0) goto L_0x1631
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16bc }
            if (r4 == 0) goto L_0x1643
        L_0x1631:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x16bc }
            if (r4 == 0) goto L_0x1643
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x16bc }
            r9 = 1
            if (r4 <= r9) goto L_0x1643
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x16bc }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x16bc }
            r15 = r0[r3]     // Catch:{ Exception -> 0x16bc }
            r3 = r2
            r5 = r15
            goto L_0x1659
        L_0x1643:
            if (r0 != 0) goto L_0x1649
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16bc }
            if (r0 == 0) goto L_0x164b
        L_0x1649:
            if (r2 == 0) goto L_0x1658
        L_0x164b:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)     // Catch:{ Exception -> 0x16bc }
            int r0 = r6 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x16bc }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16bc }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r5, r0, r4)     // Catch:{ Exception -> 0x16bc }
        L_0x1658:
            r8 = r2
        L_0x1659:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16bc }
            if (r0 != 0) goto L_0x168e
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16bc }
            if (r0 == 0) goto L_0x1662
            goto L_0x168e
        L_0x1662:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16bc }
            if (r0 == 0) goto L_0x167c
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x16bc }
            int r0 = r0 + r7
            int r6 = r6 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x16bc }
            if (r0 == 0) goto L_0x167c
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x16bc }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x16bc }
            int r7 = r7 + r4
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x16bc }
        L_0x167c:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x16bc }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16bc }
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 0
            r15 = 0
            r8 = r0
            r9 = r3
            r10 = r5
            r11 = r6
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x16bc }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16bc }
            goto L_0x16c0
        L_0x168e:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16bc }
            if (r0 == 0) goto L_0x169b
            if (r8 == 0) goto L_0x169b
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x16bc }
            int r6 = r6 + r0
        L_0x169b:
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16bc }
            r12 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x16bc }
            float r13 = (float) r0     // Catch:{ Exception -> 0x16bc }
            r14 = 0
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16bc }
            if (r8 == 0) goto L_0x16ae
            r17 = 1
            goto L_0x16b0
        L_0x16ae:
            r17 = 2
        L_0x16b0:
            r8 = r3
            r9 = r5
            r10 = r6
            r16 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x16bc }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16bc }
            goto L_0x16c0
        L_0x16bc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16c0:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1800
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1789
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1789
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x1714
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x1714
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x1714
            int r4 = r1.nameLeft
            double r4 = (double) r4
            r7 = r23
            double r8 = (double) r7
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r8
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r8 = r8.getIntrinsicWidth()
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            int r4 = (int) r4
            r1.nameMuteLeft = r4
            goto L_0x1771
        L_0x1714:
            r7 = r23
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x1740
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r8 = (double) r7
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r8
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r8 = r8.getIntrinsicWidth()
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            int r4 = (int) r4
            r1.nameMuteLeft = r4
            goto L_0x1771
        L_0x1740:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x1771
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r8 = (double) r7
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r8
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            int r8 = r1.drawScam
            r9 = 1
            if (r8 != r9) goto L_0x1763
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1765
        L_0x1763:
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1765:
            int r8 = r8.getIntrinsicWidth()
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r4 = r4 - r8
            int r4 = (int) r4
            r1.nameMuteLeft = r4
        L_0x1771:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1789
            double r4 = (double) r7
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x1789
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r4
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x1789:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x17ca
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17ca
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x179a:
            if (r2 >= r0) goto L_0x17c0
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x17bf
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
            goto L_0x179a
        L_0x17bf:
            r3 = 0
        L_0x17c0:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x17ca
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x17ca:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x188c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x188c
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x188c
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x188c
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x188c
        L_0x1800:
            r7 = r23
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1851
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1851
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r7
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1836
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r7
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1836
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1836:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1842
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1842
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1851
        L_0x1842:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1851:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1874
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1874
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x185e:
            if (r3 >= r0) goto L_0x186d
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x185e
        L_0x186d:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1874:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x188c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x188c
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x188c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18d2
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x18d2
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x18ce }
            int r0 = r0.length()     // Catch:{ Exception -> 0x18ce }
            r11 = r32
            r2 = 1
            if (r11 < r0) goto L_0x18a4
            int r6 = r0 + -1
            goto L_0x18a5
        L_0x18a4:
            r6 = r11
        L_0x18a5:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x18ce }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18ce }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x18ce }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18ce }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x18ce }
            double r2 = (double) r0     // Catch:{ Exception -> 0x18ce }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x18ce }
            int r0 = (int) r2     // Catch:{ Exception -> 0x18ce }
            if (r0 == 0) goto L_0x18c5
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x18ce }
            int r0 = r0 + r2
        L_0x18c5:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x18ce }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x18ce }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x18ce }
            goto L_0x18d2
        L_0x18ce:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x18d2:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1901
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1901
            r2 = 0
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r2 = r1.messageLayout
            r3 = 1
            float r2 = r2.getPrimaryHorizontal(r3)
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x18f2
            int r2 = r1.messageLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.statusDrawableLeft = r0
            goto L_0x1901
        L_0x18f2:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 + r2
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r0 = (int) r0
            r1.statusDrawableLeft = r0
        L_0x1901:
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
        MessageObject messageObject3;
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
                if (this.currentDialogId != tLRPC$Dialog.id || (((messageObject2 = this.message) != null && messageObject2.getId() != tLRPC$Dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tLRPC$Dialog.unread_count || this.mentionCount != tLRPC$Dialog.unread_mentions_count || this.markUnread != tLRPC$Dialog.unread_mark || (messageObject3 = this.message) != messageObject || ((messageObject3 == null && messageObject != null) || draft != this.draftMessage || this.drawPin != tLRPC$Dialog.pinned)))) {
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

    /* JADX WARNING: Removed duplicated region for block: B:128:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0191  */
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
            goto L_0x04dd
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
            if (r1 == 0) goto L_0x023a
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
            if (r7 == 0) goto L_0x0152
            r7 = r1 & 64
            if (r7 == 0) goto L_0x0152
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r0.currentDialogId
            java.lang.CharSequence r7 = r7.getPrintingString(r8, r5, r4)
            java.lang.CharSequence r8 = r0.lastPrintString
            if (r8 == 0) goto L_0x0142
            if (r7 == 0) goto L_0x0150
        L_0x0142:
            if (r8 != 0) goto L_0x0146
            if (r7 != 0) goto L_0x0150
        L_0x0146:
            if (r8 == 0) goto L_0x0152
            if (r7 == 0) goto L_0x0152
            boolean r7 = r8.equals(r7)
            if (r7 != 0) goto L_0x0152
        L_0x0150:
            r7 = 1
            goto L_0x0153
        L_0x0152:
            r7 = 0
        L_0x0153:
            if (r7 != 0) goto L_0x0166
            r8 = 32768(0x8000, float:4.5918E-41)
            r8 = r8 & r1
            if (r8 == 0) goto L_0x0166
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x0166
            java.lang.CharSequence r8 = r8.messageText
            java.lang.CharSequence r9 = r0.lastMessageString
            if (r8 == r9) goto L_0x0166
            r7 = 1
        L_0x0166:
            if (r7 != 0) goto L_0x0192
            r8 = r1 & 8192(0x2000, float:1.14794E-41)
            if (r8 == 0) goto L_0x0192
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 == 0) goto L_0x0192
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$Chat r9 = r0.chat
            int r9 = r9.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r9)
            boolean r9 = r8.call_active
            if (r9 == 0) goto L_0x018c
            boolean r8 = r8.call_not_empty
            if (r8 == 0) goto L_0x018c
            r8 = 1
            goto L_0x018d
        L_0x018c:
            r8 = 0
        L_0x018d:
            boolean r9 = r0.hasCall
            if (r8 == r9) goto L_0x0192
            r7 = 1
        L_0x0192:
            if (r7 != 0) goto L_0x019d
            r8 = r1 & 2
            if (r8 == 0) goto L_0x019d
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 != 0) goto L_0x019d
            r7 = 1
        L_0x019d:
            if (r7 != 0) goto L_0x01a8
            r8 = r1 & 1
            if (r8 == 0) goto L_0x01a8
            org.telegram.tgnet.TLRPC$Chat r8 = r0.chat
            if (r8 != 0) goto L_0x01a8
            r7 = 1
        L_0x01a8:
            if (r7 != 0) goto L_0x01b3
            r8 = r1 & 8
            if (r8 == 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 != 0) goto L_0x01b3
            r7 = 1
        L_0x01b3:
            if (r7 != 0) goto L_0x01be
            r8 = r1 & 16
            if (r8 == 0) goto L_0x01be
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            if (r8 != 0) goto L_0x01be
            r7 = 1
        L_0x01be:
            if (r7 != 0) goto L_0x021f
            r8 = r1 & 256(0x100, float:3.59E-43)
            if (r8 == 0) goto L_0x021f
            org.telegram.messenger.MessageObject r8 = r0.message
            if (r8 == 0) goto L_0x01d9
            boolean r9 = r0.lastUnreadState
            boolean r8 = r8.isUnread()
            if (r9 == r8) goto L_0x01d9
            org.telegram.messenger.MessageObject r7 = r0.message
            boolean r7 = r7.isUnread()
            r0.lastUnreadState = r7
            r7 = 1
        L_0x01d9:
            boolean r8 = r0.isDialogCell
            if (r8 == 0) goto L_0x021f
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            long r9 = r0.currentDialogId
            java.lang.Object r8 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r9 == 0) goto L_0x01fd
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r9)
            int r9 = r9.getArchiveUnreadCount()
        L_0x01fb:
            r10 = 0
            goto L_0x0206
        L_0x01fd:
            if (r8 == 0) goto L_0x0204
            int r9 = r8.unread_count
            int r10 = r8.unread_mentions_count
            goto L_0x0206
        L_0x0204:
            r9 = 0
            goto L_0x01fb
        L_0x0206:
            if (r8 == 0) goto L_0x021f
            int r11 = r0.unreadCount
            if (r11 != r9) goto L_0x0216
            boolean r11 = r0.markUnread
            boolean r12 = r8.unread_mark
            if (r11 != r12) goto L_0x0216
            int r11 = r0.mentionCount
            if (r11 == r10) goto L_0x021f
        L_0x0216:
            r0.unreadCount = r9
            r0.mentionCount = r10
            boolean r7 = r8.unread_mark
            r0.markUnread = r7
            r7 = 1
        L_0x021f:
            if (r7 != 0) goto L_0x0234
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x0234
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x0234
            int r8 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r8 == r1) goto L_0x0234
            r0.lastSendState = r1
            r7 = 1
        L_0x0234:
            if (r7 != 0) goto L_0x023a
            r21.invalidate()
            return
        L_0x023a:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r7 = 0
            if (r1 == 0) goto L_0x0257
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r21.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x0255
            long r9 = r1.getDialogId()
            goto L_0x0270
        L_0x0255:
            r9 = r7
            goto L_0x0270
        L_0x0257:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x026b
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r9 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r9)
            if (r1 == 0) goto L_0x026b
            r1 = 1
            goto L_0x026c
        L_0x026b:
            r1 = 0
        L_0x026c:
            r0.dialogMuted = r1
            long r9 = r0.currentDialogId
        L_0x0270:
            int r1 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x0311
            int r1 = (int) r9
            r3 = 32
            long r7 = r9 >> r3
            int r3 = (int) r7
            if (r1 == 0) goto L_0x02c3
            if (r1 >= 0) goto L_0x02b2
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            r0.chat = r1
            boolean r3 = r0.isDialogCell
            if (r3 != 0) goto L_0x02e9
            if (r1 == 0) goto L_0x02e9
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02e9
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            int r3 = r3.channel_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            if (r1 == 0) goto L_0x02e9
            r0.chat = r1
            goto L_0x02e9
        L_0x02b2:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            r0.user = r1
            goto L_0x02e9
        L_0x02c3:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r3)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02e9
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r0.encryptedChat
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            r0.user = r1
        L_0x02e9:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x0311
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x0311
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x0311
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            r0.user = r1
        L_0x0311:
            int r1 = r0.currentDialogFolderId
            r3 = 2
            if (r1 == 0) goto L_0x032f
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
            goto L_0x03aa
        L_0x032f:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x038e
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r1 == 0) goto L_0x0355
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
            goto L_0x03aa
        L_0x0355:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x0374
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x0374
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
            goto L_0x03aa
        L_0x0374:
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
            goto L_0x03aa
        L_0x038e:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x03aa
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
        L_0x03aa:
            if (r23 == 0) goto L_0x04dd
            int r1 = r0.unreadCount
            if (r2 != r1) goto L_0x03b4
            boolean r1 = r0.markUnread
            if (r6 == r1) goto L_0x04dd
        L_0x03b4:
            long r6 = java.lang.System.currentTimeMillis()
            long r8 = r0.lastDialogChangedTime
            long r6 = r6 - r8
            r8 = 100
            int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r1 <= 0) goto L_0x04dd
            android.animation.ValueAnimator r1 = r0.countAnimator
            if (r1 == 0) goto L_0x03c8
            r1.cancel()
        L_0x03c8:
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
            if (r2 == 0) goto L_0x040e
            boolean r1 = r0.markUnread
            if (r1 == 0) goto L_0x03ec
            goto L_0x040e
        L_0x03ec:
            int r1 = r0.unreadCount
            if (r1 != 0) goto L_0x03ff
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 150(0x96, double:7.4E-322)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x041f
        L_0x03ff:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 430(0x1ae, double:2.124E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            goto L_0x041f
        L_0x040e:
            android.animation.ValueAnimator r1 = r0.countAnimator
            r6 = 220(0xdc, double:1.087E-321)
            r1.setDuration(r6)
            android.animation.ValueAnimator r1 = r0.countAnimator
            android.view.animation.OvershootInterpolator r3 = new android.view.animation.OvershootInterpolator
            r3.<init>()
            r1.setInterpolator(r3)
        L_0x041f:
            boolean r1 = r0.drawCount
            if (r1 == 0) goto L_0x04c8
            android.text.StaticLayout r1 = r0.countLayout
            if (r1 == 0) goto L_0x04c8
            java.lang.String r1 = java.lang.String.valueOf(r2)
            int r3 = r0.unreadCount
            java.lang.String r3 = java.lang.String.valueOf(r3)
            int r6 = r1.length()
            int r7 = r3.length()
            if (r6 != r7) goto L_0x04c4
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r1)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r3)
            r8 = 0
        L_0x044b:
            int r10 = r1.length()
            if (r8 >= r10) goto L_0x047b
            char r10 = r1.charAt(r8)
            char r11 = r3.charAt(r8)
            if (r10 != r11) goto L_0x046e
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r9.setSpan(r10, r8, r11, r5)
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            r6.setSpan(r10, r8, r11, r5)
            goto L_0x0478
        L_0x046e:
            org.telegram.ui.Components.EmptyStubSpan r10 = new org.telegram.ui.Components.EmptyStubSpan
            r10.<init>()
            int r11 = r8 + 1
            r7.setSpan(r10, r8, r11, r5)
        L_0x0478:
            int r8 = r8 + 1
            goto L_0x044b
        L_0x047b:
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
            goto L_0x04c8
        L_0x04c4:
            android.text.StaticLayout r1 = r0.countLayout
            r0.countOldLayout = r1
        L_0x04c8:
            int r1 = r0.countWidth
            r0.countWidthOld = r1
            int r1 = r0.countLeft
            r0.countLeftOld = r1
            int r1 = r0.unreadCount
            if (r1 <= r2) goto L_0x04d5
            goto L_0x04d6
        L_0x04d5:
            r4 = 0
        L_0x04d6:
            r0.countAnimationIncrement = r4
            android.animation.ValueAnimator r1 = r0.countAnimator
            r1.start()
        L_0x04dd:
            int r1 = r21.getMeasuredWidth()
            if (r1 != 0) goto L_0x04ee
            int r1 = r21.getMeasuredHeight()
            if (r1 == 0) goto L_0x04ea
            goto L_0x04ee
        L_0x04ea:
            r21.requestLayout()
            goto L_0x04f1
        L_0x04ee:
            r21.buildLayout()
        L_0x04f1:
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
        }
        if (this.isSliding) {
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
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0498  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x04d8  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0568  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x05b9  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0676  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x072e  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0805  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0854  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ba8  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0bdb  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0d1a  */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0faa  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0fbd  */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0fd7  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0fef  */
    /* JADX WARNING: Removed duplicated region for block: B:626:0x1058  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x10af  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x10c2  */
    /* JADX WARNING: Removed duplicated region for block: B:650:0x10da  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x1104  */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x1148  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x1163  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x1171  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x1194  */
    /* JADX WARNING: Removed duplicated region for block: B:699:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r29) {
        /*
            r28 = this;
            r8 = r28
            r9 = r29
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
            r29.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r28.getMeasuredHeight()
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
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 2
            r15 = 0
            r7 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0098
            float r0 = r8.cornerProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x007d
            goto L_0x0098
        L_0x007d:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x0094
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r10)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r15
        L_0x0094:
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x0310
        L_0x0098:
            r29.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r16 = "chats_archivePinBackground"
            java.lang.String r17 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00d3
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00bd
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627607(0x7f0e0e57, float:1.8882483E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x00bd:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131625661(0x7f0e06bd, float:1.8878536E38)
            java.lang.String r3 = "HideOnTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x00d3:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x00ef
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r3
        L_0x00ec:
            r5 = r1
            r4 = r2
            goto L_0x011f
        L_0x00ef:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x0109
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131624273(0x7f0e0151, float:1.8875721E38)
            java.lang.String r3 = "Archive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x0109:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627598(0x7f0e0e4e, float:1.8882465E38)
            java.lang.String r3 = "Unarchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x011f:
            boolean r1 = r8.translationAnimationStarted
            if (r1 != 0) goto L_0x0145
            float r1 = r8.translationX
            float r1 = java.lang.Math.abs(r1)
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0145
            r8.translationAnimationStarted = r7
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setProgress(r10)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.start()
        L_0x0145:
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            float r2 = r8.translationX
            float r3 = r1 + r2
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 >= 0) goto L_0x01c7
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r0 = (float) r0
            float r2 = r3 - r0
            r0 = 0
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            int r6 = r28.getMeasuredHeight()
            float r6 = (float) r6
            android.graphics.Paint r19 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r20 = r1
            r1 = r29
            r7 = r3
            r3 = r0
            r0 = r4
            r4 = r20
            r22 = r5
            r5 = r6
            r6 = r19
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x01cb
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x0195
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
        L_0x0195:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x01cb
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
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r15
            goto L_0x01cb
        L_0x01c7:
            r7 = r3
            r0 = r4
            r22 = r5
        L_0x01cb:
            int r1 = r28.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r14
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x01ea
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x01e7
            goto L_0x01ea
        L_0x01e7:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x01ec
        L_0x01ea:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x01ec:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r14
            int r3 = r3 + r1
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r14
            int r4 = r4 + r2
            float r5 = r8.currentRevealProgress
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 <= 0) goto L_0x0293
            r29.save()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            float r5 = r7 - r5
            int r6 = r28.getMeasuredWidth()
            float r6 = (float) r6
            int r13 = r28.getMeasuredHeight()
            float r13 = (float) r13
            r9.clipRect(r5, r10, r6, r13)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r6 = r22
            r5.setColor(r6)
            int r5 = r3 * r3
            int r6 = r28.getMeasuredHeight()
            int r6 = r4 - r6
            int r13 = r28.getMeasuredHeight()
            int r13 = r4 - r13
            int r6 = r6 * r13
            int r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.sqrt(r5)
            float r5 = (float) r5
            float r3 = (float) r3
            float r4 = (float) r4
            android.view.animation.AccelerateInterpolator r6 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r13 = r8.currentRevealProgress
            float r6 = r6.getInterpolation(r13)
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r3, r4, r5, r6)
            r29.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x0261
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r3 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r3
        L_0x0261:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x0293
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r5 = "Line 1.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r5 = "Line 2.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r5 = "Line 3.**"
            r3.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.commitApplyLayerColors()
            r3 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r3
        L_0x0293:
            r29.save()
            float r1 = (float) r1
            float r2 = (float) r2
            r9.translate(r1, r2)
            float r1 = r8.currentRevealBounceProgress
            int r2 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            r13 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x02c1
            int r2 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x02c1
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r2 = r8.interpolator
            float r1 = r2.getInterpolation(r1)
            float r1 = r1 + r13
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r14
            float r2 = (float) r2
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r14
            float r3 = (float) r3
            r9.scale(r1, r1, r2, r3)
        L_0x02c1:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r15, (int) r15)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.draw(r9)
            r29.restore()
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r28.getMeasuredHeight()
            float r2 = (float) r2
            r9.clipRect(r7, r10, r1, r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r1.measureText(r0)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = r28.getMeasuredWidth()
            r3 = 1110179840(0x422CLASSNAME, float:43.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r1 = r1 / r14
            int r2 = r2 - r1
            float r1 = (float) r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0301
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x02fe
            goto L_0x0301
        L_0x02fe:
            r2 = 1114374144(0x426CLASSNAME, float:59.0)
            goto L_0x0303
        L_0x0301:
            r2 = 1115160576(0x42780000, float:62.0)
        L_0x0303:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r9.drawText(r0, r1, r2, r3)
            r29.restore()
        L_0x0310:
            float r0 = r8.translationX
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x031e
            r29.save()
            float r0 = r8.translationX
            r9.translate(r0, r10)
        L_0x031e:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x0335
            r2 = 0
            r3 = 0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r28.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0335:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0368
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0343
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0368
        L_0x0343:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r1, r2, r13)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r28.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x038e
        L_0x0368:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0370
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x038e
        L_0x0370:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r28.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x038e:
            float r0 = r8.translationX
            java.lang.String r16 = "windowBackgroundWhite"
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x03a1
            float r0 = r8.cornerProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x039d
            goto L_0x03a1
        L_0x039d:
            r17 = 1090519040(0x41000000, float:8.0)
            goto L_0x0453
        L_0x03a1:
            r29.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r28.getMeasuredWidth()
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r28.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r28.getMeasuredHeight()
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
            if (r0 == 0) goto L_0x041e
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x03f1
            float r0 = r8.archiveBackgroundProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x041e
        L_0x03f1:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r1 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            float r2 = r8.archiveBackgroundProgress
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r1, r2, r13)
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
            goto L_0x0427
        L_0x041e:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x042a
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x0427
            goto L_0x042a
        L_0x0427:
            r17 = 1090519040(0x41000000, float:8.0)
            goto L_0x0450
        L_0x042a:
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
        L_0x0450:
            r29.restore()
        L_0x0453:
            float r0 = r8.translationX
            r18 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x046e
            float r0 = r8.cornerProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x0483
            float r1 = (float) r11
            float r1 = r1 / r18
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0480
            r8.cornerProgress = r13
            goto L_0x0480
        L_0x046e:
            float r0 = r8.cornerProgress
            int r1 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r1 <= 0) goto L_0x0483
            float r1 = (float) r11
            float r1 = r1 / r18
            float r0 = r0 - r1
            r8.cornerProgress = r0
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 >= 0) goto L_0x0480
            r8.cornerProgress = r10
        L_0x0480:
            r19 = 1
            goto L_0x0485
        L_0x0483:
            r19 = 0
        L_0x0485:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x0498
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x04d0
        L_0x0498:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x04ab
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x04d0
        L_0x04ab:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x04be
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x04d0
        L_0x04be:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x04d0
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x04d0:
            android.text.StaticLayout r0 = r8.nameLayout
            r20 = 1092616192(0x41200000, float:10.0)
            r22 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x0548
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x04f0
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0524
        L_0x04f0:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0511
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x04fd
            int r0 = r0.type
            if (r0 != r14) goto L_0x04fd
            goto L_0x0511
        L_0x04fd:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0524
        L_0x0511:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0524:
            r29.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x0536
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x0533
            goto L_0x0536
        L_0x0533:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x0538
        L_0x0536:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x0538:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r29.restore()
        L_0x0548:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0564
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0564
            r29.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r29.restore()
        L_0x0564:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x05b2
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x057a
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0599
        L_0x057a:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x058c
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x0599
        L_0x058c:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x0599:
            r29.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x05ab }
            r0.draw(r9)     // Catch:{ Exception -> 0x05ab }
            goto L_0x05af
        L_0x05ab:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05af:
            r29.restore()
        L_0x05b2:
            android.text.StaticLayout r0 = r8.messageLayout
            r7 = 4
            r23 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x0671
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x05e9
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x05d5
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x05fc
        L_0x05d5:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x05fc
        L_0x05e9:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x05fc:
            r29.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x060e }
            r0.draw(r9)     // Catch:{ Exception -> 0x060e }
            goto L_0x0612
        L_0x060e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0612:
            r29.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0671
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0671
            r29.save()
            int r1 = r8.printingStringType
            r2 = 1
            if (r1 == r2) goto L_0x0644
            if (r1 != r7) goto L_0x062a
            goto L_0x0644
        L_0x062a:
            int r1 = r8.statusDrawableLeft
            float r1 = (float) r1
            int r2 = r8.messageTop
            float r2 = (float) r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r0.getIntrinsicHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 / r23
            float r2 = r2 + r3
            r9.translate(r1, r2)
            r6 = 1
            goto L_0x0657
        L_0x0644:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            r6 = 1
            if (r1 != r6) goto L_0x0651
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x0652
        L_0x0651:
            r1 = 0
        L_0x0652:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0657:
            r0.draw(r9)
            int r1 = r8.statusDrawableLeft
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r4 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r4 = r4 + r0
            r8.invalidate(r1, r2, r3, r4)
            r29.restore()
            goto L_0x0672
        L_0x0671:
            r6 = 1
        L_0x0672:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x072e
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x067e
            r1 = 2
            goto L_0x067f
        L_0x067e:
            r1 = 0
        L_0x067f:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0686
            r1 = 4
            goto L_0x0687
        L_0x0686:
            r1 = 0
        L_0x0687:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x0695
            if (r1 == r0) goto L_0x0695
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x0695
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x0695:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x069b
            int r0 = r8.animateToStatusDrawableParams
        L_0x069b:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x06a2
            r21 = 1
            goto L_0x06a4
        L_0x06a2:
            r21 = 0
        L_0x06a4:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x06ab
            r24 = 1
            goto L_0x06ad
        L_0x06ab:
            r24 = 0
        L_0x06ad:
            r0 = r0 & r7
            if (r0 == 0) goto L_0x06b2
            r0 = 1
            goto L_0x06b3
        L_0x06b2:
            r0 = 0
        L_0x06b3:
            if (r1 == 0) goto L_0x0709
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x06bd
            r3 = 1
            goto L_0x06be
        L_0x06bd:
            r3 = 0
        L_0x06be:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x06c4
            r4 = 1
            goto L_0x06c5
        L_0x06c4:
            r4 = 0
        L_0x06c5:
            r1 = r1 & r7
            if (r1 == 0) goto L_0x06ca
            r5 = 1
            goto L_0x06cb
        L_0x06ca:
            r5 = 0
        L_0x06cb:
            if (r21 != 0) goto L_0x06f1
            if (r3 != 0) goto L_0x06f1
            if (r5 == 0) goto L_0x06f1
            if (r4 != 0) goto L_0x06f1
            if (r24 == 0) goto L_0x06f1
            if (r0 == 0) goto L_0x06f1
            r25 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r28
            r2 = r29
            r3 = r21
            r4 = r24
            r21 = r5
            r5 = r0
            r24 = 1
            r6 = r25
            r15 = 1
            r7 = r21
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0719
        L_0x06f1:
            r15 = 1
            r6 = 0
            float r1 = r8.statusDrawableProgress
            float r7 = r13 - r1
            r1 = r28
            r2 = r29
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r21
            r4 = r24
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0719
        L_0x0709:
            r15 = 1
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r28
            r2 = r29
            r3 = r21
            r4 = r24
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0719:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0721
            r1 = 2
            goto L_0x0722
        L_0x0721:
            r1 = 0
        L_0x0722:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0729
            r7 = 4
            goto L_0x072a
        L_0x0729:
            r7 = 0
        L_0x072a:
            int r0 = r0 + r7
            r8.lastStatusDrawableParams = r0
            goto L_0x072f
        L_0x072e:
            r15 = 1
        L_0x072f:
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x0768
            boolean r0 = r8.drawVerified
            if (r0 != 0) goto L_0x0768
            int r0 = r8.drawScam
            if (r0 != 0) goto L_0x0768
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x074b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0748
            goto L_0x074b
        L_0x0748:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x074c
        L_0x074b:
            r6 = 0
        L_0x074c:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 - r2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0758
            r2 = 1096286208(0x41580000, float:13.5)
            goto L_0x075a
        L_0x0758:
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x075a:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x07d7
        L_0x0768:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x07a9
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x077c
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0779
            goto L_0x077c
        L_0x0779:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x077e
        L_0x077c:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x077e:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0795
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0792
            goto L_0x0795
        L_0x0792:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x0797
        L_0x0795:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x0797:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x07d7
        L_0x07a9:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x07d7
            if (r0 != r15) goto L_0x07b2
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x07b4
        L_0x07b2:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x07b4:
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07c2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x07bf
            goto L_0x07c2
        L_0x07bf:
            r2 = 1097859072(0x41700000, float:15.0)
            goto L_0x07c4
        L_0x07c2:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x07c4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            int r0 = r8.drawScam
            if (r0 != r15) goto L_0x07d2
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x07d4
        L_0x07d2:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x07d4:
            r0.draw(r9)
        L_0x07d7:
            boolean r0 = r8.drawReorder
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x07e3
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x07fb
        L_0x07e3:
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
        L_0x07fb:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x0854
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r6 = r8.reorderIconProgress
            float r6 = r13 - r6
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r6 = (float) r1
            int r7 = r8.errorTop
            float r7 = (float) r7
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r21
            float r1 = (float) r1
            int r15 = r8.errorTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r15 = r15 + r2
            float r2 = (float) r15
            r0.set(r6, r7, r1, r2)
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
            goto L_0x0ba2
        L_0x0854:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0883
            boolean r6 = r8.drawMention
            if (r6 != 0) goto L_0x0883
            float r6 = r8.countChangeProgress
            int r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r6 == 0) goto L_0x0863
            goto L_0x0883
        L_0x0863:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0ba2
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
            r0.draw(r9)
            goto L_0x0ba2
        L_0x0883:
            if (r0 != 0) goto L_0x088b
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0aee
        L_0x088b:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0898
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0898
            float r6 = r8.countChangeProgress
            float r6 = r13 - r6
            goto L_0x089a
        L_0x0898:
            float r6 = r8.countChangeProgress
        L_0x089a:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0a18
            if (r0 != 0) goto L_0x08a2
            goto L_0x0a18
        L_0x08a2:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x08ae
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x08ab
            goto L_0x08ae
        L_0x08ab:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x08b0
        L_0x08ae:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x08b0:
            float r7 = r8.reorderIconProgress
            float r7 = r13 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r15 = r8.reorderIconProgress
            float r15 = r13 - r15
            float r15 = r15 * r1
            int r15 = (int) r15
            r7.setAlpha(r15)
            float r7 = r6 * r23
            int r15 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r15 <= 0) goto L_0x08cf
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x08d0
        L_0x08cf:
            r15 = r7
        L_0x08d0:
            int r4 = r8.countLeft
            float r4 = (float) r4
            float r4 = r4 * r15
            int r10 = r8.countLeftOld
            float r10 = (float) r10
            float r26 = r13 - r15
            float r10 = r10 * r26
            float r4 = r4 + r10
            r10 = 1085276160(0x40b00000, float:5.5)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r10 = r4 - r10
            android.graphics.RectF r14 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r5 = r8.countWidth
            float r5 = (float) r5
            float r5 = r5 * r15
            float r5 = r5 + r10
            int r3 = r8.countWidthOld
            float r3 = (float) r3
            float r3 = r3 * r26
            float r5 = r5 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r5 = r5 + r3
            int r3 = r8.countTop
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 + r27
            float r3 = (float) r3
            r14.set(r10, r1, r5, r3)
            r1 = 1056964608(0x3var_, float:0.5)
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x091b
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r7)
            goto L_0x092b
        L_0x091b:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r5 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 - r5
            float r6 = r6 * r23
            float r6 = r13 - r6
            float r3 = r3.getInterpolation(r6)
        L_0x092b:
            float r3 = r3 * r1
            float r3 = r3 + r13
            r29.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r5 = r8.rect
            float r5 = r5.centerY()
            r9.scale(r3, r3, r1, r5)
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1094189056(0x41380000, float:11.5)
            float r6 = r3 * r5
            float r3 = r3 * r5
            r9.drawRoundRect(r1, r6, r3, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            if (r0 == 0) goto L_0x0969
            r29.save()
            int r0 = r8.countTop
            r1 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 + r3
            float r0 = (float) r0
            r9.translate(r4, r0)
            android.text.StaticLayout r0 = r8.countAnimationStableLayout
            r0.draw(r9)
            r29.restore()
        L_0x0969:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r5 = r3 * r15
            int r5 = (int) r5
            r1.setAlpha(r5)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x09a8
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0988
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x098d
        L_0x0988:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x098d:
            float r1 = (float) r1
            float r1 = r1 * r26
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            r5 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r6
            float r1 = r1 + r5
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            r1.draw(r9)
            r29.restore()
            goto L_0x09d7
        L_0x09a8:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x09d7
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x09b8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x09bd
        L_0x09b8:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x09bd:
            float r1 = (float) r1
            float r1 = r1 * r26
            int r5 = r8.countTop
            float r5 = (float) r5
            float r1 = r1 + r5
            r5 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r6
            float r1 = r1 + r5
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countLayout
            r1.draw(r9)
            r29.restore()
        L_0x09d7:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0a0e
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r26
            int r3 = (int) r3
            r1.setAlpha(r3)
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x09f0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x09f4
        L_0x09f0:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x09f4:
            float r1 = (float) r1
            float r1 = r1 * r15
            int r3 = r8.countTop
            float r3 = (float) r3
            float r1 = r1 + r3
            r3 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r5
            float r1 = r1 + r3
            r9.translate(r4, r1)
            android.text.StaticLayout r1 = r8.countOldLayout
            r1.draw(r9)
            r29.restore()
        L_0x0a0e:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r29.restore()
            goto L_0x0aee
        L_0x0a18:
            if (r0 != 0) goto L_0x0a1b
            goto L_0x0a1d
        L_0x0a1b:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0a1d:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0a29
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0a26
            goto L_0x0a29
        L_0x0a26:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0a2b
        L_0x0a29:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0a2b:
            float r1 = r8.reorderIconProgress
            float r1 = r13 - r1
            r3 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r3
            int r1 = (int) r1
            r0.setAlpha(r1)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r8.reorderIconProgress
            float r4 = r13 - r4
            float r4 = r4 * r3
            int r3 = (int) r4
            r1.setAlpha(r3)
            int r1 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r3
            android.graphics.RectF r3 = r8.rect
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
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r10 = r10 + r14
            float r10 = (float) r10
            r3.set(r4, r5, r1, r10)
            int r1 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0abf
            boolean r1 = r8.drawPin
            if (r1 == 0) goto L_0x0aad
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r3 = r8.reorderIconProgress
            float r3 = r13 - r3
            r4 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            r1.setAlpha(r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r3 = r8.pinLeft
            int r4 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r3, (int) r4)
            r29.save()
            float r1 = r13 - r6
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
            r29.restore()
        L_0x0aad:
            r29.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r6, r6, r1, r3)
        L_0x0abf:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r5, r3, r0)
            if (r7 == 0) goto L_0x0ae7
            r29.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            r3 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r4
            float r1 = (float) r1
            r9.translate(r0, r1)
            r7.draw(r9)
            r29.restore()
        L_0x0ae7:
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0aee
            r29.restore()
        L_0x0aee:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0ba2
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
            if (r0 == 0) goto L_0x0b30
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0b30
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0b32
        L_0x0b30:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0b32:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0b6d
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            r29.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            r2 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r3
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r9)
            r29.restore()
            goto L_0x0ba2
        L_0x0b6d:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r1 = r8.reorderIconProgress
            float r6 = r13 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r1 = r1 - r2
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
        L_0x0ba2:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0bc4
            r29.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 + r13
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r9.scale(r0, r0, r1, r2)
        L_0x0bc4:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0bd2
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0bd2
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0bd7
        L_0x0bd2:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0bd7:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0c0f
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0c0f
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
        L_0x0c0f:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0CLASSNAME
            r29.restore()
        L_0x0CLASSNAME:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0d13
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0d13
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0d16
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0d16
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0d16
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x0c5e
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r0.expires
            int r2 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0c5c
        L_0x0CLASSNAME:
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r8.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x0c5e
        L_0x0c5c:
            r0 = 1
            goto L_0x0c5f
        L_0x0c5e:
            r0 = 0
        L_0x0c5f:
            if (r0 != 0) goto L_0x0CLASSNAME
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0d13
        L_0x0CLASSNAME:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0CLASSNAME
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0CLASSNAME:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0c9b
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0CLASSNAME
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0cb2
        L_0x0c9b:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0cac
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0caa
            goto L_0x0cac
        L_0x0caa:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0cac:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0cb2:
            int r1 = (int) r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r16)
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
            if (r0 == 0) goto L_0x0cfe
            float r0 = r8.onlineProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x0d13
            float r1 = (float) r11
            float r1 = r1 / r18
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0d11
            r8.onlineProgress = r13
            goto L_0x0d11
        L_0x0cfe:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d13
            float r2 = (float) r11
            float r2 = r2 / r18
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0d11
            r8.onlineProgress = r1
        L_0x0d11:
            r19 = 1
        L_0x0d13:
            r2 = 0
            goto L_0x0fd1
        L_0x0d16:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0d13
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0d24
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0d24
            r0 = 1
            goto L_0x0d25
        L_0x0d24:
            r0 = 0
        L_0x0d25:
            r8.hasCall = r0
            if (r0 != 0) goto L_0x0d30
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0d13
        L_0x0d30:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0d41
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r6 = r13 - r0
            goto L_0x0d43
        L_0x0d41:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0d43:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0d51
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0d53
        L_0x0d51:
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d53:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0d76
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0d6f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0d6d
            goto L_0x0d6f
        L_0x0d6d:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d6f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0d8d
        L_0x0d76:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0d87
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0d85
            goto L_0x0d87
        L_0x0d85:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d87:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0d8d:
            int r1 = (int) r2
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
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
            float r3 = r3 * r6
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r2, r0, r3, r4)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3.setColor(r4)
            int r3 = r8.progressStage
            r4 = 1077936128(0x40400000, float:3.0)
            if (r3 != 0) goto L_0x0df9
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r10 = r8.innerProgress
            float r5 = r5 * r10
            float r3 = r3 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r10 = (float) r10
            float r14 = r8.innerProgress
        L_0x0df4:
            float r10 = r10 * r14
            float r5 = r5 - r10
            goto L_0x0ef2
        L_0x0df9:
            r5 = 1
            if (r3 != r5) goto L_0x0e20
            r5 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            r5 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            float r10 = r10 * r14
            float r3 = r3 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r10 = (float) r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r14 = r8.innerProgress
        L_0x0e1b:
            float r5 = r5 * r14
            float r5 = r5 + r10
            goto L_0x0ef2
        L_0x0e20:
            r5 = 2
            if (r3 != r5) goto L_0x0e43
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r5 = (float) r5
            float r10 = r8.innerProgress
            float r5 = r5 * r10
            float r3 = r3 + r5
            r5 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r10
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            goto L_0x0df4
        L_0x0e43:
            r5 = 3
            if (r3 != r5) goto L_0x0e66
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r5 = (float) r5
            float r10 = r8.innerProgress
            float r5 = r5 * r10
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r10 = (float) r10
            float r14 = r8.innerProgress
        L_0x0e61:
            float r10 = r10 * r14
            float r5 = r5 + r10
            goto L_0x0ef2
        L_0x0e66:
            r5 = 4
            if (r3 != r5) goto L_0x0e88
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r10 = r8.innerProgress
            float r5 = r5 * r10
            float r3 = r3 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            goto L_0x0df4
        L_0x0e88:
            r5 = 5
            if (r3 != r5) goto L_0x0eac
            r5 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            r5 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            float r10 = r10 * r14
            float r3 = r3 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r10 = (float) r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r14 = r8.innerProgress
            goto L_0x0e1b
        L_0x0eac:
            r5 = 1082130432(0x40800000, float:4.0)
            r10 = 6
            if (r3 != r10) goto L_0x0ed3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            float r10 = r10 * r14
            float r3 = r3 + r10
            r10 = 1084227584(0x40a00000, float:5.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r15 = r8.innerProgress
            float r5 = r5 * r15
            float r5 = r14 - r5
            goto L_0x0ef2
        L_0x0ed3:
            r10 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r10 = r8.innerProgress
            float r5 = r5 * r10
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            float r10 = (float) r10
            float r14 = r8.innerProgress
            goto L_0x0e61
        L_0x0ef2:
            float r10 = r8.chatCallProgress
            int r10 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r10 < 0) goto L_0x0efc
            int r10 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r10 >= 0) goto L_0x0var_
        L_0x0efc:
            r29.save()
            float r10 = r8.chatCallProgress
            float r14 = r10 * r6
            float r10 = r10 * r6
            r9.scale(r14, r10, r2, r0)
        L_0x0var_:
            android.graphics.RectF r2 = r8.rect
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r14 = r0 - r3
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = r15 + r1
            float r15 = (float) r15
            float r3 = r3 + r0
            r2.set(r10, r14, r15, r3)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r10 = (float) r10
            android.graphics.Paint r14 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r10, r14)
            android.graphics.RectF r2 = r8.rect
            r3 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r1 - r10
            float r3 = (float) r3
            float r10 = r0 - r5
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r14 = r1 - r14
            float r14 = (float) r14
            float r0 = r0 + r5
            r2.set(r3, r10, r14, r0)
            android.graphics.RectF r2 = r8.rect
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            android.graphics.Paint r14 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r2, r3, r5, r14)
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
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r2 = (float) r2
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r0, r1, r2, r3)
            float r0 = r8.chatCallProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x0var_
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x0var_
        L_0x0var_:
            r29.restore()
        L_0x0var_:
            float r0 = r8.innerProgress
            float r1 = (float) r11
            r2 = 1137180672(0x43CLASSNAME, float:400.0)
            float r2 = r1 / r2
            float r0 = r0 + r2
            r8.innerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x0fa6
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x0fa6
            r2 = 0
            r8.progressStage = r2
        L_0x0fa6:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x0fbd
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0fbb
            float r1 = r1 / r18
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0fbb
            r8.chatCallProgress = r13
        L_0x0fbb:
            r2 = 0
            goto L_0x0fcf
        L_0x0fbd:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0fcf
            float r1 = r1 / r18
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0fcf
            r8.chatCallProgress = r2
        L_0x0fcf:
            r19 = 1
        L_0x0fd1:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0fda
            r29.restore()
        L_0x0fda:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0feb
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0feb
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0feb
            r0.draw(r9)
        L_0x0feb:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x1050
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x100f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0fff
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x0fff
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x100f
        L_0x0fff:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1008
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x1008
            goto L_0x100f
        L_0x1008:
            r0 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1010
        L_0x100f:
            r2 = 0
        L_0x1010:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1035
            r0 = 0
            int r1 = r28.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r28.getMeasuredWidth()
            int r1 = r1 - r2
            float r5 = (float) r1
            int r1 = r28.getMeasuredHeight()
            int r1 = r1 - r3
            float r6 = (float) r1
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r29
            r2 = r0
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r10
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x1050
        L_0x1035:
            float r2 = (float) r2
            int r0 = r28.getMeasuredHeight()
            r10 = 1
            int r0 = r0 - r10
            float r3 = (float) r0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r28.getMeasuredHeight()
            int r0 = r0 - r10
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r29
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x1051
        L_0x1050:
            r10 = 1
        L_0x1051:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x109f
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1062
            r29.restore()
            goto L_0x109f
        L_0x1062:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r28.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r28.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r28.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x109f:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x10ad
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x10ab
            goto L_0x10ad
        L_0x10ab:
            r1 = 0
            goto L_0x10d6
        L_0x10ad:
            if (r0 == 0) goto L_0x10c2
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x10ab
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x10c0
            r8.reorderIconProgress = r13
        L_0x10c0:
            r1 = 0
            goto L_0x10d4
        L_0x10c2:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x10d6
            float r2 = (float) r11
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x10d4
            r8.reorderIconProgress = r1
        L_0x10d4:
            r19 = 1
        L_0x10d6:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1104
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x112f
            float r2 = (float) r11
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x10ed
            r8.archiveBackgroundProgress = r1
        L_0x10ed:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x112d
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x112d
        L_0x1104:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x112f
            float r1 = (float) r11
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1117
            r8.archiveBackgroundProgress = r13
        L_0x1117:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x112d
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x112d:
            r19 = 1
        L_0x112f:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x1144
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r11
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x1142
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x1142:
            r19 = 1
        L_0x1144:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x1171
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x115b
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x115b
            r8.currentRevealBounceProgress = r13
            r7 = 1
            goto L_0x115d
        L_0x115b:
            r7 = r19
        L_0x115d:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x1192
            float r1 = (float) r11
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1191
            r8.currentRevealProgress = r13
            goto L_0x1191
        L_0x1171:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x117c
            r8.currentRevealBounceProgress = r1
            r7 = 1
            goto L_0x117e
        L_0x117c:
            r7 = r19
        L_0x117e:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1192
            float r2 = (float) r11
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1191
            r8.currentRevealProgress = r1
        L_0x1191:
            r7 = 1
        L_0x1192:
            if (r7 == 0) goto L_0x1197
            r28.invalidate()
        L_0x1197:
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
        if (i2 == 0 && messageObject != null) {
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
