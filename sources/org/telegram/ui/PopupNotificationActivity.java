package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
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

    /* renamed from: org.telegram.ui.PopupNotificationActivity$5 */
    class C16195 implements OnClickListener {
        C16195() {
        }

        @TargetApi(9)
        public void onClick(DialogInterface dialog, int which) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                PopupNotificationActivity.this.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$6 */
    class C16206 implements Runnable {
        C16206() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$7 */
    class C16217 implements Runnable {
        C16217() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$8 */
    class C16228 implements Runnable {
        C16228() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$9 */
    class C16239 extends AnimatorListenerAdapter {
        C16239() {
        }

        public void onAnimationEnd(Animator animation) {
            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                PopupNotificationActivity.this.onAnimationEndRunnable = null;
            }
        }
    }

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
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$3 */
    class C22333 implements ChatActivityEnterViewDelegate {
        C22333() {
        }

        public void onMessageSend(CharSequence message) {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                    PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, true);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }
        }

        public void onTextChanged(CharSequence text, boolean big) {
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
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
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

        public void didPressedAttachButton() {
        }

        public void needStartRecordVideo(int state) {
        }

        public void needStartRecordAudio(int state) {
        }

        public void needChangeVideoPreviewState(int state, float seekProgress) {
        }

        public void needShowMediaBanHint() {
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$4 */
    class C22344 extends ActionBarMenuOnItemClick {
        C22344() {
        }

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
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.createChatResources(this, false);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.contactsDidLoaded);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable();
        this.statusDrawables[1] = new RecordStatusDrawable();
        this.statusDrawables[2] = new SendingFileDrawable();
        this.statusDrawables[3] = new PlayingGameDrawable();
        this.statusDrawables[4] = new RoundStatusDrawable();
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(this) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthMode = MeasureSpec.getMode(widthMeasureSpec);
                int heightMode = MeasureSpec.getMode(heightMeasureSpec);
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    heightSize -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + heightSize), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
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
                            case 48:
                                childTop = lp.topMargin;
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            childTop = paddingBottom != 0 ? getMeasuredHeight() - paddingBottom : getMeasuredHeight();
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
        this.popupContainer = new RelativeLayout(this) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int w = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int h = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int a = 0; a < getChildCount(); a++) {
                    View v = getChildAt(a);
                    if (v.getTag() instanceof String) {
                        v.measure(MeasureSpec.makeMeasureSpec(w, NUM), MeasureSpec.makeMeasureSpec(h - AndroidUtilities.dp(3.0f), NUM));
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                for (int a = 0; a < getChildCount(); a++) {
                    View v = getChildAt(a);
                    if (v.getTag() instanceof String) {
                        v.layout(v.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), v.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        this.popupContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        this.chatActivityEnterView = new ChatActivityEnterView(this, contentView, null, false);
        this.chatActivityEnterView.setId(id_chat_compose_panel);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new C22333());
        this.messageContainer = new FrameLayoutTouch(this);
        this.popupContainer.addView(this.messageContainer, 0);
        this.actionBar = new ActionBar(this);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem view = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        this.countText = new TextView(this);
        this.countText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        view.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        this.avatarContainer = new FrameLayout(this);
        this.avatarContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        LayoutParams layoutParams2 = (LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        this.avatarImageView = new BackupImageView(this);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        layoutParams2 = (LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(42.0f);
        layoutParams2.height = AndroidUtilities.dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams2);
        this.nameTextView = new TextView(this);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.avatarContainer.addView(this.nameTextView);
        layoutParams2 = (LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams2.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams2);
        this.onlineTextView = new TextView(this);
        this.onlineTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        layoutParams2 = (LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams2.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams2);
        this.actionBar.setActionBarMenuOnItemClick(new C22344());
        this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        this.wakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(this, newConfig);
        fixLayout();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults[0] != 0) {
            Builder builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", R.string.PermissionNoAudio));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new C16195());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.show();
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
            if (this.currentMessageNum > 0) {
                this.currentMessageNum--;
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
            if (this.onAnimationEndRunnable != null) {
                this.onAnimationEndRunnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    public boolean onTouchEventMy(MotionEvent motionEvent) {
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            diff = (int) (x - this.moveStartX);
            if (!(this.moveStartX == -1.0f || this.startedMoving || Math.abs(diff) <= AndroidUtilities.dp(10.0f))) {
                this.startedMoving = true;
                this.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                diff = 0;
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    this.velocityTracker.clear();
                }
            }
            if (this.startedMoving) {
                if (this.leftView == null && diff > 0) {
                    diff = 0;
                }
                if (this.rightView == null && diff < 0) {
                    diff = 0;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(motionEvent);
                }
                applyViewsLayoutParams(diff);
            }
        } else if (motionEvent == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent == null || !this.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                diff = (int) (motionEvent.getX() - this.moveStartX);
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                float moveDiff = 0.0f;
                int forceMove = 0;
                View otherView = null;
                View otherButtonsView = null;
                if (this.velocityTracker != null) {
                    this.velocityTracker.computeCurrentVelocity(id_chat_compose_panel);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        forceMove = 1;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        forceMove = 2;
                    }
                }
                if ((forceMove == 1 || diff > width / 3) && this.leftView != null) {
                    moveDiff = ((float) width) - this.centerView.getTranslationX();
                    otherView = this.leftView;
                    otherButtonsView = this.leftButtonsView;
                    this.onAnimationEndRunnable = new C16206();
                } else if ((forceMove == 2 || diff < (-width) / 3) && this.rightView != null) {
                    moveDiff = ((float) (-width)) - this.centerView.getTranslationX();
                    otherView = this.rightView;
                    otherButtonsView = this.rightButtonsView;
                    this.onAnimationEndRunnable = new C16217();
                } else if (this.centerView.getTranslationX() != 0.0f) {
                    moveDiff = -this.centerView.getTranslationX();
                    otherView = diff > 0 ? this.leftView : this.rightView;
                    if (diff > 0) {
                        otherButtonsView = this.leftButtonsView;
                    } else {
                        otherButtonsView = this.rightButtonsView;
                    }
                    this.onAnimationEndRunnable = new C16228();
                }
                if (moveDiff != 0.0f) {
                    int time = (int) (Math.abs(moveDiff / ((float) width)) * 200.0f);
                    ArrayList<Animator> animators = new ArrayList();
                    animators.add(ObjectAnimator.ofFloat(this.centerView, "translationX", new float[]{this.centerView.getTranslationX() + moveDiff}));
                    if (this.centerButtonsView != null) {
                        animators.add(ObjectAnimator.ofFloat(this.centerButtonsView, "translationX", new float[]{this.centerButtonsView.getTranslationX() + moveDiff}));
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
                    animatorSet.addListener(new C16239());
                    animatorSet.start();
                    this.animationInProgress = true;
                    this.animationStartTime = System.currentTimeMillis();
                }
            }
            if (this.velocityTracker != null) {
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }
            this.startedMoving = false;
            this.moveStartX = -1.0f;
        }
        return this.startedMoving;
    }

    private void applyViewsLayoutParams(int xOffset) {
        LayoutParams layoutParams;
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (this.leftView != null) {
            layoutParams = (LayoutParams) this.leftView.getLayoutParams();
            if (layoutParams.width != widht) {
                layoutParams.width = widht;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((float) ((-widht) + xOffset));
        }
        if (this.leftButtonsView != null) {
            this.leftButtonsView.setTranslationX((float) ((-widht) + xOffset));
        }
        if (this.centerView != null) {
            layoutParams = (LayoutParams) this.centerView.getLayoutParams();
            if (layoutParams.width != widht) {
                layoutParams.width = widht;
                this.centerView.setLayoutParams(layoutParams);
            }
            this.centerView.setTranslationX((float) xOffset);
        }
        if (this.centerButtonsView != null) {
            this.centerButtonsView.setTranslationX((float) xOffset);
        }
        if (this.rightView != null) {
            layoutParams = (LayoutParams) this.rightView.getLayoutParams();
            if (layoutParams.width != widht) {
                layoutParams.width = widht;
                this.rightView.setLayoutParams(layoutParams);
            }
            this.rightView.setTranslationX((float) (widht + xOffset));
        }
        if (this.rightButtonsView != null) {
            this.rightButtonsView.setTranslationX((float) (widht + xOffset));
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int num, boolean applyOffset) {
        if (this.popupMessages.size() == 1 && (num < 0 || num >= this.popupMessages.size())) {
            return null;
        }
        ArrayList<TL_keyboardButtonRow> rows;
        int size;
        int a;
        TL_keyboardButtonRow row;
        int size2;
        int b;
        if (num == -1) {
            num = this.popupMessages.size() - 1;
        } else if (num == this.popupMessages.size()) {
            num = 0;
        }
        LinearLayout view = null;
        final MessageObject messageObject = (MessageObject) this.popupMessages.get(num);
        int buttonsCount = 0;
        ReplyMarkup markup = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() == 777000 && markup != null) {
            rows = markup.rows;
            size = rows.size();
            for (a = 0; a < size; a++) {
                row = (TL_keyboardButtonRow) rows.get(a);
                size2 = row.buttons.size();
                for (b = 0; b < size2; b++) {
                    if (((KeyboardButton) row.buttons.get(b)) instanceof TL_keyboardButtonCallback) {
                        buttonsCount++;
                    }
                }
            }
        }
        final int account = messageObject.currentAccount;
        if (buttonsCount > 0) {
            rows = markup.rows;
            size = rows.size();
            for (a = 0; a < size; a++) {
                row = (TL_keyboardButtonRow) rows.get(a);
                size2 = row.buttons.size();
                for (b = 0; b < size2; b++) {
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    if (button instanceof TL_keyboardButtonCallback) {
                        if (view == null) {
                            view = new LinearLayout(this);
                            view.setOrientation(0);
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            view.setWeightSum(100.0f);
                            view.setTag("b");
                            view.setOnTouchListener(new OnTouchListener() {
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                        }
                        TextView textView = new TextView(this);
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(button.text.toUpperCase());
                        textView.setTag(button);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        view.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) buttonsCount)));
                        textView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                KeyboardButton button = (KeyboardButton) v.getTag();
                                if (button != null) {
                                    SendMessagesHelper.getInstance(account).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), button.data);
                                }
                            }
                        });
                    }
                }
            }
        }
        if (view == null) {
            return view;
        }
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(12);
        if (applyOffset) {
            if (num == this.currentMessageNum) {
                view.setTranslationX(0.0f);
            } else if (num == this.currentMessageNum - 1) {
                view.setTranslationX((float) (-widht));
            } else if (num == this.currentMessageNum + 1) {
                view.setTranslationX((float) widht);
            }
        }
        this.popupContainer.addView(view, layoutParams);
        return view;
    }

    private android.view.ViewGroup getViewForMessage(int r35, boolean r36) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r30_12 'view' android.view.View) in PHI: PHI: (r30_11 'view' android.view.View) = (r30_10 'view' android.view.View), (r30_12 'view' android.view.View) binds: {(r30_10 'view' android.view.View)=B:15:0x004f, (r30_12 'view' android.view.View)=B:43:0x0167}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r34 = this;
        r0 = r34;
        r2 = r0.popupMessages;
        r2 = r2.size();
        r3 = 1;
        if (r2 != r3) goto L_0x001c;
    L_0x000b:
        if (r35 < 0) goto L_0x0019;
    L_0x000d:
        r0 = r34;
        r2 = r0.popupMessages;
        r2 = r2.size();
        r0 = r35;
        if (r0 < r2) goto L_0x001c;
    L_0x0019:
        r30 = 0;
    L_0x001b:
        return r30;
    L_0x001c:
        r2 = -1;
        r0 = r35;
        if (r0 != r2) goto L_0x0157;
    L_0x0021:
        r0 = r34;
        r2 = r0.popupMessages;
        r2 = r2.size();
        r35 = r2 + -1;
    L_0x002b:
        r0 = r34;
        r2 = r0.popupMessages;
        r0 = r35;
        r21 = r2.get(r0);
        r21 = (org.telegram.messenger.MessageObject) r21;
        r0 = r21;
        r2 = r0.type;
        r3 = 1;
        if (r2 == r3) goto L_0x0045;
    L_0x003e:
        r0 = r21;
        r2 = r0.type;
        r3 = 4;
        if (r2 != r3) goto L_0x02b4;
    L_0x0045:
        r0 = r34;
        r2 = r0.imageViews;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x0167;
    L_0x004f:
        r0 = r34;
        r2 = r0.imageViews;
        r3 = 0;
        r30 = r2.get(r3);
        r30 = (android.view.ViewGroup) r30;
        r0 = r34;
        r2 = r0.imageViews;
        r3 = 0;
        r2.remove(r3);
    L_0x0062:
        r2 = 312; // 0x138 float:4.37E-43 double:1.54E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r24 = r0.findViewWithTag(r2);
        r24 = (android.widget.TextView) r24;
        r2 = 311; // 0x137 float:4.36E-43 double:1.537E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r16 = r0.findViewWithTag(r2);
        r16 = (org.telegram.ui.Components.BackupImageView) r16;
        r2 = 1;
        r0 = r16;
        r0.setAspectFit(r2);
        r0 = r21;
        r2 = r0.type;
        r3 = 1;
        if (r2 != r3) goto L_0x0236;
    L_0x008b:
        r0 = r21;
        r2 = r0.photoThumbs;
        r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r12 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r0 = r21;
        r2 = r0.photoThumbs;
        r3 = 100;
        r29 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r26 = 0;
        if (r12 == 0) goto L_0x00e2;
    L_0x00a5:
        r25 = 1;
        r0 = r21;
        r2 = r0.type;
        r3 = 1;
        if (r2 != r3) goto L_0x00be;
    L_0x00ae:
        r0 = r21;
        r2 = r0.messageOwner;
        r10 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
        r2 = r10.exists();
        if (r2 != 0) goto L_0x00be;
    L_0x00bc:
        r25 = 0;
    L_0x00be:
        if (r25 != 0) goto L_0x00d0;
    L_0x00c0:
        r0 = r21;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r0 = r21;
        r2 = r2.canDownloadMedia(r0);
        if (r2 == 0) goto L_0x0214;
    L_0x00d0:
        r2 = r12.location;
        r3 = "100_100";
        r0 = r29;
        r4 = r0.location;
        r5 = r12.size;
        r0 = r16;
        r0.setImage(r2, r3, r4, r5);
        r26 = 1;
    L_0x00e2:
        if (r26 != 0) goto L_0x0227;
    L_0x00e4:
        r2 = 8;
        r0 = r16;
        r0.setVisibility(r2);
        r2 = 0;
        r0 = r24;
        r0.setVisibility(r2);
        r2 = 2;
        r3 = org.telegram.messenger.SharedConfig.fontSize;
        r3 = (float) r3;
        r0 = r24;
        r0.setTextSize(r2, r3);
        r0 = r21;
        r2 = r0.messageText;
        r0 = r24;
        r0.setText(r2);
    L_0x0103:
        r2 = r30.getParent();
        if (r2 != 0) goto L_0x0112;
    L_0x0109:
        r0 = r34;
        r2 = r0.messageContainer;
        r0 = r30;
        r2.addView(r0);
    L_0x0112:
        r2 = 0;
        r0 = r30;
        r0.setVisibility(r2);
        if (r36 == 0) goto L_0x001b;
    L_0x011a:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r31 = r2 - r3;
        r17 = r30.getLayoutParams();
        r17 = (android.widget.FrameLayout.LayoutParams) r17;
        r2 = 51;
        r0 = r17;
        r0.gravity = r2;
        r2 = -1;
        r0 = r17;
        r0.height = r2;
        r0 = r31;
        r1 = r17;
        r1.width = r0;
        r0 = r34;
        r2 = r0.currentMessageNum;
        r0 = r35;
        if (r0 != r2) goto L_0x048e;
    L_0x0145:
        r2 = 0;
        r0 = r30;
        r0.setTranslationX(r2);
    L_0x014b:
        r0 = r30;
        r1 = r17;
        r0.setLayoutParams(r1);
        r30.invalidate();
        goto L_0x001b;
    L_0x0157:
        r0 = r34;
        r2 = r0.popupMessages;
        r2 = r2.size();
        r0 = r35;
        if (r0 != r2) goto L_0x002b;
    L_0x0163:
        r35 = 0;
        goto L_0x002b;
    L_0x0167:
        r30 = new android.widget.FrameLayout;
        r0 = r30;
        r1 = r34;
        r0.<init>(r1);
        r14 = new android.widget.FrameLayout;
        r0 = r34;
        r14.<init>(r0);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r14.setPadding(r2, r3, r4, r5);
        r2 = 0;
        r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2);
        r14.setBackgroundDrawable(r2);
        r2 = -1;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3);
        r0 = r30;
        r0.addView(r14, r2);
        r9 = new org.telegram.ui.Components.BackupImageView;
        r0 = r34;
        r9.<init>(r0);
        r2 = 311; // 0x137 float:4.36E-43 double:1.537E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r9.setTag(r2);
        r2 = -1;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3);
        r14.addView(r9, r2);
        r28 = new android.widget.TextView;
        r0 = r28;
        r1 = r34;
        r0.<init>(r1);
        r2 = "windowBackgroundWhiteBlackText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0 = r28;
        r0.setTextColor(r2);
        r2 = 1;
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r0 = r28;
        r0.setTextSize(r2, r3);
        r2 = 17;
        r0 = r28;
        r0.setGravity(r2);
        r2 = 312; // 0x138 float:4.37E-43 double:1.54E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r28;
        r0.setTag(r2);
        r2 = -1;
        r3 = -2;
        r4 = 17;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4);
        r0 = r28;
        r14.addView(r0, r2);
        r2 = 2;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r0.setTag(r2);
        r2 = new org.telegram.ui.PopupNotificationActivity$12;
        r0 = r34;
        r2.<init>();
        r0 = r30;
        r0.setOnClickListener(r2);
        goto L_0x0062;
    L_0x0214:
        if (r29 == 0) goto L_0x00e2;
    L_0x0216:
        r0 = r29;
        r3 = r0.location;
        r4 = 0;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r0 = r16;
        r0.setImage(r3, r4, r2);
        r26 = 1;
        goto L_0x00e2;
    L_0x0227:
        r2 = 0;
        r0 = r16;
        r0.setVisibility(r2);
        r2 = 8;
        r0 = r24;
        r0.setVisibility(r2);
        goto L_0x0103;
    L_0x0236:
        r0 = r21;
        r2 = r0.type;
        r3 = 4;
        if (r2 != r3) goto L_0x0103;
    L_0x023d:
        r2 = 8;
        r0 = r24;
        r0.setVisibility(r2);
        r0 = r21;
        r2 = r0.messageText;
        r0 = r24;
        r0.setText(r2);
        r2 = 0;
        r0 = r16;
        r0.setVisibility(r2);
        r0 = r21;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.geo;
        r0 = r2.lat;
        r18 = r0;
        r0 = r21;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.geo;
        r0 = r2._long;
        r22 = r0;
        r2 = java.util.Locale.US;
        r3 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false";
        r4 = 5;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r6 = java.lang.Double.valueOf(r18);
        r4[r5] = r6;
        r5 = 1;
        r6 = java.lang.Double.valueOf(r22);
        r4[r5] = r6;
        r5 = 2;
        r6 = 2;
        r7 = org.telegram.messenger.AndroidUtilities.density;
        r0 = (double) r7;
        r32 = r0;
        r32 = java.lang.Math.ceil(r32);
        r0 = r32;
        r7 = (int) r0;
        r6 = java.lang.Math.min(r6, r7);
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r5 = 3;
        r6 = java.lang.Double.valueOf(r18);
        r4[r5] = r6;
        r5 = 4;
        r6 = java.lang.Double.valueOf(r22);
        r4[r5] = r6;
        r13 = java.lang.String.format(r2, r3, r4);
        r2 = 0;
        r3 = 0;
        r0 = r16;
        r0.setImage(r13, r2, r3);
        goto L_0x0103;
    L_0x02b4:
        r0 = r21;
        r2 = r0.type;
        r3 = 2;
        if (r2 != r3) goto L_0x0383;
    L_0x02bb:
        r0 = r34;
        r2 = r0.audioViews;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x0300;
    L_0x02c5:
        r0 = r34;
        r2 = r0.audioViews;
        r3 = 0;
        r30 = r2.get(r3);
        r30 = (android.view.ViewGroup) r30;
        r0 = r34;
        r2 = r0.audioViews;
        r3 = 0;
        r2.remove(r3);
        r2 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r11 = r0.findViewWithTag(r2);
        r11 = (org.telegram.ui.Components.PopupAudioView) r11;
    L_0x02e6:
        r0 = r21;
        r11.setMessageObject(r0);
        r0 = r21;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r0 = r21;
        r2 = r2.canDownloadMedia(r0);
        if (r2 == 0) goto L_0x0103;
    L_0x02fb:
        r11.downloadAudioIfNeed();
        goto L_0x0103;
    L_0x0300:
        r30 = new android.widget.FrameLayout;
        r0 = r30;
        r1 = r34;
        r0.<init>(r1);
        r14 = new android.widget.FrameLayout;
        r0 = r34;
        r14.<init>(r0);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r14.setPadding(r2, r3, r4, r5);
        r2 = 0;
        r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2);
        r14.setBackgroundDrawable(r2);
        r2 = -1;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3);
        r0 = r30;
        r0.addView(r14, r2);
        r15 = new android.widget.FrameLayout;
        r0 = r34;
        r15.<init>(r0);
        r2 = -1;
        r3 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r4 = 17;
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = 0;
        r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r8 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8);
        r14.addView(r15, r2);
        r11 = new org.telegram.ui.Components.PopupAudioView;
        r0 = r34;
        r11.<init>(r0);
        r2 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r11.setTag(r2);
        r15.addView(r11);
        r2 = 3;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r0.setTag(r2);
        r2 = new org.telegram.ui.PopupNotificationActivity$13;
        r0 = r34;
        r2.<init>();
        r0 = r30;
        r0.setOnClickListener(r2);
        goto L_0x02e6;
    L_0x0383:
        r0 = r34;
        r2 = r0.textViews;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x03c2;
    L_0x038d:
        r0 = r34;
        r2 = r0.textViews;
        r3 = 0;
        r30 = r2.get(r3);
        r30 = (android.view.ViewGroup) r30;
        r0 = r34;
        r2 = r0.textViews;
        r3 = 0;
        r2.remove(r3);
    L_0x03a0:
        r2 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r24 = r0.findViewWithTag(r2);
        r24 = (android.widget.TextView) r24;
        r2 = 2;
        r3 = org.telegram.messenger.SharedConfig.fontSize;
        r3 = (float) r3;
        r0 = r24;
        r0.setTextSize(r2, r3);
        r0 = r21;
        r2 = r0.messageText;
        r0 = r24;
        r0.setText(r2);
        goto L_0x0103;
    L_0x03c2:
        r30 = new android.widget.FrameLayout;
        r0 = r30;
        r1 = r34;
        r0.<init>(r1);
        r27 = new android.widget.ScrollView;
        r0 = r27;
        r1 = r34;
        r0.<init>(r1);
        r2 = 1;
        r0 = r27;
        r0.setFillViewport(r2);
        r2 = -1;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3);
        r0 = r30;
        r1 = r27;
        r0.addView(r1, r2);
        r20 = new android.widget.LinearLayout;
        r0 = r20;
        r1 = r34;
        r0.<init>(r1);
        r2 = 0;
        r0 = r20;
        r0.setOrientation(r2);
        r2 = 0;
        r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2);
        r0 = r20;
        r0.setBackgroundDrawable(r2);
        r2 = -1;
        r3 = -2;
        r4 = 1;
        r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r3, r4);
        r0 = r27;
        r1 = r20;
        r0.addView(r1, r2);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = r20;
        r0.setPadding(r2, r3, r4, r5);
        r2 = new org.telegram.ui.PopupNotificationActivity$14;
        r0 = r34;
        r2.<init>();
        r0 = r20;
        r0.setOnClickListener(r2);
        r28 = new android.widget.TextView;
        r0 = r28;
        r1 = r34;
        r0.<init>(r1);
        r2 = 1;
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r0 = r28;
        r0.setTextSize(r2, r3);
        r2 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r28;
        r0.setTag(r2);
        r2 = "windowBackgroundWhiteBlackText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0 = r28;
        r0.setTextColor(r2);
        r2 = "windowBackgroundWhiteBlackText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0 = r28;
        r0.setLinkTextColor(r2);
        r2 = 17;
        r0 = r28;
        r0.setGravity(r2);
        r2 = -1;
        r3 = -2;
        r4 = 17;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4);
        r0 = r20;
        r1 = r28;
        r0.addView(r1, r2);
        r2 = 1;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r30;
        r0.setTag(r2);
        goto L_0x03a0;
    L_0x048e:
        r0 = r34;
        r2 = r0.currentMessageNum;
        r2 = r2 + -1;
        r0 = r35;
        if (r0 != r2) goto L_0x04a3;
    L_0x0498:
        r0 = r31;
        r2 = -r0;
        r2 = (float) r2;
        r0 = r30;
        r0.setTranslationX(r2);
        goto L_0x014b;
    L_0x04a3:
        r0 = r34;
        r2 = r0.currentMessageNum;
        r2 = r2 + 1;
        r0 = r35;
        if (r0 != r2) goto L_0x014b;
    L_0x04ad:
        r0 = r31;
        r2 = (float) r0;
        r0 = r30;
        r0.setTranslationX(r2);
        goto L_0x014b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
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
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (move == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            for (int a = this.currentMessageNum - 1; a < this.currentMessageNum + 2; a++) {
                if (a == this.currentMessageNum - 1) {
                    this.leftView = getViewForMessage(a, true);
                    this.leftButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == this.currentMessageNum) {
                    this.centerView = getViewForMessage(a, true);
                    this.centerButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == this.currentMessageNum + 1) {
                    this.rightView = getViewForMessage(a, true);
                    this.rightButtonsView = getButtonsViewForMessage(a, true);
                }
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
            if (this.rightView != null) {
                offset = this.rightView.getTranslationX();
                reuseView(this.rightView);
                r4 = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = r4;
                if (r4 != null) {
                    layoutParams = (LayoutParams) this.rightView.getLayoutParams();
                    layoutParams.width = widht;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.setTranslationX(offset);
                    this.rightView.invalidate();
                }
            }
            if (this.rightButtonsView != null) {
                offset = this.rightButtonsView.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                r4 = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = r4;
                if (r4 != null) {
                    this.rightButtonsView.setTranslationX(offset);
                }
            }
        } else if (move == 4) {
            if (this.leftView != null) {
                offset = this.leftView.getTranslationX();
                reuseView(this.leftView);
                r4 = getViewForMessage(0, false);
                this.leftView = r4;
                if (r4 != null) {
                    layoutParams = (LayoutParams) this.leftView.getLayoutParams();
                    layoutParams.width = widht;
                    this.leftView.setLayoutParams(layoutParams);
                    this.leftView.setTranslationX(offset);
                    this.leftView.invalidate();
                }
            }
            if (this.leftButtonsView != null) {
                offset = this.leftButtonsView.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                r4 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = r4;
                if (r4 != null) {
                    this.leftButtonsView.setTranslationX(offset);
                }
            }
        }
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
        if (this.messageContainer != null) {
            this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!(PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving)) {
                        MarginLayoutParams layoutParams = (MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                        layoutParams.width = -1;
                        layoutParams.height = -1;
                        PopupNotificationActivity.this.messageContainer.setLayoutParams(layoutParams);
                        PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    }
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        boolean z;
        if (intent == null || !intent.getBooleanExtra("force", false)) {
            z = false;
        } else {
            z = true;
        }
        this.isReply = z;
        this.popupMessages.clear();
        if (this.isReply) {
            this.popupMessages.addAll(NotificationsController.getInstance(intent != null ? intent.getIntExtra("currentAccount", UserConfig.selectedAccount) : UserConfig.selectedAccount).popupReplyMessages);
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

    private void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        boolean found = false;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            for (int a = 0; a < size; a++) {
                MessageObject messageObject = (MessageObject) this.popupMessages.get(a);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = a;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
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
            long dialog_id = this.currentMessageObject.getDialogId();
            if (((int) dialog_id) != 0) {
                int lower_id = (int) dialog_id;
                if (lower_id < 0) {
                    intent.putExtra("chatId", -lower_id);
                } else {
                    intent.putExtra("userId", lower_id);
                }
            } else {
                intent.putExtra("encId", (int) (dialog_id >> 32));
            }
            intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(32768);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void updateInterfaceForCurrentMessage(int move) {
        if (this.actionBar != null) {
            if (this.lastResumedAccount != this.currentMessageObject.currentAccount) {
                if (this.lastResumedAccount >= 0) {
                    ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(true, false);
                }
                this.lastResumedAccount = this.currentMessageObject.currentAccount;
                ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(false, false);
            }
            this.currentChat = null;
            this.currentUser = null;
            long dialog_id = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialog_id, this.currentMessageObject.currentAccount);
            if (((int) dialog_id) != 0) {
                int lower_id = (int) dialog_id;
                if (lower_id > 0) {
                    this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(lower_id));
                } else {
                    this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(-lower_id));
                    this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                }
            } else {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32))).user_id));
            }
            if (this.currentChat != null && this.currentUser != null) {
                this.nameTextView.setText(this.currentChat.title);
                this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            } else if (this.currentUser != null) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                if (((int) dialog_id) == 0) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
            prepareLayouts(move);
            updateSubtitle();
            checkAndUpdateAvatar();
            applyViewsLayoutParams(0);
        }
    }

    private void updateSubtitle() {
        if (this.actionBar != null && this.currentMessageObject != null && this.currentChat == null && this.currentUser != null) {
            if (this.currentUser.id / id_chat_compose_panel == 777 || this.currentUser.id / id_chat_compose_panel == 333 || ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) != null || (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
            }
            if (this.currentUser == null || this.currentUser.id != 777000) {
                CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                if (printString == null || printString.length() == 0) {
                    this.lastPrintString = null;
                    setTypingAnimation(false);
                    User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                    if (user != null) {
                        this.currentUser = user;
                    }
                    this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
                    return;
                }
                this.lastPrintString = printString;
                this.onlineTextView.setText(printString);
                setTypingAnimation(true);
                return;
            }
            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications));
        }
    }

    private void checkAndUpdateAvatar() {
        if (this.currentMessageObject != null) {
            TLObject newPhoto = null;
            Drawable avatarDrawable = null;
            if (this.currentChat != null) {
                Chat chat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    if (this.currentChat.photo != null) {
                        newPhoto = this.currentChat.photo.photo_small;
                    }
                    avatarDrawable = new AvatarDrawable(this.currentChat);
                } else {
                    return;
                }
            } else if (this.currentUser != null) {
                User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                    if (this.currentUser.photo != null) {
                        newPhoto = this.currentUser.photo.photo_small;
                    }
                    avatarDrawable = new AvatarDrawable(this.currentUser);
                } else {
                    return;
                }
            }
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(newPhoto, "50_50", avatarDrawable);
            }
        }
    }

    private void setTypingAnimation(boolean start) {
        if (this.actionBar != null) {
            int a;
            if (start) {
                try {
                    Integer type = (Integer) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[type.intValue()], null, null, null);
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    for (a = 0; a < this.statusDrawables.length; a++) {
                        if (a == type.intValue()) {
                            this.statusDrawables[a].start();
                        } else {
                            this.statusDrawables[a].stop();
                        }
                    }
                    return;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return;
                }
            }
            this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            this.onlineTextView.setCompoundDrawablePadding(0);
            for (StatusDrawable stop : this.statusDrawables) {
                stop.stop();
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

    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000);
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        if (this.lastResumedAccount >= 0) {
            ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(true, false);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            if (account == this.lastResumedAccount) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.pushMessagesUpdated) {
            if (!this.isReply) {
                this.popupMessages.clear();
                for (a = 0; a < 3; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        this.popupMessages.addAll(NotificationsController.getInstance(a).popupMessages);
                    }
                }
                getNewMessage();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject != null && account == this.lastResumedAccount) {
                int updateMask = ((Integer) args[0]).intValue();
                if (!((updateMask & 1) == 0 && (updateMask & 4) == 0 && (updateMask & 16) == 0 && (updateMask & 32) == 0)) {
                    updateSubtitle();
                }
                if (!((updateMask & 2) == 0 && (updateMask & 8) == 0)) {
                    checkAndUpdateAvatar();
                }
                if ((updateMask & 64) != 0) {
                    CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                    if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || (this.lastPrintString != null && printString != null && !this.lastPrintString.equals(printString)))) {
                        updateSubtitle();
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset) {
            mid = args[0];
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 3) {
                        cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && messageObject.currentAccount == account && messageObject.getId() == mid.intValue()) {
                            cell.updateButtonState();
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            mid = (Integer) args[0];
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 3) {
                        cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && messageObject.currentAccount == account && messageObject.getId() == mid.intValue()) {
                            cell.updateProgress();
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 1) {
                        TextView textView = (TextView) view.findViewWithTag(Integer.valueOf(301));
                        if (textView != null) {
                            textView.invalidate();
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded && account == this.lastResumedAccount) {
            updateSubtitle();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
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
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.contactsDidLoaded);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            if (this.chatActivityEnterView != null) {
                this.chatActivityEnterView.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}