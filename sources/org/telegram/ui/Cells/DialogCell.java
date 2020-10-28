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
    private CheckBox2 checkBox;
    private int checkDrawLeft;
    private int checkDrawLeft1;
    private int checkDrawTop;
    private boolean clearingDialog;
    private float clipProgress;
    private int clockDrawLeft;
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
    private int pinLeft;
    private int pinTop;
    private int printingStringType;
    private boolean promoDialog;
    private RectF rect;
    private float reorderIconProgress;
    /* access modifiers changed from: private */
    public boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
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
        this.lastStatusDrawableParams = -1;
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
        if (this.currentDialogId != j) {
            this.lastStatusDrawableParams = -1;
        }
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

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x05f4, code lost:
        if (r2.post_messages == false) goto L_0x05d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0600, code lost:
        if (r2.kicked != false) goto L_0x05d0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x1870 A[SYNTHETIC, Splitter:B:1028:0x1870] */
    /* JADX WARNING: Removed duplicated region for block: B:1043:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0bd2  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0c1d  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0d9b  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0e33  */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0e3b  */
    /* JADX WARNING: Removed duplicated region for block: B:628:0x0e43  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0e4b  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0e68  */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0e78  */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:700:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0fcc  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0fdc  */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x102d  */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x1039  */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x1078  */
    /* JADX WARNING: Removed duplicated region for block: B:743:0x1083  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x1093  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x10ba  */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x10f3  */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x111c  */
    /* JADX WARNING: Removed duplicated region for block: B:782:0x11a0  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x11b6  */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x1222 A[Catch:{ Exception -> 0x1241 }] */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x1225 A[Catch:{ Exception -> 0x1241 }] */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x124f  */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x12ff  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x13af  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x13d4  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x1404  */
    /* JADX WARNING: Removed duplicated region for block: B:869:0x1521  */
    /* JADX WARNING: Removed duplicated region for block: B:909:0x15e3  */
    /* JADX WARNING: Removed duplicated region for block: B:910:0x15ec  */
    /* JADX WARNING: Removed duplicated region for block: B:920:0x1612 A[Catch:{ Exception -> 0x16a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x161e A[ADDED_TO_REGION, Catch:{ Exception -> 0x16a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x163d A[Catch:{ Exception -> 0x16a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:944:0x1694 A[Catch:{ Exception -> 0x16a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x1697 A[Catch:{ Exception -> 0x16a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x16ae  */
    /* JADX WARNING: Removed duplicated region for block: B:995:0x17de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r45 = this;
            r1 = r45
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
            r2 = 2
            r21 = 1117257728(0x42980000, float:76.0)
            r22 = 1099956224(0x41900000, float:18.0)
            r23 = 1117519872(0x429CLASSNAME, float:78.0)
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x0359
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
            int r0 = r45.getMeasuredWidth()
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
            int r0 = r45.getMeasuredWidth()
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
            int r0 = r45.getMeasuredWidth()
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
            int r0 = r45.getMeasuredWidth()
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
            r0 = 2131625526(0x7f0e0636, float:1.8878262E38)
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
            r8 = r0
            r16 = r7
            r0 = r11
            r2 = r12
            r11 = 1
            r14 = 0
            goto L_0x1037
        L_0x0359:
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x0374
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0362
            goto L_0x0374
        L_0x0362:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x036d
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r14
            goto L_0x0385
        L_0x036d:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x0385
        L_0x0374:
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x037f
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLeft = r14
            goto L_0x0385
        L_0x037f:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
        L_0x0385:
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 == 0) goto L_0x040e
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x059f
            r1.drawNameLock = r5
            boolean r14 = r1.useForceThreeLines
            if (r14 != 0) goto L_0x03d3
            boolean r14 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r14 == 0) goto L_0x0398
            goto L_0x03d3
        L_0x0398:
            r14 = 1099169792(0x41840000, float:16.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03b9
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x059f
        L_0x03b9:
            int r14 = r45.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r14
            goto L_0x059f
        L_0x03d3:
            r14 = 1095237632(0x41480000, float:12.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r1.nameLockTop = r14
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 != 0) goto L_0x03f4
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 + r15
            r1.nameLeft = r14
            goto L_0x059f
        L_0x03f4:
            int r14 = r45.getMeasuredWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r14 = r14 - r15
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r15 = r15.getIntrinsicWidth()
            int r14 = r14 - r15
            r1.nameLockLeft = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r14
            goto L_0x059f
        L_0x040e:
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x059f
            org.telegram.tgnet.TLRPC$Chat r14 = r1.chat
            if (r14 == 0) goto L_0x0505
            boolean r2 = r14.scam
            if (r2 == 0) goto L_0x0422
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0426
        L_0x0422:
            boolean r2 = r14.verified
            r1.drawVerified = r2
        L_0x0426:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x059f
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x049c
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0433
            goto L_0x049c
        L_0x0433:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r14 = r2.id
            if (r14 < 0) goto L_0x0451
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0446
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x0446
            goto L_0x0451
        L_0x0446:
            r1.drawNameGroup = r5
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x045b
        L_0x0451:
            r1.drawNameBroadcast = r5
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x045b:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x047b
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x0470
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0472
        L_0x0470:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0472:
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 + r14
            r1.nameLeft = r2
            goto L_0x059f
        L_0x047b:
            int r2 = r45.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x048b
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x048d
        L_0x048b:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x048d:
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 - r14
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x059f
        L_0x049c:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            int r14 = r2.id
            if (r14 < 0) goto L_0x04ba
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x04af
            goto L_0x04ba
        L_0x04af:
            r1.drawNameGroup = r5
            r2 = 1096286208(0x41580000, float:13.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            goto L_0x04c4
        L_0x04ba:
            r1.drawNameBroadcast = r5
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
        L_0x04c4:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x04e4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x04d9
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04db
        L_0x04d9:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04db:
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 + r14
            r1.nameLeft = r2
            goto L_0x059f
        L_0x04e4:
            int r2 = r45.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r14
            boolean r14 = r1.drawNameGroup
            if (r14 == 0) goto L_0x04f4
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04f6
        L_0x04f4:
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04f6:
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 - r14
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
            goto L_0x059f
        L_0x0505:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x059f
            boolean r14 = r2.scam
            if (r14 == 0) goto L_0x0515
            r1.drawScam = r5
            org.telegram.ui.Components.ScamDrawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r2.checkText()
            goto L_0x0519
        L_0x0515:
            boolean r2 = r2.verified
            r1.drawVerified = r2
        L_0x0519:
            boolean r2 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r2 == 0) goto L_0x059f
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x059f
            r1.drawNameBot = r5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0567
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x052e
            goto L_0x0567
        L_0x052e:
            r2 = 1099169792(0x41840000, float:16.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x054e
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 + r14
            r1.nameLeft = r2
            goto L_0x059f
        L_0x054e:
            int r2 = r45.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 - r14
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r2
            goto L_0x059f
        L_0x0567:
            r2 = 1095237632(0x41480000, float:12.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.nameLockTop = r2
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0587
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 + r14
            r1.nameLeft = r2
            goto L_0x059f
        L_0x0587:
            int r2 = r45.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 - r14
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r14 = r14.getIntrinsicWidth()
            int r2 = r2 - r14
            r1.nameLockLeft = r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r2
        L_0x059f:
            int r2 = r1.lastMessageDate
            if (r2 != 0) goto L_0x05ab
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 == 0) goto L_0x05ab
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            int r2 = r2.date
        L_0x05ab:
            boolean r14 = r1.isDialogCell
            if (r14 == 0) goto L_0x0606
            int r14 = r1.currentAccount
            org.telegram.messenger.MediaDataController r14 = org.telegram.messenger.MediaDataController.getInstance(r14)
            r16 = r9
            long r8 = r1.currentDialogId
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r14.getDraft(r8, r6)
            r1.draftMessage = r8
            if (r8 == 0) goto L_0x05dc
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x05d2
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.reply_to_msg_id
            if (r8 == 0) goto L_0x05d0
            goto L_0x05d2
        L_0x05d0:
            r2 = 0
            goto L_0x0603
        L_0x05d2:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.date
            if (r2 <= r8) goto L_0x05dc
            int r2 = r1.unreadCount
            if (r2 != 0) goto L_0x05d0
        L_0x05dc:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x05f6
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            boolean r8 = r2.megagroup
            if (r8 != 0) goto L_0x05f6
            boolean r8 = r2.creator
            if (r8 != 0) goto L_0x05f6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x05d0
            boolean r2 = r2.post_messages
            if (r2 == 0) goto L_0x05d0
        L_0x05f6:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x060b
            boolean r8 = r2.left
            if (r8 != 0) goto L_0x05d0
            boolean r2 = r2.kicked
            if (r2 == 0) goto L_0x060b
            goto L_0x05d0
        L_0x0603:
            r1.draftMessage = r2
            goto L_0x060b
        L_0x0606:
            r16 = r9
            r2 = 0
            r1.draftMessage = r2
        L_0x060b:
            if (r0 == 0) goto L_0x0666
            r1.lastPrintString = r0
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r8 = r1.currentDialogId
            java.lang.Integer r2 = r2.getPrintingStringType(r8, r6)
            int r2 = r2.intValue()
            r1.printingStringType = r2
            org.telegram.ui.Components.StatusDrawable r2 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r2)
            if (r2 == 0) goto L_0x0633
            int r2 = r2.getIntrinsicWidth()
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            goto L_0x0634
        L_0x0633:
            r2 = 0
        L_0x0634:
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
            r2 = 2
            r8 = 0
        L_0x0662:
            r11 = 1
            r12 = 0
            goto L_0x0e3f
        L_0x0666:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06f7
            r0 = 2131625114(0x7f0e049a, float:1.8877427E38)
            java.lang.String r2 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x06a5
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x069e
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0689
            goto L_0x069e
        L_0x0689:
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r9 = "chats_draft"
            r8.<init>(r9)
            int r9 = r0.length()
            r2.setSpan(r8, r6, r9, r3)
            r8 = r0
            r3 = r2
            goto L_0x06a0
        L_0x069e:
            r8 = r0
            r3 = r4
        L_0x06a0:
            r9 = r16
            r0 = 0
            r2 = 2
            goto L_0x0662
        L_0x06a5:
            org.telegram.tgnet.TLRPC$DraftMessage r2 = r1.draftMessage
            java.lang.String r2 = r2.message
            int r8 = r2.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x06b5
            java.lang.String r2 = r2.substring(r6, r9)
        L_0x06b5:
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = 32
            r11 = 10
            java.lang.String r2 = r2.replace(r11, r8)
            r9[r6] = r2
            r9[r5] = r0
            java.lang.String r2 = java.lang.String.format(r12, r9)
            android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r2)
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x06e3
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x06e3
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            java.lang.String r9 = "chats_draft"
            r8.<init>(r9)
            int r9 = r0.length()
            int r9 = r9 + r5
            r2.setSpan(r8, r6, r9, r3)
        L_0x06e3:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r3 = r3[r8]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r8, r6)
            r8 = r0
            goto L_0x06a0
        L_0x06f7:
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x0711
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r2 = r1.paintIndex
            r9 = r0[r2]
            r0 = 2131625599(0x7f0e067f, float:1.887841E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x070a:
            r0 = 0
            r2 = 2
            r8 = 0
            r11 = 1
        L_0x070e:
            r12 = 1
            goto L_0x0e3f
        L_0x0711:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x07a9
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x0785
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r2 == 0) goto L_0x072d
            r0 = 2131625196(0x7f0e04ec, float:1.8877593E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x070a
        L_0x072d:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0745
            r0 = 2131624456(0x7f0e0208, float:1.8876092E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "AwaitingEncryption"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x070a
        L_0x0745:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r2 == 0) goto L_0x0753
            r0 = 2131625197(0x7f0e04ed, float:1.8877595E38)
            java.lang.String r2 = "EncryptionRejected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x070a
        L_0x0753:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r2 == 0) goto L_0x0783
            int r0 = r0.admin_id
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r0 != r2) goto L_0x0779
            r0 = 2131625185(0x7f0e04e1, float:1.887757E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x070a
        L_0x0779:
            r0 = 2131625184(0x7f0e04e0, float:1.8877569E38)
            java.lang.String r2 = "EncryptedChatStartedIncoming"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x070a
        L_0x0783:
            r3 = r4
            goto L_0x070a
        L_0x0785:
            int r0 = r1.dialogsType
            r2 = 3
            if (r0 != r2) goto L_0x07a4
            org.telegram.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x07a4
            r0 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r9 = r16
            r0 = 0
            r2 = 2
            r8 = 0
            r10 = 0
            r11 = 0
            goto L_0x070e
        L_0x07a4:
            r3 = r4
            r9 = r16
            goto L_0x070a
        L_0x07a9:
            int r0 = r0.getFromChatId()
            if (r0 <= 0) goto L_0x07bf
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            r2 = 0
            goto L_0x07d0
        L_0x07bf:
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            r2 = r0
            r0 = 0
        L_0x07d0:
            int r8 = r1.dialogsType
            r9 = 3
            if (r8 != r9) goto L_0x07f1
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x07f1
            r0 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            java.lang.String r2 = "SavedMessagesInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r3 = r0
            r9 = r16
            r0 = 0
            r2 = 2
            r8 = 0
            r10 = 0
            r11 = 0
        L_0x07ee:
            r12 = 1
            goto L_0x0e37
        L_0x07f1:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x080b
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x080b
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x080b
            java.lang.CharSequence r0 = r45.formatArchivedDialogNames()
            r3 = r0
            r9 = r16
            r0 = 0
            r2 = 2
        L_0x0806:
            r8 = 0
        L_0x0807:
            r11 = 1
            r12 = 0
            goto L_0x0e37
        L_0x080b:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r8.messageOwner
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r9 == 0) goto L_0x083c
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x082c
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r2 != 0) goto L_0x0829
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x082c
        L_0x0829:
            r0 = r4
            r10 = 0
            goto L_0x0830
        L_0x082c:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0830:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r2[r3]
            r3 = r0
            r0 = 0
            r2 = 2
        L_0x0839:
            r8 = 0
            r11 = 1
            goto L_0x07ee
        L_0x083c:
            int r9 = r1.currentDialogFolderId
            if (r9 != 0) goto L_0x092a
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r1.encryptedChat
            if (r9 != 0) goto L_0x092a
            boolean r8 = r8.needDrawBluredPreview()
            if (r8 != 0) goto L_0x092a
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 != 0) goto L_0x0862
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isNewGif()
            if (r8 != 0) goto L_0x0862
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x092a
        L_0x0862:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isWebpage()
            if (r8 == 0) goto L_0x0875
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r8 = r8.type
            goto L_0x0876
        L_0x0875:
            r8 = 0
        L_0x0876:
            java.lang.String r9 = "app"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x092a
            java.lang.String r9 = "profile"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x092a
            java.lang.String r9 = "article"
            boolean r8 = r9.equals(r8)
            if (r8 != 0) goto L_0x092a
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            int r14 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r14)
            if (r8 != r9) goto L_0x08a7
            r9 = 0
        L_0x08a7:
            if (r8 == 0) goto L_0x092a
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVideo()
            r1.drawPlay = r14
            java.lang.String r14 = org.telegram.messenger.FileLoader.getAttachFileName(r9)
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.mediaExists
            if (r15 != 0) goto L_0x08f6
            int r15 = r1.currentAccount
            org.telegram.messenger.DownloadController r15 = org.telegram.messenger.DownloadController.getInstance(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r15.canDownloadMedia((org.telegram.messenger.MessageObject) r3)
            if (r3 != 0) goto L_0x08f6
            int r3 = r1.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            boolean r3 = r3.isLoadingFile(r14)
            if (r3 == 0) goto L_0x08d8
            goto L_0x08f6
        L_0x08d8:
            org.telegram.messenger.ImageReceiver r3 = r1.thumbImage
            r27 = 0
            r28 = 0
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLObject r9 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r29 = org.telegram.messenger.ImageLocation.getForObject(r8, r9)
            r31 = 0
            org.telegram.messenger.MessageObject r8 = r1.message
            r33 = 0
            java.lang.String r30 = "20_20"
            r26 = r3
            r32 = r8
            r26.setImage((org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (org.telegram.messenger.ImageLocation) r29, (java.lang.String) r30, (java.lang.String) r31, (java.lang.Object) r32, (int) r33)
            goto L_0x0928
        L_0x08f6:
            org.telegram.messenger.MessageObject r3 = r1.message
            int r14 = r3.type
            if (r14 != r5) goto L_0x0905
            if (r9 == 0) goto L_0x0901
            int r14 = r9.size
            goto L_0x0902
        L_0x0901:
            r14 = 0
        L_0x0902:
            r31 = r14
            goto L_0x0907
        L_0x0905:
            r31 = 0
        L_0x0907:
            org.telegram.messenger.ImageReceiver r14 = r1.thumbImage
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r9, r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r29 = org.telegram.messenger.ImageLocation.getForObject(r8, r3)
            r32 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r34 = 0
            java.lang.String r28 = "20_20"
            java.lang.String r30 = "20_20"
            r26 = r14
            r33 = r3
            r26.setImage(r27, r28, r29, r30, r31, r32, r33, r34)
        L_0x0928:
            r3 = 0
            goto L_0x092b
        L_0x092a:
            r3 = 1
        L_0x092b:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x0bfb
            int r9 = r8.id
            if (r9 <= 0) goto L_0x0bfb
            if (r2 != 0) goto L_0x0bfb
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0943
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isMegagroup(r8)
            if (r8 == 0) goto L_0x0bfb
        L_0x0943:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isOutOwner()
            if (r8 == 0) goto L_0x0956
            r0 = 2131625526(0x7f0e0636, float:1.8878262E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0954:
            r2 = r0
            goto L_0x0999
        L_0x0956:
            if (r0 == 0) goto L_0x098b
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x096c
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0961
            goto L_0x096c
        L_0x0961:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0954
        L_0x096c:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r2 == 0) goto L_0x097c
            r0 = 2131625592(0x7f0e0678, float:1.8878396E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0954
        L_0x097c:
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0954
        L_0x098b:
            if (r2 == 0) goto L_0x0996
            java.lang.String r0 = r2.title
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x0954
        L_0x0996:
            java.lang.String r0 = "DELETED"
            goto L_0x0954
        L_0x0999:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r8 = r0.caption
            if (r8 == 0) goto L_0x0a0d
            java.lang.String r0 = r8.toString()
            int r8 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x09af
            java.lang.String r0 = r0.substring(r6, r9)
        L_0x09af:
            if (r3 != 0) goto L_0x09b4
            r3 = r4
        L_0x09b2:
            r8 = 2
            goto L_0x09e3
        L_0x09b4:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x09bf
            java.lang.String r3 = "📹 "
            goto L_0x09b2
        L_0x09bf:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x09ca
            java.lang.String r3 = "🎤 "
            goto L_0x09b2
        L_0x09ca:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x09d5
            java.lang.String r3 = "🎧 "
            goto L_0x09b2
        L_0x09d5:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x09e0
            java.lang.String r3 = "🖼 "
            goto L_0x09b2
        L_0x09e0:
            java.lang.String r3 = "📎 "
            goto L_0x09b2
        L_0x09e3:
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r3 = 32
            r11 = 10
            java.lang.String r0 = r0.replace(r11, r3)
            r8.append(r0)
            java.lang.String r0 = r8.toString()
            r9[r6] = r0
            r9[r5] = r2
            java.lang.String r0 = java.lang.String.format(r12, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r3 = r0
            r9 = r16
            goto L_0x0b79
        L_0x0a0d:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            if (r3 == 0) goto L_0x0aed
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0aed
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r0[r3]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0a4e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            r0 = 18
            if (r11 < r0) goto L_0x0a3f
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ab4
        L_0x0a3f:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ab4
        L_0x0a4e:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0a74
            r8 = 18
            if (r11 < r8) goto L_0x0a65
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ab4
        L_0x0a65:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0ab4
        L_0x0a74:
            int r3 = r0.type
            r8 = 14
            if (r3 != r8) goto L_0x0aae
            r3 = 18
            if (r11 < r3) goto L_0x0a96
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0ab4
        L_0x0a96:
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0ab4
        L_0x0aae:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0ab4:
            r3 = 32
            r8 = 10
            java.lang.String r0 = r0.replace(r8, r3)
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            r8[r6] = r0
            r8[r5] = r2
            java.lang.String r0 = java.lang.String.format(r12, r8)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0ae7 }
            java.lang.String r8 = "chats_attachMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x0ae7 }
            if (r13 == 0) goto L_0x0adb
            int r8 = r2.length()     // Catch:{ Exception -> 0x0ae7 }
            r11 = 2
            int r8 = r8 + r11
            goto L_0x0adc
        L_0x0adb:
            r8 = 0
        L_0x0adc:
            int r11 = r3.length()     // Catch:{ Exception -> 0x0ae7 }
            r12 = 33
            r3.setSpan(r0, r8, r11, r12)     // Catch:{ Exception -> 0x0ae7 }
            goto L_0x0b79
        L_0x0ae7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0b79
        L_0x0aed:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0b72
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b48
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b02
            r3 = r0
        L_0x0b02:
            int r0 = r45.getMeasuredWidth()
            r8 = 1121058816(0x42d20000, float:105.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            if (r13 == 0) goto L_0x0b2f
            boolean r8 = android.text.TextUtils.isEmpty(r2)
            if (r8 != 0) goto L_0x0b23
            float r0 = (float) r0
            java.lang.String r8 = r2.toString()
            r9 = r16
            float r8 = r9.measureText(r8)
            float r0 = r0 - r8
            int r0 = (int) r0
            goto L_0x0b25
        L_0x0b23:
            r9 = r16
        L_0x0b25:
            float r0 = (float) r0
            java.lang.String r8 = ": "
            float r8 = r9.measureText(r8)
            float r0 = r0 - r8
            int r0 = (int) r0
            goto L_0x0b31
        L_0x0b2f:
            r9 = r16
        L_0x0b31:
            if (r0 <= 0) goto L_0x0b62
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r3, r8, r0, r9, r11)
            java.lang.String r3 = r0.toString()
            goto L_0x0b62
        L_0x0b48:
            r9 = r16
            int r0 = r3.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r0 <= r8) goto L_0x0b56
            java.lang.String r3 = r3.substring(r6, r8)
        L_0x0b56:
            r8 = 32
            r11 = 10
            java.lang.String r0 = r3.replace(r11, r8)
            java.lang.String r3 = r0.trim()
        L_0x0b62:
            r8 = 2
            java.lang.Object[] r0 = new java.lang.Object[r8]
            r0[r6] = r3
            r0[r5] = r2
            java.lang.String r0 = java.lang.String.format(r12, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b78
        L_0x0b72:
            r9 = r16
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x0b78:
            r3 = r0
        L_0x0b79:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0b81
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0b8b
        L_0x0b81:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0ba7
            int r0 = r3.length()
            if (r0 <= 0) goto L_0x0ba7
        L_0x0b8b:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0ba0 }
            java.lang.String r8 = "chats_nameMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x0ba0 }
            int r8 = r2.length()     // Catch:{ Exception -> 0x0ba0 }
            int r8 = r8 + r5
            r11 = 33
            r3.setSpan(r0, r6, r8, r11)     // Catch:{ Exception -> 0x0b9e }
            r0 = r8
            goto L_0x0ba9
        L_0x0b9e:
            r0 = move-exception
            goto L_0x0ba2
        L_0x0ba0:
            r0 = move-exception
            r8 = 0
        L_0x0ba2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0ba9
        L_0x0ba7:
            r0 = 0
            r8 = 0
        L_0x0ba9:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r11 = r11[r12]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r11, r12, r6)
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.hasHighlightedWords()
            if (r11 == 0) goto L_0x0bce
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r11)
            if (r11 == 0) goto L_0x0bce
            r3 = r11
        L_0x0bce:
            boolean r11 = r1.hasMessageThumb
            if (r11 == 0) goto L_0x0bf7
            boolean r11 = r3 instanceof android.text.SpannableStringBuilder
            if (r11 != 0) goto L_0x0bdc
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r11.<init>(r3)
            r3 = r11
        L_0x0bdc:
            r11 = r3
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r11.insert(r8, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r13 = r7 + 6
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.<init>(r13)
            int r13 = r8 + 1
            r14 = 33
            r11.setSpan(r12, r8, r13, r14)
        L_0x0bf7:
            r8 = r2
            r2 = 2
            goto L_0x0807
        L_0x0bfb:
            r9 = r16
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0c1d
            org.telegram.tgnet.TLRPC$Photo r8 = r2.photo
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r8 == 0) goto L_0x0c1d
            int r8 = r2.ttl_seconds
            if (r8 == 0) goto L_0x0c1d
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0c1a:
            r2 = 2
            goto L_0x0d97
        L_0x0c1d:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Document r8 = r2.document
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r8 == 0) goto L_0x0CLASSNAME
            int r8 = r2.ttl_seconds
            if (r8 == 0) goto L_0x0CLASSNAME
            r0 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0c1a
        L_0x0CLASSNAME:
            java.lang.CharSequence r8 = r0.caption
            if (r8 == 0) goto L_0x0ce5
            if (r3 != 0) goto L_0x0c3d
            r0 = r4
            goto L_0x0CLASSNAME
        L_0x0c3d:
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "📹 "
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "🎤 "
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0c5c
            java.lang.String r0 = "🎧 "
            goto L_0x0CLASSNAME
        L_0x0c5c:
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
            if (r2 == 0) goto L_0x0cd0
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0cd0
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r45.getMeasuredWidth()
            r8 = 1122893824(0x42ee0000, float:119.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 - r8
            if (r13 == 0) goto L_0x0ca9
            r8 = 0
            boolean r11 = android.text.TextUtils.isEmpty(r8)
            if (r11 != 0) goto L_0x0ca0
            float r3 = (float) r3
            java.lang.String r11 = r8.toString()
            float r8 = r9.measureText(r11)
            float r3 = r3 - r8
            int r3 = (int) r3
        L_0x0ca0:
            float r3 = (float) r3
            java.lang.String r8 = ": "
            float r8 = r9.measureText(r8)
            float r3 = r3 - r8
            int r3 = (int) r3
        L_0x0ca9:
            if (r3 <= 0) goto L_0x0cbf
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r8, r3, r9, r11)
            java.lang.String r2 = r2.toString()
        L_0x0cbf:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0c1a
        L_0x0cd0:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0c1a
        L_0x0ce5:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0d03
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "📊 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0d00:
            r2 = 2
            goto L_0x0d83
        L_0x0d03:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0d23
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
            goto L_0x0d00
        L_0x0d23:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0d41
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r3[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r3[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0d83
        L_0x0d41:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0d78
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d78
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r45.getMeasuredWidth()
            r8 = 1119748096(0x42be0000, float:95.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 - r8
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r8, r3, r9, r11)
            java.lang.String r0 = r0.toString()
            goto L_0x0d7c
        L_0x0d78:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0d7c:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0d83:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            if (r8 == 0) goto L_0x0d97
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0d97
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r9 = r3[r8]
        L_0x0d97:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0e33
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0dd6
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0dd6
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            int r3 = r45.getMeasuredWidth()
            int r8 = r7 + 95
            int r8 = r8 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 - r8
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r8, r3, r9, r11)
            java.lang.String r0 = r0.toString()
            goto L_0x0de6
        L_0x0dd6:
            int r3 = r0.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r3 <= r8) goto L_0x0de2
            java.lang.CharSequence r0 = r0.subSequence(r6, r8)
        L_0x0de2:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0de6:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0df0
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0df0:
            r3 = r0
            android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
            java.lang.String r8 = " "
            r3.insert(r6, r8)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r8 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r11 = r7 + 6
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r8.<init>(r11)
            r11 = 33
            r3.setSpan(r8, r6, r5, r11)
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r8 = r8[r11]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            r11 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.messenger.Emoji.replaceEmoji(r3, r8, r12, r6)
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x0e2f
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r8)
            if (r3 == 0) goto L_0x0e2f
            goto L_0x0e30
        L_0x0e2f:
            r3 = r0
        L_0x0e30:
            r0 = 0
            goto L_0x0806
        L_0x0e33:
            r3 = r0
            r0 = 0
            goto L_0x0839
        L_0x0e37:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0e3f
            java.lang.CharSequence r8 = r45.formatArchivedDialogNames()
        L_0x0e3f:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0e4b
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e64
        L_0x0e4b:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0e55
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e64
        L_0x0e55:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0e63
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e64
        L_0x0e63:
            r13 = r4
        L_0x0e64:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0e78
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawCount = r6
            r1.drawMention = r6
            r1.drawError = r6
            r2 = 0
            r14 = 0
            goto L_0x0var_
        L_0x0e78:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0eb7
            int r2 = r1.unreadCount
            int r14 = r1.mentionCount
            int r16 = r2 + r14
            if (r16 <= 0) goto L_0x0eb0
            if (r2 <= r14) goto L_0x0e9a
            r1.drawCount = r5
            r1.drawMention = r6
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r15)
            goto L_0x0eb5
        L_0x0e9a:
            r1.drawCount = r6
            r1.drawMention = r5
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r15)
            r14 = r2
            r2 = 0
            goto L_0x0efa
        L_0x0eb0:
            r1.drawCount = r6
            r1.drawMention = r6
            r2 = 0
        L_0x0eb5:
            r14 = 0
            goto L_0x0efa
        L_0x0eb7:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0ec0
            r1.drawCount = r6
            r2 = 0
            r10 = 0
            goto L_0x0eee
        L_0x0ec0:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0ee3
            if (r2 != r5) goto L_0x0ed2
            int r15 = r1.mentionCount
            if (r2 != r15) goto L_0x0ed2
            if (r14 == 0) goto L_0x0ed2
            org.telegram.tgnet.TLRPC$Message r14 = r14.messageOwner
            boolean r14 = r14.mentioned
            if (r14 != 0) goto L_0x0ee3
        L_0x0ed2:
            r1.drawCount = r5
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            goto L_0x0eee
        L_0x0ee3:
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0eeb
            r1.drawCount = r5
            r2 = r4
            goto L_0x0eee
        L_0x0eeb:
            r1.drawCount = r6
            r2 = 0
        L_0x0eee:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0ef7
            r1.drawMention = r5
            java.lang.String r14 = "@"
            goto L_0x0efa
        L_0x0ef7:
            r1.drawMention = r6
            goto L_0x0eb5
        L_0x0efa:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x0var_
            if (r10 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x0var_
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0var_
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r5
            r1.drawError = r6
            goto L_0x0var_
        L_0x0var_:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isSendError()
            if (r10 == 0) goto L_0x0var_
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r5
            r1.drawCount = r6
            r1.drawMention = r6
            goto L_0x0var_
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
            r1.drawClock = r6
            r1.drawError = r6
            goto L_0x0var_
        L_0x0var_:
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawError = r6
        L_0x0var_:
            r1.promoDialog = r6
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r15 = r1.dialogsType
            r16 = r7
            if (r15 != 0) goto L_0x0fc7
            long r6 = r1.currentDialogId
            boolean r6 = r10.isPromoDialog(r6, r5)
            if (r6 == 0) goto L_0x0fc7
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r6 = r10.promoDialogType
            if (r6 != 0) goto L_0x0var_
            r6 = 2131627488(0x7f0e0de0, float:1.8882242E38)
            java.lang.String r7 = "UseProxySponsor"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x0fc8
        L_0x0var_:
            if (r6 != r5) goto L_0x0fc7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "PsaType_"
            r6.append(r7)
            java.lang.String r7 = r10.promoPsaType
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6)
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 == 0) goto L_0x0fb9
            r6 = 2131626769(0x7f0e0b11, float:1.8880784E38)
            java.lang.String r7 = "PsaTypeDefault"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x0fb9:
            java.lang.String r7 = r10.promoPsaMessage
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0fc8
            java.lang.String r3 = r10.promoPsaMessage
            r7 = 0
            r1.hasMessageThumb = r7
            goto L_0x0fc8
        L_0x0fc7:
            r6 = r13
        L_0x0fc8:
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x0fdc
            r7 = 2131624272(0x7f0e0150, float:1.887572E38)
            java.lang.String r10 = "ArchivedChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r7)
        L_0x0fd5:
            r10 = r12
            r44 = r6
            r6 = r0
            r0 = r44
            goto L_0x1037
        L_0x0fdc:
            org.telegram.tgnet.TLRPC$Chat r7 = r1.chat
            if (r7 == 0) goto L_0x0fe4
            java.lang.String r7 = r7.title
        L_0x0fe2:
            r13 = r7
            goto L_0x1027
        L_0x0fe4:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            if (r7 == 0) goto L_0x1026
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r7)
            if (r7 == 0) goto L_0x0ff8
            r7 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            java.lang.String r10 = "RepliesTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fe2
        L_0x0ff8:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r7 == 0) goto L_0x101f
            boolean r7 = r1.useMeForMyMessages
            if (r7 == 0) goto L_0x100e
            r7 = 2131625526(0x7f0e0636, float:1.8878262E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fe2
        L_0x100e:
            int r7 = r1.dialogsType
            r10 = 3
            if (r7 != r10) goto L_0x1015
            r1.drawPinBackground = r5
        L_0x1015:
            r7 = 2131626941(0x7f0e0bbd, float:1.8881132E38)
            java.lang.String r10 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fe2
        L_0x101f:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            goto L_0x0fe2
        L_0x1026:
            r13 = r4
        L_0x1027:
            int r7 = r13.length()
            if (r7 != 0) goto L_0x0fd5
            r7 = 2131625592(0x7f0e0678, float:1.8878396E38)
            java.lang.String r10 = "HiddenName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fd5
        L_0x1037:
            if (r11 == 0) goto L_0x1078
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r7 = r7.measureText(r0)
            double r11 = (double) r7
            double r11 = java.lang.Math.ceil(r11)
            int r7 = (int) r11
            android.text.StaticLayout r11 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r11
            r26 = r0
            r28 = r7
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.timeLayout = r11
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x106f
            int r0 = r45.getMeasuredWidth()
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r11
            int r0 = r0 - r7
            r1.timeLeft = r0
            goto L_0x107f
        L_0x106f:
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.timeLeft = r0
            goto L_0x107f
        L_0x1078:
            r7 = 0
            r1.timeLayout = r7
            r7 = 0
            r1.timeLeft = r7
            r7 = 0
        L_0x107f:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1093
            int r0 = r45.getMeasuredWidth()
            int r11 = r1.nameLeft
            int r0 = r0 - r11
            r11 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r11
            int r0 = r0 - r7
            goto L_0x10a7
        L_0x1093:
            int r0 = r45.getMeasuredWidth()
            int r11 = r1.nameLeft
            int r0 = r0 - r11
            r11 = 1117388800(0x429a0000, float:77.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r11
            int r0 = r0 - r7
            int r11 = r1.nameLeft
            int r11 = r11 + r7
            r1.nameLeft = r11
        L_0x10a7:
            boolean r11 = r1.drawNameLock
            if (r11 == 0) goto L_0x10ba
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r12 = r12.getIntrinsicWidth()
        L_0x10b7:
            int r11 = r11 + r12
            int r0 = r0 - r11
            goto L_0x10ed
        L_0x10ba:
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x10cb
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10b7
        L_0x10cb:
            boolean r11 = r1.drawNameBroadcast
            if (r11 == 0) goto L_0x10dc
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10b7
        L_0x10dc:
            boolean r11 = r1.drawNameBot
            if (r11 == 0) goto L_0x10ed
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10b7
        L_0x10ed:
            boolean r11 = r1.drawClock
            r12 = 1084227584(0x40a00000, float:5.0)
            if (r11 == 0) goto L_0x111c
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r19
            int r0 = r0 - r11
            boolean r19 = org.telegram.messenger.LocaleController.isRTL
            if (r19 != 0) goto L_0x110b
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.clockDrawLeft = r7
            goto L_0x1192
        L_0x110b:
            int r15 = r1.timeLeft
            int r15 = r15 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r7
            r1.clockDrawLeft = r15
            int r7 = r1.nameLeft
            int r7 = r7 + r11
            r1.nameLeft = r7
            goto L_0x1192
        L_0x111c:
            boolean r11 = r1.drawCheck2
            if (r11 == 0) goto L_0x1192
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r11 = r11.getIntrinsicWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r15
            int r0 = r0 - r11
            boolean r15 = r1.drawCheck1
            if (r15 == 0) goto L_0x1179
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            r25 = 1090519040(0x41000000, float:8.0)
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r15 = r15 - r25
            int r0 = r0 - r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x1152
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.halfCheckDrawLeft = r7
            r11 = 1085276160(0x40b00000, float:5.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r7 = r7 - r11
            r1.checkDrawLeft = r7
            goto L_0x1192
        L_0x1152:
            int r15 = r1.timeLeft
            int r15 = r15 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r7
            r1.checkDrawLeft = r15
            r7 = 1085276160(0x40b00000, float:5.5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r15 = r15 + r7
            r1.halfCheckDrawLeft = r15
            int r7 = r1.nameLeft
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r12 = r12.getIntrinsicWidth()
            int r11 = r11 + r12
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 - r12
            int r7 = r7 + r11
            r1.nameLeft = r7
            goto L_0x1192
        L_0x1179:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x1183
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.checkDrawLeft1 = r7
            goto L_0x1192
        L_0x1183:
            int r15 = r1.timeLeft
            int r15 = r15 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r7
            r1.checkDrawLeft1 = r15
            int r7 = r1.nameLeft
            int r7 = r7 + r11
            r1.nameLeft = r7
        L_0x1192:
            boolean r7 = r1.dialogMuted
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r7 == 0) goto L_0x11b6
            boolean r7 = r1.drawVerified
            if (r7 != 0) goto L_0x11b6
            boolean r7 = r1.drawScam
            if (r7 != 0) goto L_0x11b6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11e9
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
            goto L_0x11e9
        L_0x11b6:
            boolean r7 = r1.drawVerified
            if (r7 == 0) goto L_0x11d0
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11e9
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
            goto L_0x11e9
        L_0x11d0:
            boolean r7 = r1.drawScam
            if (r7 == 0) goto L_0x11e9
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11e9
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
        L_0x11e9:
            r7 = 1094713344(0x41400000, float:12.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r12 = java.lang.Math.max(r12, r0)
            r11 = 10
            r15 = 32
            java.lang.String r0 = r13.replace(r11, r15)     // Catch:{ Exception -> 0x1241 }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1241 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1241 }
            r11 = r11[r13]     // Catch:{ Exception -> 0x1241 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x1241 }
            int r13 = r12 - r13
            float r13 = (float) r13     // Catch:{ Exception -> 0x1241 }
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1241 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r11, r13, r15)     // Catch:{ Exception -> 0x1241 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x1241 }
            if (r11 == 0) goto L_0x1225
            boolean r11 = r11.hasHighlightedWords()     // Catch:{ Exception -> 0x1241 }
            if (r11 == 0) goto L_0x1225
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x1241 }
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords     // Catch:{ Exception -> 0x1241 }
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r11)     // Catch:{ Exception -> 0x1241 }
            if (r11 == 0) goto L_0x1225
            r26 = r11
            goto L_0x1227
        L_0x1225:
            r26 = r0
        L_0x1227:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1241 }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1241 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1241 }
            r27 = r11[r13]     // Catch:{ Exception -> 0x1241 }
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1241 }
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r28 = r12
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)     // Catch:{ Exception -> 0x1241 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x1241 }
            goto L_0x1245
        L_0x1241:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1245:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x12ff
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x124f
            goto L_0x12ff
        L_0x124f:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11 = 1106771968(0x41var_, float:31.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.messageNameTop = r11
            r11 = 1098907648(0x41800000, float:16.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.timeTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.errorTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.pinTop = r11
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.countTop = r11
            r11 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.checkDrawTop = r13
            int r11 = r45.getMeasuredWidth()
            r13 = 1119748096(0x42be0000, float:95.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r11 = r11 - r13
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x12b1
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            int r13 = r45.getMeasuredWidth()
            r15 = 1115684864(0x42800000, float:64.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 - r15
            int r15 = r16 + 11
            float r15 = (float) r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r13 - r15
            goto L_0x12c6
        L_0x12b1:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r13
        L_0x12c6:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r13 = (float) r13
            float r7 = (float) r0
            r17 = 1113063424(0x42580000, float:54.0)
            r34 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r23 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r11 = (float) r11
            r5.setImageCoords(r13, r7, r4, r11)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r15
            r7 = 1106247680(0x41var_, float:30.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r0
            float r7 = (float) r7
            r11 = r16
            float r13 = (float) r11
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r15 = (float) r15
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r4.setImageCoords(r5, r7, r15, r13)
            r4 = r0
            r17 = r23
            r23 = r6
            goto L_0x13ab
        L_0x12ff:
            r34 = r4
            r11 = r16
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
            int r4 = r45.getMeasuredWidth()
            r5 = 1119485952(0x42ba0000, float:93.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x1366
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            int r5 = r45.getMeasuredWidth()
            r7 = 1115947008(0x42840000, float:66.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r5 - r7
            goto L_0x137b
        L_0x1366:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 1116340224(0x428a0000, float:69.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r5
        L_0x137b:
            org.telegram.messenger.ImageReceiver r13 = r1.avatarImage
            float r5 = (float) r5
            float r15 = (float) r0
            r16 = 1113587712(0x42600000, float:56.0)
            r17 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            r23 = r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            r13.setImageCoords(r5, r15, r4, r6)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r7
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r0
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r7 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r22)
            float r13 = (float) r13
            r4.setImageCoords(r5, r6, r7, r13)
            r4 = r0
        L_0x13ab:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x13d0
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x13c8
            int r0 = r45.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x13d0
        L_0x13c8:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x13d0:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x1404
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x13ee
            int r0 = r45.getMeasuredWidth()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.errorLeft = r0
            goto L_0x1400
        L_0x13ee:
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.errorLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x1400:
            r0 = r17
            goto L_0x151f
        L_0x1404:
            if (r2 != 0) goto L_0x1430
            if (r14 == 0) goto L_0x1409
            goto L_0x1430
        L_0x1409:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x142a
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x142a
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x142a:
            r2 = 0
            r1.drawCount = r2
            r1.drawMention = r2
            goto L_0x1400
        L_0x1430:
            if (r2 == 0) goto L_0x1493
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r2)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_CENTER
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r26 = r2
            r28 = r5
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r2
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x147f
            int r0 = r45.getMeasuredWidth()
            int r2 = r1.countWidth
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r2
            r1.countLeft = r0
            goto L_0x148f
        L_0x147f:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x148f:
            r2 = 1
            r1.drawCount = r2
            goto L_0x1496
        L_0x1493:
            r2 = 0
            r1.countWidth = r2
        L_0x1496:
            if (r14 == 0) goto L_0x1400
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x14ce
            r2 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r2.measureText(r14)
            double r5 = (double) r2
            double r5 = java.lang.Math.ceil(r5)
            int r2 = (int) r5
            int r0 = java.lang.Math.max(r0, r2)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r27 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r2 = r1.mentionWidth
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_CENTER
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r26 = r14
            r28 = r2
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)
            r1.mentionLayout = r0
            goto L_0x14d6
        L_0x14ce:
            r2 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.mentionWidth = r0
        L_0x14d6:
            int r0 = r1.mentionWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r2
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x14fe
            int r0 = r45.getMeasuredWidth()
            int r2 = r1.mentionWidth
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r2
            int r2 = r1.countWidth
            if (r2 == 0) goto L_0x14f9
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 + r5
            goto L_0x14fa
        L_0x14f9:
            r2 = 0
        L_0x14fa:
            int r0 = r0 - r2
            r1.mentionLeft = r0
            goto L_0x151a
        L_0x14fe:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x150c
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r5 = r5 + r6
            goto L_0x150d
        L_0x150c:
            r5 = 0
        L_0x150d:
            int r2 = r2 + r5
            r1.mentionLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x151a:
            r2 = 1
            r1.drawMention = r2
            goto L_0x1400
        L_0x151f:
            if (r10 == 0) goto L_0x1573
            if (r3 != 0) goto L_0x1525
            r3 = r34
        L_0x1525:
            java.lang.String r2 = r3.toString()
            int r3 = r2.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x1536
            r3 = 0
            java.lang.String r2 = r2.substring(r3, r5)
        L_0x1536:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x153e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1540
        L_0x153e:
            if (r8 == 0) goto L_0x1549
        L_0x1540:
            r3 = 32
            r5 = 10
            java.lang.String r2 = r2.replace(r5, r3)
            goto L_0x1551
        L_0x1549:
            java.lang.String r3 = "\n\n"
            java.lang.String r5 = "\n"
            java.lang.String r2 = r2.replace(r3, r5)
        L_0x1551:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r5 = 1099431936(0x41880000, float:17.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r6)
            org.telegram.messenger.MessageObject r2 = r1.message
            if (r2 == 0) goto L_0x1573
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r2)
            if (r2 == 0) goto L_0x1573
            r3 = r2
        L_0x1573:
            r2 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = java.lang.Math.max(r5, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1585
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x15d9
        L_0x1585:
            if (r8 == 0) goto L_0x15d9
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x1590
            int r5 = r1.currentDialogFolderDialogsCount
            r6 = 1
            if (r5 != r6) goto L_0x15d9
        L_0x1590:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15be }
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x15be }
            if (r0 == 0) goto L_0x15a3
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15be }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x15be }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r8, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x15be }
            if (r0 == 0) goto L_0x15a3
            r8 = r0
        L_0x15a3:
            android.text.TextPaint r35 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x15be }
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x15be }
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            android.text.TextUtils$TruncateAt r41 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x15be }
            r43 = 1
            r34 = r8
            r36 = r2
            r42 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r34, r35, r36, r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x15be }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x15be }
            goto L_0x15c2
        L_0x15be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x15c2:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r5 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            r0.setImageY(r4)
            r5 = 0
            goto L_0x1601
        L_0x15d9:
            r5 = 0
            r1.messageNameLayout = r5
            if (r0 != 0) goto L_0x15ec
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x15e3
            goto L_0x15ec
        L_0x15e3:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1601
        L_0x15ec:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r6 = 1101529088(0x41a80000, float:21.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x1601:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16a6 }
            if (r0 != 0) goto L_0x1609
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16a6 }
            if (r4 == 0) goto L_0x161e
        L_0x1609:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x16a6 }
            if (r4 == 0) goto L_0x161e
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x16a6 }
            r6 = 1
            if (r4 <= r6) goto L_0x161e
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x16a6 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x16a6 }
            r9 = r0[r3]     // Catch:{ Exception -> 0x16a6 }
            r34 = r8
            r35 = r9
            r8 = r5
            goto L_0x1639
        L_0x161e:
            if (r0 != 0) goto L_0x1624
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16a6 }
            if (r0 == 0) goto L_0x1626
        L_0x1624:
            if (r8 == 0) goto L_0x1635
        L_0x1626:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x16a6 }
            int r0 = r2 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x16a6 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16a6 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r9, r0, r4)     // Catch:{ Exception -> 0x16a6 }
        L_0x1635:
            r34 = r3
            r35 = r9
        L_0x1639:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16a6 }
            if (r0 != 0) goto L_0x1676
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16a6 }
            if (r0 == 0) goto L_0x1642
            goto L_0x1676
        L_0x1642:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16a6 }
            if (r0 == 0) goto L_0x165e
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16a6 }
            int r7 = r11 + r0
            int r2 = r2 + r7
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x16a6 }
            if (r0 == 0) goto L_0x165e
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x16a6 }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16a6 }
            int r7 = r11 + r4
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x16a6 }
        L_0x165e:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x16a6 }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16a6 }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r25 = r34
            r26 = r35
            r27 = r2
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x16a6 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16a6 }
            goto L_0x16aa
        L_0x1676:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16a6 }
            if (r0 == 0) goto L_0x1683
            if (r8 == 0) goto L_0x1683
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16a6 }
            int r2 = r2 + r0
        L_0x1683:
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16a6 }
            r38 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x16a6 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x16a6 }
            r40 = 0
            android.text.TextUtils$TruncateAt r41 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16a6 }
            if (r8 == 0) goto L_0x1697
            r43 = 1
            goto L_0x1699
        L_0x1697:
            r43 = 2
        L_0x1699:
            r36 = r2
            r39 = r0
            r42 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r34, r35, r36, r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x16a6 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16a6 }
            goto L_0x16aa
        L_0x16a6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16aa:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x17de
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1767
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1767
            android.text.StaticLayout r0 = r1.nameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            boolean r5 = r1.dialogMuted
            if (r5 == 0) goto L_0x16fc
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x16fc
            boolean r5 = r1.drawScam
            if (r5 != 0) goto L_0x16fc
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r12
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
            goto L_0x174f
        L_0x16fc:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x1726
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r12
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
            goto L_0x174f
        L_0x1726:
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x174f
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r12
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
        L_0x174f:
            r5 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x1767
            double r5 = (double) r12
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1767
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x1767:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x17a8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17a8
            r3 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x1778:
            if (r3 >= r0) goto L_0x179e
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r3)
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 != 0) goto L_0x179d
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
            goto L_0x1778
        L_0x179d:
            r4 = 0
        L_0x179e:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x17a8
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x17a8:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1868
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1868
            android.text.StaticLayout r0 = r1.messageNameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1868
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r3)
            double r3 = (double) r0
            double r3 = java.lang.Math.ceil(r3)
            double r5 = (double) r2
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1868
            int r0 = r1.messageNameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.messageNameLeft = r0
            goto L_0x1868
        L_0x17de:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x182d
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x182d
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r12
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1812
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r12
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1812
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x1812:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x181e
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x181e
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x182d
        L_0x181e:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x182d:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1850
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1850
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r7 = 0
        L_0x183a:
            if (r7 >= r0) goto L_0x1849
            android.text.StaticLayout r3 = r1.messageLayout
            float r3 = r3.getLineLeft(r7)
            float r2 = java.lang.Math.min(r2, r3)
            int r7 = r7 + 1
            goto L_0x183a
        L_0x1849:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x1850:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1868
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1868
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1868:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18ac
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x18ac
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x18a8 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x18a8 }
            r6 = r23
            r2 = 1
            if (r6 < r0) goto L_0x187f
            int r6 = r0 + -1
        L_0x187f:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x18a8 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18a8 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x18a8 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18a8 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x18a8 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x18a8 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x18a8 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x18a8 }
            if (r0 == 0) goto L_0x189f
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x18a8 }
            int r0 = r0 + r2
        L_0x189f:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x18a8 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x18a8 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x18a8 }
            goto L_0x18ac
        L_0x18a8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x18ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
    }

    private void drawCheckStatus(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f) {
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
                long j = this.currentDialogId;
                long j2 = tLRPC$Dialog.id;
                boolean z3 = j != j2;
                this.currentDialogId = j2;
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

    /* JADX WARNING: Removed duplicated region for block: B:114:0x01ad  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0206  */
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
            goto L_0x0378
        L_0x003d:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x0101
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            long r6 = r0.currentDialogId
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC$Dialog) r2
            if (r2 == 0) goto L_0x00f6
            if (r1 != 0) goto L_0x0103
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
            if (r6 == 0) goto L_0x007f
            boolean r6 = r6.isUnread()
            if (r6 == 0) goto L_0x007f
            r6 = 1
            goto L_0x0080
        L_0x007f:
            r6 = 0
        L_0x0080:
            r0.lastUnreadState = r6
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r6 == 0) goto L_0x0095
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r6 = org.telegram.messenger.MessagesStorage.getInstance(r6)
            int r6 = r6.getArchiveUnreadCount()
            r0.unreadCount = r6
            r0.mentionCount = r5
            goto L_0x009d
        L_0x0095:
            int r6 = r2.unread_count
            r0.unreadCount = r6
            int r6 = r2.unread_mentions_count
            r0.mentionCount = r6
        L_0x009d:
            boolean r6 = r2.unread_mark
            r0.markUnread = r6
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x00aa
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.edit_date
            goto L_0x00ab
        L_0x00aa:
            r6 = 0
        L_0x00ab:
            r0.currentEditDate = r6
            int r6 = r2.last_message_date
            r0.lastMessageDate = r6
            int r6 = r0.dialogsType
            r7 = 7
            r8 = 8
            if (r6 == r7) goto L_0x00c9
            if (r6 != r8) goto L_0x00bb
            goto L_0x00c9
        L_0x00bb:
            int r6 = r0.currentDialogFolderId
            if (r6 != 0) goto L_0x00c5
            boolean r2 = r2.pinned
            if (r2 == 0) goto L_0x00c5
            r2 = 1
            goto L_0x00c6
        L_0x00c5:
            r2 = 0
        L_0x00c6:
            r0.drawPin = r2
            goto L_0x00eb
        L_0x00c9:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.messenger.MessagesController$DialogFilter[] r6 = r6.selectedDialogFilter
            int r7 = r0.dialogsType
            if (r7 != r8) goto L_0x00d7
            r7 = 1
            goto L_0x00d8
        L_0x00d7:
            r7 = 0
        L_0x00d8:
            r6 = r6[r7]
            if (r6 == 0) goto L_0x00e8
            android.util.LongSparseArray<java.lang.Integer> r6 = r6.pinnedDialogs
            long r7 = r2.id
            int r2 = r6.indexOfKey(r7)
            if (r2 < 0) goto L_0x00e8
            r2 = 1
            goto L_0x00e9
        L_0x00e8:
            r2 = 0
        L_0x00e9:
            r0.drawPin = r2
        L_0x00eb:
            org.telegram.messenger.MessageObject r2 = r0.message
            if (r2 == 0) goto L_0x0103
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            int r2 = r2.send_state
            r0.lastSendState = r2
            goto L_0x0103
        L_0x00f6:
            r0.unreadCount = r5
            r0.mentionCount = r5
            r0.currentEditDate = r5
            r0.lastMessageDate = r5
            r0.clearingDialog = r5
            goto L_0x0103
        L_0x0101:
            r0.drawPin = r5
        L_0x0103:
            if (r1 == 0) goto L_0x020a
            org.telegram.tgnet.TLRPC$User r2 = r0.user
            if (r2 == 0) goto L_0x0124
            r2 = r1 & 4
            if (r2 == 0) goto L_0x0124
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            int r6 = r6.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r6)
            r0.user = r2
            r19.invalidate()
        L_0x0124:
            boolean r2 = r0.isDialogCell
            if (r2 == 0) goto L_0x014e
            r2 = r1 & 64
            if (r2 == 0) goto L_0x014e
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r6 = r0.currentDialogId
            java.lang.CharSequence r2 = r2.getPrintingString(r6, r5, r4)
            java.lang.CharSequence r6 = r0.lastPrintString
            if (r6 == 0) goto L_0x013e
            if (r2 == 0) goto L_0x014c
        L_0x013e:
            if (r6 != 0) goto L_0x0142
            if (r2 != 0) goto L_0x014c
        L_0x0142:
            if (r6 == 0) goto L_0x014e
            if (r2 == 0) goto L_0x014e
            boolean r2 = r6.equals(r2)
            if (r2 != 0) goto L_0x014e
        L_0x014c:
            r2 = 1
            goto L_0x014f
        L_0x014e:
            r2 = 0
        L_0x014f:
            if (r2 != 0) goto L_0x0162
            r6 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r1
            if (r6 == 0) goto L_0x0162
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x0162
            java.lang.CharSequence r6 = r6.messageText
            java.lang.CharSequence r7 = r0.lastMessageString
            if (r6 == r7) goto L_0x0162
            r2 = 1
        L_0x0162:
            if (r2 != 0) goto L_0x016d
            r6 = r1 & 2
            if (r6 == 0) goto L_0x016d
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x016d
            r2 = 1
        L_0x016d:
            if (r2 != 0) goto L_0x0178
            r6 = r1 & 1
            if (r6 == 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0178
            r2 = 1
        L_0x0178:
            if (r2 != 0) goto L_0x0183
            r6 = r1 & 8
            if (r6 == 0) goto L_0x0183
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x0183
            r2 = 1
        L_0x0183:
            if (r2 != 0) goto L_0x018e
            r6 = r1 & 16
            if (r6 == 0) goto L_0x018e
            org.telegram.tgnet.TLRPC$User r6 = r0.user
            if (r6 != 0) goto L_0x018e
            r2 = 1
        L_0x018e:
            if (r2 != 0) goto L_0x01ef
            r6 = r1 & 256(0x100, float:3.59E-43)
            if (r6 == 0) goto L_0x01ef
            org.telegram.messenger.MessageObject r6 = r0.message
            if (r6 == 0) goto L_0x01a9
            boolean r7 = r0.lastUnreadState
            boolean r6 = r6.isUnread()
            if (r7 == r6) goto L_0x01a9
            org.telegram.messenger.MessageObject r2 = r0.message
            boolean r2 = r2.isUnread()
            r0.lastUnreadState = r2
            r2 = 1
        L_0x01a9:
            boolean r6 = r0.isDialogCell
            if (r6 == 0) goto L_0x01ef
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs_dict
            long r7 = r0.currentDialogId
            java.lang.Object r6 = r6.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r7 == 0) goto L_0x01cd
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)
            int r7 = r7.getArchiveUnreadCount()
        L_0x01cb:
            r8 = 0
            goto L_0x01d6
        L_0x01cd:
            if (r6 == 0) goto L_0x01d4
            int r7 = r6.unread_count
            int r8 = r6.unread_mentions_count
            goto L_0x01d6
        L_0x01d4:
            r7 = 0
            goto L_0x01cb
        L_0x01d6:
            if (r6 == 0) goto L_0x01ef
            int r9 = r0.unreadCount
            if (r9 != r7) goto L_0x01e6
            boolean r9 = r0.markUnread
            boolean r10 = r6.unread_mark
            if (r9 != r10) goto L_0x01e6
            int r9 = r0.mentionCount
            if (r9 == r8) goto L_0x01ef
        L_0x01e6:
            r0.unreadCount = r7
            r0.mentionCount = r8
            boolean r2 = r6.unread_mark
            r0.markUnread = r2
            r2 = 1
        L_0x01ef:
            if (r2 != 0) goto L_0x0204
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x0204
            org.telegram.messenger.MessageObject r1 = r0.message
            if (r1 == 0) goto L_0x0204
            int r6 = r0.lastSendState
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.send_state
            if (r6 == r1) goto L_0x0204
            r0.lastSendState = r1
            r2 = 1
        L_0x0204:
            if (r2 != 0) goto L_0x020a
            r19.invalidate()
            return
        L_0x020a:
            r0.user = r3
            r0.chat = r3
            r0.encryptedChat = r3
            int r1 = r0.currentDialogFolderId
            r2 = 0
            if (r1 == 0) goto L_0x0227
            r0.dialogMuted = r5
            org.telegram.messenger.MessageObject r1 = r19.findFolderTopMessage()
            r0.message = r1
            if (r1 == 0) goto L_0x0225
            long r6 = r1.getDialogId()
            goto L_0x0240
        L_0x0225:
            r6 = r2
            goto L_0x0240
        L_0x0227:
            boolean r1 = r0.isDialogCell
            if (r1 == 0) goto L_0x023b
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r6 = r0.currentDialogId
            boolean r1 = r1.isDialogMuted(r6)
            if (r1 == 0) goto L_0x023b
            r1 = 1
            goto L_0x023c
        L_0x023b:
            r1 = 0
        L_0x023c:
            r0.dialogMuted = r1
            long r6 = r0.currentDialogId
        L_0x0240:
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x02e1
            int r1 = (int) r6
            r2 = 32
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x0293
            if (r1 >= 0) goto L_0x0282
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            r0.chat = r1
            boolean r2 = r0.isDialogCell
            if (r2 != 0) goto L_0x02b9
            if (r1 == 0) goto L_0x02b9
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            if (r1 == 0) goto L_0x02b9
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.chat
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.migrated_to
            int r2 = r2.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x02b9
            r0.chat = r1
            goto L_0x02b9
        L_0x0282:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            r0.user = r1
            goto L_0x02b9
        L_0x0293:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
            r0.encryptedChat = r1
            if (r1 == 0) goto L_0x02b9
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.encryptedChat
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02b9:
            boolean r1 = r0.useMeForMyMessages
            if (r1 == 0) goto L_0x02e1
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x02e1
            org.telegram.messenger.MessageObject r1 = r0.message
            boolean r1 = r1.isOutOwner()
            if (r1 == 0) goto L_0x02e1
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.clientUserId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r0.user = r1
        L_0x02e1:
            int r1 = r0.currentDialogFolderId
            if (r1 == 0) goto L_0x02ff
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
            goto L_0x0378
        L_0x02ff:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            if (r1 == 0) goto L_0x035c
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r1 == 0) goto L_0x0325
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 12
            r1.setAvatarType(r2)
            org.telegram.messenger.ImageReceiver r3 = r0.avatarImage
            r4 = 0
            r5 = 0
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            org.telegram.tgnet.TLRPC$User r8 = r0.user
            r9 = 0
            r3.setImage(r4, r5, r6, r7, r8, r9)
            goto L_0x0378
        L_0x0325:
            org.telegram.tgnet.TLRPC$User r1 = r0.user
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x0344
            boolean r1 = r0.useMeForMyMessages
            if (r1 != 0) goto L_0x0344
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
            goto L_0x0378
        L_0x0344:
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
            goto L_0x0378
        L_0x035c:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.chat
            if (r1 == 0) goto L_0x0378
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
        L_0x0378:
            int r1 = r19.getMeasuredWidth()
            if (r1 != 0) goto L_0x0389
            int r1 = r19.getMeasuredHeight()
            if (r1 == 0) goto L_0x0385
            goto L_0x0389
        L_0x0385:
            r19.requestLayout()
            goto L_0x038c
        L_0x0389:
            r19.buildLayout()
        L_0x038c:
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
        float f3 = this.translationX;
        if (f3 != 0.0f) {
            this.isSliding = true;
        }
        if (this.isSliding) {
            boolean z2 = this.drawRevealBackground;
            if (Math.abs(f3) >= ((float) getMeasuredWidth()) * 0.3f) {
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
    /* JADX WARNING: Removed duplicated region for block: B:143:0x04a2  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x04b1  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x057f  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x05ce  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0689  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0762  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x078e  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0820  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x09f1  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a43  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b3a  */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0b52  */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0bb7  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0bc1  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0cb3  */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0cd9  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0cf9  */
    /* JADX WARNING: Removed duplicated region for block: B:543:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r31) {
        /*
            r30 = this;
            r8 = r30
            r9 = r31
            int r10 = android.os.Build.VERSION.SDK_INT
            long r0 = r8.currentDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0013
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 != 0) goto L_0x0013
            return
        L_0x0013:
            int r0 = r8.currentDialogFolderId
            r11 = 0
            if (r0 == 0) goto L_0x002c
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x002c
            float r1 = r0.outProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x002c
            float r1 = r8.translationX
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x002c
            r0.draw(r9)
            return
        L_0x002c:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r8.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x003c
            r12 = r4
            goto L_0x003d
        L_0x003c:
            r12 = r2
        L_0x003d:
            r8.lastUpdateTime = r0
            float r0 = r8.clipProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0069
            r0 = 24
            if (r10 == r0) goto L_0x0069
            r31.save()
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r0 = r0 * r1
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r30.getMeasuredHeight()
            int r3 = r8.bottomClip
            float r3 = (float) r3
            float r4 = r8.clipProgress
            float r3 = r3 * r4
            int r3 = (int) r3
            int r2 = r2 - r3
            float r2 = (float) r2
            r9.clipRect(r11, r0, r1, r2)
        L_0x0069:
            float r0 = r8.translationX
            r14 = 1090519040(0x41000000, float:8.0)
            r15 = 2
            r7 = 0
            r6 = 1
            r5 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x009b
            float r0 = r8.cornerProgress
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x007d
            goto L_0x009b
        L_0x007d:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            if (r0 == 0) goto L_0x0094
            r0.stop()
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r1 = 0
            r0.setCallback(r1)
            r0 = 0
            r8.translationDrawable = r0
            r8.translationAnimationStarted = r7
        L_0x0094:
            r20 = r12
            r11 = 1
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0320
        L_0x009b:
            r31.save()
            int r0 = r8.currentDialogFolderId
            java.lang.String r16 = "chats_archivePinBackground"
            java.lang.String r17 = "chats_archiveBackground"
            if (r0 == 0) goto L_0x00d6
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x00c0
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627441(0x7f0e0db1, float:1.8882147E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ef
        L_0x00c0:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131625596(0x7f0e067c, float:1.8878404E38)
            java.lang.String r3 = "HideOnTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ef
        L_0x00d6:
            boolean r0 = r8.promoDialog
            if (r0 == 0) goto L_0x00f2
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r3
        L_0x00ef:
            r4 = r1
            r3 = r2
            goto L_0x0122
        L_0x00f2:
            int r0 = r8.folderId
            if (r0 != 0) goto L_0x010c
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.String r3 = "Archive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ef
        L_0x010c:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627432(0x7f0e0da8, float:1.8882128E38)
            java.lang.String r3 = "Unarchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ef
        L_0x0122:
            boolean r1 = r8.translationAnimationStarted
            r18 = 1110179840(0x422CLASSNAME, float:43.0)
            if (r1 != 0) goto L_0x0148
            float r1 = r8.translationX
            float r1 = java.lang.Math.abs(r1)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0148
            r8.translationAnimationStarted = r6
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.start()
        L_0x0148:
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            float r2 = r8.translationX
            float r2 = r2 + r1
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 >= 0) goto L_0x01cc
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1.setColor(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r0 = (float) r0
            float r0 = r2 - r0
            r19 = 0
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            int r5 = r30.getMeasuredHeight()
            float r5 = (float) r5
            android.graphics.Paint r21 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r22 = r1
            r1 = r31
            r23 = r2
            r2 = r0
            r0 = r3
            r3 = r19
            r24 = r4
            r4 = r22
            r6 = r21
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x01d1
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x019a
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r17)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r7
        L_0x019a:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x01d1
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
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r7
            goto L_0x01d1
        L_0x01cc:
            r23 = r2
            r0 = r3
            r24 = r4
        L_0x01d1:
            int r1 = r30.getMeasuredWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r15
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x01ee
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x01eb
            goto L_0x01ee
        L_0x01eb:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x01f0
        L_0x01ee:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x01f0:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r15
            int r3 = r3 + r1
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r15
            int r4 = r4 + r2
            float r5 = r8.currentRevealProgress
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 <= 0) goto L_0x029d
            r31.save()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r5 = (float) r5
            r6 = r23
            float r5 = r6 - r5
            int r14 = r30.getMeasuredWidth()
            float r14 = (float) r14
            int r7 = r30.getMeasuredHeight()
            float r7 = (float) r7
            r9.clipRect(r5, r11, r14, r7)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r7 = r24
            r5.setColor(r7)
            int r5 = r3 * r3
            int r7 = r30.getMeasuredHeight()
            int r7 = r4 - r7
            int r14 = r30.getMeasuredHeight()
            int r14 = r4 - r14
            int r7 = r7 * r14
            int r5 = r5 + r7
            r20 = r12
            double r11 = (double) r5
            double r11 = java.lang.Math.sqrt(r11)
            float r5 = (float) r11
            float r3 = (float) r3
            float r4 = (float) r4
            android.view.animation.AccelerateInterpolator r7 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            float r11 = r8.currentRevealProgress
            float r7 = r7.getInterpolation(r11)
            float r5 = r5 * r7
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r3, r4, r5, r7)
            r31.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x026a
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r4 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r16)
            java.lang.String r5 = "Arrow.**"
            r3.setLayerColor(r5, r4)
            r11 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r11
            goto L_0x026b
        L_0x026a:
            r11 = 1
        L_0x026b:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x02a2
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
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r11
            goto L_0x02a2
        L_0x029d:
            r20 = r12
            r6 = r23
            r11 = 1
        L_0x02a2:
            r31.save()
            float r1 = (float) r1
            float r2 = (float) r2
            r9.translate(r1, r2)
            float r1 = r8.currentRevealBounceProgress
            r2 = 0
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            r12 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x02d1
            int r2 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x02d1
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r2 = r8.interpolator
            float r1 = r2.getInterpolation(r1)
            float r1 = r1 + r12
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r15
            float r2 = (float) r2
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicHeight()
            int r3 = r3 / r15
            float r3 = (float) r3
            r9.scale(r1, r1, r2, r3)
        L_0x02d1:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r2 = 0
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r1, (int) r2, (int) r2)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.draw(r9)
            r31.restore()
            int r1 = r30.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r30.getMeasuredHeight()
            float r2 = (float) r2
            r3 = 0
            r9.clipRect(r6, r3, r1, r2)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r1 = r1.measureText(r0)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            int r2 = r30.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 - r3
            int r1 = r1 / r15
            int r2 = r2 - r1
            float r1 = (float) r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0311
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x030e
            goto L_0x0311
        L_0x030e:
            r2 = 1114374144(0x426CLASSNAME, float:59.0)
            goto L_0x0313
        L_0x0311:
            r2 = 1115160576(0x42780000, float:62.0)
        L_0x0313:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r9.drawText(r0, r1, r2, r3)
            r31.restore()
        L_0x0320:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x032f
            r31.save()
            float r0 = r8.translationX
            r9.translate(r0, r1)
        L_0x032f:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x0346
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0346:
            int r0 = r8.currentDialogFolderId
            java.lang.String r7 = "chats_pinnedOverlay"
            if (r0 == 0) goto L_0x037b
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0357
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x037b
        L_0x0357:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r12)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x039f
        L_0x037b:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0383
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x039f
        L_0x0383:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x039f:
            float r0 = r8.translationX
            java.lang.String r13 = "windowBackgroundWhite"
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x03b4
            float r0 = r8.cornerProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x03af
            goto L_0x03b4
        L_0x03af:
            r6 = 0
            r16 = 1090519040(0x41000000, float:8.0)
            goto L_0x0466
        L_0x03b4:
            r31.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r30.getMeasuredWidth()
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r30.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r30.getMeasuredHeight()
            float r3 = (float) r3
            r4 = 0
            r0.set(r1, r4, r2, r3)
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
            if (r0 == 0) goto L_0x0432
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0406
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0432
        L_0x0406:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            float r2 = r8.archiveBackgroundProgress
            r6 = 0
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r6, r1, r2, r12)
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
            goto L_0x043c
        L_0x0432:
            r6 = 0
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x043f
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x043c
            goto L_0x043f
        L_0x043c:
            r16 = 1090519040(0x41000000, float:8.0)
            goto L_0x0463
        L_0x043f:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            r16 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r1 = (float) r1
            float r2 = r8.cornerProgress
            float r1 = r1 * r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r2 = (float) r2
            float r3 = r8.cornerProgress
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r1, r2, r3)
        L_0x0463:
            r31.restore()
        L_0x0466:
            float r0 = r8.translationX
            r17 = 1125515264(0x43160000, float:150.0)
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0484
            float r0 = r8.cornerProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            r4 = r20
            if (r1 >= 0) goto L_0x049c
            float r1 = (float) r4
            float r1 = r1 / r17
            float r0 = r0 + r1
            r8.cornerProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0499
            r8.cornerProgress = r12
            goto L_0x0499
        L_0x0484:
            r4 = r20
            float r0 = r8.cornerProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x049c
            float r2 = (float) r4
            float r2 = r2 / r17
            float r0 = r0 - r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0499
            r8.cornerProgress = r1
        L_0x0499:
            r18 = 1
            goto L_0x049e
        L_0x049c:
            r18 = 0
        L_0x049e:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x04b1
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x04e9
        L_0x04b1:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x04c4
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x04e9
        L_0x04c4:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x04d7
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x04e9
        L_0x04d7:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x04e9
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x04e9:
            android.text.StaticLayout r0 = r8.nameLayout
            r19 = 1092616192(0x41200000, float:10.0)
            if (r0 == 0) goto L_0x055f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0507
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x053b
        L_0x0507:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x0528
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x0514
            int r0 = r0.type
            if (r0 != r15) goto L_0x0514
            goto L_0x0528
        L_0x0514:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_name"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x053b
        L_0x0528:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_secretName"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x053b:
            r31.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x054d
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x054a
            goto L_0x054d
        L_0x054a:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x054f
        L_0x054d:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x054f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r31.restore()
        L_0x055f:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x057b
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x057b
            r31.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r31.restore()
        L_0x057b:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x05c9
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0591
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessageArchived_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x05b0
        L_0x0591:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x05a3
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_draft"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
            goto L_0x05b0
        L_0x05a3:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            java.lang.String r1 = "chats_nameMessage_threeLines"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r0.setColor(r1)
        L_0x05b0:
            r31.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x05c2 }
            r0.draw(r9)     // Catch:{ Exception -> 0x05c2 }
            goto L_0x05c6
        L_0x05c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05c6:
            r31.restore()
        L_0x05c9:
            android.text.StaticLayout r0 = r8.messageLayout
            r7 = 4
            if (r0 == 0) goto L_0x0685
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x05fe
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x05ea
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_nameMessageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0611
        L_0x05ea:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_messageArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
            goto L_0x0611
        L_0x05fe:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r2 = r0[r1]
            r0 = r0[r1]
            java.lang.String r1 = "chats_message"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.linkColor = r1
            r2.setColor(r1)
        L_0x0611:
            r31.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x0623 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0623 }
            goto L_0x0627
        L_0x0623:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0627:
            r31.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0685
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0685
            r31.save()
            int r1 = r8.printingStringType
            if (r1 == r11) goto L_0x065a
            if (r1 != r7) goto L_0x063e
            goto L_0x065a
        L_0x063e:
            int r1 = r8.messageLeft
            float r1 = (float) r1
            int r2 = r8.messageTop
            float r2 = (float) r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r20 = r0.getIntrinsicHeight()
            int r3 = r3 - r20
            float r3 = (float) r3
            r20 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r20
            float r2 = r2 + r3
            r9.translate(r1, r2)
            goto L_0x066c
        L_0x065a:
            int r2 = r8.messageLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            if (r1 != r11) goto L_0x0666
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x0667
        L_0x0666:
            r1 = 0
        L_0x0667:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x066c:
            r0.draw(r9)
            int r1 = r8.messageLeft
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r6 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r6 = r6 + r0
            r8.invalidate(r1, r2, r3, r6)
            r31.restore()
        L_0x0685:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0753
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0691
            r2 = 2
            goto L_0x0692
        L_0x0691:
            r2 = 0
        L_0x0692:
            int r0 = r0 + r2
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0699
            r2 = 4
            goto L_0x069a
        L_0x0699:
            r2 = 0
        L_0x069a:
            int r0 = r0 + r2
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x06a8
            if (r1 == r0) goto L_0x06a8
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x06a8
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x06a8:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x06ae
            int r0 = r8.animateToStatusDrawableParams
        L_0x06ae:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x06b5
            r21 = 1
            goto L_0x06b7
        L_0x06b5:
            r21 = 0
        L_0x06b7:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x06be
            r22 = 1
            goto L_0x06c0
        L_0x06be:
            r22 = 0
        L_0x06c0:
            r0 = r0 & r7
            if (r0 == 0) goto L_0x06c5
            r0 = 1
            goto L_0x06c6
        L_0x06c5:
            r0 = 0
        L_0x06c6:
            if (r1 == 0) goto L_0x072a
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x06d0
            r3 = 1
            goto L_0x06d1
        L_0x06d0:
            r3 = 0
        L_0x06d1:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x06d7
            r6 = 1
            goto L_0x06d8
        L_0x06d7:
            r6 = 0
        L_0x06d8:
            r1 = r1 & r7
            if (r1 == 0) goto L_0x06de
            r23 = 1
            goto L_0x06e0
        L_0x06de:
            r23 = 0
        L_0x06e0:
            if (r21 != 0) goto L_0x0707
            if (r3 != 0) goto L_0x0707
            if (r23 == 0) goto L_0x0707
            if (r6 != 0) goto L_0x0707
            if (r22 == 0) goto L_0x0707
            if (r0 == 0) goto L_0x0707
            r6 = 1
            float r3 = r8.statusDrawableProgress
            r1 = r30
            r2 = r31
            r23 = r3
            r3 = r21
            r26 = r4
            r4 = r22
            r5 = r0
            r20 = 0
            r14 = 0
            r20 = 4
            r7 = r23
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x073e
        L_0x0707:
            r26 = r4
            r14 = 0
            r20 = 4
            r7 = 0
            float r1 = r8.statusDrawableProgress
            float r25 = r12 - r1
            r1 = r30
            r2 = r31
            r4 = r6
            r5 = r23
            r6 = r7
            r7 = r25
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            r6 = 0
            float r7 = r8.statusDrawableProgress
            r3 = r21
            r4 = r22
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x073e
        L_0x072a:
            r26 = r4
            r14 = 0
            r20 = 4
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r30
            r2 = r31
            r3 = r21
            r4 = r22
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x073e:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0746
            r7 = 2
            goto L_0x0747
        L_0x0746:
            r7 = 0
        L_0x0747:
            int r0 = r0 + r7
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x074e
            r7 = 4
            goto L_0x074f
        L_0x074e:
            r7 = 0
        L_0x074f:
            int r0 = r0 + r7
            r8.lastStatusDrawableParams = r0
            goto L_0x0756
        L_0x0753:
            r26 = r4
            r14 = 0
        L_0x0756:
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x078e
            boolean r0 = r8.drawVerified
            if (r0 != 0) goto L_0x078e
            boolean r0 = r8.drawScam
            if (r0 != 0) goto L_0x078e
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0772
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x076f
            goto L_0x0772
        L_0x076f:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0773
        L_0x0772:
            r5 = 0
        L_0x0773:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r1 = r1 - r2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x077f
            r2 = 1096286208(0x41580000, float:13.5)
            goto L_0x0781
        L_0x077f:
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0781:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x07f1
        L_0x078e:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x07cf
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07a2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x079f
            goto L_0x07a2
        L_0x079f:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x07a4
        L_0x07a2:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x07a4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07bb
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x07b8
            goto L_0x07bb
        L_0x07b8:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x07bd
        L_0x07bb:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x07bd:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x07f1
        L_0x07cf:
            boolean r0 = r8.drawScam
            if (r0 == 0) goto L_0x07f1
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07e3
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x07e0
            goto L_0x07e3
        L_0x07e0:
            r2 = 1097859072(0x41700000, float:15.0)
            goto L_0x07e5
        L_0x07e3:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x07e5:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r9)
        L_0x07f1:
            boolean r0 = r8.drawReorder
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x07fe
            float r0 = r8.reorderIconProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0816
        L_0x07fe:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            float r3 = r8.reorderIconProgress
            float r3 = r3 * r1
            int r3 = (int) r3
            r0.setAlpha(r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            int r3 = r8.pinLeft
            int r4 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r3, (int) r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            r0.draw(r9)
        L_0x0816:
            boolean r0 = r8.drawError
            r3 = 1085276160(0x40b00000, float:5.5)
            r4 = 1102577664(0x41b80000, float:23.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x086f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r6 = r8.reorderIconProgress
            float r6 = r12 - r6
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r6 = (float) r1
            int r7 = r8.errorTop
            float r7 = (float) r7
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r20
            float r1 = (float) r1
            int r2 = r8.errorTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r4
            float r2 = (float) r2
            r0.set(r6, r7, r1, r2)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r1 * r5
            float r1 = r1 * r5
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r9.drawRoundRect(r0, r2, r1, r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            r3 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r9)
            goto L_0x09b8
        L_0x086f:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0898
            boolean r2 = r8.drawMention
            if (r2 == 0) goto L_0x0878
            goto L_0x0898
        L_0x0878:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x09b8
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r5 = r12 - r2
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r9)
            goto L_0x09b8
        L_0x0898:
            if (r0 == 0) goto L_0x090c
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x08a6
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x08a3
            goto L_0x08a6
        L_0x08a3:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x08a8
        L_0x08a6:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x08a8:
            float r2 = r8.reorderIconProgress
            float r2 = r12 - r2
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r6 = r8.reorderIconProgress
            float r6 = r12 - r6
            float r6 = r6 * r1
            int r6 = (int) r6
            r2.setAlpha(r6)
            int r2 = r8.countLeft
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r6
            android.graphics.RectF r6 = r8.rect
            float r7 = (float) r2
            int r14 = r8.countTop
            float r14 = (float) r14
            int r11 = r8.countWidth
            int r2 = r2 + r11
            r11 = 1093664768(0x41300000, float:11.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r2 = r2 + r11
            float r2 = (float) r2
            int r11 = r8.countTop
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = r11 + r21
            float r11 = (float) r11
            r6.set(r7, r14, r2, r11)
            android.graphics.RectF r2 = r8.rect
            float r6 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r6 * r5
            float r6 = r6 * r5
            r9.drawRoundRect(r2, r7, r6, r0)
            android.text.StaticLayout r0 = r8.countLayout
            if (r0 == 0) goto L_0x090c
            r31.save()
            int r0 = r8.countLeft
            float r0 = (float) r0
            int r2 = r8.countTop
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r6
            float r2 = (float) r2
            r9.translate(r0, r2)
            android.text.StaticLayout r0 = r8.countLayout
            r0.draw(r9)
            r31.restore()
        L_0x090c:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x09b8
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r2 = r8.reorderIconProgress
            float r2 = r12 - r2
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            int r0 = r8.mentionLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r2
            android.graphics.RectF r2 = r8.rect
            float r3 = (float) r0
            int r6 = r8.countTop
            float r6 = (float) r6
            int r7 = r8.mentionWidth
            int r0 = r0 + r7
            r7 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            float r0 = (float) r0
            int r7 = r8.countTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 + r4
            float r4 = (float) r7
            r2.set(r3, r6, r0, r4)
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x094a
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x094a
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x094c
        L_0x094a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x094c:
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r3 * r5
            float r3 = r3 * r5
            r9.drawRoundRect(r2, r4, r3, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0983
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r2 = r8.reorderIconProgress
            float r5 = r12 - r2
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            r31.save()
            int r0 = r8.mentionLeft
            float r0 = (float) r0
            int r1 = r8.countTop
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.mentionLayout
            r0.draw(r9)
            r31.restore()
            goto L_0x09b8
        L_0x0983:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r2 = r8.reorderIconProgress
            float r5 = r12 - r2
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r1 = r8.mentionLeft
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
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
        L_0x09b8:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x09da
            r31.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 + r12
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r9.scale(r0, r0, r1, r2)
        L_0x09da:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x09e8
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x09e8
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x09ed
        L_0x09e8:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x09ed:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0a24
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0a24
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            float r0 = r0.getCenterX()
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r1 = r1.getIntrinsicWidth()
            int r1 = r1 / r15
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            org.telegram.messenger.ImageReceiver r1 = r8.thumbImage
            float r1 = r1.getCenterY()
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            int r2 = r2.getIntrinsicHeight()
            int r2 = r2 / r15
            float r2 = (float) r2
            float r1 = r1 - r2
            int r1 = (int) r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r0, (int) r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            r0.draw(r9)
        L_0x0a24:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0a2b
            r31.restore()
        L_0x0a2b:
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            if (r0 == 0) goto L_0x0b31
            boolean r1 = r8.isDialogCell
            if (r1 == 0) goto L_0x0b31
            int r1 = r8.currentDialogFolderId
            if (r1 != 0) goto L_0x0b31
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0b31
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r1 = r0.bot
            if (r1 != 0) goto L_0x0b31
            boolean r1 = r0.self
            if (r1 != 0) goto L_0x0a71
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0a59
            int r0 = r0.expires
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            if (r0 > r1) goto L_0x0a6f
        L_0x0a59:
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r1 = r8.user
            int r1 = r1.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.containsKey(r1)
            if (r0 == 0) goto L_0x0a71
        L_0x0a6f:
            r0 = 1
            goto L_0x0a72
        L_0x0a71:
            r0 = 0
        L_0x0a72:
            if (r0 != 0) goto L_0x0a7b
            float r1 = r8.onlineProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0b31
        L_0x0a7b:
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getImageY2()
            boolean r3 = r8.useForceThreeLines
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r3 != 0) goto L_0x0a8f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a8c
            goto L_0x0a8f
        L_0x0a8c:
            r14 = 1090519040(0x41000000, float:8.0)
            goto L_0x0a91
        L_0x0a8f:
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0a91:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            float r1 = r1 - r3
            int r1 = (int) r1
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0ab4
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r5 = r8.useForceThreeLines
            if (r5 != 0) goto L_0x0aad
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0aab
            goto L_0x0aad
        L_0x0aab:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0aad:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r3 = r3 + r4
            goto L_0x0acb
        L_0x0ab4:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r5 = r8.useForceThreeLines
            if (r5 != 0) goto L_0x0ac5
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0ac3
            goto L_0x0ac5
        L_0x0ac3:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ac5:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r3 = r3 - r4
        L_0x0acb:
            int r3 = (int) r3
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setColor(r5)
            float r3 = (float) r3
            float r1 = (float) r1
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r8.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r1, r4, r5)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "chats_onlineCircle"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r8.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r1, r4, r5)
            if (r0 == 0) goto L_0x0b19
            float r0 = r8.onlineProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x0b31
            r5 = r26
            float r1 = (float) r5
            float r1 = r1 / r17
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0b2e
            r8.onlineProgress = r12
            goto L_0x0b2e
        L_0x0b19:
            r5 = r26
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b33
            float r2 = (float) r5
            float r2 = r2 / r17
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0b2e
            r8.onlineProgress = r1
        L_0x0b2e:
            r18 = 1
            goto L_0x0b33
        L_0x0b31:
            r5 = r26
        L_0x0b33:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0b3d
            r31.restore()
        L_0x0b3d:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b4e
            float r0 = r8.translationX
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0b4e
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0b4e
            r0.draw(r9)
        L_0x0b4e:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x0bb7
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x0b72
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b62
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x0b62
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x0b72
        L_0x0b62:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x0b6b
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x0b6b
            goto L_0x0b72
        L_0x0b6b:
            r0 = 1116733440(0x42900000, float:72.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x0b73
        L_0x0b72:
            r0 = 0
        L_0x0b73:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0b9a
            r2 = 0
            int r1 = r30.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r30.getMeasuredWidth()
            int r1 = r1 - r0
            float r0 = (float) r1
            int r1 = r30.getMeasuredHeight()
            int r1 = r1 - r3
            float r14 = (float) r1
            android.graphics.Paint r16 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r3 = r4
            r4 = r0
            r28 = r5
            r5 = r14
            r6 = r16
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0bb9
        L_0x0b9a:
            r28 = r5
            float r2 = (float) r0
            int r0 = r30.getMeasuredHeight()
            r14 = 1
            int r0 = r0 - r14
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            int r0 = r0 - r14
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r31
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0bba
        L_0x0bb7:
            r28 = r5
        L_0x0bb9:
            r14 = 1
        L_0x0bba:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0CLASSNAME
            r0 = 24
            if (r10 == r0) goto L_0x0bc9
            r31.restore()
            goto L_0x0CLASSNAME
        L_0x0bc9:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r30.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r30.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r30.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r31
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0CLASSNAME:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0CLASSNAME
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = r28
            r1 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r0 == 0) goto L_0x0c2d
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x0CLASSNAME
            r4 = r28
            float r1 = (float) r4
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0c2b
            r8.reorderIconProgress = r12
        L_0x0c2b:
            r1 = 0
            goto L_0x0CLASSNAME
        L_0x0c2d:
            r4 = r28
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0CLASSNAME
            float r2 = (float) r4
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0CLASSNAME
            r8.reorderIconProgress = r1
        L_0x0CLASSNAME:
            r6 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = r18
        L_0x0CLASSNAME:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x0CLASSNAME
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c9b
            float r2 = (float) r4
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0c5c
            r8.archiveBackgroundProgress = r1
        L_0x0c5c:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r15) goto L_0x0c9a
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r8.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
            goto L_0x0c9a
        L_0x0CLASSNAME:
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r2 >= 0) goto L_0x0c9b
            float r2 = (float) r4
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0CLASSNAME
            r8.archiveBackgroundProgress = r12
        L_0x0CLASSNAME:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            if (r0 != r15) goto L_0x0c9a
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r3 = r8.archiveBackgroundProgress
            float r2 = r2.getInterpolation(r3)
            r0.setArchivedAvatarHiddenProgress(r2)
        L_0x0c9a:
            r6 = 1
        L_0x0c9b:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0caf
            float r0 = r8.animatingArchiveAvatarProgress
            float r2 = (float) r4
            float r0 = r0 + r2
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x0cae
            r8.animatingArchiveAvatarProgress = r7
            r2 = 0
            r8.animatingArchiveAvatar = r2
        L_0x0cae:
            r6 = 1
        L_0x0caf:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x0cd9
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x0cc5
            float r1 = (float) r4
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0cc5
            r8.currentRevealBounceProgress = r12
            r6 = 1
        L_0x0cc5:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x0cf7
            float r1 = (float) r4
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0cf6
            r8.currentRevealProgress = r12
            goto L_0x0cf6
        L_0x0cd9:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x0ce3
            r8.currentRevealBounceProgress = r1
            r6 = 1
        L_0x0ce3:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cf7
            float r2 = (float) r4
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0cf6
            r8.currentRevealProgress = r1
        L_0x0cf6:
            r6 = 1
        L_0x0cf7:
            if (r6 == 0) goto L_0x0cfc
            r30.invalidate()
        L_0x0cfc:
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
                DialogCell.this.lambda$createStatusDrawableAnimator$0$DialogCell(valueAnimator);
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
    /* renamed from: lambda$createStatusDrawableAnimator$0 */
    public /* synthetic */ void lambda$createStatusDrawableAnimator$0$DialogCell(ValueAnimator valueAnimator) {
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
