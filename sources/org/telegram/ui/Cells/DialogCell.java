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
import com.airbnb.lottie.LottieDrawable;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
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
    private LottieDrawable translationDrawable;
    private float translationX;
    private int unreadCount;
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

    public DialogCell(Context context, boolean z) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        if (z) {
            this.checkBox = new CheckBox2(context);
            this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setSize(21);
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
        LottieDrawable lottieDrawable = this.translationDrawable;
        if (lottieDrawable != null) {
            lottieDrawable.stop();
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
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) + this.useSeparator);
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
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) dialog.id));
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

    /* JADX WARNING: Removed duplicated region for block: B:441:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0b2c  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0b2c  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0bbb  */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x0baa  */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x11be A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1197 A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:775:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x11dc  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1197 A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x11be A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x11dc  */
    /* JADX WARNING: Removed duplicated region for block: B:775:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0d63  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0d37  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0e21  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0e0b  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0ef3  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0e77  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x10de  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1160  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x1157  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x117e A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x11be A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1197 A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:775:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x11dc  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0cbd  */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x0c7c  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0cd9  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0cc9  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0d00  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x0cf1  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0d37  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0d63  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0de9  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0e0b  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0e21  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0e77  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0ef3  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x10de  */
    /* JADX WARNING: Removed duplicated region for block: B:690:0x111c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x1157  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1160  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x116c A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x117e A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1197 A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x11be A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x11dc  */
    /* JADX WARNING: Removed duplicated region for block: B:775:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x0c7c  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0cbd  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0cc9  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0cd9  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x0cf1  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0d00  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0d63  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0d37  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0de9  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0e21  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0e0b  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0ef3  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0e77  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x10de  */
    /* JADX WARNING: Removed duplicated region for block: B:690:0x111c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x1160  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x1157  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x116c A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x117e A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x11be A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1197 A:{Catch:{ Exception -> 0x11d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:775:0x130a  */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x11dc  */
    /* JADX WARNING: Missing block: B:230:0x05a8, code skipped:
            if (r10.post_messages != false) goto L_0x05aa;
     */
    public void buildLayout() {
        /*
        r30 = this;
        r1 = r30;
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        if (r0 == 0) goto L_0x0042;
    L_0x0008:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r3 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = "chats_message_threeLines";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.linkColor = r3;
        r0.setColor(r3);
        goto L_0x007b;
    L_0x0042:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r3 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r3 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r0.setTextSize(r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r3 = "chats_message";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.linkColor = r3;
        r0.setColor(r3);
    L_0x007b:
        r3 = 0;
        r1.currentDialogFolderDialogsCount = r3;
        r0 = r1.isDialogCell;
        if (r0 == 0) goto L_0x0093;
    L_0x0082:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.printingStrings;
        r5 = r1.currentDialogId;
        r0 = r0.get(r5);
        r0 = (java.lang.CharSequence) r0;
        goto L_0x0094;
    L_0x0093:
        r0 = 0;
    L_0x0094:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r1.drawNameGroup = r3;
        r1.drawNameBroadcast = r3;
        r1.drawNameLock = r3;
        r1.drawNameBot = r3;
        r1.drawVerified = r3;
        r1.drawScam = r3;
        r1.drawPinBackground = r3;
        r6 = r1.user;
        r6 = org.telegram.messenger.UserObject.isUserSelf(r6);
        r7 = 1;
        r6 = r6 ^ r7;
        r8 = android.os.Build.VERSION.SDK_INT;
        r9 = 18;
        if (r8 < r9) goto L_0x00c2;
    L_0x00b2:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x00bf;
    L_0x00b6:
        r8 = r1.currentDialogFolderId;
        if (r8 == 0) goto L_0x00bb;
    L_0x00ba:
        goto L_0x00bf;
    L_0x00bb:
        r8 = "⁨%s⁩";
        goto L_0x00cd;
    L_0x00bf:
        r8 = "%2$s: ⁨%1$s⁩";
        goto L_0x00d1;
    L_0x00c2:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x00cf;
    L_0x00c6:
        r8 = r1.currentDialogFolderId;
        if (r8 == 0) goto L_0x00cb;
    L_0x00ca:
        goto L_0x00cf;
    L_0x00cb:
        r8 = "%1$s";
    L_0x00cd:
        r9 = 0;
        goto L_0x00d2;
    L_0x00cf:
        r8 = "%2$s: %1$s";
    L_0x00d1:
        r9 = 1;
    L_0x00d2:
        r10 = r1.message;
        if (r10 == 0) goto L_0x00d9;
    L_0x00d6:
        r10 = r10.messageText;
        goto L_0x00da;
    L_0x00d9:
        r10 = 0;
    L_0x00da:
        r1.lastMessageString = r10;
        r10 = r1.customDialog;
        r11 = 32;
        r12 = 10;
        r13 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r14 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r15 = NUM; // 0x429CLASSNAME float:78.0 double:5.521281773E-315;
        r16 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r2 = 2;
        r17 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = "";
        if (r10 == 0) goto L_0x0315;
    L_0x00f1:
        r0 = r10.type;
        if (r0 != r2) goto L_0x0175;
    L_0x00f5:
        r1.drawNameLock = r7;
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0138;
    L_0x00fb:
        r0 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x011e;
    L_0x0107:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x011e:
        r0 = r30.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0 = r0 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x0138:
        r0 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x015b;
    L_0x0144:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x015b:
        r0 = r30.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x0175:
        r6 = r10.verified;
        r1.drawVerified = r6;
        r6 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r6 == 0) goto L_0x0219;
    L_0x017d:
        if (r0 != r7) goto L_0x0219;
    L_0x017f:
        r1.drawNameGroup = r7;
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x01d0;
    L_0x0185:
        r0 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01af;
    L_0x0191:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x01a4;
    L_0x01a1:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01a6;
    L_0x01a4:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01a6:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x01af:
        r0 = r30.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0 = r0 - r6;
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x01bf;
    L_0x01bc:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01c1;
    L_0x01bf:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01c1:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x01d0:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01f9;
    L_0x01d4:
        r0 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x01ef;
    L_0x01ec:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01f1;
    L_0x01ef:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01f1:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x01f9:
        r0 = r30.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r6;
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x0209;
    L_0x0206:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x020b;
    L_0x0209:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x020b:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x0219:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x022f;
    L_0x021d:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0228;
    L_0x0221:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x0228:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x022f:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x023a;
    L_0x0233:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r0;
        goto L_0x0240;
    L_0x023a:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r0;
    L_0x0240:
        r0 = r1.customDialog;
        r6 = r0.type;
        if (r6 != r7) goto L_0x02bf;
    L_0x0246:
        r0 = NUM; // 0x7f0d0473 float:1.8744425E38 double:1.0531303403E-314;
        r6 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r6 = r1.customDialog;
        r9 = r6.isMedia;
        if (r9 == 0) goto L_0x027c;
    L_0x0255:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r6 = new java.lang.Object[r7];
        r9 = r1.message;
        r9 = r9.messageText;
        r6[r3] = r9;
        r6 = java.lang.String.format(r8, r6);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
        r8 = new android.text.style.ForegroundColorSpan;
        r9 = "chats_attachMessage";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r8.<init>(r9);
        r9 = r6.length();
        r10 = 33;
        r6.setSpan(r8, r3, r9, r10);
        goto L_0x02ad;
    L_0x027c:
        r6 = r6.message;
        r9 = r6.length();
        if (r9 <= r13) goto L_0x0288;
    L_0x0284:
        r6 = r6.substring(r3, r13);
    L_0x0288:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x029b;
    L_0x028c:
        r9 = new java.lang.Object[r2];
        r9[r3] = r6;
        r9[r7] = r0;
        r6 = java.lang.String.format(r8, r9);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
        goto L_0x02ad;
    L_0x029b:
        r9 = new java.lang.Object[r2];
        r6 = r6.replace(r12, r11);
        r9[r3] = r6;
        r9[r7] = r0;
        r6 = java.lang.String.format(r8, r9);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
    L_0x02ad:
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r8 = r8.getFontMetricsInt();
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r8, r9, r3);
        r8 = r5;
        r5 = r0;
        r0 = 0;
        goto L_0x02ca;
    L_0x02bf:
        r6 = r0.message;
        r0 = r0.isMedia;
        if (r0 == 0) goto L_0x02c7;
    L_0x02c5:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x02c7:
        r8 = r5;
        r0 = 1;
        r5 = 0;
    L_0x02ca:
        r9 = r1.customDialog;
        r9 = r9.date;
        r9 = (long) r9;
        r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9);
        r10 = r1.customDialog;
        r10 = r10.unread_count;
        if (r10 == 0) goto L_0x02ea;
    L_0x02d9:
        r1.drawCount = r7;
        r11 = new java.lang.Object[r7];
        r10 = java.lang.Integer.valueOf(r10);
        r11[r3] = r10;
        r10 = "%d";
        r10 = java.lang.String.format(r10, r11);
        goto L_0x02ed;
    L_0x02ea:
        r1.drawCount = r3;
        r10 = 0;
    L_0x02ed:
        r11 = r1.customDialog;
        r11 = r11.sent;
        if (r11 == 0) goto L_0x02fc;
    L_0x02f3:
        r1.drawCheck1 = r7;
        r1.drawCheck2 = r7;
        r1.drawClock = r3;
        r1.drawError = r3;
        goto L_0x0304;
    L_0x02fc:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r3;
    L_0x0304:
        r11 = r1.customDialog;
        r11 = r11.name;
        r13 = r8;
        r2 = r10;
        r8 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 0;
        r29 = r9;
        r9 = r6;
        r6 = r29;
        goto L_0x0c7a;
    L_0x0315:
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 == 0) goto L_0x032b;
    L_0x0319:
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x0324;
    L_0x031d:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r10;
        goto L_0x033c;
    L_0x0324:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r10;
        goto L_0x033c;
    L_0x032b:
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x0336;
    L_0x032f:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r10;
        goto L_0x033c;
    L_0x0336:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r10;
    L_0x033c:
        r10 = r1.encryptedChat;
        if (r10 == 0) goto L_0x03c4;
    L_0x0340:
        r10 = r1.currentDialogFolderId;
        if (r10 != 0) goto L_0x0553;
    L_0x0344:
        r1.drawNameLock = r7;
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 == 0) goto L_0x0387;
    L_0x034a:
        r10 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x036d;
    L_0x0356:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x036d:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = r10 - r11;
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x0387:
        r10 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x03aa;
    L_0x0393:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x03aa:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r10 = r10 - r11;
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x03c4:
        r10 = r1.currentDialogFolderId;
        if (r10 != 0) goto L_0x0553;
    L_0x03c8:
        r10 = r1.chat;
        if (r10 == 0) goto L_0x04ba;
    L_0x03cc:
        r11 = r10.scam;
        if (r11 == 0) goto L_0x03d8;
    L_0x03d0:
        r1.drawScam = r7;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r10.checkText();
        goto L_0x03dc;
    L_0x03d8:
        r10 = r10.verified;
        r1.drawVerified = r10;
    L_0x03dc:
        r10 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r10 == 0) goto L_0x0553;
    L_0x03e0:
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 == 0) goto L_0x044f;
    L_0x03e4:
        r10 = r1.chat;
        r11 = r10.id;
        if (r11 < 0) goto L_0x0402;
    L_0x03ea:
        r10 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r10 == 0) goto L_0x03f7;
    L_0x03f0:
        r10 = r1.chat;
        r10 = r10.megagroup;
        if (r10 != 0) goto L_0x03f7;
    L_0x03f6:
        goto L_0x0402;
    L_0x03f7:
        r1.drawNameGroup = r7;
        r10 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        goto L_0x040c;
    L_0x0402:
        r1.drawNameBroadcast = r7;
        r10 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
    L_0x040c:
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x042e;
    L_0x0410:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = r1.drawNameGroup;
        if (r11 == 0) goto L_0x0423;
    L_0x0420:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0425;
    L_0x0423:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0425:
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x042e:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = r10 - r11;
        r11 = r1.drawNameGroup;
        if (r11 == 0) goto L_0x043e;
    L_0x043b:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0440;
    L_0x043e:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0440:
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x044f:
        r10 = r1.chat;
        r11 = r10.id;
        if (r11 < 0) goto L_0x046d;
    L_0x0455:
        r10 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r10 == 0) goto L_0x0462;
    L_0x045b:
        r10 = r1.chat;
        r10 = r10.megagroup;
        if (r10 != 0) goto L_0x0462;
    L_0x0461:
        goto L_0x046d;
    L_0x0462:
        r1.drawNameGroup = r7;
        r10 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        goto L_0x0477;
    L_0x046d:
        r1.drawNameBroadcast = r7;
        r10 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
    L_0x0477:
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x0499;
    L_0x047b:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = r1.drawNameGroup;
        if (r11 == 0) goto L_0x048e;
    L_0x048b:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0490;
    L_0x048e:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0490:
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x0499:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r10 = r10 - r11;
        r11 = r1.drawNameGroup;
        if (r11 == 0) goto L_0x04a9;
    L_0x04a6:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04ab;
    L_0x04a9:
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04ab:
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x04ba:
        r10 = r1.user;
        if (r10 == 0) goto L_0x0553;
    L_0x04be:
        r11 = r10.scam;
        if (r11 == 0) goto L_0x04ca;
    L_0x04c2:
        r1.drawScam = r7;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r10.checkText();
        goto L_0x04ce;
    L_0x04ca:
        r10 = r10.verified;
        r1.drawVerified = r10;
    L_0x04ce:
        r10 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r10 == 0) goto L_0x0553;
    L_0x04d2:
        r10 = r1.user;
        r10 = r10.bot;
        if (r10 == 0) goto L_0x0553;
    L_0x04d8:
        r1.drawNameBot = r7;
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 == 0) goto L_0x0519;
    L_0x04de:
        r10 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x0500;
    L_0x04ea:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x0500:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = r10 - r11;
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x0519:
        r10 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1.nameLockTop = r10;
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 != 0) goto L_0x053b;
    L_0x0525:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r10;
        r10 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 + r11;
        r1.nameLeft = r10;
        goto L_0x0553;
    L_0x053b:
        r10 = r30.getMeasuredWidth();
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r10 = r10 - r11;
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r11 = r11.getIntrinsicWidth();
        r10 = r10 - r11;
        r1.nameLockLeft = r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.nameLeft = r10;
    L_0x0553:
        r10 = r1.lastMessageDate;
        if (r10 != 0) goto L_0x055f;
    L_0x0557:
        r11 = r1.message;
        if (r11 == 0) goto L_0x055f;
    L_0x055b:
        r10 = r11.messageOwner;
        r10 = r10.date;
    L_0x055f:
        r11 = r1.isDialogCell;
        if (r11 == 0) goto L_0x05bc;
    L_0x0563:
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.DataQuery.getInstance(r11);
        r14 = r1.currentDialogId;
        r11 = r11.getDraft(r14);
        r1.draftMessage = r11;
        r11 = r1.draftMessage;
        if (r11 == 0) goto L_0x0590;
    L_0x0575:
        r11 = r11.message;
        r11 = android.text.TextUtils.isEmpty(r11);
        if (r11 == 0) goto L_0x0586;
    L_0x057d:
        r11 = r1.draftMessage;
        r11 = r11.reply_to_msg_id;
        if (r11 == 0) goto L_0x0584;
    L_0x0583:
        goto L_0x0586;
    L_0x0584:
        r10 = 0;
        goto L_0x05b7;
    L_0x0586:
        r11 = r1.draftMessage;
        r11 = r11.date;
        if (r10 <= r11) goto L_0x0590;
    L_0x058c:
        r10 = r1.unreadCount;
        if (r10 != 0) goto L_0x0584;
    L_0x0590:
        r10 = r1.chat;
        r10 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r10 == 0) goto L_0x05aa;
    L_0x0598:
        r10 = r1.chat;
        r11 = r10.megagroup;
        if (r11 != 0) goto L_0x05aa;
    L_0x059e:
        r11 = r10.creator;
        if (r11 != 0) goto L_0x05aa;
    L_0x05a2:
        r10 = r10.admin_rights;
        if (r10 == 0) goto L_0x0584;
    L_0x05a6:
        r10 = r10.post_messages;
        if (r10 == 0) goto L_0x0584;
    L_0x05aa:
        r10 = r1.chat;
        if (r10 == 0) goto L_0x05ba;
    L_0x05ae:
        r11 = r10.left;
        if (r11 != 0) goto L_0x0584;
    L_0x05b2:
        r10 = r10.kicked;
        if (r10 == 0) goto L_0x05ba;
    L_0x05b6:
        goto L_0x0584;
    L_0x05b7:
        r1.draftMessage = r10;
        goto L_0x05bf;
    L_0x05ba:
        r10 = 0;
        goto L_0x05bf;
    L_0x05bc:
        r10 = 0;
        r1.draftMessage = r10;
    L_0x05bf:
        if (r0 == 0) goto L_0x05cc;
    L_0x05c1:
        r1.lastPrintString = r0;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r8 = r0;
        r9 = r10;
        r0 = 1;
        r10 = r5;
        r5 = 1;
        goto L_0x0a57;
    L_0x05cc:
        r1.lastPrintString = r10;
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x0669;
    L_0x05d2:
        r0 = NUM; // 0x7f0d035b float:1.8743857E38 double:1.053130202E-314;
        r9 = "Draft";
        r0 = org.telegram.messenger.LocaleController.getString(r9, r0);
        r9 = r1.draftMessage;
        r9 = r9.message;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 == 0) goto L_0x060a;
    L_0x05e5:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x05ec;
    L_0x05e9:
        r9 = r0;
        r8 = r4;
        goto L_0x0605;
    L_0x05ec:
        r8 = android.text.SpannableStringBuilder.valueOf(r0);
        r9 = new android.text.style.ForegroundColorSpan;
        r10 = "chats_draft";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r9.<init>(r10);
        r10 = r0.length();
        r11 = 33;
        r8.setSpan(r9, r3, r10, r11);
    L_0x0604:
        r9 = r0;
    L_0x0605:
        r10 = r5;
        r0 = 1;
        r5 = 0;
        goto L_0x0a57;
    L_0x060a:
        r9 = r1.draftMessage;
        r9 = r9.message;
        r10 = r9.length();
        if (r10 <= r13) goto L_0x0618;
    L_0x0614:
        r9 = r9.substring(r3, r13);
    L_0x0618:
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 == 0) goto L_0x0631;
    L_0x061c:
        r10 = new java.lang.Object[r2];
        r11 = 32;
        r9 = r9.replace(r12, r11);
        r10[r3] = r9;
        r10[r7] = r0;
        r8 = java.lang.String.format(r8, r10);
        r8 = android.text.SpannableStringBuilder.valueOf(r8);
        goto L_0x065a;
    L_0x0631:
        r11 = 32;
        r10 = new java.lang.Object[r2];
        r9 = r9.replace(r12, r11);
        r10[r3] = r9;
        r10[r7] = r0;
        r8 = java.lang.String.format(r8, r10);
        r8 = android.text.SpannableStringBuilder.valueOf(r8);
        r9 = new android.text.style.ForegroundColorSpan;
        r10 = "chats_draft";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r9.<init>(r10);
        r10 = r0.length();
        r10 = r10 + r7;
        r11 = 33;
        r8.setSpan(r9, r3, r10, r11);
    L_0x065a:
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r9 = r9.getFontMetricsInt();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r10, r3);
        goto L_0x0604;
    L_0x0669:
        r0 = r1.clearingDialog;
        if (r0 == 0) goto L_0x067f;
    L_0x066d:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = NUM; // 0x7f0d04aa float:1.8744536E38 double:1.0531303675E-314;
        r8 = "HistoryCleared";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
    L_0x0678:
        r8 = r0;
    L_0x0679:
        r10 = r5;
        r0 = 1;
        r5 = 1;
        r9 = 0;
        goto L_0x0a57;
    L_0x067f:
        r0 = r1.message;
        if (r0 != 0) goto L_0x0712;
    L_0x0683:
        r0 = r1.encryptedChat;
        if (r0 == 0) goto L_0x070f;
    L_0x0687:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
        if (r8 == 0) goto L_0x0697;
    L_0x068d:
        r0 = NUM; // 0x7f0d039a float:1.8743985E38 double:1.053130233E-314;
        r8 = "EncryptionProcessing";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        goto L_0x0678;
    L_0x0697:
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
        if (r8 == 0) goto L_0x06bf;
    L_0x069b:
        r0 = r1.user;
        if (r0 == 0) goto L_0x06b1;
    L_0x069f:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x06b1;
    L_0x06a3:
        r8 = NUM; // 0x7f0d0193 float:1.8742932E38 double:1.0531299767E-314;
        r9 = new java.lang.Object[r7];
        r9[r3] = r0;
        r0 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r8, r9);
        goto L_0x0678;
    L_0x06b1:
        r0 = NUM; // 0x7f0d0193 float:1.8742932E38 double:1.0531299767E-314;
        r8 = new java.lang.Object[r7];
        r8[r3] = r4;
        r9 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r0, r8);
        goto L_0x0678;
    L_0x06bf:
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
        if (r8 == 0) goto L_0x06cd;
    L_0x06c3:
        r0 = NUM; // 0x7f0d039b float:1.8743987E38 double:1.0531302336E-314;
        r8 = "EncryptionRejected";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        goto L_0x0678;
    L_0x06cd:
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r8 == 0) goto L_0x070f;
    L_0x06d1:
        r0 = r0.admin_id;
        r8 = r1.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.getClientUserId();
        if (r0 != r8) goto L_0x0704;
    L_0x06df:
        r0 = r1.user;
        if (r0 == 0) goto L_0x06f5;
    L_0x06e3:
        r0 = r0.first_name;
        if (r0 == 0) goto L_0x06f5;
    L_0x06e7:
        r8 = NUM; // 0x7f0d038f float:1.8743962E38 double:1.0531302276E-314;
        r9 = new java.lang.Object[r7];
        r9[r3] = r0;
        r0 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r8, r9);
        goto L_0x0678;
    L_0x06f5:
        r0 = NUM; // 0x7f0d038f float:1.8743962E38 double:1.0531302276E-314;
        r8 = new java.lang.Object[r7];
        r8[r3] = r4;
        r9 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r0, r8);
        goto L_0x0678;
    L_0x0704:
        r0 = NUM; // 0x7f0d038e float:1.874396E38 double:1.053130227E-314;
        r8 = "EncryptedChatStartedIncoming";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        goto L_0x0678;
    L_0x070f:
        r8 = r4;
        goto L_0x0679;
    L_0x0712:
        r0 = r0.isFromUser();
        if (r0 == 0) goto L_0x072f;
    L_0x0718:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r10 = r1.message;
        r10 = r10.messageOwner;
        r10 = r10.from_id;
        r10 = java.lang.Integer.valueOf(r10);
        r0 = r0.getUser(r10);
        r10 = r0;
        r0 = 0;
        goto L_0x0746;
    L_0x072f:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r10 = r1.message;
        r10 = r10.messageOwner;
        r10 = r10.to_id;
        r10 = r10.channel_id;
        r10 = java.lang.Integer.valueOf(r10);
        r0 = r0.getChat(r10);
        r10 = 0;
    L_0x0746:
        r11 = r1.dialogsType;
        r14 = 3;
        if (r11 != r14) goto L_0x0764;
    L_0x074b:
        r11 = r1.user;
        r11 = org.telegram.messenger.UserObject.isUserSelf(r11);
        if (r11 == 0) goto L_0x0764;
    L_0x0753:
        r0 = NUM; // 0x7f0d0887 float:1.8746542E38 double:1.053130856E-314;
        r6 = "SavedMessagesInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r8 = r0;
        r10 = r5;
        r0 = 0;
        r5 = 1;
        r6 = 0;
    L_0x0761:
        r9 = 0;
        goto L_0x0a4f;
    L_0x0764:
        r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r11 != 0) goto L_0x0775;
    L_0x0768:
        r11 = r1.currentDialogFolderId;
        if (r11 == 0) goto L_0x0775;
    L_0x076c:
        r0 = r30.formatArchivedDialogNames();
        r8 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 0;
        goto L_0x0761;
    L_0x0775:
        r11 = r1.message;
        r14 = r11.messageOwner;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r14 == 0) goto L_0x07a1;
    L_0x077d:
        r0 = r1.chat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x0796;
    L_0x0785:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r5 != 0) goto L_0x0793;
    L_0x078f:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r0 == 0) goto L_0x0796;
    L_0x0793:
        r0 = r4;
        r6 = 0;
        goto L_0x079a;
    L_0x0796:
        r0 = r1.message;
        r0 = r0.messageText;
    L_0x079a:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
    L_0x079c:
        r8 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 1;
        goto L_0x0761;
    L_0x07a1:
        r14 = r1.chat;
        if (r14 == 0) goto L_0x096e;
    L_0x07a5:
        r14 = r14.id;
        if (r14 <= 0) goto L_0x096e;
    L_0x07a9:
        if (r0 != 0) goto L_0x096e;
    L_0x07ab:
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x07bb;
    L_0x07b1:
        r0 = NUM; // 0x7f0d0473 float:1.8744425E38 double:1.0531303403E-314;
        r10 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r10, r0);
        goto L_0x07f8;
    L_0x07bb:
        if (r10 == 0) goto L_0x07eb;
    L_0x07bd:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x07e0;
    L_0x07c1:
        r0 = org.telegram.messenger.UserObject.isDeleted(r10);
        if (r0 == 0) goto L_0x07d1;
    L_0x07c7:
        r0 = NUM; // 0x7f0d04a7 float:1.874453E38 double:1.053130366E-314;
        r10 = "HiddenName";
        r0 = org.telegram.messenger.LocaleController.getString(r10, r0);
        goto L_0x07f8;
    L_0x07d1:
        r0 = r10.first_name;
        r10 = r10.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r10);
        r10 = "\n";
        r0 = r0.replace(r10, r4);
        goto L_0x07f8;
    L_0x07e0:
        r0 = org.telegram.messenger.UserObject.getFirstName(r10);
        r10 = "\n";
        r0 = r0.replace(r10, r4);
        goto L_0x07f8;
    L_0x07eb:
        if (r0 == 0) goto L_0x07f6;
    L_0x07ed:
        r0 = r0.title;
        r10 = "\n";
        r0 = r0.replace(r10, r4);
        goto L_0x07f8;
    L_0x07f6:
        r0 = "DELETED";
    L_0x07f8:
        r10 = r1.message;
        r11 = r10.caption;
        if (r11 == 0) goto L_0x0864;
    L_0x07fe:
        r9 = r11.toString();
        r10 = r9.length();
        if (r10 <= r13) goto L_0x080c;
    L_0x0808:
        r9 = r9.substring(r3, r13);
    L_0x080c:
        r10 = r1.message;
        r10 = r10.isVideo();
        if (r10 == 0) goto L_0x0818;
    L_0x0814:
        r10 = "📹 ";
        goto L_0x083f;
    L_0x0818:
        r10 = r1.message;
        r10 = r10.isVoice();
        if (r10 == 0) goto L_0x0824;
    L_0x0820:
        r10 = "🎤 ";
        goto L_0x083f;
    L_0x0824:
        r10 = r1.message;
        r10 = r10.isMusic();
        if (r10 == 0) goto L_0x0830;
    L_0x082c:
        r10 = "🎧 ";
        goto L_0x083f;
    L_0x0830:
        r10 = r1.message;
        r10 = r10.isPhoto();
        if (r10 == 0) goto L_0x083c;
    L_0x0838:
        r10 = "🖼 ";
        goto L_0x083f;
    L_0x083c:
        r10 = "📎 ";
    L_0x083f:
        r11 = new java.lang.Object[r2];
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r10);
        r10 = 32;
        r9 = r9.replace(r12, r10);
        r14.append(r9);
        r9 = r14.toString();
        r11[r3] = r9;
        r11[r7] = r0;
        r8 = java.lang.String.format(r8, r11);
        r8 = android.text.SpannableStringBuilder.valueOf(r8);
        goto L_0x0937;
    L_0x0864:
        r11 = r10.messageOwner;
        r11 = r11.media;
        if (r11 == 0) goto L_0x090c;
    L_0x086a:
        r10 = r10.isMediaEmpty();
        if (r10 != 0) goto L_0x090c;
    L_0x0870:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r10 = r1.message;
        r11 = r10.messageOwner;
        r11 = r11.media;
        r14 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r14 == 0) goto L_0x08a2;
    L_0x087c:
        r10 = android.os.Build.VERSION.SDK_INT;
        r14 = 18;
        if (r10 < r14) goto L_0x0892;
    L_0x0882:
        r10 = new java.lang.Object[r7];
        r11 = r11.game;
        r11 = r11.title;
        r10[r3] = r11;
        r11 = "🎮 ⁨%s⁩";
        r10 = java.lang.String.format(r11, r10);
        goto L_0x08e0;
    L_0x0892:
        r10 = new java.lang.Object[r7];
        r11 = r11.game;
        r11 = r11.title;
        r10[r3] = r11;
        r11 = "🎮 %s";
        r10 = java.lang.String.format(r11, r10);
        goto L_0x08e0;
    L_0x08a2:
        r11 = r10.type;
        r14 = 14;
        if (r11 != r14) goto L_0x08de;
    L_0x08a8:
        r11 = android.os.Build.VERSION.SDK_INT;
        r14 = 18;
        if (r11 < r14) goto L_0x08c6;
    L_0x08ae:
        r11 = new java.lang.Object[r2];
        r10 = r10.getMusicAuthor();
        r11[r3] = r10;
        r10 = r1.message;
        r10 = r10.getMusicTitle();
        r11[r7] = r10;
        r10 = "🎧 ⁨%s - %s⁩";
        r10 = java.lang.String.format(r10, r11);
        goto L_0x08e0;
    L_0x08c6:
        r11 = new java.lang.Object[r2];
        r10 = r10.getMusicAuthor();
        r11[r3] = r10;
        r10 = r1.message;
        r10 = r10.getMusicTitle();
        r11[r7] = r10;
        r10 = "🎧 %s - %s";
        r10 = java.lang.String.format(r10, r11);
        goto L_0x08e0;
    L_0x08de:
        r10 = r10.messageText;
    L_0x08e0:
        r11 = new java.lang.Object[r2];
        r11[r3] = r10;
        r11[r7] = r0;
        r8 = java.lang.String.format(r8, r11);
        r8 = android.text.SpannableStringBuilder.valueOf(r8);
        r10 = new android.text.style.ForegroundColorSpan;
        r11 = "chats_attachMessage";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r10.<init>(r11);
        if (r9 == 0) goto L_0x0901;
    L_0x08fb:
        r9 = r0.length();
        r9 = r9 + r2;
        goto L_0x0902;
    L_0x0901:
        r9 = 0;
    L_0x0902:
        r11 = r8.length();
        r14 = 33;
        r8.setSpan(r10, r9, r11, r14);
        goto L_0x0937;
    L_0x090c:
        r9 = r1.message;
        r9 = r9.messageOwner;
        r9 = r9.message;
        if (r9 == 0) goto L_0x0933;
    L_0x0914:
        r10 = r9.length();
        if (r10 <= r13) goto L_0x091e;
    L_0x091a:
        r9 = r9.substring(r3, r13);
    L_0x091e:
        r10 = new java.lang.Object[r2];
        r11 = 32;
        r9 = r9.replace(r12, r11);
        r10[r3] = r9;
        r10[r7] = r0;
        r8 = java.lang.String.format(r8, r10);
        r8 = android.text.SpannableStringBuilder.valueOf(r8);
        goto L_0x0937;
    L_0x0933:
        r8 = android.text.SpannableStringBuilder.valueOf(r4);
    L_0x0937:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x0945;
    L_0x093b:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x095a;
    L_0x093f:
        r9 = r8.length();
        if (r9 <= 0) goto L_0x095a;
    L_0x0945:
        r9 = new android.text.style.ForegroundColorSpan;
        r10 = "chats_nameMessage";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r9.<init>(r10);
        r10 = r0.length();
        r10 = r10 + r7;
        r11 = 33;
        r8.setSpan(r9, r3, r10, r11);
    L_0x095a:
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r9 = r9.getFontMetricsInt();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r10, r3);
        r9 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 0;
        goto L_0x0a4f;
    L_0x096e:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r8 == 0) goto L_0x098d;
    L_0x0978:
        r8 = r0.photo;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r8 == 0) goto L_0x098d;
    L_0x097e:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x098d;
    L_0x0982:
        r0 = NUM; // 0x7f0d0143 float:1.874277E38 double:1.053129937E-314;
        r8 = "AttachPhotoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        goto L_0x079c;
    L_0x098d:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r8 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r8 == 0) goto L_0x09ac;
    L_0x0997:
        r8 = r0.document;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;
        if (r8 == 0) goto L_0x09ac;
    L_0x099d:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x09ac;
    L_0x09a1:
        r0 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r8 = "AttachVideoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        goto L_0x079c;
    L_0x09ac:
        r0 = r1.message;
        r8 = r0.caption;
        if (r8 == 0) goto L_0x09f8;
    L_0x09b2:
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x09bc;
    L_0x09b8:
        r0 = "📹 ";
        goto L_0x09e3;
    L_0x09bc:
        r0 = r1.message;
        r0 = r0.isVoice();
        if (r0 == 0) goto L_0x09c8;
    L_0x09c4:
        r0 = "🎤 ";
        goto L_0x09e3;
    L_0x09c8:
        r0 = r1.message;
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x09d4;
    L_0x09d0:
        r0 = "🎧 ";
        goto L_0x09e3;
    L_0x09d4:
        r0 = r1.message;
        r0 = r0.isPhoto();
        if (r0 == 0) goto L_0x09e0;
    L_0x09dc:
        r0 = "🖼 ";
        goto L_0x09e3;
    L_0x09e0:
        r0 = "📎 ";
    L_0x09e3:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r0);
        r0 = r1.message;
        r0 = r0.caption;
        r8.append(r0);
        r0 = r8.toString();
        goto L_0x079c;
    L_0x09f8:
        r8 = r0.messageOwner;
        r8 = r8.media;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r8 == 0) goto L_0x0a1d;
    L_0x0a00:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r8 = "🎮 ";
        r0.append(r8);
        r8 = r1.message;
        r8 = r8.messageOwner;
        r8 = r8.media;
        r8 = r8.game;
        r8 = r8.title;
        r0.append(r8);
        r0 = r0.toString();
        goto L_0x0a3d;
    L_0x0a1d:
        r8 = r0.type;
        r9 = 14;
        if (r8 != r9) goto L_0x0a3b;
    L_0x0a23:
        r8 = new java.lang.Object[r2];
        r0 = r0.getMusicAuthor();
        r8[r3] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r8[r7] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r8);
        goto L_0x0a3d;
    L_0x0a3b:
        r0 = r0.messageText;
    L_0x0a3d:
        r8 = r1.message;
        r9 = r8.messageOwner;
        r9 = r9.media;
        if (r9 == 0) goto L_0x079c;
    L_0x0a45:
        r8 = r8.isMediaEmpty();
        if (r8 != 0) goto L_0x079c;
    L_0x0a4b:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        goto L_0x079c;
    L_0x0a4f:
        r11 = r1.currentDialogFolderId;
        if (r11 == 0) goto L_0x0a57;
    L_0x0a53:
        r9 = r30.formatArchivedDialogNames();
    L_0x0a57:
        r11 = r1.draftMessage;
        if (r11 == 0) goto L_0x0a63;
    L_0x0a5b:
        r11 = r11.date;
        r14 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r14);
        goto L_0x0a7c;
    L_0x0a63:
        r11 = r1.lastMessageDate;
        if (r11 == 0) goto L_0x0a6d;
    L_0x0a67:
        r14 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r14);
        goto L_0x0a7c;
    L_0x0a6d:
        r11 = r1.message;
        if (r11 == 0) goto L_0x0a7b;
    L_0x0a71:
        r11 = r11.messageOwner;
        r11 = r11.date;
        r14 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r14);
        goto L_0x0a7c;
    L_0x0a7b:
        r11 = r4;
    L_0x0a7c:
        r14 = r1.message;
        if (r14 != 0) goto L_0x0a90;
    L_0x0a80:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawCount = r3;
        r1.drawMention = r3;
        r1.drawError = r3;
        r2 = 0;
        r14 = 0;
        goto L_0x0b83;
    L_0x0a90:
        r15 = r1.currentDialogFolderId;
        if (r15 == 0) goto L_0x0acf;
    L_0x0a94:
        r14 = r1.unreadCount;
        r15 = r1.mentionCount;
        r18 = r14 + r15;
        if (r18 <= 0) goto L_0x0ac8;
    L_0x0a9c:
        if (r14 <= r15) goto L_0x0ab2;
    L_0x0a9e:
        r1.drawCount = r7;
        r1.drawMention = r3;
        r2 = new java.lang.Object[r7];
        r14 = r14 + r15;
        r14 = java.lang.Integer.valueOf(r14);
        r2[r3] = r14;
        r14 = "%d";
        r2 = java.lang.String.format(r14, r2);
        goto L_0x0acd;
    L_0x0ab2:
        r1.drawCount = r3;
        r1.drawMention = r7;
        r2 = new java.lang.Object[r7];
        r14 = r14 + r15;
        r14 = java.lang.Integer.valueOf(r14);
        r2[r3] = r14;
        r14 = "%d";
        r2 = java.lang.String.format(r14, r2);
        r14 = r2;
        r2 = 0;
        goto L_0x0b14;
    L_0x0ac8:
        r1.drawCount = r3;
        r1.drawMention = r3;
        r2 = 0;
    L_0x0acd:
        r14 = 0;
        goto L_0x0b14;
    L_0x0acf:
        r2 = r1.clearingDialog;
        if (r2 == 0) goto L_0x0ad8;
    L_0x0ad3:
        r1.drawCount = r3;
        r2 = 0;
        r6 = 0;
        goto L_0x0b08;
    L_0x0ad8:
        r2 = r1.unreadCount;
        if (r2 == 0) goto L_0x0afd;
    L_0x0adc:
        if (r2 != r7) goto L_0x0aea;
    L_0x0ade:
        r15 = r1.mentionCount;
        if (r2 != r15) goto L_0x0aea;
    L_0x0ae2:
        if (r14 == 0) goto L_0x0aea;
    L_0x0ae4:
        r2 = r14.messageOwner;
        r2 = r2.mentioned;
        if (r2 != 0) goto L_0x0afd;
    L_0x0aea:
        r1.drawCount = r7;
        r2 = new java.lang.Object[r7];
        r14 = r1.unreadCount;
        r14 = java.lang.Integer.valueOf(r14);
        r2[r3] = r14;
        r14 = "%d";
        r2 = java.lang.String.format(r14, r2);
        goto L_0x0b08;
    L_0x0afd:
        r2 = r1.markUnread;
        if (r2 == 0) goto L_0x0b05;
    L_0x0b01:
        r1.drawCount = r7;
        r2 = r4;
        goto L_0x0b08;
    L_0x0b05:
        r1.drawCount = r3;
        r2 = 0;
    L_0x0b08:
        r14 = r1.mentionCount;
        if (r14 == 0) goto L_0x0b11;
    L_0x0b0c:
        r1.drawMention = r7;
        r14 = "@";
        goto L_0x0b14;
    L_0x0b11:
        r1.drawMention = r3;
        goto L_0x0acd;
    L_0x0b14:
        r15 = r1.message;
        r15 = r15.isOut();
        if (r15 == 0) goto L_0x0b7b;
    L_0x0b1c:
        r15 = r1.draftMessage;
        if (r15 != 0) goto L_0x0b7b;
    L_0x0b20:
        if (r6 == 0) goto L_0x0b7b;
    L_0x0b22:
        r6 = r1.message;
        r15 = r6.messageOwner;
        r15 = r15.action;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r15 != 0) goto L_0x0b7b;
    L_0x0b2c:
        r6 = r6.isSending();
        if (r6 == 0) goto L_0x0b3b;
    L_0x0b32:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r7;
        r1.drawError = r3;
        goto L_0x0b83;
    L_0x0b3b:
        r6 = r1.message;
        r6 = r6.isSendError();
        if (r6 == 0) goto L_0x0b50;
    L_0x0b43:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r7;
        r1.drawCount = r3;
        r1.drawMention = r3;
        goto L_0x0b83;
    L_0x0b50:
        r6 = r1.message;
        r6 = r6.isSent();
        if (r6 == 0) goto L_0x0b83;
    L_0x0b58:
        r6 = r1.message;
        r6 = r6.isUnread();
        if (r6 == 0) goto L_0x0b71;
    L_0x0b60:
        r6 = r1.chat;
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x0b6f;
    L_0x0b68:
        r6 = r1.chat;
        r6 = r6.megagroup;
        if (r6 != 0) goto L_0x0b6f;
    L_0x0b6e:
        goto L_0x0b71;
    L_0x0b6f:
        r6 = 0;
        goto L_0x0b72;
    L_0x0b71:
        r6 = 1;
    L_0x0b72:
        r1.drawCheck1 = r6;
        r1.drawCheck2 = r7;
        r1.drawClock = r3;
        r1.drawError = r3;
        goto L_0x0b83;
    L_0x0b7b:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r3;
    L_0x0b83:
        r6 = r1.dialogsType;
        if (r6 != 0) goto L_0x0ba3;
    L_0x0b87:
        r6 = r1.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r19 = r14;
        r13 = r1.currentDialogId;
        r6 = r6.isProxyDialog(r13);
        if (r6 == 0) goto L_0x0ba5;
    L_0x0b97:
        r1.drawPinBackground = r7;
        r6 = NUM; // 0x7f0d09d9 float:1.8747228E38 double:1.053131023E-314;
        r11 = "UseProxySponsor";
        r6 = org.telegram.messenger.LocaleController.getString(r11, r6);
        goto L_0x0ba6;
    L_0x0ba3:
        r19 = r14;
    L_0x0ba5:
        r6 = r11;
    L_0x0ba6:
        r11 = r1.currentDialogFolderId;
        if (r11 == 0) goto L_0x0bbb;
    L_0x0baa:
        r11 = NUM; // 0x7f0d00f9 float:1.874262E38 double:1.0531299006E-314;
        r13 = "ArchivedChats";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
    L_0x0bb3:
        r13 = r10;
        r10 = r9;
        r9 = r8;
        r8 = r5;
        r5 = r19;
        goto L_0x0c7a;
    L_0x0bbb:
        r11 = r1.chat;
        if (r11 == 0) goto L_0x0bc3;
    L_0x0bbf:
        r11 = r11.title;
        goto L_0x0CLASSNAME;
    L_0x0bc3:
        r11 = r1.user;
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0bc7:
        r11 = org.telegram.messenger.UserObject.isUserSelf(r11);
        if (r11 == 0) goto L_0x0bdf;
    L_0x0bcd:
        r11 = r1.dialogsType;
        r13 = 3;
        if (r11 != r13) goto L_0x0bd4;
    L_0x0bd2:
        r1.drawPinBackground = r7;
    L_0x0bd4:
        r11 = NUM; // 0x7f0d0886 float:1.874654E38 double:1.0531308556E-314;
        r13 = "SavedMessages";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
        goto L_0x0CLASSNAME;
    L_0x0bdf:
        r11 = r1.user;
        r11 = r11.id;
        r13 = r11 / 1000;
        r14 = 777; // 0x309 float:1.089E-42 double:3.84E-321;
        if (r13 == r14) goto L_0x0CLASSNAME;
    L_0x0be9:
        r11 = r11 / 1000;
        r13 = 333; // 0x14d float:4.67E-43 double:1.645E-321;
        if (r11 == r13) goto L_0x0CLASSNAME;
    L_0x0bef:
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r13 = r1.user;
        r13 = r13.id;
        r13 = java.lang.Integer.valueOf(r13);
        r11 = r11.get(r13);
        if (r11 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r11 = r11.size();
        if (r11 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsLoaded;
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0c1d:
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.isLoadingContacts();
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.user;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.user;
        r11 = r11.phone;
        if (r11 == 0) goto L_0x0c5a;
    L_0x0CLASSNAME:
        r11 = r11.length();
        if (r11 == 0) goto L_0x0c5a;
    L_0x0c3c:
        r11 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "+";
        r13.append(r14);
        r14 = r1.user;
        r14 = r14.phone;
        r13.append(r14);
        r13 = r13.toString();
        r11 = r11.format(r13);
        goto L_0x0CLASSNAME;
    L_0x0c5a:
        r11 = r1.user;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.user;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r4;
    L_0x0CLASSNAME:
        r13 = r11.length();
        if (r13 != 0) goto L_0x0bb3;
    L_0x0c6f:
        r11 = NUM; // 0x7f0d04a7 float:1.874453E38 double:1.053130366E-314;
        r13 = "HiddenName";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
        goto L_0x0bb3;
    L_0x0c7a:
        if (r0 == 0) goto L_0x0cbd;
    L_0x0c7c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r0 = r0.measureText(r6);
        r27 = r8;
        r7 = (double) r0;
        r7 = java.lang.Math.ceil(r7);
        r0 = (int) r7;
        r7 = new android.text.StaticLayout;
        r21 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r7;
        r20 = r6;
        r22 = r0;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r1.timeLayout = r7;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x0cb4;
    L_0x0ca5:
        r6 = r30.getMeasuredWidth();
        r7 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r6 = r6 - r0;
        r1.timeLeft = r6;
        goto L_0x0cc5;
    L_0x0cb4:
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.timeLeft = r6;
        goto L_0x0cc5;
    L_0x0cbd:
        r27 = r8;
        r6 = 0;
        r1.timeLayout = r6;
        r1.timeLeft = r3;
        r0 = 0;
    L_0x0cc5:
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x0cd9;
    L_0x0cc9:
        r6 = r30.getMeasuredWidth();
        r7 = r1.nameLeft;
        r6 = r6 - r7;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r6 = r6 - r0;
        goto L_0x0ced;
    L_0x0cd9:
        r6 = r30.getMeasuredWidth();
        r7 = r1.nameLeft;
        r6 = r6 - r7;
        r7 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r6 = r6 - r0;
        r7 = r1.nameLeft;
        r7 = r7 + r0;
        r1.nameLeft = r7;
    L_0x0ced:
        r7 = r1.drawNameLock;
        if (r7 == 0) goto L_0x0d00;
    L_0x0cf1:
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r8 = r8.getIntrinsicWidth();
    L_0x0cfd:
        r7 = r7 + r8;
        r6 = r6 - r7;
        goto L_0x0d33;
    L_0x0d00:
        r7 = r1.drawNameGroup;
        if (r7 == 0) goto L_0x0d11;
    L_0x0d04:
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r8 = r8.getIntrinsicWidth();
        goto L_0x0cfd;
    L_0x0d11:
        r7 = r1.drawNameBroadcast;
        if (r7 == 0) goto L_0x0d22;
    L_0x0d15:
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r8 = r8.getIntrinsicWidth();
        goto L_0x0cfd;
    L_0x0d22:
        r7 = r1.drawNameBot;
        if (r7 == 0) goto L_0x0d33;
    L_0x0d26:
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r8 = r8.getIntrinsicWidth();
        goto L_0x0cfd;
    L_0x0d33:
        r7 = r1.drawClock;
        if (r7 == 0) goto L_0x0d63;
    L_0x0d37:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r7 = r7.getIntrinsicWidth();
        r8 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 + r8;
        r6 = r6 - r7;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0d50;
    L_0x0d49:
        r0 = r1.timeLeft;
        r0 = r0 - r7;
        r1.checkDrawLeft = r0;
        goto L_0x0de3;
    L_0x0d50:
        r8 = r1.timeLeft;
        r8 = r8 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r8 = r8 + r0;
        r1.checkDrawLeft = r8;
        r0 = r1.nameLeft;
        r0 = r0 + r7;
        r1.nameLeft = r0;
        goto L_0x0de3;
    L_0x0d63:
        r7 = r1.drawCheck2;
        if (r7 == 0) goto L_0x0de3;
    L_0x0d67:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r7 = r7.getIntrinsicWidth();
        r8 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 + r8;
        r6 = r6 - r7;
        r8 = r1.drawCheck1;
        if (r8 == 0) goto L_0x0dc8;
    L_0x0d79:
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r8 = r8.getIntrinsicWidth();
        r19 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r19 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r8 = r8 - r19;
        r6 = r6 - r8;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0d9d;
    L_0x0d8c:
        r0 = r1.timeLeft;
        r0 = r0 - r7;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.halfCheckDrawLeft;
        r7 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0 = r0 - r7;
        r1.checkDrawLeft = r0;
        goto L_0x0de3;
    L_0x0d9d:
        r8 = r1.timeLeft;
        r8 = r8 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r8 = r8 + r0;
        r1.checkDrawLeft = r8;
        r0 = r1.checkDrawLeft;
        r8 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r8;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.nameLeft;
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r8 = r8.getIntrinsicWidth();
        r7 = r7 + r8;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 - r8;
        r0 = r0 + r7;
        r1.nameLeft = r0;
        goto L_0x0de3;
    L_0x0dc8:
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0dd2;
    L_0x0dcc:
        r0 = r1.timeLeft;
        r0 = r0 - r7;
        r1.checkDrawLeft = r0;
        goto L_0x0de3;
    L_0x0dd2:
        r8 = r1.timeLeft;
        r8 = r8 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r8 = r8 + r0;
        r1.checkDrawLeft = r8;
        r0 = r1.nameLeft;
        r0 = r0 + r7;
        r1.nameLeft = r0;
    L_0x0de3:
        r0 = r1.dialogMuted;
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r0 == 0) goto L_0x0e07;
    L_0x0de9:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x0e07;
    L_0x0ded:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x0e07;
    L_0x0df1:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r8 = r8.getIntrinsicWidth();
        r0 = r0 + r8;
        r6 = r6 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x0e3a;
    L_0x0e01:
        r8 = r1.nameLeft;
        r8 = r8 + r0;
        r1.nameLeft = r8;
        goto L_0x0e3a;
    L_0x0e07:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0e21;
    L_0x0e0b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r8 = r8.getIntrinsicWidth();
        r0 = r0 + r8;
        r6 = r6 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x0e3a;
    L_0x0e1b:
        r8 = r1.nameLeft;
        r8 = r8 + r0;
        r1.nameLeft = r8;
        goto L_0x0e3a;
    L_0x0e21:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0e3a;
    L_0x0e25:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r8 = r8.getIntrinsicWidth();
        r0 = r0 + r8;
        r6 = r6 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x0e3a;
    L_0x0e35:
        r8 = r1.nameLeft;
        r8 = r8 + r0;
        r1.nameLeft = r8;
    L_0x0e3a:
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = java.lang.Math.max(r0, r6);
        r14 = 32;
        r0 = r11.replace(r12, r14);	 Catch:{ Exception -> 0x0e6f }
        r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e6f }
        r14 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0e6f }
        r14 = r6 - r14;
        r14 = (float) r14;	 Catch:{ Exception -> 0x0e6f }
        r15 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0e6f }
        r20 = android.text.TextUtils.ellipsize(r0, r11, r14, r15);	 Catch:{ Exception -> 0x0e6f }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0e6f }
        r21 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0e6f }
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0e6f }
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r0;
        r22 = r6;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x0e6f }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x0e6f }
        goto L_0x0e73;
    L_0x0e6f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0e73:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0ef3;
    L_0x0e77:
        r0 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r11 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.messageNameTop = r11;
        r11 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.timeTop = r11;
        r11 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.errorTop = r11;
        r11 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.pinTop = r11;
        r11 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.countTop = r11;
        r11 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1.checkDrawTop = r11;
        r11 = r30.getMeasuredWidth();
        r14 = NUM; // 0x42ba0000 float:93.0 double:5.5309955E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r11 = r11 - r14;
        r14 = org.telegram.messenger.LocaleController.isRTL;
        if (r14 != 0) goto L_0x0ecd;
    L_0x0ebc:
        r14 = NUM; // 0x429CLASSNAME float:78.0 double:5.521281773E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.messageNameLeft = r14;
        r1.messageLeft = r14;
        r14 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        goto L_0x0ee2;
    L_0x0ecd:
        r14 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1.messageNameLeft = r14;
        r1.messageLeft = r14;
        r14 = r30.getMeasuredWidth();
        r15 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r14 = r14 - r15;
    L_0x0ee2:
        r15 = r1.avatarImage;
        r16 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r12 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r15.setImageCoords(r14, r0, r7, r12);
        goto L_0x0f6e;
    L_0x0ef3:
        r0 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.messageNameTop = r7;
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.timeTop = r7;
        r7 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.errorTop = r7;
        r7 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.pinTop = r7;
        r7 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.countTop = r7;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.checkDrawTop = r7;
        r7 = r30.getMeasuredWidth();
        r11 = NUM; // 0x42be0000 float:95.0 double:5.53229066E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = r7 - r11;
        r7 = org.telegram.messenger.LocaleController.isRTL;
        if (r7 != 0) goto L_0x0var_;
    L_0x0var_:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.messageNameLeft = r7;
        r1.messageLeft = r7;
        r7 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        goto L_0x0f5d;
    L_0x0var_:
        r7 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.messageNameLeft = r7;
        r1.messageLeft = r7;
        r7 = r30.getMeasuredWidth();
        r12 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r7 = r7 - r12;
    L_0x0f5d:
        r12 = r1.avatarImage;
        r14 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r15 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r12.setImageCoords(r7, r0, r14, r15);
    L_0x0f6e:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0f8b;
    L_0x0var_:
        r0 = r30.getMeasuredWidth();
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r7 = r7.getIntrinsicWidth();
        r0 = r0 - r7;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0 = r0 - r7;
        r1.pinLeft = r0;
        goto L_0x0var_;
    L_0x0f8b:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.pinLeft = r0;
    L_0x0var_:
        r0 = r1.drawError;
        if (r0 == 0) goto L_0x0fc5;
    L_0x0var_:
        r0 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r11 = r11 - r0;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0fb1;
    L_0x0fa2:
        r0 = r30.getMeasuredWidth();
        r2 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r1.errorLeft = r0;
        goto L_0x10dc;
    L_0x0fb1:
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.errorLeft = r2;
        r2 = r1.messageLeft;
        r2 = r2 + r0;
        r1.messageLeft = r2;
        r2 = r1.messageNameLeft;
        r2 = r2 + r0;
        r1.messageNameLeft = r2;
        goto L_0x10dc;
    L_0x0fc5:
        if (r2 != 0) goto L_0x0ff0;
    L_0x0fc7:
        if (r5 == 0) goto L_0x0fca;
    L_0x0fc9:
        goto L_0x0ff0;
    L_0x0fca:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0fea;
    L_0x0fce:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0 = r0.getIntrinsicWidth();
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r11 = r11 - r0;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0fea;
    L_0x0fe0:
        r2 = r1.messageLeft;
        r2 = r2 + r0;
        r1.messageLeft = r2;
        r2 = r1.messageNameLeft;
        r2 = r2 + r0;
        r1.messageNameLeft = r2;
    L_0x0fea:
        r1.drawCount = r3;
        r1.drawMention = r3;
        goto L_0x10dc;
    L_0x0ff0:
        if (r2 == 0) goto L_0x1052;
    L_0x0ff2:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r7.measureText(r2);
        r14 = (double) r7;
        r14 = java.lang.Math.ceil(r14);
        r7 = (int) r14;
        r0 = java.lang.Math.max(r0, r7);
        r1.countWidth = r0;
        r0 = new android.text.StaticLayout;
        r21 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r1.countWidth;
        r23 = android.text.Layout.Alignment.ALIGN_CENTER;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r0;
        r20 = r2;
        r22 = r7;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r1.countLayout = r0;
        r0 = r1.countWidth;
        r2 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r7;
        r11 = r11 - r0;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x103e;
    L_0x102f:
        r0 = r30.getMeasuredWidth();
        r2 = r1.countWidth;
        r0 = r0 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r2;
        r1.countLeft = r0;
        goto L_0x104e;
    L_0x103e:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.countLeft = r2;
        r2 = r1.messageLeft;
        r2 = r2 + r0;
        r1.messageLeft = r2;
        r2 = r1.messageNameLeft;
        r2 = r2 + r0;
        r1.messageNameLeft = r2;
    L_0x104e:
        r2 = 1;
        r1.drawCount = r2;
        goto L_0x1054;
    L_0x1052:
        r1.countWidth = r3;
    L_0x1054:
        if (r5 == 0) goto L_0x10dc;
    L_0x1056:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x108a;
    L_0x105a:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r2 = r2.measureText(r5);
        r14 = (double) r2;
        r14 = java.lang.Math.ceil(r14);
        r2 = (int) r14;
        r0 = java.lang.Math.max(r0, r2);
        r1.mentionWidth = r0;
        r0 = new android.text.StaticLayout;
        r21 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r2 = r1.mentionWidth;
        r23 = android.text.Layout.Alignment.ALIGN_CENTER;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r0;
        r20 = r5;
        r22 = r2;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r1.mentionLayout = r0;
        goto L_0x1090;
    L_0x108a:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.mentionWidth = r0;
    L_0x1090:
        r0 = r1.mentionWidth;
        r2 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r5;
        r11 = r11 - r0;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x10bb;
    L_0x109e:
        r0 = r30.getMeasuredWidth();
        r2 = r1.mentionWidth;
        r0 = r0 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r2;
        r2 = r1.countWidth;
        if (r2 == 0) goto L_0x10b6;
    L_0x10ae:
        r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        goto L_0x10b7;
    L_0x10b6:
        r2 = 0;
    L_0x10b7:
        r0 = r0 - r2;
        r1.mentionLeft = r0;
        goto L_0x10d9;
    L_0x10bb:
        r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r7 = r1.countWidth;
        if (r7 == 0) goto L_0x10cb;
    L_0x10c5:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r7;
        goto L_0x10cc;
    L_0x10cb:
        r5 = 0;
    L_0x10cc:
        r2 = r2 + r5;
        r1.mentionLeft = r2;
        r2 = r1.messageLeft;
        r2 = r2 + r0;
        r1.messageLeft = r2;
        r2 = r1.messageNameLeft;
        r2 = r2 + r0;
        r1.messageNameLeft = r2;
    L_0x10d9:
        r2 = 1;
        r1.drawMention = r2;
    L_0x10dc:
        if (r27 == 0) goto L_0x1110;
    L_0x10de:
        if (r9 != 0) goto L_0x10e1;
    L_0x10e0:
        goto L_0x10e2;
    L_0x10e1:
        r4 = r9;
    L_0x10e2:
        r0 = r4.toString();
        r2 = r0.length();
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r2 <= r4) goto L_0x10f2;
    L_0x10ee:
        r0 = r0.substring(r3, r4);
    L_0x10f2:
        r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r2 == 0) goto L_0x10f8;
    L_0x10f6:
        if (r10 == 0) goto L_0x1100;
    L_0x10f8:
        r2 = 32;
        r4 = 10;
        r0 = r0.replace(r4, r2);
    L_0x1100:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r2 = r2.getFontMetricsInt();
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r3);
    L_0x1110:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2 = java.lang.Math.max(r0, r11);
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x1150;
    L_0x111c:
        if (r10 == 0) goto L_0x1150;
    L_0x111e:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x1127;
    L_0x1122:
        r0 = r1.currentDialogFolderDialogsCount;
        r4 = 1;
        if (r0 != r4) goto L_0x1150;
    L_0x1127:
        r20 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;	 Catch:{ Exception -> 0x1142 }
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x1142 }
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r24 = 0;
        r25 = 0;
        r26 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x1142 }
        r28 = 1;
        r19 = r10;
        r21 = r2;
        r27 = r2;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x1142 }
        r1.messageNameLayout = r0;	 Catch:{ Exception -> 0x1142 }
        goto L_0x1146;
    L_0x1142:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x1146:
        r0 = NUM; // 0x424CLASSNAME float:51.0 double:5.495378504E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        r4 = 0;
        goto L_0x1168;
    L_0x1150:
        r4 = 0;
        r1.messageNameLayout = r4;
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x1160;
    L_0x1157:
        r0 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        goto L_0x1168;
    L_0x1160:
        r0 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
    L_0x1168:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11d4 }
        if (r0 == 0) goto L_0x1179;
    L_0x116c:
        r0 = r1.currentDialogFolderId;	 Catch:{ Exception -> 0x11d4 }
        if (r0 == 0) goto L_0x1179;
    L_0x1170:
        r0 = r1.currentDialogFolderDialogsCount;	 Catch:{ Exception -> 0x11d4 }
        r5 = 1;
        if (r0 <= r5) goto L_0x117a;
    L_0x1175:
        r20 = r10;
        r10 = r4;
        goto L_0x1193;
    L_0x1179:
        r5 = 1;
    L_0x117a:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11d4 }
        if (r0 == 0) goto L_0x1184;
    L_0x117e:
        if (r10 == 0) goto L_0x1181;
    L_0x1180:
        goto L_0x1184;
    L_0x1181:
        r20 = r9;
        goto L_0x1193;
    L_0x1184:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x11d4 }
        r0 = r2 - r0;
        r0 = (float) r0;	 Catch:{ Exception -> 0x11d4 }
        r4 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x11d4 }
        r0 = android.text.TextUtils.ellipsize(r9, r13, r0, r4);	 Catch:{ Exception -> 0x11d4 }
        r20 = r0;
    L_0x1193:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x11d4 }
        if (r0 == 0) goto L_0x11be;
    L_0x1197:
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x11d4 }
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x11d4 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x11d4 }
        r25 = 0;
        r26 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x11d4 }
        if (r10 == 0) goto L_0x11ab;
    L_0x11a8:
        r28 = 1;
        goto L_0x11ad;
    L_0x11ab:
        r28 = 2;
    L_0x11ad:
        r19 = r20;
        r20 = r13;
        r21 = r2;
        r24 = r0;
        r27 = r2;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x11d4 }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x11d4 }
        goto L_0x11d8;
    L_0x11be:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x11d4 }
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x11d4 }
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r0;
        r21 = r13;
        r22 = r2;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x11d4 }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x11d4 }
        goto L_0x11d8;
    L_0x11d4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x11d8:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x130a;
    L_0x11dc:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x1294;
    L_0x11e0:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1294;
    L_0x11e6:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r3);
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r3);
        r4 = (double) r4;
        r4 = java.lang.Math.ceil(r4);
        r7 = r1.dialogMuted;
        if (r7 == 0) goto L_0x1229;
    L_0x11fb:
        r7 = r1.drawVerified;
        if (r7 != 0) goto L_0x1229;
    L_0x11ff:
        r7 = r1.drawScam;
        if (r7 != 0) goto L_0x1229;
    L_0x1203:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        r9 = (double) r6;
        java.lang.Double.isNaN(r9);
        r9 = r9 - r4;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r9 = r9.getIntrinsicWidth();
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r7 = (int) r7;
        r1.nameMuteLeft = r7;
        goto L_0x127c;
    L_0x1229:
        r7 = r1.drawVerified;
        if (r7 == 0) goto L_0x1253;
    L_0x122d:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        r9 = (double) r6;
        java.lang.Double.isNaN(r9);
        r9 = r9 - r4;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r9 = r9.getIntrinsicWidth();
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r7 = (int) r7;
        r1.nameMuteLeft = r7;
        goto L_0x127c;
    L_0x1253:
        r7 = r1.drawScam;
        if (r7 == 0) goto L_0x127c;
    L_0x1257:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        r9 = (double) r6;
        java.lang.Double.isNaN(r9);
        r9 = r9 - r4;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9 = r9.getIntrinsicWidth();
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r7 = (int) r7;
        r1.nameMuteLeft = r7;
    L_0x127c:
        r7 = 0;
        r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1));
        if (r0 != 0) goto L_0x1294;
    L_0x1281:
        r6 = (double) r6;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 >= 0) goto L_0x1294;
    L_0x1286:
        r0 = r1.nameLeft;
        r8 = (double) r0;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r4;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r6;
        r0 = (int) r8;
        r1.nameLeft = r0;
    L_0x1294:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x12d5;
    L_0x1298:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x12d5;
    L_0x129e:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = 0;
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x12a5:
        if (r4 >= r0) goto L_0x12cb;
    L_0x12a7:
        r6 = r1.messageLayout;
        r6 = r6.getLineLeft(r4);
        r7 = 0;
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 != 0) goto L_0x12ca;
    L_0x12b2:
        r6 = r1.messageLayout;
        r6 = r6.getLineWidth(r4);
        r6 = (double) r6;
        r6 = java.lang.Math.ceil(r6);
        r8 = (double) r2;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r6;
        r6 = (int) r8;
        r5 = java.lang.Math.min(r5, r6);
        r4 = r4 + 1;
        goto L_0x12a5;
    L_0x12ca:
        r5 = 0;
    L_0x12cb:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r5 == r0) goto L_0x12d5;
    L_0x12d0:
        r0 = r1.messageLeft;
        r0 = r0 + r5;
        r1.messageLeft = r0;
    L_0x12d5:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x1394;
    L_0x12d9:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1394;
    L_0x12df:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineLeft(r3);
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x1394;
    L_0x12ea:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineWidth(r3);
        r3 = (double) r0;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r2;
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r0 >= 0) goto L_0x1394;
    L_0x12fa:
        r0 = r1.messageNameLeft;
        r7 = (double) r0;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r5;
        r0 = (int) r7;
        r1.messageNameLeft = r0;
        goto L_0x1394;
    L_0x130a:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x1358;
    L_0x130e:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1358;
    L_0x1314:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r3);
        r2 = (float) r6;
        r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x133d;
    L_0x131f:
        r2 = r1.nameLayout;
        r2 = r2.getLineWidth(r3);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r6 = (double) r6;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 >= 0) goto L_0x133d;
    L_0x132f:
        r2 = r1.nameLeft;
        r8 = (double) r2;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r4;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r6;
        r2 = (int) r8;
        r1.nameLeft = r2;
    L_0x133d:
        r2 = r1.dialogMuted;
        if (r2 != 0) goto L_0x1349;
    L_0x1341:
        r2 = r1.drawVerified;
        if (r2 != 0) goto L_0x1349;
    L_0x1345:
        r2 = r1.drawScam;
        if (r2 == 0) goto L_0x1358;
    L_0x1349:
        r2 = r1.nameLeft;
        r2 = (float) r2;
        r2 = r2 + r0;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (float) r0;
        r2 = r2 + r0;
        r0 = (int) r2;
        r1.nameMuteLeft = r0;
    L_0x1358:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x137d;
    L_0x135c:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x137d;
    L_0x1362:
        r2 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r2 = 0;
        r4 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
    L_0x1367:
        if (r2 >= r0) goto L_0x1376;
    L_0x1369:
        r5 = r1.messageLayout;
        r5 = r5.getLineLeft(r2);
        r4 = java.lang.Math.min(r4, r5);
        r2 = r2 + 1;
        goto L_0x1367;
    L_0x1376:
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r0 = r0 - r4;
        r0 = (int) r0;
        r1.messageLeft = r0;
    L_0x137d:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x1394;
    L_0x1381:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1394;
    L_0x1387:
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r2 = r1.messageNameLayout;
        r2 = r2.getLineLeft(r3);
        r0 = r0 - r2;
        r0 = (int) r0;
        r1.messageNameLeft = r0;
    L_0x1394:
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
            DraftMessage draft = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
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
            Theme.dialogs_archiveAvatarDrawable.setCallback(this);
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
        goto L_0x02fb;
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
        if (r1 == 0) goto L_0x02a3;
    L_0x028f:
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
        goto L_0x02fb;
    L_0x02a3:
        r1 = r0.user;
        if (r1 == 0) goto L_0x02df;
    L_0x02a7:
        r2 = r0.avatarDrawable;
        r2.setInfo(r1);
        r1 = r0.user;
        r1 = org.telegram.messenger.UserObject.isUserSelf(r1);
        if (r1 == 0) goto L_0x02c7;
    L_0x02b4:
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
        goto L_0x02fb;
    L_0x02c7:
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
        goto L_0x02fb;
    L_0x02df:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x02fb;
    L_0x02e3:
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
    L_0x02fb:
        r1 = r19.getMeasuredWidth();
        if (r1 != 0) goto L_0x030c;
    L_0x0301:
        r1 = r19.getMeasuredHeight();
        if (r1 == 0) goto L_0x0308;
    L_0x0307:
        goto L_0x030c;
    L_0x0308:
        r19.requestLayout();
        goto L_0x030f;
    L_0x030c:
        r19.buildLayout();
    L_0x030f:
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
        LottieDrawable lottieDrawable = this.translationDrawable;
        boolean z = false;
        if (lottieDrawable != null && this.translationX == 0.0f) {
            lottieDrawable.setProgress(0.0f);
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
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0a05  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0888  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x08a1  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x089e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x091c  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0a05  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x091c  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0a05  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042c  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0468  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x04bc  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x04d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0522  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x057f  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0570  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x05dc  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0809  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0888  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x089e  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x08a1  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x091c  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0a05  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042c  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0468  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x04bc  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x04d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0522  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0570  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x057f  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x05b6  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x05dc  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0658  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x06aa  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0809  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0888  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x08a1  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x089e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x091c  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x097f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x09ed  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0a05  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0a95  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a69  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0ab8  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    public void onDraw(android.graphics.Canvas r25) {
        /*
        r24 = this;
        r1 = r24;
        r8 = r25;
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
        r25.save();
        r0 = r1.topClip;
        r0 = (float) r0;
        r2 = r1.clipProgress;
        r0 = r0 * r2;
        r2 = r24.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r24.getMeasuredHeight();
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
        r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r13 = 0;
        r14 = 1;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 != 0) goto L_0x007d;
    L_0x005d:
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0064;
    L_0x0063:
        goto L_0x007d;
    L_0x0064:
        r0 = r1.translationDrawable;
        if (r0 == 0) goto L_0x02a3;
    L_0x0068:
        r0.stop();
        r0 = r1.translationDrawable;
        r0.setProgress(r11);
        r0 = r1.translationDrawable;
        r2 = 0;
        r0.setCallback(r2);
        r0 = 0;
        r1.translationDrawable = r0;
        r1.translationAnimationStarted = r13;
        goto L_0x02a3;
    L_0x007d:
        r25.save();
        r0 = r1.currentDialogFolderId;
        r16 = "chats_archiveBackground";
        r17 = "chats_archivePinBackground";
        if (r0 == 0) goto L_0x00b8;
    L_0x0088:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x00a2;
    L_0x008c:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0d09ae float:1.874714E38 double:1.053131002E-314;
        r4 = "UnhideFromTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00e7;
    L_0x00a2:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0d04a8 float:1.8744532E38 double:1.0531303665E-314;
        r4 = "HideOnTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00e7;
    L_0x00b8:
        r0 = r1.folderId;
        if (r0 != 0) goto L_0x00d2;
    L_0x00bc:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0d00ee float:1.8742597E38 double:1.053129895E-314;
        r4 = "Archive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x00e7;
    L_0x00d2:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0d09a6 float:1.8747124E38 double:1.053130998E-314;
        r4 = "Unarchive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable;
        r1.translationDrawable = r4;
    L_0x00e7:
        r7 = r2;
        r6 = r3;
        r2 = r1.translationAnimationStarted;
        r18 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        if (r2 != 0) goto L_0x010f;
    L_0x00ef:
        r2 = r1.translationX;
        r2 = java.lang.Math.abs(r2);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = (float) r3;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x010f;
    L_0x00fe:
        r1.translationAnimationStarted = r14;
        r2 = r1.translationDrawable;
        r2.setProgress(r11);
        r2 = r1.translationDrawable;
        r2.setCallback(r1);
        r2 = r1.translationDrawable;
        r2.start();
    L_0x010f:
        r2 = r24.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r1.translationX;
        r5 = r2 + r3;
        r2 = r1.currentRevealProgress;
        r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1));
        if (r2 >= 0) goto L_0x017e;
    L_0x011e:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2.setColor(r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r0 = (float) r0;
        r3 = r5 - r0;
        r0 = 0;
        r2 = r24.getMeasuredWidth();
        r2 = (float) r2;
        r4 = r24.getMeasuredHeight();
        r4 = (float) r4;
        r20 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r21 = r2;
        r2 = r25;
        r19 = r4;
        r15 = 2;
        r4 = r0;
        r0 = r5;
        r5 = r21;
        r22 = r6;
        r6 = r19;
        r23 = r7;
        r7 = r20;
        r2.drawRect(r3, r4, r5, r6, r7);
        r2 = r1.currentRevealProgress;
        r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r2 != 0) goto L_0x0184;
    L_0x0153:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored;
        if (r2 == 0) goto L_0x0184;
    L_0x0157:
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r13;
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r3 = new com.airbnb.lottie.model.KeyPath;
        r4 = new java.lang.String[r15];
        r5 = "Arrow";
        r4[r13] = r5;
        r5 = "**";
        r4[r14] = r5;
        r3.<init>(r4);
        r4 = com.airbnb.lottie.LottieProperty.COLOR_FILTER;
        r5 = new com.airbnb.lottie.value.LottieValueCallback;
        r6 = new com.airbnb.lottie.SimpleColorFilter;
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r6.<init>(r7);
        r5.<init>(r6);
        r2.addValueCallback(r3, r4, r5);
        goto L_0x0184;
    L_0x017e:
        r0 = r5;
        r22 = r6;
        r23 = r7;
        r15 = 2;
    L_0x0184:
        r2 = r24.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r3;
        r3 = r1.translationDrawable;
        r3 = r3.getIntrinsicWidth();
        r3 = r3 / r15;
        r2 = r2 - r3;
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x019c;
    L_0x0199:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        goto L_0x019e;
    L_0x019c:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
    L_0x019e:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.translationDrawable;
        r4 = r4.getIntrinsicWidth();
        r4 = r4 / r15;
        r4 = r4 + r2;
        r5 = r1.translationDrawable;
        r5 = r5.getIntrinsicHeight();
        r5 = r5 / r15;
        r5 = r5 + r3;
        r6 = r1.currentRevealProgress;
        r6 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r6 <= 0) goto L_0x022b;
    L_0x01b8:
        r25.save();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r6 = (float) r6;
        r6 = r0 - r6;
        r7 = r24.getMeasuredWidth();
        r7 = (float) r7;
        r12 = r24.getMeasuredHeight();
        r12 = (float) r12;
        r8.clipRect(r6, r11, r7, r12);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r7 = r23;
        r6.setColor(r7);
        r6 = r4 * r4;
        r7 = r24.getMeasuredHeight();
        r7 = r5 - r7;
        r12 = r24.getMeasuredHeight();
        r12 = r5 - r12;
        r7 = r7 * r12;
        r6 = r6 + r7;
        r6 = (double) r6;
        r6 = java.lang.Math.sqrt(r6);
        r6 = (float) r6;
        r4 = (float) r4;
        r5 = (float) r5;
        r7 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator;
        r12 = r1.currentRevealProgress;
        r7 = r7.getInterpolation(r12);
        r6 = r6 * r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r8.drawCircle(r4, r5, r6, r7);
        r25.restore();
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored;
        if (r4 != 0) goto L_0x022b;
    L_0x0205:
        org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r14;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r5 = new com.airbnb.lottie.model.KeyPath;
        r6 = new java.lang.String[r15];
        r7 = "Arrow";
        r6[r13] = r7;
        r7 = "**";
        r6[r14] = r7;
        r5.<init>(r6);
        r6 = com.airbnb.lottie.LottieProperty.COLOR_FILTER;
        r7 = new com.airbnb.lottie.value.LottieValueCallback;
        r12 = new com.airbnb.lottie.SimpleColorFilter;
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r12.<init>(r14);
        r7.<init>(r12);
        r4.addValueCallback(r5, r6, r7);
    L_0x022b:
        r25.save();
        r4 = (float) r2;
        r5 = (float) r3;
        r8.translate(r4, r5);
        r4 = r1.currentRevealBounceProgress;
        r5 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r5 == 0) goto L_0x0259;
    L_0x0239:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r6 == 0) goto L_0x0259;
    L_0x023f:
        r6 = r1.interpolator;
        r4 = r6.getInterpolation(r4);
        r4 = r4 + r5;
        r5 = r1.translationDrawable;
        r5 = r5.getIntrinsicWidth();
        r5 = r5 / r15;
        r5 = (float) r5;
        r6 = r1.translationDrawable;
        r6 = r6.getIntrinsicHeight();
        r6 = r6 / r15;
        r6 = (float) r6;
        r8.scale(r4, r4, r5, r6);
    L_0x0259:
        r4 = r1.translationDrawable;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r4, r2, r3);
        r2 = r1.translationDrawable;
        r2.draw(r8);
        r25.restore();
        r2 = r24.getMeasuredWidth();
        r2 = (float) r2;
        r3 = r24.getMeasuredHeight();
        r3 = (float) r3;
        r8.clipRect(r0, r11, r2, r3);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r3 = r22;
        r0 = r0.measureText(r3);
        r4 = (double) r0;
        r4 = java.lang.Math.ceil(r4);
        r0 = (int) r4;
        r2 = r24.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r4;
        r0 = r0 / r15;
        r2 = r2 - r0;
        r0 = (float) r2;
        r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r2 == 0) goto L_0x0294;
    L_0x0291:
        r2 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
        goto L_0x0296;
    L_0x0294:
        r2 = NUM; // 0x426CLASSNAME float:59.0 double:5.50573981E-315;
    L_0x0296:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8.drawText(r3, r0, r2, r4);
        r25.restore();
    L_0x02a3:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x02b1;
    L_0x02a9:
        r25.save();
        r0 = r1.translationX;
        r8.translate(r0, r11);
    L_0x02b1:
        r0 = r1.isSelected;
        if (r0 == 0) goto L_0x02c8;
    L_0x02b5:
        r3 = 0;
        r4 = 0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint;
        r2 = r25;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x02c8:
        r0 = r1.currentDialogFolderId;
        r12 = "chats_pinnedOverlay";
        if (r0 == 0) goto L_0x02fd;
    L_0x02ce:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x02d8;
    L_0x02d2:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x02fd;
    L_0x02d8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r3 = r1.archiveBackgroundProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r13, r2, r3, r4);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r25;
        r2.drawRect(r3, r4, r5, r6, r7);
        goto L_0x0321;
    L_0x02fd:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x0305;
    L_0x0301:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x0321;
    L_0x0305:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r25;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x0321:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 != 0) goto L_0x0332;
    L_0x0327:
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x032e;
    L_0x032d:
        goto L_0x0332;
    L_0x032e:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03e5;
    L_0x0332:
        r25.save();
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = "windowBackgroundWhite";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r0 = r1.rect;
        r2 = r24.getMeasuredWidth();
        r3 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r2 = (float) r2;
        r3 = r24.getMeasuredWidth();
        r3 = (float) r3;
        r4 = r24.getMeasuredHeight();
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
        if (r0 == 0) goto L_0x03b2;
    L_0x037b:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x0385;
    L_0x037f:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x03b2;
    L_0x0385:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r3 = r1.archiveBackgroundProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r13, r2, r3, r4);
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
        goto L_0x03bb;
    L_0x03b2:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x03be;
    L_0x03b6:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x03bb;
    L_0x03ba:
        goto L_0x03be;
    L_0x03bb:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03e2;
    L_0x03be:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r12);
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
    L_0x03e2:
        r25.restore();
    L_0x03e5:
        r0 = r1.translationX;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0403;
    L_0x03ed:
        r0 = r1.cornerProgress;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r5 >= 0) goto L_0x0418;
    L_0x03f5:
        r5 = (float) r9;
        r5 = r5 / r3;
        r0 = r0 + r5;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 <= 0) goto L_0x0416;
    L_0x0400:
        r1.cornerProgress = r4;
        goto L_0x0416;
    L_0x0403:
        r0 = r1.cornerProgress;
        r4 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r4 <= 0) goto L_0x0418;
    L_0x0409:
        r4 = (float) r9;
        r4 = r4 / r3;
        r0 = r0 - r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0416;
    L_0x0414:
        r1.cornerProgress = r11;
    L_0x0416:
        r4 = 1;
        goto L_0x0419;
    L_0x0418:
        r4 = 0;
    L_0x0419:
        r0 = r1.drawNameLock;
        if (r0 == 0) goto L_0x042c;
    L_0x041d:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r0.draw(r8);
        goto L_0x0464;
    L_0x042c:
        r0 = r1.drawNameGroup;
        if (r0 == 0) goto L_0x043f;
    L_0x0430:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r0.draw(r8);
        goto L_0x0464;
    L_0x043f:
        r0 = r1.drawNameBroadcast;
        if (r0 == 0) goto L_0x0452;
    L_0x0443:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r0.draw(r8);
        goto L_0x0464;
    L_0x0452:
        r0 = r1.drawNameBot;
        if (r0 == 0) goto L_0x0464;
    L_0x0456:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r0.draw(r8);
    L_0x0464:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x04b8;
    L_0x0468:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x047a;
    L_0x046c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_nameArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0499;
    L_0x047a:
        r0 = r1.encryptedChat;
        if (r0 == 0) goto L_0x048c;
    L_0x047e:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_secretName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0499;
    L_0x048c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = "chats_name";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0499:
        r25.save();
        r0 = r1.nameLeft;
        r0 = (float) r0;
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x04a6;
    L_0x04a3:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x04a8;
    L_0x04a6:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
    L_0x04a8:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.nameLayout;
        r0.draw(r8);
        r25.restore();
    L_0x04b8:
        r0 = r1.timeLayout;
        if (r0 == 0) goto L_0x04d0;
    L_0x04bc:
        r25.save();
        r0 = r1.timeLeft;
        r0 = (float) r0;
        r5 = r1.timeTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.timeLayout;
        r0.draw(r8);
        r25.restore();
    L_0x04d0:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x051e;
    L_0x04d4:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x04e6;
    L_0x04d8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessageArchived_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0505;
    L_0x04e6:
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x04f8;
    L_0x04ea:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_draft";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0505;
    L_0x04f8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessage_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0505:
        r25.save();
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r5 = r1.messageNameTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageNameLayout;	 Catch:{ Exception -> 0x0517 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0517 }
        goto L_0x051b;
    L_0x0517:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x051b:
        r25.restore();
    L_0x051e:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x056c;
    L_0x0522:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0546;
    L_0x0526:
        r0 = r1.chat;
        if (r0 == 0) goto L_0x0538;
    L_0x052a:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_nameMessageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0553;
    L_0x0538:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_messageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x0553;
    L_0x0546:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = "chats_message";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x0553:
        r25.save();
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r5 = r1.messageTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageLayout;	 Catch:{ Exception -> 0x0565 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0565 }
        goto L_0x0569;
    L_0x0565:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0569:
        r25.restore();
    L_0x056c:
        r0 = r1.drawClock;
        if (r0 == 0) goto L_0x057f;
    L_0x0570:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r0.draw(r8);
        goto L_0x05b2;
    L_0x057f:
        r0 = r1.drawCheck2;
        if (r0 == 0) goto L_0x05b2;
    L_0x0583:
        r0 = r1.drawCheck1;
        if (r0 == 0) goto L_0x05a4;
    L_0x0587:
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
        goto L_0x05b2;
    L_0x05a4:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r0.draw(r8);
    L_0x05b2:
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x05d8;
    L_0x05b6:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x05d8;
    L_0x05ba:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x05d8;
    L_0x05be:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r5 = r1.nameMuteLeft;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05c9;
    L_0x05c6:
        r6 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        goto L_0x05cb;
    L_0x05c9:
        r6 = NUM; // 0x41940000 float:18.5 double:5.435800986E-315;
    L_0x05cb:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r0.draw(r8);
        goto L_0x062c;
    L_0x05d8:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x060f;
    L_0x05dc:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r5 = r1.nameMuteLeft;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05e7;
    L_0x05e4:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        goto L_0x05e9;
    L_0x05e7:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
    L_0x05e9:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r5 = r1.nameMuteLeft;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05fb;
    L_0x05f8:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        goto L_0x05fd;
    L_0x05fb:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
    L_0x05fd:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r0.draw(r8);
        goto L_0x062c;
    L_0x060f:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x062c;
    L_0x0613:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r5 = r1.nameMuteLeft;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x061e;
    L_0x061b:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        goto L_0x0620;
    L_0x061e:
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
    L_0x0620:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r0.draw(r8);
    L_0x062c:
        r0 = r1.drawReorder;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 != 0) goto L_0x0638;
    L_0x0632:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0650;
    L_0x0638:
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
    L_0x0650:
        r0 = r1.drawError;
        r6 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r7 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        if (r0 == 0) goto L_0x06aa;
    L_0x0658:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r12 = r1.reorderIconProgress;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r14 - r12;
        r15 = r15 * r5;
        r5 = (int) r15;
        r0.setAlpha(r5);
        r0 = r1.rect;
        r5 = r1.errorLeft;
        r12 = (float) r5;
        r14 = r1.errorTop;
        r14 = (float) r14;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r15;
        r5 = (float) r5;
        r15 = r1.errorTop;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = r15 + r6;
        r6 = (float) r15;
        r0.set(r12, r14, r5, r6);
        r0 = r1.rect;
        r5 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r5 * r7;
        r5 = r5 * r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint;
        r8.drawRoundRect(r0, r6, r5, r7);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r5 = r1.errorLeft;
        r6 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r6 = r1.errorTop;
        r7 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 + r7;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable;
        r0.draw(r8);
        goto L_0x0803;
    L_0x06aa:
        r0 = r1.drawCount;
        if (r0 != 0) goto L_0x06d5;
    L_0x06ae:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x06b3;
    L_0x06b2:
        goto L_0x06d5;
    L_0x06b3:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0803;
    L_0x06b7:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r6 = r1.reorderIconProgress;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r7 - r6;
        r15 = r15 * r5;
        r5 = (int) r15;
        r0.setAlpha(r5);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r5 = r1.pinLeft;
        r6 = r1.pinTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0.draw(r8);
        goto L_0x0803;
    L_0x06d5:
        r0 = r1.drawCount;
        if (r0 == 0) goto L_0x074f;
    L_0x06d9:
        r0 = r1.dialogMuted;
        if (r0 != 0) goto L_0x06e5;
    L_0x06dd:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x06e2;
    L_0x06e1:
        goto L_0x06e5;
    L_0x06e2:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        goto L_0x06e7;
    L_0x06e5:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
    L_0x06e7:
        r12 = r1.reorderIconProgress;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r14 - r12;
        r15 = r15 * r5;
        r12 = (int) r15;
        r0.setAlpha(r12);
        r12 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r15 = r1.reorderIconProgress;
        r15 = r14 - r15;
        r15 = r15 * r5;
        r14 = (int) r15;
        r12.setAlpha(r14);
        r12 = r1.countLeft;
        r14 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = r12 - r14;
        r14 = r1.rect;
        r15 = (float) r12;
        r2 = r1.countTop;
        r2 = (float) r2;
        r13 = r1.countWidth;
        r12 = r12 + r13;
        r13 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r12 = r12 + r13;
        r12 = (float) r12;
        r13 = r1.countTop;
        r18 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = r13 + r18;
        r13 = (float) r13;
        r14.set(r15, r2, r12, r13);
        r2 = r1.rect;
        r12 = org.telegram.messenger.AndroidUtilities.density;
        r13 = r12 * r7;
        r12 = r12 * r7;
        r8.drawRoundRect(r2, r13, r12, r0);
        r0 = r1.countLayout;
        if (r0 == 0) goto L_0x074f;
    L_0x0734:
        r25.save();
        r0 = r1.countLeft;
        r0 = (float) r0;
        r2 = r1.countTop;
        r12 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r2 = r2 + r12;
        r2 = (float) r2;
        r8.translate(r0, r2);
        r0 = r1.countLayout;
        r0.draw(r8);
        r25.restore();
    L_0x074f:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x0803;
    L_0x0753:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        r2 = r1.reorderIconProgress;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r12 - r2;
        r15 = r15 * r5;
        r2 = (int) r15;
        r0.setAlpha(r2);
        r0 = r1.mentionLeft;
        r2 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.rect;
        r12 = (float) r0;
        r13 = r1.countTop;
        r13 = (float) r13;
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
        r2.set(r12, r13, r0, r6);
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x0791;
    L_0x078a:
        r0 = r1.folderId;
        if (r0 == 0) goto L_0x0791;
    L_0x078e:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
        goto L_0x0793;
    L_0x0791:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
    L_0x0793:
        r2 = r1.rect;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r12 = r6 * r7;
        r6 = r6 * r7;
        r8.drawRoundRect(r2, r12, r6, r0);
        r0 = r1.mentionLayout;
        if (r0 == 0) goto L_0x07cc;
    L_0x07a2:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r2 = r1.reorderIconProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r6 - r2;
        r15 = r15 * r5;
        r2 = (int) r15;
        r0.setAlpha(r2);
        r25.save();
        r0 = r1.mentionLeft;
        r0 = (float) r0;
        r2 = r1.countTop;
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        r2 = (float) r2;
        r8.translate(r0, r2);
        r0 = r1.mentionLayout;
        r0.draw(r8);
        r25.restore();
        goto L_0x0803;
    L_0x07cc:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r2 = r1.reorderIconProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = r6 - r2;
        r15 = r15 * r5;
        r2 = (int) r15;
        r0.setAlpha(r2);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r2 = r1.mentionLeft;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r5 = r1.countTop;
        r6 = NUM; // 0x404ccccd float:3.2 double:5.329856617E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r2, r5, r6, r7);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable;
        r0.draw(r8);
    L_0x0803:
        r0 = r1.animatingArchiveAvatar;
        r12 = NUM; // 0x432a0000 float:170.0 double:5.567260075E-315;
        if (r0 == 0) goto L_0x0827;
    L_0x0809:
        r25.save();
        r0 = r1.interpolator;
        r2 = r1.animatingArchiveAvatarProgress;
        r2 = r2 / r12;
        r0 = r0.getInterpolation(r2);
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r0 + r2;
        r2 = r1.avatarImage;
        r2 = r2.getCenterX();
        r5 = r1.avatarImage;
        r5 = r5.getCenterY();
        r8.scale(r0, r0, r2, r5);
    L_0x0827:
        r0 = r1.avatarImage;
        r0.draw(r8);
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0833;
    L_0x0830:
        r25.restore();
    L_0x0833:
        r0 = r1.user;
        if (r0 == 0) goto L_0x090e;
    L_0x0837:
        r2 = r1.isDialogCell;
        if (r2 == 0) goto L_0x090e;
    L_0x083b:
        r2 = r1.currentDialogFolderId;
        if (r2 != 0) goto L_0x090e;
    L_0x083f:
        r0 = org.telegram.messenger.MessagesController.isSupportUser(r0);
        if (r0 != 0) goto L_0x090e;
    L_0x0845:
        r0 = r1.user;
        r2 = r0.self;
        if (r2 != 0) goto L_0x0875;
    L_0x084b:
        r0 = r0.status;
        if (r0 == 0) goto L_0x085d;
    L_0x084f:
        r0 = r0.expires;
        r2 = r1.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        if (r0 > r2) goto L_0x0873;
    L_0x085d:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.onlinePrivacy;
        r2 = r1.user;
        r2 = r2.id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.containsKey(r2);
        if (r0 == 0) goto L_0x0875;
    L_0x0873:
        r0 = 1;
        goto L_0x0876;
    L_0x0875:
        r0 = 0;
    L_0x0876:
        if (r0 != 0) goto L_0x087e;
    L_0x0878:
        r2 = r1.onlineProgress;
        r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r2 == 0) goto L_0x090e;
    L_0x087e:
        r2 = r1.avatarImage;
        r2 = r2.getImageX2();
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x088d;
    L_0x0888:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x088f;
    L_0x088d:
        r16 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
    L_0x088f:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r2 - r5;
        r5 = r1.avatarImage;
        r5 = r5.getImageY2();
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x08a1;
    L_0x089e:
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x08a3;
    L_0x08a1:
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
    L_0x08a3:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r7 = "windowBackgroundWhite";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r6.setColor(r7);
        r5 = (float) r5;
        r2 = (float) r2;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r7 = r1.onlineProgress;
        r6 = r6 * r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r8.drawCircle(r5, r2, r6, r7);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r7 = "chats_onlineCircle";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r6.setColor(r7);
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r7 = r1.onlineProgress;
        r6 = r6 * r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r8.drawCircle(r5, r2, r6, r7);
        if (r0 == 0) goto L_0x08f9;
    L_0x08e3:
        r0 = r1.onlineProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x090e;
    L_0x08eb:
        r4 = (float) r9;
        r4 = r4 / r3;
        r0 = r0 + r4;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x090c;
    L_0x08f6:
        r1.onlineProgress = r2;
        goto L_0x090c;
    L_0x08f9:
        r0 = r1.onlineProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x090e;
    L_0x08ff:
        r2 = (float) r9;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x090c;
    L_0x090a:
        r1.onlineProgress = r11;
    L_0x090c:
        r14 = 1;
        goto L_0x090f;
    L_0x090e:
        r14 = r4;
    L_0x090f:
        r0 = r1.translationX;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0918;
    L_0x0915:
        r25.restore();
    L_0x0918:
        r0 = r1.useSeparator;
        if (r0 == 0) goto L_0x0978;
    L_0x091c:
        r0 = r1.fullSeparator;
        if (r0 != 0) goto L_0x093c;
    L_0x0920:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x092c;
    L_0x0924:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x092c;
    L_0x0928:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x093c;
    L_0x092c:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x0935;
    L_0x0930:
        r0 = r1.archiveHidden;
        if (r0 != 0) goto L_0x0935;
    L_0x0934:
        goto L_0x093c;
    L_0x0935:
        r0 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r0);
        goto L_0x093d;
    L_0x093c:
        r13 = 0;
    L_0x093d:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x095d;
    L_0x0941:
        r3 = 0;
        r0 = r24.getMeasuredHeight();
        r2 = 1;
        r0 = r0 - r2;
        r4 = (float) r0;
        r0 = r24.getMeasuredWidth();
        r0 = r0 - r13;
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r0 = r0 - r2;
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dividerPaint;
        r2 = r25;
        r2.drawLine(r3, r4, r5, r6, r7);
        goto L_0x0978;
    L_0x095d:
        r3 = (float) r13;
        r0 = r24.getMeasuredHeight();
        r13 = 1;
        r0 = r0 - r13;
        r4 = (float) r0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r0 = r0 - r13;
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dividerPaint;
        r2 = r25;
        r2.drawLine(r3, r4, r5, r6, r7);
        goto L_0x0979;
    L_0x0978:
        r13 = 1;
    L_0x0979:
        r0 = r1.clipProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x09c9;
    L_0x097f:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x0989;
    L_0x0985:
        r25.restore();
        goto L_0x09c9;
    L_0x0989:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = "windowBackgroundWhite";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r3 = 0;
        r4 = 0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r1.topClip;
        r0 = (float) r0;
        r2 = r1.clipProgress;
        r6 = r0 * r2;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r25;
        r2.drawRect(r3, r4, r5, r6, r7);
        r0 = r24.getMeasuredHeight();
        r2 = r1.bottomClip;
        r2 = (float) r2;
        r4 = r1.clipProgress;
        r2 = r2 * r4;
        r2 = (int) r2;
        r0 = r0 - r2;
        r4 = (float) r0;
        r0 = r24.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r24.getMeasuredHeight();
        r6 = (float) r0;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint;
        r2 = r25;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x09c9:
        r0 = r1.drawReorder;
        if (r0 != 0) goto L_0x09d3;
    L_0x09cd:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x0a01;
    L_0x09d3:
        r0 = r1.drawReorder;
        if (r0 == 0) goto L_0x09ed;
    L_0x09d7:
        r0 = r1.reorderIconProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r3 >= 0) goto L_0x0a01;
    L_0x09df:
        r3 = (float) r9;
        r3 = r3 / r12;
        r0 = r0 + r3;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a00;
    L_0x09ea:
        r1.reorderIconProgress = r2;
        goto L_0x0a00;
    L_0x09ed:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a01;
    L_0x09f3:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a00;
    L_0x09fe:
        r1.reorderIconProgress = r11;
    L_0x0a00:
        r14 = 1;
    L_0x0a01:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0a29;
    L_0x0a05:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a4f;
    L_0x0a0b:
        r2 = (float) r9;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a18;
    L_0x0a16:
        r1.currentRevealBounceProgress = r11;
    L_0x0a18:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0a4e;
    L_0x0a21:
        r0 = r1.avatarDrawable;
        r2 = r1.archiveBackgroundProgress;
        r0.setArchivedAvatarHiddenProgress(r2);
        goto L_0x0a4e;
    L_0x0a29:
        r0 = r1.archiveBackgroundProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r3 >= 0) goto L_0x0a4f;
    L_0x0a31:
        r3 = (float) r9;
        r3 = r3 / r12;
        r0 = r0 + r3;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a3e;
    L_0x0a3c:
        r1.currentRevealBounceProgress = r2;
    L_0x0a3e:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0a4e;
    L_0x0a47:
        r0 = r1.avatarDrawable;
        r2 = r1.archiveBackgroundProgress;
        r0.setArchivedAvatarHiddenProgress(r2);
    L_0x0a4e:
        r14 = 1;
    L_0x0a4f:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0a65;
    L_0x0a53:
        r0 = r1.animatingArchiveAvatarProgress;
        r2 = (float) r9;
        r0 = r0 + r2;
        r1.animatingArchiveAvatarProgress = r0;
        r0 = r1.animatingArchiveAvatarProgress;
        r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1));
        if (r0 < 0) goto L_0x0a64;
    L_0x0a5f:
        r1.animatingArchiveAvatarProgress = r12;
        r2 = 0;
        r1.animatingArchiveAvatar = r2;
    L_0x0a64:
        r14 = 1;
    L_0x0a65:
        r0 = r1.drawRevealBackground;
        if (r0 == 0) goto L_0x0a95;
    L_0x0a69:
        r0 = r1.currentRevealBounceProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r3 >= 0) goto L_0x0a7f;
    L_0x0a71:
        r3 = (float) r9;
        r3 = r3 / r12;
        r0 = r0 + r3;
        r1.currentRevealBounceProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a7f;
    L_0x0a7c:
        r1.currentRevealBounceProgress = r2;
        r14 = 1;
    L_0x0a7f:
        r0 = r1.currentRevealProgress;
        r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r3 >= 0) goto L_0x0ab6;
    L_0x0a85:
        r3 = (float) r9;
        r4 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r3 = r3 / r4;
        r0 = r0 + r3;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0ab5;
    L_0x0a92:
        r1.currentRevealProgress = r2;
        goto L_0x0ab5;
    L_0x0a95:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x0aa0;
    L_0x0a9d:
        r1.currentRevealBounceProgress = r11;
        r14 = 1;
    L_0x0aa0:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r2 <= 0) goto L_0x0ab6;
    L_0x0aa6:
        r2 = (float) r9;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x0ab5;
    L_0x0ab3:
        r1.currentRevealProgress = r11;
    L_0x0ab5:
        r14 = 1;
    L_0x0ab6:
        if (r14 == 0) goto L_0x0abb;
    L_0x0ab8:
        r24.invalidate();
    L_0x0abb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    public void onReorderStateChanged(boolean z) {
        if ((this.drawPin || !z) && this.drawReorder != z) {
            this.drawReorder = z;
            this.reorderIconProgress = this.drawReorder ? 0.0f : 1.0f;
            invalidate();
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
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        StringBuilder stringBuilder = new StringBuilder();
        String str = ". ";
        if (this.encryptedChat != null) {
            stringBuilder.append(LocaleController.getString("AccDescrSecretChat", NUM));
            stringBuilder.append(str);
        }
        User user = this.user;
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
        int i = this.unreadCount;
        if (i > 0) {
            stringBuilder.append(LocaleController.formatPluralString("NewMessages", i));
            stringBuilder.append(str);
        }
        MessageObject messageObject = this.message;
        if (messageObject == null) {
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
