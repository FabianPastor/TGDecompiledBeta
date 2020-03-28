package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.DialogsActivity;

public class DialogCell extends BaseCell {
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
    private PullForegroundDrawable archivedChatsDrawable;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int bottomClip;
    private TLRPC$Chat chat;
    private CheckBox2 checkBox;
    private int checkDrawLeft;
    private int checkDrawTop;
    private boolean clearingDialog;
    private float clipProgress;
    private float cornerProgress;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
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
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClock;
    private boolean drawCount;
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private boolean drawScam;
    private boolean drawVerified;
    private TLRPC$EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
    private int folderId;
    public boolean fullSeparator;
    public boolean fullSeparator2;
    private int halfCheckDrawLeft;
    private int index;
    private BounceInterpolator interpolator = new BounceInterpolator();
    private boolean isDialogCell;
    private boolean isSelected;
    private boolean isSliding;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString;
    private int lastSendState;
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
    private int pinLeft;
    private int pinTop;
    private RectF rect = new RectF();
    private float reorderIconProgress;
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

    public DialogCell(Context context, boolean z, boolean z2) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.useForceThreeLines = z2;
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
        this.currentDialogId = tLRPC$Dialog.id;
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
        update(0);
        checkOnline();
    }

    public void setDialogIndex(int i) {
        this.index = i;
    }

    public void setDialog(CustomDialog customDialog2) {
        this.customDialog = customDialog2;
        this.messageId = 0;
        update(0);
        checkOnline();
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
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
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

    public void setDialog(long j, MessageObject messageObject, int i, boolean z) {
        this.currentDialogId = j;
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSliding = false;
        this.drawRevealBackground = false;
        this.currentRevealProgress = 0.0f;
        this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
        this.avatarImage.onDetachedFromWindow();
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
            this.translationDrawable.setProgress(0.0f);
            this.translationDrawable.setCallback((Drawable.Callback) null);
            this.translationDrawable = null;
            this.translationAnimationStarted = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
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
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                int dp = LocaleController.isRTL ? (i3 - i) - AndroidUtilities.dp(45.0f) : AndroidUtilities.dp(45.0f);
                int dp2 = AndroidUtilities.dp(46.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(dp, dp2, checkBox2.getMeasuredWidth() + dp, this.checkBox.getMeasuredHeight() + dp2);
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

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x05f8, code lost:
        if (r4.post_messages == false) goto L_0x05d4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x035f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0b01  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0b09  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0b26  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0b36  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0bb2  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0bb7  */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0bd2  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0cae  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0ced  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0cf7  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0d07  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0d1f  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0d2e  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0d67  */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0d8f  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0e13  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e29  */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0ea9  */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0fa1  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x0fc6  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0ff7  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x1114  */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x11b0  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x11c8 A[Catch:{ Exception -> 0x122d }] */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11d0 A[Catch:{ Exception -> 0x122d }] */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x11f3 A[Catch:{ Exception -> 0x122d }] */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x121b A[Catch:{ Exception -> 0x122d }] */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x121e A[Catch:{ Exception -> 0x122d }] */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1235  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x135e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r39 = this;
            r1 = r39
            boolean r0 = r1.useForceThreeLines
            r2 = 1099431936(0x41880000, float:17.0)
            r3 = 1097859072(0x41700000, float:15.0)
            r4 = 1098907648(0x41800000, float:16.0)
            r5 = 1
            r6 = 0
            if (r0 != 0) goto L_0x0057
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0013
            goto L_0x0057
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
            goto L_0x009a
        L_0x0057:
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
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r5]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
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
        L_0x009a:
            r1.currentDialogFolderDialogsCount = r6
            boolean r0 = r1.isDialogCell
            if (r0 == 0) goto L_0x00b1
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.util.LongSparseArray<java.lang.CharSequence> r0 = r0.printingStrings
            long r8 = r1.currentDialogId
            java.lang.Object r0 = r0.get(r8)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            goto L_0x00b2
        L_0x00b1:
            r0 = 0
        L_0x00b2:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r8 = r8[r9]
            r1.drawNameGroup = r6
            r1.drawNameBroadcast = r6
            r1.drawNameLock = r6
            r1.drawNameBot = r6
            r1.drawVerified = r6
            r1.drawScam = r6
            r1.drawPinBackground = r6
            org.telegram.tgnet.TLRPC$User r9 = r1.user
            boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r9)
            if (r9 != 0) goto L_0x00d4
            boolean r9 = r1.useMeForMyMessages
            if (r9 != 0) goto L_0x00d4
            r9 = 1
            goto L_0x00d5
        L_0x00d4:
            r9 = 0
        L_0x00d5:
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 18
            if (r10 < r11) goto L_0x00ee
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x00e3
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x00e7
        L_0x00e3:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x00ea
        L_0x00e7:
            java.lang.String r10 = "%2$s: ⁨%1$s⁩"
            goto L_0x00fc
        L_0x00ea:
            java.lang.String r10 = "⁨%s⁩"
            goto L_0x0100
        L_0x00ee:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x00f6
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x00fa
        L_0x00f6:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x00fe
        L_0x00fa:
            java.lang.String r10 = "%2$s: %1$s"
        L_0x00fc:
            r11 = 1
            goto L_0x0101
        L_0x00fe:
            java.lang.String r10 = "%1$s"
        L_0x0100:
            r11 = 0
        L_0x0101:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 == 0) goto L_0x0108
            java.lang.CharSequence r12 = r12.messageText
            goto L_0x0109
        L_0x0108:
            r12 = 0
        L_0x0109:
            r1.lastMessageString = r12
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            r13 = 33
            r14 = 1117782016(0x42a00000, float:80.0)
            r15 = 1118044160(0x42a40000, float:82.0)
            r4 = 10
            r18 = 1101004800(0x41a00000, float:20.0)
            r19 = 1102053376(0x41b00000, float:22.0)
            r3 = 150(0x96, float:2.1E-43)
            r21 = 1099956224(0x41900000, float:18.0)
            r7 = 2
            java.lang.String r2 = ""
            r22 = 1117257728(0x42980000, float:76.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            if (r12 == 0) goto L_0x035f
            int r0 = r12.type
            if (r0 != r7) goto L_0x01ab
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0170
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0135
            goto L_0x0170
        L_0x0135:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0156
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 + r9
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0156:
            int r0 = r39.getMeasuredWidth()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0170:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0191
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 + r9
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0191:
            int r0 = r39.getMeasuredWidth()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x01ab:
            boolean r9 = r12.verified
            r1.drawVerified = r9
            boolean r9 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r9 == 0) goto L_0x0250
            if (r0 != r5) goto L_0x0250
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0209
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c0
            goto L_0x0209
        L_0x01c0:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01e8
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r9 = r1.drawNameGroup
            if (r9 == 0) goto L_0x01dd
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01df
        L_0x01dd:
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01df:
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 + r9
            r1.nameLeft = r0
            goto L_0x027c
        L_0x01e8:
            int r0 = r39.getMeasuredWidth()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r9
            boolean r9 = r1.drawNameGroup
            if (r9 == 0) goto L_0x01f8
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01fa
        L_0x01f8:
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01fa:
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0209:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0230
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r9 = r1.drawNameGroup
            if (r9 == 0) goto L_0x0226
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0228
        L_0x0226:
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0228:
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 + r9
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0230:
            int r0 = r39.getMeasuredWidth()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r9
            boolean r9 = r1.drawNameGroup
            if (r9 == 0) goto L_0x0240
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0242
        L_0x0240:
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0242:
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0250:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x026b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0259
            goto L_0x026b
        L_0x0259:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0264
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0264:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x026b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0276
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x027c
        L_0x0276:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r0
        L_0x027c:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r9 = r0.type
            if (r9 != r5) goto L_0x0306
            r0 = 2131625355(0x7f0e058b, float:1.8877916E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r9 = r1.customDialog
            boolean r11 = r9.isMedia
            if (r11 == 0) goto L_0x02ba
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r8 = r8[r9]
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.messenger.MessageObject r11 = r1.message
            java.lang.CharSequence r11 = r11.messageText
            r9[r6] = r11
            java.lang.String r9 = java.lang.String.format(r10, r9)
            android.text.SpannableStringBuilder r9 = android.text.SpannableStringBuilder.valueOf(r9)
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan
            java.lang.String r11 = "chats_attachMessage"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.<init>(r11)
            int r11 = r9.length()
            r9.setSpan(r10, r6, r11, r13)
            goto L_0x02f2
        L_0x02ba:
            java.lang.String r9 = r9.message
            int r11 = r9.length()
            if (r11 <= r3) goto L_0x02c6
            java.lang.String r9 = r9.substring(r6, r3)
        L_0x02c6:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x02e4
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x02cf
            goto L_0x02e4
        L_0x02cf:
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r12 = 32
            java.lang.String r9 = r9.replace(r4, r12)
            r11[r6] = r9
            r11[r5] = r0
            java.lang.String r9 = java.lang.String.format(r10, r11)
            android.text.SpannableStringBuilder r9 = android.text.SpannableStringBuilder.valueOf(r9)
            goto L_0x02f2
        L_0x02e4:
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r11[r6] = r9
            r11[r5] = r0
            java.lang.String r9 = java.lang.String.format(r10, r11)
            android.text.SpannableStringBuilder r9 = android.text.SpannableStringBuilder.valueOf(r9)
        L_0x02f2:
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r10 = r10[r11]
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r11, r6)
            r10 = 0
            goto L_0x0314
        L_0x0306:
            java.lang.String r9 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0312
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r8 = r0[r8]
        L_0x0312:
            r0 = 0
            r10 = 1
        L_0x0314:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.unread_count
            if (r12 == 0) goto L_0x0334
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13[r6] = r12
            java.lang.String r12 = "%d"
            java.lang.String r12 = java.lang.String.format(r12, r13)
            goto L_0x0337
        L_0x0334:
            r1.drawCount = r6
            r12 = 0
        L_0x0337:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            boolean r13 = r13.sent
            if (r13 == 0) goto L_0x0346
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r1.drawClock = r6
            r1.drawError = r6
            goto L_0x034e
        L_0x0346:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r6
        L_0x034e:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            java.lang.String r13 = r13.name
            r4 = r0
            r14 = r8
            r24 = r9
            r25 = r10
            r7 = r11
            r0 = r13
            r8 = 0
            r10 = 1
            r13 = r12
            goto L_0x0cac
        L_0x035f:
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x037a
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x0368
            goto L_0x037a
        L_0x0368:
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x0373
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r12
            goto L_0x038b
        L_0x0373:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r12
            goto L_0x038b
        L_0x037a:
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x0385
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r12
            goto L_0x038b
        L_0x0385:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r12
        L_0x038b:
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = r1.encryptedChat
            if (r12 == 0) goto L_0x0414
            int r12 = r1.currentDialogFolderId
            if (r12 != 0) goto L_0x05a5
            r1.drawNameLock = r5
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x03d9
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x039e
            goto L_0x03d9
        L_0x039e:
            r12 = 1099169792(0x41840000, float:16.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockTop = r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x03bf
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r12 = r12 + r14
            r1.nameLeft = r12
            goto L_0x05a5
        L_0x03bf:
            int r12 = r39.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r12 = r12 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r12 = r12 - r14
            r1.nameLockLeft = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r12
            goto L_0x05a5
        L_0x03d9:
            r12 = 1095237632(0x41480000, float:12.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.nameLockTop = r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x03fa
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r12 = r12 + r14
            r1.nameLeft = r12
            goto L_0x05a5
        L_0x03fa:
            int r12 = r39.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r12 = r12 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r12 = r12 - r14
            r1.nameLockLeft = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r12
            goto L_0x05a5
        L_0x0414:
            int r12 = r1.currentDialogFolderId
            if (r12 != 0) goto L_0x05a5
            org.telegram.tgnet.TLRPC$Chat r12 = r1.chat
            if (r12 == 0) goto L_0x050b
            boolean r4 = r12.scam
            if (r4 == 0) goto L_0x0428
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r4.checkText()
            goto L_0x042c
        L_0x0428:
            boolean r4 = r12.verified
            r1.drawVerified = r4
        L_0x042c:
            boolean r4 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r4 == 0) goto L_0x05a5
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x04a2
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0439
            goto L_0x04a2
        L_0x0439:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r12 = r4.id
            if (r12 < 0) goto L_0x0457
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x044c
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x044c
            goto L_0x0457
        L_0x044c:
            r1.drawNameGroup = r5
            r4 = 1099694080(0x418CLASSNAME, float:17.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x0461
        L_0x0457:
            r1.drawNameBroadcast = r5
            r4 = 1099169792(0x41840000, float:16.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
        L_0x0461:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0481
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x0476
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0478
        L_0x0476:
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0478:
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 + r12
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x0481:
            int r4 = r39.getMeasuredWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r4 = r4 - r12
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x0491
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0493
        L_0x0491:
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0493:
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 - r12
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x04a2:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r12 = r4.id
            if (r12 < 0) goto L_0x04c0
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x04b5
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x04b5
            goto L_0x04c0
        L_0x04b5:
            r1.drawNameGroup = r5
            r4 = 1096286208(0x41580000, float:13.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x04ca
        L_0x04c0:
            r1.drawNameBroadcast = r5
            r4 = 1095237632(0x41480000, float:12.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
        L_0x04ca:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x04ea
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x04df
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e1
        L_0x04df:
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e1:
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 + r12
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x04ea:
            int r4 = r39.getMeasuredWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r4 = r4 - r12
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x04fa
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04fc
        L_0x04fa:
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04fc:
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 - r12
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x050b:
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            if (r4 == 0) goto L_0x05a5
            boolean r12 = r4.scam
            if (r12 == 0) goto L_0x051b
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r4.checkText()
            goto L_0x051f
        L_0x051b:
            boolean r4 = r4.verified
            r1.drawVerified = r4
        L_0x051f:
            boolean r4 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r4 == 0) goto L_0x05a5
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x05a5
            r1.drawNameBot = r5
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x056d
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0534
            goto L_0x056d
        L_0x0534:
            r4 = 1099169792(0x41840000, float:16.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0554
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 + r12
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x0554:
            int r4 = r39.getMeasuredWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r4 = r4 - r12
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 - r12
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x056d:
            r4 = 1095237632(0x41480000, float:12.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x058d
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 + r12
            r1.nameLeft = r4
            goto L_0x05a5
        L_0x058d:
            int r4 = r39.getMeasuredWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r4 = r4 - r12
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            int r4 = r4 - r12
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.nameLeft = r4
        L_0x05a5:
            int r4 = r1.lastMessageDate
            if (r4 != 0) goto L_0x05b1
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 == 0) goto L_0x05b1
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            int r4 = r4.date
        L_0x05b1:
            boolean r12 = r1.isDialogCell
            if (r12 == 0) goto L_0x060c
            int r12 = r1.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            long r14 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r12.getDraft(r14)
            r1.draftMessage = r12
            if (r12 == 0) goto L_0x05e0
            java.lang.String r12 = r12.message
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x05d6
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r1.draftMessage
            int r12 = r12.reply_to_msg_id
            if (r12 == 0) goto L_0x05d4
            goto L_0x05d6
        L_0x05d4:
            r4 = 0
            goto L_0x0607
        L_0x05d6:
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r1.draftMessage
            int r12 = r12.date
            if (r4 <= r12) goto L_0x05e0
            int r4 = r1.unreadCount
            if (r4 != 0) goto L_0x05d4
        L_0x05e0:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x05fa
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r12 = r4.megagroup
            if (r12 != 0) goto L_0x05fa
            boolean r12 = r4.creator
            if (r12 != 0) goto L_0x05fa
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            if (r4 == 0) goto L_0x05d4
            boolean r4 = r4.post_messages
            if (r4 == 0) goto L_0x05d4
        L_0x05fa:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x060a
            boolean r12 = r4.left
            if (r12 != 0) goto L_0x05d4
            boolean r4 = r4.kicked
            if (r4 == 0) goto L_0x060a
            goto L_0x05d4
        L_0x0607:
            r1.draftMessage = r4
            goto L_0x060f
        L_0x060a:
            r4 = 0
            goto L_0x060f
        L_0x060c:
            r4 = 0
            r1.draftMessage = r4
        L_0x060f:
            if (r0 == 0) goto L_0x061d
            r1.lastPrintString = r0
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r8 = r8[r10]
        L_0x0619:
            r10 = 1
            r11 = 1
            goto L_0x0afd
        L_0x061d:
            r1.lastPrintString = r4
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06b6
            r0 = 2131624974(0x7f0e040e, float:1.8877143E38)
            java.lang.String r4 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x065c
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0656
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x063f
            goto L_0x0656
        L_0x063f:
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan
            java.lang.String r11 = "chats_draft"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.<init>(r11)
            int r11 = r0.length()
            r4.setSpan(r10, r6, r11, r13)
            goto L_0x06ad
        L_0x0656:
            r4 = r0
            r0 = r2
            r10 = 1
            r11 = 0
            goto L_0x0afd
        L_0x065c:
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            java.lang.String r4 = r4.message
            int r11 = r4.length()
            if (r11 <= r3) goto L_0x066a
            java.lang.String r4 = r4.substring(r6, r3)
        L_0x066a:
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r12 = 32
            r14 = 10
            java.lang.String r4 = r4.replace(r14, r12)
            r11[r6] = r4
            r11[r5] = r0
            java.lang.String r4 = java.lang.String.format(r10, r11)
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r4)
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x069b
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 != 0) goto L_0x069b
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan
            java.lang.String r11 = "chats_draft"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.<init>(r11)
            int r11 = r0.length()
            int r11 = r11 + r5
            r4.setSpan(r10, r6, r11, r13)
        L_0x069b:
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r10 = r10[r11]
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r10, r11, r6)
        L_0x06ad:
            r10 = 1
            r11 = 0
            r38 = r4
            r4 = r0
            r0 = r38
            goto L_0x0afd
        L_0x06b6:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x06cc
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r8 = r0[r4]
            r0 = 2131625424(0x7f0e05d0, float:1.8878056E38)
            java.lang.String r4 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
        L_0x06c9:
            r4 = 0
            goto L_0x0619
        L_0x06cc:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x0740
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x073e
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r8 = r4[r8]
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r4 == 0) goto L_0x06e8
            r0 = 2131625053(0x7f0e045d, float:1.8877303E38)
            java.lang.String r4 = "EncryptionProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x06c9
        L_0x06e8:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r4 == 0) goto L_0x0700
            r0 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r10)
            r4[r6] = r10
            java.lang.String r10 = "AwaitingEncryption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r0, r4)
            goto L_0x06c9
        L_0x0700:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r4 == 0) goto L_0x070e
            r0 = 2131625054(0x7f0e045e, float:1.8877305E38)
            java.lang.String r4 = "EncryptionRejected"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x06c9
        L_0x070e:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r4 == 0) goto L_0x073e
            int r0 = r0.admin_id
            int r4 = r1.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r0 != r4) goto L_0x0734
            r0 = 2131625042(0x7f0e0452, float:1.887728E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r10)
            r4[r6] = r10
            java.lang.String r10 = "EncryptedChatStartedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r0, r4)
            goto L_0x06c9
        L_0x0734:
            r0 = 2131625041(0x7f0e0451, float:1.8877279E38)
            java.lang.String r4 = "EncryptedChatStartedIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x06c9
        L_0x073e:
            r0 = r2
            goto L_0x06c9
        L_0x0740:
            boolean r0 = r0.isFromUser()
            if (r0 == 0) goto L_0x075d
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r4 = r4.from_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
            r4 = r0
            r0 = 0
            goto L_0x0774
        L_0x075d:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.to_id
            int r4 = r4.channel_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            r4 = 0
        L_0x0774:
            int r12 = r1.dialogsType
            r14 = 3
            if (r12 != r14) goto L_0x0790
            org.telegram.tgnet.TLRPC$User r12 = r1.user
            boolean r12 = org.telegram.messenger.UserObject.isUserSelf(r12)
            if (r12 == 0) goto L_0x0790
            r0 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            java.lang.String r4 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r4 = 0
            r9 = 0
            r10 = 0
        L_0x078d:
            r11 = 1
            goto L_0x0af5
        L_0x0790:
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x07a5
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 != 0) goto L_0x07a5
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x07a5
            java.lang.CharSequence r0 = r39.formatArchivedDialogNames()
            r4 = 0
        L_0x07a1:
            r10 = 1
            r11 = 0
            goto L_0x0af5
        L_0x07a5:
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r12.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r14 == 0) goto L_0x07d3
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x07c6
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r4 != 0) goto L_0x07c3
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x07c6
        L_0x07c3:
            r0 = r2
            r9 = 0
            goto L_0x07ca
        L_0x07c6:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x07ca:
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r8 = r4[r8]
        L_0x07d0:
            r4 = 0
            r10 = 1
            goto L_0x078d
        L_0x07d3:
            org.telegram.tgnet.TLRPC$Chat r14 = r1.chat
            if (r14 == 0) goto L_0x09f3
            int r14 = r14.id
            if (r14 <= 0) goto L_0x09f3
            if (r0 != 0) goto L_0x09f3
            boolean r12 = r12.isOutOwner()
            if (r12 == 0) goto L_0x07ee
            r0 = 2131625355(0x7f0e058b, float:1.8877916E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
        L_0x07ec:
            r4 = r0
            goto L_0x0831
        L_0x07ee:
            if (r4 == 0) goto L_0x0823
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0804
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x07f9
            goto L_0x0804
        L_0x07f9:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r4)
            java.lang.String r4 = "\n"
            java.lang.String r0 = r0.replace(r4, r2)
            goto L_0x07ec
        L_0x0804:
            boolean r0 = org.telegram.messenger.UserObject.isDeleted(r4)
            if (r0 == 0) goto L_0x0814
            r0 = 2131625420(0x7f0e05cc, float:1.8878047E38)
            java.lang.String r4 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x07ec
        L_0x0814:
            java.lang.String r0 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r4)
            java.lang.String r4 = "\n"
            java.lang.String r0 = r0.replace(r4, r2)
            goto L_0x07ec
        L_0x0823:
            if (r0 == 0) goto L_0x082e
            java.lang.String r0 = r0.title
            java.lang.String r4 = "\n"
            java.lang.String r0 = r0.replace(r4, r2)
            goto L_0x07ec
        L_0x082e:
            java.lang.String r0 = "DELETED"
            goto L_0x07ec
        L_0x0831:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r12 = r0.caption
            if (r12 == 0) goto L_0x089f
            java.lang.String r0 = r12.toString()
            int r11 = r0.length()
            if (r11 <= r3) goto L_0x0845
            java.lang.String r0 = r0.substring(r6, r3)
        L_0x0845:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVideo()
            if (r11 == 0) goto L_0x0851
            java.lang.String r11 = "📹 "
            goto L_0x0878
        L_0x0851:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVoice()
            if (r11 == 0) goto L_0x085d
            java.lang.String r11 = "🎤 "
            goto L_0x0878
        L_0x085d:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isMusic()
            if (r11 == 0) goto L_0x0869
            java.lang.String r11 = "🎧 "
            goto L_0x0878
        L_0x0869:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isPhoto()
            if (r11 == 0) goto L_0x0875
            java.lang.String r11 = "🖼 "
            goto L_0x0878
        L_0x0875:
            java.lang.String r11 = "📎 "
        L_0x0878:
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            r11 = 32
            r15 = 10
            java.lang.String r0 = r0.replace(r15, r11)
            r14.append(r0)
            java.lang.String r0 = r14.toString()
            r12[r6] = r0
            r12[r5] = r4
            java.lang.String r0 = java.lang.String.format(r10, r12)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09b4
        L_0x089f:
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            if (r12 == 0) goto L_0x0987
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0987
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r8 = r0[r8]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            boolean r14 = r12 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r14 == 0) goto L_0x08e4
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r12 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r12
            int r0 = android.os.Build.VERSION.SDK_INT
            r14 = 18
            if (r0 < r14) goto L_0x08d4
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_poll r12 = r12.poll
            java.lang.String r12 = r12.question
            r0[r6] = r12
            java.lang.String r12 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r12, r0)
            goto L_0x0950
        L_0x08d4:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_poll r12 = r12.poll
            java.lang.String r12 = r12.question
            r0[r6] = r12
            java.lang.String r12 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r12, r0)
            goto L_0x0950
        L_0x08e4:
            boolean r14 = r12 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r14 == 0) goto L_0x090e
            int r0 = android.os.Build.VERSION.SDK_INT
            r14 = 18
            if (r0 < r14) goto L_0x08fe
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r12 = r12.game
            java.lang.String r12 = r12.title
            r0[r6] = r12
            java.lang.String r12 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r12, r0)
            goto L_0x0950
        L_0x08fe:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r12 = r12.game
            java.lang.String r12 = r12.title
            r0[r6] = r12
            java.lang.String r12 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r12, r0)
            goto L_0x0950
        L_0x090e:
            int r12 = r0.type
            r14 = 14
            if (r12 != r14) goto L_0x094a
            int r12 = android.os.Build.VERSION.SDK_INT
            r14 = 18
            if (r12 < r14) goto L_0x0932
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r0 = r0.getMusicAuthor()
            r12[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r12[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r12)
            goto L_0x0950
        L_0x0932:
            java.lang.Object[] r12 = new java.lang.Object[r7]
            java.lang.String r0 = r0.getMusicAuthor()
            r12[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r12[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r12)
            goto L_0x0950
        L_0x094a:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0950:
            r12 = 32
            r14 = 10
            java.lang.String r0 = r0.replace(r14, r12)
            java.lang.Object[] r12 = new java.lang.Object[r7]
            r12[r6] = r0
            r12[r5] = r4
            java.lang.String r0 = java.lang.String.format(r10, r12)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0982 }
            java.lang.String r12 = "chats_attachMessage"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)     // Catch:{ Exception -> 0x0982 }
            r0.<init>(r12)     // Catch:{ Exception -> 0x0982 }
            if (r11 == 0) goto L_0x0979
            int r11 = r4.length()     // Catch:{ Exception -> 0x0982 }
            int r11 = r11 + r7
            goto L_0x097a
        L_0x0979:
            r11 = 0
        L_0x097a:
            int r12 = r10.length()     // Catch:{ Exception -> 0x0982 }
            r10.setSpan(r0, r11, r12, r13)     // Catch:{ Exception -> 0x0982 }
            goto L_0x09b5
        L_0x0982:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x09b5
        L_0x0987:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x09b0
            int r11 = r0.length()
            if (r11 <= r3) goto L_0x0999
            java.lang.String r0 = r0.substring(r6, r3)
        L_0x0999:
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r12 = 32
            r14 = 10
            java.lang.String r0 = r0.replace(r14, r12)
            r11[r6] = r0
            r11[r5] = r4
            java.lang.String r0 = java.lang.String.format(r10, r11)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09b4
        L_0x09b0:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r2)
        L_0x09b4:
            r10 = r0
        L_0x09b5:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x09bd
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x09c7
        L_0x09bd:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x09df
            int r0 = r10.length()
            if (r0 <= 0) goto L_0x09df
        L_0x09c7:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x09db }
            java.lang.String r11 = "chats_nameMessage"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)     // Catch:{ Exception -> 0x09db }
            r0.<init>(r11)     // Catch:{ Exception -> 0x09db }
            int r11 = r4.length()     // Catch:{ Exception -> 0x09db }
            int r11 = r11 + r5
            r10.setSpan(r0, r6, r11, r13)     // Catch:{ Exception -> 0x09db }
            goto L_0x09df
        L_0x09db:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x09df:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r0 = r0[r11]
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r10, r0, r11, r6)
            goto L_0x07a1
        L_0x09f3:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0a12
            org.telegram.tgnet.TLRPC$Photo r4 = r0.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x0a12
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a12
            r0 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r4 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x07d0
        L_0x0a12:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0a31
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0a31
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a31
            r0 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r4 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x07d0
        L_0x0a31:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r4 = r0.caption
            if (r4 == 0) goto L_0x0a7d
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0a41
            java.lang.String r0 = "📹 "
            goto L_0x0a68
        L_0x0a41:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0a4d
            java.lang.String r0 = "🎤 "
            goto L_0x0a68
        L_0x0a4d:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0a59
            java.lang.String r0 = "🎧 "
            goto L_0x0a68
        L_0x0a59:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0a65
            java.lang.String r0 = "🖼 "
            goto L_0x0a68
        L_0x0a65:
            java.lang.String r0 = "📎 "
        L_0x0a68:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            goto L_0x07d0
        L_0x0a7d:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r10 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r10 == 0) goto L_0x0a9e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "📊 "
            r0.append(r10)
            org.telegram.tgnet.TLRPC$TL_poll r4 = r4.poll
            java.lang.String r4 = r4.question
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            goto L_0x0adf
        L_0x0a9e:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0abf
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "🎮 "
            r0.append(r4)
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            goto L_0x0adf
        L_0x0abf:
            int r4 = r0.type
            r10 = 14
            if (r4 != r10) goto L_0x0add
            java.lang.Object[] r4 = new java.lang.Object[r7]
            java.lang.String r0 = r0.getMusicAuthor()
            r4[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0adf
        L_0x0add:
            java.lang.CharSequence r0 = r0.messageText
        L_0x0adf:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            if (r10 == 0) goto L_0x07d0
            boolean r4 = r4.isMediaEmpty()
            if (r4 != 0) goto L_0x07d0
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r8 = r4[r8]
            goto L_0x07d0
        L_0x0af5:
            int r12 = r1.currentDialogFolderId
            if (r12 == 0) goto L_0x0afd
            java.lang.CharSequence r4 = r39.formatArchivedDialogNames()
        L_0x0afd:
            org.telegram.tgnet.TLRPC$DraftMessage r12 = r1.draftMessage
            if (r12 == 0) goto L_0x0b09
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0b22
        L_0x0b09:
            int r12 = r1.lastMessageDate
            if (r12 == 0) goto L_0x0b13
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0b22
        L_0x0b13:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 == 0) goto L_0x0b21
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            int r12 = r12.date
            long r12 = (long) r12
            java.lang.String r12 = org.telegram.messenger.LocaleController.stringForMessageListDate(r12)
            goto L_0x0b22
        L_0x0b21:
            r12 = r2
        L_0x0b22:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 != 0) goto L_0x0b36
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawCount = r6
            r1.drawMention = r6
            r1.drawError = r6
            r13 = 0
            r14 = 0
            goto L_0x0CLASSNAME
        L_0x0b36:
            int r14 = r1.currentDialogFolderId
            if (r14 == 0) goto L_0x0b75
            int r13 = r1.unreadCount
            int r14 = r1.mentionCount
            int r15 = r13 + r14
            if (r15 <= 0) goto L_0x0b6e
            if (r13 <= r14) goto L_0x0b58
            r1.drawCount = r5
            r1.drawMention = r6
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15[r6] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r15)
            goto L_0x0b73
        L_0x0b58:
            r1.drawCount = r6
            r1.drawMention = r5
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r13 = r13 + r14
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r15[r6] = r13
            java.lang.String r13 = "%d"
            java.lang.String r13 = java.lang.String.format(r13, r15)
            r14 = r13
            r13 = 0
            goto L_0x0bba
        L_0x0b6e:
            r1.drawCount = r6
            r1.drawMention = r6
            r13 = 0
        L_0x0b73:
            r14 = 0
            goto L_0x0bba
        L_0x0b75:
            boolean r14 = r1.clearingDialog
            if (r14 == 0) goto L_0x0b7e
            r1.drawCount = r6
            r9 = 0
        L_0x0b7c:
            r13 = 0
            goto L_0x0bae
        L_0x0b7e:
            int r14 = r1.unreadCount
            if (r14 == 0) goto L_0x0ba3
            if (r14 != r5) goto L_0x0b90
            int r15 = r1.mentionCount
            if (r14 != r15) goto L_0x0b90
            if (r13 == 0) goto L_0x0b90
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            boolean r13 = r13.mentioned
            if (r13 != 0) goto L_0x0ba3
        L_0x0b90:
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            int r14 = r1.unreadCount
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r13[r6] = r14
            java.lang.String r14 = "%d"
            java.lang.String r13 = java.lang.String.format(r14, r13)
            goto L_0x0bae
        L_0x0ba3:
            boolean r13 = r1.markUnread
            if (r13 == 0) goto L_0x0bab
            r1.drawCount = r5
            r13 = r2
            goto L_0x0bae
        L_0x0bab:
            r1.drawCount = r6
            goto L_0x0b7c
        L_0x0bae:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0bb7
            r1.drawMention = r5
            java.lang.String r14 = "@"
            goto L_0x0bba
        L_0x0bb7:
            r1.drawMention = r6
            goto L_0x0b73
        L_0x0bba:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x0CLASSNAME
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x0CLASSNAME
            boolean r9 = r9.isSending()
            if (r9 == 0) goto L_0x0be1
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r5
            r1.drawError = r6
            goto L_0x0CLASSNAME
        L_0x0be1:
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.isSendError()
            if (r9 == 0) goto L_0x0bf6
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r5
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x0CLASSNAME
        L_0x0bf6:
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.isSent()
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.isUnread()
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r9 = r9.megagroup
            if (r9 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r9 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r9 = 1
        L_0x0CLASSNAME:
            r1.drawCheck1 = r9
            r1.drawCheck2 = r5
            r1.drawClock = r6
            r1.drawError = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r6
        L_0x0CLASSNAME:
            int r9 = r1.dialogsType
            if (r9 != 0) goto L_0x0CLASSNAME
            int r9 = r1.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            r24 = r8
            long r7 = r1.currentDialogId
            boolean r7 = r9.isProxyDialog(r7, r5)
            if (r7 == 0) goto L_0x0c4b
            r1.drawPinBackground = r5
            r7 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r8 = "UseProxySponsor"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            goto L_0x0c4c
        L_0x0CLASSNAME:
            r24 = r8
        L_0x0c4b:
            r7 = r12
        L_0x0c4c:
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x0CLASSNAME
            r8 = 2131624215(0x7f0e0117, float:1.8875603E38)
            java.lang.String r9 = "ArchivedChats"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
        L_0x0CLASSNAME:
            r25 = r11
            r38 = r24
            r24 = r0
            r0 = r8
            r8 = r14
            r14 = r38
            goto L_0x0cac
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x0c6b
            java.lang.String r8 = r8.title
            goto L_0x0c9c
        L_0x0c6b:
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            if (r8 == 0) goto L_0x0c9b
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x0CLASSNAME
            boolean r8 = r1.useMeForMyMessages
            if (r8 == 0) goto L_0x0CLASSNAME
            r8 = 2131625355(0x7f0e058b, float:1.8877916E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x0c9c
        L_0x0CLASSNAME:
            int r8 = r1.dialogsType
            r9 = 3
            if (r8 != r9) goto L_0x0c8a
            r1.drawPinBackground = r5
        L_0x0c8a:
            r8 = 2131626590(0x7f0e0a5e, float:1.888042E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x0c9c
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r8)
            goto L_0x0c9c
        L_0x0c9b:
            r8 = r2
        L_0x0c9c:
            int r9 = r8.length()
            if (r9 != 0) goto L_0x0CLASSNAME
            r8 = 2131625420(0x7f0e05cc, float:1.8878047E38)
            java.lang.String r9 = "HiddenName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x0CLASSNAME
        L_0x0cac:
            if (r10 == 0) goto L_0x0ced
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r9 = r9.measureText(r7)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            android.text.StaticLayout r10 = new android.text.StaticLayout
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r10
            r27 = r7
            r29 = r9
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.timeLayout = r10
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x0ce4
            int r7 = r39.getMeasuredWidth()
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 - r10
            int r7 = r7 - r9
            r1.timeLeft = r7
            goto L_0x0cf3
        L_0x0ce4:
            r10 = 1097859072(0x41700000, float:15.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.timeLeft = r7
            goto L_0x0cf3
        L_0x0ced:
            r7 = 0
            r1.timeLayout = r7
            r1.timeLeft = r6
            r9 = 0
        L_0x0cf3:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x0d07
            int r7 = r39.getMeasuredWidth()
            int r10 = r1.nameLeft
            int r7 = r7 - r10
            r10 = 1096810496(0x41600000, float:14.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 - r10
            int r7 = r7 - r9
            goto L_0x0d1b
        L_0x0d07:
            int r7 = r39.getMeasuredWidth()
            int r10 = r1.nameLeft
            int r7 = r7 - r10
            r10 = 1117388800(0x429a0000, float:77.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r7 = r7 - r10
            int r7 = r7 - r9
            int r10 = r1.nameLeft
            int r10 = r10 + r9
            r1.nameLeft = r10
        L_0x0d1b:
            boolean r10 = r1.drawNameLock
            if (r10 == 0) goto L_0x0d2e
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
        L_0x0d2b:
            int r10 = r10 + r11
            int r7 = r7 - r10
            goto L_0x0d61
        L_0x0d2e:
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0d3f
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x0d2b
        L_0x0d3f:
            boolean r10 = r1.drawNameBroadcast
            if (r10 == 0) goto L_0x0d50
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x0d2b
        L_0x0d50:
            boolean r10 = r1.drawNameBot
            if (r10 == 0) goto L_0x0d61
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            goto L_0x0d2b
        L_0x0d61:
            boolean r10 = r1.drawClock
            r11 = 1084227584(0x40a00000, float:5.0)
            if (r10 == 0) goto L_0x0d8f
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r12
            int r7 = r7 - r10
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x0d7e
            int r9 = r1.timeLeft
            int r9 = r9 - r10
            r1.checkDrawLeft = r9
            goto L_0x0e05
        L_0x0d7e:
            int r12 = r1.timeLeft
            int r12 = r12 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r12 + r9
            r1.checkDrawLeft = r12
            int r9 = r1.nameLeft
            int r9 = r9 + r10
            r1.nameLeft = r9
            goto L_0x0e05
        L_0x0d8f:
            boolean r10 = r1.drawCheck2
            if (r10 == 0) goto L_0x0e05
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r10 = r10.getIntrinsicWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r12
            int r7 = r7 - r10
            boolean r12 = r1.drawCheck1
            if (r12 == 0) goto L_0x0dec
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r12 = r12.getIntrinsicWidth()
            r20 = 1090519040(0x41000000, float:8.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r12 = r12 - r20
            int r7 = r7 - r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x0dc5
            int r9 = r1.timeLeft
            int r9 = r9 - r10
            r1.halfCheckDrawLeft = r9
            r10 = 1085276160(0x40b00000, float:5.5)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            r1.checkDrawLeft = r9
            goto L_0x0e05
        L_0x0dc5:
            int r12 = r1.timeLeft
            int r12 = r12 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r12 + r9
            r1.checkDrawLeft = r12
            r9 = 1085276160(0x40b00000, float:5.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r12 = r12 + r9
            r1.halfCheckDrawLeft = r12
            int r9 = r1.nameLeft
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r11 = r11.getIntrinsicWidth()
            int r10 = r10 + r11
            r11 = 1090519040(0x41000000, float:8.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            int r9 = r9 + r10
            r1.nameLeft = r9
            goto L_0x0e05
        L_0x0dec:
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x0df6
            int r9 = r1.timeLeft
            int r9 = r9 - r10
            r1.checkDrawLeft = r9
            goto L_0x0e05
        L_0x0df6:
            int r12 = r1.timeLeft
            int r12 = r12 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r12 + r9
            r1.checkDrawLeft = r12
            int r9 = r1.nameLeft
            int r9 = r9 + r10
            r1.nameLeft = r9
        L_0x0e05:
            boolean r9 = r1.dialogMuted
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r9 == 0) goto L_0x0e29
            boolean r9 = r1.drawVerified
            if (r9 != 0) goto L_0x0e29
            boolean r9 = r1.drawScam
            if (r9 != 0) goto L_0x0e29
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r10 = r10.getIntrinsicWidth()
            int r9 = r9 + r10
            int r7 = r7 - r9
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0e5c
            int r10 = r1.nameLeft
            int r10 = r10 + r9
            r1.nameLeft = r10
            goto L_0x0e5c
        L_0x0e29:
            boolean r9 = r1.drawVerified
            if (r9 == 0) goto L_0x0e43
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r10 = r10.getIntrinsicWidth()
            int r9 = r9 + r10
            int r7 = r7 - r9
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0e5c
            int r10 = r1.nameLeft
            int r10 = r10 + r9
            r1.nameLeft = r10
            goto L_0x0e5c
        L_0x0e43:
            boolean r9 = r1.drawScam
            if (r9 == 0) goto L_0x0e5c
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r10 = r10.getIntrinsicWidth()
            int r9 = r9 + r10
            int r7 = r7 - r9
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0e5c
            int r10 = r1.nameLeft
            int r10 = r10 + r9
            r1.nameLeft = r10
        L_0x0e5c:
            r35 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r12 = java.lang.Math.max(r9, r7)
            r7 = 32
            r9 = 10
            java.lang.String r0 = r0.replace(r9, r7)     // Catch:{ Exception -> 0x0e9b }
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0e9b }
            int r9 = r1.paintIndex     // Catch:{ Exception -> 0x0e9b }
            r7 = r7[r9]     // Catch:{ Exception -> 0x0e9b }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r35)     // Catch:{ Exception -> 0x0e9b }
            int r9 = r12 - r9
            float r9 = (float) r9     // Catch:{ Exception -> 0x0e9b }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0e9b }
            java.lang.CharSequence r27 = android.text.TextUtils.ellipsize(r0, r7, r9, r10)     // Catch:{ Exception -> 0x0e9b }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0e9b }
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0e9b }
            int r9 = r1.paintIndex     // Catch:{ Exception -> 0x0e9b }
            r28 = r7[r9]     // Catch:{ Exception -> 0x0e9b }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0e9b }
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r29 = r12
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x0e9b }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x0e9b }
            goto L_0x0e9f
        L_0x0e9b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0e9f:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0var_
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0ea9
            goto L_0x0var_
        L_0x0ea9:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.messageNameTop = r7
            r7 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.timeTop = r7
            r7 = 1109131264(0x421CLASSNAME, float:39.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.errorTop = r7
            r7 = 1109131264(0x421CLASSNAME, float:39.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.pinTop = r7
            r7 = 1109131264(0x421CLASSNAME, float:39.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.countTop = r7
            r7 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.checkDrawTop = r9
            int r7 = r39.getMeasuredWidth()
            r9 = 1119748096(0x42be0000, float:95.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r7 = r7 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0efd
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0var_
        L_0x0efd:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r39.getMeasuredWidth()
            r10 = 1115684864(0x42800000, float:64.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r10 = r1.avatarImage
            r11 = 1113063424(0x42580000, float:54.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r17 = 1113063424(0x42580000, float:54.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r10.setImageCoords(r9, r0, r11, r15)
            goto L_0x0f9d
        L_0x0var_:
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7 = 1107296256(0x42000000, float:32.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.messageNameTop = r7
            r7 = 1095761920(0x41500000, float:13.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.timeTop = r7
            r7 = 1110179840(0x422CLASSNAME, float:43.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.errorTop = r7
            r7 = 1110179840(0x422CLASSNAME, float:43.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.pinTop = r7
            r7 = 1110179840(0x422CLASSNAME, float:43.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.countTop = r7
            r7 = 1095761920(0x41500000, float:13.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.checkDrawTop = r7
            int r7 = r39.getMeasuredWidth()
            r9 = 1119485952(0x42ba0000, float:93.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r7 = r7 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0var_
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0f8c
        L_0x0var_:
            r9 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r39.getMeasuredWidth()
            r10 = 1115947008(0x42840000, float:66.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
        L_0x0f8c:
            org.telegram.messenger.ImageReceiver r10 = r1.avatarImage
            r11 = 1113587712(0x42600000, float:56.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.setImageCoords(r9, r0, r11, r15)
        L_0x0f9d:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x0fc2
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0fba
            int r0 = r39.getMeasuredWidth()
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r9 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.pinLeft = r0
            goto L_0x0fc2
        L_0x0fba:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x0fc2:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x0ff7
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r7 = r7 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x0fdf
            int r0 = r39.getMeasuredWidth()
            r8 = 1107820544(0x42080000, float:34.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            r1.errorLeft = r0
            goto L_0x0ff1
        L_0x0fdf:
            r8 = 1093664768(0x41300000, float:11.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.errorLeft = r8
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x0ff1:
            r36 = r12
            r37 = r14
            goto L_0x1112
        L_0x0ff7:
            if (r13 != 0) goto L_0x1021
            if (r8 == 0) goto L_0x0ffc
            goto L_0x1021
        L_0x0ffc:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x101c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 + r8
            int r7 = r7 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x101c
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x101c:
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x0ff1
        L_0x1021:
            if (r13 == 0) goto L_0x1080
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r35)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r13)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r0 = java.lang.Math.max(r0, r9)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.countWidth
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_CENTER
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r27 = r13
            r29 = r9
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 + r9
            int r7 = r7 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x106d
            int r0 = r39.getMeasuredWidth()
            int r9 = r1.countWidth
            int r0 = r0 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r9
            r1.countLeft = r0
            goto L_0x107d
        L_0x106d:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x107d:
            r1.drawCount = r5
            goto L_0x1082
        L_0x1080:
            r1.countWidth = r6
        L_0x1082:
            r0 = r7
            if (r8 == 0) goto L_0x110d
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x10bd
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r35)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r8)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r7 = java.lang.Math.max(r7, r9)
            r1.mentionWidth = r7
            android.text.StaticLayout r15 = new android.text.StaticLayout
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r10 = r1.mentionWidth
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_CENTER
            r13 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r22 = 0
            r7 = r15
            r36 = r12
            r12 = r13
            r13 = r17
            r37 = r14
            r14 = r22
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)
            r1.mentionLayout = r15
            goto L_0x10c7
        L_0x10bd:
            r36 = r12
            r37 = r14
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.mentionWidth = r7
        L_0x10c7:
            int r7 = r1.mentionWidth
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r8
            int r0 = r0 - r7
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x10ee
            int r7 = r39.getMeasuredWidth()
            int r8 = r1.mentionWidth
            int r7 = r7 - r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r7 = r7 - r8
            int r8 = r1.countWidth
            if (r8 == 0) goto L_0x10e9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r8 = r8 + r9
            goto L_0x10ea
        L_0x10e9:
            r8 = 0
        L_0x10ea:
            int r7 = r7 - r8
            r1.mentionLeft = r7
            goto L_0x110a
        L_0x10ee:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x10fc
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r9 = r9 + r10
            goto L_0x10fd
        L_0x10fc:
            r9 = 0
        L_0x10fd:
            int r8 = r8 + r9
            r1.mentionLeft = r8
            int r8 = r1.messageLeft
            int r8 = r8 + r7
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r7
            r1.messageNameLeft = r8
        L_0x110a:
            r1.drawMention = r5
            goto L_0x1111
        L_0x110d:
            r36 = r12
            r37 = r14
        L_0x1111:
            r7 = r0
        L_0x1112:
            if (r25 == 0) goto L_0x1156
            if (r24 != 0) goto L_0x1117
            goto L_0x1119
        L_0x1117:
            r2 = r24
        L_0x1119:
            java.lang.String r0 = r2.toString()
            int r2 = r0.length()
            if (r2 <= r3) goto L_0x1127
            java.lang.String r0 = r0.substring(r6, r3)
        L_0x1127:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x112f
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x1131
        L_0x112f:
            if (r4 == 0) goto L_0x113a
        L_0x1131:
            r2 = 32
            r3 = 10
            java.lang.String r0 = r0.replace(r3, r2)
            goto L_0x1142
        L_0x113a:
            java.lang.String r2 = "\n\n"
            java.lang.String r3 = "\n"
            java.lang.String r0 = r0.replace(r2, r3)
        L_0x1142:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r3 = r1.paintIndex
            r2 = r2[r3]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r3 = 1099431936(0x41880000, float:17.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r24 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r3, r6)
        L_0x1156:
            r2 = r24
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r3 = java.lang.Math.max(r0, r7)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1168
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x119b
        L_0x1168:
            if (r4 == 0) goto L_0x119b
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1172
            int r0 = r1.currentDialogFolderDialogsCount
            if (r0 != r5) goto L_0x119b
        L_0x1172:
            android.text.TextPaint r26 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x118d }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x118d }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            android.text.TextUtils$TruncateAt r32 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x118d }
            r34 = 1
            r25 = r4
            r27 = r3
            r33 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r25, r26, r27, r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x118d }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x118d }
            goto L_0x1191
        L_0x118d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1191:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            r7 = 0
            goto L_0x11b8
        L_0x119b:
            r7 = 0
            r1.messageNameLayout = r7
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x11b0
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x11a7
            goto L_0x11b0
        L_0x11a7:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x11b8
        L_0x11b0:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
        L_0x11b8:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x122d }
            if (r0 != 0) goto L_0x11c0
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x122d }
            if (r0 == 0) goto L_0x11d0
        L_0x11c0:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x122d }
            if (r0 == 0) goto L_0x11d0
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x122d }
            if (r0 <= r5) goto L_0x11d0
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x122d }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x122d }
            r14 = r0[r2]     // Catch:{ Exception -> 0x122d }
            r0 = r4
            goto L_0x11ef
        L_0x11d0:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x122d }
            if (r0 != 0) goto L_0x11d8
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x122d }
            if (r0 == 0) goto L_0x11da
        L_0x11d8:
            if (r4 == 0) goto L_0x11ea
        L_0x11da:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r35)     // Catch:{ Exception -> 0x122d }
            int r0 = r3 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x122d }
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x122d }
            r8 = r37
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r2, r8, r0, r7)     // Catch:{ Exception -> 0x122d }
            goto L_0x11ed
        L_0x11ea:
            r8 = r37
            r0 = r2
        L_0x11ed:
            r7 = r4
            r14 = r8
        L_0x11ef:
            boolean r2 = r1.useForceThreeLines     // Catch:{ Exception -> 0x122d }
            if (r2 != 0) goto L_0x120b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x122d }
            if (r2 == 0) goto L_0x11f8
            goto L_0x120b
        L_0x11f8:
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x122d }
            android.text.Layout$Alignment r12 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x122d }
            r13 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            r15 = 0
            r8 = r2
            r9 = r0
            r10 = r14
            r11 = r3
            r14 = r4
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x122d }
            r1.messageLayout = r2     // Catch:{ Exception -> 0x122d }
            goto L_0x1231
        L_0x120b:
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x122d }
            r12 = 1065353216(0x3var_, float:1.0)
            r2 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x122d }
            float r13 = (float) r2     // Catch:{ Exception -> 0x122d }
            r2 = 0
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x122d }
            if (r7 == 0) goto L_0x121e
            r17 = 1
            goto L_0x1220
        L_0x121e:
            r17 = 2
        L_0x1220:
            r8 = r0
            r9 = r14
            r10 = r3
            r14 = r2
            r16 = r3
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x122d }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x122d }
            goto L_0x1231
        L_0x122d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1231:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x135e
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x12eb
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12eb
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r6)
            android.text.StaticLayout r2 = r1.nameLayout
            float r2 = r2.getLineWidth(r6)
            double r4 = (double) r2
            double r4 = java.lang.Math.ceil(r4)
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x1282
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1282
            boolean r2 = r1.drawScam
            if (r2 != 0) goto L_0x1282
            int r2 = r1.nameLeft
            double r7 = (double) r2
            r2 = r36
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x12d3
        L_0x1282:
            r2 = r36
            boolean r7 = r1.drawVerified
            if (r7 == 0) goto L_0x12ac
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x12d3
        L_0x12ac:
            boolean r7 = r1.drawScam
            if (r7 == 0) goto L_0x12d3
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r20)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
        L_0x12d3:
            r7 = 0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x12eb
            double r7 = (double) r2
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x12eb
            int r0 = r1.nameLeft
            double r9 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r4
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r7
            int r0 = (int) r9
            r1.nameLeft = r0
        L_0x12eb:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1329
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1329
            r2 = 2147483647(0x7fffffff, float:NaN)
            r4 = 0
        L_0x12f9:
            if (r4 >= r0) goto L_0x131f
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r4)
            r7 = 0
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x131e
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineWidth(r4)
            double r7 = (double) r5
            double r7 = java.lang.Math.ceil(r7)
            double r9 = (double) r3
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r7
            int r5 = (int) r9
            int r2 = java.lang.Math.min(r2, r5)
            int r4 = r4 + 1
            goto L_0x12f9
        L_0x131e:
            r2 = 0
        L_0x131f:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r2 == r0) goto L_0x1329
            int r0 = r1.messageLeft
            int r0 = r0 + r2
            r1.messageLeft = r0
        L_0x1329:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x13e6
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x13e6
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineLeft(r6)
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x13e6
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r6)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r2 = (double) r3
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x13e6
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r2
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x13e6
        L_0x135e:
            r2 = r36
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x13ac
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x13ac
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r6)
            float r3 = (float) r2
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1393
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r6)
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            double r7 = (double) r2
            int r2 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x1393
            int r2 = r1.nameLeft
            double r9 = (double) r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r3
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r7
            int r2 = (int) r9
            r1.nameLeft = r2
        L_0x1393:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x139f
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x139f
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x13ac
        L_0x139f:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x13ac:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x13cf
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x13cf
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x13b9:
            if (r3 >= r0) goto L_0x13c8
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x13b9
        L_0x13c8:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x13cf:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x13e6
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x13e6
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            float r2 = r2.getLineLeft(r6)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x13e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
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
        ArrayList<TLRPC$Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, z);
        if (this.index < dialogsArray.size()) {
            TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(this.index);
            boolean z2 = true;
            TLRPC$Dialog tLRPC$Dialog2 = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
            TLRPC$DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            if (this.currentDialogFolderId != 0) {
                messageObject = findFolderTopMessage();
            } else {
                messageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(tLRPC$Dialog.id);
            }
            if (this.currentDialogId != tLRPC$Dialog.id || (((messageObject2 = this.message) != null && messageObject2.getId() != tLRPC$Dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tLRPC$Dialog.unread_count || this.mentionCount != tLRPC$Dialog.unread_mentions_count || this.markUnread != tLRPC$Dialog.unread_mark || (messageObject3 = this.message) != messageObject || ((messageObject3 == null && messageObject != null) || draft != this.draftMessage || this.drawPin != tLRPC$Dialog.pinned)))) {
                boolean z3 = this.currentDialogId != tLRPC$Dialog.id;
                this.currentDialogId = tLRPC$Dialog.id;
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
                update(0);
                if (z3) {
                    this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
                }
                checkOnline();
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
        ArrayList<TLRPC$Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject messageObject = null;
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

    /* JADX WARNING: Removed duplicated region for block: B:116:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x020d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            org.telegram.ui.Cells.DialogCell$CustomDialog r2 = r0.customDialog
            r3 = 0
            r4 = 1
            r5 = 0
            if (r2 == 0) goto L_0x003c
            int r1 = r2.date
            r0.lastMessageDate = r1
            int r1 = r2.unread_count
            if (r1 == 0) goto L_0x0014
            goto L_0x0015
        L_0x0014:
            r4 = 0
        L_0x0015:
            r0.lastUnreadState = r4
            org.telegram.ui.Cells.DialogCell$CustomDialog r1 = r0.customDialog
            int r2 = r1.unread_count
            r0.unreadCount = r2
            boolean r2 = r1.pinned
            r0.drawPin = r2
            boolean r2 = r1.muted
            r0.dialogMuted = r2
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            int r4 = r1.id
            java.lang.String r1 = r1.name
            r2.setInfo(r4, r1, r3)
            org.telegram.messenger.ImageReceiver r5 = r0.avatarImage
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 0
            r10 = 0
            java.lang.String r7 = "50_50"
            r5.setImage(r6, r7, r8, r9, r10)
            goto L_0x0361
        L_0x003c:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0100
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC$Dialog) r2
            if (r2 == 0) goto L_0x00f5
            if (r1 != 0) goto L_0x0102
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            long r7 = r2.id
            boolean r6 = r6.isClearingDialog(r7)
            r0.clearingDialog = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r6 = r6.dialogMessage
            long r7 = r2.id
            java.lang.Object r6 = r6.get(r7)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            r0.message = r6
            if (r6 == 0) goto L_0x007e
            boolean r6 = r6.isUnread()
            if (r6 == 0) goto L_0x007e
            r6 = 1
            goto L_0x007f
        L_0x007e:
            r6 = 0
        L_0x007f:
            r0.lastUnreadState = r6
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r6 == 0) goto L_0x0094
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r6 = org.telegram.messenger.MessagesStorage.getInstance(r6)
            int r6 = r6.getArchiveUnreadCount()
            r0.unreadCount = r6
            r0.mentionCount = r5
            goto L_0x009c
        L_0x0094:
            int r6 = r2.unread_count
            r0.unreadCount = r6
            int r6 = r2.unread_mentions_count
            r0.mentionCount = r6
        L_0x009c:
            boolean r6 = r2.unread_mark
            r0.markUnread = r6
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.edit_date
            goto L_0x00aa
        L_0x00a9:
            r6 = 0
        L_0x00aa:
            r0.currentEditDate = r6
            int r6 = r2.last_message_date
            r0.lastMessageDate = r6
            int r6 = r0.dialogsType
            r7 = 7
            r8 = 8
            if (r6 == r7) goto L_0x00c8
            if (r6 != r8) goto L_0x00ba
            goto L_0x00c8
        L_0x00ba:
            int r6 = r0.currentDialogFolderId
            if (r6 != 0) goto L_0x00c4
            boolean r2 = r2.pinned
            if (r2 == 0) goto L_0x00c4
            r2 = 1
            goto L_0x00c5
        L_0x00c4:
            r2 = 0
        L_0x00c5:
            r0.drawPin = r2
            goto L_0x00ea
        L_0x00c8:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.messenger.MessagesController$DialogFilter[] r6 = r6.selectedDialogFilter
            int r7 = r0.dialogsType
            if (r7 != r8) goto L_0x00d6
            r7 = 1
            goto L_0x00d7
        L_0x00d6:
            r7 = 0
        L_0x00d7:
            r6 = r6[r7]
            if (r6 == 0) goto L_0x00e7
            android.util.LongSparseArray<java.lang.Integer> r6 = r6.pinnedDialogs
            long r7 = r2.id
            int r2 = r6.indexOfKey(r7)
            if (r2 < 0) goto L_0x00e7
            r2 = 1
            goto L_0x00e8
        L_0x00e7:
            r2 = 0
        L_0x00e8:
            r0.drawPin = r2
        L_0x00ea:
            org.telegram.messenger.MessageObject r2 = r0.message
            if (r2 == 0) goto L_0x0102
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            int r2 = r2.send_state
            r0.lastSendState = r2
            goto L_0x0102
        L_0x00f5:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0102
        L_0x0100:
            r0.drawPin = r5
        L_0x0102:
            if (r1 == 0) goto L_0x0211
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x0123
            r2 = r1 & 4
            if (r2 == 0) goto L_0x0123
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            int r6 = r6.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            r19.invalidate()
        L_0x0123:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0155
            r2 = r1 & 64
            if (r2 == 0) goto L_0x0155
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<java.lang.CharSequence> r2 = r2.printingStrings
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0141
            if (r2 == 0) goto L_0x0153
        L_0x0141:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 != 0) goto L_0x0147
            if (r2 != 0) goto L_0x0153
        L_0x0147:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0155
            if (r2 == 0) goto L_0x0155
            boolean r2 = r6.equals(r2)
            if (r2 != 0) goto L_0x0155
        L_0x0153:
            r2 = 1
            goto L_0x0156
        L_0x0155:
            r2 = 0
        L_0x0156:
            if (r2 != 0) goto L_0x0169
            r6 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r1
            if (r6 == 0) goto L_0x0169
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0169
            java.lang.CharSequence r6 = r6.messageText
            java.lang.CharSequence r7 = r0.lastMessageString
            if (r6 == r7) goto L_0x0169
            r2 = 1
        L_0x0169:
            if (r2 != 0) goto L_0x0174
            r6 = r1 & 2
            if (r6 == 0) goto L_0x0174
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0174
            r2 = 1
        L_0x0174:
            if (r2 != 0) goto L_0x017f
            r6 = r1 & 1
            if (r6 == 0) goto L_0x017f
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x017f
            r2 = 1
        L_0x017f:
            if (r2 != 0) goto L_0x018a
            r6 = r1 & 8
            if (r6 == 0) goto L_0x018a
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x018a
            r2 = 1
        L_0x018a:
            if (r2 != 0) goto L_0x0195
            r6 = r1 & 16
            if (r6 == 0) goto L_0x0195
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x0195
            r2 = 1
        L_0x0195:
            if (r2 != 0) goto L_0x01f6
            r6 = r1 & 256(0x100, float:3.59E-43)
            if (r6 == 0) goto L_0x01f6
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x01b0
            boolean r7 = r0.lastUnreadState
            boolean r6 = r6.isUnread()
            if (r7 == r6) goto L_0x01b0
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isUnread()
            r0.lastUnreadState = r2
            r2 = 1
        L_0x01b0:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x01f6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r7 == 0) goto L_0x01d4
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)
            int r7 = r7.getArchiveUnreadCount()
        L_0x01d2:
            r8 = 0
            goto L_0x01dd
        L_0x01d4:
            if (r6 == 0) goto L_0x01db
            int r7 = r6.unread_count
            int r8 = r6.unread_mentions_count
            goto L_0x01dd
        L_0x01db:
            r7 = 0
            goto L_0x01d2
        L_0x01dd:
            if (r6 == 0) goto L_0x01f6
            int r9 = r0.unreadCount
            if (r9 != r7) goto L_0x01ed
            boolean r9 = r0.markUnread
            boolean r10 = r6.unread_mark
            if (r9 != r10) goto L_0x01ed
            int r9 = r0.mentionCount
            if (r9 == r8) goto L_0x01f6
        L_0x01ed:
            r0.unreadCount = r7
            r0.mentionCount = r8
            boolean r2 = r6.unread_mark
            r0.markUnread = r2
            r2 = 1
        L_0x01f6:
            if (r2 != 0) goto L_0x020b
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x020b
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x020b
            int r6 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r6 == r1) goto L_0x020b
            r0.lastSendState = r1
            r2 = 1
        L_0x020b:
            if (r2 != 0) goto L_0x0211
            r19.invalidate()
            return
        L_0x0211:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r2 = 0
            if (r1 == 0) goto L_0x022e
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r19.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x022c
            long r6 = r1.getDialogId()
            goto L_0x0247
        L_0x022c:
            r6 = r2
            goto L_0x0247
        L_0x022e:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x0242
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r6 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r6)
            if (r1 == 0) goto L_0x0242
            r1 = 1
            goto L_0x0243
        L_0x0242:
            r1 = 0
        L_0x0243:
            r0.dialogMuted = r1
            long r6 = r0.currentDialogId
        L_0x0247:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x02e8
            int r1 = (int) r6
            r2 = 32
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x029a
            if (r1 >= 0) goto L_0x0289
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            r0.chat = r1
            boolean r2 = r0.isDialogCell
            if (r2 != 0) goto L_0x02c0
            if (r1 == 0) goto L_0x02c0
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02c0
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x02c0
            r0.chat = r1
            goto L_0x02c0
        L_0x0289:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            r0.user = r1
            goto L_0x02c0
        L_0x029a:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02c0
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.encryptedChat
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02c0:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x02e8
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x02e8
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x02e8
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.clientUserId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02e8:
            int r1 = r0.currentDialogFolderId
            if (r1 == 0) goto L_0x0305
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r1.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 2
            r1.setAvatarType(r2)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r4 = 0
            r5 = 0
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            r9 = 0
            r3.setImage(r4, r5, r6, r7, r8, r9)
            goto L_0x0361
        L_0x0305:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x0345
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x032d
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x032d
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r5 = r0.avatarImage
            r6 = 0
            r7 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 0
            org.telegram.tgnet.TLRPC$User r10 = r0.user
            r11 = 0
            r5.setImage(r6, r7, r8, r9, r10, r11)
            goto L_0x0361
        L_0x032d:
            org.telegram.messenger.ImageReceiver r12 = r0.avatarImage
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForUser(r1, r5)
            org.telegram.ui.Components.AvatarDrawable r15 = r0.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            r18 = 0
            java.lang.String r14 = "50_50"
            r17 = r1
            r12.setImage(r13, r14, r15, r16, r17, r18)
            goto L_0x0361
        L_0x0345:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x0361
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            org.telegram.messenger.ImageReceiver r6 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForChat(r1, r5)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r10 = 0
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            r12 = 0
            java.lang.String r8 = "50_50"
            r6.setImage(r7, r8, r9, r10, r11, r12)
        L_0x0361:
            int r1 = r19.getMeasuredWidth()
            if (r1 != 0) goto L_0x0372
            int r1 = r19.getMeasuredHeight()
            if (r1 == 0) goto L_0x036e
            goto L_0x0372
        L_0x036e:
            r19.requestLayout()
            goto L_0x0375
        L_0x0372:
            r19.buildLayout()
        L_0x0375:
            r19.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int):void");
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
        if (this.translationX != 0.0f) {
            this.isSliding = true;
        }
        if (this.isSliding) {
            boolean z2 = this.drawRevealBackground;
            if (Math.abs(this.translationX) >= ((float) getMeasuredWidth()) * 0.3f) {
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
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x044e  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x04de  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x058c  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05de  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x06ea  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x083b  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x086e  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0889  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x0972  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x098a  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a42  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0a6c  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0ac4  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0ad8  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0afe  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:459:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r25) {
        /*
            r24 = this;
            r1 = r24
            r8 = r25
            long r2 = r1.currentDialogId
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x0011
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            int r0 = r1.currentDialogFolderId
            r9 = 0
            if (r0 == 0) goto L_0x002a
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x002a
            float r2 = r0.outProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x002a
            float r2 = r1.translationX
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x002a
            r0.draw(r8)
            return
        L_0x002a:
            long r2 = android.os.SystemClock.elapsedRealtime()
            long r4 = r1.lastUpdateTime
            long r4 = r2 - r4
            r6 = 17
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x003a
            r10 = r6
            goto L_0x003b
        L_0x003a:
            r10 = r4
        L_0x003b:
            r1.lastUpdateTime = r2
            float r0 = r1.clipProgress
            r12 = 24
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0069
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 == r12) goto L_0x0069
            r25.save()
            int r0 = r1.topClip
            float r0 = (float) r0
            float r2 = r1.clipProgress
            float r0 = r0 * r2
            int r2 = r24.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r24.getMeasuredHeight()
            int r4 = r1.bottomClip
            float r4 = (float) r4
            float r5 = r1.clipProgress
            float r4 = r4 * r5
            int r4 = (int) r4
            int r3 = r3 - r4
            float r3 = (float) r3
            r8.clipRect(r9, r0, r2, r3)
        L_0x0069:
            float r0 = r1.translationX
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 2
            r15 = 0
            r7 = 1
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0099
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x007d
            goto L_0x0099
        L_0x007d:
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            if (r0 == 0) goto L_0x0093
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            r0.setProgress(r9)
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            r2 = 0
            r0.setCallback(r2)
            r1.translationDrawable = r2
            r1.translationAnimationStarted = r15
        L_0x0093:
            r13 = 1065353216(0x3var_, float:1.0)
            r19 = 1
            goto L_0x0293
        L_0x0099:
            r25.save()
            int r0 = r1.currentDialogFolderId
            java.lang.String r16 = "chats_archiveBackground"
            java.lang.String r17 = "chats_archivePinBackground"
            if (r0 == 0) goto L_0x00d4
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x00be
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131627034(0x7f0e0c1a, float:1.8881321E38)
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x0103
        L_0x00be:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131625422(0x7f0e05ce, float:1.8878052E38)
            java.lang.String r4 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x0103
        L_0x00d4:
            int r0 = r1.folderId
            if (r0 != 0) goto L_0x00ee
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131624202(0x7f0e010a, float:1.8875577E38)
            java.lang.String r4 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r1.translationDrawable = r4
            goto L_0x0103
        L_0x00ee:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131627025(0x7f0e0CLASSNAME, float:1.8881303E38)
            java.lang.String r4 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r1.translationDrawable = r4
        L_0x0103:
            r5 = r2
            r4 = r3
            boolean r2 = r1.translationAnimationStarted
            r18 = 1110179840(0x422CLASSNAME, float:43.0)
            if (r2 != 0) goto L_0x012b
            float r2 = r1.translationX
            float r2 = java.lang.Math.abs(r2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x012b
            r1.translationAnimationStarted = r7
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setProgress(r9)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setCallback(r1)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.start()
        L_0x012b:
            int r2 = r24.getMeasuredWidth()
            float r2 = (float) r2
            float r3 = r1.translationX
            float r3 = r3 + r2
            float r2 = r1.currentRevealProgress
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x017f
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r0 = (float) r0
            float r0 = r3 - r0
            r19 = 0
            int r2 = r24.getMeasuredWidth()
            float r2 = (float) r2
            int r6 = r24.getMeasuredHeight()
            float r6 = (float) r6
            android.graphics.Paint r20 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r21 = r2
            r2 = r25
            r12 = r3
            r3 = r0
            r0 = r4
            r4 = r19
            r22 = r5
            r5 = r21
            r19 = 1
            r7 = r20
            r2.drawRect(r3, r4, r5, r6, r7)
            float r2 = r1.currentRevealProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x0185
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r2 == 0) goto L_0x0185
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r4 = "Arrow.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
            goto L_0x0185
        L_0x017f:
            r12 = r3
            r0 = r4
            r22 = r5
            r19 = 1
        L_0x0185:
            int r2 = r24.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 - r3
            org.telegram.ui.Components.RLottieDrawable r3 = r1.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r14
            int r2 = r2 - r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x01a2
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x019f
            goto L_0x01a2
        L_0x019f:
            r3 = 1091567616(0x41100000, float:9.0)
            goto L_0x01a4
        L_0x01a2:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x01a4:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Components.RLottieDrawable r4 = r1.translationDrawable
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r14
            int r4 = r4 + r2
            org.telegram.ui.Components.RLottieDrawable r5 = r1.translationDrawable
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r14
            int r5 = r5 + r3
            float r6 = r1.currentRevealProgress
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0218
            r25.save()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r6 = (float) r6
            float r6 = r12 - r6
            int r7 = r24.getMeasuredWidth()
            float r7 = (float) r7
            int r13 = r24.getMeasuredHeight()
            float r13 = (float) r13
            r8.clipRect(r6, r9, r7, r13)
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7 = r22
            r6.setColor(r7)
            int r6 = r4 * r4
            int r7 = r24.getMeasuredHeight()
            int r7 = r5 - r7
            int r13 = r24.getMeasuredHeight()
            int r13 = r5 - r13
            int r7 = r7 * r13
            int r6 = r6 + r7
            double r6 = (double) r6
            double r6 = java.lang.Math.sqrt(r6)
            float r6 = (float) r6
            float r4 = (float) r4
            float r5 = (float) r5
            android.view.animation.AccelerateInterpolator r7 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r13 = r1.currentRevealProgress
            float r7 = r7.getInterpolation(r13)
            float r6 = r6 * r7
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r8.drawCircle(r4, r5, r6, r7)
            r25.restore()
            boolean r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r4 != 0) goto L_0x0218
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r6 = "Arrow.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r19
        L_0x0218:
            r25.save()
            float r2 = (float) r2
            float r3 = (float) r3
            r8.translate(r2, r3)
            float r2 = r1.currentRevealBounceProgress
            int r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            r13 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x0246
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0246
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r3 = r1.interpolator
            float r2 = r3.getInterpolation(r2)
            float r2 = r2 + r13
            org.telegram.ui.Components.RLottieDrawable r3 = r1.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r14
            float r3 = (float) r3
            org.telegram.ui.Components.RLottieDrawable r4 = r1.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r14
            float r4 = (float) r4
            r8.scale(r2, r2, r3, r4)
        L_0x0246:
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r15, (int) r15)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.draw(r8)
            r25.restore()
            int r2 = r24.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r24.getMeasuredHeight()
            float r3 = (float) r3
            r8.clipRect(r12, r9, r2, r3)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r2.measureText(r0)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r3 = r24.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r3 - r4
            int r2 = r2 / r14
            int r3 = r3 - r2
            float r2 = (float) r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0284
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0281
            goto L_0x0284
        L_0x0281:
            r3 = 1114374144(0x426CLASSNAME, float:59.0)
            goto L_0x0286
        L_0x0284:
            r3 = 1115160576(0x42780000, float:62.0)
        L_0x0286:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r8.drawText(r0, r2, r3, r4)
            r25.restore()
        L_0x0293:
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x02a1
            r25.save()
            float r0 = r1.translationX
            r8.translate(r0, r9)
        L_0x02a1:
            boolean r0 = r1.isSelected
            if (r0 == 0) goto L_0x02b8
            r3 = 0
            r4 = 0
            int r0 = r24.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r24.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x02b8:
            int r0 = r1.currentDialogFolderId
            java.lang.String r12 = "chats_pinnedOverlay"
            if (r0 == 0) goto L_0x02eb
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x02c8
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x02eb
        L_0x02c8:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            float r3 = r1.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r2, r3, r13)
            r0.setColor(r2)
            r3 = 0
            r4 = 0
            int r0 = r24.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r24.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
            goto L_0x030f
        L_0x02eb:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x02f3
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x030f
        L_0x02f3:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r0.setColor(r2)
            r3 = 0
            r4 = 0
            int r0 = r24.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r24.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x030f:
            float r0 = r1.translationX
            java.lang.String r17 = "windowBackgroundWhite"
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0323
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x031f
            goto L_0x0323
        L_0x031f:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x03d1
        L_0x0323:
            r25.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r0.setColor(r2)
            android.graphics.RectF r0 = r1.rect
            int r2 = r24.getMeasuredWidth()
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r24.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r24.getMeasuredHeight()
            float r4 = (float) r4
            r0.set(r2, r9, r3, r4)
            android.graphics.RectF r0 = r1.rect
            r2 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r1.cornerProgress
            float r3 = r3 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r4
            float r4 = r1.cornerProgress
            float r2 = r2 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r8.drawRoundRect(r0, r3, r2, r4)
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x039e
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0373
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x039e
        L_0x0373:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            float r3 = r1.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r15, r2, r3, r13)
            r0.setColor(r2)
            android.graphics.RectF r0 = r1.rect
            r2 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r1.cornerProgress
            float r3 = r3 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r4
            float r4 = r1.cornerProgress
            float r2 = r2 * r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r8.drawRoundRect(r0, r3, r2, r4)
            goto L_0x03a7
        L_0x039e:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x03aa
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x03a7
            goto L_0x03aa
        L_0x03a7:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x03ce
        L_0x03aa:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r0.setColor(r2)
            android.graphics.RectF r0 = r1.rect
            r2 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r1.cornerProgress
            float r3 = r3 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            float r5 = r1.cornerProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r8.drawRoundRect(r0, r3, r4, r5)
        L_0x03ce:
            r25.restore()
        L_0x03d1:
            float r0 = r1.translationX
            r3 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x03eb
            float r0 = r1.cornerProgress
            int r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x03fe
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 + r4
            r1.cornerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x03fc
            r1.cornerProgress = r13
            goto L_0x03fc
        L_0x03eb:
            float r0 = r1.cornerProgress
            int r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x03fe
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 - r4
            r1.cornerProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x03fc
            r1.cornerProgress = r9
        L_0x03fc:
            r7 = 1
            goto L_0x03ff
        L_0x03fe:
            r7 = 0
        L_0x03ff:
            boolean r0 = r1.drawNameLock
            if (r0 == 0) goto L_0x0412
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r8)
            goto L_0x044a
        L_0x0412:
            boolean r0 = r1.drawNameGroup
            if (r0 == 0) goto L_0x0425
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r8)
            goto L_0x044a
        L_0x0425:
            boolean r0 = r1.drawNameBroadcast
            if (r0 == 0) goto L_0x0438
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r8)
            goto L_0x044a
        L_0x0438:
            boolean r0 = r1.drawNameBot
            if (r0 == 0) goto L_0x044a
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r8)
        L_0x044a:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x04be
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0466
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x049a
        L_0x0466:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 != 0) goto L_0x0487
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 == 0) goto L_0x0473
            int r0 = r0.type
            if (r0 != r14) goto L_0x0473
            goto L_0x0487
        L_0x0473:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_name"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x049a
        L_0x0487:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_secretName"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x049a:
            r25.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x04ac
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x04a9
            goto L_0x04ac
        L_0x04a9:
            r5 = 1095761920(0x41500000, float:13.0)
            goto L_0x04ae
        L_0x04ac:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x04ae:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r8)
            r25.restore()
        L_0x04be:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x04da
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x04da
            r25.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r5 = r1.timeTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r8)
            r25.restore()
        L_0x04da:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x0528
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x04f0
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessageArchived_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x050f
        L_0x04f0:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0502
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_draft"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x050f
        L_0x0502:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessage_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
        L_0x050f:
            r25.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r5 = r1.messageNameTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x0521 }
            r0.draw(r8)     // Catch:{ Exception -> 0x0521 }
            goto L_0x0525
        L_0x0521:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0525:
            r25.restore()
        L_0x0528:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0588
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x055c
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0548
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameMessageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x056f
        L_0x0548:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_messageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x056f
        L_0x055c:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_message"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x056f:
            r25.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r5 = r1.messageTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x0581 }
            r0.draw(r8)     // Catch:{ Exception -> 0x0581 }
            goto L_0x0585
        L_0x0581:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0585:
            r25.restore()
        L_0x0588:
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x05d2
            boolean r0 = r1.drawClock
            if (r0 == 0) goto L_0x059f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            r0.draw(r8)
            goto L_0x05d2
        L_0x059f:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x05d2
            boolean r0 = r1.drawCheck1
            if (r0 == 0) goto L_0x05c4
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r5 = r1.halfCheckDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkReadDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkReadDrawable
            r0.draw(r8)
            goto L_0x05d2
        L_0x05c4:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            r0.draw(r8)
        L_0x05d2:
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x060a
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x060a
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x060a
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x05ee
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x05eb
            goto L_0x05ee
        L_0x05eb:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x05ef
        L_0x05ee:
            r6 = 0
        L_0x05ef:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x05fb
            r6 = 1096286208(0x41580000, float:13.5)
            goto L_0x05fd
        L_0x05fb:
            r6 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x05fd:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r8)
            goto L_0x066d
        L_0x060a:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x064b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x061e
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x061b
            goto L_0x061e
        L_0x061b:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x0620
        L_0x061e:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x0620:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x0637
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0634
            goto L_0x0637
        L_0x0634:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x0639
        L_0x0637:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x0639:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r8)
            goto L_0x066d
        L_0x064b:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x066d
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x065f
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x065c
            goto L_0x065f
        L_0x065c:
            r6 = 1097859072(0x41700000, float:15.0)
            goto L_0x0661
        L_0x065f:
            r6 = 1094713344(0x41400000, float:12.0)
        L_0x0661:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r8)
        L_0x066d:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x0679
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0691
        L_0x0679:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            float r6 = r1.reorderIconProgress
            float r6 = r6 * r5
            int r6 = (int) r6
            r0.setAlpha(r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            int r6 = r1.pinLeft
            int r12 = r1.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r6, (int) r12)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            r0.draw(r8)
        L_0x0691:
            boolean r0 = r1.drawError
            r6 = 1085276160(0x40b00000, float:5.5)
            r12 = 1102577664(0x41b80000, float:23.0)
            r16 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x06ea
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r2 = r1.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r5
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.RectF r0 = r1.rect
            int r2 = r1.errorLeft
            float r5 = (float) r2
            int r4 = r1.errorTop
            float r4 = (float) r4
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 + r20
            float r2 = (float) r2
            int r15 = r1.errorTop
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r12
            float r12 = (float) r15
            r0.set(r5, r4, r2, r12)
            android.graphics.RectF r0 = r1.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r2 * r16
            float r2 = r2 * r16
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r8.drawRoundRect(r0, r4, r2, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r2 = r1.errorLeft
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r4
            int r4 = r1.errorTop
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r8)
            goto L_0x0835
        L_0x06ea:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x0713
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x06f3
            goto L_0x0713
        L_0x06f3:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x0835
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r1.reorderIconProgress
            float r6 = r13 - r2
            float r6 = r6 * r5
            int r2 = (int) r6
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r2 = r1.pinLeft
            int r4 = r1.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r8)
            goto L_0x0835
        L_0x0713:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x0789
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0723
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0720
            goto L_0x0723
        L_0x0720:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0725
        L_0x0723:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0725:
            float r2 = r1.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r5
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r1.reorderIconProgress
            float r4 = r13 - r4
            float r4 = r4 * r5
            int r4 = (int) r4
            r2.setAlpha(r4)
            int r2 = r1.countLeft
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 - r4
            android.graphics.RectF r4 = r1.rect
            float r15 = (float) r2
            int r14 = r1.countTop
            float r14 = (float) r14
            int r3 = r1.countWidth
            int r2 = r2 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            float r2 = (float) r2
            int r3 = r1.countTop
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r3 = r3 + r23
            float r3 = (float) r3
            r4.set(r15, r14, r2, r3)
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r16
            float r3 = r3 * r16
            r8.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r1.countLayout
            if (r0 == 0) goto L_0x0789
            r25.save()
            int r0 = r1.countLeft
            float r0 = (float) r0
            int r2 = r1.countTop
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            float r2 = (float) r2
            r8.translate(r0, r2)
            android.text.StaticLayout r0 = r1.countLayout
            r0.draw(r8)
            r25.restore()
        L_0x0789:
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x0835
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r2 = r1.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r5
            int r2 = (int) r2
            r0.setAlpha(r2)
            int r0 = r1.mentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r2
            android.graphics.RectF r2 = r1.rect
            float r3 = (float) r0
            int r4 = r1.countTop
            float r4 = (float) r4
            int r6 = r1.mentionWidth
            int r0 = r0 + r6
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            float r0 = (float) r0
            int r6 = r1.countTop
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r6 = r6 + r12
            float r6 = (float) r6
            r2.set(r3, r4, r0, r6)
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x07c7
            int r0 = r1.folderId
            if (r0 == 0) goto L_0x07c7
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x07c9
        L_0x07c7:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x07c9:
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r16
            float r3 = r3 * r16
            r8.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r1.mentionLayout
            if (r0 == 0) goto L_0x0800
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r1.reorderIconProgress
            float r6 = r13 - r2
            float r6 = r6 * r5
            int r2 = (int) r6
            r0.setAlpha(r2)
            r25.save()
            int r0 = r1.mentionLeft
            float r0 = (float) r0
            int r2 = r1.countTop
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            float r2 = (float) r2
            r8.translate(r0, r2)
            android.text.StaticLayout r0 = r1.mentionLayout
            r0.draw(r8)
            r25.restore()
            goto L_0x0835
        L_0x0800:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r2 = r1.reorderIconProgress
            float r6 = r13 - r2
            float r6 = r6 * r5
            int r2 = (int) r6
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r2 = r1.mentionLeft
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r3 = r1.countTop
            r4 = 1078774989(0x404ccccd, float:3.2)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r3, r4, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r8)
        L_0x0835:
            boolean r0 = r1.animatingArchiveAvatar
            r12 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0857
            r25.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r1.interpolator
            float r2 = r1.animatingArchiveAvatarProgress
            float r2 = r2 / r12
            float r0 = r0.getInterpolation(r2)
            float r0 = r0 + r13
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getCenterX()
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getCenterY()
            r8.scale(r0, r0, r2, r3)
        L_0x0857:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0865
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x0865
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x086a
        L_0x0865:
            org.telegram.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r8)
        L_0x086a:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0871
            r25.restore()
        L_0x0871:
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            if (r0 == 0) goto L_0x096b
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x096b
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x096b
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x096b
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x096b
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x08b7
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x089f
            int r0 = r0.expires
            int r2 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x08b5
        L_0x089f:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x08b7
        L_0x08b5:
            r0 = 1
            goto L_0x08b8
        L_0x08b7:
            r0 = 0
        L_0x08b8:
            if (r0 != 0) goto L_0x08c0
            float r2 = r1.onlineProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x096b
        L_0x08c0:
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            int r2 = r2.getImageY2()
            boolean r3 = r1.useForceThreeLines
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r3 != 0) goto L_0x08d4
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x08d1
            goto L_0x08d4
        L_0x08d1:
            r18 = 1090519040(0x41000000, float:8.0)
            goto L_0x08d6
        L_0x08d4:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x08d6:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x08f5
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            int r3 = r3.getImageX()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x08ed
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x08ef
        L_0x08ed:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x08ef:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r4
            goto L_0x090a
        L_0x08f5:
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            int r3 = r3.getImageX2()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0903
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0905
        L_0x0903:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x0905:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
        L_0x090a:
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r4.setColor(r5)
            float r3 = (float) r3
            float r2 = (float) r2
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r1.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r3, r2, r4, r5)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "chats_onlineCircle"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r1.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r3, r2, r4, r5)
            if (r0 == 0) goto L_0x0956
            float r0 = r1.onlineProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x096b
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0969
            r1.onlineProgress = r13
            goto L_0x0969
        L_0x0956:
            float r0 = r1.onlineProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x096b
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.onlineProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0969
            r1.onlineProgress = r9
        L_0x0969:
            r0 = 1
            goto L_0x096c
        L_0x096b:
            r0 = r7
        L_0x096c:
            float r2 = r1.translationX
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0975
            r25.restore()
        L_0x0975:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0986
            float r2 = r1.translationX
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x0986
            org.telegram.ui.Components.PullForegroundDrawable r2 = r1.archivedChatsDrawable
            if (r2 == 0) goto L_0x0986
            r2.draw(r8)
        L_0x0986:
            boolean r2 = r1.useSeparator
            if (r2 == 0) goto L_0x09e7
            boolean r2 = r1.fullSeparator
            if (r2 != 0) goto L_0x09aa
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x099a
            boolean r2 = r1.archiveHidden
            if (r2 == 0) goto L_0x099a
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x09aa
        L_0x099a:
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x09a3
            boolean r2 = r1.archiveHidden
            if (r2 != 0) goto L_0x09a3
            goto L_0x09aa
        L_0x09a3:
            r2 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x09ab
        L_0x09aa:
            r2 = 0
        L_0x09ab:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x09cc
            r3 = 0
            int r4 = r24.getMeasuredHeight()
            int r4 = r4 + -1
            float r4 = (float) r4
            int r5 = r24.getMeasuredWidth()
            int r5 = r5 - r2
            float r5 = (float) r5
            int r2 = r24.getMeasuredHeight()
            int r2 = r2 + -1
            float r6 = (float) r2
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r2 = r25
            r2.drawLine(r3, r4, r5, r6, r7)
            goto L_0x09e7
        L_0x09cc:
            float r3 = (float) r2
            int r2 = r24.getMeasuredHeight()
            int r2 = r2 + -1
            float r4 = (float) r2
            int r2 = r24.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r24.getMeasuredHeight()
            int r2 = r2 + -1
            float r6 = (float) r2
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r2 = r25
            r2.drawLine(r3, r4, r5, r6, r7)
        L_0x09e7:
            float r2 = r1.clipProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0a34
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 == r3) goto L_0x09f7
            r25.restore()
            goto L_0x0a34
        L_0x09f7:
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2.setColor(r3)
            r3 = 0
            r4 = 0
            int r2 = r24.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r1.topClip
            float r2 = (float) r2
            float r6 = r1.clipProgress
            float r6 = r6 * r2
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
            int r2 = r24.getMeasuredHeight()
            int r4 = r1.bottomClip
            float r4 = (float) r4
            float r5 = r1.clipProgress
            float r4 = r4 * r5
            int r4 = (int) r4
            int r2 = r2 - r4
            float r4 = (float) r2
            int r2 = r24.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r24.getMeasuredHeight()
            float r6 = (float) r2
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0a34:
            boolean r2 = r1.drawReorder
            if (r2 != 0) goto L_0x0a3e
            float r2 = r1.reorderIconProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0a67
        L_0x0a3e:
            boolean r2 = r1.drawReorder
            if (r2 == 0) goto L_0x0a54
            float r2 = r1.reorderIconProgress
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 >= 0) goto L_0x0a67
            float r0 = (float) r10
            float r0 = r0 / r12
            float r2 = r2 + r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a65
            r1.reorderIconProgress = r13
            goto L_0x0a65
        L_0x0a54:
            float r2 = r1.reorderIconProgress
            int r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r3 <= 0) goto L_0x0a67
            float r0 = (float) r10
            float r0 = r0 / r12
            float r2 = r2 - r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a65
            r1.reorderIconProgress = r9
        L_0x0a65:
            r7 = 1
            goto L_0x0a68
        L_0x0a67:
            r7 = r0
        L_0x0a68:
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x0a96
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ac0
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a7f
            r1.archiveBackgroundProgress = r9
        L_0x0a7f:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            r2 = 2
            if (r0 != r2) goto L_0x0abf
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
            goto L_0x0abf
        L_0x0a96:
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0ac0
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0aa9
            r1.archiveBackgroundProgress = r13
        L_0x0aa9:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            r2 = 2
            if (r0 != r2) goto L_0x0abf
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
        L_0x0abf:
            r7 = 1
        L_0x0ac0:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0ad4
            float r0 = r1.animatingArchiveAvatarProgress
            float r2 = (float) r10
            float r0 = r0 + r2
            r1.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 < 0) goto L_0x0ad3
            r1.animatingArchiveAvatarProgress = r12
            r2 = 0
            r1.animatingArchiveAvatar = r2
        L_0x0ad3:
            r7 = 1
        L_0x0ad4:
            boolean r0 = r1.drawRevealBackground
            if (r0 == 0) goto L_0x0afe
            float r0 = r1.currentRevealBounceProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0aea
            float r2 = (float) r10
            float r2 = r2 / r12
            float r0 = r0 + r2
            r1.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0aea
            r1.currentRevealBounceProgress = r13
            r7 = 1
        L_0x0aea:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b1b
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b1a
            r1.currentRevealProgress = r13
            goto L_0x0b1a
        L_0x0afe:
            float r0 = r1.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0b07
            r1.currentRevealBounceProgress = r9
            r7 = 1
        L_0x0b07:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b1b
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.currentRevealProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b1a
            r1.currentRevealProgress = r9
        L_0x0b1a:
            r7 = 1
        L_0x0b1b:
            if (r7 == 0) goto L_0x0b20
            r24.invalidate()
        L_0x0b20:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    public void startOutAnimation() {
        PullForegroundDrawable pullForegroundDrawable = this.archivedChatsDrawable;
        if (pullForegroundDrawable != null) {
            pullForegroundDrawable.outCy = this.avatarImage.getCenterY();
            this.archivedChatsDrawable.outCx = this.avatarImage.getCenterX();
            this.archivedChatsDrawable.outRadius = ((float) this.avatarImage.getImageWidth()) / 2.0f;
            this.archivedChatsDrawable.outImageSize = (float) this.avatarImage.getBitmapWidth();
            this.archivedChatsDrawable.startOutAnimation();
        }
    }

    public void onReorderStateChanged(boolean z, boolean z2) {
        if ((this.drawPin || !z) && this.drawReorder != z) {
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
        } else if (!this.drawPin) {
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
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(16);
        accessibilityNodeInfo.addAction(32);
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
                if (tLRPC$User.bot) {
                    sb.append(LocaleController.getString("Bot", NUM));
                    sb.append(". ");
                }
                TLRPC$User tLRPC$User2 = this.user;
                if (tLRPC$User2.self) {
                    sb.append(LocaleController.getString("SavedMessages", NUM));
                } else {
                    sb.append(ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name));
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
        String formatDateAudio = LocaleController.formatDateAudio((long) i2);
        if (this.message.isOut()) {
            sb.append(LocaleController.formatString("AccDescrSentDate", NUM, formatDateAudio));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", NUM, formatDateAudio));
        }
        sb.append(". ");
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.message.messageOwner.from_id))) != null) {
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
}
