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
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawPin;
    private boolean drawPinBackground;
    private boolean drawPlay;
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v127, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v209, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v162, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v263, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v265, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r9v249 */
    /* JADX WARNING: type inference failed for: r9v250 */
    /* JADX WARNING: type inference failed for: r9v253 */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x1b48, code lost:
        if (org.telegram.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x1b54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x1b6d, code lost:
        if (org.telegram.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x1b6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x06f1, code lost:
        if (r1.chat.kicked == false) goto L_0x06fc;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1a62  */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x1b24  */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x1b2d  */
    /* JADX WARNING: Removed duplicated region for block: B:1051:0x1b46 A[SYNTHETIC, Splitter:B:1051:0x1b46] */
    /* JADX WARNING: Removed duplicated region for block: B:1059:0x1b58 A[SYNTHETIC, Splitter:B:1059:0x1b58] */
    /* JADX WARNING: Removed duplicated region for block: B:1067:0x1b6b A[SYNTHETIC, Splitter:B:1067:0x1b6b] */
    /* JADX WARNING: Removed duplicated region for block: B:1072:0x1b81  */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x1b86 A[Catch:{ Exception -> 0x1c6b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1bdd A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1099:0x1be7 A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x1c3e A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x1CLASSNAME A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x1c7c  */
    /* JADX WARNING: Removed duplicated region for block: B:1173:0x1dc2  */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x1e62 A[Catch:{ Exception -> 0x1e8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1212:0x1e7e A[Catch:{ Exception -> 0x1e8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1220:0x1e9a  */
    /* JADX WARNING: Removed duplicated region for block: B:1229:0x1ee5  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0548  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x059e  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x05ba  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0ed0 A[SYNTHETIC, Splitter:B:582:0x0ed0] */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ef3  */
    /* JADX WARNING: Removed duplicated region for block: B:600:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0f4a  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0f5c  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x118d  */
    /* JADX WARNING: Removed duplicated region for block: B:709:0x1240  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x1247  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x1252  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x125c  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x1264  */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x1282  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x1299  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x13d8  */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x143a  */
    /* JADX WARNING: Removed duplicated region for block: B:808:0x1445  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x1457  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x14c3  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x1507  */
    /* JADX WARNING: Removed duplicated region for block: B:841:0x1517  */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x1527  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x153f  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x154f  */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x1590  */
    /* JADX WARNING: Removed duplicated region for block: B:862:0x15c1  */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x1652  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x166b  */
    /* JADX WARNING: Removed duplicated region for block: B:920:0x1719  */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x17da  */
    /* JADX WARNING: Removed duplicated region for block: B:931:0x1825  */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x1844  */
    /* JADX WARNING: Removed duplicated region for block: B:936:0x1890  */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x18b5  */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x18e6  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r56 = this;
            r1 = r56
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
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 18
            if (r5 < r6) goto L_0x010d
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x00ff
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0103
        L_0x00ff:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x0108
        L_0x0103:
            java.lang.String r5 = "%2$s: ⁨%1$s⁩"
            r22 = 1
            goto L_0x0122
        L_0x0108:
            java.lang.String r5 = "⁨%s⁩"
            r22 = 0
            goto L_0x0122
        L_0x010d:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0115
            boolean r5 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0119
        L_0x0115:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x011e
        L_0x0119:
            java.lang.String r5 = "%2$s: %1$s"
            r22 = 1
            goto L_0x0122
        L_0x011e:
            java.lang.String r5 = "%1$s"
            r22 = 0
        L_0x0122:
            org.telegram.messenger.MessageObject r6 = r1.message
            r24 = r3
            if (r6 == 0) goto L_0x012b
            java.lang.CharSequence r6 = r6.messageText
            goto L_0x012c
        L_0x012b:
            r6 = 0
        L_0x012c:
            boolean r3 = r6 instanceof android.text.Spannable
            if (r3 == 0) goto L_0x0177
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
        L_0x014a:
            if (r8 >= r6) goto L_0x0158
            r29 = r6
            r6 = r4[r8]
            r3.removeSpan(r6)
            int r8 = r8 + 1
            r6 = r29
            goto L_0x014a
        L_0x0158:
            int r4 = r3.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanNoUnderline> r6 = org.telegram.ui.Components.URLSpanNoUnderline.class
            r8 = 0
            java.lang.Object[] r4 = r3.getSpans(r8, r4, r6)
            org.telegram.ui.Components.URLSpanNoUnderline[] r4 = (org.telegram.ui.Components.URLSpanNoUnderline[]) r4
            int r6 = r4.length
            r8 = 0
        L_0x0167:
            if (r8 >= r6) goto L_0x0175
            r29 = r6
            r6 = r4[r8]
            r3.removeSpan(r6)
            int r8 = r8 + 1
            r6 = r29
            goto L_0x0167
        L_0x0175:
            r6 = r3
            goto L_0x017d
        L_0x0177:
            r26 = r4
            r27 = r6
            r28 = r8
        L_0x017d:
            r1.lastMessageString = r6
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            r8 = 1117782016(0x42a00000, float:80.0)
            r27 = 1118044160(0x42a40000, float:82.0)
            r30 = 1101004800(0x41a00000, float:20.0)
            r31 = 1102053376(0x41b00000, float:22.0)
            r33 = 1117257728(0x42980000, float:76.0)
            r34 = 1117519872(0x429CLASSNAME, float:78.0)
            r35 = 1099956224(0x41900000, float:18.0)
            r4 = 2
            if (r3 == 0) goto L_0x0405
            int r3 = r3.type
            if (r3 != r4) goto L_0x0218
            r3 = 1
            r1.drawNameLock = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x01dd
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x01a2
            goto L_0x01dd
        L_0x01a2:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01c3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 + r8
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x01c3:
            int r3 = r56.getMeasuredWidth()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 - r8
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x01dd:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01fe
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 + r8
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x01fe:
            int r3 = r56.getMeasuredWidth()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r34)
            int r3 = r3 - r8
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r8 = r8.getIntrinsicWidth()
            int r3 = r3 - r8
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x0218:
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            boolean r3 = r3.verified
            r1.drawVerified = r3
            boolean r3 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x02c4
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r4 = 1
            if (r3 != r4) goto L_0x02c4
            r1.drawNameGroup = r4
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x027d
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0234
            goto L_0x027d
        L_0x0234:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x025c
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0251
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0253
        L_0x0251:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0253:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x025c:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x026c
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x026e
        L_0x026c:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x026e:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x027d:
            r3 = 1096286208(0x41580000, float:13.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02a4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x029a
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x029c
        L_0x029a:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x029c:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x02a4:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r34)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x02b4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x02b6
        L_0x02b4:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x02b6:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x02c4:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x02df
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x02cd
            goto L_0x02df
        L_0x02cd:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02d8
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x02d8:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x02df:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02ea
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLeft = r3
            goto L_0x02f0
        L_0x02ea:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
        L_0x02f0:
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r4 = 1
            if (r3 != r4) goto L_0x03a2
            r3 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.String r4 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            boolean r4 = r4.isMedia
            if (r4 == 0) goto L_0x033d
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r2 = r4[r8]
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.messageText
            r23 = r3
            r3 = 0
            r8[r3] = r4
            java.lang.String r4 = java.lang.String.format(r5, r8)
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r4)
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r1.resourcesProvider
            r24 = r2
            java.lang.String r2 = "chats_attachMessage"
            r8.<init>(r2, r3)
            int r2 = r4.length()
            r36 = r9
            r3 = 33
            r9 = 0
            r4.setSpan(r8, r9, r2, r3)
            r37 = r10
            r2 = r24
            goto L_0x038d
        L_0x033d:
            r23 = r3
            r36 = r9
            r9 = 0
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r4 <= r8) goto L_0x0352
            java.lang.String r3 = r3.substring(r9, r8)
        L_0x0352:
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x037a
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x035d
            r4 = 1
            r9 = 0
            goto L_0x037c
        L_0x035d:
            r4 = 2
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r4 = 10
            r9 = 32
            java.lang.String r4 = r3.replace(r4, r9)
            r9 = 0
            r8[r9] = r4
            r4 = 1
            r8[r4] = r0
            java.lang.String r8 = java.lang.String.format(r5, r8)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r8)
            r4 = r8
            r37 = r10
            goto L_0x038d
        L_0x037a:
            r4 = 1
            r9 = 0
        L_0x037c:
            r37 = r10
            r8 = 2
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r10[r9] = r3
            r10[r4] = r0
            java.lang.String r4 = java.lang.String.format(r5, r10)
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x038d:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r3 = r3[r8]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r30)
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r4, r3, r8, r9)
            r24 = r23
            goto L_0x03b6
        L_0x03a2:
            r36 = r9
            r37 = r10
            org.telegram.ui.Cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            boolean r4 = r4.isMedia
            if (r4 == 0) goto L_0x03b6
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r2 = r4[r8]
        L_0x03b6:
            org.telegram.ui.Cells.DialogCell$CustomDialog r4 = r1.customDialog
            int r4 = r4.date
            long r8 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            int r8 = r8.unread_count
            if (r8 == 0) goto L_0x03dd
            r8 = 1
            r1.drawCount = r8
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            int r8 = r8.unread_count
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r10 = 0
            r9[r10] = r8
            java.lang.String r8 = "%d"
            java.lang.String r8 = java.lang.String.format(r8, r9)
            r10 = r8
            goto L_0x03e2
        L_0x03dd:
            r10 = 0
            r1.drawCount = r10
            r10 = r37
        L_0x03e2:
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            boolean r8 = r8.sent
            if (r8 == 0) goto L_0x03ef
            r8 = 1
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
            r8 = 0
            goto L_0x03f4
        L_0x03ef:
            r8 = 0
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
        L_0x03f4:
            r1.drawClock = r8
            r1.drawError = r8
            org.telegram.ui.Cells.DialogCell$CustomDialog r8 = r1.customDialog
            java.lang.String r8 = r8.name
            r12 = r11
            r9 = r26
            r11 = r10
            r10 = r8
            r8 = r2
            r2 = r0
            goto L_0x14c1
        L_0x0405:
            r36 = r9
            r37 = r10
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0424
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0412
            goto L_0x0424
        L_0x0412:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x041d
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLeft = r3
            goto L_0x0435
        L_0x041d:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x0435
        L_0x0424:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x042f
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLeft = r3
            goto L_0x0435
        L_0x042f:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
        L_0x0435:
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x04bf
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0688
            r3 = 1
            r1.drawNameLock = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0484
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0449
            goto L_0x0484
        L_0x0449:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x046a
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x046a:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0484:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x04a5
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x04a5:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r34)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x0688
        L_0x04bf:
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0688
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x05db
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x04d4
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x04e9
        L_0x04d4:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x04e3
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x04e9
        L_0x04e3:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.verified
            r1.drawVerified = r3
        L_0x04e9:
            boolean r3 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0688
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0569
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x04f7
            goto L_0x0569
        L_0x04f7:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            long r3 = r3.id
            r9 = 0
            int r27 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r27 < 0) goto L_0x051d
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0511
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0511
            r3 = 1
            goto L_0x051e
        L_0x0511:
            r3 = 1
            r1.drawNameGroup = r3
            r4 = 1099694080(0x418CLASSNAME, float:17.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x0528
        L_0x051d:
            r3 = 1
        L_0x051e:
            r1.drawNameBroadcast = r3
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
        L_0x0528:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0548
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x053d
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x053f
        L_0x053d:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x053f:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0548:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0558
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x055a
        L_0x0558:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x055a:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0569:
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            long r3 = r3.id
            r8 = 0
            int r10 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r10 < 0) goto L_0x058f
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0583
            org.telegram.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0583
            r3 = 1
            goto L_0x0590
        L_0x0583:
            r3 = 1
            r1.drawNameGroup = r3
            r4 = 1096286208(0x41580000, float:13.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r4
            goto L_0x059a
        L_0x058f:
            r3 = 1
        L_0x0590:
            r1.drawNameBroadcast = r3
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
        L_0x059a:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x05ba
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x05af
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x05b1
        L_0x05af:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x05b1:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x05ba:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r34)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x05ca
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x05cc
        L_0x05ca:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x05cc:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
            goto L_0x0688
        L_0x05db:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x0688
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x05ec
            r3 = 1
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x0601
        L_0x05ec:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.fake
            if (r3 == 0) goto L_0x05fb
            r3 = 2
            r1.drawScam = r3
            org.telegram.ui.Components.ScamDrawable r3 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
            r3.checkText()
            goto L_0x0601
        L_0x05fb:
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.verified
            r1.drawVerified = r3
        L_0x0601:
            boolean r3 = org.telegram.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0688
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.bot
            if (r3 == 0) goto L_0x0688
            r3 = 1
            r1.drawNameBot = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0650
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0617
            goto L_0x0650
        L_0x0617:
            r3 = 1099169792(0x41840000, float:16.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0637
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0637:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r33)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r35)
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0650:
            r3 = 1095237632(0x41480000, float:12.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0670
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0688
        L_0x0670:
            int r3 = r56.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r34)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.nameLeft = r3
        L_0x0688:
            int r3 = r1.lastMessageDate
            int r4 = r1.lastMessageDate
            if (r4 != 0) goto L_0x0696
            org.telegram.messenger.MessageObject r4 = r1.message
            if (r4 == 0) goto L_0x0696
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r3 = r4.date
        L_0x0696:
            boolean r4 = r1.isDialogCell
            if (r4 == 0) goto L_0x06f9
            int r4 = r1.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            long r8 = r1.currentDialogId
            r10 = 0
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r4.getDraft(r8, r10)
            r1.draftMessage = r4
            if (r4 == 0) goto L_0x06c3
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x06b9
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.reply_to_msg_id
            if (r4 == 0) goto L_0x06f3
        L_0x06b9:
            org.telegram.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.date
            if (r3 <= r4) goto L_0x06c3
            int r4 = r1.unreadCount
            if (r4 != 0) goto L_0x06f3
        L_0x06c3:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x06e5
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x06e5
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.creator
            if (r4 != 0) goto L_0x06e5
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            if (r4 == 0) goto L_0x06f3
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            boolean r4 = r4.post_messages
            if (r4 == 0) goto L_0x06f3
        L_0x06e5:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x06f7
            boolean r4 = r4.left
            if (r4 != 0) goto L_0x06f3
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.kicked
            if (r4 == 0) goto L_0x06fc
        L_0x06f3:
            r4 = 0
            r1.draftMessage = r4
            goto L_0x06fc
        L_0x06f7:
            r4 = 0
            goto L_0x06fc
        L_0x06f9:
            r4 = 0
            r1.draftMessage = r4
        L_0x06fc:
            if (r13 == 0) goto L_0x0796
            r1.lastPrintString = r13
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r8 = r1.currentDialogId
            r10 = 0
            java.lang.Integer r4 = r4.getPrintingStringType(r8, r10)
            int r4 = r4.intValue()
            r1.printingStringType = r4
            org.telegram.ui.Components.StatusDrawable r4 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r4)
            r8 = 0
            if (r4 == 0) goto L_0x0726
            int r9 = r4.getIntrinsicWidth()
            r10 = 1077936128(0x40400000, float:3.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = r9 + r10
        L_0x0726:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>()
            r27 = r3
            r10 = 1
            java.lang.String[] r3 = new java.lang.String[r10]
            java.lang.String r20 = "..."
            r21 = 0
            r3[r21] = r20
            r23 = r4
            java.lang.String[] r4 = new java.lang.String[r10]
            java.lang.String r10 = ""
            r4[r21] = r10
            java.lang.CharSequence r13 = android.text.TextUtils.replace(r13, r3, r4)
            int r3 = r1.printingStringType
            r4 = 5
            if (r3 != r4) goto L_0x0752
            java.lang.String r3 = r13.toString()
            java.lang.String r4 = "**oo**"
            int r4 = r3.indexOf(r4)
            goto L_0x0754
        L_0x0752:
            r4 = r26
        L_0x0754:
            if (r4 < 0) goto L_0x0774
            android.text.SpannableStringBuilder r3 = r9.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r10 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r29 = r11
            int r11 = r1.printingStringType
            org.telegram.ui.Components.StatusDrawable r11 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r11)
            int r11 = r11.getIntrinsicWidth()
            r10.<init>(r11)
            int r11 = r4 + 6
            r38 = r12
            r12 = 0
            r3.setSpan(r10, r4, r11, r12)
            goto L_0x078c
        L_0x0774:
            r29 = r11
            r38 = r12
            r12 = 0
            java.lang.String r3 = " "
            android.text.SpannableStringBuilder r3 = r9.append(r3)
            android.text.SpannableStringBuilder r3 = r3.append(r13)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r10 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            r10.<init>(r8)
            r11 = 1
            r3.setSpan(r10, r12, r11, r12)
        L_0x078c:
            r12 = r9
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r10 = r1.paintIndex
            r2 = r3[r10]
            r3 = 0
            goto L_0x1258
        L_0x0796:
            r27 = r3
            r29 = r11
            r38 = r12
            r3 = 0
            r1.lastPrintString = r3
            org.telegram.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            r4 = 256(0x100, float:3.59E-43)
            if (r3 == 0) goto L_0x084b
            r3 = 0
            r8 = 2131625440(0x7f0e05e0, float:1.8878088E38)
            java.lang.String r9 = "Draft"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x07e4
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x07de
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x07c2
            goto L_0x07de
        L_0x07c2:
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.resourcesProvider
            java.lang.String r10 = "chats_draft"
            r8.<init>(r10, r9)
            int r9 = r0.length()
            r10 = 33
            r11 = 0
            r4.setSpan(r8, r11, r9, r10)
            r12 = r4
            r4 = r26
            goto L_0x1258
        L_0x07de:
            java.lang.String r12 = ""
            r4 = r26
            goto L_0x1258
        L_0x07e4:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            java.lang.String r8 = r8.message
            int r9 = r8.length()
            r10 = 150(0x96, float:2.1E-43)
            if (r9 <= r10) goto L_0x07f5
            r9 = 0
            java.lang.String r8 = r8.substring(r9, r10)
        L_0x07f5:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r8)
            org.telegram.tgnet.TLRPC$DraftMessage r10 = r1.draftMessage
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.tgnet.TLRPC.DraftMessage) r10, (android.text.Spannable) r9, (int) r4)
            r4 = 2
            java.lang.CharSequence[] r10 = new java.lang.CharSequence[r4]
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            r11 = 0
            r10[r11] = r4
            r4 = 1
            r10[r4] = r0
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r10)
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x0830
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 != 0) goto L_0x0830
            org.telegram.ui.Components.ForegroundColorSpanThemable r10 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider
            java.lang.String r12 = "chats_draft"
            r10.<init>(r12, r11)
            int r11 = r0.length()
            r12 = 1
            int r11 = r11 + r12
            r23 = r3
            r3 = 0
            r12 = 33
            r4.setSpan(r10, r3, r11, r12)
            goto L_0x0832
        L_0x0830:
            r23 = r3
        L_0x0832:
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r10 = r1.paintIndex
            r3 = r3[r10]
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r11 = 0
            java.lang.CharSequence r12 = org.telegram.messenger.Emoji.replaceEmoji(r4, r3, r10, r11)
            r3 = r23
            r4 = r26
            goto L_0x1258
        L_0x084b:
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0864
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r2 = r3[r4]
            r3 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r4 = "HistoryCleared"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x0864:
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 != 0) goto L_0x0927
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x08ff
            android.text.TextPaint[] r3 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r4 = r1.paintIndex
            r2 = r3[r4]
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatRequested
            if (r4 == 0) goto L_0x0887
            r3 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r4 = "EncryptionProcessing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x0887:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r4 == 0) goto L_0x08a6
            r3 = 2131624575(0x7f0e027f, float:1.8876334E38)
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r9 = 0
            r8[r9] = r4
            java.lang.String r4 = "AwaitingEncryption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r3, r8)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x08a6:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r4 == 0) goto L_0x08b9
            r3 = 2131625544(0x7f0e0648, float:1.8878299E38)
            java.lang.String r4 = "EncryptionRejected"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x08b9:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r4 == 0) goto L_0x08f7
            long r3 = r3.admin_id
            int r8 = r1.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r8 = r8.getClientUserId()
            int r10 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x08e8
            r3 = 2131625532(0x7f0e063c, float:1.8878275E38)
            r4 = 1
            java.lang.Object[] r8 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)
            r9 = 0
            r8[r9] = r4
            java.lang.String r4 = "EncryptedChatStartedOutgoing"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r4, r3, r8)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x08e8:
            r3 = 2131625531(0x7f0e063b, float:1.8878273E38)
            java.lang.String r4 = "EncryptedChatStartedIncoming"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x08f7:
            r3 = r24
            r4 = r26
            r12 = r38
            goto L_0x1258
        L_0x08ff:
            int r3 = r1.dialogsType
            r4 = 3
            if (r3 != r4) goto L_0x091f
            org.telegram.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x091f
            r3 = 2131627867(0x7f0e0f5b, float:1.888301E38)
            java.lang.String r4 = "SavedMessagesInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r15 = 0
            r3 = 0
            r18 = r3
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x091f:
            java.lang.String r12 = ""
            r3 = r24
            r4 = r26
            goto L_0x1258
        L_0x0927:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r3.restriction_reason
            java.lang.String r3 = org.telegram.messenger.MessagesController.getRestrictionReason(r3)
            r8 = 0
            r9 = 0
            org.telegram.messenger.MessageObject r10 = r1.message
            long r10 = r10.getFromChatId()
            boolean r12 = org.telegram.messenger.DialogObject.isUserDialog(r10)
            if (r12 == 0) goto L_0x094c
            int r12 = r1.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r12.getUser(r4)
            goto L_0x0960
        L_0x094c:
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r40 = r8
            r12 = r9
            long r8 = -r10
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r9 = r4.getChat(r8)
            r8 = r40
        L_0x0960:
            r4 = 1
            r1.drawCount2 = r4
            int r4 = r1.dialogsType
            r12 = 2
            if (r4 != r12) goto L_0x0a0b
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x09f7
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x09b0
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x09b0
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x098a
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r4 = r4.participants_count
            java.lang.String r12 = "Subscribers"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralStringComma(r12, r4)
            goto L_0x09f9
        L_0x098a:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            java.lang.String r4 = r4.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x09a2
            r4 = 2131624874(0x7f0e03aa, float:1.887694E38)
            java.lang.String r12 = "ChannelPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            java.lang.String r4 = r4.toLowerCase()
            goto L_0x09f9
        L_0x09a2:
            r4 = 2131624877(0x7f0e03ad, float:1.8876946E38)
            java.lang.String r12 = "ChannelPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            java.lang.String r4 = r4.toLowerCase()
            goto L_0x09f9
        L_0x09b0:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x09c1
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            int r4 = r4.participants_count
            java.lang.String r12 = "Members"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralStringComma(r12, r4)
            goto L_0x09f9
        L_0x09c1:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.has_geo
            if (r4 == 0) goto L_0x09d1
            r4 = 2131626432(0x7f0e09c0, float:1.88801E38)
            java.lang.String r12 = "MegaLocation"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            goto L_0x09f9
        L_0x09d1:
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            java.lang.String r4 = r4.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x09e9
            r4 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            java.lang.String r12 = "MegaPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            java.lang.String r4 = r4.toLowerCase()
            goto L_0x09f9
        L_0x09e9:
            r4 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            java.lang.String r12 = "MegaPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            java.lang.String r4 = r4.toLowerCase()
            goto L_0x09f9
        L_0x09f7:
            java.lang.String r4 = ""
        L_0x09f9:
            r12 = 0
            r1.drawCount2 = r12
            r12 = 0
            r18 = 0
            r40 = r8
            r43 = r9
            r41 = r10
            r15 = r12
            r44 = r13
            r12 = r4
            goto L_0x1243
        L_0x0a0b:
            r12 = 3
            if (r4 != r12) goto L_0x0a2e
            org.telegram.tgnet.TLRPC$User r4 = r1.user
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r4)
            if (r4 == 0) goto L_0x0a2e
            r4 = 2131627867(0x7f0e0f5b, float:1.888301E38)
            java.lang.String r12 = "SavedMessagesInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            r12 = 0
            r18 = 0
            r40 = r8
            r43 = r9
            r41 = r10
            r15 = r12
            r44 = r13
            r12 = r4
            goto L_0x1243
        L_0x0a2e:
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0a4b
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 != 0) goto L_0x0a4b
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x0a4b
            r4 = 0
            java.lang.CharSequence r12 = r56.formatArchivedDialogNames()
            r24 = r4
            r40 = r8
            r43 = r9
            r41 = r10
            r44 = r13
            goto L_0x1243
        L_0x0a4b:
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r4 == 0) goto L_0x0a7e
            org.telegram.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r4)
            if (r4 == 0) goto L_0x0a69
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x0a69
            java.lang.String r4 = ""
            r15 = 0
            goto L_0x0a6a
        L_0x0a69:
            r4 = r6
        L_0x0a6a:
            android.text.TextPaint[] r12 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            r23 = r4
            int r4 = r1.paintIndex
            r2 = r12[r4]
            r40 = r8
            r43 = r9
            r41 = r10
            r44 = r13
            r12 = r23
            goto L_0x1243
        L_0x0a7e:
            r4 = 1
            boolean r12 = android.text.TextUtils.isEmpty(r3)
            if (r12 == 0) goto L_0x0bb1
            int r12 = r1.currentDialogFolderId
            if (r12 != 0) goto L_0x0bb1
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = r1.encryptedChat
            if (r12 != 0) goto L_0x0bb1
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.needDrawBluredPreview()
            if (r12 != 0) goto L_0x0bb1
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isPhoto()
            if (r12 != 0) goto L_0x0ab6
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isNewGif()
            if (r12 != 0) goto L_0x0ab6
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            if (r12 == 0) goto L_0x0aae
            goto L_0x0ab6
        L_0x0aae:
            r40 = r4
            r41 = r10
            r44 = r13
            goto L_0x0bb7
        L_0x0ab6:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isWebpage()
            if (r12 == 0) goto L_0x0ac9
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            org.telegram.tgnet.TLRPC$WebPage r12 = r12.webpage
            java.lang.String r12 = r12.type
            goto L_0x0aca
        L_0x0ac9:
            r12 = 0
        L_0x0aca:
            r40 = r4
            java.lang.String r4 = "app"
            boolean r4 = r4.equals(r12)
            if (r4 != 0) goto L_0x0baa
            java.lang.String r4 = "profile"
            boolean r4 = r4.equals(r12)
            if (r4 != 0) goto L_0x0baa
            java.lang.String r4 = "article"
            boolean r4 = r4.equals(r12)
            if (r4 != 0) goto L_0x0baa
            if (r12 == 0) goto L_0x0af5
            java.lang.String r4 = "telegram_"
            boolean r4 = r12.startsWith(r4)
            if (r4 != 0) goto L_0x0aef
            goto L_0x0af5
        L_0x0aef:
            r41 = r10
            r44 = r13
            goto L_0x0bb7
        L_0x0af5:
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.photoThumbs
            r41 = r10
            r10 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r10)
            org.telegram.messenger.MessageObject r10 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r10.photoThumbs
            int r11 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r11)
            if (r4 != r10) goto L_0x0b10
            r10 = 0
        L_0x0b10:
            if (r4 == 0) goto L_0x0ba5
            r11 = 1
            r1.hasMessageThumb = r11
            org.telegram.messenger.MessageObject r11 = r1.message
            boolean r11 = r11.isVideo()
            r1.drawPlay = r11
            java.lang.String r11 = org.telegram.messenger.FileLoader.getAttachFileName(r10)
            r43 = r12
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.mediaExists
            if (r12 != 0) goto L_0x0b69
            int r12 = r1.currentAccount
            org.telegram.messenger.DownloadController r12 = org.telegram.messenger.DownloadController.getInstance(r12)
            r44 = r13
            org.telegram.messenger.MessageObject r13 = r1.message
            boolean r12 = r12.canDownloadMedia((org.telegram.messenger.MessageObject) r13)
            if (r12 != 0) goto L_0x0b6b
            int r12 = r1.currentAccount
            org.telegram.messenger.FileLoader r12 = org.telegram.messenger.FileLoader.getInstance(r12)
            boolean r12 = r12.isLoadingFile(r11)
            if (r12 == 0) goto L_0x0b46
            goto L_0x0b6b
        L_0x0b46:
            org.telegram.messenger.ImageReceiver r12 = r1.thumbImage
            r46 = 0
            r47 = 0
            org.telegram.messenger.MessageObject r13 = r1.message
            org.telegram.tgnet.TLObject r13 = r13.photoThumbsObject
            org.telegram.messenger.ImageLocation r48 = org.telegram.messenger.ImageLocation.getForObject(r4, r13)
            r13 = 0
            r50 = r13
            android.graphics.drawable.Drawable r50 = (android.graphics.drawable.Drawable) r50
            org.telegram.messenger.MessageObject r13 = r1.message
            r52 = 0
            java.lang.String r49 = "20_20"
            r45 = r12
            r51 = r13
            r45.setImage((org.telegram.messenger.ImageLocation) r46, (java.lang.String) r47, (org.telegram.messenger.ImageLocation) r48, (java.lang.String) r49, (android.graphics.drawable.Drawable) r50, (java.lang.Object) r51, (int) r52)
            r54 = r11
            goto L_0x0ba2
        L_0x0b69:
            r44 = r13
        L_0x0b6b:
            org.telegram.messenger.MessageObject r12 = r1.message
            int r12 = r12.type
            r13 = 1
            if (r12 != r13) goto L_0x0b79
            if (r10 == 0) goto L_0x0b77
            int r12 = r10.size
            goto L_0x0b78
        L_0x0b77:
            r12 = 0
        L_0x0b78:
            goto L_0x0b7a
        L_0x0b79:
            r12 = 0
        L_0x0b7a:
            org.telegram.messenger.ImageReceiver r13 = r1.thumbImage
            r54 = r11
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r46 = org.telegram.messenger.ImageLocation.getForObject(r10, r11)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r48 = org.telegram.messenger.ImageLocation.getForObject(r4, r11)
            r51 = 0
            org.telegram.messenger.MessageObject r11 = r1.message
            r53 = 0
            java.lang.String r47 = "20_20"
            java.lang.String r49 = "20_20"
            r45 = r13
            r50 = r12
            r52 = r11
            r45.setImage(r46, r47, r48, r49, r50, r51, r52, r53)
        L_0x0ba2:
            r11 = 0
            r4 = r11
            goto L_0x0bb9
        L_0x0ba5:
            r43 = r12
            r44 = r13
            goto L_0x0bb7
        L_0x0baa:
            r41 = r10
            r43 = r12
            r44 = r13
            goto L_0x0bb7
        L_0x0bb1:
            r40 = r4
            r41 = r10
            r44 = r13
        L_0x0bb7:
            r4 = r40
        L_0x0bb9:
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            if (r10 == 0) goto L_0x0var_
            long r10 = r10.id
            r12 = 0
            int r40 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r40 <= 0) goto L_0x0var_
            if (r9 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r10 == 0) goto L_0x0bde
            org.telegram.tgnet.TLRPC$Chat r10 = r1.chat
            boolean r10 = org.telegram.messenger.ChatObject.isMegagroup(r10)
            if (r10 == 0) goto L_0x0bd8
            goto L_0x0bde
        L_0x0bd8:
            r40 = r8
            r43 = r9
            goto L_0x0var_
        L_0x0bde:
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isOutOwner()
            if (r10 == 0) goto L_0x0bf1
            r10 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.String r11 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r10 = r0
            goto L_0x0c4e
        L_0x0bf1:
            org.telegram.messenger.MessageObject r10 = r1.message
            if (r10 == 0) goto L_0x0c0f
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r10.fwd_from
            if (r10 == 0) goto L_0x0c0f
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r10.fwd_from
            java.lang.String r10 = r10.from_name
            if (r10 == 0) goto L_0x0c0f
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r10.fwd_from
            java.lang.String r0 = r10.from_name
            r10 = r0
            goto L_0x0c4e
        L_0x0c0f:
            if (r8 == 0) goto L_0x0c4b
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x0CLASSNAME
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x0c1a
            goto L_0x0CLASSNAME
        L_0x0c1a:
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r8)
            java.lang.String r11 = "\n"
            java.lang.String r12 = ""
            java.lang.String r0 = r10.replace(r11, r12)
            r10 = r0
            goto L_0x0c4e
        L_0x0CLASSNAME:
            boolean r10 = org.telegram.messenger.UserObject.isDeleted(r8)
            if (r10 == 0) goto L_0x0CLASSNAME
            r10 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r11 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r10 = r0
            goto L_0x0c4e
        L_0x0CLASSNAME:
            java.lang.String r10 = r8.first_name
            java.lang.String r11 = r8.last_name
            java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
            java.lang.String r11 = "\n"
            java.lang.String r12 = ""
            java.lang.String r0 = r10.replace(r11, r12)
            r10 = r0
            goto L_0x0c4e
        L_0x0c4b:
            java.lang.String r0 = "DELETED"
            r10 = r0
        L_0x0c4e:
            r11 = 0
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0c6d
            r12 = 2
            java.lang.Object[] r0 = new java.lang.Object[r12]
            r12 = 0
            r0[r12] = r3
            r12 = 1
            r0[r12] = r10
            java.lang.String r0 = java.lang.String.format(r5, r0)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r40 = r8
            r43 = r9
            r9 = r0
            goto L_0x0ebd
        L_0x0c6d:
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            if (r0 == 0) goto L_0x0cf2
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            java.lang.String r0 = r0.toString()
            int r12 = r0.length()
            r13 = 150(0x96, float:2.1E-43)
            if (r12 <= r13) goto L_0x0CLASSNAME
            r12 = 0
            java.lang.CharSequence r0 = r0.subSequence(r12, r13)
        L_0x0CLASSNAME:
            if (r4 != 0) goto L_0x0c8d
            java.lang.String r12 = ""
            goto L_0x0cbb
        L_0x0c8d:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVideo()
            if (r12 == 0) goto L_0x0CLASSNAME
            java.lang.String r12 = "📹 "
            goto L_0x0cbb
        L_0x0CLASSNAME:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isVoice()
            if (r12 == 0) goto L_0x0ca3
            java.lang.String r12 = "🎤 "
            goto L_0x0cbb
        L_0x0ca3:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isMusic()
            if (r12 == 0) goto L_0x0cae
            java.lang.String r12 = "🎧 "
            goto L_0x0cbb
        L_0x0cae:
            org.telegram.messenger.MessageObject r12 = r1.message
            boolean r12 = r12.isPhoto()
            if (r12 == 0) goto L_0x0cb9
            java.lang.String r12 = "🖼 "
            goto L_0x0cbb
        L_0x0cb9:
            java.lang.String r12 = "📎 "
        L_0x0cbb:
            android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
            r13.<init>(r0)
            r23 = r0
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            r40 = r8
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.CharSequence r8 = r8.caption
            r43 = r9
            r9 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r0, r8, r13, r9)
            r8 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r8]
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r12)
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r13)
            android.text.SpannableStringBuilder r8 = r8.append(r9)
            r9 = 0
            r0[r9] = r8
            r8 = 1
            r0[r8] = r10
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r0)
            r9 = r0
            goto L_0x0ebd
        L_0x0cf2:
            r40 = r8
            r43 = r9
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0e27
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0e27
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r8 = r1.paintIndex
            r2 = r0[r8]
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0d47
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 18
            if (r8 < r9) goto L_0x0d35
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r12 = r0.poll
            java.lang.String r12 = r12.question
            r13 = 0
            r9[r13] = r12
            java.lang.String r12 = "📊 ⁨%s⁩"
            java.lang.String r9 = java.lang.String.format(r12, r9)
            goto L_0x0d45
        L_0x0d35:
            r8 = 1
            r13 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r8 = r0.poll
            java.lang.String r8 = r8.question
            r9[r13] = r8
            java.lang.String r8 = "📊 %s"
            java.lang.String r9 = java.lang.String.format(r8, r9)
        L_0x0d45:
            goto L_0x0de3
        L_0x0d47:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0d86
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 18
            if (r0 < r8) goto L_0x0d6f
            r8 = 1
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
            org.telegram.tgnet.TLRPC$TL_game r9 = r9.game
            java.lang.String r9 = r9.title
            r12 = 0
            r0[r12] = r9
            java.lang.String r9 = "🎮 ⁨%s⁩"
            java.lang.String r9 = java.lang.String.format(r9, r0)
            goto L_0x0de3
        L_0x0d6f:
            r8 = 1
            r12 = 0
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            java.lang.String r8 = r8.title
            r0[r12] = r8
            java.lang.String r8 = "🎮 %s"
            java.lang.String r9 = java.lang.String.format(r8, r0)
            goto L_0x0de3
        L_0x0d86:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0d99
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r9 = r0.title
            goto L_0x0de3
        L_0x0d99:
            org.telegram.messenger.MessageObject r0 = r1.message
            int r0 = r0.type
            r8 = 14
            if (r0 != r8) goto L_0x0ddf
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 18
            if (r0 < r8) goto L_0x0dc3
            r8 = 2
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.getMusicAuthor()
            r12 = 0
            r0[r12] = r9
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.getMusicTitle()
            r13 = 1
            r0[r13] = r9
            java.lang.String r9 = "🎧 ⁨%s - %s⁩"
            java.lang.String r9 = java.lang.String.format(r9, r0)
            goto L_0x0de3
        L_0x0dc3:
            r8 = 2
            r12 = 0
            r13 = 1
            java.lang.Object[] r0 = new java.lang.Object[r8]
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.getMusicAuthor()
            r0[r12] = r8
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.getMusicTitle()
            r0[r13] = r8
            java.lang.String r8 = "🎧 %s - %s"
            java.lang.String r9 = java.lang.String.format(r8, r0)
            goto L_0x0de3
        L_0x0ddf:
            java.lang.String r9 = r6.toString()
        L_0x0de3:
            r0 = 10
            r8 = 32
            java.lang.String r8 = r9.replace(r0, r8)
            r9 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r9]
            r9 = 0
            r0[r9] = r8
            r9 = 1
            r0[r9] = r10
            android.text.SpannableStringBuilder r9 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0e1d }
            java.lang.String r12 = "chats_attachMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider     // Catch:{ Exception -> 0x0e1d }
            r0.<init>(r12, r13)     // Catch:{ Exception -> 0x0e1d }
            if (r22 == 0) goto L_0x0e0e
            int r12 = r10.length()     // Catch:{ Exception -> 0x0e0a }
            r13 = 2
            int r12 = r12 + r13
            goto L_0x0e0f
        L_0x0e0a:
            r0 = move-exception
            r23 = r2
            goto L_0x0e20
        L_0x0e0e:
            r12 = 0
        L_0x0e0f:
            int r13 = r9.length()     // Catch:{ Exception -> 0x0e1d }
            r23 = r2
            r2 = 33
            r9.setSpan(r0, r12, r13, r2)     // Catch:{ Exception -> 0x0e1b }
            goto L_0x0e23
        L_0x0e1b:
            r0 = move-exception
            goto L_0x0e20
        L_0x0e1d:
            r0 = move-exception
            r23 = r2
        L_0x0e20:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0e23:
            r2 = r23
            goto L_0x0ebd
        L_0x0e27:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0eb6
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.hasHighlightedWords()
            if (r8 == 0) goto L_0x0e86
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r8 = r8.messageTrimmedToHighlight
            if (r8 == 0) goto L_0x0e47
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.String r0 = r8.messageTrimmedToHighlight
        L_0x0e47:
            int r8 = r56.getMeasuredWidth()
            r9 = 1121058816(0x42d20000, float:105.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            if (r22 == 0) goto L_0x0e6e
            boolean r9 = android.text.TextUtils.isEmpty(r10)
            if (r9 != 0) goto L_0x0e65
            float r9 = (float) r8
            java.lang.String r12 = r10.toString()
            float r12 = r2.measureText(r12)
            float r9 = r9 - r12
            int r8 = (int) r9
        L_0x0e65:
            float r9 = (float) r8
            java.lang.String r12 = ": "
            float r12 = r2.measureText(r12)
            float r9 = r9 - r12
            int r8 = (int) r9
        L_0x0e6e:
            if (r8 <= 0) goto L_0x0e85
            org.telegram.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<java.lang.String> r9 = r9.highlightedWords
            r12 = 0
            java.lang.Object r9 = r9.get(r12)
            java.lang.String r9 = (java.lang.String) r9
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r9, r8, r2, r12)
            java.lang.String r0 = r9.toString()
        L_0x0e85:
            goto L_0x0e97
        L_0x0e86:
            int r8 = r0.length()
            r9 = 150(0x96, float:2.1E-43)
            if (r8 <= r9) goto L_0x0e93
            r8 = 0
            java.lang.CharSequence r0 = r0.subSequence(r8, r9)
        L_0x0e93:
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)
        L_0x0e97:
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            r8.<init>(r0)
            r0 = r8
            org.telegram.messenger.MessageObject r8 = r1.message
            r9 = r0
            android.text.Spannable r9 = (android.text.Spannable) r9
            r12 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r8, (android.text.Spannable) r9, (int) r12)
            r8 = 2
            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r8]
            r8 = 0
            r9[r8] = r0
            r8 = 1
            r9[r8] = r10
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r5, r9)
            r9 = r0
            goto L_0x0ebd
        L_0x0eb6:
            java.lang.String r0 = ""
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r9 = r0
        L_0x0ebd:
            r8 = 0
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0ec6
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0ed0
        L_0x0ec6:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0ef3
            int r0 = r9.length()
            if (r0 <= 0) goto L_0x0ef3
        L_0x0ed0:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0eec }
            java.lang.String r12 = "chats_nameMessage"
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r1.resourcesProvider     // Catch:{ Exception -> 0x0eec }
            r0.<init>(r12, r13)     // Catch:{ Exception -> 0x0eec }
            int r12 = r10.length()     // Catch:{ Exception -> 0x0eec }
            r13 = 1
            int r12 = r12 + r13
            r8 = r12
            r23 = r2
            r2 = 0
            r13 = 33
            r9.setSpan(r0, r2, r12, r13)     // Catch:{ Exception -> 0x0eea }
            r14 = r8
            goto L_0x0ef5
        L_0x0eea:
            r0 = move-exception
            goto L_0x0eef
        L_0x0eec:
            r0 = move-exception
            r23 = r2
        L_0x0eef:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0ef5
        L_0x0ef3:
            r23 = r2
        L_0x0ef5:
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r2 = r1.paintIndex
            r0 = r0[r2]
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r12 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r9, r0, r2, r12)
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x0f1d
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r1.resourcesProvider
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r12)
            if (r2 == 0) goto L_0x0f1d
            r0 = r2
        L_0x0f1d:
            boolean r2 = r1.hasMessageThumb
            if (r2 == 0) goto L_0x0f4a
            boolean r2 = r0 instanceof android.text.SpannableStringBuilder
            if (r2 != 0) goto L_0x0f2b
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r0 = r2
        L_0x0f2b:
            r2 = 0
            r11 = r0
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r11.insert(r8, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r13 = r7 + 6
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.<init>(r13)
            int r13 = r8 + 1
            r24 = r2
            r2 = 33
            r11.setSpan(r12, r8, r13, r2)
            goto L_0x0f4c
        L_0x0f4a:
            r24 = r11
        L_0x0f4c:
            r12 = r0
            r0 = r10
            r2 = r23
            goto L_0x1243
        L_0x0var_:
            r40 = r8
            r43 = r9
        L_0x0var_:
            boolean r8 = android.text.TextUtils.isEmpty(r3)
            if (r8 != 0) goto L_0x0var_
            r8 = r3
            r9 = r8
            r8 = 2
            goto L_0x1189
        L_0x0var_:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0f8e
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r8 == 0) goto L_0x0f8e
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            int r8 = r8.ttl_seconds
            if (r8 == 0) goto L_0x0f8e
            r8 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r9 = "AttachPhotoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = r8
            r8 = 2
            goto L_0x1189
        L_0x0f8e:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r8 == 0) goto L_0x0fbb
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$Document r8 = r8.document
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
            if (r8 == 0) goto L_0x0fbb
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            int r8 = r8.ttl_seconds
            if (r8 == 0) goto L_0x0fbb
            r8 = 2131624450(0x7f0e0202, float:1.887608E38)
            java.lang.String r9 = "AttachVideoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = r8
            r8 = 2
            goto L_0x1189
        L_0x0fbb:
            org.telegram.messenger.MessageObject r8 = r1.message
            java.lang.CharSequence r8 = r8.caption
            if (r8 == 0) goto L_0x108b
            if (r4 != 0) goto L_0x0fc6
            java.lang.String r8 = ""
            goto L_0x0ff4
        L_0x0fc6:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVideo()
            if (r8 == 0) goto L_0x0fd1
            java.lang.String r8 = "📹 "
            goto L_0x0ff4
        L_0x0fd1:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isVoice()
            if (r8 == 0) goto L_0x0fdc
            java.lang.String r8 = "🎤 "
            goto L_0x0ff4
        L_0x0fdc:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isMusic()
            if (r8 == 0) goto L_0x0fe7
            java.lang.String r8 = "🎧 "
            goto L_0x0ff4
        L_0x0fe7:
            org.telegram.messenger.MessageObject r8 = r1.message
            boolean r8 = r8.isPhoto()
            if (r8 == 0) goto L_0x0ff2
            java.lang.String r8 = "🖼 "
            goto L_0x0ff4
        L_0x0ff2:
            java.lang.String r8 = "📎 "
        L_0x0ff4:
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x1065
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x1065
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x1016
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x1016:
            int r10 = r56.getMeasuredWidth()
            r11 = 1122893824(0x42ee0000, float:119.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            if (r22 == 0) goto L_0x103d
            boolean r11 = android.text.TextUtils.isEmpty(r0)
            if (r11 != 0) goto L_0x1034
            float r11 = (float) r10
            java.lang.String r12 = r0.toString()
            float r12 = r2.measureText(r12)
            float r11 = r11 - r12
            int r10 = (int) r11
        L_0x1034:
            float r11 = (float) r10
            java.lang.String r12 = ": "
            float r12 = r2.measureText(r12)
            float r11 = r11 - r12
            int r10 = (int) r11
        L_0x103d:
            if (r10 <= 0) goto L_0x1054
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<java.lang.String> r11 = r11.highlightedWords
            r12 = 0
            java.lang.Object r11 = r11.get(r12)
            java.lang.String r11 = (java.lang.String) r11
            r12 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r9, r11, r10, r2, r12)
            java.lang.String r9 = r11.toString()
        L_0x1054:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r8 = r9
            goto L_0x1087
        L_0x1065:
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
            r10.<init>(r8)
            android.text.SpannableStringBuilder r10 = r10.append(r9)
            r8 = r10
        L_0x1087:
            r9 = r8
            r8 = 2
            goto L_0x1189
        L_0x108b:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r8 == 0) goto L_0x10b6
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r8 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r8
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "📊 "
            r9.append(r10)
            org.telegram.tgnet.TLRPC$Poll r10 = r8.poll
            java.lang.String r10 = r10.question
            r9.append(r10)
            java.lang.String r8 = r9.toString()
            r9 = r8
            r8 = 2
            goto L_0x1173
        L_0x10b6:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r8 == 0) goto L_0x10df
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "🎮 "
            r8.append(r9)
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
            org.telegram.tgnet.TLRPC$TL_game r9 = r9.game
            java.lang.String r9 = r9.title
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r9 = r8
            r8 = 2
            goto L_0x1173
        L_0x10df:
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r8 == 0) goto L_0x10f5
            org.telegram.messenger.MessageObject r8 = r1.message
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            java.lang.String r8 = r8.title
            r9 = r8
            r8 = 2
            goto L_0x1173
        L_0x10f5:
            org.telegram.messenger.MessageObject r8 = r1.message
            int r8 = r8.type
            r9 = 14
            if (r8 != r9) goto L_0x1119
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
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
            goto L_0x1173
        L_0x1119:
            r8 = 2
            org.telegram.messenger.MessageObject r9 = r1.message
            boolean r9 = r9.hasHighlightedWords()
            if (r9 == 0) goto L_0x115d
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x115d
            org.telegram.messenger.MessageObject r9 = r1.message
            java.lang.String r9 = r9.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x113c
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x113c:
            int r10 = r56.getMeasuredWidth()
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
            goto L_0x116a
        L_0x115d:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            r9.<init>(r6)
            org.telegram.messenger.MessageObject r10 = r1.message
            r11 = 256(0x100, float:3.59E-43)
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r10, (android.text.Spannable) r9, (int) r11)
            r10 = r9
        L_0x116a:
            org.telegram.messenger.MessageObject r10 = r1.message
            java.util.ArrayList<java.lang.String> r10 = r10.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r1.resourcesProvider
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r9, (java.util.ArrayList<java.lang.String>) r10, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
        L_0x1173:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            if (r10 == 0) goto L_0x1189
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.isMediaEmpty()
            if (r10 != 0) goto L_0x1189
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePrintingPaint
            int r11 = r1.paintIndex
            r2 = r10[r11]
        L_0x1189:
            boolean r10 = r1.hasMessageThumb
            if (r10 == 0) goto L_0x1240
            org.telegram.messenger.MessageObject r10 = r1.message
            boolean r10 = r10.hasHighlightedWords()
            if (r10 == 0) goto L_0x11d3
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x11d3
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r10 = r10.messageTrimmedToHighlight
            if (r10 == 0) goto L_0x11af
            org.telegram.messenger.MessageObject r10 = r1.message
            java.lang.String r9 = r10.messageTrimmedToHighlight
        L_0x11af:
            int r10 = r56.getMeasuredWidth()
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
            goto L_0x11e4
        L_0x11d3:
            int r10 = r9.length()
            r11 = 150(0x96, float:2.1E-43)
            if (r10 <= r11) goto L_0x11e0
            r10 = 0
            java.lang.CharSequence r9 = r9.subSequence(r10, r11)
        L_0x11e0:
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
        L_0x11e4:
            boolean r10 = r9 instanceof android.text.SpannableStringBuilder
            if (r10 != 0) goto L_0x11ee
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r9)
            r9 = r10
        L_0x11ee:
            r10 = 0
            r11 = r9
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11
            java.lang.String r12 = " "
            r13 = 0
            r11.insert(r13, r12)
            org.telegram.ui.Cells.DialogCell$FixedWidthSpan r12 = new org.telegram.ui.Cells.DialogCell$FixedWidthSpan
            int r8 = r7 + 6
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12.<init>(r8)
            r23 = r2
            r2 = 1
            r8 = 33
            r11.setSpan(r12, r13, r2, r8)
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r8 = r1.paintIndex
            r2 = r2[r8]
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r8 = 1099431936(0x41880000, float:17.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.messenger.Emoji.replaceEmoji(r11, r2, r12, r13)
            org.telegram.messenger.MessageObject r2 = r1.message
            boolean r2 = r2.hasHighlightedWords()
            if (r2 == 0) goto L_0x123a
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r11, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            if (r2 == 0) goto L_0x123a
            r8 = r2
            r12 = r8
            r24 = r10
            r2 = r23
            goto L_0x1243
        L_0x123a:
            r12 = r9
            r24 = r10
            r2 = r23
            goto L_0x1243
        L_0x1240:
            r23 = r2
            r12 = r9
        L_0x1243:
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x1252
            java.lang.CharSequence r0 = r56.formatArchivedDialogNames()
            r3 = r24
            r4 = r26
            r13 = r44
            goto L_0x1258
        L_0x1252:
            r3 = r24
            r4 = r26
            r13 = r44
        L_0x1258:
            org.telegram.tgnet.TLRPC$DraftMessage r8 = r1.draftMessage
            if (r8 == 0) goto L_0x1264
            int r8 = r8.date
            long r8 = (long) r8
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x127e
        L_0x1264:
            int r8 = r1.lastMessageDate
            if (r8 == 0) goto L_0x126e
            long r8 = (long) r8
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x127e
        L_0x126e:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x127c
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.date
            long r8 = (long) r8
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            goto L_0x127e
        L_0x127c:
            r9 = r36
        L_0x127e:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 != 0) goto L_0x1299
            r8 = 0
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
            r1.drawClock = r8
            r1.drawCount = r8
            r1.drawMention = r8
            r1.drawReactionMention = r8
            r1.drawError = r8
            r23 = r0
            r11 = r29
            r10 = r37
            goto L_0x13cc
        L_0x1299:
            int r10 = r1.currentDialogFolderId
            if (r10 == 0) goto L_0x12ed
            int r8 = r1.unreadCount
            int r10 = r1.mentionCount
            int r11 = r8 + r10
            if (r11 <= 0) goto L_0x12de
            if (r8 <= r10) goto L_0x12c5
            r11 = 1
            r1.drawCount = r11
            r11 = 0
            r1.drawMention = r11
            r23 = r0
            r11 = 1
            java.lang.Object[] r0 = new java.lang.Object[r11]
            int r8 = r8 + r10
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r10 = 0
            r0[r10] = r8
            java.lang.String r8 = "%d"
            java.lang.String r0 = java.lang.String.format(r8, r0)
            r37 = r0
            r11 = r29
            goto L_0x12e7
        L_0x12c5:
            r23 = r0
            r11 = 0
            r1.drawCount = r11
            r11 = 1
            r1.drawMention = r11
            java.lang.Object[] r0 = new java.lang.Object[r11]
            int r8 = r8 + r10
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r10 = 0
            r0[r10] = r8
            java.lang.String r8 = "%d"
            java.lang.String r11 = java.lang.String.format(r8, r0)
            goto L_0x12e7
        L_0x12de:
            r23 = r0
            r10 = 0
            r1.drawCount = r10
            r1.drawMention = r10
            r11 = r29
        L_0x12e7:
            r1.drawReactionMention = r10
            r10 = r37
            goto L_0x1350
        L_0x12ed:
            r23 = r0
            r10 = 0
            boolean r0 = r1.clearingDialog
            if (r0 == 0) goto L_0x12fb
            r1.drawCount = r10
            r0 = 0
            r15 = r0
            r8 = 1
            r10 = 0
            goto L_0x1338
        L_0x12fb:
            int r0 = r1.unreadCount
            if (r0 == 0) goto L_0x1327
            r10 = 1
            if (r0 != r10) goto L_0x130e
            int r10 = r1.mentionCount
            if (r0 != r10) goto L_0x130e
            if (r8 == 0) goto L_0x130e
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            boolean r0 = r0.mentioned
            if (r0 != 0) goto L_0x1327
        L_0x130e:
            r8 = 1
            r1.drawCount = r8
            java.lang.Object[] r0 = new java.lang.Object[r8]
            int r8 = r1.unreadCount
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r10 = 0
            r0[r10] = r8
            java.lang.String r8 = "%d"
            java.lang.String r10 = java.lang.String.format(r8, r0)
            r37 = r10
            r8 = 1
            r10 = 0
            goto L_0x1338
        L_0x1327:
            boolean r0 = r1.markUnread
            if (r0 == 0) goto L_0x1334
            r8 = 1
            r1.drawCount = r8
            java.lang.String r10 = ""
            r37 = r10
            r10 = 0
            goto L_0x1338
        L_0x1334:
            r8 = 1
            r10 = 0
            r1.drawCount = r10
        L_0x1338:
            int r0 = r1.mentionCount
            if (r0 == 0) goto L_0x1341
            r1.drawMention = r8
            java.lang.String r11 = "@"
            goto L_0x1345
        L_0x1341:
            r1.drawMention = r10
            r11 = r29
        L_0x1345:
            int r0 = r1.reactionMentionCount
            if (r0 <= 0) goto L_0x134c
            r1.drawReactionMention = r8
            goto L_0x134e
        L_0x134c:
            r1.drawReactionMention = r10
        L_0x134e:
            r10 = r37
        L_0x1350:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isOut()
            if (r0 == 0) goto L_0x13c3
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 != 0) goto L_0x13c3
            if (r15 == 0) goto L_0x13c3
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 != 0) goto L_0x13c3
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSending()
            if (r0 == 0) goto L_0x137c
            r8 = 0
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
            r8 = 1
            r1.drawClock = r8
            r8 = 0
            r1.drawError = r8
            goto L_0x13cc
        L_0x137c:
            r8 = 0
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSendError()
            if (r0 == 0) goto L_0x1394
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
            r1.drawClock = r8
            r8 = 1
            r1.drawError = r8
            r8 = 0
            r1.drawCount = r8
            r1.drawMention = r8
            goto L_0x13cc
        L_0x1394:
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSent()
            if (r0 == 0) goto L_0x13c1
            org.telegram.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isUnread()
            if (r0 == 0) goto L_0x13b5
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x13b3
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x13b3
            goto L_0x13b5
        L_0x13b3:
            r0 = 0
            goto L_0x13b6
        L_0x13b5:
            r0 = 1
        L_0x13b6:
            r1.drawCheck1 = r0
            r8 = 1
            r1.drawCheck2 = r8
            r8 = 0
            r1.drawClock = r8
            r1.drawError = r8
            goto L_0x13cc
        L_0x13c1:
            r8 = 0
            goto L_0x13cc
        L_0x13c3:
            r8 = 0
            r1.drawCheck1 = r8
            r1.drawCheck2 = r8
            r1.drawClock = r8
            r1.drawError = r8
        L_0x13cc:
            r1.promoDialog = r8
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r8 = r1.dialogsType
            if (r8 != 0) goto L_0x143a
            r8 = r2
            r24 = r3
            long r2 = r1.currentDialogId
            r26 = r4
            r4 = 1
            boolean r2 = r0.isPromoDialog(r2, r4)
            if (r2 == 0) goto L_0x143f
            r1.drawPinBackground = r4
            r1.promoDialog = r4
            int r2 = r0.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PROXY
            if (r2 != r3) goto L_0x13fc
            r2 = 2131628558(0x7f0e120e, float:1.8884412E38)
            java.lang.String r3 = "UseProxySponsor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r4 = r2
            r3 = r12
            goto L_0x1441
        L_0x13fc:
            int r2 = r0.promoDialogType
            int r3 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r2 != r3) goto L_0x143f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "PsaType_"
            r2.append(r3)
            java.lang.String r3 = r0.promoPsaType
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((java.lang.String) r2)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x1428
            r3 = 2131627571(0x7f0e0e33, float:1.888241E38)
            java.lang.String r4 = "PsaTypeDefault"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x1428:
            java.lang.String r3 = r0.promoPsaMessage
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1437
            java.lang.String r3 = r0.promoPsaMessage
            r4 = 0
            r1.hasMessageThumb = r4
            r4 = r2
            goto L_0x1441
        L_0x1437:
            r4 = r2
            r3 = r12
            goto L_0x1441
        L_0x143a:
            r8 = r2
            r24 = r3
            r26 = r4
        L_0x143f:
            r4 = r9
            r3 = r12
        L_0x1441:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x1457
            r2 = 2131624349(0x7f0e019d, float:1.8875875E38)
            java.lang.String r9 = "ArchivedChats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12 = r11
            r9 = r26
            r11 = r10
            r10 = r2
            r2 = r23
            goto L_0x14c1
        L_0x1457:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.chat
            if (r2 == 0) goto L_0x145e
            java.lang.String r2 = r2.title
            goto L_0x14a3
        L_0x145e:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            if (r2 == 0) goto L_0x14a1
            boolean r2 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r2)
            if (r2 == 0) goto L_0x1472
            r2 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x14a3
        L_0x1472:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r2 == 0) goto L_0x149a
            boolean r2 = r1.useMeForMyMessages
            if (r2 == 0) goto L_0x1488
            r2 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.String r9 = "FromYou"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x14a3
        L_0x1488:
            int r2 = r1.dialogsType
            r9 = 3
            if (r2 != r9) goto L_0x1490
            r2 = 1
            r1.drawPinBackground = r2
        L_0x1490:
            r2 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            goto L_0x14a3
        L_0x149a:
            org.telegram.tgnet.TLRPC$User r2 = r1.user
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x14a3
        L_0x14a1:
            r2 = r28
        L_0x14a3:
            int r9 = r2.length()
            if (r9 != 0) goto L_0x14ba
            r9 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r12 = "HiddenName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r12 = r11
            r9 = r26
            r11 = r10
            r10 = r2
            r2 = r23
            goto L_0x14c1
        L_0x14ba:
            r12 = r11
            r9 = r26
            r11 = r10
            r10 = r2
            r2 = r23
        L_0x14c1:
            if (r18 == 0) goto L_0x1507
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r4)
            r23 = r5
            r26 = r6
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r38 = org.telegram.ui.ActionBar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r40 = android.text.Layout.Alignment.ALIGN_NORMAL
            r41 = 1065353216(0x3var_, float:1.0)
            r42 = 0
            r43 = 0
            r36 = r5
            r37 = r4
            r39 = r0
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)
            r1.timeLayout = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x14fd
            int r5 = r56.getMeasuredWidth()
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r5 = r5 - r0
            r1.timeLeft = r5
            goto L_0x1505
        L_0x14fd:
            r6 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.timeLeft = r5
        L_0x1505:
            r5 = r0
            goto L_0x1513
        L_0x1507:
            r23 = r5
            r26 = r6
            r0 = 0
            r5 = 0
            r1.timeLayout = r5
            r5 = 0
            r1.timeLeft = r5
            r5 = r0
        L_0x1513:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x1527
            int r0 = r56.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r0 = r0 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            int r0 = r0 - r5
            goto L_0x153b
        L_0x1527:
            int r0 = r56.getMeasuredWidth()
            int r6 = r1.nameLeft
            int r0 = r0 - r6
            r6 = 1117388800(0x429a0000, float:77.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r6
            int r0 = r0 - r5
            int r6 = r1.nameLeft
            int r6 = r6 + r5
            r1.nameLeft = r6
        L_0x153b:
            boolean r6 = r1.drawNameLock
            if (r6 == 0) goto L_0x154f
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r17 = r17.getIntrinsicWidth()
            int r6 = r6 + r17
            int r0 = r0 - r6
            goto L_0x158a
        L_0x154f:
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x1563
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r17 = r17.getIntrinsicWidth()
            int r6 = r6 + r17
            int r0 = r0 - r6
            goto L_0x158a
        L_0x1563:
            boolean r6 = r1.drawNameBroadcast
            if (r6 == 0) goto L_0x1577
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r17 = r17.getIntrinsicWidth()
            int r6 = r6 + r17
            int r0 = r0 - r6
            goto L_0x158a
        L_0x1577:
            boolean r6 = r1.drawNameBot
            if (r6 == 0) goto L_0x158a
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r17 = r17.getIntrinsicWidth()
            int r6 = r6 + r17
            int r0 = r0 - r6
        L_0x158a:
            boolean r6 = r1.drawClock
            r17 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x15c1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_clockDrawable
            int r6 = r6.getIntrinsicWidth()
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 + r27
            int r0 = r0 - r6
            boolean r27 = org.telegram.messenger.LocaleController.isRTL
            if (r27 != 0) goto L_0x15a9
            r27 = r0
            int r0 = r1.timeLeft
            int r0 = r0 - r6
            r1.clockDrawLeft = r0
            goto L_0x15bb
        L_0x15a9:
            r27 = r0
            int r0 = r1.timeLeft
            int r0 = r0 + r5
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r17
            r1.clockDrawLeft = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r6
            r1.nameLeft = r0
        L_0x15bb:
            r0 = r27
            r27 = r4
            goto L_0x1644
        L_0x15c1:
            boolean r6 = r1.drawCheck2
            if (r6 == 0) goto L_0x1642
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_checkDrawable
            int r6 = r6.getIntrinsicWidth()
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r6 = r6 + r27
            int r0 = r0 - r6
            r27 = r4
            boolean r4 = r1.drawCheck1
            if (r4 == 0) goto L_0x1627
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_halfCheckDrawable
            int r4 = r4.getIntrinsicWidth()
            r28 = 1090519040(0x41000000, float:8.0)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r28)
            int r4 = r4 - r28
            int r0 = r0 - r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x15fb
            int r4 = r1.timeLeft
            int r4 = r4 - r6
            r1.halfCheckDrawLeft = r4
            r17 = 1085276160(0x40b00000, float:5.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 - r17
            r1.checkDrawLeft = r4
            goto L_0x1644
        L_0x15fb:
            int r4 = r1.timeLeft
            int r4 = r4 + r5
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
            int r17 = r6 + r17
            r28 = 1090519040(0x41000000, float:8.0)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r28)
            int r17 = r17 - r28
            int r4 = r4 + r17
            r1.nameLeft = r4
            goto L_0x1644
        L_0x1627:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x1631
            int r4 = r1.timeLeft
            int r4 = r4 - r6
            r1.checkDrawLeft1 = r4
            goto L_0x1644
        L_0x1631:
            int r4 = r1.timeLeft
            int r4 = r4 + r5
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r17
            r1.checkDrawLeft1 = r4
            int r4 = r1.nameLeft
            int r4 = r4 + r6
            r1.nameLeft = r4
            goto L_0x1644
        L_0x1642:
            r27 = r4
        L_0x1644:
            boolean r4 = r1.dialogMuted
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r4 == 0) goto L_0x166b
            boolean r4 = r1.drawVerified
            if (r4 != 0) goto L_0x166b
            int r4 = r1.drawScam
            if (r4 != 0) goto L_0x166b
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r17 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r17 = r17.getIntrinsicWidth()
            int r4 = r4 + r17
            int r0 = r0 - r4
            boolean r17 = org.telegram.messenger.LocaleController.isRTL
            if (r17 == 0) goto L_0x1668
            int r6 = r1.nameLeft
            int r6 = r6 + r4
            r1.nameLeft = r6
        L_0x1668:
            r28 = r5
            goto L_0x16b1
        L_0x166b:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x1689
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r4 = r4.getIntrinsicWidth()
            int r6 = r6 + r4
            int r0 = r0 - r6
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1686
            int r4 = r1.nameLeft
            int r4 = r4 + r6
            r1.nameLeft = r4
        L_0x1686:
            r28 = r5
            goto L_0x16b1
        L_0x1689:
            int r4 = r1.drawScam
            if (r4 == 0) goto L_0x16af
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r1.drawScam
            r28 = r5
            r5 = 1
            if (r4 != r5) goto L_0x169d
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x169f
        L_0x169d:
            org.telegram.ui.Components.ScamDrawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x169f:
            int r4 = r4.getIntrinsicWidth()
            int r6 = r6 + r4
            int r0 = r0 - r6
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x16b1
            int r4 = r1.nameLeft
            int r4 = r4 + r6
            r1.nameLeft = r4
            goto L_0x16b1
        L_0x16af:
            r28 = r5
        L_0x16b1:
            r4 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = java.lang.Math.max(r5, r0)
            r0 = 10
            r6 = 32
            java.lang.String r0 = r10.replace(r0, r6)     // Catch:{ Exception -> 0x170f }
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x170f }
            int r4 = r1.paintIndex     // Catch:{ Exception -> 0x170f }
            r4 = r6[r4]     // Catch:{ Exception -> 0x170f }
            r6 = 1094713344(0x41400000, float:12.0)
            int r32 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x170f }
            int r6 = r5 - r32
            float r6 = (float) r6
            r32 = r10
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x170d }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r4, r6, r10)     // Catch:{ Exception -> 0x170d }
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x170d }
            if (r4 == 0) goto L_0x16f1
            boolean r4 = r4.hasHighlightedWords()     // Catch:{ Exception -> 0x170d }
            if (r4 == 0) goto L_0x16f1
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x170d }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x170d }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider     // Catch:{ Exception -> 0x170d }
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)     // Catch:{ Exception -> 0x170d }
            if (r4 == 0) goto L_0x16f1
            r0 = r4
        L_0x16f1:
            android.text.StaticLayout r4 = new android.text.StaticLayout     // Catch:{ Exception -> 0x170d }
            android.text.TextPaint[] r6 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x170d }
            int r10 = r1.paintIndex     // Catch:{ Exception -> 0x170d }
            r38 = r6[r10]     // Catch:{ Exception -> 0x170d }
            android.text.Layout$Alignment r40 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x170d }
            r41 = 1065353216(0x3var_, float:1.0)
            r42 = 0
            r43 = 0
            r36 = r4
            r37 = r0
            r39 = r5
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x170d }
            r1.nameLayout = r4     // Catch:{ Exception -> 0x170d }
            goto L_0x1715
        L_0x170d:
            r0 = move-exception
            goto L_0x1712
        L_0x170f:
            r0 = move-exception
            r32 = r10
        L_0x1712:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1715:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x17da
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1725
            r45 = r9
            r31 = r13
            r44 = r15
            goto L_0x17e0
        L_0x1725:
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
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.checkDrawTop = r6
            int r4 = r56.getMeasuredWidth()
            r6 = 1119748096(0x42be0000, float:95.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1787
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r31)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r56.getMeasuredWidth()
            r10 = 1115684864(0x42800000, float:64.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r6 = r6 - r10
            int r10 = r7 + 11
            float r10 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r6 - r10
            goto L_0x179c
        L_0x1787:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r33)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10 = 1116078080(0x42860000, float:67.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r6
        L_0x179c:
            r19 = r4
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r31 = r13
            float r13 = (float) r6
            r33 = r6
            float r6 = (float) r0
            r34 = 1113063424(0x42580000, float:54.0)
            r44 = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r34)
            float r15 = (float) r15
            r45 = r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r34)
            float r9 = (float) r9
            r4.setImageCoords(r13, r6, r15, r9)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r10
            r9 = 1106247680(0x41var_, float:30.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            float r13 = (float) r7
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r15 = (float) r7
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r4.setImageCoords(r6, r9, r13, r15)
            r4 = r0
            r6 = r33
            r33 = r19
            goto L_0x188c
        L_0x17da:
            r45 = r9
            r31 = r13
            r44 = r15
        L_0x17e0:
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
            int r4 = r56.getMeasuredWidth()
            r6 = 1119485952(0x42ba0000, float:93.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1844
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            int r6 = r56.getMeasuredWidth()
            r9 = 1115947008(0x42840000, float:66.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = r6 - r9
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r6 - r9
            r10 = r9
            goto L_0x185a
        L_0x1844:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r34)
            r1.messageNameLeft = r6
            r1.messageLeft = r6
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 1116340224(0x428a0000, float:69.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r6
            r10 = r9
        L_0x185a:
            org.telegram.messenger.ImageReceiver r9 = r1.avatarImage
            float r13 = (float) r6
            float r15 = (float) r0
            r19 = 1113587712(0x42600000, float:56.0)
            r33 = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            r34 = r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r6 = (float) r6
            r9.setImageCoords(r13, r15, r4, r6)
            org.telegram.messenger.ImageReceiver r4 = r1.thumbImage
            float r6 = (float) r10
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r0
            float r9 = (float) r9
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r35)
            float r13 = (float) r13
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            float r15 = (float) r15
            r4.setImageCoords(r6, r9, r13, r15)
            r4 = r0
            r6 = r34
        L_0x188c:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x18b1
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x18a9
            int r0 = r56.getMeasuredWidth()
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r9 = r9.getIntrinsicWidth()
            int r0 = r0 - r9
            r9 = 1096810496(0x41600000, float:14.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r9
            r1.pinLeft = r0
            goto L_0x18b1
        L_0x18a9:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.pinLeft = r0
        L_0x18b1:
            boolean r0 = r1.drawError
            if (r0 == 0) goto L_0x18e6
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r33 = r33 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x18cf
            int r9 = r56.getMeasuredWidth()
            r13 = 1107820544(0x42080000, float:34.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r9 = r9 - r13
            r1.errorLeft = r9
            goto L_0x18e1
        L_0x18cf:
            r9 = 1093664768(0x41300000, float:11.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.errorLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x18e1:
            r13 = r10
            r0 = r33
            goto L_0x1a60
        L_0x18e6:
            if (r11 != 0) goto L_0x191a
            if (r12 != 0) goto L_0x191a
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x18ef
            goto L_0x191a
        L_0x18ef:
            boolean r0 = r1.drawPin
            if (r0 == 0) goto L_0x1910
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_pinnedDrawable
            int r0 = r0.getIntrinsicWidth()
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 + r9
            int r33 = r33 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x1910
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x1910:
            r9 = 0
            r1.drawCount = r9
            r1.drawMention = r9
            r13 = r10
            r0 = r33
            goto L_0x1a60
        L_0x191a:
            if (r11 == 0) goto L_0x197e
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r11)
            r13 = r10
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r0 = java.lang.Math.max(r0, r9)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r38 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.countWidth
            android.text.Layout$Alignment r40 = android.text.Layout.Alignment.ALIGN_CENTER
            r41 = 1065353216(0x3var_, float:1.0)
            r42 = 0
            r43 = 0
            r36 = r0
            r37 = r11
            r39 = r9
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r0 = r0 + r9
            int r33 = r33 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x196a
            int r9 = r56.getMeasuredWidth()
            int r10 = r1.countWidth
            int r9 = r9 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r9 = r9 - r10
            r1.countLeft = r9
            goto L_0x197a
        L_0x196a:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r30)
            r1.countLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x197a:
            r9 = 1
            r1.drawCount = r9
            goto L_0x1982
        L_0x197e:
            r13 = r10
            r9 = 0
            r1.countWidth = r9
        L_0x1982:
            if (r12 == 0) goto L_0x1a09
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x19ba
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r9 = r9.measureText(r12)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r0 = java.lang.Math.max(r0, r9)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r38 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r9 = r1.mentionWidth
            android.text.Layout$Alignment r40 = android.text.Layout.Alignment.ALIGN_CENTER
            r41 = 1065353216(0x3var_, float:1.0)
            r42 = 0
            r43 = 0
            r36 = r0
            r37 = r12
            r39 = r9
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)
            r1.mentionLayout = r0
            goto L_0x19c2
        L_0x19ba:
            r9 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.mentionWidth = r0
        L_0x19c2:
            int r0 = r1.mentionWidth
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r0 = r0 + r9
            int r33 = r33 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x19ea
            int r9 = r56.getMeasuredWidth()
            int r10 = r1.mentionWidth
            int r9 = r9 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r9 = r9 - r10
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x19e5
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r10 = r10 + r15
            goto L_0x19e6
        L_0x19e5:
            r10 = 0
        L_0x19e6:
            int r9 = r9 - r10
            r1.mentionLeft = r9
            goto L_0x1a06
        L_0x19ea:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x19f8
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r10 = r10 + r15
            goto L_0x19f9
        L_0x19f8:
            r10 = 0
        L_0x19f9:
            int r9 = r9 + r10
            r1.mentionLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x1a06:
            r9 = 1
            r1.drawMention = r9
        L_0x1a09:
            boolean r0 = r1.drawReactionMention
            if (r0 == 0) goto L_0x1a5e
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r33 = r33 - r0
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 != 0) goto L_0x1a3f
            int r9 = r56.getMeasuredWidth()
            r10 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r10 = r1.mentionWidth
            if (r10 == 0) goto L_0x1a2e
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r10 = r10 + r15
            goto L_0x1a2f
        L_0x1a2e:
            r10 = 0
        L_0x1a2f:
            int r9 = r9 - r10
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x1a3a
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r10 = r10 + r15
            goto L_0x1a3b
        L_0x1a3a:
            r10 = 0
        L_0x1a3b:
            int r9 = r9 - r10
            r1.reactionMentionLeft = r9
            goto L_0x1a5b
        L_0x1a3f:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r30)
            int r10 = r1.countWidth
            if (r10 == 0) goto L_0x1a4d
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r35)
            int r10 = r10 + r15
            goto L_0x1a4e
        L_0x1a4d:
            r10 = 0
        L_0x1a4e:
            int r9 = r9 + r10
            r1.reactionMentionLeft = r9
            int r9 = r1.messageLeft
            int r9 = r9 + r0
            r1.messageLeft = r9
            int r9 = r1.messageNameLeft
            int r9 = r9 + r0
            r1.messageNameLeft = r9
        L_0x1a5b:
            r0 = r33
            goto L_0x1a60
        L_0x1a5e:
            r0 = r33
        L_0x1a60:
            if (r24 == 0) goto L_0x1aaf
            if (r3 != 0) goto L_0x1a66
            java.lang.String r3 = ""
        L_0x1a66:
            r9 = r3
            int r10 = r9.length()
            r15 = 150(0x96, float:2.1E-43)
            if (r10 <= r15) goto L_0x1a74
            r10 = 0
            java.lang.CharSequence r9 = r9.subSequence(r10, r15)
        L_0x1a74:
            boolean r10 = r1.useForceThreeLines
            if (r10 != 0) goto L_0x1a7c
            boolean r10 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r10 == 0) goto L_0x1a7e
        L_0x1a7c:
            if (r2 == 0) goto L_0x1a83
        L_0x1a7e:
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            goto L_0x1a87
        L_0x1a83:
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceTwoNewLinesToOne(r9)
        L_0x1a87:
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            int r15 = r1.paintIndex
            r10 = r10[r15]
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            r15 = 1099431936(0x41880000, float:17.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r16 = r3
            r3 = 0
            java.lang.CharSequence r10 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r15, r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x1aae
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r15 = r1.resourcesProvider
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r10, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r15)
            if (r3 == 0) goto L_0x1aae
            r10 = r3
            goto L_0x1aaf
        L_0x1aae:
            r3 = r10
        L_0x1aaf:
            r9 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = java.lang.Math.max(r10, r0)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1ac1
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1b18
        L_0x1ac1:
            if (r2 == 0) goto L_0x1b18
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1acc
            int r0 = r1.currentDialogFolderDialogsCount
            r10 = 1
            if (r0 != r10) goto L_0x1b18
        L_0x1acc:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1afe }
            if (r0 == 0) goto L_0x1ae3
            boolean r0 = r0.hasHighlightedWords()     // Catch:{ Exception -> 0x1afe }
            if (r0 == 0) goto L_0x1ae3
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x1afe }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x1afe }
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r1.resourcesProvider     // Catch:{ Exception -> 0x1afe }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r2, (java.util.ArrayList<java.lang.String>) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)     // Catch:{ Exception -> 0x1afe }
            if (r0 == 0) goto L_0x1ae3
            r2 = r0
        L_0x1ae3:
            android.text.TextPaint r47 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1afe }
            android.text.Layout$Alignment r49 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1afe }
            r50 = 1065353216(0x3var_, float:1.0)
            r51 = 0
            r52 = 0
            android.text.TextUtils$TruncateAt r53 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1afe }
            r55 = 1
            r46 = r2
            r48 = r9
            r54 = r9
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r46, r47, r48, r49, r50, r51, r52, r53, r54, r55)     // Catch:{ Exception -> 0x1afe }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1afe }
            goto L_0x1b02
        L_0x1afe:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1b02:
            r0 = 1112276992(0x424CLASSNAME, float:51.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r10 = 1109393408(0x42200000, float:40.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r4
            float r10 = (float) r10
            r0.setImageY(r10)
            goto L_0x1b42
        L_0x1b18:
            r10 = 0
            r1.messageNameLayout = r10
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x1b2d
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1b24
            goto L_0x1b2d
        L_0x1b24:
            r0 = 1109131264(0x421CLASSNAME, float:39.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            goto L_0x1b42
        L_0x1b2d:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.messageTop = r0
            org.telegram.messenger.ImageReceiver r0 = r1.thumbImage
            r10 = 1101529088(0x41a80000, float:21.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r4
            float r10 = (float) r10
            r0.setImageY(r10)
        L_0x1b42:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1c6b }
            if (r0 != 0) goto L_0x1b54
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1b4b }
            if (r0 == 0) goto L_0x1b67
            goto L_0x1b54
        L_0x1b4b:
            r0 = move-exception
            r16 = r3
            r19 = r4
            r33 = r6
            goto L_0x1CLASSNAME
        L_0x1b54:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x1c6b }
            if (r0 == 0) goto L_0x1b67
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x1b4b }
            r10 = 1
            if (r0 <= r10) goto L_0x1b67
            r0 = r2
            r2 = 0
            android.text.TextPaint[] r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x1b4b }
            int r15 = r1.paintIndex     // Catch:{ Exception -> 0x1b4b }
            r10 = r10[r15]     // Catch:{ Exception -> 0x1b4b }
            r8 = r10
            goto L_0x1b82
        L_0x1b67:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1c6b }
            if (r0 != 0) goto L_0x1b6f
            boolean r0 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1b4b }
            if (r0 == 0) goto L_0x1b71
        L_0x1b6f:
            if (r2 == 0) goto L_0x1b81
        L_0x1b71:
            r10 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ Exception -> 0x1b4b }
            int r0 = r9 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x1b4b }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1b4b }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r3, r8, r0, r10)     // Catch:{ Exception -> 0x1b4b }
            goto L_0x1b82
        L_0x1b81:
            r0 = r3
        L_0x1b82:
            boolean r10 = r0 instanceof android.text.Spannable     // Catch:{ Exception -> 0x1c6b }
            if (r10 == 0) goto L_0x1bdd
            r10 = r0
            android.text.Spannable r10 = (android.text.Spannable) r10     // Catch:{ Exception -> 0x1c6b }
            int r15 = r10.length()     // Catch:{ Exception -> 0x1c6b }
            r16 = r3
            java.lang.Class<android.text.style.CharacterStyle> r3 = android.text.style.CharacterStyle.class
            r19 = r4
            r4 = 0
            java.lang.Object[] r3 = r10.getSpans(r4, r15, r3)     // Catch:{ Exception -> 0x1bd1 }
            android.text.style.CharacterStyle[] r3 = (android.text.style.CharacterStyle[]) r3     // Catch:{ Exception -> 0x1bd1 }
            int r4 = r3.length     // Catch:{ Exception -> 0x1bd1 }
            r15 = 0
        L_0x1b9c:
            if (r15 >= r4) goto L_0x1bce
            r29 = r3[r15]     // Catch:{ Exception -> 0x1bd1 }
            r30 = r29
            r29 = r3
            r3 = r30
            r30 = r4
            boolean r4 = r3 instanceof android.text.style.ClickableSpan     // Catch:{ Exception -> 0x1bd1 }
            if (r4 != 0) goto L_0x1bc0
            boolean r4 = r3 instanceof android.text.style.StyleSpan     // Catch:{ Exception -> 0x1bd1 }
            if (r4 == 0) goto L_0x1bbd
            r4 = r3
            android.text.style.StyleSpan r4 = (android.text.style.StyleSpan) r4     // Catch:{ Exception -> 0x1bd1 }
            int r4 = r4.getStyle()     // Catch:{ Exception -> 0x1bd1 }
            r33 = r6
            r6 = 1
            if (r4 != r6) goto L_0x1bc5
            goto L_0x1bc2
        L_0x1bbd:
            r33 = r6
            goto L_0x1bc5
        L_0x1bc0:
            r33 = r6
        L_0x1bc2:
            r10.removeSpan(r3)     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1bc5:
            int r15 = r15 + 1
            r3 = r29
            r4 = r30
            r6 = r33
            goto L_0x1b9c
        L_0x1bce:
            r33 = r6
            goto L_0x1be3
        L_0x1bd1:
            r0 = move-exception
            r33 = r6
            goto L_0x1CLASSNAME
        L_0x1bd6:
            r0 = move-exception
            r19 = r4
            r33 = r6
            goto L_0x1CLASSNAME
        L_0x1bdd:
            r16 = r3
            r19 = r4
            r33 = r6
        L_0x1be3:
            boolean r3 = r1.useForceThreeLines     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 != 0) goto L_0x1CLASSNAME
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1bec
            goto L_0x1CLASSNAME
        L_0x1bec:
            boolean r3 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1CLASSNAME
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x1CLASSNAME }
            int r4 = r4 + r7
            int r9 = r9 + r4
            boolean r3 = org.telegram.messenger.LocaleController.isRTL     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1CLASSNAME
            int r3 = r1.messageLeft     // Catch:{ Exception -> 0x1CLASSNAME }
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x1CLASSNAME }
            int r6 = r6 + r7
            int r3 = r3 - r6
            r1.messageLeft = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1CLASSNAME:
            android.text.StaticLayout r3 = new android.text.StaticLayout     // Catch:{ Exception -> 0x1CLASSNAME }
            android.text.Layout$Alignment r40 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1CLASSNAME }
            r41 = 1065353216(0x3var_, float:1.0)
            r42 = 0
            r43 = 0
            r36 = r3
            r37 = r0
            r38 = r8
            r39 = r9
            r36.<init>(r37, r38, r39, r40, r41, r42, r43)     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.messageLayout = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            boolean r3 = r1.hasMessageThumb     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1c2d
            if (r2 == 0) goto L_0x1c2d
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x1CLASSNAME }
            int r9 = r9 + r4
        L_0x1c2d:
            android.text.Layout$Alignment r49 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1CLASSNAME }
            r50 = 1065353216(0x3var_, float:1.0)
            r3 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x1CLASSNAME }
            float r3 = (float) r3     // Catch:{ Exception -> 0x1CLASSNAME }
            r52 = 0
            android.text.TextUtils$TruncateAt r53 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x1CLASSNAME
            r55 = 1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r55 = 2
        L_0x1CLASSNAME:
            r46 = r0
            r47 = r8
            r48 = r9
            r51 = r3
            r54 = r9
            android.text.StaticLayout r3 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r46, r47, r48, r49, r50, r51, r52, r53, r54, r55)     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.messageLayout = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1CLASSNAME:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r1.spoilersPool     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilers     // Catch:{ Exception -> 0x1CLASSNAME }
            r3.addAll(r4)     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r3 = r1.spoilers     // Catch:{ Exception -> 0x1CLASSNAME }
            r3.clear()     // Catch:{ Exception -> 0x1CLASSNAME }
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r4 = r1.spoilersPool     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.spoilers     // Catch:{ Exception -> 0x1CLASSNAME }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r3, r4, r6)     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            goto L_0x1CLASSNAME
        L_0x1c6b:
            r0 = move-exception
            r16 = r3
            r19 = r4
            r33 = r6
        L_0x1CLASSNAME:
            r3 = 0
            r1.messageLayout = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1CLASSNAME:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1dc2
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1d46
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1d46
            android.text.StaticLayout r0 = r1.nameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            boolean r6 = r1.dialogMuted
            if (r6 == 0) goto L_0x1cce
            boolean r6 = r1.drawVerified
            if (r6 != 0) goto L_0x1cce
            int r6 = r1.drawScam
            if (r6 != 0) goto L_0x1cce
            int r6 = r1.nameLeft
            r10 = r7
            double r6 = (double) r6
            r25 = r10
            r15 = r11
            double r10 = (double) r5
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r10
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r10 = r10.getIntrinsicWidth()
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            int r6 = (int) r6
            r1.nameMuteLeft = r6
            goto L_0x1d2c
        L_0x1cce:
            r25 = r7
            r15 = r11
            boolean r6 = r1.drawVerified
            if (r6 == 0) goto L_0x1cfb
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r10 = (double) r5
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r10
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r10 = r10.getIntrinsicWidth()
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            int r6 = (int) r6
            r1.nameMuteLeft = r6
            goto L_0x1d2c
        L_0x1cfb:
            int r6 = r1.drawScam
            if (r6 == 0) goto L_0x1d2c
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r10 = (double) r5
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r10
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            int r10 = r1.drawScam
            r11 = 1
            if (r10 != r11) goto L_0x1d1e
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x1d20
        L_0x1d1e:
            org.telegram.ui.Components.ScamDrawable r10 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x1d20:
            int r10 = r10.getIntrinsicWidth()
            double r10 = (double) r10
            java.lang.Double.isNaN(r10)
            double r6 = r6 - r10
            int r6 = (int) r6
            r1.nameMuteLeft = r6
        L_0x1d2c:
            r6 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x1d49
            double r6 = (double) r5
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 >= 0) goto L_0x1d49
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r10 = (double) r5
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r10
            int r6 = (int) r6
            r1.nameLeft = r6
            goto L_0x1d49
        L_0x1d46:
            r25 = r7
            r15 = r11
        L_0x1d49:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1d8b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1d8b
            r3 = 2147483647(0x7fffffff, float:NaN)
            r4 = 0
        L_0x1d57:
            if (r4 >= r0) goto L_0x1d81
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineLeft(r4)
            r7 = 0
            int r7 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r7 != 0) goto L_0x1d7e
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineWidth(r4)
            double r10 = (double) r7
            double r10 = java.lang.Math.ceil(r10)
            r17 = r6
            double r6 = (double) r9
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r10
            int r6 = (int) r6
            int r3 = java.lang.Math.min(r3, r6)
            int r4 = r4 + 1
            goto L_0x1d57
        L_0x1d7e:
            r17 = r6
            r3 = 0
        L_0x1d81:
            r4 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r4) goto L_0x1d8b
            int r4 = r1.messageLeft
            int r4 = r4 + r3
            r1.messageLeft = r4
        L_0x1d8b:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1e50
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1e50
            android.text.StaticLayout r0 = r1.messageNameLayout
            r3 = 0
            float r0 = r0.getLineLeft(r3)
            r4 = 0
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x1e50
            android.text.StaticLayout r4 = r1.messageNameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            double r6 = (double) r9
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 >= 0) goto L_0x1e50
            int r6 = r1.messageNameLeft
            double r6 = (double) r6
            double r10 = (double) r9
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r10
            int r6 = (int) r6
            r1.messageNameLeft = r6
            goto L_0x1e50
        L_0x1dc2:
            r25 = r7
            r15 = r11
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1e15
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1e15
            android.text.StaticLayout r0 = r1.nameLayout
            r3 = 0
            float r0 = r0.getLineRight(r3)
            float r4 = (float) r5
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x1dfa
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r3)
            double r3 = (double) r4
            double r3 = java.lang.Math.ceil(r3)
            double r6 = (double) r5
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 >= 0) goto L_0x1dfa
            int r6 = r1.nameLeft
            double r6 = (double) r6
            double r10 = (double) r5
            java.lang.Double.isNaN(r10)
            double r10 = r10 - r3
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r10
            int r6 = (int) r6
            r1.nameLeft = r6
        L_0x1dfa:
            boolean r3 = r1.dialogMuted
            if (r3 != 0) goto L_0x1e06
            boolean r3 = r1.drawVerified
            if (r3 != 0) goto L_0x1e06
            int r3 = r1.drawScam
            if (r3 == 0) goto L_0x1e15
        L_0x1e06:
            int r3 = r1.nameLeft
            float r3 = (float) r3
            float r3 = r3 + r0
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 + r4
            int r3 = (int) r3
            r1.nameMuteLeft = r3
        L_0x1e15:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1e38
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1e38
            r3 = 1325400064(0x4var_, float:2.14748365E9)
            r4 = 0
        L_0x1e22:
            if (r4 >= r0) goto L_0x1e31
            android.text.StaticLayout r6 = r1.messageLayout
            float r6 = r6.getLineLeft(r4)
            float r3 = java.lang.Math.min(r3, r6)
            int r4 = r4 + 1
            goto L_0x1e22
        L_0x1e31:
            int r4 = r1.messageLeft
            float r4 = (float) r4
            float r4 = r4 - r3
            int r4 = (int) r4
            r1.messageLeft = r4
        L_0x1e38:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x1e50
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1e50
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r3 = r1.messageNameLayout
            r4 = 0
            float r3 = r3.getLineLeft(r4)
            float r0 = r0 - r3
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x1e50:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1e92
            boolean r3 = r1.hasMessageThumb
            if (r3 == 0) goto L_0x1e92
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x1e8e }
            int r0 = r0.length()     // Catch:{ Exception -> 0x1e8e }
            if (r14 < r0) goto L_0x1e64
            int r14 = r0 + -1
        L_0x1e64:
            android.text.StaticLayout r3 = r1.messageLayout     // Catch:{ Exception -> 0x1e8e }
            float r3 = r3.getPrimaryHorizontal(r14)     // Catch:{ Exception -> 0x1e8e }
            android.text.StaticLayout r4 = r1.messageLayout     // Catch:{ Exception -> 0x1e8e }
            int r6 = r14 + 1
            float r4 = r4.getPrimaryHorizontal(r6)     // Catch:{ Exception -> 0x1e8e }
            float r6 = java.lang.Math.min(r3, r4)     // Catch:{ Exception -> 0x1e8e }
            double r6 = (double) r6     // Catch:{ Exception -> 0x1e8e }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x1e8e }
            int r6 = (int) r6     // Catch:{ Exception -> 0x1e8e }
            if (r6 == 0) goto L_0x1e85
            r7 = 1077936128(0x40400000, float:3.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x1e8e }
            int r6 = r6 + r7
        L_0x1e85:
            org.telegram.messenger.ImageReceiver r7 = r1.thumbImage     // Catch:{ Exception -> 0x1e8e }
            int r10 = r1.messageLeft     // Catch:{ Exception -> 0x1e8e }
            int r10 = r10 + r6
            r7.setImageX(r10)     // Catch:{ Exception -> 0x1e8e }
            goto L_0x1e92
        L_0x1e8e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1e92:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1ee5
            int r3 = r1.printingStringType
            if (r3 < 0) goto L_0x1ee5
            if (r45 < 0) goto L_0x1eb9
            int r3 = r45 + 1
            java.lang.CharSequence r0 = r0.getText()
            int r0 = r0.length()
            if (r3 >= r0) goto L_0x1eb9
            android.text.StaticLayout r0 = r1.messageLayout
            r3 = r45
            float r0 = r0.getPrimaryHorizontal(r3)
            android.text.StaticLayout r4 = r1.messageLayout
            int r6 = r3 + 1
            float r4 = r4.getPrimaryHorizontal(r6)
            goto L_0x1ec9
        L_0x1eb9:
            r3 = r45
            android.text.StaticLayout r0 = r1.messageLayout
            r4 = 0
            float r0 = r0.getPrimaryHorizontal(r4)
            android.text.StaticLayout r4 = r1.messageLayout
            r6 = 1
            float r4 = r4.getPrimaryHorizontal(r6)
        L_0x1ec9:
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x1ed5
            int r6 = r1.messageLeft
            float r6 = (float) r6
            float r6 = r6 + r0
            int r6 = (int) r6
            r1.statusDrawableLeft = r6
            goto L_0x1ee7
        L_0x1ed5:
            int r6 = r1.messageLeft
            float r6 = (float) r6
            float r6 = r6 + r4
            r7 = 1077936128(0x40400000, float:3.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r6 = r6 + r7
            int r6 = (int) r6
            r1.statusDrawableLeft = r6
            goto L_0x1ee7
        L_0x1ee5:
            r3 = r45
        L_0x1ee7:
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
                if (dialog == null) {
                    this.unreadCount = 0;
                    this.mentionCount = 0;
                    this.reactionMentionCount = 0;
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
    public /* synthetic */ void m1493lambda$update$0$orgtelegramuiCellsDialogCell(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$update$1$org-telegram-ui-Cells-DialogCell  reason: not valid java name */
    public /* synthetic */ void m1494lambda$update$1$orgtelegramuiCellsDialogCell(ValueAnimator valueAnimator) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0eb3, code lost:
        if (r8.reactionsMentionsChangeProgress != 1.0f) goto L_0x0eb8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x09c2  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x09c5  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x09cf  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x09e3  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a1c  */
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
            r6 = 2131628502(0x7f0e11d6, float:1.8884298E38)
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
            r4 = 2131626031(0x7f0e082f, float:1.8879287E38)
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
            r4 = 2131627562(0x7f0e0e2a, float:1.8882392E38)
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
            r4 = 2131628304(0x7f0e1110, float:1.8883897E38)
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
            r4 = 2131628292(0x7f0e1104, float:1.8883873E38)
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
            r4 = 2131628289(0x7f0e1101, float:1.8883866E38)
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
            r4 = 2131628291(0x7f0e1103, float:1.888387E38)
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
            r4 = 2131628290(0x7f0e1102, float:1.8883869E38)
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
            r4 = 2131628305(0x7f0e1111, float:1.8883899E38)
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
            r4 = 2131628293(0x7f0e1105, float:1.8883875E38)
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
            r4 = 2131624333(0x7f0e018d, float:1.8875843E38)
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
            r4 = 2131628493(0x7f0e11cd, float:1.888428E38)
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
            if (r0 == 0) goto L_0x065a
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            r0.draw(r9)
            goto L_0x0692
        L_0x065a:
            boolean r0 = r8.drawNameGroup
            if (r0 == 0) goto L_0x066d
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            r0.draw(r9)
            goto L_0x0692
        L_0x066d:
            boolean r0 = r8.drawNameBroadcast
            if (r0 == 0) goto L_0x0680
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            r0.draw(r9)
            goto L_0x0692
        L_0x0680:
            boolean r0 = r8.drawNameBot
            if (r0 == 0) goto L_0x0692
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r1 = r8.nameLockLeft
            int r2 = r8.nameLockTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r1, (int) r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            r0.draw(r9)
        L_0x0692:
            android.text.StaticLayout r0 = r8.nameLayout
            r19 = 1092616192(0x41200000, float:10.0)
            r20 = 1095761920(0x41500000, float:13.0)
            if (r0 == 0) goto L_0x071d
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x06b8
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
            goto L_0x06f9
        L_0x06b8:
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.encryptedChat
            if (r0 != 0) goto L_0x06e0
            org.telegram.ui.Cells.DialogCell$CustomDialog r0 = r8.customDialog
            if (r0 == 0) goto L_0x06c6
            int r0 = r0.type
            r1 = 2
            if (r0 != r1) goto L_0x06c6
            goto L_0x06e0
        L_0x06c6:
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
            goto L_0x06f9
        L_0x06e0:
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
        L_0x06f9:
            r35.save()
            int r0 = r8.nameLeft
            float r0 = (float) r0
            boolean r1 = r8.useForceThreeLines
            if (r1 != 0) goto L_0x070b
            boolean r1 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r1 == 0) goto L_0x0708
            goto L_0x070b
        L_0x0708:
            r1 = 1095761920(0x41500000, float:13.0)
            goto L_0x070d
        L_0x070b:
            r1 = 1092616192(0x41200000, float:10.0)
        L_0x070d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.nameLayout
            r0.draw(r9)
            r35.restore()
        L_0x071d:
            android.text.StaticLayout r0 = r8.timeLayout
            if (r0 == 0) goto L_0x0739
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0739
            r35.save()
            int r0 = r8.timeLeft
            float r0 = (float) r0
            int r1 = r8.timeTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.timeLayout
            r0.draw(r9)
            r35.restore()
        L_0x0739:
            android.text.StaticLayout r0 = r8.messageNameLayout
            if (r0 == 0) goto L_0x0793
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0753
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessageArchived_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x077a
        L_0x0753:
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r8.draftMessage
            if (r0 == 0) goto L_0x0769
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_draft"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
            goto L_0x077a
        L_0x0769:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_messageNamePaint
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r8.resourcesProvider
            java.lang.String r3 = "chats_nameMessage_threeLines"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r1.linkColor = r2
            r0.setColor(r2)
        L_0x077a:
            r35.save()
            int r0 = r8.messageNameLeft
            float r0 = (float) r0
            int r1 = r8.messageNameTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            android.text.StaticLayout r0 = r8.messageNameLayout     // Catch:{ Exception -> 0x078c }
            r0.draw(r9)     // Catch:{ Exception -> 0x078c }
            goto L_0x0790
        L_0x078c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0790:
            r35.restore()
        L_0x0793:
            android.text.StaticLayout r0 = r8.messageLayout
            if (r0 == 0) goto L_0x089f
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x07d3
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x07b9
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
            goto L_0x07ec
        L_0x07b9:
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
            goto L_0x07ec
        L_0x07d3:
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
        L_0x07ec:
            r35.save()
            int r0 = r8.messageLeft
            float r0 = (float) r0
            int r1 = r8.messageTop
            float r1 = (float) r1
            r9.translate(r0, r1)
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x083a
            r35.save()     // Catch:{ Exception -> 0x0835 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r8.spoilers     // Catch:{ Exception -> 0x0835 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.clipOutCanvas(r9, r0)     // Catch:{ Exception -> 0x0835 }
            android.text.StaticLayout r0 = r8.messageLayout     // Catch:{ Exception -> 0x0835 }
            r0.draw(r9)     // Catch:{ Exception -> 0x0835 }
            r35.restore()     // Catch:{ Exception -> 0x0835 }
            r0 = 0
        L_0x0811:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x0835 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0835 }
            if (r0 >= r1) goto L_0x0834
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r1 = r8.spoilers     // Catch:{ Exception -> 0x0835 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ Exception -> 0x0835 }
            org.telegram.ui.Components.spoilers.SpoilerEffect r1 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r1     // Catch:{ Exception -> 0x0835 }
            android.text.StaticLayout r2 = r8.messageLayout     // Catch:{ Exception -> 0x0835 }
            android.text.TextPaint r2 = r2.getPaint()     // Catch:{ Exception -> 0x0835 }
            int r2 = r2.getColor()     // Catch:{ Exception -> 0x0835 }
            r1.setColor(r2)     // Catch:{ Exception -> 0x0835 }
            r1.draw(r9)     // Catch:{ Exception -> 0x0835 }
            int r0 = r0 + 1
            goto L_0x0811
        L_0x0834:
            goto L_0x0839
        L_0x0835:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0839:
            goto L_0x083f
        L_0x083a:
            android.text.StaticLayout r0 = r8.messageLayout
            r0.draw(r9)
        L_0x083f:
            r35.restore()
            int r0 = r8.printingStringType
            if (r0 < 0) goto L_0x089f
            org.telegram.ui.Components.StatusDrawable r0 = org.telegram.ui.ActionBar.Theme.getChatStatusDrawable(r0)
            if (r0 == 0) goto L_0x089f
            r35.save()
            int r1 = r8.printingStringType
            r2 = 1
            if (r1 == r2) goto L_0x0871
            r2 = 4
            if (r1 != r2) goto L_0x0858
            goto L_0x0871
        L_0x0858:
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
            goto L_0x0886
        L_0x0871:
            int r2 = r8.statusDrawableLeft
            float r2 = (float) r2
            int r3 = r8.messageTop
            r4 = 1
            if (r1 != r4) goto L_0x0880
            r1 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0881
        L_0x0880:
            r4 = 0
        L_0x0881:
            int r3 = r3 + r4
            float r1 = (float) r3
            r9.translate(r2, r1)
        L_0x0886:
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
        L_0x089f:
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x0967
            boolean r0 = r8.drawClock
            boolean r1 = r8.drawCheck1
            if (r1 == 0) goto L_0x08ab
            r6 = 2
            goto L_0x08ac
        L_0x08ab:
            r6 = 0
        L_0x08ac:
            int r0 = r0 + r6
            boolean r1 = r8.drawCheck2
            if (r1 == 0) goto L_0x08b3
            r1 = 4
            goto L_0x08b4
        L_0x08b3:
            r1 = 0
        L_0x08b4:
            int r0 = r0 + r1
            int r1 = r8.lastStatusDrawableParams
            if (r1 < 0) goto L_0x08c2
            if (r1 == r0) goto L_0x08c2
            boolean r2 = r8.statusDrawableAnimationInProgress
            if (r2 != 0) goto L_0x08c2
            r8.createStatusDrawableAnimator(r1, r0)
        L_0x08c2:
            boolean r1 = r8.statusDrawableAnimationInProgress
            if (r1 == 0) goto L_0x08c8
            int r0 = r8.animateToStatusDrawableParams
        L_0x08c8:
            r2 = r0 & 1
            if (r2 == 0) goto L_0x08ce
            r5 = 1
            goto L_0x08cf
        L_0x08ce:
            r5 = 0
        L_0x08cf:
            r22 = r5
            r2 = r0 & 2
            if (r2 == 0) goto L_0x08d7
            r5 = 1
            goto L_0x08d8
        L_0x08d7:
            r5 = 0
        L_0x08d8:
            r23 = r5
            r2 = r0 & 4
            if (r2 == 0) goto L_0x08e0
            r5 = 1
            goto L_0x08e1
        L_0x08e0:
            r5 = 0
        L_0x08e1:
            r24 = r5
            if (r1 == 0) goto L_0x0943
            int r1 = r8.animateFromStatusDrawableParams
            r2 = r1 & 1
            if (r2 == 0) goto L_0x08ed
            r5 = 1
            goto L_0x08ee
        L_0x08ed:
            r5 = 0
        L_0x08ee:
            r25 = r5
            r2 = r1 & 2
            if (r2 == 0) goto L_0x08f6
            r5 = 1
            goto L_0x08f7
        L_0x08f6:
            r5 = 0
        L_0x08f7:
            r26 = r5
            r7 = 4
            r1 = r1 & r7
            if (r1 == 0) goto L_0x08ff
            r5 = 1
            goto L_0x0900
        L_0x08ff:
            r5 = 0
        L_0x0900:
            r21 = r5
            if (r22 != 0) goto L_0x0923
            if (r25 != 0) goto L_0x0923
            if (r21 == 0) goto L_0x0923
            if (r26 != 0) goto L_0x0923
            if (r23 == 0) goto L_0x0923
            if (r24 == 0) goto L_0x0923
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
            goto L_0x0942
        L_0x0923:
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
        L_0x0942:
            goto L_0x0953
        L_0x0943:
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r1 = r34
            r2 = r35
            r3 = r22
            r4 = r23
            r5 = r24
            r1.drawCheckStatus(r2, r3, r4, r5, r6, r7)
        L_0x0953:
            boolean r1 = r8.drawClock
            boolean r2 = r8.drawCheck1
            if (r2 == 0) goto L_0x095b
            r6 = 2
            goto L_0x095c
        L_0x095b:
            r6 = 0
        L_0x095c:
            int r1 = r1 + r6
            boolean r2 = r8.drawCheck2
            if (r2 == 0) goto L_0x0963
            r7 = 4
            goto L_0x0964
        L_0x0963:
            r7 = 0
        L_0x0964:
            int r1 = r1 + r7
            r8.lastStatusDrawableParams = r1
        L_0x0967:
            int r0 = r8.dialogsType
            r1 = 1132396544(0x437var_, float:255.0)
            r2 = 2
            if (r0 == r2) goto L_0x0a23
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0979
            float r2 = r8.dialogMutedProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a23
        L_0x0979:
            boolean r2 = r8.drawVerified
            if (r2 != 0) goto L_0x0a23
            int r2 = r8.drawScam
            if (r2 != 0) goto L_0x0a23
            if (r0 == 0) goto L_0x099c
            float r2 = r8.dialogMutedProgress
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x099c
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            float r2 = r2 + r0
            r8.dialogMutedProgress = r2
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0998
            r8.dialogMutedProgress = r3
            goto L_0x09b5
        L_0x0998:
            r34.invalidate()
            goto L_0x09b5
        L_0x099c:
            if (r0 != 0) goto L_0x09b5
            float r0 = r8.dialogMutedProgress
            r2 = 0
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x09b5
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            float r0 = r0 - r3
            r8.dialogMutedProgress = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x09b2
            r8.dialogMutedProgress = r2
            goto L_0x09b5
        L_0x09b2:
            r34.invalidate()
        L_0x09b5:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x09c5
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09c2
            goto L_0x09c5
        L_0x09c2:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x09c6
        L_0x09c5:
            r4 = 0
        L_0x09c6:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r3
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x09d2
            r3 = 1096286208(0x41580000, float:13.5)
            goto L_0x09d4
        L_0x09d2:
            r3 = 1099694080(0x418CLASSNAME, float:17.5)
        L_0x09d4:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            float r0 = r8.dialogMutedProgress
            r2 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0a1c
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
            goto L_0x0a94
        L_0x0a1c:
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_muteDrawable
            r0.draw(r9)
            goto L_0x0a94
        L_0x0a23:
            boolean r0 = r8.drawVerified
            if (r0 == 0) goto L_0x0a64
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a37
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a34
            goto L_0x0a37
        L_0x0a34:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a39
        L_0x0a37:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a39:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a50
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a4d
            goto L_0x0a50
        L_0x0a4d:
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x0a52
        L_0x0a50:
            r3 = 1095237632(0x41480000, float:12.5)
        L_0x0a52:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            r0.draw(r9)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r9)
            goto L_0x0a94
        L_0x0a64:
            int r0 = r8.drawScam
            if (r0 == 0) goto L_0x0a94
            r2 = 1
            if (r0 != r2) goto L_0x0a6e
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a70
        L_0x0a6e:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a70:
            int r2 = r8.nameMuteLeft
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x0a7e
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0a7b
            goto L_0x0a7e
        L_0x0a7b:
            r3 = 1097859072(0x41700000, float:15.0)
            goto L_0x0a80
        L_0x0a7e:
            r3 = 1094713344(0x41400000, float:12.0)
        L_0x0a80:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r2, (int) r3)
            int r0 = r8.drawScam
            r2 = 1
            if (r0 != r2) goto L_0x0a8f
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_scamDrawable
            goto L_0x0a91
        L_0x0a8f:
            org.telegram.ui.Components.ScamDrawable r0 = org.telegram.ui.ActionBar.Theme.dialogs_fakeDrawable
        L_0x0a91:
            r0.draw(r9)
        L_0x0a94:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x0a9f
            float r0 = r8.reorderIconProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0ab7
        L_0x0a9f:
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
        L_0x0ab7:
            boolean r0 = r8.drawError
            r2 = 1085276160(0x40b00000, float:5.5)
            r3 = 1102577664(0x41b80000, float:23.0)
            r4 = 1084227584(0x40a00000, float:5.0)
            r5 = 1094189056(0x41380000, float:11.5)
            if (r0 == 0) goto L_0x0b14
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
            goto L_0x0var_
        L_0x0b14:
            boolean r0 = r8.drawCount
            if (r0 != 0) goto L_0x0b1c
            boolean r6 = r8.drawMention
            if (r6 == 0) goto L_0x0b20
        L_0x0b1c:
            boolean r6 = r8.drawCount2
            if (r6 != 0) goto L_0x0b53
        L_0x0b20:
            float r6 = r8.countChangeProgress
            r7 = 1065353216(0x3var_, float:1.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x0b53
            boolean r6 = r8.drawReactionMention
            if (r6 != 0) goto L_0x0b53
            float r6 = r8.reactionsMentionsChangeProgress
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 == 0) goto L_0x0b33
            goto L_0x0b53
        L_0x0b33:
            boolean r0 = r8.drawPin
            if (r0 == 0) goto L_0x0var_
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
            goto L_0x0var_
        L_0x0b53:
            if (r0 == 0) goto L_0x0b59
            boolean r0 = r8.drawCount2
            if (r0 != 0) goto L_0x0b61
        L_0x0b59:
            float r0 = r8.countChangeProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0ded
        L_0x0b61:
            int r0 = r8.unreadCount
            if (r0 != 0) goto L_0x0b70
            boolean r6 = r8.markUnread
            if (r6 != 0) goto L_0x0b70
            float r6 = r8.countChangeProgress
            r7 = 1065353216(0x3var_, float:1.0)
            float r6 = r7 - r6
            goto L_0x0b72
        L_0x0b70:
            float r6 = r8.countChangeProgress
        L_0x0b72:
            android.text.StaticLayout r7 = r8.countOldLayout
            if (r7 == 0) goto L_0x0d09
            if (r0 != 0) goto L_0x0b7a
            goto L_0x0d09
        L_0x0b7a:
            boolean r0 = r8.dialogMuted
            if (r0 != 0) goto L_0x0b86
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x0b83
            goto L_0x0b86
        L_0x0b83:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0b88
        L_0x0b86:
            android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0b88:
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
            if (r7 <= 0) goto L_0x0ba8
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0ba8:
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
            goto L_0x0CLASSNAME
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
        L_0x0CLASSNAME:
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
            if (r1 == 0) goto L_0x0CLASSNAME
            r35.save()
            int r1 = r8.countTop
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r1 = r1 + r2
            float r1 = (float) r1
            r9.translate(r7, r1)
            android.text.StaticLayout r1 = r8.countAnimationStableLayout
            r1.draw(r9)
            r35.restore()
        L_0x0CLASSNAME:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            int r1 = r1.getAlpha()
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r1
            float r3 = r3 * r4
            int r3 = (int) r3
            r2.setAlpha(r3)
            android.text.StaticLayout r2 = r8.countAnimationInLayout
            if (r2 == 0) goto L_0x0CLASSNAME
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0CLASSNAME
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
        L_0x0CLASSNAME:
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
            goto L_0x0cc5
        L_0x0CLASSNAME:
            android.text.StaticLayout r2 = r8.countLayout
            if (r2 == 0) goto L_0x0cc5
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0ca4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            goto L_0x0ca9
        L_0x0ca4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
        L_0x0ca9:
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
        L_0x0cc5:
            android.text.StaticLayout r2 = r8.countOldLayout
            if (r2 == 0) goto L_0x0cff
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            float r3 = (float) r1
            r5 = 1065353216(0x3var_, float:1.0)
            float r28 = r5 - r4
            float r3 = r3 * r28
            int r3 = (int) r3
            r2.setAlpha(r3)
            r35.save()
            boolean r2 = r8.countAnimationIncrement
            if (r2 == 0) goto L_0x0ce3
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r2 = -r2
            goto L_0x0ce7
        L_0x0ce3:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
        L_0x0ce7:
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
        L_0x0cff:
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint
            r2.setAlpha(r1)
            r35.restore()
            goto L_0x0ded
        L_0x0d09:
            if (r0 != 0) goto L_0x0d0c
            goto L_0x0d0e
        L_0x0d0c:
            android.text.StaticLayout r7 = r8.countLayout
        L_0x0d0e:
            r0 = r7
            boolean r1 = r8.dialogMuted
            if (r1 != 0) goto L_0x0d1b
            int r1 = r8.currentDialogFolderId
            if (r1 == 0) goto L_0x0d18
            goto L_0x0d1b
        L_0x0d18:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
            goto L_0x0d1d
        L_0x0d1b:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
        L_0x0d1d:
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
            if (r4 == 0) goto L_0x0dbc
            boolean r4 = r8.drawPin
            if (r4 == 0) goto L_0x0daa
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
        L_0x0daa:
            r35.save()
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r6, r6, r3, r4)
        L_0x0dbc:
            android.graphics.RectF r3 = r8.rect
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1094189056(0x41380000, float:11.5)
            float r4 = r4 * r5
            float r7 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 * r5
            r9.drawRoundRect(r3, r4, r7, r1)
            if (r0 == 0) goto L_0x0de4
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
        L_0x0de4:
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0ded
            r35.restore()
        L_0x0ded:
            boolean r0 = r8.drawMention
            if (r0 == 0) goto L_0x0ea9
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
            if (r1 == 0) goto L_0x0e33
            int r1 = r8.folderId
            if (r1 == 0) goto L_0x0e33
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countGrayPaint
            goto L_0x0e35
        L_0x0e33:
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_countPaint
        L_0x0e35:
            android.graphics.RectF r2 = r8.rect
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1094189056(0x41380000, float:11.5)
            float r3 = r3 * r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 * r4
            r9.drawRoundRect(r2, r3, r5, r1)
            android.text.StaticLayout r2 = r8.mentionLayout
            if (r2 == 0) goto L_0x0e72
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
            goto L_0x0ea9
        L_0x0e72:
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
        L_0x0ea9:
            boolean r0 = r8.drawReactionMention
            if (r0 != 0) goto L_0x0eb6
            float r0 = r8.reactionsMentionsChangeProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0var_
            goto L_0x0eb8
        L_0x0eb6:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0eb8:
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
            if (r4 == 0) goto L_0x0f0b
            boolean r4 = r8.drawReactionMention
            if (r4 == 0) goto L_0x0efa
            goto L_0x0efc
        L_0x0efa:
            float r2 = r3 - r2
        L_0x0efc:
            android.graphics.RectF r3 = r8.rect
            float r3 = r3.centerX()
            android.graphics.RectF r4 = r8.rect
            float r4 = r4.centerY()
            r9.scale(r2, r2, r3, r4)
        L_0x0f0b:
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
        L_0x0var_:
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
            if (r0 == 0) goto L_0x0var_
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x0var_
            boolean r0 = r0.isDraw()
            if (r0 != 0) goto L_0x0f8c
        L_0x0var_:
            org.telegram.messenger.ImageReceiver r0 = r8.avatarImage
            r0.draw(r9)
        L_0x0f8c:
            boolean r0 = r8.hasMessageThumb
            if (r0 == 0) goto L_0x0fc4
            org.telegram.messenger.ImageReceiver r0 = r8.thumbImage
            r0.draw(r9)
            boolean r0 = r8.drawPlay
            if (r0 == 0) goto L_0x0fc4
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
        L_0x0fc4:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x0fcb
            r35.restore()
        L_0x0fcb:
            boolean r0 = r8.isDialogCell
            if (r0 == 0) goto L_0x13bd
            int r0 = r8.currentDialogFolderId
            if (r0 != 0) goto L_0x13bd
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r0 == 0) goto L_0x10ae
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x10ae
            org.telegram.tgnet.TLRPC$User r0 = r8.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x10ae
            boolean r0 = r34.isOnline()
            if (r0 != 0) goto L_0x0ff2
            float r2 = r8.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x10aa
        L_0x0ff2:
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x1000
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x1002
        L_0x1000:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1002:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x1026
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x101e
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x101c
            goto L_0x101e
        L_0x101c:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x101e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x103e
        L_0x1026:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1037
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1035
            goto L_0x1037
        L_0x1035:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1037:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x103e:
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
            if (r0 == 0) goto L_0x1094
            float r3 = r8.onlineProgress
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x10aa
            float r5 = (float) r14
            r6 = 1125515264(0x43160000, float:150.0)
            float r5 = r5 / r6
            float r3 = r3 + r5
            r8.onlineProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x1091
            r8.onlineProgress = r4
        L_0x1091:
            r30 = 1
            goto L_0x10aa
        L_0x1094:
            float r3 = r8.onlineProgress
            r4 = 0
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x10aa
            float r5 = (float) r14
            r6 = 1125515264(0x43160000, float:150.0)
            float r5 = r5 / r6
            float r3 = r3 - r5
            r8.onlineProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x10a8
            r8.onlineProgress = r4
        L_0x10a8:
            r30 = 1
        L_0x10aa:
            r17 = r10
            goto L_0x13bf
        L_0x10ae:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            if (r0 == 0) goto L_0x13ba
            boolean r0 = r0.call_active
            if (r0 == 0) goto L_0x10be
            org.telegram.tgnet.TLRPC$Chat r0 = r8.chat
            boolean r0 = r0.call_not_empty
            if (r0 == 0) goto L_0x10be
            r5 = 1
            goto L_0x10bf
        L_0x10be:
            r5 = 0
        L_0x10bf:
            r8.hasCall = r5
            if (r5 != 0) goto L_0x10cf
            float r0 = r8.chatCallProgress
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x10cb
            goto L_0x10cf
        L_0x10cb:
            r17 = r10
            goto L_0x13bf
        L_0x10cf:
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            if (r0 == 0) goto L_0x10e4
            boolean r0 = r0.isChecked()
            if (r0 == 0) goto L_0x10e4
            org.telegram.ui.Components.CheckBox2 r0 = r8.checkBox
            float r0 = r0.getProgress()
            r2 = 1065353216(0x3var_, float:1.0)
            float r4 = r2 - r0
            goto L_0x10e6
        L_0x10e4:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x10e6:
            r0 = r4
            org.telegram.messenger.ImageReceiver r2 = r8.avatarImage
            float r2 = r2.getImageY2()
            boolean r3 = r8.useForceThreeLines
            if (r3 != 0) goto L_0x10f5
            boolean r3 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x10f7
        L_0x10f5:
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x10f7:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x111b
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x1113
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x1111
            goto L_0x1113
        L_0x1111:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x1113:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            goto L_0x1133
        L_0x111b:
            org.telegram.messenger.ImageReceiver r3 = r8.avatarImage
            float r3 = r3.getImageX2()
            boolean r4 = r8.useForceThreeLines
            if (r4 != 0) goto L_0x112c
            boolean r4 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x112a
            goto L_0x112c
        L_0x112a:
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x112c:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r3 = r3 - r1
            int r1 = (int) r3
        L_0x1133:
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
            if (r3 != 0) goto L_0x11a9
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
            goto L_0x12bd
        L_0x11a9:
            r4 = 1
            if (r3 != r4) goto L_0x11d0
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
            goto L_0x12bd
        L_0x11d0:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 2
            if (r3 != r5) goto L_0x11fa
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
            goto L_0x12bd
        L_0x11fa:
            r4 = 3
            if (r3 != r4) goto L_0x1221
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
            goto L_0x12bd
        L_0x1221:
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 4
            if (r3 != r5) goto L_0x124a
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
            goto L_0x12bd
        L_0x124a:
            r4 = 5
            if (r3 != r4) goto L_0x1273
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
            goto L_0x12bd
        L_0x1273:
            r4 = 6
            if (r3 != r4) goto L_0x129b
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
            goto L_0x12bd
        L_0x129b:
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
        L_0x12bd:
            float r6 = r8.chatCallProgress
            int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x12c7
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x12d5
        L_0x12c7:
            r35.save()
            float r4 = r8.chatCallProgress
            float r6 = r4 * r0
            float r4 = r4 * r0
            float r13 = (float) r1
            float r7 = (float) r2
            r9.scale(r6, r4, r13, r7)
        L_0x12d5:
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
            if (r4 < 0) goto L_0x1367
            int r4 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x136a
        L_0x1367:
            r35.restore()
        L_0x136a:
            float r4 = r8.innerProgress
            float r6 = (float) r14
            r7 = 1137180672(0x43CLASSNAME, float:400.0)
            float r6 = r6 / r7
            float r4 = r4 + r6
            r8.innerProgress = r4
            r6 = 1065353216(0x3var_, float:1.0)
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 < 0) goto L_0x1389
            r4 = 0
            r8.innerProgress = r4
            int r4 = r8.progressStage
            r6 = 1
            int r4 = r4 + r6
            r8.progressStage = r4
            r6 = 8
            if (r4 < r6) goto L_0x1389
            r4 = 0
            r8.progressStage = r4
        L_0x1389:
            r30 = 1
            boolean r4 = r8.hasCall
            if (r4 == 0) goto L_0x13a5
            float r4 = r8.chatCallProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 >= 0) goto L_0x13bf
            float r7 = (float) r14
            r10 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r10
            float r4 = r4 + r7
            r8.chatCallProgress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x13bf
            r8.chatCallProgress = r6
            goto L_0x13bf
        L_0x13a5:
            float r4 = r8.chatCallProgress
            r6 = 0
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 <= 0) goto L_0x13bf
            float r7 = (float) r14
            r10 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r10
            float r4 = r4 - r7
            r8.chatCallProgress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x13bf
            r8.chatCallProgress = r6
            goto L_0x13bf
        L_0x13ba:
            r17 = r10
            goto L_0x13bf
        L_0x13bd:
            r17 = r10
        L_0x13bf:
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x13c9
            r35.restore()
        L_0x13c9:
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x13ef
            float r0 = r8.translationX
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x13ef
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            if (r0 == 0) goto L_0x13ef
            r35.save()
            int r0 = r34.getMeasuredWidth()
            int r1 = r34.getMeasuredHeight()
            r2 = 0
            r9.clipRect(r2, r2, r0, r1)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r8.archivedChatsDrawable
            r0.draw(r9)
            r35.restore()
        L_0x13ef:
            boolean r0 = r8.useSeparator
            if (r0 == 0) goto L_0x1456
            boolean r0 = r8.fullSeparator
            if (r0 != 0) goto L_0x1413
            int r0 = r8.currentDialogFolderId
            if (r0 == 0) goto L_0x1403
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1403
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x1413
        L_0x1403:
            boolean r0 = r8.fullSeparator2
            if (r0 == 0) goto L_0x140c
            boolean r0 = r8.archiveHidden
            if (r0 != 0) goto L_0x140c
            goto L_0x1413
        L_0x140c:
            r0 = 1116733440(0x42900000, float:72.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x1414
        L_0x1413:
            r0 = 0
        L_0x1414:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x1438
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
            goto L_0x1456
        L_0x1438:
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
        L_0x1456:
            float r0 = r8.clipProgress
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x14a6
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 == r1) goto L_0x1467
            r35.restore()
            goto L_0x14a6
        L_0x1467:
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
        L_0x14a6:
            boolean r0 = r8.drawReorder
            if (r0 != 0) goto L_0x14b1
            float r1 = r8.reorderIconProgress
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x14e1
        L_0x14b1:
            if (r0 == 0) goto L_0x14cb
            float r0 = r8.reorderIconProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x14e1
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x14c8
            r8.reorderIconProgress = r1
        L_0x14c8:
            r30 = 1
            goto L_0x14e1
        L_0x14cb:
            float r0 = r8.reorderIconProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x14e1
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.reorderIconProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14df
            r8.reorderIconProgress = r1
        L_0x14df:
            r30 = 1
        L_0x14e1:
            boolean r0 = r8.archiveHidden
            if (r0 == 0) goto L_0x1512
            float r0 = r8.archiveBackgroundProgress
            r1 = 0
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x153f
            float r2 = (float) r14
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x14f9
            r8.archiveBackgroundProgress = r1
        L_0x14f9:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x150f
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x150f:
            r30 = 1
            goto L_0x153f
        L_0x1512:
            float r0 = r8.archiveBackgroundProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x153f
            float r2 = (float) r14
            r3 = 1130758144(0x43660000, float:230.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.archiveBackgroundProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1527
            r8.archiveBackgroundProgress = r1
        L_0x1527:
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            int r0 = r0.getAvatarType()
            r1 = 2
            if (r0 != r1) goto L_0x153d
            org.telegram.ui.Components.AvatarDrawable r0 = r8.avatarDrawable
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r2 = r8.archiveBackgroundProgress
            float r1 = r1.getInterpolation(r2)
            r0.setArchivedAvatarHiddenProgress(r1)
        L_0x153d:
            r30 = 1
        L_0x153f:
            boolean r0 = r8.animatingArchiveAvatar
            if (r0 == 0) goto L_0x1556
            float r0 = r8.animatingArchiveAvatarProgress
            float r1 = (float) r14
            float r0 = r0 + r1
            r8.animatingArchiveAvatarProgress = r0
            r1 = 1126825984(0x432a0000, float:170.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x1554
            r8.animatingArchiveAvatarProgress = r1
            r1 = 0
            r8.animatingArchiveAvatar = r1
        L_0x1554:
            r30 = 1
        L_0x1556:
            boolean r0 = r8.drawRevealBackground
            if (r0 == 0) goto L_0x1587
            float r0 = r8.currentRevealBounceProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x1571
            float r2 = (float) r14
            r3 = 1126825984(0x432a0000, float:170.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealBounceProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1571
            r8.currentRevealBounceProgress = r1
            r30 = 1
        L_0x1571:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x15ab
            float r2 = (float) r14
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 + r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x1584
            r8.currentRevealProgress = r1
        L_0x1584:
            r30 = 1
            goto L_0x15ab
        L_0x1587:
            r1 = 1065353216(0x3var_, float:1.0)
            float r0 = r8.currentRevealBounceProgress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x1595
            r1 = 0
            r8.currentRevealBounceProgress = r1
            r30 = 1
            goto L_0x1596
        L_0x1595:
            r1 = 0
        L_0x1596:
            float r0 = r8.currentRevealProgress
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x15ab
            float r2 = (float) r14
            r3 = 1133903872(0x43960000, float:300.0)
            float r2 = r2 / r3
            float r0 = r0 - r2
            r8.currentRevealProgress = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x15a9
            r8.currentRevealProgress = r1
        L_0x15a9:
            r30 = 1
        L_0x15ab:
            if (r30 == 0) goto L_0x15b0
            r34.invalidate()
        L_0x15b0:
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
    public /* synthetic */ void m1492x2a941262(ValueAnimator valueAnimator) {
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
