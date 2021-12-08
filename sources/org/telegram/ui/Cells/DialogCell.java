package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyStubSpan;
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
    private boolean attachedToWindow;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int bottomClip;
    private TLRPC.Chat chat;
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
    private float dialogMutedProgress;
    private int dialogsType;
    private TLRPC.DraftMessage draftMessage;
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
    private int drawScam;
    private boolean drawVerified;
    private TLRPC.EncryptedChat encryptedChat;
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
    private int lastDrawSwipeMessageStringId;
    private RLottieDrawable lastDrawTranslationDrawable;
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
    boolean moving;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private float onlineProgress;
    private int paintIndex;
    private DialogsActivity parentFragment;
    private int pinLeft;
    private int pinTop;
    private DialogsAdapter.DialogsPreloader preloader;
    private int printingStringType;
    private int progressStage;
    private boolean promoDialog;
    private RectF rect;
    private float reorderIconProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private int statusDrawableLeft;
    private float statusDrawableProgress;
    public boolean swipeCanceled;
    private int swipeMessageTextId;
    private StaticLayout swipeMessageTextLayout;
    private int swipeMessageWidth;
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

    public void setMoving(boolean moving2) {
        this.moving = moving2;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public static class FixedWidthSpan extends ReplacementSpan {
        private int width;

        public FixedWidthSpan(int w) {
            this.width = w;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = paint.getFontMetricsInt();
            }
            if (fm != null) {
                int i = 1 - (fm.descent - fm.ascent);
                fm.descent = i;
                fm.bottom = i;
                fm.ascent = -1;
                fm.top = -1;
            }
            return this.width;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        }
    }

    public static class BounceInterpolator implements Interpolator {
        public float getInterpolation(float t) {
            if (t < 0.33f) {
                return (t / 0.33f) * 0.1f;
            }
            float t2 = t - 0.33f;
            if (t2 < 0.33f) {
                return 0.1f - ((t2 / 0.34f) * 0.15f);
            }
            return (((t2 - 0.34f) / 0.33f) * 0.05f) - 89.6f;
        }
    }

    public DialogCell(DialogsActivity fragment, Context context, boolean needCheck, boolean forceThreeLines) {
        this(fragment, context, needCheck, forceThreeLines, UserConfig.selectedAccount, (Theme.ResourcesProvider) null);
    }

    public DialogCell(DialogsActivity fragment, Context context, boolean needCheck, boolean forceThreeLines, int account, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.thumbImage = new ImageReceiver(this);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.countChangeProgress = 1.0f;
        this.rect = new RectF();
        this.lastStatusDrawableParams = -1;
        this.resourcesProvider = resourcesProvider2;
        this.parentFragment = fragment;
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.thumbImage.setRoundRadius(AndroidUtilities.dp(2.0f));
        this.useForceThreeLines = forceThreeLines;
        this.currentAccount = account;
        if (needCheck) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox);
        }
    }

    public void setDialog(TLRPC.Dialog dialog, int type, int folder) {
        if (this.currentDialogId != dialog.id) {
            ValueAnimator valueAnimator = this.statusDrawableAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.statusDrawableAnimator.cancel();
            }
            this.statusDrawableAnimationInProgress = false;
            this.lastStatusDrawableParams = -1;
        }
        this.currentDialogId = dialog.id;
        this.lastDialogChangedTime = System.currentTimeMillis();
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
        this.dialogsType = type;
        this.folderId = folder;
        this.messageId = 0;
        update(0, false);
        checkOnline();
        checkGroupCall();
        checkChatTheme();
    }

    public void setDialogIndex(int i) {
        this.index = i;
    }

    public void setDialog(CustomDialog dialog) {
        this.customDialog = dialog;
        this.messageId = 0;
        update(0);
        checkOnline();
        checkGroupCall();
        checkChatTheme();
    }

    private void checkOnline() {
        TLRPC.User newUser;
        if (!(this.user == null || (newUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.user.id))) == null)) {
            this.user = newUser;
        }
        this.onlineProgress = isOnline() ? 1.0f : 0.0f;
    }

    private boolean isOnline() {
        TLRPC.User user2 = this.user;
        if (user2 == null || user2.self) {
            return false;
        }
        if (this.user.status != null && this.user.status.expires <= 0 && MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(this.user.id))) {
            return true;
        }
        if (this.user.status == null || this.user.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
            return false;
        }
        return true;
    }

    private void checkGroupCall() {
        TLRPC.Chat chat2 = this.chat;
        boolean z = chat2 != null && chat2.call_active && this.chat.call_not_empty;
        this.hasCall = z;
        this.chatCallProgress = z ? 1.0f : 0.0f;
    }

    private void checkChatTheme() {
        MessageObject messageObject = this.message;
        if (messageObject != null && messageObject.messageOwner != null && (this.message.messageOwner.action instanceof TLRPC.TL_messageActionSetChatTheme) && this.lastUnreadState) {
            ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.currentDialogId, ((TLRPC.TL_messageActionSetChatTheme) this.message.messageOwner.action).emoticon, false);
        }
    }

    public void setDialog(long dialog_id, MessageObject messageObject, int date, boolean useMe) {
        if (this.currentDialogId != dialog_id) {
            this.lastStatusDrawableParams = -1;
        }
        this.currentDialogId = dialog_id;
        this.lastDialogChangedTime = System.currentTimeMillis();
        this.message = messageObject;
        this.useMeForMyMessages = useMe;
        this.isDialogCell = false;
        this.lastMessageDate = date;
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

    public void setPreloader(DialogsAdapter.DialogsPreloader preloader2) {
        this.preloader = preloader2;
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
        this.attachedToWindow = true;
        this.cornerProgress = 0.0f;
        setTranslationX(0.0f);
        setTranslationY(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 78.0f : 72.0f) + (this.useSeparator ? 1 : 0));
        this.topClip = 0;
        this.bottomClip = getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int x;
        if (this.currentDialogId != 0 || this.customDialog != null) {
            if (this.checkBox != null) {
                float f = 45.0f;
                if (LocaleController.isRTL) {
                    int i = right - left;
                    if (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) {
                        f = 43.0f;
                    }
                    x = i - AndroidUtilities.dp(f);
                } else {
                    if (this.useForceThreeLines || SharedConfig.useThreeLinesLayout) {
                        f = 43.0f;
                    }
                    x = AndroidUtilities.dp(f);
                }
                int y = AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 48.0f : 42.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(x, y, checkBox2.getMeasuredWidth() + x, this.checkBox.getMeasuredHeight() + y);
            }
            if (changed) {
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
        String title;
        ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            TLRPC.User currentUser = null;
            TLRPC.Chat currentChat = null;
            if (DialogObject.isEncryptedDialog(dialog.id)) {
                TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialog.id)));
                if (encryptedChat2 != null) {
                    currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat2.user_id));
                }
            } else if (DialogObject.isUserDialog(dialog.id)) {
                currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialog.id));
            } else {
                currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            }
            if (currentChat != null) {
                title = currentChat.title.replace(10, ' ');
            } else if (currentUser == null) {
                continue;
            } else if (UserObject.isDeleted(currentUser)) {
                title = LocaleController.getString("HiddenName", NUM);
            } else {
                title = ContactsController.formatName(currentUser.first_name, currentUser.last_name).replace(10, ' ');
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            int boldStart = builder.length();
            int boldEnd = title.length() + boldStart;
            builder.append(title);
            if (dialog.unread_count > 0) {
                builder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("chats_nameArchived", this.resourcesProvider)), boldStart, boldEnd, 33);
            }
            if (builder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(builder, Theme.dialogs_messagePaint[this.paintIndex].getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v169, resolved type: android.text.SpannableStringBuilder} */
    /* JADX WARNING: type inference failed for: r4v403 */
    /* JADX WARNING: type inference failed for: r4v404 */
    /* JADX WARNING: type inference failed for: r4v409 */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x06ab, code lost:
        if (r1.chat.kicked == false) goto L_0x06b6;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1005:0x1a4f A[Catch:{ Exception -> 0x1a65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x1a52 A[Catch:{ Exception -> 0x1a65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1a70  */
    /* JADX WARNING: Removed duplicated region for block: B:1061:0x1bc2  */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1CLASSNAME A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1CLASSNAME A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1108:0x1c9f  */
    /* JADX WARNING: Removed duplicated region for block: B:1117:0x1cea  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04e6  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0558  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0574  */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0e5c  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0e61  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x1076  */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x1129  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x130f  */
    /* JADX WARNING: Removed duplicated region for block: B:753:0x1320  */
    /* JADX WARNING: Removed duplicated region for block: B:864:0x15dc  */
    /* JADX WARNING: Removed duplicated region for block: B:872:0x169d  */
    /* JADX WARNING: Removed duplicated region for block: B:875:0x16e8  */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x1708  */
    /* JADX WARNING: Removed duplicated region for block: B:880:0x1755  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x177a  */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x17ab  */
    /* JADX WARNING: Removed duplicated region for block: B:926:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:969:0x199e  */
    /* JADX WARNING: Removed duplicated region for block: B:970:0x19a7  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x19cd A[Catch:{ Exception -> 0x1a65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x19d7 A[Catch:{ Exception -> 0x1a65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:990:0x19f6 A[Catch:{ Exception -> 0x1a65 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r55 = this;
            r1 = r55
            boolean r0 = r1.useForceThreeLines
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 1098907648(0x41800000, float:16.0)
            r5 = 1
            r6 = 0
            if (r0 != 0) goto L_0x005e
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0013
            goto L_0x005e
        L_0x0013:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint
            r0 = r0[r6]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
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
            r0 = r0[r6]
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r7 = r7[r6]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_message"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            r7.linkColor = r8
            r0.setColor(r8)
            r1.paintIndex = r6
            r0 = 19
            r7 = r0
            goto L_0x00a8
        L_0x005e:
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
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r0 = r0[r5]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            r0.setTextSize(r7)
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r0 = r0[r5]
            android.text.TextPaint[] r7 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r7 = r7[r5]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_message_threeLines"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            r7.linkColor = r8
            r0.setColor(r8)
            r1.paintIndex = r5
            r0 = 18
            r7 = r0
        L_0x00a8:
            r1.currentDialogFolderDialogsCount = r6
            java.lang.String r8 = ""
            java.lang.String r9 = ""
            r10 = 0
            r11 = 0
            java.lang.String r12 = ""
            r0 = 0
            r13 = 0
            boolean r14 = r1.isDialogCell
            if (r14 == 0) goto L_0x00c4
            int r14 = r1.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            long r2 = r1.currentDialogId
            java.lang.CharSequence r13 = r14.getPrintingString(r2, r6, r5)
        L_0x00c4:
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r3 = r1.paintIndex
            r2 = r2[r3]
            r3 = 1
            r1.drawNameGroup = r6
            r1.drawNameBroadcast = r6
            r1.drawNameLock = r6
            r1.drawNameBot = r6
            r1.drawVerified = r6
            r1.drawScam = r6
            r1.drawPinBackground = r6
            r1.hasMessageThumb = r6
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r1.user
            boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r15)
            if (r15 != 0) goto L_0x00ea
            boolean r15 = r1.useMeForMyMessages
            if (r15 != 0) goto L_0x00ea
            r15 = 1
            goto L_0x00eb
        L_0x00ea:
            r15 = 0
        L_0x00eb:
            r18 = 1
            r4 = -1
            r1.printingStringType = r4
            r4 = -1
            int r6 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r6 < r5) goto L_0x010d
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x00ff
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0103
        L_0x00ff:
            int r6 = r1.currentDialogFolderId
            if (r6 == 0) goto L_0x0108
        L_0x0103:
            java.lang.String r6 = "%2$s: ⁨%1$s⁩"
            r22 = 1
            goto L_0x0122
        L_0x0108:
            java.lang.String r6 = "⁨%s⁩"
            r22 = 0
            goto L_0x0122
        L_0x010d:
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x0115
            boolean r6 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0119
        L_0x0115:
            int r6 = r1.currentDialogFolderId
            if (r6 == 0) goto L_0x011e
        L_0x0119:
            java.lang.String r6 = "%2$s: %1$s"
            r22 = 1
            goto L_0x0122
        L_0x011e:
            java.lang.String r6 = "%1$s"
            r22 = 0
        L_0x0122:
            org.telegram.messenger.MessageObject r5 = r1.message
            r24 = r3
            if (r5 == 0) goto L_0x012b
            java.lang.CharSequence r5 = r5.messageText
            goto L_0x012c
        L_0x012b:
            r5 = 0
        L_0x012c:
            r1.lastMessageString = r5
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            r25 = 1117782016(0x42a00000, float:80.0)
            r26 = 1118044160(0x42a40000, float:82.0)
            r29 = 1101004800(0x41a00000, float:20.0)
            r30 = 1102053376(0x41b00000, float:22.0)
            r31 = 1117257728(0x42980000, float:76.0)
            r32 = 1099956224(0x41900000, float:18.0)
            r33 = 1117519872(0x429CLASSNAME, float:78.0)
            r3 = 2
            if (r5 == 0) goto L_0x03bd
            int r5 = r5.type
            if (r5 != r3) goto L_0x01cd
            r5 = 1
            r1.drawNameLock = r5
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x018f
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0151
            goto L_0x018f
        L_0x0151:
            r5 = 1099169792(0x41840000, float:16.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockTop = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0173
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r25)
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r5 = r5 + r23
            r1.nameLeft = r5
            goto L_0x02a5
        L_0x0173:
            int r5 = r55.getMeasuredWidth()
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r5 = r5 - r23
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r5 = r5 - r23
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r5
            goto L_0x02a5
        L_0x018f:
            r5 = 1095237632(0x41480000, float:12.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockTop = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x01b1
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r26)
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r5 = r5 + r23
            r1.nameLeft = r5
            goto L_0x02a5
        L_0x01b1:
            int r5 = r55.getMeasuredWidth()
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r5 = r5 - r23
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r5 = r5 - r23
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r5
            goto L_0x02a5
        L_0x01cd:
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            boolean r5 = r5.verified
            r1.drawVerified = r5
            boolean r5 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r5 == 0) goto L_0x0279
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.type
            r3 = 1
            if (r5 != r3) goto L_0x0279
            r1.drawNameGroup = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0232
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x01e9
            goto L_0x0232
        L_0x01e9:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0211
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r25)
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x0206
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0208
        L_0x0206:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0208:
            int r5 = r5.getIntrinsicWidth()
            int r3 = r3 + r5
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x0211:
            int r3 = r55.getMeasuredWidth()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r3 = r3 - r5
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x0221
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0223
        L_0x0221:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0223:
            int r5 = r5.getIntrinsicWidth()
            int r3 = r3 - r5
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x0232:
            r3 = 1096286208(0x41580000, float:13.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0259
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x024f
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0251
        L_0x024f:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0251:
            int r5 = r5.getIntrinsicWidth()
            int r3 = r3 + r5
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x0259:
            int r3 = r55.getMeasuredWidth()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r5
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x0269
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x026b
        L_0x0269:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x026b:
            int r5 = r5.getIntrinsicWidth()
            int r3 = r3 - r5
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x0279:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0294
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0282
            goto L_0x0294
        L_0x0282:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x028d
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x028d:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x0294:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x029f
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLeft = r3
            goto L_0x02a5
        L_0x029f:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
        L_0x02a5:
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r5 = 1
            if (r3 != r5) goto L_0x035d
            r3 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r5 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r3 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            boolean r5 = r5.isMedia
            if (r5 == 0) goto L_0x02f5
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r23 = r3
            int r3 = r1.paintIndex
            r2 = r5[r3]
            r3 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
            r34 = r4
            r4 = 0
            r5[r4] = r3
            java.lang.String r3 = java.lang.String.format(r6, r5)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r3)
            org.telegram.ui.Components.ForegroundColorSpanThemable r5 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            r24 = r2
            java.lang.String r2 = "chats_attachMessage"
            r5.<init>(r2, r4)
            int r2 = r3.length()
            r35 = r8
            r4 = 33
            r8 = 0
            r3.setSpan(r5, r8, r2, r4)
            r36 = r9
            r2 = r24
            r4 = 0
            goto L_0x0348
        L_0x02f5:
            r23 = r3
            r34 = r4
            r35 = r8
            r8 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x030c
            java.lang.String r3 = r3.substring(r8, r5)
        L_0x030c:
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0334
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0317
            r4 = 0
            r8 = 1
            goto L_0x0336
        L_0x0317:
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 10
            r8 = 32
            java.lang.String r24 = r3.replace(r4, r8)
            r4 = 0
            r5[r4] = r24
            r8 = 1
            r5[r8] = r0
            java.lang.String r5 = java.lang.String.format(r6, r5)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r5)
            r3 = r5
            r36 = r9
            goto L_0x0348
        L_0x0334:
            r4 = 0
            r8 = 1
        L_0x0336:
            r36 = r9
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            r9[r4] = r3
            r9[r8] = r0
            java.lang.String r5 = java.lang.String.format(r6, r9)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r5)
            r3 = r5
        L_0x0348:
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r5 = r5[r8]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r29)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r5, r8, r4)
            r24 = r23
            goto L_0x0373
        L_0x035d:
            r34 = r4
            r35 = r8
            r36 = r9
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            boolean r4 = r4.isMedia
            if (r4 == 0) goto L_0x0373
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r5 = r1.paintIndex
            r2 = r4[r5]
        L_0x0373:
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            int r4 = r4.date
            long r4 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.unread_count
            if (r5 == 0) goto L_0x0399
            r5 = 1
            r1.drawCount = r5
            java.lang.Object[] r8 = new java.lang.Object[r5]
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.unread_count
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r9 = 0
            r8[r9] = r5
            java.lang.String r5 = "%d"
            java.lang.String r10 = java.lang.String.format(r5, r8)
            goto L_0x039c
        L_0x0399:
            r9 = 0
            r1.drawCount = r9
        L_0x039c:
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            boolean r5 = r5.sent
            if (r5 == 0) goto L_0x03a9
            r5 = 1
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r5 = 0
            goto L_0x03ae
        L_0x03a9:
            r5 = 0
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
        L_0x03ae:
            r1.drawClock = r5
            r1.drawError = r5
            org.telegram.ui.Cells.DialogCell$CustomDialog r5 = r1.customDialog
            java.lang.String r5 = r5.name
            r8 = r2
            r9 = r5
            r5 = r34
            r2 = r0
            goto L_0x1388
        L_0x03bd:
            r34 = r4
            r35 = r8
            r36 = r9
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x03de
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x03cc
            goto L_0x03de
        L_0x03cc:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03d7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x03ef
        L_0x03d7:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x03ef
        L_0x03de:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03e9
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLeft = r3
            goto L_0x03ef
        L_0x03e9:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
        L_0x03ef:
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x0479
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0642
            r3 = 1
            r1.drawNameLock = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x043e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0403
            goto L_0x043e
        L_0x0403:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0424
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r25)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0424:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x0642
        L_0x043e:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x045f
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x045f:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0479:
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0642
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0595
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x048e
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x04a3
        L_0x048e:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x049d
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x04a3
        L_0x049d:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.verified
            r1.drawVerified = r3
        L_0x04a3:
            boolean r3 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0642
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0523
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x04b1
            goto L_0x0523
        L_0x04b1:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            long r3 = r3.id
            r8 = 0
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x04d7
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x04cb
            r3 = 1
            goto L_0x04d8
        L_0x04cb:
            r3 = 1
            r1.drawNameGroup = r3
            r4 = 1099694080(0x418CLASSNAME, float:17.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x04e2
        L_0x04d7:
            r3 = 1
        L_0x04d8:
            r1.drawNameBroadcast = r3
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
        L_0x04e2:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0502
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r25)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x04f7
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x04f9
        L_0x04f7:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x04f9:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0502:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0512
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0514
        L_0x0512:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0514:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0523:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            long r3 = r3.id
            r8 = 0
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0549
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x053d
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x053d
            r3 = 1
            goto L_0x054a
        L_0x053d:
            r3 = 1
            r1.drawNameGroup = r3
            r4 = 1096286208(0x41580000, float:13.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x0554
        L_0x0549:
            r3 = 1
        L_0x054a:
            r1.drawNameBroadcast = r3
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
        L_0x0554:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0574
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0569
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x056b
        L_0x0569:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x056b:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0574:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0584
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0586
        L_0x0584:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0586:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
            goto L_0x0642
        L_0x0595:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x0642
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x05a6
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x05bb
        L_0x05a6:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x05b5
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x05bb
        L_0x05b5:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.verified
            r1.drawVerified = r3
        L_0x05bb:
            boolean r3 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0642
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.bot
            if (r3 == 0) goto L_0x0642
            r3 = 1
            r1.drawNameBot = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x060a
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x05d1
            goto L_0x060a
        L_0x05d1:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x05f1
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r25)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x05f1:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x0642
        L_0x060a:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x062a
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0642
        L_0x062a:
            int r3 = r55.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.nameLeft = r3
        L_0x0642:
            int r3 = r1.lastMessageDate
            int r4 = r1.lastMessageDate
            if (r4 != 0) goto L_0x0650
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x0650
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r3 = r4.date
        L_0x0650:
            boolean r4 = r1.isDialogCell
            if (r4 == 0) goto L_0x06b3
            int r4 = r1.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            long r8 = r1.currentDialogId
            r5 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r4.getDraft(r8, r5)
            r1.draftMessage = r4
            if (r4 == 0) goto L_0x067d
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0673
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.reply_to_msg_id
            if (r4 == 0) goto L_0x06ad
        L_0x0673:
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.date
            if (r3 <= r4) goto L_0x067d
            int r4 = r1.unreadCount
            if (r4 != 0) goto L_0x06ad
        L_0x067d:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x069f
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x069f
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.creator
            if (r4 != 0) goto L_0x069f
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            if (r4 == 0) goto L_0x06ad
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            boolean r4 = r4.post_messages
            if (r4 == 0) goto L_0x06ad
        L_0x069f:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x06b1
            boolean r4 = r4.left
            if (r4 != 0) goto L_0x06ad
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.kicked
            if (r4 == 0) goto L_0x06b6
        L_0x06ad:
            r4 = 0
            r1.draftMessage = r4
            goto L_0x06b6
        L_0x06b1:
            r4 = 0
            goto L_0x06b6
        L_0x06b3:
            r4 = 0
            r1.draftMessage = r4
        L_0x06b6:
            if (r13 == 0) goto L_0x0750
            r1.lastPrintString = r13
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r8 = r1.currentDialogId
            r5 = 0
            java.lang.Integer r4 = r4.getPrintingStringType(r8, r5)
            int r4 = r4.intValue()
            r1.printingStringType = r4
            org.telegram.ui.Components.StatusDrawable r4 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r4)
            r5 = 0
            if (r4 == 0) goto L_0x06e0
            int r8 = r4.getIntrinsicWidth()
            r9 = 1077936128(0x40400000, float:3.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r8 + r9
        L_0x06e0:
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>()
            r25 = r3
            r9 = 1
            java.lang.String[] r3 = new java.lang.String[r9]
            java.lang.String r21 = "..."
            r20 = 0
            r3[r20] = r21
            r23 = r4
            java.lang.String[] r4 = new java.lang.String[r9]
            java.lang.String r9 = ""
            r4[r20] = r9
            java.lang.CharSequence r13 = android.text.TextUtils.replace(r13, r3, r4)
            int r3 = r1.printingStringType
            r4 = 5
            if (r3 != r4) goto L_0x070c
            java.lang.String r3 = r13.toString()
            java.lang.String r4 = "**oo**"
            int r4 = r3.indexOf(r4)
            goto L_0x070e
        L_0x070c:
            r4 = r34
        L_0x070e:
            if (r4 < 0) goto L_0x072e
            android.text.SpannableStringBuilder r3 = r8.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r9 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r26 = r10
            int r10 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r10 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r10)
            int r10 = r10.getIntrinsicWidth()
            r9.<init>(r10)
            int r10 = r4 + 6
            r37 = r11
            r11 = 0
            r3.setSpan(r9, r4, r10, r11)
            goto L_0x0746
        L_0x072e:
            r26 = r10
            r37 = r11
            r11 = 0
            java.lang.String r3 = " "
            android.text.SpannableStringBuilder r3 = r8.append(r3)
            android.text.SpannableStringBuilder r3 = r3.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r9 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r9.<init>(r5)
            r10 = 1
            r3.setSpan(r9, r11, r10, r11)
        L_0x0746:
            r12 = r8
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r2 = r3[r9]
            r3 = 0
            goto L_0x113d
        L_0x0750:
            r25 = r3
            r26 = r10
            r37 = r11
            r3 = 0
            r1.lastPrintString = r3
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            if (r3 == 0) goto L_0x07fb
            r3 = 0
            r4 = 2131625323(0x7f0e056b, float:1.887785E38)
            java.lang.String r5 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x079c
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0796
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x077a
            goto L_0x0796
        L_0x077a:
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r5 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.String r9 = "chats_draft"
            r5.<init>(r9, r8)
            int r8 = r0.length()
            r9 = 33
            r10 = 0
            r4.setSpan(r5, r10, r8, r9)
            r12 = r4
            r4 = r34
            goto L_0x113d
        L_0x0796:
            java.lang.String r12 = ""
            r4 = r34
            goto L_0x113d
        L_0x079c:
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            java.lang.String r4 = r4.message
            int r5 = r4.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r5 <= r8) goto L_0x07ae
            r5 = 0
            java.lang.String r4 = r4.substring(r5, r8)
            goto L_0x07af
        L_0x07ae:
            r5 = 0
        L_0x07af:
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = 32
            r10 = 10
            java.lang.String r11 = r4.replace(r10, r8)
            r9[r5] = r11
            r5 = 1
            r9[r5] = r0
            java.lang.String r5 = java.lang.String.format(r6, r9)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r5)
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x07e4
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x07e4
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            java.lang.String r10 = "chats_draft"
            r8.<init>(r10, r9)
            int r9 = r0.length()
            r10 = 1
            int r9 = r9 + r10
            r10 = 33
            r11 = 0
            r5.setSpan(r8, r11, r9, r10)
        L_0x07e4:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r8 = r8[r9]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r10 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r5, r8, r9, r10)
            r4 = r34
            goto L_0x113d
        L_0x07fb:
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0814
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r2 = r3[r4]
            r3 = 2131625899(0x7f0e07ab, float:1.8879019E38)
            java.lang.String r4 = "HistoryCleared"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x0814:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 != 0) goto L_0x08d5
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x08ad
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r2 = r3[r4]
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested
            if (r4 == 0) goto L_0x0837
            r3 = 2131625420(0x7f0e05cc, float:1.8878047E38)
            java.lang.String r4 = "EncryptionProcessing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x0837:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r4 == 0) goto L_0x0856
            r3 = 2131624543(0x7f0e025f, float:1.8876269E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r8 = 0
            r5[r8] = r4
            java.lang.String r4 = "AwaitingEncryption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x0856:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r4 == 0) goto L_0x0869
            r3 = 2131625421(0x7f0e05cd, float:1.887805E38)
            java.lang.String r4 = "EncryptionRejected"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x0869:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r4 == 0) goto L_0x08a7
            long r3 = r3.admin_id
            int r5 = r1.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            long r8 = r5.getClientUserId()
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0898
            r3 = 2131625409(0x7f0e05c1, float:1.8878025E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r8 = 0
            r5[r8] = r4
            java.lang.String r4 = "EncryptedChatStartedOutgoing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x0898:
            r3 = 2131625408(0x7f0e05c0, float:1.8878023E38)
            java.lang.String r4 = "EncryptedChatStartedIncoming"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x08a7:
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x08ad:
            int r3 = r1.dialogsType
            r4 = 3
            if (r3 != r4) goto L_0x08cd
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x08cd
            r3 = 2131627604(0x7f0e0e54, float:1.8882477E38)
            java.lang.String r4 = "SavedMessagesInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r15 = 0
            r3 = 0
            r18 = r3
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x08cd:
            java.lang.String r12 = ""
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x08d5:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r3.restriction_reason
            java.lang.String r3 = org.telegram.messenger.MessagesController.getRestrictionReason(r3)
            r4 = 0
            r5 = 0
            org.telegram.messenger.MessageObject r8 = r1.message
            long r8 = r8.getFromChatId()
            boolean r10 = org.telegram.messenger.DialogObject.isUserDialog(r8)
            if (r10 == 0) goto L_0x08fa
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.lang.Long r11 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r4 = r10.getUser(r11)
            goto L_0x090e
        L_0x08fa:
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            r38 = r4
            r11 = r5
            long r4 = -r8
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r5 = r10.getChat(r4)
            r4 = r38
        L_0x090e:
            int r10 = r1.dialogsType
            r11 = 3
            if (r10 != r11) goto L_0x0931
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r10)
            if (r10 == 0) goto L_0x0931
            r10 = 2131627604(0x7f0e0e54, float:1.8882477E38)
            java.lang.String r11 = "SavedMessagesInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r11 = 0
            r18 = 0
            r40 = r4
            r41 = r5
            r38 = r8
            r12 = r10
            r15 = r11
            goto L_0x112c
        L_0x0931:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x094d
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 != 0) goto L_0x094d
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x094d
            r10 = 0
            java.lang.CharSequence r11 = r55.formatArchivedDialogNames()
            r40 = r4
            r41 = r5
            r38 = r8
            r24 = r10
            r12 = r11
            goto L_0x112c
        L_0x094d:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r10 == 0) goto L_0x0988
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r10)
            if (r10 == 0) goto L_0x0975
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r10 != 0) goto L_0x0971
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x0975
        L_0x0971:
            java.lang.String r10 = ""
            r15 = 0
            goto L_0x0979
        L_0x0975:
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.CharSequence r10 = r10.messageText
        L_0x0979:
            android.text.TextPaint[] r11 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r12 = r1.paintIndex
            r2 = r11[r12]
            r40 = r4
            r41 = r5
            r38 = r8
            r12 = r10
            goto L_0x112c
        L_0x0988:
            r10 = 1
            boolean r11 = android.text.TextUtils.isEmpty(r3)
            if (r11 == 0) goto L_0x0aba
            int r11 = r1.currentDialogFolderId
            if (r11 != 0) goto L_0x0aba
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r1.encryptedChat
            if (r11 != 0) goto L_0x0aba
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.needDrawBluredPreview()
            if (r11 != 0) goto L_0x0aba
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isPhoto()
            if (r11 != 0) goto L_0x09c0
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isNewGif()
            if (r11 != 0) goto L_0x09c0
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVideo()
            if (r11 == 0) goto L_0x09b8
            goto L_0x09c0
        L_0x09b8:
            r38 = r8
            r40 = r10
            r42 = r12
            goto L_0x0ac0
        L_0x09c0:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isWebpage()
            if (r11 == 0) goto L_0x09d3
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            org.telegram.tgnet.TLRPC$WebPage r11 = r11.webpage
            java.lang.String r11 = r11.type
            goto L_0x09d4
        L_0x09d3:
            r11 = 0
        L_0x09d4:
            r38 = r8
            java.lang.String r8 = "app"
            boolean r8 = r8.equals(r11)
            if (r8 != 0) goto L_0x0ab3
            java.lang.String r8 = "profile"
            boolean r8 = r8.equals(r11)
            if (r8 != 0) goto L_0x0ab3
            java.lang.String r8 = "article"
            boolean r8 = r8.equals(r11)
            if (r8 != 0) goto L_0x0ab3
            if (r11 == 0) goto L_0x09ff
            java.lang.String r8 = "telegram_"
            boolean r8 = r11.startsWith(r8)
            if (r8 != 0) goto L_0x09f9
            goto L_0x09ff
        L_0x09f9:
            r40 = r10
            r42 = r12
            goto L_0x0ac0
        L_0x09ff:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            r40 = r10
            int r10 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r10)
            if (r8 != r9) goto L_0x0a1a
            r9 = 0
        L_0x0a1a:
            if (r8 == 0) goto L_0x0aae
            r10 = 1
            r1.hasMessageThumb = r10
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isVideo()
            r1.drawPlay = r10
            java.lang.String r10 = org.telegram.messenger.FileLoader.getAttachFileName(r9)
            r41 = r11
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.mediaExists
            if (r11 != 0) goto L_0x0a73
            int r11 = r1.currentAccount
            org.telegram.messenger.DownloadController r11 = org.telegram.messenger.DownloadController.getInstance(r11)
            r42 = r12
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r11 = r11.canDownloadMedia((org.telegram.messenger.MessageObject) r12)
            if (r11 != 0) goto L_0x0a75
            int r11 = r1.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            boolean r11 = r11.isLoadingFile(r10)
            if (r11 == 0) goto L_0x0a50
            goto L_0x0a75
        L_0x0a50:
            org.telegram.messenger.ImageReceiver r11 = r1.thumbImage
            r44 = 0
            r45 = 0
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLObject r12 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r46 = org.telegram.messenger.ImageLocation.getForObject(r8, r12)
            r12 = 0
            r48 = r12
            android.graphics.drawable.Drawable r48 = (android.graphics.drawable.Drawable) r48
            org.telegram.messenger.MessageObject r12 = r1.message
            r50 = 0
            java.lang.String r47 = "20_20"
            r43 = r11
            r49 = r12
            r43.setImage((org.telegram.messenger.ImageLocation) r44, (java.lang.String) r45, (org.telegram.messenger.ImageLocation) r46, (java.lang.String) r47, (android.graphics.drawable.Drawable) r48, (java.lang.Object) r49, (int) r50)
            r52 = r10
            goto L_0x0aac
        L_0x0a73:
            r42 = r12
        L_0x0a75:
            org.telegram.messenger.MessageObject r11 = r1.message
            int r11 = r11.type
            r12 = 1
            if (r11 != r12) goto L_0x0a83
            if (r9 == 0) goto L_0x0a81
            int r11 = r9.size
            goto L_0x0a82
        L_0x0a81:
            r11 = 0
        L_0x0a82:
            goto L_0x0a84
        L_0x0a83:
            r11 = 0
        L_0x0a84:
            org.telegram.messenger.ImageReceiver r12 = r1.thumbImage
            r52 = r10
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLObject r10 = r10.photoThumbsObject
            org.telegram.messenger.ImageLocation r44 = org.telegram.messenger.ImageLocation.getForObject(r9, r10)
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLObject r10 = r10.photoThumbsObject
            org.telegram.messenger.ImageLocation r46 = org.telegram.messenger.ImageLocation.getForObject(r8, r10)
            r49 = 0
            org.telegram.messenger.MessageObject r10 = r1.message
            r51 = 0
            java.lang.String r45 = "20_20"
            java.lang.String r47 = "20_20"
            r43 = r12
            r48 = r11
            r50 = r10
            r43.setImage(r44, r45, r46, r47, r48, r49, r50, r51)
        L_0x0aac:
            r10 = 0
            goto L_0x0ac2
        L_0x0aae:
            r41 = r11
            r42 = r12
            goto L_0x0ac0
        L_0x0ab3:
            r40 = r10
            r41 = r11
            r42 = r12
            goto L_0x0ac0
        L_0x0aba:
            r38 = r8
            r40 = r10
            r42 = r12
        L_0x0ac0:
            r10 = r40
        L_0x0ac2:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x0e52
            long r8 = r8.id
            r11 = 0
            int r40 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r40 <= 0) goto L_0x0e52
            if (r5 != 0) goto L_0x0e52
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0ae7
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isMegagroup(r8)
            if (r8 == 0) goto L_0x0ae1
            goto L_0x0ae7
        L_0x0ae1:
            r40 = r4
            r41 = r5
            goto L_0x0e56
        L_0x0ae7:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isOutOwner()
            if (r8 == 0) goto L_0x0afa
            r8 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r8 = r0
            goto L_0x0b57
        L_0x0afa:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x0b18
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            if (r8 == 0) goto L_0x0b18
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            java.lang.String r8 = r8.from_name
            if (r8 == 0) goto L_0x0b18
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            java.lang.String r0 = r8.from_name
            r8 = r0
            goto L_0x0b57
        L_0x0b18:
            if (r4 == 0) goto L_0x0b54
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x0b31
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x0b23
            goto L_0x0b31
        L_0x0b23:
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r4)
            java.lang.String r9 = "\n"
            java.lang.String r11 = ""
            java.lang.String r0 = r8.replace(r9, r11)
            r8 = r0
            goto L_0x0b57
        L_0x0b31:
            boolean r8 = org.telegram.messenger.UserObject.isDeleted(r4)
            if (r8 == 0) goto L_0x0b42
            r8 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.String r9 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r8 = r0
            goto L_0x0b57
        L_0x0b42:
            java.lang.String r8 = r4.first_name
            java.lang.String r9 = r4.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)
            java.lang.String r9 = "\n"
            java.lang.String r11 = ""
            java.lang.String r0 = r8.replace(r9, r11)
            r8 = r0
            goto L_0x0b57
        L_0x0b54:
            java.lang.String r0 = "DELETED"
            r8 = r0
        L_0x0b57:
            r9 = 0
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0b78
            r11 = 2
            java.lang.Object[] r0 = new java.lang.Object[r11]
            r11 = 0
            r0[r11] = r3
            r11 = 1
            r0[r11] = r8
            java.lang.String r0 = java.lang.String.format(r6, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r40 = r4
            r41 = r5
            r24 = r9
            r4 = r0
            goto L_0x0dc0
        L_0x0b78:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            if (r0 == 0) goto L_0x0bf9
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            java.lang.String r0 = r0.toString()
            int r11 = r0.length()
            r12 = 150(0x96, float:2.1E-43)
            if (r11 <= r12) goto L_0x0b93
            r11 = 0
            java.lang.String r0 = r0.substring(r11, r12)
        L_0x0b93:
            if (r10 != 0) goto L_0x0b98
            java.lang.String r11 = ""
            goto L_0x0bc6
        L_0x0b98:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVideo()
            if (r11 == 0) goto L_0x0ba3
            java.lang.String r11 = "📹 "
            goto L_0x0bc6
        L_0x0ba3:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVoice()
            if (r11 == 0) goto L_0x0bae
            java.lang.String r11 = "🎤 "
            goto L_0x0bc6
        L_0x0bae:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isMusic()
            if (r11 == 0) goto L_0x0bb9
            java.lang.String r11 = "🎧 "
            goto L_0x0bc6
        L_0x0bb9:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isPhoto()
            if (r11 == 0) goto L_0x0bc4
            java.lang.String r11 = "🖼 "
            goto L_0x0bc6
        L_0x0bc4:
            java.lang.String r11 = "📎 "
        L_0x0bc6:
            r40 = r4
            r12 = 2
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            r41 = r5
            r24 = r9
            r23 = r11
            r5 = 32
            r9 = 10
            java.lang.String r11 = r0.replace(r9, r5)
            r12.append(r11)
            java.lang.String r5 = r12.toString()
            r9 = 0
            r4[r9] = r5
            r5 = 1
            r4[r5] = r8
            java.lang.String r4 = java.lang.String.format(r6, r4)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            r4 = r0
            goto L_0x0dc0
        L_0x0bf9:
            r40 = r4
            r41 = r5
            r24 = r9
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0d2c
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0d2c
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r2 = r0[r4]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r4 < r5) goto L_0x0c3e
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r9 = r0.poll
            java.lang.String r9 = r9.question
            r11 = 0
            r5[r11] = r9
            java.lang.String r9 = "📊 ⁨%s⁩"
            java.lang.String r5 = java.lang.String.format(r9, r5)
            goto L_0x0c4e
        L_0x0c3e:
            r4 = 1
            r11 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Poll r4 = r0.poll
            java.lang.String r4 = r4.question
            r5[r11] = r4
            java.lang.String r4 = "📊 %s"
            java.lang.String r5 = java.lang.String.format(r4, r5)
        L_0x0c4e:
            goto L_0x0cf0
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0c8f
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0CLASSNAME
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r9 = 0
            r0[r9] = r5
            java.lang.String r5 = "🎮 ⁨%s⁩"
            java.lang.String r5 = java.lang.String.format(r5, r0)
            goto L_0x0cf0
        L_0x0CLASSNAME:
            r4 = 1
            r9 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r0[r9] = r4
            java.lang.String r4 = "🎮 %s"
            java.lang.String r5 = java.lang.String.format(r4, r0)
            goto L_0x0cf0
        L_0x0c8f:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0ca2
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r5 = r0.title
            goto L_0x0cf0
        L_0x0ca2:
            org.telegram.messenger.MessageObject r0 = r1.message
            int r0 = r0.type
            r4 = 14
            if (r0 != r4) goto L_0x0ce8
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x0ccc
            r4 = 2
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicAuthor()
            r9 = 0
            r0[r9] = r5
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicTitle()
            r11 = 1
            r0[r11] = r5
            java.lang.String r5 = "🎧 ⁨%s - %s⁩"
            java.lang.String r5 = java.lang.String.format(r5, r0)
            goto L_0x0cf0
        L_0x0ccc:
            r4 = 2
            r9 = 0
            r11 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.String r4 = r4.getMusicAuthor()
            r0[r9] = r4
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.String r4 = r4.getMusicTitle()
            r0[r11] = r4
            java.lang.String r4 = "🎧 %s - %s"
            java.lang.String r5 = java.lang.String.format(r4, r0)
            goto L_0x0cf0
        L_0x0ce8:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r5 = r0.toString()
        L_0x0cf0:
            r4 = 32
            r9 = 10
            java.lang.String r5 = r5.replace(r9, r4)
            r4 = 2
            java.lang.Object[] r0 = new java.lang.Object[r4]
            r4 = 0
            r0[r4] = r5
            r4 = 1
            r0[r4] = r8
            java.lang.String r0 = java.lang.String.format(r6, r0)
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0d26 }
            java.lang.String r9 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider     // Catch:{ Exception -> 0x0d26 }
            r0.<init>(r9, r11)     // Catch:{ Exception -> 0x0d26 }
            if (r22 == 0) goto L_0x0d1b
            int r9 = r8.length()     // Catch:{ Exception -> 0x0d26 }
            r11 = 2
            int r9 = r9 + r11
            goto L_0x0d1c
        L_0x0d1b:
            r9 = 0
        L_0x0d1c:
            int r11 = r4.length()     // Catch:{ Exception -> 0x0d26 }
            r12 = 33
            r4.setSpan(r0, r9, r11, r12)     // Catch:{ Exception -> 0x0d26 }
            goto L_0x0d2a
        L_0x0d26:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0d2a:
            goto L_0x0dc0
        L_0x0d2c:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0db9
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.hasHighlightedWords()
            if (r4 == 0) goto L_0x0d8c
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.String r4 = r4.messageTrimmedToHighlight
            if (r4 == 0) goto L_0x0d4c
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.String r0 = r4.messageTrimmedToHighlight
        L_0x0d4c:
            int r4 = r55.getMeasuredWidth()
            r5 = 1121058816(0x42d20000, float:105.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            if (r22 == 0) goto L_0x0d73
            boolean r5 = android.text.TextUtils.isEmpty(r8)
            if (r5 != 0) goto L_0x0d6a
            float r5 = (float) r4
            java.lang.String r9 = r8.toString()
            float r9 = r2.measureText(r9)
            float r5 = r5 - r9
            int r4 = (int) r5
        L_0x0d6a:
            float r5 = (float) r4
            java.lang.String r9 = ": "
            float r9 = r2.measureText(r9)
            float r5 = r5 - r9
            int r4 = (int) r5
        L_0x0d73:
            if (r4 <= 0) goto L_0x0d8a
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            r9 = 0
            java.lang.Object r5 = r5.get(r9)
            java.lang.String r5 = (java.lang.String) r5
            r9 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r5, r4, r2, r9)
            java.lang.String r0 = r5.toString()
        L_0x0d8a:
            r4 = 0
            goto L_0x0da7
        L_0x0d8c:
            int r4 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x0d9a
            r4 = 0
            java.lang.String r0 = r0.substring(r4, r5)
            goto L_0x0d9b
        L_0x0d9a:
            r4 = 0
        L_0x0d9b:
            r5 = 32
            r9 = 10
            java.lang.String r11 = r0.replace(r9, r5)
            java.lang.String r0 = r11.trim()
        L_0x0da7:
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            r9[r4] = r0
            r4 = 1
            r9[r4] = r8
            java.lang.String r4 = java.lang.String.format(r6, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            r4 = r0
            goto L_0x0dc0
        L_0x0db9:
            java.lang.String r0 = ""
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r4 = r0
        L_0x0dc0:
            r5 = 0
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0dc9
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0dd3
        L_0x0dc9:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0def
            int r0 = r4.length()
            if (r0 <= 0) goto L_0x0def
        L_0x0dd3:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0deb }
            java.lang.String r9 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider     // Catch:{ Exception -> 0x0deb }
            r0.<init>(r9, r11)     // Catch:{ Exception -> 0x0deb }
            int r9 = r8.length()     // Catch:{ Exception -> 0x0deb }
            r11 = 1
            int r9 = r9 + r11
            r5 = r9
            r11 = 33
            r12 = 0
            r4.setSpan(r0, r12, r9, r11)     // Catch:{ Exception -> 0x0deb }
            r14 = r5
            goto L_0x0def
        L_0x0deb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0def:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r0 = r0[r9]
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r11 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r4, r0, r9, r11)
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x0e17
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            if (r9 == 0) goto L_0x0e17
            r0 = r9
        L_0x0e17:
            boolean r9 = r1.hasMessageThumb
            if (r9 == 0) goto L_0x0e4a
            boolean r9 = r0 instanceof android.text.SpannableStringBuilder
            if (r9 != 0) goto L_0x0e25
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r0)
            r0 = r9
        L_0x0e25:
            r9 = 0
            r11 = r0
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r11.insert(r5, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r23 = r0
            int r0 = r7 + 6
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r12.<init>(r0)
            int r0 = r5 + 1
            r43 = r2
            r2 = 33
            r11.setSpan(r12, r5, r0, r2)
            r24 = r9
            r0 = r23
            goto L_0x0e4c
        L_0x0e4a:
            r43 = r2
        L_0x0e4c:
            r12 = r0
            r0 = r8
            r2 = r43
            goto L_0x112c
        L_0x0e52:
            r40 = r4
            r41 = r5
        L_0x0e56:
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x0e61
            r4 = r3
            r5 = r4
            r4 = 2
            goto L_0x1072
        L_0x0e61:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0e8e
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r4 == 0) goto L_0x0e8e
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            int r4 = r4.ttl_seconds
            if (r4 == 0) goto L_0x0e8e
            r4 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r5 = "AttachPhotoExpired"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = r4
            r4 = 2
            goto L_0x1072
        L_0x0e8e:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0ebb
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
            if (r4 == 0) goto L_0x0ebb
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            int r4 = r4.ttl_seconds
            if (r4 == 0) goto L_0x0ebb
            r4 = 2131624428(0x7f0e01ec, float:1.8876035E38)
            java.lang.String r5 = "AttachVideoExpired"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = r4
            r4 = 2
            goto L_0x1072
        L_0x0ebb:
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.caption
            if (r4 == 0) goto L_0x0f7d
            if (r10 != 0) goto L_0x0ec6
            java.lang.String r4 = ""
            goto L_0x0ef4
        L_0x0ec6:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isVideo()
            if (r4 == 0) goto L_0x0ed1
            java.lang.String r4 = "📹 "
            goto L_0x0ef4
        L_0x0ed1:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isVoice()
            if (r4 == 0) goto L_0x0edc
            java.lang.String r4 = "🎤 "
            goto L_0x0ef4
        L_0x0edc:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isMusic()
            if (r4 == 0) goto L_0x0ee7
            java.lang.String r4 = "🎧 "
            goto L_0x0ef4
        L_0x0ee7:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isPhoto()
            if (r4 == 0) goto L_0x0ef2
            java.lang.String r4 = "🖼 "
            goto L_0x0ef4
        L_0x0ef2:
            java.lang.String r4 = "📎 "
        L_0x0ef4:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.messageTrimmedToHighlight
            if (r8 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r5 = r8.messageTrimmedToHighlight
        L_0x0var_:
            int r8 = r55.getMeasuredWidth()
            r9 = 1122893824(0x42ee0000, float:119.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            if (r22 == 0) goto L_0x0f3d
            boolean r9 = android.text.TextUtils.isEmpty(r0)
            if (r9 != 0) goto L_0x0var_
            float r9 = (float) r8
            java.lang.String r11 = r0.toString()
            float r11 = r2.measureText(r11)
            float r9 = r9 - r11
            int r8 = (int) r9
        L_0x0var_:
            float r9 = (float) r8
            java.lang.String r11 = ": "
            float r11 = r2.measureText(r11)
            float r9 = r9 - r11
            int r8 = (int) r9
        L_0x0f3d:
            if (r8 <= 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords
            r11 = 0
            java.lang.Object r9 = r9.get(r11)
            java.lang.String r9 = (java.lang.String) r9
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r5, r9, r8, r2, r11)
            java.lang.String r5 = r9.toString()
        L_0x0var_:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r4)
            r9.append(r5)
            java.lang.String r5 = r9.toString()
            r4 = r5
            goto L_0x0var_
        L_0x0var_:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.CharSequence r8 = r8.caption
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            r4 = r5
        L_0x0var_:
            r5 = r4
            r4 = 2
            goto L_0x1072
        L_0x0f7d:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x0fa8
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r8 = "📊 "
            r5.append(r8)
            org.telegram.tgnet.TLRPC$Poll r8 = r4.poll
            java.lang.String r8 = r8.question
            r5.append(r8)
            java.lang.String r4 = r5.toString()
            r5 = r4
            r4 = 2
            goto L_0x105c
        L_0x0fa8:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r4 == 0) goto L_0x0fd1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "🎮 "
            r4.append(r5)
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r5 = r4
            r4 = 2
            goto L_0x105c
        L_0x0fd1:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r4 == 0) goto L_0x0fe7
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            java.lang.String r4 = r4.title
            r5 = r4
            r4 = 2
            goto L_0x105c
        L_0x0fe7:
            org.telegram.messenger.MessageObject r4 = r1.message
            int r4 = r4.type
            r5 = 14
            if (r4 != r5) goto L_0x100b
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.getMusicAuthor()
            r9 = 0
            r5[r9] = r8
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.getMusicTitle()
            r9 = 1
            r5[r9] = r8
            java.lang.String r8 = "🎧 %s - %s"
            java.lang.String r5 = java.lang.String.format(r8, r5)
            goto L_0x105c
        L_0x100b:
            r4 = 2
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x104f
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x104f
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.messageTrimmedToHighlight
            if (r8 == 0) goto L_0x102e
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r5 = r8.messageTrimmedToHighlight
        L_0x102e:
            int r8 = r55.getMeasuredWidth()
            r9 = 1119748096(0x42be0000, float:95.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords
            r11 = 0
            java.lang.Object r9 = r9.get(r11)
            java.lang.String r9 = (java.lang.String) r9
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r5, r9, r8, r2, r11)
            java.lang.String r5 = r9.toString()
            goto L_0x1053
        L_0x104f:
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.CharSequence r5 = r5.messageText
        L_0x1053:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r5, (java.util.ArrayList<java.lang.String>) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
        L_0x105c:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            if (r8 == 0) goto L_0x1072
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isMediaEmpty()
            if (r8 != 0) goto L_0x1072
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r2 = r8[r9]
        L_0x1072:
            boolean r8 = r1.hasMessageThumb
            if (r8 == 0) goto L_0x1129
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x10bc
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x10bc
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r5 = r8.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.messageTrimmedToHighlight
            if (r8 == 0) goto L_0x1098
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r5 = r8.messageTrimmedToHighlight
        L_0x1098:
            int r8 = r55.getMeasuredWidth()
            int r9 = r7 + 95
            int r9 = r9 + 6
            float r9 = (float) r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords
            r11 = 0
            java.lang.Object r9 = r9.get(r11)
            java.lang.String r9 = (java.lang.String) r9
            r11 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r5, r9, r8, r2, r11)
            java.lang.String r5 = r9.toString()
            goto L_0x10cd
        L_0x10bc:
            int r8 = r5.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x10c9
            r8 = 0
            java.lang.CharSequence r5 = r5.subSequence(r8, r9)
        L_0x10c9:
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r5)
        L_0x10cd:
            boolean r8 = r5 instanceof android.text.SpannableStringBuilder
            if (r8 != 0) goto L_0x10d7
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r5)
            r5 = r8
        L_0x10d7:
            r8 = 0
            r9 = r5
            android.text.SpannableStringBuilder r9 = (android.text.SpannableStringBuilder) r9
            java.lang.String r11 = " "
            r12 = 0
            r9.insert(r12, r11)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r11 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r4 = r7 + 6
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r11.<init>(r4)
            r23 = r2
            r2 = 1
            r4 = 33
            r9.setSpan(r11, r12, r2, r4)
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r4 = r1.paintIndex
            r2 = r2[r4]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r4 = 1099431936(0x41880000, float:17.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.messenger.Emoji.replaceEmoji(r9, r2, r11, r12)
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x1123
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r1.resourcesProvider
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r9, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            if (r2 == 0) goto L_0x1123
            r4 = r2
            r12 = r4
            r24 = r8
            r2 = r23
            goto L_0x112c
        L_0x1123:
            r12 = r5
            r24 = r8
            r2 = r23
            goto L_0x112c
        L_0x1129:
            r23 = r2
            r12 = r5
        L_0x112c:
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x1139
            java.lang.CharSequence r0 = r55.formatArchivedDialogNames()
            r3 = r24
            r4 = r34
            goto L_0x113d
        L_0x1139:
            r3 = r24
            r4 = r34
        L_0x113d:
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x1149
            int r5 = r5.date
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x1163
        L_0x1149:
            int r5 = r1.lastMessageDate
            if (r5 == 0) goto L_0x1153
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x1163
        L_0x1153:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x1161
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.date
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x1163
        L_0x1161:
            r9 = r36
        L_0x1163:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 != 0) goto L_0x117a
            r5 = 0
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r1.drawClock = r5
            r1.drawCount = r5
            r1.drawMention = r5
            r1.drawError = r5
            r10 = r26
            r11 = r37
            goto L_0x1296
        L_0x117a:
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x11c6
            int r5 = r1.unreadCount
            int r8 = r1.mentionCount
            int r10 = r5 + r8
            if (r10 <= 0) goto L_0x11bc
            if (r5 <= r8) goto L_0x11a3
            r10 = 1
            r1.drawCount = r10
            r11 = 0
            r1.drawMention = r11
            java.lang.Object[] r11 = new java.lang.Object[r10]
            int r5 = r5 + r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8 = 0
            r11[r8] = r5
            java.lang.String r5 = "%d"
            java.lang.String r5 = java.lang.String.format(r5, r11)
            r10 = r5
            r11 = r37
            goto L_0x121c
        L_0x11a3:
            r10 = 1
            r11 = 0
            r1.drawCount = r11
            r1.drawMention = r10
            java.lang.Object[] r11 = new java.lang.Object[r10]
            int r5 = r5 + r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8 = 0
            r11[r8] = r5
            java.lang.String r5 = "%d"
            java.lang.String r11 = java.lang.String.format(r5, r11)
            r10 = r26
            goto L_0x121c
        L_0x11bc:
            r8 = 0
            r1.drawCount = r8
            r1.drawMention = r8
            r10 = r26
            r11 = r37
            goto L_0x121c
        L_0x11c6:
            r8 = 0
            boolean r10 = r1.clearingDialog
            if (r10 == 0) goto L_0x11d4
            r1.drawCount = r8
            r5 = 0
            r15 = r5
            r10 = r26
            r5 = 1
            r8 = 0
            goto L_0x120f
        L_0x11d4:
            int r8 = r1.unreadCount
            if (r8 == 0) goto L_0x11fe
            r10 = 1
            if (r8 != r10) goto L_0x11e7
            int r10 = r1.mentionCount
            if (r8 != r10) goto L_0x11e7
            if (r5 == 0) goto L_0x11e7
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            boolean r5 = r5.mentioned
            if (r5 != 0) goto L_0x11fe
        L_0x11e7:
            r5 = 1
            r1.drawCount = r5
            java.lang.Object[] r8 = new java.lang.Object[r5]
            int r5 = r1.unreadCount
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r10 = 0
            r8[r10] = r5
            java.lang.String r5 = "%d"
            java.lang.String r10 = java.lang.String.format(r5, r8)
            r5 = 1
            r8 = 0
            goto L_0x120f
        L_0x11fe:
            boolean r5 = r1.markUnread
            if (r5 == 0) goto L_0x1209
            r5 = 1
            r1.drawCount = r5
            java.lang.String r10 = ""
            r8 = 0
            goto L_0x120f
        L_0x1209:
            r5 = 1
            r8 = 0
            r1.drawCount = r8
            r10 = r26
        L_0x120f:
            int r11 = r1.mentionCount
            if (r11 == 0) goto L_0x1218
            r1.drawMention = r5
            java.lang.String r11 = "@"
            goto L_0x121c
        L_0x1218:
            r1.drawMention = r8
            r11 = r37
        L_0x121c:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isOut()
            if (r5 == 0) goto L_0x128d
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 != 0) goto L_0x128d
            if (r15 == 0) goto L_0x128d
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r5 != 0) goto L_0x128d
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isSending()
            if (r5 == 0) goto L_0x1247
            r5 = 0
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r8 = 1
            r1.drawClock = r8
            r1.drawError = r5
            goto L_0x1296
        L_0x1247:
            r5 = 0
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isSendError()
            if (r8 == 0) goto L_0x125e
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r1.drawClock = r5
            r8 = 1
            r1.drawError = r8
            r1.drawCount = r5
            r1.drawMention = r5
            goto L_0x1296
        L_0x125e:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isSent()
            if (r5 == 0) goto L_0x128b
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isUnread()
            if (r5 == 0) goto L_0x127f
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x127d
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = r5.megagroup
            if (r5 != 0) goto L_0x127d
            goto L_0x127f
        L_0x127d:
            r5 = 0
            goto L_0x1280
        L_0x127f:
            r5 = 1
        L_0x1280:
            r1.drawCheck1 = r5
            r5 = 1
            r1.drawCheck2 = r5
            r5 = 0
            r1.drawClock = r5
            r1.drawError = r5
            goto L_0x1296
        L_0x128b:
            r5 = 0
            goto L_0x1296
        L_0x128d:
            r5 = 0
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r1.drawClock = r5
            r1.drawError = r5
        L_0x1296:
            r1.promoDialog = r5
            int r5 = r1.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r8 = r1.dialogsType
            if (r8 != 0) goto L_0x1304
            r8 = r2
            r23 = r3
            long r2 = r1.currentDialogId
            r24 = r4
            r4 = 1
            boolean r2 = r5.isPromoDialog(r2, r4)
            if (r2 == 0) goto L_0x1309
            r1.drawPinBackground = r4
            r1.promoDialog = r4
            int r2 = r5.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x12c6
            r2 = 2131628263(0x7f0e10e7, float:1.8883814E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r4 = r2
            r3 = r12
            goto L_0x130b
        L_0x12c6:
            int r2 = r5.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x1309
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r5.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x12f2
            r3 = 2131627383(0x7f0e0d77, float:1.8882029E38)
            java.lang.String r4 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x12f2:
            java.lang.String r3 = r5.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1301
            java.lang.String r3 = r5.promoPsaMessage
            r4 = 0
            r1.hasMessageThumb = r4
            r4 = r2
            goto L_0x130b
        L_0x1301:
            r4 = r2
            r3 = r12
            goto L_0x130b
        L_0x1304:
            r8 = r2
            r23 = r3
            r24 = r4
        L_0x1309:
            r4 = r9
            r3 = r12
        L_0x130b:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x1320
            r2 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r9 = "ArchivedChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r9 = r2
            r5 = r24
            r2 = r0
            r24 = r23
            goto L_0x1388
        L_0x1320:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x1327
            java.lang.String r2 = r2.title
            goto L_0x136c
        L_0x1327:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x136a
            boolean r2 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r2)
            if (r2 == 0) goto L_0x133b
            r2 = 2131627470(0x7f0e0dce, float:1.8882205E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x136c
        L_0x133b:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r2 == 0) goto L_0x1363
            boolean r2 = r1.useMeForMyMessages
            if (r2 == 0) goto L_0x1351
            r2 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x136c
        L_0x1351:
            int r2 = r1.dialogsType
            r9 = 3
            if (r2 != r9) goto L_0x1359
            r2 = 1
            r1.drawPinBackground = r2
        L_0x1359:
            r2 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x136c
        L_0x1363:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x136c
        L_0x136a:
            r2 = r35
        L_0x136c:
            int r9 = r2.length()
            if (r9 != 0) goto L_0x1382
            r9 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.String r12 = "HiddenName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r9 = r2
            r5 = r24
            r2 = r0
            r24 = r23
            goto L_0x1388
        L_0x1382:
            r9 = r2
            r5 = r24
            r2 = r0
            r24 = r23
        L_0x1388:
            if (r18 == 0) goto L_0x13cc
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r4)
            r23 = r13
            double r12 = (double) r0
            double r12 = java.lang.Math.ceil(r12)
            int r0 = (int) r12
            android.text.StaticLayout r12 = new android.text.StaticLayout
            android.text.TextPaint r36 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r38 = android.text.Layout.Alignment.ALIGN_NORMAL
            r39 = 1065353216(0x3var_, float:1.0)
            r40 = 0
            r41 = 0
            r34 = r12
            r35 = r4
            r37 = r0
            r34.<init>(r35, r36, r37, r38, r39, r40, r41)
            r1.timeLayout = r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x13c2
            int r12 = r55.getMeasuredWidth()
            r13 = 1097859072(0x41700000, float:15.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r12 = r12 - r0
            r1.timeLeft = r12
            goto L_0x13ca
        L_0x13c2:
            r13 = 1097859072(0x41700000, float:15.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.timeLeft = r12
        L_0x13ca:
            r12 = r0
            goto L_0x13d6
        L_0x13cc:
            r23 = r13
            r0 = 0
            r12 = 0
            r1.timeLayout = r12
            r12 = 0
            r1.timeLeft = r12
            r12 = r0
        L_0x13d6:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x13ea
            int r0 = r55.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r0 = r0 - r13
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r0 = r0 - r13
            int r0 = r0 - r12
            goto L_0x13fe
        L_0x13ea:
            int r0 = r55.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r0 = r0 - r13
            r13 = 1117388800(0x429a0000, float:77.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r0 = r0 - r13
            int r0 = r0 - r12
            int r13 = r1.nameLeft
            int r13 = r13 + r12
            r1.nameLeft = r13
        L_0x13fe:
            boolean r13 = r1.drawNameLock
            if (r13 == 0) goto L_0x1412
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r17 = r17.getIntrinsicWidth()
            int r13 = r13 + r17
            int r0 = r0 - r13
            goto L_0x144d
        L_0x1412:
            boolean r13 = r1.drawNameGroup
            if (r13 == 0) goto L_0x1426
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r17 = r17.getIntrinsicWidth()
            int r13 = r13 + r17
            int r0 = r0 - r13
            goto L_0x144d
        L_0x1426:
            boolean r13 = r1.drawNameBroadcast
            if (r13 == 0) goto L_0x143a
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r17 = r17.getIntrinsicWidth()
            int r13 = r13 + r17
            int r0 = r0 - r13
            goto L_0x144d
        L_0x143a:
            boolean r13 = r1.drawNameBot
            if (r13 == 0) goto L_0x144d
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r17 = r17.getIntrinsicWidth()
            int r13 = r13 + r17
            int r0 = r0 - r13
        L_0x144d:
            boolean r13 = r1.drawClock
            r17 = 1084227584(0x40a00000, float:5.0)
            if (r13 == 0) goto L_0x1484
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r13 = r13 + r25
            int r0 = r0 - r13
            boolean r25 = org.telegram.messenger.LocaleController.isRTL
            if (r25 != 0) goto L_0x146c
            r25 = r0
            int r0 = r1.timeLeft
            int r0 = r0 - r13
            r1.clockDrawLeft = r0
            goto L_0x147e
        L_0x146c:
            r25 = r0
            int r0 = r1.timeLeft
            int r0 = r0 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r17
            r1.clockDrawLeft = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r13
            r1.nameLeft = r0
        L_0x147e:
            r0 = r25
            r25 = r4
            goto L_0x1507
        L_0x1484:
            boolean r13 = r1.drawCheck2
            if (r13 == 0) goto L_0x1505
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r13 = r13.getIntrinsicWidth()
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r13 = r13 + r25
            int r0 = r0 - r13
            r25 = r4
            boolean r4 = r1.drawCheck1
            if (r4 == 0) goto L_0x14ea
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r4 = r4.getIntrinsicWidth()
            r26 = 1090519040(0x41000000, float:8.0)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r4 = r4 - r26
            int r0 = r0 - r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x14be
            int r4 = r1.timeLeft
            int r4 = r4 - r13
            r1.halfCheckDrawLeft = r4
            r17 = 1085276160(0x40b00000, float:5.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 - r17
            r1.checkDrawLeft = r4
            goto L_0x1507
        L_0x14be:
            int r4 = r1.timeLeft
            int r4 = r4 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r17
            r1.checkDrawLeft = r4
            r17 = 1085276160(0x40b00000, float:5.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r17
            r1.halfCheckDrawLeft = r4
            int r4 = r1.nameLeft
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r17 = r17.getIntrinsicWidth()
            int r17 = r13 + r17
            r26 = 1090519040(0x41000000, float:8.0)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r17 = r17 - r26
            int r4 = r4 + r17
            r1.nameLeft = r4
            goto L_0x1507
        L_0x14ea:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x14f4
            int r4 = r1.timeLeft
            int r4 = r4 - r13
            r1.checkDrawLeft1 = r4
            goto L_0x1507
        L_0x14f4:
            int r4 = r1.timeLeft
            int r4 = r4 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r17
            r1.checkDrawLeft1 = r4
            int r4 = r1.nameLeft
            int r4 = r4 + r13
            r1.nameLeft = r4
            goto L_0x1507
        L_0x1505:
            r25 = r4
        L_0x1507:
            boolean r4 = r1.dialogMuted
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r4 == 0) goto L_0x152e
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x152e
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x152e
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r17 = r17.getIntrinsicWidth()
            int r4 = r4 + r17
            int r0 = r0 - r4
            boolean r17 = org.telegram.messenger.LocaleController.isRTL
            if (r17 == 0) goto L_0x152b
            int r13 = r1.nameLeft
            int r13 = r13 + r4
            r1.nameLeft = r13
        L_0x152b:
            r26 = r6
            goto L_0x1574
        L_0x152e:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x154c
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            int r13 = r13 + r4
            int r0 = r0 - r13
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1549
            int r4 = r1.nameLeft
            int r4 = r4 + r13
            r1.nameLeft = r4
        L_0x1549:
            r26 = r6
            goto L_0x1574
        L_0x154c:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x1572
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r1.drawScam
            r26 = r6
            r6 = 1
            if (r4 != r6) goto L_0x1560
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1562
        L_0x1560:
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1562:
            int r4 = r4.getIntrinsicWidth()
            int r13 = r13 + r4
            int r0 = r0 - r13
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1574
            int r4 = r1.nameLeft
            int r4 = r4 + r13
            r1.nameLeft = r4
            goto L_0x1574
        L_0x1572:
            r26 = r6
        L_0x1574:
            r4 = 1094713344(0x41400000, float:12.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = java.lang.Math.max(r6, r0)
            r4 = 10
            r13 = 32
            java.lang.String r0 = r9.replace(r4, r13)     // Catch:{ Exception -> 0x15d2 }
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x15d2 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x15d2 }
            r4 = r4[r13]     // Catch:{ Exception -> 0x15d2 }
            r13 = 1094713344(0x41400000, float:12.0)
            int r34 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ Exception -> 0x15d2 }
            int r13 = r6 - r34
            float r13 = (float) r13
            r42 = r9
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x15d0 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r4, r13, r9)     // Catch:{ Exception -> 0x15d0 }
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x15d0 }
            if (r4 == 0) goto L_0x15b4
            boolean r4 = r4.hasHighlightedWords()     // Catch:{ Exception -> 0x15d0 }
            if (r4 == 0) goto L_0x15b4
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x15d0 }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x15d0 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider     // Catch:{ Exception -> 0x15d0 }
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)     // Catch:{ Exception -> 0x15d0 }
            if (r4 == 0) goto L_0x15b4
            r0 = r4
        L_0x15b4:
            android.text.StaticLayout r4 = new android.text.StaticLayout     // Catch:{ Exception -> 0x15d0 }
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x15d0 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x15d0 }
            r36 = r9[r13]     // Catch:{ Exception -> 0x15d0 }
            android.text.Layout$Alignment r38 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x15d0 }
            r39 = 1065353216(0x3var_, float:1.0)
            r40 = 0
            r41 = 0
            r34 = r4
            r35 = r0
            r37 = r6
            r34.<init>(r35, r36, r37, r38, r39, r40, r41)     // Catch:{ Exception -> 0x15d0 }
            r1.nameLayout = r4     // Catch:{ Exception -> 0x15d0 }
            goto L_0x15d8
        L_0x15d0:
            r0 = move-exception
            goto L_0x15d5
        L_0x15d2:
            r0 = move-exception
            r42 = r9
        L_0x15d5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x15d8:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x169d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x15e8
            r44 = r5
            r30 = r12
            r43 = r15
            goto L_0x16a3
        L_0x15e8:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r4 = 1106771968(0x41var_, float:31.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageNameTop = r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.timeTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.errorTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.pinTop = r4
            r4 = 1109131264(0x421CLASSNAME, float:39.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.countTop = r4
            r4 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.checkDrawTop = r9
            int r4 = r55.getMeasuredWidth()
            r9 = 1119748096(0x42be0000, float:95.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r4 = r4 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x164a
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r55.getMeasuredWidth()
            r13 = 1115684864(0x42800000, float:64.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r9 = r9 - r13
            int r13 = r7 + 11
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r9 - r13
            goto L_0x165f
        L_0x164a:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 1116078080(0x42860000, float:67.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r9
        L_0x165f:
            r19 = r4
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r30 = r12
            float r12 = (float) r9
            r31 = r9
            float r9 = (float) r0
            r33 = 1113063424(0x42580000, float:54.0)
            r43 = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r33)
            float r15 = (float) r15
            r44 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r33)
            float r5 = (float) r5
            r4.setImageCoords(r12, r9, r15, r5)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r13
            r9 = 1106247680(0x41var_, float:30.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            float r12 = (float) r7
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r15 = (float) r7
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r4.setImageCoords(r5, r9, r12, r15)
            r4 = r0
            r9 = r31
            r31 = r19
            goto L_0x1751
        L_0x169d:
            r44 = r5
            r30 = r12
            r43 = r15
        L_0x16a3:
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
            int r4 = r55.getMeasuredWidth()
            r5 = 1119485952(0x42ba0000, float:93.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x1708
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            int r5 = r55.getMeasuredWidth()
            r9 = 1115947008(0x42840000, float:66.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r5 - r9
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r5 - r9
            r13 = r9
            r9 = r5
            goto L_0x171f
        L_0x1708:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.messageNameLeft = r5
            r1.messageLeft = r5
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r5
            r13 = r9
            r9 = r5
        L_0x171f:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r12 = (float) r9
            float r15 = (float) r0
            r19 = 1113587712(0x42600000, float:56.0)
            r31 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            r33 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r9 = (float) r9
            r5.setImageCoords(r12, r15, r4, r9)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r5 = (float) r13
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r12 = (float) r12
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r15 = (float) r15
            r4.setImageCoords(r5, r9, r12, r15)
            r4 = r0
            r9 = r33
        L_0x1751:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1776
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x176e
            int r0 = r55.getMeasuredWidth()
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r0 = r0 - r5
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.pinLeft = r0
            goto L_0x1776
        L_0x176e:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x1776:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x17ab
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r31 = r31 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x1794
            int r5 = r55.getMeasuredWidth()
            r12 = 1107820544(0x42080000, float:34.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r5 = r5 - r12
            r1.errorLeft = r5
            goto L_0x17a6
        L_0x1794:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x17a6:
            r15 = r13
            r0 = r31
            goto L_0x18cf
        L_0x17ab:
            if (r10 != 0) goto L_0x17db
            if (r11 == 0) goto L_0x17b0
            goto L_0x17db
        L_0x17b0:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x17d1
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            int r31 = r31 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x17d1
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x17d1:
            r5 = 0
            r1.drawCount = r5
            r1.drawMention = r5
            r15 = r13
            r0 = r31
            goto L_0x18cf
        L_0x17db:
            if (r10 == 0) goto L_0x183f
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r10)
            r15 = r13
            double r12 = (double) r5
            double r12 = java.lang.Math.ceil(r12)
            int r5 = (int) r12
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r36 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r38 = android.text.Layout.Alignment.ALIGN_CENTER
            r39 = 1065353216(0x3var_, float:1.0)
            r40 = 0
            r41 = 0
            r34 = r0
            r35 = r10
            r37 = r5
            r34.<init>(r35, r36, r37, r38, r39, r40, r41)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r0 = r0 + r5
            int r31 = r31 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x182b
            int r5 = r55.getMeasuredWidth()
            int r12 = r1.countWidth
            int r5 = r5 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r29)
            int r5 = r5 - r12
            r1.countLeft = r5
            goto L_0x183b
        L_0x182b:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x183b:
            r5 = 1
            r1.drawCount = r5
            goto L_0x1843
        L_0x183f:
            r15 = r13
            r5 = 0
            r1.countWidth = r5
        L_0x1843:
            if (r11 == 0) goto L_0x18cd
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x187b
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r11)
            double r12 = (double) r5
            double r12 = java.lang.Math.ceil(r12)
            int r5 = (int) r12
            int r0 = java.lang.Math.max(r0, r5)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r36 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r5 = r1.mentionWidth
            android.text.Layout$Alignment r38 = android.text.Layout.Alignment.ALIGN_CENTER
            r39 = 1065353216(0x3var_, float:1.0)
            r40 = 0
            r41 = 0
            r34 = r0
            r35 = r11
            r37 = r5
            r34.<init>(r35, r36, r37, r38, r39, r40, r41)
            r1.mentionLayout = r0
            goto L_0x1883
        L_0x187b:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.mentionWidth = r0
        L_0x1883:
            int r0 = r1.mentionWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r0 = r0 + r5
            int r31 = r31 - r0
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x18ab
            int r5 = r55.getMeasuredWidth()
            int r12 = r1.mentionWidth
            int r5 = r5 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r29)
            int r5 = r5 - r12
            int r12 = r1.countWidth
            if (r12 == 0) goto L_0x18a6
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r12 = r12 + r13
            goto L_0x18a7
        L_0x18a6:
            r12 = 0
        L_0x18a7:
            int r5 = r5 - r12
            r1.mentionLeft = r5
            goto L_0x18c7
        L_0x18ab:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r29)
            int r12 = r1.countWidth
            if (r12 == 0) goto L_0x18b9
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r12 = r12 + r13
            goto L_0x18ba
        L_0x18b9:
            r12 = 0
        L_0x18ba:
            int r5 = r5 + r12
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x18c7:
            r5 = 1
            r1.drawMention = r5
            r0 = r31
            goto L_0x18cf
        L_0x18cd:
            r0 = r31
        L_0x18cf:
            if (r24 == 0) goto L_0x1929
            if (r3 != 0) goto L_0x18d5
            java.lang.String r3 = ""
        L_0x18d5:
            java.lang.String r5 = r3.toString()
            int r12 = r5.length()
            r13 = 150(0x96, float:2.1E-43)
            if (r12 <= r13) goto L_0x18e6
            r12 = 0
            java.lang.String r5 = r5.substring(r12, r13)
        L_0x18e6:
            boolean r12 = r1.useForceThreeLines
            if (r12 != 0) goto L_0x18ee
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x18f0
        L_0x18ee:
            if (r2 == 0) goto L_0x18f9
        L_0x18f0:
            r12 = 32
            r13 = 10
            java.lang.String r5 = r5.replace(r13, r12)
            goto L_0x1901
        L_0x18f9:
            java.lang.String r12 = "\n\n"
            java.lang.String r13 = "\n"
            java.lang.String r5 = r5.replace(r12, r13)
        L_0x1901:
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r13 = r1.paintIndex
            r12 = r12[r13]
            android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
            r13 = 1099431936(0x41880000, float:17.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r16 = r3
            r3 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r5, r12, r13, r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x1928
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r12, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r13)
            if (r3 == 0) goto L_0x1928
            r12 = r3
            goto L_0x1929
        L_0x1928:
            r3 = r12
        L_0x1929:
            r5 = 1094713344(0x41400000, float:12.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = java.lang.Math.max(r12, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x193b
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1992
        L_0x193b:
            if (r2 == 0) goto L_0x1992
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1946
            int r0 = r1.currentDialogFolderDialogsCount
            r12 = 1
            if (r0 != r12) goto L_0x1992
        L_0x1946:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1978 }
            if (r0 == 0) goto L_0x195d
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1978 }
            if (r0 == 0) goto L_0x195d
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1978 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1978 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider     // Catch:{ Exception -> 0x1978 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)     // Catch:{ Exception -> 0x1978 }
            if (r0 == 0) goto L_0x195d
            r2 = r0
        L_0x195d:
            android.text.TextPaint r46 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1978 }
            android.text.Layout$Alignment r48 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1978 }
            r49 = 1065353216(0x3var_, float:1.0)
            r50 = 0
            r51 = 0
            android.text.TextUtils$TruncateAt r52 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1978 }
            r54 = 1
            r45 = r2
            r47 = r5
            r53 = r5
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r45, r46, r47, r48, r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1978 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1978 }
            goto L_0x197c
        L_0x1978:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x197c:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r12 = 1109393408(0x42200000, float:40.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r12 + r4
            float r12 = (float) r12
            r0.setImageY(r12)
            goto L_0x19bc
        L_0x1992:
            r12 = 0
            r1.messageNameLayout = r12
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x19a7
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x199e
            goto L_0x19a7
        L_0x199e:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x19bc
        L_0x19a7:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r12 = 1101529088(0x41a80000, float:21.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r12 + r4
            float r12 = (float) r12
            r0.setImageY(r12)
        L_0x19bc:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1a65 }
            if (r0 != 0) goto L_0x19c4
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1a65 }
            if (r0 == 0) goto L_0x19d7
        L_0x19c4:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1a65 }
            if (r0 == 0) goto L_0x19d7
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1a65 }
            r12 = 1
            if (r0 <= r12) goto L_0x19d7
            r0 = r2
            r2 = 0
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1a65 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1a65 }
            r12 = r12[r13]     // Catch:{ Exception -> 0x1a65 }
            r8 = r12
            goto L_0x19f2
        L_0x19d7:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1a65 }
            if (r0 != 0) goto L_0x19df
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1a65 }
            if (r0 == 0) goto L_0x19e1
        L_0x19df:
            if (r2 == 0) goto L_0x19f1
        L_0x19e1:
            r12 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x1a65 }
            int r0 = r5 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1a65 }
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1a65 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r3, r8, r0, r12)     // Catch:{ Exception -> 0x1a65 }
            goto L_0x19f2
        L_0x19f1:
            r0 = r3
        L_0x19f2:
            boolean r12 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1a65 }
            if (r12 != 0) goto L_0x1a31
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1a65 }
            if (r12 == 0) goto L_0x19fb
            goto L_0x1a31
        L_0x19fb:
            boolean r12 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1a65 }
            if (r12 == 0) goto L_0x1a19
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x1a65 }
            int r13 = r13 + r7
            int r5 = r5 + r13
            boolean r12 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1a65 }
            if (r12 == 0) goto L_0x1a19
            int r12 = r1.messageLeft     // Catch:{ Exception -> 0x1a65 }
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ Exception -> 0x1a65 }
            int r16 = r7 + r16
            int r12 = r12 - r16
            r1.messageLeft = r12     // Catch:{ Exception -> 0x1a65 }
        L_0x1a19:
            android.text.StaticLayout r12 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1a65 }
            android.text.Layout$Alignment r38 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1a65 }
            r39 = 1065353216(0x3var_, float:1.0)
            r40 = 0
            r41 = 0
            r34 = r12
            r35 = r0
            r36 = r8
            r37 = r5
            r34.<init>(r35, r36, r37, r38, r39, r40, r41)     // Catch:{ Exception -> 0x1a65 }
            r1.messageLayout = r12     // Catch:{ Exception -> 0x1a65 }
            goto L_0x1a64
        L_0x1a31:
            boolean r12 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1a65 }
            if (r12 == 0) goto L_0x1a3e
            if (r2 == 0) goto L_0x1a3e
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x1a65 }
            int r5 = r5 + r13
        L_0x1a3e:
            android.text.Layout$Alignment r48 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1a65 }
            r49 = 1065353216(0x3var_, float:1.0)
            r12 = 1065353216(0x3var_, float:1.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x1a65 }
            float r12 = (float) r12     // Catch:{ Exception -> 0x1a65 }
            r51 = 0
            android.text.TextUtils$TruncateAt r52 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1a65 }
            if (r2 == 0) goto L_0x1a52
            r54 = 1
            goto L_0x1a54
        L_0x1a52:
            r54 = 2
        L_0x1a54:
            r45 = r0
            r46 = r8
            r47 = r5
            r50 = r12
            r53 = r5
            android.text.StaticLayout r12 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r45, r46, r47, r48, r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1a65 }
            r1.messageLayout = r12     // Catch:{ Exception -> 0x1a65 }
        L_0x1a64:
            goto L_0x1a6c
        L_0x1a65:
            r0 = move-exception
            r12 = 0
            r1.messageLayout = r12
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1a6c:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1bc2
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1b41
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1b41
            android.text.StaticLayout r0 = r1.nameLayout
            r12 = 0
            float r0 = r0.getLineLeft(r12)
            android.text.StaticLayout r13 = r1.nameLayout
            float r13 = r13.getLineWidth(r12)
            double r12 = (double) r13
            double r12 = java.lang.Math.ceil(r12)
            r16 = r2
            boolean r2 = r1.dialogMuted
            if (r2 == 0) goto L_0x1ac6
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1ac6
            int r2 = r1.drawScam
            if (r2 != 0) goto L_0x1ac6
            int r2 = r1.nameLeft
            r19 = r3
            double r2 = (double) r2
            r27 = r7
            r28 = r8
            double r7 = (double) r6
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            int r2 = (int) r2
            r1.nameMuteLeft = r2
            goto L_0x1b27
        L_0x1ac6:
            r19 = r3
            r27 = r7
            r28 = r8
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x1af6
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r7 = (double) r6
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            int r2 = (int) r2
            r1.nameMuteLeft = r2
            goto L_0x1b27
        L_0x1af6:
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1b27
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r7 = (double) r6
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r7
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            int r7 = r1.drawScam
            r8 = 1
            if (r7 != r8) goto L_0x1b19
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1b1b
        L_0x1b19:
            org.telegram.ui.Components.ScamDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1b1b:
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            java.lang.Double.isNaN(r7)
            double r2 = r2 - r7
            int r2 = (int) r2
            r1.nameMuteLeft = r2
        L_0x1b27:
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x1b49
            double r2 = (double) r6
            int r7 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r7 >= 0) goto L_0x1b49
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r7 = (double) r6
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r7
            int r2 = (int) r2
            r1.nameLeft = r2
            goto L_0x1b49
        L_0x1b41:
            r16 = r2
            r19 = r3
            r27 = r7
            r28 = r8
        L_0x1b49:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1b8b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1b8b
            r2 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
        L_0x1b57:
            if (r3 >= r0) goto L_0x1b81
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r3)
            r8 = 0
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 != 0) goto L_0x1b7e
            android.text.StaticLayout r8 = r1.messageLayout
            float r8 = r8.getLineWidth(r3)
            double r12 = (double) r8
            double r12 = java.lang.Math.ceil(r12)
            r17 = r7
            double r7 = (double) r5
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            int r7 = (int) r7
            int r2 = java.lang.Math.min(r2, r7)
            int r3 = r3 + 1
            goto L_0x1b57
        L_0x1b7e:
            r17 = r7
            r2 = 0
        L_0x1b81:
            r3 = 2147483647(0x7fffffff, float:NaN)
            if (r2 == r3) goto L_0x1b8b
            int r3 = r1.messageLeft
            int r3 = r3 + r2
            r1.messageLeft = r3
        L_0x1b8b:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1CLASSNAME
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1CLASSNAME
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1CLASSNAME
            android.text.StaticLayout r3 = r1.messageNameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r7 = (double) r5
            int r12 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r12 >= 0) goto L_0x1CLASSNAME
            int r7 = r1.messageNameLeft
            double r7 = (double) r7
            double r12 = (double) r5
            java.lang.Double.isNaN(r12)
            double r12 = r12 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r12
            int r7 = (int) r7
            r1.messageNameLeft = r7
            goto L_0x1CLASSNAME
        L_0x1bc2:
            r16 = r2
            r19 = r3
            r27 = r7
            r28 = r8
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1c1a
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1c1a
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r6
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1bff
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r7 = (double) r6
            int r12 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r12 >= 0) goto L_0x1bff
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r12 = (double) r6
            java.lang.Double.isNaN(r12)
            double r12 = r12 - r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r12
            int r7 = (int) r7
            r1.nameLeft = r7
        L_0x1bff:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1c0b
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1c0b
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1c1a
        L_0x1c0b:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            int r2 = (int) r2
            r1.nameMuteLeft = r2
        L_0x1c1a:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1c3d
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1c3d
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1CLASSNAME:
            if (r3 >= r0) goto L_0x1CLASSNAME
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r7)
            int r3 = r3 + 1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            int r3 = r1.messageLeft
            float r3 = (float) r3
            float r3 = r3 - r2
            int r3 = (int) r3
            r1.messageLeft = r3
        L_0x1c3d:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1CLASSNAME
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1CLASSNAME
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1CLASSNAME:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1CLASSNAME
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1CLASSNAME
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1CLASSNAME }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r14 < r0) goto L_0x1CLASSNAME
            int r14 = r0 + -1
        L_0x1CLASSNAME:
            android.text.StaticLayout r2 = r1.messageLayout     // Catch:{ Exception -> 0x1CLASSNAME }
            float r2 = r2.getPrimaryHorizontal(r14)     // Catch:{ Exception -> 0x1CLASSNAME }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1CLASSNAME }
            int r7 = r14 + 1
            float r3 = r3.getPrimaryHorizontal(r7)     // Catch:{ Exception -> 0x1CLASSNAME }
            float r7 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x1CLASSNAME }
            double r7 = (double) r7     // Catch:{ Exception -> 0x1CLASSNAME }
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x1CLASSNAME }
            int r7 = (int) r7     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r7 == 0) goto L_0x1c8a
            r8 = 1077936128(0x40400000, float:3.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x1CLASSNAME }
            int r7 = r7 + r8
        L_0x1c8a:
            org.telegram.messenger.ImageReceiver r8 = r1.thumbImage     // Catch:{ Exception -> 0x1CLASSNAME }
            int r12 = r1.messageLeft     // Catch:{ Exception -> 0x1CLASSNAME }
            int r12 = r12 + r7
            r8.setImageX(r12)     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1CLASSNAME:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1cea
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1cea
            if (r44 < 0) goto L_0x1cbe
            int r2 = r44 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r2 >= r0) goto L_0x1cbe
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r44
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r3 = r1.messageLayout
            int r7 = r2 + 1
            float r3 = r3.getPrimaryHorizontal(r7)
            goto L_0x1cce
        L_0x1cbe:
            r2 = r44
            android.text.StaticLayout r0 = r1.messageLayout
            r3 = 0
            float r0 = r0.getPrimaryHorizontal(r3)
            android.text.StaticLayout r3 = r1.messageLayout
            r7 = 1
            float r3 = r3.getPrimaryHorizontal(r7)
        L_0x1cce:
            int r7 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x1cda
            int r7 = r1.messageLeft
            float r7 = (float) r7
            float r7 = r7 + r0
            int r7 = (int) r7
            r1.statusDrawableLeft = r7
            goto L_0x1cec
        L_0x1cda:
            int r7 = r1.messageLeft
            float r7 = (float) r7
            float r7 = r7 + r3
            r8 = 1077936128(0x40400000, float:3.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r7 = r7 + r8
            int r7 = (int) r7
            r1.statusDrawableLeft = r7
            goto L_0x1cec
        L_0x1cea:
            r2 = r44
        L_0x1cec:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.buildLayout():void");
    }

    private void drawCheckStatus(Canvas canvas, boolean drawClock2, boolean drawCheck12, boolean drawCheck22, boolean moveCheck, float alpha) {
        if (alpha != 0.0f || moveCheck) {
            float scale = (alpha * 0.5f) + 0.5f;
            if (drawClock2) {
                setDrawableBounds(Theme.dialogs_clockDrawable, this.clockDrawLeft, this.checkDrawTop);
                if (alpha != 1.0f) {
                    canvas.save();
                    canvas.scale(scale, scale, (float) Theme.dialogs_clockDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                    Theme.dialogs_clockDrawable.setAlpha((int) (255.0f * alpha));
                }
                Theme.dialogs_clockDrawable.draw(canvas);
                if (alpha != 1.0f) {
                    canvas.restore();
                    Theme.dialogs_clockDrawable.setAlpha(255);
                }
                invalidate();
            } else if (!drawCheck22) {
            } else {
                if (drawCheck12) {
                    setDrawableBounds(Theme.dialogs_halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
                    if (moveCheck) {
                        canvas.save();
                        canvas.scale(scale, scale, (float) Theme.dialogs_halfCheckDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        Theme.dialogs_halfCheckDrawable.setAlpha((int) (alpha * 255.0f));
                    }
                    if (!moveCheck && alpha != 0.0f) {
                        canvas.save();
                        canvas.scale(scale, scale, (float) Theme.dialogs_halfCheckDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                        Theme.dialogs_halfCheckDrawable.setAlpha((int) (alpha * 255.0f));
                        Theme.dialogs_checkReadDrawable.setAlpha((int) (255.0f * alpha));
                    }
                    Theme.dialogs_halfCheckDrawable.draw(canvas);
                    if (moveCheck) {
                        canvas.restore();
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(4.0f)) * (1.0f - alpha), 0.0f);
                    }
                    setDrawableBounds(Theme.dialogs_checkReadDrawable, this.checkDrawLeft, this.checkDrawTop);
                    Theme.dialogs_checkReadDrawable.draw(canvas);
                    if (moveCheck) {
                        canvas.restore();
                        Theme.dialogs_halfCheckDrawable.setAlpha(255);
                    }
                    if (!moveCheck && alpha != 0.0f) {
                        canvas.restore();
                        Theme.dialogs_halfCheckDrawable.setAlpha(255);
                        Theme.dialogs_checkReadDrawable.setAlpha(255);
                        return;
                    }
                    return;
                }
                setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft1, this.checkDrawTop);
                if (alpha != 1.0f) {
                    canvas.save();
                    canvas.scale(scale, scale, (float) Theme.dialogs_checkDrawable.getBounds().centerX(), (float) Theme.dialogs_halfCheckDrawable.getBounds().centerY());
                    Theme.dialogs_checkDrawable.setAlpha((int) (255.0f * alpha));
                }
                Theme.dialogs_checkDrawable.draw(canvas);
                if (alpha != 1.0f) {
                    canvas.restore();
                    Theme.dialogs_checkDrawable.setAlpha(255);
                }
            }
        }
    }

    public boolean isPointInsideAvatar(float x, float y) {
        if (!LocaleController.isRTL) {
            if (x < 0.0f || x >= ((float) AndroidUtilities.dp(60.0f))) {
                return false;
            }
            return true;
        } else if (x < ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) || x >= ((float) getMeasuredWidth())) {
            return false;
        } else {
            return true;
        }
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    public void checkCurrentDialogIndex(boolean frozen) {
        MessageObject newMessageObject;
        MessageObject messageObject;
        DialogsActivity dialogsActivity = this.parentFragment;
        if (dialogsActivity != null) {
            ArrayList<TLRPC.Dialog> dialogsArray = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, frozen);
            if (this.index < dialogsArray.size()) {
                TLRPC.Dialog dialog = dialogsArray.get(this.index);
                boolean z = true;
                TLRPC.Dialog nextDialog = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
                TLRPC.DraftMessage newDraftMessage = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId, 0);
                if (this.currentDialogFolderId != 0) {
                    newMessageObject = findFolderTopMessage();
                } else {
                    newMessageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                }
                if (this.currentDialogId != dialog.id || (((messageObject = this.message) != null && messageObject.getId() != dialog.top_message) || ((newMessageObject != null && newMessageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.mentionCount != dialog.unread_mentions_count || this.markUnread != dialog.unread_mark || this.message != newMessageObject || newDraftMessage != this.draftMessage || this.drawPin != dialog.pinned))) {
                    boolean dialogChanged = this.currentDialogId != dialog.id;
                    this.currentDialogId = dialog.id;
                    if (dialogChanged) {
                        this.lastDialogChangedTime = System.currentTimeMillis();
                        ValueAnimator valueAnimator = this.statusDrawableAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.removeAllListeners();
                            this.statusDrawableAnimator.cancel();
                        }
                        this.statusDrawableAnimationInProgress = false;
                        this.lastStatusDrawableParams = -1;
                    }
                    if (dialog instanceof TLRPC.TL_dialogFolder) {
                        this.currentDialogFolderId = ((TLRPC.TL_dialogFolder) dialog).folder.id;
                    } else {
                        this.currentDialogFolderId = 0;
                    }
                    int i = this.dialogsType;
                    if (i == 7 || i == 8) {
                        MessagesController.DialogFilter filter = MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType == 8 ? (char) 1 : 0];
                        this.fullSeparator = (dialog instanceof TLRPC.TL_dialog) && nextDialog != null && filter != null && filter.pinnedDialogs.indexOfKey(dialog.id) >= 0 && filter.pinnedDialogs.indexOfKey(nextDialog.id) < 0;
                        this.fullSeparator2 = false;
                    } else {
                        this.fullSeparator = (dialog instanceof TLRPC.TL_dialog) && dialog.pinned && nextDialog != null && !nextDialog.pinned;
                        this.fullSeparator2 = (dialog instanceof TLRPC.TL_dialogFolder) && nextDialog != null && !nextDialog.pinned;
                    }
                    if (dialogChanged) {
                        z = false;
                    }
                    update(0, z);
                    if (dialogChanged) {
                        this.reorderIconProgress = (!this.drawPin || !this.drawReorder) ? 0.0f : 1.0f;
                    }
                    checkOnline();
                    checkGroupCall();
                    checkChatTheme();
                }
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

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        }
    }

    private MessageObject findFolderTopMessage() {
        DialogsActivity dialogsActivity = this.parentFragment;
        if (dialogsActivity == null) {
            return null;
        }
        ArrayList<TLRPC.Dialog> dialogs = dialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject maxMessage = null;
        if (!dialogs.isEmpty()) {
            int N = dialogs.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Dialog dialog = dialogs.get(a);
                MessageObject object = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (object != null && (maxMessage == null || object.messageOwner.date > maxMessage.messageOwner.date)) {
                    maxMessage = object;
                }
                if (dialog.pinnedNum == 0 && maxMessage != null) {
                    break;
                }
            }
        }
        return maxMessage;
    }

    public boolean isFolderCell() {
        return this.currentDialogFolderId != 0;
    }

    public void update(int mask) {
        update(mask, true);
    }

    public void update(int mask, boolean animated) {
        long dialogId;
        TLRPC.Chat chat2;
        MessageObject messageObject;
        int newMentionCount;
        int newCount;
        MessageObject messageObject2;
        CustomDialog customDialog2 = this.customDialog;
        boolean z = true;
        if (customDialog2 != null) {
            this.lastMessageDate = customDialog2.date;
            if (this.customDialog.unread_count == 0) {
                z = false;
            }
            this.lastUnreadState = z;
            this.unreadCount = this.customDialog.unread_count;
            this.drawPin = this.customDialog.pinned;
            this.dialogMuted = this.customDialog.muted;
            this.avatarDrawable.setInfo((long) this.customDialog.id, this.customDialog.name, (String) null);
            this.avatarImage.setImage((String) null, "50_50", this.avatarDrawable, (String) null, 0);
            this.thumbImage.setImageBitmap((Drawable) null);
        } else {
            int oldUnreadCount = this.unreadCount;
            boolean oldMarkUnread = this.markUnread;
            if (this.isDialogCell) {
                TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (dialog == null) {
                    this.unreadCount = 0;
                    this.mentionCount = 0;
                    this.currentEditDate = 0;
                    this.lastMessageDate = 0;
                    this.clearingDialog = false;
                } else if (mask == 0) {
                    this.clearingDialog = MessagesController.getInstance(this.currentAccount).isClearingDialog(dialog.id);
                    MessageObject messageObject3 = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                    this.message = messageObject3;
                    this.lastUnreadState = messageObject3 != null && messageObject3.isUnread();
                    if (dialog instanceof TLRPC.TL_dialogFolder) {
                        this.unreadCount = MessagesStorage.getInstance(this.currentAccount).getArchiveUnreadCount();
                        this.mentionCount = 0;
                    } else {
                        this.unreadCount = dialog.unread_count;
                        this.mentionCount = dialog.unread_mentions_count;
                    }
                    this.markUnread = dialog.unread_mark;
                    MessageObject messageObject4 = this.message;
                    this.currentEditDate = messageObject4 != null ? messageObject4.messageOwner.edit_date : 0;
                    this.lastMessageDate = dialog.last_message_date;
                    int i = this.dialogsType;
                    if (i == 7 || i == 8) {
                        MessagesController.DialogFilter filter = MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType == 8 ? (char) 1 : 0];
                        this.drawPin = filter != null && filter.pinnedDialogs.indexOfKey(dialog.id) >= 0;
                    } else {
                        this.drawPin = this.currentDialogFolderId == 0 && dialog.pinned;
                    }
                    MessageObject messageObject5 = this.message;
                    if (messageObject5 != null) {
                        this.lastSendState = messageObject5.messageOwner.send_state;
                    }
                }
            } else {
                this.drawPin = false;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if (!(this.user == null || (mask & MessagesController.UPDATE_MASK_STATUS) == 0)) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.user.id));
                    invalidate();
                }
                if (this.isDialogCell && (mask & MessagesController.UPDATE_MASK_USER_PRINT) != 0) {
                    CharSequence printString = MessagesController.getInstance(this.currentAccount).getPrintingString(this.currentDialogId, 0, true);
                    CharSequence charSequence = this.lastPrintString;
                    if ((charSequence != null && printString == null) || ((charSequence == null && printString != null) || (charSequence != null && !charSequence.equals(printString)))) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || (mask & MessagesController.UPDATE_MASK_MESSAGE_TEXT) == 0 || (messageObject2 = this.message) == null || messageObject2.messageText == this.lastMessageString)) {
                    continueUpdate = true;
                }
                if (!(continueUpdate || (mask & MessagesController.UPDATE_MASK_CHAT) == 0 || this.chat == null)) {
                    TLRPC.Chat newChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chat.id));
                    if ((newChat.call_active && newChat.call_not_empty) != this.hasCall) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && (mask & MessagesController.UPDATE_MASK_AVATAR) != 0 && this.chat == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (mask & MessagesController.UPDATE_MASK_NAME) != 0 && this.chat == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (mask & MessagesController.UPDATE_MASK_CHAT_AVATAR) != 0 && this.user == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (mask & MessagesController.UPDATE_MASK_CHAT_NAME) != 0 && this.user == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (mask & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) != 0) {
                    MessageObject messageObject6 = this.message;
                    if (!(messageObject6 == null || this.lastUnreadState == messageObject6.isUnread())) {
                        this.lastUnreadState = this.message.isUnread();
                        continueUpdate = true;
                    }
                    if (this.isDialogCell) {
                        TLRPC.Dialog dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                        if (dialog2 instanceof TLRPC.TL_dialogFolder) {
                            newCount = MessagesStorage.getInstance(this.currentAccount).getArchiveUnreadCount();
                            newMentionCount = 0;
                        } else if (dialog2 != null) {
                            newCount = dialog2.unread_count;
                            newMentionCount = dialog2.unread_mentions_count;
                        } else {
                            newCount = 0;
                            newMentionCount = 0;
                        }
                        if (!(dialog2 == null || (this.unreadCount == newCount && this.markUnread == dialog2.unread_mark && this.mentionCount == newMentionCount))) {
                            this.unreadCount = newCount;
                            this.mentionCount = newMentionCount;
                            this.markUnread = dialog2.unread_mark;
                            continueUpdate = true;
                        }
                    }
                }
                if (!(continueUpdate || (mask & MessagesController.UPDATE_MASK_SEND_STATE) == 0 || (messageObject = this.message) == null || this.lastSendState == messageObject.messageOwner.send_state)) {
                    this.lastSendState = this.message.messageOwner.send_state;
                    continueUpdate = true;
                }
                if (!continueUpdate) {
                    invalidate();
                    return;
                }
            }
            this.user = null;
            this.chat = null;
            this.encryptedChat = null;
            if (this.currentDialogFolderId != 0) {
                this.dialogMuted = false;
                MessageObject findFolderTopMessage = findFolderTopMessage();
                this.message = findFolderTopMessage;
                if (findFolderTopMessage != null) {
                    dialogId = findFolderTopMessage.getDialogId();
                } else {
                    dialogId = 0;
                }
            } else {
                this.dialogMuted = this.isDialogCell && MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId);
                dialogId = this.currentDialogId;
            }
            if (dialogId != 0) {
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
                    this.encryptedChat = encryptedChat2;
                    if (encryptedChat2 != null) {
                        this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.encryptedChat.user_id));
                    }
                } else if (DialogObject.isUserDialog(dialogId)) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
                } else {
                    TLRPC.Chat chat3 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
                    this.chat = chat3;
                    if (!(this.isDialogCell || chat3 == null || chat3.migrated_to == null || (chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chat.migrated_to.channel_id))) == null)) {
                        this.chat = chat2;
                    }
                }
                if (this.useMeForMyMessages && this.user != null && this.message.isOutOwner()) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).clientUserId));
                }
            }
            if (this.currentDialogFolderId != 0) {
                Theme.dialogs_archiveAvatarDrawable.setCallback(this);
                this.avatarDrawable.setAvatarType(2);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.user, 0);
            } else {
                TLRPC.User user2 = this.user;
                if (user2 != null) {
                    this.avatarDrawable.setInfo(user2);
                    if (UserObject.isReplyUser(this.user)) {
                        this.avatarDrawable.setAvatarType(12);
                        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.user, 0);
                    } else if (!UserObject.isUserSelf(this.user) || this.useMeForMyMessages) {
                        this.avatarImage.setForUserOrChat(this.user, this.avatarDrawable);
                    } else {
                        this.avatarDrawable.setAvatarType(1);
                        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.user, 0);
                    }
                } else {
                    TLRPC.Chat chat4 = this.chat;
                    if (chat4 != null) {
                        this.avatarDrawable.setInfo(chat4);
                        this.avatarImage.setForUserOrChat(this.chat, this.avatarDrawable);
                    }
                }
            }
            if (animated && (!(oldUnreadCount == this.unreadCount && oldMarkUnread == this.markUnread) && System.currentTimeMillis() - this.lastDialogChangedTime > 100)) {
                ValueAnimator valueAnimator = this.countAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.countAnimator = ofFloat;
                ofFloat.addUpdateListener(new DialogCell$$ExternalSyntheticLambda1(this));
                this.countAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        float unused = DialogCell.this.countChangeProgress = 1.0f;
                        StaticLayout unused2 = DialogCell.this.countOldLayout = null;
                        StaticLayout unused3 = DialogCell.this.countAnimationStableLayout = null;
                        StaticLayout unused4 = DialogCell.this.countAnimationInLayout = null;
                        DialogCell.this.invalidate();
                    }
                });
                if ((oldUnreadCount == 0 || this.markUnread) && (this.markUnread || !oldMarkUnread)) {
                    this.countAnimator.setDuration(220);
                    this.countAnimator.setInterpolator(new OvershootInterpolator());
                } else if (this.unreadCount == 0) {
                    this.countAnimator.setDuration(150);
                    this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                } else {
                    this.countAnimator.setDuration(430);
                    this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                }
                if (this.drawCount && this.countLayout != null) {
                    String oldStr = String.valueOf(oldUnreadCount);
                    String newStr = String.valueOf(this.unreadCount);
                    if (oldStr.length() == newStr.length()) {
                        SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                        SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr);
                        SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr);
                        for (int i2 = 0; i2 < oldStr.length(); i2++) {
                            if (oldStr.charAt(i2) == newStr.charAt(i2)) {
                                oldSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                                newSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                            } else {
                                stableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                            }
                        }
                        int countOldWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(oldStr)));
                        StaticLayout staticLayout = r10;
                        StaticLayout staticLayout2 = new StaticLayout(oldSpannableStr, Theme.dialogs_countTextPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countOldLayout = staticLayout;
                        this.countAnimationStableLayout = new StaticLayout(stableStr, Theme.dialogs_countTextPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationInLayout = new StaticLayout(newSpannableStr, Theme.dialogs_countTextPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.countOldLayout = this.countLayout;
                    }
                }
                this.countWidthOld = this.countWidth;
                this.countLeftOld = this.countLeft;
                this.countAnimationIncrement = this.unreadCount > oldUnreadCount;
                this.countAnimator.start();
            }
        }
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        if (!animated) {
            this.dialogMutedProgress = this.dialogMuted ? 1.0f : 0.0f;
        }
        invalidate();
    }

    /* renamed from: lambda$update$0$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m1530lambda$update$0$orgtelegramuiCellsDialogCell(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getTranslationX() {
        return this.translationX;
    }

    public void setTranslationX(float value) {
        float f = (float) ((int) value);
        this.translationX = f;
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        boolean z = false;
        if (rLottieDrawable != null && f == 0.0f) {
            rLottieDrawable.setProgress(0.0f);
            this.translationAnimationStarted = false;
            this.archiveHidden = SharedConfig.archiveHidden;
            this.currentRevealProgress = 0.0f;
            this.isSliding = false;
        }
        float f2 = this.translationX;
        if (f2 != 0.0f) {
            this.isSliding = true;
        } else {
            this.currentRevealBounceProgress = 0.0f;
            this.currentRevealProgress = 0.0f;
            this.drawRevealBackground = false;
        }
        if (this.isSliding && !this.swipeCanceled) {
            boolean prevValue = this.drawRevealBackground;
            if (Math.abs(f2) >= ((float) getMeasuredWidth()) * 0.45f) {
                z = true;
            }
            this.drawRevealBackground = z;
            if (prevValue != z && this.archiveHidden == SharedConfig.archiveHidden) {
                try {
                    performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
            }
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x09ac  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x09af  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x09b9  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x09cb  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0a04  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r35) {
        /*
            r34 = this;
            r8 = r34
            r9 = r35
            long r0 = r8.currentDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0011
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            r0 = 0
            int r1 = r8.currentDialogFolderId
            r10 = 0
            r11 = 0
            if (r1 == 0) goto L_0x003f
            org.telegram.ui.Components.PullForegroundDrawable r1 = r8.archivedChatsDrawable
            if (r1 == 0) goto L_0x003f
            float r1 = r1.outProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x003f
            float r1 = r8.translationX
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x003f
            r35.save()
            int r1 = r34.getMeasuredWidth()
            int r2 = r34.getMeasuredHeight()
            r9.clipRect(r10, r10, r1, r2)
            org.telegram.ui.Components.PullForegroundDrawable r1 = r8.archivedChatsDrawable
            r1.draw(r9)
            r35.restore()
            return
        L_0x003f:
            long r12 = android.os.SystemClock.elapsedRealtime()
            long r1 = r8.lastUpdateTime
            long r1 = r12 - r1
            r3 = 17
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0051
            r1 = 17
            r14 = r1
            goto L_0x0052
        L_0x0051:
            r14 = r1
        L_0x0052:
            r8.lastUpdateTime = r12
            float r1 = r8.clipProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0080
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r1 == r2) goto L_0x0080
            r35.save()
            int r1 = r8.topClip
            float r1 = (float) r1
            float r2 = r8.clipProgress
            float r1 = r1 * r2
            int r2 = r34.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r34.getMeasuredHeight()
            int r4 = r8.bottomClip
            float r4 = (float) r4
            float r5 = r8.clipProgress
            float r4 = r4 * r5
            int r4 = (int) r4
            int r3 = r3 - r4
            float r3 = (float) r3
            r9.clipRect(r11, r1, r2, r3)
        L_0x0080:
            float r1 = r8.translationX
            r7 = 4
            r16 = 1090519040(0x41000000, float:8.0)
            r17 = 1073741824(0x40000000, float:2.0)
            r18 = 1082130432(0x40800000, float:4.0)
            r5 = 1
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x00b8
            float r1 = r8.cornerProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0095
            goto L_0x00b8
        L_0x0095:
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            if (r1 == 0) goto L_0x00b2
            r1.stop()
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r2 = 0
            r1.setCallback(r2)
            r1 = 0
            r8.translationDrawable = r1
            r8.translationAnimationStarted = r10
            r33 = r0
            r31 = r12
            goto L_0x04e5
        L_0x00b2:
            r33 = r0
            r31 = r12
            goto L_0x04e5
        L_0x00b8:
            r35.save()
            int r1 = r8.currentDialogFolderId
            java.lang.String r3 = "chats_archivePinBackground"
            java.lang.String r2 = "chats_archiveBackground"
            if (r1 == 0) goto L_0x0109
            boolean r1 = r8.archiveHidden
            if (r1 == 0) goto L_0x00e8
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r8.resourcesProvider
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r10 = 2131628209(0x7f0e10b1, float:1.8883704E38)
            r19 = r10
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r10)
            org.telegram.ui.Components.RLottieDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r10
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x00e8:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 2131625892(0x7f0e07a4, float:1.8879005E38)
            r19 = r4
            java.lang.String r10 = "HideOnTop"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Components.RLottieDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r10
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x0109:
            boolean r1 = r8.promoDialog
            if (r1 == 0) goto L_0x012e
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 2131627374(0x7f0e0d6e, float:1.888201E38)
            r19 = r4
            java.lang.String r10 = "PsaHide"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Components.RLottieDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r10
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x012e:
            int r1 = r8.folderId
            if (r1 != 0) goto L_0x021d
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            r10 = 3
            if (r4 != r10) goto L_0x0175
            boolean r4 = r8.dialogMuted
            if (r4 == 0) goto L_0x0160
            r4 = 2131628021(0x7f0e0ff5, float:1.8883323E38)
            r19 = r4
            java.lang.String r10 = "SwipeUnmute"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Components.RLottieDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r10
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x0160:
            r4 = 2131628009(0x7f0e0fe9, float:1.8883299E38)
            r19 = r4
            java.lang.String r10 = "SwipeMute"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Components.RLottieDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r10
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x0175:
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            if (r4 != r7) goto L_0x019a
            r4 = 2131628006(0x7f0e0fe6, float:1.8883292E38)
            r19 = r4
            java.lang.String r10 = "SwipeDeleteChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r8.resourcesProvider
            java.lang.String r7 = "dialogSwipeRemove"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x019a:
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            if (r4 != r5) goto L_0x01d5
            int r4 = r8.unreadCount
            if (r4 > 0) goto L_0x01c0
            boolean r4 = r8.markUnread
            if (r4 == 0) goto L_0x01ab
            goto L_0x01c0
        L_0x01ab:
            r4 = 2131628008(0x7f0e0fe8, float:1.8883297E38)
            r19 = r4
            java.lang.String r7 = "SwipeMarkAsUnread"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x01c0:
            r4 = 2131628007(0x7f0e0fe7, float:1.8883295E38)
            r19 = r4
            java.lang.String r7 = "SwipeMarkAsRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x01d5:
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            if (r4 != 0) goto L_0x0209
            boolean r4 = r8.drawPin
            if (r4 == 0) goto L_0x01f5
            r4 = 2131628022(0x7f0e0ff6, float:1.8883325E38)
            r19 = r4
            java.lang.String r7 = "SwipeUnpin"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x01f5:
            r4 = 2131628010(0x7f0e0fea, float:1.88833E38)
            r19 = r4
            java.lang.String r7 = "SwipePin"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x0209:
            r4 = 2131624317(0x7f0e017d, float:1.887581E38)
            r19 = r4
            java.lang.String r7 = "Archive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
            goto L_0x023c
        L_0x021d:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 2131628200(0x7f0e10a8, float:1.8883686E38)
            r19 = r4
            java.lang.String r7 = "Unarchive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            org.telegram.ui.Components.RLottieDrawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r7
            r7 = r1
            r10 = r4
            r1 = r19
        L_0x023c:
            boolean r4 = r8.swipeCanceled
            if (r4 == 0) goto L_0x024a
            org.telegram.ui.Components.RLottieDrawable r4 = r8.lastDrawTranslationDrawable
            if (r4 == 0) goto L_0x024a
            r8.translationDrawable = r4
            int r1 = r8.lastDrawSwipeMessageStringId
            r4 = r1
            goto L_0x0251
        L_0x024a:
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r4
            r8.lastDrawSwipeMessageStringId = r1
            r4 = r1
        L_0x0251:
            boolean r1 = r8.translationAnimationStarted
            if (r1 != 0) goto L_0x0278
            float r1 = r8.translationX
            float r1 = java.lang.Math.abs(r1)
            r19 = 1110179840(0x422CLASSNAME, float:43.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r11 = (float) r11
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x0278
            r8.translationAnimationStarted = r5
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r11 = 0
            r1.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.setCallback(r8)
            org.telegram.ui.Components.RLottieDrawable r1 = r8.translationDrawable
            r1.start()
        L_0x0278:
            int r1 = r34.getMeasuredWidth()
            float r1 = (float) r1
            float r11 = r8.translationX
            float r11 = r11 + r1
            float r1 = r8.currentRevealProgress
            r19 = 1065353216(0x3var_, float:1.0)
            int r1 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
            if (r1 >= 0) goto L_0x0307
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1.setColor(r7)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r1 = (float) r1
            float r20 = r11 - r1
            r22 = 0
            int r1 = r34.getMeasuredWidth()
            float r1 = (float) r1
            int r5 = r34.getMeasuredHeight()
            float r5 = (float) r5
            android.graphics.Paint r24 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r25 = r1
            r1 = r35
            r26 = r2
            r2 = r20
            r20 = r3
            r3 = r22
            r30 = r4
            r4 = r25
            r31 = r12
            r12 = 2
            r13 = r6
            r6 = r24
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x030f
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x02d4
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r26)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r1
        L_0x02d4:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x030f
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r26)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r26)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r26)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r1
            goto L_0x030f
        L_0x0307:
            r20 = r3
            r30 = r4
            r31 = r12
            r12 = 2
            r13 = r6
        L_0x030f:
            int r1 = r34.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r12
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x032e
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x032b
            goto L_0x032e
        L_0x032b:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x0330
        L_0x032e:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x0330:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            int r3 = r3.getIntrinsicWidth()
            int r3 = r3 / r12
            int r3 = r3 + r1
            org.telegram.ui.Components.RLottieDrawable r4 = r8.translationDrawable
            int r4 = r4.getIntrinsicHeight()
            int r4 = r4 / r12
            int r4 = r4 + r2
            float r5 = r8.currentRevealProgress
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x03dd
            r35.save()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r5 = (float) r5
            float r5 = r11 - r5
            int r6 = r34.getMeasuredWidth()
            float r6 = (float) r6
            int r12 = r34.getMeasuredHeight()
            float r12 = (float) r12
            r19 = r7
            r7 = 0
            r9.clipRect(r5, r7, r6, r12)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r5.setColor(r13)
            int r5 = r3 * r3
            int r6 = r34.getMeasuredHeight()
            int r6 = r4 - r6
            int r7 = r34.getMeasuredHeight()
            int r7 = r4 - r7
            int r6 = r6 * r7
            int r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.sqrt(r5)
            float r5 = (float) r5
            float r6 = (float) r3
            float r7 = (float) r4
            android.view.animation.AccelerateInterpolator r12 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            r33 = r0
            float r0 = r8.currentRevealProgress
            float r0 = r12.getInterpolation(r0)
            float r0 = r0 * r5
            android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r6, r7, r0, r12)
            r35.restore()
            boolean r0 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r0 != 0) goto L_0x03aa
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r7 = "Arrow.**"
            r0.setLayerColor(r7, r6)
            r12 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r12
            goto L_0x03ab
        L_0x03aa:
            r12 = 1
        L_0x03ab:
            boolean r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r0 != 0) goto L_0x03e2
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r0.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r7 = "Line 1.**"
            r0.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r7 = "Line 2.**"
            r0.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r7 = "Line 3.**"
            r0.setLayerColor(r7, r6)
            org.telegram.ui.Components.RLottieDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r0.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r12
            goto L_0x03e2
        L_0x03dd:
            r33 = r0
            r19 = r7
            r12 = 1
        L_0x03e2:
            r35.save()
            float r0 = (float) r1
            float r5 = (float) r2
            r9.translate(r0, r5)
            float r0 = r8.currentRevealBounceProgress
            r5 = 0
            int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x0412
            r7 = 1065353216(0x3var_, float:1.0)
            int r5 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r5 == 0) goto L_0x0412
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r5 = r8.interpolator
            float r0 = r5.getInterpolation(r0)
            float r0 = r0 + r7
            org.telegram.ui.Components.RLottieDrawable r5 = r8.translationDrawable
            int r5 = r5.getIntrinsicWidth()
            r6 = 2
            int r5 = r5 / r6
            float r5 = (float) r5
            org.telegram.ui.Components.RLottieDrawable r7 = r8.translationDrawable
            int r7 = r7.getIntrinsicHeight()
            int r7 = r7 / r6
            float r6 = (float) r7
            r9.scale(r0, r0, r5, r6)
        L_0x0412:
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r5 = 0
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r5, (int) r5)
            org.telegram.ui.Components.RLottieDrawable r0 = r8.translationDrawable
            r0.draw(r9)
            r35.restore()
            int r0 = r34.getMeasuredWidth()
            float r0 = (float) r0
            int r5 = r34.getMeasuredHeight()
            float r5 = (float) r5
            r6 = 0
            r9.clipRect(r11, r6, r0, r5)
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r0 = r0.measureText(r10)
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            int r5 = r8.swipeMessageTextId
            r6 = r30
            if (r5 != r6) goto L_0x0448
            int r5 = r8.swipeMessageWidth
            int r7 = r34.getMeasuredWidth()
            if (r5 == r7) goto L_0x0494
        L_0x0448:
            r8.swipeMessageTextId = r6
            int r5 = r34.getMeasuredWidth()
            r8.swipeMessageWidth = r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r24 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r7 = 1117782016(0x42a00000, float:80.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r25 = java.lang.Math.min(r7, r0)
            android.text.Layout$Alignment r26 = android.text.Layout.Alignment.ALIGN_CENTER
            r27 = 1065353216(0x3var_, float:1.0)
            r28 = 0
            r29 = 0
            r22 = r5
            r23 = r10
            r22.<init>(r23, r24, r25, r26, r27, r28, r29)
            r8.swipeMessageTextLayout = r5
            int r5 = r5.getLineCount()
            if (r5 <= r12) goto L_0x0494
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r24 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r7 = 1118044160(0x42a40000, float:82.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r25 = java.lang.Math.min(r7, r0)
            android.text.Layout$Alignment r26 = android.text.Layout.Alignment.ALIGN_CENTER
            r27 = 1065353216(0x3var_, float:1.0)
            r28 = 0
            r29 = 0
            r22 = r5
            r23 = r10
            r22.<init>(r23, r24, r25, r26, r27, r28, r29)
            r8.swipeMessageTextLayout = r5
        L_0x0494:
            android.text.StaticLayout r5 = r8.swipeMessageTextLayout
            if (r5 == 0) goto L_0x04e1
            r35.save()
            android.text.StaticLayout r5 = r8.swipeMessageTextLayout
            int r5 = r5.getLineCount()
            if (r5 <= r12) goto L_0x04aa
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r5 = -r5
            float r5 = (float) r5
            goto L_0x04ab
        L_0x04aa:
            r5 = 0
        L_0x04ab:
            int r7 = r34.getMeasuredWidth()
            r20 = 1110179840(0x422CLASSNAME, float:43.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r7 = r7 - r20
            float r7 = (float) r7
            android.text.StaticLayout r12 = r8.swipeMessageTextLayout
            int r12 = r12.getWidth()
            float r12 = (float) r12
            float r12 = r12 / r17
            float r7 = r7 - r12
            boolean r12 = r8.useForceThreeLines
            if (r12 != 0) goto L_0x04ce
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x04cb
            goto L_0x04ce
        L_0x04cb:
            r12 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x04d0
        L_0x04ce:
            r12 = 1112014848(0x42480000, float:50.0)
        L_0x04d0:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r12 = r12 + r5
            r9.translate(r7, r12)
            android.text.StaticLayout r7 = r8.swipeMessageTextLayout
            r7.draw(r9)
            r35.restore()
        L_0x04e1:
            r35.restore()
        L_0x04e5:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x04f4
            r35.save()
            float r0 = r8.translationX
            r9.translate(r0, r1)
        L_0x04f4:
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x050b
            r2 = 0
            r3 = 0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x050b:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0544
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x051a
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0544
        L_0x051a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            r4 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r4)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x056c
        L_0x0544:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x054c
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x056c
        L_0x054c:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x056c:
            float r0 = r8.translationX
            java.lang.String r10 = "windowBackgroundWhite"
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x057b
            float r0 = r8.cornerProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x062e
        L_0x057b:
            r35.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r34.getMeasuredWidth()
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r34.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r34.getMeasuredHeight()
            float r3 = (float) r3
            r4 = 0
            r0.set(r1, r4, r2, r3)
            android.graphics.RectF r0 = r8.rect
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
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x05fd
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x05cd
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x05fd
        L_0x05cd:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            float r2 = r8.archiveBackgroundProgress
            r3 = 0
            r4 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r1, r2, r4)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
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
            goto L_0x062b
        L_0x05fd:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x0605
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x062b
        L_0x0605:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
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
        L_0x062b:
            r35.restore()
        L_0x062e:
            float r0 = r8.translationX
            r11 = 1125515264(0x43160000, float:150.0)
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x064e
            float r0 = r8.cornerProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0663
            float r2 = (float) r14
            float r2 = r2 / r11
            float r0 = r0 + r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x064a
            r8.cornerProgress = r1
        L_0x064a:
            r0 = 1
            r33 = r0
            goto L_0x0663
        L_0x064e:
            float r0 = r8.cornerProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0663
            float r2 = (float) r14
            float r2 = r2 / r11
            float r0 = r0 - r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0660
            r8.cornerProgress = r1
        L_0x0660:
            r0 = 1
            r33 = r0
        L_0x0663:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x0676
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x06ae
        L_0x0676:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x0689
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x06ae
        L_0x0689:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x069c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x06ae
        L_0x069c:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x06ae
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x06ae:
            android.text.StaticLayout r0 = r8.nameLayout
            if (r0 == 0) goto L_0x0735
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06d0
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0711
        L_0x06d0:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x06f8
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x06de
            int r0 = r0.type
            r1 = 2
            if (r0 != r1) goto L_0x06de
            goto L_0x06f8
        L_0x06de:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_name"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0711
        L_0x06f8:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_secretName"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
        L_0x0711:
            r35.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x0723
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x0720
            goto L_0x0723
        L_0x0720:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x0725
        L_0x0723:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x0725:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r35.restore()
        L_0x0735:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0751
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0751
            r35.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r35.restore()
        L_0x0751:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x07ab
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x076b
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0792
        L_0x076b:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x0781
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_draft"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0792
        L_0x0781:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessage_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
        L_0x0792:
            r35.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x07a4 }
            r0.draw(r9)     // Catch:{ Exception -> 0x07a4 }
            goto L_0x07a8
        L_0x07a4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07a8:
            r35.restore()
        L_0x07ab:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x0885
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x07eb
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x07d1
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0804
        L_0x07d1:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_messageArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0804
        L_0x07eb:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r1 = r8.paintIndex
            r0 = r0[r1]
            android.text.TextPaint[] r1 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r8.paintIndex
            r1 = r1[r2]
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_message"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
        L_0x0804:
            r35.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x0816 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0816 }
            goto L_0x081a
        L_0x0816:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x081a:
            r35.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0882
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x087f
            r35.save()
            int r1 = r8.printingStringType
            r2 = 1
            if (r1 == r2) goto L_0x084e
            r2 = 4
            if (r1 != r2) goto L_0x0833
            goto L_0x084e
        L_0x0833:
            int r1 = r8.statusDrawableLeft
            float r1 = (float) r1
            int r2 = r8.messageTop
            float r2 = (float) r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r0.getIntrinsicHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 / r17
            float r2 = r2 + r3
            r9.translate(r1, r2)
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0865
        L_0x084e:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            r4 = 1
            if (r1 != r4) goto L_0x085d
            r7 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x0860
        L_0x085d:
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = 0
        L_0x0860:
            int r3 = r3 + r1
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0865:
            r0.draw(r9)
            int r1 = r8.statusDrawableLeft
            int r2 = r8.messageTop
            int r3 = r0.getIntrinsicWidth()
            int r3 = r3 + r1
            int r4 = r8.messageTop
            int r5 = r0.getIntrinsicHeight()
            int r4 = r4 + r5
            r8.invalidate(r1, r2, r3, r4)
            r35.restore()
            goto L_0x0887
        L_0x087f:
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0887
        L_0x0882:
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x0887
        L_0x0885:
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x0887:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0956
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0893
            r6 = 2
            goto L_0x0894
        L_0x0893:
            r6 = 0
        L_0x0894:
            int r0 = r0 + r6
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x089b
            r1 = 4
            goto L_0x089c
        L_0x089b:
            r1 = 0
        L_0x089c:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x08aa
            if (r1 == r0) goto L_0x08aa
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x08aa
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x08aa:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x08b0
            int r0 = r8.animateToStatusDrawableParams
        L_0x08b0:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x08b6
            r5 = 1
            goto L_0x08b7
        L_0x08b6:
            r5 = 0
        L_0x08b7:
            r19 = r5
            r2 = r0 & 2
            if (r2 == 0) goto L_0x08bf
            r5 = 1
            goto L_0x08c0
        L_0x08bf:
            r5 = 0
        L_0x08c0:
            r20 = r5
            r2 = r0 & 4
            if (r2 == 0) goto L_0x08c8
            r5 = 1
            goto L_0x08c9
        L_0x08c8:
            r5 = 0
        L_0x08c9:
            r22 = r5
            if (r1 == 0) goto L_0x092f
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x08d5
            r5 = 1
            goto L_0x08d6
        L_0x08d5:
            r5 = 0
        L_0x08d6:
            r23 = r5
            r2 = r1 & 2
            if (r2 == 0) goto L_0x08de
            r5 = 1
            goto L_0x08df
        L_0x08de:
            r5 = 0
        L_0x08df:
            r24 = r5
            r21 = 4
            r1 = r1 & 4
            if (r1 == 0) goto L_0x08e9
            r5 = 1
            goto L_0x08ea
        L_0x08e9:
            r5 = 0
        L_0x08ea:
            r25 = r5
            if (r19 != 0) goto L_0x090f
            if (r23 != 0) goto L_0x090f
            if (r25 == 0) goto L_0x090f
            if (r24 != 0) goto L_0x090f
            if (r20 == 0) goto L_0x090f
            if (r22 == 0) goto L_0x090f
            r6 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r34
            r2 = r35
            r3 = r19
            r4 = r20
            r26 = r5
            r5 = r22
            r12 = 1065353216(0x3var_, float:1.0)
            r7 = r26
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x092e
        L_0x090f:
            r12 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            float r1 = r8.statusDrawableProgress
            float r7 = r12 - r1
            r1 = r34
            r2 = r35
            r3 = r23
            r4 = r24
            r5 = r25
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r19
            r4 = r20
            r5 = r22
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x092e:
            goto L_0x0941
        L_0x092f:
            r12 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r34
            r2 = r35
            r3 = r19
            r4 = r20
            r5 = r22
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0941:
            boolean r1 = r8.drawClock
            boolean r2 = r8.drawCheck1
            if (r2 == 0) goto L_0x0949
            r6 = 2
            goto L_0x094a
        L_0x0949:
            r6 = 0
        L_0x094a:
            int r1 = r1 + r6
            boolean r2 = r8.drawCheck2
            if (r2 == 0) goto L_0x0951
            r7 = 4
            goto L_0x0952
        L_0x0951:
            r7 = 0
        L_0x0952:
            int r1 = r1 + r7
            r8.lastStatusDrawableParams = r1
            goto L_0x0958
        L_0x0956:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x0958:
            boolean r0 = r8.dialogMuted
            r1 = 1132396544(0x437var_, float:255.0)
            if (r0 != 0) goto L_0x0965
            float r2 = r8.dialogMutedProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a0b
        L_0x0965:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x0a0b
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x0a0b
            if (r0 == 0) goto L_0x0986
            float r2 = r8.dialogMutedProgress
            int r3 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0986
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0982
            r8.dialogMutedProgress = r12
            goto L_0x099f
        L_0x0982:
            r34.invalidate()
            goto L_0x099f
        L_0x0986:
            if (r0 != 0) goto L_0x099f
            float r0 = r8.dialogMutedProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x099f
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r3
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x099c
            r8.dialogMutedProgress = r2
            goto L_0x099f
        L_0x099c:
            r34.invalidate()
        L_0x099f:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09af
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09ac
            goto L_0x09af
        L_0x09ac:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x09b0
        L_0x09af:
            r4 = 0
        L_0x09b0:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09bc
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x09be
        L_0x09bc:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x09be:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 == 0) goto L_0x0a04
            r35.save()
            float r0 = r8.dialogMutedProgress
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.centerX()
            float r2 = (float) r2
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerY()
            float r3 = (float) r3
            r9.scale(r0, r0, r2, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            float r2 = r8.dialogMutedProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r2 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r2)
            r35.restore()
            goto L_0x0a7c
        L_0x0a04:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a7c
        L_0x0a0b:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0a4c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a1f
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a1c
            goto L_0x0a1f
        L_0x0a1c:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a21
        L_0x0a1f:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a21:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a38
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a35
            goto L_0x0a38
        L_0x0a35:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a3a
        L_0x0a38:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a3a:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a7c
        L_0x0a4c:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a7c
            r2 = 1
            if (r0 != r2) goto L_0x0a56
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a58
        L_0x0a56:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a58:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a66
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a63
            goto L_0x0a66
        L_0x0a63:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a68
        L_0x0a66:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a68:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            r2 = 1
            if (r0 != r2) goto L_0x0a77
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a79
        L_0x0a77:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a79:
            r0.draw(r9)
        L_0x0a7c:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a87
            float r0 = r8.reorderIconProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0a9f
        L_0x0a87:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r2 * r1
            int r2 = (int) r2
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            int r2 = r8.pinLeft
            int r3 = r8.pinTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_reorderDrawable
            r0.draw(r9)
        L_0x0a9f:
            boolean r0 = r8.drawError
            r2 = 1102577664(0x41b80000, float:23.0)
            r3 = 1094189056(0x41380000, float:11.5)
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r0 == 0) goto L_0x0af9
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r5 = r8.reorderIconProgress
            float r5 = r12 - r5
            float r5 = r5 * r1
            int r1 = (int) r5
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r5 = (float) r1
            int r6 = r8.errorTop
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r7
            float r1 = (float) r1
            int r7 = r8.errorTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r7 = r7 + r2
            float r2 = (float) r7
            r0.set(r5, r6, r1, r2)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 * r3
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 * r3
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r9.drawRoundRect(r0, r1, r2, r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r9)
            goto L_0x0e65
        L_0x0af9:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0b28
            boolean r5 = r8.drawMention
            if (r5 != 0) goto L_0x0b28
            float r5 = r8.countChangeProgress
            int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x0b08
            goto L_0x0b28
        L_0x0b08:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0e65
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r12 - r2
            float r2 = r2 * r1
            int r1 = (int) r2
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r9)
            goto L_0x0e65
        L_0x0b28:
            if (r0 != 0) goto L_0x0b30
            float r0 = r8.countChangeProgress
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 == 0) goto L_0x0daf
        L_0x0b30:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0b3d
            boolean r5 = r8.markUnread
            if (r5 != 0) goto L_0x0b3d
            float r5 = r8.countChangeProgress
            float r5 = r12 - r5
            goto L_0x0b3f
        L_0x0b3d:
            float r5 = r8.countChangeProgress
        L_0x0b3f:
            android.text.StaticLayout r6 = r8.countOldLayout
            if (r6 == 0) goto L_0x0cd2
            if (r0 != 0) goto L_0x0b47
            goto L_0x0cd2
        L_0x0b47:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b53
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b50
            goto L_0x0b53
        L_0x0b50:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b55
        L_0x0b53:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b55:
            float r6 = r8.reorderIconProgress
            float r6 = r12 - r6
            float r6 = r6 * r1
            int r6 = (int) r6
            r0.setAlpha(r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r7 = r8.reorderIconProgress
            float r7 = r12 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r6.setAlpha(r7)
            float r6 = r5 * r17
            int r7 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r7 <= 0) goto L_0x0b73
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0b73:
            int r7 = r8.countLeft
            float r7 = (float) r7
            float r7 = r7 * r6
            int r11 = r8.countLeftOld
            float r11 = (float) r11
            float r20 = r12 - r6
            float r11 = r11 * r20
            float r7 = r7 + r11
            r11 = 1085276160(0x40b00000, float:5.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r11 = r7 - r11
            android.graphics.RectF r4 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r13 = r8.countWidth
            float r13 = (float) r13
            float r13 = r13 * r6
            float r13 = r13 + r11
            int r3 = r8.countWidthOld
            float r3 = (float) r3
            float r25 = r12 - r6
            float r3 = r3 * r25
            float r13 = r13 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r13 = r13 + r3
            int r3 = r8.countTop
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 + r25
            float r3 = (float) r3
            r4.set(r11, r1, r13, r3)
            r1 = 1065353216(0x3var_, float:1.0)
            r3 = 1056964608(0x3var_, float:0.5)
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x0bc7
            r3 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r13 = r5 * r17
            float r4 = r4.getInterpolation(r13)
            float r4 = r4 * r3
            float r1 = r1 + r4
            goto L_0x0bdb
        L_0x0bc7:
            r3 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r13 = 1056964608(0x3var_, float:0.5)
            float r13 = r5 - r13
            float r13 = r13 * r17
            float r13 = r12 - r13
            float r4 = r4.getInterpolation(r13)
            float r4 = r4 * r3
            float r1 = r1 + r4
        L_0x0bdb:
            r35.save()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r1, r1, r3, r4)
            android.graphics.RectF r3 = r8.rect
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r13 = 1094189056(0x41380000, float:11.5)
            float r4 = r4 * r13
            float r24 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r24 * r13
            r9.drawRoundRect(r3, r4, r2, r0)
            android.text.StaticLayout r2 = r8.countAnimationStableLayout
            if (r2 == 0) goto L_0x0CLASSNAME
            r35.save()
            int r2 = r8.countTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r2 = r2 + r3
            float r2 = (float) r2
            r9.translate(r7, r2)
            android.text.StaticLayout r2 = r8.countAnimationStableLayout
            r2.draw(r9)
            r35.restore()
        L_0x0CLASSNAME:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r2 = r2.getAlpha()
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = (float) r2
            float r4 = r4 * r6
            int r4 = (int) r4
            r3.setAlpha(r4)
            android.text.StaticLayout r3 = r8.countAnimationInLayout
            if (r3 == 0) goto L_0x0CLASSNAME
            r35.save()
            boolean r3 = r8.countAnimationIncrement
            if (r3 == 0) goto L_0x0CLASSNAME
            r3 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0c3e
        L_0x0CLASSNAME:
            r3 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = -r4
        L_0x0c3e:
            float r3 = (float) r4
            float r4 = r12 - r6
            float r3 = r3 * r4
            int r4 = r8.countTop
            float r4 = (float) r4
            float r3 = r3 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r3 = r3 + r4
            r9.translate(r7, r3)
            android.text.StaticLayout r3 = r8.countAnimationInLayout
            r3.draw(r9)
            r35.restore()
            goto L_0x0c8c
        L_0x0CLASSNAME:
            android.text.StaticLayout r3 = r8.countLayout
            if (r3 == 0) goto L_0x0c8c
            r35.save()
            boolean r3 = r8.countAnimationIncrement
            if (r3 == 0) goto L_0x0c6b
            r3 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0CLASSNAME
        L_0x0c6b:
            r3 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = -r4
        L_0x0CLASSNAME:
            float r3 = (float) r4
            float r4 = r12 - r6
            float r3 = r3 * r4
            int r4 = r8.countTop
            float r4 = (float) r4
            float r3 = r3 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r3 = r3 + r4
            r9.translate(r7, r3)
            android.text.StaticLayout r3 = r8.countLayout
            r3.draw(r9)
            r35.restore()
        L_0x0c8c:
            android.text.StaticLayout r3 = r8.countOldLayout
            if (r3 == 0) goto L_0x0cc8
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = (float) r2
            float r13 = r12 - r6
            float r4 = r4 * r13
            int r4 = (int) r4
            r3.setAlpha(r4)
            r35.save()
            boolean r3 = r8.countAnimationIncrement
            if (r3 == 0) goto L_0x0caa
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            goto L_0x0cb0
        L_0x0caa:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0cb0:
            float r3 = (float) r3
            float r3 = r3 * r6
            int r4 = r8.countTop
            float r4 = (float) r4
            float r3 = r3 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r3 = r3 + r4
            r9.translate(r7, r3)
            android.text.StaticLayout r3 = r8.countOldLayout
            r3.draw(r9)
            r35.restore()
        L_0x0cc8:
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r3.setAlpha(r2)
            r35.restore()
            goto L_0x0daf
        L_0x0cd2:
            if (r0 != 0) goto L_0x0cd5
            goto L_0x0cd7
        L_0x0cd5:
            android.text.StaticLayout r6 = r8.countLayout
        L_0x0cd7:
            r0 = r6
            boolean r1 = r8.dialogMuted
            if (r1 != 0) goto L_0x0ce4
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x0ce1
            goto L_0x0ce4
        L_0x0ce1:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0ce6
        L_0x0ce4:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0ce6:
            float r2 = r8.reorderIconProgress
            float r4 = r12 - r2
            r2 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r2
            int r3 = (int) r4
            r1.setAlpha(r3)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r8.reorderIconProgress
            float r4 = r12 - r4
            float r4 = r4 * r2
            int r2 = (int) r4
            r3.setAlpha(r2)
            int r2 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            android.graphics.RectF r3 = r8.rect
            float r4 = (float) r2
            int r6 = r8.countTop
            float r6 = (float) r6
            int r7 = r8.countWidth
            int r7 = r7 + r2
            r11 = 1093664768(0x41300000, float:11.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r7 = r7 + r11
            float r7 = (float) r7
            int r11 = r8.countTop
            r13 = 1102577664(0x41b80000, float:23.0)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r11 = r11 + r23
            float r11 = (float) r11
            r3.set(r4, r6, r7, r11)
            int r3 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0d7f
            boolean r3 = r8.drawPin
            if (r3 == 0) goto L_0x0d6d
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r4 = r8.reorderIconProgress
            float r4 = r12 - r4
            r6 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r6
            int r4 = (int) r4
            r3.setAlpha(r4)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r4 = r8.pinLeft
            int r6 = r8.pinTop
            setDrawableBounds((android.graphics.drawable.Drawable) r3, (int) r4, (int) r6)
            r35.save()
            float r4 = r12 - r5
            float r3 = r12 - r5
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r6 = r6.getBounds()
            int r6 = r6.centerX()
            float r6 = (float) r6
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r7 = r7.getBounds()
            int r7 = r7.centerY()
            float r7 = (float) r7
            r9.scale(r4, r3, r6, r7)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r3.draw(r9)
            r35.restore()
        L_0x0d6d:
            r35.save()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r5, r5, r3, r4)
        L_0x0d7f:
            android.graphics.RectF r3 = r8.rect
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r6 = 1094189056(0x41380000, float:11.5)
            float r4 = r4 * r6
            float r7 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 * r6
            r9.drawRoundRect(r3, r4, r7, r1)
            if (r0 == 0) goto L_0x0da7
            r35.save()
            int r3 = r8.countLeft
            float r3 = (float) r3
            int r4 = r8.countTop
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r6
            float r4 = (float) r4
            r9.translate(r3, r4)
            r0.draw(r9)
            r35.restore()
        L_0x0da7:
            int r3 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0dae
            r35.restore()
        L_0x0dae:
        L_0x0daf:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0e65
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r1 = r8.reorderIconProgress
            float r4 = r12 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r3 = r8.countTop
            float r3 = (float) r3
            int r4 = r8.mentionWidth
            int r4 = r4 + r0
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            float r4 = (float) r4
            int r5 = r8.countTop
            r6 = 1102577664(0x41b80000, float:23.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            boolean r1 = r8.dialogMuted
            if (r1 == 0) goto L_0x0df3
            int r1 = r8.folderId
            if (r1 == 0) goto L_0x0df3
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0df5
        L_0x0df3:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0df5:
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r3 = r3 * r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 * r4
            r9.drawRoundRect(r2, r3, r5, r1)
            android.text.StaticLayout r2 = r8.mentionLayout
            if (r2 == 0) goto L_0x0e30
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r8.reorderIconProgress
            float r4 = r12 - r3
            r3 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r3
            int r3 = (int) r4
            r2.setAlpha(r3)
            r35.save()
            int r2 = r8.mentionLeft
            float r2 = (float) r2
            int r3 = r8.countTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r3 + r4
            float r3 = (float) r3
            r9.translate(r2, r3)
            android.text.StaticLayout r2 = r8.mentionLayout
            r2.draw(r9)
            r35.restore()
            goto L_0x0e65
        L_0x0e30:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r3 = r8.reorderIconProgress
            float r4 = r12 - r3
            r3 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r3
            int r3 = (int) r4
            r2.setAlpha(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            int r3 = r8.mentionLeft
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r4
            int r4 = r8.countTop
            r5 = 1078774989(0x404ccccd, float:3.2)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r3, (int) r4, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            r2.draw(r9)
        L_0x0e65:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0e87
            r35.save()
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
        L_0x0e87:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0e95
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0e95
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0e9a
        L_0x0e95:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0e9a:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0ed2
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0ed2
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
            setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r0, (int) r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_playDrawable
            r2.draw(r9)
        L_0x0ed2:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0ed9
            r35.restore()
        L_0x0ed9:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x1288
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x1288
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x0fb6
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x0fb6
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x0fb6
            boolean r0 = r34.isOnline()
            if (r0 != 0) goto L_0x0var_
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0fb4
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0f0e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0var_
        L_0x0f0e:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0var_:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0var_
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0var_
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0f2b
        L_0x0var_:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x0f2b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x0f4a
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x0var_
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x0var_
        L_0x0var_:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x0var_:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x0f4a:
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r3 = (float) r1
            float r4 = (float) r2
            r5 = 1088421888(0x40e00000, float:7.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r8.onlineProgress
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r4, r5, r6)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            java.lang.String r5 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r3 = (float) r1
            float r4 = (float) r2
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r8.onlineProgress
            float r5 = r5 * r6
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r4, r5, r6)
            if (r0 == 0) goto L_0x0f9e
            float r3 = r8.onlineProgress
            int r4 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x0fb4
            float r4 = (float) r14
            r5 = 1125515264(0x43160000, float:150.0)
            float r4 = r4 / r5
            float r3 = r3 + r4
            r8.onlineProgress = r3
            int r3 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r3 <= 0) goto L_0x0f9b
            r8.onlineProgress = r12
        L_0x0f9b:
            r33 = 1
            goto L_0x0fb4
        L_0x0f9e:
            float r3 = r8.onlineProgress
            r4 = 0
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fb4
            float r5 = (float) r14
            r6 = 1125515264(0x43160000, float:150.0)
            float r5 = r5 / r6
            float r3 = r3 - r5
            r8.onlineProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x0fb2
            r8.onlineProgress = r4
        L_0x0fb2:
            r33 = 1
        L_0x0fb4:
            goto L_0x1288
        L_0x0fb6:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0fb4
            boolean r0 = r0.call_active
            if (r0 == 0) goto L_0x0fc6
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x0fc6
            r5 = 1
            goto L_0x0fc7
        L_0x0fc6:
            r5 = 0
        L_0x0fc7:
            r8.hasCall = r5
            if (r5 != 0) goto L_0x0fd2
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x1288
        L_0x0fd2:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x0fe5
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x0fe5
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            float r4 = r12 - r0
            goto L_0x0fe7
        L_0x0fe5:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0fe7:
            r0 = r4
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0ff6
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0ff8
        L_0x0ff6:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x0ff8:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x101b
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1011
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1013
        L_0x1011:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x1013:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x1032
        L_0x101b:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1029
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x102b
        L_0x1029:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x102b:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x1032:
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r3 = (float) r1
            float r4 = (float) r2
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r8.chatCallProgress
            float r5 = r5 * r6
            float r5 = r5 * r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r4, r5, r6)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            java.lang.String r5 = "chats_onlineCircle"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            float r3 = (float) r1
            float r4 = (float) r2
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r8.chatCallProgress
            float r5 = r5 * r6
            float r5 = r5 * r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawCircle(r3, r4, r5, r6)
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            r4 = 1077936128(0x40400000, float:3.0)
            if (r3 != 0) goto L_0x10a3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 - r6
            goto L_0x119b
        L_0x10a3:
            r5 = 1
            if (r3 != r5) goto L_0x10c8
            r3 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 + r6
            goto L_0x119b
        L_0x10c8:
            r5 = 2
            if (r3 != r5) goto L_0x10ed
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 + r5
            r5 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 - r6
            goto L_0x119b
        L_0x10ed:
            r5 = 3
            if (r3 != r5) goto L_0x1110
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 + r6
            goto L_0x119b
        L_0x1110:
            r5 = 4
            if (r3 != r5) goto L_0x1132
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 - r6
            goto L_0x119b
        L_0x1132:
            r5 = 5
            if (r3 != r5) goto L_0x1156
            r3 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 + r6
            goto L_0x119b
        L_0x1156:
            r5 = 6
            if (r3 != r5) goto L_0x117b
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 + r5
            r5 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r11 = (float) r11
            float r13 = r8.innerProgress
            float r11 = r11 * r13
            float r6 = r6 - r11
            r5 = r6
            goto L_0x119b
        L_0x117b:
            r5 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r3 = r3 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r11 = r8.innerProgress
            float r6 = r6 * r11
            float r5 = r5 + r6
        L_0x119b:
            float r6 = r8.chatCallProgress
            int r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r6 < 0) goto L_0x11a5
            int r6 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r6 >= 0) goto L_0x11b3
        L_0x11a5:
            r35.save()
            float r6 = r8.chatCallProgress
            float r11 = r6 * r0
            float r6 = r6 * r0
            float r13 = (float) r1
            float r7 = (float) r2
            r9.scale(r11, r6, r13, r7)
        L_0x11b3:
            android.graphics.RectF r6 = r8.rect
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r7 = r1 - r7
            float r7 = (float) r7
            float r11 = (float) r2
            float r11 = r11 - r3
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r13 + r1
            float r13 = (float) r13
            float r4 = (float) r2
            float r4 = r4 + r3
            r6.set(r7, r11, r13, r4)
            android.graphics.RectF r4 = r8.rect
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r7 = (float) r7
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r6, r7, r11)
            android.graphics.RectF r4 = r8.rect
            r6 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r1 - r7
            float r6 = (float) r6
            float r7 = (float) r2
            float r7 = r7 - r5
            r11 = 1077936128(0x40400000, float:3.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r1 - r13
            float r11 = (float) r11
            float r13 = (float) r2
            float r13 = r13 + r5
            r4.set(r6, r7, r11, r13)
            android.graphics.RectF r4 = r8.rect
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r7 = (float) r7
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r6, r7, r11)
            android.graphics.RectF r4 = r8.rect
            r6 = 1077936128(0x40400000, float:3.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r1
            float r6 = (float) r6
            float r7 = (float) r2
            float r7 = r7 - r5
            r11 = 1084227584(0x40a00000, float:5.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r1
            float r11 = (float) r11
            float r13 = (float) r2
            float r13 = r13 + r5
            r4.set(r6, r7, r11, r13)
            android.graphics.RectF r4 = r8.rect
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r7 = (float) r7
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r6, r7, r11)
            float r4 = r8.chatCallProgress
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x123a
            int r4 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x123d
        L_0x123a:
            r35.restore()
        L_0x123d:
            float r4 = r8.innerProgress
            float r6 = (float) r14
            r7 = 1137180672(0x43CLASSNAME, float:400.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r8.innerProgress = r4
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x125a
            r4 = 0
            r8.innerProgress = r4
            int r4 = r8.progressStage
            r6 = 1
            int r4 = r4 + r6
            r8.progressStage = r4
            r6 = 8
            if (r4 < r6) goto L_0x125a
            r4 = 0
            r8.progressStage = r4
        L_0x125a:
            r33 = 1
            boolean r4 = r8.hasCall
            if (r4 == 0) goto L_0x1274
            float r4 = r8.chatCallProgress
            int r6 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r6 >= 0) goto L_0x1288
            float r6 = (float) r14
            r7 = 1125515264(0x43160000, float:150.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r8.chatCallProgress = r4
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 <= 0) goto L_0x1288
            r8.chatCallProgress = r12
            goto L_0x1288
        L_0x1274:
            float r4 = r8.chatCallProgress
            r6 = 0
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 <= 0) goto L_0x1288
            float r7 = (float) r14
            r11 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r11
            float r4 = r4 - r7
            r8.chatCallProgress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x1288
            r8.chatCallProgress = r6
        L_0x1288:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x1292
            r35.restore()
        L_0x1292:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x12b8
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x12b8
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x12b8
            r35.save()
            int r0 = r34.getMeasuredWidth()
            int r1 = r34.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r35.restore()
        L_0x12b8:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x131f
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x12dc
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x12cc
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x12cc
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x12dc
        L_0x12cc:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x12d5
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x12d5
            goto L_0x12dc
        L_0x12d5:
            r0 = 1116733440(0x42900000, float:72.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x12dd
        L_0x12dc:
            r0 = 0
        L_0x12dd:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x1301
            r2 = 0
            int r1 = r34.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r34.getMeasuredWidth()
            int r1 = r1 - r0
            float r5 = (float) r1
            int r1 = r34.getMeasuredHeight()
            int r1 = r1 - r3
            float r6 = (float) r1
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r35
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x131f
        L_0x1301:
            float r2 = (float) r0
            int r1 = r34.getMeasuredHeight()
            r3 = 1
            int r1 = r1 - r3
            float r4 = (float) r1
            int r1 = r34.getMeasuredWidth()
            float r5 = (float) r1
            int r1 = r34.getMeasuredHeight()
            int r1 = r1 - r3
            float r6 = (float) r1
            android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dividerPaint
            r1 = r35
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x131f:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x136f
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1330
            r35.restore()
            goto L_0x136f
        L_0x1330:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            r2 = 0
            r3 = 0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r8.topClip
            float r0 = (float) r0
            float r1 = r8.clipProgress
            float r5 = r0 * r1
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
            int r0 = r34.getMeasuredHeight()
            int r1 = r8.bottomClip
            float r1 = (float) r1
            float r3 = r8.clipProgress
            float r1 = r1 * r3
            int r1 = (int) r1
            int r0 = r0 - r1
            float r3 = (float) r0
            int r0 = r34.getMeasuredWidth()
            float r4 = (float) r0
            int r0 = r34.getMeasuredHeight()
            float r5 = (float) r0
            android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r1 = r35
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x136f:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x137a
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x13a8
        L_0x137a:
            if (r0 == 0) goto L_0x1392
            float r0 = r8.reorderIconProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x13a8
            float r1 = (float) r14
            r2 = 1126825984(0x432a0000, float:170.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.reorderIconProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x138f
            r8.reorderIconProgress = r12
        L_0x138f:
            r33 = 1
            goto L_0x13a8
        L_0x1392:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x13a8
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x13a6
            r8.reorderIconProgress = r1
        L_0x13a6:
            r33 = 1
        L_0x13a8:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x13d9
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1404
            float r2 = (float) r14
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x13c0
            r8.archiveBackgroundProgress = r1
        L_0x13c0:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x13d6
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x13d6:
            r33 = 1
            goto L_0x1404
        L_0x13d9:
            float r0 = r8.archiveBackgroundProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x1404
            float r1 = (float) r14
            r2 = 1130758144(0x43660000, float:230.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x13ec
            r8.archiveBackgroundProgress = r12
        L_0x13ec:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1402
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1402:
            r33 = 1
        L_0x1404:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x141b
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r14
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x1419
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x1419:
            r33 = 1
        L_0x141b:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x144a
            float r0 = r8.currentRevealBounceProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x1434
            float r1 = (float) r14
            r2 = 1126825984(0x432a0000, float:170.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x1434
            r8.currentRevealBounceProgress = r12
            r33 = 1
        L_0x1434:
            float r0 = r8.currentRevealProgress
            int r1 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x146c
            float r1 = (float) r14
            r2 = 1133903872(0x43960000, float:300.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.currentRevealProgress = r0
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x1447
            r8.currentRevealProgress = r12
        L_0x1447:
            r33 = 1
            goto L_0x146c
        L_0x144a:
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x1456
            r1 = 0
            r8.currentRevealBounceProgress = r1
            r33 = 1
            goto L_0x1457
        L_0x1456:
            r1 = 0
        L_0x1457:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x146c
            float r2 = (float) r14
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x146a
            r8.currentRevealProgress = r1
        L_0x146a:
            r33 = 1
        L_0x146c:
            if (r33 == 0) goto L_0x1471
            r34.invalidate()
        L_0x1471:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: private */
    public void createStatusDrawableAnimator(int lastStatusDrawableParams2, int currentStatus) {
        this.statusDrawableProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.statusDrawableAnimator = ofFloat;
        ofFloat.setDuration(220);
        this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animateFromStatusDrawableParams = lastStatusDrawableParams2;
        this.animateToStatusDrawableParams = currentStatus;
        this.statusDrawableAnimator.addUpdateListener(new DialogCell$$ExternalSyntheticLambda0(this));
        this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                int currentStatus = (DialogCell.this.drawClock ? 1 : 0) + (DialogCell.this.drawCheck1 ? 2 : 0) + (DialogCell.this.drawCheck2 ? 4 : 0);
                if (DialogCell.this.animateToStatusDrawableParams != currentStatus) {
                    DialogCell dialogCell = DialogCell.this;
                    dialogCell.createStatusDrawableAnimator(dialogCell.animateToStatusDrawableParams, currentStatus);
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

    /* renamed from: lambda$createStatusDrawableAnimator$1$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m1529xed744e43(ValueAnimator valueAnimator) {
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

    public void onReorderStateChanged(boolean reordering, boolean animated) {
        boolean z = this.drawPin;
        if ((z || !reordering) && this.drawReorder != reordering) {
            this.drawReorder = reordering;
            float f = 1.0f;
            if (animated) {
                if (reordering) {
                    f = 0.0f;
                }
                this.reorderIconProgress = f;
            } else {
                if (!reordering) {
                    f = 0.0f;
                }
                this.reorderIconProgress = f;
            }
            invalidate();
        } else if (!z) {
            this.drawReorder = false;
        }
    }

    public void setSliding(boolean value) {
        this.isSliding = value;
    }

    public void invalidateDrawable(Drawable who) {
        if (who == this.translationDrawable || who == Theme.dialogs_archiveAvatarDrawable) {
            invalidate(who.getBounds());
        } else {
            super.invalidateDrawable(who);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (!isFolderCell() || this.archivedChatsDrawable == null || !SharedConfig.archiveHidden || this.archivedChatsDrawable.pullProgress != 0.0f) {
            info.addAction(16);
            info.addAction(32);
            return;
        }
        info.setVisibleToUser(false);
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        TLRPC.User fromUser;
        super.onPopulateAccessibilityEvent(event);
        StringBuilder sb = new StringBuilder();
        if (this.currentDialogFolderId == 1) {
            sb.append(LocaleController.getString("ArchivedChats", NUM));
            sb.append(". ");
        } else {
            if (this.encryptedChat != null) {
                sb.append(LocaleController.getString("AccDescrSecretChat", NUM));
                sb.append(". ");
            }
            TLRPC.User user2 = this.user;
            if (user2 != null) {
                if (UserObject.isReplyUser(user2)) {
                    sb.append(LocaleController.getString("RepliesTitle", NUM));
                } else {
                    if (this.user.bot) {
                        sb.append(LocaleController.getString("Bot", NUM));
                        sb.append(". ");
                    }
                    if (this.user.self) {
                        sb.append(LocaleController.getString("SavedMessages", NUM));
                    } else {
                        sb.append(ContactsController.formatName(this.user.first_name, this.user.last_name));
                    }
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
            event.setContentDescription(sb.toString());
            return;
        }
        int lastDate = this.lastMessageDate;
        if (this.lastMessageDate == 0) {
            lastDate = messageObject.messageOwner.date;
        }
        String date = LocaleController.formatDateAudio((long) lastDate, true);
        if (this.message.isOut()) {
            sb.append(LocaleController.formatString("AccDescrSentDate", NUM, date));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", NUM, date));
        }
        sb.append(". ");
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (fromUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.message.messageOwner.from_id.user_id))) != null) {
            sb.append(ContactsController.formatName(fromUser.first_name, fromUser.last_name));
            sb.append(". ");
        }
        if (this.encryptedChat == null) {
            sb.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && !TextUtils.isEmpty(this.message.caption)) {
                sb.append(". ");
                sb.append(this.message.caption);
            }
        }
        event.setContentDescription(sb.toString());
    }

    public void setClipProgress(float value) {
        this.clipProgress = value;
        invalidate();
    }

    public float getClipProgress() {
        return this.clipProgress;
    }

    public void setTopClip(int value) {
        this.topClip = value;
    }

    public void setBottomClip(int value) {
        this.bottomClip = value;
    }

    public void setArchivedPullAnimation(PullForegroundDrawable drawable) {
        this.archivedChatsDrawable = drawable;
    }

    public int getCurrentDialogFolderId() {
        return this.currentDialogFolderId;
    }

    public MessageObject getMessage() {
        return this.message;
    }
}
