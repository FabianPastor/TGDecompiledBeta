package org.telegram.ui;

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
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_channels_sendAsPeers;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$User;
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
/* loaded from: classes3.dex */
public class PopupNotificationActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private ActionBar actionBar;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private TLRPC$Chat currentChat;
    private TLRPC$User currentUser;
    private boolean isReply;
    private CharSequence lastPrintString;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private TextView nameTextView;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private ArrayList<ViewGroup> textViews = new ArrayList<>();
    private ArrayList<ViewGroup> imageViews = new ArrayList<>();
    private ArrayList<ViewGroup> audioViews = new ArrayList<>();
    private VelocityTracker velocityTracker = null;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private int lastResumedAccount = -1;
    private boolean finished = false;
    private MessageObject currentMessageObject = null;
    private MessageObject[] setMessageObjects = new MessageObject[3];
    private int currentMessageNum = 0;
    private PowerManager.WakeLock wakeLock = null;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private float moveStartX = -1.0f;
    private boolean startedMoving = false;
    private Runnable onAnimationEndRunnable = null;
    private ArrayList<MessageObject> popupMessages = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getButtonsViewForMessage$4(View view, MotionEvent motionEvent) {
        return true;
    }

    /* loaded from: classes3.dex */
    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this);
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable(false);
        this.statusDrawables[1] = new RecordStatusDrawable(false);
        this.statusDrawables[2] = new SendingFileDrawable(false);
        this.statusDrawables[3] = new PlayingGameDrawable(false, null);
        this.statusDrawables[4] = new RoundStatusDrawable(false);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(this) { // from class: org.telegram.ui.PopupNotificationActivity.1
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i2, int i3) {
                View.MeasureSpec.getMode(i2);
                View.MeasureSpec.getMode(i3);
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                setMeasuredDimension(size, size2);
                if (measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    size2 -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt.getVisibility() != 8) {
                        if (!PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                                measureChildWithMargins(childAt, i2, 0, i3, 0);
                            } else {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + size2), NUM));
                            }
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        }
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Removed duplicated region for block: B:23:0x0065  */
            /* JADX WARN: Removed duplicated region for block: B:27:0x0073  */
            /* JADX WARN: Removed duplicated region for block: B:31:0x008b  */
            /* JADX WARN: Removed duplicated region for block: B:35:0x0094  */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    Method dump skipped, instructions count: 231
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }
        };
        setContentView(sizeNotifierFrameLayout);
        sizeNotifierFrameLayout.setBackgroundColor(-NUM);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        sizeNotifierFrameLayout.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        RelativeLayout relativeLayout2 = new RelativeLayout(this) { // from class: org.telegram.ui.PopupNotificationActivity.2
            @Override // android.widget.RelativeLayout, android.view.View
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                int measuredWidth = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int measuredHeight = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int i4 = 0; i4 < getChildCount(); i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt.getTag() instanceof String) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM), View.MeasureSpec.makeMeasureSpec(measuredHeight - AndroidUtilities.dp(3.0f), NUM));
                    }
                }
            }

            @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt = getChildAt(i6);
                    if (childAt.getTag() instanceof String) {
                        childAt.layout(childAt.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), childAt.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        this.popupContainer = relativeLayout2;
        relativeLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        ChatActivityEnterView chatActivityEnterView2 = new ChatActivityEnterView(this, sizeNotifierFrameLayout, null, false);
        this.chatActivityEnterView = chatActivityEnterView2;
        chatActivityEnterView2.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() { // from class: org.telegram.ui.PopupNotificationActivity.3
            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void bottomPanelTranslationYChanged(float f) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$bottomPanelTranslationYChanged(this, f);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void didPressAttachButton() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ int getContentViewHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ TLRPC$TL_channels_sendAsPeers getSendAsPeers() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ boolean hasForwardingMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ boolean hasScheduledMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ int measureKeyboardHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needChangeVideoPreviewState(int i2, float f) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needShowMediaBanHint() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needStartRecordAudio(int i2) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needStartRecordVideo(int i2, boolean z, int i3) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAttachButtonHidden() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAttachButtonShow() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAudioVideoInterfaceUpdated() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void onContextMenuClose() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onContextMenuClose(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void onContextMenuOpen() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onContextMenuOpen(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void onEditTextScroll() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onEditTextScroll(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onMessageEditEnd(boolean z) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onPreAudioVideoRecord() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onSendLongClick() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onStickersExpandedChange() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onStickersTab(boolean z) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onSwitchRecordMode(boolean z) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextChanged(CharSequence charSequence, boolean z) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextSelectionChanged(int i2, int i3) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextSpansChanged(CharSequence charSequence) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void onTrendingStickersShowed(boolean z) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onWindowSizeChanged(int i2) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void openScheduledMessages() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void prepareMessageSending() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void scrollToSendingMessage() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onMessageSend(CharSequence charSequence, boolean z, int i2) {
                if (PopupNotificationActivity.this.currentMessageObject == null) {
                    return;
                }
                if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                    PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, 0, true, 0);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needSendTyping() {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, 0, PopupNotificationActivity.this.classGuid);
                }
            }
        });
        FrameLayoutTouch frameLayoutTouch = new FrameLayoutTouch(this);
        this.messageContainer = frameLayoutTouch;
        this.popupContainer.addView(frameLayoutTouch, 0);
        ActionBar actionBar = new ActionBar(this);
        this.actionBar = actionBar;
        actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        TextView textView = new TextView(this);
        this.countText = textView;
        textView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        addItemWithWidth.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
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
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams3.width = AndroidUtilities.dp(42.0f);
        layoutParams3.height = AndroidUtilities.dp(42.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams3);
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
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams4.width = -2;
        layoutParams4.height = -2;
        layoutParams4.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams4.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams4.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams4);
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
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams5.width = -2;
        layoutParams5.height = -2;
        layoutParams5.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams5.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams5.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams5);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PopupNotificationActivity.4
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    PopupNotificationActivity.this.onFinish();
                    PopupNotificationActivity.this.finish();
                } else if (i2 == 1) {
                    PopupNotificationActivity.this.openCurrentMessage();
                } else if (i2 != 2) {
                } else {
                    PopupNotificationActivity.this.switchToNextMessage();
                }
            }
        });
        PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "screen");
        this.wakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.checkDisplaySize(this, configuration);
        fixLayout();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 3 || iArr[0] == 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("PermissionNoAudioWithHint", R.string.PermissionNoAudioWithHint));
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                PopupNotificationActivity.this.lambda$onRequestPermissionsResult$0(dialogInterface, i2);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResult$0(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
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
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
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

    /* JADX WARN: Removed duplicated region for block: B:84:0x0137  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEventMy(android.view.MotionEvent r14) {
        /*
            Method dump skipped, instructions count: 455
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.onTouchEventMy(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouchEventMy$1() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouchEventMy$2() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouchEventMy$3() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyViewsLayoutParams(int i) {
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((-dp) + i);
        }
        ViewGroup viewGroup2 = this.leftButtonsView;
        if (viewGroup2 != null) {
            viewGroup2.setTranslationX((-dp) + i);
        }
        ViewGroup viewGroup3 = this.centerView;
        if (viewGroup3 != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewGroup3.getLayoutParams();
            if (layoutParams2.width != dp) {
                layoutParams2.width = dp;
                this.centerView.setLayoutParams(layoutParams2);
            }
            this.centerView.setTranslationX(i);
        }
        ViewGroup viewGroup4 = this.centerButtonsView;
        if (viewGroup4 != null) {
            viewGroup4.setTranslationX(i);
        }
        ViewGroup viewGroup5 = this.rightView;
        if (viewGroup5 != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup5.getLayoutParams();
            if (layoutParams3.width != dp) {
                layoutParams3.width = dp;
                this.rightView.setLayoutParams(layoutParams3);
            }
            this.rightView.setTranslationX(dp + i);
        }
        ViewGroup viewGroup6 = this.rightButtonsView;
        if (viewGroup6 != null) {
            viewGroup6.setTranslationX(dp + i);
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int i, boolean z) {
        int i2;
        int i3 = i;
        LinearLayout linearLayout = null;
        if (this.popupMessages.size() != 1 || (i3 >= 0 && i3 < this.popupMessages.size())) {
            int i4 = 0;
            if (i3 == -1) {
                i3 = this.popupMessages.size() - 1;
            } else if (i3 == this.popupMessages.size()) {
                i3 = 0;
            }
            final MessageObject messageObject = this.popupMessages.get(i3);
            TLRPC$ReplyMarkup tLRPC$ReplyMarkup = messageObject.messageOwner.reply_markup;
            if (messageObject.getDialogId() != 777000 || tLRPC$ReplyMarkup == null) {
                i2 = 0;
            } else {
                ArrayList<TLRPC$TL_keyboardButtonRow> arrayList = tLRPC$ReplyMarkup.rows;
                int size = arrayList.size();
                i2 = 0;
                for (int i5 = 0; i5 < size; i5++) {
                    TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = arrayList.get(i5);
                    int size2 = tLRPC$TL_keyboardButtonRow.buttons.size();
                    for (int i6 = 0; i6 < size2; i6++) {
                        if (tLRPC$TL_keyboardButtonRow.buttons.get(i6) instanceof TLRPC$TL_keyboardButtonCallback) {
                            i2++;
                        }
                    }
                }
            }
            final int i7 = messageObject.currentAccount;
            if (i2 > 0) {
                ArrayList<TLRPC$TL_keyboardButtonRow> arrayList2 = tLRPC$ReplyMarkup.rows;
                int size3 = arrayList2.size();
                int i8 = 0;
                while (i8 < size3) {
                    TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow2 = arrayList2.get(i8);
                    int size4 = tLRPC$TL_keyboardButtonRow2.buttons.size();
                    int i9 = 0;
                    while (i9 < size4) {
                        TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow2.buttons.get(i9);
                        if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonCallback) {
                            if (linearLayout == null) {
                                linearLayout = new LinearLayout(this);
                                linearLayout.setOrientation(i4);
                                linearLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                                linearLayout.setWeightSum(100.0f);
                                linearLayout.setTag("b");
                                linearLayout.setOnTouchListener(PopupNotificationActivity$$ExternalSyntheticLambda5.INSTANCE);
                            }
                            TextView textView = new TextView(this);
                            textView.setTextSize(1, 16.0f);
                            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
                            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            textView.setText(tLRPC$KeyboardButton.text.toUpperCase());
                            textView.setTag(tLRPC$KeyboardButton);
                            textView.setGravity(17);
                            textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / i2));
                            textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda1
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view) {
                                    PopupNotificationActivity.lambda$getButtonsViewForMessage$5(i7, messageObject, view);
                                }
                            });
                        }
                        i9++;
                        i4 = 0;
                    }
                    i8++;
                    i4 = 0;
                }
            }
            if (linearLayout != null) {
                int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                layoutParams.addRule(12);
                if (z) {
                    int i10 = this.currentMessageNum;
                    if (i3 == i10) {
                        linearLayout.setTranslationX(0.0f);
                    } else if (i3 == i10 - 1) {
                        linearLayout.setTranslationX(-dp);
                    } else if (i3 == i10 + 1) {
                        linearLayout.setTranslationX(dp);
                    }
                }
                this.popupContainer.addView(linearLayout, layoutParams);
            }
            return linearLayout;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getButtonsViewForMessage$5(int i, MessageObject messageObject, View view) {
        TLRPC$KeyboardButton tLRPC$KeyboardButton = (TLRPC$KeyboardButton) view.getTag();
        if (tLRPC$KeyboardButton != null) {
            SendMessagesHelper.getInstance(i).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), tLRPC$KeyboardButton.data);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0170  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0184  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.view.ViewGroup getViewForMessage(int r29, boolean r30) {
        /*
            Method dump skipped, instructions count: 890
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getViewForMessage$6(View view) {
        openCurrentMessage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getViewForMessage$7(View view) {
        openCurrentMessage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getViewForMessage$8(View view) {
        openCurrentMessage();
    }

    private void reuseButtonsView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        this.popupContainer.removeView(viewGroup);
    }

    private void reuseView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        int intValue = ((Integer) viewGroup.getTag()).intValue();
        viewGroup.setVisibility(8);
        if (intValue == 1) {
            this.textViews.add(viewGroup);
        } else if (intValue == 2) {
            this.imageViews.add(viewGroup);
        } else if (intValue != 3) {
        } else {
            this.audioViews.add(viewGroup);
        }
    }

    private void prepareLayouts(int i) {
        MessageObject messageObject;
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (i == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            int i2 = this.currentMessageNum - 1;
            while (true) {
                int i3 = this.currentMessageNum;
                if (i2 >= i3 + 2) {
                    break;
                }
                if (i2 == i3 - 1) {
                    this.leftView = getViewForMessage(i2, true);
                    this.leftButtonsView = getButtonsViewForMessage(i2, true);
                } else if (i2 == i3) {
                    this.centerView = getViewForMessage(i2, true);
                    this.centerButtonsView = getButtonsViewForMessage(i2, true);
                } else if (i2 == i3 + 1) {
                    this.rightView = getViewForMessage(i2, true);
                    this.rightButtonsView = getButtonsViewForMessage(i2, true);
                }
                i2++;
            }
        } else if (i == 1) {
            reuseView(this.rightView);
            reuseButtonsView(this.rightButtonsView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
            this.rightButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.leftButtonsView;
            this.leftButtonsView = getButtonsViewForMessage(this.currentMessageNum - 1, true);
        } else if (i == 2) {
            reuseView(this.leftView);
            reuseButtonsView(this.leftButtonsView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
            this.leftButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.rightButtonsView;
            this.rightButtonsView = getButtonsViewForMessage(this.currentMessageNum + 1, true);
        } else if (i == 3) {
            ViewGroup viewGroup = this.rightView;
            if (viewGroup != null) {
                float translationX = viewGroup.getTranslationX();
                reuseView(this.rightView);
                ViewGroup viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = viewForMessage;
                if (viewForMessage != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewForMessage.getLayoutParams();
                    layoutParams.width = dp;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.setTranslationX(translationX);
                    this.rightView.invalidate();
                }
            }
            ViewGroup viewGroup2 = this.rightButtonsView;
            if (viewGroup2 != null) {
                float translationX2 = viewGroup2.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                LinearLayout buttonsViewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = buttonsViewForMessage;
                if (buttonsViewForMessage != null) {
                    buttonsViewForMessage.setTranslationX(translationX2);
                }
            }
        } else if (i == 4) {
            ViewGroup viewGroup3 = this.leftView;
            if (viewGroup3 != null) {
                float translationX3 = viewGroup3.getTranslationX();
                reuseView(this.leftView);
                ViewGroup viewForMessage2 = getViewForMessage(0, false);
                this.leftView = viewForMessage2;
                if (viewForMessage2 != null) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewForMessage2.getLayoutParams();
                    layoutParams2.width = dp;
                    this.leftView.setLayoutParams(layoutParams2);
                    this.leftView.setTranslationX(translationX3);
                    this.leftView.invalidate();
                }
            }
            ViewGroup viewGroup4 = this.leftButtonsView;
            if (viewGroup4 != null) {
                float translationX4 = viewGroup4.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                LinearLayout buttonsViewForMessage2 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = buttonsViewForMessage2;
                if (buttonsViewForMessage2 != null) {
                    buttonsViewForMessage2.setTranslationX(translationX4);
                }
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            int i5 = (this.currentMessageNum - 1) + i4;
            if (this.popupMessages.size() != 1 || (i5 >= 0 && i5 < this.popupMessages.size())) {
                if (i5 == -1) {
                    i5 = this.popupMessages.size() - 1;
                } else if (i5 == this.popupMessages.size()) {
                    i5 = 0;
                }
                messageObject = this.popupMessages.get(i5);
            } else {
                messageObject = null;
            }
            this.setMessageObjects[i4] = messageObject;
        }
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.6
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
                    return true;
                }
            });
        }
        ViewGroup viewGroup = this.messageContainer;
        if (viewGroup != null) {
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.7
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving) {
                        return true;
                    }
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                    marginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                    marginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                    marginLayoutParams.width = -1;
                    marginLayoutParams.height = -1;
                    PopupNotificationActivity.this.messageContainer.setLayoutParams(marginLayoutParams);
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
            int intExtra = intent != null ? intent.getIntExtra("currentAccount", UserConfig.selectedAccount) : UserConfig.selectedAccount;
            if (!UserConfig.isValidAccount(intExtra)) {
                return;
            }
            this.popupMessages.addAll(NotificationsController.getInstance(intExtra).popupReplyMessages);
        } else {
            for (int i = 0; i < 4; i++) {
                if (UserConfig.getInstance(i).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i).popupMessages);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void getNewMessage() {
        boolean z;
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.popupMessages.get(i);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i;
                    z = true;
                    break;
                }
            }
        }
        z = false;
        if (!z) {
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
        this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCurrentMessage() {
        if (this.currentMessageObject == null) {
            return;
        }
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

    private void updateInterfaceForCurrentMessage(int i) {
        if (this.actionBar == null) {
            return;
        }
        int i2 = this.lastResumedAccount;
        if (i2 != this.currentMessageObject.currentAccount) {
            if (i2 >= 0) {
                ConnectionsManager.getInstance(i2).setAppPaused(true, false);
            }
            int i3 = this.currentMessageObject.currentAccount;
            this.lastResumedAccount = i3;
            ConnectionsManager.getInstance(i3).setAppPaused(false, false);
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
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat != null) {
            this.nameTextView.setText(tLRPC$Chat.title);
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User != null) {
                this.onlineTextView.setText(UserObject.getUserName(tLRPC$User));
            } else {
                this.onlineTextView.setText((CharSequence) null);
            }
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.nameTextView.setCompoundDrawablePadding(0);
        } else {
            TLRPC$User tLRPC$User2 = this.currentUser;
            if (tLRPC$User2 != null) {
                this.nameTextView.setText(UserObject.getUserName(tLRPC$User2));
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
        }
        prepareLayouts(i);
        updateSubtitle();
        checkAndUpdateAvatar();
        applyViewsLayoutParams(0);
    }

    private void updateSubtitle() {
        MessageObject messageObject;
        TLRPC$User tLRPC$User;
        if (this.actionBar == null || (messageObject = this.currentMessageObject) == null || this.currentChat != null || (tLRPC$User = this.currentUser) == null) {
            return;
        }
        long j = tLRPC$User.id;
        if (j / 1000 != 777 && j / 1000 != 333 && ContactsController.getInstance(messageObject.currentAccount).contactsDict.get(Long.valueOf(this.currentUser.id)) == null && (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
            String str = this.currentUser.phone;
            if (str != null && str.length() != 0) {
                TextView textView = this.nameTextView;
                PhoneFormat phoneFormat = PhoneFormat.getInstance();
                textView.setText(phoneFormat.format("+" + this.currentUser.phone));
            } else {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            }
        } else {
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        }
        TLRPC$User tLRPC$User2 = this.currentUser;
        if (tLRPC$User2 != null && tLRPC$User2.id == 777000) {
            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications));
            return;
        }
        CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
        if (printingString == null || printingString.length() == 0) {
            this.lastPrintString = null;
            setTypingAnimation(false);
            TLRPC$User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
            }
            this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
            return;
        }
        this.lastPrintString = printingString;
        this.onlineTextView.setText(printingString);
        setTypingAnimation(true);
    }

    private void checkAndUpdateAvatar() {
        TLRPC$User user;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        if (this.currentChat != null) {
            TLRPC$Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(this.currentChat.id));
            if (chat == null) {
                return;
            }
            this.currentChat = chat;
            if (this.avatarImageView == null) {
                return;
            }
            this.avatarImageView.setForUserOrChat(chat, new AvatarDrawable(this.currentChat));
        } else if (this.currentUser == null || (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id))) == null) {
        } else {
            this.currentUser = user;
            if (this.avatarImageView == null) {
                return;
            }
            this.avatarImageView.setForUserOrChat(user, new AvatarDrawable(this.currentUser));
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar == null) {
            return;
        }
        int i = 0;
        if (z) {
            try {
                Integer printingStringType = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingStringType(this.currentMessageObject.getDialogId(), 0);
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[printingStringType.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                while (i < this.statusDrawables.length) {
                    if (i == printingStringType.intValue()) {
                        this.statusDrawables[i].start();
                    } else {
                        this.statusDrawables[i].stop();
                    }
                    i++;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        this.onlineTextView.setCompoundDrawablePadding(0);
        while (true) {
            StatusDrawable[] statusDrawableArr = this.statusDrawables;
            if (i >= statusDrawableArr.length) {
                return;
            }
            statusDrawableArr[i].stop();
            i++;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000L);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        int i = this.lastResumedAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextView textView;
        PopupAudioView popupAudioView;
        MessageObject messageObject;
        PopupAudioView popupAudioView2;
        MessageObject messageObject2;
        MessageObject messageObject3;
        if (i == NotificationCenter.appDidLogout) {
            if (i2 != this.lastResumedAccount) {
                return;
            }
            onFinish();
            finish();
            return;
        }
        int i3 = 0;
        if (i == NotificationCenter.pushMessagesUpdated) {
            if (this.isReply) {
                return;
            }
            this.popupMessages.clear();
            for (int i4 = 0; i4 < 4; i4++) {
                if (UserConfig.getInstance(i4).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i4).popupMessages);
                }
            }
            getNewMessage();
            if (this.popupMessages.isEmpty()) {
                return;
            }
            for (int i5 = 0; i5 < 3; i5++) {
                int i6 = (this.currentMessageNum - 1) + i5;
                if (this.popupMessages.size() != 1 || (i6 >= 0 && i6 < this.popupMessages.size())) {
                    if (i6 == -1) {
                        i6 = this.popupMessages.size() - 1;
                    } else if (i6 == this.popupMessages.size()) {
                        i6 = 0;
                    }
                    messageObject3 = this.popupMessages.get(i6);
                } else {
                    messageObject3 = null;
                }
                if (this.setMessageObjects[i5] != messageObject3) {
                    updateInterfaceForCurrentMessage(0);
                }
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject == null || i2 != this.lastResumedAccount) {
                return;
            }
            int intValue = ((Integer) objArr[0]).intValue();
            if ((MessagesController.UPDATE_MASK_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & intValue) != 0) {
                updateSubtitle();
            }
            if ((MessagesController.UPDATE_MASK_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & intValue) != 0) {
                checkAndUpdateAvatar();
            }
            if ((intValue & MessagesController.UPDATE_MASK_USER_PRINT) == 0) {
                return;
            }
            CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
            CharSequence charSequence = this.lastPrintString;
            if ((charSequence == null || printingString != null) && ((charSequence != null || printingString == null) && (charSequence == null || charSequence.equals(printingString)))) {
                return;
            }
            updateSubtitle();
        } else if (i == NotificationCenter.messagePlayingDidReset) {
            Integer num = (Integer) objArr[0];
            ViewGroup viewGroup = this.messageContainer;
            if (viewGroup == null) {
                return;
            }
            int childCount = viewGroup.getChildCount();
            while (i3 < childCount) {
                View childAt = this.messageContainer.getChildAt(i3);
                if (((Integer) childAt.getTag()).intValue() == 3 && (messageObject2 = (popupAudioView2 = (PopupAudioView) childAt.findViewWithTag(300)).getMessageObject()) != null && messageObject2.currentAccount == i2 && messageObject2.getId() == num.intValue()) {
                    popupAudioView2.updateButtonState();
                    return;
                }
                i3++;
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num2 = (Integer) objArr[0];
            ViewGroup viewGroup2 = this.messageContainer;
            if (viewGroup2 == null) {
                return;
            }
            int childCount2 = viewGroup2.getChildCount();
            while (i3 < childCount2) {
                View childAt2 = this.messageContainer.getChildAt(i3);
                if (((Integer) childAt2.getTag()).intValue() == 3 && (messageObject = (popupAudioView = (PopupAudioView) childAt2.findViewWithTag(300)).getMessageObject()) != null && messageObject.currentAccount == i2 && messageObject.getId() == num2.intValue()) {
                    popupAudioView.updateProgress();
                    return;
                }
                i3++;
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            ViewGroup viewGroup3 = this.messageContainer;
            if (viewGroup3 == null) {
                return;
            }
            int childCount3 = viewGroup3.getChildCount();
            while (i3 < childCount3) {
                View childAt3 = this.messageContainer.getChildAt(i3);
                if (((Integer) childAt3.getTag()).intValue() == 1 && (textView = (TextView) childAt3.findViewWithTag(301)) != null) {
                    textView.invalidate();
                }
                i3++;
            }
        } else if (i == NotificationCenter.contactsDidLoad && i2 == this.lastResumedAccount) {
            updateSubtitle();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        if (this.isReply) {
            this.popupMessages.clear();
        }
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        if (!this.wakeLock.isHeld()) {
            return;
        }
        this.wakeLock.release();
    }
}
