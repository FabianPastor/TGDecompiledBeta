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
    private boolean promoDialog;
    private RectF rect = new RectF();
    private float reorderIconProgress;
    private ImageReceiver thumbImage = new ImageReceiver(this);
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
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.thumbImage.setRoundRadius(AndroidUtilities.dp(2.0f));
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

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x05fb, code lost:
        if (r2.post_messages == false) goto L_0x05d7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0362  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0a72 A[Catch:{ Exception -> 0x0a84 }] */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0a79 A[Catch:{ Exception -> 0x0a84 }] */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0acb A[SYNTHETIC, Splitter:B:476:0x0acb] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aec  */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0b04  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0b48  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0b54  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0c3f  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x0c8a  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0ca0  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0cbd  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0ccd  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:631:0x0de6  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0df1  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0e2c  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0e3d  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0e7d  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0e89  */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x0ec8  */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x0ed3  */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0ee3  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0efb  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0f0a  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x0f6b  */
    /* JADX WARNING: Removed duplicated region for block: B:711:0x0fef  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x1005  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x1085  */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x1130  */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x11e3  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x1208  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x1236  */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x134f  */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x13f2  */
    /* JADX WARNING: Removed duplicated region for block: B:821:0x13fb  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1420 A[Catch:{ Exception -> 0x14b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x1429 A[Catch:{ Exception -> 0x14b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x1449 A[Catch:{ Exception -> 0x14b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x149d A[Catch:{ Exception -> 0x14b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:858:0x14a0 A[Catch:{ Exception -> 0x14b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:864:0x14ba  */
    /* JADX WARNING: Removed duplicated region for block: B:908:0x15ea  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x167c A[SYNTHETIC, Splitter:B:941:0x167c] */
    /* JADX WARNING: Removed duplicated region for block: B:957:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r43 = this;
            r1 = r43
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
            if (r0 == 0) goto L_0x00bb
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.util.LongSparseArray<java.lang.CharSequence> r0 = r0.printingStrings
            long r9 = r1.currentDialogId
            java.lang.Object r0 = r0.get(r9)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            goto L_0x00bc
        L_0x00bb:
            r0 = 0
        L_0x00bc:
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
            if (r10 != 0) goto L_0x00e0
            boolean r10 = r1.useMeForMyMessages
            if (r10 != 0) goto L_0x00e0
            r10 = 1
            goto L_0x00e1
        L_0x00e0:
            r10 = 0
        L_0x00e1:
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r3) goto L_0x00f8
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x00ed
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x00f1
        L_0x00ed:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x00f4
        L_0x00f1:
            java.lang.String r11 = "%2$s: ⁨%1$s⁩"
            goto L_0x0106
        L_0x00f4:
            java.lang.String r11 = "⁨%s⁩"
            goto L_0x010a
        L_0x00f8:
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0100
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x0104
        L_0x0100:
            int r11 = r1.currentDialogFolderId
            if (r11 == 0) goto L_0x0108
        L_0x0104:
            java.lang.String r11 = "%2$s: %1$s"
        L_0x0106:
            r12 = 1
            goto L_0x010b
        L_0x0108:
            java.lang.String r11 = "%1$s"
        L_0x010a:
            r12 = 0
        L_0x010b:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0112
            java.lang.CharSequence r13 = r13.messageText
            goto L_0x0113
        L_0x0112:
            r13 = 0
        L_0x0113:
            r1.lastMessageString = r13
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            r14 = 1117782016(0x42a00000, float:80.0)
            r15 = 1118044160(0x42a40000, float:82.0)
            r18 = 1101004800(0x41a00000, float:20.0)
            r3 = 33
            r20 = 1102053376(0x41b00000, float:22.0)
            r8 = 150(0x96, float:2.1E-43)
            r2 = 2
            r23 = 1117257728(0x42980000, float:76.0)
            r24 = 1099956224(0x41900000, float:18.0)
            r25 = 1117519872(0x429CLASSNAME, float:78.0)
            java.lang.String r4 = ""
            if (r13 == 0) goto L_0x0362
            int r0 = r13.type
            if (r0 != r2) goto L_0x01b3
            r1.drawNameLock = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0178
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x013d
            goto L_0x0178
        L_0x013d:
            r0 = 1099169792(0x41840000, float:16.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x015e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x015e:
            int r0 = r43.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0178:
            r0 = 1095237632(0x41480000, float:12.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0199
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0199:
            int r0 = r43.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x01b3:
            boolean r10 = r13.verified
            r1.drawVerified = r10
            boolean r10 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r10 == 0) goto L_0x0258
            if (r0 != r5) goto L_0x0258
            r1.drawNameGroup = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0211
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x01c8
            goto L_0x0211
        L_0x01c8:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01f0
            r0 = 1099694080(0x418CLASSNAME, float:17.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x01e5
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x01e7
        L_0x01e5:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x01e7:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x01f0:
            int r0 = r43.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0200
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0202
        L_0x0200:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0202:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0211:
            r0 = 1096286208(0x41580000, float:13.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockTop = r0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0238
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x022e
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0230
        L_0x022e:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0230:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 + r10
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0238:
            int r0 = r43.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r10
            boolean r10 = r1.drawNameGroup
            if (r10 == 0) goto L_0x0248
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x024a
        L_0x0248:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x024a:
            int r10 = r10.getIntrinsicWidth()
            int r0 = r0 - r10
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0258:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0273
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0261
            goto L_0x0273
        L_0x0261:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x026c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x026c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x0273:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x027e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r0
            goto L_0x0284
        L_0x027e:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r0
        L_0x0284:
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            int r10 = r0.type
            if (r10 != r5) goto L_0x0310
            r0 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            org.telegram.ui.Cells.DialogCell$CustomDialog r10 = r1.customDialog
            boolean r12 = r10.isMedia
            if (r12 == 0) goto L_0x02c2
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
            r10.setSpan(r11, r6, r12, r3)
            goto L_0x02fc
        L_0x02c2:
            java.lang.String r3 = r10.message
            int r10 = r3.length()
            if (r10 <= r8) goto L_0x02ce
            java.lang.String r3 = r3.substring(r6, r8)
        L_0x02ce:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x02ee
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x02d7
            goto L_0x02ee
        L_0x02d7:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r12 = 32
            r13 = 10
            java.lang.String r3 = r3.replace(r13, r12)
            r10[r6] = r3
            r10[r5] = r0
            java.lang.String r3 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r3)
            goto L_0x02fc
        L_0x02ee:
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r10[r6] = r3
            r10[r5] = r0
            java.lang.String r3 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r3)
        L_0x02fc:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r3 = r3[r11]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r10, r3, r11, r6)
            r10 = 0
            goto L_0x031e
        L_0x0310:
            java.lang.String r3 = r0.message
            boolean r0 = r0.isMedia
            if (r0 == 0) goto L_0x031c
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r0[r9]
        L_0x031c:
            r0 = 0
            r10 = 1
        L_0x031e:
            org.telegram.ui.Cells.DialogCell$CustomDialog r11 = r1.customDialog
            int r11 = r11.date
            long r11 = (long) r11
            java.lang.String r11 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            org.telegram.ui.Cells.DialogCell$CustomDialog r12 = r1.customDialog
            int r12 = r12.unread_count
            if (r12 == 0) goto L_0x033e
            r1.drawCount = r5
            java.lang.Object[] r13 = new java.lang.Object[r5]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13[r6] = r12
            java.lang.String r12 = "%d"
            java.lang.String r12 = java.lang.String.format(r12, r13)
            goto L_0x0341
        L_0x033e:
            r1.drawCount = r6
            r12 = 0
        L_0x0341:
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            boolean r13 = r13.sent
            if (r13 == 0) goto L_0x034c
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            goto L_0x0350
        L_0x034c:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
        L_0x0350:
            r1.drawClock = r6
            r1.drawError = r6
            org.telegram.ui.Cells.DialogCell$CustomDialog r13 = r1.customDialog
            java.lang.String r13 = r13.name
            r8 = r12
            r2 = 0
            r12 = 1
            r42 = r11
            r11 = r0
            r0 = r42
            goto L_0x0e87
        L_0x0362:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x037d
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x036b
            goto L_0x037d
        L_0x036b:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x0376
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r13
            goto L_0x038e
        L_0x0376:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r13
            goto L_0x038e
        L_0x037d:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x0388
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r13
            goto L_0x038e
        L_0x0388:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r13
        L_0x038e:
            org.telegram.tgnet.TLRPC$EncryptedChat r13 = r1.encryptedChat
            if (r13 == 0) goto L_0x0417
            int r13 = r1.currentDialogFolderId
            if (r13 != 0) goto L_0x05a8
            r1.drawNameLock = r5
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x03dc
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 == 0) goto L_0x03a1
            goto L_0x03dc
        L_0x03a1:
            r13 = 1099169792(0x41840000, float:16.5)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLockTop = r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x03c2
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 + r14
            r1.nameLeft = r13
            goto L_0x05a8
        L_0x03c2:
            int r13 = r43.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r13 = r13 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 - r14
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r13
            goto L_0x05a8
        L_0x03dc:
            r13 = 1095237632(0x41480000, float:12.5)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.nameLockTop = r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x03fd
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 + r14
            r1.nameLeft = r13
            goto L_0x05a8
        L_0x03fd:
            int r13 = r43.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r13 = r13 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
            int r13 = r13 - r14
            r1.nameLockLeft = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r13
            goto L_0x05a8
        L_0x0417:
            int r13 = r1.currentDialogFolderId
            if (r13 != 0) goto L_0x05a8
            org.telegram.tgnet.TLRPC$Chat r13 = r1.chat
            if (r13 == 0) goto L_0x050e
            boolean r2 = r13.scam
            if (r2 == 0) goto L_0x042b
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x042f
        L_0x042b:
            boolean r2 = r13.verified
            r1.drawVerified = r2
        L_0x042f:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x05a8
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x04a5
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x043c
            goto L_0x04a5
        L_0x043c:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r13 = r2.id
            if (r13 < 0) goto L_0x045a
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x044f
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x044f
            goto L_0x045a
        L_0x044f:
            r1.drawNameGroup = r5
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x0464
        L_0x045a:
            r1.drawNameBroadcast = r5
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x0464:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0484
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x0479
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x047b
        L_0x0479:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x047b:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x0484:
            int r2 = r43.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r13
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x0494
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0496
        L_0x0494:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0496:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x04a5:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r13 = r2.id
            if (r13 < 0) goto L_0x04c3
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x04b8
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x04b8
            goto L_0x04c3
        L_0x04b8:
            r1.drawNameGroup = r5
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04cd
        L_0x04c3:
            r1.drawNameBroadcast = r5
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04cd:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x04ed
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x04e2
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04e4
        L_0x04e2:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04e4:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x04ed:
            int r2 = r43.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r2 = r2 - r13
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x04fd
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04ff
        L_0x04fd:
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04ff:
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x050e:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x05a8
            boolean r13 = r2.scam
            if (r13 == 0) goto L_0x051e
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0522
        L_0x051e:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0522:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x05a8
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x05a8
            r1.drawNameBot = r5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0570
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0537
            goto L_0x0570
        L_0x0537:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0557
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x0557:
            int r2 = r43.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r13
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x0570:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0590
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 + r13
            r1.nameLeft = r2
            goto L_0x05a8
        L_0x0590:
            int r2 = r43.getMeasuredWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r2 = r2 - r13
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r13 = r13.getIntrinsicWidth()
            int r2 = r2 - r13
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
        L_0x05a8:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x05b4
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner
            int r2 = r2.date
        L_0x05b4:
            boolean r13 = r1.isDialogCell
            if (r13 == 0) goto L_0x060f
            int r13 = r1.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r13)
            long r14 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r13.getDraft(r14)
            r1.draftMessage = r13
            if (r13 == 0) goto L_0x05e3
            java.lang.String r13 = r13.message
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x05d9
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            int r13 = r13.reply_to_msg_id
            if (r13 == 0) goto L_0x05d7
            goto L_0x05d9
        L_0x05d7:
            r2 = 0
            goto L_0x060a
        L_0x05d9:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            int r13 = r13.date
            if (r2 <= r13) goto L_0x05e3
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x05d7
        L_0x05e3:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x05fd
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r13 = r2.megagroup
            if (r13 != 0) goto L_0x05fd
            boolean r13 = r2.creator
            if (r13 != 0) goto L_0x05fd
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x05d7
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x05d7
        L_0x05fd:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x060d
            boolean r13 = r2.left
            if (r13 != 0) goto L_0x05d7
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x060d
            goto L_0x05d7
        L_0x060a:
            r1.draftMessage = r2
            goto L_0x0612
        L_0x060d:
            r2 = 0
            goto L_0x0612
        L_0x060f:
            r2 = 0
            r1.draftMessage = r2
        L_0x0612:
            if (r0 == 0) goto L_0x0622
            r1.lastPrintString = r0
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r9 = r3[r9]
            r11 = r2
            r2 = 2
            r3 = 0
        L_0x061f:
            r12 = 1
            goto L_0x0CLASSNAME
        L_0x0622:
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06b6
            r0 = 2131625067(0x7f0e046b, float:1.8877331E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0661
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x065b
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0644
            goto L_0x065b
        L_0x0644:
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            java.lang.String r12 = "chats_draft"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            int r12 = r0.length()
            r2.setSpan(r11, r6, r12, r3)
            goto L_0x06b3
        L_0x065b:
            r11 = r0
            r0 = r4
        L_0x065d:
            r2 = 2
            r3 = 0
            r5 = 0
            goto L_0x061f
        L_0x0661:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r12 = r2.length()
            if (r12 <= r8) goto L_0x066f
            java.lang.String r2 = r2.substring(r6, r8)
        L_0x066f:
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
            if (r11 != 0) goto L_0x06a1
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 != 0) goto L_0x06a1
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            java.lang.String r12 = "chats_draft"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            int r12 = r0.length()
            int r12 = r12 + r5
            r2.setSpan(r11, r6, r12, r3)
        L_0x06a1:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r3 = r3[r11]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r11, r6)
        L_0x06b3:
            r11 = r0
            r0 = r2
            goto L_0x065d
        L_0x06b6:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x06ce
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625540(0x7f0e0644, float:1.887829E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x06c9:
            r2 = 2
            r3 = 0
            r11 = 0
            goto L_0x061f
        L_0x06ce:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x0742
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x0740
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x06ea
            r0 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c9
        L_0x06ea:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0702
            r0 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x06c9
        L_0x0702:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x0710
            r0 = 2131625149(0x7f0e04bd, float:1.8877498E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c9
        L_0x0710:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x0740
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x0736
            r0 = 2131625137(0x7f0e04b1, float:1.8877473E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x06c9
        L_0x0736:
            r0 = 2131625136(0x7f0e04b0, float:1.8877471E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x06c9
        L_0x0740:
            r0 = r4
            goto L_0x06c9
        L_0x0742:
            boolean r0 = r0.isFromUser()
            if (r0 == 0) goto L_0x075e
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            int r2 = r2.from_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            r2 = 0
            goto L_0x0776
        L_0x075e:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.to_id
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            r2 = r0
            r0 = 0
        L_0x0776:
            int r13 = r1.dialogsType
            r14 = 3
            if (r13 != r14) goto L_0x0794
            org.telegram.tgnet.TLRPC$User r13 = r1.user
            boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r13)
            if (r13 == 0) goto L_0x0794
            r0 = 2131626772(0x7f0e0b14, float:1.888079E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2
            r3 = 0
            r5 = 0
            r10 = 0
            r11 = 0
        L_0x0791:
            r12 = 1
            goto L_0x0CLASSNAME
        L_0x0794:
            boolean r13 = r1.useForceThreeLines
            if (r13 != 0) goto L_0x07ab
            boolean r13 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r13 != 0) goto L_0x07ab
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x07ab
            java.lang.CharSequence r0 = r43.formatArchivedDialogNames()
            r2 = 2
        L_0x07a5:
            r3 = 0
            r5 = 0
            r11 = 1
            r12 = 0
            goto L_0x0CLASSNAME
        L_0x07ab:
            org.telegram.messenger.MessageObject r13 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r14 == 0) goto L_0x07db
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x07cc
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x07c9
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x07cc
        L_0x07c9:
            r0 = r4
            r10 = 0
            goto L_0x07d0
        L_0x07cc:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x07d0:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            r2 = 2
        L_0x07d7:
            r3 = 0
            r5 = 0
            r11 = 1
            goto L_0x0791
        L_0x07db:
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x08ba
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 != 0) goto L_0x08ba
            boolean r13 = r13.needDrawBluredPreview()
            if (r13 != 0) goto L_0x08ba
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isPhoto()
            if (r13 != 0) goto L_0x0801
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isNewGif()
            if (r13 != 0) goto L_0x0801
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isVideo()
            if (r13 == 0) goto L_0x08ba
        L_0x0801:
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.isWebpage()
            if (r13 == 0) goto L_0x0814
            org.telegram.messenger.MessageObject r13 = r1.message
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r13.media
            org.telegram.tgnet.TLRPC$WebPage r13 = r13.webpage
            java.lang.String r13 = r13.type
            goto L_0x0815
        L_0x0814:
            r13 = 0
        L_0x0815:
            java.lang.String r14 = "app"
            boolean r14 = r14.equals(r13)
            if (r14 != 0) goto L_0x08ba
            java.lang.String r14 = "profile"
            boolean r14 = r14.equals(r13)
            if (r14 != 0) goto L_0x08ba
            java.lang.String r14 = "article"
            boolean r13 = r14.equals(r13)
            if (r13 != 0) goto L_0x08ba
            org.telegram.messenger.MessageObject r13 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r13.photoThumbs
            r14 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r13 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r13, r14)
            org.telegram.messenger.MessageObject r14 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.photoThumbs
            int r15 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15)
            if (r13 != r14) goto L_0x0846
            r14 = 0
        L_0x0846:
            if (r13 == 0) goto L_0x08ba
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isVideo()
            r1.drawPlay = r15
            java.lang.String r15 = org.telegram.messenger.FileLoader.getAttachFileName(r14)
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.mediaExists
            if (r3 != 0) goto L_0x0895
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r3 = r3.canDownloadMedia((org.telegram.messenger.MessageObject) r5)
            if (r3 != 0) goto L_0x0895
            int r3 = r1.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            boolean r3 = r3.isLoadingFile(r15)
            if (r3 == 0) goto L_0x0877
            goto L_0x0895
        L_0x0877:
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            r27 = 0
            r28 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r29 = org.telegram.messenger.ImageLocation.getForObject(r13, r5)
            r31 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            r33 = 0
            java.lang.String r30 = "20_20"
            r26 = r3
            r32 = r5
            r26.setImage((org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (org.telegram.messenger.ImageLocation) r29, (java.lang.String) r30, (java.lang.String) r31, (java.lang.Object) r32, (int) r33)
            goto L_0x08b8
        L_0x0895:
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r35 = org.telegram.messenger.ImageLocation.getForObject(r14, r5)
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLObject r5 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r37 = org.telegram.messenger.ImageLocation.getForObject(r13, r5)
            r39 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            r41 = 0
            java.lang.String r36 = "20_20"
            java.lang.String r38 = "20_20"
            r34 = r3
            r40 = r5
            r34.setImage((org.telegram.messenger.ImageLocation) r35, (java.lang.String) r36, (org.telegram.messenger.ImageLocation) r37, (java.lang.String) r38, (java.lang.String) r39, (java.lang.Object) r40, (int) r41)
        L_0x08b8:
            r3 = 0
            goto L_0x08bb
        L_0x08ba:
            r3 = 1
        L_0x08bb:
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            if (r5 == 0) goto L_0x0b34
            int r5 = r5.id
            if (r5 <= 0) goto L_0x0b34
            if (r2 != 0) goto L_0x0b34
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isOutOwner()
            if (r5 == 0) goto L_0x08d8
            r0 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x08d6:
            r2 = r0
            goto L_0x091b
        L_0x08d8:
            if (r0 == 0) goto L_0x090d
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x08ee
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x08e3
            goto L_0x08ee
        L_0x08e3:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x08d6
        L_0x08ee:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r2 == 0) goto L_0x08fe
            r0 = 2131625534(0x7f0e063e, float:1.8878279E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x08d6
        L_0x08fe:
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x08d6
        L_0x090d:
            if (r2 == 0) goto L_0x0918
            java.lang.String r0 = r2.title
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x08d6
        L_0x0918:
            java.lang.String r0 = "DELETED"
            goto L_0x08d6
        L_0x091b:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r5 = r0.caption
            if (r5 == 0) goto L_0x0990
            java.lang.String r0 = r5.toString()
            int r5 = r0.length()
            if (r5 <= r8) goto L_0x092f
            java.lang.String r0 = r0.substring(r6, r8)
        L_0x092f:
            if (r3 != 0) goto L_0x0934
            r3 = r4
        L_0x0932:
            r5 = 2
            goto L_0x0968
        L_0x0934:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x0940
            java.lang.String r3 = "📹 "
            goto L_0x0932
        L_0x0940:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x094c
            java.lang.String r3 = "🎤 "
            goto L_0x0932
        L_0x094c:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x0958
            java.lang.String r3 = "🎧 "
            goto L_0x0932
        L_0x0958:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x0964
            java.lang.String r3 = "🖼 "
            goto L_0x0932
        L_0x0964:
            java.lang.String r3 = "📎 "
            goto L_0x0932
        L_0x0968:
            java.lang.Object[] r12 = new java.lang.Object[r5]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r3 = 32
            r13 = 10
            java.lang.String r0 = r0.replace(r13, r3)
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            r12[r6] = r0
            r3 = 1
            r12[r3] = r2
            java.lang.String r0 = java.lang.String.format(r11, r12)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0ab8
        L_0x0990:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            if (r3 == 0) goto L_0x0a89
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0a89
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r0[r3]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x09d6
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r0 < r5) goto L_0x09c5
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0a01
        L_0x09c5:
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0a01
        L_0x09d6:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0a07
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r0 < r5) goto L_0x09f1
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0a01
        L_0x09f1:
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
        L_0x0a01:
            r3 = 32
            r5 = 10
            r13 = 1
            goto L_0x0a52
        L_0x0a07:
            int r3 = r0.type
            r5 = 14
            if (r3 != r5) goto L_0x0a47
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r3 < r5) goto L_0x0a2d
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r13 = 1
            r5[r13] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0a4e
        L_0x0a2d:
            r3 = 2
            r13 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r5[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r5[r13] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r5)
            goto L_0x0a4e
        L_0x0a47:
            r13 = 1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0a4e:
            r3 = 32
            r5 = 10
        L_0x0a52:
            java.lang.String r0 = r0.replace(r5, r3)
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r5[r6] = r0
            r5[r13] = r2
            java.lang.String r0 = java.lang.String.format(r11, r5)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0a84 }
            java.lang.String r5 = "chats_attachMessage"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)     // Catch:{ Exception -> 0x0a84 }
            r0.<init>(r5)     // Catch:{ Exception -> 0x0a84 }
            if (r12 == 0) goto L_0x0a79
            int r5 = r2.length()     // Catch:{ Exception -> 0x0a84 }
            r11 = 2
            int r5 = r5 + r11
            goto L_0x0a7a
        L_0x0a79:
            r5 = 0
        L_0x0a7a:
            int r11 = r3.length()     // Catch:{ Exception -> 0x0a84 }
            r12 = 33
            r3.setSpan(r0, r5, r11, r12)     // Catch:{ Exception -> 0x0a84 }
            goto L_0x0ab9
        L_0x0a84:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0ab9
        L_0x0a89:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0ab4
            int r3 = r0.length()
            if (r3 <= r8) goto L_0x0a9b
            java.lang.String r0 = r0.substring(r6, r8)
        L_0x0a9b:
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r3 = 32
            r12 = 10
            java.lang.String r0 = r0.replace(r12, r3)
            r5[r6] = r0
            r3 = 1
            r5[r3] = r2
            java.lang.String r0 = java.lang.String.format(r11, r5)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0ab8
        L_0x0ab4:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x0ab8:
            r3 = r0
        L_0x0ab9:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0ac1
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0acb
        L_0x0ac1:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0aec
            int r0 = r3.length()
            if (r0 <= 0) goto L_0x0aec
        L_0x0acb:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0ae5 }
            java.lang.String r5 = "chats_nameMessage"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)     // Catch:{ Exception -> 0x0ae5 }
            r0.<init>(r5)     // Catch:{ Exception -> 0x0ae5 }
            int r5 = r2.length()     // Catch:{ Exception -> 0x0ae5 }
            r11 = 1
            int r5 = r5 + r11
            r11 = 33
            r3.setSpan(r0, r6, r5, r11)     // Catch:{ Exception -> 0x0ae3 }
            r0 = r5
            goto L_0x0aee
        L_0x0ae3:
            r0 = move-exception
            goto L_0x0ae7
        L_0x0ae5:
            r0 = move-exception
            r5 = 0
        L_0x0ae7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0aee
        L_0x0aec:
            r0 = 0
            r5 = 0
        L_0x0aee:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r11 = r11[r12]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r11, r12, r6)
            boolean r11 = r1.hasMessageThumb
            if (r11 == 0) goto L_0x0b29
            boolean r11 = r3 instanceof android.text.SpannableStringBuilder
            if (r11 != 0) goto L_0x0b0e
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r11.<init>(r3)
            r3 = r11
        L_0x0b0e:
            r11 = r3
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r11.insert(r5, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r13 = r7 + 6
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.<init>(r13)
            int r13 = r5 + 1
            r14 = 33
            r11.setSpan(r12, r5, r13, r14)
        L_0x0b29:
            r5 = r2
            r2 = 2
            r11 = 1
            r12 = 0
            r42 = r3
            r3 = r0
            r0 = r42
            goto L_0x0CLASSNAME
        L_0x0b34:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0b54
            org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r2 == 0) goto L_0x0b54
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0b54
            r0 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0b51:
            r2 = 2
            goto L_0x0c3b
        L_0x0b54:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x0b72
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r2 == 0) goto L_0x0b72
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0b72
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0b51
        L_0x0b72:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r2 = r0.caption
            if (r2 == 0) goto L_0x0bc1
            if (r3 != 0) goto L_0x0b7c
            r0 = r4
            goto L_0x0bad
        L_0x0b7c:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0b86
            java.lang.String r0 = "📹 "
            goto L_0x0bad
        L_0x0b86:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0b92
            java.lang.String r0 = "🎤 "
            goto L_0x0bad
        L_0x0b92:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0b9e
            java.lang.String r0 = "🎧 "
            goto L_0x0bad
        L_0x0b9e:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isPhoto()
            if (r0 == 0) goto L_0x0baa
            java.lang.String r0 = "🖼 "
            goto L_0x0bad
        L_0x0baa:
            java.lang.String r0 = "📎 "
        L_0x0bad:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0b51
        L_0x0bc1:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0be3
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "📊 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0be1:
            r2 = 2
            goto L_0x0CLASSNAME
        L_0x0be3:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0CLASSNAME
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
            goto L_0x0be1
        L_0x0CLASSNAME:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0CLASSNAME
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r3[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 2
            java.lang.CharSequence r0 = r0.messageText
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            if (r5 == 0) goto L_0x0c3b
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0c3b
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r9 = r3[r5]
        L_0x0c3b:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x07d7
            int r3 = r0.length()
            if (r3 <= r8) goto L_0x0CLASSNAME
            java.lang.CharSequence r0 = r0.subSequence(r6, r8)
        L_0x0CLASSNAME:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0CLASSNAME
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0CLASSNAME:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r5 = " "
            r3.insert(r6, r5)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r5 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r11 = r7 + 6
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r5.<init>(r11)
            r11 = 33
            r12 = 1
            r3.setSpan(r5, r6, r12, r11)
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r5 = r5[r11]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r11 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r5, r12, r6)
            goto L_0x07a5
        L_0x0CLASSNAME:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0c8e
            java.lang.CharSequence r5 = r43.formatArchivedDialogNames()
        L_0x0c8e:
            r42 = r11
            r11 = r5
            r5 = r12
            r12 = r42
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0ca0
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0cb9
        L_0x0ca0:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0caa
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0cb9
        L_0x0caa:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0cb8
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0cb9
        L_0x0cb8:
            r13 = r4
        L_0x0cb9:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0ccd
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawCount = r6
            r1.drawMention = r6
            r1.drawError = r6
            r2 = 0
            r8 = 0
            goto L_0x0dc9
        L_0x0ccd:
            int r15 = r1.currentDialogFolderId
            if (r15 == 0) goto L_0x0d0e
            int r14 = r1.unreadCount
            int r15 = r1.mentionCount
            int r19 = r14 + r15
            if (r19 <= 0) goto L_0x0d07
            if (r14 <= r15) goto L_0x0cf2
            r2 = 1
            r1.drawCount = r2
            r1.drawMention = r6
            java.lang.Object[] r8 = new java.lang.Object[r2]
            int r14 = r14 + r15
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r8[r6] = r14
            java.lang.String r14 = "%d"
            java.lang.String r8 = java.lang.String.format(r14, r8)
        L_0x0cef:
            r2 = 0
            goto L_0x0d57
        L_0x0cf2:
            r2 = 1
            r1.drawCount = r6
            r1.drawMention = r2
            java.lang.Object[] r8 = new java.lang.Object[r2]
            int r14 = r14 + r15
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r8[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r8)
            goto L_0x0d0c
        L_0x0d07:
            r1.drawCount = r6
            r1.drawMention = r6
            r2 = 0
        L_0x0d0c:
            r8 = 0
            goto L_0x0d57
        L_0x0d0e:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0d18
            r1.drawCount = r6
            r2 = 1
            r8 = 0
            r10 = 0
            goto L_0x0d4b
        L_0x0d18:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0d3f
            r8 = 1
            if (r2 != r8) goto L_0x0d2b
            int r8 = r1.mentionCount
            if (r2 != r8) goto L_0x0d2b
            if (r14 == 0) goto L_0x0d2b
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            boolean r2 = r2.mentioned
            if (r2 != 0) goto L_0x0d3f
        L_0x0d2b:
            r2 = 1
            r1.drawCount = r2
            java.lang.Object[] r8 = new java.lang.Object[r2]
            int r14 = r1.unreadCount
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r8[r6] = r14
            java.lang.String r14 = "%d"
            java.lang.String r8 = java.lang.String.format(r14, r8)
            goto L_0x0d4b
        L_0x0d3f:
            r2 = 1
            boolean r8 = r1.markUnread
            if (r8 == 0) goto L_0x0d48
            r1.drawCount = r2
            r8 = r4
            goto L_0x0d4b
        L_0x0d48:
            r1.drawCount = r6
            r8 = 0
        L_0x0d4b:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0d54
            r1.drawMention = r2
            java.lang.String r2 = "@"
            goto L_0x0d57
        L_0x0d54:
            r1.drawMention = r6
            goto L_0x0cef
        L_0x0d57:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isOut()
            if (r14 == 0) goto L_0x0dc1
            org.telegram.tgnet.TLRPC$DraftMessage r14 = r1.draftMessage
            if (r14 != 0) goto L_0x0dc1
            if (r10 == 0) goto L_0x0dc1
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r14 = r14.action
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r14 != 0) goto L_0x0dc1
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0d7f
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r10 = 1
            r1.drawClock = r10
            r1.drawError = r6
            goto L_0x0dc9
        L_0x0d7f:
            r10 = 1
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isSendError()
            if (r14 == 0) goto L_0x0d95
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r10
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x0dc9
        L_0x0d95:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSent()
            if (r10 == 0) goto L_0x0dc9
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isUnread()
            if (r10 == 0) goto L_0x0db6
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0db4
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0db4
            goto L_0x0db6
        L_0x0db4:
            r10 = 0
            goto L_0x0db7
        L_0x0db6:
            r10 = 1
        L_0x0db7:
            r1.drawCheck1 = r10
            r10 = 1
            r1.drawCheck2 = r10
            r1.drawClock = r6
            r1.drawError = r6
            goto L_0x0dc9
        L_0x0dc1:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r6
        L_0x0dc9:
            r1.promoDialog = r6
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r14 = r1.dialogsType
            if (r14 != 0) goto L_0x0e27
            long r14 = r1.currentDialogId
            r6 = 1
            boolean r14 = r10.isPromoDialog(r14, r6)
            if (r14 == 0) goto L_0x0e27
            r1.drawPinBackground = r6
            r1.promoDialog = r6
            int r14 = r10.promoDialogType
            if (r14 != 0) goto L_0x0df1
            r10 = 2131627292(0x7f0e0d1c, float:1.8881844E38)
            java.lang.String r13 = "UseProxySponsor"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r6 = r10
            goto L_0x0e28
        L_0x0df1:
            if (r14 != r6) goto L_0x0e27
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r13 = "PsaType_"
            r6.append(r13)
            java.lang.String r13 = r10.promoPsaType
            r6.append(r13)
            java.lang.String r6 = r6.toString()
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6)
            boolean r13 = android.text.TextUtils.isEmpty(r6)
            if (r13 == 0) goto L_0x0e19
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r13 = "PsaTypeDefault"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)
        L_0x0e19:
            java.lang.String r13 = r10.promoPsaMessage
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 != 0) goto L_0x0e28
            java.lang.String r0 = r10.promoPsaMessage
            r10 = 0
            r1.hasMessageThumb = r10
            goto L_0x0e28
        L_0x0e27:
            r6 = r13
        L_0x0e28:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x0e3d
            r10 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r13 = "ArchivedChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
        L_0x0e35:
            r10 = r5
            r42 = r3
            r3 = r0
            r0 = r6
            r6 = r42
            goto L_0x0e87
        L_0x0e3d:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x0e45
            java.lang.String r10 = r10.title
        L_0x0e43:
            r13 = r10
            goto L_0x0e77
        L_0x0e45:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            if (r10 == 0) goto L_0x0e76
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x0e6f
            boolean r10 = r1.useMeForMyMessages
            if (r10 == 0) goto L_0x0e5d
            r10 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r13 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x0e43
        L_0x0e5d:
            int r10 = r1.dialogsType
            r13 = 3
            if (r10 != r13) goto L_0x0e65
            r10 = 1
            r1.drawPinBackground = r10
        L_0x0e65:
            r10 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.String r13 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x0e43
        L_0x0e6f:
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x0e43
        L_0x0e76:
            r13 = r4
        L_0x0e77:
            int r10 = r13.length()
            if (r10 != 0) goto L_0x0e35
            r10 = 2131625534(0x7f0e063e, float:1.8878279E38)
            java.lang.String r13 = "HiddenName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x0e35
        L_0x0e87:
            if (r12 == 0) goto L_0x0ec8
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r5 = r5.measureText(r0)
            double r14 = (double) r5
            double r14 = java.lang.Math.ceil(r14)
            int r5 = (int) r14
            android.text.StaticLayout r12 = new android.text.StaticLayout
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r12
            r27 = r0
            r29 = r5
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.timeLayout = r12
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0ebf
            int r0 = r43.getMeasuredWidth()
            r12 = 1097859072(0x41700000, float:15.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r12
            int r0 = r0 - r5
            r1.timeLeft = r0
            goto L_0x0ecf
        L_0x0ebf:
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.timeLeft = r0
            goto L_0x0ecf
        L_0x0ec8:
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r5 = 0
        L_0x0ecf:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0ee3
            int r0 = r43.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r0 = r0 - r12
            r12 = 1096810496(0x41600000, float:14.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r12
            int r0 = r0 - r5
            goto L_0x0ef7
        L_0x0ee3:
            int r0 = r43.getMeasuredWidth()
            int r12 = r1.nameLeft
            int r0 = r0 - r12
            r12 = 1117388800(0x429a0000, float:77.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 - r12
            int r0 = r0 - r5
            int r12 = r1.nameLeft
            int r12 = r12 + r5
            r1.nameLeft = r12
        L_0x0ef7:
            boolean r12 = r1.drawNameLock
            if (r12 == 0) goto L_0x0f0a
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r14 = r14.getIntrinsicWidth()
        L_0x0var_:
            int r12 = r12 + r14
            int r0 = r0 - r12
            goto L_0x0f3d
        L_0x0f0a:
            boolean r12 = r1.drawNameGroup
            if (r12 == 0) goto L_0x0f1b
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r14 = r14.getIntrinsicWidth()
            goto L_0x0var_
        L_0x0f1b:
            boolean r12 = r1.drawNameBroadcast
            if (r12 == 0) goto L_0x0f2c
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r14 = r14.getIntrinsicWidth()
            goto L_0x0var_
        L_0x0f2c:
            boolean r12 = r1.drawNameBot
            if (r12 == 0) goto L_0x0f3d
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            goto L_0x0var_
        L_0x0f3d:
            boolean r12 = r1.drawClock
            r14 = 1084227584(0x40a00000, float:5.0)
            if (r12 == 0) goto L_0x0f6b
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r12 = r12.getIntrinsicWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 + r15
            int r0 = r0 - r12
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0f5a
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.checkDrawLeft = r5
            goto L_0x0fe1
        L_0x0f5a:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r15 + r5
            r1.checkDrawLeft = r15
            int r5 = r1.nameLeft
            int r5 = r5 + r12
            r1.nameLeft = r5
            goto L_0x0fe1
        L_0x0f6b:
            boolean r12 = r1.drawCheck2
            if (r12 == 0) goto L_0x0fe1
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r12 = r12.getIntrinsicWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 + r15
            int r0 = r0 - r12
            boolean r15 = r1.drawCheck1
            if (r15 == 0) goto L_0x0fc8
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            r26 = 1090519040(0x41000000, float:8.0)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r15 = r15 - r26
            int r0 = r0 - r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0fa1
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.halfCheckDrawLeft = r5
            r12 = 1085276160(0x40b00000, float:5.5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 - r12
            r1.checkDrawLeft = r5
            goto L_0x0fe1
        L_0x0fa1:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r15 + r5
            r1.checkDrawLeft = r15
            r5 = 1085276160(0x40b00000, float:5.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = r15 + r5
            r1.halfCheckDrawLeft = r15
            int r5 = r1.nameLeft
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r14 = r14.getIntrinsicWidth()
            int r12 = r12 + r14
            r14 = 1090519040(0x41000000, float:8.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 - r14
            int r5 = r5 + r12
            r1.nameLeft = r5
            goto L_0x0fe1
        L_0x0fc8:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x0fd2
            int r5 = r1.timeLeft
            int r5 = r5 - r12
            r1.checkDrawLeft = r5
            goto L_0x0fe1
        L_0x0fd2:
            int r15 = r1.timeLeft
            int r15 = r15 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r15 + r5
            r1.checkDrawLeft = r15
            int r5 = r1.nameLeft
            int r5 = r5 + r12
            r1.nameLeft = r5
        L_0x0fe1:
            boolean r5 = r1.dialogMuted
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r5 == 0) goto L_0x1005
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x1005
            boolean r5 = r1.drawScam
            if (r5 != 0) goto L_0x1005
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r14 = r14.getIntrinsicWidth()
            int r5 = r5 + r14
            int r0 = r0 - r5
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x1038
            int r14 = r1.nameLeft
            int r14 = r14 + r5
            r1.nameLeft = r14
            goto L_0x1038
        L_0x1005:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x101f
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r14 = r14.getIntrinsicWidth()
            int r5 = r5 + r14
            int r0 = r0 - r5
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x1038
            int r14 = r1.nameLeft
            int r14 = r14 + r5
            r1.nameLeft = r14
            goto L_0x1038
        L_0x101f:
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x1038
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            org.telegram.ui.Components.ScamDrawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r14 = r14.getIntrinsicWidth()
            int r5 = r5 + r14
            int r0 = r0 - r5
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x1038
            int r14 = r1.nameLeft
            int r14 = r14 + r5
            r1.nameLeft = r14
        L_0x1038:
            r5 = 1094713344(0x41400000, float:12.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r14 = java.lang.Math.max(r14, r0)
            r12 = 10
            r15 = 32
            java.lang.String r0 = r13.replace(r12, r15)     // Catch:{ Exception -> 0x1077 }
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1077 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1077 }
            r12 = r12[r13]     // Catch:{ Exception -> 0x1077 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x1077 }
            int r13 = r14 - r13
            float r13 = (float) r13     // Catch:{ Exception -> 0x1077 }
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1077 }
            java.lang.CharSequence r27 = android.text.TextUtils.ellipsize(r0, r12, r13, r15)     // Catch:{ Exception -> 0x1077 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1077 }
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1077 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1077 }
            r28 = r12[r13]     // Catch:{ Exception -> 0x1077 }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1077 }
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r29 = r14
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x1077 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x1077 }
            goto L_0x107b
        L_0x1077:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x107b:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1130
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1085
            goto L_0x1130
        L_0x1085:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12 = 1106771968(0x41var_, float:31.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.messageNameTop = r12
            r12 = 1098907648(0x41800000, float:16.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.timeTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.errorTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.pinTop = r12
            r12 = 1109131264(0x421CLASSNAME, float:39.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.countTop = r12
            r12 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.checkDrawTop = r13
            int r12 = r43.getMeasuredWidth()
            r13 = 1119748096(0x42be0000, float:95.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x10e7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            int r13 = r43.getMeasuredWidth()
            r15 = 1115684864(0x42800000, float:64.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 - r15
            int r15 = r7 + 11
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r13 - r15
            goto L_0x10fc
        L_0x10e7:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r13
        L_0x10fc:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r13 = (float) r13
            r23 = r4
            float r4 = (float) r0
            r16 = 1113063424(0x42580000, float:54.0)
            r25 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r12 = (float) r12
            r37 = r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            r5.setImageCoords(r13, r4, r12, r6)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r15
            r6 = 1106247680(0x41var_, float:30.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r0
            float r6 = (float) r6
            float r12 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r4.setImageCoords(r5, r6, r13, r12)
            goto L_0x11dc
        L_0x1130:
            r23 = r4
            r37 = r6
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
            int r4 = r43.getMeasuredWidth()
            r5 = 1119485952(0x42ba0000, float:93.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r12 = r4 - r5
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1198
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r43.getMeasuredWidth()
            r5 = 1115947008(0x42840000, float:66.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            r5 = 1106771968(0x41var_, float:31.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r4 - r5
            goto L_0x11ad
        L_0x1198:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r25)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1116340224(0x428a0000, float:69.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r4
        L_0x11ad:
            org.telegram.messenger.ImageReceiver r6 = r1.avatarImage
            float r4 = (float) r4
            float r13 = (float) r0
            r15 = 1113587712(0x42600000, float:56.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r16 = 1113587712(0x42600000, float:56.0)
            r25 = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r12 = (float) r12
            r6.setImageCoords(r4, r13, r15, r12)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r5
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r0
            float r6 = (float) r6
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r13 = (float) r13
            r4.setImageCoords(r5, r6, r12, r13)
        L_0x11dc:
            r4 = r0
            r12 = r25
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1204
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x11fc
            int r0 = r43.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x1204
        L_0x11fc:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x1204:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1236
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r12 = r12 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x1222
            int r0 = r43.getMeasuredWidth()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.errorLeft = r0
            goto L_0x134d
        L_0x1222:
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.errorLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
            goto L_0x134d
        L_0x1236:
            if (r8 != 0) goto L_0x1262
            if (r2 == 0) goto L_0x123b
            goto L_0x1262
        L_0x123b:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x125b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r12 = r12 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x125b
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x125b:
            r2 = 0
            r1.drawCount = r2
            r1.drawMention = r2
            goto L_0x134d
        L_0x1262:
            if (r8 == 0) goto L_0x12c4
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r8)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_CENTER
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r27 = r8
            r29 = r5
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r5
            int r12 = r12 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x12b0
            int r0 = r43.getMeasuredWidth()
            int r5 = r1.countWidth
            int r0 = r0 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r5
            r1.countLeft = r0
            goto L_0x12c0
        L_0x12b0:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x12c0:
            r5 = 1
            r1.drawCount = r5
            goto L_0x12c7
        L_0x12c4:
            r5 = 0
            r1.countWidth = r5
        L_0x12c7:
            if (r2 == 0) goto L_0x134d
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x12ff
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r2)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r28 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.mentionWidth
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_CENTER
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r27 = r2
            r29 = r5
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.mentionLayout = r0
            goto L_0x1307
        L_0x12ff:
            r2 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.mentionWidth = r0
        L_0x1307:
            int r0 = r1.mentionWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r2
            int r12 = r12 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x132e
            int r0 = r43.getMeasuredWidth()
            int r2 = r1.mentionWidth
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r2
            int r2 = r1.countWidth
            if (r2 == 0) goto L_0x1329
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r2 = r2 + r5
            goto L_0x132a
        L_0x1329:
            r2 = 0
        L_0x132a:
            int r0 = r0 - r2
            r1.mentionLeft = r0
            goto L_0x134a
        L_0x132e:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x133c
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r5 = r5 + r6
            goto L_0x133d
        L_0x133c:
            r5 = 0
        L_0x133d:
            int r2 = r2 + r5
            r1.mentionLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x134a:
            r2 = 1
            r1.drawMention = r2
        L_0x134d:
            if (r10 == 0) goto L_0x1394
            if (r3 != 0) goto L_0x1353
            r3 = r23
        L_0x1353:
            java.lang.String r0 = r3.toString()
            int r2 = r0.length()
            r3 = 150(0x96, float:2.1E-43)
            if (r2 <= r3) goto L_0x1364
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r3)
        L_0x1364:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x136c
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x136e
        L_0x136c:
            if (r11 == 0) goto L_0x1377
        L_0x136e:
            r2 = 32
            r3 = 10
            java.lang.String r0 = r0.replace(r3, r2)
            goto L_0x137f
        L_0x1377:
            java.lang.String r2 = "\n\n"
            java.lang.String r3 = "\n"
            java.lang.String r0 = r0.replace(r2, r3)
        L_0x137f:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r3 = r1.paintIndex
            r2 = r2[r3]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r3 = 1099431936(0x41880000, float:17.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r3, r5)
        L_0x1394:
            r2 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = java.lang.Math.max(r0, r12)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x13a6
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x13e6
        L_0x13a6:
            if (r11 == 0) goto L_0x13e6
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x13b1
            int r0 = r1.currentDialogFolderDialogsCount
            r5 = 1
            if (r0 != r5) goto L_0x13e6
        L_0x13b1:
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x13cc }
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x13cc }
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            android.text.TextUtils$TruncateAt r33 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x13cc }
            r35 = 1
            r26 = r11
            r28 = r2
            r34 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r26, r27, r28, r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x13cc }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x13cc }
            goto L_0x13d0
        L_0x13cc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x13d0:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            r0.setImageY(r4)
            r5 = 0
            goto L_0x140f
        L_0x13e6:
            r5 = 0
            r1.messageNameLayout = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x13fb
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x13f2
            goto L_0x13fb
        L_0x13f2:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x140f
        L_0x13fb:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r6 = 1101529088(0x41a80000, float:21.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            r0.setImageY(r4)
        L_0x140f:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x14b2 }
            if (r0 != 0) goto L_0x1417
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x14b2 }
            if (r0 == 0) goto L_0x1429
        L_0x1417:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x14b2 }
            if (r0 == 0) goto L_0x1429
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x14b2 }
            r4 = 1
            if (r0 <= r4) goto L_0x1429
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x14b2 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x14b2 }
            r9 = r0[r3]     // Catch:{ Exception -> 0x14b2 }
            r8 = r5
            r0 = r11
            goto L_0x1445
        L_0x1429:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x14b2 }
            if (r0 != 0) goto L_0x1431
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x14b2 }
            if (r0 == 0) goto L_0x1433
        L_0x1431:
            if (r11 == 0) goto L_0x1443
        L_0x1433:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x14b2 }
            int r0 = r2 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x14b2 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x14b2 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r3, r9, r0, r4)     // Catch:{ Exception -> 0x14b2 }
            goto L_0x1444
        L_0x1443:
            r0 = r3
        L_0x1444:
            r8 = r11
        L_0x1445:
            boolean r3 = r1.useForceThreeLines     // Catch:{ Exception -> 0x14b2 }
            if (r3 != 0) goto L_0x147f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x14b2 }
            if (r3 == 0) goto L_0x144e
            goto L_0x147f
        L_0x144e:
            boolean r3 = r1.hasMessageThumb     // Catch:{ Exception -> 0x14b2 }
            if (r3 == 0) goto L_0x1468
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x14b2 }
            int r4 = r4 + r7
            int r2 = r2 + r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x14b2 }
            if (r4 == 0) goto L_0x1468
            int r4 = r1.messageLeft     // Catch:{ Exception -> 0x14b2 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x14b2 }
            int r7 = r7 + r5
            int r4 = r4 - r7
            r1.messageLeft = r4     // Catch:{ Exception -> 0x14b2 }
        L_0x1468:
            android.text.StaticLayout r3 = new android.text.StaticLayout     // Catch:{ Exception -> 0x14b2 }
            android.text.Layout$Alignment r19 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x14b2 }
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r15 = r3
            r16 = r0
            r17 = r9
            r18 = r2
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)     // Catch:{ Exception -> 0x14b2 }
            r1.messageLayout = r3     // Catch:{ Exception -> 0x14b2 }
            goto L_0x14b6
        L_0x147f:
            boolean r3 = r1.hasMessageThumb     // Catch:{ Exception -> 0x14b2 }
            if (r3 == 0) goto L_0x148c
            if (r8 == 0) goto L_0x148c
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x14b2 }
            int r2 = r2 + r4
        L_0x148c:
            android.text.Layout$Alignment r18 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x14b2 }
            r19 = 1065353216(0x3var_, float:1.0)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x14b2 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x14b2 }
            r21 = 0
            android.text.TextUtils$TruncateAt r22 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x14b2 }
            if (r8 == 0) goto L_0x14a0
            r24 = 1
            goto L_0x14a2
        L_0x14a0:
            r24 = 2
        L_0x14a2:
            r15 = r0
            r16 = r9
            r17 = r2
            r20 = r3
            r23 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r15, r16, r17, r18, r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x14b2 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x14b2 }
            goto L_0x14b6
        L_0x14b2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x14b6:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x15ea
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1573
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1573
            android.text.StaticLayout r0 = r1.nameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            boolean r5 = r1.dialogMuted
            if (r5 == 0) goto L_0x1508
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x1508
            boolean r5 = r1.drawScam
            if (r5 != 0) goto L_0x1508
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r14
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r3
            java.lang.Double.isNaN(r5)
            double r5 = r5 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.nameMuteLeft = r5
            goto L_0x155b
        L_0x1508:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x1532
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r14
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r3
            java.lang.Double.isNaN(r5)
            double r5 = r5 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.nameMuteLeft = r5
            goto L_0x155b
        L_0x1532:
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x155b
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r14
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r3
            java.lang.Double.isNaN(r5)
            double r5 = r5 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x155b:
            r5 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x1573
            double r5 = (double) r14
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1573
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x1573:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x15b4
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x15b4
            r3 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x1584:
            if (r3 >= r0) goto L_0x15aa
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r3)
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 != 0) goto L_0x15a9
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineWidth(r3)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r5
            int r5 = (int) r7
            int r4 = java.lang.Math.min(r4, r5)
            int r3 = r3 + 1
            goto L_0x1584
        L_0x15a9:
            r4 = 0
        L_0x15aa:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x15b4
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x15b4:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1674
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1674
            android.text.StaticLayout r0 = r1.messageNameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1674
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r3)
            double r3 = (double) r0
            double r3 = java.lang.Math.ceil(r3)
            double r5 = (double) r2
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1674
            int r0 = r1.messageNameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.messageNameLeft = r0
            goto L_0x1674
        L_0x15ea:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1639
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1639
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r14
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x161e
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r14
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x161e
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x161e:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x162a
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x162a
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x1639
        L_0x162a:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1639:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x165c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x165c
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r10 = 0
        L_0x1646:
            if (r10 >= r0) goto L_0x1655
            android.text.StaticLayout r3 = r1.messageLayout
            float r3 = r3.getLineLeft(r10)
            float r2 = java.lang.Math.min(r2, r3)
            int r10 = r10 + 1
            goto L_0x1646
        L_0x1655:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x165c:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1674
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1674
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1674:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x16ba
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x16ba
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x16b6 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x16b6 }
            r3 = r37
            r2 = 1
            if (r3 < r0) goto L_0x168c
            int r6 = r0 + -1
            goto L_0x168d
        L_0x168c:
            r6 = r3
        L_0x168d:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x16b6 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x16b6 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x16b6 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x16b6 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x16b6 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x16b6 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x16b6 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x16b6 }
            if (r0 == 0) goto L_0x16ad
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x16b6 }
            int r0 = r0 + r2
        L_0x16ad:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x16b6 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x16b6 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x16b6 }
            goto L_0x16ba
        L_0x16b6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16ba:
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

    /* JADX WARNING: Removed duplicated region for block: B:116:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0212  */
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
            goto L_0x0366
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
            if (r1 == 0) goto L_0x0216
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
            if (r2 == 0) goto L_0x015a
            r2 = r1 & 64
            if (r2 == 0) goto L_0x015a
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<java.lang.CharSequence> r2 = r2.printingStrings
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x0146
            if (r2 == 0) goto L_0x0158
        L_0x0146:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 != 0) goto L_0x014c
            if (r2 != 0) goto L_0x0158
        L_0x014c:
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x015a
            if (r2 == 0) goto L_0x015a
            boolean r2 = r6.equals(r2)
            if (r2 != 0) goto L_0x015a
        L_0x0158:
            r2 = 1
            goto L_0x015b
        L_0x015a:
            r2 = 0
        L_0x015b:
            if (r2 != 0) goto L_0x016e
            r6 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r1
            if (r6 == 0) goto L_0x016e
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x016e
            java.lang.CharSequence r6 = r6.messageText
            java.lang.CharSequence r7 = r0.lastMessageString
            if (r6 == r7) goto L_0x016e
            r2 = 1
        L_0x016e:
            if (r2 != 0) goto L_0x0179
            r6 = r1 & 2
            if (r6 == 0) goto L_0x0179
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0179
            r2 = 1
        L_0x0179:
            if (r2 != 0) goto L_0x0184
            r6 = r1 & 1
            if (r6 == 0) goto L_0x0184
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0184
            r2 = 1
        L_0x0184:
            if (r2 != 0) goto L_0x018f
            r6 = r1 & 8
            if (r6 == 0) goto L_0x018f
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x018f
            r2 = 1
        L_0x018f:
            if (r2 != 0) goto L_0x019a
            r6 = r1 & 16
            if (r6 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x019a
            r2 = 1
        L_0x019a:
            if (r2 != 0) goto L_0x01fb
            r6 = r1 & 256(0x100, float:3.59E-43)
            if (r6 == 0) goto L_0x01fb
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x01b5
            boolean r7 = r0.lastUnreadState
            boolean r6 = r6.isUnread()
            if (r7 == r6) goto L_0x01b5
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isUnread()
            r0.lastUnreadState = r2
            r2 = 1
        L_0x01b5:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x01fb
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r7 == 0) goto L_0x01d9
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)
            int r7 = r7.getArchiveUnreadCount()
        L_0x01d7:
            r8 = 0
            goto L_0x01e2
        L_0x01d9:
            if (r6 == 0) goto L_0x01e0
            int r7 = r6.unread_count
            int r8 = r6.unread_mentions_count
            goto L_0x01e2
        L_0x01e0:
            r7 = 0
            goto L_0x01d7
        L_0x01e2:
            if (r6 == 0) goto L_0x01fb
            int r9 = r0.unreadCount
            if (r9 != r7) goto L_0x01f2
            boolean r9 = r0.markUnread
            boolean r10 = r6.unread_mark
            if (r9 != r10) goto L_0x01f2
            int r9 = r0.mentionCount
            if (r9 == r8) goto L_0x01fb
        L_0x01f2:
            r0.unreadCount = r7
            r0.mentionCount = r8
            boolean r2 = r6.unread_mark
            r0.markUnread = r2
            r2 = 1
        L_0x01fb:
            if (r2 != 0) goto L_0x0210
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x0210
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x0210
            int r6 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r6 == r1) goto L_0x0210
            r0.lastSendState = r1
            r2 = 1
        L_0x0210:
            if (r2 != 0) goto L_0x0216
            r19.invalidate()
            return
        L_0x0216:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r2 = 0
            if (r1 == 0) goto L_0x0233
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r19.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x0231
            long r6 = r1.getDialogId()
            goto L_0x024c
        L_0x0231:
            r6 = r2
            goto L_0x024c
        L_0x0233:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x0247
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r6 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r6)
            if (r1 == 0) goto L_0x0247
            r1 = 1
            goto L_0x0248
        L_0x0247:
            r1 = 0
        L_0x0248:
            r0.dialogMuted = r1
            long r6 = r0.currentDialogId
        L_0x024c:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x02ed
            int r1 = (int) r6
            r2 = 32
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x029f
            if (r1 >= 0) goto L_0x028e
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            r0.chat = r1
            boolean r2 = r0.isDialogCell
            if (r2 != 0) goto L_0x02c5
            if (r1 == 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02c5
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x02c5
            r0.chat = r1
            goto L_0x02c5
        L_0x028e:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            r0.user = r1
            goto L_0x02c5
        L_0x029f:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02c5
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.encryptedChat
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02c5:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x02ed
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x02ed
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x02ed
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.clientUserId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02ed:
            int r1 = r0.currentDialogFolderId
            if (r1 == 0) goto L_0x030a
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
            goto L_0x0366
        L_0x030a:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x034a
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x0332
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x0332
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
            goto L_0x0366
        L_0x0332:
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
            goto L_0x0366
        L_0x034a:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x0366
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
        L_0x0366:
            int r1 = r19.getMeasuredWidth()
            if (r1 != 0) goto L_0x0377
            int r1 = r19.getMeasuredHeight()
            if (r1 == 0) goto L_0x0373
            goto L_0x0377
        L_0x0373:
            r19.requestLayout()
            goto L_0x037a
        L_0x0377:
            r19.buildLayout()
        L_0x037a:
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
    /* JADX WARNING: Removed duplicated region for block: B:141:0x047f  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x048e  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x055a  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0608  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x065a  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0686  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0717  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0766  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x08ea  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0921  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x093c  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a2d  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0a45  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0b02  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b2e  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0b57  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b84  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0b98  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0bbe  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0bde  */
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
            r3 = 2131627246(0x7f0e0cee, float:1.8881751E38)
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x00be:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 2131625538(0x7f0e0642, float:1.8878287E38)
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
            r3 = 2131626608(0x7f0e0a70, float:1.8880457E38)
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
            r3 = 2131624251(0x7f0e013b, float:1.8875676E38)
            java.lang.String r4 = "Archive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r1.translationDrawable = r4
            goto L_0x00ed
        L_0x010a:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r3 = 2131627237(0x7f0e0ce5, float:1.8881733E38)
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
            if (r0 != 0) goto L_0x039f
            float r0 = r1.cornerProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x039b
            goto L_0x039f
        L_0x039b:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x044d
        L_0x039f:
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
            if (r0 == 0) goto L_0x041a
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x03ef
            float r0 = r1.archiveBackgroundProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x041a
        L_0x03ef:
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
            goto L_0x0423
        L_0x041a:
            boolean r0 = r1.drawPin
            if (r0 != 0) goto L_0x0426
            boolean r0 = r1.drawPinBackground
            if (r0 == 0) goto L_0x0423
            goto L_0x0426
        L_0x0423:
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x044a
        L_0x0426:
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
        L_0x044a:
            r25.restore()
        L_0x044d:
            float r0 = r1.translationX
            r3 = 1125515264(0x43160000, float:150.0)
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x0467
            float r0 = r1.cornerProgress
            int r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r4 >= 0) goto L_0x047a
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 + r4
            r1.cornerProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0478
            r1.cornerProgress = r13
            goto L_0x0478
        L_0x0467:
            float r0 = r1.cornerProgress
            int r4 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x047a
            float r4 = (float) r10
            float r4 = r4 / r3
            float r0 = r0 - r4
            r1.cornerProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0478
            r1.cornerProgress = r9
        L_0x0478:
            r7 = 1
            goto L_0x047b
        L_0x047a:
            r7 = 0
        L_0x047b:
            boolean r0 = r1.drawNameLock
            if (r0 == 0) goto L_0x048e
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r8)
            goto L_0x04c6
        L_0x048e:
            boolean r0 = r1.drawNameGroup
            if (r0 == 0) goto L_0x04a1
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r8)
            goto L_0x04c6
        L_0x04a1:
            boolean r0 = r1.drawNameBroadcast
            if (r0 == 0) goto L_0x04b4
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r8)
            goto L_0x04c6
        L_0x04b4:
            boolean r0 = r1.drawNameBot
            if (r0 == 0) goto L_0x04c6
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r1.nameLockLeft
            int r5 = r1.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r4, (int) r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r8)
        L_0x04c6:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x053a
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x04e2
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0516
        L_0x04e2:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 != 0) goto L_0x0503
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 == 0) goto L_0x04ef
            int r0 = r0.type
            if (r0 != r14) goto L_0x04ef
            goto L_0x0503
        L_0x04ef:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_name"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x0516
        L_0x0503:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_secretName"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x0516:
            r25.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0528
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0525
            goto L_0x0528
        L_0x0525:
            r5 = 1095761920(0x41500000, float:13.0)
            goto L_0x052a
        L_0x0528:
            r5 = 1092616192(0x41200000, float:10.0)
        L_0x052a:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r8)
            r25.restore()
        L_0x053a:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x0556
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x0556
            r25.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r5 = r1.timeTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r8)
            r25.restore()
        L_0x0556:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x05a4
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x056c
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessageArchived_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x058b
        L_0x056c:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x057e
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_draft"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
            goto L_0x058b
        L_0x057e:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r5 = "chats_nameMessage_threeLines"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r0.setColor(r5)
        L_0x058b:
            r25.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r5 = r1.messageNameTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x059d }
            r0.draw(r8)     // Catch:{ Exception -> 0x059d }
            goto L_0x05a1
        L_0x059d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05a1:
            r25.restore()
        L_0x05a4:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0604
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x05d8
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x05c4
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_nameMessageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x05eb
        L_0x05c4:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_messageArchived"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
            goto L_0x05eb
        L_0x05d8:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r6 = r0[r5]
            r0 = r0[r5]
            java.lang.String r5 = "chats_message"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.linkColor = r5
            r6.setColor(r5)
        L_0x05eb:
            r25.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r5 = r1.messageTop
            float r5 = (float) r5
            r8.translate(r0, r5)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x05fd }
            r0.draw(r8)     // Catch:{ Exception -> 0x05fd }
            goto L_0x0601
        L_0x05fd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0601:
            r25.restore()
        L_0x0604:
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x064e
            boolean r0 = r1.drawClock
            if (r0 == 0) goto L_0x061b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            r0.draw(r8)
            goto L_0x064e
        L_0x061b:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x064e
            boolean r0 = r1.drawCheck1
            if (r0 == 0) goto L_0x0640
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
            goto L_0x064e
        L_0x0640:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r5 = r1.checkDrawLeft
            int r6 = r1.checkDrawTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            r0.draw(r8)
        L_0x064e:
            boolean r0 = r1.dialogMuted
            if (r0 == 0) goto L_0x0686
            boolean r0 = r1.drawVerified
            if (r0 != 0) goto L_0x0686
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x0686
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x066a
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0667
            goto L_0x066a
        L_0x0667:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x066b
        L_0x066a:
            r6 = 0
        L_0x066b:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0677
            r6 = 1096286208(0x41580000, float:13.5)
            goto L_0x0679
        L_0x0677:
            r6 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0679:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r8)
            goto L_0x06e9
        L_0x0686:
            boolean r0 = r1.drawVerified
            if (r0 == 0) goto L_0x06c7
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x069a
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0697
            goto L_0x069a
        L_0x0697:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x069c
        L_0x069a:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x069c:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x06b3
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x06b0
            goto L_0x06b3
        L_0x06b0:
            r6 = 1099169792(0x41840000, float:16.5)
            goto L_0x06b5
        L_0x06b3:
            r6 = 1095237632(0x41480000, float:12.5)
        L_0x06b5:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r8)
            goto L_0x06e9
        L_0x06c7:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x06e9
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x06db
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x06d8
            goto L_0x06db
        L_0x06d8:
            r6 = 1097859072(0x41700000, float:15.0)
            goto L_0x06dd
        L_0x06db:
            r6 = 1094713344(0x41400000, float:12.0)
        L_0x06dd:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r6)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r8)
        L_0x06e9:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x06f5
            float r0 = r1.reorderIconProgress
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x070d
        L_0x06f5:
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
        L_0x070d:
            boolean r0 = r1.drawError
            r6 = 1085276160(0x40b00000, float:5.5)
            r12 = 1102577664(0x41b80000, float:23.0)
            r17 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0766
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
            goto L_0x08b1
        L_0x0766:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x078f
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x076f
            goto L_0x078f
        L_0x076f:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x08b1
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
            goto L_0x08b1
        L_0x078f:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x0805
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x079f
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x079c
            goto L_0x079f
        L_0x079c:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x07a1
        L_0x079f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x07a1:
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
            if (r0 == 0) goto L_0x0805
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
        L_0x0805:
            boolean r0 = r1.drawMention
            if (r0 == 0) goto L_0x08b1
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
            if (r0 == 0) goto L_0x0843
            int r0 = r1.folderId
            if (r0 == 0) goto L_0x0843
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0845
        L_0x0843:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0845:
            android.graphics.RectF r2 = r1.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r17
            float r3 = r3 * r17
            r8.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r1.mentionLayout
            if (r0 == 0) goto L_0x087c
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
            goto L_0x08b1
        L_0x087c:
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
        L_0x08b1:
            boolean r0 = r1.animatingArchiveAvatar
            r9 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x08d3
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
        L_0x08d3:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x08e1
            org.telegram.ui.Components.PullForegroundDrawable r0 = r1.archivedChatsDrawable
            if (r0 == 0) goto L_0x08e1
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x08e6
        L_0x08e1:
            org.telegram.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r8)
        L_0x08e6:
            boolean r0 = r1.hasMessageThumb
            if (r0 == 0) goto L_0x091d
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r0.draw(r8)
            boolean r0 = r1.drawPlay
            if (r0 == 0) goto L_0x091d
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
        L_0x091d:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0924
            r25.restore()
        L_0x0924:
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            if (r0 == 0) goto L_0x0a25
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x0a25
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x0a25
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0a25
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0a25
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x096a
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0952
            int r0 = r0.expires
            int r2 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0968
        L_0x0952:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x096a
        L_0x0968:
            r0 = 1
            goto L_0x096b
        L_0x096a:
            r0 = 0
        L_0x096b:
            if (r0 != 0) goto L_0x0974
            float r2 = r1.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0a25
        L_0x0974:
            org.telegram.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r1.useForceThreeLines
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r3 != 0) goto L_0x0988
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0985
            goto L_0x0988
        L_0x0985:
            r18 = 1090519040(0x41000000, float:8.0)
            goto L_0x098a
        L_0x0988:
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x098a:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x09ac
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getImageX()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x09a3
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x09a5
        L_0x09a3:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x09a5:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 + r4
            goto L_0x09c2
        L_0x09ac:
            org.telegram.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getImageX2()
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x09ba
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x09bc
        L_0x09ba:
            r4 = 1092616192(0x41200000, float:10.0)
        L_0x09bc:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 - r4
        L_0x09c2:
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
            if (r0 == 0) goto L_0x0a0f
            float r0 = r1.onlineProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0a25
            float r2 = (float) r10
            r3 = 1125515264(0x43160000, float:150.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a23
            r1.onlineProgress = r13
            goto L_0x0a23
        L_0x0a0f:
            float r0 = r1.onlineProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0a25
            float r3 = (float) r10
            r4 = 1125515264(0x43160000, float:150.0)
            float r3 = r3 / r4
            float r0 = r0 - r3
            r1.onlineProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0a23
            r1.onlineProgress = r2
        L_0x0a23:
            r0 = 1
            goto L_0x0a26
        L_0x0a25:
            r0 = r7
        L_0x0a26:
            float r2 = r1.translationX
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0a30
            r25.restore()
        L_0x0a30:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0a41
            float r2 = r1.translationX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x0a41
            org.telegram.ui.Components.PullForegroundDrawable r2 = r1.archivedChatsDrawable
            if (r2 == 0) goto L_0x0a41
            r2.draw(r8)
        L_0x0a41:
            boolean r2 = r1.useSeparator
            if (r2 == 0) goto L_0x0aa2
            boolean r2 = r1.fullSeparator
            if (r2 != 0) goto L_0x0a65
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0a55
            boolean r2 = r1.archiveHidden
            if (r2 == 0) goto L_0x0a55
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x0a65
        L_0x0a55:
            boolean r2 = r1.fullSeparator2
            if (r2 == 0) goto L_0x0a5e
            boolean r2 = r1.archiveHidden
            if (r2 != 0) goto L_0x0a5e
            goto L_0x0a65
        L_0x0a5e:
            r2 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x0a66
        L_0x0a65:
            r2 = 0
        L_0x0a66:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0a87
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
            goto L_0x0aa2
        L_0x0a87:
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
        L_0x0aa2:
            float r2 = r1.clipProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0af0
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 == r3) goto L_0x0ab3
            r25.restore()
            goto L_0x0af0
        L_0x0ab3:
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
        L_0x0af0:
            boolean r2 = r1.drawReorder
            if (r2 != 0) goto L_0x0afe
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0afc
            goto L_0x0afe
        L_0x0afc:
            r3 = 0
            goto L_0x0b29
        L_0x0afe:
            boolean r2 = r1.drawReorder
            if (r2 == 0) goto L_0x0b15
            float r2 = r1.reorderIconProgress
            int r3 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r3 >= 0) goto L_0x0afc
            float r0 = (float) r10
            float r0 = r0 / r9
            float r2 = r2 + r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b13
            r1.reorderIconProgress = r13
        L_0x0b13:
            r3 = 0
            goto L_0x0b27
        L_0x0b15:
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0b29
            float r0 = (float) r10
            float r0 = r0 / r9
            float r2 = r2 - r0
            r1.reorderIconProgress = r2
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b27
            r1.reorderIconProgress = r3
        L_0x0b27:
            r7 = 1
            goto L_0x0b2a
        L_0x0b29:
            r7 = r0
        L_0x0b2a:
            boolean r0 = r1.archiveHidden
            if (r0 == 0) goto L_0x0b57
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b80
            float r2 = (float) r10
            r4 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r4
            float r0 = r0 - r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b41
            r1.archiveBackgroundProgress = r3
        L_0x0b41:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r14) goto L_0x0b7f
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
            goto L_0x0b7f
        L_0x0b57:
            float r0 = r1.archiveBackgroundProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0b80
            float r2 = (float) r10
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b6a
            r1.archiveBackgroundProgress = r13
        L_0x0b6a:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r14) goto L_0x0b7f
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r1.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
        L_0x0b7f:
            r7 = 1
        L_0x0b80:
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0b94
            float r0 = r1.animatingArchiveAvatarProgress
            float r2 = (float) r10
            float r0 = r0 + r2
            r1.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x0b93
            r1.animatingArchiveAvatarProgress = r9
            r2 = 0
            r1.animatingArchiveAvatar = r2
        L_0x0b93:
            r7 = 1
        L_0x0b94:
            boolean r0 = r1.drawRevealBackground
            if (r0 == 0) goto L_0x0bbe
            float r0 = r1.currentRevealBounceProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0baa
            float r2 = (float) r10
            float r2 = r2 / r9
            float r0 = r0 + r2
            r1.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0baa
            r1.currentRevealBounceProgress = r13
            r7 = 1
        L_0x0baa:
            float r0 = r1.currentRevealProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0bdc
            float r2 = (float) r10
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r1.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0bdb
            r1.currentRevealProgress = r13
            goto L_0x0bdb
        L_0x0bbe:
            float r0 = r1.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            r2 = 0
            if (r0 != 0) goto L_0x0bc8
            r1.currentRevealBounceProgress = r2
            r7 = 1
        L_0x0bc8:
            float r0 = r1.currentRevealProgress
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0bdc
            float r3 = (float) r10
            r4 = 1133903872(0x43960000, float:300.0)
            float r3 = r3 / r4
            float r0 = r0 - r3
            r1.currentRevealProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0bdb
            r1.currentRevealProgress = r2
        L_0x0bdb:
            r7 = 1
        L_0x0bdc:
            if (r7 == 0) goto L_0x0be1
            r24.invalidate()
        L_0x0be1:
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
        String formatDateAudio = LocaleController.formatDateAudio((long) i2, true);
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
