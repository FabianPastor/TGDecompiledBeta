package org.telegram.p005ui;

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
import android.os.PowerManager.WakeLock;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.extractor.p003ts.PsExtractor;
import java.util.ArrayList;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.ChatActivityEnterView;
import org.telegram.p005ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.PlayingGameDrawable;
import org.telegram.p005ui.Components.PopupAudioView;
import org.telegram.p005ui.Components.RecordStatusDrawable;
import org.telegram.p005ui.Components.RoundStatusDrawable;
import org.telegram.p005ui.Components.SendingFileDrawable;
import org.telegram.p005ui.Components.SizeNotifierFrameLayout;
import org.telegram.p005ui.Components.StatusDrawable;
import org.telegram.p005ui.Components.TypingDotsDrawable;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.PopupNotificationActivity */
public class PopupNotificationActivity extends Activity implements NotificationCenterDelegate {
    private static final int id_chat_compose_panel = 1000;
    private CLASSNAMEActionBar actionBar;
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
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                PopupNotificationActivity.this.onAnimationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$6 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            if (PopupNotificationActivity.this.avatarContainer != null) {
                PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            int padding = (CLASSNAMEActionBar.getCurrentActionBarHeight() - AndroidUtilities.m9dp(48.0f)) / 2;
            PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), padding, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), padding);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$7 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
            if (!(PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving)) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                layoutParams.topMargin = CLASSNAMEActionBar.getCurrentActionBarHeight();
                layoutParams.bottomMargin = AndroidUtilities.m9dp(48.0f);
                layoutParams.width = -1;
                layoutParams.height = -1;
                PopupNotificationActivity.this.messageContainer.setLayoutParams(layoutParams);
                PopupNotificationActivity.this.applyViewsLayoutParams(0);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$FrameLayoutTouch */
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
    class CLASSNAME implements ChatActivityEnterViewDelegate {
        CLASSNAME() {
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

        public void onTextSelectionChanged(int start, int end) {
        }

        public void onTextSpansChanged(CharSequence text) {
        }

        public void onStickersExpandedChange() {
        }

        public void onSetTranslationY(int translation) {
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
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
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
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
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
                if (getKeyboardHeight() <= AndroidUtilities.m9dp(20.0f)) {
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
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(2.0f) + heightSize), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = getKeyboardHeight() <= AndroidUtilities.m9dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
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
                        v.measure(MeasureSpec.makeMeasureSpec(w, NUM), MeasureSpec.makeMeasureSpec(h - AndroidUtilities.m9dp(3.0f), NUM));
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                for (int a = 0; a < getChildCount(); a++) {
                    View v = getChildAt(a);
                    if (v.getTag() instanceof String) {
                        v.layout(v.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.m9dp(3.0f), v.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
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
        this.chatActivityEnterView.setDelegate(new CLASSNAME());
        this.messageContainer = new FrameLayoutTouch(this);
        this.popupContainer.addView(this.messageContainer, 0);
        this.actionBar = new CLASSNAMEActionBar(this);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem view = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.m9dp(56.0f));
        this.countText = new TextView(this);
        this.countText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        view.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        this.avatarContainer = new FrameLayout(this);
        this.avatarContainer.setPadding(AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        LayoutParams layoutParams2 = (LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.m9dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.m9dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        this.avatarImageView = new BackupImageView(this);
        this.avatarImageView.setRoundRadius(AndroidUtilities.m9dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        layoutParams2 = (LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.m9dp(42.0f);
        layoutParams2.height = AndroidUtilities.m9dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.m9dp(3.0f);
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
        layoutParams2.leftMargin = AndroidUtilities.m9dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.m9dp(22.0f);
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
        layoutParams2.leftMargin = AndroidUtilities.m9dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.m9dp(4.0f);
        layoutParams2.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams2);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new PopupNotificationActivity$$Lambda$0(this));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.show();
        }
    }

    final /* synthetic */ void lambda$onRequestPermissionsResult$0$PopupNotificationActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            FileLog.m13e(e);
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
        int diff;
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            diff = (int) (x - this.moveStartX);
            if (!(this.moveStartX == -1.0f || this.startedMoving || Math.abs(diff) <= AndroidUtilities.m9dp(10.0f))) {
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
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(24.0f);
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
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$Lambda$1(this);
                } else if ((forceMove == 2 || diff < (-width) / 3) && this.rightView != null) {
                    moveDiff = ((float) (-width)) - this.centerView.getTranslationX();
                    otherView = this.rightView;
                    otherButtonsView = this.rightButtonsView;
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$Lambda$2(this);
                } else if (this.centerView.getTranslationX() != 0.0f) {
                    moveDiff = -this.centerView.getTranslationX();
                    otherView = diff > 0 ? this.leftView : this.rightView;
                    if (diff > 0) {
                        otherButtonsView = this.leftButtonsView;
                    } else {
                        otherButtonsView = this.rightButtonsView;
                    }
                    this.onAnimationEndRunnable = new PopupNotificationActivity$$Lambda$3(this);
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
                    animatorSet.addListener(new CLASSNAME());
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

    final /* synthetic */ void lambda$onTouchEventMy$1$PopupNotificationActivity() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    final /* synthetic */ void lambda$onTouchEventMy$2$PopupNotificationActivity() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    final /* synthetic */ void lambda$onTouchEventMy$3$PopupNotificationActivity() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    private void applyViewsLayoutParams(int xOffset) {
        LayoutParams layoutParams;
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(24.0f);
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
        MessageObject messageObject = (MessageObject) this.popupMessages.get(num);
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
        int account = messageObject.currentAccount;
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
                            view.setOnTouchListener(PopupNotificationActivity$$Lambda$4.$instance);
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
                        textView.setOnClickListener(new PopupNotificationActivity$$Lambda$5(account, messageObject));
                    }
                }
            }
        }
        if (view == null) {
            return view;
        }
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(24.0f);
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

    static final /* synthetic */ void lambda$getButtonsViewForMessage$5$PopupNotificationActivity(int account, MessageObject messageObject, View v) {
        KeyboardButton button1 = (KeyboardButton) v.getTag();
        if (button1 != null) {
            SendMessagesHelper.getInstance(account).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), button1.data);
        }
    }

    private ViewGroup getViewForMessage(int num, boolean applyOffset) {
        if (this.popupMessages.size() == 1 && (num < 0 || num >= this.popupMessages.size())) {
            return null;
        }
        View view;
        if (num == -1) {
            num = this.popupMessages.size() - 1;
        } else if (num == this.popupMessages.size()) {
            num = 0;
        }
        MessageObject messageObject = (MessageObject) this.popupMessages.get(num);
        View frameLayout;
        TextView messageText;
        if (messageObject.type == 1 || messageObject.type == 4) {
            if (this.imageViews.size() > 0) {
                view = (ViewGroup) this.imageViews.get(0);
                this.imageViews.remove(0);
            } else {
                frameLayout = new FrameLayout(this);
                frameLayout = new FrameLayout(this);
                frameLayout.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f));
                frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
                BackupImageView backupImageView = new BackupImageView(this);
                backupImageView.setTag(Integer.valueOf(311));
                frameLayout.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout = new TextView(this);
                frameLayout.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                frameLayout.setTextSize(1, 16.0f);
                frameLayout.setGravity(17);
                frameLayout.setTag(Integer.valueOf(312));
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -2, 17));
                frameLayout.setTag(Integer.valueOf(2));
                frameLayout.setOnClickListener(new PopupNotificationActivity$$Lambda$6(this));
            }
            messageText = (TextView) view.findViewWithTag(Integer.valueOf(312));
            BackupImageView imageView = (BackupImageView) view.findViewWithTag(Integer.valueOf(311));
            imageView.setAspectFit(true);
            if (messageObject.type == 1) {
                PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                boolean photoSet = false;
                if (currentPhotoObject != null) {
                    boolean photoExist = true;
                    if (messageObject.type == 1 && !FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        photoExist = false;
                    }
                    if (photoExist || DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                        imageView.setImage(currentPhotoObject.location, "100_100", thumb.location, currentPhotoObject.size, (Object) messageObject);
                        photoSet = true;
                    } else if (thumb != null) {
                        imageView.setImage(thumb.location, null, (Drawable) null, (Object) messageObject);
                        photoSet = true;
                    }
                }
                if (photoSet) {
                    imageView.setVisibility(0);
                    messageText.setVisibility(8);
                } else {
                    imageView.setVisibility(8);
                    messageText.setVisibility(0);
                    messageText.setTextSize(2, (float) SharedConfig.fontSize);
                    messageText.setText(messageObject.messageText);
                }
            } else if (messageObject.type == 4) {
                messageText.setVisibility(8);
                messageText.setText(messageObject.messageText);
                imageView.setVisibility(0);
                GeoPoint geoPoint = messageObject.messageOwner.media.geo;
                double lat = geoPoint.lat;
                double lon = geoPoint._long;
                if (MessagesController.getInstance(messageObject.currentAccount).mapProvider == 2) {
                    imageView.setImage(WebFile.createWithGeoPoint(geoPoint, 100, 100, 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), null, (Drawable) null, (Object) messageObject);
                } else {
                    imageView.setImage(AndroidUtilities.formapMapUrl(messageObject.currentAccount, lat, lon, 100, 100, true, 15), null, null);
                }
            }
        } else if (messageObject.type == 2) {
            PopupAudioView cell;
            if (this.audioViews.size() > 0) {
                view = (ViewGroup) this.audioViews.get(0);
                this.audioViews.remove(0);
                cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
            } else {
                frameLayout = new FrameLayout(this);
                frameLayout = new FrameLayout(this);
                frameLayout.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f));
                frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout = new FrameLayout(this);
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                PopupAudioView popupAudioView = new PopupAudioView(this);
                popupAudioView.setTag(Integer.valueOf(300));
                frameLayout.addView(popupAudioView);
                frameLayout.setTag(Integer.valueOf(3));
                frameLayout.setOnClickListener(new PopupNotificationActivity$$Lambda$7(this));
            }
            cell.setMessageObject(messageObject);
            if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                cell.downloadAudioIfNeed();
            }
        } else {
            if (this.textViews.size() > 0) {
                view = (ViewGroup) this.textViews.get(0);
                this.textViews.remove(0);
            } else {
                frameLayout = new FrameLayout(this);
                frameLayout = new ScrollView(this);
                frameLayout.setFillViewport(true);
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout = new LinearLayout(this);
                frameLayout.setOrientation(0);
                frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 1));
                frameLayout.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(10.0f));
                frameLayout.setOnClickListener(new PopupNotificationActivity$$Lambda$8(this));
                frameLayout = new TextView(this);
                frameLayout.setTextSize(1, 16.0f);
                frameLayout.setTag(Integer.valueOf(301));
                frameLayout.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                frameLayout.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                frameLayout.setGravity(17);
                frameLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 17));
                frameLayout.setTag(Integer.valueOf(1));
            }
            messageText = (TextView) view.findViewWithTag(Integer.valueOf(301));
            messageText.setTextSize(2, (float) SharedConfig.fontSize);
            messageText.setText(messageObject.messageText);
        }
        if (view.getParent() == null) {
            this.messageContainer.addView(view);
        }
        view.setVisibility(0);
        if (!applyOffset) {
            return view;
        }
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(24.0f);
        ViewGroup.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.gravity = 51;
        layoutParams.height = -1;
        layoutParams.width = widht;
        if (num == this.currentMessageNum) {
            view.setTranslationX(0.0f);
        } else if (num == this.currentMessageNum - 1) {
            view.setTranslationX((float) (-widht));
        } else if (num == this.currentMessageNum + 1) {
            view.setTranslationX((float) widht);
        }
        view.setLayoutParams(layoutParams);
        view.invalidate();
        return view;
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
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(24.0f);
        float offset;
        ViewGroup viewForMessage;
        LayoutParams layoutParams;
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
                viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = viewForMessage;
                if (viewForMessage != null) {
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
                viewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = viewForMessage;
                if (viewForMessage != null) {
                    this.rightButtonsView.setTranslationX(offset);
                }
            }
        } else if (move == 4) {
            if (this.leftView != null) {
                offset = this.leftView.getTranslationX();
                reuseView(this.leftView);
                viewForMessage = getViewForMessage(0, false);
                this.leftView = viewForMessage;
                if (viewForMessage != null) {
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
                viewForMessage = getButtonsViewForMessage(0, false);
                this.leftButtonsView = viewForMessage;
                if (viewForMessage != null) {
                    this.leftButtonsView.setTranslationX(offset);
                }
            }
        }
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
        }
        if (this.messageContainer != null) {
            this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
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
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.m9dp(4.0f));
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
            if (this.currentUser.f176id / id_chat_compose_panel == 777 || this.currentUser.f176id / id_chat_compose_panel == 333 || ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.f176id)) != null || (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                this.nameTextView.setText(CLASSNAMEPhoneFormat.getInstance().format("+" + this.currentUser.phone));
            }
            if (this.currentUser == null || this.currentUser.f176id != 777000) {
                CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                if (printString == null || printString.length() == 0) {
                    this.lastPrintString = null;
                    setTypingAnimation(false);
                    User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.f176id));
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
            Object parentObject = null;
            if (this.currentChat != null) {
                Chat chat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.f78id));
                if (chat != null) {
                    this.currentChat = chat;
                    parentObject = chat;
                    if (this.currentChat.photo != null) {
                        newPhoto = this.currentChat.photo.photo_small;
                    }
                    avatarDrawable = new AvatarDrawable(this.currentChat);
                } else {
                    return;
                }
            } else if (this.currentUser != null) {
                User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.f176id));
                if (user != null) {
                    this.currentUser = user;
                    User parentObject2 = user;
                    if (this.currentUser.photo != null) {
                        newPhoto = this.currentUser.photo.photo_small;
                    }
                    avatarDrawable = new AvatarDrawable(this.currentUser);
                } else {
                    return;
                }
            }
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(newPhoto, "50_50", avatarDrawable, parentObject2);
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
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.m9dp(4.0f));
                    for (a = 0; a < this.statusDrawables.length; a++) {
                        if (a == type.intValue()) {
                            this.statusDrawables[a].start();
                        } else {
                            this.statusDrawables[a].stop();
                        }
                    }
                    return;
                } catch (Throwable e) {
                    FileLog.m13e(e);
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
        int a;
        Integer mid;
        int count;
        View view;
        PopupAudioView cell;
        MessageObject messageObject;
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
        } else if (id == NotificationCenter.emojiDidLoad) {
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
        } else if (id == NotificationCenter.contactsDidLoad && account == this.lastResumedAccount) {
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
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.contactsDidLoad);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            if (this.chatActivityEnterView != null) {
                this.chatActivityEnterView.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}
