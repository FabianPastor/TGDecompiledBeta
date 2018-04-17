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
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.PhotoSize;
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
    class C16255 implements OnClickListener {
        C16255() {
        }

        @TargetApi(9)
        public void onClick(DialogInterface dialog, int which) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                PopupNotificationActivity.this.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$6 */
    class C16266 implements Runnable {
        C16266() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$7 */
    class C16277 implements Runnable {
        C16277() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$8 */
    class C16288 implements Runnable {
        C16288() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$9 */
    class C16299 extends AnimatorListenerAdapter {
        C16299() {
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
            if (!PopupNotificationActivity.this.checkTransitionAnimation()) {
                if (!((PopupNotificationActivity) getContext()).onTouchEventMy(ev)) {
                    return false;
                }
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent ev) {
            if (!PopupNotificationActivity.this.checkTransitionAnimation()) {
                if (!((PopupNotificationActivity) getContext()).onTouchEventMy(ev)) {
                    return false;
                }
            }
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$3 */
    class C22393 implements ChatActivityEnterViewDelegate {
        C22393() {
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
    class C22404 extends ActionBarMenuOnItemClick {
        C22404() {
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
            NotificationCenter.getInstance(a).addObserver(r0, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(a).addObserver(r0, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(a).addObserver(r0, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).addObserver(r0, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(r0, NotificationCenter.contactsDidLoaded);
        }
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.emojiDidLoaded);
        r0.classGuid = ConnectionsManager.generateClassGuid();
        r0.statusDrawables[0] = new TypingDotsDrawable();
        r0.statusDrawables[1] = new RecordStatusDrawable();
        r0.statusDrawables[2] = new SendingFileDrawable();
        r0.statusDrawables[3] = new PlayingGameDrawable();
        r0.statusDrawables[4] = new RoundStatusDrawable();
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(r0) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthMode = MeasureSpec.getMode(widthMeasureSpec);
                int heightMode = MeasureSpec.getMode(heightMeasureSpec);
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    heightSize -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int heightSize2 = heightSize;
                int childCount = getChildCount();
                heightSize = 0;
                while (true) {
                    int i = heightSize;
                    if (i < childCount) {
                        View child = getChildAt(i);
                        if (child.getVisibility() != 8) {
                            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                            } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            } else {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + heightSize2), NUM));
                            }
                        }
                        heightSize = i + 1;
                    } else {
                        return;
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                C22381 c22381 = this;
                int count = getChildCount();
                int i = 0;
                int paddingBottom = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                while (true) {
                    int i2 = i;
                    if (i2 < count) {
                        View child = getChildAt(i2);
                        if (child.getVisibility() != 8) {
                            int childLeft;
                            int childLeft2;
                            int childTop;
                            int childLeft3;
                            LayoutParams lp = (LayoutParams) child.getLayoutParams();
                            int width = child.getMeasuredWidth();
                            int height = child.getMeasuredHeight();
                            int gravity = lp.gravity;
                            if (gravity == -1) {
                                gravity = 51;
                            }
                            int verticalGravity = gravity & 112;
                            int i3 = (gravity & 7) & 7;
                            if (i3 != 1) {
                                if (i3 != 5) {
                                    i3 = lp.leftMargin;
                                } else {
                                    i3 = (r - width) - lp.rightMargin;
                                }
                                childLeft = i3;
                            } else {
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            }
                            if (verticalGravity == 16) {
                                childLeft2 = childLeft;
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            } else if (verticalGravity == 48) {
                                childLeft2 = childLeft;
                                childTop = lp.topMargin;
                            } else if (verticalGravity != 80) {
                                childTop = lp.topMargin;
                                childLeft2 = childLeft;
                            } else {
                                childLeft2 = childLeft;
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            }
                            childLeft = childTop;
                            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                                childLeft = paddingBottom != 0 ? getMeasuredHeight() - paddingBottom : getMeasuredHeight();
                            } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                                childLeft = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - child.getMeasuredHeight()) - lp.bottomMargin;
                                childLeft3 = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - child.getMeasuredWidth()) - lp.rightMargin;
                                child.layout(childLeft3, childLeft, childLeft3 + width, childLeft + height);
                            }
                            childLeft3 = childLeft2;
                            child.layout(childLeft3, childLeft, childLeft3 + width, childLeft + height);
                        }
                        i = i2 + 1;
                    } else {
                        notifyHeightChanged();
                        return;
                    }
                }
            }
        };
        setContentView(contentView);
        contentView.setBackgroundColor(-NUM);
        RelativeLayout relativeLayout = new RelativeLayout(r0);
        contentView.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        r0.popupContainer = new RelativeLayout(r0) {
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
        r0.popupContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        relativeLayout.addView(r0.popupContainer, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        if (r0.chatActivityEnterView != null) {
            r0.chatActivityEnterView.onDestroy();
        }
        r0.chatActivityEnterView = new ChatActivityEnterView(r0, contentView, null, false);
        r0.chatActivityEnterView.setId(id_chat_compose_panel);
        r0.popupContainer.addView(r0.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        r0.chatActivityEnterView.setDelegate(new C22393());
        r0.messageContainer = new FrameLayoutTouch(r0);
        r0.popupContainer.addView(r0.messageContainer, 0);
        r0.actionBar = new ActionBar(r0);
        r0.actionBar.setOccupyStatusBar(false);
        r0.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        r0.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        r0.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        r0.popupContainer.addView(r0.actionBar);
        ViewGroup.LayoutParams layoutParams = r0.actionBar.getLayoutParams();
        layoutParams.width = -1;
        r0.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem view = r0.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        r0.countText = new TextView(r0);
        r0.countText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        r0.countText.setTextSize(1, 14.0f);
        r0.countText.setGravity(17);
        view.addView(r0.countText, LayoutHelper.createFrame(56, -1.0f));
        r0.avatarContainer = new FrameLayout(r0);
        r0.avatarContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        r0.actionBar.addView(r0.avatarContainer);
        LayoutParams layoutParams2 = (LayoutParams) r0.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        r0.avatarContainer.setLayoutParams(layoutParams2);
        r0.avatarImageView = new BackupImageView(r0);
        r0.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        r0.avatarContainer.addView(r0.avatarImageView);
        layoutParams2 = (LayoutParams) r0.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(42.0f);
        layoutParams2.height = AndroidUtilities.dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(3.0f);
        r0.avatarImageView.setLayoutParams(layoutParams2);
        r0.nameTextView = new TextView(r0);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        r0.nameTextView.setTextSize(1, 18.0f);
        r0.nameTextView.setLines(1);
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setGravity(3);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.avatarContainer.addView(r0.nameTextView);
        layoutParams2 = (LayoutParams) r0.nameTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams2.gravity = 80;
        r0.nameTextView.setLayoutParams(layoutParams2);
        r0.onlineTextView = new TextView(r0);
        r0.onlineTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        r0.onlineTextView.setTextSize(1, 14.0f);
        r0.onlineTextView.setLines(1);
        r0.onlineTextView.setMaxLines(1);
        r0.onlineTextView.setSingleLine(true);
        r0.onlineTextView.setEllipsize(TruncateAt.END);
        r0.onlineTextView.setGravity(3);
        r0.avatarContainer.addView(r0.onlineTextView);
        LayoutParams layoutParams22 = (LayoutParams) r0.onlineTextView.getLayoutParams();
        layoutParams22.width = -2;
        layoutParams22.height = -2;
        layoutParams22.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams22.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams22.gravity = 80;
        r0.onlineTextView.setLayoutParams(layoutParams22);
        r0.actionBar.setActionBarMenuOnItemClick(new C22404());
        r0.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        r0.wakeLock.setReferenceCounted(false);
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new C16255());
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
        PopupNotificationActivity popupNotificationActivity = this;
        MotionEvent motionEvent2 = motionEvent;
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent2 != null && motionEvent.getAction() == 0) {
            popupNotificationActivity.moveStartX = motionEvent.getX();
        } else if (motionEvent2 != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            diff = (int) (x - popupNotificationActivity.moveStartX);
            if (!(popupNotificationActivity.moveStartX == -1.0f || popupNotificationActivity.startedMoving || Math.abs(diff) <= AndroidUtilities.dp(10.0f))) {
                popupNotificationActivity.startedMoving = true;
                popupNotificationActivity.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                diff = 0;
                if (popupNotificationActivity.velocityTracker == null) {
                    popupNotificationActivity.velocityTracker = VelocityTracker.obtain();
                } else {
                    popupNotificationActivity.velocityTracker.clear();
                }
            }
            if (popupNotificationActivity.startedMoving) {
                if (popupNotificationActivity.leftView == null && diff > 0) {
                    diff = 0;
                }
                if (popupNotificationActivity.rightView == null && diff < 0) {
                    diff = 0;
                }
                if (popupNotificationActivity.velocityTracker != null) {
                    popupNotificationActivity.velocityTracker.addMovement(motionEvent2);
                }
                applyViewsLayoutParams(diff);
            }
        } else if (motionEvent2 == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent2 == null || !popupNotificationActivity.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                int diff = (int) (motionEvent.getX() - popupNotificationActivity.moveStartX);
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                float moveDiff = 0.0f;
                int forceMove = 0;
                View otherView = null;
                View otherButtonsView = null;
                if (popupNotificationActivity.velocityTracker != null) {
                    popupNotificationActivity.velocityTracker.computeCurrentVelocity(id_chat_compose_panel);
                    if (popupNotificationActivity.velocityTracker.getXVelocity() >= 3500.0f) {
                        forceMove = 1;
                    } else if (popupNotificationActivity.velocityTracker.getXVelocity() <= -3500.0f) {
                        forceMove = 2;
                    }
                }
                if ((forceMove == 1 || diff > width / 3) && popupNotificationActivity.leftView != null) {
                    moveDiff = ((float) width) - popupNotificationActivity.centerView.getTranslationX();
                    otherView = popupNotificationActivity.leftView;
                    otherButtonsView = popupNotificationActivity.leftButtonsView;
                    popupNotificationActivity.onAnimationEndRunnable = new C16266();
                } else if ((forceMove == 2 || diff < (-width) / 3) && popupNotificationActivity.rightView != null) {
                    moveDiff = ((float) (-width)) - popupNotificationActivity.centerView.getTranslationX();
                    otherView = popupNotificationActivity.rightView;
                    otherButtonsView = popupNotificationActivity.rightButtonsView;
                    popupNotificationActivity.onAnimationEndRunnable = new C16277();
                } else if (popupNotificationActivity.centerView.getTranslationX() != 0.0f) {
                    moveDiff = -popupNotificationActivity.centerView.getTranslationX();
                    otherView = diff > 0 ? popupNotificationActivity.leftView : popupNotificationActivity.rightView;
                    otherButtonsView = diff > 0 ? popupNotificationActivity.leftButtonsView : popupNotificationActivity.rightButtonsView;
                    popupNotificationActivity.onAnimationEndRunnable = new C16288();
                }
                if (moveDiff != 0.0f) {
                    diff = (int) (Math.abs(moveDiff / ((float) width)) * NUM);
                    ArrayList<Animator> animators = new ArrayList();
                    animators.add(ObjectAnimator.ofFloat(popupNotificationActivity.centerView, "translationX", new float[]{popupNotificationActivity.centerView.getTranslationX() + moveDiff}));
                    if (popupNotificationActivity.centerButtonsView != null) {
                        animators.add(ObjectAnimator.ofFloat(popupNotificationActivity.centerButtonsView, "translationX", new float[]{popupNotificationActivity.centerButtonsView.getTranslationX() + moveDiff}));
                    }
                    if (otherView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherView, "translationX", new float[]{otherView.getTranslationX() + moveDiff}));
                    }
                    if (otherButtonsView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherButtonsView, "translationX", new float[]{otherButtonsView.getTranslationX() + moveDiff}));
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration((long) diff);
                    animatorSet.addListener(new C16299());
                    animatorSet.start();
                    popupNotificationActivity.animationInProgress = true;
                    popupNotificationActivity.animationStartTime = System.currentTimeMillis();
                }
            }
            if (popupNotificationActivity.velocityTracker != null) {
                popupNotificationActivity.velocityTracker.recycle();
                popupNotificationActivity.velocityTracker = null;
            }
            popupNotificationActivity.startedMoving = false;
            popupNotificationActivity.moveStartX = -1.0f;
        }
        return popupNotificationActivity.startedMoving;
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
        int num2 = num;
        if (this.popupMessages.size() == 1 && (num2 < 0 || num2 >= r0.popupMessages.size())) {
            return null;
        }
        LinearLayout view;
        final MessageObject messageObject;
        int buttonsCount;
        ReplyMarkup markup;
        ArrayList<TL_keyboardButtonRow> rows;
        int size;
        int a;
        TL_keyboardButtonRow row;
        int size2;
        int b;
        final int account;
        ArrayList<TL_keyboardButtonRow> rows2;
        int size3;
        TL_keyboardButtonRow row2;
        int size22;
        KeyboardButton button;
        ReplyMarkup markup2;
        int widht;
        if (num2 == -1) {
            num2 = r0.popupMessages.size() - 1;
        } else {
            if (num2 == r0.popupMessages.size()) {
                num2 = 0;
            }
            view = null;
            messageObject = (MessageObject) r0.popupMessages.get(num2);
            buttonsCount = 0;
            markup = messageObject.messageOwner.reply_markup;
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
            account = messageObject.currentAccount;
            if (buttonsCount > 0) {
                rows2 = markup.rows;
                size3 = rows2.size();
                for (size = 0; size < size3; size++) {
                    row2 = (TL_keyboardButtonRow) rows2.get(size);
                    size2 = 0;
                    size22 = row2.buttons.size();
                    while (size2 < size22) {
                        button = (KeyboardButton) row2.buttons.get(size2);
                        if (button instanceof TL_keyboardButtonCallback) {
                            markup2 = markup;
                        } else {
                            if (view == null) {
                                view = new LinearLayout(r0);
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
                            TextView textView = new TextView(r0);
                            markup2 = markup;
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
                        size2++;
                        markup = markup2;
                    }
                }
            }
            if (view != null) {
                widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                layoutParams.addRule(12);
                if (applyOffset) {
                    if (num2 == r0.currentMessageNum) {
                        view.setTranslationX(0.0f);
                    } else if (num2 == r0.currentMessageNum - 1) {
                        view.setTranslationX((float) (-widht));
                    } else if (num2 == r0.currentMessageNum + 1) {
                        view.setTranslationX((float) widht);
                    }
                }
                r0.popupContainer.addView(view, layoutParams);
            }
            return view;
        }
        view = null;
        messageObject = (MessageObject) r0.popupMessages.get(num2);
        buttonsCount = 0;
        markup = messageObject.messageOwner.reply_markup;
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
        account = messageObject.currentAccount;
        if (buttonsCount > 0) {
            rows2 = markup.rows;
            size3 = rows2.size();
            for (size = 0; size < size3; size++) {
                row2 = (TL_keyboardButtonRow) rows2.get(size);
                size2 = 0;
                size22 = row2.buttons.size();
                while (size2 < size22) {
                    button = (KeyboardButton) row2.buttons.get(size2);
                    if (button instanceof TL_keyboardButtonCallback) {
                        markup2 = markup;
                    } else {
                        if (view == null) {
                            view = new LinearLayout(r0);
                            view.setOrientation(0);
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            view.setWeightSum(100.0f);
                            view.setTag("b");
                            view.setOnTouchListener(/* anonymous class already generated */);
                        }
                        TextView textView2 = new TextView(r0);
                        markup2 = markup;
                        textView2.setTextSize(1, 16.0f);
                        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView2.setText(button.text.toUpperCase());
                        textView2.setTag(button);
                        textView2.setGravity(17);
                        textView2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        view.addView(textView2, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) buttonsCount)));
                        textView2.setOnClickListener(/* anonymous class already generated */);
                    }
                    size2++;
                    markup = markup2;
                }
            }
        }
        if (view != null) {
            widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams2.addRule(12);
            if (applyOffset) {
                if (num2 == r0.currentMessageNum) {
                    view.setTranslationX(0.0f);
                } else if (num2 == r0.currentMessageNum - 1) {
                    view.setTranslationX((float) (-widht));
                } else if (num2 == r0.currentMessageNum + 1) {
                    view.setTranslationX((float) widht);
                }
            }
            r0.popupContainer.addView(view, layoutParams2);
        }
        return view;
    }

    private ViewGroup getViewForMessage(int num, boolean applyOffset) {
        int num2 = num;
        if (this.popupMessages.size() == 1 && (num2 < 0 || num2 >= r0.popupMessages.size())) {
            return null;
        }
        MessageObject messageObject;
        ViewGroup viewGroup;
        PopupAudioView cell;
        int widht;
        LayoutParams layoutParams;
        ViewGroup view;
        BackupImageView backupImageView;
        TextView messageText;
        PhotoSize currentPhotoObject;
        PhotoSize thumb;
        boolean photoSet;
        boolean photoExist;
        MessageObject messageObject2;
        if (num2 == -1) {
            num2 = r0.popupMessages.size() - 1;
        } else {
            if (num2 == r0.popupMessages.size()) {
                num2 = 0;
            }
            messageObject = (MessageObject) r0.popupMessages.get(num2);
            if (messageObject.type != 1) {
                if (messageObject.type == 4) {
                    if (messageObject.type != 2) {
                        if (r0.audioViews.size() <= 0) {
                            viewGroup = (ViewGroup) r0.audioViews.get(0);
                            r0.audioViews.remove(0);
                            cell = (PopupAudioView) viewGroup.findViewWithTag(Integer.valueOf(300));
                        } else {
                            viewGroup = new FrameLayout(r0);
                            FrameLayout frameLayout = new FrameLayout(r0);
                            frameLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                            frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            viewGroup.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
                            FrameLayout frameLayout1 = new FrameLayout(r0);
                            frameLayout.addView(frameLayout1, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                            PopupAudioView cell2 = new PopupAudioView(r0);
                            cell2.setTag(Integer.valueOf(300));
                            frameLayout1.addView(cell2);
                            viewGroup.setTag(Integer.valueOf(3));
                            viewGroup.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    PopupNotificationActivity.this.openCurrentMessage();
                                }
                            });
                            cell = cell2;
                        }
                        cell.setMessageObject(messageObject);
                        if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                            cell.downloadAudioIfNeed();
                        }
                    } else {
                        if (r0.textViews.size() <= 0) {
                            viewGroup = (ViewGroup) r0.textViews.get(0);
                            r0.textViews.remove(0);
                        } else {
                            viewGroup = new FrameLayout(r0);
                            ScrollView scrollView = new ScrollView(r0);
                            scrollView.setFillViewport(true);
                            viewGroup.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
                            LinearLayout linearLayout = new LinearLayout(r0);
                            linearLayout.setOrientation(0);
                            linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 1));
                            linearLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    PopupNotificationActivity.this.openCurrentMessage();
                                }
                            });
                            TextView textView = new TextView(r0);
                            textView.setTextSize(1, 16.0f);
                            textView.setTag(Integer.valueOf(301));
                            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            textView.setGravity(17);
                            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17));
                            viewGroup.setTag(Integer.valueOf(1));
                        }
                        TextView messageText2 = (TextView) viewGroup.findViewWithTag(Integer.valueOf(301));
                        messageText2.setTextSize(2, (float) SharedConfig.fontSize);
                        messageText2.setText(messageObject.messageText);
                    }
                    if (viewGroup.getParent() == null) {
                        r0.messageContainer.addView(viewGroup);
                    }
                    viewGroup.setVisibility(0);
                    if (applyOffset) {
                        widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                        layoutParams = (LayoutParams) viewGroup.getLayoutParams();
                        layoutParams.gravity = 51;
                        layoutParams.height = -1;
                        layoutParams.width = widht;
                        if (num2 != r0.currentMessageNum) {
                            viewGroup.setTranslationX(0.0f);
                        } else if (num2 != r0.currentMessageNum - 1) {
                            viewGroup.setTranslationX((float) (-widht));
                        } else if (num2 == r0.currentMessageNum + 1) {
                            viewGroup.setTranslationX((float) widht);
                        }
                        viewGroup.setLayoutParams(layoutParams);
                        viewGroup.invalidate();
                    }
                    return viewGroup;
                }
            }
            if (r0.imageViews.size() <= 0) {
                view = (ViewGroup) r0.imageViews.get(0);
                r0.imageViews.remove(0);
            } else {
                view = new FrameLayout(r0);
                FrameLayout frameLayout2 = new FrameLayout(r0);
                frameLayout2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                frameLayout2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                view.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
                backupImageView = new BackupImageView(r0);
                backupImageView.setTag(Integer.valueOf(311));
                frameLayout2.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
                TextView textView2 = new TextView(r0);
                textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                textView2.setTextSize(1, 16.0f);
                textView2.setGravity(17);
                textView2.setTag(Integer.valueOf(312));
                frameLayout2.addView(textView2, LayoutHelper.createFrame(-1, -2, 17));
                view.setTag(Integer.valueOf(2));
                view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        PopupNotificationActivity.this.openCurrentMessage();
                    }
                });
            }
            messageText = (TextView) view.findViewWithTag(Integer.valueOf(312));
            backupImageView = (BackupImageView) view.findViewWithTag(Integer.valueOf(311));
            backupImageView.setAspectFit(true);
            if (messageObject.type == 1) {
                currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                thumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                photoSet = false;
                if (currentPhotoObject != null) {
                    photoExist = true;
                    if (messageObject.type == 1 && !FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        photoExist = false;
                    }
                    if (!photoExist) {
                        if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                            if (thumb != null) {
                                backupImageView.setImage(thumb.location, null, (Drawable) null);
                                photoSet = true;
                            }
                        }
                    }
                    backupImageView.setImage(currentPhotoObject.location, "100_100", thumb.location, currentPhotoObject.size);
                    photoSet = true;
                }
                if (photoSet) {
                    backupImageView.setVisibility(8);
                    messageText.setVisibility(0);
                    messageText.setTextSize(2, (float) SharedConfig.fontSize);
                    messageText.setText(messageObject.messageText);
                } else {
                    backupImageView.setVisibility(0);
                    messageText.setVisibility(8);
                }
                messageObject2 = messageObject;
            } else if (messageObject.type != 4) {
                messageText.setVisibility(8);
                messageText.setText(messageObject.messageText);
                backupImageView.setVisibility(0);
                double lat = messageObject.messageOwner.media.geo.lat;
                double lon = messageObject.messageOwner.media.geo._long;
                r14 = new Object[5];
                messageObject2 = messageObject;
                r14[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                r14[3] = Double.valueOf(lat);
                r14[4] = Double.valueOf(lon);
                backupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", r14), null, null);
            }
            viewGroup = view;
            if (viewGroup.getParent() == null) {
                r0.messageContainer.addView(viewGroup);
            }
            viewGroup.setVisibility(0);
            if (applyOffset) {
                widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                layoutParams = (LayoutParams) viewGroup.getLayoutParams();
                layoutParams.gravity = 51;
                layoutParams.height = -1;
                layoutParams.width = widht;
                if (num2 != r0.currentMessageNum) {
                    viewGroup.setTranslationX(0.0f);
                } else if (num2 != r0.currentMessageNum - 1) {
                    viewGroup.setTranslationX((float) (-widht));
                } else if (num2 == r0.currentMessageNum + 1) {
                    viewGroup.setTranslationX((float) widht);
                }
                viewGroup.setLayoutParams(layoutParams);
                viewGroup.invalidate();
            }
            return viewGroup;
        }
        messageObject = (MessageObject) r0.popupMessages.get(num2);
        if (messageObject.type != 1) {
            if (messageObject.type == 4) {
                if (messageObject.type != 2) {
                    if (r0.textViews.size() <= 0) {
                        viewGroup = new FrameLayout(r0);
                        ScrollView scrollView2 = new ScrollView(r0);
                        scrollView2.setFillViewport(true);
                        viewGroup.addView(scrollView2, LayoutHelper.createFrame(-1, -1.0f));
                        LinearLayout linearLayout2 = new LinearLayout(r0);
                        linearLayout2.setOrientation(0);
                        linearLayout2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        scrollView2.addView(linearLayout2, LayoutHelper.createScroll(-1, -2, 1));
                        linearLayout2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                        linearLayout2.setOnClickListener(/* anonymous class already generated */);
                        TextView textView3 = new TextView(r0);
                        textView3.setTextSize(1, 16.0f);
                        textView3.setTag(Integer.valueOf(301));
                        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        textView3.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        textView3.setGravity(17);
                        linearLayout2.addView(textView3, LayoutHelper.createLinear(-1, -2, 17));
                        viewGroup.setTag(Integer.valueOf(1));
                    } else {
                        viewGroup = (ViewGroup) r0.textViews.get(0);
                        r0.textViews.remove(0);
                    }
                    TextView messageText22 = (TextView) viewGroup.findViewWithTag(Integer.valueOf(301));
                    messageText22.setTextSize(2, (float) SharedConfig.fontSize);
                    messageText22.setText(messageObject.messageText);
                } else {
                    if (r0.audioViews.size() <= 0) {
                        viewGroup = new FrameLayout(r0);
                        FrameLayout frameLayout3 = new FrameLayout(r0);
                        frameLayout3.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                        frameLayout3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        viewGroup.addView(frameLayout3, LayoutHelper.createFrame(-1, -1.0f));
                        FrameLayout frameLayout12 = new FrameLayout(r0);
                        frameLayout3.addView(frameLayout12, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                        PopupAudioView cell22 = new PopupAudioView(r0);
                        cell22.setTag(Integer.valueOf(300));
                        frameLayout12.addView(cell22);
                        viewGroup.setTag(Integer.valueOf(3));
                        viewGroup.setOnClickListener(/* anonymous class already generated */);
                        cell = cell22;
                    } else {
                        viewGroup = (ViewGroup) r0.audioViews.get(0);
                        r0.audioViews.remove(0);
                        cell = (PopupAudioView) viewGroup.findViewWithTag(Integer.valueOf(300));
                    }
                    cell.setMessageObject(messageObject);
                    if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                        cell.downloadAudioIfNeed();
                    }
                }
                if (viewGroup.getParent() == null) {
                    r0.messageContainer.addView(viewGroup);
                }
                viewGroup.setVisibility(0);
                if (applyOffset) {
                    widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                    layoutParams = (LayoutParams) viewGroup.getLayoutParams();
                    layoutParams.gravity = 51;
                    layoutParams.height = -1;
                    layoutParams.width = widht;
                    if (num2 != r0.currentMessageNum) {
                        viewGroup.setTranslationX(0.0f);
                    } else if (num2 != r0.currentMessageNum - 1) {
                        viewGroup.setTranslationX((float) (-widht));
                    } else if (num2 == r0.currentMessageNum + 1) {
                        viewGroup.setTranslationX((float) widht);
                    }
                    viewGroup.setLayoutParams(layoutParams);
                    viewGroup.invalidate();
                }
                return viewGroup;
            }
        }
        if (r0.imageViews.size() <= 0) {
            view = new FrameLayout(r0);
            FrameLayout frameLayout22 = new FrameLayout(r0);
            frameLayout22.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            frameLayout22.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            view.addView(frameLayout22, LayoutHelper.createFrame(-1, -1.0f));
            backupImageView = new BackupImageView(r0);
            backupImageView.setTag(Integer.valueOf(311));
            frameLayout22.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
            TextView textView22 = new TextView(r0);
            textView22.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView22.setTextSize(1, 16.0f);
            textView22.setGravity(17);
            textView22.setTag(Integer.valueOf(312));
            frameLayout22.addView(textView22, LayoutHelper.createFrame(-1, -2, 17));
            view.setTag(Integer.valueOf(2));
            view.setOnClickListener(/* anonymous class already generated */);
        } else {
            view = (ViewGroup) r0.imageViews.get(0);
            r0.imageViews.remove(0);
        }
        messageText = (TextView) view.findViewWithTag(Integer.valueOf(312));
        backupImageView = (BackupImageView) view.findViewWithTag(Integer.valueOf(311));
        backupImageView.setAspectFit(true);
        if (messageObject.type == 1) {
            currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            thumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
            photoSet = false;
            if (currentPhotoObject != null) {
                photoExist = true;
                photoExist = false;
                if (photoExist) {
                    if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                        if (thumb != null) {
                            backupImageView.setImage(thumb.location, null, (Drawable) null);
                            photoSet = true;
                        }
                    }
                }
                backupImageView.setImage(currentPhotoObject.location, "100_100", thumb.location, currentPhotoObject.size);
                photoSet = true;
            }
            if (photoSet) {
                backupImageView.setVisibility(0);
                messageText.setVisibility(8);
            } else {
                backupImageView.setVisibility(8);
                messageText.setVisibility(0);
                messageText.setTextSize(2, (float) SharedConfig.fontSize);
                messageText.setText(messageObject.messageText);
            }
            messageObject2 = messageObject;
        } else if (messageObject.type != 4) {
        } else {
            messageText.setVisibility(8);
            messageText.setText(messageObject.messageText);
            backupImageView.setVisibility(0);
            double lat2 = messageObject.messageOwner.media.geo.lat;
            double lon2 = messageObject.messageOwner.media.geo._long;
            r14 = new Object[5];
            messageObject2 = messageObject;
            r14[2] = Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
            r14[3] = Double.valueOf(lat2);
            r14[4] = Double.valueOf(lon2);
            backupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", r14), null, null);
        }
        viewGroup = view;
        if (viewGroup.getParent() == null) {
            r0.messageContainer.addView(viewGroup);
        }
        viewGroup.setVisibility(0);
        if (applyOffset) {
            widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            layoutParams = (LayoutParams) viewGroup.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = widht;
            if (num2 != r0.currentMessageNum) {
                viewGroup.setTranslationX(0.0f);
            } else if (num2 != r0.currentMessageNum - 1) {
                viewGroup.setTranslationX((float) (-widht));
            } else if (num2 == r0.currentMessageNum + 1) {
                viewGroup.setTranslationX((float) widht);
            }
            viewGroup.setLayoutParams(layoutParams);
            viewGroup.invalidate();
        }
        return viewGroup;
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
                ViewGroup viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = viewForMessage;
                if (viewForMessage != null) {
                    LayoutParams layoutParams = (LayoutParams) this.rightView.getLayoutParams();
                    layoutParams.width = widht;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.setTranslationX(offset);
                    this.rightView.invalidate();
                }
            }
            if (this.rightButtonsView != null) {
                offset = this.rightButtonsView.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                r2 = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = r2;
                if (r2 != null) {
                    this.rightButtonsView.setTranslationX(offset);
                }
            }
        } else if (move == 4) {
            if (this.leftView != null) {
                offset = this.leftView.getTranslationX();
                reuseView(this.leftView);
                r2 = getViewForMessage(0, false);
                this.leftView = r2;
                if (r2 != null) {
                    LayoutParams layoutParams2 = (LayoutParams) this.leftView.getLayoutParams();
                    layoutParams2.width = widht;
                    this.leftView.setLayoutParams(layoutParams2);
                    this.leftView.setTranslationX(offset);
                    this.leftView.invalidate();
                }
            }
            if (this.leftButtonsView != null) {
                offset = this.leftButtonsView.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                r2 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = r2;
                if (r2 != null) {
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
        boolean z = intent != null && intent.getBooleanExtra("force", false);
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
        if (!((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            if (ApplicationLoader.isScreenOn) {
                getWindow().addFlags(2623488);
                getWindow().clearFlags(2);
                if (this.currentMessageObject == null) {
                    this.currentMessageNum = 0;
                }
                getNewMessage();
            }
        }
        getWindow().addFlags(2623490);
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.tmessages.openchat");
            stringBuilder.append(Math.random());
            stringBuilder.append(ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setAction(stringBuilder.toString());
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
        if (this.actionBar != null) {
            if (this.currentMessageObject != null) {
                if (this.currentChat == null) {
                    if (this.currentUser != null) {
                        if (this.currentUser.id / id_chat_compose_panel == 777 || this.currentUser.id / id_chat_compose_panel == 333 || ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) != null || (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
                            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                        } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                        } else {
                            TextView textView = this.nameTextView;
                            PhoneFormat instance = PhoneFormat.getInstance();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("+");
                            stringBuilder.append(this.currentUser.phone);
                            textView.setText(instance.format(stringBuilder.toString()));
                        }
                        if (this.currentUser == null || this.currentUser.id != 777000) {
                            CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                            if (printString != null) {
                                if (printString.length() != 0) {
                                    this.lastPrintString = printString;
                                    this.onlineTextView.setText(printString);
                                    setTypingAnimation(true);
                                }
                            }
                            this.lastPrintString = null;
                            setTypingAnimation(false);
                            User user = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                            if (user != null) {
                                this.currentUser = user;
                            }
                            this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
                        } else {
                            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications));
                        }
                    }
                }
            }
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
            int a = 0;
            if (start) {
                try {
                    Integer type = (Integer) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[type.intValue()], null, null, null);
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    while (a < this.statusDrawables.length) {
                        if (a == type.intValue()) {
                            this.statusDrawables[a].start();
                        } else {
                            this.statusDrawables[a].stop();
                        }
                        a++;
                    }
                } catch (Throwable a2) {
                    FileLog.m3e(a2);
                }
            } else {
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                this.onlineTextView.setCompoundDrawablePadding(0);
                while (a < this.statusDrawables.length) {
                    this.statusDrawables[a].stop();
                    a++;
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
        if (id != NotificationCenter.appDidLogout) {
            int a = 0;
            int a2;
            if (id == NotificationCenter.pushMessagesUpdated) {
                if (!this.isReply) {
                    this.popupMessages.clear();
                    while (true) {
                        a2 = a;
                        if (a2 >= 3) {
                            break;
                        }
                        if (UserConfig.getInstance(a2).isClientActivated()) {
                            this.popupMessages.addAll(NotificationsController.getInstance(a2).popupMessages);
                        }
                        a = a2 + 1;
                    }
                    getNewMessage();
                }
            } else if (id == NotificationCenter.updateInterfaces) {
                if (this.currentMessageObject != null) {
                    if (account == this.lastResumedAccount) {
                        a2 = ((Integer) args[0]).intValue();
                        if (!((a2 & 1) == 0 && (a2 & 4) == 0 && (a2 & 16) == 0 && (a2 & 32) == 0)) {
                            updateSubtitle();
                        }
                        if (!((a2 & 2) == 0 && (a2 & 8) == 0)) {
                            checkAndUpdateAvatar();
                        }
                        if ((a2 & 64) != 0) {
                            CharSequence printString = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                            if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || !(this.lastPrintString == null || printString == null || this.lastPrintString.equals(printString)))) {
                                updateSubtitle();
                            }
                        }
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidReset) {
                mid = args[0];
                if (this.messageContainer != null) {
                    count = this.messageContainer.getChildCount();
                    while (a < count) {
                        view = this.messageContainer.getChildAt(a);
                        if (((Integer) view.getTag()).intValue() == 3) {
                            cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                            messageObject = cell.getMessageObject();
                            if (messageObject != null && messageObject.currentAccount == account && messageObject.getId() == mid.intValue()) {
                                cell.updateButtonState();
                                break;
                            }
                        }
                        a++;
                    }
                }
            } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
                mid = (Integer) args[0];
                if (this.messageContainer != null) {
                    count = this.messageContainer.getChildCount();
                    while (a < count) {
                        view = this.messageContainer.getChildAt(a);
                        if (((Integer) view.getTag()).intValue() == 3) {
                            cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                            messageObject = cell.getMessageObject();
                            if (messageObject != null && messageObject.currentAccount == account && messageObject.getId() == mid.intValue()) {
                                cell.updateProgress();
                                break;
                            }
                        }
                        a++;
                    }
                }
            } else if (id == NotificationCenter.emojiDidLoaded) {
                if (this.messageContainer != null) {
                    a2 = this.messageContainer.getChildCount();
                    while (true) {
                        int a3 = a;
                        if (a3 >= a2) {
                            break;
                        }
                        View view = this.messageContainer.getChildAt(a3);
                        if (((Integer) view.getTag()).intValue() == 1) {
                            TextView textView = (TextView) view.findViewWithTag(Integer.valueOf(301));
                            if (textView != null) {
                                textView.invalidate();
                            }
                        }
                        a = a3 + 1;
                    }
                }
            } else if (id == NotificationCenter.contactsDidLoaded && account == this.lastResumedAccount) {
                updateSubtitle();
            }
        } else if (account == this.lastResumedAccount) {
            onFinish();
            finish();
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
