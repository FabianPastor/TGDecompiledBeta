package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View.MeasureSpec;
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
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;
import org.telegram.tgnet.TLRPC.User;
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
    private Chat chat;
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
    private DraftMessage draftMessage;
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
    private EncryptedChat encryptedChat;
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
    private User user;

    public class BounceInterpolator implements Interpolator {
        public float getInterpolation(float f) {
            if (f < 0.33f) {
                return (f / 0.33f) * 0.1f;
            }
            f -= 0.33f;
            return f < 0.33f ? 0.1f - ((f / 0.34f) * 0.15f) : (((f - 0.34f) / 0.33f) * 0.05f) - 89.6f;
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
            this.checkBox = new CheckBox2(context, 21);
            this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox);
        }
    }

    public void setDialog(Dialog dialog, int i, int i2) {
        this.currentDialogId = dialog.id;
        this.isDialogCell = true;
        if (dialog instanceof TL_dialogFolder) {
            this.currentDialogFolderId = ((TL_dialogFolder) dialog).folder.id;
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

    public void setDialog(CustomDialog customDialog) {
        this.customDialog = customDialog;
        this.messageId = 0;
        update(0);
        checkOnline();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0035  */
    private void checkOnline() {
        /*
        r2 = this;
        r0 = r2.user;
        if (r0 == 0) goto L_0x0032;
    L_0x0004:
        r1 = r0.self;
        if (r1 != 0) goto L_0x0032;
    L_0x0008:
        r0 = r0.status;
        if (r0 == 0) goto L_0x001a;
    L_0x000c:
        r0 = r0.expires;
        r1 = r2.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r0 > r1) goto L_0x0030;
    L_0x001a:
        r0 = r2.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.onlinePrivacy;
        r1 = r2.user;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.containsKey(r1);
        if (r0 == 0) goto L_0x0032;
    L_0x0030:
        r0 = 1;
        goto L_0x0033;
    L_0x0032:
        r0 = 0;
    L_0x0033:
        if (r0 == 0) goto L_0x0038;
    L_0x0035:
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0039;
    L_0x0038:
        r0 = 0;
    L_0x0039:
        r2.onlineProgress = r0;
        return;
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
        boolean z2 = messageObject != null && messageObject.isUnread();
        this.lastUnreadState = z2;
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

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSliding = false;
        this.drawRevealBackground = false;
        this.currentRevealProgress = 0.0f;
        this.attachedToWindow = false;
        float f = (this.drawPin && this.drawReorder) ? 1.0f : 0.0f;
        this.reorderIconProgress = f;
        this.avatarImage.onDetachedFromWindow();
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
            this.translationDrawable.setProgress(0.0f);
            this.translationDrawable.setCallback(null);
            this.translationDrawable = null;
            this.translationAnimationStarted = false;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.archiveHidden = SharedConfig.archiveHidden;
        float f = 1.0f;
        this.archiveBackgroundProgress = this.archiveHidden ? 0.0f : 1.0f;
        this.avatarDrawable.setArchivedAvatarHiddenProgress(this.archiveBackgroundProgress);
        this.clipProgress = 0.0f;
        this.isSliding = false;
        if (!(this.drawPin && this.drawReorder)) {
            f = 0.0f;
        }
        this.reorderIconProgress = f;
        this.attachedToWindow = true;
        this.cornerProgress = 0.0f;
        setTranslationX(0.0f);
        setTranslationY(0.0f);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        i = MeasureSpec.getSize(i);
        float f = (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 78.0f : 72.0f;
        setMeasuredDimension(i, AndroidUtilities.dp(f) + this.useSeparator);
        this.topClip = 0;
        this.bottomClip = getMeasuredHeight();
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                i3 = LocaleController.isRTL ? (i3 - i) - AndroidUtilities.dp(45.0f) : AndroidUtilities.dp(45.0f);
                i = AndroidUtilities.dp(46.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(i3, i, checkBox2.getMeasuredWidth() + i3, this.checkBox.getMeasuredHeight() + i);
            }
            if (z) {
                try {
                    buildLayout();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public boolean isUnread() {
        return (this.unreadCount != 0 || this.markUnread) && !this.dialogMuted;
    }

    private CharSequence formatArchivedDialogNames() {
        ArrayList dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = dialogs.size();
        for (int i = 0; i < size; i++) {
            User user;
            String replace;
            Dialog dialog = (Dialog) dialogs.get(i);
            Chat chat = null;
            if (DialogObject.isSecretDialogId(dialog.id)) {
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog.id >> 32)));
                user = encryptedChat != null ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id)) : null;
            } else {
                int i2 = (int) dialog.id;
                if (i2 > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                } else {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                    user = null;
                }
            }
            if (chat != null) {
                replace = chat.title.replace(10, ' ');
            } else if (user == null) {
                continue;
            } else if (UserObject.isDeleted(user)) {
                replace = LocaleController.getString("HiddenName", NUM);
            } else {
                replace = ContactsController.formatName(user.first_name, user.last_name).replace(10, ' ');
            }
            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append(", ");
            }
            int length = spannableStringBuilder.length();
            int length2 = replace.length() + length;
            spannableStringBuilder.append(replace);
            if (dialog.unread_count > 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived")), length, length2, 33);
            }
            if (spannableStringBuilder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:489:0x0ada  */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0ada  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0bc5  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0ba9  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0b9f  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0b9f  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0ba9  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0bc5  */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x0af0  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0ae8  */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0b0d  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1352  */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x1224  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x11d8 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x1209 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1206 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x1224  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1352  */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x0d7f  */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0d53  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e3c  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e26  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0fe3  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0fb5  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x110a  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x11b3 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x11d8 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1206 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x1209 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1352  */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x1224  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0341  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0cdb  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0c9c  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0ce5  */
    /* JADX WARNING: Removed duplicated region for block: B:603:0x0d1c  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0d53  */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x0d7f  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0e04  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e26  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e3c  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0e92  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0fb5  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0fe3  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x110a  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x114d  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x11a6 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x11b3 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x11bd A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x11d8 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x1209 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1206 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x1224  */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1352  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0341  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0c9c  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0cdb  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0ce5  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:603:0x0d1c  */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x0d7f  */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0d53  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0e04  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e3c  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e26  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0e92  */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0fe3  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0fb5  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x110a  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x114d  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x11a6 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x11b3 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x11bd A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x11d8 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x1206 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x1209 A:{Catch:{ Exception -> 0x121c }} */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x1352  */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x1224  */
    /* JADX WARNING: Missing block: B:259:0x05ea, code skipped:
            if (r6.post_messages != false) goto L_0x05ec;
     */
    public void buildLayout() {
        /*
        r33 = this;
        r1 = r33;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0049;
    L_0x0006:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x000b;
    L_0x000a:
        goto L_0x0049;
    L_0x000b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r2 = "chats_message";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.linkColor = r2;
        r0.setColor(r2);
        goto L_0x0086;
    L_0x0049:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r2 = "chats_message_threeLines";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.linkColor = r2;
        r0.setColor(r2);
    L_0x0086:
        r2 = 0;
        r1.currentDialogFolderDialogsCount = r2;
        r0 = r1.isDialogCell;
        if (r0 == 0) goto L_0x009e;
    L_0x008d:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.printingStrings;
        r4 = r1.currentDialogId;
        r0 = r0.get(r4);
        r0 = (java.lang.CharSequence) r0;
        goto L_0x009f;
    L_0x009e:
        r0 = 0;
    L_0x009f:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r1.drawNameGroup = r2;
        r1.drawNameBroadcast = r2;
        r1.drawNameLock = r2;
        r1.drawNameBot = r2;
        r1.drawVerified = r2;
        r1.drawScam = r2;
        r1.drawPinBackground = r2;
        r5 = r1.user;
        r5 = org.telegram.messenger.UserObject.isUserSelf(r5);
        r6 = 1;
        if (r5 != 0) goto L_0x00be;
    L_0x00b8:
        r5 = r1.useMeForMyMessages;
        if (r5 != 0) goto L_0x00be;
    L_0x00bc:
        r5 = 1;
        goto L_0x00bf;
    L_0x00be:
        r5 = 0;
    L_0x00bf:
        r7 = android.os.Build.VERSION.SDK_INT;
        r8 = 18;
        if (r7 < r8) goto L_0x00d8;
    L_0x00c5:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x00cd;
    L_0x00c9:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x00d1;
    L_0x00cd:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x00d4;
    L_0x00d1:
        r7 = "%2$s: ⁨%1$s⁩";
        goto L_0x00e6;
    L_0x00d4:
        r7 = "⁨%s⁩";
        goto L_0x00ea;
    L_0x00d8:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x00e0;
    L_0x00dc:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x00e4;
    L_0x00e0:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x00e8;
    L_0x00e4:
        r7 = "%2$s: %1$s";
    L_0x00e6:
        r8 = 1;
        goto L_0x00eb;
    L_0x00e8:
        r7 = "%1$s";
    L_0x00ea:
        r8 = 0;
    L_0x00eb:
        r9 = r1.message;
        if (r9 == 0) goto L_0x00f2;
    L_0x00ef:
        r9 = r9.messageText;
        goto L_0x00f3;
    L_0x00f2:
        r9 = 0;
    L_0x00f3:
        r1.lastMessageString = r9;
        r9 = r1.customDialog;
        r10 = 32;
        r11 = 10;
        r13 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r14 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r15 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r16 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r17 = NUM; // 0x429CLASSNAME float:78.0 double:5.521281773E-315;
        r3 = 2;
        r12 = "";
        if (r9 == 0) goto L_0x0341;
    L_0x010a:
        r0 = r9.type;
        if (r0 != r3) goto L_0x0193;
    L_0x010e:
        r1.drawNameLock = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0156;
    L_0x0114:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0119;
    L_0x0118:
        goto L_0x0156;
    L_0x0119:
        r0 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x013c;
    L_0x0125:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x013c:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0156:
        r0 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0179;
    L_0x0162:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0179:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0193:
        r5 = r9.verified;
        r1.drawVerified = r5;
        r5 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r5 == 0) goto L_0x023c;
    L_0x019b:
        if (r0 != r6) goto L_0x023c;
    L_0x019d:
        r1.drawNameGroup = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x01f3;
    L_0x01a3:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x01a8;
    L_0x01a7:
        goto L_0x01f3;
    L_0x01a8:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01d2;
    L_0x01ac:
        r0 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x01c7;
    L_0x01c4:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01c9;
    L_0x01c7:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01c9:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x01d2:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r5;
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x01e2;
    L_0x01df:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01e4;
    L_0x01e2:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01e4:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x01f3:
        r0 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x021c;
    L_0x01ff:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x0212;
    L_0x020f:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0214;
    L_0x0212:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0214:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x021c:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r5;
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x022c;
    L_0x0229:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x022e;
    L_0x022c:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x022e:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x023c:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0257;
    L_0x0240:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0245;
    L_0x0244:
        goto L_0x0257;
    L_0x0245:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0250;
    L_0x0249:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0250:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0257:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0262;
    L_0x025b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x0268;
    L_0x0262:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
    L_0x0268:
        r0 = r1.customDialog;
        r5 = r0.type;
        if (r5 != r6) goto L_0x02ee;
    L_0x026e:
        r0 = NUM; // 0x7f0e04de float:1.8877565E38 double:1.053162772E-314;
        r5 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r5 = r1.customDialog;
        r8 = r5.isMedia;
        if (r8 == 0) goto L_0x02a4;
    L_0x027d:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r5 = new java.lang.Object[r6];
        r8 = r1.message;
        r8 = r8.messageText;
        r5[r2] = r8;
        r5 = java.lang.String.format(r7, r5);
        r5 = android.text.SpannableStringBuilder.valueOf(r5);
        r7 = new android.text.style.ForegroundColorSpan;
        r8 = "chats_attachMessage";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.<init>(r8);
        r8 = r5.length();
        r9 = 33;
        r5.setSpan(r7, r2, r8, r9);
        goto L_0x02da;
    L_0x02a4:
        r5 = r5.message;
        r8 = r5.length();
        if (r8 <= r14) goto L_0x02b0;
    L_0x02ac:
        r5 = r5.substring(r2, r14);
    L_0x02b0:
        r8 = r1.useForceThreeLines;
        if (r8 != 0) goto L_0x02cc;
    L_0x02b4:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x02b9;
    L_0x02b8:
        goto L_0x02cc;
    L_0x02b9:
        r8 = new java.lang.Object[r3];
        r5 = r5.replace(r11, r10);
        r8[r2] = r5;
        r8[r6] = r0;
        r5 = java.lang.String.format(r7, r8);
        r5 = android.text.SpannableStringBuilder.valueOf(r5);
        goto L_0x02da;
    L_0x02cc:
        r8 = new java.lang.Object[r3];
        r8[r2] = r5;
        r8[r6] = r0;
        r5 = java.lang.String.format(r7, r8);
        r5 = android.text.SpannableStringBuilder.valueOf(r5);
    L_0x02da:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r7 = r7.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r5 = org.telegram.messenger.Emoji.replaceEmoji(r5, r7, r9, r2);
        r7 = r4;
        r4 = r0;
        r0 = 0;
        goto L_0x02f9;
    L_0x02ee:
        r5 = r0.message;
        r0 = r0.isMedia;
        if (r0 == 0) goto L_0x02f6;
    L_0x02f4:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x02f6:
        r7 = r4;
        r0 = 1;
        r4 = 0;
    L_0x02f9:
        r8 = r1.customDialog;
        r8 = r8.date;
        r8 = (long) r8;
        r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8);
        r9 = r1.customDialog;
        r9 = r9.unread_count;
        if (r9 == 0) goto L_0x0319;
    L_0x0308:
        r1.drawCount = r6;
        r10 = new java.lang.Object[r6];
        r9 = java.lang.Integer.valueOf(r9);
        r10[r2] = r9;
        r9 = "%d";
        r9 = java.lang.String.format(r9, r10);
        goto L_0x031c;
    L_0x0319:
        r1.drawCount = r2;
        r9 = 0;
    L_0x031c:
        r10 = r1.customDialog;
        r10 = r10.sent;
        if (r10 == 0) goto L_0x032b;
    L_0x0322:
        r1.drawCheck1 = r6;
        r1.drawCheck2 = r6;
        r1.drawClock = r2;
        r1.drawError = r2;
        goto L_0x0333;
    L_0x032b:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r2;
    L_0x0333:
        r10 = r1.customDialog;
        r10 = r10.name;
        r13 = r7;
        r3 = r9;
        r9 = r10;
        r10 = 0;
        r7 = r5;
        r5 = r8;
        r8 = r4;
    L_0x033e:
        r4 = r0;
        goto L_0x0c9a;
    L_0x0341:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x035c;
    L_0x0345:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x034a;
    L_0x0349:
        goto L_0x035c;
    L_0x034a:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0355;
    L_0x034e:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r9;
        goto L_0x036d;
    L_0x0355:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x036d;
    L_0x035c:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0367;
    L_0x0360:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r9;
        goto L_0x036d;
    L_0x0367:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x036d:
        r9 = r1.encryptedChat;
        if (r9 == 0) goto L_0x03fa;
    L_0x0371:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x0593;
    L_0x0375:
        r1.drawNameLock = r6;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x03bd;
    L_0x037b:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0380;
    L_0x037f:
        goto L_0x03bd;
    L_0x0380:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x03a3;
    L_0x038c:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x03a3:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x03bd:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x03e0;
    L_0x03c9:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x03e0:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x03fa:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x0593;
    L_0x03fe:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x04f5;
    L_0x0402:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x040e;
    L_0x0406:
        r1.drawScam = r6;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0412;
    L_0x040e:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0412:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x0593;
    L_0x0416:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x048a;
    L_0x041a:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x041f;
    L_0x041e:
        goto L_0x048a;
    L_0x041f:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x043d;
    L_0x0425:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x0432;
    L_0x042b:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x0432;
    L_0x0431:
        goto L_0x043d;
    L_0x0432:
        r1.drawNameGroup = r6;
        r9 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x0447;
    L_0x043d:
        r1.drawNameBroadcast = r6;
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x0447:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0469;
    L_0x044b:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x045e;
    L_0x045b:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0460;
    L_0x045e:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0460:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x0469:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x0479;
    L_0x0476:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x047b;
    L_0x0479:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x047b:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x048a:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x04a8;
    L_0x0490:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x049d;
    L_0x0496:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x049d;
    L_0x049c:
        goto L_0x04a8;
    L_0x049d:
        r1.drawNameGroup = r6;
        r9 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x04b2;
    L_0x04a8:
        r1.drawNameBroadcast = r6;
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x04b2:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x04d4;
    L_0x04b6:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04c9;
    L_0x04c6:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04cb;
    L_0x04c9:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04cb:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x04d4:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04e4;
    L_0x04e1:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04e6;
    L_0x04e4:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04e6:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x04f5:
        r9 = r1.user;
        if (r9 == 0) goto L_0x0593;
    L_0x04f9:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x0505;
    L_0x04fd:
        r1.drawScam = r6;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0509;
    L_0x0505:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0509:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x0593;
    L_0x050d:
        r9 = r1.user;
        r9 = r9.bot;
        if (r9 == 0) goto L_0x0593;
    L_0x0513:
        r1.drawNameBot = r6;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x0559;
    L_0x0519:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x051e;
    L_0x051d:
        goto L_0x0559;
    L_0x051e:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0540;
    L_0x052a:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x0540:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x0559:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x057b;
    L_0x0565:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x0593;
    L_0x057b:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x0593:
        r9 = r1.lastMessageDate;
        if (r9 != 0) goto L_0x059f;
    L_0x0597:
        r10 = r1.message;
        if (r10 == 0) goto L_0x059f;
    L_0x059b:
        r9 = r10.messageOwner;
        r9 = r9.date;
    L_0x059f:
        r10 = r1.isDialogCell;
        if (r10 == 0) goto L_0x05fe;
    L_0x05a3:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MediaDataController.getInstance(r10);
        r18 = r7;
        r6 = r1.currentDialogId;
        r6 = r10.getDraft(r6);
        r1.draftMessage = r6;
        r6 = r1.draftMessage;
        if (r6 == 0) goto L_0x05d2;
    L_0x05b7:
        r6 = r6.message;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x05c8;
    L_0x05bf:
        r6 = r1.draftMessage;
        r6 = r6.reply_to_msg_id;
        if (r6 == 0) goto L_0x05c6;
    L_0x05c5:
        goto L_0x05c8;
    L_0x05c6:
        r6 = 0;
        goto L_0x05f9;
    L_0x05c8:
        r6 = r1.draftMessage;
        r6 = r6.date;
        if (r9 <= r6) goto L_0x05d2;
    L_0x05ce:
        r6 = r1.unreadCount;
        if (r6 != 0) goto L_0x05c6;
    L_0x05d2:
        r6 = r1.chat;
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x05ec;
    L_0x05da:
        r6 = r1.chat;
        r7 = r6.megagroup;
        if (r7 != 0) goto L_0x05ec;
    L_0x05e0:
        r7 = r6.creator;
        if (r7 != 0) goto L_0x05ec;
    L_0x05e4:
        r6 = r6.admin_rights;
        if (r6 == 0) goto L_0x05c6;
    L_0x05e8:
        r6 = r6.post_messages;
        if (r6 == 0) goto L_0x05c6;
    L_0x05ec:
        r6 = r1.chat;
        if (r6 == 0) goto L_0x05fc;
    L_0x05f0:
        r7 = r6.left;
        if (r7 != 0) goto L_0x05c6;
    L_0x05f4:
        r6 = r6.kicked;
        if (r6 == 0) goto L_0x05fc;
    L_0x05f8:
        goto L_0x05c6;
    L_0x05f9:
        r1.draftMessage = r6;
        goto L_0x0603;
    L_0x05fc:
        r6 = 0;
        goto L_0x0603;
    L_0x05fe:
        r18 = r7;
        r6 = 0;
        r1.draftMessage = r6;
    L_0x0603:
        if (r0 == 0) goto L_0x0610;
    L_0x0605:
        r1.lastPrintString = r0;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r8 = r4;
        r7 = r6;
        r6 = 1;
    L_0x060c:
        r4 = r0;
        r0 = 1;
        goto L_0x0ae4;
    L_0x0610:
        r1.lastPrintString = r6;
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x06c1;
    L_0x0616:
        r0 = NUM; // 0x7f0e03aa float:1.887694E38 double:1.05316262E-314;
        r6 = "Draft";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r6 = r1.draftMessage;
        r6 = r6.message;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x0655;
    L_0x0629:
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x064e;
    L_0x062d:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x0632;
    L_0x0631:
        goto L_0x064e;
    L_0x0632:
        r6 = android.text.SpannableStringBuilder.valueOf(r0);
        r7 = new android.text.style.ForegroundColorSpan;
        r8 = "chats_draft";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.<init>(r8);
        r8 = r0.length();
        r9 = 33;
        r6.setSpan(r7, r2, r8, r9);
    L_0x064a:
        r7 = r0;
        r8 = r4;
        r4 = r6;
        goto L_0x0651;
    L_0x064e:
        r7 = r0;
        r8 = r4;
        r4 = r12;
    L_0x0651:
        r0 = 0;
        r6 = 1;
        goto L_0x0ae4;
    L_0x0655:
        r6 = r1.draftMessage;
        r6 = r6.message;
        r7 = r6.length();
        if (r7 <= r14) goto L_0x0663;
    L_0x065f:
        r6 = r6.substring(r2, r14);
    L_0x0663:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x0699;
    L_0x0667:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x066c;
    L_0x066b:
        goto L_0x0699;
    L_0x066c:
        r7 = new java.lang.Object[r3];
        r8 = 32;
        r6 = r6.replace(r11, r8);
        r7[r2] = r6;
        r8 = 1;
        r7[r8] = r0;
        r9 = r18;
        r6 = java.lang.String.format(r9, r7);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
        r7 = new android.text.style.ForegroundColorSpan;
        r9 = "chats_draft";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r7.<init>(r9);
        r9 = r0.length();
        r9 = r9 + r8;
        r10 = 33;
        r6.setSpan(r7, r2, r9, r10);
        goto L_0x06b0;
    L_0x0699:
        r9 = r18;
        r8 = 1;
        r7 = new java.lang.Object[r3];
        r10 = 32;
        r6 = r6.replace(r11, r10);
        r7[r2] = r6;
        r7[r8] = r0;
        r6 = java.lang.String.format(r9, r7);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
    L_0x06b0:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r7 = r7.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r7, r9, r2);
        goto L_0x064a;
    L_0x06c1:
        r9 = r18;
        r0 = r1.clearingDialog;
        if (r0 == 0) goto L_0x06d7;
    L_0x06c7:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = NUM; // 0x7f0e051a float:1.8877686E38 double:1.053162802E-314;
        r6 = "HistoryCleared";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
    L_0x06d2:
        r8 = r4;
        r6 = 1;
        r7 = 0;
        goto L_0x060c;
    L_0x06d7:
        r0 = r1.message;
        if (r0 != 0) goto L_0x0772;
    L_0x06db:
        r0 = r1.encryptedChat;
        if (r0 == 0) goto L_0x076b;
    L_0x06df:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
        if (r6 == 0) goto L_0x06ef;
    L_0x06e5:
        r0 = NUM; // 0x7f0e03f8 float:1.8877098E38 double:1.0531626586E-314;
        r6 = "EncryptionProcessing";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06d2;
    L_0x06ef:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
        if (r6 == 0) goto L_0x0719;
    L_0x06f3:
        r0 = r1.user;
        if (r0 == 0) goto L_0x070a;
    L_0x06f7:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x070a;
    L_0x06fb:
        r6 = NUM; // 0x7f0e019f float:1.887588E38 double:1.0531623617E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r8[r2] = r0;
        r0 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r6, r8);
        goto L_0x06d2;
    L_0x070a:
        r7 = 1;
        r0 = NUM; // 0x7f0e019f float:1.887588E38 double:1.0531623617E-314;
        r6 = new java.lang.Object[r7];
        r6[r2] = r12;
        r7 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r0, r6);
        goto L_0x06d2;
    L_0x0719:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
        if (r6 == 0) goto L_0x0727;
    L_0x071d:
        r0 = NUM; // 0x7f0e03f9 float:1.88771E38 double:1.053162659E-314;
        r6 = "EncryptionRejected";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06d2;
    L_0x0727:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r6 == 0) goto L_0x076b;
    L_0x072b:
        r0 = r0.admin_id;
        r6 = r1.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r0 != r6) goto L_0x0760;
    L_0x0739:
        r0 = r1.user;
        if (r0 == 0) goto L_0x0750;
    L_0x073d:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x0750;
    L_0x0741:
        r6 = NUM; // 0x7f0e03ed float:1.8877076E38 double:1.053162653E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r8[r2] = r0;
        r0 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r6, r8);
        goto L_0x06d2;
    L_0x0750:
        r7 = 1;
        r0 = NUM; // 0x7f0e03ed float:1.8877076E38 double:1.053162653E-314;
        r6 = new java.lang.Object[r7];
        r6[r2] = r12;
        r7 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r0, r6);
        goto L_0x06d2;
    L_0x0760:
        r0 = NUM; // 0x7f0e03ec float:1.8877074E38 double:1.0531626527E-314;
        r6 = "EncryptedChatStartedIncoming";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06d2;
    L_0x076b:
        r8 = r4;
        r4 = r12;
        r0 = 1;
        r6 = 1;
        r7 = 0;
        goto L_0x0ae4;
    L_0x0772:
        r0 = r0.isFromUser();
        if (r0 == 0) goto L_0x078f;
    L_0x0778:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r1.message;
        r6 = r6.messageOwner;
        r6 = r6.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getUser(r6);
        r6 = r0;
        r0 = 0;
        goto L_0x07a6;
    L_0x078f:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r1.message;
        r6 = r6.messageOwner;
        r6 = r6.to_id;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getChat(r6);
        r6 = 0;
    L_0x07a6:
        r7 = r1.dialogsType;
        r10 = 3;
        if (r7 != r10) goto L_0x07c4;
    L_0x07ab:
        r7 = r1.user;
        r7 = org.telegram.messenger.UserObject.isUserSelf(r7);
        if (r7 == 0) goto L_0x07c4;
    L_0x07b3:
        r0 = NUM; // 0x7f0e0946 float:1.8879853E38 double:1.0531633295E-314;
        r5 = "SavedMessagesInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r6 = r0;
        r8 = r4;
        r0 = 0;
        r4 = 1;
        r5 = 0;
    L_0x07c1:
        r7 = 0;
        goto L_0x0ad6;
    L_0x07c4:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x07d9;
    L_0x07c8:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 != 0) goto L_0x07d9;
    L_0x07cc:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x07d9;
    L_0x07d0:
        r0 = r33.formatArchivedDialogNames();
        r6 = r0;
        r8 = r4;
        r0 = 1;
        r4 = 0;
        goto L_0x07c1;
    L_0x07d9:
        r7 = r1.message;
        r10 = r7.messageOwner;
        r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r10 == 0) goto L_0x0805;
    L_0x07e1:
        r0 = r1.chat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x07fa;
    L_0x07e9:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r4 != 0) goto L_0x07f7;
    L_0x07f3:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r0 == 0) goto L_0x07fa;
    L_0x07f7:
        r0 = r12;
        r5 = 0;
        goto L_0x07fe;
    L_0x07fa:
        r0 = r1.message;
        r0 = r0.messageText;
    L_0x07fe:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x0800:
        r6 = r0;
        r8 = r4;
        r0 = 1;
        r4 = 1;
        goto L_0x07c1;
    L_0x0805:
        r10 = r1.chat;
        if (r10 == 0) goto L_0x09f4;
    L_0x0809:
        r10 = r10.id;
        if (r10 <= 0) goto L_0x09f4;
    L_0x080d:
        if (r0 != 0) goto L_0x09f4;
    L_0x080f:
        r7 = r7.isOutOwner();
        if (r7 == 0) goto L_0x0820;
    L_0x0815:
        r0 = NUM; // 0x7f0e04de float:1.8877565E38 double:1.053162772E-314;
        r6 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
    L_0x081e:
        r6 = r0;
        goto L_0x0863;
    L_0x0820:
        if (r6 == 0) goto L_0x0855;
    L_0x0822:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0836;
    L_0x0826:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x082b;
    L_0x082a:
        goto L_0x0836;
    L_0x082b:
        r0 = org.telegram.messenger.UserObject.getFirstName(r6);
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x081e;
    L_0x0836:
        r0 = org.telegram.messenger.UserObject.isDeleted(r6);
        if (r0 == 0) goto L_0x0846;
    L_0x083c:
        r0 = NUM; // 0x7f0e0517 float:1.887768E38 double:1.0531628004E-314;
        r6 = "HiddenName";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x081e;
    L_0x0846:
        r0 = r6.first_name;
        r6 = r6.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r6);
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x081e;
    L_0x0855:
        if (r0 == 0) goto L_0x0860;
    L_0x0857:
        r0 = r0.title;
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x081e;
    L_0x0860:
        r0 = "DELETED";
        goto L_0x081e;
    L_0x0863:
        r0 = r1.message;
        r7 = r0.caption;
        if (r7 == 0) goto L_0x08d0;
    L_0x0869:
        r0 = r7.toString();
        r7 = r0.length();
        if (r7 <= r14) goto L_0x0877;
    L_0x0873:
        r0 = r0.substring(r2, r14);
    L_0x0877:
        r7 = r1.message;
        r7 = r7.isVideo();
        if (r7 == 0) goto L_0x0883;
    L_0x087f:
        r7 = "📹 ";
        goto L_0x08aa;
    L_0x0883:
        r7 = r1.message;
        r7 = r7.isVoice();
        if (r7 == 0) goto L_0x088f;
    L_0x088b:
        r7 = "🎤 ";
        goto L_0x08aa;
    L_0x088f:
        r7 = r1.message;
        r7 = r7.isMusic();
        if (r7 == 0) goto L_0x089b;
    L_0x0897:
        r7 = "🎧 ";
        goto L_0x08aa;
    L_0x089b:
        r7 = r1.message;
        r7 = r7.isPhoto();
        if (r7 == 0) goto L_0x08a7;
    L_0x08a3:
        r7 = "🖼 ";
        goto L_0x08aa;
    L_0x08a7:
        r7 = "📎 ";
    L_0x08aa:
        r8 = new java.lang.Object[r3];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r7 = 32;
        r0 = r0.replace(r11, r7);
        r10.append(r0);
        r0 = r10.toString();
        r8[r2] = r0;
        r7 = 1;
        r8[r7] = r6;
        r0 = java.lang.String.format(r9, r8);
        r0 = android.text.SpannableStringBuilder.valueOf(r0);
        goto L_0x09af;
    L_0x08d0:
        r7 = r0.messageOwner;
        r7 = r7.media;
        if (r7 == 0) goto L_0x0983;
    L_0x08d6:
        r0 = r0.isMediaEmpty();
        if (r0 != 0) goto L_0x0983;
    L_0x08dc:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = r1.message;
        r7 = r0.messageOwner;
        r7 = r7.media;
        r10 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r10 == 0) goto L_0x0911;
    L_0x08e8:
        r0 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r0 < r10) goto L_0x08ff;
    L_0x08ee:
        r10 = 1;
        r0 = new java.lang.Object[r10];
        r7 = r7.game;
        r7 = r7.title;
        r0[r2] = r7;
        r7 = "🎮 ⁨%s⁩";
        r0 = java.lang.String.format(r7, r0);
        goto L_0x0952;
    L_0x08ff:
        r10 = 1;
        r0 = new java.lang.Object[r10];
        r7 = r7.game;
        r7 = r7.title;
        r0[r2] = r7;
        r7 = "🎮 %s";
        r0 = java.lang.String.format(r7, r0);
        r10 = 1;
        goto L_0x0952;
    L_0x0911:
        r7 = r0.type;
        r10 = 14;
        if (r7 != r10) goto L_0x094f;
    L_0x0917:
        r7 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r7 < r10) goto L_0x0936;
    L_0x091d:
        r7 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r7[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r10 = 1;
        r7[r10] = r0;
        r0 = "🎧 ⁨%s - %s⁩";
        r0 = java.lang.String.format(r0, r7);
        goto L_0x0952;
    L_0x0936:
        r10 = 1;
        r7 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r7[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r7[r10] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r7);
        goto L_0x0952;
    L_0x094f:
        r10 = 1;
        r0 = r0.messageText;
    L_0x0952:
        r7 = new java.lang.Object[r3];
        r7[r2] = r0;
        r7[r10] = r6;
        r0 = java.lang.String.format(r9, r7);
        r7 = android.text.SpannableStringBuilder.valueOf(r0);
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x097e }
        r9 = "chats_attachMessage";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);	 Catch:{ Exception -> 0x097e }
        r0.<init>(r9);	 Catch:{ Exception -> 0x097e }
        if (r8 == 0) goto L_0x0973;
    L_0x096d:
        r8 = r6.length();	 Catch:{ Exception -> 0x097e }
        r8 = r8 + r3;
        goto L_0x0974;
    L_0x0973:
        r8 = 0;
    L_0x0974:
        r9 = r7.length();	 Catch:{ Exception -> 0x097e }
        r10 = 33;
        r7.setSpan(r0, r8, r9, r10);	 Catch:{ Exception -> 0x097e }
        goto L_0x09b0;
    L_0x097e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x09b0;
    L_0x0983:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.message;
        if (r0 == 0) goto L_0x09ab;
    L_0x098b:
        r7 = r0.length();
        if (r7 <= r14) goto L_0x0995;
    L_0x0991:
        r0 = r0.substring(r2, r14);
    L_0x0995:
        r7 = new java.lang.Object[r3];
        r8 = 32;
        r0 = r0.replace(r11, r8);
        r7[r2] = r0;
        r8 = 1;
        r7[r8] = r6;
        r0 = java.lang.String.format(r9, r7);
        r0 = android.text.SpannableStringBuilder.valueOf(r0);
        goto L_0x09af;
    L_0x09ab:
        r0 = android.text.SpannableStringBuilder.valueOf(r12);
    L_0x09af:
        r7 = r0;
    L_0x09b0:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x09b8;
    L_0x09b4:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x09c2;
    L_0x09b8:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x09dd;
    L_0x09bc:
        r0 = r7.length();
        if (r0 <= 0) goto L_0x09dd;
    L_0x09c2:
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x09d9 }
        r8 = "chats_nameMessage";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);	 Catch:{ Exception -> 0x09d9 }
        r0.<init>(r8);	 Catch:{ Exception -> 0x09d9 }
        r8 = r6.length();	 Catch:{ Exception -> 0x09d9 }
        r9 = 1;
        r8 = r8 + r9;
        r9 = 33;
        r7.setSpan(r0, r2, r8, r9);	 Catch:{ Exception -> 0x09d9 }
        goto L_0x09dd;
    L_0x09d9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x09dd:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r0 = r0.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r7, r0, r9, r2);
        r8 = r4;
        r7 = r6;
        r4 = 0;
        r6 = r0;
        r0 = 1;
        goto L_0x0ad6;
    L_0x09f4:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 == 0) goto L_0x0a13;
    L_0x09fe:
        r6 = r0.photo;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r6 == 0) goto L_0x0a13;
    L_0x0a04:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a13;
    L_0x0a08:
        r0 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r6 = "AttachPhotoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x0800;
    L_0x0a13:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r6 == 0) goto L_0x0a32;
    L_0x0a1d:
        r6 = r0.document;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;
        if (r6 == 0) goto L_0x0a32;
    L_0x0a23:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a32;
    L_0x0a27:
        r0 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r6 = "AttachVideoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x0800;
    L_0x0a32:
        r0 = r1.message;
        r6 = r0.caption;
        if (r6 == 0) goto L_0x0a7e;
    L_0x0a38:
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x0a42;
    L_0x0a3e:
        r0 = "📹 ";
        goto L_0x0a69;
    L_0x0a42:
        r0 = r1.message;
        r0 = r0.isVoice();
        if (r0 == 0) goto L_0x0a4e;
    L_0x0a4a:
        r0 = "🎤 ";
        goto L_0x0a69;
    L_0x0a4e:
        r0 = r1.message;
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x0a5a;
    L_0x0a56:
        r0 = "🎧 ";
        goto L_0x0a69;
    L_0x0a5a:
        r0 = r1.message;
        r0 = r0.isPhoto();
        if (r0 == 0) goto L_0x0a66;
    L_0x0a62:
        r0 = "🖼 ";
        goto L_0x0a69;
    L_0x0a66:
        r0 = "📎 ";
    L_0x0a69:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r0);
        r0 = r1.message;
        r0 = r0.caption;
        r6.append(r0);
        r0 = r6.toString();
        goto L_0x0800;
    L_0x0a7e:
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r6 == 0) goto L_0x0aa3;
    L_0x0a86:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r6 = "🎮 ";
        r0.append(r6);
        r6 = r1.message;
        r6 = r6.messageOwner;
        r6 = r6.media;
        r6 = r6.game;
        r6 = r6.title;
        r0.append(r6);
        r0 = r0.toString();
        goto L_0x0ac4;
    L_0x0aa3:
        r6 = r0.type;
        r7 = 14;
        if (r6 != r7) goto L_0x0ac2;
    L_0x0aa9:
        r6 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r6[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r7 = 1;
        r6[r7] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r6);
        goto L_0x0ac4;
    L_0x0ac2:
        r0 = r0.messageText;
    L_0x0ac4:
        r6 = r1.message;
        r7 = r6.messageOwner;
        r7 = r7.media;
        if (r7 == 0) goto L_0x0800;
    L_0x0acc:
        r6 = r6.isMediaEmpty();
        if (r6 != 0) goto L_0x0800;
    L_0x0ad2:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        goto L_0x0800;
    L_0x0ad6:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x0ade;
    L_0x0ada:
        r7 = r33.formatArchivedDialogNames();
    L_0x0ade:
        r32 = r6;
        r6 = r0;
        r0 = r4;
        r4 = r32;
    L_0x0ae4:
        r9 = r1.draftMessage;
        if (r9 == 0) goto L_0x0af0;
    L_0x0ae8:
        r9 = r9.date;
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0b09;
    L_0x0af0:
        r9 = r1.lastMessageDate;
        if (r9 == 0) goto L_0x0afa;
    L_0x0af4:
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0b09;
    L_0x0afa:
        r9 = r1.message;
        if (r9 == 0) goto L_0x0b08;
    L_0x0afe:
        r9 = r9.messageOwner;
        r9 = r9.date;
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0b09;
    L_0x0b08:
        r9 = r12;
    L_0x0b09:
        r10 = r1.message;
        if (r10 != 0) goto L_0x0b1d;
    L_0x0b0d:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawCount = r2;
        r1.drawMention = r2;
        r1.drawError = r2;
        r3 = 0;
        r10 = 0;
        goto L_0x0c1f;
    L_0x0b1d:
        r3 = r1.currentDialogFolderId;
        if (r3 == 0) goto L_0x0b5e;
    L_0x0b21:
        r3 = r1.unreadCount;
        r10 = r1.mentionCount;
        r19 = r3 + r10;
        if (r19 <= 0) goto L_0x0b57;
    L_0x0b29:
        if (r3 <= r10) goto L_0x0b40;
    L_0x0b2b:
        r14 = 1;
        r1.drawCount = r14;
        r1.drawMention = r2;
        r15 = new java.lang.Object[r14];
        r3 = r3 + r10;
        r3 = java.lang.Integer.valueOf(r3);
        r15[r2] = r3;
        r3 = "%d";
        r3 = java.lang.String.format(r3, r15);
        goto L_0x0b5c;
    L_0x0b40:
        r14 = 1;
        r1.drawCount = r2;
        r1.drawMention = r14;
        r15 = new java.lang.Object[r14];
        r3 = r3 + r10;
        r3 = java.lang.Integer.valueOf(r3);
        r15[r2] = r3;
        r3 = "%d";
        r3 = java.lang.String.format(r3, r15);
        r10 = r3;
        r3 = 0;
        goto L_0x0bad;
    L_0x0b57:
        r1.drawCount = r2;
        r1.drawMention = r2;
        r3 = 0;
    L_0x0b5c:
        r10 = 0;
        goto L_0x0bad;
    L_0x0b5e:
        r3 = r1.clearingDialog;
        if (r3 == 0) goto L_0x0b68;
    L_0x0b62:
        r1.drawCount = r2;
        r3 = 1;
        r5 = 0;
    L_0x0b66:
        r10 = 0;
        goto L_0x0b9b;
    L_0x0b68:
        r3 = r1.unreadCount;
        if (r3 == 0) goto L_0x0b8f;
    L_0x0b6c:
        r14 = 1;
        if (r3 != r14) goto L_0x0b7b;
    L_0x0b6f:
        r14 = r1.mentionCount;
        if (r3 != r14) goto L_0x0b7b;
    L_0x0b73:
        if (r10 == 0) goto L_0x0b7b;
    L_0x0b75:
        r3 = r10.messageOwner;
        r3 = r3.mentioned;
        if (r3 != 0) goto L_0x0b8f;
    L_0x0b7b:
        r3 = 1;
        r1.drawCount = r3;
        r10 = new java.lang.Object[r3];
        r14 = r1.unreadCount;
        r14 = java.lang.Integer.valueOf(r14);
        r10[r2] = r14;
        r14 = "%d";
        r10 = java.lang.String.format(r14, r10);
        goto L_0x0b9b;
    L_0x0b8f:
        r3 = 1;
        r10 = r1.markUnread;
        if (r10 == 0) goto L_0x0b98;
    L_0x0b94:
        r1.drawCount = r3;
        r10 = r12;
        goto L_0x0b9b;
    L_0x0b98:
        r1.drawCount = r2;
        goto L_0x0b66;
    L_0x0b9b:
        r14 = r1.mentionCount;
        if (r14 == 0) goto L_0x0ba9;
    L_0x0b9f:
        r1.drawMention = r3;
        r3 = "@";
        r32 = r10;
        r10 = r3;
        r3 = r32;
        goto L_0x0bad;
    L_0x0ba9:
        r1.drawMention = r2;
        r3 = r10;
        goto L_0x0b5c;
    L_0x0bad:
        r14 = r1.message;
        r14 = r14.isOut();
        if (r14 == 0) goto L_0x0CLASSNAME;
    L_0x0bb5:
        r14 = r1.draftMessage;
        if (r14 != 0) goto L_0x0CLASSNAME;
    L_0x0bb9:
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0bbb:
        r5 = r1.message;
        r14 = r5.messageOwner;
        r14 = r14.action;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r14 != 0) goto L_0x0CLASSNAME;
    L_0x0bc5:
        r5 = r5.isSending();
        if (r5 == 0) goto L_0x0bd5;
    L_0x0bcb:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r5 = 1;
        r1.drawClock = r5;
        r1.drawError = r2;
        goto L_0x0c1f;
    L_0x0bd5:
        r5 = 1;
        r14 = r1.message;
        r14 = r14.isSendError();
        if (r14 == 0) goto L_0x0beb;
    L_0x0bde:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r5;
        r1.drawCount = r2;
        r1.drawMention = r2;
        goto L_0x0c1f;
    L_0x0beb:
        r5 = r1.message;
        r5 = r5.isSent();
        if (r5 == 0) goto L_0x0c1f;
    L_0x0bf3:
        r5 = r1.message;
        r5 = r5.isUnread();
        if (r5 == 0) goto L_0x0c0c;
    L_0x0bfb:
        r5 = r1.chat;
        r5 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r5 == 0) goto L_0x0c0a;
    L_0x0CLASSNAME:
        r5 = r1.chat;
        r5 = r5.megagroup;
        if (r5 != 0) goto L_0x0c0a;
    L_0x0CLASSNAME:
        goto L_0x0c0c;
    L_0x0c0a:
        r5 = 0;
        goto L_0x0c0d;
    L_0x0c0c:
        r5 = 1;
    L_0x0c0d:
        r1.drawCheck1 = r5;
        r5 = 1;
        r1.drawCheck2 = r5;
        r1.drawClock = r2;
        r1.drawError = r2;
        goto L_0x0c1f;
    L_0x0CLASSNAME:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r2;
    L_0x0c1f:
        r5 = r1.dialogsType;
        if (r5 != 0) goto L_0x0c3e;
    L_0x0CLASSNAME:
        r5 = r1.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r14 = r1.currentDialogId;
        r13 = 1;
        r5 = r5.isProxyDialog(r14, r13);
        if (r5 == 0) goto L_0x0c3e;
    L_0x0CLASSNAME:
        r1.drawPinBackground = r13;
        r5 = NUM; // 0x7f0e0aef float:1.8880715E38 double:1.0531635395E-314;
        r9 = "UseProxySponsor";
        r5 = org.telegram.messenger.LocaleController.getString(r9, r5);
        goto L_0x0c3f;
    L_0x0c3e:
        r5 = r9;
    L_0x0c3f:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0e0103 float:1.8875563E38 double:1.0531622846E-314;
        r13 = "ArchivedChats";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
    L_0x0c4c:
        r13 = r8;
        r8 = r7;
        r7 = r4;
        goto L_0x033e;
    L_0x0CLASSNAME:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r9.title;
        goto L_0x0c8a;
    L_0x0CLASSNAME:
        r9 = r1.user;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0c5c:
        r9 = org.telegram.messenger.UserObject.isUserSelf(r9);
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r1.useMeForMyMessages;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0e04de float:1.8877565E38 double:1.053162772E-314;
        r13 = "FromYou";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        goto L_0x0c8a;
    L_0x0CLASSNAME:
        r9 = r1.dialogsType;
        r13 = 3;
        if (r9 != r13) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = 1;
        r1.drawPinBackground = r9;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0e0945 float:1.887985E38 double:1.053163329E-314;
        r13 = "SavedMessages";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        goto L_0x0c8a;
    L_0x0CLASSNAME:
        r9 = r1.user;
        r9 = org.telegram.messenger.UserObject.getUserName(r9);
        goto L_0x0c8a;
    L_0x0CLASSNAME:
        r9 = r12;
    L_0x0c8a:
        r13 = r9.length();
        if (r13 != 0) goto L_0x0c4c;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0e0517 float:1.887768E38 double:1.0531628004E-314;
        r13 = "HiddenName";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        goto L_0x0c4c;
    L_0x0c9a:
        if (r6 == 0) goto L_0x0cdb;
    L_0x0c9c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r0 = r0.measureText(r5);
        r14 = (double) r0;
        r14 = java.lang.Math.ceil(r14);
        r0 = (int) r14;
        r6 = new android.text.StaticLayout;
        r24 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r26 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r6;
        r23 = r5;
        r25 = r0;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);
        r1.timeLayout = r6;
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 != 0) goto L_0x0cd2;
    L_0x0cc3:
        r5 = r33.getMeasuredWidth();
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = r5 - r0;
        r1.timeLeft = r5;
        goto L_0x0ce1;
    L_0x0cd2:
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.timeLeft = r5;
        goto L_0x0ce1;
    L_0x0cdb:
        r5 = 0;
        r1.timeLayout = r5;
        r1.timeLeft = r2;
        r0 = 0;
    L_0x0ce1:
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 != 0) goto L_0x0cf5;
    L_0x0ce5:
        r5 = r33.getMeasuredWidth();
        r6 = r1.nameLeft;
        r5 = r5 - r6;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = r5 - r0;
        goto L_0x0d09;
    L_0x0cf5:
        r5 = r33.getMeasuredWidth();
        r6 = r1.nameLeft;
        r5 = r5 - r6;
        r6 = NUM; // 0x429a0000 float:77.0 double:5.52063419E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = r5 - r0;
        r6 = r1.nameLeft;
        r6 = r6 + r0;
        r1.nameLeft = r6;
    L_0x0d09:
        r6 = r1.drawNameLock;
        if (r6 == 0) goto L_0x0d1c;
    L_0x0d0d:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r14 = r14.getIntrinsicWidth();
    L_0x0d19:
        r6 = r6 + r14;
        r5 = r5 - r6;
        goto L_0x0d4f;
    L_0x0d1c:
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x0d2d;
    L_0x0d20:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0d19;
    L_0x0d2d:
        r6 = r1.drawNameBroadcast;
        if (r6 == 0) goto L_0x0d3e;
    L_0x0d31:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0d19;
    L_0x0d3e:
        r6 = r1.drawNameBot;
        if (r6 == 0) goto L_0x0d4f;
    L_0x0d42:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0d19;
    L_0x0d4f:
        r6 = r1.drawClock;
        if (r6 == 0) goto L_0x0d7f;
    L_0x0d53:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r6 = r6.getIntrinsicWidth();
        r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = r6 + r14;
        r5 = r5 - r6;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0d6c;
    L_0x0d65:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dfe;
    L_0x0d6c:
        r14 = r1.timeLeft;
        r14 = r14 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r14 = r14 + r0;
        r1.checkDrawLeft = r14;
        r0 = r1.nameLeft;
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0dfe;
    L_0x0d7f:
        r6 = r1.drawCheck2;
        if (r6 == 0) goto L_0x0dfe;
    L_0x0d83:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r6 = r6.getIntrinsicWidth();
        r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = r6 + r14;
        r5 = r5 - r6;
        r14 = r1.drawCheck1;
        if (r14 == 0) goto L_0x0de3;
    L_0x0d95:
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r14 = r14.getIntrinsicWidth();
        r15 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r14 = r14 - r15;
        r5 = r5 - r14;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0db8;
    L_0x0da7:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.halfCheckDrawLeft;
        r6 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dfe;
    L_0x0db8:
        r14 = r1.timeLeft;
        r14 = r14 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r14 = r14 + r0;
        r1.checkDrawLeft = r14;
        r0 = r1.checkDrawLeft;
        r14 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r0 = r0 + r14;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.nameLeft;
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r14 = r14.getIntrinsicWidth();
        r6 = r6 + r14;
        r14 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = r6 - r14;
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0dfe;
    L_0x0de3:
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0ded;
    L_0x0de7:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dfe;
    L_0x0ded:
        r14 = r1.timeLeft;
        r14 = r14 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r14 = r14 + r0;
        r1.checkDrawLeft = r14;
        r0 = r1.nameLeft;
        r0 = r0 + r6;
        r1.nameLeft = r0;
    L_0x0dfe:
        r0 = r1.dialogMuted;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r0 == 0) goto L_0x0e22;
    L_0x0e04:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x0e22;
    L_0x0e08:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x0e22;
    L_0x0e0c:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e55;
    L_0x0e1c:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
        goto L_0x0e55;
    L_0x0e22:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0e3c;
    L_0x0e26:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e55;
    L_0x0e36:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
        goto L_0x0e55;
    L_0x0e3c:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0e55;
    L_0x0e40:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e55;
    L_0x0e50:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
    L_0x0e55:
        r14 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r5 = java.lang.Math.max(r0, r5);
        r15 = 32;
        r0 = r9.replace(r11, r15);	 Catch:{ Exception -> 0x0e8a }
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e8a }
        r15 = org.telegram.messenger.AndroidUtilities.dp(r14);	 Catch:{ Exception -> 0x0e8a }
        r15 = r5 - r15;
        r15 = (float) r15;	 Catch:{ Exception -> 0x0e8a }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0e8a }
        r23 = android.text.TextUtils.ellipsize(r0, r9, r15, r6);	 Catch:{ Exception -> 0x0e8a }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0e8a }
        r24 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e8a }
        r26 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0e8a }
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r0;
        r25 = r5;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);	 Catch:{ Exception -> 0x0e8a }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x0e8a }
        goto L_0x0e8e;
    L_0x0e8a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0e8e:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0var_;
    L_0x0e92:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0e98;
    L_0x0e96:
        goto L_0x0var_;
    L_0x0e98:
        r0 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.messageNameTop = r6;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.timeTop = r6;
        r6 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.errorTop = r6;
        r6 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.pinTop = r6;
        r6 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.countTop = r6;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.checkDrawTop = r6;
        r6 = r33.getMeasuredWidth();
        r9 = NUM; // 0x42be0000 float:95.0 double:5.53229066E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0eec;
    L_0x0edd:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x0var_;
    L_0x0eec:
        r9 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = r33.getMeasuredWidth();
        r15 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r9 = r9 - r15;
    L_0x0var_:
        r15 = r1.avatarImage;
        r16 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r14 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r15.setImageCoords(r9, r0, r11, r14);
        goto L_0x0f8c;
    L_0x0var_:
        r0 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.messageNameTop = r6;
        r6 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.timeTop = r6;
        r6 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.errorTop = r6;
        r6 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.pinTop = r6;
        r6 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.countTop = r6;
        r6 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.checkDrawTop = r6;
        r6 = r33.getMeasuredWidth();
        r9 = NUM; // 0x42ba0000 float:93.0 double:5.5309955E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0var_;
    L_0x0var_:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x0f7b;
    L_0x0var_:
        r9 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = r33.getMeasuredWidth();
        r11 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r9 = r9 - r11;
    L_0x0f7b:
        r11 = r1.avatarImage;
        r14 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r15 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11.setImageCoords(r9, r0, r14, r15);
    L_0x0f8c:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0fb1;
    L_0x0var_:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0fa9;
    L_0x0var_:
        r0 = r33.getMeasuredWidth();
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r9 = r9.getIntrinsicWidth();
        r0 = r0 - r9;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 - r9;
        r1.pinLeft = r0;
        goto L_0x0fb1;
    L_0x0fa9:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.pinLeft = r0;
    L_0x0fb1:
        r0 = r1.drawError;
        if (r0 == 0) goto L_0x0fe3;
    L_0x0fb5:
        r0 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x0fcf;
    L_0x0fc0:
        r0 = r33.getMeasuredWidth();
        r3 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.errorLeft = r0;
        goto L_0x1108;
    L_0x0fcf:
        r3 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.errorLeft = r3;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
        goto L_0x1108;
    L_0x0fe3:
        if (r3 != 0) goto L_0x100e;
    L_0x0fe5:
        if (r10 == 0) goto L_0x0fe8;
    L_0x0fe7:
        goto L_0x100e;
    L_0x0fe8:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x1008;
    L_0x0fec:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0 = r0.getIntrinsicWidth();
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x1008;
    L_0x0ffe:
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x1008:
        r1.drawCount = r2;
        r1.drawMention = r2;
        goto L_0x1108;
    L_0x100e:
        if (r3 == 0) goto L_0x1076;
    L_0x1010:
        r9 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r9 = r9.measureText(r3);
        r14 = (double) r9;
        r14 = java.lang.Math.ceil(r14);
        r9 = (int) r14;
        r0 = java.lang.Math.max(r0, r9);
        r1.countWidth = r0;
        r0 = new android.text.StaticLayout;
        r24 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r9 = r1.countWidth;
        r26 = android.text.Layout.Alignment.ALIGN_CENTER;
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r0;
        r23 = r3;
        r25 = r9;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);
        r1.countLayout = r0;
        r0 = r1.countWidth;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r9;
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x1060;
    L_0x104f:
        r0 = r33.getMeasuredWidth();
        r3 = r1.countWidth;
        r0 = r0 - r3;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r9;
        r1.countLeft = r0;
        goto L_0x1072;
    L_0x1060:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.countLeft = r9;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x1072:
        r3 = 1;
        r1.drawCount = r3;
        goto L_0x1078;
    L_0x1076:
        r1.countWidth = r2;
    L_0x1078:
        if (r10 == 0) goto L_0x1108;
    L_0x107a:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x10b0;
    L_0x107e:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r3 = r3.measureText(r10);
        r14 = (double) r3;
        r14 = java.lang.Math.ceil(r14);
        r3 = (int) r14;
        r0 = java.lang.Math.max(r0, r3);
        r1.mentionWidth = r0;
        r0 = new android.text.StaticLayout;
        r24 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r3 = r1.mentionWidth;
        r26 = android.text.Layout.Alignment.ALIGN_CENTER;
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r0;
        r23 = r10;
        r25 = r3;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);
        r1.mentionLayout = r0;
        goto L_0x10b8;
    L_0x10b0:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.mentionWidth = r0;
    L_0x10b8:
        r0 = r1.mentionWidth;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r9;
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x10e5;
    L_0x10c6:
        r0 = r33.getMeasuredWidth();
        r3 = r1.mentionWidth;
        r0 = r0 - r3;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = r1.countWidth;
        if (r3 == 0) goto L_0x10e0;
    L_0x10d8:
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r3 = r3 + r9;
        goto L_0x10e1;
    L_0x10e0:
        r3 = 0;
    L_0x10e1:
        r0 = r0 - r3;
        r1.mentionLeft = r0;
        goto L_0x1105;
    L_0x10e5:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r10 = r1.countWidth;
        if (r10 == 0) goto L_0x10f7;
    L_0x10f1:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r10;
        goto L_0x10f8;
    L_0x10f7:
        r9 = 0;
    L_0x10f8:
        r3 = r3 + r9;
        r1.mentionLeft = r3;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x1105:
        r3 = 1;
        r1.drawMention = r3;
    L_0x1108:
        if (r4 == 0) goto L_0x113f;
    L_0x110a:
        if (r7 != 0) goto L_0x110d;
    L_0x110c:
        r7 = r12;
    L_0x110d:
        r0 = r7.toString();
        r3 = r0.length();
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r3 <= r4) goto L_0x111d;
    L_0x1119:
        r0 = r0.substring(r2, r4);
    L_0x111d:
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x1125;
    L_0x1121:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x1127;
    L_0x1125:
        if (r8 == 0) goto L_0x112f;
    L_0x1127:
        r3 = 32;
        r4 = 10;
        r0 = r0.replace(r4, r3);
    L_0x112f:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = r3.getFontMetricsInt();
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r7 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r4, r2);
    L_0x113f:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = java.lang.Math.max(r0, r6);
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x1151;
    L_0x114d:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x1185;
    L_0x1151:
        if (r8 == 0) goto L_0x1185;
    L_0x1153:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x115c;
    L_0x1157:
        r0 = r1.currentDialogFolderDialogsCount;
        r4 = 1;
        if (r0 != r4) goto L_0x1185;
    L_0x115c:
        r23 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;	 Catch:{ Exception -> 0x1177 }
        r25 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x1177 }
        r26 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r27 = 0;
        r28 = 0;
        r29 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x1177 }
        r31 = 1;
        r22 = r8;
        r24 = r3;
        r30 = r3;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x1177 }
        r1.messageNameLayout = r0;	 Catch:{ Exception -> 0x1177 }
        goto L_0x117b;
    L_0x1177:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x117b:
        r0 = NUM; // 0x424CLASSNAME float:51.0 double:5.495378504E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        r6 = 0;
        goto L_0x11a2;
    L_0x1185:
        r6 = 0;
        r1.messageNameLayout = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x119a;
    L_0x118c:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x1191;
    L_0x1190:
        goto L_0x119a;
    L_0x1191:
        r0 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        goto L_0x11a2;
    L_0x119a:
        r0 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
    L_0x11a2:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x121c }
        if (r0 != 0) goto L_0x11aa;
    L_0x11a6:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x121c }
        if (r0 == 0) goto L_0x11b8;
    L_0x11aa:
        r0 = r1.currentDialogFolderId;	 Catch:{ Exception -> 0x121c }
        if (r0 == 0) goto L_0x11b8;
    L_0x11ae:
        r0 = r1.currentDialogFolderDialogsCount;	 Catch:{ Exception -> 0x121c }
        r4 = 1;
        if (r0 <= r4) goto L_0x11b9;
    L_0x11b3:
        r13 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;	 Catch:{ Exception -> 0x121c }
        r0 = r8;
        r8 = r6;
        goto L_0x11d4;
    L_0x11b8:
        r4 = 1;
    L_0x11b9:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x121c }
        if (r0 != 0) goto L_0x11c1;
    L_0x11bd:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x121c }
        if (r0 == 0) goto L_0x11c3;
    L_0x11c1:
        if (r8 == 0) goto L_0x11d3;
    L_0x11c3:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x121c }
        r0 = r3 - r0;
        r0 = (float) r0;	 Catch:{ Exception -> 0x121c }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x121c }
        r0 = android.text.TextUtils.ellipsize(r7, r13, r0, r6);	 Catch:{ Exception -> 0x121c }
        goto L_0x11d4;
    L_0x11d3:
        r0 = r7;
    L_0x11d4:
        r6 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x121c }
        if (r6 != 0) goto L_0x11f5;
    L_0x11d8:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x121c }
        if (r6 == 0) goto L_0x11dd;
    L_0x11dc:
        goto L_0x11f5;
    L_0x11dd:
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x121c }
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x121c }
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r4;
        r20 = r0;
        r21 = r13;
        r22 = r3;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x121c }
        r1.messageLayout = r4;	 Catch:{ Exception -> 0x121c }
        goto L_0x1220;
    L_0x11f5:
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x121c }
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x121c }
        r6 = (float) r6;	 Catch:{ Exception -> 0x121c }
        r25 = 0;
        r26 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x121c }
        if (r8 == 0) goto L_0x1209;
    L_0x1206:
        r28 = 1;
        goto L_0x120b;
    L_0x1209:
        r28 = 2;
    L_0x120b:
        r19 = r0;
        r20 = r13;
        r21 = r3;
        r24 = r6;
        r27 = r3;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x121c }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x121c }
        goto L_0x1220;
    L_0x121c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x1220:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x1352;
    L_0x1224:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x12dc;
    L_0x1228:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x12dc;
    L_0x122e:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r2);
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r2);
        r6 = (double) r4;
        r6 = java.lang.Math.ceil(r6);
        r4 = r1.dialogMuted;
        if (r4 == 0) goto L_0x1271;
    L_0x1243:
        r4 = r1.drawVerified;
        if (r4 != 0) goto L_0x1271;
    L_0x1247:
        r4 = r1.drawScam;
        if (r4 != 0) goto L_0x1271;
    L_0x124b:
        r4 = r1.nameLeft;
        r8 = (double) r4;
        r10 = (double) r5;
        java.lang.Double.isNaN(r10);
        r10 = r10 - r6;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r10;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r4 = r4.getIntrinsicWidth();
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = (int) r8;
        r1.nameMuteLeft = r4;
        goto L_0x12c4;
    L_0x1271:
        r4 = r1.drawVerified;
        if (r4 == 0) goto L_0x129b;
    L_0x1275:
        r4 = r1.nameLeft;
        r8 = (double) r4;
        r10 = (double) r5;
        java.lang.Double.isNaN(r10);
        r10 = r10 - r6;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r10;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r4 = r4.getIntrinsicWidth();
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = (int) r8;
        r1.nameMuteLeft = r4;
        goto L_0x12c4;
    L_0x129b:
        r4 = r1.drawScam;
        if (r4 == 0) goto L_0x12c4;
    L_0x129f:
        r4 = r1.nameLeft;
        r8 = (double) r4;
        r10 = (double) r5;
        java.lang.Double.isNaN(r10);
        r10 = r10 - r6;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r10;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r4 = r4.getIntrinsicWidth();
        r10 = (double) r4;
        java.lang.Double.isNaN(r10);
        r8 = r8 - r10;
        r4 = (int) r8;
        r1.nameMuteLeft = r4;
    L_0x12c4:
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x12dc;
    L_0x12c9:
        r4 = (double) r5;
        r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x12dc;
    L_0x12ce:
        r0 = r1.nameLeft;
        r8 = (double) r0;
        java.lang.Double.isNaN(r4);
        r4 = r4 - r6;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r4;
        r0 = (int) r8;
        r1.nameLeft = r0;
    L_0x12dc:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x131d;
    L_0x12e0:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x131d;
    L_0x12e6:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = 0;
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x12ed:
        if (r4 >= r0) goto L_0x1313;
    L_0x12ef:
        r6 = r1.messageLayout;
        r6 = r6.getLineLeft(r4);
        r7 = 0;
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 != 0) goto L_0x1312;
    L_0x12fa:
        r6 = r1.messageLayout;
        r6 = r6.getLineWidth(r4);
        r6 = (double) r6;
        r6 = java.lang.Math.ceil(r6);
        r8 = (double) r3;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r6;
        r6 = (int) r8;
        r5 = java.lang.Math.min(r5, r6);
        r4 = r4 + 1;
        goto L_0x12ed;
    L_0x1312:
        r5 = 0;
    L_0x1313:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r5 == r0) goto L_0x131d;
    L_0x1318:
        r0 = r1.messageLeft;
        r0 = r0 + r5;
        r1.messageLeft = r0;
    L_0x131d:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x13dc;
    L_0x1321:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13dc;
    L_0x1327:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineLeft(r2);
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x13dc;
    L_0x1332:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineWidth(r2);
        r4 = (double) r0;
        r4 = java.lang.Math.ceil(r4);
        r2 = (double) r3;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x13dc;
    L_0x1342:
        r0 = r1.messageNameLeft;
        r6 = (double) r0;
        java.lang.Double.isNaN(r2);
        r2 = r2 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r2;
        r0 = (int) r6;
        r1.messageNameLeft = r0;
        goto L_0x13dc;
    L_0x1352:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x13a0;
    L_0x1356:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13a0;
    L_0x135c:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r2);
        r3 = (float) r5;
        r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r3 != 0) goto L_0x1385;
    L_0x1367:
        r3 = r1.nameLayout;
        r3 = r3.getLineWidth(r2);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r5;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x1385;
    L_0x1377:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r5;
        r3 = (int) r7;
        r1.nameLeft = r3;
    L_0x1385:
        r3 = r1.dialogMuted;
        if (r3 != 0) goto L_0x1391;
    L_0x1389:
        r3 = r1.drawVerified;
        if (r3 != 0) goto L_0x1391;
    L_0x138d:
        r3 = r1.drawScam;
        if (r3 == 0) goto L_0x13a0;
    L_0x1391:
        r3 = r1.nameLeft;
        r3 = (float) r3;
        r3 = r3 + r0;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (float) r0;
        r3 = r3 + r0;
        r0 = (int) r3;
        r1.nameMuteLeft = r0;
    L_0x13a0:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x13c5;
    L_0x13a4:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13c5;
    L_0x13aa:
        r3 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r3 = 0;
        r4 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
    L_0x13af:
        if (r3 >= r0) goto L_0x13be;
    L_0x13b1:
        r5 = r1.messageLayout;
        r5 = r5.getLineLeft(r3);
        r4 = java.lang.Math.min(r4, r5);
        r3 = r3 + 1;
        goto L_0x13af;
    L_0x13be:
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r0 = r0 - r4;
        r0 = (int) r0;
        r1.messageLeft = r0;
    L_0x13c5:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x13dc;
    L_0x13c9:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13dc;
    L_0x13cf:
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r3 = r1.messageNameLayout;
        r2 = r3.getLineLeft(r2);
        r0 = r0 - r2;
        r0 = (int) r0;
        r1.messageNameLeft = r0;
    L_0x13dc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
    }

    public boolean isPointInsideAvatar(float f, float f2) {
        boolean z = true;
        if (LocaleController.isRTL) {
            if (f < ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) || f >= ((float) getMeasuredWidth())) {
                z = false;
            }
            return z;
        }
        if (f < 0.0f || f >= ((float) AndroidUtilities.dp(60.0f))) {
            z = false;
        }
        return z;
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    public void checkCurrentDialogIndex(boolean z) {
        ArrayList dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, z);
        if (this.index < dialogsArray.size()) {
            MessageObject findFolderTopMessage;
            Dialog dialog = (Dialog) dialogsArray.get(this.index);
            boolean z2 = true;
            Dialog dialog2 = this.index + 1 < dialogsArray.size() ? (Dialog) dialogsArray.get(this.index + 1) : null;
            DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            if (this.currentDialogFolderId != 0) {
                findFolderTopMessage = findFolderTopMessage();
            } else {
                findFolderTopMessage = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
            }
            if (this.currentDialogId == dialog.id) {
                MessageObject messageObject = this.message;
                if ((messageObject == null || messageObject.getId() == dialog.top_message) && ((findFolderTopMessage == null || findFolderTopMessage.messageOwner.edit_date == this.currentEditDate) && this.unreadCount == dialog.unread_count && this.mentionCount == dialog.unread_mentions_count && this.markUnread == dialog.unread_mark)) {
                    messageObject = this.message;
                    if (messageObject == findFolderTopMessage && ((messageObject != null || findFolderTopMessage == null) && draft == this.draftMessage && this.drawPin == dialog.pinned)) {
                        return;
                    }
                }
            }
            Object obj = this.currentDialogId != dialog.id ? 1 : null;
            this.currentDialogId = dialog.id;
            boolean z3 = dialog instanceof TL_dialogFolder;
            if (z3) {
                this.currentDialogFolderId = ((TL_dialogFolder) dialog).folder.id;
            } else {
                this.currentDialogFolderId = 0;
            }
            boolean z4 = (dialog instanceof TL_dialog) && dialog.pinned && dialog2 != null && !dialog2.pinned;
            this.fullSeparator = z4;
            if (!z3 || dialog2 == null || dialog2.pinned) {
                z2 = false;
            }
            this.fullSeparator2 = z2;
            update(0);
            if (obj != null) {
                float f = (this.drawPin && this.drawReorder) ? 1.0f : 0.0f;
                this.reorderIconProgress = f;
            }
            checkOnline();
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
        int i = 0;
        ArrayList dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject messageObject = null;
        if (!dialogsArray.isEmpty()) {
            int size = dialogsArray.size();
            while (i < size) {
                Dialog dialog = (Dialog) dialogsArray.get(i);
                MessageObject messageObject2 = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (messageObject2 != null && (messageObject == null || messageObject2.messageOwner.date > messageObject.messageOwner.date)) {
                    messageObject = messageObject2;
                }
                if (dialog.pinnedNum == 0) {
                    break;
                }
                i++;
            }
        }
        return messageObject;
    }

    /* JADX WARNING: Removed duplicated region for block: B:98:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01bf  */
    /* JADX WARNING: Missing block: B:52:0x0113, code skipped:
            if (r6.equals(r2) == false) goto L_0x0115;
     */
    public void update(int r20) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r20;
        r2 = r0.customDialog;
        r3 = 0;
        r4 = 1;
        r5 = 0;
        if (r2 == 0) goto L_0x003c;
    L_0x000b:
        r1 = r2.date;
        r0.lastMessageDate = r1;
        r1 = r2.unread_count;
        if (r1 == 0) goto L_0x0014;
    L_0x0013:
        goto L_0x0015;
    L_0x0014:
        r4 = 0;
    L_0x0015:
        r0.lastUnreadState = r4;
        r1 = r0.customDialog;
        r2 = r1.unread_count;
        r0.unreadCount = r2;
        r2 = r1.pinned;
        r0.drawPin = r2;
        r2 = r1.muted;
        r0.dialogMuted = r2;
        r2 = r0.avatarDrawable;
        r4 = r1.id;
        r1 = r1.name;
        r2.setInfo(r4, r1, r3);
        r5 = r0.avatarImage;
        r6 = 0;
        r8 = r0.avatarDrawable;
        r9 = 0;
        r10 = 0;
        r7 = "50_50";
        r5.setImage(r6, r7, r8, r9, r10);
        goto L_0x0319;
    L_0x003c:
        r2 = r0.isDialogCell;
        if (r2 == 0) goto L_0x00c2;
    L_0x0040:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2 = r2.dialogs_dict;
        r6 = r0.currentDialogId;
        r2 = r2.get(r6);
        r2 = (org.telegram.tgnet.TLRPC.Dialog) r2;
        if (r2 == 0) goto L_0x00b7;
    L_0x0052:
        if (r1 != 0) goto L_0x00c4;
    L_0x0054:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r2.id;
        r6 = r6.isClearingDialog(r7);
        r0.clearingDialog = r6;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r6 = r6.dialogMessage;
        r7 = r2.id;
        r6 = r6.get(r7);
        r6 = (org.telegram.messenger.MessageObject) r6;
        r0.message = r6;
        r6 = r0.message;
        if (r6 == 0) goto L_0x0080;
    L_0x0078:
        r6 = r6.isUnread();
        if (r6 == 0) goto L_0x0080;
    L_0x007e:
        r6 = 1;
        goto L_0x0081;
    L_0x0080:
        r6 = 0;
    L_0x0081:
        r0.lastUnreadState = r6;
        r6 = r2.unread_count;
        r0.unreadCount = r6;
        r6 = r2.unread_mark;
        r0.markUnread = r6;
        r6 = r2.unread_mentions_count;
        r0.mentionCount = r6;
        r6 = r0.message;
        if (r6 == 0) goto L_0x0098;
    L_0x0093:
        r6 = r6.messageOwner;
        r6 = r6.edit_date;
        goto L_0x0099;
    L_0x0098:
        r6 = 0;
    L_0x0099:
        r0.currentEditDate = r6;
        r6 = r2.last_message_date;
        r0.lastMessageDate = r6;
        r6 = r0.currentDialogFolderId;
        if (r6 != 0) goto L_0x00a9;
    L_0x00a3:
        r2 = r2.pinned;
        if (r2 == 0) goto L_0x00a9;
    L_0x00a7:
        r2 = 1;
        goto L_0x00aa;
    L_0x00a9:
        r2 = 0;
    L_0x00aa:
        r0.drawPin = r2;
        r2 = r0.message;
        if (r2 == 0) goto L_0x00c4;
    L_0x00b0:
        r2 = r2.messageOwner;
        r2 = r2.send_state;
        r0.lastSendState = r2;
        goto L_0x00c4;
    L_0x00b7:
        r0.unreadCount = r5;
        r0.mentionCount = r5;
        r0.currentEditDate = r5;
        r0.lastMessageDate = r5;
        r0.clearingDialog = r5;
        goto L_0x00c4;
    L_0x00c2:
        r0.drawPin = r5;
    L_0x00c4:
        if (r1 == 0) goto L_0x01c3;
    L_0x00c6:
        r2 = r0.user;
        if (r2 == 0) goto L_0x00e5;
    L_0x00ca:
        r2 = r1 & 4;
        if (r2 == 0) goto L_0x00e5;
    L_0x00ce:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = r0.user;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getUser(r6);
        r0.user = r2;
        r19.invalidate();
    L_0x00e5:
        r2 = r0.isDialogCell;
        if (r2 == 0) goto L_0x0117;
    L_0x00e9:
        r2 = r1 & 64;
        if (r2 == 0) goto L_0x0117;
    L_0x00ed:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2 = r2.printingStrings;
        r6 = r0.currentDialogId;
        r2 = r2.get(r6);
        r2 = (java.lang.CharSequence) r2;
        r6 = r0.lastPrintString;
        if (r6 == 0) goto L_0x0103;
    L_0x0101:
        if (r2 == 0) goto L_0x0115;
    L_0x0103:
        r6 = r0.lastPrintString;
        if (r6 != 0) goto L_0x0109;
    L_0x0107:
        if (r2 != 0) goto L_0x0115;
    L_0x0109:
        r6 = r0.lastPrintString;
        if (r6 == 0) goto L_0x0117;
    L_0x010d:
        if (r2 == 0) goto L_0x0117;
    L_0x010f:
        r2 = r6.equals(r2);
        if (r2 != 0) goto L_0x0117;
    L_0x0115:
        r2 = 1;
        goto L_0x0118;
    L_0x0117:
        r2 = 0;
    L_0x0118:
        if (r2 != 0) goto L_0x012b;
    L_0x011a:
        r6 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r6 = r6 & r1;
        if (r6 == 0) goto L_0x012b;
    L_0x0120:
        r6 = r0.message;
        if (r6 == 0) goto L_0x012b;
    L_0x0124:
        r6 = r6.messageText;
        r7 = r0.lastMessageString;
        if (r6 == r7) goto L_0x012b;
    L_0x012a:
        r2 = 1;
    L_0x012b:
        if (r2 != 0) goto L_0x0136;
    L_0x012d:
        r6 = r1 & 2;
        if (r6 == 0) goto L_0x0136;
    L_0x0131:
        r6 = r0.chat;
        if (r6 != 0) goto L_0x0136;
    L_0x0135:
        r2 = 1;
    L_0x0136:
        if (r2 != 0) goto L_0x0141;
    L_0x0138:
        r6 = r1 & 1;
        if (r6 == 0) goto L_0x0141;
    L_0x013c:
        r6 = r0.chat;
        if (r6 != 0) goto L_0x0141;
    L_0x0140:
        r2 = 1;
    L_0x0141:
        if (r2 != 0) goto L_0x014c;
    L_0x0143:
        r6 = r1 & 8;
        if (r6 == 0) goto L_0x014c;
    L_0x0147:
        r6 = r0.user;
        if (r6 != 0) goto L_0x014c;
    L_0x014b:
        r2 = 1;
    L_0x014c:
        if (r2 != 0) goto L_0x0157;
    L_0x014e:
        r6 = r1 & 16;
        if (r6 == 0) goto L_0x0157;
    L_0x0152:
        r6 = r0.user;
        if (r6 != 0) goto L_0x0157;
    L_0x0156:
        r2 = 1;
    L_0x0157:
        if (r2 != 0) goto L_0x01a8;
    L_0x0159:
        r6 = r1 & 256;
        if (r6 == 0) goto L_0x01a8;
    L_0x015d:
        r6 = r0.message;
        if (r6 == 0) goto L_0x0173;
    L_0x0161:
        r7 = r0.lastUnreadState;
        r6 = r6.isUnread();
        if (r7 == r6) goto L_0x0173;
    L_0x0169:
        r2 = r0.message;
        r2 = r2.isUnread();
        r0.lastUnreadState = r2;
    L_0x0171:
        r2 = 1;
        goto L_0x01a8;
    L_0x0173:
        r6 = r0.isDialogCell;
        if (r6 == 0) goto L_0x01a8;
    L_0x0177:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r6 = r6.dialogs_dict;
        r7 = r0.currentDialogId;
        r6 = r6.get(r7);
        r6 = (org.telegram.tgnet.TLRPC.Dialog) r6;
        if (r6 == 0) goto L_0x01a8;
    L_0x0189:
        r7 = r0.unreadCount;
        r8 = r6.unread_count;
        if (r7 != r8) goto L_0x019b;
    L_0x018f:
        r7 = r0.markUnread;
        r8 = r6.unread_mark;
        if (r7 != r8) goto L_0x019b;
    L_0x0195:
        r7 = r0.mentionCount;
        r8 = r6.unread_mentions_count;
        if (r7 == r8) goto L_0x01a8;
    L_0x019b:
        r2 = r6.unread_count;
        r0.unreadCount = r2;
        r2 = r6.unread_mentions_count;
        r0.mentionCount = r2;
        r2 = r6.unread_mark;
        r0.markUnread = r2;
        goto L_0x0171;
    L_0x01a8:
        if (r2 != 0) goto L_0x01bd;
    L_0x01aa:
        r1 = r1 & 4096;
        if (r1 == 0) goto L_0x01bd;
    L_0x01ae:
        r1 = r0.message;
        if (r1 == 0) goto L_0x01bd;
    L_0x01b2:
        r6 = r0.lastSendState;
        r1 = r1.messageOwner;
        r1 = r1.send_state;
        if (r6 == r1) goto L_0x01bd;
    L_0x01ba:
        r0.lastSendState = r1;
        r2 = 1;
    L_0x01bd:
        if (r2 != 0) goto L_0x01c3;
    L_0x01bf:
        r19.invalidate();
        return;
    L_0x01c3:
        r0.user = r3;
        r0.chat = r3;
        r0.encryptedChat = r3;
        r1 = r0.currentDialogFolderId;
        r2 = 0;
        if (r1 == 0) goto L_0x01e2;
    L_0x01cf:
        r0.dialogMuted = r5;
        r1 = r19.findFolderTopMessage();
        r0.message = r1;
        r1 = r0.message;
        if (r1 == 0) goto L_0x01e0;
    L_0x01db:
        r6 = r1.getDialogId();
        goto L_0x01fb;
    L_0x01e0:
        r6 = r2;
        goto L_0x01fb;
    L_0x01e2:
        r1 = r0.isDialogCell;
        if (r1 == 0) goto L_0x01f6;
    L_0x01e6:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r6 = r0.currentDialogId;
        r1 = r1.isDialogMuted(r6);
        if (r1 == 0) goto L_0x01f6;
    L_0x01f4:
        r1 = 1;
        goto L_0x01f7;
    L_0x01f6:
        r1 = 0;
    L_0x01f7:
        r0.dialogMuted = r1;
        r6 = r0.currentDialogId;
    L_0x01fb:
        r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r1 == 0) goto L_0x02a0;
    L_0x01ff:
        r1 = (int) r6;
        r2 = 32;
        r2 = r6 >> r2;
        r3 = (int) r2;
        if (r1 == 0) goto L_0x0250;
    L_0x0207:
        if (r1 >= 0) goto L_0x023f;
    L_0x0209:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getChat(r1);
        r0.chat = r1;
        r1 = r0.isDialogCell;
        if (r1 != 0) goto L_0x0278;
    L_0x021e:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x0278;
    L_0x0222:
        r1 = r1.migrated_to;
        if (r1 == 0) goto L_0x0278;
    L_0x0226:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.chat;
        r2 = r2.migrated_to;
        r2 = r2.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        if (r1 == 0) goto L_0x0278;
    L_0x023c:
        r0.chat = r1;
        goto L_0x0278;
    L_0x023f:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getUser(r1);
        r0.user = r1;
        goto L_0x0278;
    L_0x0250:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = java.lang.Integer.valueOf(r3);
        r1 = r1.getEncryptedChat(r2);
        r0.encryptedChat = r1;
        r1 = r0.encryptedChat;
        if (r1 == 0) goto L_0x0278;
    L_0x0264:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.encryptedChat;
        r2 = r2.user_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        r0.user = r1;
    L_0x0278:
        r1 = r0.useMeForMyMessages;
        if (r1 == 0) goto L_0x02a0;
    L_0x027c:
        r1 = r0.user;
        if (r1 == 0) goto L_0x02a0;
    L_0x0280:
        r1 = r0.message;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x02a0;
    L_0x0288:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.clientUserId;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        r0.user = r1;
    L_0x02a0:
        r1 = r0.currentDialogFolderId;
        if (r1 == 0) goto L_0x02bd;
    L_0x02a4:
        r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveAvatarDrawable;
        r1.setCallback(r0);
        r1 = r0.avatarDrawable;
        r2 = 3;
        r1.setAvatarType(r2);
        r3 = r0.avatarImage;
        r4 = 0;
        r5 = 0;
        r6 = r0.avatarDrawable;
        r7 = 0;
        r8 = r0.user;
        r9 = 0;
        r3.setImage(r4, r5, r6, r7, r8, r9);
        goto L_0x0319;
    L_0x02bd:
        r1 = r0.user;
        if (r1 == 0) goto L_0x02fd;
    L_0x02c1:
        r2 = r0.avatarDrawable;
        r2.setInfo(r1);
        r1 = r0.user;
        r1 = org.telegram.messenger.UserObject.isUserSelf(r1);
        if (r1 == 0) goto L_0x02e5;
    L_0x02ce:
        r1 = r0.useMeForMyMessages;
        if (r1 != 0) goto L_0x02e5;
    L_0x02d2:
        r1 = r0.avatarDrawable;
        r1.setAvatarType(r4);
        r5 = r0.avatarImage;
        r6 = 0;
        r7 = 0;
        r8 = r0.avatarDrawable;
        r9 = 0;
        r10 = r0.user;
        r11 = 0;
        r5.setImage(r6, r7, r8, r9, r10, r11);
        goto L_0x0319;
    L_0x02e5:
        r12 = r0.avatarImage;
        r1 = r0.user;
        r13 = org.telegram.messenger.ImageLocation.getForUser(r1, r5);
        r15 = r0.avatarDrawable;
        r16 = 0;
        r1 = r0.user;
        r18 = 0;
        r14 = "50_50";
        r17 = r1;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        goto L_0x0319;
    L_0x02fd:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x0319;
    L_0x0301:
        r2 = r0.avatarDrawable;
        r2.setInfo(r1);
        r6 = r0.avatarImage;
        r1 = r0.chat;
        r7 = org.telegram.messenger.ImageLocation.getForChat(r1, r5);
        r9 = r0.avatarDrawable;
        r10 = 0;
        r11 = r0.chat;
        r12 = 0;
        r8 = "50_50";
        r6.setImage(r7, r8, r9, r10, r11, r12);
    L_0x0319:
        r1 = r19.getMeasuredWidth();
        if (r1 != 0) goto L_0x032a;
    L_0x031f:
        r1 = r19.getMeasuredHeight();
        if (r1 == 0) goto L_0x0326;
    L_0x0325:
        goto L_0x032a;
    L_0x0326:
        r19.requestLayout();
        goto L_0x032d;
    L_0x032a:
        r19.buildLayout();
    L_0x032d:
        r19.invalidate();
        return;
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

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a30  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x08d7  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x08bd  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x092a  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x095e  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x096f  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0976  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a30  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x095e  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x096f  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0976  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a30  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0410  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0518  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0566  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0625  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x05e8  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0673  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0839  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x084c  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0867  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x095e  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x096f  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0976  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a30  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0410  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0518  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0566  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x05e8  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0625  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0673  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0819  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0839  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x084c  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0867  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x095e  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x096f  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0976  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a44  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a30  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b1a  */
    /* JADX WARNING: Missing block: B:293:0x0841, code skipped:
            if (r0.isDraw() != false) goto L_0x0848;
     */
    @android.annotation.SuppressLint({"DrawAllocation"})
    public void onDraw(android.graphics.Canvas r24) {
        /*
        r23 = this;
        r1 = r23;
        r8 = r24;
        r2 = r1.currentDialogId;
        r4 = 0;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x0011;
    L_0x000c:
        r0 = r1.customDialog;
        if (r0 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r0 = r1.currentDialogFolderId;
        r9 = 0;
        if (r0 == 0) goto L_0x002a;
    L_0x0016:
        r0 = r1.archivedChatsDrawable;
        if (r0 == 0) goto L_0x002a;
    L_0x001a:
        r2 = r0.outProgress;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x002a;
    L_0x0020:
        r2 = r1.translationX;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x002a;
    L_0x0026:
        r0.draw(r8);
        return;
    L_0x002a:
        r2 = android.os.SystemClock.uptimeMillis();
        r4 = r1.lastUpdateTime;
        r4 = r2 - r4;
        r6 = 17;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 <= 0) goto L_0x003a;
    L_0x0038:
        r4 = 17;
    L_0x003a:
        r10 = r4;
        r1.lastUpdateTime = r2;
        r0 = r1.clipProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0069;
    L_0x0043:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x0069;
    L_0x0049:
        r24.save();
        r0 = r1.topClip;
        r0 = (float) r0;
        r2 = r1.clipProgress;
        r0 = r0 * r2;
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r23.getMeasuredHeight();
        r4 = r1.bottomClip;
        r4 = (float) r4;
        r5 = r1.clipProgress;
        r4 = r4 * r5;
        r4 = (int) r4;
        r3 = r3 - r4;
        r3 = (float) r3;
        r8.clipRect(r9, r0, r2, r3);
    L_0x0069:
        r0 = r1.translationX;
        r12 = 2;
        r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r14 = 0;
        r15 = 1;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x0098;
    L_0x0076:
        r0 = r1.cornerProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x007d;
    L_0x007c:
        goto L_0x0098;
    L_0x007d:
        r0 = r1.translationDrawable;
        if (r0 == 0) goto L_0x0094;
    L_0x0081:
        r0.stop();
        r0 = r1.translationDrawable;
        r0.setProgress(r9);
        r0 = r1.translationDrawable;
        r2 = 0;
        r0.setCallback(r2);
        r0 = 0;
        r1.translationDrawable = r0;
        r1.translationAnimationStarted = r14;
    L_0x0094:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x028e;
    L_0x0098:
        r24.save();
        r0 = r1.currentDialogFolderId;
        r16 = "chats_archiveBackground";
        r17 = "chats_archivePinBackground";
        if (r0 == 0) goto L_0x00d3;
    L_0x00a3:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x00bd;
    L_0x00a7:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0e0ac1 float:1.8880621E38 double:1.053163517E-314;
        r4 = "UnhideFromTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x0102;
    L_0x00bd:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0e0518 float:1.8877682E38 double:1.053162801E-314;
        r4 = "HideOnTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x0102;
    L_0x00d3:
        r0 = r1.folderId;
        if (r0 != 0) goto L_0x00ed;
    L_0x00d7:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0e00f8 float:1.887554E38 double:1.053162279E-314;
        r4 = "Archive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x0102;
    L_0x00ed:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0e0ab8 float:1.8880603E38 double:1.0531635123E-314;
        r4 = "Unarchive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable;
        r1.translationDrawable = r4;
    L_0x0102:
        r6 = r2;
        r5 = r3;
        r2 = r1.translationAnimationStarted;
        r18 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        if (r2 != 0) goto L_0x012a;
    L_0x010a:
        r2 = r1.translationX;
        r2 = java.lang.Math.abs(r2);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x012a;
    L_0x0119:
        r1.translationAnimationStarted = r15;
        r2 = r1.translationDrawable;
        r2.setProgress(r9);
        r2 = r1.translationDrawable;
        r2.setCallback(r1);
        r2 = r1.translationDrawable;
        r2.start();
    L_0x012a:
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r1.translationX;
        r4 = r2 + r3;
        r2 = r1.currentRevealProgress;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 >= 0) goto L_0x017b;
    L_0x0139:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2.setColor(r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r0 = (float) r0;
        r3 = r4 - r0;
        r0 = 0;
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r7 = r23.getMeasuredHeight();
        r7 = (float) r7;
        r20 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r21 = r2;
        r2 = r24;
        r15 = r4;
        r4 = r0;
        r0 = r5;
        r5 = r21;
        r22 = r6;
        r6 = r7;
        r7 = r20;
        r2.drawRect(r3, r4, r5, r6, r7);
        r2 = r1.currentRevealProgress;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x017f;
    L_0x0169:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored;
        if (r2 == 0) goto L_0x017f;
    L_0x016d:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r4 = "Arrow.**";
        r2.setLayerColor(r4, r3);
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14;
        goto L_0x017f;
    L_0x017b:
        r15 = r4;
        r0 = r5;
        r22 = r6;
    L_0x017f:
        r2 = r23.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r3;
        r3 = r1.translationDrawable;
        r3 = r3.getIntrinsicWidth();
        r3 = r3 / r12;
        r2 = r2 - r3;
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x019c;
    L_0x0194:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x0199;
    L_0x0198:
        goto L_0x019c;
    L_0x0199:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x019e;
    L_0x019c:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x019e:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.translationDrawable;
        r4 = r4.getIntrinsicWidth();
        r4 = r4 / r12;
        r4 = r4 + r2;
        r5 = r1.translationDrawable;
        r5 = r5.getIntrinsicHeight();
        r5 = r5 / r12;
        r5 = r5 + r3;
        r6 = r1.currentRevealProgress;
        r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r6 <= 0) goto L_0x0213;
    L_0x01b8:
        r24.save();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r6 = (float) r6;
        r6 = r15 - r6;
        r7 = r23.getMeasuredWidth();
        r7 = (float) r7;
        r13 = r23.getMeasuredHeight();
        r13 = (float) r13;
        r8.clipRect(r6, r9, r7, r13);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r7 = r22;
        r6.setColor(r7);
        r6 = r4 * r4;
        r7 = r23.getMeasuredHeight();
        r7 = r5 - r7;
        r13 = r23.getMeasuredHeight();
        r13 = r5 - r13;
        r7 = r7 * r13;
        r6 = r6 + r7;
        r6 = (double) r6;
        r6 = java.lang.Math.sqrt(r6);
        r6 = (float) r6;
        r4 = (float) r4;
        r5 = (float) r5;
        r7 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator;
        r13 = r1.currentRevealProgress;
        r7 = r7.getInterpolation(r13);
        r6 = r6 * r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r8.drawCircle(r4, r5, r6, r7);
        r24.restore();
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored;
        if (r4 != 0) goto L_0x0213;
    L_0x0205:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r6 = "Arrow.**";
        r4.setLayerColor(r6, r5);
        r4 = 1;
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r4;
    L_0x0213:
        r24.save();
        r2 = (float) r2;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.currentRevealBounceProgress;
        r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r3 == 0) goto L_0x0241;
    L_0x0223:
        r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1));
        if (r3 == 0) goto L_0x0241;
    L_0x0227:
        r3 = r1.interpolator;
        r2 = r3.getInterpolation(r2);
        r2 = r2 + r13;
        r3 = r1.translationDrawable;
        r3 = r3.getIntrinsicWidth();
        r3 = r3 / r12;
        r3 = (float) r3;
        r4 = r1.translationDrawable;
        r4 = r4.getIntrinsicHeight();
        r4 = r4 / r12;
        r4 = (float) r4;
        r8.scale(r2, r2, r3, r4);
    L_0x0241:
        r2 = r1.translationDrawable;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r14, r14);
        r2 = r1.translationDrawable;
        r2.draw(r8);
        r24.restore();
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r23.getMeasuredHeight();
        r3 = (float) r3;
        r8.clipRect(r15, r9, r2, r3);
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r2 = r2.measureText(r0);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r3 = r23.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r3 - r4;
        r2 = r2 / r12;
        r3 = r3 - r2;
        r2 = (float) r3;
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x027f;
    L_0x0277:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x027c;
    L_0x027b:
        goto L_0x027f;
    L_0x027c:
        r3 = NUM; // 0x426CLASSNAME float:59.0 double:5.50573981E-315;
        goto L_0x0281;
    L_0x027f:
        r3 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x0281:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint;
        r8.drawText(r0, r2, r3, r4);
        r24.restore();
    L_0x028e:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x029c;
    L_0x0294:
        r24.save();
        r0 = r1.translationX;
        r8.translate(r0, r9);
    L_0x029c:
        r0 = r1.isSelected;
        if (r0 == 0) goto L_0x02b3;
    L_0x02a0:
        r3 = 0;
        r4 = 0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x02b3:
        r0 = r1.currentDialogFolderId;
        r15 = "chats_pinnedOverlay";
        if (r0 == 0) goto L_0x02e6;
    L_0x02b9:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x02c3;
    L_0x02bd:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x02e6;
    L_0x02c3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3 = r1.archiveBackgroundProgress;
        r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r14, r2, r3, r13);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
        goto L_0x030a;
    L_0x02e6:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x02ee;
    L_0x02ea:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x030a;
    L_0x02ee:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x030a:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x031b;
    L_0x0310:
        r0 = r1.cornerProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0317;
    L_0x0316:
        goto L_0x031b;
    L_0x0317:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03cb;
    L_0x031b:
        r24.save();
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = "windowBackgroundWhite";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r0 = r1.rect;
        r2 = r23.getMeasuredWidth();
        r3 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r2 = (float) r2;
        r3 = r23.getMeasuredWidth();
        r3 = (float) r3;
        r4 = r23.getMeasuredHeight();
        r4 = (float) r4;
        r0.set(r2, r9, r3, r4);
        r0 = r1.rect;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r4 = r1.cornerProgress;
        r3 = r3 * r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r4;
        r4 = r1.cornerProgress;
        r2 = r2 * r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r8.drawRoundRect(r0, r3, r2, r4);
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0398;
    L_0x0363:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x036d;
    L_0x0367:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0398;
    L_0x036d:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3 = r1.archiveBackgroundProgress;
        r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r14, r2, r3, r13);
        r0.setColor(r2);
        r0 = r1.rect;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r4 = r1.cornerProgress;
        r3 = r3 * r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r4;
        r4 = r1.cornerProgress;
        r2 = r2 * r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r8.drawRoundRect(r0, r3, r2, r4);
        goto L_0x03a1;
    L_0x0398:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x03a4;
    L_0x039c:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x03a1;
    L_0x03a0:
        goto L_0x03a4;
    L_0x03a1:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03c8;
    L_0x03a4:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setColor(r2);
        r0 = r1.rect;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r4 = r1.cornerProgress;
        r3 = r3 * r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = (float) r4;
        r5 = r1.cornerProgress;
        r4 = r4 * r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r8.drawRoundRect(r0, r3, r4, r5);
    L_0x03c8:
        r24.restore();
    L_0x03cb:
        r0 = r1.translationX;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x03e7;
    L_0x03d3:
        r0 = r1.cornerProgress;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 >= 0) goto L_0x03fc;
    L_0x03d9:
        r4 = (float) r10;
        r4 = r4 / r3;
        r0 = r0 + r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x03fa;
    L_0x03e4:
        r1.cornerProgress = r13;
        goto L_0x03fa;
    L_0x03e7:
        r0 = r1.cornerProgress;
        r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r4 <= 0) goto L_0x03fc;
    L_0x03ed:
        r4 = (float) r10;
        r4 = r4 / r3;
        r0 = r0 - r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x03fa;
    L_0x03f8:
        r1.cornerProgress = r9;
    L_0x03fa:
        r4 = 1;
        goto L_0x03fd;
    L_0x03fc:
        r4 = 0;
    L_0x03fd:
        r0 = r1.drawNameLock;
        if (r0 == 0) goto L_0x0410;
    L_0x0401:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r0.draw(r8);
        goto L_0x0448;
    L_0x0410:
        r0 = r1.drawNameGroup;
        if (r0 == 0) goto L_0x0423;
    L_0x0414:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r0.draw(r8);
        goto L_0x0448;
    L_0x0423:
        r0 = r1.drawNameBroadcast;
        if (r0 == 0) goto L_0x0436;
    L_0x0427:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r0.draw(r8);
        goto L_0x0448;
    L_0x0436:
        r0 = r1.drawNameBot;
        if (r0 == 0) goto L_0x0448;
    L_0x043a:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r0.draw(r8);
    L_0x0448:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x04aa;
    L_0x044c:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x045e;
    L_0x0450:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_nameArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0486;
    L_0x045e:
        r0 = r1.encryptedChat;
        if (r0 != 0) goto L_0x0479;
    L_0x0462:
        r0 = r1.customDialog;
        if (r0 == 0) goto L_0x046b;
    L_0x0466:
        r0 = r0.type;
        if (r0 != r12) goto L_0x046b;
    L_0x046a:
        goto L_0x0479;
    L_0x046b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_name";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0486;
    L_0x0479:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_secretName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0486:
        r24.save();
        r0 = r1.nameLeft;
        r0 = (float) r0;
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x0498;
    L_0x0490:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x0495;
    L_0x0494:
        goto L_0x0498;
    L_0x0495:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        goto L_0x049a;
    L_0x0498:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x049a:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.nameLayout;
        r0.draw(r8);
        r24.restore();
    L_0x04aa:
        r0 = r1.timeLayout;
        if (r0 == 0) goto L_0x04c6;
    L_0x04ae:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x04c6;
    L_0x04b2:
        r24.save();
        r0 = r1.timeLeft;
        r0 = (float) r0;
        r5 = r1.timeTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.timeLayout;
        r0.draw(r8);
        r24.restore();
    L_0x04c6:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x0514;
    L_0x04ca:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x04dc;
    L_0x04ce:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessageArchived_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x04fb;
    L_0x04dc:
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x04ee;
    L_0x04e0:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_draft";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x04fb;
    L_0x04ee:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessage_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x04fb:
        r24.save();
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r5 = r1.messageNameTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageNameLayout;	 Catch:{ Exception -> 0x050d }
        r0.draw(r8);	 Catch:{ Exception -> 0x050d }
        goto L_0x0511;
    L_0x050d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0511:
        r24.restore();
    L_0x0514:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x0562;
    L_0x0518:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x053c;
    L_0x051c:
        r0 = r1.chat;
        if (r0 == 0) goto L_0x052e;
    L_0x0520:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_nameMessageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0549;
    L_0x052e:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_messageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0549;
    L_0x053c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_message";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0549:
        r24.save();
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r5 = r1.messageTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageLayout;	 Catch:{ Exception -> 0x055b }
        r0.draw(r8);	 Catch:{ Exception -> 0x055b }
        goto L_0x055f;
    L_0x055b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x055f:
        r24.restore();
    L_0x0562:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x05ac;
    L_0x0566:
        r0 = r1.drawClock;
        if (r0 == 0) goto L_0x0579;
    L_0x056a:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r0.draw(r8);
        goto L_0x05ac;
    L_0x0579:
        r0 = r1.drawCheck2;
        if (r0 == 0) goto L_0x05ac;
    L_0x057d:
        r0 = r1.drawCheck1;
        if (r0 == 0) goto L_0x059e;
    L_0x0581:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r5 = r1.halfCheckDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkReadDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkReadDrawable;
        r0.draw(r8);
        goto L_0x05ac;
    L_0x059e:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r0.draw(r8);
    L_0x05ac:
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x05e4;
    L_0x05b0:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x05e4;
    L_0x05b4:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x05e4;
    L_0x05b8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05c8;
    L_0x05c0:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05c5;
    L_0x05c4:
        goto L_0x05c8;
    L_0x05c5:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x05c9;
    L_0x05c8:
        r6 = 0;
    L_0x05c9:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05d5;
    L_0x05d2:
        r6 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        goto L_0x05d7;
    L_0x05d5:
        r6 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
    L_0x05d7:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r0.draw(r8);
        goto L_0x0647;
    L_0x05e4:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0625;
    L_0x05e8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05f8;
    L_0x05f0:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05f5;
    L_0x05f4:
        goto L_0x05f8;
    L_0x05f5:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x05fa;
    L_0x05f8:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x05fa:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x0611;
    L_0x0609:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x060e;
    L_0x060d:
        goto L_0x0611;
    L_0x060e:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x0613;
    L_0x0611:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x0613:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r0.draw(r8);
        goto L_0x0647;
    L_0x0625:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0647;
    L_0x0629:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x0639;
    L_0x0631:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x0636;
    L_0x0635:
        goto L_0x0639;
    L_0x0636:
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        goto L_0x063b;
    L_0x0639:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x063b:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r0.draw(r8);
    L_0x0647:
        r0 = r1.drawReorder;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 != 0) goto L_0x0653;
    L_0x064d:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x066b;
    L_0x0653:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable;
        r6 = r1.reorderIconProgress;
        r6 = r6 * r5;
        r6 = (int) r6;
        r0.setAlpha(r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable;
        r6 = r1.pinLeft;
        r7 = r1.pinTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r6, r7);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable;
        r0.draw(r8);
    L_0x066b:
        r0 = r1.drawError;
        r6 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r7 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        if (r0 == 0) goto L_0x06c4;
    L_0x0673:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r12 = r1.reorderIconProgress;
        r12 = r13 - r12;
        r12 = r12 * r5;
        r5 = (int) r12;
        r0.setAlpha(r5);
        r0 = r1.rect;
        r5 = r1.errorLeft;
        r12 = (float) r5;
        r15 = r1.errorTop;
        r15 = (float) r15;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r16;
        r5 = (float) r5;
        r2 = r1.errorTop;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 + r6;
        r2 = (float) r2;
        r0.set(r12, r15, r5, r2);
        r0 = r1.rect;
        r2 = org.telegram.messenger.AndroidUtilities.density;
        r5 = r2 * r7;
        r2 = r2 * r7;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint;
        r8.drawRoundRect(r0, r5, r2, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r2 = r1.errorLeft;
        r5 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        r5 = r1.errorTop;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r5);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r0.draw(r8);
        goto L_0x0813;
    L_0x06c4:
        r0 = r1.drawCount;
        if (r0 != 0) goto L_0x06ed;
    L_0x06c8:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x06cd;
    L_0x06cc:
        goto L_0x06ed;
    L_0x06cd:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0813;
    L_0x06d1:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r2 = r1.reorderIconProgress;
        r7 = r13 - r2;
        r7 = r7 * r5;
        r2 = (int) r7;
        r0.setAlpha(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r2 = r1.pinLeft;
        r5 = r1.pinTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r5);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0.draw(r8);
        goto L_0x0813;
    L_0x06ed:
        r0 = r1.drawCount;
        if (r0 == 0) goto L_0x0765;
    L_0x06f1:
        r0 = r1.dialogMuted;
        if (r0 != 0) goto L_0x06fd;
    L_0x06f5:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x06fa;
    L_0x06f9:
        goto L_0x06fd;
    L_0x06fa:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        goto L_0x06ff;
    L_0x06fd:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
    L_0x06ff:
        r2 = r1.reorderIconProgress;
        r2 = r13 - r2;
        r2 = r2 * r5;
        r2 = (int) r2;
        r0.setAlpha(r2);
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r12 = r1.reorderIconProgress;
        r12 = r13 - r12;
        r12 = r12 * r5;
        r12 = (int) r12;
        r2.setAlpha(r12);
        r2 = r1.countLeft;
        r12 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r2 = r2 - r12;
        r12 = r1.rect;
        r15 = (float) r2;
        r14 = r1.countTop;
        r14 = (float) r14;
        r3 = r1.countWidth;
        r2 = r2 + r3;
        r3 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r3 = r1.countTop;
        r19 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = r3 + r19;
        r3 = (float) r3;
        r12.set(r15, r14, r2, r3);
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.density;
        r12 = r3 * r7;
        r3 = r3 * r7;
        r8.drawRoundRect(r2, r12, r3, r0);
        r0 = r1.countLayout;
        if (r0 == 0) goto L_0x0765;
    L_0x074a:
        r24.save();
        r0 = r1.countLeft;
        r0 = (float) r0;
        r2 = r1.countTop;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r8.translate(r0, r2);
        r0 = r1.countLayout;
        r0.draw(r8);
        r24.restore();
    L_0x0765:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x0813;
    L_0x0769:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        r2 = r1.reorderIconProgress;
        r2 = r13 - r2;
        r2 = r2 * r5;
        r2 = (int) r2;
        r0.setAlpha(r2);
        r0 = r1.mentionLeft;
        r2 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.rect;
        r3 = (float) r0;
        r12 = r1.countTop;
        r12 = (float) r12;
        r14 = r1.mentionWidth;
        r0 = r0 + r14;
        r14 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r0 = r0 + r14;
        r0 = (float) r0;
        r14 = r1.countTop;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = r14 + r6;
        r6 = (float) r14;
        r2.set(r3, r12, r0, r6);
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x07a5;
    L_0x079e:
        r0 = r1.folderId;
        if (r0 == 0) goto L_0x07a5;
    L_0x07a2:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
        goto L_0x07a7;
    L_0x07a5:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
    L_0x07a7:
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r3 * r7;
        r3 = r3 * r7;
        r8.drawRoundRect(r2, r6, r3, r0);
        r0 = r1.mentionLayout;
        if (r0 == 0) goto L_0x07de;
    L_0x07b6:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r2 = r1.reorderIconProgress;
        r7 = r13 - r2;
        r7 = r7 * r5;
        r2 = (int) r7;
        r0.setAlpha(r2);
        r24.save();
        r0 = r1.mentionLeft;
        r0 = (float) r0;
        r2 = r1.countTop;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r8.translate(r0, r2);
        r0 = r1.mentionLayout;
        r0.draw(r8);
        r24.restore();
        goto L_0x0813;
    L_0x07de:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r2 = r1.reorderIconProgress;
        r7 = r13 - r2;
        r7 = r7 * r5;
        r2 = (int) r7;
        r0.setAlpha(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r2 = r1.mentionLeft;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r1.countTop;
        r5 = NUM; // 0x404ccccd float:3.2 double:5.329856617E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        r5 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r3, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r0.draw(r8);
    L_0x0813:
        r0 = r1.animatingArchiveAvatar;
        r12 = NUM; // 0x432a0000 float:170.0 double:5.567260075E-315;
        if (r0 == 0) goto L_0x0835;
    L_0x0819:
        r24.save();
        r0 = r1.interpolator;
        r2 = r1.animatingArchiveAvatarProgress;
        r2 = r2 / r12;
        r0 = r0.getInterpolation(r2);
        r0 = r0 + r13;
        r2 = r1.avatarImage;
        r2 = r2.getCenterX();
        r3 = r1.avatarImage;
        r3 = r3.getCenterY();
        r8.scale(r0, r0, r2, r3);
    L_0x0835:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0843;
    L_0x0839:
        r0 = r1.archivedChatsDrawable;
        if (r0 == 0) goto L_0x0843;
    L_0x083d:
        r0 = r0.isDraw();
        if (r0 != 0) goto L_0x0848;
    L_0x0843:
        r0 = r1.avatarImage;
        r0.draw(r8);
    L_0x0848:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x084f;
    L_0x084c:
        r24.restore();
    L_0x084f:
        r0 = r1.user;
        if (r0 == 0) goto L_0x0957;
    L_0x0853:
        r2 = r1.isDialogCell;
        if (r2 == 0) goto L_0x0957;
    L_0x0857:
        r2 = r1.currentDialogFolderId;
        if (r2 != 0) goto L_0x0957;
    L_0x085b:
        r0 = org.telegram.messenger.MessagesController.isSupportUser(r0);
        if (r0 != 0) goto L_0x0957;
    L_0x0861:
        r0 = r1.user;
        r2 = r0.bot;
        if (r2 != 0) goto L_0x0957;
    L_0x0867:
        r2 = r0.self;
        if (r2 != 0) goto L_0x0895;
    L_0x086b:
        r0 = r0.status;
        if (r0 == 0) goto L_0x087d;
    L_0x086f:
        r0 = r0.expires;
        r2 = r1.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        if (r0 > r2) goto L_0x0893;
    L_0x087d:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.onlinePrivacy;
        r2 = r1.user;
        r2 = r2.id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.containsKey(r2);
        if (r0 == 0) goto L_0x0895;
    L_0x0893:
        r0 = 1;
        goto L_0x0896;
    L_0x0895:
        r0 = 0;
    L_0x0896:
        if (r0 != 0) goto L_0x089e;
    L_0x0898:
        r2 = r1.onlineProgress;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x0957;
    L_0x089e:
        r2 = r1.avatarImage;
        r2 = r2.getImageY2();
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x08b0;
    L_0x08a8:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x08ad;
    L_0x08ac:
        goto L_0x08b0;
    L_0x08ad:
        r16 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x08b4;
    L_0x08b0:
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
    L_0x08b4:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r2 - r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x08d7;
    L_0x08bd:
        r3 = r1.avatarImage;
        r3 = r3.getImageX();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x08cf;
    L_0x08c7:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x08cc;
    L_0x08cb:
        goto L_0x08cf;
    L_0x08cc:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x08d1;
    L_0x08cf:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x08d1:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        goto L_0x08f0;
    L_0x08d7:
        r3 = r1.avatarImage;
        r3 = r3.getImageX2();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x08e9;
    L_0x08e1:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x08e6;
    L_0x08e5:
        goto L_0x08e9;
    L_0x08e6:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x08eb;
    L_0x08e9:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x08eb:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 - r5;
    L_0x08f0:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r6 = "windowBackgroundWhite";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setColor(r6);
        r3 = (float) r3;
        r2 = (float) r2;
        r5 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r1.onlineProgress;
        r5 = r5 * r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r8.drawCircle(r3, r2, r5, r6);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r6 = "chats_onlineCircle";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setColor(r6);
        r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r1.onlineProgress;
        r5 = r5 * r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r8.drawCircle(r3, r2, r5, r6);
        if (r0 == 0) goto L_0x0940;
    L_0x092a:
        r0 = r1.onlineProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0957;
    L_0x0930:
        r2 = (float) r10;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0955;
    L_0x093d:
        r1.onlineProgress = r13;
        goto L_0x0955;
    L_0x0940:
        r0 = r1.onlineProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0957;
    L_0x0946:
        r2 = (float) r10;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0955;
    L_0x0953:
        r1.onlineProgress = r9;
    L_0x0955:
        r15 = 1;
        goto L_0x0958;
    L_0x0957:
        r15 = r4;
    L_0x0958:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0961;
    L_0x095e:
        r24.restore();
    L_0x0961:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0972;
    L_0x0965:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x0972;
    L_0x096b:
        r0 = r1.archivedChatsDrawable;
        if (r0 == 0) goto L_0x0972;
    L_0x096f:
        r0.draw(r8);
    L_0x0972:
        r0 = r1.useSeparator;
        if (r0 == 0) goto L_0x09d2;
    L_0x0976:
        r0 = r1.fullSeparator;
        if (r0 != 0) goto L_0x0996;
    L_0x097a:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0986;
    L_0x097e:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0986;
    L_0x0982:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x0996;
    L_0x0986:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x098f;
    L_0x098a:
        r0 = r1.archiveHidden;
        if (r0 != 0) goto L_0x098f;
    L_0x098e:
        goto L_0x0996;
    L_0x098f:
        r0 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r0);
        goto L_0x0997;
    L_0x0996:
        r14 = 0;
    L_0x0997:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x09b7;
    L_0x099b:
        r3 = 0;
        r0 = r23.getMeasuredHeight();
        r2 = 1;
        r0 = r0 - r2;
        r4 = (float) r0;
        r0 = r23.getMeasuredWidth();
        r0 = r0 - r14;
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r0 = r0 - r2;
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dividerPaint;
        r2 = r24;
        r2.drawLine(r3, r4, r5, r6, r7);
        goto L_0x09d2;
    L_0x09b7:
        r3 = (float) r14;
        r0 = r23.getMeasuredHeight();
        r14 = 1;
        r0 = r0 - r14;
        r4 = (float) r0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r0 = r0 - r14;
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dividerPaint;
        r2 = r24;
        r2.drawLine(r3, r4, r5, r6, r7);
        goto L_0x09d3;
    L_0x09d2:
        r14 = 1;
    L_0x09d3:
        r0 = r1.clipProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0a22;
    L_0x09d9:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x09e3;
    L_0x09df:
        r24.restore();
        goto L_0x0a22;
    L_0x09e3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = "windowBackgroundWhite";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r1.topClip;
        r0 = (float) r0;
        r2 = r1.clipProgress;
        r6 = r0 * r2;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
        r0 = r23.getMeasuredHeight();
        r2 = r1.bottomClip;
        r2 = (float) r2;
        r4 = r1.clipProgress;
        r2 = r2 * r4;
        r2 = (int) r2;
        r0 = r0 - r2;
        r4 = (float) r0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x0a22:
        r0 = r1.drawReorder;
        if (r0 != 0) goto L_0x0a2c;
    L_0x0a26:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0a58;
    L_0x0a2c:
        r0 = r1.drawReorder;
        if (r0 == 0) goto L_0x0a44;
    L_0x0a30:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0a58;
    L_0x0a36:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a57;
    L_0x0a41:
        r1.reorderIconProgress = r13;
        goto L_0x0a57;
    L_0x0a44:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a58;
    L_0x0a4a:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a57;
    L_0x0a55:
        r1.reorderIconProgress = r9;
    L_0x0a57:
        r15 = 1;
    L_0x0a58:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0a88;
    L_0x0a5c:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ab4;
    L_0x0a62:
        r2 = (float) r10;
        r3 = NUM; // 0x43660000 float:230.0 double:5.586687527E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a71;
    L_0x0a6f:
        r1.archiveBackgroundProgress = r9;
    L_0x0a71:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0ab3;
    L_0x0a7a:
        r0 = r1.avatarDrawable;
        r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r3 = r1.archiveBackgroundProgress;
        r2 = r2.getInterpolation(r3);
        r0.setArchivedAvatarHiddenProgress(r2);
        goto L_0x0ab3;
    L_0x0a88:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0ab4;
    L_0x0a8e:
        r2 = (float) r10;
        r3 = NUM; // 0x43660000 float:230.0 double:5.586687527E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a9d;
    L_0x0a9b:
        r1.archiveBackgroundProgress = r13;
    L_0x0a9d:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0ab3;
    L_0x0aa6:
        r0 = r1.avatarDrawable;
        r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r3 = r1.archiveBackgroundProgress;
        r2 = r2.getInterpolation(r3);
        r0.setArchivedAvatarHiddenProgress(r2);
    L_0x0ab3:
        r15 = 1;
    L_0x0ab4:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0aca;
    L_0x0ab8:
        r0 = r1.animatingArchiveAvatarProgress;
        r2 = (float) r10;
        r0 = r0 + r2;
        r1.animatingArchiveAvatarProgress = r0;
        r0 = r1.animatingArchiveAvatarProgress;
        r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1));
        if (r0 < 0) goto L_0x0ac9;
    L_0x0ac4:
        r1.animatingArchiveAvatarProgress = r12;
        r2 = 0;
        r1.animatingArchiveAvatar = r2;
    L_0x0ac9:
        r15 = 1;
    L_0x0aca:
        r0 = r1.drawRevealBackground;
        if (r0 == 0) goto L_0x0af8;
    L_0x0ace:
        r0 = r1.currentRevealBounceProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0ae2;
    L_0x0ad4:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.currentRevealBounceProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0ae2;
    L_0x0adf:
        r1.currentRevealBounceProgress = r13;
        r15 = 1;
    L_0x0ae2:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b17;
    L_0x0ae8:
        r2 = (float) r10;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0b18;
    L_0x0af5:
        r1.currentRevealProgress = r13;
        goto L_0x0b18;
    L_0x0af8:
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x0b01;
    L_0x0afe:
        r1.currentRevealBounceProgress = r9;
        r15 = 1;
    L_0x0b01:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b17;
    L_0x0b07:
        r2 = (float) r10;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0b18;
    L_0x0b14:
        r1.currentRevealProgress = r9;
        goto L_0x0b18;
    L_0x0b17:
        r14 = r15;
    L_0x0b18:
        if (r14 == 0) goto L_0x0b1d;
    L_0x0b1a:
        r23.invalidate();
    L_0x0b1d:
        return;
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
            return;
        }
        if (!this.drawPin) {
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
        User user;
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        StringBuilder stringBuilder = new StringBuilder();
        String str = ". ";
        if (this.currentDialogFolderId == 1) {
            stringBuilder.append(LocaleController.getString("ArchivedChats", NUM));
            stringBuilder.append(str);
        } else {
            if (this.encryptedChat != null) {
                stringBuilder.append(LocaleController.getString("AccDescrSecretChat", NUM));
                stringBuilder.append(str);
            }
            user = this.user;
            if (user != null) {
                if (user.bot) {
                    stringBuilder.append(LocaleController.getString("Bot", NUM));
                    stringBuilder.append(str);
                }
                user = this.user;
                if (user.self) {
                    stringBuilder.append(LocaleController.getString("SavedMessages", NUM));
                } else {
                    stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                }
                stringBuilder.append(str);
            } else {
                Chat chat = this.chat;
                if (chat != null) {
                    if (chat.broadcast) {
                        stringBuilder.append(LocaleController.getString("AccDescrChannel", NUM));
                    } else {
                        stringBuilder.append(LocaleController.getString("AccDescrGroup", NUM));
                    }
                    stringBuilder.append(str);
                    stringBuilder.append(this.chat.title);
                    stringBuilder.append(str);
                }
            }
        }
        int i = this.unreadCount;
        if (i > 0) {
            stringBuilder.append(LocaleController.formatPluralString("NewMessages", i));
            stringBuilder.append(str);
        }
        MessageObject messageObject = this.message;
        if (messageObject == null || this.currentDialogFolderId != 0) {
            accessibilityEvent.setContentDescription(stringBuilder.toString());
            return;
        }
        int i2 = this.lastMessageDate;
        if (i2 == 0 && messageObject != null) {
            i2 = messageObject.messageOwner.date;
        }
        String formatDateAudio = LocaleController.formatDateAudio((long) i2);
        if (this.message.isOut()) {
            stringBuilder.append(LocaleController.formatString("AccDescrSentDate", NUM, formatDateAudio));
        } else {
            stringBuilder.append(LocaleController.formatString("AccDescrReceivedDate", NUM, formatDateAudio));
        }
        stringBuilder.append(str);
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.message.messageOwner.from_id));
            if (user != null) {
                stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                stringBuilder.append(str);
            }
        }
        if (this.encryptedChat == null) {
            stringBuilder.append(this.message.messageText);
            if (!(this.message.isMediaEmpty() || TextUtils.isEmpty(this.message.caption))) {
                stringBuilder.append(str);
                stringBuilder.append(this.message.caption);
            }
        }
        accessibilityEvent.setContentDescription(stringBuilder.toString());
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
