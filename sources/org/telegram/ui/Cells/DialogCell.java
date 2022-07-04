package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import java.util.List;
import java.util.Stack;
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
import org.telegram.ui.Components.spoilers.SpoilerEffect;
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
    private boolean drawCount2;
    private boolean drawError;
    private boolean drawMention;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawPlay;
    private boolean drawPremium;
    private boolean drawReactionMention;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private int drawScam;
    private boolean drawVerified;
    public boolean drawingForBlur;
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
    int lastSize;
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
    private int reactionMentionCount;
    private int reactionMentionLeft;
    private ValueAnimator reactionsMentionsAnimator;
    /* access modifiers changed from: private */
    public float reactionsMentionsChangeProgress;
    private RectF rect;
    private float reorderIconProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private List<SpoilerEffect> spoilers;
    private Stack<SpoilerEffect> spoilersPool;
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
        this.spoilersPool = new Stack<>();
        this.spoilers = new ArrayList();
        this.drawCount2 = true;
        this.countChangeProgress = 1.0f;
        this.reactionsMentionsChangeProgress = 1.0f;
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
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider2);
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
        this.reactionMentionCount = 0;
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
            int size = (getMeasuredHeight() + getMeasuredWidth()) << 16;
            if (size != this.lastSize) {
                this.lastSize = size;
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

    public boolean getHasUnread() {
        return this.unreadCount != 0 || this.markUnread;
    }

    public boolean getIsMuted() {
        return this.dialogMuted;
    }

    public boolean getIsPinned() {
        return this.drawPin;
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v181, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v180, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v344, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v345, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v346, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v348, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v349, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v350, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r10v172 */
    /* JADX WARNING: type inference failed for: r10v173 */
    /* JADX WARNING: type inference failed for: r10v176 */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x1a90, code lost:
        if (org.telegram.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x1a92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0516, code lost:
        if (r1.chat.kicked == false) goto L_0x0521;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x1a6b, code lost:
        if (org.telegram.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x1a77;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1a8e A[SYNTHETIC, Splitter:B:1003:0x1a8e] */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x1aa4  */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1aa9 A[Catch:{ Exception -> 0x1b8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1032:0x1b00 A[Catch:{ Exception -> 0x1b8c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1035:0x1b0a A[Catch:{ Exception -> 0x1b8c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x1b61 A[Catch:{ Exception -> 0x1b8c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1051:0x1b64 A[Catch:{ Exception -> 0x1b8c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x1b9f  */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x1d13  */
    /* JADX WARNING: Removed duplicated region for block: B:1150:0x1db7 A[Catch:{ Exception -> 0x1de3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1153:0x1dd3 A[Catch:{ Exception -> 0x1de3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1161:0x1def  */
    /* JADX WARNING: Removed duplicated region for block: B:1170:0x1e3a  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0e73  */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x0e78  */
    /* JADX WARNING: Removed duplicated region for block: B:608:0x10a4  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x1155  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x133b  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x134f  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x160c  */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x16cd  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x1718  */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x1738  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x1787  */
    /* JADX WARNING: Removed duplicated region for block: B:862:0x17ac  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x17dd  */
    /* JADX WARNING: Removed duplicated region for block: B:939:0x1985  */
    /* JADX WARNING: Removed duplicated region for block: B:982:0x1a47  */
    /* JADX WARNING: Removed duplicated region for block: B:983:0x1a50  */
    /* JADX WARNING: Removed duplicated region for block: B:987:0x1a69 A[SYNTHETIC, Splitter:B:987:0x1a69] */
    /* JADX WARNING: Removed duplicated region for block: B:995:0x1a7b A[SYNTHETIC, Splitter:B:995:0x1a7b] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r58 = this;
            r1 = r58
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
            r1.drawNameLock = r6
            r1.drawVerified = r6
            r1.drawPremium = r6
            r1.drawScam = r6
            r1.drawPinBackground = r6
            r1.hasMessageThumb = r6
            r14 = 0
            org.telegram.tgnet.TLRPC$User r15 = r1.user
            boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r15)
            if (r15 != 0) goto L_0x00e6
            boolean r15 = r1.useMeForMyMessages
            if (r15 != 0) goto L_0x00e6
            r15 = 1
            goto L_0x00e7
        L_0x00e6:
            r15 = 0
        L_0x00e7:
            r18 = 1
            r4 = -1
            r1.printingStringType = r4
            r4 = -1
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 18
            if (r5 < r6) goto L_0x0109
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x00fb
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x00ff
        L_0x00fb:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x0104
        L_0x00ff:
            java.lang.String r5 = "%2$s: ⁨%1$s⁩"
            r22 = 1
            goto L_0x011e
        L_0x0104:
            java.lang.String r5 = "⁨%s⁩"
            r22 = 0
            goto L_0x011e
        L_0x0109:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0111
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0115
        L_0x0111:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x011a
        L_0x0115:
            java.lang.String r5 = "%2$s: %1$s"
            r22 = 1
            goto L_0x011e
        L_0x011a:
            java.lang.String r5 = "%1$s"
            r22 = 0
        L_0x011e:
            org.telegram.messenger.MessageObject r6 = r1.message
            r24 = r3
            if (r6 == 0) goto L_0x0127
            java.lang.CharSequence r6 = r6.messageText
            goto L_0x0128
        L_0x0127:
            r6 = 0
        L_0x0128:
            boolean r3 = r6 instanceof android.text.Spannable
            if (r3 == 0) goto L_0x0173
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r6)
            r26 = r4
            int r4 = r3.length()
            r27 = r6
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderlineBold> r6 = org.telegram.ui.Components.URLSpanNoUnderlineBold.class
            r28 = r8
            r8 = 0
            java.lang.Object[] r4 = r3.getSpans(r8, r4, r6)
            org.telegram.ui.Components.URLSpanNoUnderlineBold[] r4 = (org.telegram.ui.Components.URLSpanNoUnderlineBold[]) r4
            int r6 = r4.length
            r8 = 0
        L_0x0146:
            if (r8 >= r6) goto L_0x0154
            r29 = r6
            r6 = r4[r8]
            r3.removeSpan(r6)
            int r8 = r8 + 1
            r6 = r29
            goto L_0x0146
        L_0x0154:
            int r4 = r3.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderline> r6 = org.telegram.ui.Components.URLSpanNoUnderline.class
            r8 = 0
            java.lang.Object[] r4 = r3.getSpans(r8, r4, r6)
            org.telegram.ui.Components.URLSpanNoUnderline[] r4 = (org.telegram.ui.Components.URLSpanNoUnderline[]) r4
            int r6 = r4.length
            r8 = 0
        L_0x0163:
            if (r8 >= r6) goto L_0x0171
            r29 = r6
            r6 = r4[r8]
            r3.removeSpan(r6)
            int r8 = r8 + 1
            r6 = r29
            goto L_0x0163
        L_0x0171:
            r6 = r3
            goto L_0x0179
        L_0x0173:
            r26 = r4
            r27 = r6
            r28 = r8
        L_0x0179:
            r1.lastMessageString = r6
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r4 = "%d"
            r27 = 1117257728(0x42980000, float:76.0)
            r29 = 1117519872(0x429CLASSNAME, float:78.0)
            r31 = 1101004800(0x41a00000, float:20.0)
            r32 = 1099956224(0x41900000, float:18.0)
            r8 = 2
            if (r3 == 0) goto L_0x036b
            int r3 = r3.type
            if (r3 != r8) goto L_0x021a
            r3 = 1
            r1.drawNameLock = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x01da
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x019a
            goto L_0x01da
        L_0x019a:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01be
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r1.nameLockLeft = r3
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r3 = r3 + r23
            r1.nameLeft = r3
            goto L_0x024e
        L_0x01be:
            int r3 = r58.getMeasuredWidth()
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r27)
            int r3 = r3 - r23
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r3 = r3 - r23
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x024e
        L_0x01da:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01fd
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.nameLockLeft = r3
            r3 = 1118044160(0x42a40000, float:82.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r3 = r3 + r23
            r1.nameLeft = r3
            goto L_0x024e
        L_0x01fd:
            int r3 = r58.getMeasuredWidth()
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r29)
            int r3 = r3 - r23
            android.graphics.drawable.Drawable r23 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r23 = r23.getIntrinsicWidth()
            int r3 = r3 - r23
            r1.nameLockLeft = r3
            r3 = 1102053376(0x41b00000, float:22.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r8
            goto L_0x024e
        L_0x021a:
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            boolean r3 = r3.verified
            r1.drawVerified = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x023b
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0229
            goto L_0x023b
        L_0x0229:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0234
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r1.nameLeft = r3
            goto L_0x024e
        L_0x0234:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x024e
        L_0x023b:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0246
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.nameLeft = r3
            goto L_0x024e
        L_0x0246:
            r3 = 1102053376(0x41b00000, float:22.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r8
        L_0x024e:
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r8 = 1
            if (r3 != r8) goto L_0x0305
            r3 = 2131626036(0x7f0e0834, float:1.8879297E38)
            java.lang.String r8 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r3 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            boolean r8 = r8.isMedia
            if (r8 == 0) goto L_0x029d
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r23 = r3
            int r3 = r1.paintIndex
            r2 = r8[r3]
            r3 = 1
            java.lang.Object[] r8 = new java.lang.Object[r3]
            org.telegram.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
            r33 = r9
            r9 = 0
            r8[r9] = r3
            java.lang.String r3 = java.lang.String.format(r5, r8)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r3)
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            r24 = r2
            java.lang.String r2 = "chats_attachMessage"
            r8.<init>(r2, r9)
            int r2 = r3.length()
            r34 = r10
            r9 = 33
            r10 = 0
            r3.setSpan(r8, r10, r2, r9)
            r35 = r11
            r2 = r24
            goto L_0x02f0
        L_0x029d:
            r23 = r3
            r33 = r9
            r34 = r10
            r10 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            int r8 = r3.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x02b4
            java.lang.String r3 = r3.substring(r10, r9)
        L_0x02b4:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x02dc
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x02bf
            r8 = 1
            r10 = 0
            goto L_0x02de
        L_0x02bf:
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = 10
            r10 = 32
            java.lang.String r8 = r3.replace(r8, r10)
            r10 = 0
            r9[r10] = r8
            r8 = 1
            r9[r8] = r0
            java.lang.String r9 = java.lang.String.format(r5, r9)
            android.text.SpannableStringBuilder r9 = android.text.SpannableStringBuilder.valueOf(r9)
            r3 = r9
            r35 = r11
            goto L_0x02f0
        L_0x02dc:
            r8 = 1
            r10 = 0
        L_0x02de:
            r35 = r11
            r9 = 2
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r11[r10] = r3
            r11[r8] = r0
            java.lang.String r8 = java.lang.String.format(r5, r11)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r8)
            r3 = r8
        L_0x02f0:
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r9 = r1.paintIndex
            r8 = r8[r9]
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r31)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r8, r9, r10)
            r24 = r23
            goto L_0x031b
        L_0x0305:
            r33 = r9
            r34 = r10
            r35 = r11
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            boolean r8 = r8.isMedia
            if (r8 == 0) goto L_0x031b
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r2 = r8[r9]
        L_0x031b:
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            int r8 = r8.date
            long r8 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            org.telegram.ui.Cells.DialogCell$CustomDialog r9 = r1.customDialog
            int r9 = r9.unread_count
            if (r9 == 0) goto L_0x033f
            r9 = 1
            r1.drawCount = r9
            java.lang.Object[] r10 = new java.lang.Object[r9]
            org.telegram.ui.Cells.DialogCell$CustomDialog r9 = r1.customDialog
            int r9 = r9.unread_count
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r11 = 0
            r10[r11] = r9
            java.lang.String r10 = java.lang.String.format(r4, r10)
            goto L_0x0344
        L_0x033f:
            r11 = 0
            r1.drawCount = r11
            r10 = r34
        L_0x0344:
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            boolean r4 = r4.sent
            if (r4 == 0) goto L_0x0351
            r4 = 1
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r4 = 0
            goto L_0x0356
        L_0x0351:
            r4 = 0
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
        L_0x0356:
            r1.drawClock = r4
            r1.drawError = r4
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            java.lang.String r4 = r4.name
            r46 = r5
            r9 = r8
            r8 = r26
            r11 = r35
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r0
            goto L_0x13bd
        L_0x036b:
            r33 = r9
            r34 = r10
            r35 = r11
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x038c
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x037a
            goto L_0x038c
        L_0x037a:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0385
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r1.nameLeft = r3
            goto L_0x039f
        L_0x0385:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x039f
        L_0x038c:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0397
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.nameLeft = r3
            goto L_0x039f
        L_0x0397:
            r3 = 1102053376(0x41b00000, float:22.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r8
        L_0x039f:
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x042f
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x04ad
            r3 = 1
            r1.drawNameLock = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x03f0
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x03b3
            goto L_0x03f0
        L_0x03b3:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03d6
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r1.nameLockLeft = r3
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 + r8
            r1.nameLeft = r3
            goto L_0x04ad
        L_0x03d6:
            int r3 = r58.getMeasuredWidth()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r27)
            int r3 = r3 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 - r8
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r32)
            r1.nameLeft = r3
            goto L_0x04ad
        L_0x03f0:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0413
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.nameLockLeft = r3
            r3 = 1118044160(0x42a40000, float:82.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 + r8
            r1.nameLeft = r3
            goto L_0x04ad
        L_0x0413:
            int r3 = r58.getMeasuredWidth()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r29)
            int r3 = r3 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 - r8
            r1.nameLockLeft = r3
            r3 = 1102053376(0x41b00000, float:22.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r8
            goto L_0x04ad
        L_0x042f:
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x04ad
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x045a
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x0444
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x04ad
        L_0x0444:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x0453
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x04ad
        L_0x0453:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.verified
            r1.drawVerified = r3
            goto L_0x04ad
        L_0x045a:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x04ad
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x046b
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x0480
        L_0x046b:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x047a
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x0480
        L_0x047a:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.verified
            r1.drawVerified = r3
        L_0x0480:
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r3 = r3.isPremiumUser(r8)
            if (r3 == 0) goto L_0x04aa
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r8 = r3.clientUserId
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            long r10 = r3.id
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x04aa
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            long r8 = r3.id
            r10 = 0
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x04aa
            r3 = 1
            goto L_0x04ab
        L_0x04aa:
            r3 = 0
        L_0x04ab:
            r1.drawPremium = r3
        L_0x04ad:
            int r3 = r1.lastMessageDate
            int r8 = r1.lastMessageDate
            if (r8 != 0) goto L_0x04bb
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x04bb
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r3 = r8.date
        L_0x04bb:
            boolean r8 = r1.isDialogCell
            if (r8 == 0) goto L_0x051e
            int r8 = r1.currentAccount
            org.telegram.messenger.MediaDataController r8 = org.telegram.messenger.MediaDataController.getInstance(r8)
            long r9 = r1.currentDialogId
            r11 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r8.getDraft(r9, r11)
            r1.draftMessage = r8
            if (r8 == 0) goto L_0x04e8
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x04de
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.reply_to_msg_id
            if (r8 == 0) goto L_0x0518
        L_0x04de:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            int r8 = r8.date
            if (r3 <= r8) goto L_0x04e8
            int r8 = r1.unreadCount
            if (r8 != 0) goto L_0x0518
        L_0x04e8:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x050a
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x050a
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.creator
            if (r8 != 0) goto L_0x050a
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r8 = r8.admin_rights
            if (r8 == 0) goto L_0x0518
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r8 = r8.admin_rights
            boolean r8 = r8.post_messages
            if (r8 == 0) goto L_0x0518
        L_0x050a:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x051c
            boolean r8 = r8.left
            if (r8 != 0) goto L_0x0518
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.kicked
            if (r8 == 0) goto L_0x0521
        L_0x0518:
            r8 = 0
            r1.draftMessage = r8
            goto L_0x0521
        L_0x051c:
            r8 = 0
            goto L_0x0521
        L_0x051e:
            r8 = 0
            r1.draftMessage = r8
        L_0x0521:
            if (r13 == 0) goto L_0x05c2
            r1.lastPrintString = r13
            int r8 = r1.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            long r9 = r1.currentDialogId
            r11 = 0
            java.lang.Integer r8 = r8.getPrintingStringType(r9, r11)
            int r8 = r8.intValue()
            r1.printingStringType = r8
            org.telegram.ui.Components.StatusDrawable r8 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r8)
            r9 = 0
            if (r8 == 0) goto L_0x054b
            int r10 = r8.getIntrinsicWidth()
            r11 = 1077936128(0x40400000, float:3.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r9 = r10 + r11
        L_0x054b:
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>()
            r36 = r3
            r11 = 1
            java.lang.String[] r3 = new java.lang.String[r11]
            java.lang.String r20 = "..."
            r21 = 0
            r3[r21] = r20
            r23 = r8
            java.lang.String[] r8 = new java.lang.String[r11]
            java.lang.String r11 = ""
            r8[r21] = r11
            java.lang.CharSequence r13 = android.text.TextUtils.replace(r13, r3, r8)
            int r3 = r1.printingStringType
            r8 = 5
            if (r3 != r8) goto L_0x0577
            java.lang.String r3 = r13.toString()
            java.lang.String r8 = "**oo**"
            int r3 = r3.indexOf(r8)
            goto L_0x0579
        L_0x0577:
            r3 = r26
        L_0x0579:
            if (r3 < 0) goto L_0x0599
            android.text.SpannableStringBuilder r8 = r10.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r11 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r37 = r12
            int r12 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r12 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r12)
            int r12 = r12.getIntrinsicWidth()
            r11.<init>(r12)
            int r12 = r3 + 6
            r38 = r14
            r14 = 0
            r8.setSpan(r11, r3, r12, r14)
            goto L_0x05b1
        L_0x0599:
            r37 = r12
            r38 = r14
            r14 = 0
            java.lang.String r8 = " "
            android.text.SpannableStringBuilder r8 = r10.append(r8)
            android.text.SpannableStringBuilder r8 = r8.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r11 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r11.<init>(r9)
            r12 = 1
            r8.setSpan(r11, r14, r12, r14)
        L_0x05b1:
            r12 = r10
            android.text.TextPaint[] r8 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r11 = r1.paintIndex
            r2 = r8[r11]
            r8 = 0
            r26 = r3
            r46 = r5
            r3 = r8
            r14 = r38
            goto L_0x1169
        L_0x05c2:
            r36 = r3
            r37 = r12
            r38 = r14
            r3 = 0
            r1.lastPrintString = r3
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            r8 = 256(0x100, float:3.59E-43)
            if (r3 == 0) goto L_0x067e
            r3 = 0
            r9 = 2131625536(0x7f0e0640, float:1.8878283E38)
            java.lang.String r10 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r9)
            org.telegram.tgnet.TLRPC$DraftMessage r9 = r1.draftMessage
            java.lang.String r9 = r9.message
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0614
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x060c
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 == 0) goto L_0x05ee
            goto L_0x060c
        L_0x05ee:
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r9 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r1.resourcesProvider
            java.lang.String r11 = "chats_draft"
            r9.<init>(r11, r10)
            int r10 = r0.length()
            r11 = 33
            r12 = 0
            r8.setSpan(r9, r12, r10, r11)
            r12 = r8
            r46 = r5
            r14 = r38
            goto L_0x1169
        L_0x060c:
            java.lang.String r12 = ""
            r46 = r5
            r14 = r38
            goto L_0x1169
        L_0x0614:
            org.telegram.tgnet.TLRPC$DraftMessage r9 = r1.draftMessage
            java.lang.String r9 = r9.message
            int r10 = r9.length()
            r11 = 150(0x96, float:2.1E-43)
            if (r10 <= r11) goto L_0x0625
            r10 = 0
            java.lang.String r9 = r9.substring(r10, r11)
        L_0x0625:
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r9)
            org.telegram.tgnet.TLRPC$DraftMessage r11 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC.DraftMessage) r11, (android.text.Spannable) r10, (int) r8)
            r8 = 2
            java.lang.CharSequence[] r11 = new java.lang.CharSequence[r8]
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r10)
            r12 = 0
            r11[r12] = r8
            r8 = 1
            r11[r8] = r0
            android.text.SpannableStringBuilder r8 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r11)
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0660
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 != 0) goto L_0x0660
            org.telegram.ui.Components.ForegroundColorSpanThemable r11 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider
            java.lang.String r14 = "chats_draft"
            r11.<init>(r14, r12)
            int r12 = r0.length()
            r14 = 1
            int r12 = r12 + r14
            r23 = r3
            r3 = 0
            r14 = 33
            r8.setSpan(r11, r3, r12, r14)
            goto L_0x0662
        L_0x0660:
            r23 = r3
        L_0x0662:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r11 = r1.paintIndex
            r3 = r3[r11]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r12 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r8, r3, r11, r12)
            r12 = r3
            r46 = r5
            r3 = r23
            r14 = r38
            goto L_0x1169
        L_0x067e:
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0699
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r2 = r3[r8]
            r3 = 2131626145(0x7f0e08a1, float:1.8879518E38)
            java.lang.String r8 = "HistoryCleared"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x0699:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 != 0) goto L_0x076c
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x0740
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r2 = r3[r8]
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested
            if (r8 == 0) goto L_0x06be
            r3 = 2131625639(0x7f0e06a7, float:1.8878492E38)
            java.lang.String r8 = "EncryptionProcessing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x06be:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r8 == 0) goto L_0x06df
            r3 = 2131624634(0x7f0e02ba, float:1.8876453E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r10 = 0
            r9[r10] = r8
            java.lang.String r8 = "AwaitingEncryption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r8, r3, r9)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x06df:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r8 == 0) goto L_0x06f4
            r3 = 2131625640(0x7f0e06a8, float:1.8878494E38)
            java.lang.String r8 = "EncryptionRejected"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x06f4:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r8 == 0) goto L_0x0736
            long r8 = r3.admin_id
            int r3 = r1.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r10 = r3.getClientUserId()
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 != 0) goto L_0x0725
            r3 = 2131625628(0x7f0e069c, float:1.887847E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r8)
            r10 = 0
            r9[r10] = r8
            java.lang.String r8 = "EncryptedChatStartedOutgoing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r8, r3, r9)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x0725:
            r3 = 2131625627(0x7f0e069b, float:1.8878467E38)
            java.lang.String r8 = "EncryptedChatStartedIncoming"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x0736:
            r46 = r5
            r3 = r24
            r12 = r37
            r14 = r38
            goto L_0x1169
        L_0x0740:
            int r3 = r1.dialogsType
            r8 = 3
            if (r3 != r8) goto L_0x0762
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x0762
            r3 = 2131628078(0x7f0e102e, float:1.8883439E38)
            java.lang.String r8 = "SavedMessagesInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r15 = 0
            r3 = 0
            r18 = r3
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x0762:
            java.lang.String r12 = ""
            r46 = r5
            r3 = r24
            r14 = r38
            goto L_0x1169
        L_0x076c:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r3.restriction_reason
            java.lang.String r3 = org.telegram.messenger.MessagesController.getRestrictionReason(r3)
            r9 = 0
            r10 = 0
            org.telegram.messenger.MessageObject r11 = r1.message
            long r11 = r11.getFromChatId()
            boolean r14 = org.telegram.messenger.DialogObject.isUserDialog(r11)
            if (r14 == 0) goto L_0x0791
            int r14 = r1.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            java.lang.Long r8 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r9 = r14.getUser(r8)
            goto L_0x07a5
        L_0x0791:
            int r8 = r1.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            r40 = r9
            r14 = r10
            long r9 = -r11
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r10 = r8.getChat(r9)
            r9 = r40
        L_0x07a5:
            r8 = 1
            r1.drawCount2 = r8
            int r8 = r1.dialogsType
            r14 = 2
            if (r8 != r14) goto L_0x0854
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            if (r8 == 0) goto L_0x083c
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x07f5
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x07f5
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r8 = r8.participants_count
            if (r8 == 0) goto L_0x07cf
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r8 = r8.participants_count
            java.lang.String r14 = "Subscribers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r8)
            goto L_0x083e
        L_0x07cf:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            java.lang.String r8 = r8.username
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x07e7
            r8 = 2131624946(0x7f0e03f2, float:1.8877086E38)
            java.lang.String r14 = "ChannelPrivate"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            java.lang.String r8 = r8.toLowerCase()
            goto L_0x083e
        L_0x07e7:
            r8 = 2131624949(0x7f0e03f5, float:1.8877092E38)
            java.lang.String r14 = "ChannelPublic"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            java.lang.String r8 = r8.toLowerCase()
            goto L_0x083e
        L_0x07f5:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r8 = r8.participants_count
            if (r8 == 0) goto L_0x0806
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            int r8 = r8.participants_count
            java.lang.String r14 = "Members"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r8)
            goto L_0x083e
        L_0x0806:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = r8.has_geo
            if (r8 == 0) goto L_0x0816
            r8 = 2131626585(0x7f0e0a59, float:1.888041E38)
            java.lang.String r14 = "MegaLocation"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            goto L_0x083e
        L_0x0816:
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            java.lang.String r8 = r8.username
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x082e
            r8 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.String r14 = "MegaPrivate"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            java.lang.String r8 = r8.toLowerCase()
            goto L_0x083e
        L_0x082e:
            r8 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.String r14 = "MegaPublic"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            java.lang.String r8 = r8.toLowerCase()
            goto L_0x083e
        L_0x083c:
            java.lang.String r8 = ""
        L_0x083e:
            r14 = 0
            r1.drawCount2 = r14
            r14 = 0
            r18 = 0
            r46 = r5
            r44 = r9
            r45 = r10
            r41 = r11
            r43 = r13
            r15 = r14
            r14 = r38
            r12 = r8
            goto L_0x1158
        L_0x0854:
            r14 = 3
            if (r8 != r14) goto L_0x087b
            org.telegram.tgnet.TLRPC$User r8 = r1.user
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r8)
            if (r8 == 0) goto L_0x087b
            r8 = 2131628078(0x7f0e102e, float:1.8883439E38)
            java.lang.String r14 = "SavedMessagesInfo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            r14 = 0
            r18 = 0
            r46 = r5
            r44 = r9
            r45 = r10
            r41 = r11
            r43 = r13
            r15 = r14
            r14 = r38
            r12 = r8
            goto L_0x1158
        L_0x087b:
            boolean r8 = r1.useForceThreeLines
            if (r8 != 0) goto L_0x089d
            boolean r8 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r8 != 0) goto L_0x089d
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x089d
            r8 = 0
            java.lang.CharSequence r14 = r58.formatArchivedDialogNames()
            r46 = r5
            r24 = r8
            r44 = r9
            r45 = r10
            r41 = r11
            r43 = r13
            r12 = r14
            r14 = r38
            goto L_0x1158
        L_0x089d:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r8 == 0) goto L_0x08d4
            org.telegram.tgnet.TLRPC$Chat r8 = r1.chat
            boolean r8 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r8)
            if (r8 == 0) goto L_0x08bb
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x08bb
            java.lang.String r8 = ""
            r15 = 0
            goto L_0x08bc
        L_0x08bb:
            r8 = r6
        L_0x08bc:
            android.text.TextPaint[] r14 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r23 = r8
            int r8 = r1.paintIndex
            r2 = r14[r8]
            r46 = r5
            r44 = r9
            r45 = r10
            r41 = r11
            r43 = r13
            r12 = r23
            r14 = r38
            goto L_0x1158
        L_0x08d4:
            r8 = 1
            boolean r14 = android.text.TextUtils.isEmpty(r3)
            if (r14 == 0) goto L_0x0a13
            int r14 = r1.currentDialogFolderId
            if (r14 != 0) goto L_0x0a13
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r1.encryptedChat
            if (r14 != 0) goto L_0x0a13
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.needDrawBluredPreview()
            if (r14 != 0) goto L_0x0a13
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isPhoto()
            if (r14 != 0) goto L_0x090c
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isNewGif()
            if (r14 != 0) goto L_0x090c
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVideo()
            if (r14 == 0) goto L_0x0904
            goto L_0x090c
        L_0x0904:
            r40 = r8
            r41 = r11
            r43 = r13
            goto L_0x0a19
        L_0x090c:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isWebpage()
            if (r14 == 0) goto L_0x091f
            org.telegram.messenger.MessageObject r14 = r1.message
            org.telegram.tgnet.TLRPC$Message r14 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r14 = r14.media
            org.telegram.tgnet.TLRPC$WebPage r14 = r14.webpage
            java.lang.String r14 = r14.type
            goto L_0x0920
        L_0x091f:
            r14 = 0
        L_0x0920:
            r40 = r8
            java.lang.String r8 = "app"
            boolean r8 = r8.equals(r14)
            if (r8 != 0) goto L_0x0a0c
            java.lang.String r8 = "profile"
            boolean r8 = r8.equals(r14)
            if (r8 != 0) goto L_0x0a0c
            java.lang.String r8 = "article"
            boolean r8 = r8.equals(r14)
            if (r8 != 0) goto L_0x0a0c
            if (r14 == 0) goto L_0x094b
            java.lang.String r8 = "telegram_"
            boolean r8 = r14.startsWith(r8)
            if (r8 != 0) goto L_0x0945
            goto L_0x094b
        L_0x0945:
            r41 = r11
            r43 = r13
            goto L_0x0a19
        L_0x094b:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r41 = r11
            r11 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r11)
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            int r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r11 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r12)
            if (r8 != r11) goto L_0x0966
            r11 = 0
        L_0x0966:
            if (r8 == 0) goto L_0x0a03
            r12 = 1
            r1.hasMessageThumb = r12
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            r1.drawPlay = r12
            java.lang.String r12 = org.telegram.messenger.FileLoader.getAttachFileName(r11)
            r43 = r13
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r13 = r13.mediaExists
            if (r13 != 0) goto L_0x09c3
            int r13 = r1.currentAccount
            org.telegram.messenger.DownloadController r13 = org.telegram.messenger.DownloadController.getInstance(r13)
            r44 = r14
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r13 = r13.canDownloadMedia((org.telegram.messenger.MessageObject) r14)
            if (r13 != 0) goto L_0x09c5
            int r13 = r1.currentAccount
            org.telegram.messenger.FileLoader r13 = org.telegram.messenger.FileLoader.getInstance(r13)
            boolean r13 = r13.isLoadingFile(r12)
            if (r13 == 0) goto L_0x099c
            goto L_0x09c5
        L_0x099c:
            org.telegram.messenger.ImageReceiver r13 = r1.thumbImage
            r46 = 0
            r47 = 0
            org.telegram.messenger.MessageObject r14 = r1.message
            org.telegram.tgnet.TLObject r14 = r14.photoThumbsObject
            org.telegram.messenger.ImageLocation r48 = org.telegram.messenger.ImageLocation.getForObject(r8, r14)
            r14 = 0
            r50 = r14
            android.graphics.drawable.Drawable r50 = (android.graphics.drawable.Drawable) r50
            org.telegram.messenger.MessageObject r14 = r1.message
            r52 = 0
            java.lang.String r49 = "20_20"
            r45 = r13
            r51 = r14
            r45.setImage((org.telegram.messenger.ImageLocation) r46, (java.lang.String) r47, (org.telegram.messenger.ImageLocation) r48, (java.lang.String) r49, (android.graphics.drawable.Drawable) r50, (java.lang.Object) r51, (int) r52)
            r57 = r8
            r56 = r11
            r55 = r12
            goto L_0x0a01
        L_0x09c3:
            r44 = r14
        L_0x09c5:
            org.telegram.messenger.MessageObject r13 = r1.message
            int r13 = r13.type
            r14 = 1
            if (r13 != r14) goto L_0x09d3
            if (r11 == 0) goto L_0x09d1
            int r13 = r11.size
            goto L_0x09d2
        L_0x09d1:
            r13 = 0
        L_0x09d2:
            goto L_0x09d4
        L_0x09d3:
            r13 = 0
        L_0x09d4:
            org.telegram.messenger.ImageReceiver r14 = r1.thumbImage
            r55 = r12
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLObject r12 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r46 = org.telegram.messenger.ImageLocation.getForObject(r11, r12)
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLObject r12 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r48 = org.telegram.messenger.ImageLocation.getForObject(r8, r12)
            r56 = r11
            long r11 = (long) r13
            r52 = 0
            r57 = r8
            org.telegram.messenger.MessageObject r8 = r1.message
            r54 = 0
            java.lang.String r47 = "20_20"
            java.lang.String r49 = "20_20"
            r45 = r14
            r50 = r11
            r53 = r8
            r45.setImage(r46, r47, r48, r49, r50, r52, r53, r54)
        L_0x0a01:
            r8 = 0
            goto L_0x0a1b
        L_0x0a03:
            r57 = r8
            r56 = r11
            r43 = r13
            r44 = r14
            goto L_0x0a19
        L_0x0a0c:
            r41 = r11
            r43 = r13
            r44 = r14
            goto L_0x0a19
        L_0x0a13:
            r40 = r8
            r41 = r11
            r43 = r13
        L_0x0a19:
            r8 = r40
        L_0x0a1b:
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            if (r11 == 0) goto L_0x0e67
            long r13 = r11.id
            r44 = 0
            int r11 = (r13 > r44 ? 1 : (r13 == r44 ? 0 : -1))
            if (r11 <= 0) goto L_0x0e67
            if (r10 != 0) goto L_0x0e67
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x0a42
            org.telegram.tgnet.TLRPC$Chat r11 = r1.chat
            boolean r11 = org.telegram.messenger.ChatObject.isMegagroup(r11)
            if (r11 == 0) goto L_0x0a3a
            goto L_0x0a42
        L_0x0a3a:
            r46 = r5
            r44 = r9
            r45 = r10
            goto L_0x0e6d
        L_0x0a42:
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0a55
            r11 = 2131626036(0x7f0e0834, float:1.8879297E38)
            java.lang.String r13 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r11 = r0
            goto L_0x0ab2
        L_0x0a55:
            org.telegram.messenger.MessageObject r11 = r1.message
            if (r11 == 0) goto L_0x0a73
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r11.fwd_from
            if (r11 == 0) goto L_0x0a73
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r11.fwd_from
            java.lang.String r11 = r11.from_name
            if (r11 == 0) goto L_0x0a73
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r11.fwd_from
            java.lang.String r0 = r11.from_name
            r11 = r0
            goto L_0x0ab2
        L_0x0a73:
            if (r9 == 0) goto L_0x0aaf
            boolean r11 = r1.useForceThreeLines
            if (r11 != 0) goto L_0x0a8c
            boolean r11 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r11 == 0) goto L_0x0a7e
            goto L_0x0a8c
        L_0x0a7e:
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r9)
            java.lang.String r13 = "\n"
            java.lang.String r14 = ""
            java.lang.String r0 = r11.replace(r13, r14)
            r11 = r0
            goto L_0x0ab2
        L_0x0a8c:
            boolean r11 = org.telegram.messenger.UserObject.isDeleted(r9)
            if (r11 == 0) goto L_0x0a9d
            r11 = 2131626131(0x7f0e0893, float:1.887949E38)
            java.lang.String r13 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r11 = r0
            goto L_0x0ab2
        L_0x0a9d:
            java.lang.String r11 = r9.first_name
            java.lang.String r13 = r9.last_name
            java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r13)
            java.lang.String r13 = "\n"
            java.lang.String r14 = ""
            java.lang.String r0 = r11.replace(r13, r14)
            r11 = r0
            goto L_0x0ab2
        L_0x0aaf:
            java.lang.String r0 = "DELETED"
            r11 = r0
        L_0x0ab2:
            r13 = 0
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0ad3
            r12 = 2
            java.lang.Object[] r0 = new java.lang.Object[r12]
            r12 = 0
            r0[r12] = r3
            r12 = 1
            r0[r12] = r11
            java.lang.String r0 = java.lang.String.format(r5, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r44 = r9
            r45 = r10
            r24 = r13
            r10 = r0
            goto L_0x0d99
        L_0x0ad3:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            if (r0 == 0) goto L_0x0bd8
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            java.lang.String r0 = r0.toString()
            if (r8 != 0) goto L_0x0ae6
            java.lang.String r14 = ""
            goto L_0x0b14
        L_0x0ae6:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVideo()
            if (r14 == 0) goto L_0x0af1
            java.lang.String r14 = "📹 "
            goto L_0x0b14
        L_0x0af1:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isVoice()
            if (r14 == 0) goto L_0x0afc
            java.lang.String r14 = "🎤 "
            goto L_0x0b14
        L_0x0afc:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isMusic()
            if (r14 == 0) goto L_0x0b07
            java.lang.String r14 = "🎧 "
            goto L_0x0b14
        L_0x0b07:
            org.telegram.messenger.MessageObject r14 = r1.message
            boolean r14 = r14.isPhoto()
            if (r14 == 0) goto L_0x0b12
            java.lang.String r14 = "🖼 "
            goto L_0x0b14
        L_0x0b12:
            java.lang.String r14 = "📎 "
        L_0x0b14:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.hasHighlightedWords()
            if (r12 == 0) goto L_0x0b97
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 != 0) goto L_0x0b97
            org.telegram.messenger.MessageObject r12 = r1.message
            java.lang.String r12 = r12.messageTrimmedToHighlight
            r44 = r9
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            if (r9 == 0) goto L_0x0b38
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r12 = r9.messageTrimmedToHighlight
        L_0x0b38:
            int r9 = r58.getMeasuredWidth()
            r23 = 1122893824(0x42ee0000, float:119.0)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r9 = r9 - r23
            if (r22 == 0) goto L_0x0b6c
            boolean r23 = android.text.TextUtils.isEmpty(r11)
            if (r23 != 0) goto L_0x0b5c
            r45 = r10
            float r10 = (float) r9
            r23 = r9
            java.lang.String r9 = r11.toString()
            float r9 = r2.measureText(r9)
            float r10 = r10 - r9
            int r9 = (int) r10
            goto L_0x0b60
        L_0x0b5c:
            r23 = r9
            r45 = r10
        L_0x0b60:
            float r10 = (float) r9
            r23 = r9
            java.lang.String r9 = ": "
            float r9 = r2.measureText(r9)
            float r10 = r10 - r9
            int r9 = (int) r10
            goto L_0x0b70
        L_0x0b6c:
            r23 = r9
            r45 = r10
        L_0x0b70:
            if (r9 <= 0) goto L_0x0b8a
            org.telegram.messenger.MessageObject r10 = r1.message
            java.util.ArrayList<java.lang.String> r10 = r10.highlightedWords
            r24 = r13
            r13 = 0
            java.lang.Object r10 = r10.get(r13)
            java.lang.String r10 = (java.lang.String) r10
            r13 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r10 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r12, r10, r9, r2, r13)
            java.lang.String r12 = r10.toString()
            goto L_0x0b8c
        L_0x0b8a:
            r24 = r13
        L_0x0b8c:
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r14)
            android.text.SpannableStringBuilder r9 = r10.append(r12)
            r0 = r9
            goto L_0x0bd5
        L_0x0b97:
            r44 = r9
            r45 = r10
            r24 = r13
            int r9 = r0.length()
            r10 = 150(0x96, float:2.1E-43)
            if (r9 <= r10) goto L_0x0baa
            r9 = 0
            java.lang.CharSequence r0 = r0.subSequence(r9, r10)
        L_0x0baa:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r0)
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            r12 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r10, r0, r9, r12)
            r10 = 2
            java.lang.CharSequence[] r12 = new java.lang.CharSequence[r10]
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r14)
            java.lang.CharSequence r13 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            android.text.SpannableStringBuilder r10 = r10.append(r13)
            r13 = 0
            r12[r13] = r10
            r10 = 1
            r12[r10] = r11
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r12)
            r0 = r10
        L_0x0bd5:
            r10 = r0
            goto L_0x0d99
        L_0x0bd8:
            r44 = r9
            r45 = r10
            r24 = r13
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0d03
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0d03
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r9 = r1.paintIndex
            r2 = r0[r9]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0c2f
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 18
            if (r9 < r10) goto L_0x0c1d
            r9 = 1
            java.lang.Object[] r10 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Poll r12 = r0.poll
            java.lang.String r12 = r12.question
            r13 = 0
            r10[r13] = r12
            java.lang.String r12 = "📊 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r12, r10)
            goto L_0x0c2d
        L_0x0c1d:
            r9 = 1
            r13 = 0
            java.lang.Object[] r10 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Poll r9 = r0.poll
            java.lang.String r9 = r9.question
            r10[r13] = r9
            java.lang.String r9 = "📊 %s"
            java.lang.String r10 = java.lang.String.format(r9, r10)
        L_0x0c2d:
            goto L_0x0ccb
        L_0x0c2f:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0c6e
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 18
            if (r0 < r9) goto L_0x0CLASSNAME
            r9 = 1
            java.lang.Object[] r0 = new java.lang.Object[r9]
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            org.telegram.tgnet.TLRPC$TL_game r10 = r10.game
            java.lang.String r10 = r10.title
            r12 = 0
            r0[r12] = r10
            java.lang.String r10 = "🎮 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r0)
            goto L_0x0ccb
        L_0x0CLASSNAME:
            r9 = 1
            r12 = 0
            java.lang.Object[] r0 = new java.lang.Object[r9]
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
            org.telegram.tgnet.TLRPC$TL_game r9 = r9.game
            java.lang.String r9 = r9.title
            r0[r12] = r9
            java.lang.String r9 = "🎮 %s"
            java.lang.String r10 = java.lang.String.format(r9, r0)
            goto L_0x0ccb
        L_0x0c6e:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r10 = r0.title
            goto L_0x0ccb
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r0 = r1.message
            int r0 = r0.type
            r9 = 14
            if (r0 != r9) goto L_0x0cc7
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 18
            if (r0 < r9) goto L_0x0cab
            r9 = 2
            java.lang.Object[] r0 = new java.lang.Object[r9]
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.getMusicAuthor()
            r12 = 0
            r0[r12] = r10
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.getMusicTitle()
            r13 = 1
            r0[r13] = r10
            java.lang.String r10 = "🎧 ⁨%s - %s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r0)
            goto L_0x0ccb
        L_0x0cab:
            r9 = 2
            r12 = 0
            r13 = 1
            java.lang.Object[] r0 = new java.lang.Object[r9]
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.getMusicAuthor()
            r0[r12] = r9
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.getMusicTitle()
            r0[r13] = r9
            java.lang.String r9 = "🎧 %s - %s"
            java.lang.String r10 = java.lang.String.format(r9, r0)
            goto L_0x0ccb
        L_0x0cc7:
            java.lang.String r10 = r6.toString()
        L_0x0ccb:
            r0 = 10
            r9 = 32
            java.lang.String r9 = r10.replace(r0, r9)
            r10 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r10]
            r10 = 0
            r0[r10] = r9
            r10 = 1
            r0[r10] = r11
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0cfd }
            java.lang.String r12 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider     // Catch:{ Exception -> 0x0cfd }
            r0.<init>(r12, r13)     // Catch:{ Exception -> 0x0cfd }
            if (r22 == 0) goto L_0x0cf2
            int r12 = r11.length()     // Catch:{ Exception -> 0x0cfd }
            r13 = 2
            int r12 = r12 + r13
            goto L_0x0cf3
        L_0x0cf2:
            r12 = 0
        L_0x0cf3:
            int r13 = r10.length()     // Catch:{ Exception -> 0x0cfd }
            r14 = 33
            r10.setSpan(r0, r12, r13, r14)     // Catch:{ Exception -> 0x0cfd }
            goto L_0x0d01
        L_0x0cfd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0d01:
            goto L_0x0d99
        L_0x0d03:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0d92
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x0d62
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            if (r9 == 0) goto L_0x0d23
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r0 = r9.messageTrimmedToHighlight
        L_0x0d23:
            int r9 = r58.getMeasuredWidth()
            r10 = 1121058816(0x42d20000, float:105.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            if (r22 == 0) goto L_0x0d4a
            boolean r10 = android.text.TextUtils.isEmpty(r11)
            if (r10 != 0) goto L_0x0d41
            float r10 = (float) r9
            java.lang.String r12 = r11.toString()
            float r12 = r2.measureText(r12)
            float r10 = r10 - r12
            int r9 = (int) r10
        L_0x0d41:
            float r10 = (float) r9
            java.lang.String r12 = ": "
            float r12 = r2.measureText(r12)
            float r10 = r10 - r12
            int r9 = (int) r10
        L_0x0d4a:
            if (r9 <= 0) goto L_0x0d61
            org.telegram.messenger.MessageObject r10 = r1.message
            java.util.ArrayList<java.lang.String> r10 = r10.highlightedWords
            r12 = 0
            java.lang.Object r10 = r10.get(r12)
            java.lang.String r10 = (java.lang.String) r10
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r10 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r10, r9, r2, r12)
            java.lang.String r0 = r10.toString()
        L_0x0d61:
            goto L_0x0d73
        L_0x0d62:
            int r9 = r0.length()
            r10 = 150(0x96, float:2.1E-43)
            if (r9 <= r10) goto L_0x0d6f
            r9 = 0
            java.lang.CharSequence r0 = r0.subSequence(r9, r10)
        L_0x0d6f:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0d73:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r0)
            r0 = r9
            org.telegram.messenger.MessageObject r9 = r1.message
            r10 = r0
            android.text.Spannable r10 = (android.text.Spannable) r10
            r12 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r9, (android.text.Spannable) r10, (int) r12)
            r9 = 2
            java.lang.CharSequence[] r10 = new java.lang.CharSequence[r9]
            r9 = 0
            r10[r9] = r0
            r9 = 1
            r10[r9] = r11
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r10)
            r10 = r0
            goto L_0x0d99
        L_0x0d92:
            java.lang.String r0 = ""
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r10 = r0
        L_0x0d99:
            r9 = 0
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0da2
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0dac
        L_0x0da2:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0dcb
            int r0 = r10.length()
            if (r0 <= 0) goto L_0x0dcb
        L_0x0dac:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0dc4 }
            java.lang.String r12 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider     // Catch:{ Exception -> 0x0dc4 }
            r0.<init>(r12, r13)     // Catch:{ Exception -> 0x0dc4 }
            int r12 = r11.length()     // Catch:{ Exception -> 0x0dc4 }
            r13 = 1
            int r12 = r12 + r13
            r9 = r12
            r13 = 33
            r14 = 0
            r10.setSpan(r0, r14, r12, r13)     // Catch:{ Exception -> 0x0dc4 }
            r14 = r9
            goto L_0x0dcd
        L_0x0dc4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r14 = r38
            goto L_0x0dcd
        L_0x0dcb:
            r14 = r38
        L_0x0dcd:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r0 = r0[r12]
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r13 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r10, r0, r12, r13)
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.hasHighlightedWords()
            if (r12 == 0) goto L_0x0df5
            org.telegram.messenger.MessageObject r12 = r1.message
            java.util.ArrayList<java.lang.String> r12 = r12.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider
            java.lang.CharSequence r12 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r13)
            if (r12 == 0) goto L_0x0df5
            r0 = r12
        L_0x0df5:
            boolean r12 = r1.hasMessageThumb
            if (r12 == 0) goto L_0x0e5b
            boolean r12 = r0 instanceof android.text.SpannableStringBuilder
            if (r12 != 0) goto L_0x0e03
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r0)
            r0 = r12
        L_0x0e03:
            r12 = 0
            r13 = r0
            android.text.SpannableStringBuilder r13 = (android.text.SpannableStringBuilder) r13
            r23 = r0
            int r0 = r13.length()
            if (r9 < r0) goto L_0x0e38
            java.lang.String r0 = " "
            r13.append(r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r0 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r39 = r2
            int r2 = r7 + 6
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.<init>(r2)
            int r2 = r13.length()
            r20 = 1
            int r2 = r2 + -1
            r46 = r5
            int r5 = r13.length()
            r40 = r10
            r10 = 33
            r13.setSpan(r0, r2, r5, r10)
            goto L_0x0e56
        L_0x0e38:
            r39 = r2
            r46 = r5
            r40 = r10
            java.lang.String r0 = " "
            r13.insert(r9, r0)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r0 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r2 = r7 + 6
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.<init>(r2)
            int r2 = r9 + 1
            r5 = 33
            r13.setSpan(r0, r9, r2, r5)
        L_0x0e56:
            r24 = r12
            r0 = r23
            goto L_0x0e61
        L_0x0e5b:
            r39 = r2
            r46 = r5
            r40 = r10
        L_0x0e61:
            r12 = r0
            r0 = r11
            r2 = r39
            goto L_0x1158
        L_0x0e67:
            r46 = r5
            r44 = r9
            r45 = r10
        L_0x0e6d:
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 != 0) goto L_0x0e78
            r5 = r3
            r9 = r5
            r5 = 2
            goto L_0x10a0
        L_0x0e78:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0ea5
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r5 == 0) goto L_0x0ea5
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x0ea5
            r5 = 2131624503(0x7f0e0237, float:1.8876188E38)
            java.lang.String r9 = "AttachPhotoExpired"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r9 = r5
            r5 = 2
            goto L_0x10a0
        L_0x0ea5:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x0ed2
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
            if (r5 == 0) goto L_0x0ed2
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x0ed2
            r5 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r9 = "AttachVideoExpired"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r9 = r5
            r5 = 2
            goto L_0x10a0
        L_0x0ed2:
            org.telegram.messenger.MessageObject r5 = r1.message
            java.lang.CharSequence r5 = r5.caption
            if (r5 == 0) goto L_0x0fa2
            if (r8 != 0) goto L_0x0edd
            java.lang.String r5 = ""
            goto L_0x0f0b
        L_0x0edd:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVideo()
            if (r5 == 0) goto L_0x0ee8
            java.lang.String r5 = "📹 "
            goto L_0x0f0b
        L_0x0ee8:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVoice()
            if (r5 == 0) goto L_0x0ef3
            java.lang.String r5 = "🎤 "
            goto L_0x0f0b
        L_0x0ef3:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isMusic()
            if (r5 == 0) goto L_0x0efe
            java.lang.String r5 = "🎧 "
            goto L_0x0f0b
        L_0x0efe:
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isPhoto()
            if (r5 == 0) goto L_0x0var_
            java.lang.String r5 = "🖼 "
            goto L_0x0f0b
        L_0x0var_:
            java.lang.String r5 = "📎 "
        L_0x0f0b:
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x0f7c
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x0f7c
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x0f2d
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x0f2d:
            int r10 = r58.getMeasuredWidth()
            r11 = 1122893824(0x42ee0000, float:119.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            if (r22 == 0) goto L_0x0var_
            boolean r11 = android.text.TextUtils.isEmpty(r0)
            if (r11 != 0) goto L_0x0f4b
            float r11 = (float) r10
            java.lang.String r12 = r0.toString()
            float r12 = r2.measureText(r12)
            float r11 = r11 - r12
            int r10 = (int) r11
        L_0x0f4b:
            float r11 = (float) r10
            java.lang.String r12 = ": "
            float r12 = r2.measureText(r12)
            float r11 = r11 - r12
            int r10 = (int) r11
        L_0x0var_:
            if (r10 <= 0) goto L_0x0f6b
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            r12 = 0
            java.lang.Object r11 = r11.get(r12)
            java.lang.String r11 = (java.lang.String) r11
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r9, r11, r10, r2, r12)
            java.lang.String r9 = r11.toString()
        L_0x0f6b:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r5)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r5 = r9
            goto L_0x0f9e
        L_0x0f7c:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.CharSequence r10 = r10.caption
            r9.<init>(r10)
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            org.telegram.messenger.MessageObject r11 = r1.message
            java.lang.CharSequence r11 = r11.caption
            r12 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r10, r11, r9, r12)
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r5)
            android.text.SpannableStringBuilder r10 = r10.append(r9)
            r5 = r10
        L_0x0f9e:
            r9 = r5
            r5 = 2
            goto L_0x10a0
        L_0x0fa2:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x0fcd
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "📊 "
            r9.append(r10)
            org.telegram.tgnet.TLRPC$Poll r10 = r5.poll
            java.lang.String r10 = r10.question
            r9.append(r10)
            java.lang.String r5 = r9.toString()
            r9 = r5
            r5 = 2
            goto L_0x108a
        L_0x0fcd:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r5 == 0) goto L_0x0ff6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r9 = "🎮 "
            r5.append(r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
            org.telegram.tgnet.TLRPC$TL_game r9 = r9.game
            java.lang.String r9 = r9.title
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            r9 = r5
            r5 = 2
            goto L_0x108a
        L_0x0ff6:
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r5 == 0) goto L_0x100c
            org.telegram.messenger.MessageObject r5 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            java.lang.String r5 = r5.title
            r9 = r5
            r5 = 2
            goto L_0x108a
        L_0x100c:
            org.telegram.messenger.MessageObject r5 = r1.message
            int r5 = r5.type
            r9 = 14
            if (r5 != r9) goto L_0x1030
            r5 = 2
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.getMusicAuthor()
            r11 = 0
            r9[r11] = r10
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.getMusicTitle()
            r11 = 1
            r9[r11] = r10
            java.lang.String r10 = "🎧 %s - %s"
            java.lang.String r9 = java.lang.String.format(r10, r9)
            goto L_0x108a
        L_0x1030:
            r5 = 2
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x1074
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x1074
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x1053
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x1053:
            int r10 = r58.getMeasuredWidth()
            r11 = 1119748096(0x42be0000, float:95.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            r12 = 0
            java.lang.Object r11 = r11.get(r12)
            java.lang.String r11 = (java.lang.String) r11
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r9, r11, r10, r2, r12)
            java.lang.String r9 = r11.toString()
            goto L_0x1081
        L_0x1074:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r6)
            org.telegram.messenger.MessageObject r10 = r1.message
            r11 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r10, (android.text.Spannable) r9, (int) r11)
            r10 = r9
        L_0x1081:
            org.telegram.messenger.MessageObject r10 = r1.message
            java.util.ArrayList<java.lang.String> r10 = r10.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r9, (java.util.ArrayList<java.lang.String>) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
        L_0x108a:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            if (r10 == 0) goto L_0x10a0
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isMediaEmpty()
            if (r10 != 0) goto L_0x10a0
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r11 = r1.paintIndex
            r2 = r10[r11]
        L_0x10a0:
            boolean r10 = r1.hasMessageThumb
            if (r10 == 0) goto L_0x1155
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.hasHighlightedWords()
            if (r10 == 0) goto L_0x10ea
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x10ea
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x10c6
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x10c6:
            int r10 = r58.getMeasuredWidth()
            int r11 = r7 + 95
            int r11 = r11 + 6
            float r11 = (float) r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            r12 = 0
            java.lang.Object r11 = r11.get(r12)
            java.lang.String r11 = (java.lang.String) r11
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r9, r11, r10, r2, r12)
            java.lang.String r9 = r11.toString()
            goto L_0x10fb
        L_0x10ea:
            int r10 = r9.length()
            r11 = 150(0x96, float:2.1E-43)
            if (r10 <= r11) goto L_0x10f7
            r10 = 0
            java.lang.CharSequence r9 = r9.subSequence(r10, r11)
        L_0x10f7:
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
        L_0x10fb:
            boolean r10 = r9 instanceof android.text.SpannableStringBuilder
            if (r10 != 0) goto L_0x1105
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r9)
            r9 = r10
        L_0x1105:
            r10 = 0
            r11 = r9
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r13 = 0
            r11.insert(r13, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r14 = r7 + 6
            float r14 = (float) r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r12.<init>(r14)
            r5 = 1
            r14 = 33
            r11.setSpan(r12, r13, r5, r14)
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r12 = r1.paintIndex
            r5 = r5[r12]
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r12 = 1099431936(0x41880000, float:17.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            org.telegram.messenger.Emoji.replaceEmoji(r11, r5, r14, r13)
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.hasHighlightedWords()
            if (r5 == 0) goto L_0x114f
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r11, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)
            if (r5 == 0) goto L_0x114f
            r9 = r5
            r12 = r9
            r24 = r10
            r14 = r38
            goto L_0x1158
        L_0x114f:
            r12 = r9
            r24 = r10
            r14 = r38
            goto L_0x1158
        L_0x1155:
            r12 = r9
            r14 = r38
        L_0x1158:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x1165
            java.lang.CharSequence r0 = r58.formatArchivedDialogNames()
            r3 = r24
            r13 = r43
            goto L_0x1169
        L_0x1165:
            r3 = r24
            r13 = r43
        L_0x1169:
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x1175
            int r5 = r5.date
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x118f
        L_0x1175:
            int r5 = r1.lastMessageDate
            if (r5 == 0) goto L_0x117f
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x118f
        L_0x117f:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x118d
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.date
            long r8 = (long) r5
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x118f
        L_0x118d:
            r9 = r33
        L_0x118f:
            org.telegram.messenger.MessageObject r5 = r1.message
            if (r5 != 0) goto L_0x11a8
            r4 = 0
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r1.drawClock = r4
            r1.drawCount = r4
            r1.drawMention = r4
            r1.drawReactionMention = r4
            r1.drawError = r4
            r10 = r34
            r11 = r35
            goto L_0x12c8
        L_0x11a8:
            int r8 = r1.currentDialogFolderId
            if (r8 == 0) goto L_0x11f1
            int r5 = r1.unreadCount
            int r8 = r1.mentionCount
            int r10 = r5 + r8
            if (r10 <= 0) goto L_0x11e5
            if (r5 <= r8) goto L_0x11ce
            r10 = 1
            r1.drawCount = r10
            r11 = 0
            r1.drawMention = r11
            java.lang.Object[] r11 = new java.lang.Object[r10]
            int r5 = r5 + r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8 = 0
            r11[r8] = r5
            java.lang.String r4 = java.lang.String.format(r4, r11)
            r10 = r4
            r11 = r35
            goto L_0x11ee
        L_0x11ce:
            r10 = 1
            r11 = 0
            r1.drawCount = r11
            r1.drawMention = r10
            java.lang.Object[] r11 = new java.lang.Object[r10]
            int r5 = r5 + r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8 = 0
            r11[r8] = r5
            java.lang.String r11 = java.lang.String.format(r4, r11)
            r10 = r34
            goto L_0x11ee
        L_0x11e5:
            r8 = 0
            r1.drawCount = r8
            r1.drawMention = r8
            r10 = r34
            r11 = r35
        L_0x11ee:
            r1.drawReactionMention = r8
            goto L_0x124e
        L_0x11f1:
            r8 = 0
            boolean r10 = r1.clearingDialog
            if (r10 == 0) goto L_0x11ff
            r1.drawCount = r8
            r4 = 0
            r15 = r4
            r10 = r34
            r4 = 1
            r5 = 0
            goto L_0x1238
        L_0x11ff:
            int r8 = r1.unreadCount
            if (r8 == 0) goto L_0x1227
            r10 = 1
            if (r8 != r10) goto L_0x1212
            int r10 = r1.mentionCount
            if (r8 != r10) goto L_0x1212
            if (r5 == 0) goto L_0x1212
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            boolean r5 = r5.mentioned
            if (r5 != 0) goto L_0x1227
        L_0x1212:
            r5 = 1
            r1.drawCount = r5
            java.lang.Object[] r8 = new java.lang.Object[r5]
            int r5 = r1.unreadCount
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r10 = 0
            r8[r10] = r5
            java.lang.String r10 = java.lang.String.format(r4, r8)
            r4 = 1
            r5 = 0
            goto L_0x1238
        L_0x1227:
            boolean r4 = r1.markUnread
            if (r4 == 0) goto L_0x1232
            r4 = 1
            r1.drawCount = r4
            java.lang.String r10 = ""
            r5 = 0
            goto L_0x1238
        L_0x1232:
            r4 = 1
            r5 = 0
            r1.drawCount = r5
            r10 = r34
        L_0x1238:
            int r8 = r1.mentionCount
            if (r8 == 0) goto L_0x1241
            r1.drawMention = r4
            java.lang.String r11 = "@"
            goto L_0x1245
        L_0x1241:
            r1.drawMention = r5
            r11 = r35
        L_0x1245:
            int r8 = r1.reactionMentionCount
            if (r8 <= 0) goto L_0x124c
            r1.drawReactionMention = r4
            goto L_0x124e
        L_0x124c:
            r1.drawReactionMention = r5
        L_0x124e:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isOut()
            if (r4 == 0) goto L_0x12bf
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            if (r4 != 0) goto L_0x12bf
            if (r15 == 0) goto L_0x12bf
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r4 != 0) goto L_0x12bf
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isSending()
            if (r4 == 0) goto L_0x1279
            r4 = 0
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r5 = 1
            r1.drawClock = r5
            r1.drawError = r4
            goto L_0x12c8
        L_0x1279:
            r4 = 0
            org.telegram.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isSendError()
            if (r5 == 0) goto L_0x1290
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r1.drawClock = r4
            r5 = 1
            r1.drawError = r5
            r1.drawCount = r4
            r1.drawMention = r4
            goto L_0x12c8
        L_0x1290:
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isSent()
            if (r4 == 0) goto L_0x12bd
            org.telegram.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isUnread()
            if (r4 == 0) goto L_0x12b1
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x12af
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x12af
            goto L_0x12b1
        L_0x12af:
            r4 = 0
            goto L_0x12b2
        L_0x12b1:
            r4 = 1
        L_0x12b2:
            r1.drawCheck1 = r4
            r4 = 1
            r1.drawCheck2 = r4
            r4 = 0
            r1.drawClock = r4
            r1.drawError = r4
            goto L_0x12c8
        L_0x12bd:
            r4 = 0
            goto L_0x12c8
        L_0x12bf:
            r4 = 0
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r1.drawClock = r4
            r1.drawError = r4
        L_0x12c8:
            r1.promoDialog = r4
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r5 = r1.dialogsType
            if (r5 != 0) goto L_0x1332
            r5 = r2
            r8 = r3
            long r2 = r1.currentDialogId
            r23 = r5
            r5 = 1
            boolean r2 = r4.isPromoDialog(r2, r5)
            if (r2 == 0) goto L_0x1335
            r1.drawPinBackground = r5
            r1.promoDialog = r5
            int r2 = r4.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x12f6
            r2 = 2131628791(0x7f0e12f7, float:1.8884885E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = r12
            goto L_0x1337
        L_0x12f6:
            int r2 = r4.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x1335
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r4.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((java.lang.String) r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x1322
            r3 = 2131627762(0x7f0e0ef2, float:1.8882798E38)
            java.lang.String r5 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r3)
        L_0x1322:
            java.lang.String r3 = r4.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1330
            java.lang.String r3 = r4.promoPsaMessage
            r5 = 0
            r1.hasMessageThumb = r5
            goto L_0x1337
        L_0x1330:
            r3 = r12
            goto L_0x1337
        L_0x1332:
            r23 = r2
            r8 = r3
        L_0x1335:
            r2 = r9
            r3 = r12
        L_0x1337:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x134f
            r5 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r9 = "ArchivedChats"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r9 = r2
            r4 = r3
            r24 = r8
            r3 = r23
            r8 = r26
            r2 = r0
            goto L_0x13bd
        L_0x134f:
            org.telegram.tgnet.TLRPC$Chat r5 = r1.chat
            if (r5 == 0) goto L_0x1356
            java.lang.String r5 = r5.title
            goto L_0x139b
        L_0x1356:
            org.telegram.tgnet.TLRPC$User r5 = r1.user
            if (r5 == 0) goto L_0x1399
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r5)
            if (r5 == 0) goto L_0x136a
            r5 = 2131627920(0x7f0e0var_, float:1.8883118E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            goto L_0x139b
        L_0x136a:
            org.telegram.tgnet.TLRPC$User r5 = r1.user
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r5)
            if (r5 == 0) goto L_0x1392
            boolean r5 = r1.useMeForMyMessages
            if (r5 == 0) goto L_0x1380
            r5 = 2131626036(0x7f0e0834, float:1.8879297E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            goto L_0x139b
        L_0x1380:
            int r5 = r1.dialogsType
            r9 = 3
            if (r5 != r9) goto L_0x1388
            r5 = 1
            r1.drawPinBackground = r5
        L_0x1388:
            r5 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            goto L_0x139b
        L_0x1392:
            org.telegram.tgnet.TLRPC$User r5 = r1.user
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            goto L_0x139b
        L_0x1399:
            r5 = r28
        L_0x139b:
            int r9 = r5.length()
            if (r9 != 0) goto L_0x13b4
            r9 = 2131626131(0x7f0e0893, float:1.887949E38)
            java.lang.String r12 = "HiddenName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r9 = r2
            r4 = r3
            r24 = r8
            r3 = r23
            r8 = r26
            r2 = r0
            goto L_0x13bd
        L_0x13b4:
            r9 = r2
            r4 = r3
            r24 = r8
            r3 = r23
            r8 = r26
            r2 = r0
        L_0x13bd:
            if (r18 == 0) goto L_0x1401
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r9)
            r23 = r13
            double r12 = (double) r0
            double r12 = java.lang.Math.ceil(r12)
            int r0 = (int) r12
            android.text.StaticLayout r12 = new android.text.StaticLayout
            android.text.TextPaint r35 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            r33 = r12
            r34 = r9
            r36 = r0
            r33.<init>(r34, r35, r36, r37, r38, r39, r40)
            r1.timeLayout = r12
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 != 0) goto L_0x13f7
            int r12 = r58.getMeasuredWidth()
            r13 = 1097859072(0x41700000, float:15.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r12 = r12 - r0
            r1.timeLeft = r12
            goto L_0x13ff
        L_0x13f7:
            r13 = 1097859072(0x41700000, float:15.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r1.timeLeft = r12
        L_0x13ff:
            r12 = r0
            goto L_0x140b
        L_0x1401:
            r23 = r13
            r0 = 0
            r12 = 0
            r1.timeLayout = r12
            r12 = 0
            r1.timeLeft = r12
            r12 = r0
        L_0x140b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x141f
            int r0 = r58.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r0 = r0 - r13
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r0 = r0 - r13
            int r0 = r0 - r12
            goto L_0x1433
        L_0x141f:
            int r0 = r58.getMeasuredWidth()
            int r13 = r1.nameLeft
            int r0 = r0 - r13
            r13 = 1117388800(0x429a0000, float:77.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r0 = r0 - r13
            int r0 = r0 - r12
            int r13 = r1.nameLeft
            int r13 = r13 + r12
            r1.nameLeft = r13
        L_0x1433:
            boolean r13 = r1.drawNameLock
            if (r13 == 0) goto L_0x1446
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r17 = r17.getIntrinsicWidth()
            int r13 = r13 + r17
            int r0 = r0 - r13
        L_0x1446:
            boolean r13 = r1.drawClock
            r17 = 1084227584(0x40a00000, float:5.0)
            if (r13 == 0) goto L_0x147d
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r13 = r13.getIntrinsicWidth()
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r13 = r13 + r26
            int r0 = r0 - r13
            boolean r26 = org.telegram.messenger.LocaleController.isRTL
            if (r26 != 0) goto L_0x1465
            r26 = r0
            int r0 = r1.timeLeft
            int r0 = r0 - r13
            r1.clockDrawLeft = r0
            goto L_0x1477
        L_0x1465:
            r26 = r0
            int r0 = r1.timeLeft
            int r0 = r0 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r17
            r1.clockDrawLeft = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r13
            r1.nameLeft = r0
        L_0x1477:
            r0 = r26
            r26 = r6
            goto L_0x1500
        L_0x147d:
            boolean r13 = r1.drawCheck2
            if (r13 == 0) goto L_0x14fe
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r13 = r13.getIntrinsicWidth()
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r13 = r13 + r26
            int r0 = r0 - r13
            r26 = r6
            boolean r6 = r1.drawCheck1
            if (r6 == 0) goto L_0x14e3
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r6 = r6.getIntrinsicWidth()
            r28 = 1090519040(0x41000000, float:8.0)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r28)
            int r6 = r6 - r28
            int r0 = r0 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x14b7
            int r6 = r1.timeLeft
            int r6 = r6 - r13
            r1.halfCheckDrawLeft = r6
            r17 = 1085276160(0x40b00000, float:5.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 - r17
            r1.checkDrawLeft = r6
            goto L_0x1500
        L_0x14b7:
            int r6 = r1.timeLeft
            int r6 = r6 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 + r17
            r1.checkDrawLeft = r6
            r17 = 1085276160(0x40b00000, float:5.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 + r17
            r1.halfCheckDrawLeft = r6
            int r6 = r1.nameLeft
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r17 = r17.getIntrinsicWidth()
            int r17 = r13 + r17
            r28 = 1090519040(0x41000000, float:8.0)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r28)
            int r17 = r17 - r28
            int r6 = r6 + r17
            r1.nameLeft = r6
            goto L_0x1500
        L_0x14e3:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x14ed
            int r6 = r1.timeLeft
            int r6 = r6 - r13
            r1.checkDrawLeft1 = r6
            goto L_0x1500
        L_0x14ed:
            int r6 = r1.timeLeft
            int r6 = r6 + r12
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 + r17
            r1.checkDrawLeft1 = r6
            int r6 = r1.nameLeft
            int r6 = r6 + r13
            r1.nameLeft = r6
            goto L_0x1500
        L_0x14fe:
            r26 = r6
        L_0x1500:
            boolean r6 = r1.dialogMuted
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r6 == 0) goto L_0x1529
            boolean r6 = r1.drawVerified
            if (r6 != 0) goto L_0x1529
            int r6 = r1.drawScam
            if (r6 != 0) goto L_0x1529
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r17 = r17.getIntrinsicWidth()
            int r6 = r6 + r17
            int r0 = r0 - r6
            boolean r17 = org.telegram.messenger.LocaleController.isRTL
            if (r17 == 0) goto L_0x1524
            int r13 = r1.nameLeft
            int r13 = r13 + r6
            r1.nameLeft = r13
        L_0x1524:
            r6 = r0
            r28 = r9
            goto L_0x1595
        L_0x1529:
            boolean r6 = r1.drawVerified
            if (r6 == 0) goto L_0x1548
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r13 = r13 + r6
            int r0 = r0 - r13
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1544
            int r6 = r1.nameLeft
            int r6 = r6 + r13
            r1.nameLeft = r6
        L_0x1544:
            r6 = r0
            r28 = r9
            goto L_0x1595
        L_0x1548:
            boolean r6 = r1.drawPremium
            if (r6 == 0) goto L_0x156b
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.Premium.PremiumGradient r6 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r6 = r6.premiumStarDrawableMini
            int r6 = r6.getIntrinsicWidth()
            int r13 = r13 + r6
            int r0 = r0 - r13
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1567
            int r6 = r1.nameLeft
            int r6 = r6 + r13
            r1.nameLeft = r6
        L_0x1567:
            r6 = r0
            r28 = r9
            goto L_0x1595
        L_0x156b:
            int r6 = r1.drawScam
            if (r6 == 0) goto L_0x1592
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r1.drawScam
            r28 = r9
            r9 = 1
            if (r6 != r9) goto L_0x157f
            org.telegram.ui.Components.ScamDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1581
        L_0x157f:
            org.telegram.ui.Components.ScamDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1581:
            int r6 = r6.getIntrinsicWidth()
            int r13 = r13 + r6
            int r0 = r0 - r13
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1590
            int r6 = r1.nameLeft
            int r6 = r6 + r13
            r1.nameLeft = r6
        L_0x1590:
            r6 = r0
            goto L_0x1595
        L_0x1592:
            r28 = r9
            r6 = r0
        L_0x1595:
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x1602 }
            int r0 = r6 - r0
            if (r0 >= 0) goto L_0x15a0
            r0 = 0
        L_0x15a0:
            r13 = 10
            r9 = 32
            java.lang.String r9 = r5.replace(r13, r9)     // Catch:{ Exception -> 0x1602 }
            android.text.TextPaint[] r13 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1602 }
            r41 = r5
            int r5 = r1.paintIndex     // Catch:{ Exception -> 0x1600 }
            r5 = r13[r5]     // Catch:{ Exception -> 0x1600 }
            float r13 = (float) r0     // Catch:{ Exception -> 0x1600 }
            r42 = r0
            android.text.TextUtils$TruncateAt r0 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1600 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r9, r5, r13, r0)     // Catch:{ Exception -> 0x1600 }
            android.text.TextPaint[] r5 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1600 }
            int r9 = r1.paintIndex     // Catch:{ Exception -> 0x1600 }
            r5 = r5[r9]     // Catch:{ Exception -> 0x1600 }
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()     // Catch:{ Exception -> 0x1600 }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r31)     // Catch:{ Exception -> 0x1600 }
            r13 = 0
            java.lang.CharSequence r5 = org.telegram.messenger.Emoji.replaceEmoji(r0, r5, r9, r13)     // Catch:{ Exception -> 0x1600 }
            r0 = r5
            org.telegram.messenger.MessageObject r5 = r1.message     // Catch:{ Exception -> 0x1600 }
            if (r5 == 0) goto L_0x15e4
            boolean r5 = r5.hasHighlightedWords()     // Catch:{ Exception -> 0x1600 }
            if (r5 == 0) goto L_0x15e4
            org.telegram.messenger.MessageObject r5 = r1.message     // Catch:{ Exception -> 0x1600 }
            java.util.ArrayList<java.lang.String> r5 = r5.highlightedWords     // Catch:{ Exception -> 0x1600 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider     // Catch:{ Exception -> 0x1600 }
            java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)     // Catch:{ Exception -> 0x1600 }
            if (r5 == 0) goto L_0x15e4
            r0 = r5
        L_0x15e4:
            android.text.StaticLayout r5 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1600 }
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x1600 }
            int r13 = r1.paintIndex     // Catch:{ Exception -> 0x1600 }
            r35 = r9[r13]     // Catch:{ Exception -> 0x1600 }
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1600 }
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            r33 = r5
            r34 = r0
            r36 = r6
            r33.<init>(r34, r35, r36, r37, r38, r39, r40)     // Catch:{ Exception -> 0x1600 }
            r1.nameLayout = r5     // Catch:{ Exception -> 0x1600 }
            goto L_0x1608
        L_0x1600:
            r0 = move-exception
            goto L_0x1605
        L_0x1602:
            r0 = move-exception
            r41 = r5
        L_0x1605:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1608:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x16cd
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1618
            r43 = r8
            r27 = r12
            r42 = r15
            goto L_0x16d3
        L_0x1618:
            r0 = 1091567616(0x41100000, float:9.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5 = 1106771968(0x41var_, float:31.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameTop = r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeTop = r5
            r5 = 1109131264(0x421CLASSNAME, float:39.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorTop = r5
            r5 = 1109131264(0x421CLASSNAME, float:39.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.pinTop = r5
            r5 = 1109131264(0x421CLASSNAME, float:39.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.countTop = r5
            r5 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.checkDrawTop = r9
            int r5 = r58.getMeasuredWidth()
            r9 = 1119748096(0x42be0000, float:95.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = r5 - r9
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x167c
            r9 = 1102053376(0x41b00000, float:22.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            int r9 = r58.getMeasuredWidth()
            r13 = 1115684864(0x42800000, float:64.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r9 = r9 - r13
            int r13 = r7 + 11
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r9 - r13
            goto L_0x1691
        L_0x167c:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r27)
            r1.messageNameLeft = r9
            r1.messageLeft = r9
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 1116078080(0x42860000, float:67.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r9
        L_0x1691:
            r19 = r5
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            r27 = r12
            float r12 = (float) r9
            r29 = r9
            float r9 = (float) r0
            r30 = 1113063424(0x42580000, float:54.0)
            r42 = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r15 = (float) r15
            r43 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r30)
            float r8 = (float) r8
            r5.setImageCoords(r12, r9, r15, r8)
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            float r8 = (float) r13
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
            r5.setImageCoords(r8, r9, r12, r15)
            r5 = r0
            r9 = r29
            goto L_0x1783
        L_0x16cd:
            r43 = r8
            r27 = r12
            r42 = r15
        L_0x16d3:
            r0 = 1093664768(0x41300000, float:11.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.messageNameTop = r5
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.timeTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.errorTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.pinTop = r5
            r5 = 1110179840(0x422CLASSNAME, float:43.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.countTop = r5
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.checkDrawTop = r5
            int r5 = r58.getMeasuredWidth()
            r8 = 1119485952(0x42ba0000, float:93.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x1738
            r8 = 1098907648(0x41800000, float:16.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.messageNameLeft = r8
            r1.messageLeft = r8
            int r8 = r58.getMeasuredWidth()
            r9 = 1115947008(0x42840000, float:66.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r8 - r9
            r13 = r9
            r9 = r8
            goto L_0x174f
        L_0x1738:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r29)
            r1.messageNameLeft = r8
            r1.messageLeft = r8
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r8
            r13 = r9
            r9 = r8
        L_0x174f:
            org.telegram.messenger.ImageReceiver r8 = r1.avatarImage
            float r12 = (float) r9
            float r15 = (float) r0
            r19 = 1113587712(0x42600000, float:56.0)
            r29 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            r30 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r9 = (float) r9
            r8.setImageCoords(r12, r15, r5, r9)
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage
            float r8 = (float) r13
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r12 = (float) r12
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            float r15 = (float) r15
            r5.setImageCoords(r8, r9, r12, r15)
            r5 = r0
            r19 = r29
            r9 = r30
        L_0x1783:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x17a8
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x17a0
            int r0 = r58.getMeasuredWidth()
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r8 = r8.getIntrinsicWidth()
            int r0 = r0 - r8
            r8 = 1096810496(0x41600000, float:14.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 - r8
            r1.pinLeft = r0
            goto L_0x17a8
        L_0x17a0:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x17a8:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x17dd
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r19 = r19 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x17c6
            int r8 = r58.getMeasuredWidth()
            r12 = 1107820544(0x42080000, float:34.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r8 = r8 - r12
            r1.errorLeft = r8
            goto L_0x17d8
        L_0x17c6:
            r8 = 1093664768(0x41300000, float:11.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.errorLeft = r8
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x17d8:
            r12 = r9
            r0 = r19
            goto L_0x1983
        L_0x17dd:
            if (r10 != 0) goto L_0x1811
            if (r11 != 0) goto L_0x1811
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x17e6
            goto L_0x1811
        L_0x17e6:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1807
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 + r8
            int r19 = r19 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x1807
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x1807:
            r8 = 0
            r1.drawCount = r8
            r1.drawMention = r8
            r12 = r9
            r0 = r19
            goto L_0x1983
        L_0x1811:
            if (r10 == 0) goto L_0x1875
            r8 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r8 = r8.measureText(r10)
            r12 = r9
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            int r0 = java.lang.Math.max(r0, r8)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r35 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r8 = r1.countWidth
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_CENTER
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            r33 = r0
            r34 = r10
            r36 = r8
            r33.<init>(r34, r35, r36, r37, r38, r39, r40)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r0 = r0 + r8
            int r19 = r19 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x1861
            int r8 = r58.getMeasuredWidth()
            int r9 = r1.countWidth
            int r8 = r8 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r8 = r8 - r9
            r1.countLeft = r8
            goto L_0x1871
        L_0x1861:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.countLeft = r8
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x1871:
            r8 = 1
            r1.drawCount = r8
            goto L_0x1879
        L_0x1875:
            r12 = r9
            r8 = 0
            r1.countWidth = r8
        L_0x1879:
            if (r11 == 0) goto L_0x1901
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x18b1
            r8 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r8 = r8.measureText(r11)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            int r0 = java.lang.Math.max(r0, r8)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r35 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r8 = r1.mentionWidth
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_CENTER
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            r33 = r0
            r34 = r11
            r36 = r8
            r33.<init>(r34, r35, r36, r37, r38, r39, r40)
            r1.mentionLayout = r0
            goto L_0x18b9
        L_0x18b1:
            r8 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.mentionWidth = r0
        L_0x18b9:
            int r0 = r1.mentionWidth
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r0 = r0 + r8
            int r19 = r19 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x18e1
            int r8 = r58.getMeasuredWidth()
            int r9 = r1.mentionWidth
            int r8 = r8 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r8 = r8 - r9
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x18dc
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x18dd
        L_0x18dc:
            r9 = 0
        L_0x18dd:
            int r8 = r8 - r9
            r1.mentionLeft = r8
            goto L_0x18fd
        L_0x18e1:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r31)
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x18ef
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x18f0
        L_0x18ef:
            r9 = 0
        L_0x18f0:
            int r8 = r8 + r9
            r1.mentionLeft = r8
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x18fd:
            r8 = 1
            r1.drawMention = r8
            goto L_0x1904
        L_0x1901:
            r8 = 0
            r1.mentionWidth = r8
        L_0x1904:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x1981
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r19 = r19 - r0
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 != 0) goto L_0x1948
            int r8 = r58.getMeasuredWidth()
            r9 = 1107296256(0x42000000, float:32.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            r1.reactionMentionLeft = r8
            boolean r9 = r1.drawMention
            if (r9 == 0) goto L_0x1933
            int r9 = r1.mentionWidth
            if (r9 == 0) goto L_0x192f
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x1930
        L_0x192f:
            r9 = 0
        L_0x1930:
            int r8 = r8 - r9
            r1.reactionMentionLeft = r8
        L_0x1933:
            boolean r8 = r1.drawCount
            if (r8 == 0) goto L_0x197e
            int r8 = r1.reactionMentionLeft
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x1943
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x1944
        L_0x1943:
            r9 = 0
        L_0x1944:
            int r8 = r8 - r9
            r1.reactionMentionLeft = r8
            goto L_0x197e
        L_0x1948:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.reactionMentionLeft = r8
            boolean r9 = r1.drawMention
            if (r9 == 0) goto L_0x1960
            int r9 = r1.mentionWidth
            if (r9 == 0) goto L_0x195c
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x195d
        L_0x195c:
            r9 = 0
        L_0x195d:
            int r8 = r8 + r9
            r1.reactionMentionLeft = r8
        L_0x1960:
            boolean r8 = r1.drawCount
            if (r8 == 0) goto L_0x1974
            int r8 = r1.reactionMentionLeft
            int r9 = r1.countWidth
            if (r9 == 0) goto L_0x1970
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r32)
            int r9 = r9 + r15
            goto L_0x1971
        L_0x1970:
            r9 = 0
        L_0x1971:
            int r8 = r8 + r9
            r1.reactionMentionLeft = r8
        L_0x1974:
            int r8 = r1.messageLeft
            int r8 = r8 + r0
            r1.messageLeft = r8
            int r8 = r1.messageNameLeft
            int r8 = r8 + r0
            r1.messageNameLeft = r8
        L_0x197e:
            r0 = r19
            goto L_0x1983
        L_0x1981:
            r0 = r19
        L_0x1983:
            if (r24 == 0) goto L_0x19d2
            if (r4 != 0) goto L_0x1989
            java.lang.String r4 = ""
        L_0x1989:
            r8 = r4
            int r9 = r8.length()
            r15 = 150(0x96, float:2.1E-43)
            if (r9 <= r15) goto L_0x1997
            r9 = 0
            java.lang.CharSequence r8 = r8.subSequence(r9, r15)
        L_0x1997:
            boolean r9 = r1.useForceThreeLines
            if (r9 != 0) goto L_0x199f
            boolean r9 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r9 == 0) goto L_0x19a1
        L_0x199f:
            if (r2 == 0) goto L_0x19a6
        L_0x19a1:
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r8)
            goto L_0x19aa
        L_0x19a6:
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r8)
        L_0x19aa:
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r15 = r1.paintIndex
            r9 = r9[r15]
            android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()
            r15 = 1099431936(0x41880000, float:17.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r16 = r4
            r4 = 0
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r15, r4)
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x19d1
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r15 = r1.resourcesProvider
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r9, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r15)
            if (r4 == 0) goto L_0x19d1
            r9 = r4
            goto L_0x19d2
        L_0x19d1:
            r4 = r9
        L_0x19d2:
            r8 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = java.lang.Math.max(r9, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x19e4
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1a3b
        L_0x19e4:
            if (r2 == 0) goto L_0x1a3b
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x19ef
            int r0 = r1.currentDialogFolderDialogsCount
            r9 = 1
            if (r0 != r9) goto L_0x1a3b
        L_0x19ef:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1a21 }
            if (r0 == 0) goto L_0x1a06
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1a21 }
            if (r0 == 0) goto L_0x1a06
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1a21 }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1a21 }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider     // Catch:{ Exception -> 0x1a21 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)     // Catch:{ Exception -> 0x1a21 }
            if (r0 == 0) goto L_0x1a06
            r2 = r0
        L_0x1a06:
            android.text.TextPaint r48 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1a21 }
            android.text.Layout$Alignment r50 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1a21 }
            r51 = 1065353216(0x3var_, float:1.0)
            r52 = 0
            r53 = 0
            android.text.TextUtils$TruncateAt r54 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1a21 }
            r56 = 1
            r47 = r2
            r49 = r8
            r55 = r8
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r47, r48, r49, r50, r51, r52, r53, r54, r55, r56)     // Catch:{ Exception -> 0x1a21 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1a21 }
            goto L_0x1a25
        L_0x1a21:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1a25:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r9 = 1109393408(0x42200000, float:40.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r5
            float r9 = (float) r9
            r0.setImageY(r9)
            goto L_0x1a65
        L_0x1a3b:
            r9 = 0
            r1.messageNameLayout = r9
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1a50
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1a47
            goto L_0x1a50
        L_0x1a47:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1a65
        L_0x1a50:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r9 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r5
            float r9 = (float) r9
            r0.setImageY(r9)
        L_0x1a65:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1b8e }
            if (r0 != 0) goto L_0x1a77
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1a6e }
            if (r0 == 0) goto L_0x1a8a
            goto L_0x1a77
        L_0x1a6e:
            r0 = move-exception
            r16 = r4
            r19 = r5
            r31 = r10
            goto L_0x1b95
        L_0x1a77:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1b8e }
            if (r0 == 0) goto L_0x1a8a
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1a6e }
            r9 = 1
            if (r0 <= r9) goto L_0x1a8a
            r0 = r2
            r2 = 0
            android.text.TextPaint[] r9 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1a6e }
            int r15 = r1.paintIndex     // Catch:{ Exception -> 0x1a6e }
            r9 = r9[r15]     // Catch:{ Exception -> 0x1a6e }
            r3 = r9
            goto L_0x1aa5
        L_0x1a8a:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1b8e }
            if (r0 != 0) goto L_0x1a92
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1a6e }
            if (r0 == 0) goto L_0x1a94
        L_0x1a92:
            if (r2 == 0) goto L_0x1aa4
        L_0x1a94:
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x1a6e }
            int r0 = r8 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1a6e }
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1a6e }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r4, r3, r0, r9)     // Catch:{ Exception -> 0x1a6e }
            goto L_0x1aa5
        L_0x1aa4:
            r0 = r4
        L_0x1aa5:
            boolean r9 = r0 instanceof android.text.Spannable     // Catch:{ Exception -> 0x1b8e }
            if (r9 == 0) goto L_0x1b00
            r9 = r0
            android.text.Spannable r9 = (android.text.Spannable) r9     // Catch:{ Exception -> 0x1b8e }
            int r15 = r9.length()     // Catch:{ Exception -> 0x1b8e }
            r16 = r4
            java.lang.Class<android.text.style.CharacterStyle> r4 = android.text.style.CharacterStyle.class
            r19 = r5
            r5 = 0
            java.lang.Object[] r4 = r9.getSpans(r5, r15, r4)     // Catch:{ Exception -> 0x1af4 }
            android.text.style.CharacterStyle[] r4 = (android.text.style.CharacterStyle[]) r4     // Catch:{ Exception -> 0x1af4 }
            int r5 = r4.length     // Catch:{ Exception -> 0x1af4 }
            r15 = 0
        L_0x1abf:
            if (r15 >= r5) goto L_0x1af1
            r29 = r4[r15]     // Catch:{ Exception -> 0x1af4 }
            r30 = r29
            r29 = r4
            r4 = r30
            r30 = r5
            boolean r5 = r4 instanceof android.text.style.ClickableSpan     // Catch:{ Exception -> 0x1af4 }
            if (r5 != 0) goto L_0x1ae3
            boolean r5 = r4 instanceof android.text.style.StyleSpan     // Catch:{ Exception -> 0x1af4 }
            if (r5 == 0) goto L_0x1ae0
            r5 = r4
            android.text.style.StyleSpan r5 = (android.text.style.StyleSpan) r5     // Catch:{ Exception -> 0x1af4 }
            int r5 = r5.getStyle()     // Catch:{ Exception -> 0x1af4 }
            r31 = r10
            r10 = 1
            if (r5 != r10) goto L_0x1ae8
            goto L_0x1ae5
        L_0x1ae0:
            r31 = r10
            goto L_0x1ae8
        L_0x1ae3:
            r31 = r10
        L_0x1ae5:
            r9.removeSpan(r4)     // Catch:{ Exception -> 0x1b8c }
        L_0x1ae8:
            int r15 = r15 + 1
            r4 = r29
            r5 = r30
            r10 = r31
            goto L_0x1abf
        L_0x1af1:
            r31 = r10
            goto L_0x1b06
        L_0x1af4:
            r0 = move-exception
            r31 = r10
            goto L_0x1b95
        L_0x1af9:
            r0 = move-exception
            r19 = r5
            r31 = r10
            goto L_0x1b95
        L_0x1b00:
            r16 = r4
            r19 = r5
            r31 = r10
        L_0x1b06:
            boolean r4 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1b8c }
            if (r4 != 0) goto L_0x1b43
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1b8c }
            if (r4 == 0) goto L_0x1b0f
            goto L_0x1b43
        L_0x1b0f:
            boolean r4 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1b8c }
            if (r4 == 0) goto L_0x1b2b
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1b8c }
            int r5 = r5 + r7
            int r8 = r8 + r5
            boolean r4 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1b8c }
            if (r4 == 0) goto L_0x1b2b
            int r4 = r1.messageLeft     // Catch:{ Exception -> 0x1b8c }
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x1b8c }
            int r9 = r9 + r7
            int r4 = r4 - r9
            r1.messageLeft = r4     // Catch:{ Exception -> 0x1b8c }
        L_0x1b2b:
            android.text.StaticLayout r4 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1b8c }
            android.text.Layout$Alignment r37 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1b8c }
            r38 = 1065353216(0x3var_, float:1.0)
            r39 = 0
            r40 = 0
            r33 = r4
            r34 = r0
            r35 = r3
            r36 = r8
            r33.<init>(r34, r35, r36, r37, r38, r39, r40)     // Catch:{ Exception -> 0x1b8c }
            r1.messageLayout = r4     // Catch:{ Exception -> 0x1b8c }
            goto L_0x1b76
        L_0x1b43:
            boolean r4 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1b8c }
            if (r4 == 0) goto L_0x1b50
            if (r2 == 0) goto L_0x1b50
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1b8c }
            int r8 = r8 + r5
        L_0x1b50:
            android.text.Layout$Alignment r50 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1b8c }
            r51 = 1065353216(0x3var_, float:1.0)
            r4 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1b8c }
            float r4 = (float) r4     // Catch:{ Exception -> 0x1b8c }
            r53 = 0
            android.text.TextUtils$TruncateAt r54 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1b8c }
            if (r2 == 0) goto L_0x1b64
            r56 = 1
            goto L_0x1b66
        L_0x1b64:
            r56 = 2
        L_0x1b66:
            r47 = r0
            r48 = r3
            r49 = r8
            r52 = r4
            r55 = r8
            android.text.StaticLayout r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r47, r48, r49, r50, r51, r52, r53, r54, r55, r56)     // Catch:{ Exception -> 0x1b8c }
            r1.messageLayout = r4     // Catch:{ Exception -> 0x1b8c }
        L_0x1b76:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilersPool     // Catch:{ Exception -> 0x1b8c }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r5 = r1.spoilers     // Catch:{ Exception -> 0x1b8c }
            r4.addAll(r5)     // Catch:{ Exception -> 0x1b8c }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilers     // Catch:{ Exception -> 0x1b8c }
            r4.clear()     // Catch:{ Exception -> 0x1b8c }
            android.text.StaticLayout r4 = r1.messageLayout     // Catch:{ Exception -> 0x1b8c }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r5 = r1.spoilersPool     // Catch:{ Exception -> 0x1b8c }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r9 = r1.spoilers     // Catch:{ Exception -> 0x1b8c }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r4, r5, r9)     // Catch:{ Exception -> 0x1b8c }
            goto L_0x1b9b
        L_0x1b8c:
            r0 = move-exception
            goto L_0x1b95
        L_0x1b8e:
            r0 = move-exception
            r16 = r4
            r19 = r5
            r31 = r10
        L_0x1b95:
            r4 = 0
            r1.messageLayout = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1b9b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1d13
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1CLASSNAME
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1CLASSNAME
            android.text.StaticLayout r0 = r1.nameLayout
            r4 = 0
            float r0 = r0.getLineLeft(r4)
            android.text.StaticLayout r5 = r1.nameLayout
            float r5 = r5.getLineWidth(r4)
            double r4 = (double) r5
            double r4 = java.lang.Math.ceil(r4)
            boolean r9 = r1.dialogMuted
            if (r9 == 0) goto L_0x1bf1
            boolean r9 = r1.drawVerified
            if (r9 != 0) goto L_0x1bf1
            int r9 = r1.drawScam
            if (r9 != 0) goto L_0x1bf1
            int r9 = r1.nameLeft
            double r9 = (double) r9
            r15 = r2
            r25 = r3
            double r2 = (double) r6
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r2
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r2 = (double) r2
            java.lang.Double.isNaN(r2)
            double r9 = r9 - r2
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r2.getIntrinsicWidth()
            double r2 = (double) r2
            java.lang.Double.isNaN(r2)
            double r9 = r9 - r2
            int r2 = (int) r9
            r1.nameMuteLeft = r2
            goto L_0x1c7d
        L_0x1bf1:
            r15 = r2
            r25 = r3
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x1c1e
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            int r2 = (int) r2
            r1.nameMuteLeft = r2
            goto L_0x1c7d
        L_0x1c1e:
            boolean r2 = r1.drawPremium
            if (r2 == 0) goto L_0x1c4c
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            org.telegram.ui.Components.Premium.PremiumGradient r9 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r9 = r9.premiumStarDrawableMini
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            int r2 = (int) r2
            r1.nameMuteLeft = r2
            goto L_0x1c7d
        L_0x1c4c:
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1c7d
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r9
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            int r9 = r1.drawScam
            r10 = 1
            if (r9 != r10) goto L_0x1c6f
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1CLASSNAME
        L_0x1c6f:
            org.telegram.ui.Components.ScamDrawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1CLASSNAME:
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r2 = r2 - r9
            int r2 = (int) r2
            r1.nameMuteLeft = r2
        L_0x1c7d:
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x1c9a
            double r2 = (double) r6
            int r9 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r9 >= 0) goto L_0x1c9a
            int r2 = r1.nameLeft
            double r2 = (double) r2
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r4
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r9
            int r2 = (int) r2
            r1.nameLeft = r2
            goto L_0x1c9a
        L_0x1CLASSNAME:
            r15 = r2
            r25 = r3
        L_0x1c9a:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1cdc
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1cdc
            r2 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
        L_0x1ca8:
            if (r3 >= r0) goto L_0x1cd2
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            r5 = 0
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x1ccf
            android.text.StaticLayout r5 = r1.messageLayout
            float r5 = r5.getLineWidth(r3)
            double r9 = (double) r5
            double r9 = java.lang.Math.ceil(r9)
            r17 = r4
            double r4 = (double) r8
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r9
            int r4 = (int) r4
            int r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1ca8
        L_0x1ccf:
            r17 = r4
            r2 = 0
        L_0x1cd2:
            r3 = 2147483647(0x7fffffff, float:NaN)
            if (r2 == r3) goto L_0x1cdc
            int r3 = r1.messageLeft
            int r3 = r3 + r2
            r1.messageLeft = r3
        L_0x1cdc:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1da5
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1da5
            android.text.StaticLayout r0 = r1.messageNameLayout
            r2 = 0
            float r0 = r0.getLineLeft(r2)
            r3 = 0
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1da5
            android.text.StaticLayout r3 = r1.messageNameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r8
            int r9 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r9 >= 0) goto L_0x1da5
            int r4 = r1.messageNameLeft
            double r4 = (double) r4
            double r9 = (double) r8
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r9
            int r4 = (int) r4
            r1.messageNameLeft = r4
            goto L_0x1da5
        L_0x1d13:
            r15 = r2
            r25 = r3
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1d6a
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1d6a
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            float r0 = r0.getLineRight(r2)
            float r3 = (float) r6
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x1d4b
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r2)
            double r2 = (double) r3
            double r2 = java.lang.Math.ceil(r2)
            double r4 = (double) r6
            int r9 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r9 >= 0) goto L_0x1d4b
            int r4 = r1.nameLeft
            double r4 = (double) r4
            double r9 = (double) r6
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r2
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r9
            int r4 = (int) r4
            r1.nameLeft = r4
        L_0x1d4b:
            boolean r2 = r1.dialogMuted
            if (r2 != 0) goto L_0x1d5b
            boolean r2 = r1.drawVerified
            if (r2 != 0) goto L_0x1d5b
            boolean r2 = r1.drawPremium
            if (r2 != 0) goto L_0x1d5b
            int r2 = r1.drawScam
            if (r2 == 0) goto L_0x1d6a
        L_0x1d5b:
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            int r2 = (int) r2
            r1.nameMuteLeft = r2
        L_0x1d6a:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1d8d
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1d8d
            r2 = 1325400064(0x4var_, float:2.14748365E9)
            r3 = 0
        L_0x1d77:
            if (r3 >= r0) goto L_0x1d86
            android.text.StaticLayout r4 = r1.messageLayout
            float r4 = r4.getLineLeft(r3)
            float r2 = java.lang.Math.min(r2, r4)
            int r3 = r3 + 1
            goto L_0x1d77
        L_0x1d86:
            int r3 = r1.messageLeft
            float r3 = (float) r3
            float r3 = r3 - r2
            int r3 = (int) r3
            r1.messageLeft = r3
        L_0x1d8d:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1da5
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1da5
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r2 = r1.messageNameLayout
            r3 = 0
            float r2 = r2.getLineLeft(r3)
            float r0 = r0 - r2
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1da5:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1de7
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x1de7
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1de3 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1de3 }
            if (r14 < r0) goto L_0x1db9
            int r14 = r0 + -1
        L_0x1db9:
            android.text.StaticLayout r2 = r1.messageLayout     // Catch:{ Exception -> 0x1de3 }
            float r2 = r2.getPrimaryHorizontal(r14)     // Catch:{ Exception -> 0x1de3 }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1de3 }
            int r4 = r14 + 1
            float r3 = r3.getPrimaryHorizontal(r4)     // Catch:{ Exception -> 0x1de3 }
            float r4 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x1de3 }
            double r4 = (double) r4     // Catch:{ Exception -> 0x1de3 }
            double r4 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x1de3 }
            int r4 = (int) r4     // Catch:{ Exception -> 0x1de3 }
            if (r4 == 0) goto L_0x1dda
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x1de3 }
            int r4 = r4 + r5
        L_0x1dda:
            org.telegram.messenger.ImageReceiver r5 = r1.thumbImage     // Catch:{ Exception -> 0x1de3 }
            int r9 = r1.messageLeft     // Catch:{ Exception -> 0x1de3 }
            int r9 = r9 + r4
            r5.setImageX(r9)     // Catch:{ Exception -> 0x1de3 }
            goto L_0x1de7
        L_0x1de3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1de7:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1e3a
            int r2 = r1.printingStringType
            if (r2 < 0) goto L_0x1e3a
            if (r43 < 0) goto L_0x1e0e
            int r2 = r43 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r2 >= r0) goto L_0x1e0e
            android.text.StaticLayout r0 = r1.messageLayout
            r2 = r43
            float r0 = r0.getPrimaryHorizontal(r2)
            android.text.StaticLayout r3 = r1.messageLayout
            int r4 = r2 + 1
            float r3 = r3.getPrimaryHorizontal(r4)
            goto L_0x1e1e
        L_0x1e0e:
            r2 = r43
            android.text.StaticLayout r0 = r1.messageLayout
            r3 = 0
            float r0 = r0.getPrimaryHorizontal(r3)
            android.text.StaticLayout r3 = r1.messageLayout
            r4 = 1
            float r3 = r3.getPrimaryHorizontal(r4)
        L_0x1e1e:
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x1e2a
            int r4 = r1.messageLeft
            float r4 = (float) r4
            float r4 = r4 + r0
            int r4 = (int) r4
            r1.statusDrawableLeft = r4
            goto L_0x1e3c
        L_0x1e2a:
            int r4 = r1.messageLeft
            float r4 = (float) r4
            float r4 = r4 + r3
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 + r5
            int r4 = (int) r4
            r1.statusDrawableLeft = r4
            goto L_0x1e3c
        L_0x1e3a:
            r2 = r43
        L_0x1e3c:
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
            boolean oldHasReactionsMentions = this.reactionMentionCount != 0;
            boolean oldMarkUnread = this.markUnread;
            if (this.isDialogCell) {
                TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (dialog != null) {
                    this.clearingDialog = MessagesController.getInstance(this.currentAccount).isClearingDialog(dialog.id);
                    MessageObject messageObject3 = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                    this.message = messageObject3;
                    this.lastUnreadState = messageObject3 != null && messageObject3.isUnread();
                    if (dialog instanceof TLRPC.TL_dialogFolder) {
                        this.unreadCount = MessagesStorage.getInstance(this.currentAccount).getArchiveUnreadCount();
                        this.mentionCount = 0;
                        this.reactionMentionCount = 0;
                    } else {
                        this.unreadCount = dialog.unread_count;
                        this.mentionCount = dialog.unread_mentions_count;
                        this.reactionMentionCount = dialog.unread_reactions_count;
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
                } else {
                    this.unreadCount = 0;
                    this.mentionCount = 0;
                    this.reactionMentionCount = 0;
                    this.currentEditDate = 0;
                    this.lastMessageDate = 0;
                    this.clearingDialog = false;
                }
            } else {
                this.drawPin = false;
            }
            if (this.dialogsType == 2) {
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
                        int newReactionCout = 0;
                        if (dialog2 instanceof TLRPC.TL_dialogFolder) {
                            newCount = MessagesStorage.getInstance(this.currentAccount).getArchiveUnreadCount();
                            newMentionCount = 0;
                        } else if (dialog2 != null) {
                            newCount = dialog2.unread_count;
                            newMentionCount = dialog2.unread_mentions_count;
                            newReactionCout = dialog2.unread_reactions_count;
                        } else {
                            newCount = 0;
                            newMentionCount = 0;
                        }
                        if (!(dialog2 == null || (this.unreadCount == newCount && this.markUnread == dialog2.unread_mark && this.mentionCount == newMentionCount && this.reactionMentionCount == newReactionCout))) {
                            this.unreadCount = newCount;
                            this.mentionCount = newMentionCount;
                            this.markUnread = dialog2.unread_mark;
                            this.reactionMentionCount = newReactionCout;
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
                        this.avatarImage.setForUserOrChat(this.user, this.avatarDrawable, (Object) null, true);
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
                if (this.drawCount && this.drawCount2 && this.countLayout != null) {
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
                        int max = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(oldStr)));
                        this.countOldLayout = new StaticLayout(oldSpannableStr, Theme.dialogs_countTextPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationStableLayout = new StaticLayout(stableStr, Theme.dialogs_countTextPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationInLayout = new StaticLayout(newSpannableStr, Theme.dialogs_countTextPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.countOldLayout = this.countLayout;
                    }
                }
                this.countWidthOld = this.countWidth;
                this.countLeftOld = this.countLeft;
                this.countAnimationIncrement = this.unreadCount > oldUnreadCount;
                this.countAnimator.start();
            }
            boolean newHasReactionsMentions = this.reactionMentionCount != 0;
            if (animated && newHasReactionsMentions != oldHasReactionsMentions) {
                ValueAnimator valueAnimator2 = this.reactionsMentionsAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.reactionsMentionsChangeProgress = 0.0f;
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.reactionsMentionsAnimator = ofFloat2;
                ofFloat2.addUpdateListener(new DialogCell$$ExternalSyntheticLambda2(this));
                this.reactionsMentionsAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        float unused = DialogCell.this.reactionsMentionsChangeProgress = 1.0f;
                        DialogCell.this.invalidate();
                    }
                });
                if (newHasReactionsMentions) {
                    this.reactionsMentionsAnimator.setDuration(220);
                    this.reactionsMentionsAnimator.setInterpolator(new OvershootInterpolator());
                } else {
                    this.reactionsMentionsAnimator.setDuration(150);
                    this.reactionsMentionsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                }
                this.reactionsMentionsAnimator.start();
            }
        }
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        if (!animated) {
            this.dialogMutedProgress = this.dialogMuted ? 1.0f : 0.0f;
            ValueAnimator valueAnimator3 = this.countAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
            }
        }
        invalidate();
    }

    /* renamed from: lambda$update$0$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m2791lambda$update$0$orgtelegramuiCellsDialogCell(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$update$1$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m2792lambda$update$1$orgtelegramuiCellsDialogCell(ValueAnimator valueAnimator) {
        this.reactionsMentionsChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
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
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0eba, code lost:
        if (r8.reactionsMentionsChangeProgress != 1.0f) goto L_0x0ebf;
     */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x098d  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x0990  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x099a  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x099d  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x09ae  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x09e7  */
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
            if (r1 == 0) goto L_0x0043
            org.telegram.ui.Components.PullForegroundDrawable r1 = r8.archivedChatsDrawable
            if (r1 == 0) goto L_0x0043
            float r1 = r1.outProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0043
            float r1 = r8.translationX
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0043
            boolean r1 = r8.drawingForBlur
            if (r1 != 0) goto L_0x0042
            r35.save()
            int r1 = r34.getMeasuredWidth()
            int r2 = r34.getMeasuredHeight()
            r9.clipRect(r10, r10, r1, r2)
            org.telegram.ui.Components.PullForegroundDrawable r1 = r8.archivedChatsDrawable
            r1.draw(r9)
            r35.restore()
        L_0x0042:
            return
        L_0x0043:
            long r12 = android.os.SystemClock.elapsedRealtime()
            long r1 = r8.lastUpdateTime
            long r1 = r12 - r1
            r3 = 17
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0055
            r1 = 17
            r14 = r1
            goto L_0x0056
        L_0x0055:
            r14 = r1
        L_0x0056:
            r8.lastUpdateTime = r12
            float r1 = r8.clipProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0084
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r1 == r2) goto L_0x0084
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
        L_0x0084:
            r1 = 0
            float r2 = r8.translationX
            r16 = 1090519040(0x41000000, float:8.0)
            r7 = 4
            r17 = 1073741824(0x40000000, float:2.0)
            r18 = 1082130432(0x40800000, float:4.0)
            r5 = 1
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x00b8
            float r2 = r8.cornerProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x009a
            goto L_0x00b8
        L_0x009a:
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            if (r2 == 0) goto L_0x00b1
            r2.stop()
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r2.setProgress(r11)
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r3 = 0
            r2.setCallback(r3)
            r2 = 0
            r8.translationDrawable = r2
            r8.translationAnimationStarted = r10
        L_0x00b1:
            r30 = r0
            r10 = r1
            r32 = r12
            goto L_0x04e7
        L_0x00b8:
            r35.save()
            int r2 = r8.currentDialogFolderId
            java.lang.String r3 = "chats_archivePinBackground"
            java.lang.String r10 = "chats_archiveBackground"
            if (r2 == 0) goto L_0x0109
            boolean r2 = r8.archiveHidden
            if (r2 == 0) goto L_0x00e8
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r6 = 2131628730(0x7f0e12ba, float:1.888476E38)
            r19 = r6
            java.lang.String r4 = "UnhideFromTop"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r6)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_unpinArchiveDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x00e8:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r4 = 2131626138(0x7f0e089a, float:1.8879504E38)
            r19 = r4
            java.lang.String r6 = "HideOnTop"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_pinArchiveDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x0109:
            boolean r2 = r8.promoDialog
            if (r2 == 0) goto L_0x012e
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r4 = 2131627753(0x7f0e0ee9, float:1.888278E38)
            r19 = r4
            java.lang.String r6 = "PsaHide"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x012e:
            int r2 = r8.folderId
            if (r2 != 0) goto L_0x021d
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            r6 = 3
            if (r4 != r6) goto L_0x0175
            boolean r4 = r8.dialogMuted
            if (r4 == 0) goto L_0x0160
            r4 = 2131628523(0x7f0e11eb, float:1.8884341E38)
            r19 = r4
            java.lang.String r6 = "SwipeUnmute"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnmuteDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x0160:
            r4 = 2131628511(0x7f0e11df, float:1.8884317E38)
            r19 = r4
            java.lang.String r6 = "SwipeMute"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeMuteDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x0175:
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            if (r4 != r7) goto L_0x019a
            r4 = 2131628508(0x7f0e11dc, float:1.888431E38)
            r19 = r4
            java.lang.String r6 = "SwipeDeleteChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r8.resourcesProvider
            java.lang.String r7 = "dialogSwipeRemove"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeDeleteDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
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
            r4 = 2131628510(0x7f0e11de, float:1.8884315E38)
            r19 = r4
            java.lang.String r6 = "SwipeMarkAsUnread"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnreadDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x01c0:
            r4 = 2131628509(0x7f0e11dd, float:1.8884313E38)
            r19 = r4
            java.lang.String r6 = "SwipeMarkAsRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeReadDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x01d5:
            int r4 = r8.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            if (r4 != 0) goto L_0x0209
            boolean r4 = r8.drawPin
            if (r4 == 0) goto L_0x01f5
            r4 = 2131628524(0x7f0e11ec, float:1.8884343E38)
            r19 = r4
            java.lang.String r6 = "SwipeUnpin"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipeUnpinDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x01f5:
            r4 = 2131628512(0x7f0e11e0, float:1.8884319E38)
            r19 = r4
            java.lang.String r6 = "SwipePin"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_swipePinDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x0209:
            r4 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            r19 = r4
            java.lang.String r6 = "Archive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
            goto L_0x023c
        L_0x021d:
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r4 = 2131628721(0x7f0e12b1, float:1.8884743E38)
            r19 = r4
            java.lang.String r6 = "Unarchive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            org.telegram.ui.Components.RLottieDrawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_unarchiveDrawable
            r8.translationDrawable = r6
            r7 = r1
            r6 = r2
            r1 = r19
        L_0x023c:
            boolean r2 = r8.swipeCanceled
            if (r2 == 0) goto L_0x024a
            org.telegram.ui.Components.RLottieDrawable r2 = r8.lastDrawTranslationDrawable
            if (r2 == 0) goto L_0x024a
            r8.translationDrawable = r2
            int r1 = r8.lastDrawSwipeMessageStringId
            r2 = r1
            goto L_0x0251
        L_0x024a:
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            r8.lastDrawTranslationDrawable = r2
            r8.lastDrawSwipeMessageStringId = r1
            r2 = r1
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
            if (r1 >= 0) goto L_0x0308
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
            r30 = r0
            r0 = r2
            r2 = r20
            r20 = r3
            r3 = r22
            r31 = r4
            r4 = r25
            r32 = r12
            r12 = 2
            r13 = r6
            r6 = r24
            r1.drawRect(r2, r3, r4, r5, r6)
            float r1 = r8.currentRevealProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0313
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r1 == 0) goto L_0x02d5
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r10)
            java.lang.String r3 = "Arrow.**"
            r1.setLayerColor(r3, r2)
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r1
        L_0x02d5:
            boolean r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r1 == 0) goto L_0x0313
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r10)
            java.lang.String r3 = "Line 1.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r10)
            java.lang.String r3 = "Line 2.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r10)
            java.lang.String r3 = "Line 3.**"
            r1.setLayerColor(r3, r2)
            org.telegram.ui.Components.RLottieDrawable r1 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r1.commitApplyLayerColors()
            r1 = 0
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r1
            goto L_0x0313
        L_0x0308:
            r30 = r0
            r0 = r2
            r20 = r3
            r31 = r4
            r32 = r12
            r12 = 2
            r13 = r6
        L_0x0313:
            int r1 = r34.getMeasuredWidth()
            r2 = 1110179840(0x422CLASSNAME, float:43.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            org.telegram.ui.Components.RLottieDrawable r2 = r8.translationDrawable
            int r2 = r2.getIntrinsicWidth()
            int r2 = r2 / r12
            int r1 = r1 - r2
            boolean r2 = r8.useForceThreeLines
            if (r2 != 0) goto L_0x0332
            boolean r2 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x032f
            goto L_0x0332
        L_0x032f:
            r2 = 1091567616(0x41100000, float:9.0)
            goto L_0x0334
        L_0x0332:
            r2 = 1094713344(0x41400000, float:12.0)
        L_0x0334:
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
            if (r5 <= 0) goto L_0x03df
            r35.save()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r5 = (float) r5
            float r5 = r11 - r5
            int r6 = r34.getMeasuredWidth()
            float r6 = (float) r6
            int r10 = r34.getMeasuredHeight()
            float r10 = (float) r10
            r12 = 0
            r9.clipRect(r5, r12, r6, r10)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r5.setColor(r13)
            int r5 = r3 * r3
            int r6 = r34.getMeasuredHeight()
            int r6 = r4 - r6
            int r10 = r34.getMeasuredHeight()
            int r10 = r4 - r10
            int r6 = r6 * r10
            int r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.sqrt(r5)
            float r5 = (float) r5
            float r6 = (float) r3
            float r10 = (float) r4
            android.view.animation.AccelerateInterpolator r12 = org.telegram.messenger.AndroidUtilities.accelerateInterpolator
            r19 = r3
            float r3 = r8.currentRevealProgress
            float r3 = r12.getInterpolation(r3)
            float r3 = r3 * r5
            android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawCircle(r6, r10, r3, r12)
            r35.restore()
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored
            if (r3 != 0) goto L_0x03ac
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r10 = "Arrow.**"
            r3.setLayerColor(r10, r6)
            r10 = 1
            org.telegram.ui.ActionBar.Theme.dialogs_archiveDrawableRecolored = r10
            goto L_0x03ad
        L_0x03ac:
            r10 = 1
        L_0x03ad:
            boolean r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored
            if (r3 != 0) goto L_0x03e2
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r12 = "Line 1.**"
            r3.setLayerColor(r12, r6)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r12 = "Line 2.**"
            r3.setLayerColor(r12, r6)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getNonAnimatedColor(r20)
            java.lang.String r12 = "Line 3.**"
            r3.setLayerColor(r12, r6)
            org.telegram.ui.Components.RLottieDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawable
            r3.commitApplyLayerColors()
            org.telegram.ui.ActionBar.Theme.dialogs_hidePsaDrawableRecolored = r10
            goto L_0x03e2
        L_0x03df:
            r19 = r3
            r10 = 1
        L_0x03e2:
            r35.save()
            float r3 = (float) r1
            float r5 = (float) r2
            r9.translate(r3, r5)
            float r3 = r8.currentRevealBounceProgress
            r5 = 0
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x0412
            r12 = 1065353216(0x3var_, float:1.0)
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x0412
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r5 = r8.interpolator
            float r3 = r5.getInterpolation(r3)
            float r3 = r3 + r12
            org.telegram.ui.Components.RLottieDrawable r5 = r8.translationDrawable
            int r5 = r5.getIntrinsicWidth()
            r6 = 2
            int r5 = r5 / r6
            float r5 = (float) r5
            org.telegram.ui.Components.RLottieDrawable r12 = r8.translationDrawable
            int r12 = r12.getIntrinsicHeight()
            int r12 = r12 / r6
            float r6 = (float) r12
            r9.scale(r3, r3, r5, r6)
        L_0x0412:
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            r5 = 0
            setDrawableBounds((android.graphics.drawable.Drawable) r3, (int) r5, (int) r5)
            org.telegram.ui.Components.RLottieDrawable r3 = r8.translationDrawable
            r3.draw(r9)
            r35.restore()
            int r3 = r34.getMeasuredWidth()
            float r3 = (float) r3
            int r5 = r34.getMeasuredHeight()
            float r5 = (float) r5
            r6 = 0
            r9.clipRect(r11, r6, r3, r5)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r5 = r31
            float r3 = r3.measureText(r5)
            r6 = r11
            double r10 = (double) r3
            double r10 = java.lang.Math.ceil(r10)
            int r3 = (int) r10
            int r10 = r8.swipeMessageTextId
            if (r10 != r0) goto L_0x0449
            int r10 = r8.swipeMessageWidth
            int r11 = r34.getMeasuredWidth()
            if (r10 == r11) goto L_0x0496
        L_0x0449:
            r8.swipeMessageTextId = r0
            int r10 = r34.getMeasuredWidth()
            r8.swipeMessageWidth = r10
            android.text.StaticLayout r10 = new android.text.StaticLayout
            android.text.TextPaint r24 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaint
            r11 = 1117782016(0x42a00000, float:80.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r25 = java.lang.Math.min(r11, r3)
            android.text.Layout$Alignment r26 = android.text.Layout.Alignment.ALIGN_CENTER
            r27 = 1065353216(0x3var_, float:1.0)
            r28 = 0
            r29 = 0
            r22 = r10
            r23 = r5
            r22.<init>(r23, r24, r25, r26, r27, r28, r29)
            r8.swipeMessageTextLayout = r10
            int r10 = r10.getLineCount()
            r11 = 1
            if (r10 <= r11) goto L_0x0496
            android.text.StaticLayout r10 = new android.text.StaticLayout
            android.text.TextPaint r24 = org.telegram.ui.ActionBar.Theme.dialogs_archiveTextPaintSmall
            r11 = 1118044160(0x42a40000, float:82.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r25 = java.lang.Math.min(r11, r3)
            android.text.Layout$Alignment r26 = android.text.Layout.Alignment.ALIGN_CENTER
            r27 = 1065353216(0x3var_, float:1.0)
            r28 = 0
            r29 = 0
            r22 = r10
            r23 = r5
            r22.<init>(r23, r24, r25, r26, r27, r28, r29)
            r8.swipeMessageTextLayout = r10
        L_0x0496:
            android.text.StaticLayout r10 = r8.swipeMessageTextLayout
            if (r10 == 0) goto L_0x04e3
            r35.save()
            android.text.StaticLayout r10 = r8.swipeMessageTextLayout
            int r10 = r10.getLineCount()
            r11 = 1
            if (r10 <= r11) goto L_0x04ad
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r10 = -r10
            float r10 = (float) r10
            goto L_0x04ae
        L_0x04ad:
            r10 = 0
        L_0x04ae:
            int r11 = r34.getMeasuredWidth()
            r12 = 1110179840(0x422CLASSNAME, float:43.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 - r12
            float r11 = (float) r11
            android.text.StaticLayout r12 = r8.swipeMessageTextLayout
            int r12 = r12.getWidth()
            float r12 = (float) r12
            float r12 = r12 / r17
            float r11 = r11 - r12
            boolean r12 = r8.useForceThreeLines
            if (r12 != 0) goto L_0x04d0
            boolean r12 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r12 == 0) goto L_0x04cd
            goto L_0x04d0
        L_0x04cd:
            r12 = 1111228416(0x423CLASSNAME, float:47.0)
            goto L_0x04d2
        L_0x04d0:
            r12 = 1112014848(0x42480000, float:50.0)
        L_0x04d2:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r12 = r12 + r10
            r9.translate(r11, r12)
            android.text.StaticLayout r11 = r8.swipeMessageTextLayout
            r11.draw(r9)
            r35.restore()
        L_0x04e3:
            r35.restore()
            r10 = r7
        L_0x04e7:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x04f6
            r35.save()
            float r0 = r8.translationX
            r9.translate(r0, r1)
        L_0x04f6:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r0 = (float) r0
            float r1 = r8.cornerProgress
            float r11 = r0 * r1
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x051a
            android.graphics.RectF r0 = r8.rect
            int r1 = r34.getMeasuredWidth()
            float r1 = (float) r1
            int r2 = r34.getMeasuredHeight()
            float r2 = (float) r2
            r3 = 0
            r0.set(r3, r3, r1, r2)
            android.graphics.RectF r0 = r8.rect
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r9.drawRoundRect(r0, r11, r11, r1)
        L_0x051a:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0553
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0529
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0553
        L_0x0529:
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
            goto L_0x057b
        L_0x0553:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x055b
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x057b
        L_0x055b:
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
        L_0x057b:
            float r0 = r8.translationX
            java.lang.String r12 = "windowBackgroundWhite"
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x058a
            float r0 = r8.cornerProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0612
        L_0x058a:
            r35.save()
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
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
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r11, r11, r1)
            boolean r0 = r8.isSelected
            if (r0 == 0) goto L_0x05c6
            android.graphics.RectF r0 = r8.rect
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_tabletSeletedPaint
            r9.drawRoundRect(r0, r11, r11, r1)
        L_0x05c6:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x05f3
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x05d5
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x05f3
        L_0x05d5:
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
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r11, r11, r1)
            goto L_0x060f
        L_0x05f3:
            boolean r0 = r8.drawPin
            if (r0 != 0) goto L_0x05fb
            boolean r0 = r8.drawPinBackground
            if (r0 == 0) goto L_0x060f
        L_0x05fb:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            java.lang.String r2 = "chats_pinnedOverlay"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r8.rect
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            r9.drawRoundRect(r0, r11, r11, r1)
        L_0x060f:
            r35.restore()
        L_0x0612:
            float r0 = r8.translationX
            r13 = 1125515264(0x43160000, float:150.0)
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0632
            float r0 = r8.cornerProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0647
            float r2 = (float) r14
            float r2 = r2 / r13
            float r0 = r0 + r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x062e
            r8.cornerProgress = r1
        L_0x062e:
            r0 = 1
            r30 = r0
            goto L_0x0647
        L_0x0632:
            float r0 = r8.cornerProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0647
            float r2 = (float) r14
            float r2 = r2 / r13
            float r0 = r0 - r2
            r8.cornerProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0644
            r8.cornerProgress = r1
        L_0x0644:
            r0 = 1
            r30 = r0
        L_0x0647:
            boolean r0 = r8.drawNameLock
            if (r0 == 0) goto L_0x0659
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
        L_0x0659:
            android.text.StaticLayout r0 = r8.nameLayout
            r19 = 1092616192(0x41200000, float:10.0)
            r20 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x06e4
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x067f
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
            goto L_0x06c0
        L_0x067f:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x06a7
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x068d
            int r0 = r0.type
            r1 = 2
            if (r0 != r1) goto L_0x068d
            goto L_0x06a7
        L_0x068d:
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
            goto L_0x06c0
        L_0x06a7:
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
        L_0x06c0:
            r35.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x06d2
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x06cf
            goto L_0x06d2
        L_0x06cf:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x06d4
        L_0x06d2:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x06d4:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r35.restore()
        L_0x06e4:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0700
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0700
            r35.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r35.restore()
        L_0x0700:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x075a
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x071a
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0741
        L_0x071a:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x0730
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_draft"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x0741
        L_0x0730:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessage_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
        L_0x0741:
            r35.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x0753 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0753 }
            goto L_0x0757
        L_0x0753:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0757:
            r35.restore()
        L_0x075a:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x0866
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x079a
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x0780
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
            goto L_0x07b3
        L_0x0780:
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
            goto L_0x07b3
        L_0x079a:
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
        L_0x07b3:
            r35.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0801
            r35.save()     // Catch:{ Exception -> 0x07fc }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x07fc }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r9, r0)     // Catch:{ Exception -> 0x07fc }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x07fc }
            r0.draw(r9)     // Catch:{ Exception -> 0x07fc }
            r35.restore()     // Catch:{ Exception -> 0x07fc }
            r0 = 0
        L_0x07d8:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x07fc }
            int r1 = r1.size()     // Catch:{ Exception -> 0x07fc }
            if (r0 >= r1) goto L_0x07fb
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x07fc }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ Exception -> 0x07fc }
            org.telegram.ui.Components.spoilers.SpoilerEffect r1 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r1     // Catch:{ Exception -> 0x07fc }
            android.text.StaticLayout r2 = r8.messageLayout     // Catch:{ Exception -> 0x07fc }
            android.text.TextPaint r2 = r2.getPaint()     // Catch:{ Exception -> 0x07fc }
            int r2 = r2.getColor()     // Catch:{ Exception -> 0x07fc }
            r1.setColor(r2)     // Catch:{ Exception -> 0x07fc }
            r1.draw(r9)     // Catch:{ Exception -> 0x07fc }
            int r0 = r0 + 1
            goto L_0x07d8
        L_0x07fb:
            goto L_0x0800
        L_0x07fc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0800:
            goto L_0x0806
        L_0x0801:
            android.text.StaticLayout r0 = r8.messageLayout
            r0.draw(r9)
        L_0x0806:
            r35.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x0866
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x0866
            r35.save()
            int r1 = r8.printingStringType
            r2 = 1
            if (r1 == r2) goto L_0x0838
            r2 = 4
            if (r1 != r2) goto L_0x081f
            goto L_0x0838
        L_0x081f:
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
            goto L_0x084d
        L_0x0838:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            r4 = 1
            if (r1 != r4) goto L_0x0847
            r1 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0848
        L_0x0847:
            r4 = 0
        L_0x0848:
            int r3 = r3 + r4
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x084d:
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
        L_0x0866:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x092e
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x0872
            r6 = 2
            goto L_0x0873
        L_0x0872:
            r6 = 0
        L_0x0873:
            int r0 = r0 + r6
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x087a
            r1 = 4
            goto L_0x087b
        L_0x087a:
            r1 = 0
        L_0x087b:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x0889
            if (r1 == r0) goto L_0x0889
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x0889
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x0889:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x088f
            int r0 = r8.animateToStatusDrawableParams
        L_0x088f:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x0895
            r5 = 1
            goto L_0x0896
        L_0x0895:
            r5 = 0
        L_0x0896:
            r22 = r5
            r2 = r0 & 2
            if (r2 == 0) goto L_0x089e
            r5 = 1
            goto L_0x089f
        L_0x089e:
            r5 = 0
        L_0x089f:
            r23 = r5
            r2 = r0 & 4
            if (r2 == 0) goto L_0x08a7
            r5 = 1
            goto L_0x08a8
        L_0x08a7:
            r5 = 0
        L_0x08a8:
            r24 = r5
            if (r1 == 0) goto L_0x090a
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x08b4
            r5 = 1
            goto L_0x08b5
        L_0x08b4:
            r5 = 0
        L_0x08b5:
            r25 = r5
            r2 = r1 & 2
            if (r2 == 0) goto L_0x08bd
            r5 = 1
            goto L_0x08be
        L_0x08bd:
            r5 = 0
        L_0x08be:
            r26 = r5
            r7 = 4
            r1 = r1 & r7
            if (r1 == 0) goto L_0x08c6
            r5 = 1
            goto L_0x08c7
        L_0x08c6:
            r5 = 0
        L_0x08c7:
            r21 = r5
            if (r22 != 0) goto L_0x08ea
            if (r25 != 0) goto L_0x08ea
            if (r21 == 0) goto L_0x08ea
            if (r26 != 0) goto L_0x08ea
            if (r23 == 0) goto L_0x08ea
            if (r24 == 0) goto L_0x08ea
            r6 = 1
            float r5 = r8.statusDrawableProgress
            r1 = r34
            r2 = r35
            r3 = r22
            r4 = r23
            r27 = r5
            r5 = r24
            r7 = r27
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            goto L_0x0909
        L_0x08ea:
            r6 = 0
            float r1 = r8.statusDrawableProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r7 = r2 - r1
            r1 = r34
            r2 = r35
            r3 = r25
            r4 = r26
            r5 = r21
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
            float r7 = r8.statusDrawableProgress
            r3 = r22
            r4 = r23
            r5 = r24
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0909:
            goto L_0x091a
        L_0x090a:
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r34
            r2 = r35
            r3 = r22
            r4 = r23
            r5 = r24
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x091a:
            boolean r1 = r8.drawClock
            boolean r2 = r8.drawCheck1
            if (r2 == 0) goto L_0x0922
            r6 = 2
            goto L_0x0923
        L_0x0922:
            r6 = 0
        L_0x0923:
            int r1 = r1 + r6
            boolean r2 = r8.drawCheck2
            if (r2 == 0) goto L_0x092a
            r7 = 4
            goto L_0x092b
        L_0x092a:
            r7 = 0
        L_0x092b:
            int r1 = r1 + r7
            r8.lastStatusDrawableParams = r1
        L_0x092e:
            int r0 = r8.dialogsType
            r1 = 1132396544(0x437var_, float:255.0)
            r2 = 2
            if (r0 == r2) goto L_0x09ee
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0940
            float r2 = r8.dialogMutedProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x09ee
        L_0x0940:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x09ee
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x09ee
            boolean r2 = r8.drawPremium
            if (r2 != 0) goto L_0x09ee
            if (r0 == 0) goto L_0x0967
            float r2 = r8.dialogMutedProgress
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0967
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0963
            r8.dialogMutedProgress = r3
            goto L_0x0980
        L_0x0963:
            r34.invalidate()
            goto L_0x0980
        L_0x0967:
            if (r0 != 0) goto L_0x0980
            float r0 = r8.dialogMutedProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0980
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r3
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x097d
            r8.dialogMutedProgress = r2
            goto L_0x0980
        L_0x097d:
            r34.invalidate()
        L_0x0980:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0990
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x098d
            goto L_0x0990
        L_0x098d:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0991
        L_0x0990:
            r4 = 0
        L_0x0991:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x099d
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x099f
        L_0x099d:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x099f:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x09e7
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
            goto L_0x0a9b
        L_0x09e7:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a9b
        L_0x09ee:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0a3d
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r4
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a09
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a06
            goto L_0x0a09
        L_0x0a06:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a0b
        L_0x0a09:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x0a0b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r4
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a29
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a26
            goto L_0x0a29
        L_0x0a26:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a2b
        L_0x0a29:
            r3 = 1096286208(0x41580000, float:13.5)
        L_0x0a2b:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a9b
        L_0x0a3d:
            boolean r0 = r8.drawPremium
            if (r0 == 0) goto L_0x0a69
            org.telegram.ui.Components.Premium.PremiumGradient r0 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r0 = r0.premiumStarDrawableMini
            int r2 = r8.nameMuteLeft
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r4
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a5c
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a59
            goto L_0x0a5c
        L_0x0a59:
            r3 = 1098383360(0x41780000, float:15.5)
            goto L_0x0a5e
        L_0x0a5c:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a5e:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            r0.draw(r9)
            goto L_0x0a9a
        L_0x0a69:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a9a
            r2 = 1
            if (r0 != r2) goto L_0x0a73
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a75
        L_0x0a73:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a75:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a83
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a80
            goto L_0x0a83
        L_0x0a80:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a85
        L_0x0a83:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a85:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            r2 = 1
            if (r0 != r2) goto L_0x0a94
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a96
        L_0x0a94:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a96:
            r0.draw(r9)
            goto L_0x0a9b
        L_0x0a9a:
        L_0x0a9b:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0aa6
            float r0 = r8.reorderIconProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0abe
        L_0x0aa6:
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
        L_0x0abe:
            boolean r0 = r8.drawError
            r2 = 1085276160(0x40b00000, float:5.5)
            r3 = 1102577664(0x41b80000, float:23.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0b1b
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            float r6 = r8.reorderIconProgress
            r7 = 1065353216(0x3var_, float:1.0)
            float r6 = r7 - r6
            float r6 = r6 * r1
            int r1 = (int) r6
            r0.setAlpha(r1)
            android.graphics.RectF r0 = r8.rect
            int r1 = r8.errorLeft
            float r6 = (float) r1
            int r7 = r8.errorTop
            float r7 = (float) r7
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 + r20
            float r1 = (float) r1
            int r13 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r13 = r13 + r3
            float r3 = (float) r13
            r0.set(r6, r7, r1, r3)
            android.graphics.RectF r0 = r8.rect
            float r1 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 * r5
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_errorPaint
            r9.drawRoundRect(r0, r1, r3, r5)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            int r1 = r8.errorLeft
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r8.errorTop
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_errorDrawable
            r0.draw(r9)
            goto L_0x0f5c
        L_0x0b1b:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0b23
            boolean r6 = r8.drawMention
            if (r6 == 0) goto L_0x0b27
        L_0x0b23:
            boolean r6 = r8.drawCount2
            if (r6 != 0) goto L_0x0b5a
        L_0x0b27:
            float r6 = r8.countChangeProgress
            r7 = 1065353216(0x3var_, float:1.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x0b5a
            boolean r6 = r8.drawReactionMention
            if (r6 != 0) goto L_0x0b5a
            float r6 = r8.reactionsMentionsChangeProgress
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 == 0) goto L_0x0b3a
            goto L_0x0b5a
        L_0x0b3a:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0f5c
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r2 = r8.reorderIconProgress
            float r2 = r7 - r2
            float r2 = r2 * r1
            int r1 = (int) r2
            r0.setAlpha(r1)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r1 = r8.pinLeft
            int r2 = r8.pinTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r0.draw(r9)
            goto L_0x0f5c
        L_0x0b5a:
            if (r0 == 0) goto L_0x0b60
            boolean r0 = r8.drawCount2
            if (r0 != 0) goto L_0x0b68
        L_0x0b60:
            float r0 = r8.countChangeProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0df4
        L_0x0b68:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0b77
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0b77
            float r6 = r8.countChangeProgress
            r7 = 1065353216(0x3var_, float:1.0)
            float r6 = r7 - r6
            goto L_0x0b79
        L_0x0b77:
            float r6 = r8.countChangeProgress
        L_0x0b79:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0d10
            if (r0 != 0) goto L_0x0b81
            goto L_0x0d10
        L_0x0b81:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b8d
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b8a
            goto L_0x0b8d
        L_0x0b8a:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b8f
        L_0x0b8d:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b8f:
            float r7 = r8.reorderIconProgress
            r13 = 1065353216(0x3var_, float:1.0)
            float r7 = r13 - r7
            float r7 = r7 * r1
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r4 = r8.reorderIconProgress
            float r4 = r13 - r4
            float r4 = r4 * r1
            int r4 = (int) r4
            r7.setAlpha(r4)
            float r4 = r6 * r17
            int r7 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r7 <= 0) goto L_0x0baf
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0baf:
            int r7 = r8.countLeft
            float r7 = (float) r7
            float r7 = r7 * r4
            int r13 = r8.countLeftOld
            float r13 = (float) r13
            r23 = 1065353216(0x3var_, float:1.0)
            float r24 = r23 - r4
            float r13 = r13 * r24
            float r7 = r7 + r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r13 = (float) r13
            float r13 = r7 - r13
            android.graphics.RectF r2 = r8.rect
            int r1 = r8.countTop
            float r1 = (float) r1
            int r5 = r8.countWidth
            float r5 = (float) r5
            float r5 = r5 * r4
            float r5 = r5 + r13
            int r3 = r8.countWidthOld
            float r3 = (float) r3
            r27 = 1065353216(0x3var_, float:1.0)
            float r28 = r27 - r4
            float r3 = r3 * r28
            float r5 = r5 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r5 = r5 + r3
            int r3 = r8.countTop
            r26 = 1102577664(0x41b80000, float:23.0)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r26)
            int r3 = r3 + r27
            float r3 = (float) r3
            r2.set(r13, r1, r5, r3)
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1056964608(0x3var_, float:0.5)
            int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0CLASSNAME
            r2 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r5 = r6 * r17
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r1 = r1 + r3
            goto L_0x0c1d
        L_0x0CLASSNAME:
            r2 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_IN
            r5 = 1056964608(0x3var_, float:0.5)
            float r5 = r6 - r5
            float r5 = r5 * r17
            r27 = 1065353216(0x3var_, float:1.0)
            float r5 = r27 - r5
            float r3 = r3.getInterpolation(r5)
            float r3 = r3 * r2
            float r1 = r1 + r3
        L_0x0c1d:
            r35.save()
            android.graphics.RectF r2 = r8.rect
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerY()
            r9.scale(r1, r1, r2, r3)
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1094189056(0x41380000, float:11.5)
            float r3 = r3 * r5
            float r25 = org.telegram.messenger.AndroidUtilities.density
            r27 = r1
            float r1 = r25 * r5
            r9.drawRoundRect(r2, r3, r1, r0)
            android.text.StaticLayout r1 = r8.countAnimationStableLayout
            if (r1 == 0) goto L_0x0c5a
            r35.save()
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r7, r1)
            android.text.StaticLayout r1 = r8.countAnimationStableLayout
            r1.draw(r9)
            r35.restore()
        L_0x0c5a:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r1 = r1.getAlpha()
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r1
            float r3 = r3 * r4
            int r3 = (int) r3
            r2.setAlpha(r3)
            android.text.StaticLayout r2 = r8.countAnimationInLayout
            if (r2 == 0) goto L_0x0c9b
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0CLASSNAME
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0c7e
        L_0x0CLASSNAME:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
        L_0x0c7e:
            float r2 = (float) r2
            r3 = 1065353216(0x3var_, float:1.0)
            float r5 = r3 - r4
            float r2 = r2 * r5
            int r3 = r8.countTop
            float r3 = (float) r3
            float r2 = r2 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 + r3
            r9.translate(r7, r2)
            android.text.StaticLayout r2 = r8.countAnimationInLayout
            r2.draw(r9)
            r35.restore()
            goto L_0x0ccc
        L_0x0c9b:
            android.text.StaticLayout r2 = r8.countLayout
            if (r2 == 0) goto L_0x0ccc
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0cab
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0cb0
        L_0x0cab:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
        L_0x0cb0:
            float r2 = (float) r2
            r3 = 1065353216(0x3var_, float:1.0)
            float r5 = r3 - r4
            float r2 = r2 * r5
            int r3 = r8.countTop
            float r3 = (float) r3
            float r2 = r2 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 + r3
            r9.translate(r7, r2)
            android.text.StaticLayout r2 = r8.countLayout
            r2.draw(r9)
            r35.restore()
        L_0x0ccc:
            android.text.StaticLayout r2 = r8.countOldLayout
            if (r2 == 0) goto L_0x0d06
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r1
            r5 = 1065353216(0x3var_, float:1.0)
            float r28 = r5 - r4
            float r3 = r3 * r28
            int r3 = (int) r3
            r2.setAlpha(r3)
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0cea
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
            goto L_0x0cee
        L_0x0cea:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
        L_0x0cee:
            float r2 = (float) r2
            float r2 = r2 * r4
            int r3 = r8.countTop
            float r3 = (float) r3
            float r2 = r2 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r2 = r2 + r3
            r9.translate(r7, r2)
            android.text.StaticLayout r2 = r8.countOldLayout
            r2.draw(r9)
            r35.restore()
        L_0x0d06:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r2.setAlpha(r1)
            r35.restore()
            goto L_0x0df4
        L_0x0d10:
            if (r0 != 0) goto L_0x0d13
            goto L_0x0d15
        L_0x0d13:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0d15:
            r0 = r7
            boolean r1 = r8.dialogMuted
            if (r1 != 0) goto L_0x0d22
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x0d1f
            goto L_0x0d22
        L_0x0d1f:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0d24
        L_0x0d22:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0d24:
            float r2 = r8.reorderIconProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r2
            r2 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r2
            int r4 = (int) r4
            r1.setAlpha(r4)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r5 = r8.reorderIconProgress
            float r5 = r3 - r5
            float r5 = r5 * r2
            int r2 = (int) r5
            r4.setAlpha(r2)
            int r2 = r8.countLeft
            r3 = 1085276160(0x40b00000, float:5.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r4
            android.graphics.RectF r3 = r8.rect
            float r4 = (float) r2
            int r5 = r8.countTop
            float r5 = (float) r5
            int r7 = r8.countWidth
            int r7 = r7 + r2
            r13 = 1093664768(0x41300000, float:11.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r7 = r7 + r13
            float r7 = (float) r7
            int r13 = r8.countTop
            r20 = 1102577664(0x41b80000, float:23.0)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r13 = r13 + r27
            float r13 = (float) r13
            r3.set(r4, r5, r7, r13)
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0dc3
            boolean r4 = r8.drawPin
            if (r4 == 0) goto L_0x0db1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            float r5 = r8.reorderIconProgress
            float r5 = r3 - r5
            r3 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r3
            int r3 = (int) r5
            r4.setAlpha(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r4 = r8.pinLeft
            int r5 = r8.pinTop
            setDrawableBounds((android.graphics.drawable.Drawable) r3, (int) r4, (int) r5)
            r35.save()
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r6
            float r5 = r3 - r6
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r3 = r3.getBounds()
            int r3 = r3.centerX()
            float r3 = (float) r3
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            android.graphics.Rect r7 = r7.getBounds()
            int r7 = r7.centerY()
            float r7 = (float) r7
            r9.scale(r4, r5, r3, r7)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            r3.draw(r9)
            r35.restore()
        L_0x0db1:
            r35.save()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r6, r6, r3, r4)
        L_0x0dc3:
            android.graphics.RectF r3 = r8.rect
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1094189056(0x41380000, float:11.5)
            float r4 = r4 * r5
            float r7 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 * r5
            r9.drawRoundRect(r3, r4, r7, r1)
            if (r0 == 0) goto L_0x0deb
            r35.save()
            int r3 = r8.countLeft
            float r3 = (float) r3
            int r4 = r8.countTop
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = r4 + r5
            float r4 = (float) r4
            r9.translate(r3, r4)
            r0.draw(r9)
            r35.restore()
        L_0x0deb:
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0df4
            r35.restore()
        L_0x0df4:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0eb0
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            float r1 = r8.reorderIconProgress
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r1
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
            r0.setAlpha(r1)
            int r0 = r8.mentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r2
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
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r7
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            boolean r1 = r8.dialogMuted
            if (r1 == 0) goto L_0x0e3a
            int r1 = r8.folderId
            if (r1 == 0) goto L_0x0e3a
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0e3c
        L_0x0e3a:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0e3c:
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r3 = r3 * r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 * r4
            r9.drawRoundRect(r2, r3, r5, r1)
            android.text.StaticLayout r2 = r8.mentionLayout
            if (r2 == 0) goto L_0x0e79
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = r8.reorderIconProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r4
            int r3 = (int) r3
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
            goto L_0x0eb0
        L_0x0e79:
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_mentionDrawable
            float r3 = r8.reorderIconProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r4
            int r3 = (int) r3
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
        L_0x0eb0:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0ebd
            float r0 = r8.reactionsMentionsChangeProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0f5c
            goto L_0x0ebf
        L_0x0ebd:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0ebf:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            float r2 = r8.reorderIconProgress
            float r4 = r1 - r2
            r1 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r1 = (int) r4
            r0.setAlpha(r1)
            int r0 = r8.reactionMentionLeft
            r1 = 1085276160(0x40b00000, float:5.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            android.graphics.RectF r1 = r8.rect
            float r2 = (float) r0
            int r3 = r8.countTop
            float r3 = (float) r3
            r4 = 1102577664(0x41b80000, float:23.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r5 + r0
            float r5 = (float) r5
            int r6 = r8.countTop
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r6 + r4
            float r4 = (float) r6
            r1.set(r2, r3, r5, r4)
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsCountPaint
            r35.save()
            float r2 = r8.reactionsMentionsChangeProgress
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0var_
            boolean r4 = r8.drawReactionMention
            if (r4 == 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            float r2 = r3 - r2
        L_0x0var_:
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r2, r2, r3, r4)
        L_0x0var_:
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r3 = r3 * r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 * r4
            r9.drawRoundRect(r2, r3, r5, r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            float r3 = r8.reorderIconProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            r4 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            r2.setAlpha(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            int r3 = r8.reactionMentionLeft
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 - r4
            int r4 = r8.countTop
            r5 = 1081291571(0x40733333, float:3.8)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            setDrawableBounds((android.graphics.drawable.Drawable) r2, (int) r3, (int) r4, (int) r5, (int) r6)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.dialogs_reactionsMentionDrawable
            r2.draw(r9)
            r35.restore()
        L_0x0f5c:
            boolean r0 = r8.animatingArchiveAvatar
            r7 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x0var_
            r35.save()
            org.telegram.ui.Cells.DialogCell$BounceInterpolator r0 = r8.interpolator
            float r1 = r8.animatingArchiveAvatarProgress
            float r1 = r1 / r7
            float r0 = r0.getInterpolation(r1)
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r0 + r1
            org.telegram.messenger.ImageReceiver r1 = r8.avatarImage
            float r1 = r1.getCenterX()
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getCenterY()
            r9.scale(r0, r0, r1, r2)
        L_0x0var_:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0f8e
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0f8e
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0var_
        L_0x0f8e:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0var_:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0fcb
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0fcb
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
        L_0x0fcb:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0fd2
            r35.restore()
        L_0x0fd2:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x13c4
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x13c4
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x10b5
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x10b5
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x10b5
            boolean r0 = r34.isOnline()
            if (r0 != 0) goto L_0x0ff9
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x10b1
        L_0x0ff9:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x1007
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1009
        L_0x1007:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1009:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x102d
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1025
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1023
            goto L_0x1025
        L_0x1023:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1025:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x1045
        L_0x102d:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x103e
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x103c
            goto L_0x103e
        L_0x103c:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x103e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x1045:
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
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
            if (r0 == 0) goto L_0x109b
            float r3 = r8.onlineProgress
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x10b1
            float r5 = (float) r14
            r6 = 1125515264(0x43160000, float:150.0)
            float r5 = r5 / r6
            float r3 = r3 + r5
            r8.onlineProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x1098
            r8.onlineProgress = r4
        L_0x1098:
            r30 = 1
            goto L_0x10b1
        L_0x109b:
            float r3 = r8.onlineProgress
            r4 = 0
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x10b1
            float r5 = (float) r14
            r6 = 1125515264(0x43160000, float:150.0)
            float r5 = r5 / r6
            float r3 = r3 - r5
            r8.onlineProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x10af
            r8.onlineProgress = r4
        L_0x10af:
            r30 = 1
        L_0x10b1:
            r17 = r10
            goto L_0x13c6
        L_0x10b5:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x13c1
            boolean r0 = r0.call_active
            if (r0 == 0) goto L_0x10c5
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x10c5
            r5 = 1
            goto L_0x10c6
        L_0x10c5:
            r5 = 0
        L_0x10c6:
            r8.hasCall = r5
            if (r5 != 0) goto L_0x10d6
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x10d2
            goto L_0x10d6
        L_0x10d2:
            r17 = r10
            goto L_0x13c6
        L_0x10d6:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x10eb
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x10eb
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r0
            goto L_0x10ed
        L_0x10eb:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x10ed:
            r0 = r4
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x10fc
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x10fe
        L_0x10fc:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x10fe:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x1122
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x111a
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1118
            goto L_0x111a
        L_0x1118:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x111a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x113a
        L_0x1122:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1133
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1131
            goto L_0x1133
        L_0x1131:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1133:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x113a:
            android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r8.resourcesProvider
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
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
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3.setColor(r4)
            int r3 = r8.progressStage
            if (r3 != 0) goto L_0x11b0
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r4 = r4 - r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x12c4
        L_0x11b0:
            r4 = 1
            if (r3 != r4) goto L_0x11d7
            r3 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r5 = r5 + r6
            goto L_0x12c4
        L_0x11d7:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 2
            if (r3 != r5) goto L_0x1201
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r4 = r4 - r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x12c4
        L_0x1201:
            r4 = 3
            if (r3 != r4) goto L_0x1228
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r5 = r5 + r6
            goto L_0x12c4
        L_0x1228:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 4
            if (r3 != r5) goto L_0x1251
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r4 = r4 - r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x12c4
        L_0x1251:
            r4 = 5
            if (r3 != r4) goto L_0x127a
            r3 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r6 = r8.innerProgress
            float r5 = r5 * r6
            float r4 = r4 + r5
            r5 = r4
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x12c4
        L_0x127a:
            r4 = 6
            if (r3 != r4) goto L_0x12a2
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r5 = r5 - r6
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x12c4
        L_0x12a2:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r5 = r8.innerProgress
            float r4 = r4 * r5
            float r3 = r3 - r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r6 = (float) r6
            float r13 = r8.innerProgress
            float r6 = r6 * r13
            float r5 = r5 + r6
        L_0x12c4:
            float r6 = r8.chatCallProgress
            int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x12ce
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x12dc
        L_0x12ce:
            r35.save()
            float r4 = r8.chatCallProgress
            float r6 = r4 * r0
            float r4 = r4 * r0
            float r13 = (float) r1
            float r7 = (float) r2
            r9.scale(r6, r4, r13, r7)
        L_0x12dc:
            android.graphics.RectF r4 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = r1 - r7
            float r7 = (float) r7
            float r13 = (float) r2
            float r13 = r13 - r3
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r1 + r17
            float r6 = (float) r6
            r17 = r10
            float r10 = (float) r2
            float r10 = r10 + r3
            r4.set(r7, r13, r6, r10)
            android.graphics.RectF r4 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r7, r6, r10)
            android.graphics.RectF r4 = r8.rect
            r6 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r1 - r7
            float r6 = (float) r6
            float r7 = (float) r2
            float r7 = r7 - r5
            r10 = 1077936128(0x40400000, float:3.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r1 - r10
            float r10 = (float) r10
            float r13 = (float) r2
            float r13 = r13 + r5
            r4.set(r6, r7, r10, r13)
            android.graphics.RectF r4 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r10
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r7, r6, r10)
            android.graphics.RectF r4 = r8.rect
            r6 = 1077936128(0x40400000, float:3.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r1
            float r6 = (float) r6
            float r7 = (float) r2
            float r7 = r7 - r5
            r10 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r1
            float r10 = (float) r10
            float r13 = (float) r2
            float r13 = r13 + r5
            r4.set(r6, r7, r10, r13)
            android.graphics.RectF r4 = r8.rect
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r10 = (float) r10
            android.graphics.Paint r13 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r9.drawRoundRect(r4, r7, r10, r13)
            float r4 = r8.chatCallProgress
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 < 0) goto L_0x136e
            int r4 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x1371
        L_0x136e:
            r35.restore()
        L_0x1371:
            float r4 = r8.innerProgress
            float r6 = (float) r14
            r7 = 1137180672(0x43CLASSNAME, float:400.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r8.innerProgress = r4
            r6 = 1065353216(0x3var_, float:1.0)
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 < 0) goto L_0x1390
            r4 = 0
            r8.innerProgress = r4
            int r4 = r8.progressStage
            r6 = 1
            int r4 = r4 + r6
            r8.progressStage = r4
            r6 = 8
            if (r4 < r6) goto L_0x1390
            r4 = 0
            r8.progressStage = r4
        L_0x1390:
            r30 = 1
            boolean r4 = r8.hasCall
            if (r4 == 0) goto L_0x13ac
            float r4 = r8.chatCallProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 >= 0) goto L_0x13c6
            float r7 = (float) r14
            r10 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r10
            float r4 = r4 + r7
            r8.chatCallProgress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x13c6
            r8.chatCallProgress = r6
            goto L_0x13c6
        L_0x13ac:
            float r4 = r8.chatCallProgress
            r6 = 0
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 <= 0) goto L_0x13c6
            float r7 = (float) r14
            r10 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r10
            float r4 = r4 - r7
            r8.chatCallProgress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x13c6
            r8.chatCallProgress = r6
            goto L_0x13c6
        L_0x13c1:
            r17 = r10
            goto L_0x13c6
        L_0x13c4:
            r17 = r10
        L_0x13c6:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x13d0
            r35.restore()
        L_0x13d0:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x13f6
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x13f6
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x13f6
            r35.save()
            int r0 = r34.getMeasuredWidth()
            int r1 = r34.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r35.restore()
        L_0x13f6:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x145d
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x141a
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x140a
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x140a
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x141a
        L_0x140a:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1413
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x1413
            goto L_0x141a
        L_0x1413:
            r0 = 1116733440(0x42900000, float:72.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x141b
        L_0x141a:
            r0 = 0
        L_0x141b:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x143f
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
            goto L_0x145d
        L_0x143f:
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
        L_0x145d:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x14ad
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x146e
            r35.restore()
            goto L_0x14ad
        L_0x146e:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedPaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r8.resourcesProvider
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r12, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
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
        L_0x14ad:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x14b8
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x14e8
        L_0x14b8:
            if (r0 == 0) goto L_0x14d2
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x14e8
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x14cf
            r8.reorderIconProgress = r1
        L_0x14cf:
            r30 = 1
            goto L_0x14e8
        L_0x14d2:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x14e8
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14e6
            r8.reorderIconProgress = r1
        L_0x14e6:
            r30 = 1
        L_0x14e8:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1519
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x1546
            float r2 = (float) r14
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x1500
            r8.archiveBackgroundProgress = r1
        L_0x1500:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1516
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1516:
            r30 = 1
            goto L_0x1546
        L_0x1519:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1546
            float r2 = (float) r14
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x152e
            r8.archiveBackgroundProgress = r1
        L_0x152e:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x1544
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x1544:
            r30 = 1
        L_0x1546:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x155d
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r14
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x155b
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x155b:
            r30 = 1
        L_0x155d:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x158e
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1578
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1578
            r8.currentRevealBounceProgress = r1
            r30 = 1
        L_0x1578:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x15b2
            float r2 = (float) r14
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x158b
            r8.currentRevealProgress = r1
        L_0x158b:
            r30 = 1
            goto L_0x15b2
        L_0x158e:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x159c
            r1 = 0
            r8.currentRevealBounceProgress = r1
            r30 = 1
            goto L_0x159d
        L_0x159c:
            r1 = 0
        L_0x159d:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x15b2
            float r2 = (float) r14
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x15b0
            r8.currentRevealProgress = r1
        L_0x15b0:
            r30 = 1
        L_0x15b2:
            if (r30 == 0) goto L_0x15b7
            r34.invalidate()
        L_0x15b7:
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

    /* renamed from: lambda$createStatusDrawableAnimator$2$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m2790x2a941262(ValueAnimator valueAnimator) {
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

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        DialogsActivity dialogsActivity;
        if (action != NUM || (dialogsActivity = this.parentFragment) == null) {
            return super.performAccessibilityAction(action, arguments);
        }
        dialogsActivity.showChatPreview(this);
        return true;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (!isFolderCell() || this.archivedChatsDrawable == null || !SharedConfig.archiveHidden || this.archivedChatsDrawable.pullProgress != 0.0f) {
            info.addAction(16);
            info.addAction(32);
            if (!isFolderCell() && this.parentFragment != null && Build.VERSION.SDK_INT >= 21) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("AccActionChatPreview", NUM)));
            }
        } else {
            info.setVisibleToUser(false);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setClassName("android.widget.CheckBox");
            info.setCheckable(true);
            info.setChecked(true);
        }
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
        if (this.drawVerified) {
            sb.append(LocaleController.getString("AccDescrVerified", NUM));
            sb.append(". ");
        }
        int i = this.unreadCount;
        if (i > 0) {
            sb.append(LocaleController.formatPluralString("NewMessages", i, new Object[0]));
            sb.append(". ");
        }
        int i2 = this.mentionCount;
        if (i2 > 0) {
            sb.append(LocaleController.formatPluralString("AccDescrMentionCount", i2, new Object[0]));
            sb.append(". ");
        }
        if (this.reactionMentionCount > 0) {
            sb.append(LocaleController.getString("AccDescrMentionReaction", NUM));
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
            StringBuilder messageString = new StringBuilder();
            messageString.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && !TextUtils.isEmpty(this.message.caption)) {
                messageString.append(". ");
                messageString.append(this.message.caption);
            }
            StaticLayout staticLayout = this.messageLayout;
            int len = staticLayout == null ? -1 : staticLayout.getText().length();
            if (len > 0) {
                int index2 = messageString.length();
                int indexOf = messageString.indexOf("\n", len);
                int b = indexOf;
                if (indexOf < index2 && b >= 0) {
                    index2 = b;
                }
                int indexOf2 = messageString.indexOf("\t", len);
                int b2 = indexOf2;
                if (indexOf2 < index2 && b2 >= 0) {
                    index2 = b2;
                }
                int indexOf3 = messageString.indexOf(" ", len);
                int b3 = indexOf3;
                if (indexOf3 < index2 && b3 >= 0) {
                    index2 = b3;
                }
                sb.append(messageString.substring(0, index2));
            } else {
                sb.append(messageString);
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

    public boolean isDialogFolder() {
        return this.currentDialogFolderId > 0;
    }

    public MessageObject getMessage() {
        return this.message;
    }
}
