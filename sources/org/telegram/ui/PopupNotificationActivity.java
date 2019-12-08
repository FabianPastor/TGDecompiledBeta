package org.telegram.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate.-CC;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity extends Activity implements NotificationCenterDelegate {
    private static final int id_chat_compose_panel = 1000;
    private ActionBar actionBar;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private ArrayList<ViewGroup> audioViews = new ArrayList();
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private Chat currentChat;
    private int currentMessageNum = 0;
    private MessageObject currentMessageObject = null;
    private User currentUser;
    private boolean finished = false;
    private ArrayList<ViewGroup> imageViews = new ArrayList();
    private boolean isReply;
    private CharSequence lastPrintString;
    private int lastResumedAccount = -1;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private float moveStartX = -1.0f;
    private TextView nameTextView;
    private Runnable onAnimationEndRunnable = null;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ArrayList<MessageObject> popupMessages = new ArrayList();
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private boolean startedMoving = false;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private ArrayList<ViewGroup> textViews = new ArrayList();
    private VelocityTracker velocityTracker = null;
    private WakeLock wakeLock = null;

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
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    /* Access modifiers changed, original: protected */
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
        AnonymousClass1 anonymousClass1 = new SizeNotifierFrameLayout(this) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                MeasureSpec.getMode(i);
                MeasureSpec.getMode(i2);
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    size2 -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt.getVisibility() != 8) {
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + size2), NUM));
                        }
                    }
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x009b  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x0092  */
            /* JADX WARNING: Removed duplicated region for block: B:27:0x007a  */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x0065  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x0092  */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x009b  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.getChildCount();
                r0 = r9.getKeyboardHeight();
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r2 = 0;
                if (r0 > r1) goto L_0x001c;
            L_0x0011:
                r0 = org.telegram.ui.PopupNotificationActivity.this;
                r0 = r0.chatActivityEnterView;
                r0 = r0.getEmojiPadding();
                goto L_0x001d;
            L_0x001c:
                r0 = 0;
            L_0x001d:
                if (r2 >= r10) goto L_0x00ea;
            L_0x001f:
                r1 = r9.getChildAt(r2);
                r3 = r1.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x002d;
            L_0x002b:
                goto L_0x00e6;
            L_0x002d:
                r3 = r1.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r1.getMeasuredWidth();
                r5 = r1.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x0042;
            L_0x0040:
                r6 = 51;
            L_0x0042:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x0056;
            L_0x004b:
                r8 = 5;
                if (r7 == r8) goto L_0x0051;
            L_0x004e:
                r7 = r3.leftMargin;
                goto L_0x0061;
            L_0x0051:
                r7 = r13 - r4;
                r8 = r3.rightMargin;
                goto L_0x0060;
            L_0x0056:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x0060:
                r7 = r7 - r8;
            L_0x0061:
                r8 = 16;
                if (r6 == r8) goto L_0x007a;
            L_0x0065:
                r8 = 48;
                if (r6 == r8) goto L_0x0077;
            L_0x0069:
                r8 = 80;
                if (r6 == r8) goto L_0x0070;
            L_0x006d:
                r6 = r3.topMargin;
                goto L_0x0086;
            L_0x0070:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r8 = r3.bottomMargin;
                goto L_0x0085;
            L_0x0077:
                r6 = r3.topMargin;
                goto L_0x0086;
            L_0x007a:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r8 = r3.bottomMargin;
            L_0x0085:
                r6 = r6 - r8;
            L_0x0086:
                r8 = org.telegram.ui.PopupNotificationActivity.this;
                r8 = r8.chatActivityEnterView;
                r8 = r8.isPopupView(r1);
                if (r8 == 0) goto L_0x009b;
            L_0x0092:
                r3 = r9.getMeasuredHeight();
                if (r0 == 0) goto L_0x0099;
            L_0x0098:
                r3 = r3 - r0;
            L_0x0099:
                r6 = r3;
                goto L_0x00e1;
            L_0x009b:
                r8 = org.telegram.ui.PopupNotificationActivity.this;
                r8 = r8.chatActivityEnterView;
                r8 = r8.isRecordCircle(r1);
                if (r8 == 0) goto L_0x00e1;
            L_0x00a7:
                r6 = org.telegram.ui.PopupNotificationActivity.this;
                r6 = r6.popupContainer;
                r6 = r6.getTop();
                r7 = org.telegram.ui.PopupNotificationActivity.this;
                r7 = r7.popupContainer;
                r7 = r7.getMeasuredHeight();
                r6 = r6 + r7;
                r7 = r1.getMeasuredHeight();
                r6 = r6 - r7;
                r7 = r3.bottomMargin;
                r6 = r6 - r7;
                r7 = org.telegram.ui.PopupNotificationActivity.this;
                r7 = r7.popupContainer;
                r7 = r7.getLeft();
                r8 = org.telegram.ui.PopupNotificationActivity.this;
                r8 = r8.popupContainer;
                r8 = r8.getMeasuredWidth();
                r7 = r7 + r8;
                r8 = r1.getMeasuredWidth();
                r7 = r7 - r8;
                r3 = r3.rightMargin;
                r7 = r7 - r3;
            L_0x00e1:
                r4 = r4 + r7;
                r5 = r5 + r6;
                r1.layout(r7, r6, r4, r5);
            L_0x00e6:
                r2 = r2 + 1;
                goto L_0x001d;
            L_0x00ea:
                r9.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity$AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }
        };
        setContentView(anonymousClass1);
        anonymousClass1.setBackgroundColor(-NUM);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        anonymousClass1.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.popupContainer = new RelativeLayout(this) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                i = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                i2 = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt.getTag() instanceof String) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - AndroidUtilities.dp(3.0f), NUM));
                    }
                }
            }

            /* Access modifiers changed, original: protected */
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
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        this.chatActivityEnterView = new ChatActivityEnterView(this, anonymousClass1, null, false);
        this.chatActivityEnterView.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterViewDelegate() {
            public void didPressedAttachButton() {
            }

            public /* synthetic */ boolean hasScheduledMessages() {
                return -CC.$default$hasScheduledMessages(this);
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
                -CC.$default$openScheduledMessages(this);
            }

            public /* synthetic */ void scrollToSendingMessage() {
                -CC.$default$scrollToSendingMessage(this);
            }

            public void onMessageSend(CharSequence charSequence, boolean z, int i) {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                        PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                    }
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, true, 0);
                    PopupNotificationActivity.this.currentMessageObject = null;
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
        LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        this.countText = new TextView(this);
        String str = "actionBarDefaultSubtitle";
        this.countText.setTextColor(Theme.getColor(str));
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
        layoutParams2 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(42.0f);
        layoutParams2.height = AndroidUtilities.dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams2);
        this.nameTextView = new TextView(this);
        this.nameTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.avatarContainer.addView(this.nameTextView);
        layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams2.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams2);
        this.onlineTextView = new TextView(this);
        this.onlineTextView.setTextColor(Theme.getColor(str));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams2.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams2);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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

    /* Access modifiers changed, original: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3 && iArr[0] != 0) {
            Builder builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$PopupNotificationActivity$4j1X9I2molTl8UnVVYY3k-eiVzk(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResult$0$PopupNotificationActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
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
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
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

    /* JADX WARNING: Removed duplicated region for block: B:81:0x0137  */
    public boolean onTouchEventMy(android.view.MotionEvent r14) {
        /*
        r13 = this;
        r0 = r13.checkTransitionAnimation();
        r1 = 0;
        if (r0 == 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        if (r14 == 0) goto L_0x0018;
    L_0x000a:
        r0 = r14.getAction();
        if (r0 != 0) goto L_0x0018;
    L_0x0010:
        r14 = r14.getX();
        r13.moveStartX = r14;
        goto L_0x01c4;
    L_0x0018:
        r0 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r2 = 2;
        r3 = 1;
        if (r14 == 0) goto L_0x0076;
    L_0x001e:
        r4 = r14.getAction();
        if (r4 != r2) goto L_0x0076;
    L_0x0024:
        r2 = r14.getX();
        r4 = r13.moveStartX;
        r5 = r2 - r4;
        r5 = (int) r5;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 == 0) goto L_0x0057;
    L_0x0031:
        r0 = r13.startedMoving;
        if (r0 != 0) goto L_0x0057;
    L_0x0035:
        r0 = java.lang.Math.abs(r5);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        if (r0 <= r4) goto L_0x0057;
    L_0x0041:
        r13.startedMoving = r3;
        r13.moveStartX = r2;
        org.telegram.messenger.AndroidUtilities.lockOrientation(r13);
        r0 = r13.velocityTracker;
        if (r0 != 0) goto L_0x0053;
    L_0x004c:
        r0 = android.view.VelocityTracker.obtain();
        r13.velocityTracker = r0;
        goto L_0x0056;
    L_0x0053:
        r0.clear();
    L_0x0056:
        r5 = 0;
    L_0x0057:
        r0 = r13.startedMoving;
        if (r0 == 0) goto L_0x01c4;
    L_0x005b:
        r0 = r13.leftView;
        if (r0 != 0) goto L_0x0062;
    L_0x005f:
        if (r5 <= 0) goto L_0x0062;
    L_0x0061:
        r5 = 0;
    L_0x0062:
        r0 = r13.rightView;
        if (r0 != 0) goto L_0x0069;
    L_0x0066:
        if (r5 >= 0) goto L_0x0069;
    L_0x0068:
        goto L_0x006a;
    L_0x0069:
        r1 = r5;
    L_0x006a:
        r0 = r13.velocityTracker;
        if (r0 == 0) goto L_0x0071;
    L_0x006e:
        r0.addMovement(r14);
    L_0x0071:
        r13.applyViewsLayoutParams(r1);
        goto L_0x01c4;
    L_0x0076:
        r4 = 3;
        if (r14 == 0) goto L_0x0085;
    L_0x0079:
        r5 = r14.getAction();
        if (r5 == r3) goto L_0x0085;
    L_0x007f:
        r5 = r14.getAction();
        if (r5 != r4) goto L_0x01c4;
    L_0x0085:
        r5 = 0;
        if (r14 == 0) goto L_0x01b4;
    L_0x0088:
        r6 = r13.startedMoving;
        if (r6 == 0) goto L_0x01b4;
    L_0x008c:
        r14 = r14.getX();
        r6 = r13.moveStartX;
        r14 = r14 - r6;
        r14 = (int) r14;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.x;
        r7 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r7 = r13.velocityTracker;
        if (r7 == 0) goto L_0x00c6;
    L_0x00a3:
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7.computeCurrentVelocity(r8);
        r7 = r13.velocityTracker;
        r7 = r7.getXVelocity();
        r8 = NUM; // 0x455aCLASSNAME float:3500.0 double:5.7488258E-315;
        r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r7 < 0) goto L_0x00b7;
    L_0x00b5:
        r7 = 1;
        goto L_0x00c7;
    L_0x00b7:
        r7 = r13.velocityTracker;
        r7 = r7.getXVelocity();
        r8 = -NUM; // 0xffffffffCLASSNAMEaCLASSNAME float:-3500.0 double:NaN;
        r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r7 > 0) goto L_0x00c6;
    L_0x00c4:
        r7 = 2;
        goto L_0x00c7;
    L_0x00c6:
        r7 = 0;
    L_0x00c7:
        r8 = 0;
        if (r7 == r3) goto L_0x00ce;
    L_0x00ca:
        r9 = r6 / 3;
        if (r14 <= r9) goto L_0x00e6;
    L_0x00ce:
        r9 = r13.leftView;
        if (r9 == 0) goto L_0x00e6;
    L_0x00d2:
        r14 = (float) r6;
        r2 = r13.centerView;
        r2 = r2.getTranslationX();
        r14 = r14 - r2;
        r2 = r13.leftView;
        r4 = r13.leftButtonsView;
        r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$EUnPo4xnwynM9tMiGtirwuisKAk;
        r7.<init>(r13);
        r13.onAnimationEndRunnable = r7;
        goto L_0x0133;
    L_0x00e6:
        if (r7 == r2) goto L_0x00ec;
    L_0x00e8:
        r2 = -r6;
        r2 = r2 / r4;
        if (r14 >= r2) goto L_0x0105;
    L_0x00ec:
        r2 = r13.rightView;
        if (r2 == 0) goto L_0x0105;
    L_0x00f0:
        r14 = -r6;
        r14 = (float) r14;
        r2 = r13.centerView;
        r2 = r2.getTranslationX();
        r14 = r14 - r2;
        r2 = r13.rightView;
        r4 = r13.rightButtonsView;
        r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$w3pAWV1vrsUpHtwKKGENN1Nl00M;
        r7.<init>(r13);
        r13.onAnimationEndRunnable = r7;
        goto L_0x0133;
    L_0x0105:
        r2 = r13.centerView;
        r2 = r2.getTranslationX();
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 == 0) goto L_0x0130;
    L_0x010f:
        r2 = r13.centerView;
        r2 = r2.getTranslationX();
        r2 = -r2;
        if (r14 <= 0) goto L_0x011b;
    L_0x0118:
        r4 = r13.leftView;
        goto L_0x011d;
    L_0x011b:
        r4 = r13.rightView;
    L_0x011d:
        if (r14 <= 0) goto L_0x0122;
    L_0x011f:
        r14 = r13.leftButtonsView;
        goto L_0x0124;
    L_0x0122:
        r14 = r13.rightButtonsView;
    L_0x0124:
        r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$EAJL169S6xhlVfXmCKr6JNKWWPk;
        r7.<init>(r13);
        r13.onAnimationEndRunnable = r7;
        r12 = r4;
        r4 = r14;
        r14 = r2;
        r2 = r12;
        goto L_0x0133;
    L_0x0130:
        r2 = r5;
        r4 = r2;
        r14 = 0;
    L_0x0133:
        r7 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1));
        if (r7 == 0) goto L_0x01b7;
    L_0x0137:
        r6 = (float) r6;
        r6 = r14 / r6;
        r6 = java.lang.Math.abs(r6);
        r7 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = r6 * r7;
        r6 = (int) r6;
        r7 = new java.util.ArrayList;
        r7.<init>();
        r8 = r13.centerView;
        r9 = new float[r3];
        r10 = r8.getTranslationX();
        r10 = r10 + r14;
        r9[r1] = r10;
        r10 = "translationX";
        r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r9);
        r7.add(r8);
        r8 = r13.centerButtonsView;
        if (r8 == 0) goto L_0x0170;
    L_0x0160:
        r9 = new float[r3];
        r11 = r8.getTranslationX();
        r11 = r11 + r14;
        r9[r1] = r11;
        r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r9);
        r7.add(r8);
    L_0x0170:
        if (r2 == 0) goto L_0x0182;
    L_0x0172:
        r8 = new float[r3];
        r9 = r2.getTranslationX();
        r9 = r9 + r14;
        r8[r1] = r9;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r10, r8);
        r7.add(r2);
    L_0x0182:
        if (r4 == 0) goto L_0x0194;
    L_0x0184:
        r2 = new float[r3];
        r8 = r4.getTranslationX();
        r8 = r8 + r14;
        r2[r1] = r8;
        r14 = android.animation.ObjectAnimator.ofFloat(r4, r10, r2);
        r7.add(r14);
    L_0x0194:
        r14 = new android.animation.AnimatorSet;
        r14.<init>();
        r14.playTogether(r7);
        r6 = (long) r6;
        r14.setDuration(r6);
        r2 = new org.telegram.ui.PopupNotificationActivity$5;
        r2.<init>();
        r14.addListener(r2);
        r14.start();
        r13.animationInProgress = r3;
        r2 = java.lang.System.currentTimeMillis();
        r13.animationStartTime = r2;
        goto L_0x01b7;
    L_0x01b4:
        r13.applyViewsLayoutParams(r1);
    L_0x01b7:
        r14 = r13.velocityTracker;
        if (r14 == 0) goto L_0x01c0;
    L_0x01bb:
        r14.recycle();
        r13.velocityTracker = r5;
    L_0x01c0:
        r13.startedMoving = r1;
        r13.moveStartX = r0;
    L_0x01c4:
        r14 = r13.startedMoving;
        return r14;
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

    private void applyViewsLayoutParams(int i) {
        FrameLayout.LayoutParams layoutParams;
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((float) ((-dp) + i));
        }
        viewGroup = this.leftButtonsView;
        if (viewGroup != null) {
            viewGroup.setTranslationX((float) ((-dp) + i));
        }
        viewGroup = this.centerView;
        if (viewGroup != null) {
            layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.centerView.setLayoutParams(layoutParams);
            }
            this.centerView.setTranslationX((float) i);
        }
        viewGroup = this.centerButtonsView;
        if (viewGroup != null) {
            viewGroup.setTranslationX((float) i);
        }
        viewGroup = this.rightView;
        if (viewGroup != null) {
            layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.rightView.setLayoutParams(layoutParams);
            }
            this.rightView.setTranslationX((float) (dp + i));
        }
        viewGroup = this.rightButtonsView;
        if (viewGroup != null) {
            viewGroup.setTranslationX((float) (dp + i));
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int i, boolean z) {
        int i2 = i;
        LinearLayout linearLayout = null;
        if (this.popupMessages.size() == 1 && (i2 < 0 || i2 >= this.popupMessages.size())) {
            return null;
        }
        int i3;
        int size;
        int i4;
        TL_keyboardButtonRow tL_keyboardButtonRow;
        int size2;
        int i5 = 0;
        if (i2 == -1) {
            i2 = this.popupMessages.size() - 1;
        } else if (i2 == this.popupMessages.size()) {
            i2 = 0;
        }
        MessageObject messageObject = (MessageObject) this.popupMessages.get(i2);
        ReplyMarkup replyMarkup = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() != 777000 || replyMarkup == null) {
            i3 = 0;
        } else {
            ArrayList arrayList = replyMarkup.rows;
            size = arrayList.size();
            i4 = 0;
            i3 = 0;
            while (i4 < size) {
                tL_keyboardButtonRow = (TL_keyboardButtonRow) arrayList.get(i4);
                size2 = tL_keyboardButtonRow.buttons.size();
                int i6 = i3;
                for (i3 = 0; i3 < size2; i3++) {
                    if (((KeyboardButton) tL_keyboardButtonRow.buttons.get(i3)) instanceof TL_keyboardButtonCallback) {
                        i6++;
                    }
                }
                i4++;
                i3 = i6;
            }
        }
        int i7 = messageObject.currentAccount;
        if (i3 > 0) {
            ArrayList arrayList2 = replyMarkup.rows;
            size = arrayList2.size();
            LinearLayout linearLayout2 = null;
            int i8 = 0;
            while (i8 < size) {
                tL_keyboardButtonRow = (TL_keyboardButtonRow) arrayList2.get(i8);
                size2 = tL_keyboardButtonRow.buttons.size();
                LinearLayout linearLayout3 = linearLayout2;
                i4 = 0;
                while (i4 < size2) {
                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i4);
                    if (keyboardButton instanceof TL_keyboardButtonCallback) {
                        if (linearLayout3 == null) {
                            linearLayout3 = new LinearLayout(this);
                            linearLayout3.setOrientation(i5);
                            linearLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            linearLayout3.setWeightSum(100.0f);
                            linearLayout3.setTag("b");
                            linearLayout3.setOnTouchListener(-$$Lambda$PopupNotificationActivity$XvlaP2ODWCCStorSQi9nplxzY4s.INSTANCE);
                        }
                        TextView textView = new TextView(this);
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(keyboardButton.text.toUpperCase());
                        textView.setTag(keyboardButton);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        linearLayout3.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) i3)));
                        textView.setOnClickListener(new -$$Lambda$PopupNotificationActivity$ox3mIPlvmBDmNDp_7DLxqyRSnLI(i7, messageObject));
                    }
                    i4++;
                    i5 = 0;
                }
                i8++;
                linearLayout2 = linearLayout3;
                i5 = 0;
            }
            linearLayout = linearLayout2;
        }
        if (linearLayout != null) {
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (z) {
                int i9 = this.currentMessageNum;
                if (i2 == i9) {
                    linearLayout.setTranslationX(0.0f);
                } else if (i2 == i9 - 1) {
                    linearLayout.setTranslationX((float) (-dp));
                } else if (i2 == i9 + 1) {
                    linearLayout.setTranslationX((float) dp);
                }
            }
            this.popupContainer.addView(linearLayout, layoutParams);
        }
        return linearLayout;
    }

    static /* synthetic */ void lambda$getButtonsViewForMessage$5(int i, MessageObject messageObject, View view) {
        KeyboardButton keyboardButton = (KeyboardButton) view.getTag();
        if (keyboardButton != null) {
            SendMessagesHelper.getInstance(i).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), keyboardButton.data);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x016c  */
    private android.view.ViewGroup getViewForMessage(int r28, boolean r29) {
        /*
        r27 = this;
        r0 = r27;
        r1 = r28;
        r2 = r0.popupMessages;
        r2 = r2.size();
        r3 = 0;
        r4 = 1;
        if (r2 != r4) goto L_0x0019;
    L_0x000e:
        if (r1 < 0) goto L_0x0018;
    L_0x0010:
        r2 = r0.popupMessages;
        r2 = r2.size();
        if (r1 < r2) goto L_0x0019;
    L_0x0018:
        return r3;
    L_0x0019:
        r2 = -1;
        r5 = 0;
        if (r1 != r2) goto L_0x0025;
    L_0x001d:
        r1 = r0.popupMessages;
        r1 = r1.size();
        r1 = r1 - r4;
        goto L_0x002e;
    L_0x0025:
        r6 = r0.popupMessages;
        r6 = r6.size();
        if (r1 != r6) goto L_0x002e;
    L_0x002d:
        r1 = 0;
    L_0x002e:
        r6 = r0.popupMessages;
        r6 = r6.get(r1);
        r6 = (org.telegram.messenger.MessageObject) r6;
        r7 = r6.type;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = 4;
        r11 = "windowBackgroundWhiteBlackText";
        r12 = 17;
        r13 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r15 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r7 == r4) goto L_0x0047;
    L_0x0045:
        if (r7 != r9) goto L_0x01e9;
    L_0x0047:
        r7 = r6.isSecretMedia();
        if (r7 != 0) goto L_0x01e9;
    L_0x004d:
        r7 = r0.imageViews;
        r7 = r7.size();
        r16 = 311; // 0x137 float:4.36E-43 double:1.537E-321;
        if (r7 <= 0) goto L_0x0065;
    L_0x0057:
        r7 = r0.imageViews;
        r7 = r7.get(r5);
        r7 = (android.view.ViewGroup) r7;
        r8 = r0.imageViews;
        r8.remove(r5);
        goto L_0x00d6;
    L_0x0065:
        r7 = new android.widget.FrameLayout;
        r7.<init>(r0);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r0);
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r14 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r3.setPadding(r9, r14, r10, r15);
        r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r3.setBackgroundDrawable(r9);
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13);
        r7.addView(r3, r9);
        r9 = new org.telegram.ui.Components.BackupImageView;
        r9.<init>(r0);
        r10 = java.lang.Integer.valueOf(r16);
        r9.setTag(r10);
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13);
        r3.addView(r9, r10);
        r9 = new android.widget.TextView;
        r9.<init>(r0);
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r9.setTextColor(r10);
        r9.setTextSize(r4, r8);
        r9.setGravity(r12);
        r8 = 312; // 0x138 float:4.37E-43 double:1.54E-321;
        r8 = java.lang.Integer.valueOf(r8);
        r9.setTag(r8);
        r8 = -2;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r8, r12);
        r3.addView(r9, r8);
        r3 = 2;
        r8 = java.lang.Integer.valueOf(r3);
        r7.setTag(r8);
        r3 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$yXN7dQz6jZF2SRmRmEwBYh62Ap0;
        r3.<init>(r0);
        r7.setOnClickListener(r3);
    L_0x00d6:
        r3 = r7;
        r7 = 312; // 0x138 float:4.37E-43 double:1.54E-321;
        r7 = java.lang.Integer.valueOf(r7);
        r7 = r3.findViewWithTag(r7);
        r14 = r7;
        r14 = (android.widget.TextView) r14;
        r7 = java.lang.Integer.valueOf(r16);
        r7 = r3.findViewWithTag(r7);
        r15 = r7;
        r15 = (org.telegram.ui.Components.BackupImageView) r15;
        r15.setAspectFit(r4);
        r7 = r6.type;
        r13 = 8;
        r8 = 100;
        if (r7 != r4) goto L_0x0188;
    L_0x00fa:
        r7 = r6.photoThumbs;
        r9 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r9);
        r9 = r6.photoThumbs;
        r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r8);
        if (r7 == 0) goto L_0x0167;
    L_0x010c:
        r9 = r6.type;
        if (r9 != r4) goto L_0x011e;
    L_0x0110:
        r9 = r6.messageOwner;
        r9 = org.telegram.messenger.FileLoader.getPathToMessage(r9);
        r9 = r9.exists();
        if (r9 != 0) goto L_0x011e;
    L_0x011c:
        r9 = 0;
        goto L_0x011f;
    L_0x011e:
        r9 = 1;
    L_0x011f:
        r10 = r6.needDrawBluredPreview();
        if (r10 != 0) goto L_0x0167;
    L_0x0125:
        if (r9 != 0) goto L_0x0148;
    L_0x0127:
        r9 = r6.currentAccount;
        r9 = org.telegram.messenger.DownloadController.getInstance(r9);
        r9 = r9.canDownloadMedia(r6);
        if (r9 == 0) goto L_0x0134;
    L_0x0133:
        goto L_0x0148;
    L_0x0134:
        if (r8 == 0) goto L_0x0167;
    L_0x0136:
        r7 = r6.photoThumbsObject;
        r8 = org.telegram.messenger.ImageLocation.getForObject(r8, r7);
        r10 = 0;
        r11 = 0;
        r9 = "100_100_b";
        r7 = r15;
        r12 = r6;
        r7.setImage(r8, r9, r10, r11, r12);
        r4 = 8;
        goto L_0x0165;
    L_0x0148:
        r9 = r6.photoThumbsObject;
        r9 = org.telegram.messenger.ImageLocation.getForObject(r7, r9);
        r10 = r6.photoThumbsObject;
        r10 = org.telegram.messenger.ImageLocation.getForObject(r8, r10);
        r12 = r7.size;
        r11 = "100_100";
        r16 = "100_100_b";
        r7 = r15;
        r8 = r9;
        r9 = r11;
        r11 = r16;
        r4 = 8;
        r13 = r6;
        r7.setImage(r8, r9, r10, r11, r12, r13);
    L_0x0165:
        r7 = 1;
        goto L_0x016a;
    L_0x0167:
        r4 = 8;
        r7 = 0;
    L_0x016a:
        if (r7 != 0) goto L_0x0180;
    L_0x016c:
        r15.setVisibility(r4);
        r14.setVisibility(r5);
        r4 = org.telegram.messenger.SharedConfig.fontSize;
        r4 = (float) r4;
        r7 = 2;
        r14.setTextSize(r7, r4);
        r4 = r6.messageText;
        r14.setText(r4);
        goto L_0x032e;
    L_0x0180:
        r15.setVisibility(r5);
        r14.setVisibility(r4);
        goto L_0x032e;
    L_0x0188:
        r4 = 8;
        r9 = 4;
        if (r7 != r9) goto L_0x032e;
    L_0x018d:
        r14.setVisibility(r4);
        r4 = r6.messageText;
        r14.setText(r4);
        r15.setVisibility(r5);
        r4 = r6.messageOwner;
        r4 = r4.media;
        r4 = r4.geo;
        r9 = r4.lat;
        r11 = r4._long;
        r7 = r6.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r7 = r7.mapProvider;
        r13 = 2;
        if (r7 != r13) goto L_0x01cd;
    L_0x01ad:
        r7 = 15;
        r9 = org.telegram.messenger.AndroidUtilities.density;
        r9 = (double) r9;
        r9 = java.lang.Math.ceil(r9);
        r9 = (int) r9;
        r9 = java.lang.Math.min(r13, r9);
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r4, r8, r8, r7, r9);
        r8 = org.telegram.messenger.ImageLocation.getForWebFile(r4);
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r7 = r15;
        r12 = r6;
        r7.setImage(r8, r9, r10, r11, r12);
        goto L_0x032e;
    L_0x01cd:
        r4 = r6.currentAccount;
        r22 = 100;
        r23 = 100;
        r24 = 1;
        r25 = 15;
        r26 = -1;
        r17 = r4;
        r18 = r9;
        r20 = r11;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r17, r18, r20, r22, r23, r24, r25, r26);
        r6 = 0;
        r15.setImage(r4, r6, r6);
        goto L_0x032e;
    L_0x01e9:
        r3 = r6.type;
        r4 = 2;
        if (r3 != r4) goto L_0x0287;
    L_0x01ee:
        r3 = r0.audioViews;
        r3 = r3.size();
        if (r3 <= 0) goto L_0x0210;
    L_0x01f6:
        r3 = r0.audioViews;
        r3 = r3.get(r5);
        r3 = (android.view.ViewGroup) r3;
        r4 = r0.audioViews;
        r4.remove(r5);
        r4 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r3.findViewWithTag(r4);
        r4 = (org.telegram.ui.Components.PopupAudioView) r4;
        goto L_0x0273;
    L_0x0210:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r0);
        r4 = new android.widget.FrameLayout;
        r4.<init>(r0);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r8 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r9 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r4.setPadding(r7, r8, r9, r10);
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r7);
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13);
        r3.addView(r4, r7);
        r7 = new android.widget.FrameLayout;
        r7.<init>(r0);
        r8 = -1;
        r9 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r10 = 17;
        r11 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = 0;
        r13 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r14 = 0;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14);
        r4.addView(r7, r8);
        r4 = new org.telegram.ui.Components.PopupAudioView;
        r4.<init>(r0);
        r8 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r8 = java.lang.Integer.valueOf(r8);
        r4.setTag(r8);
        r7.addView(r4);
        r7 = 3;
        r7 = java.lang.Integer.valueOf(r7);
        r3.setTag(r7);
        r7 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$VFWwjWrjLI64daw5erAQKADNXUs;
        r7.<init>(r0);
        r3.setOnClickListener(r7);
    L_0x0273:
        r4.setMessageObject(r6);
        r7 = r6.currentAccount;
        r7 = org.telegram.messenger.DownloadController.getInstance(r7);
        r6 = r7.canDownloadMedia(r6);
        if (r6 == 0) goto L_0x032e;
    L_0x0282:
        r4.downloadAudioIfNeed();
        goto L_0x032e;
    L_0x0287:
        r3 = r0.textViews;
        r3 = r3.size();
        if (r3 <= 0) goto L_0x029d;
    L_0x028f:
        r3 = r0.textViews;
        r3 = r3.get(r5);
        r3 = (android.view.ViewGroup) r3;
        r4 = r0.textViews;
        r4.remove(r5);
        goto L_0x0316;
    L_0x029d:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r0);
        r4 = new android.widget.ScrollView;
        r4.<init>(r0);
        r7 = 1;
        r4.setFillViewport(r7);
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r13);
        r3.addView(r4, r9);
        r9 = new android.widget.LinearLayout;
        r9.<init>(r0);
        r9.setOrientation(r5);
        r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r9.setBackgroundDrawable(r10);
        r10 = -2;
        r13 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r10, r7);
        r4.addView(r9, r13);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r9.setPadding(r4, r7, r10, r13);
        r4 = new org.telegram.ui.-$$Lambda$PopupNotificationActivity$1_iHFPQDV_CYBmOOqbZFgFmuyU8;
        r4.<init>(r0);
        r9.setOnClickListener(r4);
        r4 = new android.widget.TextView;
        r4.<init>(r0);
        r7 = 1;
        r4.setTextSize(r7, r8);
        r8 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r8 = java.lang.Integer.valueOf(r8);
        r4.setTag(r8);
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r4.setTextColor(r8);
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r4.setLinkTextColor(r8);
        r4.setGravity(r12);
        r8 = -2;
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r8, r12);
        r9.addView(r4, r8);
        r4 = java.lang.Integer.valueOf(r7);
        r3.setTag(r4);
    L_0x0316:
        r4 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r3.findViewWithTag(r4);
        r4 = (android.widget.TextView) r4;
        r7 = org.telegram.messenger.SharedConfig.fontSize;
        r7 = (float) r7;
        r8 = 2;
        r4.setTextSize(r8, r7);
        r6 = r6.messageText;
        r4.setText(r6);
    L_0x032e:
        r4 = r3.getParent();
        if (r4 != 0) goto L_0x0339;
    L_0x0334:
        r4 = r0.messageContainer;
        r4.addView(r3);
    L_0x0339:
        r3.setVisibility(r5);
        if (r29 == 0) goto L_0x0378;
    L_0x033e:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r5 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r5 = r3.getLayoutParams();
        r5 = (android.widget.FrameLayout.LayoutParams) r5;
        r6 = 51;
        r5.gravity = r6;
        r5.height = r2;
        r5.width = r4;
        r2 = r0.currentMessageNum;
        if (r1 != r2) goto L_0x0360;
    L_0x035b:
        r1 = 0;
        r3.setTranslationX(r1);
        goto L_0x0372;
    L_0x0360:
        r6 = r2 + -1;
        if (r1 != r6) goto L_0x036a;
    L_0x0364:
        r1 = -r4;
        r1 = (float) r1;
        r3.setTranslationX(r1);
        goto L_0x0372;
    L_0x036a:
        r6 = 1;
        r2 = r2 + r6;
        if (r1 != r2) goto L_0x0372;
    L_0x036e:
        r1 = (float) r4;
        r3.setTranslationX(r1);
    L_0x0372:
        r3.setLayoutParams(r5);
        r3.invalidate();
    L_0x0378:
        return r3;
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
        ViewGroup viewGroup;
        float translationX;
        LinearLayout buttonsViewForMessage;
        if (i == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            i = this.currentMessageNum - 1;
            while (true) {
                dp = this.currentMessageNum;
                if (i < dp + 2) {
                    if (i == dp - 1) {
                        this.leftView = getViewForMessage(i, true);
                        this.leftButtonsView = getButtonsViewForMessage(i, true);
                    } else if (i == dp) {
                        this.centerView = getViewForMessage(i, true);
                        this.centerButtonsView = getButtonsViewForMessage(i, true);
                    } else if (i == dp + 1) {
                        this.rightView = getViewForMessage(i, true);
                        this.rightButtonsView = getButtonsViewForMessage(i, true);
                    }
                    i++;
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
            viewGroup = this.rightView;
            if (viewGroup != null) {
                translationX = viewGroup.getTranslationX();
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
            viewGroup = this.rightButtonsView;
            if (viewGroup != null) {
                translationX = viewGroup.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                buttonsViewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = buttonsViewForMessage;
                if (buttonsViewForMessage != null) {
                    this.rightButtonsView.setTranslationX(translationX);
                }
            }
        } else if (i == 4) {
            viewGroup = this.leftView;
            if (viewGroup != null) {
                translationX = viewGroup.getTranslationX();
                reuseView(this.leftView);
                ViewGroup viewForMessage2 = getViewForMessage(0, false);
                this.leftView = viewForMessage2;
                if (viewForMessage2 != null) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.leftView.getLayoutParams();
                    layoutParams2.width = dp;
                    this.leftView.setLayoutParams(layoutParams2);
                    this.leftView.setTranslationX(translationX);
                    this.leftView.invalidate();
                }
            }
            viewGroup = this.leftButtonsView;
            if (viewGroup != null) {
                translationX = viewGroup.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                buttonsViewForMessage = getButtonsViewForMessage(0, false);
                this.leftButtonsView = buttonsViewForMessage;
                if (buttonsViewForMessage != null) {
                    this.leftButtonsView.setTranslationX(translationX);
                }
            }
        }
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!(PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving)) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                        marginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                        marginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                        marginLayoutParams.width = -1;
                        marginLayoutParams.height = -1;
                        PopupNotificationActivity.this.messageContainer.setLayoutParams(marginLayoutParams);
                        PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    }
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        boolean z = intent != null && intent.getBooleanExtra("force", false);
        this.isReply = z;
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

    private void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        Object obj;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = (MessageObject) this.popupMessages.get(i);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i;
                    obj = 1;
                    break;
                }
            }
        }
        obj = null;
        if (obj == null) {
            this.currentMessageNum = 0;
            this.currentMessageObject = (MessageObject) this.popupMessages.get(0);
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

    private void openCurrentMessage() {
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.tmessages.openchat");
            stringBuilder.append(Math.random());
            stringBuilder.append(Integer.MAX_VALUE);
            intent.setAction(stringBuilder.toString());
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
            Chat chat = this.currentChat;
            if (chat == null || this.currentUser == null) {
                User user = this.currentUser;
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
        if (this.actionBar != null) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null && this.currentChat == null) {
                User user = this.currentUser;
                if (user != null) {
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
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("+");
                            stringBuilder.append(this.currentUser.phone);
                            textView.setText(instance.format(stringBuilder.toString()));
                        }
                    }
                    User user2 = this.currentUser;
                    if (user2 == null || user2.id != 777000) {
                        CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                        if (charSequence == null || charSequence.length() == 0) {
                            this.lastPrintString = null;
                            setTypingAnimation(false);
                            user2 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                            if (user2 != null) {
                                this.currentUser = user2;
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
        }
    }

    private void checkAndUpdateAvatar() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            String str = "50_50";
            Object chat;
            if (this.currentChat != null) {
                chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    if (this.avatarImageView != null) {
                        this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), str, new AvatarDrawable(this.currentChat), chat);
                    }
                }
            } else if (this.currentUser != null) {
                chat = MessagesController.getInstance(messageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                if (chat != null) {
                    this.currentUser = chat;
                    if (this.avatarImageView != null) {
                        this.avatarImageView.setImage(ImageLocation.getForUser(chat, false), str, new AvatarDrawable(this.currentUser), chat);
                    }
                }
            }
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar != null) {
            int i = 0;
            if (!z) {
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                this.onlineTextView.setCompoundDrawablePadding(0);
                while (true) {
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (i >= statusDrawableArr.length) {
                        break;
                    }
                    statusDrawableArr[i].stop();
                    i++;
                }
            } else {
                try {
                    Integer num = (Integer) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[num.intValue()], null, null, null);
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
                    FileLog.e(e);
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

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000);
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
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

    /* JADX WARNING: Missing block: B:48:0x00a1, code skipped:
            if (r8.equals(r7) == false) goto L_0x00a3;
     */
    public void didReceivedNotification(int r7, int r8, java.lang.Object... r9) {
        /*
        r6 = this;
        r0 = org.telegram.messenger.NotificationCenter.appDidLogout;
        if (r7 != r0) goto L_0x0010;
    L_0x0004:
        r7 = r6.lastResumedAccount;
        if (r8 != r7) goto L_0x017e;
    L_0x0008:
        r6.onFinish();
        r6.finish();
        goto L_0x017e;
    L_0x0010:
        r0 = org.telegram.messenger.NotificationCenter.pushMessagesUpdated;
        r1 = 3;
        r2 = 0;
        if (r7 != r0) goto L_0x003e;
    L_0x0016:
        r7 = r6.isReply;
        if (r7 != 0) goto L_0x017e;
    L_0x001a:
        r7 = r6.popupMessages;
        r7.clear();
    L_0x001f:
        if (r2 >= r1) goto L_0x0039;
    L_0x0021:
        r7 = org.telegram.messenger.UserConfig.getInstance(r2);
        r7 = r7.isClientActivated();
        if (r7 == 0) goto L_0x0036;
    L_0x002b:
        r7 = r6.popupMessages;
        r8 = org.telegram.messenger.NotificationsController.getInstance(r2);
        r8 = r8.popupMessages;
        r7.addAll(r8);
    L_0x0036:
        r2 = r2 + 1;
        goto L_0x001f;
    L_0x0039:
        r6.getNewMessage();
        goto L_0x017e;
    L_0x003e:
        r0 = org.telegram.messenger.NotificationCenter.updateInterfaces;
        if (r7 != r0) goto L_0x00a9;
    L_0x0042:
        r7 = r6.currentMessageObject;
        if (r7 == 0) goto L_0x00a8;
    L_0x0046:
        r7 = r6.lastResumedAccount;
        if (r8 == r7) goto L_0x004b;
    L_0x004a:
        goto L_0x00a8;
    L_0x004b:
        r7 = r9[r2];
        r7 = (java.lang.Integer) r7;
        r7 = r7.intValue();
        r8 = r7 & 1;
        if (r8 != 0) goto L_0x0063;
    L_0x0057:
        r8 = r7 & 4;
        if (r8 != 0) goto L_0x0063;
    L_0x005b:
        r8 = r7 & 16;
        if (r8 != 0) goto L_0x0063;
    L_0x005f:
        r8 = r7 & 32;
        if (r8 == 0) goto L_0x0066;
    L_0x0063:
        r6.updateSubtitle();
    L_0x0066:
        r8 = r7 & 2;
        if (r8 != 0) goto L_0x006e;
    L_0x006a:
        r8 = r7 & 8;
        if (r8 == 0) goto L_0x0071;
    L_0x006e:
        r6.checkAndUpdateAvatar();
    L_0x0071:
        r7 = r7 & 64;
        if (r7 == 0) goto L_0x017e;
    L_0x0075:
        r7 = r6.currentMessageObject;
        r7 = r7.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r7 = r7.printingStrings;
        r8 = r6.currentMessageObject;
        r8 = r8.getDialogId();
        r7 = r7.get(r8);
        r7 = (java.lang.CharSequence) r7;
        r8 = r6.lastPrintString;
        if (r8 == 0) goto L_0x0091;
    L_0x008f:
        if (r7 == 0) goto L_0x00a3;
    L_0x0091:
        r8 = r6.lastPrintString;
        if (r8 != 0) goto L_0x0097;
    L_0x0095:
        if (r7 != 0) goto L_0x00a3;
    L_0x0097:
        r8 = r6.lastPrintString;
        if (r8 == 0) goto L_0x017e;
    L_0x009b:
        if (r7 == 0) goto L_0x017e;
    L_0x009d:
        r7 = r8.equals(r7);
        if (r7 != 0) goto L_0x017e;
    L_0x00a3:
        r6.updateSubtitle();
        goto L_0x017e;
    L_0x00a8:
        return;
    L_0x00a9:
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        r3 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        if (r7 != r0) goto L_0x00f5;
    L_0x00af:
        r7 = r9[r2];
        r7 = (java.lang.Integer) r7;
        r9 = r6.messageContainer;
        if (r9 == 0) goto L_0x017e;
    L_0x00b7:
        r9 = r9.getChildCount();
    L_0x00bb:
        if (r2 >= r9) goto L_0x017e;
    L_0x00bd:
        r0 = r6.messageContainer;
        r0 = r0.getChildAt(r2);
        r4 = r0.getTag();
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        if (r4 != r1) goto L_0x00f2;
    L_0x00cf:
        r4 = java.lang.Integer.valueOf(r3);
        r0 = r0.findViewWithTag(r4);
        r0 = (org.telegram.ui.Components.PopupAudioView) r0;
        r4 = r0.getMessageObject();
        if (r4 == 0) goto L_0x00f2;
    L_0x00df:
        r5 = r4.currentAccount;
        if (r5 != r8) goto L_0x00f2;
    L_0x00e3:
        r4 = r4.getId();
        r5 = r7.intValue();
        if (r4 != r5) goto L_0x00f2;
    L_0x00ed:
        r0.updateButtonState();
        goto L_0x017e;
    L_0x00f2:
        r2 = r2 + 1;
        goto L_0x00bb;
    L_0x00f5:
        r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        if (r7 != r0) goto L_0x013e;
    L_0x00f9:
        r7 = r9[r2];
        r7 = (java.lang.Integer) r7;
        r9 = r6.messageContainer;
        if (r9 == 0) goto L_0x017e;
    L_0x0101:
        r9 = r9.getChildCount();
    L_0x0105:
        if (r2 >= r9) goto L_0x017e;
    L_0x0107:
        r0 = r6.messageContainer;
        r0 = r0.getChildAt(r2);
        r4 = r0.getTag();
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        if (r4 != r1) goto L_0x013b;
    L_0x0119:
        r4 = java.lang.Integer.valueOf(r3);
        r0 = r0.findViewWithTag(r4);
        r0 = (org.telegram.ui.Components.PopupAudioView) r0;
        r4 = r0.getMessageObject();
        if (r4 == 0) goto L_0x013b;
    L_0x0129:
        r5 = r4.currentAccount;
        if (r5 != r8) goto L_0x013b;
    L_0x012d:
        r4 = r4.getId();
        r5 = r7.intValue();
        if (r4 != r5) goto L_0x013b;
    L_0x0137:
        r0.updateProgress();
        goto L_0x017e;
    L_0x013b:
        r2 = r2 + 1;
        goto L_0x0105;
    L_0x013e:
        r9 = org.telegram.messenger.NotificationCenter.emojiDidLoad;
        if (r7 != r9) goto L_0x0173;
    L_0x0142:
        r7 = r6.messageContainer;
        if (r7 == 0) goto L_0x017e;
    L_0x0146:
        r7 = r7.getChildCount();
    L_0x014a:
        if (r2 >= r7) goto L_0x017e;
    L_0x014c:
        r8 = r6.messageContainer;
        r8 = r8.getChildAt(r2);
        r9 = r8.getTag();
        r9 = (java.lang.Integer) r9;
        r9 = r9.intValue();
        r0 = 1;
        if (r9 != r0) goto L_0x0170;
    L_0x015f:
        r9 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r9 = java.lang.Integer.valueOf(r9);
        r8 = r8.findViewWithTag(r9);
        r8 = (android.widget.TextView) r8;
        if (r8 == 0) goto L_0x0170;
    L_0x016d:
        r8.invalidate();
    L_0x0170:
        r2 = r2 + 1;
        goto L_0x014a;
    L_0x0173:
        r9 = org.telegram.messenger.NotificationCenter.contactsDidLoad;
        if (r7 != r9) goto L_0x017e;
    L_0x0177:
        r7 = r6.lastResumedAccount;
        if (r8 != r7) goto L_0x017e;
    L_0x017b:
        r6.updateSubtitle();
    L_0x017e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
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

    /* Access modifiers changed, original: protected */
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
            ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}
