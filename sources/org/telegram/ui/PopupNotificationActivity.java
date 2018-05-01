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
import java.util.Collection;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
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
        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                i = new StringBuilder();
                i.append("package:");
                i.append(ApplicationLoader.applicationContext.getPackageName());
                dialogInterface.setData(Uri.parse(i.toString()));
                PopupNotificationActivity.this.startActivity(dialogInterface);
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

        public void onAnimationEnd(Animator animator) {
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

        public FrameLayoutTouch(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public FrameLayoutTouch(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!PopupNotificationActivity.this.checkTransitionAnimation()) {
                if (((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!PopupNotificationActivity.this.checkTransitionAnimation()) {
                if (((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$3 */
    class C22393 implements ChatActivityEnterViewDelegate {
        public void didPressedAttachButton() {
        }

        public void needChangeVideoPreviewState(int i, float f) {
        }

        public void needShowMediaBanHint() {
        }

        public void needStartRecordAudio(int i) {
        }

        public void needStartRecordVideo(int i) {
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

        public void onWindowSizeChanged(int i) {
        }

        C22393() {
        }

        public void onMessageSend(CharSequence charSequence) {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                if (PopupNotificationActivity.this.currentMessageNum >= null && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                    PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(null, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, true);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }
        }

        public void needSendTyping() {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
            }
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity$4 */
    class C22404 extends ActionBarMenuOnItemClick {
        C22404() {
        }

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
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.createChatResources(this, false);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(identifier);
        }
        for (identifier = 0; identifier < 3; identifier++) {
            NotificationCenter.getInstance(identifier).addObserver(r0, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(identifier).addObserver(r0, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(identifier).addObserver(r0, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(identifier).addObserver(r0, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(identifier).addObserver(r0, NotificationCenter.contactsDidLoaded);
        }
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.emojiDidLoaded);
        r0.classGuid = ConnectionsManager.generateClassGuid();
        r0.statusDrawables[0] = new TypingDotsDrawable();
        r0.statusDrawables[1] = new RecordStatusDrawable();
        r0.statusDrawables[2] = new SendingFileDrawable();
        r0.statusDrawables[3] = new PlayingGameDrawable();
        r0.statusDrawables[4] = new RoundStatusDrawable();
        View c22381 = new SizeNotifierFrameLayout(r0) {
            protected void onMeasure(int i, int i2) {
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

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                z = getChildCount();
                boolean z2 = false;
                int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                while (z2 < z) {
                    View childAt = getChildAt(z2);
                    if (childAt.getVisibility() != 8) {
                        LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                        int measuredWidth = childAt.getMeasuredWidth();
                        int measuredHeight = childAt.getMeasuredHeight();
                        int i5 = layoutParams.gravity;
                        if (i5 == -1) {
                            i5 = 51;
                        }
                        int i6 = i5 & 7;
                        i5 &= 112;
                        i6 &= 7;
                        if (i6 == 1) {
                            i6 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                        } else if (i6 != 5) {
                            i6 = layoutParams.leftMargin;
                        } else {
                            i6 = (i3 - measuredWidth) - layoutParams.rightMargin;
                        }
                        i5 = i5 != 16 ? i5 != 48 ? i5 != 80 ? layoutParams.topMargin : (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin : (((((i4 - emojiPadding) - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            i5 = emojiPadding != 0 ? getMeasuredHeight() - emojiPadding : getMeasuredHeight();
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                            i5 = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - childAt.getMeasuredHeight()) - layoutParams.bottomMargin;
                            i6 = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - childAt.getMeasuredWidth()) - layoutParams.rightMargin;
                        }
                        childAt.layout(i6, i5, measuredWidth + i6, measuredHeight + i5);
                    }
                    z2++;
                }
                notifyHeightChanged();
            }
        };
        setContentView(c22381);
        c22381.setBackgroundColor(-NUM);
        View relativeLayout = new RelativeLayout(r0);
        c22381.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        r0.popupContainer = new RelativeLayout(r0) {
            protected void onMeasure(int i, int i2) {
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

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                for (z = false; z < getChildCount(); z++) {
                    i = getChildAt(z);
                    if ((i.getTag() instanceof String) != 0) {
                        i.layout(i.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(NUM), i.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        r0.popupContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        relativeLayout.addView(r0.popupContainer, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        if (r0.chatActivityEnterView != null) {
            r0.chatActivityEnterView.onDestroy();
        }
        r0.chatActivityEnterView = new ChatActivityEnterView(r0, c22381, null, false);
        r0.chatActivityEnterView.setId(id_chat_compose_panel);
        r0.popupContainer.addView(r0.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        r0.chatActivityEnterView.setDelegate(new C22393());
        r0.messageContainer = new FrameLayoutTouch(r0);
        r0.popupContainer.addView(r0.messageContainer, 0);
        r0.actionBar = new ActionBar(r0);
        r0.actionBar.setOccupyStatusBar(false);
        r0.actionBar.setBackButtonImage(C0446R.drawable.ic_close_white);
        r0.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        r0.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        r0.popupContainer.addView(r0.actionBar);
        ViewGroup.LayoutParams layoutParams = r0.actionBar.getLayoutParams();
        layoutParams.width = -1;
        r0.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem addItemWithWidth = r0.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        r0.countText = new TextView(r0);
        r0.countText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        r0.countText.setTextSize(1, 14.0f);
        r0.countText.setGravity(17);
        addItemWithWidth.addView(r0.countText, LayoutHelper.createFrame(56, -1.0f));
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
        layoutParams2 = (LayoutParams) r0.onlineTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams2.gravity = 80;
        r0.onlineTextView.setLayoutParams(layoutParams2);
        r0.actionBar.setActionBarMenuOnItemClick(new C22404());
        r0.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        r0.wakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.checkDisplaySize(this, configuration);
        fixLayout();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3 && iArr[0] != 0) {
            i = new Builder((Context) this);
            i.setTitle(LocaleController.getString("AppName", NUM));
            i.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
            i.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new C16255());
            i.setPositiveButton(LocaleController.getString("OK", NUM), null);
            i.show();
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
        int i = 0;
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            r2 = motionEvent.getX();
            int i2 = (int) (r2 - this.moveStartX);
            if (!(this.moveStartX == -1.0f || this.startedMoving || Math.abs(i2) <= AndroidUtilities.dp(10.0f))) {
                this.startedMoving = true;
                this.moveStartX = r2;
                AndroidUtilities.lockOrientation(this);
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    this.velocityTracker.clear();
                }
                i2 = 0;
            }
            if (this.startedMoving) {
                if (this.leftView == null && r4 > 0) {
                    i2 = 0;
                }
                if (this.rightView != null || i2 >= 0) {
                    i = i2;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(motionEvent);
                }
                applyViewsLayoutParams(i);
            }
        } else if (motionEvent == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent == null || !this.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                boolean z;
                View view;
                View view2;
                Collection arrayList;
                motionEvent = (int) (motionEvent.getX() - this.moveStartX);
                int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                if (this.velocityTracker != null) {
                    this.velocityTracker.computeCurrentVelocity(id_chat_compose_panel);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        z = true;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        z = true;
                    }
                    if ((!z || motionEvent > dp / 3) && this.leftView != null) {
                        motionEvent = ((float) dp) - this.centerView.getTranslationX();
                        view = this.leftView;
                        view2 = this.leftButtonsView;
                        this.onAnimationEndRunnable = new C16266();
                    } else if ((z || motionEvent < (-dp) / 3) && this.rightView != null) {
                        motionEvent = ((float) (-dp)) - this.centerView.getTranslationX();
                        view = this.rightView;
                        view2 = this.rightButtonsView;
                        this.onAnimationEndRunnable = new C16277();
                    } else if (this.centerView.getTranslationX() != 0.0f) {
                        r2 = -this.centerView.getTranslationX();
                        view2 = motionEvent > null ? this.leftView : this.rightView;
                        motionEvent = motionEvent > null ? this.leftButtonsView : this.rightButtonsView;
                        this.onAnimationEndRunnable = new C16288();
                        View view3 = view2;
                        view2 = motionEvent;
                        motionEvent = r2;
                        view = view3;
                    } else {
                        view = null;
                        view2 = view;
                        motionEvent = null;
                    }
                    if (motionEvent != null) {
                        dp = (int) (Math.abs(motionEvent / ((float) dp)) * 200.0f);
                        arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(this.centerView, "translationX", new float[]{this.centerView.getTranslationX() + motionEvent}));
                        if (this.centerButtonsView != null) {
                            arrayList.add(ObjectAnimator.ofFloat(this.centerButtonsView, "translationX", new float[]{this.centerButtonsView.getTranslationX() + motionEvent}));
                        }
                        if (view != null) {
                            arrayList.add(ObjectAnimator.ofFloat(view, "translationX", new float[]{view.getTranslationX() + motionEvent}));
                        }
                        if (view2 != null) {
                            arrayList.add(ObjectAnimator.ofFloat(view2, "translationX", new float[]{view2.getTranslationX() + motionEvent}));
                        }
                        motionEvent = new AnimatorSet();
                        motionEvent.playTogether(arrayList);
                        motionEvent.setDuration((long) dp);
                        motionEvent.addListener(new C16299());
                        motionEvent.start();
                        this.animationInProgress = true;
                        this.animationStartTime = System.currentTimeMillis();
                    }
                }
                z = false;
                if (!z) {
                }
                motionEvent = ((float) dp) - this.centerView.getTranslationX();
                view = this.leftView;
                view2 = this.leftButtonsView;
                this.onAnimationEndRunnable = new C16266();
                if (motionEvent != null) {
                    dp = (int) (Math.abs(motionEvent / ((float) dp)) * 200.0f);
                    arrayList = new ArrayList();
                    arrayList.add(ObjectAnimator.ofFloat(this.centerView, "translationX", new float[]{this.centerView.getTranslationX() + motionEvent}));
                    if (this.centerButtonsView != null) {
                        arrayList.add(ObjectAnimator.ofFloat(this.centerButtonsView, "translationX", new float[]{this.centerButtonsView.getTranslationX() + motionEvent}));
                    }
                    if (view != null) {
                        arrayList.add(ObjectAnimator.ofFloat(view, "translationX", new float[]{view.getTranslationX() + motionEvent}));
                    }
                    if (view2 != null) {
                        arrayList.add(ObjectAnimator.ofFloat(view2, "translationX", new float[]{view2.getTranslationX() + motionEvent}));
                    }
                    motionEvent = new AnimatorSet();
                    motionEvent.playTogether(arrayList);
                    motionEvent.setDuration((long) dp);
                    motionEvent.addListener(new C16299());
                    motionEvent.start();
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

    private void applyViewsLayoutParams(int i) {
        LayoutParams layoutParams;
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (this.leftView != null) {
            layoutParams = (LayoutParams) this.leftView.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((float) ((-dp) + i));
        }
        if (this.leftButtonsView != null) {
            this.leftButtonsView.setTranslationX((float) ((-dp) + i));
        }
        if (this.centerView != null) {
            layoutParams = (LayoutParams) this.centerView.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.centerView.setLayoutParams(layoutParams);
            }
            this.centerView.setTranslationX((float) i);
        }
        if (this.centerButtonsView != null) {
            this.centerButtonsView.setTranslationX((float) i);
        }
        if (this.rightView != null) {
            layoutParams = (LayoutParams) this.rightView.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.rightView.setLayoutParams(layoutParams);
            }
            this.rightView.setTranslationX((float) (dp + i));
        }
        if (this.rightButtonsView != null) {
            this.rightButtonsView.setTranslationX((float) (dp + i));
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int i, boolean z) {
        int i2 = i;
        View view = null;
        if (this.popupMessages.size() == 1 && (i2 < 0 || i2 >= r0.popupMessages.size())) {
            return null;
        }
        int i3;
        int size;
        int i4;
        TL_keyboardButtonRow tL_keyboardButtonRow;
        int size2;
        int i5 = 0;
        if (i2 == -1) {
            i2 = r0.popupMessages.size() - 1;
        } else if (i2 == r0.popupMessages.size()) {
            i2 = 0;
        }
        final MessageObject messageObject = (MessageObject) r0.popupMessages.get(i2);
        ReplyMarkup replyMarkup = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() != 777000 || replyMarkup == null) {
            i3 = 0;
        } else {
            ArrayList arrayList = replyMarkup.rows;
            size = arrayList.size();
            i4 = 0;
            i3 = i4;
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
        final int i7 = messageObject.currentAccount;
        if (i3 > 0) {
            ArrayList arrayList2 = replyMarkup.rows;
            size = arrayList2.size();
            View view2 = null;
            int i8 = 0;
            while (i8 < size) {
                tL_keyboardButtonRow = (TL_keyboardButtonRow) arrayList2.get(i8);
                size2 = tL_keyboardButtonRow.buttons.size();
                View view3 = view2;
                i4 = i5;
                while (i4 < size2) {
                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i4);
                    if (keyboardButton instanceof TL_keyboardButtonCallback) {
                        if (view3 == null) {
                            view3 = new LinearLayout(r0);
                            view3.setOrientation(i5);
                            view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            view3.setWeightSum(100.0f);
                            view3.setTag("b");
                            view3.setOnTouchListener(new OnTouchListener() {
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });
                        }
                        View textView = new TextView(r0);
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(keyboardButton.text.toUpperCase());
                        textView.setTag(keyboardButton);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        view3.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / ((float) i3)));
                        textView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                KeyboardButton keyboardButton = (KeyboardButton) view.getTag();
                                if (keyboardButton != null) {
                                    SendMessagesHelper.getInstance(i7).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), keyboardButton.data);
                                }
                            }
                        });
                    }
                    i4++;
                    i5 = 0;
                }
                i8++;
                view2 = view3;
                i5 = 0;
            }
            view = view2;
        }
        if (view != null) {
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (z) {
                if (i2 == r0.currentMessageNum) {
                    view.setTranslationX(0.0f);
                } else if (i2 == r0.currentMessageNum - 1) {
                    view.setTranslationX((float) (-dp));
                } else if (i2 == r0.currentMessageNum + 1) {
                    view.setTranslationX((float) dp);
                }
            }
            r0.popupContainer.addView(view, layoutParams);
        }
        return view;
    }

    private ViewGroup getViewForMessage(int i, boolean z) {
        int i2 = i;
        if (this.popupMessages.size() == 1 && (i2 < 0 || i2 >= r0.popupMessages.size())) {
            return null;
        }
        View view;
        View textView;
        TextView textView2;
        int dp;
        if (i2 == -1) {
            i2 = r0.popupMessages.size() - 1;
        } else if (i2 == r0.popupMessages.size()) {
            i2 = 0;
        }
        MessageObject messageObject = (MessageObject) r0.popupMessages.get(i2);
        if (messageObject.type != 1) {
            if (messageObject.type != 4) {
                if (messageObject.type == 2) {
                    PopupAudioView popupAudioView;
                    if (r0.audioViews.size() > 0) {
                        view = (ViewGroup) r0.audioViews.get(0);
                        r0.audioViews.remove(0);
                        popupAudioView = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                    } else {
                        view = new FrameLayout(r0);
                        View frameLayout = new FrameLayout(r0);
                        frameLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        view.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
                        View frameLayout2 = new FrameLayout(r0);
                        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                        frameLayout = new PopupAudioView(r0);
                        frameLayout.setTag(Integer.valueOf(300));
                        frameLayout2.addView(frameLayout);
                        view.setTag(Integer.valueOf(3));
                        view.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                PopupNotificationActivity.this.openCurrentMessage();
                            }
                        });
                        popupAudioView = frameLayout;
                    }
                    popupAudioView.setMessageObject(messageObject);
                    if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                        popupAudioView.downloadAudioIfNeed();
                    }
                } else {
                    if (r0.textViews.size() > 0) {
                        view = (ViewGroup) r0.textViews.get(0);
                        r0.textViews.remove(0);
                    } else {
                        view = new FrameLayout(r0);
                        View scrollView = new ScrollView(r0);
                        scrollView.setFillViewport(true);
                        view.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
                        View linearLayout = new LinearLayout(r0);
                        linearLayout.setOrientation(0);
                        linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 1));
                        linearLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                PopupNotificationActivity.this.openCurrentMessage();
                            }
                        });
                        textView = new TextView(r0);
                        textView.setTextSize(1, 16.0f);
                        textView.setTag(Integer.valueOf(301));
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        textView.setGravity(17);
                        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17));
                        view.setTag(Integer.valueOf(1));
                    }
                    textView2 = (TextView) view.findViewWithTag(Integer.valueOf(301));
                    textView2.setTextSize(2, (float) SharedConfig.fontSize);
                    textView2.setText(messageObject.messageText);
                }
                if (view.getParent() == null) {
                    r0.messageContainer.addView(view);
                }
                view.setVisibility(0);
                if (z) {
                    dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.gravity = 51;
                    layoutParams.height = -1;
                    layoutParams.width = dp;
                    if (i2 == r0.currentMessageNum) {
                        view.setTranslationX(0.0f);
                    } else if (i2 == r0.currentMessageNum - 1) {
                        view.setTranslationX((float) (-dp));
                    } else if (i2 == r0.currentMessageNum + 1) {
                        view.setTranslationX((float) dp);
                    }
                    view.setLayoutParams(layoutParams);
                    view.invalidate();
                }
                return view;
            }
        }
        if (r0.imageViews.size() > 0) {
            textView = (ViewGroup) r0.imageViews.get(0);
            r0.imageViews.remove(0);
        } else {
            textView = new FrameLayout(r0);
            scrollView = new FrameLayout(r0);
            scrollView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            scrollView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
            view = new BackupImageView(r0);
            view.setTag(Integer.valueOf(311));
            scrollView.addView(view, LayoutHelper.createFrame(-1, -1.0f));
            view = new TextView(r0);
            view.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            view.setTextSize(1, 16.0f);
            view.setGravity(17);
            view.setTag(Integer.valueOf(312));
            scrollView.addView(view, LayoutHelper.createFrame(-1, -2, 17));
            textView.setTag(Integer.valueOf(2));
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    PopupNotificationActivity.this.openCurrentMessage();
                }
            });
        }
        view = textView;
        textView2 = (TextView) view.findViewWithTag(Integer.valueOf(312));
        BackupImageView backupImageView = (BackupImageView) view.findViewWithTag(Integer.valueOf(311));
        backupImageView.setAspectFit(true);
        if (messageObject.type == 1) {
            boolean z2;
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
            if (closestPhotoSizeWithSize != null) {
                boolean z3 = messageObject.type != 1 || FileLoader.getPathToMessage(messageObject.messageOwner).exists();
                if (!z3) {
                    if (!DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                        if (closestPhotoSizeWithSize2 != null) {
                            backupImageView.setImage(closestPhotoSizeWithSize2.location, null, (Drawable) null);
                            z2 = true;
                            if (z2) {
                                backupImageView.setVisibility(0);
                                textView2.setVisibility(8);
                            } else {
                                backupImageView.setVisibility(8);
                                textView2.setVisibility(0);
                                textView2.setTextSize(2, (float) SharedConfig.fontSize);
                                textView2.setText(messageObject.messageText);
                            }
                        }
                    }
                }
                backupImageView.setImage(closestPhotoSizeWithSize.location, "100_100", closestPhotoSizeWithSize2.location, closestPhotoSizeWithSize.size);
                z2 = true;
                if (z2) {
                    backupImageView.setVisibility(0);
                    textView2.setVisibility(8);
                } else {
                    backupImageView.setVisibility(8);
                    textView2.setVisibility(0);
                    textView2.setTextSize(2, (float) SharedConfig.fontSize);
                    textView2.setText(messageObject.messageText);
                }
            }
            z2 = false;
            if (z2) {
                backupImageView.setVisibility(8);
                textView2.setVisibility(0);
                textView2.setTextSize(2, (float) SharedConfig.fontSize);
                textView2.setText(messageObject.messageText);
            } else {
                backupImageView.setVisibility(0);
                textView2.setVisibility(8);
            }
        } else if (messageObject.type == 4) {
            textView2.setVisibility(8);
            textView2.setText(messageObject.messageText);
            backupImageView.setVisibility(0);
            double d = messageObject.messageOwner.media.geo.lat;
            double d2 = messageObject.messageOwner.media.geo._long;
            backupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), Double.valueOf(d), Double.valueOf(d2)}), null, null);
        }
        if (view.getParent() == null) {
            r0.messageContainer.addView(view);
        }
        view.setVisibility(0);
        if (z) {
            dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            LayoutParams layoutParams2 = (LayoutParams) view.getLayoutParams();
            layoutParams2.gravity = 51;
            layoutParams2.height = -1;
            layoutParams2.width = dp;
            if (i2 == r0.currentMessageNum) {
                view.setTranslationX(0.0f);
            } else if (i2 == r0.currentMessageNum - 1) {
                view.setTranslationX((float) (-dp));
            } else if (i2 == r0.currentMessageNum + 1) {
                view.setTranslationX((float) dp);
            }
            view.setLayoutParams(layoutParams2);
            view.invalidate();
        }
        return view;
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
            for (i = this.currentMessageNum - 1; i < this.currentMessageNum + 2; i++) {
                if (i == this.currentMessageNum - 1) {
                    this.leftView = getViewForMessage(i, true);
                    this.leftButtonsView = getButtonsViewForMessage(i, true);
                } else if (i == this.currentMessageNum) {
                    this.centerView = getViewForMessage(i, true);
                    this.centerButtonsView = getButtonsViewForMessage(i, true);
                } else if (i == this.currentMessageNum + 1) {
                    this.rightView = getViewForMessage(i, true);
                    this.rightButtonsView = getButtonsViewForMessage(i, true);
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
            if (this.rightView != 0) {
                i = this.rightView.getTranslationX();
                reuseView(this.rightView);
                r1 = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = r1;
                if (r1 != null) {
                    r1 = (LayoutParams) this.rightView.getLayoutParams();
                    r1.width = dp;
                    this.rightView.setLayoutParams(r1);
                    this.rightView.setTranslationX(i);
                    this.rightView.invalidate();
                }
            }
            if (this.rightButtonsView != 0) {
                i = this.rightButtonsView.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                r0 = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = r0;
                if (r0 != null) {
                    this.rightButtonsView.setTranslationX(i);
                }
            }
        } else if (i == 4) {
            if (this.leftView != 0) {
                i = this.leftView.getTranslationX();
                reuseView(this.leftView);
                r1 = getViewForMessage(0, false);
                this.leftView = r1;
                if (r1 != null) {
                    r1 = (LayoutParams) this.leftView.getLayoutParams();
                    r1.width = dp;
                    this.leftView.setLayoutParams(r1);
                    this.leftView.setTranslationX(i);
                    this.leftView.invalidate();
                }
            }
            if (this.leftButtonsView != 0) {
                i = this.leftButtonsView.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                r0 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = r0;
                if (r0 != null) {
                    this.leftButtonsView.setTranslationX(i);
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
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
                    return true;
                }
            });
        }
        if (this.messageContainer != null) {
            this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
            for (intent = null; intent < 3; intent++) {
                if (UserConfig.getInstance(intent).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(intent).popupMessages);
                }
            }
        }
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() == null) {
            if (ApplicationLoader.isScreenOn != null) {
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
        int size;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            size = this.popupMessages.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = (MessageObject) this.popupMessages.get(i);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i;
                    size = 1;
                    break;
                }
            }
        }
        size = 0;
        if (size == 0) {
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
            stringBuilder.append(ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setAction(stringBuilder.toString());
            intent.setFlags(32768);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void updateInterfaceForCurrentMessage(int i) {
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
            long dialogId = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialogId, this.currentMessageObject.currentAccount);
            int i2 = (int) dialogId;
            if (i2 == 0) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialogId >> 32))).user_id));
            } else if (i2 > 0) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(i2));
            } else {
                this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(-i2));
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
            }
            if (this.currentChat != null && this.currentUser != null) {
                this.nameTextView.setText(this.currentChat.title);
                this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            } else if (this.currentUser != null) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                if (i2 == 0) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(C0446R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
            prepareLayouts(i);
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
                            CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                            if (charSequence != null) {
                                if (charSequence.length() != 0) {
                                    this.lastPrintString = charSequence;
                                    this.onlineTextView.setText(charSequence);
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
                            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", C0446R.string.ServiceNotifications));
                        }
                    }
                }
            }
        }
    }

    private void checkAndUpdateAvatar() {
        if (this.currentMessageObject != null) {
            Drawable avatarDrawable;
            TLObject tLObject = null;
            if (this.currentChat != null) {
                Chat chat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    if (this.currentChat.photo != null) {
                        tLObject = this.currentChat.photo.photo_small;
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
                        tLObject = this.currentUser.photo.photo_small;
                    }
                    avatarDrawable = new AvatarDrawable(this.currentUser);
                } else {
                    return;
                }
            } else {
                avatarDrawable = null;
            }
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(tLObject, "50_50", avatarDrawable);
            }
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar != null) {
            boolean z2 = false;
            if (z) {
                try {
                    Integer num = (Integer) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
                    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[num.intValue()], null, null, null);
                    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                    int i;
                    while (i < this.statusDrawables.length) {
                        if (i == num.intValue()) {
                            this.statusDrawables[i].start();
                        } else {
                            this.statusDrawables[i].stop();
                        }
                        i++;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else {
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                this.onlineTextView.setCompoundDrawablePadding(0);
                while (z2 < this.statusDrawables.length) {
                    this.statusDrawables[z2].stop();
                    z2++;
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.appDidLogout) {
            int i3 = 0;
            if (i == NotificationCenter.pushMessagesUpdated) {
                if (this.isReply == 0) {
                    this.popupMessages.clear();
                    while (i3 < 3) {
                        if (UserConfig.getInstance(i3).isClientActivated() != 0) {
                            this.popupMessages.addAll(NotificationsController.getInstance(i3).popupMessages);
                        }
                        i3++;
                    }
                    getNewMessage();
                }
            } else if (i == NotificationCenter.updateInterfaces) {
                if (this.currentMessageObject != 0) {
                    if (i2 == this.lastResumedAccount) {
                        i = ((Integer) objArr[0]).intValue();
                        if (!((i & 1) == 0 && (i & 4) == 0 && (i & 16) == 0 && (i & 32) == 0)) {
                            updateSubtitle();
                        }
                        if (!((i & 2) == 0 && (i & 8) == 0)) {
                            checkAndUpdateAvatar();
                        }
                        if ((i & 64) != 0) {
                            CharSequence charSequence = (CharSequence) MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
                            if ((this.lastPrintString != 0 && charSequence == null) || ((this.lastPrintString == 0 && charSequence != null) || !(this.lastPrintString == 0 || charSequence == null || this.lastPrintString.equals(charSequence) != 0))) {
                                updateSubtitle();
                            }
                        }
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidReset) {
                r7 = (Integer) objArr[0];
                if (this.messageContainer != null) {
                    objArr = this.messageContainer.getChildCount();
                    while (i3 < objArr) {
                        r0 = this.messageContainer.getChildAt(i3);
                        if (((Integer) r0.getTag()).intValue() == 3) {
                            r0 = (PopupAudioView) r0.findViewWithTag(Integer.valueOf(300));
                            r4 = r0.getMessageObject();
                            if (r4 != null && r4.currentAccount == i2 && r4.getId() == r7.intValue()) {
                                r0.updateButtonState();
                                break;
                            }
                        }
                        i3++;
                    }
                }
            } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                r7 = (Integer) objArr[0];
                if (this.messageContainer != null) {
                    objArr = this.messageContainer.getChildCount();
                    while (i3 < objArr) {
                        r0 = this.messageContainer.getChildAt(i3);
                        if (((Integer) r0.getTag()).intValue() == 3) {
                            r0 = (PopupAudioView) r0.findViewWithTag(Integer.valueOf(300));
                            r4 = r0.getMessageObject();
                            if (r4 != null && r4.currentAccount == i2 && r4.getId() == r7.intValue()) {
                                r0.updateProgress();
                                break;
                            }
                        }
                        i3++;
                    }
                }
            } else if (i == NotificationCenter.emojiDidLoaded) {
                if (this.messageContainer != 0) {
                    i = this.messageContainer.getChildCount();
                    while (i3 < i) {
                        i2 = this.messageContainer.getChildAt(i3);
                        if (((Integer) i2.getTag()).intValue() == 1) {
                            TextView textView = (TextView) i2.findViewWithTag(Integer.valueOf(301));
                            if (textView != null) {
                                textView.invalidate();
                            }
                        }
                        i3++;
                    }
                }
            } else if (i == NotificationCenter.contactsDidLoaded && i2 == this.lastResumedAccount) {
                updateSubtitle();
            }
        } else if (i2 == this.lastResumedAccount) {
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
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoaded);
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
