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
import org.telegram.tgnet.TLRPC;
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
    private boolean attachedToWindow;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int bottomClip;
    private TLRPC.Chat chat;
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
    private TLRPC.DraftMessage draftMessage;
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
    private TLRPC.EncryptedChat encryptedChat;
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
    private TLRPC.User user;

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

    public class BounceInterpolator implements Interpolator {
        public float getInterpolation(float f) {
            if (f < 0.33f) {
                return (f / 0.33f) * 0.1f;
            }
            float f2 = f - 0.33f;
            return f2 < 0.33f ? 0.1f - ((f2 / 0.34f) * 0.15f) : (((f2 - 0.34f) / 0.33f) * 0.05f) - 89.6f;
        }

        public BounceInterpolator() {
        }
    }

    public DialogCell(Context context, boolean z, boolean z2) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.useForceThreeLines = z2;
        if (z) {
            this.checkBox = new CheckBox2(context, 21);
            this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox);
        }
    }

    public void setDialog(TLRPC.Dialog dialog, int i, int i2) {
        this.currentDialogId = dialog.id;
        this.isDialogCell = true;
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            this.currentDialogFolderId = ((TLRPC.TL_dialogFolder) dialog).folder.id;
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
        this.attachedToWindow = false;
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
        this.archiveHidden = SharedConfig.archiveHidden;
        float f = 1.0f;
        this.archiveBackgroundProgress = this.archiveHidden ? 0.0f : 1.0f;
        this.avatarDrawable.setArchivedAvatarHiddenProgress(this.archiveBackgroundProgress);
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

    public boolean isUnread() {
        return (this.unreadCount != 0 || this.markUnread) && !this.dialogMuted;
    }

    private CharSequence formatArchivedDialogNames() {
        TLRPC.User user2;
        String str;
        ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = dialogs.size();
        for (int i = 0; i < size; i++) {
            TLRPC.Dialog dialog = dialogs.get(i);
            TLRPC.Chat chat2 = null;
            if (DialogObject.isSecretDialogId(dialog.id)) {
                TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog.id >> 32)));
                user2 = encryptedChat2 != null ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id)) : null;
            } else {
                int i2 = (int) dialog.id;
                if (i2 > 0) {
                    user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                } else {
                    chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                    user2 = null;
                }
            }
            if (chat2 != null) {
                str = chat2.title.replace(10, ' ');
            } else if (user2 == null) {
                continue;
            } else if (UserObject.isDeleted(user2)) {
                str = LocaleController.getString("HiddenName", NUM);
            } else {
                str = ContactsController.formatName(user2.first_name, user2.last_name).replace(10, ' ');
            }
            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append(", ");
            }
            int length = spannableStringBuilder.length();
            int length2 = str.length() + length;
            spannableStringBuilder.append(str);
            if (dialog.unread_count > 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived")), length, length2, 33);
            }
            if (spannableStringBuilder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0618, code lost:
        if (r9.post_messages == false) goto L_0x05f4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0371  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0b45  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0bfe  */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0c1e  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0d3a  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0d47  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0d57  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0d7e  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0db5  */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0de1  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0e6e  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e84  */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0ffe  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x1023  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x1051  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x1172  */
    /* JADX WARNING: Removed duplicated region for block: B:741:0x1207  */
    /* JADX WARNING: Removed duplicated region for block: B:742:0x1210  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x1224 A[Catch:{ Exception -> 0x129b }] */
    /* JADX WARNING: Removed duplicated region for block: B:753:0x1235 A[Catch:{ Exception -> 0x129b }] */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x1257 A[Catch:{ Exception -> 0x129b }] */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x1285 A[Catch:{ Exception -> 0x129b }] */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x1288 A[Catch:{ Exception -> 0x129b }] */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x12a3  */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x13cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r42 = this;
            r1 = r42
            boolean r0 = r1.useForceThreeLines
            r2 = 1
            r3 = 0
            if (r0 != 0) goto L_0x0059
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x000d
            goto L_0x0059
        L_0x000d:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r3]
            r4 = 1099431936(0x41880000, float:17.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r3]
            r4 = 1099431936(0x41880000, float:17.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r3]
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r3]
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r4 = r0[r3]
            r0 = r0[r3]
            java.lang.String r5 = "chats_message"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r4.setColor(r5)
            r1.paintIndex = r3
            goto L_0x00a4
        L_0x0059:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r2]
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r2]
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r2]
            r4 = 1097859072(0x41700000, float:15.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r2]
            r4 = 1097859072(0x41700000, float:15.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r4 = r0[r2]
            r0 = r0[r2]
            java.lang.String r5 = "chats_message_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r4.setColor(r5)
            r1.paintIndex = r2
        L_0x00a4:
            r1.currentDialogFolderDialogsCount = r3
            boolean r0 = r1.isDialogCell
            if (r0 == 0) goto L_0x00bb
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.util.LongSparseArray<java.lang.CharSequence> r0 = r0.printingStrings
            long r5 = r1.currentDialogId
            java.lang.Object r0 = r0.get(r5)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            goto L_0x00bc
        L_0x00bb:
            r0 = 0
        L_0x00bc:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r5 = r5[r6]
            r1.drawNameGroup = r3
            r1.drawNameBroadcast = r3
            r1.drawNameLock = r3
            r1.drawNameBot = r3
            r1.drawVerified = r3
            r1.drawScam = r3
            r1.drawPinBackground = r3
            org.telegram.tgnet.TLRPC$User r6 = r1.user
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r6)
            if (r6 != 0) goto L_0x00de
            boolean r6 = r1.useMeForMyMessages
            if (r6 != 0) goto L_0x00de
            r6 = 1
            goto L_0x00df
        L_0x00de:
            r6 = 0
        L_0x00df:
            int r7 = android.os.Build.VERSION.SDK_INT
            r8 = 18
            if (r7 < r8) goto L_0x00f8
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x00ed
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x00f1
        L_0x00ed:
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x00f4
        L_0x00f1:
            java.lang.String r7 = "%2$s: ⁨%1$s⁩"
            goto L_0x0106
        L_0x00f4:
            java.lang.String r7 = "⁨%s⁩"
            goto L_0x010a
        L_0x00f8:
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0100
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x0104
        L_0x0100:
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x0108
        L_0x0104:
            java.lang.String r7 = "%2$s: %1$s"
        L_0x0106:
            r8 = 1
            goto L_0x010b
        L_0x0108:
            java.lang.String r7 = "%1$s"
        L_0x010a:
            r8 = 0
        L_0x010b:
            org.telegram.messenger.MessageObject r9 = r1.message
            if (r9 == 0) goto L_0x0112
            java.lang.CharSequence r9 = r9.messageText
            goto L_0x0113
        L_0x0112:
            r9 = 0
        L_0x0113:
            r1.lastMessageString = r9
            org.telegram.ui.Cells.DialogCell$CustomDialog r9 = r1.customDialog
            r11 = 32
            r12 = 10
            r13 = 1102053376(0x41b00000, float:22.0)
            r14 = 150(0x96, float:2.1E-43)
            r15 = 1099956224(0x41900000, float:18.0)
            java.lang.String r4 = ""
            r16 = 1117257728(0x42980000, float:76.0)
            r17 = 1117519872(0x429CLASSNAME, float:78.0)
            r10 = 2
            if (r9 == 0) goto L_0x0371
            int r0 = r9.type
            if (r0 != r10) goto L_0x01b3
            r1.drawNameLock = r2
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0176
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0139
            goto L_0x0176
        L_0x0139:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x015c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLockLeft = r0
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0288
        L_0x015c:
            int r0 = r42.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 - r6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0176:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0199
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLockLeft = r0
            r0 = 1118044160(0x42a40000, float:82.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0199:
            int r0 = r42.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 - r6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x01b3:
            boolean r6 = r9.verified
            r1.drawVerified = r6
            boolean r6 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r6 == 0) goto L_0x025c
            if (r0 != r2) goto L_0x025c
            r1.drawNameGroup = r2
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0213
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c8
            goto L_0x0213
        L_0x01c8:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01f2
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLockLeft = r0
            r0 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x01e7
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01e9
        L_0x01e7:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01e9:
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0288
        L_0x01f2:
            int r0 = r42.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 - r6
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x0202
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0204
        L_0x0202:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0204:
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0213:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x023c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLockLeft = r0
            r0 = 1118044160(0x42a40000, float:82.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x0232
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0234
        L_0x0232:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0234:
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0288
        L_0x023c:
            int r0 = r42.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 - r6
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x024c
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x024e
        L_0x024c:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x024e:
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
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
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0270:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0277:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0282
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLeft = r0
            goto L_0x0288
        L_0x0282:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r0
        L_0x0288:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r6 = r0.type
            if (r6 != r2) goto L_0x0316
            r0 = 2131625269(0x7f0e0535, float:1.8877741E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r6 = r1.customDialog
            boolean r8 = r6.isMedia
            if (r8 == 0) goto L_0x02c8
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r5 = r5[r6]
            java.lang.Object[] r6 = new java.lang.Object[r2]
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.CharSequence r8 = r8.messageText
            r6[r3] = r8
            java.lang.String r6 = java.lang.String.format(r7, r6)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)
            android.text.style.ForegroundColorSpan r7 = new android.text.style.ForegroundColorSpan
            java.lang.String r8 = "chats_attachMessage"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.<init>(r8)
            int r8 = r6.length()
            r9 = 33
            r6.setSpan(r7, r3, r8, r9)
            goto L_0x02fe
        L_0x02c8:
            java.lang.String r6 = r6.message
            int r8 = r6.length()
            if (r8 <= r14) goto L_0x02d4
            java.lang.String r6 = r6.substring(r3, r14)
        L_0x02d4:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x02f0
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x02dd
            goto L_0x02f0
        L_0x02dd:
            java.lang.Object[] r8 = new java.lang.Object[r10]
            java.lang.String r6 = r6.replace(r12, r11)
            r8[r3] = r6
            r8[r2] = r0
            java.lang.String r6 = java.lang.String.format(r7, r8)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)
            goto L_0x02fe
        L_0x02f0:
            java.lang.Object[] r8 = new java.lang.Object[r10]
            r8[r3] = r6
            r8[r2] = r0
            java.lang.String r6 = java.lang.String.format(r7, r8)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)
        L_0x02fe:
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r7 = r7[r8]
            android.graphics.Paint$FontMetricsInt r7 = r7.getFontMetricsInt()
            r8 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r7, r9, r3)
            r7 = r5
            r5 = r0
            r0 = 0
            goto L_0x0325
        L_0x0316:
            java.lang.String r6 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0322
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r5 = r0[r5]
        L_0x0322:
            r7 = r5
            r0 = 1
            r5 = 0
        L_0x0325:
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            int r8 = r8.date
            long r8 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            org.telegram.ui.Cells.DialogCell$CustomDialog r9 = r1.customDialog
            int r9 = r9.unread_count
            if (r9 == 0) goto L_0x0345
            r1.drawCount = r2
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r11[r3] = r9
            java.lang.String r9 = "%d"
            java.lang.String r9 = java.lang.String.format(r9, r11)
            goto L_0x0348
        L_0x0345:
            r1.drawCount = r3
            r9 = 0
        L_0x0348:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            boolean r11 = r11.sent
            if (r11 == 0) goto L_0x0357
            r1.drawCheck1 = r2
            r1.drawCheck2 = r2
            r1.drawClock = r3
            r1.drawError = r3
            goto L_0x035f
        L_0x0357:
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawError = r3
        L_0x035f:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            java.lang.String r11 = r11.name
            r28 = r0
            r40 = r5
            r29 = r6
            r6 = r8
            r15 = r9
            r14 = r11
            r0 = 1
            r12 = 0
            r11 = r7
            goto L_0x0cf3
        L_0x0371:
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x038c
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x037a
            goto L_0x038c
        L_0x037a:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0385
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLeft = r9
            goto L_0x039d
        L_0x0385:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r9
            goto L_0x039d
        L_0x038c:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0397
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLeft = r9
            goto L_0x039d
        L_0x0397:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r9
        L_0x039d:
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r1.encryptedChat
            if (r9 == 0) goto L_0x042a
            int r9 = r1.currentDialogFolderId
            if (r9 != 0) goto L_0x05c3
            r1.drawNameLock = r2
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x03ed
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x03b0
            goto L_0x03ed
        L_0x03b0:
            r9 = 1099169792(0x41840000, float:16.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x03d3
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLockLeft = r9
            r9 = 1117782016(0x42a00000, float:80.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x03d3:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r9 = r9 - r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x03ed:
            r9 = 1095237632(0x41480000, float:12.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0410
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLockLeft = r9
            r9 = 1118044160(0x42a40000, float:82.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0410:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r9 - r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x042a:
            int r9 = r1.currentDialogFolderId
            if (r9 != 0) goto L_0x05c3
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            if (r9 == 0) goto L_0x0525
            boolean r11 = r9.scam
            if (r11 == 0) goto L_0x043e
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r9.checkText()
            goto L_0x0442
        L_0x043e:
            boolean r9 = r9.verified
            r1.drawVerified = r9
        L_0x0442:
            boolean r9 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r9 == 0) goto L_0x05c3
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x04ba
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x044f
            goto L_0x04ba
        L_0x044f:
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            int r11 = r9.id
            if (r11 < 0) goto L_0x046d
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x0462
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r9 = r9.megagroup
            if (r9 != 0) goto L_0x0462
            goto L_0x046d
        L_0x0462:
            r1.drawNameGroup = r2
            r9 = 1099694080(0x418CLASSNAME, float:17.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            goto L_0x0477
        L_0x046d:
            r1.drawNameBroadcast = r2
            r9 = 1099169792(0x41840000, float:16.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
        L_0x0477:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0499
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLockLeft = r9
            r9 = 1117782016(0x42a00000, float:80.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x048e
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0490
        L_0x048e:
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0490:
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0499:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r9 = r9 - r11
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x04a9
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04ab
        L_0x04a9:
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04ab:
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x04ba:
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            int r11 = r9.id
            if (r11 < 0) goto L_0x04d8
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x04cd
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r9 = r9.megagroup
            if (r9 != 0) goto L_0x04cd
            goto L_0x04d8
        L_0x04cd:
            r1.drawNameGroup = r2
            r9 = 1096286208(0x41580000, float:13.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            goto L_0x04e2
        L_0x04d8:
            r1.drawNameBroadcast = r2
            r9 = 1095237632(0x41480000, float:12.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
        L_0x04e2:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0504
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLockLeft = r9
            r9 = 1118044160(0x42a40000, float:82.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x04f9
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04fb
        L_0x04f9:
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04fb:
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0504:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r9 - r11
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x0514
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0516
        L_0x0514:
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0516:
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0525:
            org.telegram.tgnet.TLRPC$User r9 = r1.user
            if (r9 == 0) goto L_0x05c3
            boolean r11 = r9.scam
            if (r11 == 0) goto L_0x0535
            r1.drawScam = r2
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r9.checkText()
            goto L_0x0539
        L_0x0535:
            boolean r9 = r9.verified
            r1.drawVerified = r9
        L_0x0539:
            boolean r9 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r9 == 0) goto L_0x05c3
            org.telegram.tgnet.TLRPC$User r9 = r1.user
            boolean r9 = r9.bot
            if (r9 == 0) goto L_0x05c3
            r1.drawNameBot = r2
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x0589
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x054e
            goto L_0x0589
        L_0x054e:
            r9 = 1099169792(0x41840000, float:16.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0570
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.nameLockLeft = r9
            r9 = 1117782016(0x42a00000, float:80.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0570:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r9 = r9 - r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x0589:
            r9 = 1095237632(0x41480000, float:12.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.nameLockTop = r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x05ab
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.nameLockLeft = r9
            r9 = 1118044160(0x42a40000, float:82.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 + r11
            r1.nameLeft = r9
            goto L_0x05c3
        L_0x05ab:
            int r9 = r42.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r9 = r9 - r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r11 = r11.getIntrinsicWidth()
            int r9 = r9 - r11
            r1.nameLockLeft = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLeft = r9
        L_0x05c3:
            int r9 = r1.lastMessageDate
            if (r9 != 0) goto L_0x05cf
            org.telegram.messenger.MessageObject r11 = r1.message
            if (r11 == 0) goto L_0x05cf
            org.telegram.tgnet.TLRPC$Message r9 = r11.messageOwner
            int r9 = r9.date
        L_0x05cf:
            boolean r11 = r1.isDialogCell
            if (r11 == 0) goto L_0x062c
            int r11 = r1.currentAccount
            org.telegram.messenger.MediaDataController r11 = org.telegram.messenger.MediaDataController.getInstance(r11)
            long r12 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r11.getDraft(r12)
            r1.draftMessage = r11
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            if (r11 == 0) goto L_0x0600
            java.lang.String r11 = r11.message
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 == 0) goto L_0x05f6
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            int r11 = r11.reply_to_msg_id
            if (r11 == 0) goto L_0x05f4
            goto L_0x05f6
        L_0x05f4:
            r9 = 0
            goto L_0x0627
        L_0x05f6:
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            int r11 = r11.date
            if (r9 <= r11) goto L_0x0600
            int r9 = r1.unreadCount
            if (r9 != 0) goto L_0x05f4
        L_0x0600:
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x061a
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            boolean r11 = r9.megagroup
            if (r11 != 0) goto L_0x061a
            boolean r11 = r9.creator
            if (r11 != 0) goto L_0x061a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r9.admin_rights
            if (r9 == 0) goto L_0x05f4
            boolean r9 = r9.post_messages
            if (r9 == 0) goto L_0x05f4
        L_0x061a:
            org.telegram.tgnet.TLRPC$Chat r9 = r1.chat
            if (r9 == 0) goto L_0x062a
            boolean r11 = r9.left
            if (r11 != 0) goto L_0x05f4
            boolean r9 = r9.kicked
            if (r9 == 0) goto L_0x062a
            goto L_0x05f4
        L_0x0627:
            r1.draftMessage = r9
            goto L_0x062f
        L_0x062a:
            r9 = 0
            goto L_0x062f
        L_0x062c:
            r9 = 0
            r1.draftMessage = r9
        L_0x062f:
            if (r0 == 0) goto L_0x063f
            r1.lastPrintString = r0
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            r7 = r0
            r8 = r5
            r0 = 1
            r5 = 1
            goto L_0x0b49
        L_0x063f:
            r1.lastPrintString = r9
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06f0
            r0 = 2131624956(0x7f0e03fc, float:1.8877106E38)
            java.lang.String r8 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0682
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x067b
            boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x0661
            goto L_0x067b
        L_0x0661:
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r8 = new android.text.style.ForegroundColorSpan
            java.lang.String r9 = "chats_draft"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.<init>(r9)
            int r9 = r0.length()
            r11 = 33
            r7.setSpan(r8, r3, r9, r11)
        L_0x0679:
            r9 = r0
            goto L_0x067d
        L_0x067b:
            r9 = r0
            r7 = r4
        L_0x067d:
            r8 = r5
            r0 = 1
            r5 = 0
            goto L_0x0b49
        L_0x0682:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            int r9 = r8.length()
            if (r9 <= r14) goto L_0x0690
            java.lang.String r8 = r8.substring(r3, r14)
        L_0x0690:
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x06c5
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x0699
            goto L_0x06c5
        L_0x0699:
            java.lang.Object[] r9 = new java.lang.Object[r10]
            r11 = 32
            r12 = 10
            java.lang.String r8 = r8.replace(r12, r11)
            r9[r3] = r8
            r9[r2] = r0
            java.lang.String r7 = java.lang.String.format(r7, r9)
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r7)
            android.text.style.ForegroundColorSpan r8 = new android.text.style.ForegroundColorSpan
            java.lang.String r9 = "chats_draft"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.<init>(r9)
            int r9 = r0.length()
            int r9 = r9 + r2
            r11 = 33
            r7.setSpan(r8, r3, r9, r11)
            goto L_0x06db
        L_0x06c5:
            java.lang.Object[] r9 = new java.lang.Object[r10]
            r11 = 32
            r12 = 10
            java.lang.String r8 = r8.replace(r12, r11)
            r9[r3] = r8
            r9[r2] = r0
            java.lang.String r7 = java.lang.String.format(r7, r9)
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r7)
        L_0x06db:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r8 = r8[r9]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            r9 = 1101004800(0x41a00000, float:20.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            java.lang.CharSequence r7 = org.telegram.messenger.Emoji.replaceEmoji(r7, r8, r11, r3)
            goto L_0x0679
        L_0x06f0:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x070a
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r5 = r0[r5]
            r0 = 2131625332(0x7f0e0574, float:1.8877869E38)
            java.lang.String r7 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
        L_0x0703:
            r7 = r0
        L_0x0704:
            r8 = r5
            r0 = 1
            r5 = 1
            r9 = 0
            goto L_0x0b49
        L_0x070a:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x077e
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x077c
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested
            if (r7 == 0) goto L_0x0726
            r0 = 2131625035(0x7f0e044b, float:1.8877267E38)
            java.lang.String r7 = "EncryptionProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x0703
        L_0x0726:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r7 == 0) goto L_0x073e
            r0 = 2131624383(0x7f0e01bf, float:1.8875944E38)
            java.lang.Object[] r7 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r7[r3] = r8
            java.lang.String r8 = "AwaitingEncryption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r0, r7)
            goto L_0x0703
        L_0x073e:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r7 == 0) goto L_0x074c
            r0 = 2131625036(0x7f0e044c, float:1.8877269E38)
            java.lang.String r7 = "EncryptionRejected"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x0703
        L_0x074c:
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r7 == 0) goto L_0x077c
            int r0 = r0.admin_id
            int r7 = r1.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            int r7 = r7.getClientUserId()
            if (r0 != r7) goto L_0x0772
            r0 = 2131625024(0x7f0e0440, float:1.8877244E38)
            java.lang.Object[] r7 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r7[r3] = r8
            java.lang.String r8 = "EncryptedChatStartedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r0, r7)
            goto L_0x0703
        L_0x0772:
            r0 = 2131625023(0x7f0e043f, float:1.8877242E38)
            java.lang.String r7 = "EncryptedChatStartedIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x0703
        L_0x077c:
            r7 = r4
            goto L_0x0704
        L_0x077e:
            boolean r0 = r0.isFromUser()
            if (r0 == 0) goto L_0x079b
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            int r9 = r9.from_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r9)
            r9 = r0
            r0 = 0
            goto L_0x07b2
        L_0x079b:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r9 = r9.to_id
            int r9 = r9.channel_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r9)
            r9 = 0
        L_0x07b2:
            int r11 = r1.dialogsType
            r12 = 3
            if (r11 != r12) goto L_0x07d0
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 == 0) goto L_0x07d0
            r0 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            java.lang.String r6 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r7 = r0
            r8 = r5
            r0 = 0
            r5 = 1
            r6 = 0
        L_0x07cd:
            r9 = 0
            goto L_0x0b41
        L_0x07d0:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x07e5
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 != 0) goto L_0x07e5
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x07e5
            java.lang.CharSequence r0 = r42.formatArchivedDialogNames()
            r7 = r0
            r8 = r5
            r0 = 1
            r5 = 0
            goto L_0x07cd
        L_0x07e5:
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner
            boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r12 == 0) goto L_0x0815
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0806
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r5 != 0) goto L_0x0803
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x0806
        L_0x0803:
            r0 = r4
            r6 = 0
            goto L_0x080a
        L_0x0806:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x080a:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
        L_0x0810:
            r7 = r0
            r8 = r5
            r0 = 1
            r5 = 1
            goto L_0x07cd
        L_0x0815:
            org.telegram.tgnet.TLRPC$Chat r12 = r1.chat
            if (r12 == 0) goto L_0x0a3f
            int r12 = r12.id
            if (r12 <= 0) goto L_0x0a3f
            if (r0 != 0) goto L_0x0a3f
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0830
            r0 = 2131625269(0x7f0e0535, float:1.8877741E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
        L_0x082e:
            r9 = r0
            goto L_0x0873
        L_0x0830:
            if (r9 == 0) goto L_0x0865
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0846
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x083b
            goto L_0x0846
        L_0x083b:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r9)
            java.lang.String r9 = "\n"
            java.lang.String r0 = r0.replace(r9, r4)
            goto L_0x082e
        L_0x0846:
            boolean r0 = org.telegram.messenger.UserObject.isDeleted(r9)
            if (r0 == 0) goto L_0x0856
            r0 = 2131625328(0x7f0e0570, float:1.887786E38)
            java.lang.String r9 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            goto L_0x082e
        L_0x0856:
            java.lang.String r0 = r9.first_name
            java.lang.String r9 = r9.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r9)
            java.lang.String r9 = "\n"
            java.lang.String r0 = r0.replace(r9, r4)
            goto L_0x082e
        L_0x0865:
            if (r0 == 0) goto L_0x0870
            java.lang.String r0 = r0.title
            java.lang.String r9 = "\n"
            java.lang.String r0 = r0.replace(r9, r4)
            goto L_0x082e
        L_0x0870:
            java.lang.String r0 = "DELETED"
            goto L_0x082e
        L_0x0873:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r11 = r0.caption
            if (r11 == 0) goto L_0x08e1
            java.lang.String r0 = r11.toString()
            int r8 = r0.length()
            if (r8 <= r14) goto L_0x0887
            java.lang.String r0 = r0.substring(r3, r14)
        L_0x0887:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x0893
            java.lang.String r8 = "📹 "
            goto L_0x08ba
        L_0x0893:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVoice()
            if (r8 == 0) goto L_0x089f
            java.lang.String r8 = "🎤 "
            goto L_0x08ba
        L_0x089f:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isMusic()
            if (r8 == 0) goto L_0x08ab
            java.lang.String r8 = "🎧 "
            goto L_0x08ba
        L_0x08ab:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 == 0) goto L_0x08b7
            java.lang.String r8 = "🖼 "
            goto L_0x08ba
        L_0x08b7:
            java.lang.String r8 = "📎 "
        L_0x08ba:
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r8)
            r8 = 32
            r13 = 10
            java.lang.String r0 = r0.replace(r13, r8)
            r12.append(r0)
            java.lang.String r0 = r12.toString()
            r11[r3] = r0
            r11[r2] = r9
            java.lang.String r0 = java.lang.String.format(r7, r11)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09f8
        L_0x08e1:
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            if (r11 == 0) goto L_0x09cb
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x09cb
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r5 = r0[r5]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r12 == 0) goto L_0x0926
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r11
            int r0 = android.os.Build.VERSION.SDK_INT
            r12 = 18
            if (r0 < r12) goto L_0x0916
            java.lang.Object[] r0 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$TL_poll r11 = r11.poll
            java.lang.String r11 = r11.question
            r0[r3] = r11
            java.lang.String r11 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r11, r0)
            goto L_0x0992
        L_0x0916:
            java.lang.Object[] r0 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$TL_poll r11 = r11.poll
            java.lang.String r11 = r11.question
            r0[r3] = r11
            java.lang.String r11 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r11, r0)
            goto L_0x0992
        L_0x0926:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r12 == 0) goto L_0x0950
            int r0 = android.os.Build.VERSION.SDK_INT
            r12 = 18
            if (r0 < r12) goto L_0x0940
            java.lang.Object[] r0 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
            java.lang.String r11 = r11.title
            r0[r3] = r11
            java.lang.String r11 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r11, r0)
            goto L_0x0992
        L_0x0940:
            java.lang.Object[] r0 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
            java.lang.String r11 = r11.title
            r0[r3] = r11
            java.lang.String r11 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r11, r0)
            goto L_0x0992
        L_0x0950:
            int r11 = r0.type
            r12 = 14
            if (r11 != r12) goto L_0x098c
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 18
            if (r11 < r12) goto L_0x0974
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r0 = r0.getMusicAuthor()
            r11[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r11[r2] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            goto L_0x0992
        L_0x0974:
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r0 = r0.getMusicAuthor()
            r11[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r11[r2] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            goto L_0x0992
        L_0x098c:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0992:
            r11 = 32
            r12 = 10
            java.lang.String r0 = r0.replace(r12, r11)
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r3] = r0
            r11[r2] = r9
            java.lang.String r0 = java.lang.String.format(r7, r11)
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x09c6 }
            java.lang.String r11 = "chats_attachMessage"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)     // Catch:{ Exception -> 0x09c6 }
            r0.<init>(r11)     // Catch:{ Exception -> 0x09c6 }
            if (r8 == 0) goto L_0x09bb
            int r8 = r9.length()     // Catch:{ Exception -> 0x09c6 }
            int r8 = r8 + r10
            goto L_0x09bc
        L_0x09bb:
            r8 = 0
        L_0x09bc:
            int r11 = r7.length()     // Catch:{ Exception -> 0x09c6 }
            r12 = 33
            r7.setSpan(r0, r8, r11, r12)     // Catch:{ Exception -> 0x09c6 }
            goto L_0x09f9
        L_0x09c6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x09f9
        L_0x09cb:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x09f4
            int r8 = r0.length()
            if (r8 <= r14) goto L_0x09dd
            java.lang.String r0 = r0.substring(r3, r14)
        L_0x09dd:
            java.lang.Object[] r8 = new java.lang.Object[r10]
            r11 = 32
            r12 = 10
            java.lang.String r0 = r0.replace(r12, r11)
            r8[r3] = r0
            r8[r2] = r9
            java.lang.String r0 = java.lang.String.format(r7, r8)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x09f8
        L_0x09f4:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x09f8:
            r7 = r0
        L_0x09f9:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0a01
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0a0b
        L_0x0a01:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0a25
            int r0 = r7.length()
            if (r0 <= 0) goto L_0x0a25
        L_0x0a0b:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r8 = "chats_nameMessage"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x0a21 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0a21 }
            int r8 = r9.length()     // Catch:{ Exception -> 0x0a21 }
            int r8 = r8 + r2
            r11 = 33
            r7.setSpan(r0, r3, r8, r11)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0a25
        L_0x0a21:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a25:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r0 = r0[r8]
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            r8 = 1101004800(0x41a00000, float:20.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r7, r0, r11, r3)
            r7 = r0
            r8 = r5
            r0 = 1
            r5 = 0
            goto L_0x0b41
        L_0x0a3f:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r7 == 0) goto L_0x0a5e
            org.telegram.tgnet.TLRPC$Photo r7 = r0.photo
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r7 == 0) goto L_0x0a5e
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a5e
            r0 = 2131624291(0x7f0e0163, float:1.8875758E38)
            java.lang.String r7 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x0810
        L_0x0a5e:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r7 == 0) goto L_0x0a7d
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
            if (r7 == 0) goto L_0x0a7d
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a7d
            r0 = 2131624297(0x7f0e0169, float:1.887577E38)
            java.lang.String r7 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x0810
        L_0x0a7d:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r7 = r0.caption
            if (r7 == 0) goto L_0x0ac9
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0a8d
            java.lang.String r0 = "📹 "
            goto L_0x0ab4
        L_0x0a8d:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0a99
            java.lang.String r0 = "🎤 "
            goto L_0x0ab4
        L_0x0a99:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0aa5
            java.lang.String r0 = "🎧 "
            goto L_0x0ab4
        L_0x0aa5:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0ab1
            java.lang.String r0 = "🖼 "
            goto L_0x0ab4
        L_0x0ab1:
            java.lang.String r0 = "📎 "
        L_0x0ab4:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            goto L_0x0810
        L_0x0ac9:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r8 == 0) goto L_0x0aea
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "📊 "
            r0.append(r8)
            org.telegram.tgnet.TLRPC$TL_poll r7 = r7.poll
            java.lang.String r7 = r7.question
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0b2b
        L_0x0aea:
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r7 == 0) goto L_0x0b0b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "🎮 "
            r0.append(r7)
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            goto L_0x0b2b
        L_0x0b0b:
            int r7 = r0.type
            r8 = 14
            if (r7 != r8) goto L_0x0b29
            java.lang.Object[] r7 = new java.lang.Object[r10]
            java.lang.String r0 = r0.getMusicAuthor()
            r7[r3] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r7[r2] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            goto L_0x0b2b
        L_0x0b29:
            java.lang.CharSequence r0 = r0.messageText
        L_0x0b2b:
            org.telegram.messenger.MessageObject r7 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            if (r8 == 0) goto L_0x0810
            boolean r7 = r7.isMediaEmpty()
            if (r7 != 0) goto L_0x0810
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r7 = r1.paintIndex
            r5 = r5[r7]
            goto L_0x0810
        L_0x0b41:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0b49
            java.lang.CharSequence r9 = r42.formatArchivedDialogNames()
        L_0x0b49:
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            if (r11 == 0) goto L_0x0b55
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0b6e
        L_0x0b55:
            int r11 = r1.lastMessageDate
            if (r11 == 0) goto L_0x0b5f
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0b6e
        L_0x0b5f:
            org.telegram.messenger.MessageObject r11 = r1.message
            if (r11 == 0) goto L_0x0b6d
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            goto L_0x0b6e
        L_0x0b6d:
            r11 = r4
        L_0x0b6e:
            org.telegram.messenger.MessageObject r12 = r1.message
            if (r12 != 0) goto L_0x0b82
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawCount = r3
            r1.drawMention = r3
            r1.drawError = r3
            r10 = 0
            r12 = 0
            goto L_0x0CLASSNAME
        L_0x0b82:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0bc1
            int r12 = r1.unreadCount
            int r13 = r1.mentionCount
            int r18 = r12 + r13
            if (r18 <= 0) goto L_0x0bba
            if (r12 <= r13) goto L_0x0ba4
            r1.drawCount = r2
            r1.drawMention = r3
            java.lang.Object[] r10 = new java.lang.Object[r2]
            int r12 = r12 + r13
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10[r3] = r12
            java.lang.String r12 = "%d"
            java.lang.String r10 = java.lang.String.format(r12, r10)
            goto L_0x0bbf
        L_0x0ba4:
            r1.drawCount = r3
            r1.drawMention = r2
            java.lang.Object[] r10 = new java.lang.Object[r2]
            int r12 = r12 + r13
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10[r3] = r12
            java.lang.String r12 = "%d"
            java.lang.String r10 = java.lang.String.format(r12, r10)
            r12 = r10
            r10 = 0
            goto L_0x0CLASSNAME
        L_0x0bba:
            r1.drawCount = r3
            r1.drawMention = r3
            r10 = 0
        L_0x0bbf:
            r12 = 0
            goto L_0x0CLASSNAME
        L_0x0bc1:
            boolean r10 = r1.clearingDialog
            if (r10 == 0) goto L_0x0bca
            r1.drawCount = r3
            r6 = 0
        L_0x0bc8:
            r10 = 0
            goto L_0x0bfa
        L_0x0bca:
            int r10 = r1.unreadCount
            if (r10 == 0) goto L_0x0bef
            if (r10 != r2) goto L_0x0bdc
            int r13 = r1.mentionCount
            if (r10 != r13) goto L_0x0bdc
            if (r12 == 0) goto L_0x0bdc
            org.telegram.tgnet.TLRPC$Message r10 = r12.messageOwner
            boolean r10 = r10.mentioned
            if (r10 != 0) goto L_0x0bef
        L_0x0bdc:
            r1.drawCount = r2
            java.lang.Object[] r10 = new java.lang.Object[r2]
            int r12 = r1.unreadCount
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10[r3] = r12
            java.lang.String r12 = "%d"
            java.lang.String r10 = java.lang.String.format(r12, r10)
            goto L_0x0bfa
        L_0x0bef:
            boolean r10 = r1.markUnread
            if (r10 == 0) goto L_0x0bf7
            r1.drawCount = r2
            r10 = r4
            goto L_0x0bfa
        L_0x0bf7:
            r1.drawCount = r3
            goto L_0x0bc8
        L_0x0bfa:
            int r12 = r1.mentionCount
            if (r12 == 0) goto L_0x0CLASSNAME
            r1.drawMention = r2
            java.lang.String r12 = "@"
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1.drawMention = r3
            goto L_0x0bbf
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isOut()
            if (r13 == 0) goto L_0x0c6d
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 != 0) goto L_0x0c6d
            if (r6 == 0) goto L_0x0c6d
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r13 != 0) goto L_0x0c6d
            boolean r6 = r6.isSending()
            if (r6 == 0) goto L_0x0c2d
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r2
            r1.drawError = r3
            goto L_0x0CLASSNAME
        L_0x0c2d:
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isSendError()
            if (r6 == 0) goto L_0x0CLASSNAME
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawError = r2
            r1.drawCount = r3
            r1.drawMention = r3
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isSent()
            if (r6 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isUnread()
            if (r6 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = 1
        L_0x0CLASSNAME:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r2
            r1.drawClock = r3
            r1.drawError = r3
            goto L_0x0CLASSNAME
        L_0x0c6d:
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClock = r3
            r1.drawError = r3
        L_0x0CLASSNAME:
            int r6 = r1.dialogsType
            if (r6 != 0) goto L_0x0CLASSNAME
            int r6 = r1.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            long r14 = r1.currentDialogId
            boolean r6 = r6.isProxyDialog(r14, r2)
            if (r6 == 0) goto L_0x0CLASSNAME
            r1.drawPinBackground = r2
            r6 = 2131626961(0x7f0e0bd1, float:1.8881173E38)
            java.lang.String r11 = "UseProxySponsor"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r6)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = r11
        L_0x0CLASSNAME:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0cab
            r11 = 2131624210(0x7f0e0112, float:1.8875593E38)
            java.lang.String r14 = "ArchivedChats"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
        L_0x0ca1:
            r28 = r5
            r29 = r7
            r40 = r9
            r15 = r10
            r14 = r11
            r11 = r8
            goto L_0x0cf3
        L_0x0cab:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            if (r11 == 0) goto L_0x0cb2
            java.lang.String r11 = r11.title
            goto L_0x0ce3
        L_0x0cb2:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            if (r11 == 0) goto L_0x0ce2
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r11)
            if (r11 == 0) goto L_0x0cdb
            boolean r11 = r1.useMeForMyMessages
            if (r11 == 0) goto L_0x0cca
            r11 = 2131625269(0x7f0e0535, float:1.8877741E38)
            java.lang.String r14 = "FromYou"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x0ce3
        L_0x0cca:
            int r11 = r1.dialogsType
            r14 = 3
            if (r11 != r14) goto L_0x0cd1
            r1.drawPinBackground = r2
        L_0x0cd1:
            r11 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r14 = "SavedMessages"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x0ce3
        L_0x0cdb:
            org.telegram.tgnet.TLRPC$User r11 = r1.user
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            goto L_0x0ce3
        L_0x0ce2:
            r11 = r4
        L_0x0ce3:
            int r14 = r11.length()
            if (r14 != 0) goto L_0x0ca1
            r11 = 2131625328(0x7f0e0570, float:1.887786E38)
            java.lang.String r14 = "HiddenName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x0ca1
        L_0x0cf3:
            if (r0 == 0) goto L_0x0d3a
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r6)
            double r7 = (double) r0
            double r7 = java.lang.Math.ceil(r7)
            int r0 = (int) r7
            android.text.StaticLayout r10 = new android.text.StaticLayout
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r5 = r10
            r8 = r0
            r13 = r10
            r10 = r20
            r41 = r11
            r11 = r21
            r2 = r12
            r12 = r22
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)
            r1.timeLayout = r13
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0d31
            int r5 = r42.getMeasuredWidth()
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x0d43
        L_0x0d31:
            r5 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeLeft = r5
            goto L_0x0d43
        L_0x0d3a:
            r41 = r11
            r2 = r12
            r5 = 0
            r1.timeLayout = r5
            r1.timeLeft = r3
            r0 = 0
        L_0x0d43:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0d57
            int r5 = r42.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            goto L_0x0d6b
        L_0x0d57:
            int r5 = r42.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1117388800(0x429a0000, float:77.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x0d6b:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x0d7e
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r7 = r7.getIntrinsicWidth()
        L_0x0d7b:
            int r6 = r6 + r7
            int r5 = r5 - r6
            goto L_0x0db1
        L_0x0d7e:
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x0d8f
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r7 = r7.getIntrinsicWidth()
            goto L_0x0d7b
        L_0x0d8f:
            boolean r6 = r1.drawNameBroadcast
            if (r6 == 0) goto L_0x0da0
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r7 = r7.getIntrinsicWidth()
            goto L_0x0d7b
        L_0x0da0:
            boolean r6 = r1.drawNameBot
            if (r6 == 0) goto L_0x0db1
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r7 = r7.getIntrinsicWidth()
            goto L_0x0d7b
        L_0x0db1:
            boolean r6 = r1.drawClock
            if (r6 == 0) goto L_0x0de1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            r7 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            int r5 = r5 - r6
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x0dce
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x0e60
        L_0x0dce:
            int r7 = r1.timeLeft
            int r7 = r7 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r7 = r7 + r0
            r1.checkDrawLeft = r7
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0e60
        L_0x0de1:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x0e60
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            r7 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            int r5 = r5 - r6
            boolean r7 = r1.drawCheck1
            if (r7 == 0) goto L_0x0e45
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r7 = r7.getIntrinsicWidth()
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            int r5 = r5 - r7
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x0e1a
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.halfCheckDrawLeft = r0
            int r0 = r1.halfCheckDrawLeft
            r6 = 1085276160(0x40b00000, float:5.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x0e60
        L_0x0e1a:
            int r7 = r1.timeLeft
            int r7 = r7 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r7 = r7 + r0
            r1.checkDrawLeft = r7
            int r0 = r1.checkDrawLeft
            r7 = 1085276160(0x40b00000, float:5.5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.halfCheckDrawLeft = r0
            int r0 = r1.nameLeft
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r7 = r7.getIntrinsicWidth()
            int r6 = r6 + r7
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x0e60
        L_0x0e45:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 != 0) goto L_0x0e4f
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x0e60
        L_0x0e4f:
            int r7 = r1.timeLeft
            int r7 = r7 + r0
            r0 = 1084227584(0x40a00000, float:5.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r7 = r7 + r0
            r1.checkDrawLeft = r7
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x0e60:
            boolean r0 = r1.dialogMuted
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0e84
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x0e84
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x0e84
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r7 = r7.getIntrinsicWidth()
            int r0 = r0 + r7
            int r5 = r5 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0eb7
            int r7 = r1.nameLeft
            int r7 = r7 + r0
            r1.nameLeft = r7
            goto L_0x0eb7
        L_0x0e84:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x0e9e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r7 = r7.getIntrinsicWidth()
            int r0 = r0 + r7
            int r5 = r5 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0eb7
            int r7 = r1.nameLeft
            int r7 = r7 + r0
            r1.nameLeft = r7
            goto L_0x0eb7
        L_0x0e9e:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x0eb7
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r7 = r7.getIntrinsicWidth()
            int r0 = r0 + r7
            int r5 = r5 - r0
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0eb7
            int r7 = r1.nameLeft
            int r7 = r7 + r0
            r1.nameLeft = r7
        L_0x0eb7:
            r7 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = java.lang.Math.max(r0, r5)
            r8 = 32
            r9 = 10
            java.lang.String r0 = r14.replace(r9, r8)     // Catch:{ Exception -> 0x0ef6 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0ef6 }
            int r9 = r1.paintIndex     // Catch:{ Exception -> 0x0ef6 }
            r8 = r8[r9]     // Catch:{ Exception -> 0x0ef6 }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x0ef6 }
            int r9 = r5 - r9
            float r9 = (float) r9     // Catch:{ Exception -> 0x0ef6 }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0ef6 }
            java.lang.CharSequence r21 = android.text.TextUtils.ellipsize(r0, r8, r9, r10)     // Catch:{ Exception -> 0x0ef6 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0ef6 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0ef6 }
            int r9 = r1.paintIndex     // Catch:{ Exception -> 0x0ef6 }
            r22 = r8[r9]     // Catch:{ Exception -> 0x0ef6 }
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0ef6 }
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r0
            r23 = r5
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x0ef6 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x0ef6 }
            goto L_0x0efa
        L_0x0ef6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0efa:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0var_
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
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
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.checkDrawTop = r8
            int r8 = r42.getMeasuredWidth()
            r9 = 1119748096(0x42be0000, float:95.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0var_
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0f6d
        L_0x0var_:
            r9 = 1102053376(0x41b00000, float:22.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r42.getMeasuredWidth()
            r10 = 1115684864(0x42800000, float:64.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
        L_0x0f6d:
            org.telegram.messenger.ImageReceiver r10 = r1.avatarImage
            r11 = 1113063424(0x42580000, float:54.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 1113063424(0x42580000, float:54.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r10.setImageCoords(r9, r0, r11, r12)
            goto L_0x0ffa
        L_0x0var_:
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8 = 1107296256(0x42000000, float:32.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.messageNameTop = r8
            r8 = 1095761920(0x41500000, float:13.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.timeTop = r8
            r8 = 1110179840(0x422CLASSNAME, float:43.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.errorTop = r8
            r8 = 1110179840(0x422CLASSNAME, float:43.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.pinTop = r8
            r8 = 1110179840(0x422CLASSNAME, float:43.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.countTop = r8
            r8 = 1095761920(0x41500000, float:13.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.checkDrawTop = r8
            int r8 = r42.getMeasuredWidth()
            r9 = 1119485952(0x42ba0000, float:93.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x0fd4
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0fe9
        L_0x0fd4:
            r9 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r42.getMeasuredWidth()
            r10 = 1115947008(0x42840000, float:66.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
        L_0x0fe9:
            org.telegram.messenger.ImageReceiver r10 = r1.avatarImage
            r11 = 1113587712(0x42600000, float:56.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 1113587712(0x42600000, float:56.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r10.setImageCoords(r9, r0, r11, r12)
        L_0x0ffa:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x101f
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1017
            int r0 = r42.getMeasuredWidth()
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r9 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.pinLeft = r0
            goto L_0x101f
        L_0x1017:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x101f:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1051
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r8 = r8 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x103d
            int r0 = r42.getMeasuredWidth()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.errorLeft = r0
            goto L_0x1170
        L_0x103d:
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.errorLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
            goto L_0x1170
        L_0x1051:
            if (r15 != 0) goto L_0x107c
            if (r2 == 0) goto L_0x1056
            goto L_0x107c
        L_0x1056:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1076
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r8 = r8 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x1076
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x1076:
            r1.drawCount = r3
            r1.drawMention = r3
            goto L_0x1170
        L_0x107c:
            if (r15 == 0) goto L_0x10e2
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r15)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r0 = java.lang.Math.max(r0, r9)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r22 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.countWidth
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_CENTER
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r0
            r21 = r15
            r23 = r9
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r1.countLayout = r0
            int r0 = r1.countWidth
            r9 = 1099956224(0x41900000, float:18.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 + r10
            int r8 = r8 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x10cc
            int r0 = r42.getMeasuredWidth()
            int r9 = r1.countWidth
            int r0 = r0 - r9
            r9 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r10
            r1.countLeft = r0
            goto L_0x10de
        L_0x10cc:
            r9 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.countLeft = r10
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x10de:
            r9 = 1
            r1.drawCount = r9
            goto L_0x10e4
        L_0x10e2:
            r1.countWidth = r3
        L_0x10e4:
            if (r2 == 0) goto L_0x1170
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x111a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r2)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r0 = java.lang.Math.max(r0, r9)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r22 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.mentionWidth
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_CENTER
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r0
            r21 = r2
            r23 = r9
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r1.mentionLayout = r0
            goto L_0x1120
        L_0x111a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.mentionWidth = r0
        L_0x1120:
            int r0 = r1.mentionWidth
            r2 = 1099956224(0x41900000, float:18.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r9
            int r8 = r8 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x114d
            int r0 = r42.getMeasuredWidth()
            int r2 = r1.mentionWidth
            int r0 = r0 - r2
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r2 = r1.countWidth
            if (r2 == 0) goto L_0x1148
            r9 = 1099956224(0x41900000, float:18.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 + r9
            goto L_0x1149
        L_0x1148:
            r2 = 0
        L_0x1149:
            int r0 = r0 - r2
            r1.mentionLeft = r0
            goto L_0x116d
        L_0x114d:
            r2 = 1101004800(0x41a00000, float:20.0)
            r9 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x115f
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r10
            goto L_0x1160
        L_0x115f:
            r9 = 0
        L_0x1160:
            int r2 = r2 + r9
            r1.mentionLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x116d:
            r2 = 1
            r1.drawMention = r2
        L_0x1170:
            if (r28 == 0) goto L_0x11b5
            if (r29 != 0) goto L_0x1176
            r29 = r4
        L_0x1176:
            java.lang.String r0 = r29.toString()
            int r2 = r0.length()
            r4 = 150(0x96, float:2.1E-43)
            if (r2 <= r4) goto L_0x1186
            java.lang.String r0 = r0.substring(r3, r4)
        L_0x1186:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x118e
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x1190
        L_0x118e:
            if (r40 == 0) goto L_0x1199
        L_0x1190:
            r2 = 32
            r4 = 10
            java.lang.String r0 = r0.replace(r4, r2)
            goto L_0x11a1
        L_0x1199:
            java.lang.String r2 = "\n\n"
            java.lang.String r4 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
        L_0x11a1:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r2 = r2[r4]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r4 = 1099431936(0x41880000, float:17.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.CharSequence r29 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r3)
        L_0x11b5:
            r2 = r29
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = java.lang.Math.max(r0, r8)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x11c7
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x11fb
        L_0x11c7:
            if (r40 == 0) goto L_0x11fb
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x11d2
            int r0 = r1.currentDialogFolderDialogsCount
            r8 = 1
            if (r0 != r8) goto L_0x11fb
        L_0x11d2:
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x11ed }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x11ed }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            android.text.TextUtils$TruncateAt r37 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x11ed }
            r39 = 1
            r30 = r40
            r32 = r4
            r38 = r4
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r30, r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ Exception -> 0x11ed }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x11ed }
            goto L_0x11f1
        L_0x11ed:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x11f1:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            r9 = 0
            goto L_0x1218
        L_0x11fb:
            r9 = 0
            r1.messageNameLayout = r9
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1210
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1207
            goto L_0x1210
        L_0x1207:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1218
        L_0x1210:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
        L_0x1218:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x129b }
            if (r0 != 0) goto L_0x1220
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x129b }
            if (r0 == 0) goto L_0x1235
        L_0x1220:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x129b }
            if (r0 == 0) goto L_0x1235
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x129b }
            r8 = 1
            if (r0 <= r8) goto L_0x1236
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x129b }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x129b }
            r11 = r0[r2]     // Catch:{ Exception -> 0x129b }
            r0 = r40
            r40 = r9
            r9 = r11
            goto L_0x1253
        L_0x1235:
            r8 = 1
        L_0x1236:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x129b }
            if (r0 != 0) goto L_0x123e
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x129b }
            if (r0 == 0) goto L_0x1240
        L_0x123e:
            if (r40 == 0) goto L_0x1250
        L_0x1240:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x129b }
            int r0 = r4 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x129b }
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x129b }
            r9 = r41
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r2, r9, r0, r7)     // Catch:{ Exception -> 0x129b }
            goto L_0x1253
        L_0x1250:
            r9 = r41
            r0 = r2
        L_0x1253:
            boolean r2 = r1.useForceThreeLines     // Catch:{ Exception -> 0x129b }
            if (r2 != 0) goto L_0x1274
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x129b }
            if (r2 == 0) goto L_0x125c
            goto L_0x1274
        L_0x125c:
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x129b }
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x129b }
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r2
            r20 = r0
            r21 = r9
            r22 = r4
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)     // Catch:{ Exception -> 0x129b }
            r1.messageLayout = r2     // Catch:{ Exception -> 0x129b }
            goto L_0x129f
        L_0x1274:
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x129b }
            r23 = 1065353216(0x3var_, float:1.0)
            r2 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x129b }
            float r2 = (float) r2     // Catch:{ Exception -> 0x129b }
            r25 = 0
            android.text.TextUtils$TruncateAt r26 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x129b }
            if (r40 == 0) goto L_0x1288
            r28 = 1
            goto L_0x128a
        L_0x1288:
            r28 = 2
        L_0x128a:
            r19 = r0
            r20 = r9
            r21 = r4
            r24 = r2
            r27 = r4
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ Exception -> 0x129b }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x129b }
            goto L_0x129f
        L_0x129b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x129f:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x13cb
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1355
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1355
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r2 = r1.nameLayout
            float r2 = r2.getLineWidth(r3)
            double r7 = (double) r2
            double r7 = java.lang.Math.ceil(r7)
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x12ee
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x12ee
            boolean r2 = r1.drawScam
            if (r2 != 0) goto L_0x12ee
            int r2 = r1.nameLeft
            double r9 = (double) r2
            double r11 = (double) r5
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r2.getIntrinsicWidth()
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            int r2 = (int) r9
            r1.nameMuteLeft = r2
            goto L_0x133d
        L_0x12ee:
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x1316
            int r2 = r1.nameLeft
            double r9 = (double) r2
            double r11 = (double) r5
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r2.getIntrinsicWidth()
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            int r2 = (int) r9
            r1.nameMuteLeft = r2
            goto L_0x133d
        L_0x1316:
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x133d
            int r2 = r1.nameLeft
            double r9 = (double) r2
            double r11 = (double) r5
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r2 = r2.getIntrinsicWidth()
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r9 = r9 - r11
            int r2 = (int) r9
            r1.nameMuteLeft = r2
        L_0x133d:
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x1355
            double r5 = (double) r5
            int r0 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1355
            int r0 = r1.nameLeft
            double r9 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r5
            int r0 = (int) r9
            r1.nameLeft = r0
        L_0x1355:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1396
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1396
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r5 = 2147483647(0x7fffffff, float:NaN)
        L_0x1366:
            if (r2 >= r0) goto L_0x138c
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineLeft(r2)
            r7 = 0
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x138b
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineWidth(r2)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r6
            int r6 = (int) r8
            int r5 = java.lang.Math.min(r5, r6)
            int r2 = r2 + 1
            goto L_0x1366
        L_0x138b:
            r5 = 0
        L_0x138c:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r5 == r0) goto L_0x1396
            int r0 = r1.messageLeft
            int r0 = r0 + r5
            r1.messageLeft = r0
        L_0x1396:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1453
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1453
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineLeft(r3)
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x1453
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r3)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r4
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x1453
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x1453
        L_0x13cb:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1417
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1417
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r3)
            float r2 = (float) r5
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x13fe
            android.text.StaticLayout r2 = r1.nameLayout
            float r2 = r2.getLineWidth(r3)
            double r7 = (double) r2
            double r7 = java.lang.Math.ceil(r7)
            double r4 = (double) r5
            int r2 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x13fe
            int r2 = r1.nameLeft
            double r9 = (double) r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            int r2 = (int) r9
            r1.nameLeft = r2
        L_0x13fe:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x140a
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x140a
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x1417
        L_0x140a:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1417:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x143c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x143c
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r2 = 0
            r4 = 1325400064(0x4var_, float:2.14748365E9)
        L_0x1426:
            if (r2 >= r0) goto L_0x1435
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r2)
            float r4 = java.lang.Math.min(r4, r5)
            int r2 = r2 + 1
            goto L_0x1426
        L_0x1435:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r4
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x143c:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1453
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1453
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1453:
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
        ArrayList<TLRPC.Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, z);
        if (this.index < dialogsArray.size()) {
            TLRPC.Dialog dialog = dialogsArray.get(this.index);
            boolean z2 = true;
            TLRPC.Dialog dialog2 = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
            TLRPC.DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            if (this.currentDialogFolderId != 0) {
                messageObject = findFolderTopMessage();
            } else {
                messageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
            }
            if (this.currentDialogId != dialog.id || (((messageObject2 = this.message) != null && messageObject2.getId() != dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.mentionCount != dialog.unread_mentions_count || this.markUnread != dialog.unread_mark || (messageObject3 = this.message) != messageObject || ((messageObject3 == null && messageObject != null) || draft != this.draftMessage || this.drawPin != dialog.pinned)))) {
                boolean z3 = this.currentDialogId != dialog.id;
                this.currentDialogId = dialog.id;
                boolean z4 = dialog instanceof TLRPC.TL_dialogFolder;
                if (z4) {
                    this.currentDialogFolderId = ((TLRPC.TL_dialogFolder) dialog).folder.id;
                } else {
                    this.currentDialogFolderId = 0;
                }
                this.fullSeparator = (dialog instanceof TLRPC.TL_dialog) && dialog.pinned && dialog2 != null && !dialog2.pinned;
                if (!z4 || dialog2 == null || dialog2.pinned) {
                    z2 = false;
                }
                this.fullSeparator2 = z2;
                update(0);
                if (z3) {
                    this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
                }
                checkOnline();
            }
        }
    }

    public void animateArchiveAvatar() {
        if (this.avatarDrawable.getAvatarType() == 3) {
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
        ArrayList<TLRPC.Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject messageObject = null;
        if (!dialogsArray.isEmpty()) {
            int size = dialogsArray.size();
            for (int i = 0; i < size; i++) {
                TLRPC.Dialog dialog = dialogsArray.get(i);
                MessageObject messageObject2 = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (messageObject2 != null && (messageObject == null || messageObject2.messageOwner.date > messageObject.messageOwner.date)) {
                    messageObject = messageObject2;
                }
                if (dialog.pinnedNum == 0 && messageObject != null) {
                    break;
                }
            }
        }
        return messageObject;
    }

    public boolean isFolderCell() {
        return this.currentDialogFolderId != 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:115:0x01be  */
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
            goto L_0x0318
        L_0x003c:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x00c2
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC.Dialog) r2
            if (r2 == 0) goto L_0x00b7
            if (r1 != 0) goto L_0x00c4
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
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0080
            boolean r6 = r6.isUnread()
            if (r6 == 0) goto L_0x0080
            r6 = 1
            goto L_0x0081
        L_0x0080:
            r6 = 0
        L_0x0081:
            r0.lastUnreadState = r6
            int r6 = r2.unread_count
            r0.unreadCount = r6
            boolean r6 = r2.unread_mark
            r0.markUnread = r6
            int r6 = r2.unread_mentions_count
            r0.mentionCount = r6
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0098
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.edit_date
            goto L_0x0099
        L_0x0098:
            r6 = 0
        L_0x0099:
            r0.currentEditDate = r6
            int r6 = r2.last_message_date
            r0.lastMessageDate = r6
            int r6 = r0.currentDialogFolderId
            if (r6 != 0) goto L_0x00a9
            boolean r2 = r2.pinned
            if (r2 == 0) goto L_0x00a9
            r2 = 1
            goto L_0x00aa
        L_0x00a9:
            r2 = 0
        L_0x00aa:
            r0.drawPin = r2
            org.telegram.messenger.MessageObject r2 = r0.message
            if (r2 == 0) goto L_0x00c4
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            int r2 = r2.send_state
            r0.lastSendState = r2
            goto L_0x00c4
        L_0x00b7:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x00c4
        L_0x00c2:
            r0.drawPin = r5
        L_0x00c4:
            if (r1 == 0) goto L_0x01c2
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x00e5
            r2 = r1 & 4
            if (r2 == 0) goto L_0x00e5
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            int r6 = r6.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            r19.invalidate()
        L_0x00e5:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0117
            r2 = r1 & 64
            if (r2 == 0) goto L_0x0117
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<java.lang.CharSequence> r2 = r2.printingStrings
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0103
            if (r2 == 0) goto L_0x0115
        L_0x0103:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 != 0) goto L_0x0109
            if (r2 != 0) goto L_0x0115
        L_0x0109:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0117
            if (r2 == 0) goto L_0x0117
            boolean r2 = r6.equals(r2)
            if (r2 != 0) goto L_0x0117
        L_0x0115:
            r2 = 1
            goto L_0x0118
        L_0x0117:
            r2 = 0
        L_0x0118:
            if (r2 != 0) goto L_0x012b
            r6 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r1
            if (r6 == 0) goto L_0x012b
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x012b
            java.lang.CharSequence r6 = r6.messageText
            java.lang.CharSequence r7 = r0.lastMessageString
            if (r6 == r7) goto L_0x012b
            r2 = 1
        L_0x012b:
            if (r2 != 0) goto L_0x0136
            r6 = r1 & 2
            if (r6 == 0) goto L_0x0136
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0136
            r2 = 1
        L_0x0136:
            if (r2 != 0) goto L_0x0141
            r6 = r1 & 1
            if (r6 == 0) goto L_0x0141
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0141
            r2 = 1
        L_0x0141:
            if (r2 != 0) goto L_0x014c
            r6 = r1 & 8
            if (r6 == 0) goto L_0x014c
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x014c
            r2 = 1
        L_0x014c:
            if (r2 != 0) goto L_0x0157
            r6 = r1 & 16
            if (r6 == 0) goto L_0x0157
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x0157
            r2 = 1
        L_0x0157:
            if (r2 != 0) goto L_0x01a7
            r6 = r1 & 256(0x100, float:3.59E-43)
            if (r6 == 0) goto L_0x01a7
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0172
            boolean r7 = r0.lastUnreadState
            boolean r6 = r6.isUnread()
            if (r7 == r6) goto L_0x0172
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isUnread()
            r0.lastUnreadState = r2
            r2 = 1
        L_0x0172:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x01a7
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC.Dialog) r6
            if (r6 == 0) goto L_0x01a7
            int r7 = r0.unreadCount
            int r8 = r6.unread_count
            if (r7 != r8) goto L_0x019a
            boolean r7 = r0.markUnread
            boolean r8 = r6.unread_mark
            if (r7 != r8) goto L_0x019a
            int r7 = r0.mentionCount
            int r8 = r6.unread_mentions_count
            if (r7 == r8) goto L_0x01a7
        L_0x019a:
            int r2 = r6.unread_count
            r0.unreadCount = r2
            int r2 = r6.unread_mentions_count
            r0.mentionCount = r2
            boolean r2 = r6.unread_mark
            r0.markUnread = r2
            r2 = 1
        L_0x01a7:
            if (r2 != 0) goto L_0x01bc
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x01bc
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x01bc
            int r6 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r6 == r1) goto L_0x01bc
            r0.lastSendState = r1
            r2 = 1
        L_0x01bc:
            if (r2 != 0) goto L_0x01c2
            r19.invalidate()
            return
        L_0x01c2:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r2 = 0
            if (r1 == 0) goto L_0x01e1
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r19.findFolderTopMessage()
            r0.message = r1
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x01df
            long r6 = r1.getDialogId()
            goto L_0x01fa
        L_0x01df:
            r6 = r2
            goto L_0x01fa
        L_0x01e1:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x01f5
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r6 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r6)
            if (r1 == 0) goto L_0x01f5
            r1 = 1
            goto L_0x01f6
        L_0x01f5:
            r1 = 0
        L_0x01f6:
            r0.dialogMuted = r1
            long r6 = r0.currentDialogId
        L_0x01fa:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x029f
            int r1 = (int) r6
            r2 = 32
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x024f
            if (r1 >= 0) goto L_0x023e
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            r0.chat = r1
            boolean r1 = r0.isDialogCell
            if (r1 != 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x0277
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0277
            r0.chat = r1
            goto L_0x0277
        L_0x023e:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            r0.user = r1
            goto L_0x0277
        L_0x024f:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
            r0.encryptedChat = r1
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r0.encryptedChat
            if (r1 == 0) goto L_0x0277
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.encryptedChat
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x0277:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x029f
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x029f
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.clientUserId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x029f:
            int r1 = r0.currentDialogFolderId
            if (r1 == 0) goto L_0x02bc
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable
            r1.setCallback(r0)
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 3
            r1.setAvatarType(r2)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r4 = 0
            r5 = 0
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            r9 = 0
            r3.setImage(r4, r5, r6, r7, r8, r9)
            goto L_0x0318
        L_0x02bc:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x02fc
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC.User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x02e4
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x02e4
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
            goto L_0x0318
        L_0x02e4:
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
            goto L_0x0318
        L_0x02fc:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x0318
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC.Chat) r1)
            org.telegram.messenger.ImageReceiver r6 = r0.avatarImage
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForChat(r1, r5)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r10 = 0
            org.telegram.tgnet.TLRPC$Chat r11 = r0.chat
            r12 = 0
            java.lang.String r8 = "50_50"
            r6.setImage(r7, r8, r9, r10, r11, r12)
        L_0x0318:
            int r1 = r19.getMeasuredWidth()
            if (r1 != 0) goto L_0x0329
            int r1 = r19.getMeasuredHeight()
            if (r1 == 0) goto L_0x0325
            goto L_0x0329
        L_0x0325:
            r19.requestLayout()
            goto L_0x032c
        L_0x0329:
            r19.buildLayout()
        L_0x032c:
            r19.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.update(int):void");
    }

    public float getTranslationX() {
        return this.translationX;
    }

    public void setTranslationX(float f) {
        this.translationX = (float) ((int) f);
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        boolean z = false;
        if (rLottieDrawable != null && this.translationX == 0.0f) {
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
            if (z2 != this.drawRevealBackground && this.archiveHidden == SharedConfig.archiveHidden) {
                try {
                    performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
            }
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x052b  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05dd  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0609  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0698  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0871  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x088c  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x099c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09ff  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a83  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0aaf  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:460:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r1 = r23
            r8 = r24
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
            r4 = 17
        L_0x003a:
            r10 = r4
            r1.lastUpdateTime = r2
            float r0 = r1.clipProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0069
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r0 == r2) goto L_0x0069
            r24.save()
            int r0 = r1.topClip
            float r0 = (float) r0
            float r2 = r1.clipProgress
            float r0 = r0 * r2
            int r2 = r23.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r23.getMeasuredHeight()
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
            r12 = 2
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 0
            r15 = 1
            r7 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0098
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x007d
            goto L_0x0098
        L_0x007d:
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            if (r0 == 0) goto L_0x0094
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            r0.setProgress(r9)
            org.telegram.ui.Components.RLottieDrawable r0 = r1.translationDrawable
            r2 = 0
            r0.setCallback(r2)
            r0 = 0
            r1.translationDrawable = r0
            r1.translationAnimationStarted = r14
        L_0x0094:
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x028e
        L_0x0098:
            r24.save()
            int r0 = r1.currentDialogFolderId
            java.lang.String r16 = "chats_archiveBackground"
            java.lang.String r17 = "chats_archivePinBackground"
            if (r0 == 0) goto L_0x00d3
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x00bd
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x0102
        L_0x00bd:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131625330(0x7f0e0572, float:1.8877865E38)
            java.lang.String r4 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x0102
        L_0x00d3:
            int r0 = r1.folderId
            if (r0 != 0) goto L_0x00ed
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131624199(0x7f0e0107, float:1.887557E38)
            java.lang.String r4 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r1.translationDrawable = r4
            goto L_0x0102
        L_0x00ed:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            java.lang.String r4 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r1.translationDrawable = r4
        L_0x0102:
            r6 = r2
            r5 = r3
            boolean r2 = r1.translationAnimationStarted
            r18 = 1110179840(0x422CLASSNAME, float:43.0)
            if (r2 != 0) goto L_0x012a
            float r2 = r1.translationX
            float r2 = java.lang.Math.abs(r2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x012a
            r1.translationAnimationStarted = r15
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setProgress(r9)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setCallback(r1)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.start()
        L_0x012a:
            int r2 = r23.getMeasuredWidth()
            float r2 = (float) r2
            float r3 = r1.translationX
            float r4 = r2 + r3
            float r2 = r1.currentRevealProgress
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x017b
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r0 = (float) r0
            float r3 = r4 - r0
            r0 = 0
            int r2 = r23.getMeasuredWidth()
            float r2 = (float) r2
            int r7 = r23.getMeasuredHeight()
            float r7 = (float) r7
            android.graphics.Paint r20 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r21 = r2
            r2 = r24
            r15 = r4
            r4 = r0
            r0 = r5
            r5 = r21
            r22 = r6
            r6 = r7
            r7 = r20
            r2.drawRect(r3, r4, r5, r6, r7)
            float r2 = r1.currentRevealProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x017f
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r2 == 0) goto L_0x017f
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r4 = "Arrow.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14
            goto L_0x017f
        L_0x017b:
            r15 = r4
            r0 = r5
            r22 = r6
        L_0x017f:
            int r2 = r23.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 - r3
            org.telegram.ui.Components.RLottieDrawable r3 = r1.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r12
            int r2 = r2 - r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x019c
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0199
            goto L_0x019c
        L_0x0199:
            r3 = 1091567616(0x41100000, float:9.0)
            goto L_0x019e
        L_0x019c:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x019e:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Components.RLottieDrawable r4 = r1.translationDrawable
            int r4 = r4.getIntrinsicWidth()
            int r4 = r4 / r12
            int r4 = r4 + r2
            org.telegram.ui.Components.RLottieDrawable r5 = r1.translationDrawable
            int r5 = r5.getIntrinsicHeight()
            int r5 = r5 / r12
            int r5 = r5 + r3
            float r6 = r1.currentRevealProgress
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0213
            r24.save()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r6 = (float) r6
            float r6 = r15 - r6
            int r7 = r23.getMeasuredWidth()
            float r7 = (float) r7
            int r13 = r23.getMeasuredHeight()
            float r13 = (float) r13
            r8.clipRect(r6, r9, r7, r13)
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7 = r22
            r6.setColor(r7)
            int r6 = r4 * r4
            int r7 = r23.getMeasuredHeight()
            int r7 = r5 - r7
            int r13 = r23.getMeasuredHeight()
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
            r24.restore()
            boolean r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r4 != 0) goto L_0x0213
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r6 = "Arrow.**"
            r4.setLayerColor(r6, r5)
            r4 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r4
        L_0x0213:
            r24.save()
            float r2 = (float) r2
            float r3 = (float) r3
            r8.translate(r2, r3)
            float r2 = r1.currentRevealBounceProgress
            int r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            r13 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x0241
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0241
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r3 = r1.interpolator
            float r2 = r3.getInterpolation(r2)
            float r2 = r2 + r13
            org.telegram.ui.Components.RLottieDrawable r3 = r1.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r12
            float r3 = (float) r3
            org.telegram.ui.Components.RLottieDrawable r4 = r1.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r12
            float r4 = (float) r4
            r8.scale(r2, r2, r3, r4)
        L_0x0241:
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r14, (int) r14)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.draw(r8)
            r24.restore()
            int r2 = r23.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r23.getMeasuredHeight()
            float r3 = (float) r3
            r8.clipRect(r15, r9, r2, r3)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r2.measureText(r0)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r3 = r23.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r3 - r4
            int r2 = r2 / r12
            int r3 = r3 - r2
            float r2 = (float) r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x027f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x027c
            goto L_0x027f
        L_0x027c:
            r3 = 1114374144(0x426CLASSNAME, float:59.0)
            goto L_0x0281
        L_0x027f:
            r3 = 1115160576(0x42780000, float:62.0)
        L_0x0281:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r8.drawText(r0, r2, r3, r4)
            r24.restore()
        L_0x028e:
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x029c
            r24.save()
            float r0 = r1.translationX
            r8.translate(r0, r9)
        L_0x029c:
            boolean r0 = r1.isSelected
            if (r0 == 0) goto L_0x02b3
            r3 = 0
            r4 = 0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x02b3:
            int r0 = r1.currentDialogFolderId
            java.lang.String r15 = "chats_pinnedOverlay"
            if (r0 == 0) goto L_0x02e6
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x02c3
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x02e6
        L_0x02c3:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            float r3 = r1.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r14, r2, r3, r13)
            r0.setColor(r2)
            r3 = 0
            r4 = 0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
            goto L_0x030a
        L_0x02e6:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x02ee
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x030a
        L_0x02ee:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setColor(r2)
            r3 = 0
            r4 = 0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x030a:
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x031b
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0317
            goto L_0x031b
        L_0x0317:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x03cc
        L_0x031b:
            r24.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColor(r2)
            android.graphics.RectF r0 = r1.rect
            int r2 = r23.getMeasuredWidth()
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r23.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r23.getMeasuredHeight()
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
            if (r0 == 0) goto L_0x0399
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x036e
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0399
        L_0x036e:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            float r3 = r1.archiveBackgroundProgress
            int r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r14, r2, r3, r13)
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
            goto L_0x03a2
        L_0x0399:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x03a5
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x03a2
            goto L_0x03a5
        L_0x03a2:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x03c9
        L_0x03a5:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
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
        L_0x03c9:
            r24.restore()
        L_0x03cc:
            float r0 = r1.translationX
            r3 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x03e8
            float r0 = r1.cornerProgress
            int r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x03fd
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 + r4
            r1.cornerProgress = r0
            float r0 = r1.cornerProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x03fb
            r1.cornerProgress = r13
            goto L_0x03fb
        L_0x03e8:
            float r0 = r1.cornerProgress
            int r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x03fd
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 - r4
            r1.cornerProgress = r0
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x03fb
            r1.cornerProgress = r9
        L_0x03fb:
            r4 = 1
            goto L_0x03fe
        L_0x03fd:
            r4 = 0
        L_0x03fe:
            boolean r0 = r1.drawNameLock
            if (r0 == 0) goto L_0x0411
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r5 = r1.nameLockLeft
            int r6 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r8)
            goto L_0x0449
        L_0x0411:
            boolean r0 = r1.drawNameGroup
            if (r0 == 0) goto L_0x0424
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r5 = r1.nameLockLeft
            int r6 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r8)
            goto L_0x0449
        L_0x0424:
            boolean r0 = r1.drawNameBroadcast
            if (r0 == 0) goto L_0x0437
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r5 = r1.nameLockLeft
            int r6 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r8)
            goto L_0x0449
        L_0x0437:
            boolean r0 = r1.drawNameBot
            if (r0 == 0) goto L_0x0449
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r5 = r1.nameLockLeft
            int r6 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r8)
        L_0x0449:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x04bd
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0465
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0499
        L_0x0465:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 != 0) goto L_0x0486
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 == 0) goto L_0x0472
            int r0 = r0.type
            if (r0 != r12) goto L_0x0472
            goto L_0x0486
        L_0x0472:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_name"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0499
        L_0x0486:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_secretName"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x0499:
            r24.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x04ab
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x04a8
            goto L_0x04ab
        L_0x04a8:
            r5 = 1095761920(0x41500000, float:13.0)
            goto L_0x04ad
        L_0x04ab:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x04ad:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r8)
            r24.restore()
        L_0x04bd:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x04d9
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x04d9
            r24.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r5 = r1.timeTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r8)
            r24.restore()
        L_0x04d9:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x0527
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x04ef
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessageArchived_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x050e
        L_0x04ef:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0501
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_draft"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x050e
        L_0x0501:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessage_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
        L_0x050e:
            r24.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r5 = r1.messageNameTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x0520 }
            r0.draw(r8)     // Catch:{ Exception -> 0x0520 }
            goto L_0x0524
        L_0x0520:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0524:
            r24.restore()
        L_0x0527:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0587
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x055b
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0547
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameMessageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x056e
        L_0x0547:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_messageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x056e
        L_0x055b:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_message"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x056e:
            r24.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r5 = r1.messageTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x0580 }
            r0.draw(r8)     // Catch:{ Exception -> 0x0580 }
            goto L_0x0584
        L_0x0580:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0584:
            r24.restore()
        L_0x0587:
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x05d1
            boolean r0 = r1.drawClock
            if (r0 == 0) goto L_0x059e
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            r0.draw(r8)
            goto L_0x05d1
        L_0x059e:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x05d1
            boolean r0 = r1.drawCheck1
            if (r0 == 0) goto L_0x05c3
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
            goto L_0x05d1
        L_0x05c3:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            r0.draw(r8)
        L_0x05d1:
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x0609
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x0609
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x0609
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x05ed
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x05ea
            goto L_0x05ed
        L_0x05ea:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x05ee
        L_0x05ed:
            r6 = 0
        L_0x05ee:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x05fa
            r6 = 1096286208(0x41580000, float:13.5)
            goto L_0x05fc
        L_0x05fa:
            r6 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x05fc:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r8)
            goto L_0x066c
        L_0x0609:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x064a
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x061d
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x061a
            goto L_0x061d
        L_0x061a:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x061f
        L_0x061d:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x061f:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x0636
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0633
            goto L_0x0636
        L_0x0633:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x0638
        L_0x0636:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x0638:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r8)
            goto L_0x066c
        L_0x064a:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x066c
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x065e
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x065b
            goto L_0x065e
        L_0x065b:
            r6 = 1097859072(0x41700000, float:15.0)
            goto L_0x0660
        L_0x065e:
            r6 = 1094713344(0x41400000, float:12.0)
        L_0x0660:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r8)
        L_0x066c:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x0678
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0690
        L_0x0678:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            float r6 = r1.reorderIconProgress
            float r6 = r6 * r5
            int r6 = (int) r6
            r0.setAlpha(r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            int r6 = r1.pinLeft
            int r7 = r1.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r6, (int) r7)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            r0.draw(r8)
        L_0x0690:
            boolean r0 = r1.drawError
            r6 = 1102577664(0x41b80000, float:23.0)
            r7 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x06e9
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r12 = r1.reorderIconProgress
            float r12 = r13 - r12
            float r12 = r12 * r5
            int r5 = (int) r12
            r0.setAlpha(r5)
            android.graphics.RectF r0 = r1.rect
            int r5 = r1.errorLeft
            float r12 = (float) r5
            int r15 = r1.errorTop
            float r15 = (float) r15
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r16
            float r5 = (float) r5
            int r2 = r1.errorTop
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r6
            float r2 = (float) r2
            r0.set(r12, r15, r5, r2)
            android.graphics.RectF r0 = r1.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r2 * r7
            float r2 = r2 * r7
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r8.drawRoundRect(r0, r5, r2, r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r2 = r1.errorLeft
            r5 = 1085276160(0x40b00000, float:5.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 + r5
            int r5 = r1.errorTop
            r6 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r8)
            goto L_0x0838
        L_0x06e9:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x0712
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x06f2
            goto L_0x0712
        L_0x06f2:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x0838
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r1.reorderIconProgress
            float r7 = r13 - r2
            float r7 = r7 * r5
            int r2 = (int) r7
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r2 = r1.pinLeft
            int r5 = r1.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r8)
            goto L_0x0838
        L_0x0712:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x078a
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0722
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x071f
            goto L_0x0722
        L_0x071f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0724
        L_0x0722:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0724:
            float r2 = r1.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r5
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r12 = r1.reorderIconProgress
            float r12 = r13 - r12
            float r12 = r12 * r5
            int r12 = (int) r12
            r2.setAlpha(r12)
            int r2 = r1.countLeft
            r12 = 1085276160(0x40b00000, float:5.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r2 = r2 - r12
            android.graphics.RectF r12 = r1.rect
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
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 + r19
            float r3 = (float) r3
            r12.set(r15, r14, r2, r3)
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r12 = r3 * r7
            float r3 = r3 * r7
            r8.drawRoundRect(r2, r12, r3, r0)
            android.text.StaticLayout r0 = r1.countLayout
            if (r0 == 0) goto L_0x078a
            r24.save()
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
            r24.restore()
        L_0x078a:
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x0838
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r2 = r1.reorderIconProgress
            float r2 = r13 - r2
            float r2 = r2 * r5
            int r2 = (int) r2
            r0.setAlpha(r2)
            int r0 = r1.mentionLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.graphics.RectF r2 = r1.rect
            float r3 = (float) r0
            int r12 = r1.countTop
            float r12 = (float) r12
            int r14 = r1.mentionWidth
            int r0 = r0 + r14
            r14 = 1093664768(0x41300000, float:11.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 + r14
            float r0 = (float) r0
            int r14 = r1.countTop
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r14 = r14 + r6
            float r6 = (float) r14
            r2.set(r3, r12, r0, r6)
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x07ca
            int r0 = r1.folderId
            if (r0 == 0) goto L_0x07ca
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x07cc
        L_0x07ca:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x07cc:
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r6 = r3 * r7
            float r3 = r3 * r7
            r8.drawRoundRect(r2, r6, r3, r0)
            android.text.StaticLayout r0 = r1.mentionLayout
            if (r0 == 0) goto L_0x0803
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r1.reorderIconProgress
            float r7 = r13 - r2
            float r7 = r7 * r5
            int r2 = (int) r7
            r0.setAlpha(r2)
            r24.save()
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
            r24.restore()
            goto L_0x0838
        L_0x0803:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r2 = r1.reorderIconProgress
            float r7 = r13 - r2
            float r7 = r7 * r5
            int r2 = (int) r7
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r2 = r1.mentionLeft
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r3 = r1.countTop
            r5 = 1078774989(0x404ccccd, float:3.2)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r3, r5, r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r8)
        L_0x0838:
            boolean r0 = r1.animatingArchiveAvatar
            r12 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x085a
            r24.save()
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
        L_0x085a:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0868
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x0868
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x086d
        L_0x0868:
            org.telegram.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r8)
        L_0x086d:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0874
            r24.restore()
        L_0x0874:
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            if (r0 == 0) goto L_0x097d
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x097d
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x097d
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x097d
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x097d
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x08ba
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x08a2
            int r0 = r0.expires
            int r2 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x08b8
        L_0x08a2:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x08ba
        L_0x08b8:
            r0 = 1
            goto L_0x08bb
        L_0x08ba:
            r0 = 0
        L_0x08bb:
            if (r0 != 0) goto L_0x08c3
            float r2 = r1.onlineProgress
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x097d
        L_0x08c3:
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            int r2 = r2.getImageY2()
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x08d5
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x08d2
            goto L_0x08d5
        L_0x08d2:
            r16 = 1090519040(0x41000000, float:8.0)
            goto L_0x08d9
        L_0x08d5:
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x08d9:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x08fc
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            int r3 = r3.getImageX()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x08f4
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x08f1
            goto L_0x08f4
        L_0x08f1:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            goto L_0x08f6
        L_0x08f4:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x08f6:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r5
            goto L_0x0915
        L_0x08fc:
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            int r3 = r3.getImageX2()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x090e
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x090b
            goto L_0x090e
        L_0x090b:
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            goto L_0x0910
        L_0x090e:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x0910:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 - r5
        L_0x0915:
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r6 = "windowBackgroundWhite"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColor(r6)
            float r3 = (float) r3
            float r2 = (float) r2
            r5 = 1088421888(0x40e00000, float:7.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r1.onlineProgress
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r3, r2, r5, r6)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r6 = "chats_onlineCircle"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColor(r6)
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r1.onlineProgress
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r3, r2, r5, r6)
            if (r0 == 0) goto L_0x0966
            float r0 = r1.onlineProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x097d
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.onlineProgress = r0
            float r0 = r1.onlineProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x097b
            r1.onlineProgress = r13
            goto L_0x097b
        L_0x0966:
            float r0 = r1.onlineProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x097d
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.onlineProgress = r0
            float r0 = r1.onlineProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x097b
            r1.onlineProgress = r9
        L_0x097b:
            r15 = 1
            goto L_0x097e
        L_0x097d:
            r15 = r4
        L_0x097e:
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0987
            r24.restore()
        L_0x0987:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0998
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0998
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x0998
            r0.draw(r8)
        L_0x0998:
            boolean r0 = r1.useSeparator
            if (r0 == 0) goto L_0x09f8
            boolean r0 = r1.fullSeparator
            if (r0 != 0) goto L_0x09bc
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x09ac
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x09ac
            boolean r0 = r1.fullSeparator2
            if (r0 == 0) goto L_0x09bc
        L_0x09ac:
            boolean r0 = r1.fullSeparator2
            if (r0 == 0) goto L_0x09b5
            boolean r0 = r1.archiveHidden
            if (r0 != 0) goto L_0x09b5
            goto L_0x09bc
        L_0x09b5:
            r0 = 1116733440(0x42900000, float:72.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x09bd
        L_0x09bc:
            r14 = 0
        L_0x09bd:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x09dd
            r3 = 0
            int r0 = r23.getMeasuredHeight()
            r2 = 1
            int r0 = r0 - r2
            float r4 = (float) r0
            int r0 = r23.getMeasuredWidth()
            int r0 = r0 - r14
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            int r0 = r0 - r2
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r2 = r24
            r2.drawLine(r3, r4, r5, r6, r7)
            goto L_0x09f8
        L_0x09dd:
            float r3 = (float) r14
            int r0 = r23.getMeasuredHeight()
            r14 = 1
            int r0 = r0 - r14
            float r4 = (float) r0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            int r0 = r0 - r14
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r2 = r24
            r2.drawLine(r3, r4, r5, r6, r7)
            goto L_0x09f9
        L_0x09f8:
            r14 = 1
        L_0x09f9:
            float r0 = r1.clipProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0a49
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r0 == r2) goto L_0x0a09
            r24.restore()
            goto L_0x0a49
        L_0x0a09:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColor(r2)
            r3 = 0
            r4 = 0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r1.topClip
            float r0 = (float) r0
            float r2 = r1.clipProgress
            float r6 = r0 * r2
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
            int r0 = r23.getMeasuredHeight()
            int r2 = r1.bottomClip
            float r2 = (float) r2
            float r4 = r1.clipProgress
            float r2 = r2 * r4
            int r2 = (int) r2
            int r0 = r0 - r2
            float r4 = (float) r0
            int r0 = r23.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r23.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0a49:
            boolean r0 = r1.drawReorder
            if (r0 != 0) goto L_0x0a53
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0a7f
        L_0x0a53:
            boolean r0 = r1.drawReorder
            if (r0 == 0) goto L_0x0a6b
            float r0 = r1.reorderIconProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a7f
            float r2 = (float) r10
            float r2 = r2 / r12
            float r0 = r0 + r2
            r1.reorderIconProgress = r0
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a7e
            r1.reorderIconProgress = r13
            goto L_0x0a7e
        L_0x0a6b:
            float r0 = r1.reorderIconProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a7f
            float r2 = (float) r10
            float r2 = r2 / r12
            float r0 = r0 - r2
            r1.reorderIconProgress = r0
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a7e
            r1.reorderIconProgress = r9
        L_0x0a7e:
            r15 = 1
        L_0x0a7f:
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x0aaf
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0adb
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.archiveBackgroundProgress = r0
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a98
            r1.archiveBackgroundProgress = r9
        L_0x0a98:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            r2 = 3
            if (r0 != r2) goto L_0x0ada
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
            goto L_0x0ada
        L_0x0aaf:
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0adb
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.archiveBackgroundProgress = r0
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0ac4
            r1.archiveBackgroundProgress = r13
        L_0x0ac4:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            r2 = 3
            if (r0 != r2) goto L_0x0ada
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
        L_0x0ada:
            r15 = 1
        L_0x0adb:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0af1
            float r0 = r1.animatingArchiveAvatarProgress
            float r2 = (float) r10
            float r0 = r0 + r2
            r1.animatingArchiveAvatarProgress = r0
            float r0 = r1.animatingArchiveAvatarProgress
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 < 0) goto L_0x0af0
            r1.animatingArchiveAvatarProgress = r12
            r2 = 0
            r1.animatingArchiveAvatar = r2
        L_0x0af0:
            r15 = 1
        L_0x0af1:
            boolean r0 = r1.drawRevealBackground
            if (r0 == 0) goto L_0x0b1f
            float r0 = r1.currentRevealBounceProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b09
            float r2 = (float) r10
            float r2 = r2 / r12
            float r0 = r0 + r2
            r1.currentRevealBounceProgress = r0
            float r0 = r1.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b09
            r1.currentRevealBounceProgress = r13
            r15 = 1
        L_0x0b09:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b3e
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.currentRevealProgress = r0
            float r0 = r1.currentRevealProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b3f
            r1.currentRevealProgress = r13
            goto L_0x0b3f
        L_0x0b1f:
            float r0 = r1.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0b28
            r1.currentRevealBounceProgress = r9
            r15 = 1
        L_0x0b28:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b3e
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r1.currentRevealProgress = r0
            float r0 = r1.currentRevealProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b3f
            r1.currentRevealProgress = r9
            goto L_0x0b3f
        L_0x0b3e:
            r14 = r15
        L_0x0b3f:
            if (r14 == 0) goto L_0x0b44
            r23.invalidate()
        L_0x0b44:
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
                if (this.drawReorder) {
                    f = 0.0f;
                }
                this.reorderIconProgress = f;
            } else {
                if (!this.drawReorder) {
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
        TLRPC.User user2;
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
            TLRPC.User user3 = this.user;
            if (user3 != null) {
                if (user3.bot) {
                    sb.append(LocaleController.getString("Bot", NUM));
                    sb.append(". ");
                }
                TLRPC.User user4 = this.user;
                if (user4.self) {
                    sb.append(LocaleController.getString("SavedMessages", NUM));
                } else {
                    sb.append(ContactsController.formatName(user4.first_name, user4.last_name));
                }
                sb.append(". ");
            } else {
                TLRPC.Chat chat2 = this.chat;
                if (chat2 != null) {
                    if (chat2.broadcast) {
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
