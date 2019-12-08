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
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.DialogsActivity;

public class DialogCell extends BaseCell {
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
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

    public void setDialog(long j, MessageObject messageObject, int i) {
        this.currentDialogId = j;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = i;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        this.markUnread = false;
        this.messageId = messageObject != null ? messageObject.getId() : 0;
        this.mentionCount = 0;
        boolean z = messageObject != null && messageObject.isUnread();
        this.lastUnreadState = z;
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

    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ac1  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ac1  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0bac  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0b90  */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x0b86  */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x0b86  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0b90  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0bac  */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x0ad7  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0acf  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0b04  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0af4  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0c2a  */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x11fd  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11b1 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x11e2 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x11df A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x11fd  */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0d58  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e15  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0dff  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0f3f  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0fbc  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0f8e  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x10e3  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x118c A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11b1 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x11df A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x11e2 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x11fd  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x0cb4  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0cce  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ce6  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0d58  */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0ddd  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0dff  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e15  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0e6b  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0f3f  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0f8e  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0fbc  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x10e3  */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x1126  */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x117f A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x118c A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x1196 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11b1 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x11e2 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x11df A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x11fd  */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x0cb4  */
    /* JADX WARNING: Removed duplicated region for block: B:588:0x0cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0cce  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ce6  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0d58  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0ddd  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0e15  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0dff  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0e6b  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0f3f  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0fbc  */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0f8e  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x10e3  */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x1126  */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x117f A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x118c A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x1196 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x11b1 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x11df A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x11e2 A:{Catch:{ Exception -> 0x11f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x11fd  */
    /* JADX WARNING: Missing block: B:253:0x05e1, code skipped:
            if (r6.post_messages != false) goto L_0x05e3;
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
        r5 = r5 ^ r6;
        r7 = android.os.Build.VERSION.SDK_INT;
        r8 = 18;
        if (r7 < r8) goto L_0x00cf;
    L_0x00bd:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x00c5;
    L_0x00c1:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x00c9;
    L_0x00c5:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x00cc;
    L_0x00c9:
        r7 = "%2$s: ⁨%1$s⁩";
        goto L_0x00dd;
    L_0x00cc:
        r7 = "⁨%s⁩";
        goto L_0x00e1;
    L_0x00cf:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x00d7;
    L_0x00d3:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x00db;
    L_0x00d7:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x00df;
    L_0x00db:
        r7 = "%2$s: %1$s";
    L_0x00dd:
        r8 = 1;
        goto L_0x00e2;
    L_0x00df:
        r7 = "%1$s";
    L_0x00e1:
        r8 = 0;
    L_0x00e2:
        r9 = r1.message;
        if (r9 == 0) goto L_0x00e9;
    L_0x00e6:
        r9 = r9.messageText;
        goto L_0x00ea;
    L_0x00e9:
        r9 = 0;
    L_0x00ea:
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
        if (r9 == 0) goto L_0x0338;
    L_0x0101:
        r0 = r9.type;
        if (r0 != r3) goto L_0x018a;
    L_0x0105:
        r1.drawNameLock = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x014d;
    L_0x010b:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0110;
    L_0x010f:
        goto L_0x014d;
    L_0x0110:
        r0 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0133;
    L_0x011c:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0133:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x014d:
        r0 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0170;
    L_0x0159:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0170:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x018a:
        r5 = r9.verified;
        r1.drawVerified = r5;
        r5 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r5 == 0) goto L_0x0233;
    L_0x0192:
        if (r0 != r6) goto L_0x0233;
    L_0x0194:
        r1.drawNameGroup = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x01ea;
    L_0x019a:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x019f;
    L_0x019e:
        goto L_0x01ea;
    L_0x019f:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01c9;
    L_0x01a3:
        r0 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x01be;
    L_0x01bb:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01c0;
    L_0x01be:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01c0:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x01c9:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r5;
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x01d9;
    L_0x01d6:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01db;
    L_0x01d9:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01db:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x01ea:
        r0 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0213;
    L_0x01f6:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x0209;
    L_0x0206:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x020b;
    L_0x0209:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x020b:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0213:
        r0 = r33.getMeasuredWidth();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r5;
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x0223;
    L_0x0220:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0225;
    L_0x0223:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0225:
        r5 = r5.getIntrinsicWidth();
        r0 = r0 - r5;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0233:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x024e;
    L_0x0237:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x023c;
    L_0x023b:
        goto L_0x024e;
    L_0x023c:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0247;
    L_0x0240:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0247:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x024e:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0259;
    L_0x0252:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x025f;
    L_0x0259:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
    L_0x025f:
        r0 = r1.customDialog;
        r5 = r0.type;
        if (r5 != r6) goto L_0x02e5;
    L_0x0265:
        r0 = NUM; // 0x7f0d04ce float:1.874461E38 double:1.053130385E-314;
        r5 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r5 = r1.customDialog;
        r8 = r5.isMedia;
        if (r8 == 0) goto L_0x029b;
    L_0x0274:
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
        goto L_0x02d1;
    L_0x029b:
        r5 = r5.message;
        r8 = r5.length();
        if (r8 <= r14) goto L_0x02a7;
    L_0x02a3:
        r5 = r5.substring(r2, r14);
    L_0x02a7:
        r8 = r1.useForceThreeLines;
        if (r8 != 0) goto L_0x02c3;
    L_0x02ab:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x02b0;
    L_0x02af:
        goto L_0x02c3;
    L_0x02b0:
        r8 = new java.lang.Object[r3];
        r5 = r5.replace(r11, r10);
        r8[r2] = r5;
        r8[r6] = r0;
        r5 = java.lang.String.format(r7, r8);
        r5 = android.text.SpannableStringBuilder.valueOf(r5);
        goto L_0x02d1;
    L_0x02c3:
        r8 = new java.lang.Object[r3];
        r8[r2] = r5;
        r8[r6] = r0;
        r5 = java.lang.String.format(r7, r8);
        r5 = android.text.SpannableStringBuilder.valueOf(r5);
    L_0x02d1:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r7 = r7.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r5 = org.telegram.messenger.Emoji.replaceEmoji(r5, r7, r9, r2);
        r7 = r4;
        r4 = r0;
        r0 = 0;
        goto L_0x02f0;
    L_0x02e5:
        r5 = r0.message;
        r0 = r0.isMedia;
        if (r0 == 0) goto L_0x02ed;
    L_0x02eb:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x02ed:
        r7 = r4;
        r0 = 1;
        r4 = 0;
    L_0x02f0:
        r8 = r1.customDialog;
        r8 = r8.date;
        r8 = (long) r8;
        r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8);
        r9 = r1.customDialog;
        r9 = r9.unread_count;
        if (r9 == 0) goto L_0x0310;
    L_0x02ff:
        r1.drawCount = r6;
        r10 = new java.lang.Object[r6];
        r9 = java.lang.Integer.valueOf(r9);
        r10[r2] = r9;
        r9 = "%d";
        r9 = java.lang.String.format(r9, r10);
        goto L_0x0313;
    L_0x0310:
        r1.drawCount = r2;
        r9 = 0;
    L_0x0313:
        r10 = r1.customDialog;
        r10 = r10.sent;
        if (r10 == 0) goto L_0x0322;
    L_0x0319:
        r1.drawCheck1 = r6;
        r1.drawCheck2 = r6;
        r1.drawClock = r2;
        r1.drawError = r2;
        goto L_0x032a;
    L_0x0322:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r2;
    L_0x032a:
        r10 = r1.customDialog;
        r10 = r10.name;
        r13 = r7;
        r3 = r9;
        r9 = r10;
        r10 = 0;
        r7 = r5;
        r5 = r8;
        r8 = r4;
    L_0x0335:
        r4 = r0;
        goto L_0x0CLASSNAME;
    L_0x0338:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x0353;
    L_0x033c:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0341;
    L_0x0340:
        goto L_0x0353;
    L_0x0341:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x034c;
    L_0x0345:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r9;
        goto L_0x0364;
    L_0x034c:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x0364;
    L_0x0353:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x035e;
    L_0x0357:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r9;
        goto L_0x0364;
    L_0x035e:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x0364:
        r9 = r1.encryptedChat;
        if (r9 == 0) goto L_0x03f1;
    L_0x0368:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x058a;
    L_0x036c:
        r1.drawNameLock = r6;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x03b4;
    L_0x0372:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0377;
    L_0x0376:
        goto L_0x03b4;
    L_0x0377:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x039a;
    L_0x0383:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x039a:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x03b4:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x03d7;
    L_0x03c0:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x03d7:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x03f1:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x058a;
    L_0x03f5:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x04ec;
    L_0x03f9:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x0405;
    L_0x03fd:
        r1.drawScam = r6;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0409;
    L_0x0405:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0409:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x058a;
    L_0x040d:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x0481;
    L_0x0411:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0416;
    L_0x0415:
        goto L_0x0481;
    L_0x0416:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x0434;
    L_0x041c:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x0429;
    L_0x0422:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x0429;
    L_0x0428:
        goto L_0x0434;
    L_0x0429:
        r1.drawNameGroup = r6;
        r9 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x043e;
    L_0x0434:
        r1.drawNameBroadcast = r6;
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x043e:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0460;
    L_0x0442:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x0455;
    L_0x0452:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0457;
    L_0x0455:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0457:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x0460:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x0470;
    L_0x046d:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0472;
    L_0x0470:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0472:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x0481:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x049f;
    L_0x0487:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x0494;
    L_0x048d:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x0494;
    L_0x0493:
        goto L_0x049f;
    L_0x0494:
        r1.drawNameGroup = r6;
        r9 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x04a9;
    L_0x049f:
        r1.drawNameBroadcast = r6;
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x04a9:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x04cb;
    L_0x04ad:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04c0;
    L_0x04bd:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04c2;
    L_0x04c0:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04c2:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x04cb:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04db;
    L_0x04d8:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04dd;
    L_0x04db:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04dd:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x04ec:
        r9 = r1.user;
        if (r9 == 0) goto L_0x058a;
    L_0x04f0:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x04fc;
    L_0x04f4:
        r1.drawScam = r6;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0500;
    L_0x04fc:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0500:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x058a;
    L_0x0504:
        r9 = r1.user;
        r9 = r9.bot;
        if (r9 == 0) goto L_0x058a;
    L_0x050a:
        r1.drawNameBot = r6;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x0550;
    L_0x0510:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0515;
    L_0x0514:
        goto L_0x0550;
    L_0x0515:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0537;
    L_0x0521:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x0537:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x0550:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0572;
    L_0x055c:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x058a;
    L_0x0572:
        r9 = r33.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x058a:
        r9 = r1.lastMessageDate;
        if (r9 != 0) goto L_0x0596;
    L_0x058e:
        r10 = r1.message;
        if (r10 == 0) goto L_0x0596;
    L_0x0592:
        r9 = r10.messageOwner;
        r9 = r9.date;
    L_0x0596:
        r10 = r1.isDialogCell;
        if (r10 == 0) goto L_0x05f5;
    L_0x059a:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MediaDataController.getInstance(r10);
        r18 = r7;
        r6 = r1.currentDialogId;
        r6 = r10.getDraft(r6);
        r1.draftMessage = r6;
        r6 = r1.draftMessage;
        if (r6 == 0) goto L_0x05c9;
    L_0x05ae:
        r6 = r6.message;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x05bf;
    L_0x05b6:
        r6 = r1.draftMessage;
        r6 = r6.reply_to_msg_id;
        if (r6 == 0) goto L_0x05bd;
    L_0x05bc:
        goto L_0x05bf;
    L_0x05bd:
        r6 = 0;
        goto L_0x05f0;
    L_0x05bf:
        r6 = r1.draftMessage;
        r6 = r6.date;
        if (r9 <= r6) goto L_0x05c9;
    L_0x05c5:
        r6 = r1.unreadCount;
        if (r6 != 0) goto L_0x05bd;
    L_0x05c9:
        r6 = r1.chat;
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x05e3;
    L_0x05d1:
        r6 = r1.chat;
        r7 = r6.megagroup;
        if (r7 != 0) goto L_0x05e3;
    L_0x05d7:
        r7 = r6.creator;
        if (r7 != 0) goto L_0x05e3;
    L_0x05db:
        r6 = r6.admin_rights;
        if (r6 == 0) goto L_0x05bd;
    L_0x05df:
        r6 = r6.post_messages;
        if (r6 == 0) goto L_0x05bd;
    L_0x05e3:
        r6 = r1.chat;
        if (r6 == 0) goto L_0x05f3;
    L_0x05e7:
        r7 = r6.left;
        if (r7 != 0) goto L_0x05bd;
    L_0x05eb:
        r6 = r6.kicked;
        if (r6 == 0) goto L_0x05f3;
    L_0x05ef:
        goto L_0x05bd;
    L_0x05f0:
        r1.draftMessage = r6;
        goto L_0x05fa;
    L_0x05f3:
        r6 = 0;
        goto L_0x05fa;
    L_0x05f5:
        r18 = r7;
        r6 = 0;
        r1.draftMessage = r6;
    L_0x05fa:
        if (r0 == 0) goto L_0x0607;
    L_0x05fc:
        r1.lastPrintString = r0;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r8 = r4;
        r7 = r6;
        r6 = 1;
    L_0x0603:
        r4 = r0;
        r0 = 1;
        goto L_0x0acb;
    L_0x0607:
        r1.lastPrintString = r6;
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x06b8;
    L_0x060d:
        r0 = NUM; // 0x7f0d039c float:1.8743989E38 double:1.053130234E-314;
        r6 = "Draft";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r6 = r1.draftMessage;
        r6 = r6.message;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x064c;
    L_0x0620:
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x0645;
    L_0x0624:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x0629;
    L_0x0628:
        goto L_0x0645;
    L_0x0629:
        r6 = android.text.SpannableStringBuilder.valueOf(r0);
        r7 = new android.text.style.ForegroundColorSpan;
        r8 = "chats_draft";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.<init>(r8);
        r8 = r0.length();
        r9 = 33;
        r6.setSpan(r7, r2, r8, r9);
    L_0x0641:
        r7 = r0;
        r8 = r4;
        r4 = r6;
        goto L_0x0648;
    L_0x0645:
        r7 = r0;
        r8 = r4;
        r4 = r12;
    L_0x0648:
        r0 = 0;
        r6 = 1;
        goto L_0x0acb;
    L_0x064c:
        r6 = r1.draftMessage;
        r6 = r6.message;
        r7 = r6.length();
        if (r7 <= r14) goto L_0x065a;
    L_0x0656:
        r6 = r6.substring(r2, r14);
    L_0x065a:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x0690;
    L_0x065e:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x0663;
    L_0x0662:
        goto L_0x0690;
    L_0x0663:
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
        goto L_0x06a7;
    L_0x0690:
        r9 = r18;
        r8 = 1;
        r7 = new java.lang.Object[r3];
        r10 = 32;
        r6 = r6.replace(r11, r10);
        r7[r2] = r6;
        r7[r8] = r0;
        r6 = java.lang.String.format(r9, r7);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
    L_0x06a7:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r7 = r7.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r7, r9, r2);
        goto L_0x0641;
    L_0x06b8:
        r9 = r18;
        r0 = r1.clearingDialog;
        if (r0 == 0) goto L_0x06ce;
    L_0x06be:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = NUM; // 0x7f0d0508 float:1.8744727E38 double:1.053130414E-314;
        r6 = "HistoryCleared";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
    L_0x06c9:
        r8 = r4;
        r6 = 1;
        r7 = 0;
        goto L_0x0603;
    L_0x06ce:
        r0 = r1.message;
        if (r0 != 0) goto L_0x0769;
    L_0x06d2:
        r0 = r1.encryptedChat;
        if (r0 == 0) goto L_0x0762;
    L_0x06d6:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
        if (r6 == 0) goto L_0x06e6;
    L_0x06dc:
        r0 = NUM; // 0x7f0d03e9 float:1.8744145E38 double:1.053130272E-314;
        r6 = "EncryptionProcessing";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06c9;
    L_0x06e6:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
        if (r6 == 0) goto L_0x0710;
    L_0x06ea:
        r0 = r1.user;
        if (r0 == 0) goto L_0x0701;
    L_0x06ee:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x0701;
    L_0x06f2:
        r6 = NUM; // 0x7f0d019c float:1.874295E38 double:1.053129981E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r8[r2] = r0;
        r0 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r6, r8);
        goto L_0x06c9;
    L_0x0701:
        r7 = 1;
        r0 = NUM; // 0x7f0d019c float:1.874295E38 double:1.053129981E-314;
        r6 = new java.lang.Object[r7];
        r6[r2] = r12;
        r7 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r0, r6);
        goto L_0x06c9;
    L_0x0710:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
        if (r6 == 0) goto L_0x071e;
    L_0x0714:
        r0 = NUM; // 0x7f0d03ea float:1.8744147E38 double:1.0531302726E-314;
        r6 = "EncryptionRejected";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06c9;
    L_0x071e:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r6 == 0) goto L_0x0762;
    L_0x0722:
        r0 = r0.admin_id;
        r6 = r1.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r0 != r6) goto L_0x0757;
    L_0x0730:
        r0 = r1.user;
        if (r0 == 0) goto L_0x0747;
    L_0x0734:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x0747;
    L_0x0738:
        r6 = NUM; // 0x7f0d03de float:1.8744123E38 double:1.0531302667E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r8[r2] = r0;
        r0 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r6, r8);
        goto L_0x06c9;
    L_0x0747:
        r7 = 1;
        r0 = NUM; // 0x7f0d03de float:1.8744123E38 double:1.0531302667E-314;
        r6 = new java.lang.Object[r7];
        r6[r2] = r12;
        r7 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r0, r6);
        goto L_0x06c9;
    L_0x0757:
        r0 = NUM; // 0x7f0d03dd float:1.874412E38 double:1.053130266E-314;
        r6 = "EncryptedChatStartedIncoming";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x06c9;
    L_0x0762:
        r8 = r4;
        r4 = r12;
        r0 = 1;
        r6 = 1;
        r7 = 0;
        goto L_0x0acb;
    L_0x0769:
        r0 = r0.isFromUser();
        if (r0 == 0) goto L_0x0786;
    L_0x076f:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r1.message;
        r6 = r6.messageOwner;
        r6 = r6.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getUser(r6);
        r6 = r0;
        r0 = 0;
        goto L_0x079d;
    L_0x0786:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r1.message;
        r6 = r6.messageOwner;
        r6 = r6.to_id;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getChat(r6);
        r6 = 0;
    L_0x079d:
        r7 = r1.dialogsType;
        r10 = 3;
        if (r7 != r10) goto L_0x07bb;
    L_0x07a2:
        r7 = r1.user;
        r7 = org.telegram.messenger.UserObject.isUserSelf(r7);
        if (r7 == 0) goto L_0x07bb;
    L_0x07aa:
        r0 = NUM; // 0x7f0d091c float:1.8746845E38 double:1.0531309297E-314;
        r5 = "SavedMessagesInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r6 = r0;
        r8 = r4;
        r0 = 0;
        r4 = 1;
        r5 = 0;
    L_0x07b8:
        r7 = 0;
        goto L_0x0abd;
    L_0x07bb:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x07d0;
    L_0x07bf:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 != 0) goto L_0x07d0;
    L_0x07c3:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x07d0;
    L_0x07c7:
        r0 = r33.formatArchivedDialogNames();
        r6 = r0;
        r8 = r4;
        r0 = 1;
        r4 = 0;
        goto L_0x07b8;
    L_0x07d0:
        r7 = r1.message;
        r10 = r7.messageOwner;
        r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r10 == 0) goto L_0x07fc;
    L_0x07d8:
        r0 = r1.chat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x07f1;
    L_0x07e0:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r4 != 0) goto L_0x07ee;
    L_0x07ea:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r0 == 0) goto L_0x07f1;
    L_0x07ee:
        r0 = r12;
        r5 = 0;
        goto L_0x07f5;
    L_0x07f1:
        r0 = r1.message;
        r0 = r0.messageText;
    L_0x07f5:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x07f7:
        r6 = r0;
        r8 = r4;
        r0 = 1;
        r4 = 1;
        goto L_0x07b8;
    L_0x07fc:
        r10 = r1.chat;
        if (r10 == 0) goto L_0x09e2;
    L_0x0800:
        r10 = r10.id;
        if (r10 <= 0) goto L_0x09e2;
    L_0x0804:
        if (r0 != 0) goto L_0x09e2;
    L_0x0806:
        r7 = r7.isOutOwner();
        if (r7 == 0) goto L_0x0817;
    L_0x080c:
        r0 = NUM; // 0x7f0d04ce float:1.874461E38 double:1.053130385E-314;
        r6 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
    L_0x0815:
        r6 = r0;
        goto L_0x085a;
    L_0x0817:
        if (r6 == 0) goto L_0x084c;
    L_0x0819:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x082d;
    L_0x081d:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0822;
    L_0x0821:
        goto L_0x082d;
    L_0x0822:
        r0 = org.telegram.messenger.UserObject.getFirstName(r6);
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x0815;
    L_0x082d:
        r0 = org.telegram.messenger.UserObject.isDeleted(r6);
        if (r0 == 0) goto L_0x083d;
    L_0x0833:
        r0 = NUM; // 0x7f0d0505 float:1.8744721E38 double:1.0531304124E-314;
        r6 = "HiddenName";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x0815;
    L_0x083d:
        r0 = r6.first_name;
        r6 = r6.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r6);
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x0815;
    L_0x084c:
        if (r0 == 0) goto L_0x0857;
    L_0x084e:
        r0 = r0.title;
        r6 = "\n";
        r0 = r0.replace(r6, r12);
        goto L_0x0815;
    L_0x0857:
        r0 = "DELETED";
        goto L_0x0815;
    L_0x085a:
        r0 = r1.message;
        r7 = r0.caption;
        if (r7 == 0) goto L_0x08c2;
    L_0x0860:
        r0 = r7.toString();
        r7 = r0.length();
        if (r7 <= r14) goto L_0x086e;
    L_0x086a:
        r0 = r0.substring(r2, r14);
    L_0x086e:
        r7 = r1.message;
        r7 = r7.isVideo();
        if (r7 == 0) goto L_0x0879;
    L_0x0876:
        r7 = "📹 ";
        goto L_0x089c;
    L_0x0879:
        r7 = r1.message;
        r7 = r7.isVoice();
        if (r7 == 0) goto L_0x0884;
    L_0x0881:
        r7 = "🎤 ";
        goto L_0x089c;
    L_0x0884:
        r7 = r1.message;
        r7 = r7.isMusic();
        if (r7 == 0) goto L_0x088f;
    L_0x088c:
        r7 = "🎧 ";
        goto L_0x089c;
    L_0x088f:
        r7 = r1.message;
        r7 = r7.isPhoto();
        if (r7 == 0) goto L_0x089a;
    L_0x0897:
        r7 = "🖼 ";
        goto L_0x089c;
    L_0x089a:
        r7 = "📎 ";
    L_0x089c:
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
        goto L_0x099d;
    L_0x08c2:
        r7 = r0.messageOwner;
        r7 = r7.media;
        if (r7 == 0) goto L_0x0971;
    L_0x08c8:
        r0 = r0.isMediaEmpty();
        if (r0 != 0) goto L_0x0971;
    L_0x08ce:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = r1.message;
        r7 = r0.messageOwner;
        r7 = r7.media;
        r10 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r10 == 0) goto L_0x0901;
    L_0x08da:
        r0 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r0 < r10) goto L_0x08f0;
    L_0x08e0:
        r10 = 1;
        r0 = new java.lang.Object[r10];
        r7 = r7.game;
        r7 = r7.title;
        r0[r2] = r7;
        r7 = "🎮 ⁨%s⁩";
        r0 = java.lang.String.format(r7, r0);
        goto L_0x0940;
    L_0x08f0:
        r10 = 1;
        r0 = new java.lang.Object[r10];
        r7 = r7.game;
        r7 = r7.title;
        r0[r2] = r7;
        r7 = "🎮 %s";
        r0 = java.lang.String.format(r7, r0);
        r10 = 1;
        goto L_0x0940;
    L_0x0901:
        r7 = r0.type;
        r10 = 14;
        if (r7 != r10) goto L_0x093d;
    L_0x0907:
        r7 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r7 < r10) goto L_0x0925;
    L_0x090d:
        r7 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r7[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r10 = 1;
        r7[r10] = r0;
        r0 = "🎧 ⁨%s - %s⁩";
        r0 = java.lang.String.format(r0, r7);
        goto L_0x0940;
    L_0x0925:
        r10 = 1;
        r7 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r7[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r7[r10] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r7);
        goto L_0x0940;
    L_0x093d:
        r10 = 1;
        r0 = r0.messageText;
    L_0x0940:
        r7 = new java.lang.Object[r3];
        r7[r2] = r0;
        r7[r10] = r6;
        r0 = java.lang.String.format(r9, r7);
        r7 = android.text.SpannableStringBuilder.valueOf(r0);
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x096c }
        r9 = "chats_attachMessage";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);	 Catch:{ Exception -> 0x096c }
        r0.<init>(r9);	 Catch:{ Exception -> 0x096c }
        if (r8 == 0) goto L_0x0961;
    L_0x095b:
        r8 = r6.length();	 Catch:{ Exception -> 0x096c }
        r8 = r8 + r3;
        goto L_0x0962;
    L_0x0961:
        r8 = 0;
    L_0x0962:
        r9 = r7.length();	 Catch:{ Exception -> 0x096c }
        r10 = 33;
        r7.setSpan(r0, r8, r9, r10);	 Catch:{ Exception -> 0x096c }
        goto L_0x099e;
    L_0x096c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x099e;
    L_0x0971:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.message;
        if (r0 == 0) goto L_0x0999;
    L_0x0979:
        r7 = r0.length();
        if (r7 <= r14) goto L_0x0983;
    L_0x097f:
        r0 = r0.substring(r2, r14);
    L_0x0983:
        r7 = new java.lang.Object[r3];
        r8 = 32;
        r0 = r0.replace(r11, r8);
        r7[r2] = r0;
        r8 = 1;
        r7[r8] = r6;
        r0 = java.lang.String.format(r9, r7);
        r0 = android.text.SpannableStringBuilder.valueOf(r0);
        goto L_0x099d;
    L_0x0999:
        r0 = android.text.SpannableStringBuilder.valueOf(r12);
    L_0x099d:
        r7 = r0;
    L_0x099e:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x09a6;
    L_0x09a2:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x09b0;
    L_0x09a6:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x09cb;
    L_0x09aa:
        r0 = r7.length();
        if (r0 <= 0) goto L_0x09cb;
    L_0x09b0:
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x09c7 }
        r8 = "chats_nameMessage";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);	 Catch:{ Exception -> 0x09c7 }
        r0.<init>(r8);	 Catch:{ Exception -> 0x09c7 }
        r8 = r6.length();	 Catch:{ Exception -> 0x09c7 }
        r9 = 1;
        r8 = r8 + r9;
        r9 = 33;
        r7.setSpan(r0, r2, r8, r9);	 Catch:{ Exception -> 0x09c7 }
        goto L_0x09cb;
    L_0x09c7:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x09cb:
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
        goto L_0x0abd;
    L_0x09e2:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 == 0) goto L_0x0a01;
    L_0x09ec:
        r6 = r0.photo;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r6 == 0) goto L_0x0a01;
    L_0x09f2:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a01;
    L_0x09f6:
        r0 = NUM; // 0x7f0d014c float:1.8742788E38 double:1.0531299416E-314;
        r6 = "AttachPhotoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x07f7;
    L_0x0a01:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r6 == 0) goto L_0x0a20;
    L_0x0a0b:
        r6 = r0.document;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;
        if (r6 == 0) goto L_0x0a20;
    L_0x0a11:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a20;
    L_0x0a15:
        r0 = NUM; // 0x7f0d0152 float:1.87428E38 double:1.0531299445E-314;
        r6 = "AttachVideoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        goto L_0x07f7;
    L_0x0a20:
        r0 = r1.message;
        r6 = r0.caption;
        if (r6 == 0) goto L_0x0a67;
    L_0x0a26:
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x0a2f;
    L_0x0a2c:
        r0 = "📹 ";
        goto L_0x0a52;
    L_0x0a2f:
        r0 = r1.message;
        r0 = r0.isVoice();
        if (r0 == 0) goto L_0x0a3a;
    L_0x0a37:
        r0 = "🎤 ";
        goto L_0x0a52;
    L_0x0a3a:
        r0 = r1.message;
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x0a45;
    L_0x0a42:
        r0 = "🎧 ";
        goto L_0x0a52;
    L_0x0a45:
        r0 = r1.message;
        r0 = r0.isPhoto();
        if (r0 == 0) goto L_0x0a50;
    L_0x0a4d:
        r0 = "🖼 ";
        goto L_0x0a52;
    L_0x0a50:
        r0 = "📎 ";
    L_0x0a52:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r0);
        r0 = r1.message;
        r0 = r0.caption;
        r6.append(r0);
        r0 = r6.toString();
        goto L_0x07f7;
    L_0x0a67:
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r6 == 0) goto L_0x0a8b;
    L_0x0a6f:
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
        goto L_0x0aab;
    L_0x0a8b:
        r6 = r0.type;
        r7 = 14;
        if (r6 != r7) goto L_0x0aa9;
    L_0x0a91:
        r6 = new java.lang.Object[r3];
        r0 = r0.getMusicAuthor();
        r6[r2] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r7 = 1;
        r6[r7] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r6);
        goto L_0x0aab;
    L_0x0aa9:
        r0 = r0.messageText;
    L_0x0aab:
        r6 = r1.message;
        r7 = r6.messageOwner;
        r7 = r7.media;
        if (r7 == 0) goto L_0x07f7;
    L_0x0ab3:
        r6 = r6.isMediaEmpty();
        if (r6 != 0) goto L_0x07f7;
    L_0x0ab9:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        goto L_0x07f7;
    L_0x0abd:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x0ac5;
    L_0x0ac1:
        r7 = r33.formatArchivedDialogNames();
    L_0x0ac5:
        r32 = r6;
        r6 = r0;
        r0 = r4;
        r4 = r32;
    L_0x0acb:
        r9 = r1.draftMessage;
        if (r9 == 0) goto L_0x0ad7;
    L_0x0acf:
        r9 = r9.date;
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0af0;
    L_0x0ad7:
        r9 = r1.lastMessageDate;
        if (r9 == 0) goto L_0x0ae1;
    L_0x0adb:
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0af0;
    L_0x0ae1:
        r9 = r1.message;
        if (r9 == 0) goto L_0x0aef;
    L_0x0ae5:
        r9 = r9.messageOwner;
        r9 = r9.date;
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        goto L_0x0af0;
    L_0x0aef:
        r9 = r12;
    L_0x0af0:
        r10 = r1.message;
        if (r10 != 0) goto L_0x0b04;
    L_0x0af4:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawCount = r2;
        r1.drawMention = r2;
        r1.drawError = r2;
        r3 = 0;
        r10 = 0;
        goto L_0x0CLASSNAME;
    L_0x0b04:
        r3 = r1.currentDialogFolderId;
        if (r3 == 0) goto L_0x0b45;
    L_0x0b08:
        r3 = r1.unreadCount;
        r10 = r1.mentionCount;
        r19 = r3 + r10;
        if (r19 <= 0) goto L_0x0b3e;
    L_0x0b10:
        if (r3 <= r10) goto L_0x0b27;
    L_0x0b12:
        r14 = 1;
        r1.drawCount = r14;
        r1.drawMention = r2;
        r15 = new java.lang.Object[r14];
        r3 = r3 + r10;
        r3 = java.lang.Integer.valueOf(r3);
        r15[r2] = r3;
        r3 = "%d";
        r3 = java.lang.String.format(r3, r15);
        goto L_0x0b43;
    L_0x0b27:
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
        goto L_0x0b94;
    L_0x0b3e:
        r1.drawCount = r2;
        r1.drawMention = r2;
        r3 = 0;
    L_0x0b43:
        r10 = 0;
        goto L_0x0b94;
    L_0x0b45:
        r3 = r1.clearingDialog;
        if (r3 == 0) goto L_0x0b4f;
    L_0x0b49:
        r1.drawCount = r2;
        r3 = 1;
        r5 = 0;
    L_0x0b4d:
        r10 = 0;
        goto L_0x0b82;
    L_0x0b4f:
        r3 = r1.unreadCount;
        if (r3 == 0) goto L_0x0b76;
    L_0x0b53:
        r14 = 1;
        if (r3 != r14) goto L_0x0b62;
    L_0x0b56:
        r14 = r1.mentionCount;
        if (r3 != r14) goto L_0x0b62;
    L_0x0b5a:
        if (r10 == 0) goto L_0x0b62;
    L_0x0b5c:
        r3 = r10.messageOwner;
        r3 = r3.mentioned;
        if (r3 != 0) goto L_0x0b76;
    L_0x0b62:
        r3 = 1;
        r1.drawCount = r3;
        r10 = new java.lang.Object[r3];
        r14 = r1.unreadCount;
        r14 = java.lang.Integer.valueOf(r14);
        r10[r2] = r14;
        r14 = "%d";
        r10 = java.lang.String.format(r14, r10);
        goto L_0x0b82;
    L_0x0b76:
        r3 = 1;
        r10 = r1.markUnread;
        if (r10 == 0) goto L_0x0b7f;
    L_0x0b7b:
        r1.drawCount = r3;
        r10 = r12;
        goto L_0x0b82;
    L_0x0b7f:
        r1.drawCount = r2;
        goto L_0x0b4d;
    L_0x0b82:
        r14 = r1.mentionCount;
        if (r14 == 0) goto L_0x0b90;
    L_0x0b86:
        r1.drawMention = r3;
        r3 = "@";
        r32 = r10;
        r10 = r3;
        r3 = r32;
        goto L_0x0b94;
    L_0x0b90:
        r1.drawMention = r2;
        r3 = r10;
        goto L_0x0b43;
    L_0x0b94:
        r14 = r1.message;
        r14 = r14.isOut();
        if (r14 == 0) goto L_0x0bfe;
    L_0x0b9c:
        r14 = r1.draftMessage;
        if (r14 != 0) goto L_0x0bfe;
    L_0x0ba0:
        if (r5 == 0) goto L_0x0bfe;
    L_0x0ba2:
        r5 = r1.message;
        r14 = r5.messageOwner;
        r14 = r14.action;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r14 != 0) goto L_0x0bfe;
    L_0x0bac:
        r5 = r5.isSending();
        if (r5 == 0) goto L_0x0bbc;
    L_0x0bb2:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r5 = 1;
        r1.drawClock = r5;
        r1.drawError = r2;
        goto L_0x0CLASSNAME;
    L_0x0bbc:
        r5 = 1;
        r14 = r1.message;
        r14 = r14.isSendError();
        if (r14 == 0) goto L_0x0bd2;
    L_0x0bc5:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r5;
        r1.drawCount = r2;
        r1.drawMention = r2;
        goto L_0x0CLASSNAME;
    L_0x0bd2:
        r5 = r1.message;
        r5 = r5.isSent();
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0bda:
        r5 = r1.message;
        r5 = r5.isUnread();
        if (r5 == 0) goto L_0x0bf3;
    L_0x0be2:
        r5 = r1.chat;
        r5 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r5 == 0) goto L_0x0bf1;
    L_0x0bea:
        r5 = r1.chat;
        r5 = r5.megagroup;
        if (r5 != 0) goto L_0x0bf1;
    L_0x0bf0:
        goto L_0x0bf3;
    L_0x0bf1:
        r5 = 0;
        goto L_0x0bf4;
    L_0x0bf3:
        r5 = 1;
    L_0x0bf4:
        r1.drawCheck1 = r5;
        r5 = 1;
        r1.drawCheck2 = r5;
        r1.drawClock = r2;
        r1.drawError = r2;
        goto L_0x0CLASSNAME;
    L_0x0bfe:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r2;
        r1.drawError = r2;
    L_0x0CLASSNAME:
        r5 = r1.dialogsType;
        if (r5 != 0) goto L_0x0CLASSNAME;
    L_0x0c0a:
        r5 = r1.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r14 = r1.currentDialogId;
        r13 = 1;
        r5 = r5.isProxyDialog(r14, r13);
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.drawPinBackground = r13;
        r5 = NUM; // 0x7f0d0a8b float:1.8747589E38 double:1.053131111E-314;
        r9 = "UseProxySponsor";
        r5 = org.telegram.messenger.LocaleController.getString(r9, r5);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = r9;
    L_0x0CLASSNAME:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0c2a:
        r9 = NUM; // 0x7f0d0101 float:1.8742636E38 double:1.0531299045E-314;
        r13 = "ArchivedChats";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
    L_0x0CLASSNAME:
        r13 = r8;
        r8 = r7;
        r7 = r4;
        goto L_0x0335;
    L_0x0CLASSNAME:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x0c3f;
    L_0x0c3c:
        r9 = r9.title;
        goto L_0x0CLASSNAME;
    L_0x0c3f:
        r9 = r1.user;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = org.telegram.messenger.UserObject.isUserSelf(r9);
        if (r9 == 0) goto L_0x0c5b;
    L_0x0CLASSNAME:
        r9 = r1.dialogsType;
        r13 = 3;
        if (r9 != r13) goto L_0x0CLASSNAME;
    L_0x0c4e:
        r9 = 1;
        r1.drawPinBackground = r9;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0d091b float:1.8746843E38 double:1.053130929E-314;
        r13 = "SavedMessages";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        goto L_0x0CLASSNAME;
    L_0x0c5b:
        r9 = r1.user;
        r9 = org.telegram.messenger.UserObject.getUserName(r9);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r12;
    L_0x0CLASSNAME:
        r13 = r9.length();
        if (r13 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = NUM; // 0x7f0d0505 float:1.8744721E38 double:1.0531304124E-314;
        r13 = "HiddenName";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r6 == 0) goto L_0x0cb4;
    L_0x0CLASSNAME:
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
        if (r5 != 0) goto L_0x0cab;
    L_0x0c9c:
        r5 = r33.getMeasuredWidth();
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = r5 - r0;
        r1.timeLeft = r5;
        goto L_0x0cba;
    L_0x0cab:
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.timeLeft = r5;
        goto L_0x0cba;
    L_0x0cb4:
        r5 = 0;
        r1.timeLayout = r5;
        r1.timeLeft = r2;
        r0 = 0;
    L_0x0cba:
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 != 0) goto L_0x0cce;
    L_0x0cbe:
        r5 = r33.getMeasuredWidth();
        r6 = r1.nameLeft;
        r5 = r5 - r6;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = r5 - r0;
        goto L_0x0ce2;
    L_0x0cce:
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
    L_0x0ce2:
        r6 = r1.drawNameLock;
        if (r6 == 0) goto L_0x0cf5;
    L_0x0ce6:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r14 = r14.getIntrinsicWidth();
    L_0x0cf2:
        r6 = r6 + r14;
        r5 = r5 - r6;
        goto L_0x0d28;
    L_0x0cf5:
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x0d06;
    L_0x0cf9:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0cf2;
    L_0x0d06:
        r6 = r1.drawNameBroadcast;
        if (r6 == 0) goto L_0x0d17;
    L_0x0d0a:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0cf2;
    L_0x0d17:
        r6 = r1.drawNameBot;
        if (r6 == 0) goto L_0x0d28;
    L_0x0d1b:
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r14 = r14.getIntrinsicWidth();
        goto L_0x0cf2;
    L_0x0d28:
        r6 = r1.drawClock;
        if (r6 == 0) goto L_0x0d58;
    L_0x0d2c:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r6 = r6.getIntrinsicWidth();
        r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = r6 + r14;
        r5 = r5 - r6;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0d45;
    L_0x0d3e:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dd7;
    L_0x0d45:
        r14 = r1.timeLeft;
        r14 = r14 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r14 = r14 + r0;
        r1.checkDrawLeft = r14;
        r0 = r1.nameLeft;
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0dd7;
    L_0x0d58:
        r6 = r1.drawCheck2;
        if (r6 == 0) goto L_0x0dd7;
    L_0x0d5c:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r6 = r6.getIntrinsicWidth();
        r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = r6 + r14;
        r5 = r5 - r6;
        r14 = r1.drawCheck1;
        if (r14 == 0) goto L_0x0dbc;
    L_0x0d6e:
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r14 = r14.getIntrinsicWidth();
        r15 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r14 = r14 - r15;
        r5 = r5 - r14;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0d91;
    L_0x0d80:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.halfCheckDrawLeft;
        r6 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dd7;
    L_0x0d91:
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
        goto L_0x0dd7;
    L_0x0dbc:
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0dc6;
    L_0x0dc0:
        r0 = r1.timeLeft;
        r0 = r0 - r6;
        r1.checkDrawLeft = r0;
        goto L_0x0dd7;
    L_0x0dc6:
        r14 = r1.timeLeft;
        r14 = r14 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r14 = r14 + r0;
        r1.checkDrawLeft = r14;
        r0 = r1.nameLeft;
        r0 = r0 + r6;
        r1.nameLeft = r0;
    L_0x0dd7:
        r0 = r1.dialogMuted;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r0 == 0) goto L_0x0dfb;
    L_0x0ddd:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x0dfb;
    L_0x0de1:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x0dfb;
    L_0x0de5:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e2e;
    L_0x0df5:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
        goto L_0x0e2e;
    L_0x0dfb:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0e15;
    L_0x0dff:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e2e;
    L_0x0e0f:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
        goto L_0x0e2e;
    L_0x0e15:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0e2e;
    L_0x0e19:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r14 = r14.getIntrinsicWidth();
        r0 = r0 + r14;
        r5 = r5 - r0;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 == 0) goto L_0x0e2e;
    L_0x0e29:
        r14 = r1.nameLeft;
        r14 = r14 + r0;
        r1.nameLeft = r14;
    L_0x0e2e:
        r14 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r5 = java.lang.Math.max(r0, r5);
        r15 = 32;
        r0 = r9.replace(r11, r15);	 Catch:{ Exception -> 0x0e63 }
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e63 }
        r15 = org.telegram.messenger.AndroidUtilities.dp(r14);	 Catch:{ Exception -> 0x0e63 }
        r15 = r5 - r15;
        r15 = (float) r15;	 Catch:{ Exception -> 0x0e63 }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0e63 }
        r23 = android.text.TextUtils.ellipsize(r0, r9, r15, r6);	 Catch:{ Exception -> 0x0e63 }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0e63 }
        r24 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e63 }
        r26 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0e63 }
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r0;
        r25 = r5;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);	 Catch:{ Exception -> 0x0e63 }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x0e63 }
        goto L_0x0e67;
    L_0x0e63:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0e67:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0eeb;
    L_0x0e6b:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0e71;
    L_0x0e6f:
        goto L_0x0eeb;
    L_0x0e71:
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
        if (r9 != 0) goto L_0x0ec5;
    L_0x0eb6:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x0eda;
    L_0x0ec5:
        r9 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = r33.getMeasuredWidth();
        r15 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r9 = r9 - r15;
    L_0x0eda:
        r15 = r1.avatarImage;
        r16 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r14 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r15.setImageCoords(r9, r0, r11, r14);
        goto L_0x0var_;
    L_0x0eeb:
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
        if (r9 != 0) goto L_0x0f3f;
    L_0x0var_:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x0var_;
    L_0x0f3f:
        r9 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.messageNameLeft = r9;
        r1.messageLeft = r9;
        r9 = r33.getMeasuredWidth();
        r11 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r9 = r9 - r11;
    L_0x0var_:
        r11 = r1.avatarImage;
        r14 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r15 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11.setImageCoords(r9, r0, r14, r15);
    L_0x0var_:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0f8a;
    L_0x0var_:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0var_;
    L_0x0f6d:
        r0 = r33.getMeasuredWidth();
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r9 = r9.getIntrinsicWidth();
        r0 = r0 - r9;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 - r9;
        r1.pinLeft = r0;
        goto L_0x0f8a;
    L_0x0var_:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.pinLeft = r0;
    L_0x0f8a:
        r0 = r1.drawError;
        if (r0 == 0) goto L_0x0fbc;
    L_0x0f8e:
        r0 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x0fa8;
    L_0x0var_:
        r0 = r33.getMeasuredWidth();
        r3 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.errorLeft = r0;
        goto L_0x10e1;
    L_0x0fa8:
        r3 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.errorLeft = r3;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
        goto L_0x10e1;
    L_0x0fbc:
        if (r3 != 0) goto L_0x0fe7;
    L_0x0fbe:
        if (r10 == 0) goto L_0x0fc1;
    L_0x0fc0:
        goto L_0x0fe7;
    L_0x0fc1:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0fe1;
    L_0x0fc5:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0 = r0.getIntrinsicWidth();
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x0fe1;
    L_0x0fd7:
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x0fe1:
        r1.drawCount = r2;
        r1.drawMention = r2;
        goto L_0x10e1;
    L_0x0fe7:
        if (r3 == 0) goto L_0x104f;
    L_0x0fe9:
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
        if (r3 != 0) goto L_0x1039;
    L_0x1028:
        r0 = r33.getMeasuredWidth();
        r3 = r1.countWidth;
        r0 = r0 - r3;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r9;
        r1.countLeft = r0;
        goto L_0x104b;
    L_0x1039:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.countLeft = r9;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x104b:
        r3 = 1;
        r1.drawCount = r3;
        goto L_0x1051;
    L_0x104f:
        r1.countWidth = r2;
    L_0x1051:
        if (r10 == 0) goto L_0x10e1;
    L_0x1053:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x1089;
    L_0x1057:
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
        goto L_0x1091;
    L_0x1089:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.mentionWidth = r0;
    L_0x1091:
        r0 = r1.mentionWidth;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r9;
        r6 = r6 - r0;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x10be;
    L_0x109f:
        r0 = r33.getMeasuredWidth();
        r3 = r1.mentionWidth;
        r0 = r0 - r3;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = r1.countWidth;
        if (r3 == 0) goto L_0x10b9;
    L_0x10b1:
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r3 = r3 + r9;
        goto L_0x10ba;
    L_0x10b9:
        r3 = 0;
    L_0x10ba:
        r0 = r0 - r3;
        r1.mentionLeft = r0;
        goto L_0x10de;
    L_0x10be:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r10 = r1.countWidth;
        if (r10 == 0) goto L_0x10d0;
    L_0x10ca:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r10;
        goto L_0x10d1;
    L_0x10d0:
        r9 = 0;
    L_0x10d1:
        r3 = r3 + r9;
        r1.mentionLeft = r3;
        r3 = r1.messageLeft;
        r3 = r3 + r0;
        r1.messageLeft = r3;
        r3 = r1.messageNameLeft;
        r3 = r3 + r0;
        r1.messageNameLeft = r3;
    L_0x10de:
        r3 = 1;
        r1.drawMention = r3;
    L_0x10e1:
        if (r4 == 0) goto L_0x1118;
    L_0x10e3:
        if (r7 != 0) goto L_0x10e6;
    L_0x10e5:
        r7 = r12;
    L_0x10e6:
        r0 = r7.toString();
        r3 = r0.length();
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r3 <= r4) goto L_0x10f6;
    L_0x10f2:
        r0 = r0.substring(r2, r4);
    L_0x10f6:
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x10fe;
    L_0x10fa:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x1100;
    L_0x10fe:
        if (r8 == 0) goto L_0x1108;
    L_0x1100:
        r3 = 32;
        r4 = 10;
        r0 = r0.replace(r4, r3);
    L_0x1108:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = r3.getFontMetricsInt();
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r7 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r4, r2);
    L_0x1118:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = java.lang.Math.max(r0, r6);
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x112a;
    L_0x1126:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x115e;
    L_0x112a:
        if (r8 == 0) goto L_0x115e;
    L_0x112c:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x1135;
    L_0x1130:
        r0 = r1.currentDialogFolderDialogsCount;
        r4 = 1;
        if (r0 != r4) goto L_0x115e;
    L_0x1135:
        r23 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;	 Catch:{ Exception -> 0x1150 }
        r25 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x1150 }
        r26 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r27 = 0;
        r28 = 0;
        r29 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x1150 }
        r31 = 1;
        r22 = r8;
        r24 = r3;
        r30 = r3;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x1150 }
        r1.messageNameLayout = r0;	 Catch:{ Exception -> 0x1150 }
        goto L_0x1154;
    L_0x1150:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x1154:
        r0 = NUM; // 0x424CLASSNAME float:51.0 double:5.495378504E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        r6 = 0;
        goto L_0x117b;
    L_0x115e:
        r6 = 0;
        r1.messageNameLayout = r6;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x1173;
    L_0x1165:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x116a;
    L_0x1169:
        goto L_0x1173;
    L_0x116a:
        r0 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        goto L_0x117b;
    L_0x1173:
        r0 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
    L_0x117b:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x11f5 }
        if (r0 != 0) goto L_0x1183;
    L_0x117f:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11f5 }
        if (r0 == 0) goto L_0x1191;
    L_0x1183:
        r0 = r1.currentDialogFolderId;	 Catch:{ Exception -> 0x11f5 }
        if (r0 == 0) goto L_0x1191;
    L_0x1187:
        r0 = r1.currentDialogFolderDialogsCount;	 Catch:{ Exception -> 0x11f5 }
        r4 = 1;
        if (r0 <= r4) goto L_0x1192;
    L_0x118c:
        r13 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;	 Catch:{ Exception -> 0x11f5 }
        r0 = r8;
        r8 = r6;
        goto L_0x11ad;
    L_0x1191:
        r4 = 1;
    L_0x1192:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x11f5 }
        if (r0 != 0) goto L_0x119a;
    L_0x1196:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11f5 }
        if (r0 == 0) goto L_0x119c;
    L_0x119a:
        if (r8 == 0) goto L_0x11ac;
    L_0x119c:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x11f5 }
        r0 = r3 - r0;
        r0 = (float) r0;	 Catch:{ Exception -> 0x11f5 }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x11f5 }
        r0 = android.text.TextUtils.ellipsize(r7, r13, r0, r6);	 Catch:{ Exception -> 0x11f5 }
        goto L_0x11ad;
    L_0x11ac:
        r0 = r7;
    L_0x11ad:
        r6 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x11f5 }
        if (r6 != 0) goto L_0x11ce;
    L_0x11b1:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11f5 }
        if (r6 == 0) goto L_0x11b6;
    L_0x11b5:
        goto L_0x11ce;
    L_0x11b6:
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x11f5 }
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x11f5 }
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r4;
        r20 = r0;
        r21 = r13;
        r22 = r3;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x11f5 }
        r1.messageLayout = r4;	 Catch:{ Exception -> 0x11f5 }
        goto L_0x11f9;
    L_0x11ce:
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x11f5 }
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x11f5 }
        r6 = (float) r6;	 Catch:{ Exception -> 0x11f5 }
        r25 = 0;
        r26 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x11f5 }
        if (r8 == 0) goto L_0x11e2;
    L_0x11df:
        r28 = 1;
        goto L_0x11e4;
    L_0x11e2:
        r28 = 2;
    L_0x11e4:
        r19 = r0;
        r20 = r13;
        r21 = r3;
        r24 = r6;
        r27 = r3;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x11f5 }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x11f5 }
        goto L_0x11f9;
    L_0x11f5:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x11f9:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x132b;
    L_0x11fd:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x12b5;
    L_0x1201:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x12b5;
    L_0x1207:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r2);
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r2);
        r6 = (double) r4;
        r6 = java.lang.Math.ceil(r6);
        r4 = r1.dialogMuted;
        if (r4 == 0) goto L_0x124a;
    L_0x121c:
        r4 = r1.drawVerified;
        if (r4 != 0) goto L_0x124a;
    L_0x1220:
        r4 = r1.drawScam;
        if (r4 != 0) goto L_0x124a;
    L_0x1224:
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
        goto L_0x129d;
    L_0x124a:
        r4 = r1.drawVerified;
        if (r4 == 0) goto L_0x1274;
    L_0x124e:
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
        goto L_0x129d;
    L_0x1274:
        r4 = r1.drawScam;
        if (r4 == 0) goto L_0x129d;
    L_0x1278:
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
    L_0x129d:
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x12b5;
    L_0x12a2:
        r4 = (double) r5;
        r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x12b5;
    L_0x12a7:
        r0 = r1.nameLeft;
        r8 = (double) r0;
        java.lang.Double.isNaN(r4);
        r4 = r4 - r6;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r4;
        r0 = (int) r8;
        r1.nameLeft = r0;
    L_0x12b5:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x12f6;
    L_0x12b9:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x12f6;
    L_0x12bf:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = 0;
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x12c6:
        if (r4 >= r0) goto L_0x12ec;
    L_0x12c8:
        r6 = r1.messageLayout;
        r6 = r6.getLineLeft(r4);
        r7 = 0;
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 != 0) goto L_0x12eb;
    L_0x12d3:
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
        goto L_0x12c6;
    L_0x12eb:
        r5 = 0;
    L_0x12ec:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r5 == r0) goto L_0x12f6;
    L_0x12f1:
        r0 = r1.messageLeft;
        r0 = r0 + r5;
        r1.messageLeft = r0;
    L_0x12f6:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x13b5;
    L_0x12fa:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13b5;
    L_0x1300:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineLeft(r2);
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x13b5;
    L_0x130b:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineWidth(r2);
        r4 = (double) r0;
        r4 = java.lang.Math.ceil(r4);
        r2 = (double) r3;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x13b5;
    L_0x131b:
        r0 = r1.messageNameLeft;
        r6 = (double) r0;
        java.lang.Double.isNaN(r2);
        r2 = r2 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r2;
        r0 = (int) r6;
        r1.messageNameLeft = r0;
        goto L_0x13b5;
    L_0x132b:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x1379;
    L_0x132f:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1379;
    L_0x1335:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r2);
        r3 = (float) r5;
        r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r3 != 0) goto L_0x135e;
    L_0x1340:
        r3 = r1.nameLayout;
        r3 = r3.getLineWidth(r2);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r5;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x135e;
    L_0x1350:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r5;
        r3 = (int) r7;
        r1.nameLeft = r3;
    L_0x135e:
        r3 = r1.dialogMuted;
        if (r3 != 0) goto L_0x136a;
    L_0x1362:
        r3 = r1.drawVerified;
        if (r3 != 0) goto L_0x136a;
    L_0x1366:
        r3 = r1.drawScam;
        if (r3 == 0) goto L_0x1379;
    L_0x136a:
        r3 = r1.nameLeft;
        r3 = (float) r3;
        r3 = r3 + r0;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (float) r0;
        r3 = r3 + r0;
        r0 = (int) r3;
        r1.nameMuteLeft = r0;
    L_0x1379:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x139e;
    L_0x137d:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x139e;
    L_0x1383:
        r3 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r3 = 0;
        r4 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
    L_0x1388:
        if (r3 >= r0) goto L_0x1397;
    L_0x138a:
        r5 = r1.messageLayout;
        r5 = r5.getLineLeft(r3);
        r4 = java.lang.Math.min(r4, r5);
        r3 = r3 + 1;
        goto L_0x1388;
    L_0x1397:
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r0 = r0 - r4;
        r0 = (int) r0;
        r1.messageLeft = r0;
    L_0x139e:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x13b5;
    L_0x13a2:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13b5;
    L_0x13a8:
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r3 = r1.messageNameLayout;
        r2 = r3.getLineLeft(r2);
        r0 = r0 - r2;
        r0 = (int) r0;
        r1.messageNameLeft = r0;
    L_0x13b5:
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
        r2.setInfo(r4, r1, r3, r5);
        r6 = r0.avatarImage;
        r7 = 0;
        r9 = r0.avatarDrawable;
        r10 = 0;
        r11 = 0;
        r8 = "50_50";
        r6.setImage(r7, r8, r9, r10, r11);
        goto L_0x0300;
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
        if (r1 == 0) goto L_0x028b;
    L_0x01ff:
        r1 = (int) r6;
        r2 = 32;
        r2 = r6 >> r2;
        r3 = (int) r2;
        if (r1 == 0) goto L_0x0263;
    L_0x0207:
        if (r3 != r4) goto L_0x021a;
    L_0x0209:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getChat(r1);
        r0.chat = r1;
        goto L_0x028b;
    L_0x021a:
        if (r1 >= 0) goto L_0x0252;
    L_0x021c:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getChat(r1);
        r0.chat = r1;
        r1 = r0.isDialogCell;
        if (r1 != 0) goto L_0x028b;
    L_0x0231:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x028b;
    L_0x0235:
        r1 = r1.migrated_to;
        if (r1 == 0) goto L_0x028b;
    L_0x0239:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.chat;
        r2 = r2.migrated_to;
        r2 = r2.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        if (r1 == 0) goto L_0x028b;
    L_0x024f:
        r0.chat = r1;
        goto L_0x028b;
    L_0x0252:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getUser(r1);
        r0.user = r1;
        goto L_0x028b;
    L_0x0263:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = java.lang.Integer.valueOf(r3);
        r1 = r1.getEncryptedChat(r2);
        r0.encryptedChat = r1;
        r1 = r0.encryptedChat;
        if (r1 == 0) goto L_0x028b;
    L_0x0277:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.encryptedChat;
        r2 = r2.user_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        r0.user = r1;
    L_0x028b:
        r1 = r0.currentDialogFolderId;
        if (r1 == 0) goto L_0x02a8;
    L_0x028f:
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
        goto L_0x0300;
    L_0x02a8:
        r1 = r0.user;
        if (r1 == 0) goto L_0x02e4;
    L_0x02ac:
        r2 = r0.avatarDrawable;
        r2.setInfo(r1);
        r1 = r0.user;
        r1 = org.telegram.messenger.UserObject.isUserSelf(r1);
        if (r1 == 0) goto L_0x02cc;
    L_0x02b9:
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
        goto L_0x0300;
    L_0x02cc:
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
        goto L_0x0300;
    L_0x02e4:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x0300;
    L_0x02e8:
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
    L_0x0300:
        r1 = r19.getMeasuredWidth();
        if (r1 != 0) goto L_0x0311;
    L_0x0306:
        r1 = r19.getMeasuredHeight();
        if (r1 == 0) goto L_0x030d;
    L_0x030c:
        goto L_0x0311;
    L_0x030d:
        r19.requestLayout();
        goto L_0x0314;
    L_0x0311:
        r19.buildLayout();
    L_0x0314:
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
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09a5  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a10  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x09fc  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a4c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x08b4  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x089a  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x091d  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0907  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x093b  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09a5  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x09fc  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a10  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a4c  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x093b  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09a5  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a10  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x09fc  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a4c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x04b5  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0551  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x05d3  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x065e  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0844  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x093b  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09a5  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x09fc  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a10  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a4c  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x04b5  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0551  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x059b  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x05d3  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x065e  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0844  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x093b  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09a5  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0a10  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x09fc  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0a4c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0a74  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a8a  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0ad6  */
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
        r2 = android.os.SystemClock.uptimeMillis();
        r4 = r1.lastUpdateTime;
        r4 = r2 - r4;
        r6 = 17;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 <= 0) goto L_0x0021;
    L_0x001f:
        r4 = 17;
    L_0x0021:
        r9 = r4;
        r1.lastUpdateTime = r2;
        r0 = r1.clipProgress;
        r11 = 0;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0051;
    L_0x002b:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x0051;
    L_0x0031:
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
        r8.clipRect(r11, r0, r2, r3);
    L_0x0051:
        r0 = r1.translationX;
        r12 = 2;
        r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r14 = 0;
        r15 = 1;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 != 0) goto L_0x0080;
    L_0x005e:
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0065;
    L_0x0064:
        goto L_0x0080;
    L_0x0065:
        r0 = r1.translationDrawable;
        if (r0 == 0) goto L_0x007c;
    L_0x0069:
        r0.stop();
        r0 = r1.translationDrawable;
        r0.setProgress(r11);
        r0 = r1.translationDrawable;
        r2 = 0;
        r0.setCallback(r2);
        r0 = 0;
        r1.translationDrawable = r0;
        r1.translationAnimationStarted = r14;
    L_0x007c:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0279;
    L_0x0080:
        r24.save();
        r0 = r1.currentDialogFolderId;
        r16 = "chats_archiveBackground";
        r17 = "chats_archivePinBackground";
        if (r0 == 0) goto L_0x00bb;
    L_0x008b:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x00a5;
    L_0x008f:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0d0a5f float:1.87475E38 double:1.0531310893E-314;
        r4 = "UnhideFromTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00ea;
    L_0x00a5:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0d0506 float:1.8744723E38 double:1.053130413E-314;
        r4 = "HideOnTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00ea;
    L_0x00bb:
        r0 = r1.folderId;
        if (r0 != 0) goto L_0x00d5;
    L_0x00bf:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0d00f6 float:1.8742614E38 double:1.053129899E-314;
        r4 = "Archive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00ea;
    L_0x00d5:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0d0a56 float:1.8747481E38 double:1.053131085E-314;
        r4 = "Unarchive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable;
        r1.translationDrawable = r4;
    L_0x00ea:
        r6 = r2;
        r5 = r3;
        r2 = r1.translationAnimationStarted;
        r18 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        if (r2 != 0) goto L_0x0112;
    L_0x00f2:
        r2 = r1.translationX;
        r2 = java.lang.Math.abs(r2);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x0112;
    L_0x0101:
        r1.translationAnimationStarted = r15;
        r2 = r1.translationDrawable;
        r2.setProgress(r11);
        r2 = r1.translationDrawable;
        r2.setCallback(r1);
        r2 = r1.translationDrawable;
        r2.start();
    L_0x0112:
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r1.translationX;
        r4 = r2 + r3;
        r2 = r1.currentRevealProgress;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 >= 0) goto L_0x0163;
    L_0x0121:
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
        r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r2 != 0) goto L_0x0167;
    L_0x0151:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored;
        if (r2 == 0) goto L_0x0167;
    L_0x0155:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r4 = "Arrow.**";
        r2.setLayerColor(r4, r3);
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14;
        goto L_0x0167;
    L_0x0163:
        r15 = r4;
        r0 = r5;
        r22 = r6;
    L_0x0167:
        r2 = r23.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r3;
        r3 = r1.translationDrawable;
        r3 = r3.getIntrinsicWidth();
        r3 = r3 / r12;
        r2 = r2 - r3;
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x0184;
    L_0x017c:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x0181;
    L_0x0180:
        goto L_0x0184;
    L_0x0181:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x0186;
    L_0x0184:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x0186:
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
        r6 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r6 <= 0) goto L_0x01fb;
    L_0x01a0:
        r24.save();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r6 = (float) r6;
        r6 = r15 - r6;
        r7 = r23.getMeasuredWidth();
        r7 = (float) r7;
        r13 = r23.getMeasuredHeight();
        r13 = (float) r13;
        r8.clipRect(r6, r11, r7, r13);
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
        if (r4 != 0) goto L_0x01fb;
    L_0x01ed:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r6 = "Arrow.**";
        r4.setLayerColor(r6, r5);
        r4 = 1;
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r4;
    L_0x01fb:
        r24.save();
        r2 = (float) r2;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.currentRevealBounceProgress;
        r3 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r3 == 0) goto L_0x022a;
    L_0x0209:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1));
        if (r3 == 0) goto L_0x022c;
    L_0x020f:
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
        goto L_0x022c;
    L_0x022a:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x022c:
        r2 = r1.translationDrawable;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r14, r14);
        r2 = r1.translationDrawable;
        r2.draw(r8);
        r24.restore();
        r2 = r23.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r23.getMeasuredHeight();
        r3 = (float) r3;
        r8.clipRect(r15, r11, r2, r3);
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
        if (r3 != 0) goto L_0x026a;
    L_0x0262:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x0267;
    L_0x0266:
        goto L_0x026a;
    L_0x0267:
        r3 = NUM; // 0x426CLASSNAME float:59.0 double:5.50573981E-315;
        goto L_0x026c;
    L_0x026a:
        r3 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x026c:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint;
        r8.drawText(r0, r2, r3, r4);
        r24.restore();
    L_0x0279:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0287;
    L_0x027f:
        r24.save();
        r0 = r1.translationX;
        r8.translate(r0, r11);
    L_0x0287:
        r0 = r1.isSelected;
        if (r0 == 0) goto L_0x029e;
    L_0x028b:
        r3 = 0;
        r4 = 0;
        r0 = r23.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r23.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint;
        r2 = r24;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x029e:
        r0 = r1.currentDialogFolderId;
        r15 = "chats_pinnedOverlay";
        if (r0 == 0) goto L_0x02d1;
    L_0x02a4:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x02ae;
    L_0x02a8:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x02d1;
    L_0x02ae:
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
        goto L_0x02f5;
    L_0x02d1:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x02d9;
    L_0x02d5:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x02f5;
    L_0x02d9:
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
    L_0x02f5:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 != 0) goto L_0x0306;
    L_0x02fb:
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0302;
    L_0x0301:
        goto L_0x0306;
    L_0x0302:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03b6;
    L_0x0306:
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
        r0.set(r2, r11, r3, r4);
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
        if (r0 == 0) goto L_0x0383;
    L_0x034e:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x0358;
    L_0x0352:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0383;
    L_0x0358:
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
        goto L_0x038c;
    L_0x0383:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x038f;
    L_0x0387:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x038c;
    L_0x038b:
        goto L_0x038f;
    L_0x038c:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03b3;
    L_0x038f:
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
    L_0x03b3:
        r24.restore();
    L_0x03b6:
        r0 = r1.translationX;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x03d2;
    L_0x03be:
        r0 = r1.cornerProgress;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 >= 0) goto L_0x03e7;
    L_0x03c4:
        r4 = (float) r9;
        r4 = r4 / r3;
        r0 = r0 + r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x03e5;
    L_0x03cf:
        r1.cornerProgress = r13;
        goto L_0x03e5;
    L_0x03d2:
        r0 = r1.cornerProgress;
        r4 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r4 <= 0) goto L_0x03e7;
    L_0x03d8:
        r4 = (float) r9;
        r4 = r4 / r3;
        r0 = r0 - r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x03e5;
    L_0x03e3:
        r1.cornerProgress = r11;
    L_0x03e5:
        r4 = 1;
        goto L_0x03e8;
    L_0x03e7:
        r4 = 0;
    L_0x03e8:
        r0 = r1.drawNameLock;
        if (r0 == 0) goto L_0x03fb;
    L_0x03ec:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r0.draw(r8);
        goto L_0x0433;
    L_0x03fb:
        r0 = r1.drawNameGroup;
        if (r0 == 0) goto L_0x040e;
    L_0x03ff:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r0.draw(r8);
        goto L_0x0433;
    L_0x040e:
        r0 = r1.drawNameBroadcast;
        if (r0 == 0) goto L_0x0421;
    L_0x0412:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r0.draw(r8);
        goto L_0x0433;
    L_0x0421:
        r0 = r1.drawNameBot;
        if (r0 == 0) goto L_0x0433;
    L_0x0425:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r0.draw(r8);
    L_0x0433:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x0495;
    L_0x0437:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0449;
    L_0x043b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_nameArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0471;
    L_0x0449:
        r0 = r1.encryptedChat;
        if (r0 != 0) goto L_0x0464;
    L_0x044d:
        r0 = r1.customDialog;
        if (r0 == 0) goto L_0x0456;
    L_0x0451:
        r0 = r0.type;
        if (r0 != r12) goto L_0x0456;
    L_0x0455:
        goto L_0x0464;
    L_0x0456:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_name";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0471;
    L_0x0464:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_secretName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0471:
        r24.save();
        r0 = r1.nameLeft;
        r0 = (float) r0;
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x0483;
    L_0x047b:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x0480;
    L_0x047f:
        goto L_0x0483;
    L_0x0480:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        goto L_0x0485;
    L_0x0483:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x0485:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.nameLayout;
        r0.draw(r8);
        r24.restore();
    L_0x0495:
        r0 = r1.timeLayout;
        if (r0 == 0) goto L_0x04b1;
    L_0x0499:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x04b1;
    L_0x049d:
        r24.save();
        r0 = r1.timeLeft;
        r0 = (float) r0;
        r5 = r1.timeTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.timeLayout;
        r0.draw(r8);
        r24.restore();
    L_0x04b1:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x04ff;
    L_0x04b5:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x04c7;
    L_0x04b9:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessageArchived_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x04e6;
    L_0x04c7:
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x04d9;
    L_0x04cb:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_draft";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x04e6;
    L_0x04d9:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessage_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x04e6:
        r24.save();
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r5 = r1.messageNameTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageNameLayout;	 Catch:{ Exception -> 0x04f8 }
        r0.draw(r8);	 Catch:{ Exception -> 0x04f8 }
        goto L_0x04fc;
    L_0x04f8:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x04fc:
        r24.restore();
    L_0x04ff:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x054d;
    L_0x0503:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0527;
    L_0x0507:
        r0 = r1.chat;
        if (r0 == 0) goto L_0x0519;
    L_0x050b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_nameMessageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0534;
    L_0x0519:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_messageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0534;
    L_0x0527:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_message";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0534:
        r24.save();
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r5 = r1.messageTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageLayout;	 Catch:{ Exception -> 0x0546 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0546 }
        goto L_0x054a;
    L_0x0546:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x054a:
        r24.restore();
    L_0x054d:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x0597;
    L_0x0551:
        r0 = r1.drawClock;
        if (r0 == 0) goto L_0x0564;
    L_0x0555:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r0.draw(r8);
        goto L_0x0597;
    L_0x0564:
        r0 = r1.drawCheck2;
        if (r0 == 0) goto L_0x0597;
    L_0x0568:
        r0 = r1.drawCheck1;
        if (r0 == 0) goto L_0x0589;
    L_0x056c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r5 = r1.halfCheckDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r0.draw(r8);
        goto L_0x0597;
    L_0x0589:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r0.draw(r8);
    L_0x0597:
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x05cf;
    L_0x059b:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x05cf;
    L_0x059f:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x05cf;
    L_0x05a3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05b3;
    L_0x05ab:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05b0;
    L_0x05af:
        goto L_0x05b3;
    L_0x05b0:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x05b4;
    L_0x05b3:
        r6 = 0;
    L_0x05b4:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05c0;
    L_0x05bd:
        r6 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        goto L_0x05c2;
    L_0x05c0:
        r6 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
    L_0x05c2:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r0.draw(r8);
        goto L_0x0632;
    L_0x05cf:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0610;
    L_0x05d3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05e3;
    L_0x05db:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05e0;
    L_0x05df:
        goto L_0x05e3;
    L_0x05e0:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x05e5;
    L_0x05e3:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x05e5:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05fc;
    L_0x05f4:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05f9;
    L_0x05f8:
        goto L_0x05fc;
    L_0x05f9:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x05fe;
    L_0x05fc:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x05fe:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r0.draw(r8);
        goto L_0x0632;
    L_0x0610:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0632;
    L_0x0614:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x0624;
    L_0x061c:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x0621;
    L_0x0620:
        goto L_0x0624;
    L_0x0621:
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        goto L_0x0626;
    L_0x0624:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x0626:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r0.draw(r8);
    L_0x0632:
        r0 = r1.drawReorder;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 != 0) goto L_0x063e;
    L_0x0638:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0656;
    L_0x063e:
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
    L_0x0656:
        r0 = r1.drawError;
        r6 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r7 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        if (r0 == 0) goto L_0x06af;
    L_0x065e:
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
        goto L_0x07fe;
    L_0x06af:
        r0 = r1.drawCount;
        if (r0 != 0) goto L_0x06d8;
    L_0x06b3:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x06b8;
    L_0x06b7:
        goto L_0x06d8;
    L_0x06b8:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x07fe;
    L_0x06bc:
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
        goto L_0x07fe;
    L_0x06d8:
        r0 = r1.drawCount;
        if (r0 == 0) goto L_0x0750;
    L_0x06dc:
        r0 = r1.dialogMuted;
        if (r0 != 0) goto L_0x06e8;
    L_0x06e0:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x06e5;
    L_0x06e4:
        goto L_0x06e8;
    L_0x06e5:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        goto L_0x06ea;
    L_0x06e8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
    L_0x06ea:
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
        if (r0 == 0) goto L_0x0750;
    L_0x0735:
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
    L_0x0750:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x07fe;
    L_0x0754:
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
        if (r0 == 0) goto L_0x0790;
    L_0x0789:
        r0 = r1.folderId;
        if (r0 == 0) goto L_0x0790;
    L_0x078d:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
        goto L_0x0792;
    L_0x0790:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
    L_0x0792:
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r3 * r7;
        r3 = r3 * r7;
        r8.drawRoundRect(r2, r6, r3, r0);
        r0 = r1.mentionLayout;
        if (r0 == 0) goto L_0x07c9;
    L_0x07a1:
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
        goto L_0x07fe;
    L_0x07c9:
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
    L_0x07fe:
        r0 = r1.animatingArchiveAvatar;
        r12 = NUM; // 0x432a0000 float:170.0 double:5.567260075E-315;
        if (r0 == 0) goto L_0x0820;
    L_0x0804:
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
    L_0x0820:
        r0 = r1.avatarImage;
        r0.draw(r8);
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x082c;
    L_0x0829:
        r24.restore();
    L_0x082c:
        r0 = r1.user;
        if (r0 == 0) goto L_0x0934;
    L_0x0830:
        r2 = r1.isDialogCell;
        if (r2 == 0) goto L_0x0934;
    L_0x0834:
        r2 = r1.currentDialogFolderId;
        if (r2 != 0) goto L_0x0934;
    L_0x0838:
        r0 = org.telegram.messenger.MessagesController.isSupportUser(r0);
        if (r0 != 0) goto L_0x0934;
    L_0x083e:
        r0 = r1.user;
        r2 = r0.bot;
        if (r2 != 0) goto L_0x0934;
    L_0x0844:
        r2 = r0.self;
        if (r2 != 0) goto L_0x0872;
    L_0x0848:
        r0 = r0.status;
        if (r0 == 0) goto L_0x085a;
    L_0x084c:
        r0 = r0.expires;
        r2 = r1.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        if (r0 > r2) goto L_0x0870;
    L_0x085a:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.onlinePrivacy;
        r2 = r1.user;
        r2 = r2.id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.containsKey(r2);
        if (r0 == 0) goto L_0x0872;
    L_0x0870:
        r0 = 1;
        goto L_0x0873;
    L_0x0872:
        r0 = 0;
    L_0x0873:
        if (r0 != 0) goto L_0x087b;
    L_0x0875:
        r2 = r1.onlineProgress;
        r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r2 == 0) goto L_0x0934;
    L_0x087b:
        r2 = r1.avatarImage;
        r2 = r2.getImageY2();
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x088d;
    L_0x0885:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x088a;
    L_0x0889:
        goto L_0x088d;
    L_0x088a:
        r16 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x0891;
    L_0x088d:
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
    L_0x0891:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r2 - r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x08b4;
    L_0x089a:
        r3 = r1.avatarImage;
        r3 = r3.getImageX();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x08ac;
    L_0x08a4:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x08a9;
    L_0x08a8:
        goto L_0x08ac;
    L_0x08a9:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x08ae;
    L_0x08ac:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x08ae:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        goto L_0x08cd;
    L_0x08b4:
        r3 = r1.avatarImage;
        r3 = r3.getImageX2();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x08c6;
    L_0x08be:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x08c3;
    L_0x08c2:
        goto L_0x08c6;
    L_0x08c3:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x08c8;
    L_0x08c6:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x08c8:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 - r5;
    L_0x08cd:
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
        if (r0 == 0) goto L_0x091d;
    L_0x0907:
        r0 = r1.onlineProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0934;
    L_0x090d:
        r2 = (float) r9;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0932;
    L_0x091a:
        r1.onlineProgress = r13;
        goto L_0x0932;
    L_0x091d:
        r0 = r1.onlineProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0934;
    L_0x0923:
        r2 = (float) r9;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0932;
    L_0x0930:
        r1.onlineProgress = r11;
    L_0x0932:
        r15 = 1;
        goto L_0x0935;
    L_0x0934:
        r15 = r4;
    L_0x0935:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x093e;
    L_0x093b:
        r24.restore();
    L_0x093e:
        r0 = r1.useSeparator;
        if (r0 == 0) goto L_0x099e;
    L_0x0942:
        r0 = r1.fullSeparator;
        if (r0 != 0) goto L_0x0962;
    L_0x0946:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0952;
    L_0x094a:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0952;
    L_0x094e:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x0962;
    L_0x0952:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x095b;
    L_0x0956:
        r0 = r1.archiveHidden;
        if (r0 != 0) goto L_0x095b;
    L_0x095a:
        goto L_0x0962;
    L_0x095b:
        r0 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r0);
        goto L_0x0963;
    L_0x0962:
        r14 = 0;
    L_0x0963:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x0983;
    L_0x0967:
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
        goto L_0x099e;
    L_0x0983:
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
        goto L_0x099f;
    L_0x099e:
        r14 = 1;
    L_0x099f:
        r0 = r1.clipProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x09ee;
    L_0x09a5:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x09af;
    L_0x09ab:
        r24.restore();
        goto L_0x09ee;
    L_0x09af:
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
    L_0x09ee:
        r0 = r1.drawReorder;
        if (r0 != 0) goto L_0x09f8;
    L_0x09f2:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0a24;
    L_0x09f8:
        r0 = r1.drawReorder;
        if (r0 == 0) goto L_0x0a10;
    L_0x09fc:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0a24;
    L_0x0a02:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a23;
    L_0x0a0d:
        r1.reorderIconProgress = r13;
        goto L_0x0a23;
    L_0x0a10:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a24;
    L_0x0a16:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a23;
    L_0x0a21:
        r1.reorderIconProgress = r11;
    L_0x0a23:
        r15 = 1;
    L_0x0a24:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0a4c;
    L_0x0a28:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a70;
    L_0x0a2e:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a3b;
    L_0x0a39:
        r1.currentRevealBounceProgress = r11;
    L_0x0a3b:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0a6f;
    L_0x0a44:
        r0 = r1.avatarDrawable;
        r2 = r1.archiveBackgroundProgress;
        r0.setArchivedAvatarHiddenProgress(r2);
        goto L_0x0a6f;
    L_0x0a4c:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0a70;
    L_0x0a52:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a5f;
    L_0x0a5d:
        r1.currentRevealBounceProgress = r13;
    L_0x0a5f:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0a6f;
    L_0x0a68:
        r0 = r1.avatarDrawable;
        r2 = r1.archiveBackgroundProgress;
        r0.setArchivedAvatarHiddenProgress(r2);
    L_0x0a6f:
        r15 = 1;
    L_0x0a70:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0a86;
    L_0x0a74:
        r0 = r1.animatingArchiveAvatarProgress;
        r2 = (float) r9;
        r0 = r0 + r2;
        r1.animatingArchiveAvatarProgress = r0;
        r0 = r1.animatingArchiveAvatarProgress;
        r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1));
        if (r0 < 0) goto L_0x0a85;
    L_0x0a80:
        r1.animatingArchiveAvatarProgress = r12;
        r2 = 0;
        r1.animatingArchiveAvatar = r2;
    L_0x0a85:
        r15 = 1;
    L_0x0a86:
        r0 = r1.drawRevealBackground;
        if (r0 == 0) goto L_0x0ab4;
    L_0x0a8a:
        r0 = r1.currentRevealBounceProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0a9e;
    L_0x0a90:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.currentRevealBounceProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a9e;
    L_0x0a9b:
        r1.currentRevealBounceProgress = r13;
        r15 = 1;
    L_0x0a9e:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0ad3;
    L_0x0aa4:
        r2 = (float) r9;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0ad4;
    L_0x0ab1:
        r1.currentRevealProgress = r13;
        goto L_0x0ad4;
    L_0x0ab4:
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x0abd;
    L_0x0aba:
        r1.currentRevealBounceProgress = r11;
        r15 = 1;
    L_0x0abd:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ad3;
    L_0x0ac3:
        r2 = (float) r9;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0ad4;
    L_0x0ad0:
        r1.currentRevealProgress = r11;
        goto L_0x0ad4;
    L_0x0ad3:
        r14 = r15;
    L_0x0ad4:
        if (r14 == 0) goto L_0x0ad9;
    L_0x0ad6:
        r23.invalidate();
    L_0x0ad9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
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
}
