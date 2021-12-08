package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private static final int id_chat_compose_panel = 1000;
    private ActionBar actionBar;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private ArrayList<ViewGroup> audioViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    /* access modifiers changed from: private */
    public ChatActivityEnterView chatActivityEnterView;
    /* access modifiers changed from: private */
    public int classGuid;
    private TextView countText;
    private TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public int currentMessageNum = 0;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject = null;
    private TLRPC.User currentUser;
    private boolean finished = false;
    private ArrayList<ViewGroup> imageViews = new ArrayList<>();
    private boolean isReply;
    private CharSequence lastPrintString;
    private int lastResumedAccount = -1;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    /* access modifiers changed from: private */
    public ViewGroup messageContainer;
    private float moveStartX = -1.0f;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public Runnable onAnimationEndRunnable = null;
    private TextView onlineTextView;
    /* access modifiers changed from: private */
    public RelativeLayout popupContainer;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> popupMessages = new ArrayList<>();
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private MessageObject[] setMessageObjects = new MessageObject[3];
    /* access modifiers changed from: private */
    public boolean startedMoving = false;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private ArrayList<ViewGroup> textViews = new ArrayList<>();
    private VelocityTracker velocityTracker = null;
    private PowerManager.WakeLock wakeLock = null;

    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        public FrameLayoutTouch(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FrameLayoutTouch(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy((MotionEvent) null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this);
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable(false);
        this.statusDrawables[1] = new RecordStatusDrawable(false);
        this.statusDrawables[2] = new SendingFileDrawable(false);
        this.statusDrawables[3] = new PlayingGameDrawable(false, (Theme.ResourcesProvider) null);
        this.statusDrawables[4] = new RoundStatusDrawable(false);
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(this) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int heightSize;
                int mode = View.MeasureSpec.getMode(widthMeasureSpec);
                int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize2);
                if (measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    heightSize = heightSize2 - PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                } else {
                    heightSize = heightSize2;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + heightSize), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int paddingBottom = measureKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            int measuredHeight = getMeasuredHeight();
                            if (paddingBottom != 0) {
                                measuredHeight -= paddingBottom;
                            }
                            childTop = measuredHeight;
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                            childTop = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - child.getMeasuredHeight()) - lp.bottomMargin;
                            childLeft = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - child.getMeasuredWidth()) - lp.rightMargin;
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        setContentView(contentView);
        contentView.setBackgroundColor(-NUM);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        contentView.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r10 = new RelativeLayout(this) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int w = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int h = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int a = 0; a < getChildCount(); a++) {
                    View v = getChildAt(a);
                    if (v.getTag() instanceof String) {
                        v.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(h - AndroidUtilities.dp(3.0f), NUM));
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                for (int a = 0; a < getChildCount(); a++) {
                    View v = getChildAt(a);
                    if (v.getTag() instanceof String) {
                        v.layout(v.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), v.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        this.popupContainer = r10;
        r10.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (chatActivityEnterView2 != null) {
            chatActivityEnterView2.onDestroy();
        }
        ChatActivityEnterView chatActivityEnterView3 = new ChatActivityEnterView(this, contentView, (ChatActivity) null, false);
        this.chatActivityEnterView = chatActivityEnterView3;
        chatActivityEnterView3.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
            public /* synthetic */ void bottomPanelTranslationYChanged(float f) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$bottomPanelTranslationYChanged(this, f);
            }

            public /* synthetic */ int getContentViewHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
            }

            public /* synthetic */ TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
            }

            public /* synthetic */ boolean hasForwardingMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
            }

            public /* synthetic */ boolean hasScheduledMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
            }

            public /* synthetic */ int measureKeyboardHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
            }

            public /* synthetic */ void onTrendingStickersShowed(boolean z) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z);
            }

            public /* synthetic */ void openScheduledMessages() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
            }

            public /* synthetic */ void prepareMessageSending() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
            }

            public /* synthetic */ void scrollToSendingMessage() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
            }

            public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                        PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                    }
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, 0, true, 0);
                    MessageObject unused = PopupNotificationActivity.this.currentMessageObject = null;
                    PopupNotificationActivity.this.getNewMessage();
                }
            }

            public void onTextChanged(CharSequence text, boolean big) {
            }

            public void onTextSelectionChanged(int start, int end) {
            }

            public void onTextSpansChanged(CharSequence text) {
            }

            public void onStickersExpandedChange() {
            }

            public void onSwitchRecordMode(boolean video) {
            }

            public void onPreAudioVideoRecord() {
            }

            public void onMessageEditEnd(boolean loading) {
            }

            public void needSendTyping() {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, 0, PopupNotificationActivity.this.classGuid);
                }
            }

            public void onAttachButtonHidden() {
            }

            public void onAttachButtonShow() {
            }

            public void onWindowSizeChanged(int size) {
            }

            public void onStickersTab(boolean opened) {
            }

            public void didPressAttachButton() {
            }

            public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
            }

            public void needStartRecordAudio(int state) {
            }

            public void needChangeVideoPreviewState(int state, float seekProgress) {
            }

            public void needShowMediaBanHint() {
            }

            public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
            }

            public void onSendLongClick() {
            }

            public void onAudioVideoInterfaceUpdated() {
            }
        });
        FrameLayoutTouch frameLayoutTouch = new FrameLayoutTouch(this);
        this.messageContainer = frameLayoutTouch;
        this.popupContainer.addView(frameLayoutTouch, 0);
        ActionBar actionBar2 = new ActionBar(this);
        this.actionBar = actionBar2;
        actionBar2.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem view = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        TextView textView = new TextView(this);
        this.countText = textView;
        textView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        view.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        FrameLayout frameLayout = new FrameLayout(this);
        this.avatarContainer = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        BackupImageView backupImageView = new BackupImageView(this);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams22.width = AndroidUtilities.dp(42.0f);
        layoutParams22.height = AndroidUtilities.dp(42.0f);
        layoutParams22.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams22);
        TextView textView2 = new TextView(this);
        this.nameTextView = textView2;
        textView2.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.avatarContainer.addView(this.nameTextView);
        FrameLayout.LayoutParams layoutParams23 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams23.width = -2;
        layoutParams23.height = -2;
        layoutParams23.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams23.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams23.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams23);
        TextView textView3 = new TextView(this);
        this.onlineTextView = textView3;
        textView3.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        FrameLayout.LayoutParams layoutParams24 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams24.width = -2;
        layoutParams24.height = -2;
        layoutParams24.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams24.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams24.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams24);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PopupNotificationActivity.this.onFinish();
                    PopupNotificationActivity.this.finish();
                } else if (id == 1) {
                    PopupNotificationActivity.this.openCurrentMessage();
                } else if (id == 2) {
                    PopupNotificationActivity.this.switchToNextMessage();
                }
            }
        });
        PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "screen");
        this.wakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(this, newConfig);
        fixLayout();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults[0] != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new PopupNotificationActivity$$ExternalSyntheticLambda0(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    /* renamed from: lambda$onRequestPermissionsResult$0$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3650xac2d2ac6(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
        }
    }

    private void switchToPreviousMessage() {
        if (this.popupMessages.size() > 1) {
            int i = this.currentMessageNum;
            if (i > 0) {
                this.currentMessageNum = i - 1;
            } else {
                this.currentMessageNum = this.popupMessages.size() - 1;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
        }
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            Runnable runnable = this.onAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    public boolean onTouchEventMy(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent2 != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent2 != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            float f = this.moveStartX;
            int diff = (int) (x - f);
            if (f != -1.0f && !this.startedMoving && Math.abs(diff) > AndroidUtilities.dp(10.0f)) {
                this.startedMoving = true;
                this.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                diff = 0;
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker2.clear();
                }
            }
            if (this.startedMoving) {
                if (this.leftView == null && diff > 0) {
                    diff = 0;
                }
                if (this.rightView == null && diff < 0) {
                    diff = 0;
                }
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.addMovement(motionEvent2);
                }
                applyViewsLayoutParams(diff);
            }
        } else if (motionEvent2 == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent2 == null || !this.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                int diff2 = (int) (motionEvent.getX() - this.moveStartX);
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                float moveDiff = 0.0f;
                int forceMove = 0;
                View otherView = null;
                View otherButtonsView = null;
                VelocityTracker velocityTracker4 = this.velocityTracker;
                if (velocityTracker4 != null) {
                    velocityTracker4.computeCurrentVelocity(1000);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        forceMove = 1;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        forceMove = 2;
                    }
                }
                if ((forceMove == 1 || diff2 > width / 3) && this.leftView != null) {
                    moveDiff = ((float) width) - this.centerView.getTranslationX();
                    otherView = this.leftView;
                    otherButtonsView = this.leftButtonsView;
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$ExternalSyntheticLambda6(this);
                } else if ((forceMove == 2 || diff2 < (-width) / 3) && this.rightView != null) {
                    moveDiff = ((float) (-width)) - this.centerView.getTranslationX();
                    otherView = this.rightView;
                    otherButtonsView = this.rightButtonsView;
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$ExternalSyntheticLambda7(this);
                } else if (this.centerView.getTranslationX() != 0.0f) {
                    moveDiff = -this.centerView.getTranslationX();
                    otherView = diff2 > 0 ? this.leftView : this.rightView;
                    otherButtonsView = diff2 > 0 ? this.leftButtonsView : this.rightButtonsView;
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$ExternalSyntheticLambda8(this);
                }
                if (moveDiff != 0.0f) {
                    int time = (int) (Math.abs(moveDiff / ((float) width)) * 200.0f);
                    ArrayList<Animator> animators = new ArrayList<>();
                    ViewGroup viewGroup = this.centerView;
                    animators.add(ObjectAnimator.ofFloat(viewGroup, "translationX", new float[]{viewGroup.getTranslationX() + moveDiff}));
                    ViewGroup viewGroup2 = this.centerButtonsView;
                    if (viewGroup2 != null) {
                        animators.add(ObjectAnimator.ofFloat(viewGroup2, "translationX", new float[]{viewGroup2.getTranslationX() + moveDiff}));
                    }
                    if (otherView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherView, "translationX", new float[]{otherView.getTranslationX() + moveDiff}));
                    }
                    if (otherButtonsView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherButtonsView, "translationX", new float[]{otherButtonsView.getTranslationX() + moveDiff}));
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration((long) time);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                                Runnable unused = PopupNotificationActivity.this.onAnimationEndRunnable = null;
                            }
                        }
                    });
                    animatorSet.start();
                    this.animationInProgress = true;
                    this.animationStartTime = System.currentTimeMillis();
                }
            }
            VelocityTracker velocityTracker5 = this.velocityTracker;
            if (velocityTracker5 != null) {
                velocityTracker5.recycle();
                this.velocityTracker = null;
            }
            this.startedMoving = false;
            this.moveStartX = -1.0f;
        }
        return this.startedMoving;
    }

    /* renamed from: lambda$onTouchEventMy$1$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3651xe54846bc() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* renamed from: lambda$onTouchEventMy$2$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3652xca89b57d() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* renamed from: lambda$onTouchEventMy$3$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3653xafcb243e() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    /* access modifiers changed from: private */
    public void applyViewsLayoutParams(int xOffset) {
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != widht) {
                layoutParams.width = widht;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((float) ((-widht) + xOffset));
        }
        ViewGroup viewGroup2 = this.leftButtonsView;
        if (viewGroup2 != null) {
            viewGroup2.setTranslationX((float) ((-widht) + xOffset));
        }
        ViewGroup viewGroup3 = this.centerView;
        if (viewGroup3 != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewGroup3.getLayoutParams();
            if (layoutParams2.width != widht) {
                layoutParams2.width = widht;
                this.centerView.setLayoutParams(layoutParams2);
            }
            this.centerView.setTranslationX((float) xOffset);
        }
        ViewGroup viewGroup4 = this.centerButtonsView;
        if (viewGroup4 != null) {
            viewGroup4.setTranslationX((float) xOffset);
        }
        ViewGroup viewGroup5 = this.rightView;
        if (viewGroup5 != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup5.getLayoutParams();
            if (layoutParams3.width != widht) {
                layoutParams3.width = widht;
                this.rightView.setLayoutParams(layoutParams3);
            }
            this.rightView.setTranslationX((float) (widht + xOffset));
        }
        ViewGroup viewGroup6 = this.rightButtonsView;
        if (viewGroup6 != null) {
            viewGroup6.setTranslationX((float) (widht + xOffset));
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int num, boolean applyOffset) {
        TLRPC.ReplyMarkup markup;
        int num2 = num;
        if (this.popupMessages.size() == 1 && (num2 < 0 || num2 >= this.popupMessages.size())) {
            return null;
        }
        if (num2 == -1) {
            num2 = this.popupMessages.size() - 1;
        } else if (num2 == this.popupMessages.size()) {
            num2 = 0;
        }
        LinearLayout view = null;
        MessageObject messageObject = this.popupMessages.get(num2);
        int buttonsCount = 0;
        TLRPC.ReplyMarkup markup2 = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() == 777000 && markup2 != null) {
            ArrayList<TLRPC.TL_keyboardButtonRow> rows = markup2.rows;
            int size = rows.size();
            for (int a = 0; a < size; a++) {
                TLRPC.TL_keyboardButtonRow row = rows.get(a);
                int size2 = row.buttons.size();
                for (int b = 0; b < size2; b++) {
                    if (row.buttons.get(b) instanceof TLRPC.TL_keyboardButtonCallback) {
                        buttonsCount++;
                    }
                }
            }
        }
        int account = messageObject.currentAccount;
        if (buttonsCount > 0) {
            ArrayList<TLRPC.TL_keyboardButtonRow> rows2 = markup2.rows;
            int size3 = rows2.size();
            for (int a2 = 0; a2 < size3; a2++) {
                TLRPC.TL_keyboardButtonRow row2 = rows2.get(a2);
                int b2 = 0;
                int size22 = row2.buttons.size();
                while (b2 < size22) {
                    TLRPC.KeyboardButton button = row2.buttons.get(b2);
                    if (button instanceof TLRPC.TL_keyboardButtonCallback) {
                        if (view == null) {
                            view = new LinearLayout(this);
                            view.setOrientation(0);
                            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            view.setWeightSum(100.0f);
                            view.setTag("b");
                            view.setOnTouchListener(PopupNotificationActivity$$ExternalSyntheticLambda5.INSTANCE);
                        }
                        TextView textView = new TextView(this);
                        markup = markup2;
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(button.text.toUpperCase());
                        textView.setTag(button);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        view.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) buttonsCount)));
                        textView.setOnClickListener(new PopupNotificationActivity$$ExternalSyntheticLambda1(account, messageObject));
                    } else {
                        markup = markup2;
                    }
                    b2++;
                    markup2 = markup;
                }
            }
        }
        if (view != null) {
            int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (applyOffset) {
                int i = this.currentMessageNum;
                if (num2 == i) {
                    view.setTranslationX(0.0f);
                } else if (num2 == i - 1) {
                    view.setTranslationX((float) (-widht));
                } else if (num2 == i + 1) {
                    view.setTranslationX((float) widht);
                }
            }
            this.popupContainer.addView(view, layoutParams);
        }
        return view;
    }

    static /* synthetic */ boolean lambda$getButtonsViewForMessage$4(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ void lambda$getButtonsViewForMessage$5(int account, MessageObject messageObject, View v) {
        TLRPC.KeyboardButton button1 = (TLRPC.KeyboardButton) v.getTag();
        if (button1 != null) {
            SendMessagesHelper.getInstance(account).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), button1.data);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x037e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.ViewGroup getViewForMessage(int r30, boolean r31) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.popupMessages
            int r2 = r2.size()
            r3 = 0
            r4 = 1
            if (r2 != r4) goto L_0x0019
            if (r1 < 0) goto L_0x0018
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.popupMessages
            int r2 = r2.size()
            if (r1 < r2) goto L_0x0019
        L_0x0018:
            return r3
        L_0x0019:
            r2 = -1
            if (r1 != r2) goto L_0x0025
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.popupMessages
            int r5 = r5.size()
            int r1 = r5 + -1
            goto L_0x002e
        L_0x0025:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.popupMessages
            int r5 = r5.size()
            if (r1 != r5) goto L_0x002e
            r1 = 0
        L_0x002e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.popupMessages
            java.lang.Object r5 = r5.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            int r6 = r5.type
            r7 = 1098907648(0x41800000, float:16.0)
            r8 = 4
            java.lang.String r10 = "windowBackgroundWhiteBlackText"
            r11 = 17
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 1092616192(0x41200000, float:10.0)
            r15 = 0
            if (r6 == r4) goto L_0x004e
            int r6 = r5.type
            if (r6 != r8) goto L_0x004b
            goto L_0x004e
        L_0x004b:
            r6 = r5
            goto L_0x021e
        L_0x004e:
            boolean r6 = r5.isSecretMedia()
            if (r6 != 0) goto L_0x021d
            java.util.ArrayList<android.view.ViewGroup> r6 = r0.imageViews
            int r6 = r6.size()
            r16 = 312(0x138, float:4.37E-43)
            r17 = 311(0x137, float:4.36E-43)
            if (r6 <= 0) goto L_0x006f
            java.util.ArrayList<android.view.ViewGroup> r6 = r0.imageViews
            java.lang.Object r6 = r6.get(r15)
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            java.util.ArrayList<android.view.ViewGroup> r7 = r0.imageViews
            r7.remove(r15)
            r3 = r6
            goto L_0x00df
        L_0x006f:
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r0)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r3.setPadding(r8, r13, r9, r14)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r15)
            r3.setBackgroundDrawable(r8)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r12)
            r6.addView(r3, r8)
            org.telegram.ui.Components.BackupImageView r8 = new org.telegram.ui.Components.BackupImageView
            r8.<init>(r0)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r17)
            r8.setTag(r9)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r12)
            r3.addView(r8, r9)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r0)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setTextColor(r10)
            r9.setTextSize(r4, r7)
            r9.setGravity(r11)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r16)
            r9.setTag(r7)
            r7 = -2
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r7, (int) r11)
            r3.addView(r9, r7)
            r7 = 2
            java.lang.Integer r10 = java.lang.Integer.valueOf(r7)
            r6.setTag(r10)
            org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda2
            r7.<init>(r0)
            r6.setOnClickListener(r7)
            r3 = r6
        L_0x00df:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r16)
            android.view.View r6 = r3.findViewWithTag(r6)
            r13 = r6
            android.widget.TextView r13 = (android.widget.TextView) r13
            java.lang.Integer r6 = java.lang.Integer.valueOf(r17)
            android.view.View r6 = r3.findViewWithTag(r6)
            r14 = r6
            org.telegram.ui.Components.BackupImageView r14 = (org.telegram.ui.Components.BackupImageView) r14
            r14.setAspectFit(r4)
            int r6 = r5.type
            r12 = 8
            r7 = 100
            if (r6 != r4) goto L_0x01a9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r5.photoThumbs
            int r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r11 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r5.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            r16 = 0
            if (r11 == 0) goto L_0x0185
            r6 = 1
            int r7 = r5.type
            if (r7 != r4) goto L_0x0129
            org.telegram.tgnet.TLRPC$Message r7 = r5.messageOwner
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToMessage(r7)
            boolean r8 = r7.exists()
            if (r8 != 0) goto L_0x0129
            r6 = 0
            r17 = r6
            goto L_0x012b
        L_0x0129:
            r17 = r6
        L_0x012b:
            boolean r6 = r5.needDrawBluredPreview()
            if (r6 != 0) goto L_0x0180
            if (r17 != 0) goto L_0x0162
            int r6 = r5.currentAccount
            org.telegram.messenger.DownloadController r6 = org.telegram.messenger.DownloadController.getInstance(r6)
            boolean r6 = r6.canDownloadMedia((org.telegram.messenger.MessageObject) r5)
            if (r6 == 0) goto L_0x0142
            r4 = r10
            r2 = r11
            goto L_0x0164
        L_0x0142:
            if (r10 == 0) goto L_0x015d
            org.telegram.tgnet.TLObject r6 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r10, r6)
            r9 = 0
            r18 = 0
            java.lang.String r8 = "100_100_b"
            r6 = r14
            r4 = r10
            r10 = r18
            r2 = r11
            r11 = r5
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11)
            r16 = 1
            r15 = 8
            goto L_0x0189
        L_0x015d:
            r4 = r10
            r2 = r11
            r15 = 8
            goto L_0x0189
        L_0x0162:
            r4 = r10
            r2 = r11
        L_0x0164:
            org.telegram.tgnet.TLObject r6 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            org.telegram.tgnet.TLObject r6 = r5.photoThumbsObject
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForObject(r4, r6)
            int r11 = r2.size
            java.lang.String r8 = "100_100"
            java.lang.String r10 = "100_100_b"
            r6 = r14
            r15 = 8
            r12 = r5
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (int) r11, (java.lang.Object) r12)
            r16 = 1
            goto L_0x0189
        L_0x0180:
            r4 = r10
            r2 = r11
            r15 = 8
            goto L_0x0189
        L_0x0185:
            r4 = r10
            r2 = r11
            r15 = 8
        L_0x0189:
            if (r16 != 0) goto L_0x019f
            r14.setVisibility(r15)
            r6 = 0
            r13.setVisibility(r6)
            int r7 = org.telegram.messenger.SharedConfig.fontSize
            float r7 = (float) r7
            r8 = 2
            r13.setTextSize(r8, r7)
            java.lang.CharSequence r7 = r5.messageText
            r13.setText(r7)
            goto L_0x01a6
        L_0x019f:
            r6 = 0
            r14.setVisibility(r6)
            r13.setVisibility(r15)
        L_0x01a6:
            r6 = r5
            goto L_0x021b
        L_0x01a9:
            r15 = 8
            int r2 = r5.type
            r4 = 4
            if (r2 != r4) goto L_0x021a
            r13.setVisibility(r15)
            java.lang.CharSequence r2 = r5.messageText
            r13.setText(r2)
            r2 = 0
            r14.setVisibility(r2)
            org.telegram.tgnet.TLRPC$Message r2 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            double r11 = r2.lat
            double r9 = r2._long
            int r4 = r5.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.mapProvider
            r6 = 2
            if (r4 != r6) goto L_0x01f9
            float r8 = org.telegram.messenger.AndroidUtilities.density
            r30 = r5
            double r4 = (double) r8
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r4 = java.lang.Math.min(r6, r4)
            r5 = 15
            org.telegram.messenger.WebFile r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r2, r7, r7, r5, r4)
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForWebFile(r4)
            r8 = 0
            r4 = 0
            r5 = 0
            r6 = r14
            r15 = r9
            r9 = r4
            r10 = r5
            r4 = r11
            r11 = r30
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11)
            r6 = r30
            goto L_0x021b
        L_0x01f9:
            r30 = r5
            r15 = r9
            r4 = r11
            r6 = r30
            int r7 = r6.currentAccount
            r24 = 100
            r25 = 100
            r26 = 1
            r27 = 15
            r28 = -1
            r19 = r7
            r20 = r4
            r22 = r15
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r19, r20, r22, r24, r25, r26, r27, r28)
            r8 = 0
            r14.setImage(r7, r8, r8)
            goto L_0x021b
        L_0x021a:
            r6 = r5
        L_0x021b:
            goto L_0x036d
        L_0x021d:
            r6 = r5
        L_0x021e:
            int r2 = r6.type
            r3 = 2
            if (r2 != r3) goto L_0x02bf
            java.util.ArrayList<android.view.ViewGroup> r2 = r0.audioViews
            int r2 = r2.size()
            r3 = 300(0x12c, float:4.2E-43)
            if (r2 <= 0) goto L_0x0246
            java.util.ArrayList<android.view.ViewGroup> r2 = r0.audioViews
            r4 = 0
            java.lang.Object r2 = r2.get(r4)
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            java.util.ArrayList<android.view.ViewGroup> r5 = r0.audioViews
            r5.remove(r4)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            android.view.View r3 = r2.findViewWithTag(r3)
            org.telegram.ui.Components.PopupAudioView r3 = (org.telegram.ui.Components.PopupAudioView) r3
            goto L_0x02aa
        L_0x0246:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r0)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r4.setPadding(r5, r7, r8, r9)
            r5 = 0
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r4.setBackgroundDrawable(r7)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r12)
            r2.addView(r4, r7)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r0)
            r7 = -1
            r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r9 = 17
            r10 = 1101004800(0x41a00000, float:20.0)
            r11 = 0
            r12 = 1101004800(0x41a00000, float:20.0)
            r13 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r4.addView(r5, r7)
            org.telegram.ui.Components.PopupAudioView r7 = new org.telegram.ui.Components.PopupAudioView
            r7.<init>(r0)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r7.setTag(r3)
            r5.addView(r7)
            r3 = 3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.setTag(r3)
            org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda3 r3 = new org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda3
            r3.<init>(r0)
            r2.setOnClickListener(r3)
            r3 = r7
        L_0x02aa:
            r3.setMessageObject(r6)
            int r4 = r6.currentAccount
            org.telegram.messenger.DownloadController r4 = org.telegram.messenger.DownloadController.getInstance(r4)
            boolean r4 = r4.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r4 == 0) goto L_0x02bc
            r3.downloadAudioIfNeed()
        L_0x02bc:
            r3 = r2
            goto L_0x036d
        L_0x02bf:
            java.util.ArrayList<android.view.ViewGroup> r2 = r0.textViews
            int r2 = r2.size()
            r3 = 301(0x12d, float:4.22E-43)
            if (r2 <= 0) goto L_0x02d9
            java.util.ArrayList<android.view.ViewGroup> r2 = r0.textViews
            r4 = 0
            java.lang.Object r2 = r2.get(r4)
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            java.util.ArrayList<android.view.ViewGroup> r5 = r0.textViews
            r5.remove(r4)
            goto L_0x0356
        L_0x02d9:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r0)
            android.widget.ScrollView r4 = new android.widget.ScrollView
            r4.<init>(r0)
            r5 = 1
            r4.setFillViewport(r5)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r12)
            r2.addView(r4, r8)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r0)
            r8 = 0
            r5.setOrientation(r8)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r5.setBackgroundDrawable(r9)
            r8 = -2
            r9 = -1
            r12 = 1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createScroll(r9, r8, r12)
            r4.addView(r5, r13)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.setPadding(r8, r9, r12, r13)
            org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda4
            r8.<init>(r0)
            r5.setOnClickListener(r8)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r0)
            r9 = 1
            r8.setTextSize(r9, r7)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
            r8.setTag(r7)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setTextColor(r7)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setLinkTextColor(r7)
            r8.setGravity(r11)
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r7, (int) r11)
            r5.addView(r8, r7)
            r7 = 1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r7)
            r2.setTag(r9)
        L_0x0356:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            android.view.View r3 = r2.findViewWithTag(r3)
            android.widget.TextView r3 = (android.widget.TextView) r3
            int r4 = org.telegram.messenger.SharedConfig.fontSize
            float r4 = (float) r4
            r5 = 2
            r3.setTextSize(r5, r4)
            java.lang.CharSequence r4 = r6.messageText
            r3.setText(r4)
            r3 = r2
        L_0x036d:
            android.view.ViewParent r2 = r3.getParent()
            if (r2 != 0) goto L_0x0378
            android.view.ViewGroup r2 = r0.messageContainer
            r2.addView(r3)
        L_0x0378:
            r2 = 0
            r3.setVisibility(r2)
            if (r31 == 0) goto L_0x03b9
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r2.x
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r4
            android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r5 = 51
            r4.gravity = r5
            r5 = -1
            r4.height = r5
            r4.width = r2
            int r5 = r0.currentMessageNum
            if (r1 != r5) goto L_0x03a1
            r5 = 0
            r3.setTranslationX(r5)
            goto L_0x03b3
        L_0x03a1:
            int r7 = r5 + -1
            if (r1 != r7) goto L_0x03ab
            int r5 = -r2
            float r5 = (float) r5
            r3.setTranslationX(r5)
            goto L_0x03b3
        L_0x03ab:
            r7 = 1
            int r5 = r5 + r7
            if (r1 != r5) goto L_0x03b3
            float r5 = (float) r2
            r3.setTranslationX(r5)
        L_0x03b3:
            r3.setLayoutParams(r4)
            r3.invalidate()
        L_0x03b9:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
    }

    /* renamed from: lambda$getViewForMessage$6$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3647x2b03983a(View v) {
        openCurrentMessage();
    }

    /* renamed from: lambda$getViewForMessage$7$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3648x104506fb(View v) {
        openCurrentMessage();
    }

    /* renamed from: lambda$getViewForMessage$8$org-telegram-ui-PopupNotificationActivity  reason: not valid java name */
    public /* synthetic */ void m3649xvar_bc(View v) {
        openCurrentMessage();
    }

    private void reuseButtonsView(ViewGroup view) {
        if (view != null) {
            this.popupContainer.removeView(view);
        }
    }

    private void reuseView(ViewGroup view) {
        if (view != null) {
            int tag = ((Integer) view.getTag()).intValue();
            view.setVisibility(8);
            if (tag == 1) {
                this.textViews.add(view);
            } else if (tag == 2) {
                this.imageViews.add(view);
            } else if (tag == 3) {
                this.audioViews.add(view);
            }
        }
    }

    private void prepareLayouts(int move) {
        MessageObject messageObject;
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (move == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            int a = this.currentMessageNum - 1;
            while (true) {
                int i = this.currentMessageNum;
                if (a >= i + 2) {
                    break;
                }
                if (a == i - 1) {
                    this.leftView = getViewForMessage(a, true);
                    this.leftButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == i) {
                    this.centerView = getViewForMessage(a, true);
                    this.centerButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == i + 1) {
                    this.rightView = getViewForMessage(a, true);
                    this.rightButtonsView = getButtonsViewForMessage(a, true);
                }
                a++;
            }
        } else if (move == 1) {
            reuseView(this.rightView);
            reuseButtonsView(this.rightButtonsView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
            this.rightButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.leftButtonsView;
            this.leftButtonsView = getButtonsViewForMessage(this.currentMessageNum - 1, true);
        } else if (move == 2) {
            reuseView(this.leftView);
            reuseButtonsView(this.leftButtonsView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
            this.leftButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.rightButtonsView;
            this.rightButtonsView = getButtonsViewForMessage(this.currentMessageNum + 1, true);
        } else if (move == 3) {
            ViewGroup viewGroup = this.rightView;
            if (viewGroup != null) {
                float offset = viewGroup.getTranslationX();
                reuseView(this.rightView);
                ViewGroup viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = viewForMessage;
                if (viewForMessage != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewForMessage.getLayoutParams();
                    layoutParams.width = widht;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.setTranslationX(offset);
                    this.rightView.invalidate();
                }
            }
            ViewGroup viewGroup2 = this.rightButtonsView;
            if (viewGroup2 != null) {
                float offset2 = viewGroup2.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                LinearLayout buttonsViewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = buttonsViewForMessage;
                if (buttonsViewForMessage != null) {
                    buttonsViewForMessage.setTranslationX(offset2);
                }
            }
        } else if (move == 4) {
            ViewGroup viewGroup3 = this.leftView;
            if (viewGroup3 != null) {
                float offset3 = viewGroup3.getTranslationX();
                reuseView(this.leftView);
                ViewGroup viewForMessage2 = getViewForMessage(0, false);
                this.leftView = viewForMessage2;
                if (viewForMessage2 != null) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewForMessage2.getLayoutParams();
                    layoutParams2.width = widht;
                    this.leftView.setLayoutParams(layoutParams2);
                    this.leftView.setTranslationX(offset3);
                    this.leftView.invalidate();
                }
            }
            ViewGroup viewGroup4 = this.leftButtonsView;
            if (viewGroup4 != null) {
                float offset4 = viewGroup4.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                LinearLayout buttonsViewForMessage2 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = buttonsViewForMessage2;
                if (buttonsViewForMessage2 != null) {
                    buttonsViewForMessage2.setTranslationX(offset4);
                }
            }
        }
        for (int a2 = 0; a2 < 3; a2++) {
            int num = (this.currentMessageNum - 1) + a2;
            if (this.popupMessages.size() != 1 || (num >= 0 && num < this.popupMessages.size())) {
                if (num == -1) {
                    num = this.popupMessages.size() - 1;
                } else if (num == this.popupMessages.size()) {
                    num = 0;
                }
                messageObject = this.popupMessages.get(num);
            } else {
                messageObject = null;
            }
            this.setMessageObjects[a2] = messageObject;
        }
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int padding = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), padding, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), padding);
                    return true;
                }
            });
        }
        ViewGroup viewGroup = this.messageContainer;
        if (viewGroup != null) {
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving) {
                        return true;
                    }
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                    layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                    layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    PopupNotificationActivity.this.messageContainer.setLayoutParams(layoutParams);
                    PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        this.isReply = intent != null && intent.getBooleanExtra("force", false);
        this.popupMessages.clear();
        if (this.isReply) {
            int account = UserConfig.selectedAccount;
            if (intent != null) {
                account = intent.getIntExtra("currentAccount", account);
            }
            if (UserConfig.isValidAccount(account)) {
                this.popupMessages.addAll(NotificationsController.getInstance(account).popupReplyMessages);
            } else {
                return;
            }
        } else {
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(a).popupMessages);
                }
            }
        }
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    /* access modifiers changed from: private */
    public void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        boolean found = false;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int a = 0;
            int size = this.popupMessages.size();
            while (true) {
                if (a >= size) {
                    break;
                }
                MessageObject messageObject = this.popupMessages.get(a);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = a;
                    found = true;
                    break;
                }
                a++;
            }
        }
        if (!found) {
            this.currentMessageNum = 0;
            this.currentMessageObject = this.popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        } else if (this.startedMoving) {
            if (this.currentMessageNum == this.popupMessages.size() - 1) {
                prepareLayouts(3);
            } else if (this.currentMessageNum == 1) {
                prepareLayouts(4);
            }
        }
        this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
    }

    /* access modifiers changed from: private */
    public void openCurrentMessage() {
        if (this.currentMessageObject != null) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            long dialogId = this.currentMessageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                intent.putExtra("encId", DialogObject.getEncryptedChatId(dialogId));
            } else if (DialogObject.isUserDialog(dialogId)) {
                intent.putExtra("userId", dialogId);
            } else if (DialogObject.isChatDialog(dialogId)) {
                intent.putExtra("chatId", -dialogId);
            }
            intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
            intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
            intent.setFlags(32768);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void updateInterfaceForCurrentMessage(int move) {
        if (this.actionBar != null) {
            if (this.lastResumedAccount != this.currentMessageObject.currentAccount) {
                int i = this.lastResumedAccount;
                if (i >= 0) {
                    ConnectionsManager.getInstance(i).setAppPaused(true, false);
                }
                int i2 = this.currentMessageObject.currentAccount;
                this.lastResumedAccount = i2;
                ConnectionsManager.getInstance(i2).setAppPaused(false, false);
            }
            this.currentChat = null;
            this.currentUser = null;
            long dialogId = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialogId, this.currentMessageObject.currentAccount);
            if (DialogObject.isEncryptedDialog(dialogId)) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId))).user_id));
            } else if (DialogObject.isUserDialog(dialogId)) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(dialogId));
            } else if (DialogObject.isChatDialog(dialogId)) {
                this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Long.valueOf(-dialogId));
                if (this.currentMessageObject.isFromUser()) {
                    this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentMessageObject.messageOwner.from_id.user_id));
                }
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null) {
                this.nameTextView.setText(chat.title);
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    this.onlineTextView.setText(UserObject.getUserName(user));
                } else {
                    this.onlineTextView.setText((CharSequence) null);
                }
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            } else {
                TLRPC.User user2 = this.currentUser;
                if (user2 != null) {
                    this.nameTextView.setText(UserObject.getUserName(user2));
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(NUM, 0, 0, 0);
                        this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    } else {
                        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        this.nameTextView.setCompoundDrawablePadding(0);
                    }
                }
            }
            prepareLayouts(move);
            updateSubtitle();
            checkAndUpdateAvatar();
            applyViewsLayoutParams(0);
        }
    }

    private void updateSubtitle() {
        TLRPC.User user;
        if (this.actionBar != null && this.currentMessageObject != null && this.currentChat == null && (user = this.currentUser) != null) {
            if (user.id / 1000 == 777 || this.currentUser.id / 1000 == 333 || ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Long.valueOf(this.currentUser.id)) != null || (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                TextView textView = this.nameTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                textView.setText(instance.format("+" + this.currentUser.phone));
            }
            TLRPC.User user2 = this.currentUser;
            if (user2 == null || user2.id != 777000) {
                CharSequence printString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
                if (printString == null || printString.length() == 0) {
                    this.lastPrintString = null;
                    setTypingAnimation(false);
                    TLRPC.User user3 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id));
                    if (user3 != null) {
                        this.currentUser = user3;
                    }
                    this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
                    return;
                }
                this.lastPrintString = printString;
                this.onlineTextView.setText(printString);
                setTypingAnimation(true);
                return;
            }
            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", NUM));
        }
    }

    private void checkAndUpdateAvatar() {
        TLRPC.User user;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            if (this.currentChat != null) {
                TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    if (this.avatarImageView != null) {
                        this.avatarImageView.setForUserOrChat(chat, new AvatarDrawable(this.currentChat));
                    }
                }
            } else if (this.currentUser != null && (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id))) != null) {
                this.currentUser = user;
                if (this.avatarImageView != null) {
                    this.avatarImageView.setForUserOrChat(user, new AvatarDrawable(this.currentUser));
                }
            }
        }
    }

    private void setTypingAnimation(boolean start) {
        if (this.actionBar != null) {
            if (start) {
                try {
                    Integer type = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingStringType(this.currentMessageObject.getDialogId(), 0);
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[type.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    for (int a = 0; a < this.statusDrawables.length; a++) {
                        if (a == type.intValue()) {
                            this.statusDrawables[a].start();
                        } else {
                            this.statusDrawables[a].stop();
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(0);
                int a2 = 0;
                while (true) {
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (a2 < statusDrawableArr.length) {
                        statusDrawableArr[a2].stop();
                        a2++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (chatActivityEnterView2 != null) {
            chatActivityEnterView2.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (chatActivityEnterView2 != null) {
            chatActivityEnterView2.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        int i = this.lastResumedAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView;
        PopupAudioView cell;
        MessageObject messageObject;
        PopupAudioView cell2;
        MessageObject messageObject2;
        MessageObject messageObject3;
        if (id == NotificationCenter.appDidLogout) {
            if (account == this.lastResumedAccount) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.pushMessagesUpdated) {
            if (!this.isReply) {
                this.popupMessages.clear();
                for (int a = 0; a < 3; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        this.popupMessages.addAll(NotificationsController.getInstance(a).popupMessages);
                    }
                }
                getNewMessage();
                if (!this.popupMessages.isEmpty()) {
                    for (int a2 = 0; a2 < 3; a2++) {
                        int num = (this.currentMessageNum - 1) + a2;
                        if (this.popupMessages.size() != 1 || (num >= 0 && num < this.popupMessages.size())) {
                            if (num == -1) {
                                num = this.popupMessages.size() - 1;
                            } else if (num == this.popupMessages.size()) {
                                num = 0;
                            }
                            messageObject3 = this.popupMessages.get(num);
                        } else {
                            messageObject3 = null;
                        }
                        if (this.setMessageObjects[a2] != messageObject3) {
                            updateInterfaceForCurrentMessage(0);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject != null && account == this.lastResumedAccount) {
                int updateMask = args[0].intValue();
                if (!((MessagesController.UPDATE_MASK_NAME & updateMask) == 0 && (MessagesController.UPDATE_MASK_STATUS & updateMask) == 0 && (MessagesController.UPDATE_MASK_CHAT_NAME & updateMask) == 0 && (MessagesController.UPDATE_MASK_CHAT_MEMBERS & updateMask) == 0)) {
                    updateSubtitle();
                }
                if (!((MessagesController.UPDATE_MASK_AVATAR & updateMask) == 0 && (MessagesController.UPDATE_MASK_CHAT_AVATAR & updateMask) == 0)) {
                    checkAndUpdateAvatar();
                }
                if ((MessagesController.UPDATE_MASK_USER_PRINT & updateMask) != 0) {
                    CharSequence printString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
                    CharSequence charSequence = this.lastPrintString;
                    if ((charSequence != null && printString == null) || ((charSequence == null && printString != null) || (charSequence != null && !charSequence.equals(printString)))) {
                        updateSubtitle();
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset) {
            Integer mid = args[0];
            ViewGroup viewGroup = this.messageContainer;
            if (viewGroup != null) {
                int count = viewGroup.getChildCount();
                for (int a3 = 0; a3 < count; a3++) {
                    View view = this.messageContainer.getChildAt(a3);
                    if (((Integer) view.getTag()).intValue() == 3 && (messageObject2 = cell2.getMessageObject()) != null && messageObject2.currentAccount == account && messageObject2.getId() == mid.intValue()) {
                        (cell2 = (PopupAudioView) view.findViewWithTag(300)).updateButtonState();
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid2 = args[0];
            ViewGroup viewGroup2 = this.messageContainer;
            if (viewGroup2 != null) {
                int count2 = viewGroup2.getChildCount();
                for (int a4 = 0; a4 < count2; a4++) {
                    View view2 = this.messageContainer.getChildAt(a4);
                    if (((Integer) view2.getTag()).intValue() == 3 && (messageObject = cell.getMessageObject()) != null && messageObject.currentAccount == account && messageObject.getId() == mid2.intValue()) {
                        (cell = (PopupAudioView) view2.findViewWithTag(300)).updateProgress();
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            ViewGroup viewGroup3 = this.messageContainer;
            if (viewGroup3 != null) {
                int count3 = viewGroup3.getChildCount();
                for (int a5 = 0; a5 < count3; a5++) {
                    View view3 = this.messageContainer.getChildAt(a5);
                    if (((Integer) view3.getTag()).intValue() == 1 && (textView = (TextView) view3.findViewWithTag(301)) != null) {
                        textView.invalidate();
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoad && account == this.lastResumedAccount) {
            updateSubtitle();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinish() {
        if (!this.finished) {
            this.finished = true;
            if (this.isReply) {
                this.popupMessages.clear();
            }
            for (int a = 0; a < 3; a++) {
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.contactsDidLoad);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
            ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
            if (chatActivityEnterView2 != null) {
                chatActivityEnterView2.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}
