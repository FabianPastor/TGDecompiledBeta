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
    private boolean drawScam;
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
        this.countChangeProgress = 1.0f;
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

    /* JADX WARNING: Code restructure failed: missing block: B:258:0x05f4, code lost:
        if (r2.post_messages == false) goto L_0x05d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0600, code lost:
        if (r2.kicked != false) goto L_0x05d0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:1000:0x17ea  */
    /* JADX WARNING: Removed duplicated region for block: B:1033:0x187c A[SYNTHETIC, Splitter:B:1033:0x187c] */
    /* JADX WARNING: Removed duplicated region for block: B:1048:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0bdc  */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0c1b  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0da5  */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0e3d  */
    /* JADX WARNING: Removed duplicated region for block: B:628:0x0e45  */
    /* JADX WARNING: Removed duplicated region for block: B:631:0x0e4d  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0e55  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0e72  */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0e82  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0f9b  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0fd6  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0fe6  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x1037  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x1043  */
    /* JADX WARNING: Removed duplicated region for block: B:743:0x1082  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x108d  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x109d  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x10b5  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x10c4  */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x10fd  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1126  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x11aa  */
    /* JADX WARNING: Removed duplicated region for block: B:788:0x11c0  */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x122c A[Catch:{ Exception -> 0x124b }] */
    /* JADX WARNING: Removed duplicated region for block: B:807:0x122f A[Catch:{ Exception -> 0x124b }] */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x1259  */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x1309  */
    /* JADX WARNING: Removed duplicated region for block: B:827:0x13b9  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x13de  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x140e  */
    /* JADX WARNING: Removed duplicated region for block: B:872:0x152b  */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x15ef  */
    /* JADX WARNING: Removed duplicated region for block: B:915:0x15f8  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x161e A[Catch:{ Exception -> 0x16b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:926:0x162a A[ADDED_TO_REGION, Catch:{ Exception -> 0x16b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x1649 A[Catch:{ Exception -> 0x16b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:949:0x16a0 A[Catch:{ Exception -> 0x16b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:950:0x16a3 A[Catch:{ Exception -> 0x16b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:956:0x16ba  */
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
            r0 = 2131625556(0x7f0e0654, float:1.8878323E38)
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
            goto L_0x1041
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
            goto L_0x0e49
        L_0x0666:
            r2 = 0
            r1.lastPrintString = r2
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x06f7
            r0 = 2131625128(0x7f0e04a8, float:1.8877455E38)
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
            r0 = 2131625629(0x7f0e069d, float:1.8878471E38)
            java.lang.String r2 = "HistoryCleared"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x070a:
            r0 = 0
            r2 = 2
            r8 = 0
            r11 = 1
        L_0x070e:
            r12 = 1
            goto L_0x0e49
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
            r0 = 2131625218(0x7f0e0502, float:1.8877638E38)
            java.lang.String r2 = "EncryptionProcessing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x070a
        L_0x072d:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r2 == 0) goto L_0x0745
            r0 = 2131624470(0x7f0e0216, float:1.887612E38)
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
            r0 = 2131625219(0x7f0e0503, float:1.887764E38)
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
            r0 = 2131625207(0x7f0e04f7, float:1.8877615E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)
            r2[r6] = r3
            java.lang.String r3 = "EncryptedChatStartedOutgoing"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x070a
        L_0x0779:
            r0 = 2131625206(0x7f0e04f6, float:1.8877613E38)
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
            r0 = 2131626999(0x7f0e0bf7, float:1.888125E38)
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
            r0 = 2131626999(0x7f0e0bf7, float:1.888125E38)
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
            goto L_0x0e41
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
            goto L_0x0e41
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
            if (r9 != 0) goto L_0x0934
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r1.encryptedChat
            if (r9 != 0) goto L_0x0934
            boolean r8 = r8.needDrawBluredPreview()
            if (r8 != 0) goto L_0x0934
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 != 0) goto L_0x0862
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isNewGif()
            if (r8 != 0) goto L_0x0862
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x0934
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
            if (r9 != 0) goto L_0x0934
            java.lang.String r9 = "profile"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0934
            java.lang.String r9 = "article"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0934
            if (r8 == 0) goto L_0x0898
            java.lang.String r9 = "telegram_"
            boolean r8 = r8.startsWith(r9)
            if (r8 != 0) goto L_0x0934
        L_0x0898:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            int r14 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r14)
            if (r8 != r9) goto L_0x08b1
            r9 = 0
        L_0x08b1:
            if (r8 == 0) goto L_0x0934
            r1.hasMessageThumb = r5
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVideo()
            r1.drawPlay = r14
            java.lang.String r14 = org.telegram.messenger.FileLoader.getAttachFileName(r9)
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.mediaExists
            if (r15 != 0) goto L_0x0900
            int r15 = r1.currentAccount
            org.telegram.messenger.DownloadController r15 = org.telegram.messenger.DownloadController.getInstance(r15)
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r15.canDownloadMedia((org.telegram.messenger.MessageObject) r3)
            if (r3 != 0) goto L_0x0900
            int r3 = r1.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            boolean r3 = r3.isLoadingFile(r14)
            if (r3 == 0) goto L_0x08e2
            goto L_0x0900
        L_0x08e2:
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
            goto L_0x0932
        L_0x0900:
            org.telegram.messenger.MessageObject r3 = r1.message
            int r14 = r3.type
            if (r14 != r5) goto L_0x090f
            if (r9 == 0) goto L_0x090b
            int r14 = r9.size
            goto L_0x090c
        L_0x090b:
            r14 = 0
        L_0x090c:
            r31 = r14
            goto L_0x0911
        L_0x090f:
            r31 = 0
        L_0x0911:
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
        L_0x0932:
            r3 = 0
            goto L_0x0935
        L_0x0934:
            r3 = 1
        L_0x0935:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x0CLASSNAME
            int r9 = r8.id
            if (r9 <= 0) goto L_0x0CLASSNAME
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isMegagroup(r8)
            if (r8 == 0) goto L_0x0CLASSNAME
        L_0x094d:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isOutOwner()
            if (r8 == 0) goto L_0x0960
            r0 = 2131625556(0x7f0e0654, float:1.8878323E38)
            java.lang.String r2 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x095e:
            r2 = r0
            goto L_0x09a3
        L_0x0960:
            if (r0 == 0) goto L_0x0995
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x0976
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x096b
            goto L_0x0976
        L_0x096b:
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x095e
        L_0x0976:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r2 == 0) goto L_0x0986
            r0 = 2131625622(0x7f0e0696, float:1.8878457E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x095e
        L_0x0986:
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x095e
        L_0x0995:
            if (r2 == 0) goto L_0x09a0
            java.lang.String r0 = r2.title
            java.lang.String r2 = "\n"
            java.lang.String r0 = r0.replace(r2, r4)
            goto L_0x095e
        L_0x09a0:
            java.lang.String r0 = "DELETED"
            goto L_0x095e
        L_0x09a3:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r8 = r0.caption
            if (r8 == 0) goto L_0x0a17
            java.lang.String r0 = r8.toString()
            int r8 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x09b9
            java.lang.String r0 = r0.substring(r6, r9)
        L_0x09b9:
            if (r3 != 0) goto L_0x09be
            r3 = r4
        L_0x09bc:
            r8 = 2
            goto L_0x09ed
        L_0x09be:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x09c9
            java.lang.String r3 = "📹 "
            goto L_0x09bc
        L_0x09c9:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x09d4
            java.lang.String r3 = "🎤 "
            goto L_0x09bc
        L_0x09d4:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x09df
            java.lang.String r3 = "🎧 "
            goto L_0x09bc
        L_0x09df:
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x09ea
            java.lang.String r3 = "🖼 "
            goto L_0x09bc
        L_0x09ea:
            java.lang.String r3 = "📎 "
            goto L_0x09bc
        L_0x09ed:
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
            goto L_0x0b83
        L_0x0a17:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            if (r3 == 0) goto L_0x0af7
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0af7
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r3 = r1.paintIndex
            r9 = r0[r3]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0a58
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            r0 = 18
            if (r11 < r0) goto L_0x0a49
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abe
        L_0x0a49:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            java.lang.String r3 = r3.question
            r0[r6] = r3
            java.lang.String r3 = "📊 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abe
        L_0x0a58:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x0a7e
            r8 = 18
            if (r11 < r8) goto L_0x0a6f
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abe
        L_0x0a6f:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0[r6] = r3
            java.lang.String r3 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r3, r0)
            goto L_0x0abe
        L_0x0a7e:
            int r3 = r0.type
            r8 = 14
            if (r3 != r8) goto L_0x0ab8
            r3 = 18
            if (r11 < r3) goto L_0x0aa0
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0abe
        L_0x0aa0:
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r0 = r0.getMusicAuthor()
            r8[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r8[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            goto L_0x0abe
        L_0x0ab8:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
        L_0x0abe:
            r3 = 32
            r8 = 10
            java.lang.String r0 = r0.replace(r8, r3)
            r3 = 2
            java.lang.Object[] r8 = new java.lang.Object[r3]
            r8[r6] = r0
            r8[r5] = r2
            java.lang.String r0 = java.lang.String.format(r12, r8)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0af1 }
            java.lang.String r8 = "chats_attachMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x0af1 }
            if (r13 == 0) goto L_0x0ae5
            int r8 = r2.length()     // Catch:{ Exception -> 0x0af1 }
            r11 = 2
            int r8 = r8 + r11
            goto L_0x0ae6
        L_0x0ae5:
            r8 = 0
        L_0x0ae6:
            int r11 = r3.length()     // Catch:{ Exception -> 0x0af1 }
            r12 = 33
            r3.setSpan(r0, r8, r11, r12)     // Catch:{ Exception -> 0x0af1 }
            goto L_0x0b83
        L_0x0af1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0b83
        L_0x0af7:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0b7c
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0b52
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.messageTrimmedToHighlight
            if (r0 == 0) goto L_0x0b0c
            r3 = r0
        L_0x0b0c:
            int r0 = r45.getMeasuredWidth()
            r8 = 1121058816(0x42d20000, float:105.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            if (r13 == 0) goto L_0x0b39
            boolean r8 = android.text.TextUtils.isEmpty(r2)
            if (r8 != 0) goto L_0x0b2d
            float r0 = (float) r0
            java.lang.String r8 = r2.toString()
            r9 = r16
            float r8 = r9.measureText(r8)
            float r0 = r0 - r8
            int r0 = (int) r0
            goto L_0x0b2f
        L_0x0b2d:
            r9 = r16
        L_0x0b2f:
            float r0 = (float) r0
            java.lang.String r8 = ": "
            float r8 = r9.measureText(r8)
            float r0 = r0 - r8
            int r0 = (int) r0
            goto L_0x0b3b
        L_0x0b39:
            r9 = r16
        L_0x0b3b:
            if (r0 <= 0) goto L_0x0b6c
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r3, r8, r0, r9, r11)
            java.lang.String r3 = r0.toString()
            goto L_0x0b6c
        L_0x0b52:
            r9 = r16
            int r0 = r3.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r0 <= r8) goto L_0x0b60
            java.lang.String r3 = r3.substring(r6, r8)
        L_0x0b60:
            r8 = 32
            r11 = 10
            java.lang.String r0 = r3.replace(r11, r8)
            java.lang.String r3 = r0.trim()
        L_0x0b6c:
            r8 = 2
            java.lang.Object[] r0 = new java.lang.Object[r8]
            r0[r6] = r3
            r0[r5] = r2
            java.lang.String r0 = java.lang.String.format(r12, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0b82
        L_0x0b7c:
            r9 = r16
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x0b82:
            r3 = r0
        L_0x0b83:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0b8b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0b95
        L_0x0b8b:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0bb1
            int r0 = r3.length()
            if (r0 <= 0) goto L_0x0bb1
        L_0x0b95:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0baa }
            java.lang.String r8 = "chats_nameMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x0baa }
            int r8 = r2.length()     // Catch:{ Exception -> 0x0baa }
            int r8 = r8 + r5
            r11 = 33
            r3.setSpan(r0, r6, r8, r11)     // Catch:{ Exception -> 0x0ba8 }
            r0 = r8
            goto L_0x0bb3
        L_0x0ba8:
            r0 = move-exception
            goto L_0x0bac
        L_0x0baa:
            r0 = move-exception
            r8 = 0
        L_0x0bac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0bb3
        L_0x0bb1:
            r0 = 0
            r8 = 0
        L_0x0bb3:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r11 = r11[r12]
            android.graphics.Paint$FontMetricsInt r11 = r11.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r11, r12, r6)
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.hasHighlightedWords()
            if (r11 == 0) goto L_0x0bd8
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r11)
            if (r11 == 0) goto L_0x0bd8
            r3 = r11
        L_0x0bd8:
            boolean r11 = r1.hasMessageThumb
            if (r11 == 0) goto L_0x0CLASSNAME
            boolean r11 = r3 instanceof android.text.SpannableStringBuilder
            if (r11 != 0) goto L_0x0be6
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r11.<init>(r3)
            r3 = r11
        L_0x0be6:
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
        L_0x0CLASSNAME:
            r8 = r2
            r2 = 2
            goto L_0x0807
        L_0x0CLASSNAME:
            r9 = r16
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Photo r8 = r2.photo
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r8 == 0) goto L_0x0CLASSNAME
            int r8 = r2.ttl_seconds
            if (r8 == 0) goto L_0x0CLASSNAME
            r0 = 2131624371(0x7f0e01b3, float:1.887592E38)
            java.lang.String r2 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0CLASSNAME:
            r2 = 2
            goto L_0x0da1
        L_0x0CLASSNAME:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0c3f
            org.telegram.tgnet.TLRPC$Document r8 = r2.document
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r8 == 0) goto L_0x0c3f
            int r8 = r2.ttl_seconds
            if (r8 == 0) goto L_0x0c3f
            r0 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.String r2 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0CLASSNAME
        L_0x0c3f:
            java.lang.CharSequence r8 = r0.caption
            if (r8 == 0) goto L_0x0cef
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
            if (r0 == 0) goto L_0x0c5b
            java.lang.String r0 = "🎤 "
            goto L_0x0CLASSNAME
        L_0x0c5b:
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
            if (r2 == 0) goto L_0x0cda
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0cda
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.String r2 = r2.messageTrimmedToHighlight
            int r3 = r45.getMeasuredWidth()
            r8 = 1122893824(0x42ee0000, float:119.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r3 = r3 - r8
            if (r13 == 0) goto L_0x0cb3
            r8 = 0
            boolean r11 = android.text.TextUtils.isEmpty(r8)
            if (r11 != 0) goto L_0x0caa
            float r3 = (float) r3
            java.lang.String r11 = r8.toString()
            float r8 = r9.measureText(r11)
            float r3 = r3 - r8
            int r3 = (int) r3
        L_0x0caa:
            float r3 = (float) r3
            java.lang.String r8 = ": "
            float r8 = r9.measureText(r8)
            float r3 = r3 - r8
            int r3 = (int) r3
        L_0x0cb3:
            if (r3 <= 0) goto L_0x0cc9
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.Object r8 = r8.get(r6)
            java.lang.String r8 = (java.lang.String) r8
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r2, r8, r3, r9, r11)
            java.lang.String r2 = r2.toString()
        L_0x0cc9:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            goto L_0x0CLASSNAME
        L_0x0cda:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0CLASSNAME
        L_0x0cef:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0d0d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "📊 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            java.lang.String r2 = r2.question
            r0.append(r2)
            java.lang.String r0 = r0.toString()
        L_0x0d0a:
            r2 = 2
            goto L_0x0d8d
        L_0x0d0d:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0d2d
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
            goto L_0x0d0a
        L_0x0d2d:
            int r2 = r0.type
            r3 = 14
            if (r2 != r3) goto L_0x0d4b
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r0 = r0.getMusicAuthor()
            r3[r6] = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.String r0 = r0.getMusicTitle()
            r3[r5] = r0
            java.lang.String r0 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x0d8d
        L_0x0d4b:
            r2 = 2
            boolean r0 = r0.hasHighlightedWords()
            if (r0 == 0) goto L_0x0d82
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d82
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
            goto L_0x0d86
        L_0x0d82:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
        L_0x0d86:
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
        L_0x0d8d:
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            if (r8 == 0) goto L_0x0da1
            boolean r3 = r3.isMediaEmpty()
            if (r3 != 0) goto L_0x0da1
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r9 = r3[r8]
        L_0x0da1:
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x0e3d
            org.telegram.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.hasHighlightedWords()
            if (r3 == 0) goto L_0x0de0
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0de0
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
            goto L_0x0df0
        L_0x0de0:
            int r3 = r0.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r3 <= r8) goto L_0x0dec
            java.lang.CharSequence r0 = r0.subSequence(r6, r8)
        L_0x0dec:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0df0:
            boolean r3 = r0 instanceof android.text.SpannableStringBuilder
            if (r3 != 0) goto L_0x0dfa
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r0)
            r0 = r3
        L_0x0dfa:
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
            if (r8 == 0) goto L_0x0e39
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r8)
            if (r3 == 0) goto L_0x0e39
            goto L_0x0e3a
        L_0x0e39:
            r3 = r0
        L_0x0e3a:
            r0 = 0
            goto L_0x0806
        L_0x0e3d:
            r3 = r0
            r0 = 0
            goto L_0x0839
        L_0x0e41:
            int r13 = r1.currentDialogFolderId
            if (r13 == 0) goto L_0x0e49
            java.lang.CharSequence r8 = r45.formatArchivedDialogNames()
        L_0x0e49:
            org.telegram.tgnet.TLRPC$DraftMessage r13 = r1.draftMessage
            if (r13 == 0) goto L_0x0e55
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e6e
        L_0x0e55:
            int r13 = r1.lastMessageDate
            if (r13 == 0) goto L_0x0e5f
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e6e
        L_0x0e5f:
            org.telegram.messenger.MessageObject r13 = r1.message
            if (r13 == 0) goto L_0x0e6d
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner
            int r13 = r13.date
            long r13 = (long) r13
            java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r13)
            goto L_0x0e6e
        L_0x0e6d:
            r13 = r4
        L_0x0e6e:
            org.telegram.messenger.MessageObject r14 = r1.message
            if (r14 != 0) goto L_0x0e82
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r6
            r1.drawCount = r6
            r1.drawMention = r6
            r1.drawError = r6
            r2 = 0
            r14 = 0
            goto L_0x0var_
        L_0x0e82:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x0ec1
            int r2 = r1.unreadCount
            int r14 = r1.mentionCount
            int r16 = r2 + r14
            if (r16 <= 0) goto L_0x0eba
            if (r2 <= r14) goto L_0x0ea4
            r1.drawCount = r5
            r1.drawMention = r6
            java.lang.Object[] r15 = new java.lang.Object[r5]
            int r2 = r2 + r14
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r15)
            goto L_0x0ebf
        L_0x0ea4:
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
            goto L_0x0var_
        L_0x0eba:
            r1.drawCount = r6
            r1.drawMention = r6
            r2 = 0
        L_0x0ebf:
            r14 = 0
            goto L_0x0var_
        L_0x0ec1:
            boolean r2 = r1.clearingDialog
            if (r2 == 0) goto L_0x0eca
            r1.drawCount = r6
            r2 = 0
            r10 = 0
            goto L_0x0ef8
        L_0x0eca:
            int r2 = r1.unreadCount
            if (r2 == 0) goto L_0x0eed
            if (r2 != r5) goto L_0x0edc
            int r15 = r1.mentionCount
            if (r2 != r15) goto L_0x0edc
            if (r14 == 0) goto L_0x0edc
            org.telegram.tgnet.TLRPC$Message r14 = r14.messageOwner
            boolean r14 = r14.mentioned
            if (r14 != 0) goto L_0x0eed
        L_0x0edc:
            r1.drawCount = r5
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14[r6] = r2
            java.lang.String r2 = "%d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            goto L_0x0ef8
        L_0x0eed:
            boolean r2 = r1.markUnread
            if (r2 == 0) goto L_0x0ef5
            r1.drawCount = r5
            r2 = r4
            goto L_0x0ef8
        L_0x0ef5:
            r1.drawCount = r6
            r2 = 0
        L_0x0ef8:
            int r14 = r1.mentionCount
            if (r14 == 0) goto L_0x0var_
            r1.drawMention = r5
            java.lang.String r14 = "@"
            goto L_0x0var_
        L_0x0var_:
            r1.drawMention = r6
            goto L_0x0ebf
        L_0x0var_:
            org.telegram.messenger.MessageObject r15 = r1.message
            boolean r15 = r15.isOut()
            if (r15 == 0) goto L_0x0f6b
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r1.draftMessage
            if (r15 != 0) goto L_0x0f6b
            if (r10 == 0) goto L_0x0f6b
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r15 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r15 = r15.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r15 != 0) goto L_0x0f6b
            boolean r10 = r10.isSending()
            if (r10 == 0) goto L_0x0f2b
            r1.drawCheck1 = r6
            r1.drawCheck2 = r6
            r1.drawClock = r5
            r1.drawError = r6
            goto L_0x0var_
        L_0x0f2b:
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
            if (r10 == 0) goto L_0x0f5f
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = r10.megagroup
            if (r10 != 0) goto L_0x0f5f
            goto L_0x0var_
        L_0x0f5f:
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
        L_0x0f6b:
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
            if (r15 != 0) goto L_0x0fd1
            long r6 = r1.currentDialogId
            boolean r6 = r10.isPromoDialog(r6, r5)
            if (r6 == 0) goto L_0x0fd1
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r6 = r10.promoDialogType
            if (r6 != 0) goto L_0x0f9b
            r6 = 2131627552(0x7f0e0e20, float:1.8882372E38)
            java.lang.String r7 = "UseProxySponsor"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x0fd2
        L_0x0f9b:
            if (r6 != r5) goto L_0x0fd1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "PsaType_"
            r6.append(r7)
            java.lang.String r7 = r10.promoPsaType
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6)
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 == 0) goto L_0x0fc3
            r6 = 2131626825(0x7f0e0b49, float:1.8880897E38)
            java.lang.String r7 = "PsaTypeDefault"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x0fc3:
            java.lang.String r7 = r10.promoPsaMessage
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0fd2
            java.lang.String r3 = r10.promoPsaMessage
            r7 = 0
            r1.hasMessageThumb = r7
            goto L_0x0fd2
        L_0x0fd1:
            r6 = r13
        L_0x0fd2:
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x0fe6
            r7 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r10 = "ArchivedChats"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r7)
        L_0x0fdf:
            r10 = r12
            r44 = r6
            r6 = r0
            r0 = r44
            goto L_0x1041
        L_0x0fe6:
            org.telegram.tgnet.TLRPC$Chat r7 = r1.chat
            if (r7 == 0) goto L_0x0fee
            java.lang.String r7 = r7.title
        L_0x0fec:
            r13 = r7
            goto L_0x1031
        L_0x0fee:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            if (r7 == 0) goto L_0x1030
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r7)
            if (r7 == 0) goto L_0x1002
            r7 = 2131626904(0x7f0e0b98, float:1.8881057E38)
            java.lang.String r10 = "RepliesTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fec
        L_0x1002:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r7 == 0) goto L_0x1029
            boolean r7 = r1.useMeForMyMessages
            if (r7 == 0) goto L_0x1018
            r7 = 2131625556(0x7f0e0654, float:1.8878323E38)
            java.lang.String r10 = "FromYou"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fec
        L_0x1018:
            int r7 = r1.dialogsType
            r10 = 3
            if (r7 != r10) goto L_0x101f
            r1.drawPinBackground = r5
        L_0x101f:
            r7 = 2131626998(0x7f0e0bf6, float:1.8881248E38)
            java.lang.String r10 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fec
        L_0x1029:
            org.telegram.tgnet.TLRPC$User r7 = r1.user
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            goto L_0x0fec
        L_0x1030:
            r13 = r4
        L_0x1031:
            int r7 = r13.length()
            if (r7 != 0) goto L_0x0fdf
            r7 = 2131625622(0x7f0e0696, float:1.8878457E38)
            java.lang.String r10 = "HiddenName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r7)
            goto L_0x0fdf
        L_0x1041:
            if (r11 == 0) goto L_0x1082
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
            if (r0 != 0) goto L_0x1079
            int r0 = r45.getMeasuredWidth()
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r11
            int r0 = r0 - r7
            r1.timeLeft = r0
            goto L_0x1089
        L_0x1079:
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.timeLeft = r0
            goto L_0x1089
        L_0x1082:
            r7 = 0
            r1.timeLayout = r7
            r7 = 0
            r1.timeLeft = r7
            r7 = 0
        L_0x1089:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x109d
            int r0 = r45.getMeasuredWidth()
            int r11 = r1.nameLeft
            int r0 = r0 - r11
            r11 = 1096810496(0x41600000, float:14.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r11
            int r0 = r0 - r7
            goto L_0x10b1
        L_0x109d:
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
        L_0x10b1:
            boolean r11 = r1.drawNameLock
            if (r11 == 0) goto L_0x10c4
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r12 = r12.getIntrinsicWidth()
        L_0x10c1:
            int r11 = r11 + r12
            int r0 = r0 - r11
            goto L_0x10f7
        L_0x10c4:
            boolean r11 = r1.drawNameGroup
            if (r11 == 0) goto L_0x10d5
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10c1
        L_0x10d5:
            boolean r11 = r1.drawNameBroadcast
            if (r11 == 0) goto L_0x10e6
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10c1
        L_0x10e6:
            boolean r11 = r1.drawNameBot
            if (r11 == 0) goto L_0x10f7
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r12 = r12.getIntrinsicWidth()
            goto L_0x10c1
        L_0x10f7:
            boolean r11 = r1.drawClock
            r12 = 1084227584(0x40a00000, float:5.0)
            if (r11 == 0) goto L_0x1126
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r11 = r11.getIntrinsicWidth()
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r19
            int r0 = r0 - r11
            boolean r19 = org.telegram.messenger.LocaleController.isRTL
            if (r19 != 0) goto L_0x1115
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.clockDrawLeft = r7
            goto L_0x119c
        L_0x1115:
            int r15 = r1.timeLeft
            int r15 = r15 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r7
            r1.clockDrawLeft = r15
            int r7 = r1.nameLeft
            int r7 = r7 + r11
            r1.nameLeft = r7
            goto L_0x119c
        L_0x1126:
            boolean r11 = r1.drawCheck2
            if (r11 == 0) goto L_0x119c
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r11 = r11.getIntrinsicWidth()
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r15
            int r0 = r0 - r11
            boolean r15 = r1.drawCheck1
            if (r15 == 0) goto L_0x1183
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r15 = r15.getIntrinsicWidth()
            r25 = 1090519040(0x41000000, float:8.0)
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r25)
            int r15 = r15 - r25
            int r0 = r0 - r15
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x115c
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.halfCheckDrawLeft = r7
            r11 = 1085276160(0x40b00000, float:5.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r7 = r7 - r11
            r1.checkDrawLeft = r7
            goto L_0x119c
        L_0x115c:
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
            goto L_0x119c
        L_0x1183:
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 != 0) goto L_0x118d
            int r7 = r1.timeLeft
            int r7 = r7 - r11
            r1.checkDrawLeft1 = r7
            goto L_0x119c
        L_0x118d:
            int r15 = r1.timeLeft
            int r15 = r15 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r15 = r15 + r7
            r1.checkDrawLeft1 = r15
            int r7 = r1.nameLeft
            int r7 = r7 + r11
            r1.nameLeft = r7
        L_0x119c:
            boolean r7 = r1.dialogMuted
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r7 == 0) goto L_0x11c0
            boolean r7 = r1.drawVerified
            if (r7 != 0) goto L_0x11c0
            boolean r7 = r1.drawScam
            if (r7 != 0) goto L_0x11c0
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11f3
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
            goto L_0x11f3
        L_0x11c0:
            boolean r7 = r1.drawVerified
            if (r7 == 0) goto L_0x11da
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11f3
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
            goto L_0x11f3
        L_0x11da:
            boolean r7 = r1.drawScam
            if (r7 == 0) goto L_0x11f3
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.ScamDrawable r12 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r12 = r12.getIntrinsicWidth()
            int r7 = r7 + r12
            int r0 = r0 - r7
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x11f3
            int r12 = r1.nameLeft
            int r12 = r12 + r7
            r1.nameLeft = r12
        L_0x11f3:
            r7 = 1094713344(0x41400000, float:12.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r12 = java.lang.Math.max(r12, r0)
            r11 = 10
            r15 = 32
            java.lang.String r0 = r13.replace(r11, r15)     // Catch:{ Exception -> 0x124b }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x124b }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x124b }
            r11 = r11[r13]     // Catch:{ Exception -> 0x124b }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x124b }
            int r13 = r12 - r13
            float r13 = (float) r13     // Catch:{ Exception -> 0x124b }
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x124b }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r11, r13, r15)     // Catch:{ Exception -> 0x124b }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x124b }
            if (r11 == 0) goto L_0x122f
            boolean r11 = r11.hasHighlightedWords()     // Catch:{ Exception -> 0x124b }
            if (r11 == 0) goto L_0x122f
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x124b }
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords     // Catch:{ Exception -> 0x124b }
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r11)     // Catch:{ Exception -> 0x124b }
            if (r11 == 0) goto L_0x122f
            r26 = r11
            goto L_0x1231
        L_0x122f:
            r26 = r0
        L_0x1231:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x124b }
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x124b }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x124b }
            r27 = r11[r13]     // Catch:{ Exception -> 0x124b }
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x124b }
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 0
            r32 = 0
            r25 = r0
            r28 = r12
            r25.<init>(r26, r27, r28, r29, r30, r31, r32)     // Catch:{ Exception -> 0x124b }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x124b }
            goto L_0x124f
        L_0x124b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x124f:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1309
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1259
            goto L_0x1309
        L_0x1259:
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
            if (r13 == 0) goto L_0x12bb
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
            goto L_0x12d0
        L_0x12bb:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r1.messageNameLeft = r13
            r1.messageLeft = r13
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r15 = 1116078080(0x42860000, float:67.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r13
        L_0x12d0:
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
            goto L_0x13b5
        L_0x1309:
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
            if (r5 == 0) goto L_0x1370
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
            goto L_0x1385
        L_0x1370:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 1116340224(0x428a0000, float:69.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r5
        L_0x1385:
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
        L_0x13b5:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x13da
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x13d2
            int r0 = r45.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x13da
        L_0x13d2:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x13da:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x140e
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x13f8
            int r0 = r45.getMeasuredWidth()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.errorLeft = r0
            goto L_0x140a
        L_0x13f8:
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.errorLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x140a:
            r0 = r17
            goto L_0x1529
        L_0x140e:
            if (r2 != 0) goto L_0x143a
            if (r14 == 0) goto L_0x1413
            goto L_0x143a
        L_0x1413:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1434
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x1434
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x1434:
            r2 = 0
            r1.drawCount = r2
            r1.drawMention = r2
            goto L_0x140a
        L_0x143a:
            if (r2 == 0) goto L_0x149d
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
            if (r2 != 0) goto L_0x1489
            int r0 = r45.getMeasuredWidth()
            int r2 = r1.countWidth
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r2
            r1.countLeft = r0
            goto L_0x1499
        L_0x1489:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.countLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x1499:
            r2 = 1
            r1.drawCount = r2
            goto L_0x14a0
        L_0x149d:
            r2 = 0
            r1.countWidth = r2
        L_0x14a0:
            if (r14 == 0) goto L_0x140a
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x14d8
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
            goto L_0x14e0
        L_0x14d8:
            r2 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.mentionWidth = r0
        L_0x14e0:
            int r0 = r1.mentionWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r2
            int r17 = r17 - r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x1508
            int r0 = r45.getMeasuredWidth()
            int r2 = r1.mentionWidth
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 - r2
            int r2 = r1.countWidth
            if (r2 == 0) goto L_0x1503
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 + r5
            goto L_0x1504
        L_0x1503:
            r2 = 0
        L_0x1504:
            int r0 = r0 - r2
            r1.mentionLeft = r0
            goto L_0x1524
        L_0x1508:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r5 = r1.countWidth
            if (r5 == 0) goto L_0x1516
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r5 = r5 + r6
            goto L_0x1517
        L_0x1516:
            r5 = 0
        L_0x1517:
            int r2 = r2 + r5
            r1.mentionLeft = r2
            int r2 = r1.messageLeft
            int r2 = r2 + r0
            r1.messageLeft = r2
            int r2 = r1.messageNameLeft
            int r2 = r2 + r0
            r1.messageNameLeft = r2
        L_0x1524:
            r2 = 1
            r1.drawMention = r2
            goto L_0x140a
        L_0x1529:
            if (r10 == 0) goto L_0x157d
            if (r3 != 0) goto L_0x152f
            r3 = r34
        L_0x152f:
            java.lang.String r2 = r3.toString()
            int r3 = r2.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r3 <= r5) goto L_0x1540
            r3 = 0
            java.lang.String r2 = r2.substring(r3, r5)
        L_0x1540:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x1548
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x154a
        L_0x1548:
            if (r8 == 0) goto L_0x1553
        L_0x154a:
            r3 = 32
            r5 = 10
            java.lang.String r2 = r2.replace(r5, r3)
            goto L_0x155b
        L_0x1553:
            java.lang.String r3 = "\n\n"
            java.lang.String r5 = "\n"
            java.lang.String r2 = r2.replace(r3, r5)
        L_0x155b:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r5 = r1.paintIndex
            r3 = r3[r5]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r5 = 1099431936(0x41880000, float:17.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r2, r3, r5, r6)
            org.telegram.messenger.MessageObject r2 = r1.message
            if (r2 == 0) goto L_0x157d
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r3, (java.util.ArrayList<java.lang.String>) r2)
            if (r2 == 0) goto L_0x157d
            r3 = r2
        L_0x157d:
            r2 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = java.lang.Math.max(r5, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x158f
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x15e5
        L_0x158f:
            if (r8 == 0) goto L_0x15e5
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x159a
            int r5 = r1.currentDialogFolderDialogsCount
            r6 = 1
            if (r5 != r6) goto L_0x15e5
        L_0x159a:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15ca }
            if (r0 == 0) goto L_0x15af
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x15ca }
            if (r0 == 0) goto L_0x15af
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x15ca }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x15ca }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r8, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x15ca }
            if (r0 == 0) goto L_0x15af
            r8 = r0
        L_0x15af:
            android.text.TextPaint r35 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x15ca }
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x15ca }
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            android.text.TextUtils$TruncateAt r41 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x15ca }
            r43 = 1
            r34 = r8
            r36 = r2
            r42 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r34, r35, r36, r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x15ca }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x15ca }
            goto L_0x15ce
        L_0x15ca:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x15ce:
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
            goto L_0x160d
        L_0x15e5:
            r5 = 0
            r1.messageNameLayout = r5
            if (r0 != 0) goto L_0x15f8
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x15ef
            goto L_0x15f8
        L_0x15ef:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x160d
        L_0x15f8:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r6 = 1101529088(0x41a80000, float:21.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            float r4 = (float) r4
            r0.setImageY(r4)
        L_0x160d:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16b2 }
            if (r0 != 0) goto L_0x1615
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16b2 }
            if (r4 == 0) goto L_0x162a
        L_0x1615:
            int r4 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x16b2 }
            if (r4 == 0) goto L_0x162a
            int r4 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x16b2 }
            r6 = 1
            if (r4 <= r6) goto L_0x162a
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x16b2 }
            int r3 = r1.paintIndex     // Catch:{ Exception -> 0x16b2 }
            r9 = r0[r3]     // Catch:{ Exception -> 0x16b2 }
            r34 = r8
            r35 = r9
            r8 = r5
            goto L_0x1645
        L_0x162a:
            if (r0 != 0) goto L_0x1630
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16b2 }
            if (r0 == 0) goto L_0x1632
        L_0x1630:
            if (r8 == 0) goto L_0x1641
        L_0x1632:
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x16b2 }
            int r0 = r2 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x16b2 }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16b2 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r3, r9, r0, r4)     // Catch:{ Exception -> 0x16b2 }
        L_0x1641:
            r34 = r3
            r35 = r9
        L_0x1645:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x16b2 }
            if (r0 != 0) goto L_0x1682
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x16b2 }
            if (r0 == 0) goto L_0x164e
            goto L_0x1682
        L_0x164e:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16b2 }
            if (r0 == 0) goto L_0x166a
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16b2 }
            int r7 = r11 + r0
            int r2 = r2 + r7
            boolean r0 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x16b2 }
            if (r0 == 0) goto L_0x166a
            int r0 = r1.messageLeft     // Catch:{ Exception -> 0x16b2 }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16b2 }
            int r7 = r11 + r4
            int r0 = r0 - r7
            r1.messageLeft = r0     // Catch:{ Exception -> 0x16b2 }
        L_0x166a:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x16b2 }
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16b2 }
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r0
            r25 = r34
            r26 = r35
            r27 = r2
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)     // Catch:{ Exception -> 0x16b2 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16b2 }
            goto L_0x16b6
        L_0x1682:
            boolean r0 = r1.hasMessageThumb     // Catch:{ Exception -> 0x16b2 }
            if (r0 == 0) goto L_0x168f
            if (r8 == 0) goto L_0x168f
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x16b2 }
            int r2 = r2 + r0
        L_0x168f:
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x16b2 }
            r38 = 1065353216(0x3var_, float:1.0)
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x16b2 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x16b2 }
            r40 = 0
            android.text.TextUtils$TruncateAt r41 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x16b2 }
            if (r8 == 0) goto L_0x16a3
            r43 = 1
            goto L_0x16a5
        L_0x16a3:
            r43 = 2
        L_0x16a5:
            r36 = r2
            r39 = r0
            r42 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r34, r35, r36, r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x16b2 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x16b2 }
            goto L_0x16b6
        L_0x16b2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x16b6:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x17ea
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1773
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1773
            android.text.StaticLayout r0 = r1.nameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            boolean r5 = r1.dialogMuted
            if (r5 == 0) goto L_0x1708
            boolean r5 = r1.drawVerified
            if (r5 != 0) goto L_0x1708
            boolean r5 = r1.drawScam
            if (r5 != 0) goto L_0x1708
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
            goto L_0x175b
        L_0x1708:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x1732
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
            goto L_0x175b
        L_0x1732:
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x175b
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
        L_0x175b:
            r5 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x1773
            double r5 = (double) r12
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1773
            int r0 = r1.nameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.nameLeft = r0
        L_0x1773:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x17b4
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x17b4
            r3 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x1784:
            if (r3 >= r0) goto L_0x17aa
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineLeft(r3)
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 != 0) goto L_0x17a9
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
            goto L_0x1784
        L_0x17a9:
            r4 = 0
        L_0x17aa:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r4 == r0) goto L_0x17b4
            int r0 = r1.messageLeft
            int r0 = r0 + r4
            r1.messageLeft = r0
        L_0x17b4:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1874
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1874
            android.text.StaticLayout r0 = r1.messageNameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x1874
            android.text.StaticLayout r0 = r1.messageNameLayout
            float r0 = r0.getLineWidth(r3)
            double r3 = (double) r0
            double r3 = java.lang.Math.ceil(r3)
            double r5 = (double) r2
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x1874
            int r0 = r1.messageNameLeft
            double r7 = (double) r0
            java.lang.Double.isNaN(r5)
            double r5 = r5 - r3
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r5
            int r0 = (int) r7
            r1.messageNameLeft = r0
            goto L_0x1874
        L_0x17ea:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1839
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1839
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r12
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x181e
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r12
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x181e
            int r6 = r1.nameLeft
            double r6 = (double) r6
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            int r2 = (int) r6
            r1.nameLeft = r2
        L_0x181e:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x182a
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x182a
            boolean r2 = r1.drawScam
            if (r2 == 0) goto L_0x1839
        L_0x182a:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r2 = r2 + r0
            int r0 = (int) r2
            r1.nameMuteLeft = r0
        L_0x1839:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x185c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x185c
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r7 = 0
        L_0x1846:
            if (r7 >= r0) goto L_0x1855
            android.text.StaticLayout r3 = r1.messageLayout
            float r3 = r3.getLineLeft(r7)
            float r2 = java.lang.Math.min(r2, r3)
            int r7 = r7 + 1
            goto L_0x1846
        L_0x1855:
            int r0 = r1.messageLeft
            float r0 = (float) r0
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageLeft = r0
        L_0x185c:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1874
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1874
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1874:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x18b8
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x18b8
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x18b4 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x18b4 }
            r6 = r23
            r2 = 1
            if (r6 < r0) goto L_0x188b
            int r6 = r0 + -1
        L_0x188b:
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x18b4 }
            float r0 = r0.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18b4 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x18b4 }
            int r6 = r6 + r2
            float r2 = r3.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x18b4 }
            float r0 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x18b4 }
            double r2 = (double) r0     // Catch:{ Exception -> 0x18b4 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x18b4 }
            int r0 = (int) r2     // Catch:{ Exception -> 0x18b4 }
            if (r0 == 0) goto L_0x18ab
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x18b4 }
            int r0 = r0 + r2
        L_0x18ab:
            org.telegram.messenger.ImageReceiver r2 = r1.thumbImage     // Catch:{ Exception -> 0x18b4 }
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x18b4 }
            int r3 = r3 + r0
            r2.setImageX(r3)     // Catch:{ Exception -> 0x18b4 }
            goto L_0x18b8
        L_0x18b4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x18b8:
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
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0498  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x04d8  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0568  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x05b9  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0746  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0810  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x085f  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0bb3  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0be6  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0c1e  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0c3b  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0d25  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0fa1  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0fb5  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0fc8  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0fe2  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0ffa  */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x1063  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x10ba  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x10cd  */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x10e5  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x110f  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x113e  */
    /* JADX WARNING: Removed duplicated region for block: B:674:0x1153  */
    /* JADX WARNING: Removed duplicated region for block: B:682:0x116e  */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x117c  */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x119f  */
    /* JADX WARNING: Removed duplicated region for block: B:698:? A[RETURN, SYNTHETIC] */
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
            r2 = 2131627505(0x7f0e0df1, float:1.8882276E38)
            java.lang.String r3 = "UnhideFromTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x00bd:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2 = 2131625626(0x7f0e069a, float:1.8878465E38)
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
            r2 = 2131626816(0x7f0e0b40, float:1.8880879E38)
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
            r2 = 2131624268(0x7f0e014c, float:1.887571E38)
            java.lang.String r3 = "Archive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r3
            goto L_0x00ec
        L_0x0109:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r2 = 2131627496(0x7f0e0de8, float:1.8882258E38)
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
            if (r0 == 0) goto L_0x0689
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
            if (r0 < 0) goto L_0x0689
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0689
            r29.save()
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 != 0) goto L_0x0632
            android.text.StaticLayout r1 = r8.messageLayout
            boolean r1 = r1.isRtlCharAt(r15)
            if (r1 == 0) goto L_0x062f
            goto L_0x0632
        L_0x062f:
            int r1 = r8.messageLeft
            goto L_0x0640
        L_0x0632:
            int r1 = r8.messageLeft
            android.text.StaticLayout r2 = r8.messageLayout
            int r2 = r2.getWidth()
            int r1 = r1 + r2
            int r2 = r0.getIntrinsicWidth()
            int r1 = r1 - r2
        L_0x0640:
            int r2 = r8.printingStringType
            r3 = 1
            if (r2 == r3) goto L_0x0660
            if (r2 != r7) goto L_0x0648
            goto L_0x0660
        L_0x0648:
            float r2 = (float) r1
            int r3 = r8.messageTop
            float r3 = (float) r3
            r4 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0.getIntrinsicHeight()
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 / r23
            float r3 = r3 + r4
            r9.translate(r2, r3)
            r6 = 1
            goto L_0x0671
        L_0x0660:
            float r3 = (float) r1
            int r4 = r8.messageTop
            r6 = 1
            if (r2 != r6) goto L_0x066b
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x066c
        L_0x066b:
            r2 = 0
        L_0x066c:
            int r4 = r4 + r2
            float r2 = (float) r4
            r9.translate(r3, r2)
        L_0x0671:
            r0.draw(r9)
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r4 = r8.messageTop
            int r0 = r0.getIntrinsicHeight()
            int r4 = r4 + r0
            r8.invalidate(r1, r2, r3, r4)
            r29.restore()
            goto L_0x068a
        L_0x0689:
            r6 = 1
        L_0x068a:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0746
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0696
            r1 = 2
            goto L_0x0697
        L_0x0696:
            r1 = 0
        L_0x0697:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x069e
            r1 = 4
            goto L_0x069f
        L_0x069e:
            r1 = 0
        L_0x069f:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x06ad
            if (r1 == r0) goto L_0x06ad
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x06ad
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x06ad:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x06b3
            int r0 = r8.animateToStatusDrawableParams
        L_0x06b3:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x06ba
            r21 = 1
            goto L_0x06bc
        L_0x06ba:
            r21 = 0
        L_0x06bc:
            r2 = r0 & 2
            if (r2 == 0) goto L_0x06c3
            r24 = 1
            goto L_0x06c5
        L_0x06c3:
            r24 = 0
        L_0x06c5:
            r0 = r0 & r7
            if (r0 == 0) goto L_0x06ca
            r0 = 1
            goto L_0x06cb
        L_0x06ca:
            r0 = 0
        L_0x06cb:
            if (r1 == 0) goto L_0x0721
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x06d5
            r3 = 1
            goto L_0x06d6
        L_0x06d5:
            r3 = 0
        L_0x06d6:
            r2 = r1 & 2
            if (r2 == 0) goto L_0x06dc
            r4 = 1
            goto L_0x06dd
        L_0x06dc:
            r4 = 0
        L_0x06dd:
            r1 = r1 & r7
            if (r1 == 0) goto L_0x06e2
            r5 = 1
            goto L_0x06e3
        L_0x06e2:
            r5 = 0
        L_0x06e3:
            if (r21 != 0) goto L_0x0709
            if (r3 != 0) goto L_0x0709
            if (r5 == 0) goto L_0x0709
            if (r4 != 0) goto L_0x0709
            if (r24 == 0) goto L_0x0709
            if (r0 == 0) goto L_0x0709
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
            goto L_0x0731
        L_0x0709:
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
            goto L_0x0731
        L_0x0721:
            r15 = 1
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r28
            r2 = r29
            r3 = r21
            r4 = r24
            r5 = r0
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0731:
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0739
            r1 = 2
            goto L_0x073a
        L_0x0739:
            r1 = 0
        L_0x073a:
            int r0 = r0 + r1
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x0741
            r7 = 4
            goto L_0x0742
        L_0x0741:
            r7 = 0
        L_0x0742:
            int r0 = r0 + r7
            r8.lastStatusDrawableParams = r0
            goto L_0x0747
        L_0x0746:
            r15 = 1
        L_0x0747:
            boolean r0 = r8.dialogMuted
            if (r0 == 0) goto L_0x077f
            boolean r0 = r8.drawVerified
            if (r0 != 0) goto L_0x077f
            boolean r0 = r8.drawScam
            if (r0 != 0) goto L_0x077f
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0763
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0760
            goto L_0x0763
        L_0x0760:
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0764
        L_0x0763:
            r6 = 0
        L_0x0764:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 - r2
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0770
            r2 = 1096286208(0x41580000, float:13.5)
            goto L_0x0772
        L_0x0770:
            r2 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x0772:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x07e2
        L_0x077f:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x07c0
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0793
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0790
            goto L_0x0793
        L_0x0790:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x0795
        L_0x0793:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x0795:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07ac
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x07a9
            goto L_0x07ac
        L_0x07a9:
            r2 = 1099169792(0x41840000, float:16.5)
            goto L_0x07ae
        L_0x07ac:
            r2 = 1095237632(0x41480000, float:12.5)
        L_0x07ae:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x07e2
        L_0x07c0:
            boolean r0 = r8.drawScam
            if (r0 == 0) goto L_0x07e2
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            int r1 = r8.nameMuteLeft
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x07d4
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x07d1
            goto L_0x07d4
        L_0x07d1:
            r2 = 1097859072(0x41700000, float:15.0)
            goto L_0x07d6
        L_0x07d4:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x07d6:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Cells.BaseCell.setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r0.draw(r9)
        L_0x07e2:
            boolean r0 = r8.drawReorder
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x07ee
            float r0 = r8.reorderIconProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0806
        L_0x07ee:
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
        L_0x0806:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x085f
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
            goto L_0x0bad
        L_0x085f:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x088e
            boolean r6 = r8.drawMention
            if (r6 != 0) goto L_0x088e
            float r6 = r8.countChangeProgress
            int r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r6 == 0) goto L_0x086e
            goto L_0x088e
        L_0x086e:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0bad
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
            goto L_0x0bad
        L_0x088e:
            if (r0 != 0) goto L_0x0896
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0af9
        L_0x0896:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x08a3
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x08a3
            float r6 = r8.countChangeProgress
            float r6 = r13 - r6
            goto L_0x08a5
        L_0x08a3:
            float r6 = r8.countChangeProgress
        L_0x08a5:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0a23
            if (r0 != 0) goto L_0x08ad
            goto L_0x0a23
        L_0x08ad:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x08b9
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x08b6
            goto L_0x08b9
        L_0x08b6:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x08bb
        L_0x08b9:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x08bb:
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
            if (r15 <= 0) goto L_0x08da
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x08db
        L_0x08da:
            r15 = r7
        L_0x08db:
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
            if (r1 > 0) goto L_0x0926
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r3 = r3.getInterpolation(r7)
            goto L_0x0936
        L_0x0926:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r5 = 1056964608(0x3var_, float:0.5)
            float r6 = r6 - r5
            float r6 = r6 * r23
            float r6 = r13 - r6
            float r3 = r3.getInterpolation(r6)
        L_0x0936:
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
            if (r0 == 0) goto L_0x0974
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
        L_0x0974:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r0 = r0.getAlpha()
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r0
            float r5 = r3 * r15
            int r5 = (int) r5
            r1.setAlpha(r5)
            android.text.StaticLayout r1 = r8.countAnimationInLayout
            if (r1 == 0) goto L_0x09b3
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x0993
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0998
        L_0x0993:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x0998:
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
            goto L_0x09e2
        L_0x09b3:
            android.text.StaticLayout r1 = r8.countLayout
            if (r1 == 0) goto L_0x09e2
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x09c3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x09c8
        L_0x09c3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
        L_0x09c8:
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
        L_0x09e2:
            android.text.StaticLayout r1 = r8.countOldLayout
            if (r1 == 0) goto L_0x0a19
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r3 * r26
            int r3 = (int) r3
            r1.setAlpha(r3)
            r29.save()
            boolean r1 = r8.countAnimationIncrement
            if (r1 == 0) goto L_0x09fb
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r1 = -r1
            goto L_0x09ff
        L_0x09fb:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x09ff:
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
        L_0x0a19:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r1.setAlpha(r0)
            r29.restore()
            goto L_0x0af9
        L_0x0a23:
            if (r0 != 0) goto L_0x0a26
            goto L_0x0a28
        L_0x0a26:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0a28:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0a34
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0a31
            goto L_0x0a34
        L_0x0a31:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0a36
        L_0x0a34:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0a36:
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
            if (r1 == 0) goto L_0x0aca
            boolean r1 = r8.drawPin
            if (r1 == 0) goto L_0x0ab8
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
        L_0x0ab8:
            r29.save()
            android.graphics.RectF r1 = r8.rect
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r6, r6, r1, r3)
        L_0x0aca:
            android.graphics.RectF r1 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r5 = r3 * r4
            float r3 = r3 * r4
            r9.drawRoundRect(r1, r5, r3, r0)
            if (r7 == 0) goto L_0x0af2
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
        L_0x0af2:
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0af9
            r29.restore()
        L_0x0af9:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0bad
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
            if (r0 == 0) goto L_0x0b3b
            int r0 = r8.folderId
            if (r0 == 0) goto L_0x0b3b
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0b3d
        L_0x0b3b:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0b3d:
            android.graphics.RectF r1 = r8.rect
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1094189056(0x41380000, float:11.5)
            float r4 = r2 * r3
            float r2 = r2 * r3
            r9.drawRoundRect(r1, r4, r2, r0)
            android.text.StaticLayout r0 = r8.mentionLayout
            if (r0 == 0) goto L_0x0b78
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
            goto L_0x0bad
        L_0x0b78:
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
        L_0x0bad:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0bcf
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
        L_0x0bcf:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0bdd
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0bdd
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0be2
        L_0x0bdd:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0be2:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0c1a
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0c1a
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
        L_0x0c1a:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0CLASSNAME
            r29.restore()
        L_0x0CLASSNAME:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x0d1e
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0d1e
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0d21
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0d21
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r2 = r0.bot
            if (r2 != 0) goto L_0x0d21
            boolean r2 = r0.self
            if (r2 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r0.expires
            int r2 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r2 = r8.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = 1
            goto L_0x0c6a
        L_0x0CLASSNAME:
            r0 = 0
        L_0x0c6a:
            if (r0 != 0) goto L_0x0CLASSNAME
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0d1e
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
            if (r3 == 0) goto L_0x0ca6
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0c9f
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0c9d
            goto L_0x0c9f
        L_0x0c9d:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0c9f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r3 = r3 + r1
            goto L_0x0cbd
        L_0x0ca6:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0cb7
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0cb5
            goto L_0x0cb7
        L_0x0cb5:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0cb7:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r3 = r3 - r1
        L_0x0cbd:
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
            if (r0 == 0) goto L_0x0d09
            float r0 = r8.onlineProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x0d1e
            float r1 = (float) r11
            float r1 = r1 / r18
            float r0 = r0 + r1
            r8.onlineProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0d1c
            r8.onlineProgress = r13
            goto L_0x0d1c
        L_0x0d09:
            float r0 = r8.onlineProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d1e
            float r2 = (float) r11
            float r2 = r2 / r18
            float r0 = r0 - r2
            r8.onlineProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0d1c
            r8.onlineProgress = r1
        L_0x0d1c:
            r19 = 1
        L_0x0d1e:
            r2 = 0
            goto L_0x0fdc
        L_0x0d21:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0d1e
            boolean r2 = r0.call_active
            if (r2 == 0) goto L_0x0d2f
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0d2f
            r0 = 1
            goto L_0x0d30
        L_0x0d2f:
            r0 = 0
        L_0x0d30:
            r8.hasCall = r0
            if (r0 != 0) goto L_0x0d3b
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0d1e
        L_0x0d3b:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0d4c
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r6 = r13 - r0
            goto L_0x0d4e
        L_0x0d4c:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0d4e:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            float r0 = r0.getImageY2()
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0d5c
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x0d5e
        L_0x0d5c:
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d5e:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0d81
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0d7a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0d78
            goto L_0x0d7a
        L_0x0d78:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d7a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r2 = r2 + r1
            goto L_0x0d98
        L_0x0d81:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageX2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0d92
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0d90
            goto L_0x0d92
        L_0x0d90:
            r20 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0d92:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r1 = (float) r1
            float r2 = r2 - r1
        L_0x0d98:
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
            if (r3 != 0) goto L_0x0e04
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
        L_0x0dff:
            float r10 = r10 * r14
            float r5 = r5 - r10
            goto L_0x0efd
        L_0x0e04:
            r5 = 1
            if (r3 != r5) goto L_0x0e2b
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
        L_0x0e26:
            float r5 = r5 * r14
            float r5 = r5 + r10
            goto L_0x0efd
        L_0x0e2b:
            r5 = 2
            if (r3 != r5) goto L_0x0e4e
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
            goto L_0x0dff
        L_0x0e4e:
            r5 = 3
            if (r3 != r5) goto L_0x0e71
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
        L_0x0e6c:
            float r10 = r10 * r14
            float r5 = r5 + r10
            goto L_0x0efd
        L_0x0e71:
            r5 = 4
            if (r3 != r5) goto L_0x0e93
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
            goto L_0x0dff
        L_0x0e93:
            r5 = 5
            if (r3 != r5) goto L_0x0eb7
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
            goto L_0x0e26
        L_0x0eb7:
            r5 = 1082130432(0x40800000, float:4.0)
            r10 = 6
            if (r3 != r10) goto L_0x0ede
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
            goto L_0x0efd
        L_0x0ede:
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
            goto L_0x0e6c
        L_0x0efd:
            float r10 = r8.chatCallProgress
            int r10 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r10 < 0) goto L_0x0var_
            int r10 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r10 >= 0) goto L_0x0var_
        L_0x0var_:
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
            if (r0 < 0) goto L_0x0fb1
            r2 = 0
            r8.innerProgress = r2
            int r0 = r8.progressStage
            r2 = 1
            int r0 = r0 + r2
            r8.progressStage = r0
            r2 = 8
            if (r0 < r2) goto L_0x0fb1
            r2 = 0
            r8.progressStage = r2
        L_0x0fb1:
            boolean r0 = r8.hasCall
            if (r0 == 0) goto L_0x0fc8
            float r0 = r8.chatCallProgress
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x0fc6
            float r1 = r1 / r18
            float r0 = r0 + r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0fc6
            r8.chatCallProgress = r13
        L_0x0fc6:
            r2 = 0
            goto L_0x0fda
        L_0x0fc8:
            float r0 = r8.chatCallProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L_0x0fda
            float r1 = r1 / r18
            float r0 = r0 - r1
            r8.chatCallProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0fda
            r8.chatCallProgress = r2
        L_0x0fda:
            r19 = 1
        L_0x0fdc:
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0fe5
            r29.restore()
        L_0x0fe5:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0ff6
            float r0 = r8.translationX
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0ff6
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0ff6
            r0.draw(r9)
        L_0x0ff6:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x105b
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x101a
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x100a
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x100a
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x101a
        L_0x100a:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1013
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x1013
            goto L_0x101a
        L_0x1013:
            r0 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x101b
        L_0x101a:
            r2 = 0
        L_0x101b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1040
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
            goto L_0x105b
        L_0x1040:
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
            goto L_0x105c
        L_0x105b:
            r10 = 1
        L_0x105c:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x10aa
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x106d
            r29.restore()
            goto L_0x10aa
        L_0x106d:
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
        L_0x10aa:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x10b8
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x10b6
            goto L_0x10b8
        L_0x10b6:
            r1 = 0
            goto L_0x10e1
        L_0x10b8:
            if (r0 == 0) goto L_0x10cd
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x10b6
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x10cb
            r8.reorderIconProgress = r13
        L_0x10cb:
            r1 = 0
            goto L_0x10df
        L_0x10cd:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x10e1
            float r2 = (float) r11
            float r2 = r2 / r7
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x10df
            r8.reorderIconProgress = r1
        L_0x10df:
            r19 = 1
        L_0x10e1:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x110f
            float r0 = r8.archiveBackgroundProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x113a
            float r2 = (float) r11
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x10f8
            r8.archiveBackgroundProgress = r1
        L_0x10f8:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1138
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
            goto L_0x1138
        L_0x110f:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x113a
            float r1 = (float) r11
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1122
            r8.archiveBackgroundProgress = r13
        L_0x1122:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1138
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1138:
            r19 = 1
        L_0x113a:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x114f
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r11
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x114d
            r8.animatingArchiveAvatarProgress = r7
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x114d:
            r19 = 1
        L_0x114f:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x117c
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x1166
            float r1 = (float) r11
            float r1 = r1 / r7
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x1166
            r8.currentRevealBounceProgress = r13
            r7 = 1
            goto L_0x1168
        L_0x1166:
            r7 = r19
        L_0x1168:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x119d
            float r1 = (float) r11
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x119c
            r8.currentRevealProgress = r13
            goto L_0x119c
        L_0x117c:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            r1 = 0
            if (r0 != 0) goto L_0x1187
            r8.currentRevealBounceProgress = r1
            r7 = 1
            goto L_0x1189
        L_0x1187:
            r7 = r19
        L_0x1189:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x119d
            float r2 = (float) r11
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x119c
            r8.currentRevealProgress = r1
        L_0x119c:
            r7 = 1
        L_0x119d:
            if (r7 == 0) goto L_0x11a2
            r28.invalidate()
        L_0x11a2:
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
