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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
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
    /* access modifiers changed from: private */
    public boolean startedMoving = false;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private ArrayList<ViewGroup> textViews = new ArrayList<>();
    private VelocityTracker velocityTracker = null;
    private PowerManager.WakeLock wakeLock = null;

    static /* synthetic */ boolean lambda$getButtonsViewForMessage$4(View view, MotionEvent motionEvent) {
        return true;
    }

    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        public FrameLayoutTouch(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public FrameLayoutTouch(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy((MotionEvent) null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this);
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable();
        this.statusDrawables[1] = new RecordStatusDrawable();
        this.statusDrawables[2] = new SendingFileDrawable();
        this.statusDrawables[3] = new PlayingGameDrawable();
        this.statusDrawables[4] = new RoundStatusDrawable();
        AnonymousClass1 r2 = new SizeNotifierFrameLayout(this, false) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                View.MeasureSpec.getMode(i);
                View.MeasureSpec.getMode(i2);
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    size2 -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt.getVisibility() != 8) {
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + size2), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x0065  */
            /* JADX WARNING: Removed duplicated region for block: B:27:0x007a  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x0092  */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x009b  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    r9 = this;
                    int r10 = r9.getChildCount()
                    int r0 = r9.getKeyboardHeight()
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    r2 = 0
                    if (r0 > r1) goto L_0x001c
                    org.telegram.ui.PopupNotificationActivity r0 = org.telegram.ui.PopupNotificationActivity.this
                    org.telegram.ui.Components.ChatActivityEnterView r0 = r0.chatActivityEnterView
                    int r0 = r0.getEmojiPadding()
                    goto L_0x001d
                L_0x001c:
                    r0 = 0
                L_0x001d:
                    if (r2 >= r10) goto L_0x00ea
                    android.view.View r1 = r9.getChildAt(r2)
                    int r3 = r1.getVisibility()
                    r4 = 8
                    if (r3 != r4) goto L_0x002d
                    goto L_0x00e6
                L_0x002d:
                    android.view.ViewGroup$LayoutParams r3 = r1.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r1.getMeasuredWidth()
                    int r5 = r1.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L_0x0042
                    r6 = 51
                L_0x0042:
                    r7 = r6 & 7
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L_0x0056
                    r8 = 5
                    if (r7 == r8) goto L_0x0051
                    int r7 = r3.leftMargin
                    goto L_0x0061
                L_0x0051:
                    int r7 = r13 - r4
                    int r8 = r3.rightMargin
                    goto L_0x0060
                L_0x0056:
                    int r7 = r13 - r11
                    int r7 = r7 - r4
                    int r7 = r7 / 2
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L_0x0060:
                    int r7 = r7 - r8
                L_0x0061:
                    r8 = 16
                    if (r6 == r8) goto L_0x007a
                    r8 = 48
                    if (r6 == r8) goto L_0x0077
                    r8 = 80
                    if (r6 == r8) goto L_0x0070
                    int r6 = r3.topMargin
                    goto L_0x0086
                L_0x0070:
                    int r6 = r14 - r0
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r8 = r3.bottomMargin
                    goto L_0x0085
                L_0x0077:
                    int r6 = r3.topMargin
                    goto L_0x0086
                L_0x007a:
                    int r6 = r14 - r0
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r8 = r3.bottomMargin
                L_0x0085:
                    int r6 = r6 - r8
                L_0x0086:
                    org.telegram.ui.PopupNotificationActivity r8 = org.telegram.ui.PopupNotificationActivity.this
                    org.telegram.ui.Components.ChatActivityEnterView r8 = r8.chatActivityEnterView
                    boolean r8 = r8.isPopupView(r1)
                    if (r8 == 0) goto L_0x009b
                    int r3 = r9.getMeasuredHeight()
                    if (r0 == 0) goto L_0x0099
                    int r3 = r3 - r0
                L_0x0099:
                    r6 = r3
                    goto L_0x00e1
                L_0x009b:
                    org.telegram.ui.PopupNotificationActivity r8 = org.telegram.ui.PopupNotificationActivity.this
                    org.telegram.ui.Components.ChatActivityEnterView r8 = r8.chatActivityEnterView
                    boolean r8 = r8.isRecordCircle(r1)
                    if (r8 == 0) goto L_0x00e1
                    org.telegram.ui.PopupNotificationActivity r6 = org.telegram.ui.PopupNotificationActivity.this
                    android.widget.RelativeLayout r6 = r6.popupContainer
                    int r6 = r6.getTop()
                    org.telegram.ui.PopupNotificationActivity r7 = org.telegram.ui.PopupNotificationActivity.this
                    android.widget.RelativeLayout r7 = r7.popupContainer
                    int r7 = r7.getMeasuredHeight()
                    int r6 = r6 + r7
                    int r7 = r1.getMeasuredHeight()
                    int r6 = r6 - r7
                    int r7 = r3.bottomMargin
                    int r6 = r6 - r7
                    org.telegram.ui.PopupNotificationActivity r7 = org.telegram.ui.PopupNotificationActivity.this
                    android.widget.RelativeLayout r7 = r7.popupContainer
                    int r7 = r7.getLeft()
                    org.telegram.ui.PopupNotificationActivity r8 = org.telegram.ui.PopupNotificationActivity.this
                    android.widget.RelativeLayout r8 = r8.popupContainer
                    int r8 = r8.getMeasuredWidth()
                    int r7 = r7 + r8
                    int r8 = r1.getMeasuredWidth()
                    int r7 = r7 - r8
                    int r3 = r3.rightMargin
                    int r7 = r7 - r3
                L_0x00e1:
                    int r4 = r4 + r7
                    int r5 = r5 + r6
                    r1.layout(r7, r6, r4, r5)
                L_0x00e6:
                    int r2 = r2 + 1
                    goto L_0x001d
                L_0x00ea:
                    r9.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }
        };
        setContentView(r2);
        r2.setBackgroundColor(-NUM);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        r2.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.popupContainer = new RelativeLayout(this) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                int measuredWidth = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int measuredHeight = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt.getTag() instanceof String) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM), View.MeasureSpec.makeMeasureSpec(measuredHeight - AndroidUtilities.dp(3.0f), NUM));
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                for (int i5 = 0; i5 < getChildCount(); i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt.getTag() instanceof String) {
                        childAt.layout(childAt.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), childAt.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        this.popupContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (chatActivityEnterView2 != null) {
            chatActivityEnterView2.onDestroy();
        }
        this.chatActivityEnterView = new ChatActivityEnterView(this, r2, (ChatActivity) null, false);
        this.chatActivityEnterView.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
            public void didPressedAttachButton() {
            }

            public /* synthetic */ boolean hasScheduledMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
            }

            public void needChangeVideoPreviewState(int i, float f) {
            }

            public void needShowMediaBanHint() {
            }

            public void needStartRecordAudio(int i) {
            }

            public void needStartRecordVideo(int i, boolean z, int i2) {
            }

            public void onAttachButtonHidden() {
            }

            public void onAttachButtonShow() {
            }

            public void onMessageEditEnd(boolean z) {
            }

            public void onPreAudioVideoRecord() {
            }

            public void onSendLongClick() {
            }

            public void onStickersExpandedChange() {
            }

            public void onStickersTab(boolean z) {
            }

            public void onSwitchRecordMode(boolean z) {
            }

            public void onTextChanged(CharSequence charSequence, boolean z) {
            }

            public void onTextSelectionChanged(int i, int i2) {
            }

            public void onTextSpansChanged(CharSequence charSequence) {
            }

            public void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence) {
            }

            public void onWindowSizeChanged(int i) {
            }

            public /* synthetic */ void openScheduledMessages() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
            }

            public /* synthetic */ void scrollToSendingMessage() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
            }

            public void onMessageSend(CharSequence charSequence, boolean z, int i) {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                        PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                    }
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, true, 0);
                    MessageObject unused = PopupNotificationActivity.this.currentMessageObject = null;
                    PopupNotificationActivity.this.getNewMessage();
                }
            }

            public void needSendTyping() {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
                }
            }
        });
        this.messageContainer = new FrameLayoutTouch(this);
        this.popupContainer.addView(this.messageContainer, 0);
        this.actionBar = new ActionBar(this);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        this.countText = new TextView(this);
        this.countText.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        addItemWithWidth.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        this.avatarContainer = new FrameLayout(this);
        this.avatarContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        this.avatarImageView = new BackupImageView(this);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams3.width = AndroidUtilities.dp(42.0f);
        layoutParams3.height = AndroidUtilities.dp(42.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams3);
        this.nameTextView = new TextView(this);
        this.nameTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
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
        this.onlineTextView = new TextView(this);
        this.onlineTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PopupNotificationActivity.this.onFinish();
                    PopupNotificationActivity.this.finish();
                } else if (i == 1) {
                    PopupNotificationActivity.this.openCurrentMessage();
                } else if (i == 2) {
                    PopupNotificationActivity.this.switchToNextMessage();
                }
            }
        });
        this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "screen");
        this.wakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.checkDisplaySize(this, configuration);
        fixLayout();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3 && iArr[0] != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PopupNotificationActivity.this.lambda$onRequestPermissionsResult$0$PopupNotificationActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResult$0$PopupNotificationActivity(DialogInterface dialogInterface, int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:61:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0137  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEventMy(android.view.MotionEvent r14) {
        /*
            r13 = this;
            boolean r0 = r13.checkTransitionAnimation()
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            if (r14 == 0) goto L_0x0018
            int r0 = r14.getAction()
            if (r0 != 0) goto L_0x0018
            float r14 = r14.getX()
            r13.moveStartX = r14
            goto L_0x01c5
        L_0x0018:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = 2
            r3 = 1
            if (r14 == 0) goto L_0x0076
            int r4 = r14.getAction()
            if (r4 != r2) goto L_0x0076
            float r2 = r14.getX()
            float r4 = r13.moveStartX
            float r5 = r2 - r4
            int r5 = (int) r5
            int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x0057
            boolean r0 = r13.startedMoving
            if (r0 != 0) goto L_0x0057
            int r0 = java.lang.Math.abs(r5)
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            if (r0 <= r4) goto L_0x0057
            r13.startedMoving = r3
            r13.moveStartX = r2
            org.telegram.messenger.AndroidUtilities.lockOrientation(r13)
            android.view.VelocityTracker r0 = r13.velocityTracker
            if (r0 != 0) goto L_0x0053
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r13.velocityTracker = r0
            goto L_0x0056
        L_0x0053:
            r0.clear()
        L_0x0056:
            r5 = 0
        L_0x0057:
            boolean r0 = r13.startedMoving
            if (r0 == 0) goto L_0x01c5
            android.view.ViewGroup r0 = r13.leftView
            if (r0 != 0) goto L_0x0062
            if (r5 <= 0) goto L_0x0062
            r5 = 0
        L_0x0062:
            android.view.ViewGroup r0 = r13.rightView
            if (r0 != 0) goto L_0x0069
            if (r5 >= 0) goto L_0x0069
            goto L_0x006a
        L_0x0069:
            r1 = r5
        L_0x006a:
            android.view.VelocityTracker r0 = r13.velocityTracker
            if (r0 == 0) goto L_0x0071
            r0.addMovement(r14)
        L_0x0071:
            r13.applyViewsLayoutParams(r1)
            goto L_0x01c5
        L_0x0076:
            r4 = 3
            if (r14 == 0) goto L_0x0085
            int r5 = r14.getAction()
            if (r5 == r3) goto L_0x0085
            int r5 = r14.getAction()
            if (r5 != r4) goto L_0x01c5
        L_0x0085:
            r5 = 0
            if (r14 == 0) goto L_0x01b5
            boolean r6 = r13.startedMoving
            if (r6 == 0) goto L_0x01b5
            float r14 = r14.getX()
            float r6 = r13.moveStartX
            float r14 = r14 - r6
            int r14 = (int) r14
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r6.x
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            android.view.VelocityTracker r7 = r13.velocityTracker
            if (r7 == 0) goto L_0x00c6
            r8 = 1000(0x3e8, float:1.401E-42)
            r7.computeCurrentVelocity(r8)
            android.view.VelocityTracker r7 = r13.velocityTracker
            float r7 = r7.getXVelocity()
            r8 = 1163575296(0x455aCLASSNAME, float:3500.0)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 < 0) goto L_0x00b7
            r7 = 1
            goto L_0x00c7
        L_0x00b7:
            android.view.VelocityTracker r7 = r13.velocityTracker
            float r7 = r7.getXVelocity()
            r8 = -983908352(0xffffffffCLASSNAMEaCLASSNAME, float:-3500.0)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 > 0) goto L_0x00c6
            r7 = 2
            goto L_0x00c7
        L_0x00c6:
            r7 = 0
        L_0x00c7:
            r8 = 0
            if (r7 == r3) goto L_0x00ce
            int r9 = r6 / 3
            if (r14 <= r9) goto L_0x00e6
        L_0x00ce:
            android.view.ViewGroup r9 = r13.leftView
            if (r9 == 0) goto L_0x00e6
            float r14 = (float) r6
            android.view.ViewGroup r2 = r13.centerView
            float r2 = r2.getTranslationX()
            float r14 = r14 - r2
            android.view.ViewGroup r2 = r13.leftView
            android.view.ViewGroup r4 = r13.leftButtonsView
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$EUnPo4xnwynM9tMiGtirwuisKAk r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$EUnPo4xnwynM9tMiGtirwuisKAk
            r7.<init>()
            r13.onAnimationEndRunnable = r7
            goto L_0x0133
        L_0x00e6:
            if (r7 == r2) goto L_0x00ec
            int r2 = -r6
            int r2 = r2 / r4
            if (r14 >= r2) goto L_0x0105
        L_0x00ec:
            android.view.ViewGroup r2 = r13.rightView
            if (r2 == 0) goto L_0x0105
            int r14 = -r6
            float r14 = (float) r14
            android.view.ViewGroup r2 = r13.centerView
            float r2 = r2.getTranslationX()
            float r14 = r14 - r2
            android.view.ViewGroup r2 = r13.rightView
            android.view.ViewGroup r4 = r13.rightButtonsView
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$w3pAWV1vrsUpHtwKKGENN1Nl00M r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$w3pAWV1vrsUpHtwKKGENN1Nl00M
            r7.<init>()
            r13.onAnimationEndRunnable = r7
            goto L_0x0133
        L_0x0105:
            android.view.ViewGroup r2 = r13.centerView
            float r2 = r2.getTranslationX()
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x0130
            android.view.ViewGroup r2 = r13.centerView
            float r2 = r2.getTranslationX()
            float r2 = -r2
            if (r14 <= 0) goto L_0x011b
            android.view.ViewGroup r4 = r13.leftView
            goto L_0x011d
        L_0x011b:
            android.view.ViewGroup r4 = r13.rightView
        L_0x011d:
            if (r14 <= 0) goto L_0x0122
            android.view.ViewGroup r14 = r13.leftButtonsView
            goto L_0x0124
        L_0x0122:
            android.view.ViewGroup r14 = r13.rightButtonsView
        L_0x0124:
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$EAJL169S6xhlVfXmCKr6JNKWWPk r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$EAJL169S6xhlVfXmCKr6JNKWWPk
            r7.<init>()
            r13.onAnimationEndRunnable = r7
            r12 = r4
            r4 = r14
            r14 = r2
            r2 = r12
            goto L_0x0133
        L_0x0130:
            r2 = r5
            r4 = r2
            r14 = 0
        L_0x0133:
            int r7 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x01b8
            float r6 = (float) r6
            float r6 = r14 / r6
            float r6 = java.lang.Math.abs(r6)
            r7 = 1128792064(0x43480000, float:200.0)
            float r6 = r6 * r7
            int r6 = (int) r6
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            android.view.ViewGroup r8 = r13.centerView
            float[] r9 = new float[r3]
            float r10 = r8.getTranslationX()
            float r10 = r10 + r14
            r9[r1] = r10
            java.lang.String r10 = "translationX"
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r9)
            r7.add(r8)
            android.view.ViewGroup r8 = r13.centerButtonsView
            if (r8 == 0) goto L_0x0171
            float[] r9 = new float[r3]
            float r11 = r8.getTranslationX()
            float r11 = r11 + r14
            r9[r1] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r9)
            r7.add(r8)
        L_0x0171:
            if (r2 == 0) goto L_0x0183
            float[] r8 = new float[r3]
            float r9 = r2.getTranslationX()
            float r9 = r9 + r14
            r8[r1] = r9
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r10, r8)
            r7.add(r2)
        L_0x0183:
            if (r4 == 0) goto L_0x0195
            float[] r2 = new float[r3]
            float r8 = r4.getTranslationX()
            float r8 = r8 + r14
            r2[r1] = r8
            android.animation.ObjectAnimator r14 = android.animation.ObjectAnimator.ofFloat(r4, r10, r2)
            r7.add(r14)
        L_0x0195:
            android.animation.AnimatorSet r14 = new android.animation.AnimatorSet
            r14.<init>()
            r14.playTogether(r7)
            long r6 = (long) r6
            r14.setDuration(r6)
            org.telegram.ui.PopupNotificationActivity$5 r2 = new org.telegram.ui.PopupNotificationActivity$5
            r2.<init>()
            r14.addListener(r2)
            r14.start()
            r13.animationInProgress = r3
            long r2 = java.lang.System.currentTimeMillis()
            r13.animationStartTime = r2
            goto L_0x01b8
        L_0x01b5:
            r13.applyViewsLayoutParams(r1)
        L_0x01b8:
            android.view.VelocityTracker r14 = r13.velocityTracker
            if (r14 == 0) goto L_0x01c1
            r14.recycle()
            r13.velocityTracker = r5
        L_0x01c1:
            r13.startedMoving = r1
            r13.moveStartX = r0
        L_0x01c5:
            boolean r14 = r13.startedMoving
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.onTouchEventMy(android.view.MotionEvent):boolean");
    }

    public /* synthetic */ void lambda$onTouchEventMy$1$PopupNotificationActivity() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    public /* synthetic */ void lambda$onTouchEventMy$2$PopupNotificationActivity() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    public /* synthetic */ void lambda$onTouchEventMy$3$PopupNotificationActivity() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    /* access modifiers changed from: private */
    public void applyViewsLayoutParams(int i) {
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((float) ((-dp) + i));
        }
        ViewGroup viewGroup2 = this.leftButtonsView;
        if (viewGroup2 != null) {
            viewGroup2.setTranslationX((float) ((-dp) + i));
        }
        ViewGroup viewGroup3 = this.centerView;
        if (viewGroup3 != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewGroup3.getLayoutParams();
            if (layoutParams2.width != dp) {
                layoutParams2.width = dp;
                this.centerView.setLayoutParams(layoutParams2);
            }
            this.centerView.setTranslationX((float) i);
        }
        ViewGroup viewGroup4 = this.centerButtonsView;
        if (viewGroup4 != null) {
            viewGroup4.setTranslationX((float) i);
        }
        ViewGroup viewGroup5 = this.rightView;
        if (viewGroup5 != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup5.getLayoutParams();
            if (layoutParams3.width != dp) {
                layoutParams3.width = dp;
                this.rightView.setLayoutParams(layoutParams3);
            }
            this.rightView.setTranslationX((float) (dp + i));
        }
        ViewGroup viewGroup6 = this.rightButtonsView;
        if (viewGroup6 != null) {
            viewGroup6.setTranslationX((float) (dp + i));
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int i, boolean z) {
        int i2;
        int i3 = i;
        LinearLayout linearLayout = null;
        if (this.popupMessages.size() == 1 && (i3 < 0 || i3 >= this.popupMessages.size())) {
            return null;
        }
        int i4 = 0;
        if (i3 == -1) {
            i3 = this.popupMessages.size() - 1;
        } else if (i3 == this.popupMessages.size()) {
            i3 = 0;
        }
        MessageObject messageObject = this.popupMessages.get(i3);
        TLRPC.ReplyMarkup replyMarkup = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() != 777000 || replyMarkup == null) {
            i2 = 0;
        } else {
            ArrayList<TLRPC.TL_keyboardButtonRow> arrayList = replyMarkup.rows;
            int size = arrayList.size();
            int i5 = 0;
            i2 = 0;
            while (i5 < size) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow = arrayList.get(i5);
                int size2 = tL_keyboardButtonRow.buttons.size();
                int i6 = i2;
                for (int i7 = 0; i7 < size2; i7++) {
                    if (tL_keyboardButtonRow.buttons.get(i7) instanceof TLRPC.TL_keyboardButtonCallback) {
                        i6++;
                    }
                }
                i5++;
                i2 = i6;
            }
        }
        int i8 = messageObject.currentAccount;
        if (i2 > 0) {
            ArrayList<TLRPC.TL_keyboardButtonRow> arrayList2 = replyMarkup.rows;
            int size3 = arrayList2.size();
            LinearLayout linearLayout2 = null;
            int i9 = 0;
            while (i9 < size3) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow2 = arrayList2.get(i9);
                int size4 = tL_keyboardButtonRow2.buttons.size();
                LinearLayout linearLayout3 = linearLayout2;
                int i10 = 0;
                while (i10 < size4) {
                    TLRPC.KeyboardButton keyboardButton = tL_keyboardButtonRow2.buttons.get(i10);
                    if (keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) {
                        if (linearLayout3 == null) {
                            linearLayout3 = new LinearLayout(this);
                            linearLayout3.setOrientation(i4);
                            linearLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            linearLayout3.setWeightSum(100.0f);
                            linearLayout3.setTag("b");
                            linearLayout3.setOnTouchListener($$Lambda$PopupNotificationActivity$XvlaP2ODWCCStorSQi9nplxzY4s.INSTANCE);
                        }
                        TextView textView = new TextView(this);
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(keyboardButton.text.toUpperCase());
                        textView.setTag(keyboardButton);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        linearLayout3.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) i2)));
                        textView.setOnClickListener(new View.OnClickListener(i8, messageObject) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ MessageObject f$1;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                PopupNotificationActivity.lambda$getButtonsViewForMessage$5(this.f$0, this.f$1, view);
                            }
                        });
                    }
                    i10++;
                    i4 = 0;
                }
                i9++;
                linearLayout2 = linearLayout3;
                i4 = 0;
            }
            linearLayout = linearLayout2;
        }
        if (linearLayout != null) {
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (z) {
                int i11 = this.currentMessageNum;
                if (i3 == i11) {
                    linearLayout.setTranslationX(0.0f);
                } else if (i3 == i11 - 1) {
                    linearLayout.setTranslationX((float) (-dp));
                } else if (i3 == i11 + 1) {
                    linearLayout.setTranslationX((float) dp);
                }
            }
            this.popupContainer.addView(linearLayout, layoutParams);
        }
        return linearLayout;
    }

    static /* synthetic */ void lambda$getButtonsViewForMessage$5(int i, MessageObject messageObject, View view) {
        TLRPC.KeyboardButton keyboardButton = (TLRPC.KeyboardButton) view.getTag();
        if (keyboardButton != null) {
            SendMessagesHelper.getInstance(i).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), keyboardButton.data);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0181  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.ViewGroup getViewForMessage(int r28, boolean r29) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
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
            r5 = 0
            if (r1 != r2) goto L_0x0025
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.popupMessages
            int r1 = r1.size()
            int r1 = r1 - r4
            goto L_0x002e
        L_0x0025:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.popupMessages
            int r6 = r6.size()
            if (r1 != r6) goto L_0x002e
            r1 = 0
        L_0x002e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.popupMessages
            java.lang.Object r6 = r6.get(r1)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            int r7 = r6.type
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 4
            java.lang.String r11 = "windowBackgroundWhiteBlackText"
            r12 = 17
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = 1092616192(0x41200000, float:10.0)
            if (r7 == r4) goto L_0x0048
            if (r7 != r9) goto L_0x01ea
        L_0x0048:
            boolean r7 = r6.isSecretMedia()
            if (r7 != 0) goto L_0x01ea
            java.util.ArrayList<android.view.ViewGroup> r7 = r0.imageViews
            int r7 = r7.size()
            r16 = 311(0x137, float:4.36E-43)
            if (r7 <= 0) goto L_0x0066
            java.util.ArrayList<android.view.ViewGroup> r7 = r0.imageViews
            java.lang.Object r7 = r7.get(r5)
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            java.util.ArrayList<android.view.ViewGroup> r8 = r0.imageViews
            r8.remove(r5)
            goto L_0x00d7
        L_0x0066:
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r0)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r3.setPadding(r9, r14, r10, r15)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r9)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13)
            r7.addView(r3, r9)
            org.telegram.ui.Components.BackupImageView r9 = new org.telegram.ui.Components.BackupImageView
            r9.<init>(r0)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r16)
            r9.setTag(r10)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13)
            r3.addView(r9, r10)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r0)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r9.setTextColor(r10)
            r9.setTextSize(r4, r8)
            r9.setGravity(r12)
            r8 = 312(0x138, float:4.37E-43)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r9.setTag(r8)
            r8 = -2
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r8, (int) r12)
            r3.addView(r9, r8)
            r3 = 2
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            r7.setTag(r8)
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$yXN7dQz6jZF2SRmRmEwBYh62Ap0 r3 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$yXN7dQz6jZF2SRmRmEwBYh62Ap0
            r3.<init>()
            r7.setOnClickListener(r3)
        L_0x00d7:
            r3 = r7
            r7 = 312(0x138, float:4.37E-43)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            android.view.View r7 = r3.findViewWithTag(r7)
            r14 = r7
            android.widget.TextView r14 = (android.widget.TextView) r14
            java.lang.Integer r7 = java.lang.Integer.valueOf(r16)
            android.view.View r7 = r3.findViewWithTag(r7)
            r15 = r7
            org.telegram.ui.Components.BackupImageView r15 = (org.telegram.ui.Components.BackupImageView) r15
            r15.setAspectFit(r4)
            int r7 = r6.type
            r13 = 8
            r8 = 100
            if (r7 != r4) goto L_0x0189
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r6.photoThumbs
            int r9 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r6.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r8)
            if (r7 == 0) goto L_0x0168
            int r9 = r6.type
            if (r9 != r4) goto L_0x011f
            org.telegram.tgnet.TLRPC$Message r9 = r6.messageOwner
            java.io.File r9 = org.telegram.messenger.FileLoader.getPathToMessage(r9)
            boolean r9 = r9.exists()
            if (r9 != 0) goto L_0x011f
            r9 = 0
            goto L_0x0120
        L_0x011f:
            r9 = 1
        L_0x0120:
            boolean r10 = r6.needDrawBluredPreview()
            if (r10 != 0) goto L_0x0168
            if (r9 != 0) goto L_0x0149
            int r9 = r6.currentAccount
            org.telegram.messenger.DownloadController r9 = org.telegram.messenger.DownloadController.getInstance(r9)
            boolean r9 = r9.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r9 == 0) goto L_0x0135
            goto L_0x0149
        L_0x0135:
            if (r8 == 0) goto L_0x0168
            org.telegram.tgnet.TLObject r7 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForObject(r8, r7)
            r10 = 0
            r11 = 0
            java.lang.String r9 = "100_100_b"
            r7 = r15
            r12 = r6
            r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12)
            r4 = 8
            goto L_0x0166
        L_0x0149:
            org.telegram.tgnet.TLObject r9 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForObject(r7, r9)
            org.telegram.tgnet.TLObject r10 = r6.photoThumbsObject
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForObject(r8, r10)
            int r12 = r7.size
            java.lang.String r11 = "100_100"
            java.lang.String r16 = "100_100_b"
            r7 = r15
            r8 = r9
            r9 = r11
            r11 = r16
            r4 = 8
            r13 = r6
            r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (int) r12, (java.lang.Object) r13)
        L_0x0166:
            r7 = 1
            goto L_0x016b
        L_0x0168:
            r4 = 8
            r7 = 0
        L_0x016b:
            if (r7 != 0) goto L_0x0181
            r15.setVisibility(r4)
            r14.setVisibility(r5)
            int r4 = org.telegram.messenger.SharedConfig.fontSize
            float r4 = (float) r4
            r7 = 2
            r14.setTextSize(r7, r4)
            java.lang.CharSequence r4 = r6.messageText
            r14.setText(r4)
            goto L_0x032f
        L_0x0181:
            r15.setVisibility(r5)
            r14.setVisibility(r4)
            goto L_0x032f
        L_0x0189:
            r4 = 8
            r9 = 4
            if (r7 != r9) goto L_0x032f
            r14.setVisibility(r4)
            java.lang.CharSequence r4 = r6.messageText
            r14.setText(r4)
            r15.setVisibility(r5)
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$GeoPoint r4 = r4.geo
            double r9 = r4.lat
            double r11 = r4._long
            int r7 = r6.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.mapProvider
            r13 = 2
            if (r7 != r13) goto L_0x01ce
            r7 = 15
            float r9 = org.telegram.messenger.AndroidUtilities.density
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r9 = java.lang.Math.min(r13, r9)
            org.telegram.messenger.WebFile r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r4, r8, r8, r7, r9)
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForWebFile(r4)
            r9 = 0
            r10 = 0
            r11 = 0
            r7 = r15
            r12 = r6
            r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12)
            goto L_0x032f
        L_0x01ce:
            int r4 = r6.currentAccount
            r22 = 100
            r23 = 100
            r24 = 1
            r25 = 15
            r26 = -1
            r17 = r4
            r18 = r9
            r20 = r11
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r17, r18, r20, r22, r23, r24, r25, r26)
            r6 = 0
            r15.setImage(r4, r6, r6)
            goto L_0x032f
        L_0x01ea:
            int r3 = r6.type
            r4 = 2
            if (r3 != r4) goto L_0x0288
            java.util.ArrayList<android.view.ViewGroup> r3 = r0.audioViews
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0211
            java.util.ArrayList<android.view.ViewGroup> r3 = r0.audioViews
            java.lang.Object r3 = r3.get(r5)
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            java.util.ArrayList<android.view.ViewGroup> r4 = r0.audioViews
            r4.remove(r5)
            r4 = 300(0x12c, float:4.2E-43)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            android.view.View r4 = r3.findViewWithTag(r4)
            org.telegram.ui.Components.PopupAudioView r4 = (org.telegram.ui.Components.PopupAudioView) r4
            goto L_0x0274
        L_0x0211:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r0)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r4.setPadding(r7, r8, r9, r10)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r4.setBackgroundDrawable(r7)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13)
            r3.addView(r4, r7)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r0)
            r8 = -1
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r10 = 17
            r11 = 1101004800(0x41a00000, float:20.0)
            r12 = 0
            r13 = 1101004800(0x41a00000, float:20.0)
            r14 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r4.addView(r7, r8)
            org.telegram.ui.Components.PopupAudioView r4 = new org.telegram.ui.Components.PopupAudioView
            r4.<init>(r0)
            r8 = 300(0x12c, float:4.2E-43)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r4.setTag(r8)
            r7.addView(r4)
            r7 = 3
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r3.setTag(r7)
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$VFWwjWrjLI64daw5erAQKADNXUs r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$VFWwjWrjLI64daw5erAQKADNXUs
            r7.<init>()
            r3.setOnClickListener(r7)
        L_0x0274:
            r4.setMessageObject(r6)
            int r7 = r6.currentAccount
            org.telegram.messenger.DownloadController r7 = org.telegram.messenger.DownloadController.getInstance(r7)
            boolean r6 = r7.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r6 == 0) goto L_0x032f
            r4.downloadAudioIfNeed()
            goto L_0x032f
        L_0x0288:
            java.util.ArrayList<android.view.ViewGroup> r3 = r0.textViews
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x029e
            java.util.ArrayList<android.view.ViewGroup> r3 = r0.textViews
            java.lang.Object r3 = r3.get(r5)
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            java.util.ArrayList<android.view.ViewGroup> r4 = r0.textViews
            r4.remove(r5)
            goto L_0x0317
        L_0x029e:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r0)
            android.widget.ScrollView r4 = new android.widget.ScrollView
            r4.<init>(r0)
            r7 = 1
            r4.setFillViewport(r7)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13)
            r3.addView(r4, r9)
            android.widget.LinearLayout r9 = new android.widget.LinearLayout
            r9.<init>(r0)
            r9.setOrientation(r5)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r9.setBackgroundDrawable(r10)
            r10 = -2
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r10, r7)
            r4.addView(r9, r13)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r9.setPadding(r4, r7, r10, r13)
            org.telegram.ui.-$$Lambda$PopupNotificationActivity$1_iHFPQDV_CYBmOOqbZFgFmuyU8 r4 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$1_iHFPQDV_CYBmOOqbZFgFmuyU8
            r4.<init>()
            r9.setOnClickListener(r4)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r0)
            r7 = 1
            r4.setTextSize(r7, r8)
            r8 = 301(0x12d, float:4.22E-43)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r4.setTag(r8)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r8)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setLinkTextColor(r8)
            r4.setGravity(r12)
            r8 = -2
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r8, (int) r12)
            r9.addView(r4, r8)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)
            r3.setTag(r4)
        L_0x0317:
            r4 = 301(0x12d, float:4.22E-43)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            android.view.View r4 = r3.findViewWithTag(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            int r7 = org.telegram.messenger.SharedConfig.fontSize
            float r7 = (float) r7
            r8 = 2
            r4.setTextSize(r8, r7)
            java.lang.CharSequence r6 = r6.messageText
            r4.setText(r6)
        L_0x032f:
            android.view.ViewParent r4 = r3.getParent()
            if (r4 != 0) goto L_0x033a
            android.view.ViewGroup r4 = r0.messageContainer
            r4.addView(r3)
        L_0x033a:
            r3.setVisibility(r5)
            if (r29 == 0) goto L_0x0379
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            android.view.ViewGroup$LayoutParams r5 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r6 = 51
            r5.gravity = r6
            r5.height = r2
            r5.width = r4
            int r2 = r0.currentMessageNum
            if (r1 != r2) goto L_0x0361
            r1 = 0
            r3.setTranslationX(r1)
            goto L_0x0373
        L_0x0361:
            int r6 = r2 + -1
            if (r1 != r6) goto L_0x036b
            int r1 = -r4
            float r1 = (float) r1
            r3.setTranslationX(r1)
            goto L_0x0373
        L_0x036b:
            r6 = 1
            int r2 = r2 + r6
            if (r1 != r2) goto L_0x0373
            float r1 = (float) r4
            r3.setTranslationX(r1)
        L_0x0373:
            r3.setLayoutParams(r5)
            r3.invalidate()
        L_0x0379:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
    }

    public /* synthetic */ void lambda$getViewForMessage$6$PopupNotificationActivity(View view) {
        openCurrentMessage();
    }

    public /* synthetic */ void lambda$getViewForMessage$7$PopupNotificationActivity(View view) {
        openCurrentMessage();
    }

    public /* synthetic */ void lambda$getViewForMessage$8$PopupNotificationActivity(View view) {
        openCurrentMessage();
    }

    private void reuseButtonsView(ViewGroup viewGroup) {
        if (viewGroup != null) {
            this.popupContainer.removeView(viewGroup);
        }
    }

    private void reuseView(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int intValue = ((Integer) viewGroup.getTag()).intValue();
            viewGroup.setVisibility(8);
            if (intValue == 1) {
                this.textViews.add(viewGroup);
            } else if (intValue == 2) {
                this.imageViews.add(viewGroup);
            } else if (intValue == 3) {
                this.audioViews.add(viewGroup);
            }
        }
    }

    private void prepareLayouts(int i) {
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
                if (i2 < i3 + 2) {
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
                } else {
                    return;
                }
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
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.rightView.getLayoutParams();
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
                    this.rightButtonsView.setTranslationX(translationX2);
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
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.leftView.getLayoutParams();
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
                    this.leftButtonsView.setTranslationX(translationX4);
                }
            }
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
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
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
            this.popupMessages.addAll(NotificationsController.getInstance(intent != null ? intent.getIntExtra("currentAccount", UserConfig.selectedAccount) : UserConfig.selectedAccount).popupReplyMessages);
        } else {
            for (int i = 0; i < 3; i++) {
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

    /* access modifiers changed from: private */
    public void getNewMessage() {
        boolean z;
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                MessageObject messageObject = this.popupMessages.get(i);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i;
                    z = true;
                    break;
                }
                i++;
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
        this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
    }

    /* access modifiers changed from: private */
    public void openCurrentMessage() {
        if (this.currentMessageObject != null) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            long dialogId = this.currentMessageObject.getDialogId();
            int i = (int) dialogId;
            if (i == 0) {
                intent.putExtra("encId", (int) (dialogId >> 32));
            } else if (i < 0) {
                intent.putExtra("chatId", -i);
            } else {
                intent.putExtra("userId", i);
            }
            intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
            intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
            intent.setFlags(32768);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void updateInterfaceForCurrentMessage(int i) {
        if (this.actionBar != null) {
            int i2 = this.lastResumedAccount;
            if (i2 != this.currentMessageObject.currentAccount) {
                if (i2 >= 0) {
                    ConnectionsManager.getInstance(i2).setAppPaused(true, false);
                }
                this.lastResumedAccount = this.currentMessageObject.currentAccount;
                ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(false, false);
            }
            this.currentChat = null;
            this.currentUser = null;
            long dialogId = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialogId, this.currentMessageObject.currentAccount);
            int i3 = (int) dialogId;
            if (i3 == 0) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialogId >> 32))).user_id));
            } else if (i3 > 0) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(i3));
            } else {
                this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(-i3));
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat == null || this.currentUser == null) {
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    this.nameTextView.setText(UserObject.getUserName(user));
                    if (i3 == 0) {
                        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(NUM, 0, 0, 0);
                        this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    } else {
                        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        this.nameTextView.setCompoundDrawablePadding(0);
                    }
                }
            } else {
                this.nameTextView.setText(chat.title);
                this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            }
            prepareLayouts(i);
            updateSubtitle();
            checkAndUpdateAvatar();
            applyViewsLayoutParams(0);
        }
    }

    private void updateSubtitle() {
        MessageObject messageObject;
        TLRPC.User user;
        if (this.actionBar != null && (messageObject = this.currentMessageObject) != null && this.currentChat == null && (user = this.currentUser) != null) {
            int i = user.id;
            if (i / 1000 == 777 || i / 1000 == 333 || ContactsController.getInstance(messageObject.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) != null || (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                String str = this.currentUser.phone;
                if (str == null || str.length() == 0) {
                    this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                } else {
                    TextView textView = this.nameTextView;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    textView.setText(instance.format("+" + this.currentUser.phone));
                }
            }
            TLRPC.User user2 = this.currentUser;
            if (user2 == null || user2.id != 777000) {
                CharSequence charSequence = MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                if (charSequence == null || charSequence.length() == 0) {
                    this.lastPrintString = null;
                    setTypingAnimation(false);
                    TLRPC.User user3 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                    if (user3 != null) {
                        this.currentUser = user3;
                    }
                    this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
                    return;
                }
                this.lastPrintString = charSequence;
                this.onlineTextView.setText(charSequence);
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
                TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    if (this.avatarImageView != null) {
                        this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) new AvatarDrawable(this.currentChat), (Object) chat);
                    }
                }
            } else if (this.currentUser != null && (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id))) != null) {
                this.currentUser = user;
                if (this.avatarImageView != null) {
                    this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) new AvatarDrawable(this.currentUser), (Object) user);
                }
            }
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar != null) {
            int i = 0;
            if (z) {
                try {
                    Integer num = MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[num.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    while (i < this.statusDrawables.length) {
                        if (i == num.intValue()) {
                            this.statusDrawables[i].start();
                        } else {
                            this.statusDrawables[i].stop();
                        }
                        i++;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(0);
                while (true) {
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (i < statusDrawableArr.length) {
                        statusDrawableArr[i].stop();
                        i++;
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

    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00cf, code lost:
        r0 = (org.telegram.ui.Components.PopupAudioView) r0.findViewWithTag(300);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0119, code lost:
        r0 = (org.telegram.ui.Components.PopupAudioView) r0.findViewWithTag(300);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r7, int r8, java.lang.Object... r9) {
        /*
            r6 = this;
            int r0 = org.telegram.messenger.NotificationCenter.appDidLogout
            if (r7 != r0) goto L_0x0010
            int r7 = r6.lastResumedAccount
            if (r8 != r7) goto L_0x017e
            r6.onFinish()
            r6.finish()
            goto L_0x017e
        L_0x0010:
            int r0 = org.telegram.messenger.NotificationCenter.pushMessagesUpdated
            r1 = 3
            r2 = 0
            if (r7 != r0) goto L_0x003e
            boolean r7 = r6.isReply
            if (r7 != 0) goto L_0x017e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r6.popupMessages
            r7.clear()
        L_0x001f:
            if (r2 >= r1) goto L_0x0039
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r7 = r7.isClientActivated()
            if (r7 == 0) goto L_0x0036
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r6.popupMessages
            org.telegram.messenger.NotificationsController r8 = org.telegram.messenger.NotificationsController.getInstance(r2)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r8.popupMessages
            r7.addAll(r8)
        L_0x0036:
            int r2 = r2 + 1
            goto L_0x001f
        L_0x0039:
            r6.getNewMessage()
            goto L_0x017e
        L_0x003e:
            int r0 = org.telegram.messenger.NotificationCenter.updateInterfaces
            if (r7 != r0) goto L_0x00a9
            org.telegram.messenger.MessageObject r7 = r6.currentMessageObject
            if (r7 == 0) goto L_0x00a8
            int r7 = r6.lastResumedAccount
            if (r8 == r7) goto L_0x004b
            goto L_0x00a8
        L_0x004b:
            r7 = r9[r2]
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            r8 = r7 & 1
            if (r8 != 0) goto L_0x0063
            r8 = r7 & 4
            if (r8 != 0) goto L_0x0063
            r8 = r7 & 16
            if (r8 != 0) goto L_0x0063
            r8 = r7 & 32
            if (r8 == 0) goto L_0x0066
        L_0x0063:
            r6.updateSubtitle()
        L_0x0066:
            r8 = r7 & 2
            if (r8 != 0) goto L_0x006e
            r8 = r7 & 8
            if (r8 == 0) goto L_0x0071
        L_0x006e:
            r6.checkAndUpdateAvatar()
        L_0x0071:
            r7 = r7 & 64
            if (r7 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r7 = r6.currentMessageObject
            int r7 = r7.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            android.util.LongSparseArray<java.lang.CharSequence> r7 = r7.printingStrings
            org.telegram.messenger.MessageObject r8 = r6.currentMessageObject
            long r8 = r8.getDialogId()
            java.lang.Object r7 = r7.get(r8)
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            java.lang.CharSequence r8 = r6.lastPrintString
            if (r8 == 0) goto L_0x0091
            if (r7 == 0) goto L_0x00a3
        L_0x0091:
            java.lang.CharSequence r8 = r6.lastPrintString
            if (r8 != 0) goto L_0x0097
            if (r7 != 0) goto L_0x00a3
        L_0x0097:
            java.lang.CharSequence r8 = r6.lastPrintString
            if (r8 == 0) goto L_0x017e
            if (r7 == 0) goto L_0x017e
            boolean r7 = r8.equals(r7)
            if (r7 != 0) goto L_0x017e
        L_0x00a3:
            r6.updateSubtitle()
            goto L_0x017e
        L_0x00a8:
            return
        L_0x00a9:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r3 = 300(0x12c, float:4.2E-43)
            if (r7 != r0) goto L_0x00f5
            r7 = r9[r2]
            java.lang.Integer r7 = (java.lang.Integer) r7
            android.view.ViewGroup r9 = r6.messageContainer
            if (r9 == 0) goto L_0x017e
            int r9 = r9.getChildCount()
        L_0x00bb:
            if (r2 >= r9) goto L_0x017e
            android.view.ViewGroup r0 = r6.messageContainer
            android.view.View r0 = r0.getChildAt(r2)
            java.lang.Object r4 = r0.getTag()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            if (r4 != r1) goto L_0x00f2
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            android.view.View r0 = r0.findViewWithTag(r4)
            org.telegram.ui.Components.PopupAudioView r0 = (org.telegram.ui.Components.PopupAudioView) r0
            org.telegram.messenger.MessageObject r4 = r0.getMessageObject()
            if (r4 == 0) goto L_0x00f2
            int r5 = r4.currentAccount
            if (r5 != r8) goto L_0x00f2
            int r4 = r4.getId()
            int r5 = r7.intValue()
            if (r4 != r5) goto L_0x00f2
            r0.updateButtonState()
            goto L_0x017e
        L_0x00f2:
            int r2 = r2 + 1
            goto L_0x00bb
        L_0x00f5:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r7 != r0) goto L_0x013e
            r7 = r9[r2]
            java.lang.Integer r7 = (java.lang.Integer) r7
            android.view.ViewGroup r9 = r6.messageContainer
            if (r9 == 0) goto L_0x017e
            int r9 = r9.getChildCount()
        L_0x0105:
            if (r2 >= r9) goto L_0x017e
            android.view.ViewGroup r0 = r6.messageContainer
            android.view.View r0 = r0.getChildAt(r2)
            java.lang.Object r4 = r0.getTag()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            if (r4 != r1) goto L_0x013b
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            android.view.View r0 = r0.findViewWithTag(r4)
            org.telegram.ui.Components.PopupAudioView r0 = (org.telegram.ui.Components.PopupAudioView) r0
            org.telegram.messenger.MessageObject r4 = r0.getMessageObject()
            if (r4 == 0) goto L_0x013b
            int r5 = r4.currentAccount
            if (r5 != r8) goto L_0x013b
            int r4 = r4.getId()
            int r5 = r7.intValue()
            if (r4 != r5) goto L_0x013b
            r0.updateProgress()
            goto L_0x017e
        L_0x013b:
            int r2 = r2 + 1
            goto L_0x0105
        L_0x013e:
            int r9 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r7 != r9) goto L_0x0173
            android.view.ViewGroup r7 = r6.messageContainer
            if (r7 == 0) goto L_0x017e
            int r7 = r7.getChildCount()
        L_0x014a:
            if (r2 >= r7) goto L_0x017e
            android.view.ViewGroup r8 = r6.messageContainer
            android.view.View r8 = r8.getChildAt(r2)
            java.lang.Object r9 = r8.getTag()
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            r0 = 1
            if (r9 != r0) goto L_0x0170
            r9 = 301(0x12d, float:4.22E-43)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            android.view.View r8 = r8.findViewWithTag(r9)
            android.widget.TextView r8 = (android.widget.TextView) r8
            if (r8 == 0) goto L_0x0170
            r8.invalidate()
        L_0x0170:
            int r2 = r2 + 1
            goto L_0x014a
        L_0x0173:
            int r9 = org.telegram.messenger.NotificationCenter.contactsDidLoad
            if (r7 != r9) goto L_0x017e
            int r7 = r6.lastResumedAccount
            if (r8 != r7) goto L_0x017e
            r6.updateSubtitle()
        L_0x017e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
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
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoad);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
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
