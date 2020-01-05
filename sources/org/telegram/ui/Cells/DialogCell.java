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
        resetPinnedArchiveState();
    }

    public void resetPinnedArchiveState() {
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
        return Emoji.replaceEmoji(spannableStringBuilder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:534:0x0bdb  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0bc0  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0bbb  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0bbb  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0bc0  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0bdb  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0b12  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0b0a  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0b0a  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0b12  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0b12  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0b0a  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0b0a  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0b12  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x125e  */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x121a A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1247 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1244 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x125e  */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0d9e  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0d72  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x0e5b  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x0e45  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:662:0x0fbb  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x100e  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0fe0  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x112f  */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x11e6 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x121a A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1244 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1247 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x125e  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0371  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0cf7  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0cb2  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0d14  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0d04  */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0d3b  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0d72  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0d9e  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0e23  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x0e45  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x0e5b  */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0ebb  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:662:0x0fbb  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0fe0  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x100e  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x112f  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1180  */
    /* JADX WARNING: Removed duplicated region for block: B:742:0x11d9 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x11e6 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x11f8 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x121a A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1247 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1244 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x125e  */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0371  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0cb2  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0cf7  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0d04  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0d14  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0d2c  */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0d3b  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0d9e  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0d72  */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0e23  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x0e5b  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x0e45  */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0ebb  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:662:0x0fbb  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x100e  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0fe0  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x112f  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x1180  */
    /* JADX WARNING: Removed duplicated region for block: B:742:0x11d9 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x11e6 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x11f8 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x121a A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x1244 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1247 A:{Catch:{ Exception -> 0x1256 }} */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x1386  */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x125e  */
    /* JADX WARNING: Missing block: B:258:0x0618, code skipped:
            if (r9.post_messages != false) goto L_0x061a;
     */
    public void buildLayout() {
        /*
        r42 = this;
        r1 = r42;
        r0 = r1.useForceThreeLines;
        r2 = 1;
        r3 = 0;
        if (r0 != 0) goto L_0x0059;
    L_0x0008:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x000d;
    L_0x000c:
        goto L_0x0059;
    L_0x000d:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r0 = r0[r3];
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r0 = r0[r3];
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r0 = r0[r3];
        r4 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = r0[r3];
        r4 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r4 = r0[r3];
        r0 = r0[r3];
        r5 = "chats_message";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r4.setColor(r5);
        r1.paintIndex = r3;
        goto L_0x00a4;
    L_0x0059:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r0 = r0[r2];
        r4 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        r0 = r0[r2];
        r4 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r0 = r0[r2];
        r4 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r0 = r0[r2];
        r4 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0.setTextSize(r4);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r4 = r0[r2];
        r0 = r0[r2];
        r5 = "chats_message_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r4.setColor(r5);
        r1.paintIndex = r2;
    L_0x00a4:
        r1.currentDialogFolderDialogsCount = r3;
        r0 = r1.isDialogCell;
        if (r0 == 0) goto L_0x00bb;
    L_0x00aa:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.printingStrings;
        r5 = r1.currentDialogId;
        r0 = r0.get(r5);
        r0 = (java.lang.CharSequence) r0;
        goto L_0x00bc;
    L_0x00bb:
        r0 = 0;
    L_0x00bc:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r6 = r1.paintIndex;
        r5 = r5[r6];
        r1.drawNameGroup = r3;
        r1.drawNameBroadcast = r3;
        r1.drawNameLock = r3;
        r1.drawNameBot = r3;
        r1.drawVerified = r3;
        r1.drawScam = r3;
        r1.drawPinBackground = r3;
        r6 = r1.user;
        r6 = org.telegram.messenger.UserObject.isUserSelf(r6);
        if (r6 != 0) goto L_0x00de;
    L_0x00d8:
        r6 = r1.useMeForMyMessages;
        if (r6 != 0) goto L_0x00de;
    L_0x00dc:
        r6 = 1;
        goto L_0x00df;
    L_0x00de:
        r6 = 0;
    L_0x00df:
        r7 = android.os.Build.VERSION.SDK_INT;
        r8 = 18;
        if (r7 < r8) goto L_0x00f8;
    L_0x00e5:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x00ed;
    L_0x00e9:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x00f1;
    L_0x00ed:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x00f4;
    L_0x00f1:
        r7 = "%2$s: ⁨%1$s⁩";
        goto L_0x0106;
    L_0x00f4:
        r7 = "⁨%s⁩";
        goto L_0x010a;
    L_0x00f8:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x0100;
    L_0x00fc:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x0104;
    L_0x0100:
        r7 = r1.currentDialogFolderId;
        if (r7 == 0) goto L_0x0108;
    L_0x0104:
        r7 = "%2$s: %1$s";
    L_0x0106:
        r8 = 1;
        goto L_0x010b;
    L_0x0108:
        r7 = "%1$s";
    L_0x010a:
        r8 = 0;
    L_0x010b:
        r9 = r1.message;
        if (r9 == 0) goto L_0x0112;
    L_0x010f:
        r9 = r9.messageText;
        goto L_0x0113;
    L_0x0112:
        r9 = 0;
    L_0x0113:
        r1.lastMessageString = r9;
        r9 = r1.customDialog;
        r10 = 32;
        r11 = 10;
        r13 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r14 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r15 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r4 = "";
        r16 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r17 = NUM; // 0x429CLASSNAME float:78.0 double:5.521281773E-315;
        r12 = 2;
        if (r9 == 0) goto L_0x0371;
    L_0x012a:
        r0 = r9.type;
        if (r0 != r12) goto L_0x01b3;
    L_0x012e:
        r1.drawNameLock = r2;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0176;
    L_0x0134:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0139;
    L_0x0138:
        goto L_0x0176;
    L_0x0139:
        r0 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x015c;
    L_0x0145:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x015c:
        r0 = r42.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0176:
        r0 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0199;
    L_0x0182:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0199:
        r0 = r42.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x01b3:
        r6 = r9.verified;
        r1.drawVerified = r6;
        r6 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r6 == 0) goto L_0x025c;
    L_0x01bb:
        if (r0 != r2) goto L_0x025c;
    L_0x01bd:
        r1.drawNameGroup = r2;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0213;
    L_0x01c3:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x01c8;
    L_0x01c7:
        goto L_0x0213;
    L_0x01c8:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01f2;
    L_0x01cc:
        r0 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x01e7;
    L_0x01e4:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x01e9;
    L_0x01e7:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x01e9:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x01f2:
        r0 = r42.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 - r6;
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x0202;
    L_0x01ff:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0204;
    L_0x0202:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0204:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0213:
        r0 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockTop = r0;
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x023c;
    L_0x021f:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r0;
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x0232;
    L_0x022f:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0234;
    L_0x0232:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0234:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x023c:
        r0 = r42.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 - r6;
        r6 = r1.drawNameGroup;
        if (r6 == 0) goto L_0x024c;
    L_0x0249:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x024e;
    L_0x024c:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x024e:
        r6 = r6.getIntrinsicWidth();
        r0 = r0 - r6;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x025c:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0277;
    L_0x0260:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0265;
    L_0x0264:
        goto L_0x0277;
    L_0x0265:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0270;
    L_0x0269:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0270:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0277:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0282;
    L_0x027b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r0;
        goto L_0x0288;
    L_0x0282:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r0;
    L_0x0288:
        r0 = r1.customDialog;
        r6 = r0.type;
        if (r6 != r2) goto L_0x0316;
    L_0x028e:
        r0 = NUM; // 0x7f0e051a float:1.8877686E38 double:1.053162802E-314;
        r6 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r6 = r1.customDialog;
        r8 = r6.isMedia;
        if (r8 == 0) goto L_0x02c8;
    L_0x029d:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r6 = r1.paintIndex;
        r5 = r5[r6];
        r6 = new java.lang.Object[r2];
        r8 = r1.message;
        r8 = r8.messageText;
        r6[r3] = r8;
        r6 = java.lang.String.format(r7, r6);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
        r7 = new android.text.style.ForegroundColorSpan;
        r8 = "chats_attachMessage";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.<init>(r8);
        r8 = r6.length();
        r9 = 33;
        r6.setSpan(r7, r3, r8, r9);
        goto L_0x02fe;
    L_0x02c8:
        r6 = r6.message;
        r8 = r6.length();
        if (r8 <= r14) goto L_0x02d4;
    L_0x02d0:
        r6 = r6.substring(r3, r14);
    L_0x02d4:
        r8 = r1.useForceThreeLines;
        if (r8 != 0) goto L_0x02f0;
    L_0x02d8:
        r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r8 == 0) goto L_0x02dd;
    L_0x02dc:
        goto L_0x02f0;
    L_0x02dd:
        r8 = new java.lang.Object[r12];
        r6 = r6.replace(r11, r10);
        r8[r3] = r6;
        r8[r2] = r0;
        r6 = java.lang.String.format(r7, r8);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
        goto L_0x02fe;
    L_0x02f0:
        r8 = new java.lang.Object[r12];
        r8[r3] = r6;
        r8[r2] = r0;
        r6 = java.lang.String.format(r7, r8);
        r6 = android.text.SpannableStringBuilder.valueOf(r6);
    L_0x02fe:
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r8 = r1.paintIndex;
        r7 = r7[r8];
        r7 = r7.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r6, r7, r9, r3);
        r7 = r5;
        r5 = r0;
        r0 = 0;
        goto L_0x0325;
    L_0x0316:
        r6 = r0.message;
        r0 = r0.isMedia;
        if (r0 == 0) goto L_0x0322;
    L_0x031c:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r5 = r1.paintIndex;
        r5 = r0[r5];
    L_0x0322:
        r7 = r5;
        r0 = 1;
        r5 = 0;
    L_0x0325:
        r8 = r1.customDialog;
        r8 = r8.date;
        r8 = (long) r8;
        r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8);
        r9 = r1.customDialog;
        r9 = r9.unread_count;
        if (r9 == 0) goto L_0x0345;
    L_0x0334:
        r1.drawCount = r2;
        r10 = new java.lang.Object[r2];
        r9 = java.lang.Integer.valueOf(r9);
        r10[r3] = r9;
        r9 = "%d";
        r9 = java.lang.String.format(r9, r10);
        goto L_0x0348;
    L_0x0345:
        r1.drawCount = r3;
        r9 = 0;
    L_0x0348:
        r10 = r1.customDialog;
        r10 = r10.sent;
        if (r10 == 0) goto L_0x0357;
    L_0x034e:
        r1.drawCheck1 = r2;
        r1.drawCheck2 = r2;
        r1.drawClock = r3;
        r1.drawError = r3;
        goto L_0x035f;
    L_0x0357:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r3;
    L_0x035f:
        r10 = r1.customDialog;
        r10 = r10.name;
        r28 = r0;
        r40 = r5;
        r29 = r6;
        r11 = r7;
        r6 = r8;
        r14 = r9;
        r13 = r10;
        r0 = 1;
        r12 = 0;
        goto L_0x0cb0;
    L_0x0371:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x038c;
    L_0x0375:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x037a;
    L_0x0379:
        goto L_0x038c;
    L_0x037a:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0385;
    L_0x037e:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLeft = r9;
        goto L_0x039d;
    L_0x0385:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x039d;
    L_0x038c:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0397;
    L_0x0390:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLeft = r9;
        goto L_0x039d;
    L_0x0397:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x039d:
        r9 = r1.encryptedChat;
        if (r9 == 0) goto L_0x042a;
    L_0x03a1:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x05c3;
    L_0x03a5:
        r1.drawNameLock = r2;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x03ed;
    L_0x03ab:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x03b0;
    L_0x03af:
        goto L_0x03ed;
    L_0x03b0:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x03d3;
    L_0x03bc:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x03d3:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x03ed:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0410;
    L_0x03f9:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0410:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x042a:
        r9 = r1.currentDialogFolderId;
        if (r9 != 0) goto L_0x05c3;
    L_0x042e:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x0525;
    L_0x0432:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x043e;
    L_0x0436:
        r1.drawScam = r2;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0442;
    L_0x043e:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0442:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x05c3;
    L_0x0446:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x04ba;
    L_0x044a:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x044f;
    L_0x044e:
        goto L_0x04ba;
    L_0x044f:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x046d;
    L_0x0455:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x0462;
    L_0x045b:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x0462;
    L_0x0461:
        goto L_0x046d;
    L_0x0462:
        r1.drawNameGroup = r2;
        r9 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x0477;
    L_0x046d:
        r1.drawNameBroadcast = r2;
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x0477:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0499;
    L_0x047b:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x048e;
    L_0x048b:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0490;
    L_0x048e:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0490:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0499:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04a9;
    L_0x04a6:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04ab;
    L_0x04a9:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04ab:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x04ba:
        r9 = r1.chat;
        r10 = r9.id;
        if (r10 < 0) goto L_0x04d8;
    L_0x04c0:
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x04cd;
    L_0x04c6:
        r9 = r1.chat;
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x04cd;
    L_0x04cc:
        goto L_0x04d8;
    L_0x04cd:
        r1.drawNameGroup = r2;
        r9 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        goto L_0x04e2;
    L_0x04d8:
        r1.drawNameBroadcast = r2;
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
    L_0x04e2:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0504;
    L_0x04e6:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x04f9;
    L_0x04f6:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x04fb;
    L_0x04f9:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x04fb:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0504:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = r1.drawNameGroup;
        if (r10 == 0) goto L_0x0514;
    L_0x0511:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0516;
    L_0x0514:
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0516:
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0525:
        r9 = r1.user;
        if (r9 == 0) goto L_0x05c3;
    L_0x0529:
        r10 = r9.scam;
        if (r10 == 0) goto L_0x0535;
    L_0x052d:
        r1.drawScam = r2;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r9.checkText();
        goto L_0x0539;
    L_0x0535:
        r9 = r9.verified;
        r1.drawVerified = r9;
    L_0x0539:
        r9 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r9 == 0) goto L_0x05c3;
    L_0x053d:
        r9 = r1.user;
        r9 = r9.bot;
        if (r9 == 0) goto L_0x05c3;
    L_0x0543:
        r1.drawNameBot = r2;
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x0589;
    L_0x0549:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x054e;
    L_0x054d:
        goto L_0x0589;
    L_0x054e:
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x0570;
    L_0x055a:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0570:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x0589:
        r9 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1.nameLockTop = r9;
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 != 0) goto L_0x05ab;
    L_0x0595:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.nameLockLeft = r9;
        r9 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 + r10;
        r1.nameLeft = r9;
        goto L_0x05c3;
    L_0x05ab:
        r9 = r42.getMeasuredWidth();
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r9 = r9 - r10;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r9 = r9 - r10;
        r1.nameLockLeft = r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r1.nameLeft = r9;
    L_0x05c3:
        r9 = r1.lastMessageDate;
        if (r9 != 0) goto L_0x05cf;
    L_0x05c7:
        r10 = r1.message;
        if (r10 == 0) goto L_0x05cf;
    L_0x05cb:
        r9 = r10.messageOwner;
        r9 = r9.date;
    L_0x05cf:
        r10 = r1.isDialogCell;
        if (r10 == 0) goto L_0x062c;
    L_0x05d3:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MediaDataController.getInstance(r10);
        r11 = r1.currentDialogId;
        r10 = r10.getDraft(r11);
        r1.draftMessage = r10;
        r10 = r1.draftMessage;
        if (r10 == 0) goto L_0x0600;
    L_0x05e5:
        r10 = r10.message;
        r10 = android.text.TextUtils.isEmpty(r10);
        if (r10 == 0) goto L_0x05f6;
    L_0x05ed:
        r10 = r1.draftMessage;
        r10 = r10.reply_to_msg_id;
        if (r10 == 0) goto L_0x05f4;
    L_0x05f3:
        goto L_0x05f6;
    L_0x05f4:
        r9 = 0;
        goto L_0x0627;
    L_0x05f6:
        r10 = r1.draftMessage;
        r10 = r10.date;
        if (r9 <= r10) goto L_0x0600;
    L_0x05fc:
        r9 = r1.unreadCount;
        if (r9 != 0) goto L_0x05f4;
    L_0x0600:
        r9 = r1.chat;
        r9 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r9 == 0) goto L_0x061a;
    L_0x0608:
        r9 = r1.chat;
        r10 = r9.megagroup;
        if (r10 != 0) goto L_0x061a;
    L_0x060e:
        r10 = r9.creator;
        if (r10 != 0) goto L_0x061a;
    L_0x0612:
        r9 = r9.admin_rights;
        if (r9 == 0) goto L_0x05f4;
    L_0x0616:
        r9 = r9.post_messages;
        if (r9 == 0) goto L_0x05f4;
    L_0x061a:
        r9 = r1.chat;
        if (r9 == 0) goto L_0x062a;
    L_0x061e:
        r10 = r9.left;
        if (r10 != 0) goto L_0x05f4;
    L_0x0622:
        r9 = r9.kicked;
        if (r9 == 0) goto L_0x062a;
    L_0x0626:
        goto L_0x05f4;
    L_0x0627:
        r1.draftMessage = r9;
        goto L_0x062f;
    L_0x062a:
        r9 = 0;
        goto L_0x062f;
    L_0x062c:
        r9 = 0;
        r1.draftMessage = r9;
    L_0x062f:
        if (r0 == 0) goto L_0x0641;
    L_0x0631:
        r1.lastPrintString = r0;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r7 = r1.paintIndex;
        r5 = r5[r7];
        r7 = r0;
        r10 = r5;
        r8 = r9;
        r0 = 1;
        r5 = 1;
    L_0x063e:
        r9 = 2;
        goto L_0x0b06;
    L_0x0641:
        r1.lastPrintString = r9;
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x06f3;
    L_0x0647:
        r0 = NUM; // 0x7f0e03e2 float:1.8877054E38 double:1.0531626477E-314;
        r8 = "Draft";
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        r8 = r1.draftMessage;
        r8 = r8.message;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0683;
    L_0x065a:
        r7 = r1.useForceThreeLines;
        if (r7 != 0) goto L_0x067d;
    L_0x065e:
        r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r7 == 0) goto L_0x0663;
    L_0x0662:
        goto L_0x067d;
    L_0x0663:
        r7 = android.text.SpannableStringBuilder.valueOf(r0);
        r8 = new android.text.style.ForegroundColorSpan;
        r9 = "chats_draft";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r8.<init>(r9);
        r9 = r0.length();
        r10 = 33;
        r7.setSpan(r8, r3, r9, r10);
    L_0x067b:
        r8 = r0;
        goto L_0x067f;
    L_0x067d:
        r8 = r0;
        r7 = r4;
    L_0x067f:
        r10 = r5;
        r0 = 1;
        r5 = 0;
        goto L_0x063e;
    L_0x0683:
        r8 = r1.draftMessage;
        r8 = r8.message;
        r9 = r8.length();
        if (r9 <= r14) goto L_0x0691;
    L_0x068d:
        r8 = r8.substring(r3, r14);
    L_0x0691:
        r9 = r1.useForceThreeLines;
        if (r9 != 0) goto L_0x06c7;
    L_0x0695:
        r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r9 == 0) goto L_0x069a;
    L_0x0699:
        goto L_0x06c7;
    L_0x069a:
        r9 = 2;
        r10 = new java.lang.Object[r9];
        r9 = 32;
        r11 = 10;
        r8 = r8.replace(r11, r9);
        r10[r3] = r8;
        r10[r2] = r0;
        r7 = java.lang.String.format(r7, r10);
        r7 = android.text.SpannableStringBuilder.valueOf(r7);
        r8 = new android.text.style.ForegroundColorSpan;
        r9 = "chats_draft";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r8.<init>(r9);
        r9 = r0.length();
        r9 = r9 + r2;
        r10 = 33;
        r7.setSpan(r8, r3, r9, r10);
        goto L_0x06de;
    L_0x06c7:
        r9 = 2;
        r10 = new java.lang.Object[r9];
        r9 = 32;
        r11 = 10;
        r8 = r8.replace(r11, r9);
        r10[r3] = r8;
        r10[r2] = r0;
        r7 = java.lang.String.format(r7, r10);
        r7 = android.text.SpannableStringBuilder.valueOf(r7);
    L_0x06de:
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r9 = r1.paintIndex;
        r8 = r8[r9];
        r8 = r8.getFontMetricsInt();
        r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r7 = org.telegram.messenger.Emoji.replaceEmoji(r7, r8, r10, r3);
        goto L_0x067b;
    L_0x06f3:
        r0 = r1.clearingDialog;
        if (r0 == 0) goto L_0x070d;
    L_0x06f7:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r5 = r1.paintIndex;
        r5 = r0[r5];
        r0 = NUM; // 0x7f0e0558 float:1.8877812E38 double:1.0531628325E-314;
        r7 = "HistoryCleared";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
    L_0x0706:
        r7 = r0;
    L_0x0707:
        r10 = r5;
        r0 = 1;
        r5 = 1;
        r8 = 0;
        goto L_0x063e;
    L_0x070d:
        r0 = r1.message;
        if (r0 != 0) goto L_0x0781;
    L_0x0711:
        r0 = r1.encryptedChat;
        if (r0 == 0) goto L_0x077f;
    L_0x0715:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r7 = r1.paintIndex;
        r5 = r5[r7];
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
        if (r7 == 0) goto L_0x0729;
    L_0x071f:
        r0 = NUM; // 0x7f0e0431 float:1.8877214E38 double:1.053162687E-314;
        r7 = "EncryptionProcessing";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
        goto L_0x0706;
    L_0x0729:
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
        if (r7 == 0) goto L_0x0741;
    L_0x072d:
        r0 = NUM; // 0x7f0e01b7 float:1.8875928E38 double:1.0531623735E-314;
        r7 = new java.lang.Object[r2];
        r8 = r1.user;
        r8 = org.telegram.messenger.UserObject.getFirstName(r8);
        r7[r3] = r8;
        r8 = "AwaitingEncryption";
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r0, r7);
        goto L_0x0706;
    L_0x0741:
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
        if (r7 == 0) goto L_0x074f;
    L_0x0745:
        r0 = NUM; // 0x7f0e0432 float:1.8877216E38 double:1.0531626873E-314;
        r7 = "EncryptionRejected";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
        goto L_0x0706;
    L_0x074f:
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r7 == 0) goto L_0x077f;
    L_0x0753:
        r0 = r0.admin_id;
        r7 = r1.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7 = r7.getClientUserId();
        if (r0 != r7) goto L_0x0775;
    L_0x0761:
        r0 = NUM; // 0x7f0e0426 float:1.8877192E38 double:1.0531626813E-314;
        r7 = new java.lang.Object[r2];
        r8 = r1.user;
        r8 = org.telegram.messenger.UserObject.getFirstName(r8);
        r7[r3] = r8;
        r8 = "EncryptedChatStartedOutgoing";
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r0, r7);
        goto L_0x0706;
    L_0x0775:
        r0 = NUM; // 0x7f0e0425 float:1.887719E38 double:1.053162681E-314;
        r7 = "EncryptedChatStartedIncoming";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
        goto L_0x0706;
    L_0x077f:
        r7 = r4;
        goto L_0x0707;
    L_0x0781:
        r0 = r0.isFromUser();
        if (r0 == 0) goto L_0x079e;
    L_0x0787:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r9 = r1.message;
        r9 = r9.messageOwner;
        r9 = r9.from_id;
        r9 = java.lang.Integer.valueOf(r9);
        r0 = r0.getUser(r9);
        r9 = r0;
        r0 = 0;
        goto L_0x07b5;
    L_0x079e:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r9 = r1.message;
        r9 = r9.messageOwner;
        r9 = r9.to_id;
        r9 = r9.channel_id;
        r9 = java.lang.Integer.valueOf(r9);
        r0 = r0.getChat(r9);
        r9 = 0;
    L_0x07b5:
        r10 = r1.dialogsType;
        r11 = 3;
        if (r10 != r11) goto L_0x07d4;
    L_0x07ba:
        r10 = r1.user;
        r10 = org.telegram.messenger.UserObject.isUserSelf(r10);
        if (r10 == 0) goto L_0x07d4;
    L_0x07c2:
        r0 = NUM; // 0x7f0e09a8 float:1.8880051E38 double:1.053163378E-314;
        r6 = "SavedMessagesInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
        r7 = r0;
        r10 = r5;
        r0 = 0;
        r5 = 1;
        r6 = 0;
    L_0x07d0:
        r8 = 0;
    L_0x07d1:
        r9 = 2;
        goto L_0x0afe;
    L_0x07d4:
        r10 = r1.useForceThreeLines;
        if (r10 != 0) goto L_0x07e9;
    L_0x07d8:
        r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r10 != 0) goto L_0x07e9;
    L_0x07dc:
        r10 = r1.currentDialogFolderId;
        if (r10 == 0) goto L_0x07e9;
    L_0x07e0:
        r0 = r42.formatArchivedDialogNames();
        r7 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 0;
        goto L_0x07d0;
    L_0x07e9:
        r10 = r1.message;
        r11 = r10.messageOwner;
        r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r11 == 0) goto L_0x0819;
    L_0x07f1:
        r0 = r1.chat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x080a;
    L_0x07f9:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r5 != 0) goto L_0x0807;
    L_0x0803:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r0 == 0) goto L_0x080a;
    L_0x0807:
        r0 = r4;
        r6 = 0;
        goto L_0x080e;
    L_0x080a:
        r0 = r1.message;
        r0 = r0.messageText;
    L_0x080e:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r7 = r1.paintIndex;
        r5 = r5[r7];
    L_0x0814:
        r7 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 1;
        goto L_0x07d0;
    L_0x0819:
        r11 = r1.chat;
        if (r11 == 0) goto L_0x0a13;
    L_0x081d:
        r11 = r11.id;
        if (r11 <= 0) goto L_0x0a13;
    L_0x0821:
        if (r0 != 0) goto L_0x0a13;
    L_0x0823:
        r10 = r10.isOutOwner();
        if (r10 == 0) goto L_0x0834;
    L_0x0829:
        r0 = NUM; // 0x7f0e051a float:1.8877686E38 double:1.053162802E-314;
        r9 = "FromYou";
        r0 = org.telegram.messenger.LocaleController.getString(r9, r0);
    L_0x0832:
        r9 = r0;
        goto L_0x0877;
    L_0x0834:
        if (r9 == 0) goto L_0x0869;
    L_0x0836:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x084a;
    L_0x083a:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x083f;
    L_0x083e:
        goto L_0x084a;
    L_0x083f:
        r0 = org.telegram.messenger.UserObject.getFirstName(r9);
        r9 = "\n";
        r0 = r0.replace(r9, r4);
        goto L_0x0832;
    L_0x084a:
        r0 = org.telegram.messenger.UserObject.isDeleted(r9);
        if (r0 == 0) goto L_0x085a;
    L_0x0850:
        r0 = NUM; // 0x7f0e0554 float:1.8877804E38 double:1.0531628305E-314;
        r9 = "HiddenName";
        r0 = org.telegram.messenger.LocaleController.getString(r9, r0);
        goto L_0x0832;
    L_0x085a:
        r0 = r9.first_name;
        r9 = r9.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r9);
        r9 = "\n";
        r0 = r0.replace(r9, r4);
        goto L_0x0832;
    L_0x0869:
        if (r0 == 0) goto L_0x0874;
    L_0x086b:
        r0 = r0.title;
        r9 = "\n";
        r0 = r0.replace(r9, r4);
        goto L_0x0832;
    L_0x0874:
        r0 = "DELETED";
        goto L_0x0832;
    L_0x0877:
        r0 = r1.message;
        r10 = r0.caption;
        if (r10 == 0) goto L_0x08e7;
    L_0x087d:
        r0 = r10.toString();
        r8 = r0.length();
        if (r8 <= r14) goto L_0x088b;
    L_0x0887:
        r0 = r0.substring(r3, r14);
    L_0x088b:
        r8 = r1.message;
        r8 = r8.isVideo();
        if (r8 == 0) goto L_0x0898;
    L_0x0893:
        r8 = "📹 ";
    L_0x0896:
        r10 = 2;
        goto L_0x08c0;
    L_0x0898:
        r8 = r1.message;
        r8 = r8.isVoice();
        if (r8 == 0) goto L_0x08a4;
    L_0x08a0:
        r8 = "🎤 ";
        goto L_0x0896;
    L_0x08a4:
        r8 = r1.message;
        r8 = r8.isMusic();
        if (r8 == 0) goto L_0x08b0;
    L_0x08ac:
        r8 = "🎧 ";
        goto L_0x0896;
    L_0x08b0:
        r8 = r1.message;
        r8 = r8.isPhoto();
        if (r8 == 0) goto L_0x08bc;
    L_0x08b8:
        r8 = "🖼 ";
        goto L_0x0896;
    L_0x08bc:
        r8 = "📎 ";
        goto L_0x0896;
    L_0x08c0:
        r11 = new java.lang.Object[r10];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r8);
        r8 = 32;
        r12 = 10;
        r0 = r0.replace(r12, r8);
        r10.append(r0);
        r0 = r10.toString();
        r11[r3] = r0;
        r11[r2] = r9;
        r0 = java.lang.String.format(r7, r11);
        r0 = android.text.SpannableStringBuilder.valueOf(r0);
        goto L_0x09cb;
    L_0x08e7:
        r10 = r0.messageOwner;
        r10 = r10.media;
        if (r10 == 0) goto L_0x099d;
    L_0x08ed:
        r0 = r0.isMediaEmpty();
        if (r0 != 0) goto L_0x099d;
    L_0x08f3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r5 = r1.paintIndex;
        r5 = r0[r5];
        r0 = r1.message;
        r10 = r0.messageOwner;
        r10 = r10.media;
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r11 == 0) goto L_0x092a;
    L_0x0903:
        r0 = android.os.Build.VERSION.SDK_INT;
        r11 = 18;
        if (r0 < r11) goto L_0x0919;
    L_0x0909:
        r0 = new java.lang.Object[r2];
        r10 = r10.game;
        r10 = r10.title;
        r0[r3] = r10;
        r10 = "🎮 ⁨%s⁩";
        r0 = java.lang.String.format(r10, r0);
        goto L_0x0928;
    L_0x0919:
        r0 = new java.lang.Object[r2];
        r10 = r10.game;
        r10 = r10.title;
        r0[r3] = r10;
        r10 = "🎮 %s";
        r0 = java.lang.String.format(r10, r0);
    L_0x0928:
        r10 = 2;
        goto L_0x096b;
    L_0x092a:
        r10 = r0.type;
        r11 = 14;
        if (r10 != r11) goto L_0x0968;
    L_0x0930:
        r10 = android.os.Build.VERSION.SDK_INT;
        r11 = 18;
        if (r10 < r11) goto L_0x094f;
    L_0x0936:
        r10 = 2;
        r11 = new java.lang.Object[r10];
        r0 = r0.getMusicAuthor();
        r11[r3] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r11[r2] = r0;
        r0 = "🎧 ⁨%s - %s⁩";
        r0 = java.lang.String.format(r0, r11);
        goto L_0x096b;
    L_0x094f:
        r10 = 2;
        r11 = new java.lang.Object[r10];
        r0 = r0.getMusicAuthor();
        r11[r3] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r11[r2] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r11);
        goto L_0x096b;
    L_0x0968:
        r10 = 2;
        r0 = r0.messageText;
    L_0x096b:
        r11 = new java.lang.Object[r10];
        r11[r3] = r0;
        r11[r2] = r9;
        r0 = java.lang.String.format(r7, r11);
        r7 = android.text.SpannableStringBuilder.valueOf(r0);
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0998 }
        r10 = "chats_attachMessage";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);	 Catch:{ Exception -> 0x0998 }
        r0.<init>(r10);	 Catch:{ Exception -> 0x0998 }
        if (r8 == 0) goto L_0x098d;
    L_0x0986:
        r8 = r9.length();	 Catch:{ Exception -> 0x0998 }
        r10 = 2;
        r8 = r8 + r10;
        goto L_0x098e;
    L_0x098d:
        r8 = 0;
    L_0x098e:
        r10 = r7.length();	 Catch:{ Exception -> 0x0998 }
        r11 = 33;
        r7.setSpan(r0, r8, r10, r11);	 Catch:{ Exception -> 0x0998 }
        goto L_0x09cc;
    L_0x0998:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x09cc;
    L_0x099d:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.message;
        if (r0 == 0) goto L_0x09c7;
    L_0x09a5:
        r8 = r0.length();
        if (r8 <= r14) goto L_0x09af;
    L_0x09ab:
        r0 = r0.substring(r3, r14);
    L_0x09af:
        r8 = 2;
        r10 = new java.lang.Object[r8];
        r8 = 32;
        r11 = 10;
        r0 = r0.replace(r11, r8);
        r10[r3] = r0;
        r10[r2] = r9;
        r0 = java.lang.String.format(r7, r10);
        r0 = android.text.SpannableStringBuilder.valueOf(r0);
        goto L_0x09cb;
    L_0x09c7:
        r0 = android.text.SpannableStringBuilder.valueOf(r4);
    L_0x09cb:
        r7 = r0;
    L_0x09cc:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x09d4;
    L_0x09d0:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x09de;
    L_0x09d4:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x09f8;
    L_0x09d8:
        r0 = r7.length();
        if (r0 <= 0) goto L_0x09f8;
    L_0x09de:
        r0 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x09f4 }
        r8 = "chats_nameMessage";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);	 Catch:{ Exception -> 0x09f4 }
        r0.<init>(r8);	 Catch:{ Exception -> 0x09f4 }
        r8 = r9.length();	 Catch:{ Exception -> 0x09f4 }
        r8 = r8 + r2;
        r10 = 33;
        r7.setSpan(r0, r3, r8, r10);	 Catch:{ Exception -> 0x09f4 }
        goto L_0x09f8;
    L_0x09f4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x09f8:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r8 = r1.paintIndex;
        r0 = r0[r8];
        r0 = r0.getFontMetricsInt();
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r7, r0, r10, r3);
        r7 = r0;
        r10 = r5;
        r8 = r9;
        r0 = 1;
        r5 = 0;
        goto L_0x07d1;
    L_0x0a13:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r7 == 0) goto L_0x0a32;
    L_0x0a1d:
        r7 = r0.photo;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r7 == 0) goto L_0x0a32;
    L_0x0a23:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a32;
    L_0x0a27:
        r0 = NUM; // 0x7f0e015b float:1.8875741E38 double:1.053162328E-314;
        r7 = "AttachPhotoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
        goto L_0x0814;
    L_0x0a32:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r7 == 0) goto L_0x0a51;
    L_0x0a3c:
        r7 = r0.document;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;
        if (r7 == 0) goto L_0x0a51;
    L_0x0a42:
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a51;
    L_0x0a46:
        r0 = NUM; // 0x7f0e0161 float:1.8875754E38 double:1.053162331E-314;
        r7 = "AttachVideoExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r7, r0);
        goto L_0x0814;
    L_0x0a51:
        r0 = r1.message;
        r7 = r0.caption;
        if (r7 == 0) goto L_0x0a9d;
    L_0x0a57:
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x0a61;
    L_0x0a5d:
        r0 = "📹 ";
        goto L_0x0a88;
    L_0x0a61:
        r0 = r1.message;
        r0 = r0.isVoice();
        if (r0 == 0) goto L_0x0a6d;
    L_0x0a69:
        r0 = "🎤 ";
        goto L_0x0a88;
    L_0x0a6d:
        r0 = r1.message;
        r0 = r0.isMusic();
        if (r0 == 0) goto L_0x0a79;
    L_0x0a75:
        r0 = "🎧 ";
        goto L_0x0a88;
    L_0x0a79:
        r0 = r1.message;
        r0 = r0.isPhoto();
        if (r0 == 0) goto L_0x0a85;
    L_0x0a81:
        r0 = "🖼 ";
        goto L_0x0a88;
    L_0x0a85:
        r0 = "📎 ";
    L_0x0a88:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r0);
        r0 = r1.message;
        r0 = r0.caption;
        r7.append(r0);
        r0 = r7.toString();
        goto L_0x0814;
    L_0x0a9d:
        r7 = r0.messageOwner;
        r7 = r7.media;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r7 == 0) goto L_0x0ac3;
    L_0x0aa5:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r7 = "🎮 ";
        r0.append(r7);
        r7 = r1.message;
        r7 = r7.messageOwner;
        r7 = r7.media;
        r7 = r7.game;
        r7 = r7.title;
        r0.append(r7);
        r0 = r0.toString();
        r9 = 2;
        goto L_0x0ae5;
    L_0x0ac3:
        r7 = r0.type;
        r8 = 14;
        if (r7 != r8) goto L_0x0ae2;
    L_0x0ac9:
        r9 = 2;
        r7 = new java.lang.Object[r9];
        r0 = r0.getMusicAuthor();
        r7[r3] = r0;
        r0 = r1.message;
        r0 = r0.getMusicTitle();
        r7[r2] = r0;
        r0 = "🎧 %s - %s";
        r0 = java.lang.String.format(r0, r7);
        goto L_0x0ae5;
    L_0x0ae2:
        r9 = 2;
        r0 = r0.messageText;
    L_0x0ae5:
        r7 = r1.message;
        r8 = r7.messageOwner;
        r8 = r8.media;
        if (r8 == 0) goto L_0x0af9;
    L_0x0aed:
        r7 = r7.isMediaEmpty();
        if (r7 != 0) goto L_0x0af9;
    L_0x0af3:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint;
        r7 = r1.paintIndex;
        r5 = r5[r7];
    L_0x0af9:
        r7 = r0;
        r10 = r5;
        r0 = 1;
        r5 = 1;
        r8 = 0;
    L_0x0afe:
        r11 = r1.currentDialogFolderId;
        if (r11 == 0) goto L_0x0b06;
    L_0x0b02:
        r8 = r42.formatArchivedDialogNames();
    L_0x0b06:
        r11 = r1.draftMessage;
        if (r11 == 0) goto L_0x0b12;
    L_0x0b0a:
        r11 = r11.date;
        r11 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11);
        goto L_0x0b2b;
    L_0x0b12:
        r11 = r1.lastMessageDate;
        if (r11 == 0) goto L_0x0b1c;
    L_0x0b16:
        r11 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11);
        goto L_0x0b2b;
    L_0x0b1c:
        r11 = r1.message;
        if (r11 == 0) goto L_0x0b2a;
    L_0x0b20:
        r11 = r11.messageOwner;
        r11 = r11.date;
        r11 = (long) r11;
        r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11);
        goto L_0x0b2b;
    L_0x0b2a:
        r11 = r4;
    L_0x0b2b:
        r12 = r1.message;
        if (r12 != 0) goto L_0x0b3f;
    L_0x0b2f:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawCount = r3;
        r1.drawMention = r3;
        r1.drawError = r3;
        r9 = 0;
        r12 = 0;
        goto L_0x0CLASSNAME;
    L_0x0b3f:
        r9 = r1.currentDialogFolderId;
        if (r9 == 0) goto L_0x0b7e;
    L_0x0b43:
        r9 = r1.unreadCount;
        r12 = r1.mentionCount;
        r18 = r9 + r12;
        if (r18 <= 0) goto L_0x0b77;
    L_0x0b4b:
        if (r9 <= r12) goto L_0x0b61;
    L_0x0b4d:
        r1.drawCount = r2;
        r1.drawMention = r3;
        r14 = new java.lang.Object[r2];
        r9 = r9 + r12;
        r9 = java.lang.Integer.valueOf(r9);
        r14[r3] = r9;
        r9 = "%d";
        r9 = java.lang.String.format(r9, r14);
        goto L_0x0b7c;
    L_0x0b61:
        r1.drawCount = r3;
        r1.drawMention = r2;
        r14 = new java.lang.Object[r2];
        r9 = r9 + r12;
        r9 = java.lang.Integer.valueOf(r9);
        r14[r3] = r9;
        r9 = "%d";
        r9 = java.lang.String.format(r9, r14);
        r12 = r9;
        r9 = 0;
        goto L_0x0bc3;
    L_0x0b77:
        r1.drawCount = r3;
        r1.drawMention = r3;
        r9 = 0;
    L_0x0b7c:
        r12 = 0;
        goto L_0x0bc3;
    L_0x0b7e:
        r9 = r1.clearingDialog;
        if (r9 == 0) goto L_0x0b87;
    L_0x0b82:
        r1.drawCount = r3;
        r6 = 0;
    L_0x0b85:
        r9 = 0;
        goto L_0x0bb7;
    L_0x0b87:
        r9 = r1.unreadCount;
        if (r9 == 0) goto L_0x0bac;
    L_0x0b8b:
        if (r9 != r2) goto L_0x0b99;
    L_0x0b8d:
        r14 = r1.mentionCount;
        if (r9 != r14) goto L_0x0b99;
    L_0x0b91:
        if (r12 == 0) goto L_0x0b99;
    L_0x0b93:
        r9 = r12.messageOwner;
        r9 = r9.mentioned;
        if (r9 != 0) goto L_0x0bac;
    L_0x0b99:
        r1.drawCount = r2;
        r9 = new java.lang.Object[r2];
        r12 = r1.unreadCount;
        r12 = java.lang.Integer.valueOf(r12);
        r9[r3] = r12;
        r12 = "%d";
        r9 = java.lang.String.format(r12, r9);
        goto L_0x0bb7;
    L_0x0bac:
        r9 = r1.markUnread;
        if (r9 == 0) goto L_0x0bb4;
    L_0x0bb0:
        r1.drawCount = r2;
        r9 = r4;
        goto L_0x0bb7;
    L_0x0bb4:
        r1.drawCount = r3;
        goto L_0x0b85;
    L_0x0bb7:
        r12 = r1.mentionCount;
        if (r12 == 0) goto L_0x0bc0;
    L_0x0bbb:
        r1.drawMention = r2;
        r12 = "@";
        goto L_0x0bc3;
    L_0x0bc0:
        r1.drawMention = r3;
        goto L_0x0b7c;
    L_0x0bc3:
        r14 = r1.message;
        r14 = r14.isOut();
        if (r14 == 0) goto L_0x0c2a;
    L_0x0bcb:
        r14 = r1.draftMessage;
        if (r14 != 0) goto L_0x0c2a;
    L_0x0bcf:
        if (r6 == 0) goto L_0x0c2a;
    L_0x0bd1:
        r6 = r1.message;
        r14 = r6.messageOwner;
        r14 = r14.action;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
        if (r14 != 0) goto L_0x0c2a;
    L_0x0bdb:
        r6 = r6.isSending();
        if (r6 == 0) goto L_0x0bea;
    L_0x0be1:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r2;
        r1.drawError = r3;
        goto L_0x0CLASSNAME;
    L_0x0bea:
        r6 = r1.message;
        r6 = r6.isSendError();
        if (r6 == 0) goto L_0x0bff;
    L_0x0bf2:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r2;
        r1.drawCount = r3;
        r1.drawMention = r3;
        goto L_0x0CLASSNAME;
    L_0x0bff:
        r6 = r1.message;
        r6 = r6.isSent();
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r1.message;
        r6 = r6.isUnread();
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0c0f:
        r6 = r1.chat;
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x0c1e;
    L_0x0CLASSNAME:
        r6 = r1.chat;
        r6 = r6.megagroup;
        if (r6 != 0) goto L_0x0c1e;
    L_0x0c1d:
        goto L_0x0CLASSNAME;
    L_0x0c1e:
        r6 = 0;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = 1;
    L_0x0CLASSNAME:
        r1.drawCheck1 = r6;
        r1.drawCheck2 = r2;
        r1.drawClock = r3;
        r1.drawError = r3;
        goto L_0x0CLASSNAME;
    L_0x0c2a:
        r1.drawCheck1 = r3;
        r1.drawCheck2 = r3;
        r1.drawClock = r3;
        r1.drawError = r3;
    L_0x0CLASSNAME:
        r6 = r1.dialogsType;
        if (r6 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r1.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r13 = r1.currentDialogId;
        r6 = r6.isProxyDialog(r13, r2);
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.drawPinBackground = r2;
        r6 = NUM; // 0x7f0e0b6c float:1.8880968E38 double:1.0531636013E-314;
        r11 = "UseProxySponsor";
        r6 = org.telegram.messenger.LocaleController.getString(r11, r6);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r11;
    L_0x0CLASSNAME:
        r11 = r1.currentDialogFolderId;
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = NUM; // 0x7f0e010a float:1.8875577E38 double:1.053162288E-314;
        r13 = "ArchivedChats";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
    L_0x0c5e:
        r28 = r5;
        r29 = r7;
        r40 = r8;
        r14 = r9;
        r13 = r11;
        r11 = r10;
        goto L_0x0cb0;
    L_0x0CLASSNAME:
        r11 = r1.chat;
        if (r11 == 0) goto L_0x0c6f;
    L_0x0c6c:
        r11 = r11.title;
        goto L_0x0ca0;
    L_0x0c6f:
        r11 = r1.user;
        if (r11 == 0) goto L_0x0c9f;
    L_0x0CLASSNAME:
        r11 = org.telegram.messenger.UserObject.isUserSelf(r11);
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r11 = r1.useMeForMyMessages;
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0c7d:
        r11 = NUM; // 0x7f0e051a float:1.8877686E38 double:1.053162802E-314;
        r13 = "FromYou";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r11 = r1.dialogsType;
        r13 = 3;
        if (r11 != r13) goto L_0x0c8e;
    L_0x0c8c:
        r1.drawPinBackground = r2;
    L_0x0c8e:
        r11 = NUM; // 0x7f0e09a7 float:1.888005E38 double:1.0531633775E-314;
        r13 = "SavedMessages";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r11 = r1.user;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        goto L_0x0ca0;
    L_0x0c9f:
        r11 = r4;
    L_0x0ca0:
        r13 = r11.length();
        if (r13 != 0) goto L_0x0c5e;
    L_0x0ca6:
        r11 = NUM; // 0x7f0e0554 float:1.8877804E38 double:1.0531628305E-314;
        r13 = "HiddenName";
        r11 = org.telegram.messenger.LocaleController.getString(r13, r11);
        goto L_0x0c5e;
    L_0x0cb0:
        if (r0 == 0) goto L_0x0cf7;
    L_0x0cb2:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r0 = r0.measureText(r6);
        r7 = (double) r0;
        r7 = java.lang.Math.ceil(r7);
        r0 = (int) r7;
        r10 = new android.text.StaticLayout;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint;
        r9 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r20 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r21 = 0;
        r22 = 0;
        r5 = r10;
        r8 = r0;
        r2 = r10;
        r10 = r20;
        r41 = r11;
        r11 = r21;
        r15 = r12;
        r12 = r22;
        r5.<init>(r6, r7, r8, r9, r10, r11, r12);
        r1.timeLayout = r2;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0cee;
    L_0x0cdf:
        r2 = r42.getMeasuredWidth();
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r2 = r2 - r0;
        r1.timeLeft = r2;
        goto L_0x0d00;
    L_0x0cee:
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.timeLeft = r2;
        goto L_0x0d00;
    L_0x0cf7:
        r41 = r11;
        r15 = r12;
        r2 = 0;
        r1.timeLayout = r2;
        r1.timeLeft = r3;
        r0 = 0;
    L_0x0d00:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0d14;
    L_0x0d04:
        r2 = r42.getMeasuredWidth();
        r5 = r1.nameLeft;
        r2 = r2 - r5;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r2 = r2 - r0;
        goto L_0x0d28;
    L_0x0d14:
        r2 = r42.getMeasuredWidth();
        r5 = r1.nameLeft;
        r2 = r2 - r5;
        r5 = NUM; // 0x429a0000 float:77.0 double:5.52063419E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r2 = r2 - r0;
        r5 = r1.nameLeft;
        r5 = r5 + r0;
        r1.nameLeft = r5;
    L_0x0d28:
        r5 = r1.drawNameLock;
        if (r5 == 0) goto L_0x0d3b;
    L_0x0d2c:
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
    L_0x0d38:
        r5 = r5 + r6;
        r2 = r2 - r5;
        goto L_0x0d6e;
    L_0x0d3b:
        r5 = r1.drawNameGroup;
        if (r5 == 0) goto L_0x0d4c;
    L_0x0d3f:
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0d38;
    L_0x0d4c:
        r5 = r1.drawNameBroadcast;
        if (r5 == 0) goto L_0x0d5d;
    L_0x0d50:
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0d38;
    L_0x0d5d:
        r5 = r1.drawNameBot;
        if (r5 == 0) goto L_0x0d6e;
    L_0x0d61:
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0d38;
    L_0x0d6e:
        r5 = r1.drawClock;
        if (r5 == 0) goto L_0x0d9e;
    L_0x0d72:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r5 = r5.getIntrinsicWidth();
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r2 = r2 - r5;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x0d8b;
    L_0x0d84:
        r0 = r1.timeLeft;
        r0 = r0 - r5;
        r1.checkDrawLeft = r0;
        goto L_0x0e1d;
    L_0x0d8b:
        r6 = r1.timeLeft;
        r6 = r6 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r6 + r0;
        r1.checkDrawLeft = r6;
        r0 = r1.nameLeft;
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0e1d;
    L_0x0d9e:
        r5 = r1.drawCheck2;
        if (r5 == 0) goto L_0x0e1d;
    L_0x0da2:
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r5.getIntrinsicWidth();
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r2 = r2 - r5;
        r6 = r1.drawCheck1;
        if (r6 == 0) goto L_0x0e02;
    L_0x0db4:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r6 = r6.getIntrinsicWidth();
        r7 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r2 = r2 - r6;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x0dd7;
    L_0x0dc6:
        r0 = r1.timeLeft;
        r0 = r0 - r5;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.halfCheckDrawLeft;
        r5 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = r0 - r5;
        r1.checkDrawLeft = r0;
        goto L_0x0e1d;
    L_0x0dd7:
        r6 = r1.timeLeft;
        r6 = r6 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r6 + r0;
        r1.checkDrawLeft = r6;
        r0 = r1.checkDrawLeft;
        r6 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r0 + r6;
        r1.halfCheckDrawLeft = r0;
        r0 = r1.nameLeft;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable;
        r6 = r6.getIntrinsicWidth();
        r5 = r5 + r6;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r0 = r0 + r5;
        r1.nameLeft = r0;
        goto L_0x0e1d;
    L_0x0e02:
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x0e0c;
    L_0x0e06:
        r0 = r1.timeLeft;
        r0 = r0 - r5;
        r1.checkDrawLeft = r0;
        goto L_0x0e1d;
    L_0x0e0c:
        r6 = r1.timeLeft;
        r6 = r6 + r0;
        r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = r6 + r0;
        r1.checkDrawLeft = r6;
        r0 = r1.nameLeft;
        r0 = r0 + r5;
        r1.nameLeft = r0;
    L_0x0e1d:
        r0 = r1.dialogMuted;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r0 == 0) goto L_0x0e41;
    L_0x0e23:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x0e41;
    L_0x0e27:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x0e41;
    L_0x0e2b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r2 = r2 - r0;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x0e74;
    L_0x0e3b:
        r6 = r1.nameLeft;
        r6 = r6 + r0;
        r1.nameLeft = r6;
        goto L_0x0e74;
    L_0x0e41:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x0e5b;
    L_0x0e45:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r2 = r2 - r0;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x0e74;
    L_0x0e55:
        r6 = r1.nameLeft;
        r6 = r6 + r0;
        r1.nameLeft = r6;
        goto L_0x0e74;
    L_0x0e5b:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x0e74;
    L_0x0e5f:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r6 = r6.getIntrinsicWidth();
        r0 = r0 + r6;
        r2 = r2 - r0;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x0e74;
    L_0x0e6f:
        r6 = r1.nameLeft;
        r6 = r6 + r0;
        r1.nameLeft = r6;
    L_0x0e74:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = java.lang.Math.max(r0, r2);
        r7 = 32;
        r8 = 10;
        r0 = r13.replace(r8, r7);	 Catch:{ Exception -> 0x0eb3 }
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0eb3 }
        r8 = r1.paintIndex;	 Catch:{ Exception -> 0x0eb3 }
        r7 = r7[r8];	 Catch:{ Exception -> 0x0eb3 }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0eb3 }
        r8 = r2 - r8;
        r8 = (float) r8;	 Catch:{ Exception -> 0x0eb3 }
        r9 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0eb3 }
        r21 = android.text.TextUtils.ellipsize(r0, r7, r8, r9);	 Catch:{ Exception -> 0x0eb3 }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0eb3 }
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;	 Catch:{ Exception -> 0x0eb3 }
        r8 = r1.paintIndex;	 Catch:{ Exception -> 0x0eb3 }
        r22 = r7[r8];	 Catch:{ Exception -> 0x0eb3 }
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0eb3 }
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20 = r0;
        r23 = r2;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);	 Catch:{ Exception -> 0x0eb3 }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x0eb3 }
        goto L_0x0eb7;
    L_0x0eb3:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0eb7:
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x0f3d;
    L_0x0ebb:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x0ec1;
    L_0x0ebf:
        goto L_0x0f3d;
    L_0x0ec1:
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
        r7 = r42.getMeasuredWidth();
        r8 = NUM; // 0x42be0000 float:95.0 double:5.53229066E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 - r8;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0var_;
    L_0x0var_:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1.messageNameLeft = r8;
        r1.messageLeft = r8;
        r8 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x0f2a;
    L_0x0var_:
        r8 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.messageNameLeft = r8;
        r1.messageLeft = r8;
        r8 = r42.getMeasuredWidth();
        r9 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
    L_0x0f2a:
        r9 = r1.avatarImage;
        r10 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r9.setImageCoords(r8, r0, r10, r11);
        goto L_0x0fb7;
    L_0x0f3d:
        r0 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.messageNameTop = r7;
        r7 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.timeTop = r7;
        r7 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.errorTop = r7;
        r7 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.pinTop = r7;
        r7 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.countTop = r7;
        r7 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.checkDrawTop = r7;
        r7 = r42.getMeasuredWidth();
        r8 = NUM; // 0x42ba0000 float:93.0 double:5.5309955E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 - r8;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0var_;
    L_0x0var_:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1.messageNameLeft = r8;
        r1.messageLeft = r8;
        r8 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x0fa6;
    L_0x0var_:
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.messageNameLeft = r8;
        r1.messageLeft = r8;
        r8 = r42.getMeasuredWidth();
        r9 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
    L_0x0fa6:
        r9 = r1.avatarImage;
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r9.setImageCoords(r8, r0, r10, r11);
    L_0x0fb7:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0fdc;
    L_0x0fbb:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0fd4;
    L_0x0fbf:
        r0 = r42.getMeasuredWidth();
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r8 = r8.getIntrinsicWidth();
        r0 = r0 - r8;
        r8 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 - r8;
        r1.pinLeft = r0;
        goto L_0x0fdc;
    L_0x0fd4:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.pinLeft = r0;
    L_0x0fdc:
        r0 = r1.drawError;
        if (r0 == 0) goto L_0x100e;
    L_0x0fe0:
        r0 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = r7 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x0ffa;
    L_0x0feb:
        r0 = r42.getMeasuredWidth();
        r8 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 - r8;
        r1.errorLeft = r0;
        goto L_0x112d;
    L_0x0ffa:
        r8 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.errorLeft = r8;
        r8 = r1.messageLeft;
        r8 = r8 + r0;
        r1.messageLeft = r8;
        r8 = r1.messageNameLeft;
        r8 = r8 + r0;
        r1.messageNameLeft = r8;
        goto L_0x112d;
    L_0x100e:
        if (r14 != 0) goto L_0x1039;
    L_0x1010:
        if (r15 == 0) goto L_0x1013;
    L_0x1012:
        goto L_0x1039;
    L_0x1013:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x1033;
    L_0x1017:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable;
        r0 = r0.getIntrinsicWidth();
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r8;
        r7 = r7 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x1033;
    L_0x1029:
        r8 = r1.messageLeft;
        r8 = r8 + r0;
        r1.messageLeft = r8;
        r8 = r1.messageNameLeft;
        r8 = r8 + r0;
        r1.messageNameLeft = r8;
    L_0x1033:
        r1.drawCount = r3;
        r1.drawMention = r3;
        goto L_0x112d;
    L_0x1039:
        if (r14 == 0) goto L_0x109f;
    L_0x103b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r8.measureText(r14);
        r8 = (double) r8;
        r8 = java.lang.Math.ceil(r8);
        r8 = (int) r8;
        r0 = java.lang.Math.max(r0, r8);
        r1.countWidth = r0;
        r0 = new android.text.StaticLayout;
        r22 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r1.countWidth;
        r24 = android.text.Layout.Alignment.ALIGN_CENTER;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20 = r0;
        r21 = r14;
        r23 = r8;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r1.countLayout = r0;
        r0 = r1.countWidth;
        r8 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r9;
        r7 = r7 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x1089;
    L_0x1078:
        r0 = r42.getMeasuredWidth();
        r8 = r1.countWidth;
        r0 = r0 - r8;
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 - r9;
        r1.countLeft = r0;
        goto L_0x109b;
    L_0x1089:
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.countLeft = r9;
        r8 = r1.messageLeft;
        r8 = r8 + r0;
        r1.messageLeft = r8;
        r8 = r1.messageNameLeft;
        r8 = r8 + r0;
        r1.messageNameLeft = r8;
    L_0x109b:
        r8 = 1;
        r1.drawCount = r8;
        goto L_0x10a1;
    L_0x109f:
        r1.countWidth = r3;
    L_0x10a1:
        if (r15 == 0) goto L_0x112d;
    L_0x10a3:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x10d7;
    L_0x10a7:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r8.measureText(r15);
        r8 = (double) r8;
        r8 = java.lang.Math.ceil(r8);
        r8 = (int) r8;
        r0 = java.lang.Math.max(r0, r8);
        r1.mentionWidth = r0;
        r0 = new android.text.StaticLayout;
        r22 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r1.mentionWidth;
        r24 = android.text.Layout.Alignment.ALIGN_CENTER;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20 = r0;
        r21 = r15;
        r23 = r8;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r1.mentionLayout = r0;
        goto L_0x10dd;
    L_0x10d7:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.mentionWidth = r0;
    L_0x10dd:
        r0 = r1.mentionWidth;
        r8 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r9;
        r7 = r7 - r0;
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 != 0) goto L_0x110a;
    L_0x10eb:
        r0 = r42.getMeasuredWidth();
        r8 = r1.mentionWidth;
        r0 = r0 - r8;
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 - r8;
        r8 = r1.countWidth;
        if (r8 == 0) goto L_0x1105;
    L_0x10fd:
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 + r9;
        goto L_0x1106;
    L_0x1105:
        r8 = 0;
    L_0x1106:
        r0 = r0 - r8;
        r1.mentionLeft = r0;
        goto L_0x112a;
    L_0x110a:
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = r1.countWidth;
        if (r10 == 0) goto L_0x111c;
    L_0x1116:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r10;
        goto L_0x111d;
    L_0x111c:
        r9 = 0;
    L_0x111d:
        r8 = r8 + r9;
        r1.mentionLeft = r8;
        r8 = r1.messageLeft;
        r8 = r8 + r0;
        r1.messageLeft = r8;
        r8 = r1.messageNameLeft;
        r8 = r8 + r0;
        r1.messageNameLeft = r8;
    L_0x112a:
        r8 = 1;
        r1.drawMention = r8;
    L_0x112d:
        if (r28 == 0) goto L_0x1172;
    L_0x112f:
        if (r29 != 0) goto L_0x1133;
    L_0x1131:
        r29 = r4;
    L_0x1133:
        r0 = r29.toString();
        r4 = r0.length();
        r8 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r4 <= r8) goto L_0x1143;
    L_0x113f:
        r0 = r0.substring(r3, r8);
    L_0x1143:
        r4 = r1.useForceThreeLines;
        if (r4 != 0) goto L_0x114b;
    L_0x1147:
        r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r4 == 0) goto L_0x114d;
    L_0x114b:
        if (r40 == 0) goto L_0x1156;
    L_0x114d:
        r4 = 32;
        r8 = 10;
        r0 = r0.replace(r8, r4);
        goto L_0x115e;
    L_0x1156:
        r4 = "\n\n";
        r8 = "\n";
        r0 = r0.replace(r4, r8);
    L_0x115e:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r8 = r1.paintIndex;
        r4 = r4[r8];
        r4 = r4.getFontMetricsInt();
        r8 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r29 = org.telegram.messenger.Emoji.replaceEmoji(r0, r4, r8, r3);
    L_0x1172:
        r4 = r29;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = java.lang.Math.max(r0, r7);
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x1184;
    L_0x1180:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x11b8;
    L_0x1184:
        if (r40 == 0) goto L_0x11b8;
    L_0x1186:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x118f;
    L_0x118a:
        r0 = r1.currentDialogFolderDialogsCount;
        r8 = 1;
        if (r0 != r8) goto L_0x11b8;
    L_0x118f:
        r31 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;	 Catch:{ Exception -> 0x11aa }
        r33 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x11aa }
        r34 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r35 = 0;
        r36 = 0;
        r37 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x11aa }
        r39 = 1;
        r30 = r40;
        r32 = r7;
        r38 = r7;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r30, r31, r32, r33, r34, r35, r36, r37, r38, r39);	 Catch:{ Exception -> 0x11aa }
        r1.messageNameLayout = r0;	 Catch:{ Exception -> 0x11aa }
        goto L_0x11ae;
    L_0x11aa:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x11ae:
        r0 = NUM; // 0x424CLASSNAME float:51.0 double:5.495378504E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        r9 = 0;
        goto L_0x11d5;
    L_0x11b8:
        r9 = 0;
        r1.messageNameLayout = r9;
        r0 = r1.useForceThreeLines;
        if (r0 != 0) goto L_0x11cd;
    L_0x11bf:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r0 == 0) goto L_0x11c4;
    L_0x11c3:
        goto L_0x11cd;
    L_0x11c4:
        r0 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
        goto L_0x11d5;
    L_0x11cd:
        r0 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.messageTop = r0;
    L_0x11d5:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x1256 }
        if (r0 != 0) goto L_0x11dd;
    L_0x11d9:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x1256 }
        if (r0 == 0) goto L_0x11f3;
    L_0x11dd:
        r0 = r1.currentDialogFolderId;	 Catch:{ Exception -> 0x1256 }
        if (r0 == 0) goto L_0x11f3;
    L_0x11e1:
        r0 = r1.currentDialogFolderDialogsCount;	 Catch:{ Exception -> 0x1256 }
        r8 = 1;
        if (r0 <= r8) goto L_0x11f4;
    L_0x11e6:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;	 Catch:{ Exception -> 0x1256 }
        r4 = r1.paintIndex;	 Catch:{ Exception -> 0x1256 }
        r11 = r0[r4];	 Catch:{ Exception -> 0x1256 }
        r19 = r11;
        r18 = r40;
        r40 = r9;
        goto L_0x1216;
    L_0x11f3:
        r8 = 1;
    L_0x11f4:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x1256 }
        if (r0 != 0) goto L_0x11fc;
    L_0x11f8:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x1256 }
        if (r0 == 0) goto L_0x11fe;
    L_0x11fc:
        if (r40 == 0) goto L_0x1210;
    L_0x11fe:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x1256 }
        r0 = r7 - r0;
        r0 = (float) r0;	 Catch:{ Exception -> 0x1256 }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x1256 }
        r10 = r41;
        r0 = android.text.TextUtils.ellipsize(r4, r10, r0, r6);	 Catch:{ Exception -> 0x1256 }
        r18 = r0;
        goto L_0x1214;
    L_0x1210:
        r10 = r41;
        r18 = r4;
    L_0x1214:
        r19 = r10;
    L_0x1216:
        r0 = r1.useForceThreeLines;	 Catch:{ Exception -> 0x1256 }
        if (r0 != 0) goto L_0x1233;
    L_0x121a:
        r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;	 Catch:{ Exception -> 0x1256 }
        if (r0 == 0) goto L_0x121f;
    L_0x121e:
        goto L_0x1233;
    L_0x121f:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x1256 }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x1256 }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = 0;
        r15 = 0;
        r8 = r0;
        r9 = r18;
        r10 = r19;
        r11 = r7;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x1256 }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x1256 }
        goto L_0x125a;
    L_0x1233:
        r21 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x1256 }
        r22 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x1256 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x1256 }
        r24 = 0;
        r25 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x1256 }
        if (r40 == 0) goto L_0x1247;
    L_0x1244:
        r27 = 1;
        goto L_0x1249;
    L_0x1247:
        r27 = 2;
    L_0x1249:
        r20 = r7;
        r23 = r0;
        r26 = r7;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ Exception -> 0x1256 }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x1256 }
        goto L_0x125a;
    L_0x1256:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x125a:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x1386;
    L_0x125e:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x1310;
    L_0x1262:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1310;
    L_0x1268:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r3);
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r3);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = r1.dialogMuted;
        if (r4 == 0) goto L_0x12a9;
    L_0x127d:
        r4 = r1.drawVerified;
        if (r4 != 0) goto L_0x12a9;
    L_0x1281:
        r4 = r1.drawScam;
        if (r4 != 0) goto L_0x12a9;
    L_0x1285:
        r4 = r1.nameLeft;
        r10 = (double) r4;
        r12 = (double) r2;
        java.lang.Double.isNaN(r12);
        r12 = r12 - r8;
        java.lang.Double.isNaN(r10);
        r10 = r10 + r12;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r4 = r4.getIntrinsicWidth();
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = (int) r10;
        r1.nameMuteLeft = r4;
        goto L_0x12f8;
    L_0x12a9:
        r4 = r1.drawVerified;
        if (r4 == 0) goto L_0x12d1;
    L_0x12ad:
        r4 = r1.nameLeft;
        r10 = (double) r4;
        r12 = (double) r2;
        java.lang.Double.isNaN(r12);
        r12 = r12 - r8;
        java.lang.Double.isNaN(r10);
        r10 = r10 + r12;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r4 = r4.getIntrinsicWidth();
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = (int) r10;
        r1.nameMuteLeft = r4;
        goto L_0x12f8;
    L_0x12d1:
        r4 = r1.drawScam;
        if (r4 == 0) goto L_0x12f8;
    L_0x12d5:
        r4 = r1.nameLeft;
        r10 = (double) r4;
        r12 = (double) r2;
        java.lang.Double.isNaN(r12);
        r12 = r12 - r8;
        java.lang.Double.isNaN(r10);
        r10 = r10 + r12;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r4 = r4.getIntrinsicWidth();
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r10 = r10 - r4;
        r4 = (int) r10;
        r1.nameMuteLeft = r4;
    L_0x12f8:
        r4 = 0;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x1310;
    L_0x12fd:
        r4 = (double) r2;
        r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x1310;
    L_0x1302:
        r0 = r1.nameLeft;
        r10 = (double) r0;
        java.lang.Double.isNaN(r4);
        r4 = r4 - r8;
        java.lang.Double.isNaN(r10);
        r10 = r10 + r4;
        r0 = (int) r10;
        r1.nameLeft = r0;
    L_0x1310:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x1351;
    L_0x1314:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x1351;
    L_0x131a:
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r2 = 0;
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x1321:
        if (r2 >= r0) goto L_0x1347;
    L_0x1323:
        r5 = r1.messageLayout;
        r5 = r5.getLineLeft(r2);
        r6 = 0;
        r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r5 != 0) goto L_0x1346;
    L_0x132e:
        r5 = r1.messageLayout;
        r5 = r5.getLineWidth(r2);
        r5 = (double) r5;
        r5 = java.lang.Math.ceil(r5);
        r8 = (double) r7;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r5;
        r5 = (int) r8;
        r4 = java.lang.Math.min(r4, r5);
        r2 = r2 + 1;
        goto L_0x1321;
    L_0x1346:
        r4 = 0;
    L_0x1347:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r4 == r0) goto L_0x1351;
    L_0x134c:
        r0 = r1.messageLeft;
        r0 = r0 + r4;
        r1.messageLeft = r0;
    L_0x1351:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x140e;
    L_0x1355:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x140e;
    L_0x135b:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineLeft(r3);
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x140e;
    L_0x1366:
        r0 = r1.messageNameLayout;
        r0 = r0.getLineWidth(r3);
        r2 = (double) r0;
        r2 = java.lang.Math.ceil(r2);
        r4 = (double) r7;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x140e;
    L_0x1376:
        r0 = r1.messageNameLeft;
        r6 = (double) r0;
        java.lang.Double.isNaN(r4);
        r4 = r4 - r2;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r4;
        r0 = (int) r6;
        r1.messageNameLeft = r0;
        goto L_0x140e;
    L_0x1386:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x13d2;
    L_0x138a:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13d2;
    L_0x1390:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r3);
        r4 = (float) r2;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x13b9;
    L_0x139b:
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r3);
        r6 = (double) r4;
        r6 = java.lang.Math.ceil(r6);
        r8 = (double) r2;
        r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r2 >= 0) goto L_0x13b9;
    L_0x13ab:
        r2 = r1.nameLeft;
        r10 = (double) r2;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r6;
        java.lang.Double.isNaN(r10);
        r10 = r10 - r8;
        r2 = (int) r10;
        r1.nameLeft = r2;
    L_0x13b9:
        r2 = r1.dialogMuted;
        if (r2 != 0) goto L_0x13c5;
    L_0x13bd:
        r2 = r1.drawVerified;
        if (r2 != 0) goto L_0x13c5;
    L_0x13c1:
        r2 = r1.drawScam;
        if (r2 == 0) goto L_0x13d2;
    L_0x13c5:
        r2 = r1.nameLeft;
        r2 = (float) r2;
        r2 = r2 + r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = (float) r0;
        r2 = r2 + r0;
        r0 = (int) r2;
        r1.nameMuteLeft = r0;
    L_0x13d2:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x13f7;
    L_0x13d6:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x13f7;
    L_0x13dc:
        r2 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r2 = 0;
        r4 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
    L_0x13e1:
        if (r2 >= r0) goto L_0x13f0;
    L_0x13e3:
        r5 = r1.messageLayout;
        r5 = r5.getLineLeft(r2);
        r4 = java.lang.Math.min(r4, r5);
        r2 = r2 + 1;
        goto L_0x13e1;
    L_0x13f0:
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r0 = r0 - r4;
        r0 = (int) r0;
        r1.messageLeft = r0;
    L_0x13f7:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x140e;
    L_0x13fb:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x140e;
    L_0x1401:
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r2 = r1.messageNameLayout;
        r2 = r2.getLineLeft(r3);
        r0 = r0 - r2;
        r0 = (int) r0;
        r1.messageNameLeft = r0;
    L_0x140e:
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
                if (dialog.pinnedNum == 0 && messageObject != null) {
                    break;
                }
                i++;
            }
        }
        return messageObject;
    }

    public boolean isFolderCell() {
        return this.currentDialogFolderId != 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:97:0x0176  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01be  */
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
        goto L_0x0318;
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
        if (r1 == 0) goto L_0x01c2;
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
        if (r2 != 0) goto L_0x01a7;
    L_0x0159:
        r6 = r1 & 256;
        if (r6 == 0) goto L_0x01a7;
    L_0x015d:
        r6 = r0.message;
        if (r6 == 0) goto L_0x0172;
    L_0x0161:
        r7 = r0.lastUnreadState;
        r6 = r6.isUnread();
        if (r7 == r6) goto L_0x0172;
    L_0x0169:
        r2 = r0.message;
        r2 = r2.isUnread();
        r0.lastUnreadState = r2;
        r2 = 1;
    L_0x0172:
        r6 = r0.isDialogCell;
        if (r6 == 0) goto L_0x01a7;
    L_0x0176:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r6 = r6.dialogs_dict;
        r7 = r0.currentDialogId;
        r6 = r6.get(r7);
        r6 = (org.telegram.tgnet.TLRPC.Dialog) r6;
        if (r6 == 0) goto L_0x01a7;
    L_0x0188:
        r7 = r0.unreadCount;
        r8 = r6.unread_count;
        if (r7 != r8) goto L_0x019a;
    L_0x018e:
        r7 = r0.markUnread;
        r8 = r6.unread_mark;
        if (r7 != r8) goto L_0x019a;
    L_0x0194:
        r7 = r0.mentionCount;
        r8 = r6.unread_mentions_count;
        if (r7 == r8) goto L_0x01a7;
    L_0x019a:
        r2 = r6.unread_count;
        r0.unreadCount = r2;
        r2 = r6.unread_mentions_count;
        r0.mentionCount = r2;
        r2 = r6.unread_mark;
        r0.markUnread = r2;
        r2 = 1;
    L_0x01a7:
        if (r2 != 0) goto L_0x01bc;
    L_0x01a9:
        r1 = r1 & 4096;
        if (r1 == 0) goto L_0x01bc;
    L_0x01ad:
        r1 = r0.message;
        if (r1 == 0) goto L_0x01bc;
    L_0x01b1:
        r6 = r0.lastSendState;
        r1 = r1.messageOwner;
        r1 = r1.send_state;
        if (r6 == r1) goto L_0x01bc;
    L_0x01b9:
        r0.lastSendState = r1;
        r2 = 1;
    L_0x01bc:
        if (r2 != 0) goto L_0x01c2;
    L_0x01be:
        r19.invalidate();
        return;
    L_0x01c2:
        r0.user = r3;
        r0.chat = r3;
        r0.encryptedChat = r3;
        r1 = r0.currentDialogFolderId;
        r2 = 0;
        if (r1 == 0) goto L_0x01e1;
    L_0x01ce:
        r0.dialogMuted = r5;
        r1 = r19.findFolderTopMessage();
        r0.message = r1;
        r1 = r0.message;
        if (r1 == 0) goto L_0x01df;
    L_0x01da:
        r6 = r1.getDialogId();
        goto L_0x01fa;
    L_0x01df:
        r6 = r2;
        goto L_0x01fa;
    L_0x01e1:
        r1 = r0.isDialogCell;
        if (r1 == 0) goto L_0x01f5;
    L_0x01e5:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r6 = r0.currentDialogId;
        r1 = r1.isDialogMuted(r6);
        if (r1 == 0) goto L_0x01f5;
    L_0x01f3:
        r1 = 1;
        goto L_0x01f6;
    L_0x01f5:
        r1 = 0;
    L_0x01f6:
        r0.dialogMuted = r1;
        r6 = r0.currentDialogId;
    L_0x01fa:
        r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r1 == 0) goto L_0x029f;
    L_0x01fe:
        r1 = (int) r6;
        r2 = 32;
        r2 = r6 >> r2;
        r3 = (int) r2;
        if (r1 == 0) goto L_0x024f;
    L_0x0206:
        if (r1 >= 0) goto L_0x023e;
    L_0x0208:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getChat(r1);
        r0.chat = r1;
        r1 = r0.isDialogCell;
        if (r1 != 0) goto L_0x0277;
    L_0x021d:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x0277;
    L_0x0221:
        r1 = r1.migrated_to;
        if (r1 == 0) goto L_0x0277;
    L_0x0225:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.chat;
        r2 = r2.migrated_to;
        r2 = r2.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        if (r1 == 0) goto L_0x0277;
    L_0x023b:
        r0.chat = r1;
        goto L_0x0277;
    L_0x023e:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r2.getUser(r1);
        r0.user = r1;
        goto L_0x0277;
    L_0x024f:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = java.lang.Integer.valueOf(r3);
        r1 = r1.getEncryptedChat(r2);
        r0.encryptedChat = r1;
        r1 = r0.encryptedChat;
        if (r1 == 0) goto L_0x0277;
    L_0x0263:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.encryptedChat;
        r2 = r2.user_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        r0.user = r1;
    L_0x0277:
        r1 = r0.useMeForMyMessages;
        if (r1 == 0) goto L_0x029f;
    L_0x027b:
        r1 = r0.user;
        if (r1 == 0) goto L_0x029f;
    L_0x027f:
        r1 = r0.message;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x029f;
    L_0x0287:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.clientUserId;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        r0.user = r1;
    L_0x029f:
        r1 = r0.currentDialogFolderId;
        if (r1 == 0) goto L_0x02bc;
    L_0x02a3:
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
        goto L_0x0318;
    L_0x02bc:
        r1 = r0.user;
        if (r1 == 0) goto L_0x02fc;
    L_0x02c0:
        r2 = r0.avatarDrawable;
        r2.setInfo(r1);
        r1 = r0.user;
        r1 = org.telegram.messenger.UserObject.isUserSelf(r1);
        if (r1 == 0) goto L_0x02e4;
    L_0x02cd:
        r1 = r0.useMeForMyMessages;
        if (r1 != 0) goto L_0x02e4;
    L_0x02d1:
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
        goto L_0x0318;
    L_0x02e4:
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
        goto L_0x0318;
    L_0x02fc:
        r1 = r0.chat;
        if (r1 == 0) goto L_0x0318;
    L_0x0300:
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
    L_0x0318:
        r1 = r19.getMeasuredWidth();
        if (r1 != 0) goto L_0x0329;
    L_0x031e:
        r1 = r19.getMeasuredHeight();
        if (r1 == 0) goto L_0x0325;
    L_0x0324:
        goto L_0x0329;
    L_0x0325:
        r19.requestLayout();
        goto L_0x032c;
    L_0x0329:
        r19.buildLayout();
    L_0x032c:
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
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09ff  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0aaf  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a83  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x08e2  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0950  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0995  */
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
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0995  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x099c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09ff  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0aaf  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a83  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x052b  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0698  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x085e  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0871  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x088c  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0995  */
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
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0411  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x052b  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x05d5  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0698  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x085e  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0871  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x088c  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0995  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x099c  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x09ff  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0aaf  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a83  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b41  */
    /* JADX WARNING: Missing block: B:293:0x0866, code skipped:
            if (r0.isDraw() != false) goto L_0x086d;
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
        r3 = NUM; // 0x7f0e0b3e float:1.8880875E38 double:1.0531635786E-314;
        r4 = "UnhideFromTop";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x0102;
    L_0x00bd:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r3 = NUM; // 0x7f0e0556 float:1.8877808E38 double:1.0531628315E-314;
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
        r3 = NUM; // 0x7f0e00ff float:1.8875555E38 double:1.0531622826E-314;
        r4 = "Archive";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable;
        r1.translationDrawable = r4;
        goto L_0x0102;
    L_0x00ed:
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r3 = NUM; // 0x7f0e0b35 float:1.8880857E38 double:1.053163574E-314;
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
        goto L_0x03cc;
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
        if (r0 == 0) goto L_0x0399;
    L_0x0364:
        r0 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r0 == 0) goto L_0x036e;
    L_0x0368:
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0399;
    L_0x036e:
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
        goto L_0x03a2;
    L_0x0399:
        r0 = r1.drawPin;
        if (r0 != 0) goto L_0x03a5;
    L_0x039d:
        r0 = r1.drawPinBackground;
        if (r0 == 0) goto L_0x03a2;
    L_0x03a1:
        goto L_0x03a5;
    L_0x03a2:
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x03c9;
    L_0x03a5:
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
    L_0x03c9:
        r24.restore();
    L_0x03cc:
        r0 = r1.translationX;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x03e8;
    L_0x03d4:
        r0 = r1.cornerProgress;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 >= 0) goto L_0x03fd;
    L_0x03da:
        r4 = (float) r10;
        r4 = r4 / r3;
        r0 = r0 + r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x03fb;
    L_0x03e5:
        r1.cornerProgress = r13;
        goto L_0x03fb;
    L_0x03e8:
        r0 = r1.cornerProgress;
        r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r4 <= 0) goto L_0x03fd;
    L_0x03ee:
        r4 = (float) r10;
        r4 = r4 / r3;
        r0 = r0 - r4;
        r1.cornerProgress = r0;
        r0 = r1.cornerProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x03fb;
    L_0x03f9:
        r1.cornerProgress = r9;
    L_0x03fb:
        r4 = 1;
        goto L_0x03fe;
    L_0x03fd:
        r4 = 0;
    L_0x03fe:
        r0 = r1.drawNameLock;
        if (r0 == 0) goto L_0x0411;
    L_0x0402:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r0.draw(r8);
        goto L_0x0449;
    L_0x0411:
        r0 = r1.drawNameGroup;
        if (r0 == 0) goto L_0x0424;
    L_0x0415:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r0.draw(r8);
        goto L_0x0449;
    L_0x0424:
        r0 = r1.drawNameBroadcast;
        if (r0 == 0) goto L_0x0437;
    L_0x0428:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r0.draw(r8);
        goto L_0x0449;
    L_0x0437:
        r0 = r1.drawNameBot;
        if (r0 == 0) goto L_0x0449;
    L_0x043b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r5 = r1.nameLockLeft;
        r6 = r1.nameLockTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r0.draw(r8);
    L_0x0449:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x04bd;
    L_0x044d:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0465;
    L_0x0451:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_nameArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
        goto L_0x0499;
    L_0x0465:
        r0 = r1.encryptedChat;
        if (r0 != 0) goto L_0x0486;
    L_0x0469:
        r0 = r1.customDialog;
        if (r0 == 0) goto L_0x0472;
    L_0x046d:
        r0 = r0.type;
        if (r0 != r12) goto L_0x0472;
    L_0x0471:
        goto L_0x0486;
    L_0x0472:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_name";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
        goto L_0x0499;
    L_0x0486:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_secretName";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
    L_0x0499:
        r24.save();
        r0 = r1.nameLeft;
        r0 = (float) r0;
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x04ab;
    L_0x04a3:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x04a8;
    L_0x04a7:
        goto L_0x04ab;
    L_0x04a8:
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        goto L_0x04ad;
    L_0x04ab:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x04ad:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.nameLayout;
        r0.draw(r8);
        r24.restore();
    L_0x04bd:
        r0 = r1.timeLayout;
        if (r0 == 0) goto L_0x04d9;
    L_0x04c1:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x04d9;
    L_0x04c5:
        r24.save();
        r0 = r1.timeLeft;
        r0 = (float) r0;
        r5 = r1.timeTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.timeLayout;
        r0.draw(r8);
        r24.restore();
    L_0x04d9:
        r0 = r1.messageNameLayout;
        if (r0 == 0) goto L_0x0527;
    L_0x04dd:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x04ef;
    L_0x04e1:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessageArchived_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x050e;
    L_0x04ef:
        r0 = r1.draftMessage;
        if (r0 == 0) goto L_0x0501;
    L_0x04f3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_draft";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
        goto L_0x050e;
    L_0x0501:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint;
        r5 = "chats_nameMessage_threeLines";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r0.setColor(r5);
    L_0x050e:
        r24.save();
        r0 = r1.messageNameLeft;
        r0 = (float) r0;
        r5 = r1.messageNameTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageNameLayout;	 Catch:{ Exception -> 0x0520 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0520 }
        goto L_0x0524;
    L_0x0520:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0524:
        r24.restore();
    L_0x0527:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x0587;
    L_0x052b:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x055b;
    L_0x052f:
        r0 = r1.chat;
        if (r0 == 0) goto L_0x0547;
    L_0x0533:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_nameMessageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
        goto L_0x056e;
    L_0x0547:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_messageArchived";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
        goto L_0x056e;
    L_0x055b:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r5 = r1.paintIndex;
        r6 = r0[r5];
        r0 = r0[r5];
        r5 = "chats_message";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.linkColor = r5;
        r6.setColor(r5);
    L_0x056e:
        r24.save();
        r0 = r1.messageLeft;
        r0 = (float) r0;
        r5 = r1.messageTop;
        r5 = (float) r5;
        r8.translate(r0, r5);
        r0 = r1.messageLayout;	 Catch:{ Exception -> 0x0580 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0580 }
        goto L_0x0584;
    L_0x0580:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0584:
        r24.restore();
    L_0x0587:
        r0 = r1.currentDialogFolderId;
        if (r0 != 0) goto L_0x05d1;
    L_0x058b:
        r0 = r1.drawClock;
        if (r0 == 0) goto L_0x059e;
    L_0x058f:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable;
        r0.draw(r8);
        goto L_0x05d1;
    L_0x059e:
        r0 = r1.drawCheck2;
        if (r0 == 0) goto L_0x05d1;
    L_0x05a2:
        r0 = r1.drawCheck1;
        if (r0 == 0) goto L_0x05c3;
    L_0x05a6:
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
        goto L_0x05d1;
    L_0x05c3:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r5 = r1.checkDrawLeft;
        r6 = r1.checkDrawTop;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable;
        r0.draw(r8);
    L_0x05d1:
        r0 = r1.dialogMuted;
        if (r0 == 0) goto L_0x0609;
    L_0x05d5:
        r0 = r1.drawVerified;
        if (r0 != 0) goto L_0x0609;
    L_0x05d9:
        r0 = r1.drawScam;
        if (r0 != 0) goto L_0x0609;
    L_0x05dd:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x05ed;
    L_0x05e5:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05ea;
    L_0x05e9:
        goto L_0x05ed;
    L_0x05ea:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x05ee;
    L_0x05ed:
        r6 = 0;
    L_0x05ee:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x05fa;
    L_0x05f7:
        r6 = NUM; // 0x41580000 float:13.5 double:5.416373534E-315;
        goto L_0x05fc;
    L_0x05fa:
        r6 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
    L_0x05fc:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable;
        r0.draw(r8);
        goto L_0x066c;
    L_0x0609:
        r0 = r1.drawVerified;
        if (r0 == 0) goto L_0x064a;
    L_0x060d:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x061d;
    L_0x0615:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x061a;
    L_0x0619:
        goto L_0x061d;
    L_0x061a:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x061f;
    L_0x061d:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x061f:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x0636;
    L_0x062e:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x0633;
    L_0x0632:
        goto L_0x0636;
    L_0x0633:
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        goto L_0x0638;
    L_0x0636:
        r6 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
    L_0x0638:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r0.draw(r8);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable;
        r0.draw(r8);
        goto L_0x066c;
    L_0x064a:
        r0 = r1.drawScam;
        if (r0 == 0) goto L_0x066c;
    L_0x064e:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r5 = r1.nameMuteLeft;
        r6 = r1.useForceThreeLines;
        if (r6 != 0) goto L_0x065e;
    L_0x0656:
        r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r6 == 0) goto L_0x065b;
    L_0x065a:
        goto L_0x065e;
    L_0x065b:
        r6 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        goto L_0x0660;
    L_0x065e:
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x0660:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r0, r5, r6);
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable;
        r0.draw(r8);
    L_0x066c:
        r0 = r1.drawReorder;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 != 0) goto L_0x0678;
    L_0x0672:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0690;
    L_0x0678:
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
    L_0x0690:
        r0 = r1.drawError;
        r6 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r7 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        if (r0 == 0) goto L_0x06e9;
    L_0x0698:
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
        goto L_0x0838;
    L_0x06e9:
        r0 = r1.drawCount;
        if (r0 != 0) goto L_0x0712;
    L_0x06ed:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x06f2;
    L_0x06f1:
        goto L_0x0712;
    L_0x06f2:
        r0 = r1.drawPin;
        if (r0 == 0) goto L_0x0838;
    L_0x06f6:
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
        goto L_0x0838;
    L_0x0712:
        r0 = r1.drawCount;
        if (r0 == 0) goto L_0x078a;
    L_0x0716:
        r0 = r1.dialogMuted;
        if (r0 != 0) goto L_0x0722;
    L_0x071a:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x071f;
    L_0x071e:
        goto L_0x0722;
    L_0x071f:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
        goto L_0x0724;
    L_0x0722:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
    L_0x0724:
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
        if (r0 == 0) goto L_0x078a;
    L_0x076f:
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
    L_0x078a:
        r0 = r1.drawMention;
        if (r0 == 0) goto L_0x0838;
    L_0x078e:
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
        if (r0 == 0) goto L_0x07ca;
    L_0x07c3:
        r0 = r1.folderId;
        if (r0 == 0) goto L_0x07ca;
    L_0x07c7:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint;
        goto L_0x07cc;
    L_0x07ca:
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint;
    L_0x07cc:
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r3 * r7;
        r3 = r3 * r7;
        r8.drawRoundRect(r2, r6, r3, r0);
        r0 = r1.mentionLayout;
        if (r0 == 0) goto L_0x0803;
    L_0x07db:
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
        goto L_0x0838;
    L_0x0803:
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
    L_0x0838:
        r0 = r1.animatingArchiveAvatar;
        r12 = NUM; // 0x432a0000 float:170.0 double:5.567260075E-315;
        if (r0 == 0) goto L_0x085a;
    L_0x083e:
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
    L_0x085a:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0868;
    L_0x085e:
        r0 = r1.archivedChatsDrawable;
        if (r0 == 0) goto L_0x0868;
    L_0x0862:
        r0 = r0.isDraw();
        if (r0 != 0) goto L_0x086d;
    L_0x0868:
        r0 = r1.avatarImage;
        r0.draw(r8);
    L_0x086d:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0874;
    L_0x0871:
        r24.restore();
    L_0x0874:
        r0 = r1.user;
        if (r0 == 0) goto L_0x097d;
    L_0x0878:
        r2 = r1.isDialogCell;
        if (r2 == 0) goto L_0x097d;
    L_0x087c:
        r2 = r1.currentDialogFolderId;
        if (r2 != 0) goto L_0x097d;
    L_0x0880:
        r0 = org.telegram.messenger.MessagesController.isSupportUser(r0);
        if (r0 != 0) goto L_0x097d;
    L_0x0886:
        r0 = r1.user;
        r2 = r0.bot;
        if (r2 != 0) goto L_0x097d;
    L_0x088c:
        r2 = r0.self;
        if (r2 != 0) goto L_0x08ba;
    L_0x0890:
        r0 = r0.status;
        if (r0 == 0) goto L_0x08a2;
    L_0x0894:
        r0 = r0.expires;
        r2 = r1.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        if (r0 > r2) goto L_0x08b8;
    L_0x08a2:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r0.onlinePrivacy;
        r2 = r1.user;
        r2 = r2.id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.containsKey(r2);
        if (r0 == 0) goto L_0x08ba;
    L_0x08b8:
        r0 = 1;
        goto L_0x08bb;
    L_0x08ba:
        r0 = 0;
    L_0x08bb:
        if (r0 != 0) goto L_0x08c3;
    L_0x08bd:
        r2 = r1.onlineProgress;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x097d;
    L_0x08c3:
        r2 = r1.avatarImage;
        r2 = r2.getImageY2();
        r3 = r1.useForceThreeLines;
        if (r3 != 0) goto L_0x08d5;
    L_0x08cd:
        r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r3 == 0) goto L_0x08d2;
    L_0x08d1:
        goto L_0x08d5;
    L_0x08d2:
        r16 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x08d9;
    L_0x08d5:
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r16 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
    L_0x08d9:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = r2 - r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x08fc;
    L_0x08e2:
        r3 = r1.avatarImage;
        r3 = r3.getImageX();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x08f4;
    L_0x08ec:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x08f1;
    L_0x08f0:
        goto L_0x08f4;
    L_0x08f1:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x08f6;
    L_0x08f4:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x08f6:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        goto L_0x0915;
    L_0x08fc:
        r3 = r1.avatarImage;
        r3 = r3.getImageX2();
        r5 = r1.useForceThreeLines;
        if (r5 != 0) goto L_0x090e;
    L_0x0906:
        r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
        if (r5 == 0) goto L_0x090b;
    L_0x090a:
        goto L_0x090e;
    L_0x090b:
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        goto L_0x0910;
    L_0x090e:
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x0910:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 - r5;
    L_0x0915:
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
        if (r0 == 0) goto L_0x0966;
    L_0x0950:
        r0 = r1.onlineProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x097d;
    L_0x0956:
        r2 = (float) r10;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x097b;
    L_0x0963:
        r1.onlineProgress = r13;
        goto L_0x097b;
    L_0x0966:
        r0 = r1.onlineProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x097d;
    L_0x096c:
        r2 = (float) r10;
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.onlineProgress = r0;
        r0 = r1.onlineProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x097b;
    L_0x0979:
        r1.onlineProgress = r9;
    L_0x097b:
        r15 = 1;
        goto L_0x097e;
    L_0x097d:
        r15 = r4;
    L_0x097e:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0987;
    L_0x0984:
        r24.restore();
    L_0x0987:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x0998;
    L_0x098b:
        r0 = r1.translationX;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x0998;
    L_0x0991:
        r0 = r1.archivedChatsDrawable;
        if (r0 == 0) goto L_0x0998;
    L_0x0995:
        r0.draw(r8);
    L_0x0998:
        r0 = r1.useSeparator;
        if (r0 == 0) goto L_0x09f8;
    L_0x099c:
        r0 = r1.fullSeparator;
        if (r0 != 0) goto L_0x09bc;
    L_0x09a0:
        r0 = r1.currentDialogFolderId;
        if (r0 == 0) goto L_0x09ac;
    L_0x09a4:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x09ac;
    L_0x09a8:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x09bc;
    L_0x09ac:
        r0 = r1.fullSeparator2;
        if (r0 == 0) goto L_0x09b5;
    L_0x09b0:
        r0 = r1.archiveHidden;
        if (r0 != 0) goto L_0x09b5;
    L_0x09b4:
        goto L_0x09bc;
    L_0x09b5:
        r0 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r0);
        goto L_0x09bd;
    L_0x09bc:
        r14 = 0;
    L_0x09bd:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x09dd;
    L_0x09c1:
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
        goto L_0x09f8;
    L_0x09dd:
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
        goto L_0x09f9;
    L_0x09f8:
        r14 = 1;
    L_0x09f9:
        r0 = r1.clipProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0a49;
    L_0x09ff:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 == r2) goto L_0x0a09;
    L_0x0a05:
        r24.restore();
        goto L_0x0a49;
    L_0x0a09:
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
    L_0x0a49:
        r0 = r1.drawReorder;
        if (r0 != 0) goto L_0x0a53;
    L_0x0a4d:
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 == 0) goto L_0x0a7f;
    L_0x0a53:
        r0 = r1.drawReorder;
        if (r0 == 0) goto L_0x0a6b;
    L_0x0a57:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0a7f;
    L_0x0a5d:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0a7e;
    L_0x0a68:
        r1.reorderIconProgress = r13;
        goto L_0x0a7e;
    L_0x0a6b:
        r0 = r1.reorderIconProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0a7f;
    L_0x0a71:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 - r2;
        r1.reorderIconProgress = r0;
        r0 = r1.reorderIconProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a7e;
    L_0x0a7c:
        r1.reorderIconProgress = r9;
    L_0x0a7e:
        r15 = 1;
    L_0x0a7f:
        r0 = r1.archiveHidden;
        if (r0 == 0) goto L_0x0aaf;
    L_0x0a83:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0adb;
    L_0x0a89:
        r2 = (float) r10;
        r3 = NUM; // 0x43660000 float:230.0 double:5.586687527E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0a98;
    L_0x0a96:
        r1.archiveBackgroundProgress = r9;
    L_0x0a98:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0ada;
    L_0x0aa1:
        r0 = r1.avatarDrawable;
        r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r3 = r1.archiveBackgroundProgress;
        r2 = r2.getInterpolation(r3);
        r0.setArchivedAvatarHiddenProgress(r2);
        goto L_0x0ada;
    L_0x0aaf:
        r0 = r1.archiveBackgroundProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0adb;
    L_0x0ab5:
        r2 = (float) r10;
        r3 = NUM; // 0x43660000 float:230.0 double:5.586687527E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.archiveBackgroundProgress = r0;
        r0 = r1.archiveBackgroundProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0ac4;
    L_0x0ac2:
        r1.archiveBackgroundProgress = r13;
    L_0x0ac4:
        r0 = r1.avatarDrawable;
        r0 = r0.getAvatarType();
        r2 = 3;
        if (r0 != r2) goto L_0x0ada;
    L_0x0acd:
        r0 = r1.avatarDrawable;
        r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT;
        r3 = r1.archiveBackgroundProgress;
        r2 = r2.getInterpolation(r3);
        r0.setArchivedAvatarHiddenProgress(r2);
    L_0x0ada:
        r15 = 1;
    L_0x0adb:
        r0 = r1.animatingArchiveAvatar;
        if (r0 == 0) goto L_0x0af1;
    L_0x0adf:
        r0 = r1.animatingArchiveAvatarProgress;
        r2 = (float) r10;
        r0 = r0 + r2;
        r1.animatingArchiveAvatarProgress = r0;
        r0 = r1.animatingArchiveAvatarProgress;
        r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1));
        if (r0 < 0) goto L_0x0af0;
    L_0x0aeb:
        r1.animatingArchiveAvatarProgress = r12;
        r2 = 0;
        r1.animatingArchiveAvatar = r2;
    L_0x0af0:
        r15 = 1;
    L_0x0af1:
        r0 = r1.drawRevealBackground;
        if (r0 == 0) goto L_0x0b1f;
    L_0x0af5:
        r0 = r1.currentRevealBounceProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b09;
    L_0x0afb:
        r2 = (float) r10;
        r2 = r2 / r12;
        r0 = r0 + r2;
        r1.currentRevealBounceProgress = r0;
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0b09;
    L_0x0b06:
        r1.currentRevealBounceProgress = r13;
        r15 = 1;
    L_0x0b09:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x0b3e;
    L_0x0b0f:
        r2 = (float) r10;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 + r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x0b3f;
    L_0x0b1c:
        r1.currentRevealProgress = r13;
        goto L_0x0b3f;
    L_0x0b1f:
        r0 = r1.currentRevealBounceProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x0b28;
    L_0x0b25:
        r1.currentRevealBounceProgress = r9;
        r15 = 1;
    L_0x0b28:
        r0 = r1.currentRevealProgress;
        r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x0b3e;
    L_0x0b2e:
        r2 = (float) r10;
        r3 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = r2 / r3;
        r0 = r0 - r2;
        r1.currentRevealProgress = r0;
        r0 = r1.currentRevealProgress;
        r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r0 >= 0) goto L_0x0b3f;
    L_0x0b3b:
        r1.currentRevealProgress = r9;
        goto L_0x0b3f;
    L_0x0b3e:
        r14 = r15;
    L_0x0b3f:
        if (r14 == 0) goto L_0x0b44;
    L_0x0b41:
        r23.invalidate();
    L_0x0b44:
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
