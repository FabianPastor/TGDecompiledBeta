package org.telegram.ui.Cells;

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
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
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
    private boolean drawPlay;
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
    private boolean hasMessageThumb;
    private int index;
    private BounceInterpolator interpolator;
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
    private boolean promoDialog;
    private RectF rect;
    private float reorderIconProgress;
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

    public DialogCell(Context context, boolean z, boolean z2) {
        this(context, z, z2, UserConfig.selectedAccount);
    }

    public DialogCell(Context context, boolean z, boolean z2, int i) {
        super(context);
        this.thumbImage = new ImageReceiver(this);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.rect = new RectF();
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
        this.thumbImage.onDetachedFromWindow();
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

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x05f9, code lost:
        if (r2.post_messages == false) goto L_0x05d5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x1824 A[SYNTHETIC, Splitter:B:1006:0x1824] */
    /* JADX WARNING: Removed duplicated region for block: B:1022:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0360  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0b1c A[SYNTHETIC, Splitter:B:495:0x0b1c] */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0b3d  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0b69  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0baa  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0bb6  */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0d4b  */
    /* JADX WARNING: Removed duplicated region for block: B:600:0x0de6  */
    /* JADX WARNING: Removed duplicated region for block: B:603:0x0dee  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0df6  */
    /* JADX WARNING: Removed duplicated region for block: B:613:0x0e13  */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0e25  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0ec5  */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x0f3c  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0fed  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x102c  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x1037  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x1047  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x105f  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x106e  */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x10a7  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x10cf  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x1152  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x1168  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x11d6 A[Catch:{ Exception -> 0x11f5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x11d9 A[Catch:{ Exception -> 0x11f5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x1203  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x12ac  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x135d  */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x1382  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x13b3  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x14d4  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x1596  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x159f  */
    /* JADX WARNING: Removed duplicated region for block: B:896:0x15c5 A[Catch:{ Exception -> 0x165a }] */
    /* JADX WARNING: Removed duplicated region for block: B:897:0x15d0 A[Catch:{ Exception -> 0x165a }] */
    /* JADX WARNING: Removed duplicated region for block: B:907:0x15f5 A[Catch:{ Exception -> 0x165a }] */
    /* JADX WARNING: Removed duplicated region for block: B:922:0x1648 A[Catch:{ Exception -> 0x165a }] */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x164b A[Catch:{ Exception -> 0x165a }] */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x1662  */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x1792  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r46 = this;
            r1 = r46
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
            java.lang.CharSequence r0 = r0.getPrintingString(r9, r6)
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
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r3) goto L_0x00f4
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x00e9
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x00ed
        L_0x00e9:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x00f0
        L_0x00ed:
            java.lang.String r11 = "%2$s: ⁨%1$s⁩"
            goto L_0x0102
        L_0x00f0:
            java.lang.String r11 = "⁨%s⁩"
            goto L_0x0106
        L_0x00f4:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x00fc
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x0100
        L_0x00fc:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0104
        L_0x0100:
            java.lang.String r11 = "%2$s: %1$s"
        L_0x0102:
            r12 = 1
            goto L_0x0107
        L_0x0104:
            java.lang.String r11 = "%1$s"
        L_0x0106:
            r12 = 0
        L_0x0107:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x010e
            java.lang.CharSequence r13 = r13.messageText
            goto L_0x010f
        L_0x010e:
            r13 = 0
        L_0x010f:
            r1.lastMessageString = r13
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            r14 = 1117782016(0x42a00000, float:80.0)
            r15 = 1118044160(0x42a40000, float:82.0)
            r16 = 1101004800(0x41a00000, float:20.0)
            r4 = 33
            r18 = 1102053376(0x41b00000, float:22.0)
            r8 = 150(0x96, float:2.1E-43)
            r2 = 2
            r20 = 1117257728(0x42980000, float:76.0)
            r21 = 1099956224(0x41900000, float:18.0)
            r22 = 1117519872(0x429CLASSNAME, float:78.0)
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x0360
            int r0 = r13.type
            if (r0 != r2) goto L_0x01af
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0174
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0139
            goto L_0x0174
        L_0x0139:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x015a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0280
        L_0x015a:
            int r0 = r46.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x0174:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0195
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0280
        L_0x0195:
            int r0 = r46.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x01af:
            boolean r10 = r13.verified
            r1.drawVerified = r10
            boolean r10 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r10 == 0) goto L_0x0254
            if (r0 != r5) goto L_0x0254
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x020d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c4
            goto L_0x020d
        L_0x01c4:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01ec
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01e1
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01e3
        L_0x01e1:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01e3:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0280
        L_0x01ec:
            int r0 = r46.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01fc
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01fe
        L_0x01fc:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01fe:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x020d:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0234
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x022a
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x022c
        L_0x022a:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x022c:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0280
        L_0x0234:
            int r0 = r46.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0244
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0246
        L_0x0244:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0246:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x0254:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x026f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x025d
            goto L_0x026f
        L_0x025d:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0268
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x0268:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x026f:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x027a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r0
            goto L_0x0280
        L_0x027a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r0
        L_0x0280:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r10 = r0.type
            if (r10 != r5) goto L_0x030c
            r0 = 2131625495(0x7f0e0617, float:1.88782E38)
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
            java.lang.String r10 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r10)
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            java.lang.String r12 = "chats_attachMessage"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            int r12 = r10.length()
            r10.setSpan(r11, r6, r12, r4)
            goto L_0x02f8
        L_0x02be:
            java.lang.String r4 = r10.message
            int r10 = r4.length()
            if (r10 <= r8) goto L_0x02ca
            java.lang.String r4 = r4.substring(r6, r8)
        L_0x02ca:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x02ea
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x02d3
            goto L_0x02ea
        L_0x02d3:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r12 = 32
            r13 = 10
            java.lang.String r4 = r4.replace(r13, r12)
            r10[r6] = r4
            r10[r5] = r0
            java.lang.String r4 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r4)
            goto L_0x02f8
        L_0x02ea:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r10[r6] = r4
            r10[r5] = r0
            java.lang.String r4 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x02f8:
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r4 = r4[r11]
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.CharSequence r4 = org.telegram.messenger.Emoji.replaceEmoji(r10, r4, r11, r6)
            r10 = 0
            goto L_0x031a
        L_0x030c:
            java.lang.String r4 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x0318
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r0[r9]
        L_0x0318:
            r0 = 0
            r10 = 1
        L_0x031a:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.unread_count
            if (r12 == 0) goto L_0x033a
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13[r6] = r12
            java.lang.String r12 = "%d"
            java.lang.String r12 = java.lang.String.format(r12, r13)
            goto L_0x033d
        L_0x033a:
            r1.drawCount = r6
            r12 = 0
        L_0x033d:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            boolean r13 = r13.sent
            if (r13 == 0) goto L_0x0348
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            goto L_0x034c
        L_0x0348:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
        L_0x034c:
            r1.drawClock = r6
            r1.drawError = r6
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            java.lang.String r13 = r13.name
            r2 = r4
            r14 = r9
            r23 = r10
            r10 = r13
            r6 = 1
            r9 = 0
            r15 = 0
            r4 = r0
            r13 = r11
            goto L_0x0feb
        L_0x0360:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x037b
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x0369
            goto L_0x037b
        L_0x0369:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x0374
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r13
            goto L_0x038c
        L_0x0374:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r13
            goto L_0x038c
        L_0x037b:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x0386
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r13
            goto L_0x038c
        L_0x0386:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r13
        L_0x038c:
            org.telegram.tgnet.TLRPC$EncryptedChat r13 = r1.encryptedChat
            if (r13 == 0) goto L_0x0415
            int r13 = r1.currentDialogFolderId
            if (r13 != 0) goto L_0x05a6
            r1.drawNameLock = r5
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x03da
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x039f
            goto L_0x03da
        L_0x039f:
            r13 = 1099169792(0x41840000, float:16.5)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLockTop = r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x03c0
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 + r14
            r1.nameLeft = r13
            goto L_0x05a6
        L_0x03c0:
            int r13 = r46.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r13 = r13 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 - r14
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r13
            goto L_0x05a6
        L_0x03da:
            r13 = 1095237632(0x41480000, float:12.5)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLockTop = r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x03fb
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 + r14
            r1.nameLeft = r13
            goto L_0x05a6
        L_0x03fb:
            int r13 = r46.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r13 = r13 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 - r14
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r13
            goto L_0x05a6
        L_0x0415:
            int r13 = r1.currentDialogFolderId
            if (r13 != 0) goto L_0x05a6
            org.telegram.tgnet.TLRPC$Chat r13 = r1.chat
            if (r13 == 0) goto L_0x050c
            boolean r2 = r13.scam
            if (r2 == 0) goto L_0x0429
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x042d
        L_0x0429:
            boolean r2 = r13.verified
            r1.drawVerified = r2
        L_0x042d:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x05a6
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x04a3
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x043a
            goto L_0x04a3
        L_0x043a:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r13 = r2.id
            if (r13 < 0) goto L_0x0458
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x044d
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x044d
            goto L_0x0458
        L_0x044d:
            r1.drawNameGroup = r5
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x0462
        L_0x0458:
            r1.drawNameBroadcast = r5
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x0462:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0482
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x0477
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0479
        L_0x0477:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0479:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x0482:
            int r2 = r46.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = r2 - r13
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x0492
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0494
        L_0x0492:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0494:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x04a3:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r13 = r2.id
            if (r13 < 0) goto L_0x04c1
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x04b6
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x04b6
            goto L_0x04c1
        L_0x04b6:
            r1.drawNameGroup = r5
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04cb
        L_0x04c1:
            r1.drawNameBroadcast = r5
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04cb:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x04eb
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x04e0
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e2
        L_0x04e0:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e2:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x04eb:
            int r2 = r46.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r13
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x04fb
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04fd
        L_0x04fb:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04fd:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x050c:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x05a6
            boolean r13 = r2.scam
            if (r13 == 0) goto L_0x051c
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0520
        L_0x051c:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0520:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x05a6
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x05a6
            r1.drawNameBot = r5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x056e
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0535
            goto L_0x056e
        L_0x0535:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0555
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x0555:
            int r2 = r46.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = r2 - r13
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x056e:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x058e
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a6
        L_0x058e:
            int r2 = r46.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r13
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.nameLeft = r2
        L_0x05a6:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x05b2
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x05b2
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner
            int r2 = r2.date
        L_0x05b2:
            boolean r13 = r1.isDialogCell
            if (r13 == 0) goto L_0x060d
            int r13 = r1.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r13)
            long r14 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r13.getDraft(r14, r6)
            r1.draftMessage = r13
            if (r13 == 0) goto L_0x05e1
            java.lang.String r13 = r13.message
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x05d7
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            int r13 = r13.reply_to_msg_id
            if (r13 == 0) goto L_0x05d5
            goto L_0x05d7
        L_0x05d5:
            r2 = 0
            goto L_0x0608
        L_0x05d7:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            int r13 = r13.date
            if (r2 <= r13) goto L_0x05e1
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x05d5
        L_0x05e1:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x05fb
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r13 = r2.megagroup
            if (r13 != 0) goto L_0x05fb
            boolean r13 = r2.creator
            if (r13 != 0) goto L_0x05fb
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x05d5
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x05d5
        L_0x05fb:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x060b
            boolean r13 = r2.left
            if (r13 != 0) goto L_0x05d5
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x060b
            goto L_0x05d5
        L_0x0608:
            r1.draftMessage = r2
            goto L_0x0610
        L_0x060b:
            r2 = 0
            goto L_0x0610
        L_0x060d:
            r2 = 0
            r1.draftMessage = r2
        L_0x0610:
            if (r0 == 0) goto L_0x0621
            r1.lastPrintString = r0
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r4[r9]
            r4 = r2
            r2 = 2
        L_0x061c:
            r6 = 1
            r11 = 1
        L_0x061e:
            r12 = 0
            goto L_0x0dea
        L_0x0621:
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06b5
            r0 = 2131625091(0x7f0e0483, float:1.887738E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0660
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x065a
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0643
            goto L_0x065a
        L_0x0643:
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            java.lang.String r12 = "chats_draft"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            int r12 = r0.length()
            r2.setSpan(r11, r6, r12, r4)
            goto L_0x06b2
        L_0x065a:
            r4 = r0
            r0 = r3
        L_0x065c:
            r2 = 2
            r6 = 1
            r11 = 0
            goto L_0x061e
        L_0x0660:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r12 = r2.length()
            if (r12 <= r8) goto L_0x066e
            java.lang.String r2 = r2.substring(r6, r8)
        L_0x066e:
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            r12 = 32
            r14 = 10
            java.lang.String r2 = r2.replace(r14, r12)
            r13[r6] = r2
            r13[r5] = r0
            java.lang.String r2 = java.lang.String.format(r11, r13)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x06a0
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 != 0) goto L_0x06a0
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            java.lang.String r12 = "chats_draft"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            int r12 = r0.length()
            int r12 = r12 + r5
            r2.setSpan(r11, r6, r12, r4)
        L_0x06a0:
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r4 = r4[r11]
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r11, r6)
        L_0x06b2:
            r4 = r0
            r0 = r2
            goto L_0x065c
        L_0x06b5:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x06cc
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625566(0x7f0e065e, float:1.8878344E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x06c8:
            r2 = 2
            r4 = 0
            goto L_0x061c
        L_0x06cc:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x0740
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x073e
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r9 = r2[r4]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x06e8
            r0 = 2131625173(0x7f0e04d5, float:1.8877546E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c8
        L_0x06e8:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0700
            r0 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r2[r6] = r4
            java.lang.String r4 = "AwaitingEncryption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x06c8
        L_0x0700:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x070e
            r0 = 2131625174(0x7f0e04d6, float:1.8877549E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c8
        L_0x070e:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x073e
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x0734
            r0 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r2[r6] = r4
            java.lang.String r4 = "EncryptedChatStartedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x06c8
        L_0x0734:
            r0 = 2131625161(0x7f0e04c9, float:1.8877522E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c8
        L_0x073e:
            r0 = r3
            goto L_0x06c8
        L_0x0740:
            int r0 = r0.getFromChatId()
            if (r0 <= 0) goto L_0x0756
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            r2 = 0
            goto L_0x0767
        L_0x0756:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            r2 = r0
            r0 = 0
        L_0x0767:
            int r13 = r1.dialogsType
            r14 = 3
            if (r13 != r14) goto L_0x0784
            org.telegram.tgnet.TLRPC$User r13 = r1.user
            boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r13)
            if (r13 == 0) goto L_0x0784
            r0 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2
            r4 = 0
            r10 = 0
        L_0x0780:
            r11 = 1
        L_0x0781:
            r12 = 0
            goto L_0x0de2
        L_0x0784:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x0799
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 != 0) goto L_0x0799
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0799
            java.lang.CharSequence r0 = r46.formatArchivedDialogNames()
            r2 = 2
        L_0x0795:
            r4 = 0
            r6 = 1
            r11 = 0
            goto L_0x0781
        L_0x0799:
            org.telegram.messenger.MessageObject r13 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r14 == 0) goto L_0x07c8
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x07ba
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x07b7
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x07ba
        L_0x07b7:
            r0 = r3
            r10 = 0
            goto L_0x07be
        L_0x07ba:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x07be:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r9 = r2[r4]
            r2 = 2
        L_0x07c5:
            r4 = 0
            r6 = 1
            goto L_0x0780
        L_0x07c8:
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x08b8
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 != 0) goto L_0x08b8
            boolean r13 = r13.needDrawBluredPreview()
            if (r13 != 0) goto L_0x08b8
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isPhoto()
            if (r13 != 0) goto L_0x07ee
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isNewGif()
            if (r13 != 0) goto L_0x07ee
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isVideo()
            if (r13 == 0) goto L_0x08b8
        L_0x07ee:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isWebpage()
            if (r13 == 0) goto L_0x0801
            org.telegram.messenger.MessageObject r13 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r13.media
            org.telegram.tgnet.TLRPC$WebPage r13 = r13.webpage
            java.lang.String r13 = r13.type
            goto L_0x0802
        L_0x0801:
            r13 = 0
        L_0x0802:
            java.lang.String r14 = "app"
            boolean r14 = r14.equals(r13)
            if (r14 != 0) goto L_0x08b8
            java.lang.String r14 = "profile"
            boolean r14 = r14.equals(r13)
            if (r14 != 0) goto L_0x08b8
            java.lang.String r14 = "article"
            boolean r13 = r14.equals(r13)
            if (r13 != 0) goto L_0x08b8
            org.telegram.messenger.MessageObject r13 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r13.photoThumbs
            r14 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r13 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r13, r14)
            org.telegram.messenger.MessageObject r14 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.photoThumbs
            int r15 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15)
            if (r13 != r14) goto L_0x0833
            r14 = 0
        L_0x0833:
            if (r13 == 0) goto L_0x08b8
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isVideo()
            r1.drawPlay = r15
            java.lang.String r15 = org.telegram.messenger.FileLoader.getAttachFileName(r14)
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.mediaExists
            if (r4 != 0) goto L_0x0882
            int r4 = r1.currentAccount
            org.telegram.messenger.DownloadController r4 = org.telegram.messenger.DownloadController.getInstance(r4)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r4 = r4.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r4 != 0) goto L_0x0882
            int r4 = r1.currentAccount
            org.telegram.messenger.FileLoader r4 = org.telegram.messenger.FileLoader.getInstance(r4)
            boolean r4 = r4.isLoadingFile(r15)
            if (r4 == 0) goto L_0x0864
            goto L_0x0882
        L_0x0864:
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            r25 = 0
            r26 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r13, r6)
            r29 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            r31 = 0
            java.lang.String r28 = "20_20"
            r24 = r4
            r30 = r6
            r24.setImage((org.telegram.messenger.ImageLocation) r25, (java.lang.String) r26, (org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (java.lang.String) r29, (java.lang.Object) r30, (int) r31)
            goto L_0x08b6
        L_0x0882:
            org.telegram.messenger.MessageObject r4 = r1.message
            int r4 = r4.type
            if (r4 != r5) goto L_0x0891
            if (r14 == 0) goto L_0x088d
            int r4 = r14.size
            goto L_0x088e
        L_0x088d:
            r4 = 0
        L_0x088e:
            r29 = r4
            goto L_0x0893
        L_0x0891:
            r29 = 0
        L_0x0893:
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForObject(r14, r6)
            org.telegram.messenger.MessageObject r6 = r1.message
            org.telegram.tgnet.TLObject r6 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r13, r6)
            r30 = 0
            org.telegram.messenger.MessageObject r6 = r1.message
            r32 = 0
            java.lang.String r26 = "20_20"
            java.lang.String r28 = "20_20"
            r24 = r4
            r31 = r6
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x08b6:
            r4 = 0
            goto L_0x08b9
        L_0x08b8:
            r4 = 1
        L_0x08b9:
            org.telegram.tgnet.TLRPC$Chat r6 = r1.chat
            if (r6 == 0) goto L_0x0b96
            int r6 = r6.id
            if (r6 <= 0) goto L_0x0b96
            if (r2 != 0) goto L_0x0b96
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L_0x08d6
            r0 = 2131625495(0x7f0e0617, float:1.88782E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x08d4:
            r2 = r0
            goto L_0x0919
        L_0x08d6:
            if (r0 == 0) goto L_0x090b
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x08ec
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08e1
            goto L_0x08ec
        L_0x08e1:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r3)
            goto L_0x08d4
        L_0x08ec:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r2 == 0) goto L_0x08fc
            r0 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x08d4
        L_0x08fc:
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r3)
            goto L_0x08d4
        L_0x090b:
            if (r2 == 0) goto L_0x0916
            java.lang.String r0 = r2.title
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r3)
            goto L_0x08d4
        L_0x0916:
            java.lang.String r0 = "DELETED"
            goto L_0x08d4
        L_0x0919:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r6 = r0.caption
            if (r6 == 0) goto L_0x098f
            java.lang.String r0 = r6.toString()
            int r6 = r0.length()
            if (r6 <= r8) goto L_0x092e
            r6 = 0
            java.lang.String r0 = r0.substring(r6, r8)
        L_0x092e:
            if (r4 != 0) goto L_0x0933
            r4 = r3
        L_0x0931:
            r6 = 2
            goto L_0x0967
        L_0x0933:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isVideo()
            if (r4 == 0) goto L_0x093f
            java.lang.String r4 = "📹 "
            goto L_0x0931
        L_0x093f:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isVoice()
            if (r4 == 0) goto L_0x094b
            java.lang.String r4 = "🎤 "
            goto L_0x0931
        L_0x094b:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isMusic()
            if (r4 == 0) goto L_0x0957
            java.lang.String r4 = "🎧 "
            goto L_0x0931
        L_0x0957:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isPhoto()
            if (r4 == 0) goto L_0x0963
            java.lang.String r4 = "🖼 "
            goto L_0x0931
        L_0x0963:
            java.lang.String r4 = "📎 "
            goto L_0x0931
        L_0x0967:
            java.lang.Object[] r12 = new java.lang.Object[r6]
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r4 = 32
            r13 = 10
            java.lang.String r0 = r0.replace(r13, r4)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r4 = 0
            r12[r4] = r0
            r12[r5] = r2
            java.lang.String r0 = java.lang.String.format(r11, r12)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b09
        L_0x098f:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            if (r4 == 0) goto L_0x0a85
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0a85
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r9 = r0[r4]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x09d6
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 18
            if (r0 < r6) goto L_0x09c5
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r4 = r4.poll
            java.lang.String r4 = r4.question
            r6 = 0
            r0[r6] = r4
            java.lang.String r4 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            goto L_0x0a47
        L_0x09c5:
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r4 = r4.poll
            java.lang.String r4 = r4.question
            r0[r6] = r4
            java.lang.String r4 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            goto L_0x0a47
        L_0x09d6:
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r6 == 0) goto L_0x0a02
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 18
            if (r0 < r6) goto L_0x09f1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r6 = 0
            r0[r6] = r4
            java.lang.String r4 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            goto L_0x0a47
        L_0x09f1:
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r0[r6] = r4
            java.lang.String r4 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            goto L_0x0a47
        L_0x0a02:
            r6 = 0
            int r4 = r0.type
            r13 = 14
            if (r4 != r13) goto L_0x0a41
            int r4 = android.os.Build.VERSION.SDK_INT
            r13 = 18
            if (r4 < r13) goto L_0x0a28
            r4 = 2
            java.lang.Object[] r13 = new java.lang.Object[r4]
            java.lang.String r0 = r0.getMusicAuthor()
            r13[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r13[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r13)
            goto L_0x0a47
        L_0x0a28:
            r4 = 2
            java.lang.Object[] r13 = new java.lang.Object[r4]
            java.lang.String r0 = r0.getMusicAuthor()
            r13[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r13[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r13)
            goto L_0x0a47
        L_0x0a41:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0a47:
            r4 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r4)
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            r4 = 0
            r6[r4] = r0
            r6[r5] = r2
            java.lang.String r0 = java.lang.String.format(r11, r6)
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0a7f }
            java.lang.String r6 = "chats_attachMessage"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ Exception -> 0x0a7f }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0a7f }
            if (r12 == 0) goto L_0x0a73
            int r6 = r2.length()     // Catch:{ Exception -> 0x0a7f }
            r11 = 2
            int r6 = r6 + r11
            goto L_0x0a74
        L_0x0a73:
            r6 = 0
        L_0x0a74:
            int r11 = r4.length()     // Catch:{ Exception -> 0x0a7f }
            r12 = 33
            r4.setSpan(r0, r6, r11, r12)     // Catch:{ Exception -> 0x0a7f }
            goto L_0x0b0a
        L_0x0a7f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0b0a
        L_0x0a85:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            if (r4 == 0) goto L_0x0b05
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0ade
            r6 = 32
            r13 = 10
            java.lang.String r0 = r4.replace(r13, r6)
            java.lang.String r0 = r0.trim()
            int r4 = r46.getMeasuredWidth()
            r6 = 1121058816(0x42d20000, float:105.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            if (r12 == 0) goto L_0x0ac6
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            if (r6 != 0) goto L_0x0abd
            float r4 = (float) r4
            java.lang.String r6 = r2.toString()
            float r6 = r9.measureText(r6)
            float r4 = r4 - r6
            int r4 = (int) r4
        L_0x0abd:
            float r4 = (float) r4
            java.lang.String r6 = ": "
            float r6 = r9.measureText(r6)
            float r4 = r4 - r6
            int r4 = (int) r4
        L_0x0ac6:
            if (r4 <= 0) goto L_0x0adc
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r12 = 0
            java.lang.Object r6 = r6.get(r12)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r4, r9)
            java.lang.String r0 = r0.toString()
            goto L_0x0af5
        L_0x0adc:
            r12 = 0
            goto L_0x0af5
        L_0x0ade:
            r12 = 0
            int r0 = r4.length()
            if (r0 <= r8) goto L_0x0ae9
            java.lang.String r4 = r4.substring(r12, r8)
        L_0x0ae9:
            r6 = 32
            r13 = 10
            java.lang.String r0 = r4.replace(r13, r6)
            java.lang.String r0 = r0.trim()
        L_0x0af5:
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            r6[r12] = r0
            r6[r5] = r2
            java.lang.String r0 = java.lang.String.format(r11, r6)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b09
        L_0x0b05:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r3)
        L_0x0b09:
            r4 = r0
        L_0x0b0a:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0b12
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0b1c
        L_0x0b12:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0b3d
            int r0 = r4.length()
            if (r0 <= 0) goto L_0x0b3d
        L_0x0b1c:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0b36 }
            java.lang.String r6 = "chats_nameMessage"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ Exception -> 0x0b36 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0b36 }
            int r6 = r2.length()     // Catch:{ Exception -> 0x0b36 }
            int r6 = r6 + r5
            r11 = 33
            r12 = 0
            r4.setSpan(r0, r12, r6, r11)     // Catch:{ Exception -> 0x0b34 }
            r0 = r6
            goto L_0x0b3f
        L_0x0b34:
            r0 = move-exception
            goto L_0x0b38
        L_0x0b36:
            r0 = move-exception
            r6 = 0
        L_0x0b38:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0b3f
        L_0x0b3d:
            r0 = 0
            r6 = 0
        L_0x0b3f:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r11 = r11[r12]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r13 = 0
            java.lang.CharSequence r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r11, r12, r13)
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.hasHighlightedWords()
            if (r11 == 0) goto L_0x0b65
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r4, (java.util.ArrayList<java.lang.String>) r11)
            if (r11 == 0) goto L_0x0b65
            r4 = r11
        L_0x0b65:
            boolean r11 = r1.hasMessageThumb
            if (r11 == 0) goto L_0x0b8e
            boolean r11 = r4 instanceof android.text.SpannableStringBuilder
            if (r11 != 0) goto L_0x0b73
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r11.<init>(r4)
            r4 = r11
        L_0x0b73:
            r11 = r4
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r11.insert(r6, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r13 = r7 + 6
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.<init>(r13)
            int r13 = r6 + 1
            r14 = 33
            r11.setSpan(r12, r6, r13, r14)
        L_0x0b8e:
            r12 = r0
            r0 = r4
            r6 = 1
            r11 = 0
            r4 = r2
            r2 = 2
            goto L_0x0de2
        L_0x0b96:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0bb6
            org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r2 == 0) goto L_0x0bb6
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0bb6
            r0 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0bb3:
            r2 = 2
            goto L_0x0d47
        L_0x0bb6:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x0bd4
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r2 == 0) goto L_0x0bd4
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0bd4
            r0 = 2131624358(0x7f0e01a6, float:1.8875893E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0bb3
        L_0x0bd4:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r2 = r0.caption
            if (r2 == 0) goto L_0x0c8c
            if (r4 != 0) goto L_0x0bde
            r0 = r3
            goto L_0x0c0f
        L_0x0bde:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0be8
            java.lang.String r0 = "📹 "
            goto L_0x0c0f
        L_0x0be8:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0bf4
            java.lang.String r0 = "🎤 "
            goto L_0x0c0f
        L_0x0bf4:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "🎧 "
            goto L_0x0c0f
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0c0c
            java.lang.String r0 = "🖼 "
            goto L_0x0c0f
        L_0x0c0c:
            java.lang.String r0 = "📎 "
        L_0x0c0f:
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r4 = 32
            r6 = 10
            java.lang.String r2 = r2.replace(r6, r4)
            java.lang.String r2 = r2.trim()
            int r4 = r46.getMeasuredWidth()
            r6 = 1122893824(0x42ee0000, float:119.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            if (r12 == 0) goto L_0x0CLASSNAME
            r6 = 0
            boolean r11 = android.text.TextUtils.isEmpty(r6)
            if (r11 != 0) goto L_0x0CLASSNAME
            float r4 = (float) r4
            java.lang.String r11 = r6.toString()
            float r6 = r9.measureText(r11)
            float r4 = r4 - r6
            int r4 = (int) r4
        L_0x0CLASSNAME:
            float r4 = (float) r4
            java.lang.String r6 = ": "
            float r6 = r9.measureText(r6)
            float r4 = r4 - r6
            int r4 = (int) r4
        L_0x0CLASSNAME:
            if (r4 <= 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r11 = 0
            java.lang.Object r6 = r6.get(r11)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r6, r4, r9)
            java.lang.String r2 = r2.toString()
        L_0x0CLASSNAME:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            goto L_0x0bb3
        L_0x0CLASSNAME:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0bb3
        L_0x0c8c:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0caf
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "📊 "
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0cac:
            r2 = 2
            goto L_0x0d33
        L_0x0caf:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0cd0
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
            goto L_0x0cac
        L_0x0cd0:
            int r2 = r0.type
            r4 = 14
            if (r2 != r4) goto L_0x0cf0
            r2 = 2
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r6 = 0
            r4[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r4[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r4)
            goto L_0x0d33
        L_0x0cf0:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0d2f
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r4)
            java.lang.String r0 = r0.trim()
            int r4 = r46.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r11 = 0
            java.lang.Object r6 = r6.get(r11)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r4, r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4)
            goto L_0x0d33
        L_0x0d2f:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0d33:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            if (r6 == 0) goto L_0x0d47
            boolean r4 = r4.isMediaEmpty()
            if (r4 != 0) goto L_0x0d47
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r6 = r1.paintIndex
            r9 = r4[r6]
        L_0x0d47:
            boolean r4 = r1.hasMessageThumb
            if (r4 == 0) goto L_0x07c5
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0d87
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r4)
            java.lang.String r0 = r0.trim()
            int r4 = r46.getMeasuredWidth()
            int r6 = r7 + 95
            int r6 = r6 + 6
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r11 = 0
            java.lang.Object r6 = r6.get(r11)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r6, r4, r9)
            java.lang.String r0 = r0.toString()
            goto L_0x0d96
        L_0x0d87:
            r11 = 0
            int r4 = r0.length()
            if (r4 <= r8) goto L_0x0d92
            java.lang.CharSequence r0 = r0.subSequence(r11, r8)
        L_0x0d92:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0d96:
            boolean r4 = r0 instanceof android.text.SpannableStringBuilder
            if (r4 != 0) goto L_0x0da0
            android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
            r4.<init>(r0)
            r0 = r4
        L_0x0da0:
            r4 = r0
            android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
            java.lang.String r6 = " "
            r11 = 0
            r4.insert(r11, r6)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r6 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r12 = r7 + 6
            float r12 = (float) r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.<init>(r12)
            r12 = 33
            r4.setSpan(r6, r11, r5, r12)
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r6 = r6[r12]
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r12 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r13, r11)
            org.telegram.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.hasHighlightedWords()
            if (r6 == 0) goto L_0x0795
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r4, (java.util.ArrayList<java.lang.String>) r6)
            if (r4 == 0) goto L_0x0795
            r0 = r4
            goto L_0x0795
        L_0x0de2:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0dea
            java.lang.CharSequence r4 = r46.formatArchivedDialogNames()
        L_0x0dea:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0df6
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e0f
        L_0x0df6:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0e00
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e0f
        L_0x0e00:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0e0e
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e0f
        L_0x0e0e:
            r13 = r3
        L_0x0e0f:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0e25
            r15 = 0
            r1.drawCheck1 = r15
            r1.drawCheck2 = r15
            r1.drawClock = r15
            r1.drawCount = r15
            r1.drawMention = r15
            r1.drawError = r15
            r2 = 0
            r8 = 0
        L_0x0e22:
            r10 = 0
            goto L_0x0var_
        L_0x0e25:
            r15 = 0
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0e65
            int r2 = r1.unreadCount
            int r14 = r1.mentionCount
            int r19 = r2 + r14
            if (r19 <= 0) goto L_0x0e5e
            if (r2 <= r14) goto L_0x0e48
            r1.drawCount = r5
            r1.drawMention = r15
            java.lang.Object[] r8 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r8[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r8)
            goto L_0x0e63
        L_0x0e48:
            r1.drawCount = r15
            r1.drawMention = r5
            java.lang.Object[] r8 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r8[r15] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r8)
            r8 = r2
            r2 = 0
            goto L_0x0ead
        L_0x0e5e:
            r1.drawCount = r15
            r1.drawMention = r15
            r2 = 0
        L_0x0e63:
            r8 = 0
            goto L_0x0ead
        L_0x0e65:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0e6f
            r1.drawCount = r15
            r2 = 0
            r10 = 0
            r14 = 0
            goto L_0x0ea1
        L_0x0e6f:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0e95
            if (r2 != r5) goto L_0x0e81
            int r8 = r1.mentionCount
            if (r2 != r8) goto L_0x0e81
            if (r14 == 0) goto L_0x0e81
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            boolean r2 = r2.mentioned
            if (r2 != 0) goto L_0x0e95
        L_0x0e81:
            r1.drawCount = r5
            java.lang.Object[] r2 = new java.lang.Object[r5]
            int r8 = r1.unreadCount
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r14 = 0
            r2[r14] = r8
            java.lang.String r8 = "%d"
            java.lang.String r2 = java.lang.String.format(r8, r2)
            goto L_0x0ea1
        L_0x0e95:
            r14 = 0
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0e9e
            r1.drawCount = r5
            r2 = r3
            goto L_0x0ea1
        L_0x0e9e:
            r1.drawCount = r14
            r2 = 0
        L_0x0ea1:
            int r8 = r1.mentionCount
            if (r8 == 0) goto L_0x0eaa
            r1.drawMention = r5
            java.lang.String r8 = "@"
            goto L_0x0ead
        L_0x0eaa:
            r1.drawMention = r14
            goto L_0x0e63
        L_0x0ead:
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
            if (r10 == 0) goto L_0x0ed5
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r5
            r1.drawError = r10
            goto L_0x0var_
        L_0x0ed5:
            r10 = 0
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x0eeb
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r5
            r1.drawCount = r10
            r1.drawMention = r10
            goto L_0x0var_
        L_0x0eeb:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x0e22
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x0f0c
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0f0a
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0f0a
            goto L_0x0f0c
        L_0x0f0a:
            r10 = 0
            goto L_0x0f0d
        L_0x0f0c:
            r10 = 1
        L_0x0f0d:
            r1.drawCheck1 = r10
            r1.drawCheck2 = r5
            r10 = 0
            r1.drawClock = r10
            r1.drawError = r10
            goto L_0x0var_
        L_0x0var_:
            r10 = 0
            r1.drawCheck1 = r10
            r1.drawCheck2 = r10
            r1.drawClock = r10
            r1.drawError = r10
        L_0x0var_:
            r1.promoDialog = r10
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r14 = r1.dialogsType
            if (r14 != 0) goto L_0x0f7c
            long r14 = r1.currentDialogId
            boolean r14 = r10.isPromoDialog(r14, r5)
            if (r14 == 0) goto L_0x0f7c
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r14 = r10.promoDialogType
            if (r14 != 0) goto L_0x0var_
            r10 = 2131627369(0x7f0e0d69, float:1.8882E38)
            java.lang.String r13 = "UseProxySponsor"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r13 = r10
            goto L_0x0f7c
        L_0x0var_:
            if (r14 != r5) goto L_0x0f7c
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "PsaType_"
            r13.append(r14)
            java.lang.String r14 = r10.promoPsaType
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13)
            boolean r14 = android.text.TextUtils.isEmpty(r13)
            if (r14 == 0) goto L_0x0f6f
            r13 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            java.lang.String r14 = "PsaTypeDefault"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
        L_0x0f6f:
            java.lang.String r14 = r10.promoPsaMessage
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x0f7c
            java.lang.String r0 = r10.promoPsaMessage
            r10 = 0
            r1.hasMessageThumb = r10
        L_0x0f7c:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x0var_
            r10 = 2131624267(0x7f0e014b, float:1.8875709E38)
            java.lang.String r14 = "ArchivedChats"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
        L_0x0var_:
            r14 = r9
            r23 = r11
            r15 = r12
            r12 = r2
            r9 = r8
            r2 = r0
            goto L_0x0feb
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x0var_
            java.lang.String r10 = r10.title
            goto L_0x0fdb
        L_0x0var_:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            if (r10 == 0) goto L_0x0fda
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r10)
            if (r10 == 0) goto L_0x0fac
            r10 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.String r14 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x0fdb
        L_0x0fac:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x0fd3
            boolean r10 = r1.useMeForMyMessages
            if (r10 == 0) goto L_0x0fc2
            r10 = 2131625495(0x7f0e0617, float:1.88782E38)
            java.lang.String r14 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x0fdb
        L_0x0fc2:
            int r10 = r1.dialogsType
            r14 = 3
            if (r10 != r14) goto L_0x0fc9
            r1.drawPinBackground = r5
        L_0x0fc9:
            r10 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            java.lang.String r14 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x0fdb
        L_0x0fd3:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x0fdb
        L_0x0fda:
            r10 = r3
        L_0x0fdb:
            int r14 = r10.length()
            if (r14 != 0) goto L_0x0var_
            r10 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r14 = "HiddenName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            goto L_0x0var_
        L_0x0feb:
            if (r6 == 0) goto L_0x102c
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r13)
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
            r25 = r13
            r27 = r0
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.timeLayout = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1023
            int r5 = r46.getMeasuredWidth()
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x1033
        L_0x1023:
            r5 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeLeft = r5
            goto L_0x1033
        L_0x102c:
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r0 = 0
        L_0x1033:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1047
            int r5 = r46.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            goto L_0x105b
        L_0x1047:
            int r5 = r46.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r5 = r5 - r6
            r6 = 1117388800(0x429a0000, float:77.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            int r6 = r1.nameLeft
            int r6 = r6 + r0
            r1.nameLeft = r6
        L_0x105b:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x106e
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
        L_0x106b:
            int r6 = r6 + r8
            int r5 = r5 - r6
            goto L_0x10a1
        L_0x106e:
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x107f
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x106b
        L_0x107f:
            boolean r6 = r1.drawNameBroadcast
            if (r6 == 0) goto L_0x1090
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x106b
        L_0x1090:
            boolean r6 = r1.drawNameBot
            if (r6 == 0) goto L_0x10a1
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r8 = r8.getIntrinsicWidth()
            goto L_0x106b
        L_0x10a1:
            boolean r6 = r1.drawClock
            r8 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x10cf
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r11
            int r5 = r5 - r6
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x10be
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x1144
        L_0x10be:
            int r11 = r1.timeLeft
            int r11 = r11 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r0
            r1.checkDrawLeft = r11
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
            goto L_0x1144
        L_0x10cf:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x1144
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r11
            int r5 = r5 - r6
            boolean r11 = r1.drawCheck1
            if (r11 == 0) goto L_0x112b
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r11 = r11.getIntrinsicWidth()
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r11 = r11 - r13
            int r5 = r5 - r11
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x1104
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.halfCheckDrawLeft = r0
            r6 = 1085276160(0x40b00000, float:5.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x1144
        L_0x1104:
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
            goto L_0x1144
        L_0x112b:
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 != 0) goto L_0x1135
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.checkDrawLeft = r0
            goto L_0x1144
        L_0x1135:
            int r11 = r1.timeLeft
            int r11 = r11 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r11 = r11 + r0
            r1.checkDrawLeft = r11
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x1144:
            boolean r0 = r1.dialogMuted
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x1168
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x1168
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x1168
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x119b
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
            goto L_0x119b
        L_0x1168:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x1182
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x119b
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
            goto L_0x119b
        L_0x1182:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x119b
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.ScamDrawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 + r8
            int r5 = r5 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x119b
            int r8 = r1.nameLeft
            int r8 = r8 + r0
            r1.nameLeft = r8
        L_0x119b:
            r33 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r5 = java.lang.Math.max(r0, r5)
            r8 = 32
            r11 = 10
            java.lang.String r0 = r10.replace(r11, r8)     // Catch:{ Exception -> 0x11f5 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x11f5 }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x11f5 }
            r8 = r8[r10]     // Catch:{ Exception -> 0x11f5 }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r33)     // Catch:{ Exception -> 0x11f5 }
            int r10 = r5 - r10
            float r10 = (float) r10     // Catch:{ Exception -> 0x11f5 }
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x11f5 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r8, r10, r11)     // Catch:{ Exception -> 0x11f5 }
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x11f5 }
            if (r8 == 0) goto L_0x11d9
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x11f5 }
            boolean r8 = r8.hasHighlightedWords()     // Catch:{ Exception -> 0x11f5 }
            if (r8 == 0) goto L_0x11d9
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x11f5 }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x11f5 }
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8)     // Catch:{ Exception -> 0x11f5 }
            if (r8 == 0) goto L_0x11d9
            r25 = r8
            goto L_0x11db
        L_0x11d9:
            r25 = r0
        L_0x11db:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x11f5 }
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x11f5 }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x11f5 }
            r26 = r8[r10]     // Catch:{ Exception -> 0x11f5 }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x11f5 }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r27 = r5
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x11f5 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x11f5 }
            goto L_0x11f9
        L_0x11f5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x11f9:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x12ac
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1203
            goto L_0x12ac
        L_0x1203:
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
            int r8 = r46.getMeasuredWidth()
            r10 = 1119748096(0x42be0000, float:95.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r8 - r10
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x1265
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.messageNameLeft = r10
            r1.messageLeft = r10
            int r10 = r46.getMeasuredWidth()
            r11 = 1115684864(0x42800000, float:64.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            int r11 = r7 + 11
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r10 - r11
            goto L_0x127a
        L_0x1265:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r10
            r1.messageLeft = r10
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r11 = 1116078080(0x42860000, float:67.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r10
        L_0x127a:
            org.telegram.messenger.ImageReceiver r13 = r1.avatarImage
            float r10 = (float) r10
            float r6 = (float) r0
            r17 = 1113063424(0x42580000, float:54.0)
            r20 = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            r22 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r8 = (float) r8
            r13.setImageCoords(r10, r6, r3, r8)
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            float r6 = (float) r11
            r8 = 1106247680(0x41var_, float:30.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r8 + r0
            float r8 = (float) r8
            float r10 = (float) r7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r11 = (float) r11
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r3.setImageCoords(r6, r8, r11, r10)
            goto L_0x1356
        L_0x12ac:
            r20 = r3
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3 = 1107296256(0x42000000, float:32.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.messageNameTop = r3
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.timeTop = r3
            r3 = 1110179840(0x422CLASSNAME, float:43.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.errorTop = r3
            r3 = 1110179840(0x422CLASSNAME, float:43.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.pinTop = r3
            r3 = 1110179840(0x422CLASSNAME, float:43.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.countTop = r3
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.checkDrawTop = r3
            int r3 = r46.getMeasuredWidth()
            r6 = 1119485952(0x42ba0000, float:93.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r3 - r6
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x1312
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            int r3 = r46.getMeasuredWidth()
            r6 = 1115947008(0x42840000, float:66.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r3 - r6
            goto L_0x1327
        L_0x1312:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            r3 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6 = 1116340224(0x428a0000, float:69.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r3
        L_0x1327:
            org.telegram.messenger.ImageReceiver r10 = r1.avatarImage
            float r3 = (float) r3
            float r11 = (float) r0
            r13 = 1113587712(0x42600000, float:56.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r17 = 1113587712(0x42600000, float:56.0)
            r22 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r8 = (float) r8
            r10.setImageCoords(r3, r11, r13, r8)
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            float r6 = (float) r6
            r8 = 1106771968(0x41var_, float:31.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r8 + r0
            float r8 = (float) r8
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r10 = (float) r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r21)
            float r11 = (float) r11
            r3.setImageCoords(r6, r8, r10, r11)
        L_0x1356:
            r3 = r0
            r8 = r22
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x137e
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1376
            int r0 = r46.getMeasuredWidth()
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r0 = r0 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.pinLeft = r0
            goto L_0x137e
        L_0x1376:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x137e:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x13b3
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x139b
            int r0 = r46.getMeasuredWidth()
            r6 = 1107820544(0x42080000, float:34.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            r1.errorLeft = r0
            goto L_0x13ad
        L_0x139b:
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.errorLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x13ad:
            r34 = r14
            r35 = r15
            goto L_0x14d2
        L_0x13b3:
            if (r12 != 0) goto L_0x13de
            if (r9 == 0) goto L_0x13b8
            goto L_0x13de
        L_0x13b8:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x13d8
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r6
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x13d8
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x13d8:
            r6 = 0
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x13ad
        L_0x13de:
            if (r12 == 0) goto L_0x143e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r6 = r6.measureText(r12)
            double r10 = (double) r6
            double r10 = java.lang.Math.ceil(r10)
            int r6 = (int) r10
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
            r25 = r12
            r27 = r6
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 + r6
            int r8 = r8 - r0
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x142a
            int r0 = r46.getMeasuredWidth()
            int r6 = r1.countWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 - r6
            r1.countLeft = r0
            goto L_0x143a
        L_0x142a:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r1.countLeft = r6
            int r6 = r1.messageLeft
            int r6 = r6 + r0
            r1.messageLeft = r6
            int r6 = r1.messageNameLeft
            int r6 = r6 + r0
            r1.messageNameLeft = r6
        L_0x143a:
            r6 = 1
            r1.drawCount = r6
            goto L_0x1441
        L_0x143e:
            r6 = 0
            r1.countWidth = r6
        L_0x1441:
            r0 = r8
            if (r9 == 0) goto L_0x14cd
            int r6 = r1.currentDialogFolderId
            if (r6 == 0) goto L_0x147b
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
            r17 = 0
            r22 = 0
            r8 = r6
            r34 = r14
            r14 = r17
            r35 = r15
            r15 = r22
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r1.mentionLayout = r6
            goto L_0x1485
        L_0x147b:
            r34 = r14
            r35 = r15
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.mentionWidth = r6
        L_0x1485:
            int r6 = r1.mentionWidth
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r8
            int r8 = r0 - r6
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x14ad
            int r0 = r46.getMeasuredWidth()
            int r6 = r1.mentionWidth
            int r0 = r0 - r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 - r6
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x14a8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r9
            goto L_0x14a9
        L_0x14a8:
            r6 = 0
        L_0x14a9:
            int r0 = r0 - r6
            r1.mentionLeft = r0
            goto L_0x14c9
        L_0x14ad:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x14bb
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r9 = r9 + r10
            goto L_0x14bc
        L_0x14bb:
            r9 = 0
        L_0x14bc:
            int r0 = r0 + r9
            r1.mentionLeft = r0
            int r0 = r1.messageLeft
            int r0 = r0 + r6
            r1.messageLeft = r0
            int r0 = r1.messageNameLeft
            int r0 = r0 + r6
            r1.messageNameLeft = r0
        L_0x14c9:
            r6 = 1
            r1.drawMention = r6
            goto L_0x14d2
        L_0x14cd:
            r34 = r14
            r35 = r15
            r8 = r0
        L_0x14d2:
            if (r23 == 0) goto L_0x1526
            if (r2 != 0) goto L_0x14d8
            r2 = r20
        L_0x14d8:
            java.lang.String r0 = r2.toString()
            int r2 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r2 <= r6) goto L_0x14e9
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r6)
        L_0x14e9:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x14f1
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x14f3
        L_0x14f1:
            if (r4 == 0) goto L_0x14fc
        L_0x14f3:
            r2 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r2)
            goto L_0x1504
        L_0x14fc:
            java.lang.String r2 = "\n\n"
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r2, r6)
        L_0x1504:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r6 = r1.paintIndex
            r2 = r2[r6]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r6, r9)
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x1526
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0)
            if (r0 == 0) goto L_0x1526
            r2 = r0
        L_0x1526:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r6 = java.lang.Math.max(r0, r8)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1536
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x158a
        L_0x1536:
            if (r4 == 0) goto L_0x158a
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1541
            int r0 = r1.currentDialogFolderDialogsCount
            r8 = 1
            if (r0 != r8) goto L_0x158a
        L_0x1541:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x156f }
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x156f }
            if (r0 == 0) goto L_0x1554
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x156f }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x156f }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r4, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x156f }
            if (r0 == 0) goto L_0x1554
            r4 = r0
        L_0x1554:
            android.text.TextPaint r37 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x156f }
            android.text.Layout$Alignment r39 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x156f }
            r40 = 1065353216(0x3var_, float:1.0)
            r41 = 0
            r42 = 0
            android.text.TextUtils$TruncateAt r43 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x156f }
            r45 = 1
            r36 = r4
            r38 = r6
            r44 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)     // Catch:{ Exception -> 0x156f }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x156f }
            goto L_0x1573
        L_0x156f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1573:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r8 = 1109393408(0x42200000, float:40.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 + r8
            float r3 = (float) r3
            r0.setImageY(r3)
            r8 = 0
            goto L_0x15b4
        L_0x158a:
            r8 = 0
            r1.messageNameLayout = r8
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x159f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1596
            goto L_0x159f
        L_0x1596:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x15b4
        L_0x159f:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r9 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r3 = r3 + r9
            float r3 = (float) r3
            r0.setImageY(r3)
        L_0x15b4:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x165a }
            if (r0 != 0) goto L_0x15bc
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x15d0
        L_0x15bc:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x15d0
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x165a }
            r3 = 1
            if (r0 <= r3) goto L_0x15d0
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x165a }
            int r2 = r1.paintIndex     // Catch:{ Exception -> 0x165a }
            r14 = r0[r2]     // Catch:{ Exception -> 0x165a }
            r36 = r4
            r37 = r14
            goto L_0x15f1
        L_0x15d0:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x165a }
            if (r0 != 0) goto L_0x15d8
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x15da
        L_0x15d8:
            if (r4 == 0) goto L_0x15ea
        L_0x15da:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r33)     // Catch:{ Exception -> 0x165a }
            int r0 = r6 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x165a }
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x165a }
            r9 = r34
            java.lang.CharSequence r2 = android.text.TextUtils.ellipsize(r2, r9, r0, r3)     // Catch:{ Exception -> 0x165a }
            goto L_0x15ec
        L_0x15ea:
            r9 = r34
        L_0x15ec:
            r36 = r2
            r8 = r4
            r37 = r9
        L_0x15f1:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x165a }
            if (r0 != 0) goto L_0x162a
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x15fa
            goto L_0x162a
        L_0x15fa:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x1614
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x165a }
            int r0 = r0 + r7
            int r6 = r6 + r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x1614
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x165a }
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x165a }
            int r7 = r7 + r3
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x165a }
        L_0x1614:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x165a }
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x165a }
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r0
            r11 = r36
            r12 = r37
            r13 = r6
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x165a }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x165a }
            goto L_0x165e
        L_0x162a:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x165a }
            if (r0 == 0) goto L_0x1637
            if (r8 == 0) goto L_0x1637
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x165a }
            int r6 = r6 + r0
        L_0x1637:
            android.text.Layout$Alignment r39 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x165a }
            r40 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x165a }
            float r0 = (float) r0     // Catch:{ Exception -> 0x165a }
            r42 = 0
            android.text.TextUtils$TruncateAt r43 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x165a }
            if (r8 == 0) goto L_0x164b
            r45 = 1
            goto L_0x164d
        L_0x164b:
            r45 = 2
        L_0x164d:
            r38 = r6
            r41 = r0
            r44 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)     // Catch:{ Exception -> 0x165a }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x165a }
            goto L_0x165e
        L_0x165a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x165e:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1792
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x171b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x171b
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            boolean r4 = r1.dialogMuted
            if (r4 == 0) goto L_0x16b0
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x16b0
            boolean r4 = r1.drawScam
            if (r4 != 0) goto L_0x16b0
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
            goto L_0x1703
        L_0x16b0:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x16da
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
            goto L_0x1703
        L_0x16da:
            boolean r4 = r1.drawScam
            if (r4 == 0) goto L_0x1703
            int r4 = r1.nameLeft
            double r7 = (double) r4
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r4 = r4.getIntrinsicWidth()
            double r9 = (double) r4
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r4 = (int) r7
            r1.nameMuteLeft = r4
        L_0x1703:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x171b
            double r4 = (double) r5
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x171b
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r4
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x171b:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x175c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x175c
            r2 = 2147483647(0x7fffffff, float:NaN)
            r2 = 0
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x172c:
            if (r2 >= r0) goto L_0x1752
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r2)
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x1751
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
            goto L_0x172c
        L_0x1751:
            r3 = 0
        L_0x1752:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r0) goto L_0x175c
            int r0 = r1.messageLeft
            int r0 = r0 + r3
            r1.messageLeft = r0
        L_0x175c:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x181c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x181c
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x181c
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r2)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x181c
            int r0 = r1.messageNameLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r4
            int r0 = (int) r6
            r1.messageNameLeft = r0
            goto L_0x181c
        L_0x1792:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x17e1
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17e1
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r5
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x17c6
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r5
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x17c6
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x17c6:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x17d2
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x17d2
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x17e1
        L_0x17d2:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x17e1:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1804
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1804
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r6 = 0
        L_0x17ee:
            if (r6 >= r0) goto L_0x17fd
            android.text.StaticLayout r3 = r1.messageLayout
            float r3 = r3.getLineLeft(r6)
            float r2 = java.lang.Math.min(r2, r3)
            int r6 = r6 + 1
            goto L_0x17ee
        L_0x17fd:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1804:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x181c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x181c
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x181c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1862
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1862
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x185e }
            int r0 = r0.length()     // Catch:{ Exception -> 0x185e }
            r12 = r35
            r2 = 1
            if (r12 < r0) goto L_0x1834
            int r15 = r0 + -1
            goto L_0x1835
        L_0x1834:
            r15 = r12
        L_0x1835:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x185e }
            float r0 = r0.getPrimaryHorizontal(r15)     // Catch:{ Exception -> 0x185e }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x185e }
            int r15 = r15 + r2
            float r2 = r3.getPrimaryHorizontal(r15)     // Catch:{ Exception -> 0x185e }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x185e }
            double r2 = (double) r0     // Catch:{ Exception -> 0x185e }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x185e }
            int r0 = (int) r2     // Catch:{ Exception -> 0x185e }
            if (r0 == 0) goto L_0x1855
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x185e }
            int r0 = r0 + r2
        L_0x1855:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x185e }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x185e }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x185e }
            goto L_0x1862
        L_0x185e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1862:
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
            TLRPC$DraftMessage draft = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId, 0);
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

    /* JADX WARNING: Removed duplicated region for block: B:116:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x020e  */
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
            if (r2 == 0) goto L_0x0041
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
            org.telegram.messenger.ImageReceiver r1 = r0.thumbImage
            r1.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x037f
        L_0x0041:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0105
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC$Dialog) r2
            if (r2 == 0) goto L_0x00fa
            if (r1 != 0) goto L_0x0107
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
            if (r6 == 0) goto L_0x0083
            boolean r6 = r6.isUnread()
            if (r6 == 0) goto L_0x0083
            r6 = 1
            goto L_0x0084
        L_0x0083:
            r6 = 0
        L_0x0084:
            r0.lastUnreadState = r6
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r6 == 0) goto L_0x0099
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r6 = org.telegram.messenger.MessagesStorage.getInstance(r6)
            int r6 = r6.getArchiveUnreadCount()
            r0.unreadCount = r6
            r0.mentionCount = r5
            goto L_0x00a1
        L_0x0099:
            int r6 = r2.unread_count
            r0.unreadCount = r6
            int r6 = r2.unread_mentions_count
            r0.mentionCount = r6
        L_0x00a1:
            boolean r6 = r2.unread_mark
            r0.markUnread = r6
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x00ae
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.edit_date
            goto L_0x00af
        L_0x00ae:
            r6 = 0
        L_0x00af:
            r0.currentEditDate = r6
            int r6 = r2.last_message_date
            r0.lastMessageDate = r6
            int r6 = r0.dialogsType
            r7 = 7
            r8 = 8
            if (r6 == r7) goto L_0x00cd
            if (r6 != r8) goto L_0x00bf
            goto L_0x00cd
        L_0x00bf:
            int r6 = r0.currentDialogFolderId
            if (r6 != 0) goto L_0x00c9
            boolean r2 = r2.pinned
            if (r2 == 0) goto L_0x00c9
            r2 = 1
            goto L_0x00ca
        L_0x00c9:
            r2 = 0
        L_0x00ca:
            r0.drawPin = r2
            goto L_0x00ef
        L_0x00cd:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.messenger.MessagesController$DialogFilter[] r6 = r6.selectedDialogFilter
            int r7 = r0.dialogsType
            if (r7 != r8) goto L_0x00db
            r7 = 1
            goto L_0x00dc
        L_0x00db:
            r7 = 0
        L_0x00dc:
            r6 = r6[r7]
            if (r6 == 0) goto L_0x00ec
            android.util.LongSparseArray<java.lang.Integer> r6 = r6.pinnedDialogs
            long r7 = r2.id
            int r2 = r6.indexOfKey(r7)
            if (r2 < 0) goto L_0x00ec
            r2 = 1
            goto L_0x00ed
        L_0x00ec:
            r2 = 0
        L_0x00ed:
            r0.drawPin = r2
        L_0x00ef:
            org.telegram.messenger.MessageObject r2 = r0.message
            if (r2 == 0) goto L_0x0107
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            int r2 = r2.send_state
            r0.lastSendState = r2
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
            if (r1 == 0) goto L_0x0212
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x0128
            r2 = r1 & 4
            if (r2 == 0) goto L_0x0128
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            int r6 = r6.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            r19.invalidate()
        L_0x0128:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0156
            r2 = r1 & 64
            if (r2 == 0) goto L_0x0156
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r6 = r0.currentDialogId
            java.lang.CharSequence r2 = r2.getPrintingString(r6, r5)
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0142
            if (r2 == 0) goto L_0x0154
        L_0x0142:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 != 0) goto L_0x0148
            if (r2 != 0) goto L_0x0154
        L_0x0148:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0156
            if (r2 == 0) goto L_0x0156
            boolean r2 = r6.equals(r2)
            if (r2 != 0) goto L_0x0156
        L_0x0154:
            r2 = 1
            goto L_0x0157
        L_0x0156:
            r2 = 0
        L_0x0157:
            if (r2 != 0) goto L_0x016a
            r6 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r1
            if (r6 == 0) goto L_0x016a
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x016a
            java.lang.CharSequence r6 = r6.messageText
            java.lang.CharSequence r7 = r0.lastMessageString
            if (r6 == r7) goto L_0x016a
            r2 = 1
        L_0x016a:
            if (r2 != 0) goto L_0x0175
            r6 = r1 & 2
            if (r6 == 0) goto L_0x0175
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0175
            r2 = 1
        L_0x0175:
            if (r2 != 0) goto L_0x0180
            r6 = r1 & 1
            if (r6 == 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0180
            r2 = 1
        L_0x0180:
            if (r2 != 0) goto L_0x018b
            r6 = r1 & 8
            if (r6 == 0) goto L_0x018b
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x018b
            r2 = 1
        L_0x018b:
            if (r2 != 0) goto L_0x0196
            r6 = r1 & 16
            if (r6 == 0) goto L_0x0196
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x0196
            r2 = 1
        L_0x0196:
            if (r2 != 0) goto L_0x01f7
            r6 = r1 & 256(0x100, float:3.59E-43)
            if (r6 == 0) goto L_0x01f7
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x01b1
            boolean r7 = r0.lastUnreadState
            boolean r6 = r6.isUnread()
            if (r7 == r6) goto L_0x01b1
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isUnread()
            r0.lastUnreadState = r2
            r2 = 1
        L_0x01b1:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x01f7
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r7 == 0) goto L_0x01d5
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)
            int r7 = r7.getArchiveUnreadCount()
        L_0x01d3:
            r8 = 0
            goto L_0x01de
        L_0x01d5:
            if (r6 == 0) goto L_0x01dc
            int r7 = r6.unread_count
            int r8 = r6.unread_mentions_count
            goto L_0x01de
        L_0x01dc:
            r7 = 0
            goto L_0x01d3
        L_0x01de:
            if (r6 == 0) goto L_0x01f7
            int r9 = r0.unreadCount
            if (r9 != r7) goto L_0x01ee
            boolean r9 = r0.markUnread
            boolean r10 = r6.unread_mark
            if (r9 != r10) goto L_0x01ee
            int r9 = r0.mentionCount
            if (r9 == r8) goto L_0x01f7
        L_0x01ee:
            r0.unreadCount = r7
            r0.mentionCount = r8
            boolean r2 = r6.unread_mark
            r0.markUnread = r2
            r2 = 1
        L_0x01f7:
            if (r2 != 0) goto L_0x020c
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x020c
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x020c
            int r6 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r6 == r1) goto L_0x020c
            r0.lastSendState = r1
            r2 = 1
        L_0x020c:
            if (r2 != 0) goto L_0x0212
            r19.invalidate()
            return
        L_0x0212:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r2 = 0
            if (r1 == 0) goto L_0x022f
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r19.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x022d
            long r6 = r1.getDialogId()
            goto L_0x0248
        L_0x022d:
            r6 = r2
            goto L_0x0248
        L_0x022f:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x0243
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r6 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r6)
            if (r1 == 0) goto L_0x0243
            r1 = 1
            goto L_0x0244
        L_0x0243:
            r1 = 0
        L_0x0244:
            r0.dialogMuted = r1
            long r6 = r0.currentDialogId
        L_0x0248:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x02e9
            int r1 = (int) r6
            r2 = 32
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x029b
            if (r1 >= 0) goto L_0x028a
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            r0.chat = r1
            boolean r2 = r0.isDialogCell
            if (r2 != 0) goto L_0x02c1
            if (r1 == 0) goto L_0x02c1
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02c1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x02c1
            r0.chat = r1
            goto L_0x02c1
        L_0x028a:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            r0.user = r1
            goto L_0x02c1
        L_0x029b:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02c1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.encryptedChat
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02c1:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x02e9
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x02e9
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x02e9
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.clientUserId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02e9:
            int r1 = r0.currentDialogFolderId
            if (r1 == 0) goto L_0x0307
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
            goto L_0x037f
        L_0x0307:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x0363
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r1 == 0) goto L_0x032c
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 4
            r1.setAvatarType(r2)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r4 = 0
            r5 = 0
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            r9 = 0
            r3.setImage(r4, r5, r6, r7, r8, r9)
            goto L_0x037f
        L_0x032c:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x034b
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x034b
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
            goto L_0x037f
        L_0x034b:
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
            goto L_0x037f
        L_0x0363:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x037f
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
        L_0x037f:
            int r1 = r19.getMeasuredWidth()
            if (r1 != 0) goto L_0x0390
            int r1 = r19.getMeasuredHeight()
            if (r1 == 0) goto L_0x038c
            goto L_0x0390
        L_0x038c:
            r19.requestLayout()
            goto L_0x0393
        L_0x0390:
            r19.buildLayout()
        L_0x0393:
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
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0480  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x048f  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x055b  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x05a9  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0609  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0687  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0718  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0767  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x08b8  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x08eb  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0922  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x093d  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a2e  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0a46  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0aaa  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0b03  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0b16  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0b58  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b85  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0b99  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0bbf  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0bdf  */
    /* JADX WARNING: Removed duplicated region for block: B:476:? A[RETURN, SYNTHETIC] */
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
            goto L_0x0310
        L_0x0099:
            r25.save()
            int r0 = r1.currentDialogFolderId
            java.lang.String r16 = "chats_archivePinBackground"
            java.lang.String r17 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00d4
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x00be
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131627323(0x7f0e0d3b, float:1.8881907E38)
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x00be:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131625564(0x7f0e065c, float:1.887834E38)
            java.lang.String r4 = "HideOnTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x00d4:
            boolean r0 = r1.promoDialog
            if (r0 == 0) goto L_0x00f0
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131626654(0x7f0e0a9e, float:1.888055E38)
            java.lang.String r4 = "PsaHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.translationDrawable = r4
        L_0x00ed:
            r5 = r2
            r4 = r3
            goto L_0x0120
        L_0x00f0:
            int r0 = r1.folderId
            if (r0 != 0) goto L_0x010a
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131624252(0x7f0e013c, float:1.8875678E38)
            java.lang.String r4 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x010a:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131627314(0x7f0e0d32, float:1.8881889E38)
            java.lang.String r4 = "Unarchive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x0120:
            boolean r2 = r1.translationAnimationStarted
            r18 = 1110179840(0x422CLASSNAME, float:43.0)
            if (r2 != 0) goto L_0x0146
            float r2 = r1.translationX
            float r2 = java.lang.Math.abs(r2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0146
            r1.translationAnimationStarted = r7
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setProgress(r9)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.setCallback(r1)
            org.telegram.ui.Components.RLottieDrawable r2 = r1.translationDrawable
            r2.start()
        L_0x0146:
            int r2 = r24.getMeasuredWidth()
            float r2 = (float) r2
            float r3 = r1.translationX
            float r3 = r3 + r2
            float r2 = r1.currentRevealProgress
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x01cb
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
            if (r2 != 0) goto L_0x01d1
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r2 == 0) goto L_0x0199
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r4 = "Arrow.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r15
        L_0x0199:
            boolean r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r2 == 0) goto L_0x01d1
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r2.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r4 = "Line 1.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r4 = "Line 2.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r4 = "Line 3.**"
            r2.setLayerColor(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r2.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r15
            goto L_0x01d1
        L_0x01cb:
            r12 = r3
            r0 = r4
            r22 = r5
            r19 = 1
        L_0x01d1:
            int r2 = r24.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 - r3
            org.telegram.ui.Components.RLottieDrawable r3 = r1.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r14
            int r2 = r2 - r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x01ee
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x01eb
            goto L_0x01ee
        L_0x01eb:
            r3 = 1091567616(0x41100000, float:9.0)
            goto L_0x01f0
        L_0x01ee:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x01f0:
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
            if (r6 <= 0) goto L_0x0295
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
            if (r4 != 0) goto L_0x0264
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r6 = "Arrow.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r19
        L_0x0264:
            boolean r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r4 != 0) goto L_0x0295
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r4.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r6 = "Line 1.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r6 = "Line 2.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r5 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r6 = "Line 3.**"
            r4.setLayerColor(r6, r5)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r4.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r19
        L_0x0295:
            r25.save()
            float r2 = (float) r2
            float r3 = (float) r3
            r8.translate(r2, r3)
            float r2 = r1.currentRevealBounceProgress
            int r3 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            r13 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x02c3
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x02c3
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
        L_0x02c3:
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
            if (r3 != 0) goto L_0x0301
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x02fe
            goto L_0x0301
        L_0x02fe:
            r3 = 1114374144(0x426CLASSNAME, float:59.0)
            goto L_0x0303
        L_0x0301:
            r3 = 1115160576(0x42780000, float:62.0)
        L_0x0303:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r8.drawText(r0, r2, r3, r4)
            r25.restore()
        L_0x0310:
            float r0 = r1.translationX
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x031e
            r25.save()
            float r0 = r1.translationX
            r8.translate(r0, r9)
        L_0x031e:
            boolean r0 = r1.isSelected
            if (r0 == 0) goto L_0x0335
            r3 = 0
            r4 = 0
            int r0 = r24.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r24.getMeasuredHeight()
            float r6 = (float) r0
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r2 = r25
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0335:
            int r0 = r1.currentDialogFolderId
            java.lang.String r12 = "chats_pinnedOverlay"
            if (r0 == 0) goto L_0x0368
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0345
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0368
        L_0x0345:
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
            goto L_0x038c
        L_0x0368:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x0370
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x038c
        L_0x0370:
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
        L_0x038c:
            float r0 = r1.translationX
            java.lang.String r16 = "windowBackgroundWhite"
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x03a0
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x039c
            goto L_0x03a0
        L_0x039c:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x044e
        L_0x03a0:
            r25.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
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
            if (r0 == 0) goto L_0x041b
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x03f0
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x041b
        L_0x03f0:
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
            goto L_0x0424
        L_0x041b:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x0427
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x0424
            goto L_0x0427
        L_0x0424:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x044b
        L_0x0427:
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
        L_0x044b:
            r25.restore()
        L_0x044e:
            float r0 = r1.translationX
            r3 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0468
            float r0 = r1.cornerProgress
            int r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x047b
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 + r4
            r1.cornerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0479
            r1.cornerProgress = r13
            goto L_0x0479
        L_0x0468:
            float r0 = r1.cornerProgress
            int r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x047b
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 - r4
            r1.cornerProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0479
            r1.cornerProgress = r9
        L_0x0479:
            r7 = 1
            goto L_0x047c
        L_0x047b:
            r7 = 0
        L_0x047c:
            boolean r0 = r1.drawNameLock
            if (r0 == 0) goto L_0x048f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r8)
            goto L_0x04c7
        L_0x048f:
            boolean r0 = r1.drawNameGroup
            if (r0 == 0) goto L_0x04a2
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r8)
            goto L_0x04c7
        L_0x04a2:
            boolean r0 = r1.drawNameBroadcast
            if (r0 == 0) goto L_0x04b5
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r8)
            goto L_0x04c7
        L_0x04b5:
            boolean r0 = r1.drawNameBot
            if (r0 == 0) goto L_0x04c7
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r8)
        L_0x04c7:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x053b
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x04e3
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0517
        L_0x04e3:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 != 0) goto L_0x0504
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 == 0) goto L_0x04f0
            int r0 = r0.type
            if (r0 != r14) goto L_0x04f0
            goto L_0x0504
        L_0x04f0:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_name"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0517
        L_0x0504:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_secretName"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x0517:
            r25.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0529
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0526
            goto L_0x0529
        L_0x0526:
            r5 = 1095761920(0x41500000, float:13.0)
            goto L_0x052b
        L_0x0529:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x052b:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r8)
            r25.restore()
        L_0x053b:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x0557
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x0557
            r25.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r5 = r1.timeTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r8)
            r25.restore()
        L_0x0557:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x05a5
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x056d
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessageArchived_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x058c
        L_0x056d:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x057f
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_draft"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x058c
        L_0x057f:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessage_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
        L_0x058c:
            r25.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r5 = r1.messageNameTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x059e }
            r0.draw(r8)     // Catch:{ Exception -> 0x059e }
            goto L_0x05a2
        L_0x059e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05a2:
            r25.restore()
        L_0x05a5:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0605
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x05d9
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x05c5
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameMessageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x05ec
        L_0x05c5:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_messageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x05ec
        L_0x05d9:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_message"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x05ec:
            r25.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r5 = r1.messageTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x05fe }
            r0.draw(r8)     // Catch:{ Exception -> 0x05fe }
            goto L_0x0602
        L_0x05fe:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0602:
            r25.restore()
        L_0x0605:
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x064f
            boolean r0 = r1.drawClock
            if (r0 == 0) goto L_0x061c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            r0.draw(r8)
            goto L_0x064f
        L_0x061c:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x064f
            boolean r0 = r1.drawCheck1
            if (r0 == 0) goto L_0x0641
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
            goto L_0x064f
        L_0x0641:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            r0.draw(r8)
        L_0x064f:
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x0687
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x0687
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x0687
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x066b
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0668
            goto L_0x066b
        L_0x0668:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x066c
        L_0x066b:
            r6 = 0
        L_0x066c:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0678
            r6 = 1096286208(0x41580000, float:13.5)
            goto L_0x067a
        L_0x0678:
            r6 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x067a:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r8)
            goto L_0x06ea
        L_0x0687:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x06c8
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x069b
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0698
            goto L_0x069b
        L_0x0698:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x069d
        L_0x069b:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x069d:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x06b4
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x06b1
            goto L_0x06b4
        L_0x06b1:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x06b6
        L_0x06b4:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x06b6:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r8)
            goto L_0x06ea
        L_0x06c8:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x06ea
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x06dc
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x06d9
            goto L_0x06dc
        L_0x06d9:
            r6 = 1097859072(0x41700000, float:15.0)
            goto L_0x06de
        L_0x06dc:
            r6 = 1094713344(0x41400000, float:12.0)
        L_0x06de:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r8)
        L_0x06ea:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x06f6
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x070e
        L_0x06f6:
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
        L_0x070e:
            boolean r0 = r1.drawError
            r6 = 1085276160(0x40b00000, float:5.5)
            r12 = 1102577664(0x41b80000, float:23.0)
            r17 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0767
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
            float r4 = r2 * r17
            float r2 = r2 * r17
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
            goto L_0x08b2
        L_0x0767:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x0790
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x0770
            goto L_0x0790
        L_0x0770:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x08b2
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
            goto L_0x08b2
        L_0x0790:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x0806
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x07a0
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x079d
            goto L_0x07a0
        L_0x079d:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x07a2
        L_0x07a0:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x07a2:
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
            int r3 = r1.countTop
            float r3 = (float) r3
            int r9 = r1.countWidth
            int r2 = r2 + r9
            r9 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r2 = r2 + r9
            float r2 = (float) r2
            int r9 = r1.countTop
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r9 = r9 + r23
            float r9 = (float) r9
            r4.set(r15, r3, r2, r9)
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r17
            float r3 = r3 * r17
            r8.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r1.countLayout
            if (r0 == 0) goto L_0x0806
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
        L_0x0806:
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x08b2
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
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r6 = r6 + r9
            float r6 = (float) r6
            r2.set(r3, r4, r0, r6)
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x0844
            int r0 = r1.folderId
            if (r0 == 0) goto L_0x0844
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0846
        L_0x0844:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0846:
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r17
            float r3 = r3 * r17
            r8.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r1.mentionLayout
            if (r0 == 0) goto L_0x087d
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
            goto L_0x08b2
        L_0x087d:
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
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r0.draw(r8)
        L_0x08b2:
            boolean r0 = r1.animatingArchiveAvatar
            r9 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x08d4
            r25.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r1.interpolator
            float r2 = r1.animatingArchiveAvatarProgress
            float r2 = r2 / r9
            float r0 = r0.getInterpolation(r2)
            float r0 = r0 + r13
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getCenterX()
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getCenterY()
            r8.scale(r0, r0, r2, r3)
        L_0x08d4:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x08e2
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x08e2
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x08e7
        L_0x08e2:
            org.telegram.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r8)
        L_0x08e7:
            boolean r0 = r1.hasMessageThumb
            if (r0 == 0) goto L_0x091e
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r0.draw(r8)
            boolean r0 = r1.drawPlay
            if (r0 == 0) goto L_0x091e
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            float r0 = r0.getCenterX()
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r14
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage
            float r2 = r2.getCenterY()
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r14
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r3, (int) r0, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            r0.draw(r8)
        L_0x091e:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0925
            r25.restore()
        L_0x0925:
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            if (r0 == 0) goto L_0x0a26
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x0a26
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x0a26
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0a26
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0a26
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x096b
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0953
            int r0 = r0.expires
            int r2 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0969
        L_0x0953:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x096b
        L_0x0969:
            r0 = 1
            goto L_0x096c
        L_0x096b:
            r0 = 0
        L_0x096c:
            if (r0 != 0) goto L_0x0975
            float r2 = r1.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0a26
        L_0x0975:
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r1.useForceThreeLines
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r3 != 0) goto L_0x0989
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0986
            goto L_0x0989
        L_0x0986:
            r18 = 1090519040(0x41000000, float:8.0)
            goto L_0x098b
        L_0x0989:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x098b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x09ad
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getImageX()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x09a4
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x09a6
        L_0x09a4:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x09a6:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 + r4
            goto L_0x09c3
        L_0x09ad:
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getImageX2()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x09bb
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x09bd
        L_0x09bb:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x09bd:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 - r4
        L_0x09c3:
            int r3 = (int) r3
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r16)
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
            if (r0 == 0) goto L_0x0a10
            float r0 = r1.onlineProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a26
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a24
            r1.onlineProgress = r13
            goto L_0x0a24
        L_0x0a10:
            float r0 = r1.onlineProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0a26
            float r3 = (float) r10
            r4 = 1125515264(0x43160000, float:150.0)
            float r3 = r3 / r4
            float r0 = r0 - r3
            r1.onlineProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a24
            r1.onlineProgress = r2
        L_0x0a24:
            r0 = 1
            goto L_0x0a27
        L_0x0a26:
            r0 = r7
        L_0x0a27:
            float r2 = r1.translationX
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0a31
            r25.restore()
        L_0x0a31:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0a42
            float r2 = r1.translationX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x0a42
            org.telegram.ui.Components.PullForegroundDrawable r2 = r1.archivedChatsDrawable
            if (r2 == 0) goto L_0x0a42
            r2.draw(r8)
        L_0x0a42:
            boolean r2 = r1.useSeparator
            if (r2 == 0) goto L_0x0aa3
            boolean r2 = r1.fullSeparator
            if (r2 != 0) goto L_0x0a66
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0a56
            boolean r2 = r1.archiveHidden
            if (r2 == 0) goto L_0x0a56
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x0a66
        L_0x0a56:
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x0a5f
            boolean r2 = r1.archiveHidden
            if (r2 != 0) goto L_0x0a5f
            goto L_0x0a66
        L_0x0a5f:
            r2 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x0a67
        L_0x0a66:
            r2 = 0
        L_0x0a67:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0a88
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
            goto L_0x0aa3
        L_0x0a88:
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
        L_0x0aa3:
            float r2 = r1.clipProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0af1
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 == r3) goto L_0x0ab4
            r25.restore()
            goto L_0x0af1
        L_0x0ab4:
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
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
        L_0x0af1:
            boolean r2 = r1.drawReorder
            if (r2 != 0) goto L_0x0aff
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0afd
            goto L_0x0aff
        L_0x0afd:
            r3 = 0
            goto L_0x0b2a
        L_0x0aff:
            boolean r2 = r1.drawReorder
            if (r2 == 0) goto L_0x0b16
            float r2 = r1.reorderIconProgress
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 >= 0) goto L_0x0afd
            float r0 = (float) r10
            float r0 = r0 / r9
            float r2 = r2 + r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b14
            r1.reorderIconProgress = r13
        L_0x0b14:
            r3 = 0
            goto L_0x0b28
        L_0x0b16:
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b2a
            float r0 = (float) r10
            float r0 = r0 / r9
            float r2 = r2 - r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b28
            r1.reorderIconProgress = r3
        L_0x0b28:
            r7 = 1
            goto L_0x0b2b
        L_0x0b2a:
            r7 = r0
        L_0x0b2b:
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x0b58
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b81
            float r2 = (float) r10
            r4 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r4
            float r0 = r0 - r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b42
            r1.archiveBackgroundProgress = r3
        L_0x0b42:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r14) goto L_0x0b80
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
            goto L_0x0b80
        L_0x0b58:
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b81
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b6b
            r1.archiveBackgroundProgress = r13
        L_0x0b6b:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r14) goto L_0x0b80
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
        L_0x0b80:
            r7 = 1
        L_0x0b81:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0b95
            float r0 = r1.animatingArchiveAvatarProgress
            float r2 = (float) r10
            float r0 = r0 + r2
            r1.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x0b94
            r1.animatingArchiveAvatarProgress = r9
            r2 = 0
            r1.animatingArchiveAvatar = r2
        L_0x0b94:
            r7 = 1
        L_0x0b95:
            boolean r0 = r1.drawRevealBackground
            if (r0 == 0) goto L_0x0bbf
            float r0 = r1.currentRevealBounceProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0bab
            float r2 = (float) r10
            float r2 = r2 / r9
            float r0 = r0 + r2
            r1.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0bab
            r1.currentRevealBounceProgress = r13
            r7 = 1
        L_0x0bab:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0bdd
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0bdc
            r1.currentRevealProgress = r13
            goto L_0x0bdc
        L_0x0bbf:
            float r0 = r1.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            r2 = 0
            if (r0 != 0) goto L_0x0bc9
            r1.currentRevealBounceProgress = r2
            r7 = 1
        L_0x0bc9:
            float r0 = r1.currentRevealProgress
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0bdd
            float r3 = (float) r10
            r4 = 1133903872(0x43960000, float:300.0)
            float r3 = r3 / r4
            float r0 = r0 - r3
            r1.currentRevealProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0bdc
            r1.currentRevealProgress = r2
        L_0x0bdc:
            r7 = 1
        L_0x0bdd:
            if (r7 == 0) goto L_0x0be2
            r24.invalidate()
        L_0x0be2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
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

    public MessageObject getMessage() {
        return this.message;
    }
}
