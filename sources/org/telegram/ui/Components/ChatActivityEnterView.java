package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BotCommandsMenuView;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SenderSelectPopup;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends BlurredFrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    private static final int POPUP_CONTENT_BOT_KEYBOARD = 1;
    private static final int RECORD_STATE_CANCEL = 2;
    private static final int RECORD_STATE_CANCEL_BY_GESTURE = 5;
    private static final int RECORD_STATE_CANCEL_BY_TIME = 4;
    private static final int RECORD_STATE_ENTER = 0;
    private static final int RECORD_STATE_PREPARING = 3;
    private static final int RECORD_STATE_SENDING = 1;
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    public boolean allowBlur;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    /* access modifiers changed from: protected */
    public int animatedTop;
    /* access modifiers changed from: private */
    public int animatingContentType;
    /* access modifiers changed from: private */
    public HashMap<View, Float> animationParamsX;
    private ImageView attachButton;
    /* access modifiers changed from: private */
    public LinearLayout attachLayout;
    /* access modifiers changed from: private */
    public ImageView audioSendButton;
    private TLRPC.TL_document audioToSend;
    /* access modifiers changed from: private */
    public MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    /* access modifiers changed from: private */
    public AnimatorSet audioVideoButtonAnimation;
    /* access modifiers changed from: private */
    public FrameLayout audioVideoButtonContainer;
    Paint backgroundPaint;
    /* access modifiers changed from: private */
    public ImageView botButton;
    private ReplaceableIconDrawable botButtonDrawable;
    /* access modifiers changed from: private */
    public MessageObject botButtonsMessageObject;
    int botCommandLastPosition;
    int botCommandLastTop;
    private BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter;
    /* access modifiers changed from: private */
    public BotCommandsMenuView botCommandsMenuButton;
    public BotCommandsMenuContainer botCommandsMenuContainer;
    private int botCount;
    /* access modifiers changed from: private */
    public BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private BotMenuButtonType botMenuButtonType;
    private String botMenuWebViewTitle;
    private String botMenuWebViewUrl;
    private MessageObject botMessageObject;
    private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
    /* access modifiers changed from: private */
    public ChatActivityBotWebViewButton botWebViewButton;
    private BotWebViewMenuContainer botWebViewMenuContainer;
    /* access modifiers changed from: private */
    public boolean calledRecordRunnable;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    /* access modifiers changed from: private */
    public Drawable cameraOutline;
    /* access modifiers changed from: private */
    public boolean canWriteToChannel;
    /* access modifiers changed from: private */
    public ImageView cancelBotButton;
    /* access modifiers changed from: private */
    public NumberTextView captionLimitView;
    private float chatSearchExpandOffset;
    private boolean clearBotButtonsOnKeyboardOpen;
    /* access modifiers changed from: private */
    public boolean closeAnimationInProgress;
    /* access modifiers changed from: private */
    public int codePointCount;
    private float composeShadowAlpha;
    /* access modifiers changed from: private */
    public boolean configAnimationsEnabled;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentEmojiIcon;
    /* access modifiers changed from: private */
    public int currentLimit;
    /* access modifiers changed from: private */
    public int currentPopupContentType;
    private Animator currentResizeAnimation;
    public ValueAnimator currentTopViewAnimation;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public long dialog_id;
    private float distCanMove;
    /* access modifiers changed from: private */
    public AnimatorSet doneButtonAnimation;
    /* access modifiers changed from: private */
    public ValueAnimator doneButtonColorAnimator;
    private FrameLayout doneButtonContainer;
    boolean doneButtonEnabled;
    /* access modifiers changed from: private */
    public float doneButtonEnabledProgress;
    /* access modifiers changed from: private */
    public ImageView doneButtonImage;
    /* access modifiers changed from: private */
    public ContextProgressView doneButtonProgress;
    /* access modifiers changed from: private */
    public final Drawable doneCheckDrawable;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    private CharSequence draftMessage;
    private boolean draftSearchWebpage;
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public ImageView[] emojiButton;
    /* access modifiers changed from: private */
    public AnimatorSet emojiButtonAnimation;
    /* access modifiers changed from: private */
    public int emojiPadding;
    /* access modifiers changed from: private */
    public boolean emojiTabOpen;
    /* access modifiers changed from: private */
    public EmojiView emojiView;
    /* access modifiers changed from: private */
    public boolean emojiViewVisible;
    /* access modifiers changed from: private */
    public ImageView expandStickersButton;
    private boolean expandStickersWithKeyboard;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    /* access modifiers changed from: private */
    public boolean hasRecordVideo;
    private Runnable hideKeyboardRunnable;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public Drawable inactinveSendButtonDrawable;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int innerTextChange;
    /* access modifiers changed from: private */
    public boolean isInitLineCount;
    /* access modifiers changed from: private */
    public boolean isPaste;
    private boolean isPaused;
    /* access modifiers changed from: private */
    public int keyboardHeight;
    /* access modifiers changed from: private */
    public int keyboardHeightLand;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    /* access modifiers changed from: private */
    public long lastTypingTimeSend;
    /* access modifiers changed from: private */
    public int lineCount;
    private int[] location;
    /* access modifiers changed from: private */
    public Drawable lockShadowDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate;
    protected EditTextCaption messageEditText;
    boolean messageTransitionIsRunning;
    private TLRPC.WebPage messageWebPage;
    /* access modifiers changed from: private */
    public boolean messageWebPageSearch;
    /* access modifiers changed from: private */
    public Drawable micDrawable;
    /* access modifiers changed from: private */
    public Drawable micOutline;
    private Runnable moveToSendStateRunnable;
    private boolean needShowTopView;
    /* access modifiers changed from: private */
    public int notificationsIndex;
    /* access modifiers changed from: private */
    public ImageView notifyButton;
    /* access modifiers changed from: private */
    public CrossOutDrawable notifySilentDrawable;
    /* access modifiers changed from: private */
    public Runnable onEmojiSearchClosed;
    /* access modifiers changed from: private */
    public Runnable onFinishInitCameraRunnable;
    private Runnable onKeyboardClosed;
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable;
    private int originalViewHeight;
    /* access modifiers changed from: private */
    public Paint paint;
    /* access modifiers changed from: private */
    public AnimatorSet panelAnimation;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public RectF pauseRect;
    private TLRPC.KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    /* access modifiers changed from: private */
    public MediaActionDrawable playPauseDrawable;
    private int popupX;
    private int popupY;
    public boolean preventInput;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable;
    /* access modifiers changed from: private */
    public boolean recordAudioVideoRunnableStarted;
    /* access modifiers changed from: private */
    public RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale;
    private RLottieImageView recordDeleteImageView;
    /* access modifiers changed from: private */
    public RecordDot recordDot;
    private int recordInterfaceState;
    private boolean recordIsCanceled;
    /* access modifiers changed from: private */
    public FrameLayout recordPanel;
    private AnimatorSet recordPannelAnimation;
    private LinearLayout recordTimeContainer;
    /* access modifiers changed from: private */
    public TimerView recordTimerView;
    /* access modifiers changed from: private */
    public View recordedAudioBackground;
    /* access modifiers changed from: private */
    public FrameLayout recordedAudioPanel;
    /* access modifiers changed from: private */
    public ImageView recordedAudioPlayButton;
    /* access modifiers changed from: private */
    public SeekBarWaveformView recordedAudioSeekBar;
    /* access modifiers changed from: private */
    public TextView recordedAudioTimeTextView;
    /* access modifiers changed from: private */
    public boolean recordingAudioVideo;
    /* access modifiers changed from: private */
    public int recordingGuid;
    /* access modifiers changed from: private */
    public Rect rect;
    /* access modifiers changed from: private */
    public Paint redDotPaint;
    /* access modifiers changed from: private */
    public boolean removeEmojiViewAfterAnimation;
    /* access modifiers changed from: private */
    public MessageObject replyingMessageObject;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    private Property<View, Integer> roundedTranslationYProperty;
    private Runnable runEmojiPanelAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimation2;
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimationAudio;
    /* access modifiers changed from: private */
    public int runningAnimationType;
    private boolean scheduleButtonHidden;
    /* access modifiers changed from: private */
    public ImageView scheduledButton;
    /* access modifiers changed from: private */
    public AnimatorSet scheduledButtonAnimation;
    private ValueAnimator searchAnimator;
    /* access modifiers changed from: private */
    public float searchToOpenProgress;
    /* access modifiers changed from: private */
    public int searchingType;
    /* access modifiers changed from: private */
    public SeekBarWaveform seekBarWaveform;
    /* access modifiers changed from: private */
    public View sendButton;
    private FrameLayout sendButtonContainer;
    /* access modifiers changed from: private */
    public Drawable sendButtonDrawable;
    /* access modifiers changed from: private */
    public Drawable sendButtonInverseDrawable;
    /* access modifiers changed from: private */
    public boolean sendByEnter;
    /* access modifiers changed from: private */
    public Drawable sendDrawable;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    /* access modifiers changed from: private */
    public Rect sendRect;
    /* access modifiers changed from: private */
    public SenderSelectPopup senderSelectPopupWindow;
    /* access modifiers changed from: private */
    public SenderSelectView senderSelectView;
    private Runnable setTextFieldRunnable;
    protected boolean shouldAnimateEditTextWithBounds;
    private boolean showKeyboardOnResume;
    private Runnable showTopViewRunnable;
    /* access modifiers changed from: private */
    public boolean silent;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout sizeNotifierLayout;
    /* access modifiers changed from: private */
    public SlideTextView slideText;
    /* access modifiers changed from: private */
    public SimpleTextView slowModeButton;
    /* access modifiers changed from: private */
    public int slowModeTimer;
    private boolean smoothKeyboard;
    /* access modifiers changed from: private */
    public float startedDraggingX;
    private AnimatedArrowDrawable stickersArrow;
    /* access modifiers changed from: private */
    public boolean stickersDragging;
    /* access modifiers changed from: private */
    public boolean stickersExpanded;
    /* access modifiers changed from: private */
    public int stickersExpandedHeight;
    /* access modifiers changed from: private */
    public Animator stickersExpansionAnim;
    /* access modifiers changed from: private */
    public float stickersExpansionProgress;
    /* access modifiers changed from: private */
    public boolean stickersTabOpen;
    /* access modifiers changed from: private */
    public FrameLayout textFieldContainer;
    boolean textTransitionIsRunning;
    /* access modifiers changed from: protected */
    public View topLineView;
    /* access modifiers changed from: protected */
    public View topView;
    /* access modifiers changed from: protected */
    public float topViewEnterProgress;
    protected boolean topViewShowed;
    private final ValueAnimator.AnimatorUpdateListener topViewUpdateListener;
    /* access modifiers changed from: private */
    public TrendingStickersAlert trendingStickersAlert;
    /* access modifiers changed from: private */
    public Runnable updateExpandabilityRunnable;
    private Runnable updateSlowModeRunnable;
    /* access modifiers changed from: private */
    public ImageView videoSendButton;
    /* access modifiers changed from: private */
    public VideoTimelineView videoTimelineView;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoToSendMessageObject;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpen;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpenAfterAnimation;
    private PowerManager.WakeLock wakeLock;
    private boolean wasSendTyping;

    public enum BotMenuButtonType {
        NO_BUTTON,
        COMMANDS,
        WEB_VIEW
    }

    public interface ChatActivityEnterViewDelegate {
        void bottomPanelTranslationYChanged(float f);

        void didPressAttachButton();

        int getContentViewHeight();

        TLRPC.TL_channels_sendAsPeers getSendAsPeers();

        boolean hasForwardingMessages();

        boolean hasScheduledMessages();

        int measureKeyboardHeight();

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i, boolean z, int i2);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onAudioVideoInterfaceUpdated();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence, boolean z, int i);

        void onPreAudioVideoRecord();

        void onSendLongClick();

        void onStickersExpandedChange();

        void onStickersTab(boolean z);

        void onSwitchRecordMode(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onTextSelectionChanged(int i, int i2);

        void onTextSpansChanged(CharSequence charSequence);

        void onTrendingStickersShowed(boolean z);

        void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence);

        void onWindowSizeChanged(int i);

        void openScheduledMessages();

        void prepareMessageSending();

        void scrollToSendingMessage();

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate _this) {
            }

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate _this) {
            }

            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate _this) {
                return true;
            }

            public static void $default$bottomPanelTranslationYChanged(ChatActivityEnterViewDelegate _this, float translation) {
            }

            public static void $default$prepareMessageSending(ChatActivityEnterViewDelegate _this) {
            }

            public static void $default$onTrendingStickersShowed(ChatActivityEnterViewDelegate _this, boolean show) {
            }

            public static boolean $default$hasForwardingMessages(ChatActivityEnterViewDelegate _this) {
                return false;
            }

            public static int $default$getContentViewHeight(ChatActivityEnterViewDelegate _this) {
                return 0;
            }

            public static int $default$measureKeyboardHeight(ChatActivityEnterViewDelegate _this) {
                return 0;
            }

            public static TLRPC.TL_channels_sendAsPeers $default$getSendAsPeers(ChatActivityEnterViewDelegate _this) {
                return null;
            }
        }
    }

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            SeekBarWaveform unused = ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatActivityEnterView$SeekBarWaveformView  reason: not valid java name */
        public /* synthetic */ void m709x426e2bc3(float progress) {
            if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, progress);
            }
        }

        public void setWaveform(byte[] waveform) {
            ChatActivityEnterView.this.seekBarWaveform.setWaveform(waveform);
            invalidate();
        }

        public void setProgress(float progress) {
            ChatActivityEnterView.this.seekBarWaveform.setProgress(progress);
            invalidate();
        }

        public boolean isDragging() {
            return ChatActivityEnterView.this.seekBarWaveform.isDragging();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean result = ChatActivityEnterView.this.seekBarWaveform.onTouch(event.getAction(), event.getX(), event.getY());
            if (result) {
                if (event.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            if (result || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ChatActivityEnterView.this.seekBarWaveform.setSize(right - left, bottom - top);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgress"), ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgressInner"), ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgress"));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas, this);
        }
    }

    private class RecordDot extends View {
        private float alpha;
        boolean attachedToWindow;
        RLottieDrawable drawable;
        /* access modifiers changed from: private */
        public boolean enterAnimation;
        private boolean isIncr;
        private long lastUpdateTime;
        boolean playing;

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attachedToWindow = true;
            if (this.playing) {
                this.drawable.start();
            }
            this.drawable.addParentView(this);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attachedToWindow = false;
            this.drawable.stop();
            this.drawable.removeParentView(this);
        }

        public RecordDot(Context context) {
            super(context);
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "" + NUM, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), false, (int[]) null);
            this.drawable = rLottieDrawable;
            rLottieDrawable.setCurrentParentView(this);
            this.drawable.setInvalidateOnProgressSet(true);
            updateColors();
        }

        public void updateColors() {
            int dotColor = ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceDot");
            int background = ChatActivityEnterView.this.getThemedColor("chat_messagePanelBackground");
            ChatActivityEnterView.this.redDotPaint.setColor(dotColor);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", dotColor);
            this.drawable.setLayerColor("Box.**", dotColor);
            this.drawable.setLayerColor("Line 1.**", background);
            this.drawable.setLayerColor("Line 2.**", background);
            this.drawable.setLayerColor("Line 3.**", background);
            this.drawable.commitApplyLayerColors();
            if (ChatActivityEnterView.this.playPauseDrawable != null) {
                ChatActivityEnterView.this.playPauseDrawable.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordedVoicePlayPause"));
            }
        }

        public void resetAlpha() {
            this.alpha = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            this.playing = false;
            this.drawable.stop();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.playing) {
                this.drawable.setAlpha((int) (this.alpha * 255.0f));
            }
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.enterAnimation) {
                this.alpha = 1.0f;
            } else if (this.isIncr || this.playing) {
                float f = this.alpha + (((float) dt) / 600.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            } else {
                float f2 = this.alpha - (((float) dt) / 600.0f);
                this.alpha = f2;
                if (f2 <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.playing) {
                this.drawable.draw(canvas);
            }
            if (!this.playing || !this.drawable.hasBitmap()) {
                canvas.drawCircle((float) (getMeasuredWidth() >> 1), (float) (getMeasuredHeight() >> 1), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
            }
            invalidate();
        }

        public void playDeleteAnimation() {
            this.playing = true;
            this.drawable.setProgress(0.0f);
            if (this.attachedToWindow) {
                this.drawable.start();
            }
        }
    }

    public class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        BlobDrawable bigWaveDrawable = new BlobDrawable(12);
        private boolean canceledByGesture;
        private float circleRadius = AndroidUtilities.dpf2(41.0f);
        private float circleRadiusAmplitude = ((float) AndroidUtilities.dp(30.0f));
        public float drawingCircleRadius;
        public float drawingCx;
        public float drawingCy;
        private float exitTransition;
        public float iconScale;
        float idleProgress;
        boolean incIdle;
        private float lastMovingX;
        private float lastMovingY;
        private int lastSize;
        private long lastUpdateTime;
        private float lockAnimatedTranslation;
        Paint lockBackgroundPaint = new Paint(1);
        Paint lockOutlinePaint = new Paint(1);
        Paint lockPaint = new Paint(1);
        private Paint p = new Paint(1);
        private int paintAlpha;
        Path path = new Path();
        private boolean pressed;
        private float progressToSeekbarStep3;
        private float progressToSendButton;
        RectF rectF = new RectF();
        private float scale;
        /* access modifiers changed from: private */
        public boolean sendButtonVisible;
        private boolean showTooltip;
        private long showTooltipStartTime;
        private boolean showWaves = true;
        public boolean skipDraw;
        private int slideDelta;
        private float slideToCancelLockProgress;
        /* access modifiers changed from: private */
        public float slideToCancelProgress;
        private float snapAnimationProgress;
        /* access modifiers changed from: private */
        public float startTranslation;
        BlobDrawable tinyWaveDrawable = new BlobDrawable(11);
        private float tooltipAlpha;
        private Drawable tooltipBackground;
        private Drawable tooltipBackgroundArrow;
        private StaticLayout tooltipLayout;
        private String tooltipMessage;
        private TextPaint tooltipPaint = new TextPaint(1);
        private float tooltipWidth;
        private float touchSlop;
        private float transformToSeekbar;
        private VirtualViewHelper virtualViewHelper;
        public boolean voiceEnterTransitionInProgress;
        private float wavesEnterAnimation = 0.0f;

        public RecordCircle(Context context) {
            super(context);
            Drawable unused = ChatActivityEnterView.this.micDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused2 = ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused3 = ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused4 = ChatActivityEnterView.this.micOutline = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.micOutline.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            Drawable unused5 = ChatActivityEnterView.this.cameraOutline = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraOutline.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            VirtualViewHelper virtualViewHelper2 = new VirtualViewHelper(this);
            this.virtualViewHelper = virtualViewHelper2;
            ViewCompat.setAccessibilityDelegate(this, virtualViewHelper2);
            this.tinyWaveDrawable.minRadius = (float) AndroidUtilities.dp(47.0f);
            this.tinyWaveDrawable.maxRadius = (float) AndroidUtilities.dp(55.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = (float) AndroidUtilities.dp(47.0f);
            this.bigWaveDrawable.maxRadius = (float) AndroidUtilities.dp(55.0f);
            this.bigWaveDrawable.generateBlob();
            this.lockOutlinePaint.setStyle(Paint.Style.STROKE);
            this.lockOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
            this.lockOutlinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
            Drawable unused6 = ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("key_chat_messagePanelVoiceLockShadow"), PorterDuff.Mode.MULTIPLY));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.getThemedColor("chat_gifSaveHintBackground"));
            this.tooltipPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, NUM);
            this.tooltipMessage = LocaleController.getString("SlideUpToLock", NUM);
            this.iconScale = 1.0f;
            float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            this.touchSlop = scaledTouchSlop;
            this.touchSlop = scaledTouchSlop * scaledTouchSlop;
            updateColors();
        }

        public void setAmplitude(double value) {
            this.bigWaveDrawable.setValue((float) (Math.min(1800.0d, value) / 1800.0d), true);
            this.tinyWaveDrawable.setValue((float) (Math.min(1800.0d, value) / 1800.0d), false);
            float min = (float) (Math.min(1800.0d, value) / 1800.0d);
            this.animateToAmplitude = min;
            this.animateAmplitudeDiff = (min - this.amplitude) / 375.0f;
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        public void setScale(float value) {
            this.scale = value;
            invalidate();
        }

        public void setLockAnimatedTranslation(float value) {
            this.lockAnimatedTranslation = value;
            invalidate();
        }

        public void setSnapAnimationProgress(float snapAnimationProgress2) {
            this.snapAnimationProgress = snapAnimationProgress2;
            invalidate();
        }

        public float getLockAnimatedTranslation() {
            return this.lockAnimatedTranslation;
        }

        public boolean isSendButtonVisible() {
            return this.sendButtonVisible;
        }

        public void setSendButtonInvisible() {
            this.sendButtonVisible = false;
            invalidate();
        }

        public int setLockTranslation(float value) {
            if (value == 10000.0f) {
                this.sendButtonVisible = false;
                this.lockAnimatedTranslation = -1.0f;
                this.startTranslation = -1.0f;
                invalidate();
                this.snapAnimationProgress = 0.0f;
                this.transformToSeekbar = 0.0f;
                this.exitTransition = 0.0f;
                this.iconScale = 1.0f;
                this.scale = 0.0f;
                this.tooltipAlpha = 0.0f;
                this.showTooltip = false;
                this.progressToSendButton = 0.0f;
                this.slideToCancelProgress = 1.0f;
                this.slideToCancelLockProgress = 1.0f;
                this.canceledByGesture = false;
                return 0;
            } else if (this.sendButtonVisible) {
                return 2;
            } else {
                if (this.lockAnimatedTranslation == -1.0f) {
                    this.startTranslation = value;
                }
                this.lockAnimatedTranslation = value;
                invalidate();
                if (this.canceledByGesture || this.slideToCancelProgress < 0.7f || this.startTranslation - this.lockAnimatedTranslation < ((float) AndroidUtilities.dp(57.0f))) {
                    return 1;
                }
                this.sendButtonVisible = true;
                return 2;
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.sendButtonVisible) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == 0) {
                    boolean contains = ChatActivityEnterView.this.pauseRect.contains((float) x, (float) y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (event.getAction() == 1 && ChatActivityEnterView.this.pauseRect.contains((float) x, (float) y)) {
                        if (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                            MediaController.getInstance().stopRecording(2, true, 0);
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                        } else {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                        }
                        ChatActivityEnterView.this.slideText.setEnabled(false);
                    }
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int currentSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(194.0f);
            if (this.lastSize != currentSize) {
                this.lastSize = currentSize;
                StaticLayout staticLayout = new StaticLayout(this.tooltipMessage, this.tooltipPaint, AndroidUtilities.dp(220.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                this.tooltipLayout = staticLayout;
                int n = staticLayout.getLineCount();
                this.tooltipWidth = 0.0f;
                for (int i = 0; i < n; i++) {
                    float w = this.tooltipLayout.getLineWidth(i);
                    if (w > this.tooltipWidth) {
                        this.tooltipWidth = w;
                    }
                }
            }
            StaticLayout staticLayout2 = this.tooltipLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 1) {
                h += this.tooltipLayout.getHeight() - this.tooltipLayout.getLineBottom(0);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, NUM));
            float distance = ((float) getMeasuredWidth()) * 0.35f;
            if (distance > ((float) AndroidUtilities.dp(140.0f))) {
                distance = (float) AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-distance) * (1.0f - this.slideToCancelProgress));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v87, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v7, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v13, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v32, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v33, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v34, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: float} */
        /* JADX WARNING: type inference failed for: r15v25, types: [android.view.ViewParent] */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:172:0x06fd, code lost:
            if ((java.lang.System.currentTimeMillis() - r6.showTooltipStartTime) <= 200) goto L_0x0702;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:100:0x02db  */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x030e  */
        /* JADX WARNING: Removed duplicated region for block: B:106:0x0322  */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x033c  */
        /* JADX WARNING: Removed duplicated region for block: B:112:0x03a1  */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x03b2  */
        /* JADX WARNING: Removed duplicated region for block: B:116:0x03b5  */
        /* JADX WARNING: Removed duplicated region for block: B:127:0x03d2  */
        /* JADX WARNING: Removed duplicated region for block: B:138:0x046a  */
        /* JADX WARNING: Removed duplicated region for block: B:141:0x0474  */
        /* JADX WARNING: Removed duplicated region for block: B:160:0x05e6  */
        /* JADX WARNING: Removed duplicated region for block: B:163:0x0600  */
        /* JADX WARNING: Removed duplicated region for block: B:168:0x0670  */
        /* JADX WARNING: Removed duplicated region for block: B:171:0x06ef  */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x0700  */
        /* JADX WARNING: Removed duplicated region for block: B:187:0x072a  */
        /* JADX WARNING: Removed duplicated region for block: B:192:0x0742  */
        /* JADX WARNING: Removed duplicated region for block: B:197:0x0769  */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x0895  */
        /* JADX WARNING: Removed duplicated region for block: B:199:0x089a  */
        /* JADX WARNING: Removed duplicated region for block: B:202:0x08c2  */
        /* JADX WARNING: Removed duplicated region for block: B:203:0x08c6  */
        /* JADX WARNING: Removed duplicated region for block: B:214:0x08e4  */
        /* JADX WARNING: Removed duplicated region for block: B:219:0x08f9  */
        /* JADX WARNING: Removed duplicated region for block: B:226:0x0931  */
        /* JADX WARNING: Removed duplicated region for block: B:227:0x0934  */
        /* JADX WARNING: Removed duplicated region for block: B:230:0x0a48  */
        /* JADX WARNING: Removed duplicated region for block: B:233:0x0a59  */
        /* JADX WARNING: Removed duplicated region for block: B:238:0x0b4a  */
        /* JADX WARNING: Removed duplicated region for block: B:241:0x0b63  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x01e5  */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x0205  */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x0225  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x023f  */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x027e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r53) {
            /*
                r52 = this;
                r6 = r52
                r7 = r53
                boolean r0 = r6.skipDraw
                if (r0 == 0) goto L_0x0009
                return
            L_0x0009:
                r0 = 0
                android.text.StaticLayout r1 = r6.tooltipLayout
                r8 = 1
                r9 = 0
                if (r1 == 0) goto L_0x0026
                int r1 = r1.getLineCount()
                if (r1 <= r8) goto L_0x0026
                android.text.StaticLayout r1 = r6.tooltipLayout
                int r1 = r1.getHeight()
                android.text.StaticLayout r2 = r6.tooltipLayout
                int r2 = r2.getLineBottom(r9)
                int r1 = r1 - r2
                float r0 = (float) r1
                r10 = r0
                goto L_0x0027
            L_0x0026:
                r10 = r0
            L_0x0027:
                int r0 = r52.getMeasuredWidth()
                r1 = 1104150528(0x41d00000, float:26.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp2(r1)
                int r11 = r0 - r1
                r0 = 1126825984(0x432a0000, float:170.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r0 + r10
                int r12 = (int) r0
                r0 = 0
                int r1 = r6.slideDelta
                int r1 = r1 + r11
                float r1 = (float) r1
                r6.drawingCx = r1
                float r1 = (float) r12
                r6.drawingCy = r1
                float r1 = r6.lockAnimatedTranslation
                r2 = 1176256512(0x461CLASSNAME, float:10000.0)
                r3 = 1113849856(0x42640000, float:57.0)
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 == 0) goto L_0x006c
                float r2 = r6.startTranslation
                float r2 = r2 - r1
                int r1 = (int) r2
                int r1 = java.lang.Math.max(r9, r1)
                float r0 = (float) r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 <= 0) goto L_0x006a
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r0 = (float) r1
                r13 = r0
                goto L_0x006d
            L_0x006a:
                r13 = r0
                goto L_0x006d
            L_0x006c:
                r13 = r0
            L_0x006d:
                r0 = 1065353216(0x3var_, float:1.0)
                float r1 = r6.scale
                r2 = 1056964608(0x3var_, float:0.5)
                r14 = 1065353216(0x3var_, float:1.0)
                int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r4 > 0) goto L_0x007c
                float r1 = r1 / r2
                r15 = r1
                goto L_0x009d
            L_0x007c:
                r4 = 1061158912(0x3var_, float:0.75)
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 > 0) goto L_0x008f
                float r1 = r1 - r2
                r2 = 1048576000(0x3e800000, float:0.25)
                float r1 = r1 / r2
                r2 = 1036831949(0x3dcccccd, float:0.1)
                float r1 = r1 * r2
                float r1 = r14 - r1
                r15 = r1
                goto L_0x009d
            L_0x008f:
                r2 = 1063675494(0x3var_, float:0.9)
                float r1 = r1 - r4
                r4 = 1048576000(0x3e800000, float:0.25)
                float r1 = r1 / r4
                r4 = 1036831949(0x3dcccccd, float:0.1)
                float r1 = r1 * r4
                float r1 = r1 + r2
                r15 = r1
            L_0x009d:
                long r1 = java.lang.System.currentTimeMillis()
                long r4 = r6.lastUpdateTime
                long r4 = r1 - r4
                float r1 = r6.animateToAmplitude
                float r2 = r6.amplitude
                r8 = 0
                int r17 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r17 == 0) goto L_0x00ca
                float r9 = r6.animateAmplitudeDiff
                float r3 = (float) r4
                float r3 = r3 * r9
                float r2 = r2 + r3
                r6.amplitude = r2
                int r3 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
                if (r3 <= 0) goto L_0x00c1
                int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r2 <= 0) goto L_0x00c7
                r6.amplitude = r1
                goto L_0x00c7
            L_0x00c1:
                int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r2 >= 0) goto L_0x00c7
                r6.amplitude = r1
            L_0x00c7:
                r52.invalidate()
            L_0x00ca:
                boolean r1 = r6.canceledByGesture
                r9 = 1060320051(0x3var_, float:0.7)
                if (r1 == 0) goto L_0x00e0
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r2 = r6.slideToCancelProgress
                float r2 = r14 - r2
                float r1 = r1.getInterpolation(r2)
                float r1 = r1 * r9
                r19 = r1
                goto L_0x00ea
            L_0x00e0:
                float r1 = r6.slideToCancelProgress
                r2 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 * r2
                float r1 = r1 + r9
                r19 = r1
            L_0x00ea:
                float r1 = r6.circleRadius
                float r2 = r6.circleRadiusAmplitude
                float r3 = r6.amplitude
                float r2 = r2 * r3
                float r1 = r1 + r2
                float r1 = r1 * r15
                float r1 = r1 * r19
                r6.progressToSeekbarStep3 = r8
                r2 = 0
                r3 = 0
                r20 = 0
                float r9 = r6.transformToSeekbar
                r22 = 1053609165(0x3ecccccd, float:0.4)
                r23 = 1098907648(0x41800000, float:16.0)
                r24 = 1073741824(0x40000000, float:2.0)
                int r25 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
                if (r25 == 0) goto L_0x0173
                r25 = 1052938076(0x3eCLASSNAMEf5c, float:0.38)
                r26 = 1048576000(0x3e800000, float:0.25)
                float r27 = r14 - r25
                float r27 = r27 - r26
                int r28 = (r9 > r25 ? 1 : (r9 == r25 ? 0 : -1))
                if (r28 <= 0) goto L_0x011a
                r28 = 1065353216(0x3var_, float:1.0)
                goto L_0x011c
            L_0x011a:
                float r28 = r9 / r25
            L_0x011c:
                r2 = r28
                float r28 = r25 + r26
                int r28 = (r9 > r28 ? 1 : (r9 == r28 ? 0 : -1))
                if (r28 <= 0) goto L_0x0127
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x012f
            L_0x0127:
                float r9 = r9 - r25
                float r9 = r9 / r26
                float r9 = java.lang.Math.max(r8, r9)
            L_0x012f:
                r3 = r9
                float r9 = r6.transformToSeekbar
                float r9 = r9 - r25
                float r9 = r9 - r26
                float r9 = r9 / r27
                float r9 = java.lang.Math.max(r8, r9)
                r6.progressToSeekbarStep3 = r9
                org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r2 = r9.getInterpolation(r2)
                org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r3 = r9.getInterpolation(r3)
                org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r8 = r6.progressToSeekbarStep3
                float r8 = r9.getInterpolation(r8)
                r6.progressToSeekbarStep3 = r8
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
                float r8 = (float) r8
                float r8 = r8 * r2
                float r1 = r1 + r8
                org.telegram.ui.Components.ChatActivityEnterView r8 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r8 = r8.recordedAudioBackground
                int r8 = r8.getMeasuredHeight()
                float r8 = (float) r8
                float r8 = r8 / r24
                float r9 = r1 - r8
                float r29 = r14 - r3
                float r9 = r9 * r29
                float r1 = r8 + r9
                goto L_0x01de
            L_0x0173:
                float r8 = r6.exitTransition
                r9 = 0
                int r25 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r25 == 0) goto L_0x01de
                r9 = 1058642330(0x3var_a, float:0.6)
                r25 = 1053609165(0x3ecccccd, float:0.4)
                int r26 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r26 <= 0) goto L_0x0187
                r8 = 1065353216(0x3var_, float:1.0)
                goto L_0x0188
            L_0x0187:
                float r8 = r8 / r9
            L_0x0188:
                r2 = r8
                org.telegram.ui.Components.ChatActivityEnterView r8 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r8 = r8.messageTransitionIsRunning
                if (r8 == 0) goto L_0x0192
                float r8 = r6.exitTransition
                goto L_0x019c
            L_0x0192:
                float r8 = r6.exitTransition
                float r8 = r8 - r9
                float r8 = r8 / r25
                r14 = 0
                float r8 = java.lang.Math.max(r14, r8)
            L_0x019c:
                org.telegram.ui.Components.CubicBezierInterpolator r14 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r2 = r14.getInterpolation(r2)
                org.telegram.ui.Components.CubicBezierInterpolator r14 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r20 = r14.getInterpolation(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r23)
                float r8 = (float) r8
                float r8 = r8 * r2
                float r1 = r1 + r8
                r8 = 1065353216(0x3var_, float:1.0)
                float r14 = r8 - r20
                float r1 = r1 * r14
                org.telegram.ui.Components.ChatActivityEnterView r8 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r8 = r8.configAnimationsEnabled
                if (r8 == 0) goto L_0x01da
                float r8 = r6.exitTransition
                r14 = 1058642330(0x3var_a, float:0.6)
                int r14 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
                if (r14 <= 0) goto L_0x01da
                r14 = 1058642330(0x3var_a, float:0.6)
                float r8 = r8 - r14
                float r8 = r8 / r22
                r14 = 1065353216(0x3var_, float:1.0)
                float r8 = r14 - r8
                r14 = 0
                float r0 = java.lang.Math.max(r14, r8)
                r8 = r1
                r9 = r2
                r14 = r3
                goto L_0x01e1
            L_0x01da:
                r8 = r1
                r9 = r2
                r14 = r3
                goto L_0x01e1
            L_0x01de:
                r8 = r1
                r9 = r2
                r14 = r3
            L_0x01e1:
                boolean r1 = r6.canceledByGesture
                if (r1 == 0) goto L_0x01fc
                float r1 = r6.slideToCancelProgress
                r2 = 1060320051(0x3var_, float:0.7)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 <= 0) goto L_0x01fc
                float r1 = r1 - r2
                r2 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 / r2
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = r2 - r1
                float r0 = r0 * r1
                r25 = r0
                goto L_0x01fe
            L_0x01fc:
                r25 = r0
            L_0x01fe:
                float r0 = r6.progressToSeekbarStep3
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0225
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r1 = r1.getThemedColor(r2)
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r3 = "chat_recordedVoiceBackground"
                int r2 = r2.getThemedColor(r3)
                float r3 = r6.progressToSeekbarStep3
                int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r2, r3)
                r0.setColor(r1)
                goto L_0x0236
            L_0x0225:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r1 = r1.getThemedColor(r2)
                r0.setColor(r1)
            L_0x0236:
                r0 = 0
                boolean r1 = r52.isSendButtonVisible()
                r27 = 1125515264(0x43160000, float:150.0)
                if (r1 == 0) goto L_0x027e
                float r1 = r6.progressToSendButton
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 == 0) goto L_0x0275
                float r3 = (float) r4
                float r3 = r3 / r27
                float r1 = r1 + r3
                r6.progressToSendButton = r1
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0253
                r6.progressToSendButton = r2
            L_0x0253:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                if (r1 == 0) goto L_0x026e
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                java.lang.Object r1 = r1.getTag()
                if (r1 == 0) goto L_0x026e
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.cameraDrawable
                goto L_0x0274
            L_0x026e:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.micDrawable
            L_0x0274:
                r0 = r1
            L_0x0275:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.sendDrawable
                r3 = r0
                r2 = r1
                goto L_0x02a1
            L_0x027e:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                if (r1 == 0) goto L_0x0299
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                java.lang.Object r1 = r1.getTag()
                if (r1 == 0) goto L_0x0299
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.cameraDrawable
                goto L_0x029f
            L_0x0299:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.micDrawable
            L_0x029f:
                r3 = r0
                r2 = r1
            L_0x02a1:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r0 = r0.sendRect
                int r1 = r2.getIntrinsicWidth()
                int r1 = r1 / 2
                int r1 = r11 - r1
                int r29 = r2.getIntrinsicHeight()
                int r29 = r29 / 2
                r30 = r15
                int r15 = r12 - r29
                int r29 = r2.getIntrinsicWidth()
                int r29 = r29 / 2
                r31 = r10
                int r10 = r11 + r29
                int r29 = r2.getIntrinsicHeight()
                int r29 = r29 / 2
                r32 = r8
                int r8 = r12 + r29
                r0.set(r1, r15, r10, r8)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r0 = r0.sendRect
                r2.setBounds(r0)
                if (r3 == 0) goto L_0x02fc
                int r0 = r3.getIntrinsicWidth()
                int r0 = r0 / 2
                int r0 = r11 - r0
                int r1 = r3.getIntrinsicHeight()
                int r1 = r1 / 2
                int r1 = r12 - r1
                int r8 = r3.getIntrinsicWidth()
                int r8 = r8 / 2
                int r8 = r8 + r11
                int r10 = r3.getIntrinsicHeight()
                int r10 = r10 / 2
                int r10 = r10 + r12
                r3.setBounds(r0, r1, r8, r10)
            L_0x02fc:
                r0 = 1113849856(0x42640000, float:57.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r13 / r0
                r1 = 1065353216(0x3var_, float:1.0)
                float r8 = r1 - r0
                r10 = 0
                boolean r0 = r6.incIdle
                if (r0 == 0) goto L_0x0322
                float r0 = r6.idleProgress
                r1 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 + r1
                r6.idleProgress = r0
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0334
                r0 = 0
                r6.incIdle = r0
                r6.idleProgress = r1
                goto L_0x0334
            L_0x0322:
                float r0 = r6.idleProgress
                r1 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 - r1
                r6.idleProgress = r0
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 >= 0) goto L_0x0334
                r0 = 1
                r6.incIdle = r0
                r6.idleProgress = r1
            L_0x0334:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r0 = r0.configAnimationsEnabled
                if (r0 == 0) goto L_0x03a1
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r1 = 1111228416(0x423CLASSNAME, float:47.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r0.minRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r1 = 1111228416(0x423CLASSNAME, float:47.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r18 = 1097859072(0x41700000, float:15.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r18)
                float r15 = (float) r15
                float r18 = org.telegram.ui.Components.BlobDrawable.FORM_SMALL_MAX
                float r15 = r15 * r18
                float r1 = r1 + r15
                r0.maxRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                r1 = 1112014848(0x42480000, float:50.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r0.minRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                r1 = 1112014848(0x42480000, float:50.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r18 = r2
                r15 = 1094713344(0x41400000, float:12.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r2 = (float) r2
                float r15 = org.telegram.ui.Components.BlobDrawable.FORM_BIG_MAX
                float r2 = r2 * r15
                float r1 = r1 + r2
                r0.maxRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                r0.updateAmplitude(r4)
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                float r1 = r0.amplitude
                r2 = 1065437102(0x3var_ae, float:1.01)
                r0.update(r1, r2)
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r0.updateAmplitude(r4)
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                float r1 = r0.amplitude
                r2 = 1065520988(0x3var_f5c, float:1.02)
                r0.update(r1, r2)
                goto L_0x03a3
            L_0x03a1:
                r18 = r2
            L_0x03a3:
                long r0 = java.lang.System.currentTimeMillis()
                r6.lastUpdateTime = r0
                float r0 = r6.slideToCancelProgress
                r1 = 1060320051(0x3var_, float:0.7)
                int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r2 <= 0) goto L_0x03b5
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x03b6
            L_0x03b5:
                float r0 = r0 / r1
            L_0x03b6:
                r15 = r0
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r0 = r0.configAnimationsEnabled
                if (r0 == 0) goto L_0x046a
                r0 = 1065353216(0x3var_, float:1.0)
                int r1 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
                if (r1 == 0) goto L_0x046a
                int r0 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1))
                if (r0 >= 0) goto L_0x046a
                r0 = 0
                int r1 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
                if (r1 <= 0) goto L_0x046a
                boolean r0 = r6.canceledByGesture
                if (r0 != 0) goto L_0x046a
                boolean r0 = r6.showWaves
                if (r0 == 0) goto L_0x03ea
                float r0 = r6.wavesEnterAnimation
                r1 = 1065353216(0x3var_, float:1.0)
                int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r2 == 0) goto L_0x03ea
                r2 = 1025758986(0x3d23d70a, float:0.04)
                float r0 = r0 + r2
                r6.wavesEnterAnimation = r0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x03ea
                r6.wavesEnterAnimation = r1
            L_0x03ea:
                boolean r0 = r6.voiceEnterTransitionInProgress
                if (r0 != 0) goto L_0x0465
                org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r1 = r6.wavesEnterAnimation
                float r0 = r0.getInterpolation(r1)
                r53.save()
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r33 = r2 - r9
                float r1 = r1 * r33
                float r1 = r1 * r15
                float r1 = r1 * r0
                float r2 = org.telegram.ui.Components.BlobDrawable.SCALE_BIG_MIN
                r33 = 1068708659(0x3fb33333, float:1.4)
                r34 = r3
                org.telegram.ui.Components.BlobDrawable r3 = r6.bigWaveDrawable
                float r3 = r3.amplitude
                float r3 = r3 * r33
                float r2 = r2 + r3
                float r1 = r1 * r2
                int r2 = r6.slideDelta
                int r2 = r2 + r11
                float r2 = (float) r2
                float r3 = (float) r12
                r7.scale(r1, r1, r2, r3)
                org.telegram.ui.Components.BlobDrawable r2 = r6.bigWaveDrawable
                int r3 = r6.slideDelta
                int r3 = r3 + r11
                float r3 = (float) r3
                r33 = r1
                float r1 = (float) r12
                r35 = r4
                android.graphics.Paint r4 = r2.paint
                r2.draw(r3, r1, r7, r4)
                r53.restore()
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r3 = r2 - r9
                float r1 = r1 * r3
                float r1 = r1 * r15
                float r1 = r1 * r0
                float r2 = org.telegram.ui.Components.BlobDrawable.SCALE_SMALL_MIN
                r3 = 1068708659(0x3fb33333, float:1.4)
                org.telegram.ui.Components.BlobDrawable r4 = r6.tinyWaveDrawable
                float r4 = r4.amplitude
                float r4 = r4 * r3
                float r2 = r2 + r4
                float r1 = r1 * r2
                r53.save()
                int r2 = r6.slideDelta
                int r2 = r2 + r11
                float r2 = (float) r2
                float r3 = (float) r12
                r7.scale(r1, r1, r2, r3)
                org.telegram.ui.Components.BlobDrawable r2 = r6.tinyWaveDrawable
                int r3 = r6.slideDelta
                int r3 = r3 + r11
                float r3 = (float) r3
                float r4 = (float) r12
                android.graphics.Paint r5 = r2.paint
                r2.draw(r3, r4, r7, r5)
                r53.restore()
                goto L_0x046e
            L_0x0465:
                r34 = r3
                r35 = r4
                goto L_0x046e
            L_0x046a:
                r34 = r3
                r35 = r4
            L_0x046e:
                boolean r0 = r6.voiceEnterTransitionInProgress
                r33 = 1132396544(0x437var_, float:255.0)
                if (r0 != 0) goto L_0x05e6
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                int r1 = r6.paintAlpha
                float r1 = (float) r1
                float r1 = r1 * r25
                int r1 = (int) r1
                r0.setAlpha(r1)
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 != 0) goto L_0x05d5
                float r0 = r6.transformToSeekbar
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x0590
                float r0 = r6.progressToSeekbarStep3
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0575
                float r0 = (float) r12
                float r0 = r0 + r32
                float r1 = (float) r12
                float r1 = r1 - r32
                int r2 = r6.slideDelta
                int r3 = r11 + r2
                float r3 = (float) r3
                float r3 = r3 + r32
                int r2 = r2 + r11
                float r2 = (float) r2
                float r2 = r2 - r32
                r4 = 0
                r5 = 0
                r37 = r4
                org.telegram.ui.Components.ChatActivityEnterView r4 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r4 = r4.recordedAudioBackground
                android.view.ViewParent r38 = r4.getParent()
                android.view.View r38 = (android.view.View) r38
                r51 = r38
                r38 = r10
                r10 = r51
            L_0x04bf:
                r39 = r15
                android.view.ViewParent r15 = r52.getParent()
                if (r10 == r15) goto L_0x04dc
                int r15 = r10.getTop()
                int r37 = r37 + r15
                int r15 = r10.getLeft()
                int r5 = r5 + r15
                android.view.ViewParent r15 = r10.getParent()
                r10 = r15
                android.view.View r10 = (android.view.View) r10
                r15 = r39
                goto L_0x04bf
            L_0x04dc:
                int r15 = r4.getTop()
                int r15 = r15 + r37
                int r40 = r52.getTop()
                int r15 = r15 - r40
                int r40 = r4.getBottom()
                int r40 = r40 + r37
                int r41 = r52.getTop()
                r42 = r10
                int r10 = r40 - r41
                int r40 = r4.getRight()
                int r40 = r40 + r5
                int r41 = r52.getLeft()
                r43 = r9
                int r9 = r40 - r41
                int r40 = r4.getLeft()
                int r40 = r40 + r5
                int r41 = r52.getLeft()
                r44 = r5
                int r5 = r40 - r41
                r40 = r8
                org.telegram.ui.Components.ChatActivityEnterView r8 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r8 = r8.isInVideoMode()
                if (r8 == 0) goto L_0x051e
                r8 = 0
                goto L_0x0525
            L_0x051e:
                int r8 = r4.getMeasuredHeight()
                float r8 = (float) r8
                float r8 = r8 / r24
            L_0x0525:
                r41 = r4
                float r4 = (float) r15
                r45 = r13
                float r13 = (float) r15
                float r13 = r1 - r13
                r46 = r1
                float r1 = r6.progressToSeekbarStep3
                r26 = 1065353216(0x3var_, float:1.0)
                float r47 = r26 - r1
                float r13 = r13 * r47
                float r4 = r4 + r13
                float r13 = (float) r10
                r47 = r15
                float r15 = (float) r10
                float r15 = r0 - r15
                float r48 = r26 - r1
                float r15 = r15 * r48
                float r13 = r13 + r15
                float r15 = (float) r5
                r48 = r0
                float r0 = (float) r5
                float r0 = r2 - r0
                float r49 = r26 - r1
                float r0 = r0 * r49
                float r15 = r15 + r0
                float r0 = (float) r9
                r49 = r2
                float r2 = (float) r9
                float r2 = r3 - r2
                float r50 = r26 - r1
                float r2 = r2 * r50
                float r0 = r0 + r2
                float r2 = r32 - r8
                float r1 = r26 - r1
                float r2 = r2 * r1
                float r2 = r2 + r8
                android.graphics.RectF r1 = r6.rectF
                r1.set(r15, r4, r0, r13)
                android.graphics.RectF r1 = r6.rectF
                r50 = r0
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                r7.drawRoundRect(r1, r2, r2, r0)
                r8 = r32
                goto L_0x05aa
            L_0x0575:
                r40 = r8
                r43 = r9
                r38 = r10
                r45 = r13
                r39 = r15
                int r0 = r6.slideDelta
                int r0 = r0 + r11
                float r0 = (float) r0
                float r1 = (float) r12
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r8 = r32
                r7.drawCircle(r0, r1, r8, r2)
                goto L_0x05aa
            L_0x0590:
                r40 = r8
                r43 = r9
                r38 = r10
                r45 = r13
                r39 = r15
                r8 = r32
                int r0 = r6.slideDelta
                int r0 = r0 + r11
                float r0 = (float) r0
                float r1 = (float) r12
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r7.drawCircle(r0, r1, r8, r2)
            L_0x05aa:
                r53.save()
                r0 = 1065353216(0x3var_, float:1.0)
                float r9 = r0 - r20
                int r1 = r6.slideDelta
                float r1 = (float) r1
                r2 = 0
                r7.translate(r1, r2)
                float r4 = r6.progressToSendButton
                float r1 = r0 - r14
                float r1 = r1 * r9
                float r1 = r1 * r33
                int r5 = (int) r1
                r0 = r52
                r1 = r53
                r10 = r18
                r2 = r10
                r13 = r34
                r3 = r13
                r15 = r9
                r9 = r35
                r0.drawIconInternal(r1, r2, r3, r4, r5)
                r53.restore()
                goto L_0x05f6
            L_0x05d5:
                r40 = r8
                r43 = r9
                r38 = r10
                r45 = r13
                r39 = r15
                r8 = r32
                r13 = r34
                r9 = r35
                goto L_0x05f6
            L_0x05e6:
                r40 = r8
                r43 = r9
                r38 = r10
                r45 = r13
                r39 = r15
                r8 = r32
                r13 = r34
                r9 = r35
            L_0x05f6:
                boolean r0 = r52.isSendButtonVisible()
                r1 = 1108344832(0x42100000, float:36.0)
                r2 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x0670
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r0 = (float) r0
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 + r31
                r4 = 1106247680(0x41var_, float:30.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                r5 = 1065353216(0x3var_, float:1.0)
                float r15 = r5 - r30
                float r4 = r4 * r15
                float r3 = r3 + r4
                float r3 = r3 - r45
                r4 = 1096810496(0x41600000, float:14.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r4 = r4 * r40
                float r3 = r3 + r4
                float r4 = r0 / r24
                float r4 = r4 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r4 = r4 - r5
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r4 = r4 + r5
                float r5 = r0 / r24
                float r5 = r5 + r3
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
                float r5 = r5 - r15
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r5 = r5 + r15
                int r15 = (r40 > r22 ? 1 : (r40 == r22 ? 0 : -1))
                if (r15 <= 0) goto L_0x0647
                r15 = 1065353216(0x3var_, float:1.0)
                goto L_0x0649
            L_0x0647:
                float r15 = r40 / r22
            L_0x0649:
                r22 = 1091567616(0x41100000, float:9.0)
                r26 = 1065353216(0x3var_, float:1.0)
                float r32 = r26 - r40
                float r32 = r32 * r22
                float r2 = r6.snapAnimationProgress
                float r34 = r26 - r2
                float r32 = r32 * r34
                r34 = 1097859072(0x41700000, float:15.0)
                float r2 = r2 * r34
                float r34 = r26 - r15
                float r2 = r2 * r34
                float r32 = r32 - r2
                r2 = r40
                r38 = r2
                r34 = r5
                r15 = r45
                r5 = r3
                r3 = r32
                r32 = r0
                goto L_0x06e7
            L_0x0670:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r2 = 1096810496(0x41600000, float:14.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r2 = r2 * r40
                int r2 = (int) r2
                int r0 = r0 + r2
                float r0 = (float) r0
                r2 = 1114636288(0x42700000, float:60.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r2 = r2 + r31
                r3 = 1106247680(0x41var_, float:30.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                r4 = 1065353216(0x3var_, float:1.0)
                float r5 = r4 - r30
                float r3 = r3 * r5
                int r3 = (int) r3
                float r3 = (float) r3
                float r2 = r2 + r3
                r15 = r45
                int r3 = (int) r15
                float r3 = (float) r3
                float r2 = r2 - r3
                float r3 = r6.idleProgress
                float r3 = r3 * r40
                r4 = 1090519040(0x41000000, float:8.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r5 = -r5
                float r5 = (float) r5
                float r3 = r3 * r5
                float r3 = r3 + r2
                float r2 = r0 / r24
                float r2 = r2 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r2 = r2 - r5
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r2 = r2 + r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r4 = r4 * r40
                float r4 = r4 + r2
                float r2 = r0 / r24
                float r2 = r2 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
                float r2 = r2 - r5
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r2 = r2 + r5
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r5 = r5 * r40
                float r5 = r5 + r2
                r2 = 1091567616(0x41100000, float:9.0)
                r26 = 1065353216(0x3var_, float:1.0)
                float r32 = r26 - r40
                float r32 = r32 * r2
                r2 = 0
                r6.snapAnimationProgress = r2
                r34 = r5
                r5 = r3
                r3 = r32
                r32 = r0
            L_0x06e7:
                boolean r0 = r6.showTooltip
                r35 = 1077936128(0x40400000, float:3.0)
                r36 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x0700
                long r41 = java.lang.System.currentTimeMillis()
                r37 = r3
                long r2 = r6.showTooltipStartTime
                long r41 = r41 - r2
                r2 = 200(0xc8, double:9.9E-322)
                int r44 = (r41 > r2 ? 1 : (r41 == r2 ? 0 : -1))
                if (r44 > 0) goto L_0x0709
                goto L_0x0702
            L_0x0700:
                r37 = r3
            L_0x0702:
                float r2 = r6.tooltipAlpha
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x089a
            L_0x0709:
                r2 = 1061997773(0x3f4ccccd, float:0.8)
                int r2 = (r40 > r2 ? 1 : (r40 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0723
                boolean r2 = r52.isSendButtonVisible()
                if (r2 != 0) goto L_0x0723
                float r2 = r6.exitTransition
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 != 0) goto L_0x0723
                float r2 = r6.transformToSeekbar
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x0726
            L_0x0723:
                r2 = 0
                r6.showTooltip = r2
            L_0x0726:
                boolean r2 = r6.showTooltip
                if (r2 == 0) goto L_0x0742
                float r2 = r6.tooltipAlpha
                r3 = 1065353216(0x3var_, float:1.0)
                int r26 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r26 == 0) goto L_0x0751
                float r0 = (float) r9
                float r0 = r0 / r27
                float r2 = r2 + r0
                r6.tooltipAlpha = r2
                int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r0 < 0) goto L_0x0751
                r6.tooltipAlpha = r3
                org.telegram.messenger.SharedConfig.increaseLockRecordAudioVideoHintShowed()
                goto L_0x0751
            L_0x0742:
                float r0 = r6.tooltipAlpha
                float r2 = (float) r9
                float r2 = r2 / r27
                float r0 = r0 - r2
                r6.tooltipAlpha = r0
                r2 = 0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x0751
                r6.tooltipAlpha = r2
            L_0x0751:
                float r0 = r6.tooltipAlpha
                float r0 = r0 * r33
                int r2 = (int) r0
                android.graphics.drawable.Drawable r0 = r6.tooltipBackground
                r0.setAlpha(r2)
                android.graphics.drawable.Drawable r0 = r6.tooltipBackgroundArrow
                r0.setAlpha(r2)
                android.text.TextPaint r0 = r6.tooltipPaint
                r0.setAlpha(r2)
                android.text.StaticLayout r0 = r6.tooltipLayout
                if (r0 == 0) goto L_0x0895
                r53.save()
                android.graphics.RectF r0 = r6.rectF
                int r3 = r52.getMeasuredWidth()
                float r3 = (float) r3
                int r1 = r52.getMeasuredHeight()
                float r1 = (float) r1
                r44 = r9
                r9 = 0
                r0.set(r9, r9, r3, r1)
                int r0 = r52.getMeasuredWidth()
                float r0 = (float) r0
                float r1 = r6.tooltipWidth
                float r0 = r0 - r1
                r1 = 1110441984(0x42300000, float:44.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                float r0 = r0 - r1
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
                r7.translate(r0, r1)
                android.graphics.drawable.Drawable r0 = r6.tooltipBackground
                r1 = 1090519040(0x41000000, float:8.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = -r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)
                int r3 = -r3
                float r9 = r6.tooltipWidth
                r10 = 1108344832(0x42100000, float:36.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r10
                float r9 = r9 + r10
                int r9 = (int) r9
                android.text.StaticLayout r10 = r6.tooltipLayout
                int r10 = r10.getHeight()
                float r10 = (float) r10
                float r23 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r10 = r10 + r23
                int r10 = (int) r10
                r0.setBounds(r1, r3, r9, r10)
                android.graphics.drawable.Drawable r0 = r6.tooltipBackground
                r0.draw(r7)
                android.text.StaticLayout r0 = r6.tooltipLayout
                r0.draw(r7)
                r53.restore()
                r53.save()
                float r0 = (float) r11
                r1 = 1099431936(0x41880000, float:17.0)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                android.text.StaticLayout r3 = r6.tooltipLayout
                int r3 = r3.getHeight()
                float r3 = (float) r3
                float r3 = r3 / r24
                float r1 = r1 + r3
                float r3 = r6.idleProgress
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                float r3 = r3 * r9
                float r1 = r1 - r3
                r7.translate(r0, r1)
                android.graphics.Path r0 = r6.path
                r0.reset()
                android.graphics.Path r0 = r6.path
                r1 = 1084227584(0x40a00000, float:5.0)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r1 = -r1
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                r0.setLastPoint(r1, r3)
                android.graphics.Path r0 = r6.path
                r1 = 0
                r0.lineTo(r1, r1)
                android.graphics.Path r0 = r6.path
                r1 = 1084227584(0x40a00000, float:5.0)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                r0.lineTo(r1, r3)
                android.graphics.Paint r0 = r6.p
                r1 = -1
                r0.setColor(r1)
                android.graphics.Paint r0 = r6.p
                r0.setAlpha(r2)
                android.graphics.Paint r0 = r6.p
                android.graphics.Paint$Style r1 = android.graphics.Paint.Style.STROKE
                r0.setStyle(r1)
                android.graphics.Paint r0 = r6.p
                android.graphics.Paint$Cap r1 = android.graphics.Paint.Cap.ROUND
                r0.setStrokeCap(r1)
                android.graphics.Paint r0 = r6.p
                android.graphics.Paint$Join r1 = android.graphics.Paint.Join.ROUND
                r0.setStrokeJoin(r1)
                android.graphics.Paint r0 = r6.p
                r1 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                r0.setStrokeWidth(r1)
                android.graphics.Path r0 = r6.path
                android.graphics.Paint r1 = r6.p
                r7.drawPath(r0, r1)
                r53.restore()
                r53.save()
                android.graphics.drawable.Drawable r1 = r6.tooltipBackgroundArrow
                int r0 = r1.getIntrinsicWidth()
                int r0 = r0 / 2
                int r3 = r11 - r0
                android.text.StaticLayout r0 = r6.tooltipLayout
                int r0 = r0.getHeight()
                float r0 = (float) r0
                r9 = 1101004800(0x41a00000, float:20.0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r9)
                float r0 = r0 + r10
                int r10 = (int) r0
                android.graphics.drawable.Drawable r0 = r6.tooltipBackgroundArrow
                int r0 = r0.getIntrinsicWidth()
                int r0 = r0 / 2
                int r0 = r0 + r11
                android.text.StaticLayout r9 = r6.tooltipLayout
                int r9 = r9.getHeight()
                float r9 = (float) r9
                r23 = 1101004800(0x41a00000, float:20.0)
                float r27 = org.telegram.messenger.AndroidUtilities.dpf2(r23)
                float r9 = r9 + r27
                int r9 = (int) r9
                r23 = r2
                android.graphics.drawable.Drawable r2 = r6.tooltipBackgroundArrow
                int r2 = r2.getIntrinsicHeight()
                int r9 = r9 + r2
                r1.setBounds(r3, r10, r0, r9)
                android.graphics.drawable.Drawable r0 = r6.tooltipBackgroundArrow
                r0.draw(r7)
                r53.restore()
                goto L_0x089c
            L_0x0895:
                r23 = r2
                r44 = r9
                goto L_0x089c
            L_0x089a:
                r44 = r9
            L_0x089c:
                r53.save()
                int r0 = r52.getMeasuredWidth()
                int r1 = r52.getMeasuredHeight()
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.FrameLayout r2 = r2.textFieldContainer
                int r2 = r2.getMeasuredHeight()
                int r1 = r1 - r2
                r2 = 0
                r7.clipRect(r2, r2, r0, r1)
                r0 = 0
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r3 = r2 - r1
                r9 = 0
                int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r3 == 0) goto L_0x08c6
                float r0 = r2 - r1
                r9 = r0
                goto L_0x08d6
            L_0x08c6:
                int r1 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
                if (r1 == 0) goto L_0x08cd
                r0 = r14
                r9 = r0
                goto L_0x08d6
            L_0x08cd:
                int r1 = (r20 > r9 ? 1 : (r20 == r9 ? 0 : -1))
                if (r1 == 0) goto L_0x08d5
                r0 = r20
                r9 = r0
                goto L_0x08d6
            L_0x08d5:
                r9 = r0
            L_0x08d6:
                float r0 = r6.slideToCancelProgress
                r1 = 1060320051(0x3var_, float:0.7)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 < 0) goto L_0x08f9
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x08e4
                goto L_0x08f9
            L_0x08e4:
                float r0 = r6.slideToCancelLockProgress
                r1 = 1065353216(0x3var_, float:1.0)
                int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r2 == 0) goto L_0x090f
                r2 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r0 = r0 + r2
                r6.slideToCancelLockProgress = r0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x090f
                r6.slideToCancelLockProgress = r1
                goto L_0x090f
            L_0x08f9:
                r0 = 0
                r6.showTooltip = r0
                float r0 = r6.slideToCancelLockProgress
                r1 = 0
                int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r2 == 0) goto L_0x090f
                r2 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r0 = r0 - r2
                r6.slideToCancelLockProgress = r0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 >= 0) goto L_0x090f
                r6.slideToCancelLockProgress = r1
            L_0x090f:
                r0 = 1116733440(0x42900000, float:72.0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r0 = r10 * r9
                r1 = 1101004800(0x41a00000, float:20.0)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r1 = r1 * r43
                r2 = 1065353216(0x3var_, float:1.0)
                float r3 = r2 - r9
                float r1 = r1 * r3
                float r0 = r0 - r1
                float r1 = r6.slideToCancelLockProgress
                float r1 = r2 - r1
                float r1 = r1 * r10
                float r0 = r0 + r1
                int r1 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r1 <= 0) goto L_0x0934
                r0 = r10
                r3 = r0
                goto L_0x0935
            L_0x0934:
                r3 = r0
            L_0x0935:
                r0 = 0
                r7.translate(r0, r3)
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                float r2 = r1 - r14
                float r0 = r0 * r2
                float r2 = r1 - r20
                float r0 = r0 * r2
                float r1 = r6.slideToCancelLockProgress
                float r2 = r0 * r1
                float r0 = (float) r11
                r7.scale(r2, r2, r0, r4)
                android.graphics.RectF r0 = r6.rectF
                float r1 = (float) r11
                r17 = 1099956224(0x41900000, float:18.0)
                float r21 = org.telegram.messenger.AndroidUtilities.dpf2(r17)
                float r1 = r1 - r21
                r21 = r2
                float r2 = (float) r11
                float r23 = org.telegram.messenger.AndroidUtilities.dpf2(r17)
                float r2 = r2 + r23
                r23 = r9
                float r9 = r5 + r32
                r0.set(r1, r5, r2, r9)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.lockShadowDrawable
                android.graphics.RectF r1 = r6.rectF
                float r1 = r1.left
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                float r1 = r1 - r2
                int r1 = (int) r1
                android.graphics.RectF r2 = r6.rectF
                float r2 = r2.top
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                float r2 = r2 - r9
                int r2 = (int) r2
                android.graphics.RectF r9 = r6.rectF
                float r9 = r9.right
                float r27 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                float r9 = r9 + r27
                int r9 = (int) r9
                r27 = r5
                android.graphics.RectF r5 = r6.rectF
                float r5 = r5.bottom
                float r41 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                float r5 = r5 + r41
                int r5 = (int) r5
                r0.setBounds(r1, r2, r9, r5)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.lockShadowDrawable
                r0.draw(r7)
                android.graphics.RectF r0 = r6.rectF
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r17)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r17)
                android.graphics.Paint r5 = r6.lockBackgroundPaint
                r7.drawRoundRect(r0, r1, r2, r5)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.RectF r0 = r0.pauseRect
                android.graphics.RectF r1 = r6.rectF
                r0.set(r1)
                android.graphics.RectF r0 = r6.rectF
                float r1 = (float) r11
                r2 = 1086324736(0x40CLASSNAME, float:6.0)
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r1 = r1 - r5
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                r9 = 1065353216(0x3var_, float:1.0)
                float r17 = r9 - r38
                float r5 = r5 * r17
                float r1 = r1 - r5
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r17 = r9 - r38
                float r5 = r5 * r17
                float r5 = r4 - r5
                int r17 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r11 + r17
                float r2 = (float) r2
                float r17 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r26 = r9 - r38
                float r17 = r17 * r26
                float r2 = r2 + r17
                r17 = 1094713344(0x41400000, float:12.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
                float r9 = (float) r9
                float r9 = r9 + r4
                float r17 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                r26 = 1065353216(0x3var_, float:1.0)
                float r42 = r26 - r38
                float r17 = r17 * r42
                float r9 = r9 + r17
                r0.set(r1, r5, r2, r9)
                android.graphics.RectF r0 = r6.rectF
                float r9 = r0.bottom
                android.graphics.RectF r0 = r6.rectF
                float r5 = r0.centerX()
                android.graphics.RectF r0 = r6.rectF
                float r2 = r0.centerY()
                r53.save()
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                r1 = 1065353216(0x3var_, float:1.0)
                float r17 = r1 - r40
                float r0 = r0 * r17
                r1 = 0
                r7.translate(r1, r0)
                r1 = r37
                r7.rotate(r1, r5, r2)
                android.graphics.RectF r0 = r6.rectF
                r17 = r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                r37 = r10
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r35)
                r35 = r14
                android.graphics.Paint r14 = r6.lockPaint
                r7.drawRoundRect(r0, r4, r10, r14)
                r0 = 1065353216(0x3var_, float:1.0)
                int r4 = (r38 > r0 ? 1 : (r38 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x0a55
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r14 = r0 - r38
                float r4 = r4 * r14
                android.graphics.Paint r10 = r6.lockBackgroundPaint
                r7.drawCircle(r5, r2, r4, r10)
            L_0x0a55:
                int r4 = (r38 > r0 ? 1 : (r38 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x0b4a
                android.graphics.RectF r0 = r6.rectF
                r4 = 1090519040(0x41000000, float:8.0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r14 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                r4 = 0
                r0.set(r4, r4, r10, r14)
                r53.save()
                int r0 = r52.getMeasuredWidth()
                float r0 = (float) r0
                float r10 = r3 + r9
                float r14 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                r26 = 1065353216(0x3var_, float:1.0)
                float r28 = r26 - r40
                float r14 = r14 * r28
                float r10 = r10 + r14
                r7.clipRect(r4, r4, r0, r10)
                float r0 = (float) r11
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r0 = r0 - r4
                r4 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r10 = r6.idleProgress
                float r14 = r26 - r10
                float r4 = r4 * r14
                float r4 = r4 * r40
                float r4 = r34 - r4
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r14 = r26 - r40
                float r10 = r10 * r14
                float r4 = r4 - r10
                r10 = 1094713344(0x41400000, float:12.0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
                float r10 = r10 * r38
                float r4 = r4 + r10
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r24)
                float r14 = r6.snapAnimationProgress
                float r10 = r10 * r14
                float r4 = r4 + r10
                r7.translate(r0, r4)
                r0 = 0
                int r4 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                if (r4 <= 0) goto L_0x0aca
                r0 = 1090519040(0x41000000, float:8.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r4 = (float) r4
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r10 = (float) r10
                r7.rotate(r1, r4, r10)
                goto L_0x0acc
            L_0x0aca:
                r0 = 1090519040(0x41000000, float:8.0)
            L_0x0acc:
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r14 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                r0 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r22 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                r24 = 1065353216(0x3var_, float:1.0)
                float r29 = r24 - r38
                float r22 = r22 * r29
                float r22 = r0 + r22
                android.graphics.Paint r0 = r6.lockOutlinePaint
                r24 = r0
                r0 = r53
                r29 = r1
                r1 = r4
                r41 = r2
                r2 = r10
                r10 = r3
                r3 = r14
                r14 = r17
                r4 = r22
                r17 = r5
                r22 = r27
                r5 = r24
                r0.drawLine(r1, r2, r3, r4, r5)
                android.graphics.RectF r1 = r6.rectF
                r2 = 0
                r3 = -1020002304(0xffffffffCLASSNAME, float:-180.0)
                r4 = 0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0.drawArc(r1, r2, r3, r4, r5)
                r1 = 0
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                r3 = 0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r5 = r6.idleProgress
                float r4 = r4 * r5
                float r4 = r4 * r40
                boolean r5 = r52.isSendButtonVisible()
                r16 = 1
                r5 = r5 ^ 1
                float r5 = (float) r5
                float r4 = r4 * r5
                float r0 = r0 + r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r36)
                float r5 = r6.snapAnimationProgress
                float r4 = r4 * r5
                r5 = 1065353216(0x3var_, float:1.0)
                float r16 = r5 - r40
                float r4 = r4 * r16
                float r4 = r4 + r0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0 = r53
                r0.drawLine(r1, r2, r3, r4, r5)
                r53.restore()
                goto L_0x0b55
            L_0x0b4a:
                r29 = r1
                r41 = r2
                r10 = r3
                r14 = r17
                r22 = r27
                r17 = r5
            L_0x0b55:
                r53.restore()
                r53.restore()
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x0b9e
                int r0 = r6.slideDelta
                int r0 = r0 + r11
                float r0 = (float) r0
                float r1 = (float) r12
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r7.drawCircle(r0, r1, r8, r2)
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x0b7c
                float r0 = r6.slideToCancelProgress
                r1 = 1065353216(0x3var_, float:1.0)
                float r0 = r1 - r0
                goto L_0x0b80
            L_0x0b7c:
                r1 = 1065353216(0x3var_, float:1.0)
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x0b80:
                r16 = r0
                r53.save()
                int r0 = r6.slideDelta
                float r0 = (float) r0
                r1 = 0
                r7.translate(r0, r1)
                float r4 = r6.progressToSendButton
                float r0 = r16 * r33
                int r5 = (int) r0
                r0 = r52
                r1 = r53
                r2 = r18
                r3 = r13
                r0.drawIconInternal(r1, r2, r3, r4, r5)
                r53.restore()
            L_0x0b9e:
                r6.drawingCircleRadius = r8
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        public void drawIcon(Canvas canvas, int cx, int cy, float alpha) {
            Drawable drawable;
            Drawable replaceDrawable = null;
            if (isSendButtonVisible()) {
                if (this.progressToSendButton != 1.0f) {
                    replaceDrawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
                }
                drawable = ChatActivityEnterView.this.sendDrawable;
            } else {
                drawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            }
            ChatActivityEnterView.this.sendRect.set(cx - (drawable.getIntrinsicWidth() / 2), cy - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + cx, (drawable.getIntrinsicHeight() / 2) + cy);
            drawable.setBounds(ChatActivityEnterView.this.sendRect);
            if (replaceDrawable != null) {
                replaceDrawable.setBounds(cx - (replaceDrawable.getIntrinsicWidth() / 2), cy - (replaceDrawable.getIntrinsicHeight() / 2), (replaceDrawable.getIntrinsicWidth() / 2) + cx, (replaceDrawable.getIntrinsicHeight() / 2) + cy);
            }
            drawIconInternal(canvas, drawable, replaceDrawable, this.progressToSendButton, (int) (255.0f * alpha));
        }

        private void drawIconInternal(Canvas canvas, Drawable drawable, Drawable replaceDrawable, float progressToSendButton2, int alpha) {
            float f = 0.0f;
            if (progressToSendButton2 == 0.0f || progressToSendButton2 == 1.0f || replaceDrawable == null) {
                boolean z = this.canceledByGesture;
                if (z && this.slideToCancelProgress == 1.0f) {
                    (ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.videoSendButton : ChatActivityEnterView.this.audioSendButton).setAlpha(1.0f);
                    setVisibility(8);
                } else if (z && this.slideToCancelProgress < 1.0f) {
                    Drawable outlineDrawable = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                    outlineDrawable.setBounds(drawable.getBounds());
                    float f2 = this.slideToCancelProgress;
                    if (f2 >= 0.93f) {
                        f = 255.0f * ((f2 - 0.93f) / 0.07f);
                    }
                    int a = (int) f;
                    outlineDrawable.setAlpha(a);
                    outlineDrawable.draw(canvas);
                    outlineDrawable.setAlpha(255);
                    drawable.setAlpha(255 - a);
                    drawable.draw(canvas);
                } else if (!z) {
                    drawable.setAlpha(alpha);
                    drawable.draw(canvas);
                }
            } else {
                canvas.save();
                canvas.scale(progressToSendButton2, progressToSendButton2, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                drawable.setAlpha((int) (((float) alpha) * progressToSendButton2));
                drawable.draw(canvas);
                canvas.restore();
                canvas.save();
                canvas.scale(1.0f - progressToSendButton2, 1.0f - progressToSendButton2, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                replaceDrawable.setAlpha((int) (((float) alpha) * (1.0f - progressToSendButton2)));
                replaceDrawable.draw(canvas);
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean dispatchHoverEvent(MotionEvent event) {
            return super.dispatchHoverEvent(event) || this.virtualViewHelper.dispatchHoverEvent(event);
        }

        public void setTransformToSeekbar(float value) {
            this.transformToSeekbar = value;
            invalidate();
        }

        public float getTransformToSeekbarProgressStep3() {
            return this.progressToSeekbarStep3;
        }

        public float getExitTransition() {
            return this.exitTransition;
        }

        public void setExitTransition(float exitTransition2) {
            this.exitTransition = exitTransition2;
            invalidate();
        }

        public void updateColors() {
            ChatActivityEnterView.this.paint.setColor(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoiceBackground"));
            this.tinyWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoiceBackground"), 38));
            this.bigWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoiceBackground"), 76));
            this.tooltipPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_gifSaveHintText"));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.getThemedColor("chat_gifSaveHintBackground"));
            this.tooltipBackgroundArrow.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
            this.lockBackgroundPaint.setColor(ChatActivityEnterView.this.getThemedColor("key_chat_messagePanelVoiceLockBackground"));
            this.lockPaint.setColor(ChatActivityEnterView.this.getThemedColor("key_chat_messagePanelVoiceLock"));
            this.lockOutlinePaint.setColor(ChatActivityEnterView.this.getThemedColor("key_chat_messagePanelVoiceLock"));
            this.paintAlpha = ChatActivityEnterView.this.paint.getAlpha();
        }

        public void showTooltipIfNeed() {
            if (SharedConfig.lockRecordAudioVideoHint < 3) {
                this.showTooltip = true;
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        public float getSlideToCancelProgress() {
            return this.slideToCancelProgress;
        }

        public void setSlideToCancelProgress(float slideToCancelProgress2) {
            this.slideToCancelProgress = slideToCancelProgress2;
            float distance = ((float) getMeasuredWidth()) * 0.35f;
            if (distance > ((float) AndroidUtilities.dp(140.0f))) {
                distance = (float) AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-distance) * (1.0f - slideToCancelProgress2));
            invalidate();
        }

        public void canceledByGesture() {
            this.canceledByGesture = true;
        }

        public void setMovingCords(float x, float y) {
            float f = this.lastMovingX;
            float f2 = (x - f) * (x - f);
            float f3 = this.lastMovingY;
            float delta = f2 + ((y - f3) * (y - f3));
            this.lastMovingY = y;
            this.lastMovingX = x;
            if (this.showTooltip && this.tooltipAlpha == 0.0f && delta > this.touchSlop) {
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        public void showWaves(boolean b, boolean animated) {
            if (!animated) {
                this.wavesEnterAnimation = b ? 1.0f : 0.5f;
            }
            this.showWaves = b;
        }

        public void drawWaves(Canvas canvas, float cx, float cy, float additionalScale) {
            float enter = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnterAnimation);
            float f = this.slideToCancelProgress;
            float slideToCancelProgress1 = f > 0.7f ? 1.0f : f / 0.7f;
            canvas.save();
            float s = this.scale * slideToCancelProgress1 * enter * (BlobDrawable.SCALE_BIG_MIN + (this.bigWaveDrawable.amplitude * 1.4f)) * additionalScale;
            canvas.scale(s, s, cx, cy);
            BlobDrawable blobDrawable = this.bigWaveDrawable;
            blobDrawable.draw(cx, cy, canvas, blobDrawable.paint);
            canvas.restore();
            float s2 = this.scale * slideToCancelProgress1 * enter * (BlobDrawable.SCALE_SMALL_MIN + (this.tinyWaveDrawable.amplitude * 1.4f)) * additionalScale;
            canvas.save();
            canvas.scale(s2, s2, cx, cy);
            BlobDrawable blobDrawable2 = this.tinyWaveDrawable;
            blobDrawable2.draw(cx, cy, canvas, blobDrawable2.paint);
            canvas.restore();
        }

        private class VirtualViewHelper extends ExploreByTouchHelper {
            private int[] coords = new int[2];

            public VirtualViewHelper(View host) {
                super(host);
            }

            /* access modifiers changed from: protected */
            public int getVirtualViewAt(float x, float y) {
                if (!RecordCircle.this.isSendButtonVisible()) {
                    return -1;
                }
                if (ChatActivityEnterView.this.sendRect.contains((int) x, (int) y)) {
                    return 1;
                }
                if (ChatActivityEnterView.this.pauseRect.contains(x, y)) {
                    return 2;
                }
                if (ChatActivityEnterView.this.slideText == null || ChatActivityEnterView.this.slideText.cancelRect == null) {
                    return -1;
                }
                AndroidUtilities.rectTmp.set(ChatActivityEnterView.this.slideText.cancelRect);
                ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                RectF rectF = AndroidUtilities.rectTmp;
                int[] iArr = this.coords;
                rectF.offset((float) iArr[0], (float) iArr[1]);
                ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                RectF rectF2 = AndroidUtilities.rectTmp;
                int[] iArr2 = this.coords;
                rectF2.offset((float) (-iArr2[0]), (float) (-iArr2[1]));
                if (AndroidUtilities.rectTmp.contains(x, y)) {
                    return 3;
                }
                return -1;
            }

            /* access modifiers changed from: protected */
            public void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(1);
                    list.add(2);
                    list.add(3);
                }
            }

            /* access modifiers changed from: protected */
            public void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat info) {
                if (id == 1) {
                    info.setBoundsInParent(ChatActivityEnterView.this.sendRect);
                    info.setText(LocaleController.getString("Send", NUM));
                } else if (id == 2) {
                    ChatActivityEnterView.this.rect.set((int) ChatActivityEnterView.this.pauseRect.left, (int) ChatActivityEnterView.this.pauseRect.top, (int) ChatActivityEnterView.this.pauseRect.right, (int) ChatActivityEnterView.this.pauseRect.bottom);
                    info.setBoundsInParent(ChatActivityEnterView.this.rect);
                    info.setText(LocaleController.getString("Stop", NUM));
                } else if (id == 3) {
                    if (!(ChatActivityEnterView.this.slideText == null || ChatActivityEnterView.this.slideText.cancelRect == null)) {
                        AndroidUtilities.rectTmp2.set(ChatActivityEnterView.this.slideText.cancelRect);
                        ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                        Rect rect = AndroidUtilities.rectTmp2;
                        int[] iArr = this.coords;
                        rect.offset(iArr[0], iArr[1]);
                        ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                        Rect rect2 = AndroidUtilities.rectTmp2;
                        int[] iArr2 = this.coords;
                        rect2.offset(-iArr2[0], -iArr2[1]);
                        info.setBoundsInParent(AndroidUtilities.rectTmp2);
                    }
                    info.setText(LocaleController.getString("Cancel", NUM));
                }
            }

            /* access modifiers changed from: protected */
            public boolean onPerformActionForVirtualView(int id, int action, Bundle args) {
                return true;
            }
        }
    }

    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, ChatActivity fragment, boolean isChat) {
        this(context, parent, fragment, isChat, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatActivityEnterView(android.app.Activity r40, org.telegram.ui.Components.SizeNotifierFrameLayout r41, org.telegram.ui.ChatActivity r42, boolean r43, org.telegram.ui.ActionBar.Theme.ResourcesProvider r44) {
        /*
            r39 = this;
            r7 = r39
            r8 = r40
            r9 = r41
            r10 = r42
            r11 = r44
            if (r10 != 0) goto L_0x000e
            r0 = 0
            goto L_0x0010
        L_0x000e:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r10.contentView
        L_0x0010:
            r7.<init>(r8, r0)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r0
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            r7.accountInstance = r0
            r13 = 1
            r7.lineCount = r13
            r14 = -1
            r7.currentLimit = r14
            org.telegram.ui.Components.ChatActivityEnterView$BotMenuButtonType r0 = org.telegram.ui.Components.ChatActivityEnterView.BotMenuButtonType.NO_BUTTON
            r7.botMenuButtonType = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.animationParamsX = r0
            org.telegram.ui.Components.ChatActivityEnterView$1 r0 = new org.telegram.ui.Components.ChatActivityEnterView$1
            r0.<init>()
            r7.mediaMessageButtonsDelegate = r0
            r0 = 2
            android.widget.ImageView[] r1 = new android.widget.ImageView[r0]
            r7.emojiButton = r1
            r7.currentPopupContentType = r14
            r7.currentEmojiIcon = r14
            r7.isPaused = r13
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7.startedDraggingX = r15
            r1 = 1117782016(0x42a00000, float:80.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r7.distCanMove = r1
            int[] r1 = new int[r0]
            r7.location = r1
            r7.messageWebPageSearch = r13
            r7.animatingContentType = r14
            r6 = 1065353216(0x3var_, float:1.0)
            r7.doneButtonEnabledProgress = r6
            r7.doneButtonEnabled = r13
            org.telegram.ui.Components.ChatActivityEnterView$2 r1 = new org.telegram.ui.Components.ChatActivityEnterView$2
            r1.<init>()
            r7.openKeyboardRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$3 r1 = new org.telegram.ui.Components.ChatActivityEnterView$3
            r1.<init>()
            r7.updateExpandabilityRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$4 r1 = new org.telegram.ui.Components.ChatActivityEnterView$4
            java.lang.Class<java.lang.Integer> r2 = java.lang.Integer.class
            java.lang.String r3 = "translationY"
            r1.<init>(r2, r3)
            r7.roundedTranslationYProperty = r1
            org.telegram.ui.Components.ChatActivityEnterView$5 r1 = new org.telegram.ui.Components.ChatActivityEnterView$5
            java.lang.Class<java.lang.Float> r2 = java.lang.Float.class
            java.lang.String r3 = "scale"
            r1.<init>(r2, r3)
            r7.recordCircleScale = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r13)
            r7.redDotPaint = r1
            org.telegram.ui.Components.ChatActivityEnterView$6 r1 = new org.telegram.ui.Components.ChatActivityEnterView$6
            r1.<init>()
            r7.onFinishInitCameraRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$7 r1 = new org.telegram.ui.Components.ChatActivityEnterView$7
            r1.<init>()
            r7.recordAudioVideoRunnable = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r13)
            r7.paint = r1
            android.graphics.RectF r1 = new android.graphics.RectF
            r1.<init>()
            r7.pauseRect = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.sendRect = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.rect = r1
            org.telegram.ui.Components.ChatActivityEnterView$8 r1 = new org.telegram.ui.Components.ChatActivityEnterView$8
            r1.<init>()
            r7.runEmojiPanelAnimation = r1
            r7.allowBlur = r13
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>()
            r7.backgroundPaint = r1
            r7.composeShadowAlpha = r6
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22
            r1.<init>(r7)
            r7.topViewUpdateListener = r1
            r7.botCommandLastPosition = r14
            r7.resourcesProvider = r11
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r7.getThemedColor(r1)
            r7.backgroundColor = r1
            r5 = 0
            r7.drawBlur = r5
            if (r43 == 0) goto L_0x00ee
            boolean r1 = org.telegram.messenger.SharedConfig.smoothKeyboard
            if (r1 == 0) goto L_0x00ee
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r1 != 0) goto L_0x00ee
            if (r10 == 0) goto L_0x00ec
            boolean r1 = r42.isInBubbleMode()
            if (r1 != 0) goto L_0x00ee
        L_0x00ec:
            r1 = 1
            goto L_0x00ef
        L_0x00ee:
            r1 = 0
        L_0x00ef:
            r7.smoothKeyboard = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r13)
            r7.dotPaint = r1
            java.lang.String r2 = "chat_emojiPanelNewTrending"
            int r2 = r7.getThemedColor(r2)
            r1.setColor(r2)
            r7.setFocusable(r13)
            r7.setFocusableInTouchMode(r13)
            r7.setWillNotDraw(r5)
            r7.setClipChildren(r5)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.recordStarted
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.recordStartError
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.recordStopped
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.recordProgressChanged
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.audioDidSent
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.audioRouteChanged
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.sendingMessagesChanged
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.audioRecordTooShort
            r1.addObserver(r7, r2)
            int r1 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.updateBotMenuButton
            r1.addObserver(r7, r2)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r2 = org.telegram.messenger.NotificationCenter.emojiLoaded
            r1.addObserver(r7, r2)
            r7.parentActivity = r8
            r7.parentFragment = r10
            if (r10 == 0) goto L_0x01bc
            int r1 = r42.getClassGuid()
            r7.recordingGuid = r1
        L_0x01bc:
            r7.sizeNotifierLayout = r9
            r9.setDelegate(r7)
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "send_by_enter"
            boolean r1 = r4.getBoolean(r1, r5)
            r7.sendByEnter = r1
            java.lang.String r1 = "view_animations"
            boolean r1 = r4.getBoolean(r1, r13)
            r7.configAnimationsEnabled = r1
            org.telegram.ui.Components.ChatActivityEnterView$9 r1 = new org.telegram.ui.Components.ChatActivityEnterView$9
            r1.<init>(r8)
            r7.textFieldContainer = r1
            r1.setClipChildren(r5)
            android.widget.FrameLayout r1 = r7.textFieldContainer
            r1.setClipToPadding(r5)
            android.widget.FrameLayout r1 = r7.textFieldContainer
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.setPadding(r5, r2, r5, r5)
            android.widget.FrameLayout r1 = r7.textFieldContainer
            r16 = -1
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 83
            r19 = 0
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r1, r2)
            org.telegram.ui.Components.ChatActivityEnterView$10 r1 = new org.telegram.ui.Components.ChatActivityEnterView$10
            r1.<init>(r8)
            r3 = r1
            r3.setClipChildren(r5)
            android.widget.FrameLayout r1 = r7.textFieldContainer
            r18 = 80
            r20 = 0
            r21 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r3, r2)
            r1 = 0
        L_0x021d:
            r2 = 4
            java.lang.String r14 = "listSelectorSDK21"
            r12 = 21
            java.lang.String r5 = "chat_messagePanelIcons"
            if (r1 >= r0) goto L_0x02bf
            android.widget.ImageView[] r0 = r7.emojiButton
            org.telegram.ui.Components.ChatActivityEnterView$11 r6 = new org.telegram.ui.Components.ChatActivityEnterView$11
            r6.<init>(r8)
            r0[r1] = r6
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            int r5 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r5, r15)
            r0.setColorFilter(r6)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r5)
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r12) goto L_0x025d
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            int r5 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5)
            r0.setBackgroundDrawable(r5)
        L_0x025d:
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r23 = 48
            r24 = 1111490560(0x42400000, float:48.0)
            r25 = 83
            r26 = 1077936128(0x40400000, float:3.0)
            r27 = 0
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r3.addView(r0, r5)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda3
            r5.<init>(r7)
            r0.setOnClickListener(r5)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r5 = 2131623982(0x7f0e002e, float:1.887513E38)
            java.lang.String r6 = "AccDescrEmojiButton"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setContentDescription(r5)
            if (r1 != r13) goto L_0x02b4
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r0.setVisibility(r2)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r2 = 0
            r0.setAlpha(r2)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r2 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r2)
            android.widget.ImageView[] r0 = r7.emojiButton
            r0 = r0[r1]
            r0.setScaleY(r2)
        L_0x02b4:
            int r1 = r1 + 1
            r0 = 2
            r5 = 0
            r6 = 1065353216(0x3var_, float:1.0)
            r14 = -1
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x021d
        L_0x02bf:
            r6 = 0
            r7.setEmojiButtonImage(r6, r6)
            org.telegram.ui.Components.NumberTextView r0 = new org.telegram.ui.Components.NumberTextView
            r0.<init>(r8)
            r7.captionLimitView = r0
            r15 = 8
            r0.setVisibility(r15)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            r1 = 15
            r0.setTextSize(r1)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            java.lang.String r1 = "windowBackgroundWhiteGrayText"
            int r1 = r7.getThemedColor(r1)
            r0.setTextColor(r1)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            r0.setCenterAlign(r13)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            r23 = 48
            r24 = 1101004800(0x41a00000, float:20.0)
            r25 = 85
            r26 = 1077936128(0x40400000, float:3.0)
            r27 = 0
            r28 = 0
            r29 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView$12 r1 = new org.telegram.ui.Components.ChatActivityEnterView$12
            r0 = r1
            r12 = r1
            r1 = r39
            r2 = r40
            r15 = r3
            r3 = r44
            r23 = r4
            r4 = r44
            r30 = r5
            r5 = r42
            r13 = 0
            r6 = r40
            r0.<init>(r2, r3, r4, r5, r6)
            r7.messageEditText = r12
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda53 r0 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda53
            r0.<init>(r7)
            r12.setDelegate(r0)
            org.telegram.ui.Components.EditTextCaption r0 = r7.messageEditText
            r0.setIncludeFontPadding(r13)
            org.telegram.ui.Components.EditTextCaption r0 = r7.messageEditText
            android.app.Activity r1 = r7.parentActivity
            android.view.Window r1 = r1.getWindow()
            android.view.View r1 = r1.getDecorView()
            r0.setWindowView(r1)
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            if (r0 == 0) goto L_0x0348
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getCurrentEncryptedChat()
            goto L_0x0349
        L_0x0348:
            r0 = 0
        L_0x0349:
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            boolean r2 = r39.supportsSendingNewEntities()
            r1.setAllowTextEntitiesIntersection(r2)
            r7.updateFieldHint(r13)
            r1 = 268435456(0x10000000, float:2.5243549E-29)
            if (r0 == 0) goto L_0x035c
            r2 = 16777216(0x1000000, float:2.3509887E-38)
            r1 = r1 | r2
        L_0x035c:
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r2.setImeOptions(r1)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            int r3 = r2.getInputType()
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r4 = 131072(0x20000, float:1.83671E-40)
            r3 = r3 | r4
            r2.setInputType(r3)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r2.setSingleLine(r13)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r3 = 6
            r2.setMaxLines(r3)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r3 = 1099956224(0x41900000, float:18.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r3 = 80
            r2.setGravity(r3)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r4 = 1093664768(0x41300000, float:11.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r13, r4, r13, r5)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r4 = 0
            r2.setBackgroundDrawable(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_messagePanelText"
            int r4 = r7.getThemedColor(r4)
            r2.setTextColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_messageLinkOut"
            int r4 = r7.getThemedColor(r4)
            r2.setLinkTextColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_inTextSelectionHighlight"
            int r4 = r7.getThemedColor(r4)
            r2.setHighlightColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_messagePanelHint"
            int r4 = r7.getThemedColor(r4)
            r2.setHintColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_messagePanelHint"
            int r4 = r7.getThemedColor(r4)
            r2.setHintTextColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_messagePanelCursor"
            int r4 = r7.getThemedColor(r4)
            r2.setCursorColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            java.lang.String r4 = "chat_TextSelectionCursor"
            int r4 = r7.getThemedColor(r4)
            r2.setHandlesColor(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            r31 = -1
            r32 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r33 = 80
            r34 = 1112539136(0x42500000, float:52.0)
            r35 = 0
            if (r43 == 0) goto L_0x0400
            r4 = 1112014848(0x42480000, float:50.0)
            r36 = 1112014848(0x42480000, float:50.0)
            goto L_0x0404
        L_0x0400:
            r4 = 1073741824(0x40000000, float:2.0)
            r36 = 1073741824(0x40000000, float:2.0)
        L_0x0404:
            r37 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r15.addView(r2, r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$13 r4 = new org.telegram.ui.Components.ChatActivityEnterView$13
            r4.<init>()
            r2.setOnKeyListener(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$14 r4 = new org.telegram.ui.Components.ChatActivityEnterView$14
            r4.<init>()
            r2.setOnEditorActionListener(r4)
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$15 r4 = new org.telegram.ui.Components.ChatActivityEnterView$15
            r4.<init>()
            r2.addTextChangedListener(r4)
            if (r43 == 0) goto L_0x0702
            org.telegram.ui.ChatActivity r12 = r7.parentFragment
            if (r12 == 0) goto L_0x04c7
            android.content.res.Resources r12 = r40.getResources()
            r3 = 2131165530(0x7var_a, float:1.794528E38)
            android.graphics.drawable.Drawable r3 = r12.getDrawable(r3)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.content.res.Resources r12 = r40.getResources()
            r2 = 2131165531(0x7var_b, float:1.7945282E38)
            android.graphics.drawable.Drawable r2 = r12.getDrawable(r2)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            r5 = r30
            int r13 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r4)
            r3.setColorFilter(r12)
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            java.lang.String r12 = "chat_recordedVoiceDot"
            int r12 = r7.getThemedColor(r12)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r12, r13)
            r2.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r4 = new org.telegram.ui.Components.CombinedDrawable
            r4.<init>(r3, r2)
            android.widget.ImageView r12 = new android.widget.ImageView
            r12.<init>(r8)
            r7.scheduledButton = r12
            r12.setImageDrawable(r4)
            android.widget.ImageView r12 = r7.scheduledButton
            r13 = 8
            r12.setVisibility(r13)
            android.widget.ImageView r12 = r7.scheduledButton
            r13 = 2131628084(0x7f0e1034, float:1.888345E38)
            java.lang.String r6 = "ScheduledMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r13)
            r12.setContentDescription(r6)
            android.widget.ImageView r6 = r7.scheduledButton
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r6.setScaleType(r12)
            int r6 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r6 < r12) goto L_0x04ad
            android.widget.ImageView r6 = r7.scheduledButton
            int r12 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12)
            r6.setBackgroundDrawable(r12)
        L_0x04ad:
            android.widget.ImageView r6 = r7.scheduledButton
            r29 = r0
            r12 = 85
            r13 = 48
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
            r15.addView(r6, r0)
            android.widget.ImageView r0 = r7.scheduledButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda10 r6 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda10
            r6.<init>(r7)
            r0.setOnClickListener(r6)
            goto L_0x04cb
        L_0x04c7:
            r29 = r0
            r5 = r30
        L_0x04cb:
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.attachLayout = r0
            r2 = 0
            r0.setOrientation(r2)
            android.widget.LinearLayout r0 = r7.attachLayout
            r0.setEnabled(r2)
            android.widget.LinearLayout r0 = r7.attachLayout
            r3 = 1111490560(0x42400000, float:48.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            r0.setPivotX(r3)
            android.widget.LinearLayout r0 = r7.attachLayout
            r0.setClipChildren(r2)
            android.widget.LinearLayout r0 = r7.attachLayout
            r2 = -2
            r3 = 85
            r4 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r4, (int) r3)
            r15.addView(r0, r6)
            org.telegram.ui.Components.BotCommandsMenuView r0 = new org.telegram.ui.Components.BotCommandsMenuView
            android.content.Context r2 = r39.getContext()
            r0.<init>(r2)
            r7.botCommandsMenuButton = r0
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r31 = -2
            r32 = 1107296256(0x42000000, float:32.0)
            r33 = 83
            r34 = 1092616192(0x41200000, float:10.0)
            r35 = 1090519040(0x41000000, float:8.0)
            r36 = 1092616192(0x41200000, float:10.0)
            r37 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r15.addView(r0, r2)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r3, r2, r3)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r4 = 1
            r0.setExpanded(r4, r3)
            androidx.recyclerview.widget.LinearLayoutManager r0 = new androidx.recyclerview.widget.LinearLayoutManager
            r0.<init>(r8)
            org.telegram.ui.Components.ChatActivityEnterView$16 r3 = new org.telegram.ui.Components.ChatActivityEnterView$16
            r3.<init>(r8)
            r7.botCommandsMenuContainer = r3
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r3.setLayoutManager(r0)
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r4 = new org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter
            r4.<init>()
            r7.botCommandsAdapter = r4
            r3.setAdapter(r4)
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.ChatActivityEnterView$17 r4 = new org.telegram.ui.Components.ChatActivityEnterView$17
            r4.<init>(r11, r10)
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.ChatActivityEnterView$18 r4 = new org.telegram.ui.Components.ChatActivityEnterView$18
            r4.<init>()
            r3.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r4)
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r7.botCommandsMenuContainer
            r4 = 0
            r3.setClipToPadding(r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r7.sizeNotifierLayout
            org.telegram.ui.Components.BotCommandsMenuContainer r4 = r7.botCommandsMenuContainer
            r6 = 14
            r12 = 80
            r13 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
            r3.addView(r4, r6, r2)
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r7.botCommandsMenuContainer
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.ChatActivityEnterView$19 r2 = new org.telegram.ui.Components.ChatActivityEnterView$19
            r2.<init>(r8, r7)
            r7.botWebViewMenuContainer = r2
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r7.sizeNotifierLayout
            r4 = 15
            r6 = 80
            r12 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r12, (int) r6)
            r3.addView(r2, r4, r13)
            org.telegram.ui.Components.BotWebViewMenuContainer r2 = r7.botWebViewMenuContainer
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.BotWebViewMenuContainer r2 = r7.botWebViewMenuContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda31 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda31
            r3.<init>(r7)
            r2.setOnDismissGlobalListener(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r8)
            r7.botButton = r2
            org.telegram.ui.Components.ReplaceableIconDrawable r3 = new org.telegram.ui.Components.ReplaceableIconDrawable
            r3.<init>(r8)
            r7.botButtonDrawable = r3
            r2.setImageDrawable(r3)
            org.telegram.ui.Components.ReplaceableIconDrawable r2 = r7.botButtonDrawable
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.ReplaceableIconDrawable r2 = r7.botButtonDrawable
            r3 = 2131165529(0x7var_, float:1.7945278E38)
            r4 = 0
            r2.setIcon((int) r3, (boolean) r4)
            android.widget.ImageView r2 = r7.botButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 < r3) goto L_0x05f0
            android.widget.ImageView r2 = r7.botButton
            int r3 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3)
            r2.setBackgroundDrawable(r3)
        L_0x05f0:
            android.widget.ImageView r2 = r7.botButton
            r3 = 8
            r2.setVisibility(r3)
            android.widget.ImageView r2 = r7.botButton
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r4 = 0
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r2, r4, r3, r4)
            android.widget.LinearLayout r2 = r7.attachLayout
            android.widget.ImageView r3 = r7.botButton
            r4 = 48
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r2.addView(r3, r6)
            android.widget.ImageView r2 = r7.botButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14
            r3.<init>(r7)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r8)
            r7.notifyButton = r2
            org.telegram.ui.Components.CrossOutDrawable r2 = new org.telegram.ui.Components.CrossOutDrawable
            r3 = 2131165540(0x7var_, float:1.79453E38)
            r2.<init>(r8, r3, r5)
            r7.notifySilentDrawable = r2
            android.widget.ImageView r3 = r7.notifyButton
            r3.setImageDrawable(r2)
            org.telegram.ui.Components.CrossOutDrawable r2 = r7.notifySilentDrawable
            boolean r3 = r7.silent
            r4 = 0
            r2.setCrossOut(r3, r4)
            android.widget.ImageView r2 = r7.notifyButton
            boolean r3 = r7.silent
            if (r3 == 0) goto L_0x0641
            r3 = 2131623973(0x7f0e0025, float:1.8875113E38)
            java.lang.String r4 = "AccDescrChanSilentOn"
            goto L_0x0646
        L_0x0641:
            r3 = 2131623972(0x7f0e0024, float:1.887511E38)
            java.lang.String r4 = "AccDescrChanSilentOff"
        L_0x0646:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setContentDescription(r3)
            android.widget.ImageView r2 = r7.notifyButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r7.notifyButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 < r3) goto L_0x0677
            android.widget.ImageView r2 = r7.notifyButton
            int r3 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3)
            r2.setBackgroundDrawable(r3)
        L_0x0677:
            android.widget.ImageView r2 = r7.notifyButton
            boolean r3 = r7.canWriteToChannel
            if (r3 == 0) goto L_0x0689
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r7.delegate
            if (r3 == 0) goto L_0x0687
            boolean r3 = r3.hasScheduledMessages()
            if (r3 != 0) goto L_0x0689
        L_0x0687:
            r3 = 0
            goto L_0x068b
        L_0x0689:
            r3 = 8
        L_0x068b:
            r2.setVisibility(r3)
            android.widget.LinearLayout r2 = r7.attachLayout
            android.widget.ImageView r3 = r7.notifyButton
            r4 = 48
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r2.addView(r3, r6)
            android.widget.ImageView r2 = r7.notifyButton
            org.telegram.ui.Components.ChatActivityEnterView$20 r3 = new org.telegram.ui.Components.ChatActivityEnterView$20
            r3.<init>(r8, r10)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r8)
            r7.attachButton = r2
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r7.attachButton
            r3 = 2131165527(0x7var_, float:1.7945274E38)
            r2.setImageResource(r3)
            android.widget.ImageView r2 = r7.attachButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 < r3) goto L_0x06dc
            android.widget.ImageView r2 = r7.attachButton
            int r3 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3)
            r2.setBackgroundDrawable(r3)
        L_0x06dc:
            android.widget.LinearLayout r2 = r7.attachLayout
            android.widget.ImageView r3 = r7.attachButton
            r4 = 48
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r2.addView(r3, r6)
            android.widget.ImageView r2 = r7.attachButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15
            r3.<init>(r7)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r7.attachButton
            r3 = 2131623960(0x7f0e0018, float:1.8875086E38)
            java.lang.String r4 = "AccDescrAttachButton"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setContentDescription(r3)
            goto L_0x0706
        L_0x0702:
            r29 = r0
            r5 = r30
        L_0x0706:
            org.telegram.ui.Components.SenderSelectView r0 = new org.telegram.ui.Components.SenderSelectView
            android.content.Context r2 = r39.getContext()
            r0.<init>(r2)
            r7.senderSelectView = r0
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18
            r2.<init>(r7, r8)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            r31 = 32
            r32 = 1107296256(0x42000000, float:32.0)
            r33 = 83
            r34 = 1092616192(0x41200000, float:10.0)
            r35 = 1090519040(0x41000000, float:8.0)
            r36 = 1092616192(0x41200000, float:10.0)
            r37 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r15.addView(r0, r2)
            org.telegram.ui.Components.ChatActivityEnterView$26 r0 = new org.telegram.ui.Components.ChatActivityEnterView$26
            r0.<init>(r8)
            r7.recordedAudioPanel = r0
            org.telegram.tgnet.TLRPC$TL_document r2 = r7.audioToSend
            if (r2 != 0) goto L_0x0745
            r2 = 8
            goto L_0x0746
        L_0x0745:
            r2 = 0
        L_0x0746:
            r0.setVisibility(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            r2 = 1
            r0.setFocusable(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            r0.setFocusableInTouchMode(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            r0.setClickable(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            r2 = 80
            r3 = -1
            r4 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r4, (int) r2)
            r15.addView(r0, r6)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r8)
            r7.recordDeleteImageView = r0
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            r2 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r3 = 28
            r4 = 28
            r0.setAnimation(r2, r3, r4)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r2 = 1
            r0.setInvalidateOnProgressSet(r2)
            r39.updateRecordedDeleteIconColors()
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            r2 = 2131625368(0x7f0e0598, float:1.8877942E38)
            java.lang.String r3 = "Delete"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x07ad
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            int r2 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
        L_0x07ad:
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            org.telegram.ui.Components.RLottieImageView r2 = r7.recordDeleteImageView
            r3 = 1111490560(0x42400000, float:48.0)
            r4 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r0.addView(r2, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda4
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.VideoTimelineView r0 = new org.telegram.ui.Components.VideoTimelineView
            r0.<init>(r8)
            r7.videoTimelineView = r0
            java.lang.String r2 = "chat_messagePanelVideoFrame"
            int r2 = r7.getThemedColor(r2)
            r0.setColor(r2)
            org.telegram.ui.Components.VideoTimelineView r0 = r7.videoTimelineView
            r2 = 1
            r0.setRoundFrames(r2)
            org.telegram.ui.Components.VideoTimelineView r0 = r7.videoTimelineView
            org.telegram.ui.Components.ChatActivityEnterView$27 r2 = new org.telegram.ui.Components.ChatActivityEnterView$27
            r2.<init>()
            r0.setDelegate(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            org.telegram.ui.Components.VideoTimelineView r2 = r7.videoTimelineView
            r31 = -1
            r32 = -1082130432(0xffffffffbvar_, float:-1.0)
            r33 = 19
            r34 = 1113587712(0x42600000, float:56.0)
            r35 = 0
            r36 = 1090519040(0x41000000, float:8.0)
            r37 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r0.addView(r2, r3)
            org.telegram.ui.Components.VideoTimelineView$TimeHintView r0 = new org.telegram.ui.Components.VideoTimelineView$TimeHintView
            r0.<init>(r8)
            org.telegram.ui.Components.VideoTimelineView r2 = r7.videoTimelineView
            r2.setTimeHintView(r0)
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = r7.sizeNotifierLayout
            r32 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r33 = 80
            r34 = 0
            r36 = 0
            r37 = 1112539136(0x42500000, float:52.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r2.addView(r0, r3)
            android.view.View r2 = new android.view.View
            r2.<init>(r8)
            r7.recordedAudioBackground = r2
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r4 = "chat_recordedVoiceBackground"
            int r4 = r7.getThemedColor(r4)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r3, r4)
            r2.setBackgroundDrawable(r3)
            android.widget.FrameLayout r2 = r7.recordedAudioPanel
            android.view.View r3 = r7.recordedAudioBackground
            r32 = 1108344832(0x42100000, float:36.0)
            r33 = 19
            r34 = 1111490560(0x42400000, float:48.0)
            r37 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r2.addView(r3, r4)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r2 = new org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView
            r2.<init>(r8)
            r7.recordedAudioSeekBar = r2
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r8)
            r3 = 0
            r2.setOrientation(r3)
            android.widget.FrameLayout r3 = r7.recordedAudioPanel
            r32 = 1107296256(0x42000000, float:32.0)
            r34 = 1119354880(0x42b80000, float:92.0)
            r36 = 1095761920(0x41500000, float:13.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r3.addView(r2, r4)
            org.telegram.ui.Components.MediaActionDrawable r3 = new org.telegram.ui.Components.MediaActionDrawable
            r3.<init>()
            r7.playPauseDrawable = r3
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r8)
            r7.recordedAudioPlayButton = r3
            android.graphics.Matrix r3 = new android.graphics.Matrix
            r3.<init>()
            r4 = 1061997773(0x3f4ccccd, float:0.8)
            r6 = 1061997773(0x3f4ccccd, float:0.8)
            r12 = 1103101952(0x41CLASSNAME, float:24.0)
            float r12 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            r13 = 1103101952(0x41CLASSNAME, float:24.0)
            float r13 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
            r3.postScale(r4, r6, r12, r13)
            android.widget.ImageView r4 = r7.recordedAudioPlayButton
            r4.setImageMatrix(r3)
            android.widget.ImageView r4 = r7.recordedAudioPlayButton
            org.telegram.ui.Components.MediaActionDrawable r6 = r7.playPauseDrawable
            r4.setImageDrawable(r6)
            android.widget.ImageView r4 = r7.recordedAudioPlayButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.MATRIX
            r4.setScaleType(r6)
            android.widget.ImageView r4 = r7.recordedAudioPlayButton
            r6 = 2131623955(0x7f0e0013, float:1.8875076E38)
            java.lang.String r12 = "AccActionPlay"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r4.setContentDescription(r6)
            android.widget.FrameLayout r4 = r7.recordedAudioPanel
            android.widget.ImageView r6 = r7.recordedAudioPlayButton
            r31 = 48
            r32 = 1111490560(0x42400000, float:48.0)
            r33 = 83
            r34 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r4.addView(r6, r12)
            android.widget.ImageView r4 = r7.recordedAudioPlayButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda5 r6 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda5
            r6.<init>(r7)
            r4.setOnClickListener(r6)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r8)
            r7.recordedAudioTimeTextView = r4
            java.lang.String r6 = "chat_messagePanelVoiceDuration"
            int r6 = r7.getThemedColor(r6)
            r4.setTextColor(r6)
            android.widget.TextView r4 = r7.recordedAudioTimeTextView
            r6 = 1095761920(0x41500000, float:13.0)
            r12 = 1
            r4.setTextSize(r12, r6)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r4 = r7.recordedAudioSeekBar
            r31 = 0
            r32 = 32
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 16
            r35 = 0
            r36 = 0
            r37 = 4
            r38 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r31, r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r4, r12)
            android.widget.TextView r4 = r7.recordedAudioTimeTextView
            r12 = 16
            r6 = 0
            r13 = -2
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r13, r6, r12)
            r2.addView(r4, r12)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r8)
            r7.recordPanel = r4
            r6 = 0
            r4.setClipChildren(r6)
            android.widget.FrameLayout r4 = r7.recordPanel
            r6 = 8
            r4.setVisibility(r6)
            android.widget.FrameLayout r4 = r7.recordPanel
            r6 = 1111490560(0x42400000, float:48.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r6)
            r15.addView(r4, r13)
            android.widget.FrameLayout r4 = r7.recordPanel
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23 r6 = org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23.INSTANCE
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r4 = new org.telegram.ui.Components.ChatActivityEnterView$SlideTextView
            r4.<init>(r8)
            r7.slideText = r4
            android.widget.FrameLayout r6 = r7.recordPanel
            r31 = -1
            r32 = -1082130432(0xffffffffbvar_, float:-1.0)
            r33 = 0
            r34 = 1110704128(0x42340000, float:45.0)
            r35 = 0
            r36 = 0
            r37 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r6.addView(r4, r12)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r8)
            r7.recordTimeContainer = r4
            r6 = 0
            r4.setOrientation(r6)
            android.widget.LinearLayout r4 = r7.recordTimeContainer
            r12 = 1095761920(0x41500000, float:13.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r4.setPadding(r13, r6, r6, r6)
            android.widget.LinearLayout r4 = r7.recordTimeContainer
            r4.setFocusable(r6)
            android.widget.FrameLayout r4 = r7.recordPanel
            android.widget.LinearLayout r6 = r7.recordTimeContainer
            r12 = 16
            r13 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
            r4.addView(r6, r12)
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r4 = new org.telegram.ui.Components.ChatActivityEnterView$RecordDot
            r4.<init>(r8)
            r7.recordDot = r4
            android.widget.LinearLayout r6 = r7.recordTimeContainer
            r31 = 28
            r32 = 28
            r33 = 16
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r6.addView(r4, r12)
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r4 = new org.telegram.ui.Components.ChatActivityEnterView$TimerView
            r4.<init>(r8)
            r7.recordTimerView = r4
            android.widget.LinearLayout r6 = r7.recordTimeContainer
            r31 = -1
            r32 = -1
            r34 = 6
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r6.addView(r4, r12)
            org.telegram.ui.Components.ChatActivityEnterView$28 r4 = new org.telegram.ui.Components.ChatActivityEnterView$28
            r4.<init>(r8)
            r7.sendButtonContainer = r4
            r6 = 0
            r4.setClipChildren(r6)
            android.widget.FrameLayout r4 = r7.sendButtonContainer
            r4.setClipToPadding(r6)
            android.widget.FrameLayout r4 = r7.textFieldContainer
            android.widget.FrameLayout r12 = r7.sendButtonContainer
            r30 = r0
            r6 = 48
            r13 = 85
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r13)
            r4.addView(r12, r0)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.audioVideoButtonContainer = r0
            r4 = 0
            r0.setSoundEffectsEnabled(r4)
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.widget.FrameLayout r4 = r7.audioVideoButtonContainer
            r12 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r12)
            r0.addView(r4, r13)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            r4 = 1
            r0.setFocusable(r4)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            r0.setImportantForAccessibility(r4)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda21 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda21
            r4.<init>(r7, r11)
            r0.setOnTouchListener(r4)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.audioSendButton = r0
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r6 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r6, r12)
            r0.setColorFilter(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            r4 = 2131165537(0x7var_, float:1.7945294E38)
            r0.setImageResource(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            r4 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 0
            r0.setPadding(r6, r6, r4, r6)
            android.widget.ImageView r0 = r7.audioSendButton
            r4 = 2131624107(0x7f0e00ab, float:1.8875384E38)
            java.lang.String r6 = "AccDescrVoiceMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setContentDescription(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            r4 = 1
            r0.setFocusable(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            r0.setImportantForAccessibility(r4)
            android.widget.ImageView r0 = r7.audioSendButton
            android.view.View$AccessibilityDelegate r4 = r7.mediaMessageButtonsDelegate
            r0.setAccessibilityDelegate(r4)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            android.widget.ImageView r4 = r7.audioSendButton
            r6 = 1111490560(0x42400000, float:48.0)
            r12 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r6)
            r0.addView(r4, r13)
            if (r43 == 0) goto L_0x0aba
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.videoSendButton = r0
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r6 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r6, r12)
            r0.setColorFilter(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            r4 = 2131165545(0x7var_, float:1.794531E38)
            r0.setImageResource(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            r4 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 0
            r0.setPadding(r6, r6, r4, r6)
            android.widget.ImageView r0 = r7.videoSendButton
            r4 = 2131624105(0x7f0e00a9, float:1.887538E38)
            java.lang.String r6 = "AccDescrVideoMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.setContentDescription(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            r4 = 1
            r0.setFocusable(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            r0.setImportantForAccessibility(r4)
            android.widget.ImageView r0 = r7.videoSendButton
            android.view.View$AccessibilityDelegate r4 = r7.mediaMessageButtonsDelegate
            r0.setAccessibilityDelegate(r4)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            android.widget.ImageView r4 = r7.videoSendButton
            r6 = 1111490560(0x42400000, float:48.0)
            r12 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r6)
            r0.addView(r4, r13)
        L_0x0aba:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = new org.telegram.ui.Components.ChatActivityEnterView$RecordCircle
            r0.<init>(r8)
            r7.recordCircle = r0
            r4 = 8
            r0.setVisibility(r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierLayout
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r4 = r7.recordCircle
            r31 = -1
            r32 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r33 = 80
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r0.addView(r4, r6)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.cancelBotButton = r0
            r4 = 4
            r0.setVisibility(r4)
            android.widget.ImageView r0 = r7.cancelBotButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            org.telegram.ui.Components.ChatActivityEnterView$29 r6 = new org.telegram.ui.Components.ChatActivityEnterView$29
            r6.<init>()
            r7.progressDrawable = r6
            r0.setImageDrawable(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            r6 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r12 = "Cancel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r0.setContentDescription(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            r6 = 0
            r0.setSoundEffectsEnabled(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            r6 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            r0.setScaleY(r6)
            android.widget.ImageView r0 = r7.cancelBotButton
            r6 = 0
            r0.setAlpha(r6)
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r0 < r6) goto L_0x0b37
            android.widget.ImageView r0 = r7.cancelBotButton
            int r6 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r6)
            r0.setBackgroundDrawable(r6)
        L_0x0b37:
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.widget.ImageView r6 = r7.cancelBotButton
            r12 = 1111490560(0x42400000, float:48.0)
            r13 = 48
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)
            r0.addView(r6, r4)
            android.widget.ImageView r0 = r7.cancelBotButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda6 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda6
            r4.<init>(r7)
            r0.setOnClickListener(r4)
            boolean r0 = r39.isInScheduleMode()
            if (r0 == 0) goto L_0x0b84
            android.content.res.Resources r0 = r40.getResources()
            r4 = 2131165542(0x7var_, float:1.7945304E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonDrawable = r0
            android.content.res.Resources r0 = r40.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonInverseDrawable = r0
            android.content.res.Resources r0 = r40.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.inactinveSendButtonDrawable = r0
            goto L_0x0bb1
        L_0x0b84:
            android.content.res.Resources r0 = r40.getResources()
            r4 = 2131165498(0x7var_a, float:1.7945215E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonDrawable = r0
            android.content.res.Resources r0 = r40.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonInverseDrawable = r0
            android.content.res.Resources r0 = r40.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.inactinveSendButtonDrawable = r0
        L_0x0bb1:
            org.telegram.ui.Components.ChatActivityEnterView$30 r0 = new org.telegram.ui.Components.ChatActivityEnterView$30
            r0.<init>(r8)
            r7.sendButton = r0
            r4 = 4
            r0.setVisibility(r4)
            java.lang.String r0 = "chat_messagePanelSend"
            int r0 = r7.getThemedColor(r0)
            android.view.View r4 = r7.sendButton
            r6 = 2131628175(0x7f0e108f, float:1.8883635E38)
            java.lang.String r12 = "Send"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r4.setContentDescription(r6)
            android.view.View r4 = r7.sendButton
            r6 = 0
            r4.setSoundEffectsEnabled(r6)
            android.view.View r4 = r7.sendButton
            r6 = 1036831949(0x3dcccccd, float:0.1)
            r4.setScaleX(r6)
            android.view.View r4 = r7.sendButton
            r4.setScaleY(r6)
            android.view.View r4 = r7.sendButton
            r6 = 0
            r4.setAlpha(r6)
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r4 < r6) goto L_0x0c0e
            android.view.View r4 = r7.sendButton
            r6 = 24
            int r12 = android.graphics.Color.red(r0)
            int r13 = android.graphics.Color.green(r0)
            r31 = r1
            int r1 = android.graphics.Color.blue(r0)
            int r1 = android.graphics.Color.argb(r6, r12, r13, r1)
            r6 = 1
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r6)
            r4.setBackgroundDrawable(r1)
            goto L_0x0CLASSNAME
        L_0x0c0e:
            r31 = r1
        L_0x0CLASSNAME:
            android.widget.FrameLayout r1 = r7.sendButtonContainer
            android.view.View r4 = r7.sendButton
            r6 = 1111490560(0x42400000, float:48.0)
            r12 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r6)
            r1.addView(r4, r13)
            android.view.View r1 = r7.sendButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda7
            r4.<init>(r7)
            r1.setOnClickListener(r4)
            android.view.View r1 = r7.sendButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20
            r4.<init>(r7)
            r1.setOnLongClickListener(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = new org.telegram.ui.ActionBar.SimpleTextView
            r1.<init>(r8)
            r7.slowModeButton = r1
            r4 = 18
            r1.setTextSize(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 4
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 0
            r1.setSoundEffectsEnabled(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 1036831949(0x3dcccccd, float:0.1)
            r1.setScaleX(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r1.setScaleY(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 0
            r1.setAlpha(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 0
            r1.setPadding(r6, r6, r4, r6)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            r4 = 21
            r1.setGravity(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            int r4 = r7.getThemedColor(r5)
            r1.setTextColor(r4)
            android.widget.FrameLayout r1 = r7.sendButtonContainer
            org.telegram.ui.ActionBar.SimpleTextView r4 = r7.slowModeButton
            r6 = 64
            r12 = 53
            r13 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r13, (int) r12)
            r1.addView(r4, r6)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda8 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda8
            r4.<init>(r7)
            r1.setOnClickListener(r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r7.slowModeButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19
            r4.<init>(r7)
            r1.setOnLongClickListener(r4)
            org.telegram.ui.Components.ChatActivityEnterView$31 r1 = new org.telegram.ui.Components.ChatActivityEnterView$31
            r1.<init>(r8)
            r7.expandStickersButton = r1
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            org.telegram.ui.Components.AnimatedArrowDrawable r4 = new org.telegram.ui.Components.AnimatedArrowDrawable
            int r5 = r7.getThemedColor(r5)
            r6 = 0
            r4.<init>(r5, r6)
            r7.stickersArrow = r4
            r1.setImageDrawable(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            r4 = 8
            r1.setVisibility(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            r4 = 1036831949(0x3dcccccd, float:0.1)
            r1.setScaleX(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            r1.setScaleY(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            r4 = 0
            r1.setAlpha(r4)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x0ce9
            android.widget.ImageView r1 = r7.expandStickersButton
            int r4 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4)
            r1.setBackgroundDrawable(r4)
        L_0x0ce9:
            android.widget.FrameLayout r1 = r7.sendButtonContainer
            android.widget.ImageView r4 = r7.expandStickersButton
            r5 = 1111490560(0x42400000, float:48.0)
            r6 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r1.addView(r4, r12)
            android.widget.ImageView r1 = r7.expandStickersButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda9 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda9
            r4.<init>(r7)
            r1.setOnClickListener(r4)
            android.widget.ImageView r1 = r7.expandStickersButton
            r4 = 2131623983(0x7f0e002f, float:1.8875133E38)
            java.lang.String r5 = "AccDescrExpandPanel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.doneButtonContainer = r1
            r4 = 8
            r1.setVisibility(r4)
            android.widget.FrameLayout r1 = r7.textFieldContainer
            android.widget.FrameLayout r4 = r7.doneButtonContainer
            r5 = 85
            r6 = 48
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r5)
            r1.addView(r4, r5)
            android.widget.FrameLayout r1 = r7.doneButtonContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12
            r4.<init>(r7)
            r1.setOnClickListener(r4)
            r1 = 1098907648(0x41800000, float:16.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r4 = "chat_messagePanelSend"
            int r4 = r7.getThemedColor(r4)
            android.graphics.drawable.ShapeDrawable r1 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r1, r4)
            android.content.res.Resources r4 = r40.getResources()
            r5 = 2131165533(0x7var_d, float:1.7945286E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            r7.doneCheckDrawable = r4
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "chat_messagePanelVoicePressed"
            int r6 = r7.getThemedColor(r6)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r12)
            r4.setColorFilter(r5)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            r6 = 1065353216(0x3var_, float:1.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r12 = 0
            r5.<init>(r1, r4, r12, r6)
            r4 = r5
            r5 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1107296256(0x42000000, float:32.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCustomSize(r5, r6)
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r8)
            r7.doneButtonImage = r5
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r6)
            android.widget.ImageView r5 = r7.doneButtonImage
            r5.setImageDrawable(r4)
            android.widget.ImageView r5 = r7.doneButtonImage
            r6 = 2131625525(0x7f0e0635, float:1.887826E38)
            java.lang.String r12 = "Done"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r5.setContentDescription(r6)
            android.widget.FrameLayout r5 = r7.doneButtonContainer
            android.widget.ImageView r6 = r7.doneButtonImage
            r12 = 1111490560(0x42400000, float:48.0)
            r13 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)
            r5.addView(r6, r12)
            org.telegram.ui.Components.ContextProgressView r5 = new org.telegram.ui.Components.ContextProgressView
            r6 = 0
            r5.<init>(r8, r6)
            r7.doneButtonProgress = r5
            r6 = 4
            r5.setVisibility(r6)
            android.widget.FrameLayout r5 = r7.doneButtonContainer
            org.telegram.ui.Components.ContextProgressView r6 = r7.doneButtonProgress
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)
            r5.addView(r6, r12)
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            r6 = 1128792064(0x43480000, float:200.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.String r12 = "kbd_height"
            int r6 = r5.getInt(r12, r6)
            r7.keyboardHeight = r6
            r6 = 1128792064(0x43480000, float:200.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.String r12 = "kbd_height_land3"
            int r6 = r5.getInt(r12, r6)
            r7.keyboardHeightLand = r6
            r6 = 0
            r7.setRecordVideoButtonVisible(r6, r6)
            r7.checkSendButton(r6)
            r39.checkChannelRights()
            org.telegram.ui.Components.ChatActivityBotWebViewButton r6 = new org.telegram.ui.Components.ChatActivityBotWebViewButton
            r6.<init>(r8)
            r7.botWebViewButton = r6
            r12 = 8
            r6.setVisibility(r12)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r6 = r7.botWebViewButton
            org.telegram.ui.Components.BotCommandsMenuView r12 = r7.botCommandsMenuButton
            r6.setBotMenuButton(r12)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r6 = r7.botWebViewButton
            r12 = 80
            r13 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
            r15.addView(r6, r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.<init>(android.app.Activity, org.telegram.ui.Components.SizeNotifierFrameLayout, org.telegram.ui.ChatActivity, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m655lambda$new$1$orgtelegramuiComponentsChatActivityEnterView(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper2 != null && adjustPanLayoutHelper2.animationInProgress()) {
            return;
        }
        if (!hasBotWebView() || !botCommandsMenuIsShowing()) {
            boolean z = true;
            if (!isPopupShowing() || this.currentPopupContentType != 0) {
                showPopup(1, 0);
                EmojiView emojiView2 = this.emojiView;
                if (this.messageEditText.length() <= 0) {
                    z = false;
                }
                emojiView2.onOpen(z);
                return;
            }
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    emojiView3.closeSearch(false);
                }
                this.messageEditText.requestFocus();
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda28(this), 200);
                return;
            }
            openKeyboardInternal();
            return;
        }
        BotWebViewMenuContainer botWebViewMenuContainer2 = this.botWebViewMenuContainer;
        view.getClass();
        botWebViewMenuContainer2.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda26(view));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m654lambda$new$0$orgtelegramuiComponentsChatActivityEnterView() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m663lambda$new$2$orgtelegramuiComponentsChatActivityEnterView() {
        this.messageEditText.invalidateEffects();
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m672lambda$new$3$orgtelegramuiComponentsChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m675lambda$new$4$orgtelegramuiComponentsChatActivityEnterView(View view) {
        boolean open = !this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(open);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        if (hasBotWebView()) {
            if (!open) {
                this.botWebViewMenuContainer.dismiss();
            } else if (this.emojiViewVisible || this.botKeyboardViewVisible) {
                AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda40(this), 275);
                hidePopup(false);
            } else {
                openWebViewMenu();
            }
        } else if (open) {
            this.botCommandsMenuContainer.show();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m676lambda$new$5$orgtelegramuiComponentsChatActivityEnterView() {
        if (this.botButtonsMessageObject != null && TextUtils.isEmpty(this.messageEditText.getText()) && !this.botWebViewMenuContainer.hasSavedText()) {
            showPopup(1, 1);
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m677lambda$new$6$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (!hasBotWebView() || !botCommandsMenuIsShowing()) {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, false);
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            if (this.botReplyMarkup != null) {
                if (!isPopupShowing() || this.currentPopupContentType != 1) {
                    showPopup(1, 1);
                }
            } else if (this.hasBotCommands) {
                setFieldText("/");
                this.messageEditText.requestFocus();
                openKeyboard();
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, false, false);
                return;
            }
            return;
        }
        BotWebViewMenuContainer botWebViewMenuContainer2 = this.botWebViewMenuContainer;
        v.getClass();
        botWebViewMenuContainer2.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda26(v));
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m678lambda$new$7$orgtelegramuiComponentsChatActivityEnterView(View v) {
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper2 == null || !adjustPanLayoutHelper2.animationInProgress()) {
            this.delegate.didPressAttachButton();
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m660lambda$new$14$orgtelegramuiComponentsChatActivityEnterView(Activity context, View v) {
        int popupY2;
        View view = v;
        if (getTranslationY() != 0.0f) {
            this.onEmojiSearchClosed = new ChatActivityEnterView$$ExternalSyntheticLambda32(this);
            hidePopup(true, true);
            return;
        }
        if (this.delegate.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            int totalHeight = this.delegate.getContentViewHeight();
            int keyboard = this.delegate.measureKeyboardHeight();
            if (keyboard <= AndroidUtilities.dp(20.0f)) {
                totalHeight += keyboard;
            }
            if (this.emojiViewVisible) {
                totalHeight -= getEmojiPadding();
            }
            if (totalHeight < AndroidUtilities.dp(200.0f)) {
                this.onKeyboardClosed = new ChatActivityEnterView$$ExternalSyntheticLambda34(this);
                closeKeyboard();
                return;
            }
        }
        if (this.delegate.getSendAsPeers() != null) {
            try {
                view.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            int i = 0;
            if (senderSelectPopup != null) {
                senderSelectPopup.setPauseNotifications(false);
                this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation[0]);
                return;
            }
            MessagesController controller = MessagesController.getInstance(this.currentAccount);
            TLRPC.ChatFull chatFull = controller.getChatFull(-this.dialog_id);
            if (chatFull != null) {
                ViewGroup fl = this.parentFragment.getParentLayout();
                ChatActivity chatActivity = this.parentFragment;
                TLRPC.ChatFull chatFull2 = chatFull;
                AnonymousClass21 r13 = r1;
                TLRPC.TL_channels_sendAsPeers sendAsPeers = this.delegate.getSendAsPeers();
                ViewGroup fl2 = fl;
                ChatActivityEnterView$$ExternalSyntheticLambda54 chatActivityEnterView$$ExternalSyntheticLambda54 = new ChatActivityEnterView$$ExternalSyntheticLambda54(this, chatFull, controller);
                TLRPC.ChatFull chatFull3 = chatFull;
                final ViewGroup viewGroup = fl2;
                AnonymousClass21 r1 = new SenderSelectPopup(context, chatActivity, controller, chatFull2, sendAsPeers, chatActivityEnterView$$ExternalSyntheticLambda54) {
                    public void dismiss() {
                        if (ChatActivityEnterView.this.senderSelectPopupWindow != this) {
                            viewGroup.removeView(this.dimView);
                            super.dismiss();
                            return;
                        }
                        SenderSelectPopup unused = ChatActivityEnterView.this.senderSelectPopupWindow = null;
                        if (!this.runningCustomSprings) {
                            startDismissAnimation(new SpringAnimation[0]);
                            ChatActivityEnterView.this.senderSelectView.setProgress(0.0f, true, true);
                            return;
                        }
                        for (SpringAnimation springAnimation : this.springAnimations) {
                            springAnimation.cancel();
                        }
                        this.springAnimations.clear();
                        super.dismiss();
                    }
                };
                this.senderSelectPopupWindow = r13;
                r13.setPauseNotifications(true);
                this.senderSelectPopupWindow.setDismissAnimationDuration(220);
                this.senderSelectPopupWindow.setOutsideTouchable(true);
                this.senderSelectPopupWindow.setClippingEnabled(true);
                this.senderSelectPopupWindow.setFocusable(true);
                this.senderSelectPopupWindow.getContentView().measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                this.senderSelectPopupWindow.setInputMethodMode(2);
                this.senderSelectPopupWindow.setSoftInputMode(0);
                this.senderSelectPopupWindow.getContentView().setFocusableInTouchMode(true);
                this.senderSelectPopupWindow.setAnimationEnabled(false);
                int pad = -AndroidUtilities.dp(4.0f);
                int[] location2 = new int[2];
                int popupX2 = pad;
                if (AndroidUtilities.isTablet()) {
                    this.parentFragment.getFragmentView().getLocationInWindow(location2);
                    popupX2 += location2[0];
                }
                int totalHeight2 = this.delegate.getContentViewHeight();
                int height = this.senderSelectPopupWindow.getContentView().getMeasuredHeight();
                int keyboard2 = this.delegate.measureKeyboardHeight();
                if (keyboard2 <= AndroidUtilities.dp(20.0f)) {
                    totalHeight2 += keyboard2;
                }
                if (this.emojiViewVisible) {
                    totalHeight2 -= getEmojiPadding();
                }
                int shadowPad = AndroidUtilities.dp(1.0f);
                if (height < (((pad * 2) + totalHeight2) - (this.parentFragment.isInBubbleMode() ? 0 : AndroidUtilities.statusBarHeight)) - this.senderSelectPopupWindow.headerText.getMeasuredHeight()) {
                    getLocationInWindow(location2);
                    popupY2 = ((location2[1] - height) - pad) - AndroidUtilities.dp(2.0f);
                    fl2.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, popupY2 + pad + height + shadowPad + AndroidUtilities.dp(2.0f)));
                    MessagesController messagesController = controller;
                } else {
                    ViewGroup fl3 = fl2;
                    if (!this.parentFragment.isInBubbleMode()) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    popupY2 = i;
                    int off = AndroidUtilities.dp(14.0f);
                    this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height = ((totalHeight2 - popupY2) - off) - getHeightWithTopView();
                    MessagesController messagesController2 = controller;
                    fl3.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, off + popupY2 + this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height + shadowPad));
                }
                this.senderSelectPopupWindow.startShowAnimation();
                SenderSelectPopup senderSelectPopup2 = this.senderSelectPopupWindow;
                this.popupX = popupX2;
                this.popupY = popupY2;
                senderSelectPopup2.showAtLocation(view, 51, popupX2, popupY2);
                this.senderSelectView.setProgress(1.0f);
            }
        }
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m679lambda$new$8$orgtelegramuiComponentsChatActivityEnterView() {
        this.senderSelectView.callOnClick();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m680lambda$new$9$orgtelegramuiComponentsChatActivityEnterView() {
        this.senderSelectView.callOnClick();
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m659lambda$new$13$orgtelegramuiComponentsChatActivityEnterView(TLRPC.ChatFull chatFull, MessagesController controller, RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC.Peer peer) {
        TLRPC.User user;
        TLRPC.ChatFull chatFull2 = chatFull;
        MessagesController messagesController = controller;
        SenderSelectPopup.SenderView senderView2 = senderView;
        TLRPC.Peer peer2 = peer;
        if (this.senderSelectPopupWindow != null) {
            if (chatFull2 != null) {
                chatFull2.default_send_as = peer2;
                updateSendAsButton();
            }
            this.parentFragment.getMessagesController().setDefaultSendAs(this.dialog_id, peer2.user_id != 0 ? peer2.user_id : -peer2.channel_id);
            int[] loc = new int[2];
            boolean wasSelected = senderView2.avatar.isSelected();
            senderView2.avatar.getLocationInWindow(loc);
            senderView2.avatar.setSelected(true, true);
            SimpleAvatarView avatar = new SimpleAvatarView(getContext());
            if (peer2.channel_id != 0) {
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(peer2.channel_id));
                if (chat != null) {
                    avatar.setAvatar(chat);
                }
            } else if (!(peer2.user_id == 0 || (user = messagesController.getUser(Long.valueOf(peer2.user_id))) == null)) {
                avatar.setAvatar(user);
            }
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View ch = recyclerView.getChildAt(i);
                if ((ch instanceof SenderSelectPopup.SenderView) && ch != senderView2) {
                    ((SenderSelectPopup.SenderView) ch).avatar.setSelected(false, true);
                }
            }
            RecyclerView recyclerView2 = recyclerView;
            AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda43(this, avatar, loc, senderView2), wasSelected ? 0 : 200);
        }
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m658lambda$new$12$orgtelegramuiComponentsChatActivityEnterView(SimpleAvatarView avatar, int[] loc, SenderSelectPopup.SenderView senderView) {
        final SimpleAvatarView simpleAvatarView = avatar;
        if (this.senderSelectPopupWindow != null) {
            Dialog d = new Dialog(getContext(), NUM);
            FrameLayout aFrame = new FrameLayout(getContext());
            aFrame.addView(simpleAvatarView, LayoutHelper.createFrame(40, 40, 3));
            d.setContentView(aFrame);
            d.getWindow().setLayout(-1, -1);
            if (Build.VERSION.SDK_INT >= 21) {
                d.getWindow().clearFlags(1024);
                d.getWindow().clearFlags(67108864);
                d.getWindow().clearFlags(NUM);
                d.getWindow().addFlags(Integer.MIN_VALUE);
                d.getWindow().addFlags(512);
                d.getWindow().addFlags(131072);
                d.getWindow().getAttributes().windowAnimations = 0;
                d.getWindow().getDecorView().setSystemUiVisibility(1792);
                d.getWindow().setStatusBarColor(0);
                d.getWindow().setNavigationBarColor(0);
                AndroidUtilities.setLightStatusBar(d.getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
                if (Build.VERSION.SDK_INT >= 26) {
                    AndroidUtilities.setLightNavigationBar(d.getWindow(), AndroidUtilities.computePerceivedBrightness(Theme.getColor("windowBackgroundGray", (boolean[]) null, true)) >= 0.721f);
                }
            }
            if (Build.VERSION.SDK_INT >= 23) {
                this.popupX += getRootWindowInsets().getSystemWindowInsetLeft();
            }
            this.senderSelectView.getLocationInWindow(this.location);
            int[] iArr = this.location;
            float endX = (float) iArr[0];
            final float endY = (float) iArr[1];
            float off = (float) AndroidUtilities.dp(5.0f);
            float startX = ((float) (loc[0] + this.popupX)) + off + ((float) AndroidUtilities.dp(4.0f)) + 0.0f;
            float startY = ((float) (loc[1] + this.popupY)) + off + 0.0f;
            simpleAvatarView.setTranslationX(startX);
            simpleAvatarView.setTranslationY(startY);
            float endScale = ((float) this.senderSelectView.getLayoutParams().width) / ((float) AndroidUtilities.dp(40.0f));
            simpleAvatarView.setPivotX(0.0f);
            simpleAvatarView.setPivotY(0.0f);
            simpleAvatarView.setScaleX(0.75f);
            simpleAvatarView.setScaleY(0.75f);
            final SenderSelectPopup.SenderView senderView2 = senderView;
            avatar.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                public void onDraw() {
                    SimpleAvatarView simpleAvatarView = simpleAvatarView;
                    simpleAvatarView.post(new ChatActivityEnterView$22$$ExternalSyntheticLambda0(this, simpleAvatarView, senderView2));
                }

                /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ChatActivityEnterView$22  reason: not valid java name */
                public /* synthetic */ void m706xCLASSNAMEb77(SimpleAvatarView avatar, SenderSelectPopup.SenderView senderView) {
                    avatar.getViewTreeObserver().removeOnDrawListener(this);
                    senderView.avatar.setHideAvatar(true);
                }
            });
            d.show();
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            SpringAnimation[] springAnimationArr = new SpringAnimation[7];
            springAnimationArr[0] = new SpringAnimation(this.senderSelectView, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f));
            springAnimationArr[1] = new SpringAnimation(this.senderSelectView, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f));
            FrameLayout frameLayout = aFrame;
            SpringAnimation[] springAnimationArr2 = springAnimationArr;
            ChatActivityEnterView$$ExternalSyntheticLambda24 chatActivityEnterView$$ExternalSyntheticLambda24 = r0;
            float f = off;
            float startY2 = startY;
            float startY3 = endScale;
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            float f2 = endX;
            Dialog d2 = d;
            float startX2 = startX;
            ChatActivityEnterView$$ExternalSyntheticLambda24 chatActivityEnterView$$ExternalSyntheticLambda242 = new ChatActivityEnterView$$ExternalSyntheticLambda24(this, d, avatar, f2, endY);
            springAnimationArr2[2] = (SpringAnimation) new SpringAnimation(this.senderSelectView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(chatActivityEnterView$$ExternalSyntheticLambda24);
            springAnimationArr2[3] = (SpringAnimation) ((SpringAnimation) new SpringAnimation(simpleAvatarView, DynamicAnimation.TRANSLATION_X).setStartValue(MathUtils.clamp(startX2, endX - ((float) AndroidUtilities.dp(6.0f)), startX2))).setSpring(new SpringForce(endX).setStiffness(700.0f).setDampingRatio(0.75f)).setMinValue(endX - ((float) AndroidUtilities.dp(6.0f)));
            ChatActivityEnterView$$ExternalSyntheticLambda25 chatActivityEnterView$$ExternalSyntheticLambda25 = r0;
            ChatActivityEnterView$$ExternalSyntheticLambda25 chatActivityEnterView$$ExternalSyntheticLambda252 = new ChatActivityEnterView$$ExternalSyntheticLambda25(this, d2, avatar, f2, endY);
            springAnimationArr2[4] = (SpringAnimation) ((SpringAnimation) ((SpringAnimation) ((SpringAnimation) new SpringAnimation(simpleAvatarView, DynamicAnimation.TRANSLATION_Y).setStartValue(MathUtils.clamp(startY2, startY2, ((float) AndroidUtilities.dp(6.0f)) + endY))).setSpring(new SpringForce(endY).setStiffness(700.0f).setDampingRatio(0.75f)).setMaxValue(((float) AndroidUtilities.dp(6.0f)) + endY)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                boolean performedHapticFeedback = false;

                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    if (!this.performedHapticFeedback && value >= endY) {
                        this.performedHapticFeedback = true;
                        try {
                            simpleAvatarView.performHapticFeedback(3, 2);
                        } catch (Exception e) {
                        }
                    }
                }
            })).addEndListener(chatActivityEnterView$$ExternalSyntheticLambda25);
            float endScale2 = startY3;
            springAnimationArr2[5] = new SpringAnimation(simpleAvatarView, DynamicAnimation.SCALE_X).setSpring(new SpringForce(endScale2).setStiffness(1000.0f).setDampingRatio(1.0f));
            springAnimationArr2[6] = new SpringAnimation(simpleAvatarView, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(endScale2).setStiffness(1000.0f).setDampingRatio(1.0f));
            senderSelectPopup.startDismissAnimation(springAnimationArr2);
        }
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m656lambda$new$10$orgtelegramuiComponentsChatActivityEnterView(final Dialog d, SimpleAvatarView avatar, float endX, float endY, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (d.isShowing()) {
            avatar.setTranslationX(endX);
            avatar.setTranslationY(endY);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView access$12100 = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = d;
                    dialog.getClass();
                    access$12100.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100);
                    return true;
                }
            });
        }
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m657lambda$new$11$orgtelegramuiComponentsChatActivityEnterView(final Dialog d, SimpleAvatarView avatar, float endX, float endY, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (d.isShowing()) {
            avatar.setTranslationX(endX);
            avatar.setTranslationY(endY);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView access$12100 = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = d;
                    dialog.getClass();
                    access$12100.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100);
                    return true;
                }
            });
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m661lambda$new$15$orgtelegramuiComponentsChatActivityEnterView(View v) {
        AnimatorSet animatorSet = this.runningAnimationAudio;
        if (animatorSet == null || !animatorSet.isRunning()) {
            if (this.videoToSendMessageObject != null) {
                CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                this.delegate.needStartRecordVideo(2, true, 0);
            } else {
                MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
                if (playing != null && playing == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
            }
            if (this.audioToSendPath != null) {
                new File(this.audioToSendPath).delete();
            }
            hideRecordedAudioPanel(false);
            checkSendButton(true);
        }
    }

    /* renamed from: lambda$new$16$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m662lambda$new$16$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (this.audioToSend != null) {
            if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setIcon(1, true);
                MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
                return;
            }
            MediaController.getInstance().m104lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.audioToSendMessageObject);
            this.playPauseDrawable.setIcon(0, true);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        }
    }

    static /* synthetic */ boolean lambda$new$17(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$24$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ boolean m666lambda$new$24$orgtelegramuiComponentsChatActivityEnterView(Theme.ResourcesProvider resourcesProvider2, View view, MotionEvent motionEvent) {
        TLRPC.Chat chat;
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                boolean z2 = this.hasRecordVideo;
                if (!z2 || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!z2 || this.videoSendButton.getTag() == null) {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) ChatActivityEnterView$$ExternalSyntheticLambda50.INSTANCE, (Runnable) ChatActivityEnterView$$ExternalSyntheticLambda45.INSTANCE, resourcesProvider2);
                        }
                        MediaController instance = MediaController.getInstance();
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        instance.stopRecording(i, true, 0);
                        this.delegate.needStartRecordAudio(0);
                    } else {
                        this.delegate.needStartRecordVideo(1, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    this.messageTransitionIsRunning = false;
                    ChatActivityEnterView$$ExternalSyntheticLambda29 chatActivityEnterView$$ExternalSyntheticLambda29 = new ChatActivityEnterView$$ExternalSyntheticLambda29(this);
                    this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda29;
                    AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda29, 200);
                }
                return false;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || (chat = chatActivity.getCurrentChat()) == null || ChatObject.canSendMedia(chat)) {
                if (this.hasRecordVideo) {
                    this.calledRecordRunnable = false;
                    this.recordAudioVideoRunnableStarted = true;
                    AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150);
                } else {
                    this.recordAudioVideoRunnable.run();
                }
                return true;
            }
            this.delegate.needShowMediaBanHint();
            return false;
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 3 && this.recordingAudioVideo) {
                if (this.recordCircle.slideToCancelProgress < 0.7f) {
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    } else {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordInterface(5);
                } else {
                    boolean unused = this.recordCircle.sendButtonVisible = true;
                    startLockTransition();
                }
                return false;
            } else if (this.recordCircle.isSendButtonVisible() || this.recordedAudioPanel.getVisibility() == 0) {
                if (this.recordAudioVideoRunnableStarted) {
                    AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                }
                return false;
            } else {
                if (((double) ((((motionEvent.getX() + this.audioVideoButtonContainer.getX()) - this.startedDraggingX) / this.distCanMove) + 1.0f)) < 0.45d) {
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    } else {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordInterface(5);
                } else if (this.recordAudioVideoRunnableStarted) {
                    AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                    this.delegate.onSwitchRecordMode(this.videoSendButton.getTag() == null);
                    if (this.videoSendButton.getTag() == null) {
                        z = true;
                    }
                    setRecordVideoButtonVisible(z, true);
                    performHapticFeedback(3);
                    sendAccessibilityEvent(1);
                } else {
                    boolean z3 = this.hasRecordVideo;
                    if (!z3 || this.calledRecordRunnable) {
                        this.startedDraggingX = -1.0f;
                        if (!z3 || this.videoSendButton.getTag() == null) {
                            if (this.recordingAudioVideo && isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) ChatActivityEnterView$$ExternalSyntheticLambda51.INSTANCE, (Runnable) ChatActivityEnterView$$ExternalSyntheticLambda46.INSTANCE, resourcesProvider2);
                            }
                            this.delegate.needStartRecordAudio(0);
                            MediaController instance2 = MediaController.getInstance();
                            if (!isInScheduleMode()) {
                                i = 1;
                            }
                            instance2.stopRecording(i, true, 0);
                        } else {
                            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                            this.delegate.needStartRecordVideo(1, true, 0);
                        }
                        this.recordingAudioVideo = false;
                        this.messageTransitionIsRunning = false;
                        ChatActivityEnterView$$ExternalSyntheticLambda30 chatActivityEnterView$$ExternalSyntheticLambda30 = new ChatActivityEnterView$$ExternalSyntheticLambda30(this);
                        this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda30;
                        AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda30, 500);
                    }
                }
                return true;
            }
        } else if (motionEvent.getAction() != 2 || !this.recordingAudioVideo) {
            view.onTouchEvent(motionEvent);
            return true;
        } else {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (this.recordCircle.isSendButtonVisible()) {
                return false;
            }
            if (this.recordCircle.setLockTranslation(y) == 2) {
                startLockTransition();
                return false;
            }
            this.recordCircle.setMovingCords(x, y);
            if (this.startedDraggingX == -1.0f) {
                this.startedDraggingX = x;
                double measuredWidth = (double) this.sizeNotifierLayout.getMeasuredWidth();
                Double.isNaN(measuredWidth);
                float f = (float) (measuredWidth * 0.35d);
                this.distCanMove = f;
                if (f > ((float) AndroidUtilities.dp(140.0f))) {
                    this.distCanMove = (float) AndroidUtilities.dp(140.0f);
                }
            }
            float x2 = x + this.audioVideoButtonContainer.getX();
            float f2 = this.startedDraggingX;
            float alpha = ((x2 - f2) / this.distCanMove) + 1.0f;
            if (f2 != -1.0f) {
                if (alpha > 1.0f) {
                    alpha = 1.0f;
                } else if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                this.slideText.setSlideX(alpha);
                this.recordCircle.setSlideToCancelProgress(alpha);
            }
            if (alpha == 0.0f) {
                if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0, false, 0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordInterface(5);
            }
            return true;
        }
    }

    /* renamed from: lambda$new$20$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m664lambda$new$20$orgtelegramuiComponentsChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* renamed from: lambda$new$23$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m665lambda$new$23$orgtelegramuiComponentsChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* renamed from: lambda$new$25$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m667lambda$new$25$orgtelegramuiComponentsChatActivityEnterView(View view) {
        String text = this.messageEditText.getText().toString();
        int idx = text.indexOf(32);
        if (idx == -1 || idx == text.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(text.substring(0, idx + 1));
        }
    }

    /* renamed from: lambda$new$26$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m668lambda$new$26$orgtelegramuiComponentsChatActivityEnterView(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if ((animatorSet == null || !animatorSet.isRunning()) && this.moveToSendStateRunnable == null) {
                sendMessage();
            }
        }
    }

    /* renamed from: lambda$new$27$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m669lambda$new$27$orgtelegramuiComponentsChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    /* renamed from: lambda$new$28$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ boolean m670lambda$new$28$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(v);
    }

    /* renamed from: lambda$new$29$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m671lambda$new$29$orgtelegramuiComponentsChatActivityEnterView(View v) {
        EmojiView emojiView2;
        if (this.expandStickersButton.getVisibility() != 0 || this.expandStickersButton.getAlpha() != 1.0f || this.waitingForKeyboardOpen) {
            return;
        }
        if (!this.keyboardVisible || !this.messageEditText.isFocused()) {
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, true);
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging && (emojiView2 = this.emojiView) != null) {
                    emojiView2.showSearchField(false);
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (!this.stickersDragging) {
                setStickersExpanded(!this.stickersExpanded, true, false);
            }
        }
    }

    /* renamed from: lambda$new$30$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m673lambda$new$30$orgtelegramuiComponentsChatActivityEnterView(View view) {
        doneEditingMessage();
    }

    /* access modifiers changed from: private */
    public void openWebViewMenu() {
        Runnable onRequestWebView = new ChatActivityEnterView$$ExternalSyntheticLambda36(this);
        if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            onRequestWebView.run();
            return;
        }
        new AlertDialog.Builder((Context) this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(NUM, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialog_id)))))).setPositiveButton(LocaleController.getString(NUM), new ChatActivityEnterView$$ExternalSyntheticLambda59(this, onRequestWebView)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new ChatActivityEnterView$$ExternalSyntheticLambda2(this)).show();
    }

    /* renamed from: lambda$openWebViewMenu$31$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m686xCLASSNAMEe8() {
        AndroidUtilities.hideKeyboard(this);
        if (AndroidUtilities.isTablet()) {
            BotWebViewSheet webViewSheet = new BotWebViewSheet(getContext(), this.parentFragment.getResourceProvider());
            webViewSheet.setParentActivity(this.parentActivity);
            int i = this.currentAccount;
            long j = this.dialog_id;
            webViewSheet.requestWebView(i, j, j, this.botMenuWebViewTitle, this.botMenuWebViewUrl, 2, 0, false);
            webViewSheet.show();
            this.botCommandsMenuButton.setOpened(false);
            return;
        }
        this.botWebViewMenuContainer.show(this.currentAccount, this.dialog_id, this.botMenuWebViewUrl);
    }

    /* renamed from: lambda$openWebViewMenu$32$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m687xfdbfad07(Runnable onRequestWebView, DialogInterface dialog, int which) {
        onRequestWebView.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, this.dialog_id, true);
    }

    /* renamed from: lambda$openWebViewMenu$33$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m688xevar_(DialogInterface dialog) {
        if (!SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            this.botCommandsMenuButton.setOpened(false);
        }
    }

    public void setBotWebViewButtonOffsetX(float offset) {
        for (ImageView imageView : this.emojiButton) {
            imageView.setTranslationX(offset);
        }
        this.messageEditText.setTranslationX(offset);
        this.attachButton.setTranslationX(offset);
        this.audioSendButton.setTranslationX(offset);
        this.videoSendButton.setTranslationX(offset);
        ImageView imageView2 = this.botButton;
        if (imageView2 != null) {
            imageView2.setTranslationX(offset);
        }
    }

    public void setComposeShadowAlpha(float alpha) {
        this.composeShadowAlpha = alpha;
        invalidate();
    }

    public ChatActivityBotWebViewButton getBotWebViewButton() {
        return this.botWebViewButton;
    }

    public ChatActivity getParentFragment() {
        return this.parentFragment;
    }

    /* access modifiers changed from: private */
    public void checkBotMenu() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null) {
            boolean wasExpanded = botCommandsMenuView.expanded;
            this.botCommandsMenuButton.setExpanded(TextUtils.isEmpty(this.messageEditText.getText()) && !this.keyboardVisible && !this.waitingForKeyboardOpen && !isPopupShowing(), true);
            if (wasExpanded != this.botCommandsMenuButton.expanded) {
                beginDelayedTransition();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
        r0 = r1.parentFragment;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void forceSmoothKeyboard(boolean r2) {
        /*
            r1 = this;
            if (r2 == 0) goto L_0x0016
            boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
            if (r0 == 0) goto L_0x0016
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x0016
            org.telegram.ui.ChatActivity r0 = r1.parentFragment
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.isInBubbleMode()
            if (r0 != 0) goto L_0x0016
        L_0x0014:
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            r1.smoothKeyboard = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.forceSmoothKeyboard(boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int oldLineCount, int newLineCount) {
    }

    private void startLockTransition() {
        AnimatorSet animatorSet = new AnimatorSet();
        performHapticFeedback(3, 2);
        RecordCircle recordCircle2 = this.recordCircle;
        ObjectAnimator translate = ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation});
        translate.setStartDelay(100);
        translate.setDuration(350);
        ObjectAnimator snap = ObjectAnimator.ofFloat(this.recordCircle, "snapAnimationProgress", new float[]{1.0f});
        snap.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        snap.setDuration(250);
        SharedConfig.removeLockRecordAudioVideoHint();
        animatorSet.playTogether(new Animator[]{snap, translate, ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f}).setDuration(200), ObjectAnimator.ofFloat(this.slideText, "cancelToProgress", new float[]{1.0f})});
        animatorSet.start();
    }

    public int getBackgroundTop() {
        int t = getTop();
        View view = this.topView;
        if (view == null || view.getVisibility() != 0) {
            return t;
        }
        return t + this.topView.getLayoutParams().height;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean clip = child == this.topView || child == this.textFieldContainer;
        if (clip) {
            canvas.save();
            if (child == this.textFieldContainer) {
                int top = (int) (((float) (this.animatedTop + AndroidUtilities.dp(2.0f))) + this.chatSearchExpandOffset);
                View view = this.topView;
                if (view != null && view.getVisibility() == 0) {
                    top += this.topView.getHeight();
                }
                canvas.clipRect(0, top, getMeasuredWidth(), getMeasuredHeight());
            } else {
                canvas.clipRect(0, this.animatedTop, getMeasuredWidth(), this.animatedTop + child.getLayoutParams().height + AndroidUtilities.dp(2.0f));
            }
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (clip) {
            canvas.restore();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int top = (int) (((float) this.animatedTop) + (((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()) * (1.0f - this.composeShadowAlpha)));
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            top = (int) (((float) top) + ((1.0f - this.topViewEnterProgress) * ((float) this.topView.getLayoutParams().height)));
        }
        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + top;
        Theme.chat_composeShadowDrawable.setAlpha((int) (this.composeShadowAlpha * 255.0f));
        Theme.chat_composeShadowDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
        Theme.chat_composeShadowDrawable.draw(canvas);
        int bottom2 = (int) (((float) bottom) + this.chatSearchExpandOffset);
        if (this.allowBlur) {
            this.backgroundPaint.setColor(getThemedColor("chat_messagePanelBackground"));
            if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierLayout == null) {
                canvas.drawRect(0.0f, (float) bottom2, (float) getWidth(), (float) getHeight(), this.backgroundPaint);
                return;
            }
            AndroidUtilities.rectTmp2.set(0, bottom2, getWidth(), getHeight());
            this.sizeNotifierLayout.drawBlurRect(canvas, (float) getTop(), AndroidUtilities.rectTmp2, this.backgroundPaint, false);
            return;
        }
        canvas.drawRect(0.0f, (float) bottom2, (float) getWidth(), (float) getHeight(), getThemedPaint("paintChatComposeBackground"));
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: private */
    public boolean onSendLongClick(View view) {
        int y;
        if (isInScheduleMode()) {
            return false;
        }
        ChatActivity chatActivity = this.parentFragment;
        boolean self = chatActivity != null && UserObject.isUserSelf(chatActivity.getCurrentUser());
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() != 0 || ChatActivityEnterView.this.sendPopupWindow == null || !ChatActivityEnterView.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    v.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        return false;
                    }
                    ChatActivityEnterView.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ChatActivityEnterView$$ExternalSyntheticLambda47(this));
            this.sendPopupLayout.setShownFromBottom(false);
            ChatActivity chatActivity2 = this.parentFragment;
            boolean scheduleButtonValue = chatActivity2 != null && chatActivity2.canScheduleMessage();
            boolean sendWithoutSoundButtonValue = !self && (this.slowModeTimer <= 0 || isInScheduleMode());
            if (scheduleButtonValue) {
                ActionBarMenuSubItem scheduleButton = new ActionBarMenuSubItem(getContext(), true, !sendWithoutSoundButtonValue, this.resourcesProvider);
                if (self) {
                    scheduleButton.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                } else {
                    scheduleButton.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                }
                scheduleButton.setMinimumWidth(AndroidUtilities.dp(196.0f));
                scheduleButton.setOnClickListener(new ChatActivityEnterView$$ExternalSyntheticLambda16(this));
                this.sendPopupLayout.addView(scheduleButton, LayoutHelper.createLinear(-1, 48));
            }
            if (sendWithoutSoundButtonValue) {
                ActionBarMenuSubItem sendWithoutSoundButton = new ActionBarMenuSubItem(getContext(), !scheduleButtonValue, true, this.resourcesProvider);
                sendWithoutSoundButton.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                sendWithoutSoundButton.setMinimumWidth(AndroidUtilities.dp(196.0f));
                sendWithoutSoundButton.setOnClickListener(new ChatActivityEnterView$$ExternalSyntheticLambda17(this));
                this.sendPopupLayout.addView(sendWithoutSoundButton, LayoutHelper.createLinear(-1, 48));
            }
            this.sendPopupLayout.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
            AnonymousClass33 r6 = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
                public void dismiss() {
                    super.dismiss();
                    ChatActivityEnterView.this.sendButton.invalidate();
                }
            };
            this.sendPopupWindow = r6;
            r6.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(NUM);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            SharedConfig.removeScheduledOrNoSoundHint();
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onSendLongClick();
            }
        }
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        view.getLocationInWindow(this.location);
        if (this.keyboardVisible) {
            int measuredHeight = getMeasuredHeight();
            View view2 = this.topView;
            if (measuredHeight > AndroidUtilities.dp((view2 == null || view2.getVisibility() != 0) ? 58.0f : 106.0f)) {
                y = this.location[1] + view.getMeasuredHeight();
                this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
                this.sendPopupWindow.dimBehind();
                this.sendButton.invalidate();
                view.performHapticFeedback(3, 2);
                return false;
            }
        }
        y = (this.location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
        this.sendPopupWindow.dimBehind();
        this.sendButton.invalidate();
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        return false;
    }

    /* renamed from: lambda$onSendLongClick$34$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m682xvar_var_c(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$35$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m683xe628d79b(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda48(this), this.resourcesProvider);
    }

    /* renamed from: lambda$onSendLongClick$36$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m684xd7d27dba(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendMessageInternal(false, 0);
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean visible, boolean animated) {
        boolean z = visible;
        ImageView imageView = this.videoSendButton;
        if (imageView != null) {
            imageView.setTag(z ? 1 : null);
            AnimatorSet animatorSet = this.audioVideoButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.audioVideoButtonAnimation = null;
            }
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                boolean isChannel = false;
                if (DialogObject.isChatDialog(this.dialog_id)) {
                    TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                    isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                }
                preferences.edit().putBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", z).commit();
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.audioVideoButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[6];
                ImageView imageView2 = this.videoSendButton;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.videoSendButton;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.videoSendButton;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                ImageView imageView5 = this.audioSendButton;
                Property property4 = View.SCALE_X;
                float[] fArr4 = new float[1];
                fArr4[0] = z ? 0.1f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
                ImageView imageView6 = this.audioSendButton;
                Property property5 = View.SCALE_Y;
                float[] fArr5 = new float[1];
                if (!z) {
                    f2 = 1.0f;
                }
                fArr5[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(imageView6, property5, fArr5);
                ImageView imageView7 = this.audioSendButton;
                Property property6 = View.ALPHA;
                float[] fArr6 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr6[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(imageView7, property6, fArr6);
                animatorSet2.playTogether(animatorArr);
                this.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(ChatActivityEnterView.this.audioVideoButtonAnimation)) {
                            AnimatorSet unused = ChatActivityEnterView.this.audioVideoButtonAnimation = null;
                        }
                        (ChatActivityEnterView.this.videoSendButton.getTag() == null ? ChatActivityEnterView.this.audioSendButton : ChatActivityEnterView.this.videoSendButton).sendAccessibilityEvent(8);
                    }
                });
                this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
                this.audioVideoButtonAnimation.setDuration(150);
                this.audioVideoButtonAnimation.start();
                return;
            }
            this.videoSendButton.setScaleX(z ? 1.0f : 0.1f);
            this.videoSendButton.setScaleY(z ? 1.0f : 0.1f);
            this.videoSendButton.setAlpha(z ? 1.0f : 0.0f);
            this.audioSendButton.setScaleX(z ? 0.1f : 1.0f);
            ImageView imageView8 = this.audioSendButton;
            if (!z) {
                f2 = 1.0f;
            }
            imageView8.setScaleY(f2);
            ImageView imageView9 = this.audioSendButton;
            if (!z) {
                f = 1.0f;
            }
            imageView9.setAlpha(f);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.runningAnimationAudio;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRecordingAudioVideo() {
        /*
            r1 = this;
            boolean r0 = r1.recordingAudioVideo
            if (r0 != 0) goto L_0x0011
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            if (r0 == 0) goto L_0x000f
            boolean r0 = r0.isRunning()
            if (r0 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r0 = 0
            goto L_0x0012
        L_0x0011:
            r0 = 1
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.isRecordingAudioVideo():boolean");
    }

    public boolean isRecordLocked() {
        return this.recordingAudioVideo && this.recordCircle.isSendButtonVisible();
    }

    public void cancelRecordingAudioVideo() {
        ImageView imageView;
        if (!this.hasRecordVideo || (imageView = this.videoSendButton) == null || imageView.getTag() == null) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(5, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordInterface(2);
    }

    public void showContextProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (show) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setCaption(String caption) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setCaption(caption);
            checkSendButton(true);
        }
    }

    public void setSlowModeTimer(int time) {
        this.slowModeTimer = time;
        updateSlowModeText();
    }

    public CharSequence getSlowModeTimer() {
        if (this.slowModeTimer > 0) {
            return this.slowModeButton.getText();
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSlowModeText() {
        /*
            r9 = this;
            int r0 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getCurrentTime()
            java.lang.Runnable r1 = r9.updateSlowModeRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
            r1 = 0
            r9.updateSlowModeRunnable = r1
            org.telegram.tgnet.TLRPC$ChatFull r1 = r9.info
            r2 = 2147483646(0x7ffffffe, float:NaN)
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0068
            int r1 = r1.slowmode_seconds
            if (r1 == 0) goto L_0x0068
            org.telegram.tgnet.TLRPC$ChatFull r1 = r9.info
            int r1 = r1.slowmode_next_send_date
            if (r1 > r0) goto L_0x0068
            int r1 = r9.currentAccount
            org.telegram.messenger.SendMessagesHelper r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1)
            long r5 = r9.dialog_id
            boolean r1 = r1.isUploadingMessageIdDialog(r5)
            r5 = r1
            if (r1 != 0) goto L_0x0042
            int r1 = r9.currentAccount
            org.telegram.messenger.SendMessagesHelper r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1)
            long r6 = r9.dialog_id
            boolean r1 = r1.isSendingMessageIdDialog(r6)
            if (r1 == 0) goto L_0x0068
        L_0x0042:
            org.telegram.messenger.AccountInstance r1 = r9.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.info
            long r6 = r6.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r6)
            boolean r6 = org.telegram.messenger.ChatObject.hasAdminRights(r1)
            if (r6 != 0) goto L_0x0066
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.info
            int r6 = r6.slowmode_seconds
            if (r5 == 0) goto L_0x0063
            r2 = 2147483647(0x7fffffff, float:NaN)
        L_0x0063:
            r9.slowModeTimer = r2
            goto L_0x0067
        L_0x0066:
            r6 = 0
        L_0x0067:
            goto L_0x0081
        L_0x0068:
            int r1 = r9.slowModeTimer
            if (r1 < r2) goto L_0x007f
            r6 = 0
            org.telegram.tgnet.TLRPC$ChatFull r1 = r9.info
            if (r1 == 0) goto L_0x0081
            org.telegram.messenger.AccountInstance r1 = r9.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r2 = r9.info
            long r7 = r2.id
            r1.loadFullChat(r7, r4, r3)
            goto L_0x0081
        L_0x007f:
            int r6 = r1 - r0
        L_0x0081:
            int r1 = r9.slowModeTimer
            if (r1 == 0) goto L_0x00ae
            if (r6 <= 0) goto L_0x00ae
            org.telegram.ui.ActionBar.SimpleTextView r1 = r9.slowModeButton
            int r2 = java.lang.Math.max(r3, r6)
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.formatDurationNoHours(r2, r4)
            r1.setText(r2)
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r1 = r9.delegate
            if (r1 == 0) goto L_0x00a1
            org.telegram.ui.ActionBar.SimpleTextView r2 = r9.slowModeButton
            java.lang.CharSequence r5 = r2.getText()
            r1.onUpdateSlowModeButton(r2, r4, r5)
        L_0x00a1:
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda39 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda39
            r1.<init>(r9)
            r9.updateSlowModeRunnable = r1
            r4 = 100
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4)
            goto L_0x00b0
        L_0x00ae:
            r9.slowModeTimer = r4
        L_0x00b0:
            boolean r1 = r9.isInScheduleMode()
            if (r1 != 0) goto L_0x00b9
            r9.checkSendButton(r3)
        L_0x00b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateSlowModeText():void");
    }

    public void addTopView(View view, View lineView, int height) {
        if (view != null) {
            this.topLineView = lineView;
            lineView.setVisibility(8);
            this.topLineView.setAlpha(0.0f);
            addView(this.topLineView, LayoutHelper.createFrame(-1, 1.0f, 51, 0.0f, (float) (height + 1), 0.0f, 0.0f));
            this.topView = view;
            view.setVisibility(8);
            this.topViewEnterProgress = 0.0f;
            this.topView.setTranslationY((float) height);
            addView(this.topView, 0, LayoutHelper.createFrame(-1, (float) height, 51, 0.0f, 2.0f, 0.0f, 0.0f));
            this.needShowTopView = false;
        }
    }

    public void setForceShowSendButton(boolean value, boolean animated) {
        this.forceShowSendButton = value;
        checkSendButton(animated);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2) {
        setAllowStickersAndGifs(value, value2, false);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2, boolean waitingForKeyboardOpen2) {
        if (!((this.allowStickers == value && this.allowGifs == value2) || this.emojiView == null)) {
            if (!SharedConfig.smoothKeyboard) {
                if (this.emojiViewVisible) {
                    hidePopup(false);
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            } else if (!this.emojiViewVisible || waitingForKeyboardOpen2) {
                if (waitingForKeyboardOpen2) {
                    openKeyboardInternal();
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            } else {
                this.removeEmojiViewAfterAnimation = true;
                hidePopup(false);
            }
        }
        this.allowStickers = value;
        this.allowGifs = value2;
        setEmojiButtonImage(false, !this.isPaused);
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    /* renamed from: lambda$new$37$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m674lambda$new$37$orgtelegramuiComponentsChatActivityEnterView(ValueAnimator animation) {
        if (this.topView != null) {
            float v = ((Float) animation.getAnimatedValue()).floatValue();
            this.topViewEnterProgress = v;
            View view = this.topView;
            view.setTranslationY(((float) this.animatedTop) + ((1.0f - v) * ((float) view.getLayoutParams().height)));
            this.topLineView.setAlpha(v);
            this.topLineView.setTranslationY((float) this.animatedTop);
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.mentionContainer != null) {
                this.parentFragment.mentionContainer.setTranslationY((1.0f - v) * ((float) this.topView.getLayoutParams().height));
            }
        }
    }

    public void showTopView(boolean animated, boolean openKeyboard) {
        showTopView(animated, openKeyboard, false);
    }

    private void showTopView(boolean animated, boolean openKeyboard, boolean skipAwait) {
        if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
            boolean openKeyboardInternal = this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || openKeyboard) && (this.botReplyMarkup == null || this.editingMessageObject != null);
            if (skipAwait || !animated || !openKeyboardInternal || this.keyboardVisible || isPopupShowing()) {
                this.needShowTopView = true;
                this.topViewShowed = true;
                if (this.allowShowTopView) {
                    this.topView.setVisibility(0);
                    this.topLineView.setVisibility(0);
                    ValueAnimator valueAnimator = this.currentTopViewAnimation;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    resizeForTopView(true);
                    if (animated) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.topViewEnterProgress, 1.0f});
                        this.currentTopViewAnimation = ofFloat;
                        ofFloat.addUpdateListener(this.topViewUpdateListener);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.mentionContainer != null) {
                                    ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(270);
                        this.currentTopViewAnimation.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                        this.currentTopViewAnimation.start();
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    } else {
                        this.topViewEnterProgress = 1.0f;
                        this.topView.setTranslationY(0.0f);
                        this.topLineView.setAlpha(1.0f);
                    }
                    if (openKeyboardInternal) {
                        this.messageEditText.requestFocus();
                        openKeyboard();
                        return;
                    }
                    return;
                }
                return;
            }
            openKeyboard();
            Runnable runnable = this.showTopViewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            ChatActivityEnterView$$ExternalSyntheticLambda38 chatActivityEnterView$$ExternalSyntheticLambda38 = new ChatActivityEnterView$$ExternalSyntheticLambda38(this);
            this.showTopViewRunnable = chatActivityEnterView$$ExternalSyntheticLambda38;
            AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda38, 200);
        } else if (this.recordedAudioPanel.getVisibility() == 0) {
        } else {
            if (!this.forceShowSendButton || openKeyboard) {
                openKeyboard();
            }
        }
    }

    /* renamed from: lambda$showTopView$38$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m696xbe36d130() {
        showTopView(true, false, true);
        this.showTopViewRunnable = null;
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.doneButtonAnimation = new AnimatorSet();
            if (z) {
                this.doneButtonProgress.setVisibility(0);
                this.doneButtonContainer.setEnabled(false);
                this.doneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{1.0f})});
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                this.doneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{1.0f})});
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        if (!z) {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        AnimatorSet unused = ChatActivityEnterView.this.doneButtonAnimation = null;
                    }
                }
            });
            this.doneButtonAnimation.setDuration(150);
            this.doneButtonAnimation.start();
        } else if (z) {
            this.doneButtonImage.setScaleX(0.1f);
            this.doneButtonImage.setScaleY(0.1f);
            this.doneButtonImage.setAlpha(0.0f);
            this.doneButtonProgress.setScaleX(1.0f);
            this.doneButtonProgress.setScaleY(1.0f);
            this.doneButtonProgress.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(4);
            this.doneButtonProgress.setVisibility(0);
            this.doneButtonContainer.setEnabled(false);
        } else {
            this.doneButtonProgress.setScaleX(0.1f);
            this.doneButtonProgress.setScaleY(0.1f);
            this.doneButtonProgress.setAlpha(0.0f);
            this.doneButtonImage.setScaleX(1.0f);
            this.doneButtonImage.setScaleY(1.0f);
            this.doneButtonImage.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(0);
            this.doneButtonProgress.setVisibility(4);
            this.doneButtonContainer.setEnabled(true);
        }
    }

    public void hideTopView(boolean animated) {
        if (this.topView != null && this.topViewShowed) {
            Runnable runnable = this.showTopViewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.topViewShowed = false;
            this.needShowTopView = false;
            if (this.allowShowTopView) {
                ValueAnimator valueAnimator = this.currentTopViewAnimation;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.currentTopViewAnimation = null;
                }
                if (animated) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.topViewEnterProgress, 0.0f});
                    this.currentTopViewAnimation = ofFloat;
                    ofFloat.addUpdateListener(this.topViewUpdateListener);
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.topLineView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                            if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.mentionContainer != null) {
                                ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(250);
                    this.currentTopViewAnimation.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                    this.currentTopViewAnimation.start();
                    return;
                }
                this.topViewEnterProgress = 0.0f;
                this.topView.setVisibility(8);
                this.topLineView.setVisibility(8);
                this.topLineView.setAlpha(0.0f);
                resizeForTopView(false);
                View view = this.topView;
                view.setTranslationY((float) view.getLayoutParams().height);
            }
        }
    }

    public boolean isTopViewVisible() {
        View view = this.topView;
        return view != null && view.getVisibility() == 0;
    }

    public void onAdjustPanTransitionUpdate(float y, float progress, boolean keyboardVisible2) {
        this.botWebViewMenuContainer.setTranslationY(y);
    }

    public void onAdjustPanTransitionEnd() {
        this.botWebViewMenuContainer.onPanTransitionEnd();
        Runnable runnable = this.onKeyboardClosed;
        if (runnable != null) {
            runnable.run();
            this.onKeyboardClosed = null;
        }
    }

    public void onAdjustPanTransitionStart(boolean keyboardVisible2, int contentHeight) {
        Runnable runnable;
        this.botWebViewMenuContainer.onPanTransitionStart(keyboardVisible2, contentHeight);
        if (keyboardVisible2 && (runnable = this.showTopViewRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showTopViewRunnable.run();
        }
        Runnable runnable2 = this.setTextFieldRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.setTextFieldRunnable.run();
        }
        if (keyboardVisible2 && this.messageEditText.hasFocus() && hasBotWebView() && botCommandsMenuIsShowing()) {
            this.botWebViewMenuContainer.dismiss();
        }
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onWindowSizeChanged(size);
        }
        if (this.topView == null) {
            return;
        }
        if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
            if (this.allowShowTopView) {
                this.allowShowTopView = false;
                if (this.needShowTopView) {
                    this.topView.setVisibility(8);
                    this.topLineView.setVisibility(8);
                    this.topLineView.setAlpha(0.0f);
                    resizeForTopView(false);
                    this.topViewEnterProgress = 0.0f;
                    View view = this.topView;
                    view.setTranslationY((float) view.getLayoutParams().height);
                }
            }
        } else if (!this.allowShowTopView) {
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.topView.setVisibility(0);
                this.topLineView.setVisibility(0);
                this.topLineView.setAlpha(1.0f);
                resizeForTopView(true);
                this.topViewEnterProgress = 1.0f;
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void resizeForTopView(boolean show) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (show ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        setMinimumHeight(AndroidUtilities.dp(51.0f) + (show ? this.topView.getLayoutParams().height : 0));
        if (!this.stickersExpanded) {
            return;
        }
        if (this.searchingType == 0) {
            setStickersExpanded(false, true, false);
        } else {
            checkStickresExpandHeight();
        }
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateBotMenuButton);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
        }
        PowerManager.WakeLock wakeLock2 = this.wakeLock;
        if (wakeLock2 != null) {
            try {
                wakeLock2.release();
                this.wakeLock = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate((SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate) null);
        }
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void checkChannelRights() {
        TLRPC.Chat chat;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && (chat = chatActivity.getCurrentChat()) != null) {
            this.audioVideoButtonContainer.setAlpha(ChatObject.canSendMedia(chat) ? 1.0f : 0.5f);
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.setStickersBanned(!ChatObject.canSendStickers(chat), chat.id);
            }
        }
    }

    public void onBeginHide() {
        Runnable runnable = this.focusRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.focusRunnable = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void onPause() {
        this.isPaused = true;
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
        if (this.keyboardVisible) {
            this.showKeyboardOnResume = true;
        }
        ChatActivityEnterView$$ExternalSyntheticLambda35 chatActivityEnterView$$ExternalSyntheticLambda35 = new ChatActivityEnterView$$ExternalSyntheticLambda35(this);
        this.hideKeyboardRunnable = chatActivityEnterView$$ExternalSyntheticLambda35;
        AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda35, 500);
    }

    /* renamed from: lambda$onPause$39$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m681x3ecdfvar_() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.isLastFragment()) {
            closeKeyboard();
        }
        this.hideKeyboardRunnable = null;
    }

    public void onResume() {
        this.isPaused = false;
        Runnable runnable = this.hideKeyboardRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideKeyboardRunnable = null;
        }
        if (!hasBotWebView() || !botCommandsMenuIsShowing()) {
            int visibility = getVisibility();
            if (this.showKeyboardOnResume && this.parentFragment.isLastFragment()) {
                this.showKeyboardOnResume = false;
                if (this.searchingType == 0) {
                    this.messageEditText.requestFocus();
                }
                AndroidUtilities.showKeyboard(this.messageEditText);
                if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                    this.waitingForKeyboardOpen = true;
                    AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
                }
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.messageEditText.setEnabled(visibility == 0);
    }

    public void setDialogId(long id, int account) {
        this.dialog_id = id;
        int i = this.currentAccount;
        if (i != account) {
            NotificationCenter.getInstance(i).onAnimationFinish(this.notificationsIndex);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
            this.currentAccount = account;
            this.accountInstance = AccountInstance.getInstance(account);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.sendingMessagesChanged);
        }
        updateScheduleButton(false);
        checkRoundVideo();
        updateFieldHint(false);
        updateSendAsButton(false);
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setChatInfo(chatInfo);
        }
        setSlowModeTimer(chatInfo.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        if (!this.hasRecordVideo) {
            if (this.attachLayout == null || Build.VERSION.SDK_INT < 18) {
                this.hasRecordVideo = false;
                setRecordVideoButtonVisible(false, false);
                return;
            }
            boolean z = true;
            this.hasRecordVideo = true;
            boolean isChannel = false;
            if (DialogObject.isChatDialog(this.dialog_id)) {
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    z = false;
                }
                isChannel = z;
                if (isChannel && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
                    this.hasRecordVideo = false;
                }
            }
            if (!SharedConfig.inappCamera) {
                this.hasRecordVideo = false;
            }
            if (this.hasRecordVideo) {
                if (SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", isChannel), false);
                return;
            }
            setRecordVideoButtonVisible(false, false);
        }
    }

    public boolean isInVideoMode() {
        ImageView imageView = this.videoSendButton;
        return (imageView == null || imageView.getTag() == null) ? false : true;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    public MessageObject getReplyingMessageObject() {
        return this.replyingMessageObject;
    }

    public void updateFieldHint(boolean animated) {
        MessageObject messageObject;
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 == null || messageObject2.messageOwner.reply_markup == null || TextUtils.isEmpty(this.replyingMessageObject.messageOwner.reply_markup.placeholder)) {
            int i = NUM;
            String str = "TypeMessage";
            if (this.editingMessageObject != null) {
                EditTextCaption editTextCaption = this.messageEditText;
                if (this.editingCaption) {
                    i = NUM;
                    str = "Caption";
                }
                editTextCaption.setHintText(LocaleController.getString(str, i));
            } else if (!this.botKeyboardViewVisible || (messageObject = this.botButtonsMessageObject) == null || messageObject.messageOwner.reply_markup == null || TextUtils.isEmpty(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder)) {
                boolean isChannel = false;
                boolean anonymously = false;
                if (DialogObject.isChatDialog(this.dialog_id)) {
                    TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                    TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(-this.dialog_id);
                    boolean z = true;
                    isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                    if (ChatObject.getSendAsPeerId(chat, chatFull) != chat.id) {
                        z = false;
                    }
                    anonymously = z;
                }
                if (anonymously) {
                    this.messageEditText.setHintText(LocaleController.getString("SendAnonymously", NUM));
                    return;
                }
                ChatActivity chatActivity = this.parentFragment;
                if (chatActivity == null || !chatActivity.isThreadChat()) {
                    if (!isChannel) {
                        this.messageEditText.setHintText(LocaleController.getString(str, NUM));
                    } else if (this.silent) {
                        this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", NUM), animated);
                    } else {
                        this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", NUM), animated);
                    }
                } else if (this.parentFragment.isReplyChatComment()) {
                    this.messageEditText.setHintText(LocaleController.getString("Comment", NUM));
                } else {
                    this.messageEditText.setHintText(LocaleController.getString("Reply", NUM));
                }
            } else {
                this.messageEditText.setHintText(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder, animated);
            }
        } else {
            this.messageEditText.setHintText(this.replyingMessageObject.messageOwner.reply_markup.placeholder, animated);
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        MessageObject messageObject2;
        if (messageObject != null) {
            if (this.botMessageObject == null && (messageObject2 = this.botButtonsMessageObject) != this.replyingMessageObject) {
                this.botMessageObject = messageObject2;
            }
            this.replyingMessageObject = messageObject;
            setButtons(messageObject, true);
        } else if (this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = null;
        }
        MediaController.getInstance().setReplyingMessage(messageObject, getThreadMessage());
        updateFieldHint(false);
    }

    public void setWebPage(TLRPC.WebPage webPage, boolean searchWebPages) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = searchWebPages;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel(boolean wasSent) {
        AnimatorSet attachIconAnimator;
        AnimatorSet animatorSet = this.recordPannelAnimation;
        if (animatorSet == null || !animatorSet.isRunning()) {
            this.audioToSendPath = null;
            this.audioToSend = null;
            this.audioToSendMessageObject = null;
            this.videoToSendMessageObject = null;
            this.videoTimelineView.destroy();
            if (this.videoSendButton == null || !isInVideoMode()) {
                ImageView imageView = this.audioSendButton;
                if (imageView != null) {
                    imageView.setVisibility(0);
                }
            } else {
                this.videoSendButton.setVisibility(0);
            }
            if (wasSent) {
                this.attachButton.setAlpha(0.0f);
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.attachButton.setScaleX(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.attachButton.setScaleY(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet2;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
                if (botCommandsMenuView != null) {
                    botCommandsMenuView.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    this.recordPannelAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{1.0f})});
                }
                this.recordPannelAnimation.setDuration(150);
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                });
            } else {
                this.recordDeleteImageView.playAnimation();
                AnimatorSet exitAnimation = new AnimatorSet();
                if (isInVideoMode()) {
                    exitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                } else {
                    this.messageEditText.setAlpha(1.0f);
                    this.messageEditText.setTranslationX(0.0f);
                    exitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))})});
                }
                exitAnimation.setDuration(200);
                ImageView imageView2 = this.attachButton;
                if (imageView2 != null) {
                    imageView2.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                    attachIconAnimator = new AnimatorSet();
                    attachIconAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f})});
                    attachIconAnimator.setDuration(150);
                } else {
                    attachIconAnimator = null;
                }
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet iconsEndAnimator = new AnimatorSet();
                iconsEndAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f})});
                BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                if (botCommandsMenuView2 != null) {
                    botCommandsMenuView2.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    iconsEndAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{1.0f})});
                }
                iconsEndAnimator.setDuration(150);
                iconsEndAnimator.setStartDelay(600);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet3;
                if (attachIconAnimator != null) {
                    animatorSet3.playTogether(new Animator[]{exitAnimation, attachIconAnimator, iconsEndAnimator});
                } else {
                    animatorSet3.playTogether(new Animator[]{exitAnimation, iconsEndAnimator});
                }
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.recordedAudioSeekBar.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioSeekBar.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioBackground.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioTimeTextView.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioTimeTextView.setTranslationX(0.0f);
                        ChatActivityEnterView.this.videoTimelineView.setAlpha(1.0f);
                        ChatActivityEnterView.this.videoTimelineView.setTranslationX(0.0f);
                        ChatActivityEnterView.this.messageEditText.setAlpha(1.0f);
                        ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                    }
                });
            }
            this.recordPannelAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public void sendMessage() {
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda48(this), this.resourcesProvider);
        } else {
            sendMessageInternal(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessageInternal(boolean notify, int scheduleDate) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC.Chat chat;
        EmojiView emojiView2;
        boolean z = notify;
        int i = scheduleDate;
        if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                TLRPC.Chat chat2 = chatActivity.getCurrentChat();
                if (this.parentFragment.getCurrentUser() != null || ((ChatObject.isChannel(chat2) && chat2.megagroup) || !ChatObject.isChannel(chat2))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + this.dialog_id, !z).commit();
                }
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                if (!(this.searchingType == 0 || (emojiView2 = this.emojiView) == null)) {
                    emojiView2.closeSearch(false);
                    this.emojiView.hideSearchKeyboard();
                }
            }
            if (this.videoToSendMessageObject != null) {
                this.delegate.needStartRecordVideo(4, z, i);
                hideRecordedAudioPanel(true);
                checkSendButton(true);
            } else if (this.audioToSend != null) {
                MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
                if (playing != null && playing == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
                MessageObject messageObject = playing;
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, (VideoEditedInfo) null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, getThreadMessage(), (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, 0, (Object) null, (MessageObject.SendAnimationData) null);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    chatActivityEnterViewDelegate2.onMessageSend((CharSequence) null, notify, scheduleDate);
                } else {
                    boolean z2 = notify;
                    int i2 = scheduleDate;
                }
                hideRecordedAudioPanel(true);
                checkSendButton(true);
            } else {
                int i3 = i;
                boolean z3 = z;
                CharSequence message = this.messageEditText.getText();
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null && (chat = chatActivity2.getCurrentChat()) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
                    if (message.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendErrorTooLong", NUM), this.resourcesProvider);
                        return;
                    } else if (this.forceShowSendButton && message.length() > 0) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM), this.resourcesProvider);
                        return;
                    }
                }
                if (processSendingText(message, z3, i3)) {
                    if (this.delegate.hasForwardingMessages() || ((i3 != 0 && !isInScheduleMode()) || isInScheduleMode())) {
                        this.messageEditText.setText("");
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                        if (chatActivityEnterViewDelegate3 != null) {
                            chatActivityEnterViewDelegate3.onMessageSend(message, z3, i3);
                        }
                    } else {
                        this.messageTransitionIsRunning = false;
                        ChatActivityEnterView$$ExternalSyntheticLambda42 chatActivityEnterView$$ExternalSyntheticLambda42 = new ChatActivityEnterView$$ExternalSyntheticLambda42(this, message, z3, i3);
                        this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda42;
                        AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda42, 200);
                    }
                    this.lastTypingTimeSend = 0;
                } else if (this.forceShowSendButton && (chatActivityEnterViewDelegate = this.delegate) != null) {
                    chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, z3, i3);
                }
            }
        } else {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
            if (chatActivityEnterViewDelegate4 != null) {
                chatActivityEnterViewDelegate4.scrollToSendingMessage();
            }
        }
    }

    /* renamed from: lambda$sendMessageInternal$40$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m689xa32fd0d8(CharSequence message, boolean notify, int scheduleDate) {
        this.moveToSendStateRunnable = null;
        hideTopView(true);
        this.messageEditText.setText("");
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(message, notify, scheduleDate);
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            if (this.currentLimit - this.codePointCount < 0) {
                AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
                Vibrator v = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                    return;
                }
                return;
            }
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                this.emojiView.closeSearch(false);
                if (this.stickersExpanded) {
                    setStickersExpanded(false, true, false);
                    this.waitingForKeyboardOpenAfterAnimation = true;
                    AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda27(this), 200);
                }
            }
            CharSequence[] message = {AndroidUtilities.getTrimmedString(this.messageEditText.getText())};
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(message, supportsSendingNewEntities());
            if (!TextUtils.equals(message[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || (((entities == null || entities.isEmpty()) && !this.editingMessageObject.messageOwner.entities.isEmpty()) || (this.editingMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))) {
                this.editingMessageObject.editingMessage = message[0];
                this.editingMessageObject.editingMessageEntities = entities;
                this.editingMessageObject.editingMessageSearchWebPage = this.messageWebPageSearch;
                SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.TL_document) null, (String) null, (HashMap<String, String>) null, false, (Object) null);
            }
            setEditingMessageObject((MessageObject) null, false);
        }
    }

    /* renamed from: lambda$doneEditingMessage$41$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m653xb8852e06() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    public boolean processSendingText(CharSequence text, boolean notify, int scheduleDate) {
        int enterIndex;
        int tabIndex;
        int dotIndex;
        int whitespaceIndex;
        int end;
        MessageObject.SendAnimationData sendAnimationData;
        ChatActivity chatActivity;
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        boolean supportsNewEntities = supportsSendingNewEntities();
        int maxLength = this.accountInstance.getMessagesController().maxMessageLength;
        int end2 = 0;
        if (text2.length() == 0) {
            return false;
        }
        if (!(this.delegate == null || (chatActivity = this.parentFragment) == null)) {
            if ((scheduleDate != 0) == chatActivity.isInScheduleMode()) {
                this.delegate.prepareMessageSending();
            }
        }
        int start = 0;
        while (true) {
            int whitespaceIndex2 = -1;
            int dotIndex2 = -1;
            int enterIndex2 = -1;
            if (text2.length() > start + maxLength) {
                int i = (start + maxLength) - 1;
                int k = 0;
                while (true) {
                    if (i > start && k < 300) {
                        char c = text2.charAt(i);
                        char c2 = i > 0 ? text2.charAt(i - 1) : ' ';
                        if (c == 10 && c2 == 10) {
                            whitespaceIndex = whitespaceIndex2;
                            dotIndex = dotIndex2;
                            tabIndex = i;
                            enterIndex = enterIndex2;
                            break;
                        }
                        if (c == 10) {
                            enterIndex2 = i;
                        } else if (dotIndex2 < 0 && Character.isWhitespace(c) && c2 == '.') {
                            dotIndex2 = i;
                        } else if (whitespaceIndex2 < 0 && Character.isWhitespace(c)) {
                            whitespaceIndex2 = i;
                        }
                        i--;
                        k++;
                    } else {
                        whitespaceIndex = whitespaceIndex2;
                        dotIndex = dotIndex2;
                        tabIndex = -1;
                        enterIndex = enterIndex2;
                    }
                }
            } else {
                whitespaceIndex = -1;
                dotIndex = -1;
                tabIndex = -1;
                enterIndex = -1;
            }
            int end3 = Math.min(start + maxLength, text2.length());
            if (tabIndex > 0) {
                end = tabIndex;
            } else if (enterIndex > 0) {
                end = enterIndex;
            } else if (dotIndex > 0) {
                end = dotIndex;
            } else if (whitespaceIndex > 0) {
                end = whitespaceIndex;
            } else {
                end = end3;
            }
            CharSequence[] charSequenceArr = new CharSequence[1];
            charSequenceArr[end2] = AndroidUtilities.getTrimmedString(text2.subSequence(start, end));
            CharSequence[] message = charSequenceArr;
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(message, supportsNewEntities);
            if (!this.delegate.hasForwardingMessages()) {
                MessageObject.SendAnimationData sendAnimationData2 = new MessageObject.SendAnimationData();
                float dp = (float) AndroidUtilities.dp(22.0f);
                sendAnimationData2.height = dp;
                sendAnimationData2.width = dp;
                this.messageEditText.getLocationInWindow(this.location);
                sendAnimationData2.x = (float) (this.location[end2] + AndroidUtilities.dp(11.0f));
                sendAnimationData2.y = (float) (this.location[1] + AndroidUtilities.dp(19.0f));
                sendAnimationData = sendAnimationData2;
            } else {
                sendAnimationData = null;
            }
            CharSequence[] charSequenceArr2 = message;
            int end4 = end;
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message[end2].toString(), this.dialog_id, this.replyingMessageObject, getThreadMessage(), this.messageWebPage, this.messageWebPageSearch, entities, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, sendAnimationData);
            start = end4 + 1;
            if (end4 == text2.length()) {
                return true;
            }
            end2 = 0;
        }
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        TLRPC.EncryptedChat encryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return encryptedChat == null || AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 101;
    }

    /* access modifiers changed from: private */
    public void checkSendButton(boolean animated) {
        boolean animated2;
        int color;
        int i;
        int i2;
        if (this.editingMessageObject == null && !this.recordingAudioVideo) {
            if (this.isPaused) {
                animated2 = false;
            } else {
                animated2 = animated;
            }
            CharSequence message = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
            int i3 = this.slowModeTimer;
            if (i3 <= 0 || i3 == Integer.MAX_VALUE || isInScheduleMode()) {
                if (message.length() <= 0 && !this.forceShowSendButton && this.audioToSend == null && this.videoToSendMessageObject == null) {
                    if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                        if (this.emojiView == null || !this.emojiViewVisible || ((!this.stickersTabOpen && (!this.emojiTabOpen || this.searchingType != 2)) || AndroidUtilities.isInMultiwindow)) {
                            if (this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0 && this.expandStickersButton.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0) {
                                return;
                            }
                            if (!animated2) {
                                this.slowModeButton.setScaleX(0.1f);
                                this.slowModeButton.setScaleY(0.1f);
                                this.slowModeButton.setAlpha(0.0f);
                                setSlowModeButtonVisible(false);
                                this.sendButton.setScaleX(0.1f);
                                this.sendButton.setScaleY(0.1f);
                                this.sendButton.setAlpha(0.0f);
                                this.sendButton.setVisibility(8);
                                this.cancelBotButton.setScaleX(0.1f);
                                this.cancelBotButton.setScaleY(0.1f);
                                this.cancelBotButton.setAlpha(0.0f);
                                this.cancelBotButton.setVisibility(8);
                                this.expandStickersButton.setScaleX(0.1f);
                                this.expandStickersButton.setScaleY(0.1f);
                                this.expandStickersButton.setAlpha(0.0f);
                                this.expandStickersButton.setVisibility(8);
                                this.audioVideoButtonContainer.setScaleX(1.0f);
                                this.audioVideoButtonContainer.setScaleY(1.0f);
                                this.audioVideoButtonContainer.setAlpha(1.0f);
                                this.audioVideoButtonContainer.setVisibility(0);
                                if (this.attachLayout != null) {
                                    if (getVisibility() == 0) {
                                        this.delegate.onAttachButtonShow();
                                    }
                                    this.attachLayout.setAlpha(1.0f);
                                    this.attachLayout.setScaleX(1.0f);
                                    this.attachLayout.setVisibility(0);
                                    updateFieldRight(1);
                                }
                                this.scheduleButtonHidden = false;
                                if (this.scheduledButton != null) {
                                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                                    if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                                        this.scheduledButton.setVisibility(0);
                                        this.scheduledButton.setTag(1);
                                    }
                                    this.scheduledButton.setAlpha(1.0f);
                                    this.scheduledButton.setScaleX(1.0f);
                                    this.scheduledButton.setScaleY(1.0f);
                                    this.scheduledButton.setTranslationX(0.0f);
                                    return;
                                }
                                return;
                            } else if (this.runningAnimationType != 2) {
                                AnimatorSet animatorSet = this.runningAnimation;
                                if (animatorSet != null) {
                                    animatorSet.cancel();
                                    this.runningAnimation = null;
                                }
                                AnimatorSet animatorSet2 = this.runningAnimation2;
                                if (animatorSet2 != null) {
                                    animatorSet2.cancel();
                                    this.runningAnimation2 = null;
                                }
                                LinearLayout linearLayout = this.attachLayout;
                                if (linearLayout != null) {
                                    if (linearLayout.getVisibility() != 0) {
                                        this.attachLayout.setVisibility(0);
                                        this.attachLayout.setAlpha(0.0f);
                                        this.attachLayout.setScaleX(0.0f);
                                    }
                                    this.runningAnimation2 = new AnimatorSet();
                                    ArrayList<Animator> animators = new ArrayList<>();
                                    animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                                    animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                                    boolean hasScheduled = chatActivityEnterViewDelegate2 != null && chatActivityEnterViewDelegate2.hasScheduledMessages();
                                    this.scheduleButtonHidden = false;
                                    ImageView imageView = this.scheduledButton;
                                    if (imageView != null) {
                                        if (hasScheduled) {
                                            imageView.setVisibility(0);
                                            this.scheduledButton.setTag(1);
                                            this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                        } else {
                                            imageView.setAlpha(1.0f);
                                            this.scheduledButton.setScaleX(1.0f);
                                            this.scheduledButton.setScaleY(1.0f);
                                            this.scheduledButton.setTranslationX(0.0f);
                                        }
                                    }
                                    this.runningAnimation2.playTogether(animators);
                                    this.runningAnimation2.setDuration(100);
                                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                            }
                                        }

                                        public void onAnimationCancel(Animator animation) {
                                            if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                            }
                                        }
                                    });
                                    this.runningAnimation2.start();
                                    updateFieldRight(1);
                                    if (getVisibility() == 0) {
                                        this.delegate.onAttachButtonShow();
                                    }
                                }
                                this.audioVideoButtonContainer.setVisibility(0);
                                this.runningAnimation = new AnimatorSet();
                                this.runningAnimationType = 2;
                                ArrayList<Animator> animators2 = new ArrayList<>();
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f}));
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f}));
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}));
                                if (this.cancelBotButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                                } else if (this.expandStickersButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                                } else if (this.slowModeButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                                } else {
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                                }
                                this.runningAnimation.playTogether(animators2);
                                this.runningAnimation.setDuration(150);
                                this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                            ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                            int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                            if (ChatActivityEnterView.this.audioVideoButtonContainer != null) {
                                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                            }
                                        }
                                    }

                                    public void onAnimationCancel(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                        }
                                    }
                                });
                                this.runningAnimation.start();
                                return;
                            } else {
                                return;
                            }
                        } else if (!animated2) {
                            this.slowModeButton.setScaleX(0.1f);
                            this.slowModeButton.setScaleY(0.1f);
                            this.slowModeButton.setAlpha(0.0f);
                            setSlowModeButtonVisible(false);
                            this.sendButton.setScaleX(0.1f);
                            this.sendButton.setScaleY(0.1f);
                            this.sendButton.setAlpha(0.0f);
                            this.sendButton.setVisibility(8);
                            this.cancelBotButton.setScaleX(0.1f);
                            this.cancelBotButton.setScaleY(0.1f);
                            this.cancelBotButton.setAlpha(0.0f);
                            this.cancelBotButton.setVisibility(8);
                            this.audioVideoButtonContainer.setScaleX(0.1f);
                            this.audioVideoButtonContainer.setScaleY(0.1f);
                            this.audioVideoButtonContainer.setAlpha(0.0f);
                            this.audioVideoButtonContainer.setVisibility(8);
                            this.expandStickersButton.setScaleX(1.0f);
                            this.expandStickersButton.setScaleY(1.0f);
                            this.expandStickersButton.setAlpha(1.0f);
                            this.expandStickersButton.setVisibility(0);
                            if (this.attachLayout != null) {
                                if (getVisibility() == 0) {
                                    this.delegate.onAttachButtonShow();
                                }
                                this.attachLayout.setVisibility(0);
                                updateFieldRight(1);
                            }
                            this.scheduleButtonHidden = false;
                            if (this.scheduledButton != null) {
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                                if (chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages()) {
                                    this.scheduledButton.setVisibility(0);
                                    this.scheduledButton.setTag(1);
                                }
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                                this.scheduledButton.setScaleY(1.0f);
                                this.scheduledButton.setTranslationX(0.0f);
                                return;
                            }
                            return;
                        } else if (this.runningAnimationType != 4) {
                            AnimatorSet animatorSet3 = this.runningAnimation;
                            if (animatorSet3 != null) {
                                animatorSet3.cancel();
                                this.runningAnimation = null;
                            }
                            AnimatorSet animatorSet4 = this.runningAnimation2;
                            if (animatorSet4 != null) {
                                animatorSet4.cancel();
                                this.runningAnimation2 = null;
                            }
                            LinearLayout linearLayout2 = this.attachLayout;
                            if (linearLayout2 != null && this.recordInterfaceState == 0) {
                                linearLayout2.setVisibility(0);
                                this.runningAnimation2 = new AnimatorSet();
                                ArrayList<Animator> animators3 = new ArrayList<>();
                                animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                                animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                                boolean hasScheduled2 = chatActivityEnterViewDelegate4 != null && chatActivityEnterViewDelegate4.hasScheduledMessages();
                                this.scheduleButtonHidden = false;
                                ImageView imageView2 = this.scheduledButton;
                                if (imageView2 != null) {
                                    imageView2.setScaleY(1.0f);
                                    if (hasScheduled2) {
                                        this.scheduledButton.setVisibility(0);
                                        this.scheduledButton.setTag(1);
                                        this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                    } else {
                                        this.scheduledButton.setAlpha(1.0f);
                                        this.scheduledButton.setScaleX(1.0f);
                                        this.scheduledButton.setTranslationX(0.0f);
                                    }
                                }
                                this.runningAnimation2.playTogether(animators3);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }
                                });
                                this.runningAnimation2.start();
                                updateFieldRight(1);
                                if (getVisibility() == 0) {
                                    this.delegate.onAttachButtonShow();
                                }
                            }
                            this.expandStickersButton.setVisibility(0);
                            this.runningAnimation = new AnimatorSet();
                            this.runningAnimationType = 4;
                            ArrayList<Animator> animators4 = new ArrayList<>();
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{1.0f}));
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{1.0f}));
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{1.0f}));
                            if (this.cancelBotButton.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                            } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                            } else if (this.slowModeButton.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                            } else {
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                            }
                            this.runningAnimation.playTogether(animators4);
                            this.runningAnimation.setDuration(250);
                            this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                        ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                        ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                        int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                            return;
                        } else {
                            return;
                        }
                    }
                }
                final String caption = this.messageEditText.getCaption();
                boolean showBotButton = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                boolean showSendButton = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                    color = getThemedColor("chat_messagePanelSend");
                } else {
                    color = getThemedColor("chat_messagePanelIcons");
                }
                Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), true);
                if (this.audioVideoButtonContainer.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0 && !showBotButton && !showSendButton) {
                    return;
                }
                if (!animated2) {
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                    if (this.slowModeButton.getVisibility() == 0) {
                        this.slowModeButton.setScaleX(0.1f);
                        this.slowModeButton.setScaleY(0.1f);
                        this.slowModeButton.setAlpha(0.0f);
                        setSlowModeButtonVisible(false);
                    }
                    if (caption != null) {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(8);
                        this.cancelBotButton.setScaleX(1.0f);
                        this.cancelBotButton.setScaleY(1.0f);
                        this.cancelBotButton.setAlpha(1.0f);
                        this.cancelBotButton.setVisibility(0);
                    } else {
                        this.cancelBotButton.setScaleX(0.1f);
                        this.cancelBotButton.setScaleY(0.1f);
                        this.cancelBotButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(0);
                        this.sendButton.setScaleX(1.0f);
                        this.sendButton.setScaleY(1.0f);
                        this.sendButton.setAlpha(1.0f);
                        this.cancelBotButton.setVisibility(8);
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        this.expandStickersButton.setScaleX(0.1f);
                        this.expandStickersButton.setScaleY(0.1f);
                        this.expandStickersButton.setAlpha(0.0f);
                        i = 8;
                        this.expandStickersButton.setVisibility(8);
                    } else {
                        i = 8;
                    }
                    LinearLayout linearLayout3 = this.attachLayout;
                    if (linearLayout3 != null) {
                        linearLayout3.setVisibility(i);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                    this.scheduleButtonHidden = true;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate5 = this.delegate;
                        if (chatActivityEnterViewDelegate5 != null && chatActivityEnterViewDelegate5.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(8);
                            this.scheduledButton.setTag((Object) null);
                        }
                        this.scheduledButton.setAlpha(0.0f);
                        this.scheduledButton.setScaleX(0.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        ImageView imageView3 = this.scheduledButton;
                        ImageView imageView4 = this.botButton;
                        imageView3.setTranslationX((float) AndroidUtilities.dp((imageView4 == null || imageView4.getVisibility() == 8) ? 48.0f : 96.0f));
                    }
                } else if (this.runningAnimationType != 1 || this.messageEditText.getCaption() != null) {
                    if (this.runningAnimationType != 3 || caption == null) {
                        AnimatorSet animatorSet5 = this.runningAnimation;
                        if (animatorSet5 != null) {
                            animatorSet5.cancel();
                            this.runningAnimation = null;
                        }
                        AnimatorSet animatorSet6 = this.runningAnimation2;
                        if (animatorSet6 != null) {
                            animatorSet6.cancel();
                            this.runningAnimation2 = null;
                        }
                        if (this.attachLayout != null) {
                            this.runningAnimation2 = new AnimatorSet();
                            ArrayList<Animator> animators5 = new ArrayList<>();
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate6 = this.delegate;
                            final boolean hasScheduled3 = chatActivityEnterViewDelegate6 != null && chatActivityEnterViewDelegate6.hasScheduledMessages();
                            this.scheduleButtonHidden = true;
                            ImageView imageView5 = this.scheduledButton;
                            if (imageView5 != null) {
                                imageView5.setScaleY(1.0f);
                                if (hasScheduled3) {
                                    this.scheduledButton.setTag((Object) null);
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f}));
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{0.0f}));
                                    ImageView imageView6 = this.scheduledButton;
                                    Property property = View.TRANSLATION_X;
                                    float[] fArr = new float[1];
                                    ImageView imageView7 = this.botButton;
                                    fArr[0] = (float) AndroidUtilities.dp((imageView7 == null || imageView7.getVisibility() == 8) ? 48.0f : 96.0f);
                                    animators5.add(ObjectAnimator.ofFloat(imageView6, property, fArr));
                                } else {
                                    this.scheduledButton.setAlpha(0.0f);
                                    this.scheduledButton.setScaleX(0.0f);
                                    ImageView imageView8 = this.scheduledButton;
                                    ImageView imageView9 = this.botButton;
                                    imageView8.setTranslationX((float) AndroidUtilities.dp((imageView9 == null || imageView9.getVisibility() == 8) ? 48.0f : 96.0f));
                                }
                            }
                            this.runningAnimation2.playTogether(animators5);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                        if (hasScheduled3) {
                                            ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                        }
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }
                            });
                            this.runningAnimation2.start();
                            updateFieldRight(0);
                            if (this.delegate != null && getVisibility() == 0) {
                                this.delegate.onAttachButtonHidden();
                            }
                        }
                        this.runningAnimation = new AnimatorSet();
                        ArrayList<Animator> animators6 = new ArrayList<>();
                        if (this.audioVideoButtonContainer.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                        }
                        if (this.expandStickersButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (this.slowModeButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (showBotButton) {
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                        } else if (showSendButton) {
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (caption != null) {
                            this.runningAnimationType = 3;
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{1.0f}));
                            this.cancelBotButton.setVisibility(0);
                        } else {
                            this.runningAnimationType = 1;
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{1.0f}));
                            this.sendButton.setVisibility(0);
                        }
                        this.runningAnimation.playTogether(animators6);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    if (caption != null) {
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    } else {
                                        ChatActivityEnterView.this.sendButton.setVisibility(0);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    }
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                    ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                }
            } else if (this.slowModeButton.getVisibility() == 0) {
            } else {
                if (!animated2) {
                    this.slowModeButton.setScaleX(1.0f);
                    this.slowModeButton.setScaleY(1.0f);
                    this.slowModeButton.setAlpha(1.0f);
                    setSlowModeButtonVisible(true);
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.sendButton.setVisibility(8);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.cancelBotButton.setVisibility(8);
                    if (this.expandStickersButton.getVisibility() == 0) {
                        this.expandStickersButton.setScaleX(0.1f);
                        this.expandStickersButton.setScaleY(0.1f);
                        this.expandStickersButton.setAlpha(0.0f);
                        i2 = 8;
                        this.expandStickersButton.setVisibility(8);
                    } else {
                        i2 = 8;
                    }
                    LinearLayout linearLayout4 = this.attachLayout;
                    if (linearLayout4 != null) {
                        linearLayout4.setVisibility(i2);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                    this.scheduleButtonHidden = false;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate7 = this.delegate;
                        if (chatActivityEnterViewDelegate7 != null && chatActivityEnterViewDelegate7.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                        }
                        ImageView imageView10 = this.scheduledButton;
                        ImageView imageView11 = this.botButton;
                        imageView10.setTranslationX((float) AndroidUtilities.dp((imageView11 == null || imageView11.getVisibility() != 0) ? 48.0f : 96.0f));
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                    }
                } else if (this.runningAnimationType != 5) {
                    AnimatorSet animatorSet7 = this.runningAnimation;
                    if (animatorSet7 != null) {
                        animatorSet7.cancel();
                        this.runningAnimation = null;
                    }
                    AnimatorSet animatorSet8 = this.runningAnimation2;
                    if (animatorSet8 != null) {
                        animatorSet8.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachLayout != null) {
                        this.runningAnimation2 = new AnimatorSet();
                        ArrayList<Animator> animators7 = new ArrayList<>();
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                        this.scheduleButtonHidden = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate8 = this.delegate;
                        boolean hasScheduled4 = chatActivityEnterViewDelegate8 != null && chatActivityEnterViewDelegate8.hasScheduledMessages();
                        ImageView imageView12 = this.scheduledButton;
                        if (imageView12 != null) {
                            imageView12.setScaleY(1.0f);
                            if (hasScheduled4) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                ImageView imageView13 = this.scheduledButton;
                                Property property2 = View.TRANSLATION_X;
                                float[] fArr2 = new float[1];
                                ImageView imageView14 = this.botButton;
                                fArr2[0] = (float) AndroidUtilities.dp((imageView14 == null || imageView14.getVisibility() != 0) ? 48.0f : 96.0f);
                                animators7.add(ObjectAnimator.ofFloat(imageView13, property2, fArr2));
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                            } else {
                                ImageView imageView15 = this.scheduledButton;
                                ImageView imageView16 = this.botButton;
                                imageView15.setTranslationX((float) AndroidUtilities.dp((imageView16 == null || imageView16.getVisibility() != 0) ? 48.0f : 96.0f));
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(animators7);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }
                        });
                        this.runningAnimation2.start();
                        updateFieldRight(0);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                    }
                    this.runningAnimationType = 5;
                    this.runningAnimation = new AnimatorSet();
                    ArrayList<Animator> animators8 = new ArrayList<>();
                    if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.sendButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.cancelBotButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                    }
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{1.0f}));
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{1.0f}));
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{1.0f}));
                    setSlowModeButtonVisible(true);
                    this.runningAnimation.playTogether(animators8);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setSlowModeButtonVisible(boolean visible) {
        this.slowModeButton.setVisibility(visible ? 0 : 8);
        int padding = visible ? AndroidUtilities.dp(16.0f) : 0;
        if (this.messageEditText.getPaddingRight() != padding) {
            this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), padding, AndroidUtilities.dp(12.0f));
        }
    }

    private void updateFieldRight(int attachVisible) {
        ImageView imageView;
        ImageView imageView2;
        ImageView imageView3;
        LinearLayout linearLayout;
        ImageView imageView4;
        ImageView imageView5;
        ImageView imageView6;
        LinearLayout linearLayout2;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null && this.editingMessageObject == null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editTextCaption.getLayoutParams();
            int oldRightMargin = layoutParams.rightMargin;
            if (attachVisible == 1) {
                ImageView imageView7 = this.botButton;
                if (imageView7 == null || imageView7.getVisibility() != 0 || (imageView6 = this.scheduledButton) == null || imageView6.getVisibility() != 0 || (linearLayout2 = this.attachLayout) == null || linearLayout2.getVisibility() != 0) {
                    ImageView imageView8 = this.botButton;
                    if ((imageView8 == null || imageView8.getVisibility() != 0) && (((imageView4 = this.notifyButton) == null || imageView4.getVisibility() != 0) && ((imageView5 = this.scheduledButton) == null || imageView5.getTag() == null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
                }
            } else if (attachVisible != 2) {
                ImageView imageView9 = this.scheduledButton;
                if (imageView9 == null || imageView9.getTag() == null) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                ImageView imageView10 = this.botButton;
                if (imageView10 == null || imageView10.getVisibility() != 0 || (imageView3 = this.scheduledButton) == null || imageView3.getVisibility() != 0 || (linearLayout = this.attachLayout) == null || linearLayout.getVisibility() != 0) {
                    ImageView imageView11 = this.botButton;
                    if ((imageView11 == null || imageView11.getVisibility() != 0) && (((imageView = this.notifyButton) == null || imageView.getVisibility() != 0) && ((imageView2 = this.scheduledButton) == null || imageView2.getTag() == null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
                }
            }
            if (oldRightMargin != layoutParams.rightMargin) {
                this.messageEditText.setLayoutParams(layoutParams);
            }
        }
    }

    public void startMessageTransition() {
        Runnable runnable = this.moveToSendStateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.messageTransitionIsRunning = true;
            this.moveToSendStateRunnable.run();
            this.moveToSendStateRunnable = null;
        }
    }

    public boolean canShowMessageTransition() {
        return this.moveToSendStateRunnable != null;
    }

    /* access modifiers changed from: private */
    public void updateRecordInterface(int recordState) {
        ViewGroup.LayoutParams oldLayoutParams;
        final int i = recordState;
        Runnable runnable = this.moveToSendStateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.moveToSendStateRunnable = null;
        }
        this.recordCircle.voiceEnterTransitionInProgress = false;
        if (this.recordingAudioVideo) {
            if (this.recordInterfaceState != 1) {
                this.recordInterfaceState = 1;
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.setEnabled(false);
                }
                AnimatorSet animatorSet = this.emojiButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                try {
                    if (this.wakeLock == null) {
                        PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "telegram:audio_record_lock");
                        this.wakeLock = newWakeLock;
                        newWakeLock.acquire();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                AndroidUtilities.lockOrientation(this.parentActivity);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.needStartRecordAudio(0);
                }
                AnimatorSet animatorSet2 = this.runningAnimationAudio;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = this.recordPannelAnimation;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                this.recordPanel.setVisibility(0);
                this.recordCircle.setVisibility(0);
                this.recordCircle.setAmplitude(0.0d);
                this.recordDot.resetAlpha();
                this.runningAnimationAudio = new AnimatorSet();
                this.recordDot.setScaleX(0.0f);
                this.recordDot.setScaleY(0.0f);
                boolean unused = this.recordDot.enterAnimation = true;
                this.recordTimerView.setTranslationX((float) AndroidUtilities.dp(20.0f));
                this.recordTimerView.setAlpha(0.0f);
                this.slideText.setTranslationX((float) AndroidUtilities.dp(20.0f));
                this.slideText.setAlpha(0.0f);
                this.slideText.setCancelToProgress(0.0f);
                this.slideText.setSlideX(1.0f);
                this.recordCircle.setLockTranslation(10000.0f);
                this.slideText.setEnabled(true);
                this.recordIsCanceled = false;
                AnimatorSet iconChanges = new AnimatorSet();
                iconChanges.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{1.0f})});
                ImageView imageView = this.audioSendButton;
                if (imageView != null) {
                    iconChanges.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView, View.ALPHA, new float[]{0.0f})});
                }
                ImageView imageView2 = this.videoSendButton;
                if (imageView2 != null) {
                    iconChanges.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView2, View.ALPHA, new float[]{0.0f})});
                }
                BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
                if (botCommandsMenuView != null) {
                    iconChanges.playTogether(new Animator[]{ObjectAnimator.ofFloat(botCommandsMenuView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{0.0f})});
                }
                AnimatorSet viewTransition = new AnimatorSet();
                viewTransition.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{1.0f})});
                ImageView imageView3 = this.scheduledButton;
                if (imageView3 != null) {
                    viewTransition.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView3, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(30.0f)}), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f})});
                }
                LinearLayout linearLayout = this.attachLayout;
                if (linearLayout != null) {
                    viewTransition.playTogether(new Animator[]{ObjectAnimator.ofFloat(linearLayout, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(30.0f)}), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f})});
                }
                this.runningAnimationAudio.playTogether(new Animator[]{iconChanges.setDuration(150), viewTransition.setDuration(150), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{1.0f}).setDuration(300)});
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                        ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                        ChatActivityEnterView.this.slideText.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordCircle.showTooltipIfNeed();
                        ChatActivityEnterView.this.messageEditText.setAlpha(0.0f);
                    }
                });
                this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
                this.runningAnimationAudio.start();
                this.recordTimerView.start();
            } else {
                return;
            }
        } else if (!this.recordIsCanceled || i != 3) {
            PowerManager.WakeLock wakeLock2 = this.wakeLock;
            if (wakeLock2 != null) {
                try {
                    wakeLock2.release();
                    this.wakeLock = null;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            this.wasSendTyping = false;
            if (this.recordInterfaceState != 0) {
                this.accountInstance.getMessagesController().sendTyping(this.dialog_id, getThreadMessageId(), 2, 0);
                this.recordInterfaceState = 0;
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    emojiView3.setEnabled(true);
                }
                boolean shouldShowFastTransition = false;
                AnimatorSet animatorSet4 = this.runningAnimationAudio;
                if (animatorSet4 != null) {
                    shouldShowFastTransition = animatorSet4.isRunning();
                    ImageView imageView4 = this.videoSendButton;
                    if (imageView4 != null) {
                        imageView4.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                    }
                    ImageView imageView5 = this.audioSendButton;
                    if (imageView5 != null) {
                        imageView5.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                    }
                    this.runningAnimationAudio.removeAllListeners();
                    this.runningAnimationAudio.cancel();
                }
                AnimatorSet animatorSet5 = this.recordPannelAnimation;
                if (animatorSet5 != null) {
                    animatorSet5.cancel();
                }
                this.messageEditText.setVisibility(0);
                this.runningAnimationAudio = new AnimatorSet();
                if (shouldShowFastTransition || i == 4) {
                    if (this.videoSendButton == null || !isInVideoMode()) {
                        ImageView imageView6 = this.audioSendButton;
                        if (imageView6 != null) {
                            imageView6.setVisibility(0);
                        }
                    } else {
                        this.videoSendButton.setVisibility(0);
                    }
                    this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f})});
                    BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                    if (botCommandsMenuView2 != null) {
                        this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(botCommandsMenuView2, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f})});
                    }
                    ImageView imageView7 = this.audioSendButton;
                    if (imageView7 != null) {
                        imageView7.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                        AnimatorSet animatorSet6 = this.runningAnimationAudio;
                        Animator[] animatorArr = new Animator[1];
                        ImageView imageView8 = this.audioSendButton;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr[0] = ObjectAnimator.ofFloat(imageView8, property, fArr);
                        animatorSet6.playTogether(animatorArr);
                    }
                    ImageView imageView9 = this.videoSendButton;
                    if (imageView9 != null) {
                        imageView9.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                        AnimatorSet animatorSet7 = this.runningAnimationAudio;
                        Animator[] animatorArr2 = new Animator[1];
                        ImageView imageView10 = this.videoSendButton;
                        Property property2 = View.ALPHA;
                        float[] fArr2 = new float[1];
                        fArr2[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr2[0] = ObjectAnimator.ofFloat(imageView10, property2, fArr2);
                        animatorSet7.playTogether(animatorArr2);
                    }
                    ImageView imageView11 = this.scheduledButton;
                    if (imageView11 != null) {
                        this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView11, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f})});
                    }
                    LinearLayout linearLayout2 = this.attachLayout;
                    if (linearLayout2 != null) {
                        this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(linearLayout2, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f})});
                    }
                    this.recordIsCanceled = true;
                    this.runningAnimationAudio.setDuration(150);
                } else if (i == 3) {
                    this.slideText.setEnabled(false);
                    if (isInVideoMode()) {
                        this.recordedAudioBackground.setVisibility(8);
                        this.recordedAudioTimeTextView.setVisibility(8);
                        this.recordedAudioPlayButton.setVisibility(8);
                        this.recordedAudioSeekBar.setVisibility(8);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        this.recordDeleteImageView.setProgress(0.0f);
                        this.recordDeleteImageView.stopAnimation();
                    } else {
                        this.videoTimelineView.setVisibility(8);
                        this.recordedAudioBackground.setVisibility(0);
                        this.recordedAudioTimeTextView.setVisibility(0);
                        this.recordedAudioPlayButton.setVisibility(0);
                        this.recordedAudioSeekBar.setVisibility(0);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioBackground.setAlpha(0.0f);
                        this.recordedAudioTimeTextView.setAlpha(0.0f);
                        this.recordedAudioPlayButton.setAlpha(0.0f);
                        this.recordedAudioSeekBar.setAlpha(0.0f);
                        this.recordedAudioPanel.setVisibility(0);
                    }
                    this.recordDeleteImageView.setAlpha(0.0f);
                    this.recordDeleteImageView.setScaleX(0.0f);
                    this.recordDeleteImageView.setScaleY(0.0f);
                    this.recordDeleteImageView.setProgress(0.0f);
                    this.recordDeleteImageView.stopAnimation();
                    ValueAnimator transformToSeekbar = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    transformToSeekbar.addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda44(this));
                    ViewGroup parent = null;
                    if (!isInVideoMode()) {
                        ViewGroup parent2 = (ViewGroup) this.recordedAudioPanel.getParent();
                        ViewGroup.LayoutParams oldLayoutParams2 = this.recordedAudioPanel.getLayoutParams();
                        parent2.removeView(this.recordedAudioPanel);
                        FrameLayout.LayoutParams newLayoutParams = new FrameLayout.LayoutParams(parent2.getMeasuredWidth(), AndroidUtilities.dp(48.0f));
                        newLayoutParams.gravity = 80;
                        this.sizeNotifierLayout.addView(this.recordedAudioPanel, newLayoutParams);
                        this.videoTimelineView.setVisibility(8);
                        parent = parent2;
                        oldLayoutParams = oldLayoutParams2;
                    } else {
                        this.videoTimelineView.setVisibility(0);
                        oldLayoutParams = null;
                    }
                    this.recordDeleteImageView.setAlpha(0.0f);
                    this.recordDeleteImageView.setScaleX(0.0f);
                    this.recordDeleteImageView.setScaleY(0.0f);
                    AnimatorSet iconsAnimator = new AnimatorSet();
                    iconsAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{0.0f})});
                    ImageView imageView12 = this.videoSendButton;
                    if (imageView12 != null) {
                        Animator[] animatorArr3 = new Animator[3];
                        Property property3 = View.ALPHA;
                        float[] fArr3 = new float[1];
                        fArr3[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr3[0] = ObjectAnimator.ofFloat(imageView12, property3, fArr3);
                        animatorArr3[1] = ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_X, new float[]{1.0f});
                        animatorArr3[2] = ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_Y, new float[]{1.0f});
                        iconsAnimator.playTogether(animatorArr3);
                    }
                    ImageView imageView13 = this.audioSendButton;
                    if (imageView13 != null) {
                        Animator[] animatorArr4 = new Animator[3];
                        Property property4 = View.ALPHA;
                        float[] fArr4 = new float[1];
                        fArr4[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr4[0] = ObjectAnimator.ofFloat(imageView13, property4, fArr4);
                        animatorArr4[1] = ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_X, new float[]{1.0f});
                        animatorArr4[2] = ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_Y, new float[]{1.0f});
                        iconsAnimator.playTogether(animatorArr4);
                    }
                    BotCommandsMenuView botCommandsMenuView3 = this.botCommandsMenuButton;
                    if (botCommandsMenuView3 != null) {
                        iconsAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(botCommandsMenuView3, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{0.0f})});
                    }
                    iconsAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.videoSendButton != null) {
                                ChatActivityEnterView.this.videoSendButton.setScaleX(1.0f);
                                ChatActivityEnterView.this.videoSendButton.setScaleY(1.0f);
                            }
                            if (ChatActivityEnterView.this.audioSendButton != null) {
                                ChatActivityEnterView.this.audioSendButton.setScaleX(1.0f);
                                ChatActivityEnterView.this.audioSendButton.setScaleY(1.0f);
                            }
                        }
                    });
                    iconsAnimator.setDuration(150);
                    iconsAnimator.setStartDelay(150);
                    AnimatorSet videoAdditionalAnimations = new AnimatorSet();
                    if (isInVideoMode()) {
                        this.recordedAudioTimeTextView.setAlpha(0.0f);
                        this.videoTimelineView.setAlpha(0.0f);
                        videoAdditionalAnimations.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, new float[]{1.0f})});
                        videoAdditionalAnimations.setDuration(150);
                        videoAdditionalAnimations.setStartDelay(430);
                    }
                    transformToSeekbar.setDuration(isInVideoMode() ? 490 : 580);
                    this.runningAnimationAudio.playTogether(new Animator[]{iconsAnimator, transformToSeekbar, videoAdditionalAnimations});
                    final ViewGroup finalParent = parent;
                    final ViewGroup.LayoutParams finalOldLayoutParams = oldLayoutParams;
                    this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (finalParent != null) {
                                ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.recordedAudioPanel);
                                finalParent.addView(ChatActivityEnterView.this.recordedAudioPanel, finalOldLayoutParams);
                            }
                            ChatActivityEnterView.this.recordedAudioPanel.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioTimeTextView.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setScaleY(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setScaleX(1.0f);
                            ChatActivityEnterView.this.recordedAudioSeekBar.setAlpha(1.0f);
                            for (int i = 0; i < 2; i++) {
                                ChatActivityEnterView.this.emojiButton[i].setScaleY(0.0f);
                                ChatActivityEnterView.this.emojiButton[i].setScaleX(0.0f);
                                ChatActivityEnterView.this.emojiButton[i].setAlpha(0.0f);
                            }
                            if (ChatActivityEnterView.this.botCommandsMenuButton != null) {
                                ChatActivityEnterView.this.botCommandsMenuButton.setAlpha(0.0f);
                                ChatActivityEnterView.this.botCommandsMenuButton.setScaleX(0.0f);
                                ChatActivityEnterView.this.botCommandsMenuButton.setScaleY(0.0f);
                            }
                        }
                    });
                } else if (i == 2 || i == 5) {
                    if (this.videoSendButton == null || !isInVideoMode()) {
                        ImageView imageView14 = this.audioSendButton;
                        if (imageView14 != null) {
                            imageView14.setVisibility(0);
                        }
                    } else {
                        this.videoSendButton.setVisibility(0);
                    }
                    this.recordIsCanceled = true;
                    AnimatorSet iconsAnimator2 = new AnimatorSet();
                    iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f})});
                    BotCommandsMenuView botCommandsMenuView4 = this.botCommandsMenuButton;
                    if (botCommandsMenuView4 != null) {
                        iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(botCommandsMenuView4, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f})});
                    }
                    AnimatorSet recordTimer = new AnimatorSet();
                    recordTimer.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))})});
                    if (i != 5) {
                        this.audioVideoButtonContainer.setScaleX(0.0f);
                        this.audioVideoButtonContainer.setScaleY(0.0f);
                        ImageView imageView15 = this.attachButton;
                        if (imageView15 != null && imageView15.getVisibility() == 0) {
                            this.attachButton.setScaleX(0.0f);
                            this.attachButton.setScaleY(0.0f);
                        }
                        ImageView imageView16 = this.botButton;
                        if (imageView16 != null && imageView16.getVisibility() == 0) {
                            this.botButton.setScaleX(0.0f);
                            this.botButton.setScaleY(0.0f);
                        }
                        iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                        LinearLayout linearLayout3 = this.attachLayout;
                        if (linearLayout3 != null) {
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(linearLayout3, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{0.0f})});
                        }
                        ImageView imageView17 = this.attachButton;
                        if (imageView17 != null) {
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView17, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f})});
                        }
                        ImageView imageView18 = this.botButton;
                        if (imageView18 != null) {
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView18, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botButton, View.SCALE_Y, new float[]{1.0f})});
                        }
                        ImageView imageView19 = this.videoSendButton;
                        if (imageView19 != null) {
                            Animator[] animatorArr5 = new Animator[1];
                            Property property5 = View.ALPHA;
                            float[] fArr5 = new float[1];
                            fArr5[0] = isInVideoMode() ? 1.0f : 0.0f;
                            animatorArr5[0] = ObjectAnimator.ofFloat(imageView19, property5, fArr5);
                            iconsAnimator2.playTogether(animatorArr5);
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_X, new float[]{1.0f})});
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_Y, new float[]{1.0f})});
                        }
                        ImageView imageView20 = this.audioSendButton;
                        if (imageView20 != null) {
                            Animator[] animatorArr6 = new Animator[1];
                            Property property6 = View.ALPHA;
                            float[] fArr6 = new float[1];
                            fArr6[0] = isInVideoMode() ? 0.0f : 1.0f;
                            animatorArr6[0] = ObjectAnimator.ofFloat(imageView20, property6, fArr6);
                            iconsAnimator2.playTogether(animatorArr6);
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_X, new float[]{1.0f})});
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_Y, new float[]{1.0f})});
                        }
                        ImageView imageView21 = this.scheduledButton;
                        if (imageView21 != null) {
                            iconsAnimator2.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView21, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f})});
                        }
                    } else {
                        AnimatorSet icons2 = new AnimatorSet();
                        icons2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                        LinearLayout linearLayout4 = this.attachLayout;
                        if (linearLayout4 != null) {
                            icons2.playTogether(new Animator[]{ObjectAnimator.ofFloat(linearLayout4, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f})});
                        }
                        ImageView imageView22 = this.scheduledButton;
                        if (imageView22 != null) {
                            icons2.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView22, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f})});
                        }
                        icons2.setDuration(150);
                        icons2.setStartDelay(110);
                        icons2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                float f = 0.0f;
                                if (ChatActivityEnterView.this.audioSendButton != null) {
                                    ChatActivityEnterView.this.audioSendButton.setAlpha(ChatActivityEnterView.this.isInVideoMode() ? 0.0f : 1.0f);
                                }
                                if (ChatActivityEnterView.this.videoSendButton != null) {
                                    ImageView access$2000 = ChatActivityEnterView.this.videoSendButton;
                                    if (ChatActivityEnterView.this.isInVideoMode()) {
                                        f = 1.0f;
                                    }
                                    access$2000.setAlpha(f);
                                }
                            }
                        });
                        this.runningAnimationAudio.playTogether(new Animator[]{icons2});
                    }
                    iconsAnimator2.setDuration(150);
                    iconsAnimator2.setStartDelay(700);
                    recordTimer.setDuration(200);
                    recordTimer.setStartDelay(200);
                    this.messageEditText.setTranslationX(0.0f);
                    ObjectAnimator messageEditTextAniamtor = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f});
                    messageEditTextAniamtor.setStartDelay(300);
                    messageEditTextAniamtor.setDuration(200);
                    AnimatorSet animatorSet8 = this.runningAnimationAudio;
                    RecordCircle recordCircle2 = this.recordCircle;
                    animatorSet8.playTogether(new Animator[]{iconsAnimator2, recordTimer, messageEditTextAniamtor, ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation}).setDuration(200)});
                    if (i == 5) {
                        this.recordCircle.canceledByGesture();
                        ObjectAnimator cancel = ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f}).setDuration(200);
                        cancel.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                        this.runningAnimationAudio.playTogether(new Animator[]{cancel});
                    } else {
                        Animator recordCircleAnimator = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", new float[]{1.0f});
                        recordCircleAnimator.setDuration(360);
                        recordCircleAnimator.setStartDelay(490);
                        this.runningAnimationAudio.playTogether(new Animator[]{recordCircleAnimator});
                    }
                    this.recordDot.playDeleteAnimation();
                } else {
                    if (this.videoSendButton == null || !isInVideoMode()) {
                        ImageView imageView23 = this.audioSendButton;
                        if (imageView23 != null) {
                            imageView23.setVisibility(0);
                        }
                    } else {
                        this.videoSendButton.setVisibility(0);
                    }
                    AnimatorSet iconsAnimator3 = new AnimatorSet();
                    iconsAnimator3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                    BotCommandsMenuView botCommandsMenuView5 = this.botCommandsMenuButton;
                    if (botCommandsMenuView5 != null) {
                        iconsAnimator3.playTogether(new Animator[]{ObjectAnimator.ofFloat(botCommandsMenuView5, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f})});
                    }
                    ImageView imageView24 = this.audioSendButton;
                    if (imageView24 != null) {
                        imageView24.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                        Animator[] animatorArr7 = new Animator[1];
                        ImageView imageView25 = this.audioSendButton;
                        Property property7 = View.ALPHA;
                        float[] fArr7 = new float[1];
                        fArr7[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr7[0] = ObjectAnimator.ofFloat(imageView25, property7, fArr7);
                        iconsAnimator3.playTogether(animatorArr7);
                    }
                    ImageView imageView26 = this.videoSendButton;
                    if (imageView26 != null) {
                        imageView26.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                        Animator[] animatorArr8 = new Animator[1];
                        ImageView imageView27 = this.videoSendButton;
                        Property property8 = View.ALPHA;
                        float[] fArr8 = new float[1];
                        fArr8[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr8[0] = ObjectAnimator.ofFloat(imageView27, property8, fArr8);
                        iconsAnimator3.playTogether(animatorArr8);
                    }
                    LinearLayout linearLayout5 = this.attachLayout;
                    if (linearLayout5 != null) {
                        linearLayout5.setTranslationX(0.0f);
                        iconsAnimator3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f})});
                    }
                    ImageView imageView28 = this.scheduledButton;
                    if (imageView28 != null) {
                        imageView28.setTranslationX(0.0f);
                        iconsAnimator3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f})});
                    }
                    iconsAnimator3.setDuration(150);
                    iconsAnimator3.setStartDelay(200);
                    AnimatorSet recordTimer2 = new AnimatorSet();
                    recordTimer2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(40.0f)}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(40.0f)})});
                    recordTimer2.setDuration(150);
                    Animator recordCircleAnimator2 = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", new float[]{1.0f});
                    recordCircleAnimator2.setDuration(this.messageTransitionIsRunning ? 220 : 360);
                    this.messageEditText.setTranslationX(0.0f);
                    ObjectAnimator messageEditTextAniamtor2 = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f});
                    messageEditTextAniamtor2.setStartDelay(150);
                    messageEditTextAniamtor2.setDuration(200);
                    this.runningAnimationAudio.playTogether(new Animator[]{iconsAnimator3, recordTimer2, messageEditTextAniamtor2, recordCircleAnimator2});
                }
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                            if (i != 3) {
                                ChatActivityEnterView.this.messageEditText.requestFocus();
                            }
                            ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                            if (ChatActivityEnterView.this.attachLayout != null) {
                                ChatActivityEnterView.this.attachLayout.setTranslationX(0.0f);
                            }
                            ChatActivityEnterView.this.slideText.setCancelToProgress(0.0f);
                            ChatActivityEnterView.this.delegate.onAudioVideoInterfaceUpdated();
                            ChatActivityEnterView.this.updateSendAsButton();
                        }
                    }
                });
                this.runningAnimationAudio.start();
                this.recordTimerView.stop();
            } else {
                return;
            }
        } else {
            return;
        }
        this.delegate.onAudioVideoInterfaceUpdated();
        updateSendAsButton();
    }

    /* renamed from: lambda$updateRecordInterface$42$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m697xbCLASSNAMEa979(ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        if (!isInVideoMode()) {
            this.recordCircle.setTransformToSeekbar(value);
            this.seekBarWaveform.setWaveScaling(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.invalidate();
            this.recordedAudioTimeTextView.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleX(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleY(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            return;
        }
        this.recordCircle.setExitTransition(value);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.recordingAudioVideo) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        this.delegate = chatActivityEnterViewDelegate;
    }

    public void setCommand(MessageObject messageObject, String command, boolean longPress, boolean username) {
        String text;
        MessageObject messageObject2 = messageObject;
        String str = command;
        if (str != null && getVisibility() == 0) {
            TLRPC.User user = null;
            if (longPress) {
                String text2 = this.messageEditText.getText().toString();
                if (messageObject2 != null && DialogObject.isChatDialog(this.dialog_id)) {
                    user = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject2.messageOwner.from_id.user_id));
                }
                if ((this.botCount != 1 || username) && user != null && user.bot && !str.contains("@")) {
                    text = String.format(Locale.US, "%s@%s", new Object[]{str, user.username}) + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                } else {
                    text = str + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                }
                this.ignoreTextChange = true;
                this.messageEditText.setText(text);
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.getText().length());
                this.ignoreTextChange = false;
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
                }
                if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                    openKeyboard();
                }
            } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
                if (messageObject2 != null && DialogObject.isChatDialog(this.dialog_id)) {
                    user = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject2.messageOwner.from_id.user_id));
                }
                TLRPC.User user2 = user;
                if ((this.botCount != 1 || username) && user2 != null && user2.bot && !str.contains("@")) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str, user2.username}), this.dialog_id, this.replyingMessageObject, getThreadMessage(), (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                    return;
                }
                TLRPC.User user3 = user2;
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(command, this.dialog_id, this.replyingMessageObject, getThreadMessage(), (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
            } else {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    SimpleTextView simpleTextView = this.slowModeButton;
                    chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x030c  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0296  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setEditingMessageObject(org.telegram.messenger.MessageObject r18, boolean r19) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            org.telegram.tgnet.TLRPC$TL_document r0 = r1.audioToSend
            if (r0 != 0) goto L_0x046d
            org.telegram.messenger.VideoEditedInfo r0 = r1.videoToSendMessageObject
            if (r0 != 0) goto L_0x046d
            org.telegram.messenger.MessageObject r0 = r1.editingMessageObject
            if (r0 != r2) goto L_0x0014
            goto L_0x046d
        L_0x0014:
            r4 = 0
            r5 = 1
            if (r0 == 0) goto L_0x001a
            r0 = 1
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            r6 = r0
            r1.editingMessageObject = r2
            r1.editingCaption = r3
            r7 = 0
            r0 = 0
            r9 = 1036831949(0x3dcccccd, float:0.1)
            r10 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x0312
            android.animation.AnimatorSet r11 = r1.doneButtonAnimation
            if (r11 == 0) goto L_0x0032
            r11.cancel()
            r1.doneButtonAnimation = r7
        L_0x0032:
            android.widget.FrameLayout r11 = r1.doneButtonContainer
            r11.setVisibility(r4)
            android.widget.ImageView r11 = r1.doneButtonImage
            r11.setScaleX(r9)
            android.widget.ImageView r11 = r1.doneButtonImage
            r11.setScaleY(r9)
            android.widget.ImageView r9 = r1.doneButtonImage
            r9.setAlpha(r0)
            android.widget.ImageView r0 = r1.doneButtonImage
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.alpha(r10)
            android.view.ViewPropertyAnimator r0 = r0.scaleX(r10)
            android.view.ViewPropertyAnimator r0 = r0.scaleY(r10)
            r9 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r9)
            org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r0 = r0.setInterpolator(r9)
            r0.start()
            if (r3 == 0) goto L_0x0079
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            int r0 = r0.maxCaptionLength
            r1.currentLimit = r0
            org.telegram.messenger.MessageObject r0 = r1.editingMessageObject
            java.lang.CharSequence r0 = r0.caption
            r9 = r0
            goto L_0x0088
        L_0x0079:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            int r0 = r0.maxMessageLength
            r1.currentLimit = r0
            org.telegram.messenger.MessageObject r0 = r1.editingMessageObject
            java.lang.CharSequence r0 = r0.messageText
            r9 = r0
        L_0x0088:
            if (r9 == 0) goto L_0x0284
            org.telegram.messenger.MessageObject r0 = r1.editingMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r0.entities
            org.telegram.messenger.MediaDataController.sortEntities(r10)
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r9)
            r11 = r0
            int r0 = r11.length()
            java.lang.Class<java.lang.Object> r12 = java.lang.Object.class
            java.lang.Object[] r12 = r11.getSpans(r4, r0, r12)
            if (r12 == 0) goto L_0x00b4
            int r0 = r12.length
            if (r0 <= 0) goto L_0x00b4
            r0 = 0
        L_0x00a9:
            int r13 = r12.length
            if (r0 >= r13) goto L_0x00b4
            r13 = r12[r0]
            r11.removeSpan(r13)
            int r0 = r0 + 1
            goto L_0x00a9
        L_0x00b4:
            if (r10 == 0) goto L_0x0267
            r0 = 0
        L_0x00b7:
            int r13 = r10.size()     // Catch:{ Exception -> 0x0260 }
            if (r0 >= r13) goto L_0x025d
            java.lang.Object r13 = r10.get(r0)     // Catch:{ Exception -> 0x0260 }
            org.telegram.tgnet.TLRPC$MessageEntity r13 = (org.telegram.tgnet.TLRPC.MessageEntity) r13     // Catch:{ Exception -> 0x0260 }
            int r14 = r13.offset     // Catch:{ Exception -> 0x0260 }
            int r15 = r13.length     // Catch:{ Exception -> 0x0260 }
            int r14 = r14 + r15
            int r15 = r11.length()     // Catch:{ Exception -> 0x0260 }
            if (r14 <= r15) goto L_0x00d2
            r16 = r6
            goto L_0x0252
        L_0x00d2:
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName     // Catch:{ Exception -> 0x0260 }
            r15 = 3
            r7 = 32
            java.lang.String r4 = ""
            if (r14 == 0) goto L_0x0127
            int r14 = r13.offset     // Catch:{ Exception -> 0x0260 }
            int r8 = r13.length     // Catch:{ Exception -> 0x0260 }
            int r14 = r14 + r8
            int r8 = r11.length()     // Catch:{ Exception -> 0x0260 }
            if (r14 >= r8) goto L_0x00fc
            int r8 = r13.offset     // Catch:{ Exception -> 0x00f7 }
            int r14 = r13.length     // Catch:{ Exception -> 0x00f7 }
            int r8 = r8 + r14
            char r8 = r11.charAt(r8)     // Catch:{ Exception -> 0x00f7 }
            if (r8 != r7) goto L_0x00fc
            int r7 = r13.length     // Catch:{ Exception -> 0x00f7 }
            int r7 = r7 + r5
            r13.length = r7     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00fc
        L_0x00f7:
            r0 = move-exception
            r16 = r6
            goto L_0x0263
        L_0x00fc:
            org.telegram.ui.Components.URLSpanUserMention r7 = new org.telegram.ui.Components.URLSpanUserMention     // Catch:{ Exception -> 0x0260 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0260 }
            r8.<init>()     // Catch:{ Exception -> 0x0260 }
            r8.append(r4)     // Catch:{ Exception -> 0x0260 }
            r4 = r13
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r4 = (org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName) r4     // Catch:{ Exception -> 0x0260 }
            org.telegram.tgnet.TLRPC$InputUser r4 = r4.user_id     // Catch:{ Exception -> 0x0260 }
            r16 = r6
            long r5 = r4.user_id     // Catch:{ Exception -> 0x025b }
            r8.append(r5)     // Catch:{ Exception -> 0x025b }
            java.lang.String r4 = r8.toString()     // Catch:{ Exception -> 0x025b }
            r7.<init>(r4, r15)     // Catch:{ Exception -> 0x025b }
            int r4 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r5 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r6 = r13.length     // Catch:{ Exception -> 0x025b }
            int r5 = r5 + r6
            r6 = 33
            r11.setSpan(r7, r4, r5, r6)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x0127:
            r16 = r6
            boolean r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName     // Catch:{ Exception -> 0x025b }
            if (r5 == 0) goto L_0x0170
            int r5 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r6 = r13.length     // Catch:{ Exception -> 0x025b }
            int r5 = r5 + r6
            int r6 = r11.length()     // Catch:{ Exception -> 0x025b }
            if (r5 >= r6) goto L_0x0149
            int r5 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r6 = r13.length     // Catch:{ Exception -> 0x025b }
            int r5 = r5 + r6
            char r5 = r11.charAt(r5)     // Catch:{ Exception -> 0x025b }
            if (r5 != r7) goto L_0x0149
            int r5 = r13.length     // Catch:{ Exception -> 0x025b }
            r6 = 1
            int r5 = r5 + r6
            r13.length = r5     // Catch:{ Exception -> 0x025b }
        L_0x0149:
            org.telegram.ui.Components.URLSpanUserMention r5 = new org.telegram.ui.Components.URLSpanUserMention     // Catch:{ Exception -> 0x025b }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x025b }
            r6.<init>()     // Catch:{ Exception -> 0x025b }
            r6.append(r4)     // Catch:{ Exception -> 0x025b }
            r4 = r13
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r4 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r4     // Catch:{ Exception -> 0x025b }
            long r7 = r4.user_id     // Catch:{ Exception -> 0x025b }
            r6.append(r7)     // Catch:{ Exception -> 0x025b }
            java.lang.String r4 = r6.toString()     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4, r15)     // Catch:{ Exception -> 0x025b }
            int r4 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.length     // Catch:{ Exception -> 0x025b }
            int r6 = r6 + r7
            r7 = 33
            r11.setSpan(r5, r4, r6, r7)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x0170:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode     // Catch:{ Exception -> 0x025b }
            if (r4 != 0) goto L_0x0236
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x017a
            goto L_0x0236
        L_0x017a:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x019b
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r6 = 1
            r5 = r5 | r6
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x019b:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x01bc
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r5 = r5 | 2
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x01bc:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x01de
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r6 = 8
            r5 = r5 | r6
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x01de:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x01fe
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r5 = r5 | 16
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x01fe:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x0216
            org.telegram.ui.Components.URLSpanReplacement r4 = new org.telegram.ui.Components.URLSpanReplacement     // Catch:{ Exception -> 0x025b }
            java.lang.String r5 = r13.url     // Catch:{ Exception -> 0x025b }
            r4.<init>(r5)     // Catch:{ Exception -> 0x025b }
            int r5 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.length     // Catch:{ Exception -> 0x025b }
            int r6 = r6 + r7
            r7 = 33
            r11.setSpan(r4, r5, r6, r7)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x0216:
            boolean r4 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageEntitySpoiler     // Catch:{ Exception -> 0x025b }
            if (r4 == 0) goto L_0x0252
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
            goto L_0x0252
        L_0x0236:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x025b }
            r4.<init>()     // Catch:{ Exception -> 0x025b }
            int r5 = r4.flags     // Catch:{ Exception -> 0x025b }
            r5 = r5 | 4
            r4.flags = r5     // Catch:{ Exception -> 0x025b }
            org.telegram.ui.Components.TextStyleSpan r5 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x025b }
            r5.<init>(r4)     // Catch:{ Exception -> 0x025b }
            int r6 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r7 = r13.offset     // Catch:{ Exception -> 0x025b }
            int r8 = r13.length     // Catch:{ Exception -> 0x025b }
            int r7 = r7 + r8
            r8 = 1
            org.telegram.messenger.MediaDataController.addStyleToText(r5, r6, r7, r11, r8)     // Catch:{ Exception -> 0x025b }
        L_0x0252:
            int r0 = r0 + 1
            r6 = r16
            r4 = 0
            r5 = 1
            r7 = 0
            goto L_0x00b7
        L_0x025b:
            r0 = move-exception
            goto L_0x0263
        L_0x025d:
            r16 = r6
            goto L_0x0269
        L_0x0260:
            r0 = move-exception
            r16 = r6
        L_0x0263:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0269
        L_0x0267:
            r16 = r6
        L_0x0269:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            r0.<init>(r11)
            org.telegram.ui.Components.EditTextCaption r4 = r1.messageEditText
            android.text.TextPaint r4 = r4.getPaint()
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r5 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r4, r5, r6)
            goto L_0x0288
        L_0x0284:
            r16 = r6
            java.lang.String r0 = ""
        L_0x0288:
            java.lang.CharSequence r4 = r1.draftMessage
            if (r4 != 0) goto L_0x02a4
            if (r16 != 0) goto L_0x02a4
            org.telegram.ui.Components.EditTextCaption r4 = r1.messageEditText
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x029d
            org.telegram.ui.Components.EditTextCaption r4 = r1.messageEditText
            android.text.Editable r4 = r4.getText()
            goto L_0x029e
        L_0x029d:
            r4 = 0
        L_0x029e:
            r1.draftMessage = r4
            boolean r4 = r1.messageWebPageSearch
            r1.draftSearchWebpage = r4
        L_0x02a4:
            org.telegram.messenger.MessageObject r4 = r1.editingMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            r1.messageWebPageSearch = r4
            boolean r4 = r1.keyboardVisible
            if (r4 != 0) goto L_0x02bf
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda41 r4 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda41
            r4.<init>(r1, r0)
            r1.setTextFieldRunnable = r4
            r5 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r5)
            goto L_0x02cc
        L_0x02bf:
            java.lang.Runnable r4 = r1.setTextFieldRunnable
            if (r4 == 0) goto L_0x02c9
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r4 = 0
            r1.setTextFieldRunnable = r4
        L_0x02c9:
            r1.setFieldText(r0)
        L_0x02cc:
            org.telegram.ui.Components.EditTextCaption r4 = r1.messageEditText
            r4.requestFocus()
            r17.openKeyboard()
            org.telegram.ui.Components.EditTextCaption r4 = r1.messageEditText
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.rightMargin = r5
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            r5.setLayoutParams(r4)
            android.view.View r5 = r1.sendButton
            r6 = 8
            r5.setVisibility(r6)
            r5 = 0
            r1.setSlowModeButtonVisible(r5)
            android.widget.ImageView r5 = r1.cancelBotButton
            r5.setVisibility(r6)
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            r5.setVisibility(r6)
            android.widget.LinearLayout r5 = r1.attachLayout
            r5.setVisibility(r6)
            android.widget.FrameLayout r5 = r1.sendButtonContainer
            r5.setVisibility(r6)
            android.widget.ImageView r5 = r1.scheduledButton
            if (r5 == 0) goto L_0x030f
            r5.setVisibility(r6)
        L_0x030f:
            r4 = 1
            goto L_0x0466
        L_0x0312:
            r16 = r6
            java.lang.Runnable r4 = r1.setTextFieldRunnable
            if (r4 == 0) goto L_0x031e
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r4 = 0
            r1.setTextFieldRunnable = r4
        L_0x031e:
            android.widget.FrameLayout r4 = r1.doneButtonContainer
            r5 = 8
            r4.setVisibility(r5)
            r4 = -1
            r1.currentLimit = r4
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r4 = r1.delegate
            r5 = 0
            r4.onMessageEditEnd(r5)
            android.widget.FrameLayout r4 = r1.sendButtonContainer
            r4.setVisibility(r5)
            android.widget.ImageView r4 = r1.cancelBotButton
            r4.setScaleX(r9)
            android.widget.ImageView r4 = r1.cancelBotButton
            r4.setScaleY(r9)
            android.widget.ImageView r4 = r1.cancelBotButton
            r4.setAlpha(r0)
            android.widget.ImageView r4 = r1.cancelBotButton
            r5 = 8
            r4.setVisibility(r5)
            int r4 = r1.slowModeTimer
            if (r4 <= 0) goto L_0x03d6
            boolean r4 = r17.isInScheduleMode()
            if (r4 != 0) goto L_0x03d6
            int r4 = r1.slowModeTimer
            r5 = 2147483647(0x7fffffff, float:NaN)
            if (r4 != r5) goto L_0x0382
            android.view.View r4 = r1.sendButton
            r4.setScaleX(r10)
            android.view.View r4 = r1.sendButton
            r4.setScaleY(r10)
            android.view.View r4 = r1.sendButton
            r4.setAlpha(r10)
            android.view.View r4 = r1.sendButton
            r5 = 0
            r4.setVisibility(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleX(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleY(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setAlpha(r0)
            r1.setSlowModeButtonVisible(r5)
            goto L_0x03ab
        L_0x0382:
            android.view.View r4 = r1.sendButton
            r4.setScaleX(r9)
            android.view.View r4 = r1.sendButton
            r4.setScaleY(r9)
            android.view.View r4 = r1.sendButton
            r4.setAlpha(r0)
            android.view.View r4 = r1.sendButton
            r5 = 8
            r4.setVisibility(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleX(r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleY(r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setAlpha(r10)
            r4 = 1
            r1.setSlowModeButtonVisible(r4)
        L_0x03ab:
            android.widget.LinearLayout r4 = r1.attachLayout
            r5 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            r4.setScaleX(r5)
            android.widget.LinearLayout r4 = r1.attachLayout
            r4.setAlpha(r0)
            android.widget.LinearLayout r4 = r1.attachLayout
            r5 = 8
            r4.setVisibility(r5)
            android.widget.FrameLayout r4 = r1.audioVideoButtonContainer
            r4.setScaleX(r9)
            android.widget.FrameLayout r4 = r1.audioVideoButtonContainer
            r4.setScaleY(r9)
            android.widget.FrameLayout r4 = r1.audioVideoButtonContainer
            r4.setAlpha(r0)
            android.widget.FrameLayout r0 = r1.audioVideoButtonContainer
            r4 = 8
            r0.setVisibility(r4)
            goto L_0x0423
        L_0x03d6:
            android.view.View r4 = r1.sendButton
            r4.setScaleX(r9)
            android.view.View r4 = r1.sendButton
            r4.setScaleY(r9)
            android.view.View r4 = r1.sendButton
            r4.setAlpha(r0)
            android.view.View r4 = r1.sendButton
            r5 = 8
            r4.setVisibility(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleX(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setScaleY(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r1.slowModeButton
            r4.setAlpha(r0)
            r4 = 0
            r1.setSlowModeButtonVisible(r4)
            android.widget.LinearLayout r0 = r1.attachLayout
            r0.setScaleX(r10)
            android.widget.LinearLayout r0 = r1.attachLayout
            r0.setAlpha(r10)
            android.widget.LinearLayout r0 = r1.attachLayout
            r0.setVisibility(r4)
            android.widget.FrameLayout r0 = r1.audioVideoButtonContainer
            r0.setScaleX(r10)
            android.widget.FrameLayout r0 = r1.audioVideoButtonContainer
            r0.setScaleY(r10)
            android.widget.FrameLayout r0 = r1.audioVideoButtonContainer
            r0.setAlpha(r10)
            android.widget.FrameLayout r0 = r1.audioVideoButtonContainer
            r4 = 0
            r0.setVisibility(r4)
        L_0x0423:
            android.widget.ImageView r0 = r1.scheduledButton
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0440
            android.widget.ImageView r0 = r1.scheduledButton
            r0.setScaleX(r10)
            android.widget.ImageView r0 = r1.scheduledButton
            r0.setScaleY(r10)
            android.widget.ImageView r0 = r1.scheduledButton
            r0.setAlpha(r10)
            android.widget.ImageView r0 = r1.scheduledButton
            r4 = 0
            r0.setVisibility(r4)
        L_0x0440:
            org.telegram.ui.Components.EditTextCaption r0 = r1.messageEditText
            java.lang.CharSequence r4 = r1.draftMessage
            r0.setText(r4)
            r4 = 0
            r1.draftMessage = r4
            boolean r0 = r1.draftSearchWebpage
            r1.messageWebPageSearch = r0
            org.telegram.ui.Components.EditTextCaption r0 = r1.messageEditText
            int r4 = r0.length()
            r0.setSelection(r4)
            int r0 = r17.getVisibility()
            if (r0 != 0) goto L_0x0462
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r1.delegate
            r0.onAttachButtonShow()
        L_0x0462:
            r4 = 1
            r1.updateFieldRight(r4)
        L_0x0466:
            r1.updateFieldHint(r4)
            r1.updateSendAsButton(r4)
            return
        L_0x046d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEditingMessageObject(org.telegram.messenger.MessageObject, boolean):void");
    }

    /* renamed from: lambda$setEditingMessageObject$43$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m691x885CLASSNAME(CharSequence textToSetWithKeyboard) {
        setFieldText(textToSetWithKeyboard);
        this.setTextFieldRunnable = null;
    }

    public ImageView getAttachButton() {
        return this.attachButton;
    }

    public View getSendButton() {
        return this.sendButton.getVisibility() == 0 ? this.sendButton : this.audioVideoButtonContainer;
    }

    public View getAudioVideoButtonContainer() {
        return this.audioVideoButtonContainer;
    }

    public View getEmojiButton() {
        return this.emojiButton[0];
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public TrendingStickersAlert getTrendingStickersAlert() {
        return this.trendingStickersAlert;
    }

    public void updateColors() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
        if (actionBarPopupWindowLayout != null) {
            int count = actionBarPopupWindowLayout.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.sendPopupLayout.getChildAt(a);
                if (view instanceof ActionBarMenuSubItem) {
                    ActionBarMenuSubItem item = (ActionBarMenuSubItem) view;
                    item.setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                    item.setSelectorColor(getThemedColor("dialogButtonSelector"));
                }
            }
            this.sendPopupLayout.setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupLayout.invalidate();
            }
        }
        updateRecordedDeleteIconColors();
        this.recordCircle.updateColors();
        this.recordDot.updateColors();
        this.slideText.updateColors();
        this.recordTimerView.updateColors();
        this.videoTimelineView.updateColors();
        NumberTextView numberTextView = this.captionLimitView;
        if (!(numberTextView == null || this.messageEditText == null)) {
            if (this.codePointCount - this.currentLimit < 0) {
                numberTextView.setTextColor(getThemedColor("windowBackgroundWhiteRedText"));
            } else {
                numberTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
            }
        }
        int color = getThemedColor("chat_messagePanelVoicePressed");
        this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * ((this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
        BotCommandsMenuContainer botCommandsMenuContainer2 = this.botCommandsMenuContainer;
        if (botCommandsMenuContainer2 != null) {
            botCommandsMenuContainer2.updateColors();
        }
        BotKeyboardView botKeyboardView2 = this.botKeyboardView;
        if (botKeyboardView2 != null) {
            botKeyboardView2.updateColors();
        }
        for (int i = 0; i < 2; i++) {
            this.emojiButton[i].setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            if (Build.VERSION.SDK_INT >= 21) {
                this.emojiButton[i].setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21")));
            }
        }
    }

    private void updateRecordedDeleteIconColors() {
        int dotColor = getThemedColor("chat_recordedVoiceDot");
        int background = getThemedColor("chat_messagePanelBackground");
        int greyColor = getThemedColor("chat_messagePanelVoiceDelete");
        this.recordDeleteImageView.setLayerColor("Cup Red.**", dotColor);
        this.recordDeleteImageView.setLayerColor("Box Red.**", dotColor);
        this.recordDeleteImageView.setLayerColor("Cup Grey.**", greyColor);
        this.recordDeleteImageView.setLayerColor("Box Grey.**", greyColor);
        this.recordDeleteImageView.setLayerColor("Line 1.**", background);
        this.recordDeleteImageView.setLayerColor("Line 2.**", background);
        this.recordDeleteImageView.setLayerColor("Line 3.**", background);
    }

    public void setFieldText(CharSequence text) {
        setFieldText(text, true);
    }

    public void setFieldText(CharSequence text, boolean ignoreChange) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            this.ignoreTextChange = ignoreChange;
            editTextCaption.setText(text);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            this.ignoreTextChange = false;
            if (ignoreChange && (chatActivityEnterViewDelegate = this.delegate) != null) {
                chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int start) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setSelection(start, editTextCaption.length());
        }
    }

    public int getCursorPosition() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        return editTextCaption.getSelectionStart();
    }

    public int getSelectionLength() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            return editTextCaption.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return 0;
        }
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !am.isTouchExplorationEnabled()) {
            try {
                this.messageEditText.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setFieldFocused(boolean focus) {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !am.isTouchExplorationEnabled()) {
            if (!focus) {
                EditTextCaption editTextCaption = this.messageEditText;
                if (editTextCaption != null && editTextCaption.isFocused()) {
                    if (!this.keyboardVisible || this.isPaused) {
                        this.messageEditText.clearFocus();
                    }
                }
            } else if (this.searchingType == 0 && !this.messageEditText.isFocused()) {
                ChatActivityEnterView$$ExternalSyntheticLambda37 chatActivityEnterView$$ExternalSyntheticLambda37 = new ChatActivityEnterView$$ExternalSyntheticLambda37(this);
                this.focusRunnable = chatActivityEnterView$$ExternalSyntheticLambda37;
                AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda37, 600);
            }
        }
    }

    /* renamed from: lambda$setFieldFocused$44$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m692xffa0deb7() {
        boolean allowFocus;
        EditTextCaption editTextCaption;
        this.focusRunnable = null;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                View layout = ((LaunchActivity) activity).getLayersActionBarLayout();
                allowFocus = layout == null || layout.getVisibility() != 0;
            } else {
                allowFocus = true;
            }
        } else {
            allowFocus = true;
        }
        if (!this.isPaused && allowFocus && (editTextCaption = this.messageEditText) != null) {
            try {
                editTextCaption.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean hasText() {
        EditTextCaption editTextCaption = this.messageEditText;
        return editTextCaption != null && editTextCaption.length() > 0;
    }

    public EditTextCaption getEditField() {
        return this.messageEditText;
    }

    public CharSequence getDraftMessage() {
        if (this.editingMessageObject != null) {
            if (TextUtils.isEmpty(this.draftMessage)) {
                return null;
            }
            return this.draftMessage;
        } else if (hasText()) {
            return this.messageEditText.getText();
        } else {
            return null;
        }
    }

    public CharSequence getFieldText() {
        if (hasText()) {
            return this.messageEditText.getText();
        }
        return null;
    }

    public void updateScheduleButton(boolean animated) {
        ImageView imageView;
        ImageView imageView2;
        boolean notifyVisible = false;
        int i = 0;
        if (DialogObject.isChatDialog(this.dialog_id)) {
            TLRPC.Chat currentChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            this.silent = notificationsSettings.getBoolean("silent_" + this.dialog_id, false);
            this.canWriteToChannel = ChatObject.isChannel(currentChat) && (currentChat.creator || (currentChat.admin_rights != null && currentChat.admin_rights.post_messages)) && !currentChat.megagroup;
            if (this.notifyButton != null) {
                notifyVisible = this.canWriteToChannel;
                if (this.notifySilentDrawable == null) {
                    this.notifySilentDrawable = new CrossOutDrawable(getContext(), NUM, "chat_messagePanelIcons");
                }
                this.notifySilentDrawable.setCrossOut(this.silent, false);
                this.notifyButton.setImageDrawable(this.notifySilentDrawable);
            }
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                updateFieldRight(linearLayout.getVisibility() == 0 ? 1 : 0);
            }
        }
        boolean hasScheduled = this.delegate != null && !isInScheduleMode() && this.delegate.hasScheduledMessages();
        final boolean visible = hasScheduled && !this.scheduleButtonHidden && !this.recordingAudioVideo;
        ImageView imageView3 = this.scheduledButton;
        float f = 96.0f;
        if (imageView3 != null) {
            if ((imageView3.getTag() == null || !visible) && (this.scheduledButton.getTag() != null || visible)) {
                this.scheduledButton.setTag(visible ? 1 : null);
            } else if (this.notifyButton != null) {
                if (hasScheduled || !notifyVisible || this.scheduledButton.getVisibility() == 0) {
                    i = 8;
                }
                int newVisibility = i;
                if (newVisibility != this.notifyButton.getVisibility()) {
                    this.notifyButton.setVisibility(newVisibility);
                    LinearLayout linearLayout2 = this.attachLayout;
                    if (linearLayout2 != null) {
                        ImageView imageView4 = this.botButton;
                        if ((imageView4 == null || imageView4.getVisibility() == 8) && ((imageView2 = this.notifyButton) == null || imageView2.getVisibility() == 8)) {
                            f = 48.0f;
                        }
                        linearLayout2.setPivotX((float) AndroidUtilities.dp(f));
                        return;
                    }
                    return;
                }
                return;
            } else {
                return;
            }
        }
        AnimatorSet animatorSet = this.scheduledButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.scheduledButtonAnimation = null;
        }
        float f2 = 0.1f;
        if (!animated || notifyVisible) {
            ImageView imageView5 = this.scheduledButton;
            if (imageView5 != null) {
                imageView5.setVisibility(visible ? 0 : 8);
                this.scheduledButton.setAlpha(visible ? 1.0f : 0.0f);
                this.scheduledButton.setScaleX(visible ? 1.0f : 0.1f);
                ImageView imageView6 = this.scheduledButton;
                if (visible) {
                    f2 = 1.0f;
                }
                imageView6.setScaleY(f2);
                ImageView imageView7 = this.notifyButton;
                if (imageView7 != null) {
                    if (!notifyVisible || this.scheduledButton.getVisibility() == 0) {
                        i = 8;
                    }
                    imageView7.setVisibility(i);
                }
            }
        } else {
            ImageView imageView8 = this.scheduledButton;
            if (imageView8 != null) {
                if (visible) {
                    imageView8.setVisibility(0);
                }
                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(24.0f));
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.scheduledButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView9 = this.scheduledButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = visible ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView9, property, fArr);
                ImageView imageView10 = this.scheduledButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = visible ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView10, property2, fArr2);
                ImageView imageView11 = this.scheduledButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (visible) {
                    f2 = 1.0f;
                }
                fArr3[0] = f2;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView11, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.scheduledButtonAnimation.setDuration(180);
                this.scheduledButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = ChatActivityEnterView.this.scheduledButtonAnimation = null;
                        if (!visible) {
                            ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                        }
                    }
                });
                this.scheduledButtonAnimation.start();
            }
        }
        LinearLayout linearLayout3 = this.attachLayout;
        if (linearLayout3 != null) {
            ImageView imageView12 = this.botButton;
            if ((imageView12 == null || imageView12.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) {
                f = 48.0f;
            }
            linearLayout3.setPivotX((float) AndroidUtilities.dp(f));
        }
    }

    public void updateSendAsButton() {
        updateSendAsButton(true);
    }

    public void updateSendAsButton(boolean animated) {
        TLRPC.Peer defPeer;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && this.delegate != null) {
            TLRPC.ChatFull full = chatActivity.getMessagesController().getChatFull(-this.dialog_id);
            TLRPC.Peer defPeer2 = full != null ? full.default_send_as : null;
            if (defPeer2 != null || this.delegate.getSendAsPeers() == null || this.delegate.getSendAsPeers().peers.isEmpty()) {
                defPeer = defPeer2;
            } else {
                defPeer = this.delegate.getSendAsPeers().peers.get(0);
            }
            if (defPeer != null) {
                if (defPeer.channel_id != 0) {
                    TLRPC.Chat ch = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(defPeer.channel_id));
                    if (ch != null) {
                        this.senderSelectView.setAvatar(ch);
                    }
                } else {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(defPeer.user_id));
                    if (user != null) {
                        this.senderSelectView.setAvatar(user);
                    }
                }
            }
            boolean z = true;
            boolean wasVisible = this.senderSelectView.getVisibility() == 0;
            if (defPeer == null || ((this.delegate.getSendAsPeers() != null && this.delegate.getSendAsPeers().peers.size() <= 1) || isEditingMessage() || isRecordingAudioVideo() || this.recordedAudioPanel.getVisibility() == 0)) {
                z = false;
            }
            boolean isVisible = z;
            int pad = AndroidUtilities.dp(2.0f);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.senderSelectView.getLayoutParams();
            float f = 1.0f;
            float translationX = 0.0f;
            float startAlpha = isVisible ? 0.0f : 1.0f;
            float startX = isVisible ? (float) (((-this.senderSelectView.getLayoutParams().width) - params.leftMargin) - pad) : 0.0f;
            if (!isVisible) {
                f = 0.0f;
            }
            float endAlpha = f;
            float endX = isVisible ? 0.0f : (float) (((-this.senderSelectView.getLayoutParams().width) - params.leftMargin) - pad);
            if (wasVisible != isVisible) {
                ValueAnimator animator = (ValueAnimator) this.senderSelectView.getTag();
                if (animator != null) {
                    animator.cancel();
                    this.senderSelectView.setTag((Object) null);
                }
                if (this.parentFragment.getOtherSameChatsDiff() != 0 || !this.parentFragment.fragmentOpened || !animated) {
                    float endX2 = endX;
                    float endAlpha2 = endAlpha;
                    float f2 = startX;
                    ViewGroup.MarginLayoutParams marginLayoutParams = params;
                    TLRPC.ChatFull chatFull = full;
                    this.senderSelectView.setVisibility(isVisible ? 0 : 8);
                    this.senderSelectView.setTranslationX(endX2);
                    if (isVisible) {
                        translationX = endX2;
                    }
                    for (ImageView emoji : this.emojiButton) {
                        emoji.setTranslationX(translationX);
                    }
                    this.messageEditText.setTranslationX(translationX);
                    this.senderSelectView.setAlpha(endAlpha2);
                    this.senderSelectView.setTag((Object) null);
                    return;
                }
                ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
                this.senderSelectView.setTranslationX(startX);
                this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
                float f3 = endAlpha;
                float endAlpha3 = endAlpha;
                ChatActivityEnterView$$ExternalSyntheticLambda55 chatActivityEnterView$$ExternalSyntheticLambda55 = r0;
                TLRPC.ChatFull chatFull2 = full;
                ValueAnimator anim2 = anim;
                ChatActivityEnterView$$ExternalSyntheticLambda55 chatActivityEnterView$$ExternalSyntheticLambda552 = new ChatActivityEnterView$$ExternalSyntheticLambda55(this, startAlpha, f3, startX, endX);
                anim2.addUpdateListener(chatActivityEnterView$$ExternalSyntheticLambda55);
                final boolean z2 = isVisible;
                float endX3 = endX;
                final float endX4 = startAlpha;
                float endAlpha4 = endAlpha3;
                final float f4 = startX;
                float f5 = startX;
                final float startX2 = endAlpha4;
                ViewGroup.MarginLayoutParams marginLayoutParams2 = params;
                final float f6 = endX3;
                anim2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        if (z2) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                        }
                        ChatActivityEnterView.this.senderSelectView.setAlpha(endX4);
                        ChatActivityEnterView.this.senderSelectView.setTranslationX(f4);
                        for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                            emoji.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        }
                        ChatActivityEnterView.this.messageEditText.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        if (ChatActivityEnterView.this.botCommandsMenuButton.getTag() == null) {
                            ChatActivityEnterView.this.animationParamsX.clear();
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (!z2) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                            for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                                emoji.setTranslationX(0.0f);
                            }
                            ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (z2) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                        } else {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                        }
                        ChatActivityEnterView.this.senderSelectView.setAlpha(startX2);
                        ChatActivityEnterView.this.senderSelectView.setTranslationX(f6);
                        for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                            emoji.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        }
                        ChatActivityEnterView.this.messageEditText.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        ChatActivityEnterView.this.requestLayout();
                    }
                });
                anim2.start();
                this.senderSelectView.setTag(anim2);
                float f7 = endAlpha4;
                return;
            }
            float f8 = endAlpha;
            float f9 = startX;
            ViewGroup.MarginLayoutParams marginLayoutParams3 = params;
            TLRPC.ChatFull chatFull3 = full;
        }
    }

    /* renamed from: lambda$updateSendAsButton$45$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m698x6afb3626(float startAlpha, float endAlpha, float startX, float endX, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.senderSelectView.setAlpha(((endAlpha - startAlpha) * val) + startAlpha);
        this.senderSelectView.setTranslationX(((endX - startX) * val) + startX);
        for (ImageView emoji : this.emojiButton) {
            emoji.setTranslationX(this.senderSelectView.getTranslationX());
        }
        this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
    }

    public boolean onBotWebViewBackPressed() {
        BotWebViewMenuContainer botWebViewMenuContainer2 = this.botWebViewMenuContainer;
        return botWebViewMenuContainer2 != null && botWebViewMenuContainer2.onBackPressed();
    }

    public boolean hasBotWebView() {
        return this.botMenuButtonType == BotMenuButtonType.WEB_VIEW;
    }

    private void updateBotButton(boolean animated) {
        ImageView imageView;
        if (this.botButton != null) {
            if (!this.parentFragment.openAnimationEnded) {
                animated = false;
            }
            boolean hasBotWebView = hasBotWebView();
            boolean canShowBotsMenu = this.botMenuButtonType != BotMenuButtonType.NO_BUTTON && this.dialog_id > 0;
            boolean wasVisible = this.botButton.getVisibility() == 0;
            if (!hasBotWebView && !this.hasBotCommands && this.botReplyMarkup == null) {
                this.botButton.setVisibility(8);
            } else if (this.botReplyMarkup != null) {
                if (!isPopupShowing() || this.currentPopupContentType != 1) {
                    if (this.botButton.getVisibility() != 0) {
                        this.botButton.setVisibility(0);
                    }
                    this.botButtonDrawable.setIcon(NUM, true);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrBotKeyboard", NUM));
                } else if (this.botButton.getVisibility() != 8) {
                    this.botButton.setVisibility(8);
                }
            } else if (!canShowBotsMenu) {
                this.botButtonDrawable.setIcon(NUM, true);
                this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", NUM));
                this.botButton.setVisibility(0);
            } else {
                this.botButton.setVisibility(8);
            }
            boolean wasWebView = this.botCommandsMenuButton.isWebView;
            this.botCommandsMenuButton.setWebView(this.botMenuButtonType == BotMenuButtonType.WEB_VIEW);
            boolean textChanged = this.botCommandsMenuButton.setMenuText(this.botMenuButtonType == BotMenuButtonType.COMMANDS ? LocaleController.getString(NUM) : this.botMenuWebViewTitle);
            AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, canShowBotsMenu, 0.5f, animated);
            if ((((this.botButton.getVisibility() == 0) == wasVisible && !textChanged && wasWebView == this.botCommandsMenuButton.isWebView) ? false : true) && animated) {
                beginDelayedTransition();
                boolean show = this.botButton.getVisibility() == 0;
                if (show != wasVisible) {
                    this.botButton.setVisibility(0);
                    if (show) {
                        this.botButton.setAlpha(0.0f);
                        this.botButton.setScaleX(0.1f);
                        this.botButton.setScaleY(0.1f);
                    } else if (!show) {
                        this.botButton.setAlpha(1.0f);
                        this.botButton.setScaleX(1.0f);
                        this.botButton.setScaleY(1.0f);
                    }
                    AndroidUtilities.updateViewVisibilityAnimated(this.botButton, show, 0.1f, true);
                }
            }
            updateFieldRight(2);
            LinearLayout linearLayout = this.attachLayout;
            ImageView imageView2 = this.botButton;
            linearLayout.setPivotX((float) AndroidUtilities.dp(((imageView2 == null || imageView2.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) ? 48.0f : 96.0f));
        }
    }

    public boolean isRtlText() {
        try {
            return this.messageEditText.getLayout().getParagraphDirection(0) == -1;
        } catch (Throwable th) {
            return false;
        }
    }

    public void updateBotWebView(boolean animated) {
        this.botCommandsMenuButton.setWebView(hasBotWebView());
        updateBotButton(animated);
    }

    public void setBotsCount(int count, boolean hasCommands, boolean animated) {
        this.botCount = count;
        if (this.hasBotCommands != hasCommands) {
            this.hasBotCommands = hasCommands;
            updateBotButton(animated);
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean openKeyboard) {
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && messageObject2 == this.botButtonsMessageObject && messageObject2 != messageObject) {
            this.botMessageObject = messageObject;
        } else if (this.botButton != null) {
            MessageObject messageObject3 = this.botButtonsMessageObject;
            if (messageObject3 != null && messageObject3 == messageObject) {
                return;
            }
            if (messageObject3 != null || messageObject != null) {
                if (this.botKeyboardView == null) {
                    AnonymousClass55 r0 = new BotKeyboardView(this.parentActivity, this.resourcesProvider) {
                        public void setTranslationY(float translationY) {
                            super.setTranslationY(translationY);
                            if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 1) {
                                ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(translationY);
                            }
                        }
                    };
                    this.botKeyboardView = r0;
                    r0.setVisibility(8);
                    this.botKeyboardViewVisible = false;
                    this.botKeyboardView.setDelegate(new ChatActivityEnterView$$ExternalSyntheticLambda52(this));
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    sizeNotifierFrameLayout.addView(this.botKeyboardView, sizeNotifierFrameLayout.getChildCount() - 1);
                }
                this.botButtonsMessageObject = messageObject;
                this.botReplyMarkup = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardMarkup)) ? null : (TLRPC.TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
                this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
                if (this.botReplyMarkup != null) {
                    SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
                    boolean showPopup = true;
                    if (this.botButtonsMessageObject != this.replyingMessageObject && this.botReplyMarkup.single_use) {
                        if (preferences.getInt("answered_" + this.dialog_id, 0) == messageObject.getId()) {
                            showPopup = false;
                        }
                    }
                    if (showPopup && this.messageEditText.length() == 0 && !isPopupShowing()) {
                        showPopup(1, 1);
                    }
                    this.botKeyboardView.setButtons(this.botReplyMarkup);
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    if (openKeyboard) {
                        this.clearBotButtonsOnKeyboardOpen = true;
                        openKeyboardInternal();
                    } else {
                        showPopup(0, 1);
                    }
                }
                updateBotButton(true);
            }
        }
    }

    /* renamed from: lambda$setButtons$46$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m690x30a8cbef(TLRPC.KeyboardButton button) {
        MessageObject object = this.replyingMessageObject;
        if (object == null) {
            object = DialogObject.isChatDialog(this.dialog_id) ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = this.botButtonsMessageObject;
        }
        boolean open = didPressedBotButton(button, object, messageObject);
        if (this.replyingMessageObject != null) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else {
            MessageObject messageObject2 = this.botButtonsMessageObject;
            if (messageObject2 != null && messageObject2.messageOwner.reply_markup.single_use) {
                if (open) {
                    openKeyboardInternal();
                } else {
                    showPopup(0, 0);
                }
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.putInt("answered_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, 0);
        }
    }

    public boolean didPressedBotButton(TLRPC.KeyboardButton button, MessageObject replyMessageObject, MessageObject messageObject) {
        TLRPC.KeyboardButton keyboardButton = button;
        MessageObject messageObject2 = messageObject;
        if (keyboardButton == null || messageObject2 == null) {
            return false;
        }
        if (keyboardButton instanceof TLRPC.TL_keyboardButton) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton.text, this.dialog_id, replyMessageObject, getThreadMessage(), (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
        } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrl) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, keyboardButton.url, false, true, this.resourcesProvider);
        } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonRequestPhone) {
            this.parentFragment.shareMyContact(2, messageObject2);
        } else {
            Boolean bool = null;
            if (keyboardButton instanceof TLRPC.TL_keyboardButtonRequestPoll) {
                ChatActivity chatActivity = this.parentFragment;
                if ((keyboardButton.flags & 1) != 0) {
                    bool = Boolean.valueOf(keyboardButton.quiz);
                }
                chatActivity.openPollCreate(bool);
                return false;
            } else if ((keyboardButton instanceof TLRPC.TL_keyboardButtonWebView) || (keyboardButton instanceof TLRPC.TL_keyboardButtonSimpleWebView)) {
                long botId = messageObject2.messageOwner.via_bot_id != 0 ? messageObject2.messageOwner.via_bot_id : messageObject2.messageOwner.from_id.user_id;
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(botId));
                final MessageObject messageObject3 = messageObject;
                final long j = botId;
                long botId2 = botId;
                final TLRPC.KeyboardButton keyboardButton2 = button;
                final MessageObject messageObject4 = replyMessageObject;
                Runnable onRequestWebView = new Runnable() {
                    public void run() {
                        if (ChatActivityEnterView.this.sizeNotifierLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this);
                            AndroidUtilities.runOnUIThread(this, 150);
                            return;
                        }
                        BotWebViewSheet webViewSheet = new BotWebViewSheet(ChatActivityEnterView.this.getContext(), ChatActivityEnterView.this.resourcesProvider);
                        webViewSheet.setParentActivity(ChatActivityEnterView.this.parentActivity);
                        int access$2800 = ChatActivityEnterView.this.currentAccount;
                        long j = messageObject3.messageOwner.dialog_id;
                        long j2 = j;
                        String str = keyboardButton2.text;
                        String str2 = keyboardButton2.url;
                        boolean z = keyboardButton2 instanceof TLRPC.TL_keyboardButtonSimpleWebView;
                        MessageObject messageObject = messageObject4;
                        webViewSheet.requestWebView(access$2800, j, j2, str, str2, z ? 1 : 0, messageObject != null ? messageObject.messageOwner.id : 0, false);
                        webViewSheet.show();
                    }
                };
                if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, botId2)) {
                    onRequestWebView.run();
                } else {
                    new AlertDialog.Builder((Context) this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotOpenPageMessage", NUM, UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString(NUM), new ChatActivityEnterView$$ExternalSyntheticLambda60(this, onRequestWebView, botId2)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
                }
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new ChatActivityEnterView$$ExternalSyntheticLambda1(this, messageObject2, keyboardButton));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.parentFragment.showDialog(builder.create());
            } else if ((keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject2, keyboardButton, this.parentFragment);
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline) {
                if (this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline) keyboardButton)) {
                    return true;
                }
                if (keyboardButton.same_peer) {
                    long uid = messageObject2.messageOwner.from_id.user_id;
                    if (messageObject2.messageOwner.via_bot_id != 0) {
                        uid = messageObject2.messageOwner.via_bot_id;
                    }
                    TLRPC.User user2 = this.accountInstance.getMessagesController().getUser(Long.valueOf(uid));
                    if (user2 == null) {
                        return true;
                    }
                    setFieldText("@" + user2.username + " " + keyboardButton.query);
                } else {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 1);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new ChatActivityEnterView$$ExternalSyntheticLambda56(this, messageObject2, keyboardButton));
                    this.parentFragment.presentFragment(fragment);
                }
            } else if ((keyboardButton instanceof TLRPC.TL_keyboardButtonUserProfile) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(keyboardButton.user_id)) != null) {
                Bundle args2 = new Bundle();
                args2.putLong("user_id", keyboardButton.user_id);
                this.parentFragment.presentFragment(new ProfileActivity(args2));
            }
        }
        return true;
    }

    /* renamed from: lambda$didPressedBotButton$47$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m650x2184bbeb(Runnable onRequestWebView, long botId, DialogInterface dialog, int which) {
        onRequestWebView.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, botId, true);
    }

    /* renamed from: lambda$didPressedBotButton$48$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m651x132e620a(MessageObject messageObject, TLRPC.KeyboardButton button, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, button);
            return;
        }
        this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        this.pendingMessageObject = messageObject;
        this.pendingLocationButton = button;
    }

    /* renamed from: lambda$didPressedBotButton$49$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m652x4d80829(MessageObject messageObject, TLRPC.KeyboardButton button, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        MessageObject messageObject2 = messageObject;
        long uid = messageObject2.messageOwner.from_id.user_id;
        if (messageObject2.messageOwner.via_bot_id != 0) {
            uid = messageObject2.messageOwner.via_bot_id;
        }
        TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(uid));
        if (user == null) {
            fragment1.finishFragment();
            return;
        }
        long did = ((Long) dids.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        long j = uid;
        long did2 = did;
        instance.saveDraft(did, 0, "@" + user.username + " " + button.query, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, true);
        if (did2 == this.dialog_id) {
            DialogsActivity dialogsActivity = fragment1;
            fragment1.finishFragment();
        } else if (!DialogObject.isEncryptedDialog(did2)) {
            Bundle args1 = new Bundle();
            if (DialogObject.isUserDialog(did2)) {
                args1.putLong("user_id", did2);
            } else {
                args1.putLong("chat_id", -did2);
            }
            if (this.accountInstance.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                if (!this.parentFragment.presentFragment(new ChatActivity(args1), true)) {
                    fragment1.finishFragment();
                } else if (!AndroidUtilities.isTablet()) {
                    this.parentFragment.removeSelfFromStack();
                }
            }
        } else {
            DialogsActivity dialogsActivity2 = fragment1;
            fragment1.finishFragment();
        }
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    public SizeNotifierFrameLayout getSizeNotifierLayout() {
        return this.sizeNotifierLayout;
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            AnonymousClass57 r1 = new EmojiView(this.allowStickers, this.allowGifs, getContext(), true, this.info, this.sizeNotifierLayout, this.resourcesProvider) {
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 0) {
                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(translationY);
                    }
                }
            };
            this.emojiView = r1;
            r1.setVisibility(8);
            this.emojiView.setShowing(false);
            this.emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
                public boolean onBackspace() {
                    if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        int unused = ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        ChatActivityEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = ChatActivityEnterView.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(View view, TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean notify, int scheduleDate) {
                    if (ChatActivityEnterView.this.trendingStickersAlert != null) {
                        ChatActivityEnterView.this.trendingStickersAlert.dismiss();
                        TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = null;
                    }
                    if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(sticker));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.m685xe7085eb6(sticker, query, parent, sendAnimationData, false, notify, scheduleDate);
                        if (!DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id) || !MessageObject.isGifDocument(sticker)) {
                            TLRPC.Document document = sticker;
                            Object obj = parent;
                            return;
                        }
                        TLRPC.Document document2 = sticker;
                        ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(parent, sticker);
                    } else if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    }
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                /* renamed from: onGifSelected */
                public void m708x1774b15d(View view, Object gif, String query, Object parent, boolean notify, int scheduleDate) {
                    int i;
                    Object obj = gif;
                    Object obj2 = parent;
                    int i2 = scheduleDate;
                    if (isInScheduleMode() && i2 == 0) {
                        AlertsCreator.createScheduleDatePickerDialog((Context) ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$58$$ExternalSyntheticLambda1(this, view, gif, query, parent), ChatActivityEnterView.this.resourcesProvider);
                        boolean z = notify;
                        int i3 = i2;
                        Object obj3 = obj2;
                    } else if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        if (obj instanceof TLRPC.Document) {
                            TLRPC.Document document = (TLRPC.Document) obj;
                            i = i2;
                            Object obj4 = obj2;
                            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, query, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), parent, (MessageObject.SendAnimationData) null, notify, scheduleDate);
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000), true);
                            if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, document);
                            }
                            Object obj5 = obj4;
                        } else {
                            i = i2;
                            Object obj6 = obj2;
                            if (obj instanceof TLRPC.BotInlineResult) {
                                TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) obj;
                                if (result.document != null) {
                                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(result.document, (int) (System.currentTimeMillis() / 1000), false);
                                    if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                                        ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj6, result.document);
                                    }
                                }
                                TLRPC.User user = (TLRPC.User) obj6;
                                HashMap<String, String> params = new HashMap<>();
                                params.put("id", result.id);
                                params.put("query_id", "" + result.query_id);
                                params.put("force_gif", "1");
                                TLRPC.BotInlineResult botInlineResult = result;
                                SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, result, params, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), notify, scheduleDate);
                                if (ChatActivityEnterView.this.searchingType != 0) {
                                    ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                    ChatActivityEnterView.this.emojiView.closeSearch(true);
                                    ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                                }
                            }
                        }
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, notify, i);
                        } else {
                            boolean z2 = notify;
                        }
                    } else if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    }
                }

                public void onTabOpened(int type) {
                    ChatActivityEnterView.this.delegate.onStickersTab(type == 3);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.resourcesProvider);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", NUM));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new ChatActivityEnterView$58$$ExternalSyntheticLambda0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                /* renamed from: lambda$onClearEmojiRecent$1$org-telegram-ui-Components-ChatActivityEnterView$58  reason: not valid java name */
                public /* synthetic */ void m707x718147cb(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                    if (ChatActivityEnterView.this.trendingStickersAlert != null && !ChatActivityEnterView.this.trendingStickersAlert.isDismissed()) {
                        ChatActivityEnterView.this.trendingStickersAlert.getLayout().showStickerSet(stickerSet, inputStickerSet);
                    } else if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        if (stickerSet != null) {
                            inputStickerSet = new TLRPC.TL_inputStickerSetID();
                            inputStickerSet.access_hash = stickerSet.access_hash;
                            inputStickerSet.id = stickerSet.id;
                        }
                        ChatActivity access$2600 = ChatActivityEnterView.this.parentFragment;
                        Activity access$1600 = ChatActivityEnterView.this.parentActivity;
                        ChatActivity access$26002 = ChatActivityEnterView.this.parentFragment;
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        access$2600.showDialog(new StickersAlert(access$1600, access$26002, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, chatActivityEnterView, chatActivityEnterView.resourcesProvider));
                    }
                }

                public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, stickerSet, 2, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, stickerSet, 0, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickersGroupClick(long chatId) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.hidePopup(false);
                        }
                        GroupStickersActivity fragment = new GroupStickersActivity(chatId);
                        fragment.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(fragment);
                    }
                }

                public void onSearchOpenClose(int type) {
                    ChatActivityEnterView.this.setSearchingTypeInternal(type, true);
                    if (type != 0) {
                        ChatActivityEnterView.this.setStickersExpanded(true, true, false);
                    }
                    if (ChatActivityEnterView.this.emojiTabOpen && ChatActivityEnterView.this.searchingType == 2) {
                        ChatActivityEnterView.this.checkStickresExpandHeight();
                    }
                }

                public boolean isSearchOpened() {
                    return ChatActivityEnterView.this.searchingType != 0;
                }

                public boolean isExpanded() {
                    return ChatActivityEnterView.this.stickersExpanded;
                }

                public boolean canSchedule() {
                    return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.canScheduleMessage();
                }

                public boolean isInScheduleMode() {
                    return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.isInScheduleMode();
                }

                public long getDialogId() {
                    return ChatActivityEnterView.this.dialog_id;
                }

                public int getThreadId() {
                    return ChatActivityEnterView.this.getThreadMessageId();
                }

                public void showTrendingStickersAlert(TrendingStickersLayout layout) {
                    if (ChatActivityEnterView.this.parentActivity != null && ChatActivityEnterView.this.parentFragment != null) {
                        TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = new TrendingStickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, layout, ChatActivityEnterView.this.resourcesProvider) {
                            public void dismiss() {
                                super.dismiss();
                                if (ChatActivityEnterView.this.trendingStickersAlert == this) {
                                    TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = null;
                                }
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.onTrendingStickersShowed(false);
                                }
                            }
                        };
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onTrendingStickersShowed(true);
                        }
                        ChatActivityEnterView.this.trendingStickersAlert.show();
                    }
                }

                public void invalidateEnterView() {
                    ChatActivityEnterView.this.invalidate();
                }

                public float getProgressToSearchOpened() {
                    return ChatActivityEnterView.this.searchToOpenProgress;
                }
            });
            this.emojiView.setDragListener(new EmojiView.DragListener() {
                int initialOffset;
                boolean wasExpanded;

                public void onDragStart() {
                    if (allowDragging()) {
                        if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                            ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                        }
                        boolean unused = ChatActivityEnterView.this.stickersDragging = true;
                        this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                        boolean unused2 = ChatActivityEnterView.this.stickersExpanded = true;
                        int i = 0;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        int height = chatActivityEnterView.sizeNotifierLayout.getHeight();
                        if (Build.VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        int unused3 = chatActivityEnterView.stickersExpandedHeight = (((height - i) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        if (ChatActivityEnterView.this.searchingType == 2) {
                            ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                            int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(chatActivityEnterView2.stickersExpandedHeight, AndroidUtilities.dp(120.0f) + (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
                        }
                        ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                        ChatActivityEnterView.this.emojiView.setLayerType(2, (Paint) null);
                        ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                        ChatActivityEnterView.this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                        this.initialOffset = (int) ChatActivityEnterView.this.getTranslationY();
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onStickersExpandedChange();
                        }
                    }
                }

                public void onDragEnd(float velocity) {
                    if (allowDragging()) {
                        boolean unused = ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || velocity < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || velocity > ((float) AndroidUtilities.dp(-200.0f))) && ((!this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                        } else {
                            ChatActivityEnterView.this.setStickersExpanded(!this.wasExpanded, true, true);
                        }
                    }
                }

                public void onDragCancel() {
                    if (ChatActivityEnterView.this.stickersTabOpen) {
                        boolean unused = ChatActivityEnterView.this.stickersDragging = false;
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
                    }
                }

                public void onDrag(int offset) {
                    if (allowDragging()) {
                        int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        int offset2 = Math.max(Math.min(offset + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - origHeight));
                        ChatActivityEnterView.this.emojiView.setTranslationY((float) offset2);
                        ChatActivityEnterView.this.setTranslationY((float) offset2);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        float unused = chatActivityEnterView.stickersExpansionProgress = ((float) offset2) / ((float) (-(chatActivityEnterView.stickersExpandedHeight - origHeight)));
                        ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                    }
                }

                private boolean allowDragging() {
                    return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers() && !ChatActivityEnterView.this.waitingForKeyboardOpen;
                }
            });
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
            checkChannelRights();
        }
    }

    /* renamed from: onStickerSelected */
    public void m685xe7085eb6(TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean clearsInputField, boolean notify, int scheduleDate) {
        int i = scheduleDate;
        if (isInScheduleMode() && i == 0) {
            AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda49(this, sticker, query, parent, sendAnimationData, clearsInputField), this.resourcesProvider);
            int i2 = i;
        } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                this.emojiView.closeSearch(true);
                this.emojiView.hideSearchKeyboard();
            }
            setStickersExpanded(false, true, false);
            int i3 = i;
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(sticker, query, this.dialog_id, this.replyingMessageObject, getThreadMessage(), parent, sendAnimationData, notify, scheduleDate);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, i3);
            }
            if (clearsInputField) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, parent, sticker, (int) (System.currentTimeMillis() / 1000), false);
        } else {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
            }
        }
    }

    public boolean canSchedule() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.canScheduleMessage();
    }

    public boolean isInScheduleMode() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.isInScheduleMode();
    }

    public void addStickerToRecent(TLRPC.Document sticker) {
        createEmojiView();
        this.emojiView.addRecentSticker(sticker);
    }

    public void hideEmojiView() {
        EmojiView emojiView2;
        if (!this.emojiViewVisible && (emojiView2 = this.emojiView) != null && emojiView2.getVisibility() != 8) {
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView.setVisibility(8);
            this.emojiView.setShowing(false);
        }
    }

    public void showEmojiView() {
        showPopup(1, 0);
    }

    /* access modifiers changed from: private */
    public void showPopup(int show, int contentType) {
        showPopup(show, contentType, true);
    }

    private void showPopup(int show, int contentType, boolean allowAnimation) {
        int previousHeight;
        final int i = show;
        int i2 = contentType;
        if (i != 2) {
            if (i == 1) {
                if (i2 == 0 && this.emojiView == null) {
                    if (this.parentActivity != null) {
                        createEmojiView();
                    } else {
                        return;
                    }
                }
                View currentView = null;
                int previousHeight2 = 0;
                if (i2 == 0) {
                    if (this.emojiView.getParent() == null) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
                    }
                    boolean samePannelWasVisible = this.emojiViewVisible && this.emojiView.getVisibility() == 0;
                    this.emojiView.setVisibility(0);
                    this.emojiViewVisible = true;
                    BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                    if (!(botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8)) {
                        this.botKeyboardView.setVisibility(8);
                        this.botKeyboardViewVisible = false;
                        previousHeight2 = this.botKeyboardView.getMeasuredHeight();
                    }
                    this.emojiView.setShowing(true);
                    currentView = this.emojiView;
                    this.animatingContentType = 0;
                    previousHeight = previousHeight2;
                } else if (i2 == 1) {
                    boolean samePannelWasVisible2 = this.botKeyboardViewVisible && this.botKeyboardView.getVisibility() == 0;
                    this.botKeyboardViewVisible = true;
                    EmojiView emojiView2 = this.emojiView;
                    if (!(emojiView2 == null || emojiView2.getVisibility() == 8)) {
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiView.setShowing(false);
                        this.emojiViewVisible = false;
                        previousHeight2 = this.emojiView.getMeasuredHeight();
                    }
                    this.botKeyboardView.setVisibility(0);
                    currentView = this.botKeyboardView;
                    this.animatingContentType = 1;
                    previousHeight = previousHeight2;
                } else {
                    previousHeight = 0;
                }
                this.currentPopupContentType = i2;
                if (this.keyboardHeight <= 0) {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
                }
                if (this.keyboardHeightLand <= 0) {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
                }
                int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
                if (i2 == 1) {
                    currentHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight);
                }
                BotKeyboardView botKeyboardView3 = this.botKeyboardView;
                if (botKeyboardView3 != null) {
                    botKeyboardView3.setPanelHeight(currentHeight);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
                layoutParams.height = currentHeight;
                currentView.setLayoutParams(layoutParams);
                if (!AndroidUtilities.isInMultiwindow) {
                    AndroidUtilities.hideKeyboard(this.messageEditText);
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout2 != null) {
                    this.emojiPadding = currentHeight;
                    sizeNotifierFrameLayout2.requestLayout();
                    setEmojiButtonImage(true, true);
                    updateBotButton(true);
                    onWindowSizeChanged();
                    if (this.smoothKeyboard && !this.keyboardVisible && currentHeight != previousHeight && allowAnimation) {
                        this.panelAnimation = new AnimatorSet();
                        currentView.setTranslationY((float) (currentHeight - previousHeight));
                        this.panelAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(currentView, View.TRANSLATION_Y, new float[]{(float) (currentHeight - previousHeight), 0.0f})});
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                AnimatorSet unused = ChatActivityEnterView.this.panelAnimation = null;
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50);
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                        requestLayout();
                    }
                }
            } else {
                if (this.emojiButton != null) {
                    setEmojiButtonImage(false, true);
                }
                this.currentPopupContentType = -1;
                if (this.emojiView != null) {
                    if (i == 2 && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                        this.removeEmojiViewAfterAnimation = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null) {
                            chatActivityEnterViewDelegate.bottomPanelTranslationYChanged(0.0f);
                        }
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView = null;
                    } else if (!this.smoothKeyboard || this.keyboardVisible || this.stickersExpanded) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                        if (chatActivityEnterViewDelegate2 != null) {
                            chatActivityEnterViewDelegate2.bottomPanelTranslationYChanged(0.0f);
                        }
                        this.emojiPadding = 0;
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiView.setShowing(false);
                    } else {
                        this.emojiViewVisible = true;
                        this.animatingContentType = 0;
                        this.emojiView.setShowing(false);
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiView, View.TRANSLATION_Y, new float[]{(float) this.emojiView.getMeasuredHeight()})});
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (i == 0) {
                                    int unused = ChatActivityEnterView.this.emojiPadding = 0;
                                }
                                AnimatorSet unused2 = ChatActivityEnterView.this.panelAnimation = null;
                                if (ChatActivityEnterView.this.emojiView != null) {
                                    ChatActivityEnterView.this.emojiView.setTranslationY(0.0f);
                                    ChatActivityEnterView.this.emojiView.setVisibility(8);
                                    ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.emojiView);
                                    if (ChatActivityEnterView.this.removeEmojiViewAfterAnimation) {
                                        boolean unused3 = ChatActivityEnterView.this.removeEmojiViewAfterAnimation = false;
                                        EmojiView unused4 = ChatActivityEnterView.this.emojiView = null;
                                    }
                                }
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50);
                        requestLayout();
                    }
                    this.emojiViewVisible = false;
                }
                BotKeyboardView botKeyboardView4 = this.botKeyboardView;
                if (botKeyboardView4 != null && botKeyboardView4.getVisibility() == 0) {
                    if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                        if (this.smoothKeyboard && !this.keyboardVisible) {
                            if (this.botKeyboardViewVisible) {
                                this.animatingContentType = 1;
                            }
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.panelAnimation = animatorSet2;
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botKeyboardView, View.TRANSLATION_Y, new float[]{(float) this.botKeyboardView.getMeasuredHeight()})});
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (i == 0) {
                                        int unused = ChatActivityEnterView.this.emojiPadding = 0;
                                    }
                                    AnimatorSet unused2 = ChatActivityEnterView.this.panelAnimation = null;
                                    ChatActivityEnterView.this.botKeyboardView.setTranslationY(0.0f);
                                    ChatActivityEnterView.this.botKeyboardView.setVisibility(8);
                                    NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                    if (ChatActivityEnterView.this.delegate != null) {
                                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                    }
                                    ChatActivityEnterView.this.requestLayout();
                                }
                            });
                            this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                            AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50);
                            requestLayout();
                        } else if (!this.waitingForKeyboardOpen) {
                            this.botKeyboardView.setVisibility(8);
                        }
                    }
                    this.botKeyboardViewVisible = false;
                }
                if (this.sizeNotifierLayout != null && !SharedConfig.smoothKeyboard && i == 0) {
                    this.emojiPadding = 0;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
                updateBotButton(true);
            }
            if (this.stickersTabOpen || this.emojiTabOpen) {
                checkSendButton(true);
            }
            if (this.stickersExpanded && i != 1) {
                setStickersExpanded(false, false, false);
            }
            updateFieldHint(false);
            checkBotMenu();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r13.recordedAudioPanel;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setEmojiButtonImage(boolean r14, boolean r15) {
        /*
            r13 = this;
            int r0 = r13.recordInterfaceState
            r1 = 0
            r2 = 1
            if (r0 == r2) goto L_0x0013
            android.widget.FrameLayout r0 = r13.recordedAudioPanel
            if (r0 == 0) goto L_0x0011
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0011
            goto L_0x0013
        L_0x0011:
            r0 = 0
            goto L_0x0014
        L_0x0013:
            r0 = 1
        L_0x0014:
            r3 = 0
            if (r0 == 0) goto L_0x0042
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r1]
            r4.setScaleX(r3)
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r1]
            r4.setScaleY(r3)
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r1]
            r4.setAlpha(r3)
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r2]
            r4.setScaleX(r3)
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r2]
            r4.setScaleY(r3)
            android.widget.ImageView[] r4 = r13.emojiButton
            r4 = r4[r2]
            r4.setAlpha(r3)
            r15 = 0
        L_0x0042:
            if (r15 == 0) goto L_0x004a
            int r4 = r13.currentEmojiIcon
            r5 = -1
            if (r4 != r5) goto L_0x004a
            r15 = 0
        L_0x004a:
            if (r14 == 0) goto L_0x0052
            int r4 = r13.currentPopupContentType
            if (r4 != 0) goto L_0x0052
            r4 = 0
            goto L_0x007a
        L_0x0052:
            org.telegram.ui.Components.EmojiView r4 = r13.emojiView
            if (r4 != 0) goto L_0x0061
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r5 = "selected_page"
            int r4 = r4.getInt(r5, r1)
            goto L_0x0065
        L_0x0061:
            int r4 = r4.getCurrentPage()
        L_0x0065:
            if (r4 == 0) goto L_0x0078
            boolean r5 = r13.allowStickers
            if (r5 != 0) goto L_0x0070
            boolean r5 = r13.allowGifs
            if (r5 != 0) goto L_0x0070
            goto L_0x0078
        L_0x0070:
            if (r4 != r2) goto L_0x0075
            r5 = 2
            r4 = r5
            goto L_0x007a
        L_0x0075:
            r5 = 3
            r4 = r5
            goto L_0x007a
        L_0x0078:
            r5 = 1
            r4 = r5
        L_0x007a:
            int r5 = r13.currentEmojiIcon
            if (r5 != r4) goto L_0x007f
            return
        L_0x007f:
            android.animation.AnimatorSet r5 = r13.emojiButtonAnimation
            r6 = 0
            if (r5 == 0) goto L_0x0089
            r5.cancel()
            r13.emojiButtonAnimation = r6
        L_0x0089:
            r5 = 3
            r7 = 2
            if (r4 != 0) goto L_0x0098
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r15]
            r9 = 2131165536(0x7var_, float:1.7945292E38)
            r8.setImageResource(r9)
            goto L_0x00be
        L_0x0098:
            if (r4 != r2) goto L_0x00a5
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r15]
            r9 = 2131165543(0x7var_, float:1.7945306E38)
            r8.setImageResource(r9)
            goto L_0x00be
        L_0x00a5:
            if (r4 != r7) goto L_0x00b2
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r15]
            r9 = 2131165544(0x7var_, float:1.7945308E38)
            r8.setImageResource(r9)
            goto L_0x00be
        L_0x00b2:
            if (r4 != r5) goto L_0x00be
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r15]
            r9 = 2131165535(0x7var_f, float:1.794529E38)
            r8.setImageResource(r9)
        L_0x00be:
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r15]
            if (r4 != r7) goto L_0x00c8
            java.lang.Integer r6 = java.lang.Integer.valueOf(r2)
        L_0x00c8:
            r8.setTag(r6)
            r13.currentEmojiIcon = r4
            if (r15 == 0) goto L_0x0175
            android.widget.ImageView[] r6 = r13.emojiButton
            r6 = r6[r2]
            r6.setVisibility(r1)
            android.widget.ImageView[] r6 = r13.emojiButton
            r6 = r6[r2]
            r6.setAlpha(r3)
            android.widget.ImageView[] r6 = r13.emojiButton
            r6 = r6[r2]
            r8 = 1036831949(0x3dcccccd, float:0.1)
            r6.setScaleX(r8)
            android.widget.ImageView[] r6 = r13.emojiButton
            r6 = r6[r2]
            r6.setScaleY(r8)
            android.animation.AnimatorSet r6 = new android.animation.AnimatorSet
            r6.<init>()
            r13.emojiButtonAnimation = r6
            r9 = 6
            android.animation.Animator[] r9 = new android.animation.Animator[r9]
            android.widget.ImageView[] r10 = r13.emojiButton
            r10 = r10[r1]
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r2]
            r12[r1] = r8
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r1] = r10
            android.widget.ImageView[] r10 = r13.emojiButton
            r10 = r10[r1]
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r2]
            r12[r1] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r2] = r8
            android.widget.ImageView[] r8 = r13.emojiButton
            r8 = r8[r1]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r2]
            r11[r1] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r8, r10, r11)
            r9[r7] = r3
            android.widget.ImageView[] r3 = r13.emojiButton
            r3 = r3[r2]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r2]
            r10 = 1065353216(0x3var_, float:1.0)
            r8[r1] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r9[r5] = r3
            r3 = 4
            android.widget.ImageView[] r5 = r13.emojiButton
            r5 = r5[r2]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r2]
            r8[r1] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r8)
            r9[r3] = r5
            r3 = 5
            android.widget.ImageView[] r5 = r13.emojiButton
            r5 = r5[r2]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            r2[r1] = r10
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r5, r7, r2)
            r9[r3] = r1
            r6.playTogether(r9)
            android.animation.AnimatorSet r1 = r13.emojiButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$63 r2 = new org.telegram.ui.Components.ChatActivityEnterView$63
            r2.<init>()
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r13.emojiButtonAnimation
            r2 = 150(0x96, double:7.4E-322)
            r1.setDuration(r2)
            android.animation.AnimatorSet r1 = r13.emojiButtonAnimation
            r1.start()
        L_0x0175:
            r13.onEmojiIconChanged(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEmojiButtonImage(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onEmojiIconChanged(int currentIcon) {
        if (currentIcon == 3 && this.emojiView == null) {
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            ArrayList<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            int N = Math.min(10, gifSearchEmojies.size());
            for (int i = 0; i < N; i++) {
                Emoji.preloadEmoji(gifSearchEmojies.get(i));
            }
        }
    }

    public boolean hidePopup(boolean byBackButton) {
        return hidePopup(byBackButton, false);
    }

    public boolean hidePopup(boolean byBackButton, boolean forceAnimate) {
        if (!isPopupShowing()) {
            return false;
        }
        if (this.currentPopupContentType == 1 && byBackButton && this.botButtonsMessageObject != null) {
            return false;
        }
        if ((!byBackButton || this.searchingType == 0) && !forceAnimate) {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, false);
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            showPopup(0, 0);
        } else {
            setSearchingTypeInternal(0, true);
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.closeSearch(true);
            }
            this.messageEditText.requestFocus();
            setStickersExpanded(false, true, false);
            if (this.emojiTabOpen) {
                checkSendButton(true);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void setSearchingTypeInternal(int searchingType2, boolean animated) {
        final boolean showSearchingNew = searchingType2 != 0;
        if (showSearchingNew != (this.searchingType != 0)) {
            ValueAnimator valueAnimator = this.searchAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.searchAnimator.cancel();
            }
            float f = 1.0f;
            if (!animated) {
                if (!showSearchingNew) {
                    f = 0.0f;
                }
                this.searchToOpenProgress = f;
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.searchProgressChanged();
                }
            } else {
                float[] fArr = new float[2];
                fArr[0] = this.searchToOpenProgress;
                if (!showSearchingNew) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.searchAnimator = ofFloat;
                ofFloat.addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda33(this));
                this.searchAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        float unused = ChatActivityEnterView.this.searchToOpenProgress = showSearchingNew ? 1.0f : 0.0f;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.searchProgressChanged();
                        }
                    }
                });
                this.searchAnimator.setDuration(220);
                this.searchAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.searchAnimator.start();
            }
        }
        this.searchingType = searchingType2;
    }

    /* renamed from: lambda$setSearchingTypeInternal$51$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m693x20ac0ec5(ValueAnimator valueAnimator) {
        this.searchToOpenProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.searchProgressChanged();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
        r0 = r9.parentFragment;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openKeyboardInternal() {
        /*
            r9 = this;
            boolean r0 = r9.hasBotWebView()
            if (r0 == 0) goto L_0x000d
            boolean r0 = r9.botCommandsMenuIsShowing()
            if (r0 == 0) goto L_0x000d
            return
        L_0x000d:
            boolean r0 = org.telegram.messenger.AndroidUtilities.usingHardwareInput
            r1 = 0
            if (r0 != 0) goto L_0x0027
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x0027
            org.telegram.ui.ChatActivity r0 = r9.parentFragment
            if (r0 == 0) goto L_0x0020
            boolean r0 = r0.isInBubbleMode()
            if (r0 != 0) goto L_0x0027
        L_0x0020:
            boolean r0 = r9.isPaused
            if (r0 == 0) goto L_0x0025
            goto L_0x0027
        L_0x0025:
            r0 = 2
            goto L_0x0028
        L_0x0027:
            r0 = 0
        L_0x0028:
            r9.showPopup(r0, r1)
            org.telegram.ui.Components.EditTextCaption r0 = r9.messageEditText
            r0.requestFocus()
            org.telegram.ui.Components.EditTextCaption r0 = r9.messageEditText
            org.telegram.messenger.AndroidUtilities.showKeyboard(r0)
            boolean r0 = r9.isPaused
            r1 = 1
            if (r0 == 0) goto L_0x003d
            r9.showKeyboardOnResume = r1
            goto L_0x0078
        L_0x003d:
            boolean r0 = org.telegram.messenger.AndroidUtilities.usingHardwareInput
            if (r0 != 0) goto L_0x0078
            boolean r0 = r9.keyboardVisible
            if (r0 != 0) goto L_0x0078
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x0078
            org.telegram.ui.ChatActivity r0 = r9.parentFragment
            if (r0 == 0) goto L_0x0053
            boolean r0 = r0.isInBubbleMode()
            if (r0 != 0) goto L_0x0078
        L_0x0053:
            r9.waitingForKeyboardOpen = r1
            org.telegram.ui.Components.EmojiView r0 = r9.emojiView
            if (r0 == 0) goto L_0x006c
            long r1 = android.os.SystemClock.uptimeMillis()
            long r3 = android.os.SystemClock.uptimeMillis()
            r5 = 3
            r6 = 0
            r7 = 0
            r8 = 0
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r1, r3, r5, r6, r7, r8)
            r0.onTouchEvent(r1)
        L_0x006c:
            java.lang.Runnable r0 = r9.openKeyboardRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r9.openKeyboardRunnable
            r1 = 100
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x0078:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.openKeyboardInternal():void");
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean hasAudioToSend() {
        return (this.audioToSendMessageObject == null && this.videoToSendMessageObject == null) ? false : true;
    }

    public void openKeyboard() {
        if ((!hasBotWebView() || !botCommandsMenuIsShowing()) && !AndroidUtilities.showKeyboard(this.messageEditText)) {
            this.messageEditText.clearFocus();
            this.messageEditText.requestFocus();
        }
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible || this.botKeyboardViewVisible;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(TLRPC.Document searchImage) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(searchImage, (int) (System.currentTimeMillis() / 1000), true);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.addRecentGif(searchImage);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && this.stickersExpanded) {
            setSearchingTypeInternal(0, false);
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        MessageObject messageObject;
        boolean z2 = true;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = height;
            this.lastSizeChangeValue2 = isWidthGreater;
            if (height <= 0) {
                z2 = false;
            }
            this.keyboardVisible = z2;
            checkBotMenu();
            return;
        }
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (this.keyboardVisible && this.emojiViewVisible && this.emojiView == null) {
            this.emojiViewVisible = false;
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                newHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), newHeight);
            }
            View currentView = null;
            int i = this.currentPopupContentType;
            if (i == 0) {
                currentView = this.emojiView;
            } else if (i == 1) {
                currentView = this.botKeyboardView;
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.setPanelHeight(newHeight);
            }
            if (currentView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
                if (!this.closeAnimationInProgress && (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight) && !this.stickersExpanded)) {
                    layoutParams.width = AndroidUtilities.displaySize.x;
                    layoutParams.height = newHeight;
                    currentView.setLayoutParams(layoutParams);
                    if (this.sizeNotifierLayout != null) {
                        int oldHeight = this.emojiPadding;
                        this.emojiPadding = layoutParams.height;
                        this.sizeNotifierLayout.requestLayout();
                        onWindowSizeChanged();
                        if (this.smoothKeyboard && !this.keyboardVisible && oldHeight != this.emojiPadding && pannelAnimationEnabled()) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.panelAnimation = animatorSet;
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(currentView, View.TRANSLATION_Y, new float[]{(float) (this.emojiPadding - oldHeight), 0.0f})});
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    AnimatorSet unused = ChatActivityEnterView.this.panelAnimation = null;
                                    if (ChatActivityEnterView.this.delegate != null) {
                                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                    }
                                    ChatActivityEnterView.this.requestLayout();
                                    NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                }
                            });
                            AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50);
                            this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                            requestLayout();
                        }
                    }
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        this.keyboardVisible = height > 0;
        checkBotMenu();
        if (this.keyboardVisible && isPopupShowing() && this.stickersExpansionAnim == null) {
            showPopup(0, this.currentPopupContentType);
        } else if (!this.keyboardVisible && !isPopupShowing() && (messageObject = this.botButtonsMessageObject) != null && this.replyingMessageObject != messageObject && ((!hasBotWebView() || !botCommandsMenuIsShowing()) && TextUtils.isEmpty(this.messageEditText.getText()))) {
            if (this.sizeNotifierLayout.adjustPanLayoutHelper.animationInProgress()) {
                this.sizeNotifierLayout.adjustPanLayoutHelper.stopTransition();
            } else {
                this.sizeNotifierLayout.adjustPanLayoutHelper.ignoreOnce();
            }
            showPopup(1, 1, false);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            if (this.clearBotButtonsOnKeyboardOpen) {
                this.clearBotButtonsOnKeyboardOpen = false;
                this.botKeyboardView.setButtons(this.botReplyMarkup);
            }
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public int getVisibleEmojiPadding() {
        if (this.emojiViewVisible) {
            return this.emojiPadding;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public MessageObject getThreadMessage() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            return chatActivity.getThreadMessage();
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int getThreadMessageId() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.getThreadMessage() == null) {
            return 0;
        }
        return this.parentFragment.getThreadMessage().getId();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int state;
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
        int i = id;
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.invalidateViews();
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.invalidateViews();
            }
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                editTextCaption.postInvalidate();
                return;
            }
            return;
        }
        int i2 = 0;
        if (i == NotificationCenter.recordProgressChanged) {
            if (args[0].intValue() == this.recordingGuid) {
                if (this.recordInterfaceState != 0 && !this.wasSendTyping && !isInScheduleMode()) {
                    this.wasSendTyping = true;
                    MessagesController messagesController = this.accountInstance.getMessagesController();
                    long j = this.dialog_id;
                    int threadMessageId = getThreadMessageId();
                    ImageView imageView = this.videoSendButton;
                    messagesController.sendTyping(j, threadMessageId, (imageView == null || imageView.getTag() == null) ? 1 : 7, 0);
                }
                RecordCircle recordCircle2 = this.recordCircle;
                if (recordCircle2 != null) {
                    recordCircle2.setAmplitude(args[1].doubleValue());
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 != null && editTextCaption2.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else if (i != NotificationCenter.recordStartError && i != NotificationCenter.recordStopped) {
            int i3 = null;
            if (i == NotificationCenter.recordStarted) {
                if (args[0].intValue() == this.recordingGuid) {
                    boolean audio = args[1].booleanValue();
                    ImageView imageView2 = this.videoSendButton;
                    if (imageView2 != null) {
                        if (!audio) {
                            i3 = 1;
                        }
                        imageView2.setTag(i3);
                        int i4 = 8;
                        this.videoSendButton.setVisibility(audio ? 8 : 0);
                        ImageView imageView3 = this.videoSendButton;
                        if (audio) {
                            i4 = 0;
                        }
                        imageView3.setVisibility(i4);
                    }
                    if (!this.recordingAudioVideo) {
                        this.recordingAudioVideo = true;
                        updateRecordInterface(0);
                    } else {
                        this.recordCircle.showWaves(true, true);
                    }
                    this.recordTimerView.start();
                    boolean unused = this.recordDot.enterAnimation = false;
                }
            } else if (i == NotificationCenter.audioDidSent) {
                if (args[0].intValue() == this.recordingGuid) {
                    VideoEditedInfo videoEditedInfo = args[1];
                    if (videoEditedInfo instanceof VideoEditedInfo) {
                        this.videoToSendMessageObject = videoEditedInfo;
                        String str = args[2];
                        this.audioToSendPath = str;
                        this.videoTimelineView.setVideoPath(str);
                        this.videoTimelineView.setKeyframes(args[3]);
                        this.videoTimelineView.setVisibility(0);
                        this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                        updateRecordInterface(3);
                        checkSendButton(false);
                        return;
                    }
                    TLRPC.TL_document tL_document = args[1];
                    this.audioToSend = tL_document;
                    this.audioToSendPath = args[2];
                    if (tL_document == null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null) {
                            chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, 0);
                        }
                    } else if (this.recordedAudioPanel != null) {
                        TLRPC.TL_message message = new TLRPC.TL_message();
                        message.out = true;
                        message.id = 0;
                        message.peer_id = new TLRPC.TL_peerUser();
                        message.from_id = new TLRPC.TL_peerUser();
                        TLRPC.Peer peer = message.peer_id;
                        TLRPC.Peer peer2 = message.from_id;
                        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        peer2.user_id = clientUserId;
                        peer.user_id = clientUserId;
                        message.date = (int) (System.currentTimeMillis() / 1000);
                        message.message = "";
                        message.attachPath = this.audioToSendPath;
                        message.media = new TLRPC.TL_messageMediaDocument();
                        message.media.flags |= 3;
                        message.media.document = this.audioToSend;
                        message.flags |= 768;
                        this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, message, false, true);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        this.recordDeleteImageView.setVisibility(0);
                        this.recordDeleteImageView.setAlpha(0.0f);
                        this.recordDeleteImageView.setScaleY(0.0f);
                        this.recordDeleteImageView.setScaleX(0.0f);
                        int duration = 0;
                        int a = 0;
                        while (true) {
                            if (a >= this.audioToSend.attributes.size()) {
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = (TLRPC.DocumentAttribute) this.audioToSend.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                                duration = attribute.duration;
                                break;
                            }
                            a++;
                        }
                        int a2 = 0;
                        while (true) {
                            if (a2 >= this.audioToSend.attributes.size()) {
                                break;
                            }
                            TLRPC.DocumentAttribute attribute2 = (TLRPC.DocumentAttribute) this.audioToSend.attributes.get(a2);
                            if (attribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                                if (attribute2.waveform == null || attribute2.waveform.length == 0) {
                                    attribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                                }
                                this.recordedAudioSeekBar.setWaveform(attribute2.waveform);
                            } else {
                                a2++;
                            }
                        }
                        this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(duration));
                        checkSendButton(false);
                        updateRecordInterface(3);
                    }
                }
            } else if (i == NotificationCenter.audioRouteChanged) {
                if (this.parentActivity != null) {
                    boolean frontSpeaker = args[0].booleanValue();
                    Activity activity = this.parentActivity;
                    if (!frontSpeaker) {
                        i2 = Integer.MIN_VALUE;
                    }
                    activity.setVolumeControlStream(i2);
                }
            } else if (i == NotificationCenter.messagePlayingDidReset) {
                if (this.audioToSendMessageObject != null && !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    this.playPauseDrawable.setIcon(0, true);
                    this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                    this.recordedAudioSeekBar.setProgress(0.0f);
                }
            } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                Integer num = args[0];
                if (this.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                    this.audioToSendMessageObject.audioProgress = player.audioProgress;
                    this.audioToSendMessageObject.audioProgressSec = player.audioProgressSec;
                    if (!this.recordedAudioSeekBar.isDragging()) {
                        this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                    }
                }
            } else if (i == NotificationCenter.featuredStickersDidLoad) {
                if (this.emojiButton != null) {
                    int a3 = 0;
                    while (true) {
                        ImageView[] imageViewArr = this.emojiButton;
                        if (a3 < imageViewArr.length) {
                            imageViewArr[a3].invalidate();
                            a3++;
                        } else {
                            return;
                        }
                    }
                }
            } else if (i == NotificationCenter.messageReceivedByServer) {
                if (!args[6].booleanValue() && args[3].longValue() == this.dialog_id && (chatFull = this.info) != null && chatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
                    this.info.slowmode_next_send_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + this.info.slowmode_seconds;
                    this.info.flags |= 262144;
                    setSlowModeTimer(this.info.slowmode_next_send_date);
                }
            } else if (i == NotificationCenter.sendingMessagesChanged) {
                if (this.info != null) {
                    updateSlowModeText();
                }
            } else if (i == NotificationCenter.audioRecordTooShort) {
                updateRecordInterface(4);
            } else if (i == NotificationCenter.updateBotMenuButton) {
                long botId = args[0].longValue();
                TLRPC.BotMenuButton botMenuButton = args[1];
                if (botId == this.dialog_id) {
                    if (botMenuButton instanceof TLRPC.TL_botMenuButton) {
                        TLRPC.TL_botMenuButton webViewButton = (TLRPC.TL_botMenuButton) botMenuButton;
                        this.botMenuWebViewTitle = webViewButton.text;
                        this.botMenuWebViewUrl = webViewButton.url;
                        this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
                    } else if (this.hasBotCommands) {
                        this.botMenuButtonType = BotMenuButtonType.COMMANDS;
                    } else {
                        this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
                    }
                    updateBotButton(false);
                }
            }
        } else if (((Integer) args[0]).intValue() == this.recordingGuid) {
            if (this.recordingAudioVideo) {
                this.recordingAudioVideo = false;
                if (i == NotificationCenter.recordStopped) {
                    Integer reason = args[1];
                    if (reason.intValue() == 4) {
                        state = 4;
                    } else if (isInVideoMode() && reason.intValue() == 5) {
                        state = 1;
                    } else if (reason.intValue() == 0) {
                        state = 5;
                    } else if (reason.intValue() == 6) {
                        state = 2;
                    } else {
                        state = 3;
                    }
                    if (state != 3) {
                        updateRecordInterface(state);
                    }
                } else {
                    updateRecordInterface(2);
                }
            }
            if (i == NotificationCenter.recordStopped) {
                Integer num2 = args[1];
            }
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && this.pendingLocationButton != null) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    /* access modifiers changed from: private */
    public void checkStickresExpandHeight() {
        if (this.emojiView != null) {
            int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            int newHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
            if (this.searchingType == 2) {
                newHeight = Math.min(newHeight, AndroidUtilities.dp(120.0f) + origHeight);
            }
            int currentHeight = this.emojiView.getLayoutParams().height;
            if (currentHeight != newHeight) {
                Animator animator = this.stickersExpansionAnim;
                if (animator != null) {
                    animator.cancel();
                    this.stickersExpansionAnim = null;
                }
                this.stickersExpandedHeight = newHeight;
                if (currentHeight > newHeight) {
                    AnimatorSet anims = new AnimatorSet();
                    anims.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)})});
                    ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda0(this));
                    anims.setDuration(300);
                    anims.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    anims.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            }
                        }
                    });
                    this.stickersExpansionAnim = anims;
                    this.emojiView.setLayerType(2, (Paint) null);
                    anims.start();
                    return;
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                int start = this.messageEditText.getSelectionStart();
                int end = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(start, end);
                AnimatorSet anims2 = new AnimatorSet();
                anims2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)})});
                ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda11(this));
                anims2.setDuration(300);
                anims2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                anims2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                        ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                    }
                });
                this.stickersExpansionAnim = anims2;
                this.emojiView.setLayerType(2, (Paint) null);
                anims2.start();
            }
        }
    }

    /* renamed from: lambda$checkStickresExpandHeight$52$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m648x98dfb494(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    /* renamed from: lambda$checkStickresExpandHeight$53$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m649x8a895ab3(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    public void setStickersExpanded(boolean expanded, boolean animated, boolean byDrag) {
        boolean z = expanded;
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper2 != null && adjustPanLayoutHelper2.animationInProgress()) || this.waitingForKeyboardOpenAfterAnimation || this.emojiView == null) {
            return;
        }
        if (byDrag || this.stickersExpanded != z) {
            this.stickersExpanded = z;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onStickersExpandedChange();
            }
            final int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            Animator animator = this.stickersExpansionAnim;
            if (animator != null) {
                animator.cancel();
                this.stickersExpansionAnim = null;
            }
            if (this.stickersExpanded) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                int height = this.sizeNotifierLayout.getHeight();
                this.originalViewHeight = height;
                int currentActionBarHeight = (((height - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                this.stickersExpandedHeight = currentActionBarHeight;
                if (this.searchingType == 2) {
                    this.stickersExpandedHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + origHeight);
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                int start = this.messageEditText.getSelectionStart();
                int end = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(start, end);
                if (animated) {
                    AnimatorSet anims = new AnimatorSet();
                    anims.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{1.0f})});
                    anims.setDuration(300);
                    anims.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda57(this, origHeight));
                    anims.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionAnim = anims;
                    this.emojiView.setLayerType(2, (Paint) null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    this.stickersExpansionProgress = 0.0f;
                    this.sizeNotifierLayout.invalidate();
                    anims.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY((float) (-(this.stickersExpandedHeight - origHeight)));
                    this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - origHeight)));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                if (animated) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet anims2 = new AnimatorSet();
                    anims2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{0.0f})});
                    anims2.setDuration(300);
                    anims2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda58(this, origHeight));
                    anims2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            boolean unused = ChatActivityEnterView.this.closeAnimationInProgress = false;
                            Animator unused2 = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = origHeight;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            }
                            if (ChatActivityEnterView.this.sizeNotifierLayout != null) {
                                ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                                ChatActivityEnterView.this.sizeNotifierLayout.setForeground((Drawable) null);
                                ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                            }
                            if (ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                                chatActivityEnterView.showPopup(0, chatActivityEnterView.currentPopupContentType);
                            }
                            if (ChatActivityEnterView.this.onEmojiSearchClosed != null) {
                                ChatActivityEnterView.this.onEmojiSearchClosed.run();
                                Runnable unused3 = ChatActivityEnterView.this.onEmojiSearchClosed = null;
                            }
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionProgress = 1.0f;
                    this.sizeNotifierLayout.invalidate();
                    this.stickersExpansionAnim = anims2;
                    this.emojiView.setLayerType(2, (Paint) null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    anims2.start();
                } else {
                    this.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    this.emojiView.setTranslationY(0.0f);
                    this.emojiView.getLayoutParams().height = origHeight;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground((Drawable) null);
                    this.sizeNotifierLayout.setWillNotDraw(false);
                    this.stickersArrow.setAnimationProgress(0.0f);
                }
            }
            if (z) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", NUM));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
            }
        }
    }

    /* renamed from: lambda$setStickersExpanded$54$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m694xCLASSNAMEb418a(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / ((float) (-(this.stickersExpandedHeight - origHeight))));
        this.sizeNotifierLayout.invalidate();
    }

    /* renamed from: lambda$setStickersExpanded$55$org-telegram-ui-Components-ChatActivityEnterView  reason: not valid java name */
    public /* synthetic */ void m695xb8b4e7a9(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - origHeight)));
        this.sizeNotifierLayout.invalidate();
    }

    public boolean swipeToBackEnabled() {
        FrameLayout frameLayout;
        if (this.recordingAudioVideo) {
            return false;
        }
        if (this.videoSendButton != null && isInVideoMode() && (frameLayout = this.recordedAudioPanel) != null && frameLayout.getVisibility() == 0) {
            return false;
        }
        if (!hasBotWebView() || !this.botCommandsMenuButton.isOpened()) {
            return true;
        }
        return false;
    }

    public int getHeightWithTopView() {
        int h = getMeasuredHeight();
        View view = this.topView;
        if (view == null || view.getVisibility() != 0) {
            return h;
        }
        return (int) (((float) h) - ((1.0f - this.topViewEnterProgress) * ((float) this.topView.getLayoutParams().height)));
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper2) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper2;
    }

    public AdjustPanLayoutHelper getAdjustPanLayoutHelper() {
        return this.adjustPanLayoutHelper;
    }

    public boolean panelAnimationInProgress() {
        return this.panelAnimation != null;
    }

    public float getTopViewTranslation() {
        View view = this.topView;
        if (view == null || view.getVisibility() == 8) {
            return 0.0f;
        }
        return this.topView.getTranslationY();
    }

    public int getAnimatedTop() {
        return this.animatedTop;
    }

    public void checkAnimation() {
    }

    private class ScrimDrawable extends Drawable {
        private Paint paint;

        public ScrimDrawable() {
            Paint paint2 = new Paint();
            this.paint = paint2;
            paint2.setColor(0);
        }

        public void draw(Canvas canvas) {
            if (ChatActivityEnterView.this.emojiView != null) {
                this.paint.setAlpha(Math.round(ChatActivityEnterView.this.stickersExpansionProgress * 102.0f));
                canvas.drawRect(0.0f, 0.0f, (float) ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ((float) ChatActivityEnterView.this.getHeight())) + ((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()), this.paint);
            }
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }
    }

    private class SlideTextView extends View {
        Paint arrowPaint = new Paint(1);
        Path arrowPath = new Path();
        TextPaint bluePaint;
        float cancelAlpha;
        int cancelCharOffset;
        StaticLayout cancelLayout;
        public Rect cancelRect = new Rect();
        String cancelString;
        float cancelToProgress;
        float cancelWidth;
        TextPaint grayPaint;
        private int lastSize;
        long lastUpdateTime;
        boolean moveForward;
        private boolean pressed;
        Drawable selectableBackground;
        float slideProgress;
        float slideToAlpha;
        String slideToCancelString;
        float slideToCancelWidth;
        StaticLayout slideToLayout;
        boolean smallSize;
        float xOffset = 0.0f;

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == 3 || event.getAction() == 1) {
                setPressed(false);
            }
            if (this.cancelToProgress == 0.0f || !isEnabled()) {
                return false;
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 0) {
                boolean contains = this.cancelRect.contains(x, y);
                this.pressed = contains;
                if (contains) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.selectableBackground.setHotspot((float) x, (float) y);
                    }
                    setPressed(true);
                }
                return this.pressed;
            }
            boolean z = this.pressed;
            if (!z) {
                return z;
            }
            if (event.getAction() != 2 || this.cancelRect.contains(x, y)) {
                if (event.getAction() == 1 && this.cancelRect.contains(x, y)) {
                    onCancelButtonPressed();
                }
                return true;
            }
            setPressed(false);
            return false;
        }

        public void onCancelButtonPressed() {
            if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                MediaController.getInstance().stopRecording(0, false, 0);
            } else {
                CameraController.getInstance().cancelOnInitRunnable(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                ChatActivityEnterView.this.delegate.needStartRecordVideo(5, true, 0);
            }
            boolean unused = ChatActivityEnterView.this.recordingAudioVideo = false;
            ChatActivityEnterView.this.updateRecordInterface(2);
        }

        public SlideTextView(Context context) {
            super(context);
            this.smallSize = AndroidUtilities.displaySize.x <= AndroidUtilities.dp(320.0f);
            TextPaint textPaint = new TextPaint(1);
            this.grayPaint = textPaint;
            textPaint.setTextSize((float) AndroidUtilities.dp(this.smallSize ? 13.0f : 15.0f));
            TextPaint textPaint2 = new TextPaint(1);
            this.bluePaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.bluePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.arrowPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_messagePanelIcons"));
            this.arrowPaint.setStyle(Paint.Style.STROKE);
            this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(this.smallSize ? 1.0f : 1.6f));
            this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
            this.arrowPaint.setStrokeJoin(Paint.Join.ROUND);
            this.slideToCancelString = LocaleController.getString("SlideToCancel", NUM);
            this.slideToCancelString = this.slideToCancelString.charAt(0) + this.slideToCancelString.substring(1).toLowerCase();
            String upperCase = LocaleController.getString("Cancel", NUM).toUpperCase();
            this.cancelString = upperCase;
            this.cancelCharOffset = this.slideToCancelString.indexOf(upperCase);
            updateColors();
        }

        public void updateColors() {
            this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
            this.bluePaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordVoiceCancel"));
            this.slideToAlpha = (float) this.grayPaint.getAlpha();
            this.cancelAlpha = (float) this.bluePaint.getAlpha();
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(60.0f), 0, ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor("chat_recordVoiceCancel"), 26));
            this.selectableBackground = createSimpleSelectorCircleDrawable;
            createSimpleSelectorCircleDrawable.setCallback(this);
        }

        /* access modifiers changed from: protected */
        public void drawableStateChanged() {
            super.drawableStateChanged();
            this.selectableBackground.setState(getDrawableState());
        }

        public boolean verifyDrawable(Drawable drawable) {
            return this.selectableBackground == drawable || super.verifyDrawable(drawable);
        }

        public void jumpDrawablesToCurrentState() {
            super.jumpDrawablesToCurrentState();
            Drawable drawable = this.selectableBackground;
            if (drawable != null) {
                drawable.jumpToCurrentState();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int currentSize = getMeasuredHeight() + (getMeasuredWidth() << 16);
            if (this.lastSize != currentSize) {
                this.lastSize = currentSize;
                this.slideToCancelWidth = this.grayPaint.measureText(this.slideToCancelString);
                this.cancelWidth = this.bluePaint.measureText(this.cancelString);
                this.lastUpdateTime = System.currentTimeMillis();
                int heightHalf = getMeasuredHeight() >> 1;
                this.arrowPath.reset();
                if (this.smallSize) {
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(2.5f), ((float) heightHalf) - AndroidUtilities.dpf2(3.12f));
                    this.arrowPath.lineTo(0.0f, (float) heightHalf);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(2.5f), ((float) heightHalf) + AndroidUtilities.dpf2(3.12f));
                } else {
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(4.0f), ((float) heightHalf) - AndroidUtilities.dpf2(5.0f));
                    this.arrowPath.lineTo(0.0f, (float) heightHalf);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(4.0f), ((float) heightHalf) + AndroidUtilities.dpf2(5.0f));
                }
                this.slideToLayout = new StaticLayout(this.slideToCancelString, this.grayPaint, (int) this.slideToCancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.cancelLayout = new StaticLayout(this.cancelString, this.bluePaint, (int) this.cancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            StaticLayout staticLayout;
            float xi;
            Canvas canvas2 = canvas;
            if (this.slideToLayout != null && (staticLayout = this.cancelLayout) != null) {
                int w = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
                this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
                this.grayPaint.setAlpha((int) (this.slideToAlpha * (1.0f - this.cancelToProgress) * this.slideProgress));
                this.bluePaint.setAlpha((int) (this.cancelAlpha * this.cancelToProgress));
                this.arrowPaint.setColor(this.grayPaint.getColor());
                boolean z = true;
                if (this.smallSize) {
                    this.xOffset = (float) AndroidUtilities.dp(16.0f);
                } else {
                    long dt = System.currentTimeMillis() - this.lastUpdateTime;
                    this.lastUpdateTime = System.currentTimeMillis();
                    if (this.cancelToProgress == 0.0f && this.slideProgress > 0.8f) {
                        if (this.moveForward) {
                            float dp = this.xOffset + ((((float) AndroidUtilities.dp(3.0f)) / 250.0f) * ((float) dt));
                            this.xOffset = dp;
                            if (dp > ((float) AndroidUtilities.dp(6.0f))) {
                                this.xOffset = (float) AndroidUtilities.dp(6.0f);
                                this.moveForward = false;
                            }
                        } else {
                            float dp2 = this.xOffset - ((((float) AndroidUtilities.dp(3.0f)) / 250.0f) * ((float) dt));
                            this.xOffset = dp2;
                            if (dp2 < ((float) (-AndroidUtilities.dp(6.0f)))) {
                                this.xOffset = (float) (-AndroidUtilities.dp(6.0f));
                                this.moveForward = true;
                            }
                        }
                    }
                }
                if (this.cancelCharOffset < 0) {
                    z = false;
                }
                boolean enableTransition = z;
                int slideX = ((int) ((((float) getMeasuredWidth()) - this.slideToCancelWidth) / 2.0f)) + AndroidUtilities.dp(5.0f);
                int cancelX = (int) ((((float) getMeasuredWidth()) - this.cancelWidth) / 2.0f);
                float offset = enableTransition ? this.slideToLayout.getPrimaryHorizontal(this.cancelCharOffset) : 0.0f;
                float cancelDiff = enableTransition ? (((float) slideX) + offset) - ((float) cancelX) : 0.0f;
                float f = this.xOffset;
                float f2 = this.cancelToProgress;
                float x = ((((float) slideX) + ((f * (1.0f - f2)) * this.slideProgress)) - (f2 * cancelDiff)) + ((float) AndroidUtilities.dp(16.0f));
                float offsetY = enableTransition ? 0.0f : this.cancelToProgress * ((float) AndroidUtilities.dp(12.0f));
                if (this.cancelToProgress != 1.0f) {
                    int slideDelta = (int) (((float) ((-getMeasuredWidth()) / 4)) * (1.0f - this.slideProgress));
                    canvas.save();
                    canvas2.clipRect(ChatActivityEnterView.this.recordTimerView.getLeftProperty() + ((float) AndroidUtilities.dp(4.0f)), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.save();
                    canvas2.translate((float) ((((int) x) - AndroidUtilities.dp(this.smallSize ? 7.0f : 10.0f)) + slideDelta), offsetY);
                    canvas2.drawPath(this.arrowPath, this.arrowPaint);
                    canvas.restore();
                    canvas.save();
                    canvas2.translate((float) (((int) x) + slideDelta), (((float) (getMeasuredHeight() - this.slideToLayout.getHeight())) / 2.0f) + offsetY);
                    this.slideToLayout.draw(canvas2);
                    canvas.restore();
                    canvas.restore();
                }
                float yi = ((float) (getMeasuredHeight() - this.cancelLayout.getHeight())) / 2.0f;
                if (!enableTransition) {
                    yi -= ((float) AndroidUtilities.dp(12.0f)) - offsetY;
                }
                if (enableTransition) {
                    xi = x + offset;
                } else {
                    xi = (float) cancelX;
                }
                boolean z2 = enableTransition;
                this.cancelRect.set((int) xi, (int) yi, (int) (((float) this.cancelLayout.getWidth()) + xi), (int) (((float) this.cancelLayout.getHeight()) + yi));
                this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
                if (this.cancelToProgress > 0.0f) {
                    this.selectableBackground.setBounds((getMeasuredWidth() / 2) - w, (getMeasuredHeight() / 2) - w, (getMeasuredWidth() / 2) + w, (getMeasuredHeight() / 2) + w);
                    this.selectableBackground.draw(canvas2);
                    canvas.save();
                    canvas2.translate(xi, yi);
                    this.cancelLayout.draw(canvas2);
                    canvas.restore();
                } else {
                    setPressed(false);
                }
                if (this.cancelToProgress != 1.0f) {
                    invalidate();
                }
            }
        }

        public void setCancelToProgress(float cancelToProgress2) {
            this.cancelToProgress = cancelToProgress2;
        }

        public float getSlideToCancelWidth() {
            return this.slideToCancelWidth;
        }

        public void setSlideX(float v) {
            this.slideProgress = v;
        }
    }

    public class TimerView extends View {
        StaticLayout inLayout;
        boolean isRunning;
        long lastSendTypingTime;
        float left;
        String oldString;
        StaticLayout outLayout;
        final float replaceDistance;
        SpannableStringBuilder replaceIn = new SpannableStringBuilder();
        SpannableStringBuilder replaceOut = new SpannableStringBuilder();
        SpannableStringBuilder replaceStable = new SpannableStringBuilder();
        float replaceTransition;
        long startTime;
        long stopTime;
        boolean stoppedInternal;
        final TextPaint textPaint;

        public TimerView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            this.replaceDistance = (float) AndroidUtilities.dp(15.0f);
            textPaint2.setTextSize((float) AndroidUtilities.dp(15.0f));
            textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            updateColors();
        }

        public void start() {
            this.isRunning = true;
            long currentTimeMillis = System.currentTimeMillis();
            this.startTime = currentTimeMillis;
            this.lastSendTypingTime = currentTimeMillis;
            invalidate();
        }

        public void stop() {
            if (this.isRunning) {
                this.isRunning = false;
                if (this.startTime > 0) {
                    this.stopTime = System.currentTimeMillis();
                }
                invalidate();
            }
            this.lastSendTypingTime = 0;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String newString;
            String str;
            long t;
            Canvas canvas2 = canvas;
            long currentTimeMillis = System.currentTimeMillis();
            long t2 = this.isRunning ? currentTimeMillis - this.startTime : this.stopTime - this.startTime;
            long time = t2 / 1000;
            int ms = ((int) (t2 % 1000)) / 10;
            if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null && t2 >= 59500 && !this.stoppedInternal) {
                float unused = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                this.stoppedInternal = true;
            }
            if (this.isRunning && currentTimeMillis > this.lastSendTypingTime + 5000) {
                this.lastSendTypingTime = currentTimeMillis;
                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).sendTyping(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.getThreadMessageId(), (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? 1 : 7, 0);
            }
            if (time / 60 >= 60) {
                newString = String.format(Locale.US, "%01d:%02d:%02d,%d", new Object[]{Long.valueOf((time / 60) / 60), Long.valueOf((time / 60) % 60), Long.valueOf(time % 60), Integer.valueOf(ms / 10)});
            } else {
                newString = String.format(Locale.US, "%01d:%02d,%d", new Object[]{Long.valueOf(time / 60), Long.valueOf(time % 60), Integer.valueOf(ms / 10)});
            }
            if (newString.length() < 3 || (str = this.oldString) == null || str.length() < 3 || newString.length() != this.oldString.length() || newString.charAt(newString.length() - 3) == this.oldString.charAt(newString.length() - 3)) {
                long j = t2;
                if (this.replaceStable == null) {
                    this.replaceStable = new SpannableStringBuilder(newString);
                }
                if (this.replaceStable.length() == 0 || this.replaceStable.length() != newString.length()) {
                    this.replaceStable.clear();
                    this.replaceStable.append(newString);
                } else {
                    SpannableStringBuilder spannableStringBuilder = this.replaceStable;
                    spannableStringBuilder.replace(spannableStringBuilder.length() - 1, this.replaceStable.length(), newString, (newString.length() - 1) - (newString.length() - this.replaceStable.length()), newString.length());
                }
            } else {
                int n = newString.length();
                this.replaceIn.clear();
                this.replaceOut.clear();
                this.replaceStable.clear();
                this.replaceIn.append(newString);
                this.replaceOut.append(this.oldString);
                this.replaceStable.append(newString);
                int inLast = -1;
                int inCount = 0;
                int outCount = 0;
                int outLast = -1;
                int i = 0;
                while (true) {
                    long currentTimeMillis2 = currentTimeMillis;
                    if (i >= n - 1) {
                        break;
                    }
                    if (this.oldString.charAt(i) != newString.charAt(i)) {
                        if (outCount == 0) {
                            outLast = i;
                        }
                        outCount++;
                        if (inCount != 0) {
                            EmptyStubSpan span = new EmptyStubSpan();
                            if (i == n - 2) {
                                inCount++;
                            }
                            t = t2;
                            this.replaceIn.setSpan(span, inLast, inLast + inCount, 33);
                            this.replaceOut.setSpan(span, inLast, inLast + inCount, 33);
                            inCount = 0;
                        } else {
                            t = t2;
                        }
                    } else {
                        t = t2;
                        if (inCount == 0) {
                            inLast = i;
                        }
                        inCount++;
                        if (outCount != 0) {
                            this.replaceStable.setSpan(new EmptyStubSpan(), outLast, outLast + outCount, 33);
                            outCount = 0;
                        }
                    }
                    i++;
                    currentTimeMillis = currentTimeMillis2;
                    t2 = t;
                }
                if (inCount != 0) {
                    EmptyStubSpan span2 = new EmptyStubSpan();
                    this.replaceIn.setSpan(span2, inLast, inLast + inCount + 1, 33);
                    this.replaceOut.setSpan(span2, inLast, inLast + inCount + 1, 33);
                }
                if (outCount != 0) {
                    this.replaceStable.setSpan(new EmptyStubSpan(), outLast, outLast + outCount, 33);
                }
                this.inLayout = new StaticLayout(this.replaceIn, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.outLayout = new StaticLayout(this.replaceOut, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.replaceTransition = 1.0f;
            }
            float f = this.replaceTransition;
            if (f != 0.0f) {
                float f2 = f - 0.15f;
                this.replaceTransition = f2;
                if (f2 < 0.0f) {
                    this.replaceTransition = 0.0f;
                }
            }
            float y = (float) (getMeasuredHeight() / 2);
            if (this.replaceTransition == 0.0f) {
                this.replaceStable.clearSpans();
                StaticLayout staticLayout = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.save();
                canvas2.translate(0.0f, y - (((float) staticLayout.getHeight()) / 2.0f));
                staticLayout.draw(canvas2);
                canvas.restore();
                this.left = staticLayout.getLineWidth(0) + 0.0f;
            } else {
                if (this.inLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) ((1.0f - this.replaceTransition) * 255.0f));
                    canvas2.translate(0.0f, (y - (((float) this.inLayout.getHeight()) / 2.0f)) - (this.replaceDistance * this.replaceTransition));
                    this.inLayout.draw(canvas2);
                    canvas.restore();
                }
                if (this.outLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) (this.replaceTransition * 255.0f));
                    canvas2.translate(0.0f, (y - (((float) this.outLayout.getHeight()) / 2.0f)) + (this.replaceDistance * (1.0f - this.replaceTransition)));
                    this.outLayout.draw(canvas2);
                    canvas.restore();
                }
                canvas.save();
                this.textPaint.setAlpha(255);
                StaticLayout staticLayout2 = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas2.translate(0.0f, y - (((float) staticLayout2.getHeight()) / 2.0f));
                staticLayout2.draw(canvas2);
                canvas.restore();
                this.left = staticLayout2.getLineWidth(0) + 0.0f;
            }
            this.oldString = newString;
            if (this.isRunning || this.replaceTransition != 0.0f) {
                invalidate();
            }
        }

        public void updateColors() {
            this.textPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
        }

        public float getLeftProperty() {
            return this.left;
        }

        public void reset() {
            this.isRunning = false;
            this.startTime = 0;
            this.stopTime = 0;
            this.stoppedInternal = false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean pannelAnimationEnabled() {
        return true;
    }

    public RecordCircle getRecordCicle() {
        return this.recordCircle;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0132, code lost:
        r1 = (androidx.recyclerview.widget.LinearLayoutManager) r7.botCommandsMenuContainer.listView.getLayoutManager();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r8, int r9) {
        /*
            r7 = this;
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            if (r0 == 0) goto L_0x0048
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0048
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r0.measure(r8, r9)
            r0 = 0
        L_0x0010:
            android.widget.ImageView[] r1 = r7.emojiButton
            int r2 = r1.length
            if (r0 >= r2) goto L_0x002f
            r1 = r1[r0]
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            r2 = 1092616192(0x41200000, float:10.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.BotCommandsMenuView r3 = r7.botCommandsMenuButton
            int r3 = r3.getMeasuredWidth()
            int r2 = r2 + r3
            r1.leftMargin = r2
            int r0 = r0 + 1
            goto L_0x0010
        L_0x002f:
            org.telegram.ui.Components.EditTextCaption r0 = r7.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1113849856(0x42640000, float:57.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.BotCommandsMenuView r2 = r7.botCommandsMenuButton
            int r2 = r2.getMeasuredWidth()
            int r1 = r1 + r2
            r0.leftMargin = r1
            goto L_0x00c6
        L_0x0048:
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            if (r0 == 0) goto L_0x009d
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x009d
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            int r0 = r0.width
            org.telegram.ui.Components.SenderSelectView r1 = r7.senderSelectView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            int r1 = r1.height
            org.telegram.ui.Components.SenderSelectView r2 = r7.senderSelectView
            r3 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r2.measure(r4, r3)
            r2 = 0
        L_0x0072:
            android.widget.ImageView[] r3 = r7.emojiButton
            int r4 = r3.length
            if (r2 >= r4) goto L_0x008b
            r3 = r3[r2]
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r3 = (android.view.ViewGroup.MarginLayoutParams) r3
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r0
            r3.leftMargin = r4
            int r2 = r2 + 1
            goto L_0x0072
        L_0x008b:
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r2 = (android.view.ViewGroup.MarginLayoutParams) r2
            r3 = 1115422720(0x427CLASSNAME, float:63.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r0
            r2.leftMargin = r3
            goto L_0x00c6
        L_0x009d:
            r0 = 0
        L_0x009e:
            android.widget.ImageView[] r1 = r7.emojiButton
            int r2 = r1.length
            if (r0 >= r2) goto L_0x00b6
            r1 = r1[r0]
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            r2 = 1077936128(0x40400000, float:3.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.leftMargin = r2
            int r0 = r0 + 1
            goto L_0x009e
        L_0x00b6:
            org.telegram.ui.Components.EditTextCaption r0 = r7.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.leftMargin = r1
        L_0x00c6:
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            if (r0 == 0) goto L_0x0166
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r0 = r7.botCommandsAdapter
            int r0 = r0.getItemCount()
            r1 = 4
            r2 = 0
            if (r0 <= r1) goto L_0x00e7
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            r1 = 1126354125(0x4322cccd, float:162.8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r0 = java.lang.Math.max(r2, r0)
            goto L_0x010a
        L_0x00e7:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r3 = r7.botCommandsAdapter
            int r3 = r3.getItemCount()
            int r1 = java.lang.Math.min(r1, r3)
            r3 = 1
            int r1 = java.lang.Math.max(r3, r1)
            int r1 = r1 * 36
            int r1 = r1 + 8
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r0 = java.lang.Math.max(r2, r0)
        L_0x010a:
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            int r1 = r1.getPaddingTop()
            if (r1 == r0) goto L_0x0166
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.setTopGlowOffset(r0)
            int r1 = r7.botCommandLastPosition
            r3 = -1
            if (r1 != r3) goto L_0x0159
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0159
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            if (r1 == 0) goto L_0x0159
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r1 = (androidx.recyclerview.widget.LinearLayoutManager) r1
            int r3 = r1.findFirstVisibleItemPosition()
            if (r3 < 0) goto L_0x0159
            android.view.View r4 = r1.findViewByPosition(r3)
            if (r4 == 0) goto L_0x0159
            r7.botCommandLastPosition = r3
            int r5 = r4.getTop()
            org.telegram.ui.Components.BotCommandsMenuContainer r6 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r6 = r6.listView
            int r6 = r6.getPaddingTop()
            int r5 = r5 - r6
            r7.botCommandLastTop = r5
        L_0x0159:
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r2, r0, r2, r3)
        L_0x0166:
            super.onMeasure(r8, r9)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r7.botWebViewButton
            if (r0 == 0) goto L_0x0190
            org.telegram.ui.Components.BotCommandsMenuView r1 = r7.botCommandsMenuButton
            if (r1 == 0) goto L_0x0178
            int r1 = r1.getMeasuredWidth()
            r0.setMeasuredButtonWidth(r1)
        L_0x0178:
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r7.botWebViewButton
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            int r1 = r7.getMeasuredHeight()
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            r0.height = r1
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r7.botWebViewButton
            r7.measureChild(r0, r8, r9)
        L_0x0190:
            org.telegram.ui.Components.BotWebViewMenuContainer r0 = r7.botWebViewMenuContainer
            if (r0 == 0) goto L_0x01a7
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            int r1 = r1.getMeasuredHeight()
            r0.bottomMargin = r1
            org.telegram.ui.Components.BotWebViewMenuContainer r1 = r7.botWebViewMenuContainer
            r7.measureChild(r1, r8, r9)
        L_0x01a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.botCommandLastPosition != -1) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) this.botCommandsMenuContainer.listView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(this.botCommandLastPosition, this.botCommandLastTop);
            }
            this.botCommandLastPosition = -1;
        }
    }

    private void beginDelayedTransition() {
        HashMap<View, Float> hashMap = this.animationParamsX;
        ImageView[] imageViewArr = this.emojiButton;
        hashMap.put(imageViewArr[0], Float.valueOf(imageViewArr[0].getX()));
        HashMap<View, Float> hashMap2 = this.animationParamsX;
        ImageView[] imageViewArr2 = this.emojiButton;
        hashMap2.put(imageViewArr2[1], Float.valueOf(imageViewArr2[1].getX()));
        HashMap<View, Float> hashMap3 = this.animationParamsX;
        EditTextCaption editTextCaption = this.messageEditText;
        hashMap3.put(editTextCaption, Float.valueOf(editTextCaption.getX()));
    }

    public void setBotInfo(LongSparseArray<TLRPC.BotInfo> botInfo) {
        if (botInfo.size() == 1 && botInfo.valueAt(0).user_id == this.dialog_id) {
            TLRPC.BotInfo info2 = botInfo.valueAt(0);
            TLRPC.BotMenuButton menuButton = info2.menu_button;
            if (menuButton instanceof TLRPC.TL_botMenuButton) {
                TLRPC.TL_botMenuButton webViewButton = (TLRPC.TL_botMenuButton) menuButton;
                this.botMenuWebViewTitle = webViewButton.text;
                this.botMenuWebViewUrl = webViewButton.url;
                this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
            } else if (!info2.commands.isEmpty()) {
                this.botMenuButtonType = BotMenuButtonType.COMMANDS;
            } else {
                this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
            }
        } else {
            this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
        }
        BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter2 = this.botCommandsAdapter;
        if (botCommandsAdapter2 != null) {
            botCommandsAdapter2.setBotInfo(botInfo);
        }
    }

    public boolean botCommandsMenuIsShowing() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        return botCommandsMenuView != null && botCommandsMenuView.isOpened();
    }

    public void hideBotCommands() {
        this.botCommandsMenuButton.setOpened(false);
        if (hasBotWebView()) {
            this.botWebViewMenuContainer.dismiss();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    public void setTextTransitionIsRunning(boolean b) {
        this.textTransitionIsRunning = b;
        this.sendButtonContainer.invalidate();
    }

    public float getTopViewHeight() {
        View view = this.topView;
        if (view == null || view.getVisibility() != 0) {
            return 0.0f;
        }
        return (float) this.topView.getLayoutParams().height;
    }

    public void runEmojiPanelAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.runEmojiPanelAnimation);
        this.runEmojiPanelAnimation.run();
    }

    public Drawable getStickersArrowDrawable() {
        return this.stickersArrow;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 == null || emojiView2.getVisibility() != 0 || this.emojiView.getStickersExpandOffset() == 0.0f) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.save();
        canvas.clipRect(0, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
        canvas.translate(0.0f, -this.emojiView.getStickersExpandOffset());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint2 = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint2 != null ? paint2 : Theme.getThemePaint(paintKey);
    }

    public void setChatSearchExpandOffset(float chatSearchExpandOffset2) {
        this.chatSearchExpandOffset = chatSearchExpandOffset2;
        invalidate();
    }
}
