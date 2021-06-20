package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_keyboardButton;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestPhone;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestPoll;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BotCommandsMenuView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    /* access modifiers changed from: protected */
    public int animatedTop;
    /* access modifiers changed from: private */
    public int animatingContentType;
    /* access modifiers changed from: private */
    public HashMap<View, Float> animationParamsX = new HashMap<>();
    private ImageView attachButton;
    /* access modifiers changed from: private */
    public LinearLayout attachLayout;
    /* access modifiers changed from: private */
    public ImageView audioSendButton;
    private TLRPC$TL_document audioToSend;
    /* access modifiers changed from: private */
    public MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    /* access modifiers changed from: private */
    public AnimatorSet audioVideoButtonAnimation;
    /* access modifiers changed from: private */
    public FrameLayout audioVideoButtonContainer;
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
    private MessageObject botMessageObject;
    private TLRPC$TL_replyKeyboardMarkup botReplyMarkup;
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
    private boolean clearBotButtonsOnKeyboardOpen;
    /* access modifiers changed from: private */
    public boolean closeAnimationInProgress;
    /* access modifiers changed from: private */
    public int codePointCount;
    /* access modifiers changed from: private */
    public boolean configAnimationsEnabled;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentEmojiIcon;
    /* access modifiers changed from: private */
    public int currentLimit = -1;
    /* access modifiers changed from: private */
    public int currentPopupContentType;
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
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public ImageView[] emojiButton;
    private ImageView emojiButton1;
    private ImageView emojiButton2;
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
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean hasBotCommands;
    /* access modifiers changed from: private */
    public boolean hasRecordVideo;
    private Runnable hideKeyboardRunnable;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public Drawable inactinveSendButtonDrawable;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
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
    public int lineCount = 1;
    private int[] location;
    /* access modifiers changed from: private */
    public Drawable lockShadowDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate = new View.AccessibilityDelegate() {
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
            accessibilityNodeInfo.setClickable(true);
            accessibilityNodeInfo.setLongClickable(true);
        }
    };
    protected EditTextCaption messageEditText;
    boolean messageTransitionIsRunning;
    private TLRPC$WebPage messageWebPage;
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
    public Runnable onFinishInitCameraRunnable;
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
    private TLRPC$KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    /* access modifiers changed from: private */
    public MediaActionDrawable playPauseDrawable;
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
    private Property<View, Integer> roundedTranslationYProperty;
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
    public LinearLayout textFieldContainer;
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

    public interface ChatActivityEnterViewDelegate {

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$bottomPanelTranslationYChanged(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate, float f) {
            }

            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return true;
            }

            public static void $default$onTrendingStickersShowed(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate, boolean z) {
            }

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$prepareMessageSending(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }
        }

        void bottomPanelTranslationYChanged(float f);

        void didPressAttachButton();

        boolean hasScheduledMessages();

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
    }

    static /* synthetic */ boolean lambda$new$9(View view, MotionEvent motionEvent) {
        return true;
    }

    public void checkAnimation() {
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int i, int i2) {
    }

    /* access modifiers changed from: protected */
    public boolean pannelAnimationEnabled() {
        return true;
    }

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            SeekBarWaveform unused = ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate() {
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                public final void onSeekBarDrag(float f) {
                    ChatActivityEnterView.SeekBarWaveformView.this.lambda$new$0$ChatActivityEnterView$SeekBarWaveformView(f);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ChatActivityEnterView$SeekBarWaveformView(float f) {
            if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = f;
                MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, f);
            }
        }

        public void setWaveform(byte[] bArr) {
            ChatActivityEnterView.this.seekBarWaveform.setWaveform(bArr);
            invalidate();
        }

        public void setProgress(float f) {
            ChatActivityEnterView.this.seekBarWaveform.setProgress(f);
            invalidate();
        }

        public boolean isDragging() {
            return ChatActivityEnterView.this.seekBarWaveform.isDragging();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean onTouch = ChatActivityEnterView.this.seekBarWaveform.onTouch(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY());
            if (onTouch) {
                if (motionEvent.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            if (onTouch || super.onTouchEvent(motionEvent)) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatActivityEnterView.this.seekBarWaveform.setSize(i3 - i, i4 - i2);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(Theme.getColor("chat_recordedVoiceProgress"), Theme.getColor("chat_recordedVoiceProgressInner"), Theme.getColor("chat_recordedVoiceProgress"));
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
            int color = Theme.getColor("chat_recordedVoiceDot");
            int color2 = Theme.getColor("chat_messagePanelBackground");
            ChatActivityEnterView.this.redDotPaint.setColor(color);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", color);
            this.drawable.setLayerColor("Box.**", color);
            this.drawable.setLayerColor("Line 1.**", color2);
            this.drawable.setLayerColor("Line 2.**", color2);
            this.drawable.setLayerColor("Line 3.**", color2);
            this.drawable.commitApplyLayerColors();
            if (ChatActivityEnterView.this.playPauseDrawable != null) {
                ChatActivityEnterView.this.playPauseDrawable.setColor(Theme.getColor("chat_recordedVoicePlayPause"));
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
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.playing) {
                this.drawable.setAlpha((int) (this.alpha * 255.0f));
            }
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.enterAnimation) {
                this.alpha = 1.0f;
            } else if (this.isIncr || this.playing) {
                float f = this.alpha + (((float) currentTimeMillis) / 600.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            } else {
                float f2 = this.alpha - (((float) currentTimeMillis) / 600.0f);
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
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused2 = ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused3 = ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused4 = ChatActivityEnterView.this.micOutline = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.micOutline.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            Drawable unused5 = ChatActivityEnterView.this.cameraOutline = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraOutline.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
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
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockShadow"), PorterDuff.Mode.MULTIPLY));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground"));
            this.tooltipPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, NUM);
            this.tooltipMessage = LocaleController.getString("SlideUpToLock", NUM);
            this.iconScale = 1.0f;
            float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            this.touchSlop = scaledTouchSlop;
            this.touchSlop = scaledTouchSlop * scaledTouchSlop;
            updateColors();
        }

        public void setAmplitude(double d) {
            this.bigWaveDrawable.setValue((float) (Math.min(1800.0d, d) / 1800.0d), true);
            this.tinyWaveDrawable.setValue((float) (Math.min(1800.0d, d) / 1800.0d), false);
            float min = (float) (Math.min(1800.0d, d) / 1800.0d);
            this.animateToAmplitude = min;
            this.animateAmplitudeDiff = (min - this.amplitude) / 375.0f;
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        @Keep
        public void setScale(float f) {
            this.scale = f;
            invalidate();
        }

        @Keep
        public void setLockAnimatedTranslation(float f) {
            this.lockAnimatedTranslation = f;
            invalidate();
        }

        @Keep
        public void setSnapAnimationProgress(float f) {
            this.snapAnimationProgress = f;
            invalidate();
        }

        @Keep
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

        public int setLockTranslation(float f) {
            if (f == 10000.0f) {
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
                    this.startTranslation = f;
                }
                this.lockAnimatedTranslation = f;
                invalidate();
                if (this.canceledByGesture || this.slideToCancelProgress < 0.7f || this.startTranslation - this.lockAnimatedTranslation < ((float) AndroidUtilities.dp(57.0f))) {
                    return 1;
                }
                this.sendButtonVisible = true;
                return 2;
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.sendButtonVisible) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 0) {
                    boolean contains = ChatActivityEnterView.this.pauseRect.contains((float) x, (float) y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (motionEvent.getAction() == 1 && ChatActivityEnterView.this.pauseRect.contains((float) x, (float) y)) {
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
        @SuppressLint({"DrawAllocation"})
        public void onMeasure(int i, int i2) {
            int dp = AndroidUtilities.dp(194.0f);
            StaticLayout staticLayout = new StaticLayout(this.tooltipMessage, this.tooltipPaint, AndroidUtilities.dp(220.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            this.tooltipLayout = staticLayout;
            int lineCount = staticLayout.getLineCount();
            this.tooltipWidth = 0.0f;
            for (int i3 = 0; i3 < lineCount; i3++) {
                float lineWidth = this.tooltipLayout.getLineWidth(i3);
                if (lineWidth > this.tooltipWidth) {
                    this.tooltipWidth = lineWidth;
                }
            }
            if (this.tooltipLayout.getLineCount() > 1) {
                dp += this.tooltipLayout.getHeight() - this.tooltipLayout.getLineBottom(0);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(dp, NUM));
            float measuredWidth = ((float) getMeasuredWidth()) * 0.35f;
            if (measuredWidth > ((float) AndroidUtilities.dp(140.0f))) {
                measuredWidth = (float) AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-measuredWidth) * (1.0f - this.slideToCancelProgress));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x0574  */
        /* JADX WARNING: Removed duplicated region for block: B:162:0x05d8  */
        /* JADX WARNING: Removed duplicated region for block: B:165:0x0651  */
        /* JADX WARNING: Removed duplicated region for block: B:168:0x0664  */
        /* JADX WARNING: Removed duplicated region for block: B:182:0x068d  */
        /* JADX WARNING: Removed duplicated region for block: B:187:0x06ab  */
        /* JADX WARNING: Removed duplicated region for block: B:192:0x06d8  */
        /* JADX WARNING: Removed duplicated region for block: B:193:0x07ff  */
        /* JADX WARNING: Removed duplicated region for block: B:197:0x082a  */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x082f  */
        /* JADX WARNING: Removed duplicated region for block: B:209:0x0848  */
        /* JADX WARNING: Removed duplicated region for block: B:214:0x085d  */
        /* JADX WARNING: Removed duplicated region for block: B:221:0x0894  */
        /* JADX WARNING: Removed duplicated region for block: B:224:0x098f  */
        /* JADX WARNING: Removed duplicated region for block: B:227:0x099e  */
        /* JADX WARNING: Removed duplicated region for block: B:233:0x0a74  */
        /* JADX WARNING: Removed duplicated region for block: B:238:0x0ab0  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r38) {
            /*
                r37 = this;
                r6 = r37
                r7 = r38
                boolean r0 = r6.skipDraw
                if (r0 == 0) goto L_0x0009
                return
            L_0x0009:
                android.text.StaticLayout r0 = r6.tooltipLayout
                r8 = 1
                r9 = 0
                r10 = 0
                if (r0 == 0) goto L_0x0026
                int r0 = r0.getLineCount()
                if (r0 <= r8) goto L_0x0026
                android.text.StaticLayout r0 = r6.tooltipLayout
                int r0 = r0.getHeight()
                android.text.StaticLayout r1 = r6.tooltipLayout
                int r1 = r1.getLineBottom(r9)
                int r0 = r0 - r1
                float r0 = (float) r0
                r11 = r0
                goto L_0x0027
            L_0x0026:
                r11 = 0
            L_0x0027:
                int r0 = r37.getMeasuredWidth()
                r1 = 1104150528(0x41d00000, float:26.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp2(r1)
                int r12 = r0 - r1
                r0 = 1126825984(0x432a0000, float:170.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r0 + r11
                int r0 = (int) r0
                int r1 = r6.slideDelta
                int r1 = r1 + r12
                float r1 = (float) r1
                r6.drawingCx = r1
                float r13 = (float) r0
                r6.drawingCy = r13
                float r1 = r6.lockAnimatedTranslation
                r2 = 1176256512(0x461CLASSNAME, float:10000.0)
                r3 = 1113849856(0x42640000, float:57.0)
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 == 0) goto L_0x0069
                float r2 = r6.startTranslation
                float r2 = r2 - r1
                int r1 = (int) r2
                int r1 = java.lang.Math.max(r9, r1)
                float r1 = (float) r1
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r2 = (float) r2
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0067
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
            L_0x0067:
                r14 = r1
                goto L_0x006a
            L_0x0069:
                r14 = 0
            L_0x006a:
                float r1 = r6.scale
                r2 = 1048576000(0x3e800000, float:0.25)
                r4 = 1056964608(0x3var_, float:0.5)
                r15 = 1065353216(0x3var_, float:1.0)
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 > 0) goto L_0x007a
                float r1 = r1 / r4
            L_0x0077:
                r16 = r1
                goto L_0x0098
            L_0x007a:
                r5 = 1061158912(0x3var_, float:0.75)
                int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r5 > 0) goto L_0x008a
                float r1 = r1 - r4
                float r1 = r1 / r2
                r4 = 1036831949(0x3dcccccd, float:0.1)
                float r1 = r1 * r4
                float r1 = r15 - r1
                goto L_0x0077
            L_0x008a:
                r4 = 1063675494(0x3var_, float:0.9)
                r5 = 1061158912(0x3var_, float:0.75)
                float r1 = r1 - r5
                float r1 = r1 / r2
                r5 = 1036831949(0x3dcccccd, float:0.1)
                float r1 = r1 * r5
                float r1 = r1 + r4
                goto L_0x0077
            L_0x0098:
                long r4 = java.lang.System.currentTimeMillis()
                long r8 = r6.lastUpdateTime
                long r8 = r4 - r8
                float r1 = r6.animateToAmplitude
                float r4 = r6.amplitude
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 == 0) goto L_0x00c4
                float r5 = r6.animateAmplitudeDiff
                float r3 = (float) r8
                float r3 = r3 * r5
                float r4 = r4 + r3
                r6.amplitude = r4
                int r3 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
                if (r3 <= 0) goto L_0x00bb
                int r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
                if (r3 <= 0) goto L_0x00c1
                r6.amplitude = r1
                goto L_0x00c1
            L_0x00bb:
                int r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
                if (r3 >= 0) goto L_0x00c1
                r6.amplitude = r1
            L_0x00c1:
                r37.invalidate()
            L_0x00c4:
                boolean r1 = r6.canceledByGesture
                r19 = 1060320051(0x3var_, float:0.7)
                if (r1 == 0) goto L_0x00d8
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r3 = r6.slideToCancelProgress
                float r3 = r15 - r3
                float r1 = r1.getInterpolation(r3)
                float r1 = r1 * r19
                goto L_0x00e1
            L_0x00d8:
                float r1 = r6.slideToCancelProgress
                r3 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 * r3
                float r1 = r1 + r19
            L_0x00e1:
                float r3 = r6.circleRadius
                float r4 = r6.circleRadiusAmplitude
                float r5 = r6.amplitude
                float r4 = r4 * r5
                float r3 = r3 + r4
                float r3 = r3 * r16
                float r3 = r3 * r1
                r6.progressToSeekbarStep3 = r10
                float r1 = r6.transformToSeekbar
                r20 = 1098907648(0x41800000, float:16.0)
                r21 = 1053609165(0x3ecccccd, float:0.4)
                r22 = 1073741824(0x40000000, float:2.0)
                int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
                if (r4 == 0) goto L_0x0161
                r4 = 1052938076(0x3eCLASSNAMEf5c, float:0.38)
                r5 = 1052602532(0x3ebd70a4, float:0.37)
                int r23 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r23 <= 0) goto L_0x0108
                goto L_0x010c
            L_0x0108:
                float r23 = r1 / r4
                r15 = r23
            L_0x010c:
                r23 = 1059145646(0x3var_ae, float:0.63)
                int r23 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
                if (r23 <= 0) goto L_0x0116
                r1 = 1065353216(0x3var_, float:1.0)
                goto L_0x011c
            L_0x0116:
                float r1 = r1 - r4
                float r1 = r1 / r2
                float r1 = java.lang.Math.max(r10, r1)
            L_0x011c:
                float r10 = r6.transformToSeekbar
                float r10 = r10 - r4
                float r10 = r10 - r2
                float r10 = r10 / r5
                r2 = 0
                float r4 = java.lang.Math.max(r2, r10)
                r6.progressToSeekbarStep3 = r4
                org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r4 = r2.getInterpolation(r15)
                float r1 = r2.getInterpolation(r1)
                float r5 = r6.progressToSeekbarStep3
                float r2 = r2.getInterpolation(r5)
                r6.progressToSeekbarStep3 = r2
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
                float r2 = (float) r2
                float r2 = r2 * r4
                float r3 = r3 + r2
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r2 = r2.recordedAudioBackground
                int r2 = r2.getMeasuredHeight()
                float r2 = (float) r2
                float r2 = r2 / r22
                float r3 = r3 - r2
                r5 = 1065353216(0x3var_, float:1.0)
                float r15 = r5 - r1
                float r3 = r3 * r15
                float r3 = r3 + r2
                r15 = r1
                r10 = r3
                r26 = r4
                r5 = 1065353216(0x3var_, float:1.0)
                r25 = 0
                goto L_0x01ca
            L_0x0161:
                float r1 = r6.exitTransition
                r2 = 0
                int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r4 == 0) goto L_0x01c2
                r2 = 1058642330(0x3var_a, float:0.6)
                int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r4 <= 0) goto L_0x0172
                r4 = 1065353216(0x3var_, float:1.0)
                goto L_0x0174
            L_0x0172:
                float r4 = r1 / r2
            L_0x0174:
                org.telegram.ui.Components.ChatActivityEnterView r5 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r5 = r5.messageTransitionIsRunning
                if (r5 == 0) goto L_0x017b
                goto L_0x0183
            L_0x017b:
                float r1 = r1 - r2
                float r1 = r1 / r21
                r5 = 0
                float r1 = java.lang.Math.max(r5, r1)
            L_0x0183:
                org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r4 = r5.getInterpolation(r4)
                float r1 = r5.getInterpolation(r1)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
                float r5 = (float) r5
                float r5 = r5 * r4
                float r3 = r3 + r5
                r5 = 1065353216(0x3var_, float:1.0)
                float r15 = r5 - r1
                float r3 = r3 * r15
                org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r10 = r10.configAnimationsEnabled
                if (r10 == 0) goto L_0x01b9
                float r10 = r6.exitTransition
                int r15 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
                if (r15 <= 0) goto L_0x01b9
                float r10 = r10 - r2
                float r10 = r10 / r21
                float r15 = r5 - r10
                r2 = 0
                float r5 = java.lang.Math.max(r2, r15)
                r25 = r1
                r10 = r3
                r26 = r4
                goto L_0x01c0
            L_0x01b9:
                r25 = r1
                r10 = r3
                r26 = r4
                r5 = 1065353216(0x3var_, float:1.0)
            L_0x01c0:
                r15 = 0
                goto L_0x01ca
            L_0x01c2:
                r10 = r3
                r5 = 1065353216(0x3var_, float:1.0)
                r15 = 0
                r25 = 0
                r26 = 0
            L_0x01ca:
                boolean r1 = r6.canceledByGesture
                if (r1 == 0) goto L_0x01e0
                float r1 = r6.slideToCancelProgress
                int r2 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
                if (r2 <= 0) goto L_0x01e0
                float r1 = r1 - r19
                r2 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 / r2
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = r2 - r1
                float r5 = r5 * r1
            L_0x01e0:
                float r1 = r6.progressToSeekbarStep3
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0203
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                java.lang.String r3 = "chat_recordedVoiceBackground"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                float r4 = r6.progressToSeekbarStep3
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r4)
                r1.setColor(r2)
                goto L_0x0212
            L_0x0203:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.setColor(r2)
            L_0x0212:
                r1 = 0
                boolean r2 = r37.isSendButtonVisible()
                r27 = 1125515264(0x43160000, float:150.0)
                if (r2 == 0) goto L_0x0259
                float r2 = r6.progressToSendButton
                r3 = 1065353216(0x3var_, float:1.0)
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0250
                float r1 = (float) r8
                float r1 = r1 / r27
                float r2 = r2 + r1
                r6.progressToSendButton = r2
                int r1 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r1 <= 0) goto L_0x022f
                r6.progressToSendButton = r3
            L_0x022f:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                if (r1 == 0) goto L_0x024a
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r1 = r1.videoSendButton
                java.lang.Object r1 = r1.getTag()
                if (r1 == 0) goto L_0x024a
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.cameraDrawable
                goto L_0x0250
            L_0x024a:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.micDrawable
            L_0x0250:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.sendDrawable
            L_0x0256:
                r3 = r1
                r4 = r2
                goto L_0x027b
            L_0x0259:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r2 = r2.videoSendButton
                if (r2 == 0) goto L_0x0274
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r2 = r2.videoSendButton
                java.lang.Object r2 = r2.getTag()
                if (r2 == 0) goto L_0x0274
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.cameraDrawable
                goto L_0x0256
            L_0x0274:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.micDrawable
                goto L_0x0256
            L_0x027b:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r1 = r1.sendRect
                int r2 = r4.getIntrinsicWidth()
                int r2 = r2 / 2
                int r2 = r12 - r2
                int r28 = r4.getIntrinsicHeight()
                int r28 = r28 / 2
                r29 = r11
                int r11 = r0 - r28
                int r28 = r4.getIntrinsicWidth()
                int r28 = r28 / 2
                r30 = r10
                int r10 = r12 + r28
                int r28 = r4.getIntrinsicHeight()
                int r28 = r28 / 2
                r31 = r5
                int r5 = r0 + r28
                r1.set(r2, r11, r10, r5)
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r1 = r1.sendRect
                r4.setBounds(r1)
                if (r3 == 0) goto L_0x02d6
                int r1 = r3.getIntrinsicWidth()
                int r1 = r1 / 2
                int r1 = r12 - r1
                int r2 = r3.getIntrinsicHeight()
                int r2 = r2 / 2
                int r2 = r0 - r2
                int r5 = r3.getIntrinsicWidth()
                int r5 = r5 / 2
                int r5 = r5 + r12
                int r10 = r3.getIntrinsicHeight()
                int r10 = r10 / 2
                int r0 = r0 + r10
                r3.setBounds(r1, r2, r5, r0)
            L_0x02d6:
                r0 = 1113849856(0x42640000, float:57.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r14 / r0
                r1 = 1065353216(0x3var_, float:1.0)
                float r10 = r1 - r0
                boolean r0 = r6.incIdle
                if (r0 == 0) goto L_0x02f9
                float r0 = r6.idleProgress
                r2 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 + r2
                r6.idleProgress = r0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x030b
                r0 = 0
                r6.incIdle = r0
                r6.idleProgress = r1
                goto L_0x030b
            L_0x02f9:
                float r0 = r6.idleProgress
                r1 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 - r1
                r6.idleProgress = r0
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 >= 0) goto L_0x030b
                r0 = 1
                r6.incIdle = r0
                r6.idleProgress = r1
            L_0x030b:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r0 = r0.configAnimationsEnabled
                r11 = 1094713344(0x41400000, float:12.0)
                if (r0 == 0) goto L_0x0375
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r1 = 1111228416(0x423CLASSNAME, float:47.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r0.minRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r1 = 1111228416(0x423CLASSNAME, float:47.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r2 = 1097859072(0x41700000, float:15.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r5 = org.telegram.ui.Components.BlobDrawable.FORM_SMALL_MAX
                float r2 = r2 * r5
                float r1 = r1 + r2
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
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r2 = (float) r2
                float r5 = org.telegram.ui.Components.BlobDrawable.FORM_BIG_MAX
                float r2 = r2 * r5
                float r1 = r1 + r2
                r0.maxRadius = r1
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                r0.updateAmplitude(r8)
                org.telegram.ui.Components.BlobDrawable r0 = r6.bigWaveDrawable
                float r1 = r0.amplitude
                r2 = 1065437102(0x3var_ae, float:1.01)
                r0.update(r1, r2)
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                r0.updateAmplitude(r8)
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                float r1 = r0.amplitude
                r2 = 1065520988(0x3var_f5c, float:1.02)
                r0.update(r1, r2)
            L_0x0375:
                long r0 = java.lang.System.currentTimeMillis()
                r6.lastUpdateTime = r0
                float r0 = r6.slideToCancelProgress
                int r1 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
                if (r1 <= 0) goto L_0x0384
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x0386
            L_0x0384:
                float r0 = r0 / r19
            L_0x0386:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r1 = r1.configAnimationsEnabled
                if (r1 == 0) goto L_0x0423
                r1 = 1065353216(0x3var_, float:1.0)
                int r2 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1))
                if (r2 == 0) goto L_0x0423
                int r2 = (r25 > r21 ? 1 : (r25 == r21 ? 0 : -1))
                if (r2 >= 0) goto L_0x0423
                r2 = 0
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 <= 0) goto L_0x0423
                boolean r2 = r6.canceledByGesture
                if (r2 != 0) goto L_0x0423
                boolean r2 = r6.showWaves
                if (r2 == 0) goto L_0x03b7
                float r2 = r6.wavesEnterAnimation
                int r5 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r5 == 0) goto L_0x03b7
                r5 = 1025758986(0x3d23d70a, float:0.04)
                float r2 = r2 + r5
                r6.wavesEnterAnimation = r2
                int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r2 <= 0) goto L_0x03b7
                r6.wavesEnterAnimation = r1
            L_0x03b7:
                boolean r1 = r6.voiceEnterTransitionInProgress
                if (r1 != 0) goto L_0x0423
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r2 = r6.wavesEnterAnimation
                float r1 = r1.getInterpolation(r2)
                r38.save()
                float r2 = r6.scale
                r5 = 1065353216(0x3var_, float:1.0)
                float r18 = r5 - r26
                float r2 = r2 * r18
                float r2 = r2 * r0
                float r2 = r2 * r1
                float r5 = org.telegram.ui.Components.BlobDrawable.SCALE_BIG_MIN
                r28 = 1068708659(0x3fb33333, float:1.4)
                org.telegram.ui.Components.BlobDrawable r11 = r6.bigWaveDrawable
                float r11 = r11.amplitude
                float r11 = r11 * r28
                float r5 = r5 + r11
                float r2 = r2 * r5
                int r5 = r6.slideDelta
                int r5 = r5 + r12
                float r5 = (float) r5
                r7.scale(r2, r2, r5, r13)
                org.telegram.ui.Components.BlobDrawable r2 = r6.bigWaveDrawable
                int r5 = r6.slideDelta
                int r5 = r5 + r12
                float r5 = (float) r5
                android.graphics.Paint r11 = r2.paint
                r2.draw(r5, r13, r7, r11)
                r38.restore()
                float r2 = r6.scale
                float r2 = r2 * r18
                float r2 = r2 * r0
                float r2 = r2 * r1
                float r0 = org.telegram.ui.Components.BlobDrawable.SCALE_SMALL_MIN
                r1 = 1068708659(0x3fb33333, float:1.4)
                org.telegram.ui.Components.BlobDrawable r5 = r6.tinyWaveDrawable
                float r5 = r5.amplitude
                float r5 = r5 * r1
                float r0 = r0 + r5
                float r2 = r2 * r0
                r38.save()
                int r0 = r6.slideDelta
                int r0 = r0 + r12
                float r0 = (float) r0
                r7.scale(r2, r2, r0, r13)
                org.telegram.ui.Components.BlobDrawable r0 = r6.tinyWaveDrawable
                int r1 = r6.slideDelta
                int r1 = r1 + r12
                float r1 = (float) r1
                android.graphics.Paint r2 = r0.paint
                r0.draw(r1, r13, r7, r2)
                r38.restore()
            L_0x0423:
                boolean r0 = r6.voiceEnterTransitionInProgress
                if (r0 != 0) goto L_0x0560
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                int r1 = r6.paintAlpha
                float r1 = (float) r1
                float r1 = r1 * r31
                int r1 = (int) r1
                r0.setAlpha(r1)
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 != 0) goto L_0x0560
                float r0 = r6.transformToSeekbar
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x051e
                float r0 = r6.progressToSeekbarStep3
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0506
                float r0 = r13 + r30
                float r1 = r13 - r30
                int r2 = r6.slideDelta
                int r5 = r12 + r2
                float r5 = (float) r5
                float r5 = r5 + r30
                int r2 = r2 + r12
                float r2 = (float) r2
                float r2 = r2 - r30
                org.telegram.ui.Components.ChatActivityEnterView r11 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r11 = r11.recordedAudioBackground
                android.view.ViewParent r28 = r11.getParent()
                android.view.View r28 = (android.view.View) r28
                r32 = r3
                r33 = r8
                r3 = r28
                r28 = 0
                r31 = 0
            L_0x0470:
                android.view.ViewParent r8 = r37.getParent()
                if (r3 == r8) goto L_0x0489
                int r8 = r3.getTop()
                int r28 = r28 + r8
                int r8 = r3.getLeft()
                int r31 = r31 + r8
                android.view.ViewParent r3 = r3.getParent()
                android.view.View r3 = (android.view.View) r3
                goto L_0x0470
            L_0x0489:
                int r3 = r11.getTop()
                int r3 = r3 + r28
                int r8 = r37.getTop()
                int r3 = r3 - r8
                int r8 = r11.getBottom()
                int r8 = r8 + r28
                int r9 = r37.getTop()
                int r8 = r8 - r9
                int r9 = r11.getRight()
                int r9 = r9 + r31
                int r28 = r37.getLeft()
                int r9 = r9 - r28
                int r28 = r11.getLeft()
                int r28 = r28 + r31
                int r31 = r37.getLeft()
                r35 = r10
                int r10 = r28 - r31
                r28 = r14
                org.telegram.ui.Components.ChatActivityEnterView r14 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r14 = r14.isInVideoMode()
                if (r14 == 0) goto L_0x04c5
                r11 = 0
                goto L_0x04cc
            L_0x04c5:
                int r11 = r11.getMeasuredHeight()
                float r11 = (float) r11
                float r11 = r11 / r22
            L_0x04cc:
                float r3 = (float) r3
                float r1 = r1 - r3
                float r14 = r6.progressToSeekbarStep3
                r24 = 1065353216(0x3var_, float:1.0)
                float r31 = r24 - r14
                float r1 = r1 * r31
                float r3 = r3 + r1
                float r1 = (float) r8
                float r0 = r0 - r1
                float r8 = r24 - r14
                float r0 = r0 * r8
                float r1 = r1 + r0
                float r0 = (float) r10
                float r2 = r2 - r0
                float r8 = r24 - r14
                float r2 = r2 * r8
                float r0 = r0 + r2
                float r2 = (float) r9
                float r5 = r5 - r2
                float r8 = r24 - r14
                float r5 = r5 * r8
                float r2 = r2 + r5
                float r10 = r30 - r11
                float r5 = r24 - r14
                float r10 = r10 * r5
                float r11 = r11 + r10
                android.graphics.RectF r5 = r6.rectF
                r5.set(r0, r3, r2, r1)
                android.graphics.RectF r0 = r6.rectF
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                r7.drawRoundRect(r0, r11, r11, r1)
                r8 = r30
                goto L_0x0535
            L_0x0506:
                r32 = r3
                r33 = r8
                r35 = r10
                r28 = r14
                int r0 = r6.slideDelta
                int r0 = r0 + r12
                float r0 = (float) r0
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                r8 = r30
                r7.drawCircle(r0, r13, r8, r1)
                goto L_0x0535
            L_0x051e:
                r32 = r3
                r33 = r8
                r35 = r10
                r28 = r14
                r8 = r30
                int r0 = r6.slideDelta
                int r0 = r0 + r12
                float r0 = (float) r0
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                r7.drawCircle(r0, r13, r8, r1)
            L_0x0535:
                r38.save()
                r0 = 1065353216(0x3var_, float:1.0)
                float r1 = r0 - r25
                int r2 = r6.slideDelta
                float r2 = (float) r2
                r3 = 0
                r7.translate(r2, r3)
                float r5 = r6.progressToSendButton
                float r2 = r0 - r15
                float r2 = r2 * r1
                r0 = 1132396544(0x437var_, float:255.0)
                float r2 = r2 * r0
                int r9 = (int) r2
                r0 = r37
                r1 = r38
                r2 = r4
                r10 = r32
                r3 = r10
                r11 = r4
                r4 = r5
                r5 = r9
                r0.drawIconInternal(r1, r2, r3, r4, r5)
                r38.restore()
                goto L_0x056a
            L_0x0560:
                r11 = r4
                r33 = r8
                r35 = r10
                r28 = r14
                r8 = r30
                r10 = r3
            L_0x056a:
                boolean r0 = r37.isSendButtonVisible()
                r1 = 1108344832(0x42100000, float:36.0)
                r2 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x05d8
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r0 = (float) r0
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 + r29
                r4 = 1106247680(0x41var_, float:30.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                r5 = 1065353216(0x3var_, float:1.0)
                float r9 = r5 - r16
                float r4 = r4 * r9
                float r3 = r3 + r4
                float r3 = r3 - r28
                r4 = 1096810496(0x41600000, float:14.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r4 = r4 * r35
                float r3 = r3 + r4
                float r4 = r0 / r22
                float r4 = r4 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r5 = r4 - r5
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r4 = r4 - r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r4 = r4 + r9
                int r9 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1))
                if (r9 <= 0) goto L_0x05b9
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x05bb
            L_0x05b9:
                float r9 = r35 / r21
            L_0x05bb:
                r14 = 1091567616(0x41100000, float:9.0)
                r16 = 1065353216(0x3var_, float:1.0)
                float r21 = r16 - r35
                float r21 = r21 * r14
                float r14 = r6.snapAnimationProgress
                float r24 = r16 - r14
                float r21 = r21 * r24
                r24 = 1097859072(0x41700000, float:15.0)
                float r14 = r14 * r24
                float r9 = r16 - r9
                float r14 = r14 * r9
                float r21 = r21 - r14
                r9 = r21
                r14 = r35
                goto L_0x0647
            L_0x05d8:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r3 = 1096810496(0x41600000, float:14.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 * r35
                int r3 = (int) r3
                int r0 = r0 + r3
                float r0 = (float) r0
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 + r29
                r4 = 1106247680(0x41var_, float:30.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                r5 = 1065353216(0x3var_, float:1.0)
                float r9 = r5 - r16
                float r4 = r4 * r9
                int r4 = (int) r4
                float r4 = (float) r4
                float r3 = r3 + r4
                r4 = r28
                int r4 = (int) r4
                float r4 = (float) r4
                float r3 = r3 - r4
                float r4 = r6.idleProgress
                float r4 = r4 * r35
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = -r5
                float r5 = (float) r5
                float r4 = r4 * r5
                float r3 = r3 + r4
                float r4 = r0 / r22
                float r4 = r4 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r5 = r4 - r5
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r9 = r9 * r35
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r4 = r4 - r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r4 = r4 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r9 = r9 * r35
                float r4 = r4 + r9
                r9 = 1091567616(0x41100000, float:9.0)
                r14 = 1065353216(0x3var_, float:1.0)
                float r16 = r14 - r35
                float r21 = r16 * r9
                r9 = 0
                r6.snapAnimationProgress = r9
                r9 = r21
                r14 = 0
            L_0x0647:
                boolean r1 = r6.showTooltip
                r21 = 1101004800(0x41a00000, float:20.0)
                r28 = 1077936128(0x40400000, float:3.0)
                r29 = 1082130432(0x40800000, float:4.0)
                if (r1 == 0) goto L_0x0664
                long r30 = java.lang.System.currentTimeMillis()
                r32 = r3
                long r2 = r6.showTooltipStartTime
                long r30 = r30 - r2
                r2 = 200(0xc8, double:9.9E-322)
                int r36 = (r30 > r2 ? 1 : (r30 == r2 ? 0 : -1))
                if (r36 > 0) goto L_0x0662
                goto L_0x0666
            L_0x0662:
                r3 = 0
                goto L_0x066d
            L_0x0664:
                r32 = r3
            L_0x0666:
                float r2 = r6.tooltipAlpha
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x07ff
            L_0x066d:
                r2 = 1061997773(0x3f4ccccd, float:0.8)
                int r2 = (r35 > r2 ? 1 : (r35 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0686
                boolean r2 = r37.isSendButtonVisible()
                if (r2 != 0) goto L_0x0686
                float r2 = r6.exitTransition
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 != 0) goto L_0x0686
                float r2 = r6.transformToSeekbar
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x0689
            L_0x0686:
                r2 = 0
                r6.showTooltip = r2
            L_0x0689:
                boolean r2 = r6.showTooltip
                if (r2 == 0) goto L_0x06ab
                float r2 = r6.tooltipAlpha
                r3 = 1065353216(0x3var_, float:1.0)
                int r24 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                r30 = r4
                if (r24 == 0) goto L_0x06be
                r3 = r33
                float r3 = (float) r3
                float r3 = r3 / r27
                float r2 = r2 + r3
                r6.tooltipAlpha = r2
                r3 = 1065353216(0x3var_, float:1.0)
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 < 0) goto L_0x06be
                r6.tooltipAlpha = r3
                org.telegram.messenger.SharedConfig.increaseLockRecordAudioVideoHintShowed()
                goto L_0x06be
            L_0x06ab:
                r30 = r4
                r3 = r33
                float r2 = r6.tooltipAlpha
                float r3 = (float) r3
                float r3 = r3 / r27
                float r2 = r2 - r3
                r6.tooltipAlpha = r2
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x06be
                r6.tooltipAlpha = r3
            L_0x06be:
                float r2 = r6.tooltipAlpha
                r3 = 1132396544(0x437var_, float:255.0)
                float r2 = r2 * r3
                int r2 = (int) r2
                android.graphics.drawable.Drawable r3 = r6.tooltipBackground
                r3.setAlpha(r2)
                android.graphics.drawable.Drawable r3 = r6.tooltipBackgroundArrow
                r3.setAlpha(r2)
                android.text.TextPaint r3 = r6.tooltipPaint
                r3.setAlpha(r2)
                android.text.StaticLayout r3 = r6.tooltipLayout
                if (r3 == 0) goto L_0x0801
                r38.save()
                android.graphics.RectF r3 = r6.rectF
                int r4 = r37.getMeasuredWidth()
                float r4 = (float) r4
                int r1 = r37.getMeasuredHeight()
                float r1 = (float) r1
                r31 = r10
                r10 = 0
                r3.set(r10, r10, r4, r1)
                int r1 = r37.getMeasuredWidth()
                float r1 = (float) r1
                float r3 = r6.tooltipWidth
                float r1 = r1 - r3
                r3 = 1110441984(0x42300000, float:44.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r1 = r1 - r3
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                r7.translate(r1, r3)
                android.graphics.drawable.Drawable r3 = r6.tooltipBackground
                r1 = 1090519040(0x41000000, float:8.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r4 = -r4
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
                int r10 = -r10
                float r1 = r6.tooltipWidth
                r20 = r11
                r16 = 1108344832(0x42100000, float:36.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
                float r11 = (float) r11
                float r1 = r1 + r11
                int r1 = (int) r1
                android.text.StaticLayout r11 = r6.tooltipLayout
                int r11 = r11.getHeight()
                float r11 = (float) r11
                float r16 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r11 = r11 + r16
                int r11 = (int) r11
                r3.setBounds(r4, r10, r1, r11)
                android.graphics.drawable.Drawable r1 = r6.tooltipBackground
                r1.draw(r7)
                android.text.StaticLayout r1 = r6.tooltipLayout
                r1.draw(r7)
                r38.restore()
                r38.save()
                float r1 = (float) r12
                r3 = 1099431936(0x41880000, float:17.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                android.text.StaticLayout r4 = r6.tooltipLayout
                int r4 = r4.getHeight()
                float r4 = (float) r4
                float r4 = r4 / r22
                float r3 = r3 + r4
                float r4 = r6.idleProgress
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r4 = r4 * r10
                float r3 = r3 - r4
                r7.translate(r1, r3)
                android.graphics.Path r1 = r6.path
                r1.reset()
                android.graphics.Path r1 = r6.path
                r3 = 1084227584(0x40a00000, float:5.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                float r3 = -r3
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                r1.setLastPoint(r3, r4)
                android.graphics.Path r1 = r6.path
                r3 = 0
                r1.lineTo(r3, r3)
                android.graphics.Path r1 = r6.path
                r3 = 1084227584(0x40a00000, float:5.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                r1.lineTo(r3, r4)
                android.graphics.Paint r1 = r6.p
                r3 = -1
                r1.setColor(r3)
                android.graphics.Paint r1 = r6.p
                r1.setAlpha(r2)
                android.graphics.Paint r1 = r6.p
                android.graphics.Paint$Style r2 = android.graphics.Paint.Style.STROKE
                r1.setStyle(r2)
                android.graphics.Paint r1 = r6.p
                android.graphics.Paint$Cap r2 = android.graphics.Paint.Cap.ROUND
                r1.setStrokeCap(r2)
                android.graphics.Paint r1 = r6.p
                android.graphics.Paint$Join r2 = android.graphics.Paint.Join.ROUND
                r1.setStrokeJoin(r2)
                android.graphics.Paint r1 = r6.p
                r2 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                r1.setStrokeWidth(r2)
                android.graphics.Path r1 = r6.path
                android.graphics.Paint r2 = r6.p
                r7.drawPath(r1, r2)
                r38.restore()
                r38.save()
                android.graphics.drawable.Drawable r1 = r6.tooltipBackgroundArrow
                int r2 = r1.getIntrinsicWidth()
                int r2 = r2 / 2
                int r2 = r12 - r2
                android.text.StaticLayout r3 = r6.tooltipLayout
                int r3 = r3.getHeight()
                float r3 = (float) r3
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r3 = r3 + r4
                int r3 = (int) r3
                android.graphics.drawable.Drawable r4 = r6.tooltipBackgroundArrow
                int r4 = r4.getIntrinsicWidth()
                int r4 = r4 / 2
                int r4 = r4 + r12
                android.text.StaticLayout r10 = r6.tooltipLayout
                int r10 = r10.getHeight()
                float r10 = (float) r10
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r10 = r10 + r11
                int r10 = (int) r10
                android.graphics.drawable.Drawable r11 = r6.tooltipBackgroundArrow
                int r11 = r11.getIntrinsicHeight()
                int r10 = r10 + r11
                r1.setBounds(r2, r3, r4, r10)
                android.graphics.drawable.Drawable r1 = r6.tooltipBackgroundArrow
                r1.draw(r7)
                r38.restore()
                goto L_0x0805
            L_0x07ff:
                r30 = r4
            L_0x0801:
                r31 = r10
                r20 = r11
            L_0x0805:
                r38.save()
                int r1 = r37.getMeasuredWidth()
                int r2 = r37.getMeasuredHeight()
                org.telegram.ui.Components.ChatActivityEnterView r3 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.LinearLayout r3 = r3.textFieldContainer
                int r3 = r3.getMeasuredHeight()
                int r2 = r2 - r3
                r3 = 0
                r7.clipRect(r3, r3, r1, r2)
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r3 = r2 - r1
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x082f
                float r23 = r2 - r1
                r1 = r23
                goto L_0x083d
            L_0x082f:
                int r1 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x0835
                r1 = r15
                goto L_0x083d
            L_0x0835:
                int r1 = (r25 > r4 ? 1 : (r25 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x083c
                r1 = r25
                goto L_0x083d
            L_0x083c:
                r1 = 0
            L_0x083d:
                float r2 = r6.slideToCancelProgress
                int r2 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
                if (r2 < 0) goto L_0x085d
                boolean r2 = r6.canceledByGesture
                if (r2 == 0) goto L_0x0848
                goto L_0x085d
            L_0x0848:
                float r2 = r6.slideToCancelLockProgress
                r3 = 1065353216(0x3var_, float:1.0)
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0873
                r4 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 + r4
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 <= 0) goto L_0x0873
                r6.slideToCancelLockProgress = r3
                goto L_0x0873
            L_0x085d:
                r2 = 0
                r6.showTooltip = r2
                float r2 = r6.slideToCancelLockProgress
                r3 = 0
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0873
                r4 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 - r4
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x0873
                r6.slideToCancelLockProgress = r3
            L_0x0873:
                r2 = 1116733440(0x42900000, float:72.0)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r3 = r2 * r1
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r4 = r4 * r26
                r10 = 1065353216(0x3var_, float:1.0)
                float r1 = r10 - r1
                float r4 = r4 * r1
                float r3 = r3 - r4
                float r1 = r6.slideToCancelLockProgress
                float r1 = r10 - r1
                float r1 = r1 * r2
                float r3 = r3 + r1
                int r1 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0894
                goto L_0x0895
            L_0x0894:
                r2 = r3
            L_0x0895:
                r1 = 0
                r7.translate(r1, r2)
                float r1 = r6.scale
                float r15 = r10 - r15
                float r1 = r1 * r15
                float r15 = r10 - r25
                float r1 = r1 * r15
                float r3 = r6.slideToCancelLockProgress
                float r1 = r1 * r3
                float r3 = (float) r12
                r7.scale(r1, r1, r3, r5)
                android.graphics.RectF r1 = r6.rectF
                r4 = 1099956224(0x41900000, float:18.0)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r10 = r3 - r10
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r11 = r11 + r3
                float r0 = r32 + r0
                r15 = r32
                r1.set(r10, r15, r11, r0)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.lockShadowDrawable
                android.graphics.RectF r1 = r6.rectF
                float r1 = r1.left
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r1 = r1 - r10
                int r1 = (int) r1
                android.graphics.RectF r10 = r6.rectF
                float r10 = r10.top
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r10 = r10 - r11
                int r10 = (int) r10
                android.graphics.RectF r11 = r6.rectF
                float r11 = r11.right
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r11 = r11 + r15
                int r11 = (int) r11
                android.graphics.RectF r15 = r6.rectF
                float r15 = r15.bottom
                float r16 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r15 = r15 + r16
                int r15 = (int) r15
                r0.setBounds(r1, r10, r11, r15)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.lockShadowDrawable
                r0.draw(r7)
                android.graphics.RectF r0 = r6.rectF
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                android.graphics.Paint r10 = r6.lockBackgroundPaint
                r7.drawRoundRect(r0, r1, r4, r10)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.RectF r0 = r0.pauseRect
                android.graphics.RectF r1 = r6.rectF
                r0.set(r1)
                android.graphics.RectF r0 = r6.rectF
                r1 = 1086324736(0x40CLASSNAME, float:6.0)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r1 = r3 - r1
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                r10 = 1065353216(0x3var_, float:1.0)
                float r15 = r10 - r14
                float r4 = r4 * r15
                float r1 = r1 - r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r4 = r4 * r15
                float r4 = r5 - r4
                r10 = 1086324736(0x40CLASSNAME, float:6.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r10 = r10 + r12
                float r10 = (float) r10
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r11 = r11 * r15
                float r10 = r10 + r11
                r16 = r8
                r11 = 1094713344(0x41400000, float:12.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r8 = (float) r8
                float r5 = r5 + r8
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r8 = r8 * r15
                float r5 = r5 + r8
                r0.set(r1, r4, r10, r5)
                android.graphics.RectF r0 = r6.rectF
                float r4 = r0.bottom
                float r0 = r0.centerX()
                android.graphics.RectF r1 = r6.rectF
                float r1 = r1.centerY()
                r38.save()
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                r8 = 1065353216(0x3var_, float:1.0)
                float r10 = r8 - r35
                float r5 = r5 * r10
                r11 = 0
                r7.translate(r11, r5)
                r7.rotate(r9, r0, r1)
                android.graphics.RectF r5 = r6.rectF
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r28)
                r17 = r13
                android.graphics.Paint r13 = r6.lockPaint
                r7.drawRoundRect(r5, r11, r8, r13)
                r5 = 1065353216(0x3var_, float:1.0)
                int r8 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r8 == 0) goto L_0x099a
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r8 = r8 * r15
                android.graphics.Paint r11 = r6.lockBackgroundPaint
                r7.drawCircle(r0, r1, r8, r11)
            L_0x099a:
                int r0 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r0 == 0) goto L_0x0a66
                android.graphics.RectF r0 = r6.rectF
                r1 = 1090519040(0x41000000, float:8.0)
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                r11 = 0
                r0.set(r11, r11, r5, r8)
                r38.save()
                int r0 = r37.getMeasuredWidth()
                float r0 = (float) r0
                float r2 = r2 + r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r4 = r4 * r10
                float r2 = r2 + r4
                r7.clipRect(r11, r11, r0, r2)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r3 = r3 - r0
                r0 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r2 = r6.idleProgress
                r4 = 1065353216(0x3var_, float:1.0)
                float r2 = r4 - r2
                float r0 = r0 * r2
                float r0 = r0 * r35
                float r4 = r30 - r0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r0 = r0 * r10
                float r4 = r4 - r0
                r0 = 1094713344(0x41400000, float:12.0)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r0 = r0 * r14
                float r4 = r4 + r0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r2 = r6.snapAnimationProgress
                float r0 = r0 * r2
                float r4 = r4 + r0
                r7.translate(r3, r4)
                r0 = 0
                int r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                r0 = 1090519040(0x41000000, float:8.0)
                if (r2 <= 0) goto L_0x0a08
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r1 = (float) r1
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r2 = (float) r2
                r7.rotate(r9, r1, r2)
            L_0x0a08:
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                r0 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r4 = r4 * r15
                float r4 = r4 + r0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0 = r38
                r0.drawLine(r1, r2, r3, r4, r5)
                android.graphics.RectF r1 = r6.rectF
                r2 = 0
                r3 = -1020002304(0xffffffffCLASSNAME, float:-180.0)
                r4 = 0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0.drawArc(r1, r2, r3, r4, r5)
                r1 = 0
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                r3 = 0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r5 = r6.idleProgress
                float r4 = r4 * r5
                float r4 = r4 * r35
                boolean r5 = r37.isSendButtonVisible()
                r8 = 1
                r5 = r5 ^ r8
                float r5 = (float) r5
                float r4 = r4 * r5
                float r0 = r0 + r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r29)
                float r5 = r6.snapAnimationProgress
                float r4 = r4 * r5
                float r4 = r4 * r10
                float r4 = r4 + r0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0 = r38
                r0.drawLine(r1, r2, r3, r4, r5)
                r38.restore()
            L_0x0a66:
                r38.restore()
                r38.restore()
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x0ab0
                int r0 = r6.slideDelta
                int r12 = r12 + r0
                float r0 = (float) r12
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r8 = r16
                r3 = r17
                r7.drawCircle(r0, r3, r8, r2)
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x0a8e
                float r0 = r6.slideToCancelProgress
                float r15 = r1 - r0
                goto L_0x0a90
            L_0x0a8e:
                r15 = 1065353216(0x3var_, float:1.0)
            L_0x0a90:
                r38.save()
                int r0 = r6.slideDelta
                float r0 = (float) r0
                r1 = 0
                r7.translate(r0, r1)
                float r4 = r6.progressToSendButton
                r0 = 1132396544(0x437var_, float:255.0)
                float r15 = r15 * r0
                int r5 = (int) r15
                r0 = r37
                r1 = r38
                r2 = r20
                r3 = r31
                r0.drawIconInternal(r1, r2, r3, r4, r5)
                r38.restore()
                goto L_0x0ab2
            L_0x0ab0:
                r8 = r16
            L_0x0ab2:
                r6.drawingCircleRadius = r8
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        public void drawIcon(Canvas canvas, int i, int i2, float f) {
            Drawable access$3600;
            Drawable drawable = null;
            if (isSendButtonVisible()) {
                if (this.progressToSendButton != 1.0f) {
                    drawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
                }
                access$3600 = ChatActivityEnterView.this.sendDrawable;
            } else {
                access$3600 = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            }
            Drawable drawable2 = access$3600;
            Drawable drawable3 = drawable;
            ChatActivityEnterView.this.sendRect.set(i - (drawable2.getIntrinsicWidth() / 2), i2 - (drawable2.getIntrinsicHeight() / 2), (drawable2.getIntrinsicWidth() / 2) + i, (drawable2.getIntrinsicHeight() / 2) + i2);
            drawable2.setBounds(ChatActivityEnterView.this.sendRect);
            if (drawable3 != null) {
                drawable3.setBounds(i - (drawable3.getIntrinsicWidth() / 2), i2 - (drawable3.getIntrinsicHeight() / 2), i + (drawable3.getIntrinsicWidth() / 2), i2 + (drawable3.getIntrinsicHeight() / 2));
            }
            drawIconInternal(canvas, drawable2, drawable3, this.progressToSendButton, (int) (f * 255.0f));
        }

        private void drawIconInternal(Canvas canvas, Drawable drawable, Drawable drawable2, float f, int i) {
            float f2 = 0.0f;
            if (f == 0.0f || f == 1.0f || drawable2 == null) {
                boolean z = this.canceledByGesture;
                if (z && this.slideToCancelProgress == 1.0f) {
                    (ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.videoSendButton : ChatActivityEnterView.this.audioSendButton).setAlpha(1.0f);
                    setVisibility(8);
                } else if (z && this.slideToCancelProgress < 1.0f) {
                    Drawable access$4000 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                    access$4000.setBounds(drawable.getBounds());
                    float f3 = this.slideToCancelProgress;
                    if (f3 >= 0.93f) {
                        f2 = ((f3 - 0.93f) / 0.07f) * 255.0f;
                    }
                    int i2 = (int) f2;
                    access$4000.setAlpha(i2);
                    access$4000.draw(canvas);
                    access$4000.setAlpha(255);
                    drawable.setAlpha(255 - i2);
                    drawable.draw(canvas);
                } else if (!z) {
                    drawable.setAlpha(i);
                    drawable.draw(canvas);
                }
            } else {
                canvas.save();
                canvas.scale(f, f, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                float f4 = (float) i;
                drawable.setAlpha((int) (f4 * f));
                drawable.draw(canvas);
                canvas.restore();
                canvas.save();
                float f5 = 1.0f - f;
                canvas.scale(f5, f5, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                drawable2.setAlpha((int) (f4 * f5));
                drawable2.draw(canvas);
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean dispatchHoverEvent(MotionEvent motionEvent) {
            return super.dispatchHoverEvent(motionEvent) || this.virtualViewHelper.dispatchHoverEvent(motionEvent);
        }

        public void setTransformToSeekbar(float f) {
            this.transformToSeekbar = f;
            invalidate();
        }

        public float getTransformToSeekbarProgressStep3() {
            return this.progressToSeekbarStep3;
        }

        @Keep
        public float getExitTransition() {
            return this.exitTransition;
        }

        @Keep
        public void setExitTransition(float f) {
            this.exitTransition = f;
            invalidate();
        }

        public void updateColors() {
            ChatActivityEnterView.this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
            this.tinyWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("chat_messagePanelVoiceBackground"), 38));
            this.bigWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("chat_messagePanelVoiceBackground"), 76));
            this.tooltipPaint.setColor(Theme.getColor("chat_gifSaveHintText"));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground"));
            this.tooltipBackgroundArrow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
            this.lockBackgroundPaint.setColor(Theme.getColor("key_chat_messagePanelVoiceLockBackground"));
            this.lockPaint.setColor(Theme.getColor("key_chat_messagePanelVoiceLock"));
            this.lockOutlinePaint.setColor(Theme.getColor("key_chat_messagePanelVoiceLock"));
            this.paintAlpha = ChatActivityEnterView.this.paint.getAlpha();
        }

        public void showTooltipIfNeed() {
            if (SharedConfig.lockRecordAudioVideoHint < 3) {
                this.showTooltip = true;
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        @Keep
        public float getSlideToCancelProgress() {
            return this.slideToCancelProgress;
        }

        @Keep
        public void setSlideToCancelProgress(float f) {
            this.slideToCancelProgress = f;
            float measuredWidth = ((float) getMeasuredWidth()) * 0.35f;
            if (measuredWidth > ((float) AndroidUtilities.dp(140.0f))) {
                measuredWidth = (float) AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-measuredWidth) * (1.0f - f));
            invalidate();
        }

        public void canceledByGesture() {
            this.canceledByGesture = true;
        }

        public void setMovingCords(float f, float f2) {
            float f3 = this.lastMovingX;
            float f4 = (f - f3) * (f - f3);
            float f5 = this.lastMovingY;
            float f6 = f4 + ((f2 - f5) * (f2 - f5));
            this.lastMovingY = f2;
            this.lastMovingX = f;
            if (this.showTooltip && this.tooltipAlpha == 0.0f && f6 > this.touchSlop) {
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        public void showWaves(boolean z, boolean z2) {
            if (!z2) {
                this.wavesEnterAnimation = z ? 1.0f : 0.5f;
            }
            this.showWaves = z;
        }

        public void drawWaves(Canvas canvas, float f, float f2, float f3) {
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnterAnimation);
            float f4 = this.slideToCancelProgress;
            float f5 = f4 > 0.7f ? 1.0f : f4 / 0.7f;
            canvas.save();
            float f6 = this.scale * f5 * interpolation * (BlobDrawable.SCALE_BIG_MIN + (this.bigWaveDrawable.amplitude * 1.4f)) * f3;
            canvas.scale(f6, f6, f, f2);
            BlobDrawable blobDrawable = this.bigWaveDrawable;
            blobDrawable.draw(f, f2, canvas, blobDrawable.paint);
            canvas.restore();
            float f7 = this.scale * f5 * interpolation * (BlobDrawable.SCALE_SMALL_MIN + (this.tinyWaveDrawable.amplitude * 1.4f)) * f3;
            canvas.save();
            canvas.scale(f7, f7, f, f2);
            BlobDrawable blobDrawable2 = this.tinyWaveDrawable;
            blobDrawable2.draw(f, f2, canvas, blobDrawable2.paint);
            canvas.restore();
        }

        private class VirtualViewHelper extends ExploreByTouchHelper {
            /* access modifiers changed from: protected */
            public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
                return true;
            }

            public VirtualViewHelper(View view) {
                super(view);
            }

            /* access modifiers changed from: protected */
            public int getVirtualViewAt(float f, float f2) {
                if (!RecordCircle.this.isSendButtonVisible()) {
                    return -1;
                }
                if (ChatActivityEnterView.this.sendRect.contains((int) f, (int) f2)) {
                    return 1;
                }
                return ChatActivityEnterView.this.pauseRect.contains(f, f2) ? 2 : -1;
            }

            /* access modifiers changed from: protected */
            public void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(1);
                    list.add(2);
                }
            }

            /* access modifiers changed from: protected */
            public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                if (i == 1) {
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.sendRect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Send", NUM));
                } else if (i == 2) {
                    ChatActivityEnterView.this.rect.set((int) ChatActivityEnterView.this.pauseRect.left, (int) ChatActivityEnterView.this.pauseRect.top, (int) ChatActivityEnterView.this.pauseRect.right, (int) ChatActivityEnterView.this.pauseRect.bottom);
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.rect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Stop", NUM));
                }
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @SuppressLint({"ClickableViewAccessibility"})
    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        super(activity);
        String str;
        int i;
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        final Activity activity2 = activity;
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = sizeNotifierFrameLayout;
        final ChatActivity chatActivity2 = chatActivity;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.accountInstance = AccountInstance.getInstance(i2);
        this.emojiButton = new ImageView[2];
        this.currentPopupContentType = -1;
        this.currentEmojiIcon = -1;
        this.isPaused = true;
        this.startedDraggingX = -1.0f;
        this.distCanMove = (float) AndroidUtilities.dp(80.0f);
        this.location = new int[2];
        this.messageWebPageSearch = true;
        this.animatingContentType = -1;
        this.doneButtonEnabledProgress = 1.0f;
        this.doneButtonEnabled = true;
        this.openKeyboardRunnable = new Runnable() {
            public void run() {
                if (!ChatActivityEnterView.this.destroyed) {
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    if (chatActivityEnterView.messageEditText != null && chatActivityEnterView.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                        AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                        AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
                    }
                }
            }
        };
        this.updateExpandabilityRunnable = new Runnable() {
            private int lastKnownPage = -1;

            public void run() {
                int currentPage;
                if (ChatActivityEnterView.this.emojiView != null && (currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage()) != this.lastKnownPage) {
                    this.lastKnownPage = currentPage;
                    boolean access$700 = ChatActivityEnterView.this.stickersTabOpen;
                    int i = 2;
                    boolean unused = ChatActivityEnterView.this.stickersTabOpen = currentPage == 1 || currentPage == 2;
                    boolean access$800 = ChatActivityEnterView.this.emojiTabOpen;
                    boolean unused2 = ChatActivityEnterView.this.emojiTabOpen = currentPage == 0;
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                            if (currentPage != 0) {
                                i = 1;
                            }
                            int unused3 = chatActivityEnterView.searchingType = i;
                            ChatActivityEnterView.this.checkStickresExpandHeight();
                        } else if (!ChatActivityEnterView.this.stickersTabOpen) {
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                    }
                    if (access$700 != ChatActivityEnterView.this.stickersTabOpen || access$800 != ChatActivityEnterView.this.emojiTabOpen) {
                        ChatActivityEnterView.this.checkSendButton(true);
                    }
                }
            }
        };
        this.roundedTranslationYProperty = new Property<View, Integer>(Integer.class, "translationY") {
            public Integer get(View view) {
                return Integer.valueOf(Math.round(view.getTranslationY()));
            }

            public void set(View view, Integer num) {
                view.setTranslationY((float) num.intValue());
            }
        };
        this.recordCircleScale = new Property<RecordCircle, Float>(Float.class, "scale") {
            public Float get(RecordCircle recordCircle) {
                return Float.valueOf(recordCircle.getScale());
            }

            public void set(RecordCircle recordCircle, Float f) {
                recordCircle.setScale(f.floatValue());
            }
        };
        this.redDotPaint = new Paint(1);
        this.onFinishInitCameraRunnable = new Runnable() {
            public void run() {
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
                }
            }
        };
        this.recordAudioVideoRunnable = new Runnable() {
            public void run() {
                if (ChatActivityEnterView.this.delegate != null && ChatActivityEnterView.this.parentActivity != null) {
                    ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                    boolean unused = ChatActivityEnterView.this.calledRecordRunnable = true;
                    boolean unused2 = ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                    ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                    ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                    if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            boolean z = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                            boolean z2 = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                            if (!z || !z2) {
                                String[] strArr = new String[((z || z2) ? 1 : 2)];
                                if (!z && !z2) {
                                    strArr[0] = "android.permission.RECORD_AUDIO";
                                    strArr[1] = "android.permission.CAMERA";
                                } else if (!z) {
                                    strArr[0] = "android.permission.RECORD_AUDIO";
                                } else {
                                    strArr[0] = "android.permission.CAMERA";
                                }
                                ChatActivityEnterView.this.parentActivity.requestPermissions(strArr, 3);
                                return;
                            }
                        }
                        if (!CameraController.getInstance().isCameraInitied()) {
                            CameraController.getInstance().initCamera(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                        } else {
                            ChatActivityEnterView.this.onFinishInitCameraRunnable.run();
                        }
                        if (!ChatActivityEnterView.this.recordingAudioVideo) {
                            boolean unused3 = ChatActivityEnterView.this.recordingAudioVideo = true;
                            ChatActivityEnterView.this.updateRecordIntefrace(0);
                            ChatActivityEnterView.this.recordCircle.showWaves(false, false);
                            ChatActivityEnterView.this.recordTimerView.reset();
                        }
                    } else if (ChatActivityEnterView.this.parentFragment == null || Build.VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                        float unused4 = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                        MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), ChatActivityEnterView.this.recordingGuid);
                        boolean unused5 = ChatActivityEnterView.this.recordingAudioVideo = true;
                        ChatActivityEnterView.this.updateRecordIntefrace(0);
                        ChatActivityEnterView.this.recordTimerView.start();
                        boolean unused6 = ChatActivityEnterView.this.recordDot.enterAnimation = false;
                        ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                        ChatActivityEnterView.this.recordCircle.showWaves(true, false);
                    } else {
                        ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                    }
                }
            }
        };
        this.paint = new Paint(1);
        this.pauseRect = new RectF();
        this.sendRect = new Rect();
        this.rect = new Rect();
        this.topViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatActivityEnterView.this.lambda$new$25$ChatActivityEnterView(valueAnimator);
            }
        };
        this.botCommandLastPosition = -1;
        this.smoothKeyboard = z && SharedConfig.smoothKeyboard && !AndroidUtilities.isInMultiwindow && (chatActivity2 == null || !chatActivity.isInBubbleMode());
        Paint paint2 = new Paint(1);
        this.dotPaint = paint2;
        paint2.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setClipChildren(false);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentActivity = activity2;
        this.parentFragment = chatActivity2;
        if (chatActivity2 != null) {
            this.recordingGuid = chatActivity.getClassGuid();
        }
        this.sizeNotifierLayout = sizeNotifierFrameLayout2;
        sizeNotifierFrameLayout2.setDelegate(this);
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        this.sendByEnter = globalMainSettings.getBoolean("send_by_enter", false);
        this.configAnimationsEnabled = globalMainSettings.getBoolean("view_animations", true);
        LinearLayout linearLayout = new LinearLayout(activity2);
        this.textFieldContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.textFieldContainer.setClipChildren(false);
        this.textFieldContainer.setClipToPadding(false);
        this.textFieldContainer.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 1.0f, 0.0f, 0.0f));
        AnonymousClass8 r2 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (ChatActivityEnterView.this.scheduledButton != null) {
                    int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp((ChatActivityEnterView.this.botButton == null || ChatActivityEnterView.this.botButton.getVisibility() != 0) ? 48.0f : 96.0f)) - AndroidUtilities.dp(48.0f);
                    ChatActivityEnterView.this.scheduledButton.layout(measuredWidth, ChatActivityEnterView.this.scheduledButton.getTop(), ChatActivityEnterView.this.scheduledButton.getMeasuredWidth() + measuredWidth, ChatActivityEnterView.this.scheduledButton.getBottom());
                }
                if (!ChatActivityEnterView.this.animationParamsX.isEmpty()) {
                    for (int i5 = 0; i5 < getChildCount(); i5++) {
                        View childAt = getChildAt(i5);
                        Float f = (Float) ChatActivityEnterView.this.animationParamsX.get(childAt);
                        if (f != null) {
                            childAt.setTranslationX(f.floatValue() - ((float) childAt.getLeft()));
                            childAt.animate().translationX(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        }
                    }
                    ChatActivityEnterView.this.animationParamsX.clear();
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != ChatActivityEnterView.this.messageEditText) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(0, ((-getTop()) - ChatActivityEnterView.this.textFieldContainer.getTop()) - ChatActivityEnterView.this.getTop(), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(6.0f));
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }
        };
        r2.setClipChildren(false);
        this.textFieldContainer.addView(r2, LayoutHelper.createLinear(0, -2, 1.0f, 80));
        int i3 = 0;
        for (int i4 = 2; i3 < i4; i4 = 2) {
            this.emojiButton[i3] = new ImageView(activity2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (getTag() != null && ChatActivityEnterView.this.attachLayout != null && !ChatActivityEnterView.this.emojiViewVisible && !MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotPaint != null) {
                        canvas.drawCircle((float) ((getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
                    }
                }
            };
            this.emojiButton[i3].setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.emojiButton[i3].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= 21) {
                this.emojiButton[i3].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            r2.addView(this.emojiButton[i3], LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
            this.emojiButton[i3].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$1$ChatActivityEnterView(view);
                }
            });
            this.emojiButton[i3].setContentDescription(LocaleController.getString("AccDescrEmojiButton", NUM));
            if (i3 == 1) {
                this.emojiButton[i3].setVisibility(4);
                this.emojiButton[i3].setAlpha(0.0f);
                this.emojiButton[i3].setScaleX(0.1f);
                this.emojiButton[i3].setScaleY(0.1f);
                this.emojiButton2 = this.emojiButton[i3];
            } else {
                this.emojiButton1 = this.emojiButton[i3];
            }
            i3++;
        }
        setEmojiButtonImage(false, false);
        NumberTextView numberTextView = new NumberTextView(activity2);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 0.0f, 48.0f));
        AnonymousClass10 r6 = new EditTextCaption(activity2) {
            /* access modifiers changed from: private */
            /* renamed from: send */
            public void lambda$onCreateInputConnection$0(InputContentInfoCompat inputContentInfoCompat, boolean z, int i) {
                if (inputContentInfoCompat.getDescription().hasMimeType("image/gif")) {
                    SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, (String) null, (String) null, inputContentInfoCompat.getContentUri(), (String) null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), inputContentInfoCompat, (MessageObject) null, z, 0);
                } else {
                    SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, (String) null, inputContentInfoCompat.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), (CharSequence) null, (ArrayList<TLRPC$MessageEntity>) null, (ArrayList<TLRPC$InputDocument>) null, inputContentInfoCompat, 0, (MessageObject) null, z, 0);
                }
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, true, i);
                }
            }

            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                try {
                    EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png", "image/webp"});
                    return InputConnectionCompat.createWrapper(onCreateInputConnection, editorInfo, new InputConnectionCompat.OnCommitContentListener() {
                        public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                            return ChatActivityEnterView.AnonymousClass10.this.lambda$onCreateInputConnection$1$ChatActivityEnterView$10(inputContentInfoCompat, i, bundle);
                        }
                    });
                } catch (Throwable th) {
                    FileLog.e(th);
                    return onCreateInputConnection;
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onCreateInputConnection$1 */
            public /* synthetic */ boolean lambda$onCreateInputConnection$1$ChatActivityEnterView$10(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                if (BuildCompat.isAtLeastNMR1() && (i & 1) != 0) {
                    try {
                        inputContentInfoCompat.requestPermission();
                    } catch (Exception unused) {
                        return false;
                    }
                }
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(inputContentInfoCompat) {
                        public final /* synthetic */ InputContentInfoCompat f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ChatActivityEnterView.AnonymousClass10.this.lambda$onCreateInputConnection$0$ChatActivityEnterView$10(this.f$1, z, i);
                        }
                    });
                } else {
                    lambda$onCreateInputConnection$0(inputContentInfoCompat, true, 0);
                }
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!ChatActivityEnterView.this.stickersDragging && ChatActivityEnterView.this.stickersExpansionAnim == null) {
                    if (!ChatActivityEnterView.this.isPopupShowing() || motionEvent.getAction() != 0) {
                        try {
                            return super.onTouchEvent(motionEvent);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            int unused = ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(false);
                        }
                        ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            boolean unused2 = ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = true;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    ChatActivityEnterView.AnonymousClass10.this.lambda$onTouchEvent$2$ChatActivityEnterView$10();
                                }
                            }, 200);
                        } else {
                            ChatActivityEnterView.this.openKeyboardInternal();
                        }
                        return false;
                    }
                }
                return false;
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onTouchEvent$2 */
            public /* synthetic */ void lambda$onTouchEvent$2$ChatActivityEnterView$10() {
                boolean unused = ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = false;
                ChatActivityEnterView.this.openKeyboardInternal();
            }

            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (ChatActivityEnterView.this.preventInput) {
                    return false;
                }
                return super.dispatchKeyEvent(keyEvent);
            }

            /* access modifiers changed from: protected */
            public void onSelectionChanged(int i, int i2) {
                super.onSelectionChanged(i, i2);
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onTextSelectionChanged(i, i2);
                }
            }

            /* access modifiers changed from: protected */
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                if (ChatActivityEnterView.this.parentFragment != null) {
                    ChatActivityEnterView.this.parentFragment.extendActionMode(menu);
                }
            }

            public boolean requestRectangleOnScreen(Rect rect) {
                rect.bottom += AndroidUtilities.dp(1000.0f);
                return super.requestRectangleOnScreen(rect);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                boolean unused = ChatActivityEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
                super.onMeasure(i, i2);
                if (ChatActivityEnterView.this.isInitLineCount) {
                    int unused2 = ChatActivityEnterView.this.lineCount = getLineCount();
                }
                boolean unused3 = ChatActivityEnterView.this.isInitLineCount = false;
            }

            public boolean onTextContextMenuItem(int i) {
                if (i == 16908322) {
                    boolean unused = ChatActivityEnterView.this.isPaste = true;
                }
                ClipData primaryClip = ((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip();
                if (primaryClip != null) {
                    ClipDescription description = primaryClip.getDescription();
                    if (primaryClip.getItemCount() == 1 && primaryClip.getDescription().hasMimeType("image/*")) {
                        if (description.hasMimeType("image/gif")) {
                            SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, (String) null, (String) null, primaryClip.getItemAt(0).getUri(), (String) null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                        } else {
                            SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, (String) null, primaryClip.getItemAt(0).getUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), (CharSequence) null, (ArrayList<TLRPC$MessageEntity>) null, (ArrayList<TLRPC$InputDocument>) null, (InputContentInfoCompat) null, 0, (MessageObject) null, true, 0);
                        }
                    }
                }
                return super.onTextContextMenuItem(i);
            }
        };
        this.messageEditText = r6;
        r6.setDelegate(new EditTextCaption.EditTextCaptionDelegate() {
            public final void onSpansChanged() {
                ChatActivityEnterView.this.lambda$new$2$ChatActivityEnterView();
            }
        });
        this.messageEditText.setWindowView(this.parentActivity.getWindow().getDecorView());
        ChatActivity chatActivity3 = this.parentFragment;
        TLRPC$EncryptedChat currentEncryptedChat = chatActivity3 != null ? chatActivity3.getCurrentEncryptedChat() : null;
        this.messageEditText.setAllowTextEntitiesIntersection(supportsSendingNewEntities());
        updateFieldHint(false);
        this.messageEditText.setImeOptions(currentEncryptedChat != null ? NUM : NUM);
        EditTextCaption editTextCaption = this.messageEditText;
        editTextCaption.setInputType(editTextCaption.getInputType() | 16384 | 131072);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(6);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable((Drawable) null);
        this.messageEditText.setTextColor(Theme.getColor("chat_messagePanelText"));
        this.messageEditText.setHintColor(Theme.getColor("chat_messagePanelHint"));
        this.messageEditText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        this.messageEditText.setCursorColor(Theme.getColor("chat_messagePanelCursor"));
        r2.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, z ? 50.0f : 2.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() {
            boolean ctrlPressed = false;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = false;
                if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                    if (keyEvent.getAction() == 1) {
                        if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                            SharedPreferences.Editor edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                            edit.putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                        }
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            int unused = ChatActivityEnterView.this.searchingType = 0;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                            }
                            ChatActivityEnterView.this.messageEditText.requestFocus();
                        } else if (ChatActivityEnterView.this.stickersExpanded) {
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        } else if (ChatActivityEnterView.this.stickersExpansionAnim == null) {
                            ChatActivityEnterView.this.showPopup(0, 0);
                        }
                    }
                    return true;
                } else if (i == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (i != 113 && i != 114) {
                    return false;
                } else {
                    if (keyEvent.getAction() == 0) {
                        z = true;
                    }
                    this.ctrlPressed = z;
                    return true;
                }
            }
        });
        this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            boolean ctrlPressed = false;

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 4) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (keyEvent == null || i != 0) {
                    return false;
                } else {
                    if ((!this.ctrlPressed && !ChatActivityEnterView.this.sendByEnter) || keyEvent.getAction() != 0 || ChatActivityEnterView.this.editingMessageObject != null) {
                        return false;
                    }
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                }
            }
        });
        this.messageEditText.addTextChangedListener(new TextWatcher() {
            private boolean nextChangeIsSend;
            private boolean processChange;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ChatActivityEnterView.this.lineCount != ChatActivityEnterView.this.messageEditText.getLineCount()) {
                    if (!ChatActivityEnterView.this.isInitLineCount && ChatActivityEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.onLineCountChanged(chatActivityEnterView.lineCount, ChatActivityEnterView.this.messageEditText.getLineCount());
                    }
                    ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                    int unused = chatActivityEnterView2.lineCount = chatActivityEnterView2.messageEditText.getLineCount();
                }
                if (ChatActivityEnterView.this.innerTextChange != 1) {
                    if (ChatActivityEnterView.this.sendByEnter && !ChatActivityEnterView.this.isPaste && ChatActivityEnterView.this.editingMessageObject == null && i3 > i2 && charSequence.length() > 0 && charSequence.length() == i + i3 && charSequence.charAt(charSequence.length() - 1) == 10) {
                        this.nextChangeIsSend = true;
                    }
                    boolean z = false;
                    boolean unused2 = ChatActivityEnterView.this.isPaste = false;
                    ChatActivityEnterView.this.checkSendButton(true);
                    CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence.toString());
                    if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                        if (i3 > 2 || TextUtils.isEmpty(charSequence)) {
                            boolean unused3 = ChatActivityEnterView.this.messageWebPageSearch = true;
                        }
                        ChatActivityEnterViewDelegate access$1300 = ChatActivityEnterView.this.delegate;
                        if (i2 > i3 + 1 || i3 - i2 > 2) {
                            z = true;
                        }
                        access$1300.onTextChanged(charSequence, z);
                    }
                    if (ChatActivityEnterView.this.innerTextChange != 2 && i3 - i2 > 1) {
                        this.processChange = true;
                    }
                    if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && trimmedString.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - 5000 && !ChatActivityEnterView.this.ignoreTextChange) {
                        long unused4 = ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.needSendTyping();
                        }
                    }
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:34:0x013e  */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x018e  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void afterTextChanged(android.text.Editable r10) {
                /*
                    r9 = this;
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    int r0 = r0.innerTextChange
                    r1 = 0
                    if (r0 != 0) goto L_0x0047
                    boolean r0 = r9.nextChangeIsSend
                    if (r0 == 0) goto L_0x0014
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    r0.sendMessage()
                    r9.nextChangeIsSend = r1
                L_0x0014:
                    boolean r0 = r9.processChange
                    if (r0 == 0) goto L_0x0047
                    int r0 = r10.length()
                    java.lang.Class<android.text.style.ImageSpan> r2 = android.text.style.ImageSpan.class
                    java.lang.Object[] r0 = r10.getSpans(r1, r0, r2)
                    android.text.style.ImageSpan[] r0 = (android.text.style.ImageSpan[]) r0
                    r2 = 0
                L_0x0025:
                    int r3 = r0.length
                    if (r2 >= r3) goto L_0x0030
                    r3 = r0[r2]
                    r10.removeSpan(r3)
                    int r2 = r2 + 1
                    goto L_0x0025
                L_0x0030:
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.EditTextCaption r0 = r0.messageEditText
                    android.text.TextPaint r0 = r0.getPaint()
                    android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
                    r2 = 1101004800(0x41a00000, float:20.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    org.telegram.messenger.Emoji.replaceEmoji(r10, r0, r2, r1)
                    r9.processChange = r1
                L_0x0047:
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    int r2 = r10.length()
                    int r10 = java.lang.Character.codePointCount(r10, r1, r2)
                    int unused = r0.codePointCount = r10
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    int r10 = r10.currentLimit
                    r2 = 100
                    r0 = 1056964608(0x3var_, float:0.5)
                    r4 = 0
                    r5 = 1
                    r6 = 1065353216(0x3var_, float:1.0)
                    if (r10 <= 0) goto L_0x0115
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    int r10 = r10.currentLimit
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    int r7 = r7.codePointCount
                    int r10 = r10 - r7
                    r7 = 100
                    if (r10 > r7) goto L_0x0115
                    r7 = -9999(0xffffffffffffd8f1, float:NaN)
                    if (r10 >= r7) goto L_0x007b
                    r10 = -9999(0xffffffffffffd8f1, float:NaN)
                L_0x007b:
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    org.telegram.ui.Components.ChatActivityEnterView r8 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    int r8 = r8.getVisibility()
                    if (r8 != 0) goto L_0x008f
                    r8 = 1
                    goto L_0x0090
                L_0x008f:
                    r8 = 0
                L_0x0090:
                    r7.setNumber(r10, r8)
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    int r7 = r7.getVisibility()
                    if (r7 == 0) goto L_0x00c3
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    r7.setVisibility(r1)
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    r7.setAlpha(r4)
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    r7.setScaleX(r0)
                    org.telegram.ui.Components.ChatActivityEnterView r7 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r7 = r7.captionLimitView
                    r7.setScaleY(r0)
                L_0x00c3:
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r0 = r0.captionLimitView
                    android.view.ViewPropertyAnimator r0 = r0.animate()
                    r7 = 0
                    android.view.ViewPropertyAnimator r0 = r0.setListener(r7)
                    r0.cancel()
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r0 = r0.captionLimitView
                    android.view.ViewPropertyAnimator r0 = r0.animate()
                    android.view.ViewPropertyAnimator r0 = r0.alpha(r6)
                    android.view.ViewPropertyAnimator r0 = r0.scaleX(r6)
                    android.view.ViewPropertyAnimator r0 = r0.scaleY(r6)
                    android.view.ViewPropertyAnimator r0 = r0.setDuration(r2)
                    r0.start()
                    if (r10 >= 0) goto L_0x0105
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r10 = r10.captionLimitView
                    java.lang.String r0 = "windowBackgroundWhiteRedText"
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                    r10.setTextColor(r0)
                    r10 = 0
                    goto L_0x0138
                L_0x0105:
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r10 = r10.captionLimitView
                    java.lang.String r0 = "windowBackgroundWhiteGrayText"
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                    r10.setTextColor(r0)
                    goto L_0x0137
                L_0x0115:
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.NumberTextView r10 = r10.captionLimitView
                    android.view.ViewPropertyAnimator r10 = r10.animate()
                    android.view.ViewPropertyAnimator r10 = r10.alpha(r4)
                    android.view.ViewPropertyAnimator r10 = r10.scaleX(r0)
                    android.view.ViewPropertyAnimator r10 = r10.scaleY(r0)
                    android.view.ViewPropertyAnimator r10 = r10.setDuration(r2)
                    org.telegram.ui.Components.ChatActivityEnterView$13$1 r0 = new org.telegram.ui.Components.ChatActivityEnterView$13$1
                    r0.<init>()
                    r10.setListener(r0)
                L_0x0137:
                    r10 = 1
                L_0x0138:
                    org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                    boolean r2 = r0.doneButtonEnabled
                    if (r2 == r10) goto L_0x0188
                    r0.doneButtonEnabled = r10
                    android.animation.ValueAnimator r10 = r0.doneButtonColorAnimator
                    if (r10 == 0) goto L_0x014f
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    android.animation.ValueAnimator r10 = r10.doneButtonColorAnimator
                    r10.cancel()
                L_0x014f:
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    r0 = 2
                    float[] r0 = new float[r0]
                    boolean r2 = r10.doneButtonEnabled
                    if (r2 == 0) goto L_0x015a
                    r3 = 0
                    goto L_0x015c
                L_0x015a:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x015c:
                    r0[r1] = r3
                    if (r2 == 0) goto L_0x0162
                    r4 = 1065353216(0x3var_, float:1.0)
                L_0x0162:
                    r0[r5] = r4
                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                    android.animation.ValueAnimator unused = r10.doneButtonColorAnimator = r0
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    android.animation.ValueAnimator r10 = r10.doneButtonColorAnimator
                    org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$13$-C7Tyk3vBJ7UonOL7rDHDAaCQrM r0 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$13$-C7Tyk3vBJ7UonOL7rDHDAaCQrM
                    r0.<init>()
                    r10.addUpdateListener(r0)
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    android.animation.ValueAnimator r10 = r10.doneButtonColorAnimator
                    r0 = 150(0x96, double:7.4E-322)
                    android.animation.ValueAnimator r10 = r10.setDuration(r0)
                    r10.start()
                L_0x0188:
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    org.telegram.ui.Components.BotCommandsMenuContainer r10 = r10.botCommandsMenuContainer
                    if (r10 == 0) goto L_0x0191
                    r10.dismiss()
                L_0x0191:
                    org.telegram.ui.Components.ChatActivityEnterView r10 = org.telegram.ui.Components.ChatActivityEnterView.this
                    r10.checkBotMenu()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass13.afterTextChanged(android.text.Editable):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$afterTextChanged$0 */
            public /* synthetic */ void lambda$afterTextChanged$0$ChatActivityEnterView$13(ValueAnimator valueAnimator) {
                int color = Theme.getColor("chat_messagePanelVoicePressed");
                int alpha = Color.alpha(color);
                float unused = ChatActivityEnterView.this.doneButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ChatActivityEnterView.this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (((float) alpha) * ((ChatActivityEnterView.this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
                ChatActivityEnterView.this.doneButtonImage.invalidate();
            }
        });
        if (z) {
            if (this.parentFragment != null) {
                Drawable mutate = activity.getResources().getDrawable(NUM).mutate();
                Drawable mutate2 = activity.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_recordedVoiceDot"), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, mutate2);
                ImageView imageView = new ImageView(activity2);
                this.scheduledButton = imageView;
                imageView.setImageDrawable(combinedDrawable);
                this.scheduledButton.setVisibility(8);
                this.scheduledButton.setContentDescription(LocaleController.getString("ScheduledMessages", NUM));
                this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.scheduledButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                r2.addView(this.scheduledButton, LayoutHelper.createFrame(48, 48, 85));
                this.scheduledButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.lambda$new$3$ChatActivityEnterView(view);
                    }
                });
            }
            LinearLayout linearLayout2 = new LinearLayout(activity2);
            this.attachLayout = linearLayout2;
            linearLayout2.setOrientation(0);
            this.attachLayout.setEnabled(false);
            this.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
            this.attachLayout.setClipChildren(false);
            r2.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            BotCommandsMenuView botCommandsMenuView = new BotCommandsMenuView(getContext());
            this.botCommandsMenuButton = botCommandsMenuView;
            botCommandsMenuView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$4$ChatActivityEnterView(view);
                }
            });
            r2.addView(this.botCommandsMenuButton, LayoutHelper.createFrame(-2, 32.0f, 83, 10.0f, 8.0f, 10.0f, 8.0f));
            AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, false, 1.0f, false);
            this.botCommandsMenuButton.setExpanded(true, false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity2);
            AnonymousClass14 r7 = new BotCommandsMenuContainer(activity2) {
                /* access modifiers changed from: protected */
                public void onDismiss() {
                    super.onDismiss();
                    ChatActivityEnterView.this.botCommandsMenuButton.setOpened(false);
                }
            };
            this.botCommandsMenuContainer = r7;
            r7.listView.setLayoutManager(linearLayoutManager);
            RecyclerListView recyclerListView = this.botCommandsMenuContainer.listView;
            BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter2 = new BotCommandsMenuView.BotCommandsAdapter();
            this.botCommandsAdapter = botCommandsAdapter2;
            recyclerListView.setAdapter(botCommandsAdapter2);
            this.botCommandsMenuContainer.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    View view2 = view;
                    if (view2 instanceof BotCommandsMenuView.BotCommandView) {
                        String command = ((BotCommandsMenuView.BotCommandView) view2).getCommand();
                        if (!TextUtils.isEmpty(command)) {
                            if (ChatActivityEnterView.this.isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate(command) {
                                    public final /* synthetic */ String f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void didSelectDate(boolean z, int i) {
                                        ChatActivityEnterView.AnonymousClass15.this.lambda$onItemClick$0$ChatActivityEnterView$15(this.f$1, z, i);
                                    }
                                });
                                return;
                            }
                            ChatActivity chatActivity = chatActivity2;
                            if (chatActivity == null || !chatActivity.checkSlowMode(view2)) {
                                SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(command, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                                ChatActivityEnterView.this.setFieldText("");
                                ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                            }
                        }
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onItemClick$0 */
                public /* synthetic */ void lambda$onItemClick$0$ChatActivityEnterView$15(String str, boolean z, int i) {
                    SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(str, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, (MessageObject.SendAnimationData) null);
                    ChatActivityEnterView.this.setFieldText("");
                    ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                }
            });
            this.botCommandsMenuContainer.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public boolean onItemClick(View view, int i) {
                    if (!(view instanceof BotCommandsMenuView.BotCommandView)) {
                        return false;
                    }
                    String command = ((BotCommandsMenuView.BotCommandView) view).getCommand();
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.setFieldText(command + " ");
                    ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                    return true;
                }
            });
            this.botCommandsMenuContainer.setClipToPadding(false);
            this.sizeNotifierLayout.addView(this.botCommandsMenuContainer, 14, LayoutHelper.createFrame(-1, -1.0f, 80, 0.0f, 0.0f, 0.0f, 47.0f));
            this.botCommandsMenuContainer.setVisibility(8);
            ImageView imageView2 = new ImageView(activity2);
            this.botButton = imageView2;
            ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(activity2);
            this.botButtonDrawable = replaceableIconDrawable;
            imageView2.setImageDrawable(replaceableIconDrawable);
            this.botButtonDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.botButtonDrawable.setIcon(NUM, false);
            this.botButton.setScaleType(ImageView.ScaleType.CENTER);
            int i5 = Build.VERSION.SDK_INT;
            if (i5 >= 21) {
                this.botButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.botButton.setVisibility(8);
            this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$5$ChatActivityEnterView(view);
                }
            });
            this.notifyButton = new ImageView(activity2);
            CrossOutDrawable crossOutDrawable = new CrossOutDrawable(activity2, NUM, "chat_messagePanelIcons");
            this.notifySilentDrawable = crossOutDrawable;
            this.notifyButton.setImageDrawable(crossOutDrawable);
            this.notifySilentDrawable.setCrossOut(this.silent, false);
            ImageView imageView3 = this.notifyButton;
            if (this.silent) {
                i = NUM;
                str = "AccDescrChanSilentOn";
            } else {
                i = NUM;
                str = "AccDescrChanSilentOff";
            }
            imageView3.setContentDescription(LocaleController.getString(str, i));
            this.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i5 >= 21) {
                this.notifyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.notifyButton.setVisibility((!this.canWriteToChannel || ((chatActivityEnterViewDelegate = this.delegate) != null && chatActivityEnterViewDelegate.hasScheduledMessages())) ? 8 : 0);
            this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new View.OnClickListener() {
                private Toast visibleToast;

                public void onClick(View view) {
                    String str;
                    int i;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    boolean unused = chatActivityEnterView.silent = !chatActivityEnterView.silent;
                    if (ChatActivityEnterView.this.notifySilentDrawable == null) {
                        CrossOutDrawable unused2 = ChatActivityEnterView.this.notifySilentDrawable = new CrossOutDrawable(activity2, NUM, "chat_messagePanelIcons");
                    }
                    ChatActivityEnterView.this.notifySilentDrawable.setCrossOut(ChatActivityEnterView.this.silent, true);
                    ChatActivityEnterView.this.notifyButton.setImageDrawable(ChatActivityEnterView.this.notifySilentDrawable);
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                    edit.putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                    NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount).updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                    try {
                        Toast toast = this.visibleToast;
                        if (toast != null) {
                            toast.cancel();
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    chatActivity2.getUndoView().showWithAction(0, !ChatActivityEnterView.this.silent ? 54 : 55, (Runnable) null);
                    ImageView access$8600 = ChatActivityEnterView.this.notifyButton;
                    if (ChatActivityEnterView.this.silent) {
                        i = NUM;
                        str = "AccDescrChanSilentOn";
                    } else {
                        i = NUM;
                        str = "AccDescrChanSilentOff";
                    }
                    access$8600.setContentDescription(LocaleController.getString(str, i));
                    ChatActivityEnterView.this.updateFieldHint(true);
                }
            });
            ImageView imageView4 = new ImageView(activity2);
            this.attachButton = imageView4;
            imageView4.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.attachButton.setImageResource(NUM);
            this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i5 >= 21) {
                this.attachButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
            this.attachButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$6$ChatActivityEnterView(view);
                }
            });
            this.attachButton.setContentDescription(LocaleController.getString("AccDescrAttachButton", NUM));
        }
        FrameLayout frameLayout = new FrameLayout(activity2);
        this.recordedAudioPanel = frameLayout;
        frameLayout.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        r2.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        RLottieImageView rLottieImageView = new RLottieImageView(activity2);
        this.recordDeleteImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setAnimation(NUM, 28, 28);
        this.recordDeleteImageView.getAnimatedDrawable().setInvalidateOnProgressSet(true);
        updateRecordedDeleteIconColors();
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
        int i6 = Build.VERSION.SDK_INT;
        if (i6 >= 21) {
            this.recordDeleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        }
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$7$ChatActivityEnterView(view);
            }
        });
        VideoTimelineView videoTimelineView2 = new VideoTimelineView(activity2);
        this.videoTimelineView = videoTimelineView2;
        videoTimelineView2.setColor(Theme.getColor("chat_messagePanelVideoFrame"));
        this.videoTimelineView.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() {
            public void onLeftProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.startTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * f);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
                }
            }

            public void onRightProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.endTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * f);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
                }
            }

            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, -1.0f, 19, 56.0f, 0.0f, 8.0f, 0.0f));
        VideoTimelineView.TimeHintView timeHintView = new VideoTimelineView.TimeHintView(activity2);
        this.videoTimelineView.setTimeHintView(timeHintView);
        this.sizeNotifierLayout.addView(timeHintView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 52.0f));
        View view = new View(activity2);
        this.recordedAudioBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_recordedVoiceBackground")));
        this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(activity2);
        LinearLayout linearLayout3 = new LinearLayout(activity2);
        linearLayout3.setOrientation(0);
        this.recordedAudioPanel.addView(linearLayout3, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 13.0f, 0.0f));
        this.playPauseDrawable = new MediaActionDrawable();
        this.recordedAudioPlayButton = new ImageView(activity2);
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f, AndroidUtilities.dpf2(24.0f), AndroidUtilities.dpf2(24.0f));
        this.recordedAudioPlayButton.setImageMatrix(matrix);
        this.recordedAudioPlayButton.setImageDrawable(this.playPauseDrawable);
        this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.MATRIX);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 13.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$8$ChatActivityEnterView(view);
            }
        });
        TextView textView = new TextView(activity2);
        this.recordedAudioTimeTextView = textView;
        textView.setTextColor(Theme.getColor("chat_messagePanelVoiceDuration"));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        linearLayout3.addView(this.recordedAudioSeekBar, LayoutHelper.createLinear(0, 32, 1.0f, 16, 0, 0, 4, 0));
        linearLayout3.addView(this.recordedAudioTimeTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
        FrameLayout frameLayout2 = new FrameLayout(activity2);
        this.recordPanel = frameLayout2;
        frameLayout2.setClipChildren(false);
        this.recordPanel.setVisibility(8);
        r2.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48.0f));
        this.recordPanel.setOnTouchListener($$Lambda$ChatActivityEnterView$0e_zd0s8aRihfxc4NG1jgGBP7ao.INSTANCE);
        SlideTextView slideTextView = new SlideTextView(activity2);
        this.slideText = slideTextView;
        this.recordPanel.addView(slideTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 45.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout4 = new LinearLayout(activity2);
        this.recordTimeContainer = linearLayout4;
        linearLayout4.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-1, -1, 16));
        RecordDot recordDot2 = new RecordDot(activity2);
        this.recordDot = recordDot2;
        this.recordTimeContainer.addView(recordDot2, LayoutHelper.createLinear(28, 28, 16, 0, 0, 0, 0));
        TimerView timerView = new TimerView(activity2);
        this.recordTimerView = timerView;
        this.recordTimeContainer.addView(timerView, LayoutHelper.createLinear(-1, -1, 16, 6, 0, 0, 0));
        FrameLayout frameLayout3 = new FrameLayout(activity2);
        this.sendButtonContainer = frameLayout3;
        frameLayout3.setClipChildren(false);
        this.sendButtonContainer.setClipToPadding(false);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        FrameLayout frameLayout4 = new FrameLayout(activity2);
        this.audioVideoButtonContainer = frameLayout4;
        frameLayout4.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ChatActivityEnterView.this.lambda$new$16$ChatActivityEnterView(view, motionEvent);
            }
        });
        ImageView imageView5 = new ImageView(activity2);
        this.audioSendButton = imageView5;
        imageView5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.audioSendButton.setImageResource(NUM);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", NUM));
        this.audioSendButton.setFocusable(true);
        this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (z) {
            ImageView imageView6 = new ImageView(activity2);
            this.videoSendButton = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.videoSendButton.setImageResource(NUM);
            this.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.videoSendButton.setContentDescription(LocaleController.getString("AccDescrVideoMessage", NUM));
            this.videoSendButton.setFocusable(true);
            this.videoSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
            this.audioVideoButtonContainer.addView(this.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        RecordCircle recordCircle2 = new RecordCircle(activity2);
        this.recordCircle = recordCircle2;
        recordCircle2.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView7 = new ImageView(activity2);
        this.cancelBotButton = imageView7;
        imageView7.setVisibility(4);
        this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView8 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView8.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", NUM));
        this.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelCancelInlineBot"), PorterDuff.Mode.MULTIPLY));
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        if (i6 >= 21) {
            this.cancelBotButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        }
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$17$ChatActivityEnterView(view);
            }
        });
        if (isInScheduleMode()) {
            this.sendButtonDrawable = activity.getResources().getDrawable(NUM).mutate();
            this.sendButtonInverseDrawable = activity.getResources().getDrawable(NUM).mutate();
            this.inactinveSendButtonDrawable = activity.getResources().getDrawable(NUM).mutate();
        } else {
            this.sendButtonDrawable = activity.getResources().getDrawable(NUM).mutate();
            this.sendButtonInverseDrawable = activity.getResources().getDrawable(NUM).mutate();
            this.inactinveSendButtonDrawable = activity.getResources().getDrawable(NUM).mutate();
        }
        AnonymousClass19 r22 = new View(activity2) {
            private float animationDuration;
            private float animationProgress;
            private int drawableColor;
            private long lastAnimationTime;
            private int prevColorType;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int i;
                int dp;
                float f;
                float dp2;
                int measuredWidth = (getMeasuredWidth() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth()) / 2;
                int measuredHeight = (getMeasuredHeight() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight()) / 2;
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    measuredHeight -= AndroidUtilities.dp(1.0f);
                } else {
                    measuredWidth += AndroidUtilities.dp(2.0f);
                }
                int i2 = 1;
                boolean z = ChatActivityEnterView.this.sendPopupWindow != null && ChatActivityEnterView.this.sendPopupWindow.isShowing();
                if (z) {
                    i = Theme.getColor("chat_messagePanelVoicePressed");
                } else {
                    i = Theme.getColor("chat_messagePanelSend");
                    i2 = 2;
                }
                if (i != this.drawableColor) {
                    this.lastAnimationTime = SystemClock.elapsedRealtime();
                    int i3 = this.prevColorType;
                    if (i3 == 0 || i3 == i2) {
                        this.animationProgress = 1.0f;
                    } else {
                        this.animationProgress = 0.0f;
                        if (z) {
                            this.animationDuration = 200.0f;
                        } else {
                            this.animationDuration = 120.0f;
                        }
                    }
                    this.prevColorType = i2;
                    this.drawableColor = i;
                    ChatActivityEnterView.this.sendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelSend"), PorterDuff.Mode.MULTIPLY));
                    int color = Theme.getColor("chat_messagePanelIcons");
                    ChatActivityEnterView.this.inactinveSendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Color.argb(180, Color.red(color), Color.green(color), Color.blue(color)), PorterDuff.Mode.MULTIPLY));
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
                }
                if (this.animationProgress < 1.0f) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    float f2 = this.animationProgress + (((float) (elapsedRealtime - this.lastAnimationTime)) / this.animationDuration);
                    this.animationProgress = f2;
                    if (f2 > 1.0f) {
                        this.animationProgress = 1.0f;
                    }
                    this.lastAnimationTime = elapsedRealtime;
                    invalidate();
                }
                if (!z) {
                    if (ChatActivityEnterView.this.slowModeTimer != Integer.MAX_VALUE || ChatActivityEnterView.this.isInScheduleMode()) {
                        ChatActivityEnterView.this.sendButtonDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                        ChatActivityEnterView.this.sendButtonDrawable.draw(canvas);
                    } else {
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.draw(canvas);
                    }
                }
                if (z || this.animationProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("chat_messagePanelSend"));
                    int dp3 = AndroidUtilities.dp(20.0f);
                    if (z) {
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(255);
                        float f3 = this.animationProgress;
                        if (f3 <= 0.25f) {
                            f = (float) dp3;
                            dp2 = ((float) AndroidUtilities.dp(2.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(f3 / 0.25f);
                        } else {
                            float f4 = f3 - 0.25f;
                            if (f4 <= 0.5f) {
                                f = (float) dp3;
                                dp2 = ((float) AndroidUtilities.dp(2.0f)) - (((float) AndroidUtilities.dp(3.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(f4 / 0.5f));
                            } else {
                                dp = (int) (((float) dp3) + ((float) (-AndroidUtilities.dp(1.0f))) + (((float) AndroidUtilities.dp(1.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation((f4 - 0.5f) / 0.25f)));
                                dp3 = dp;
                            }
                        }
                        dp = (int) (f + dp2);
                        dp3 = dp;
                    } else {
                        int i4 = (int) ((1.0f - this.animationProgress) * 255.0f);
                        Theme.dialogs_onlineCirclePaint.setAlpha(i4);
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(i4);
                    }
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) dp3, Theme.dialogs_onlineCirclePaint);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.draw(canvas);
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.sendButton = r22;
        r22.setVisibility(4);
        int color = Theme.getColor("chat_messagePanelSend");
        this.sendButton.setContentDescription(LocaleController.getString("Send", NUM));
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        if (i6 >= 21) {
            this.sendButton.setBackgroundDrawable(Theme.createSelectorDrawable(Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), 1));
        }
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$18$ChatActivityEnterView(view);
            }
        });
        this.sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return ChatActivityEnterView.this.onSendLongClick(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(activity2);
        this.slowModeButton = simpleTextView;
        simpleTextView.setTextSize(18);
        this.slowModeButton.setVisibility(4);
        this.slowModeButton.setSoundEffectsEnabled(false);
        this.slowModeButton.setScaleX(0.1f);
        this.slowModeButton.setScaleY(0.1f);
        this.slowModeButton.setAlpha(0.0f);
        this.slowModeButton.setPadding(0, 0, AndroidUtilities.dp(13.0f), 0);
        this.slowModeButton.setGravity(21);
        this.slowModeButton.setTextColor(Theme.getColor("chat_messagePanelIcons"));
        this.sendButtonContainer.addView(this.slowModeButton, LayoutHelper.createFrame(64, 48, 53));
        this.slowModeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$19$ChatActivityEnterView(view);
            }
        });
        this.slowModeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return ChatActivityEnterView.this.lambda$new$20$ChatActivityEnterView(view);
            }
        });
        AnonymousClass20 r23 = new ImageView(activity2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.expandStickersButton = r23;
        r23.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView9 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor("chat_messagePanelIcons"), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView9.setImageDrawable(animatedArrowDrawable);
        this.expandStickersButton.setVisibility(8);
        this.expandStickersButton.setScaleX(0.1f);
        this.expandStickersButton.setScaleY(0.1f);
        this.expandStickersButton.setAlpha(0.0f);
        if (i6 >= 21) {
            this.expandStickersButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        }
        this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        this.expandStickersButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$21$ChatActivityEnterView(view);
            }
        });
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
        FrameLayout frameLayout5 = new FrameLayout(activity2);
        this.doneButtonContainer = frameLayout5;
        frameLayout5.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.doneButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$22$ChatActivityEnterView(view);
            }
        });
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), Theme.getColor("chat_messagePanelSend"));
        Drawable mutate3 = activity.getResources().getDrawable(NUM).mutate();
        this.doneCheckDrawable = mutate3;
        mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(createCircleDrawable, mutate3, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView10 = new ImageView(activity2);
        this.doneButtonImage = imageView10;
        imageView10.setScaleType(ImageView.ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable2);
        this.doneButtonImage.setContentDescription(LocaleController.getString("Done", NUM));
        this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity2, 0);
        this.doneButtonProgress = contextProgressView;
        contextProgressView.setVisibility(4);
        this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = globalEmojiSettings.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = globalEmojiSettings.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ChatActivityEnterView(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper2 == null || !adjustPanLayoutHelper2.animationInProgress()) {
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
                this.searchingType = 0;
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    emojiView3.closeSearch(false);
                }
                this.messageEditText.requestFocus();
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ChatActivityEnterView.this.lambda$new$0$ChatActivityEnterView();
                    }
                }, 200);
                return;
            }
            openKeyboardInternal();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ChatActivityEnterView() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$ChatActivityEnterView() {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$ChatActivityEnterView(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$ChatActivityEnterView(View view) {
        boolean z = !this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(z);
        if (z) {
            this.botCommandsMenuContainer.show();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$ChatActivityEnterView(View view) {
        if (this.searchingType != 0) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            this.messageEditText.requestFocus();
        }
        if (this.botReplyMarkup != null) {
            if (!isPopupShowing() || this.currentPopupContentType != 1) {
                showPopup(1, 1);
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.remove("hidekeyboard_" + this.dialog_id).commit();
            } else {
                if (this.botButtonsMessageObject != null) {
                    SharedPreferences.Editor edit2 = MessagesController.getMainSettings(this.currentAccount).edit();
                    edit2.putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
                }
                openKeyboardInternal();
            }
        } else if (this.hasBotCommands) {
            setFieldText("/");
            this.messageEditText.requestFocus();
            openKeyboard();
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, false, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$ChatActivityEnterView(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper2 == null || !adjustPanLayoutHelper2.animationInProgress()) {
            this.delegate.didPressAttachButton();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$ChatActivityEnterView(View view) {
        AnimatorSet animatorSet = this.runningAnimationAudio;
        if (animatorSet == null || !animatorSet.isRunning()) {
            if (this.videoToSendMessageObject != null) {
                CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                this.delegate.needStartRecordVideo(2, true, 0);
            } else {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$8 */
    public /* synthetic */ void lambda$new$8$ChatActivityEnterView(View view) {
        if (this.audioToSend != null) {
            if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setIcon(1, true);
                MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
                return;
            }
            MediaController.getInstance().lambda$startAudioAgain$7(this.audioToSendMessageObject);
            this.playPauseDrawable.setIcon(0, true);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$16 */
    public /* synthetic */ boolean lambda$new$16$ChatActivityEnterView(View view, MotionEvent motionEvent) {
        TLRPC$Chat currentChat;
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() != 0) {
            float f = 1.0f;
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
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
                        updateRecordIntefrace(5);
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
                } else if (((double) ((((motionEvent.getX() + this.audioVideoButtonContainer.getX()) - this.startedDraggingX) / this.distCanMove) + 1.0f)) < 0.45d) {
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    } else {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace(5);
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
                    boolean z2 = this.hasRecordVideo;
                    if (!z2 || this.calledRecordRunnable) {
                        this.startedDraggingX = -1.0f;
                        if (!z2 || this.videoSendButton.getTag() == null) {
                            if (this.recordingAudioVideo && isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) $$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8.INSTANCE, (Runnable) $$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCneX_xvHnwg8.INSTANCE);
                            }
                            this.delegate.needStartRecordAudio(0);
                            MediaController instance = MediaController.getInstance();
                            if (!isInScheduleMode()) {
                                i = 1;
                            }
                            instance.stopRecording(i, true, 0);
                        } else {
                            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                            this.delegate.needStartRecordVideo(1, true, 0);
                        }
                        this.recordingAudioVideo = false;
                        $$Lambda$ChatActivityEnterView$W151PjnI1oawgMqW3qnpqIlxVr4 r0 = new Runnable() {
                            public final void run() {
                                ChatActivityEnterView.this.lambda$new$15$ChatActivityEnterView();
                            }
                        };
                        this.moveToSendStateRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 500);
                    }
                }
            } else if (motionEvent.getAction() == 2 && this.recordingAudioVideo) {
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
                    float f2 = (float) (measuredWidth * 0.35d);
                    this.distCanMove = f2;
                    if (f2 > ((float) AndroidUtilities.dp(140.0f))) {
                        this.distCanMove = (float) AndroidUtilities.dp(140.0f);
                    }
                }
                float x2 = x + this.audioVideoButtonContainer.getX();
                float f3 = this.startedDraggingX;
                float f4 = ((x2 - f3) / this.distCanMove) + 1.0f;
                if (f3 != -1.0f) {
                    if (f4 <= 1.0f) {
                        f = f4 < 0.0f ? 0.0f : f4;
                    }
                    this.slideText.setSlideX(f);
                    this.recordCircle.setSlideToCancelProgress(f);
                    f4 = f;
                }
                if (f4 == 0.0f) {
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    } else {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace(5);
                }
            }
        } else if (this.recordCircle.isSendButtonVisible()) {
            boolean z3 = this.hasRecordVideo;
            if (!z3 || this.calledRecordRunnable) {
                this.startedDraggingX = -1.0f;
                if (!z3 || this.videoSendButton.getTag() == null) {
                    if (this.recordingAudioVideo && isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) $$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk.INSTANCE, (Runnable) $$Lambda$ChatActivityEnterView$1_S0vOQvkoPajvvL58TRMSrmPU.INSTANCE);
                    }
                    MediaController instance2 = MediaController.getInstance();
                    if (!isInScheduleMode()) {
                        i = 1;
                    }
                    instance2.stopRecording(i, true, 0);
                    this.delegate.needStartRecordAudio(0);
                } else {
                    this.delegate.needStartRecordVideo(1, true, 0);
                }
                this.recordingAudioVideo = false;
                $$Lambda$ChatActivityEnterView$qNji1T63RTeQFMMaEGtyzpD5ocY r13 = new Runnable() {
                    public final void run() {
                        ChatActivityEnterView.this.lambda$new$12$ChatActivityEnterView();
                    }
                };
                this.moveToSendStateRunnable = r13;
                AndroidUtilities.runOnUIThread(r13, 200);
            }
            return false;
        } else {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && (currentChat = chatActivity.getCurrentChat()) != null && !ChatObject.canSendMedia(currentChat)) {
                this.delegate.needShowMediaBanHint();
                return false;
            } else if (this.hasRecordVideo) {
                this.calledRecordRunnable = false;
                this.recordAudioVideoRunnableStarted = true;
                AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150);
            } else {
                this.recordAudioVideoRunnable.run();
            }
        }
        view.onTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$12 */
    public /* synthetic */ void lambda$new$12$ChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        this.messageTransitionIsRunning = false;
        updateRecordIntefrace(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$15 */
    public /* synthetic */ void lambda$new$15$ChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        this.messageTransitionIsRunning = false;
        updateRecordIntefrace(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$17 */
    public /* synthetic */ void lambda$new$17$ChatActivityEnterView(View view) {
        String obj = this.messageEditText.getText().toString();
        int indexOf = obj.indexOf(32);
        if (indexOf == -1 || indexOf == obj.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(obj.substring(0, indexOf + 1));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$18 */
    public /* synthetic */ void lambda$new$18$ChatActivityEnterView(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if (animatorSet == null || !animatorSet.isRunning()) {
                sendMessage();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$19 */
    public /* synthetic */ void lambda$new$19$ChatActivityEnterView(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$20 */
    public /* synthetic */ boolean lambda$new$20$ChatActivityEnterView(View view) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$21 */
    public /* synthetic */ void lambda$new$21$ChatActivityEnterView(View view) {
        EmojiView emojiView2;
        if (this.expandStickersButton.getVisibility() != 0 || this.expandStickersButton.getAlpha() != 1.0f || this.waitingForKeyboardOpen) {
            return;
        }
        if (!this.keyboardVisible || !this.messageEditText.isFocused()) {
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$22 */
    public /* synthetic */ void lambda$new$22$ChatActivityEnterView(View view) {
        doneEditingMessage();
    }

    /* access modifiers changed from: private */
    public void checkBotMenu() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null) {
            botCommandsMenuView.setExpanded(TextUtils.isEmpty(this.messageEditText.getText()) && !this.keyboardVisible && !this.waitingForKeyboardOpen && !isPopupShowing(), true);
            beginDelayedTransition();
        }
    }

    private void startLockTransition() {
        AnimatorSet animatorSet = new AnimatorSet();
        performHapticFeedback(3, 2);
        RecordCircle recordCircle2 = this.recordCircle;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation});
        ofFloat.setStartDelay(100);
        ofFloat.setDuration(350);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.recordCircle, "snapAnimationProgress", new float[]{1.0f});
        ofFloat2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        ofFloat2.setDuration(250);
        SharedConfig.removeLockRecordAudioVideoHint();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat, ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f}).setDuration(200), ObjectAnimator.ofFloat(this.slideText, "cancelToProgress", new float[]{1.0f})});
        animatorSet.start();
    }

    public int getBackgroundTop() {
        int top = getTop();
        View view = this.topView;
        return (view == null || view.getVisibility() != 0) ? top : top + this.topView.getLayoutParams().height;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean z = view == this.topView || view == this.textFieldContainer;
        if (z) {
            canvas.save();
            if (view == this.textFieldContainer) {
                int dp = this.animatedTop + AndroidUtilities.dp(2.0f);
                View view2 = this.topView;
                if (view2 != null && view2.getVisibility() == 0) {
                    dp += this.topView.getHeight();
                }
                canvas.clipRect(0, dp, getMeasuredWidth(), getMeasuredHeight());
            } else {
                canvas.clipRect(0, this.animatedTop, getMeasuredWidth(), this.animatedTop + view.getLayoutParams().height + AndroidUtilities.dp(2.0f));
            }
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (z) {
            canvas.restore();
        }
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.animatedTop;
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            i = (int) (((float) i) + ((1.0f - this.topViewEnterProgress) * ((float) this.topView.getLayoutParams().height)));
        }
        int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + i;
        Theme.chat_composeShadowDrawable.setBounds(0, i, getMeasuredWidth(), intrinsicHeight);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getWidth(), (float) getHeight(), Theme.chat_composeBackgroundPaint);
    }

    /* access modifiers changed from: private */
    public boolean onSendLongClick(View view) {
        int i;
        if (this.parentFragment != null && !isInScheduleMode()) {
            this.parentFragment.getCurrentChat();
            TLRPC$User currentUser = this.parentFragment.getCurrentUser();
            if (this.sendPopupLayout == null) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
                this.sendPopupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || ChatActivityEnterView.this.sendPopupWindow == null || !ChatActivityEnterView.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        ChatActivityEnterView.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        ChatActivityEnterView.this.lambda$onSendLongClick$23$ChatActivityEnterView(keyEvent);
                    }
                });
                this.sendPopupLayout.setShownFromBotton(false);
                int i2 = 0;
                while (i2 < 2) {
                    if ((i2 != 0 || this.parentFragment.canScheduleMessage()) && (i2 != 1 || (!UserObject.isUserSelf(currentUser) && (this.slowModeTimer <= 0 || isInScheduleMode())))) {
                        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), i2 == 0, i2 == 1);
                        if (i2 != 0) {
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                        } else if (UserObject.isUserSelf(currentUser)) {
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                        } else {
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                        }
                        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
                        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener(i2) {
                            public final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                ChatActivityEnterView.this.lambda$onSendLongClick$24$ChatActivityEnterView(this.f$1, view);
                            }
                        });
                    }
                    i2++;
                }
                this.sendPopupLayout.setupRadialSelectors(Theme.getColor("dialogButtonSelector"));
                AnonymousClass22 r0 = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
                    public void dismiss() {
                        super.dismiss();
                        ChatActivityEnterView.this.sendButton.invalidate();
                    }
                };
                this.sendPopupWindow = r0;
                r0.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
                SharedConfig.removeScheduledOrNoSuoundHint();
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
                    i = this.location[1] + view.getMeasuredHeight();
                    this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
                    this.sendPopupWindow.dimBehind();
                    this.sendButton.invalidate();
                    view.performHapticFeedback(3, 2);
                }
            }
            i = (this.location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
            this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
            this.sendPopupWindow.dimBehind();
            this.sendButton.invalidate();
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSendLongClick$23 */
    public /* synthetic */ void lambda$onSendLongClick$23$ChatActivityEnterView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSendLongClick$24 */
    public /* synthetic */ void lambda$onSendLongClick$24$ChatActivityEnterView(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.sendMessageInternal(z, i);
                }
            });
        } else {
            sendMessageInternal(false, 0);
        }
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setRecordVideoButtonVisible(boolean r12, boolean r13) {
        /*
            r11 = this;
            android.widget.ImageView r0 = r11.videoSendButton
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r1 = 0
            r2 = 1
            if (r12 == 0) goto L_0x000e
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            goto L_0x000f
        L_0x000e:
            r3 = r1
        L_0x000f:
            r0.setTag(r3)
            android.animation.AnimatorSet r0 = r11.audioVideoButtonAnimation
            if (r0 == 0) goto L_0x001b
            r0.cancel()
            r11.audioVideoButtonAnimation = r1
        L_0x001b:
            r0 = 0
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r3 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x010c
            android.content.SharedPreferences r13 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            long r4 = r11.dialog_id
            int r5 = (int) r4
            r4 = 0
            if (r5 >= 0) goto L_0x004b
            org.telegram.messenger.AccountInstance r5 = r11.accountInstance
            org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
            long r6 = r11.dialog_id
            int r7 = (int) r6
            int r6 = -r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r6 == 0) goto L_0x004b
            boolean r5 = r5.megagroup
            if (r5 != 0) goto L_0x004b
            r5 = 1
            goto L_0x004c
        L_0x004b:
            r5 = 0
        L_0x004c:
            android.content.SharedPreferences$Editor r13 = r13.edit()
            if (r5 == 0) goto L_0x0055
            java.lang.String r5 = "currentModeVideoChannel"
            goto L_0x0057
        L_0x0055:
            java.lang.String r5 = "currentModeVideo"
        L_0x0057:
            android.content.SharedPreferences$Editor r13 = r13.putBoolean(r5, r12)
            r13.commit()
            android.animation.AnimatorSet r13 = new android.animation.AnimatorSet
            r13.<init>()
            r11.audioVideoButtonAnimation = r13
            r5 = 6
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.widget.ImageView r6 = r11.videoSendButton
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r2]
            if (r12 == 0) goto L_0x0073
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x0076
        L_0x0073:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0076:
            r8[r4] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r4] = r6
            android.widget.ImageView r6 = r11.videoSendButton
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r2]
            if (r12 == 0) goto L_0x0089
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x008c
        L_0x0089:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x008c:
            r8[r4] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r2] = r6
            r6 = 2
            android.widget.ImageView r7 = r11.videoSendButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00a0
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00a1
        L_0x00a0:
            r10 = 0
        L_0x00a1:
            r9[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r7
            r6 = 3
            android.widget.ImageView r7 = r11.audioSendButton
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00b6
            r10 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x00b8
        L_0x00b6:
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x00b8:
            r9[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r7
            r6 = 4
            android.widget.ImageView r7 = r11.audioSendButton
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00ca
            goto L_0x00cc
        L_0x00ca:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x00cc:
            r9[r4] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r1
            r1 = 5
            android.widget.ImageView r6 = r11.audioSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            if (r12 == 0) goto L_0x00de
            goto L_0x00e0
        L_0x00de:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x00e0:
            r2[r4] = r0
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r6, r7, r2)
            r5[r1] = r12
            r13.playTogether(r5)
            android.animation.AnimatorSet r12 = r11.audioVideoButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$23 r13 = new org.telegram.ui.Components.ChatActivityEnterView$23
            r13.<init>()
            r12.addListener(r13)
            android.animation.AnimatorSet r12 = r11.audioVideoButtonAnimation
            android.view.animation.DecelerateInterpolator r13 = new android.view.animation.DecelerateInterpolator
            r13.<init>()
            r12.setInterpolator(r13)
            android.animation.AnimatorSet r12 = r11.audioVideoButtonAnimation
            r0 = 150(0x96, double:7.4E-322)
            r12.setDuration(r0)
            android.animation.AnimatorSet r12 = r11.audioVideoButtonAnimation
            r12.start()
            goto L_0x0152
        L_0x010c:
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x0113
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0116
        L_0x0113:
            r2 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0116:
            r13.setScaleX(r2)
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x0120
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0123
        L_0x0120:
            r2 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0123:
            r13.setScaleY(r2)
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x012d
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x012e
        L_0x012d:
            r2 = 0
        L_0x012e:
            r13.setAlpha(r2)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x0139
            r2 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x013b
        L_0x0139:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x013b:
            r13.setScaleX(r2)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x0143
            goto L_0x0145
        L_0x0143:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0145:
            r13.setScaleY(r1)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x014d
            goto L_0x014f
        L_0x014d:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x014f:
            r13.setAlpha(r0)
        L_0x0152:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setRecordVideoButtonVisible(boolean, boolean):void");
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
        updateRecordIntefrace(2);
    }

    public void showContextProgress(boolean z) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (z) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setCaption(String str) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setCaption(str);
            checkSendButton(true);
        }
    }

    public void setSlowModeTimer(int i) {
        this.slowModeTimer = i;
        updateSlowModeText();
    }

    public CharSequence getSlowModeTimer() {
        if (this.slowModeTimer > 0) {
            return this.slowModeButton.getText();
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0080 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSlowModeText() {
        /*
            r8 = this;
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getCurrentTime()
            java.lang.Runnable r1 = r8.updateSlowModeRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
            r1 = 0
            r8.updateSlowModeRunnable = r1
            org.telegram.tgnet.TLRPC$ChatFull r1 = r8.info
            r2 = 2147483646(0x7ffffffe, float:NaN)
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0063
            int r5 = r1.slowmode_seconds
            if (r5 == 0) goto L_0x0063
            int r1 = r1.slowmode_next_send_date
            if (r1 > r0) goto L_0x0063
            int r1 = r8.currentAccount
            org.telegram.messenger.SendMessagesHelper r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1)
            long r5 = r8.dialog_id
            boolean r1 = r1.isUploadingMessageIdDialog(r5)
            if (r1 != 0) goto L_0x003f
            int r5 = r8.currentAccount
            org.telegram.messenger.SendMessagesHelper r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5)
            long r6 = r8.dialog_id
            boolean r5 = r5.isSendingMessageIdDialog(r6)
            if (r5 == 0) goto L_0x0063
        L_0x003f:
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r5 = r8.info
            int r5 = r5.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            boolean r0 = org.telegram.messenger.ChatObject.hasAdminRights(r0)
            if (r0 != 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$ChatFull r0 = r8.info
            int r0 = r0.slowmode_seconds
            if (r1 == 0) goto L_0x0060
            r2 = 2147483647(0x7fffffff, float:NaN)
        L_0x0060:
            r8.slowModeTimer = r2
            goto L_0x007c
        L_0x0063:
            int r1 = r8.slowModeTimer
            if (r1 < r2) goto L_0x007a
            org.telegram.tgnet.TLRPC$ChatFull r0 = r8.info
            if (r0 == 0) goto L_0x0078
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r1 = r8.info
            int r1 = r1.id
            r0.loadFullChat(r1, r4, r3)
        L_0x0078:
            r0 = 0
            goto L_0x007c
        L_0x007a:
            int r0 = r1 - r0
        L_0x007c:
            int r1 = r8.slowModeTimer
            if (r1 == 0) goto L_0x00a9
            if (r0 <= 0) goto L_0x00a9
            org.telegram.ui.ActionBar.SimpleTextView r1 = r8.slowModeButton
            int r0 = java.lang.Math.max(r3, r0)
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.formatDurationNoHours(r0, r4)
            r1.setText(r0)
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x009c
            org.telegram.ui.ActionBar.SimpleTextView r1 = r8.slowModeButton
            java.lang.CharSequence r2 = r1.getText()
            r0.onUpdateSlowModeButton(r1, r4, r2)
        L_0x009c:
            org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$PY_fjfc_MSm-tnCo-atTkeCzYdY r0 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$PY_fjfc_MSm-tnCo-atTkeCzYdY
            r0.<init>()
            r8.updateSlowModeRunnable = r0
            r1 = 100
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            goto L_0x00ab
        L_0x00a9:
            r8.slowModeTimer = r4
        L_0x00ab:
            boolean r0 = r8.isInScheduleMode()
            if (r0 != 0) goto L_0x00b4
            r8.checkSendButton(r3)
        L_0x00b4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateSlowModeText():void");
    }

    public void addTopView(View view, View view2, int i) {
        if (view != null) {
            this.topLineView = view2;
            view2.setVisibility(8);
            this.topLineView.setAlpha(0.0f);
            addView(this.topLineView, LayoutHelper.createFrame(-1, 1.0f, 51, 0.0f, (float) (i + 1), 0.0f, 0.0f));
            this.topView = view;
            view.setVisibility(8);
            this.topViewEnterProgress = 0.0f;
            float f = (float) i;
            this.topView.setTranslationY(f);
            addView(this.topView, 0, LayoutHelper.createFrame(-1, f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
            this.needShowTopView = false;
        }
    }

    public void setForceShowSendButton(boolean z, boolean z2) {
        this.forceShowSendButton = z;
        checkSendButton(z2);
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2) {
        setAllowStickersAndGifs(z, z2, false);
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2, boolean z3) {
        if (!((this.allowStickers == z && this.allowGifs == z2) || this.emojiView == null)) {
            if (!SharedConfig.smoothKeyboard) {
                if (this.emojiViewVisible) {
                    hidePopup(false);
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            } else if (!this.emojiViewVisible || z3) {
                if (z3) {
                    openKeyboardInternal();
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            } else {
                this.removeEmojiViewAfterAnimation = true;
                hidePopup(false);
            }
        }
        this.allowStickers = z;
        this.allowGifs = z2;
        setEmojiButtonImage(false, !this.isPaused);
    }

    public void addEmojiToRecent(String str) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(str);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$25 */
    public /* synthetic */ void lambda$new$25$ChatActivityEnterView(ValueAnimator valueAnimator) {
        if (this.topView != null) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.topViewEnterProgress = floatValue;
            View view = this.topView;
            view.setTranslationY(((float) this.animatedTop) + ((1.0f - floatValue) * ((float) view.getLayoutParams().height)));
            this.topLineView.setAlpha(floatValue);
            this.topLineView.setTranslationY((float) this.animatedTop);
        }
    }

    public void showTopView(boolean z, boolean z2) {
        showTopView(z, z2, false);
    }

    private void showTopView(boolean z, boolean z2, boolean z3) {
        if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
            boolean z4 = this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || z2) && (this.botReplyMarkup == null || this.editingMessageObject != null);
            if (z3 || !z || !z4 || this.keyboardVisible || isPopupShowing()) {
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
                    if (z) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.topViewEnterProgress, 1.0f});
                        this.currentTopViewAnimation = ofFloat;
                        ofFloat.addUpdateListener(this.topViewUpdateListener);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ValueAnimator valueAnimator = ChatActivityEnterView.this.currentTopViewAnimation;
                                if (valueAnimator != null && valueAnimator.equals(animator)) {
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                            }
                        });
                        this.currentTopViewAnimation.setDuration(250);
                        this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.currentTopViewAnimation.start();
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    } else {
                        this.topViewEnterProgress = 1.0f;
                        this.topView.setTranslationY(0.0f);
                        this.topLineView.setAlpha(1.0f);
                    }
                    if (z4) {
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
            $$Lambda$ChatActivityEnterView$tN0OEYP6os5Brq0S4UeKZiKI_0 r5 = new Runnable() {
                public final void run() {
                    ChatActivityEnterView.this.lambda$showTopView$26$ChatActivityEnterView();
                }
            };
            this.showTopViewRunnable = r5;
            AndroidUtilities.runOnUIThread(r5, 200);
        } else if (this.recordedAudioPanel.getVisibility() == 0) {
        } else {
            if (!this.forceShowSendButton || z2) {
                openKeyboard();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showTopView$26 */
    public /* synthetic */ void lambda$showTopView$26$ChatActivityEnterView() {
        showTopView(true, false, true);
        this.showTopViewRunnable = null;
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(final boolean z, boolean z2) {
        AnimatorSet animatorSet = this.doneButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z2) {
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
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
                        if (!z) {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
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

    public void hideTopView(boolean z) {
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
                if (z) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.topViewEnterProgress, 0.0f});
                    this.currentTopViewAnimation = ofFloat;
                    ofFloat.addUpdateListener(this.topViewUpdateListener);
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ValueAnimator valueAnimator = ChatActivityEnterView.this.currentTopViewAnimation;
                            if (valueAnimator != null && valueAnimator.equals(animator)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.topLineView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            ValueAnimator valueAnimator = ChatActivityEnterView.this.currentTopViewAnimation;
                            if (valueAnimator != null && valueAnimator.equals(animator)) {
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(220);
                    this.currentTopViewAnimation.setStartDelay(50);
                    this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
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

    public void onAdjustPanTransitionStart(boolean z) {
        Runnable runnable;
        if (z && (runnable = this.showTopViewRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showTopViewRunnable.run();
        }
        Runnable runnable2 = this.setTextFieldRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.setTextFieldRunnable.run();
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onWindowSizeChanged(height);
        }
        if (this.topView == null) {
            return;
        }
        if (height < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
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
    public void resizeForTopView(boolean z) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (z ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        setMinimumHeight(AndroidUtilities.dp(51.0f) + (z ? this.topView.getLayoutParams().height : 0));
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
    }

    public void checkChannelRights() {
        TLRPC$Chat currentChat;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && (currentChat = chatActivity.getCurrentChat()) != null) {
            this.audioVideoButtonContainer.setAlpha(ChatObject.canSendMedia(currentChat) ? 1.0f : 0.5f);
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.setStickersBanned(!ChatObject.canSendStickers(currentChat), currentChat.id);
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

    public void onPause() {
        this.isPaused = true;
        if (this.keyboardVisible) {
            this.showKeyboardOnResume = true;
        }
        $$Lambda$ChatActivityEnterView$wClVXHGsGSCr2uoQAlJwjlikDkQ r0 = new Runnable() {
            public final void run() {
                ChatActivityEnterView.this.lambda$onPause$27$ChatActivityEnterView();
            }
        };
        this.hideKeyboardRunnable = r0;
        AndroidUtilities.runOnUIThread(r0, 500);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPause$27 */
    public /* synthetic */ void lambda$onPause$27$ChatActivityEnterView() {
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
        getVisibility();
        if (this.showKeyboardOnResume) {
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

    public void setVisibility(int i) {
        super.setVisibility(i);
        this.messageEditText.setEnabled(i == 0);
    }

    public void setDialogId(long j, int i) {
        this.dialog_id = j;
        int i2 = this.currentAccount;
        if (i2 != i) {
            NotificationCenter.getInstance(i2).onAnimationFinish(this.notificationsIndex);
            NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
            int i3 = NotificationCenter.recordStarted;
            instance.removeObserver(this, i3);
            NotificationCenter instance2 = NotificationCenter.getInstance(this.currentAccount);
            int i4 = NotificationCenter.recordStartError;
            instance2.removeObserver(this, i4);
            NotificationCenter instance3 = NotificationCenter.getInstance(this.currentAccount);
            int i5 = NotificationCenter.recordStopped;
            instance3.removeObserver(this, i5);
            NotificationCenter instance4 = NotificationCenter.getInstance(this.currentAccount);
            int i6 = NotificationCenter.recordProgressChanged;
            instance4.removeObserver(this, i6);
            NotificationCenter instance5 = NotificationCenter.getInstance(this.currentAccount);
            int i7 = NotificationCenter.closeChats;
            instance5.removeObserver(this, i7);
            NotificationCenter instance6 = NotificationCenter.getInstance(this.currentAccount);
            int i8 = NotificationCenter.audioDidSent;
            instance6.removeObserver(this, i8);
            NotificationCenter instance7 = NotificationCenter.getInstance(this.currentAccount);
            int i9 = NotificationCenter.audioRouteChanged;
            instance7.removeObserver(this, i9);
            NotificationCenter instance8 = NotificationCenter.getInstance(this.currentAccount);
            int i10 = NotificationCenter.messagePlayingDidReset;
            instance8.removeObserver(this, i10);
            NotificationCenter instance9 = NotificationCenter.getInstance(this.currentAccount);
            int i11 = NotificationCenter.messagePlayingProgressDidChanged;
            instance9.removeObserver(this, i11);
            NotificationCenter instance10 = NotificationCenter.getInstance(this.currentAccount);
            int i12 = NotificationCenter.featuredStickersDidLoad;
            instance10.removeObserver(this, i12);
            NotificationCenter instance11 = NotificationCenter.getInstance(this.currentAccount);
            int i13 = NotificationCenter.messageReceivedByServer;
            instance11.removeObserver(this, i13);
            NotificationCenter instance12 = NotificationCenter.getInstance(this.currentAccount);
            int i14 = NotificationCenter.sendingMessagesChanged;
            instance12.removeObserver(this, i14);
            this.currentAccount = i;
            this.accountInstance = AccountInstance.getInstance(i);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i3);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i4);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i5);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i6);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i7);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i8);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i9);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i10);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i11);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i12);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i13);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i14);
        }
        updateScheduleButton(false);
        checkRoundVideo();
        updateFieldHint(false);
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setChatInfo(tLRPC$ChatFull);
        }
        setSlowModeTimer(tLRPC$ChatFull.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        if (!this.hasRecordVideo) {
            if (this.attachLayout == null || Build.VERSION.SDK_INT < 18) {
                this.hasRecordVideo = false;
                setRecordVideoButtonVisible(false, false);
                return;
            }
            long j = this.dialog_id;
            int i = (int) j;
            int i2 = (int) (j >> 32);
            boolean z = true;
            if (i != 0 || i2 == 0) {
                this.hasRecordVideo = true;
            } else {
                this.accountInstance.getMessagesController().getEncryptedChat(Integer.valueOf(i2));
                this.hasRecordVideo = true;
            }
            if (((int) this.dialog_id) < 0) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    z = false;
                }
                if (z && !chat.creator && ((tLRPC$TL_chatAdminRights = chat.admin_rights) == null || !tLRPC$TL_chatAdminRights.post_messages)) {
                    this.hasRecordVideo = false;
                }
            } else {
                z = false;
            }
            if (!SharedConfig.inappCamera) {
                this.hasRecordVideo = false;
            }
            if (this.hasRecordVideo) {
                if (SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(z ? "currentModeVideoChannel" : "currentModeVideo", z), false);
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

    /* access modifiers changed from: private */
    public void updateFieldHint(boolean z) {
        boolean z2;
        MessageObject messageObject;
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup;
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup2;
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && (tLRPC$ReplyMarkup2 = messageObject2.messageOwner.reply_markup) != null && !TextUtils.isEmpty(tLRPC$ReplyMarkup2.placeholder)) {
            this.messageEditText.setHintText(this.replyingMessageObject.messageOwner.reply_markup.placeholder, z);
        } else if (this.editingMessageObject != null) {
            this.messageEditText.setHintText(this.editingCaption ? LocaleController.getString("Caption", NUM) : LocaleController.getString("TypeMessage", NUM));
        } else if (!this.botKeyboardViewVisible || (messageObject = this.botButtonsMessageObject) == null || (tLRPC$ReplyMarkup = messageObject.messageOwner.reply_markup) == null || TextUtils.isEmpty(tLRPC$ReplyMarkup.placeholder)) {
            boolean z3 = false;
            if (((int) this.dialog_id) < 0) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    z3 = true;
                }
                boolean z4 = z3;
                z3 = ChatObject.shouldSendAnonymously(chat);
                z2 = z4;
            } else {
                z2 = false;
            }
            if (z3) {
                this.messageEditText.setHintText(LocaleController.getString("SendAnonymously", NUM));
                return;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isThreadChat()) {
                if (!z2) {
                    this.messageEditText.setHintText(LocaleController.getString("TypeMessage", NUM));
                } else if (this.silent) {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", NUM), z);
                } else {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", NUM), z);
                }
            } else if (this.parentFragment.isReplyChatComment()) {
                this.messageEditText.setHintText(LocaleController.getString("Comment", NUM));
            } else {
                this.messageEditText.setHintText(LocaleController.getString("Reply", NUM));
            }
        } else {
            this.messageEditText.setHintText(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder, z);
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

    public void setWebPage(TLRPC$WebPage tLRPC$WebPage, boolean z) {
        this.messageWebPage = tLRPC$WebPage;
        this.messageWebPageSearch = z;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel(boolean z) {
        AnimatorSet animatorSet;
        AnimatorSet animatorSet2 = this.recordPannelAnimation;
        if (animatorSet2 == null || !animatorSet2.isRunning()) {
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
            if (z) {
                this.attachButton.setAlpha(0.0f);
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.attachButton.setScaleX(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.attachButton.setScaleY(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
                if (botCommandsMenuView != null) {
                    botCommandsMenuView.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    this.recordPannelAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{1.0f})});
                }
                this.recordPannelAnimation.setDuration(150);
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                });
            } else {
                this.recordDeleteImageView.playAnimation();
                AnimatorSet animatorSet4 = new AnimatorSet();
                if (isInVideoMode()) {
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                } else {
                    this.messageEditText.setAlpha(1.0f);
                    this.messageEditText.setTranslationX(0.0f);
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))})});
                }
                animatorSet4.setDuration(200);
                ImageView imageView2 = this.attachButton;
                if (imageView2 != null) {
                    imageView2.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                    AnimatorSet animatorSet5 = new AnimatorSet();
                    animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f})});
                    animatorSet5.setDuration(150);
                    animatorSet = animatorSet5;
                } else {
                    animatorSet = null;
                }
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet animatorSet6 = new AnimatorSet();
                animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f})});
                BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                if (botCommandsMenuView2 != null) {
                    botCommandsMenuView2.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{1.0f})});
                }
                animatorSet6.setDuration(150);
                animatorSet6.setStartDelay(600);
                AnimatorSet animatorSet7 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet7;
                if (animatorSet != null) {
                    animatorSet7.playTogether(new Animator[]{animatorSet4, animatorSet, animatorSet6});
                } else {
                    animatorSet7.playTogether(new Animator[]{animatorSet4, animatorSet6});
                }
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
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
                    }
                });
            }
            this.recordPannelAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public void sendMessage() {
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.sendMessageInternal(z, i);
                }
            });
        } else {
            sendMessageInternal(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessageInternal(boolean z, int i) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC$Chat currentChat;
        boolean z2 = z;
        int i2 = i;
        if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                TLRPC$Chat currentChat2 = chatActivity.getCurrentChat();
                if (this.parentFragment.getCurrentUser() != null || ((ChatObject.isChannel(currentChat2) && currentChat2.megagroup) || !ChatObject.isChannel(currentChat2))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + this.dialog_id, !z2).commit();
                }
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                if (this.searchingType != 0) {
                    this.emojiView.closeSearch(false);
                    this.emojiView.hideSearchKeyboard();
                }
            }
            if (this.videoToSendMessageObject != null) {
                this.delegate.needStartRecordVideo(4, z2, i2);
                hideRecordedAudioPanel(true);
                checkSendButton(true);
            } else if (this.audioToSend != null) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, (VideoEditedInfo) null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, getThreadMessage(), (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    chatActivityEnterViewDelegate2.onMessageSend((CharSequence) null, z, i);
                }
                hideRecordedAudioPanel(true);
                checkSendButton(true);
            } else {
                int i3 = i2;
                boolean z3 = z2;
                Editable text = this.messageEditText.getText();
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null && (currentChat = chatActivity2.getCurrentChat()) != null && currentChat.slowmode_enabled && !ChatObject.hasAdminRights(currentChat)) {
                    if (text.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendErrorTooLong", NUM));
                        return;
                    } else if (this.forceShowSendButton && text.length() > 0) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
                        return;
                    }
                }
                if (processSendingText(text, z3, i3)) {
                    this.messageEditText.setText("");
                    this.lastTypingTimeSend = 0;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                    if (chatActivityEnterViewDelegate3 != null) {
                        chatActivityEnterViewDelegate3.onMessageSend(text, z3, i3);
                    }
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

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            if (this.currentLimit - this.codePointCount < 0) {
                AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
                Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                    return;
                }
                return;
            }
            CharSequence[] charSequenceArr = {AndroidUtilities.getTrimmedString(this.messageEditText.getText())};
            ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
            if (!TextUtils.equals(charSequenceArr[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || ((entities == null && !this.editingMessageObject.messageOwner.entities.isEmpty()) || (this.editingMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage)))) {
                MessageObject messageObject = this.editingMessageObject;
                messageObject.editingMessage = charSequenceArr[0];
                messageObject.editingMessageEntities = entities;
                messageObject.editingMessageSearchWebPage = this.messageWebPageSearch;
                SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, (HashMap<String, String>) null, false, (Object) null);
            }
            setEditingMessageObject((MessageObject) null, false);
        }
    }

    public boolean processSendingText(CharSequence charSequence, boolean z, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        MessageObject.SendAnimationData sendAnimationData;
        ChatActivity chatActivity;
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
        boolean supportsSendingNewEntities = supportsSendingNewEntities();
        int i6 = this.accountInstance.getMessagesController().maxMessageLength;
        char c = 0;
        if (trimmedString.length() == 0) {
            return false;
        }
        if (!(this.delegate == null || (chatActivity = this.parentFragment) == null)) {
            if ((i != 0) == chatActivity.isInScheduleMode()) {
                this.delegate.prepareMessageSending();
            }
        }
        int i7 = 0;
        while (true) {
            int i8 = i7 + i6;
            if (trimmedString.length() > i8) {
                int i9 = i8 - 1;
                int i10 = 0;
                i4 = -1;
                i3 = -1;
                i2 = -1;
                while (true) {
                    if (i9 > i7 && i10 < 300) {
                        char charAt = trimmedString.charAt(i9);
                        char charAt2 = i9 > 0 ? trimmedString.charAt(i9 - 1) : ' ';
                        if (charAt == 10 && charAt2 == 10) {
                            i5 = i9;
                            break;
                        }
                        if (charAt == 10) {
                            i2 = i9;
                        } else if (i4 < 0 && Character.isWhitespace(charAt) && charAt2 == '.') {
                            i4 = i9;
                        } else if (i3 < 0 && Character.isWhitespace(charAt)) {
                            i3 = i9;
                        }
                        i9--;
                        i10++;
                    } else {
                        i5 = -1;
                    }
                }
            } else {
                i5 = -1;
                i4 = -1;
                i3 = -1;
                i2 = -1;
            }
            int min = i5 > 0 ? i5 : i2 > 0 ? i2 : i4 > 0 ? i4 : i3 > 0 ? i3 : Math.min(i8, trimmedString.length());
            CharSequence[] charSequenceArr = new CharSequence[1];
            charSequenceArr[c] = AndroidUtilities.getTrimmedString(trimmedString.subSequence(i7, min));
            ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities);
            if (charSequenceArr[c].length() < 20) {
                sendAnimationData = new MessageObject.SendAnimationData();
                float dp = (float) AndroidUtilities.dp(22.0f);
                sendAnimationData.height = dp;
                sendAnimationData.width = dp;
                this.messageEditText.getLocationInWindow(this.location);
                sendAnimationData.x = (float) (this.location[c] + AndroidUtilities.dp(11.0f));
                sendAnimationData.y = (float) (this.location[1] + AndroidUtilities.dp(19.0f));
            } else {
                sendAnimationData = null;
            }
            int i11 = min;
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[c].toString(), this.dialog_id, this.replyingMessageObject, getThreadMessage(), this.messageWebPage, this.messageWebPageSearch, entities, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, sendAnimationData);
            i7 = i11 + 1;
            if (i11 == trimmedString.length()) {
                return true;
            }
            c = 0;
        }
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        TLRPC$EncryptedChat currentEncryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 101;
    }

    /* access modifiers changed from: private */
    public void checkSendButton(boolean z) {
        int i;
        int i2;
        if (this.editingMessageObject == null && !this.recordingAudioVideo) {
            boolean z2 = this.isPaused ? false : z;
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
            int i3 = this.slowModeTimer;
            if (i3 <= 0 || i3 == Integer.MAX_VALUE || isInScheduleMode()) {
                if (trimmedString.length() > 0 || this.forceShowSendButton || this.audioToSend != null || this.videoToSendMessageObject != null || (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode())) {
                    final String caption = this.messageEditText.getCaption();
                    boolean z3 = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                    boolean z4 = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                    if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                        i = Theme.getColor("chat_messagePanelSend");
                    } else {
                        i = Theme.getColor("chat_messagePanelIcons");
                    }
                    Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(i), Color.green(i), Color.blue(i)), true);
                    if (this.audioVideoButtonContainer.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0 && !z3 && !z4) {
                        return;
                    }
                    if (!z2) {
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
                            i2 = 8;
                            this.expandStickersButton.setVisibility(8);
                        } else {
                            i2 = 8;
                        }
                        LinearLayout linearLayout = this.attachLayout;
                        if (linearLayout != null) {
                            linearLayout.setVisibility(i2);
                            if (this.delegate != null && getVisibility() == 0) {
                                this.delegate.onAttachButtonHidden();
                            }
                            updateFieldRight(0);
                        }
                        this.scheduleButtonHidden = true;
                        if (this.scheduledButton != null) {
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                            if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                                this.scheduledButton.setVisibility(8);
                                this.scheduledButton.setTag((Object) null);
                            }
                            this.scheduledButton.setAlpha(0.0f);
                            this.scheduledButton.setScaleX(0.0f);
                            this.scheduledButton.setScaleY(1.0f);
                            ImageView imageView = this.scheduledButton;
                            ImageView imageView2 = this.botButton;
                            imageView.setTranslationX((float) AndroidUtilities.dp((imageView2 == null || imageView2.getVisibility() == 8) ? 48.0f : 96.0f));
                        }
                    } else if (this.runningAnimationType != 1 || this.messageEditText.getCaption() != null) {
                        if (this.runningAnimationType != 3 || caption == null) {
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
                            if (this.attachLayout != null) {
                                this.runningAnimation2 = new AnimatorSet();
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                                final boolean z5 = chatActivityEnterViewDelegate2 != null && chatActivityEnterViewDelegate2.hasScheduledMessages();
                                this.scheduleButtonHidden = true;
                                ImageView imageView3 = this.scheduledButton;
                                if (imageView3 != null) {
                                    imageView3.setScaleY(1.0f);
                                    if (z5) {
                                        this.scheduledButton.setTag((Object) null);
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f}));
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{0.0f}));
                                        ImageView imageView4 = this.scheduledButton;
                                        Property property = View.TRANSLATION_X;
                                        float[] fArr = new float[1];
                                        ImageView imageView5 = this.botButton;
                                        fArr[0] = (float) AndroidUtilities.dp((imageView5 == null || imageView5.getVisibility() == 8) ? 48.0f : 96.0f);
                                        arrayList.add(ObjectAnimator.ofFloat(imageView4, property, fArr));
                                    } else {
                                        this.scheduledButton.setAlpha(0.0f);
                                        this.scheduledButton.setScaleX(0.0f);
                                        ImageView imageView6 = this.scheduledButton;
                                        ImageView imageView7 = this.botButton;
                                        imageView6.setTranslationX((float) AndroidUtilities.dp((imageView7 == null || imageView7.getVisibility() == 8) ? 48.0f : 96.0f));
                                    }
                                }
                                this.runningAnimation2.playTogether(arrayList);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                            if (z5) {
                                                ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                            }
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                            ArrayList arrayList2 = new ArrayList();
                            if (this.audioVideoButtonContainer.getVisibility() == 0) {
                                arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                            }
                            if (this.expandStickersButton.getVisibility() == 0) {
                                arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                            }
                            if (this.slowModeButton.getVisibility() == 0) {
                                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                            }
                            if (z3) {
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                            } else if (z4) {
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                            }
                            if (caption != null) {
                                this.runningAnimationType = 3;
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{1.0f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{1.0f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{1.0f}));
                                this.cancelBotButton.setVisibility(0);
                            } else {
                                this.runningAnimationType = 1;
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{1.0f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{1.0f}));
                                arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{1.0f}));
                                this.sendButton.setVisibility(0);
                            }
                            this.runningAnimation.playTogether(arrayList2);
                            this.runningAnimation.setDuration(150);
                            this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
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

                                public void onAnimationCancel(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                        }
                    }
                } else if (this.emojiView == null || !this.emojiViewVisible || ((!this.stickersTabOpen && (!this.emojiTabOpen || this.searchingType != 2)) || AndroidUtilities.isInMultiwindow)) {
                    if (this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0 && this.expandStickersButton.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0) {
                        return;
                    }
                    if (!z2) {
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
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                            if (chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages()) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                            }
                            this.scheduledButton.setAlpha(1.0f);
                            this.scheduledButton.setScaleX(1.0f);
                            this.scheduledButton.setScaleY(1.0f);
                            this.scheduledButton.setTranslationX(0.0f);
                        }
                    } else if (this.runningAnimationType != 2) {
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
                        if (linearLayout2 != null) {
                            if (linearLayout2.getVisibility() != 0) {
                                this.attachLayout.setVisibility(0);
                                this.attachLayout.setAlpha(0.0f);
                                this.attachLayout.setScaleX(0.0f);
                            }
                            this.runningAnimation2 = new AnimatorSet();
                            ArrayList arrayList3 = new ArrayList();
                            arrayList3.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                            arrayList3.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                            boolean z6 = chatActivityEnterViewDelegate4 != null && chatActivityEnterViewDelegate4.hasScheduledMessages();
                            this.scheduleButtonHidden = false;
                            ImageView imageView8 = this.scheduledButton;
                            if (imageView8 != null) {
                                if (z6) {
                                    imageView8.setVisibility(0);
                                    this.scheduledButton.setTag(1);
                                    this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                    arrayList3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                    arrayList3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                    arrayList3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                } else {
                                    imageView8.setAlpha(1.0f);
                                    this.scheduledButton.setScaleX(1.0f);
                                    this.scheduledButton.setScaleY(1.0f);
                                    this.scheduledButton.setTranslationX(0.0f);
                                }
                            }
                            this.runningAnimation2.playTogether(arrayList3);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                        ArrayList arrayList4 = new ArrayList();
                        arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f}));
                        arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f}));
                        arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}));
                        if (this.cancelBotButton.getVisibility() == 0) {
                            arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                        } else if (this.expandStickersButton.getVisibility() == 0) {
                            arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                        } else if (this.slowModeButton.getVisibility() == 0) {
                            arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                        } else {
                            arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                        }
                        this.runningAnimation.playTogether(arrayList4);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                    if (ChatActivityEnterView.this.audioVideoButtonContainer != null) {
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                    }
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                } else if (!z2) {
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
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate5 = this.delegate;
                        if (chatActivityEnterViewDelegate5 != null && chatActivityEnterViewDelegate5.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                        }
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        this.scheduledButton.setTranslationX(0.0f);
                    }
                } else if (this.runningAnimationType != 4) {
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
                    LinearLayout linearLayout3 = this.attachLayout;
                    if (linearLayout3 != null && this.recordInterfaceState == 0) {
                        linearLayout3.setVisibility(0);
                        this.runningAnimation2 = new AnimatorSet();
                        ArrayList arrayList5 = new ArrayList();
                        arrayList5.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                        arrayList5.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate6 = this.delegate;
                        boolean z7 = chatActivityEnterViewDelegate6 != null && chatActivityEnterViewDelegate6.hasScheduledMessages();
                        this.scheduleButtonHidden = false;
                        ImageView imageView9 = this.scheduledButton;
                        if (imageView9 != null) {
                            imageView9.setScaleY(1.0f);
                            if (z7) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                            } else {
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                                this.scheduledButton.setTranslationX(0.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(arrayList5);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                    ArrayList arrayList6 = new ArrayList();
                    arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{1.0f}));
                    arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{1.0f}));
                    arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{1.0f}));
                    if (this.cancelBotButton.getVisibility() == 0) {
                        arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                    } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                    } else if (this.slowModeButton.getVisibility() == 0) {
                        arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                    } else {
                        arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    this.runningAnimation.playTogether(arrayList6);
                    this.runningAnimation.setDuration(250);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            } else if (this.slowModeButton.getVisibility() == 0) {
            } else {
                if (!z2) {
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
                        this.expandStickersButton.setVisibility(8);
                    }
                    LinearLayout linearLayout4 = this.attachLayout;
                    if (linearLayout4 != null) {
                        linearLayout4.setVisibility(8);
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
                        ArrayList arrayList7 = new ArrayList();
                        arrayList7.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                        arrayList7.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                        this.scheduleButtonHidden = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate8 = this.delegate;
                        boolean z8 = chatActivityEnterViewDelegate8 != null && chatActivityEnterViewDelegate8.hasScheduledMessages();
                        ImageView imageView12 = this.scheduledButton;
                        if (imageView12 != null) {
                            imageView12.setScaleY(1.0f);
                            if (z8) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                ImageView imageView13 = this.scheduledButton;
                                Property property2 = View.TRANSLATION_X;
                                float[] fArr2 = new float[1];
                                ImageView imageView14 = this.botButton;
                                fArr2[0] = (float) AndroidUtilities.dp((imageView14 == null || imageView14.getVisibility() != 0) ? 48.0f : 96.0f);
                                arrayList7.add(ObjectAnimator.ofFloat(imageView13, property2, fArr2));
                                arrayList7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                arrayList7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                            } else {
                                ImageView imageView15 = this.scheduledButton;
                                ImageView imageView16 = this.botButton;
                                imageView15.setTranslationX((float) AndroidUtilities.dp((imageView16 == null || imageView16.getVisibility() != 0) ? 48.0f : 96.0f));
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(arrayList7);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                    ArrayList arrayList8 = new ArrayList();
                    if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.sendButton.getVisibility() == 0) {
                        arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.cancelBotButton.getVisibility() == 0) {
                        arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                    }
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{1.0f}));
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{1.0f}));
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{1.0f}));
                    setSlowModeButtonVisible(true);
                    this.runningAnimation.playTogether(arrayList8);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
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
    public void setSlowModeButtonVisible(boolean z) {
        this.slowModeButton.setVisibility(z ? 0 : 8);
        int dp = z ? AndroidUtilities.dp(16.0f) : 0;
        if (this.messageEditText.getPaddingRight() != dp) {
            this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), dp, AndroidUtilities.dp(12.0f));
        }
    }

    private void updateFieldRight(int i) {
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
            int i2 = layoutParams.rightMargin;
            if (i == 1) {
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
            } else if (i != 2) {
                ImageView imageView9 = this.scheduledButton;
                if (imageView9 == null || imageView9.getTag() == null) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            } else if (i2 != AndroidUtilities.dp(2.0f)) {
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
            if (i2 != layoutParams.rightMargin) {
                this.messageEditText.setLayoutParams(layoutParams);
            }
        }
    }

    public void startMessageTransition() {
        Runnable runnable = this.moveToSendStateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.moveToSendStateRunnable = null;
        }
        this.messageTransitionIsRunning = true;
        updateRecordIntefrace(1);
    }

    public boolean canShowVoiceMessageTransition() {
        return this.moveToSendStateRunnable != null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x02f5 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02f6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRecordIntefrace(int r24) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            java.lang.Runnable r0 = r1.moveToSendStateRunnable
            r3 = 0
            if (r0 == 0) goto L_0x000e
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r1.moveToSendStateRunnable = r3
        L_0x000e:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r4 = 0
            r0.voiceEnterTransitionInProgress = r4
            boolean r0 = r1.recordingAudioVideo
            r5 = 12
            r11 = 1101004800(0x41a00000, float:20.0)
            r12 = 5
            r3 = 4
            r13 = 3
            r14 = 2
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 1
            if (r0 == 0) goto L_0x02cf
            int r0 = r1.recordInterfaceState
            if (r0 != r8) goto L_0x0028
            return
        L_0x0028:
            r1.recordInterfaceState = r8
            org.telegram.ui.Components.EmojiView r0 = r1.emojiView
            if (r0 == 0) goto L_0x0031
            r0.setEnabled(r4)
        L_0x0031:
            android.animation.AnimatorSet r0 = r1.emojiButtonAnimation
            if (r0 == 0) goto L_0x0038
            r0.cancel()
        L_0x0038:
            android.os.PowerManager$WakeLock r0 = r1.wakeLock     // Catch:{ Exception -> 0x0055 }
            if (r0 != 0) goto L_0x0059
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0055 }
            java.lang.String r2 = "power"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x0055 }
            android.os.PowerManager r0 = (android.os.PowerManager) r0     // Catch:{ Exception -> 0x0055 }
            r2 = 536870918(0x20000006, float:1.084203E-19)
            java.lang.String r15 = "telegram:audio_record_lock"
            android.os.PowerManager$WakeLock r0 = r0.newWakeLock(r2, r15)     // Catch:{ Exception -> 0x0055 }
            r1.wakeLock = r0     // Catch:{ Exception -> 0x0055 }
            r0.acquire()     // Catch:{ Exception -> 0x0055 }
            goto L_0x0059
        L_0x0055:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0059:
            android.app.Activity r0 = r1.parentActivity
            org.telegram.messenger.AndroidUtilities.lockOrientation(r0)
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x0065
            r0.needStartRecordAudio(r4)
        L_0x0065:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            if (r0 == 0) goto L_0x006c
            r0.cancel()
        L_0x006c:
            android.animation.AnimatorSet r0 = r1.recordPannelAnimation
            if (r0 == 0) goto L_0x0073
            r0.cancel()
        L_0x0073:
            android.widget.FrameLayout r0 = r1.recordPanel
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r9 = 0
            r0.setAmplitude(r9)
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            r0.resetAlpha()
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r1.runningAnimationAudio = r0
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            r0.setScaleX(r6)
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            r0.setScaleY(r6)
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            boolean unused = r0.enterAnimation = r8
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            r0.setTranslationX(r2)
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            r0.setAlpha(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            r0.setTranslationX(r2)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setAlpha(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setCancelToProgress(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setSlideX(r7)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r2 = 1176256512(0x461CLASSNAME, float:10000.0)
            r0.setLockTranslation(r2)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setEnabled(r8)
            r1.recordIsCanceled = r4
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r5]
            android.widget.ImageView[] r5 = r1.emojiButton
            r5 = r5[r4]
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r2[r4] = r5
            android.widget.ImageView[] r5 = r1.emojiButton
            r5 = r5[r4]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r2[r8] = r5
            android.widget.ImageView[] r5 = r1.emojiButton
            r5 = r5[r4]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r2[r14] = r5
            android.widget.ImageView[] r5 = r1.emojiButton
            r5 = r5[r8]
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r2[r13] = r5
            android.widget.ImageView[] r5 = r1.emojiButton
            r5 = r5[r8]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r2[r3] = r5
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r2[r12] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r3 = r1.recordDot
            android.util.Property r5 = android.view.View.SCALE_Y
            float[] r9 = new float[r8]
            r9[r4] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 6
            r2[r5] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r3 = r1.recordDot
            android.util.Property r5 = android.view.View.SCALE_X
            float[] r9 = new float[r8]
            r9[r4] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 7
            r2[r5] = r3
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r3 = r1.recordTimerView
            android.util.Property r5 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 8
            r2[r5] = r3
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r3 = r1.recordTimerView
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 9
            r2[r5] = r3
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r3 = r1.slideText
            android.util.Property r5 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 10
            r2[r5] = r3
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r3 = r1.slideText
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r5, r9)
            r5 = 11
            r2[r5] = r3
            r0.playTogether(r2)
            android.widget.ImageView r2 = r1.audioSendButton
            if (r2 == 0) goto L_0x01b3
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r9)
            r3[r4] = r2
            r0.playTogether(r3)
        L_0x01b3:
            android.widget.ImageView r2 = r1.videoSendButton
            if (r2 == 0) goto L_0x01c8
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r9)
            r3[r4] = r2
            r0.playTogether(r3)
        L_0x01c8:
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            if (r2 == 0) goto L_0x01f9
            android.animation.Animator[] r3 = new android.animation.Animator[r13]
            android.util.Property r5 = android.view.View.SCALE_Y
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r9)
            r3[r4] = r2
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            android.util.Property r5 = android.view.View.SCALE_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r9)
            r3[r8] = r2
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            android.util.Property r5 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r9)
            r3[r14] = r2
            r0.playTogether(r3)
        L_0x01f9:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            android.animation.Animator[] r3 = new android.animation.Animator[r13]
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r3[r4] = r5
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r3[r8] = r5
            android.widget.FrameLayout r5 = r1.recordedAudioPanel
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r7
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r3[r14] = r5
            r2.playTogether(r3)
            android.widget.ImageView r3 = r1.scheduledButton
            if (r3 == 0) goto L_0x025c
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r9, r10)
            r5[r4] = r3
            android.widget.ImageView r3 = r1.scheduledButton
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r9, r10)
            r5[r8] = r3
            r2.playTogether(r5)
        L_0x025c:
            android.widget.LinearLayout r3 = r1.attachLayout
            if (r3 == 0) goto L_0x0286
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r9, r10)
            r5[r4] = r3
            android.widget.LinearLayout r3 = r1.attachLayout
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r9, r10)
            r5[r8] = r3
            r2.playTogether(r5)
        L_0x0286:
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r13]
            r9 = 150(0x96, double:7.4E-322)
            android.animation.AnimatorSet r0 = r0.setDuration(r9)
            r5[r4] = r0
            android.animation.AnimatorSet r0 = r2.setDuration(r9)
            r5[r8] = r0
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r2 = r1.recordCircleScale
            float[] r6 = new float[r8]
            r6[r4] = r7
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r2, r6)
            r6 = 300(0x12c, double:1.48E-321)
            android.animation.ObjectAnimator r0 = r0.setDuration(r6)
            r5[r14] = r0
            r3.playTogether(r5)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$37 r2 = new org.telegram.ui.Components.ChatActivityEnterView$37
            r2.<init>()
            r0.addListener(r2)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r0.start()
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            r0.start()
            goto L_0x0e8f
        L_0x02cf:
            boolean r0 = r1.recordIsCanceled
            if (r0 == 0) goto L_0x02d6
            if (r2 != r13) goto L_0x02d6
            return
        L_0x02d6:
            android.os.PowerManager$WakeLock r0 = r1.wakeLock
            if (r0 == 0) goto L_0x02e9
            r0.release()     // Catch:{ Exception -> 0x02e3 }
            r9 = 0
            r1.wakeLock = r9     // Catch:{ Exception -> 0x02e1 }
            goto L_0x02ea
        L_0x02e1:
            r0 = move-exception
            goto L_0x02e5
        L_0x02e3:
            r0 = move-exception
            r9 = 0
        L_0x02e5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02ea
        L_0x02e9:
            r9 = 0
        L_0x02ea:
            android.app.Activity r0 = r1.parentActivity
            org.telegram.messenger.AndroidUtilities.unlockOrientation(r0)
            r1.wasSendTyping = r4
            int r0 = r1.recordInterfaceState
            if (r0 != 0) goto L_0x02f6
            return
        L_0x02f6:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r17 = r0.getMessagesController()
            long r9 = r1.dialog_id
            int r20 = r23.getThreadMessageId()
            r21 = 2
            r22 = 0
            r18 = r9
            r17.sendTyping(r18, r20, r21, r22)
            r1.recordInterfaceState = r4
            org.telegram.ui.Components.EmojiView r0 = r1.emojiView
            if (r0 == 0) goto L_0x0314
            r0.setEnabled(r8)
        L_0x0314:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            if (r0 == 0) goto L_0x033f
            boolean r0 = r0.isRunning()
            android.widget.ImageView r9 = r1.videoSendButton
            if (r9 == 0) goto L_0x0328
            r9.setScaleX(r7)
            android.widget.ImageView r9 = r1.videoSendButton
            r9.setScaleY(r7)
        L_0x0328:
            android.widget.ImageView r9 = r1.audioSendButton
            if (r9 == 0) goto L_0x0334
            r9.setScaleX(r7)
            android.widget.ImageView r9 = r1.audioSendButton
            r9.setScaleY(r7)
        L_0x0334:
            android.animation.AnimatorSet r9 = r1.runningAnimationAudio
            r9.removeAllListeners()
            android.animation.AnimatorSet r9 = r1.runningAnimationAudio
            r9.cancel()
            goto L_0x0340
        L_0x033f:
            r0 = 0
        L_0x0340:
            android.animation.AnimatorSet r9 = r1.recordPannelAnimation
            if (r9 == 0) goto L_0x0347
            r9.cancel()
        L_0x0347:
            org.telegram.ui.Components.EditTextCaption r9 = r1.messageEditText
            r9.setVisibility(r4)
            android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
            r9.<init>()
            r1.runningAnimationAudio = r9
            java.lang.String r15 = "slideToCancelProgress"
            if (r0 != 0) goto L_0x0CLASSNAME
            if (r2 != r3) goto L_0x035b
            goto L_0x0CLASSNAME
        L_0x035b:
            if (r2 != r13) goto L_0x066f
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setEnabled(r4)
            boolean r0 = r23.isInVideoMode()
            if (r0 == 0) goto L_0x0393
            android.view.View r0 = r1.recordedAudioBackground
            r15 = 8
            r0.setVisibility(r15)
            android.widget.TextView r0 = r1.recordedAudioTimeTextView
            r0.setVisibility(r15)
            android.widget.ImageView r0 = r1.recordedAudioPlayButton
            r0.setVisibility(r15)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = r1.recordedAudioSeekBar
            r0.setVisibility(r15)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setAlpha(r7)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setProgress(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.stopAnimation()
            goto L_0x03cc
        L_0x0393:
            r15 = 8
            org.telegram.ui.Components.VideoTimelineView r0 = r1.videoTimelineView
            r0.setVisibility(r15)
            android.view.View r0 = r1.recordedAudioBackground
            r0.setVisibility(r4)
            android.widget.TextView r0 = r1.recordedAudioTimeTextView
            r0.setVisibility(r4)
            android.widget.ImageView r0 = r1.recordedAudioPlayButton
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = r1.recordedAudioSeekBar
            r0.setVisibility(r4)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setAlpha(r7)
            android.view.View r0 = r1.recordedAudioBackground
            r0.setAlpha(r6)
            android.widget.TextView r0 = r1.recordedAudioTimeTextView
            r0.setAlpha(r6)
            android.widget.ImageView r0 = r1.recordedAudioPlayButton
            r0.setAlpha(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = r1.recordedAudioSeekBar
            r0.setAlpha(r6)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setVisibility(r4)
        L_0x03cc:
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setAlpha(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setScaleX(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setScaleY(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setProgress(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.stopAnimation()
            float[] r0 = new float[r14]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$PYhmNUMeA9B3RCVAJvS9mPVUwwY r15 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$PYhmNUMeA9B3RCVAJvS9mPVUwwY
            r15.<init>()
            r0.addUpdateListener(r15)
            boolean r15 = r23.isInVideoMode()
            if (r15 != 0) goto L_0x0431
            android.widget.FrameLayout r15 = r1.recordedAudioPanel
            android.view.ViewParent r15 = r15.getParent()
            android.view.ViewGroup r15 = (android.view.ViewGroup) r15
            android.widget.FrameLayout r9 = r1.recordedAudioPanel
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.widget.FrameLayout r10 = r1.recordedAudioPanel
            r15.removeView(r10)
            android.widget.FrameLayout$LayoutParams r10 = new android.widget.FrameLayout$LayoutParams
            int r5 = r15.getMeasuredWidth()
            r16 = 1111490560(0x42400000, float:48.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r10.<init>(r5, r12)
            r5 = 80
            r10.gravity = r5
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r1.sizeNotifierLayout
            android.widget.FrameLayout r12 = r1.recordedAudioPanel
            r5.addView(r12, r10)
            org.telegram.ui.Components.VideoTimelineView r5 = r1.videoTimelineView
            r10 = 8
            r5.setVisibility(r10)
            goto L_0x0438
        L_0x0431:
            org.telegram.ui.Components.VideoTimelineView r5 = r1.videoTimelineView
            r5.setVisibility(r4)
            r9 = 0
            r15 = 0
        L_0x0438:
            org.telegram.ui.Components.RLottieImageView r5 = r1.recordDeleteImageView
            r5.setAlpha(r6)
            org.telegram.ui.Components.RLottieImageView r5 = r1.recordDeleteImageView
            r5.setScaleX(r6)
            org.telegram.ui.Components.RLottieImageView r5 = r1.recordDeleteImageView
            r5.setScaleY(r6)
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            r10 = 15
            android.animation.Animator[] r10 = new android.animation.Animator[r10]
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r12 = r1.recordDot
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r3 = new float[r8]
            r3[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r12, r7, r3)
            r10[r4] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r3 = r1.recordDot
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r10[r8] = r3
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r3 = r1.recordTimerView
            android.util.Property r7 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r10[r14] = r3
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r3 = r1.recordTimerView
            android.util.Property r7 = android.view.View.TRANSLATION_X
            float[] r12 = new float[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r11 = (float) r11
            r12[r4] = r11
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r10[r13] = r3
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r3 = r1.slideText
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 4
            r10[r7] = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.recordDeleteImageView
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 5
            r10[r7] = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.recordDeleteImageView
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 6
            r10[r7] = r3
            org.telegram.ui.Components.RLottieImageView r3 = r1.recordDeleteImageView
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 7
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r4]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 8
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r4]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 9
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r4]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 10
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 11
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 12
            r10[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 13
            r10[r7] = r3
            org.telegram.ui.Components.EditTextCaption r3 = r1.messageEditText
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 14
            r10[r7] = r3
            r5.playTogether(r10)
            android.widget.ImageView r3 = r1.videoSendButton
            if (r3 == 0) goto L_0x0588
            android.animation.Animator[] r7 = new android.animation.Animator[r13]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            boolean r12 = r23.isInVideoMode()
            if (r12 == 0) goto L_0x055e
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x055f
        L_0x055e:
            r12 = 0
        L_0x055f:
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r4] = r3
            android.widget.ImageView r3 = r1.videoSendButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r8] = r3
            android.widget.ImageView r3 = r1.videoSendButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r14] = r3
            r5.playTogether(r7)
        L_0x0588:
            android.widget.ImageView r3 = r1.audioSendButton
            if (r3 == 0) goto L_0x05c5
            android.animation.Animator[] r7 = new android.animation.Animator[r13]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            boolean r12 = r23.isInVideoMode()
            if (r12 == 0) goto L_0x059a
            r12 = 0
            goto L_0x059c
        L_0x059a:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x059c:
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r4] = r3
            android.widget.ImageView r3 = r1.audioSendButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r8] = r3
            android.widget.ImageView r3 = r1.audioSendButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r14] = r3
            r5.playTogether(r7)
        L_0x05c5:
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            if (r3 == 0) goto L_0x05f6
            android.animation.Animator[] r7 = new android.animation.Animator[r13]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r4] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r8] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r7[r14] = r3
            r5.playTogether(r7)
        L_0x05f6:
            org.telegram.ui.Components.ChatActivityEnterView$38 r3 = new org.telegram.ui.Components.ChatActivityEnterView$38
            r3.<init>()
            r5.addListener(r3)
            r10 = 150(0x96, double:7.4E-322)
            r5.setDuration(r10)
            r5.setStartDelay(r10)
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            boolean r7 = r23.isInVideoMode()
            if (r7 == 0) goto L_0x0648
            android.widget.TextView r7 = r1.recordedAudioTimeTextView
            r7.setAlpha(r6)
            org.telegram.ui.Components.VideoTimelineView r7 = r1.videoTimelineView
            r7.setAlpha(r6)
            android.animation.Animator[] r6 = new android.animation.Animator[r14]
            android.widget.TextView r7 = r1.recordedAudioTimeTextView
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r6[r4] = r7
            org.telegram.ui.Components.VideoTimelineView r7 = r1.videoTimelineView
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r6[r8] = r7
            r3.playTogether(r6)
            r6 = 150(0x96, double:7.4E-322)
            r3.setDuration(r6)
            r6 = 430(0x1ae, double:2.124E-321)
            r3.setStartDelay(r6)
        L_0x0648:
            boolean r6 = r23.isInVideoMode()
            if (r6 == 0) goto L_0x0651
            r6 = 490(0x1ea, double:2.42E-321)
            goto L_0x0653
        L_0x0651:
            r6 = 580(0x244, double:2.866E-321)
        L_0x0653:
            r0.setDuration(r6)
            android.animation.AnimatorSet r6 = r1.runningAnimationAudio
            android.animation.Animator[] r7 = new android.animation.Animator[r13]
            r7[r4] = r5
            r7[r8] = r0
            r7[r14] = r3
            r6.playTogether(r7)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$39 r3 = new org.telegram.ui.Components.ChatActivityEnterView$39
            r3.<init>(r15, r9)
            r0.addListener(r3)
            goto L_0x0e7b
        L_0x066f:
            r9 = 200(0xc8, double:9.9E-322)
            if (r2 == r14) goto L_0x0891
            r5 = 5
            if (r2 != r5) goto L_0x0678
            goto L_0x0891
        L_0x0678:
            android.widget.ImageView r0 = r1.videoSendButton
            if (r0 == 0) goto L_0x0688
            boolean r0 = r23.isInVideoMode()
            if (r0 == 0) goto L_0x0688
            android.widget.ImageView r0 = r1.videoSendButton
            r0.setVisibility(r4)
            goto L_0x068f
        L_0x0688:
            android.widget.ImageView r0 = r1.audioSendButton
            if (r0 == 0) goto L_0x068f
            r0.setVisibility(r4)
        L_0x068f:
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r5 = 9
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r15 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r5[r4] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r5[r8] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r5[r14] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r5[r13] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r11 = 4
            r5[r11] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r11 = 5
            r5[r11] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r11 = 6
            r5[r11] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r11, r12)
            r3 = 7
            r5[r3] = r7
            android.widget.FrameLayout r3 = r1.audioVideoButtonContainer
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r7 = 8
            r5[r7] = r3
            r0.playTogether(r5)
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            if (r3 == 0) goto L_0x0760
            android.animation.Animator[] r5 = new android.animation.Animator[r13]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r5[r4] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r5[r8] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r11)
            r5[r14] = r3
            r0.playTogether(r5)
        L_0x0760:
            android.widget.ImageView r3 = r1.audioSendButton
            if (r3 == 0) goto L_0x0789
            r3.setScaleX(r12)
            android.widget.ImageView r3 = r1.audioSendButton
            r3.setScaleY(r12)
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.ImageView r5 = r1.audioSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            boolean r12 = r23.isInVideoMode()
            if (r12 == 0) goto L_0x077c
            r12 = 0
            goto L_0x077e
        L_0x077c:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x077e:
            r11[r4] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r11)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x0789:
            android.widget.ImageView r3 = r1.videoSendButton
            if (r3 == 0) goto L_0x07b4
            r5 = 1065353216(0x3var_, float:1.0)
            r3.setScaleX(r5)
            android.widget.ImageView r3 = r1.videoSendButton
            r3.setScaleY(r5)
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.ImageView r5 = r1.videoSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            boolean r12 = r23.isInVideoMode()
            if (r12 == 0) goto L_0x07a8
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x07a9
        L_0x07a8:
            r12 = 0
        L_0x07a9:
            r11[r4] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r11)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x07b4:
            android.widget.LinearLayout r3 = r1.attachLayout
            if (r3 == 0) goto L_0x07d0
            r3.setTranslationX(r6)
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.LinearLayout r5 = r1.attachLayout
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r11)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x07d0:
            android.widget.ImageView r3 = r1.scheduledButton
            if (r3 == 0) goto L_0x07ec
            r3.setTranslationX(r6)
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.ImageView r5 = r1.scheduledButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r11)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x07ec:
            r11 = 150(0x96, double:7.4E-322)
            r0.setDuration(r11)
            r0.setStartDelay(r9)
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r5 = 4
            android.animation.Animator[] r7 = new android.animation.Animator[r5]
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r5 = r1.recordTimerView
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r12)
            r7[r4] = r5
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r5 = r1.recordTimerView
            android.util.Property r11 = android.view.View.TRANSLATION_X
            float[] r12 = new float[r8]
            r15 = 1109393408(0x42200000, float:40.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r12[r4] = r15
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r12)
            r7[r8] = r5
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r5 = r1.slideText
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r12)
            r7[r14] = r5
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r5 = r1.slideText
            android.util.Property r11 = android.view.View.TRANSLATION_X
            float[] r12 = new float[r8]
            r15 = 1109393408(0x42200000, float:40.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r12[r4] = r15
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r12)
            r7[r13] = r5
            r3.playTogether(r7)
            r11 = 150(0x96, double:7.4E-322)
            r3.setDuration(r11)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r5 = r1.recordCircle
            float[] r7 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r7[r4] = r11
            java.lang.String r11 = "exitTransition"
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r7)
            boolean r7 = r1.messageTransitionIsRunning
            if (r7 == 0) goto L_0x085f
            r11 = 220(0xdc, double:1.087E-321)
            goto L_0x0861
        L_0x085f:
            r11 = 360(0x168, double:1.78E-321)
        L_0x0861:
            r5.setDuration(r11)
            org.telegram.ui.Components.EditTextCaption r7 = r1.messageEditText
            r7.setTranslationX(r6)
            org.telegram.ui.Components.EditTextCaption r6 = r1.messageEditText
            android.util.Property r7 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r11)
            r11 = 150(0x96, double:7.4E-322)
            r6.setStartDelay(r11)
            r6.setDuration(r9)
            android.animation.AnimatorSet r7 = r1.runningAnimationAudio
            r9 = 4
            android.animation.Animator[] r9 = new android.animation.Animator[r9]
            r9[r4] = r0
            r9[r8] = r3
            r9[r14] = r6
            r9[r13] = r5
            r7.playTogether(r9)
            goto L_0x0e7b
        L_0x0891:
            android.widget.ImageView r0 = r1.videoSendButton
            if (r0 == 0) goto L_0x08a1
            boolean r0 = r23.isInVideoMode()
            if (r0 == 0) goto L_0x08a1
            android.widget.ImageView r0 = r1.videoSendButton
            r0.setVisibility(r4)
            goto L_0x08a8
        L_0x08a1:
            android.widget.ImageView r0 = r1.audioSendButton
            if (r0 == 0) goto L_0x08a8
            r0.setVisibility(r4)
        L_0x08a8:
            r1.recordIsCanceled = r8
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r5 = 8
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r12 = android.view.View.SCALE_Y
            float[] r3 = new float[r8]
            r16 = 1065353216(0x3var_, float:1.0)
            r3[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r7, r12, r3)
            r5[r4] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r4]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r8] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r4]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r14] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r13] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r7 = 4
            r5[r7] = r3
            android.widget.ImageView[] r3 = r1.emojiButton
            r3 = r3[r8]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r7 = 5
            r5[r7] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r3 = r1.recordDot
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r7 = 6
            r5[r7] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r3 = r1.recordDot
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r7 = 7
            r5[r7] = r3
            r0.playTogether(r5)
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            if (r3 == 0) goto L_0x096b
            android.animation.Animator[] r5 = new android.animation.Animator[r13]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r12 = new float[r8]
            r16 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r4] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r8] = r3
            org.telegram.ui.Components.BotCommandsMenuView r3 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r12 = new float[r8]
            r12[r4] = r16
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r12)
            r5[r14] = r3
            r0.playTogether(r5)
        L_0x096b:
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r5 = 4
            android.animation.Animator[] r7 = new android.animation.Animator[r5]
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r5 = r1.recordTimerView
            android.util.Property r12 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r12, r9)
            r7[r4] = r5
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r5 = r1.recordTimerView
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = -r12
            float r12 = (float) r12
            r10[r4] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r8] = r5
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r5 = r1.slideText
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r14] = r5
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r5 = r1.slideText
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r13] = r5
            r3.playTogether(r7)
            r5 = 5
            if (r2 == r5) goto L_0x0b53
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            r5.setScaleX(r6)
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            r5.setScaleY(r6)
            android.widget.ImageView r5 = r1.attachButton
            if (r5 == 0) goto L_0x09db
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x09db
            android.widget.ImageView r5 = r1.attachButton
            r5.setScaleX(r6)
            android.widget.ImageView r5 = r1.attachButton
            r5.setScaleY(r6)
        L_0x09db:
            android.widget.ImageView r5 = r1.botButton
            if (r5 == 0) goto L_0x09ef
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x09ef
            android.widget.ImageView r5 = r1.botButton
            r5.setScaleX(r6)
            android.widget.ImageView r5 = r1.botButton
            r5.setScaleY(r6)
        L_0x09ef:
            r5 = 4
            android.animation.Animator[] r7 = new android.animation.Animator[r5]
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r5 = r1.recordCircle
            float[] r9 = new float[r8]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r15, r9)
            r7[r4] = r5
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r11 = new float[r8]
            r11[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r11)
            r7[r8] = r5
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r11)
            r7[r14] = r5
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            android.util.Property r9 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r11)
            r7[r13] = r5
            r0.playTogether(r7)
            android.widget.LinearLayout r5 = r1.attachLayout
            if (r5 == 0) goto L_0x0a50
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r11)
            r7[r4] = r5
            android.widget.LinearLayout r5 = r1.attachLayout
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r8] = r5
            r0.playTogether(r7)
        L_0x0a50:
            android.widget.ImageView r5 = r1.attachButton
            if (r5 == 0) goto L_0x0a76
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r4] = r5
            android.widget.ImageView r5 = r1.attachButton
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r8] = r5
            r0.playTogether(r7)
            goto L_0x0a78
        L_0x0a76:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x0a78:
            android.widget.ImageView r5 = r1.botButton
            if (r5 == 0) goto L_0x0a9b
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r4] = r5
            android.widget.ImageView r5 = r1.botButton
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r8] = r5
            r0.playTogether(r7)
        L_0x0a9b:
            android.widget.ImageView r5 = r1.videoSendButton
            if (r5 == 0) goto L_0x0ae2
            android.animation.Animator[] r7 = new android.animation.Animator[r8]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            boolean r11 = r23.isInVideoMode()
            if (r11 == 0) goto L_0x0aae
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x0aaf
        L_0x0aae:
            r11 = 0
        L_0x0aaf:
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r4] = r5
            r0.playTogether(r7)
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            android.widget.ImageView r7 = r1.videoSendButton
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r4] = r7
            r0.playTogether(r5)
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            android.widget.ImageView r7 = r1.videoSendButton
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r4] = r7
            r0.playTogether(r5)
        L_0x0ae2:
            android.widget.ImageView r5 = r1.audioSendButton
            if (r5 == 0) goto L_0x0b2a
            android.animation.Animator[] r7 = new android.animation.Animator[r8]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            boolean r11 = r23.isInVideoMode()
            if (r11 == 0) goto L_0x0af4
            r11 = 0
            goto L_0x0af6
        L_0x0af4:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x0af6:
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r4] = r5
            r0.playTogether(r7)
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            android.widget.ImageView r7 = r1.audioSendButton
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r4] = r7
            r0.playTogether(r5)
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            android.widget.ImageView r7 = r1.audioSendButton
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r4] = r7
            r0.playTogether(r5)
            goto L_0x0b2c
        L_0x0b2a:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x0b2c:
            android.widget.ImageView r5 = r1.scheduledButton
            if (r5 == 0) goto L_0x0b4f
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r4] = r5
            android.widget.ImageView r5 = r1.scheduledButton
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r9, r10)
            r7[r8] = r5
            r0.playTogether(r7)
        L_0x0b4f:
            r9 = 150(0x96, double:7.4E-322)
            goto L_0x0bd3
        L_0x0b53:
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            android.animation.Animator[] r7 = new android.animation.Animator[r8]
            android.widget.FrameLayout r9 = r1.audioVideoButtonContainer
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r7[r4] = r9
            r5.playTogether(r7)
            android.widget.LinearLayout r7 = r1.attachLayout
            if (r7 == 0) goto L_0x0b93
            android.animation.Animator[] r9 = new android.animation.Animator[r14]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r4] = r7
            android.widget.LinearLayout r7 = r1.attachLayout
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r8] = r7
            r5.playTogether(r9)
            goto L_0x0b95
        L_0x0b93:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x0b95:
            android.widget.ImageView r7 = r1.scheduledButton
            if (r7 == 0) goto L_0x0bb8
            android.animation.Animator[] r9 = new android.animation.Animator[r14]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r8]
            r11[r4] = r12
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r4] = r7
            android.widget.ImageView r7 = r1.scheduledButton
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r8]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r8] = r7
            r5.playTogether(r9)
        L_0x0bb8:
            r9 = 150(0x96, double:7.4E-322)
            r5.setDuration(r9)
            r11 = 110(0x6e, double:5.43E-322)
            r5.setStartDelay(r11)
            org.telegram.ui.Components.ChatActivityEnterView$40 r7 = new org.telegram.ui.Components.ChatActivityEnterView$40
            r7.<init>()
            r5.addListener(r7)
            android.animation.AnimatorSet r7 = r1.runningAnimationAudio
            android.animation.Animator[] r11 = new android.animation.Animator[r8]
            r11[r4] = r5
            r7.playTogether(r11)
        L_0x0bd3:
            r0.setDuration(r9)
            r9 = 700(0x2bc, double:3.46E-321)
            r0.setStartDelay(r9)
            r9 = 200(0xc8, double:9.9E-322)
            r3.setDuration(r9)
            r3.setStartDelay(r9)
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            r5.setTranslationX(r6)
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r7[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r6 = 300(0x12c, double:1.48E-321)
            r5.setStartDelay(r6)
            r5.setDuration(r9)
            android.animation.AnimatorSet r6 = r1.runningAnimationAudio
            r7 = 4
            android.animation.Animator[] r7 = new android.animation.Animator[r7]
            r7[r4] = r0
            r7[r8] = r3
            r7[r14] = r5
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r3 = new float[r8]
            float r5 = r0.startTranslation
            r3[r4] = r5
            java.lang.String r5 = "lockAnimatedTranslation"
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r5, r3)
            r9 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r0 = r0.setDuration(r9)
            r7[r13] = r0
            r6.playTogether(r7)
            r3 = 5
            if (r2 != r3) goto L_0x0c4b
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r0.canceledByGesture()
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r3 = new float[r8]
            r5 = 1065353216(0x3var_, float:1.0)
            r3[r4] = r5
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r15, r3)
            android.animation.ObjectAnimator r0 = r0.setDuration(r9)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r3)
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            r5[r4] = r0
            r3.playTogether(r5)
            goto L_0x0c6c
        L_0x0c4b:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r3 = new float[r8]
            r5 = 1065353216(0x3var_, float:1.0)
            r3[r4] = r5
            java.lang.String r5 = "exitTransition"
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r5, r3)
            r5 = 360(0x168, double:1.78E-321)
            r0.setDuration(r5)
            r5 = 490(0x1ea, double:2.42E-321)
            r0.setStartDelay(r5)
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r8]
            r5[r4] = r0
            r3.playTogether(r5)
        L_0x0c6c:
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            r0.playDeleteAnimation()
            goto L_0x0e7b
        L_0x0CLASSNAME:
            android.widget.ImageView r0 = r1.videoSendButton
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r0 = r23.isInVideoMode()
            if (r0 == 0) goto L_0x0CLASSNAME
            android.widget.ImageView r0 = r1.videoSendButton
            r0.setVisibility(r4)
            goto L_0x0c8a
        L_0x0CLASSNAME:
            android.widget.ImageView r0 = r1.audioSendButton
            if (r0 == 0) goto L_0x0c8a
            r0.setVisibility(r4)
        L_0x0c8a:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r5 = 16
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r4] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r8] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r4]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r14] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5[r13] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 4
            r5[r9] = r7
            android.widget.ImageView[] r7 = r1.emojiButton
            r7 = r7[r8]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r8]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 5
            r5[r9] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 6
            r5[r9] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r3 = 7
            r5[r3] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r7 = r1.recordCircleScale
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 8
            r5[r7] = r3
            android.widget.FrameLayout r3 = r1.audioVideoButtonContainer
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 9
            r5[r7] = r3
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r3 = r1.recordTimerView
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 10
            r5[r7] = r3
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r7 = r1.recordCircleScale
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 11
            r5[r7] = r3
            android.widget.FrameLayout r3 = r1.audioVideoButtonContainer
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 12
            r5[r7] = r3
            org.telegram.ui.Components.EditTextCaption r3 = r1.messageEditText
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 13
            r5[r7] = r3
            org.telegram.ui.Components.EditTextCaption r3 = r1.messageEditText
            android.util.Property r7 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r9)
            r7 = 14
            r5[r7] = r3
            r3 = 15
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r7 = r1.recordCircle
            float[] r9 = new float[r8]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r15, r9)
            r5[r3] = r7
            r0.playTogether(r5)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            if (r0 == 0) goto L_0x0dcc
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r13]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r9 = new float[r8]
            r9[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r4] = r0
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r9 = new float[r8]
            r9[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r8] = r0
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r9[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r14] = r0
            r3.playTogether(r5)
        L_0x0dcc:
            android.widget.ImageView r0 = r1.audioSendButton
            if (r0 == 0) goto L_0x0df7
            r0.setScaleX(r10)
            android.widget.ImageView r0 = r1.audioSendButton
            r0.setScaleY(r10)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.ImageView r5 = r1.audioSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            boolean r10 = r23.isInVideoMode()
            if (r10 == 0) goto L_0x0dea
            r10 = 0
            goto L_0x0dec
        L_0x0dea:
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x0dec:
            r9[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x0df7:
            android.widget.ImageView r0 = r1.videoSendButton
            if (r0 == 0) goto L_0x0e24
            r3 = 1065353216(0x3var_, float:1.0)
            r0.setScaleX(r3)
            android.widget.ImageView r0 = r1.videoSendButton
            r0.setScaleY(r3)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r8]
            android.widget.ImageView r5 = r1.videoSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            boolean r10 = r23.isInVideoMode()
            if (r10 == 0) goto L_0x0e18
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0e19
        L_0x0e18:
            r10 = 0
        L_0x0e19:
            r9[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r3[r4] = r5
            r0.playTogether(r3)
        L_0x0e24:
            android.widget.ImageView r0 = r1.scheduledButton
            if (r0 == 0) goto L_0x0e4b
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            android.util.Property r7 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r4] = r0
            android.widget.ImageView r0 = r1.scheduledButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r8]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r8] = r0
            r3.playTogether(r5)
        L_0x0e4b:
            android.widget.LinearLayout r0 = r1.attachLayout
            if (r0 == 0) goto L_0x0e72
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            android.util.Property r7 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r8]
            r9[r4] = r6
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r9)
            r5[r4] = r0
            android.widget.LinearLayout r0 = r1.attachLayout
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r8]
            r9 = 1065353216(0x3var_, float:1.0)
            r7[r4] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r5[r8] = r0
            r3.playTogether(r5)
        L_0x0e72:
            r1.recordIsCanceled = r8
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r3 = 150(0x96, double:7.4E-322)
            r0.setDuration(r3)
        L_0x0e7b:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$41 r3 = new org.telegram.ui.Components.ChatActivityEnterView$41
            r3.<init>(r2)
            r0.addListener(r3)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r0.start()
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            r0.stop()
        L_0x0e8f:
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r1.delegate
            r0.onAudioVideoInterfaceUpdated()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateRecordIntefrace(int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateRecordIntefrace$28 */
    public /* synthetic */ void lambda$updateRecordIntefrace$28$ChatActivityEnterView(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (!isInVideoMode()) {
            this.recordCircle.setTransformToSeekbar(floatValue);
            this.seekBarWaveform.setWaveScaling(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.invalidate();
            this.recordedAudioTimeTextView.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleX(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleY(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            return;
        }
        this.recordCircle.setExitTransition(floatValue);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.recordingAudioVideo) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        this.delegate = chatActivityEnterViewDelegate;
    }

    public void setCommand(MessageObject messageObject, String str, boolean z, boolean z2) {
        String str2;
        MessageObject messageObject2 = messageObject;
        String str3 = str;
        if (str3 != null && getVisibility() == 0) {
            TLRPC$User tLRPC$User = null;
            if (z) {
                String obj = this.messageEditText.getText().toString();
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    tLRPC$User = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id.user_id));
                }
                if ((this.botCount != 1 || z2) && tLRPC$User != null && tLRPC$User.bot && !str3.contains("@")) {
                    str2 = String.format(Locale.US, "%s@%s", new Object[]{str3, tLRPC$User.username}) + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                } else {
                    str2 = str3 + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                }
                this.ignoreTextChange = true;
                this.messageEditText.setText(str2);
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
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    tLRPC$User = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id.user_id));
                }
                if ((this.botCount != 1 || z2) && tLRPC$User != null && tLRPC$User.bot && !str3.contains("@")) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str3, tLRPC$User.username}), this.dialog_id, this.replyingMessageObject, getThreadMessage(), (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                    return;
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, getThreadMessage(), (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
            } else {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    SimpleTextView simpleTextView = this.slowModeButton;
                    chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [java.lang.CharSequence] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setEditingMessageObject(org.telegram.messenger.MessageObject r13, boolean r14) {
        /*
            r12 = this;
            org.telegram.tgnet.TLRPC$TL_document r0 = r12.audioToSend
            if (r0 != 0) goto L_0x03a8
            org.telegram.messenger.VideoEditedInfo r0 = r12.videoToSendMessageObject
            if (r0 != 0) goto L_0x03a8
            org.telegram.messenger.MessageObject r0 = r12.editingMessageObject
            if (r0 != r13) goto L_0x000e
            goto L_0x03a8
        L_0x000e:
            r12.editingMessageObject = r13
            r12.editingCaption = r14
            r0 = 0
            java.lang.String r1 = ""
            r2 = 0
            r3 = 1
            r4 = 8
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x0279
            android.animation.AnimatorSet r13 = r12.doneButtonAnimation
            if (r13 == 0) goto L_0x002a
            r13.cancel()
            r12.doneButtonAnimation = r0
        L_0x002a:
            android.widget.FrameLayout r13 = r12.doneButtonContainer
            r13.setVisibility(r6)
            android.widget.ImageView r13 = r12.doneButtonImage
            r13.setScaleX(r5)
            android.widget.ImageView r13 = r12.doneButtonImage
            r13.setScaleY(r5)
            android.widget.ImageView r13 = r12.doneButtonImage
            r13.setAlpha(r2)
            android.widget.ImageView r13 = r12.doneButtonImage
            android.view.ViewPropertyAnimator r13 = r13.animate()
            android.view.ViewPropertyAnimator r13 = r13.alpha(r7)
            android.view.ViewPropertyAnimator r13 = r13.scaleX(r7)
            android.view.ViewPropertyAnimator r13 = r13.scaleY(r7)
            r7 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r13 = r13.setDuration(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r13 = r13.setInterpolator(r2)
            r13.start()
            if (r14 == 0) goto L_0x0070
            org.telegram.messenger.AccountInstance r13 = r12.accountInstance
            org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
            int r13 = r13.maxCaptionLength
            r12.currentLimit = r13
            org.telegram.messenger.MessageObject r13 = r12.editingMessageObject
            java.lang.CharSequence r13 = r13.caption
            goto L_0x007e
        L_0x0070:
            org.telegram.messenger.AccountInstance r13 = r12.accountInstance
            org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
            int r13 = r13.maxMessageLength
            r12.currentLimit = r13
            org.telegram.messenger.MessageObject r13 = r12.editingMessageObject
            java.lang.CharSequence r13 = r13.messageText
        L_0x007e:
            if (r13 == 0) goto L_0x021a
            org.telegram.messenger.MessageObject r14 = r12.editingMessageObject
            org.telegram.tgnet.TLRPC$Message r14 = r14.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r14 = r14.entities
            org.telegram.messenger.MediaDataController.sortEntities(r14)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r13)
            int r13 = r2.length()
            java.lang.Class<java.lang.Object> r5 = java.lang.Object.class
            java.lang.Object[] r13 = r2.getSpans(r6, r13, r5)
            if (r13 == 0) goto L_0x00a9
            int r5 = r13.length
            if (r5 <= 0) goto L_0x00a9
            r5 = 0
        L_0x009e:
            int r7 = r13.length
            if (r5 >= r7) goto L_0x00a9
            r7 = r13[r5]
            r2.removeSpan(r7)
            int r5 = r5 + 1
            goto L_0x009e
        L_0x00a9:
            if (r14 == 0) goto L_0x0201
            r13 = 0
        L_0x00ac:
            int r5 = r14.size()     // Catch:{ Exception -> 0x01fd }
            if (r13 >= r5) goto L_0x0201
            java.lang.Object r5 = r14.get(r13)     // Catch:{ Exception -> 0x01fd }
            org.telegram.tgnet.TLRPC$MessageEntity r5 = (org.telegram.tgnet.TLRPC$MessageEntity) r5     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r8 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r8
            int r8 = r2.length()     // Catch:{ Exception -> 0x01fd }
            if (r7 <= r8) goto L_0x00c5
            goto L_0x01f9
        L_0x00c5:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName     // Catch:{ Exception -> 0x01fd }
            r8 = 3
            r9 = 32
            r10 = 33
            if (r7 == 0) goto L_0x010e
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r11 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r11
            int r11 = r2.length()     // Catch:{ Exception -> 0x01fd }
            if (r7 >= r11) goto L_0x00e9
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r11 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r11
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x01fd }
            if (r7 != r9) goto L_0x00e9
            int r7 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r3
            r5.length = r7     // Catch:{ Exception -> 0x01fd }
        L_0x00e9:
            org.telegram.ui.Components.URLSpanUserMention r7 = new org.telegram.ui.Components.URLSpanUserMention     // Catch:{ Exception -> 0x01fd }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fd }
            r9.<init>()     // Catch:{ Exception -> 0x01fd }
            r9.append(r1)     // Catch:{ Exception -> 0x01fd }
            r11 = r5
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r11 = (org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName) r11     // Catch:{ Exception -> 0x01fd }
            org.telegram.tgnet.TLRPC$InputUser r11 = r11.user_id     // Catch:{ Exception -> 0x01fd }
            int r11 = r11.user_id     // Catch:{ Exception -> 0x01fd }
            r9.append(r11)     // Catch:{ Exception -> 0x01fd }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x01fd }
            r7.<init>(r9, r8)     // Catch:{ Exception -> 0x01fd }
            int r8 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r8
            r2.setSpan(r7, r8, r5, r10)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x010e:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x0150
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r11 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r11
            int r11 = r2.length()     // Catch:{ Exception -> 0x01fd }
            if (r7 >= r11) goto L_0x012d
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r11 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r11
            char r7 = r2.charAt(r7)     // Catch:{ Exception -> 0x01fd }
            if (r7 != r9) goto L_0x012d
            int r7 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r7 = r7 + r3
            r5.length = r7     // Catch:{ Exception -> 0x01fd }
        L_0x012d:
            org.telegram.ui.Components.URLSpanUserMention r7 = new org.telegram.ui.Components.URLSpanUserMention     // Catch:{ Exception -> 0x01fd }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fd }
            r9.<init>()     // Catch:{ Exception -> 0x01fd }
            r9.append(r1)     // Catch:{ Exception -> 0x01fd }
            r11 = r5
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r11 = (org.telegram.tgnet.TLRPC$TL_messageEntityMentionName) r11     // Catch:{ Exception -> 0x01fd }
            int r11 = r11.user_id     // Catch:{ Exception -> 0x01fd }
            r9.append(r11)     // Catch:{ Exception -> 0x01fd }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x01fd }
            r7.<init>(r9, r8)     // Catch:{ Exception -> 0x01fd }
            int r8 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r8
            r2.setSpan(r7, r8, r5, r10)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x0150:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode     // Catch:{ Exception -> 0x01fd }
            if (r7 != 0) goto L_0x01e1
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x015a
            goto L_0x01e1
        L_0x015a:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x0177
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x01fd }
            r7.<init>()     // Catch:{ Exception -> 0x01fd }
            int r8 = r7.flags     // Catch:{ Exception -> 0x01fd }
            r8 = r8 | r3
            r7.flags = r8     // Catch:{ Exception -> 0x01fd }
            org.telegram.ui.Components.TextStyleSpan r8 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x01fd }
            r8.<init>(r7)     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r7
            org.telegram.messenger.MediaDataController.addStyleToText(r8, r7, r5, r2, r3)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x0177:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x0194
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x01fd }
            r7.<init>()     // Catch:{ Exception -> 0x01fd }
            int r8 = r7.flags     // Catch:{ Exception -> 0x01fd }
            r8 = r8 | 2
            r7.flags = r8     // Catch:{ Exception -> 0x01fd }
            org.telegram.ui.Components.TextStyleSpan r8 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x01fd }
            r8.<init>(r7)     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r7
            org.telegram.messenger.MediaDataController.addStyleToText(r8, r7, r5, r2, r3)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x0194:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x01b0
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x01fd }
            r7.<init>()     // Catch:{ Exception -> 0x01fd }
            int r8 = r7.flags     // Catch:{ Exception -> 0x01fd }
            r8 = r8 | r4
            r7.flags = r8     // Catch:{ Exception -> 0x01fd }
            org.telegram.ui.Components.TextStyleSpan r8 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x01fd }
            r8.<init>(r7)     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r7
            org.telegram.messenger.MediaDataController.addStyleToText(r8, r7, r5, r2, r3)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x01b0:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x01cd
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x01fd }
            r7.<init>()     // Catch:{ Exception -> 0x01fd }
            int r8 = r7.flags     // Catch:{ Exception -> 0x01fd }
            r8 = r8 | 16
            r7.flags = r8     // Catch:{ Exception -> 0x01fd }
            org.telegram.ui.Components.TextStyleSpan r8 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x01fd }
            r8.<init>(r7)     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r7
            org.telegram.messenger.MediaDataController.addStyleToText(r8, r7, r5, r2, r3)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x01cd:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl     // Catch:{ Exception -> 0x01fd }
            if (r7 == 0) goto L_0x01f9
            org.telegram.ui.Components.URLSpanReplacement r7 = new org.telegram.ui.Components.URLSpanReplacement     // Catch:{ Exception -> 0x01fd }
            java.lang.String r8 = r5.url     // Catch:{ Exception -> 0x01fd }
            r7.<init>(r8)     // Catch:{ Exception -> 0x01fd }
            int r8 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r8
            r2.setSpan(r7, r8, r5, r10)     // Catch:{ Exception -> 0x01fd }
            goto L_0x01f9
        L_0x01e1:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x01fd }
            r7.<init>()     // Catch:{ Exception -> 0x01fd }
            int r8 = r7.flags     // Catch:{ Exception -> 0x01fd }
            r8 = r8 | 4
            r7.flags = r8     // Catch:{ Exception -> 0x01fd }
            org.telegram.ui.Components.TextStyleSpan r8 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x01fd }
            r8.<init>(r7)     // Catch:{ Exception -> 0x01fd }
            int r7 = r5.offset     // Catch:{ Exception -> 0x01fd }
            int r5 = r5.length     // Catch:{ Exception -> 0x01fd }
            int r5 = r5 + r7
            org.telegram.messenger.MediaDataController.addStyleToText(r8, r7, r5, r2, r3)     // Catch:{ Exception -> 0x01fd }
        L_0x01f9:
            int r13 = r13 + 1
            goto L_0x00ac
        L_0x01fd:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x0201:
            android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
            r13.<init>(r2)
            org.telegram.ui.Components.EditTextCaption r14 = r12.messageEditText
            android.text.TextPaint r14 = r14.getPaint()
            android.graphics.Paint$FontMetricsInt r14 = r14.getFontMetricsInt()
            r1 = 1101004800(0x41a00000, float:20.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r13, r14, r1, r6)
        L_0x021a:
            boolean r13 = r12.keyboardVisible
            if (r13 != 0) goto L_0x022b
            org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$dtmts_vEQuxCnB6KRfKLR_WqUOs r13 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$dtmts_vEQuxCnB6KRfKLR_WqUOs
            r13.<init>(r1)
            r12.setTextFieldRunnable = r13
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r13, r0)
            goto L_0x0237
        L_0x022b:
            java.lang.Runnable r13 = r12.setTextFieldRunnable
            if (r13 == 0) goto L_0x0234
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13)
            r12.setTextFieldRunnable = r0
        L_0x0234:
            r12.setFieldText(r1)
        L_0x0237:
            org.telegram.ui.Components.EditTextCaption r13 = r12.messageEditText
            r13.requestFocus()
            r12.openKeyboard()
            org.telegram.ui.Components.EditTextCaption r13 = r12.messageEditText
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13.rightMargin = r14
            org.telegram.ui.Components.EditTextCaption r14 = r12.messageEditText
            r14.setLayoutParams(r13)
            android.view.View r13 = r12.sendButton
            r13.setVisibility(r4)
            r12.setSlowModeButtonVisible(r6)
            android.widget.ImageView r13 = r12.cancelBotButton
            r13.setVisibility(r4)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setVisibility(r4)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setVisibility(r4)
            android.widget.FrameLayout r13 = r12.sendButtonContainer
            r13.setVisibility(r4)
            android.widget.ImageView r13 = r12.scheduledButton
            if (r13 == 0) goto L_0x03a5
            r13.setVisibility(r4)
            goto L_0x03a5
        L_0x0279:
            java.lang.Runnable r13 = r12.setTextFieldRunnable
            if (r13 == 0) goto L_0x0282
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r13)
            r12.setTextFieldRunnable = r0
        L_0x0282:
            android.widget.FrameLayout r13 = r12.doneButtonContainer
            r13.setVisibility(r4)
            r13 = -1
            r12.currentLimit = r13
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r13 = r12.delegate
            r13.onMessageEditEnd(r6)
            android.widget.FrameLayout r13 = r12.sendButtonContainer
            r13.setVisibility(r6)
            android.widget.ImageView r13 = r12.cancelBotButton
            r13.setScaleX(r5)
            android.widget.ImageView r13 = r12.cancelBotButton
            r13.setScaleY(r5)
            android.widget.ImageView r13 = r12.cancelBotButton
            r13.setAlpha(r2)
            android.widget.ImageView r13 = r12.cancelBotButton
            r13.setVisibility(r4)
            int r13 = r12.slowModeTimer
            if (r13 <= 0) goto L_0x032d
            boolean r13 = r12.isInScheduleMode()
            if (r13 != 0) goto L_0x032d
            int r13 = r12.slowModeTimer
            r14 = 2147483647(0x7fffffff, float:NaN)
            if (r13 != r14) goto L_0x02e0
            android.view.View r13 = r12.sendButton
            r13.setScaleX(r7)
            android.view.View r13 = r12.sendButton
            r13.setScaleY(r7)
            android.view.View r13 = r12.sendButton
            r13.setAlpha(r7)
            android.view.View r13 = r12.sendButton
            r13.setVisibility(r6)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleX(r5)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleY(r5)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setAlpha(r2)
            r12.setSlowModeButtonVisible(r6)
            goto L_0x0306
        L_0x02e0:
            android.view.View r13 = r12.sendButton
            r13.setScaleX(r5)
            android.view.View r13 = r12.sendButton
            r13.setScaleY(r5)
            android.view.View r13 = r12.sendButton
            r13.setAlpha(r2)
            android.view.View r13 = r12.sendButton
            r13.setVisibility(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleX(r7)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleY(r7)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setAlpha(r7)
            r12.setSlowModeButtonVisible(r3)
        L_0x0306:
            android.widget.LinearLayout r13 = r12.attachLayout
            r14 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            r13.setScaleX(r14)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setAlpha(r2)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setVisibility(r4)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setScaleX(r5)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setScaleY(r5)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setAlpha(r2)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setVisibility(r4)
            goto L_0x0376
        L_0x032d:
            android.view.View r13 = r12.sendButton
            r13.setScaleX(r5)
            android.view.View r13 = r12.sendButton
            r13.setScaleY(r5)
            android.view.View r13 = r12.sendButton
            r13.setAlpha(r2)
            android.view.View r13 = r12.sendButton
            r13.setVisibility(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleX(r5)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setScaleY(r5)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.slowModeButton
            r13.setAlpha(r2)
            r12.setSlowModeButtonVisible(r6)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setScaleX(r7)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setAlpha(r7)
            android.widget.LinearLayout r13 = r12.attachLayout
            r13.setVisibility(r6)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setScaleX(r7)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setScaleY(r7)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setAlpha(r7)
            android.widget.FrameLayout r13 = r12.audioVideoButtonContainer
            r13.setVisibility(r6)
        L_0x0376:
            android.widget.ImageView r13 = r12.scheduledButton
            java.lang.Object r13 = r13.getTag()
            if (r13 == 0) goto L_0x0392
            android.widget.ImageView r13 = r12.scheduledButton
            r13.setScaleX(r7)
            android.widget.ImageView r13 = r12.scheduledButton
            r13.setScaleY(r7)
            android.widget.ImageView r13 = r12.scheduledButton
            r13.setAlpha(r7)
            android.widget.ImageView r13 = r12.scheduledButton
            r13.setVisibility(r6)
        L_0x0392:
            org.telegram.ui.Components.EditTextCaption r13 = r12.messageEditText
            r13.setText(r1)
            int r13 = r12.getVisibility()
            if (r13 != 0) goto L_0x03a2
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r13 = r12.delegate
            r13.onAttachButtonShow()
        L_0x03a2:
            r12.updateFieldRight(r3)
        L_0x03a5:
            r12.updateFieldHint(r6)
        L_0x03a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEditingMessageObject(org.telegram.messenger.MessageObject, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setEditingMessageObject$29 */
    public /* synthetic */ void lambda$setEditingMessageObject$29$ChatActivityEnterView(CharSequence charSequence) {
        setFieldText(charSequence);
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

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public TrendingStickersAlert getTrendingStickersAlert() {
        return this.trendingStickersAlert;
    }

    public void updateColors() {
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.updateColors();
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
        if (actionBarPopupWindowLayout != null) {
            int childCount = actionBarPopupWindowLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.sendPopupLayout.getChildAt(i);
                if (childAt instanceof ActionBarMenuSubItem) {
                    ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) childAt;
                    actionBarMenuSubItem.setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                    actionBarMenuSubItem.setSelectorColor(Theme.getColor("dialogButtonSelector"));
                }
            }
            this.sendPopupLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
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
                numberTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
            } else {
                numberTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            }
        }
        int color = Theme.getColor("chat_messagePanelVoicePressed");
        this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * ((this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
        BotCommandsMenuContainer botCommandsMenuContainer2 = this.botCommandsMenuContainer;
        if (botCommandsMenuContainer2 != null) {
            botCommandsMenuContainer2.updateColors();
        }
        BotKeyboardView botKeyboardView2 = this.botKeyboardView;
        if (botKeyboardView2 != null) {
            botKeyboardView2.updateColors();
        }
    }

    private void updateRecordedDeleteIconColors() {
        int color = Theme.getColor("chat_recordedVoiceDot");
        int color2 = Theme.getColor("chat_messagePanelBackground");
        int color3 = Theme.getColor("chat_messagePanelVoiceDelete");
        this.recordDeleteImageView.setLayerColor("Cup Red.**", color);
        this.recordDeleteImageView.setLayerColor("Box Red.**", color);
        this.recordDeleteImageView.setLayerColor("Cup Grey.**", color3);
        this.recordDeleteImageView.setLayerColor("Box Grey.**", color3);
        this.recordDeleteImageView.setLayerColor("Line 1.**", color2);
        this.recordDeleteImageView.setLayerColor("Line 2.**", color2);
        this.recordDeleteImageView.setLayerColor("Line 3.**", color2);
    }

    public void setFieldText(CharSequence charSequence) {
        setFieldText(charSequence, true);
    }

    public void setFieldText(CharSequence charSequence, boolean z) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            this.ignoreTextChange = z;
            editTextCaption.setText(charSequence);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            this.ignoreTextChange = false;
            if (z && (chatActivityEnterViewDelegate = this.delegate) != null) {
                chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int i) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setSelection(i, editTextCaption.length());
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

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji(spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(spannableStringBuilder);
            this.messageEditText.setSelection(i + charSequence.length());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !accessibilityManager.isTouchExplorationEnabled()) {
            try {
                this.messageEditText.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setFieldFocused(boolean z) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !accessibilityManager.isTouchExplorationEnabled()) {
            if (!z) {
                EditTextCaption editTextCaption = this.messageEditText;
                if (editTextCaption != null && editTextCaption.isFocused()) {
                    if (!this.keyboardVisible || this.isPaused) {
                        this.messageEditText.clearFocus();
                    }
                }
            } else if (this.searchingType == 0 && !this.messageEditText.isFocused()) {
                $$Lambda$ChatActivityEnterView$YjKWlFzCPQnQEEMSRS3CN_Mn84 r3 = new Runnable() {
                    public final void run() {
                        ChatActivityEnterView.this.lambda$setFieldFocused$30$ChatActivityEnterView();
                    }
                };
                this.focusRunnable = r3;
                AndroidUtilities.runOnUIThread(r3, 600);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setFieldFocused$30 */
    public /* synthetic */ void lambda$setFieldFocused$30$ChatActivityEnterView() {
        EditTextCaption editTextCaption;
        ActionBarLayout layersActionBarLayout;
        this.focusRunnable = null;
        boolean z = true;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if ((activity instanceof LaunchActivity) && (layersActionBarLayout = ((LaunchActivity) activity).getLayersActionBarLayout()) != null && layersActionBarLayout.getVisibility() == 0) {
                z = false;
            }
        }
        if (!this.isPaused && z && (editTextCaption = this.messageEditText) != null) {
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

    public CharSequence getFieldText() {
        if (hasText()) {
            return this.messageEditText.getText();
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0042, code lost:
        r3 = r1.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateScheduleButton(boolean r14) {
        /*
            r13 = this;
            long r0 = r13.dialog_id
            int r1 = (int) r0
            r0 = 1
            r2 = 0
            if (r1 >= 0) goto L_0x008c
            org.telegram.messenger.AccountInstance r1 = r13.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            long r3 = r13.dialog_id
            int r4 = (int) r3
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            int r3 = r13.currentAccount
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "silent_"
            r4.append(r5)
            long r5 = r13.dialog_id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            boolean r3 = r3.getBoolean(r4, r2)
            r13.silent = r3
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r3 == 0) goto L_0x0050
            boolean r3 = r1.creator
            if (r3 != 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r1.admin_rights
            if (r3 == 0) goto L_0x0050
            boolean r3 = r3.post_messages
            if (r3 == 0) goto L_0x0050
        L_0x004a:
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0050
            r1 = 1
            goto L_0x0051
        L_0x0050:
            r1 = 0
        L_0x0051:
            r13.canWriteToChannel = r1
            android.widget.ImageView r3 = r13.notifyButton
            if (r3 == 0) goto L_0x007a
            org.telegram.ui.Components.CrossOutDrawable r3 = r13.notifySilentDrawable
            if (r3 != 0) goto L_0x006b
            org.telegram.ui.Components.CrossOutDrawable r3 = new org.telegram.ui.Components.CrossOutDrawable
            android.content.Context r4 = r13.getContext()
            r5 = 2131165537(0x7var_, float:1.7945294E38)
            java.lang.String r6 = "chat_messagePanelIcons"
            r3.<init>(r4, r5, r6)
            r13.notifySilentDrawable = r3
        L_0x006b:
            org.telegram.ui.Components.CrossOutDrawable r3 = r13.notifySilentDrawable
            boolean r4 = r13.silent
            r3.setCrossOut(r4, r2)
            android.widget.ImageView r3 = r13.notifyButton
            org.telegram.ui.Components.CrossOutDrawable r4 = r13.notifySilentDrawable
            r3.setImageDrawable(r4)
            goto L_0x007b
        L_0x007a:
            r1 = 0
        L_0x007b:
            android.widget.LinearLayout r3 = r13.attachLayout
            if (r3 == 0) goto L_0x008d
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0087
            r3 = 1
            goto L_0x0088
        L_0x0087:
            r3 = 0
        L_0x0088:
            r13.updateFieldRight(r3)
            goto L_0x008d
        L_0x008c:
            r1 = 0
        L_0x008d:
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
            if (r3 == 0) goto L_0x00a1
            boolean r3 = r13.isInScheduleMode()
            if (r3 != 0) goto L_0x00a1
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
            boolean r3 = r3.hasScheduledMessages()
            if (r3 == 0) goto L_0x00a1
            r3 = 1
            goto L_0x00a2
        L_0x00a1:
            r3 = 0
        L_0x00a2:
            if (r3 == 0) goto L_0x00ae
            boolean r4 = r13.scheduleButtonHidden
            if (r4 != 0) goto L_0x00ae
            boolean r4 = r13.recordingAudioVideo
            if (r4 != 0) goto L_0x00ae
            r4 = 1
            goto L_0x00af
        L_0x00ae:
            r4 = 0
        L_0x00af:
            android.widget.ImageView r5 = r13.scheduledButton
            r6 = 1119879168(0x42CLASSNAME, float:96.0)
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 0
            r9 = 8
            if (r5 == 0) goto L_0x011c
            java.lang.Object r5 = r5.getTag()
            if (r5 == 0) goto L_0x00c2
            if (r4 != 0) goto L_0x00cc
        L_0x00c2:
            android.widget.ImageView r5 = r13.scheduledButton
            java.lang.Object r5 = r5.getTag()
            if (r5 != 0) goto L_0x010f
            if (r4 != 0) goto L_0x010f
        L_0x00cc:
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x010e
            if (r3 != 0) goto L_0x00dd
            if (r1 == 0) goto L_0x00dd
            android.widget.ImageView r14 = r13.scheduledButton
            int r14 = r14.getVisibility()
            if (r14 == 0) goto L_0x00dd
            goto L_0x00df
        L_0x00dd:
            r2 = 8
        L_0x00df:
            android.widget.ImageView r14 = r13.notifyButton
            int r14 = r14.getVisibility()
            if (r2 == r14) goto L_0x010e
            android.widget.ImageView r14 = r13.notifyButton
            r14.setVisibility(r2)
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x010e
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x00fa
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0106
        L_0x00fa:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x0104
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0106
        L_0x0104:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x0106:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x010e:
            return
        L_0x010f:
            android.widget.ImageView r3 = r13.scheduledButton
            if (r4 == 0) goto L_0x0118
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
            goto L_0x0119
        L_0x0118:
            r5 = r8
        L_0x0119:
            r3.setTag(r5)
        L_0x011c:
            android.animation.AnimatorSet r3 = r13.scheduledButtonAnimation
            if (r3 == 0) goto L_0x0125
            r3.cancel()
            r13.scheduledButtonAnimation = r8
        L_0x0125:
            r3 = 0
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r8 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x01a5
            if (r1 == 0) goto L_0x0131
            goto L_0x01a5
        L_0x0131:
            android.widget.ImageView r14 = r13.scheduledButton
            if (r14 == 0) goto L_0x01e5
            if (r4 == 0) goto L_0x013a
            r14.setVisibility(r2)
        L_0x013a:
            android.widget.ImageView r14 = r13.scheduledButton
            r1 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r14.setPivotX(r1)
            android.animation.AnimatorSet r14 = new android.animation.AnimatorSet
            r14.<init>()
            r13.scheduledButtonAnimation = r14
            r1 = 3
            android.animation.Animator[] r1 = new android.animation.Animator[r1]
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r0]
            if (r4 == 0) goto L_0x015a
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x015a:
            r12[r2] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r1[r2] = r3
            android.widget.ImageView r3 = r13.scheduledButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r0]
            if (r4 == 0) goto L_0x016d
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0170
        L_0x016d:
            r12 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0170:
            r11[r2] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r1[r0] = r3
            r3 = 2
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r0 = new float[r0]
            if (r4 == 0) goto L_0x0183
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0183:
            r0[r2] = r5
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r10, r11, r0)
            r1[r3] = r0
            r14.playTogether(r1)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r0 = 180(0xb4, double:8.9E-322)
            r14.setDuration(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$42 r0 = new org.telegram.ui.Components.ChatActivityEnterView$42
            r0.<init>(r4)
            r14.addListener(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r14.start()
            goto L_0x01e5
        L_0x01a5:
            android.widget.ImageView r14 = r13.scheduledButton
            if (r14 == 0) goto L_0x01e5
            if (r4 == 0) goto L_0x01ad
            r0 = 0
            goto L_0x01af
        L_0x01ad:
            r0 = 8
        L_0x01af:
            r14.setVisibility(r0)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01b8
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x01b8:
            r14.setAlpha(r3)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01c2
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x01c5
        L_0x01c2:
            r0 = 1036831949(0x3dcccccd, float:0.1)
        L_0x01c5:
            r14.setScaleX(r0)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01ce
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01ce:
            r14.setScaleY(r5)
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x01e5
            if (r1 == 0) goto L_0x01e0
            android.widget.ImageView r0 = r13.scheduledButton
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x01e0
            goto L_0x01e2
        L_0x01e0:
            r2 = 8
        L_0x01e2:
            r14.setVisibility(r2)
        L_0x01e5:
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x0207
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x01f3
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x01ff
        L_0x01f3:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x01fd
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x01ff
        L_0x01fd:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x01ff:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x0207:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateScheduleButton(boolean):void");
    }

    private void updateBotButton(boolean z) {
        ImageView imageView;
        if (this.botButton != null) {
            boolean z2 = false;
            if (!this.parentFragment.openAnimationEnded) {
                z = false;
            }
            boolean z3 = this.hasBotCommands;
            if (z3 && this.dialog_id < 0) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                z3 = chat == null || !chat.megagroup;
            }
            if (!this.hasBotCommands && this.botReplyMarkup == null) {
                this.botButton.setVisibility(8);
            } else if (this.botReplyMarkup != null) {
                if (this.botButton.getVisibility() != 0) {
                    this.botButton.setVisibility(0);
                }
                if (!isPopupShowing() || this.currentPopupContentType != 1) {
                    this.botButtonDrawable.setIcon(NUM, true);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrBotKeyboard", NUM));
                } else {
                    this.botButtonDrawable.setIcon(NUM, true);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrShowKeyboard", NUM));
                }
            } else if (!z3) {
                this.botButtonDrawable.setIcon(NUM, true);
                this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", NUM));
                this.botButton.setVisibility(0);
            } else {
                this.botButton.setVisibility(8);
            }
            BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
            if (z3 && this.hasBotCommands) {
                z2 = true;
            }
            AndroidUtilities.updateViewVisibilityAnimated(botCommandsMenuView, z2, 0.5f, z);
            if (z) {
                beginDelayedTransition();
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
        } catch (Throwable unused) {
            return false;
        }
    }

    public void setBotsCount(int i, boolean z, boolean z2) {
        this.botCount = i;
        if (this.hasBotCommands != z) {
            this.hasBotCommands = z;
            updateBotButton(z2);
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00be, code lost:
        if (r8.getInt("answered_" + r6.dialog_id, 0) == r7.getId()) goto L_0x00c2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setButtons(org.telegram.messenger.MessageObject r7, boolean r8) {
        /*
            r6 = this;
            org.telegram.messenger.MessageObject r0 = r6.replyingMessageObject
            if (r0 == 0) goto L_0x000d
            org.telegram.messenger.MessageObject r1 = r6.botButtonsMessageObject
            if (r0 != r1) goto L_0x000d
            if (r0 == r7) goto L_0x000d
            r6.botMessageObject = r7
            return
        L_0x000d:
            android.widget.ImageView r0 = r6.botButton
            if (r0 == 0) goto L_0x00f7
            org.telegram.messenger.MessageObject r0 = r6.botButtonsMessageObject
            if (r0 == 0) goto L_0x0017
            if (r0 == r7) goto L_0x00f7
        L_0x0017:
            if (r0 != 0) goto L_0x001d
            if (r7 != 0) goto L_0x001d
            goto L_0x00f7
        L_0x001d:
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0049
            org.telegram.ui.Components.ChatActivityEnterView$43 r0 = new org.telegram.ui.Components.ChatActivityEnterView$43
            android.app.Activity r3 = r6.parentActivity
            r0.<init>(r3)
            r6.botKeyboardView = r0
            r3 = 8
            r0.setVisibility(r3)
            r6.botKeyboardViewVisible = r1
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$efA7ZCpCsVnq_QQ1xnOnAZF3DAM r3 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$efA7ZCpCsVnq_QQ1xnOnAZF3DAM
            r3.<init>()
            r0.setDelegate(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r6.sizeNotifierLayout
            org.telegram.ui.Components.BotKeyboardView r3 = r6.botKeyboardView
            int r4 = r0.getChildCount()
            int r4 = r4 - r2
            r0.addView(r3, r4)
        L_0x0049:
            r6.botButtonsMessageObject = r7
            if (r7 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup
            if (r3 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = (org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup) r0
            goto L_0x0059
        L_0x0058:
            r0 = 0
        L_0x0059:
            r6.botReplyMarkup = r0
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            if (r4 <= r3) goto L_0x0068
            int r3 = r6.keyboardHeightLand
            goto L_0x006a
        L_0x0068:
            int r3 = r6.keyboardHeight
        L_0x006a:
            r0.setPanelHeight(r3)
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r6.botReplyMarkup
            if (r0 == 0) goto L_0x00df
            int r8 = r6.currentAccount
            android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getMainSettings(r8)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "hidekeyboard_"
            r0.append(r3)
            long r3 = r6.dialog_id
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            int r0 = r8.getInt(r0, r1)
            int r3 = r7.getId()
            if (r0 != r3) goto L_0x0096
            r0 = 1
            goto L_0x0097
        L_0x0096:
            r0 = 0
        L_0x0097:
            org.telegram.messenger.MessageObject r3 = r6.botButtonsMessageObject
            org.telegram.messenger.MessageObject r4 = r6.replyingMessageObject
            if (r3 == r4) goto L_0x00c1
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r3 = r6.botReplyMarkup
            boolean r3 = r3.single_use
            if (r3 == 0) goto L_0x00c1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "answered_"
            r3.append(r4)
            long r4 = r6.dialog_id
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            int r8 = r8.getInt(r3, r1)
            int r7 = r7.getId()
            if (r8 != r7) goto L_0x00c1
            goto L_0x00c2
        L_0x00c1:
            r1 = 1
        L_0x00c2:
            if (r1 == 0) goto L_0x00d7
            if (r0 != 0) goto L_0x00d7
            org.telegram.ui.Components.EditTextCaption r7 = r6.messageEditText
            int r7 = r7.length()
            if (r7 != 0) goto L_0x00d7
            boolean r7 = r6.isPopupShowing()
            if (r7 != 0) goto L_0x00d7
            r6.showPopup(r2, r2)
        L_0x00d7:
            org.telegram.ui.Components.BotKeyboardView r7 = r6.botKeyboardView
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r8 = r6.botReplyMarkup
            r7.setButtons(r8)
            goto L_0x00f4
        L_0x00df:
            boolean r7 = r6.isPopupShowing()
            if (r7 == 0) goto L_0x00f4
            int r7 = r6.currentPopupContentType
            if (r7 != r2) goto L_0x00f4
            if (r8 == 0) goto L_0x00f1
            r6.clearBotButtonsOnKeyboardOpen = r2
            r6.openKeyboardInternal()
            goto L_0x00f4
        L_0x00f1:
            r6.showPopup(r1, r2)
        L_0x00f4:
            r6.updateBotButton(r2)
        L_0x00f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setButtons$31 */
    public /* synthetic */ void lambda$setButtons$31$ChatActivityEnterView(TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        MessageObject messageObject = this.replyingMessageObject;
        MessageObject messageObject2 = messageObject != null ? messageObject : ((int) this.dialog_id) < 0 ? this.botButtonsMessageObject : null;
        if (messageObject == null) {
            messageObject = this.botButtonsMessageObject;
        }
        boolean didPressedBotButton = didPressedBotButton(tLRPC$KeyboardButton, messageObject2, messageObject);
        if (this.replyingMessageObject != null) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else {
            MessageObject messageObject3 = this.botButtonsMessageObject;
            if (messageObject3 != null && messageObject3.messageOwner.reply_markup.single_use) {
                if (didPressedBotButton) {
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

    public boolean didPressedBotButton(TLRPC$KeyboardButton tLRPC$KeyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        TLRPC$KeyboardButton tLRPC$KeyboardButton2 = tLRPC$KeyboardButton;
        MessageObject messageObject3 = messageObject2;
        if (tLRPC$KeyboardButton2 == null || messageObject3 == null) {
            return false;
        }
        if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButton) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(tLRPC$KeyboardButton2.text, this.dialog_id, messageObject, getThreadMessage(), (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
        } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonUrl) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, tLRPC$KeyboardButton2.url, false, true);
        } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonRequestPhone) {
            this.parentFragment.shareMyContact(2, messageObject3);
        } else {
            Boolean bool = null;
            if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonRequestPoll) {
                ChatActivity chatActivity = this.parentFragment;
                if ((tLRPC$KeyboardButton2.flags & 1) != 0) {
                    bool = Boolean.valueOf(tLRPC$KeyboardButton2.quiz);
                }
                chatActivity.openPollCreate(bool);
                return false;
            } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(messageObject3, tLRPC$KeyboardButton2) {
                    public final /* synthetic */ MessageObject f$1;
                    public final /* synthetic */ TLRPC$KeyboardButton f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.lambda$didPressedBotButton$32$ChatActivityEnterView(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.parentFragment.showDialog(builder.create());
            } else if ((tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonCallback) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonGame) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonBuy) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject3, tLRPC$KeyboardButton2, this.parentFragment);
            } else if (!(tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonSwitchInline) || this.parentFragment.processSwitchButton((TLRPC$TL_keyboardButtonSwitchInline) tLRPC$KeyboardButton2)) {
                return true;
            } else {
                if (tLRPC$KeyboardButton2.same_peer) {
                    TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                    int i = tLRPC$Message.from_id.user_id;
                    int i2 = tLRPC$Message.via_bot_id;
                    if (i2 != 0) {
                        i = i2;
                    }
                    TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
                    if (user == null) {
                        return true;
                    }
                    setFieldText("@" + user.username + " " + tLRPC$KeyboardButton2.query);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 1);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate(messageObject3, tLRPC$KeyboardButton2) {
                        public final /* synthetic */ MessageObject f$1;
                        public final /* synthetic */ TLRPC$KeyboardButton f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            ChatActivityEnterView.this.lambda$didPressedBotButton$33$ChatActivityEnterView(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    this.parentFragment.presentFragment(dialogsActivity);
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didPressedBotButton$32 */
    public /* synthetic */ void lambda$didPressedBotButton$32$ChatActivityEnterView(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, tLRPC$KeyboardButton);
            return;
        }
        this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        this.pendingMessageObject = messageObject;
        this.pendingLocationButton = tLRPC$KeyboardButton;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didPressedBotButton$33 */
    public /* synthetic */ void lambda$didPressedBotButton$33$ChatActivityEnterView(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        int i = tLRPC$Message.from_id.user_id;
        int i2 = tLRPC$Message.via_bot_id;
        if (i2 != 0) {
            i = i2;
        }
        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
        if (user == null) {
            dialogsActivity.finishFragment();
            return;
        }
        long longValue = ((Long) arrayList.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        instance.saveDraft(longValue, 0, "@" + user.username + " " + tLRPC$KeyboardButton.query, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$Message) null, true);
        if (longValue != this.dialog_id) {
            int i3 = (int) longValue;
            if (i3 != 0) {
                Bundle bundle = new Bundle();
                if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else {
                    bundle.putInt("chat_id", -i3);
                }
                if (this.accountInstance.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                    if (!this.parentFragment.presentFragment(new ChatActivity(bundle), true)) {
                        dialogsActivity.finishFragment();
                    } else if (!AndroidUtilities.isTablet()) {
                        this.parentFragment.removeSelfFromStack();
                    }
                }
            } else {
                dialogsActivity.finishFragment();
            }
        } else {
            dialogsActivity.finishFragment();
        }
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            AnonymousClass44 r1 = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity, true, this.info) {
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 0) {
                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(f);
                    }
                }
            };
            this.emojiView = r1;
            r1.setVisibility(8);
            this.emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
                public boolean onBackspace() {
                    if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String str) {
                    int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        int unused = ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        EditTextCaption editTextCaption = ChatActivityEnterView.this.messageEditText;
                        editTextCaption.setText(editTextCaption.getText().insert(selectionEnd, replaceEmoji));
                        int length = selectionEnd + replaceEmoji.length();
                        ChatActivityEnterView.this.messageEditText.setSelection(length, length);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = ChatActivityEnterView.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
                    if (ChatActivityEnterView.this.trendingStickersAlert != null) {
                        ChatActivityEnterView.this.trendingStickersAlert.dismiss();
                        TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = null;
                    }
                    if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                int unused2 = ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(tLRPC$Document));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.lambda$onStickerSelected$34(tLRPC$Document, str, obj, sendAnimationData, false, z, i);
                        if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(tLRPC$Document)) {
                            TLRPC$Document tLRPC$Document2 = tLRPC$Document;
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj, tLRPC$Document);
                        }
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
                public void lambda$onGifSelected$0(View view, Object obj, String str, Object obj2, boolean z, int i) {
                    Object obj3 = obj;
                    Object obj4 = obj2;
                    int i2 = i;
                    if (isInScheduleMode() && i2 == 0) {
                        AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(view, obj, str, obj2) {
                            public final /* synthetic */ View f$1;
                            public final /* synthetic */ Object f$2;
                            public final /* synthetic */ String f$3;
                            public final /* synthetic */ Object f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                ChatActivityEnterView.AnonymousClass45.this.lambda$onGifSelected$0$ChatActivityEnterView$45(this.f$1, this.f$2, this.f$3, this.f$4, z, i);
                            }
                        });
                    } else if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        if (obj3 instanceof TLRPC$Document) {
                            TLRPC$Document tLRPC$Document = (TLRPC$Document) obj3;
                            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(tLRPC$Document, str, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), obj2, (MessageObject.SendAnimationData) null, z, i);
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$Document, (int) (System.currentTimeMillis() / 1000));
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, tLRPC$Document);
                            }
                        } else if (obj3 instanceof TLRPC$BotInlineResult) {
                            TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) obj3;
                            if (tLRPC$BotInlineResult.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$BotInlineResult.document, (int) (System.currentTimeMillis() / 1000));
                                if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, tLRPC$BotInlineResult.document);
                                }
                            }
                            TLRPC$User tLRPC$User = (TLRPC$User) obj4;
                            HashMap hashMap = new HashMap();
                            hashMap.put("id", tLRPC$BotInlineResult.id);
                            hashMap.put("query_id", "" + tLRPC$BotInlineResult.query_id);
                            hashMap.put("force_gif", "1");
                            SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, tLRPC$BotInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), z, i);
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                int unused = ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                        }
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, z, i2);
                        }
                    } else if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    }
                }

                public void onTabOpened(int i) {
                    ChatActivityEnterView.this.delegate.onStickersTab(i == 3);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) ChatActivityEnterView.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", NUM));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ChatActivityEnterView.AnonymousClass45.this.lambda$onClearEmojiRecent$1$ChatActivityEnterView$45(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onClearEmojiRecent$1 */
                public /* synthetic */ void lambda$onClearEmojiRecent$1$ChatActivityEnterView$45(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
                    if (ChatActivityEnterView.this.trendingStickersAlert != null && !ChatActivityEnterView.this.trendingStickersAlert.isDismissed()) {
                        ChatActivityEnterView.this.trendingStickersAlert.getLayout().showStickerSet(tLRPC$StickerSet, tLRPC$InputStickerSet);
                    } else if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        if (tLRPC$StickerSet != null) {
                            tLRPC$InputStickerSet = new TLRPC$TL_inputStickerSetID();
                            tLRPC$InputStickerSet.access_hash = tLRPC$StickerSet.access_hash;
                            tLRPC$InputStickerSet.id = tLRPC$StickerSet.id;
                        }
                        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, tLRPC$InputStickerSet, (TLRPC$TL_messages_stickerSet) null, ChatActivityEnterView.this));
                    }
                }

                public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 2, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 0, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickersGroupClick(int i) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.hidePopup(false);
                        }
                        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(i);
                        groupStickersActivity.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(groupStickersActivity);
                    }
                }

                public void onSearchOpenClose(int i) {
                    int unused = ChatActivityEnterView.this.searchingType = i;
                    if (i != 0) {
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

                public void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
                    if (ChatActivityEnterView.this.parentActivity != null && ChatActivityEnterView.this.parentFragment != null) {
                        TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = new TrendingStickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, trendingStickersLayout) {
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
                            int access$12300 = chatActivityEnterView2.stickersExpandedHeight;
                            int dp = AndroidUtilities.dp(120.0f);
                            Point point = AndroidUtilities.displaySize;
                            int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(access$12300, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
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

                public void onDragEnd(float f) {
                    if (allowDragging()) {
                        boolean unused = ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || f < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || f > ((float) AndroidUtilities.dp(-200.0f))) && ((!this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
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

                public void onDrag(int i) {
                    if (allowDragging()) {
                        Point point = AndroidUtilities.displaySize;
                        int access$12400 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$12400));
                        ChatActivityEnterView.this.emojiView.setTranslationY(max);
                        ChatActivityEnterView.this.setTranslationY(max);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        float unused = chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$12400)));
                        ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                    }
                }

                private boolean allowDragging() {
                    return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers() && !ChatActivityEnterView.this.waitingForKeyboardOpen;
                }
            });
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
            checkChannelRights();
        }
    }

    /* renamed from: onStickerSelected */
    public void lambda$onStickerSelected$34(TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i) {
        int i2 = i;
        if (isInScheduleMode() && i2 == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tLRPC$Document, str, obj, sendAnimationData, z) {
                public final /* synthetic */ TLRPC$Document f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ Object f$3;
                public final /* synthetic */ MessageObject.SendAnimationData f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.lambda$onStickerSelected$34$ChatActivityEnterView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, z, i);
                }
            });
        } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
            if (this.searchingType != 0) {
                this.searchingType = 0;
                this.emojiView.closeSearch(true);
                this.emojiView.hideSearchKeyboard();
            }
            setStickersExpanded(false, true, false);
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(tLRPC$Document, str, this.dialog_id, this.replyingMessageObject, getThreadMessage(), obj, sendAnimationData, z2, i);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, i2);
            }
            if (z) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, obj, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
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

    public void addStickerToRecent(TLRPC$Document tLRPC$Document) {
        createEmojiView();
        this.emojiView.addRecentSticker(tLRPC$Document);
    }

    public void showEmojiView() {
        showPopup(1, 0);
    }

    /* access modifiers changed from: private */
    public void showPopup(final int i, int i2) {
        View view;
        int i3;
        int i4;
        if (i != 2) {
            if (i == 1) {
                if (i2 == 0 && this.emojiView == null) {
                    if (this.parentActivity != null) {
                        createEmojiView();
                    } else {
                        return;
                    }
                }
                if (i2 == 0) {
                    if (this.emojiView.getParent() == null) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
                    }
                    if (this.emojiViewVisible) {
                        int visibility = this.emojiView.getVisibility();
                    }
                    this.emojiView.setVisibility(0);
                    this.emojiViewVisible = true;
                    BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                    if (botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8) {
                        i3 = 0;
                    } else {
                        this.botKeyboardView.setVisibility(8);
                        this.botKeyboardViewVisible = false;
                        i3 = this.botKeyboardView.getMeasuredHeight();
                    }
                    view = this.emojiView;
                    this.animatingContentType = 0;
                } else if (i2 == 1) {
                    if (this.botKeyboardViewVisible) {
                        int visibility2 = this.botKeyboardView.getVisibility();
                    }
                    this.botKeyboardViewVisible = true;
                    EmojiView emojiView2 = this.emojiView;
                    if (emojiView2 == null || emojiView2.getVisibility() == 8) {
                        i4 = 0;
                    } else {
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiViewVisible = false;
                        i4 = this.emojiView.getMeasuredHeight();
                    }
                    this.botKeyboardView.setVisibility(0);
                    view = this.botKeyboardView;
                    this.animatingContentType = 1;
                } else {
                    view = null;
                    i3 = 0;
                }
                this.currentPopupContentType = i2;
                if (this.keyboardHeight <= 0) {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
                }
                if (this.keyboardHeightLand <= 0) {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
                }
                Point point = AndroidUtilities.displaySize;
                int i5 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
                if (i2 == 1) {
                    i5 = Math.min(this.botKeyboardView.getKeyboardHeight(), i5);
                }
                BotKeyboardView botKeyboardView3 = this.botKeyboardView;
                if (botKeyboardView3 != null) {
                    botKeyboardView3.setPanelHeight(i5);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = i5;
                view.setLayoutParams(layoutParams);
                if (!AndroidUtilities.isInMultiwindow) {
                    AndroidUtilities.hideKeyboard(this.messageEditText);
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout2 != null) {
                    this.emojiPadding = i5;
                    sizeNotifierFrameLayout2.requestLayout();
                    setEmojiButtonImage(true, true);
                    updateBotButton(true);
                    onWindowSizeChanged();
                    if (this.smoothKeyboard && !this.keyboardVisible && i5 != i3) {
                        this.panelAnimation = new AnimatorSet();
                        float f = (float) (i5 - i3);
                        view.setTranslationY(f);
                        this.panelAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{f, 0.0f})});
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = ChatActivityEnterView.this.panelAnimation = null;
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.panelAnimation.start();
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
                    } else {
                        this.emojiViewVisible = true;
                        this.animatingContentType = 0;
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        EmojiView emojiView3 = this.emojiView;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(emojiView3, View.TRANSLATION_Y, new float[]{(float) emojiView3.getMeasuredHeight()})});
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
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
                        this.panelAnimation.start();
                        requestLayout();
                    }
                    this.emojiViewVisible = false;
                }
                BotKeyboardView botKeyboardView4 = this.botKeyboardView;
                if (botKeyboardView4 != null) {
                    if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                        if (this.smoothKeyboard && !this.keyboardVisible) {
                            if (this.botKeyboardViewVisible) {
                                this.animatingContentType = 1;
                            }
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.panelAnimation = animatorSet2;
                            BotKeyboardView botKeyboardView5 = this.botKeyboardView;
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(botKeyboardView5, View.TRANSLATION_Y, new float[]{(float) botKeyboardView5.getMeasuredHeight()})});
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
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
                            this.panelAnimation.start();
                            requestLayout();
                        } else if (!this.waitingForKeyboardOpen) {
                            botKeyboardView4.setVisibility(8);
                        }
                    }
                    this.botKeyboardViewVisible = false;
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout3 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout3 != null && !SharedConfig.smoothKeyboard && i == 0) {
                    this.emojiPadding = 0;
                    sizeNotifierFrameLayout3.requestLayout();
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
        r0 = r10.recordedAudioPanel;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setEmojiButtonImage(boolean r11, boolean r12) {
        /*
            r10 = this;
            int r0 = r10.recordInterfaceState
            r1 = 0
            r2 = 1
            if (r0 == r2) goto L_0x0013
            android.widget.FrameLayout r0 = r10.recordedAudioPanel
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
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r1]
            r12.setScaleX(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r1]
            r12.setScaleY(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r1]
            r12.setAlpha(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setScaleX(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setScaleY(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setAlpha(r3)
            r12 = 0
        L_0x0042:
            if (r12 == 0) goto L_0x004a
            int r0 = r10.currentEmojiIcon
            r4 = -1
            if (r0 != r4) goto L_0x004a
            r12 = 0
        L_0x004a:
            r0 = 3
            r4 = 2
            if (r11 == 0) goto L_0x0054
            int r11 = r10.currentPopupContentType
            if (r11 != 0) goto L_0x0054
            r11 = 0
            goto L_0x0079
        L_0x0054:
            org.telegram.ui.Components.EmojiView r11 = r10.emojiView
            if (r11 != 0) goto L_0x0063
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r5 = "selected_page"
            int r11 = r11.getInt(r5, r1)
            goto L_0x0067
        L_0x0063:
            int r11 = r11.getCurrentPage()
        L_0x0067:
            if (r11 == 0) goto L_0x0078
            boolean r5 = r10.allowStickers
            if (r5 != 0) goto L_0x0072
            boolean r5 = r10.allowGifs
            if (r5 != 0) goto L_0x0072
            goto L_0x0078
        L_0x0072:
            if (r11 != r2) goto L_0x0076
            r11 = 2
            goto L_0x0079
        L_0x0076:
            r11 = 3
            goto L_0x0079
        L_0x0078:
            r11 = 1
        L_0x0079:
            int r5 = r10.currentEmojiIcon
            if (r5 != r11) goto L_0x007e
            return
        L_0x007e:
            android.animation.AnimatorSet r5 = r10.emojiButtonAnimation
            r6 = 0
            if (r5 == 0) goto L_0x0088
            r5.cancel()
            r10.emojiButtonAnimation = r6
        L_0x0088:
            if (r11 != 0) goto L_0x0095
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r12]
            r7 = 2131165533(0x7var_d, float:1.7945286E38)
            r5.setImageResource(r7)
            goto L_0x00bb
        L_0x0095:
            if (r11 != r2) goto L_0x00a2
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r12]
            r7 = 2131165540(0x7var_, float:1.79453E38)
            r5.setImageResource(r7)
            goto L_0x00bb
        L_0x00a2:
            if (r11 != r4) goto L_0x00af
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r12]
            r7 = 2131165541(0x7var_, float:1.7945302E38)
            r5.setImageResource(r7)
            goto L_0x00bb
        L_0x00af:
            if (r11 != r0) goto L_0x00bb
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r12]
            r7 = 2131165532(0x7var_c, float:1.7945284E38)
            r5.setImageResource(r7)
        L_0x00bb:
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r12]
            if (r11 != r4) goto L_0x00c5
            java.lang.Integer r6 = java.lang.Integer.valueOf(r2)
        L_0x00c5:
            r5.setTag(r6)
            r10.currentEmojiIcon = r11
            if (r12 == 0) goto L_0x0172
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setVisibility(r1)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setAlpha(r3)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r12.setScaleX(r5)
            android.widget.ImageView[] r12 = r10.emojiButton
            r12 = r12[r2]
            r12.setScaleY(r5)
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r10.emojiButtonAnimation = r12
            r6 = 6
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            android.widget.ImageView[] r7 = r10.emojiButton
            r7 = r7[r1]
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r2]
            r9[r1] = r5
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6[r1] = r7
            android.widget.ImageView[] r7 = r10.emojiButton
            r7 = r7[r1]
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r2]
            r9[r1] = r5
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6[r2] = r5
            android.widget.ImageView[] r5 = r10.emojiButton
            r5 = r5[r1]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            r8[r1] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r5, r7, r8)
            r6[r4] = r3
            android.widget.ImageView[] r3 = r10.emojiButton
            r3 = r3[r2]
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r5 = new float[r2]
            r7 = 1065353216(0x3var_, float:1.0)
            r5[r1] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r6[r0] = r3
            r0 = 4
            android.widget.ImageView[] r3 = r10.emojiButton
            r3 = r3[r2]
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r5 = new float[r2]
            r5[r1] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r6[r0] = r3
            r0 = 5
            android.widget.ImageView[] r3 = r10.emojiButton
            r3 = r3[r2]
            android.util.Property r4 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            r2[r1] = r7
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r3, r4, r2)
            r6[r0] = r1
            r12.playTogether(r6)
            android.animation.AnimatorSet r12 = r10.emojiButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$50 r0 = new org.telegram.ui.Components.ChatActivityEnterView$50
            r0.<init>()
            r12.addListener(r0)
            android.animation.AnimatorSet r12 = r10.emojiButtonAnimation
            r0 = 150(0x96, double:7.4E-322)
            r12.setDuration(r0)
            android.animation.AnimatorSet r12 = r10.emojiButtonAnimation
            r12.start()
        L_0x0172:
            r10.onEmojiIconChanged(r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEmojiButtonImage(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onEmojiIconChanged(int i) {
        if (i == 3 && this.emojiView == null) {
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            int min = Math.min(10, arrayList.size());
            for (int i2 = 0; i2 < min; i2++) {
                Emoji.preloadEmoji(arrayList.get(i2));
            }
        }
    }

    public void hidePopup(boolean z) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && z && this.botButtonsMessageObject != null) {
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
            if (!z || this.searchingType == 0) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
                    this.emojiView.closeSearch(false);
                    this.messageEditText.requestFocus();
                }
                showPopup(0, 0);
                return;
            }
            this.searchingType = 0;
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
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0009, code lost:
        r0 = r9.parentFragment;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openKeyboardInternal() {
        /*
            r9 = this;
            boolean r0 = org.telegram.messenger.AndroidUtilities.usingHardwareInput
            r1 = 0
            if (r0 != 0) goto L_0x001a
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x001a
            org.telegram.ui.ChatActivity r0 = r9.parentFragment
            if (r0 == 0) goto L_0x0013
            boolean r0 = r0.isInBubbleMode()
            if (r0 != 0) goto L_0x001a
        L_0x0013:
            boolean r0 = r9.isPaused
            if (r0 == 0) goto L_0x0018
            goto L_0x001a
        L_0x0018:
            r0 = 2
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            r9.showPopup(r0, r1)
            org.telegram.ui.Components.EditTextCaption r0 = r9.messageEditText
            r0.requestFocus()
            org.telegram.ui.Components.EditTextCaption r0 = r9.messageEditText
            org.telegram.messenger.AndroidUtilities.showKeyboard(r0)
            boolean r0 = r9.isPaused
            r1 = 1
            if (r0 == 0) goto L_0x0030
            r9.showKeyboardOnResume = r1
            goto L_0x006b
        L_0x0030:
            boolean r0 = org.telegram.messenger.AndroidUtilities.usingHardwareInput
            if (r0 != 0) goto L_0x006b
            boolean r0 = r9.keyboardVisible
            if (r0 != 0) goto L_0x006b
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x006b
            org.telegram.ui.ChatActivity r0 = r9.parentFragment
            if (r0 == 0) goto L_0x0046
            boolean r0 = r0.isInBubbleMode()
            if (r0 != 0) goto L_0x006b
        L_0x0046:
            r9.waitingForKeyboardOpen = r1
            org.telegram.ui.Components.EmojiView r0 = r9.emojiView
            if (r0 == 0) goto L_0x005f
            long r1 = android.os.SystemClock.uptimeMillis()
            long r3 = android.os.SystemClock.uptimeMillis()
            r5 = 3
            r6 = 0
            r7 = 0
            r8 = 0
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r1, r3, r5, r6, r7, r8)
            r0.onTouchEvent(r1)
        L_0x005f:
            java.lang.Runnable r0 = r9.openKeyboardRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r9.openKeyboardRunnable
            r1 = 100
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x006b:
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
        if (!AndroidUtilities.showKeyboard(this.messageEditText)) {
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

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(tLRPC$Document, (int) (System.currentTimeMillis() / 1000));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.addRecentGif(tLRPC$Document);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 && this.stickersExpanded) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    public void onSizeChanged(int i, boolean z) {
        boolean z2;
        View view;
        int i2;
        boolean z3 = true;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = i;
            this.lastSizeChangeValue2 = z;
            if (i <= 0) {
                z3 = false;
            }
            this.keyboardVisible = z3;
            checkBotMenu();
            return;
        }
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (z) {
                this.keyboardHeightLand = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (this.keyboardVisible && this.emojiViewVisible && this.emojiView == null) {
            this.emojiViewVisible = false;
        }
        if (isPopupShowing()) {
            int i3 = z ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                i3 = Math.min(this.botKeyboardView.getKeyboardHeight(), i3);
            }
            int i4 = this.currentPopupContentType;
            if (i4 == 0) {
                view = this.emojiView;
            } else {
                view = i4 == 1 ? this.botKeyboardView : null;
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.setPanelHeight(i3);
            }
            if (view != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                if (!this.closeAnimationInProgress && (!(layoutParams.width == (i2 = AndroidUtilities.displaySize.x) && layoutParams.height == i3) && !this.stickersExpanded)) {
                    layoutParams.width = i2;
                    layoutParams.height = i3;
                    view.setLayoutParams(layoutParams);
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    if (sizeNotifierFrameLayout != null) {
                        int i5 = this.emojiPadding;
                        this.emojiPadding = layoutParams.height;
                        sizeNotifierFrameLayout.requestLayout();
                        onWindowSizeChanged();
                        if (this.smoothKeyboard && !this.keyboardVisible && i5 != this.emojiPadding && pannelAnimationEnabled()) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.panelAnimation = animatorSet;
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) (this.emojiPadding - i5), 0.0f})});
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    AnimatorSet unused = ChatActivityEnterView.this.panelAnimation = null;
                                    if (ChatActivityEnterView.this.delegate != null) {
                                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                    }
                                    ChatActivityEnterView.this.requestLayout();
                                    NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                }
                            });
                            this.panelAnimation.start();
                            this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                            requestLayout();
                        }
                    }
                }
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z4 = this.keyboardVisible;
        if (i <= 0) {
            z3 = false;
        }
        this.keyboardVisible = z3;
        checkBotMenu();
        if (this.keyboardVisible && isPopupShowing() && this.stickersExpansionAnim == null) {
            showPopup(0, this.currentPopupContentType);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z4 && !isPopupShowing()) {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$ChatFull tLRPC$ChatFull;
        TLRPC$Chat chat;
        int i3;
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.invalidateViews();
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.invalidateViews();
                return;
            }
            return;
        }
        int i4 = 0;
        if (i == NotificationCenter.recordProgressChanged) {
            if (objArr[0].intValue() == this.recordingGuid) {
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
                    recordCircle2.setAmplitude(objArr[1].doubleValue());
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null && editTextCaption.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else {
            int i5 = 4;
            if (i != NotificationCenter.recordStartError && i != NotificationCenter.recordStopped) {
                int i6 = null;
                if (i == NotificationCenter.recordStarted) {
                    if (objArr[0].intValue() == this.recordingGuid) {
                        boolean booleanValue = objArr[1].booleanValue();
                        ImageView imageView2 = this.videoSendButton;
                        if (imageView2 != null) {
                            if (!booleanValue) {
                                i6 = 1;
                            }
                            imageView2.setTag(i6);
                            int i7 = 8;
                            this.videoSendButton.setVisibility(booleanValue ? 8 : 0);
                            ImageView imageView3 = this.videoSendButton;
                            if (booleanValue) {
                                i7 = 0;
                            }
                            imageView3.setVisibility(i7);
                        }
                        if (!this.recordingAudioVideo) {
                            this.recordingAudioVideo = true;
                            updateRecordIntefrace(0);
                        } else {
                            this.recordCircle.showWaves(true, true);
                        }
                        this.recordTimerView.start();
                        boolean unused = this.recordDot.enterAnimation = false;
                    }
                } else if (i == NotificationCenter.audioDidSent) {
                    if (objArr[0].intValue() == this.recordingGuid) {
                        VideoEditedInfo videoEditedInfo = objArr[1];
                        if (videoEditedInfo instanceof VideoEditedInfo) {
                            this.videoToSendMessageObject = videoEditedInfo;
                            String str = objArr[2];
                            this.audioToSendPath = str;
                            this.videoTimelineView.setVideoPath(str);
                            this.videoTimelineView.setKeyframes(objArr[3]);
                            this.videoTimelineView.setVisibility(0);
                            this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                            updateRecordIntefrace(3);
                            checkSendButton(false);
                            return;
                        }
                        TLRPC$TL_document tLRPC$TL_document = objArr[1];
                        this.audioToSend = tLRPC$TL_document;
                        this.audioToSendPath = objArr[2];
                        if (tLRPC$TL_document == null) {
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                            if (chatActivityEnterViewDelegate != null) {
                                chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, 0);
                            }
                        } else if (this.recordedAudioPanel != null) {
                            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                            tLRPC$TL_message.out = true;
                            tLRPC$TL_message.id = 0;
                            tLRPC$TL_message.peer_id = new TLRPC$TL_peerUser();
                            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                            tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
                            TLRPC$Peer tLRPC$Peer = tLRPC$TL_message.peer_id;
                            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                            tLRPC$TL_peerUser.user_id = clientUserId;
                            tLRPC$Peer.user_id = clientUserId;
                            tLRPC$TL_message.date = (int) (System.currentTimeMillis() / 1000);
                            tLRPC$TL_message.message = "";
                            tLRPC$TL_message.attachPath = this.audioToSendPath;
                            TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                            tLRPC$TL_message.media = tLRPC$TL_messageMediaDocument;
                            tLRPC$TL_messageMediaDocument.flags |= 3;
                            tLRPC$TL_messageMediaDocument.document = this.audioToSend;
                            tLRPC$TL_message.flags |= 768;
                            this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, true);
                            this.recordedAudioPanel.setAlpha(1.0f);
                            this.recordedAudioPanel.setVisibility(0);
                            this.recordDeleteImageView.setVisibility(0);
                            this.recordDeleteImageView.setAlpha(0.0f);
                            this.recordDeleteImageView.setScaleY(0.0f);
                            this.recordDeleteImageView.setScaleX(0.0f);
                            int i8 = 0;
                            while (true) {
                                if (i8 >= this.audioToSend.attributes.size()) {
                                    i3 = 0;
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.audioToSend.attributes.get(i8);
                                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                                    i3 = tLRPC$DocumentAttribute.duration;
                                    break;
                                }
                                i8++;
                            }
                            int i9 = 0;
                            while (true) {
                                if (i9 >= this.audioToSend.attributes.size()) {
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.audioToSend.attributes.get(i9);
                                if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                                    byte[] bArr = tLRPC$DocumentAttribute2.waveform;
                                    if (bArr == null || bArr.length == 0) {
                                        tLRPC$DocumentAttribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                                    }
                                    this.recordedAudioSeekBar.setWaveform(tLRPC$DocumentAttribute2.waveform);
                                } else {
                                    i9++;
                                }
                            }
                            this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(i3));
                            checkSendButton(false);
                            updateRecordIntefrace(3);
                        }
                    }
                } else if (i == NotificationCenter.audioRouteChanged) {
                    if (this.parentActivity != null) {
                        boolean booleanValue2 = objArr[0].booleanValue();
                        Activity activity = this.parentActivity;
                        if (!booleanValue2) {
                            i4 = Integer.MIN_VALUE;
                        }
                        activity.setVolumeControlStream(i4);
                    }
                } else if (i == NotificationCenter.messagePlayingDidReset) {
                    if (this.audioToSendMessageObject != null && !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                        this.playPauseDrawable.setIcon(0, true);
                        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                        this.recordedAudioSeekBar.setProgress(0.0f);
                    }
                } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                    Integer num = objArr[0];
                    if (this.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                        MessageObject messageObject = this.audioToSendMessageObject;
                        messageObject.audioProgress = playingMessageObject.audioProgress;
                        messageObject.audioProgressSec = playingMessageObject.audioProgressSec;
                        if (!this.recordedAudioSeekBar.isDragging()) {
                            this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                        }
                    }
                } else if (i == NotificationCenter.featuredStickersDidLoad) {
                    if (this.emojiButton != null) {
                        while (true) {
                            ImageView[] imageViewArr = this.emojiButton;
                            if (i4 < imageViewArr.length) {
                                imageViewArr[i4].invalidate();
                                i4++;
                            } else {
                                return;
                            }
                        }
                    }
                } else if (i == NotificationCenter.messageReceivedByServer) {
                    if (!objArr[6].booleanValue() && objArr[3].longValue() == this.dialog_id && (tLRPC$ChatFull = this.info) != null && tLRPC$ChatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
                        TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
                        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
                        tLRPC$ChatFull2.slowmode_next_send_date = currentTime + tLRPC$ChatFull3.slowmode_seconds;
                        tLRPC$ChatFull3.flags |= 262144;
                        setSlowModeTimer(tLRPC$ChatFull3.slowmode_next_send_date);
                    }
                } else if (i == NotificationCenter.sendingMessagesChanged) {
                    if (this.info != null) {
                        updateSlowModeText();
                    }
                } else if (i == NotificationCenter.audioRecordTooShort) {
                    updateRecordIntefrace(4);
                }
            } else if (objArr[0].intValue() == this.recordingGuid) {
                if (this.recordingAudioVideo) {
                    this.recordingAudioVideo = false;
                    if (i == NotificationCenter.recordStopped) {
                        Integer num2 = objArr[1];
                        if (num2.intValue() != 4) {
                            if (isInVideoMode() && num2.intValue() == 5) {
                                i5 = 1;
                            } else if (num2.intValue() == 0) {
                                i5 = 5;
                            } else {
                                i5 = num2.intValue() == 6 ? 2 : 3;
                            }
                        }
                        if (i5 != 3) {
                            updateRecordIntefrace(i5);
                        }
                    } else {
                        updateRecordIntefrace(2);
                    }
                }
                if (i == NotificationCenter.recordStopped) {
                    Integer num3 = objArr[1];
                }
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 2 && this.pendingLocationButton != null) {
            if (iArr.length > 0 && iArr[0] == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    /* access modifiers changed from: private */
    public void checkStickresExpandHeight() {
        if (this.emojiView != null) {
            Point point = AndroidUtilities.displaySize;
            int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
            int currentActionBarHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
            if (this.searchingType == 2) {
                currentActionBarHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + i);
            }
            int i2 = this.emojiView.getLayoutParams().height;
            if (i2 != currentActionBarHeight) {
                Animator animator = this.stickersExpansionAnim;
                if (animator != null) {
                    animator.cancel();
                    this.stickersExpansionAnim = null;
                }
                this.stickersExpandedHeight = currentActionBarHeight;
                if (i2 > currentActionBarHeight) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)})});
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$checkStickresExpandHeight$35$ChatActivityEnterView(valueAnimator);
                        }
                    });
                    animatorSet.setDuration(300);
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            }
                        }
                    });
                    this.stickersExpansionAnim = animatorSet;
                    this.emojiView.setLayerType(2, (Paint) null);
                    animatorSet.start();
                    return;
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                int selectionStart = this.messageEditText.getSelectionStart();
                int selectionEnd = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(selectionStart, selectionEnd);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)})});
                ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatActivityEnterView.this.lambda$checkStickresExpandHeight$36$ChatActivityEnterView(valueAnimator);
                    }
                });
                animatorSet2.setDuration(300);
                animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                        ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                    }
                });
                this.stickersExpansionAnim = animatorSet2;
                this.emojiView.setLayerType(2, (Paint) null);
                animatorSet2.start();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkStickresExpandHeight$35 */
    public /* synthetic */ void lambda$checkStickresExpandHeight$35$ChatActivityEnterView(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkStickresExpandHeight$36 */
    public /* synthetic */ void lambda$checkStickresExpandHeight$36$ChatActivityEnterView(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public void setStickersExpanded(boolean z, boolean z2, boolean z3) {
        boolean z4 = z;
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper2 != null && adjustPanLayoutHelper2.animationInProgress()) || this.waitingForKeyboardOpenAfterAnimation || this.emojiView == null) {
            return;
        }
        if (z3 || this.stickersExpanded != z4) {
            this.stickersExpanded = z4;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onStickersExpandedChange();
            }
            Point point = AndroidUtilities.displaySize;
            final int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
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
                    this.stickersExpandedHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + i);
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                int selectionStart = this.messageEditText.getSelectionStart();
                int selectionEnd = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(selectionStart, selectionEnd);
                if (z2) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{1.0f})});
                    animatorSet.setDuration(300);
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$37$ChatActivityEnterView(this.f$1, valueAnimator);
                        }
                    });
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionAnim = animatorSet;
                    this.emojiView.setLayerType(2, (Paint) null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    this.stickersExpansionProgress = 0.0f;
                    this.sizeNotifierLayout.invalidate();
                    animatorSet.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                if (z2) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{0.0f})});
                    animatorSet2.setDuration(300);
                    animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$38$ChatActivityEnterView(this.f$1, valueAnimator);
                        }
                    });
                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            boolean unused = ChatActivityEnterView.this.closeAnimationInProgress = false;
                            Animator unused2 = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = i;
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
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionProgress = 1.0f;
                    this.sizeNotifierLayout.invalidate();
                    this.stickersExpansionAnim = animatorSet2;
                    this.emojiView.setLayerType(2, (Paint) null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
                    animatorSet2.start();
                } else {
                    this.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    this.emojiView.setTranslationY(0.0f);
                    this.emojiView.getLayoutParams().height = i;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground((Drawable) null);
                    this.sizeNotifierLayout.setWillNotDraw(false);
                    this.stickersArrow.setAnimationProgress(0.0f);
                }
            }
            if (z4) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", NUM));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setStickersExpanded$37 */
    public /* synthetic */ void lambda$setStickersExpanded$37$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / ((float) (-(this.stickersExpandedHeight - i))));
        this.sizeNotifierLayout.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setStickersExpanded$38 */
    public /* synthetic */ void lambda$setStickersExpanded$38$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }

    public boolean swipeToBackEnabled() {
        FrameLayout frameLayout;
        if (this.recordingAudioVideo) {
            return false;
        }
        if (this.videoSendButton == null || !isInVideoMode() || (frameLayout = this.recordedAudioPanel) == null || frameLayout.getVisibility() != 0) {
            return true;
        }
        return false;
    }

    public int getHeightWithTopView() {
        int measuredHeight = getMeasuredHeight();
        View view = this.topView;
        return (view == null || view.getVisibility() != 0) ? measuredHeight : (int) (((float) measuredHeight) - ((1.0f - this.topViewEnterProgress) * ((float) this.topView.getLayoutParams().height)));
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper2) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper2;
    }

    public AdjustPanLayoutHelper getAdjustPanLayoutHelper() {
        return this.adjustPanLayoutHelper;
    }

    public boolean pannelAniamationInProgress() {
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

    private class ScrimDrawable extends Drawable {
        private Paint paint;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

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
    }

    private class SlideTextView extends View {
        Paint arrowPaint = new Paint(1);
        Path arrowPath = new Path();
        TextPaint bluePaint;
        float cancelAlpha;
        int cancelCharOffset;
        StaticLayout cancelLayout;
        private Rect cancelRect = new Rect();
        String cancelString;
        float cancelToProgress;
        float cancelWidth;
        TextPaint grayPaint;
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                setPressed(false);
            }
            if (this.cancelToProgress == 0.0f || !isEnabled()) {
                return false;
            }
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 0) {
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
            if (motionEvent.getAction() != 2 || this.cancelRect.contains(x, y)) {
                if (motionEvent.getAction() == 1 && this.cancelRect.contains(x, y)) {
                    if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                        ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    } else {
                        CameraController.getInstance().cancelOnInitRunnable(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                        ChatActivityEnterView.this.delegate.needStartRecordVideo(5, true, 0);
                    }
                    boolean unused = ChatActivityEnterView.this.recordingAudioVideo = false;
                    ChatActivityEnterView.this.updateRecordIntefrace(2);
                }
                return true;
            }
            setPressed(false);
            return false;
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
            this.arrowPaint.setColor(Theme.getColor("chat_messagePanelIcons"));
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
            this.grayPaint.setColor(Theme.getColor("chat_recordTime"));
            this.bluePaint.setColor(Theme.getColor("chat_recordVoiceCancel"));
            this.slideToAlpha = (float) this.grayPaint.getAlpha();
            this.cancelAlpha = (float) this.bluePaint.getAlpha();
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(60.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("chat_recordVoiceCancel"), 26));
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
        @SuppressLint({"DrawAllocation"})
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.slideToCancelWidth = this.grayPaint.measureText(this.slideToCancelString);
            this.cancelWidth = this.bluePaint.measureText(this.cancelString);
            this.lastUpdateTime = System.currentTimeMillis();
            int measuredHeight = getMeasuredHeight() >> 1;
            this.arrowPath.reset();
            if (this.smallSize) {
                float f = (float) measuredHeight;
                this.arrowPath.setLastPoint(AndroidUtilities.dpf2(2.5f), f - AndroidUtilities.dpf2(3.12f));
                this.arrowPath.lineTo(0.0f, f);
                this.arrowPath.lineTo(AndroidUtilities.dpf2(2.5f), f + AndroidUtilities.dpf2(3.12f));
            } else {
                float f2 = (float) measuredHeight;
                this.arrowPath.setLastPoint(AndroidUtilities.dpf2(4.0f), f2 - AndroidUtilities.dpf2(5.0f));
                this.arrowPath.lineTo(0.0f, f2);
                this.arrowPath.lineTo(AndroidUtilities.dpf2(4.0f), f2 + AndroidUtilities.dpf2(5.0f));
            }
            this.slideToLayout = new StaticLayout(this.slideToCancelString, this.grayPaint, (int) this.slideToCancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.cancelLayout = new StaticLayout(this.cancelString, this.bluePaint, (int) this.cancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            StaticLayout staticLayout;
            float f;
            Canvas canvas2 = canvas;
            if (this.slideToLayout != null && (staticLayout = this.cancelLayout) != null) {
                int width = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
                this.grayPaint.setColor(Theme.getColor("chat_recordTime"));
                this.grayPaint.setAlpha((int) (this.slideToAlpha * (1.0f - this.cancelToProgress) * this.slideProgress));
                this.bluePaint.setAlpha((int) (this.cancelAlpha * this.cancelToProgress));
                this.arrowPaint.setColor(this.grayPaint.getColor());
                boolean z = true;
                if (this.smallSize) {
                    this.xOffset = (float) AndroidUtilities.dp(16.0f);
                } else {
                    long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
                    this.lastUpdateTime = System.currentTimeMillis();
                    if (this.cancelToProgress == 0.0f && this.slideProgress > 0.8f) {
                        if (this.moveForward) {
                            float dp = this.xOffset + ((((float) AndroidUtilities.dp(3.0f)) / 250.0f) * ((float) currentTimeMillis));
                            this.xOffset = dp;
                            if (dp > ((float) AndroidUtilities.dp(6.0f))) {
                                this.xOffset = (float) AndroidUtilities.dp(6.0f);
                                this.moveForward = false;
                            }
                        } else {
                            float dp2 = this.xOffset - ((((float) AndroidUtilities.dp(3.0f)) / 250.0f) * ((float) currentTimeMillis));
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
                int measuredWidth = ((int) ((((float) getMeasuredWidth()) - this.slideToCancelWidth) / 2.0f)) + AndroidUtilities.dp(5.0f);
                int measuredWidth2 = (int) ((((float) getMeasuredWidth()) - this.cancelWidth) / 2.0f);
                float primaryHorizontal = z ? this.slideToLayout.getPrimaryHorizontal(this.cancelCharOffset) : 0.0f;
                float f2 = z ? (((float) measuredWidth) + primaryHorizontal) - ((float) measuredWidth2) : 0.0f;
                float f3 = this.xOffset;
                float f4 = this.cancelToProgress;
                float dp3 = ((((float) measuredWidth) + ((f3 * (1.0f - f4)) * this.slideProgress)) - (f2 * f4)) + ((float) AndroidUtilities.dp(16.0f));
                if (z) {
                    f = 0.0f;
                } else {
                    f = this.cancelToProgress * ((float) AndroidUtilities.dp(12.0f));
                }
                if (this.cancelToProgress != 1.0f) {
                    int i = (int) (((float) ((-getMeasuredWidth()) / 4)) * (1.0f - this.slideProgress));
                    canvas.save();
                    canvas2.clipRect(ChatActivityEnterView.this.recordTimerView.getLeftProperty() + ((float) AndroidUtilities.dp(4.0f)), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.save();
                    int i2 = (int) dp3;
                    canvas2.translate((float) ((i2 - AndroidUtilities.dp(this.smallSize ? 7.0f : 10.0f)) + i), f);
                    canvas2.drawPath(this.arrowPath, this.arrowPaint);
                    canvas.restore();
                    canvas.save();
                    canvas2.translate((float) (i2 + i), (((float) (getMeasuredHeight() - this.slideToLayout.getHeight())) / 2.0f) + f);
                    this.slideToLayout.draw(canvas2);
                    canvas.restore();
                    canvas.restore();
                }
                if (this.cancelToProgress > 0.0f) {
                    this.selectableBackground.setBounds((getMeasuredWidth() / 2) - width, (getMeasuredHeight() / 2) - width, (getMeasuredWidth() / 2) + width, (getMeasuredHeight() / 2) + width);
                    this.selectableBackground.draw(canvas2);
                    float measuredHeight = ((float) (getMeasuredHeight() - this.cancelLayout.getHeight())) / 2.0f;
                    if (!z) {
                        measuredHeight -= ((float) AndroidUtilities.dp(12.0f)) - f;
                    }
                    float f5 = z ? dp3 + primaryHorizontal : (float) measuredWidth2;
                    canvas.save();
                    canvas2.translate(f5, measuredHeight);
                    this.cancelRect.set((int) f5, (int) measuredHeight, (int) (f5 + ((float) this.cancelLayout.getWidth())), (int) (measuredHeight + ((float) this.cancelLayout.getHeight())));
                    this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
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

        @Keep
        public void setCancelToProgress(float f) {
            this.cancelToProgress = f;
        }

        @Keep
        public float getSlideToCancelWidth() {
            return this.slideToCancelWidth;
        }

        public void setSlideX(float f) {
            this.slideProgress = f;
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
        @SuppressLint({"DrawAllocation"})
        public void onDraw(Canvas canvas) {
            String str;
            String str2;
            Canvas canvas2 = canvas;
            long currentTimeMillis = System.currentTimeMillis();
            long j = this.isRunning ? currentTimeMillis - this.startTime : this.stopTime - this.startTime;
            long j2 = j / 1000;
            int i = ((int) (j % 1000)) / 10;
            if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null && j >= 59500 && !this.stoppedInternal) {
                float unused = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                this.stoppedInternal = true;
            }
            if (this.isRunning && currentTimeMillis > this.lastSendTypingTime + 5000) {
                this.lastSendTypingTime = currentTimeMillis;
                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).sendTyping(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.getThreadMessageId(), (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? 1 : 7, 0);
            }
            long j3 = j2 / 60;
            if (j3 >= 60) {
                str = String.format(Locale.US, "%01d:%02d:%02d,%d", new Object[]{Long.valueOf(j3 / 60), Long.valueOf(j3 % 60), Long.valueOf(j2 % 60), Integer.valueOf(i / 10)});
            } else {
                str = String.format(Locale.US, "%01d:%02d,%d", new Object[]{Long.valueOf(j3), Long.valueOf(j2 % 60), Integer.valueOf(i / 10)});
            }
            if (str.length() < 3 || (str2 = this.oldString) == null || str2.length() < 3 || str.length() != this.oldString.length() || str.charAt(str.length() - 3) == this.oldString.charAt(str.length() - 3)) {
                if (this.replaceStable == null) {
                    this.replaceStable = new SpannableStringBuilder(str);
                }
                if (this.replaceStable.length() == 0 || this.replaceStable.length() != str.length()) {
                    this.replaceStable.clear();
                    this.replaceStable.append(str);
                } else {
                    SpannableStringBuilder spannableStringBuilder = this.replaceStable;
                    spannableStringBuilder.replace(spannableStringBuilder.length() - 1, this.replaceStable.length(), str, (str.length() - 1) - (str.length() - this.replaceStable.length()), str.length());
                }
            } else {
                int length = str.length();
                this.replaceIn.clear();
                this.replaceOut.clear();
                this.replaceStable.clear();
                this.replaceIn.append(str);
                this.replaceOut.append(this.oldString);
                this.replaceStable.append(str);
                int i2 = -1;
                int i3 = -1;
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < length - 1; i6++) {
                    if (this.oldString.charAt(i6) != str.charAt(i6)) {
                        if (i5 == 0) {
                            i3 = i6;
                        }
                        i5++;
                        if (i4 != 0) {
                            EmptyStubSpan emptyStubSpan = new EmptyStubSpan();
                            if (i6 == length - 2) {
                                i4++;
                            }
                            int i7 = i4 + i2;
                            this.replaceIn.setSpan(emptyStubSpan, i2, i7, 33);
                            this.replaceOut.setSpan(emptyStubSpan, i2, i7, 33);
                            i4 = 0;
                        }
                    } else {
                        if (i4 == 0) {
                            i2 = i6;
                        }
                        i4++;
                        if (i5 != 0) {
                            this.replaceStable.setSpan(new EmptyStubSpan(), i3, i5 + i3, 33);
                            i5 = 0;
                        }
                    }
                }
                if (i4 != 0) {
                    EmptyStubSpan emptyStubSpan2 = new EmptyStubSpan();
                    int i8 = i4 + i2 + 1;
                    this.replaceIn.setSpan(emptyStubSpan2, i2, i8, 33);
                    this.replaceOut.setSpan(emptyStubSpan2, i2, i8, 33);
                }
                if (i5 != 0) {
                    this.replaceStable.setSpan(new EmptyStubSpan(), i3, i5 + i3, 33);
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
            float measuredHeight = (float) (getMeasuredHeight() / 2);
            if (this.replaceTransition == 0.0f) {
                this.replaceStable.clearSpans();
                StaticLayout staticLayout = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.save();
                canvas2.translate(0.0f, measuredHeight - (((float) staticLayout.getHeight()) / 2.0f));
                staticLayout.draw(canvas2);
                canvas.restore();
                this.left = staticLayout.getLineWidth(0) + 0.0f;
            } else {
                if (this.inLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) ((1.0f - this.replaceTransition) * 255.0f));
                    canvas2.translate(0.0f, (measuredHeight - (((float) this.inLayout.getHeight()) / 2.0f)) - (this.replaceDistance * this.replaceTransition));
                    this.inLayout.draw(canvas2);
                    canvas.restore();
                }
                if (this.outLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) (this.replaceTransition * 255.0f));
                    canvas2.translate(0.0f, (measuredHeight - (((float) this.outLayout.getHeight()) / 2.0f)) + (this.replaceDistance * (1.0f - this.replaceTransition)));
                    this.outLayout.draw(canvas2);
                    canvas.restore();
                }
                canvas.save();
                this.textPaint.setAlpha(255);
                StaticLayout staticLayout2 = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas2.translate(0.0f, measuredHeight - (((float) staticLayout2.getHeight()) / 2.0f));
                staticLayout2.draw(canvas2);
                canvas.restore();
                this.left = staticLayout2.getLineWidth(0) + 0.0f;
            }
            this.oldString = str;
            if (this.isRunning || this.replaceTransition != 0.0f) {
                invalidate();
            }
        }

        public void updateColors() {
            this.textPaint.setColor(Theme.getColor("chat_recordTime"));
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

    public RecordCircle getRecordCicle() {
        return this.recordCircle;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00dc, code lost:
        r2 = (androidx.recyclerview.widget.LinearLayoutManager) r5.botCommandsMenuContainer.listView.getLayoutManager();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r6, int r7) {
        /*
            r5 = this;
            org.telegram.ui.Components.BotCommandsMenuView r0 = r5.botCommandsMenuButton
            r1 = 0
            if (r0 == 0) goto L_0x0048
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0048
            org.telegram.ui.Components.BotCommandsMenuView r0 = r5.botCommandsMenuButton
            r0.measure(r6, r7)
            r0 = 0
        L_0x0011:
            android.widget.ImageView[] r2 = r5.emojiButton
            int r3 = r2.length
            if (r0 >= r3) goto L_0x0030
            r2 = r2[r0]
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r2 = (android.view.ViewGroup.MarginLayoutParams) r2
            r3 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Components.BotCommandsMenuView r4 = r5.botCommandsMenuButton
            int r4 = r4.getMeasuredWidth()
            int r3 = r3 + r4
            r2.leftMargin = r3
            int r0 = r0 + 1
            goto L_0x0011
        L_0x0030:
            org.telegram.ui.Components.EditTextCaption r0 = r5.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r2 = 1113849856(0x42640000, float:57.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.BotCommandsMenuView r3 = r5.botCommandsMenuButton
            int r3 = r3.getMeasuredWidth()
            int r2 = r2 + r3
            r0.leftMargin = r2
            goto L_0x0071
        L_0x0048:
            r0 = 0
        L_0x0049:
            android.widget.ImageView[] r2 = r5.emojiButton
            int r3 = r2.length
            if (r0 >= r3) goto L_0x0061
            r2 = r2[r0]
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r2 = (android.view.ViewGroup.MarginLayoutParams) r2
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.leftMargin = r3
            int r0 = r0 + 1
            goto L_0x0049
        L_0x0061:
            org.telegram.ui.Components.EditTextCaption r0 = r5.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r2 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.leftMargin = r2
        L_0x0071:
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r5.botCommandsMenuContainer
            if (r0 == 0) goto L_0x0110
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r0 = r5.botCommandsAdapter
            int r0 = r0.getItemCount()
            r2 = 4
            if (r0 <= r2) goto L_0x0091
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            r2 = 1129630925(0x4354cccd, float:212.8)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r0 = java.lang.Math.max(r1, r0)
            goto L_0x00b4
        L_0x0091:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r3 = r5.botCommandsAdapter
            int r3 = r3.getItemCount()
            int r2 = java.lang.Math.min(r2, r3)
            r3 = 1
            int r2 = java.lang.Math.max(r3, r2)
            int r2 = r2 * 36
            int r2 = r2 + 58
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r0 = java.lang.Math.max(r1, r0)
        L_0x00b4:
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getPaddingTop()
            if (r2 == r0) goto L_0x0110
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setTopGlowOffset(r0)
            int r2 = r5.botCommandLastPosition
            r3 = -1
            if (r2 != r3) goto L_0x0103
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x0103
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r2 = r2.getLayoutManager()
            if (r2 == 0) goto L_0x0103
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r2 = r2.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r2 = (androidx.recyclerview.widget.LinearLayoutManager) r2
            int r3 = r2.findFirstVisibleItemPosition()
            if (r3 < 0) goto L_0x0103
            android.view.View r2 = r2.findViewByPosition(r3)
            if (r2 == 0) goto L_0x0103
            r5.botCommandLastPosition = r3
            int r2 = r2.getTop()
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            int r3 = r3.getPaddingTop()
            int r2 = r2 - r3
            r5.botCommandLastTop = r2
        L_0x0103:
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setPadding(r1, r0, r1, r3)
        L_0x0110:
            super.onMeasure(r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.botCommandLastPosition != -1) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.botCommandsMenuContainer.listView.getLayoutManager();
            if (linearLayoutManager != null) {
                linearLayoutManager.scrollToPositionWithOffset(this.botCommandLastPosition, this.botCommandLastTop);
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

    public void setBotInfo(SparseArray<TLRPC$BotInfo> sparseArray) {
        BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter2 = this.botCommandsAdapter;
        if (botCommandsAdapter2 != null) {
            botCommandsAdapter2.setBotInfo(sparseArray);
        }
    }

    public boolean botCommandsMenuIsShowing() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        return botCommandsMenuView != null && botCommandsMenuView.isOpened();
    }

    public void hideBotCommands() {
        this.botCommandsMenuButton.setOpened(false);
        this.botCommandsMenuContainer.dismiss();
    }
}
